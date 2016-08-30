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

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

import org.openspcoop2.example.pdd.server.trasparente.comunicazionevariazione.ComunicazioneVariazione;
import org.openspcoop2.example.pdd.server.trasparente.comunicazionevariazione.ComunicazioneVariazioneService11;

/**
 * ComunicazioneVariazioneService11
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@WebServiceClient(name = "ComunicazioneVariazioneSOAP11Service", 
                  wsdlLocation = "file:configurazionePdD/wsdl/implementazioneErogatoreSoap11.wsdl",
                  targetNamespace = "http://openspcoop2.org/example/pdd/server/ComunicazioneVariazione") 
public class ComunicazioneVariazioneService11 extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://openspcoop2.org/example/pdd/server/ComunicazioneVariazione", "ComunicazioneVariazioneSOAP11Service");
    public final static QName ComunicazioneVariazioneInterfaceEndpoint = new QName("http://openspcoop2.org/example/pdd/server/ComunicazioneVariazione", "ComunicazioneVariazioneSOAP11InterfaceEndpoint");
    static {
        URL url = ComunicazioneVariazioneService11.class.getResource("configurazionePdD/wsdl/implementazioneErogatoreSoap11.wsdl");
        if (url == null) {
            url = ComunicazioneVariazioneService11.class.getClassLoader().getResource("configurazionePdD/wsdl/implementazioneErogatoreSoap11.wsdl");
        } 
        if (url == null) {
            System.out.println("Can not initialize the default wsdl from configurazionePdD/wsdl/implementazioneErogatoreSoap11.wsdl");
        }       
        WSDL_LOCATION = url;
    }

    public ComunicazioneVariazioneService11(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public ComunicazioneVariazioneService11(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ComunicazioneVariazioneService11() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    /**
     *
     * @return
     *     returns ComunicazioneVariazione
     */
    @WebEndpoint(name = "ComunicazioneVariazioneSOAP11InterfaceEndpoint")
    public ComunicazioneVariazione getComunicazioneVariazioneInterfaceEndpoint() {
        return super.getPort(ComunicazioneVariazioneInterfaceEndpoint, ComunicazioneVariazione.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ComunicazioneVariazione
     */
    @WebEndpoint(name = "ComunicazioneVariazioneSOAP11InterfaceEndpoint")
    public ComunicazioneVariazione getComunicazioneVariazioneInterfaceEndpoint(WebServiceFeature... features) {
        return super.getPort(ComunicazioneVariazioneInterfaceEndpoint, ComunicazioneVariazione.class, features);
    }

}
