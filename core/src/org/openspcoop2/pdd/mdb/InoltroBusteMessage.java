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



package org.openspcoop2.pdd.mdb;

import org.openspcoop2.pdd.config.RichiestaDelegata;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateless;
import org.openspcoop2.protocol.sdk.Busta;

/**
 * Classe utilizzata per raccogliere informazioni incluse in un MessaggioJMS. 
 * Il messaggio JMS sara' poi ricevuto, attraverso una coda apposita, 
 * dal mdb definito nella classe {@link InoltroBusteMDB}.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class InoltroBusteMessage implements GenericMessage {

	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Busta da Spedire */
	private Busta busta;   
	/** Identificatore della Porta Delegata */
	private RichiestaDelegata richiestaDelegata;  
	/** Indicazione se siamo in modalita oneway11 */
	private boolean oneWayVersione11;
	/** OpenSPCoop Stateless */
	private OpenSPCoopStateless openspcoopstate = null;
	
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
	public InoltroBusteMessage(){
	}





	/* ********  S E T T E R   ******** */

	/**
	 * Imposta l'identificatore della Porta Delegata di tipo {@link org.openspcoop2.pdd.config.RichiestaDelegata}
	 *
	 * @param idpd Identificatore della Porta Delegata
	 * 
	 */
	public void setRichiestaDelegata(RichiestaDelegata idpd){
		this.richiestaDelegata = idpd;
	}

	/**
	 * Imposta la busta da inoltrare.
	 *
	 * @param busta Busta da inviare.
	 * 
	 */
	public void setBusta(Busta busta){
		this.busta = busta;
	}
	
	/**
	 * @param oneWayVersione11
	 */
	public void setOneWayVersione11(boolean oneWayVersione11) {
		this.oneWayVersione11 = oneWayVersione11;
	}
	
	public void setOpenspcoopstate(OpenSPCoopStateless openspcoopstate) {
		this.openspcoopstate = openspcoopstate;
	}
	
	
	


	/* ********  G E T T E R   ******** */


	/**
	 * Ritorna l'identificatore della Porta Delegata
	 *
	 * @return Identificatore della Porta Delegata
	 * 
	 */
	public RichiestaDelegata getRichiestaDelegata(){
		return this.richiestaDelegata;
	}
	/**
	 * Ritorna la busta da inoltrare.
	 *
	 * @return Busta da inviare.
	 * 
	 */
	public Busta getBusta(){
		return this.busta;
	}


	public OpenSPCoopStateless getOpenspcoopstate() {
		return this.openspcoopstate;
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
}





