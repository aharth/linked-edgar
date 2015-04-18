package com.ontologycentral.edgarwrap.webapp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
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

import com.ontologycentral.edgarwrap.Concept;

@SuppressWarnings("serial")
public class SicServlet extends HttpServlet {
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
		int index = id.indexOf("/sic/");
		id = id.substring(index + "/sic/".length());

		ServletContext ctx = getServletContext();

		Map<Integer, String> sics = (Map<Integer, String>)ctx.getAttribute(Listener.SIC);
		Map<Integer, String> dbpedia = (Map<Integer, String>)ctx.getAttribute(Listener.SICDBP);

		Map<Integer, Set<Integer>> sicciks = (Map<Integer, Set<Integer>>)ctx.getAttribute(Listener.SICCIK);

		Integer sic = -1;

		if (id.length() != 0) {
			sic = Integer.parseInt(id);
			if ( !sics.containsKey(sic)) { 
				resp.sendError(404, "Not found " + sic);
			}
		}
			
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
			ch.writeNamespace("ed", "http://edgarwrap.ontologycentral.com/vocab/edgar#");
			ch.writeNamespace("qb", "http://purl.org/linked-data/cube#");
			ch.writeNamespace("skos", "http://www.w3.org/2004/02/skos/core#");


			if (id.length() == 0) {
				for (Integer i : sics.keySet()) {
					Concept c = new Concept(i, sics.get(i));
					if (dbpedia.containsKey(sic)) {
						c.addSameAs(dbpedia.get(sic));
					}
					c.toXml(ch);
				}
				
				for (Integer s : sicciks.keySet()) {
					Set<Integer> ciks = sicciks.get(s);

					for (Integer cik : ciks) {
						ch.writeStartElement("rdf:Description");
						ch.writeAttribute("rdf:about", "/cik/"+cik+"#id");

						ch.writeStartElement("ed:assignedSic");
						ch.writeAttribute("rdf:resource", "/sic/" + s + "#id");
						ch.writeEndElement();
						
						ch.writeEndElement();
					}
				}
			} else {
				Concept a = new Concept(sic, sics.get(sic));
				if (dbpedia.containsKey(sic)) {
					a.addSameAs(dbpedia.get(sic));
				}
				a.toXml(ch);
				
				Set<Integer> ciks = sicciks.get(sic);

				for (Integer cik : ciks) {
					ch.writeStartElement("rdf:Description");
					ch.writeAttribute("rdf:about", "/cik/"+cik+"#id");

					ch.writeStartElement("ed:assignedSic");
					ch.writeAttribute("rdf:resource", "/sic/" + sic + "#id");
					ch.writeEndElement();
						
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
