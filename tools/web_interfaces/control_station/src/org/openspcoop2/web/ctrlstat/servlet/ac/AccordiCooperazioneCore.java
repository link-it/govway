/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.web.ctrlstat.servlet.ac;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDAccordoCooperazioneWithSoggetto;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaAccordi;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.validator.ValidazioneResult;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;

/**
 * AccordiCooperazioneCore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordiCooperazioneCore extends ControlStationCore {

	public AccordiCooperazioneCore() throws Exception {
		super();
	}
	public AccordiCooperazioneCore(ControlStationCore core) throws Exception {
		super(core);
	}

	public ValidazioneResult validazione(AccordoCooperazione ac,SoggettiCore soggettiCore) throws DriverRegistroServiziException {
		String nomeMetodo = "validazione";
		try {
			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(ac.getSoggettoReferente().getTipo());
			IProtocolFactory protocol = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			return protocol.createValidazioneAccordi().valida(ac);
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		}
	}
	
	public long getIdAccordoCoopeazione(IDAccordoCooperazione idAccordo) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getIdAccordoCoopeazione";
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			return DBUtils.getIdAccordoCooperazione(idAccordo, con, this.tipoDB);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<IDSoggetto> accordiCoopPartecipantiList(long idAccordo,ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiCoopPartecipantiList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().accordiCoopPartecipantiList(idAccordo,ricerca);
		} catch (DriverRegistroServiziException e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public void validaStatoAccordoCooperazione(AccordoCooperazione ac) throws DriverRegistroServiziException,ValidazioneStatoPackageException{
		Connection con = null;
		String nomeMetodo = "validaStatoAccordoCooperazione";
		DriverRegistroServiziDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverRegistroServiziDB(con, ControlStationCore.log, this.tipoDB);

			driver.validaStatoAccordoCooperazione(ac);
			
		} catch (ValidazioneStatoPackageException e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] ValidazioneStatoPackageException :" + e.getMessage(), e);
			throw e;
		}  catch (DriverRegistroServiziException e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] DriverRegistroServiziException :" + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<Documento> accordiCoopAllegatiList(int idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiCoopAllegatiList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().accordiCoopAllegatiList(idAccordo, ricerca);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean isAccordoCooperazioneInUso(AccordoCooperazione ac, Map<ErrorsHandlerCostant, List<String>> whereIsInUso) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "isAccordoCooperazioneInUso";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().isAccordoCooperazioneInUso(ac, whereIsInUso);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<AccordoCooperazione> accordiCooperazioneList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiCooperazioneList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().accordiCooperazioneList(superuser, ricerca);
		} catch (DriverRegistroServiziException e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	
	
	public boolean existsAccordoCooperazione(IDAccordoCooperazione idAccordo) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "existsAccordoCooperazione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverRegistroServiziDB().existsAccordoCooperazione(idAccordo);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public IDAccordoCooperazioneWithSoggetto getIdAccordoCooperazione(long idAccordo)
			throws DriverRegistroServiziException,
			DriverRegistroServiziNotFound {
		Connection con = null;
		String nomeMetodo = "getIdAccordoCooperazione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getIdAccordoCooperazione(idAccordo);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public AccordoCooperazione getAccordoCooperazione(IDAccordoCooperazione idAccordo)
			throws DriverRegistroServiziException,
			DriverRegistroServiziNotFound {
		Connection con = null;
		String nomeMetodo = "getAccordoCooperazione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getAccordoCooperazione(idAccordo);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public AccordoCooperazione getAccordoCooperazione(IDAccordoCooperazione idAccordo,boolean deepRead) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		Connection con = null;
		String nomeMetodo = "getAccordoCooperazione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getAccordoCooperazione(idAccordo,deepRead);
		} catch (DriverRegistroServiziNotFound e) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw e;
		} catch (DriverRegistroServiziException e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public AccordoCooperazione getAccordoCooperazione(long idAccordo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		Connection con = null;
		String nomeMetodo = "getAccordoCooperazione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getAccordoCooperazione(idAccordo);
		} catch (DriverRegistroServiziNotFound e) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw e;
		} catch (DriverRegistroServiziException e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[RegservCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<IDAccordoCooperazione> getAllIdAccordiCooperazione(
			FiltroRicercaAccordi filtroRicerca)
			throws DriverRegistroServiziException,
			DriverRegistroServiziNotFound {
		Connection con = null;
		String nomeMetodo = "getAccordoCooperazione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getAllIdAccordiCooperazione(filtroRicerca);

		} catch (DriverRegistroServiziNotFound e) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(),e);
			throw e;
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<AccordoCooperazione> accordiCoopWithSoggettoPartecipante(IDSoggetto idSoggetto) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiCoopWithSoggettoPartecipante";
		DriverRegistroServiziDB driver = null;
			
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverRegistroServiziDB(con, ControlStationCore.log, this.tipoDB);
		
			return driver.accordiCoopWithSoggettoPartecipante(idSoggetto);
		} catch (DriverRegistroServiziException e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
}
