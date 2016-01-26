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
package org.openspcoop2.example.pdd.server.trasparente.comunicazionevariazione;

/**
 * ComunicazioneVariazioneImplSec11
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@javax.jws.WebService(
                      serviceName = "ComunicazioneVariazioneSOAP11SecService",
                      portName = "ComunicazioneVariazioneSOAP11SecInterfaceEndpoint",
                      targetNamespace = "http://openspcoop2.org/example/pdd/server/ComunicazioneVariazione",
                      wsdlLocation = "file:configurazionePdD/wsdl/implementazioneErogatoreSoap11Security.wsdl",
                      endpointInterface = "org.openspcoop2.example.pdd.server.trasparente.comunicazionevariazione.ComunicazioneVariazione")
                      
public class ComunicazioneVariazioneImplSec11 extends ComunicazioneVariazioneCommonImpl {

}
