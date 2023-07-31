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
package org.openspcoop2.example.pdd.server.trasparente.richiestastatofamiglia;

import java.net.URL;
import javax.xml.namespace.QName;
import jakarta.xml.ws.WebEndpoint;
import jakarta.xml.ws.WebServiceClient;
import jakarta.xml.ws.WebServiceFeature;
import jakarta.xml.ws.Service;

/**
 * RichiestaStatoFamigliaServiceSec11
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@WebServiceClient(name = "RichiestaStatoFamigliaSOAP11SecService", 
                  wsdlLocation = "file:configurazionePdD/wsdl/implementazioneErogatoreSoap11Security.wsdl",
                  targetNamespace = "http://openspcoop2.org/example/pdd/server/RichiestaStatoFamiglia") 
public class RichiestaStatoFamigliaServiceSec11 extends Service {

    public static final URL WSDL_LOCATION;

    public static final QName SERVICE = new QName("http://openspcoop2.org/example/pdd/server/RichiestaStatoFamiglia", "RichiestaStatoFamigliaSOAP11SecService");
    public static final QName RichiestaStatoFamigliaInterfaceEndpointSec = new QName("http://openspcoop2.org/example/pdd/server/RichiestaStatoFamiglia", "RichiestaStatoFamigliaSOAP11SecInterfaceEndpoint");
    static {
        URL url = RichiestaStatoFamigliaServiceSec11.class.getResource("configurazionePdD/wsdl/implementazioneErogatoreSoap11Security.wsdl");
        if (url == null) {
            url = RichiestaStatoFamigliaServiceSec11.class.getClassLoader().getResource("configurazionePdD/wsdl/implementazioneErogatoreSoap11Security.wsdl");
        } 
        if (url == null) {
        	System.out.println("Can not initialize the default wsdl from configurazionePdD/wsdl/implementazioneErogatoreSoap11Security.wsdl");
        }       
        WSDL_LOCATION = url;
    }

    public RichiestaStatoFamigliaServiceSec11(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public RichiestaStatoFamigliaServiceSec11(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public RichiestaStatoFamigliaServiceSec11() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    /**
     *
     * @return
     *     returns RichiestaStatoFamiglia
     */
    @WebEndpoint(name = "RichiestaStatoFamigliaSOAP11SecInterfaceEndpoint")
    public RichiestaStatoFamiglia getRichiestaStatoFamigliaInterfaceEndpointSec() {
        return super.getPort(RichiestaStatoFamigliaInterfaceEndpointSec, RichiestaStatoFamiglia.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns RichiestaStatoFamiglia
     */
    @WebEndpoint(name = "RichiestaStatoFamigliaSOAP11SecInterfaceEndpoint")
    public RichiestaStatoFamiglia getRichiestaStatoFamigliaInterfaceEndpointSec(WebServiceFeature... features) {
        return super.getPort(RichiestaStatoFamigliaInterfaceEndpointSec, RichiestaStatoFamiglia.class, features);
    }

}
