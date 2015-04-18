package com.ontologycentral.egdarwrap;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import junit.framework.TestCase;

public class ConvertCikColeft extends TestCase {
	public void testConvert() throws IOException {
		FileInputStream in = new FileInputStream("files/cik.coleft.c");
		FileOutputStream out = new FileOutputStream("files/ciks.txt");
		
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
				pw.println(cik + "\t " + name);
			}
		}
		in.close();
		pw.close();
	
	}
}
