/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.example.pdd.server.identificasoggetto;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

/**
 * IdentificaSoggettoService
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@WebServiceClient(name = "IdentificaSoggettoService", 
                  wsdlLocation = "file:configurazionePdD/wsdl/implementativoErogatore.wsdl",
                  targetNamespace = "http://openspcoop2.org/example/pdd/server/IdentificaSoggetto") 
public class IdentificaSoggettoService extends Service {

    public final static URL WSDL_LOCATION;
    public final static QName SERVICE = new QName("http://openspcoop2.org/example/pdd/server/IdentificaSoggetto", "IdentificaSoggettoService");
    public final static QName IdentificaSoggettoInterfaceEndpoint = new QName("http://openspcoop2.org/example/pdd/server/IdentificaSoggetto", "IdentificaSoggettoInterfaceEndpoint");
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

    public IdentificaSoggettoService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public IdentificaSoggettoService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public IdentificaSoggettoService() {
        super(WSDL_LOCATION, SERVICE);
    }

    /**
     * 
     * @return
     *     returns IdentificaSoggetto
     */
    @WebEndpoint(name = "IdentificaSoggettoInterfaceEndpoint")
    public IdentificaSoggetto getIdentificaSoggettoInterfaceEndpoint() {
        return super.getPort(IdentificaSoggettoInterfaceEndpoint, IdentificaSoggetto.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns IdentificaSoggetto
     */
    @WebEndpoint(name = "IdentificaSoggettoInterfaceEndpoint")
    public IdentificaSoggetto getIdentificaSoggettoInterfaceEndpoint(WebServiceFeature... features) {
        return super.getPort(IdentificaSoggettoInterfaceEndpoint, IdentificaSoggetto.class, features);
    }

}
