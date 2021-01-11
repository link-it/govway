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
package org.openspcoop2.web.monitor.statistiche.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.ServizioApplicativo;

import org.openspcoop2.core.commons.search.IdAccordoServizioParteComune;
import org.openspcoop2.core.commons.search.PortaApplicativa;

/**
 * DettaglioPA
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class DettaglioPA implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PortaApplicativa portaApplicativa = null;
	private org.openspcoop2.core.config.PortaApplicativa portaApplicativaOp2 = null;
	private boolean supportatoAutenticazione = true;
	private List<String> fruitori = null;
	private List<String> ruoli = null;
	private String matchRuoli = "";
	private List<DettaglioSA> listaSA = null;
	private List<Property> propertyGenerali = null;
	private List<Property> propertyAutorizzazione = null;
	private List<Property> propertyAutenticazione = null;
	private List<List<Property>> propertyConnettore = null;
	private IdAccordoServizioParteComune idAccordoServizioParteComune = null;
	private String portType = null;
	private List<String> azioni = null;
	private String urlInvocazione = null;
	private boolean trasparente= false;
	private List<Property> propertyIntegrazione = null;
	
	public DettaglioPA(){
		this.ruoli = new ArrayList<String>();
		this.fruitori = new ArrayList<String>();
		this.listaSA = new ArrayList<DettaglioSA>();
		this.azioni = new ArrayList<String>();
	}

	public PortaApplicativa getPortaApplicativa() {
		return this.portaApplicativa;
	}

	public void setPortaApplicativa(PortaApplicativa portaApplicativa) {
		this.portaApplicativa = portaApplicativa;
	}

	public org.openspcoop2.core.config.PortaApplicativa getPortaApplicativaOp2() {
		return this.portaApplicativaOp2;
	}

	public void setPortaApplicativaOp2(org.openspcoop2.core.config.PortaApplicativa portaApplicativaOp2) {
		this.portaApplicativaOp2 = portaApplicativaOp2;
	}

	public boolean isSupportatoAutenticazione() {
		return this.supportatoAutenticazione;
	}

	public void setSupportatoAutenticazione(boolean supportatoAutenticazione) {
		this.supportatoAutenticazione = supportatoAutenticazione;
	}

	public List<String> getFruitori() {
		return this.fruitori;
	}

	public void setFruitori(List<String> fruitori) {
		this.fruitori = fruitori;
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

	public List<DettaglioSA> getListaSA() {
		return this.listaSA;
	}

	public void setListaSA(List<DettaglioSA> listaSA) {
		this.listaSA = listaSA;
	}

	public List<Property> getPropertyGenerali() {
		return this.propertyGenerali;
	}

	public void setPropertyGenerali(List<Property> propertyGenerali) {
		this.propertyGenerali = propertyGenerali;
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

	public List<List<Property>> getPropertyConnettore() {
		return this.propertyConnettore;
	}

	public void setPropertyConnettore(List<List<Property>> propertyConnettore) {
		this.propertyConnettore = propertyConnettore;
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


	public List<String> getAzioni() {
		return this.azioni;
	}

	public void setAzioni(List<String> azioni) {
		this.azioni = azioni;
	}

	public String getUrlInvocazione() {
		return this.urlInvocazione;
	}

	public void setUrlInvocazione(String urlInvocazione) {
		this.urlInvocazione = urlInvocazione;
	}
	
	public boolean isTrasparente() {
		return this.trasparente;
	}

	public void setTrasparente(boolean trasparente) {
		this.trasparente = trasparente;
	}

	public List<Property> getPropertyIntegrazione() {
		return this.propertyIntegrazione;
	}

	public void setPropertyIntegrazione(List<Property> propertyIntegrazione) {
		this.propertyIntegrazione = propertyIntegrazione;
	}



	public class DettaglioSA implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private List<Property> propertyConnettore = null;
		private List<Property> propertySA = null;
		private ServizioApplicativo saOp2 = null;
		
		public List<Property> getPropertyConnettore() {
			return this.propertyConnettore;
		}
		public void setPropertyConnettore(List<Property> propertyConnettore) {
			this.propertyConnettore = propertyConnettore;
		}
		public ServizioApplicativo getSaOp2() {
			return this.saOp2;
		}
		public void setSaOp2(ServizioApplicativo saOp2) {
			this.saOp2 = saOp2;
		}
		public List<Property> getPropertySA() {
			return this.propertySA;
		}
		public void setPropertySA(List<Property> propertySA) {
			this.propertySA = propertySA;
		}
		
		
	}
}
