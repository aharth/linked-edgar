package com.ontologycentral.edgarwrap;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import junit.framework.TestCase;

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.Nodes;
import org.semanticweb.yars.nx.Resource;
import org.semanticweb.yars.nx.namespace.RDFS;
import org.semanticweb.yars2.rdfxml.RDFXMLParser;

public class MapDbpediaTest extends TestCase {
	public void testConvert() throws IOException {
		FileInputStream in = new FileInputStream("files/dbpedia-map/cik-companies.txt");
		FileOutputStream out = new FileOutputStream("files/dbpedia-map/mappings.nt");
		
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		PrintWriter pw = new PrintWriter(out);
		
		String line = null;
		
		while ((line = br.readLine()) != null)   {
			//System.out.println(line);

			StringTokenizer tok = new StringTokenizer(line, "\t");
			
			Integer cik = null;
			String name = null;
			
			if (tok.hasMoreTokens()) {
				cik = Integer.parseInt(tok.nextToken());
			}
			if (tok.hasMoreTokens()) {
				name = tok.nextToken();
			}
			
			try {
				List<Resource> result = Map.map(cik, name);
				if (result != null) {
					for (Resource r : result) {
						System.out.println("\n" + cik + "\t" + name + "\t" + result);
						Node s = new Resource("http://edgarwrap.ontologycentral.com/cik/" + cik);

						pw.println(Nodes.toN3(new Node[] { s, RDFS.SEEALSO, r } ) + " .");
					}
				}
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
		
		in.close();
		pw.close();
	}
}

class Map {	
	public Map() {
		;
	}
	
	public static List<Resource> map(Integer cik, String name) throws Exception {
		URL u = new URL("http://swse.deri.org/rss.rdf?keyword=" + URLEncoder.encode(name, "utf-8"));
		
		System.out.print(".");
		
		InputStream is = u.openStream();
		RDFXMLParser rdfxml = new RDFXMLParser(is, "http://example.org/");
		
		List<Resource> result = new ArrayList<Resource>();
		
		while (rdfxml.hasNext()) {
			Node[] nx = rdfxml.next();
			
			if (nx[0].toString().startsWith("http://dbpedia.org/")) {
				System.out.println(nx[0]);
				if (result.isEmpty() || !result.get(result.size()-1).equals(nx[0])) {
					result.add((Resource)nx[0]);					
				}
			}
		}
		
		is.close();
		
		return result;
	}
}
