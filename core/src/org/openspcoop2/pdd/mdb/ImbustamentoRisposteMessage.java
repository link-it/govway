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

import java.sql.Timestamp;

import org.openspcoop2.pdd.config.RichiestaApplicativa;
import org.openspcoop2.pdd.config.RichiestaDelegata;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.protocol.sdk.Busta;

/**
 * Classe utilizzata per raccogliere informazioni incluse in un MessaggioJMS. 
 * Il messaggio JMS sara' poi ricevuto, attraverso una coda apposita, 
 * dal mdb definito nella classe {@link ImbustamentoRisposteMDB}.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ImbustamentoRisposteMessage implements GenericMessage {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Identificatore creato e associato a questa risposta */
	private String idMessageResponse;
	/** Busta che ha causato l'invocazione. */
	private Busta busta;

	//  RichiesteApplicative di invocazione servizio:
	/** Identificatore Porta Applicativa  */
	private RichiestaApplicativa richiestaApplicativa;

	// RichiesteDelegata: Consegna contenuti applicativi e consegna risposta asincrona simmetrica:
	private RichiestaDelegata richiestaDelegata;

	/** Gestione tempi di attraversamento */
	private Timestamp spedizioneMsgIngresso;
	private Timestamp ricezioneMsgRisposta;
	
	/** Indicazione se siamo in modalita oneway11 */
	private boolean oneWayVersione11;
	
	/** Indicazione se la porta delegata/applicativa richiesta una modalita stateless (oneway o sincrono) 
	 *  Il caso oneway11 possiedera questo booleano con il valore false.
	 * */
	private boolean stateless;
	
	/** Tipologia di porta di domino del soggetto mittente */
	private String implementazionePdDSoggettoMittente;
	/** Tipologia di porta di domino del soggetto destinatario */
	private String implementazionePdDSoggettoDestinatario;
	
	/** PdDContext */
	private PdDContext pddContext;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Costruttore. 
	 *
	 * 
	 */    
	public ImbustamentoRisposteMessage(){
	}





	/* ********  S E T T E R   ******** */

	/**
	 * Imposta l'identificatore associato alla risposta.
	 *
	 * @param id Identificatore associato alla risposta.
	 * 
	 */
	public void setIDMessageResponse(String id){
		this.idMessageResponse = id;
	} 
	/**
	 * Imposta la Busta che ha causato l'invocazione di servizio.
	 *
	 * @param aBusta Busta .
	 * 
	 */
	public void setBusta(Busta aBusta){
		this.busta = aBusta;
	}

//	RichiesteApplicative di invocazione servizio:
	/**
	 * Imposta l'identificatore della Porta Applicativa di tipo {@link org.openspcoop2.pdd.config.RichiestaApplicativa}
	 *
	 * @param idpa Identificatore della Porta Applicativa
	 * 
	 */
	public void setRichiestaApplicativa(RichiestaApplicativa idpa){
		this.richiestaApplicativa = idpa;
	}


//	RichiesteDelegata: Consegna contenuti applicativi:
	/**
	 * Imposta l'identificatore della Porta Delegata di tipo {@link org.openspcoop2.pdd.config.RichiestaDelegata}
	 *
	 * @param idpd Identificatore della Porta Delegata
	 * 
	 */
	public void setRichiestaDelegata(RichiestaDelegata idpd){
		this.richiestaDelegata = idpd;
	}

	public void setRicezioneMsgRisposta(Timestamp ricezioneMsgRisposta) {
		this.ricezioneMsgRisposta = ricezioneMsgRisposta;
	}

	public void setSpedizioneMsgIngresso(Timestamp spedizioneMsgIngresso) {
		this.spedizioneMsgIngresso = spedizioneMsgIngresso;
	}

	/**
	 * @param oneWayVersione11
	 */
	public void setOneWayVersione11(boolean oneWayVersione11) {
		this.oneWayVersione11 = oneWayVersione11;
	}
	
	public void setStateless(boolean stateless) {
		this.stateless = stateless;
	}
	



	/* ********  G E T T E R   ******** */

	/**
	 * Ritorna l'identificatore associato alla risposta.
	 *
	 * @return Identificatore associato alla risposta.
	 * 
	 */
	public String getIDMessageResponse(){
		return this.idMessageResponse;
	} 
	/**
	 * Ritorna la Busta che ha causato l'invocazione di servizio.
	 *
	 * @return Busta .
	 * 
	 */
	public Busta getBusta(){
		return this.busta;
	}

	//  RichiesteApplicative di invocazione servizio:
	/**
	 * Ritorna l'identificatore della Porta Applicativa di tipo {@link org.openspcoop2.pdd.config.RichiestaApplicativa}
	 *
	 * @return Identificatore della Porta Applicativa
	 * 
	 */
	public RichiestaApplicativa getRichiestaApplicativa(){
		return this.richiestaApplicativa;
	}

	//  RichiesteDelegata: Consegna contenuti applicativi:
	/**
	 * Ritorna l'identificatore della Porta Delegata
	 *
	 * @return Identificatore della Porta Delegata
	 * 
	 */
	public RichiestaDelegata getRichiestaDelegata(){
		return this.richiestaDelegata;
	}


	public Timestamp getRicezioneMsgRisposta() {
		return this.ricezioneMsgRisposta;
	}

	public Timestamp getSpedizioneMsgIngresso() {
		return this.spedizioneMsgIngresso;
	}
	
	/**
	 * @return indicazione se stiamo gestendo oneway in modalita stateless 1.4
	 */
	public boolean isOneWayVersione11() {
		return this.oneWayVersione11;
	}

	public boolean isStateless() {
		return this.stateless;
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
}






