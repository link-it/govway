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


package org.openspcoop2.core.config;

import java.io.Serializable;

/**
 * Classe utilizzata per rappresentare le informazioni utilizzate dalla porta di dominio per accedere alla configurazione..
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccessoConfigurazionePdD extends org.openspcoop2.utils.beans.BaseBean  implements Serializable , Cloneable{

	private static final long serialVersionUID = 1L;
	
	// Configurazione
	/** Tipo di Configurazione */
	private String tipo;
	/** Location della Configurazione */
	private String location;
	/** Eventuale tipo di database */
	private String tipoDatabase;
	/** Contesto della Configurazione */
	private java.util.Properties context;
	/** condivisione database registro e pdd */
	private boolean condivisioneDatabasePddRegistro;



	/** ---------------- SETTER ---------------------- */

	/**
	 * Tipo di configurazione
	 * 
	 * @param tipo
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	/**
	 * Location della configurazione
	 * 
	 * @param location
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	/**
	 * Contesto per la configurazione
	 * 
	 * @param context
	 */
	public void setContext(java.util.Properties context) {
		this.context = context;
	}

	public void setTipoDatabase(String tipoDatabase) {
		this.tipoDatabase = tipoDatabase;
	}

	public void setCondivisioneDatabasePddRegistro(
			boolean condivisioneDatabasePddRegistro) {
		this.condivisioneDatabasePddRegistro = condivisioneDatabasePddRegistro;
	}







	/** ---------------- GETTER ---------------------- */

	/**
	 * Tipo di Configurazione
	 * 
	 * @return Tipo di Configurazione
	 */
	public String getTipo() {
		return this.tipo;
	}
	/**
	 * Location della Configurazione
	 * 
	 * @return Location della Configurazione
	 */
	public String getLocation() {
		return this.location;
	}
	/**
	 * Contesto della Configurazione
	 * 
	 * @return Contesto della Configurazione
	 */
	public java.util.Properties getContext() {
		return this.context;
	}

	public String getTipoDatabase() {
		return this.tipoDatabase;
	}
	
	public boolean isCondivisioneDatabasePddRegistro() {
		return this.condivisioneDatabasePddRegistro;
	}

}
