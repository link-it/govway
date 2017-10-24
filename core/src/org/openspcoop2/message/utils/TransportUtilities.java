/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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

package org.openspcoop2.message.utils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.openspcoop2.message.ForwardConfig;
import org.openspcoop2.message.OpenSPCoop2MessageProperties;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * TransportUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13353 $, $Date: 2017-10-11 14:15:51 +0200 (Wed, 11 Oct 2017) $
 */
public class TransportUtilities {
	
	public static void initializeTransportHeaders(OpenSPCoop2MessageProperties op2MessageProperties, MessageRole messageRole, 
			Properties transportHeaders, ForwardConfig forwardConfig) throws MessageException{
		
		initializeHeaders(true,op2MessageProperties, messageRole, transportHeaders, forwardConfig);
		
	}
	
	public static void initializeForwardUrlParameters(OpenSPCoop2MessageProperties op2MessageProperties, MessageRole messageRole, 
			Properties forwardUrlParameters, ForwardConfig forwardConfig) throws MessageException{
		
		initializeHeaders(false,op2MessageProperties, messageRole, forwardUrlParameters, forwardConfig);
		
	}
	
	private static boolean match(String tipoOp, String key, List<String> listParam) {
		String keyLowerCase = key.toLowerCase();
		List<String> listHeaderLowerCase = new ArrayList<>();
		for (String hdr : listParam) {
			listHeaderLowerCase.add(hdr.toLowerCase()); // controllo in case insensitive mode
		}
		if(listHeaderLowerCase.contains(keyLowerCase)) {
			//System.out.println(tipoOp+" A");
			return true;
		}
		// check eventuali istruzioni con '*'
		for (String hdr : listHeaderLowerCase) {
			if(hdr.equals("*")) {
				// filtro tutti
				//System.out.println(tipoOp+" B");
				return true;
			}else if(hdr.endsWith("*")) {
				String keyStart = hdr.substring(0, hdr.length()-1);
				if(keyLowerCase.startsWith(keyStart)) {
					//System.out.println(tipoOp+" C");
					return true;
				}
			}
		}
		return false;
	}
		
	private static void initializeHeaders(boolean trasporto, OpenSPCoop2MessageProperties op2MessageProperties, MessageRole messageRole, 
			Properties applicativeInfo, ForwardConfig forwardConfig) throws MessageException{
		
		try{
//			String tipo = "Header";
//			if(trasporto==false){
//				tipo = "URLParameter";
//			}
			
			//System.out.println(tipo+" =============================== ["+messageRole+"]");
			
			if(applicativeInfo!=null && applicativeInfo.size()>0){
				Enumeration<?> enumHeader = applicativeInfo.keys();
				while (enumHeader.hasMoreElements()) {
					String key = (String) enumHeader.nextElement();
					if(MessageRole.REQUEST.equals(messageRole)==false){
						if(trasporto && HttpConstants.RETURN_CODE.equalsIgnoreCase(key)){
							continue;
						}
					}
					boolean add = true;
					boolean white = false;
					if(forwardConfig.getWhiteList()!=null && forwardConfig.getWhiteList().size()>0) {
						white = match("whiteList", key, forwardConfig.getWhiteList());
					}
					if(!white && forwardConfig.getBlackList()!=null && forwardConfig.getBlackList().size()>0) {
						if(match("blackList", key, forwardConfig.getBlackList())) {
							add = false;
						}
					}
					if( add ){
						//System.out.println("ADD ["+key+"] ["+applicativeInfo.getProperty(key)+"]");
						op2MessageProperties.addProperty(key, applicativeInfo.getProperty(key));
					}
//					else {
//						System.out.println("FILTRO ["+key+"]");
//					}
				}
			}
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
		
	}
}
