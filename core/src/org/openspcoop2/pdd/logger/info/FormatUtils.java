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

package org.openspcoop2.pdd.logger.info;

import org.openspcoop2.utils.xml.PrettyPrintXMLUtils;
import org.openspcoop2.utils.xml.XMLUtils;
import org.slf4j.Logger;

/**
 * FormatUtils
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class FormatUtils {

	public static String getTestoVisualizzabile(Logger log, byte [] b,StringBuilder stringBuffer, boolean logError) {
		// 1024 = 1K
		// Visualizzo al massimo 250K
		int max = 250 * 1024;

		return getTestoVisualizzabile(log, b, stringBuffer, logError, max);
	}
	public static String getTestoVisualizzabile(Logger log, byte [] b,StringBuilder stringBuffer, boolean logError, int max) {
		try{

			//			 if(b.length>max){
			//				 return "Visualizzazione non riuscita: la dimensione supera 250K";
			//			 }
			//
			//			 for (int i = 0; i < b.length; i++) {
			//				 if(!Utilities.isPrintableChar((char)b[i])){
			//
			//					 return "Visualizzazione non riuscita: il documento contiene caratteri non visualizzabili";
			//				 }
			//			 }
			stringBuffer.append(org.openspcoop2.utils.Utilities.convertToPrintableText(b, max));
			return null;

		}catch(Exception e){
			if(logError) {
				log.error("getTestoVisualizzabile error", e);
			}
			else {
				log.debug("getTestoVisualizzabile error", e);
			}
			return e.getMessage();
		}

	}

	public static String prettifyXml(Logger log, String xml) {
		if (xml == null || "".equals(xml))
			return "";
		try {
			return PrettyPrintXMLUtils.prettyPrintWithTrAX(XMLUtils.getInstance().newDocument(xml.getBytes()));
		} catch (Exception e) {
			// non sono riuscito a formattare il messaggio
			log.error(e.getMessage(),e);
		}
		return xml;
	}
	public static String prettifyXml(Logger log, byte[] xml) {
		if (xml == null)
			return "";
		String res = "";
		try {
			return PrettyPrintXMLUtils.prettyPrintWithTrAX(XMLUtils.getInstance().newDocument(xml));
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return res;
	} 

}
