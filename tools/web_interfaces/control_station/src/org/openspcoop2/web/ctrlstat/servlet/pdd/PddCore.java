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
package org.openspcoop2.web.ctrlstat.servlet.pdd;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicerca;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationNotFound;

/**
 * PddCore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PddCore extends ControlStationCore {

	public PddCore() throws Exception {
		super();
	}
	public PddCore(ControlStationCore core) throws Exception {
		super(core);
	}
	
	public List<PortaDominio> porteDominioWithSubject(String subject) throws DriverRegistroServiziException,ValidazioneStatoPackageException{
		Connection con = null;
		String nomeMetodo = "porteDominioWithSubject";
		DriverRegistroServiziDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverRegistroServiziDB(con, ControlStationCore.log, this.tipoDB);

			return driver.porteDominioWithSubject(subject);
			
		} catch (DriverRegistroServiziException e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] DriverRegistroServiziException :" + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<String> fruzioniWithClientAuthAbilitato(String nomePdD) throws DriverRegistroServiziException{
		Connection con = null;
		String nomeMetodo = "fruzioniWithClientAuthAbilitato";
		DriverRegistroServiziDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverRegistroServiziDB(con, ControlStationCore.log, this.tipoDB);

			return driver.fruzioniWithClientAuthAbilitato(nomePdD);
			
		} catch (DriverRegistroServiziException e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] DriverRegistroServiziException :" + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<String> getAllIdPorteDominio(FiltroRicerca filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		Connection con = null;
		String nomeMetodo = "getAllIdPorteDominio";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getAllIdPorteDominio(filtroRicerca);

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
	
	public long existPdd(String nomePdd) throws DriverControlStationException {
		Connection con = null;
		String nomeMetodo = "existPdd";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.existPdd(nomePdd);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public PdDControlStation getPdDControlStation(String pdd) throws DriverControlStationException, DriverControlStationNotFound {
		Connection con = null;
		String nomeMetodo = "getPdd";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getPdDControlStation(pdd);

		} catch (DriverControlStationNotFound e) {
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public PdDControlStation getPdDControlStation(long idPdd) throws DriverControlStationException, DriverControlStationNotFound {
		Connection con = null;
		String nomeMetodo = "getPdd";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getPdDControlStation(idPdd);

		} catch (DriverControlStationNotFound e) {
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public PortaDominio getPortaDominio(String nomePdD) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		String nomeMetodo = "getPortaDominio(" + nomePdD + ")";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getPortaDominio(nomePdD);

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
	
	public List<PortaDominio> pddListSinglePdd(String userLogin, ISearch ricerca) throws DriverRegistroServiziException {
		return this.pddListSinglePdd(userLogin,null,ricerca);
	}
	public List<PortaDominio> pddListSinglePdd(String userLogin,String tipo, ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "pddListSinglePdd";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().porteDominioList(userLogin, tipo, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	
	public List<PdDControlStation> pddList(String userLogin, ISearch ricerca) throws DriverControlStationException {
		Connection con = null;
		String nomeMetodo = "pddList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.pddList(userLogin, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<PortaDominio> porteDominioList(String userLogin, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "porteDominioList()";
		try {
			List<PortaDominio> list = new ArrayList<PortaDominio>();
			List<PdDControlStation> listControlStation = this.pddList(userLogin, ricerca);
			while (listControlStation.size() > 0) {
				list.add(listControlStation.remove(0));
			}
			return list;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}
	}
	
	public List<org.openspcoop2.core.config.Soggetto> pddSoggettiList(int idPDD, ISearch ricerca) throws DriverControlStationException {
		Connection con = null;
		String nomeMetodo = "pddSoggettiList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.pddSoggettiList(idPDD, ricerca);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public boolean isPddInUsoSinglePdd(PortaDominio pdd, List<String> whereIsInUso) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "isPddInUsoSinglePdd";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().isPddInUso(pdd, whereIsInUso);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean isPddInUso(PdDControlStation pdd, List<String> whereIsInUso) throws DriverControlStationException {
		Connection con = null;
		String nomeMetodo = "isPddInUso";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.isPddInUso(pdd, whereIsInUso);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<SoggettoCtrlStat> soggettiWithServer(String nomePdD) throws DriverControlStationException {
		Connection con = null;
		String nomeMetodo = "soggettiWithServer";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.soggettiWithServer(nomePdD);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public String getNomePddOperativa() throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getNomePddOperativa";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			// Bug: Le porte di dominio in pddList hanno il limit a 20 !!
			//			List<PdDControlStation> list = driver.pddList(null, new org.openspcoop2.web.ctrlstat.core.Search());
//			for (PdDControlStation pddControlStation : list) {
//				if(pddControlStation.getTipo()!=null && PddTipologia.OPERATIVO.toString().equals(pddControlStation.getTipo())){
//					return pddControlStation.getNome();
//				}
//			}
			
			FiltroRicerca filtro = new FiltroRicerca();
			filtro.setTipo(PddTipologia.OPERATIVO.toString());
			try{
				List<String> nomi = driver.getDriverRegistroServiziDB().getAllIdPorteDominio(filtro);
				if(nomi!=null && nomi.size()>0){
					return nomi.get(0); // dovrebbe esisterne solo una
				}
			}catch(DriverRegistroServiziNotFound notFound){}

			throw new Exception("Non e' stata trovata una porta di dominio con tipo '"+PddTipologia.OPERATIVO.toString()+"'");
			
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
}
