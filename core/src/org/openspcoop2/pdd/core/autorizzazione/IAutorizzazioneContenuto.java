/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
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

import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.ICore;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;

/**
 * Interfaccia che definisce un processo di autorizzazione per servizi applicativi che invocano richieste delegate.
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IAutorizzazioneContenuto extends ICore {


    /**
     * Avvia il processo di autorizzazione.
     *
     * @param infoConnettoreIngresso infoConnettoreIngresso della Porta Delegata invocata
     * @param servizioApplicativo Servizio Applicativo che richiede il processo
     * @param idPD IDPortaDelegata
     * @param servizio Servizio invocato
     * @param msg Messaggio Applicativo
     * @return Esito dell'autorizzazione.
     * 
     */
    public EsitoAutorizzazioneIntegrazione process(InfoConnettoreIngresso infoConnettoreIngresso,
    		   String servizioApplicativo,IDPortaDelegata idPD,
			   IDServizio servizio,PortaDelegata pd,OpenSPCoop2Message msg) throws AutorizzazioneException;
    
 
}

