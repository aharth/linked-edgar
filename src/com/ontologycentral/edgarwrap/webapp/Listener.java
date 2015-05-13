package com.ontologycentral.edgarwrap.webapp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

public class Listener implements ServletContextListener {
	Logger _log = Logger.getLogger(this.getClass().getName());

	public static SimpleDateFormat RFC822 = new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", Locale.US);

	public static String FEED_T = "feed_t";
	public static String XBRL_T = "xbrl_t";
	public static String F4_T = "form4_t";

	public static String CIK = "cik";
	public static String CIKDBP = "dbpedia-cik";
	public static String CIKFB = "freebase-cik";
	
	public static String CIKXBRL = "xbrl filings";
	public static String XBRLTYPEDATE = "xbrl filings type and date";

	public static String CIKFORM4 = "form4 filings";

	public static String SIC = "sic";
	public static String SICDBP = "dbpedia-sic";
	
	public static String CIKSIC = "ciksic";
	public static String SICCIK = "siccik";

//	public static String CACHE = "c";
	public static String FACTORY = "f";

	public static String DATASTORE = "dss";
	
	public void contextInitialized(ServletContextEvent event) {
		ServletContext ctx = event.getServletContext();
       
//		javax.xml.transform.TransformerFactory tf =
//		      javax.xml.transform.TransformerFactory.newInstance(
//			"net.sf.saxon.TransformerFactoryImpl",
//		    		  Thread.currentThread().getContextClassLoader()); 
//
//		javax.xml.transform.TransformerFactory tf =
//			javax.xml.transform.TransformerFactory.newInstance("org.apache.xalan.processor.TransformerFactoryImpl", this.getClass().getClassLoader() ); 

		TransformerFactory tf = TransformerFactory.newInstance();

		try {
			Transformer t = tf.newTransformer(new StreamSource(ctx.getRealPath("/WEB-INF/feed2rdf.xsl")));
			ctx.setAttribute(FEED_T, t);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			//throw new RuntimeException(e);
		}

		try {
			Transformer t = tf.newTransformer(new StreamSource(ctx.getRealPath("/WEB-INF/xbrl2rdf.xsl")));
			ctx.setAttribute(XBRL_T, t);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		try {
			Transformer t = tf.newTransformer(new StreamSource(ctx.getRealPath("/WEB-INF/form4.xsl")));
			ctx.setAttribute(F4_T, t);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		InputStream in = null;
		BufferedReader br = null;

		try {
			Map<Integer, String> ciks = new HashMap<Integer, String>();

			in = new GZIPInputStream(new FileInputStream(ctx.getRealPath("/WEB-INF/ciks.txt.gz")));
			br = new BufferedReader(new InputStreamReader(in));

			String line = null;
			while ((line = br.readLine()) != null)   {
				StringTokenizer tok = new StringTokenizer(line, "\t");

				if (tok.hasMoreTokens()) {
					Integer cik = Integer.parseInt(tok.nextToken());
					String name = tok.nextToken().trim();

					ciks.put(cik, name);
				}
			}
			ctx.setAttribute(CIK, ciks);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				br.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			Map<Integer, Set<String>> filings = new HashMap<Integer, Set<String>>();
			
			Map<String, String[]> ftd = new HashMap<String, String[]>();

			// We ask manually, what type, what date, and what cik
			in = new FileInputStream(ctx.getRealPath("/WEB-INF/xbrl.1"));
			br = new BufferedReader(new InputStreamReader(in));

			String line = null;
			while ((line = br.readLine()) != null)   {
				StringTokenizer tok = new StringTokenizer(line, "\t");

				if (tok.hasMoreTokens()) {
					Integer cik = Integer.parseInt(tok.nextToken());
					if (tok.hasMoreTokens()) {
						String name = tok.nextToken().trim();
						
						int delim = name.lastIndexOf('/');
						String post = name.substring(delim+1);
						Set<String> set = filings.get(cik);
						if (set == null) {							
							set = new HashSet<String>();
						}
						set.add(post.trim());

						filings.put(cik, set);
						
						String type = tok.nextToken().trim();
						String date = tok.nextToken().trim();
						
						ftd.put(post, new String[] { type, date, cik.toString() } );
					}
				}
			}
			
			in = new FileInputStream(ctx.getRealPath("/WEB-INF/cik-accession-number.csv"));
			br = new BufferedReader(new InputStreamReader(in));

			line = null;
			boolean first = true;
			while ((line = br.readLine()) != null)   {
				if (first == true) {
					first = false;
					continue;
				}
				
				StringTokenizer tok = new StringTokenizer(line, ";");

				if (tok.hasMoreTokens()) {
					Integer cik = Integer.parseInt(tok.nextToken().trim());
					if (tok.hasMoreTokens()) {
						String name = tok.nextToken();
						
						//System.out.println(name);

						Set<String> set = filings.get(cik);
						if (set == null) {							
							set = new HashSet<String>();
						}
						set.add(name.trim());

						filings.put(cik, set);
					}
				}
			}

			ctx.setAttribute(CIKXBRL, filings);

			ctx.setAttribute(XBRLTYPEDATE, ftd);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				br.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


		try {
			Map<Integer, Set<String>> filings = new HashMap<Integer, Set<String>>();
			
			in = new FileInputStream(ctx.getRealPath("/WEB-INF/master-form4"));
			br = new BufferedReader(new InputStreamReader(in));

			String line = null;
			while ((line = br.readLine()) != null)   {
				StringTokenizer tok = new StringTokenizer(line, "\t");

				if (tok.hasMoreTokens()) {
					Integer cik = Integer.parseInt(tok.nextToken());
					if (tok.hasMoreTokens()) {
						String name = tok.nextToken().trim();
						
						Set<String> set = filings.get(cik);
						if (set == null) {							
							set = new HashSet<String>();
						}
						set.add(name.trim());

						filings.put(cik, set);
						
						//System.out.println("" + cik + set);
					}
				}
			}
			ctx.setAttribute(CIKFORM4, filings);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				br.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			Map<Integer, String> sics = new HashMap<Integer, String>();
			
			in = new FileInputStream(ctx.getRealPath("/WEB-INF/sic.tsv"));
			br = new BufferedReader(new InputStreamReader(in));

			String line = null;
			while ((line = br.readLine()) != null)   {
				StringTokenizer tok = new StringTokenizer(line, "\t");

				if (tok.hasMoreTokens()) {
					Integer sic = Integer.parseInt(tok.nextToken().trim());
					if (tok.hasMoreTokens()) {
						tok.nextToken();
						tok.nextToken();
						
						String label = tok.nextToken().trim();

						sics.put(sic, label);
					}
				}
			}
			ctx.setAttribute(SIC, sics);
			ctx.setAttribute(SICDBP, new HashMap<Integer, String>());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				br.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			Map<Integer, Integer> ciksic = new HashMap<Integer, Integer>();
			Map<Integer, Set<Integer>> siccik = new HashMap<Integer, Set<Integer>>();
			
			in = new FileInputStream(ctx.getRealPath("/WEB-INF/cik-sic.csv"));
			br = new BufferedReader(new InputStreamReader(in));

			String line = null;
			boolean first = true;
			while ((line = br.readLine()) != null)   {
				if (first == true) {
					first = false;
					continue;
				}
				
				StringTokenizer tok = new StringTokenizer(line, ";");

				if (tok.hasMoreTokens()) {
					Integer cik = Integer.parseInt(tok.nextToken().trim());
					if (tok.hasMoreTokens()) {
						Integer sic = Integer.parseInt(tok.nextToken().trim());

						ciksic.put(cik, sic);
						
						Set<Integer> ciks = siccik.get(sic);
						if (ciks == null) {
							ciks = new HashSet<Integer>();
							siccik.put(sic, ciks);
						}

						ciks.add(cik);
					}
				}
			}
			ctx.setAttribute(CIKSIC, ciksic);
			ctx.setAttribute(SICCIK, siccik);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				br.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	    XMLOutputFactory factory = XMLOutputFactory.newInstance();

	    ctx.setAttribute(FACTORY, factory);
	    
		try {
			Map<Integer, String> ciks = new HashMap<Integer, String>();
			in = new FileInputStream(ctx.getRealPath("/WEB-INF/dbpedia-cik.csv"));
			br = new BufferedReader(new InputStreamReader(in));

			String line = br.readLine();
			while ((line = br.readLine()) != null)   {
				StringTokenizer tok = new StringTokenizer(line, ",\"");

				if (tok.hasMoreTokens()) {
					Integer cik = Integer.parseInt(tok.nextToken());
					if (tok.hasMoreTokens()) {
						String name = tok.nextToken();

						ciks.put(cik, name.trim());
						//_log.info(cik + " " + name.trim());
					}
				}
			}
			ctx.setAttribute(CIKDBP, ciks);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				br.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			Map<Integer, String> fbciks = new HashMap<Integer, String>();
			in = new FileInputStream(ctx.getRealPath("/WEB-INF/freebase-ciks.txt"));
			br = new BufferedReader(new InputStreamReader(in));

			String line = br.readLine();
			while ((line = br.readLine()) != null)   {
				StringTokenizer tok = new StringTokenizer(line);

				if (tok.hasMoreTokens()) {
					String cik = tok.nextToken();
					fbciks.put(Integer.parseInt(cik), cik);
				}
			}
			ctx.setAttribute(CIKFB, fbciks);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				br.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
//		Cache cache = null;
//
//		try {
//			CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
//			cache = cacheFactory.createCache(Collections.emptyMap());
//			ctx.setAttribute(CACHE, cache);
//		} catch (CacheException e) {
//			e.printStackTrace();
//		}
		
//		DatastoreService dss = DatastoreServiceFactory.getDatastoreService();
//
//		ctx.setAttribute(DATASTORE, dss);
	}
		
	public void contextDestroyed(ServletContextEvent event) {
		//ServletContext ctx = event.getServletContext();
	}
}