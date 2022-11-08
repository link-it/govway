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
package org.openspcoop2.core.config.rs.server.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.rs.server.api.StatusApi;
import org.openspcoop2.core.config.rs.server.api.impl.soggetti.SoggettiEnv;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.Problem;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.utils.beans.BeanUtils;
import org.openspcoop2.utils.beans.BlackListElement;
import org.openspcoop2.utils.service.BaseImpl;
import org.openspcoop2.utils.service.authorization.AuthorizationConfig;
import org.openspcoop2.utils.service.authorization.AuthorizationManager;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;

/**
 * StatusApiServiceImpl
 *
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatusApiServiceImpl extends BaseImpl implements StatusApi {

	public StatusApiServiceImpl(){
		super(org.slf4j.LoggerFactory.getLogger(StatusApiServiceImpl.class));
	}

	private AuthorizationConfig getAuthorizationConfig() throws Exception{
		return new AuthorizationConfig(ServerProperties.getInstance().getProperties());
	}

    /**
     * Ritorna lo stato dell&#x27;applicazione
     *
     * Ritorna lo stato dell&#x27;applicazione in formato problem+json
     *
     */
	@Override
    public Problem getStatus() {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
			
			Problem p = null;
			          
			// Cerco soggetto di default in modo che sicuro esiste, almeno verifico la connessione
			SoggettiEnv env = new SoggettiEnv(context.getServletRequest(), ProfiloEnum.APIGATEWAY, context);			
			IDSoggetto idSogg = env.idSoggetto.toIDSoggetto();
			
			boolean exists = false;
			try {
				exists = env.soggettiCore.existsSoggetto(idSogg);
			} catch (Exception e) {}
			
			if (exists) {
				p = new Problem();
				org.openspcoop2.utils.service.fault.jaxrs.ProblemRFC7807 pUtils = FaultCode.STATUS_OK.toFault();
				List<BlackListElement> metodiEsclusi = new ArrayList<BlackListElement>(0);
				BeanUtils.copy(p, pUtils, metodiEsclusi);
			}
			else {
				throw new Exception("Servizio non disponibile");
			}
			
			context.getLogger().info("Invocazione completata con successo");
            return p;
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
}

