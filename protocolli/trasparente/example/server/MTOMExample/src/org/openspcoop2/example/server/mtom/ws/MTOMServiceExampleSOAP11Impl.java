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

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package org.openspcoop2.example.server.mtom.ws;

/**
 * MTOMServiceExampleSOAP11Impl
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@javax.jws.WebService(
                      serviceName = "MTOMServiceExampleSOAP11Service",
                      portName = "MTOMServiceExampleSOAP11InterfaceEndpoint",
                      targetNamespace = "http://www.openspcoop2.org/example/server/mtom/ws",
                      wsdlLocation = "configurazionePdD/wsdl/implementazioneErogatoreSoap11.wsdl",
                      endpointInterface = "org.openspcoop2.example.server.mtom.ws.MTOMServiceExample")
                      
public class MTOMServiceExampleSOAP11Impl implements MTOMServiceExample {

    /* (non-Javadoc)
     * @see org.openspcoop2.example.server.mtom.ws.MTOMServiceExample#echo(java.lang.String  richiesta ,)javax.xml.transform.Source  imageData ,)java.util.List<javax.activation.DataHandler>  other ,)java.lang.String  risposta ,)javax.xml.transform.Source  imageDataResponse ,)java.util.List<javax.activation.DataHandler>  otherResponse )*
     */
    @Override
	public void echo(java.lang.String richiesta,javax.xml.transform.Source imageData,java.util.List<javax.activation.DataHandler> other,javax.xml.ws.Holder<java.lang.String> risposta,javax.xml.ws.Holder<javax.xml.transform.Source> imageDataResponse,javax.xml.ws.Holder<java.util.List<javax.activation.DataHandler>> otherResponse) { 
    	MTOMServiceExampleImpl.echo(richiesta, imageData, other, risposta, imageDataResponse, otherResponse);
    }

}
