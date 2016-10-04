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



package org.openspcoop2.core.commons;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCParameterUtilities;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCSqlLogger;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;


/**
 * Funzioni di utilita utilizzate dai driver
 *
 * @author Stefano Corallo <corallo@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DBUtils {

	
	public static String estraiTipoDatabaseFromLocation(String location)throws CoreException{
		if(location==null){
			throw new CoreException("Location del db is null");
		}
		if(location.indexOf("@")==-1){
			throw new CoreException("Tipo di database non indicato nella location, sintassi corretta e' tipoDatabase@datasource");
		}
		String tipoDatabase = location.split("@")[0].trim();
		if( ! TipiDatabase.isAMember(tipoDatabase)){
			throw new CoreException("Tipo di database indicato nella location ["+tipoDatabase+"] non supportato");
		}
		return tipoDatabase;
	}
	

	
	
	public static List<List<Object>> readCustom(Logger log, Connection connection, String tipoDB,
			ISQLQueryObject sqlQueryObject, List<Class<?>> returnTypes, List<JDBCObject> paramTypes) throws CoreException
	{
		PreparedStatement stm = null;
		ResultSet rs = null;
		try
		{
			if(returnTypes==null || returnTypes.size()<=0){
				throw new CoreException("Non sono stati definiti tipi da ritornare");
			}
			
			List<List<Object>> lista = new ArrayList<List<Object>>();
			
			String sql = sqlQueryObject.createSQLQuery();
			stm=connection.prepareStatement(sql);
			JDBCParameterUtilities jdbcParameterUtilities = new JDBCParameterUtilities(TipiDatabase.toEnumConstant(tipoDB));
			JDBCObject [] paramsArray = null;
			if(paramTypes!=null && paramTypes.size()>0){
				paramsArray = paramTypes.toArray(new JDBCObject[1]);
			}
			jdbcParameterUtilities.setParameters(stm, paramsArray);
			
			JDBCSqlLogger sqlLogger = new JDBCSqlLogger(log);
			sqlLogger.infoSql(sql, paramsArray);
			
			rs=stm.executeQuery();

			while(rs.next()){
				
				List<Object> listaInterna = new ArrayList<Object>();
				
				for (int i = 0; i < returnTypes.size(); i++) {
					listaInterna.add(jdbcParameterUtilities.readParameter(rs, (i+1), returnTypes.get(i)));
				}
				
				lista.add(listaInterna);
			}

			return lista;

		}catch (SQLException e) {
			throw new CoreException(e);
		}catch (Exception e) {
			throw new CoreException(e);
		}finally
		{
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}
	
	
	
	
	/**
	 * Recupera l'id del soggetto in base al nome e al tipo passati come parametri
	 * @param nomeSoggetto
	 * @param tipoSoggetto
	 * @param con
	 * @return L'id del soggetto se esiste, altrimenti -1
	 * @throws CoreException 
	 */
	public static long getIdSoggetto(String nomeSoggetto, String tipoSoggetto,Connection con, String tipoDB) throws CoreException
	{
		return DBUtils.getIdSoggetto(nomeSoggetto, tipoSoggetto, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static long getIdSoggetto(String nomeSoggetto, String tipoSoggetto,Connection con, String tipoDB,String tabellaSoggetti) throws CoreException
	{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idSoggetto=-1;
		try
		{
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(tabellaSoggetti);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("tipo_soggetto = ?");
			sqlQueryObject.addWhereCondition("nome_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String query = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(query);
			stm.setString(1, tipoSoggetto);
			stm.setString(2, nomeSoggetto);

			rs=stm.executeQuery();

			if(rs.next()){
				idSoggetto = rs.getLong("id");
			}

			return idSoggetto;

		}catch (SQLException e) {
			throw new CoreException(e);
		}catch (Exception e) {
			throw new CoreException(e);
		}finally
		{
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}

	public static long getIdConnettore(String nomeConnettore,Connection con, String tipoDB) throws CoreException
	{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idConnettore=-1;
		try
		{
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome_connettore = ?");
			String query = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(query);
			stm.setString(1, nomeConnettore);
			rs=stm.executeQuery();

			if(rs.next()){
				idConnettore = rs.getLong("id");
			}

			return idConnettore;

		}catch (SQLException e) {
			throw new CoreException(e);
		}catch (Exception e) {
			throw new CoreException(e);
		}finally
		{
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}

	/**
	 * Recupero l'id del servizio
	 * @param nomeServizio
	 * @param tipoServizio
	 * @param nomeProprietario
	 * @param tipoProprietario
	 * @param con
	 * @return L'id del servizio se esiste, -1 altrimenti
	 * @throws CoreException TODO
	 */
	public static long getIdServizio(String nomeServizio, String tipoServizio,String nomeProprietario,String tipoProprietario,Connection con, String tipoDB) throws CoreException{
		return DBUtils.getIdServizio(nomeServizio,tipoServizio,nomeProprietario,tipoProprietario,con,false,tipoDB, CostantiDB.SOGGETTI);
	}
	public static long getIdServizio(String nomeServizio, String tipoServizio,String nomeProprietario,String tipoProprietario,Connection con, String tipoDB,String tabellaSoggetti) throws CoreException{
		return DBUtils.getIdServizio(nomeServizio,tipoServizio,nomeProprietario,tipoProprietario,con,false,tipoDB,tabellaSoggetti);
	}

	/**
	 * Recupero l'id del servizio
	 * @param nomeServizio
	 * @param tipoServizio
	 * @param nomeProprietario
	 * @param tipoProprietario
	 * @param con
	 * @return L'id del servizio se esiste, -1 altrimenti
	 * @throws CoreException TODO
	 */
	public static long getIdServizio(String nomeServizio, String tipoServizio,String nomeProprietario,String tipoProprietario,
			Connection con,boolean testServizioNonCorrelato,String tipoDB) throws CoreException
	{
		return DBUtils.getIdServizio(nomeServizio, tipoServizio, nomeProprietario, tipoProprietario, con, testServizioNonCorrelato, tipoDB, CostantiDB.SOGGETTI);
	}
	public static long getIdServizio(String nomeServizio, String tipoServizio,String nomeProprietario,String tipoProprietario,
			Connection con,boolean testServizioNonCorrelato,String tipoDB,String tabellaSoggetti) throws CoreException
	{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idSoggetto;
		long idServizio=0;
		try
		{
			idSoggetto = DBUtils.getIdSoggetto(nomeProprietario, tipoProprietario, con, tipoDB,tabellaSoggetti);

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("tipo_servizio = ?");
			sqlQueryObject.addWhereCondition("nome_servizio = ?");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String query = sqlQueryObject.createSQLQuery();
			if(testServizioNonCorrelato)
				query = query + " AND servizio_correlato=?";
//			probabile errore del merge
//			if(testServizioNonCorrelato)
//			query = query + " AND servizio_correlato=?";
			stm=con.prepareStatement(query);
			stm.setString(1, tipoServizio);
			stm.setString(2, nomeServizio);
			stm.setLong(3, idSoggetto);
			if(testServizioNonCorrelato)
				stm.setString(4, "disabilitato");

			rs=stm.executeQuery();

			if(rs.next()){
				idServizio = rs.getLong("id");
			}

			return idServizio;

		}catch (SQLException e) {
			throw new CoreException(e);
		}catch (Exception e) {
			throw new CoreException(e);
		}finally
		{
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}

	

	public static long getIdPortaApplicativa(String nomePorta, String tipoProprietario, String nomeProprietario,
			Connection con, String tipoDB) throws CoreException
	{
		return DBUtils.getIdPortaApplicativa(nomePorta, tipoProprietario, nomeProprietario, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static long getIdPortaApplicativa(String nomePorta, String tipoProprietario, String nomeProprietario,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException
	{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idSoggetto;
		long idPortaApplicativa=-1;

		try
		{
			idSoggetto = DBUtils.getIdSoggetto(nomeProprietario, tipoProprietario, con, tipoDB,tabellaSoggetti);

			//recupero l'id della porta applicativa appena inserita
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.addWhereCondition("nome_porta = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(sqlQuery);
			stm.setLong(1, idSoggetto);
			stm.setString(2, nomePorta);

			rs=stm.executeQuery();

			if(rs.next())
			{
				idPortaApplicativa=rs.getLong("id");
			}
			return idPortaApplicativa;
		}catch (SQLException e) {
			throw new CoreException(e);
		}catch (Exception e) {
			throw new CoreException(e);
		}finally
		{
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

		}

	}
	
	
	
	
	
	
	public static long getIdPortaDelegata(String nomePorta, String tipoProprietario, String nomeProprietario,
			Connection con, String tipoDB) throws CoreException
	{
		return DBUtils.getIdPortaDelegata(nomePorta, tipoProprietario, nomeProprietario, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static long getIdPortaDelegata(String nomePorta, String tipoProprietario, String nomeProprietario,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException
	{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idSoggetto;
		long idPortaDelegata=-1;

		try
		{
			idSoggetto = DBUtils.getIdSoggetto(nomeProprietario, tipoProprietario, con, tipoDB,tabellaSoggetti);

			//recupero l'id della porta applicativa appena inserita
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.addWhereCondition("nome_porta = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(sqlQuery);
			stm.setLong(1, idSoggetto);
			stm.setString(2, nomePorta);

			rs=stm.executeQuery();

			if(rs.next())
			{
				idPortaDelegata=rs.getLong("id");
			}
			return idPortaDelegata;
		}catch (SQLException e) {
			throw new CoreException(e);
		}catch (Exception e) {
			throw new CoreException(e);
		}finally
		{
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

		}

	}
	
	
	
	
	
	
	public static long getIdServizioApplicativo(String nomeServizioApplicativo, String tipoProprietario, String nomeProprietario,
			Connection con, String tipoDB) throws CoreException
	{
		return DBUtils.getIdServizioApplicativo(nomeServizioApplicativo, tipoProprietario, nomeProprietario, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static long getIdServizioApplicativo(String nomeServizioApplicativo, String tipoProprietario, String nomeProprietario,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException
	{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idSoggetto;
		long idServizioApplicativo=-1;

		try
		{
			idSoggetto = DBUtils.getIdSoggetto(nomeProprietario, tipoProprietario, con, tipoDB,tabellaSoggetti);

			//recupero l'id della porta applicativa appena inserita
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.addWhereCondition("nome = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(sqlQuery);
			stm.setLong(1, idSoggetto);
			stm.setString(2, nomeServizioApplicativo);

			rs=stm.executeQuery();

			if(rs.next())
			{
				idServizioApplicativo=rs.getLong("id");
			}
			return idServizioApplicativo;
		}catch (SQLException e) {
			throw new CoreException(e);
		}catch (Exception e) {
			throw new CoreException(e);
		}finally
		{
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

		}

	}

	
	
	
	
	
	public static long getIdPortaDominio(String nome, Connection con, String tipoDB) throws CoreException
	{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idPdD=-1;
		try
		{
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PDD);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String query = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(query);
			stm.setString(1, nome);

			rs=stm.executeQuery();

			if(rs.next()){
				idPdD = rs.getLong("id");
			}

			return idPdD;

		}catch (SQLException e) {
			throw new CoreException(e);
		}catch (Exception e) {
			throw new CoreException(e);
		}finally
		{
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}
	
	
	
	
	

	public static long getIdAccordo(String nomeServizio, String tipoServizio,String nomeProprietario,String tipoProprietario,Connection con, String tipoDB) throws CoreException
	{
		return DBUtils.getIdAccordo(nomeServizio,tipoServizio,nomeProprietario,tipoProprietario,con,tipoDB,CostantiDB.SOGGETTI);
	}
	public static long getIdAccordo(String nomeServizio, String tipoServizio,String nomeProprietario,String tipoProprietario,Connection con, String tipoDB,String tabellaSoggetti) throws CoreException
	{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idServizio;
		long idAccordo = -1;
		try
		{
			idServizio = DBUtils.getIdServizio(nomeServizio, tipoServizio, nomeProprietario, tipoProprietario, con, tipoDB,tabellaSoggetti);

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			String query = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(query);
			stm.setLong(1, idServizio);

			rs=stm.executeQuery();

			if(rs.next()){
				idAccordo = rs.getLong("id_accordo");
			}

			return idAccordo;

		}catch (SQLException e) {
			throw new CoreException(e);
		}catch (Exception e) {
			throw new CoreException(e);
		}finally
		{
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}
	
	public static long getIdPortType(Long idAccordo,String nomePortType,Connection con) throws CoreException{
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		long id=-1;
		try
		{
			String selectQuery = "SELECT id FROM " + CostantiDB.PORT_TYPE + " WHERE id_accordo = ? AND nome=?";
			selectStmt = con.prepareStatement(selectQuery);
			selectStmt.setLong(1, idAccordo);
			selectStmt.setString(2, nomePortType);
			selectRS = selectStmt.executeQuery();
			if (selectRS.next()) {
				id = selectRS.getLong("id");	
			}
			selectRS.close();
			selectStmt.close();
			return id;
		}catch (Exception e) {
			throw new CoreException(e);
		}finally
		{
			//Chiudo statement and resultset
			try{
				if(selectRS!=null) selectRS.close();
				if(selectStmt!=null) selectStmt.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}
	
	public static long getIdAccordoServizioParteComune(IDAccordo idAccordo,Connection con, String tipoDB) throws CoreException{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idAccordoLong=-1;
		try
		{
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome = ?");
			sqlQueryObject.addWhereCondition("id_referente = ?");
			if(AccordiUtils.versioneNonDefinita(idAccordo.getVersione()))
				sqlQueryObject.addWhereCondition(false,"versione = ?","versione is null");
			else
				sqlQueryObject.addWhereCondition("versione = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String query = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(query);
			stm.setString(1, idAccordo.getNome());
			
			long idSoggettoReferente =  AccordiUtils.SOGGETTO_REFERENTE_DEFAULT;
			if(idAccordo.getSoggettoReferente()!=null){
				idSoggettoReferente = DBUtils.getIdSoggetto(idAccordo.getSoggettoReferente().getNome(), idAccordo.getSoggettoReferente().getTipo(), con, tipoDB);
				if(idSoggettoReferente<=0){
					throw new CoreException("[getIdAccordoServizioParteComune] Soggetto Referente ["+idAccordo.getSoggettoReferente().toString()+"] non esiste");
				}
			}
			stm.setLong(2, idSoggettoReferente);
				
			String vers = AccordiUtils.VERSIONE_DEFAULT;
			if(idAccordo.getVersione()!=null)
				vers = idAccordo.getVersione();
			stm.setString(3, vers);
						
			rs=stm.executeQuery();

			if(rs.next()){
				idAccordoLong = rs.getLong("id");
			}

			return idAccordoLong;

		}catch (SQLException e) {
			throw new CoreException(e);
		}catch (Exception e) {
			throw new CoreException(e);
		}finally
		{
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}
	
	
	
	
	
	public static long getIdAccordoCooperazione(IDAccordoCooperazione idAccordo,Connection con, String tipoDB) throws CoreException{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idAccordoLong=-1;
		try
		{
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome = ?");
			if(AccordiUtils.versioneNonDefinita(idAccordo.getVersione()))
				sqlQueryObject.addWhereCondition(false,"versione = ?","versione is null");
			else
				sqlQueryObject.addWhereCondition("versione = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String query = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(query);
			stm.setString(1, idAccordo.getNome());
				
			String vers = AccordiUtils.VERSIONE_DEFAULT;
			if(idAccordo.getVersione()!=null)
				vers = idAccordo.getVersione();
			stm.setString(2, vers);
						
			rs=stm.executeQuery();

			if(rs.next()){
				idAccordoLong = rs.getLong("id");
			}

			return idAccordoLong;

		}catch (SQLException e) {
			throw new CoreException(e);
		}catch (Exception e) {
			throw new CoreException(e);
		}finally
		{
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}
	
	public static long getIdAccordoServizioParteSpecifica(IDAccordo idAccordo,Connection con, String tipoDB) throws CoreException{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idAccordoLong=-1;
		try
		{
			
			// NOTA: nell'APS, il soggetto e la versione sono obbligatori
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("aps_nome = ?");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.addWhereCondition("aps_versione = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String query = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(query);
			stm.setString(1, idAccordo.getNome());
			
			long idSoggettoReferente =  DBUtils.getIdSoggetto(idAccordo.getSoggettoReferente().getNome(), idAccordo.getSoggettoReferente().getTipo(), con, tipoDB);
			if(idSoggettoReferente<=0){
				throw new CoreException("[getIdAccordoServizio] Soggetto Referente ["+idAccordo.getSoggettoReferente().toString()+"] non esiste");
			}
			stm.setLong(2, idSoggettoReferente);
				
			stm.setString(3, idAccordo.getVersione());
						
			rs=stm.executeQuery();

			if(rs.next()){
				idAccordoLong = rs.getLong("id");
			}

			return idAccordoLong;

		}catch (SQLException e) {
			throw new CoreException(e);
		}catch (Exception e) {
			throw new CoreException(e);
		}finally
		{
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}
	
	public static long getIdAccordoServizioParteSpecifica(IDServizio idServizio,Connection con, String tipoDB) throws CoreException{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idAccordoLong=-1;
		try
		{
			
			// NOTA: nell'APS, il soggetto e la versione sono obbligatori
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.addWhereCondition("tipo_soggetto = ?");
			sqlQueryObject.addWhereCondition("nome_soggetto = ?");
			sqlQueryObject.addWhereCondition("tipo_servizio = ?");
			sqlQueryObject.addWhereCondition("nome_servizio = ?");
			String query = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(query);
			stm.setString(1, idServizio.getSoggettoErogatore().getTipo());
			stm.setString(2, idServizio.getSoggettoErogatore().getNome());
			stm.setString(3, idServizio.getTipoServizio());
			stm.setString(4, idServizio.getServizio());
			
			rs=stm.executeQuery();

			if(rs.next()){
				idAccordoLong = rs.getLong("id");
			}

			return idAccordoLong;

		}catch (SQLException e) {
			throw new CoreException(e);
		}catch (Exception e) {
			throw new CoreException(e);
		}finally
		{
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}
	

	public static long getIdDocumento(String nome, String tipo, String ruolo, long idProprietario,Connection con,String tipoDB,String tipoProprietario) throws CoreException{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idDoc=-1;
		try
		{
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("id_proprietario = ?");
			sqlQueryObject.addWhereCondition("nome = ?");
			sqlQueryObject.addWhereCondition("tipo = ?");
			sqlQueryObject.addWhereCondition("ruolo = ?");
			sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, idProprietario);
			stm.setString(2, nome);
			stm.setString(3, tipo);
			stm.setString(4, ruolo);
			stm.setString(5, tipoProprietario);
			rs = stm.executeQuery();
			if (rs.next())
				idDoc = rs.getLong("id");
			rs.close();
			stm.close();

			return idDoc;
		}catch (SQLException e) {
			throw new CoreException(e);
		}catch (Exception e) {
			throw new CoreException(e);
		}finally
		{
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}
	
	
	
	
	
	
	

	/**
	 * Utility per formattare la string sql con i parametri passati, e stamparla per debug
	 * @param sql la string sql utilizzata nel prepared statement
	 * @param params i parametri da inserire nella stringa che sostituiranno i '?'
	 * @return La stringa sql con al posto dei '?' ha i parametri passati
	 */
	public static String formatSQLString(String sql,Object ... params)
	{
		String res = sql;

		for (int i = 0; i < params.length; i++)
		{
			res=res.replaceFirst("\\?", "{"+i+"}");
		}

		return MessageFormat.format(res, params);

	}


}


