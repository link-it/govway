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
	
	
	public static void createMappingErogazione(IDServizio idServizio, IDPortaApplicativa idPortaApplicativa,
			Connection con, String tipoDB) throws CoreException{
		createMappingErogazione(idServizio, idPortaApplicativa, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static void createMappingErogazione(IDServizio idServizio, IDPortaApplicativa idPortaApplicativa,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException{
		long idServizioLong = DBUtils.getIdAccordoServizioParteSpecifica(idServizio, con, tipoDB);
		if(idServizioLong<=0){
			throw new CoreException("Servizio ["+idServizio.toString()+"] non esistente");
		}
		_createMappingErogazione(idServizioLong, idPortaApplicativa, con, tipoDB, tabellaSoggetti);
	}
	
	private static void _createMappingErogazione(long idServizioLong, IDPortaApplicativa idPortaApplicativa,
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
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addInsertTable(CostantiDB.MAPPING_EROGAZIONE_PA);
			sqlQueryObject.addInsertField("id_erogazione", "?");
			sqlQueryObject.addInsertField("id_porta", "?");
			String queryString = sqlQueryObject.createSQLInsert();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idServizioLong);
			stmt.setLong(2, idPA);
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

	public static void createMappingFruizione(IDServizio idServizio, IDSoggetto idFruitore, IDPortaDelegata idPortaDelegata,
			Connection con, String tipoDB) throws CoreException{
		createMappingFruizione(idServizio, idFruitore, idPortaDelegata, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static void createMappingFruizione(IDServizio idServizio, IDSoggetto idFruitore, IDPortaDelegata idPortaDelegata,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException{
		long idFruizione = DBUtils.getIdFruizioneServizio(idServizio, idFruitore, con, tipoDB, tabellaSoggetti);
		if(idFruizione<=0){
			throw new CoreException("Fruizione da parte del soggetto ["+idFruitore.toString()+"] del servizio ["+idServizio.toString()+"] non esistente");
		}
		_createMappingFruizione(idFruizione, idPortaDelegata, con, tipoDB, tabellaSoggetti);
	}
	
	private static void _createMappingFruizione(long idFruizione, IDPortaDelegata idPortaDelegata,
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
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addInsertTable(CostantiDB.MAPPING_FRUIZIONE_PD);
			sqlQueryObject.addInsertField("id_fruizione", "?");
			sqlQueryObject.addInsertField("id_porta", "?");
			String queryString = sqlQueryObject.createSQLInsert();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idFruizione);
			stmt.setLong(2, idPD);
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
			sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".location");
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
	
}
