<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xslt" version="1.0">
    <xsl:output method="xml" encoding="UTF-8" indent="yes" xalan:indent-amount="4"/>
    <!--Important!  Remove existing whitespace in DOM elements.-->
    <xsl:strip-space elements="*"/>
    <!--Identity transformation (see http://www.w3.org/TR/xslt#copying).-->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>      
