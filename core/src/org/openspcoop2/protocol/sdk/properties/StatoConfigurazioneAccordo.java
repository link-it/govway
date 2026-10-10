/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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
package org.openspcoop2.protocol.sdk.properties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.id.IDAccordo;

/**
 * Esito della verifica, specifica del profilo di interoperabilità, della completezza della configurazione di un'API.
 * 
 * Un'API in errore non risulta utilizzabile (stato non configurato); un'API con un avviso è utilizzabile, 
 * ma alcuni suoi servizi (API SOAP) non devono essere proposti nelle erogazioni e fruizioni.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatoConfigurazioneAccordo implements Serializable {

	private static final long serialVersionUID = 1L;

	private IDAccordo idAccordo;
	private boolean errore;
	private String descrizione;
	private List<String> serviziNonUtilizzabili = new ArrayList<>();
	
	public static StatoConfigurazioneAccordo newErrore(String descrizione) {
		StatoConfigurazioneAccordo s = new StatoConfigurazioneAccordo();
		s.errore = true;
		s.descrizione = descrizione;
		return s;
	}
	public static StatoConfigurazioneAccordo newAvviso(String descrizione, List<String> serviziNonUtilizzabili) {
		StatoConfigurazioneAccordo s = new StatoConfigurazioneAccordo();
		s.errore = false;
		s.descrizione = descrizione;
		if(serviziNonUtilizzabili!=null) {
			s.serviziNonUtilizzabili.addAll(serviziNonUtilizzabili);
		}
		return s;
	}
	
	public IDAccordo getIdAccordo() {
		return this.idAccordo;
	}
	public void setIdAccordo(IDAccordo idAccordo) {
		this.idAccordo = idAccordo;
	}
	public boolean isErrore() {
		return this.errore;
	}
	public String getDescrizione() {
		return this.descrizione;
	}
	public List<String> getServiziNonUtilizzabili() {
		return this.serviziNonUtilizzabili;
	}
}
