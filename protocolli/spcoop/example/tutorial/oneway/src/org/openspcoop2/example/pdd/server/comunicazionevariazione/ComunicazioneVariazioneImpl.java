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
package org.openspcoop2.example.pdd.server.comunicazionevariazione;

/**
 * ComunicazioneVariazioneImpl
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@javax.jws.WebService(
                      serviceName = "ComunicazioneVariazioneService",
                      portName = "ComunicazioneVariazioneInterfaceEndpoint",
                      targetNamespace = "http://openspcoop2.org/example/pdd/server/ComunicazioneVariazione",
                      wsdlLocation = "file:configurazionePdD/wsdl/implementativoErogatore.wsdl",
                      endpointInterface = "org.openspcoop2.example.pdd.server.comunicazionevariazione.ComunicazioneVariazione")
                      
public class ComunicazioneVariazioneImpl implements ComunicazioneVariazione {
    @Override
	public void notifica(ComunicazioneVariazione_Type comunicazioneVariazionePart) { 
        try {
        	System.out.println("========== Ricevuta Comunicazione Variazione ==============");
			System.out.println("== Codice fiscale da modificare: " + comunicazioneVariazionePart.getCF());
			System.out.println();
			System.out.println("== Nuovo nome: " + comunicazioneVariazionePart.getNome());
			System.out.println("== Nuovo cognome: " + comunicazioneVariazionePart.getCognome());
			System.out.println("== Nuovo codice fiscale: " + comunicazioneVariazionePart.getCodiceFiscale());
			System.out.println("== Nuova data di nascita: " + comunicazioneVariazionePart.getNascita());
			System.out.println("== Nuovo stato civile: " + comunicazioneVariazionePart.getStatoCivile());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
}
