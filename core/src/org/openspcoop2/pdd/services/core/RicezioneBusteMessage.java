/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
import org.openspcoop2.protocol.sdk.Busta;


/**
 * Classe utilizzata per raccogliere informazioni incluse in un MessaggioJMS. 
 * Il messaggio JMS sara' poi ricevuto, attraverso una coda apposita, 
 * dal servizio definito nella classe {@link RicezioneBuste}.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class RicezioneBusteMessage implements GenericMessage {
   
	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

    /* ********  F I E L D S  P R I V A T I  ******** */
   
    /** Busta  della risposta  */
    private Busta bustaRisposta;
    /** ID per il messaggio di Sblocco*/
    private String idMessaggioSblocco;

	/** PdDContext */
	private PdDContext pddContext;
	


    /* ********  C O S T R U T T O R E  ******** */

    /**
     * Costruttore. 
     *
     * 
     */    
    public RicezioneBusteMessage(){
    }

    

    

    /* ********  S E T T E R   ******** */
    
    /**
     * Imposta la busta della risposta
     *
     * @param busta busta di risposta
     * 
     */
    public void setBustaRisposta(Busta busta){
    	this.bustaRisposta = busta;
    }
	public void setIdMessaggioSblocco(String idMessaggioSblocco) {
		this.idMessaggioSblocco = idMessaggioSblocco;
	}
	public void setPddContext(PdDContext pddContext) {
		this.pddContext = pddContext;
	}





    

    /* ********  G E T T E R   ******** */
    
    /**
     * Ritorna la busta della risposta
     *
     * @return busta di risposta
     * 
     */
    public Busta getBustaRisposta(){
	return this.bustaRisposta;
    }
    
	public String getIdMessaggioSblocco() {
		return this.idMessaggioSblocco;
	}
	@Override
	public PdDContext getPddContext() {
		return this.pddContext;
	}

  
}





