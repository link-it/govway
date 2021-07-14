/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCParameterUtilities;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCSqlLogger;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.jdbc.JDBCAdapterException;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.LikeConfig;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;
import org.slf4j.Logger;


/**
 * Funzioni di utilita utilizzate dai driver
 *
 * @author Stefano Corallo (corallo@link.it)
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
	 */
	public static long getIdServizio(String nomeServizio, String tipoServizio,Integer versioneServizio, String nomeSoggettoErogatore,String tipoSoggettoErogatore,Connection con, String tipoDB) throws CoreException{
		return DBUtils.getIdServizio(nomeServizio,tipoServizio,versioneServizio,nomeSoggettoErogatore,tipoSoggettoErogatore,con,false,tipoDB, CostantiDB.SOGGETTI);
	}
	public static long getIdServizio(String nomeServizio, String tipoServizio,Integer versioneServizio, String nomeSoggettoErogatore,String tipoSoggettoErogatore,Connection con, String tipoDB,String tabellaSoggetti) throws CoreException{
		return DBUtils.getIdServizio(nomeServizio,tipoServizio,versioneServizio,nomeSoggettoErogatore,tipoSoggettoErogatore,con,false,tipoDB,tabellaSoggetti);
	}
	public static long getIdServizio(String nomeServizio, String tipoServizio,Integer versioneServizio, long idSoggetto,Connection con, String tipoDB) throws CoreException{
		return DBUtils.getIdServizio(nomeServizio,tipoServizio,versioneServizio,idSoggetto,con,false,tipoDB, CostantiDB.SOGGETTI);
	}
	public static long getIdServizio(String nomeServizio, String tipoServizio,Integer versioneServizio, long idSoggetto,Connection con, String tipoDB,String tabellaSoggetti) throws CoreException{
		return DBUtils.getIdServizio(nomeServizio,tipoServizio,versioneServizio,idSoggetto,con,false,tipoDB,tabellaSoggetti);
	}

	/**
	 * Recupero l'id del servizio
	 */
	public static long getIdServizio(String nomeServizio, String tipoServizio,Integer versioneServizio, String nomeSoggettoErogatore,String tipoSoggettoErogatore,
			Connection con,boolean testServizioNonCorrelato,String tipoDB) throws CoreException
	{
		return DBUtils.getIdServizio(nomeServizio, tipoServizio, versioneServizio, nomeSoggettoErogatore, tipoSoggettoErogatore, con, testServizioNonCorrelato, tipoDB, CostantiDB.SOGGETTI);
	}
	public static long getIdServizio(String nomeServizio, String tipoServizio,Integer versioneServizio, String nomeSoggettoErogatore,String tipoSoggettoErogatore,
			Connection con,boolean testServizioNonCorrelato,String tipoDB,String tabellaSoggetti) throws CoreException
	{
		long idSoggetto = DBUtils.getIdSoggetto(nomeSoggettoErogatore, tipoSoggettoErogatore, con, tipoDB,tabellaSoggetti);
		return getIdServizio(nomeServizio, tipoServizio, versioneServizio, idSoggetto,
				con, testServizioNonCorrelato, tipoDB, tabellaSoggetti);
	}
	public static long getIdServizio(String nomeServizio, String tipoServizio,Integer versioneServizio, long idSoggetto,
			Connection con,boolean testServizioNonCorrelato,String tipoDB) throws CoreException
	{
		return  getIdServizio(nomeServizio, tipoServizio, versioneServizio, idSoggetto,
				con,testServizioNonCorrelato,tipoDB,CostantiDB.SOGGETTI);
	}
	public static long getIdServizio(String nomeServizio, String tipoServizio,Integer versioneServizio, long idSoggetto,
			Connection con,boolean testServizioNonCorrelato,String tipoDB,String tabellaSoggetti) throws CoreException
	{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idServizio=0;
		try
		{
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.addWhereCondition("tipo_servizio = ?");
			sqlQueryObject.addWhereCondition("nome_servizio = ?");
			sqlQueryObject.addWhereCondition("versione_servizio = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String query = sqlQueryObject.createSQLQuery();
			if(testServizioNonCorrelato)
				query = query + " AND servizio_correlato=?";
//			probabile errore del merge
//			if(testServizioNonCorrelato)
//			query = query + " AND servizio_correlato=?";
			stm=con.prepareStatement(query);
			stm.setLong(1, idSoggetto);
			stm.setString(2, tipoServizio);
			stm.setString(3, nomeServizio);
			stm.setInt(4, versioneServizio);
			if(testServizioNonCorrelato)
				stm.setString(5, "disabilitato");

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

	

	public static long getIdPortaApplicativa(String nomePorta, Connection con, String tipoDB) throws CoreException
	{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idPortaApplicativa=-1;

		try
		{
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("nome_porta = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(sqlQuery);
			stm.setString(1, nomePorta);

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
	
	
	
	
	
	
	public static long getIdPortaDelegata(String nomePorta, Connection con, String tipoDB) throws CoreException
	{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idPortaDelegata=-1;

		try
		{
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("nome_porta = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(sqlQuery);
			stm.setString(1, nomePorta);

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
	
	
	
	
	
	public static long getIdGruppo(IDGruppo idGruppo, Connection con, String tipoDB) throws CoreException
	{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idGruppoLong=-1;
		try
		{
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.GRUPPI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String query = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(query);
			stm.setString(1, idGruppo.getNome());

			rs=stm.executeQuery();

			if(rs.next()){
				idGruppoLong = rs.getLong("id");
			}

			return idGruppoLong;

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
	
	
	
	
	
	
	public static long getIdRuolo(IDRuolo idRuolo, Connection con, String tipoDB) throws CoreException
	{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idRuoloLong=-1;
		try
		{
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.RUOLI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String query = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(query);
			stm.setString(1, idRuolo.getNome());

			rs=stm.executeQuery();

			if(rs.next()){
				idRuoloLong = rs.getLong("id");
			}

			return idRuoloLong;

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
	
	
	
	
	public static long getIdScope(IDScope idScope, Connection con, String tipoDB) throws CoreException
	{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idScopeLong=-1;
		try
		{
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SCOPE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String query = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(query);
			stm.setString(1, idScope.getNome());

			rs=stm.executeQuery();

			if(rs.next()){
				idScopeLong = rs.getLong("id");
			}

			return idScopeLong;

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
	
	
	
	
	

	public static long getIdAccordoServizioParteComune(String nomeServizio, String tipoServizio,Integer versioneServizio, String nomeSoggettoErogatore,String tipoSoggettoErogatore,Connection con, String tipoDB) throws CoreException
	{
		return DBUtils.getIdAccordoServizioParteComune(nomeServizio,tipoServizio,versioneServizio, nomeSoggettoErogatore,tipoSoggettoErogatore,con,tipoDB,CostantiDB.SOGGETTI);
	}
	public static long getIdAccordoServizioParteComune(String nomeServizio, String tipoServizio,Integer versioneServizio, String nomeSoggettoErogatore,String tipoSoggettoErogatore,Connection con, String tipoDB,String tabellaSoggetti) throws CoreException
	{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idServizio;
		long idAccordo = -1;
		try
		{
			idServizio = DBUtils.getIdServizio(nomeServizio, tipoServizio, versioneServizio, nomeSoggettoErogatore, tipoSoggettoErogatore, con, tipoDB,tabellaSoggetti);

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
	
	public static long getIdResource(Long idAccordo,String nomeRisorsa,Connection con) throws CoreException{
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		long id=-1;
		try
		{
			String selectQuery = "SELECT id FROM " + CostantiDB.API_RESOURCES + " WHERE id_accordo = ? AND nome=?";
			selectStmt = con.prepareStatement(selectQuery);
			selectStmt.setLong(1, idAccordo);
			selectStmt.setString(2, nomeRisorsa);
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
			sqlQueryObject.addWhereCondition("versione = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String query = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(query);
			stm.setString(1, idAccordo.getNome());
			
			long idSoggettoReferente =  0;
			if(idAccordo.getSoggettoReferente()!=null){
				idSoggettoReferente = DBUtils.getIdSoggetto(idAccordo.getSoggettoReferente().getNome(), idAccordo.getSoggettoReferente().getTipo(), con, tipoDB);
				if(idSoggettoReferente<=0){
					throw new CoreException("[getIdAccordoServizioParteComune] Soggetto Referente ["+idAccordo.getSoggettoReferente().toString()+"] non esiste");
				}
			}
			stm.setLong(2, idSoggettoReferente);
				
			stm.setInt(3, idAccordo.getVersione());
						
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
	
	public static int getAccordoServizioParteComuneNextVersion(IDAccordo idAccordo,Connection con, String tipoDB) throws CoreException{
		PreparedStatement stm = null;
		ResultSet rs = null;
		try
		{
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addSelectField("versione");
			sqlQueryObject.addWhereCondition("nome = ?");
			sqlQueryObject.addWhereCondition("id_referente = ?");
			sqlQueryObject.addOrderBy("versione");
			sqlQueryObject.setSortType(false);
			sqlQueryObject.setANDLogicOperator(true);
			String query = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(query);
			stm.setString(1, idAccordo.getNome());
			
			long idSoggettoReferente =  0;
			if(idAccordo.getSoggettoReferente()!=null){
				idSoggettoReferente = DBUtils.getIdSoggetto(idAccordo.getSoggettoReferente().getNome(), idAccordo.getSoggettoReferente().getTipo(), con, tipoDB);
				if(idSoggettoReferente<=0){
					throw new CoreException("[getIdAccordoServizioParteComune] Soggetto Referente ["+idAccordo.getSoggettoReferente().toString()+"] non esiste");
				}
			}
			stm.setLong(2, idSoggettoReferente);
				
			rs=stm.executeQuery();

			if(rs.next()){
				int versione = rs.getInt("versione");
				if(versione>0) {
					return versione+1;
				}
			}

			return 1; // accordo non esistente

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
			sqlQueryObject.addWhereCondition("id_referente = ?");
			sqlQueryObject.addWhereCondition("versione = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String query = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(query);
			stm.setString(1, idAccordo.getNome());
				
			long idSoggettoReferente =  0;
			if(idAccordo.getSoggettoReferente()!=null){
				idSoggettoReferente = DBUtils.getIdSoggetto(idAccordo.getSoggettoReferente().getNome(), idAccordo.getSoggettoReferente().getTipo(), con, tipoDB);
				if(idSoggettoReferente<=0){
					throw new CoreException("[getIdAccordoCooperazione] Soggetto Referente ["+idAccordo.getSoggettoReferente().toString()+"] non esiste");
				}
			}
			stm.setLong(2, idSoggettoReferente);
				
			stm.setInt(3, idAccordo.getVersione());
						
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
			sqlQueryObject.addWhereCondition("versione_servizio = ?");
			String query = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(query);
			stm.setString(1, idServizio.getSoggettoErogatore().getTipo());
			stm.setString(2, idServizio.getSoggettoErogatore().getNome());
			stm.setString(3, idServizio.getTipo());
			stm.setString(4, idServizio.getNome());
			stm.setInt(5, idServizio.getVersione());
			
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
	
	
	public static long getIdFruizioneServizio(IDServizio idServizio,IDSoggetto idFruitore, Connection con, String tipoDB) throws CoreException{
		return getIdFruizioneServizio(idServizio, idFruitore, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static long getIdFruizioneServizio(IDServizio idServizio,IDSoggetto idFruitore, Connection con, String tipoDB, String tabellaSoggetti) throws CoreException{
		return _getIdFruizioneServizio(DBUtils.getIdAccordoServizioParteSpecifica(idServizio, con, tipoDB), idFruitore, con, tipoDB, tabellaSoggetti);
	}
	
	private static long _getIdFruizioneServizio(long idServizio,IDSoggetto idFruitore, Connection con, String tipoDB, String tabellaSoggetti) throws CoreException{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idFruizione=-1;
		try
		{
			
			// NOTA: nell'APS, il soggetto e la versione sono obbligatori
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_servizio = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_soggetto = ?");
			String query = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(query);
			stm.setLong(1, idServizio);
			stm.setLong(2, DBUtils.getIdSoggetto(idFruitore.getNome(), idFruitore.getTipo(), con, tipoDB,tabellaSoggetti));
			
			rs=stm.executeQuery();

			if(rs.next()){
				idFruizione = rs.getLong("id");
			}

			return idFruizione;

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
			if(tipo!=null) {
				sqlQueryObject.addWhereCondition("tipo = ?");
			}
			if(ruolo!=null) {
				sqlQueryObject.addWhereCondition("ruolo = ?");
			}
			sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			int index = 1;
			stm.setLong(index++, idProprietario);
			stm.setString(index++, nome);
			if(tipo!=null) {
				stm.setString(index++, tipo);
			}
			if(ruolo!=null) {
				stm.setString(index++, ruolo);
			}
			stm.setString(index++, tipoProprietario);
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
	
	
	public static long getIdProtocolProperty(String tipoProprietario, long idProprietario,String nome, Connection con,String tipoDB) throws CoreException{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idPP=-1;
		try
		{
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PROTOCOL_PROPERTIES);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
			sqlQueryObject.addWhereCondition("id_proprietario = ?");
			sqlQueryObject.addWhereCondition("name = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setString(1, tipoProprietario);
			stm.setLong(2, idProprietario);
			stm.setString(3, nome);
			rs = stm.executeQuery();
			if (rs.next())
				idPP = rs.getLong("id");
			rs.close();
			stm.close();

			return idPP;
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
	
	
	
	
	
	public static long getIdRegistroPlugin(String nome, Connection con,String tipoDB) throws CoreException{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idRP=-1;
		try
		{
			if(nome==null) {
				throw new CoreException("Nome non fornito");
			}
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.REGISTRO_PLUGINS);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("nome=?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setString(1, nome);
			rs = stm.executeQuery();
			if(rs.next()) {
				idRP = rs.getLong("id");
			}
			rs.close();
			stm.close();
			rs=null;
			stm=null;
			
			return idRP;
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
	
	
	public static long getIdPlugin(String className, String label, String tipoPlugin, String tipo, Connection con,String tipoDB) throws CoreException{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idRP=-1;
		try
		{
			if(className==null) {
				throw new CoreException("ClassName non fornito");
			}
			if(label==null) {
				throw new CoreException("Label non fornito");
			}
			if(tipoPlugin==null) {
				throw new CoreException("TipoPlugin non fornito");
			}
			if(tipo==null) {
				throw new CoreException("Tipo non fornito");
			}
			
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.REGISTRO_CLASSI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("class_name=?");
			sqlQueryObject.addWhereCondition("label=?");
			sqlQueryObject.addWhereCondition("tipo_plugin=?");
			sqlQueryObject.addWhereCondition("tipo=?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setString(1, className);
			stm.setString(2, label);
			stm.setString(3, tipoPlugin);
			stm.setString(4, tipo);
			rs = stm.executeQuery();
			if(rs.next()) {
				idRP = rs.getLong("id");
			}
			rs.close();
			stm.close();
			rs=null;
			stm=null;
			
			return idRP;
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
	
	
	public static long getUrlInvocazioneRegola(String nome, Connection con,String tipoDB) throws CoreException{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idRP=-1;
		try
		{
			if(nome==null) {
				throw new CoreException("Nome non fornito");
			}
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_URL_REGOLE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("nome=?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setString(1, nome);
			rs = stm.executeQuery();
			if(rs.next()) {
				idRP = rs.getLong("id");
			}
			rs.close();
			stm.close();
			rs=null;
			stm=null;
			
			return idRP;
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
	
	

	public static void setPropertiesForSearch(ISQLQueryObject sqlQueryObject, Map<String, String> properties, String tabellaPadre, 
			String tabellaProprieta, String columnName, String columnValue, String columnFK) throws SQLQueryObjectException{
		if(properties!=null && properties.size()>0){
			String [] conditions = new String[properties.size()];
			int i = 0;
			for (String nome : properties.keySet()) {

				String aliasTabella = "pp"+i+tabellaPadre;
				sqlQueryObject.addFromTable(tabellaProprieta, aliasTabella);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addWhereCondition(aliasTabella+"."+columnFK+"="+tabellaPadre+".id");
				String valore = properties.get(nome);
								
				if(conditions[i]!=null){
					conditions[i] = conditions[i] + " AND ";
				}
				else {
					conditions[i] = "";
				}
				conditions[i] = conditions[i] + " " + aliasTabella+"."+columnName+"=?";
				
				if(valore!=null){
					if(conditions[i]!=null){
						conditions[i] = conditions[i] + " AND ";
					}
					else {
						conditions[i] = "";
					}
					conditions[i] = conditions[i] + " " + aliasTabella+"."+columnValue+"=?";
				}
				else {
					if(conditions[i]!=null){
						conditions[i] = conditions[i] + " AND ";
					}
					else {
						conditions[i] = "";
					}
					conditions[i] = conditions[i] + " " + aliasTabella+"."+columnValue+" is null";
				}
				
				// casoSpecialeValoreNull
				ISQLQueryObject sqlQueryObjectPropertyNotExists = null;
				// in un caso dove il valore non e' definito nel database ci possono essere due casistiche:
				// 1) Passando via govwayConsole, la proprieta' esiste con il nome ('name') ed e' valorizzata null in tutte le colonne (value_string,value_number,value_boolean)
				// 2) Passando via govwayLoader, in una configurazione xml, non si definisce la proprietà senza il valore, quindi la riga con il nome non esistera proprio nel db.
				if(valore==null){
					
					ISQLQueryObject sqlQueryObjectPropertyNotExistsInternal = sqlQueryObject.newSQLQueryObject();
					String aliasTabellaNotExists =  "not_exists_"+aliasTabella;
					sqlQueryObjectPropertyNotExistsInternal.addFromTable(tabellaProprieta, aliasTabellaNotExists);
					sqlQueryObjectPropertyNotExistsInternal.addSelectField(aliasTabellaNotExists, "id");
					sqlQueryObjectPropertyNotExistsInternal.addWhereCondition(aliasTabellaNotExists+"."+columnFK+"="+aliasTabella+"."+columnFK);
					sqlQueryObjectPropertyNotExistsInternal.addWhereCondition(aliasTabellaNotExists+"."+columnName+"=?");
					sqlQueryObjectPropertyNotExistsInternal.setANDLogicOperator(true);
					
					sqlQueryObjectPropertyNotExists = sqlQueryObject.newSQLQueryObject();
					sqlQueryObjectPropertyNotExists.addWhereExistsCondition(true, sqlQueryObjectPropertyNotExistsInternal);

					conditions[i] = "( " + conditions[i] + " ) OR ( " + sqlQueryObjectPropertyNotExists.createSQLConditions() + " )";
				}
				i++;
			}
			sqlQueryObject.addWhereCondition(true, conditions);
		}
	}
	
	public static void setPropertiesForSearch(PreparedStatement stmt, int index, 
			Map<String, String> properties,
			String tipoDatabase, Logger log) throws SQLQueryObjectException, SQLException, JDBCAdapterException, UtilsException{
		
		JDBCParameterUtilities jdbcParameterUtilities = new JDBCParameterUtilities(TipiDatabase.toEnumConstant(tipoDatabase));
		
		if(properties!=null && properties.size()>0){
			int i = 0;
			for (String nome : properties.keySet()) {
				
				String valore = properties.get(nome);
				
				log.debug("Proprieta["+i+"] nome stmt.setString("+nome+")");
				stmt.setString(index++, nome);
								
				if(valore!=null){
					log.debug("Proprieta["+i+"] valore stmt.setString("+valore+")");
					jdbcParameterUtilities.setParameter(stmt, index++, valore, String.class);
				}
				
				// casoSpecialeValoreNull
				// in un caso dove il valore non e' definito nel database ci possono essere due casistiche:
				// 1) Passando via govwayConsole, la proprieta' esiste con il nome ('name') ed e' valorizzata null in tutte le colonne (value_string,value_number,value_boolean)
				// 2) Passando via govwayLoader, in una configurazione xml, non si definisce la proprietà senza il valore, quindi la riga con il nome non esistera proprio nel db.
				if(valore==null){
					log.debug("Proprieta["+i+"] nome stmt.setString("+nome+")");
					stmt.setString(index++, nome);
				}
				
				i++;
			}
		}
	}


	public static void setFiltriConnettoreApplicativo(ISQLQueryObject sqlQueryObject, String tipoDB,
			TipiConnettore tipoConnettore, String endpointType, boolean tipoConnettoreIntegrationManager,
			String filtroConnettoreTokenPolicy, String filtroConnettoreEndpoint, String filtroConnettoreKeystore) throws Exception {

		ISQLQueryObject sql = SQLObjectFactory.createSQLQueryObject(tipoDB);
		sql.addFromTable(CostantiDB.CONNETTORI);
		sql.setANDLogicOperator(true);
		sql.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_connettore_inv="+CostantiDB.CONNETTORI+".id");
		setFiltriConnettore(sql, tipoDB,
				tipoConnettore, endpointType, tipoConnettoreIntegrationManager,
				filtroConnettoreTokenPolicy, filtroConnettoreEndpoint, filtroConnettoreKeystore,
				CostantiDB.SERVIZI_APPLICATIVI);
		sqlQueryObject.addWhereExistsCondition(false, sql);
		
	}
	public static void setFiltriConnettoreErogazione(ISQLQueryObject sqlQueryObject, String tipoDB,
			TipiConnettore tipoConnettore, String endpointType, boolean tipoConnettoreIntegrationManager,
			String filtroConnettoreTokenPolicy, String filtroConnettoreEndpoint, String filtroConnettoreKeystore) throws Exception {

		String aliasMAPPING_EROGAZIONE_PA = "c_map";
		String aliasPORTE_APPLICATIVE = "c_pa";
		String aliasPORTE_APPLICATIVE_SA = "c_pasa";
		String aliasSERVIZI_APPLICATIVI = "c_sa";
		
		ISQLQueryObject sql = SQLObjectFactory.createSQLQueryObject(tipoDB);
		sql.addFromTable(CostantiDB.MAPPING_EROGAZIONE_PA, aliasMAPPING_EROGAZIONE_PA);
		sql.addFromTable(CostantiDB.PORTE_APPLICATIVE, aliasPORTE_APPLICATIVE);
		sql.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA, aliasPORTE_APPLICATIVE_SA);
		sql.addFromTable(CostantiDB.SERVIZI_APPLICATIVI, aliasSERVIZI_APPLICATIVI);
		sql.addFromTable(CostantiDB.CONNETTORI);
		sql.setANDLogicOperator(true);
		sql.addWhereCondition(aliasMAPPING_EROGAZIONE_PA+".id_erogazione="+CostantiDB.SERVIZI+".id");
		sql.addWhereCondition(aliasMAPPING_EROGAZIONE_PA+".id_porta="+aliasPORTE_APPLICATIVE+".id");
		sql.addWhereCondition(aliasPORTE_APPLICATIVE_SA+".id_porta="+aliasPORTE_APPLICATIVE+".id");
		sql.addWhereCondition(aliasPORTE_APPLICATIVE_SA+".id_servizio_applicativo="+aliasSERVIZI_APPLICATIVI+".id");
		sql.addWhereCondition(aliasSERVIZI_APPLICATIVI+".id_connettore_inv="+CostantiDB.CONNETTORI+".id");
		setFiltriConnettore(sql, tipoDB,
				tipoConnettore, endpointType, tipoConnettoreIntegrationManager,
				filtroConnettoreTokenPolicy, filtroConnettoreEndpoint, filtroConnettoreKeystore,
				aliasSERVIZI_APPLICATIVI);
		sqlQueryObject.addWhereExistsCondition(false, sql);
		
	}
	public static void setFiltriConnettoreFruizione(ISQLQueryObject sqlQueryObject, String tipoDB,
			TipiConnettore tipoConnettore, String endpointType, boolean tipoConnettoreIntegrationManager,
			String filtroConnettoreTokenPolicy, String filtroConnettoreEndpoint, String filtroConnettoreKeystore) throws Exception {
		
		ISQLQueryObject sqlConnettoreDefault = SQLObjectFactory.createSQLQueryObject(tipoDB);
		sqlConnettoreDefault.addFromTable(CostantiDB.CONNETTORI);
		sqlConnettoreDefault.setANDLogicOperator(true);
		sqlConnettoreDefault.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_connettore="+CostantiDB.CONNETTORI+".id");
		setFiltriConnettore(sqlConnettoreDefault, tipoDB,
				tipoConnettore, endpointType, false,
				filtroConnettoreTokenPolicy, filtroConnettoreEndpoint, filtroConnettoreKeystore,
				null);
		
		ISQLQueryObject sqlGruppiConnettoreRidefinito = SQLObjectFactory.createSQLQueryObject(tipoDB);
		sqlGruppiConnettoreRidefinito.addFromTable(CostantiDB.SERVIZI_FRUITORI_AZIONI);
		sqlGruppiConnettoreRidefinito.addFromTable(CostantiDB.CONNETTORI);
		sqlGruppiConnettoreRidefinito.setANDLogicOperator(true);
		sqlGruppiConnettoreRidefinito.addWhereCondition(CostantiDB.SERVIZI_FRUITORI_AZIONI+".id_fruizione="+CostantiDB.SERVIZI_FRUITORI+".id");
		sqlGruppiConnettoreRidefinito.addWhereCondition(CostantiDB.SERVIZI_FRUITORI_AZIONI+".id_connettore="+CostantiDB.CONNETTORI+".id");
		setFiltriConnettore(sqlGruppiConnettoreRidefinito, tipoDB,
				tipoConnettore, endpointType, false,
				filtroConnettoreTokenPolicy, filtroConnettoreEndpoint, filtroConnettoreKeystore,
				null);
		
		sqlQueryObject.addWhereCondition(false, 
				sqlConnettoreDefault.getWhereExistsCondition(false, sqlConnettoreDefault),
				sqlConnettoreDefault.getWhereExistsCondition(false, sqlGruppiConnettoreRidefinito));
		
	}
	private static void setFiltriConnettore(ISQLQueryObject sqlQueryObject, String tipoDB,
			TipiConnettore tipoConnettore, String endpointType, boolean tipoConnettoreIntegrationManager,
			String filtroConnettoreTokenPolicy, String filtroConnettoreEndpoint, String filtroConnettoreKeystore,
			String aliasTabellaServiziApplicativi) throws Exception {
		
		// NOTA: logica inserita anche in PorteApplicativeHelper.applicaFiltriRicercaConnettoriMultipli
		
		if(endpointType!=null) {
			sqlQueryObject.addWhereLikeCondition(CostantiDB.CONNETTORI+".endpointtype", endpointType, false, false, false);
		}
		else if(tipoConnettore!=null) {
			if(TipiConnettore.CUSTOM.equals(tipoConnettore)) {
				List<String> tipiConosciuti = new ArrayList<String>();
				TipiConnettore[] tipi = TipiConnettore.values();
				for (TipiConnettore tipiConnettore : tipi) {
					tipiConosciuti.add(tipiConnettore.getNome());
				}
				ISQLQueryObject sql = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sql.addFromTable(CostantiDB.CONNETTORI);
				sql.setANDLogicOperator(true);
				sql.setNOTBeforeConditions(true);
				sql.addWhereINCondition(CostantiDB.CONNETTORI+".endpointtype", true, tipiConosciuti.toArray(new String[1]));
				sqlQueryObject.addWhereCondition(sql.createSQLConditions());
			}
		}
		
		if(tipoConnettoreIntegrationManager) {
			sqlQueryObject.addWhereLikeCondition(aliasTabellaServiziApplicativi+".getmsginv", "abilitato", false, false, false);
		}
		
		if(filtroConnettoreTokenPolicy!=null) {
			sqlQueryObject.addWhereLikeCondition(CostantiDB.CONNETTORI+".token_policy", filtroConnettoreTokenPolicy, false, false, false);
		}
		
		if(filtroConnettoreEndpoint!=null) {
			List<String> query = new ArrayList<String>();
			if((tipoConnettore==null || TipiConnettore.HTTP.equals(tipoConnettore))) {
				query.add(sqlQueryObject.getWhereLikeCondition(CostantiDB.CONNETTORI+".url", filtroConnettoreEndpoint, LikeConfig.contains(true)));
			}
			if((tipoConnettore==null || TipiConnettore.HTTPS.equals(tipoConnettore))) {
				ISQLQueryObject exists = buildSQLQueryObjectConnettoreCustomPropertyContains(tipoDB, CostantiDB.CONNETTORE_HTTP_LOCATION, filtroConnettoreEndpoint);
				query.add(sqlQueryObject.getWhereExistsCondition(false, exists));
			}
			//if((tipoConnettore==null || TipiConnettore.FILE.equals(tipoConnettore))) {
			if(tipoConnettore!=null && TipiConnettore.FILE.equals(tipoConnettore)) {
				ISQLQueryObject existsRequest = buildSQLQueryObjectConnettoreCustomPropertyContains(tipoDB, CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE, filtroConnettoreEndpoint);
				query.add(sqlQueryObject.getWhereExistsCondition(false, existsRequest));
				ISQLQueryObject existsRequestHeader = buildSQLQueryObjectConnettoreCustomPropertyContains(tipoDB, CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS, filtroConnettoreEndpoint);
				query.add(sqlQueryObject.getWhereExistsCondition(false, existsRequestHeader));
				ISQLQueryObject existsResponse = buildSQLQueryObjectConnettoreCustomPropertyContains(tipoDB, CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE, filtroConnettoreEndpoint);
				query.add(sqlQueryObject.getWhereExistsCondition(false, existsResponse));
				ISQLQueryObject existsResponseHeader = buildSQLQueryObjectConnettoreCustomPropertyContains(tipoDB, CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE_HEADERS, filtroConnettoreEndpoint);
				query.add(sqlQueryObject.getWhereExistsCondition(false, existsResponseHeader));
			}
			//if((tipoConnettore==null || TipiConnettore.JMS.equals(tipoConnettore))) {
			if(tipoConnettore!=null && TipiConnettore.JMS.equals(tipoConnettore)) {
				query.add(sqlQueryObject.getWhereLikeCondition(CostantiDB.CONNETTORI+".nome", filtroConnettoreEndpoint, LikeConfig.contains(true)));
				query.add(sqlQueryObject.getWhereLikeCondition(CostantiDB.CONNETTORI+".provurl", filtroConnettoreEndpoint, LikeConfig.contains(true)));
				query.add(sqlQueryObject.getWhereLikeCondition(CostantiDB.CONNETTORI+".connection_factory", filtroConnettoreEndpoint, LikeConfig.contains(true)));
				query.add(sqlQueryObject.getWhereLikeCondition(CostantiDB.CONNETTORI+".initcont", filtroConnettoreEndpoint, LikeConfig.contains(true)));
				query.add(sqlQueryObject.getWhereLikeCondition(CostantiDB.CONNETTORI+".urlpkg", filtroConnettoreEndpoint, LikeConfig.contains(true)));
			}
			if(!query.isEmpty()) {
				sqlQueryObject.addWhereCondition(false, query.toArray(new String[1]));
			}
		}
		
		if(filtroConnettoreKeystore!=null &&
				(tipoConnettore==null || TipiConnettore.HTTPS.equals(tipoConnettore))
				) {
			ISQLQueryObject existsKeystore = buildSQLQueryObjectConnettoreCustomPropertyContains(tipoDB, CostantiDB.CONNETTORE_HTTPS_KEY_STORE_LOCATION, filtroConnettoreKeystore);
			ISQLQueryObject existsTruststore = buildSQLQueryObjectConnettoreCustomPropertyContains(tipoDB, CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_LOCATION, filtroConnettoreKeystore);
			sqlQueryObject.addWhereCondition(false,
					existsKeystore.getWhereExistsCondition(false, existsKeystore),
					existsKeystore.getWhereExistsCondition(false, existsTruststore)
					);
		}
		
	} 
	private static ISQLQueryObject buildSQLQueryObjectConnettoreCustomPropertyContains(String tipoDB, String nomeProprieta, String valoreProprieta) throws Exception {
		ISQLQueryObject sql = SQLObjectFactory.createSQLQueryObject(tipoDB);
		sql.addFromTable(CostantiDB.CONNETTORI_CUSTOM);
		sql.setANDLogicOperator(true);
		sql.addWhereCondition(CostantiDB.CONNETTORI_CUSTOM+".id_connettore="+CostantiDB.CONNETTORI+".id");
		sql.addWhereLikeCondition(CostantiDB.CONNETTORI_CUSTOM+".name", nomeProprieta, false, false, false);
		sql.addWhereLikeCondition(CostantiDB.CONNETTORI_CUSTOM+".value", valoreProprieta, LikeConfig.contains(true));
		return sql;
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


	
	
	
	public static List<String> convertToList(String v){
		List<String> l = new ArrayList<>();
		if(v!=null && !"".equals(v)) {
			if(v.contains(",")) {
				String [] tmp = v.split(",");
				for (int i = 0; i < tmp.length; i++) {
					l.add(tmp[i].trim());
				}
			}else {
				l.add(v.trim());
			}
		}
		return l;
	}
}


