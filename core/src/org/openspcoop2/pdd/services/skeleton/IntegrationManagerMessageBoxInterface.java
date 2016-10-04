/*
 * OpenSPCoop - Customizable API Gateway 
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


package org.openspcoop2.pdd.services.skeleton;

import java.util.List;

import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;


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
 
@javax.jws.WebService(targetNamespace = "http://services.pdd.openspcoop2.org", name = "MessageBox")

public interface IntegrationManagerMessageBoxInterface {

   
	
	/*-------- getAllMessagesID ----*/
	
    @javax.xml.ws.ResponseWrapper(localName = "getAllMessagesIdResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.GetAllMessagesIdResponse")
    @javax.xml.ws.RequestWrapper(localName = "getAllMessagesId", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.GetAllMessagesId")
    @javax.jws.WebResult(name = "messageId", targetNamespace = "http://services.pdd.openspcoop2.org")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
   	@javax.jws.WebMethod(action="getAllMessagesId",operationName="getAllMessagesId")
    public List<String> getAllMessagesId() throws IntegrationManagerException;

    @javax.xml.ws.ResponseWrapper(localName = "getAllMessagesIdByServiceResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.GetAllMessagesIdByServiceResponse")
    @javax.xml.ws.RequestWrapper(localName = "getAllMessagesIdByService", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.GetAllMessagesIdByService")
    @javax.jws.WebResult(name = "messageId", targetNamespace = "http://services.pdd.openspcoop2.org")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
   	@javax.jws.WebMethod(action="getAllMessagesIdByService",operationName="getAllMessagesIdByService")
    public List<String> getAllMessagesIdByService(
    	@javax.jws.WebParam(name = "tipoServizio", targetNamespace = "http://services.pdd.openspcoop2.org")
    	java.lang.String tipoServizio,
        @javax.jws.WebParam(name = "servizio", targetNamespace = "http://services.pdd.openspcoop2.org")
        java.lang.String servizio,
        @javax.jws.WebParam(name = "azione", targetNamespace = "http://services.pdd.openspcoop2.org")
        java.lang.String azione
    ) throws IntegrationManagerException;
	
    @javax.xml.ws.ResponseWrapper(localName = "getNextMessagesIdResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.GetNextMessagesIdResponse")
    @javax.xml.ws.RequestWrapper(localName = "getNextMessagesId", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.GetNextMessagesId")
    @javax.jws.WebResult(name = "messageId", targetNamespace = "http://services.pdd.openspcoop2.org")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
   	@javax.jws.WebMethod(action="getNextMessagesId",operationName="getNextMessagesId")
    public List<String> getNextMessagesId(
    	@javax.jws.WebParam(name = "counter", targetNamespace = "http://services.pdd.openspcoop2.org")
    	int counter
    ) throws IntegrationManagerException;
    
    @javax.xml.ws.ResponseWrapper(localName = "getNextMessagesIdByServiceResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.GetNextMessagesIdByServiceResponse")
    @javax.xml.ws.RequestWrapper(localName = "getNextMessagesIdByService", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.GetNextMessagesIdByService")
    @javax.jws.WebResult(name = "messageId", targetNamespace = "http://services.pdd.openspcoop2.org")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
   	@javax.jws.WebMethod(action="getNextMessagesIdByService",operationName="getNextMessagesIdByService")
    public List<String> getNextMessagesIdByService(
    	@javax.jws.WebParam(name = "counter", targetNamespace = "http://services.pdd.openspcoop2.org")
    	int counter,
        @javax.jws.WebParam(name = "tipoServizio", targetNamespace = "http://services.pdd.openspcoop2.org")
        java.lang.String tipoServizio,
        @javax.jws.WebParam(name = "servizio", targetNamespace = "http://services.pdd.openspcoop2.org")
        java.lang.String servizio,
        @javax.jws.WebParam(name = "azione", targetNamespace = "http://services.pdd.openspcoop2.org")
        java.lang.String azione
    ) throws IntegrationManagerException;
    
    @javax.xml.ws.ResponseWrapper(localName = "getMessagesIdArrayResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.getMessagesIdArrayResponse")
    @javax.xml.ws.RequestWrapper(localName = "getMessagesIdArray", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.getMessagesIdArray")
    @javax.jws.WebResult(name = "messageId", targetNamespace = "http://services.pdd.openspcoop2.org")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
   	@javax.jws.WebMethod(action="getMessagesIdArray",operationName="getMessagesIdArray")
    public List<String> getMessagesIdArray(
        @javax.jws.WebParam(name = "offset", targetNamespace = "http://services.pdd.openspcoop2.org")
        int offset,
    	@javax.jws.WebParam(name = "counter", targetNamespace = "http://services.pdd.openspcoop2.org")
    	int counter
    ) throws IntegrationManagerException;
    
    @javax.xml.ws.ResponseWrapper(localName = "getMessagesIdArrayByServiceResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.getMessagesIdArrayByServiceResponse")
    @javax.xml.ws.RequestWrapper(localName = "getMessagesIdArrayByService", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.getMessagesIdArrayByService")
    @javax.jws.WebResult(name = "messageId", targetNamespace = "http://services.pdd.openspcoop2.org")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
   	@javax.jws.WebMethod(action="getMessagesIdArrayByService",operationName="getMessagesIdArrayByService")
    public List<String> getMessagesIdArrayByService(
        @javax.jws.WebParam(name = "offset", targetNamespace = "http://services.pdd.openspcoop2.org")
        int offset,
    	@javax.jws.WebParam(name = "counter", targetNamespace = "http://services.pdd.openspcoop2.org")
    	int counter,
        @javax.jws.WebParam(name = "tipoServizio", targetNamespace = "http://services.pdd.openspcoop2.org")
        java.lang.String tipoServizio,
        @javax.jws.WebParam(name = "servizio", targetNamespace = "http://services.pdd.openspcoop2.org")
        java.lang.String servizio,
        @javax.jws.WebParam(name = "azione", targetNamespace = "http://services.pdd.openspcoop2.org")
        java.lang.String azione
    ) throws IntegrationManagerException;
    
    
    
    
    /* ------ Get ----- */
  
    @javax.xml.ws.ResponseWrapper(localName = "getMessageResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.GetMessageResponse")
    @javax.xml.ws.RequestWrapper(localName = "getMessage", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.GetMessage")
    @javax.jws.WebResult(name = "integrationManagerMessage", targetNamespace = "http://services.pdd.openspcoop2.org")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
   	@javax.jws.WebMethod(action="getMessage",operationName="getMessage")
    public IntegrationManagerMessage getMessage(
    	@javax.jws.WebParam(name = "idMessaggio", targetNamespace = "http://services.pdd.openspcoop2.org")
    	java.lang.String idMessaggio
    ) throws IntegrationManagerException;
    
    @javax.xml.ws.ResponseWrapper(localName = "getMessageByReferenceResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.GetMessageByReferenceResponse")
    @javax.xml.ws.RequestWrapper(localName = "getMessageByReference", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.GetMessageByReference")
    @javax.jws.WebResult(name = "integrationManagerMessage", targetNamespace = "http://services.pdd.openspcoop2.org")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
   	@javax.jws.WebMethod(action="getMessageByReference",operationName="getMessageByReference")
    public IntegrationManagerMessage getMessageByReference(
    	@javax.jws.WebParam(name = "riferimentoMsg", targetNamespace = "http://services.pdd.openspcoop2.org")
    	java.lang.String riferimentoMsg
    ) throws IntegrationManagerException;
	
    
    
    
    /* -------- delete ------ */
    
    @javax.xml.ws.ResponseWrapper(localName = "deleteMessageResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.DeleteMessageResponse")
    @javax.xml.ws.RequestWrapper(localName = "deleteMessage", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.DeleteMessage")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
   	@javax.jws.WebMethod(action="deleteMessage",operationName="deleteMessage")
    public void deleteMessage(
    	@javax.jws.WebParam(name = "idMessaggio", targetNamespace = "http://services.pdd.openspcoop2.org")
    	java.lang.String idMessaggio
    ) throws IntegrationManagerException;

    @javax.xml.ws.ResponseWrapper(localName = "deleteMessageByReferenceResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.DeleteMessageByReferenceResponse")
    @javax.xml.ws.RequestWrapper(localName = "deleteMessageByReference", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.DeleteMessageByReference")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
   	@javax.jws.WebMethod(action="deleteMessageByReference",operationName="deleteMessageByReference")
    public void deleteMessageByReference(
    	@javax.jws.WebParam(name = "riferimentoMsg", targetNamespace = "http://services.pdd.openspcoop2.org")
    	java.lang.String riferimentoMsg
    ) throws IntegrationManagerException;   
    
    
    
    
	/* --------- delete All Messages ----------- */
    
    @javax.xml.ws.ResponseWrapper(localName = "deleteAllMessagesResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.DeleteAllMessagesResponse")
    @javax.xml.ws.RequestWrapper(localName = "deleteAllMessages", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.DeleteAllMessages")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
   	@javax.jws.WebMethod(action="deleteAllMessages",operationName="deleteAllMessages")
    public void deleteAllMessages() throws IntegrationManagerException;
    
    
    
}
