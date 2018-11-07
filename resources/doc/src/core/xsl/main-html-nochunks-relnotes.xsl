<!-- created by Tammy Fox tfox@redhat.com for the Fedora Project -->
<!-- License: GPL -->
<!-- Copyright 2003 Tammy Fox, Red Hat, Inc. -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exsl="http://exslt.org/common"
                version="1.0"
                exclude-result-prefixes="exsl">

<xsl:import href="redhat.xsl"/>
<xsl:import href="/usr/share/sgml/docbook/xsl-stylesheets/html/docbook.xsl"/>
<xsl:include href="html-common-relnotes.xsl"/>

<!-- TOC -->
<xsl:param name="toc.section.depth">3</xsl:param>
<xsl:param name="section.autolabel" select="1" />
<xsl:param name="section.label.includes.component.label" select="1"></xsl:param>
<xsl:param name="generate.toc">
book toc,title,figure,table,example,equation
article toc
chapter nop
qandadiv toc
qandaset toc
sect1 nop
sect2 nop
sect3 nop
sect4 nop
sect5 nop
section nop
</xsl:param>

<!-- Navigation
<xsl:param name="navig.graphics" select="1"></xsl:param>
<xsl:param name="navig.graphics.extension" select="'.png'"></xsl:param>
<xsl:param name="navig.graphics.path">nav-images/</xsl:param>
<xsl:param name="navig.showtitles">1</xsl:param>
-->

<!-- one page -->
<xsl:param name="onechunk" select="1"/>

<!-- Generate UTF-8 html == only applies to the one chunk-->
<xsl:output method="html"
            encoding="UTF-8"
            indent="yes"/>

</xsl:stylesheet>
