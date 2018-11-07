<!-- created by Tammy Fox tfox@redhat.com for the Fedora Project -->
<!-- License: GPL -->
<!-- Copyright 2003 Tammy Fox, Red Hat, Inc. -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exsl="http://exslt.org/common"
		version="1.0"
                exclude-result-prefixes="exsl">

<xsl:import href="/usr/share/sgml/docbook/xsl-stylesheets/html/docbook.xsl"/>
<xsl:import href="redhat.xsl"/>
<xsl:import href="/usr/share/sgml/docbook/xsl-stylesheets/html/chunk-common.xsl"/>
<xsl:include href="/usr/share/sgml/docbook/xsl-stylesheets/html/manifest.xsl"/>

<xsl:include href="/usr/share/sgml/docbook/xsl-stylesheets/html/chunk-code.xsl"/>

<xsl:include href="html-common.xsl"/>

<!-- TOC -->
<xsl:param name="toc.section.depth">3</xsl:param>
<xsl:param name="section.autolabel" select="1"/>
<xsl:param name="section.label.includes.component.label" select="1"></xsl:param>

<xsl:param name="generate.legalnotice.link" select="1"></xsl:param>

<xsl:param name="generate.toc">
book toc
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

<xsl:template match="revhistory">
  <xsl:variable name="numcols">
    <xsl:choose>
      <xsl:when test="//authorinitials">3</xsl:when>
      <xsl:otherwise>2</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:variable name="doctitle" select="//title"/>

  <div class="{name(.)}">
    <table border="1" width="100%" summary="Revision history - {$doctitle}">
      <tr>
        <th align="left" valign="top" colspan="{$numcols}">
          <b>
            <xsl:call-template name="gentext">
              <xsl:with-param name="key" select="'RevHistory'"/>
            </xsl:call-template>
          </b>
        </th>
      </tr>
      <xsl:apply-templates>
        <xsl:with-param name="numcols" select="$numcols"/>
      </xsl:apply-templates>
    </table>
  </div>
</xsl:template>

<xsl:template match="revhistory/revision">  
  <xsl:param name="numcols" select="'3'"/>
  <xsl:variable name="revnumber" select=".//revnumber"/>
  <xsl:variable name="revdate"   select=".//date"/>
  <xsl:variable name="revauthor" select=".//authorinitials"/>
  <xsl:variable name="revremark" select=".//revremark|.//revdescription"/>
  <tr>
    <td align="left">
      <xsl:if test="$revnumber">
        <xsl:call-template name="gentext">
          <xsl:with-param name="key" select="'Revision'"/>
        </xsl:call-template>
        <xsl:call-template name="gentext.space"/>
        <xsl:apply-templates select="$revnumber[1]"/>
      </xsl:if>
    </td>
    <td align="left">
      <xsl:apply-templates select="$revdate[1]"/>
    </td>
    <xsl:choose>
      <xsl:when test="$revauthor">
        <td align="left">
          <xsl:apply-templates select="$revauthor[1]"/>
        </td>
      </xsl:when>
      <xsl:when test="$numcols &gt; 2">
        <td>&#160;</td>
      </xsl:when>
      <xsl:otherwise></xsl:otherwise>
    </xsl:choose>
  </tr>
  <xsl:if test="$revremark">
    <tr>
      <td align="left" colspan="{$numcols}">
        <xsl:apply-templates select="$revremark[1]"/>
      </td>
    </tr>
  </xsl:if>
</xsl:template>

<xsl:template match="revhistory" mode="titlepage.mode">
  <xsl:variable name="id">revhistory</xsl:variable>
  <xsl:variable name="filename">
    <xsl:call-template name="make-relative-filename">
      <xsl:with-param name="base.dir" select="$base.dir"/>
      <xsl:with-param name="base.name" select="concat('rv-',$id,$html.ext)"/>
    </xsl:call-template>
  </xsl:variable>

  <a href="{concat('rv-',$id,$html.ext)}">Revision History</a>

  <xsl:call-template name="write.chunk">
    <xsl:with-param name="filename" select="$filename"/>
    <xsl:with-param name="quiet" select="$chunk.quietly"/>
    <xsl:with-param name="content">
      <xsl:call-template name="user.preroot"/>
      <html>
        <head>
          <xsl:call-template name="system.head.content"/>
          <xsl:call-template name="head.content"/>
          <xsl:call-template name="user.head.content"/>
        </head>
        <body>
          <xsl:call-template name="body.attributes"/>
          <div class="{local-name(.)}">
            <xsl:apply-templates select="."/>
          </div>
        </body>
      </html>
    </xsl:with-param>
  </xsl:call-template>
</xsl:template>


<!-- Navigation
<xsl:param name="navig.graphics" select="1"></xsl:param>
<xsl:param name="navig.graphics.extension" select="'.png'"></xsl:param>
<xsl:param name="navig.graphics.path">nav-images/</xsl:param>
<xsl:param name="navig.showtitles">1</xsl:param>
-->

</xsl:stylesheet>
