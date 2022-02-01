/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.credenziali;

import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;

/**
 * Esempio che definisce un gestore delle credenziali per il servizio IntegrationManager
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreCredenzialiIMTest extends AbstractCore implements IGestoreCredenzialiIM {

	private GestoreCredenzialiTest test = null;
	
	@Override
	public Credenziali elaborazioneCredenziali(InfoConnettoreIngresso infoConnettoreIngresso) throws GestoreCredenzialiException,GestoreCredenzialiConfigurationException{
		
		this.test = new GestoreCredenzialiTest();
		return this.test.elaborazioneCredenziali(null,infoConnettoreIngresso, null);
		
	}
	
	@Override
	public String getIdentitaGestoreCredenziali(){
		return this.test.getIdentitaGestoreCredenziali();
	}
	
}
