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
import it.gov.spcoop.sica.manifest.ServizioComposto;

/**
 * Package accordo di servizio composto
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordoServizioComposto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5643644790808146349L;

	/** Contiene la firma apposta dal Servizio di Registro quando l'accordo viene scaricato dal Registro stesso  */
	private Firma firma;

	/** Contiene i metadati associati all'Accordo di Servizio. */
	private ServizioComposto manifesto;

	/** Specifica Interfaccia: interfaccia concettuale */
	private Documento interfacciaConcettuale;
	/** Specifica Interfaccia: interfaccia logico erogatore */
	private Documento interfacciaLogicaLatoErogatore;
	/** Specifica Interfaccia: interfaccia logico fruitore */
	private Documento interfacciaLogicaLatoFruitore;

	/** Specifica Conversazioni: conversazione concettuale */
	private Documento conversazioneConcettuale;
	/** Specifica Conversazioni: conversazione logico erogatore */
	private Documento conversazioneLogicaErogatore;
	/** Specifica Conversazioni: conversazione logico fruitore */
	private Documento conversazioneLogicaFruitore;

	/** Specifica Coordinamento */
	private ArrayList<Documento> specificheCoordinamento = new ArrayList<Documento>();
	
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

	public ServizioComposto getManifesto() {
		return this.manifesto;
	}

	public void setManifesto(ServizioComposto manifesto) {
		this.manifesto = manifesto;
	}

	public Documento getInterfacciaConcettuale() {
		return this.interfacciaConcettuale;
	}

	public void setInterfacciaConcettuale(Documento interfacciaConcettuale) {
		this.interfacciaConcettuale = interfacciaConcettuale;
	}

	public Documento getInterfacciaLogicaLatoErogatore() {
		return this.interfacciaLogicaLatoErogatore;
	}

	public void setInterfacciaLogicaLatoErogatore(
			Documento interfacciaLogicaLatoErogatore) {
		this.interfacciaLogicaLatoErogatore = interfacciaLogicaLatoErogatore;
	}

	public Documento getInterfacciaLogicaLatoFruitore() {
		return this.interfacciaLogicaLatoFruitore;
	}

	public void setInterfacciaLogicaLatoFruitore(
			Documento interfacciaLogicaLatoFruitore) {
		this.interfacciaLogicaLatoFruitore = interfacciaLogicaLatoFruitore;
	}

	public Documento getConversazioneConcettuale() {
		return this.conversazioneConcettuale;
	}

	public void setConversazioneConcettuale(Documento conversazioneConcettuale) {
		this.conversazioneConcettuale = conversazioneConcettuale;
	}

	public Documento getConversazioneLogicaErogatore() {
		return this.conversazioneLogicaErogatore;
	}

	public void setConversazioneLogicaErogatore(
			Documento conversazioneLogicaErogatore) {
		this.conversazioneLogicaErogatore = conversazioneLogicaErogatore;
	}

	public Documento getConversazioneLogicaFruitore() {
		return this.conversazioneLogicaFruitore;
	}

	public void setConversazioneLogicaFruitore(Documento conversazioneLogicaFruitore) {
		this.conversazioneLogicaFruitore = conversazioneLogicaFruitore;
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
	
	public void addSpecificaCoordinamento(Documento s) {
		this.specificheCoordinamento.add(s);
	}

	public Documento getSpecificaCoordinamento(int index) {
		return this.specificheCoordinamento.get( index );
	}

	public Documento removeSpecificaCoordinamento(int index) {
		return this.specificheCoordinamento.remove( index );
	}

	public Documento[] getSpecificheCoordinamento() {
		Documento[] array = new Documento[1];
		if(this.specificheCoordinamento.size()>0){
			return this.specificheCoordinamento.toArray(array);
		}else{
			return null;
		}
	}

	public void setSpecificheCoordinamento(Documento[] array) {
		if(array!=null){
			this.specificheCoordinamento.clear();
			for(int i=0; i<array.length; i++){
				this.specificheCoordinamento.add(array[i]);
			}
		}
	}

	public int sizeSpecificheCoordinamento() {
		return this.specificheCoordinamento.size();
	}
}
