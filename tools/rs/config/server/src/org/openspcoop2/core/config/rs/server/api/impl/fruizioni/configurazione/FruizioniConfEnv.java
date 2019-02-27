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
package org.openspcoop2.core.config.rs.server.api.impl.fruizioni.configurazione;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.core.config.rs.server.api.impl.IdServizio;
import org.openspcoop2.core.config.rs.server.api.impl.erogazioni.ErogazioniApiHelper;
import org.openspcoop2.core.config.rs.server.api.impl.erogazioni.ErogazioniEnv;
import org.openspcoop2.core.config.rs.server.model.ProfiloEnum;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.utils.service.context.IContext;

/**
 * FruizioniConfEnv
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class FruizioniConfEnv extends ErogazioniEnv {
	
	public final AccordoServizioParteSpecifica asps;
	public final IdServizio idAsps;
	public final IDPortaDelegata idPd;
	public final IDSoggetto idErogatore;


	public FruizioniConfEnv(HttpServletRequest req, ProfiloEnum profilo, String soggetto, IContext context, String erogatore, String nome, Integer versione, String gruppo)
			throws Exception {
		super(req, profilo, soggetto, context);
		
		this.idErogatore = new IDSoggetto(this.tipo_soggetto, erogatore);
		
		this.asps = Helper.supplyOrNotFound( 
				() -> ErogazioniApiHelper.getServizioIfFruizione(nome, versione, this.idErogatore, this.idSoggetto.toIDSoggetto(), this),
				"Fruizione"
			);
		
		this.idAsps = new IdServizio(this.idServizioFactory.getIDServizioFromAccordo(this.asps), this.asps.getId());
		this.idPd = StringUtils.isEmpty(gruppo)
				? Helper.supplyOrNotFound( () -> ErogazioniApiHelper.getIDGruppoPDDefault(this.idSoggetto.toIDSoggetto(), this.idAsps, this.apsCore), "Gruppo default per la fruizione scelta")
				: Helper.supplyOrNotFound( () -> ErogazioniApiHelper.getIDGruppoPD(gruppo, this.idSoggetto.toIDSoggetto(), this.idAsps, this.apsCore), "Gruppo con nome " + gruppo + " per la fruizione scelta"); 

	}

}
