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
package org.openspcoop2.example.pdd.server.sdi.ricevi_notifica;

import java.net.URL;
import javax.xml.namespace.QName;
import jakarta.xml.ws.WebEndpoint;
import jakarta.xml.ws.WebServiceClient;
import jakarta.xml.ws.WebServiceFeature;
import jakarta.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.7.4
 * 2014-10-09T15:59:57.747+02:00
 * Generated source version: 2.7.4
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@WebServiceClient(name = "SdIRiceviNotifica_service", 
                  wsdlLocation = "SdIRiceviNotifica_v1.0.wsdl",
                  targetNamespace = "http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0") 
public class SdIRiceviNotificaService extends Service {

    public static final URL WSDL_LOCATION;

    public static final QName SERVICE = new QName("http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0", "SdIRiceviNotifica_service");
    public static final QName SdIRiceviNotificaPort = new QName("http://www.fatturapa.gov.it/sdi/ws/ricezione/v1.0", "SdIRiceviNotifica_port");
    static {
        URL url = SdIRiceviNotificaService.class.getResource("SdIRiceviNotifica_v1.0.wsdl");
        if (url == null) {
            url = SdIRiceviNotificaService.class.getClassLoader().getResource("SdIRiceviNotifica_v1.0.wsdl");
        } 
        if (url == null) {
        	System.out.println("Can not initialize the default wsdl from SdIRiceviNotifica_v1.0.wsdl");
        }       
        WSDL_LOCATION = url;
    }

    public SdIRiceviNotificaService(URL wsdlLocation) {
        super(wsdlLocation, SdIRiceviNotificaService.SERVICE);
    }

    public SdIRiceviNotificaService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public SdIRiceviNotificaService() {
        super(SdIRiceviNotificaService.WSDL_LOCATION, SdIRiceviNotificaService.SERVICE);
    }
    
//    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
//    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
//    //compliant code instead.
//    public SdIRiceviNotificaService(WebServiceFeature ... features) {
//        super(SdIRiceviNotificaService.WSDL_LOCATION, SdIRiceviNotificaService.SERVICE, features);
//    }
//
//    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
//    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
//    //compliant code instead.
//    public SdIRiceviNotificaService(URL wsdlLocation, WebServiceFeature ... features) {
//        super(wsdlLocation, SdIRiceviNotificaService.SERVICE, features);
//    }
//
//    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
//    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
//    //compliant code instead.
//    public SdIRiceviNotificaService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
//        super(wsdlLocation, serviceName, features);
//    }

    /**
     *
     * @return
     *     returns SdIRiceviNotifica
     */
    @WebEndpoint(name = "SdIRiceviNotifica_port")
    public SdIRiceviNotifica getSdIRiceviNotificaPort() {
        return super.getPort(SdIRiceviNotificaService.SdIRiceviNotificaPort, SdIRiceviNotifica.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns SdIRiceviNotifica
     */
    @WebEndpoint(name = "SdIRiceviNotifica_port")
    public SdIRiceviNotifica getSdIRiceviNotificaPort(WebServiceFeature... features) {
        return super.getPort(SdIRiceviNotificaService.SdIRiceviNotificaPort, SdIRiceviNotifica.class, features);
    }

}
