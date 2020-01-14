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

package org.openspcoop2.pdd.core.integrazione;


import org.openspcoop2.core.id.IDServizio;

/**
 * Classe utilizzata per la ricezione di informazioni di integrazione 
 * dai servizi applicativi verso la porta di dominio.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IGestoreIntegrazionePDSoap extends IGestoreIntegrazionePD {

	// IN - Request
	
	public void deleteInRequestHeader(InRequestPDMessage inRequestPDMessage) throws HeaderIntegrazioneException;
	
	public void updateInRequestHeader(InRequestPDMessage inRequestPDMessage,
			IDServizio idServizio,String idMessaggio,
			String servizioApplicativo,String correlazioneApplicativa) throws HeaderIntegrazioneException;

	// OUT - Request
	
	// IN - Response
	
	public void deleteInResponseHeader(InResponsePDMessage inResponsePDMessage) throws HeaderIntegrazioneException;
	
	public void updateInResponseHeader(InResponsePDMessage inResponsePDMessage,
			String idMessaggioRichiesta,String idMessaggioRisposta,String servizioApplicativo,
			String correlazioneApplicativa,String riferimentoCorrelazioneApplicativaRichiesta) throws HeaderIntegrazioneException;
	
	// OUT - Response
		
}
