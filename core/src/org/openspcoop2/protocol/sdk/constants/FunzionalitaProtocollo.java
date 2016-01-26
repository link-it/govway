/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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


/**
 * ProfiliDiCollaborazione
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum FunzionalitaProtocollo {
	
	FILTRO_DUPLICATI("filtroDuplicati"), 
	CONFERMA_RICEZIONE("confermaRicezione"), 
	COLLABORAZIONE("collaborazione"), 
	CONSEGNA_IN_ORDINE("consegnaInOrdine"), 
	SCADENZA("scadenza"),
	MANIFEST_ATTACHMENTS("manifestAttachments");
	
	private final String funzionalita;

	FunzionalitaProtocollo(String profilo){
		this.funzionalita = profilo;
	}
	
	@Override
	public String toString() {
		return this.funzionalita;
	}
	
	public boolean equals(FunzionalitaProtocollo p){
		if(p==null){
			return false;
		}
		return this.funzionalita.equals(p.funzionalita);
	}
	
	public String getEngineValue(){
		return this.funzionalita;
	}


}
