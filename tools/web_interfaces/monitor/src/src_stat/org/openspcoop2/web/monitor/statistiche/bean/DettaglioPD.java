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
package org.openspcoop2.web.monitor.statistiche.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.Property;

import org.openspcoop2.core.commons.search.IdAccordoServizioParteComune;
import org.openspcoop2.core.commons.search.PortaDelegata;

/**
 * DettaglioPD
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class DettaglioPD implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Connettore connettore = null;
	private PortaDelegata portaDelegata = null; 
	private org.openspcoop2.core.config.PortaDelegata portaDelegataOp2 = null;
	
	private List<String> azioni = null;
	private List<String> ruoli = null;
	private String matchRuoli = "";
	private List<String> sa = null;
	private List<Property> propertyGenerali = null;
	private List<Property> propertyConnettore = null;
	private List<Property> propertyAutorizzazione = null;
	private List<Property> propertyAutenticazione = null;
	private List<Property> propertyIntegrazione = null;
	private IdAccordoServizioParteComune idAccordoServizioParteComune = null;
	private String portType = null;
	private String urlInvocazione = null;

	public DettaglioPD(){
		this.azioni = new ArrayList<String>();
		this.ruoli = new ArrayList<String>();
		this.sa = new ArrayList<String>();
	}
	
	public Connettore getConnettore() {
		return this.connettore;
	}
	public void setConnettore(Connettore connettore) {
		this.connettore = connettore;
	}
	public PortaDelegata getPortaDelegata() {
		return this.portaDelegata;
	}
	public void setPortaDelegata(PortaDelegata portaDelegata) {
		this.portaDelegata = portaDelegata;
	}
	public org.openspcoop2.core.config.PortaDelegata getPortaDelegataOp2() {
		return this.portaDelegataOp2;
	}
	public void setPortaDelegataOp2(org.openspcoop2.core.config.PortaDelegata portaDelegataOp2) {
		this.portaDelegataOp2 = portaDelegataOp2;
	}
	public List<String> getAzioni() {
		return this.azioni;
	}
	public void setAzioni(List<String> azioni) {
		this.azioni = azioni;
	}

	public List<String> getRuoli() {
		return this.ruoli;
	}

	public void setRuoli(List<String> ruoli) {
		this.ruoli = ruoli;
	}

	public String getMatchRuoli() {
		return this.matchRuoli;
	}

	public void setMatchRuoli(String matchRuoli) {
		this.matchRuoli = matchRuoli;
	}

	public List<String> getSa() {
		return this.sa;
	}

	public void setSa(List<String> sa) {
		this.sa = sa;
	}

	public List<Property> getPropertyConnettore() {
		return this.propertyConnettore;
	}

	public void setPropertyConnettore(List<Property> propertyConnettore) {
		this.propertyConnettore = propertyConnettore;
	}

	public List<Property> getPropertyAutorizzazione() {
		return this.propertyAutorizzazione;
	}

	public void setPropertyAutorizzazione(List<Property> propertyAutorizzazione) {
		this.propertyAutorizzazione = propertyAutorizzazione;
	}

	public List<Property> getPropertyAutenticazione() {
		return this.propertyAutenticazione;
	}

	public void setPropertyAutenticazione(List<Property> propertyAutenticazione) {
		this.propertyAutenticazione = propertyAutenticazione;
	}

	public List<Property> getPropertyIntegrazione() {
		return this.propertyIntegrazione;
	}

	public void setPropertyIntegrazione(List<Property> propertyIntegrazione) {
		this.propertyIntegrazione = propertyIntegrazione;
	}

	public List<Property> getPropertyGenerali() {
		return this.propertyGenerali;
	}

	public void setPropertyGenerali(List<Property> propertyGenerali) {
		this.propertyGenerali = propertyGenerali;
	}
	
	public IdAccordoServizioParteComune getIdAccordoServizioParteComune() {
		return this.idAccordoServizioParteComune;
	}

	public void setIdAccordoServizioParteComune(IdAccordoServizioParteComune idAccordoServizioParteComune) {
		this.idAccordoServizioParteComune = idAccordoServizioParteComune;
	}


	public String getPortType() {
		return this.portType;
	}

	public void setPortType(String portType) {
		this.portType = portType;
	}

	public String getUrlInvocazione() {
		return this.urlInvocazione;
	}

	public void setUrlInvocazione(String urlInvocazione) {
		this.urlInvocazione = urlInvocazione;
	}
	
}
