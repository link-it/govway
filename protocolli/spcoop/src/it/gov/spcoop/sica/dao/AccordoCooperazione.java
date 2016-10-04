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


package it.gov.spcoop.sica.dao;

import java.io.Serializable;
import java.util.ArrayList;

import it.gov.spcoop.sica.firma.Firma;

/**
 * Package accordo di cooperazione
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordoCooperazione implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5643644790808146349L;

	/** Contiene la firma apposta dal Servizio di Registro quando l'accordo viene scaricato dal Registro stesso  */
	private Firma firma;

	/** Contiene i metadati associati all'Accordo di Cooperazione. */
	private it.gov.spcoop.sica.manifest.AccordoCooperazione manifesto;

	/** Allegati */
	private ArrayList<Documento> allegati = new ArrayList<Documento>();

	/** Specifica semiformale */
	private ArrayList<Documento> specificheSemiformali = new ArrayList<Documento>();


	public Firma getFirma() {
		return this.firma;
	}

	public void setFirma(Firma firma) {
		this.firma = firma;
	}

	public it.gov.spcoop.sica.manifest.AccordoCooperazione getManifesto() {
		return this.manifesto;
	}

	public void setManifesto(it.gov.spcoop.sica.manifest.AccordoCooperazione manifesto) {
		this.manifesto = manifesto;
	}

	public void addAllegato(Documento allegato) {
		this.allegati.add(allegato);
	}

	public Documento getAllegato(int index) {
		return this.allegati.get( index );
	}

	public Documento removeAllegato(int index) {
		return this.allegati.remove( index );
	}

	public Documento[] getAllegati() {
		Documento[] array = new Documento[1];
		if(this.allegati.size()>0){
			return this.allegati.toArray(array);
		}else{
			return null;
		}
	}

	public void setAllegati(Documento[] array) {
		if(array!=null){
			this.allegati.clear();
			for(int i=0; i<array.length; i++){
				this.allegati.add(array[i]);
			}
		}
	}

	public int sizeAllegati() {
		return this.allegati.size();
	}
	
	public void addSpecificaSemiformale(Documento specificaSemiformale) {
		this.specificheSemiformali.add(specificaSemiformale);
	}

	public Documento getSpecificaSemiformale(int index) {
		return this.specificheSemiformali.get( index );
	}

	public Documento removeSpecificaSemiformale(int index) {
		return this.specificheSemiformali.remove( index );
	}

	public Documento[] getSpecificheSemiformali() {
		Documento[] array = new Documento[1];
		if(this.specificheSemiformali.size()>0){
			return this.specificheSemiformali.toArray(array);
		}else{
			return null;
		}
	}

	public void setSpecificheSemiformali(Documento[] array) {
		if(array!=null){
			this.specificheSemiformali.clear();
			for(int i=0; i<array.length; i++){
				this.specificheSemiformali.add(array[i]);
			}
		}
	}

	public int sizeSpecificheSemiformali() {
		return this.specificheSemiformali.size();
	}
}
