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

package org.openspcoop2.web.monitor.transazioni.utils;

//import java.util.Iterator;
import java.util.List;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
//import org.openspcoop2.message.utils.DumpAttachment;
import org.openspcoop2.message.utils.DumpMessaggio;
import org.openspcoop2.message.utils.DumpMessaggioConfig;
import org.openspcoop2.pdd.logger.Dump;
import org.openspcoop2.protocol.sdk.dump.Messaggio;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.utils.MimeTypeUtils;
import org.slf4j.Logger;

/**
 * DumpMessaggioUtils
 *
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DumpMessaggioUtils {


	public static DumpMessaggio getFromBytes(byte[] content, String contentType) throws Exception {
		if(ContentTypeUtilities.isMultipart(contentType)) {

			//			MessageType messageType = MessageType.SOAP_11;
			// test
			MessageType messageType = null;
			if(messageType==null) {
				String contentTypeInternal = ContentTypeUtilities.getInternalMultipartContentType(contentType);
				if(HttpConstants.CONTENT_TYPE_SOAP_1_1.equalsIgnoreCase(contentTypeInternal) || contentTypeInternal.toLowerCase().startsWith(HttpConstants.CONTENT_TYPE_SOAP_1_1)) {
					messageType = MessageType.SOAP_11;
				}
				else if(HttpConstants.CONTENT_TYPE_SOAP_1_2.equalsIgnoreCase(contentTypeInternal) || contentTypeInternal.toLowerCase().startsWith(HttpConstants.CONTENT_TYPE_SOAP_1_2)) {
					messageType = MessageType.SOAP_12;
				}
				else {
					messageType = MessageType.MIME_MULTIPART;
				}
//				System.out.println("CALCOLATO: "+messageType);
			}

			OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			OpenSPCoop2MessageParseResult pr = factory.createMessage(messageType, MessageRole.NONE, contentType, content, null, null); 	
			OpenSPCoop2Message msg = pr.getMessage_throwParseException();

			Messaggio messaggio = new Messaggio();
			messaggio.setFormatoMessaggio(messageType);

			DumpMessaggioConfig dumpMessaggioConfig = new DumpMessaggioConfig();
			DumpMessaggio dumpMessaggio = Dump.fillMessaggio(msg, dumpMessaggioConfig, true, true, true, messaggio);

			// Envelope
//			System.out.println("=======");

//			if(dumpMessaggio.getMultipartInfoBody()!=null) {
//				System.out.append("\n*** MimePart Header ***\n");
//				if(dumpMessaggio.getMultipartInfoBody().getContentId()!=null) {
//					System.out.append("- "+HttpConstants.CONTENT_ID+": "+dumpMessaggio.getMultipartInfoBody().getContentId()+"\n");
//				}
//				if(dumpMessaggio.getMultipartInfoBody().getContentLocation()!=null) {
//					System.out.append("- "+HttpConstants.CONTENT_LOCATION+": "+dumpMessaggio.getMultipartInfoBody().getContentLocation()+"\n");
//				}
//				if(dumpMessaggio.getMultipartInfoBody().getContentType()!=null) {
//					System.out.append("- "+HttpConstants.CONTENT_TYPE+": "+dumpMessaggio.getMultipartInfoBody().getContentType()+"\n");
//				}
//				if(dumpMessaggio.getMultipartInfoBody().getHeadersValues()!=null &&
//						dumpMessaggio.getMultipartInfoBody().getHeadersValues().size()>0) {
//					Iterator<String> itM = dumpMessaggio.getMultipartInfoBody().getHeadersValues().keySet().iterator();
//					while(itM.hasNext()) {
//						String key = itM.next();
//						if(HttpConstants.CONTENT_ID.equalsIgnoreCase(key) ||
//								HttpConstants.CONTENT_LOCATION.equalsIgnoreCase(key) ||
//								HttpConstants.CONTENT_TYPE.equalsIgnoreCase(key)) {
//							continue;
//						}
//						List<String> values = dumpMessaggio.getMultipartInfoBody().getHeadersValues().get(key);
//						if(values!=null && !values.isEmpty()) {
//							for (String value : values) {
//								System.out.append("- "+key+": "+value+"\n");
//							}
//						}
//					}
//				}
//			}

//			System.out.append("\n");

//			if(dumpMessaggio.getBodyLength()>0) {
//				System.out.append(dumpMessaggio.getBodyAsString());
//			}


//			if(dumpMessaggio.getAttachments()!=null && dumpMessaggio.getAttachments().size()>0){
//				java.util.Iterator<DumpAttachment> it = dumpMessaggio.getAttachments().iterator();
//				int index = 1;
//				while(it.hasNext()){
//					DumpAttachment ap = it.next();
//					System.out.append("\n------ Attach-"+(index)+" ------\n");
//
//					System.out.append("\n*** MimePart Header ***\n");
//					if(ap.getContentId()!=null) {
//						System.out.append("- "+HttpConstants.CONTENT_ID+": "+ap.getContentId()+"\n");
//					}
//					if(ap.getContentLocation()!=null) {
//						System.out.append("- "+HttpConstants.CONTENT_LOCATION+": "+ap.getContentLocation()+"\n");
//					}
//					if(ap.getContentType()!=null) {
//						System.out.append("- "+HttpConstants.CONTENT_TYPE+": "+ap.getContentType()+"\n");
//					}
//					if(ap.getHeadersValues()!=null && ap.getHeadersValues().size()>0) {
//						Iterator<String> itM = ap.getHeadersValues().keySet().iterator();
//						while(itM.hasNext()) {
//							String key = itM.next();
//							if(HttpConstants.CONTENT_ID.equalsIgnoreCase(key) ||
//									HttpConstants.CONTENT_LOCATION.equalsIgnoreCase(key) ||
//									HttpConstants.CONTENT_TYPE.equalsIgnoreCase(key)) {
//								continue;
//							}
//							List<String> values = ap.getHeadersValues().get(key);
//							if(values!=null && !values.isEmpty()) {
//								for (String value : values) {
//									System.out.append("- "+key+": "+value+"\n");
//								}
//							}
//						}
//					}
//					System.out.append("\n");
//
//					if(ap.getErrorContentNotSerializable()!=null) {
//						System.out.append(ap.getErrorContentNotSerializable());
//					}
//					else {
//						System.out.append(ap.getContentAsString(true));
//					}
//
//					index++;
//				}
//			}

			return dumpMessaggio;
		}

		return null;
	}
	
	
	public static byte[] decodeAllegatoBase64(byte[] contenuto, String contentType, String contentId, Logger log){
		try{
			PddMonitorProperties prop = PddMonitorProperties.getInstance(log);
			boolean isTransazioniAllegatiDecodeBase64 = prop.isTransazioniAllegatiDecodeBase64();
			List<String> isTransazioniAllegatiDecodeBase64_noDecodeList = prop.getTransazioniAllegatiDecodeBase64_noDecodeList();
			
			String mimeTypeBase = MimeTypeUtils.getBaseType(contentType);
			boolean checkBase64 = isTransazioniAllegatiDecodeBase64 &&
					mimeTypeBase!=null &&
					!isTransazioniAllegatiDecodeBase64_noDecodeList.contains(mimeTypeBase);
			if(checkBase64){
				if(MimeTypeUtils.isBase64(contenuto)){
					log.debug("Decode Base64 Content ["+contentId+"] ...");
					return org.apache.commons.codec.binary.Base64.decodeBase64(contenuto);
				}
			}
		}catch(Exception e){
			log.error("IsBase64 error: "+e.getMessage(),e);
		}
		return contenuto;
	}
	
	public static boolean isAllegatoBase64(byte[] contenuto, String contentType, String contentId, Logger log){
		try{
			PddMonitorProperties prop = PddMonitorProperties.getInstance(log);
			boolean isTransazioniAllegatiDecodeBase64 = prop.isTransazioniAllegatiDecodeBase64();
			List<String> isTransazioniAllegatiDecodeBase64_noDecodeList = prop.getTransazioniAllegatiDecodeBase64_noDecodeList();
			
			String mimeTypeBase = MimeTypeUtils.getBaseType(contentType);
			boolean checkBase64 = isTransazioniAllegatiDecodeBase64 &&
					mimeTypeBase!=null &&
					!isTransazioniAllegatiDecodeBase64_noDecodeList.contains(mimeTypeBase);
			if(checkBase64){
				return MimeTypeUtils.isBase64(contenuto);
			}
		}catch(Exception e){
			log.error("IsBase64 error: "+e.getMessage(),e);
		}
		return false;
	}
}
