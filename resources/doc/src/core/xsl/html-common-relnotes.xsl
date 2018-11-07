<!-- created by Tammy Fox tfox@redhat.com for the Fedora Project -->
<!-- License: GPL -->
<!-- Copyright 2003 Tammy Fox, Red Hat, Inc. -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exsl="http://exslt.org/common"
		version="1.0"
                exclude-result-prefixes="exsl">

<!-- Admonition Graphics -->

<xsl:param name="admon.graphics" select="1"></xsl:param>
<xsl:param name="admon.graphics.path">./stylesheet-images/</xsl:param>
<xsl:param name="callout.graphics.path">./stylesheet-images/</xsl:param>

<!-- titles after all elements -->

<xsl:param name="formal.title.placement">
figure after
example after
equation after
table after
procedure after
</xsl:param>

<xsl:output method="html" indent="no"/>

<!--
disable for FC4
<xsl:param name="generate.legalnotice.link" select="1"></xsl:param>
-->

<xsl:param name="html.stylesheet" select="'fedora.css'"></xsl:param>
<xsl:param name="html.stylesheet.type">text/css</xsl:param>

<xsl:param name="html.cleanup" select="1"></xsl:param>



</xsl:stylesheet>
