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



package org.openspcoop2.core.id;

/**
 * Classe utilizzata per rappresentare un identificatore di un Accordo di Cooperazione nel registro dei Servizi.
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IDAccordoCooperazioneWithSoggetto extends IDAccordoCooperazione implements java.io.Serializable {

	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public IDAccordoCooperazioneWithSoggetto(IDAccordoCooperazione idAccordoCooperazione){
		this.nome = idAccordoCooperazione.nome;
		this.versione = idAccordoCooperazione.versione;
	}
	
	
	/* ********  F I E L D S  P R I V A T I  ******** */

	protected IDSoggetto soggettoReferente;
	
	public IDSoggetto getSoggettoReferente() {
		return this.soggettoReferente;
	}

	public void setSoggettoReferente(IDSoggetto soggettoReferente) {
		this.soggettoReferente = soggettoReferente;
	}
	

	/* ********  C O S T R U T T O R E  ******** */

	@Override 
	public String toString(){
		StringBuffer bf = new StringBuffer();
		bf.append(super.toString());
		if(this.soggettoReferente!=null)
			bf.append("["+this.soggettoReferente.toString()+"]");
		return bf.toString();
	}
	
	@Override 
	public boolean equals(Object idAS){
		if(idAS == null)
			return false;
		if(idAS.getClass().getName().equals(this.getClass().getName()) == false)
			return false;
		IDAccordoCooperazioneWithSoggetto id = (IDAccordoCooperazioneWithSoggetto) idAS;
		return (this.toString().equals(id.toString()));
	}
	
	// Utile per usare l'oggetto in hashtable come chiave
	@Override
	public int hashCode(){
		return this.toString().hashCode();
	}
	
	@Override
	public IDAccordoCooperazioneWithSoggetto clone(){
		IDAccordoCooperazione idAccordo = new IDAccordoCooperazione();
		if(this.nome!=null){
			idAccordo.nome = new String(this.nome);
		}
		if(this.versione!=null){
			idAccordo.versione = new String(this.versione);
		}
		IDAccordoCooperazioneWithSoggetto idAccordoWithSoggetto = new IDAccordoCooperazioneWithSoggetto(idAccordo);
		if(this.soggettoReferente!=null){
			idAccordoWithSoggetto.soggettoReferente = this.soggettoReferente.clone();
		}
		return idAccordoWithSoggetto;
	}
}






