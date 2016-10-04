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
import it.gov.spcoop.sica.manifest.AccordoServizio;

/**
 * Package accordo di servizio parte specifica
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordoServizioParteSpecifica implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5643644790808146349L;

	/** Contiene la firma apposta dal Servizio di Registro quando l'accordo viene scaricato dal Registro stesso  */
	private Firma firma;

	/** Contiene i metadati associati all'Accordo di Servizio. */
	private AccordoServizio manifesto;

	/** Specifica Porti di Accesso: erogatore */
	private Documento portiAccessoErogatore;
	/** Specifica Porti di Accesso: fruitore */
	private Documento portiAccessoFruitore;

	/** Specifica Livelli Servizio */
	private ArrayList<Documento> specificheLivelliServizio = new ArrayList<Documento>();
	
	/** Specifica Sicurezza */
	private ArrayList<Documento> specificheSicurezza = new ArrayList<Documento>();

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

	public AccordoServizio getManifesto() {
		return this.manifesto;
	}

	public void setManifesto(AccordoServizio manifesto) {
		this.manifesto = manifesto;
	}

	public Documento getPortiAccessoErogatore() {
		return this.portiAccessoErogatore;
	}

	public void setPortiAccessoErogatore(Documento portiAccessoErogatore) {
		this.portiAccessoErogatore = portiAccessoErogatore;
	}

	public Documento getPortiAccessoFruitore() {
		return this.portiAccessoFruitore;
	}

	public void setPortiAccessoFruitore(Documento portiAccessoFruitore) {
		this.portiAccessoFruitore = portiAccessoFruitore;
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
	
	public void addSpecificaLivelloServizio(Documento allegato) {
		this.specificheLivelliServizio.add(allegato);
	}

	public Documento getSpecificaLivelloServizio(int index) {
		return this.specificheLivelliServizio.get( index );
	}

	public Documento removeSpecificaLivelloServizio(int index) {
		return this.specificheLivelliServizio.remove( index );
	}

	public Documento[] getSpecificheLivelliServizio() {
		Documento[] array = new Documento[1];
		if(this.specificheLivelliServizio.size()>0){
			return this.specificheLivelliServizio.toArray(array);
		}else{
			return null;
		}
	}

	public void setSpecificheLivelliServizio(Documento[] array) {
		if(array!=null){
			this.specificheLivelliServizio.clear();
			for(int i=0; i<array.length; i++){
				this.specificheLivelliServizio.add(array[i]);
			}
		}
	}

	public int sizeSpecificheLivelliServizio() {
		return this.specificheLivelliServizio.size();
	}
	
	
	public void addSpecificaSicurezza(Documento allegato) {
		this.specificheSicurezza.add(allegato);
	}

	public Documento getSpecificaSicurezza(int index) {
		return this.specificheSicurezza.get( index );
	}

	public Documento removeSpecificaSicurezza(int index) {
		return this.specificheSicurezza.remove( index );
	}

	public Documento[] getSpecificheSicurezza() {
		Documento[] array = new Documento[1];
		if(this.specificheSicurezza.size()>0){
			return this.specificheSicurezza.toArray(array);
		}else{
			return null;
		}
	}

	public void setSpecificheSicurezza(Documento[] array) {
		if(array!=null){
			this.specificheSicurezza.clear();
			for(int i=0; i<array.length; i++){
				this.specificheSicurezza.add(array[i]);
			}
		}
	}

	public int sizeSpecificheSicurezza() {
		return this.specificheSicurezza.size();
	}
}
