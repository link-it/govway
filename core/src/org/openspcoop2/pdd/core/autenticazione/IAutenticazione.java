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



package org.openspcoop2.pdd.core.autenticazione;

import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.pdd.core.ICore;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.state.IState;

/**
 * Interfaccia che definisce un processo di autenticazione per richieste delegate.
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IAutenticazione extends ICore {

	
    /**
     * Avvia il processo di autenticazione.
     *
     * @return true in caso di autenticazione con successo, false altrimenti.
     * 
     */
    public boolean process(InfoConnettoreIngresso infoConnettoreIngresso,IDPortaDelegata idPD,IState state);
    
    /**
     * Avvia il processo di autenticazione.
     *
     * @return true in caso di autenticazione con successo, false altrimenti.
     * 
     */
    public boolean process(InfoConnettoreIngresso infoConnettoreIngresso,IState state);
    
    /**
     * Ritorna il servizio applicativo autenticato. 
     *
     * @return servizio applicativo.
     * 
     */
    public IDServizioApplicativo getServizioApplicativo();

    /**
     * In caso di avvenuto errore, questo metodo ritorna il motivo dell'errore.
     *
     * @return motivo dell'errore (se avvenuto).
     * 
     */
    public ErroreIntegrazione getErrore();
    
    /**
     * In caso di avvenuto errore, questo metodo ritorna l'eccezione
     *
     * @return motivo dell'errore l'eccezione (se avvenuto).
     * 
     */
    public Exception getException();
}

