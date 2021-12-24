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

package org.openspcoop2.message.xml;

import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;

import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.xml.XMLException;

/**
 * XMLUtils
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XMLUtils extends org.openspcoop2.utils.xml.XMLUtils {

	private static HashMap<String, XMLUtils> xmlUtilsMap = new HashMap<>();
	private static synchronized void init(OpenSPCoop2MessageFactory messageFactory){
		String key = messageFactory.getClass().getName();
		if(!XMLUtils.xmlUtilsMap.containsKey(key)){
			XMLUtils xmlUtils = new XMLUtils(messageFactory);
			xmlUtilsMap.put(key, xmlUtils);
		}
	}
	public static XMLUtils getInstance(OpenSPCoop2MessageFactory messageFactory){
		String key = messageFactory.getClass().getName();
		if(!XMLUtils.xmlUtilsMap.containsKey(key)){
			XMLUtils.init(messageFactory);
		}
		return XMLUtils.xmlUtilsMap.get(key);
	}
	
	public static XMLUtils DEFAULT = XMLUtils.getInstance(OpenSPCoop2MessageFactory.getDefaultMessageFactory());
	
	private OpenSPCoop2MessageFactory messageFactory;
	
	public XMLUtils(OpenSPCoop2MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}
	
	@Override
	protected DocumentBuilderFactory newDocumentBuilderFactory() throws XMLException {
		try{
			return (DocumentBuilderFactory) Loader.getInstance().newInstance(this.messageFactory.getDocumentBuilderFactoryClass());
		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}
	}
	
	@Override
	protected SAXParserFactory newSAXParserFactory() throws XMLException {
		try{
			return (SAXParserFactory) Loader.getInstance().newInstance(this.messageFactory.getSAXParserFactoryClass());
		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}
	}

}
