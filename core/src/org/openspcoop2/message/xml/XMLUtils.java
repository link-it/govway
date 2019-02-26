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
 * @author $Author$
 * @version $Rev$, $Date$
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
			return (DocumentBuilderFactory) Loader.getInstance().newInstance(OpenSPCoop2MessageFactory.getMessageFactory().getDocumentBuilderFactoryClass());
		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}
	}

}
