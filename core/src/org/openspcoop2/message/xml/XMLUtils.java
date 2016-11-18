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

package org.openspcoop2.message.xml;

import javax.xml.parsers.DocumentBuilderFactory;

import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.xml.XMLException;

/**
 * XMLUtils
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12237 $, $Date: 2016-10-04 11:41:45 +0200 (Tue, 04 Oct 2016) $
 */
public class XMLUtils extends org.openspcoop2.utils.xml.XMLUtils {

	private static XMLUtils xmlUtils = null;
	private static synchronized void init(){
		if(XMLUtils.xmlUtils==null){
			XMLUtils.xmlUtils = new XMLUtils();
		}
	}
	public static XMLUtils getInstance(){
		if(XMLUtils.xmlUtils==null){
			XMLUtils.init();
		}
		return XMLUtils.xmlUtils;
	}
	
	@Override
	protected DocumentBuilderFactory newDocumentBuilderFactory() throws XMLException {
		try{
			return (DocumentBuilderFactory) Loader.getInstance().forName(OpenSPCoop2MessageFactory.getMessageFactory().getDocumentBuilderFactoryClass()).newInstance();
		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}
	}

}
