/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.example.pdd.server.trasparente.comunicazionevariazione;

import java.net.URL;
import javax.xml.namespace.QName;
import jakarta.xml.ws.WebEndpoint;
import jakarta.xml.ws.WebServiceClient;
import jakarta.xml.ws.WebServiceFeature;
import jakarta.xml.ws.Service;

/**
 * ComunicazioneVariazioneServiceSec11
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@WebServiceClient(name = "ComunicazioneVariazioneSOAP11SecService", 
                  wsdlLocation = "file:configurazionePdD/wsdl/implementazioneErogatoreSoap11Security.wsdl",
                  targetNamespace = "http://openspcoop2.org/example/pdd/server/ComunicazioneVariazione") 
public class ComunicazioneVariazioneServiceSec11 extends Service {

    public static final URL WSDL_LOCATION;

    public static final QName SERVICE = new QName("http://openspcoop2.org/example/pdd/server/ComunicazioneVariazione", "ComunicazioneVariazioneSOAP11SecService");
    public static final QName ComunicazioneVariazioneInterfaceEndpointSec = new QName("http://openspcoop2.org/example/pdd/server/ComunicazioneVariazione", "ComunicazioneVariazioneSOAP11SecInterfaceEndpoint");
    static {
        URL url = ComunicazioneVariazioneServiceSec11.class.getResource("configurazionePdD/wsdl/implementazioneErogatoreSoap11Security.wsdl");
        if (url == null) {
            url = ComunicazioneVariazioneServiceSec11.class.getClassLoader().getResource("configurazionePdD/wsdl/implementazioneErogatoreSoap11Security.wsdl");
        } 
        if (url == null) {
        	System.out.println("Can not initialize the default wsdl from configurazionePdD/wsdl/implementazioneErogatoreSoap11Security.wsdl");
        }       
        WSDL_LOCATION = url;
    }

    public ComunicazioneVariazioneServiceSec11(URL wsdlLocation) {
        super(wsdlLocation, ComunicazioneVariazioneServiceSec11.SERVICE);
    }

    public ComunicazioneVariazioneServiceSec11(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ComunicazioneVariazioneServiceSec11() {
        super(ComunicazioneVariazioneServiceSec11.WSDL_LOCATION, ComunicazioneVariazioneServiceSec11.SERVICE);
    }
    
    /**
     *
     * @return
     *     returns ComunicazioneVariazione
     */
    @WebEndpoint(name = "ComunicazioneVariazioneSOAP11SecInterfaceEndpoint")
    public ComunicazioneVariazione getComunicazioneVariazioneInterfaceEndpointSec() {
        return super.getPort(ComunicazioneVariazioneServiceSec11.ComunicazioneVariazioneInterfaceEndpointSec, ComunicazioneVariazione.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ComunicazioneVariazione
     */
    @WebEndpoint(name = "ComunicazioneVariazioneSOAP11SecInterfaceEndpoint")
    public ComunicazioneVariazione getComunicazioneVariazioneInterfaceEndpointSec(WebServiceFeature... features) {
        return super.getPort(ComunicazioneVariazioneServiceSec11.ComunicazioneVariazioneInterfaceEndpointSec, ComunicazioneVariazione.class, features);
    }

}
