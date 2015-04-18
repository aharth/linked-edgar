package com.ontologycentral.edgarwrap.webapp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;

import com.ontologycentral.edgarwrap.Form4;
import com.ontologycentral.edgarwrap.FormXbrlArchive;

@SuppressWarnings("serial")
public class ArchiveServlet extends HttpServlet {
	Logger _log = Logger.getLogger(this.getClass().getName());

	/**
	 * URI scheme: {cik}/{accession-number}
	 * 
	 */
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
		
		resp.setHeader("Cache-Control", "public");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 1);
		resp.setHeader("Expires", Listener.RFC822.format(c.getTime()));

		OutputStream os = resp.getOutputStream();

		String id = req.getRequestURI();
		int index = id.indexOf("/archive/");
		id = id.substring(index + "/archive/".length());
				
		ServletContext ctx = getServletContext();

		Map<Integer, Set<String>> filings = (Map<Integer, Set<String>>)ctx.getAttribute(Listener.CIKXBRL);
		Map<Integer, Set<String>> form4s = (Map<Integer, Set<String>>)ctx.getAttribute(Listener.CIKFORM4);

		boolean f4 = false;
		// Manually checking whether form 4
		for (Set<String> vals : form4s.values()) {
			if (vals.contains(id)) {
				f4 = true;
				break;
			}
		}
		
		if (f4) {
			// Give it a transformer F4_T
			Transformer t = (Transformer)ctx.getAttribute(Listener.F4_T);

			Form4 f = new Form4(t);
			
			String cik = id.substring(0, id.indexOf("/"));
			
			try {
				os.write(f.tranform(cik, id));
			} catch (IOException e) {
				resp.sendError(500, e.getMessage());
				e.printStackTrace();
				return;
			} catch (RuntimeException e) {
				resp.sendError(500, e.getMessage());
				e.printStackTrace();
				return;			
			}
			os.close();
		} else {		
			Map<String, String[]> xbrltypedate = (Map<String, String[]>)ctx.getAttribute(Listener.XBRLTYPEDATE);

			Transformer t = (Transformer)ctx.getAttribute(Listener.XBRL_T);

			FormXbrlArchive a = new FormXbrlArchive(t);

			String type = null;
			String date = null;
			String cik = null;

			//System.out.println("ID>>>" + id);
			if (xbrltypedate.containsKey(id)) {
				type = xbrltypedate.get(id)[0];
				date = xbrltypedate.get(id)[1];
				cik = xbrltypedate.get(id)[2];
			}
			//			System.out.println(type + " " + date + " " + cik);
			//			System.out.println(xbrltypedate.entrySet().contains(id));

			try {
				os.write(a.tranform(id, type, date, cik));
			} catch (IOException e) {
				resp.sendError(500, e.getMessage());
				e.printStackTrace();
				return;
			} catch (RuntimeException e) {
				resp.sendError(500, e.getMessage());
				e.printStackTrace();
				return;			
			}
			os.close();
		}
	}
}