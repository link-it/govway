/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.protocol.sdi.utils;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.XMLUtils;
import org.openspcoop2.utils.xml.XPathExpressionEngine;
import org.openspcoop2.utils.xml.XPathNotFoundException;
import org.openspcoop2.utils.xml.XPathReturnType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * SDIUtils
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SDILottoUtils {
	
	public static List<byte[]> splitLotto(byte[] lotto) throws Exception {
		
		List<byte[]> risultato = new ArrayList<byte[]>();
		
		AbstractXMLUtils xmlUtils = XMLUtils.getInstance();
		XPathExpressionEngine xpathEngine = new XPathExpressionEngine();
		
		Element lottoElement = null;
		DynamicNamespaceContext dnc = null;
		try{
			lottoElement = xmlUtils.newElement(lotto);
			dnc = new DynamicNamespaceContext();
			dnc.findPrefixNamespace(lottoElement);
		}catch(Exception e){
			throw new Exception("Lettura lotto di fattura non riuscita: "+e.getMessage(),e);
		}
		
		NodeList nl = null;
		try{
			nl = (NodeList) xpathEngine.getMatchPattern(lottoElement, dnc, "//FatturaElettronicaBody", XPathReturnType.NODESET);
		}catch(XPathNotFoundException notFound){
			throw new Exception("La fattura non contiene alcun body?: "+notFound.getMessage(),notFound);
		}catch(Exception e){
			throw new Exception("Estrazione body dal lotto di fattura non riuscita: "+e.getMessage(),e);
		}
		if(nl==null){
			throw new Exception("La fattura non contiene alcun body? (null)");
		}
		if(nl.getLength()<1){
			throw new Exception("La fattura non contiene alcun body? (size): "+nl.getLength());
		}
		
		if(nl.getLength()==1){
			// non si tratta di un lotto, ma di una fattura con un body solamente
			risultato.add(lotto);
			return risultato;
		}
		
		List<Node> listBody = new ArrayList<Node>();
		try{
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				listBody.add(n);
				lottoElement.removeChild(n);
			}
		}catch(Exception e){
			throw new Exception("Elaborazione lotto di fattura (step1) non riuscita: "+e.getMessage(),e);
		}
		int i = 0;
		try{
			for (; i < listBody.size(); i++) {
				Node n = listBody.get(i);
				lottoElement.appendChild(n);
				risultato.add(xmlUtils.toByteArray(lottoElement));
				lottoElement.removeChild(n);
			}
		}catch(Exception e){
			throw new Exception("Elaborazione lotto di fattura (step2-"+i+") non riuscita: "+e.getMessage(),e);
		}
		
		return risultato;
	}
	
}
