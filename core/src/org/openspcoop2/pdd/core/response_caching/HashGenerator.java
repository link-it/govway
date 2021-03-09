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

package org.openspcoop2.pdd.core.response_caching;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneHashGenerator;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaCacheDigestQueryParameter;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.transport.TransportUtils;

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
		
		ResponseCachingConfigurazioneHashGenerator configHash = responseCachingConfig.getHashGenerator();
		if(configHash==null) {
			configHash = new ResponseCachingConfigurazioneHashGenerator(); // utilizzo i valori di default
		}
		
		if(configHash!=null) {
			
			if(StatoFunzionalita.ABILITATO.equals(configHash.getRequestUri())) {
				
				// I parametri vengono riordinati proprio per far si differenze nell'ordine non impattano nel digest
				
				sb = new StringBuilder();
				sb.append("requestType").append("=").append(requestInfo.getProtocolContext().getRequestType());
				sb.append("\nrequestURI").append("=").append(requestInfo.getProtocolContext().getRequestURI());
				digest.update(sb.toString().getBytes());
				//System.out.println("TESTb: "+sb.toString());
				
			}
			
			if(StatoFunzionalitaCacheDigestQueryParameter.ABILITATO.equals(configHash.getQueryParameters()) ||
					StatoFunzionalitaCacheDigestQueryParameter.SELEZIONE_PUNTUALE.equals(configHash.getQueryParameters())) {
				
				// I parametri vengono riordinati proprio per far si differenze nell'ordine non impattano nel digest
				
				sb = new StringBuilder();
				sb.append("ParametriURL");
				if(StatoFunzionalitaCacheDigestQueryParameter.ABILITATO.equals(configHash.getQueryParameters())) {
					this.addList(requestInfo.getProtocolContext().getParameters(), false, sb);
				}
				else {
					Map<String, List<String>> pUrlForDigest = new HashMap<String, List<String>>();
					if(requestInfo.getProtocolContext().getParameters()!=null && configHash.sizeQueryParameterList()>0) {
						for (String queryParameter : configHash.getQueryParameterList()) {
							List<String> v = requestInfo.getProtocolContext().getParameterValues(queryParameter);
							if(v!=null && !v.isEmpty()) {
								pUrlForDigest.put(queryParameter, v);
							}
						}
					}
					
					if(!pUrlForDigest.isEmpty()) {
						this.addList(pUrlForDigest, false, sb);	
					}
				}
				digest.update(sb.toString().getBytes());
				//System.out.println("TESTb: "+sb.toString());
				
			}
			
			if(StatoFunzionalita.ABILITATO.equals(configHash.getHeaders())) {
				
				// Gli header vengono riordinati e le chiavi vengono prese lowerCase proprio per far si che tali differenze non impattano nel digest
				
				Map<String, List<String>> pTrasportoForDigest = new HashMap<String, List<String>>();
				if(requestInfo.getProtocolContext().getHeaders()!=null && configHash.sizeHeaderList()>0) {
					for (String header : configHash.getHeaderList()) {
						List<String> v = requestInfo.getProtocolContext().getHeaderValues(header);
						if(v!=null && !v.isEmpty()) {
							pTrasportoForDigest.put(header, v);
						}
					}
				}
				
				if(!pTrasportoForDigest.isEmpty()) {
					sb = new StringBuilder("HEADER");
					this.addList(pTrasportoForDigest, true, sb);
					digest.update(sb.toString().getBytes());
					//System.out.println("TESTb: "+sb.toString());
				}
				
			}
			
			if(StatoFunzionalita.ABILITATO.equals(configHash.getPayload())) {

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
	
	private void addList(Map<String, List<String>> p, boolean toLowerCase, StringBuilder sb) {
		if(p!=null &&
				!p.isEmpty()) {
			List<String> sortKeys = new ArrayList<>();
			Iterator<String> keys = p.keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				sortKeys.add(key);
			}
			Collections.sort(sortKeys);
			for (String sortKey : sortKeys) {
				List<String> values = TransportUtils.getRawObject(p, sortKey);
				List<String> ordinatedValues = new ArrayList<String>();
				ordinatedValues.addAll(values);
				if(ordinatedValues.size()>1) {
					Collections.sort(ordinatedValues);
				}
				String key = sortKey;
				if(toLowerCase) {
					key = key.toLowerCase();
				}
				if(ordinatedValues!=null && !ordinatedValues.isEmpty()) {
					for (String value : ordinatedValues) {
						sb.append("\n").append(key).append("=").append(value);		
					}
				}
			}

		}
	}
	
}
