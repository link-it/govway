/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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


package org.openspcoop2.web.lib.audit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.web.lib.audit.costanti.Costanti;
import org.openspcoop2.web.lib.audit.costanti.StatoOperazione;
import org.openspcoop2.web.lib.audit.costanti.TipoOperazione;
import org.openspcoop2.web.lib.audit.dao.Appender;
import org.openspcoop2.web.lib.audit.dao.AppenderProperty;
import org.openspcoop2.web.lib.audit.dao.Configurazione;
import org.openspcoop2.web.lib.audit.dao.Filtro;

/**
 * Sono forniti metodi per la lettura dei dati di Users
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class DriverAudit {

	/** Connessione al Database */
	private Connection connectionDB;

	// Tipo database passato al momento della creazione dell'oggetto
	private String tipoDB = null;

	public DriverAudit(Connection con, String tipoDB) throws AuditException {
		this.connectionDB = con;
		if (con == null)
			throw new AuditException("Connessione al Database non definita");

		this.tipoDB = tipoDB;
		if (tipoDB == null)
			throw new AuditException("Il tipoDatabase non puo essere null.");
	}

	
	
	
	public Configurazione getConfigurazione() throws AuditException {
		
		Configurazione configurazione = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(Costanti.DB_AUDIT_CONFIGURAZIONE);
			sqlQueryObject.addSelectField("*");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			rs = stm.executeQuery();
			if (rs.next()) {
				configurazione = new Configurazione();
				if(rs.getInt("audit_engine")==1)
					configurazione.setAuditEngineEnabled(true);
				else
					configurazione.setAuditEngineEnabled(false);
				if(rs.getInt("enabled")==1)
					configurazione.setAuditEnabled(true);
				else
					configurazione.setAuditEnabled(false);
				if(rs.getInt("dump")==1)
					configurazione.setDumpEnabled(true);
				else
					configurazione.setDumpEnabled(false);
				configurazione.setDumpFormat(rs.getString("dump_format"));

				configurazione.setAppender(this.getAppender());
				configurazione.setFiltri(this.getFiltri());		
				
			}else{
				throw new AuditException("[DriverAudit::getConfigurazione] Configurazione non presente");
			}
			rs.close();
			stm.close();
		
			return configurazione;
		} catch (SQLException se) {
			throw new AuditException("[DriverAudit::getConfigurazione] SqlException: " + se.getMessage(),se);
		} catch (Exception ex) {
			throw new AuditException("[DriverAudit::getConfigurazione] Exception: " + ex.getMessage(),ex);
		} finally {
			try {
				if(rs!=null)
					rs.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(stm!=null)
					stm.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	public ArrayList<Filtro> getFiltri() throws AuditException {
		
		ArrayList<Filtro> filtri = new ArrayList<Filtro>();
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(Costanti.DB_AUDIT_FILTRI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("ora_registrazione");
			sqlQueryObject.addOrderBy("ora_registrazione");
			sqlQueryObject.setSortType(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			rs = stm.executeQuery();
			while (rs.next()) {
				filtri.add(this.getFiltro(rs.getLong("id")));
			}
			rs.close();
			stm.close();
		
			return filtri;
		} catch (SQLException se) {
			throw new AuditException("[DriverAudit::::getFiltri] SqlException: " + se.getMessage(),se);
		} catch (Exception ex) {
			throw new AuditException("[DriverAudit::::getFiltri] Exception: " + ex.getMessage(),ex);
		} finally {
			try {
				if(rs!=null)
					rs.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(stm!=null)
					stm.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public List<Filtro> filtriList(ISearch ricerca, int idLista) throws AuditException {
		String nomeMetodo = "filtriList";
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Filtro> lista = new ArrayList<Filtro>();

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(Costanti.DB_AUDIT_FILTRI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(false, sqlQueryObject.getWhereLikeCondition("username",search,true,true),
						sqlQueryObject.getWhereLikeCondition("tipo_operazione",search,true,true),
						sqlQueryObject.getWhereLikeCondition("tipo",search,true,true),
						sqlQueryObject.getWhereLikeCondition("stato",search,true,true),
						sqlQueryObject.getWhereLikeCondition("dump_search",search,true,true));
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(Costanti.DB_AUDIT_FILTRI);
				sqlQueryObject.addSelectCountField("*", "cont");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = this.connectionDB.prepareStatement(queryString);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;

			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(Costanti.DB_AUDIT_FILTRI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("username");
				sqlQueryObject.addSelectField("tipo_operazione");
				sqlQueryObject.addSelectField("tipo");
				sqlQueryObject.addSelectField("stato");
				sqlQueryObject.addSelectField("dump_search");
				sqlQueryObject.addSelectField("ora_registrazione");
				sqlQueryObject.addWhereCondition(false, sqlQueryObject.getWhereLikeCondition("username",search,true,true),
						sqlQueryObject.getWhereLikeCondition("tipo_operazione",search,true,true),
						sqlQueryObject.getWhereLikeCondition("tipo",search,true,true),
						sqlQueryObject.getWhereLikeCondition("stato",search,true,true),
						sqlQueryObject.getWhereLikeCondition("dump_search",search,true,true));
				sqlQueryObject.addOrderBy("ora_registrazione");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(Costanti.DB_AUDIT_FILTRI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("username");
				sqlQueryObject.addSelectField("tipo_operazione");
				sqlQueryObject.addSelectField("tipo");
				sqlQueryObject.addSelectField("stato");
				sqlQueryObject.addSelectField("dump_search");
				sqlQueryObject.addSelectField("ora_registrazione");
				sqlQueryObject.addOrderBy("ora_registrazione");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = this.connectionDB.prepareStatement(queryString);
			risultato = stmt.executeQuery();

			while (risultato.next()) {
				lista.add(this.getFiltro(risultato.getLong("id")));
			}

			return lista;

		} catch (Exception qe) {
			throw new AuditException("[DriverAudit::" + nomeMetodo + "] Errore : " + qe.getMessage());
		} finally {
			try {
				if(risultato!=null)
					risultato.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(stmt!=null)
					stmt.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	public Filtro getFiltro(long id) throws AuditException {
		
		Filtro filtro = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(Costanti.DB_AUDIT_FILTRI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id=?");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			stm.setLong(1, id);
			rs = stm.executeQuery();
			if (rs.next()) {
				filtro = new Filtro();
				filtro.setUsername(rs.getString("username"));
				filtro.setTipoOperazione(TipoOperazione.parseString(rs.getString("tipo_operazione")));
				filtro.setTipoOggettoInModifica(rs.getString("tipo"));
				filtro.setStatoOperazione(StatoOperazione.parseString(rs.getString("stato")));
				filtro.setDump(rs.getString("dump_search"));
				if(rs.getInt("dump_expr")==1)
					filtro.setDumpExprRegular(true);
				else
					filtro.setDumpExprRegular(false);
				
				if(rs.getInt("enabled")==1)
					filtro.setAuditEnabled(true);
				else
					filtro.setAuditEnabled(false);
				if(rs.getInt("dump")==1)
					filtro.setDumpEnabled(true);
				else
					filtro.setDumpEnabled(false);
				
				filtro.setId(rs.getLong("id"));
			}else{
				throw new Exception("Filtro con id["+id+"] non trovato");
			}
			rs.close();
			stm.close();
		
			return filtro;
		} catch (SQLException se) {
			throw new AuditException("[DriverAudit::::getFiltri] SqlException: " + se.getMessage(),se);
		} catch (Exception ex) {
			throw new AuditException("[DriverAudit::::getFiltri] Exception: " + ex.getMessage(),ex);
		} finally {
			try {
				if(rs!=null)
					rs.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(stm!=null)
					stm.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	public ArrayList<Appender> getAppender() throws AuditException {
		
		ArrayList<Appender> appender = new ArrayList<Appender>();
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(Costanti.DB_AUDIT_APPENDER);
			sqlQueryObject.addSelectField("id");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			rs = stm.executeQuery();
			while (rs.next()) {
				appender.add(this.getAppender(rs.getLong("id")));
			}
			rs.close();
			stm.close();
		
			return appender;
		} catch (SQLException se) {
			throw new AuditException("[DriverAudit::getAppender] SqlException: " + se.getMessage(),se);
		} catch (Exception ex) {
			throw new AuditException("[DriverAudit::getAppender] Exception: " + ex.getMessage(),ex);
		} finally {
			try {
				if(rs!=null)
					rs.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(stm!=null)
					stm.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	
	public Appender getAppender(String nome) throws AuditException {
		PreparedStatement stm = null;
		ResultSet rs = null;
		
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(Costanti.DB_AUDIT_APPENDER);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("name=?");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			stm.setString(1, nome);
			rs = stm.executeQuery();
			if (rs.next()) {
			
				return this.getAppender(rs.getLong("id"));
				
			}else{
				throw new Exception("Appender con nome["+nome+"] non trovato");
			}
		
		} catch (SQLException se) {
			throw new AuditException("[DriverAudit::getAppender] SqlException: " + se.getMessage(),se);
		} catch (Exception ex) {
			throw new AuditException("[DriverAudit::getAppender] Exception: " + ex.getMessage(),ex);
		} finally {
			try {
				if(rs!=null)
					rs.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(stm!=null)
					stm.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	public Appender getAppender(long id) throws AuditException {
		
		Appender appender = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		PreparedStatement stmProperties = null;
		ResultSet rsProperties = null;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(Costanti.DB_AUDIT_APPENDER);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id=?");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			stm.setLong(1, id);
			rs = stm.executeQuery();
			if (rs.next()) {
				
				appender = new Appender();
				
				appender.setNome(rs.getString("name"));
				appender.setClassName(rs.getString("class"));
				appender.setId(rs.getLong("id"));
				
				ISQLQueryObject sqlQueryObjectProperties = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObjectProperties.addFromTable(Costanti.DB_AUDIT_APPENDER_PROPERTIES);
				sqlQueryObjectProperties.addSelectField("*");
				sqlQueryObjectProperties.addWhereCondition("id_audit_appender=?");
				String sqlQueryProperties = sqlQueryObjectProperties.createSQLQuery();
				stmProperties = this.connectionDB.prepareStatement(sqlQueryProperties);
				stmProperties.setLong(1, appender.getId());
				rsProperties = stmProperties.executeQuery();
				while (rsProperties.next()) {
					
					AppenderProperty ap = new AppenderProperty();
					ap.setId(rsProperties.getLong("id"));
					ap.setIdAppender(appender.getId());
					ap.setName(rsProperties.getString("name"));
					ap.setValue(rsProperties.getString("value"));
					appender.addProperty(ap);
					
				}
				
			}else{
				throw new Exception("Appender con id["+id+"] non trovato");
			}
			rs.close();
			stm.close();
		
			return appender;
		} catch (SQLException se) {
			throw new AuditException("[DriverAudit::getAppender] SqlException: " + se.getMessage(),se);
		} catch (Exception ex) {
			throw new AuditException("[DriverAudit::getAppender] Exception: " + ex.getMessage(),ex);
		} finally {
			try {
				if(rsProperties!=null)
					rsProperties.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(stmProperties!=null)
					stmProperties.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(rs!=null)
					rs.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if(stm!=null)
					stm.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	

	
	
	public void createConfigurazione(Configurazione configurazione) throws AuditException {
		PreparedStatement stm = null;
		String sqlQuery = "";

		if (configurazione == null)
			throw new AuditException("[DriverAudit::createConfigurazione] Parametro non valido.");

		
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addInsertTable(Costanti.DB_AUDIT_CONFIGURAZIONE);
			sqlQueryObject.addInsertField("audit_engine", "?");
			sqlQueryObject.addInsertField("enabled", "?");
			sqlQueryObject.addInsertField("dump", "?");
			sqlQueryObject.addInsertField("dump_format", "?");
			sqlQuery = sqlQueryObject.createSQLInsert();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			if(configurazione.isAuditEngineEnabled())
				stm.setInt(1, 1);
			else
				stm.setInt(1, 0);
			if(configurazione.isAuditEnabled())
				stm.setInt(2, 1);
			else
				stm.setInt(2, 0);
			if(configurazione.isDumpEnabled())
				stm.setInt(3, 1);
			else
				stm.setInt(3, 0);
			stm.setString(4, configurazione.getDumpFormat());
			stm.executeUpdate();
			stm.close();
			
			// Filtri
			for(int i=0; i<configurazione.sizeFiltri(); i++){
				Filtro filtro = configurazione.getFiltro(i);
				this.createFiltro(filtro);
			}
			
			// Appender
			for(int i=0; i<configurazione.sizeAppender(); i++){
				Appender appender = configurazione.getAppender(i);
				this.createAppender(appender);
			}
			
		} catch (Exception qe) {
			throw new AuditException(qe.getMessage(),qe);
		} finally {
			try {
				if(stm!=null)
					stm.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	public void createFiltro(Filtro filtro) throws AuditException {
		PreparedStatement stm = null;
		String sqlQuery = "";

		if (filtro == null)
			throw new AuditException("[DriverAudit::createFiltro] Parametro non valido.");

		
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addInsertTable(Costanti.DB_AUDIT_FILTRI);
			sqlQueryObject.addInsertField("username", "?");
			sqlQueryObject.addInsertField("tipo_operazione", "?");
			sqlQueryObject.addInsertField("tipo", "?");
			sqlQueryObject.addInsertField("stato", "?");
			sqlQueryObject.addInsertField("dump_search", "?");
			sqlQueryObject.addInsertField("dump_expr", "?");
			sqlQueryObject.addInsertField("enabled", "?");
			sqlQueryObject.addInsertField("dump", "?");
				
			sqlQuery = sqlQueryObject.createSQLInsert();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			stm.setString(1, filtro.getUsername());
			stm.setString(2, filtro.getTipoOperazione() != null ?
					filtro.getTipoOperazione().toString() : null);
			stm.setString(3, filtro.getTipoOggettoInModifica());
			stm.setString(4, filtro.getStatoOperazione() != null ?
					filtro.getStatoOperazione().toString() : null);
			stm.setString(5, filtro.getDump());
			if(filtro.isDumpExprRegular())
				stm.setInt(6, 1);
			else
				stm.setInt(6, 0);
			if(filtro.isAuditEnabled())
				stm.setInt(7, 1);
			else
				stm.setInt(7, 0);
			if(filtro.isDumpEnabled())
				stm.setInt(8, 1);
			else
				stm.setInt(8, 0);
			stm.executeUpdate();
			stm.close();
			
		} catch (Exception qe) {
			throw new AuditException(qe.getMessage(),qe);
		} finally {
			try {
				if(stm!=null)
					stm.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	public void createAppender(Appender appender) throws AuditException {
		PreparedStatement stm = null;
		String sqlQuery = "";

		if (appender == null)
			throw new AuditException("[DriverAudit::createAppender] Parametro non valido.");

		
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addInsertTable(Costanti.DB_AUDIT_APPENDER);
			sqlQueryObject.addInsertField("name", "?");
			sqlQueryObject.addInsertField("class", "?");
				
			sqlQuery = sqlQueryObject.createSQLInsert();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			stm.setString(1, appender.getNome());
			stm.setString(2, appender.getClassName());
			stm.executeUpdate();
			stm.close();
			
			// Recupero id dell'appender appena inserito
			long idAppender = this.getAppender(appender.getNome()).getId();
			appender.setId(idAppender);
			
			// Inserisco proprieta
			for(int i=0; i<appender.sizeProperties(); i++){
				
				AppenderProperty ap = appender.getProperty(i);
				ap.setIdAppender(idAppender);
				
				this.createAppenderProperty(ap);
			}
			
		} catch (Exception qe) {
			throw new AuditException(qe.getMessage(),qe);
		} finally {
			try {
				if(stm!=null)
					stm.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	public void createAppenderProperty(AppenderProperty appender) throws AuditException {
		PreparedStatement stm = null;
		String sqlQuery = "";

		if (appender == null)
			throw new AuditException("[DriverAudit::createAppender] Parametro non valido.");
		
		if (appender.getIdAppender() <=0 )
			throw new AuditException("[DriverAudit::createAppender] IdAppender ["+appender.getIdAppender()+"] non valido.");

		
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addInsertTable(Costanti.DB_AUDIT_APPENDER_PROPERTIES);
			sqlQueryObject.addInsertField("name", "?");
			sqlQueryObject.addInsertField("value", "?");
			sqlQueryObject.addInsertField("id_audit_appender", "?");
			
			sqlQuery = sqlQueryObject.createSQLInsert();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			stm.setString(1, appender.getName());
			stm.setString(2, appender.getValue());
			stm.setLong(3, appender.getIdAppender());
			stm.executeUpdate();
			stm.close();
						
		} catch (Exception qe) {
			throw new AuditException(qe.getMessage(),qe);
		} finally {
			try {
				if(stm!=null)
					stm.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	

	public void updateConfigurazione(Configurazione configurazione) throws AuditException {
		PreparedStatement stm = null;
		String sqlQuery = "";

		if (configurazione == null)
			throw new AuditException("[DriverAudit::updateConfigurazione] Parametro non valido.");

		
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addUpdateTable(Costanti.DB_AUDIT_CONFIGURAZIONE);
			sqlQueryObject.addUpdateField("audit_engine", "?");
			sqlQueryObject.addUpdateField("enabled", "?");
			sqlQueryObject.addUpdateField("dump", "?");
			sqlQueryObject.addUpdateField("dump_format", "?");
			sqlQuery = sqlQueryObject.createSQLUpdate();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			if(configurazione.isAuditEngineEnabled())
				stm.setInt(1, 1);
			else
				stm.setInt(1, 0);
			if(configurazione.isAuditEnabled())
				stm.setInt(2, 1);
			else
				stm.setInt(2, 0);
			if(configurazione.isDumpEnabled())
				stm.setInt(3, 1);
			else
				stm.setInt(3, 0);
			stm.setString(4, configurazione.getDumpFormat());
			stm.executeUpdate();
			stm.close();
			
			// Elimino tutti i filtri presenti
			this.deleteFiltri();
						
			// Aggiungo quelli presenti 
			for(int i=0; i<configurazione.sizeFiltri(); i++){
				Filtro filtro = configurazione.getFiltro(i);
				this.createFiltro(filtro);
			}
			
			// Elimino tutti gli appender presenti
			this.deleteAppenders();
			
			// Appender
			for(int i=0; i<configurazione.sizeAppender(); i++){
				Appender appender = configurazione.getAppender(i);
				this.createAppender(appender);
			}
			
		} catch (Exception qe) {
			throw new AuditException(qe.getMessage(),qe);
		} finally {
			try {
				if(stm!=null)
					stm.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	
	public void updateFiltro(Filtro filtro) throws AuditException {
		PreparedStatement stm = null;
		String sqlQuery = "";

		if (filtro == null)
			throw new AuditException("[DriverAudit::updateFiltro] Parametro non valido.");

		if (filtro.getId()<=0)
			throw new AuditException("[DriverAudit::updateFiltro] Id ["+filtro.getId()+"] non valido.");
		
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addUpdateTable(Costanti.DB_AUDIT_FILTRI);
			sqlQueryObject.addUpdateField("username", "?");
			sqlQueryObject.addUpdateField("tipo_operazione", "?");
			sqlQueryObject.addUpdateField("tipo", "?");
			sqlQueryObject.addUpdateField("stato", "?");
			sqlQueryObject.addUpdateField("dump_search", "?");
			sqlQueryObject.addUpdateField("dump_expr", "?");
			sqlQueryObject.addUpdateField("enabled", "?");
			sqlQueryObject.addUpdateField("dump", "?");
			sqlQueryObject.addWhereCondition("id=?");
				
			sqlQuery = sqlQueryObject.createSQLUpdate();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			stm.setString(1, filtro.getUsername());
			stm.setString(2, filtro.getTipoOperazione() != null ?
					filtro.getTipoOperazione().toString() : null);
			stm.setString(3, filtro.getTipoOggettoInModifica());
			stm.setString(4, filtro.getStatoOperazione() != null ?
					filtro.getStatoOperazione().toString() : null);
			stm.setString(5, filtro.getDump());
			if(filtro.isDumpExprRegular())
				stm.setInt(6, 1);
			else
				stm.setInt(6, 0);
			if(filtro.isAuditEnabled())
				stm.setInt(7, 1);
			else
				stm.setInt(7, 0);
			if(filtro.isDumpEnabled())
				stm.setInt(8, 1);
			else
				stm.setInt(8, 0);
			stm.setLong(9, filtro.getId());
			stm.executeUpdate();
			stm.close();
			
		} catch (Exception qe) {
			throw new AuditException(qe.getMessage(),qe);
		} finally {
			try {
				if(stm!=null)
					stm.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	public void updateAppender(Appender appender) throws AuditException {
		PreparedStatement stm = null;
		String sqlQuery = "";

		if (appender == null)
			throw new AuditException("[DriverAudit::updateAppender] Parametro non valido.");
		
		if (appender.getId()<=0)
			throw new AuditException("[DriverAudit::updateAppender] Id ["+appender.getId()+"] non valido.");
		
		
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addUpdateTable(Costanti.DB_AUDIT_APPENDER);
			sqlQueryObject.addUpdateField("name", "?");
			sqlQueryObject.addUpdateField("class", "?");
			sqlQueryObject.addWhereCondition("id=?");
				
			sqlQuery = sqlQueryObject.createSQLUpdate();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			stm.setString(1, appender.getNome());
			stm.setString(2, appender.getClassName());
			stm.setLong(3, appender.getId());
			stm.executeUpdate();
			stm.close();
			
			// Delete appenderProperties
			deleteAppenderProperties(appender);
			
			// Inserisco le proprieta
			for(int i=0; i<appender.sizeProperties(); i++){
				
				AppenderProperty ap = appender.getProperty(i);
				ap.setIdAppender(appender.getId());
				
				this.createAppenderProperty(ap);
			}
			
		} catch (Exception qe) {
			throw new AuditException(qe.getMessage(),qe);
		} finally {
			try {
				if(stm!=null)
					stm.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	
	public void updateAppenderProperty(AppenderProperty appender) throws AuditException {
		PreparedStatement stm = null;
		String sqlQuery = "";

		if (appender == null)
			throw new AuditException("[DriverAudit::updateAppenderProperty] Parametro non valido.");
		
		if (appender.getIdAppender() <=0 )
			throw new AuditException("[DriverAudit::updateAppenderProperty] IdAppender ["+appender.getIdAppender()+"] non valido.");

		if (appender.getId() <=0 )
			throw new AuditException("[DriverAudit::updateAppenderProperty] Id ["+appender.getId()+"] non valido.");

		
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addUpdateTable(Costanti.DB_AUDIT_APPENDER_PROPERTIES);
			sqlQueryObject.addUpdateField("name", "?");
			sqlQueryObject.addUpdateField("value", "?");
			sqlQueryObject.addUpdateField("id_audit_appender", "?");
			sqlQueryObject.addWhereCondition("id=?");
			
			sqlQuery = sqlQueryObject.createSQLUpdate();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			stm.setString(1, appender.getName());
			stm.setString(2, appender.getValue());
			stm.setLong(3, appender.getIdAppender());
			stm.executeUpdate();
			stm.close();
						
		} catch (Exception qe) {
			throw new AuditException(qe.getMessage(),qe);
		} finally {
			try {
				if(stm!=null)
					stm.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	
	
	public void deleteConfigurazione() throws AuditException {
		PreparedStatement stm = null;
		String sqlQuery = "";

		try {
			// Elimino tutti i filtri presenti
			this.deleteFiltri();
						
			// Elimino tutti gli appender presenti
			this.deleteAppenders();
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(Costanti.DB_AUDIT_CONFIGURAZIONE);
			sqlQuery = sqlQueryObject.createSQLDelete();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			stm.executeUpdate();
			stm.close();
			
		} catch (Exception qe) {
			throw new AuditException(qe.getMessage(),qe);
		} finally {
			try {
				if(stm!=null)
					stm.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	
	public void deleteFiltri() throws AuditException {
		PreparedStatement stm = null;
		String sqlQuery = "";

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(Costanti.DB_AUDIT_FILTRI);
			sqlQuery = sqlQueryObject.createSQLDelete();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			stm.executeUpdate();
			stm.close();
			
		} catch (Exception qe) {
			throw new AuditException(qe.getMessage(),qe);
		} finally {
			try {
				if(stm!=null)
					stm.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	public void deleteFiltro(Filtro f) throws AuditException {
		PreparedStatement stm = null;
		String sqlQuery = "";

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(Costanti.DB_AUDIT_FILTRI);
			sqlQueryObject.addWhereCondition("id=?");
			sqlQuery = sqlQueryObject.createSQLDelete();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			stm.setLong(1, f.getId());
			stm.executeUpdate();
			stm.close();
			
		} catch (Exception qe) {
			throw new AuditException(qe.getMessage(),qe);
		} finally {
			try {
				if(stm!=null)
					stm.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	public void deleteAppenders() throws AuditException {
		PreparedStatement stm = null;
		String sqlQuery = "";

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(Costanti.DB_AUDIT_APPENDER_PROPERTIES);
			sqlQuery = sqlQueryObject.createSQLDelete();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			stm.executeUpdate();
			stm.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(Costanti.DB_AUDIT_APPENDER);
			sqlQuery = sqlQueryObject.createSQLDelete();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			stm.executeUpdate();
			stm.close();
			
		} catch (Exception qe) {
			throw new AuditException(qe.getMessage(),qe);
		} finally {
			try {
				if(stm!=null)
					stm.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	public void deleteAppenderProperties(Appender appender) throws AuditException {
		PreparedStatement stm = null;
		String sqlQuery = "";

		if (appender == null)
			throw new AuditException("[DriverAudit::deleteAppenderProperties] Parametro non valido.");
		
		if (appender.getId() <=0 )
			throw new AuditException("[DriverAudit::deleteAppenderProperties] Id ["+appender.getId()+"] non valido.");

		
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(Costanti.DB_AUDIT_APPENDER_PROPERTIES);
			sqlQueryObject.addWhereCondition("id_audit_appender=?");
			sqlQuery = sqlQueryObject.createSQLDelete();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			stm.setLong(1, appender.getId());
			stm.executeUpdate();
			stm.close();
			
		} catch (Exception qe) {
			throw new AuditException(qe.getMessage(),qe);
		} finally {
			try {
				if(stm!=null)
					stm.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
}
