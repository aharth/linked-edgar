package com.ontologycentral.edgarwrap;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import junit.framework.TestCase;

public class ZipTest extends TestCase {
	public static int BUFFER = 1024*16;
	
	public void testZip() throws Exception {
		FileInputStream fis = new FileInputStream("files/zip/0001193125-10-285703-xbrl.zip");
		
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));

		ZipEntry ze;
		while((ze = zis.getNextEntry()) != null) {
			if (!ze.getName().contains("_") && ze.getName().contains("xml") ) {
				System.out.println(ze.getName());

				byte[] data = new byte[BUFFER];
				int count;
				while ((count = zis.read(data, 0, BUFFER)) != -1) {
					System.out.write(data, 0, count);				
				}
			}
		}
	}
}
