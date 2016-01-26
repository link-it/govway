/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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



package org.openspcoop2.protocol.sdk.tracciamento;

import java.io.Serializable;
import java.text.MessageFormat;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;

/**
 *
 * Informazioni Busta
 * 
 * 
 * @author Stefano Corallo <corallo@link.it>
 * @author Lorenzo Nardi <nardi@link.it>
* @author $Author$
* @version $Rev$, $Date$
 */

public class InformazioniProtocollo implements Serializable{

	private static final long serialVersionUID = -3415656968896569068L;

    protected String azione;
    protected IDSoggetto destinatario;
    protected IDSoggetto mittente;
    protected String servizio;
    protected String tipoServizio;
    protected Integer versioneServizio;
	protected String profiloCollaborazioneProtocollo;
	protected ProfiloDiCollaborazione profiloCollaborazioneEngine;
    
    

	public String getProfiloCollaborazioneProtocollo() {
		return this.profiloCollaborazioneProtocollo;
	}
	public void setProfiloCollaborazioneProtocollo(
			String profiloCollaborazioneProtocollo) {
		this.profiloCollaborazioneProtocollo = profiloCollaborazioneProtocollo;
	}
	public ProfiloDiCollaborazione getProfiloCollaborazioneEngine() {
		return this.profiloCollaborazioneEngine;
	}
	public void setProfiloCollaborazioneEngine(
			ProfiloDiCollaborazione profiloCollaborazioneEngine) {
		this.profiloCollaborazioneEngine = profiloCollaborazioneEngine;
	}
	public IDSoggetto getDestinatario() {
		return this.destinatario;
	}
	public void setDestinatario(IDSoggetto erogatore) {
		this.destinatario = erogatore;
	}
	public IDSoggetto getMittente() {
		return this.mittente;
	}
	public void setMittente(IDSoggetto fruitore) {
		this.mittente = fruitore;
	}
	public String getServizio() {
		return this.servizio;
	}
	public void setServizio(String servizio) {
		this.servizio = servizio;
	}
	public String getAzione() {
		return this.azione;
	}
	public void setAzione(String azione) {
		this.azione = azione;
	}
	public String getTipoServizio() {
		return this.tipoServizio;
	}
	public void setTipoServizio(String tipoServizio) {
		this.tipoServizio = tipoServizio;
	}
	public Integer getVersioneServizio() {
		return this.versioneServizio;
	}
	public void setVersioneServizio(Integer versioneServizio) {
		this.versioneServizio = versioneServizio;
	}
	
	@Override
	public String toString() {
		String pattern = "erogatore [{0}]" +
				" fruitore [{1}]" +
				" servizio [{2}]" +
				" tipoServizio [{3}]" +
				" versioneServizio [{4}]" +
				" azione [{5}]" +
				" profiloCollaborazioneProtocollo [{6}]" +
				" profiloCollaborazioneEngine [{7}]";
		return MessageFormat.format(pattern, 
				this.destinatario!=null ? this.destinatario.toString() : "not set",
						this.mittente!=null ? this.mittente.toString() : "not set",
						this.servizio!=null ? this.servizio : "not set",
						this.tipoServizio!=null ? this.tipoServizio : "not set",
						this.versioneServizio!=null ? this.versioneServizio : "not set",
						this.azione!=null ? this.azione : "not set",
						this.profiloCollaborazioneProtocollo!=null ? this.profiloCollaborazioneProtocollo : "not set",
						this.profiloCollaborazioneEngine!=null ? this.profiloCollaborazioneEngine : "not set"	);
	}
}


