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
package org.openspcoop2.example.pdd.server.comunicazionevariazione;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebEndpoint;
import jakarta.xml.ws.WebServiceClient;
import jakarta.xml.ws.WebServiceFeature;

/**
 * ComunicazioneVariazioneService
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@WebServiceClient(name = "ComunicazioneVariazioneService", 
                  wsdlLocation = "file:configurazionePdD/wsdl/implementativoErogatore.wsdl",
                  targetNamespace = "http://openspcoop2.org/example/pdd/server/ComunicazioneVariazione") 
public class ComunicazioneVariazioneService extends Service {

    public static final URL WSDL_LOCATION;
    public static final QName SERVICE = new QName("http://openspcoop2.org/example/pdd/server/ComunicazioneVariazione", "ComunicazioneVariazioneService");
    public static final QName ComunicazioneVariazioneInterfaceEndpoint = new QName("http://openspcoop2.org/example/pdd/server/ComunicazioneVariazione", "ComunicazioneVariazioneInterfaceEndpoint");
    static {
        URL url = null;
        try {
            url = new URL("file:configurazionePdD/wsdl/implementativoErogatore.wsdl");
        } catch (MalformedURLException e) {
            System.err.println("Can not initialize the default wsdl from file:configurazionePdD/wsdl/implementativoErogatore.wsdl");
        }
        WSDL_LOCATION = url;
    }

    public ComunicazioneVariazioneService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public ComunicazioneVariazioneService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ComunicazioneVariazioneService() {
        super(WSDL_LOCATION, SERVICE);
    }

    /**
     *
     * @return
     *     returns ComunicazioneVariazione
     */
    @WebEndpoint(name = "ComunicazioneVariazioneInterfaceEndpoint")
    public ComunicazioneVariazione getComunicazioneVariazioneInterfaceEndpoint() {
        return super.getPort(ComunicazioneVariazioneInterfaceEndpoint, ComunicazioneVariazione.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ComunicazioneVariazione
     */
    @WebEndpoint(name = "ComunicazioneVariazioneInterfaceEndpoint")
    public ComunicazioneVariazione getComunicazioneVariazioneInterfaceEndpoint(WebServiceFeature... features) {
        return super.getPort(ComunicazioneVariazioneInterfaceEndpoint, ComunicazioneVariazione.class, features);
    }

}
