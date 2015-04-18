package com.ontologycentral.egdarwrap;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import junit.framework.TestCase;

import org.semanticweb.yars.nx.Literal;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.Nodes;
import org.semanticweb.yars.nx.Resource;
import org.semanticweb.yars.nx.namespace.FOAF;
import org.semanticweb.yars.nx.namespace.RDF;
import org.semanticweb.yars.nx.namespace.RDFS;

public class ConvertCikColeftToNtriples extends TestCase {
	public void testConvert() throws IOException {
		FileInputStream in = new FileInputStream("files/cik.coleft.c");
		FileOutputStream out = new FileOutputStream("files/dbpedia-map/edgar-cik-labels.nt");
		
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		PrintWriter pw = new PrintWriter(out);
		
		String line = null;
		
		while ((line = br.readLine()) != null)   {
			//System.out.println(line);

			StringTokenizer tok = new StringTokenizer(line, ":");
			
			Integer cik = null;
			String name = null;
			String next = null;
			
			if (tok.hasMoreTokens()) {
				name = tok.nextToken();
			}
			if (tok.hasMoreTokens()) {
				next = tok.nextToken();
			}
			if (tok.hasMoreTokens()) {
				name = name + ":" + next;
				next = tok.nextToken();
			}
			
			try {
				cik = Integer.parseInt(next);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.out.println(line);
			}

			if (cik != null && name != null) {
				Resource subj = new Resource("http://edgarwrap.ontologycentral.com/cik/" + cik);
				pw.println(Nodes.toN3(new Node[] { subj, RDF.TYPE, FOAF.AGENT } ));
				pw.println(Nodes.toN3(new Node[] { subj, RDFS.LABEL, new Literal(name) } ));
			}
		}
		in.close();
		pw.close();
	
	}
}
