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
package org.openspcoop2.monitor.engine.statistic;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.plugins.utils.FilterUtils;
import org.openspcoop2.core.statistiche.StatisticaContenuti;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.core.transazioni.dao.ITransazioneServiceSearch;
import org.openspcoop2.generic_project.beans.AliasTableComplexField;
import org.openspcoop2.generic_project.beans.ComplexField;
import org.openspcoop2.generic_project.beans.ConstantField;
import org.openspcoop2.generic_project.beans.Function;
import org.openspcoop2.generic_project.beans.FunctionField;
import org.openspcoop2.generic_project.beans.IAliasTableField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.UnionExpression;
import org.openspcoop2.generic_project.beans.UnixTimestampIntervalField;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.monitor.engine.constants.Costanti;
import org.openspcoop2.monitor.engine.exceptions.EngineException;
import org.openspcoop2.monitor.engine.transaction.TransactionContentUtils;
import org.openspcoop2.monitor.engine.utils.ContentFormatter;
import org.openspcoop2.monitor.sdk.statistic.StatisticFilterName;
import org.openspcoop2.monitor.sdk.statistic.StatisticResourceFilter;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
 * StatisticsUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticsUtils {

	
	public static Long readLongValue(Map<String, Object> row, String alias){
		Object object = row.get(alias);
		if(object!=null){
			if(object instanceof org.apache.commons.lang.ObjectUtils.Null){
				return null;
			}
			else{
				return (Long) object;
			}
		}
		return null;
	}
	
	public static void setExpressionNotNullDate(ITransazioneServiceSearch transazioneSearchDAO, IExpression expr, Date data, Date dateNext, TipoPdD tipoPdD, StatisticBean stat,
			ISQLFieldConverter fieldConverter) throws Exception{
		StatisticsUtils.setExpressionEngine(transazioneSearchDAO, expr, data, dateNext, tipoPdD, 
				true, false, 
				stat, fieldConverter, 
				false, 
				null,null);
	}
	public static void setExpressionNullDate(ITransazioneServiceSearch transazioneSearchDAO, IExpression expr, Date data, Date dateNext, TipoPdD tipoPdD, StatisticBean stat,
			ISQLFieldConverter fieldConverter) throws Exception{
		StatisticsUtils.setExpressionEngine(transazioneSearchDAO, expr, data, dateNext, tipoPdD, 
				false, true, 
				stat, fieldConverter, 
				false, 
				null,null);
	}
	public static void setExpression(ITransazioneServiceSearch transazioneSearchDAO, IExpression expr, Date data, Date dateNext, TipoPdD tipoPdD, boolean setNotNullDate, StatisticBean stat,
			ISQLFieldConverter fieldConverter) throws Exception{
		StatisticsUtils.setExpressionEngine(transazioneSearchDAO, expr, data, dateNext, tipoPdD, 
				setNotNullDate, false, 
				stat, fieldConverter, 
				false, 
				null,null);
	}
	public static void setExpressionByStato(ITransazioneServiceSearch transazioneSearchDAO, IExpression expr, Date data, Date dateNext, TipoPdD tipoPdD, boolean setNotNullDate, StatisticBean stat,
			ISQLFieldConverter fieldConverter) throws Exception{
		StatisticsUtils.setExpressionEngine(transazioneSearchDAO, expr, data, dateNext, tipoPdD, 
				setNotNullDate, false, 
				stat, fieldConverter, 
				true, 
				null,null);
	}
	public static void setExpressionStatsPersonalizzate(ITransazioneServiceSearch transazioneSearchDAO, IExpression expr, Date data, Date dateNext, TipoPdD tipoPdD, boolean setNotNullDate, StatisticBean stat,
			ISQLFieldConverter fieldConverter,
			List<AliasFilter> aliases,
			String idRisorsa,
			StatisticResourceFilter ... risorseFiltri) throws Exception{
		StatisticsUtils.setExpressionEngine(transazioneSearchDAO, expr, data, dateNext, tipoPdD, 
				setNotNullDate, false,
				stat, fieldConverter,
				false,
				aliases,idRisorsa, 
				risorseFiltri);
	}
	private static void setExpressionEngine(ITransazioneServiceSearch transazioneSearchDAO, IExpression expr, Date data, Date dateNext, TipoPdD tipoPdD, 
			boolean setNotNullDate, boolean setNullDate, 
			StatisticBean stat, ISQLFieldConverter fieldConverter,
			boolean groupByStato,
			List<AliasFilter> aliases, String idRisorsa,
			StatisticResourceFilter ... risorseFiltri) throws Exception{
		
		expr.and();
		
		// indice
		//expr.between(Transazione.model().DATA_INGRESSO_RICHIESTA, data , dateNext); between is inclusive!
		expr.between(Transazione.model().DATA_INGRESSO_RICHIESTA, DateUtils.incredementDate1MsIf999(data) , dateNext);
		expr.isNotNull(Transazione.model().DATA_INGRESSO_RICHIESTA);
		
		if(TipoPdD.DELEGATA.equals(tipoPdD)){
			expr.equals(Transazione.model().PDD_RUOLO, PddRuolo.DELEGATA);
		}else{
			expr.equals(Transazione.model().PDD_RUOLO, PddRuolo.APPLICATIVA);
		}
		expr.isNotNull(Transazione.model().PDD_RUOLO);
		
		expr.isNotNull(Transazione.model().ESITO_CONTESTO);
		
		if(stat!=null){
			// condizioni di group by messe anche in equals
//			expr.equals(Transazione.model().PDD_CODICE,stat.getIdPorta());
//			expr.equals(Transazione.model().TIPO_SOGGETTO_FRUITORE, stat.getMittente()!=null ? stat.getMittente().getTipo() : null);
//			expr.equals(Transazione.model().NOME_SOGGETTO_FRUITORE, stat.getMittente()!=null ? stat.getMittente().getNome() : null);
//			expr.equals(Transazione.model().TIPO_SOGGETTO_EROGATORE, stat.getDestinatario()!=null ? stat.getDestinatario().getTipo() : null);
//			expr.equals(Transazione.model().NOME_SOGGETTO_EROGATORE, stat.getDestinatario()!=null ? stat.getDestinatario().getNome() : null);
//			expr.equals(Transazione.model().TIPO_SERVIZIO, stat.getTipoServizio());
//			expr.equals(Transazione.model().NOME_SERVIZIO, stat.getServizio());
//			expr.equals(Transazione.model().AZIONE, stat.getAzione());

			// Gestisco i possibili valori null con '-'.
			// Sono state prese le informazioni anche con null poichè senno non venivano contate nelle statistiche le transazioni che non possedevano info sui servizi. (es porta delegata non trovata)
			String pddCodice = stat.getIdPorta();
			StatisticsUtils.setCondition(expr, pddCodice, Transazione.model().PDD_CODICE);
			String tipoMittente = stat.getMittente()!=null ? stat.getMittente().getTipo() : null;
			StatisticsUtils.setCondition(expr, tipoMittente, Transazione.model().TIPO_SOGGETTO_FRUITORE);
			String nomeMittente = stat.getMittente()!=null ? stat.getMittente().getNome() : null;
			StatisticsUtils.setCondition(expr, nomeMittente, Transazione.model().NOME_SOGGETTO_FRUITORE);
			String tipoDestinatario = stat.getDestinatario()!=null ? stat.getDestinatario().getTipo() : null;
			StatisticsUtils.setCondition(expr, tipoDestinatario, Transazione.model().TIPO_SOGGETTO_EROGATORE);
			String nomeDestinatario = stat.getDestinatario()!=null ? stat.getDestinatario().getNome() : null;
			StatisticsUtils.setCondition(expr, nomeDestinatario, Transazione.model().NOME_SOGGETTO_EROGATORE);
			String tipoServizio = stat.getTipoServizio();
			StatisticsUtils.setCondition(expr, tipoServizio, Transazione.model().TIPO_SERVIZIO);
			String nomeServizio = stat.getServizio();
			StatisticsUtils.setCondition(expr, nomeServizio, Transazione.model().NOME_SERVIZIO);
			Integer versioneServizio = stat.getVersioneServizio();
			StatisticsUtils.setCondition(expr, versioneServizio, Transazione.model().VERSIONE_SERVIZIO);
			String azione = stat.getAzione();
			StatisticsUtils.setCondition(expr, azione, Transazione.model().AZIONE);
			
			
//			if(TipoPdD.DELEGATA.equals(tipoPdD)){
			// Nella consultazione delle statistiche si utilizzano sempre gli applicativi fruitori come informazione fornita.
			if(Costanti.SERVIZIO_APPLICATIVO_ANONIMO.equals(stat.getServizioApplicativo()) || stat.getServizioApplicativo()==null || Costanti.INFORMAZIONE_NON_DISPONIBILE.equals(stat.getServizioApplicativo())){
				expr.isNull(Transazione.model().SERVIZIO_APPLICATIVO_FRUITORE);
			}
			else{
				expr.equals(Transazione.model().SERVIZIO_APPLICATIVO_FRUITORE,stat.getServizioApplicativo());
			}
//			}else{
//				if(Costanti.SERVIZIO_APPLICATIVO_ANONIMO.equals(stat.getServizioApplicativo()) || stat.getServizioApplicativo()==null){
//					expr.isNull(Transazione.model().SERVIZIO_APPLICATIVO_EROGATORE);
//				}
//				else{
//					expr.equals(Transazione.model().SERVIZIO_APPLICATIVO_EROGATORE,stat.getServizioApplicativo());
//				}
//			}
			
			String trasportoMittente = stat.getTrasportoMittente();
			StatisticsUtils.setCondition(expr, trasportoMittente, Transazione.model().TRASPORTO_MITTENTE);
			
			String tokenIssuer = stat.getTokenIssuer();
			StatisticsUtils.setCondition(expr, tokenIssuer, Transazione.model().TOKEN_ISSUER);
			String tokenClientId = stat.getTokenClientId();
			StatisticsUtils.setCondition(expr, tokenClientId, Transazione.model().TOKEN_CLIENT_ID);
			String tokenSubject = stat.getTokenSubject();
			StatisticsUtils.setCondition(expr, tokenSubject, Transazione.model().TOKEN_SUBJECT);
			String tokenUsername = stat.getTokenUsername();
			StatisticsUtils.setCondition(expr, tokenUsername, Transazione.model().TOKEN_USERNAME);
			String tokenMail = stat.getTokenMail();
			StatisticsUtils.setCondition(expr, tokenMail, Transazione.model().TOKEN_MAIL);
			
			String clientAddress = stat.getClientAddress();
			StatisticsUtils.setCondition(expr, clientAddress, Transazione.model().CLIENT_ADDRESS);
			
			String gruppo = stat.getGruppo();
			StatisticsUtils.setCondition(expr, gruppo, Transazione.model().GRUPPI);
			
			String uriApi = stat.getApi();
			StatisticsUtils.setCondition(expr, uriApi, Transazione.model().URI_API);
			
			String clusterId = stat.getClusterId();
			StatisticsUtils.setCondition(expr, clusterId, Transazione.model().CLUSTER_ID);
			
			expr.equals(Transazione.model().ESITO, stat.getEsito()!=null ? stat.getEsito() : -1);
			
			String esitoContesto = stat.getEsitoContesto();
			StatisticsUtils.setCondition(expr, esitoContesto, Transazione.model().ESITO_CONTESTO);
		}
		else{
			// Gestisco i possibili valori null con '-'.
			// Sono state prese le informazioni anche con null poichè senno non venivano contate nelle statistiche le transazioni che non possedevano info sui servizi. (es porta delegata non trovata)
//			expr.isNotNull(Transazione.model().PDD_CODICE);
//			expr.isNotNull(Transazione.model().TIPO_SOGGETTO_FRUITORE);
//			expr.isNotNull(Transazione.model().NOME_SOGGETTO_FRUITORE);
//			expr.isNotNull(Transazione.model().TIPO_SOGGETTO_EROGATORE);
//			expr.isNotNull(Transazione.model().NOME_SOGGETTO_EROGATORE);
//			expr.isNotNull(Transazione.model().TIPO_SERVIZIO);
//			expr.isNotNull(Transazione.model().NOME_SERVIZIO);
//			expr.isNotNull(Transazione.model().VERSIONE_SERVIZIO);
		}
		
		if(setNotNullDate){
			expr.isNotNull(Transazione.model().DATA_USCITA_RICHIESTA);
			expr.isNotNull(Transazione.model().DATA_INGRESSO_RISPOSTA);
			expr.isNotNull(Transazione.model().DATA_USCITA_RISPOSTA);
		}
		else if(setNullDate) {
			IExpression exprNullDate = transazioneSearchDAO.newExpression();
			exprNullDate.isNotNull(Transazione.model().DATA_USCITA_RICHIESTA);
			exprNullDate.isNotNull(Transazione.model().DATA_INGRESSO_RISPOSTA);
			exprNullDate.isNotNull(Transazione.model().DATA_USCITA_RISPOSTA);
			exprNullDate.and();
			expr.not(exprNullDate);
		}

		if(idRisorsa!=null){
			expr.equals(Transazione.model().DUMP_MESSAGGIO.CONTENUTO.NOME,idRisorsa);
		}
		if(risorseFiltri!=null && risorseFiltri.length>0){
			for (int i = 0; i < risorseFiltri.length; i++) {
				AliasFilter af = new AliasFilter();
				IAliasTableField atf = new AliasTableComplexField((ComplexField)Transazione.model().DUMP_MESSAGGIO.CONTENUTO.NOME, FilterUtils.getNextAliasStatisticsTable());
				//System.out.println("FILTRO["+i+"]= ("+af.getAliasTable()+") "+risorseFiltri[i]);
				af.setNomeFiltro(atf);
				af.setStatisticFilterName(risorseFiltri[i].getStatisticFilterName());
				aliases.add(af);
				expr.equals(atf,risorseFiltri[i].getResourceID());	
			}
		}
		
		// ** GROUP BY **
		expr.addGroupBy(Transazione.model().PDD_RUOLO);
		expr.addGroupBy(Transazione.model().PDD_CODICE);
		expr.addGroupBy(Transazione.model().TIPO_SOGGETTO_FRUITORE);
		expr.addGroupBy(Transazione.model().NOME_SOGGETTO_FRUITORE);
		expr.addGroupBy(Transazione.model().TIPO_SOGGETTO_EROGATORE);
		expr.addGroupBy(Transazione.model().NOME_SOGGETTO_EROGATORE);
		expr.addGroupBy(Transazione.model().TIPO_SERVIZIO);
		expr.addGroupBy(Transazione.model().NOME_SERVIZIO);
		expr.addGroupBy(Transazione.model().VERSIONE_SERVIZIO);
		expr.addGroupBy(Transazione.model().AZIONE);
		// Nella consultazione delle statistiche si utilizzano sempre gli applicativi fruitori come informazione fornita.
//		if(TipoPdD.DELEGATA.equals(tipoPdD)){
		expr.addGroupBy(Transazione.model().SERVIZIO_APPLICATIVO_FRUITORE);
//		}else{
//			expr.addGroupBy(Transazione.model().SERVIZIO_APPLICATIVO_EROGATORE);
//		}
		expr.addGroupBy(Transazione.model().TRASPORTO_MITTENTE);
		expr.addGroupBy(Transazione.model().TOKEN_ISSUER);
		expr.addGroupBy(Transazione.model().TOKEN_CLIENT_ID);
		expr.addGroupBy(Transazione.model().TOKEN_SUBJECT);
		expr.addGroupBy(Transazione.model().TOKEN_USERNAME);
		expr.addGroupBy(Transazione.model().TOKEN_MAIL);
		expr.addGroupBy(Transazione.model().CLIENT_ADDRESS);
		expr.addGroupBy(Transazione.model().GRUPPI);
		expr.addGroupBy(Transazione.model().URI_API);
		expr.addGroupBy(Transazione.model().CLUSTER_ID);
		expr.addGroupBy(Transazione.model().ESITO);
		expr.addGroupBy(Transazione.model().ESITO_CONTESTO);
		if(groupByStato){
			expr.addGroupBy(Transazione.model().STATO);
		}
		if(idRisorsa!=null){
			expr.addGroupBy(Transazione.model().DUMP_MESSAGGIO.CONTENUTO.NOME);
			expr.addGroupBy(Transazione.model().DUMP_MESSAGGIO.CONTENUTO.VALORE);
		}
		if(aliases!=null && aliases.size()>0){
			for (AliasFilter aliasFilter : aliases) {
				IAliasTableField afName = aliasFilter.getNomeFiltro(); 
				expr.addGroupBy(afName);
				
				String tableAlias = afName.getAliasTable();
				IAliasTableField afValue = new AliasTableComplexField((ComplexField)Transazione.model().DUMP_MESSAGGIO.CONTENUTO.VALORE, tableAlias);
				aliasFilter.setValoreFiltro(afValue);
				
				expr.addGroupBy(afValue);
			}
		}
	}
	
	private static void setCondition(IExpression expr, String value,IField field) throws ExpressionNotImplementedException, ExpressionException{
		if(value==null || "".equals(value) || Costanti.INFORMAZIONE_NON_DISPONIBILE.equals(value)){
			expr.isNull(field);
		}
		else{
			expr.equals(field,value);
		}
	}
	
	private static void setCondition(IExpression expr, Integer value,IField field) throws ExpressionNotImplementedException, ExpressionException{
		if(value==null || value.intValue() == Costanti.INFORMAZIONE_VERSIONE_NON_DISPONIBILE.intValue()){
			expr.equals(field,Costanti.INFORMAZIONE_VERSIONE_NON_DISPONIBILE);
		}
		else{
			expr.equals(field,value);
		}
	}
	
	public static void addSelectUnionField(UnionExpression expr, ISQLFieldConverter fieldConverter) throws Exception {
			addSelectUnionField(expr, fieldConverter,
					false,
					null, null);
	}
	public static void addSelectUnionField(UnionExpression expr, ISQLFieldConverter fieldConverter,
			boolean groupByStato,
			List<AliasFilter> aliases, String idRisorsa) throws Exception {
		expr.addSelectField(Transazione.model().PDD_RUOLO, fieldConverter.toColumn(Transazione.model().PDD_RUOLO, false));
		expr.addSelectField(Transazione.model().PDD_CODICE, fieldConverter.toColumn(Transazione.model().PDD_CODICE, false));
		expr.addSelectField(Transazione.model().TIPO_SOGGETTO_FRUITORE, fieldConverter.toColumn(Transazione.model().TIPO_SOGGETTO_FRUITORE, false));
		expr.addSelectField(Transazione.model().NOME_SOGGETTO_FRUITORE, fieldConverter.toColumn(Transazione.model().NOME_SOGGETTO_FRUITORE, false));
		expr.addSelectField(Transazione.model().TIPO_SOGGETTO_EROGATORE, fieldConverter.toColumn(Transazione.model().TIPO_SOGGETTO_EROGATORE, false));
		expr.addSelectField(Transazione.model().NOME_SOGGETTO_EROGATORE, fieldConverter.toColumn(Transazione.model().NOME_SOGGETTO_EROGATORE, false));
		expr.addSelectField(Transazione.model().TIPO_SERVIZIO, fieldConverter.toColumn(Transazione.model().TIPO_SERVIZIO, false));
		expr.addSelectField(Transazione.model().NOME_SERVIZIO, fieldConverter.toColumn(Transazione.model().NOME_SERVIZIO, false));
		expr.addSelectField(Transazione.model().VERSIONE_SERVIZIO, fieldConverter.toColumn(Transazione.model().VERSIONE_SERVIZIO, false));
		expr.addSelectField(Transazione.model().AZIONE, fieldConverter.toColumn(Transazione.model().AZIONE, false));
		// Nella consultazione delle statistiche si utilizzano sempre gli applicativi fruitori come informazione fornita.
//		if(TipoPdD.DELEGATA.equals(tipoPdD)){
		expr.addSelectField(Transazione.model().SERVIZIO_APPLICATIVO_FRUITORE, fieldConverter.toColumn(Transazione.model().SERVIZIO_APPLICATIVO_FRUITORE, false));
//		}else{
//			expr.addSelectField(Transazione.model().SERVIZIO_APPLICATIVO_EROGATORE, fieldConverter.toColumn(Transazione.model().SERVIZIO_APPLICATIVO_EROGATORE, false));
//		}
		expr.addSelectField(Transazione.model().TRASPORTO_MITTENTE, fieldConverter.toColumn(Transazione.model().TRASPORTO_MITTENTE, false));
		expr.addSelectField(Transazione.model().TOKEN_ISSUER, fieldConverter.toColumn(Transazione.model().TOKEN_ISSUER, false));
		expr.addSelectField(Transazione.model().TOKEN_CLIENT_ID, fieldConverter.toColumn(Transazione.model().TOKEN_CLIENT_ID, false));
		expr.addSelectField(Transazione.model().TOKEN_SUBJECT, fieldConverter.toColumn(Transazione.model().TOKEN_SUBJECT, false));
		expr.addSelectField(Transazione.model().TOKEN_USERNAME, fieldConverter.toColumn(Transazione.model().TOKEN_USERNAME, false));
		expr.addSelectField(Transazione.model().TOKEN_MAIL, fieldConverter.toColumn(Transazione.model().TOKEN_MAIL, false));
		expr.addSelectField(Transazione.model().CLIENT_ADDRESS, fieldConverter.toColumn(Transazione.model().CLIENT_ADDRESS, false));
		expr.addSelectField(Transazione.model().GRUPPI, fieldConverter.toColumn(Transazione.model().GRUPPI, false));
		expr.addSelectField(Transazione.model().URI_API, fieldConverter.toColumn(Transazione.model().URI_API, false));
		expr.addSelectField(Transazione.model().CLUSTER_ID, fieldConverter.toColumn(Transazione.model().CLUSTER_ID, false));
		expr.addSelectField(Transazione.model().ESITO, fieldConverter.toColumn(Transazione.model().ESITO, false));
		expr.addSelectField(Transazione.model().ESITO_CONTESTO, fieldConverter.toColumn(Transazione.model().ESITO_CONTESTO, false));
		if(groupByStato){
			expr.addSelectField(Transazione.model().STATO, fieldConverter.toColumn(Transazione.model().STATO, false));
		}
		if(idRisorsa!=null){
			expr.addSelectField(Transazione.model().DUMP_MESSAGGIO.CONTENUTO.NOME, fieldConverter.toColumn(Transazione.model().DUMP_MESSAGGIO.CONTENUTO.NOME, false));
			expr.addSelectField(Transazione.model().DUMP_MESSAGGIO.CONTENUTO.VALORE, fieldConverter.toColumn(Transazione.model().DUMP_MESSAGGIO.CONTENUTO.VALORE, false));
		}
		if(aliases!=null && aliases.size()>0){
			for (AliasFilter aliasFilter : aliases) {
				IAliasTableField afName = aliasFilter.getNomeFiltro(); 
				expr.addSelectField(afName, afName.getFieldName());
				
				String tableAlias = afName.getAliasTable();
				IAliasTableField afValue = new AliasTableComplexField((ComplexField)Transazione.model().DUMP_MESSAGGIO.CONTENUTO.VALORE, tableAlias);
				aliasFilter.setValoreFiltro(afValue);
				
				expr.addSelectField(afValue, afValue.getFieldName());
			}
		}
	}
	
	public static void addSelectFieldCountTransaction(List<FunctionField> selectList) throws ExpressionException{
		
		// Numero Transazioni
		FunctionField fCount = new FunctionField(
				//Transazione.model().ID_TRANSAZIONE,
				Transazione.model().DATA_INGRESSO_RICHIESTA, // piu' efficente
				//Function.COUNT_DISTINCT,
				Function.COUNT,
				"richieste");
		selectList.add(fCount);
		
	}
	
	public static void addSelectFieldSizeTransaction(TipoPdD tipoPdD,List<FunctionField> selectList) throws ExpressionException{
		
		// Dimensione Transazioni
		FunctionField fSum1 = null, fSum2= null, fSum3= null, fSum4= null;
		
		fSum1 = new FunctionField(Transazione.model().RICHIESTA_INGRESSO_BYTES, Function.SUM, "message_bytes_in_richiesta");
		fSum2 = new FunctionField(Transazione.model().RICHIESTA_USCITA_BYTES, Function.SUM, "message_bytes_out_richiesta");
		fSum3 = new FunctionField(Transazione.model().RISPOSTA_INGRESSO_BYTES, Function.SUM, "message_bytes_in_risposta");
		fSum4 = new FunctionField(Transazione.model().RISPOSTA_USCITA_BYTES, Function.SUM, "message_bytes_out_risposta");

		selectList.add(fSum1);
		selectList.add(fSum2);
		selectList.add(fSum3);
		selectList.add(fSum4);
	}
	
	public static void addSelectFunctionFieldLatencyTransaction(TipoPdD tipoPdD,ISQLFieldConverter fieldConverter, 
			List<FunctionField> selectFunctionList) throws ExpressionException, SQLQueryObjectException{
		_addSelectFieldLatencyTransaction(tipoPdD, fieldConverter, selectFunctionList, null);
	}
	public static void addSelectConstantFieldLatencyTransaction(TipoPdD tipoPdD,ISQLFieldConverter fieldConverter, 
			List<ConstantField> selectConstantList) throws ExpressionException, SQLQueryObjectException{
		_addSelectFieldLatencyTransaction(tipoPdD, fieldConverter, null, selectConstantList);
	}
	private static void _addSelectFieldLatencyTransaction(TipoPdD tipoPdD,ISQLFieldConverter fieldConverter, 
			List<FunctionField> selectFunctionList,
			List<ConstantField> selectConstantList) throws ExpressionException, SQLQueryObjectException{
		
		if(selectFunctionList!=null) {
		
			// Latenza Totale
			UnixTimestampIntervalField latenzaTotale = new UnixTimestampIntervalField("unix_latenza_totale", fieldConverter, true, 
					Transazione.model().DATA_USCITA_RISPOSTA, Transazione.model().DATA_INGRESSO_RICHIESTA);
			FunctionField fLatenzaTotaleAvg = new FunctionField(latenzaTotale, Function.AVG, "latenza_totale");
			selectFunctionList.add(fLatenzaTotaleAvg);
			
			// Latenza Servizio
			UnixTimestampIntervalField latenzaServizio = new UnixTimestampIntervalField("unix_latenza_servizio", fieldConverter, true, 
					Transazione.model().DATA_INGRESSO_RISPOSTA, Transazione.model().DATA_USCITA_RICHIESTA);
			FunctionField fLatenzaServizioAvg = new FunctionField(latenzaServizio, Function.AVG, "latenza_servizio");
			selectFunctionList.add(fLatenzaServizioAvg);
			
			// Latenza Gateway Richiesta
			UnixTimestampIntervalField latenzaPortaRichiesta = new UnixTimestampIntervalField("unix_latenza_richiesta", fieldConverter, true, 
					Transazione.model().DATA_USCITA_RICHIESTA, Transazione.model().DATA_INGRESSO_RICHIESTA);
			FunctionField fLatenzaPortaRichiestaAvg = new FunctionField(latenzaPortaRichiesta, Function.AVG, "latenza_porta_richiesta");
			selectFunctionList.add(fLatenzaPortaRichiestaAvg);
			
			// Latenza Gateway Risposta
			UnixTimestampIntervalField latenzaPortaRisposta = new UnixTimestampIntervalField("unix_latenza_risposta", fieldConverter, true, 
					Transazione.model().DATA_USCITA_RISPOSTA, Transazione.model().DATA_INGRESSO_RISPOSTA);
			FunctionField fLatenzaPortaRispostaAvg = new FunctionField(latenzaPortaRisposta, Function.AVG, "latenza_porta_risposta");
			selectFunctionList.add(fLatenzaPortaRispostaAvg);
			
		}
		else {
			
			// Latenza Totale
			ConstantField latenzaTotale = new ConstantField("latenza_totale", Costanti.INFORMAZIONE_LATENZA_NON_DISPONIBILE, Long.class);
			selectConstantList.add(latenzaTotale);
			
			// Latenza Servizio
			ConstantField latenzaServizio = new ConstantField("latenza_servizio", Costanti.INFORMAZIONE_LATENZA_NON_DISPONIBILE, Long.class);
			selectConstantList.add(latenzaServizio);
			
			// Latenza Gateway Richiesta
			ConstantField latenzaPortaRichiesta = new ConstantField("latenza_porta_richiesta", Costanti.INFORMAZIONE_LATENZA_NON_DISPONIBILE, Long.class);
			selectConstantList.add(latenzaPortaRichiesta);
			
			// Latenza Gateway Risposta
			ConstantField latenzaPortaRisposta = new ConstantField("latenza_porta_risposta", Costanti.INFORMAZIONE_LATENZA_NON_DISPONIBILE, Long.class);
			selectConstantList.add(latenzaPortaRisposta);
		}
		
	}
	
	public static StatisticBean readStatisticBean(StatisticBean stat,Map<String, Object> row, ISQLFieldConverter fieldConverter, boolean useFieldConverter) throws ExpressionException{
		
		stat.setIdPorta(StatisticsUtils.getValueFromMap(Transazione.model().PDD_CODICE,row,fieldConverter,useFieldConverter));
		String TipoPortaS = StatisticsUtils.getValueFromMap(Transazione.model().PDD_RUOLO,row,fieldConverter,useFieldConverter);
		TipoPdD tipo = TipoPdD.toTipoPdD(TipoPortaS);
		stat.setTipoPorta(tipo);
		
		/*
		String sa= null;
		Object saObject = null;
		// Nella consultazione delle statistiche si utilizzano sempre gli applicativi fruitori come informazione fornita.
//		if(tipo.equals(TipoPdD.DELEGATA)){
		saObject = row.get(Transazione.model().SERVIZIO_APPLICATIVO_FRUITORE.getFieldName());
//		}else{
//			saObject = row.get(Transazione.model().SERVIZIO_APPLICATIVO_EROGATORE.getFieldName());
//		}
		if(saObject!=null){
			if(saObject instanceof org.apache.commons.lang.ObjectUtils.Null){
				sa = null;
			}
			else{
				sa = (String) saObject;
			}
		}
		stat.setServizioApplicativo(sa != null ? sa : Costanti.SERVIZIO_APPLICATIVO_ANONIMO);
		*/
		// Registrare nelle statistiche il valore 'Anonimo' porta ad avere informazioni non corrette nella distribuzione per applicativo sulle erogazioni.
		// L'errore avviene poichè è richiesto il group by sul nomeApplicativo, tipoSoggettoProprietario, nomeSoggettoProprietario
		// poichè un applicativo viene identificato univocamente se si considera sia il nome dell'applicativo che il soggetto proprietario.
		// Il group by sulle fruizioni, se si usa l'informazione anonima, non porta problemi perchè l'entry anonima sarà 1 sempre, essendo il soggetto fruitore uno solo (Soggetto locale impostato).
		// Il group by sulle erogazioni produrrà invece più entry anonime se si hanno più soggetto che la invocano senza un applicativo specifico.
		stat.setServizioApplicativo(StatisticsUtils.getValueFromMap(Transazione.model().SERVIZIO_APPLICATIVO_FRUITORE,row,fieldConverter,useFieldConverter));
						
		stat.setTrasportoMittente(StatisticsUtils.getValueFromMap(Transazione.model().TRASPORTO_MITTENTE,row,fieldConverter,useFieldConverter));
		
		stat.setTokenIssuer(StatisticsUtils.getValueFromMap(Transazione.model().TOKEN_ISSUER,row,fieldConverter,useFieldConverter));
		stat.setTokenClientId(StatisticsUtils.getValueFromMap(Transazione.model().TOKEN_CLIENT_ID,row,fieldConverter,useFieldConverter));
		stat.setTokenSubject(StatisticsUtils.getValueFromMap(Transazione.model().TOKEN_SUBJECT,row,fieldConverter,useFieldConverter));
		stat.setTokenUsername(StatisticsUtils.getValueFromMap(Transazione.model().TOKEN_USERNAME,row,fieldConverter,useFieldConverter));
		stat.setTokenMail(StatisticsUtils.getValueFromMap(Transazione.model().TOKEN_MAIL,row,fieldConverter,useFieldConverter));
		
		stat.setClientAddress(StatisticsUtils.getValueFromMap(Transazione.model().CLIENT_ADDRESS,row,fieldConverter,useFieldConverter));
		
		stat.setGruppo(StatisticsUtils.getValueFromMap(Transazione.model().GRUPPI,row,fieldConverter,useFieldConverter));
		
		stat.setApi(StatisticsUtils.getValueFromMap(Transazione.model().URI_API,row,fieldConverter,useFieldConverter));
		
		stat.setClusterId(StatisticsUtils.getValueFromMap(Transazione.model().CLUSTER_ID,row,fieldConverter,useFieldConverter));
		
//		stat.setMittente(new IDSoggetto((String)row.get(Transazione.model().TIPO_SOGGETTO_FRUITORE.getFieldName()), (String)row.get(Transazione.model().NOME_SOGGETTO_FRUITORE .getFieldName())));
//		stat.setDestinatario(new IDSoggetto((String)row.get(Transazione.model().TIPO_SOGGETTO_EROGATORE.getFieldName()),(String)row.get(Transazione.model().NOME_SOGGETTO_EROGATORE.getFieldName())));
//		stat.setTipoServizio((String)row.get(Transazione.model().TIPO_SERVIZIO.getFieldName()));
//		stat.setServizio((String)row.get(Transazione.model().NOME_SERVIZIO.getFieldName()));
//		Object azObject = row.get(Transazione.model().AZIONE.getFieldName());
//		String az = null;
//		if(azObject!=null && !(azObject instanceof org.apache.commons.lang.ObjectUtils.Null)){ 
//			az = (String) azObject;
//		}
//		stat.setAzione(az != null ? az : " ");
		
		// Gestisco i possibili valori null con '-'.
		// Sono state prese le informazioni anche con null poichè senno non venivano contate nelle statistiche le transazioni che non possedevano info sui servizi. (es porta delegata non trovata)
		stat.setMittente(new IDSoggetto(StatisticsUtils.getValueFromMap(Transazione.model().TIPO_SOGGETTO_FRUITORE,row,fieldConverter,useFieldConverter), 
										StatisticsUtils.getValueFromMap(Transazione.model().NOME_SOGGETTO_FRUITORE,row,fieldConverter,useFieldConverter)));
		
		stat.setDestinatario(new IDSoggetto(StatisticsUtils.getValueFromMap(Transazione.model().TIPO_SOGGETTO_EROGATORE,row,fieldConverter,useFieldConverter), 
											StatisticsUtils.getValueFromMap(Transazione.model().NOME_SOGGETTO_EROGATORE,row,fieldConverter,useFieldConverter)));
		
		stat.setTipoServizio(StatisticsUtils.getValueFromMap(Transazione.model().TIPO_SERVIZIO,row,fieldConverter,useFieldConverter));
		stat.setServizio(StatisticsUtils.getValueFromMap(Transazione.model().NOME_SERVIZIO,row,fieldConverter,useFieldConverter));
		stat.setVersioneServizio(StatisticsUtils.getIntegerValueFromMap(Transazione.model().VERSIONE_SERVIZIO,row,false,fieldConverter,useFieldConverter));
		
		stat.setAzione(StatisticsUtils.getValueFromMap(Transazione.model().AZIONE,row,fieldConverter,useFieldConverter));
		
		stat.setEsito(StatisticsUtils.getIntegerValueFromMap(Transazione.model().ESITO,row,true,fieldConverter,useFieldConverter));
		
		stat.setEsitoContesto(StatisticsUtils.getValueFromMap(Transazione.model().ESITO_CONTESTO,row,fieldConverter,useFieldConverter));
		
		return stat;
	}
	private static String getValueFromMap(IField field, Map<String, Object> row, ISQLFieldConverter fieldConverter, boolean useFieldConverter) throws ExpressionException{
		String nomeKeyMappa = null;
		if(useFieldConverter) {
			nomeKeyMappa = fieldConverter.toColumn(field, false);
		}
		else {
			nomeKeyMappa = field.getFieldName();
		}
		Object tmpObject = row.get(nomeKeyMappa);
		String tmp = null;
		if(tmpObject!=null && !(tmpObject instanceof org.apache.commons.lang.ObjectUtils.Null)){ 
			tmp = (String) tmpObject;
		}
		if(tmp!=null && !"".equals(tmp)){
			return tmp;
		}
		else{
			return Costanti.INFORMAZIONE_NON_DISPONIBILE;
		}
	}
	private static Integer getIntegerValueFromMap(IField field, Map<String, Object> row, boolean acceptZeroValue, ISQLFieldConverter fieldConverter, boolean useFieldConverter) throws ExpressionException{
		String nomeKeyMappa = null;
		if(useFieldConverter) {
			nomeKeyMappa = fieldConverter.toColumn(field, false);
		}
		else {
			nomeKeyMappa = field.getFieldName();
		}
		Object tmpObject = row.get(nomeKeyMappa);
		Integer tmp = null;
		if(tmpObject!=null && !(tmpObject instanceof org.apache.commons.lang.ObjectUtils.Null)){ 
			tmp = (Integer) tmpObject;
		}
		if(tmp!=null && tmp.intValue()>0){
			return tmp;
		}
		else if(tmp!=null && tmp.intValue()==0 && acceptZeroValue) {
			return tmp;
		}
		else{
			return Costanti.INFORMAZIONE_VERSIONE_NON_DISPONIBILE;
		}
	}
	
	public static void updateStatisticBeanCountTransactionInfo(StatisticBean stat,Map<String, Object> row){
		
		Long tmp = StatisticsUtils.readLongValue(row, "richieste");
		if(tmp!=null){
			stat.setRichieste(tmp);
		}

	}
	
	public static void updateStatisticBeanSizeTransactionInfo(StatisticBean stat,Map<String, Object> row){
				
		long messageBytesInRequest = 0;
		Long tmp = StatisticsUtils.readLongValue(row, "message_bytes_in_richiesta");
		if(tmp!=null){
			messageBytesInRequest = tmp.longValue();
		}
		long messageBytesOutRequest = 0;
		tmp = StatisticsUtils.readLongValue(row, "message_bytes_out_richiesta");
		if(tmp!=null){
			messageBytesOutRequest = tmp.longValue();
		}
		long messageBytesInResponse = 0;
		tmp = StatisticsUtils.readLongValue(row, "message_bytes_in_risposta");
		if(tmp!=null){
			messageBytesInResponse = tmp.longValue();
		}
		long messageBytesOutResponse = 0;
		tmp = StatisticsUtils.readLongValue(row, "message_bytes_out_risposta");
		if(tmp!=null){
			messageBytesOutResponse = tmp.longValue();
		}
		
		switch (stat.getTipoPorta()) {
		case DELEGATA:
			stat.setBytesBandaInterna(messageBytesInRequest+messageBytesOutResponse);
			stat.setBytesBandaEsterna(messageBytesOutRequest+messageBytesInResponse);
			break;
		default:
			stat.setBytesBandaInterna(messageBytesOutRequest+messageBytesInResponse);
			stat.setBytesBandaEsterna(messageBytesInRequest+messageBytesOutResponse);
			break;
		}
		stat.setBytesBandaTotale(stat.getBytesBandaInterna()+stat.getBytesBandaEsterna());
		
	}
	
	public static void updateStatisticsBeanLatencyTransactionInfo(StatisticBean stat,Map<String, Object> row){
		// Latenza Totale
		long latenzaTotaleValue = -1;
		Long tmp = StatisticsUtils.readLongValue(row, "latenza_totale");
		if(tmp!=null){
			latenzaTotaleValue = tmp.longValue();
		}
		stat.setLatenzaTotale(latenzaTotaleValue);
		
		// Latenza Servizio
		long latenzaServizioValue = -1;
		tmp = StatisticsUtils.readLongValue(row, "latenza_servizio");
		if(tmp!=null){
			latenzaServizioValue = tmp.longValue();
		}
		stat.setLatenzaServizio(latenzaServizioValue);
		
		// Latenza Gateway
		long latenzaPortaRichiestaValue = -1;
		tmp = StatisticsUtils.readLongValue(row, "latenza_porta_richiesta");
		if(tmp!=null){
			latenzaPortaRichiestaValue = tmp.longValue();
		}
		long latenzaPortaRispostaValue = -1;
		tmp = StatisticsUtils.readLongValue(row, "latenza_porta_risposta");
		if(tmp!=null){
			latenzaPortaRispostaValue = tmp.longValue();
		}
		long latenzaPortaValue = -1;
		if(latenzaPortaRichiestaValue>=0){
			latenzaPortaValue = latenzaPortaRichiestaValue;
			if(latenzaPortaRispostaValue>=0){
				latenzaPortaValue = latenzaPortaValue + latenzaPortaRispostaValue;
			}
		}
		else{
			if(latenzaPortaRispostaValue>=0){
				latenzaPortaValue = latenzaPortaRispostaValue;
			}
		}
		stat.setLatenzaPorta(latenzaPortaValue);
	}
	
	public static void fillStatisticsContenutiByStato(String idStatisticaPersonalizzata,StatisticaContenuti statisticaContenuti,Map<String, Object> row) throws EngineException{
		String aliasValore = Transazione.model().STATO.getFieldName();
		Object oValore = row.get(aliasValore);
		String valore = null;
		if(oValore!=null && oValore instanceof String){
			String s = (String) oValore;
			if(!"".equals(s)){
				valore = s;
			}
			else{
				valore = org.openspcoop2.monitor.engine.constants.Costanti.TRANSAZIONE_SENZA_STATO;
			}
		}else{
			valore = org.openspcoop2.monitor.engine.constants.Costanti.TRANSAZIONE_SENZA_STATO;
		}
		StatisticsUtils.setRisorsaValore(statisticaContenuti, idStatisticaPersonalizzata, valore);
	}
	
	public static void fillStatisticsContenuti(String idStatisticaPersonalizzata,StatisticaContenuti statisticaContenuti,Map<String, Object> row,
			List<AliasFilter> aliases,RisorsaSemplice risorsaSemplice) throws EngineException{
		
		String risorsaNome = null;
		if(risorsaSemplice.getIdStatistica()!=null){
			risorsaNome = idStatisticaPersonalizzata+"-"+risorsaSemplice.getIdStatistica();
		}
		else{
			risorsaNome = idStatisticaPersonalizzata;
		}
		
		String aliasValore = Transazione.model().DUMP_MESSAGGIO.getBaseField().getFieldName()+"."+
				Transazione.model().DUMP_MESSAGGIO.CONTENUTO.getBaseField().getFieldName()+"."+
				Transazione.model().DUMP_MESSAGGIO.CONTENUTO.VALORE.getFieldName();
		
		StatisticsUtils.setRisorsaValore(statisticaContenuti, risorsaNome, (String)row.get(aliasValore));
		
		
//		java.util.Iterator<String> itS = row.keySet().iterator();
//		while (itS.hasNext()) {
//			String key = (String) itS.next();
//			System.out.println("ESEMPIO KEY["+key+"]");
//		}
		
		if(aliases.size()>0){
			for (int i = 0; i < aliases.size(); i++) {
				AliasFilter af = aliases.get(i);
				IAliasTableField afName = af.getNomeFiltro();
				IAliasTableField afValore = af.getValoreFiltro();
				
				String aliasFiltroNome = afName.getAliasTable()+"."+
						Transazione.model().DUMP_MESSAGGIO.CONTENUTO.NOME.getFieldName();
				String aliasFiltroValore = afValore.getAliasTable()+"."+
						Transazione.model().DUMP_MESSAGGIO.CONTENUTO.VALORE.getFieldName();
				
				StatisticsUtils.setFiltro(statisticaContenuti, 
						af.getStatisticFilterName(), 
						(String)row.get(aliasFiltroNome), 
						(String)row.get(aliasFiltroValore));
				
			}
		}
		
	}
	
	public static void fillStatisticsContenuti(String idStatistica,StatisticaContenuti statisticaContenuti,RisorsaAggregata risorsaAggregata) throws EngineException{
		
		StatisticsUtils.setRisorsaValore(statisticaContenuti, idStatistica, risorsaAggregata.getValoreRisorsaAggregata());
		if(risorsaAggregata.getFiltri()!=null && risorsaAggregata.getFiltri().size()>0){
			for (int i = 0; i < risorsaAggregata.getFiltri().size(); i++) {
				String nome = risorsaAggregata.getFiltri().get(i).getResourceID();
				String valore = ContentFormatter.toString(risorsaAggregata.getFiltri().get(i).getValue());
				
				StatisticsUtils.setFiltro(statisticaContenuti, 
						risorsaAggregata.getFiltri().get(i).getStatisticFilterName(), 
						nome, 
						valore);
				
			}
		}
	}
	
	private static void setRisorsaValore(StatisticaContenuti statisticaContenuti,String risorsaNome,String valore) throws EngineException{
		if(valore.length()>TransactionContentUtils.SOGLIA_VALUE_TOO_LONG){
			throw new EngineException("Valore fornito per il contenuto statistico ["+risorsaNome
					+"] è troppo grande (>"+TransactionContentUtils.SOGLIA_VALUE_TOO_LONG+") per essere utilizzato come informazione statistica. Valore fornito: "+valore);
		}
		if(TransactionContentUtils.KEY_COMPRESSED.equals(valore)){
			throw new EngineException("Una risorsa compressa è stata fornita per il contenuto statistico ["+risorsaNome
					+"]; questo tipo di risorsa non è utilizzabile come informazione statistica");
		}
		if(TransactionContentUtils.KEY_VALUE_TOO_LONG.equals(valore)){
			throw new EngineException("Una risorsa con valore > "+TransactionContentUtils.SOGLIA_VALUE_TOO_LONG+
					" caratteri è stata fornita per il contenuto statistico ["+risorsaNome
					+"]; questo tipo di risorsa non è utilizzabile come informazione statistica");
		}
		statisticaContenuti.setRisorsaNome(risorsaNome);
		statisticaContenuti.setRisorsaValore(valore);
	}
	
	private static void setFiltro(StatisticaContenuti statisticaContenuti,StatisticFilterName filterName,
			String nome,String valore) throws EngineException{
		
		if(nome.length()>255){
			throw new EngineException("Nome assegnato a "+filterName.name()+" ("+nome
					+") è troppo grande (>255)");
		}
		if(valore.length()>TransactionContentUtils.SOGLIA_VALUE_TOO_LONG){
			throw new EngineException("Valore fornito per "+filterName.name()+" ["+nome
					+"] è troppo grande (>"+TransactionContentUtils.SOGLIA_VALUE_TOO_LONG+") per essere utilizzato come filtro. Valore fornito: "+valore);
		}
		if(TransactionContentUtils.KEY_COMPRESSED.equals(valore)){
			throw new EngineException("Una risorsa compressa è stata fornita per "+filterName.name()+" ["+nome
					+"]; questo tipo di risorsa non è utilizzabile come valore per un filtro");
		}
		if(TransactionContentUtils.KEY_VALUE_TOO_LONG.equals(valore)){
			throw new EngineException("Una risorsa con valore > "+TransactionContentUtils.SOGLIA_VALUE_TOO_LONG+" caratteri è stata fornita per "+
					filterName.name()+" ["+nome
					+"]; questo tipo di risorsa non è utilizzabile come valore per un filtro");
		}
		
		switch (filterName) {
		case FILTER_1:
			statisticaContenuti.setFiltroNome1(nome);
			statisticaContenuti.setFiltroValore1(valore);
			break;
		case FILTER_2:
			statisticaContenuti.setFiltroNome2(nome);
			statisticaContenuti.setFiltroValore2(valore);
			break;
		case FILTER_3:
			statisticaContenuti.setFiltroNome3(nome);
			statisticaContenuti.setFiltroValore3(valore);
			break;
		case FILTER_4:
			statisticaContenuti.setFiltroNome4(nome);
			statisticaContenuti.setFiltroValore4(valore);
			break;
		case FILTER_5:
			statisticaContenuti.setFiltroNome5(nome);
			statisticaContenuti.setFiltroValore5(valore);
			break;
		case FILTER_6:
			statisticaContenuti.setFiltroNome6(nome);
			statisticaContenuti.setFiltroValore6(valore);
			break;
		case FILTER_7:
			statisticaContenuti.setFiltroNome7(nome);
			statisticaContenuti.setFiltroValore7(valore);
			break;
		case FILTER_8:
			statisticaContenuti.setFiltroNome8(nome);
			statisticaContenuti.setFiltroValore8(valore);
			break;
		case FILTER_9:
			statisticaContenuti.setFiltroNome9(nome);
			statisticaContenuti.setFiltroValore9(valore);
			break;
		case FILTER_10:
			statisticaContenuti.setFiltroNome10(nome);
			statisticaContenuti.setFiltroValore10(valore);
			break;
		}
	}
	
	public static String buildKey(StatisticaContenuti statisticaContenuti){
		StringBuilder bf = new StringBuilder();
		
		bf.append(statisticaContenuti.getRisorsaNome());
		bf.append("=");
		bf.append(statisticaContenuti.getRisorsaValore());
		
		if(statisticaContenuti.getFiltroNome1()!=null){
			bf.append(statisticaContenuti.getFiltroNome1());
			bf.append("=");
			bf.append(statisticaContenuti.getFiltroValore1());
		}
		
		if(statisticaContenuti.getFiltroNome2()!=null){
			bf.append(statisticaContenuti.getFiltroNome2());
			bf.append("=");
			bf.append(statisticaContenuti.getFiltroValore2());
		}
		
		if(statisticaContenuti.getFiltroNome3()!=null){
			bf.append(statisticaContenuti.getFiltroNome3());
			bf.append("=");
			bf.append(statisticaContenuti.getFiltroValore3());
		}
		
		if(statisticaContenuti.getFiltroNome4()!=null){
			bf.append(statisticaContenuti.getFiltroNome4());
			bf.append("=");
			bf.append(statisticaContenuti.getFiltroValore4());
		}
		
		if(statisticaContenuti.getFiltroNome5()!=null){
			bf.append(statisticaContenuti.getFiltroNome5());
			bf.append("=");
			bf.append(statisticaContenuti.getFiltroValore5());
		}
		
		if(statisticaContenuti.getFiltroNome6()!=null){
			bf.append(statisticaContenuti.getFiltroNome6());
			bf.append("=");
			bf.append(statisticaContenuti.getFiltroValore6());
		}
		
		if(statisticaContenuti.getFiltroNome7()!=null){
			bf.append(statisticaContenuti.getFiltroNome7());
			bf.append("=");
			bf.append(statisticaContenuti.getFiltroValore7());
		}
		
		if(statisticaContenuti.getFiltroNome8()!=null){
			bf.append(statisticaContenuti.getFiltroNome8());
			bf.append("=");
			bf.append(statisticaContenuti.getFiltroValore8());
		}
		
		if(statisticaContenuti.getFiltroNome9()!=null){
			bf.append(statisticaContenuti.getFiltroNome9());
			bf.append("=");
			bf.append(statisticaContenuti.getFiltroValore9());
		}
		
		if(statisticaContenuti.getFiltroNome10()!=null){
			bf.append(statisticaContenuti.getFiltroNome10());
			bf.append("=");
			bf.append(statisticaContenuti.getFiltroValore10());
		}
		
		return bf.toString();
		
	}
}
