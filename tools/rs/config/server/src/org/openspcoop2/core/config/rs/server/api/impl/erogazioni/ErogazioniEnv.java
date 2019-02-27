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
package org.openspcoop2.core.config.rs.server.api.impl.erogazioni;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.core.config.rs.server.api.impl.Environment;
import org.openspcoop2.core.config.rs.server.model.ProfiloEnum;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniHelper;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeHelper;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateHelper;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiHelper;

/**
 * ErogazioniEnv
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ErogazioniEnv extends Environment {

	// TODO: Vedere se posso spezzettare in più environment per motivi di efficienza. (Per esempio non ho bisogno di PorteDelegateCore nelle opeazioni che coinvolgono le erogazioni)
	public final AccordiServizioParteSpecificaCore apsCore;
	public final AccordiServizioParteComuneCore apcCore;
	public final ArchiviCore archiviCore;
	public final PorteApplicativeCore paCore;
	public final PorteDelegateCore pdCore;
	public final ServiziApplicativiCore saCore;

	public final AccordiServizioParteSpecificaHelper apsHelper;
	public final ServiziApplicativiHelper saHelper;
	public final PorteApplicativeHelper paHelper;
	public final PorteDelegateHelper pdHelper;
	public final ErogazioniHelper erogazioniHelper;
	
	public final List<Soggetto> soggettiCompatibili = null;
	  
	public final boolean isSupportatoAutenticazioneSoggetti;
	public final IDAccordoFactory idAccordoFactory;
	public final IDServizioFactory idServizioFactory;	
	

	public ErogazioniEnv(HttpServletRequest req, ProfiloEnum profilo, String soggetto, IContext context)
			throws Exception {
		super(req, profilo, soggetto, context);
		this.apsCore = new AccordiServizioParteSpecificaCore(this.stationCore);
		this.apcCore = new AccordiServizioParteComuneCore(this.stationCore);
		this.archiviCore = new ArchiviCore(this.stationCore);
		this.paCore  = new PorteApplicativeCore(this.stationCore);
		this.pdCore = new  PorteDelegateCore(this.stationCore);
		this.saCore = new ServiziApplicativiCore(this.stationCore);
		
		this.apsHelper = new AccordiServizioParteSpecificaHelper(this.stationCore, this.requestWrapper, this.pd, req.getSession());
		this.saHelper = new ServiziApplicativiHelper(this.stationCore, this.requestWrapper, this.pd, req.getSession());
		this.paHelper = new PorteApplicativeHelper(this.stationCore, this.requestWrapper, this.pd, req.getSession());
		this.pdHelper = new PorteDelegateHelper(this.stationCore, this.requestWrapper, this.pd, req.getSession());
		this.erogazioniHelper = new ErogazioniHelper(this.stationCore, this.requestWrapper, this.pd, req.getSession());
		
		this.isSupportatoAutenticazioneSoggetti = this.soggettiCore.isSupportatoAutenticazioneSoggetti(this.tipo_protocollo);
		this.idAccordoFactory = IDAccordoFactory.getInstance();
		this.idServizioFactory = IDServizioFactory.getInstance();

	}

}
