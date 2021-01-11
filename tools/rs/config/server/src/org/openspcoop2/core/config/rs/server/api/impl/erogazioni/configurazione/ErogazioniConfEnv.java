/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.rs.server.api.impl.erogazioni.configurazione;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.config.rs.server.api.impl.IdServizio;
import org.openspcoop2.core.config.rs.server.api.impl.erogazioni.ErogazioniApiHelper;
import org.openspcoop2.core.config.rs.server.api.impl.erogazioni.ErogazioniEnv;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.beans.utils.BaseHelper;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneHelper;

/**
 * ErogazioniConfEnv
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ErogazioniConfEnv extends ErogazioniEnv {
		
	public final ConfigurazioneCore confCore;
	public final ConfigurazioneHelper confHelper;
	public final AccordoServizioParteSpecifica asps;
	public final IdServizio idAsps;
	public final IDPortaApplicativa idPa;

	public ErogazioniConfEnv(HttpServletRequest req, ProfiloEnum profilo, String soggetto, IContext context, String nome, Integer versione, String gruppo, String tipoServizio)
			throws Exception {
		super(req, profilo, soggetto, context);
		this.confCore = new ConfigurazioneCore(this.stationCore);
		this.confHelper = new ConfigurazioneHelper(this.stationCore, this.requestWrapper, this.pd, req.getSession());
			
		this.asps = BaseHelper.supplyOrNotFound( () -> ErogazioniApiHelper.getServizioIfErogazione(tipoServizio, nome, versione, this.idSoggetto.toIDSoggetto(), this), "Erogazione");
		this.idAsps = new IdServizio(this.idServizioFactory.getIDServizioFromAccordo(this.asps), this.asps.getId());
		
		if ( tipoServizio != null && ! this.protocolFactoryMgr._getServiceTypes().get(this.tipo_protocollo).contains(tipoServizio))
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Tipo Servizio: " + tipoServizio + " sconosciuto ");
	
		
		this.idPa = StringUtils.isEmpty(gruppo)
				? BaseHelper.supplyOrNotFound( () -> ErogazioniApiHelper.getIDGruppoPADefault(this.idAsps, this.apsCore),  "Gruppo default per l'erogazione scelta")
				: BaseHelper.supplyOrNotFound( () -> ErogazioniApiHelper.getIDGruppoPA(gruppo, this.idAsps, this.apsCore), "Gruppo per l'erogazione scelta");

	}

}
