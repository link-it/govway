/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.xml2json;

import java.util.List;

import org.codehaus.jettison.mapped.Configuration;
import org.openspcoop2.utils.xml.AbstractXPathExpressionEngine;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.XPathExpressionEngine;
import org.slf4j.Logger;

/**	
 * PathExpressionEngine
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JsonXmlPathExpressionEngine {

	
	public static String extractAndConvertResultAsString(String elementJson, String pattern, Logger log) throws Exception {
		Object o = _extractAndConvertResultAsString(elementJson, pattern, log, false);
		if(o!=null) {
			return (String) o;
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public static List<String> extractAndConvertResultAsList(String elementJson, String pattern, Logger log) throws Exception {
		Object o = _extractAndConvertResultAsString(elementJson, pattern, log, true);
		if(o!=null) {
			return (List<String>) o;
		}
		return null;
	}
	private static final String PREFIX = "xpath ";
	private static final String NAMESPACE_PREFIX = "namespace("; // namespace
	private static final String NAMESPACE_END = ")";
	private static Object _extractAndConvertResultAsString(String elementJson, String patternParam, Logger log, boolean returnAsList) throws Exception {
			
		IJson2Xml json2Xml = null;
		String pattern = patternParam;
		String tipo = "Mapped";
		if(patternParam!=null && patternParam.toLowerCase().startsWith(PREFIX.toLowerCase())) {
			// default
			json2Xml = Xml2JsonFactory.getJson2XmlMapped(new Configuration()); // gli altri tipi, jsonML e badgerFished richiedono una struttura json specifica.
			pattern = patternParam.substring(PREFIX.length());
			tipo = "Mapped";
		}
		if(pattern==null || "".equals(pattern)) {
			throw new Exception("Espressione da utilizzare non fornita");
		}
				
		if(json2Xml!=null) {
			String node = null;
			DynamicNamespaceContext dnc = new DynamicNamespaceContext();		
			try {
				// Wrappo in un unico elemento radice, altrimenti la conversione xml non riesce
				String elementJsonWrapped = "{ \"json2xml\" : "+elementJson+" }";				
				node = json2Xml.json2xml(elementJsonWrapped);
				if(node==null || "".equals(node)) {
					throw new Exception("xml element ottenuto è vuoto");
				}
				pattern = pattern.trim();
				String namespaces = null;
				StringBuilder sbNamespaceDeclarations = new StringBuilder("");
				if(pattern.toLowerCase().startsWith(NAMESPACE_PREFIX)) {
					pattern = pattern.substring(NAMESPACE_PREFIX.length());
					if(!pattern.contains(NAMESPACE_END)) {
						throw new Exception("Espressione in un formato non corretto; non è stata riscontrata la chiusura della definizione dei namespace");
					}
					int offSet = pattern.indexOf(NAMESPACE_END);
					namespaces = pattern.substring(0, offSet);
					if(offSet<(pattern.length()-1)) {
						pattern = pattern.substring(offSet+1, pattern.length());
						pattern = pattern.trim();
					}
					else {
						pattern = null;
					}
					
					if(pattern==null || "".equals(pattern)) {
						throw new Exception("Espressione da utilizzare non fornita dopo la dichiarazione dei namespace");
					}
					//System.out.println("NAMESPACE ["+namespaces+"]");
					//System.out.println("PATTERN ["+pattern+"]");
					
					String newNameXmlns = "___xmlns";
					boolean foundXmlns = false;
					boolean foundDeclXmlns = false;
					if(node.contains("<xmlns:")) {
						foundXmlns = true;
						node = node.replaceAll("<xmlns:", "<"+newNameXmlns+":");
						node = node.replaceAll("</xmlns:", "</"+newNameXmlns+":");
					}
					
					if(namespaces!=null && !"".equals(namespaces)) {
						
						String [] tmp = namespaces.split(",");
						if(tmp!=null && tmp.length>0) {
							for (String namespaceDeclaration : tmp) {
								if(namespaceDeclaration!=null && !"".equals(namespaceDeclaration)) {
									if(!namespaceDeclaration.contains(":")) {
										throw new Exception("Espressione da utilizzare non corretta; dichiarazione dei namespace in un formato non corretto: atteso ':' separator. ("+namespaces+") ("+namespaceDeclaration+")");
									}
									else {
										int indexOfPrefix = namespaceDeclaration.indexOf(":");
										String prefix = namespaceDeclaration.substring(0, indexOfPrefix);
										String uri = null;
										if(indexOfPrefix<(namespaceDeclaration.length()-1)) {
											uri = namespaceDeclaration.substring(indexOfPrefix+1, namespaceDeclaration.length());
										}
										else {
											throw new Exception("Espressione da utilizzare non corretta; dichiarazione dei namespace in un formato non corretto: attesa dichiarazione namespace. ("+namespaces+") ("+namespaceDeclaration+")");
										}
										prefix = prefix.trim();
										uri = uri.trim();
										if(prefix==null || "".equals(prefix)) {
											throw new Exception("Espressione da utilizzare non corretta; dichiarazione dei namespace in un formato non corretto: prefisso non presente. ("+namespaces+") ("+namespaceDeclaration+")");
										}
										if(uri==null || "".equals(uri)) {
											throw new Exception("Espressione da utilizzare non corretta; dichiarazione dei namespace in un formato non corretto: dichiarazione namespace non presente. ("+namespaces+") ("+namespaceDeclaration+")");
										}
										//System.out.println("prefix ["+prefix+"] uri["+uri+"]");
										if(prefix.equals("xmlns")) {
											prefix = newNameXmlns;
											foundDeclXmlns = true;
										}
										dnc.addNamespace(prefix, uri);
										sbNamespaceDeclarations.append(" xmlns:").append(prefix).append("=\"").append(uri).append("\" ");
									}
								}
							}
						}
					}
					
					if(foundXmlns && !foundDeclXmlns) {
						String prefix = newNameXmlns;
						String uri = "http://govway.org/utils/json2xml/xmlns";
						dnc.addNamespace(prefix, uri);
						sbNamespaceDeclarations.append(" xmlns:").append(prefix).append("=\"").append(uri).append("\" ");
					}
					
				}
				
				if(sbNamespaceDeclarations!=null && sbNamespaceDeclarations.length()>0) {
					node = node.replaceFirst("<json2xml>", ("<json2xml"+sbNamespaceDeclarations+">"));
				}
				
			}catch(Exception e) {
				throw new Exception("Trasformazione json2xml '"+tipo+"' fallita: "+e.getMessage(),e);
			}
			try {		
				// NOTA: e' importante invocarlo con il corretto metodo
				XPathExpressionEngine xPathEngine = new XPathExpressionEngine();
				if(returnAsList) {
					return AbstractXPathExpressionEngine.extractAndConvertResultAsList(node, dnc, xPathEngine, pattern, log);
				}
				else {
					return AbstractXPathExpressionEngine.extractAndConvertResultAsString(node, dnc, xPathEngine, pattern, log);
				}
			}catch(Exception e) {
				throw new Exception("Trasformazione json2xml '"+tipo+"' fallita: "+e.getMessage()+"\nXml: \n"+node,e);
			}
		}
		
		if(returnAsList) {
			return org.openspcoop2.utils.json.JsonPathExpressionEngine.extractAndConvertResultAsList(elementJson, pattern, log);
		}
		else {
			return org.openspcoop2.utils.json.JsonPathExpressionEngine.extractAndConvertResultAsString(elementJson, pattern, log);
		}
	}
}
