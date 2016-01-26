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

package org.openspcoop2.pdd.core.handlers.statistics;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;

/**
 * Statistic
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Statistic {

	private TipoPdD tipoPdD;
	
	private EsitoTransazione esito;
	
	private long timeMillisIngressoRichiesta = -1;
	private long timeMillisUscitaRichiesta = -1;
	private long timeMillisIngressoRisposta = -1;
	private long timeMillisUscitaRisposta = -1;
	
	private long dimensioneIngressoRichiesta = -1;
	private long dimensioneUscitaRichiesta = -1;
	private long dimensioneIngressoRisposta = -1;
	private long dimensioneUscitaRisposta = -1;
	
	public TipoPdD getTipoPdD() {
		return this.tipoPdD;
	}
	public void setTipoPdD(TipoPdD tipoPdD) {
		this.tipoPdD = tipoPdD;
	}
	public EsitoTransazione getEsito() {
		return this.esito;
	}
	public void setEsito(EsitoTransazione esito) {
		this.esito = esito;
	}
	public long getTimeMillisIngressoRichiesta() {
		return this.timeMillisIngressoRichiesta;
	}
	public void setTimeMillisIngressoRichiesta(long timeMillisIngressoRichiesta) {
		this.timeMillisIngressoRichiesta = timeMillisIngressoRichiesta;
	}
	public long getTimeMillisUscitaRichiesta() {
		return this.timeMillisUscitaRichiesta;
	}
	public void setTimeMillisUscitaRichiesta(long timeMillisUscitaRichiesta) {
		this.timeMillisUscitaRichiesta = timeMillisUscitaRichiesta;
	}
	public long getTimeMillisIngressoRisposta() {
		return this.timeMillisIngressoRisposta;
	}
	public void setTimeMillisIngressoRisposta(long timeMillisIngressoRisposta) {
		this.timeMillisIngressoRisposta = timeMillisIngressoRisposta;
	}
	public long getTimeMillisUscitaRisposta() {
		return this.timeMillisUscitaRisposta;
	}
	public void setTimeMillisUscitaRisposta(long timeMillisUscitaRisposta) {
		this.timeMillisUscitaRisposta = timeMillisUscitaRisposta;
	}
	public long getDimensioneIngressoRichiesta() {
		return this.dimensioneIngressoRichiesta;
	}
	public void setDimensioneIngressoRichiesta(long dimensioneIngressoRichiesta) {
		this.dimensioneIngressoRichiesta = dimensioneIngressoRichiesta;
	}
	public long getDimensioneUscitaRichiesta() {
		return this.dimensioneUscitaRichiesta;
	}
	public void setDimensioneUscitaRichiesta(long dimensioneUscitaRichiesta) {
		this.dimensioneUscitaRichiesta = dimensioneUscitaRichiesta;
	}
	public long getDimensioneIngressoRisposta() {
		return this.dimensioneIngressoRisposta;
	}
	public void setDimensioneIngressoRisposta(long dimensioneIngressoRisposta) {
		this.dimensioneIngressoRisposta = dimensioneIngressoRisposta;
	}
	public long getDimensioneUscitaRisposta() {
		return this.dimensioneUscitaRisposta;
	}
	public void setDimensioneUscitaRisposta(long dimensioneUscitaRisposta) {
		this.dimensioneUscitaRisposta = dimensioneUscitaRisposta;
	}
	
}
