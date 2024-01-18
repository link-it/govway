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
package org.openspcoop2.example.server.mtom.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * MTOMServiceExample
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@WebService(targetNamespace = "http://www.openspcoop2.org/example/server/mtom/ws", name = "MTOMServiceExample")
@XmlSeeAlso({org.openspcoop2.example.server.mtom.ObjectFactory.class})
public interface MTOMServiceExample {

    @RequestWrapper(localName = "echo", targetNamespace = "http://www.openspcoop2.org/example/server/mtom", className = "org.openspcoop2.example.server.mtom.Echo")
    @WebMethod(action = "echo")
    @ResponseWrapper(localName = "echoResponse", targetNamespace = "http://www.openspcoop2.org/example/server/mtom", className = "org.openspcoop2.example.server.mtom.EchoResponse")
    public void echo(
        @WebParam(name = "richiesta", targetNamespace = "http://www.openspcoop2.org/example/server/mtom")
        java.lang.String richiesta,
        @WebParam(name = "ImageData", targetNamespace = "http://www.openspcoop2.org/example/server/mtom")
        javax.xml.transform.Source imageData,
        @WebParam(name = "other", targetNamespace = "http://www.openspcoop2.org/example/server/mtom")
        java.util.List<javax.activation.DataHandler> other,
        @WebParam(mode = WebParam.Mode.OUT, name = "risposta", targetNamespace = "http://www.openspcoop2.org/example/server/mtom")
        javax.xml.ws.Holder<java.lang.String> risposta,
        @WebParam(mode = WebParam.Mode.OUT, name = "ImageDataResponse", targetNamespace = "http://www.openspcoop2.org/example/server/mtom")
        javax.xml.ws.Holder<javax.xml.transform.Source> imageDataResponse,
        @WebParam(mode = WebParam.Mode.OUT, name = "otherResponse", targetNamespace = "http://www.openspcoop2.org/example/server/mtom")
        javax.xml.ws.Holder<java.util.List<javax.activation.DataHandler>> otherResponse
    );
}
