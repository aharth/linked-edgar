<?xml version='1.0' encoding='utf-8'?>
<xsl:stylesheet
   xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
   xmlns:bkh="http://www.company.com/20100930"
   xmlns:dei="http://xbrl.us/dei/2009-01-31"
   xmlns:ed="http://edgarwrap.ontologycentral.com/vocab/edgar#"
   xmlns:iso4217="http://www.xbrl.org/2003/iso4217"
   xmlns:link="http://www.xbrl.org/2003/linkbase"
   xmlns:us-gaap="http://fasb.org/us-gaap/2011-01-31"
   xmlns:us-gaap1="http://xbrl.us/us-gaap/2009-01-31"
   xmlns:xbrli="http://www.xbrl.org/2003/instance"
   xmlns:xlink="http://www.w3.org/1999/xlink"
   xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
   xmlns:dc="http://purl.org/dc/terms/"
   xmlns:foaf="http://xmlns.com/foaf/0.1/"
   xmlns:ical="http://www.w3.org/2002/12/cal/ical#"
   xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
   xmlns:gaap="http://edgarwrap.ontologycentral.com/vocab/us-gaap#"
   xmlns:sdmx-measure="http://purl.org/linked-data/sdmx/2009/measure#"
   xmlns:qb="http://purl.org/linked-data/cube#"
   xmlns:skos="http://www.w3.org/2004/02/skos/core#"
   version='1.0'>

  <xsl:output method='xml' encoding='utf-8'/>
  
  <xsl:param name="cik"/>
  <xsl:param name="type"/>
  <xsl:param name="date"/>
  <xsl:param name="link"/>

  <xsl:template match='xbrli:xbrl'>
    <rdf:RDF>
      <rdf:Description rdf:about="">
	<rdfs:comment>No guarantee of correctness! USE AT YOUR OWN RISK!</rdfs:comment>
	<dc:publisher>U.S. Securities and Exchange Commission via edgarwrap (http://edgarwrap.ontologycentral.com/)</dc:publisher>
	<foaf:topic rdf:resource="#ds"/>
	<xsl:if test="$link">
	  <rdfs:seeAlso>
	    <xsl:attribute name="rdf:resource"><xsl:value-of select="$link"/></xsl:attribute>
	  </rdfs:seeAlso>
	</xsl:if>
      </rdf:Description>

      <qb:DataSet>
	<xsl:attribute name="rdf:about">#ds</xsl:attribute>
	<qb:structure rdf:resource="#dsd"/>
	<foaf:page rdf:resource=""/>
      </qb:DataSet>
      <ed:Form>
	<xsl:attribute name="rdf:about">#ds</xsl:attribute>

	<xsl:if test="$type">
	  <ed:formType><xsl:value-of select="$type"/></ed:formType>
	</xsl:if>
	<xsl:if test="$date">
	  <ed:filingDate><xsl:value-of select="$date"/></ed:filingDate>
	</xsl:if>
	<xsl:if test="$cik">
	  <ed:issuer>
	    <xsl:attribute name="rdf:resource">../../cik/<xsl:value-of select="$cik"/>#id</xsl:attribute>
	  </ed:issuer>
	</xsl:if>
      </ed:Form>

      <qb:DataStructureDefinition rdf:about="#dsd">      
	<qb:component>
	  <rdf:Description>
	    <qb:dimension>
	      <qb:DimensionProperty>
		<xsl:attribute name="rdf:about">http://edgarwrap.ontologycentral.com/vocab/edgar#issuer</xsl:attribute>
		<rdf:type rdf:resource="http://www.w3.org/2002/07/owl#ObjectProperty"/>
	      </qb:DimensionProperty>
	    </qb:dimension>
	  </rdf:Description>
	</qb:component>
      </qb:DataStructureDefinition>

      <qb:DataStructureDefinition rdf:about="#dsd">      
	<qb:component>
	  <rdf:Description>
	    <qb:dimension>
	      <qb:DimensionProperty>
		<xsl:attribute name="rdf:about">http://purl.org/dc/terms/date</xsl:attribute>
		<rdf:type rdf:resource="http://www.w3.org/2002/07/owl#DatatypeProperty"/>
	      </qb:DimensionProperty>
	    </qb:dimension>
	  </rdf:Description>
	</qb:component>
      </qb:DataStructureDefinition>

      <qb:DataStructureDefinition rdf:about="#dsd">      
	<qb:component>
	  <rdf:Description>
	    <qb:dimension>
	      <qb:DimensionProperty>
		<xsl:attribute name="rdf:about">http://edgarwrap.ontologycentral.com/vocab/edgar#segment</xsl:attribute>
		<rdf:type rdf:resource="http://www.w3.org/2002/07/owl#DatatypeProperty"/>
	      </qb:DimensionProperty>
	    </qb:dimension>
	  </rdf:Description>
	</qb:component>
      </qb:DataStructureDefinition>

      <qb:DataStructureDefinition rdf:about="#dsd">      
	<qb:component>
	  <rdf:Description>
	    <qb:dimension>
	      <qb:DimensionProperty>
		<xsl:attribute name="rdf:about">http://edgarwrap.ontologycentral.com/vocab/edgar#subject</xsl:attribute>
		<rdf:type rdf:resource="http://www.w3.org/2002/07/owl#ObjectProperty"/>
	      </qb:DimensionProperty>
	    </qb:dimension>
	  </rdf:Description>
	</qb:component>
      </qb:DataStructureDefinition>

      <xsl:apply-templates/>
    </rdf:RDF>
  </xsl:template>

  <xsl:template match="us-gaap:*|us-gaap1:*">
    <!-- only use numeric values as meausure, show strings as comments -->
    <xsl:choose>
      <xsl:when test="string-length(.) &lt; 20">
	<xsl:variable name="id">
	  <xsl:value-of select="@contextRef"/>
	</xsl:variable>
	
	<!--
	    <xsl:variable name="cik2">
	    <xsl:value-of select="/xbrli:xbrl/xbrli:context[@id = $id]/xbrli:entity/xbrli:identifier"/>
	    </xsl:variable> -->
	
<!--
	<qb:DataStructureDefinition rdf:about="#dsd">      
	  <qb:component>
	    <rdf:Description>
	      <qb:measure>
		<rdfs:Property>
		  <xsl:if test="namespace-uri() = 'http://fasb.org/us-gaap/2011-01-31'">
		    <xsl:attribute name="rdf:about">http://edgarwrap.ontologycentral.com/vocab/us-gaap-2011-01-31#<xsl:value-of select="local-name(.)"/></xsl:attribute>
		  </xsl:if>
		  <xsl:if test="namespace-uri() = 'http://xbrl.us/us-gaap/2009-01-31'">
		    <xsl:attribute name="rdf:about">http://edgarwrap.ontologycentral.com/vocab/us-gaap-2009-01-31#<xsl:value-of select="local-name(.)"/></xsl:attribute>
		  </xsl:if>
		</rdfs:Property>
	      </qb:measure>
	    </rdf:Description>
	  </qb:component>
	</qb:DataStructureDefinition>
-->
<!--
	<qb:MeasureProperty>
	  <xsl:if test="namespace-uri() = 'http://fasb.org/us-gaap/2011-01-31'">
	    <xsl:attribute name="rdf:about">http://edgarwrap.ontologycentral.com/vocab/us-gaap-2011-01-31#<xsl:value-of select="local-name(.)"/></xsl:attribute>
	  </xsl:if>
	  <xsl:if test="namespace-uri() = 'http://xbrl.us/us-gaap/2009-01-31'">
	    <xsl:attribute name="rdf:about">http://edgarwrap.ontologycentral.com/vocab/us-gaap-2009-01-31#<xsl:value-of select="local-name(.)"/></xsl:attribute>
	  </xsl:if>
	  
#<xsl:attribute name="rdf:about">http://edgarwrap.ontologycentral.com/vocab/us-gaap#<xsl:value-of select="local-name(.)"/></xsl:attribute>
	  <rdfs:subPropertyOf>
	    <xsl:attribute name="rdf:resource">http://purl.org/linked-data/sdmx/2009/measure#obsValue</xsl:attribute>
	  </rdfs:subPropertyOf>
	  <rdf:type rdf:resource="http://www.w3.org/2002/07/owl#DatatypeProperty"/>
	</qb:MeasureProperty>
-->
	<rdf:Description rdf:about="#ds">
	  <rdfs:seeAlso>
	    <qb:Observation>
	      <qb:dataSet>
		<xsl:attribute name="rdf:resource">#ds</xsl:attribute>
	      </qb:dataSet>

<!--
	      <xsl:element name="gaap:{local-name(.)}">
		<xsl:value-of select="."/>
	      </xsl:element>
-->

<ed:subject>
	  <xsl:if test="namespace-uri() = 'http://fasb.org/us-gaap/2011-01-31'">
	    <xsl:attribute name="rdf:resource">http://edgarwrap.ontologycentral.com/vocab/us-gaap-2011-01-31#<xsl:value-of select="local-name(.)"/></xsl:attribute>
	  </xsl:if>
	  <xsl:if test="namespace-uri() = 'http://xbrl.us/us-gaap/2009-01-31'">
	    <xsl:attribute name="rdf:resource">http://edgarwrap.ontologycentral.com/vocab/us-gaap-2009-01-31#<xsl:value-of select="local-name(.)"/></xsl:attribute>
	  </xsl:if>
	  <xsl:if test="namespace-uri() = 'http://xbrl.us/us-gaap/2008-03-31'">
	    <xsl:attribute name="rdf:resource">http://edgarwrap.ontologycentral.com/vocab/us-gaap-2008-03-31#<xsl:value-of select="local-name(.)"/></xsl:attribute>
	  </xsl:if>
</ed:subject>
	
	      <sdmx-measure:obsValue>
		<xsl:value-of select="."/>
	      </sdmx-measure:obsValue>
	
	      <xsl:if test="/xbrli:xbrl/xbrli:context[@id = $id]/xbrli:period/xbrli:instant">
		<dc:date><xsl:value-of select="/xbrli:xbrl/xbrli:context[@id = $id]/xbrli:period/xbrli:instant"/></dc:date>
	      </xsl:if>
	      
	      <xsl:if test="/xbrli:xbrl/xbrli:context[@id = $id]/xbrli:period/xbrli:startDate">
		<ical:dtstart><xsl:value-of select="/xbrli:xbrl/xbrli:context[@id = $id]/xbrli:period/xbrli:startDate"/></ical:dtstart>
		<ical:dtend><xsl:value-of select="/xbrli:xbrl/xbrli:context[@id = $id]/xbrli:period/xbrli:endDate"/></ical:dtend>
	      </xsl:if>
	      
	      <xsl:if test="/xbrli:xbrl/xbrli:context[@id = $id]/xbrli:entity/xbrli:identifier">
		<ed:issuer>
		  <xsl:attribute name="rdf:resource">../../cik/<xsl:value-of select="format-number(number(/xbrli:xbrl/xbrli:context[@id = $id]/xbrli:entity/xbrli:identifier), '################')"/>#id</xsl:attribute>
		</ed:issuer>
	      </xsl:if>

	      <ed:segment><xsl:value-of select="normalize-space(/xbrli:xbrl/xbrli:context[@id = $id])"/></ed:segment>
	    </qb:Observation>
	  </rdfs:seeAlso>
	</rdf:Description>
	<!--
	    <xsl:apply-templates select="/xbrli:xbrl/xbrli:context[@id = $id]/xbrli:context"/>
	-->
      </xsl:when>
      <xsl:otherwise>
	<rdf:Description rdf:about="#ds">
	  <xsl:element name="gaap:{local-name(.)}">
	    <xsl:value-of select="."/>
	  </xsl:element>
	</rdf:Description>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

<!--
  <xsl:template match="xbrli:period">
    <xsl:apply-templates/>
  </xsl:template>


  <xsl:template match="xbrli:instant">
    <dc:date><xsl:value-of select="."/></dc:date>
  </xsl:template>
-->

  <xsl:template match='*'/>

</xsl:stylesheet>
