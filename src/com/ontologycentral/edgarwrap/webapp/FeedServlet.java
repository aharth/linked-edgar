package com.ontologycentral.edgarwrap.webapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

@SuppressWarnings("serial")
public class FeedServlet extends HttpServlet {
	Logger _log = Logger.getLogger(this.getClass().getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if (req.getServerName().contains("edgarwrap.appspot.com")) {
			try {
				URI re = new URI("http://edgarwrap.ontologycentral.com/" + req.getRequestURI());
				re = re.normalize();
				resp.sendRedirect(re.toString());
			} catch (URISyntaxException e) {
				resp.sendError(500, e.getMessage());
			}
			return;
		}
		resp.setContentType("application/rdf+xml");

		ServletContext ctx = getServletContext();
		
		OutputStream os = resp.getOutputStream();

		try {
			URL u = new URL("http://www.sec.gov/Archives/edgar/usgaap.rss.xml");
				
			HttpURLConnection conn = (HttpURLConnection)u.openConnection();

			_log.info("looking up " + u);
			
			if (conn.getResponseCode() != 200) {
				resp.sendError(conn.getResponseCode(), streamToString(conn.getErrorStream()));
				return;
			}

			String encoding = conn.getContentEncoding();
			if (encoding == null) {
				encoding = "utf-8";
			}
			
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), encoding));

			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = in.readLine()) != null) {
				sb.append(line);
				sb.append('\n');
			}

			in.close();

			//System.out.println(sb.toString());

			StringReader sr = new StringReader(sb.toString());

			Transformer t = (Transformer)ctx.getAttribute(Listener.FEED_T);

			try {
				StreamSource ssource = new StreamSource(sr);
				StreamResult sresult = new StreamResult(os);
				
				_log.info("lapplying xslt");
				//System.out.println("herwe");

				t.transform(ssource, sresult);
			} catch (TransformerException e) {
				e.printStackTrace(); 
				resp.sendError(500, e.getMessage());
			}
		} catch (IOException ioex) {
			ioex.printStackTrace();
			resp.sendError(500, ioex.getMessage());
			return;
		}

		os.close();
	}


	public static String streamToString(InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder();

		if (is != null) {
			String line;

			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb.append(line);
					sb.append("\n");
				}
			} finally {
				is.close();
			}
		}

		return sb.toString();
	}
}
