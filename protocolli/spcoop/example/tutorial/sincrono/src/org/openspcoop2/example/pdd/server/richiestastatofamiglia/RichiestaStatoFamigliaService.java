/*
 * OpenSPCoop - Customizable API Gateway 
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
package org.openspcoop2.example.pdd.server.richiestastatofamiglia;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * RichiestaStatoFamigliaService
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@WebServiceClient(name = "RichiestaStatoFamigliaService", 
                  wsdlLocation = "file:configurazionePdD/wsdl/implementativoErogatore.wsdl",
                  targetNamespace = "http://openspcoop2.org/example/pdd/server/RichiestaStatoFamiglia") 
public class RichiestaStatoFamigliaService extends Service {
	
    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://openspcoop2.org/example/pdd/server/RichiestaStatoFamiglia", "RichiestaStatoFamigliaService");
    public final static QName RichiestaStatoFamigliaInterfaceEndpoint = new QName("http://openspcoop2.org/example/pdd/server/RichiestaStatoFamiglia", "RichiestaStatoFamigliaInterfaceEndpoint");
    static {
        URL url = null;
        try {
            url = new URL("file:configurazionePdD/wsdl/implementativoErogatore.wsdl");
        } catch (MalformedURLException e) {
            System.err.println("Can not initialize the default wsdl from file:configurazionePdD/wsdl/implementativoErogatore.wsdl");
            // e.printStackTrace();
        }
        WSDL_LOCATION = url;
    }

    public RichiestaStatoFamigliaService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public RichiestaStatoFamigliaService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public RichiestaStatoFamigliaService() {
        super(WSDL_LOCATION, SERVICE);
    }

    /**
     *
     * @return
     *     returns RichiestaStatoFamiglia
     */
    @WebEndpoint(name = "RichiestaStatoFamigliaInterfaceEndpoint")
    public RichiestaStatoFamiglia getRichiestaStatoFamigliaInterfaceEndpoint() {
        return super.getPort(RichiestaStatoFamigliaInterfaceEndpoint, RichiestaStatoFamiglia.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns RichiestaStatoFamiglia
     */
    @WebEndpoint(name = "RichiestaStatoFamigliaInterfaceEndpoint")
    public RichiestaStatoFamiglia getRichiestaStatoFamigliaInterfaceEndpoint(WebServiceFeature... features) {
        return super.getPort(RichiestaStatoFamigliaInterfaceEndpoint, RichiestaStatoFamiglia.class, features);
    }

}
