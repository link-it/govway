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



package org.openspcoop2.core.config.driver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.id.IDRuolo;

/**
 * Permette il filtro di ricerca attraverso i driver che implementano l'interfaccia 'get'
 * 
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class FiltroRicercaServiziApplicativi extends FiltroRicercaBase implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** TipoSoggetto */
	private String tipoSoggetto;
	
	/** NomeSoggetto */
	private String nomeSoggetto;
	
	/** Ruolo */
	private IDRuolo idRuolo;
	
	/** Tipo */
	private String tipo;

	/** ProtocolProperty */
	private List<FiltroRicercaProtocolProperty> protocolProperties = new ArrayList<FiltroRicercaProtocolProperty>();

	public List<FiltroRicercaProtocolProperty> getProtocolProperties() {
		return this.protocolProperties;
	}

	public void setProtocolProperties(
			List<FiltroRicercaProtocolProperty> list) {
		this.protocolProperties = list;
	}

	public void addProtocolProperty(FiltroRicercaProtocolProperty filtro){
		this.protocolProperties.add(filtro);
	}
	
	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();
		bf.append(super.toString(false));
		if(this.tipoSoggetto!=null)
			bf.append(" [tipoSoggetto:"+this.tipoSoggetto+"]");
		if(this.nomeSoggetto!=null)
			bf.append(" [nomeSoggetto:"+this.nomeSoggetto+"]");
		if(this.idRuolo!=null)
			bf.append(" [ruolo:"+this.idRuolo+"]");
		if(this.protocolProperties!=null && this.protocolProperties.size()>0){
			bf.append(" [protocol-properties:"+this.protocolProperties.size()+"]");
			for (int i = 0; i < this.protocolProperties.size(); i++) {
				bf.append(" [protocol-properties["+i+"]:");
				this.protocolProperties.get(i).addDetails(bf);
				bf.append("]");
			}
		}
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
	
	public IDRuolo getIdRuolo() {
		return this.idRuolo;
	}

	public void setIdRuolo(IDRuolo idRuolo) {
		this.idRuolo = idRuolo;
	}

	public String getTipo() {
		return this.tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

}
