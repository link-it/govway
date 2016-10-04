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



package org.openspcoop2.protocol.sdk;

/**
 * Dati integrativi in appoggio inseriti nel repository
 *  
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Integrazione {



	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Identificatore del modulo OpenSPCoop che ha gestito la richiesta, e che sta aspettando una risposta */
	private String idModuloInAttesa;
	/** Location della Porta Delegata Richiesta */
	private String locationPD;
	/** Nome del Servizio Applicativo che sta' richiedendo il servizio */
	private String servizioApplicativo;
	/** Indica il tipo di scenario di cooperazione da intraprendere. */
	private String scenario;
	/** Indica la modalita' di gestione stateless/stateful */
	private boolean stateless = false;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Costruttore. 
	 *
	 * 
	 */
	public Integrazione(){
	}
	/**
	 * Costruttore. 
	 *
	 * @param idModulo Identificatore del modulo OpenSPCoop che sta aspettando una risposta.
	 * @param aLocation Location della Porta Delegata.
	 * @param idServizio Nome del Servizio Applicativo.
	 * 
	 */
	public Integrazione(String idModulo,String aLocation, String idServizio){
		this.idModuloInAttesa = idModulo;
		this.locationPD = aLocation;
		this.servizioApplicativo = idServizio;
	}




	/* ********  S E T T E R   ******** */
	/**
	 * Identificatore del modulo OpenSPCoop che ha gestito la richiesta, 
	 * e che sta aspettando una risposta.
	 *
	 * @param idModulo Identificatore del modulo OpenSPCoop che sta aspettando una risposta.
	 * 
	 */
	public void setIdModuloInAttesa(String idModulo){
		this.idModuloInAttesa = idModulo;
	} 
	/**
	 * Imposta la Location della Porta Delegata Richiesta
	 *
	 * @param aLocation Location della Porta Delegata.
	 * 
	 */
	public void setLocationPD(String aLocation){
		this.locationPD = aLocation;
	}
	/**
	 * Imposta il nome del Servizio Applicativo che sta' richiedendo il servizio
	 *
	 * @param idServizio Nome del Servizio Applicativo.
	 * 
	 */
	public void setServizioApplicativo(String idServizio){
		this.servizioApplicativo = idServizio;
	} 
	/**
	 * Imposta il tipo di scenario di cooperazione da intraprendere
	 * 
	 * @param scenario tipo di scenario di cooperazione da intraprendere
	 * 
	 */
	public void setScenario(String scenario) {
		this.scenario = scenario;
	}

	public void setStateless(boolean stateless) {
		this.stateless = stateless;
	}




	/* ********  G E T T E R   ******** */
	/**
	 * Ritorna l'identificatore del modulo OpenSPCoop che ha gestito la richiesta, 
	 * e che sta aspettando una risposta.
	 *
	 * @return Identificatore del modulo OpenSPCoop che sta aspettando una risposta.
	 * 
	 */
	public String getIdModuloInAttesa(){
		return this.idModuloInAttesa;
	} 
	/**
	 * Ritorna la Location della Porta Delegata Richiesta
	 *
	 * @return Location della Porta Delegata.
	 * 
	 */
	public String getLocationPD(){
		return this.locationPD;
	}
	/**
	 * Ritorna il nome del Servizio Applicativo che sta' richiedendo il servizio
	 *
	 * @return Nome del Servizio Applicativo.
	 * 
	 */
	public String getServizioApplicativo(){
		return this.servizioApplicativo;
	} 
	/**
	 * Ritorna il tipo di scenario di cooperazione da intraprendere
	 * 
	 * @return tipo di scenario di cooperazione da intraprendere
	 * 
	 */
	public String getScenario() {
		return this.scenario;
	}
	
	public boolean isStateless() {
		return this.stateless;
	}
	
}
