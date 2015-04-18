<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:edgar="http://www.sec.gov/Archives/edgar"
   xmlns:ed="http://edgarwrap.ontologycentral.com/vocab/edgar#"
   xmlns:iso4217="http://www.xbrl.org/2003/iso4217"
   xmlns:link="http://www.xbrl.org/2003/linkbase"
   xmlns:us-gaap="http://xbrl.us/us-gaap/2009-01-31"
   xmlns:xbrli="http://www.xbrl.org/2003/instance"
   xmlns:xlink="http://www.w3.org/1999/xlink"
   xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
   xmlns:dc="http://purl.org/dc/elements/1.1/"
   xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
   xmlns:foaf="http://xmlns.com/foaf/0.1/"
   xmlns:dbpprop="http://dbpedia.org/property/"
   xmlns="http://purl.org/rss/1.0/"
   version="1.0">

  <xsl:template match="rss">
    <rdf:RDF>
      <xsl:apply-templates/>
    </rdf:RDF>
  </xsl:template>

  <xsl:template match="channel">
    <channel rdf:about=""> <!--http://edgarwrap.ontologycentral.com/feed/usgaap">-->
      <title><xsl:value-of select="/rss/channel/title"/></title>
      <description>Recently updated SEC filings</description>
      <dc:title><xsl:value-of select="/rss/channel/title"/></dc:title>
      <rdfs:comment>No guarantee of correctness! USE AT YOUR OWN RISK!</rdfs:comment>
      <dc:publisher>U.S. Securities and Exchange Commission via edgarwrap (http://edgarwrap.ontologycentral.com/)</dc:publisher>
      <dc:date><xsl:value-of select="/rss/channel/pubDate"/></dc:date>

      <items>
	<rdf:Seq>
	  <xsl:for-each select="item">
	    <rdf:li>
	      <xsl:attribute name="rdf:resource">http://edgarwrap.ontologycentral.com/archive/<xsl:value-of select="format-number(edgar:xbrlFiling/edgar:cikNumber, 0)"/>/<xsl:value-of select="edgar:xbrlFiling/edgar:accessionNumber"/>#ds</xsl:attribute>
	    </rdf:li>
	  </xsl:for-each>
	</rdf:Seq>
      </items>
    </channel>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="item">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="edgar:xbrlFiling">
    <xsl:variable name="cik">
      <xsl:value-of select="format-number(edgar:cikNumber, 0)"/>
    </xsl:variable>

    <xsl:variable name="euri">
      <xsl:value-of select="substring(../enclosure/@url, 40)"/>
<!--
      <xsl:value-of select="substring(edgar:xbrlFiles/edgar:xbrlFile[@edgar:type='EX-101.INS']/@edgar:url, 40)"/>
-->
    </xsl:variable>

    <item>
      <xsl:attribute name="rdf:about">http://edgarwrap.ontologycentral.com/archive/<xsl:value-of select="$cik"/>/<xsl:value-of select="edgar:accessionNumber"/>#ds</xsl:attribute>
      <link>http://edgarwrap.ontologycentral.com/archive/<xsl:value-of select="$cik"/>/<xsl:value-of select="edgar:accessionNumber"/>#ds</link>
      <title><xsl:value-of select="edgar:companyName"/><xsl:text> </xsl:text><xsl:value-of select="edgar:formType"/></title>
      <dc:date><xsl:value-of select="../pubDate"/></dc:date>
      <rdf:type rdf:resource="http://edgarwrap.ontologycentral.com/vocab/edgar#Form"/>
      <ed:formType><xsl:value-of select="edgar:formType"/></ed:formType>
      <ed:accessionNumber><xsl:value-of select="edgar:accessionNumber"/></ed:accessionNumber>
      <ed:fileNumber><xsl:value-of select="edgar:fileNumber"/></ed:fileNumber>
      <ed:filingDate><xsl:value-of select="edgar:filingDate"/></ed:filingDate>
      <rdfs:seeAlso>
	<xsl:attribute name="rdf:resource">
	  <xsl:value-of select="../enclosure/@url"/>
<!--
	  <xsl:value-of select="edgar:xbrlFiles/edgar:xbrlFile[@edgar:type='EX-101.INS']/@edgar:url"/>
-->
	</xsl:attribute>
      </rdfs:seeAlso>
      <ed:issuer>
	<foaf:Agent>
	  <xsl:attribute name="rdf:about">../cik/<xsl:value-of select="$cik"/>#id</xsl:attribute>
	  <foaf:name><xsl:value-of select="edgar:companyName"/></foaf:name>
	  <dbpprop:secCik><xsl:value-of select="format-number(edgar:cikNumber, 0)"/></dbpprop:secCik>
	  <ed:cikNumber><xsl:value-of select="format-number(edgar:cikNumber, 0)"/></ed:cikNumber>
          <ed:assignedSic>
            <rdf:Description><xsl:attribute name="rdf:about">../sic/<xsl:value-of select="edgar:assignedSic"/>#id</xsl:attribute>
	    <ed:sic><xsl:value-of select="edgar:assignedSic"/></ed:sic>
	    </rdf:Description>
	  </ed:assignedSic>
	</foaf:Agent>
      </ed:issuer>
    </item>
  </xsl:template>
  
<!--
 <edgar:xbrlFiling xmlns:edgar="http://www.sec.gov/Archives/edgar">
        <edgar:companyName>COSTCO WHOLESALE CORP /NEW</edgar:companyName>
        <edgar:formType>10-Q</edgar:formType>
        <edgar:filingDate>12/17/2010</edgar:filingDate>
        <edgar:cikNumber>0000909832</edgar:cikNumber>
        <edgar:accessionNumber>0001193125-10-283424</edgar:accessionNumber>
        <edgar:fileNumber>000-20355</edgar:fileNumber>
        <edgar:acceptanceDatetime>20101217164925</edgar:acceptanceDatetime>
        <edgar:period>20101121</edgar:period>
        <edgar:assistantDirector>2</edgar:assistantDirector>
        <edgar:assignedSic>5331</edgar:assignedSic>
        <edgar:fiscalYearEnd>0828</edgar:fiscalYearEnd>
"http://www
.sec.gov/Archives/edgar/data/909832/000119312510283424/cost-20101121.xml


-->

  <xsl:template match="*"/>
</xsl:stylesheet>
