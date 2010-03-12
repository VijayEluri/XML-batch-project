<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/> 
<xsl:template match="/">
  <html>
<head>
	<meta http-equiv="Content-Language" content="en-us"/>
<title>Format User Manual</title>
<base target="mainFrame"/>
<link rel="stylesheet" type="text/css" href="stylesheets/antmanual.css"/>
</head>
  <body>
  <h2><a href="taskdirFtp.html" target="navFrame">Table of Contents</a></h2>
  	<table>
  <xsl:for-each select="directory/files/file">
      <tr>
        <td><a href="{dir}/{base}.html"><xsl:value-of select="base"/></a></td>
      </tr>
      </xsl:for-each>
    </table>
    <h2><a href="index.html" target="_top">Home</a></h2>
  </body>
  </html>
</xsl:template>
</xsl:stylesheet>
