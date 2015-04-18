package com.ontologycentral.egdarwrap;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import junit.framework.TestCase;

public class ConvertXbrl extends TestCase {
	public void testConvert() throws IOException {
		FileInputStream in = new FileInputStream("files/xbrl");
		FileOutputStream out = new FileOutputStream("files/xbrl.new");
		
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
			
			String type = tok.nextToken();
			
			String date = tok.nextToken();

			String id = tok.nextToken();
			
			id = id.substring(11);
			int end = id.length()-4;
			id = id.substring(0, end);
			
			pw.println(cik + "\t " + id + "\t" + type + "\t" + date);
		}
		in.close();
		pw.close();
	
	}
}
