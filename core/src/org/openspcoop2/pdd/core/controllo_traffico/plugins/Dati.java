/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.controllo_traffico.plugins;

import java.io.Serializable;

import org.openspcoop2.core.controllo_traffico.beans.DatiTransazione;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;

/**
 * Dati 
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Dati implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private DatiTransazione datiTransazione;
	private InfoConnettoreIngresso connettore;
	private OpenSPCoop2Message messaggio;
	private PdDContext pddContext;
	
	public DatiTransazione getDatiTransazione() {
		return this.datiTransazione;
	}
	public void setDatiTransazione(DatiTransazione datiTransazione) {
		this.datiTransazione = datiTransazione;
	}
	public InfoConnettoreIngresso getConnettore() {
		return this.connettore;
	}
	public void setConnettore(InfoConnettoreIngresso connettore) {
		this.connettore = connettore;
	}
	public OpenSPCoop2Message getMessaggio() {
		return this.messaggio;
	}
	public void setMessaggio(OpenSPCoop2Message messaggio) {
		this.messaggio = messaggio;
	}
	public PdDContext getPddContext() {
		return this.pddContext;
	}
	public void setPddContext(PdDContext pddContext) {
		this.pddContext = pddContext;
	}
	
}
