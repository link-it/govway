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

package org.openspcoop2.protocol.basic.diagnostica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnostici;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnosticiConPaginazione;
import org.openspcoop2.protocol.sdk.diagnostica.InformazioniProtocollo;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnosticoCorrelazione;
import org.openspcoop2.utils.StringWrapper;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
 * DriverMsgDiagnosticiUtilities
 *
 * @author Stefano Corallo <corallo@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverMsgDiagnosticiUtilities {
	
	public static ISQLQueryObject createSQLQueryObj_searchMessaggiDiagnostici(FiltroRicercaDiagnosticiConPaginazione filter,String tipoDatabase) throws SQLQueryObjectException{
		return DriverMsgDiagnosticiUtilities.createSQLQueryObj(filter, tipoDatabase, TipoRicercaDiagnostici.MSGDIAGNOSTICI);
	}
	public static ISQLQueryObject createSQLQueryObj_countMessaggiDiagnostici(FiltroRicercaDiagnostici filter,String tipoDatabase) throws SQLQueryObjectException{
		return DriverMsgDiagnosticiUtilities.createSQLQueryObj(filter, tipoDatabase, TipoRicercaDiagnostici.COUNT_MSGDIAGNOSTICI);
	}
	public static ISQLQueryObject createSQLQueryObj_deleteMessaggiDiagnostici(FiltroRicercaDiagnostici filter,String tipoDatabase) throws SQLQueryObjectException{
		ISQLQueryObject from = DriverMsgDiagnosticiUtilities.createSQLQueryObj(filter, tipoDatabase, TipoRicercaDiagnostici.DELETE_MSGDIAGNOSTICI);
		ISQLQueryObject sqlQueryObjectDelete = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
		sqlQueryObjectDelete.addDeleteTable(CostantiDB.MSG_DIAGNOSTICI);
		sqlQueryObjectDelete.addWhereINSelectSQLCondition(false, "id", from);
		return sqlQueryObjectDelete;
	}
	
	public static ISQLQueryObject createSQLQueryObj_searchMsgDiagCorrelazione(FiltroRicercaDiagnosticiConPaginazione filter,String tipoDatabase) throws SQLQueryObjectException{
		return DriverMsgDiagnosticiUtilities.createSQLQueryObj(filter, tipoDatabase, TipoRicercaDiagnostici.MSGDIAGNOSTICI_CORRELAZIONE);
	}
	public static ISQLQueryObject createSQLQueryObj_countMsgDiagCorrelazione(FiltroRicercaDiagnostici filter,String tipoDatabase) throws SQLQueryObjectException{
		return DriverMsgDiagnosticiUtilities.createSQLQueryObj(filter, tipoDatabase, TipoRicercaDiagnostici.COUNT_MSGDIAGNOSTICI_CORRELAZIONE);
	}
	public static ISQLQueryObject createSQLQueryObj_deleteMsgDiagCorrelazione(FiltroRicercaDiagnostici filter,String tipoDatabase) throws SQLQueryObjectException{
		ISQLQueryObject from = DriverMsgDiagnosticiUtilities.createSQLQueryObj(filter, tipoDatabase, TipoRicercaDiagnostici.DELETE_MSGDIAGNOSTICI_CORRELAZIONE);
		ISQLQueryObject sqlQueryObjectDelete = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
		sqlQueryObjectDelete.addDeleteTable(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE);
		sqlQueryObjectDelete.addWhereINSelectSQLCondition(false, "id", from);
		return sqlQueryObjectDelete;
	}
	
	private static ISQLQueryObject createSQLQueryObj(FiltroRicercaDiagnostici filter,String tipoDatabase,TipoRicercaDiagnostici tipoRicerca) throws SQLQueryObjectException{
		
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
		
		//select field
		boolean distinct = true;
		switch (tipoRicerca) {
		case MSGDIAGNOSTICI:
			sqlQueryObject.setSelectDistinct(distinct);
			sqlQueryObject.addSelectAliasField(CostantiDB.MSG_DIAGNOSTICI+".id", "idMsgDiagnostico");
			sqlQueryObject.addSelectField(CostantiDB.MSG_DIAGNOSTICI+".gdo");
			break;
		case MSGDIAGNOSTICI_CORRELAZIONE:
			sqlQueryObject.setSelectDistinct(distinct);
			sqlQueryObject.addSelectAliasField(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".id", "idMsgDiagCorrelazione");
			sqlQueryObject.addSelectField(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".gdo");
			break;
		case COUNT_MSGDIAGNOSTICI:
			sqlQueryObject.addSelectCountField(CostantiDB.MSG_DIAGNOSTICI+".id", "countMsgDiagnostici",distinct);
			break;
		case COUNT_MSGDIAGNOSTICI_CORRELAZIONE:
			sqlQueryObject.addSelectCountField(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".id", "countCorrelazioniMsgDiagnostici",distinct);
			break;
		case DELETE_MSGDIAGNOSTICI:
			sqlQueryObject.setSelectDistinct(distinct);
			sqlQueryObject.addSelectAliasField(CostantiDB.MSG_DIAGNOSTICI+".id", "idMsgDiagnostico");
			break;
		case DELETE_MSGDIAGNOSTICI_CORRELAZIONE:
			sqlQueryObject.setSelectDistinct(distinct);
			sqlQueryObject.addSelectAliasField(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".id", "idMsgDiagCorrelazione");
			break;
		}
		
		boolean joinWithCorrelazioneSA = false;
		boolean joinWithCorrelazione = false;
		
		switch (tipoRicerca) {
		case MSGDIAGNOSTICI:
		case COUNT_MSGDIAGNOSTICI:
		case DELETE_MSGDIAGNOSTICI:
			joinWithCorrelazioneSA = DriverMsgDiagnosticiUtilities.isDefined(filter.getServizioApplicativo());
			joinWithCorrelazione = false;
			joinWithCorrelazione = (DriverMsgDiagnosticiUtilities.isDefined(filter.isDelegata())) || 
					(DriverMsgDiagnosticiUtilities.isDefined(filter.getNomePorta())) ||
					(DriverMsgDiagnosticiUtilities.isDefined(filter.getRicercaSoloMessaggiCorrelatiInformazioniProtocollo()) && filter.getRicercaSoloMessaggiCorrelatiInformazioniProtocollo()) ||
					(DriverMsgDiagnosticiUtilities.isDefined(filter.getInformazioniProtocollo())) ||
					joinWithCorrelazioneSA ||
					(DriverMsgDiagnosticiUtilities.isDefined(filter.getCorrelazioneApplicativa())) ||
					(DriverMsgDiagnosticiUtilities.isDefined(filter.getCorrelazioneApplicativaRisposta())) ||
					(DriverMsgDiagnosticiUtilities.isDefined(filter.getFiltroSoggetti()));
			break;
		case MSGDIAGNOSTICI_CORRELAZIONE:
		case COUNT_MSGDIAGNOSTICI_CORRELAZIONE:
		case DELETE_MSGDIAGNOSTICI_CORRELAZIONE:
			joinWithCorrelazioneSA = true;
			joinWithCorrelazione = true;
			break;
		}
		
		
		//from
		sqlQueryObject.addFromTable(CostantiDB.MSG_DIAGNOSTICI);
		if(joinWithCorrelazione){
			sqlQueryObject.addFromTable(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE);
		}
		if(joinWithCorrelazioneSA){
			sqlQueryObject.addFromTable(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE_SA);
		}
		
		sqlQueryObject.setANDLogicOperator(true);
		
		if(joinWithCorrelazione){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".idmessaggio="+CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".idmessaggio");
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".pdd_codice="+CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".pdd_codice");
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".pdd_tipo_soggetto="+CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".pdd_tipo_soggetto");
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".pdd_nome_soggetto="+CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".pdd_nome_soggetto");
		}
		if(joinWithCorrelazioneSA){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".id="+CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE_SA+".id_correlazione");
		}
		
		//where
		
		
		//data inizio
		if(DriverMsgDiagnosticiUtilities.isDefined(filter.getDataInizio())){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".gdo>=?");
		}
		//data fine
		if(DriverMsgDiagnosticiUtilities.isDefined(filter.getDataFine())){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".gdo<=?");
		}
		
		if(DriverMsgDiagnosticiUtilities.isDefined(filter.isDelegata())){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".delegata=?");
		}
		if(DriverMsgDiagnosticiUtilities.isDefined(filter.getNomePorta())){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".porta=?");
		}
		if(DriverMsgDiagnosticiUtilities.isDefined(filter.getIdFunzione())){
			String idF  = filter.getIdFunzione();
			if("RicezioneContenutiApplicativi".equals(idF)
					||"Imbustamento".equals(idF)
					||"RicezioneBuste".equals(idF)
					||"Sbustamento".equals(idF)
			){
				sqlQueryObject.addWhereLikeCondition(CostantiDB.MSG_DIAGNOSTICI+".idfunzione",idF,true,true);
			}
			else if("InoltroBuste".equals(idF)){
				sqlQueryObject.addWhereCondition(false, 
						sqlQueryObject.getWhereLikeCondition(CostantiDB.MSG_DIAGNOSTICI+".idfunzione", idF),
						sqlQueryObject.getWhereLikeCondition(CostantiDB.MSG_DIAGNOSTICI+".idfunzione", "InoltroRisposte"));
			}
			else if("ConsegnaContenutiApplicativi".equals(idF)){
				sqlQueryObject.addWhereLikeCondition(CostantiDB.MSG_DIAGNOSTICI+".idfunzione", idF);
			}
			else{
				sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".idfunzione=?");
			}
		}
		if(DriverMsgDiagnosticiUtilities.isDefined(filter.getDominio())){
			if(DriverMsgDiagnosticiUtilities.isDefined(filter.getDominio().getCodicePorta())){
				sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".pdd_codice=?");
			}
			if(DriverMsgDiagnosticiUtilities.isDefined(filter.getDominio().getTipo())){
				sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".pdd_tipo_soggetto=?");
			}
			if(DriverMsgDiagnosticiUtilities.isDefined(filter.getDominio().getNome())){
				sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".pdd_nome_soggetto=?");
			}
		}
		
		if(DriverMsgDiagnosticiUtilities.isDefined(filter.getIdBustaRichiesta())){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".idmessaggio=?");
		}
		if(DriverMsgDiagnosticiUtilities.isDefined(filter.getIdBustaRisposta())){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".idmessaggio_risposta=?");
		}
		
		
		if(DriverMsgDiagnosticiUtilities.isDefined(filter.getInformazioniProtocollo())){
			if(DriverMsgDiagnosticiUtilities.isDefined(filter.getInformazioniProtocollo().getFruitore())){
				if(DriverMsgDiagnosticiUtilities.isDefined(filter.getInformazioniProtocollo().getFruitore().getTipo())){
					sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".tipo_fruitore=?");
				}
				if(DriverMsgDiagnosticiUtilities.isDefined(filter.getInformazioniProtocollo().getFruitore().getNome())){
					sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".fruitore=?");
				}
			}
			if(DriverMsgDiagnosticiUtilities.isDefined(filter.getInformazioniProtocollo().getErogatore())){
				if(DriverMsgDiagnosticiUtilities.isDefined(filter.getInformazioniProtocollo().getErogatore().getTipo())){
					sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".tipo_erogatore=?");
				}
				if(DriverMsgDiagnosticiUtilities.isDefined(filter.getInformazioniProtocollo().getErogatore().getNome())){
					sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".erogatore=?");
				}
			}
			if(DriverMsgDiagnosticiUtilities.isDefined(filter.getInformazioniProtocollo().getTipoServizio())){
				sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".tipo_servizio=?");
			}
			if(DriverMsgDiagnosticiUtilities.isDefined(filter.getInformazioniProtocollo().getServizio())){
				sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".servizio=?");
			}
			if(DriverMsgDiagnosticiUtilities.isDefined(filter.getInformazioniProtocollo().getVersioneServizio())){
				sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".versione_servizio=?");
			}
			if(DriverMsgDiagnosticiUtilities.isDefined(filter.getInformazioniProtocollo().getAzione())){
				sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".azione=?");
			}
		}
		
		if(DriverMsgDiagnosticiUtilities.isDefined(filter.getServizioApplicativo())){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE_SA+".servizio_applicativo=?");
		}
		
		if(DriverMsgDiagnosticiUtilities.isDefined(filter.getCorrelazioneApplicativa()) && DriverMsgDiagnosticiUtilities.isDefined(filter.getCorrelazioneApplicativaRisposta())){
			sqlQueryObject.addWhereCondition((!filter.isCorrelazioneApplicativaOrMatch()),
						CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".id_correlazione_applicativa=?",
						CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".id_correlazione_risposta=?");
		}
		else if(DriverMsgDiagnosticiUtilities.isDefined(filter.getCorrelazioneApplicativa())){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".id_correlazione_applicativa=?");
		}
		else if(DriverMsgDiagnosticiUtilities.isDefined(filter.getCorrelazioneApplicativaRisposta())){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".id_correlazione_risposta=?");
		}
		
		if(DriverMsgDiagnosticiUtilities.isDefined(filter.getSeverita())){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".severita<=?");
		}
		
		if(DriverMsgDiagnosticiUtilities.isDefined(filter.getCodice())){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".codice=?");
		}
		
		if( DriverMsgDiagnosticiUtilities.isDefined(filter.getMessaggioCercatoInternamenteTestoDiagnostico()) )
			sqlQueryObject.addWhereLikeCondition(CostantiDB.MSG_DIAGNOSTICI+".messaggio", filter.getMessaggioCercatoInternamenteTestoDiagnostico(),true,true);
		
		if(DriverMsgDiagnosticiUtilities.isDefined(filter.getProtocollo())){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".protocollo=?");
		}
		
		if(filter.getProperties()!=null){
			Enumeration<String> keys =filter.getProperties().keys();
			while(keys.hasMoreElements()){
				String key = keys.nextElement();
				switch (tipoRicerca) {
				case MSGDIAGNOSTICI:
				case COUNT_MSGDIAGNOSTICI:
				case DELETE_MSGDIAGNOSTICI:
					if(DriverMsgDiagnostici.IDDIAGNOSTICI.equals(key)){
						// Caso particolare dell'id long della traccia
						sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".id=?");
					}else{
						sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+"."+key+"=?");
					}
					break;
				case MSGDIAGNOSTICI_CORRELAZIONE:
				case COUNT_MSGDIAGNOSTICI_CORRELAZIONE:
				case DELETE_MSGDIAGNOSTICI_CORRELAZIONE:
					if(DriverMsgDiagnostici.IDDIAGNOSTICI.equals(key)){
						// Caso particolare dell'id long della traccia
						sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".id=?");
					}else{
						sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+"."+key+"=?");
					}
					break;
				}
			}
		}
		
		if(filter.getFiltroSoggetti()!=null && filter.sizeFiltroSoggetti()>0){
			List<IDSoggetto> filtroSoggetti = filter.getFiltroSoggetti();
			StringBuffer query = new StringBuffer();
			for(int k=0; k<filtroSoggetti.size(); k++){
				if(k>0)
					query.append(" OR ");
				query.append("( ");
				query.append("("+CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".tipo_fruitore = ? AND "+CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".fruitore = ?)");
				query.append(" OR ");
				query.append("("+CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".tipo_erogatore = ? AND "+CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".erogatore = ?)");
				query.append(" )");
			}
			sqlQueryObject.addWhereCondition(query.toString());
		}
		
		
		switch (tipoRicerca) {
		case MSGDIAGNOSTICI:
		case MSGDIAGNOSTICI_CORRELAZIONE:
			
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
		case COUNT_MSGDIAGNOSTICI_CORRELAZIONE:
		case DELETE_MSGDIAGNOSTICI:
		case DELETE_MSGDIAGNOSTICI_CORRELAZIONE:
			// Niente da effettuare
			break;
		}
				
		return sqlQueryObject;

	}
	
	private static final String format = "yyyy-MM-dd_HH:mm:ss.SSS";
	
	public static int setValuesSearchMessaggiDiagnostici(FiltroRicercaDiagnostici filter,Object object,int startIndex) throws SQLQueryObjectException, SQLException{
		return DriverMsgDiagnosticiUtilities.setValuesSearch(filter, object,true,startIndex);
	}
	public static int setValuesSearchMsgDiagCorrelazione(FiltroRicercaDiagnostici filter,Object object,int startIndex) throws SQLQueryObjectException, SQLException{
		return DriverMsgDiagnosticiUtilities.setValuesSearch(filter, object, false,startIndex);
	}
	private static int setValuesSearch(FiltroRicercaDiagnostici filter,Object object,boolean diagnostici,int startIndex) throws SQLQueryObjectException, SQLException{
		
		
		SimpleDateFormat dateformat = new SimpleDateFormat (format); // SimpleDateFormat non e' thread-safe
		
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
		if(DriverMsgDiagnosticiUtilities.isDefined(filter.getDataInizio())){
			if(pstmt!=null)
				pstmt.setTimestamp(startIndex++, new Timestamp(filter.getDataInizio().getTime()));
			if(query!=null)
				query.replaceFirst("\\?","'"+dateformat.format(filter.getDataInizio())+"'");
		}
		//data fine
		if(DriverMsgDiagnosticiUtilities.isDefined(filter.getDataFine())){
			if(pstmt!=null)
				pstmt.setTimestamp(startIndex++, new Timestamp(filter.getDataFine().getTime()));
			if(query!=null)
				query.replaceFirst("\\?","'"+dateformat.format(filter.getDataFine())+"'");
		}
		
		if(DriverMsgDiagnosticiUtilities.isDefined(filter.isDelegata())){
			if(filter.isDelegata()){
				if(pstmt!=null)
					pstmt.setInt(startIndex++,1);
				if(query!=null)
					query.replaceFirst("\\?","1");
			}else{
				if(pstmt!=null)
					pstmt.setInt(startIndex++,0);
				if(query!=null)
					query.replaceFirst("\\?","0");
			}
		}
		if(DriverMsgDiagnosticiUtilities.isDefined(filter.getNomePorta())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filter.getNomePorta());
			if(query!=null)
				query.replaceFirst("\\?","'"+filter.getNomePorta()+"'");
		}
		if(DriverMsgDiagnosticiUtilities.isDefined(filter.getIdFunzione())){
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
		if(DriverMsgDiagnosticiUtilities.isDefined(filter.getDominio())){
			if(DriverMsgDiagnosticiUtilities.isDefined(filter.getDominio().getCodicePorta())){
				if(pstmt!=null)
					pstmt.setString(startIndex++, filter.getDominio().getCodicePorta());
				if(query!=null)
					query.replaceFirst("\\?","'"+filter.getDominio().getCodicePorta()+"'");
			}
			if(DriverMsgDiagnosticiUtilities.isDefined(filter.getDominio().getTipo())){
				if(pstmt!=null)
					pstmt.setString(startIndex++, filter.getDominio().getTipo());
				if(query!=null)
					query.replaceFirst("\\?","'"+filter.getDominio().getTipo()+"'");
			}
			if(DriverMsgDiagnosticiUtilities.isDefined(filter.getDominio().getNome())){
				if(pstmt!=null)
					pstmt.setString(startIndex++, filter.getDominio().getNome());
				if(query!=null)
					query.replaceFirst("\\?","'"+filter.getDominio().getNome()+"'");
			}
		}
		
		if(DriverMsgDiagnosticiUtilities.isDefined(filter.getIdBustaRichiesta())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filter.getIdBustaRichiesta());
			if(query!=null)
				query.replaceFirst("\\?","'"+filter.getIdBustaRichiesta()+"'");
		}
		if(DriverMsgDiagnosticiUtilities.isDefined(filter.getIdBustaRisposta())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filter.getIdBustaRisposta());
			if(query!=null)
				query.replaceFirst("\\?","'"+filter.getIdBustaRisposta()+"'");
		}
		
		
		if(DriverMsgDiagnosticiUtilities.isDefined(filter.getInformazioniProtocollo())){
			if(DriverMsgDiagnosticiUtilities.isDefined(filter.getInformazioniProtocollo().getFruitore())){
				if(DriverMsgDiagnosticiUtilities.isDefined(filter.getInformazioniProtocollo().getFruitore().getTipo())){
					if(pstmt!=null)
						pstmt.setString(startIndex++, filter.getInformazioniProtocollo().getFruitore().getTipo());
					if(query!=null)
						query.replaceFirst("\\?","'"+filter.getInformazioniProtocollo().getFruitore().getTipo()+"'");
				}
				if(DriverMsgDiagnosticiUtilities.isDefined(filter.getInformazioniProtocollo().getFruitore().getNome())){
					if(pstmt!=null)
						pstmt.setString(startIndex++, filter.getInformazioniProtocollo().getFruitore().getNome());
					if(query!=null)
						query.replaceFirst("\\?","'"+filter.getInformazioniProtocollo().getFruitore().getNome()+"'");
				}
			}
			if(DriverMsgDiagnosticiUtilities.isDefined(filter.getInformazioniProtocollo().getErogatore())){
				if(DriverMsgDiagnosticiUtilities.isDefined(filter.getInformazioniProtocollo().getErogatore().getTipo())){
					if(pstmt!=null)
						pstmt.setString(startIndex++, filter.getInformazioniProtocollo().getErogatore().getTipo());
					if(query!=null)
						query.replaceFirst("\\?","'"+filter.getInformazioniProtocollo().getErogatore().getTipo()+"'");
				}
				if(DriverMsgDiagnosticiUtilities.isDefined(filter.getInformazioniProtocollo().getErogatore().getNome())){
					if(pstmt!=null)
						pstmt.setString(startIndex++, filter.getInformazioniProtocollo().getErogatore().getNome());
					if(query!=null)
						query.replaceFirst("\\?","'"+filter.getInformazioniProtocollo().getErogatore().getNome()+"'");
				}
			}
			if(DriverMsgDiagnosticiUtilities.isDefined(filter.getInformazioniProtocollo().getTipoServizio())){
				if(pstmt!=null)
					pstmt.setString(startIndex++, filter.getInformazioniProtocollo().getTipoServizio());
				if(query!=null)
					query.replaceFirst("\\?","'"+filter.getInformazioniProtocollo().getTipoServizio()+"'");
			}
			if(DriverMsgDiagnosticiUtilities.isDefined(filter.getInformazioniProtocollo().getServizio())){
				if(pstmt!=null)
					pstmt.setString(startIndex++, filter.getInformazioniProtocollo().getServizio());
				if(query!=null)
					query.replaceFirst("\\?","'"+filter.getInformazioniProtocollo().getServizio()+"'");
			}
			if(DriverMsgDiagnosticiUtilities.isDefined(filter.getInformazioniProtocollo().getVersioneServizio())){
				if(pstmt!=null)
					pstmt.setInt(startIndex++, filter.getInformazioniProtocollo().getVersioneServizio());
				if(query!=null)
					query.replaceFirst("\\?",filter.getInformazioniProtocollo().getVersioneServizio()+"");
			}
			if(DriverMsgDiagnosticiUtilities.isDefined(filter.getInformazioniProtocollo().getAzione())){
				if(pstmt!=null)
					pstmt.setString(startIndex++, filter.getInformazioniProtocollo().getAzione());
				if(query!=null)
					query.replaceFirst("\\?","'"+filter.getInformazioniProtocollo().getAzione()+"'");
			}
		}
		
		if(DriverMsgDiagnosticiUtilities.isDefined(filter.getServizioApplicativo())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filter.getServizioApplicativo());
			if(query!=null)
				query.replaceFirst("\\?","'"+filter.getServizioApplicativo()+"'");
		}
		
		if(DriverMsgDiagnosticiUtilities.isDefined(filter.getCorrelazioneApplicativa()) && DriverMsgDiagnosticiUtilities.isDefined(filter.getCorrelazioneApplicativaRisposta())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filter.getCorrelazioneApplicativa());
			if(query!=null)
				query.replaceFirst("\\?","'"+filter.getCorrelazioneApplicativa()+"'");
			if(pstmt!=null)
				pstmt.setString(startIndex++, filter.getCorrelazioneApplicativaRisposta());
			if(query!=null)
				query.replaceFirst("\\?","'"+filter.getCorrelazioneApplicativaRisposta()+"'");
		}
		else if(DriverMsgDiagnosticiUtilities.isDefined(filter.getCorrelazioneApplicativa())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filter.getCorrelazioneApplicativa());
			if(query!=null)
				query.replaceFirst("\\?","'"+filter.getCorrelazioneApplicativa()+"'");
		}
		else if(DriverMsgDiagnosticiUtilities.isDefined(filter.getCorrelazioneApplicativaRisposta())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filter.getCorrelazioneApplicativaRisposta());
			if(query!=null)
				query.replaceFirst("\\?","'"+filter.getCorrelazioneApplicativaRisposta()+"'");
		}
		
		if(DriverMsgDiagnosticiUtilities.isDefined(filter.getSeverita())){
			if(pstmt!=null)
				pstmt.setInt(startIndex++, filter.getSeverita());
			if(query!=null)
				query.replaceFirst("\\?",filter.getSeverita()+"");
		}
		
		if(DriverMsgDiagnosticiUtilities.isDefined(filter.getCodice())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filter.getCodice());
			if(query!=null)
				query.replaceFirst("\\?","'"+filter.getCodice()+"'");
		}
		
		if(DriverMsgDiagnosticiUtilities.isDefined(filter.getProtocollo())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filter.getProtocollo());
			if(query!=null)
				query.replaceFirst("\\?","'"+filter.getProtocollo()+"'");
		}
		
		if(filter.getProperties()!=null){
			Enumeration<String> keys =filter.getProperties().keys();
			while(keys.hasMoreElements()){
				String key = keys.nextElement();
				String value = filter.getProperties().get(key);
				if(DriverMsgDiagnostici.IDDIAGNOSTICI.equals(key)){
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
		
		if(filter.getFiltroSoggetti()!=null && filter.sizeFiltroSoggetti()>0){
			List<IDSoggetto> filtroSoggetti = filter.getFiltroSoggetti();
			for(int k=0; k<filtroSoggetti.size(); k++){
				IDSoggetto id = filtroSoggetti.get(k);
				
				if(pstmt!=null)
					pstmt.setString(startIndex++, id.getTipo());
				if(pstmt!=null)
					pstmt.setString(startIndex++, id.getNome());
				if(pstmt!=null)
					pstmt.setString(startIndex++, id.getTipo());
				if(pstmt!=null)
					pstmt.setString(startIndex++, id.getNome());
				
				if(query!=null)
					query.replaceFirst("\\?","'"+id.getTipo()+"'");
				if(query!=null)
					query.replaceFirst("\\?","'"+id.getNome()+"'");
				if(query!=null)
					query.replaceFirst("\\?","'"+id.getTipo()+"'");
				if(query!=null)
					query.replaceFirst("\\?","'"+id.getNome()+"'");
			}
		}

		
		return startIndex;
	}
	
	
	public static MsgDiagnostico getMsgDiagnostico(Connection c,String tipoDatabase, 
			Logger log,long id,Vector<String> properties) throws Exception{
		
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
		sqlQueryObject.addFromTable(CostantiDB.MSG_DIAGNOSTICI);
		sqlQueryObject.addWhereCondition("id=?");
		
		log.debug("Eseguo query : "+sqlQueryObject.createSQLQuery().replaceFirst("\\?", id+""));
		PreparedStatement stmt=null;
		ResultSet rs= null;
		try{
			stmt=c.prepareStatement(sqlQueryObject.createSQLQuery());
			stmt.setLong(1, id);
			rs=stmt.executeQuery();
			if(rs.next()){
				
				MsgDiagnostico msg = new MsgDiagnostico();
				
				msg.setId(rs.getLong("id"));
				msg.addProperty(DriverMsgDiagnostici.IDDIAGNOSTICI, msg.getId()+"");
				
				Timestamp gdo=rs.getTimestamp("gdo");
				msg.setGdo(gdo);
				
				IDSoggetto idSoggetto = new IDSoggetto();
				idSoggetto.setCodicePorta(rs.getString("pdd_codice"));
				idSoggetto.setNome(rs.getString("pdd_nome_soggetto"));
				idSoggetto.setTipo(rs.getString("pdd_tipo_soggetto"));
				msg.setIdSoggetto(idSoggetto);
				
				msg.setIdFunzione(rs.getString("idfunzione"));

				msg.setSeverita(rs.getInt("severita"));
				
				msg.setMessaggio(rs.getString("messaggio"));
				
				msg.setIdBusta(rs.getString("idmessaggio"));
				
				msg.setIdBustaRisposta(rs.getString("idmessaggio_risposta"));
				
				msg.setCodice(rs.getString("codice"));
				
				msg.setProtocollo(rs.getString("protocollo"));
				
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
	
	
	
	public static MsgDiagnosticoCorrelazione getMsgDiagnosticoCorrelazione(Connection c,String tipoDatabase, 
			Logger log,long id,Vector<String> properties) throws Exception{
		
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
		sqlQueryObject.addFromTable(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE);
		sqlQueryObject.addWhereCondition("id=?");
		
		MsgDiagnosticoCorrelazione msg = null;
		
		log.debug("Eseguo query : "+sqlQueryObject.createSQLQuery().replaceFirst("\\?", id+""));
		PreparedStatement stmt=null;
		ResultSet rs= null;
		try{
			stmt=c.prepareStatement(sqlQueryObject.createSQLQuery());
			stmt.setLong(1, id);
			rs=stmt.executeQuery();
			if(rs.next()){
				
				msg = new MsgDiagnosticoCorrelazione();
				
				msg.setId(rs.getLong("id"));
				msg.addProperty(DriverMsgDiagnostici.IDDIAGNOSTICI, msg.getId()+"");
				
				msg.setIdBusta(rs.getString("idmessaggio"));
				
				IDSoggetto idSoggetto = new IDSoggetto();
				idSoggetto.setCodicePorta(rs.getString("pdd_codice"));
				idSoggetto.setNome(rs.getString("pdd_nome_soggetto"));
				idSoggetto.setTipo(rs.getString("pdd_tipo_soggetto"));
				msg.setIdSoggetto(idSoggetto);
				
				Timestamp gdo=rs.getTimestamp("gdo");
				msg.setGdo(gdo);
				
				msg.setNomePorta(rs.getString("porta"));
				
				int isDelegata = rs.getInt("delegata");
				msg.setDelegata(isDelegata==1);
				
				InformazioniProtocollo info = new InformazioniProtocollo();
				info.setFruitore(new IDSoggetto(rs.getString("tipo_fruitore"), rs.getString("fruitore")));
				info.setErogatore(new IDSoggetto(rs.getString("tipo_erogatore"), rs.getString("erogatore")));
				info.setTipoServizio(rs.getString("tipo_servizio"));
				info.setServizio(rs.getString("servizio"));
				info.setVersioneServizio(rs.getInt("versione_servizio"));
				info.setAzione(rs.getString("azione"));
				msg.setInformazioniProtocollo(info);
				
				msg.setCorrelazioneApplicativa(rs.getString("id_correlazione_applicativa"));
				msg.setCorrelazioneApplicativaRisposta(rs.getString("id_correlazione_risposta"));
				
				msg.setProtocollo(rs.getString("protocollo"));
								
				if(properties!=null){
					for (int i = 0; i < properties.size(); i++) {
						String key = properties.get(i);
						msg.addProperty(key, rs.getString(key));
					}
				}
				
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
		
		
		sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
		sqlQueryObject.addSelectField("servizio_applicativo");
		sqlQueryObject.addFromTable(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE);
		sqlQueryObject.addFromTable(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE_SA);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".id=?");
		sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".id="+CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE_SA+".id_correlazione");
		
		log.debug("Eseguo query : "+sqlQueryObject.createSQLQuery().replaceFirst("\\?", id+""));
		stmt=null;
		rs= null;
		try{
			stmt=c.prepareStatement(sqlQueryObject.createSQLQuery());
			stmt.setLong(1, id);
			rs=stmt.executeQuery();
			List<String> list = new ArrayList<String>();
			while(rs.next()){
				list.add(rs.getString("servizio_applicativo"));
			}
			msg.setServiziApplicativiList(list);
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
	
		return msg;
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
	protected static boolean isDefined(InformazioniProtocollo v){
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
