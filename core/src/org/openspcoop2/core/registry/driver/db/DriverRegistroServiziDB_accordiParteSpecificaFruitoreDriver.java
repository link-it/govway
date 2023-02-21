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



package org.openspcoop2.core.registry.driver.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.CredenzialeTipo;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverRegistroServiziDB_accordiParteSpecificaFruitoreDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverRegistroServiziDB_accordiParteSpecificaFruitoreDriver {

	private DriverRegistroServiziDB driver = null;
	
	public DriverRegistroServiziDB_accordiParteSpecificaFruitoreDriver(DriverRegistroServiziDB driver) {
		this.driver = driver;
	}
	
	

	protected long getServizioFruitore(IDServizio idServizio, long idSogg) throws DriverRegistroServiziException {

		long idFru = 0;
		Connection connection;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.driver.atomica) {
			try {
				connection = this.driver.getConnectionFromDatasource("getServizioFruitore");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::getServizioWithSoggettoAccordoServCorr] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			connection = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);
		try {
			long idServ = 0;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".nome_servizio = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".tipo_servizio = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".versione_servizio = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setString(1, idServizio.getNome());
			stm.setString(2, idServizio.getTipo());
			stm.setInt(3, idServizio.getVersione());
			stm.setString(4, idServizio.getSoggettoErogatore().getNome());
			stm.setString(5, idServizio.getSoggettoErogatore().getTipo());
			rs = stm.executeQuery();
			if (rs.next())
				idServ = rs.getLong("id");
			rs.close();
			stm.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_servizio = ?");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idServ);
			stm.setLong(2, idSogg);
			rs = stm.executeQuery();
			if (rs.next())
				idFru = rs.getLong("id");
			rs.close();
			stm.close();
		} catch (Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) 
					rs.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stm!=null) 
					stm.close();
			}catch (Exception e) {
				//ignore
			}

			if (this.driver.atomica) {
				try {
					connection.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}

		return idFru;
	}

	protected long getServizioFruitoreSoggettoFruitoreID(long idServizioFruitore) throws DriverRegistroServiziException {

		long idSoggFru = 0;
		Connection connection;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.driver.atomica) {
			try {
				connection = this.driver.getConnectionFromDatasource("getServizioFruitoreSoggettoFruitoreID");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::getServizioFruitoreSoggettoFruitoreID] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			connection = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.addSelectField("id_soggetto");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idServizioFruitore);
			rs = stm.executeQuery();
			if (rs.next())
				idSoggFru = rs.getLong("id_soggetto");
			rs.close();
			stm.close();
		} catch (Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) 
					rs.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stm!=null) 
					stm.close();
			}catch (Exception e) {
				//ignore
			}

			if (this.driver.atomica) {
				try {
					connection.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}

		return idSoggFru;
	}

	protected long getServizioFruitoreServizioID(long idServizioFruitore) throws DriverRegistroServiziException {

		long idServ = 0;
		Connection connection;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.driver.atomica) {
			try {
				connection = this.driver.getConnectionFromDatasource("getServizioFruitoreServizioID");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::getServizioFruitoreServizioID] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			connection = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.addSelectField("id_servizio");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idServizioFruitore);
			rs = stm.executeQuery();
			if (rs.next())
				idServ = rs.getLong("id_servizio");
			rs.close();
			stm.close();
		} catch (Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) 
					rs.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stm!=null) 
					stm.close();
			}catch (Exception e) {
				//ignore
			}

			if (this.driver.atomica) {
				try {
					connection.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}

		return idServ;
	}

	
	protected boolean existFruizioniServiziSoggettoWithoutConnettore(long idSoggetto, boolean escludiSoggettiEsterni) throws DriverRegistroServiziException {
		if (idSoggetto <= 0)
			throw new DriverRegistroServiziException("idSoggetto non valido.");

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		boolean trovatoServ = false;
		boolean error = false;

		try {
			if (this.driver.atomica) {
				try {
					con = this.driver.getConnectionFromDatasource("existFruizioniWithoutConnettore");
					con.setAutoCommit(false);
				} catch (Exception e) {
					throw new DriverRegistroServiziException("Exception accedendo al datasource :" + e.getMessage(), e);

				}

			} else
				con = this.driver.globalConnection;

			this.driver.log.debug("operazione atomica = " + this.driver.atomica);
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			if(escludiSoggettiEsterni){
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addFromTable(CostantiDB.PDD);
			}
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI, "cservizio");
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI, "cfruizione");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI + ".id_soggetto = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI + ".id_connettore = cservizio.id");
			sqlQueryObject.addWhereCondition("cservizio.endpointtype = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_servizio = "+CostantiDB.SERVIZI+".id");
			if(escludiSoggettiEsterni){
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				sqlQueryObject.addWhereIsNotNullCondition(CostantiDB.SOGGETTI + ".server");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI + ".server = "+CostantiDB.PDD+".nome");
				sqlQueryObject.addWhereCondition(false,CostantiDB.PDD + ".tipo = ?",CostantiDB.PDD + ".tipo = ?");
			}
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_connettore = cfruizione.id");
			sqlQueryObject.addWhereCondition("cfruizione.endpointtype = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			int index = 1;
			stm.setLong(index++, idSoggetto);
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(CostantiRegistroServizi.DISABILITATO));
			if(escludiSoggettiEsterni){
				stm.setString(index++, PddTipologia.OPERATIVO.toString());
				stm.setString(index++, PddTipologia.NONOPERATIVO.toString());
			}
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(CostantiRegistroServizi.DISABILITATO));
			rs = stm.executeQuery();
			if (rs.next())
				trovatoServ = true;

			return trovatoServ;
		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("Errore durante existFruizioniWithoutConnettore: " + qe.getMessage(), qe);
		} finally {

			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (stm != null) {
					stm.close();
				}
			} catch (Exception e) {
				// ignore
			}
			
			try {
				if (error && this.driver.atomica) {
					this.driver.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.driver.atomica) {
					this.driver.log.debug("eseguo commit e rilascio connessioni...");
					con.commit();
					con.setAutoCommit(true);
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	protected boolean existFruizioniServizioWithoutConnettore(long idServizio, boolean escludiSoggettiEsterni) throws DriverRegistroServiziException {
		if (idServizio <= 0)
			throw new DriverRegistroServiziException("idServizio non valido.");

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		boolean trovatoServ = false;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("existFruizioniServizioWithoutConnettore");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("Exception accedendo al datasource :" + e.getMessage(), e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			if(escludiSoggettiEsterni){
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addFromTable(CostantiDB.PDD);
			}
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI, "cfruizione");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI + ".id = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_servizio = "+CostantiDB.SERVIZI+".id");
			if(escludiSoggettiEsterni){
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				sqlQueryObject.addWhereIsNotNullCondition(CostantiDB.SOGGETTI + ".server");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI + ".server = "+CostantiDB.PDD+".nome");
				sqlQueryObject.addWhereCondition(false,CostantiDB.PDD + ".tipo = ?",CostantiDB.PDD + ".tipo = ?");
			}
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_connettore = cfruizione.id");
			sqlQueryObject.addWhereCondition("cfruizione.endpointtype = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			int index = 1;
			stm.setLong(index++, idServizio);
			if(escludiSoggettiEsterni){
				stm.setString(index++, PddTipologia.OPERATIVO.toString());
				stm.setString(index++, PddTipologia.NONOPERATIVO.toString());
			}
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(CostantiRegistroServizi.DISABILITATO));
			rs = stm.executeQuery();
			if (rs.next())
				trovatoServ = true;

			return trovatoServ;
		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("Errore durante existFruizioniServizioWithoutConnettore: " + qe.getMessage(), qe);
		} finally {

			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (stm != null) {
					stm.close();
				}
			} catch (Exception e) {
				// ignore
			}
			
			try {
				if (error && this.driver.atomica) {
					this.driver.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.driver.atomica) {
					this.driver.log.debug("eseguo commit e rilascio connessioni...");
					con.commit();
					con.setAutoCommit(true);
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	
	protected List<AccordoServizioParteSpecifica> servizioWithSoggettoFruitore(IDSoggetto idSoggetto) throws DriverRegistroServiziException {
		String nomeMetodo = "servizioWithSoggettoFruitore";

		String queryString;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<AccordoServizioParteSpecifica> idServizi = new ArrayList<AccordoServizioParteSpecifica>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("servizioWithSoggettoFruitore");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			ISQLQueryObject sqlQueryObjectSoggetti = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObjectSoggetti.setSelectDistinct(true);
			sqlQueryObjectSoggetti.addSelectAliasField(CostantiDB.SERVIZI, "id","idServizio");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_servizio="+CostantiDB.SERVIZI+".id");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto=?");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto=?");
			sqlQueryObjectSoggetti.setANDLogicOperator(true);
			queryString = sqlQueryObjectSoggetti.createSQLQuery();

			this.driver.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(queryString));

			stmt = con.prepareStatement(queryString);
			stmt.setString(1,idSoggetto.getTipo());
			stmt.setString(2,idSoggetto.getNome());
			risultato = stmt.executeQuery();

			while (risultato.next()) {

				long idServizioLong = risultato.getLong("idServizio");
				AccordoServizioParteSpecifica serv = this.driver.getAccordoServizioParteSpecifica(idServizioLong);
				Soggetto s = this.driver.getSoggetto(serv.getIdSoggetto());
				serv.setTipoSoggettoErogatore(s.getTipo());
				serv.setNomeSoggettoErogatore(s.getNome());

				idServizi.add(serv);

			}

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) 
					risultato.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stmt!=null) 
					stmt.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (this.driver.atomica) {
					this.driver.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}


		return idServizi;
	}
	
	

	protected Fruitore getAccordoErogatoreFruitore(long id) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		this.driver.log.debug("richiesto getAccordoErogatoreFruitore: " + id);
		// conrollo consistenza
		if (id <= 0)
			return null;

		org.openspcoop2.core.registry.Fruitore fruitore = null;
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();

			this.driver.log.debug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("getAccordoErogatoreFruitore");
			else
				con = this.driver.globalConnection;

			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, id);

			this.driver.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, id));
			rs = stm.executeQuery();

			if (rs.next()) {
				fruitore = new org.openspcoop2.core.registry.Fruitore();

				fruitore.setId(id);
				fruitore.setTipo(rs.getString(CostantiDB.SOGGETTI + ".tipo_soggetto"));
				fruitore.setNome(rs.getString(CostantiDB.SOGGETTI + ".nome_soggetto"));
				long idConnettore = rs.getLong(CostantiDB.SOGGETTI + "id_connettore");
				fruitore.setConnettore(this.driver.getConnettore(idConnettore, con));
			} else {
				throw new DriverRegistroServiziNotFound("[DriverRegistroServiziDB::getAccordoErogatoreFruitore] rs.next non ha restituito valori con la seguente interrogazione :\n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, id));
			}

			return fruitore;

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getAccordoErogatoreFruitore] SQLException :" + se.getMessage(),se);
		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getAccordoErogatoreFruitore] Exception :" + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) 
					rs.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stm!=null) 
					stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {

				if (this.driver.atomica) {
					this.driver.log.debug("rilascio connessione al db...");
					if(con!=null) {
						con.close();
					}
				}

			} catch (Exception e) {
				// ignore
			}

		}
	}

	protected Fruitore getErogatoreFruitore(long id) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		this.driver.log.debug("richiesto getErogatoreFruitore: " + id);
		// conrollo consistenza
		if (id <= 0)
			return null;

		org.openspcoop2.core.registry.Fruitore fruitore = null;
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".tipo_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".nome_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".id_connettore");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI+".stato");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();

			this.driver.log.debug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("getErogatoreFruitore");
			else
				con = this.driver.globalConnection;

			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, id);

			this.driver.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, id));
			rs = stm.executeQuery();

			if (rs.next()) {
				fruitore = new org.openspcoop2.core.registry.Fruitore();

				fruitore.setId(id);
				fruitore.setTipo(rs.getString("tipo_soggetto"));
				fruitore.setNome(rs.getString("nome_soggetto"));
				fruitore.setStatoPackage(rs.getString("stato"));
				long idConnettore = rs.getLong("id_connettore");
				fruitore.setConnettore(this.driver.getConnettore(idConnettore, con));
			} else {
				throw new DriverRegistroServiziNotFound("[DriverRegistroServiziDB::getErogatoreFruitore] rs.next non ha restituito valori con la seguente interrogazione :\n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, id));
			}

			return fruitore;

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getErogatoreFruitore] SQLException :" + se.getMessage(),se);
		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getErogatoreFruitore] Exception :" + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) 
					rs.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stm!=null) 
					stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {

				if (this.driver.atomica) {
					this.driver.log.debug("rilascio connessione al db...");
					if(con!=null) {
						con.close();
					}
				}

			} catch (Exception e) {
				// ignore
			}

		}
	}
	
	protected Fruitore getServizioFruitore(long idServFru) throws DriverRegistroServiziException {
		String nomeMetodo = "getServizioFruitore";
		String queryString;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		//ArrayList<Fruitore> lista = new ArrayList<Fruitore>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getServizioFruitore");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.atomica = " + this.driver.atomica);

		try {
			// ricavo le entries
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI+".id");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI+".id_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI+".id_servizio");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI+".id_connettore");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI+".wsdl_implementativo_erogatore");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI+".wsdl_implementativo_fruitore");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI+".stato");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".nome_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".tipo_soggetto");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id = "+CostantiDB.SERVIZI_FRUITORI+".id_soggetto");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idServFru);
			risultato = stmt.executeQuery();

			Fruitore f = null;
			if (risultato.next()) {

				f = new Fruitore();

				f.setId(risultato.getLong("id"));
				f.setIdSoggetto(risultato.getLong("id_soggetto"));
				f.setIdServizio(risultato.getLong("id_servizio"));
				f.setNome(risultato.getString("nome_soggetto"));
				f.setTipo(risultato.getString("tipo_soggetto"));
				long idConnettore = risultato.getLong("id_connettore");
				f.setConnettore(this.driver.getConnettore(idConnettore, con));
				f.setByteWsdlImplementativoErogatore(risultato.getString("wsdl_implementativo_erogatore")!=null && !risultato.getString("wsdl_implementativo_erogatore").trim().equals("") ? risultato.getString("wsdl_implementativo_erogatore").trim().getBytes() : null);
				f.setByteWsdlImplementativoFruitore(risultato.getString("wsdl_implementativo_fruitore")!=null && !risultato.getString("wsdl_implementativo_fruitore").trim().equals("") ? risultato.getString("wsdl_implementativo_fruitore").trim().getBytes() : null);
				f.setStatoPackage(risultato.getString("stato"));
				
				// Protocol Properties
				try{
					List<ProtocolProperty> listPP = DriverRegistroServiziDB_LIB.getListaProtocolProperty(f.getId(), ProprietariProtocolProperty.FRUITORE, con, this.driver.tipoDB);
					if(listPP!=null && listPP.size()>0){
						for (ProtocolProperty protocolProperty : listPP) {
							f.addProtocolProperty(protocolProperty);
						}
					}
				}catch(DriverRegistroServiziNotFound dNotFound){}
			}

			return f;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) 
					risultato.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stmt!=null) 
					stmt.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.driver.atomica) {
					this.driver.log.debug("rilascio connessioni al db...");
					if(con!=null) {
						con.close();
					}
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	
	protected List<Fruitore> serviziFruitoriList(long idServizi, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "serviziFruitoriList";
		int idLista = Liste.SERVIZI_FRUITORI;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		String filterStatoAccordo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_STATO_ACCORDO);

		this.driver.log.debug("search : " + search);
		this.driver.log.debug("filterStatoAccordo : " + filterStatoAccordo);
		

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<Fruitore> lista = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("serviziFruitoriList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else {
			con = this.driver.globalConnection;
		}

		this.driver.log.debug("operazione this.atomica = " + this.driver.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_servizio = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_soggetto = " + CostantiDB.SOGGETTI + ".id");
				sqlQueryObject.addWhereCondition(false, 
						sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI + ".nome_soggetto",search,true,true)); 
						//sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI + ".tipo_soggetto",search,true,true));
				if(filterStatoAccordo!=null && !filterStatoAccordo.equals("")) {
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".stato = ?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_servizio = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_soggetto = " + CostantiDB.SOGGETTI + ".id");
				if(filterStatoAccordo!=null && !filterStatoAccordo.equals("")) {
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".stato = ?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} 
			stmt = con.prepareStatement(queryString);
			int index = 1;
			stmt.setLong(index++, idServizi);
			if(filterStatoAccordo!=null && !filterStatoAccordo.equals("")) {
				stmt.setString(index++, filterStatoAccordo);
			}
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			}
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) {
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			}
			if (!search.equals("")) {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_FRUITORI,"id","idFruitore");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_FRUITORI,"stato","statoFruitore");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".tipo_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".nome_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI + ".id_servizio");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI + ".id_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".id");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_servizio = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_soggetto = " + CostantiDB.SOGGETTI + ".id");
				sqlQueryObject.addWhereCondition(false, 
						sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI + ".nome_soggetto",search,true,true)); 
						//sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI + ".tipo_soggetto",search,true,true));
				if(filterStatoAccordo!=null && !filterStatoAccordo.equals("")) {
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".stato = ?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI + ".tipo_soggetto");
				sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI + ".nome_soggetto");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_FRUITORI,"id","idFruitore");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_FRUITORI,"stato","statoFruitore");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".tipo_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".nome_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI + ".id_servizio");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI + ".id_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".id");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_servizio = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_soggetto = " + CostantiDB.SOGGETTI + ".id");
				if(filterStatoAccordo!=null && !filterStatoAccordo.equals("")) {
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".stato = ?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI + ".tipo_soggetto");
				sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI + ".nome_soggetto");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			index = 1;
			stmt.setLong(index++, idServizi);
			if(filterStatoAccordo!=null && !filterStatoAccordo.equals("")) {
				stmt.setString(index++, filterStatoAccordo);
			}
			risultato = stmt.executeQuery();

			Fruitore f;
			lista = new ArrayList<Fruitore>();
			while (risultato.next()) {
				f = new Fruitore();

				f.setId(risultato.getLong("idFruitore"));
				f.setNome(risultato.getString("nome_soggetto"));
				f.setTipo(risultato.getString("tipo_soggetto"));
				f.setIdSoggetto(risultato.getLong("id_soggetto"));
				f.setIdServizio(risultato.getLong("id_servizio"));
				f.setStatoPackage(risultato.getString("statoFruitore"));

				lista.add(f);
			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) 
					risultato.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stmt!=null) 
					stmt.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.driver.atomica) {
					this.driver.log.debug("rilascio connessioni al db...");
					if(con!=null) {
						con.close();
					}
				}
			} catch (Exception e) {
				// ignore exception
			}
		}

	}
	
	protected long getIdServizioFruitore(long idServizio, long idSoggetto) throws DriverRegistroServiziException {
		long idFru = 0;
		Connection connection;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.driver.atomica) {
			try {
				connection = this.driver.getConnectionFromDatasource("getIdServizioFruitore");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::getIdServizioFruitore] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			connection = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI + ".id");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_servizio = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idServizio);
			stm.setLong(2, idSoggetto);
			rs = stm.executeQuery();
			if (rs.next())
				idFru = rs.getLong("id");
			rs.close();
			stm.close();
		} catch (Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) 
					rs.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stm!=null) 
					stm.close();
			}catch (Exception e) {
				//ignore
			}

			if (this.driver.atomica) {
				try {
					if(connection!=null) {
						connection.close();
					}
				} catch (Exception e) {
					// ignore
				}
			}
		}

		return idFru;
	}

	protected List<Fruitore> getServiziFruitoriWithServizio(long idServizio) throws DriverRegistroServiziException {
		String nomeMetodo = "getServiziFruitoriWithServizio";
		String queryString;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<Fruitore> lista = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getServiziFruitoriWithServizio");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else {
			con = this.driver.globalConnection;
		}

		this.driver.log.debug("operazione this.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".tipo_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".nome_soggetto");
			sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI + ".id = " + CostantiDB.SERVIZI_FRUITORI + ".id_soggetto");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_servizio = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idServizio);
			risultato = stmt.executeQuery();

			Fruitore f;
			lista = new ArrayList<Fruitore>();
			while (risultato.next()) {

				f = new Fruitore();

				f.setNome(risultato.getString("nome_soggetto"));
				f.setTipo(risultato.getString("tipo_soggetto"));

				lista.add(f);
			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) 
					risultato.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stmt!=null) 
					stmt.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.driver.atomica) {
					this.driver.log.debug("rilascio connessioni al db...");
					if(con!=null) {
						con.close();
					}
				}
			} catch (Exception e) {
				// ignore exception
			}
		}

	}
	
	protected List<AccordoServizioParteSpecifica> getServiziByFruitore(Fruitore fruitore) throws DriverRegistroServiziException {
		String nomeMetodo = "getServiziByFruitore";
		String queryString;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<AccordoServizioParteSpecifica> lista = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getServiziByFruitore");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else {
			con = this.driver.globalConnection;
		}

		this.driver.log.debug("operazione this.atomica = " + this.driver.atomica);

		try {

			long idFruitore = DBUtils.getIdSoggetto(fruitore.getNome(), fruitore.getTipo(), con, this.driver.tipoDB);

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idFruitore);
			risultato = stmt.executeQuery();

			lista = new ArrayList<AccordoServizioParteSpecifica>();
			while (risultato.next()) {
				long id=risultato.getLong("id");
				AccordoServizioParteSpecifica se=this.driver.getAccordoServizioParteSpecifica(id, con);
				lista.add(se);
			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) 
					risultato.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stmt!=null) 
					stmt.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.driver.atomica) {
					this.driver.log.debug("rilascio connessioni al db...");
					if(con!=null) {
						con.close();
					}
				}
			} catch (Exception e) {
				// ignore exception
			}
		}

	}

	protected List<Fruitore> getSoggettiWithServizioNotFruitori(long idServizio, boolean escludiSoggettiEsterni, CredenzialeTipo credenzialeTipo) throws DriverRegistroServiziException {
		String nomeMetodo = "getSoggettiWithServizioNotFruitori";
		String queryString;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<Fruitore> lista = new ArrayList<Fruitore>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getSoggettiWithServizioNotFruitori");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else {
			con = this.driver.globalConnection;
		}

		this.driver.log.debug("operazione this.atomica = " + this.driver.atomica);

		try {

			// Condizione where not
			ISQLQueryObject sqlQueryObjectWhere = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObjectWhere.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObjectWhere.addSelectField("*");
			sqlQueryObjectWhere.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_soggetto = " + CostantiDB.SOGGETTI + ".id");
			sqlQueryObjectWhere.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_servizio = ?");
			sqlQueryObjectWhere.setANDLogicOperator(true);

			// Query
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			if(escludiSoggettiEsterni){
				sqlQueryObject.addFromTable(CostantiDB.PDD);
			}
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".id");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".tipo_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".nome_soggetto");
			if(escludiSoggettiEsterni){
				sqlQueryObject.addWhereIsNotNullCondition(CostantiDB.SOGGETTI + ".server");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI + ".server = "+CostantiDB.PDD+".nome");
				sqlQueryObject.addWhereCondition(false,CostantiDB.PDD + ".tipo = ?",CostantiDB.PDD + ".tipo = ?");
			}
			if(credenzialeTipo!=null){
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI + ".tipoauth = ?");
			}
			sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectWhere);
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI + ".tipo_soggetto");
			sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI + ".nome_soggetto");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int index = 1;
			if(escludiSoggettiEsterni){
				stmt.setString(index++, PddTipologia.OPERATIVO.toString());
				stmt.setString(index++, PddTipologia.NONOPERATIVO.toString());
			}
			if(credenzialeTipo!=null){
				stmt.setString(index++, credenzialeTipo.getValue());
			}
			stmt.setLong(index++, idServizio);
			risultato = stmt.executeQuery();

			Fruitore f;
			while (risultato.next()) {
				f = new Fruitore();

				f.setId(risultato.getLong("id"));
				f.setNome(risultato.getString("nome_soggetto"));
				f.setTipo(risultato.getString("tipo_soggetto"));

				lista.add(f);
			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) 
					risultato.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stmt!=null) 
					stmt.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.driver.atomica) {
					this.driver.log.debug("rilascio connessioni al db...");
					if(con!=null) {
						con.close();
					}
				}
			} catch (Exception e) {
				// ignore exception
			}
		}

	}
	
	protected void validaStatoFruitoreServizio(Fruitore fruitore,AccordoServizioParteSpecifica serv) throws ValidazioneStatoPackageException{

		ValidazioneStatoPackageException erroreValidazione =
				new ValidazioneStatoPackageException("FruitoreServizio",fruitore.getStatoPackage(),null);

		try{

			// Controlli di stato
			if(StatiAccordo.bozza.toString().equals(fruitore.getStatoPackage()) == false){

				String uriAPS = this.driver.idServizioFactory.getUriFromAccordo(serv);
				
				if(StatiAccordo.operativo.toString().equals(fruitore.getStatoPackage())){
					if(StatiAccordo.finale.toString().equals(serv.getStatoPackage())==false && StatiAccordo.operativo.toString().equals(serv.getStatoPackage())==false){
						erroreValidazione.addErroreValidazione("servizio riferito ["+uriAPS+"] possiede lo stato ["+serv.getStatoPackage()+"]");
					}
				}
				
				else if(StatiAccordo.finale.toString().equals(fruitore.getStatoPackage())){

					if(StatiAccordo.finale.toString().equals(serv.getStatoPackage())==false){
						erroreValidazione.addErroreValidazione("servizio ["+uriAPS+"] in uno stato non finale ["+serv.getStatoPackage()+"]");
					}
				}

			}

		}catch(Exception e){
			throw new ValidazioneStatoPackageException(e);
		}

		if(erroreValidazione.sizeErroriValidazione()>0){
			throw erroreValidazione;
		}
	}
}
