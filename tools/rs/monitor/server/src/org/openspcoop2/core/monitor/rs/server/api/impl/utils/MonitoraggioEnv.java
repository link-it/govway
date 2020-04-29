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

package org.openspcoop2.core.monitor.rs.server.api.impl.utils;

import org.openspcoop2.core.monitor.rs.server.config.ServerProperties;
import org.openspcoop2.core.monitor.rs.server.config.SoggettiConfig;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.beans.utils.BaseHelper;
import org.openspcoop2.utils.service.beans.utils.ProfiloUtils;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.slf4j.Logger;

/**
 * MonitoraggioEnv
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class MonitoraggioEnv {
	
	public final IContext context;
	public final ProfiloEnum profilo;
	public final String tipoSoggetto;
	public final String nomeSoggettoLocale;
	public final Logger log;
	public final String tipo_protocollo;
	public final ProtocolFactoryManager protocolFactoryMgr;

	
	public MonitoraggioEnv(IContext context, ProfiloEnum profilo, String nome_soggetto, Logger log) throws UtilsException, ProtocolException {
		this.context = context;
		
		if (profilo == null) {
			this.profilo = ProfiloUtils.getMapProtocolloToProfilo().get(ServerProperties.getInstance().getProtocolloDefault());
		} else {
			this.profilo = profilo;
		}
		
		this.tipoSoggetto = ProtocolFactoryManager.getInstance().getDefaultOrganizationTypes().get(Converter.toProtocollo(profilo));
				
		if (nome_soggetto == null) {
			ServerProperties serverProperties = ServerProperties.getInstance();
			if(serverProperties.useSoggettoDefault()) {
				nome_soggetto = serverProperties.getSoggettoDefaultIfEnabled(ProfiloUtils.toProtocollo(profilo));
			}
		}
				
		if (nome_soggetto != null) {
			this.nomeSoggettoLocale = nome_soggetto;
			if(!SoggettiConfig.existsIdentificativoPorta(this.tipoSoggetto, this.nomeSoggettoLocale)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il soggetto '"+this.nomeSoggettoLocale+"' indicato non esiste");
			}
		}
		else {
			this.nomeSoggettoLocale = null; // non verra' attuato alcun filtro sul soggetto locale.
		}
		
		this.log = log;
		
		this.tipo_protocollo = BaseHelper.tipoProtocolloFromProfilo.get(this.profilo);
		this.protocolFactoryMgr = ProtocolFactoryManager.getInstance();
	}

}
