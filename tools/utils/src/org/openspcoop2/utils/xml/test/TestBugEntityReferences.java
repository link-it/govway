/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.utils.xml.test;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.xml.AbstractXPathExpressionEngine;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.w3c.dom.Element;


/**
 * TestBugEntityReferences
 * Il test verifica che se nel xml sono presenti entity referency queste rimangano tali una volta estratte tramite istruzioni xpath e non siano risolte
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestBugEntityReferences {

	// Character   |   Entity reference  |   Numeric reference   |   Hexadecimal reference
	//    &        |         &amp;       |         &#38;         |          &#x26;
	//    <        |         &lt;        |         &#60;         |          &#x3C;
	//    >        |         &gt;        |         &#62;         |          &#x3E;
	//    "        |         &quot;      |         &#34;         |          &#x22;
	//    '        |         &apos;      |         &#39;         |          &#x27;
	
	public static void main(String[] args) throws Exception {
		test();
	}
	
	public static void test() throws Exception {
    
    	String template = "REFERENCE";
    	String valoreReferenceConTemplate = "Valore con reference: "+template+" .\nTerminata altra informazione\n\nFine";
    	String attributoReferenceConTemplate = "Attributo con reference: "+template+" .";
    	
    	String xml_riga1 = "<ns:elemento>"+valoreReferenceConTemplate+"</ns:elemento>";
    	String xml_riga2 = "<ns:attributo attr1=\"val1\" attrRef=\""+attributoReferenceConTemplate+"\">"+valoreReferenceConTemplate+"</ns:attributo>";
    	String xml = "<ns:prova xmlns:ns=\"http://prova\"><ns:contenutoInterno>\n"+
    			xml_riga1+"\n"+
    			xml_riga2+"\n"+
    			"</ns:contenutoInterno></ns:prova>";
    	String xmlMultiLine = "<ns:prova xmlns:ns=\"http://prova\"><ns:contenutoInterno>\n"+
    			xml_riga1+"\n"+
    			xml_riga1+"\n"+
    			xml_riga1+"\n"+
    			"</ns:contenutoInterno></ns:prova>";

    	List<String> listTest = new ArrayList<String>();
    	listTest.add("&");
    	listTest.add("<");
    	listTest.add(">");
    	listTest.add("\"");
    	listTest.add("'");
    	listTest.add("&<>\"'");
    	
    	List<String> listReferences = new ArrayList<String>();
    	listReferences.add("(&amp; , &#38; , &#x26;)");
    	listReferences.add("(&lt; , &#60; , &#x3C;)");
    	listReferences.add("(&gt; , &#62; , &#x3E;)");
    	listReferences.add("(&quot; , &#34; , &#x22;)");
    	listReferences.add("(&apos; , &#39; , &#x27;)");
    	listReferences.add("(&amp; , &lt; , &gt; , &quot; , &apos;)");
    	
    	List<String> listReferencesAttese = new ArrayList<String>();
    	listReferencesAttese.add("(&amp; , &amp; , &amp;)");
    	listReferencesAttese.add("(&lt; , &lt; , &lt;)");
    	listReferencesAttese.add("(&gt; , &gt; , &gt;)");
    	listReferencesAttese.add("(\" , \" , \")"); // vengono risolte
    	listReferencesAttese.add("(' , ' , ')"); // vengono risolte
    	listReferencesAttese.add("(&amp; , &lt; , &gt; , \" , ')");
		
    	for (int i = 0; i < listTest.size(); i++) {
			String test = listTest.get(i);
			String reference = listReferences.get(i);
			String referenceAttesa = listReferencesAttese.get(i);

			System.out.println("\n\n======== ["+test+"] ========");
			String pattern = "/ns:prova/*";
			String xmlConReference = xml.replace(template, reference);
			String xml_riga1_conReference_attesa = xml_riga1.replace(template, referenceAttesa);
			String xml_riga2_conReference_attesa = xml_riga2.replace(template, referenceAttesa);
			verify(xmlConReference, pattern,
					xml_riga1_conReference_attesa, xml_riga2_conReference_attesa);
			
			pattern = "//ns:attributo/@attrRef";
			String attributo_conReference_attesa = attributoReferenceConTemplate.replace(template, referenceAttesa);
			verify(xmlConReference, pattern,
					attributo_conReference_attesa);
			
			pattern = "//ns:elemento/text()";
			String valore_conReference_attesa_risolta = valoreReferenceConTemplate.replace(template, referenceAttesa);
			verify(xmlConReference, pattern,
					valore_conReference_attesa_risolta);
			
			System.out.println("-- MultiLine");
			
			pattern = "//ns:elemento/text()";
			String xmlMultiLineConReference = xmlMultiLine.replace(template, reference);
			verify(xmlMultiLineConReference, pattern,
					"[0]="+valore_conReference_attesa_risolta+", [1]="+valore_conReference_attesa_risolta+", [2]="+valore_conReference_attesa_risolta);
			
			pattern = "//ns:contenutoInterno/*";
			xmlMultiLineConReference = xmlMultiLine.replace(template, reference);
			verify(xmlMultiLineConReference, pattern,
					xml_riga1_conReference_attesa+xml_riga1_conReference_attesa+xml_riga1_conReference_attesa);
    	}
    }
    
    private static void verify(String xml, String pattern, String ... verifica) throws Exception {
    	
    	//System.out.println("PROVA ["+xml+"]");
    	
    	Element element = org.openspcoop2.utils.xml.XMLUtils.getInstance().newElement(xml.getBytes());
    	AbstractXPathExpressionEngine xPathEngine = new org.openspcoop2.utils.xml.XPathExpressionEngine();
		DynamicNamespaceContext dnc = new DynamicNamespaceContext();
		dnc.findPrefixNamespace(element);
		String xmlValue = AbstractXPathExpressionEngine.extractAndConvertResultAsString(element, dnc, xPathEngine, pattern, LoggerWrapperFactory.getLogger(TestBugEntityReferences.class));
		//System.out.println("Valore Estratto: ["+xmlValue+"]");
		for (int i = 0; i < verifica.length; i++) {
			String verificaS = verifica[i];
			if(xmlValue.contains(verificaS)==false) {
				throw new Exception("Pattern '"+pattern+"' failed: stringa '"+verificaS+"' attesa non riscontrata nel valore estratto ["+xmlValue+"]\n\nXml: "+xml);
			}
		}
		System.out.println("Pattern '"+pattern+"' ok"); 
    }
}
