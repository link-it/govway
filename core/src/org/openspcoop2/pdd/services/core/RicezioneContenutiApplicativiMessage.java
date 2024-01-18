/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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



package org.openspcoop2.pdd.services.core;

import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.mdb.GenericMessage;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;



/**
 * Classe utilizzata per raccogliere informazioni incluse in un MessaggioJMS. 
 * Il messaggio JMS sara' poi ricevuto, attraverso una coda apposita, 
 * dal servizio definito nella classe {@link RicezioneContenutiApplicativi}.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class RicezioneContenutiApplicativiMessage implements GenericMessage {
   
	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
    /* ********  F I E L D S  P R I V A T I  ******** */
  
    /** ID associato alla sessione della risposta */
    private String idRisposta;
    /** IDCollaborazione (per header di integrazione-risposta) */
    private String idCollaborazione;
    /** ProfiloCollaborazione (per header di integrazione-risposta) */
    private ProfiloDiCollaborazione profiloCollaborazione;
    private String profiloCollaborazioneValue;
  
	/** PdDContext */
	private PdDContext pddContext;

	

    /* ********  C O S T R U T T O R E  ******** */

    /**
     * Costruttore. 
     *
     * 
     */
    public RicezioneContenutiApplicativiMessage(){
    }
    /**
     * Costruttore. 
     *
     * @param aID ID associato alla sessione della risposta
     * 
     */
    public RicezioneContenutiApplicativiMessage(String aID){
	this.idRisposta = aID;
    }


    



    /* ********  S E T T E R   ******** */
    
    /**
     * Imposta l'ID associato alla sessione della risposta.
     *
     * @param id ID associato alla sessione della risposta.
     * 
     */
    public void setIdBustaRisposta(String id){
    	this.idRisposta = id;
    }
	/**
	 * @param idCollaborazione the idCollaborazione to set
	 */
	public void setIdCollaborazione(String idCollaborazione) {
		this.idCollaborazione = idCollaborazione;
	}
	/**
	 * @param profiloCollaborazione the profiloCollaborazione to set
	 */
	public void setProfiloCollaborazione(ProfiloDiCollaborazione profiloCollaborazione, String profiloCollaborazioneValue) {
		this.profiloCollaborazione = profiloCollaborazione;
		this.profiloCollaborazioneValue = profiloCollaborazioneValue;
	}
	public void setPddContext(PdDContext pddContext) {
		this.pddContext = pddContext;
	}



    /* ********  G E T T E R   ******** */

    /**
     * Ritorna l'ID associato alla sessione della risposta.
     *
     * @return ID associato alla sessione della risposta.
     * 
     */
    public String getIdBustaRisposta(){
    	return this.idRisposta;
    }
	/**
	 * @return the idCollaborazione
	 */
	public String getIdCollaborazione() {
		return this.idCollaborazione;
	}

	/**
	 * @return the profiloCollaborazione
	 */
	public ProfiloDiCollaborazione getProfiloCollaborazione() {
		return this.profiloCollaborazione;
	}
	public String getProfiloCollaborazioneValue() {
		return this.profiloCollaborazioneValue;
	}

	@Override
	public PdDContext getPddContext() {
		return this.pddContext;
	}
    
 
}





