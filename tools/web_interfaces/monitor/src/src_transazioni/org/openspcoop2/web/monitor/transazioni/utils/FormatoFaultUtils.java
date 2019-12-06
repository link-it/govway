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
package org.openspcoop2.web.monitor.transazioni.utils;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.web.monitor.core.core.Utils;

/**
 * FormatoFaultUtils
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class FormatoFaultUtils {

	
	public static String getFaultPretty(String fault, String formatoFault){
		String toRet = null;
		if(fault !=null) {
			StringBuffer contenutoDocumentoStringBuffer = new StringBuffer();
			String errore = Utils.getTestoVisualizzabile(fault.getBytes(),contenutoDocumentoStringBuffer);
			if(errore!= null)
				return "";

			MessageType messageType= MessageType.XML;
			if(StringUtils.isNotEmpty(formatoFault)) {
				messageType = MessageType.valueOf(formatoFault);
			}

			switch (messageType) {
			case BINARY:
			case MIME_MULTIPART:
				// questi due casi dovrebbero essere gestiti sopra 
				break;	
			case JSON:
				JSONUtils jsonUtils = JSONUtils.getInstance(true);
				try {
					toRet = jsonUtils.toString(jsonUtils.getAsNode(fault));
				} catch (UtilsException e) {
				}
				break;
			case SOAP_11:
			case SOAP_12:
			case XML:
			default:
				toRet = Utils.prettifyXml(fault);
				break;
			}
		}

		if(toRet == null)
			toRet = fault != null ? fault : "";

		return toRet;
	}

	public static boolean isVisualizzaFault(String fault){
		boolean visualizzaMessaggio = true;

		if(fault == null)
			return false;

		StringBuffer contenutoDocumentoStringBuffer = new StringBuffer();
		String errore = Utils.getTestoVisualizzabile(fault.getBytes(),contenutoDocumentoStringBuffer);
		if(errore!= null)
			return false;

		return visualizzaMessaggio;
	}

	public static String getBrushFault(String fault, String formatoFault) {
		String toRet = null;
		if(fault!=null) {
			MessageType messageType= MessageType.XML;
			if(StringUtils.isNotEmpty(formatoFault)) {
				messageType = MessageType.valueOf(formatoFault);
			}

			switch (messageType) {
			case JSON:
				toRet = "json";
				break;
			case BINARY:
			case MIME_MULTIPART:
				// per ora restituisco il default
			case SOAP_11:
			case SOAP_12:
			case XML:
			default:
				toRet = "xml";
				break;
			}
		}

		return toRet;
	}

	public static String getErroreVisualizzaFault(String fault){
		if(fault!=null) {
			StringBuffer contenutoDocumentoStringBuffer = new StringBuffer();
			String errore = Utils.getTestoVisualizzabile(fault.getBytes(),contenutoDocumentoStringBuffer);
			return errore;
		}

		return null;
	}
	
}
