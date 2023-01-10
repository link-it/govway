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


package org.openspcoop2.pdd.core.connettori;

import java.util.List;
import java.util.Map;

import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.pdd.core.credenziali.Credenziali;

/**
 * InfoConnettoreUscita
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InfoConnettoreUscita {

	/** Tipo di Connettore */
	private String tipoConnettore;
	/** Location */
	private String location;
	/** Proprieta' del connettore */
	private java.util.Map<String,String> properties;
	/** Indicazione su di un eventuale sbustamento SOAP */
	private boolean sbustamentoSoap;
	/** Indicazione su di un eventuale sbustamento delle informazioni di Protocollo */
	private boolean sbustamentoInformazioniProtocollo;
	/** Proprieta' di trasporto che deve utilizzare il connettore */
	private Map<String, List<String>> headers;
	/** Proprieta' urlBased che deve utilizzare il connettore */
	private Map<String, List<String>> parameters;
	/** Tipo di Autenticazione */
	private String tipoAutenticazione;
	/** Credenziali */
	private Credenziali credenziali;
	
	public String getTipoConnettore() {
		return this.tipoConnettore;
	}
	public void setTipoConnettore(String tipoConnettore) {
		this.tipoConnettore = tipoConnettore;
	}
	public String getLocation() {
		return this.location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public java.util.Map<String, String> getProperties() {
		return this.properties;
	}
	public void setProperties(java.util.Map<String, String> properties) {
		this.properties = properties;
	}
	public boolean isSbustamentoSoap() {
		return this.sbustamentoSoap;
	}
	public void setSbustamentoSoap(boolean sbustamentoSoap) {
		this.sbustamentoSoap = sbustamentoSoap;
	}
	public Map<String, List<String>> getHeaders() {
		return this.headers;
	}
	public void setHeaders(Map<String, List<String>> propertiesTrasporto) {
		this.headers = propertiesTrasporto;
	}
	public Map<String, List<String>> getParameters() {
		return this.parameters;
	}
	public void setParameters(Map<String, List<String>> propertiesUrlBased) {
		this.parameters = propertiesUrlBased;
	}
	public String getTipoAutenticazione() {
		return this.tipoAutenticazione;
	}
	public void setTipoAutenticazione(String tipoAutenticazione) {
		this.tipoAutenticazione = tipoAutenticazione;
	}
	public Credenziali getCredenziali() {
		return this.credenziali;
	}
	public void setCredenziali(Credenziali credenziali) {
		this.credenziali = credenziali;
	}
	public void setCredenziali(InvocazioneCredenziali credenziali) {
		if(credenziali!=null){
			this.credenziali = new Credenziali();
			this.credenziali.setUsername(credenziali.getUser());
			this.credenziali.setPassword(credenziali.getPassword());
		}
	}
	public boolean isSbustamentoInformazioniProtocollo() {
		return this.sbustamentoInformazioniProtocollo;
	}
	public void setSbustamentoInformazioniProtocollo(
			boolean sbustamentoInformazioniProtocollo) {
		this.sbustamentoInformazioniProtocollo = sbustamentoInformazioniProtocollo;
	}
	
}
