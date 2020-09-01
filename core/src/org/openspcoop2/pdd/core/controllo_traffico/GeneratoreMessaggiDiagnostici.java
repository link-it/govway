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

package org.openspcoop2.pdd.core.controllo_traffico;

import java.util.Hashtable;

import org.slf4j.Logger;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;

/**     
 * GeneratoreMessaggiDiagnostici
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GeneratoreMessaggiDiagnostici {

	
	public final static int NUMERO_TEMPLATE_DINAMICI_ERRORI_GENERICI = 4; // sono i diagnostici emessi dai moduli ricezione contenuti o buste
	public final static int NUMERO_TEMPLATE_DINAMICI_DETTAGLI_SINGOLO_ERRORE = 10; // sono i diagnostici di "appoggio" registrati nel modulo 'all' utilizzati per generare l'errore
	// NOTA: siccome tanto viene salvato direttamente il diagnostico emesso dai moduli ricezione contenuti o buste non sembra necessario salvare i singoli dettagli visto che sono già nell'errore generico
	
	public final static boolean saveSingleValueOfMessageError = false;
	
	public static void cleanPolicyValues(MsgDiagnostico msgDiag) throws Exception{
		
		Hashtable<String, String> keywords = msgDiag.getKeywordLogPersonalizzati();
		if(keywords==null || keywords.size()<=0){
			return;
		}
		keywords.remove(GeneratoreMessaggiErrore.TEMPLATE_POLICY_ACTIVE_ID);
		keywords.remove(GeneratoreMessaggiErrore.TEMPLATE_POLICY_VIOLATA_MOTIVO);
		keywords.remove(GeneratoreMessaggiErrore.TEMPLATE_POLICY_FILTRATA_MOTIVO);
		keywords.remove(GeneratoreMessaggiErrore.TEMPLATE_POLICY_NON_APPLICABILE_MOTIVO);
		if(saveSingleValueOfMessageError){
			keywords.remove(GeneratoreMessaggiErrore.TEMPLATE_POLICY_VALORE_SOGLIA);
			keywords.remove(GeneratoreMessaggiErrore.TEMPLATE_POLICY_TIPOLOGIA_TEMPO_MEDIO);
			keywords.remove(GeneratoreMessaggiErrore.TEMPLATE_POLICY_VALORE_RILEVATO);
			keywords.remove(GeneratoreMessaggiErrore.TEMPLATE_POLICY_AVG_TIME_RILEVATO);
			keywords.remove(GeneratoreMessaggiErrore.TEMPLATE_POLICY_VALORE_SOGLIA_DEGRADO_PRESTAZIONALE);
			keywords.remove(GeneratoreMessaggiErrore.TEMPLATE_POLICY_GRUPPO);
			keywords.remove(GeneratoreMessaggiErrore.TEMPLATE_POLICY_INTERVALLO_TEMPORALE);
			keywords.remove(GeneratoreMessaggiErrore.TEMPLATE_POLICY_NOME_ALLARME);
			keywords.remove(GeneratoreMessaggiErrore.TEMPLATE_POLICY_STATO_ALLARME);
			keywords.remove(GeneratoreMessaggiErrore.TEMPLATE_POLICY_STATO_ALLARME_ATTESO);
		}
		
	}
	
	public static void emitDiagnostic(MsgDiagnostico msgDiag,String idDiagnostico, Logger log) {
		
		Hashtable<String, String> keywords = msgDiag.getKeywordLogPersonalizzati();
		StringBuilder bf = new StringBuilder();
		try{
			if(keywords!=null && keywords.size()>0){
				addNextValue(keywords, bf, GeneratoreMessaggiErrore.TEMPLATE_POLICY_ACTIVE_ID);
				addNextValue(keywords, bf, GeneratoreMessaggiErrore.TEMPLATE_POLICY_VIOLATA_MOTIVO);
				addNextValue(keywords, bf, GeneratoreMessaggiErrore.TEMPLATE_POLICY_FILTRATA_MOTIVO);
				addNextValue(keywords, bf, GeneratoreMessaggiErrore.TEMPLATE_POLICY_NON_APPLICABILE_MOTIVO);
				if(saveSingleValueOfMessageError){
					addNextValue(keywords, bf, GeneratoreMessaggiErrore.TEMPLATE_POLICY_VALORE_SOGLIA);
					addNextValue(keywords, bf, GeneratoreMessaggiErrore.TEMPLATE_POLICY_TIPOLOGIA_TEMPO_MEDIO);
					addNextValue(keywords, bf, GeneratoreMessaggiErrore.TEMPLATE_POLICY_VALORE_RILEVATO);
					addNextValue(keywords, bf, GeneratoreMessaggiErrore.TEMPLATE_POLICY_AVG_TIME_RILEVATO);
					addNextValue(keywords, bf, GeneratoreMessaggiErrore.TEMPLATE_POLICY_VALORE_SOGLIA_DEGRADO_PRESTAZIONALE);
					addNextValue(keywords, bf, GeneratoreMessaggiErrore.TEMPLATE_POLICY_GRUPPO);
					addNextValue(keywords, bf, GeneratoreMessaggiErrore.TEMPLATE_POLICY_INTERVALLO_TEMPORALE);
					addNextValue(keywords, bf, GeneratoreMessaggiErrore.TEMPLATE_POLICY_NOME_ALLARME);
					addNextValue(keywords, bf, GeneratoreMessaggiErrore.TEMPLATE_POLICY_STATO_ALLARME);
					addNextValue(keywords, bf, GeneratoreMessaggiErrore.TEMPLATE_POLICY_STATO_ALLARME_ATTESO);
				}
			}
		}catch(Exception e){
			log.error(e.getMessage(),e);
		}
				
		if(bf.length()>0){
			msgDiag.getProperties().put(MsgDiagnosticiProperties.DIAGNOSTIC_WITH_DYNAMIC_INFO_TYPE,MsgDiagnosticiProperties.DIAGNOSTIC_TYPE_POLICY_CONTROLLO_TRAFFICO);
			msgDiag.getProperties().put(MsgDiagnosticiProperties.DIAGNOSTIC_WITH_DYNAMIC_INFO_VALUE,bf.toString());
		}		
		
		msgDiag.logPersonalizzato(idDiagnostico);
		
		if(bf.length()>0){
			msgDiag.getProperties().remove(MsgDiagnosticiProperties.DIAGNOSTIC_WITH_DYNAMIC_INFO_TYPE);
			msgDiag.getProperties().remove(MsgDiagnosticiProperties.DIAGNOSTIC_WITH_DYNAMIC_INFO_VALUE);
		}		
		
	}
	private static void addNextValue(Hashtable<String, String> keywords,StringBuilder bf, String keyId) throws Exception{
		if(bf.length()>0){
			bf.append(MsgDiagnosticiProperties.DIAGNOSTIC_WITH_DYNAMIC_INFO_SEPARATOR);
		}
		if(keywords.containsKey(keyId)){
			String v = keywords.get(keyId);
			if(v==null || v.length()<=0){
				bf.append(MsgDiagnosticiProperties.NON_PRESENTE);
			}
			else{
				if(v.contains(MsgDiagnosticiProperties.DIAGNOSTIC_WITH_DYNAMIC_INFO_SEPARATOR)){
					throw new Exception("Valore ["+v+"] della chiave ["+keyId+"] non simulabile poichè contiene il separatore ["+MsgDiagnosticiProperties.DIAGNOSTIC_WITH_DYNAMIC_INFO_SEPARATOR+"]");
				}
				if(v.contains(MsgDiagnosticiProperties.DIAGNOSTIC_WITH_DYNAMIC_INFO_DIAG_SEPARATOR)){
					throw new Exception("Valore ["+v+"] della chiave ["+keyId+"] non simulabile poichè contiene il separatore di diagnostici ["+MsgDiagnosticiProperties.DIAGNOSTIC_WITH_DYNAMIC_INFO_DIAG_SEPARATOR+"]");
				}
				bf.append(v);
			}
		}
		else{
			bf.append(MsgDiagnosticiProperties.NON_PRESENTE);
		}
	}
	
	public static Hashtable<String, String> convertToProperties(String value) throws Exception{
		if(value.contains(MsgDiagnosticiProperties.DIAGNOSTIC_WITH_DYNAMIC_INFO_SEPARATOR)){
			
			String [] tmp = value.split(MsgDiagnosticiProperties.DIAGNOSTIC_WITH_DYNAMIC_INFO_SEPARATOR);
			if(tmp==null || tmp.length<=0){
				throw new Exception("Split value ["+value+"] with ["+MsgDiagnosticiProperties.DIAGNOSTIC_WITH_DYNAMIC_INFO_SEPARATOR+"] failed");
			}
			int NUMERO_TEMPLATE_DINAMICI = NUMERO_TEMPLATE_DINAMICI_ERRORI_GENERICI;
			if(saveSingleValueOfMessageError){
				NUMERO_TEMPLATE_DINAMICI+=NUMERO_TEMPLATE_DINAMICI_DETTAGLI_SINGOLO_ERRORE;
			}
			if(tmp.length!=NUMERO_TEMPLATE_DINAMICI){
				throw new Exception("Split value ["+value+"] with ["+MsgDiagnosticiProperties.DIAGNOSTIC_WITH_DYNAMIC_INFO_SEPARATOR+
						"] failed (expected:"+NUMERO_TEMPLATE_DINAMICI+" found:"+tmp.length+")");
			}
			
			Hashtable<String, String> map = new Hashtable<String, String>();
			
			for (int i = 0; i < tmp.length; i++) {
				
				String v = tmp[i];
				if(v==null || (v.length()==1 && MsgDiagnosticiProperties.NON_PRESENTE.equals(v))){
					continue;
				}
				
				if(i==0)
					map.put(GeneratoreMessaggiErrore.TEMPLATE_POLICY_ACTIVE_ID,v);
				else if(i==1)
					map.put(GeneratoreMessaggiErrore.TEMPLATE_POLICY_VIOLATA_MOTIVO,v);
				else if(i==2)
					map.put(GeneratoreMessaggiErrore.TEMPLATE_POLICY_FILTRATA_MOTIVO,v);
				else if(i==3)
					map.put(GeneratoreMessaggiErrore.TEMPLATE_POLICY_NON_APPLICABILE_MOTIVO,v);
				
				if(saveSingleValueOfMessageError){
					if(i==4)
						map.put(GeneratoreMessaggiErrore.TEMPLATE_POLICY_VALORE_SOGLIA,v);
					else if(i==5)
						map.put(GeneratoreMessaggiErrore.TEMPLATE_POLICY_TIPOLOGIA_TEMPO_MEDIO,v);
					else if(i==6)
						map.put(GeneratoreMessaggiErrore.TEMPLATE_POLICY_VALORE_RILEVATO,v);
					else if(i==7)
						map.put(GeneratoreMessaggiErrore.TEMPLATE_POLICY_AVG_TIME_RILEVATO,v);
					else if(i==8)
						map.put(GeneratoreMessaggiErrore.TEMPLATE_POLICY_VALORE_SOGLIA_DEGRADO_PRESTAZIONALE,v);
					else if(i==9)
						map.put(GeneratoreMessaggiErrore.TEMPLATE_POLICY_GRUPPO,v);
					else if(i==10)
						map.put(GeneratoreMessaggiErrore.TEMPLATE_POLICY_INTERVALLO_TEMPORALE,v);
					else if(i==11)
						map.put(GeneratoreMessaggiErrore.TEMPLATE_POLICY_NOME_ALLARME,v);
					else if(i==12)
						map.put(GeneratoreMessaggiErrore.TEMPLATE_POLICY_STATO_ALLARME,v);
					else if(i==13)
						map.put(GeneratoreMessaggiErrore.TEMPLATE_POLICY_STATO_ALLARME_ATTESO,v);
				}
				
			}
			
			return map;
			
		}
		throw new Exception("Value is null");
	}
}
