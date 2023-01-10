/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.integrazione;




/**
 * Classe utilizzata per la spedizione di informazioni di integrazione 
 * dalla porta di dominio verso i servizi applicativi.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IGestoreIntegrazionePASoap extends IGestoreIntegrazionePA {
		
	// IN - Request
	
	public void deleteInRequestHeader(InRequestPAMessage inRequestPAMessage) throws HeaderIntegrazioneException;
	
	public void updateInRequestHeader(InRequestPAMessage inRequestPAMessage,
			String idMessaggio,String servizioApplicativo,String correlazioneApplicativa) throws HeaderIntegrazioneException;
	
	// OUT - Request
	
	// IN - Response
	
	public void deleteInResponseHeader(InResponsePAMessage inResponsePAMessage) throws HeaderIntegrazioneException;
	
	public void updateInResponseHeader(InResponsePAMessage inResponsePAMessage,
			String idMessaggioRichiesta,String idMessaggioRisposta,String servizioApplicativo,
			String correlazioneApplicativa,String riferimentoCorrelazioneApplicativaRichiesta) throws HeaderIntegrazioneException;
	
	// OUT - Response
}