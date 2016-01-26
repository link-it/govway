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

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKey;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKeyJDBCType;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKeyObject;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.web.lib.audit.costanti.Costanti;
import org.openspcoop2.web.lib.audit.costanti.StatoOperazione;
import org.openspcoop2.web.lib.audit.costanti.TipoOperazione;
import org.openspcoop2.web.lib.audit.dao.Binary;
import org.openspcoop2.web.lib.audit.dao.Operation;

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

public class DriverAuditDBAppender {

	/** Connessione al Database */
	private Connection connectionDB;

	// Tipo database passato al momento della creazione dell'oggetto
	private String tipoDB = null;

	public DriverAuditDBAppender(Connection con, String tipoDB) throws AuditException {
		this.connectionDB = con;
		if (con == null)
			throw new AuditException("Connessione al Database non definita");

		this.tipoDB = tipoDB;
		if (tipoDB == null)
			throw new AuditException("Il tipoDatabase non puo essere null.");
	}

	
	
	
	public Operation getOperation(long id) throws AuditException {
		
		Operation operation = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		if(id<=0){
			throw new AuditException("[DriverAudit::getOperation]  Id non valido");
		}
		
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(Costanti.DB_AUDIT_OPERATIONS_TABLE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id=?");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			stm.setLong(1, id);
			rs = stm.executeQuery();
			if (rs.next()) {
				operation = new Operation();
				operation.setTipologia(TipoOperazione.parseString(rs.getString("tipo_operazione")).toString());
				operation.setTipoOggetto(rs.getString("tipo"));
				operation.setObjectId(rs.getString("object_id"));
				operation.setObjectOldId(rs.getString("object_old_id"));
				operation.setUtente(rs.getString("utente"));
				operation.setStato(StatoOperazione.parseString(rs.getString("stato")).toString());
				operation.setObjectDetails(rs.getString("object_details"));
				operation.setObjectClass(rs.getString("object_class"));
				operation.setError(rs.getString("error"));
				operation.setTimeRequest(rs.getTimestamp("time_request"));
				operation.setTimeExecute(rs.getTimestamp("time_execute"));
				operation.setId(id);
				
				// binaries
				ArrayList<Binary> list = this.getBinaries(id);
				if(list!=null && list.size()>0){
					operation.setBinaryList(list);
				}	
				
			}else{
				throw new AuditException("[DriverAudit::getOperation] non presente");
			}
			rs.close();
			stm.close();
		
			return operation;
		} catch (SQLException se) {
			throw new AuditException("[DriverAudit::getOperation] SqlException: " + se.getMessage(),se);
		} catch (Exception ex) {
			throw new AuditException("[DriverAudit::getOperation] Exception: " + ex.getMessage(),ex);
		} finally {
			try {
				rs.close();
				stm.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	public ArrayList<Binary> getBinaries(long idOperation) throws AuditException {
		
		ArrayList<Binary> binaries = new ArrayList<Binary>();
		PreparedStatement stm = null;
		ResultSet rs = null;

		if(idOperation<=0){
			throw new AuditException("[DriverAudit::getBinaries]  Id non valido");
		}
		
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(Costanti.DB_AUDIT_BINARIES_TABLE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_audit_operation=?");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			stm.setLong(1, idOperation);
			rs = stm.executeQuery();
			while (rs.next()) {
				binaries.add(this.getBinary(rs.getLong("id")));
			}
			rs.close();
			stm.close();
		
			return binaries;
		} catch (SQLException se) {
			throw new AuditException("[DriverAudit::getBinaries] SqlException: " + se.getMessage(),se);
		} catch (Exception ex) {
			throw new AuditException("[DriverAudit::getBinaries] Exception: " + ex.getMessage(),ex);
		} finally {
			try {
				rs.close();
				stm.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	public Binary getBinary(long id) throws AuditException {
		
		Binary binary = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		if(id<=0){
			throw new AuditException("[DriverAudit::getBinary]  Id non valido");
		}
		
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(Costanti.DB_AUDIT_BINARIES_TABLE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id=?");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			stm.setLong(1, id);
			rs = stm.executeQuery();
			if (rs.next()) {
				binary = new Binary();
				
				binary.setBinaryId(rs.getString("binary_id"));
				binary.setChecksum(BigInteger.valueOf(rs.getLong("checksum")));	
				binary.setIdOperation(rs.getLong("id_audit_operation"));
				binary.setId(id);
				
			}else{
				throw new AuditException("[DriverAudit::getBinary] non presente");
			}
			rs.close();
			stm.close();
		
			return binary;
		} catch (SQLException se) {
			throw new AuditException("[DriverAudit::getBinary] SqlException: " + se.getMessage(),se);
		} catch (Exception ex) {
			throw new AuditException("[DriverAudit::getBinary] Exception: " + ex.getMessage(),ex);
		} finally {
			try {
				rs.close();
				stm.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	
	public void createOperation(Operation operation) throws AuditException {
		ResultSet rs = null;
		PreparedStatement stm = null;
		
		if (operation == null)
			throw new AuditException("[DriverAudit::createOperation] Parametro non valido.");

		
		try {
			
			//Inserimento della traccia nel DB
			if(!TipiDatabase.isAMember(this.tipoDB)){
				throw new Exception("Tipo database ["+this.tipoDB+"] non supportato");
			}
			TipiDatabase tipo = TipiDatabase.toEnumConstant(this.tipoDB);
			// ** Preparazione parametri
			Timestamp timeRequestT = null;
			if(operation.getTimeRequest()!=null){
				timeRequestT = new Timestamp(operation.getTimeRequest().getTime());
			}
			Timestamp timeExecuteT = null;
			if(operation.getTimeExecute()!=null){
				timeExecuteT = new Timestamp(operation.getTimeExecute().getTime());
			}
			// ** Insert and return generated key
			CustomKeyGeneratorObject customKeyGeneratorObject = new CustomKeyGeneratorObject(Costanti.DB_AUDIT_OPERATIONS_TABLE, Costanti.DB_AUDIT_OPERATIONS_TABLE_ID, 
					Costanti.DB_AUDIT_OPERATIONS_TABLE_SEQUENCE, Costanti.DB_AUDIT_OPERATIONS_TABLE_FOR_ID_SEQUENCE);
			long idoperazione = InsertAndGeneratedKey.insertAndReturnGeneratedKey(this.connectionDB, tipo, customKeyGeneratorObject, 
						new InsertAndGeneratedKeyObject("tipo_operazione", operation.getTipologia(), InsertAndGeneratedKeyJDBCType.STRING),
						new InsertAndGeneratedKeyObject("tipo", operation.getTipoOggetto(), InsertAndGeneratedKeyJDBCType.STRING),
						new InsertAndGeneratedKeyObject("object_id", operation.getObjectId(), InsertAndGeneratedKeyJDBCType.STRING),
						new InsertAndGeneratedKeyObject("object_old_id", operation.getObjectOldId(), InsertAndGeneratedKeyJDBCType.STRING),
						new InsertAndGeneratedKeyObject("utente", operation.getUtente(), InsertAndGeneratedKeyJDBCType.STRING),
						new InsertAndGeneratedKeyObject("stato", operation.getStato(), InsertAndGeneratedKeyJDBCType.STRING),
						new InsertAndGeneratedKeyObject("object_details", operation.getObjectDetails(), InsertAndGeneratedKeyJDBCType.STRING),
						new InsertAndGeneratedKeyObject("object_class", operation.getObjectClass(), InsertAndGeneratedKeyJDBCType.STRING),
						new InsertAndGeneratedKeyObject("error", operation.getError(), InsertAndGeneratedKeyJDBCType.STRING),
						new InsertAndGeneratedKeyObject("time_request", timeRequestT, InsertAndGeneratedKeyJDBCType.TIMESTAMP),
						new InsertAndGeneratedKeyObject("time_execute", timeExecuteT, InsertAndGeneratedKeyJDBCType.TIMESTAMP));
			if(idoperazione<=0){
				throw new Exception("ID autoincrementale non ottenuto");
			}						
			
			// binaries
			for(int i=0; i<operation.sizeBinaryList(); i++){
				Binary binary = operation.getBinary(i);
				binary.setIdOperation(idoperazione);
				this.createBinary(binary);
			}
			
			// Aggiorno l'id
			operation.setId(idoperazione);
			
		} catch (Exception qe) {
			throw new AuditException(qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				//ignore
			}
			try {
				if (stm != null)
					stm.close();
			} catch (Exception e) {
				//ignore
			}
		}
	}
	
	public void createBinary(Binary binary) throws AuditException {
		ResultSet rs = null;
		PreparedStatement stm = null;
		String sqlQuery = "";

		if (binary == null)
			throw new AuditException("[DriverAudit::createBinary] Parametro non valido.");
		if (binary.getIdOperation() == null)
			throw new AuditException("[DriverAudit::createBinary] IDOperation non valido.");
		if (binary.getIdOperation() <= 0)
			throw new AuditException("[DriverAudit::createBinary] IDOperation("+binary.getIdOperation()+") non valido.");
		
		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addInsertTable(Costanti.DB_AUDIT_BINARIES_TABLE);
			sqlQueryObject.addInsertField("binary_id", "?");
			sqlQueryObject.addInsertField("checksum", "?");
			sqlQueryObject.addInsertField("id_audit_operation", "?");
			sqlQuery = sqlQueryObject.createSQLInsert();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			
			stm.setString(1, binary.getBinaryId());
			if(binary.getChecksum()!=null)
				stm.setLong(2, binary.getChecksum().longValue());
			else
				stm.setLong(2, -1);
			stm.setLong(3, binary.getIdOperation());
			stm.executeUpdate();
			stm.close();
			
			// Recupero id
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(Costanti.DB_AUDIT_BINARIES_TABLE);
			sqlQueryObject.addSelectField(Costanti.DB_AUDIT_BINARIES_TABLE, "id");
			sqlQueryObject.addWhereCondition("binary_id=?");
			sqlQueryObject.addWhereCondition("id_audit_operation=?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			stm.setString(1, binary.getBinaryId());
			stm.setLong(2, binary.getIdOperation());
			rs = stm.executeQuery();
			if(rs.next()){
				binary.setId(rs.getLong("id"));
			}else{
				throw new Exception("Identificativo dopo l'insert non recuperato");
			}
			rs.close();
			stm.close();
			
		} catch (Exception qe) {
			throw new AuditException(qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				//ignore
			}
			try {
				if (stm != null)
					stm.close();
			} catch (Exception e) {
				//ignore
			}
		}
	}
	
	
	public void updateOperation(Operation operation,boolean updateBinaries) throws AuditException {
		ResultSet rs = null;
		PreparedStatement stm = null;
		String sqlQuery = "";

		if (operation == null)
			throw new AuditException("[DriverAudit::updateOperation] Parametro non valido.");
		if (operation.getId()<=0)
			throw new AuditException("[DriverAudit::updateOperation] ID ("+operation.getId()+") non valido.");

		
		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addUpdateTable(Costanti.DB_AUDIT_OPERATIONS_TABLE);
			sqlQueryObject.addUpdateField("tipo_operazione", "?");
			sqlQueryObject.addUpdateField("tipo", "?");
			sqlQueryObject.addUpdateField("object_id", "?");
			sqlQueryObject.addUpdateField("object_old_id", "?");
			sqlQueryObject.addUpdateField("utente", "?");
			sqlQueryObject.addUpdateField("stato", "?");
			sqlQueryObject.addUpdateField("object_details", "?");
			sqlQueryObject.addUpdateField("object_class", "?");
			sqlQueryObject.addUpdateField("error", "?");
			sqlQueryObject.addUpdateField("time_request", "?");
			sqlQueryObject.addUpdateField("time_execute", "?");
			sqlQueryObject.addWhereCondition("id=?");
			sqlQuery = sqlQueryObject.createSQLUpdate();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			
			stm.setString(1, operation.getTipologia());
			stm.setString(2, operation.getTipoOggetto());
			stm.setString(3, operation.getObjectId());
			stm.setString(4, operation.getObjectOldId());
			stm.setString(5, operation.getUtente());
			stm.setString(6, operation.getStato());
			stm.setString(7, operation.getObjectDetails());
			stm.setString(8, operation.getObjectClass());
			stm.setString(9, operation.getError());
			stm.setTimestamp(10, new Timestamp(operation.getTimeRequest().getTime()));
			stm.setTimestamp(11, new Timestamp(operation.getTimeExecute().getTime()));
			stm.setLong(12,operation.getId());
			stm.executeUpdate();
			
			if(updateBinaries){
			
				// deleteAllBinaries
				this.deleteBinaries(operation.getId());
				
				// binaries
				for(int i=0; i<operation.sizeBinaryList(); i++){
					Binary binary = operation.getBinary(i);
					binary.setIdOperation(operation.getId());
					this.createBinary(binary);
				}
				
			}
			
		} catch (Exception qe) {
			throw new AuditException(qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				//ignore
			}
			try {
				if (stm != null)
					stm.close();
			} catch (Exception e) {
				//ignore
			}
		}
	}
	
	public void updateBinary(Binary binary) throws AuditException {
		PreparedStatement stm = null;
		String sqlQuery = "";

		if (binary == null)
			throw new AuditException("[DriverAudit::updateBinary] Parametro non valido.");
		if (binary.getIdOperation() == null)
			throw new AuditException("[DriverAudit::updateBinary] IDOperation non valido.");
		if (binary.getIdOperation() <= 0)
			throw new AuditException("[DriverAudit::updateBinary] IDOperation("+binary.getIdOperation()+") non valido.");
		if (binary.getId() == null)
			throw new AuditException("[DriverAudit::updateBinary] ID non valido.");
		if (binary.getId() <= 0)
			throw new AuditException("[DriverAudit::updateBinary] ID("+binary.getId()+") non valido.");
		
		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addUpdateTable(Costanti.DB_AUDIT_BINARIES_TABLE);
			sqlQueryObject.addUpdateField("binary_id", "?");
			sqlQueryObject.addUpdateField("checksum", "?");
			sqlQueryObject.addUpdateField("id_audit_operation", "?");
			sqlQueryObject.addWhereCondition("id=?");
			sqlQuery = sqlQueryObject.createSQLUpdate();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			
			stm.setString(1, binary.getBinaryId());
			if(binary.getChecksum()!=null)
				stm.setLong(2, binary.getChecksum().longValue());
			else
				stm.setLong(2, -1);
			stm.setLong(3, binary.getIdOperation());
			stm.setLong(4, binary.getId());
			stm.executeUpdate();
			stm.close();
			
		} catch (Exception qe) {
			throw new AuditException(qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try {
				if (stm != null)
					stm.close();
			} catch (Exception e) {
				//ignore
			}
		}
	}
	
	
	
	
	public void deleteOperation(Operation operation) throws AuditException {
		if (operation == null)
			throw new AuditException("[DriverAudit::deleteOperation] Parametro non valido.");
		if (operation.getId() <= 0)
			throw new AuditException("[DriverAudit::deleteOperation] ID("+operation.getId()+") non valido.");
		this.deleteOperation(operation.getId());
	}
	
	public void deleteOperation(long id) throws AuditException {
		PreparedStatement stm = null;
		String sqlQuery = "";

		if (id <= 0)
			throw new AuditException("[DriverAudit::deleteBinary] ID("+id+") non valido.");
		
		try {
			
			this.deleteBinaries(id);
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(Costanti.DB_AUDIT_OPERATIONS_TABLE);
			sqlQueryObject.addWhereCondition("id=?");
			sqlQuery = sqlQueryObject.createSQLDelete();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			stm.setLong(1, id);
			stm.executeUpdate();
			stm.close();
			
		} catch (Exception qe) {
			throw new AuditException(qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try {
				if (stm != null)
					stm.close();
			} catch (Exception e) {
				//ignore
			}
		}
	}
	
	
	public void deleteBinaries(long idOperation) throws AuditException {
		PreparedStatement stm = null;
		String sqlQuery = "";

		if (idOperation <= 0)
			throw new AuditException("[DriverAudit::deleteBinary] IDOperation("+idOperation+") non valido.");
		
		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(Costanti.DB_AUDIT_BINARIES_TABLE);
			sqlQueryObject.addWhereCondition("id_audit_operation=?");
			sqlQuery = sqlQueryObject.createSQLDelete();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			stm.setLong(1, idOperation);
			stm.executeUpdate();
			stm.close();
			
		} catch (Exception qe) {
			throw new AuditException(qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try {
				if (stm != null)
					stm.close();
			} catch (Exception e) {
				//ignore
			}
		}
	}
	
	public void deleteBinary(Binary binary) throws AuditException {
		if (binary == null)
			throw new AuditException("[DriverAudit::deleteBinary] Parametro non valido.");
		if (binary.getId() == null)
			throw new AuditException("[DriverAudit::deleteBinary] ID non valido.");
		if (binary.getId() <= 0)
			throw new AuditException("[DriverAudit::deleteBinary] ID("+binary.getId()+") non valido.");
		this.deleteBinary(binary.getId());
	}
	
	public void deleteBinary(long id) throws AuditException {
		PreparedStatement stm = null;
		String sqlQuery = "";

		if (id <= 0)
			throw new AuditException("[DriverAudit::deleteBinary] ID("+id+") non valido.");
		
		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(Costanti.DB_AUDIT_BINARIES_TABLE);
			sqlQueryObject.addWhereCondition("id=?");
			sqlQuery = sqlQueryObject.createSQLDelete();
			stm = this.connectionDB.prepareStatement(sqlQuery);
			stm.setLong(1, id);
			stm.executeUpdate();
			stm.close();
			
		} catch (Exception qe) {
			throw new AuditException(qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try {
				if (stm != null)
					stm.close();
			} catch (Exception e) {
				//ignore
			}
		}
	}

	public List<Operation> auditOperationList(ISearch ricerca, int idLista,
			String datainizio, String datafine, String tipooperazione,
			String tipooggetto, String id, String oldid, String utente,
			String statooperazione, String contoggetto) throws AuditException {
		String nomeMetodo = "auditOperationList";
		int offset;
		int limit;
		//String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		//search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Operation> lista = new ArrayList<Operation>();

		try {
			// Conversione Data
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd"); // SimpleDateFormat non e' thread-safe
			Date dataInizioData = datainizio != null && !"".equals(datainizio) ?
					simpleDateFormat.parse(datainizio) : null;
			Date dataFineData = datafine != null && !"".equals(datafine) ?
					simpleDateFormat.parse(datafine) : null;
			// data fine inidica l intera giornata quinid aggiungo 1 giorno alla
			// data
			// inserita
			if (dataFineData != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataFineData);
				cal.add(Calendar.DAY_OF_WEEK, 1);
				dataFineData = cal.getTime();
			}
			Calendar cal_start = null;
			if (dataInizioData != null) {
				cal_start = Calendar.getInstance();
				cal_start.setTime(dataInizioData);
			}
			Calendar cal_end = null;
			if (dataFineData != null) {
				cal_end = Calendar.getInstance();
				cal_end.setTime(dataFineData);
			}

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(Costanti.DB_AUDIT_OPERATIONS_TABLE);
			sqlQueryObject.addSelectCountField("*", "cont");
			if (datainizio != null && !datainizio.equals(""))
				sqlQueryObject.addWhereCondition("time_request >= ?");
			if (datafine != null && !datafine.equals(""))
				sqlQueryObject.addWhereCondition("time_request <= ?");
			if (tipooperazione != null && !tipooperazione.equals("-"))
				sqlQueryObject.addWhereCondition("tipo_operazione = ?");
			if (tipooggetto != null && !tipooggetto.equals("-"))
				sqlQueryObject.addWhereCondition("tipo = ?");
			if (utente != null && !utente.equals(""))
				sqlQueryObject.addWhereCondition("utente = ?");
			if (statooperazione != null && !statooperazione.equals("-"))
				sqlQueryObject.addWhereCondition("stato = ?");
			if (id != null && !id.equals(""))
				sqlQueryObject.addWhereCondition("object_id = ?");
			if (oldid != null && !oldid.equals(""))
				sqlQueryObject.addWhereCondition("object_old_id = ?");
			if (contoggetto != null && !contoggetto.equals(""))
				sqlQueryObject.addWhereLikeCondition("object_details",
						contoggetto, true, true);
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = this.connectionDB.prepareStatement(queryString);
			int param_index = 0;
			if (datainizio != null && !datainizio.equals(""))
				stmt.setTimestamp(++param_index, new Timestamp(cal_start.getTimeInMillis()));
			if (datafine != null && !datafine.equals(""))
				stmt.setTimestamp(++param_index, new Timestamp(cal_end.getTimeInMillis()));
			if (tipooperazione != null && !tipooperazione.equals("-"))
				stmt.setString(++param_index, tipooperazione);
			if (tipooggetto != null && !tipooggetto.equals("-"))
				stmt.setString(++param_index, tipooggetto);
			if (utente != null && !utente.equals(""))
				stmt.setString(++param_index, utente);
			if (statooperazione != null && !statooperazione.equals("-"))
				stmt.setString(++param_index, statooperazione);
			if (id != null && !id.equals(""))
				stmt.setString(++param_index, id);
			if (oldid != null && !oldid.equals(""))
				stmt.setString(++param_index, oldid);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(Costanti.DB_AUDIT_OPERATIONS_TABLE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("time_execute");
			if (datainizio != null && !datainizio.equals(""))
				sqlQueryObject.addWhereCondition("time_request >= ?");
			if (datafine != null && !datafine.equals(""))
				sqlQueryObject.addWhereCondition("time_request <= ?");
			if (tipooperazione != null && !tipooperazione.equals("-"))
				sqlQueryObject.addWhereCondition("tipo_operazione = ?");
			if (tipooggetto != null && !tipooggetto.equals("-"))
				sqlQueryObject.addWhereCondition("tipo = ?");
			if (utente != null && !utente.equals(""))
				sqlQueryObject.addWhereCondition("utente = ?");
			if (statooperazione != null && !statooperazione.equals("-"))
				sqlQueryObject.addWhereCondition("stato = ?");
			if (id != null && !id.equals(""))
				sqlQueryObject.addWhereCondition("object_id = ?");
			if (oldid != null && !oldid.equals(""))
				sqlQueryObject.addWhereCondition("object_old_id = ?");
			if (contoggetto != null && !contoggetto.equals(""))
				sqlQueryObject.addWhereLikeCondition("object_details",
						contoggetto, true, true);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy("time_execute");
			sqlQueryObject.setSortType(false);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = this.connectionDB.prepareStatement(queryString);
			param_index = 0;
			if (datainizio != null && !datainizio.equals(""))
				stmt.setTimestamp(++param_index, new Timestamp(cal_start.getTimeInMillis()));
			if (datafine != null && !datafine.equals(""))
				stmt.setTimestamp(++param_index, new Timestamp(cal_end.getTimeInMillis()));
			if (tipooperazione != null && !tipooperazione.equals("-"))
				stmt.setString(++param_index, tipooperazione);
			if (tipooggetto != null && !tipooggetto.equals("-"))
				stmt.setString(++param_index, tipooggetto);
			if (utente != null && !utente.equals(""))
				stmt.setString(++param_index, utente);
			if (statooperazione != null && !statooperazione.equals("-"))
				stmt.setString(++param_index, statooperazione);
			if (id != null && !id.equals(""))
				stmt.setString(++param_index, id);
			if (oldid != null && !oldid.equals(""))
				stmt.setString(++param_index, oldid);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				lista.add(this.getOperation(risultato.getLong("id")));
			}

			return lista;

		} catch (Exception qe) {
			throw new AuditException("[DriverAuditDBAppender::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {
			try {
				risultato.close();
				stmt.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
}
