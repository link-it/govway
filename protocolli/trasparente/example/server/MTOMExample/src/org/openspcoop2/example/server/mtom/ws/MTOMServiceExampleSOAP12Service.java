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
package org.openspcoop2.example.server.mtom.ws;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * MTOMServiceExampleSOAP12Service
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@WebServiceClient(name = "MTOMServiceExampleSOAP12Service", 
                  wsdlLocation = "configurazionePdD/wsdl/implementazioneErogatoreSoap12.wsdl",
                  targetNamespace = "http://www.openspcoop2.org/example/server/mtom/ws") 
public class MTOMServiceExampleSOAP12Service extends Service {

    public static final URL WSDL_LOCATION;

    public static final QName SERVICE = new QName("http://www.openspcoop2.org/example/server/mtom/ws", "MTOMServiceExampleSOAP12Service");
    public static final QName MTOMServiceExampleSOAP12InterfaceEndpoint = new QName("http://www.openspcoop2.org/example/server/mtom/ws", "MTOMServiceExampleSOAP12InterfaceEndpoint");
    static {
        URL url = MTOMServiceExampleSOAP12Service.class.getResource("configurazionePdD/wsdl/implementazioneErogatoreSoap12.wsdl");
        if (url == null) {
            url = MTOMServiceExampleSOAP12Service.class.getClassLoader().getResource("configurazionePdD/wsdl/implementazioneErogatoreSoap12.wsdl");
        } 
        if (url == null) {
            java.util.logging.Logger.getLogger(MTOMServiceExampleSOAP12Service.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "configurazionePdD/wsdl/implementazioneErogatoreSoap12.wsdl");
        }       
        WSDL_LOCATION = url;
    }

    public MTOMServiceExampleSOAP12Service(URL wsdlLocation) {
        super(wsdlLocation, MTOMServiceExampleSOAP12Service.SERVICE);
    }

    public MTOMServiceExampleSOAP12Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public MTOMServiceExampleSOAP12Service() {
        super(MTOMServiceExampleSOAP12Service.WSDL_LOCATION, MTOMServiceExampleSOAP12Service.SERVICE);
    }
    
//    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
//    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
//    //compliant code instead.
//    public MTOMServiceExampleSOAP12Service(WebServiceFeature ... features) {
//        super(MTOMServiceExampleSOAP12Service.WSDL_LOCATION, MTOMServiceExampleSOAP12Service.SERVICE, features);
//    }
//
//    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
//    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
//    //compliant code instead.
//    public MTOMServiceExampleSOAP12Service(URL wsdlLocation, WebServiceFeature ... features) {
//        super(wsdlLocation, MTOMServiceExampleSOAP12Service.SERVICE, features);
//    }
//
//    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
//    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
//    //compliant code instead.
//    public MTOMServiceExampleSOAP12Service(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
//        super(wsdlLocation, serviceName, features);
//    }

    /**
     *
     * @return
     *     returns MTOMServiceExample
     */
    @WebEndpoint(name = "MTOMServiceExampleSOAP12InterfaceEndpoint")
    public MTOMServiceExample getMTOMServiceExampleSOAP12InterfaceEndpoint() {
        return super.getPort(MTOMServiceExampleSOAP12Service.MTOMServiceExampleSOAP12InterfaceEndpoint, MTOMServiceExample.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns MTOMServiceExample
     */
    @WebEndpoint(name = "MTOMServiceExampleSOAP12InterfaceEndpoint")
    public MTOMServiceExample getMTOMServiceExampleSOAP12InterfaceEndpoint(WebServiceFeature... features) {
        return super.getPort(MTOMServiceExampleSOAP12Service.MTOMServiceExampleSOAP12InterfaceEndpoint, MTOMServiceExample.class, features);
    }

}
