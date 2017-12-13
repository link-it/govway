/*
 * OpenSPCoop - Customizable API Gateway
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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

package org.openspcoop2.core.commons;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * Funzioni di utilita utilizzate dai driver
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DBMappingUtils {

	// -- MAPPING EROGAZIONE

	public static int countMappingErogazione(Connection con, String tipoDB) throws CoreException{
		return _countMappingErogazione(con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static int countMappingErogazione(Connection con, String tipoDB,String tabellaSoggetti) throws CoreException{
		return _countMappingErogazione(con, tipoDB, tabellaSoggetti);
	}
	private static int _countMappingErogazione(Connection con, String tipoDB,String tabellaSoggetti) throws CoreException{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.MAPPING_EROGAZIONE_PA);
			sqlQueryObject.addSelectCountField("id","mapping_count");
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("mapping_count");
			}
			else{
				return 0;
			}
			
		}catch(Exception e){
			throw new CoreException("createMappingErogazione error",e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}
	
	public static List<MappingErogazionePortaApplicativa> mappingErogazionePortaApplicativaList(Connection con,	String tipoDB, IDServizio idAccordoServizio, Integer idServizio, Integer idSoggettoErogatore, ISearch ricerca) throws CoreException{
		return _mappingErogazionePortaApplicativaList(con, tipoDB, idAccordoServizio, idServizio, idSoggettoErogatore, ricerca,CostantiDB.SOGGETTI);
	}
	
	public static List<MappingErogazionePortaApplicativa> mappingErogazionePortaApplicativaList(Connection con,	String tipoDB, IDServizio idAccordoServizio, Integer idServizio, Integer idSoggettoErogatore, ISearch ricerca,String tabellaSoggetti) throws CoreException{
		return _mappingErogazionePortaApplicativaList(con, tipoDB, idAccordoServizio, idServizio, idSoggettoErogatore, ricerca,tabellaSoggetti);
	}
	
	private static List<MappingErogazionePortaApplicativa> _mappingErogazionePortaApplicativaList(Connection con, String tipoDB, IDServizio idAccordoServizio, Integer idServizio, Integer idSoggettoErogatore, ISearch ricerca,String tabellaSoggetti) throws CoreException{
		int idLista = Liste.CONFIGURAZIONE_EROGAZIONE;
		int offset = 0;
		int limit = 0;
		String search = "";
		String queryString;

		if(ricerca != null) {
			limit = ricerca.getPageSize(idLista);
			offset = ricerca.getIndexIniziale(idLista);
			search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		}
		
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		
		List<MappingErogazionePortaApplicativa> lista = new ArrayList<MappingErogazionePortaApplicativa>();
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.MAPPING_EROGAZIONE_PA);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_EROGAZIONE_PA+".id_erogazione = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id="+CostantiDB.MAPPING_EROGAZIONE_PA+".id_porta");
			if (!search.equals("")) {
				sqlQueryObject.addWhereCondition(false, 
						sqlQueryObject.getWhereLikeCondition(CostantiDB.MAPPING_EROGAZIONE_PA+".nome", search, true, true),
						sqlQueryObject.getWhereLikeCondition(CostantiDB.PORTE_APPLICATIVE +".nome_porta", search, true, true));
			} 
			
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int index = 1;
			stmt.setLong(index++, idServizio);
			
			risultato = stmt.executeQuery();
			if (risultato.next() && ricerca != null)
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();
			
			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.MAPPING_EROGAZIONE_PA);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField(CostantiDB.MAPPING_EROGAZIONE_PA+".id_erogazione");
			sqlQueryObject.addSelectField(CostantiDB.MAPPING_EROGAZIONE_PA+".id_porta");
			sqlQueryObject.addSelectField(CostantiDB.MAPPING_EROGAZIONE_PA+".is_default");
			sqlQueryObject.addSelectField(CostantiDB.MAPPING_EROGAZIONE_PA+".nome");
			sqlQueryObject.addSelectField(CostantiDB.MAPPING_EROGAZIONE_PA+".id");
			sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".nome_porta");
			sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_EROGAZIONE_PA+".id_erogazione = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id="+CostantiDB.MAPPING_EROGAZIONE_PA+".id_porta");
			
			if (!search.equals("")) {
				sqlQueryObject.addWhereCondition(false, 
						sqlQueryObject.getWhereLikeCondition(CostantiDB.MAPPING_EROGAZIONE_PA+".nome", search, true, true),
						sqlQueryObject.getWhereLikeCondition(CostantiDB.PORTE_APPLICATIVE +".nome_porta", search, true, true));
			} 
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(CostantiDB.MAPPING_EROGAZIONE_PA+".is_default",false);
			sqlQueryObject.addOrderBy(CostantiDB.MAPPING_EROGAZIONE_PA+".nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			
			stmt = con.prepareStatement(queryString);
			
			index = 1;
			stmt.setLong(index++, idServizio);
			
			risultato = stmt.executeQuery();
			
			MappingErogazionePortaApplicativa mapping = null;

			while (risultato.next()) {

				mapping = new MappingErogazionePortaApplicativa();
				
				Long id = risultato.getLong("id");
//				Long idPorta = risultato.getLong("id_porta");
//				Long idErogazione = risultato.getLong("id_erogazione");
				String nome = risultato.getString("nome");
				Integer isDefaultInt = risultato.getInt("is_default");
				boolean isDefault = isDefaultInt > 0 ;
				
				mapping.setNome(nome);
				mapping.setTableId(id);
				mapping.setDefault(isDefault);
				// evitiamo una join utilizzo l'id che mi sono passato come parametro
				mapping.setIdServizio(idAccordoServizio);
				
				IDPortaApplicativa idPortaApplicativa = new IDPortaApplicativa();
				String nomePorta = risultato.getString("nome_porta");
				idPortaApplicativa.setNome(nomePorta);
				mapping.setIdPortaApplicativa(idPortaApplicativa);

				lista.add(mapping);

			}
			
		}catch(Exception e){
			throw new CoreException("mappingErogazionePortaApplicativaList error",e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

		}
		
		
		return lista;
	}
	
	
	public static void createMappingErogazione(String nome, boolean isDefault, IDServizio idServizio, IDPortaApplicativa idPortaApplicativa,  
			Connection con, String tipoDB) throws CoreException{
		createMappingErogazione(nome, isDefault, idServizio, idPortaApplicativa, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static void createMappingErogazione(String nome, boolean isDefault, IDServizio idServizio, IDPortaApplicativa idPortaApplicativa,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException{
		long idServizioLong = DBUtils.getIdAccordoServizioParteSpecifica(idServizio, con, tipoDB);
		if(idServizioLong<=0){
			throw new CoreException("Servizio ["+idServizio.toString()+"] non esistente");
		}
		_createMappingErogazione(nome, isDefault, idServizioLong, idPortaApplicativa, con, tipoDB, tabellaSoggetti);
	}
	
	private static void _createMappingErogazione(String nome, boolean isDefault, long idServizioLong, IDPortaApplicativa idPortaApplicativa,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException{
		PreparedStatement stmt = null;

		try {

			if(idServizioLong<=0){
				throw new Exception("IdServizio non fornito");
			}
			long idPA = DBUtils.getIdPortaApplicativa(idPortaApplicativa.getNome(), con, tipoDB);
			if(idPA<=0){
				throw new Exception("PortaApplicativa ["+idPortaApplicativa+"] non esistente");
			}
			
			int isDefaultInt = isDefault ? 1 : 0;
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addInsertTable(CostantiDB.MAPPING_EROGAZIONE_PA);
			sqlQueryObject.addInsertField("id_erogazione", "?");
			sqlQueryObject.addInsertField("id_porta", "?");
			sqlQueryObject.addInsertField("nome", "?");
			sqlQueryObject.addInsertField("is_default", "?");
			String queryString = sqlQueryObject.createSQLInsert();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idServizioLong);
			stmt.setLong(2, idPA);
			stmt.setString(3, nome);
			stmt.setInt(4, isDefaultInt);
			stmt.executeUpdate();
			stmt.close();
			
		}catch(Exception e){
			throw new CoreException("createMappingErogazione error",e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}
	
		
	public static void deleteMappingErogazione(IDServizio idServizio, 
			Connection con, String tipoDB) throws CoreException{
		deleteMappingErogazione(idServizio, null, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static void deleteMappingErogazione(IDServizio idServizio, 
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException{
		deleteMappingErogazione(idServizio, null, con, tipoDB, tabellaSoggetti);
	}
	public static void deleteMappingErogazione(IDServizio idServizio, IDPortaApplicativa idPortaApplicativa,
			Connection con, String tipoDB) throws CoreException{
		deleteMappingErogazione(idServizio, idPortaApplicativa, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static void deleteMappingErogazione(IDServizio idServizio, IDPortaApplicativa idPortaApplicativa,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException{
		long idServizioLong = DBUtils.getIdAccordoServizioParteSpecifica(idServizio, con, tipoDB);
		if(idServizioLong<=0){
			throw new CoreException("Servizio ["+idServizio.toString()+"] non esistente");
		}
		_deleteMappingErogazione(idServizioLong, idPortaApplicativa, con, tipoDB, tabellaSoggetti);
	}
	
	private static void _deleteMappingErogazione(long idServizioLong, IDPortaApplicativa idPortaApplicativa,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException{
		PreparedStatement stmt = null;

		try {

			if(idServizioLong<=0){
				throw new Exception("IdServizio non fornito");
			}
			long idPA = -1;
			if(idPortaApplicativa!=null){
				idPA = DBUtils.getIdPortaApplicativa(idPortaApplicativa.getNome(), con, tipoDB);
				if(idPA<=0){
					throw new Exception("PortaApplicativa ["+idPortaApplicativa+"] non esistente");
				}
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.MAPPING_EROGAZIONE_PA);
			sqlQueryObject.addWhereCondition("id_erogazione=?");
			if(idPortaApplicativa!=null){
				sqlQueryObject.addWhereCondition("id_porta=?");
			}
			sqlQueryObject.setANDLogicOperator(true);
			String queryString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idServizioLong);
			if(idPortaApplicativa!=null){
				stmt.setLong(2, idPA);
			}
			stmt.executeUpdate();
			stmt.close();
			
		}catch(Exception e){
			throw new CoreException("deleteMappingErogazione error",e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}
		

	
	public static IDPortaApplicativa getIDPortaApplicativaAssociata(IDServizio idServizio,
			Connection con, String tipoDB) throws CoreException {
		return getIDPortaApplicativaAssociata(idServizio, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static IDPortaApplicativa getIDPortaApplicativaAssociata(IDServizio idServizio, 
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		long idServizioLong = DBUtils.getIdAccordoServizioParteSpecifica(idServizio, con, tipoDB);
		if(idServizioLong<=0){
			throw new CoreException("Servizio ["+idServizio.toString()+"] non esistente");
		}
		return _getIDPortaApplicativaAssociata(idServizioLong, con, tipoDB, tabellaSoggetti);
	}
	
	private static IDPortaApplicativa _getIDPortaApplicativaAssociata(long idServizioLong, 
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {

			if(idServizioLong<=0){
				throw new Exception("IdServizio non fornito");
			}

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.MAPPING_EROGAZIONE_PA);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
//			sqlQueryObject.addFromTable(tabellaSoggetti);
			sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".nome_porta");
//			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".tipo_soggetto");
//			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".nome_soggetto");
			sqlQueryObject.addWhereCondition("id_erogazione = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id="+CostantiDB.MAPPING_EROGAZIONE_PA+".id_porta");
//			sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id="+CostantiDB.PORTE_APPLICATIVE+".id_soggetto");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			int indexStmt = 1;
			stm.setLong(indexStmt++,idServizioLong);
		
			rs = stm.executeQuery();
			if (rs.next()) {
				IDPortaApplicativa idPA = new IDPortaApplicativa();
				String nome = rs.getString("nome_porta");
				idPA.setNome(nome);
				
//				IDSoggetto idS = new IDSoggetto(rs.getString("tipo_soggetto"),rs.getString("nome_soggetto"));
//				idPA.setSoggetto(idS);
				
				return idPA;
			}
			else{
				//throw new CoreException("Mapping tra PA e servizio ["+idServizio.toString()+"] non esistente");
				return null;
			}
		}catch(CoreException de){
			throw de;
		}
		catch(Exception e){
			throw new CoreException("getIDPortaApplicativaAssociata error",e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}
	
	
	
	public static boolean existsIDPortaApplicativaAssociata(IDServizio idServizio,
			Connection con, String tipoDB) throws CoreException {
		return existsIDPortaApplicativaAssociata(idServizio, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static boolean existsIDPortaApplicativaAssociata(IDServizio idServizio, 
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		long idServizioLong = DBUtils.getIdAccordoServizioParteSpecifica(idServizio, con, tipoDB);
		if(idServizioLong<=0){
			throw new CoreException("Servizio ["+idServizio.toString()+"] non esistente");
		}
		return _existsIDPortaApplicativaAssociata(idServizioLong, con, tipoDB, tabellaSoggetti);
	}
	
	private static boolean _existsIDPortaApplicativaAssociata(long idServizioLong, 
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {

			if(idServizioLong<=0){
				throw new Exception("IdServizio non fornito");
			}

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.MAPPING_EROGAZIONE_PA);
			sqlQueryObject.addWhereCondition("id_erogazione = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			int indexStmt = 1;
			stm.setLong(indexStmt++,idServizioLong);
		
			rs = stm.executeQuery();
			return rs.next();
		}catch(CoreException de){
			throw de;
		}
		catch(Exception e){
			throw new CoreException("existsIDPortaApplicativaAssociata error",e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}
	
	
	

	
	public static boolean existsMappingErogazione(IDServizio idServizio, IDPortaApplicativa idPortaApplicativa,
			Connection con, String tipoDB) throws CoreException {
		return existsMappingErogazione(idServizio, idPortaApplicativa, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static boolean existsMappingErogazione(IDServizio idServizio, IDPortaApplicativa idPortaApplicativa,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		try {
			long id = getTableIdMappingErogazione(idServizio, idPortaApplicativa, con, tipoDB);
			return id>0;
		}catch(Exception e) {
			return false;
		}
	}
	
	
	
	
	public static long getTableIdMappingErogazione(IDServizio idServizio, IDPortaApplicativa idPortaApplicativa,
			Connection con, String tipoDB) throws CoreException {
		return getTableIdMappingErogazione(idServizio, idPortaApplicativa, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static long getTableIdMappingErogazione(IDServizio idServizio, IDPortaApplicativa idPortaApplicativa,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		long idServizioLong = DBUtils.getIdAccordoServizioParteSpecifica(idServizio, con, tipoDB);
		if(idServizioLong<=0){
			throw new CoreException("Servizio ["+idServizio.toString()+"] non esistente");
		}
		return _getTableIdMappingErogazione(idServizioLong, idPortaApplicativa, con, tipoDB, tabellaSoggetti);
	}
	
	private static long _getTableIdMappingErogazione(long idServizioLong, IDPortaApplicativa idPortaApplicativa,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {

			if(idServizioLong<=0){
				throw new Exception("IdServizio non fornito");
			}
			long idPA = DBUtils.getIdPortaApplicativa(idPortaApplicativa.getNome(), con, tipoDB);
			if(idPA<=0){
				throw new Exception("PortaApplicativa ["+idPortaApplicativa+"] non esistente");
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.MAPPING_EROGAZIONE_PA);
			sqlQueryObject.addWhereCondition("id_erogazione=?");
			sqlQueryObject.addWhereCondition("id_porta=?");
			sqlQueryObject.setANDLogicOperator(true);
			String queryString = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(queryString);
			stm.setLong(1, idServizioLong);
			stm.setLong(2, idPA);
			rs = stm.executeQuery();
			if (rs.next()) {
				return rs.getLong("id");
			}
			else{
				throw new CoreException("Mapping tra PA (id:"+idPA+") e servizio (id:"+idServizioLong+") non esistente");
			}
		}catch(CoreException de){
			throw de;
		}
		catch(Exception e){
			throw new CoreException("getIDPortaApplicativaAssociata error",e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}
	
	public static boolean checkMappingErogazione(IDServizio idServizio, IDPortaApplicativa idPortaApplicativa, boolean isDefault,
			Connection con, String tipoDB) throws CoreException {
		return checkMappingErogazione(idServizio, idPortaApplicativa, isDefault, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static boolean checkMappingErogazione(IDServizio idServizio, IDPortaApplicativa idPortaApplicativa, boolean isDefault,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		long idServizioLong = DBUtils.getIdAccordoServizioParteSpecifica(idServizio, con, tipoDB);
		if(idServizioLong<=0){
			throw new CoreException("Servizio ["+idServizio.toString()+"] non esistente");
		}
		return _checkMappingErogazione(idServizioLong, idPortaApplicativa, isDefault, con, tipoDB, tabellaSoggetti);
	}
	
	private static boolean _checkMappingErogazione(long idServizioLong, IDPortaApplicativa idPortaApplicativa, boolean isDefault,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {

			if(idServizioLong<=0){
				throw new Exception("IdServizio non fornito");
			}
			long idPA = DBUtils.getIdPortaApplicativa(idPortaApplicativa.getNome(), con, tipoDB);
			if(idPA<=0){
				throw new Exception("PortaApplicativa ["+idPortaApplicativa+"] non esistente");
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.MAPPING_EROGAZIONE_PA);
			sqlQueryObject.addSelectCountField("id","mapping_count");
			sqlQueryObject.addWhereCondition("id_erogazione=?");
			sqlQueryObject.addWhereCondition("id_porta=?");
			sqlQueryObject.addWhereCondition("is_default = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String queryString = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(queryString);
			stm.setLong(1, idServizioLong);
			stm.setLong(2, idPA);
			stm.setLong(3, (isDefault ? 1 : 0));
			rs = stm.executeQuery();
			int found = 0;
			if (rs.next()) {
				found = rs.getInt("mapping_count");
			}
			
			return found > 0;
		}catch(CoreException de){
			throw de;
		}
		catch(Exception e){
			throw new CoreException("_checkIsDefaultMappingErogazione error",e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}
		}
	}
	
	
	
	
	
	
	// -- MAPPING FRUIZIONE
	
	public static int countMappingFruizione(Connection con, String tipoDB) throws CoreException{
		return _countMappingFruizione(con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static int countMappingFruizione(Connection con, String tipoDB,String tabellaSoggetti) throws CoreException{
		return _countMappingFruizione(con, tipoDB, tabellaSoggetti);
	}
	private static int _countMappingFruizione(Connection con, String tipoDB,String tabellaSoggetti) throws CoreException{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.MAPPING_FRUIZIONE_PD);
			sqlQueryObject.addSelectCountField("id","mapping_count");
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("mapping_count");
			}
			else{
				return 0;
			}
			
		}catch(Exception e){
			throw new CoreException("createMappingFruizione error",e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}
	
	public static List<MappingFruizionePortaDelegata> mappingFruizionePortaDelegataList(Connection con, String tipoDB, Long idFru, IDSoggetto idSoggettoFruitore, Long idSoggetto, String tipoServizio, String nomeServizio, IDServizio idAccordoServizio, Long idServizio, String tipoSoggettoErogatore,
			String nomeSoggettoErogatore, Long idSoggettoErogatore, ISearch ricerca) throws CoreException {
		return _mappingFruizionePortaDelegataList(con, tipoDB, idFru, idSoggettoFruitore, idSoggetto, tipoServizio, nomeServizio,  idAccordoServizio, idServizio, tipoSoggettoErogatore, nomeSoggettoErogatore, idSoggettoErogatore, ricerca, CostantiDB.SOGGETTI); 
	}
	
	public static List<MappingFruizionePortaDelegata> mappingFruizionePortaDelegataList(Connection con, String tipoDB, Long idFru, IDSoggetto idSoggettoFruitore, Long idSoggetto, String tipoServizio, String nomeServizio,  IDServizio idAccordoServizio, Long idServizio, String tipoSoggettoErogatore,
			String nomeSoggettoErogatore, Long idSoggettoErogatore, ISearch ricerca,String tabellaSoggetti) throws CoreException {
		return _mappingFruizionePortaDelegataList(con, tipoDB, idFru, idSoggettoFruitore, idSoggetto, tipoServizio, nomeServizio, idAccordoServizio, idServizio, tipoSoggettoErogatore, nomeSoggettoErogatore, idSoggettoErogatore, ricerca, tabellaSoggetti); 
	}
	
	private static List<MappingFruizionePortaDelegata> _mappingFruizionePortaDelegataList(Connection con, String tipoDB, Long idFru,IDSoggetto idSoggettoFruitore,  Long idSoggetto, String tipoServizio, String nomeServizio, IDServizio idAccordoServizio, Long idServizio, String tipoSoggettoErogatore,
			String nomeSoggettoErogatore, Long idSoggettoErogatore, ISearch ricerca,String tabellaSoggetti) throws CoreException {
		int idLista = Liste.CONFIGURAZIONE_FRUIZIONE;
		int offset = 0;
		int limit = 0;
		String search = "";
		String queryString;

		if(ricerca != null) {
			limit = ricerca.getPageSize(idLista);
			offset = ricerca.getIndexIniziale(idLista);
			search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		}
		
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		
		List<MappingFruizionePortaDelegata> lista = new ArrayList<MappingFruizionePortaDelegata>();
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.MAPPING_FRUIZIONE_PD);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".id_fruizione = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id="+CostantiDB.MAPPING_FRUIZIONE_PD+".id_porta");
			if (!search.equals("")) {
				sqlQueryObject.addWhereCondition(false, 
						sqlQueryObject.getWhereLikeCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".nome", search, true, true),
						sqlQueryObject.getWhereLikeCondition(CostantiDB.PORTE_DELEGATE +".nome_porta", search, true, true));
			} 
			
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int index = 1;
			stmt.setLong(index++, idFru);
			
			risultato = stmt.executeQuery();
			if (risultato.next() && ricerca != null)
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();
			
			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.MAPPING_FRUIZIONE_PD);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField(CostantiDB.MAPPING_FRUIZIONE_PD+".id_fruizione");
			sqlQueryObject.addSelectField(CostantiDB.MAPPING_FRUIZIONE_PD+".id_porta");
			sqlQueryObject.addSelectField(CostantiDB.MAPPING_FRUIZIONE_PD+".is_default");
			sqlQueryObject.addSelectField(CostantiDB.MAPPING_FRUIZIONE_PD+".nome");
			sqlQueryObject.addSelectField(CostantiDB.MAPPING_FRUIZIONE_PD+".id");
			sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".nome_porta");
			sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".id_fruizione = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id="+CostantiDB.MAPPING_FRUIZIONE_PD+".id_porta");
			
			if (!search.equals("")) {
				sqlQueryObject.addWhereCondition(false, 
						sqlQueryObject.getWhereLikeCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".nome", search, true, true),
						sqlQueryObject.getWhereLikeCondition(CostantiDB.PORTE_DELEGATE +".nome_porta", search, true, true));
			} 
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(CostantiDB.MAPPING_FRUIZIONE_PD+".is_default",false);
			sqlQueryObject.addOrderBy(CostantiDB.MAPPING_FRUIZIONE_PD+".nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			
			stmt = con.prepareStatement(queryString);
			
			index = 1;
			stmt.setLong(index++, idFru);
			
			risultato = stmt.executeQuery();
			
			MappingFruizionePortaDelegata mapping = null;

			while (risultato.next()) {

				mapping = new MappingFruizionePortaDelegata();
				
				Long id = risultato.getLong("id");
//				Long idPorta = risultato.getLong("id_porta");
//				Long idFruizione = risultato.getLong("id_fruizione");
				String nome = risultato.getString("nome");
				Integer isDefaultInt = risultato.getInt("is_default");
				boolean isDefault = isDefaultInt > 0 ;
				
				mapping.setNome(nome);
				mapping.setTableId(id);
				mapping.setDefault(isDefault);
				// evitiamo una join utilizzo l'id che mi sono passato come parametro
				mapping.setIdServizio(idAccordoServizio);
				mapping.setIdFruitore(idSoggettoFruitore); 
				
				IDPortaDelegata idPortaDelegata = new IDPortaDelegata();
				String nomePorta = risultato.getString("nome_porta");
				idPortaDelegata.setNome(nomePorta);
				mapping.setIdPortaDelegata(idPortaDelegata);
				
				lista.add(mapping);

			}
			
		
		}catch(Exception e){
			throw new CoreException("mappingFruizionePortaDelegataList error",e);
		} finally {
		
			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}
		
		}
		
		return lista;
	}

	public static void createMappingFruizione(String nome, boolean isDefault, IDServizio idServizio, IDSoggetto idFruitore, IDPortaDelegata idPortaDelegata,
			Connection con, String tipoDB) throws CoreException{
		createMappingFruizione(nome, isDefault, idServizio, idFruitore, idPortaDelegata, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static void createMappingFruizione(String nome, boolean isDefault, IDServizio idServizio, IDSoggetto idFruitore, IDPortaDelegata idPortaDelegata,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException{
		long idFruizione = DBUtils.getIdFruizioneServizio(idServizio, idFruitore, con, tipoDB, tabellaSoggetti);
		if(idFruizione<=0){
			throw new CoreException("Fruizione da parte del soggetto ["+idFruitore.toString()+"] del servizio ["+idServizio.toString()+"] non esistente");
		}
		_createMappingFruizione(nome, isDefault, idFruizione, idPortaDelegata, con, tipoDB, tabellaSoggetti);
	}
	
	private static void _createMappingFruizione(String nome, boolean isDefault, long idFruizione, IDPortaDelegata idPortaDelegata,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException{
		PreparedStatement stmt = null;

		try {

			if(idFruizione<=0){
				throw new Exception("IdFruizione non fornita");
			}
			long idPD = DBUtils.getIdPortaDelegata(idPortaDelegata.getNome(), con, tipoDB);
			if(idPD<=0){
				throw new Exception("PortaDelegata ["+idPortaDelegata+"] non esistente");
			}
			
			int isDefaultInt = isDefault ? 1 : 0;
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addInsertTable(CostantiDB.MAPPING_FRUIZIONE_PD);
			sqlQueryObject.addInsertField("id_fruizione", "?");
			sqlQueryObject.addInsertField("id_porta", "?");
			sqlQueryObject.addInsertField("nome", "?");
			sqlQueryObject.addInsertField("is_default", "?");
			String queryString = sqlQueryObject.createSQLInsert();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idFruizione);
			stmt.setLong(2, idPD);
			stmt.setString(3, nome);
			stmt.setInt(4, isDefaultInt);
			stmt.executeUpdate();
			stmt.close();
			
		}catch(Exception e){
			throw new CoreException("createMappingFruizione error",e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}
	
	
	public static void deleteMappingFruizione(IDServizio idServizio, IDSoggetto idFruitore,
			Connection con, String tipoDB) throws CoreException{
		deleteMappingFruizione(idServizio, idFruitore, null, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static void deleteMappingFruizione(IDServizio idServizio, IDSoggetto idFruitore,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException{
		deleteMappingFruizione(idServizio, idFruitore, null, con, tipoDB, tabellaSoggetti);
	}
	public static void deleteMappingFruizione(IDServizio idServizio, IDSoggetto idFruitore, IDPortaDelegata idPortaDelegata,
			Connection con, String tipoDB) throws CoreException{
		deleteMappingFruizione(idServizio, idFruitore, idPortaDelegata, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static void deleteMappingFruizione(IDServizio idServizio, IDSoggetto idFruitore, IDPortaDelegata idPortaDelegata,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException{
		long idFruizione = DBUtils.getIdFruizioneServizio(idServizio, idFruitore, con, tipoDB, tabellaSoggetti);
		if(idFruizione<=0){
			throw new CoreException("Fruizione da parte del soggetto ["+idFruitore.toString()+"] del servizio ["+idServizio.toString()+"] non esistente");
		}
		_deleteMappingFruizione(idFruizione, idPortaDelegata, con, tipoDB, tabellaSoggetti);
	}
	
	private static void _deleteMappingFruizione(long idFruizione, IDPortaDelegata idPortaDelegata,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException{
		PreparedStatement stmt = null;

		try {

			if(idFruizione<=0){
				throw new Exception("IdFruizione non fornita");
			}
			long idPD = -1;
			if(idPortaDelegata!=null){
				idPD = DBUtils.getIdPortaDelegata(idPortaDelegata.getNome(), con, tipoDB);
				if(idPD<=0){
					throw new Exception("PortaDelegata ["+idPortaDelegata+"] non esistente");
				}
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.MAPPING_FRUIZIONE_PD);
			sqlQueryObject.addWhereCondition("id_fruizione=?");
			if(idPortaDelegata!=null){
				sqlQueryObject.addWhereCondition("id_porta=?");
			}
			sqlQueryObject.setANDLogicOperator(true);
			String queryString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idFruizione);
			if(idPortaDelegata!=null){
				stmt.setLong(2, idPD);
			}
			stmt.executeUpdate();
			stmt.close();
			
		}catch(Exception e){
			throw new CoreException("deleteMappingFruizione error",e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}

	
	public static IDPortaDelegata getIDPortaDelegataAssociata(IDServizio idServizio, IDSoggetto idFruitore,
			Connection con, String tipoDB) throws CoreException {
		return getIDPortaDelegataAssociata(idServizio, idFruitore, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static IDPortaDelegata getIDPortaDelegataAssociata(IDServizio idServizio, IDSoggetto idFruitore,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		long idFruizione = DBUtils.getIdFruizioneServizio(idServizio, idFruitore, con, tipoDB, tabellaSoggetti);
		if(idFruizione<=0){
			throw new CoreException("Fruizione da parte del soggetto ["+idFruitore.toString()+"] del servizio ["+idServizio.toString()+"] non esistente");
		}
		return _getIDPortaDelegataAssociata(idFruizione, con, tipoDB, tabellaSoggetti);
	}
	
	private static IDPortaDelegata _getIDPortaDelegataAssociata(long idFruizione,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {

			if(idFruizione<=0){
				throw new Exception("IdFruizione non fornita");
			}

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.MAPPING_FRUIZIONE_PD);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
//			sqlQueryObject.addFromTable(tabellaSoggetti);
			sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".nome_porta");
//			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".tipo_soggetto");
//			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".nome_soggetto");
			sqlQueryObject.addWhereCondition("id_fruizione = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id="+CostantiDB.MAPPING_FRUIZIONE_PD+".id_porta");
//			sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id="+CostantiDB.PORTE_DELEGATE+".id_soggetto");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			int indexStmt = 1;
			stm.setLong(indexStmt++,idFruizione);
		
			rs = stm.executeQuery();
			if (rs.next()) {
				IDPortaDelegata idPD = new IDPortaDelegata();
				String nome = rs.getString("nome_porta");
				idPD.setNome(nome);
				
//				IDSoggetto idS = new IDSoggetto(rs.getString("tipo_soggetto"),rs.getString("nome_soggetto"));
//				idPD.setSoggettoFruitore(idS);
				
				return idPD;
			}
			else{
				//throw new CoreException("Mapping tra PD e Fruizione da parte del soggetto ["+idFruitore.toString()+"] del servizio ["+idServizio.toString()+"] non esistente");
				return null;
			}
		}catch(CoreException de){
			throw de;
		}
		catch(Exception e){
			throw new CoreException("getIDPortaDelegataAssociata error",e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}
	
	
	
	

	public static boolean existsIDPortaDelegataAssociata(IDServizio idServizio, IDSoggetto idFruitore,
			Connection con, String tipoDB) throws CoreException {
		return existsIDPortaDelegataAssociata(idServizio, idFruitore, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static boolean existsIDPortaDelegataAssociata(IDServizio idServizio, IDSoggetto idFruitore,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		long idFruizione = DBUtils.getIdFruizioneServizio(idServizio, idFruitore, con, tipoDB, tabellaSoggetti);
		if(idFruizione<=0){
			throw new CoreException("Fruizione da parte del soggetto ["+idFruitore.toString()+"] del servizio ["+idServizio.toString()+"] non esistente");
		}
		return _existsIDPortaDelegataAssociata(idFruizione, con, tipoDB, tabellaSoggetti);
	}
	
	private static boolean _existsIDPortaDelegataAssociata(long idFruizione,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {

			if(idFruizione<=0){
				throw new Exception("IdFruizione non fornita");
			}

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.MAPPING_FRUIZIONE_PD);
			sqlQueryObject.addWhereCondition("id_fruizione = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			int indexStmt = 1;
			stm.setLong(indexStmt++,idFruizione);
		
			rs = stm.executeQuery();
			return rs.next();
		}catch(CoreException de){
			throw de;
		}
		catch(Exception e){
			throw new CoreException("existsIDPortaDelegataAssociata error",e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}
	
	
	
	


	
	
	public static boolean existsMappingFruizione(IDServizio idServizio, IDSoggetto idFruitore, IDPortaDelegata idPortaDelegata,
			Connection con, String tipoDB) throws CoreException {
		return existsMappingFruizione(idServizio, idFruitore, idPortaDelegata, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static boolean existsMappingFruizione(IDServizio idServizio, IDSoggetto idFruitore, IDPortaDelegata idPortaDelegata,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		try {
			long id = getTableIdMappingFruizione(idServizio, idFruitore, idPortaDelegata, con, tipoDB);
			return id>0;
		}catch(Exception e) {
			return false;
		}
	}
	
	
	
	
	
	

	public static long getTableIdMappingFruizione(IDServizio idServizio, IDSoggetto idFruitore, IDPortaDelegata idPortaDelegata,
			Connection con, String tipoDB) throws CoreException {
		return getTableIdMappingFruizione(idServizio, idFruitore, idPortaDelegata, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static long getTableIdMappingFruizione(IDServizio idServizio, IDSoggetto idFruitore, IDPortaDelegata idPortaDelegata,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		long idFruizione = DBUtils.getIdFruizioneServizio(idServizio, idFruitore, con, tipoDB, tabellaSoggetti);
		if(idFruizione<=0){
			throw new CoreException("Fruizione da parte del soggetto ["+idFruitore.toString()+"] del servizio ["+idServizio.toString()+"] non esistente");
		}
		return _getTableIdMappingFruizione(idFruizione, idPortaDelegata, con, tipoDB, tabellaSoggetti);
	}
	
	private static long _getTableIdMappingFruizione(long idFruizione, IDPortaDelegata idPortaDelegata,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {

			if(idFruizione<=0){
				throw new Exception("IdFruizione non fornita");
			}
			long idPD = DBUtils.getIdPortaDelegata(idPortaDelegata.getNome(), con, tipoDB);
			if(idPD<=0){
				throw new Exception("PortaDelegata ["+idPortaDelegata+"] non esistente");
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.MAPPING_FRUIZIONE_PD);
			sqlQueryObject.addWhereCondition("id_fruizione=?");
			sqlQueryObject.addWhereCondition("id_porta=?");
			sqlQueryObject.setANDLogicOperator(true);
			String queryString = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(queryString);
			stm.setLong(1, idFruizione);
			stm.setLong(2, idPD);
			rs = stm.executeQuery();
			if (rs.next()) {
				return rs.getLong("id");
			}
			else{
				throw new CoreException("Mapping tra PD (id:"+idPD+") e Fruizione (id:"+idFruizione+") non esistente");
			}
		}catch(CoreException de){
			throw de;
		}
		catch(Exception e){
			throw new CoreException("getIDPortaApplicativaAssociata error",e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

		}
	} 
	
	public static boolean checkMappingFruizione(IDServizio idServizio, IDSoggetto idFruitore, IDPortaDelegata idPortaDelegata,boolean isDefault,
			Connection con, String tipoDB) throws CoreException {
		return checkMappingFruizione(idServizio, idFruitore, idPortaDelegata, isDefault, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static boolean checkMappingFruizione(IDServizio idServizio, IDSoggetto idFruitore, IDPortaDelegata idPortaDelegata,boolean isDefault,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		long idFruizione = DBUtils.getIdFruizioneServizio(idServizio, idFruitore, con, tipoDB, tabellaSoggetti);
		if(idFruizione<=0){
			throw new CoreException("Fruizione da parte del soggetto ["+idFruitore.toString()+"] del servizio ["+idServizio.toString()+"] non esistente");
		}
		return checkMappingFruizione(idFruizione, idPortaDelegata, isDefault, con, tipoDB, tabellaSoggetti);
	}
	
	private static boolean checkMappingFruizione(long idFruizione, IDPortaDelegata idPortaDelegata, boolean isDefault,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {

			if(idFruizione<=0){
				throw new Exception("IdFruizione non fornita");
			}
			long idPD = DBUtils.getIdPortaDelegata(idPortaDelegata.getNome(), con, tipoDB);
			if(idPD<=0){
				throw new Exception("PortaDelegata ["+idPortaDelegata+"] non esistente");
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.MAPPING_FRUIZIONE_PD);
			sqlQueryObject.addSelectCountField("id","mapping_count");
			sqlQueryObject.addWhereCondition("id_fruizione=?");
			sqlQueryObject.addWhereCondition("id_porta=?");
			sqlQueryObject.addWhereCondition("is_default = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String queryString = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(queryString);
			stm.setLong(1, idFruizione);
			stm.setLong(2, idPD);
			stm.setLong(3, (isDefault ? 1 : 0));
			rs = stm.executeQuery();
			int found = 0;
			if (rs.next()) {
				found = rs.getInt("mapping_count");
			}
			
			return found > 0;
		}catch(CoreException de){
			throw de;
		}
		catch(Exception e){
			throw new CoreException("getIDPortaApplicativaAssociata error",e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

		}
	} 
}
