package com.ontologycentral.edgarwrap;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class FormXbrlArchive {
	Logger _log = Logger.getLogger(this.getClass().getName());

	Transformer _t;
	
	public FormXbrlArchive(Transformer t) {
		_t = t;
	}
	
	public byte[] tranform(String id, String type, String date, String cik) throws IOException {
		int delim = id.lastIndexOf('/');
		String pre = id.substring(0, delim);
		String post = id.substring(delim+1);

		String archive = "http://www.sec.gov/Archives/edgar/data/" + pre + "/" + post.replace("-", "") + "/" + post + "-xbrl.zip";
		//String archive = "http://localhost:8888/0001193125-10-285703-xbrl.zip";
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

		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));

		ZipEntry ze;
		while((ze = zis.getNextEntry()) != null) {
			// We only take the xml (the xbrl instance, I guess)
			if (!ze.getName().contains("_") && ze.getName().contains("xml") ) {
				break;
			}
		}
		
//		System.out.println(cik);
//		cik = "4711";
		
//		_t.setParameter("cik", cik);
		_t.setParameter("link", u.toString());

		if (date != null) {
			_t.setParameter("date", date);
		}
		if (type != null) {
			_t.setParameter("type", type);
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			clean(zis, baos);
		} catch (XMLStreamException e) {
			e.printStackTrace(); 
			throw new RuntimeException(e.getMessage());
		}
		
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		
		baos = new ByteArrayOutputStream();
		
		try {
			StreamSource ssource = new StreamSource(bais);
			StreamResult sresult = new StreamResult(baos);

			_log.info("lapplying xslt");
			//System.out.println("herwe");

			_t.transform(ssource, sresult);
		} catch (TransformerException e) {
			e.printStackTrace(); 
			throw new RuntimeException(e.getMessage());
		}

		zis.close();
		
		return baos.toByteArray();
	}
	
	void clean(InputStream in, OutputStream out) throws XMLStreamException {
		XMLInputFactory ifa = XMLInputFactory.newInstance();
		XMLOutputFactory ofa = XMLOutputFactory.newInstance();
		XMLEventFactory efa = XMLEventFactory.newInstance();

		XMLEventReader er = null;
		XMLEventWriter ew = null;

		er = ifa.createXMLEventReader(in);
		ew = ofa.createXMLEventWriter(out);

		StringBuffer sb = null;

		while (er.hasNext()) {
			XMLEvent event = er.nextEvent();

			if (event instanceof StartElement) {
				StartElement se = (StartElement)event;
				String local = se.getName().getLocalPart();
				if (local.endsWith("TextBlock") || local.endsWith("Reclassifications")) {
					sb = new StringBuffer();
				}
			}

			if (event instanceof Characters && sb != null) {
				Characters ce = (Characters)event;
				sb.append(ce.getData());
				continue;
			}

			if (event instanceof EndElement && sb != null) {
				Document doc = Jsoup.parseBodyFragment(sb.toString());
				Element body = doc.body();

				Characters ce = efa.createCharacters(body.text());

				ew.add(ce);

				sb = null;
			}

			ew.add(event);
		}

		ew.close();
		er.close();
	}

}