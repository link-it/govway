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
package org.openspcoop2.pdd.core.credenziali.engine;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
import org.openspcoop2.pdd.core.credenziali.Credenziali;
import org.openspcoop2.pdd.core.credenziali.GestoreCredenzialiConfigurationException;
import org.openspcoop2.pdd.core.credenziali.GestoreCredenzialiException;
import org.openspcoop2.pdd.core.credenziali.IGestoreCredenziali;
import org.openspcoop2.protocol.sdk.IProtocolFactory;

/**     
 * GestoreCredenzialiPA
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreCredenzialiPA implements IGestoreCredenziali {

	private GestoreCredenzialiEngine gestore = null;
	public GestoreCredenzialiPA(){
		this.gestore = new GestoreCredenzialiEngine(true);
	}
	
	@Override
	public void init(PdDContext pddContext, IProtocolFactory<?> protocolFactory,
			Object... args) {
	}
	
	@Override
	public Credenziali elaborazioneCredenziali(
			IDSoggetto idSoggetto,
			InfoConnettoreIngresso infoConnettoreIngresso, OpenSPCoop2Message messaggio)
			throws GestoreCredenzialiException,
			GestoreCredenzialiConfigurationException {
		return this.gestore.elaborazioneCredenziali(idSoggetto,infoConnettoreIngresso, messaggio);
	}

	@Override
	public String getIdentitaGestoreCredenziali() {
		return this.gestore.getIdentitaGestoreCredenziali();
	}


}
