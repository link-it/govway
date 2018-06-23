/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.basic.tracciamento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.basic.ProtocolliRegistrati;
import org.openspcoop2.protocol.sdk.Allegato;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Riscontro;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ContestoCodificaEccezione;
import org.openspcoop2.protocol.sdk.constants.EsitoElaborazioneMessaggioTracciatura;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.LivelloRilevanza;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.constants.SubCodiceErrore;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.sdk.diagnostica.DriverMsgDiagnosticiException;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoException;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoNotFoundException;
import org.openspcoop2.protocol.sdk.tracciamento.EsitoElaborazioneMessaggioTracciato;
import org.openspcoop2.protocol.sdk.tracciamento.FiltroRicercaTracce;
import org.openspcoop2.protocol.sdk.tracciamento.FiltroRicercaTracceConPaginazione;
import org.openspcoop2.protocol.sdk.tracciamento.InformazioniProtocollo;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.utils.StringWrapper;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;
import org.slf4j.Logger;

/**
 * TracciaDriverUtilities
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TracciaDriverUtilities {

	public static ISQLQueryObject getSQLQueryObject(String tipoDatabase)throws SQLQueryObjectException{
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
		return sqlQueryObject;
	}
	
	
	public static ISQLQueryObject createSQLQueryObj_searchTracce(FiltroRicercaTracceConPaginazione filter,String tipoDatabase) throws SQLQueryObjectException{
		return TracciaDriverUtilities.createSQLQueryObj(filter, tipoDatabase, TracciaSearchType.TRACCE);
	}
	public static ISQLQueryObject createSQLQueryObj_countTracce(FiltroRicercaTracce filter,String tipoDatabase) throws SQLQueryObjectException{
		return TracciaDriverUtilities.createSQLQueryObj(filter, tipoDatabase, TracciaSearchType.COUNT_TRACCE);
	}
	public static ISQLQueryObject createSQLQueryObj_deleteTracce(FiltroRicercaTracce filter,String tipoDatabase) throws SQLQueryObjectException{
		ISQLQueryObject from = TracciaDriverUtilities.createSQLQueryObj(filter, tipoDatabase, TracciaSearchType.DELETE_TRACCE);
		ISQLQueryObject sqlQueryObjectDelete = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
		sqlQueryObjectDelete.addDeleteTable(CostantiDB.TRACCE);
		sqlQueryObjectDelete.addWhereINSelectSQLCondition(false, CostantiDB.TRACCE_COLUMN_ID, from);
		return sqlQueryObjectDelete;
	}
	
	private static ISQLQueryObject createSQLQueryObj(FiltroRicercaTracce filtro,String tipoDatabase,TracciaSearchType tipoRicerca) throws SQLQueryObjectException{
		
		ISQLQueryObject sqlQueryObject = TracciaDriverUtilities.getSQLQueryObject(tipoDatabase);
		sqlQueryObject.addFromTable(CostantiDB.TRACCE);
		
		//select field
		boolean distinct = true;
		switch (tipoRicerca) {
		case TRACCE:
			sqlQueryObject.setSelectDistinct(distinct);
			sqlQueryObject.addSelectAliasField(CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_ID, "idTraccia");
			sqlQueryObject.addSelectField(CostantiDB.TRACCE+".gdo");
			break;
		case COUNT_TRACCE:
			sqlQueryObject.addSelectCountField(CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_ID, "countTracce",distinct);
			break;
		case DELETE_TRACCE:
			sqlQueryObject.setSelectDistinct(distinct);
			sqlQueryObject.addSelectAliasField(CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_ID, "idTraccia");
			break;
		}

		
		
		// WHERE
		
		sqlQueryObject.setANDLogicOperator(true);
				
		if(TracciaDriverUtilities.isDefined(filtro.getMinDate())){
			sqlQueryObject.addWhereCondition(CostantiDB.TRACCE_COLUMN_GDO+">=?");
		}
		if(TracciaDriverUtilities.isDefined(filtro.getMaxDate())){
			sqlQueryObject.addWhereCondition(CostantiDB.TRACCE_COLUMN_GDO+"<=?");
		}
		
		if(TracciaDriverUtilities.isDefined(filtro.getIdTransazione())){
			sqlQueryObject.addWhereCondition(CostantiDB.TRACCE_COLUMN_ID_TRANSAZIONE+"=?");
		}
		if(TracciaDriverUtilities.isDefined(filtro.getTipoTraccia())){
			sqlQueryObject.addWhereCondition(CostantiDB.TRACCE_COLUMN_TIPO_MESSAGGIO+"=?");
		}		
		if(TracciaDriverUtilities.isDefined(filtro.getTipoPdD())){
			sqlQueryObject.addWhereCondition(CostantiDB.TRACCE_COLUMN_PDD_RUOLO+"=?");
		}
		if(TracciaDriverUtilities.isDefined(filtro.getDominio())){
			if(TracciaDriverUtilities.isDefined(filtro.getDominio().getCodicePorta())){
				sqlQueryObject.addWhereCondition(CostantiDB.TRACCE_COLUMN_PDD_CODICE+"=?");
			}
			if(TracciaDriverUtilities.isDefined(filtro.getDominio().getTipo())){
				sqlQueryObject.addWhereCondition(CostantiDB.TRACCE_COLUMN_PDD_TIPO_SOGGETTO+"=?");
			}
			if(TracciaDriverUtilities.isDefined(filtro.getDominio().getNome())){
				sqlQueryObject.addWhereCondition(CostantiDB.TRACCE_COLUMN_PDD_NOME_SOGGETTO+"=?");
			}
		}
		
		
		if(TracciaDriverUtilities.isDefined(filtro.getIdBusta()) && 
				(TracciaDriverUtilities.isDefined(filtro.getIdBustaRichiesta()) || 
				TracciaDriverUtilities.isDefined(filtro.getIdBustaRisposta()))){
			throw new SQLQueryObjectException("Non è possibile definire sia il filtro idBusta che uno dei filtri su richiesta e/o risposta)");
		}
		
		if(TracciaDriverUtilities.isDefined(filtro.getIdBusta())){
			sqlQueryObject.addWhereCondition(CostantiDB.TRACCE_COLUMN_ID_MESSAGGIO+"=?");	// utilizzabile con tipo traccia
		}
		else if(TracciaDriverUtilities.isDefined(filtro.getIdBustaRichiesta()) && 
				TracciaDriverUtilities.isDefined(filtro.getIdBustaRisposta())){
			if(TracciaDriverUtilities.isDefined(filtro.getTipoTraccia())){
				throw new SQLQueryObjectException("Non è possibile definire il filtro sul tipo di traccia se si impostano entrambi gli identificativi di richiesta e risposta");
			}
			sqlQueryObject.addWhereCondition(false,CostantiDB.TRACCE_COLUMN_TIPO_MESSAGGIO+"=? AND "+CostantiDB.TRACCE_COLUMN_ID_MESSAGGIO+"=?",
					CostantiDB.TRACCE_COLUMN_TIPO_MESSAGGIO+"=? AND "+CostantiDB.TRACCE_COLUMN_ID_MESSAGGIO+"=?");	
		}
		else if(TracciaDriverUtilities.isDefined(filtro.getIdBustaRichiesta())){
			if(TracciaDriverUtilities.isDefined(filtro.getTipoTraccia())){
				if(RuoloMessaggio.RISPOSTA.equals(filtro.getTipoTraccia())){
					throw new SQLQueryObjectException("Non è possibile definire il filtro sul tipo di traccia a '"+RuoloMessaggio.RISPOSTA.getTipo()+
							"' ed il filtro sull'identificativo di richiesta");
				}
				sqlQueryObject.addWhereCondition(CostantiDB.TRACCE_COLUMN_ID_MESSAGGIO+"=?");
			}
			else{
				sqlQueryObject.addWhereCondition(CostantiDB.TRACCE_COLUMN_TIPO_MESSAGGIO+"=? AND "+CostantiDB.TRACCE_COLUMN_ID_MESSAGGIO+"=?");
			}
		}
		else if(TracciaDriverUtilities.isDefined(filtro.getIdBustaRisposta())){
			if(TracciaDriverUtilities.isDefined(filtro.getTipoTraccia())){
				if(RuoloMessaggio.RICHIESTA.equals(filtro.getTipoTraccia())){
					throw new SQLQueryObjectException("Non è possibile definire il filtro sul tipo di traccia a '"+RuoloMessaggio.RICHIESTA.getTipo()+
							"' ed il filtro sull'identificativo di risposta");
				}
				sqlQueryObject.addWhereCondition(CostantiDB.TRACCE_COLUMN_ID_MESSAGGIO+"=?");
			}
			else{
				sqlQueryObject.addWhereCondition(CostantiDB.TRACCE_COLUMN_TIPO_MESSAGGIO+"=? AND "+CostantiDB.TRACCE_COLUMN_ID_MESSAGGIO+"=?");
			}
		}
		if(TracciaDriverUtilities.isDefined(filtro.getRiferimentoMessaggio())){
			sqlQueryObject.addWhereCondition(CostantiDB.TRACCE_COLUMN_RIFERIMENTO_MESSAGGIO+"=?");
		}
		if(filtro.isRicercaSoloBusteErrore()){
			ISQLQueryObject sqlQueryObjectBusteErrore = TracciaDriverUtilities.getSQLQueryObject(tipoDatabase);
			sqlQueryObjectBusteErrore.addFromTable(CostantiDB.TRACCE_ECCEZIONI);
			sqlQueryObjectBusteErrore.addSelectField(CostantiDB.TRACCE_ECCEZIONI_COLUMN_ID_TRACCIA);
			sqlQueryObjectBusteErrore.addWhereCondition(CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_ID+"="+CostantiDB.TRACCE_ECCEZIONI+"."+CostantiDB.TRACCE_ECCEZIONI_COLUMN_ID_TRACCIA);
			sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectBusteErrore);
		}
		if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo())){
			if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo().getMittente())){
				if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo().getMittente().getTipo())){
					sqlQueryObject.addWhereCondition(CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_MITTENTE_TIPO+"=?");
				}
				if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo().getMittente().getNome())){
					sqlQueryObject.addWhereCondition(CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_MITTENTE_NOME+"=?");
				}
				if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo().getMittente().getCodicePorta())){
					sqlQueryObject.addWhereCondition(CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_MITTENTE_IDPORTA+"=?");
				}
			}
			if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo().getDestinatario())){
				if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo().getDestinatario().getTipo())){
					sqlQueryObject.addWhereCondition(CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_DESTINATARIO_TIPO+"=?");
				}
				if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo().getDestinatario().getNome())){
					sqlQueryObject.addWhereCondition(CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_DESTINATARIO_NOME+"=?");
				}
				if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo().getDestinatario().getCodicePorta())){
					sqlQueryObject.addWhereCondition(CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_DESTINATARIO_IDPORTA+"=?");
				}
			}
			if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo().getTipoServizio())){
				sqlQueryObject.addWhereCondition(CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_SERVIZIO_TIPO+"=?");
			}
			if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo().getServizio())){
				sqlQueryObject.addWhereCondition(CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_SERVIZIO_NOME+"=?");
			}
			if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo().getVersioneServizio())){
				sqlQueryObject.addWhereCondition(CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_SERVIZIO_VERSIONE+"=?");
			}
			if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo().getAzione())){
				sqlQueryObject.addWhereCondition(CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_AZIONE+"=?");
			}
			if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo().getProfiloCollaborazioneProtocollo())){
				sqlQueryObject.addWhereCondition(CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_PROFILO_COLLABORAZIONE+"=?");
			}
			if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo().getProfiloCollaborazioneEngine())){
				sqlQueryObject.addWhereCondition(CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_PROFILO_COLLABORAZIONE_SDK_CONSTANT+"=?");
			}
		}		
		
		if(TracciaDriverUtilities.isDefined(filtro.getServizioApplicativoFruitore())){
			sqlQueryObject.addWhereCondition(CostantiDB.TRACCE_COLUMN_SA_FRUITORE+"=?");
		}
		if(TracciaDriverUtilities.isDefined(filtro.getServizioApplicativoErogatore())){
			sqlQueryObject.addWhereCondition(CostantiDB.TRACCE_COLUMN_SA_EROGATORE+"=?");
		}
	
		if(TracciaDriverUtilities.isDefined(filtro.getIdCorrelazioneApplicativa()) && TracciaDriverUtilities.isDefined(filtro.getIdCorrelazioneApplicativaRisposta())){
			sqlQueryObject.addWhereCondition((!filtro.isIdCorrelazioneApplicativaOrMatch()),
						CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_CORRELAZIONE_APPLICATIVA_RICHIESTA+"=?",
						CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_CORRELAZIONE_APPLICATIVA_RISPOSTA+"=?");
		}
		else if(TracciaDriverUtilities.isDefined(filtro.getIdCorrelazioneApplicativa())){
			sqlQueryObject.addWhereCondition(CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_CORRELAZIONE_APPLICATIVA_RICHIESTA+"=?");
		}
		else if(TracciaDriverUtilities.isDefined(filtro.getIdCorrelazioneApplicativaRisposta())){
			sqlQueryObject.addWhereCondition(CostantiDB.TRACCE+"."+CostantiDB.TRACCE_COLUMN_CORRELAZIONE_APPLICATIVA_RISPOSTA+"=?");
		}
		
		
		if(TracciaDriverUtilities.isDefined(filtro.getProtocollo())){
			sqlQueryObject.addWhereCondition(CostantiDB.TRACCE_COLUMN_PROTOCOLLO+"=?");
		}
		
		if(filtro.getPropertiesNames()!=null && filtro.getPropertiesNames().length>0){
			String [] names = filtro.getPropertiesNames();
			for (int i = 0; i < names.length; i++) {
				sqlQueryObject.addWhereCondition(names[i]+"=?");	
			}
		}
		
		switch (tipoRicerca) {
		case TRACCE:
			
			FiltroRicercaTracceConPaginazione f = (FiltroRicercaTracceConPaginazione) filtro;
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
		case COUNT_TRACCE:
			// Niente da effettuare
			break;
		case DELETE_TRACCE:
			// Niente da effettuare
			break;
		}
				
		return sqlQueryObject;
		
	}
	
	private static final String format = "yyyy-MM-dd_HH:mm:ss.SSS";
	
	public static int setValuesSearch(Object object,FiltroRicercaTracce filtro,int startIndex) throws SQLException{
		
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
		
		if(TracciaDriverUtilities.isDefined(filtro.getMinDate())){
			if(pstmt!=null)
				pstmt.setTimestamp(startIndex++, new Timestamp(filtro.getMinDate().getTime()));
			if(query!=null)
				query.replaceFirst("\\?","'"+dateformat.format(filtro.getMinDate())+"'");
		}
		if(TracciaDriverUtilities.isDefined(filtro.getMaxDate())){
			if(pstmt!=null)
				pstmt.setTimestamp(startIndex++, new Timestamp(filtro.getMaxDate().getTime()));
			if(query!=null)
				query.replaceFirst("\\?", "'"+dateformat.format(filtro.getMaxDate())+"'");
		}
	
		
		if(TracciaDriverUtilities.isDefined(filtro.getIdTransazione())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filtro.getIdTransazione());
			if(query!=null)
				query.replaceFirst("\\?", "'"+filtro.getIdTransazione()+"'");
		}	
		if(TracciaDriverUtilities.isDefined(filtro.getTipoTraccia())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filtro.getTipoTraccia().getTipo());
			if(query!=null)
				query.replaceFirst("\\?", "'"+filtro.getTipoTraccia().getTipo()+"'");
		}		
		if(TracciaDriverUtilities.isDefined(filtro.getTipoPdD())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filtro.getTipoPdD().getTipo());
			if(query!=null)
				query.replaceFirst("\\?", "'"+filtro.getTipoPdD().getTipo()+"'");
		}
		if(TracciaDriverUtilities.isDefined(filtro.getDominio())){
			if(TracciaDriverUtilities.isDefined(filtro.getDominio().getCodicePorta())){
				if(pstmt!=null)
					pstmt.setString(startIndex++, filtro.getDominio().getCodicePorta());
				if(query!=null)
					query.replaceFirst("\\?", "'"+filtro.getDominio().getCodicePorta()+"'");
			}
			if(TracciaDriverUtilities.isDefined(filtro.getDominio().getTipo())){
				if(pstmt!=null)
					pstmt.setString(startIndex++, filtro.getDominio().getTipo());
				if(query!=null)
					query.replaceFirst("\\?", "'"+filtro.getDominio().getTipo()+"'");
			}
			if(TracciaDriverUtilities.isDefined(filtro.getDominio().getNome())){
				if(pstmt!=null)
					pstmt.setString(startIndex++, filtro.getDominio().getNome());
				if(query!=null)
					query.replaceFirst("\\?", "'"+filtro.getDominio().getNome()+"'");
			}
		}
		
		
		if(TracciaDriverUtilities.isDefined(filtro.getIdBusta())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filtro.getIdBusta());
			if(query!=null)
				query.replaceFirst("\\?", "'"+filtro.getIdBusta()+"'");
		}
		else if(TracciaDriverUtilities.isDefined(filtro.getIdBustaRichiesta()) && 
				TracciaDriverUtilities.isDefined(filtro.getIdBustaRisposta())){
			
			if(pstmt!=null)
				pstmt.setString(startIndex++, RuoloMessaggio.RICHIESTA.getTipo());
			if(query!=null)
				query.replaceFirst("\\?", "'"+RuoloMessaggio.RICHIESTA.getTipo()+"'");
			if(pstmt!=null)
				pstmt.setString(startIndex++, filtro.getIdBustaRichiesta());
			if(query!=null)
				query.replaceFirst("\\?", "'"+filtro.getIdBustaRichiesta()+"'");
			
			if(pstmt!=null)
				pstmt.setString(startIndex++, RuoloMessaggio.RISPOSTA.getTipo());
			if(query!=null)
				query.replaceFirst("\\?", "'"+RuoloMessaggio.RISPOSTA.getTipo()+"'");
			if(pstmt!=null)
				pstmt.setString(startIndex++, filtro.getIdBustaRisposta());
			if(query!=null)
				query.replaceFirst("\\?", "'"+filtro.getIdBustaRisposta()+"'");

		}
		else if(TracciaDriverUtilities.isDefined(filtro.getIdBustaRichiesta())){
			if(TracciaDriverUtilities.isDefined(filtro.getTipoTraccia())){
				if(pstmt!=null)
					pstmt.setString(startIndex++, filtro.getIdBustaRichiesta());
				if(query!=null)
					query.replaceFirst("\\?", "'"+filtro.getIdBustaRichiesta()+"'");
			}
			else{
				if(pstmt!=null)
					pstmt.setString(startIndex++, RuoloMessaggio.RICHIESTA.getTipo());
				if(query!=null)
					query.replaceFirst("\\?", "'"+RuoloMessaggio.RICHIESTA.getTipo()+"'");
				if(pstmt!=null)
					pstmt.setString(startIndex++, filtro.getIdBustaRichiesta());
				if(query!=null)
					query.replaceFirst("\\?", "'"+filtro.getIdBustaRichiesta()+"'");
			}
		}
		else if(TracciaDriverUtilities.isDefined(filtro.getIdBustaRisposta())){
			if(TracciaDriverUtilities.isDefined(filtro.getTipoTraccia())){
				if(pstmt!=null)
					pstmt.setString(startIndex++, filtro.getIdBustaRisposta());
				if(query!=null)
					query.replaceFirst("\\?", "'"+filtro.getIdBustaRisposta()+"'");
			}
			else{
				if(pstmt!=null)
					pstmt.setString(startIndex++, RuoloMessaggio.RISPOSTA.getTipo());
				if(query!=null)
					query.replaceFirst("\\?", "'"+RuoloMessaggio.RISPOSTA.getTipo()+"'");
				if(pstmt!=null)
					pstmt.setString(startIndex++, filtro.getIdBustaRisposta());
				if(query!=null)
					query.replaceFirst("\\?", "'"+filtro.getIdBustaRisposta()+"'");
			}
		}
		if(TracciaDriverUtilities.isDefined(filtro.getRiferimentoMessaggio())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filtro.getRiferimentoMessaggio());
			if(query!=null)
				query.replaceFirst("\\?", "'"+filtro.getRiferimentoMessaggio()+"'");
		}
		if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo())){
			if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo().getMittente())){
				if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo().getMittente().getTipo())){
					if(pstmt!=null)
						pstmt.setString(startIndex++, filtro.getInformazioniProtocollo().getMittente().getTipo());
					if(query!=null)
						query.replaceFirst("\\?", "'"+filtro.getInformazioniProtocollo().getMittente().getTipo()+"'");
				}
				if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo().getMittente().getNome())){
					if(pstmt!=null)
						pstmt.setString(startIndex++, filtro.getInformazioniProtocollo().getMittente().getNome());
					if(query!=null)
						query.replaceFirst("\\?", "'"+filtro.getInformazioniProtocollo().getMittente().getNome()+"'");
				}
				if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo().getMittente().getCodicePorta())){
					if(pstmt!=null)
						pstmt.setString(startIndex++, filtro.getInformazioniProtocollo().getMittente().getCodicePorta());
					if(query!=null)
						query.replaceFirst("\\?", "'"+filtro.getInformazioniProtocollo().getMittente().getCodicePorta()+"'");
				}
			}
			if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo().getDestinatario())){
				if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo().getDestinatario().getTipo())){
					if(pstmt!=null)
						pstmt.setString(startIndex++, filtro.getInformazioniProtocollo().getDestinatario().getTipo());
					if(query!=null)
						query.replaceFirst("\\?", "'"+filtro.getInformazioniProtocollo().getDestinatario().getTipo()+"'");
				}
				if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo().getDestinatario().getNome())){
					if(pstmt!=null)
						pstmt.setString(startIndex++, filtro.getInformazioniProtocollo().getDestinatario().getNome());
					if(query!=null)
						query.replaceFirst("\\?", "'"+filtro.getInformazioniProtocollo().getDestinatario().getNome()+"'");
				}
				if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo().getDestinatario().getCodicePorta())){
					if(pstmt!=null)
						pstmt.setString(startIndex++, filtro.getInformazioniProtocollo().getDestinatario().getCodicePorta());
					if(query!=null)
						query.replaceFirst("\\?", "'"+filtro.getInformazioniProtocollo().getDestinatario().getCodicePorta()+"'");
				}
			}
			if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo().getTipoServizio())){
				if(pstmt!=null)
					pstmt.setString(startIndex++, filtro.getInformazioniProtocollo().getTipoServizio());
				if(query!=null)
					query.replaceFirst("\\?", "'"+filtro.getInformazioniProtocollo().getTipoServizio()+"'");
			}
			if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo().getServizio())){
				if(pstmt!=null)
					pstmt.setString(startIndex++, filtro.getInformazioniProtocollo().getServizio());
				if(query!=null)
					query.replaceFirst("\\?", "'"+filtro.getInformazioniProtocollo().getServizio()+"'");
			}
			if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo().getVersioneServizio())){
				if(pstmt!=null)
					pstmt.setInt(startIndex++, filtro.getInformazioniProtocollo().getVersioneServizio());
				if(query!=null)
					query.replaceFirst("\\?", "'"+filtro.getInformazioniProtocollo().getVersioneServizio()+"'");
			}
			if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo().getAzione())){
				if(pstmt!=null)
					pstmt.setString(startIndex++, filtro.getInformazioniProtocollo().getAzione());
				if(query!=null)
					query.replaceFirst("\\?", "'"+filtro.getInformazioniProtocollo().getAzione()+"'");
			}
			if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo().getProfiloCollaborazioneProtocollo())){
				if(pstmt!=null)
					pstmt.setString(startIndex++, filtro.getInformazioniProtocollo().getProfiloCollaborazioneProtocollo());
				if(query!=null)
					query.replaceFirst("\\?", "'"+filtro.getInformazioniProtocollo().getProfiloCollaborazioneProtocollo()+"'");
			}
			if(TracciaDriverUtilities.isDefined(filtro.getInformazioniProtocollo().getProfiloCollaborazioneEngine())){
				if(pstmt!=null)
					pstmt.setString(startIndex++, filtro.getInformazioniProtocollo().getProfiloCollaborazioneEngine().getEngineValue());
				if(query!=null)
					query.replaceFirst("\\?", "'"+filtro.getInformazioniProtocollo().getProfiloCollaborazioneEngine().getEngineValue()+"'");
			}
		}	
		
		
		if(TracciaDriverUtilities.isDefined(filtro.getServizioApplicativoFruitore())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filtro.getServizioApplicativoFruitore());
			if(query!=null)
				query.replaceFirst("\\?", "'"+filtro.getServizioApplicativoFruitore()+"'");
		}
		if(TracciaDriverUtilities.isDefined(filtro.getServizioApplicativoErogatore())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filtro.getServizioApplicativoErogatore());
			if(query!=null)
				query.replaceFirst("\\?", "'"+filtro.getServizioApplicativoErogatore()+"'");
		}
		
		
		if(TracciaDriverUtilities.isDefined(filtro.getIdCorrelazioneApplicativa()) && TracciaDriverUtilities.isDefined(filtro.getIdCorrelazioneApplicativaRisposta())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filtro.getIdCorrelazioneApplicativa());
			if(query!=null)
				query.replaceFirst("\\?", "'"+filtro.getIdCorrelazioneApplicativa()+"'");
			if(pstmt!=null)
				pstmt.setString(startIndex++, filtro.getIdCorrelazioneApplicativaRisposta());
			if(query!=null)
				query.replaceFirst("\\?", "'"+filtro.getIdCorrelazioneApplicativaRisposta()+"'");
			
		}
		else if(TracciaDriverUtilities.isDefined(filtro.getIdCorrelazioneApplicativa())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filtro.getIdCorrelazioneApplicativa());
			if(query!=null)
				query.replaceFirst("\\?", "'"+filtro.getIdCorrelazioneApplicativa()+"'");
		}
		else if(TracciaDriverUtilities.isDefined(filtro.getIdCorrelazioneApplicativaRisposta())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filtro.getIdCorrelazioneApplicativaRisposta());
			if(query!=null)
				query.replaceFirst("\\?", "'"+filtro.getIdCorrelazioneApplicativaRisposta()+"'");
		}
		
	
		if(TracciaDriverUtilities.isDefined(filtro.getProtocollo())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filtro.getProtocollo());
			if(query!=null)
				query.replaceFirst("\\?", "'"+filtro.getProtocollo()+"'");
		}
	
		if(filtro.getPropertiesNames()!=null && filtro.getPropertiesNames().length>0){
			String [] names = filtro.getPropertiesNames();
			for (int i = 0; i < names.length; i++) {
				String value = filtro.getProperty(names[i]);
				if(pstmt!=null)
					pstmt.setString(startIndex++, value);
				if(query!=null)
					query.replaceFirst("\\?", "'"+value+"'");
			}
		}

		return startIndex;
	}
	
		
	
	/**
	 * Recupera la traccia
	 * 
	 * @return Traccia
	 * @throws DriverMsgDiagnosticiException
	 */
	public static Traccia getTraccia(Connection con,String tipoDatabase,
			Logger log,Long id,List<String> properties,
			ProtocolliRegistrati protocolli) throws DriverTracciamentoException, DriverTracciamentoNotFoundException, SQLQueryObjectException{
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		PreparedStatement pstmtLista = null;
		ResultSet rsLista = null;
		Traccia tr = null;
		
		try{
			
			ISQLQueryObject sqlQueryObject = TracciaDriverUtilities.getSQLQueryObject(tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.TRACCE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(CostantiDB.TRACCE_COLUMN_ID+"=?");
			
			pstmt = con.prepareStatement(sqlQueryObject.toString());
			pstmt.setLong(1, id);
			rs = pstmt.executeQuery();
			if(rs.next()){
				
				tr = new Traccia();
				tr.setGdo(rs.getTimestamp(CostantiDB.TRACCE_COLUMN_GDO));
				tr.setIdTransazione(rs.getString(CostantiDB.TRACCE_COLUMN_ID_TRANSAZIONE));
				IDSoggetto idSoggetto = new IDSoggetto();
				idSoggetto.setTipo(rs.getString(CostantiDB.TRACCE_COLUMN_PDD_TIPO_SOGGETTO));
				idSoggetto.setNome(rs.getString(CostantiDB.TRACCE_COLUMN_PDD_NOME_SOGGETTO));
				idSoggetto.setCodicePorta(rs.getString(CostantiDB.TRACCE_COLUMN_PDD_CODICE));
				tr.setTipoPdD(TipoPdD.toTipoPdD(rs.getString(CostantiDB.TRACCE_COLUMN_PDD_RUOLO)));
				tr.setIdSoggetto(idSoggetto);
				String t = rs.getString(CostantiDB.TRACCE_COLUMN_TIPO_MESSAGGIO);
				if(t!=null){
					tr.setTipoMessaggio(RuoloMessaggio.toTipoTraccia(t));
				}
				EsitoElaborazioneMessaggioTracciato esitoTraccia = new EsitoElaborazioneMessaggioTracciato();
				esitoTraccia.setEsito(EsitoElaborazioneMessaggioTracciatura.valueOf(rs.getString(CostantiDB.TRACCE_COLUMN_ESITO_ELABORAZIONE)));
				esitoTraccia.setDettaglio(rs.getString(CostantiDB.TRACCE_COLUMN_DETTAGLIO_ESITO_ELABORAZIONE));
				tr.setEsitoElaborazioneMessaggioTracciato(esitoTraccia);
				tr.setCorrelazioneApplicativa(rs.getString(CostantiDB.TRACCE_COLUMN_CORRELAZIONE_APPLICATIVA_RICHIESTA));
				tr.setCorrelazioneApplicativaRisposta(rs.getString(CostantiDB.TRACCE_COLUMN_CORRELAZIONE_APPLICATIVA_RISPOSTA));
				tr.setLocation(rs.getString(CostantiDB.TRACCE_COLUMN_LOCATION));
				tr.setProtocollo(rs.getString(CostantiDB.TRACCE_COLUMN_PROTOCOLLO));
				if(properties!=null){
					for (int i = 0; i < properties.size(); i++) {
						String key = properties.get(i);
						tr.addProperty(key, rs.getString(key));
					}
				}
				Busta busta = new Busta(tr.getProtocollo());
				
				if(rs.getString(CostantiDB.TRACCE_COLUMN_MITTENTE_TIPO)!=null && ("".equals(rs.getString(CostantiDB.TRACCE_COLUMN_MITTENTE_TIPO))==false) )
					busta.setTipoMittente(rs.getString(CostantiDB.TRACCE_COLUMN_MITTENTE_TIPO));
				if(rs.getString(CostantiDB.TRACCE_COLUMN_MITTENTE_NOME)!=null && ("".equals(rs.getString(CostantiDB.TRACCE_COLUMN_MITTENTE_NOME))==false) )
					busta.setMittente(rs.getString(CostantiDB.TRACCE_COLUMN_MITTENTE_NOME));
				if(rs.getString(CostantiDB.TRACCE_COLUMN_MITTENTE_IDPORTA)!=null && ("".equals(rs.getString(CostantiDB.TRACCE_COLUMN_MITTENTE_IDPORTA))==false) )
					busta.setIdentificativoPortaMittente(rs.getString(CostantiDB.TRACCE_COLUMN_MITTENTE_IDPORTA));
				if(rs.getString(CostantiDB.TRACCE_COLUMN_MITTENTE_INDIRIZZO)!=null && ("".equals(rs.getString(CostantiDB.TRACCE_COLUMN_MITTENTE_INDIRIZZO))==false)){
					busta.setIndirizzoMittente(rs.getString(CostantiDB.TRACCE_COLUMN_MITTENTE_INDIRIZZO));
				}
				
				if(rs.getString(CostantiDB.TRACCE_COLUMN_DESTINATARIO_TIPO)!=null && ("".equals(rs.getString(CostantiDB.TRACCE_COLUMN_DESTINATARIO_TIPO))==false))
					busta.setTipoDestinatario(rs.getString(CostantiDB.TRACCE_COLUMN_DESTINATARIO_TIPO));
				if(rs.getString(CostantiDB.TRACCE_COLUMN_DESTINATARIO_NOME)!=null && ("".equals(rs.getString(CostantiDB.TRACCE_COLUMN_DESTINATARIO_NOME))==false))
					busta.setDestinatario(rs.getString(CostantiDB.TRACCE_COLUMN_DESTINATARIO_NOME));
				if(rs.getString(CostantiDB.TRACCE_COLUMN_DESTINATARIO_IDPORTA)!=null && ("".equals(rs.getString(CostantiDB.TRACCE_COLUMN_DESTINATARIO_IDPORTA))==false) )
					busta.setIdentificativoPortaDestinatario(rs.getString(CostantiDB.TRACCE_COLUMN_DESTINATARIO_IDPORTA));
				if(rs.getString(CostantiDB.TRACCE_COLUMN_DESTINATARIO_INDIRIZZO)!=null && ("".equals(rs.getString(CostantiDB.TRACCE_COLUMN_DESTINATARIO_INDIRIZZO))==false))
					busta.setIndirizzoDestinatario(rs.getString(CostantiDB.TRACCE_COLUMN_DESTINATARIO_INDIRIZZO));
				
				if(rs.getString(CostantiDB.TRACCE_COLUMN_SA_FRUITORE)!=null && ("".equals(rs.getString(CostantiDB.TRACCE_COLUMN_SA_FRUITORE))==false))
					busta.setServizioApplicativoFruitore(rs.getString(CostantiDB.TRACCE_COLUMN_SA_FRUITORE));
				if(rs.getString(CostantiDB.TRACCE_COLUMN_SA_EROGATORE)!=null && ("".equals(rs.getString(CostantiDB.TRACCE_COLUMN_SA_EROGATORE))==false))
					busta.setServizioApplicativoErogatore(rs.getString(CostantiDB.TRACCE_COLUMN_SA_EROGATORE));
				
				if(rs.getString(CostantiDB.TRACCE_COLUMN_PROFILO_COLLABORAZIONE)!=null && ("".equals(rs.getString(CostantiDB.TRACCE_COLUMN_PROFILO_COLLABORAZIONE))==false))
					busta.setProfiloDiCollaborazioneValue(rs.getString(CostantiDB.TRACCE_COLUMN_PROFILO_COLLABORAZIONE));
				if(rs.getString(CostantiDB.TRACCE_COLUMN_PROFILO_COLLABORAZIONE_SDK_CONSTANT)!=null && ("".equals(rs.getString(CostantiDB.TRACCE_COLUMN_PROFILO_COLLABORAZIONE_SDK_CONSTANT))==false))
					busta.setProfiloDiCollaborazione(ProfiloDiCollaborazione.toProfiloDiCollaborazione(rs.getString(CostantiDB.TRACCE_COLUMN_PROFILO_COLLABORAZIONE_SDK_CONSTANT)));
				
				Integer versioneServizioCorrelato = rs.getInt(CostantiDB.TRACCE_COLUMN_SERVIZIO_CORRELATO_VERSIONE);
				if(rs.wasNull()){
					versioneServizioCorrelato = null;
				}
				if(versioneServizioCorrelato!=null){
					busta.setVersioneServizioCorrelato(versioneServizioCorrelato);
				}
				if(rs.getString(CostantiDB.TRACCE_COLUMN_SERVIZIO_CORRELATO_NOME)!=null && ("".equals(rs.getString(CostantiDB.TRACCE_COLUMN_SERVIZIO_CORRELATO_NOME))==false))
					busta.setServizioCorrelato(rs.getString(CostantiDB.TRACCE_COLUMN_SERVIZIO_CORRELATO_NOME));
				if(rs.getString(CostantiDB.TRACCE_COLUMN_SERVIZIO_CORRELATO_TIPO)!=null && ("".equals(rs.getString(CostantiDB.TRACCE_COLUMN_SERVIZIO_CORRELATO_TIPO))==false))
					busta.setTipoServizioCorrelato(rs.getString(CostantiDB.TRACCE_COLUMN_SERVIZIO_CORRELATO_TIPO));
				
				if(rs.getString(CostantiDB.TRACCE_COLUMN_COLLABORAZIONE)!=null && ("".equals(rs.getString(CostantiDB.TRACCE_COLUMN_COLLABORAZIONE))==false))
					busta.setCollaborazione(rs.getString(CostantiDB.TRACCE_COLUMN_COLLABORAZIONE));
				
				Integer versioneServizio = rs.getInt(CostantiDB.TRACCE_COLUMN_SERVIZIO_VERSIONE);
				if(rs.wasNull()){
					versioneServizio = null;
				}
				if(versioneServizio!=null){
					busta.setVersioneServizio(versioneServizio);
				}
				if(rs.getString(CostantiDB.TRACCE_COLUMN_SERVIZIO_NOME)!=null && ("".equals(rs.getString(CostantiDB.TRACCE_COLUMN_SERVIZIO_NOME))==false))
					busta.setServizio(rs.getString(CostantiDB.TRACCE_COLUMN_SERVIZIO_NOME));
				if(rs.getString(CostantiDB.TRACCE_COLUMN_SERVIZIO_TIPO)!=null && ("".equals(rs.getString(CostantiDB.TRACCE_COLUMN_SERVIZIO_TIPO))==false))
					busta.setTipoServizio(rs.getString(CostantiDB.TRACCE_COLUMN_SERVIZIO_TIPO));
				if(rs.getString(CostantiDB.TRACCE_COLUMN_AZIONE)!=null && ("".equals(rs.getString(CostantiDB.TRACCE_COLUMN_AZIONE))==false))
					busta.setAzione(rs.getString(CostantiDB.TRACCE_COLUMN_AZIONE));
				
				if(rs.getString(CostantiDB.TRACCE_COLUMN_ID_MESSAGGIO)!=null && ("".equals(rs.getString(CostantiDB.TRACCE_COLUMN_ID_MESSAGGIO))==false))
					busta.setID(rs.getString(CostantiDB.TRACCE_COLUMN_ID_MESSAGGIO));
				
				if(rs.getTimestamp(CostantiDB.TRACCE_COLUMN_ORA_REGISTRAZIONE)!=null )
					busta.setOraRegistrazione(rs.getTimestamp(CostantiDB.TRACCE_COLUMN_ORA_REGISTRAZIONE));
				if(rs.getString(CostantiDB.TRACCE_COLUMN_ORA_REGISTRAZIONE_TIPO)!=null && ("".equals(rs.getString(CostantiDB.TRACCE_COLUMN_ORA_REGISTRAZIONE_TIPO))==false))
					busta.setTipoOraRegistrazioneValue(rs.getString(CostantiDB.TRACCE_COLUMN_ORA_REGISTRAZIONE_TIPO));
				if(rs.getString(CostantiDB.TRACCE_COLUMN_ORA_REGISTRAZIONE_TIPO_SDK_CONSTANT)!=null && ("".equals(rs.getString(CostantiDB.TRACCE_COLUMN_ORA_REGISTRAZIONE_TIPO_SDK_CONSTANT))==false))
					busta.setTipoOraRegistrazione(TipoOraRegistrazione.toTipoOraRegistrazione(rs.getString(CostantiDB.TRACCE_COLUMN_ORA_REGISTRAZIONE_TIPO_SDK_CONSTANT)),busta.getTipoOraRegistrazioneValue());
				
				if(rs.getString(CostantiDB.TRACCE_COLUMN_RIFERIMENTO_MESSAGGIO)!=null && ("".equals(rs.getString(CostantiDB.TRACCE_COLUMN_RIFERIMENTO_MESSAGGIO))==false))
					busta.setRiferimentoMessaggio(rs.getString(CostantiDB.TRACCE_COLUMN_RIFERIMENTO_MESSAGGIO));
				
				if(rs.getTimestamp(CostantiDB.TRACCE_COLUMN_SCADENZA)!=null )
					busta.setScadenza(rs.getTimestamp(CostantiDB.TRACCE_COLUMN_SCADENZA));
				
				if(rs.getString(CostantiDB.TRACCE_COLUMN_INOLTRO)!=null && ("".equals(rs.getString(CostantiDB.TRACCE_COLUMN_INOLTRO))==false))
					busta.setInoltroValue(rs.getString(CostantiDB.TRACCE_COLUMN_INOLTRO));
				if(rs.getString(CostantiDB.TRACCE_COLUMN_INOLTRO_SDK_CONSTANT)!=null && ("".equals(rs.getString(CostantiDB.TRACCE_COLUMN_INOLTRO_SDK_CONSTANT))==false))
					busta.setInoltro(Inoltro.toInoltro(rs.getString(CostantiDB.TRACCE_COLUMN_INOLTRO_SDK_CONSTANT)),busta.getInoltroValue());
				if(rs.getInt(CostantiDB.TRACCE_COLUMN_CONFERMA_RICEZIONE)==1)
					busta.setConfermaRicezione(true);
				else
					busta.setConfermaRicezione(false);
				
				if(rs.getLong(CostantiDB.TRACCE_COLUMN_SEQUENZA)>0){
					busta.setSequenza(rs.getLong(CostantiDB.TRACCE_COLUMN_SEQUENZA));
				}
				
				busta.setDigest(rs.getString(CostantiDB.TRACCE_COLUMN_DIGEST));
				
				String headerProtocollo = rs.getString(CostantiDB.TRACCE_COLUMN_SOAP);
				if(headerProtocollo!=null){
					tr.setBustaAsString(headerProtocollo);
				}
				
				long idTraccia = rs.getLong(CostantiDB.TRACCE_COLUMN_ID);
				tr.setId(idTraccia);
				tr.addProperty(TracciaDriver.IDTRACCIA, idTraccia+"");
				
				//System.out.println("ID TRACCIA ["+idTraccia+"]");
				
				IProtocolFactory<?> protocolFactory = protocolli.getProtocolFactory(tr.getProtocollo());
				ITraduttore traduttoreProtocollo = protocolFactory.createTraduttore();
				
				// Lista trasmissioni
				sqlQueryObject = TracciaDriverUtilities.getSQLQueryObject(tipoDatabase);
				sqlQueryObject.addFromTable(CostantiDB.TRACCE_TRASMISSIONI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ID_TRACCIA+"=?");
				sqlQueryObject.setANDLogicOperator(true);
				pstmtLista = con.prepareStatement(sqlQueryObject.toString());
				pstmtLista.setLong(1, idTraccia);
				rsLista = pstmtLista.executeQuery();
				while(rsLista.next()){
					Trasmissione trtr = new Trasmissione();
					
					if(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORIGINE)!=null && ("".equals(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORIGINE))==false))
						trtr.setOrigine(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORIGINE));
					if(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORIGINE_TIPO)!=null && ("".equals(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORIGINE_TIPO))==false))
						trtr.setTipoOrigine(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORIGINE_TIPO));
					if(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORIGINE_INDIRIZZO)!=null && ("".equals(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORIGINE_INDIRIZZO))==false))
						trtr.setIndirizzoOrigine(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORIGINE_INDIRIZZO));
					if(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORIGINE_IDPORTA)!=null && ("".equals(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORIGINE_IDPORTA))==false)){
						trtr.setIdentificativoPortaOrigine(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORIGINE_IDPORTA));
					}
					
					if(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_DESTINAZIONE)!=null && ("".equals(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_DESTINAZIONE))==false))
						trtr.setDestinazione(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_DESTINAZIONE));
					if(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_DESTINAZIONE_TIPO)!=null && ("".equals(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_DESTINAZIONE_TIPO))==false))
						trtr.setTipoDestinazione(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_DESTINAZIONE_TIPO));
					if(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_DESTINAZIONE_INDIRIZZO)!=null && ("".equals(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_DESTINAZIONE_INDIRIZZO))==false))
						trtr.setIndirizzoDestinazione(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_DESTINAZIONE_INDIRIZZO));
					if(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_DESTINAZIONE_IDPORTA)!=null && ("".equals(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_DESTINAZIONE_IDPORTA))==false)){
						trtr.setIdentificativoPortaDestinazione(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_DESTINAZIONE_IDPORTA));
					}
					
					if(rsLista.getTimestamp(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORA_REGISTRAZIONE)!=null )
						trtr.setOraRegistrazione(rsLista.getTimestamp(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORA_REGISTRAZIONE));
					if(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORA_REGISTRAZIONE_TIPO)!=null && ("".equals(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORA_REGISTRAZIONE_TIPO))==false))
						trtr.setTempoValue(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORA_REGISTRAZIONE_TIPO));
					if(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORA_REGISTRAZIONE_TIPO_SDK_CONSTANT)!=null && ("".equals(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORA_REGISTRAZIONE_TIPO_SDK_CONSTANT))==false))
						trtr.setTempo(TipoOraRegistrazione.toTipoOraRegistrazione(rsLista.getString(CostantiDB.TRACCE_TRASMISSIONI_COLUMN_ORA_REGISTRAZIONE_TIPO_SDK_CONSTANT)));
					busta.addTrasmissione(trtr);
				}
				rsLista.close();
				pstmtLista.close();
				
				// Lista eccezioni
				sqlQueryObject = TracciaDriverUtilities.getSQLQueryObject(tipoDatabase);
				sqlQueryObject.addFromTable(CostantiDB.TRACCE_ECCEZIONI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition(CostantiDB.TRACCE_ECCEZIONI_COLUMN_ID_TRACCIA+"=?");
				sqlQueryObject.setANDLogicOperator(true);
				pstmtLista = con.prepareStatement(sqlQueryObject.toString());
				pstmtLista.setLong(1, idTraccia);
				rsLista = pstmtLista.executeQuery();
				
				while(rsLista.next()){
					
					Eccezione trec = Eccezione.newEccezione();
					
					String contestoCodifica = rsLista.getString(CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA);
					String contestoCodificaMeta = rsLista.getString(CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA_SDK_CONSTANT);
					String codiceEccezione = rsLista.getString(CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE);
					int codiceEccezioneMeta = rsLista.getInt(CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE_SDK_CONSTANT);
					int subCodiceEccezioneMeta = rsLista.getInt(CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE_SUBCOD_SDK_CONSTANT);
					String rilevanza = rsLista.getString(CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA);
					String rilevanzaMeta = rsLista.getString(CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA_SDK_CONSTANT);
					String posizione = rsLista.getString(CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE);
						
					
					// ContestoCodificaValue
					if(contestoCodifica!=null && (!"".equals(contestoCodifica))){
						trec.setContestoCodificaValue(contestoCodifica);
					}
					else if(contestoCodificaMeta!=null && (!"".equals(contestoCodificaMeta))){
						trec.setContestoCodificaValue(traduttoreProtocollo.toString(ContestoCodificaEccezione.toContestoCodificaEccezione(contestoCodificaMeta)));
					}
					
					// ContestoCodifica (META)
					if(contestoCodificaMeta!=null && (!"".equals(contestoCodificaMeta))){
						trec.setContestoCodifica(ContestoCodificaEccezione.toContestoCodificaEccezione(contestoCodificaMeta));
					}
					else if(contestoCodifica!=null && (!"".equals(contestoCodifica))){
						trec.setContestoCodifica(traduttoreProtocollo.toContestoCodificaEccezione(contestoCodifica));
					}
					
					// Tmp
					CodiceErroreCooperazione cod = CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceEccezioneMeta); // se non matcha con niente diventa UNKNOWN
					SubCodiceErrore subCodiceErrore = null;
					if(subCodiceEccezioneMeta>=0){
						subCodiceErrore = new SubCodiceErrore();
						subCodiceErrore.setSubCodice(subCodiceEccezioneMeta);
					}
					
					// CodiceEccezioneValue
					if(codiceEccezione!=null && (!"".equals(codiceEccezione))){
						trec.setCodiceEccezioneValue(codiceEccezione);
					}
					else {
						trec.setCodiceEccezioneValue(traduttoreProtocollo.toString(cod,subCodiceErrore));
					}
					
					// CodiceEccezione (META)
					if(CodiceErroreCooperazione.UNKNOWN.equals(cod)){
						if(codiceEccezione!=null && (!"".equals(codiceEccezione))){
							trec.setCodiceEccezione(traduttoreProtocollo.toCodiceErroreCooperazione(codiceEccezione));
						}
					}
					else{
						trec.setCodiceEccezione(cod);
					}
					
					// SubCodiceEccezione (META)
					if(subCodiceErrore!=null){
						trec.setSubCodiceEccezione(subCodiceErrore);
					}
					
					// RilevanzaValue
					if(rilevanza!=null && (!"".equals(rilevanza))){
						trec.setRilevanzaValue(rilevanza);
					}
					else if(rilevanzaMeta!=null && (!"".equals(rilevanzaMeta))){
						trec.setRilevanzaValue(traduttoreProtocollo.toString(LivelloRilevanza.toLivelloRilevanza(rilevanzaMeta)));
					}
					
					// Rilevanza
					if(rilevanzaMeta!=null && (!"".equals(rilevanzaMeta))){
						trec.setRilevanza(LivelloRilevanza.toLivelloRilevanza(rilevanzaMeta));
					}
					else if(rilevanza!=null && (!"".equals(rilevanza))){
						trec.setRilevanza(traduttoreProtocollo.toLivelloRilevanza(rilevanza));
					}
					
					// Posizione/Descrizione
					if(posizione!=null && (!"".equals(posizione))){
						trec.setDescrizione(posizione);
					}
					
					busta.addEccezione(trec);
				}
				rsLista.close();
				pstmtLista.close();
				
				// Lista riscontri
				sqlQueryObject = TracciaDriverUtilities.getSQLQueryObject(tipoDatabase);
				sqlQueryObject.addFromTable(CostantiDB.TRACCE_RISCONTRI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition(CostantiDB.TRACCE_RISCONTRI_COLUMN_ID_TRACCIA+"=?");
				sqlQueryObject.setANDLogicOperator(true);
				pstmtLista = con.prepareStatement(sqlQueryObject.toString());
				pstmtLista.setLong(1, idTraccia);
				rsLista = pstmtLista.executeQuery();
				while(rsLista.next()){
					Riscontro trris = new  Riscontro();
					if(rsLista.getString(CostantiDB.TRACCE_RISCONTRI_COLUMN_ID_RISCONTRO)!=null && ("".equals(rsLista.getString(CostantiDB.TRACCE_RISCONTRI_COLUMN_ID_RISCONTRO))==false))
						trris.setID(rsLista.getString(CostantiDB.TRACCE_RISCONTRI_COLUMN_ID_RISCONTRO));
					if(rsLista.getString(CostantiDB.TRACCE_RISCONTRI_COLUMN_RICEVUTA)!=null && ("".equals(rsLista.getString(CostantiDB.TRACCE_RISCONTRI_COLUMN_RICEVUTA))==false))
						trris.setRicevuta(rsLista.getString(CostantiDB.TRACCE_RISCONTRI_COLUMN_RICEVUTA));
					if(rsLista.getTimestamp(CostantiDB.TRACCE_RISCONTRI_COLUMN_ORA_REGISTRAZIONE)!=null )
						trris.setOraRegistrazione(rsLista.getTimestamp(CostantiDB.TRACCE_RISCONTRI_COLUMN_ORA_REGISTRAZIONE));
					if(rsLista.getString(CostantiDB.TRACCE_RISCONTRI_COLUMN_ORA_REGISTRAZIONE_TIPO)!=null && ("".equals(rsLista.getString(CostantiDB.TRACCE_RISCONTRI_COLUMN_ORA_REGISTRAZIONE_TIPO))==false))
						trris.setTipoOraRegistrazioneValue(rsLista.getString(CostantiDB.TRACCE_RISCONTRI_COLUMN_ORA_REGISTRAZIONE_TIPO));
					if(rsLista.getString(CostantiDB.TRACCE_RISCONTRI_COLUMN_ORA_REGISTRAZIONE_TIPO_SDK_CONSTANT)!=null && ("".equals(rsLista.getString(CostantiDB.TRACCE_RISCONTRI_COLUMN_ORA_REGISTRAZIONE_TIPO_SDK_CONSTANT))==false))
						trris.setTipoOraRegistrazione(TipoOraRegistrazione.toTipoOraRegistrazione(rsLista.getString(CostantiDB.TRACCE_RISCONTRI_COLUMN_ORA_REGISTRAZIONE_TIPO_SDK_CONSTANT)));
					busta.addRiscontro(trris);
				}
				rsLista.close();
				pstmtLista.close();
				
				
				// Lista Allegati
				sqlQueryObject = TracciaDriverUtilities.getSQLQueryObject(tipoDatabase);
				sqlQueryObject.addFromTable(CostantiDB.TRACCE_ALLEGATI);				
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition(CostantiDB.TRACCE_ALLEGATI_COLUMN_ID_TRACCIA+"=?");
				sqlQueryObject.setANDLogicOperator(true);
				pstmtLista = con.prepareStatement(sqlQueryObject.toString());
				pstmtLista.setLong(1, idTraccia);
				rsLista = pstmtLista.executeQuery();
				while(rsLista.next()){
					Allegato allegato = new Allegato();
					allegato.setContentId(rsLista.getString(CostantiDB.TRACCE_ALLEGATI_COLUMN_CONTENT_ID));
					allegato.setContentLocation(rsLista.getString(CostantiDB.TRACCE_ALLEGATI_COLUMN_CONTENT_LOCATION));
					allegato.setContentType(rsLista.getString(CostantiDB.TRACCE_ALLEGATI_COLUMN_CONTENT_TYPE));
					allegato.setDigest(rsLista.getString(CostantiDB.TRACCE_ALLEGATI_COLUMN_DIGEST));
					tr.addAllegato(allegato);
				}
				rsLista.close();
				pstmtLista.close();
				
				
				// Protocol extra info
				sqlQueryObject = TracciaDriverUtilities.getSQLQueryObject(tipoDatabase);
				sqlQueryObject.addFromTable(CostantiDB.TRACCE_EXT_INFO);				
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition(CostantiDB.TRACCE_EXT_PROTOCOL_INFO_COLUMN_ID_TRACCIA+"=?");
				sqlQueryObject.setANDLogicOperator(true);
				pstmtLista = con.prepareStatement(sqlQueryObject.toString());
				pstmtLista.setLong(1, idTraccia);
				rsLista = pstmtLista.executeQuery();
				while(rsLista.next()){
					busta.addProperty(rsLista.getString(CostantiDB.TRACCE_EXT_PROTOCOL_INFO_COLUMN_NAME), rsLista.getString(CostantiDB.TRACCE_EXT_PROTOCOL_INFO_COLUMN_VALUE));
				}
				rsLista.close();
				pstmtLista.close();
				
				
				tr.setBusta(busta);
				
				
			}
			rs.close();
			pstmt.close();
			
			if(tr==null){
				throw new DriverTracciamentoNotFoundException("Traccia non trovata (id:"+id+")");
			}
			return tr;
			
		}catch(DriverTracciamentoNotFoundException d){
			throw d;
		}catch(Exception e){
			throw new DriverTracciamentoException("Tracciamento exception: "+e.getMessage(),e);
		}finally{
			try{
				rsLista.close();
			}catch(Exception eClose){}
			try{
				pstmtLista.close();
			}catch(Exception eClose){}
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				pstmt.close();
			}catch(Exception eClose){}
		}
	} 
	
	
	
	
	
	protected static boolean isDefined(String v){
		return v!=null && !"".equals(v);
	}
	protected static boolean isDefined(Date v){
		return v!=null;
	}
	protected static boolean isDefined(RuoloMessaggio v){
		return v!=null;
	}
	protected static boolean isDefined(TipoPdD v){
		return v!=null;
	}
	protected static boolean isDefined(IDSoggetto v){
		return v!=null;
	}
	protected static boolean isDefined(InformazioniProtocollo v){
		return v!=null;
	}
	protected static boolean isDefined(Integer v){
		return v!=null;
	}
	protected static boolean isDefined(ProfiloDiCollaborazione v){
		return v!=null;
	}
}
