/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.services.skeleton;

import java.util.Date;

/**
 * MessaggioIM
 *
 * @author apoli@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessaggioIM {

	private boolean authorized;
	private boolean sbustamentoSoap;
	private boolean sbustamentoInformazioniProtocollo;
	private String riferimentoMessaggio;
	private String identificativoRichiesta; // risolto in caso di accesso per riferimentoMessaggio
	private String idTransazione;
	private String servizioApplicativo;
	private Date oraRegistrazione;
	private String nomePorta;
	
	
	public String getNomePorta() {
		return this.nomePorta;
	}

	public void setNomePorta(String nomePorta) {
		this.nomePorta = nomePorta;
	}

	public Date getOraRegistrazione() {
		return this.oraRegistrazione;
	}

	public void setOraRegistrazione(Date oraRegistrazione) {
		this.oraRegistrazione = oraRegistrazione;
	}

	public String getServizioApplicativo() {
		return this.servizioApplicativo;
	}

	public void setServizioApplicativo(String servizioApplicativo) {
		this.servizioApplicativo = servizioApplicativo;
	}

	public String getIdTransazione() {
		return this.idTransazione;
	}

	public void setIdTransazione(String idTransazione) {
		this.idTransazione = idTransazione;
	}

	public String getIdentificativoRichiesta() {
		return this.identificativoRichiesta;
	}

	public void setIdentificativoRichiesta(String identificativoRichiesta) {
		this.identificativoRichiesta = identificativoRichiesta;
	}

	public String getRiferimentoMessaggio() {
		return this.riferimentoMessaggio;
	}

	public void setRiferimentoMessaggio(String riferimentoMessaggio) {
		this.riferimentoMessaggio = riferimentoMessaggio;
	}

	public boolean isSbustamentoInformazioniProtocollo() {
		return this.sbustamentoInformazioniProtocollo;
	}

	public void setSbustamentoInformazioniProtocollo(boolean sbustamentoInformazioniProtocollo) {
		this.sbustamentoInformazioniProtocollo = sbustamentoInformazioniProtocollo;
	}

	public boolean isSbustamentoSoap() {
		return this.sbustamentoSoap;
	}

	public void setSbustamentoSoap(boolean sbustamentoSoap) {
		this.sbustamentoSoap = sbustamentoSoap;
	}

	public boolean isAuthorized() {
		return this.authorized;
	}

	public void setAuthorized(boolean authorized) {
		this.authorized = authorized;
	}
	
}
