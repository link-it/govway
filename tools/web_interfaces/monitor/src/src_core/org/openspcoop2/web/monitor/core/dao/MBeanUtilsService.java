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
package org.openspcoop2.web.monitor.core.dao;

import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.search.Resource;
import org.openspcoop2.core.commons.search.dao.IResourceServiceSearch;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.transazioni.CredenzialeMittente;
import org.openspcoop2.core.transazioni.dao.jdbc.JDBCCredenzialeMittenteServiceSearch;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.web.monitor.core.listener.AbstractConsoleStartupListener;
import org.slf4j.Logger;

/***
 * 
 * Funzionalita' di supporto per la gestione delle maschere di ricerca.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class MBeanUtilsService {

	// **** COSTRUTTORE PER METODI USATI IN XXXBean ****

	private Logger log;
	
	private org.openspcoop2.core.commons.search.dao.IServiceManager service;
	public MBeanUtilsService(org.openspcoop2.core.commons.search.dao.IServiceManager service, Logger log){
		this.service = service;
		this.log = log;
	}
	
	private org.openspcoop2.core.transazioni.dao.ICredenzialeMittenteService credenzialiMittenteDAO;
	public MBeanUtilsService(org.openspcoop2.core.transazioni.dao.ICredenzialeMittenteService credenzialiMittenteDAO, Logger log){
		this.credenzialiMittenteDAO = credenzialiMittenteDAO;
		this.log = log;
	}




	// **** UTILITY ****

	public static String buildKey(Object ... params) {
		return DynamicUtilsService.buildKey(params);
	}





	// **** METODI USATI IN XXXBean ****

	
	
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> getInfoOperazioneFromCache(String op, IDAccordo idAccordo) throws Exception{
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(op, idAccordo);
			String methodName = "getInfoOperazione";
			try {
				return (List<Map<String,Object>>) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {String.class, IDAccordo.class},
						op, idAccordo);
			}catch(Throwable e) {
				this.log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.getInfoOperazione(op, idAccordo);
		}
	}
	public List<Map<String,Object>> getInfoOperazione(String op, IDAccordo idAccordo) throws Exception{
		IResourceServiceSearch resourceServiceSearch = this.service.getResourceServiceSearch();
		IPaginatedExpression pagExpr = resourceServiceSearch.newPaginatedExpression();
		pagExpr.equals(Resource.model().NOME, op).
		and().
		equals(Resource.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, idAccordo.getNome()).
		equals(Resource.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, idAccordo.getVersione()).
		equals(Resource.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, idAccordo.getSoggettoReferente().getTipo()).
		equals(Resource.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, idAccordo.getSoggettoReferente().getNome());
		List<Map<String,Object>> l = this.service.getResourceServiceSearch().select(pagExpr, Resource.model().HTTP_METHOD, Resource.model().PATH);
		return l;
	}
	
	
	
	public CredenzialeMittente getCredenzialeMittenteFromCache(Long id) throws ServiceException, MultipleResultException, NotFoundException, NotImplementedException{
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(id);
			String methodName = "getCredenzialeMittente";
			try {
				return (CredenzialeMittente) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {Long.class},
						id);
			}catch(Throwable e) {
				this.log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.getCredenzialeMittente(id);
		}
	}
	public CredenzialeMittente getCredenzialeMittente(Long id) throws ServiceException, MultipleResultException, NotFoundException, NotImplementedException{
		return ((JDBCCredenzialeMittenteServiceSearch)this.credenzialiMittenteDAO).get(id);
	}
}
