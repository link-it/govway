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



package org.openspcoop2.pdd.core.token.attribute_authority.pa;

import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.token.GestoreToken;
import org.openspcoop2.pdd.core.token.attribute_authority.AbstractDatiInvocazione;
import org.openspcoop2.pdd.core.token.attribute_authority.AttributeAuthorityException;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.slf4j.Logger;

/**
 * Classe che implementa il recupero degli attributi
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class GestioneAttributeAuthority {

	private Logger log;
	@SuppressWarnings("unused")
	private String idTransazione;
	private PdDContext pddContext;
	private IProtocolFactory<?> protocolFactory;
	public GestioneAttributeAuthority(Logger log, String idTransazione,
			PdDContext pddContext, IProtocolFactory<?> protocolFactory) {
		this.log = log;
		this.idTransazione = idTransazione;
		this.pddContext = pddContext;
		this.protocolFactory = protocolFactory;
	}
	
    public EsitoRecuperoAttributiPortaApplicativa readAttributes(AbstractDatiInvocazione datiInvocazione, String token) throws AttributeAuthorityException {
    	try {
        	
    		EsitoRecuperoAttributiPortaApplicativa esito = (EsitoRecuperoAttributiPortaApplicativa) GestoreToken.readAttributes(this.log, datiInvocazione, 
    				this.pddContext, this.protocolFactory,
    				GestoreToken.PORTA_APPLICATIVA);
        	return esito;
    		
    	}catch(Throwable e) {
    		throw new AttributeAuthorityException(e.getMessage(),e); // errore di processamento
    	}
	}
	
}

