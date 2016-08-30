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

package org.openspcoop2.protocol.basic.tracciamento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
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
import org.openspcoop2.protocol.sdk.constants.SubCodiceErrore;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.sdk.constants.TipoTraccia;
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

/**
 * DriverTracciamentoUtilities
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverTracciamentoUtilities {

	public static ISQLQueryObject getSQLQueryObject(String tipoDatabase)throws SQLQueryObjectException{
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
		return sqlQueryObject;
	}
	
	
	public static ISQLQueryObject createSQLQueryObj_searchTracce(FiltroRicercaTracceConPaginazione filter,String tipoDatabase) throws SQLQueryObjectException{
		return DriverTracciamentoUtilities.createSQLQueryObj(filter, tipoDatabase, TipoRicercaTracce.TRACCE);
	}
	public static ISQLQueryObject createSQLQueryObj_countTracce(FiltroRicercaTracce filter,String tipoDatabase) throws SQLQueryObjectException{
		return DriverTracciamentoUtilities.createSQLQueryObj(filter, tipoDatabase, TipoRicercaTracce.COUNT_TRACCE);
	}
	public static ISQLQueryObject createSQLQueryObj_deleteTracce(FiltroRicercaTracce filter,String tipoDatabase) throws SQLQueryObjectException{
		ISQLQueryObject from = DriverTracciamentoUtilities.createSQLQueryObj(filter, tipoDatabase, TipoRicercaTracce.DELETE_TRACCE);
		ISQLQueryObject sqlQueryObjectDelete = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
		sqlQueryObjectDelete.addDeleteTable(CostantiDB.TRACCE);
		sqlQueryObjectDelete.addWhereINSelectSQLCondition(false, "id", from);
		return sqlQueryObjectDelete;
	}
	
	private static ISQLQueryObject createSQLQueryObj(FiltroRicercaTracce filtro,String tipoDatabase,TipoRicercaTracce tipoRicerca) throws SQLQueryObjectException{
		
		ISQLQueryObject sqlQueryObject = DriverTracciamentoUtilities.getSQLQueryObject(tipoDatabase);
		sqlQueryObject.addFromTable(CostantiDB.TRACCE);
		
		//select field
		boolean distinct = true;
		switch (tipoRicerca) {
		case TRACCE:
			sqlQueryObject.setSelectDistinct(distinct);
			sqlQueryObject.addSelectAliasField(CostantiDB.TRACCE+".id", "idTraccia");
			sqlQueryObject.addSelectField(CostantiDB.TRACCE+".gdo");
			break;
		case COUNT_TRACCE:
			sqlQueryObject.addSelectCountField(CostantiDB.TRACCE+".id", "countTracce",distinct);
			break;
		case DELETE_TRACCE:
			sqlQueryObject.setSelectDistinct(distinct);
			sqlQueryObject.addSelectAliasField(CostantiDB.TRACCE+".id", "idTraccia");
			break;
		}

		
		
		// WHERE
		
		sqlQueryObject.setANDLogicOperator(true);
				
		if(DriverTracciamentoUtilities.isDefined(filtro.getMinDate())){
			sqlQueryObject.addWhereCondition("gdo>=?");
		}
		if(DriverTracciamentoUtilities.isDefined(filtro.getMaxDate())){
			sqlQueryObject.addWhereCondition("gdo<=?");
		}
		
		
		if(DriverTracciamentoUtilities.isDefined(filtro.getTipoTraccia())){
			sqlQueryObject.addWhereCondition("tipo_messaggio=?");
		}		
		if(DriverTracciamentoUtilities.isDefined(filtro.getTipoPdD())){
			sqlQueryObject.addWhereCondition("pdd_ruolo=?");
		}
		if(DriverTracciamentoUtilities.isDefined(filtro.getDominio())){
			if(DriverTracciamentoUtilities.isDefined(filtro.getDominio().getCodicePorta())){
				sqlQueryObject.addWhereCondition("pdd_codice=?");
			}
			if(DriverTracciamentoUtilities.isDefined(filtro.getDominio().getTipo())){
				sqlQueryObject.addWhereCondition("pdd_tipo_soggetto=?");
			}
			if(DriverTracciamentoUtilities.isDefined(filtro.getDominio().getNome())){
				sqlQueryObject.addWhereCondition("pdd_nome_soggetto=?");
			}
		}
		
		
		if(DriverTracciamentoUtilities.isDefined(filtro.getIdBusta())){
			sqlQueryObject.addWhereCondition("id_messaggio=?");
		}
		if(DriverTracciamentoUtilities.isDefined(filtro.getRiferimentoMessaggio())){
			sqlQueryObject.addWhereCondition("rif_messaggio=?");
		}
		if(filtro.isRicercaSoloBusteErrore()){
			ISQLQueryObject sqlQueryObjectBusteErrore = DriverTracciamentoUtilities.getSQLQueryObject(tipoDatabase);
			sqlQueryObjectBusteErrore.addFromTable(CostantiDB.TRACCE_ECCEZIONI);
			sqlQueryObjectBusteErrore.addSelectField("idtraccia");
			sqlQueryObjectBusteErrore.addWhereCondition(CostantiDB.TRACCE+".id="+CostantiDB.TRACCE_ECCEZIONI+".idtraccia");
			sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectBusteErrore);
		}
		if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo())){
			if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo().getMittente())){
				if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo().getMittente().getTipo())){
					sqlQueryObject.addWhereCondition(CostantiDB.TRACCE+".tipo_mittente=?");
				}
				if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo().getMittente().getNome())){
					sqlQueryObject.addWhereCondition(CostantiDB.TRACCE+".mittente=?");
				}
				if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo().getMittente().getCodicePorta())){
					sqlQueryObject.addWhereCondition(CostantiDB.TRACCE+".idporta_mittente=?");
				}
			}
			if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo().getDestinatario())){
				if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo().getDestinatario().getTipo())){
					sqlQueryObject.addWhereCondition(CostantiDB.TRACCE+".tipo_destinatario=?");
				}
				if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo().getDestinatario().getNome())){
					sqlQueryObject.addWhereCondition(CostantiDB.TRACCE+".destinatario=?");
				}
				if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo().getDestinatario().getCodicePorta())){
					sqlQueryObject.addWhereCondition(CostantiDB.TRACCE+".idporta_destinatario=?");
				}
			}
			if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo().getTipoServizio())){
				sqlQueryObject.addWhereCondition(CostantiDB.TRACCE+".tipo_servizio=?");
			}
			if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo().getServizio())){
				sqlQueryObject.addWhereCondition(CostantiDB.TRACCE+".servizio=?");
			}
			if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo().getVersioneServizio())){
				sqlQueryObject.addWhereCondition(CostantiDB.TRACCE+".versione_servizio=?");
			}
			if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo().getAzione())){
				sqlQueryObject.addWhereCondition(CostantiDB.TRACCE+".azione=?");
			}
			if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo().getProfiloCollaborazioneProtocollo())){
				sqlQueryObject.addWhereCondition(CostantiDB.TRACCE+".profilo_collaborazione=?");
			}
			if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo().getProfiloCollaborazioneEngine())){
				sqlQueryObject.addWhereCondition(CostantiDB.TRACCE+".profilo_collaborazione_meta=?");
			}
		}		
		
		if(DriverTracciamentoUtilities.isDefined(filtro.getServizioApplicativoFruitore())){
			sqlQueryObject.addWhereCondition("sa_fruitore=?");
		}
		if(DriverTracciamentoUtilities.isDefined(filtro.getServizioApplicativoErogatore())){
			sqlQueryObject.addWhereCondition("sa_erogatore=?");
		}
	
		if(DriverTracciamentoUtilities.isDefined(filtro.getIdCorrelazioneApplicativa()) && DriverTracciamentoUtilities.isDefined(filtro.getIdCorrelazioneApplicativaRisposta())){
			sqlQueryObject.addWhereCondition((!filtro.isIdCorrelazioneApplicativaOrMatch()),
						CostantiDB.TRACCE+".correlazione_applicativa=?",
						CostantiDB.TRACCE+".correlazione_risposta=?");
		}
		else if(DriverTracciamentoUtilities.isDefined(filtro.getIdCorrelazioneApplicativa())){
			sqlQueryObject.addWhereCondition(CostantiDB.TRACCE+".correlazione_applicativa=?");
		}
		else if(DriverTracciamentoUtilities.isDefined(filtro.getIdCorrelazioneApplicativaRisposta())){
			sqlQueryObject.addWhereCondition(CostantiDB.TRACCE+".correlazione_risposta=?");
		}
		
		
		if(DriverTracciamentoUtilities.isDefined(filtro.getProtocollo())){
			sqlQueryObject.addWhereCondition("protocollo=?");
		}
		
		if(filtro.getPropertiesNames()!=null && filtro.getPropertiesNames().length>0){
			String [] names = filtro.getPropertiesNames();
			for (int i = 0; i < names.length; i++) {
				sqlQueryObject.addWhereCondition(names[i]+"=?");	
			}
		}
		
		if(filtro.getFiltroSoggetti()!=null && filtro.sizeFiltroSoggetti()>0){
			List<IDSoggetto> filtroSoggetti = filtro.getFiltroSoggetti();
			StringBuffer query = new StringBuffer();
			for(int k=0; k<filtroSoggetti.size(); k++){
				if(k>0)
					query.append(" OR ");
				query.append("( ");
				query.append("("+CostantiDB.TRACCE+".tipo_mittente = ? AND "+CostantiDB.TRACCE+".mittente = ?)");
				query.append(" OR ");
				query.append("("+CostantiDB.TRACCE+".tipo_destinatario = ? AND "+CostantiDB.TRACCE+".destinatario = ?)");
				query.append(" )");
			}
			sqlQueryObject.addWhereCondition(query.toString());
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
		
		if(DriverTracciamentoUtilities.isDefined(filtro.getMinDate())){
			if(pstmt!=null)
				pstmt.setTimestamp(startIndex++, new Timestamp(filtro.getMinDate().getTime()));
			if(query!=null)
				query.replaceFirst("\\?","'"+dateformat.format(filtro.getMinDate())+"'");
		}
		if(DriverTracciamentoUtilities.isDefined(filtro.getMaxDate())){
			if(pstmt!=null)
				pstmt.setTimestamp(startIndex++, new Timestamp(filtro.getMaxDate().getTime()));
			if(query!=null)
				query.replaceFirst("\\?", "'"+dateformat.format(filtro.getMaxDate())+"'");
		}
	
		
		if(DriverTracciamentoUtilities.isDefined(filtro.getTipoTraccia())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filtro.getTipoTraccia().getTipo());
			if(query!=null)
				query.replaceFirst("\\?", "'"+filtro.getTipoTraccia().getTipo()+"'");
		}		
		if(DriverTracciamentoUtilities.isDefined(filtro.getTipoPdD())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filtro.getTipoPdD().getTipo());
			if(query!=null)
				query.replaceFirst("\\?", "'"+filtro.getTipoPdD().getTipo()+"'");
		}
		if(DriverTracciamentoUtilities.isDefined(filtro.getDominio())){
			if(DriverTracciamentoUtilities.isDefined(filtro.getDominio().getCodicePorta())){
				if(pstmt!=null)
					pstmt.setString(startIndex++, filtro.getDominio().getCodicePorta());
				if(query!=null)
					query.replaceFirst("\\?", "'"+filtro.getDominio().getCodicePorta()+"'");
			}
			if(DriverTracciamentoUtilities.isDefined(filtro.getDominio().getTipo())){
				if(pstmt!=null)
					pstmt.setString(startIndex++, filtro.getDominio().getTipo());
				if(query!=null)
					query.replaceFirst("\\?", "'"+filtro.getDominio().getTipo()+"'");
			}
			if(DriverTracciamentoUtilities.isDefined(filtro.getDominio().getNome())){
				if(pstmt!=null)
					pstmt.setString(startIndex++, filtro.getDominio().getNome());
				if(query!=null)
					query.replaceFirst("\\?", "'"+filtro.getDominio().getNome()+"'");
			}
		}
		
		
		if(DriverTracciamentoUtilities.isDefined(filtro.getIdBusta())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filtro.getIdBusta());
			if(query!=null)
				query.replaceFirst("\\?", "'"+filtro.getIdBusta()+"'");
		}
		if(DriverTracciamentoUtilities.isDefined(filtro.getRiferimentoMessaggio())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filtro.getRiferimentoMessaggio());
			if(query!=null)
				query.replaceFirst("\\?", "'"+filtro.getRiferimentoMessaggio()+"'");
		}
		if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo())){
			if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo().getMittente())){
				if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo().getMittente().getTipo())){
					if(pstmt!=null)
						pstmt.setString(startIndex++, filtro.getInformazioniProtocollo().getMittente().getTipo());
					if(query!=null)
						query.replaceFirst("\\?", "'"+filtro.getInformazioniProtocollo().getMittente().getTipo()+"'");
				}
				if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo().getMittente().getNome())){
					if(pstmt!=null)
						pstmt.setString(startIndex++, filtro.getInformazioniProtocollo().getMittente().getNome());
					if(query!=null)
						query.replaceFirst("\\?", "'"+filtro.getInformazioniProtocollo().getMittente().getNome()+"'");
				}
				if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo().getMittente().getCodicePorta())){
					if(pstmt!=null)
						pstmt.setString(startIndex++, filtro.getInformazioniProtocollo().getMittente().getCodicePorta());
					if(query!=null)
						query.replaceFirst("\\?", "'"+filtro.getInformazioniProtocollo().getMittente().getCodicePorta()+"'");
				}
			}
			if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo().getDestinatario())){
				if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo().getDestinatario().getTipo())){
					if(pstmt!=null)
						pstmt.setString(startIndex++, filtro.getInformazioniProtocollo().getDestinatario().getTipo());
					if(query!=null)
						query.replaceFirst("\\?", "'"+filtro.getInformazioniProtocollo().getDestinatario().getTipo()+"'");
				}
				if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo().getDestinatario().getNome())){
					if(pstmt!=null)
						pstmt.setString(startIndex++, filtro.getInformazioniProtocollo().getDestinatario().getNome());
					if(query!=null)
						query.replaceFirst("\\?", "'"+filtro.getInformazioniProtocollo().getDestinatario().getNome()+"'");
				}
				if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo().getDestinatario().getCodicePorta())){
					if(pstmt!=null)
						pstmt.setString(startIndex++, filtro.getInformazioniProtocollo().getDestinatario().getCodicePorta());
					if(query!=null)
						query.replaceFirst("\\?", "'"+filtro.getInformazioniProtocollo().getDestinatario().getCodicePorta()+"'");
				}
			}
			if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo().getTipoServizio())){
				if(pstmt!=null)
					pstmt.setString(startIndex++, filtro.getInformazioniProtocollo().getTipoServizio());
				if(query!=null)
					query.replaceFirst("\\?", "'"+filtro.getInformazioniProtocollo().getTipoServizio()+"'");
			}
			if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo().getServizio())){
				if(pstmt!=null)
					pstmt.setString(startIndex++, filtro.getInformazioniProtocollo().getServizio());
				if(query!=null)
					query.replaceFirst("\\?", "'"+filtro.getInformazioniProtocollo().getServizio()+"'");
			}
			if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo().getVersioneServizio())){
				if(pstmt!=null)
					pstmt.setInt(startIndex++, filtro.getInformazioniProtocollo().getVersioneServizio());
				if(query!=null)
					query.replaceFirst("\\?", "'"+filtro.getInformazioniProtocollo().getVersioneServizio()+"'");
			}
			if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo().getAzione())){
				if(pstmt!=null)
					pstmt.setString(startIndex++, filtro.getInformazioniProtocollo().getAzione());
				if(query!=null)
					query.replaceFirst("\\?", "'"+filtro.getInformazioniProtocollo().getAzione()+"'");
			}
			if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo().getProfiloCollaborazioneProtocollo())){
				if(pstmt!=null)
					pstmt.setString(startIndex++, filtro.getInformazioniProtocollo().getProfiloCollaborazioneProtocollo());
				if(query!=null)
					query.replaceFirst("\\?", "'"+filtro.getInformazioniProtocollo().getProfiloCollaborazioneProtocollo()+"'");
			}
			if(DriverTracciamentoUtilities.isDefined(filtro.getInformazioniProtocollo().getProfiloCollaborazioneEngine())){
				if(pstmt!=null)
					pstmt.setString(startIndex++, filtro.getInformazioniProtocollo().getProfiloCollaborazioneEngine().getEngineValue());
				if(query!=null)
					query.replaceFirst("\\?", "'"+filtro.getInformazioniProtocollo().getProfiloCollaborazioneEngine().getEngineValue()+"'");
			}
		}	
		
		
		if(DriverTracciamentoUtilities.isDefined(filtro.getServizioApplicativoFruitore())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filtro.getServizioApplicativoFruitore());
			if(query!=null)
				query.replaceFirst("\\?", "'"+filtro.getServizioApplicativoFruitore()+"'");
		}
		if(DriverTracciamentoUtilities.isDefined(filtro.getServizioApplicativoErogatore())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filtro.getServizioApplicativoErogatore());
			if(query!=null)
				query.replaceFirst("\\?", "'"+filtro.getServizioApplicativoErogatore()+"'");
		}
		
		
		if(DriverTracciamentoUtilities.isDefined(filtro.getIdCorrelazioneApplicativa()) && DriverTracciamentoUtilities.isDefined(filtro.getIdCorrelazioneApplicativaRisposta())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filtro.getIdCorrelazioneApplicativa());
			if(query!=null)
				query.replaceFirst("\\?", "'"+filtro.getIdCorrelazioneApplicativa()+"'");
			if(pstmt!=null)
				pstmt.setString(startIndex++, filtro.getIdCorrelazioneApplicativaRisposta());
			if(query!=null)
				query.replaceFirst("\\?", "'"+filtro.getIdCorrelazioneApplicativaRisposta()+"'");
			
		}
		else if(DriverTracciamentoUtilities.isDefined(filtro.getIdCorrelazioneApplicativa())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filtro.getIdCorrelazioneApplicativa());
			if(query!=null)
				query.replaceFirst("\\?", "'"+filtro.getIdCorrelazioneApplicativa()+"'");
		}
		else if(DriverTracciamentoUtilities.isDefined(filtro.getIdCorrelazioneApplicativaRisposta())){
			if(pstmt!=null)
				pstmt.setString(startIndex++, filtro.getIdCorrelazioneApplicativaRisposta());
			if(query!=null)
				query.replaceFirst("\\?", "'"+filtro.getIdCorrelazioneApplicativaRisposta()+"'");
		}
		
	
		if(DriverTracciamentoUtilities.isDefined(filtro.getProtocollo())){
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
			Logger log,Long id,Vector<String> properties,
			ProtocolliRegistrati protocolli) throws DriverTracciamentoException, DriverTracciamentoNotFoundException, SQLQueryObjectException{
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		PreparedStatement pstmtLista = null;
		ResultSet rsLista = null;
		Traccia tr = null;
		
		try{
			
			ISQLQueryObject sqlQueryObject = DriverTracciamentoUtilities.getSQLQueryObject(tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.TRACCE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id=?");
			
			pstmt = con.prepareStatement(sqlQueryObject.toString());
			pstmt.setLong(1, id);
			rs = pstmt.executeQuery();
			if(rs.next()){
				
				tr = new Traccia();
				tr.setGdo(rs.getTimestamp("gdo"));
				IDSoggetto idSoggetto = new IDSoggetto();
				idSoggetto.setTipo(rs.getString("pdd_tipo_soggetto"));
				idSoggetto.setNome(rs.getString("pdd_nome_soggetto"));
				idSoggetto.setCodicePorta(rs.getString("pdd_codice"));
				tr.setTipoPdD(TipoPdD.toTipoPdD(rs.getString("pdd_ruolo")));
				tr.setIdSoggetto(idSoggetto);
				String t = rs.getString("tipo_messaggio");
				if(t!=null){
					tr.setTipoMessaggio(TipoTraccia.toTipoTraccia(t));
				}
				EsitoElaborazioneMessaggioTracciato esitoTraccia = new EsitoElaborazioneMessaggioTracciato();
				esitoTraccia.setEsito(EsitoElaborazioneMessaggioTracciatura.valueOf(rs.getString("esito_elaborazione")));
				esitoTraccia.setDettaglio(rs.getString("dettaglio_esito_elaborazione"));
				tr.setEsitoElaborazioneMessaggioTracciato(esitoTraccia);
				tr.setCorrelazioneApplicativa(rs.getString("correlazione_applicativa"));
				tr.setCorrelazioneApplicativaRisposta(rs.getString("correlazione_risposta"));
				tr.setLocation(rs.getString("location"));
				tr.setProtocollo(rs.getString("protocollo"));
				if(properties!=null){
					for (int i = 0; i < properties.size(); i++) {
						String key = properties.get(i);
						tr.addProperty(key, rs.getString(key));
					}
				}
				Busta busta = new Busta(tr.getProtocollo());
				
				if(rs.getString("tipo_mittente")!=null && ("".equals(rs.getString("tipo_mittente"))==false) )
					busta.setTipoMittente(rs.getString("tipo_mittente"));
				if(rs.getString("mittente")!=null && ("".equals(rs.getString("mittente"))==false) )
					busta.setMittente(rs.getString("mittente"));
				if(rs.getString("idporta_mittente")!=null && ("".equals(rs.getString("idporta_mittente"))==false) )
					busta.setIdentificativoPortaMittente(rs.getString("idporta_mittente"));
				if(rs.getString("indirizzo_mittente")!=null && ("".equals(rs.getString("indirizzo_mittente"))==false)){
					busta.setIndirizzoMittente(rs.getString("indirizzo_mittente"));
				}
				
				if(rs.getString("tipo_destinatario")!=null && ("".equals(rs.getString("tipo_destinatario"))==false))
					busta.setTipoDestinatario(rs.getString("tipo_destinatario"));
				if(rs.getString("destinatario")!=null && ("".equals(rs.getString("destinatario"))==false))
					busta.setDestinatario(rs.getString("destinatario"));
				if(rs.getString("idporta_destinatario")!=null && ("".equals(rs.getString("idporta_destinatario"))==false) )
					busta.setIdentificativoPortaDestinatario(rs.getString("idporta_destinatario"));
				if(rs.getString("indirizzo_destinatario")!=null && ("".equals(rs.getString("indirizzo_destinatario"))==false))
					busta.setIndirizzoDestinatario(rs.getString("indirizzo_destinatario"));
				
				if(rs.getString("sa_fruitore")!=null && ("".equals(rs.getString("sa_fruitore"))==false))
					busta.setServizioApplicativoFruitore(rs.getString("sa_fruitore"));
				if(rs.getString("sa_erogatore")!=null && ("".equals(rs.getString("sa_erogatore"))==false))
					busta.setServizioApplicativoErogatore(rs.getString("sa_erogatore"));
				
				if(rs.getString("profilo_collaborazione")!=null && ("".equals(rs.getString("profilo_collaborazione"))==false))
					busta.setProfiloDiCollaborazioneValue(rs.getString("profilo_collaborazione"));
				if(rs.getString("profilo_collaborazione_meta")!=null && ("".equals(rs.getString("profilo_collaborazione_meta"))==false))
					busta.setProfiloDiCollaborazione(ProfiloDiCollaborazione.toProfiloDiCollaborazione(rs.getString("profilo_collaborazione_meta")));
				if(rs.getString("servizio_correlato")!=null && ("".equals(rs.getString("servizio_correlato"))==false))
					busta.setServizioCorrelato(rs.getString("servizio_correlato"));
				if(rs.getString("tipo_servizio_correlato")!=null && ("".equals(rs.getString("tipo_servizio_correlato"))==false))
					busta.setTipoServizioCorrelato(rs.getString("tipo_servizio_correlato"));
				
				if(rs.getString("collaborazione")!=null && ("".equals(rs.getString("collaborazione"))==false))
					busta.setCollaborazione(rs.getString("collaborazione"));
				
				if(rs.getString("versione_servizio")!=null && ("".equals(rs.getString("versione_servizio"))==false))
					busta.setVersioneServizio(rs.getString("versione_servizio"));
				if(rs.getString("servizio")!=null && ("".equals(rs.getString("servizio"))==false))
					busta.setServizio(rs.getString("servizio"));
				if(rs.getString("tipo_servizio")!=null && ("".equals(rs.getString("tipo_servizio"))==false))
					busta.setTipoServizio(rs.getString("tipo_servizio"));
				if(rs.getString("azione")!=null && ("".equals(rs.getString("azione"))==false))
					busta.setAzione(rs.getString("azione"));
				
				if(rs.getString("id_messaggio")!=null && ("".equals(rs.getString("id_messaggio"))==false))
					busta.setID(rs.getString("id_messaggio"));
				
				if(rs.getTimestamp("ora_registrazione")!=null )
					busta.setOraRegistrazione(rs.getTimestamp("ora_registrazione"));
				if(rs.getString("tipo_ora_reg")!=null && ("".equals(rs.getString("tipo_ora_reg"))==false))
					busta.setTipoOraRegistrazioneValue(rs.getString("tipo_ora_reg"));
				if(rs.getString("tipo_ora_reg_meta")!=null && ("".equals(rs.getString("tipo_ora_reg_meta"))==false))
					busta.setTipoOraRegistrazione(TipoOraRegistrazione.toTipoOraRegistrazione(rs.getString("tipo_ora_reg_meta")),busta.getTipoOraRegistrazioneValue());
				
				if(rs.getString("rif_messaggio")!=null && ("".equals(rs.getString("rif_messaggio"))==false))
					busta.setRiferimentoMessaggio(rs.getString("rif_messaggio"));
				
				if(rs.getTimestamp("scadenza")!=null )
					busta.setScadenza(rs.getTimestamp("scadenza"));
				
				if(rs.getString("inoltro")!=null && ("".equals(rs.getString("inoltro"))==false))
					busta.setInoltroValue(rs.getString("inoltro"));
				if(rs.getString("inoltro_meta")!=null && ("".equals(rs.getString("inoltro_meta"))==false))
					busta.setInoltro(Inoltro.toInoltro(rs.getString("inoltro_meta")),busta.getInoltroValue());
				if(rs.getInt("conferma_ricezione")==1)
					busta.setConfermaRicezione(true);
				else
					busta.setConfermaRicezione(false);
				
				if(rs.getLong("sequenza")>0){
					busta.setSequenza(rs.getLong("sequenza"));
				}
				
				busta.setDigest(rs.getString("digest"));
				
				String headerProtocollo = rs.getString("soap_element");
				if(headerProtocollo!=null){
					tr.setBustaAsString(headerProtocollo);
				}
				
				long idTraccia = rs.getLong("id");
				tr.setId(idTraccia);
				tr.addProperty(DriverTracciamento.IDTRACCIA, idTraccia+"");
				
				//System.out.println("ID TRACCIA ["+idTraccia+"]");
				
				IProtocolFactory protocolFactory = protocolli.getProtocolFactory(tr.getProtocollo());
				ITraduttore traduttoreProtocollo = protocolFactory.createTraduttore();
				
				// Lista trasmissioni
				sqlQueryObject = DriverTracciamentoUtilities.getSQLQueryObject(tipoDatabase);
				sqlQueryObject.addFromTable(CostantiDB.TRACCE_TRASMISSIONI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("idtraccia=?");
				sqlQueryObject.setANDLogicOperator(true);
				pstmtLista = con.prepareStatement(sqlQueryObject.toString());
				pstmtLista.setLong(1, idTraccia);
				rsLista = pstmtLista.executeQuery();
				while(rsLista.next()){
					Trasmissione trtr = new Trasmissione();
					
					if(rsLista.getString("origine")!=null && ("".equals(rsLista.getString("origine"))==false))
						trtr.setOrigine(rsLista.getString("origine"));
					if(rsLista.getString("tipo_origine")!=null && ("".equals(rsLista.getString("tipo_origine"))==false))
						trtr.setTipoOrigine(rsLista.getString("tipo_origine"));
					if(rsLista.getString("indirizzo_origine")!=null && ("".equals(rsLista.getString("indirizzo_origine"))==false))
						trtr.setIndirizzoOrigine(rsLista.getString("indirizzo_origine"));
					if(rsLista.getString("idporta_origine")!=null && ("".equals(rsLista.getString("idporta_origine"))==false)){
						trtr.setIdentificativoPortaOrigine(rsLista.getString("idporta_origine"));
					}
					
					if(rsLista.getString("destinazione")!=null && ("".equals(rsLista.getString("destinazione"))==false))
						trtr.setDestinazione(rsLista.getString("destinazione"));
					if(rsLista.getString("tipo_destinazione")!=null && ("".equals(rsLista.getString("tipo_destinazione"))==false))
						trtr.setTipoDestinazione(rsLista.getString("tipo_destinazione"));
					if(rsLista.getString("indirizzo_destinazione")!=null && ("".equals(rsLista.getString("indirizzo_destinazione"))==false))
						trtr.setIndirizzoDestinazione(rsLista.getString("indirizzo_destinazione"));
					if(rsLista.getString("idporta_destinazione")!=null && ("".equals(rsLista.getString("idporta_destinazione"))==false)){
						trtr.setIdentificativoPortaDestinazione(rsLista.getString("idporta_destinazione"));
					}
					
					if(rsLista.getTimestamp("ora_registrazione")!=null )
						trtr.setOraRegistrazione(rsLista.getTimestamp("ora_registrazione"));
					if(rsLista.getString("tipo_ora_reg")!=null && ("".equals(rsLista.getString("tipo_ora_reg"))==false))
						trtr.setTempoValue(rsLista.getString("tipo_ora_reg"));
					if(rsLista.getString("tipo_ora_reg_meta")!=null && ("".equals(rsLista.getString("tipo_ora_reg_meta"))==false))
						trtr.setTempo(TipoOraRegistrazione.toTipoOraRegistrazione(rsLista.getString("tipo_ora_reg_meta")));
					busta.addTrasmissione(trtr);
				}
				rsLista.close();
				pstmtLista.close();
				
				// Lista eccezioni
				sqlQueryObject = DriverTracciamentoUtilities.getSQLQueryObject(tipoDatabase);
				sqlQueryObject.addFromTable(CostantiDB.TRACCE_ECCEZIONI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("idtraccia=?");
				sqlQueryObject.setANDLogicOperator(true);
				pstmtLista = con.prepareStatement(sqlQueryObject.toString());
				pstmtLista.setLong(1, idTraccia);
				rsLista = pstmtLista.executeQuery();
				
				while(rsLista.next()){
					
					Eccezione trec = Eccezione.newEccezione();
					
					String contestoCodifica = rsLista.getString("contesto_codifica");
					String contestoCodificaMeta = rsLista.getString("contesto_codifica_meta");
					String codiceEccezione = rsLista.getString("codice_eccezione");
					int codiceEccezioneMeta = rsLista.getInt("codice_eccezione_meta");
					int subCodiceEccezioneMeta = rsLista.getInt("subcodice_eccezione_meta");
					String rilevanza = rsLista.getString("rilevanza");
					String rilevanzaMeta = rsLista.getString("rilevanza_meta");
					String posizione = rsLista.getString("posizione");
						
					
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
				sqlQueryObject = DriverTracciamentoUtilities.getSQLQueryObject(tipoDatabase);
				sqlQueryObject.addFromTable(CostantiDB.TRACCE_RISCONTRI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("idtraccia=?");
				sqlQueryObject.setANDLogicOperator(true);
				pstmtLista = con.prepareStatement(sqlQueryObject.toString());
				pstmtLista.setLong(1, idTraccia);
				rsLista = pstmtLista.executeQuery();
				while(rsLista.next()){
					Riscontro trris = new  Riscontro();
					if(rsLista.getString("riscontro")!=null && ("".equals(rsLista.getString("riscontro"))==false))
						trris.setID(rsLista.getString("riscontro"));
					if(rsLista.getTimestamp("ora_registrazione")!=null )
						trris.setOraRegistrazione(rsLista.getTimestamp("ora_registrazione"));
					if(rsLista.getString("tipo_ora_reg")!=null && ("".equals(rsLista.getString("tipo_ora_reg"))==false))
						trris.setTipoOraRegistrazioneValue(rsLista.getString("tipo_ora_reg"));
					if(rsLista.getString("tipo_ora_reg_meta")!=null && ("".equals(rsLista.getString("tipo_ora_reg_meta"))==false))
						trris.setTipoOraRegistrazione(TipoOraRegistrazione.toTipoOraRegistrazione(rsLista.getString("tipo_ora_reg_meta")));
					busta.addRiscontro(trris);
				}
				rsLista.close();
				pstmtLista.close();
				
				
				// Lista Allegati
				sqlQueryObject = DriverTracciamentoUtilities.getSQLQueryObject(tipoDatabase);
				sqlQueryObject.addFromTable(CostantiDB.TRACCE_ALLEGATI);				
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("idtraccia=?");
				sqlQueryObject.setANDLogicOperator(true);
				pstmtLista = con.prepareStatement(sqlQueryObject.toString());
				pstmtLista.setLong(1, idTraccia);
				rsLista = pstmtLista.executeQuery();
				while(rsLista.next()){
					Allegato allegato = new Allegato();
					allegato.setContentId(rsLista.getString("content_id"));
					allegato.setContentLocation(rsLista.getString("content_location"));
					allegato.setContentType(rsLista.getString("content_type"));
					allegato.setDigest(rsLista.getString("digest"));
					tr.addAllegato(allegato);
				}
				rsLista.close();
				pstmtLista.close();
				
				
				// Protocol extra info
				sqlQueryObject = DriverTracciamentoUtilities.getSQLQueryObject(tipoDatabase);
				sqlQueryObject.addFromTable(CostantiDB.TRACCE_EXT_INFO);				
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("idtraccia=?");
				sqlQueryObject.setANDLogicOperator(true);
				pstmtLista = con.prepareStatement(sqlQueryObject.toString());
				pstmtLista.setLong(1, idTraccia);
				rsLista = pstmtLista.executeQuery();
				while(rsLista.next()){
					busta.addProperty(rsLista.getString("name"), rsLista.getString("value"));
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
	protected static boolean isDefined(TipoTraccia v){
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
