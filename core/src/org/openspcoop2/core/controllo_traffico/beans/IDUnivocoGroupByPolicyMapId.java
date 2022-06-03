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
package org.openspcoop2.core.controllo_traffico.beans;

import java.io.Serializable;

/**     
 *  IDUnivocoGroupByPolicyMapId
 *
 * @author Francesco Scarlato (scarlato@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IDUnivocoGroupByPolicyMapId extends IDUnivocoGroupByPolicy implements Serializable  {

	private static final long serialVersionUID = 1L;
	
	private String uniqueMapId = QUALSIASI;
	
	public IDUnivocoGroupByPolicyMapId() {
	}
	
	public IDUnivocoGroupByPolicyMapId(IDUnivocoGroupByPolicy idSuper, String uniqueMapId) {
		this.setRuoloPorta(idSuper.getRuoloPorta());
		this.setProtocollo(idSuper.getProtocollo());
		this.setFruitore(idSuper.getFruitore());
		this.setServizioApplicativoFruitore(idSuper.getServizioApplicativoFruitore());
		this.setErogatore(idSuper.getErogatore());
		this.setServizioApplicativoErogatore(idSuper.getServizioApplicativoErogatore());
		this.setServizio(idSuper.getServizio());
		this.setAzione(idSuper.getAzione());
		this.setTipoKey(idSuper.getTipoKey());
		this.setNomeKey(idSuper.getNomeKey());
		this.setValoreKey(idSuper.getValoreKey());
		this.setIdentificativoAutenticato(idSuper.getIdentificativoAutenticato());
		this.setTokenSubject(idSuper.getTokenSubject());
		this.setTokenIssuer(idSuper.getTokenIssuer());
		this.setTokenUsername(idSuper.getTokenUsername());
		this.setTokenClientId(idSuper.getTokenClientId());
		this.setTokenEMail(idSuper.getTokenEMail());
		
		// aggiunta
		this.setUniqueMapId(uniqueMapId);
	}
	
	@Override
	public boolean match(IDUnivocoGroupByPolicy filtro){
		if (filtro instanceof IDUnivocoGroupByPolicyMapId) {
			return this.uniqueMapId.equals(((IDUnivocoGroupByPolicyMapId) filtro).uniqueMapId) && super.match(filtro);
		} else {
			return super.match(filtro);
		}
	}
	
	
	@Override
	public boolean equals(Object param){
		if(param==null || !(param instanceof IDUnivocoGroupByPolicyMapId))
			return false;
		return this.match((IDUnivocoGroupByPolicyMapId) param);
	}
	
	
	// Utile per usare l'oggetto in hashtable come chiave
	@Override
	public int hashCode(){
		return this.toString().hashCode();
	}
	

	@Override
	public String toString(){
		return this.toString(false);
	}
	@Override
	public String toString(boolean filterGroupByNotSet){
		
		StringBuilder bf = new StringBuilder(super.toString(filterGroupByNotSet));

		if(!QUALSIASI.equals(this.uniqueMapId) || !filterGroupByNotSet){
			if(filterGroupByNotSet){
				if(bf.length()>0){
					bf.append("\n");
				}
				bf.append("\t");
			}
			else{
				bf.append(" ");
			}
			bf.append("UniqueMapId:");
			if(filterGroupByNotSet){
				bf.append(" ");
			}
			bf.append(this.uniqueMapId);
		}
		
		return bf.toString();
	}
	
	public String getUniqueMapId() {
		return this.uniqueMapId;
	}
	
	public void setUniqueMapId(String value) {
		this.uniqueMapId = value;
	}
}
