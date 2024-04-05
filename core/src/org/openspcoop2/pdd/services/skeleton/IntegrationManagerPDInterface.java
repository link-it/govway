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


package org.openspcoop2.pdd.services.skeleton;

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
 
@jakarta.jws.WebService(targetNamespace = "http://services.pdd.openspcoop2.org", name = "PD")

public interface IntegrationManagerPDInterface {

   
    
	/* ------- Invocazione Porta Delegata ---------------*/
    
	@jakarta.xml.ws.ResponseWrapper(localName = "invocaPortaDelegataResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.InvocaPortaDelegataResponse")
    @jakarta.xml.ws.RequestWrapper(localName = "invocaPortaDelegata", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.InvocaPortaDelegata")
	@jakarta.jws.WebResult(name = "integrationManagerMessage", targetNamespace = "http://services.pdd.openspcoop2.org")
    @jakarta.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
	@jakarta.jws.WebMethod(action="invocaPortaDelegata",operationName="invocaPortaDelegata")
    public IntegrationManagerMessage invocaPortaDelegata(
    	@jakarta.jws.WebParam(name = "portaDelegata", targetNamespace = "http://services.pdd.openspcoop2.org")
    	java.lang.String portaDelegata,
        @jakarta.jws.WebParam(name = "msg", targetNamespace = "http://services.pdd.openspcoop2.org")
        IntegrationManagerMessage msg
    ) throws IntegrationManagerException;


    @jakarta.xml.ws.ResponseWrapper(localName = "sendRispostaAsincronaSimmetricaResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.SendRispostaAsincronaSimmetricaResponse")
    @jakarta.xml.ws.RequestWrapper(localName = "sendRispostaAsincronaSimmetrica", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.SendRispostaAsincronaSimmetrica")
    @jakarta.jws.WebResult(name = "integrationManagerMessage", targetNamespace = "http://services.pdd.openspcoop2.org")
    @jakarta.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
	@jakarta.jws.WebMethod(action="sendRispostaAsincronaSimmetrica",operationName="sendRispostaAsincronaSimmetrica")
    public IntegrationManagerMessage sendRispostaAsincronaSimmetrica(
    	@jakarta.jws.WebParam(name = "portaDelegata", targetNamespace = "http://services.pdd.openspcoop2.org")
    	java.lang.String portaDelegata,
        @jakarta.jws.WebParam(name = "msg", targetNamespace = "http://services.pdd.openspcoop2.org")
        IntegrationManagerMessage msg
    ) throws IntegrationManagerException;


    @jakarta.xml.ws.ResponseWrapper(localName = "sendRichiestaStatoAsincronaAsimmetricaResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.SendRichiestaStatoAsincronaAsimmetricaResponse")
    @jakarta.xml.ws.RequestWrapper(localName = "sendRichiestaStatoAsincronaAsimmetrica", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.SendRichiestaStatoAsincronaAsimmetrica")
    @jakarta.jws.WebResult(name = "integrationManagerMessage", targetNamespace = "http://services.pdd.openspcoop2.org")
    @jakarta.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
	@jakarta.jws.WebMethod(action="sendRichiestaStatoAsincronaAsimmetrica",operationName="sendRichiestaStatoAsincronaAsimmetrica")
    public IntegrationManagerMessage sendRichiestaStatoAsincronaAsimmetrica(
    	@jakarta.jws.WebParam(name = "portaDelegata", targetNamespace = "http://services.pdd.openspcoop2.org")
    	java.lang.String portaDelegata,
        @jakarta.jws.WebParam(name = "msg", targetNamespace = "http://services.pdd.openspcoop2.org")
        IntegrationManagerMessage msg
    ) throws IntegrationManagerException;

    
    @jakarta.xml.ws.ResponseWrapper(localName = "invocaPortaDelegataPerRiferimentoResponse", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.InvocaPortaDelegataPerRiferimentoResponse")
    @jakarta.xml.ws.RequestWrapper(localName = "invocaPortaDelegataPerRiferimento", targetNamespace = "http://services.pdd.openspcoop2.org", className = "org.openspcoop2.pdd.services.skeleton.InvocaPortaDelegataPerRiferimento")
    @jakarta.jws.WebResult(name = "integrationManagerMessage", targetNamespace = "http://services.pdd.openspcoop2.org")
    @jakarta.jws.soap.SOAPBinding(parameterStyle=ParameterStyle.WRAPPED,style=Style.DOCUMENT,use=Use.LITERAL)
	@jakarta.jws.WebMethod(action="invocaPortaDelegataPerRiferimento",operationName="invocaPortaDelegataPerRiferimento")
    public IntegrationManagerMessage invocaPortaDelegataPerRiferimento(
    	@jakarta.jws.WebParam(name = "portaDelegata", targetNamespace = "http://services.pdd.openspcoop2.org")
    	java.lang.String portaDelegata,
        @jakarta.jws.WebParam(name = "msg", targetNamespace = "http://services.pdd.openspcoop2.org")
        IntegrationManagerMessage msg,
        @jakarta.jws.WebParam(name = "riferimentoMessaggio", targetNamespace = "http://services.pdd.openspcoop2.org")
        java.lang.String riferimentoMessaggio
    ) throws IntegrationManagerException;
}
