/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.audit;

import java.sql.Connection;
import java.util.List;

import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB;
import org.openspcoop2.web.lib.audit.AuditException;
import org.openspcoop2.web.lib.audit.DriverAuditDBAppender;


/**
 * AuditingCore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AuditingCore extends ControlStationCore {

	public AuditingCore() throws Exception {
		super();
	}
	public AuditingCore(ControlStationCore core) throws Exception {
		super(core);
	}

	
	public List<org.openspcoop2.web.lib.audit.log.Operation> auditOperationList(ISearch ricerca,
			String datainizio, String datafine, String tipooperazione,
			String tipooggetto, String id, String oldid, String utente,
			String statooperazione, String contoggetto) throws AuditException {
		Connection con = null;
		String nomeMetodo = "auditOperationList";
		DriverAuditDBAppender driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverAuditDBAppender(con, this.tipoDB);
			return driver.auditOperationList(ricerca, Liste.AUDIT_REPORT,
					datainizio, datafine, tipooperazione,
					tipooggetto, id, oldid, utente,
					statooperazione, contoggetto);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new AuditException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public org.openspcoop2.web.lib.audit.log.Operation getAuditOperation(long idOp) throws AuditException {
		Connection con = null;
		String nomeMetodo = "getAuditOperation";
		DriverAuditDBAppender driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverAuditDBAppender(con, this.tipoDB);
			return driver.getOperation(idOp);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new AuditException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public org.openspcoop2.web.lib.audit.dao.Configurazione getConfigurazioneAudit() throws AuditException {
		Connection con = null;
		String nomeMetodo = "getConfigurazioneAudit";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverAuditDB().getConfigurazione();

		} catch (AuditException ae) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  ae),ae);
			throw ae;
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new AuditException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
}
