package com.ontologycentral.edgarwrap;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class Form4 {
	Logger _log = Logger.getLogger(this.getClass().getName());

	Transformer _t;
	
	public Form4(Transformer t) {
		_t = t;
	}

	public byte[] tranform(String cik, String id) throws IOException {
		String archive = "http://www.sec.gov/Archives/edgar/data/" + id + ".txt";
		
		URL u = new URL(archive);

		_log.info("retrieving " + u);
		//System.out.println("retrieving " + _u);

		HttpURLConnection conn = (HttpURLConnection)u.openConnection();
		InputStream is = conn.getInputStream();

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("lookup on " + u + " resulted HTTP in status code " + conn.getResponseCode());
		}

		String encoding = conn.getContentEncoding();
		if (encoding == null) {
			encoding = "ISO-8859-1";
		}

		StringWriter writer = new StringWriter();
		try {
			char[] buffer = new char[1024];
			Reader reader = new BufferedReader(new InputStreamReader(is, encoding));
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
		} finally {
			is.close();			
		}
		
		String content = writer.getBuffer().toString();

		int start = content.indexOf("<XML>") + 5;
		int end = content.indexOf("</XML>");
		
		content = content.substring(start, end).trim();

		//System.out.println(content);
		
		_t.setParameter("link", u.toString());
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			StreamSource ssource = new StreamSource(new StringReader(content));
			StreamResult sresult = new StreamResult(baos);

			_log.info("lapplying xslt");
			//System.out.println("herwe");

			_t.transform(ssource, sresult);
		} catch (TransformerException e) {
			e.printStackTrace(); 
			throw new RuntimeException(e.getMessage());
		}

		return baos.toByteArray();
	}
}
