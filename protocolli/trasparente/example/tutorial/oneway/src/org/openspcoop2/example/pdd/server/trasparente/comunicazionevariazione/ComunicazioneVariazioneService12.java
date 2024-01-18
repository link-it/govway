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
package org.openspcoop2.example.pdd.server.trasparente.comunicazionevariazione;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * ComunicazioneVariazioneService12
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@WebServiceClient(name = "ComunicazioneVariazioneSOAP12Service", 
                  wsdlLocation = "file:configurazionePdD/wsdl/implementazioneErogatoreSoap12.wsdl",
                  targetNamespace = "http://openspcoop2.org/example/pdd/server/ComunicazioneVariazione") 
public class ComunicazioneVariazioneService12 extends Service {

    public static final URL WSDL_LOCATION;

    public static final QName SERVICE = new QName("http://openspcoop2.org/example/pdd/server/ComunicazioneVariazione", "ComunicazioneVariazioneSOAP12Service");
    public static final QName ComunicazioneVariazioneInterfaceEndpoint12 = new QName("http://openspcoop2.org/example/pdd/server/ComunicazioneVariazione", "ComunicazioneVariazioneSOAP12InterfaceEndpoint");
    static {
        URL url = ComunicazioneVariazioneService12.class.getResource("configurazionePdD/wsdl/implementazioneErogatoreSoap12.wsdl");
        if (url == null) {
            url = ComunicazioneVariazioneService12.class.getClassLoader().getResource("configurazionePdD/wsdl/implementazioneErogatoreSoap12.wsdl");
        } 
        if (url == null) {
        	 System.out.println("Can not initialize the default wsdl from configurazionePdD/wsdl/implementazioneErogatoreSoap12.wsdl");
        }       
        WSDL_LOCATION = url;
    }

    public ComunicazioneVariazioneService12(URL wsdlLocation) {
        super(wsdlLocation, ComunicazioneVariazioneService12.SERVICE);
    }

    public ComunicazioneVariazioneService12(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ComunicazioneVariazioneService12() {
        super(ComunicazioneVariazioneService12.WSDL_LOCATION, ComunicazioneVariazioneService12.SERVICE);
    }
    
    /**
     *
     * @return
     *     returns ComunicazioneVariazione
     */
    @WebEndpoint(name = "ComunicazioneVariazioneInterfaceSOAP12Endpoint")
    public ComunicazioneVariazione getComunicazioneVariazioneInterfaceEndpoint12() {
        return super.getPort(ComunicazioneVariazioneService12.ComunicazioneVariazioneInterfaceEndpoint12, ComunicazioneVariazione.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ComunicazioneVariazione
     */
    @WebEndpoint(name = "ComunicazioneVariazioneSOAP12InterfaceEndpoint")
    public ComunicazioneVariazione getComunicazioneVariazioneInterfaceEndpoint12(WebServiceFeature... features) {
        return super.getPort(ComunicazioneVariazioneService12.ComunicazioneVariazioneInterfaceEndpoint12, ComunicazioneVariazione.class, features);
    }

}
