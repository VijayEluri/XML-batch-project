<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/> 
<xsl:template match="/">
  <html>
<head>
	<meta http-equiv="Content-Language" content="en-us"/>
	<title><xsl:value-of select="object/name"/></title>

<link rel="stylesheet" type="text/css" href="manual.css"/>
</head>
  <body>
	  <h2><xsl:value-of select="object/name"/></h2>
	<h3>Description</h3>
	<p><xsl:value-of select="object/description"/>
	</p>
	<h3>Extends</h3>
	<p>You should also check <xsl:value-of select="object/extend"/> for Parameters
	</p>
	<h3>Parameters</h3>
<table border="1" cellpadding="2" cellspacing="0">
  <tr>
    <td valign="top"><b>Attribute</b></td>
    <td valign="top"><b>Description</b></td>
    <td align="center" valign="top"><b>Required</b></td>
  </tr>
  <xsl:for-each select="object/parameters/parameter">
      <tr>
        <td><xsl:value-of select="attribute"/></td>
        <td><xsl:value-of select="description"/></td>
	<td valign="top" align="center"><xsl:copy-of select="required"/> </td>
      </tr>
      </xsl:for-each>
    </table>
<h3>Parameters specified as nested elements</h3>
	<xsl:for-each select="object/nested/parameter">
	<h4><xsl:copy-of select="name"/></h4><xsl:copy-of select="description"/>
	</xsl:for-each>
	<h3>Objects that could act as the parent</h3>
	<xsl:for-each select="object/parents">
	<xsl:copy-of select="parent"/>
	</xsl:for-each>
<h3>Examples</h3>
	<xsl:for-each select="object/examples/example">
	<h4><xsl:copy-of select="description"/></h4><xsl:copy-of select="code"/>
	<xsl:for-each select="note"><strong>Note: </strong><xsl:copy-of select="."/><br/></xsl:for-each>
	</xsl:for-each>
  </body>
  </html>
</xsl:template>
</xsl:stylesheet>
