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

package org.openspcoop2.protocol.basic.diagnostica;

import java.util.Vector;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnosticiConPaginazione;
import org.openspcoop2.protocol.sdk.diagnostica.InformazioniProtocollo;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
 * DriverMsgDiagnosticiUnionUtilities
 *
 * @author Stefano Corallo <corallo@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverMsgDiagnosticiUnionUtilities {

	
	
	/* *********** 3 METODI PER RACCOGLIERE le informazioni su diagnostici/correlazone con casi eccezionali **********
	
	/**
	 * crea una query che
	 * In base ai parametri di filtro ricerca nella tabella msgdiagnostici_correlazione
	 * le entry che soddisfano i filtri impostati
	 * @param filter
	 * @return
	 * @throws SQLQueryObjectException
	 */
	public static ISQLQueryObject createSQLQueryObjCorrelazione(FiltroRicercaDiagnosticiConPaginazione filter,
			boolean setLimit,
			Logger log,String tipoDatabase,Vector<String>properties) throws SQLQueryObjectException{
			
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
		
		//from
		sqlQueryObject.addFromTable(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE);
		//se presente filtro servizio_applicativo allora effettuo la join
		if(filter.getServizioApplicativo()!=null && !"".equals(filter.getServizioApplicativo())){
			sqlQueryObject.addFromTable(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE_SA);
		}
		
		//select
		sqlQueryObject.addSelectField(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE,"idmessaggio");
		sqlQueryObject.addSelectField(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE,"gdo");
		sqlQueryObject.addSelectField(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE,"porta");
		sqlQueryObject.addSelectField(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE,"delegata");
		sqlQueryObject.addSelectField(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE,"tipo_fruitore");
		sqlQueryObject.addSelectField(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE,"fruitore");
		sqlQueryObject.addSelectField(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE,"tipo_erogatore");
		sqlQueryObject.addSelectField(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE,"erogatore");
		sqlQueryObject.addSelectField(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE,"tipo_servizio");
		sqlQueryObject.addSelectField(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE,"servizio");
		sqlQueryObject.addSelectField(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE,"azione");
		sqlQueryObject.addSelectField(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE,"id");
		sqlQueryObject.addSelectField(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE,"id_correlazione_applicativa");
		sqlQueryObject.addSelectField(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE,"id_correlazione_risposta");
		if(properties!=null){
			for (int i = 0; i < properties.size(); i++) {
				String key = properties.get(i);
				sqlQueryObject.addSelectField(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE,key);
			}
		}
		sqlQueryObject.addSelectAliasField("null","pdd_codice");
		sqlQueryObject.addSelectAliasField("null","pdd_tipo_soggetto");
		sqlQueryObject.addSelectAliasField("null","pdd_nome_soggetto");
		sqlQueryObject.addSelectAliasField("null","idfunzione");
		sqlQueryObject.addSelectAliasField("null","severita");
		sqlQueryObject.addSelectAliasField("null","idmessaggio_risposta");
		sqlQueryObject.addSelectAliasField("null","messaggio");
		sqlQueryObject.addSelectAliasField("null","codice");
		
		
		
		//eseguo ricerca in base ai valori di filtro specificati
		//where

		sqlQueryObject.setANDLogicOperator(true);
		//data inizio
		if(filter.getDataInizio()!=null){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".gdo>=?");
		}
		//data fine
		if(filter.getDataFine()!=null){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".gdo<=?");
		}
		//id busta
		if(filter.getIdBustaRichiesta()!=null && !"".equals(filter.getIdBustaRichiesta())){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".idmessaggio=?");
		}
		//nome porta
		if(filter.getNomePorta()!=null && !"".equals(filter.getNomePorta())){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".porta='"+filter.getNomePorta()+"'");
		}
		//is delegata
		if(filter.isDelegata()!=null){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".delegata="+(filter.isDelegata()?1:0));
		}
		//correlazione applicativa
//		if(filter.getCorrelazioneApplicativa()!=null && !"".equals(filter.getCorrelazioneApplicativa())){
//			sqlQueryObject.addWhereLikeCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".id_correlazione_applicativa", filter.getCorrelazioneApplicativa(), true, true);
//			//sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".id_correlazione_applicativa='"+filter.getCorrelazioneApplicativa()+"'");
//		}
		if(DriverMsgDiagnosticiUtilities.isDefined(filter.getCorrelazioneApplicativa()) && DriverMsgDiagnosticiUtilities.isDefined(filter.getCorrelazioneApplicativaRisposta())){
			sqlQueryObject.addWhereCondition((!filter.isCorrelazioneApplicativaOrMatch()),
						CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".id_correlazione_applicativa='"+sqlQueryObject.escapeStringValue(filter.getCorrelazioneApplicativa())+"'",
						CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".id_correlazione_risposta='"+sqlQueryObject.escapeStringValue(filter.getCorrelazioneApplicativaRisposta())+"'");
		}
		else if(DriverMsgDiagnosticiUtilities.isDefined(filter.getCorrelazioneApplicativa())){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".id_correlazione_applicativa='"+sqlQueryObject.escapeStringValue(filter.getCorrelazioneApplicativa())+"'");
		}
		else if(DriverMsgDiagnosticiUtilities.isDefined(filter.getCorrelazioneApplicativaRisposta())){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".id_correlazione_risposta='"+sqlQueryObject.escapeStringValue(filter.getCorrelazioneApplicativaRisposta())+"'");
		}
		
		//servizio applicativo
		if(filter.getServizioApplicativo()!=null && !"".equals(filter.getServizioApplicativo())){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".id="+CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE_SA+".id_correlazione");
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE_SA+".servizio_applicativo='"+filter.getServizioApplicativo()+"'");
		}
		//Informazioni
		InformazioniProtocollo infoBusta = filter.getInformazioniProtocollo();
		if(infoBusta!=null){
			//soggetto fruitore
			if(infoBusta.getFruitore()!=null){
				if(infoBusta.getFruitore().getTipo()!=null && !"".equals(infoBusta.getFruitore().getTipo())) sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".tipo_fruitore='"+infoBusta.getFruitore().getTipo()+"'");
				if(infoBusta.getFruitore().getNome()!=null && !"".equals(infoBusta.getFruitore().getNome()))sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".fruitore='"+infoBusta.getFruitore().getNome()+"'");
			}
			//soggetto erogatore
			if(infoBusta.getErogatore()!=null){
				if(infoBusta.getErogatore().getTipo()!=null && !"".equals(infoBusta.getErogatore().getTipo())) sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".tipo_erogatore='"+infoBusta.getErogatore().getTipo()+"'");
				if(infoBusta.getErogatore().getNome()!=null && !"".equals(infoBusta.getErogatore().getNome())) sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".erogatore='"+infoBusta.getErogatore().getNome()+"'");
			}
			//servizio
			if(infoBusta.getServizio()!=null){
				if(infoBusta.getTipoServizio()!=null && !"".equals(infoBusta.getTipoServizio())) sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".tipo_servizio='"+infoBusta.getTipoServizio()+"'");
				if(infoBusta.getServizio()!=null && !"".equals(infoBusta.getServizio())) sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".servizio='"+infoBusta.getServizio()+"'");
			}
			//azione
			if(infoBusta.getAzione()!=null && !"".equals(infoBusta.getAzione())){
				sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".azione='"+infoBusta.getAzione()+"'");
			}
		}
		
		sqlQueryObject.addOrderBy("gdo");
//		sqlQueryObject.addOrderBy("tipo_fruitore");
//		sqlQueryObject.addOrderBy("fruitore");
//		sqlQueryObject.addOrderBy("tipo_erogatore");
//		sqlQueryObject.addOrderBy("erogatore");
//		sqlQueryObject.addOrderBy("tipo_servizio");
//		sqlQueryObject.addOrderBy("servizio");
		sqlQueryObject.setSortType(false);
		
		/*
		 * se setLimit=false allora non voglio settare ne limit ne offset
		 */
		if(setLimit){
			sqlQueryObject.setLimit(filter.getLimit());
			log.debug("Limit :"+filter.getLimit());
			//query complessa gestisco gli offset tramite la offsetmap
			//offsetmap[0] e' l offset che mi interessa
			long offset=filter.getOffsetMap()!=null && filter.getOffsetMap().length>2 ? filter.getOffsetMap()[0] : 0;
			log.debug("Offset : "+offset);
			sqlQueryObject.setOffset((int)offset);
		}
		return sqlQueryObject;
		
	}
	
	/**
	 * Crea la query sulla tabella msgdiagnostici aggiungendo delle colonne in modo da poter fare la union
	 * con la tabella di correlazione
	 * @param filter
	 * @return ISQLQueryObject
	 */
	public static ISQLQueryObject createSqlQueryObjDiagnostici(FiltroRicercaDiagnosticiConPaginazione filter,boolean setLimit,
			Logger log,String tipoDatabase,Vector<String>properties) throws SQLQueryObjectException{
		
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
		
		//select
		sqlQueryObject.addSelectField("idmessaggio");
		sqlQueryObject.addSelectField("gdo");
		sqlQueryObject.addSelectAliasField("null","porta");
		sqlQueryObject.addSelectAliasField("null","delegata");
		sqlQueryObject.addSelectAliasField("null","tipo_fruitore");
		sqlQueryObject.addSelectAliasField("null","fruitore");
		sqlQueryObject.addSelectAliasField("null","tipo_erogatore");
		sqlQueryObject.addSelectAliasField("null","erogatore");
		sqlQueryObject.addSelectAliasField("null","tipo_servizio");
		sqlQueryObject.addSelectAliasField("null","servizio");
		sqlQueryObject.addSelectAliasField("null","azione");
		sqlQueryObject.addSelectField("id");
		sqlQueryObject.addSelectAliasField("null","id_correlazione_applicativa");
		sqlQueryObject.addSelectAliasField("null","id_correlazione_risposta");
		if(properties!=null){
			for (int i = 0; i < properties.size(); i++) {
				String key = properties.get(i);
				sqlQueryObject.addSelectAliasField("null",key);
			}
		}
		sqlQueryObject.addSelectField("pdd_codice");
		sqlQueryObject.addSelectField("pdd_tipo_soggetto");
		sqlQueryObject.addSelectField("pdd_nome_soggetto");
		sqlQueryObject.addSelectField("idfunzione");
		sqlQueryObject.addSelectField("severita");
		sqlQueryObject.addSelectField("idmessaggio_risposta");
		sqlQueryObject.addSelectField("messaggio");
		sqlQueryObject.addSelectField("codice");
		
		//from
		sqlQueryObject.addFromTable(CostantiDB.MSG_DIAGNOSTICI);
		
		//eseguo ricerca in base ai valori di filtro specificati
		//where

		sqlQueryObject.setANDLogicOperator(true);
		//data inizio
		if(filter.getDataInizio()!=null){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".gdo>=?");
		}
		//data fine
		if(filter.getDataFine()!=null){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".gdo<=?");
		}
		//id busta deve essere null
		if(filter.getIdBustaRichiesta()!=null && !"".equals(filter.getIdBustaRichiesta())){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".idmessaggio=?");
		}else{
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".idmessaggio is NULL");
		}
		
	
		sqlQueryObject.addOrderBy("gdo");
		sqlQueryObject.setSortType(false);
		if(setLimit){
			sqlQueryObject.setLimit(filter.getLimit());
			log.debug("Limit:"+filter.getLimit());
			//query complessa gestisco gli offset tramite la offsetmap
			//offsetmap[0] e' l offset che mi interessa
			long offset=filter.getOffsetMap()!=null && filter.getOffsetMap().length>2 ? filter.getOffsetMap()[1] : 0;
			log.debug("Offset : "+offset);
			sqlQueryObject.setOffset((int)offset);
		}
		
	
		return sqlQueryObject;
		
	}
	
	public static ISQLQueryObject createQueryObjDiagnosticiNotExist(FiltroRicercaDiagnosticiConPaginazione filter,boolean setLimit,
			Logger log,String tipoDatabase,Vector<String>properties) throws SQLQueryObjectException{
		
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
		ISQLQueryObject innerObj = SQLObjectFactory.createSQLQueryObject(tipoDatabase);

		//select
		sqlQueryObject.addSelectField("idmessaggio");
		sqlQueryObject.addSelectField("gdo");
		sqlQueryObject.addSelectAliasField("null","porta");
		sqlQueryObject.addSelectAliasField("null","delegata");
		sqlQueryObject.addSelectAliasField("null","tipo_fruitore");
		sqlQueryObject.addSelectAliasField("null","fruitore");
		sqlQueryObject.addSelectAliasField("null","tipo_erogatore");
		sqlQueryObject.addSelectAliasField("null","erogatore");
		sqlQueryObject.addSelectAliasField("null","tipo_servizio");
		sqlQueryObject.addSelectAliasField("null","servizio");
		sqlQueryObject.addSelectAliasField("null","azione");
		sqlQueryObject.addSelectField("id");
		sqlQueryObject.addSelectAliasField("null","id_correlazione_applicativa");
		sqlQueryObject.addSelectAliasField("null","id_correlazione_risposta");
		if(properties!=null){
			for (int i = 0; i < properties.size(); i++) {
				String key = properties.get(i);
				sqlQueryObject.addSelectAliasField("null",key);
			}
		}
		sqlQueryObject.addSelectField("pdd_codice");
		sqlQueryObject.addSelectField("pdd_tipo_soggetto");
		sqlQueryObject.addSelectField("pdd_nome_soggetto");
		sqlQueryObject.addSelectField("idfunzione");
		sqlQueryObject.addSelectField("severita");
		sqlQueryObject.addSelectField("idmessaggio_risposta");
		sqlQueryObject.addSelectField("messaggio");
		sqlQueryObject.addSelectField("codice");
		
		//from
		sqlQueryObject.addFromTable(CostantiDB.MSG_DIAGNOSTICI);
		
		//eseguo ricerca in base ai valori di filtro specificati
		//where

		sqlQueryObject.setANDLogicOperator(true);
		//data inizio
		if(filter.getDataInizio()!=null){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".gdo>=?");
		}
		//data fine
		if(filter.getDataFine()!=null){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".gdo<=?");
		}
		//id busta
		if(filter.getIdBustaRichiesta()!=null && !"".equals(filter.getIdBustaRichiesta())){
			sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".idmessaggio=?");
		}
		
		//query interna
		innerObj.addFromTable(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE);
		innerObj.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".idmessaggio="+CostantiDB.MSG_DIAGNOSTICI+".idmessaggio");
		 
		//aggiungo not exist
		sqlQueryObject.addWhereExistsCondition(true, innerObj);
		
		sqlQueryObject.addOrderBy("gdo");
		sqlQueryObject.setSortType(false);
		
		if(setLimit){
			sqlQueryObject.setLimit(filter.getLimit());
			log.debug("Limit : "+filter.getLimit());
			//query complessa gestisco gli offset tramite la offsetmap
			//offsetmap[0] e' l offset che mi interessa
			long offset=filter.getOffsetMap()!=null && filter.getOffsetMap().length>2 ? filter.getOffsetMap()[2] : 0;
			log.debug("Offset : "+offset);
			sqlQueryObject.setOffset((int)offset);
		}
		
		
		return sqlQueryObject;
	}
	
	
	
	
	
	
	
	
	/* *********** UNION **********
	
	/**
	 * Effettua la union con gli sqlqueryobj 
	 * per recuperare tutti i messaggi diagnostici che soddisfano il filtro ma che possono non avere
	 * una correlazione con idBusta
	 * @param filter
	 * @return
	 * @throws String
	 */
	public static String createUnionObj(FiltroRicercaDiagnosticiConPaginazione filter,
			Logger log, String tipoDatabase,Vector<String> properties) throws SQLQueryObjectException{
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
		
		//orderby solo su gdo causa bug su orderby
		sqlQueryObject.addOrderBy("gdo");
//		sqlQueryObject.addOrderBy("tipo_fruitore");
//		sqlQueryObject.addOrderBy("fruitore");
//		sqlQueryObject.addOrderBy("tipo_erogatore");
//		sqlQueryObject.addOrderBy("erogatore");
//		sqlQueryObject.addOrderBy("tipo_servizio");
//		sqlQueryObject.addOrderBy("servizio");
		sqlQueryObject.setSortType(false);
		
		sqlQueryObject.setLimit(filter.getLimit());
		
		//NOTA: l'offset non va impostato in quanto viene impostato singolarmente utilizzando
		//la offsetmap all'interno del filter all'interno della createSQLUnion
		//sqlQueryObject.setOffset((int) filter.getOffset());
		if(filter.isPartial()){
			//se query parziale non eseguo la terza query
			return sqlQueryObject.createSQLUnion(false, 
					DriverMsgDiagnosticiUnionUtilities.createSQLQueryObjCorrelazione(filter,true,log,tipoDatabase,properties),
					DriverMsgDiagnosticiUnionUtilities.createSqlQueryObjDiagnostici(filter,true,log,tipoDatabase,properties));
		}else{
			return sqlQueryObject.createSQLUnion(false, 
					DriverMsgDiagnosticiUnionUtilities.createSQLQueryObjCorrelazione(filter,true,log,tipoDatabase,properties),
					DriverMsgDiagnosticiUnionUtilities.createSqlQueryObjDiagnostici(filter,true,log,tipoDatabase,properties),
					DriverMsgDiagnosticiUnionUtilities.createQueryObjDiagnosticiNotExist(filter,true,log,tipoDatabase,properties));
		}
	}
	
	
}
