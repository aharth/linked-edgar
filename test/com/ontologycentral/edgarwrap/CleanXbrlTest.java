package com.ontologycentral.edgarwrap;

import java.io.FileReader;
import java.io.FileWriter;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import junit.framework.TestCase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class CleanXbrlTest extends TestCase {
	public void testHandler() throws Exception {
		XMLInputFactory ifa = XMLInputFactory.newInstance();
		XMLOutputFactory ofa = XMLOutputFactory.newInstance();
		XMLEventFactory efa = XMLEventFactory.newInstance();
			
		XMLEventReader er = null;
		XMLEventWriter ew = null;
		
		er = ifa.createXMLEventReader(new FileReader("test/npk-20110703.xml"));
		ew = ofa.createXMLEventWriter(new FileWriter("test/npk-20110703-out.xml"));

		StringBuffer sb = null;

		while (er.hasNext()) {
			XMLEvent event = er.nextEvent();
			
			if (event instanceof StartElement) {
				StartElement se = (StartElement)event;
				if (se.getName().getLocalPart().endsWith("TextBlock")) {
					sb = new StringBuffer();
					System.out.println(se.getName());
				}
			}
			
			if (event instanceof Characters && sb != null) {
				Characters ce = (Characters)event;
				sb.append(ce.getData());
				continue;
			}
			
			if (event instanceof EndElement && sb != null) {
				System.out.println("need to fix " + sb.toString());
				
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