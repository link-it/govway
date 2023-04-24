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
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject;
import org.openspcoop2.generic_project.dao.jdbc.utils.GenericJDBCParameterUtilities;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCSqlLogger;
import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.jdbc.JDBCAdapterException;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
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
			GenericJDBCParameterUtilities jdbcParameterUtilities = new GenericJDBCParameterUtilities(TipiDatabase.toEnumConstant(tipoDB));
			JDBCObject [] paramsArray = null;
			if(paramTypes!=null && paramTypes.size()>0){
				paramsArray = paramTypes.toArray(new JDBCObject[1]);
			}
			jdbcParameterUtilities.setParameters(stm, paramsArray);
			
			JDBCSqlLogger sqlLogger = new JDBCSqlLogger(log);
			sqlLogger.infoSql(sql, paramsArray);
			
			rs=stm.executeQuery();

			while(rs.next()){
				
				List<Object> listaInterna = new ArrayList<>();
				
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
			JDBCUtilities.closeResources(rs, stm);

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
			JDBCUtilities.closeResources(rs, stm);

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
			JDBCUtilities.closeResources(rs, stm);

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
			int index = 1;
			stm.setLong(index++, idSoggetto);
			stm.setString(index++, tipoServizio);
			stm.setString(index++, nomeServizio);
			stm.setInt(index++, versioneServizio);
			if(testServizioNonCorrelato)
				stm.setString(index++, CostantiDB.STATO_FUNZIONALITA_DISABILITATO);

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
			JDBCUtilities.closeResources(rs, stm);

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
			JDBCUtilities.closeResources(rs, stm);

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
			JDBCUtilities.closeResources(rs, stm);

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
			JDBCUtilities.closeResources(rs, stm);

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
			JDBCUtilities.closeResources(rs, stm);

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
			JDBCUtilities.closeResources(rs, stm);

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
			JDBCUtilities.closeResources(rs, stm);

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
			JDBCUtilities.closeResources(rs, stm);

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
			JDBCUtilities.closeResources(rs, stm);

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
			JDBCUtilities.closeResources(selectRS, selectStmt);

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
			JDBCUtilities.closeResources(selectRS, selectStmt);

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
			JDBCUtilities.closeResources(rs, stm);

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
			JDBCUtilities.closeResources(rs, stm);

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
			JDBCUtilities.closeResources(rs, stm);

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
			JDBCUtilities.closeResources(rs, stm);

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
			JDBCUtilities.closeResources(rs, stm);

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
			JDBCUtilities.closeResources(rs, stm);

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
			JDBCUtilities.closeResources(rs, stm);

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
			JDBCUtilities.closeResources(rs, stm);

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
			JDBCUtilities.closeResources(rs, stm);

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
			JDBCUtilities.closeResources(rs, stm);

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
		
		GenericJDBCParameterUtilities jdbcParameterUtilities = new GenericJDBCParameterUtilities(TipiDatabase.toEnumConstant(tipoDatabase));
		
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
			String filtroConnettoreTokenPolicy, String filtroConnettoreEndpoint, String filtroConnettoreKeystore, String filtroConnettoreDebug) throws Exception {

		ISQLQueryObject sql = SQLObjectFactory.createSQLQueryObject(tipoDB);
		sql.addFromTable(CostantiDB.CONNETTORI);
		sql.setANDLogicOperator(true);
		sql.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_connettore_inv="+CostantiDB.CONNETTORI+".id");
		setFiltriConnettore(sql, tipoDB,
				tipoConnettore, endpointType, tipoConnettoreIntegrationManager,
				filtroConnettoreTokenPolicy, filtroConnettoreEndpoint, filtroConnettoreKeystore, filtroConnettoreDebug,
				CostantiDB.SERVIZI_APPLICATIVI);
		sqlQueryObject.addWhereExistsCondition(false, sql);
		
	}
	public static void setFiltriConnettoreErogazione(ISQLQueryObject sqlQueryObject, String tipoDB,
			TipiConnettore tipoConnettore, String endpointType, boolean tipoConnettoreIntegrationManager,
			String filtroConnettoreTokenPolicy, String filtroConnettoreEndpoint, String filtroConnettoreKeystore, String filtroConnettoreDebug) throws Exception {

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
				filtroConnettoreTokenPolicy, filtroConnettoreEndpoint, filtroConnettoreKeystore, filtroConnettoreDebug,
				aliasSERVIZI_APPLICATIVI);
		sqlQueryObject.addWhereExistsCondition(false, sql);
		
	}
	public static void setFiltriConnettoreFruizione(ISQLQueryObject sqlQueryObject, String tipoDB,
			TipiConnettore tipoConnettore, String endpointType, boolean tipoConnettoreIntegrationManager,
			String filtroConnettoreTokenPolicy, String filtroConnettoreEndpoint, String filtroConnettoreKeystore, String filtroConnettoreDebug) throws Exception {
		
		ISQLQueryObject sqlConnettoreDefault = SQLObjectFactory.createSQLQueryObject(tipoDB);
		sqlConnettoreDefault.addFromTable(CostantiDB.CONNETTORI);
		sqlConnettoreDefault.setANDLogicOperator(true);
		sqlConnettoreDefault.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_connettore="+CostantiDB.CONNETTORI+".id");
		setFiltriConnettore(sqlConnettoreDefault, tipoDB,
				tipoConnettore, endpointType, false,
				filtroConnettoreTokenPolicy, filtroConnettoreEndpoint, filtroConnettoreKeystore, filtroConnettoreDebug,
				null);
		
		ISQLQueryObject sqlGruppiConnettoreRidefinito = SQLObjectFactory.createSQLQueryObject(tipoDB);
		sqlGruppiConnettoreRidefinito.addFromTable(CostantiDB.SERVIZI_FRUITORI_AZIONI);
		sqlGruppiConnettoreRidefinito.addFromTable(CostantiDB.CONNETTORI);
		sqlGruppiConnettoreRidefinito.setANDLogicOperator(true);
		sqlGruppiConnettoreRidefinito.addWhereCondition(CostantiDB.SERVIZI_FRUITORI_AZIONI+".id_fruizione="+CostantiDB.SERVIZI_FRUITORI+".id");
		sqlGruppiConnettoreRidefinito.addWhereCondition(CostantiDB.SERVIZI_FRUITORI_AZIONI+".id_connettore="+CostantiDB.CONNETTORI+".id");
		setFiltriConnettore(sqlGruppiConnettoreRidefinito, tipoDB,
				tipoConnettore, endpointType, false,
				filtroConnettoreTokenPolicy, filtroConnettoreEndpoint, filtroConnettoreKeystore, filtroConnettoreDebug,
				null);
		
		sqlQueryObject.addWhereCondition(false, 
				sqlConnettoreDefault.getWhereExistsCondition(false, sqlConnettoreDefault),
				sqlConnettoreDefault.getWhereExistsCondition(false, sqlGruppiConnettoreRidefinito));
		
	}
	private static void setFiltriConnettore(ISQLQueryObject sqlQueryObject, String tipoDB,
			TipiConnettore tipoConnettore, String endpointType, boolean tipoConnettoreIntegrationManager,
			String filtroConnettoreTokenPolicy, String filtroConnettoreEndpoint, String filtroConnettoreKeystore, String filtroConnettoreDebug,
			String aliasTabellaServiziApplicativi) throws Exception {
		
		// NOTA: logica inserita anche in PorteApplicativeHelper.applicaFiltriRicercaConnettoriMultipli
		
		boolean setEndpointtype = false;
		
		if(endpointType!=null) {
			sqlQueryObject.addWhereLikeCondition(CostantiDB.CONNETTORI+".endpointtype", endpointType, false, false, false);
			setEndpointtype=true;
		}
		else if(tipoConnettore!=null) {
			if(TipiConnettore.CUSTOM.equals(tipoConnettore)) {
				List<String> tipiConosciuti = new ArrayList<>();
				TipiConnettore[] tipi = TipiConnettore.values();
				for (TipiConnettore tipiConnettore : tipi) {
					tipiConosciuti.add(tipiConnettore.getNome());
				}
				ISQLQueryObject sql = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sql.addFromTable(CostantiDB.CONNETTORI);
				sql.setANDLogicOperator(true);
				sql.setNOTBeforeConditions(true);
				sql.addWhereINCondition(CostantiDB.CONNETTORI+".endpointtype", true, tipiConosciuti.toArray(new String[1]));
				setEndpointtype=true;
				sqlQueryObject.addWhereCondition(sql.createSQLConditions());
			}
		}
		
		if(tipoConnettoreIntegrationManager) {
			sqlQueryObject.addWhereLikeCondition(aliasTabellaServiziApplicativi+".getmsginv", CostantiDB.STATO_FUNZIONALITA_ABILITATO, false, false, false);
		}
		
		if(filtroConnettoreTokenPolicy!=null) {
			sqlQueryObject.addWhereLikeCondition(CostantiDB.CONNETTORI+".token_policy", filtroConnettoreTokenPolicy, false, false, false);
		}
		
		if(filtroConnettoreEndpoint!=null) {
			List<String> query = new ArrayList<>();
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
				if(!setEndpointtype) {
					sqlQueryObject.addWhereCondition(CostantiDB.CONNETTORI+".endpointtype <> '"+TipiConnettore.DISABILITATO.getNome()+"'");
				}
			}
		}
		
		if(filtroConnettoreKeystore!=null &&
				(tipoConnettore==null || TipiConnettore.HTTPS.equals(tipoConnettore))
				) {
			ISQLQueryObject existsKeystore = buildSQLQueryObjectConnettoreCustomPropertyContains(tipoDB, CostantiDB.CONNETTORE_HTTPS_KEY_STORE_LOCATION, filtroConnettoreKeystore);
			ISQLQueryObject existsTruststore = buildSQLQueryObjectConnettoreCustomPropertyContains(tipoDB, CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_LOCATION, filtroConnettoreKeystore);
			sqlQueryObject.addWhereCondition(false,
					sqlQueryObject.getWhereExistsCondition(false, existsKeystore),
					sqlQueryObject.getWhereExistsCondition(false, existsTruststore)
					);
		}
		
		if(filtroConnettoreDebug!=null) {
			if(Filtri.FILTRO_CONNETTORE_DEBUG_VALORE_ABILITATO.equals(filtroConnettoreDebug)) {
				sqlQueryObject.addWhereCondition(CostantiDB.CONNETTORI+".debug="+CostantiDB.TRUE);
			}
			else if(Filtri.FILTRO_CONNETTORE_DEBUG_VALORE_DISABILITATO.equals(filtroConnettoreDebug)) {
				sqlQueryObject.addWhereCondition(CostantiDB.CONNETTORI+".debug="+CostantiDB.FALSE);
			}
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
	
	
	
	
	public static void setFiltriModIApplicativi(ISQLQueryObject sqlQueryObject, String tipoDB,
			Boolean filtroModISicurezzaMessaggio,
			String filtroModIKeystorePath, String filtroModIKeystoreSubject, String filtroModIKeystoreIssuer, 
			Boolean filtroModISicurezzaToken,
			String filtroModITokenPolicy, String filtroModITokenClientId,
			String filtroModIAudience,
			boolean checkCredenzialiBase) throws Exception {
		
		ProprietariProtocolProperty proprietario = ProprietariProtocolProperty.SERVIZIO_APPLICATIVO;
		String tabellaDB = 	CostantiDB.SERVIZI_APPLICATIVI ;
		
		List<ISQLQueryObject> listSqlQueryProtocolProperties = new ArrayList<ISQLQueryObject>();
		
		if(filtroModISicurezzaMessaggio!=null) {
			ISQLQueryObject sql = buildSQLQueryObjectProtocolProperties(proprietario, tabellaDB,
					tipoDB, CostantiDB.MODIPA_SICUREZZA_MESSAGGIO , null, null, filtroModISicurezzaMessaggio);
			listSqlQueryProtocolProperties.add(sql);
		}
		
		if(filtroModIKeystorePath!=null) {
			ISQLQueryObject sql = buildSQLQueryObjectProtocolProperties(proprietario, tabellaDB,
					tipoDB, CostantiDB.MODIPA_KEYSTORE_PATH, null, filtroModIKeystorePath, null);
			listSqlQueryProtocolProperties.add(sql);
		}
		
		if(filtroModIKeystoreSubject!=null) {
			ISQLQueryObject sql = buildSQLQueryObjectProtocolProperties(proprietario, tabellaDB,
					tipoDB, CostantiDB.MODIPA_KEY_CN_SUBJECT, null, filtroModIKeystoreSubject, null);
			listSqlQueryProtocolProperties.add(sql);
		}
		
		if(filtroModIKeystoreIssuer!=null) {
			ISQLQueryObject sql = buildSQLQueryObjectProtocolProperties(proprietario, tabellaDB,
					tipoDB, CostantiDB.MODIPA_KEY_CN_ISSUER, null, filtroModIKeystoreIssuer, null);
			listSqlQueryProtocolProperties.add(sql);
		}
		
		if(filtroModISicurezzaToken!=null) {
			ISQLQueryObject sql = buildSQLQueryObjectProtocolProperties(proprietario, tabellaDB,
					tipoDB, CostantiDB.MODIPA_SICUREZZA_TOKEN , null, null, filtroModISicurezzaToken);
			listSqlQueryProtocolProperties.add(sql);
		}
		
		if(filtroModITokenPolicy!=null) {
			ISQLQueryObject sql = buildSQLQueryObjectProtocolProperties(proprietario, tabellaDB,
					tipoDB, CostantiDB.MODIPA_SICUREZZA_TOKEN_POLICY, null, filtroModITokenPolicy, null);
			listSqlQueryProtocolProperties.add(sql);			
		}
		
		if(filtroModITokenClientId!=null) {
			ISQLQueryObject sql = buildSQLQueryObjectProtocolProperties(proprietario, tabellaDB,
					tipoDB, CostantiDB.MODIPA_SICUREZZA_TOKEN_CLIENT_ID, null, filtroModITokenClientId, null);
			listSqlQueryProtocolProperties.add(sql);
		}
		
		if(filtroModIAudience!=null) {
			ISQLQueryObject sql = buildSQLQueryObjectProtocolProperties(proprietario, tabellaDB,
					tipoDB, CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE , null, filtroModIAudience, null);
			listSqlQueryProtocolProperties.add(sql);
		}
		
		
		
		if(checkCredenzialiBase && 
				(filtroModIKeystoreSubject!=null || filtroModIKeystoreIssuer!=null || filtroModITokenClientId!=null)) {
			
			ISQLQueryObject sqlOr = sqlQueryObject.newSQLQueryObject();
			sqlOr.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlOr.setANDLogicOperator(false);
			
			// ramo modi
			ISQLQueryObject sqlModi = sqlQueryObject.newSQLQueryObject();
			sqlModi.setANDLogicOperator(true);
			for (ISQLQueryObject sql : listSqlQueryProtocolProperties) {
				sqlModi.addWhereExistsCondition(false, sql);
			}
			sqlOr.addWhereCondition(sqlModi.createSQLConditions());
			
			// ramo credenzialiBase
			ISQLQueryObject sqlCredenzialiBase = sqlQueryObject.newSQLQueryObject();
			sqlCredenzialiBase.setANDLogicOperator(true);
			String ssl = "ssl";// --> org.openspcoop2.core.config.constants.CredenzialeTipo.SSL.toString();
			String token = "token";// --> org.openspcoop2.core.config.constants.CredenzialeTipo.TOKEN.toString();
			if(filtroModITokenClientId!=null) {
				sqlCredenzialiBase.addWhereCondition(false,
						CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = '"+token+"'",
						CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = '"+ssl+"' AND "+CostantiDB.SERVIZI_APPLICATIVI+".token_policy IS NOT NULL");
			}
			else {
				sqlCredenzialiBase.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = '"+ssl+"'");
			}
			if(filtroModITokenClientId!=null) {
				sqlCredenzialiBase.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".utente", 
						filtroModITokenClientId, LikeConfig.contains(true,true));
			}
			if(filtroModIKeystoreSubject!=null) {
				sqlCredenzialiBase.addWhereCondition(false, 
						sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".cn_subject", filtroModIKeystoreSubject, 
								LikeConfig.contains(true,true)),
						sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".subject", filtroModIKeystoreSubject, 
								LikeConfig.contains(true,true)));
			} 
			if(filtroModIKeystoreIssuer!=null) {
				sqlCredenzialiBase.addWhereCondition(false, 
						sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".cn_issuer", filtroModIKeystoreIssuer, 
								LikeConfig.contains(true,true)),
						sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".issuer", filtroModIKeystoreIssuer, 
								LikeConfig.contains(true,true)));
			} 
			sqlOr.addWhereCondition(sqlCredenzialiBase.createSQLConditions());
			
			sqlQueryObject.addWhereCondition(sqlOr.createSQLConditions());
			
		}
		else {
		
			if(!listSqlQueryProtocolProperties.isEmpty()) {
				for (ISQLQueryObject sql : listSqlQueryProtocolProperties) {
					sqlQueryObject.addWhereExistsCondition(false, sql);
				}
			}
			
		}
		
	}
	
	public static void setFiltriModIErogazione(ISQLQueryObject sqlQueryObject, String tipoDB,
			String filtroModISicurezzaCanale, String filtroModISicurezzaMessaggio,
			Boolean filtroModIDigestRichiesta, Boolean filtroModIInfoUtente,
			String filtroModIKeystore, String filtroModIAudience) throws Exception {
		setFiltriModIErogazioneFruizione(sqlQueryObject, tipoDB,
				filtroModISicurezzaCanale, filtroModISicurezzaMessaggio,
				filtroModIDigestRichiesta, filtroModIInfoUtente,
				filtroModIKeystore, filtroModIAudience,
				true);
	}
	public static void setFiltriModIFruizione(ISQLQueryObject sqlQueryObject, String tipoDB,
			String filtroModISicurezzaCanale, String filtroModISicurezzaMessaggio,
			Boolean filtroModIDigestRichiesta, Boolean filtroModIInfoUtente,
			String filtroModIKeystore, String filtroModIAudience) throws Exception {
		setFiltriModIErogazioneFruizione(sqlQueryObject, tipoDB,
				filtroModISicurezzaCanale, filtroModISicurezzaMessaggio,
				filtroModIDigestRichiesta, filtroModIInfoUtente,
				filtroModIKeystore, filtroModIAudience,
				false);
	}
	private static void setFiltriModIErogazioneFruizione(ISQLQueryObject sqlQueryObject, String tipoDB,
			String filtroModISicurezzaCanale, String filtroModISicurezzaMessaggio,
			Boolean filtroModIDigestRichiesta, Boolean filtroModIInfoUtente,
			String filtroModIKeystore, String filtroModIAudience,
			boolean erogazione) throws Exception {
		
		setFiltriModI(sqlQueryObject, tipoDB,
				filtroModISicurezzaCanale, filtroModISicurezzaMessaggio,
				filtroModIDigestRichiesta, filtroModIInfoUtente);
		
		ProprietariProtocolProperty proprietario = erogazione ? ProprietariProtocolProperty.ACCORDO_SERVIZIO_PARTE_SPECIFICA : ProprietariProtocolProperty.FRUITORE;
		String tabellaDB = 	erogazione ? CostantiDB.SERVIZI : CostantiDB.SERVIZI_FRUITORI ;
		
		if(filtroModIKeystore!=null) {
			
			List<String> query = new ArrayList<>();
			
			ISQLQueryObject sqlAccordoKeystore = buildSQLQueryObjectProtocolProperties(proprietario, tabellaDB,
					tipoDB, CostantiDB.MODIPA_KEYSTORE_PATH, null, filtroModIKeystore, null);
			query.add(sqlQueryObject.getWhereExistsCondition(false, sqlAccordoKeystore));
			
			ISQLQueryObject sqlAccordoTruststore = buildSQLQueryObjectProtocolProperties(proprietario, tabellaDB,
					tipoDB, CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH, null, filtroModIKeystore, null);
			query.add(sqlQueryObject.getWhereExistsCondition(false, sqlAccordoTruststore));
			
			ISQLQueryObject sqlAccordoTruststoreCrl = buildSQLQueryObjectProtocolProperties(proprietario, tabellaDB,
					tipoDB, CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS, null, filtroModIKeystore, null);
			query.add(sqlQueryObject.getWhereExistsCondition(false, sqlAccordoTruststoreCrl));
			
			ISQLQueryObject sqlAccordoTruststoreSsl = buildSQLQueryObjectProtocolProperties(proprietario, tabellaDB,
					tipoDB, CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH, null, filtroModIKeystore, null);
			query.add(sqlQueryObject.getWhereExistsCondition(false, sqlAccordoTruststoreSsl));
						
			if(!query.isEmpty()) {
				sqlQueryObject.addWhereCondition(false, query.toArray(new String[1]));
			}
		}
		
		if(filtroModIAudience!=null) {
			
			ISQLQueryObject sqlAudience = buildSQLQueryObjectProtocolProperties(proprietario, tabellaDB,
					tipoDB, CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE , null, filtroModIAudience, null);
			sqlQueryObject.addWhereExistsCondition(false, sqlAudience);
			
		}
		
	}
	public static void setFiltriModI(ISQLQueryObject sqlQueryObject, String tipoDB,
			String filtroModISicurezzaCanale, String filtroModISicurezzaMessaggio,
			Boolean filtroModIDigestRichiesta, Boolean filtroModIInfoUtente) throws Exception {
		
		if(filtroModISicurezzaCanale!=null) {
			ISQLQueryObject sql = buildSQLQueryObjectProtocolProperties(ProprietariProtocolProperty.ACCORDO_SERVIZIO_PARTE_COMUNE, CostantiDB.ACCORDI,
					tipoDB, CostantiDB.MODIPA_PROFILO_SICUREZZA_CANALE, filtroModISicurezzaCanale, null, null);
			sqlQueryObject.addWhereExistsCondition(false, sql);
		}
		if(filtroModISicurezzaMessaggio!=null) {
			
			List<String> query = new ArrayList<>();
			
			ISQLQueryObject sqlAccordoSec = buildSQLQueryObjectProtocolProperties(ProprietariProtocolProperty.ACCORDO_SERVIZIO_PARTE_COMUNE, CostantiDB.ACCORDI,
					tipoDB, CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO, filtroModISicurezzaMessaggio, null, null);
			query.add(sqlQueryObject.getWhereExistsCondition(false, sqlAccordoSec));
			
			addRestCondition(query, sqlQueryObject, tipoDB, CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO, filtroModISicurezzaMessaggio, null);
			
			addSoapCondition(query, sqlQueryObject, tipoDB, CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO, filtroModISicurezzaMessaggio, null);
			
			if(!query.isEmpty()) {
				sqlQueryObject.addWhereCondition(false, query.toArray(new String[1]));
			}
		}
		if(filtroModIDigestRichiesta!=null) {
			
			List<String> query = new ArrayList<>();
			
			ISQLQueryObject sqlAccordoSec = buildSQLQueryObjectProtocolProperties(ProprietariProtocolProperty.ACCORDO_SERVIZIO_PARTE_COMUNE, CostantiDB.ACCORDI,
					tipoDB, CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REQUEST_DIGEST, null, null, filtroModIDigestRichiesta);
			query.add(sqlQueryObject.getWhereExistsCondition(false, sqlAccordoSec));
			
			addRestCondition(query, sqlQueryObject, tipoDB, CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REQUEST_DIGEST, null, filtroModIDigestRichiesta);
			
			addSoapCondition(query, sqlQueryObject, tipoDB, CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REQUEST_DIGEST, null, filtroModIDigestRichiesta);
			
			if(!query.isEmpty()) {
				sqlQueryObject.addWhereCondition(false, query.toArray(new String[1]));
			}
		}
		if(filtroModIInfoUtente!=null) {
			
			List<String> query = new ArrayList<>();
			
			ISQLQueryObject sqlAccordoSec = buildSQLQueryObjectProtocolProperties(ProprietariProtocolProperty.ACCORDO_SERVIZIO_PARTE_COMUNE, CostantiDB.ACCORDI,
					tipoDB, CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA, null, null, filtroModIInfoUtente);
			query.add(sqlQueryObject.getWhereExistsCondition(false, sqlAccordoSec));
			
			addRestCondition(query, sqlQueryObject, tipoDB, CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA, null, filtroModIInfoUtente);
			
			addSoapCondition(query, sqlQueryObject, tipoDB, CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA, null, filtroModIInfoUtente);
			
			if(!query.isEmpty()) {
				sqlQueryObject.addWhereCondition(false, query.toArray(new String[1]));
			}
			
		}
				
	}
	private static void addRestCondition(List<String> query, ISQLQueryObject sqlQueryObject, String tipoDB, String nomeProprieta, String valoreProprieta, Boolean valoreProprietaBoolean) throws Exception {
		String aliasRISORSE = "m_res";
		ISQLQueryObject sqlREST = SQLObjectFactory.createSQLQueryObject(tipoDB);
		sqlREST.addFromTable(CostantiDB.API_RESOURCES, aliasRISORSE);
		sqlREST.setANDLogicOperator(true);
		sqlREST.addWhereCondition(aliasRISORSE+".id_accordo="+CostantiDB.ACCORDI+".id");
		ISQLQueryObject sqlRestSec = buildSQLQueryObjectProtocolProperties(ProprietariProtocolProperty.RESOURCE, aliasRISORSE,
				tipoDB, nomeProprieta, valoreProprieta, null, valoreProprietaBoolean);
		sqlREST.addWhereExistsCondition(false, sqlRestSec);
		query.add(sqlQueryObject.getWhereExistsCondition(false, sqlREST));
	}
	private static void addSoapCondition(List<String> query, ISQLQueryObject sqlQueryObject, String tipoDB, String nomeProprieta, String valoreProprieta, Boolean valoreProprietaBoolean) throws Exception {
		String aliasPORTTYPES = "m_pt";
		String aliasOPERATIONS = "m_op";
		ISQLQueryObject sqlSOAP = SQLObjectFactory.createSQLQueryObject(tipoDB);
		sqlSOAP.addFromTable(CostantiDB.PORT_TYPE, aliasPORTTYPES);
		sqlSOAP.addFromTable(CostantiDB.PORT_TYPE_AZIONI, aliasOPERATIONS);
		sqlSOAP.setANDLogicOperator(true);
		sqlSOAP.addWhereCondition(aliasPORTTYPES+".id_accordo="+CostantiDB.ACCORDI+".id");
		sqlSOAP.addWhereCondition(aliasOPERATIONS+".id_port_type="+aliasPORTTYPES+".id");
		ISQLQueryObject sqlSoapSec = buildSQLQueryObjectProtocolProperties(ProprietariProtocolProperty.OPERATION, aliasOPERATIONS,
				tipoDB, nomeProprieta, valoreProprieta, null, valoreProprietaBoolean);
		sqlSOAP.addWhereExistsCondition(false, sqlSoapSec);
		query.add(sqlQueryObject.getWhereExistsCondition(false, sqlSOAP));
	}
	private static ISQLQueryObject buildSQLQueryObjectProtocolProperties(ProprietariProtocolProperty tipoProprietario, String tabellaProprietario,
			String tipoDB, String nomeProprieta, String valoreProprietaEquals, String valoreProprietaContains, Boolean valoreProprietaBoolean) throws Exception {
		ISQLQueryObject sql = SQLObjectFactory.createSQLQueryObject(tipoDB);
		sql.addFromTable(CostantiDB.PROTOCOL_PROPERTIES);
		sql.setANDLogicOperator(true);
		sql.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES+".id_proprietario="+tabellaProprietario+".id");
		sql.addWhereLikeCondition(CostantiDB.PROTOCOL_PROPERTIES+".tipo_proprietario", tipoProprietario.name(), false, false, false);
		sql.addWhereLikeCondition(CostantiDB.PROTOCOL_PROPERTIES+".name", nomeProprieta, false, false, false);
		if(valoreProprietaContains!=null) {
			sql.addWhereLikeCondition(CostantiDB.PROTOCOL_PROPERTIES+".value_string", valoreProprietaContains, LikeConfig.contains(true));
		}
		else if(valoreProprietaEquals!=null) {
			sql.addWhereLikeCondition(CostantiDB.PROTOCOL_PROPERTIES+".value_string", valoreProprietaEquals, false, false, false);
		}
		if(valoreProprietaBoolean!=null) {
			if(valoreProprietaBoolean) {
				sql.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES+".value_boolean="+CostantiDB.TRUE);
			}
			else {
				sql.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES+".value_boolean="+CostantiDB.FALSE);
			}
		}
		return sql;
	}
	
	
	public static void setFiltriProprietaApplicativo(ISQLQueryObject sqlQueryObject, String tipoDB, 
			String nomeProprieta, String valoreProprieta) throws Exception {
		setFiltriProprieta(sqlQueryObject, tipoDB, 
				ProprietariProtocolProperty.SERVIZIO_APPLICATIVO, null,
				nomeProprieta, valoreProprieta);
	}
	public static void setFiltriProprietaSoggetto(ISQLQueryObject sqlQueryObject, String tipoDB, 
			String nomeProprieta, String valoreProprieta) throws Exception {
		setFiltriProprieta(sqlQueryObject, tipoDB, 
				ProprietariProtocolProperty.SOGGETTO, null,
				nomeProprieta, valoreProprieta);
	}
	public static void setFiltriProprietaErogazione(ISQLQueryObject sqlQueryObject, String tipoDB, 
			String nomeProprieta, String valoreProprieta) throws Exception {
		
		String aliasMAPPING_EROGAZIONE_PA = "c_map";
		String aliasPORTE_APPLICATIVE = "c_pa";
		
		ISQLQueryObject sql = SQLObjectFactory.createSQLQueryObject(tipoDB);
		sql.addFromTable(CostantiDB.MAPPING_EROGAZIONE_PA, aliasMAPPING_EROGAZIONE_PA);
		sql.addFromTable(CostantiDB.PORTE_APPLICATIVE, aliasPORTE_APPLICATIVE);
		sql.setANDLogicOperator(true);
		sql.addWhereCondition(aliasMAPPING_EROGAZIONE_PA+".id_erogazione="+CostantiDB.SERVIZI+".id");
		sql.addWhereCondition(aliasMAPPING_EROGAZIONE_PA+".id_porta="+aliasPORTE_APPLICATIVE+".id");
		setFiltriProprieta(sql, tipoDB, 
				ProprietariProtocolProperty.ACCORDO_SERVIZIO_PARTE_SPECIFICA, aliasPORTE_APPLICATIVE,
				nomeProprieta, valoreProprieta);
		sqlQueryObject.addWhereExistsCondition(false, sql);
		
	}
	public static void setFiltriProprietaFruizione(ISQLQueryObject sqlQueryObject, String tipoDB, 
			String nomeProprieta, String valoreProprieta) throws Exception {
		
		String aliasMAPPING_FRUIZIONE_PD = "c_map";
		String aliasPORTE_DELEGATE = "c_pd";
		
		ISQLQueryObject sql = SQLObjectFactory.createSQLQueryObject(tipoDB);
		sql.addFromTable(CostantiDB.MAPPING_FRUIZIONE_PD, aliasMAPPING_FRUIZIONE_PD);
		sql.addFromTable(CostantiDB.PORTE_DELEGATE, aliasPORTE_DELEGATE);
		sql.setANDLogicOperator(true);
		sql.addWhereCondition(aliasMAPPING_FRUIZIONE_PD+".id_fruizione="+CostantiDB.SERVIZI_FRUITORI+".id");
		sql.addWhereCondition(aliasMAPPING_FRUIZIONE_PD+".id_porta="+aliasPORTE_DELEGATE+".id");
		setFiltriProprieta(sql, tipoDB, 
				ProprietariProtocolProperty.FRUITORE, aliasPORTE_DELEGATE,
				nomeProprieta, valoreProprieta);
		sqlQueryObject.addWhereExistsCondition(false, sql);
		
	}
	private static void setFiltriProprieta(ISQLQueryObject sqlQueryObject, String tipoDB, 
			ProprietariProtocolProperty tipoProprietario, String tabellaAlias,
			String nomeProprieta, String valoreProprieta) throws Exception {
		ISQLQueryObject sql = SQLObjectFactory.createSQLQueryObject(tipoDB);
		String nomeTabellaPrincipale = null;
		String nomeTabellaProprieta = null;
		String colonnaJoin = null;
		if(ProprietariProtocolProperty.SERVIZIO_APPLICATIVO.equals(tipoProprietario)) {
			nomeTabellaPrincipale = CostantiDB.SERVIZI_APPLICATIVI;
			nomeTabellaProprieta = CostantiDB.SERVIZI_APPLICATIVI_PROPS;
			colonnaJoin = "id_servizio_applicativo";
		}
		else if(ProprietariProtocolProperty.SOGGETTO.equals(tipoProprietario)) {
			nomeTabellaPrincipale = CostantiDB.SOGGETTI;
			nomeTabellaProprieta = CostantiDB.SOGGETTI_PROPS;
			colonnaJoin = "id_soggetto";
		}
		else if(ProprietariProtocolProperty.ACCORDO_SERVIZIO_PARTE_SPECIFICA.equals(tipoProprietario)) {
			nomeTabellaPrincipale = tabellaAlias;
			nomeTabellaProprieta = CostantiDB.PORTE_APPLICATIVE_PROP;
			colonnaJoin = "id_porta";
		}
		else if(ProprietariProtocolProperty.FRUITORE.equals(tipoProprietario)) {
			nomeTabellaPrincipale = tabellaAlias;
			nomeTabellaProprieta = CostantiDB.PORTE_DELEGATE_PROP;
			colonnaJoin = "id_porta";
		}
		else {
			throw new Exception("Tipo proprietario '"+tipoProprietario+"' non gestito");
		}
		sql.addFromTable(nomeTabellaProprieta);
		sql.setANDLogicOperator(true);
		sql.addWhereCondition(nomeTabellaProprieta+"."+colonnaJoin+"="+nomeTabellaPrincipale+".id");
		if(nomeProprieta!=null) {
			sql.addWhereLikeCondition(nomeTabellaProprieta+".nome", nomeProprieta, false, false, false);
		}
		if(valoreProprieta!=null) {
			sql.addWhereLikeCondition(nomeTabellaProprieta+".valore", valoreProprieta, LikeConfig.contains(true));
		}
		sqlQueryObject.addWhereExistsCondition(false, sql);
	}
	
	
	
	public static void setFiltriConfigurazioneErogazione(ISQLQueryObject sqlQueryObject, String tipoDB,
			String filtroStatoAPIImpl,
			String filtroAutenticazioneTokenPolicy,
			String filtroAutenticazioneTrasporto,
			String filtroRateLimitingStato,
			String filtroValidazioneStato,
			String filtroCacheRispostaStato,
			String filtroMessageSecurityStato,
			String filtroMTOMStato,
			String filtroTrasformazione,
			String filtroCorrelazioneApplicativa,
			String filtroConfigurazioneDumpTipo,
			String filtroCORS, String filtroCORS_origin) throws Exception {
		_setFiltriConfigurazione(sqlQueryObject, tipoDB,
				filtroStatoAPIImpl,
				filtroAutenticazioneTokenPolicy,
				filtroAutenticazioneTrasporto,
				filtroRateLimitingStato,
				filtroValidazioneStato,
				filtroCacheRispostaStato,
				filtroMessageSecurityStato,
				filtroMTOMStato,
				filtroTrasformazione,
				filtroCorrelazioneApplicativa,
				filtroConfigurazioneDumpTipo,
				filtroCORS, filtroCORS_origin, 
				true);
	}
	public static void setFiltriConfigurazioneFruizione(ISQLQueryObject sqlQueryObject, String tipoDB,
			String filtroStatoAPIImpl,
			String filtroAutenticazioneTokenPolicy,
			String filtroAutenticazioneTrasporto,
			String filtroRateLimitingStato,
			String filtroValidazioneStato,
			String filtroCacheRispostaStato,
			String filtroMessageSecurityStato,
			String filtroMTOMStato,
			String filtroTrasformazione,
			String filtroCorrelazioneApplicativa,
			String filtroConfigurazioneDumpTipo,
			String filtroCORS, String filtroCORS_origin) throws Exception {
		_setFiltriConfigurazione(sqlQueryObject, tipoDB,
				filtroStatoAPIImpl,
				filtroAutenticazioneTokenPolicy,
				filtroAutenticazioneTrasporto,
				filtroRateLimitingStato,
				filtroValidazioneStato,
				filtroCacheRispostaStato,
				filtroMessageSecurityStato,
				filtroMTOMStato,
				filtroTrasformazione,
				filtroCorrelazioneApplicativa,
				filtroConfigurazioneDumpTipo,
				filtroCORS, filtroCORS_origin,
				false);
	}
	public static void _setFiltriConfigurazione(ISQLQueryObject sqlQueryObject, String tipoDB,
			String filtroStatoAPIImpl,
			String filtroAutenticazioneTokenPolicy,
			String filtroAutenticazioneTrasporto,
			String filtroRateLimitingStato,
			String filtroValidazioneStato,
			String filtroCacheRispostaStato,
			String filtroMessageSecurityStato,
			String filtroMTOMStato,
			String filtroTrasformazione,
			String filtroCorrelazioneApplicativa,
			String filtroConfigurazioneDumpTipo,
			String filtroCORS, String filtroCORS_origin,
			boolean erogazioni) throws Exception {

		String aliasCONFIG_PORTA = "conf_p";
		String aliasCONFIG_MAPPING = "conf_m";
		
		ISQLQueryObject sql = SQLObjectFactory.createSQLQueryObject(tipoDB);
		if(erogazioni) {
			sql.addFromTable(CostantiDB.PORTE_APPLICATIVE, aliasCONFIG_PORTA);
			sql.addFromTable(CostantiDB.MAPPING_EROGAZIONE_PA, aliasCONFIG_MAPPING);
		}
		else {
			sql.addFromTable(CostantiDB.PORTE_DELEGATE, aliasCONFIG_PORTA);
			sql.addFromTable(CostantiDB.MAPPING_FRUIZIONE_PD, aliasCONFIG_MAPPING);
		}
		sql.setANDLogicOperator(true);
		if(erogazioni) {
			sql.addWhereCondition(aliasCONFIG_MAPPING+".id_erogazione="+CostantiDB.SERVIZI+".id");
			sql.addWhereCondition(aliasCONFIG_MAPPING+".id_porta="+aliasCONFIG_PORTA+".id");
		}
		else {
			sql.addWhereCondition(aliasCONFIG_MAPPING+".id_fruizione="+CostantiDB.SERVIZI_FRUITORI+".id");
			sql.addWhereCondition(aliasCONFIG_MAPPING+".id_porta="+aliasCONFIG_PORTA+".id");
		}	
		
		if(filtroStatoAPIImpl!=null && !"".equals(filtroStatoAPIImpl)) {
			if(Filtri.FILTRO_CONFIGURAZIONE_STATO_VALORE_ABILITATO.equals(filtroStatoAPIImpl)) {
				ISQLQueryObject sqlStato = SQLObjectFactory.createSQLQueryObject(tipoDB);
				if(erogazioni) {
					sqlStato.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				}
				else {
					sqlStato.addFromTable(CostantiDB.PORTE_DELEGATE);
				}
				sqlStato.setANDLogicOperator(false);
				sqlStato.addWhereLikeCondition(aliasCONFIG_PORTA+".stato", "abilitato", false, false, false);
				sqlStato.addWhereIsNullCondition(aliasCONFIG_PORTA+".stato");
				sql.addWhereCondition(sqlStato.createSQLConditions());
			}
			else if(Filtri.FILTRO_CONFIGURAZIONE_STATO_VALORE_DISABILITATO.equals(filtroStatoAPIImpl)) {
				sql.addWhereLikeCondition(aliasCONFIG_PORTA+".stato", "disabilitato", false, false, false);
			}	
		}
		
		if(filtroAutenticazioneTokenPolicy!=null && !"".equals(filtroAutenticazioneTokenPolicy)) {
			sql.addWhereLikeCondition(aliasCONFIG_PORTA+".token_policy", filtroAutenticazioneTokenPolicy, false, false, false);
		}
		
		if(filtroAutenticazioneTrasporto!=null && !"".equals(filtroAutenticazioneTrasporto)) {
			sql.addWhereLikeCondition(aliasCONFIG_PORTA+".autenticazione", filtroAutenticazioneTrasporto, false, false, false);
		}
		
		if(filtroRateLimitingStato!=null && !"".equals(filtroRateLimitingStato)) {
			String aliasRT = "rt_c";
			ISQLQueryObject sqlRT = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlRT.addFromTable(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY, aliasRT);
			sqlRT.addSelectField(aliasRT, "id");
			sqlRT.setANDLogicOperator(true);
			sqlRT.addWhereCondition(aliasCONFIG_PORTA+".nome_porta="+aliasRT+".filtro_porta");
			String ruolo = erogazioni ? "applicativa" : "delegata";
			sqlRT.addWhereLikeCondition(aliasRT+".filtro_ruolo", ruolo, false, false, false);
			if(Filtri.FILTRO_CONFIGURAZIONE_RATE_LIMITING_STATO_VALORE_ABILITATO.equals(filtroRateLimitingStato)) {
				sql.addWhereExistsCondition(false, sqlRT);	
			}
			else if(Filtri.FILTRO_CONFIGURAZIONE_RATE_LIMITING_STATO_VALORE_DISABILITATO.equals(filtroRateLimitingStato)) {
				sql.addWhereExistsCondition(true, sqlRT);	
			}
		}
		
		if(filtroValidazioneStato!=null && !"".equals(filtroValidazioneStato)) {
			if(Filtri.FILTRO_CONFIGURAZIONE_VALIDAZIONE_STATO_VALORE_ABILITATO.equals(filtroValidazioneStato)) {
				sql.addWhereIsNotNullCondition(aliasCONFIG_PORTA+".validazione_contenuti_stato");
			}
			else if(Filtri.FILTRO_CONFIGURAZIONE_VALIDAZIONE_STATO_VALORE_DISABILITATO.equals(filtroValidazioneStato)) {
				sql.addWhereIsNullCondition(aliasCONFIG_PORTA+".validazione_contenuti_stato");
			}
		}
		
		if(filtroCacheRispostaStato!=null && !"".equals(filtroCacheRispostaStato)) {
			if(Filtri.FILTRO_CONFIGURAZIONE_CACHE_RISPOSTA_STATO_VALORE_DISABILITATO.equals(filtroCacheRispostaStato)) {
				ISQLQueryObject sqlStato = SQLObjectFactory.createSQLQueryObject(tipoDB);
				if(erogazioni) {
					sqlStato.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				}
				else {
					sqlStato.addFromTable(CostantiDB.PORTE_DELEGATE);
				}
				sqlStato.setANDLogicOperator(false);
				sqlStato.addWhereLikeCondition(aliasCONFIG_PORTA+".response_cache_stato", "disabilitato", false, false, false);
				sqlStato.addWhereIsNullCondition(aliasCONFIG_PORTA+".response_cache_stato");
				sql.addWhereCondition(sqlStato.createSQLConditions());
			}
			else if(Filtri.FILTRO_CONFIGURAZIONE_CACHE_RISPOSTA_STATO_VALORE_ABILITATO.equals(filtroCacheRispostaStato)) {
				sql.addWhereLikeCondition(aliasCONFIG_PORTA+".response_cache_stato", "abilitato", false, false, false);
			}	
		}
		
		if(filtroMessageSecurityStato!=null && !"".equals(filtroMessageSecurityStato)) {
			if(Filtri.FILTRO_CONFIGURAZIONE_MESSAGE_SECURITY_VALORE_ABILITATO.equals(filtroMessageSecurityStato) ||
					Filtri.FILTRO_CONFIGURAZIONE_MESSAGE_SECURITY_VALORE_DISABILITATO.equals(filtroMessageSecurityStato)) {
				ISQLQueryObject sqlStato = SQLObjectFactory.createSQLQueryObject(tipoDB);
				if(erogazioni) {
					sqlStato.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				}
				else {
					sqlStato.addFromTable(CostantiDB.PORTE_DELEGATE);
				}
				if(Filtri.FILTRO_CONFIGURAZIONE_MESSAGE_SECURITY_VALORE_ABILITATO.equals(filtroMessageSecurityStato)) {
					sqlStato.setANDLogicOperator(false);
					sqlStato.addWhereIsNotNullCondition(aliasCONFIG_PORTA+".security_request_mode");
					sqlStato.addWhereIsNotNullCondition(aliasCONFIG_PORTA+".security_response_mode");
				}
				else {
					sqlStato.setANDLogicOperator(true);
					sqlStato.addWhereIsNullCondition(aliasCONFIG_PORTA+".security_request_mode");
					sqlStato.addWhereIsNullCondition(aliasCONFIG_PORTA+".security_response_mode");
				}
				sql.addWhereCondition(sqlStato.createSQLConditions());	
			}
			else if(Filtri.FILTRO_CONFIGURAZIONE_MESSAGE_SECURITY_STATO_VALORE_ABILITATO_RICHIESTA.equals(filtroMessageSecurityStato)) {
				sql.addWhereIsNotNullCondition(aliasCONFIG_PORTA+".security_request_mode");
			}
			else if(Filtri.FILTRO_CONFIGURAZIONE_MESSAGE_SECURITY_STATO_VALORE_ABILITATO_RISPOSTA.equals(filtroMessageSecurityStato)) {
				sql.addWhereIsNotNullCondition(aliasCONFIG_PORTA+".security_response_mode");
			}
		}
		
		if(filtroMTOMStato!=null && !"".equals(filtroMTOMStato)) {
			if(Filtri.FILTRO_CONFIGURAZIONE_MTOM_VALORE_ABILITATO.equals(filtroMTOMStato) ||
					Filtri.FILTRO_CONFIGURAZIONE_MTOM_VALORE_DISABILITATO.equals(filtroMTOMStato)) {
				ISQLQueryObject sqlStato = SQLObjectFactory.createSQLQueryObject(tipoDB);
				if(erogazioni) {
					sqlStato.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				}
				else {
					sqlStato.addFromTable(CostantiDB.PORTE_DELEGATE);
				}
				if(Filtri.FILTRO_CONFIGURAZIONE_MTOM_VALORE_ABILITATO.equals(filtroMTOMStato)) {
					sqlStato.setANDLogicOperator(false);
					sqlStato.addWhereCondition(true,  aliasCONFIG_PORTA+".mtom_request_mode is not null", aliasCONFIG_PORTA+".mtom_request_mode <> 'disable'");
					sqlStato.addWhereCondition(true,  aliasCONFIG_PORTA+".mtom_response_mode is not null", aliasCONFIG_PORTA+".mtom_response_mode <> 'disable'");
				}
				else {
					sqlStato.setANDLogicOperator(true);
					sqlStato.addWhereCondition(false,  aliasCONFIG_PORTA+".mtom_request_mode is null", aliasCONFIG_PORTA+".mtom_request_mode = 'disable'");
					sqlStato.addWhereCondition(false,  aliasCONFIG_PORTA+".mtom_response_mode is null", aliasCONFIG_PORTA+".mtom_response_mode = 'disable'");
				}
				sql.addWhereCondition(sqlStato.createSQLConditions());	
			}
			else if(Filtri.FILTRO_CONFIGURAZIONE_MTOM_STATO_VALORE_ABILITATO_RICHIESTA.equals(filtroMTOMStato)) {
				sql.addWhereCondition(true,  aliasCONFIG_PORTA+".mtom_request_mode is not null", aliasCONFIG_PORTA+".mtom_request_mode <> 'disable'");
			}
			else if(Filtri.FILTRO_CONFIGURAZIONE_MTOM_STATO_VALORE_ABILITATO_RISPOSTA.equals(filtroMTOMStato)) {
				sql.addWhereCondition(true,  aliasCONFIG_PORTA+".mtom_response_mode is not null", aliasCONFIG_PORTA+".mtom_response_mode <> 'disable'");
			}
		}
		
		if(filtroTrasformazione!=null && !"".equals(filtroTrasformazione)) {		
			String aliasTRANSFORM = "tra_c";
			ISQLQueryObject sqlTrasformazione = SQLObjectFactory.createSQLQueryObject(tipoDB);
			if(erogazioni) {
				sqlTrasformazione.addFromTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI, aliasTRANSFORM);
			}
			else {
				sqlTrasformazione.addFromTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI, aliasTRANSFORM);
			}
			sqlTrasformazione.addSelectField(aliasTRANSFORM, "id");
			sqlTrasformazione.setANDLogicOperator(true);
			sqlTrasformazione.addWhereCondition(aliasCONFIG_PORTA+".id="+aliasTRANSFORM+".id_porta");
			sqlTrasformazione.addWhereLikeCondition(aliasTRANSFORM+".stato", "abilitato", false, false, false);
			sql.addWhereExistsCondition(Filtri.FILTRO_CONFIGURAZIONE_TRASFORMAZIONE_STATO_VALORE_ABILITATO.equals(filtroTrasformazione) ? false : true, sqlTrasformazione);
		}
		
		if(filtroCorrelazioneApplicativa!=null && !"".equals(filtroCorrelazioneApplicativa)) {
			
			// Richiesta
			String aliasCorrelazioneRichiesta = erogazioni ? CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE : CostantiDB.PORTE_DELEGATE_CORRELAZIONE;
			ISQLQueryObject sqlCorrelazioneRichiesta = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlCorrelazioneRichiesta.addFromTable(aliasCorrelazioneRichiesta);
			sqlCorrelazioneRichiesta.setANDLogicOperator(true);
			sqlCorrelazioneRichiesta.addWhereCondition(aliasCONFIG_PORTA+".id="+aliasCorrelazioneRichiesta+".id_porta");
			
			// Risposta
			String aliasCorrelazioneRisposta = erogazioni ? CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE_RISPOSTA : CostantiDB.PORTE_DELEGATE_CORRELAZIONE_RISPOSTA;
			ISQLQueryObject sqlCorrelazioneRisposta = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlCorrelazioneRisposta.addFromTable(aliasCorrelazioneRisposta);
			sqlCorrelazioneRisposta.setANDLogicOperator(true);
			sqlCorrelazioneRisposta.addWhereCondition(aliasCONFIG_PORTA+".id="+aliasCorrelazioneRisposta+".id_porta");
			
			if(Filtri.FILTRO_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_VALORE_ABILITATO.equals(filtroCorrelazioneApplicativa) ||
					Filtri.FILTRO_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_VALORE_DISABILITATO.equals(filtroCorrelazioneApplicativa)) {
								
				ISQLQueryObject sqlStato = SQLObjectFactory.createSQLQueryObject(tipoDB);
				if(erogazioni) {
					sqlStato.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				}
				else {
					sqlStato.addFromTable(CostantiDB.PORTE_DELEGATE);
				}
				if(Filtri.FILTRO_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_VALORE_ABILITATO.equals(filtroCorrelazioneApplicativa)) {
					sqlStato.setANDLogicOperator(false);
					sqlStato.addWhereExistsCondition(false, sqlCorrelazioneRichiesta);
					sqlStato.addWhereExistsCondition(false, sqlCorrelazioneRisposta);
				}
				else {
					sqlStato.setANDLogicOperator(true);
					sqlStato.addWhereExistsCondition(true, sqlCorrelazioneRichiesta);
					sqlStato.addWhereExistsCondition(true, sqlCorrelazioneRisposta);
				}
				sql.addWhereCondition(sqlStato.createSQLConditions());	
			}
			else if(Filtri.FILTRO_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_STATO_VALORE_ABILITATO_RICHIESTA.equals(filtroCorrelazioneApplicativa)) {
				sql.addWhereExistsCondition(false, sqlCorrelazioneRichiesta);
			}
			else if(Filtri.FILTRO_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_STATO_VALORE_ABILITATO_RISPOSTA.equals(filtroCorrelazioneApplicativa)) {
				sql.addWhereExistsCondition(false, sqlCorrelazioneRisposta);
			}
		}
		
		if(filtroConfigurazioneDumpTipo!=null && !"".equals(filtroConfigurazioneDumpTipo)) {
			addFiltroConfigurazioneDump(tipoDB,
					aliasCONFIG_PORTA,
					filtroConfigurazioneDumpTipo, erogazioni,
					sql);
		}
		
		if(filtroCORS!=null && !"".equals(filtroCORS)) {
			if(Filtri.FILTRO_CONFIGURAZIONE_CORS_TIPO_VALORE_DEFAULT.equals(filtroCORS)) {
				sql.addWhereIsNullCondition(aliasCONFIG_PORTA+".cors_stato");
			}
			else if(Filtri.FILTRO_CONFIGURAZIONE_CORS_TIPO_VALORE_RIDEFINITO_ABILITATO.equals(filtroCORS)) {
				sql.addWhereLikeCondition(aliasCONFIG_PORTA+".cors_stato", "abilitato", false, false, false);
				
				if(filtroCORS_origin!=null && !"".equals(filtroCORS_origin)) {
					sql.addWhereLikeCondition(aliasCONFIG_PORTA+".cors_allow_origins", filtroCORS_origin, LikeConfig.contains(true));
				}
			}
			else if(Filtri.FILTRO_CONFIGURAZIONE_CORS_TIPO_VALORE_RIDEFINITO_DISABILITATO.equals(filtroCORS)) {
				sql.addWhereLikeCondition(aliasCONFIG_PORTA+".cors_stato", "disabilitato", false, false, false);
			}
		}
		
		sqlQueryObject.addWhereExistsCondition(false, sql);
	}
	public static void addFiltroConfigurazioneDump(String tipoDB,
			String aliasCONFIG_PORTA,
			String filtroConfigurazioneDumpTipo, boolean erogazioni,
			ISQLQueryObject sqlQueryQuery) throws Exception {

		String aliasDUMP_CONFIG = "dump_c";
		
		ISQLQueryObject sql = SQLObjectFactory.createSQLQueryObject(tipoDB);
		sql.addFromTable(CostantiDB.DUMP_CONFIGURAZIONE, aliasDUMP_CONFIG);
		sql.addSelectField(aliasDUMP_CONFIG, "id_proprietario");
		sql.setANDLogicOperator(true);
		if(erogazioni) {
			sql.addWhereCondition(aliasCONFIG_PORTA+".id="+aliasDUMP_CONFIG+".id_proprietario");
			sql.addWhereLikeCondition(aliasDUMP_CONFIG+".proprietario", "pa", false, false, false);
		}
		else {
			sql.addWhereCondition(aliasCONFIG_PORTA+".id="+aliasDUMP_CONFIG+".id_proprietario");
			sql.addWhereLikeCondition(aliasDUMP_CONFIG+".proprietario", "pd", false, false, false);
		}	
		
		//Prepariamo anzi una utility per ogni singola voce e poi ad ogni if la si chiama!
		
		boolean and = true;
		boolean or = false;
		boolean notExists = true;
		boolean exists = false;
		
		if(Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_DEFAULT.equals(filtroConfigurazioneDumpTipo)) {
			sqlQueryQuery.addWhereExistsCondition(notExists, sql);	
		}
		else if(Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO.equals(filtroConfigurazioneDumpTipo)
				||
				Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER.equals(filtroConfigurazioneDumpTipo)
			    ||
			    Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_RICHIESTA.equals(filtroConfigurazioneDumpTipo)
				||
				Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RICHIESTA.equals(filtroConfigurazioneDumpTipo)
				||
				Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_RICHIESTA_INGRESSO.equals(filtroConfigurazioneDumpTipo)
				||
				Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RICHIESTA_INGRESSO.equals(filtroConfigurazioneDumpTipo)
				||
				Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_RICHIESTA_USCITA.equals(filtroConfigurazioneDumpTipo)
				||
				Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RICHIESTA_USCITA.equals(filtroConfigurazioneDumpTipo)
				||
			    Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_RISPOSTA.equals(filtroConfigurazioneDumpTipo)
				||
				Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RISPOSTA.equals(filtroConfigurazioneDumpTipo)
				||
				Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_RISPOSTA_INGRESSO.equals(filtroConfigurazioneDumpTipo)
				||
				Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RISPOSTA_INGRESSO.equals(filtroConfigurazioneDumpTipo)
				||
				Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_RISPOSTA_USCITA.equals(filtroConfigurazioneDumpTipo)
				||
				Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RISPOSTA_USCITA.equals(filtroConfigurazioneDumpTipo)
				){
			
			boolean soloHeader = (
					Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER.equals(filtroConfigurazioneDumpTipo)
					||
					Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RICHIESTA.equals(filtroConfigurazioneDumpTipo)
					||
					Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RICHIESTA_INGRESSO.equals(filtroConfigurazioneDumpTipo)
					||
					Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RICHIESTA_USCITA.equals(filtroConfigurazioneDumpTipo)
					||
					Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RISPOSTA.equals(filtroConfigurazioneDumpTipo)
					||
					Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RISPOSTA_INGRESSO.equals(filtroConfigurazioneDumpTipo)
					||
					Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RISPOSTA_USCITA.equals(filtroConfigurazioneDumpTipo)
					);
			BooleanNullable payload = soloHeader ? BooleanNullable.FALSE() : BooleanNullable.TRUE();
			BooleanNullable headers = BooleanNullable.TRUE();
			
			boolean condizione_payload_headers = soloHeader ? and : or;
			
			List<ISQLQueryObject> sqlMessages = new ArrayList<>();
			
			if(Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO.equals(filtroConfigurazioneDumpTipo)
					||
					Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER.equals(filtroConfigurazioneDumpTipo)
				    ||
				    Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_RICHIESTA.equals(filtroConfigurazioneDumpTipo)
					||
					Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RICHIESTA.equals(filtroConfigurazioneDumpTipo)
					||
					Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_RICHIESTA_INGRESSO.equals(filtroConfigurazioneDumpTipo)
					||
					Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RICHIESTA_INGRESSO.equals(filtroConfigurazioneDumpTipo)
					) {
				ISQLQueryObject sqlRichiestaIngresso = SQLObjectFactory.createSQLQueryObject(tipoDB);
				_setFiltriDumpRegolaConfigurazioneDump(tipoDB,
						aliasDUMP_CONFIG, 
						"id_richiesta_ingresso", payload, headers, condizione_payload_headers,
						sqlRichiestaIngresso, exists);
				sqlMessages.add(sqlRichiestaIngresso);
			}
			
			if(Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO.equals(filtroConfigurazioneDumpTipo)
					||
					Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER.equals(filtroConfigurazioneDumpTipo)
				    ||
				    Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_RICHIESTA.equals(filtroConfigurazioneDumpTipo)
					||
					Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RICHIESTA.equals(filtroConfigurazioneDumpTipo)
					||
					Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_RICHIESTA_USCITA.equals(filtroConfigurazioneDumpTipo)
					||
					Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RICHIESTA_USCITA.equals(filtroConfigurazioneDumpTipo)
					) {
				ISQLQueryObject sqlRichiestaUscita = SQLObjectFactory.createSQLQueryObject(tipoDB);
				_setFiltriDumpRegolaConfigurazioneDump(tipoDB,
						aliasDUMP_CONFIG, 
						"id_richiesta_uscita", payload, headers, condizione_payload_headers,
						sqlRichiestaUscita, exists);
				sqlMessages.add(sqlRichiestaUscita);
			}
			
			if(Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO.equals(filtroConfigurazioneDumpTipo)
					||
					Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER.equals(filtroConfigurazioneDumpTipo)
				    ||
				    Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_RISPOSTA.equals(filtroConfigurazioneDumpTipo)
					||
					Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RISPOSTA.equals(filtroConfigurazioneDumpTipo)
					||
					Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_RISPOSTA_INGRESSO.equals(filtroConfigurazioneDumpTipo)
					||
					Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RISPOSTA_INGRESSO.equals(filtroConfigurazioneDumpTipo)
					) {
				ISQLQueryObject sqlRispostaIngresso = SQLObjectFactory.createSQLQueryObject(tipoDB);
				_setFiltriDumpRegolaConfigurazioneDump(tipoDB,
						aliasDUMP_CONFIG, 
						"id_risposta_ingresso", payload, headers, condizione_payload_headers,
						sqlRispostaIngresso, exists);
				sqlMessages.add(sqlRispostaIngresso);
			}
			
			if(Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO.equals(filtroConfigurazioneDumpTipo)
					||
					Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER.equals(filtroConfigurazioneDumpTipo)
				    ||
				    Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_RISPOSTA.equals(filtroConfigurazioneDumpTipo)
					||
					Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RISPOSTA.equals(filtroConfigurazioneDumpTipo)
					||
					Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_RISPOSTA_USCITA.equals(filtroConfigurazioneDumpTipo)
					||
					Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RISPOSTA_USCITA.equals(filtroConfigurazioneDumpTipo)
					) {
				ISQLQueryObject sqlRispostaUscita = SQLObjectFactory.createSQLQueryObject(tipoDB);
				_setFiltriDumpRegolaConfigurazioneDump(tipoDB,
						aliasDUMP_CONFIG, 
						"id_risposta_uscita", payload, headers, condizione_payload_headers,
						sqlRispostaUscita, exists);
				sqlMessages.add(sqlRispostaUscita);
			}
			
			_setFiltriDumpRegolaConfigurazioneDump(tipoDB,
					sql, or,
					sqlMessages.toArray(new ISQLQueryObject[1]));
			sqlQueryQuery.addWhereExistsCondition(exists, sql);	
		}
		else if(Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_DISABILITATO.equals(filtroConfigurazioneDumpTipo)
				||
				Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_DISABILITATO_RICHIESTA.equals(filtroConfigurazioneDumpTipo)
				||
				Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_DISABILITATO_RISPOSTA.equals(filtroConfigurazioneDumpTipo)
				){
			
			BooleanNullable payload = BooleanNullable.FALSE();
			BooleanNullable headers = BooleanNullable.FALSE();
			
			boolean condizione_payload_headers = and;
			
			List<ISQLQueryObject> sqlMessages = new ArrayList<>();
			
			if(Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_DISABILITATO.equals(filtroConfigurazioneDumpTipo)
					||
					Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_DISABILITATO_RICHIESTA.equals(filtroConfigurazioneDumpTipo)
					) {
				ISQLQueryObject sqlRichiestaIngresso = SQLObjectFactory.createSQLQueryObject(tipoDB);
				_setFiltriDumpRegolaConfigurazioneDump(tipoDB,
						aliasDUMP_CONFIG, 
						"id_richiesta_ingresso", payload, headers, condizione_payload_headers,
						sqlRichiestaIngresso, exists);
				sqlMessages.add(sqlRichiestaIngresso);
				
				ISQLQueryObject sqlRichiestaUscita = SQLObjectFactory.createSQLQueryObject(tipoDB);
				_setFiltriDumpRegolaConfigurazioneDump(tipoDB,
						aliasDUMP_CONFIG, 
						"id_richiesta_uscita", payload, headers, condizione_payload_headers,
						sqlRichiestaUscita, exists);
				sqlMessages.add(sqlRichiestaUscita);
			}

			if(Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_DISABILITATO.equals(filtroConfigurazioneDumpTipo)
					||
					Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_DISABILITATO_RISPOSTA.equals(filtroConfigurazioneDumpTipo)
					) {
				ISQLQueryObject sqlRispostaIngresso = SQLObjectFactory.createSQLQueryObject(tipoDB);
				_setFiltriDumpRegolaConfigurazioneDump(tipoDB,
						aliasDUMP_CONFIG, 
						"id_risposta_ingresso", payload, headers, condizione_payload_headers,
						sqlRispostaIngresso, exists);
				sqlMessages.add(sqlRispostaIngresso);
			
				ISQLQueryObject sqlRispostaUscita = SQLObjectFactory.createSQLQueryObject(tipoDB);
				_setFiltriDumpRegolaConfigurazioneDump(tipoDB,
						aliasDUMP_CONFIG, 
						"id_risposta_uscita", payload, headers, condizione_payload_headers,
						sqlRispostaUscita, exists);
				sqlMessages.add(sqlRispostaUscita);
			}
			
			_setFiltriDumpRegolaConfigurazioneDump(tipoDB,
					sql, and,
					sqlMessages.toArray(new ISQLQueryObject[1]));
			sqlQueryQuery.addWhereExistsCondition(exists, sql);	
		}
		
	}
	private static void _setFiltriDumpRegolaConfigurazioneDump(String tipoDB,
			ISQLQueryObject sqlQueryObjectConditions, boolean and,
			ISQLQueryObject ... sqlQueryObjectMessage) throws Exception {		
		List<String> conditions = new ArrayList<>();
		
		if(sqlQueryObjectMessage!=null && sqlQueryObjectMessage.length>0) {
			for (ISQLQueryObject sql : sqlQueryObjectMessage) {
				conditions.add(sql.createSQLConditions());	
			}
		}
		
		if(conditions==null || conditions.isEmpty()) {
			throw new Exception("Usage error");
		}
		sqlQueryObjectConditions.addWhereCondition(and, conditions.toArray(new String[1]));
	}
	private static void _setFiltriDumpRegolaConfigurazioneDump(String tipoDB,
			String aliasDUMP_CONFIG, 
			String colonna, BooleanNullable payload, BooleanNullable headers, boolean and,
			ISQLQueryObject sqlQueryObjectConditions, boolean notExists) throws Exception {
		String aliasDUMP_CONFIG_REGOLE = "dump_cr";
		
		ISQLQueryObject sql = SQLObjectFactory.createSQLQueryObject(tipoDB);
		sql.addSelectField("id");
		sql.addFromTable(CostantiDB.DUMP_CONFIGURAZIONE_REGOLA, aliasDUMP_CONFIG_REGOLE);
		sql.setANDLogicOperator(true);
		sql.addWhereCondition(aliasDUMP_CONFIG+"."+colonna+"="+aliasDUMP_CONFIG_REGOLE+".id");
		if(payload!=null && payload.getValue()!=null &&
				headers!=null && headers.getValue()!=null) {
			sql.addWhereCondition(and, 
					sql.getWhereLikeCondition("payload", payload.getValue()? "abilitato" : "disabilitato", false, false, false),
					sql.getWhereLikeCondition("headers", headers.getValue()? "abilitato" : "disabilitato", false, false, false));
		}
		else if(payload!=null && payload.getValue()!=null) {
			sql.addWhereLikeCondition("payload", payload.getValue()? "abilitato" : "disabilitato", false, false, false);
		}
		else if(headers!=null && headers.getValue()!=null) {
			sql.addWhereLikeCondition("headers", headers.getValue()? "abilitato" : "disabilitato", false, false, false);
		}
		else {
			throw new Exception("Usage error");
		}
		sqlQueryObjectConditions.addWhereExistsCondition(notExists, sql);
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


