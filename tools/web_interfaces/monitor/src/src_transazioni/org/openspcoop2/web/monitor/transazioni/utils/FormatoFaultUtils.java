/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
import org.openspcoop2.pdd.logger.info.InfoEsitoTransazioneFormatUtils;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.web.monitor.core.core.Utils;
import org.slf4j.Logger;

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
			StringBuilder contenutoDocumentoStringBuilder = new StringBuilder();
			String errore = Utils.getTestoVisualizzabile(fault.getBytes(),contenutoDocumentoStringBuilder, true);
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
		Logger log = LoggerWrapperFactory.getLogger(FormatoFaultUtils.class);
		return InfoEsitoTransazioneFormatUtils.isVisualizzaFault(log, fault);
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
			StringBuilder contenutoDocumentoStringBuilder = new StringBuilder();
			String errore = Utils.getTestoVisualizzabile(fault.getBytes(),contenutoDocumentoStringBuilder, false);
			return errore;
		}

		return null;
	}
	
}
