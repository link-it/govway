/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

	public static EsitiProperties getEsitiPropertiesForConfiguration(Logger log) throws ProtocolException {
		return EsitiProperties.getInstance(log, EsitiProperties.NO_PROTOCOL_CONFIG);
	}
	public static EsitiProperties getEsitiPropertiesForContext(Logger log) throws ProtocolException {
		return EsitiProperties.getInstance(log, EsitiProperties.NO_PROTOCOL_CONFIG);
	}
	
	public static List<String> getRegistrazioneEsiti(String esitiConfig, Logger log, StringBuffer bf) throws Exception{
		if(esitiConfig==null || "".equals(esitiConfig.trim())){
			
			// creo un default composto da tutti ad eccezione dell'esito (MaxThreads)
			EsitiProperties esiti = getEsitiPropertiesForConfiguration(log);
			List<Integer> esitiCodes = esiti.getEsitiCode();
			
			if(esitiCodes!=null && esitiCodes.size()>0){
				List<String> esitiDaRegistrare = new ArrayList<String>();
				for (Integer esito : esitiCodes) {
					int esitoMaxThreads = esiti.convertNameToCode(EsitoTransazioneName.CONTROLLO_TRAFFICO_MAX_THREADS.name());
					if(esito!=esitoMaxThreads){
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
