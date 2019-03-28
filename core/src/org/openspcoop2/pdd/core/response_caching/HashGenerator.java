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

package org.openspcoop2.pdd.core.response_caching;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.utils.io.Base64Utilities;

/**     
 * HashGenerator
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HashGenerator {

	private String algoritmo;
	
	public HashGenerator(String algoritmo) {
		// MD5, SHA-1, SHA-256
		this.algoritmo = algoritmo;
	}
	
	// NOTA:
	// La generazione dell'hash deve essere fatta all'inizio prima che il messaggio venga modificato.
	// L'hash deve essere calcolato, se il response caching è abilitato, subito dopo i vari controlli di auth e rateLimiting ma prima degli handler di out ed imbustamento
	// In modo che si calcola sulla richiesta in ingresso effettiva.
	
	// NOTA2: Mascherare con una doppia entry in cache per non ritornare hash
	
	public String buildKeyCache(OpenSPCoop2Message message, RequestInfo requestInfo, ResponseCachingConfigurazione responseCachingConfig) throws Exception {
		
		MessageDigest digest  = MessageDigest.getInstance(this.algoritmo);
		
		StringBuilder sb = new StringBuilder();
		sb.append("interfaceName").append("=").append(requestInfo.getProtocolContext().getInterfaceName());
		sb.append("\n").append("function").append("=").append(requestInfo.getProtocolContext().getFunction());
		boolean printAzione = true;
		sb.append("\n").append("idServizio").append("=").append(requestInfo.getIdServizio().toString(printAzione));
		digest.update(sb.toString().getBytes());
		//System.out.println("TESTa: "+sb.toString());
		
		if(responseCachingConfig.getHashGenerator()!=null) {
			
			if(StatoFunzionalita.ABILITATO.equals(responseCachingConfig.getHashGenerator().getRequestUri())) {
				
				// I parametri vengono riordinati proprio per far si differenze nell'ordine non impattano nel digest
				
				sb = new StringBuilder();
				sb.append("requestType").append("=").append(requestInfo.getProtocolContext().getRequestType());
				sb.append("\nrequestURI").append("=").append(requestInfo.getProtocolContext().getRequestURI());
				sb.append("\nParametriURL");
				this.addList(requestInfo.getProtocolContext().getParametersFormBased(), false, sb);
				digest.update(sb.toString().getBytes());
				//System.out.println("TESTb: "+sb.toString());
				
			}
			
			if(StatoFunzionalita.ABILITATO.equals(responseCachingConfig.getHashGenerator().getHeaders())) {
				
				// Gli header vengono riordinati e le chiavi vengono prese lowerCase proprio per far si che tali differenze non impattano nel digest
				
				Properties pTrasportoForDigest = new Properties();
				if(requestInfo.getProtocolContext().getParametersTrasporto()!=null && responseCachingConfig.getHashGenerator().sizeHeaderList()>0) {
					for (String header : responseCachingConfig.getHashGenerator().getHeaderList()) {
						String v = requestInfo.getProtocolContext().getParameterTrasporto(header);
						if(v!=null) {
							pTrasportoForDigest.put(header, v);
						}
					}
				}
				
				if(pTrasportoForDigest.isEmpty()) {
					sb = new StringBuilder("HEADER");
					this.addList(pTrasportoForDigest, true, sb);
					digest.update(sb.toString().getBytes());
					//System.out.println("TESTb: "+sb.toString());
				}
				
			}
			
			if(StatoFunzionalita.ABILITATO.equals(responseCachingConfig.getHashGenerator().getPayload())) {

				boolean doDigest = true;
				message.saveChanges();
				if(ServiceBinding.REST.equals(message.getServiceBinding())) {
					doDigest = message.castAsRest().hasContent();
				}
				
				if(doDigest) {
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					message.writeTo(bout, false);
					bout.flush();
					bout.close();
					digest.update(bout.toByteArray());
					//System.out.println("TESTd: "+bout.toString());
				}
			}
			
		}
		
		return Base64Utilities.encodeAsString(digest.digest());
	}
	
	private void addList(Properties p, boolean toLowerCase, StringBuilder sb) {
		if(p!=null &&
				!p.isEmpty()) {
			Enumeration<Object> en = p.keys();
			List<String> sortKeys = new ArrayList<>();
			while (en.hasMoreElements()) {
				Object object = (Object) en.nextElement();
				if(object!=null && object instanceof String) {
					String key = (String) object;
					sortKeys.add(key);
				}
			}
			Collections.sort(sortKeys);
			for (String sortKey : sortKeys) {
				String value = p.getProperty(sortKey);
				if(value==null) {
					value = p.getProperty(sortKey.toLowerCase());
				}
				if(value==null) {
					value = p.getProperty(sortKey.toUpperCase());
				}
				String key = sortKey;
				if(toLowerCase) {
					key = key.toLowerCase();
				}
				sb.append("\n").append(key).append("=").append(value);
			}

		}
	}
	
}
