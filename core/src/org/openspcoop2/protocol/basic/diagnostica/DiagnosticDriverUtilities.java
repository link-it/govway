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

package org.openspcoop2.protocol.basic.diagnostica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnostici;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnosticiConPaginazione;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.utils.StringWrapper;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;
import org.slf4j.Logger;

/**
 * DriverMsgDiagnosticiUtilities
 *
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DiagnosticDriverUtilities {
	
	public static ISQLQueryObject createSQLQueryObj_searchMessaggiDiagnostici(FiltroRicercaDiagnosticiConPaginazione filter,String tipoDatabase) throws SQLQueryObjectException{
		return DiagnosticDriverUtilities.createSQLQueryObj(filter, tipoDatabase, DiagnosticSearchType.MSGDIAGNOSTICI);
	}
	public static ISQLQueryObject createSQLQueryObj_countMessaggiDiagnostici(FiltroRicercaDiagnostici filter,String tipoDatabase) throws SQLQueryObjectException{
		return DiagnosticDriverUtilities.createSQLQueryObj(filter, tipoDatabase, DiagnosticSearchType.COUNT_MSGDIAGNOSTICI);
	}
	public static ISQLQueryObject createSQLQueryObj_deleteMessaggiDiagnostici(FiltroRicercaDiagnostici filter,String tipoDatabase) throws SQLQueryObjectException{
		ISQLQueryObject from = DiagnosticDriverUtilities.createSQLQueryObj(filter, tipoDatabase, DiagnosticSearchType.DELETE_MSGDIAGNOSTICI);
		ISQLQueryObject sqlQueryObjectDelete = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
		sqlQueryObjectDelete.addDeleteTable(CostantiDB.MSG_DIAGNOSTICI);
		sqlQueryObjectDelete.addWhereINSelectSQLCondition(false, "id", from);
		return sqlQueryObjectDelete;
	}
	
	private static ISQLQueryObject createSQLQueryObj(FiltroRicercaDiagnostici filter,String tipoDatabase,DiagnosticSearchType tipoRicerca) throws SQLQueryObjectException{
		
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
		
		//select field
		boolean distinct = true;
		switch (tipoRicerca) {
		case MSGDIAGNOSTICI:
			sqlQueryObject.setSelectDistinct(distinct);
			sqlQueryObject.addSelectAliasField(CostantiDB.MSG_DIAGNOSTICI+"."+CostantiDB.MSG_DIAGNOSTICI_COLUMN_ID, "idMsgDiagnostico");
			sqlQueryObject.addSelectField(CostantiDB.MSG_DIAGNOSTICI+"."+CostantiDB.MSG_DIAGNOSTICI_COLUMN_GDO);
			break;
		case COUNT_MSGDIAGNOSTICI:
			sqlQueryObject.addSelectCountField(CostantiDB.MSG_DIAGNOSTICI+"."+CostantiDB.MSG_DIAGNOSTICI_COLUMN_ID, "countMsgDiagnostici",distinct);
			break;
		case DELETE_MSGDIAGNOSTICI:
			sqlQueryObject.setSelectDistinct(distinct);
			sqlQueryObject.addSelectAliasField(CostantiDB.MSG_DIAGNOSTICI+"."+CostantiDB.MSG_DIAGNOSTICI_COLUMN_ID, "idMsgDiagnostico");
			break;
		}
		
		
		//from
		sqlQueryObject.addFromTable(CostantiDB.MSG_DIAGNOSTICI);
		
		sqlQueryObject.setANDLogicOperator(true);
		
		
		//where
		
		
		//data inizio
		if(DiagnosticDriverUtilities.isDefined(filter.getDataInizio())){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+"."+CostantiDB.MSG_DIAGNOSTICI_COLUMN_GDO+">=?");
		}
		//data fine
		if(DiagnosticDriverUtilities.isDefined(filter.getDataFine())){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+"."+CostantiDB.MSG_DIAGNOSTICI_COLUMN_GDO+"<=?");
		}
		
		if(DiagnosticDriverUtilities.isDefined(filter.getIdTransazione())){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+"."+CostantiDB.MSG_DIAGNOSTICI_COLUMN_ID_TRANSAZIONE+"=?");
		}
		
		if(DiagnosticDriverUtilities.isDefined(filter.getIdFunzione())){
			String idF  = filter.getIdFunzione();
			if("RicezioneContenutiApplicativi".equals(idF)
					||"Imbustamento".equals(idF)
					||"RicezioneBuste".equals(idF)
					||"Sbustamento".equals(idF)
			){
				sqlQueryObject.addWhereLikeCondition(CostantiDB.MSG_DIAGNOSTICI+"."+CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDFUNZIONE,idF,true,true);
			}
			else if("InoltroBuste".equals(idF)){
				sqlQueryObject.addWhereCondition(false, 
						sqlQueryObject.getWhereLikeCondition(CostantiDB.MSG_DIAGNOSTICI+"."+CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDFUNZIONE, idF),
						sqlQueryObject.getWhereLikeCondition(CostantiDB.MSG_DIAGNOSTICI+"."+CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDFUNZIONE, "InoltroRisposte"));
			}
			else if("ConsegnaContenutiApplicativi".equals(idF)){
				sqlQueryObject.addWhereLikeCondition(CostantiDB.MSG_DIAGNOSTICI+"."+CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDFUNZIONE, idF);
			}
			else{
				sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+"."+CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDFUNZIONE+"=?");
			}
		}
		if(DiagnosticDriverUtilities.isDefined(filter.getDominio())){
			if(DiagnosticDriverUtilities.isDefined(filter.getDominio().getCodicePorta())){
				sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+"."+CostantiDB.MSG_DIAGNOSTICI_COLUMN_PDD_CODICE+"=?");
			}
			if(DiagnosticDriverUtilities.isDefined(filter.getDominio().getTipo())){
				sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+"."+CostantiDB.MSG_DIAGNOSTICI_COLUMN_PDD_TIPO_SOGGETTO+"=?");
			}
			if(DiagnosticDriverUtilities.isDefined(filter.getDominio().getNome())){
				sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+"."+CostantiDB.MSG_DIAGNOSTICI_COLUMN_PDD_NOME_SOGGETTO+"=?");
			}
		}
		
		if(DiagnosticDriverUtilities.isDefined(filter.getIdBustaRichiesta())){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+"."+CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO+"=?");
		}
		if(DiagnosticDriverUtilities.isDefined(filter.getIdBustaRisposta())){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+"."+CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO_RISPOSTA+"=?");
		}
		
		if(DiagnosticDriverUtilities.isDefined(filter.getSeverita())){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+"."+CostantiDB.MSG_DIAGNOSTICI_COLUMN_SEVERITA+"<=?");
		}
		
		if(DiagnosticDriverUtilities.isDefined(filter.getCodice())){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+"."+CostantiDB.MSG_DIAGNOSTICI_COLUMN_CODICE+"=?");
		}
		
		if( DiagnosticDriverUtilities.isDefined(filter.getMessaggioCercatoInternamenteTestoDiagnostico()) )
			sqlQueryObject.addWhereLikeCondition(CostantiDB.MSG_DIAGNOSTICI+"."+CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO, filter.getMessaggioCercatoInternamenteTestoDiagnostico(),true,true);
		
		if(DiagnosticDriverUtilities.isDefined(filter.getProtocollo())){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+"."+CostantiDB.MSG_DIAGNOSTICI_COLUMN_PROTOCOLLO+"=?");
		}
		
		if(DiagnosticDriverUtilities.isDefined(filter.getApplicativo())){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+"."+CostantiDB.MSG_DIAGNOSTICI_COLUMN_APPLICATIVO+"=?");
		}
		else if(DiagnosticDriverUtilities.isDefined(filter.getCheckApplicativoIsNull()) && filter.getCheckApplicativoIsNull()){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+"."+CostantiDB.MSG_DIAGNOSTICI_COLUMN_APPLICATIVO+" is null");
		}
		
		if(filter.getProperties()!=null){
			for (String key : filter.getProperties().keySet()) {
				switch (tipoRicerca) {
				case MSGDIAGNOSTICI:
				case COUNT_MSGDIAGNOSTICI:
				case DELETE_MSGDIAGNOSTICI:
					if(DiagnosticDriver.IDDIAGNOSTICI.equals(key)){
						// Caso particolare dell'id long della traccia
						sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".id=?");
					}else{
						sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+"."+key+"=?");
					}
					break;
				}
			}
		}
		
		
		switch (tipoRicerca) {
		case MSGDIAGNOSTICI:
			
			FiltroRicercaDiagnosticiConPaginazione f = (FiltroRicercaDiagnosticiConPaginazione) filter;
			//limit
			if(f.getLimit()>0)
				sqlQueryObject.setLimit(f.getLimit());
			/*else 
				sqlQueryObject.setLimit(1000);*/
			// Offset
			if(f.getOffset()>0)
				sqlQueryObject.setOffset(f.getOffset());
			
			sqlQueryObject.addOrderBy("gdo");
			sqlQueryObject.setSortType(f.isAsc());	
			break;
		case COUNT_MSGDIAGNOSTICI:
		case DELETE_MSGDIAGNOSTICI:
			// Niente da effettuare
			break;
		}
				
		return sqlQueryObject;

	}
	
	public static int setValues_searchMessaggiDiagnostici(FiltroRicercaDiagnostici filter,Object object,int startIndex) throws SQLQueryObjectException, SQLException{
		return DiagnosticDriverUtilities.setValuesSearch(filter, object,startIndex, DiagnosticSearchType.MSGDIAGNOSTICI);
	}
	public static int setValues_countMessaggiDiagnostici(FiltroRicercaDiagnostici filter,Object object,int startIndex) throws SQLQueryObjectException, SQLException{
		return DiagnosticDriverUtilities.setValuesSearch(filter, object,startIndex, DiagnosticSearchType.COUNT_MSGDIAGNOSTICI);
	}
	public static int setValues_deleteMessaggiDiagnostici(FiltroRicercaDiagnostici filter,Object object,int startIndex) throws SQLQueryObjectException, SQLException{
		return DiagnosticDriverUtilities.setValuesSearch(filter, object,startIndex, DiagnosticSearchType.DELETE_MSGDIAGNOSTICI);
	}
		
	private static int setValuesSearch(FiltroRicercaDiagnostici filter,Object object,int startIndex, DiagnosticSearchType tipoRicerca) throws SQLQueryObjectException, SQLException{
		
		
		SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
		
		PreparedStatement pstmt = null;
		StringWrapper query = null;
		if(object instanceof PreparedStatement){
			pstmt = (PreparedStatement) object;
		}
		else if(object instanceof StringWrapper){
			query = (StringWrapper) object;
		}
		else{
			throw new SQLException("Tipo di parametro ["+object.getClass().getName()+"] non gestito");
		}
		
		
		
		//where
		
		
		//data inizio
		if(DiagnosticDriverUtilities.isDefined(filter.getDataInizio())){
			if(pstmt!=null)
				pstmt.setTimestamp(startIndex++, new Timestamp(filter.getDataInizio().getTime()));
			if(query!=null)
				query.replaceFirst("\\?","'"+dateformat.format(filter.getDataInizio())+"'");
		}
		//data fine
		if(DiagnosticDriverUtilities.isDefined(filter.getDataFine())){
			if(pstmt!=null)
				pstmt.setTimestamp(startIndex++, new Timestamp(filter.getDataFine().getTime()));
			if(query!=null)
				query.replaceFirst("\\?","'"+dateformat.format(filter.getDataFine())+"'");
		}
		
		// id transazione
		if(DiagnosticDriverUtilities.isDefined(filter.getIdTransazione())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filter.getIdTransazione());
			if(query!=null)
				query.replaceFirst("\\?","'"+filter.getIdTransazione()+"'");
		}
		
		if(DiagnosticDriverUtilities.isDefined(filter.getIdFunzione())){
			String idF  = filter.getIdFunzione();
			if("RicezioneContenutiApplicativi".equals(idF)
					||"Imbustamento".equals(idF)
					||"RicezioneBuste".equals(idF)
					||"Sbustamento".equals(idF)
			){
				// Like impostato in sqlQueryObject
			}
			else if("InoltroBuste".equals(idF)){
				// Like impostato in sqlQueryObject
			}
			else if("ConsegnaContenutiApplicativi".equals(idF)){
				// Like impostato in sqlQueryObject
			}
			else{
				if(pstmt!=null)
					pstmt.setString(startIndex++, idF);
				if(query!=null)
					query.replaceFirst("\\?","'"+idF+"'");
			}
		}
		if(DiagnosticDriverUtilities.isDefined(filter.getDominio())){
			if(DiagnosticDriverUtilities.isDefined(filter.getDominio().getCodicePorta())){
				if(pstmt!=null)
					pstmt.setString(startIndex++, filter.getDominio().getCodicePorta());
				if(query!=null)
					query.replaceFirst("\\?","'"+filter.getDominio().getCodicePorta()+"'");
			}
			if(DiagnosticDriverUtilities.isDefined(filter.getDominio().getTipo())){
				if(pstmt!=null)
					pstmt.setString(startIndex++, filter.getDominio().getTipo());
				if(query!=null)
					query.replaceFirst("\\?","'"+filter.getDominio().getTipo()+"'");
			}
			if(DiagnosticDriverUtilities.isDefined(filter.getDominio().getNome())){
				if(pstmt!=null)
					pstmt.setString(startIndex++, filter.getDominio().getNome());
				if(query!=null)
					query.replaceFirst("\\?","'"+filter.getDominio().getNome()+"'");
			}
		}
		
		if(DiagnosticDriverUtilities.isDefined(filter.getIdBustaRichiesta())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filter.getIdBustaRichiesta());
			if(query!=null)
				query.replaceFirst("\\?","'"+filter.getIdBustaRichiesta()+"'");
		}
		if(DiagnosticDriverUtilities.isDefined(filter.getIdBustaRisposta())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filter.getIdBustaRisposta());
			if(query!=null)
				query.replaceFirst("\\?","'"+filter.getIdBustaRisposta()+"'");
		}
				
		if(DiagnosticDriverUtilities.isDefined(filter.getSeverita())){
			if(pstmt!=null)
				pstmt.setInt(startIndex++, filter.getSeverita());
			if(query!=null)
				query.replaceFirst("\\?",filter.getSeverita()+"");
		}
		
		if(DiagnosticDriverUtilities.isDefined(filter.getCodice())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filter.getCodice());
			if(query!=null)
				query.replaceFirst("\\?","'"+filter.getCodice()+"'");
		}
		
		if(DiagnosticDriverUtilities.isDefined(filter.getProtocollo())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filter.getProtocollo());
			if(query!=null)
				query.replaceFirst("\\?","'"+filter.getProtocollo()+"'");
		}
		
		if(DiagnosticDriverUtilities.isDefined(filter.getApplicativo())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filter.getApplicativo());
			if(query!=null)
				query.replaceFirst("\\?","'"+filter.getApplicativo()+"'");
		}
		else if(DiagnosticDriverUtilities.isDefined(filter.getCheckApplicativoIsNull()) && filter.getCheckApplicativoIsNull()){
			// nop
		}
		
		if(filter.getProperties()!=null){
			for (String key : filter.getProperties().keySet()) {
				String value = filter.getProperties().get(key);
				if(DiagnosticDriver.IDDIAGNOSTICI.equals(key)){
					// Caso particolare dell'id long della traccia
					if(pstmt!=null)
						pstmt.setLong(startIndex++, Long.parseLong(value));
					if(query!=null)
						query.replaceFirst("\\?",value);
				}else{
					if(pstmt!=null)
						pstmt.setString(startIndex++, value);
					if(query!=null)
						query.replaceFirst("\\?","'"+value+"'");
				}
			}
		}
		
		
		return startIndex;
	}
	
	
	public static MsgDiagnostico getMsgDiagnostico(Connection c,String tipoDatabase, 
			Logger log,long id,List<String> properties) throws Exception{
		
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
		sqlQueryObject.addFromTable(CostantiDB.MSG_DIAGNOSTICI);
		sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_COLUMN_ID+"=?");
		
		log.debug("Eseguo query : "+sqlQueryObject.createSQLQuery().replaceFirst("\\?", id+""));
		PreparedStatement stmt=null;
		ResultSet rs= null;
		try{
			stmt=c.prepareStatement(sqlQueryObject.createSQLQuery());
			stmt.setLong(1, id);
			rs=stmt.executeQuery();
			if(rs.next()){
				
				MsgDiagnostico msg = new MsgDiagnostico();
				
				msg.setId(rs.getLong(CostantiDB.MSG_DIAGNOSTICI_COLUMN_ID));
				msg.addProperty(DiagnosticDriver.IDDIAGNOSTICI, msg.getId()+"");
				
				Timestamp gdo=rs.getTimestamp(CostantiDB.MSG_DIAGNOSTICI_COLUMN_GDO);
				msg.setGdo(gdo);
				
				msg.setIdTransazione(rs.getString(CostantiDB.MSG_DIAGNOSTICI_COLUMN_ID_TRANSAZIONE));
				
				msg.setApplicativo(rs.getString(CostantiDB.MSG_DIAGNOSTICI_COLUMN_APPLICATIVO));
				
				IDSoggetto idSoggetto = new IDSoggetto();
				idSoggetto.setCodicePorta(rs.getString(CostantiDB.MSG_DIAGNOSTICI_COLUMN_PDD_CODICE));
				idSoggetto.setNome(rs.getString(CostantiDB.MSG_DIAGNOSTICI_COLUMN_PDD_NOME_SOGGETTO));
				idSoggetto.setTipo(rs.getString(CostantiDB.MSG_DIAGNOSTICI_COLUMN_PDD_TIPO_SOGGETTO));
				msg.setIdSoggetto(idSoggetto);
				
				msg.setIdFunzione(rs.getString(CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDFUNZIONE));

				msg.setSeverita(rs.getInt(CostantiDB.MSG_DIAGNOSTICI_COLUMN_SEVERITA));
				
				msg.setMessaggio(rs.getString(CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO));
				
				msg.setIdBusta(rs.getString(CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO));
				
				msg.setIdBustaRisposta(rs.getString(CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO_RISPOSTA));
				
				msg.setCodice(rs.getString(CostantiDB.MSG_DIAGNOSTICI_COLUMN_CODICE));
				
				msg.setProtocollo(rs.getString(CostantiDB.MSG_DIAGNOSTICI_COLUMN_PROTOCOLLO));
				
				if(properties!=null){
					for (int i = 0; i < properties.size(); i++) {
						String key = properties.get(i);
						msg.addProperty(key, rs.getString(key));
					}
				}
				
				return msg;
				
			}
			else{
				
				throw new Exception("MsgDiagnostico con id["+id+"] non trovato");
				
			}
		}finally{
			try{
				if(rs!=null){
					rs.close();
				}
				if(stmt!=null){
					stmt.close();
				}
			}catch(Exception eClose){}
		}
	}
	
	
	
	protected static boolean isDefined(String v){
		return v!=null && !"".equals(v);
	}
	protected static boolean isDefined(Boolean v){
		return v!=null;
	}
	protected static boolean isDefined(Integer v){
		return v!=null;
	}
	protected static boolean isDefined(List<?> v){
		return v!=null && v.size()>0;
	}
	protected static boolean isDefined(Date v){
		return v!=null;
	}
	protected static boolean isDefined(IDSoggetto v){
		return v!=null;
	}
}
