/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
import java.util.ArrayList;
import java.util.List;

/**
 * Permette il filtro di ricerca attraverso i driver che implementano l'interfaccia 'get'
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class FiltroRicercaFruizioniServizio extends FiltroRicercaServizi implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** tipo Soggetto Fruitore */
	private String tipoSoggettoFruitore;
	/** nome Soggetto Fruitore */
	private String nomeSoggettoFruitore;

	/** ProtocolProperty */
	private List<FiltroRicercaProtocolProperty> protocolPropertiesFruizione = new ArrayList<FiltroRicercaProtocolProperty>();

	public List<FiltroRicercaProtocolProperty> getProtocolPropertiesFruizione() {
		return this.protocolPropertiesFruizione;
	}

	public void setProtocolPropertiesFruizione(
			List<FiltroRicercaProtocolProperty> list) {
		this.protocolPropertiesFruizione = list;
	}

	public void addProtocolPropertyFruizione(FiltroRicercaProtocolProperty filtro){
		this.protocolPropertiesFruizione.add(filtro);
	}
	
	@Override
	public String getTipoSoggettoFruitore() {
		return this.tipoSoggettoFruitore;
	}

	@Override
	public void setTipoSoggettoFruitore(String tipoSoggettoFruitore) {
		this.tipoSoggettoFruitore = tipoSoggettoFruitore;
	}

	@Override
	public String getNomeSoggettoFruitore() {
		return this.nomeSoggettoFruitore;
	}

	@Override
	public void setNomeSoggettoFruitore(String nomeSoggettoFruitore) {
		this.nomeSoggettoFruitore = nomeSoggettoFruitore;
	}
	
	
	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();
		bf.append("Filtro Fruizione: ");
		this.addDetails(bf);
		if(bf.length()=="Filtro Fruizione: ".length())
			bf.append(" nessun filtro presente");
		return bf.toString();
	}
	@Override
	public void addDetails(StringBuilder bf){
		if(this.tipoSoggettoFruitore!=null)
			bf.append(" [tipo-soggetto-fruitore:"+this.tipoSoggettoFruitore+"]");
		if(this.nomeSoggettoFruitore!=null)
			bf.append(" [nome-soggetto-fruitore:"+this.nomeSoggettoFruitore+"]");
		if(this.protocolPropertiesFruizione!=null && this.protocolPropertiesFruizione.size()>0){
			bf.append(" [protocol-properties-fruizione:"+this.protocolPropertiesFruizione.size()+"]");
			for (int i = 0; i < this.protocolPropertiesFruizione.size(); i++) {
				bf.append(" [protocol-properties-fruizione["+i+"]:"+this.protocolPropertiesFruizione.get(i).toString()+"]");
			}
		}
		super.addDetails(bf);
	}

}
