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
package org.openspcoop2.web.monitor.statistiche.bean;

import java.io.Serializable;

import org.openspcoop2.core.transazioni.constants.PddRuolo;

/**
 * ConfigurazioneGenerale
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ConfigurazioneGenerale implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	private ConfigurazioneGeneralePK id = null;
	private String label = null;
	private String value = null;
	private String stato = null;
	
	// informazioni estese per la tabella
	private String fruitore = null;
	private String erogatore = null;
	private String servizio = null;
	private String azione = null;
	
	private String protocollo = null;
	
	private Long dbId = null;
	private PddRuolo ruolo = null;
	
	private DettaglioPA pa = null;
	private DettaglioPD pd = null;
	
	public ConfigurazioneGenerale(){
		this.label = "";
		this.value = "";
	}
	
	public ConfigurazioneGenerale(Long dbId, PddRuolo ruolo){
		this.dbId = dbId;
		this.ruolo = ruolo;
		this.id = new ConfigurazioneGeneralePK(dbId, ruolo); 
	}
	
	public String getLabel() {
		return this.label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getValue() {
		return this.value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public ConfigurazioneGeneralePK getId() {
		return this.id;
	}

	public void setId(ConfigurazioneGeneralePK id) {
		this.id = id;
	}

	public String getFruitore() {
		return this.fruitore;
	}

	public void setFruitore(String fruitore) {
		this.fruitore = fruitore;
	}

	public String getErogatore() {
		return this.erogatore;
	}

	public void setErogatore(String erogatore) {
		this.erogatore = erogatore;
	}

	public String getServizio() {
		return this.servizio;
	}

	public void setServizio(String servizio) {
		this.servizio = servizio;
	}

	public String getAzione() {
		return this.azione;
	}

	public void setAzione(String azione) {
		this.azione = azione;
	}

	public Long getDbId() {
		return this.dbId;
	}

	public void setDbId(Long dbId) {
		this.dbId = dbId;
	}

	public PddRuolo getRuolo() {
		return this.ruolo;
	}

	public void setRuolo(PddRuolo ruolo) {
		this.ruolo = ruolo;
	}

	public DettaglioPA getPa() {
		return this.pa;
	}

	public void setPa(DettaglioPA pa) {
		this.pa = pa;
	}

	public DettaglioPD getPd() {
		return this.pd;
	}

	public void setPd(DettaglioPD pd) {
		this.pd = pd;
	}

	public String getStato() {
		return this.stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}
	
	public String getNome() {
		if(this.pa != null) {
			return this.pa.getPortaApplicativa().getNome();
		} 
		
		if(this.pd != null) {
			return this.pd.getPortaDelegata().getNome();
		} 
		
		return this.label;
	}

	public String getProtocollo() {
		return this.protocollo;
	}

	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
	}
	
	
}
