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
package org.openspcoop2.web.ctrlstat.servlet.soggetti;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.constants.CredenzialeTipo;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti;
import org.openspcoop2.core.registry.driver.db.IDSoggettoDB;
import org.openspcoop2.pdd.core.autenticazione.ApiKey;
import org.openspcoop2.pdd.core.autenticazione.ApiKeyUtilities;
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.resources.MapReader;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.registro.GestoreRegistroServiziRemoto;
import org.openspcoop2.web.lib.users.DriverUsersDBException;

/**
 * SoggettiCore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoggettiCore extends ControlStationCore {

	public SoggettiCore() throws Exception {
		super();
	}
	public SoggettiCore(ControlStationCore core) throws Exception {
		super(core);
	}
	
	public List<IDSoggetto> getAllIdSoggettiRegistro(FiltroRicercaSoggetti filtroRicerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getAllIdSoggettiRegistro";
		DriverControlStationDB driver = null;

		try {
			if(this.isRegistroServiziLocale()){
				// prendo una connessione
				con = ControlStationCore.dbM.getConnection();
				// istanzio il driver
				driver = new DriverControlStationDB(con, null, this.tipoDB);
	
				return driver.getDriverRegistroServiziDB().getAllIdSoggetti(filtroRicerca);
			}
			else{
				return GestoreRegistroServiziRemoto.getDriverRegistroServizi(ControlStationCore.log).getAllIdSoggetti(filtroRicerca);
			}

		} catch (DriverRegistroServiziNotFound de) {
			ControlStationCore.log.info("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage());
			return new ArrayList<IDSoggetto>();
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public boolean existsSoggetto(long idSoggetto) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "existsSoggetto";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().existsSoggetto(idSoggetto);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public boolean existsSoggetto(IDSoggetto idSoggetto) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "existsSoggetto";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().existsSoggetto(idSoggetto);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existsSoggetto(String codiceIPA) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "existsSoggetto";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().existsSoggetto(codiceIPA);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public org.openspcoop2.core.registry.Soggetto getSoggettoByCodiceIPA(String codiceIPA) throws DriverConfigurazioneException,DriverRegistroServiziNotFound {
		Connection con = null;
		String nomeMetodo = "getSoggettoByCodiceIPA";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getSoggetto(codiceIPA);

		} catch (DriverRegistroServiziNotFound de) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public String getCodiceIPA(IDSoggetto idSoggetto) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		Connection con = null;
		String nomeMetodo = "getCodiceIPA";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getCodiceIPA(idSoggetto);

		} catch (DriverRegistroServiziNotFound de) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public org.openspcoop2.core.registry.Soggetto getSoggettoRegistro(IDSoggetto idSoggetto) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		Connection con = null;
		String nomeMetodo = "getSoggettoRegistro";
		DriverControlStationDB driver = null;

		try {
			if(this.isRegistroServiziLocale()){
				// prendo una connessione
				con = ControlStationCore.dbM.getConnection();
				// istanzio il driver
				driver = new DriverControlStationDB(con, null, this.tipoDB);
	
				return driver.getDriverRegistroServiziDB().getSoggetto(idSoggetto);
			}
			else{
				return GestoreRegistroServiziRemoto.getDriverRegistroServizi(ControlStationCore.log).getSoggetto(idSoggetto);
			}

		} catch (DriverRegistroServiziNotFound de) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public IDSoggetto getIdSoggettoRegistro(long idErogatore) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getIdSoggettoRegistro";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getIdSoggetto(idErogatore);

		} catch (DriverRegistroServiziNotFound de) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public org.openspcoop2.core.registry.Soggetto getSoggettoRegistro(long idErogatore) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getSoggetto";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getSoggetto(idErogatore);

		} catch (DriverRegistroServiziNotFound de) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public boolean existsSoggettoServiziWithoutConnettore(long idSoggetto) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "existsSoggettoServiziWithoutConnettore";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().existsSoggettoServiziWithoutConnettore(idSoggetto);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existFruizioniServiziSoggettoWithoutConnettore(long idSoggetto, boolean escludiSoggettiEsterni) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "existFruizioniServiziSoggettoWithoutConnettore";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().existFruizioniServiziSoggettoWithoutConnettore(idSoggetto,escludiSoggettiEsterni);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public Soggetto getRouter() throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getRouter";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getRouter();

		} catch (DriverConfigurazioneNotFound de) {
			ControlStationCore.log.info("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage());
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	/**
	 * Ritorna il {@link Soggetto} utilizzando il driver
	 * {@link DriverConfigurazioneDB} che soddisfa i parametri passati come
	 * input
	 */
	public Soggetto getSoggetto(IDSoggetto soggetto) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		String nomeMetodo = "getSoggetto";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getSoggetto(soggetto);

		} catch (DriverControlStationException e) {
			ControlStationCore.log.error(e.getMessage(),e);
			throw new DriverConfigurazioneException(e);
		} catch (DriverConfigurazioneNotFound de) {
			ControlStationCore.log.info("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage());
			throw de;
		} catch (DriverConfigurazioneException e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public Soggetto getSoggetto(long idSoggetto) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getSoggetto";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getSoggetto(idSoggetto);

		} catch (DriverConfigurazioneNotFound de) {
			ControlStationCore.log.info("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage());
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public SoggettoCtrlStat getSoggettoCtrlStat(long idSoggetto) throws DriverConfigurazioneNotFound, DriverRegistroServiziNotFound, DriverControlStationException {
		Connection con = null;
		String nomeMetodo = "getSoggettoCtrlStat";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			Soggetto soggConf = driver.getDriverConfigurazioneDB().getSoggetto(idSoggetto);
			org.openspcoop2.core.registry.Soggetto soggReg = null;
			if(this.isRegistroServiziLocale()){
				soggReg = driver.getDriverRegistroServiziDB().getSoggetto(idSoggetto);
			}

			SoggettoCtrlStat soggetto = new SoggettoCtrlStat(soggReg, soggConf);

			return soggetto;

		} catch (DriverConfigurazioneNotFound de) {
			ControlStationCore.log.info("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage());
			throw de;
		} catch (DriverRegistroServiziNotFound de) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public long getIdSoggetto(String nomeSoggetto, String tipoSoggetto) throws DriverConfigurazioneException
	{
		Connection con = null;
		String nomeMetodo = "getIdSoggetto";
		DriverControlStationDB driver = null;
 
		long idSoggetto=-1;
		try
		{
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			IDSoggetto aSoggetto = new IDSoggetto(tipoSoggetto, nomeSoggetto);
			
			idSoggetto = driver.getDriverConfigurazioneDB().getSoggetto(aSoggetto).getId();

		} catch (DriverConfigurazioneNotFound e) {
			// Lasciare DEBUG, usato anche in servizio API RS
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] NotFound :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] NotFound :" + e.getMessage(),e);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally
		{
			ControlStationCore.dbM.releaseConnection(con);
		}
		return idSoggetto;
	}
	
	public List<Soggetto> soggettiList(String superuser, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "soggettiList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().soggettiList(superuser, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<Soggetto> soggettiWithServiziList(String superuser,ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "soggettiWithServiziList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().soggettiWithServiziList(superuser,ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	
	public boolean isSoggettoInUso(org.openspcoop2.core.registry.Soggetto soggetto, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverControlStationException {

		Connection con = null;
		String nomeMetodo = "isSoggettoInUso(Registro)";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.isSoggettoInUso(soggetto, whereIsInUso, normalizeObjectIds);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public boolean isSoggettoInUso(org.openspcoop2.core.config.Soggetto soggetto, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverControlStationException {

		Connection con = null;
		String nomeMetodo = "isSoggettoInUso(Config)";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.isSoggettoInUso(soggetto, whereIsInUso, normalizeObjectIds);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public boolean isSoggettoInUsoInPackageFinali(org.openspcoop2.core.registry.Soggetto soggetto, Map<ErrorsHandlerCostant,String> whereIsInUso) throws DriverControlStationException {

		Connection con = null;
		String nomeMetodo = "isSoggettoInUsoInPackageFinali";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().isSoggettoInUsoInPackageFinali(soggetto, whereIsInUso);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public boolean isSoggettoInUsoInPackagePubblici(org.openspcoop2.core.registry.Soggetto soggetto, Map<ErrorsHandlerCostant,String> whereIsInUso) throws DriverControlStationException {

		Connection con = null;
		String nomeMetodo = "isSoggettoInUsoInPackagePubblici";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().isSoggettoInUsoInPackagePubblici(soggetto, whereIsInUso);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<IDSoggetto> getSoggettiWithSuperuser(String user) throws DriverUsersDBException {
		Connection con = null;
		String nomeMetodo = "getSoggettiWithSuperuser";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().getSoggettiWithSuperuser(user);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverUsersDBException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	
		
	public List<String> getTipiSoggettiGestiti() throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String nomeMetodo = "getTipiSoggettiGestiti";
		try{
			
			List<String> tipi = new ArrayList<String>();
			
			MapReader<String, IProtocolFactory<?>> protocolFactories = this.protocolFactoryManager.getProtocolFactories();
			Enumeration<String> protocolli = protocolFactories.keys();
			while (protocolli.hasMoreElements()) {
				
				String protocollo = protocolli.nextElement();
				IProtocolFactory<?> protocolFactory = protocolFactories.get(protocollo);
				for (String tipo : protocolFactory.createProtocolConfiguration().getTipiSoggetti()){
					if(!tipi.contains(tipo)){
						tipi.add(tipo);
					}
				}
			}
			
			return tipi;
			
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}
	}
	public String getTipoSoggettoDefaultProtocollo(String protocollo) throws  DriverRegistroServiziException {
		String getTipoServizioDefault = "getTipoSoggettoDefaultProtocollo";
		try{
			
			return this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().getTipoSoggettoDefault();
			
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + getTipoServizioDefault + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + getTipoServizioDefault + "] Error :" + e.getMessage(),e);
		}
	}

	public List<String> getTipiSoggettiGestitiProtocollo(String protocollo) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String nomeMetodo = "getTipiSoggettiGestitiProtocollo";
		try{
			
			return this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().getTipiSoggetti();
			
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}
	}
	public String getProtocolloAssociatoTipoSoggetto(String tipoSoggetto) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String nomeMetodo = "getProtocolloAssociatoTipoSoggetto";
		try{
			
			return this.protocolFactoryManager.getProtocolByOrganizationType(tipoSoggetto);
			
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}
	}
	
	public String getWebContextProtocolAssociatoTipoSoggetto(String tipoSoggetto) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String nomeMetodo = "getWebContextProtocolAssociatoTipoSoggetto";
		try{
			
			String protocollo = this.getProtocolloAssociatoTipoSoggetto(tipoSoggetto);
			IProtocolFactory<?> pf = this.protocolFactoryManager.getProtocolFactoryByName(protocollo);
			if(pf.getManifest().getWeb().getEmptyContext()!=null && pf.getManifest().getWeb().getEmptyContext().isEnabled()){
				return "";
			}
			else{
				return pf.getManifest().getWeb().getContext(0).getName();
			}
			
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}
	}
	
	public String getIdentificativoPortaDefault(String protocollo,IDSoggetto soggetto) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String nomeMetodo = "getIdentificativoPortaDefault";
		try{
			
			return this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createTraduttore().getIdentificativoPortaDefault(soggetto);
			
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}
	}

	public boolean isSupportatoIdentificativoPorta(String protocollo) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String nomeMetodo = "isSupportatoIdentificativoPorta";
		try{
			
			return this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().isSupportoIdentificativoPortaSoggetto();
			
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}
	}
	
	public boolean isSupportatoCodiceIPA(String protocollo) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String nomeMetodo = "isSupportatoCodiceIPA";
		try{
			
			return this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().isSupportoCodiceIPA();
			
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}
	}
	
	public boolean isSupportatoAutenticazioneSoggetti(String protocollo) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String nomeMetodo = "isSupportatoAutenticazioneSoggetti";
		try{
			
			return this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().isSupportoAutenticazioneSoggetti();
			
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}
	}
	
	public boolean isSupportatoAutenticazioneApplicativiErogazione(String protocollo) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String nomeMetodo = "isSupportatoAutenticazioneApplicativiErogazione";
		try{
			
			return this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().isSupportoAutenticazioneApplicativiErogazioni();
			
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}
	}
	
	public boolean isSupportatoAutorizzazioneRichiedenteSenzaAutenticazioneErogazione(String protocollo) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String nomeMetodo = "isSupportatoAutorizzazioneRichiedenteSenzaAutenticazioneErogazione";
		try{
			
			return this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().isSupportatoAutorizzazioneRichiedenteSenzaAutenticazioneErogazioni();
			
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}
	}
	
	public String getCodiceIPADefault(String protocollo,IDSoggetto soggetto,boolean createURI) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String nomeMetodo = "getCodiceIPADefault";
		try{
			
			return this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createTraduttore().getIdentificativoCodiceIPADefault(soggetto,createURI);
			
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}
	}
	
	public List<String> soggettiRuoliList(long idSoggetto, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "soggettiRuoliList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().soggettiRuoliList(idSoggetto, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public org.openspcoop2.core.registry.Soggetto soggettoWithCredenzialiBasic(String user, String password, boolean checkPassword) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "soggettoWithCredenzialiBasic";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().soggettoWithCredenzialiBasic(user, password, checkPassword);

		} 
		catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public String toAppId(String protocollo, IDSoggetto idSoggetto,  boolean multipleApiKeys) throws DriverConfigurazioneException {
		
		Connection con = null;
		String nomeMetodo = "toAppId";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return ApiKeyUtilities.toAppId(protocollo, idSoggetto, multipleApiKeys, driver.getDriverRegistroServiziDB());

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
		
	}
	
	public ApiKey newApiKey(String protocollo, IDSoggetto idSoggetto) throws DriverConfigurazioneException {
		
		Connection con = null;
		String nomeMetodo = "newApiKey";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return ApiKeyUtilities.newApiKey(protocollo, idSoggetto, this.getSoggettiApiKeyLunghezzaPasswordGenerate(), driver.getDriverRegistroServiziDB());

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
		
	}
	
	public ApiKey newMultipleApiKey() throws DriverConfigurazioneException {
		
		String nomeMetodo = "newMultipleApiKey";
		try {
			return ApiKeyUtilities.newMultipleApiKey(this.getSoggettiApiKeyLunghezzaPasswordGenerate());
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}
		
	}
	
	public org.openspcoop2.core.registry.Soggetto soggettoWithCredenzialiApiKey(String user, boolean appId) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "soggettoWithCredenzialiApiKey";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().soggettoWithCredenzialiApiKey(user, appId);

		} 
		catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public org.openspcoop2.core.registry.Soggetto getSoggettoRegistroAutenticatoBasic(String user, String password) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getSoggettoAutenticatoBasic";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getSoggettoByCredenzialiBasic(user, password, this.getSoggettiPasswordEncrypt());

		} 
		catch (DriverRegistroServiziNotFound e) {
			return null;
		}
		catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public org.openspcoop2.core.registry.Soggetto getSoggettoRegistroAutenticatoSsl(String subject, String issuer) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getSoggettoRegistroAutenticatoSsl";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getSoggettoByCredenzialiSsl(subject, issuer);

		} 
		catch (DriverRegistroServiziNotFound e) {
			return null;
		}
		catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public org.openspcoop2.core.registry.Soggetto getSoggettoRegistroAutenticatoSsl(CertificateInfo certificate, boolean strictVerifier) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getSoggettoRegistroAutenticatoSsl";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getSoggettoByCredenzialiSsl(certificate, strictVerifier);

		} 
		catch (DriverRegistroServiziNotFound e) {
			return null;
		}
		catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<org.openspcoop2.core.registry.Soggetto> soggettoWithCredenzialiSslList(CertificateInfo certificate, boolean strictVerifier) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getSoggettoRegistroAutenticatoSsl";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().soggettoWithCredenzialiSslList(certificate, strictVerifier);

		} 
		catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public org.openspcoop2.core.registry.Soggetto getSoggettoRegistroAutenticatoPrincipal(String principal) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getSoggettoAutenticatoPrincipal";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getSoggettoByCredenzialiPrincipal(principal);

		} 
		catch (DriverRegistroServiziNotFound e) {
			return null;
		}
		catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<IDSoggettoDB> getSoggettiFromTipoAutenticazione(List<String> tipiSoggetto, String superuser,CredenzialeTipo credenziale, Boolean appId, PddTipologia pddTipologia) throws DriverConfigurazioneException{
		Connection con = null;
		String nomeMetodo = "getSoggettiFromTipoAutenticazione";
		DriverControlStationDB driver = null;
		
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getSoggettiFromTipoAutenticazione(tipiSoggetto,superuser,credenziale,appId, pddTipologia);

		} 
		catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	
	public String getDettagliSoggettoInUso(org.openspcoop2.core.registry.Soggetto soggetto) throws DriverConfigurazioneException, DriverConfigurazioneNotFound, DriverControlStationException {
		HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
		boolean normalizeObjectIds = true;
		boolean saInUso  = this.isSoggettoInUso(soggetto, whereIsInUso, normalizeObjectIds );
		
		StringBuilder inUsoMessage = new StringBuilder();
		if(saInUso) {
			
			IDSoggetto idSoggetto = new IDSoggetto();
			idSoggetto.setTipo(soggetto.getTipo());
			idSoggetto.setNome(soggetto.getNome());
			idSoggetto.setCodicePorta(soggetto.getIdentificativoPorta());
			
			String s = DBOggettiInUsoUtils.toString(idSoggetto , whereIsInUso, false, "\n", normalizeObjectIds);
			if(s!=null && s.startsWith("\n") && s.length()>1) {
				s = s.substring(1);
			}
			inUsoMessage.append(s);
			inUsoMessage.append("\n");
		} else {
			inUsoMessage.append(SoggettiCostanti.LABEL_IN_USO_BODY_HEADER_NESSUN_RISULTATO);
		}
		
		return inUsoMessage.toString();
	}
}
