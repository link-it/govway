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

package org.openspcoop2.core.mapping;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.id.IdentificativiErogazione;
import org.openspcoop2.core.id.IdentificativiFruizione;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * Funzioni di utilita utilizzate dai driver
 *
 * @author Andrea Poli (apoli@link.it)
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
	
	public static List<MappingErogazionePortaApplicativa> mappingErogazionePortaApplicativaList(Connection con,	String tipoDB, 
			IDServizio idServizio, 
			ISearch ricerca,
			boolean orderByDescrizione) throws CoreException{
		return _mappingErogazionePortaApplicativaList(con, tipoDB, 
				idServizio, DBUtils.getIdServizio(idServizio.getNome(), idServizio.getTipo(), idServizio.getVersione(), 
						idServizio.getSoggettoErogatore().getNome(), idServizio.getSoggettoErogatore().getTipo(), 
						con, tipoDB), 
				ricerca,CostantiDB.SOGGETTI,
				orderByDescrizione);
	}
	public static List<MappingErogazionePortaApplicativa> mappingErogazionePortaApplicativaList(Connection con,	String tipoDB, 
			IDServizio idServizio, 
			ISearch ricerca,String tabellaSoggetti,
			boolean orderByDescrizione) throws CoreException{
		return _mappingErogazionePortaApplicativaList(con, tipoDB, 
				idServizio, DBUtils.getIdServizio(idServizio.getNome(), idServizio.getTipo(), idServizio.getVersione(), 
						idServizio.getSoggettoErogatore().getNome(), idServizio.getSoggettoErogatore().getTipo(), 
						con, tipoDB), 
				ricerca,tabellaSoggetti,
				orderByDescrizione);
	}
	
	public static List<MappingErogazionePortaApplicativa> mappingErogazionePortaApplicativaList(Connection con,	String tipoDB, 
			IDServizio idServizio,
			boolean orderByDescrizione) throws CoreException{
		return _mappingErogazionePortaApplicativaList(con, tipoDB, 
				idServizio, DBUtils.getIdServizio(idServizio.getNome(), idServizio.getTipo(), idServizio.getVersione(), 
						idServizio.getSoggettoErogatore().getNome(), idServizio.getSoggettoErogatore().getTipo(), 
						con, tipoDB), 
				null,CostantiDB.SOGGETTI,
				orderByDescrizione);
	}
	public static List<MappingErogazionePortaApplicativa> mappingErogazionePortaApplicativaList(Connection con,	String tipoDB, 
			IDServizio idServizio, 
			String tabellaSoggetti,
			boolean orderByDescrizione) throws CoreException{
		return _mappingErogazionePortaApplicativaList(con, tipoDB, 
				idServizio, DBUtils.getIdServizio(idServizio.getNome(), idServizio.getTipo(), idServizio.getVersione(), 
						idServizio.getSoggettoErogatore().getNome(), idServizio.getSoggettoErogatore().getTipo(), 
						con, tipoDB), 
				null,tabellaSoggetti,
				orderByDescrizione);
	}
	
	public static List<MappingErogazionePortaApplicativa> mappingErogazionePortaApplicativaList(Connection con,	String tipoDB, 
			IDServizio idServizio, long idServizioAsLong, 
			ISearch ricerca,
			boolean orderByDescrizione) throws CoreException{
		return _mappingErogazionePortaApplicativaList(con, tipoDB, 
				idServizio, idServizioAsLong, 
				ricerca,CostantiDB.SOGGETTI,
				orderByDescrizione);
	}
	public static List<MappingErogazionePortaApplicativa> mappingErogazionePortaApplicativaList(Connection con,	String tipoDB, 
			IDServizio idServizio, long idServizioAsLong, 
			ISearch ricerca,String tabellaSoggetti,
			boolean orderByDescrizione) throws CoreException{
		return _mappingErogazionePortaApplicativaList(con, tipoDB, 
				idServizio, idServizioAsLong, 
				ricerca,tabellaSoggetti,
				orderByDescrizione);
	}
	
	public static List<MappingErogazionePortaApplicativa> mappingErogazionePortaApplicativaList(Connection con,	String tipoDB, 
			IDServizio idServizio, long idServizioAsLong,
			boolean orderByDescrizione) throws CoreException{
		return _mappingErogazionePortaApplicativaList(con, tipoDB, 
				idServizio, idServizioAsLong, 
				null,CostantiDB.SOGGETTI,
				orderByDescrizione);
	}
	public static List<MappingErogazionePortaApplicativa> mappingErogazionePortaApplicativaList(Connection con,	String tipoDB, 
			IDServizio idServizio, long idServizioAsLong, 
			String tabellaSoggetti,
			boolean orderByDescrizione) throws CoreException{
		return _mappingErogazionePortaApplicativaList(con, tipoDB, 
				idServizio, idServizioAsLong, 
				null,tabellaSoggetti,
				orderByDescrizione);
	}
	
	private static List<MappingErogazionePortaApplicativa> _mappingErogazionePortaApplicativaList(Connection con, String tipoDB, 
			IDServizio idServizio, long idServizioAsLong, 
			ISearch ricerca,String tabellaSoggetti,
			boolean orderByDescrizione) throws CoreException{
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
			if(ricerca != null) {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.MAPPING_EROGAZIONE_PA);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_EROGAZIONE_PA+".id_erogazione = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id="+CostantiDB.MAPPING_EROGAZIONE_PA+".id_porta");
				if (!search.equals("")) {
					sqlQueryObject.addWhereCondition(false, 
							sqlQueryObject.getWhereLikeCondition(CostantiDB.MAPPING_EROGAZIONE_PA+".nome", search, true, true));
							//sqlQueryObject.getWhereLikeCondition(CostantiDB.PORTE_APPLICATIVE +".nome_porta", search, true, true));
				} 
				
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				int index = 1;
				stmt.setLong(index++, idServizioAsLong);
				
				risultato = stmt.executeQuery();
				if (risultato.next())
					ricerca.setNumEntries(idLista,risultato.getInt(1));
				risultato.close();
				stmt.close();
			}
			
			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.MAPPING_EROGAZIONE_PA);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField(CostantiDB.MAPPING_EROGAZIONE_PA+".id_erogazione");
			sqlQueryObject.addSelectField(CostantiDB.MAPPING_EROGAZIONE_PA+".id_porta");
			sqlQueryObject.addSelectField(CostantiDB.MAPPING_EROGAZIONE_PA+".is_default");
			sqlQueryObject.addSelectField(CostantiDB.MAPPING_EROGAZIONE_PA+".nome");
			sqlQueryObject.addSelectField(CostantiDB.MAPPING_EROGAZIONE_PA+".descrizione");
			sqlQueryObject.addSelectField(CostantiDB.MAPPING_EROGAZIONE_PA+".id");
			sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".nome_porta");
			sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_EROGAZIONE_PA+".id_erogazione = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id="+CostantiDB.MAPPING_EROGAZIONE_PA+".id_porta");
			
			if (ricerca!=null && !search.equals("")) {
				sqlQueryObject.addWhereCondition(false, 
						sqlQueryObject.getWhereLikeCondition(CostantiDB.MAPPING_EROGAZIONE_PA+".nome", search, true, true));
						//sqlQueryObject.getWhereLikeCondition(CostantiDB.PORTE_APPLICATIVE +".nome_porta", search, true, true));
			} 
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(CostantiDB.MAPPING_EROGAZIONE_PA+".is_default",true);
			if(orderByDescrizione) {
				sqlQueryObject.addOrderBy(CostantiDB.MAPPING_EROGAZIONE_PA+".descrizione");
			}
			else {
				sqlQueryObject.addOrderBy(CostantiDB.MAPPING_EROGAZIONE_PA+".nome");
			}
			sqlQueryObject.setSortType(true);
			if(ricerca != null) {
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
			}
			queryString = sqlQueryObject.createSQLQuery();
			
			stmt = con.prepareStatement(queryString);
			
			int index = 1;
			stmt.setLong(index++, idServizioAsLong);
			
			risultato = stmt.executeQuery();
			
			MappingErogazionePortaApplicativa mapping = null;

			while (risultato.next()) {

				mapping = new MappingErogazionePortaApplicativa();
				
				Long id = risultato.getLong("id");
//				Long idPorta = risultato.getLong("id_porta");
//				Long idErogazione = risultato.getLong("id_erogazione");
				String nome = risultato.getString("nome");
				String descrizione = risultato.getString("descrizione");
				Integer isDefaultInt = risultato.getInt("is_default");
				boolean isDefault = isDefaultInt > 0 ;
				
				mapping.setNome(nome);
				mapping.setDescrizione(descrizione);
				mapping.setTableId(id);
				mapping.setDefault(isDefault);
				// evitiamo una join utilizzo l'id che mi sono passato come parametro
				mapping.setIdServizio(idServizio);
				
				IDPortaApplicativa idPortaApplicativa = new IDPortaApplicativa();
				String nomePorta = risultato.getString("nome_porta");
				idPortaApplicativa.setNome(nomePorta);
				
				IdentificativiErogazione identificativiErogazione = new IdentificativiErogazione();
				identificativiErogazione.setIdServizio(idServizio);
				idPortaApplicativa.setIdentificativiErogazione(identificativiErogazione);
				
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
	
	
	public static void createMappingErogazione(String nome, String descrizione, boolean isDefault, IDServizio idServizio, IDPortaApplicativa idPortaApplicativa,  
			Connection con, String tipoDB) throws CoreException{
		createMappingErogazione(nome, descrizione, isDefault, idServizio, idPortaApplicativa, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static void createMappingErogazione(String nome, String descrizione, boolean isDefault, IDServizio idServizio, IDPortaApplicativa idPortaApplicativa,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException{
		long idServizioLong = DBUtils.getIdAccordoServizioParteSpecifica(idServizio, con, tipoDB);
		if(idServizioLong<=0){
			throw new CoreException("Servizio ["+idServizio.toString()+"] non esistente");
		}
		_createMappingErogazione(nome, descrizione, isDefault, idServizioLong, idPortaApplicativa, con, tipoDB, tabellaSoggetti);
	}
	
	private static void _createMappingErogazione(String nome, String descrizione, boolean isDefault, long idServizioLong, IDPortaApplicativa idPortaApplicativa,
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
			sqlQueryObject.addInsertField("descrizione", "?");
			sqlQueryObject.addInsertField("is_default", "?");
			String queryString = sqlQueryObject.createSQLInsert();
			stmt = con.prepareStatement(queryString);
			int index = 1;
			stmt.setLong(index++, idServizioLong);
			stmt.setLong(index++, idPA);
			stmt.setString(index++, nome);
			stmt.setString(index++, descrizione);
			stmt.setInt(index++, isDefaultInt);
			stmt.executeUpdate();
			stmt.close();
			
		}catch(Exception e){
			throw new CoreException("Creazione Mapping Erogazione fallita: "+e.getMessage(),e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}
	
	
	public static void updateMappingErogazione(long tableId, String descrizione, Connection con, String tipoDB) throws CoreException{
		PreparedStatement stmt = null;

		try {

			if(tableId<=0){
				throw new Exception("TableId non fornito");
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addUpdateTable(CostantiDB.MAPPING_EROGAZIONE_PA);
			sqlQueryObject.addUpdateField("descrizione", "?");
			sqlQueryObject.addWhereCondition("id=?");
			String queryString = sqlQueryObject.createSQLUpdate();
			stmt = con.prepareStatement(queryString);
			int index = 1;
			stmt.setString(index++, descrizione);
			stmt.setLong(index++, tableId);
			stmt.executeUpdate();
			stmt.close();
			
		}catch(Exception e){
			throw new CoreException("updateMappingErogazione error",e);
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
	public static void deleteMappingErogazione(IDServizio idServizio, boolean deletePorte,
			Connection con, String tipoDB) throws CoreException{
		deleteMappingErogazione(idServizio, deletePorte, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static void deleteMappingErogazione(IDServizio idServizio, boolean deletePorte,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException{
		List<IDPortaApplicativa> list = null;
		if(deletePorte) {
			list = getIDPorteApplicativeAssociate(idServizio, con, tipoDB);
		}
		deleteMappingErogazione(idServizio, null, con, tipoDB, tabellaSoggetti);
		if(list!=null && list.size()>0) {
			for (IDPortaApplicativa idPortaApplicativa : list) {
				PreparedStatement stmt = null;
				try {
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE);
					sqlQueryObject.addWhereCondition("id=?");
					sqlQueryObject.setANDLogicOperator(true);
					String queryString = sqlQueryObject.createSQLDelete();
					stmt = con.prepareStatement(queryString);
					stmt.setLong(1, DBUtils.getIdPortaApplicativa(idPortaApplicativa.getNome(), con, tipoDB));
					stmt.executeUpdate();
					stmt.close();
				}catch(Exception e){
					throw new CoreException("deletePAMappingErogazione error",e);
				} finally {

					//Chiudo statement and resultset
					try{
						if(stmt!=null) stmt.close();
					}catch (Exception e) {
						//ignore
					}

				}
			}
		}
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
		

	public static IDPortaApplicativa getIDPortaApplicativaAssociataDefault(IDServizio idServizio,
			Connection con, String tipoDB) throws CoreException {
		return getIDPortaApplicativaAssociataDefault(idServizio, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static IDPortaApplicativa getIDPortaApplicativaAssociataDefault(IDServizio idServizio,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		long idServizioLong = DBUtils.getIdAccordoServizioParteSpecifica(idServizio, con, tipoDB);
		if(idServizioLong<=0){
			throw new CoreException("Servizio ["+idServizio.toString()+"] non esistente");
		}
		List<IDPortaApplicativa> l = _getIDPorteApplicativeAssociate(idServizioLong, true, null, con, tipoDB, tabellaSoggetti,
				idServizio);
		if(l!=null && l.size()>0) {
			if(l.size()>1) {
				throw new CoreException("Esiste più di un mapping di default per l'erogazione del servizio ["+idServizio.toString()+"]");
			}
			return l.get(0);
		}
		return null;
	}
	
	public static IDPortaApplicativa getIDPortaApplicativaAssociataAzione(IDServizio idServizio,
			Connection con, String tipoDB) throws CoreException {
		return getIDPortaApplicativaAssociataAzione(idServizio, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static IDPortaApplicativa getIDPortaApplicativaAssociataAzione(IDServizio idServizio,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		if(idServizio.getAzione()==null) {
			throw new CoreException("Azione non indicata nel parametro 'idServizio'");
		}
		long idServizioLong = DBUtils.getIdAccordoServizioParteSpecifica(idServizio, con, tipoDB);
		if(idServizioLong<=0){
			throw new CoreException("Servizio ["+idServizio.toString()+"] non esistente");
		}
		List<IDPortaApplicativa> l = _getIDPorteApplicativeAssociate(idServizioLong, false, idServizio.getAzione(), con, tipoDB, tabellaSoggetti,
				idServizio);
		if(l!=null && l.size()>0) {
			if(l.size()>1) {
				throw new CoreException("Esiste più di un mapping per l'erogazione dell'azione '"+idServizio.getAzione()+"' del servizio ["+idServizio.toString()+"]");
			}
			return l.get(0);
		}
		return null;
	}
	
	public static List<IDPortaApplicativa> getIDPorteApplicativeAssociate(IDServizio idServizio,
			Connection con, String tipoDB) throws CoreException {
		return getIDPorteApplicativeAssociate(idServizio, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static List<IDPortaApplicativa> getIDPorteApplicativeAssociate(IDServizio idServizio, 
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		long idServizioLong = DBUtils.getIdAccordoServizioParteSpecifica(idServizio, con, tipoDB);
		if(idServizioLong<=0){
			throw new CoreException("Servizio ["+idServizio.toString()+"] non esistente");
		}
		return _getIDPorteApplicativeAssociate(idServizioLong, false, null, con, tipoDB, tabellaSoggetti,
				idServizio);
	}
	
	private static List<IDPortaApplicativa> _getIDPorteApplicativeAssociate(long idServizioLong, boolean defaultMapping, String azioneMapping,
			Connection con, String tipoDB,String tabellaSoggetti,
			IDServizio idServizio) throws CoreException {
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {

			if(idServizioLong<=0){
				throw new Exception("IdServizio non fornito");
			}

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.MAPPING_EROGAZIONE_PA);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".nome_porta");
			sqlQueryObject.addWhereCondition("id_erogazione = ?");
			if(defaultMapping) {
				sqlQueryObject.addWhereCondition("is_default = ?");
			}
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id="+CostantiDB.MAPPING_EROGAZIONE_PA+".id_porta");
			if(azioneMapping!=null) {
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AZIONI);
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id="+CostantiDB.PORTE_APPLICATIVE_AZIONI+".id_porta");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_AZIONI+".azione = ?");
			}
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			int indexStmt = 1;
			stm.setLong(indexStmt++,idServizioLong);
			if(defaultMapping) {
				stm.setInt(indexStmt++,1);
			}
			if(azioneMapping!=null) {
				stm.setString(indexStmt++,azioneMapping);
			}
		
			rs = stm.executeQuery();
			List<IDPortaApplicativa> list = new ArrayList<IDPortaApplicativa>();
			while (rs.next()) {
				IDPortaApplicativa idPA = new IDPortaApplicativa();
				String nome = rs.getString("nome_porta");
				idPA.setNome(nome);
				
				IdentificativiErogazione identificativiErogazione = new IdentificativiErogazione();
				identificativiErogazione.setIdServizio(idServizio);
				idPA.setIdentificativiErogazione(identificativiErogazione);
				
				list.add(idPA);
			}
			if(list.size()<=0){
				return null;
			}
			else {
				return list;
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
	
	public static boolean existsIDPortaApplicativaAssociataDefault(IDServizio idServizio,
			Connection con, String tipoDB) throws CoreException {
		return existsIDPortaApplicativaAssociataDefault(idServizio, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static boolean existsIDPortaApplicativaAssociataDefault(IDServizio idServizio, 
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		long idServizioLong = DBUtils.getIdAccordoServizioParteSpecifica(idServizio, con, tipoDB);
		if(idServizioLong<=0){
			throw new CoreException("Servizio ["+idServizio.toString()+"] non esistente");
		}
		int c = _countIDPorteApplicativeAssociate(idServizioLong, true, null, con, tipoDB, tabellaSoggetti);
		if(c<=0) {
			return false;
		}
		else {
			if(c>1) {
				throw new CoreException("Esiste più di un mapping di default per l'erogazione del servizio ["+idServizio.toString()+"]");
			}
			return true;
		}
	}
	
	public static boolean existsIDPortaApplicativaAssociataAzione(IDServizio idServizio,
			Connection con, String tipoDB) throws CoreException {
		return existsIDPortaApplicativaAssociataAzione(idServizio, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static boolean existsIDPortaApplicativaAssociataAzione(IDServizio idServizio, 
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		if(idServizio.getAzione()==null) {
			throw new CoreException("Azione non indicata nel parametro 'idServizio'");
		}
		long idServizioLong = DBUtils.getIdAccordoServizioParteSpecifica(idServizio, con, tipoDB);
		if(idServizioLong<=0){
			throw new CoreException("Servizio ["+idServizio.toString()+"] non esistente");
		}
		int c = _countIDPorteApplicativeAssociate(idServizioLong, false, idServizio.getAzione(), con, tipoDB, tabellaSoggetti);
		if(c<=0) {
			return false;
		}
		else {
			if(c>1) {
				throw new CoreException("Esiste più di un mapping per l'erogazione dell'azione '"+idServizio.getAzione()+"' del servizio ["+idServizio.toString()+"]");
			}
			return true;
		}
	}
	
	public static boolean existsIDPorteApplicativeAssociate(IDServizio idServizio,
			Connection con, String tipoDB) throws CoreException {
		return existsIDPorteApplicativeAssociate(idServizio, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static boolean existsIDPorteApplicativeAssociate(IDServizio idServizio, 
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		long idServizioLong = DBUtils.getIdAccordoServizioParteSpecifica(idServizio, con, tipoDB);
		if(idServizioLong<=0){
			throw new CoreException("Servizio ["+idServizio.toString()+"] non esistente");
		}
		return _countIDPorteApplicativeAssociate(idServizioLong, false, null, con, tipoDB, tabellaSoggetti)>0;
	}
	
	private static int _countIDPorteApplicativeAssociate(long idServizioLong, boolean defaultMapping, String azioneMapping, 
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
			if(defaultMapping) {
				sqlQueryObject.addWhereCondition("is_default = ?");
			}
			if(azioneMapping!=null) {
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AZIONI);
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id="+CostantiDB.MAPPING_EROGAZIONE_PA+".id_porta");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id="+CostantiDB.PORTE_APPLICATIVE_AZIONI+".id_porta");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_AZIONI+".azione = ?");
			}
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			int indexStmt = 1;
			stm.setLong(indexStmt++,idServizioLong);
			if(defaultMapping) {
				stm.setInt(indexStmt++,1);
			}
			if(azioneMapping!=null) {
				stm.setString(indexStmt++,azioneMapping);
			}
		
			rs = stm.executeQuery();
			int c = 0;
			while(rs.next()) {
				c++;
			}
			return c;
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
		if(idServizioLong<=0){
			throw new CoreException("IdServizio non fornito");
		}
		long idPA = DBUtils.getIdPortaApplicativa(idPortaApplicativa.getNome(), con, tipoDB);
		if(idPA<=0){
			throw new CoreException("PortaApplicativa ["+idPortaApplicativa+"] non esistente");
		}

		PreparedStatement stm = null;
		ResultSet rs = null;

		try {

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
	
	
	public static MappingErogazionePortaApplicativa getMappingErogazione(IDServizio idServizio, IDPortaApplicativa idPortaApplicativa,
			Connection con, String tipoDB) throws CoreException {
		return getMappingErogazione(idServizio, idPortaApplicativa, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static MappingErogazionePortaApplicativa getMappingErogazione(IDServizio idServizio, IDPortaApplicativa idPortaApplicativa,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		long idServizioLong = DBUtils.getIdAccordoServizioParteSpecifica(idServizio, con, tipoDB);
		if(idServizioLong<=0){
			throw new CoreException("Servizio ["+idServizio.toString()+"] non esistente");
		}
		
		long idPA = DBUtils.getIdPortaApplicativa(idPortaApplicativa.getNome(), con, tipoDB);
		if(idPA<=0){
			throw new CoreException("PortaApplicativa ["+idPortaApplicativa+"] non esistente");
		}
		
		MappingErogazionePortaApplicativa mapping = _getMappingErogazione(idServizioLong, idPA, con, tipoDB, tabellaSoggetti);
		mapping.setIdPortaApplicativa(idPortaApplicativa);
		mapping.setIdServizio(idServizio);
		return mapping;
	}
	private static MappingErogazionePortaApplicativa _getMappingErogazione(long idServizioLong, long idPA,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {

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
				MappingErogazionePortaApplicativa mapping = new MappingErogazionePortaApplicativa();
				int isDefault = rs.getInt("is_default");
				mapping.setDefault(isDefault == 1);
				
				mapping.setTableId(rs.getLong("id"));
				
				mapping.setNome(rs.getString("nome"));
				mapping.setDescrizione(rs.getString("descrizione"));
				
				return mapping;
			}
			else{
				throw new CoreException("Mapping tra PA (id:"+idPA+") e servizio (id:"+idServizioLong+") non esistente");
			}
		}catch(CoreException de){
			throw de;
		}
		catch(Exception e){
			throw new CoreException("getMappingErogazione error",e);
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
	
	public static List<MappingFruizionePortaDelegata> mappingFruizionePortaDelegataList(Connection con, String tipoDB, 
			long idFruizione, 
			IDSoggetto idSoggettoFruitore,
			IDServizio idServizio,
			ISearch ricerca,
			boolean orderByDescrizione) throws CoreException {
		return _mappingFruizionePortaDelegataList(con, tipoDB, 
				idFruizione, 
				idSoggettoFruitore, 
				idServizio,
				ricerca, CostantiDB.SOGGETTI,
				orderByDescrizione); 
	}
	public static List<MappingFruizionePortaDelegata> mappingFruizionePortaDelegataList(Connection con, String tipoDB, 
			long idFruizione, 
			IDSoggetto idSoggettoFruitore, 
			IDServizio idServizio,
			ISearch ricerca,String tabellaSoggetti,
			boolean orderByDescrizione) throws CoreException {
		return _mappingFruizionePortaDelegataList(con, tipoDB, 
				idFruizione, 
				idSoggettoFruitore, 
				idServizio,
				ricerca, tabellaSoggetti,
				orderByDescrizione); 
	}
	
	public static List<MappingFruizionePortaDelegata> mappingFruizionePortaDelegataList(Connection con, String tipoDB, 
			long idFruizione, 
			IDSoggetto idSoggettoFruitore,
			IDServizio idServizio,
			boolean orderByDescrizione) throws CoreException {
		return _mappingFruizionePortaDelegataList(con, tipoDB, 
				idFruizione, 
				idSoggettoFruitore, 
				idServizio,
				null, CostantiDB.SOGGETTI,
				orderByDescrizione); 
	}
	public static List<MappingFruizionePortaDelegata> mappingFruizionePortaDelegataList(Connection con, String tipoDB, 
			long idFruizione, 
			IDSoggetto idSoggettoFruitore, 
			IDServizio idServizio,
			String tabellaSoggetti,
			boolean orderByDescrizione) throws CoreException {
		return _mappingFruizionePortaDelegataList(con, tipoDB, 
				idFruizione, 
				idSoggettoFruitore, 
				idServizio,
				null, tabellaSoggetti,
				orderByDescrizione); 
	}
	
	public static List<MappingFruizionePortaDelegata> mappingFruizionePortaDelegataList(Connection con, String tipoDB, 
			IDSoggetto idSoggettoFruitore,
			IDServizio idServizio,
			ISearch ricerca,
			boolean orderByDescrizione) throws CoreException {
		return _mappingFruizionePortaDelegataList(con, tipoDB, 
				DBUtils.getIdFruizioneServizio(idServizio, idSoggettoFruitore, con, tipoDB), 
				idSoggettoFruitore, 
				idServizio,
				ricerca, CostantiDB.SOGGETTI,
				orderByDescrizione); 
	}
	public static List<MappingFruizionePortaDelegata> mappingFruizionePortaDelegataList(Connection con, String tipoDB, 
			IDSoggetto idSoggettoFruitore, 
			IDServizio idServizio,
			ISearch ricerca,String tabellaSoggetti,
			boolean orderByDescrizione) throws CoreException {
		return _mappingFruizionePortaDelegataList(con, tipoDB, 
				DBUtils.getIdFruizioneServizio(idServizio, idSoggettoFruitore, con, tipoDB), 
				idSoggettoFruitore, 
				idServizio,
				ricerca, tabellaSoggetti,
				orderByDescrizione); 
	}
	
	public static List<MappingFruizionePortaDelegata> mappingFruizionePortaDelegataList(Connection con, String tipoDB, 
			IDSoggetto idSoggettoFruitore,
			IDServizio idServizio,
			boolean orderByDescrizione) throws CoreException {
		return _mappingFruizionePortaDelegataList(con, tipoDB, 
				DBUtils.getIdFruizioneServizio(idServizio, idSoggettoFruitore, con, tipoDB), 
				idSoggettoFruitore, 
				idServizio,
				null, CostantiDB.SOGGETTI,
				orderByDescrizione); 
	}
	public static List<MappingFruizionePortaDelegata> mappingFruizionePortaDelegataList(Connection con, String tipoDB, 
			IDSoggetto idSoggettoFruitore, 
			IDServizio idServizio,
			String tabellaSoggetti,
			boolean orderByDescrizione) throws CoreException {
		return _mappingFruizionePortaDelegataList(con, tipoDB, 
				DBUtils.getIdFruizioneServizio(idServizio, idSoggettoFruitore, con, tipoDB), 
				idSoggettoFruitore, 
				idServizio,
				null, tabellaSoggetti,
				orderByDescrizione); 
	}
	
	private static List<MappingFruizionePortaDelegata> _mappingFruizionePortaDelegataList(Connection con, String tipoDB, 
			long idFruizione,
			IDSoggetto idSoggettoFruitore, 
			IDServizio idServizio,
			ISearch ricerca,String tabellaSoggetti,
			boolean orderByDescrizione) throws CoreException {
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
			if(ricerca != null) {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.MAPPING_FRUIZIONE_PD);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".id_fruizione = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id="+CostantiDB.MAPPING_FRUIZIONE_PD+".id_porta");
				if (!search.equals("")) {
					sqlQueryObject.addWhereCondition(false, 
							sqlQueryObject.getWhereLikeCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".nome", search, true, true));
							//sqlQueryObject.getWhereLikeCondition(CostantiDB.PORTE_DELEGATE +".nome_porta", search, true, true));
				} 
				
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				int index = 1;
				stmt.setLong(index++, idFruizione);
				
				risultato = stmt.executeQuery();
				if (risultato.next())
					ricerca.setNumEntries(idLista,risultato.getInt(1));
				risultato.close();
				stmt.close();
			}
			
			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.MAPPING_FRUIZIONE_PD);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField(CostantiDB.MAPPING_FRUIZIONE_PD+".id_fruizione");
			sqlQueryObject.addSelectField(CostantiDB.MAPPING_FRUIZIONE_PD+".id_porta");
			sqlQueryObject.addSelectField(CostantiDB.MAPPING_FRUIZIONE_PD+".is_default");
			sqlQueryObject.addSelectField(CostantiDB.MAPPING_FRUIZIONE_PD+".nome");
			sqlQueryObject.addSelectField(CostantiDB.MAPPING_FRUIZIONE_PD+".descrizione");
			sqlQueryObject.addSelectField(CostantiDB.MAPPING_FRUIZIONE_PD+".id");
			sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".nome_porta");
			sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".id_fruizione = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id="+CostantiDB.MAPPING_FRUIZIONE_PD+".id_porta");
			
			if (ricerca != null && !search.equals("")) {
				sqlQueryObject.addWhereCondition(false, 
						sqlQueryObject.getWhereLikeCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".nome", search, true, true));
						//sqlQueryObject.getWhereLikeCondition(CostantiDB.PORTE_DELEGATE +".nome_porta", search, true, true));
			} 
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(CostantiDB.MAPPING_FRUIZIONE_PD+".is_default",true);
			if(orderByDescrizione) {
				sqlQueryObject.addOrderBy(CostantiDB.MAPPING_FRUIZIONE_PD+".descrizione");
			}
			else {
				sqlQueryObject.addOrderBy(CostantiDB.MAPPING_FRUIZIONE_PD+".nome");
			}
			sqlQueryObject.setSortType(true);
			if(ricerca != null) {
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
			}
			queryString = sqlQueryObject.createSQLQuery();
			
			stmt = con.prepareStatement(queryString);
			
			int index = 1;
			stmt.setLong(index++, idFruizione);
			
			risultato = stmt.executeQuery();
			
			MappingFruizionePortaDelegata mapping = null;

			while (risultato.next()) {

				mapping = new MappingFruizionePortaDelegata();
				
				Long id = risultato.getLong("id");
//				Long idPorta = risultato.getLong("id_porta");
//				Long idFruizione = risultato.getLong("id_fruizione");
				String nome = risultato.getString("nome");
				String descrizione = risultato.getString("descrizione");
				Integer isDefaultInt = risultato.getInt("is_default");
				boolean isDefault = isDefaultInt > 0 ;
				
				mapping.setNome(nome);
				mapping.setDescrizione(descrizione);
				mapping.setTableId(id);
				mapping.setDefault(isDefault);
				// evitiamo una join utilizzo l'id che mi sono passato come parametro
				mapping.setIdServizio(idServizio);
				mapping.setIdFruitore(idSoggettoFruitore); 
				
				IDPortaDelegata idPortaDelegata = new IDPortaDelegata();
				String nomePorta = risultato.getString("nome_porta");
				idPortaDelegata.setNome(nomePorta);
				
				IdentificativiFruizione identificativiFruizione = new IdentificativiFruizione();
				identificativiFruizione.setSoggettoFruitore(idSoggettoFruitore);
				identificativiFruizione.setIdServizio(idServizio);
				idPortaDelegata.setIdentificativiFruizione(identificativiFruizione);
				
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

	public static void createMappingFruizione(String nome, String descrizione, boolean isDefault, IDServizio idServizio, IDSoggetto idFruitore, IDPortaDelegata idPortaDelegata,
			Connection con, String tipoDB) throws CoreException{
		createMappingFruizione(nome, descrizione, isDefault, idServizio, idFruitore, idPortaDelegata, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static void createMappingFruizione(String nome, String descrizione, boolean isDefault, IDServizio idServizio, IDSoggetto idFruitore, IDPortaDelegata idPortaDelegata,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException{
		long idFruizione = DBUtils.getIdFruizioneServizio(idServizio, idFruitore, con, tipoDB, tabellaSoggetti);
		if(idFruizione<=0){
			throw new CoreException("Fruizione da parte del soggetto ["+idFruitore.toString()+"] del servizio ["+idServizio.toString()+"] non esistente");
		}
		_createMappingFruizione(nome, descrizione, isDefault, idFruizione, idPortaDelegata, con, tipoDB, tabellaSoggetti);
	}
	
	private static void _createMappingFruizione(String nome, String descrizione, boolean isDefault, long idFruizione, IDPortaDelegata idPortaDelegata,
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
			sqlQueryObject.addInsertField("descrizione", "?");
			sqlQueryObject.addInsertField("is_default", "?");
			String queryString = sqlQueryObject.createSQLInsert();
			stmt = con.prepareStatement(queryString);
			int index = 1;
			stmt.setLong(index++, idFruizione);
			stmt.setLong(index++, idPD);
			stmt.setString(index++, nome);
			stmt.setString(index++, descrizione);
			stmt.setInt(index++, isDefaultInt);
			stmt.executeUpdate();
			stmt.close();
			
		}catch(Exception e){
			throw new CoreException("Creazione Mapping Fruizione fallita: "+e.getMessage(),e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}
	
	
	
	public static void updateMappingFruizione(long tableId, String descrizione, Connection con, String tipoDB) throws CoreException{
		PreparedStatement stmt = null;

		try {

			if(tableId<=0){
				throw new Exception("TableId non fornito");
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addUpdateTable(CostantiDB.MAPPING_FRUIZIONE_PD);
			sqlQueryObject.addUpdateField("descrizione", "?");
			sqlQueryObject.addWhereCondition("id=?");
			String queryString = sqlQueryObject.createSQLUpdate();
			stmt = con.prepareStatement(queryString);
			int index = 1;
			stmt.setString(index++, descrizione);
			stmt.setLong(index++, tableId);
			stmt.executeUpdate();
			stmt.close();
			
		}catch(Exception e){
			throw new CoreException("updateMappingFruizione error",e);
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
	public static void deleteMappingFruizione(IDServizio idServizio, IDSoggetto idFruitore, boolean deletePorte,
			Connection con, String tipoDB) throws CoreException{
		deleteMappingFruizione(idServizio, idFruitore, deletePorte, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static void deleteMappingFruizione(IDServizio idServizio, IDSoggetto idFruitore, boolean deletePorte,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException{
		List<IDPortaDelegata> list = null;
		if(deletePorte) {
			list = getIDPorteDelegateAssociate(idServizio, idFruitore, con, tipoDB);
		}
		deleteMappingFruizione(idServizio, idFruitore, null, con, tipoDB, tabellaSoggetti);
		if(list!=null && list.size()>0) {
			for (IDPortaDelegata idPortaDelegata : list) {
				PreparedStatement stmt = null;
				try {
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE);
					sqlQueryObject.addWhereCondition("id=?");
					sqlQueryObject.setANDLogicOperator(true);
					String queryString = sqlQueryObject.createSQLDelete();
					stmt = con.prepareStatement(queryString);
					stmt.setLong(1, DBUtils.getIdPortaDelegata(idPortaDelegata.getNome(), con, tipoDB));
					stmt.executeUpdate();
					stmt.close();
				}catch(Exception e){
					throw new CoreException("deletePAMappingErogazione error",e);
				} finally {

					//Chiudo statement and resultset
					try{
						if(stmt!=null) stmt.close();
					}catch (Exception e) {
						//ignore
					}

				}
			}
		}
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

	public static IDPortaDelegata getIDPortaDelegataAssociataDefault(IDServizio idServizio, IDSoggetto idFruitore,
			Connection con, String tipoDB) throws CoreException {
		return getIDPortaDelegataAssociataDefault(idServizio, idFruitore, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static IDPortaDelegata getIDPortaDelegataAssociataDefault(IDServizio idServizio, IDSoggetto idFruitore,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		long idFruizione = DBUtils.getIdFruizioneServizio(idServizio, idFruitore, con, tipoDB, tabellaSoggetti);
		if(idFruizione<=0){
			throw new CoreException("Fruizione da parte del soggetto ["+idFruitore.toString()+"] del servizio ["+idServizio.toString()+"] non esistente");
		}
		List<IDPortaDelegata> l = _getIDPorteDelegateAssociate(idFruizione, true, null, con, tipoDB, tabellaSoggetti,
				idFruitore, idServizio);
		if(l!=null && l.size()>0) {
			if(l.size()>1) {
				throw new CoreException("Esiste più di un mapping di default per la fruizione da parte del soggetto ["+idFruitore.toString()+"] del servizio ["+idServizio.toString()+"]");
			}
			return l.get(0);
		}
		return null;
	}
	
	public static IDPortaDelegata getIDPortaDelegataAssociataAzione(IDServizio idServizio, IDSoggetto idFruitore,
			Connection con, String tipoDB) throws CoreException {
		return getIDPortaDelegataAssociataAzione(idServizio, idFruitore, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static IDPortaDelegata getIDPortaDelegataAssociataAzione(IDServizio idServizio, IDSoggetto idFruitore,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		if(idServizio.getAzione()==null) {
			throw new CoreException("Azione non indicata nel parametro 'idServizio'");
		}
		long idFruizione = DBUtils.getIdFruizioneServizio(idServizio, idFruitore, con, tipoDB, tabellaSoggetti);
		if(idFruizione<=0){
			throw new CoreException("Fruizione da parte del soggetto ["+idFruitore.toString()+"] del servizio ["+idServizio.toString()+"] non esistente");
		}
		List<IDPortaDelegata> l = _getIDPorteDelegateAssociate(idFruizione, false, idServizio.getAzione(), con, tipoDB, tabellaSoggetti,
				idFruitore, idServizio);
		if(l!=null && l.size()>0) {
			if(l.size()>1) {
				throw new CoreException("Esiste più di un mapping per l'azione '"+idServizio.getAzione()+"' per la fruizione da parte del soggetto ["+idFruitore.toString()+"] del servizio ["+idServizio.toString()+"]");
			}
			return l.get(0);
		}
		return null;
	}
	
	public static List<IDPortaDelegata> getIDPorteDelegateAssociate(IDServizio idServizio, IDSoggetto idFruitore,
			Connection con, String tipoDB) throws CoreException {
		return getIDPorteDelegateAssociate(idServizio, idFruitore, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static List<IDPortaDelegata> getIDPorteDelegateAssociate(IDServizio idServizio, IDSoggetto idFruitore,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		long idFruizione = DBUtils.getIdFruizioneServizio(idServizio, idFruitore, con, tipoDB, tabellaSoggetti);
		if(idFruizione<=0){
			throw new CoreException("Fruizione da parte del soggetto ["+idFruitore.toString()+"] del servizio ["+idServizio.toString()+"] non esistente");
		}
		return _getIDPorteDelegateAssociate(idFruizione, false, null, con, tipoDB, tabellaSoggetti,
				idFruitore, idServizio);
	}
	
	private static List<IDPortaDelegata> _getIDPorteDelegateAssociate(long idFruizione, boolean defaultMapping, String azioneMapping,
			Connection con, String tipoDB,String tabellaSoggetti,
			IDSoggetto idFruitore, IDServizio idServizio) throws CoreException {
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {

			if(idFruizione<=0){
				throw new Exception("IdFruizione non fornita");
			}

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.MAPPING_FRUIZIONE_PD);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".nome_porta");
			sqlQueryObject.addWhereCondition("id_fruizione = ?");
			if(defaultMapping) {
				sqlQueryObject.addWhereCondition("is_default = ?");
			}
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id="+CostantiDB.MAPPING_FRUIZIONE_PD+".id_porta");
			if(azioneMapping!=null) {
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AZIONI);
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id="+CostantiDB.PORTE_DELEGATE_AZIONI+".id_porta");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_AZIONI+".azione = ?");
			}
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			int indexStmt = 1;
			stm.setLong(indexStmt++,idFruizione);
			if(defaultMapping) {
				stm.setInt(indexStmt++,1);
			}
			if(azioneMapping!=null) {
				stm.setString(indexStmt++,azioneMapping);
			}
		
			rs = stm.executeQuery();
			
			List<IDPortaDelegata> list = new ArrayList<>();
			
			while (rs.next()) {
				IDPortaDelegata idPD = new IDPortaDelegata();
				String nome = rs.getString("nome_porta");
				idPD.setNome(nome);
				
				IdentificativiFruizione identificativiFruizione = new IdentificativiFruizione();
				identificativiFruizione.setSoggettoFruitore(idFruitore);
				identificativiFruizione.setIdServizio(idServizio);
				idPD.setIdentificativiFruizione(identificativiFruizione);
				
				list.add(idPD);
			}
			if(list.size()<=0){
				//throw new CoreException("Mapping tra PD e Fruizione da parte del soggetto ["+idFruitore.toString()+"] del servizio ["+idServizio.toString()+"] non esistente");
				return null;
			}
			else {
				return list;
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
	
	
	
	public static boolean existsIDPortaDelegataAssociataDefault(IDServizio idServizio, IDSoggetto idFruitore,
			Connection con, String tipoDB) throws CoreException {
		return existsIDPortaDelegataAssociataDefault(idServizio, idFruitore, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static boolean existsIDPortaDelegataAssociataDefault(IDServizio idServizio, IDSoggetto idFruitore,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		long idFruizione = DBUtils.getIdFruizioneServizio(idServizio, idFruitore, con, tipoDB, tabellaSoggetti);
		if(idFruizione<=0){
			throw new CoreException("Fruizione da parte del soggetto ["+idFruitore.toString()+"] del servizio ["+idServizio.toString()+"] non esistente");
		}
		int c = _countIDPorteDelegateAssociate(idFruizione, true, null, con, tipoDB, tabellaSoggetti);
		if(c<=0) {
			return false;
		}
		else {
			if(c>1) {
				throw new CoreException("Esiste più di un mapping di default per la fruizione da parte del soggetto ["+idFruitore.toString()+"] del servizio ["+idServizio.toString()+"]");
			}
			return true;
		}
	}
	
	public static boolean existsIDPortaDelegataAssociataAzione(IDServizio idServizio, IDSoggetto idFruitore,
			Connection con, String tipoDB) throws CoreException {
		return existsIDPortaDelegataAssociataAzione(idServizio, idFruitore, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static boolean existsIDPortaDelegataAssociataAzione(IDServizio idServizio, IDSoggetto idFruitore,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		if(idServizio.getAzione()==null) {
			throw new CoreException("Azione non indicata nel parametro 'idServizio'");
		}
		long idFruizione = DBUtils.getIdFruizioneServizio(idServizio, idFruitore, con, tipoDB, tabellaSoggetti);
		if(idFruizione<=0){
			throw new CoreException("Fruizione da parte del soggetto ["+idFruitore.toString()+"] del servizio ["+idServizio.toString()+"] non esistente");
		}
		int c = _countIDPorteDelegateAssociate(idFruizione, false, idServizio.getAzione(), con, tipoDB, tabellaSoggetti);
		if(c<=0) {
			return false;
		}
		else {
			if(c>1) {
				throw new CoreException("Esiste più di un mapping per la fruizione dell'azione '"+idServizio.getAzione()+"' da parte del soggetto ["+idFruitore.toString()+"] del servizio ["+idServizio.toString()+"]");
			}
			return true;
		}
	}

	public static boolean existsIDPorteDelegateAssociate(IDServizio idServizio, IDSoggetto idFruitore,
			Connection con, String tipoDB) throws CoreException {
		return existsIDPorteDelegateAssociate(idServizio, idFruitore, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static boolean existsIDPorteDelegateAssociate(IDServizio idServizio, IDSoggetto idFruitore,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		long idFruizione = DBUtils.getIdFruizioneServizio(idServizio, idFruitore, con, tipoDB, tabellaSoggetti);
		if(idFruizione<=0){
			throw new CoreException("Fruizione da parte del soggetto ["+idFruitore.toString()+"] del servizio ["+idServizio.toString()+"] non esistente");
		}
		return _countIDPorteDelegateAssociate(idFruizione, false, null, con, tipoDB, tabellaSoggetti)>0;
	}
	
	private static int _countIDPorteDelegateAssociate(long idFruizione, boolean defaultMapping, String azioneMapping, 
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
			if(defaultMapping) {
				sqlQueryObject.addWhereCondition("is_default = ?");
			}
			if(azioneMapping!=null) {
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AZIONI);
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id="+CostantiDB.MAPPING_FRUIZIONE_PD+".id_porta");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id="+CostantiDB.PORTE_DELEGATE_AZIONI+".id_porta");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_AZIONI+".azione = ?");
			}
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			int indexStmt = 1;
			stm.setLong(indexStmt++,idFruizione);
			if(defaultMapping) {
				stm.setInt(indexStmt++,1);
			}
			if(azioneMapping!=null) {
				stm.setString(indexStmt++,azioneMapping);
			}
		
			rs = stm.executeQuery();
			int c = 0;
			while(rs.next()) {
				c++;
			}
			return c;
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
			throw new CoreException("_getTableIdMappingFruizione error",e);
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
	
	
	
	public static MappingFruizionePortaDelegata getMappingFruizione(IDServizio idServizio, IDSoggetto idFruitore, IDPortaDelegata idPortaDelegata,
			Connection con, String tipoDB) throws CoreException {
		return getMappingFruizione(idServizio, idFruitore, idPortaDelegata, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static MappingFruizionePortaDelegata getMappingFruizione(IDServizio idServizio, IDSoggetto idFruitore, IDPortaDelegata idPortaDelegata,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		long idFruizione = DBUtils.getIdFruizioneServizio(idServizio, idFruitore, con, tipoDB, tabellaSoggetti);
		if(idFruizione<=0){
			throw new CoreException("Fruizione da parte del soggetto ["+idFruitore.toString()+"] del servizio ["+idServizio.toString()+"] non esistente");
		}
		
		long idPD = DBUtils.getIdPortaDelegata(idPortaDelegata.getNome(), con, tipoDB);
		if(idPD<=0){
			throw new CoreException("PortaDelegata ["+idPortaDelegata+"] non esistente");
		}
		
		MappingFruizionePortaDelegata mapping = _getMappingFruizione(idFruizione, idPD, con, tipoDB, tabellaSoggetti);
		mapping.setIdPortaDelegata(idPortaDelegata);
		mapping.setIdServizio(idServizio);
		mapping.setIdFruitore(idFruitore);
		return mapping;
	}
	private static MappingFruizionePortaDelegata _getMappingFruizione(long idFruizione, long idPD,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException {
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {

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
				MappingFruizionePortaDelegata mapping = new MappingFruizionePortaDelegata();
				int isDefault = rs.getInt("is_default");
				mapping.setDefault(isDefault == 1);
				
				mapping.setTableId(rs.getLong("id"));
				
				mapping.setNome(rs.getString("nome"));			
				mapping.setDescrizione(rs.getString("descrizione"));
				
				return mapping;
			}
			else{
				throw new CoreException("Mapping tra PD (id:"+idPD+") e Fruizione (id:"+idFruizione+") non esistente");
			}
		}catch(CoreException de){
			throw de;
		}
		catch(Exception e){
			throw new CoreException("getMappingFruizione error",e);
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
