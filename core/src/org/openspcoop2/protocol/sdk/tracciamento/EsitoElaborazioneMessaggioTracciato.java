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

import org.openspcoop2.protocol.sdk.constants.EsitoElaborazioneMessaggioTracciatura;

/**
* EsitoElaborazioneMessaggioTracciato
*
* @author Andrea Poli <apoli@link.it>
* @author $Author$
* @version $Rev$, $Date$
*/
public class EsitoElaborazioneMessaggioTracciato implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private EsitoElaborazioneMessaggioTracciatura esito;
	private String dettaglio;
	
	public EsitoElaborazioneMessaggioTracciatura getEsito() {
		return this.esito;
	}
	public void setEsito(EsitoElaborazioneMessaggioTracciatura esito) {
		this.esito = esito;
	}
	public String getDettaglio() {
		return this.dettaglio;
	}
	public void setDettaglio(String dettaglioErrore) {
		this.dettaglio = dettaglioErrore;
	}
	
	public static EsitoElaborazioneMessaggioTracciato getEsitoElaborazioneMessaggioInviato(){
		return EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneMessaggioTracciato(EsitoElaborazioneMessaggioTracciatura.INVIATO, null);
	}
	public static EsitoElaborazioneMessaggioTracciato getEsitoElaborazioneMessaggioRicevuto(){
		return EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneMessaggioTracciato(EsitoElaborazioneMessaggioTracciatura.RICEVUTO, null);
	}
	public static EsitoElaborazioneMessaggioTracciato getEsitoElaborazioneMessaggioRicevuto(String dettaglio){
		return EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneMessaggioTracciato(EsitoElaborazioneMessaggioTracciatura.RICEVUTO, dettaglio);
	}
	public static EsitoElaborazioneMessaggioTracciato getEsitoElaborazioneConErrore(String dettaglioErrore){
		return EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneMessaggioTracciato(EsitoElaborazioneMessaggioTracciatura.ERRORE, dettaglioErrore);
	}
	public static EsitoElaborazioneMessaggioTracciato getEsitoElaborazioneMessaggioTracciato(EsitoElaborazioneMessaggioTracciatura esito,String dettaglio){
		EsitoElaborazioneMessaggioTracciato esitoMsg = new EsitoElaborazioneMessaggioTracciato();
		esitoMsg.setEsito(esito);
		esitoMsg.setDettaglio(dettaglio);
		return esitoMsg;
	}
	
	@Override
	public EsitoElaborazioneMessaggioTracciato clone(){
		
		EsitoElaborazioneMessaggioTracciato clone = new EsitoElaborazioneMessaggioTracciato();
		
		clone.setEsito(this.esito);
		clone.setDettaglio(this.dettaglio!=null ? new String(this.dettaglio) : null);
		
		return clone;
		
	}
}
