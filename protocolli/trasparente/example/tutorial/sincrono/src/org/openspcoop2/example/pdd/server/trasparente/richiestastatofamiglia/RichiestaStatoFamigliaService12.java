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
package org.openspcoop2.example.pdd.server.trasparente.richiestastatofamiglia;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * RichiestaStatoFamigliaService12
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@WebServiceClient(name = "RichiestaStatoFamigliaSOAP12Service", 
                  wsdlLocation = "file:configurazionePdD/wsdl/implementazioneErogatoreSoap12.wsdl",
                  targetNamespace = "http://openspcoop2.org/example/pdd/server/RichiestaStatoFamiglia") 
public class RichiestaStatoFamigliaService12 extends Service {

    public static final URL WSDL_LOCATION;

    public static final QName SERVICE = new QName("http://openspcoop2.org/example/pdd/server/RichiestaStatoFamiglia", "RichiestaStatoFamigliaSOAP12Service");
    public static final QName RichiestaStatoFamigliaInterfaceEndpoint12 = new QName("http://openspcoop2.org/example/pdd/server/RichiestaStatoFamiglia", "RichiestaStatoFamigliaSOAP12InterfaceEndpoint");
    static {
        URL url = RichiestaStatoFamigliaService12.class.getResource("configurazionePdD/wsdl/implementazioneErogatoreSoap12.wsdl");
        if (url == null) {
            url = RichiestaStatoFamigliaService12.class.getClassLoader().getResource("configurazionePdD/wsdl/implementazioneErogatoreSoap12.wsdl");
        } 
        if (url == null) {
        	System.out.println("Can not initialize the default wsdl from configurazionePdD/wsdl/implementazioneErogatoreSoap12.wsdl");
        }       
        WSDL_LOCATION = url;
    }

    public RichiestaStatoFamigliaService12(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public RichiestaStatoFamigliaService12(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public RichiestaStatoFamigliaService12() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    /**
     *
     * @return
     *     returns RichiestaStatoFamiglia
     */
    @WebEndpoint(name = "RichiestaStatoFamigliaSOAP12InterfaceEndpoint")
    public RichiestaStatoFamiglia getRichiestaStatoFamigliaInterfaceEndpoint12() {
        return super.getPort(RichiestaStatoFamigliaInterfaceEndpoint12, RichiestaStatoFamiglia.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns RichiestaStatoFamiglia
     */
    @WebEndpoint(name = "RichiestaStatoFamigliaSOAP12InterfaceEndpoint")
    public RichiestaStatoFamiglia getRichiestaStatoFamigliaInterfaceEndpoint12(WebServiceFeature... features) {
        return super.getPort(RichiestaStatoFamigliaInterfaceEndpoint12, RichiestaStatoFamiglia.class, features);
    }

}
