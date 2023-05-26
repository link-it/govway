/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.apc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.core.registry.driver.db.IDAccordoDB;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.UtilitiesSQLQuery;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;

/**
 * AccordoServizioParteComuneServiziCompostiCore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordoServizioParteComuneServiziCompostiCore extends ControlStationCore {

	protected AccordoServizioParteComuneServiziCompostiCore(ControlStationCore core) throws DriverControlStationException {
		super(core);
	}
	
	public List<IDAccordoDB> idAccordiServizioCompostiList(String superuser, ISearch ricerca, 
			boolean soloAccordiConsistentiRest, boolean soloAccordiConsistentiSoap) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "idAccordiServizioCompostiList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().idAccordiServizioCompostiList(superuser, ricerca, 
					soloAccordiConsistentiRest, soloAccordiConsistentiSoap);
		} catch (DriverRegistroServiziException e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<AccordoServizioParteComune> accordiServizioServiziComponentiConSoggettoErogatore(IDSoggetto idSoggetto) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiServizio_serviziComponentiConSoggettoErogatore";
		DriverRegistroServiziDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverRegistroServiziDB(con, ControlStationCore.log, this.tipoDB);

			return driver.accordiServizio_serviziComponentiConSoggettoErogatore(idSoggetto);
		} catch (DriverRegistroServiziException e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<AccordoServizioParteComune> accordiServizioServiziComponenti(IDServizio idServizio) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiServizio_serviziComponenti";
		DriverRegistroServiziDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverRegistroServiziDB(con, ControlStationCore.log, this.tipoDB);

			return driver.accordiServizio_serviziComponenti(idServizio);
		} catch (DriverRegistroServiziException e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<AccordoServizioParteComune> accordiServizioWithAccordoCooperazione(IDAccordoCooperazione idAccordoCooperazione) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiServizioWithAccordoCooperazione";
		DriverRegistroServiziDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverRegistroServiziDB(con, ControlStationCore.log, this.tipoDB);

			return driver.accordiServizioWithAccordoCooperazione(idAccordoCooperazione);
		} catch (DriverRegistroServiziException e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}


	public List<AccordoServizioParteComuneServizioCompostoServizioComponente> accordiComponentiList(long idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiComponentiList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().accordiComponentiList(idAccordo, ricerca);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<String[]> getAccordiServizioCompostoLabels(AccordoServizioParteComune as, long idAccordoLong, String userLogin, 
			List<String> tipiServiziCompatibili, List<String> tipiSoggettiCompatibili, ConsoleHelper helper){
		String[] serviziList = null;
		String[] serviziListLabel = null;
		Connection con = null;
		String nomeMetodo = "getAccordiServizioCompostoLabels";
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		try{

			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(this);

			con = ControlStationCore.dbM.getConnection();

			// Servizi
			ISQLQueryObject sqlQueryObject = (new UtilitiesSQLQuery()).getSQLQueryObject();
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
			sqlQueryObject.addSelectCountField(CostantiDB.SERVIZI + ".id", "tot", true);
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo=?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_accordo<>?");
			if(as.getPrivato()==null || !as.getPrivato()){
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".privato=?");
			}
			sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id="+CostantiDB.SERVIZI+".id_soggetto");
			if( !isVisioneOggettiGlobale(userLogin)){
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
			}
			sqlQueryObject.setANDLogicOperator(true);

			//voglio i servizi che non sono gia' stati usati come componenti
			ISQLQueryObject sqlQueryObjectNotExist = (new UtilitiesSQLQuery()).getSQLQueryObject();
			sqlQueryObjectNotExist.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPONENTI);
			sqlQueryObjectNotExist.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_composto="+CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id");
			sqlQueryObjectNotExist.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_componente="+CostantiDB.SERVIZI+".id");
			sqlQueryObjectNotExist.setANDLogicOperator(true);

			sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectNotExist);

			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordoLong);
			stmt.setLong(2, idAccordoLong);
			int index = 3;
			if(as.getPrivato()==null || !as.getPrivato()){
				stmt.setInt(index, 0);
				index++;
			}
			if( !isVisioneOggettiGlobale(userLogin)){
				stmt.setString(index, userLogin);
				index++;
			}
			risultato = stmt.executeQuery();
			int totServ = 0;
			if (risultato.next()) {
				totServ = risultato.getInt("tot");
			}
			risultato.close();
			stmt.close();

			List<String> serviziL = new ArrayList<>();
			List<String> serviziLabelL = new ArrayList<>();
			serviziL.add("-1");
			serviziLabelL.add("-");

			if (totServ != 0) {
				// Servizi
				sqlQueryObject = (new UtilitiesSQLQuery()).getSQLQueryObject();					
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI, "id","idServizio");
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_accordo<>?");
				if(as.getPrivato()==null || !as.getPrivato()){
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".privato=?");
				}
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id="+CostantiDB.SERVIZI+".id_soggetto");
				if( !isVisioneOggettiGlobale(userLogin)){
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				}
				sqlQueryObject.setANDLogicOperator(true);

				//voglio i servizi che non sono gia' stati usati come componenti
				sqlQueryObjectNotExist = (new UtilitiesSQLQuery()).getSQLQueryObject();
				sqlQueryObjectNotExist.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPONENTI);
				sqlQueryObjectNotExist.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_composto="+CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id");
				sqlQueryObjectNotExist.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_componente="+CostantiDB.SERVIZI+".id");
				sqlQueryObjectNotExist.setANDLogicOperator(true);

				sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectNotExist);

				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setLong(1, idAccordoLong);
				stmt.setLong(2, idAccordoLong);
				index = 3;
				if(as.getPrivato()==null || !as.getPrivato()){
					stmt.setInt(index, 0);
					index++;
				}
				if( !isVisioneOggettiGlobale(userLogin)){
					stmt.setString(index, userLogin);
					index++;
				}
				risultato = stmt.executeQuery();
				while (risultato.next()) {
					long idServizio = risultato.getLong("idServizio");
					AccordoServizioParteSpecifica asps =  apsCore.getAccordoServizioParteSpecifica(idServizio);
					
					if(tipiServiziCompatibili.contains(asps.getTipo()) && tipiSoggettiCompatibili.contains(asps.getTipoSoggettoErogatore())){
						serviziL.add(""+idServizio);
						serviziLabelL.add(helper.getLabelIdServizio(asps));
					}
				}
				risultato.close();
				stmt.close();

				serviziList = serviziL.toArray(new String[1]);
				serviziListLabel = serviziLabelL.toArray(new String[1]);
			}
		}catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
		} finally{
			try {
				if(risultato!=null) {
					risultato.close();
				}
			}catch(Exception t) {
				// ignore
			}
			try {
				if(stmt!=null) {
					stmt.close();
				}
			}catch(Exception t) {
				// ignore
			}
			ControlStationCore.dbM.releaseConnection(con);
		}

		List<String[]> toRet = new ArrayList<>();
		toRet.add(serviziList);
		toRet.add(serviziListLabel);

		return toRet;
	}
}
