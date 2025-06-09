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
package org.openspcoop2.web.monitor.core.constants;


/****
 * 
 * Classe per gestire l'enumerazione dei ruoli utente possibili
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public enum ModalitaRicercaTransazioni {

	ANDAMENTO_TEMPORALE ("intervalloTemporale"),
	RICERCA_LIBERA ("ricercaLibera"),
	ESAMINA_ARCHIVIO_ZIP("esaminaArchivioZip"),
	MITTENTE_TOKEN_INFO ("mittenteTokenInfo"),
	MITTENTE_SOGGETTO ("mittenteSoggetto"),
	MITTENTE_APPLICATIVO ("mittenteApplicativo"),
	MITTENTE_IDENTIFICATIVO_AUTENTICATO ("mittenteIdentificativoAutenticato"),
	MITTENTE_INDIRIZZO_IP ("mittenteIndirizzoIP"),
	ID_APPLICATIVO_AVANZATA ("idApplicativo"), 
	ID_APPLICATIVO_BASE ("idApplicativoBase"), 
	ID_MESSAGGIO ("idMessaggio"),
	ID_TRANSAZIONE ("idTransazione"),
	LIVE ("Live"),
	PURPOSE_ID ("purposeId");
	
	private String value;
	ModalitaRicercaTransazioni(String ruolo) {
		this.value = ruolo;
	}
	
	@Override
	public String toString(){
		return this.value;
	}
	
	public String getValue() {
		return this.value;
	}

	public static ModalitaRicercaTransazioni getFromString(String v){
		ModalitaRicercaTransazioni res = null;
		for (ModalitaRicercaTransazioni tmp : values()) {
			if(tmp.getValue().equals(v)){
				res = tmp;
				break;
			}
		}
		return res;
	}
	
	
	public static int getLivello(String modalita) {
		ModalitaRicercaTransazioni modalitaEnum = getFromString(modalita); 
		if(modalitaEnum!=null) {
			switch (modalitaEnum) {
				case ANDAMENTO_TEMPORALE,
					ID_MESSAGGIO,
					ID_TRANSAZIONE,
					MITTENTE_APPLICATIVO,
					MITTENTE_IDENTIFICATIVO_AUTENTICATO,
					MITTENTE_INDIRIZZO_IP,
					MITTENTE_SOGGETTO,
					MITTENTE_TOKEN_INFO,
					RICERCA_LIBERA,
					LIVE,
					ESAMINA_ARCHIVIO_ZIP,
					PURPOSE_ID:
					return 1;
				case ID_APPLICATIVO_AVANZATA,
					ID_APPLICATIVO_BASE:
					return 2;
				default:
					return 1;
			}
		}
		
		return 1;
	}
}
