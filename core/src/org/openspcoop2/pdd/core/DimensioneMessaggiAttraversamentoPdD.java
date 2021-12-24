/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core;

import java.io.Serializable;

/**
 * Classe utilizzata per rappresentare le dimensioni dei messaggi che attraversamo la PdD
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DimensioneMessaggiAttraversamentoPdD implements Serializable {

	private static final long serialVersionUID = 1L;
		
	private Long ricezioneMsgIngresso = null ; 
	private Long ricezioneMsgRisposta = null ;
	private Long spedizioneMessaggioIngresso = null ;
	


	public Long getRicezioneMsgIngresso() {
		return this.ricezioneMsgIngresso;
	}

	public void setRicezioneMsgIngresso(Long ricezioneMsgIngresso) {
		this.ricezioneMsgIngresso = ricezioneMsgIngresso;
	}

	public Long getRicezioneMsgRisposta() {
		return this.ricezioneMsgRisposta;
	}

	public void setRicezioneMsgRisposta(Long ricezioneMsgRisposta) {
		this.ricezioneMsgRisposta = ricezioneMsgRisposta;
	}

	public Long getSpedizioneMessaggioIngresso() {
		return this.spedizioneMessaggioIngresso;
	}

	public void setSpedizioneMessaggioIngresso(
			Long spedizioneMessaggioIngresso) {
		this.spedizioneMessaggioIngresso = spedizioneMessaggioIngresso;
	}

	public DimensioneMessaggiAttraversamentoPdD(Long ricezioneMessaggioIngresso, Long spedizioneMessaggioIngresso, Long ricezioneMessaggioRisposta) {
		this.ricezioneMsgIngresso = ricezioneMessaggioIngresso;
		this.ricezioneMsgRisposta = ricezioneMessaggioRisposta;
		this.spedizioneMessaggioIngresso = spedizioneMessaggioIngresso;
	}

	public DimensioneMessaggiAttraversamentoPdD() {
	}

	public void aggiornaDimensioneMessaggiAttraversamentoPDD(Long ricezioneMessaggioIngresso, Long spedizioneMessaggioIngresso, Long ricezioneMessaggioRisposta) {
		if (ricezioneMessaggioIngresso != null )
			this.ricezioneMsgIngresso = ricezioneMessaggioIngresso;
		if (ricezioneMessaggioRisposta != null ) 
			this.ricezioneMsgRisposta = ricezioneMessaggioRisposta;
		if (spedizioneMessaggioIngresso != null ) 
			this.spedizioneMessaggioIngresso = spedizioneMessaggioIngresso;
	}

}
