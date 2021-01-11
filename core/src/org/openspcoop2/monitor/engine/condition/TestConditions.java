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
package org.openspcoop2.monitor.engine.condition;

import org.apache.logging.log4j.Level;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryException;
import org.openspcoop2.core.statistiche.StatisticaGiornaliera;
import org.openspcoop2.core.statistiche.StatisticaMensile;
import org.openspcoop2.core.statistiche.StatisticaOraria;
import org.openspcoop2.core.statistiche.StatisticaSettimanale;
import org.openspcoop2.core.statistiche.dao.IStatisticaGiornalieraServiceSearch;
import org.openspcoop2.core.statistiche.dao.IStatisticaMensileServiceSearch;
import org.openspcoop2.core.statistiche.dao.IStatisticaOrariaServiceSearch;
import org.openspcoop2.core.statistiche.dao.IStatisticaSettimanaleServiceSearch;
import org.openspcoop2.core.statistiche.dao.jdbc.converter.StatisticaGiornalieraFieldConverter;
import org.openspcoop2.core.statistiche.dao.jdbc.converter.StatisticaMensileFieldConverter;
import org.openspcoop2.core.statistiche.dao.jdbc.converter.StatisticaOrariaFieldConverter;
import org.openspcoop2.core.statistiche.dao.jdbc.converter.StatisticaSettimanaleFieldConverter;
import org.openspcoop2.core.statistiche.model.StatisticaContenutiModel;
import org.openspcoop2.core.statistiche.model.StatisticaModel;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.dao.ITransazioneServiceSearch;
import org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager;
import org.openspcoop2.core.transazioni.dao.jdbc.converter.TransazioneFieldConverter;
import org.openspcoop2.generic_project.beans.AliasTableComplexField;
import org.openspcoop2.generic_project.beans.ComplexField;
import org.openspcoop2.generic_project.beans.Function;
import org.openspcoop2.generic_project.beans.FunctionField;
import org.openspcoop2.generic_project.beans.IAliasTableField;
import org.openspcoop2.generic_project.dao.jdbc.JDBCPaginatedExpression;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.monitor.engine.config.base.utils.FilterUtils;
import org.openspcoop2.monitor.sdk.condition.FilterFactory;
import org.openspcoop2.monitor.sdk.condition.IFilter;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.monitor.sdk.exceptions.FilterFactoryException;
import org.openspcoop2.monitor.sdk.exceptions.SearchException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.TipiDatabase;

/**
 * FilterUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestConditions {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		TipiDatabase tipiDatabase = TipiDatabase.POSTGRESQL;
		
		LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.ALL);
		
		System.out.println("----------------------------- Repository Transazioni ------------------------------------");
		TestConditions.test(null,tipiDatabase);
		System.out.println("----------------------------- Repository Transazioni ------------------------------------");
		
		
		System.out.println("\n\n\n----------------------------- Repository Statistiche ------------------------------------");
		TestConditions.test(StatisticType.MENSILE,tipiDatabase);
		System.out.println("----------------------------- Repository Statistiche ------------------------------------");

		
	}
	
	private static IFilter newFilter(StatisticType statisticType,TipiDatabase tipiDatabase) throws FilterFactoryException{
		if(statisticType!=null){
			return FilterFactory.newFilterStatisticRepository(tipiDatabase,statisticType);
		}else{
			return FilterFactory.newFilterTransactionRepository(tipiDatabase);
		}
	}
	
	
	private static IPaginatedExpression newExpression(StatisticType statisticType,TipiDatabase tipiDatabase) throws ExpressionException, ExpressionNotImplementedException{
		ISQLFieldConverter fieldConverter = null;
		if(statisticType!=null){
			switch (statisticType) {
			case ORARIA:
				fieldConverter = new StatisticaOrariaFieldConverter(tipiDatabase);
				break;
			case GIORNALIERA:
				fieldConverter = new StatisticaGiornalieraFieldConverter(tipiDatabase);
				break;
			case SETTIMANALE:
				fieldConverter = new StatisticaSettimanaleFieldConverter(tipiDatabase);
				break;
			case MENSILE:
				fieldConverter = new StatisticaMensileFieldConverter(tipiDatabase);
				break;
			}
		}else{
			fieldConverter = new TransazioneFieldConverter(tipiDatabase);
		}
		JDBCPaginatedExpression pagExpression = new JDBCPaginatedExpression(fieldConverter);
		if(statisticType!=null){
			pagExpression.and();
			StatisticaModel model = null;
			StatisticaContenutiModel contenutiModel = null;
			switch (statisticType) {
			case ORARIA:
				model = StatisticaOraria.model().STATISTICA_BASE;
				contenutiModel = StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI;
				break;
			case GIORNALIERA:
				model = StatisticaGiornaliera.model().STATISTICA_BASE;
				contenutiModel = StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI;
				break;
			case SETTIMANALE:
				model = StatisticaSettimanale.model().STATISTICA_BASE;
				contenutiModel = StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI;
				break;
			case MENSILE:
				model = StatisticaMensile.model().STATISTICA_BASE;
				contenutiModel = StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI;
				break;
			}
			pagExpression.equals(model.SERVIZIO, "ServizioTest");
			pagExpression.equals(model.AZIONE, "AzioneTest");
			pagExpression.limit(25);
			pagExpression.offset(10);
			pagExpression.sortOrder(SortOrder.ASC);
			pagExpression.addOrder(model.SERVIZIO);
			
			pagExpression.addGroupBy(model.SERVIZIO);
			pagExpression.addGroupBy(contenutiModel.RISORSA_NOME);
			
			// esempio di un filtro
			IAliasTableField af = new AliasTableComplexField((ComplexField)contenutiModel.FILTRO_NOME_1, FilterUtils.getNextAliasStatisticsTable());
			IAliasTableField afValore = new AliasTableComplexField((ComplexField)contenutiModel.FILTRO_VALORE_1, af.getAliasTable());
			pagExpression.addGroupBy(af);
			pagExpression.addGroupBy(afValore);
			IAliasTableField af2 = new AliasTableComplexField((ComplexField)contenutiModel.FILTRO_NOME_2, FilterUtils.getNextAliasStatisticsTable());
			IAliasTableField afValore2 = new AliasTableComplexField((ComplexField)contenutiModel.FILTRO_VALORE_2, af2.getAliasTable());
			pagExpression.addGroupBy(af2);
			pagExpression.addGroupBy(afValore2);
			
			
		}else{
			pagExpression.and();
			pagExpression.equals(Transazione.model().NOME_SERVIZIO, "ServizioTest");
			pagExpression.equals(Transazione.model().AZIONE, "AzioneTest");
			pagExpression.limit(25);
			pagExpression.offset(10);
			pagExpression.sortOrder(SortOrder.ASC);
			pagExpression.addOrder(Transazione.model().NOME_SOGGETTO_FRUITORE);
			
			pagExpression.addGroupBy(Transazione.model().PDD_RUOLO);
			pagExpression.addGroupBy(Transazione.model().PDD_CODICE);
			pagExpression.addGroupBy(Transazione.model().TIPO_SOGGETTO_FRUITORE);
			pagExpression.addGroupBy(Transazione.model().NOME_SOGGETTO_FRUITORE);
			pagExpression.addGroupBy(Transazione.model().TIPO_SOGGETTO_EROGATORE);
			pagExpression.addGroupBy(Transazione.model().NOME_SOGGETTO_EROGATORE);
			pagExpression.addGroupBy(Transazione.model().TIPO_SERVIZIO);
			pagExpression.addGroupBy(Transazione.model().NOME_SERVIZIO);
			pagExpression.addGroupBy(Transazione.model().AZIONE);
			pagExpression.addGroupBy(Transazione.model().SERVIZIO_APPLICATIVO_FRUITORE);
			pagExpression.addGroupBy(Transazione.model().ESITO);
			pagExpression.addGroupBy(Transazione.model().DUMP_MESSAGGIO.CONTENUTO.NOME);
			pagExpression.addGroupBy(Transazione.model().DUMP_MESSAGGIO.CONTENUTO.VALORE);
			
			// esempio di un filtro
			IAliasTableField af = new AliasTableComplexField((ComplexField)Transazione.model().DUMP_MESSAGGIO.CONTENUTO.NOME, FilterUtils.getNextAliasStatisticsTable());
			IAliasTableField afValore = new AliasTableComplexField((ComplexField)Transazione.model().DUMP_MESSAGGIO.CONTENUTO.VALORE, af.getAliasTable());
			pagExpression.addGroupBy(af);
			pagExpression.addGroupBy(afValore);
			
		}
		return pagExpression;
	}
	private static void useExpression(StatisticType statisticType,IPaginatedExpression expression) throws ExpressionException, DAOFactoryException, ServiceException, NotImplementedException, NotFoundException{
		FunctionField functionField = null;
		if(statisticType!=null){
			org.openspcoop2.core.statistiche.dao.jdbc.JDBCServiceManager serviceManager = (org.openspcoop2.core.statistiche.dao.jdbc.JDBCServiceManager) 
					DAOFactory.getInstance(LoggerWrapperFactory.getLogger(TestConditions.class)).
						getServiceManager(org.openspcoop2.core.statistiche.utils.ProjectInfo.getInstance());
			switch (statisticType) {
			case ORARIA:
				functionField = new FunctionField(StatisticaOraria.model().STATISTICA_ORARIA_CONTENUTI.NUMERO_TRANSAZIONI, Function.SUM, "somma");
				IStatisticaOrariaServiceSearch statOrariaSearviceSearch = serviceManager.getStatisticaOrariaServiceSearch();
				try{
					statOrariaSearviceSearch.groupBy(expression, functionField);
				}catch(NotFoundException notFound){}
				break;
			case GIORNALIERA:
				functionField = new FunctionField(StatisticaGiornaliera.model().STATISTICA_GIORNALIERA_CONTENUTI.NUMERO_TRANSAZIONI, Function.SUM, "somma");
				IStatisticaGiornalieraServiceSearch statGiornalieraSearviceSearch = serviceManager.getStatisticaGiornalieraServiceSearch();
				try{
					statGiornalieraSearviceSearch.groupBy(expression, functionField);
				}catch(NotFoundException notFound){}
				break;
			case SETTIMANALE:
				functionField = new FunctionField(StatisticaSettimanale.model().STATISTICA_SETTIMANALE_CONTENUTI.NUMERO_TRANSAZIONI, Function.SUM, "somma");
				IStatisticaSettimanaleServiceSearch statSettimanaleSearviceSearch = serviceManager.getStatisticaSettimanaleServiceSearch();
				try{
					statSettimanaleSearviceSearch.groupBy(expression, functionField);
				}catch(NotFoundException notFound){}
				break;
			case MENSILE:
				functionField = new FunctionField(StatisticaMensile.model().STATISTICA_MENSILE_CONTENUTI.NUMERO_TRANSAZIONI, Function.SUM, "somma");
				IStatisticaMensileServiceSearch statMensileSearviceSearch = serviceManager.getStatisticaMensileServiceSearch();
				try{
					statMensileSearviceSearch.groupBy(expression, functionField);
				}catch(NotFoundException notFound){}
				break;
			}
			
		}else{			
			functionField = new FunctionField(Transazione.model().ID_TRANSAZIONE, Function.COUNT, "somma");
			JDBCServiceManager serviceManager = (JDBCServiceManager) 
					DAOFactory.getInstance(LoggerWrapperFactory.getLogger(TestConditions.class)).
						getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
			ITransazioneServiceSearch transazioneSearviceSearch = serviceManager.getTransazioneServiceSearch();
			try{
				transazioneSearviceSearch.groupBy(expression, functionField);
			}catch(NotFoundException notFound){}
		}
	}
	
	public static void test(StatisticType statisticType,TipiDatabase tipiDatabase) throws SearchException, FilterFactoryException, ExpressionException, ExpressionNotImplementedException, DAOFactoryException, ServiceException, NotImplementedException, NotFoundException{
		
		IPaginatedExpression expression = newExpression(statisticType, tipiDatabase);
		
		
		
		// EQUALS
		
		FilterImpl filtro = (FilterImpl) TestConditions.newFilter(statisticType,tipiDatabase).equals("Resourcetest", "ValoreTest");
		FilterImpl filtro2 = (FilterImpl) filtro.newFilter().notEquals("ResourcetestB", "ValoreTestB");
		
		expression.and(filtro.getExpression());
		expression.and(filtro2.getExpression());
		
		useExpression(statisticType,expression);
	}

}
