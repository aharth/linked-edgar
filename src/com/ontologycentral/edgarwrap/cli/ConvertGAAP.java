package com.ontologycentral.edgarwrap.cli;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import org.semanticweb.yars.nx.Literal;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.Nodes;
import org.semanticweb.yars.nx.Resource;
import org.semanticweb.yars.nx.namespace.RDF;
import org.semanticweb.yars.nx.namespace.RDFS;
import org.semanticweb.yars.nx.util.NxUtil;

import au.com.bytecode.opencsv.CSVReader;

public class ConvertGAAP {
	public static void main(String[] args) throws Exception {
		String[] versions = { "2008-03-31", "2009-01-31", "2011-01-31" };
		
		for (String v: versions) {
			FileOutputStream fos = new FileOutputStream("xbrl-challenge/TaxonomyCsvExports/us-gaap-" + v + "/us-gaap-" + v + ".ttl");
			PrintWriter pw = new PrintWriter(fos);
			
			FileInputStream fis = new FileInputStream("xbrl-challenge/TaxonomyCsvExports/us-gaap-" + v + "/us-gaap-all-" + v + "-label.csv");
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			CSVReader reader = new CSVReader(br);

			List<String[]> data = reader.readAll();

			for (String[] sarr : data) {
				String localname = sarr[1];
				String lang = sarr[2];
				String desc = sarr[3];

				Node[] nx = new Node[3];

				nx[0] = new Resource("http://edgarwrap.ontologycentral.com/vocab/us-gaap-" + v + "#" + localname);
				nx[1] = RDFS.LABEL;
				
				if (desc != null && desc.trim().length() > 0) {
					nx[2] = new Literal(NxUtil.escapeForNx(desc), lang);
					pw.println(Nodes.toN3(nx));
				}
				
				nx[1] = RDF.TYPE;
				nx[2] = new Resource("http://www.w3.org/2004/02/skos/core#Concept");
				
				pw.println(Nodes.toN3(nx));
			}

			br.close();
			fis.close();
			pw.close();
			fos.close();
		}
	}
}