/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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



package org.openspcoop2.core.registry.driver;

import java.io.Serializable;

import org.openspcoop2.core.constants.TipologiaServizio;
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
	
	/** id Accordo di Servizio Parte Comune */
	private IDAccordo idAccordoServizioParteComune;
	
	/** tipo Soggetto Erogatore */
	private String tipoSoggettoErogatore;
	/** nome Soggetto Erogatore */
	private String nomeSoggettoErogatore;
	
	/** Versione */
	private Integer versione;
	
	
	private TipologiaServizio tipologia;
	
	public TipologiaServizio getTipologia() {
		return this.tipologia;
	}
	public void setTipologia(TipologiaServizio tipologiaServizio) {
		this.tipologia = tipologiaServizio;
	}
	public IDAccordo getIdAccordoServizioParteComune() {
		return this.idAccordoServizioParteComune;
	}
	public void setIdAccordoServizioParteComune(IDAccordo idAccordo) {
		this.idAccordoServizioParteComune = idAccordo;
	}
	
	public Integer getVersione() {
		return this.versione;
	}

	public void setVersione(Integer versione) {
		this.versione = versione;
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
		this.addDetails(bf);
		if(bf.length()=="Filtro Servizi:".length())
			bf.append(" nessun filtro presente");
		return bf.toString();
	}
	@Override
	public void addDetails(StringBuffer bf){
		if(this.idAccordoServizioParteComune!=null)
			bf.append(" [accordo-servizio-parte-comune:"+this.idAccordoServizioParteComune+"]");
		if(this.tipoSoggettoErogatore!=null)
			bf.append(" [tipo-soggetto-erogatore:"+this.tipoSoggettoErogatore+"]");
		if(this.nomeSoggettoErogatore!=null)
			bf.append(" [nome-soggetto-erogatore:"+this.nomeSoggettoErogatore+"]");
		if(this.tipologia!=null)
			bf.append(" [tipologia-servizio:"+this.tipologia+"]");
		if(this.versione!=null)
			bf.append(" [versione:"+this.versione+"]");
		super.addDetails(bf);
	}

}
