package com.ontologycentral.edgarwrap.webapp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.ontologycentral.edgarwrap.Agent;

@SuppressWarnings("serial")
public class CorpWatchServlet extends HttpServlet {
	Logger _log = Logger.getLogger(this.getClass().getName());

	// http://api.corpwatch.org/companies.xml?cik=37996
	// 4e86f776b9cff6d3ce8833408fd29f69
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

		OutputStream os = resp.getOutputStream();

		String id = req.getRequestURI();
		id = id.substring("/cik/".length());

		ServletContext ctx = getServletContext();

		Map<Integer, String> ciks = (Map<Integer, String>)ctx.getAttribute(Listener.CIK);
		Map<Integer, String> dbpedia = (Map<Integer, String>)ctx.getAttribute(Listener.CIKDBP);
		Map<Integer, String> freebase = (Map<Integer, String>)ctx.getAttribute(Listener.CIKFB);
		Map<Integer, Set<String>> filings = (Map<Integer, Set<String>>)ctx.getAttribute(Listener.CIKXBRL);
		Map<Integer, Set<String>> form4 = (Map<Integer, Set<String>>)ctx.getAttribute(Listener.CIKFORM4);
		Map<Integer, Integer> ciksic = (Map<Integer, Integer>)ctx.getAttribute(Listener.CIKSIC);

		Integer cik = -1;

		if (id.length() != 0) {
			cik = Integer.parseInt(id);
			if ( !ciks.containsKey(cik)) { 
				resp.sendError(404, "Not found " + cik);
				return;
			}
		}
		
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();

			XMLOutputFactory factory = (XMLOutputFactory)ctx.getAttribute(Listener.FACTORY);

			XMLStreamWriter ch = factory.createXMLStreamWriter(baos, "utf-8");

			ch.writeStartDocument("utf-8", "1.0");

			ch.writeStartElement("rdf:RDF");
			ch.writeNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
			ch.writeNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
			ch.writeNamespace("foaf", "http://xmlns.com/foaf/0.1/");
			ch.writeNamespace("owl", "http://www.w3.org/2002/07/owl#");
			ch.writeNamespace("dbp", "http://dbpedia.org/property/");
			ch.writeNamespace("ed", "http://edgarwrap.ontologycentral.com/vocab/edgar#");
			ch.writeNamespace("dcterms", "http://purl.org/dc/terms/");
			ch.writeNamespace("qb", "http://purl.org/linked-data/cube#");

			ch.writeStartElement("rdf:Description");
			ch.writeAttribute("rdf:about", "");

			ch.writeStartElement("rdfs:comment");
			ch.writeCharacters("No guarantee of correctness! USE AT YOUR OWN RISK!");
			ch.writeEndElement();
			
			ch.writeStartElement("dcterms:publisher");
			ch.writeCharacters("SEC (http://edgar.sec.gov/) via Linked Edgar (http://edgarwrap.ontologycentral.com/");
			ch.writeEndElement();

			ch.writeStartElement("foaf:maker");
			ch.writeAttribute("rdf:resource", "http://cbasewrap.ontologycentral.com/company/ontologycentral#id");
			ch.writeEndElement();

//			ch.writeStartElement("rdfs:seeAlso");
//			ch.writeAttribute("rdf:resource", "./links.rdf");
//			ch.writeEndElement();

			ch.writeStartElement("foaf:topic");
			ch.writeAttribute("rdf:resource", "#id");
			ch.writeEndElement();
			
			ch.writeEndElement();
			
			if (id.length() == 0) {
				for (Integer i : filings.keySet()) {
					Agent a = new Agent(i, ciks.get(i), filings.get(i), form4.get(i), Collections.EMPTY_MAP);
					if (dbpedia.containsKey(cik)) {
						a.addSameAs(dbpedia.get(cik));
					}
					a.toXml(ch);
				}
			} else {
				Agent a = new Agent(cik, ciks.get(cik), filings.get(cik), form4.get(cik), Collections.EMPTY_MAP);
				
				if (ciksic.containsKey(cik)) {
					a.addSic(ciksic.get(cik));
				}
				if (dbpedia.containsKey(cik)) {
					a.addSameAs(dbpedia.get(cik));
				}
				if (freebase.containsKey(cik)) {
					a.addSameAs("http://rdf.freebase.com/ns/business.cik." + freebase.get(cik));					
				}
				a.toXml(ch);
			}
			
			// rdf:RDF
			ch.writeEndElement();

			ch.writeEndDocument();
		} catch (XMLStreamException e) {
			e.printStackTrace();
			resp.sendError(500, e.getMessage());
			return;
		} catch (RuntimeException e) {
			e.printStackTrace();
			resp.sendError(500, e.getMessage());
			return;			
		}
		
		resp.setHeader("Cache-Control", "public");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 1);
		resp.setHeader("Expires", Listener.RFC822.format(c.getTime()));

		os.write(baos.toByteArray());

		os.close();
	}
}