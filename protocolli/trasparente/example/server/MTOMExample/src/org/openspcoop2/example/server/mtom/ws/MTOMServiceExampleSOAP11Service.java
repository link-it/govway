/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
 * MTOMServiceExampleSOAP11Service
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@WebServiceClient(name = "MTOMServiceExampleSOAP11Service", 
                  wsdlLocation = "configurazionePdD/wsdl/implementazioneErogatoreSoap11.wsdl",
                  targetNamespace = "http://www.openspcoop2.org/example/server/mtom/ws") 
public class MTOMServiceExampleSOAP11Service extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://www.openspcoop2.org/example/server/mtom/ws", "MTOMServiceExampleSOAP11Service");
    public final static QName MTOMServiceExampleSOAP11InterfaceEndpoint = new QName("http://www.openspcoop2.org/example/server/mtom/ws", "MTOMServiceExampleSOAP11InterfaceEndpoint");
    static {
        URL url = MTOMServiceExampleSOAP11Service.class.getResource("configurazionePdD/wsdl/implementazioneErogatoreSoap11.wsdl");
        if (url == null) {
            url = MTOMServiceExampleSOAP11Service.class.getClassLoader().getResource("configurazionePdD/wsdl/implementazioneErogatoreSoap11.wsdl");
        } 
        if (url == null) {
            java.util.logging.Logger.getLogger(MTOMServiceExampleSOAP11Service.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "configurazionePdD/wsdl/implementazioneErogatoreSoap11.wsdl");
        }       
        WSDL_LOCATION = url;
    }

    public MTOMServiceExampleSOAP11Service(URL wsdlLocation) {
        super(wsdlLocation, MTOMServiceExampleSOAP11Service.SERVICE);
    }

    public MTOMServiceExampleSOAP11Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public MTOMServiceExampleSOAP11Service() {
        super(MTOMServiceExampleSOAP11Service.WSDL_LOCATION, MTOMServiceExampleSOAP11Service.SERVICE);
    }
    
//    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
//    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
//    //compliant code instead.
//    public MTOMServiceExampleSOAP11Service(WebServiceFeature ... features) {
//        super(MTOMServiceExampleSOAP11Service.WSDL_LOCATION, MTOMServiceExampleSOAP11Service.SERVICE, features);
//    }
//
//    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
//    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
//    //compliant code instead.
//    public MTOMServiceExampleSOAP11Service(URL wsdlLocation, WebServiceFeature ... features) {
//        super(wsdlLocation, MTOMServiceExampleSOAP11Service.SERVICE, features);
//    }
//
//    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
//    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
//    //compliant code instead.
//    public MTOMServiceExampleSOAP11Service(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
//        super(wsdlLocation, serviceName, features);
//    }

    /**
     *
     * @return
     *     returns MTOMServiceExample
     */
    @WebEndpoint(name = "MTOMServiceExampleSOAP11InterfaceEndpoint")
    public MTOMServiceExample getMTOMServiceExampleSOAP11InterfaceEndpoint() {
        return super.getPort(MTOMServiceExampleSOAP11Service.MTOMServiceExampleSOAP11InterfaceEndpoint, MTOMServiceExample.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns MTOMServiceExample
     */
    @WebEndpoint(name = "MTOMServiceExampleSOAP11InterfaceEndpoint")
    public MTOMServiceExample getMTOMServiceExampleSOAP11InterfaceEndpoint(WebServiceFeature... features) {
        return super.getPort(MTOMServiceExampleSOAP11Service.MTOMServiceExampleSOAP11InterfaceEndpoint, MTOMServiceExample.class, features);
    }

}
