/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.sdk.validator;

import java.util.List;

import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.BustaRawContent;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;

/**
 * Wrapper per i risultati prodotti dal processo di validazione sintattica:
 * <ul>
 * <li><i>erroriValidazione</i> - Vettore con gli errori di validazione riscontrati
 * <li><i>erroriProcessamento</i> - Vettore con gli errori di processamento incorsi durante la validazione
 * <li><i>erroriTrovatiSullaListaEccezioni</i> - Vettore di Eccezioni trovati tra le informazioni di cooperazione
 * <li><i>busta</i> - Busta con le informazioni di cooperazione raccolte
 * <li><i>errore</i> - Eventuale errore avvenuto durante il processo di validazione
 * <li><i>bustaErrore</i> - Generata solo quando la busta arrivata non contiene gli elementi principali
 * <li><i>bustaRaw</i> - Contiene l'informazione raw del protocollo (es. header soap, header di trasporto o altra informazione dipendente dal protocollo)
 * <li><i>isValido</i> - booleano che indica se il messaggio puo' considerarsi valido.
 * </ul>
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ValidazioneSintatticaResult<BustaRawType> {

	/** Errori di validazione riscontrati sulla busta */
	private List<Eccezione> erroriValidazione;
	/** Errori di processamento riscontrati sulla busta */
	private List<Eccezione> erroriProcessamento;
	/** Errors riscontrati sulla lista eccezioni */
	private List<Eccezione> errorsTrovatiSullaListaEccezioni;
	/** Busta */
	private Busta busta;
	/** Eventuale errore avvenuto durante il processo di validazione */
	private ErroreCooperazione errore;
	/** bustaErroreHeaderIntestazione: generata solo quando la busta arrivata non contiene gli elementi principali */
	private Busta bustaErrore;
	/** Elemento che raccoglie i dati di cooperazione */
	private BustaRawContent<BustaRawType> bustaRaw;
	/** Indica se il messaggio e' valido o meno */
	private boolean isValido;
	
	/**
	 * Imposta i risultati del processo di validazione sintattiva
	 * 
	 * @param erroriValidazione Vettore con gli errori di validazione riscontrati
	 * @param erroriProcessamento Vettore con gli errori di processamento incorsi durante la validazione
	 * @param errorsTrovatiSullaListaEccezioni Vettore di Eccezioni trovati tra le informazioni di cooperazione
	 * @param busta Busta con le informazioni di cooperazione raccolte
	 * @param errore Errore da inserire nella risposta
	 * @param bustaErrore Generata solo quando la busta arrivata non contiene gli elementi principali
	 * @param bustaRaw Contiene l'informazione raw del protocollo (es. header soap, header di trasporto o altra informazione dipendente dal protocollo)
	 * @param isValido Indicazione se il messaggio è valido o meno rispetto al protocollo
	 */
	
	public ValidazioneSintatticaResult(List<Eccezione> erroriValidazione, 
			List<Eccezione> erroriProcessamento, 
			List<Eccezione> errorsTrovatiSullaListaEccezioni,
			Busta busta, 
			ErroreCooperazione errore,
			Busta bustaErrore, 
			BustaRawContent<BustaRawType> bustaRaw,
			boolean isValido){
		this.erroriProcessamento = erroriProcessamento;
		this.erroriValidazione = erroriValidazione;
		this.errorsTrovatiSullaListaEccezioni = errorsTrovatiSullaListaEccezioni;
		this.busta = busta;
		this.errore = errore;
		this.bustaErrore = bustaErrore;
		this.bustaRaw = bustaRaw;
		this.isValido = isValido;
	}
	
	public List<Eccezione> getErroriValidazione() {
		return this.erroriValidazione;
	}

	public List<Eccezione> getErroriProcessamento() {
		return this.erroriProcessamento;
	}

	public List<Eccezione> getErrorsTrovatiSullaListaEccezioni() {
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
	
	public BustaRawContent<BustaRawType> getBustaRawContent() {
		return this.bustaRaw;
	}
	
	public boolean isValido(){
		return this.isValido;
	}

}
