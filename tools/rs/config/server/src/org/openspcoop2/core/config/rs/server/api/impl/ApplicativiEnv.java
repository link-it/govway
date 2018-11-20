package org.openspcoop2.core.config.rs.server.api.impl;

import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.ProfiloEnum;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;

public class ApplicativiEnv {
	public final String tipo_protocollo;
	public final ControlStationCore stationCore;
	public final ServiziApplicativiCore saCore;
	public final SoggettiCore	soggettiCore;
	public final PorteDelegateCore porteDelegateCore;
	
	ApplicativiEnv(ProfiloEnum profilo) throws UtilsException, Exception {

		this.tipo_protocollo = ApplicativiApiHelper.getProtocolloOrDefault(profilo);

		ServerProperties serverProperties = ServerProperties.getInstance();
		this.stationCore = new ControlStationCore(true, serverProperties.getConfDirectory(),this.tipo_protocollo); 
		this.saCore = new ServiziApplicativiCore(this.stationCore);
		this.porteDelegateCore = new PorteDelegateCore(this.stationCore);
		this.soggettiCore = new SoggettiCore(this.saCore);
	}
	
	
}