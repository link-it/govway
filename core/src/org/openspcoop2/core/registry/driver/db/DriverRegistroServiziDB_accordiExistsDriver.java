/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General protected License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General protected License for more details.
 *
 * You should have received a copy of the GNU General protected License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */



package org.openspcoop2.core.registry.driver.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.registry.constants.ParameterType;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverRegistroServiziDB_accordiExistsDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverRegistroServiziDB_accordiExistsDriver {

	private DriverRegistroServiziDB driver = null;
	
	public DriverRegistroServiziDB_accordiExistsDriver(DriverRegistroServiziDB driver) {
		this.driver = driver;
	}
	
	protected boolean existsAccordoServizioParteComune(IDAccordo idAccordo) throws DriverRegistroServiziException {

		Connection connection;
		if (this.driver.atomica) {
			try {
				connection = this.driver.getConnectionFromDatasource("existsAccordoServizioParteComune(idAccordo)");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::existsAccordoServizioParteComune] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			connection = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);
		try {

			// Check soggetto referente se esiste.
			if(idAccordo.getSoggettoReferente()!=null){
				if(this.driver.existsSoggetto(connection, idAccordo.getSoggettoReferente())==false){
					return false;
				}
			}

			long idAccordoLong = DBUtils.getIdAccordoServizioParteComune(idAccordo, connection, this.driver.tipoDB);
			if (idAccordoLong <= 0)
				return false;
			else
				return true;
		} catch (Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {
			if (this.driver.atomica) {
				try {
					connection.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

	/**
	 * Verifica l'esistenza di un accordo registrato.
	 * 
	 * @param idAccordo
	 *                dell'accordo da verificare
	 * @return true se l'accordo esiste, false altrimenti
	 */
	protected boolean existsAccordoServizioParteComune(long idAccordo) throws DriverRegistroServiziException {

		Connection connection = null;
		this.driver.log.debug("operazione atomica = " + this.driver.atomica);
		PreparedStatement stm=null;
		ResultSet rs=null;
		try {
			if (this.driver.atomica) {
				try {
					connection = this.driver.getConnectionFromDatasource("existsAccordoServizioParteComune(longId)");
					connection.setAutoCommit(false);
				} catch (Exception e) {
					throw new DriverRegistroServiziException("DriverRegistroServiziDB::existsAccordoServizioParteComune] Exception accedendo al datasource :" + e.getMessage(),e);

				}

			} else
				connection = this.driver.globalConnection;
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm=connection.prepareStatement(sqlQuery);
			stm.setLong(1, idAccordo);
			rs=stm.executeQuery();
			if (rs.next())
				return true;
			else
				return false;
		} catch (Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {
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
	}

	/**
	 * Verifica l'esistenza di un'azione in un accordo
	 * 
	 * @param nome
	 *                dell'azione da verificare
	 * @param idAccordo Identificativo dell'accordi di servizio
	 *               
	 * @return true se l'azione esiste, false altrimenti
	 */
	protected boolean existsAccordoServizioParteComuneAzione(String nome, IDAccordo idAccordo) throws DriverRegistroServiziException {

		Connection connection;
		if (this.driver.atomica) {
			try {
				connection = this.driver.getConnectionFromDatasource("existsAccordoServizioParteComuneAzione(nome,idAccordo)");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::existsAccordoServizioParteComuneAzione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			connection = this.driver.globalConnection;

		try {
			return existsAccordoServizioParteComuneAzione(nome, DBUtils.getIdAccordoServizioParteComune(idAccordo, connection, this.driver.tipoDB));
		}catch (Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {
			if (this.driver.atomica) {
				try {
					connection.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}

	}

	/**
	 * Verifica l'esistenza di un'azione in un accordo
	 * 
	 * @param nome
	 *                dell'azione da verificare
	 * @param idAccordo
	 *                dell'accordo
	 * @return true se l'azione esiste, false altrimenti
	 */
	protected boolean existsAccordoServizioParteComuneAzione(String nome, long idAccordo) throws DriverRegistroServiziException {

		boolean exist = false;
		Connection connection;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.driver.atomica) {
			try {
				connection = this.driver.getConnectionFromDatasource("existsAccordoServizioParteComuneAzione(nome,longId)");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::existsAccordoServizioParteComuneAzione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			connection = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_AZIONI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_accordo = ?");
			sqlQueryObject.addWhereCondition("nome = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idAccordo);
			stm.setString(2, nome);
			rs = stm.executeQuery();
			if (rs.next())
				exist = true;
			rs.close();
			stm.close();
		} catch (Exception e) {
			exist = false;
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

		return exist;
	}

	/**
	 * Verifica l'esistenza di un'azione in un accordo
	 * 
	 * @param idAzione dell'azione
	 * @return true se l'azione esiste, false altrimenti
	 */
	protected boolean existsAccordoServizioParteComuneAzione(long idAzione) throws DriverRegistroServiziException {

		boolean exist = false;
		Connection connection;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.driver.atomica) {
			try {
				connection = this.driver.getConnectionFromDatasource("existsAccordoServizioParteComuneAzione(longId)");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::existsAccordoServizioParteComuneAzione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			connection = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_AZIONI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idAzione);
			rs = stm.executeQuery();
			if (rs.next())
				exist = true;
			rs.close();
			stm.close();
		} catch (Exception e) {
			exist = false;
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

		return exist;
	}

	protected boolean existsAccordoServizioParteComunePorttype(String nome, IDAccordo idAccordo) throws DriverRegistroServiziException {
		Connection connection;
		if (this.driver.atomica) {
			try {
				connection = this.driver.getConnectionFromDatasource("existsAccordoServizioParteComunePorttype(nome,idAccordo)");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::existsAccordoServizioParteComunePorttype] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			connection = this.driver.globalConnection;

		try {
			return existsAccordoServizioParteComunePorttype(nome, DBUtils.getIdAccordoServizioParteComune(idAccordo, connection, this.driver.tipoDB));
		}catch (Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {
			if (this.driver.atomica) {
				try {
					connection.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}



	/**
	 * Verifica l'esistenza di un'operation in un accordo
	 * 
	 * @param idAzione dell'azione
	 * @return true se l'azione esiste, false altrimenti
	 */
	protected boolean existsAccordoServizioParteComuneOperation(long idAzione) throws DriverRegistroServiziException {

		boolean exist = false;
		Connection connection;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.driver.atomica) {
			try {
				connection = this.driver.getConnectionFromDatasource("existsAccordoServizioParteComuneOperation(longId)");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::existsAccordoServizioParteComuneOperation] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			connection = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idAzione);
			rs = stm.executeQuery();
			if (rs.next())
				exist = true;
			rs.close();
			stm.close();
		} catch (Exception e) {
			exist = false;
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

		return exist;
	}

	/**
	 * Verifica l'esistenza di un port-type in un accordo
	 * 
	 * @param nome
	 *                del port-type da verificare
	 * @param idAccordo
	 *                dell'accordo
	 * @return true se il port-type esiste, false altrimenti
	 */
	protected boolean existsAccordoServizioParteComunePorttype(String nome, long idAccordo) throws DriverRegistroServiziException {

		boolean exist = false;
		Connection connection;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.driver.atomica) {
			try {
				connection = this.driver.getConnectionFromDatasource("existsAccordoServizioParteComunePorttype(nome,longId)");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::existsAccordoServizioParteComunePorttype] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			connection = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_accordo = ?");
			sqlQueryObject.addWhereCondition("nome = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idAccordo);
			stm.setString(2, nome);
			rs = stm.executeQuery();
			if (rs.next())
				exist = true;
			rs.close();
			stm.close();
		} catch (Exception e) {
			exist = false;
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

		return exist;
	}

	/**
	 * 
	 * @param nome Nome dell'operation
	 * @param idPortType L'identificativo del PortType
	 * @return true se esiste, false altrimenti.
	 * @throws DriverRegistroServiziException
	 */
	protected boolean existsAccordoServizioParteComunePorttypeOperation(String nome, IDPortType idPortType) throws DriverRegistroServiziException {
		Connection connection;
		if (this.driver.atomica) {
			try {
				connection = this.driver.getConnectionFromDatasource("existsAccordoServizioParteComunePorttypeOperation(nome,idPortType)");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::existsAccordoServizioParteComunePorttypeOperation] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			connection = this.driver.globalConnection;

		try {
			long idAccordo = DBUtils.getIdAccordoServizioParteComune(idPortType.getIdAccordo(), connection, this.driver.tipoDB);
			return existsAccordoServizioParteComunePorttypeOperation(nome, DBUtils.getIdPortType(idAccordo,idPortType.getNome(), connection));
		}catch (Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {
			if (this.driver.atomica) {
				try {
					connection.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

	/**
	 * Verifica l'esistenza di un operation in un port-type
	 * 
	 * @param nome
	 *                dell'operation da verificare
	 * @param idPortType
	 *                del port-type
	 * @return true se l'operation esiste, false altrimenti
	 */
	protected boolean existsAccordoServizioParteComunePorttypeOperation(String nome, long idPortType) throws DriverRegistroServiziException {

		boolean exist = false;
		Connection connection;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.driver.atomica) {
			try {
				connection = this.driver.getConnectionFromDatasource("existsAccordoServizioParteComunePorttypeOperation(nome,longId)");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::existsAccordoServizioParteComunePorttypeOperation] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			connection = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_port_type = ?");
			sqlQueryObject.addWhereCondition("nome = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idPortType);
			stm.setString(2, nome);
			rs = stm.executeQuery();
			if (rs.next())
				exist = true;
			rs.close();
			stm.close();
		} catch (Exception e) {
			exist = false;
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

		return exist;
	}
	
	
	/**
	 * Verifica l'esistenza di un risorsa con determinato nome in un accordo
	 * 
	 * @param nome
	 *                del port-type da verificare
	 * @param idAccordo
	 *                dell'accordo
	 * @return true se il port-type esiste, false altrimenti
	 */
	protected boolean existsAccordoServizioParteComuneResource(String nome, long idAccordo) throws DriverRegistroServiziException {

		boolean exist = false;
		Connection connection;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.driver.atomica) {
			try {
				connection = this.driver.getConnectionFromDatasource("existsAccordoServizioParteComuneResource(nome,idAccordo)");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::existsAccordoServizioParteComuneResource] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			connection = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_accordo = ?");
			sqlQueryObject.addWhereCondition("nome = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idAccordo);
			stm.setString(2, nome);
			rs = stm.executeQuery();
			if (rs.next())
				exist = true;
			rs.close();
			stm.close();
		} catch (Exception e) {
			exist = false;
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

		return exist;
	}
	
	protected boolean existsAccordoServizioParteComuneResource(String httpMethod, String path, long idAccordo, String excludeResourceWithName) throws DriverRegistroServiziException {

		boolean exist = false;
		Connection connection;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.driver.atomica) {
			try {
				connection = this.driver.getConnectionFromDatasource("existsAccordoServizioParteComuneResource(httpMethod,path,idAccordo)");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::existsAccordoServizioParteComuneResource] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			connection = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_accordo = ?");
			sqlQueryObject.addWhereCondition("http_method = ?");
			sqlQueryObject.addWhereCondition("path = ?");
			if(excludeResourceWithName!=null && !"".equals(excludeResourceWithName)) {
				sqlQueryObject.addWhereCondition("nome <> ?");
			}
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idAccordo);
			if(httpMethod==null || "".equals(httpMethod)) {
				stm.setString(2, CostantiDB.API_RESOURCE_HTTP_METHOD_ALL_VALUE);
			}
			else {
				stm.setString(2, httpMethod);
			}
			if(path==null || "".equals(path)) {
				stm.setString(3, CostantiDB.API_RESOURCE_PATH_ALL_VALUE);
			}
			else {
				stm.setString(3, path);
			}
			if(excludeResourceWithName!=null && !"".equals(excludeResourceWithName)) {
				stm.setString(4, excludeResourceWithName);
			}
			rs = stm.executeQuery();
			if (rs.next())
				exist = true;
			rs.close();
			stm.close();
		} catch (Exception e) {
			exist = false;
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

		return exist;
	}
	
	/**
	 * Verifica l'esistenza di un response con Status in una Resource
	 * 
	 * @param httpStatus
	 *                 della response da verificare
	 * @param idRisorsa
	 *                della resource
	 * @return true se la risposta esiste, false altrimenti
	 */
	protected boolean existsAccordoServizioResourceResponse(long idRisorsa, int httpStatus) throws DriverRegistroServiziException {

		boolean exist = false;
		Connection connection;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.driver.atomica) {
			try {
				connection = this.driver.getConnectionFromDatasource("existsAccordoServizioResourceResponse(idRisorsa,httpStatus)");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::existsAccordoServizioResourceResponse] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			connection = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES_RESPONSE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_resource = ?");
			sqlQueryObject.addWhereCondition("status = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idRisorsa);
			stm.setInt(2, httpStatus);
			rs = stm.executeQuery();
			if (rs.next())
				exist = true;
			rs.close();
			stm.close();
		} catch (Exception e) {
			exist = false;
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

		return exist;
	}
	

	protected boolean existsAccordoServizioResourceRepresentation(Long idRisorsa, boolean isRequest, Long idResponse, String mediaType) throws DriverRegistroServiziException {

		boolean exist = false;
		Connection connection;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.driver.atomica) {
			try {
				connection = this.driver.getConnectionFromDatasource("existsAccordoServizioResourceRepresentation(idRisorsa,isRequest,idResponse,mediaType)");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::existsAccordoServizioResourceRepresentation] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			connection = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES_MEDIA);
			sqlQueryObject.addSelectField("*");
			if(isRequest)
				sqlQueryObject.addWhereCondition("id_resource_media = ?");
			else 
				sqlQueryObject.addWhereCondition("id_resource_response_media = ?");
			sqlQueryObject.addWhereCondition("media_type = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			if(isRequest)
				stm.setLong(1, idRisorsa);
			else 
				stm.setLong(1, idResponse);
			stm.setString(2, mediaType);
			rs = stm.executeQuery();
			if (rs.next())
				exist = true;
			rs.close();
			stm.close();
		} catch (Exception e) {
			exist = false;
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

		return exist;
	}
	
	protected boolean existsAccordoServizioResourceParameter(Long idRisorsa, boolean isRequest, Long idResponse, ParameterType tipo, String nome) throws DriverRegistroServiziException {

		boolean exist = false;
		Connection connection;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.driver.atomica) {
			try {
				connection = this.driver.getConnectionFromDatasource("existsAccordoServizioResourceRepresentation(idRisorsa,isRequest,idResponse,tipo,nome)");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::existsAccordoServizioResourceRepresentation] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			connection = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES_PARAMETER);
			sqlQueryObject.addSelectField("*");
			if(isRequest)
				sqlQueryObject.addWhereCondition("id_resource_parameter = ?");
			else 
				sqlQueryObject.addWhereCondition("id_resource_response_par = ?");
			sqlQueryObject.addWhereCondition("tipo_parametro = ?");
			sqlQueryObject.addWhereCondition("nome = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			if(isRequest)
				stm.setLong(1, idRisorsa);
			else 
				stm.setLong(1, idResponse);
			stm.setString(2, tipo.toString());
			stm.setString(3, nome);
			rs = stm.executeQuery();
			if (rs.next())
				exist = true;
			rs.close();
			stm.close();
		} catch (Exception e) {
			exist = false;
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

		return exist;
	}
}
