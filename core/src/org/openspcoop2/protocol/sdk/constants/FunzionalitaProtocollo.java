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
	RIFERIMENTO_ID_RICHIESTA("riferimentoIdRichiesta"), 
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
	
	public boolean equals(String p){
		if(p==null){
			return false;
		}
		return this.funzionalita.equals(p);
	}
	
	public String getEngineValue(){
		return this.funzionalita;
	}


}
