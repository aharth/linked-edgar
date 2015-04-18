package com.ontologycentral.edgarwrap;

import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * skos:Concept
 * 
 * @author aharth
 *
 */
public class Concept {
	Integer _sic;
	String _name;
	
	Set<String> _same;
	
	public Concept(int cik, String name) {
		_sic = cik;
		_name = name;
		
		_same = new HashSet<String>();
	}
	
	public void addSameAs(String uri) {
		_same.add(uri);
	}
	
	public void toXml(XMLStreamWriter ch) throws XMLStreamException {
		ch.writeStartElement("skos:Concept");
		ch.writeAttribute("rdf:about", "./" + _sic + "#id");

		/*
		ch.writeStartElement("dbp:secSic@@@");
		ch.writeCharacters(_sic+"");
		ch.writeEndElement();
		*/
		
		ch.writeStartElement("ed:sic");
		ch.writeCharacters(_sic+"");
		ch.writeEndElement();

		ch.writeStartElement("foaf:name");
		ch.writeCharacters(_name);
		ch.writeEndElement();

		NumberFormat nf = NumberFormat.getIntegerInstance();
		nf.setMinimumIntegerDigits(10);
		nf.setGroupingUsed(false);
		
//		ch.writeStartElement("owl:sameAs");
//		ch.writeAttribute("rdf:resource", "http://www.rdfabout.com/rdf/usgov/sec/id/cik" + nf.format(_cik));
//		ch.writeEndElement();	

		if (!_same.isEmpty()) {
			for (String uri : _same) {
				ch.writeStartElement("owl:sameAs");
				ch.writeAttribute("rdf:resource", uri);
				ch.writeEndElement();			
			}
		}
				
		ch.writeEndElement();
	}
}
