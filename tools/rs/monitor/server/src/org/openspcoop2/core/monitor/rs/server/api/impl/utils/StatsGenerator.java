/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

package org.openspcoop2.core.monitor.rs.server.api.impl.utils;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.web.monitor.core.bean.UserDetailsBean;
import org.openspcoop2.web.monitor.statistiche.dao.ConfigurazioniGeneraliService;
import org.openspcoop2.web.monitor.statistiche.dao.StatisticheGiornaliereService;
import org.openspcoop2.web.monitor.statistiche.servlet.ReportExporter;

/**
 * StatsGenerator
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class StatsGenerator {

	public static byte[] generateReport(HttpRequestWrapper request, IContext context, StatisticheGiornaliereService statisticheService) throws CoreException, NotFoundException {
		
		HttpResponseWrapper wrapper = new HttpResponseWrapper(context.getServletResponse());
		
		try {
			UserDetailsBean user = new UserDetailsBean(); 
			SearchFormUtilities utilities = new SearchFormUtilities();
			user.setUtente(utilities.getUtente(context));
			
			ReportExporter.generaStatistiche(request, wrapper, user, statisticheService);
		}
		catch(NotFoundException notFound) {
			throw notFound;
		}
		catch(Exception e) {
			throw new CoreException(e.getMessage(), e);
		}
	
		byte[] report = wrapper.toByteArray();
		if(report==null || report.length<=0) {
			throw new NotFoundException("Empty Report");
		}
		return report;
	}
	
	public static byte[] generateReport(HttpRequestWrapper request, IContext context, ConfigurazioniGeneraliService configurazioniService) throws CoreException, NotFoundException {
		
		HttpResponseWrapper wrapper = new HttpResponseWrapper(context.getServletResponse());
		
		try {
			UserDetailsBean user = new UserDetailsBean(); 
			SearchFormUtilities utilities = new SearchFormUtilities();
			user.setUtente(utilities.getUtente(context));
			
			ReportExporter.generaConfigurazione(request, wrapper, user, configurazioniService);
		}
		catch(NotFoundException notFound) {
			throw notFound;
		}
		catch(Exception e) {
			throw new CoreException(e.getMessage(), e);
		}
	
		byte[] report = wrapper.toByteArray();
		if(report==null || report.length<=0) {
			throw new NotFoundException("Empty Report");
		}
		return report;
	}
	
}
