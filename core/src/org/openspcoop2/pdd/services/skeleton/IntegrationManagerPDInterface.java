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
 
@javax.jws.WebService(targetNamespace = "http://services.pdd.openspcoop2.org", name = "PD")

public interface IntegrationManagerPDInterface {

   
    
	/* ------- Invocazione Porta Delegata ---------------*/
    
	@javax.xml.ws.ResponseWrapper(localName = "invocaPortaDelegataResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.InvocaPortaDelegataResponse")
    @javax.xml.ws.RequestWrapper(localName = "invocaPortaDelegata", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.InvocaPortaDelegata")
	@javax.jws.WebResult(name = "integrationManagerMessage", targetNamespace = "http://services.pdd.openspcoop2.org")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
	@javax.jws.WebMethod(action="invocaPortaDelegata",operationName="invocaPortaDelegata")
    public IntegrationManagerMessage invocaPortaDelegata(
    	@javax.jws.WebParam(name = "portaDelegata", targetNamespace = "http://services.pdd.openspcoop2.org")
    	java.lang.String portaDelegata,
        @javax.jws.WebParam(name = "msg", targetNamespace = "http://services.pdd.openspcoop2.org")
        IntegrationManagerMessage msg
    ) throws IntegrationManagerException;


    @javax.xml.ws.ResponseWrapper(localName = "sendRispostaAsincronaSimmetricaResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.SendRispostaAsincronaSimmetricaResponse")
    @javax.xml.ws.RequestWrapper(localName = "sendRispostaAsincronaSimmetrica", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.SendRispostaAsincronaSimmetrica")
    @javax.jws.WebResult(name = "integrationManagerMessage", targetNamespace = "http://services.pdd.openspcoop2.org")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
	@javax.jws.WebMethod(action="sendRispostaAsincronaSimmetrica",operationName="sendRispostaAsincronaSimmetrica")
    public IntegrationManagerMessage sendRispostaAsincronaSimmetrica(
    	@javax.jws.WebParam(name = "portaDelegata", targetNamespace = "http://services.pdd.openspcoop2.org")
    	java.lang.String portaDelegata,
        @javax.jws.WebParam(name = "msg", targetNamespace = "http://services.pdd.openspcoop2.org")
        IntegrationManagerMessage msg
    ) throws IntegrationManagerException;


    @javax.xml.ws.ResponseWrapper(localName = "sendRichiestaStatoAsincronaAsimmetricaResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.SendRichiestaStatoAsincronaAsimmetricaResponse")
    @javax.xml.ws.RequestWrapper(localName = "sendRichiestaStatoAsincronaAsimmetrica", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.SendRichiestaStatoAsincronaAsimmetrica")
    @javax.jws.WebResult(name = "integrationManagerMessage", targetNamespace = "http://services.pdd.openspcoop2.org")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
	@javax.jws.WebMethod(action="sendRichiestaStatoAsincronaAsimmetrica",operationName="sendRichiestaStatoAsincronaAsimmetrica")
    public IntegrationManagerMessage sendRichiestaStatoAsincronaAsimmetrica(
    	@javax.jws.WebParam(name = "portaDelegata", targetNamespace = "http://services.pdd.openspcoop2.org")
    	java.lang.String portaDelegata,
        @javax.jws.WebParam(name = "msg", targetNamespace = "http://services.pdd.openspcoop2.org")
        IntegrationManagerMessage msg
    ) throws IntegrationManagerException;

    
    @javax.xml.ws.ResponseWrapper(localName = "invocaPortaDelegataPerRiferimentoResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.InvocaPortaDelegataPerRiferimentoResponse")
    @javax.xml.ws.RequestWrapper(localName = "invocaPortaDelegataPerRiferimento", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.InvocaPortaDelegataPerRiferimento")
    @javax.jws.WebResult(name = "integrationManagerMessage", targetNamespace = "http://services.pdd.openspcoop2.org")
    @javax.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
	@javax.jws.WebMethod(action="invocaPortaDelegataPerRiferimento",operationName="invocaPortaDelegataPerRiferimento")
    public IntegrationManagerMessage invocaPortaDelegataPerRiferimento(
    	@javax.jws.WebParam(name = "portaDelegata", targetNamespace = "http://services.pdd.openspcoop2.org")
    	java.lang.String portaDelegata,
        @javax.jws.WebParam(name = "msg", targetNamespace = "http://services.pdd.openspcoop2.org")
        IntegrationManagerMessage msg,
        @javax.jws.WebParam(name = "riferimentoMessaggio", targetNamespace = "http://services.pdd.openspcoop2.org")
        java.lang.String riferimentoMessaggio
    ) throws IntegrationManagerException;
}
