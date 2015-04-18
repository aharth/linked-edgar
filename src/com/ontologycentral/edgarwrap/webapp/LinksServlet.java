package com.ontologycentral.edgarwrap.webapp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

@SuppressWarnings("serial")
public class LinksServlet extends HttpServlet {
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

		OutputStream os = resp.getOutputStream();

		String id = req.getRequestURI();
		id = id.substring("/sic/".length());

		ServletContext ctx = getServletContext();

		Map<Integer, String> freebase = (Map<Integer, String>)ctx.getAttribute(Listener.CIKFB);
		Map<Integer, String> dbpedia = (Map<Integer, String>)ctx.getAttribute(Listener.CIKDBP);
		Map<Integer, String> ciks = (Map<Integer, String>)ctx.getAttribute(Listener.CIK);

		try {
			resp.setHeader("Cache-Control", "public");
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DATE, 1);
			resp.setHeader("Expires", Listener.RFC822.format(c.getTime()));

			XMLOutputFactory factory = (XMLOutputFactory)ctx.getAttribute(Listener.FACTORY);

			XMLStreamWriter ch = factory.createXMLStreamWriter(os, "utf-8");

			ch.writeStartDocument("utf-8", "1.0");

			ch.writeStartElement("rdf:RDF");
			ch.writeNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
			ch.writeNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
			ch.writeNamespace("foaf", "http://xmlns.com/foaf/0.1/");
			ch.writeNamespace("owl", "http://www.w3.org/2002/07/owl#");
			ch.writeNamespace("dbp", "http://dbpedia.org/property/");
			ch.writeNamespace("ed", "http://edgarwrap.ontologycentral.com/vocab/edgar#");
			ch.writeNamespace("qb", "http://purl.org/linked-data/cube#");
			ch.writeNamespace("skos", "http://www.w3.org/2004/02/skos/core#");

			ch.writeStartElement("rdf:Description");
			ch.writeAttribute("rdf:about", "");
			
			ch.writeStartElement("foaf:maker");
			ch.writeAttribute("rdf:resource", "http://harth.org/andreas/foaf#ah");
			ch.writeEndElement();			

			ch.writeEndElement();			

			Iterator<Integer> it = ciks.keySet().iterator();
			
			Random r = new Random();

			while (it.hasNext()) {				
				Integer cik = it.next();
				String dbp = dbpedia.get(cik);
				String fb = freebase.get(cik);

				// randomly select mappings to be served
				if (r.nextFloat() < .10 && (dbp != null || fb != null)) {
					ch.writeStartElement("rdf:Description");
					ch.writeAttribute("rdf:about", "./" + cik + "#id");

					if (dbp != null) {
						String uri = dbp;
						ch.writeStartElement("owl:sameAs");
						ch.writeAttribute("rdf:resource", uri);
						ch.writeEndElement();			
					}
					if (fb != null) {
						String uri = "http://rdf.freebase.com/ns/business.cik." + fb;		

						ch.writeStartElement("owl:sameAs");
						ch.writeAttribute("rdf:resource", uri);
						ch.writeEndElement();			
					}

					ch.writeEndElement();
				}
			}
			
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
