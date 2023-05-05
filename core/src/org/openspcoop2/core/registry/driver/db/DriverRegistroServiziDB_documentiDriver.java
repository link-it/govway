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
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.constants.ProprietariDocumento;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.TipiDocumentoLivelloServizio;
import org.openspcoop2.core.registry.constants.TipiDocumentoSemiformale;
import org.openspcoop2.core.registry.constants.TipiDocumentoSicurezza;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverRegistroServiziDB_scopeDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverRegistroServiziDB_documentiDriver {

	private DriverRegistroServiziDB driver = null;
	
	protected DriverRegistroServiziDB_documentiDriver(DriverRegistroServiziDB driver) {
		this.driver = driver;
	}
	
	protected boolean existsDocumento(String nome, String tipo, String ruolo, long idProprietario, ProprietariDocumento proprietarioDocumento) throws DriverRegistroServiziException {

		boolean exist = false;
		Connection connection;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.driver.atomica) {
			try {
				connection = this.driver.getConnectionFromDatasource("existsDocumento");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::existsDocumento] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			connection = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_proprietario = ?");
			sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
			sqlQueryObject.addWhereCondition("nome = ?");
			if(tipo!=null) {
				sqlQueryObject.addWhereCondition("tipo = ?");
			}
			if(ruolo!=null) {
				sqlQueryObject.addWhereCondition("ruolo = ?");
			}
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			int index = 1;
			stm.setLong(index++, idProprietario);
			stm.setString(index++, proprietarioDocumento.toString());
			stm.setString(index++, nome);
			if(tipo!=null) {
				stm.setString(index++, tipo);
			}
			if(ruolo!=null) {
				stm.setString(index++, ruolo);
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
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(connection);
		}

		return exist;
	}

	protected Documento getDocumento(String nome, String tipo, String ruolo, long idProprietario,boolean readBytes,ProprietariDocumento tipoProprietario) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		String nomeMetodo = "getDocumento";

		Connection con = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getDocumento");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(), e);

			}

		} else {
			con = this.driver.globalConnection;
		}

		try {
			String tipoProprietarioAsString = null;
			if(tipoProprietario!=null){
				tipoProprietarioAsString = tipoProprietario.toString();
			}
			long idDoc = DBUtils.getIdDocumento(nome, tipo, ruolo, idProprietario,con,this.driver.tipoDB,tipoProprietarioAsString);
			if(idDoc <= 0 ) {
				throw new DriverRegistroServiziNotFound("Documento richiesto non esistente (nome:"+nome+", tipo:"+tipo+", ruolo:"+ruolo+", idProprietario:"+idProprietario+", tipoProprietario:"+tipoProprietarioAsString+")");
			}
			return DriverRegistroServiziDB_documentiLIB.getDocumento(idDoc, readBytes, con, this.driver.tipoDB);

		} 
		catch (DriverRegistroServiziNotFound se) {
			throw se;
		}
		catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziException::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			this.driver.closeConnection(con);
		}
	}

	protected Documento getDocumento(long idDocumento,boolean readBytes) throws DriverRegistroServiziException {
		String nomeMetodo = "getDocumento";

		Connection con = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getDocumento");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(), e);

			}

		} else {
			con = this.driver.globalConnection;
		}

		try {

			return DriverRegistroServiziDB_documentiLIB.getDocumento(idDocumento, readBytes, con, this.driver.tipoDB);

		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziException::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			this.driver.closeConnection(con);
		}
	}

	protected List<Documento> serviziAllegatiList(long idServizio, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "serviziAllegatiList";
		int idLista = Liste.SERVIZI_ALLEGATI;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		ricerca.getSearchString(idLista);

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<Documento> lista = new ArrayList<Documento>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("serviziAllegatiList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_proprietario = ?");
				sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
				sqlQueryObject.addWhereCondition(false, 
						//sqlQueryObject.getWhereLikeCondition("ruolo",search,true,true),
						sqlQueryObject.getWhereLikeCondition("nome",search,true,true));
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_proprietario = ?");
				sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}

			stmt = con.prepareStatement(queryString);
			stmt.setLong(1,idServizio);
			stmt.setString(2,ProprietariDocumento.servizio.toString());
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt("cont"));
			risultato.close();
			stmt.close();

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("ruolo");
			sqlQueryObject.addSelectField("id_proprietario");
			sqlQueryObject.addSelectField("tipo_proprietario");
			//where
			sqlQueryObject.addWhereCondition("id_proprietario = ?");
			sqlQueryObject.addWhereCondition("tipo_proprietario = ?");

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;

			if (!search.equals("")) { // con search
				sqlQueryObject.addWhereCondition(false, 
						//sqlQueryObject.getWhereLikeCondition("ruolo",search,true,true),
						sqlQueryObject.getWhereLikeCondition("nome",search,true,true));
			} 

			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idServizio);
			stmt.setString(2,ProprietariDocumento.servizio.toString());
			risultato = stmt.executeQuery();

			while(risultato.next()){
				Documento doc = DriverRegistroServiziDB_documentiLIB.getDocumento(risultato.getLong("id"),false, con, this.driver.tipoDB); 
				lista.add(doc);
			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(con);
		}
	}

	protected Documento getAllegato(IDServizio idASPS, String nome)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return getEngineDocumento(idASPS, RuoliDocumento.allegato.toString(), null, nome);
	}
	protected Documento getSpecificaSemiformale(IDServizio idASPS, TipiDocumentoSemiformale tipo, String nome)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return getEngineDocumento(idASPS, RuoliDocumento.specificaSemiformale.toString(), tipo.toString(), nome);
	}
	protected Documento getSpecificaSicurezza(IDServizio idASPS, TipiDocumentoSicurezza tipo, String nome)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return getEngineDocumento(idASPS, RuoliDocumento.specificaSicurezza.toString(), tipo.toString(), nome);
	}
	protected Documento getSpecificaLivelloServizio(IDServizio idASPS, TipiDocumentoLivelloServizio tipo, String nome)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return getEngineDocumento(idASPS, RuoliDocumento.specificaLivelloServizio.toString(), tipo.toString(), nome);
	}
	private Documento getEngineDocumento(IDServizio idASPS, String ruolo, String tipo, String nome) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		String nomeMetodo = "getDocumento";
		String queryString;
		
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("serviziAllegatiList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		try {

			long idServizio = DBUtils.getIdAccordoServizioParteSpecifica(idASPS, con, this.driver.tipoDB);
			if(idServizio<=0) {
				throw new DriverRegistroServiziNotFound("ApiImpl '"+idASPS+"' undefined");
			}	

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("id_proprietario = ?");
			sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
			sqlQueryObject.addWhereCondition("ruolo = ?");
			if(tipo!=null) {
				sqlQueryObject.addWhereCondition("tipo = ?");
			}
			sqlQueryObject.addWhereCondition("nome = ?");
			
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);
			int index = 1;
			stmt.setLong(index++, idServizio);
			stmt.setString(index++, ProprietariDocumento.servizio.toString());
			stmt.setString(index++, ruolo);
			if(tipo!=null) {
				stmt.setString(index++, tipo);
			}
			stmt.setString(index++, nome);
			risultato = stmt.executeQuery();

			if(risultato.next()){
				Documento doc = DriverRegistroServiziDB_documentiLIB.getDocumento(risultato.getLong("id"),true, con, this.driver.tipoDB); 
				return doc;
			}

			String tipoS = (tipo!=null) ? ("tipo:"+tipo+" ") : "";
			throw new DriverRegistroServiziNotFound("Documento (ruolo:"+ruolo+" "+tipoS+"nome:"+nome+") non trovato");

		} catch(DriverRegistroServiziNotFound notFound) {
			throw notFound;
		}
		catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(con);
		}
	}

	
	protected Documento getAllegato(IDAccordo idAccordo, String nome)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return getEngineDocumento(idAccordo, RuoliDocumento.allegato.toString(), null, nome);
	}
	protected Documento getSpecificaSemiformale(IDAccordo idAccordo, TipiDocumentoSemiformale tipo, String nome)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return getEngineDocumento(idAccordo, RuoliDocumento.specificaSemiformale.toString(), tipo.toString(), nome);
	}
	private Documento getEngineDocumento(IDAccordo idAccordo, String ruolo, String tipo, String nome) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		String nomeMetodo = "getDocumento";
		String queryString;
		
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("serviziAllegatiList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		try {

			long idAccordoLong = DBUtils.getIdAccordoServizioParteComune(idAccordo, con, this.driver.tipoDB);
			if(idAccordoLong<=0) {
				throw new DriverRegistroServiziNotFound("Api '"+idAccordo+"' undefined");
			}	

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("id_proprietario = ?");
			sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
			sqlQueryObject.addWhereCondition("ruolo = ?");
			if(tipo!=null) {
				sqlQueryObject.addWhereCondition("tipo = ?");
			}
			sqlQueryObject.addWhereCondition("nome = ?");
			
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);
			int index = 1;
			stmt.setLong(index++, idAccordoLong);
			stmt.setString(index++, ProprietariDocumento.accordoServizio.toString());
			stmt.setString(index++, ruolo);
			if(tipo!=null) {
				stmt.setString(index++, tipo);
			}
			stmt.setString(index++, nome);
			risultato = stmt.executeQuery();

			if(risultato.next()){
				Documento doc = DriverRegistroServiziDB_documentiLIB.getDocumento(risultato.getLong("id"),true, con, this.driver.tipoDB); 
				return doc;
			}

			String tipoS = (tipo!=null) ? ("tipo:"+tipo+" ") : "";
			throw new DriverRegistroServiziNotFound("Documento (ruolo:"+ruolo+" "+tipoS+"nome:"+nome+") non trovato");

		} catch(DriverRegistroServiziNotFound notFound) {
			throw notFound;
		}
		catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(con);
		}
	}
}
