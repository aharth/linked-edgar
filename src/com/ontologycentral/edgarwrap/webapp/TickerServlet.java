package com.ontologycentral.edgarwrap.webapp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@SuppressWarnings("serial")
public class TickerServlet extends HttpServlet {
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
		resp.setHeader("Cache-Control", "public");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 2);
		resp.setHeader("Expires", Listener.RFC822.format(c.getTime()));

		OutputStream os = resp.getOutputStream();

		String id = req.getRequestURI();
		id = id.substring("/ticker/".length());

		ServletContext ctx = getServletContext();

//		Cache cache = (Cache)ctx.getAttribute(Listener.CACHE);

		String cik = null;
		
//		if (cache.containsKey(id)) {
//			cik = (String)cache.get(id);
//		} else {
			// <link rel="alternate" type="application/atom+xml" title="ATOM" href="/cgi-bin/browse-edgar?action=getcompany&amp;CIK=0000109380&amp;type=&amp;dateb=&amp;owner=exclude&amp;count=40&amp;output=atom" />
			Document doc = Jsoup.connect("http://www.sec.gov/cgi-bin/browse-edgar?CIK=" + id + "&action=getcompany").get();

			Element link = doc.select("link[type=application/atom+xml]").first();

			if (link == null) {
				resp.sendError(404, id + " not found");
				return;
			}

			String href = link.attr("href");
			int start = href.indexOf("CIK=") + 4;
			int end = href.indexOf("&", start);
			cik = href.substring(start, end);
//
//			cache.put(id, cik);
//		}
			
		try {
			XMLOutputFactory factory = (XMLOutputFactory)ctx.getAttribute(Listener.FACTORY);

			XMLStreamWriter ch = factory.createXMLStreamWriter(os, "utf-8");

			ch.writeStartDocument("utf-8", "1.0");

			ch.writeStartElement("rdf:RDF");
			ch.writeNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
			ch.writeNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
			ch.writeNamespace("foaf", "http://xmlns.com/foaf/0.1/");
			ch.writeNamespace("owl", "http://www.w3.org/2002/07/owl#");
			ch.writeNamespace("dbp", "http://dbpedia.org/property/");
			ch.writeNamespace("dcterms", "http://purl.org/dc/terms/");
			ch.writeNamespace("ed", "http://edgarwrap.ontologycentral.com/vocab/edgar#");
			ch.writeNamespace("qb", "http://purl.org/linked-data/cube#");
			ch.writeNamespace("skos", "http://www.w3.org/2004/02/skos/core#");

			ch.writeStartElement("rdf:Description");
			ch.writeAttribute("rdf:about", "");

			ch.writeStartElement("rdfs:comment");
			ch.writeCharacters("No guarantee of correctness! USE AT YOUR OWN RISK!");
			ch.writeEndElement();
			
			ch.writeStartElement("dcterms:publisher");
			ch.writeCharacters("SEC (http://edgar.sec.gov/) via Linked Edgar (http://edgarwrap.ontologycentral.com/)");
			ch.writeEndElement();

			ch.writeStartElement("foaf:maker");
			ch.writeAttribute("rdf:resource", "http://cbasewrap.ontologycentral.com/company/ontologycentral#id");
			ch.writeEndElement();

			ch.writeStartElement("rdfs:seeAlso");
			ch.writeAttribute("rdf:resource", "./links.rdf");
			ch.writeEndElement();
			
			ch.writeStartElement("foaf:topic");
			ch.writeAttribute("rdf:resource", "#id");
			ch.writeEndElement();
			
			ch.writeEndElement();

			ch.writeStartElement("rdf:Description");
			
			ch.writeAttribute("rdf:about", "#id");
			
			ch.writeStartElement("owl:sameAs");
			ch.writeAttribute("rdf:resource", "../cik/" + Integer.parseInt(cik) + "#id");
			ch.writeEndElement();
			
			ch.writeEndElement();

			// rdf:RDF
			ch.writeEndElement();

			ch.writeEndDocument();
		} catch (XMLStreamException e) {
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
