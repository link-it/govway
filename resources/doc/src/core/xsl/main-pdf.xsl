<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:fo="http://www.w3.org/1999/XSL/Format"
        xmlns:exsl="http://exslt.org/common"
>

<xsl:import href="/usr/share/sgml/docbook/xsl-stylesheets/fo/docbook.xsl"/>

<xsl:param name="use.extensions" select="0"/>
<xsl:param name="tablecolumns.extensions" select="0"/>

<xsl:param name="generate.toc">article toc</xsl:param>
<xsl:param name="toc.section.depth">3</xsl:param>
<xsl:param name="section.autolabel" select="1"/>
<xsl:param name="fop.extensions" select="0"/>

<xsl:param name="FDPDIR">..</xsl:param>

<!-- THIS MUST BE AN ABSOLUTE PATH OR URL; PDF GETS BUILT IN A TMPDIR  -->
<xsl:param name="callout.graphics.path">
  <xsl:value-of
    select="concat( $FDPDIR, '/docs-common/stylesheet-images/' )"/>
</xsl:param>

<xsl:param name="admon.graphics.path">
  <xsl:value-of
    select="concat( $FDPDIR, '/docs-common/stylesheet-images/' )"/>
</xsl:param>

<!-- FIXME -->

<!-- ***************  Redefined Templates  *************  -->
<!-- ***************************************************  -->

<!-- for some reason, some of Norman's stylesheets put an <fo:inline/>
     directly below a <fo:flow/> element, which causes problems.  So
     we wrap it in an <fo:block/> to get around this issue -->
<xsl:template match="anchor">
  <fo:block><fo:inline id="{@id}"/></fo:block>
</xsl:template>

<!-- generate some of our own pagemasters...
     This idea was stolen and modified from
     http://www.dpawson.co.uk/docbook/styling/titlepage.html -->
<xsl:template name="user.pagemasters">

  <fo:simple-page-master margin-right="0in"  margin-left="0in"
                         margin-bottom="0in" margin-top="0in"
                   page-height="{$page.height}"
                   page-width="{$page.width}"
                   master-name="cover">
    <xsl:element name="fo:region-body">
      <xsl:attribute name="margin-bottom">0in</xsl:attribute>
      <xsl:attribute name="margin-top">0in</xsl:attribute>
      <xsl:attribute name="margin-left">0in</xsl:attribute>
      <xsl:attribute name="margin-right">0in</xsl:attribute>
      <xsl:attribute name="background-repeat">no-repeat</xsl:attribute>
      <xsl:attribute name="background-image">
        <xsl:value-of select="concat( $FDPDIR, '/docs-common/stylesheet-images/titlepage.png' )"/>
      </xsl:attribute>
    </xsl:element>
    <fo:region-before extent="0pt" display-align="after" region-name="xsl-region-before-first"/>
    <fo:region-after extent="0pt" display-align="after" region-name="xsl-region-after-first"/>
  </fo:simple-page-master>

  <fo:page-sequence-master master-name="coversequence">
    <fo:repeatable-page-master-alternatives>
      <fo:conditional-page-master-reference master-reference="cover" page-position="first"/>
      <fo:conditional-page-master-reference master-reference="blank" blank-or-not-blank="blank"/>
    </fo:repeatable-page-master-alternatives>
  </fo:page-sequence-master>

</xsl:template>

<!-- modified this template from fo/pagesetup.xsl:1357
     if the element is book or chapter, select our custom titlepage -->
<xsl:template name="select.user.pagemaster">
  <xsl:param name="element"/>
  <xsl:param name="pageclass"/>
  <xsl:param name="default-pagemaster"/>

  <xsl:message>Got element=<xsl:value-of select="$element"/> pageclass=<xsl:value-of select="$pageclass"/> default=<xsl:value-of select="$default-pagemaster"/>
</xsl:message>

  <!-- by default, return the default. But if you've created your own
       pagemasters in user.pagemasters, you might want to select one here. -->
  <xsl:choose>
    <xsl:when test="$element = 'book' and $pageclass = 'titlepage'">
      <xsl:value-of select="'coversequence'"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$default-pagemaster"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<!-- The only modifications I made to this template, is to set a different
     fo:marker for each section level.  Therefore a section with $level=1
     will set the marker section1.head.marker;  We need this to show the
     top-level section on the right side of the running header -->
<xsl:template name="section.heading">
  <xsl:param name="level" select="1"/>
  <xsl:param name="marker" select="1"/>
  <xsl:param name="title"/>
  <xsl:param name="titleabbrev"/>

  <fo:block xsl:use-attribute-sets="section.title.properties">
    <xsl:if test="$marker != 0">
      <fo:block>
        <xsl:attribute name="font-family"><xsl:value-of select="$body.font.family"/></xsl:attribute>
        <xsl:attribute name="font-weight">normal</xsl:attribute>
      <fo:marker>
        <!-- start modification -->
        <xsl:attribute name="marker-class-name">section<xsl:value-of select="$level"/>.head.marker</xsl:attribute>
        <!-- end modification -->
        <xsl:choose>
          <xsl:when test="$titleabbrev = ''">
            <xsl:value-of select="$title"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$titleabbrev"/>
          </xsl:otherwise>
        </xsl:choose>
      </fo:marker>
      </fo:block>
    </xsl:if>
    <xsl:choose>
      <xsl:when test="$level=1">
        <fo:block xsl:use-attribute-sets="section.title.level1.properties">
          <xsl:copy-of select="$title"/>
        </fo:block>
      </xsl:when>
      <xsl:when test="$level=2">
        <fo:block xsl:use-attribute-sets="section.title.level2.properties">
          <xsl:copy-of select="$title"/>
        </fo:block>
      </xsl:when>
      <xsl:when test="$level=3">
        <fo:block xsl:use-attribute-sets="section.title.level3.properties">
          <xsl:copy-of select="$title"/>
        </fo:block>
      </xsl:when>
      <xsl:when test="$level=4">
        <fo:block xsl:use-attribute-sets="section.title.level4.properties">
          <xsl:copy-of select="$title"/>
        </fo:block>
      </xsl:when>
      <xsl:when test="$level=5">
        <fo:block xsl:use-attribute-sets="section.title.level5.properties">
          <xsl:copy-of select="$title"/>
        </fo:block>
      </xsl:when>
      <xsl:otherwise>
        <fo:block xsl:use-attribute-sets="section.title.level6.properties">
          <xsl:copy-of select="$title"/>
        </fo:block>
      </xsl:otherwise>
    </xsl:choose>
  </fo:block>
</xsl:template>

<xsl:template name="header.content">
  <xsl:param name="pageclass" select="''"/>
  <xsl:param name="sequence" select="''"/>
  <xsl:param name="position" select="''"/>
  <xsl:param name="gentext-key" select="''"/>

  <fo:block>

    <!-- sequence can be odd, even, first, blank -->
    <!-- position can be left, center, right -->
    <xsl:choose>
      <xsl:when test="$sequence = 'blank'">
        <!-- nothing -->
      </xsl:when>

      <xsl:when test="$position='left'">
        <!-- Same for odd, even, empty, and blank sequences -->
        <!-- Insert the chapter/appendix title -->
        <xsl:apply-templates select="." mode="object.title.markup"/>
      </xsl:when>

      <!-- put the section title on the right -->
      <xsl:when test="($sequence='odd' or $sequence='even') and $position='right'">
        <xsl:if test="$pageclass != 'titlepage'">
          <!--<xsl:choose>
            <xsl:when test="ancestor::book and ($double.sided != 0)">-->
            <fo:block>
              <xsl:attribute name="font-family">
                <xsl:value-of select="$body.font.family"/>
              </xsl:attribute>
              <fo:retrieve-marker retrieve-class-name="section1.head.marker"
                                  retrieve-position="first-including-carryover"
                                  retrieve-boundary="page-sequence">
              </fo:retrieve-marker>
            </fo:block>
          <!--</xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates select="." mode="titleabbrev.markup"/>
            </xsl:otherwise>
          </xsl:choose>-->
        </xsl:if>
      </xsl:when>

      <xsl:when test="$position='right'">
        <!-- nothing for empty, and blank sequences -->
      </xsl:when>

      <xsl:when test="$position='center'">
        <!-- Same for odd, even, empty, and blank sequences -->
        <!-- nothing in the center -->
      </xsl:when>


      <xsl:when test="$sequence = 'first'">
        <!-- nothing for first pages -->
      </xsl:when>

      <xsl:when test="$sequence = 'blank'">
        <!-- nothing for blank pages -->
      </xsl:when>
    </xsl:choose>
  </fo:block>
</xsl:template>

<!-- redefined from fo/pagesetup.xsl:1472 -->
<xsl:template name="header.table">
  <xsl:param name="pageclass" select="''"/>
  <xsl:param name="sequence" select="''"/>
  <xsl:param name="gentext-key" select="''"/>

  <!-- default is a single table style for all headers -->
  <!-- Customize it for different page classes or sequence location -->

  <xsl:choose>
      <xsl:when test="$pageclass = 'index'">
          <xsl:attribute name="margin-left">0pt</xsl:attribute>
      </xsl:when>
  </xsl:choose>

  <xsl:variable name="column1">
    <xsl:choose>
      <xsl:when test="$double.sided = 0">1</xsl:when>
      <xsl:when test="$sequence = 'first' or $sequence = 'odd'">1</xsl:when>
      <xsl:otherwise>3</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="column3">
    <xsl:choose>
      <xsl:when test="$double.sided = 0">3</xsl:when>
      <xsl:when test="$sequence = 'first' or $sequence = 'odd'">3</xsl:when>
      <xsl:otherwise>1</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="candidate">
    <fo:table table-layout="fixed" width="100%">
      <xsl:call-template name="head.sep.rule">
        <xsl:with-param name="pageclass" select="$pageclass"/>
        <xsl:with-param name="sequence" select="$sequence"/>
        <xsl:with-param name="gentext-key" select="$gentext-key"/>
      </xsl:call-template>

      <fo:table-column column-number="1">
        <xsl:attribute name="column-width">
          <xsl:text>proportional-column-width(</xsl:text>
          <xsl:call-template name="header.footer.width">
            <xsl:with-param name="location">header</xsl:with-param>
            <xsl:with-param name="position" select="$column1"/>
          </xsl:call-template>
          <xsl:text>)</xsl:text>
        </xsl:attribute>
      </fo:table-column>
      <fo:table-column column-number="2">
        <xsl:attribute name="column-width">
          <xsl:text>proportional-column-width(</xsl:text>
          <xsl:call-template name="header.footer.width">
            <xsl:with-param name="location">header</xsl:with-param>
            <xsl:with-param name="position" select="2"/>
          </xsl:call-template>
          <xsl:text>)</xsl:text>
        </xsl:attribute>
      </fo:table-column>
      <fo:table-column column-number="3">
        <xsl:attribute name="column-width">
          <xsl:text>proportional-column-width(</xsl:text>
          <xsl:call-template name="header.footer.width">
            <xsl:with-param name="location">header</xsl:with-param>
            <xsl:with-param name="position" select="$column3"/>
          </xsl:call-template>
          <xsl:text>)</xsl:text>
        </xsl:attribute>
      </fo:table-column>

      <fo:table-body>
        <fo:table-row height="14pt">
          <fo:table-cell text-align="left"
                         display-align="before">
            <xsl:if test="$fop.extensions = 0">
              <xsl:attribute name="relative-align">baseline</xsl:attribute>
            </xsl:if>
            <fo:block>
              <xsl:call-template name="header.content">
                <xsl:with-param name="pageclass" select="$pageclass"/>
                <xsl:with-param name="sequence" select="$sequence"/>
                <xsl:with-param name="position" select="'left'"/>
                <xsl:with-param name="gentext-key" select="$gentext-key"/>
              </xsl:call-template>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell text-align="center"
                         display-align="before">
            <xsl:if test="$fop.extensions = 0">
              <xsl:attribute name="relative-align">baseline</xsl:attribute>
            </xsl:if>
            <fo:block>
              <xsl:call-template name="header.content">
                <xsl:with-param name="pageclass" select="$pageclass"/>
                <xsl:with-param name="sequence" select="$sequence"/>
                <xsl:with-param name="position" select="'center'"/>
                <xsl:with-param name="gentext-key" select="$gentext-key"/>
              </xsl:call-template>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell text-align="right"
                         display-align="before">
            <xsl:if test="$fop.extensions = 0">
              <xsl:attribute name="relative-align">baseline</xsl:attribute>
            </xsl:if>
            <fo:block>
              <xsl:call-template name="header.content">
                <xsl:with-param name="pageclass" select="$pageclass"/>
                <xsl:with-param name="sequence" select="$sequence"/>
                <xsl:with-param name="position" select="'right'"/>
                <xsl:with-param name="gentext-key" select="$gentext-key"/>
              </xsl:call-template>
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
      </fo:table-body>
    </fo:table>
  </xsl:variable>

  <!-- Really output a header? -->
  <xsl:choose>
    <xsl:when test="$pageclass = 'titlepage' and $gentext-key = 'book'
                    and $sequence='first'">
      <!-- no, book titlepages have no headers at all -->
    </xsl:when>
    <!-- begin modification -->
    <!-- This gets rid of headers on chapter front pages -->
    <xsl:when test="$pageclass = 'body' and $sequence = 'first' and $gentext-key = 'chapter'">
      <!-- no, chapter front pages have no headers at all -->
    </xsl:when>
    <!-- end modification -->
    <xsl:when test="$sequence = 'blank' and $headers.on.blank.pages = 0">
      <!-- no output -->
    </xsl:when>
    <xsl:otherwise>
      <xsl:copy-of select="$candidate"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>



<!-- ***************  Additional  *********************  -->
<!-- ***************************************************  -->



<!-- ***************  Admonitions  *********************  -->
<!-- ***************************************************  -->


<!-- If true (non-zero), admonitions are presented in an alternate style that uses a graphic. Default graphics are provided in the distribution. -->
<xsl:param name="admon.graphics" select="1"></xsl:param>

<!-- If true (non-zero), admonitions are presented with a generated text label such as Note or Warning in the appropriate language. If zero, such labels are turned off, but any title children of the admonition element are still output. The default value is 1. -->
<xsl:param name="admon.textlabel" select="0"></xsl:param>


<!-- ***************  Automatic labelling  *********************  -->
<!-- ***************************************************  -->


<!-- If true (non-zero), unlabeled sections are enumerated. -->
<xsl:param name="section.autolabel" select="0"></xsl:param>

<!-- When section numbering is turned on by the section.autolabel parameter, this parameter controls the depth of section nesting that is numbered. Sections nested to a level deeper than this value will not be numbered. -->
<xsl:param name="section.autolabel.max.depth" select="1"></xsl:param>


<!-- ***************  Bibliography  *********************  -->
<!-- ***************************************************  -->



<!-- ***************  Callouts  *********************  -->
<!-- ***************************************************  -->


<!-- If non-zero, callouts are presented with graphics (e.g., reverse-video circled numbers instead of "(1)", "(2)", etc.). Default graphics are provided in the distribution. -->
<xsl:param name="callout.graphics" select="'0'"></xsl:param>

<!-- The stylesheets can use either an image of the numbers one to ten, or the single Unicode character which represents the numeral, in white on a black background. Use this to select the Unicode character option.  -->
<xsl:param name="callout.unicode" select="1"></xsl:param>


<!-- The callouts extension processes areaset elements in ProgramListingCO and other text-based callout elements.  -->
<xsl:param name="callouts.extension" select="'1'"></xsl:param>


<!-- ***************  Cross References  *********************  -->
<!-- ***************************************************  -->


<!-- ***************  EBNF  *********************  -->
<!-- ***************************************************  -->


<!-- ***************  Font Families  *********************  -->
<!-- ***************************************************  -->


<!-- ***************  Glossary  *********************  -->
<!-- ***************************************************  -->


<!-- ***************  Graphics  *********************  -->
<!-- ***************************************************  -->


<!-- ***************  Linking  *********************  -->
<!-- ***************************************************  -->


<!-- ***************  Lists  *********************  -->
<!-- ***************************************************  -->

<!-- Specify what spacing you want between each list item when spacing is "compact". -->
<xsl:attribute-set name="compact.list.item.spacing">
  <xsl:attribute name="space-before.optimum">
    0em
  </xsl:attribute>
  <xsl:attribute name="space-before.minimum">
    0em
  </xsl:attribute>
  <xsl:attribute name="space-before.maximum">
    0.2em
  </xsl:attribute>
</xsl:attribute-set>

<!-- Specify the spacing required before and after a list. It is necessary to specify the space after a list block because lists can come inside of paras. -->
<xsl:attribute-set name="list.block.spacing">
  <xsl:attribute name="space-before.optimum">
    0.3em
  </xsl:attribute>
  <xsl:attribute name="space-before.minimum">
    0.1em
  </xsl:attribute>
  <xsl:attribute name="space-before.maximum">
    0.5em
  </xsl:attribute>
  <xsl:attribute name="space-after.optimum">
    0.3em
  </xsl:attribute>
  <xsl:attribute name="space-after.minimum">
    0.1em
  </xsl:attribute>
  <xsl:attribute name="space-after.maximum">
    0.5em
  </xsl:attribute>
</xsl:attribute-set>

<!-- Specify what spacing you want between each list item. -->
<xsl:attribute-set name="list.item.spacing">
  <xsl:attribute name="space-before.optimum">
    0.5em
  </xsl:attribute>
  <xsl:attribute name="space-before.minimum">
    0.3em
  </xsl:attribute>
  <xsl:attribute name="space-before.maximum">
    0.7em
  </xsl:attribute>
</xsl:attribute-set>



<!-- ***************  Localization  *********************  -->
<!-- ***************************************************  -->


<!-- ***************  Meta/*Info  *********************  -->
<!-- ***************************************************  -->


<!-- ***************  Miscellaneous  *********************  -->
<!-- ***************************************************  -->


<!-- If the lines of a program listing are too long to fit into one line, it is quite common to split them at a space and indicate by hook arrow that code continues on the next line. You can turn on this behaviour for programlisting, screen, and synopsis elements by using this parameter. Note that you must also enable line wrapping for verbatim environments and select an appropriate hyphenation character (e.g. hook arrow). This can be done using the monospace.verbatim.properties attribute set. See the DocBook XSL distribution's documentation for more help and an example. -->
<xsl:param name="hyphenate.verbatim" select="1"></xsl:param>

<!-- Separator used to connect items of a menuchoice with guimenuitem or guisubmenu. Other elements are linked with menuchoice.separator. -->
<xsl:param name="menuchoice.menu.separator" select="'→'"></xsl:param>


<!-- Should verbatim environments be shaded? In the FO stylesheet, if this parameter is non-zero then the shade.verbatim.style properties will be applied to verbatim environments. In the HTML stylesheet, this parameter is now deprecated. Use CSS instead. -->
<xsl:param name="shade.verbatim" select="1"></xsl:param>

<!-- Properties that specify the style of shaded verbatim listings -->
<xsl:attribute-set name="shade.verbatim.style">
  <xsl:attribute name="background-color">#F0F0F0</xsl:attribute>
</xsl:attribute-set>

<!-- If non-zero, the URL of each ULink will appear after the text of the link. If the text of the link and the URL are identical, the URL is suppressed. -->
<xsl:param name="ulink.show" select="1"/>

<!-- If non-zero, the URL of each ULink will appear as a footnote. -->
<xsl:param name="ulink.footnotes" select="0"></xsl:param>

<!-- If non-zero, variablelists will be formatted as blocks.  If you have long terms, proper list markup in the FO case may produce unattractive lists.  By setting this parameter, you can force the stylesheets to produce block markup instead of proper lists.  See the DocBook XSL distribution's documentation for help. -->
<xsl:param name="variablelist.as.blocks" select="1"/>


<!-- ***************  Pagination and General Styles  *********************  -->
<!-- ***************************************************  -->

<!--  Selects draft mode. If draft.mode is "yes", the entire document will be treated as a draft. If it is "no", the entire document will be treated as a final copy. If it is "maybe", individual sections will be treated as draft or final independently, depending on how their status attribute is set. -->
<xsl:param name="draft.mode" select="'no'"></xsl:param>

<!-- If non-zero, a rule will be drawn above the page footers. -->
<xsl:param name="footer.rule" select="0"></xsl:param>

<!-- If non-zero, a rule will be drawn below the page headers. -->
<xsl:param name="header.rule" select="1"></xsl:param>

<!-- make center cell small in header -->
<xsl:param name="header.column.widths" select="'10 1 8'"></xsl:param>

<!-- marker.section.level -->
<xsl:param name="marker.section.level" select="3"/>

<!--  This parameter adjusts the left margin for titles, effectively leaving the titles at the left margin and indenting the body text. The default value is -4pc, which means the body text is indented 4 picas relative to the titles. If you set the value to zero, be sure to still include a unit indicator such as 0pt, or the FO processor will report errors. This parameter is set to 0pt if the passivetex.extensions parameter is nonzero because PassiveTeX cannot handle the math expression with negative values used to calculate the indents. -->
<xsl:param name="title.margin.left">
  <xsl:choose>
    <xsl:when test="$passivetex.extensions != 0">
      0pt
    </xsl:when>
  <xsl:otherwise>
     0pc
  </xsl:otherwise>
  </xsl:choose>
</xsl:param>


<!-- ***************  Processor Extensions  *********************  -->
<!-- ***************************************************  -->

<!-- If non-zero, FOP extensions will be used. At present, this consists of PDF bookmarks. This parameter can also affect which graphics file formats are supported. -->
<xsl:param name="fop.extensions" select="1"></xsl:param>


<!-- ***************  Profiling  *********************  -->
<!-- ***************************************************  -->


<!-- ***************  Property Sets  *********************  -->
<!-- ***************************************************  -->

<!-- What font and size do you want for monospaced content? Specify the font name and size you want for monospaced output. -->
<xsl:attribute-set name="monospace.verbatim.properties" use-attribute-sets="verbatim.properties monospace.properties">
  <xsl:attribute name="font-family">
    <xsl:value-of select="$monospace.font.family"/>
  </xsl:attribute>
  <!--<xsl:attribute name="border-color">#000000</xsl:attribute>
  <xsl:attribute name="border-style">solid</xsl:attribute>
  <xsl:attribute name="border-width">heavy</xsl:attribute>-->
  <xsl:attribute name="wrap-option">wrap</xsl:attribute>
  <xsl:attribute name="hyphenation-character">&#x21B2;</xsl:attribute>
  <!--<xsl:attribute name="wrap-option">no-wrap</xsl:attribute>-->
  <xsl:attribute name="padding-top">3pt</xsl:attribute>
  <xsl:attribute name="padding-bottom">3pt</xsl:attribute>
  <xsl:attribute name="padding-left">3pt</xsl:attribute>
  <xsl:attribute name="padding-right">3pt</xsl:attribute>
</xsl:attribute-set>


<!-- The properties of the fo:root element. This property set is used on the fo:root element of an FO file. It defines a set of default, global parameters. -->
<xsl:attribute-set name="root.properties">
  <xsl:attribute name="font-family">
    <xsl:value-of select="$body.fontset"></xsl:value-of>
  </xsl:attribute>
  <xsl:attribute name="font-size">
    <xsl:value-of select="$body.font.size"></xsl:value-of>
  </xsl:attribute>
  <xsl:attribute name="text-align">
    <xsl:value-of select="$alignment"></xsl:value-of>
  </xsl:attribute>
  <xsl:attribute name="line-height">
    <xsl:value-of select="$line-height"></xsl:value-of>
  </xsl:attribute>
  <xsl:attribute name="font-selection-strategy">character-by-character</xsl:attribute>
  <xsl:attribute name="line-height-shift-adjustment">disregard-shifts</xsl:attribute>
</xsl:attribute-set>

<!--  The properties that apply to the containing block of a level-1 section, and therefore apply to the whole section. This includes sect1 elements and section elements at level 1. See the DocBook XSL distribution's documentation for more help and an example. -->
<xsl:attribute-set name="section.level1.properties" use-attribute-sets="section.properties">
  <!--  <xsl:attribute name="break-before">page</xsl:attribute> -->
</xsl:attribute-set>

<!--  The properties that apply to the containing block of a level-2 section, and therefore apply to the whole section. This includes sect2 elements and section elements at level 2. See the DocBook XSL distribution's documentation for more help and an example. -->
<xsl:attribute-set name="section.level2.properties" use-attribute-sets="section.properties">
  <!--<xsl:attribute name="start-indent">0pc</xsl:attribute>-->
</xsl:attribute-set>

<!--  The properties that apply to the containing block of a level-3 section, and therefore apply to the whole section. This includes sect3 elements and section elements at level 3. See the DocBook XSL distribution's documentation for more help and an example. -->
<xsl:attribute-set name="section.level3.properties" use-attribute-sets="section.properties">
  <!--<xsl:attribute name="start-indent">0pc</xsl:attribute>-->
</xsl:attribute-set>


<!--  The properties of level-1 section titles. -->
<xsl:attribute-set name="section.title.level1.properties">
  <xsl:attribute name="font-size">
      <xsl:value-of select="$body.font.master * 2.0 "></xsl:value-of><xsl:text>pt</xsl:text>
  </xsl:attribute>
  <xsl:attribute name="font-weight">bold</xsl:attribute>
  <!--  <xsl:attribute name="border-bottom">1.5pt solid black</xsl:attribute> -->
  <xsl:attribute name="padding-top">6pt</xsl:attribute>
  <xsl:attribute name="padding-bottom">3pt</xsl:attribute>
</xsl:attribute-set>

<!--  The properties of level-2 section titles. -->
<xsl:attribute-set name="section.title.level2.properties">
    <xsl:attribute name="font-size">
      <xsl:value-of select="$body.font.master * 1.6"></xsl:value-of><xsl:text>pt</xsl:text>
  </xsl:attribute>
  <xsl:attribute name="font-weight">bold</xsl:attribute>
  <!--<xsl:attribute name="start-indent">2pc</xsl:attribute>-->
</xsl:attribute-set>

<!--  The properties of level-3 section titles. -->
<xsl:attribute-set name="section.title.level3.properties">
  <xsl:attribute name="font-size">
      <xsl:value-of select="$body.font.master * 1.3"></xsl:value-of><xsl:text>pt</xsl:text>
  </xsl:attribute>
  <!--<xsl:attribute name="start-indent">4pc</xsl:attribute>-->
  <xsl:attribute name="font-weight">normal</xsl:attribute>
  <xsl:attribute name="font-style">italic</xsl:attribute>
</xsl:attribute-set>


<!--  This attribute set is used on all verbatim environments. -->
<xsl:attribute-set name="verbatim.properties">
  <xsl:attribute name="font-size">
    <xsl:value-of select="$body.font.master * 0.8"/>
    <xsl:text>pt</xsl:text>
  </xsl:attribute>
  <xsl:attribute name="space-before.minimum">
    0.3em
  </xsl:attribute>
  <xsl:attribute name="space-before.optimum">
    0.5em
  </xsl:attribute>
  <xsl:attribute name="space-before.maximum">
    0.7em
  </xsl:attribute>
  <xsl:attribute name="space-after.minimum">
    0.3em
  </xsl:attribute>
  <xsl:attribute name="space-after.optimum">
    0.5em
  </xsl:attribute>
  <xsl:attribute name="space-after.maximum">
    0.7em
  </xsl:attribute>
<!--  <xsl:attribute name="hyphenate">
    false
  </xsl:attribute> -->
</xsl:attribute-set>


<!-- ***************  QAndASet  *********************  -->
<!-- ***************************************************  -->

<!-- If no defaultlabel attribute is specified on a QandASet, this value is used. It must be one of the legal values for the defaultlabel attribute. -->
<xsl:param name="qanda.defaultlabel">
qanda
</xsl:param>


<!-- ***************  Reference Pages  *********************  -->
<!-- ***************************************************  -->


<!-- ***************  Stylesheet Extensions  *********************  -->
<!-- ***************************************************  -->

<!-- If line numbering is enabled, every Nth line will be numbered. -->
<xsl:param name="linenumbering.everyNth" select="'1'"></xsl:param>

<!-- If true, verbatim environments (elements that have the format='linespecific' notation attribute: address, literallayout, programlisting, screen, synopsis) that specify line numbering will have, surprise, line numbers. -->
<xsl:param name="linenumbering.extension" select="'1'"></xsl:param>

<!-- If non-zero, extensions may be used. Each extension is further controlled by its own parameter. But if use.extensions is zero, no extensions will be used. -->
<xsl:param name="use.extensions" select="'1'"></xsl:param>

<!-- The table columns extension function adjusts the widths of table columns in the HTML result to more accurately reflect the specifications in the CALS tables -->
<!-- it seems that if this is set, the table widths don't get converted properly for print output -->
<xsl:param name="tablecolumns.extension" select="'0'" />


<!-- ***************  Tables  *********************  -->
<!-- ***************************************************  -->

<!-- If specified, this value will be used for the width attribute on tables that do not specify an alternate width (with the dbhtml processing instruction) -->
<xsl:param name="table.default.width">6in</xsl:param>

<!-- In order to convert CALS column widths into HTML column widths, it is sometimes necessary to have an absolute table width to use for conversion of mixed absolute and relative widths.  This value must be an absolute length (not a percentage)  -->
<xsl:param name="nominal.table.width">6in</xsl:param>

<xsl:param name="table.frame.border.thickness">0.25pt</xsl:param>
<xsl:param name="table.frame.border.style">none</xsl:param>
<xsl:param name="table.frame.border.color">black</xsl:param>
<xsl:param name="table.cell.border.thickness">0.5pt</xsl:param>
<xsl:param name="table.cell.border.style">solid</xsl:param>
<xsl:param name="table.cell.border.color">black</xsl:param>

<xsl:attribute-set name="table.cell.padding">
  <xsl:attribute name="padding-left">1pt</xsl:attribute>
  <xsl:attribute name="padding-right">1pt</xsl:attribute>
  <xsl:attribute name="padding-top">1pt</xsl:attribute>
  <xsl:attribute name="padding-bottom">1pt</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="table.table.properties">
  <xsl:attribute name="border-before-width.conditionality">retain</xsl:attribute>
  <xsl:attribute name="border-collapse">collapse</xsl:attribute>
</xsl:attribute-set>

<!-- ***************  ToC/LoT/Index Generation  *********************  -->
<!-- ***************************************************  -->

<!-- This is an excellent reference:  http://www.sagehill.net/docbookxsl/TOCcontrol.html -->

<!-- Specify if an index should be generated -->
<xsl:param name="generate.index" select="1" />

<!-- Generate XML index markup in the index? -->
<xsl:param name="make.index.markup" select="0"/>

<!-- Properties associated with the letter headings in an index -->
<xsl:attribute-set name="index.div.title.properties">
  <xsl:attribute name="margin-left">0pt</xsl:attribute>
  <xsl:attribute name="font-size">14.4pt</xsl:attribute>
  <xsl:attribute name="font-family">serif</xsl:attribute>
  <xsl:attribute name="font-weight">bold</xsl:attribute>
  <xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
  <xsl:attribute name="space-before.optimum"><xsl:value-of select="concat($body.font.master,'pt')"></xsl:value-of></xsl:attribute>
  <xsl:attribute name="space-before.minimum"><xsl:value-of select="concat($body.font.master,'pt * 0.8')"></xsl:value-of></xsl:attribute>
  <xsl:attribute name="space-before.optimum"><xsl:value-of select="concat($body.font.master,'pt * 1.2')"></xsl:value-of></xsl:attribute>
  <xsl:attribute name="start-indent">0pt</xsl:attribute>
</xsl:attribute-set>

<!-- Properties applied to the formatted entries in an index -->
<xsl:attribute-set name="index.entry.properties">
  <xsl:attribute name="start-indent">0pt</xsl:attribute>
  <xsl:attribute name="font-family">serif</xsl:attribute>
</xsl:attribute-set>

<!-- Specifies the depth to which recursive sections should appear in the TOC -->
<xsl:param name="toc.section.depth" select="2" />

<!-- How maximaly deep should be each TOC? -->
<xsl:param name="toc.max.depth" select="3" />


<!-- ***************  XSLT Processing  *********************  -->
<!-- ***************************************************  -->
<!-- FIXME -->

</xsl:stylesheet>
