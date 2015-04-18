<?xml version='1.0' encoding='utf-8'?>
<xsl:stylesheet
   xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
   xmlns:bkh="http://www.company.com/20100930"
   xmlns:dei="http://xbrl.us/dei/2009-01-31"
   xmlns:ed="http://edgarwrap.ontologycentral.com/vocab/edgar#"
   xmlns:iso4217="http://www.xbrl.org/2003/iso4217"
   xmlns:link="http://www.xbrl.org/2003/linkbase"
   xmlns:us-gaap="http://xbrl.us/us-gaap/2009-01-31"
   xmlns:xbrli="http://www.xbrl.org/2003/instance"
   xmlns:xlink="http://www.w3.org/1999/xlink"
   xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
   xmlns:foaf="http://xmlns.com/foaf/0.1/"
   xmlns:dc="http://purl.org/dc/terms/"
   xmlns:ical="http://www.w3.org/2002/12/cal/ical#"
   xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
   xmlns:gaap="http://edgarwrap.ontologycentral.com/vocab/us-gaap#"
   xmlns:sdmx-measure="http://purl.org/linked-data/sdmx/2009/measure#"
   xmlns:qb="http://purl.org/linked-data/cube#"
   xmlns:skos="http://www.w3.org/2004/02/skos/core#"
   xmlns:v="http://www.w3.org/2006/vcard/ns#"
   version='1.0'>

  <xsl:output method='xml' encoding='utf-8'/>

  <xsl:param name="link"/>
  <xsl:param name="date"/>
  
  <xsl:template match="ownershipDocument">
    <rdf:RDF>
      <rdf:Description rdf:about="">
	<rdfs:comment>No guarantee of correctness! USE AT YOUR OWN RISK!</rdfs:comment>
	<dc:publisher>U.S. Securities and Exchange Commission via edgarwrap (http://edgarwrap.ontologycentral.com/)</dc:publisher>
	<xsl:if test="$link">
	  <rdfs:seeAlso>
	    <xsl:attribute name="rdf:resource"><xsl:value-of select="$link"/></xsl:attribute>
 	  </rdfs:seeAlso>
	</xsl:if>
	<foaf:topic rdf:resource="#ds"/>
      </rdf:Description>

      <qb:Dataset>
	<xsl:attribute name="rdf:about">#ds</xsl:attribute>
	<foaf:page rdf:resource=""/>
      </qb:Dataset>
      <ed:Form>
	<xsl:attribute name="rdf:about">#ds</xsl:attribute>

	<ed:formType>4</ed:formType>
	<xsl:if test="$date">
	  <ed:filingDate><xsl:value-of select="$date"/></ed:filingDate>
	</xsl:if>

	<xsl:apply-templates/>
      </ed:Form>
    </rdf:RDF>
  </xsl:template>

  <xsl:template match="issuer">
    <ed:issuer>
      <xsl:attribute name="rdf:resource">../../cik/<xsl:value-of select="format-number(issuerCik, 0)"/>#id</xsl:attribute>
    </ed:issuer>
  </xsl:template>

  <xsl:template match="reportingOwner">
    <ed:owner>
      <foaf:Agent>
	<xsl:attribute name="rdf:about">../../cik/<xsl:value-of select="format-number(reportingOwnerId/rptOwnerCik, 0)"/>#id</xsl:attribute>
	<foaf:name><xsl:value-of select="reportingOwnerId/rptOwnerName"/></foaf:name>
	<v:adr>
	  <rdf:Description>
            <v:street-address><xsl:value-of select="reportingOwnerAddress/rptOwnerStreet1"/></v:street-address>
	    <v:extended-address><xsl:value-of select="reportingOwnerAddress/rptOwnerStreet2"/></v:extended-address>
            <v:locality><xsl:value-of select="reportingOwnerAddress/rptOwnerCity"/></v:locality>
            <v:postal-code><xsl:value-of select="reportingOwnerAddress/rptOwnerZipCode"/></v:postal-code>
	    <v:region><xsl:value-of select="reportingOwnerAddress/rptOwnerState"/></v:region>
	  </rdf:Description>  
	</v:adr>
	<xsl:if test="reportingOwnerRelationship/isDirector = 1">
	  <ed:directorOf>
	    <xsl:attribute name="rdf:resource">../../cik/<xsl:value-of select="format-number(reportingOwnerId/rptOwnerCik, 0)"/></xsl:attribute>
	  </ed:directorOf>
	</xsl:if>
	<xsl:if test="reportingOwnerRelationship/isOfficer = 1">
	  <ed:officerOf>
	    <xsl:attribute name="rdf:resource">../../cik/<xsl:value-of select="format-number(reportingOwnerId/rptOwnerCik, 0)"/></xsl:attribute>
	  </ed:officerOf>
	</xsl:if>
      </foaf:Agent>
    </ed:owner>
  </xsl:template>

  <xsl:template match='*'/>

</xsl:stylesheet>
