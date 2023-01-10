/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.controllo_traffico;

import java.io.Serializable;

/**
 * SoglieDimensioneMessaggi
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SogliaDimensioneMessaggio implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean policyGlobale = false;
	private long sogliaKb = -1;
	private String nomePolicy;
	private String idPolicyConGruppo;
	private String configurazione;
	
	public boolean isPolicyGlobale() {
		return this.policyGlobale;
	}
	public void setPolicyGlobale(boolean policyGlobale) {
		this.policyGlobale = policyGlobale;
	}
	public long getSogliaKb() {
		return this.sogliaKb;
	}
	public void setSogliaKb(long sogliaKb) {
		this.sogliaKb = sogliaKb;
	}
	public String getNomePolicy() {
		return this.nomePolicy;
	}
	public void setNomePolicy(String nomePolicy) {
		this.nomePolicy = nomePolicy;
	}
	public String getIdPolicyConGruppo() {
		return this.idPolicyConGruppo;
	}
	public void setIdPolicyConGruppo(String idPolicyConGruppo) {
		this.idPolicyConGruppo = idPolicyConGruppo;
	}
	public String getConfigurazione() {
		return this.configurazione;
	}
	public void setConfigurazione(String configurazione) {
		this.configurazione = configurazione;
	}
}
