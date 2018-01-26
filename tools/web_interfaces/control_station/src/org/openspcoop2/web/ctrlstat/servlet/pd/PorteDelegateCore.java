/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.pd;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.MappingFruizionePortaDelegata;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.MessageSecurityFlowParameter;
import org.openspcoop2.core.config.MtomProcessorFlowParameter;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB;

/**
 * PorteDelegateCore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteDelegateCore extends ControlStationCore {

	public PorteDelegateCore() throws Exception {
		super();
	}
	public PorteDelegateCore(ControlStationCore core) throws Exception {
		super(core);
	}
	

	public List<PortaDelegata> getPorteDelegateWithServizio(Long idServizio) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		String nomeMetodo = "getPorteDelegateWithServizio";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getPorteDelegateWithServizio(idServizio);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<PortaDelegata> porteDelegateWithSoggettoErogatoreList(long idSoggettoErogatore) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		String nomeMetodo = "porteDelegateWithSoggettoErogatoreList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteDelegateWithSoggettoErogatoreList(idSoggettoErogatore);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<PortaDelegata> porteDelegateWithTipoNomeErogatoreList(String tipoSoggettoErogatore, String nomeSoggettoErogatore) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		String nomeMetodo = "porteDelegateWithTipoNomeErogatoreList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteDelegateWithTipoNomeErogatoreList(tipoSoggettoErogatore, nomeSoggettoErogatore);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<PortaDelegata> getPorteDelegateWithServizio(Long idServizio, String tiposervizio, String nomeservizio, Integer versioneServizio,
			Long idSoggetto, String tiposoggetto, String nomesoggetto) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getPorteDelegateWithServizio";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getPorteDelegateWithServizio(idServizio, tiposervizio, nomeservizio, versioneServizio,
					idSoggetto, tiposoggetto, nomesoggetto);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public boolean existsPortaDelegataAzione(String nome) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "existsPortaDelegataAzione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().existsPortaDelegataAzione(nome);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existsPortaDelegata(IDPortaDelegata idPD) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "existsPortaDelegata";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().existsPortaDelegata(idPD);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<MessageSecurityFlowParameter> porteDelegateMessageSecurityRequestList(int idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteDelegateMessageSecurityRequestList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteDelegateMessageSecurityRequestList(idPortaDelegata, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<MessageSecurityFlowParameter> porteDelegateMessageSecurityResponseList(int idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteDelegateMessageSecurityResponseList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteDelegateMessageSecurityResponseList(idPortaDelegata, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public PortaDelegata getPortaDelegata(IDPortaDelegata idPD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		String nomeMetodo = "getPortaDelegata";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getPortaDelegata(idPD);

		} catch (DriverConfigurazioneNotFound de) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public PortaDelegata getPortaDelegata(long idPortaDelegata) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getPortaDelegata";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getPortaDelegata(idPortaDelegata);

		} catch (DriverConfigurazioneNotFound de) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<IDPortaDelegata> getPortaDelegataAzione(String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		String nomeMetodo = "getPortaDelegataAzione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getPortaDelegataAzione(nome);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	
	public long getIdPortaDelegata(String nomePorta) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getPortaDelegata";
	
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			return DBUtils.getIdPortaDelegata(nomePorta,  con, this.getTipoDatabase());

		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<IDPortaDelegata> getAllIdPorteDelegate(FiltroRicercaPorteDelegate filtroRicerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getAllIdPorteDelegate";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getAllIdPorteDelegate(filtroRicerca);

		} catch (DriverConfigurazioneNotFound notFound) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + notFound.getMessage(), notFound);
			return new ArrayList<IDPortaDelegata>();
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<PortaDelegata> porteDelegateList(int idSoggetto, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteDelegateList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteDelegateList(idSoggetto, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<PortaDelegata> porteDelegateList(String superUser, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteDelegateList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteDelegateList(superUser, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<ServizioApplicativo> porteDelegateServizioApplicativoList(int idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteDelegateServizioApplicativoList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteDelegateServizioApplicativoList(idPortaDelegata, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<CorrelazioneApplicativaElemento> porteDelegateCorrelazioneApplicativaList(int idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteDelegateCorrelazioneApplicativaList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteDelegateCorrelazioneApplicativaList(idPortaDelegata, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<CorrelazioneApplicativaRispostaElemento> porteDelegateCorrelazioneApplicativaRispostaList(int idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteDelegateCorrelazioneApplicativaRispostaList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteDelegateCorrelazioneApplicativaRispostaList(idPortaDelegata, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage());
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<MtomProcessorFlowParameter> porteDelegateMTOMRequestList(int idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteDelegateMTOMRequestList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteDelegateMTOMRequestList(idPortaDelegata, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}

	public List<MtomProcessorFlowParameter> porteDelegateMTOMResponseList(int idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "porteDelegateMTOMResponseList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().porteDelegateMTOMResponseList(idPortaDelegata, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	
	
	public IDPortaDelegata getIDPortaDelegataAssociataDefault(IDServizio idServizio, IDSoggetto fruitore) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getIDPortaDelegataAssociataDefault";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getIDPortaDelegataAssociataDefault(idServizio,fruitore);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public IDPortaDelegata getIDPortaDelegataAssociataAzione(IDServizio idServizio, IDSoggetto fruitore) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getIDPortaDelegataAssociataAzione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getIDPortaDelegataAssociataAzione(idServizio,fruitore);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<IDPortaDelegata> getIDPorteDelegateAssociate(IDServizio idServizio, IDSoggetto fruitore) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getIDPorteDelegateAssociate";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getIDPorteDelegateAssociate(idServizio,fruitore);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existsMappingFruizionePortaDelegata(MappingFruizionePortaDelegata mapping) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "existsMappingFruizionePortaDelegata";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.existsMappingFruizionePortaDelegata(mapping);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<String> portaDelegataRuoliList(long idPD, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "portaDelegataRuoliList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().portaDelegataRuoliList(idPD, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
}
