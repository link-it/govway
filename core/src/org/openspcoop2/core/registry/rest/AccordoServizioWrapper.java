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


package org.openspcoop2.core.registry.rest;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.utils.rest.api.Api;

/**
 * Classe utilizzata per rappresentare l'api che forma un accordo di un servizio
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordoServizioWrapper implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Schema XSD */
	private transient Api api = null;

	public Api getApi() {
		return this.api;
	}
	public void setApi(Api api) {
		this.api = api;
	}
	private org.openspcoop2.utils.Semaphore semaphore = new org.openspcoop2.utils.Semaphore("AccordoServizioWrapperREST");
	public void updateApi(Api api) {
		this.semaphore.acquireThrowRuntime("updateAPI");
		try {
			this.api = api;
		}finally {
			this.semaphore.release("updateAPI");
		}
	}


	/** Nome accordo di servizio */
	private IDAccordo idAccordoServizio;
	public IDAccordo getIdAccordoServizio() {
		return this.idAccordoServizio;
	}
	public void setIdAccordoServizio(IDAccordo idAccordoServizio) {
		this.idAccordoServizio = idAccordoServizio;
	}
	

	/** Accordo di Servizio OpenSPCoop */
	private org.openspcoop2.core.registry.AccordoServizioParteComune accordoServizio;
	public org.openspcoop2.core.registry.AccordoServizioParteComune getAccordoServizio() {
		return this.accordoServizio;
	}
	public void setAccordoServizio(
			org.openspcoop2.core.registry.AccordoServizioParteComune accordoServizio) {
		this.accordoServizio = accordoServizio;
	}

	
	/** specifica */
	private String locationSpecifica;
	private byte[] bytesSpecifica;
	public String getLocationSpecifica() {
		return this.locationSpecifica;
	}
	public void setLocationSpecifica(String locationSpecifica) {
		this.locationSpecifica = locationSpecifica;
	}
	public byte[] getBytesSpecifica() {
		return this.bytesSpecifica;
	}
	public void setBytesSpecifica(byte[] bytesSpecifica) {
		this.bytesSpecifica = bytesSpecifica;
	}

	/** Indicazione se il registro e' su DB */
	private boolean registroServiziDB = false;
	public boolean isRegistroServiziDB() {
		return this.registroServiziDB;
	}
	public void setRegistroServiziDB(boolean registroServiziDB) {
		this.registroServiziDB = registroServiziDB;
	}
	
	@Override
	public AccordoServizioWrapper clone(){

		AccordoServizioWrapper as = new AccordoServizioWrapper();
		
		if(this.accordoServizio!=null)
			as.accordoServizio = (AccordoServizioParteComune) this.accordoServizio.clone();
				
		if(this.idAccordoServizio!=null)
			as.idAccordoServizio = this.idAccordoServizio.clone();
		
		if(this.locationSpecifica!=null) {
			as.locationSpecifica = new String(this.locationSpecifica);
		}
		as.bytesSpecifica = this.bytesSpecifica;

		if(this.api!=null) {
			as.api = this.api;
		}
		
		as.registroServiziDB = this.registroServiziDB;

		return as;
	}

}

