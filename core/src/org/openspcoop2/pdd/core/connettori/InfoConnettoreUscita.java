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


package org.openspcoop2.pdd.core.connettori;

import org.openspcoop2.pdd.core.autenticazione.Credenziali;

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
	private java.util.Hashtable<String,String> properties;
	/** Indicazione su di un eventuale sbustamento SOAP */
	private boolean sbustamentoSoap;
	/** Indicazione su di un eventuale sbustamento delle informazioni di Protocollo */
	private boolean sbustamentoInformazioniProtocollo;
	/** Proprieta' di trasporto che deve utilizzare il connettore */
	private java.util.Properties propertiesTrasporto;
	/** Proprieta' urlBased che deve utilizzare il connettore */
	private java.util.Properties propertiesUrlBased;
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
	public java.util.Hashtable<String, String> getProperties() {
		return this.properties;
	}
	public void setProperties(java.util.Hashtable<String, String> properties) {
		this.properties = properties;
	}
	public boolean isSbustamentoSoap() {
		return this.sbustamentoSoap;
	}
	public void setSbustamentoSoap(boolean sbustamentoSoap) {
		this.sbustamentoSoap = sbustamentoSoap;
	}
	public java.util.Properties getPropertiesTrasporto() {
		return this.propertiesTrasporto;
	}
	public void setPropertiesTrasporto(java.util.Properties propertiesTrasporto) {
		this.propertiesTrasporto = propertiesTrasporto;
	}
	public java.util.Properties getPropertiesUrlBased() {
		return this.propertiesUrlBased;
	}
	public void setPropertiesUrlBased(java.util.Properties propertiesUrlBased) {
		this.propertiesUrlBased = propertiesUrlBased;
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
	public boolean isSbustamentoInformazioniProtocollo() {
		return this.sbustamentoInformazioniProtocollo;
	}
	public void setSbustamentoInformazioniProtocollo(
			boolean sbustamentoInformazioniProtocollo) {
		this.sbustamentoInformazioniProtocollo = sbustamentoInformazioniProtocollo;
	}
	
}
