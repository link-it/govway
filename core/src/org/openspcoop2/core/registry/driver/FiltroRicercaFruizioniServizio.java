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



package org.openspcoop2.core.registry.driver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Permette il filtro di ricerca attraverso i driver che implementano l'interfaccia 'get'
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12237 $, $Date: 2016-10-04 11:41:45 +0200 (Tue, 04 Oct 2016) $
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
	
	public String getTipoSoggettoFruitore() {
		return this.tipoSoggettoFruitore;
	}

	public void setTipoSoggettoFruitore(String tipoSoggettoFruitore) {
		this.tipoSoggettoFruitore = tipoSoggettoFruitore;
	}

	public String getNomeSoggettoFruitore() {
		return this.nomeSoggettoFruitore;
	}

	public void setNomeSoggettoFruitore(String nomeSoggettoFruitore) {
		this.nomeSoggettoFruitore = nomeSoggettoFruitore;
	}
	
	
	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer();
		bf.append("Filtro Fruizione: ");
		this.addDetails(bf);
		if(bf.length()=="Filtro Fruizione: ".length())
			bf.append(" nessun filtro presente");
		return bf.toString();
	}
	@Override
	public void addDetails(StringBuffer bf){
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
