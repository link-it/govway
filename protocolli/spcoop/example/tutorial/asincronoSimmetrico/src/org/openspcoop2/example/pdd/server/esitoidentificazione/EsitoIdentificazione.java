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
package org.openspcoop2.example.pdd.server.esitoidentificazione;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.xml.bind.annotation.XmlSeeAlso;

import org.openspcoop2.example.pdd.server.identificasoggetto.ObjectFactory;

/**
 * EsitoIdentificazione
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@WebService(targetNamespace = "http://openspcoop2.org/example/pdd/server/IdentificaSoggetto", name = "EsitoIdentificazione")
@XmlSeeAlso({ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface EsitoIdentificazione {

    @WebResult(name = "risultatoResponse", targetNamespace = "http://openspcoop2.org/example/pdd/server/IdentificaSoggetto", partName = "risultatoIdentificaResponse")
    @WebMethod(operationName = "Risultato")
    public java.lang.String risultato(
        @WebParam(partName = "identificaRequestPart", name = "risultato", targetNamespace = "http://openspcoop2.org/example/pdd/server/IdentificaSoggetto")
        PersonaType identificaRequestPart
    );
}
