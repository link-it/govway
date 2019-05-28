#! /bin/bash
#find _build -name \*.html -exec ls {} \;
#for f in `ls _build/html/*.html `; do
# test=$1
# for f in `ls $test`; do
 for f in `find _build -name \*.html`; do    echo processing $f;
    #    xml_grep -v head $f |xml_grep -v 'div[@role="searchbox"]' | grep -v "searchbox" | grep -v "DOCTYPE" | grep -v "<html"| grep -v  "<body>"|grep -v "</body>" | grep -v "</html>" > $f.tmp;
    # perl -pi -e "s/%<![CDATA[&copy;]]>%/AMPERSAND/g" xhtmlout/*.html
    grep -v "<meta" $f | sed "s/&/AMPERSAND/g"|xml_grep -v head | xml_grep -v 'div[@role="contentinfo"]' | grep -v "2015 HPED LP" | sed "s/AMPERSAND/\&/g" | grep -v "DOCTYPE" | grep -v "<html"| grep -v  "<body"|grep -v "</body>" | grep -v "</html>" > $f.tmp;    
    sed "s/<section data-toggle=\"wy-nav-shift\" class=\"wy-nav-content-wrap\">/<section data-toggle=\"wy-nav-shift\" class=\"wy-nav-content-wrap\">\n{% include header-doc.html %}/g" $f.tmp > $f.tmp1
    sed "s/<\/section>/{% include footer-doc.html %}\n<\/section>/g" $f.tmp1 > $f.tmp2;
    echo "---
layout: doc
title: Documentazione
---" > $f.tmp3;
    cat $f.tmp2 >> $f.tmp3;
    mv $f.tmp3 $f;
    rm -f $f.tmp $f.tmp1 $f.tmp2 $f.tmp3;    
    done

# xml_grep -v 'div[@role="contentinfo"]' | grep -v "2015 HPED LP"

