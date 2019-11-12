/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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



package org.openspcoop2.pdd.core.autorizzazione.pa;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.autorizzazione.AutorizzazioneException;

/**
 * AbstractAutorizzazioneBase
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractAutorizzazioneBase extends AbstractCore implements IAutorizzazionePortaApplicativa {

	@Override
	public boolean saveAuthorizationResultInCache() {
		return true;
	}

	@Override
	public String getSuffixKeyAuthorizationResultInCache(DatiInvocazionePortaApplicativa datiInvocazione) throws AutorizzazioneException {
		return null;
	}
	
	protected String getErrorString(IDSoggetto idSoggetto, IDServizio idServizio){
		String prefix = "Il mittente";
		if(idSoggetto!=null){
			prefix = "Il soggetto "+idSoggetto.getTipo()+"/"+idSoggetto.getNome();
		}
		return prefix+" non è autorizzato ad invocare il servizio "+idServizio.getTipo()+"/"+idServizio.getNome()+" (versione:"+idServizio.getVersione()+") erogato da "+idServizio.getSoggettoErogatore().getTipo()+"/"+idServizio.getSoggettoErogatore().getNome();
		
	}
}

