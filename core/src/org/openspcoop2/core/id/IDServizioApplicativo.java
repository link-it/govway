/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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


package org.openspcoop2.core.id;

import java.io.Serializable;

/**
 * Classe utilizzata per rappresentare un identificatore di un Servizio Applicativo nella configurazione
 * 
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IDServizioApplicativo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected IDSoggetto idSoggettoProprietario;

	protected String nome;
	
	
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
		StringBuilder bf = new StringBuilder();
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
	
	
	
	public String toFormatString(){
		StringBuilder sb = new StringBuilder();
		sb.append(this.idSoggettoProprietario.getTipo());
		sb.append("/");
		sb.append(this.idSoggettoProprietario.getNome());
		sb.append("/");
		sb.append(this.nome);
		return sb.toString();
	}
	
	public static IDServizioApplicativo toIDServizioApplicativo(String formatString) throws Exception {
		String [] tmp = formatString.split("/");
		if(tmp.length!=3) {
			throw new Exception("Formato non supportato, attesi 3 valori, trovati "+tmp.length);
		}
		String tipoSoggettoErogatore = tmp[0];
		String nomeSoggettoErogatore = tmp[1];
		String nome = tmp[2];
		IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
		idServizioApplicativo.idSoggettoProprietario = new IDSoggetto(tipoSoggettoErogatore, nomeSoggettoErogatore);
		idServizioApplicativo.nome=nome;
		return idServizioApplicativo;
	}
}
