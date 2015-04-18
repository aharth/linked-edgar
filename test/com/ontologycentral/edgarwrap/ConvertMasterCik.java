package com.ontologycentral.edgarwrap;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import junit.framework.TestCase;

public class ConvertMasterCik extends TestCase {
	public void testConvert() throws IOException {
		FileInputStream in = new FileInputStream("test/master");
		FileOutputStream out = new FileOutputStream("test/master.1");
		
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		PrintWriter pw = new PrintWriter(out);
		
		String line = null;
		
		for (int i = 0; i < 9; i++) {
			line = br.readLine();
		}
		
		char c = line.charAt(3);
		
		br.readLine();
		
		System.out.println(line);
		
		while ((line = br.readLine()) != null)   {
			//System.out.println(line);

			StringTokenizer tok = new StringTokenizer(line, ""+c);
			
			String cik = null;
			String name = null;
			
			if (tok.hasMoreTokens()) {
				cik = tok.nextToken();
			}
			if (tok.hasMoreTokens()) {
				name = tok.nextToken();
			}
			
			pw.println(cik + "\t " + name);
		}
		in.close();
		pw.close();
	
	}
}
