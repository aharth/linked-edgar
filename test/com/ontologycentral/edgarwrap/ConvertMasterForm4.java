package com.ontologycentral.edgarwrap;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import junit.framework.TestCase;

public class ConvertMasterForm4 extends TestCase {
	public void testConvert() throws IOException {
		FileInputStream in = new FileInputStream("files/master");
		FileOutputStream out = new FileOutputStream("files/master-form4.new");
		
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
			
			if (tok.hasMoreTokens()) {
				cik = tok.nextToken();
			}
			
			tok.nextToken();			
			String form = tok.nextToken().trim();
						
			if ("4".equals(form)) {
				tok.nextToken();

				String id = tok.nextToken();
				id = id.substring(0, id.length()-4);

				pw.println(cik + "\t " + id.substring(11));	
			}
		}
		in.close();
		pw.close();
	
	}
}
