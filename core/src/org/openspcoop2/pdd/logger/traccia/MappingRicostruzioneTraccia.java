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

package org.openspcoop2.pdd.logger.traccia;

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
public enum MappingRicostruzioneTraccia implements IEnumeration , Serializable , Cloneable {

	
	TRACCIA_EMESSA (CostantiMappingTracciamento.TRACCIA_EMESSA),
	
	TRACCIA_DATA_REGISTRAZIONE (CostantiMappingTracciamento.TRACCIA_DATA_REGISTRAZIONE),
	
	TRACCIA_BUSTA_ORA_REGISTRAZIONE (CostantiMappingTracciamento.TRACCIA_BUSTA_ORA_REGISTRAZIONE),
	
	TRACCIA_BUSTA_TIPO_ORA_REGISTRAZIONE_BY_PROTOCOLLO (CostantiMappingTracciamento.TRACCIA_BUSTA_TIPO_ORA_REGISTRAZIONE_BY_PROTOCOLLO),
	
	TRACCIA_BUSTA_TIPO_ORA_REGISTRAZIONE_BY_OPENSPCOOP (CostantiMappingTracciamento.TRACCIA_BUSTA_TIPO_ORA_REGISTRAZIONE_BY_OPENSPCOOP), 
	
	TRACCIA_BUSTA_RIFERIMENTO_MESSAGGIO (CostantiMappingTracciamento.TRACCIA_BUSTA_RIFERIMENTO_MESSAGGIO), 
	
	TRACCIA_BUSTA_SCADENZA (CostantiMappingTracciamento.TRACCIA_BUSTA_SCADENZA),
	
	TRACCIA_FILTRO_DUPLICATI_CODE (CostantiMappingTracciamento.TRACCIA_FILTRO_DUPLICATI_CODE), 
	
	TRACCIA_FILTRO_DUPLICATI (CostantiMappingTracciamento.TRACCIA_FILTRO_DUPLICATI), 
	
	TRACCIA_BUSTA_SEQUENZA (CostantiMappingTracciamento.TRACCIA_BUSTA_SEQUENZA), 
	
	TRACCIA_BUSTA_RISCONTRO_ORA_REGISTRAZIONE (CostantiMappingTracciamento.TRACCIA_BUSTA_RISCONTRO_ORA_REGISTRAZIONE), 
	
	TRACCIA_BUSTA_RISCONTRO_TIPO_ORA_REGISTRAZIONE (CostantiMappingTracciamento.TRACCIA_BUSTA_RISCONTRO_TIPO_ORA_REGISTRAZIONE), 
	
	TRACCIA_BUSTA_RISCONTRO_TIPO_ORA_REGISTRAZIONE_CODE (CostantiMappingTracciamento.TRACCIA_BUSTA_RISCONTRO_TIPO_ORA_REGISTRAZIONE_CODE), 
	
	TRACCIA_BUSTA_PRIMA_TRASMISSIONE_ORA_REGISTRAZIONE (CostantiMappingTracciamento.TRACCIA_BUSTA_PRIMA_TRASMISSIONE_ORA_REGISTRAZIONE), 
	
	TRACCIA_BUSTA_PRIMA_TRASMISSIONE_TIPO_ORA_REGISTRAZIONE (CostantiMappingTracciamento.TRACCIA_BUSTA_PRIMA_TRASMISSIONE_TIPO_ORA_REGISTRAZIONE), 
	
	TRACCIA_BUSTA_PRIMA_TRASMISSIONE_TIPO_ORA_REGISTRAZIONE_CODE (CostantiMappingTracciamento.TRACCIA_BUSTA_PRIMA_TRASMISSIONE_TIPO_ORA_REGISTRAZIONE_CODE), 
	
	TRACCIA_BUSTA_SECONDA_TRASMISSIONE_ORA_REGISTRAZIONE (CostantiMappingTracciamento.TRACCIA_BUSTA_SECONDA_TRASMISSIONE_ORA_REGISTRAZIONE), 
	
	TRACCIA_BUSTA_SECONDA_TRASMISSIONE_TIPO_ORA_REGISTRAZIONE (CostantiMappingTracciamento.TRACCIA_BUSTA_SECONDA_TRASMISSIONE_TIPO_ORA_REGISTRAZIONE), 
	
	TRACCIA_BUSTA_SECONDA_TRASMISSIONE_TIPO_ORA_REGISTRAZIONE_CODE (CostantiMappingTracciamento.TRACCIA_BUSTA_SECONDA_TRASMISSIONE_TIPO_ORA_REGISTRAZIONE_CODE),
	
	TRACCIA_BUSTA_CONFERMA_RICHIESTA (CostantiMappingTracciamento.TRACCIA_BUSTA_CONFERMA_RICHIESTA), 
	
	TRACCIA_BUSTA_SERVIZIO_CORRELATO_PRESENTE (CostantiMappingTracciamento.TRACCIA_BUSTA_SERVIZIO_CORRELATO_PRESENTE), 
	
	TRACCIA_BUSTA_ESITO_TRACCIA (CostantiMappingTracciamento.TRACCIA_BUSTA_ESITO_TRACCIA),
	
	TRACCIA_BUSTA_SOGGETTO_APPLICATIVO_TOKEN (CostantiMappingTracciamento.TRACCIA_BUSTA_SOGGETTO_APPLICATIVO_TOKEN)
	
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
	MappingRicostruzioneTraccia(InfoDato infoElementoTraccia)
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
		if( !(object instanceof MappingRicostruzioneTraccia) ){
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
		for (MappingRicostruzioneTraccia tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (MappingRicostruzioneTraccia tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (MappingRicostruzioneTraccia tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(java.lang.Integer value){
		return toEnumConstant(value)!=null;
	}
	
	public static MappingRicostruzioneTraccia toEnumConstant(java.lang.Integer value){
		for (MappingRicostruzioneTraccia tmp : values()) {
			if(tmp.getValue()!=null && value!=null && (tmp.getValue().intValue() == value.intValue())){
				return tmp;
			}
		}
		return null;
	}
	
	public static IEnumeration toEnumConstantFromString(String value){
		for (MappingRicostruzioneTraccia tmp : values()) {
			if(tmp.toString().equals(value)){
				return tmp;
			}
		}
		return null;
	}
}
