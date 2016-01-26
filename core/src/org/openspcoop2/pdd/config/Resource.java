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



package org.openspcoop2.pdd.config;

import java.util.Date;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.utils.date.UniqueIDGenerator;

/**
 * Identifica una risorsa rilasciata dai Manager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Resource {

	/** Identificatore univoco risorsa */
	private String id = null;
	
	/** Data di rilascio della risorsa */
	private Date date = null;
	
	/** Identificativo Porta richiedente della risorsa */
	private IDSoggetto identificativoPorta = null;
	
	/** Modulo funzionale richiedente della risorsa */
	private String moduloFunzionale = null;
	
	/** IDTransazione */
	private String idTransazione = null;
	
	/** Tipo della risorsa */
	private String resourceType = null;
	
	/** Risorsa */
	private Object resource = null;

	
	public static String generaIdentificatoreUnivoco(IDSoggetto identificativoPorta,String moduloFunzionale){
		return identificativoPorta.getCodicePorta()+"_"+moduloFunzionale+"_"+UniqueIDGenerator.getUniqueID();
	}
	
	/**
	 * @return the identificatoreUnivocoRisorsa
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @param identificatoreUnivocoRisorsa the identificatoreUnivocoRisorsa to set
	 */
	public void setId(String identificatoreUnivocoRisorsa) {
		this.id = identificatoreUnivocoRisorsa;
	}

	/**
	 * @return the dataRilascioRisorsa
	 */
	public Date getDate() {
		return this.date;
	}

	/**
	 * @param dataRilascioRisorsa the dataRilascioRisorsa to set
	 */
	public void setDate(Date dataRilascioRisorsa) {
		this.date = dataRilascioRisorsa;
	}

	/**
	 * @return the identificativoPorta
	 */
	public IDSoggetto getIdentificativoPorta() {
		return this.identificativoPorta;
	}

	/**
	 * @param identificativoPorta the identificativoPorta to set
	 */
	public void setIdentificativoPorta(IDSoggetto identificativoPorta) {
		this.identificativoPorta = identificativoPorta;
	}

	/**
	 * @return the moduloFunzionale
	 */
	public String getModuloFunzionale() {
		return this.moduloFunzionale;
	}

	/**
	 * @param moduloFunzionale the moduloFunzionale to set
	 */
	public void setModuloFunzionale(String moduloFunzionale) {
		this.moduloFunzionale = moduloFunzionale;
	}

	/**
	 * @return the tipoRisorsa
	 */
	public String getResourceType() {
		return this.resourceType;
	}

	/**
	 * @param tipoRisorsa the tipoRisorsa to set
	 */
	public void setResourceType(String tipoRisorsa) {
		this.resourceType = tipoRisorsa;
	}

	/**
	 * @return the risorsa
	 */
	public Object getResource() {
		return this.resource;
	}

	/**
	 * @param risorsa the risorsa to set
	 */
	public void setResource(Object risorsa) {
		this.resource = risorsa;
	}
	
	public String getIdTransazione() {
		return this.idTransazione;
	}

	public void setIdTransazione(String idTransazione) {
		this.idTransazione = idTransazione;
	}
	
}
