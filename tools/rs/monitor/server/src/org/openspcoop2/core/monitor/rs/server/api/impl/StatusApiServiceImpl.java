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
package org.openspcoop2.core.monitor.rs.server.api.impl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.monitor.rs.server.api.StatusApi;
import org.openspcoop2.core.monitor.rs.server.config.DBManager;
import org.openspcoop2.core.monitor.rs.server.config.LoggerProperties;
import org.openspcoop2.core.monitor.rs.server.config.ServerProperties;
import org.openspcoop2.core.monitor.rs.server.model.Problem;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.utils.beans.BlackListElement;
import org.openspcoop2.utils.service.BaseImpl;
import org.openspcoop2.utils.service.authorization.AuthorizationConfig;
import org.openspcoop2.utils.service.authorization.AuthorizationManager;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.monitor.core.utils.BeanUtils;
import org.openspcoop2.web.monitor.eventi.bean.EventoBean;
import org.openspcoop2.web.monitor.eventi.dao.EventiService;

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
                        
			// Cerco un evento con id negativo, in modo che sicuro non esiste, almeno verifico la connessione
			DBManager dbManager = DBManager.getInstance();
			Connection connection = null;
			Problem p = null;
			try {
				connection = dbManager.getConnectionTracce();
				ServiceManagerProperties smp = dbManager.getServiceManagerPropertiesTracce();
				EventiService eventiService = new EventiService(connection, true, smp, LoggerProperties.getLoggerDAO());
				
				Long id = 1l;
				boolean ok = false;
				try {
					EventoBean eventoBean = eventiService.findById(id, true);
					// o l'evento viene ritornato o viene lanciata una NotFoundException. Gli altri casi sono errore
					if(eventoBean!=null) {
						ok = true;
					}
				}catch(NotFoundException notFound) {
					ok = true;
				}
				
				if(ok) {
					p = new Problem();
					org.openspcoop2.utils.service.fault.jaxrs.ProblemRFC7807 pUtils = FaultCode.STATUS_OK.toFault();
					List<BlackListElement> metodiEsclusi = new ArrayList<BlackListElement>(0);
					BeanUtils.copy(p, pUtils, metodiEsclusi);
				}
				else {
					throw new Exception("Servizio non disponibile");
				}
			}
			finally {
				dbManager.releaseConnectionTracce(connection);
			}
        
			context.getLogger().info("Invocazione completata con successo");
            return p;
		}
		catch(jakarta.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }
    
}

