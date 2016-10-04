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

package org.openspcoop2.protocol.sdk.validator;

import java.util.Vector;

import javax.xml.soap.SOAPElement;

import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;

/**
 * Wrapper per i risultati prodotti dal processo di validazione sintattica:
 * <ul>
 * <li><i>erroriValidazione</i> - Vettore con gli errori di validazione riscontrati
 * <li><i>erroriProcessamento</i> - Vettore con gli errori di processamento incorsi durante la validazione
 * <li><i>erroriTrovatiSullaListaEccezioni</i> - Vettore di Eccezioni trovati tra le informazioni di cooperazione
 * <li><i>busta</i> - Busta con le informazioni di cooperazione raccolte
 * <li><i>msgErrore</i> - Messaggio di errore da inserire nella risposta
 * <li><i>codiceErrore</i> - Codice dell'errore da inserire nella risposta
 * <li><i>bustaErrore</i> - Generata solo quando la busta arrivata non contiene gli elementi principali
 * <li><i>isValido</i> - booleano che indica se il messaggio puo' considerarsi valido.
 * </ul>
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ValidazioneSintatticaResult {

	/**
	 * Imposta i risultati del processo di validazione sintattiva
	 * 
	 * @param erroriValidazione Vettore con gli errori di validazione riscontrati
	 * @param erroriProcessamento Vettore con gli errori di processamento incorsi durante la validazione
	 * @param errorsTrovatiSullaListaEccezioni Vettore di Eccezioni trovati tra le informazioni di cooperazione
	 * @param busta Busta con le informazioni di cooperazione raccolte
	 * @param errore Errore da inserire nella risposta
	 * @param bustaErrore Generata solo quando la busta arrivata non contiene gli elementi principali
	 */
	
	public ValidazioneSintatticaResult(Vector<Eccezione> erroriValidazione, 
			Vector<Eccezione> erroriProcessamento, 
			Vector<Eccezione> errorsTrovatiSullaListaEccezioni,
			Busta busta, ErroreCooperazione errore,
			Busta bustaErrore, SOAPElement protocolElement,
			boolean isValido){
		this.erroriProcessamento = erroriProcessamento;
		this.erroriValidazione = erroriValidazione;
		this.errorsTrovatiSullaListaEccezioni = errorsTrovatiSullaListaEccezioni;
		this.busta = busta;
		this.errore = errore;
		this.bustaErrore = bustaErrore;
		this.protocolElement = protocolElement;
		this.isValido = isValido;
	}
	
	public Vector<Eccezione> getErroriValidazione() {
		return this.erroriValidazione;
	}

	public Vector<Eccezione> getErroriProcessamento() {
		return this.erroriProcessamento;
	}

	public Vector<Eccezione> getErrorsTrovatiSullaListaEccezioni() {
		return this.errorsTrovatiSullaListaEccezioni;
	}

	public Busta getBusta() {
		return this.busta;
	}

	public ErroreCooperazione getErrore() {
		return this.errore;
	}
	
	public Busta getBustaErrore() {
		return this.bustaErrore;
	}
	
	public SOAPElement getProtocolElement() {
		return this.protocolElement;
	}
	
	public boolean isValido(){
		return this.isValido;
	}
	/** Errori di validazione riscontrati sulla busta */
	private Vector<Eccezione> erroriValidazione;
	/** Errori di processamento riscontrati sulla busta */
	private Vector<Eccezione> erroriProcessamento;
	/** Errors riscontrati sulla lista eccezioni */
	private Vector<Eccezione> errorsTrovatiSullaListaEccezioni;
	/** Busta */
	private Busta busta;
	/** Eventuale errore avvenuto durante il processo di validazione */
	private ErroreCooperazione errore;
	/** bustaErroreHeaderIntestazione: generata solo quando la busta arrivata non contiene gli elementi principali */
	private Busta bustaErrore;
	/** Elemento SOAP che raccoglie i dati di cooperazione */
	private SOAPElement protocolElement;
	/** Indica se il messaggio e' valido o meno */
	private boolean isValido;
}
