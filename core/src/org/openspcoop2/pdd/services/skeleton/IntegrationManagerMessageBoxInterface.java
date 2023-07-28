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


package org.openspcoop2.pdd.services.skeleton;

import java.util.List;

import jakarta.jws.soap.SOAPBinding.ParameterStyle;
import jakarta.jws.soap.SOAPBinding.Style;
import jakarta.jws.soap.SOAPBinding.Use;


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
 
@jakarta.jws.WebService(targetNamespace = "http://services.pdd.openspcoop2.org", name = "MessageBox")

public interface IntegrationManagerMessageBoxInterface {

   
	
	/*-------- getAllMessagesID ----*/
	
    @jakarta.xml.ws.ResponseWrapper(localName = "getAllMessagesIdResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.GetAllMessagesIdResponse")
    @jakarta.xml.ws.RequestWrapper(localName = "getAllMessagesId", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.GetAllMessagesId")
    @jakarta.jws.WebResult(name = "messageId", targetNamespace = "http://services.pdd.openspcoop2.org")
    @jakarta.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
   	@jakarta.jws.WebMethod(action="getAllMessagesId",operationName="getAllMessagesId")
    public List<String> getAllMessagesId() throws IntegrationManagerException;

    @jakarta.xml.ws.ResponseWrapper(localName = "getAllMessagesIdByServiceResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.GetAllMessagesIdByServiceResponse")
    @jakarta.xml.ws.RequestWrapper(localName = "getAllMessagesIdByService", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.GetAllMessagesIdByService")
    @jakarta.jws.WebResult(name = "messageId", targetNamespace = "http://services.pdd.openspcoop2.org")
    @jakarta.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
   	@jakarta.jws.WebMethod(action="getAllMessagesIdByService",operationName="getAllMessagesIdByService")
    public List<String> getAllMessagesIdByService(
    	@jakarta.jws.WebParam(name = "tipoServizio", targetNamespace = "http://services.pdd.openspcoop2.org")
    	java.lang.String tipoServizio,
        @jakarta.jws.WebParam(name = "servizio", targetNamespace = "http://services.pdd.openspcoop2.org")
        java.lang.String servizio,
        @jakarta.jws.WebParam(name = "azione", targetNamespace = "http://services.pdd.openspcoop2.org")
        java.lang.String azione
    ) throws IntegrationManagerException;
	
    @jakarta.xml.ws.ResponseWrapper(localName = "getNextMessagesIdResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.GetNextMessagesIdResponse")
    @jakarta.xml.ws.RequestWrapper(localName = "getNextMessagesId", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.GetNextMessagesId")
    @jakarta.jws.WebResult(name = "messageId", targetNamespace = "http://services.pdd.openspcoop2.org")
    @jakarta.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
   	@jakarta.jws.WebMethod(action="getNextMessagesId",operationName="getNextMessagesId")
    public List<String> getNextMessagesId(
    	@jakarta.jws.WebParam(name = "counter", targetNamespace = "http://services.pdd.openspcoop2.org")
    	int counter
    ) throws IntegrationManagerException;
    
    @jakarta.xml.ws.ResponseWrapper(localName = "getNextMessagesIdByServiceResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.GetNextMessagesIdByServiceResponse")
    @jakarta.xml.ws.RequestWrapper(localName = "getNextMessagesIdByService", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.GetNextMessagesIdByService")
    @jakarta.jws.WebResult(name = "messageId", targetNamespace = "http://services.pdd.openspcoop2.org")
    @jakarta.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
   	@jakarta.jws.WebMethod(action="getNextMessagesIdByService",operationName="getNextMessagesIdByService")
    public List<String> getNextMessagesIdByService(
    	@jakarta.jws.WebParam(name = "counter", targetNamespace = "http://services.pdd.openspcoop2.org")
    	int counter,
        @jakarta.jws.WebParam(name = "tipoServizio", targetNamespace = "http://services.pdd.openspcoop2.org")
        java.lang.String tipoServizio,
        @jakarta.jws.WebParam(name = "servizio", targetNamespace = "http://services.pdd.openspcoop2.org")
        java.lang.String servizio,
        @jakarta.jws.WebParam(name = "azione", targetNamespace = "http://services.pdd.openspcoop2.org")
        java.lang.String azione
    ) throws IntegrationManagerException;
    
    @jakarta.xml.ws.ResponseWrapper(localName = "getMessagesIdArrayResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.getMessagesIdArrayResponse")
    @jakarta.xml.ws.RequestWrapper(localName = "getMessagesIdArray", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.getMessagesIdArray")
    @jakarta.jws.WebResult(name = "messageId", targetNamespace = "http://services.pdd.openspcoop2.org")
    @jakarta.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
   	@jakarta.jws.WebMethod(action="getMessagesIdArray",operationName="getMessagesIdArray")
    public List<String> getMessagesIdArray(
        @jakarta.jws.WebParam(name = "offset", targetNamespace = "http://services.pdd.openspcoop2.org")
        int offset,
    	@jakarta.jws.WebParam(name = "counter", targetNamespace = "http://services.pdd.openspcoop2.org")
    	int counter
    ) throws IntegrationManagerException;
    
    @jakarta.xml.ws.ResponseWrapper(localName = "getMessagesIdArrayByServiceResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.getMessagesIdArrayByServiceResponse")
    @jakarta.xml.ws.RequestWrapper(localName = "getMessagesIdArrayByService", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.getMessagesIdArrayByService")
    @jakarta.jws.WebResult(name = "messageId", targetNamespace = "http://services.pdd.openspcoop2.org")
    @jakarta.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
   	@jakarta.jws.WebMethod(action="getMessagesIdArrayByService",operationName="getMessagesIdArrayByService")
    public List<String> getMessagesIdArrayByService(
        @jakarta.jws.WebParam(name = "offset", targetNamespace = "http://services.pdd.openspcoop2.org")
        int offset,
    	@jakarta.jws.WebParam(name = "counter", targetNamespace = "http://services.pdd.openspcoop2.org")
    	int counter,
        @jakarta.jws.WebParam(name = "tipoServizio", targetNamespace = "http://services.pdd.openspcoop2.org")
        java.lang.String tipoServizio,
        @jakarta.jws.WebParam(name = "servizio", targetNamespace = "http://services.pdd.openspcoop2.org")
        java.lang.String servizio,
        @jakarta.jws.WebParam(name = "azione", targetNamespace = "http://services.pdd.openspcoop2.org")
        java.lang.String azione
    ) throws IntegrationManagerException;
    
    
    
    
    /* ------ Get ----- */
  
    @jakarta.xml.ws.ResponseWrapper(localName = "getMessageResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.GetMessageResponse")
    @jakarta.xml.ws.RequestWrapper(localName = "getMessage", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.GetMessage")
    @jakarta.jws.WebResult(name = "integrationManagerMessage", targetNamespace = "http://services.pdd.openspcoop2.org")
    @jakarta.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
   	@jakarta.jws.WebMethod(action="getMessage",operationName="getMessage")
    public IntegrationManagerMessage getMessage(
    	@jakarta.jws.WebParam(name = "idMessaggio", targetNamespace = "http://services.pdd.openspcoop2.org")
    	java.lang.String idMessaggio
    ) throws IntegrationManagerException;
    
    @jakarta.xml.ws.ResponseWrapper(localName = "getMessageByReferenceResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.GetMessageByReferenceResponse")
    @jakarta.xml.ws.RequestWrapper(localName = "getMessageByReference", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.GetMessageByReference")
    @jakarta.jws.WebResult(name = "integrationManagerMessage", targetNamespace = "http://services.pdd.openspcoop2.org")
    @jakarta.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
   	@jakarta.jws.WebMethod(action="getMessageByReference",operationName="getMessageByReference")
    public IntegrationManagerMessage getMessageByReference(
    	@jakarta.jws.WebParam(name = "riferimentoMsg", targetNamespace = "http://services.pdd.openspcoop2.org")
    	java.lang.String riferimentoMsg
    ) throws IntegrationManagerException;
	
    
    
    
    /* -------- delete ------ */
    
    @jakarta.xml.ws.ResponseWrapper(localName = "deleteMessageResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.DeleteMessageResponse")
    @jakarta.xml.ws.RequestWrapper(localName = "deleteMessage", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.DeleteMessage")
    @jakarta.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
   	@jakarta.jws.WebMethod(action="deleteMessage",operationName="deleteMessage")
    public void deleteMessage(
    	@jakarta.jws.WebParam(name = "idMessaggio", targetNamespace = "http://services.pdd.openspcoop2.org")
    	java.lang.String idMessaggio
    ) throws IntegrationManagerException;

    @jakarta.xml.ws.ResponseWrapper(localName = "deleteMessageByReferenceResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.DeleteMessageByReferenceResponse")
    @jakarta.xml.ws.RequestWrapper(localName = "deleteMessageByReference", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.DeleteMessageByReference")
    @jakarta.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
   	@jakarta.jws.WebMethod(action="deleteMessageByReference",operationName="deleteMessageByReference")
    public void deleteMessageByReference(
    	@jakarta.jws.WebParam(name = "riferimentoMsg", targetNamespace = "http://services.pdd.openspcoop2.org")
    	java.lang.String riferimentoMsg
    ) throws IntegrationManagerException;   
    
    
    
    
	/* --------- delete All Messages ----------- */
    
    @jakarta.xml.ws.ResponseWrapper(localName = "deleteAllMessagesResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.DeleteAllMessagesResponse")
    @jakarta.xml.ws.RequestWrapper(localName = "deleteAllMessages", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.DeleteAllMessages")
    @jakarta.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
   	@jakarta.jws.WebMethod(action="deleteAllMessages",operationName="deleteAllMessages")
    public void deleteAllMessages() throws IntegrationManagerException;
    
    
    
}
