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
 
@javax.jws.WebService(targetNamespace = "http://services.pdd.openspcoop.org", name = "IntegrationManager")

public interface IntegrationManagerInterface {

	public static String ID_MODULO = "IntegrationManager";
    
	
	/*-------- getAllMessagesID ----*/
	
    @javax.xml.ws.ResponseWrapper(localName = "getAllMessagesIdResponse", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.GetAllMessagesIdResponse")
    @javax.xml.ws.RequestWrapper(localName = "getAllMessagesId", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.GetAllMessagesId")
    @javax.jws.WebResult(name = "getAllMessagesIdReturn", targetNamespace = "http://services.pdd.openspcoop.org")
    @javax.jws.WebMethod
    public String []  getAllMessagesId() throws SPCoopException;

    @javax.xml.ws.ResponseWrapper(localName = "getAllMessagesIdByServiceResponse", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.GetAllMessagesIdByServiceResponse")
    @javax.xml.ws.RequestWrapper(localName = "getAllMessagesIdByService", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.GetAllMessagesIdByService")
    @javax.jws.WebResult(name = "getAllMessagesIdByServiceReturn", targetNamespace = "http://services.pdd.openspcoop.org")
    @javax.jws.WebMethod
    public String [] getAllMessagesIdByService(
    	@javax.jws.WebParam(name = "tipoServizio", targetNamespace = "http://services.pdd.openspcoop.org")
    	java.lang.String tipoServizio,
        @javax.jws.WebParam(name = "servizio", targetNamespace = "http://services.pdd.openspcoop.org")
        java.lang.String servizio,
        @javax.jws.WebParam(name = "azione", targetNamespace = "http://services.pdd.openspcoop.org")
        java.lang.String azione
    ) throws SPCoopException;
	
    @javax.xml.ws.ResponseWrapper(localName = "getNextMessagesIdResponse", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.GetNextMessagesIdResponse")
    @javax.xml.ws.RequestWrapper(localName = "getNextMessagesId", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.GetNextMessagesId")
    @javax.jws.WebResult(name = "getNextMessagesIdReturn", targetNamespace = "http://services.pdd.openspcoop.org")
    @javax.jws.WebMethod
    public String [] getNextMessagesId(
    	@javax.jws.WebParam(name = "counter", targetNamespace = "http://services.pdd.openspcoop.org")
    	int counter
    ) throws SPCoopException;
    
    @javax.xml.ws.ResponseWrapper(localName = "getNextMessagesIdByServiceResponse", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.GetNextMessagesIdByServiceResponse")
    @javax.xml.ws.RequestWrapper(localName = "getNextMessagesIdByService", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.GetNextMessagesIdByService")
    @javax.jws.WebResult(name = "getNextMessagesIdByServiceReturn", targetNamespace = "http://services.pdd.openspcoop.org")
    @javax.jws.WebMethod
    public String [] getNextMessagesIdByService(
    	@javax.jws.WebParam(name = "counter", targetNamespace = "http://services.pdd.openspcoop.org")
    	int counter,
        @javax.jws.WebParam(name = "tipoServizio", targetNamespace = "http://services.pdd.openspcoop.org")
        java.lang.String tipoServizio,
        @javax.jws.WebParam(name = "servizio", targetNamespace = "http://services.pdd.openspcoop.org")
        java.lang.String servizio,
        @javax.jws.WebParam(name = "azione", targetNamespace = "http://services.pdd.openspcoop.org")
        java.lang.String azione
    ) throws SPCoopException;
    
    @javax.xml.ws.ResponseWrapper(localName = "getMessagesIdArrayResponse", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.getMessagesIdArrayResponse")
    @javax.xml.ws.RequestWrapper(localName = "getMessagesIdArray", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.getMessagesIdArray")
    @javax.jws.WebResult(name = "getMessagesIdArrayReturn", targetNamespace = "http://services.pdd.openspcoop.org")
    @javax.jws.WebMethod
    public String [] getMessagesIdArray(
        @javax.jws.WebParam(name = "offset", targetNamespace = "http://services.pdd.openspcoop.org")
        int offset,
    	@javax.jws.WebParam(name = "counter", targetNamespace = "http://services.pdd.openspcoop.org")
    	int counter
    ) throws SPCoopException;
    
    @javax.xml.ws.ResponseWrapper(localName = "getMessagesIdArrayByServiceResponse", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.getMessagesIdArrayByServiceResponse")
    @javax.xml.ws.RequestWrapper(localName = "getMessagesIdArrayByService", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.getMessagesIdArrayByService")
    @javax.jws.WebResult(name = "getMessagesIdArrayByServiceReturn", targetNamespace = "http://services.pdd.openspcoop.org")
    @javax.jws.WebMethod
    public String [] getMessagesIdArrayByService(
        @javax.jws.WebParam(name = "offset", targetNamespace = "http://services.pdd.openspcoop.org")
        int offset,
    	@javax.jws.WebParam(name = "counter", targetNamespace = "http://services.pdd.openspcoop.org")
    	int counter,
        @javax.jws.WebParam(name = "tipoServizio", targetNamespace = "http://services.pdd.openspcoop.org")
        java.lang.String tipoServizio,
        @javax.jws.WebParam(name = "servizio", targetNamespace = "http://services.pdd.openspcoop.org")
        java.lang.String servizio,
        @javax.jws.WebParam(name = "azione", targetNamespace = "http://services.pdd.openspcoop.org")
        java.lang.String azione
    ) throws SPCoopException;
    
    
    
    
    /* ------ Get ----- */
  
    @javax.xml.ws.ResponseWrapper(localName = "getMessageResponse", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.GetMessageResponse")
    @javax.xml.ws.RequestWrapper(localName = "getMessage", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.GetMessage")
    @javax.jws.WebResult(name = "getMessageReturn", targetNamespace = "http://services.pdd.openspcoop.org")
    @javax.jws.WebMethod
    public SPCoopMessage getMessage(
    	@javax.jws.WebParam(name = "idEGov", targetNamespace = "http://services.pdd.openspcoop.org")
    	java.lang.String idEGov
    ) throws SPCoopException;
    
    @javax.xml.ws.ResponseWrapper(localName = "getMessageByReferenceResponse", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.GetMessageByReferenceResponse")
    @javax.xml.ws.RequestWrapper(localName = "getMessageByReference", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.GetMessageByReference")
    @javax.jws.WebResult(name = "getMessageByReferenceReturn", targetNamespace = "http://services.pdd.openspcoop.org")
    @javax.jws.WebMethod
    public SPCoopMessage getMessageByReference(
    	@javax.jws.WebParam(name = "riferimentoMsg", targetNamespace = "http://services.pdd.openspcoop.org")
    	java.lang.String riferimentoMsg
    ) throws SPCoopException;
	
    
    
    
    /* -------- delete ------ */
    
    @javax.xml.ws.ResponseWrapper(localName = "deleteMessageResponse", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.DeleteMessageResponse")
    @javax.xml.ws.RequestWrapper(localName = "deleteMessage", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.DeleteMessage")
    @javax.jws.WebMethod
    public void deleteMessage(
    	@javax.jws.WebParam(name = "idEGov", targetNamespace = "http://services.pdd.openspcoop.org")
    	java.lang.String idEGov
    ) throws SPCoopException;

    @javax.xml.ws.ResponseWrapper(localName = "deleteMessageByReferenceResponse", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.DeleteMessageByReferenceResponse")
    @javax.xml.ws.RequestWrapper(localName = "deleteMessageByReference", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.DeleteMessageByReference")
    @javax.jws.WebMethod
    public void deleteMessageByReference(
    	@javax.jws.WebParam(name = "riferimentoMsg", targetNamespace = "http://services.pdd.openspcoop.org")
    	java.lang.String riferimentoMsg
    ) throws SPCoopException;   
    
    
    
    
	/* --------- delete All Messages ----------- */
    
    @javax.xml.ws.ResponseWrapper(localName = "deleteAllMessagesResponse", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.DeleteAllMessagesResponse")
    @javax.xml.ws.RequestWrapper(localName = "deleteAllMessages", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.DeleteAllMessages")
    @javax.jws.WebMethod
    public void deleteAllMessages() throws SPCoopException;
    
    
    
    
    
	/* ------- Invocazione Porta Delegata ---------------*/
    
	@javax.xml.ws.ResponseWrapper(localName = "invocaPortaDelegataResponse", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.InvocaPortaDelegataResponse")
    @javax.xml.ws.RequestWrapper(localName = "invocaPortaDelegata", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.InvocaPortaDelegata")
    @javax.jws.WebResult(name = "invocaPortaDelegataReturn", targetNamespace = "http://services.pdd.openspcoop.org")
    @javax.jws.WebMethod
    public SPCoopMessage invocaPortaDelegata(
    	@javax.jws.WebParam(name = "portaDelegata", targetNamespace = "http://services.pdd.openspcoop.org")
    	java.lang.String portaDelegata,
        @javax.jws.WebParam(name = "msg", targetNamespace = "http://services.pdd.openspcoop.org")
        SPCoopMessage msg
    ) throws SPCoopException;


    @javax.xml.ws.ResponseWrapper(localName = "sendRispostaAsincronaSimmetricaResponse", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.SendRispostaAsincronaSimmetricaResponse")
    @javax.xml.ws.RequestWrapper(localName = "sendRispostaAsincronaSimmetrica", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.SendRispostaAsincronaSimmetrica")
    @javax.jws.WebResult(name = "sendRispostaAsincronaSimmetricaReturn", targetNamespace = "http://services.pdd.openspcoop.org")
    @javax.jws.WebMethod
    public SPCoopMessage sendRispostaAsincronaSimmetrica(
    	@javax.jws.WebParam(name = "portaDelegata", targetNamespace = "http://services.pdd.openspcoop.org")
    	java.lang.String portaDelegata,
        @javax.jws.WebParam(name = "msg", targetNamespace = "http://services.pdd.openspcoop.org")
        SPCoopMessage msg
    ) throws SPCoopException;


    @javax.xml.ws.ResponseWrapper(localName = "sendRichiestaStatoAsincronaAsimmetricaResponse", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.SendRichiestaStatoAsincronaAsimmetricaResponse")
    @javax.xml.ws.RequestWrapper(localName = "sendRichiestaStatoAsincronaAsimmetrica", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.SendRichiestaStatoAsincronaAsimmetrica")
    @javax.jws.WebResult(name = "sendRichiestaStatoAsincronaAsimmetricaReturn", targetNamespace = "http://services.pdd.openspcoop.org")
    @javax.jws.WebMethod
    public SPCoopMessage sendRichiestaStatoAsincronaAsimmetrica(
    	@javax.jws.WebParam(name = "portaDelegata", targetNamespace = "http://services.pdd.openspcoop.org")
    	java.lang.String portaDelegata,
        @javax.jws.WebParam(name = "msg", targetNamespace = "http://services.pdd.openspcoop.org")
        SPCoopMessage msg
    ) throws SPCoopException;

    
    @javax.xml.ws.ResponseWrapper(localName = "invocaPortaDelegataPerRiferimentoResponse", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.InvocaPortaDelegataPerRiferimentoResponse")
    @javax.xml.ws.RequestWrapper(localName = "invocaPortaDelegataPerRiferimento", targetNamespace = "http://services.pdd.openspcoop.org", className = "org.openspcoop.pdd.services.skeleton.InvocaPortaDelegataPerRiferimento")
    @javax.jws.WebResult(name = "invocaPortaDelegataPerRiferimentoReturn", targetNamespace = "http://services.pdd.openspcoop.org")
    @javax.jws.WebMethod
    public SPCoopMessage invocaPortaDelegataPerRiferimento(
    	@javax.jws.WebParam(name = "portaDelegata", targetNamespace = "http://services.pdd.openspcoop.org")
    	java.lang.String portaDelegata,
        @javax.jws.WebParam(name = "msg", targetNamespace = "http://services.pdd.openspcoop.org")
        SPCoopMessage msg,
        @javax.jws.WebParam(name = "riferimentoMessaggio", targetNamespace = "http://services.pdd.openspcoop.org")
        java.lang.String riferimentoMessaggio
    ) throws SPCoopException;
}
