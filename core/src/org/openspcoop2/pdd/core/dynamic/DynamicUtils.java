/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

package org.openspcoop2.pdd.core.dynamic;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.utils.DynamicStringReplace;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.TemplateUtils;
import org.openspcoop2.utils.resources.VelocityTemplateUtils;
import org.slf4j.Logger;
import org.w3c.dom.Element;

import freemarker.template.Template;

/**
 * DynamicUtils
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DynamicUtils {

	public static void fillDynamicMap(Logger log, Map<String, Object> dynamicMap, DynamicInfo dynamicInfo) {
		if(dynamicMap.containsKey(Costanti.MAP_DATE_OBJECT)==false) {
			dynamicMap.put(Costanti.MAP_DATE_OBJECT, DateManager.getDate());
		}
		
		if(dynamicInfo!=null && dynamicInfo.getPddContext()!=null && dynamicInfo.getPddContext().getContext()!=null) {
			if(dynamicMap.containsKey(Costanti.MAP_CTX_OBJECT)==false) {
				dynamicMap.put(Costanti.MAP_CTX_OBJECT, dynamicInfo.getPddContext().getContext());
			}
			if(dynamicMap.containsKey(Costanti.MAP_TRANSACTION_ID_OBJECT)==false) {
				if(dynamicInfo.getPddContext().containsKey(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE)) {
					String idTransazione = (String)dynamicInfo.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
					dynamicMap.put(Costanti.MAP_TRANSACTION_ID_OBJECT, idTransazione);
				}
			}
		}
		
		if(dynamicMap.containsKey(Costanti.MAP_BUSTA_OBJECT)==false && dynamicInfo!=null && dynamicInfo.getBusta()!=null) {
			dynamicMap.put(Costanti.MAP_BUSTA_OBJECT, dynamicInfo.getBusta());
		}
		if(dynamicMap.containsKey(Costanti.MAP_BUSTA_PROPERTY)==false && dynamicInfo!=null && 
				dynamicInfo.getBusta()!=null && dynamicInfo.getBusta().sizeProperties()>0) {
			Properties propertiesBusta = new Properties();
			String[] pNames = dynamicInfo.getBusta().getPropertiesNames();
			if(pNames!=null && pNames.length>0) {
				for (int j = 0; j < pNames.length; j++) {
					String pName = pNames[j];
					String pValue = dynamicInfo.getBusta().getProperty(pName);
					if(pValue!=null) {
						propertiesBusta.setProperty(pName, pValue);
					}
				}
			}
			if(!propertiesBusta.isEmpty()) {
				dynamicMap.put(Costanti.MAP_BUSTA_PROPERTY, propertiesBusta);
			}
		}
		
		if(dynamicMap.containsKey(Costanti.MAP_HEADER)==false && dynamicInfo!=null && 
				dynamicInfo.getTrasporto()!=null && !dynamicInfo.getTrasporto().isEmpty()) {
			dynamicMap.put(Costanti.MAP_HEADER, dynamicInfo.getTrasporto());
		}
		
		if(dynamicMap.containsKey(Costanti.MAP_QUERY_PARAMETER)==false && dynamicInfo!=null && 
				dynamicInfo.getQueryParameters()!=null && !dynamicInfo.getQueryParameters().isEmpty()) {
			dynamicMap.put(Costanti.MAP_QUERY_PARAMETER, dynamicInfo.getQueryParameters());
		}
		
		// questi sottostanti, non sono disponnibili sul connettore
		if(dynamicInfo!=null && dynamicInfo.getUrl()!=null) {
			URLRegExpExtractor urle = new URLRegExpExtractor(dynamicInfo.getUrl(), log);
			dynamicMap.put(Costanti.MAP_ELEMENT_URL_REGEXP, urle);
			dynamicMap.put(Costanti.MAP_ELEMENT_URL_REGEXP.toLowerCase(), urle);
		}
		if(dynamicInfo!=null && dynamicInfo.getXml()!=null) {
			PatternExtractor pe = new PatternExtractor(dynamicInfo.getXml(), log);
			dynamicMap.put(Costanti.MAP_ELEMENT_XML_XPATH, pe);
			dynamicMap.put(Costanti.MAP_ELEMENT_XML_XPATH.toLowerCase(), pe);
		}
		if(dynamicInfo!=null && dynamicInfo.getJson()!=null) {
			PatternExtractor pe = new PatternExtractor(dynamicInfo.getJson(), log);
			dynamicMap.put(Costanti.MAP_ELEMENT_JSON_PATH, pe);
			dynamicMap.put(Costanti.MAP_ELEMENT_JSON_PATH.toLowerCase(), pe);
		}
	}


	public static String convertDynamicPropertyValue(String name,String tmpParam,Map<String,Object> dynamicMap,PdDContext pddContext, boolean forceStartWithDollaro) throws DynamicException{
		
		String tmp = tmpParam;
		if(!forceStartWithDollaro) {
			// per retrocompatibilità nel connettore file gestisco entrambi
			while(tmp.contains("${")) {
				tmp = tmp.replace("${", "{");
			}
		}
		
		String transactionIdConstant = Costanti.MAP_TRANSACTION_ID;
		if(forceStartWithDollaro) {
			transactionIdConstant = "$"+transactionIdConstant;
		}
		if(tmp.contains(transactionIdConstant)){
			String idTransazione = (String)pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
			while(tmp.contains(transactionIdConstant)){
				tmp = tmp.replace(transactionIdConstant, idTransazione);
			}
		}
		
		boolean request = false;
		boolean response = true;
		
		// conversione url
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				false, false, true, 
				forceStartWithDollaro, request);
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				false, false, true, 
				forceStartWithDollaro, response);
		
		// conversione xpath
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				true, false, false, 
				forceStartWithDollaro, request);
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				true, false, false, 
				forceStartWithDollaro, response);
		
		// conversione jsonpath
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				false, true, false, 
				forceStartWithDollaro, request);
		tmp = convertDynamicPropertyContent(tmp, dynamicMap, 
				false, true, false, 
				forceStartWithDollaro, response);
		
		try{
			tmp = DynamicStringReplace.replace(tmp, dynamicMap, forceStartWithDollaro);
		}catch(Exception e){
			throw new DynamicException("Proprieta' '"+name+"' contiene un valore non corretto: "+e.getMessage(),e);
		}
		return tmp;
	}
	
	private static String convertDynamicPropertyContent(String tmp, Map<String,Object> dynamicMap, 
			boolean xml, boolean json, boolean url, 
			boolean forceStartWithDollaro, boolean response) throws DynamicException {
		
		String istruzione = Costanti.MAP_ELEMENT_XML_XPATH;
		String prefix = Costanti.MAP_ELEMENT_XML_XPATH_PREFIX;
		if(json) {
			istruzione = Costanti.MAP_ELEMENT_JSON_PATH;
			prefix = Costanti.MAP_ELEMENT_JSON_PATH_PREFIX;
		}
		else if(url) {
			istruzione = Costanti.MAP_ELEMENT_URL_REGEXP;
			prefix = Costanti.MAP_ELEMENT_URL_REGEXP_PREFIX;
		}
		if(forceStartWithDollaro) {
			prefix = "$"+prefix;
		}
		if(response) {
			istruzione = istruzione+Costanti.MAP_SUFFIX_RESPONSE;
			prefix = prefix.substring(0,prefix.length()-1);
			prefix = prefix + Costanti.MAP_SUFFIX_RESPONSE + ":";
		}
		
		String tmpLowerCase = tmp.toLowerCase();
		String prefixLowerCase = prefix.toLowerCase();
		
		if(tmpLowerCase.contains(prefixLowerCase)){
			int maxIteration = 100;
			while (maxIteration>0 && tmpLowerCase.contains(prefixLowerCase)) {
				int indexOfStart = tmpLowerCase.indexOf(prefixLowerCase);
				String pattern = tmp.substring(indexOfStart+prefix.length(),tmp.length());
				if(pattern.contains("}")==false) {
					throw new DynamicException("Trovata istruzione '"+istruzione+"' non correttamente formata (chiusura '}' non trovata)");
				}
				
				// cerco chiusura, all'interno ci potrebbero essere altre aperture di { per le regole xpath
				char [] patternChars = pattern.toCharArray();
				int numAperture = 0;
				int positionChiusura = -1;
				for (int i = 0; i < patternChars.length; i++) {
					if(patternChars[i] == '{') {
						numAperture++;
					}
					if(patternChars[i] == '}') {
						if(numAperture==0) {
							positionChiusura = i;
							break;
						}
						else {
							numAperture--;
						}
					}
				}
				if(positionChiusura<=0) {
					throw new DynamicException("Trovata istruzione '"+istruzione+"' non correttamente formata (chiusura '}' non trovata)");
				}
				
				pattern = pattern.substring(0,positionChiusura);
				
				String complete = tmp.substring(indexOfStart, positionChiusura+indexOfStart+prefix.length()+1);
				Object o = dynamicMap.get(istruzione);
				if(o==null) {
					throw new DynamicException("Trovata istruzione '"+istruzione+"' non utilizzabile in questo contesto");
				}
				String value = null;
				if(json || xml) {
					if( !(o instanceof PatternExtractor) ) {
						throw new DynamicException("Trovata istruzione '"+istruzione+"' non utilizzabile in questo contesto (extractor wrong class: "+o.getClass().getName()+")");
					}
					PatternExtractor patternExtractor = (PatternExtractor) o;
					value = patternExtractor.read(pattern);
				}
				else {
					if( !(o instanceof URLRegExpExtractor) ) {
						throw new DynamicException("Trovata istruzione '"+istruzione+"' non utilizzabile in questo contesto (extractor wrong class: "+o.getClass().getName()+")");
					}
					URLRegExpExtractor urlExtractor = (URLRegExpExtractor) o;
					value = urlExtractor.read(pattern);
				}
				if(value==null) {
					value = "";
				}
				tmp = tmp.replace(complete, value);
				tmpLowerCase = tmp.toLowerCase();
				maxIteration--;
			}
		}
		
		return tmp;
	}
	
	
	public static void convertFreeMarkerTemplate(String name, byte[] template, Map<String,Object> dynamicMap, OutputStream out) throws DynamicException {
		try {
			OutputStreamWriter oow = new OutputStreamWriter(out);
			_convertFreeMarkerTemplate(name, template, dynamicMap, oow);
			oow.flush();
			oow.close();
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	public static void convertFreeMarkerTemplate(String name, byte[] template, Map<String,Object> dynamicMap, Writer writer) throws DynamicException {
		_convertFreeMarkerTemplate(name, template, dynamicMap, writer);
	}
	private static void _convertFreeMarkerTemplate(String name, byte[] template, Map<String,Object> dynamicMap, Writer writer) throws DynamicException {
		try {
			Template templateFTL = TemplateUtils.buildTemplate(name, template);
			templateFTL.process(dynamicMap, writer);
			writer.flush();
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	
	
	public static void convertVelocityTemplate(String name, byte[] template, Map<String,Object> dynamicMap, OutputStream out) throws DynamicException {
		try {
			OutputStreamWriter oow = new OutputStreamWriter(out);
			_convertVelocityTemplate(name, template, dynamicMap, oow);
			oow.flush();
			oow.close();
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
	public static void convertVelocityTemplate(String name, byte[] template, Map<String,Object> dynamicMap, Writer writer) throws DynamicException {
		_convertVelocityTemplate(name, template, dynamicMap, writer);
	}
	private static void _convertVelocityTemplate(String name, byte[] template, Map<String,Object> dynamicMap, Writer writer) throws DynamicException {
		try {
			org.apache.velocity.Template templateVelocity = VelocityTemplateUtils.buildTemplate(name, template);
			templateVelocity.merge(VelocityTemplateUtils.toVelocityContext(dynamicMap), writer);
			writer.flush();
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
		
	
	
	public static void convertXSLTTemplate(String name, byte[] template, Element element, OutputStream out) throws DynamicException {
		try {
			Source xsltSource = new StreamSource(new ByteArrayInputStream(template));
			Source xmlSource = new DOMSource(element);
			Transformer trans = XMLUtils.getInstance().getTransformerFactory().newTransformer(xsltSource);
			trans.transform(xmlSource, new StreamResult(out));
			out.flush();
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
	}
}
