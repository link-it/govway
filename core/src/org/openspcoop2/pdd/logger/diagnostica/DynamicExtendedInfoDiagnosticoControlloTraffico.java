/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.logger.diagnostica;

import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.pdd.core.controllo_traffico.GeneratoreMessaggiErrore;
import org.openspcoop2.pdd.logger.record.CostantiDati;

/**     
 * DynamicExtendedInfoDiagnosticoControlloTraffico
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DynamicExtendedInfoDiagnosticoControlloTraffico {

	private DynamicExtendedInfoDiagnosticoControlloTraffico() {}
	
	public static final int NUMERO_TEMPLATE_DINAMICI_ERRORI_GENERICI = 4; // sono i diagnostici emessi dai moduli ricezione contenuti o buste
	public static final int NUMERO_TEMPLATE_DINAMICI_DETTAGLI_SINGOLO_ERRORE = 10; // sono i diagnostici di "appoggio" registrati nel modulo 'all' utilizzati per generare l'errore
	// NOTA: siccome tanto viene salvato direttamente il diagnostico emesso dai moduli ricezione contenuti o buste non sembra necessario salvare i singoli dettagli visto che sono già nell'errore generico
	
	private static final boolean SAVE_SINGLE_VALUE_OF_MESSAGE_ERROR = false;
	
	public static Map<String, String> convertToProperties(String value) throws CoreException{
		if(value.contains(CostantiMappingDiagnostici.DIAGNOSTIC_WITH_DYNAMIC_INFO_SEPARATOR)){
			
			String [] tmp = value.split(CostantiMappingDiagnostici.DIAGNOSTIC_WITH_DYNAMIC_INFO_SEPARATOR);
			if(tmp==null || tmp.length<=0){
				throw new CoreException("Split value ["+value+"] with ["+CostantiMappingDiagnostici.DIAGNOSTIC_WITH_DYNAMIC_INFO_SEPARATOR+"] failed");
			}
			int numeroTemplateDinamici = NUMERO_TEMPLATE_DINAMICI_ERRORI_GENERICI;
			if(SAVE_SINGLE_VALUE_OF_MESSAGE_ERROR){
				numeroTemplateDinamici+=NUMERO_TEMPLATE_DINAMICI_DETTAGLI_SINGOLO_ERRORE;
			}
			if(tmp.length!=numeroTemplateDinamici){
				throw new CoreException("Split value ["+value+"] with ["+CostantiMappingDiagnostici.DIAGNOSTIC_WITH_DYNAMIC_INFO_SEPARATOR+
						"] failed (expected:"+numeroTemplateDinamici+" found:"+tmp.length+")");
			}
			
			return convertToProperties(tmp);
			
		}
		throw new CoreException("Value is null");
	}
	private static Map<String, String> convertToProperties(String [] tmp) {
		Map<String, String> map = new HashMap<>();
		
		for (int i = 0; i < tmp.length; i++) {
			
			String v = tmp[i];
			if(v==null || (v.length()==1 && CostantiDati.NON_PRESENTE.equals(v))){
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
			
			if(SAVE_SINGLE_VALUE_OF_MESSAGE_ERROR){
				fillError(map, i, v);
			}
			
		}
		
		return map;
	}
	private static void fillError(Map<String, String> map, int i, String v) {
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
