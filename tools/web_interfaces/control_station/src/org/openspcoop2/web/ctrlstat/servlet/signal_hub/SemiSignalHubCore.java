/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.signal_hub;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.pdd.config.DigestServiceParams;
import org.openspcoop2.pdd.config.DigestServiceParamsDriver;
import org.openspcoop2.protocol.utils.ModIUtils;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.monitor.MonitorUtilities;

/**
 * SemiSignalHubCore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SemiSignalHubCore extends ControlStationCore {

	public SemiSignalHubCore() throws DriverControlStationException {
		super();
	}
	public SemiSignalHubCore(ControlStationCore core) throws DriverControlStationException {
		super(core);
	}
	
	public long countServizi(IDSoggetto soggettoErogatore, String search) throws DriverControlStationException {
		Connection con = null;
		String nomeMetodo = "countServizi";
		try {
			con = ControlStationCore.dbM.getConnection();
			DriverConfigurazioneDB driver = new DriverConfigurazioneDB(con, null, this.tipoDB);
			return DigestServiceParamsDriver.countServizi(driver, soggettoErogatore, search);
		} catch (DriverConfigurazioneException e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverControlStationException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<DigestServiceParams> listServizi(IDSoggetto soggettoErogatore, String search, int offset, int limit) throws DriverControlStationException {
		Connection con = null;
		String nomeMetodo = "listServizi";
		try {
			con = ControlStationCore.dbM.getConnection();
			DriverConfigurazioneDB driver = new DriverConfigurazioneDB(con, null, this.tipoDB);
			return DigestServiceParamsDriver.listServizi(driver, soggettoErogatore, search, offset, limit);
		} catch (DriverConfigurazioneException e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverControlStationException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<DigestServiceParams> getStorico(long idServizioRef) throws DriverControlStationException {
		Connection con = null;
		String nomeMetodo = "getStorico";
		try {
			con = ControlStationCore.dbM.getConnection();
			DriverConfigurazioneDB driver = new DriverConfigurazioneDB(con, null, this.tipoDB);
			return DigestServiceParamsDriver.getStorico(driver, idServizioRef);
		} catch (DriverConfigurazioneException e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverControlStationException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	/**
	 * Intervallo, configurato nel profilo ModI e condiviso con il gateway, dopo il quale una rotazione forzata del seme diventa effettiva
	 */
	public static int getSeedCacheRefreshSeconds() {
		try {
			return ModIUtils.getSignalHubSeedCacheRefreshSeconds();
		}catch(Exception e) {
			ControlStationCore.logError("Lettura dell'intervallo di aggiornamento della cache dei semi Signal Hub non riuscita: "+e.getMessage(), e);
			return SemiSignalHubCostanti.DEFAULT_SEED_CACHE_REFRESH_SECONDS;
		}
	}
	
	/**
	 * Ritorna l'identificativo dell'e-service associato all'erogazione, utilizzato come chiave del progressivo dei signalId
	 */
	public String getEServiceId(long idServizioRef) throws DriverControlStationException {
		String nomeMetodo = "getEServiceId";
		try {
			AccordoServizioParteSpecifica asps = new AccordiServizioParteSpecificaCore(this).getAccordoServizioParteSpecifica(idServizioRef);
			if(asps.getProtocolPropertyList()!=null) {
				for (ProtocolProperty pp : asps.getProtocolPropertyList()) {
					if(CostantiDB.MODIPA_API_IMPL_INFO_ESERVICE_ID.equals(pp.getName())) {
						return pp.getValue();
					}
				}
			}
			return null;
		} catch (DriverRegistroServiziNotFound | DriverRegistroServiziException e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverControlStationException(getPrefixError(nomeMetodo,  e),e);
		}
	}
	
	/**
	 * Le basi dati runtime, dove e' mantenuto il progressivo dei signalId, sono consultabili solamente con un'unica PdD gestita
	 */
	public List<String> getSorgentiDatiRuntime() throws DriverControlStationException {
		if(!this.isSinglePdD()) {
			return new ArrayList<>();
		}
		try {
			return MonitorUtilities.getSorgentiDati();
		}catch(Exception e) {
			throw new DriverControlStationException(e.getMessage(),e);
		}
	}
	
	public List<String> getLabelSorgentiDatiRuntime() throws DriverControlStationException {
		try {
			return MonitorUtilities.getLabelSorgentiDati();
		}catch(Exception e) {
			throw new DriverControlStationException(e.getMessage(),e);
		}
	}
	
	/**
	 * Ritorna l'ultimo signalId generato per l'e-service (null se non ancora generato)
	 */
	public Long getUltimoSignalId(String eServiceId, String sorgente) throws DriverControlStationException {
		try {
			return MonitorUtilities.getUltimoProgressivoRelativo(CostantiLabel.MODIPA_PROTOCOL_NAME, eServiceId, sorgente);
		}catch(Exception e) {
			throw new DriverControlStationException(e.getMessage(),e);
		}
	}
	
}
