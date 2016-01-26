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



package org.openspcoop2.core.config.driver;

import java.io.Serializable;

/**
 * Permette il filtro di ricerca attraverso i driver che implementano l'interfaccia 'get'
 * 
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class FiltroRicercaPorteDelegate extends FiltroRicercaBase implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** TipoSoggetto */
	private String tipoSoggetto;
	
	/** NomeSoggetto */
	private String nomeSoggetto;

	/** TipoSoggettoErogatore */
	private String tipoSoggettoErogatore;
	
	/** NomeSoggettoErogatore */
	private String nomeSoggettoErogatore;
		
	/** TipoServizio */
	private String tipoServizio;
	
	/** NomeServizio */
	private String nomeServizio;

	/** Azione */
	private String azione;
	

	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer();
		bf.append(super.toString(false));
		if(this.tipoSoggetto!=null)
			bf.append(" [tipoSoggetto:"+this.tipoSoggetto+"]");
		if(this.nomeSoggetto!=null)
			bf.append(" [nomeSoggetto:"+this.nomeSoggetto+"]");
		if(this.tipoSoggettoErogatore!=null)
			bf.append(" [tipoSoggettoErogatore:"+this.tipoSoggettoErogatore+"]");
		if(this.nomeSoggettoErogatore!=null)
			bf.append(" [nomeSoggettoErogatore:"+this.nomeSoggettoErogatore+"]");
		if(this.tipoServizio!=null)
			bf.append(" [tipoServizio:"+this.tipoServizio+"]");
		if(this.nomeServizio!=null)
			bf.append(" [nomeServizio:"+this.nomeServizio+"]");
		if(this.azione!=null)
			bf.append(" [azione:"+this.azione+"]");
		if(bf.length()=="Filtro:".length())
			bf.append(" nessun filtro presente");
		return bf.toString();
	}
	
	public String getTipoSoggetto() {
		return this.tipoSoggetto;
	}


	public void setTipoSoggetto(String tipoSoggetto) {
		this.tipoSoggetto = tipoSoggetto;
	}


	public String getNomeSoggetto() {
		return this.nomeSoggetto;
	}


	public void setNomeSoggetto(String nomeSoggetto) {
		this.nomeSoggetto = nomeSoggetto;
	}


	public String getTipoSoggettoErogatore() {
		return this.tipoSoggettoErogatore;
	}


	public void setTipoSoggettoErogatore(String tipoSoggettoErogatore) {
		this.tipoSoggettoErogatore = tipoSoggettoErogatore;
	}


	public String getNomeSoggettoErogatore() {
		return this.nomeSoggettoErogatore;
	}


	public void setNomeSoggettoErogatore(String nomeSoggettoErogatore) {
		this.nomeSoggettoErogatore = nomeSoggettoErogatore;
	}


	public String getTipoServizio() {
		return this.tipoServizio;
	}


	public void setTipoServizio(String tipoServizio) {
		this.tipoServizio = tipoServizio;
	}


	public String getNomeServizio() {
		return this.nomeServizio;
	}


	public void setNomeServizio(String nomeServizio) {
		this.nomeServizio = nomeServizio;
	}


	public String getAzione() {
		return this.azione;
	}


	public void setAzione(String azione) {
		this.azione = azione;
	}
}
