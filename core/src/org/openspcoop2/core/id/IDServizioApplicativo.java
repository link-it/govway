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


package org.openspcoop2.core.id;


/**
 * Classe utilizzata per rappresentare un identificatore di un Servizio Applicativo nella configurazione
 * 
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IDServizioApplicativo {

	private IDSoggetto idSoggettoProprietario;

	private String nome;
	
	
	public IDSoggetto getIdSoggettoProprietario() {
		return this.idSoggettoProprietario;
	}
	public void setIdSoggettoProprietario(IDSoggetto idSoggettoProprietario) {
		this.idSoggettoProprietario = idSoggettoProprietario;
	}
	public String getNome() {
		return this.nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer();
		if(this.nome!=null)
			bf.append("Nome:"+this.nome);
		else
			bf.append("Nome:NonDefinito");
		bf.append(" ");
		if(this.idSoggettoProprietario!=null)
			bf.append("Soggetto:"+this.idSoggettoProprietario.toString());
		else
			bf.append("Soggetto:NonDefinito");
		return bf.toString();
	}
	
	@Override 
	public boolean equals(Object object){
		if(object == null)
			return false;
		if(object.getClass().getName().equals(this.getClass().getName()) == false)
			return false;
		IDServizioApplicativo id = (IDServizioApplicativo) object;
		
		if(this.nome==null){
			if(id.nome!=null)
				return false;
		}else{
			if(this.nome.equals(id.nome)==false)
				return false;
		}

		if(this.idSoggettoProprietario==null){
			if(id.idSoggettoProprietario!=null)
				return false;
		}else{
			if(this.idSoggettoProprietario.equals(id.idSoggettoProprietario)==false)
				return false;
		}
		
		return true;
	}
	
	// Utile per usare l'oggetto in hashtable come chiave
	@Override
	public int hashCode(){
		return this.toString().hashCode();
	}
	
	@Override
	public IDServizioApplicativo clone(){
		IDServizioApplicativo idSA = new IDServizioApplicativo();
		if(this.nome!=null){
			idSA.nome = new String(this.nome);
		}
		if(this.idSoggettoProprietario!=null){
			idSA.idSoggettoProprietario = this.idSoggettoProprietario.clone();
		}
		return idSA;
	}
}
