{   
    "SortAs": "${xpath.read("//prova/text()")}",
    "GlossTerm": "Standard Generalized Markup Language",
    "Acronym": "${xPath.read("//{http://schemas.xmlsoap.org/soap/envelope/}:Envelope/{http://schemas.xmlsoap.org/soap/envelope/}:Body/prova2/text()")}",
    "Abbrev": "ISO 8879:1986",
    "Enabled": true,
    "Year": 2018,
    "Quote": 1.45,
    "include1": {
<#include "TestJsonInclude_1.ftl">

    },
    "include2": {
<#include "lib/TestJsonInclude_2.ftl">

    },
    "List": [ <#assign x = xpath.readList("//list/text()")><#list x as value>"${value}" <#if value_has_next>,</#if></#list> ]
"}