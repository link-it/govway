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



package org.openspcoop2.protocol.registry;

import java.util.Vector;

/**
 * Classe utilizzata per raccogliere informazioni sulla validazione di un servizio
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class RisultatoValidazione implements java.io.Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Indicazione se il servizio risultato registrato o meno nel registro dei servizi */
	private boolean servizioRegistrato;
	/** Indicazione se il servizio permette l'accesso con azione o senza */
	private boolean accessoSenzaAzione;
	/** Indicazione se il servizio e' un servizio correlato */
	private boolean isServizioCorrelato;
	/** Tipo dell'eventuale servizio correlato associato ad un normale servizio */
	private String tipoServizioCorrelato;
	/** Nome dell'eventuale servizio correlato associato ad un normale servizio */
	private String servizioCorrelato;
	/** Azione correlata (presente se il servizio trovato non e' correlato, ma l'azione si) */
	private String azioneCorrelata;
	/**  Azioni associate al servizio */
	private Vector<String> azioni;
	/** Tipologia di porta di domino del soggetto fruitore */
	private String implementazionePdDSoggettoFruitore;
	/** Tipologia di porta di domino del soggetto erogatore */
	private String implementazionePdDSoggettoErogatore;




	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Costruttore. 
	 *
	 * 
	 */
	public RisultatoValidazione(){
		this.azioni = new Vector<String>();
	}




	/* ********  S E T T E R   ******** */

	/**
	 * Imposta l'indicazione se il servizio risultato registrato o meno nel registro dei servizi
	 *
	 * @param value indicazione se il servizio risultato registrato o meno nel registro dei servizi
	 * 
	 */
	public void setServizioRegistrato(boolean value){
		this.servizioRegistrato = value;
	}
	/**
	 * Imposta l'indicazione se il servizio permette l'accesso con azione o senza
	 *
	 * @param value indicazione se il servizio permette l'accesso con azione o senza
	 * 
	 */
	public void setAccessoSenzaAzione(boolean value){
		this.accessoSenzaAzione = value;
	}
	/**
	 * Imposta l'indicazione se il servizio e' un servizio correlato
	 *
	 * @param value indicazione se il servizio e' un servizio correlato
	 * 
	 */
	public void setIsServizioCorrelato(boolean value){
		this.isServizioCorrelato = value;
	}
	/**
	 * Imposta il nome dell'eventuale servizio correlato associato ad un normale servizio
	 *
	 * @param value nome dell'eventuale servizio correlato associato ad un normale servizio
	 * 
	 */
	public void setServizioCorrelato(String value){
		this.servizioCorrelato = value;
	}
	/**
	 * Imposta il tipo dell'eventuale servizio correlato associato ad un normale servizio
	 *
	 * @param tipoServizioCorrelato tipo dell'eventuale servizio correlato associato ad un normale servizio
	 * 
	 */
	public void setTipoServizioCorrelato(String tipoServizioCorrelato) {
		this.tipoServizioCorrelato = tipoServizioCorrelato;
	}









	/* ********  G E T T E R   ******** */

	/**
	 * Ritorna l'indicazione se il servizio risultato registrato o meno nel registro dei servizi
	 *
	 * @return indicazione se il servizio risultato registrato o meno nel registro dei servizi
	 * 
	 */
	public boolean getServizioRegistrato(){
		return this.servizioRegistrato;
	}
	/**
	 * Ritorna l'indicazione se il servizio permette l'accesso con azione o senza
	 *
	 * @return indicazione se il servizio permette l'accesso con azione o senza
	 * 
	 */
	public boolean getAccessoSenzaAzione(){
		return this.accessoSenzaAzione;
	}
	/**
	 * Ritorna l'indicazione se il servizio e' un servizio correlato
	 *
	 * @return indicazione se il servizio e' un servizio correlato
	 * 
	 */
	public boolean getIsServizioCorrelato(){
		return this.isServizioCorrelato;
	}
	/**
	 * Ritorna il nome dell'eventuale servizio correlato associato ad un normale servizio
	 *
	 * @return nome dell'eventuale servizio correlato associato ad un normale servizio
	 * 
	 */
	public String getServizioCorrelato(){
		return this.servizioCorrelato;
	}
	/**
	 * Ritorna il tipo dell'eventuale servizio correlato associato ad un normale servizio
	 *
	 * @return tipo dell'eventuale servizio correlato associato ad un normale servizio
	 * 
	 */
	public String getTipoServizioCorrelato() {
		return this.tipoServizioCorrelato;
	}



	/**
	 * Imposta le azioni associate ad un servizio
	 *
	 * @param a azioni associate ad un servizio
	 * 
	 */
	public void setAzioni(String[] a) {
		for(int i=0; i<a.length; i++)
			this.azioni.add(a[i]);
	}
	/**
	 * Aggiunge una azione
	 *
	 * @param a azione
	 * 
	 */
	public void addAzione(String a) {
		this.azioni.add(a);
	}
	/**
	 * Restituisce una azione
	 *
	 * @param index Indice dell'azione desiderata
	 * @return Azione
	 * 
	 */
	public String getAzione(int index) {
		return this.azioni.get(index);
	}
	/**
	 * Numero di azioni associate al servizio
	 *
	 * 
	 */
	public int sizeAzioni() {
		return this.azioni.size();
	}
	/**
	 * Restituisce una azione
	 *
	 * @param index Indice dell'azione desiderata
	 * @return Azione
	 * 
	 */
	public String removeAzione(int index) {
		return this.azioni.remove(index);
	}
	/**
	 * Azioni associate al servizio
	 *
	 * 
	 */
	public String[] getAzioni() {
		String[] azNomeValore = new String[1];
		azNomeValore =  this.azioni.toArray(azNomeValore);
		return azNomeValore;
	}

	public String getAzioneCorrelata() {
		return this.azioneCorrelata;
	}

	public void setAzioneCorrelata(String azioneCorrelata) {
		this.azioneCorrelata = azioneCorrelata;
	}

	public String getImplementazionePdDSoggettoFruitore() {
		return this.implementazionePdDSoggettoFruitore;
	}

	public void setImplementazionePdDSoggettoFruitore(
			String implementazionePdDSoggettoFruitore) {
		this.implementazionePdDSoggettoFruitore = implementazionePdDSoggettoFruitore;
	}

	public String getImplementazionePdDSoggettoErogatore() {
		return this.implementazionePdDSoggettoErogatore;
	}

	public void setImplementazionePdDSoggettoErogatore(
			String implementazionePdDSoggettoErogatore) {
		this.implementazionePdDSoggettoErogatore = implementazionePdDSoggettoErogatore;
	}

}





