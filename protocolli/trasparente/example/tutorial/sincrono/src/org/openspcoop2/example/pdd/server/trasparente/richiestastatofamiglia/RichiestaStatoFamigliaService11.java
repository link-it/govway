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
package org.openspcoop2.example.pdd.server.trasparente.richiestastatofamiglia;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * RichiestaStatoFamigliaService11
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@WebServiceClient(name = "RichiestaStatoFamigliaSOAP11Service", 
                  wsdlLocation = "file:configurazionePdD/wsdl/implementazioneErogatoreSoap11.wsdll",
                  targetNamespace = "http://openspcoop2.org/example/pdd/server/RichiestaStatoFamiglia") 
public class RichiestaStatoFamigliaService11 extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://openspcoop2.org/example/pdd/server/RichiestaStatoFamiglia", "RichiestaStatoFamigliaSOAP11Service");
    public final static QName RichiestaStatoFamigliaInterfaceEndpoint = new QName("http://openspcoop2.org/example/pdd/server/RichiestaStatoFamiglia", "RichiestaStatoFamigliaSOAP11InterfaceEndpoint");
    static {
        URL url = RichiestaStatoFamigliaService11.class.getResource("configurazionePdD/wsdl/implementazioneErogatoreSoap11.wsdl");
        if (url == null) {
            url = RichiestaStatoFamigliaService11.class.getClassLoader().getResource("configurazionePdD/wsdl/implementazioneErogatoreSoap11.wsdl");
        } 
        if (url == null) {
        	System.out.println("Can not initialize the default wsdl from configurazionePdD/wsdl/implementazioneErogatoreSoap11.wsdl");
        }       
        WSDL_LOCATION = url;
    }

    public RichiestaStatoFamigliaService11(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public RichiestaStatoFamigliaService11(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public RichiestaStatoFamigliaService11() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    /**
     *
     * @return
     *     returns RichiestaStatoFamiglia
     */
    @WebEndpoint(name = "RichiestaStatoFamigliaSOAP11InterfaceEndpoint")
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
    @WebEndpoint(name = "RichiestaStatoFamigliaSOAP11InterfaceEndpoint")
    public RichiestaStatoFamiglia getRichiestaStatoFamigliaInterfaceEndpoint(WebServiceFeature... features) {
        return super.getPort(RichiestaStatoFamigliaInterfaceEndpoint, RichiestaStatoFamiglia.class, features);
    }

}
