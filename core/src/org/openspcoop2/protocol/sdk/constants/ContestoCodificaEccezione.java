/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.sdk.constants;

import org.apache.commons.lang.NotImplementedException;

/**
 * ContestoCodificaEccezione
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum ContestoCodificaEccezione {

	INTESTAZIONE("EccezioneValidazioneProtocollo"),PROCESSAMENTO("EccezioneProcessamento");
	
	private String value;
	ContestoCodificaEccezione(String v){
		this.value = v;
	}
	
	@Override
	public String toString(){
		throw new NotImplementedException("Use Protocol Factory");
	}
	
	public boolean equals(String c){
		if(c==null){
			return false;
		}
		return this.value.equals(c);
	}
	
	public String getEngineValue(){
		return this.value;
	}
	
	public static ContestoCodificaEccezione toContestoCodificaEccezione(String v){
		if(INTESTAZIONE.value.equals(v)){
			return INTESTAZIONE;
		}
		else if (PROCESSAMENTO.value.equals(v)){
			return PROCESSAMENTO;
		}
		else{
			return null;
		}
	}
}
