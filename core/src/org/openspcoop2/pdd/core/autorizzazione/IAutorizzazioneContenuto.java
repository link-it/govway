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



package org.openspcoop2.pdd.core.autorizzazione;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.ICore;

/**
 * Interfaccia che definisce un processo di autorizzazione dei contenuti
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IAutorizzazioneContenuto extends ICore {

    /**
     * Indicazione se il risultato deve essere salvato in cache
     * 
     * @return Indicazione se il risultato deve essere salvato in cache
     */
    public boolean saveAuthorizationResultInCache();
	
    public default void cleanPostAuth(OpenSPCoop2Message message) {
    	// nop
    }
    
}
