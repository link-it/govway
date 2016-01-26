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



package org.openspcoop2.core.registry.driver;

import java.io.Serializable;

import org.openspcoop2.core.id.IDAccordo;


/**
 * Permette il filtro di ricerca attraverso i driver che implementano l'interfaccia 'get'
 * 
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class FiltroRicercaServizi extends FiltroRicerca implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** id Accordo di Servizio */
	private IDAccordo idAccordo;
	/** tipo Soggetto Erogatore */
	private String tipoSoggettoErogatore;
	/** nome Soggetto Erogatore */
	private String nomeSoggettoErogatore;
	private String tipologiaServizio;
	
	public String getTipologiaServizio() {
		return this.tipologiaServizio;
	}
	public void setTipologiaServizio(String tipologiaServizio) {
		this.tipologiaServizio = tipologiaServizio;
	}
	public IDAccordo getIdAccordo() {
		return this.idAccordo;
	}
	public void setIdAccordo(IDAccordo idAccordo) {
		this.idAccordo = idAccordo;
	}
	
	/**
	 * @return the nomeSoggettoErogatore
	 */
	public String getNomeSoggettoErogatore() {
		return this.nomeSoggettoErogatore;
	}
	/**
	 * @param nomeSoggettoErogatore the nomeSoggettoErogatore to set
	 */
	public void setNomeSoggettoErogatore(String nomeSoggettoErogatore) {
		this.nomeSoggettoErogatore = nomeSoggettoErogatore;
	}
	/**
	 * @return the tipoSoggettoErogatore
	 */
	public String getTipoSoggettoErogatore() {
		return this.tipoSoggettoErogatore;
	}
	/**
	 * @param tipoSoggettoErogatore the tipoSoggettoErogatore to set
	 */
	public void setTipoSoggettoErogatore(String tipoSoggettoErogatore) {
		this.tipoSoggettoErogatore = tipoSoggettoErogatore;
	}
	
	
	
	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer();
		bf.append("Filtro Servizi:");
		if(this.getMinDate()!=null)
			bf.append(" [intervallo-inferiore-data:"+this.getMinDate()+"]");
		if(this.getMaxDate()!=null)
			bf.append(" [intervallo-superiore-data:"+this.getMaxDate()+"]");
		if(this.getTipo()!=null)
			bf.append(" [tipo:"+this.getTipo()+"]");
		if(this.getNome()!=null)
			bf.append(" [nome:"+this.getNome()+"]");
		if(this.idAccordo!=null)
			bf.append(" [accordo-servizio:"+this.idAccordo+"]");
		if(this.tipoSoggettoErogatore!=null)
			bf.append(" [tipo-soggetto-erogatore:"+this.tipoSoggettoErogatore+"]");
		if(this.nomeSoggettoErogatore!=null)
			bf.append(" [nome-soggetto-erogatore:"+this.nomeSoggettoErogatore+"]");
		if(bf.length()=="Filtro Servizi:".length())
			bf.append(" nessun filtro presente");
		return bf.toString();
	}

}
