/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.monitor.engine.config.BasicServiceLibrary;
import org.openspcoop2.monitor.engine.config.BasicServiceLibraryReader;
import org.openspcoop2.monitor.engine.config.TransactionServiceLibrary;
import org.openspcoop2.monitor.engine.config.TransactionServiceLibraryReader;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto;
import org.openspcoop2.monitor.engine.exceptions.RegolaDumpNotExistsException;
import org.openspcoop2.monitor.sdk.condition.Context;
import org.openspcoop2.monitor.sdk.condition.FilterFactory;
import org.openspcoop2.monitor.sdk.condition.IStatisticFilter;
import org.openspcoop2.monitor.sdk.condition.StatisticsContext;
import org.openspcoop2.monitor.sdk.constants.ParameterType;
import org.openspcoop2.monitor.sdk.exceptions.ParameterException;
import org.openspcoop2.monitor.sdk.exceptions.SearchException;
import org.openspcoop2.monitor.sdk.exceptions.StatisticException;
import org.openspcoop2.monitor.sdk.parameters.Parameter;
import org.openspcoop2.monitor.sdk.parameters.ParameterFactory;
import org.openspcoop2.monitor.sdk.plugins.StatisticProcessing;
import org.openspcoop2.monitor.sdk.statistic.IStatistic;
import org.slf4j.Logger;

/**
 * StatisticByResource
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticByResource extends StatisticProcessing {

	// plugins ConfigurazioneStatistica
	public static final String ID = "__StatisticByResource__";
	public static final String LABEL = "Risorse";
	public static final String DESCRIZIONE = "Informazioni Statistiche sulle Risorse associate alle Transazioni";
	
	
	public final static String PARAM_RESOURCE = "Risorsa";
	

	// popolamento

	@Override
	public void createHourlyStatisticData(IStatistic context) throws StatisticException {
		this.createStatisticData(context);
	}
	@Override
	public void createWeeklyStatisticData(IStatistic context) throws StatisticException {
		this.createStatisticData(context);
	}
	@Override
	public void createMonthlyStatisticData(IStatistic context) throws StatisticException {
		this.createStatisticData(context);
	}
	@Override
	public void createDailyStatisticData(IStatistic context) throws StatisticException {
		this.createStatisticData(context);
	}
	private void createStatisticData(IStatistic context) throws StatisticException {

		try{

			IDServizio idServizio = IDServizioFactory.getInstance().
					getIDServizioFromValues(context.getTipoServizio(), context.getServizio(), 
							context.getTipoSoggettoDestinatario(), context.getSoggettoDestinatario(), 
							context.getVersioneServizio());
			idServizio.setAzione(context.getAzione());

			boolean debug = false;
			if(context instanceof CustomStatisticsSdkGenerator){
				CustomStatisticsSdkGenerator custom = (CustomStatisticsSdkGenerator) context;
				debug = custom.getAbstractStatisticheCore().isDebug();
			}
			List<String> listResources = this.readResources(idServizio, context.getLogger(), debug);
			
			/* ****  Creo filtri che mi serviranno per effettuare la campionatura in base ad ogni risorsa ***** */
			for (String resourceID : listResources) {
				context.createStatistics(resourceID,resourceID); // associo come id statistica lo stesso nome dell'id risorsa
			}
			
		} catch(Exception e) {
			throw new StatisticException(e.getMessage(),e);
		}
		
	}
	



	
	// grafica (ricerca)
	
	@Override
	public List<Parameter<?>> getParameters(Context context) throws SearchException, ParameterException {
		
		List<String> listResources = null;
		try{
			listResources = this.readResources(this.toIDServizio(context), context.getLogger(), false);	
		} catch(Exception e) {
			throw new SearchException(e);
		}
		
		List<Parameter<?>> parameters = new ArrayList<Parameter<?>>();
	
		Parameter<?> pRisorsa = ParameterFactory.createParameter(ParameterType.SELECT_LIST, StatisticByResource.PARAM_RESOURCE);
		pRisorsa.getRendering().setLabel("Risorsa");
		pRisorsa.getRendering().setSuggestion("Indicare la risorsa per cui si vuole visualizzare le informazioni statistiche");
		pRisorsa.getRendering().setValues(listResources);
		pRisorsa.getRendering().setRequired(true);
		parameters.add(pRisorsa);
		
		return parameters;
			
	}
	
	

	// estrazione

	@Override
	public IStatisticFilter createSearchFilter(StatisticsContext context)
			throws StatisticException {

		try{
			// Recupero valore inserito dall'utente
			Parameter<?> resource = context.getParameter(StatisticByResource.PARAM_RESOURCE);
			String resourceValue = (String) resource.getValue();
			if(resourceValue==null){
				throw new StatisticException("Parameter undefined");
			}

			IStatisticFilter filter = FilterFactory.newFilterStatisticRepository(context);
			filter.setIdStatistic(resourceValue);

			return filter;

		}catch(Exception e){
			throw new StatisticException(e.getMessage(),e);
		}
	}




	// Utils
	
	private IDServizio toIDServizio(Context context) throws DriverRegistroServiziException{
		IDServizio idServizio = IDServizioFactory.getInstance().
				getIDServizioFromValues(context.getTipoServizio(), context.getServizio(), 
						context.getTipoSoggettoDestinatario(), context.getSoggettoDestinatario(), 
						context.getVersioneServizio());
		idServizio.setAzione(context.getAzione());
		return idServizio;
	}
	
	private List<String> readResources(IDServizio idServizio,Logger log, boolean debug) throws Exception{
		TransactionServiceLibrary transactionServiceLibrary = this.getObjectReadFromDB(idServizio, log, debug);
		List<ConfigurazioneTransazioneRisorsaContenuto> list = transactionServiceLibrary.mergeServiceActionTransactionLibrary_resources();
		List<String> resources = new ArrayList<String>();
		if(list!=null){
			for (ConfigurazioneTransazioneRisorsaContenuto configurazioneTransazioneRisorsaContenuto : list) {
				if(configurazioneTransazioneRisorsaContenuto.isStatEnabled()){
					resources.add(configurazioneTransazioneRisorsaContenuto.getNome());
				}
			}
		}
		return resources;
	}
	
	private TransactionServiceLibrary getObjectReadFromDB(IDServizio idServizio,Logger log, boolean debug) throws Exception,RegolaDumpNotExistsException{
		
		DAOFactory daoFactory = DAOFactory.getInstance(log);
		
		org.openspcoop2.core.plugins.dao.IServiceManager jdbcServiceManagerPluginsBase = 
				(org.openspcoop2.core.plugins.dao.IServiceManager) daoFactory.getServiceManager(org.openspcoop2.core.plugins.utils.ProjectInfo.getInstance());
		org.openspcoop2.core.commons.search.dao.IServiceManager jdbcServiceManagerUtils = 
				(org.openspcoop2.core.commons.search.dao.IServiceManager) daoFactory.getServiceManager(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
		org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager serviceManagerPluginsTransazioni =
				(org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager) daoFactory.getServiceManager(org.openspcoop2.monitor.engine.config.transazioni.utils.ProjectInfo.getInstance());
		
		BasicServiceLibraryReader basicServiceLibraryReader = new BasicServiceLibraryReader(jdbcServiceManagerPluginsBase, jdbcServiceManagerUtils, debug);
				
		BasicServiceLibrary basicServiceLibrary = basicServiceLibraryReader.read(idServizio, log);
		if(basicServiceLibrary==null){
			throw new RegolaDumpNotExistsException("Regola di dump non esiste per servizio ["+idServizio+"]");
		}
		
		TransactionServiceLibraryReader transactionServiceLibraryReader = 
				new TransactionServiceLibraryReader(serviceManagerPluginsTransazioni, debug);
		TransactionServiceLibrary transactionServiceLibrary = transactionServiceLibraryReader.readConfigurazioneTransazione(basicServiceLibrary, log);
		if(transactionServiceLibrary==null){
			throw new RegolaDumpNotExistsException("Regola di dump (Transaction-Info) non esiste per servizio ["+idServizio+"]");
		}	
		
		return transactionServiceLibrary;
		
	}


}
