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
package org.openspcoop2.web.ctrlstat.servlet.sa;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.DBOggettiInUsoUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.dao.Ruolo;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationNotFound;

/**
 * ServiziApplicativiCore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServiziApplicativiCore extends ControlStationCore {

	public ServiziApplicativiCore() throws Exception {
		super();
	}
	public ServiziApplicativiCore(ControlStationCore core) throws Exception {
		super(core);
	}
	
	
	public ServizioApplicativo getServizioApplicativo(long idServizioApplicativo) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getServizioApplicativo";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			if(this.isRegistroServiziLocale()){
				return driver.getServizioApplicativoWithRuolo(idServizioApplicativo);
			}
			else{
				return driver.getDriverConfigurazioneDB().getServizioApplicativo(idServizioApplicativo);
			}

		} catch (DriverControlStationNotFound e) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(),e);
			throw new DriverConfigurazioneNotFound(e);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public ServizioApplicativo getServizioApplicativo(String nomeServizioApplicativo, String nomeProprietario, String tipoProprietario) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getServizioApplicativo";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getServizioApplicativoWithRuolo(nomeServizioApplicativo, nomeProprietario, tipoProprietario);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public ServizioApplicativo getServizioApplicativo(IDPortaDelegata idPD, String servizioApplicativo) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getServizioApplicativo";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getServizioApplicativoWithRuolo(idPD, servizioApplicativo);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public ServizioApplicativo getServizioApplicativo(IDPortaApplicativa idPA, String servizioApplicativo) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getServizioApplicativo";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getServizioApplicativoWithRuolo(idPA, servizioApplicativo);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public ServizioApplicativo getServizioApplicativoAutenticato(IDPortaDelegata idPD, String user, String password) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getServizioApplicativoAutenticato";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getServizioApplicativoAutenticatoWithRuolo(idPD, user, password);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public ServizioApplicativo getServizioApplicativoAutenticato(String user, String password) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getServizioApplicativoAutenticato";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getServizioApplicativoAutenticatoWithRuolo(user, password);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public ServizioApplicativo getServizioApplicativoAutenticato(IDPortaDelegata idPD, String subject) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getServizioApplicativoAutenticato";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getServizioApplicativoAutenticatoWithRuolo(idPD, subject);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public ServizioApplicativo getServizioApplicativoAutenticato(String subject) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getServizioApplicativoAutenticato";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getServizioApplicativoAutenticatoWithRuolo(subject);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public ServizioApplicativo getServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getServizioApplicativo(IDServizioApplicativo)";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			IDPortaDelegata idPD = new IDPortaDelegata();
			idPD.setSoggettoFruitore(idServizioApplicativo.getIdSoggettoProprietario());
			return driver.getServizioApplicativoWithRuolo(idPD, idServizioApplicativo.getNome());

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	

	

	public List<Ruolo> ruoliWithIdServizioApplicativo(long idServizioApplicativo) throws DriverControlStationException {
		Connection con = null;
		String nomeMetodo = "ruoliWithIdServizioApplicativo";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.ruoliWithIdServizioApplicativo(idServizioApplicativo);
		} catch (DriverControlStationException de) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<ServizioApplicativo> accordiServizioApplicativoList(int idSoggettoFruitoreDelServizio, int idServizio, ISearch ricerca) throws DriverControlStationException {
		Connection con = null;
		String nomeMetodo = "accordiServizioApplicativoList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.accordiServizioApplicativoList(idSoggettoFruitoreDelServizio, idServizio, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public boolean existsRuoloServizioApplicativo(Long idServizioApplicativo) throws DriverControlStationException {
		Connection con = null;
		String nomeMetodo = "existsRuoloServizioApplicativo";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.existsRuoloServizioApplicativo(idServizioApplicativo);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public boolean existsRuoloServizioApplicativo(Long idAccordo, Long idServizioApplicativo, int servizioCorrelato) throws DriverControlStationException {
		Connection con = null;
		String nomeMetodo = "existsRuoloServizioApplicativo";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.existsRuoloServizioApplicativo(idAccordo, idServizioApplicativo, servizioCorrelato);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<ServizioApplicativo> serviziServizioApplicativoList(int idSoggettoFruitoreDelServizio, int idServizio, ISearch ricerca) throws DriverControlStationException {
		Connection con = null;
		String nomeMetodo = "serviziServizioApplicativoList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.serviziServizioApplicativoList(idSoggettoFruitoreDelServizio, idServizio, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	
	public long getIdServizioApplicativo(IDSoggetto idSoggetto, String nomeServizioApplicativo) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getIdServizioApplicativo";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getIdServizioApplicativo(idSoggetto, nomeServizioApplicativo);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<ServizioApplicativo> servizioApplicativoWithCredenzialiList(String utente, String password) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "servizioApplicativoWithCredenzialiList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().servizioApplicativoWithCredenzialiList(utente, password);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<ServizioApplicativo> servizioApplicativoWithCredenzialiList(String subject) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "servizioApplicativoWithCredenzialiList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().servizioApplicativoWithCredenzialiList(subject);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<ServizioApplicativo> servizioApplicativoList(ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "servizioApplicativoList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().servizioApplicativoList(ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<ServizioApplicativo> servizioApplicativoList(IDSoggetto idSoggetto,ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "servizioApplicativoList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().servizioApplicativoList(idSoggetto,ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	

	public boolean isServizioApplicativoInUso(ServizioApplicativo sa, Map<ErrorsHandlerCostant, String> whereIsInUso) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "isServizioApplicativoInUso";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().isServizioApplicativoInUso(sa, whereIsInUso);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	
	public boolean existsServizioApplicativo(IDSoggetto idSoggetto, String nomeServizioApplicativo) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "existsServizioApplicativo";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().existsServizioApplicativo(idSoggetto, nomeServizioApplicativo);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public ServizioApplicativo getServizioApplicativo(IDSoggetto idSoggetto,
			String nomeServizioApplicativo)
			throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		String nomeMetodo = "getServizioApplicativo";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getServizioApplicativo(idSoggetto, nomeServizioApplicativo);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	
	public List<ServizioApplicativo> getServiziApplicativiByIdErogatore(Long idErogatore) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		String nomeMetodo = "getServiziApplicativiWithIdErogatore";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getServiziApplicativiWithIdErogatore(idErogatore);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<ServizioApplicativo> soggettiServizioApplicativoList(IDSoggetto idSoggetto,String superuser,CredenzialeTipo credenziale) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		String nomeMetodo = "soggettiServizioApplicativoList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().soggettiServizioApplicativoList(idSoggetto,superuser,credenziale);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<ServizioApplicativo> soggettiServizioApplicativoList(String superuser, ISearch ricerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		String nomeMetodo = "soggettiServizioApplicativoList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().soggettiServizioApplicativoList(superuser, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<ServizioApplicativo> soggettiServizioApplicativoList(ISearch ricerca,Long idSoggetto) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		String nomeMetodo = "soggettiServizioApplicativoList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().soggettiServizioApplicativoList(idSoggetto, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean isServizioApplicativoInUso(IDServizioApplicativo idServizioApplicativo,
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean isRegistroServiziLocale) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		String nomeMetodo = "isServizioApplicativoInUso";
		
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();

			return DBOggettiInUsoUtils.isServizioApplicativoInUso(con, this.tipoDB, idServizioApplicativo, whereIsInUso, isRegistroServiziLocale);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existsPortaDelegataServizioApplicativo(Long idServizioApplicativo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		String nomeMetodo = "existsPortaDelegataServizioApplicativo";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().existsPortaDelegataServizioApplicativo(idServizioApplicativo);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public boolean existsPortaApplicativaServizioApplicativo(Long idServizioApplicativo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		String nomeMetodo = "existsPortaApplicativaServizioApplicativo";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().existsPortaApplicativaServizioApplicativo(idServizioApplicativo);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existsPoliticaSicurezzaServizioApplicativo(Long idServizioApplicativo) throws DriverControlStationException {
		Connection con = null;
		String nomeMetodo = "existsPoliticaSicurezzaServizioApplicativo";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.existsPoliticaSicurezzaServizioApplicativo(idServizioApplicativo);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<Ruolo> getRuoli(String userLogin) throws DriverControlStationException {
		Connection con = null;
		String nomeMetodo = "getRuoli";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getRuoli(userLogin);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<Ruolo> ruoliList(ISearch ricerca) throws DriverControlStationException {
		Connection con = null;
		String nomeMetodo = "ruoliList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.ruoliList(ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public Ruolo getRuolo(long idRuolo) throws DriverControlStationException, DriverControlStationNotFound {
		Connection con = null;
		String nomeMetodo = "getRuolo";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getRuolo(idRuolo);

		} catch (DriverControlStationNotFound e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(),e);
			throw new DriverControlStationNotFound("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

}
