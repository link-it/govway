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

package org.openspcoop2.example.pdd.server.stampadocumento;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

/**
 * StampaDocumentoService
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@WebServiceClient(name = "StampaDocumentoService", 
                  wsdlLocation = "file:configurazionePdD/wsdl/implementativoErogatore.wsdl",
                  targetNamespace = "http://openspcoop2.org/example/pdd/server/StampaDocumento") 
public class StampaDocumentoService extends Service {
	
    public final static URL WSDL_LOCATION;
    public final static QName SERVICE = new QName("http://openspcoop2.org/example/pdd/server/StampaDocumento", "StampaDocumentoService");
    public final static QName StampaDocumentoInterfaceEndpoint = new QName("http://openspcoop2.org/example/pdd/server/StampaDocumento", "StampaDocumentoInterfaceEndpoint");
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

    public StampaDocumentoService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public StampaDocumentoService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public StampaDocumentoService() {
        super(WSDL_LOCATION, SERVICE);
    }

    /**
     * 
     * @return
     *     returns StampaDocumento
     */
    @WebEndpoint(name = "StampaDocumentoInterfaceEndpoint")
    public StampaDocumento getStampaDocumentoInterfaceEndpoint() {
        return super.getPort(StampaDocumentoInterfaceEndpoint, StampaDocumento.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns StampaDocumento
     */
    @WebEndpoint(name = "StampaDocumentoInterfaceEndpoint")
    public StampaDocumento getStampaDocumentoInterfaceEndpoint(WebServiceFeature... features) {
        return super.getPort(StampaDocumentoInterfaceEndpoint, StampaDocumento.class, features);
    }

}
