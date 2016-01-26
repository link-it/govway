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



package org.openspcoop2.protocol.spcoop.backward_compatibility.services.skeleton;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.pdd.services.skeleton.IntegrationManagerException;
import org.openspcoop2.pdd.services.skeleton.IntegrationManagerMessage;
import org.openspcoop2.pdd.services.skeleton.ProtocolHeaderInfo;
import org.openspcoop2.protocol.spcoop.backward_compatibility.services.BackwardCompatibilityStartup;

		
/**
 * IntegrationManager service
 *
 *
 * @author Lo Votrico Fabio (fabio@link.it)
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
		
public abstract class IntegrationManager implements IntegrationManagerInterface {

	
	protected abstract HttpServletRequest getHttpServletRequest() throws SPCoopException;
	protected abstract HttpServletResponse getHttpServletResponse() throws SPCoopException;
	
	
	private SPCoopException toSPCoopException(IntegrationManagerException imException) {
		SPCoopException spcoopException = new SPCoopException(imException.getDescrizioneEccezione());
		spcoopException.setCodiceEccezione(BackwardCompatibilityStartup.getCodeMapping().toOpenSPCoopV1Code(imException.getCodiceEccezione()));
		spcoopException.setDescrizioneEccezione(imException.getDescrizioneEccezione());
		spcoopException.setIdentificativoFunzione(imException.getIdentificativoFunzione());
		spcoopException.setIdentificativoPorta(imException.getIdentificativoPorta());
		spcoopException.setOraRegistrazione(imException.getOraRegistrazione());
		spcoopException.setTipoEccezione(imException.getTipoEccezione());
		
		return spcoopException;
	}
	
	private SPCoopMessage toSPCoopMessage(IntegrationManagerMessage imMessage){
		SPCoopMessage spcoopMessage = new SPCoopMessage();
		spcoopMessage.setIdApplicativo(imMessage.getIdApplicativo());
		spcoopMessage.setImbustamento(imMessage.getImbustamento());
		spcoopMessage.setMessage(imMessage.getMessage());
		spcoopMessage.setServizioApplicativo(imMessage.getServizioApplicativo());
		if(imMessage.getProtocolHeaderInfo()!=null)
			spcoopMessage.setSpcoopHeaderInfo(toSPCoopHeaderInfo(imMessage.getProtocolHeaderInfo()));
		return spcoopMessage;

	}
	
	private SPCoopHeaderInfo toSPCoopHeaderInfo(ProtocolHeaderInfo imProtocolHeaderInfo){
		SPCoopHeaderInfo spcoopHeaderInfo = new SPCoopHeaderInfo();
		spcoopHeaderInfo.setTipoMittente(imProtocolHeaderInfo.getTipoMittente());
		spcoopHeaderInfo.setMittente(imProtocolHeaderInfo.getMittente());
		spcoopHeaderInfo.setTipoDestinatario(imProtocolHeaderInfo.getTipoDestinatario());
		spcoopHeaderInfo.setDestinatario(imProtocolHeaderInfo.getDestinatario());
		spcoopHeaderInfo.setTipoServizio(imProtocolHeaderInfo.getTipoServizio());
		spcoopHeaderInfo.setServizio(imProtocolHeaderInfo.getServizio());
		spcoopHeaderInfo.setAzione(imProtocolHeaderInfo.getAzione());
		spcoopHeaderInfo.setID(imProtocolHeaderInfo.getID());
		spcoopHeaderInfo.setIdCollaborazione(imProtocolHeaderInfo.getIdCollaborazione());
		spcoopHeaderInfo.setRiferimentoMessaggio(imProtocolHeaderInfo.getRiferimentoMessaggio());
		return spcoopHeaderInfo;
	}	
	
	private IntegrationManagerMessage toIntegrationManagerMessage(SPCoopMessage spcoopMsg){
		IntegrationManagerMessage imMessage = new IntegrationManagerMessage();
		imMessage.setIdApplicativo(spcoopMsg.getIdApplicativo());
		imMessage.setImbustamento(spcoopMsg.getImbustamento());
		imMessage.setMessage(spcoopMsg.getMessage());
		if(spcoopMsg.getSpcoopHeaderInfo()!=null)
			imMessage.setProtocolHeaderInfo(toProtocolHeaderInfo(spcoopMsg.getSpcoopHeaderInfo()));
		imMessage.setServizioApplicativo(spcoopMsg.getServizioApplicativo());
		return imMessage;
}
	
	private ProtocolHeaderInfo toProtocolHeaderInfo(SPCoopHeaderInfo spcoopHeaderInfo){
		ProtocolHeaderInfo imProtocolHeaderInfo = new ProtocolHeaderInfo();
		imProtocolHeaderInfo.setTipoMittente(spcoopHeaderInfo.getTipoMittente());
		imProtocolHeaderInfo.setMittente(spcoopHeaderInfo.getMittente());
		imProtocolHeaderInfo.setTipoDestinatario(spcoopHeaderInfo.getTipoDestinatario());
		imProtocolHeaderInfo.setDestinatario(spcoopHeaderInfo.getDestinatario());
		imProtocolHeaderInfo.setTipoServizio(spcoopHeaderInfo.getTipoServizio());
		imProtocolHeaderInfo.setServizio(spcoopHeaderInfo.getServizio());
		imProtocolHeaderInfo.setAzione(spcoopHeaderInfo.getAzione());
		imProtocolHeaderInfo.setID(spcoopHeaderInfo.getID());
		imProtocolHeaderInfo.setIdCollaborazione(spcoopHeaderInfo.getIdCollaborazione());
		imProtocolHeaderInfo.setRiferimentoMessaggio(spcoopHeaderInfo.getRiferimentoMessaggio());
		return imProtocolHeaderInfo;
	}
	
	
	

	/*-------- getAllMessagesID ----*/

	/**
	 * Restituisce gli id di tutti i messaggi in base al servizio_applicativo
	 *
	 * @return una Collezione di ID di risposte applicative destinate al servizio applicativo che ha invocato il servizio
	 * 
	 */
	@Override
	public String[] getAllMessagesId() throws SPCoopException {
		
		IntegrationManagerOpenspcoop2 imOpenSPCoop2 = new IntegrationManagerOpenspcoop2(this.getHttpServletRequest(), this.getHttpServletResponse());
		
		List<String> listaId = null;
		try{
			listaId = imOpenSPCoop2.getAllMessagesId();
			return listaId.toArray(new String[1]);
		}catch(IntegrationManagerException imException){
			throw toSPCoopException(imException);
		}
		
	}


	/**
	 * Restituisce gli id di tutti i messaggi in base a servizio_applicativo, servizio
	 *
	 * @param tipoServizio Filtro per Servizio
	 * @param servizio Filtro per Servizio
	 * @param azione Filtro per Azione
	 * @return una Collezione di ID di risposte applicative destinate al servizio applicativo che ha invocato il servizio
	 * 
	 */
	@Override
	public String[] getAllMessagesIdByService(String tipoServizio, String servizio, String azione) throws SPCoopException {
		
		IntegrationManagerOpenspcoop2 imOpenSPCoop2 = new IntegrationManagerOpenspcoop2(this.getHttpServletRequest(), this.getHttpServletResponse());
		
		List<String> listaId = null;
		try{
			listaId = imOpenSPCoop2.getAllMessagesIdByService(tipoServizio, servizio, azione);
			return listaId.toArray(new String[1]);
		}catch(IntegrationManagerException imException){
			throw toSPCoopException(imException);
		}
	}


	/**
	 * Restituisce gli id dei primi <var>counter</var> i messaggi, in base al servizio_applicativo,
	 *
	 * @param counter Indica il numero di id da ritornare
	 * @return una Collezione di ID di risposte applicative destinate al servizio applicativo che ha invocato il servizio
	 * 
	 */
	@SuppressWarnings("deprecation")
	@Override
	@Deprecated
	public String[] getNextMessagesId(int counter) throws SPCoopException {
		
		IntegrationManagerOpenspcoop2 imOpenSPCoop2 = new IntegrationManagerOpenspcoop2(this.getHttpServletRequest(), this.getHttpServletResponse());
		
		List<String> listaId = null;
		try{
			listaId = imOpenSPCoop2.getNextMessagesId(counter);
			return listaId.toArray(new String[1]);
		}catch(IntegrationManagerException imException){
			throw toSPCoopException(imException);
		}
	}

	/**
	 *  Restituisce gli id dei primi <var>counter</var> i messaggi, in base a servizio_applicativo, servizio
	 *
	 * @param counter Indica il numero di id da ritornare
	 * @param tipoServizio Filtro per Servizio
	 * @param servizio Filtro per Servizio
	 * @param azione Filtro per Azione
	 * @return una Collezione di ID di risposte applicative destinate al servizio applicativo che ha invocato il servizio
	 * 
	 */
	@SuppressWarnings("deprecation")
	@Override
	@Deprecated
	public String[] getNextMessagesIdByService(int counter,String tipoServizio,String servizio, String azione) throws SPCoopException {
		
		IntegrationManagerOpenspcoop2 imOpenSPCoop2 = new IntegrationManagerOpenspcoop2(this.getHttpServletRequest(), this.getHttpServletResponse());
		
		List<String> listaId = null;
		try{
			listaId = imOpenSPCoop2.getNextMessagesIdByService(counter, tipoServizio, servizio, azione);
			return listaId.toArray(new String[1]);
		}catch(IntegrationManagerException imException){
			throw toSPCoopException(imException);
		}
	}
	
	/**
	 * Restituisce gli id dei primi <var>counter</var> i messaggi, in base al servizio_applicativo,
	 *
	 * @param counter Indica il numero di id da ritornare
	 * @return una Collezione di ID di risposte applicative destinate al servizio applicativo che ha invocato il servizio
	 * 
	 */
	@Override
	public String[] getMessagesIdArray(int offset,int counter) throws SPCoopException {
		
		IntegrationManagerOpenspcoop2 imOpenSPCoop2 = new IntegrationManagerOpenspcoop2(this.getHttpServletRequest(), this.getHttpServletResponse());
		
		List<String> listaId = null;
		try{
			listaId = imOpenSPCoop2.getMessagesIdArray(offset, counter);
			return listaId.toArray(new String[1]);
		}catch(IntegrationManagerException imException){
			throw toSPCoopException(imException);
		}
	}

	/**
	 *  Restituisce gli id dei primi <var>counter</var> i messaggi, in base a servizio_applicativo, servizio
	 *
	 * @param counter Indica il numero di id da ritornare
	 * @param tipoServizio Filtro per Servizio
	 * @param servizio Filtro per Servizio
	 * @param azione Filtro per Azione
	 * @return una Collezione di ID di risposte applicative destinate al servizio applicativo che ha invocato il servizio
	 * 
	 */
	@Override
	public String[] getMessagesIdArrayByService(int offset,int counter,String tipoServizio,String servizio, String azione) throws SPCoopException {
		
		IntegrationManagerOpenspcoop2 imOpenSPCoop2 = new IntegrationManagerOpenspcoop2(this.getHttpServletRequest(), this.getHttpServletResponse());
		
		List<String> listaId = null;
		try{
			listaId = imOpenSPCoop2.getAllMessagesIdByService(tipoServizio, servizio, azione);
			return listaId.toArray(new String[1]);
		}catch(IntegrationManagerException imException){
			throw toSPCoopException(imException);
		}
	}






	/* ------ Get ----- */

	/**
	 * Recupera e restituisce un messaggio
	 *
	 * @param idEGov ID del Messaggio da recuperare
	 * @return un SPCoopMessage contenente il messaggio recuperato (e informazioni supplementari)
	 * 
	 */
	@Override
	public SPCoopMessage getMessage(String idEGov) throws SPCoopException {
		
		IntegrationManagerOpenspcoop2 imOpenSPCoop2 = new IntegrationManagerOpenspcoop2(this.getHttpServletRequest(), this.getHttpServletResponse());
		
		try{
			return toSPCoopMessage(imOpenSPCoop2.getMessage(idEGov));
		}catch(IntegrationManagerException imException){
			throw toSPCoopException(imException);
		}
	}

	/**
	 * Recupera e restituisce un messaggio cercandolo per riferimentoMessaggio
	 *
	 * @param riferimentoMsg del Messaggio da recuperare
	 * @return un SPCoopMessage contenente il messaggio recuperato (e informazioni supplementari)
	 * 
	 */
	@Override
	public SPCoopMessage getMessageByReference(String riferimentoMsg) throws SPCoopException {
		
		IntegrationManagerOpenspcoop2 imOpenSPCoop2 = new IntegrationManagerOpenspcoop2(this.getHttpServletRequest(), this.getHttpServletResponse());
		
		try{
			return toSPCoopMessage(imOpenSPCoop2.getMessageByReference(riferimentoMsg));
		}catch(IntegrationManagerException imException){
			throw toSPCoopException(imException);
		}
	}






	/* -------- delete ------ */

	/**
	 * Cancella un messaggio
	 *
	 * @param idEGov ID del Messaggio da recuperare
	 * 
	 */
	@Override
	public void deleteMessage(String idEGov) throws SPCoopException {
		
		IntegrationManagerOpenspcoop2 imOpenSPCoop2 = new IntegrationManagerOpenspcoop2(this.getHttpServletRequest(), this.getHttpServletResponse());
		
		try{
			imOpenSPCoop2.deleteMessage(idEGov);
		}catch(IntegrationManagerException imException){
			throw toSPCoopException(imException);
		}
	}

	/**
	 * Cancella un messaggio cercandolo per riferimentoMessaggio
	 *
	 * @param riferimentoMsg del Messaggio da recuperare
	 * 
	 */
	@Override
	public void deleteMessageByReference(String riferimentoMsg) throws SPCoopException {
		
		IntegrationManagerOpenspcoop2 imOpenSPCoop2 = new IntegrationManagerOpenspcoop2(this.getHttpServletRequest(), this.getHttpServletResponse());
		
		try{
			imOpenSPCoop2.deleteMessageByReference(riferimentoMsg);
		}catch(IntegrationManagerException imException){
			throw toSPCoopException(imException);
		}
	}







	/* --------- delete All Messages ----------- */

	/**
	 * Cancella tutti i messaggi di un servizio applicativo
	 *
	 * 
	 */
	@Override
	public void deleteAllMessages() throws SPCoopException {

		IntegrationManagerOpenspcoop2 imOpenSPCoop2 = new IntegrationManagerOpenspcoop2(this.getHttpServletRequest(), this.getHttpServletResponse());
			
		try{
			imOpenSPCoop2.deleteAllMessages();
		}catch(IntegrationManagerException imException){
			throw toSPCoopException(imException);
		}
	}




	/* ------- Invocazione Porta Delegata ---------------*/

	/**
	 * Invoca una porta delegata
	 *
	 * @param portaDelegata Porta Delegata da invocare
	 * @param msg SPCoopMessage da utilizza come messaggio da invocare (contiene anche il servizio che si desidera invocare)
	 * @return un SPCoopMessage contenente il messaggio di risposta 
	 * 
	 */
	@Override
	public SPCoopMessage invocaPortaDelegata(String portaDelegata,SPCoopMessage msg) throws SPCoopException {
		
		IntegrationManagerOpenspcoop2 imOpenSPCoop2 = new IntegrationManagerOpenspcoop2(this.getHttpServletRequest(), this.getHttpServletResponse());
		
		try{
			return toSPCoopMessage(imOpenSPCoop2.invocaPortaDelegata(portaDelegata, toIntegrationManagerMessage(msg)));
		}catch(IntegrationManagerException imException){
			throw toSPCoopException(imException);
		}

	}


	/**
	 * Invoca una porta delegata per riferimento
	 *
	 * @param portaDelegata Porta Delegata da invocare
	 * @param msg SPCoopMessage da utilizza come messaggio da invocare (contiene anche il servizio che si desidera invocare)
	 * @param riferimentoMessaggio ID che identifica un Messaggio da utilizzare
	 * 
	 * @return un SPCoopMessage contenente il messaggio di risposta 
	 * 
	 */
	@Override
	public SPCoopMessage invocaPortaDelegataPerRiferimento(String portaDelegata,
			SPCoopMessage msg,String riferimentoMessaggio) throws SPCoopException {
		
		IntegrationManagerOpenspcoop2 imOpenSPCoop2 = new IntegrationManagerOpenspcoop2(this.getHttpServletRequest(), this.getHttpServletResponse());
		
		try{
			return toSPCoopMessage(imOpenSPCoop2.invocaPortaDelegataPerRiferimento(portaDelegata, toIntegrationManagerMessage(msg), riferimentoMessaggio));
		}catch(IntegrationManagerException imException){
			throw toSPCoopException(imException);
		}

	}


	/**
	 * Invia una risposta asicrona a OpenSPCoop
	 * @param portaDelegata Porta Delegata da invocare
	 * @param msg SPCoopMessage da utilizza come messaggio da invocare (contiene anche il servizio che si desidera invocare)
	 * @return un SPCoopMessage contenente il messaggio di risposta 
	 * 
	 */
	@Override
	public SPCoopMessage sendRispostaAsincronaSimmetrica(String portaDelegata,SPCoopMessage msg) throws SPCoopException {
		
		IntegrationManagerOpenspcoop2 imOpenSPCoop2 = new IntegrationManagerOpenspcoop2(this.getHttpServletRequest(), this.getHttpServletResponse());
		
		try{
			return toSPCoopMessage(imOpenSPCoop2.sendRispostaAsincronaSimmetrica(portaDelegata, toIntegrationManagerMessage(msg)));
		}catch(IntegrationManagerException imException){
			throw toSPCoopException(imException);
		}

	}

	/**
	 * Invia una risposta asicrona a OpenSPCoop
	 * @param portaDelegata Porta Delegata da invocare
	 * @param msg SPCoopMessage da utilizza come messaggio da invocare (contiene anche il servizio che si desidera invocare)
	 * @return un SPCoopMessage contenente il messaggio di risposta 
	 * 
	 */
	@Override
	public SPCoopMessage sendRichiestaStatoAsincronaAsimmetrica(String portaDelegata,SPCoopMessage msg) throws SPCoopException {
		
		IntegrationManagerOpenspcoop2 imOpenSPCoop2 = new IntegrationManagerOpenspcoop2(this.getHttpServletRequest(), this.getHttpServletResponse());
		
		try{
			return toSPCoopMessage(imOpenSPCoop2.sendRichiestaStatoAsincronaAsimmetrica(portaDelegata, toIntegrationManagerMessage(msg)));
		}catch(IntegrationManagerException imException){
			throw toSPCoopException(imException);
		}
	}



}
