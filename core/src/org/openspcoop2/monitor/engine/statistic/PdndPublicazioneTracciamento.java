/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import java.util.List;

import org.openspcoop2.core.statistiche.StatistichePdndTracing;
import org.openspcoop2.core.statistiche.constants.PossibiliStatiPdnd;
import org.openspcoop2.core.statistiche.constants.PossibiliStatiRichieste;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

/**
 * PdndPublicazioneTracciamento
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PdndPublicazioneTracciamento implements IStatisticsEngine {

	private org.openspcoop2.core.statistiche.dao.IServiceManager statisticheSM;
	private org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM;
	private org.openspcoop2.core.commons.search.dao.IServiceManager utilsSM;
	private Logger logger;
	private StatisticsConfig config;
	
	PdndPublicazioneTracciamento(){
			super();
	}
	
	@Override
	public void init(StatisticsConfig config,
			org.openspcoop2.core.statistiche.dao.IServiceManager statisticheSM,
			org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM,
			org.openspcoop2.monitor.engine.config.statistiche.dao.IServiceManager pluginsStatisticheSM,
			org.openspcoop2.core.plugins.dao.IServiceManager pluginsBaseSM,
			org.openspcoop2.core.commons.search.dao.IServiceManager utilsSM,
			org.openspcoop2.monitor.engine.config.transazioni.dao.IServiceManager pluginsTransazioniSM) {
		this.logger = config.getLogCore();
		this.transazioniSM = transazioniSM;
		this.statisticheSM = statisticheSM;
		this.utilsSM = utilsSM;
		this.config = config;
	}
	
	private void sendTrace(StatistichePdndTracing stat) {
		HttpRequest req = this.config.getPdndTracingBaseRequest();
		req.setMethod(HttpRequestMethod.POST);
		req.setUrl(req.getUrl() + "/tracing");
		
		//HttpUtilities.httpInvoke(req);
		return;
	}
	
	private boolean updateTraceStatus(StatistichePdndTracing stat) {
		return false;
	}
	
	private void publishRecords() throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, ExpressionNotImplementedException, ExpressionException {
		IPaginatedExpression expr = this.statisticheSM.getStatistichePdndTracingService().newPaginatedExpression();
		
		expr.isNotNull(StatistichePdndTracing.model().CSV);
		expr.equals(StatistichePdndTracing.model().STATO_PDND, PossibiliStatiPdnd.WAITING);
		
		List<StatistichePdndTracing> stats = this.statisticheSM.getStatistichePdndTracingService().findAll(expr);
		
		for (StatistichePdndTracing stat : stats) {
			sendTrace(stat);
		}
		
	}
	
	private void checkPending() throws ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException, NotFoundException {
		IPaginatedExpression expr = this.statisticheSM.getStatistichePdndTracingService().newPaginatedExpression();
		
		expr.equals(StatistichePdndTracing.model().STATO_PDND, PossibiliStatiPdnd.PENDING);
		
		List<StatistichePdndTracing> stats = this.statisticheSM.getStatistichePdndTracingService().findAll(expr);

		for (StatistichePdndTracing stat : stats) {
			updateTraceStatus(stat);
			
			this.statisticheSM.getStatistichePdndTracingService().update(stat);
		}
	}
	
	private void updateMissing() {
		
	}
	
	@Override
	public void generate() throws StatisticsEngineException {
		try {
			publishRecords();
			
			checkPending();
			
			updateMissing();
		} catch (ServiceException 
				| NotFoundException 
				| MultipleResultException 
				| NotImplementedException
				| ExpressionNotImplementedException 
				| ExpressionException e) {
			this.logger.error("Errore nella pubblicazione delle tracce", e);
		}
	}
	
	@Override
	public boolean isEnabled(StatisticsConfig config) {
		return config.isPdndGenerazioneTracciamento();
	}
}
