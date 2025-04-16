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
package org.openspcoop2.web.monitor.core.dao;

import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.search.AccordoServizioParteComune;
import org.openspcoop2.core.commons.search.Resource;
import org.openspcoop2.core.commons.search.dao.IAccordoServizioParteComuneServiceSearch;
import org.openspcoop2.core.commons.search.dao.IResourceServiceSearch;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.transazioni.CredenzialeMittente;
import org.openspcoop2.core.transazioni.constants.TipoAPI;
import org.openspcoop2.core.transazioni.dao.jdbc.JDBCCredenzialeMittenteServiceSearch;
import org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeTokenClient;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.LikeMode;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
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

	public int getTipoApiFromCache(IDAccordo idAccordo) throws Exception{
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(idAccordo);
			String methodName = "getTipoApi";
			try {
				return (Integer) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {IDAccordo.class},
						idAccordo);
			}catch(Throwable e) {
				this.log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return -1;
			}
		}
		else {
			return this.getTipoApi(idAccordo);
		}
	}
	public int getTipoApi(IDAccordo idAccordo) throws Exception{
		IAccordoServizioParteComuneServiceSearch apcServiceSearch = this.service.getAccordoServizioParteComuneServiceSearch();
		IPaginatedExpression pagExpr = apcServiceSearch.newPaginatedExpression();
		pagExpr.and().
		equals(AccordoServizioParteComune.model().NOME, idAccordo.getNome()).
		equals(AccordoServizioParteComune.model().VERSIONE, idAccordo.getVersione()).
		equals(AccordoServizioParteComune.model().ID_REFERENTE.TIPO, idAccordo.getSoggettoReferente().getTipo()).
		equals(AccordoServizioParteComune.model().ID_REFERENTE.NOME, idAccordo.getSoggettoReferente().getNome());
		List<Object> l = this.service.getAccordoServizioParteComuneServiceSearch().select(pagExpr, AccordoServizioParteComune.model().SERVICE_BINDING);
		if(l!=null && !l.isEmpty()) {
			String sb = (String) l.get(0);
			ServiceBinding serviceBinding = ServiceBinding.toEnumConstant(sb);
			switch (serviceBinding) {
			case REST:
				return TipoAPI.REST.getValoreAsInt();
			case SOAP:
				return TipoAPI.SOAP.getValoreAsInt();
			}
		}
		return -1;
	}
	
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
	
	
	public CredenzialeMittente getCredenzialeMittenteByReferenceFromCache(TipoCredenzialeMittente tipo, Long id) throws ServiceException, NotFoundException, NotImplementedException, ExpressionNotImplementedException, ExpressionException{
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(tipo, id);
			String methodName = "getCredenzialeMittenteByReference";
			try {
				return (CredenzialeMittente) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {TipoCredenzialeMittente.class, Long.class},
						tipo, id);
			}
			catch(NotFoundException e) {
				String msg = "Cache Access NotFound (method:"+methodName+" key:"+key+"): "+e.getMessage();
				this.log.debug(msg);
				return null;
			}
			catch(Throwable e) {
				this.log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.getCredenzialeMittenteByReference(tipo, id);
		}
	}
	public CredenzialeMittente getCredenzialeMittenteByReference(TipoCredenzialeMittente tipo, Long id) throws ServiceException, NotFoundException, NotImplementedException, ExpressionNotImplementedException, ExpressionException{
		JDBCCredenzialeMittenteServiceSearch search = ((JDBCCredenzialeMittenteServiceSearch)this.credenzialiMittenteDAO);
		IPaginatedExpression pagExpr = search.newPaginatedExpression();
		pagExpr.equals(CredenzialeMittente.model().REF_CREDENZIALE, id);
		pagExpr.equals(CredenzialeMittente.model().TIPO, tipo.getRawValue());
		pagExpr.addOrder(CredenzialeMittente.model().ORA_REGISTRAZIONE, SortOrder.DESC);
		pagExpr.limit(1);
		List<CredenzialeMittente> l = search.findAll(pagExpr);
		CredenzialeMittente cm = null;
		if(l!=null && !l.isEmpty()) {
			cm = l.get(0);
		}
		/**System.out.println("TEST FORZO RICERCA ALTERNATIVA"); cm = null;*/
		if(cm!=null) {
			return cm;
		}
		else if(TipoCredenzialeMittente.PDND_ORGANIZATION_NAME.equals(tipo) || TipoCredenzialeMittente.PDND_ORGANIZATION_JSON.equals(tipo) ) {
			// provo a cercare se l'informazione sull'organizzazione è stata asssociata ad un altro clientId della stessa organizzazione
			try {
				cm = getPdndOrganizationJsonFromClientIdLong(search, tipo, id);
			}catch(Exception e) {
				// fatalError
				if(this.log!=null) {
					this.log.error("getPdndOrganizationJsonFromClientIdLong process failed: "+e.getMessage(),e);
				}
			}
			if(cm!=null) {
				/**System.out.println("TROVATA INFO TRAMITE RICERCA ALTERNATIVA!");*/
				return cm;
			}
		}
		throw new NotFoundException("Credential type["+tipo.getRawValue()+"] ref["+id+"] not found");
		
	}
	private CredenzialeMittente getPdndOrganizationJsonFromClientIdLong(JDBCCredenzialeMittenteServiceSearch search, TipoCredenzialeMittente tipo, Long id) throws ServiceException, NotFoundException, NotImplementedException, ExpressionNotImplementedException, ExpressionException{
		
		// Potrebbe succedere che esista sul DB delle tracce una situazione tipo clientId diversi con stesso consumerId
		/** pdnd_client_json	{"id":"aaa5313f-ec86-4ec5-bfcb-55d80b8addaf","consumerId":"ddda331c-2516-48dc-96f6-1ad334985846"}
		    pdnd_client_json	{"id":"bbb7d8d4-7bb6-4b28-bd1a-f03dd1602f1d","consumerId":"ddda331c-2516-48dc-96f6-1ad334985846"}
		    pdnd_client_json	{"id":"ccc5d37c-7ec9-4326-9069-e0696fb017d9","consumerId":"ddda331c-2516-48dc-96f6-1ad334985846"}
		
    	    e invece l'informazione sull'organizzazione riferisce solo uno degli id con id 1812
		    pdnd_org_name	Comune di Esempio	14-APR-25 17:04:57,627000000	2699	1812 <----
		    pdnd_org_json	{"id":"kkkka331c-2516-48dc-96f6-1ad334985846","externalId":{"origin":"IPA","id":"c_a123"},"name":"Comune di Esempio","category":"Comuni e loro Consorzi e Associazioni"} 2679	1812 <----
		
		    dove 1812 è : 
		    token_clientId	bbb7d8d4-7bb6-4b28-bd1a-f03dd1602f1d	14-APR-25 17:04:57,606000000	1812
		 *	
		 */
		
		// recupero uuid del clientId
		CredenzialeMittente cmClient = null;
		try {
			cmClient = search.get(id);
		}catch(NotFoundException notFound) {
			// not found ?
		}catch(Exception e) {
			// fatalError
			this.log.error("getPdndOrganizationJsonFromClientIdLong failed: "+e.getMessage(),e);
		}
		String clientId = null;
		if(cmClient!=null) {
			/**System.out.println("RECUPERATO CLIENT ID ["+cmClient.getCredenziale()+"]");*/
			TipoCredenzialeMittente t = TipoCredenzialeMittente.toEnumConstant(cmClient.getTipo(), false);
			if(t!=null && t.equals(TipoCredenzialeMittente.TOKEN_CLIENT_ID)) {
				clientId = CredenzialeTokenClient.convertClientIdDBValueToOriginal(cmClient.getCredenziale());
				/**System.out.println("RECUPERATO CLIENT ID ["+cmClient.getCredenziale()+"]: "+clientId);*/
			}
		}
		
		String consumerId = getPdndOrganizationConsumerId(search, clientId);
		/**System.out.println("RECUPERATO CONSUMER ID ["+consumerId+"]");*/
		
		if(consumerId!=null) {
			return getPdndOrganizationInfoFromConsumerId(search, consumerId, tipo, id);
		}
		
		return null;
		
	}
	private String getPdndOrganizationConsumerId(JDBCCredenzialeMittenteServiceSearch search, String clientId) throws ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException {
		// cerco consumer id con quel client id
		IPaginatedExpression pagExpr = search.newPaginatedExpression();
		pagExpr.equals(CredenzialeMittente.model().TIPO, TipoCredenzialeMittente.PDND_CLIENT_JSON.getRawValue());
		pagExpr.like(CredenzialeMittente.model().CREDENZIALE, "\""+clientId+"\"", LikeMode.ANYWHERE);
		pagExpr.addOrder(CredenzialeMittente.model().ORA_REGISTRAZIONE, SortOrder.DESC);
		pagExpr.limit(1);
		List<CredenzialeMittente> l = null;
		try {
			l = search.findAll(pagExpr);
		}catch(Exception e) {
			// fatalError
			this.log.error("getPdndOrganizationConsumerId clientId:'"+clientId+"' failed: "+e.getMessage(),e);
		}
		CredenzialeMittente cmJson = null;
		if(l!=null && !l.isEmpty()) {
			cmJson = l.get(0);
		}
		String consumerId = null;
		if(cmJson!=null && cmJson.getCredenziale()!=null) {
			String pattern = "$.consumerId";
			try {
				consumerId = JsonPathExpressionEngine.extractAndConvertResultAsString(cmJson.getCredenziale(), pattern, this.log);
			}catch(Exception e) {
				// fatalError
				this.log.error("getPdndOrganizationJsonFromClientIdLong consumerId read failed from ["+cmJson.getCredenziale()+"] with pattern ["+pattern+"]: "+e.getMessage(),e);
			}
		}
		return consumerId;
	}
	private CredenzialeMittente getPdndOrganizationInfoFromConsumerId(JDBCCredenzialeMittenteServiceSearch search, String consumerId, TipoCredenzialeMittente tipo, long id) throws ExpressionNotImplementedException, ExpressionException, ServiceException, NotImplementedException {
		IPaginatedExpression pagExpr = search.newPaginatedExpression();
		pagExpr.equals(CredenzialeMittente.model().TIPO, TipoCredenzialeMittente.PDND_ORGANIZATION_JSON.getRawValue());
		pagExpr.like(CredenzialeMittente.model().CREDENZIALE, "\""+consumerId+"\"", LikeMode.ANYWHERE);
		pagExpr.addOrder(CredenzialeMittente.model().ORA_REGISTRAZIONE, SortOrder.DESC);
		pagExpr.limit(1);
		List<CredenzialeMittente> l = null;
		try {
			l = search.findAll(pagExpr);
		}catch(Exception e) {
			// fatalError
			this.log.error("getPdndOrganizationInfoFromConsumerId consumerId:'"+consumerId+"' failed: "+e.getMessage(),e);
		}
		CredenzialeMittente cmJson = null;
		if(l!=null && !l.isEmpty()) {
			cmJson = l.get(0);
		}
		if(cmJson!=null && cmJson.getCredenziale()!=null) {
			if(TipoCredenzialeMittente.PDND_ORGANIZATION_JSON.equals(tipo)) {
				cmJson.setRefCredenziale(id); // aggiorno ref
				/**System.out.println("RECUPERATO PDND_ORGANIZATION_JSON ["+cmJson.getCredenziale()+"]");*/
				return cmJson;
			}
			else {
				String pattern =  "$.name";
				String name = null;
				try {
					name = JsonPathExpressionEngine.extractAndConvertResultAsString(cmJson.getCredenziale(), pattern, this.log);
				}catch(Exception e) {
					// fatalError
					this.log.error("getPdndOrganizationJsonFromClientIdLong name read failed from ["+cmJson.getCredenziale()+"] with pattern ["+pattern+"]: "+e.getMessage(),e);
				}
				if(name!=null) {
					CredenzialeMittente cmName = new CredenzialeMittente();
					cmName.setCredenziale(name);
					cmName.setOraRegistrazione(cmJson.getOraRegistrazione()); // metto stessa data
					cmName.setRefCredenziale(id); // aggiorno ref
					cmName.setTipo(TipoCredenzialeMittente.PDND_ORGANIZATION_NAME.getRawValue());
					/**System.out.println("RECUPERATO PDND_ORGANIZATION_NAME ["+cmName.getCredenziale()+"]");*/
					return cmName;
				}
			}
		}
		return null;
	}
}
