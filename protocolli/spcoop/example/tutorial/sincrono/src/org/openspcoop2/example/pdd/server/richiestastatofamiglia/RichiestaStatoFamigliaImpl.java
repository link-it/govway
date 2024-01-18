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
package org.openspcoop2.example.pdd.server.richiestastatofamiglia;

/**
 * RichiestaStatoFamigliaImpl
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@javax.jws.WebService(serviceName = "RichiestaStatoFamigliaService",
                      portName = "RichiestaStatoFamigliaInterfaceEndpoint",
                      targetNamespace = "http://openspcoop2.org/example/pdd/server/RichiestaStatoFamiglia",
                      wsdlLocation = "file:configurazionePdD/wsdl/implementativoErogatore.wsdl",
                      endpointInterface = "org.openspcoop2.example.pdd.server.richiestastatofamiglia.RichiestaStatoFamiglia")
public class RichiestaStatoFamigliaImpl implements RichiestaStatoFamiglia {

    @Override
	public PersonaType acquisisci(java.lang.String statoFamigliaRequestPart) { 
        System.out.println("Richiesto Stato Famiglia per cf:" + statoFamigliaRequestPart);
        try {
            PersonaType persona = new PersonaType();
            
            persona.setCodiceFiscale("DDDFFF22G22G222G");
            persona.setCognome("Rossi");
            persona.setNome("Mario");
            persona.setNascita(javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar("1980-01-01T12:00:00.000Z"));
            persona.setStatoCivile("Celibe");
            
            
            return persona;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

}
