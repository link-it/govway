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

package org.openspcoop2.pdd.mdb;

/**
 * 
 * Incapsula l'esito dell'invocazione del metodo OnMessage delle Librerie 
 * @author Tronci Fabio (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class EsitoLib {

	public final static int OK = 1;
	public final static int ERRORE_GESTITO = 2;
	public final static int ERRORE_NON_GESTITO = 3;
	
	private Throwable erroreNonGestito = null;
	private String motivazioneErroreNonGestito = null;
	
	private int statoInvocazione = 0;
	
	private boolean esitoInvocazione = false;

	private boolean erroreProcessamentoMessaggioAggiornato = false;
	private boolean dataRispedizioneAggiornata = false;


	public EsitoLib(){
		setEsitoInvocazione(false);
	}
	
	
	
	public boolean isDataRispedizioneAggiornata() {
		return this.dataRispedizioneAggiornata;
	}

	public void setDataRispedizioneAggiornata(boolean dataRispedizioneAggiornata) {
		this.dataRispedizioneAggiornata = dataRispedizioneAggiornata;
	}
	
	public String getMotivazioneErroreNonGestito() {
		return this.motivazioneErroreNonGestito;
	}

	public Throwable getErroreNonGestito() {
		return this.erroreNonGestito;
	}

	public void setErroreNonGestito(Exception erroreNonGestito) {
		this.erroreNonGestito = erroreNonGestito;
	}
	
	public boolean isErroreProcessamentoMessaggioAggiornato() {
		return this.erroreProcessamentoMessaggioAggiornato;
	}

	public void setErroreProcessamentoMessaggioAggiornato(
			boolean erroreProcessamentoMessaggioAggiornato) {
		this.erroreProcessamentoMessaggioAggiornato = erroreProcessamentoMessaggioAggiornato;
	}
		
	public boolean isEsitoInvocazione() {
		return this.esitoInvocazione;
	}

	public void setEsitoInvocazione(boolean esitoInvocazione) {
		this.esitoInvocazione = esitoInvocazione;
	}

	public int getStatoInvocazione() {
		return this.statoInvocazione;
	}

	public void setStatoInvocazione(int statoInvocazione,String motivazioneErroreNonGestito) {
		this.statoInvocazione = statoInvocazione;
		this.motivazioneErroreNonGestito = motivazioneErroreNonGestito;
	}
	@Deprecated
	public void setStatoInvocazione(int statoInvocazione) {
		this.statoInvocazione = statoInvocazione;
	}
	
	public void setStatoInvocazioneErroreNonGestito(Throwable e) {
		this.statoInvocazione = EsitoLib.ERRORE_NON_GESTITO;
		this.erroreNonGestito = e;
	}
	
}
