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

package org.openspcoop2.pdd.core;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Classe utilizzata per rappresentare i tempi di attraversamento PdD
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TempiAttraversamentoPDD implements Serializable {

	private static final long serialVersionUID = 1L;
		
	private java.sql.Timestamp ricezioneMsgIngresso = null ; 
	private java.sql.Timestamp ricezioneMsgRisposta = null ;
	private java.sql.Timestamp spedizioneMessaggioIngresso = null ;
	


	public java.sql.Timestamp getRicezioneMsgIngresso() {
		return this.ricezioneMsgIngresso;
	}

	public void setRicezioneMsgIngresso(java.sql.Timestamp ricezioneMsgIngresso) {
		this.ricezioneMsgIngresso = ricezioneMsgIngresso;
	}

	public java.sql.Timestamp getRicezioneMsgRisposta() {
		return this.ricezioneMsgRisposta;
	}

	public void setRicezioneMsgRisposta(java.sql.Timestamp ricezioneMsgRisposta) {
		this.ricezioneMsgRisposta = ricezioneMsgRisposta;
	}

	public java.sql.Timestamp getSpedizioneMessaggioIngresso() {
		return this.spedizioneMessaggioIngresso;
	}

	public void setSpedizioneMessaggioIngresso(
			java.sql.Timestamp spedizioneMessaggioIngresso) {
		this.spedizioneMessaggioIngresso = spedizioneMessaggioIngresso;
	}

	public TempiAttraversamentoPDD(Timestamp ricezioneMessaggioIngresso, Timestamp spedizioneMessaggioIngresso, Timestamp ricezioneMessaggioRisposta) {
		this.ricezioneMsgIngresso = ricezioneMessaggioIngresso;
		this.ricezioneMsgRisposta = ricezioneMessaggioRisposta;
		this.spedizioneMessaggioIngresso = spedizioneMessaggioIngresso;
	}

	public TempiAttraversamentoPDD() {
	}

	public void aggiornaTempiAttraversamentoPDD(Timestamp ricezioneMessaggioIngresso, Timestamp spedizioneMessaggioIngresso, Timestamp ricezioneMessaggioRisposta) {
		if (ricezioneMessaggioIngresso != null )
			this.ricezioneMsgIngresso = ricezioneMessaggioIngresso;
		if (ricezioneMessaggioRisposta != null ) 
			this.ricezioneMsgRisposta = ricezioneMessaggioRisposta;
		if (spedizioneMessaggioIngresso != null ) 
			this.spedizioneMessaggioIngresso = spedizioneMessaggioIngresso;
	}


}
