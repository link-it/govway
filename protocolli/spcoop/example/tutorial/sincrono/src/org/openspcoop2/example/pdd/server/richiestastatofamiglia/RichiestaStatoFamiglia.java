/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.example.pdd.server.richiestastatofamiglia;

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
