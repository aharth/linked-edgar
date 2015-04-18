package com.ontologycentral.edgarwrap;

import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * foaf:Agent
 * 
 * @author aharth
 *
 */
public class Agent {
	Integer _cik;
	String _name;
	Set<String> _xbrl;
	Set<String> _form4;
	
	Set<String> _same;
	Integer _sic = null;
	
	public Agent(int cik, String name, Set<String> xbrl, Set<String> form4) {
		_cik = cik;
		_name = name;
		_xbrl = xbrl;
		_form4 = form4;
		
		_same = new HashSet<String>();
	}
	
	public void addSic(Integer sic) {
		_sic = sic;
	}
	
	public void addSameAs(String uri) {
		_same.add(uri);
	}
	
	public void toXml(XMLStreamWriter ch) throws XMLStreamException {
		ch.writeStartElement("foaf:Agent");
		ch.writeAttribute("rdf:about", "./" + _cik + "#id");

		ch.writeStartElement("foaf:page");
		ch.writeAttribute("rdf:resource", "");
		ch.writeEndElement();

		ch.writeStartElement("dbp:secCik");
		ch.writeCharacters(_cik+"");
		ch.writeEndElement();

		ch.writeStartElement("ed:cikNumber");
		ch.writeCharacters(_cik+"");
		ch.writeEndElement();

		ch.writeStartElement("foaf:name");
		ch.writeCharacters(_name);
		ch.writeEndElement();

		ch.writeStartElement("foaf:page");
		ch.writeAttribute("rdf:resource", "https://www.sec.gov/cgi-bin/browse-edgar?action=getcompany&CIK=" + _cik);
		ch.writeEndElement();

		if (_sic != null) {
			ch.writeStartElement("ed:assignedSic");
			ch.writeAttribute("rdf:resource", "/sic/" + _sic + "#id");
			ch.writeEndElement();
		}
		
		if (!_same.isEmpty()) {
			for (String uri : _same) {
				ch.writeStartElement("owl:sameAs");
				ch.writeAttribute("rdf:resource", uri);
				ch.writeEndElement();			
			}
		}

		if (_xbrl != null && !_xbrl.isEmpty()) {
			for (String accessionno : _xbrl) {
				ch.writeStartElement("ed:issued");
				ch.writeStartElement("qb:Dataset");	
				ch.writeAttribute("rdf:about", "../archive/" + _cik + "/" + accessionno + "#ds");

//				ch.writeStartElement("rdfs:seeAlso");
//				String archive = "http://www.sec.gov/Archives/edgar/data/" + _cik + "/" + accessionno.replace("-", "") + "/" + accessionno + "-xbrl.zip";
//				ch.writeAttribute("rdf:resource", archive);
//				ch.writeEndElement();
				
				ch.writeEndElement();	
				ch.writeEndElement();
			}			
		}

		if (_form4 != null && !_form4.isEmpty()) {
			for (String uri : _form4) {
				ch.writeStartElement("ed:issued");
				ch.writeStartElement("qb:Dataset");	
				ch.writeAttribute("rdf:about", "../archive/" + uri + "#ds");
				// uri
				// uri = uri.
//				ch.writeStartElement("rdfs:seeAlso");
//				String archive = "http://www.sec.gov/Archives/edgar/data/" + uri + ".txt";
//				ch.writeAttribute("rdf:resource", archive);
//				ch.writeEndElement();
				
				ch.writeEndElement();	
				
				ch.writeEndElement();
			}			
		}

		NumberFormat nf = NumberFormat.getIntegerInstance();
		nf.setMinimumIntegerDigits(10);
		nf.setGroupingUsed(false);
		
		ch.writeStartElement("owl:sameAs");
		ch.writeAttribute("rdf:resource", "http://www.rdfabout.com/rdf/usgov/sec/id/cik" + nf.format(_cik));
		ch.writeEndElement();

//		ch.writeStartElement("owl:sameAs");
//		ch.writeAttribute("rdf:resource", "http://rdf.freebase.com/ns/business.cik." + _cik);
//		ch.writeEndElement();
				
		// foaf:Agent
		ch.writeEndElement();
	}
}
