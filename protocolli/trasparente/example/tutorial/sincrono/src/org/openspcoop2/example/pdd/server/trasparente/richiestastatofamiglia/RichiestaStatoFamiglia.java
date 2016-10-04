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

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * RichiestaStatoFamiglia
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@WebService(targetNamespace = "http://openspcoop2.org/example/pdd/server/RichiestaStatoFamiglia", name = "RichiestaStatoFamiglia")
@XmlSeeAlso({ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface RichiestaStatoFamiglia {

    @WebResult(name = "statoFamiglia", targetNamespace = "http://openspcoop2.org/example/pdd/server/RichiestaStatoFamiglia", partName = "statoFamigliaResponsePart")
    @WebMethod(operationName = "Acquisisci")
    public PersonaType acquisisci(
        @WebParam(partName = "statoFamigliaRequestPart", name = "richiestaStatoFamiglia", targetNamespace = "http://openspcoop2.org/example/pdd/server/RichiestaStatoFamiglia")
        java.lang.String statoFamigliaRequestPart
    );
}
