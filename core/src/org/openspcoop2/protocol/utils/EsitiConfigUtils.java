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

package org.openspcoop2.protocol.utils;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.slf4j.Logger;

/**     
 * EsitiConfigUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EsitiConfigUtils {

	public static int TUTTI_ESITI_DISABILITATI = -2;
	
	public static EsitiProperties getEsitiPropertiesForConfiguration(Logger log) throws ProtocolException {
		return EsitiProperties.getInstance(log, EsitiProperties.NO_PROTOCOL_CONFIG);
	}
	public static EsitiProperties getEsitiPropertiesForContext(Logger log) throws ProtocolException {
		return EsitiProperties.getInstance(log, EsitiProperties.NO_PROTOCOL_CONFIG);
	}
	
	// esiti indipendenti dal protocollo. Li inizializzo una volta sola per questione di performance.
	
	private static int esitoMaxThreads = -1;
	private static int esitoCorsGateway = -1;
	private static int esitoCorsTrasparente = -1;
	private static void checkInitEsiti(EsitiProperties esiti) throws ProtocolException {
		if(esitoMaxThreads<0) {
			initEsiti(esiti);
		}
	}
	private static synchronized void initEsiti(EsitiProperties esiti) throws ProtocolException {
		if(esitoMaxThreads<0) {
			esitoMaxThreads = esiti.convertNameToCode(EsitoTransazioneName.CONTROLLO_TRAFFICO_MAX_THREADS.name());
			esitoCorsGateway = esiti.convertNameToCode(EsitoTransazioneName.CORS_PREFLIGHT_REQUEST_VIA_GATEWAY.name());
			esitoCorsTrasparente = esiti.convertNameToCode(EsitoTransazioneName.CORS_PREFLIGHT_REQUEST_TRASPARENTE.name());
		}
	}
	
	public static List<String> getRegistrazioneEsiti(String esitiConfig, Logger log, StringBuilder bf) throws Exception{
		return getRegistrazioneEsiti(esitiConfig, log, bf, getEsitiPropertiesForConfiguration(log));
	}
	public static List<String> getRegistrazioneEsiti(String esitiConfig, Logger log, StringBuilder bf, EsitiProperties esiti) throws Exception{
		if(esitiConfig==null || "".equals(esitiConfig.trim())){
			
			// creo un default composto da tutti ad eccezione dell'esito (MaxThreads) e delle richieste CORS OPTIONS
			List<Integer> esitiCodes = esiti.getEsitiCode();
			
			if(esitiCodes!=null && esitiCodes.size()>0){
				List<String> esitiDaRegistrare = new ArrayList<String>();
				for (Integer esito : esitiCodes) {
					checkInitEsiti(esiti);
					if(esito!=esitoMaxThreads && esito!=esitoCorsGateway && esito!=esitoCorsTrasparente){
						if(bf.length()>0){
							bf.append(",");
						}
						bf.append(esito);
						esitiDaRegistrare.add(esito+"");
					}
				}
				if(esitiDaRegistrare.size()>0){
					return esitiDaRegistrare;
				}
			}
			
			return null; // non dovrebbe succedere, degli esiti nell'EsitiProperties dovrebbero esistere
		}
		else{
			
			String [] tmp = esitiConfig.split(",");
			if(tmp!=null && tmp.length>0){
				List<String> esitiDaRegistrare = new ArrayList<String>();
				for (int i = 0; i < tmp.length; i++) {
					String t = tmp[i];
					if(t!=null){
						t = t.trim();
						if(!"".equals(t)){
							if(bf.length()>0){
								bf.append(",");
							}
							bf.append(t);
							esitiDaRegistrare.add(t);
						}
					}
				}
				if(esitiDaRegistrare.size()>0){
					return esitiDaRegistrare;
				}
			}
			
			return null; // non dovrebbe succedere, si rientra nel ramo then dell'if principale
		}
	}
	
}
