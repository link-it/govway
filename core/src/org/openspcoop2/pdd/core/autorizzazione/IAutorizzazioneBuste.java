/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved. 
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



package org.openspcoop2.pdd.core.autorizzazione;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.core.ICore;
import org.openspcoop2.pdd.core.autenticazione.Credenziali;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
import org.openspcoop2.protocol.sdk.state.IState;

/**
 * Interfaccia che definisce un processo di autorizzazione per servizi applicativi che invocano richieste delegate.
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IAutorizzazioneBuste extends ICore {


    /**
     * Avvia il processo di autorizzazione.
     *
     * @param credenzialiPdDMittente Credenziali della Porta di Dominio Mittente
     * @param identitaServizioApplicativoFruitore identita del Servizio Applicativo che richiede il processo
     * @param subjectServizioApplicativoFruitoreFromMessageSecurityHeader Subject del Servizio Applicativo fruitore portato nell'header di MessageSecurity
     * @param soggettoFruitore Soggetto con cui viene mappato il servizio applicativo
     * @param servizio Servizio invocato
     * @return Esito dell'autorizzazione.
     * 
     */
    public EsitoAutorizzazioneCooperazione process(InfoConnettoreIngresso infoConnettoreIngresso,Credenziali credenzialiPdDMittente,String identitaServizioApplicativoFruitore,String subjectServizioApplicativoFruitoreFromMessageSecurityHeader,
    		IDSoggetto soggettoFruitore,IDServizio servizio,IState state);
    
}
