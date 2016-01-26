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



package org.openspcoop2.pdd.mdb;

import org.openspcoop2.pdd.config.RichiestaDelegata;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.protocol.sdk.Servizio;

/**
 * Classe utilizzata per raccogliere informazioni incluse in un MessaggioJMS. 
 * Il messaggio JMS sara' poi ricevuto, attraverso una coda apposita, 
 * dal mdb definito nella classe {@link ImbustamentoMDB}.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ImbustamentoMessage implements GenericMessage {

	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Raccoglie i dati della richiesta */
	private RichiestaDelegata richiestaDelegata;
	/** RiferimentoMessaggio utilizzato per eventuali risposte asincrone o invocazione di servizi correlati */
	private String riferimentoServizioCorrelato;
	/** IDCollaborazione utilizzato per eventuali collaborazioni */
	private String idCollaborazione;
	/** InfoServizio */
	private Servizio infoServizio;
	/** Indicazione se siamo in modalita oneway11 */
	private boolean oneWayVersione11;
	
	/** Tipologia di porta di domino del soggetto mittente */
	private String implementazionePdDSoggettoMittente;
	/** Tipologia di porta di domino del soggetto destinatario */
	private String implementazionePdDSoggettoDestinatario;
	
	/** Indirizzo soggetto mittente */
	private String indirizzoSoggettoMittente;
	/** Indirizzo soggetto destinatario */
	private String indirizzoSoggettoDestinatario;

	/** PdDContext */
	private PdDContext pddContext;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Costruttore. 
	 *
	 * 
	 */
	public ImbustamentoMessage(){
	}





	/* ********  S E T T E R   ******** */

	/**
	 * Imposta un oggetto che raccoglie i dati della richiesta
	 *
	 * @param idPD RichiestaDelegata.
	 * 
	 */
	public void setRichiestaDelegata(RichiestaDelegata idPD){
		this.richiestaDelegata = idPD;
	}
	/**
	 * RiferimentoMessaggio utilizzato per richieste-stato asincrone asimmetriche 
	 * o risposte asincrone simmetriche o invocazione di servizi correlati
	 * 
	 * @param rifMsg RiferimentoMessaggio
	 */
	public void setRiferimentoServizioCorrelato(String rifMsg) {
		this.riferimentoServizioCorrelato = rifMsg;
	}
	
	/**
	 * @param idCollaborazione the idCollaborazione to set
	 */
	public void setIdCollaborazione(String idCollaborazione) {
		this.idCollaborazione = idCollaborazione;
	}

	/**
	 * @param oneWayVersione11
	 */
	public void setOneWayVersione11(boolean oneWayVersione11) {
		this.oneWayVersione11 = oneWayVersione11;
	}

	
	


	/* ********  G E T T E R   ******** */

	/**
	 * Ritorna un oggetto che raccoglie i dati della richiesta
	 *
	 * @return RichiestaDelegata.
	 * 
	 */
	public RichiestaDelegata getRichiestaDelegata(){
		return this.richiestaDelegata;
	}
	/**
	 * Ritorna il RiferimentoMessaggio utilizzato per richieste-stato asincrone asimmetriche 
	 * o risposte asincrone simmetriche o invocazione di servizi correlati
	 * 
	 * @return riferimentoServizioCorrelato
	 */
	public String getRiferimentoServizioCorrelato() {
		return this.riferimentoServizioCorrelato;
	}

	/**
	 * @return the idCollaborazione
	 */
	public String getIdCollaborazione() {
		return this.idCollaborazione;
	}

	public Servizio getInfoServizio() {
		return this.infoServizio;
	}

	public void setInfoServizio(Servizio infoServizio) {
		this.infoServizio = infoServizio;
	}

	/**
	 * @return indicazione se stiamo gestendo oneway in modalita stateless 1.4
	 */
	public boolean isOneWayVersione11() {
		return this.oneWayVersione11;
	}

	
	
	public String getImplementazionePdDSoggettoMittente() {
		return this.implementazionePdDSoggettoMittente;
	}

	public void setImplementazionePdDSoggettoMittente(
			String implementazionePdDSoggettoMittente) {
		this.implementazionePdDSoggettoMittente = implementazionePdDSoggettoMittente;
	}

	public String getImplementazionePdDSoggettoDestinatario() {
		return this.implementazionePdDSoggettoDestinatario;
	}

	public void setImplementazionePdDSoggettoDestinatario(
			String implementazionePdDSoggettoDestinatario) {
		this.implementazionePdDSoggettoDestinatario = implementazionePdDSoggettoDestinatario;
	}
	
	public PdDContext getPddContext() {
		return this.pddContext;
	}

	public void setPddContext(PdDContext pddContext) {
		this.pddContext = pddContext;
	}
	
	
	public String getIndirizzoSoggettoMittente() {
		return this.indirizzoSoggettoMittente;
	}

	public void setIndirizzoSoggettoMittente(String indirizzoSoggettoMittente) {
		this.indirizzoSoggettoMittente = indirizzoSoggettoMittente;
	}
	
	public String getIndirizzoSoggettoDestinatario() {
		return this.indirizzoSoggettoDestinatario;
	}
	
	public void setIndirizzoSoggettoDestinatario(
			String indirizzoSoggettoDestinatario) {
		this.indirizzoSoggettoDestinatario = indirizzoSoggettoDestinatario;
	}
}





