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

import java.io.Serializable;
import java.util.List;

import org.openspcoop2.generic_project.beans.IEnumeration;

import org.openspcoop2.pdd.logger.record.InfoDato;
import org.openspcoop2.utils.UtilsRuntimeException;

/**     
 * Enumeration dell'elemento TipoSeverita xsd (tipo:int) 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum MappingRicostruzioneDiagnostici implements IEnumeration , Serializable , Cloneable {

	
	DIAGNOSTICI_EMESSI (CostantiMappingDiagnostici.DIAGNOSTICI_EMESSI),
	
	DIAGNOSTICI_EMISSIONE_FIRST_DATE (CostantiMappingDiagnostici.DIAGNOSTICI_EMISSIONE_FIRST_DATE),
	
	TIPO_AUTORIZZAZIONE (CostantiMappingDiagnostici.TIPO_AUTORIZZAZIONE),
	
	CODICE_TRASPORTO_RICHIESTA (CostantiMappingDiagnostici.CODICE_TRASPORTO_RICHIESTA),
	
	CODICE_TRASPORTO_RISPOSTA (CostantiMappingDiagnostici.CODICE_TRASPORTO_RISPOSTA),
	
	TIPO_CONNETTORE (CostantiMappingDiagnostici.TIPO_CONNETTORE),
	
	MAX_THREADS_THRESHOLD (CostantiMappingDiagnostici.MAX_THREADS_THRESHOLD),
	
	CONTROLLO_TRAFFICO_THRESHOLD (CostantiMappingDiagnostici.CONTROLLO_TRAFFICO_THRESHOLD),
	
	ACTIVE_THREADS (CostantiMappingDiagnostici.ACTIVE_THREADS),
	
	NUMERO_POLICY_CONFIGURATE (CostantiMappingDiagnostici.NUMERO_POLICY_CONFIGURATE),
	
	NUMERO_POLICY_DISABILITATE (CostantiMappingDiagnostici.NUMERO_POLICY_DISABILITATE),
	
	NUMERO_POLICY_FILTRATE (CostantiMappingDiagnostici.NUMERO_POLICY_FILTRATE),
	
	NUMERO_POLICY_NON_APPLICATE (CostantiMappingDiagnostici.NUMERO_POLICY_NON_APPLICATE),
	
	NUMERO_POLICY_RISPETTATE (CostantiMappingDiagnostici.NUMERO_POLICY_RISPETTATE),
	
	NUMERO_POLICY_VIOLATE (CostantiMappingDiagnostici.NUMERO_POLICY_VIOLATE),
	
	NUMERO_POLICY_VIOLATE_WARNING_ONLY (CostantiMappingDiagnostici.NUMERO_POLICY_VIOLATE_WARNING_ONLY),
	
	NUMERO_POLICY_IN_ERRORE (CostantiMappingDiagnostici.NUMERO_POLICY_IN_ERRORE),
	
	TIPO_AUTENTICAZIONE (CostantiMappingDiagnostici.TIPO_AUTENTICAZIONE),
	
	TIPO_AUTORIZZAZIONE_CONTENUTI (CostantiMappingDiagnostici.TIPO_AUTORIZZAZIONE_CONTENUTI),
	
	TIPO_VALIDAZIONE_CONTENUTI (CostantiMappingDiagnostici.TIPO_VALIDAZIONE_CONTENUTI),
	
	TIPO_PROCESSAMENTO_MTOM_RICHIESTA (CostantiMappingDiagnostici.TIPO_PROCESSAMENTO_MTOM_RICHIESTA),
	
	TIPO_PROCESSAMENTO_MTOM_RISPOSTA (CostantiMappingDiagnostici.TIPO_PROCESSAMENTO_MTOM_RISPOSTA),
	
	TIPO_PROCESSAMENTO_MESSAGE_SECURITY_RICHIESTA (CostantiMappingDiagnostici.TIPO_PROCESSAMENTO_MESSAGE_SECURITY_RICHIESTA),
	
	TIPO_PROCESSAMENTO_MESSAGE_SECURITY_RISPOSTA (CostantiMappingDiagnostici.TIPO_PROCESSAMENTO_MESSAGE_SECURITY_RISPOSTA),
	
	AUTENTICAZIONE_IN_CACHE (CostantiMappingDiagnostici.AUTENTICAZIONE_IN_CACHE),
	
	AUTORIZZAZIONE_IN_CACHE (CostantiMappingDiagnostici.AUTORIZZAZIONE_IN_CACHE),
	
	AUTORIZZAZIONE_CONTENUTI_IN_CACHE (CostantiMappingDiagnostici.AUTORIZZAZIONE_CONTENUTI_IN_CACHE),
	
	TOKEN_POLICY (CostantiMappingDiagnostici.TOKEN_POLICY),
	
	TOKEN_POLICY_ACTIONS (CostantiMappingDiagnostici.TOKEN_POLICY_ACTIONS),
	
	TOKEN_POLICY_AUTENTCAZIONE (CostantiMappingDiagnostici.TOKEN_POLICY_AUTENTCAZIONE),
	
	RESPONSE_FROM_CACHE (CostantiMappingDiagnostici.RESPONSE_FROM_CACHE),
	
	TIPO_TRASFORMAZIONE_RICHIESTA (CostantiMappingDiagnostici.TIPO_TRASFORMAZIONE_RICHIESTA),
	
	TIPO_TRASFORMAZIONE_RISPOSTA (CostantiMappingDiagnostici.TIPO_TRASFORMAZIONE_RISPOSTA),
	
	AUTENTICAZIONE_TOKEN_IN_CACHE (CostantiMappingDiagnostici.AUTENTICAZIONE_TOKEN_IN_CACHE),
	
	AUTENTICAZIONE_FALLITA_MOTIVAZIONE (CostantiMappingDiagnostici.AUTENTICAZIONE_FALLITA_MOTIVAZIONE),
	
	MODI_TOKEN_AUTHORIZATION_IN_CACHE (CostantiMappingDiagnostici.MODI_TOKEN_AUTHORIZATION_IN_CACHE),
	
	MODI_TOKEN_INTEGRITY_IN_CACHE (CostantiMappingDiagnostici.MODI_TOKEN_INTEGRITY_IN_CACHE),
	
	MODI_TOKEN_AUDIT_IN_CACHE (CostantiMappingDiagnostici.MODI_TOKEN_AUDIT_IN_CACHE)
	
	;
	
	
	/** Value */
	private java.lang.Integer position;
	private String description;
	private InfoDato info;
	@Override
	public java.lang.Integer getValue()
	{
		return this.position;
	}
	public java.lang.Integer getPosition() {
		return this.position;
	}
	public String getDescription() {
		return this.description;
	}
	public InfoDato getInfo() {
		return this.info;
	}


	/** Official Constructor */
	MappingRicostruzioneDiagnostici(InfoDato infoElementoTraccia)
	{
		this.position = infoElementoTraccia.getPosition();
		this.description = infoElementoTraccia.getDescription();
		this.info = infoElementoTraccia;
	}


	
	@Override
	public String toString(){
		return this.position+"";
	}
	public boolean equals(java.lang.Integer object){
		if(object==null)
			return false;
		return object.toString().equals(this.getValue().toString());	
	}
	
		
	
	/** compatibility with the generated bean (reflection) */
	public boolean equals(Object object,List<String> fieldsNotCheck){
		if(fieldsNotCheck!=null) {
			// nop
		}
		if( !(object instanceof MappingRicostruzioneDiagnostici) ){
			throw new UtilsRuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals((object));
	}
	public String toString(boolean reportHTML){
		if(reportHTML) {
			// nop
		}
		return toString();
	}
  	public String toString(boolean reportHTML,List<String> fieldsNotIncluded){
  		if(reportHTML || fieldsNotIncluded!=null) {
			// nop
		}
		return toString();
  	}
  	public String diff(Object object,StringBuilder bf,boolean reportHTML){
  		if(object!=null || reportHTML) {
			// nop
		}
  		return bf.toString();
	}
	public String diff(Object object,StringBuilder bf,boolean reportHTML,List<String> fieldsNotIncluded){
		if(object!=null || reportHTML || fieldsNotIncluded!=null) {
			// nop
		}
  		return bf.toString();
	}
	
	
	/** Utilities */
	
	public static java.lang.Integer[] toArray(){
		java.lang.Integer[] res = new java.lang.Integer[values().length];
		int i=0;
		for (MappingRicostruzioneDiagnostici tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (MappingRicostruzioneDiagnostici tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (MappingRicostruzioneDiagnostici tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(java.lang.Integer value){
		return toEnumConstant(value)!=null;
	}
	
	public static MappingRicostruzioneDiagnostici toEnumConstant(java.lang.Integer value){
		for (MappingRicostruzioneDiagnostici tmp : values()) {
			if(tmp.getValue()!=null && value!=null && (tmp.getValue().intValue() == value.intValue())){
				return tmp;
			}
		}
		return null;
	}
	
	public static IEnumeration toEnumConstantFromString(String value){
		for (MappingRicostruzioneDiagnostici tmp : values()) {
			if(tmp.toString().equals(value)){
				return tmp;
			}
		}
		return null;
	}
}
