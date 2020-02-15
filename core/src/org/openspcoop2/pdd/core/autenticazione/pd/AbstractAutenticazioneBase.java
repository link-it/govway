/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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



package org.openspcoop2.pdd.core.autenticazione.pd;

import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.autenticazione.AutenticazioneException;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazione;

/**
 * AbstractAutenticazioneBase
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractAutenticazioneBase extends AbstractCore implements IAutenticazionePortaDelegata {

	protected ParametriAutenticazione parametri;
	@Override
	public void initParametri(ParametriAutenticazione parametri) throws AutenticazioneException {
		this.parametri = parametri;
	}
	
	@Override
	public boolean saveAuthenticationResultInCache() {
		return true;
	}

	@Override
	public String getSuffixKeyAuthenticationResultInCache(DatiInvocazionePortaDelegata datiInvocazione) throws AutenticazioneException {
		return null;
	}
}

