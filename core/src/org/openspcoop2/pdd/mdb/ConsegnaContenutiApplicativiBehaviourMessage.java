/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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



package org.openspcoop2.pdd.mdb;

import java.io.Serializable;

import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer;
import org.openspcoop2.pdd.core.behaviour.BehaviourForwardToConfiguration;

/**
 * Classe utilizzata per raccogliere informazioni incluse in un MessaggioJMS. 
 * Il messaggio JMS sara' poi ricevuto, attraverso una coda apposita, 
 * dal mdb definito nella classe {@link ConsegnaContenutiApplicativiMDB}.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ConsegnaContenutiApplicativiBehaviourMessage implements Serializable {


	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/* ********  F I E L D S  P R I V A T I  ******** */

	private String idMessaggioPreBehaviour = null;
	private BehaviourForwardToConfiguration behaviourForwardToConfiguration;
	private IdTransazioneApplicativoServer idTransazioneApplicativoServer;
	private GestioneErrore gestioneErrore;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Costruttore. 
	 *
	 * 
	 */
	public ConsegnaContenutiApplicativiBehaviourMessage(){
	}


	public String getIdMessaggioPreBehaviour() {
		return this.idMessaggioPreBehaviour;
	}


	public void setIdMessaggioPreBehaviour(String idMessaggioPreBehaviour) {
		this.idMessaggioPreBehaviour = idMessaggioPreBehaviour;
	}


	public BehaviourForwardToConfiguration getBehaviourForwardToConfiguration() {
		return this.behaviourForwardToConfiguration;
	}


	public void setBehaviourForwardToConfiguration(BehaviourForwardToConfiguration behaviourForwardToConfiguration) {
		this.behaviourForwardToConfiguration = behaviourForwardToConfiguration;
	}

	public IdTransazioneApplicativoServer getIdTransazioneApplicativoServer() {
		return this.idTransazioneApplicativoServer;
	}

	public void setIdTransazioneApplicativoServer(IdTransazioneApplicativoServer idTransazioneApplicativoServer) {
		this.idTransazioneApplicativoServer = idTransazioneApplicativoServer;
	}
	
	public GestioneErrore getGestioneErrore() {
		return this.gestioneErrore;
	}
	
	public void setGestioneErrore(GestioneErrore gestioneErrore) {
		this.gestioneErrore = gestioneErrore;
	}
	
}





