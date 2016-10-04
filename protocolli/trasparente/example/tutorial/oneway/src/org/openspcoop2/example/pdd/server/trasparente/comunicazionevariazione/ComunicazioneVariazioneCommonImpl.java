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
package org.openspcoop2.example.pdd.server.trasparente.comunicazionevariazione;

import org.openspcoop2.example.pdd.server.trasparente.comunicazionevariazione.ComunicazioneVariazione;
import org.openspcoop2.example.pdd.server.trasparente.comunicazionevariazione.ComunicazioneVariazione_Type;

/**
 * ComunicazioneVariazioneCommonImpl
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ComunicazioneVariazioneCommonImpl implements ComunicazioneVariazione {
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
