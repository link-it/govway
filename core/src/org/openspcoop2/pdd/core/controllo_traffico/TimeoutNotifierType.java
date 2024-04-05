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
package org.openspcoop2.pdd.core.controllo_traffico;

import org.openspcoop2.core.eventi.constants.TipoEvento;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;

/**
 * TimeoutNotifierType
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum TimeoutNotifierType{
	
	CONNECTION, REQUEST, WAIT_RESPONSE, RECEIVE_RESPONSE;
	
	public static CategoriaEventoControlloTraffico toCategoriaEventoControlloTraffico(TimeoutNotifierType type) {
		if(type!=null) {
			switch (type) {
			case CONNECTION:
				return CategoriaEventoControlloTraffico.TIMEOUT_CONNESSIONE;
			case REQUEST:
				return CategoriaEventoControlloTraffico.TIMEOUT_RICHIESTA;
			case WAIT_RESPONSE:
			case RECEIVE_RESPONSE:
				return CategoriaEventoControlloTraffico.TIMEOUT_RISPOSTA;
			}
		}
		return CategoriaEventoControlloTraffico.TIMEOUT_RISPOSTA;
	}
	
	public static TipoEvento toTipoEvento(TimeoutNotifierType type) {
		if(type!=null) {
			switch (type) {
			case CONNECTION:
				return TipoEvento.CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT;
			case REQUEST:
				return TipoEvento.CONTROLLO_TRAFFICO_REQUEST_READ_TIMEOUT;
			case WAIT_RESPONSE:
			case RECEIVE_RESPONSE:
				return TipoEvento.CONTROLLO_TRAFFICO_READ_TIMEOUT;
			}
		}
		return TipoEvento.CONTROLLO_TRAFFICO_READ_TIMEOUT;
	}
	
	public static EsitoTransazioneName toEsitoTransazioneName(TimeoutNotifierType type) {
		if(type!=null) {
			switch (type) {
			case CONNECTION:
				return EsitoTransazioneName.ERRORE_CONNECTION_TIMEOUT;
			case REQUEST:
				return EsitoTransazioneName.ERRORE_REQUEST_TIMEOUT;
			case WAIT_RESPONSE:
			case RECEIVE_RESPONSE:
				return EsitoTransazioneName.ERRORE_RESPONSE_TIMEOUT;
			}
		}
		return EsitoTransazioneName.ERRORE_RESPONSE_TIMEOUT;
	}

}
