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
package org.openspcoop2.core.commons.search.utils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.ProtocolFactoryReflectionUtils;
import org.openspcoop2.core.commons.search.AccordoServizioParteComune;
import org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione;
import org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo;
import org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica;
import org.openspcoop2.core.commons.search.Fruitore;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComune;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo;
import org.openspcoop2.core.commons.search.IdPortType;
import org.openspcoop2.core.commons.search.IdSoggetto;
import org.openspcoop2.core.commons.search.Operation;
import org.openspcoop2.core.commons.search.PortaApplicativa;
import org.openspcoop2.core.commons.search.PortaDelegata;
import org.openspcoop2.core.commons.search.Resource;
import org.openspcoop2.core.commons.search.ServizioApplicativo;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.commons.search.dao.IAccordoServizioParteComuneAzioneServiceSearch;
import org.openspcoop2.core.commons.search.dao.IAccordoServizioParteComuneGruppoServiceSearch;
import org.openspcoop2.core.commons.search.dao.IAccordoServizioParteComuneServiceSearch;
import org.openspcoop2.core.commons.search.dao.IAccordoServizioParteSpecificaServiceSearch;
import org.openspcoop2.core.commons.search.dao.IFruitoreServiceSearch;
import org.openspcoop2.core.commons.search.dao.IOperationServiceSearch;
import org.openspcoop2.core.commons.search.dao.IPortaApplicativaServiceSearch;
import org.openspcoop2.core.commons.search.dao.IPortaDelegataServiceSearch;
import org.openspcoop2.core.commons.search.dao.IResourceServiceSearch;
import org.openspcoop2.core.commons.search.dao.IServizioApplicativoServiceSearch;
import org.openspcoop2.core.commons.search.dao.ISoggettoServiceSearch;
import org.openspcoop2.core.commons.search.dao.jdbc.JDBCAccordoServizioParteComuneServiceSearch;
import org.openspcoop2.core.commons.search.dao.jdbc.JDBCServiceManager;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.generic_project.beans.CustomField;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.LikeMode;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.message.constants.ServiceBinding;
import org.slf4j.Logger;

/**     
 * RegistroCore
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RegistroCore {
	
	public static JDBCServiceManager getServiceManager(Logger log, String tipoDB, Connection con) throws Exception {
		return getServiceManager(log, tipoDB, true, con);
	}
	
	public static JDBCServiceManager getServiceManager(Logger log, String tipoDB, boolean showSql, Connection con) throws Exception {
		ServiceManagerProperties properties = new ServiceManagerProperties();
		properties.setDatabaseType(tipoDB);
		properties.setShowSql(showSql);
		JDBCServiceManager manager = new JDBCServiceManager(con, properties);
		
		return manager;
	}

//	public static List<String> getProtocolli() throws Exception{
//		return ProtocolFactoryReflectionUtils.getProtocolli();
//	}
//	
	
	public static List<IDSoggetto> getSoggetti(JDBCServiceManager manager, String protocollo) throws Exception{
		List<String> protocolli = null;
		if(protocollo!=null) {
			protocolli = new ArrayList<>();
			protocolli.add(protocollo);
		}
		return getSoggetti(manager, protocolli);
	}
	public static List<IDSoggetto> getSoggetti(JDBCServiceManager manager, List<String> protocolli) throws Exception{
		
		List<IDSoggetto> list = new ArrayList<IDSoggetto>();
		
		ISoggettoServiceSearch soggettoServiceSearch = manager.getSoggettoServiceSearch();
		IPaginatedExpression pag = soggettoServiceSearch.newPaginatedExpression();
		pag.and();
		
		if(protocolli!=null && protocolli.size()>0){
			List<String> types = new ArrayList<>();
			for (String protocollo : protocolli) {
				types.addAll(ProtocolFactoryReflectionUtils.getOrganizationTypes(protocollo));
			}
			pag.in(Soggetto.model().TIPO_SOGGETTO, types);
		}
		
		pag.addOrder(Soggetto.model().NOME_SOGGETTO,SortOrder.ASC);
		pag.addOrder(Soggetto.model().TIPO_SOGGETTO,SortOrder.ASC);
		
		List<Map<String, Object>> result = null;
		try{
			result = soggettoServiceSearch.select(pag, true, Soggetto.model().TIPO_SOGGETTO, Soggetto.model().NOME_SOGGETTO);
		}catch(NotFoundException notFound){}
		if(result!=null && result.size()>0){
			for (Map<String, Object> map : result) {
				String tipo = (String) map.get(Soggetto.model().TIPO_SOGGETTO.getFieldName());
				String nome = (String) map.get(Soggetto.model().NOME_SOGGETTO.getFieldName());
				list.add(new IDSoggetto(tipo, nome));
			}
		}
		
		return list;
	}
	
	public static List<IDSoggetto> getSoggettiFruitori(JDBCServiceManager manager, String protocollo,
			String tipoSoggettoErogatore,String nomeSoggettoErogatore, String tipoServizio, String nomeServizio, Integer versioneServizio) throws Exception{
		List<String> protocolli = null;
		if(protocollo!=null) {
			protocolli = new ArrayList<>();
			protocolli.add(protocollo);
		}
		return getSoggettiFruitori(manager, protocolli, tipoSoggettoErogatore, nomeSoggettoErogatore, tipoServizio, nomeServizio, versioneServizio);
	}
	public static List<IDSoggetto> getSoggettiFruitori(JDBCServiceManager manager, List<String> protocolli,
			String tipoSoggettoErogatore,String nomeSoggettoErogatore, String tipoServizio, String nomeServizio, Integer versioneServizio) throws Exception{
		
		List<IDSoggetto> list = new ArrayList<IDSoggetto>();
		
		IFruitoreServiceSearch fruitoreServiceSearch = manager.getFruitoreServiceSearch();
		IPaginatedExpression pag = fruitoreServiceSearch.newPaginatedExpression();
		pag.and();
		
		if(protocolli!=null && protocolli.size()>0){
			List<String> types = new ArrayList<>();
			for (String protocollo : protocolli) {
				types.addAll(ProtocolFactoryReflectionUtils.getOrganizationTypes(protocollo));
			}
			pag.in(Fruitore.model().ID_FRUITORE.TIPO, types);
		}
		if(tipoSoggettoErogatore!=null && nomeSoggettoErogatore!=null){
			pag.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE.TIPO, tipoSoggettoErogatore);
			pag.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE.NOME, nomeSoggettoErogatore);
		}
		if(tipoServizio!=null && nomeServizio!=null && versioneServizio!=null){
			pag.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.TIPO, tipoServizio);
			pag.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.NOME, nomeServizio);
			pag.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.VERSIONE, versioneServizio);
		}
		
		pag.addOrder(Fruitore.model().ID_FRUITORE.NOME,SortOrder.ASC);
		pag.addOrder(Fruitore.model().ID_FRUITORE.TIPO,SortOrder.ASC);
		
		List<Map<String, Object>> result = null;
		try{
			result = fruitoreServiceSearch.select(pag, true, Fruitore.model().ID_FRUITORE.TIPO, Fruitore.model().ID_FRUITORE.NOME);
		}catch(NotFoundException notFound){}
		if(result!=null && result.size()>0){
			for (Map<String, Object> map : result) {
				String tipo = (String) map.get(Fruitore.model().ID_FRUITORE.getBaseField().getFieldName()+"."+Fruitore.model().ID_FRUITORE.TIPO.getFieldName());
				String nome = (String) map.get(Fruitore.model().ID_FRUITORE.getBaseField().getFieldName()+"."+Fruitore.model().ID_FRUITORE.NOME.getFieldName());
				list.add(new IDSoggetto(tipo, nome));
			}
		}
		
		return list;
	}
	
	public static List<IDSoggetto> getSoggettiErogatori(JDBCServiceManager manager, String protocollo) throws Exception{
		List<String> protocolli = null;
		if(protocollo!=null) {
			protocolli = new ArrayList<>();
			protocolli.add(protocollo);
		}
		return getSoggettiErogatori(manager, protocolli);
	}
	public static List<IDSoggetto> getSoggettiErogatori(JDBCServiceManager manager, List<String> protocolli) throws Exception{
		List<IDSoggetto> list = new ArrayList<IDSoggetto>();
		
		IAccordoServizioParteSpecificaServiceSearch aspsServiceSearch = manager.getAccordoServizioParteSpecificaServiceSearch();
		IPaginatedExpression pag = aspsServiceSearch.newPaginatedExpression();
		pag.and();
		
		if(protocolli!=null && protocolli.size()>0){
			List<String> types = new ArrayList<>();
			for (String protocollo : protocolli) {
				types.addAll(ProtocolFactoryReflectionUtils.getOrganizationTypes(protocollo));
			}
			pag.in(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, types);
		}

		pag.addOrder(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME,SortOrder.ASC);
		pag.addOrder(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO,SortOrder.ASC);
		
		List<Map<String, Object>> result = null;
		try{
			result = aspsServiceSearch.select(pag, true, AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME);
		}catch(NotFoundException notFound){}
		if(result!=null && result.size()>0){
			for (Map<String, Object> map : result) {
				String tipo = (String) map.get(AccordoServizioParteSpecifica.model().ID_EROGATORE.getBaseField().getFieldName()+"."+AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO.getFieldName());
				String nome = (String) map.get(AccordoServizioParteSpecifica.model().ID_EROGATORE.getBaseField().getFieldName()+"."+AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME.getFieldName());
				list.add(new IDSoggetto(tipo, nome));
			}
		}
		
		return list;
	}
	
	public static List<IDServizio> getServizi(JDBCServiceManager manager, String protocollo, String tipoSoggettoErogatore, String nomeSoggettoErogatore) throws Exception{
		List<String> protocolli = null;
		if(protocollo!=null) {
			protocolli = new ArrayList<>();
			protocolli.add(protocollo);
		}
		return getServizi(manager, protocolli, tipoSoggettoErogatore, nomeSoggettoErogatore);
	}
	public static List<IDServizio> getServizi(JDBCServiceManager manager,  List<String> protocolli, String tipoSoggettoErogatore, String nomeSoggettoErogatore) throws Exception{
		List<IDServizio> list = new ArrayList<IDServizio>();
		
		IAccordoServizioParteSpecificaServiceSearch aspsServiceSearch = manager.getAccordoServizioParteSpecificaServiceSearch();
		IPaginatedExpression pag = aspsServiceSearch.newPaginatedExpression();
		pag.and();
		
		if(protocolli!=null && protocolli.size()>0){
			List<String> types = new ArrayList<>();
			for (String protocollo : protocolli) {
				types.addAll(ProtocolFactoryReflectionUtils.getServiceTypes(protocollo));
			}
			pag.in(AccordoServizioParteSpecifica.model().TIPO, types);
		}

		if(tipoSoggettoErogatore!=null && nomeSoggettoErogatore!=null){
			pag.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoSoggettoErogatore);
			pag.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME, nomeSoggettoErogatore);
		}
		
		pag.addOrder(AccordoServizioParteSpecifica.model().NOME,SortOrder.ASC);
		pag.addOrder(AccordoServizioParteSpecifica.model().VERSIONE,SortOrder.ASC);
		pag.addOrder(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME,SortOrder.ASC);
		pag.addOrder(AccordoServizioParteSpecifica.model().TIPO,SortOrder.ASC);
		pag.addOrder(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO,SortOrder.ASC);
		
		List<Map<String, Object>> result = null;
		try{
			result = aspsServiceSearch.select(pag, true, 
					AccordoServizioParteSpecifica.model().TIPO, 
					AccordoServizioParteSpecifica.model().NOME,
					AccordoServizioParteSpecifica.model().VERSIONE,
					AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO,
					AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME);
		}catch(NotFoundException notFound){}
		if(result!=null && result.size()>0){
			for (Map<String, Object> map : result) {
				String tipo = (String) map.get(AccordoServizioParteSpecifica.model().TIPO.getFieldName());
				String nome = (String) map.get(AccordoServizioParteSpecifica.model().NOME.getFieldName());
				Integer versione = (Integer) map.get(AccordoServizioParteSpecifica.model().VERSIONE.getFieldName());
				String tipoSoggetto = (String) map.get(AccordoServizioParteSpecifica.model().ID_EROGATORE.getBaseField().getFieldName()+
						"."+
						AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO.getFieldName());
				String nomeSoggetto = (String) map.get(AccordoServizioParteSpecifica.model().ID_EROGATORE.getBaseField().getFieldName()+
						"."+
						AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME.getFieldName());
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tipo, nome, tipoSoggetto, nomeSoggetto, versione);
				list.add(idServizio);
			}
		}
		
		return list;
	}
	
	public static List<String> getAzioni(JDBCServiceManager manager, String protocollo, 
			String tipoSoggettoErogatore, String nomeSoggettoErogatore,	String tipoServizio, String nomeServizio, Integer versioneServizio) throws Exception{
		List<String> protocolli = null;
		if(protocollo!=null) {
			protocolli = new ArrayList<>();
			protocolli.add(protocollo);
		}
		return getAzioni(manager, protocolli, tipoSoggettoErogatore, nomeSoggettoErogatore, tipoServizio, nomeServizio, versioneServizio);
	}
	public static List<String> getAzioni(JDBCServiceManager manager, List<String> protocolli, 
			String tipoSoggettoErogatore, String nomeSoggettoErogatore,	String tipoServizio, String nomeServizio, Integer versioneServizio) throws Exception{
		List<String> list = new ArrayList<String>();
		list.addAll(_getAzioni(manager, protocolli, tipoSoggettoErogatore, nomeSoggettoErogatore, tipoServizio, nomeServizio, versioneServizio,null).keySet());
		 
		if(list!=null && list.size()>0){
			Collections.sort(list);
		}
		 
		return list;
	}
	
	public static Map<String,String> getAzioniConLabel(JDBCServiceManager manager, String protocollo, String tipoSoggettoErogatore, String nomeSoggettoErogatore, String tipoServizio, String nomeServizio, Integer versioneServizio, String input) throws Exception{
		List<String> protocolli = null;
		if(protocollo!=null) {
			protocolli = new ArrayList<>();
			protocolli.add(protocollo);
		}
		Map<String,String> mapAzioni = _getAzioni(manager, protocolli, tipoSoggettoErogatore, nomeSoggettoErogatore, tipoServizio, nomeServizio, versioneServizio,input);
		
		//convert map to a List
		List<Entry<String, String>> list = new LinkedList<Map.Entry<String, String>>(mapAzioni.entrySet());

		//sorting the list with a comparator
		Collections.sort(list, new Comparator<Entry<String, String>>() {
			@Override
			public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		//convert sortedMap back to Map
		Map<String, String> sortedMap = new LinkedHashMap<String, String>();
		for (Entry<String, String> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		
		return sortedMap;
	}
	
	public static Map<String,String> _getAzioni(JDBCServiceManager manager, List<String> protocolli, String tipoSoggettoErogatore, String nomeSoggettoErogatore, String tipoServizio, String nomeServizio, Integer versioneServizio, String input) throws Exception{
		
		List<String> list = new ArrayList<String>();
		Map<String,String> mapAzioni = new HashMap<String,String>();
			
		// Localizzo Accordi di Servizio Parte Comune dalle Parti Specifiche
		List<IdPortType> idPortTypes = new ArrayList<IdPortType>();
		IAccordoServizioParteSpecificaServiceSearch aspsServiceSearch = manager.getAccordoServizioParteSpecificaServiceSearch();
		IPaginatedExpression pag = aspsServiceSearch.newPaginatedExpression();
		pag.and();
		
		if(protocolli!=null && protocolli.size()>0){
			List<String> types = new ArrayList<>();
			for (String protocollo : protocolli) {
				types.addAll(ProtocolFactoryReflectionUtils.getServiceTypes(protocollo));
			}
			pag.in(AccordoServizioParteSpecifica.model().TIPO, types);
		}
		
		if(tipoSoggettoErogatore!=null && nomeSoggettoErogatore!=null){
			pag.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoSoggettoErogatore);
			pag.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME, nomeSoggettoErogatore);
		}
		if(tipoServizio!=null && nomeServizio!=null && versioneServizio!=null){
			pag.equals(AccordoServizioParteSpecifica.model().TIPO, tipoServizio);
			pag.equals(AccordoServizioParteSpecifica.model().NOME, nomeServizio);
			pag.equals(AccordoServizioParteSpecifica.model().VERSIONE, versioneServizio);
		}		
		List<Map<String, Object>> result = null;
		try{
			result = aspsServiceSearch.select(pag, true, AccordoServizioParteSpecifica.model().PORT_TYPE, 
					AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME,
					AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO,
					AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME,
					AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE,
					AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.SERVICE_BINDING);
		}catch(NotFoundException notFound){}
		if(result!=null && result.size()>0){
			for (Map<String, Object> map : result) {
				
				IdPortType idPortType = new IdPortType();
				
				Object portType = map.get(AccordoServizioParteSpecifica.model().PORT_TYPE.getFieldName());
				if(portType!=null && (portType instanceof String)){
					idPortType.setNome((String)portType); // Può essere null
				}
				
				String nomeAccordo = (String) map.get(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.getBaseField().getFieldName()+
						"."+AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME.getFieldName());
				
				Object tipoSoggettoReferenteAccordo = map.get(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.getBaseField().getFieldName()+
						"."+AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.getBaseField().getFieldName()+
						"."+AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO.getFieldName());
				Object nomeSoggettoReferenteAccordo = map.get(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.getBaseField().getFieldName()+
						"."+AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.getBaseField().getFieldName()+
						"."+AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME.getFieldName());
				
				Object versioneAccordo = map.get(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.getBaseField().getFieldName()+
						"."+AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE.getFieldName());
				
				IdSoggetto idSoggettoReferente = null;
				if(tipoSoggettoReferenteAccordo!=null && (tipoSoggettoReferenteAccordo instanceof String) &&
						nomeSoggettoReferenteAccordo!=null && (nomeSoggettoReferenteAccordo instanceof String)){
					idSoggettoReferente = new IdSoggetto();
					idSoggettoReferente.setTipo((String)tipoSoggettoReferenteAccordo);
					idSoggettoReferente.setNome((String)nomeSoggettoReferenteAccordo);
				}
				Integer versione = null;
				if(versioneAccordo!=null && (versioneAccordo instanceof Integer)){
					versione = (Integer) versioneAccordo;
				} 
				
				String serviceBindingAccordo = (String) map.get(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.getBaseField().getFieldName()+
						"."+AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.SERVICE_BINDING.getFieldName());
				
				IdAccordoServizioParteComune idAccordo = new IdAccordoServizioParteComune();
				idAccordo.setNome(nomeAccordo);
				idAccordo.setVersione(versione);
				idAccordo.setIdSoggetto(idSoggettoReferente);
				idAccordo.setServiceBinding(serviceBindingAccordo);
				idPortType.setIdAccordoServizioParteComune(idAccordo);
				
				idPortTypes.add(idPortType);
			}
		}
		
		// Localizzo Azioni dagli Accordi Implementati
		if(idPortTypes!=null && idPortTypes.size()>0){
			for (IdPortType idPortType : idPortTypes) {
				
				ServiceBinding serviceBinding = ServiceBinding.valueOf(idPortType.getIdAccordoServizioParteComune().getServiceBinding().toUpperCase());
				
				if(ServiceBinding.REST.equals(serviceBinding)) { 
					IResourceServiceSearch aspcResourceServiceSearch = manager.getResourceServiceSearch();
					IPaginatedExpression pagResources = aspcResourceServiceSearch.newPaginatedExpression();
					pagResources.and();
					pagResources.equals(Resource.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, idPortType.getIdAccordoServizioParteComune().getNome());
					if(idPortType.getIdAccordoServizioParteComune().getVersione()!=null){
						pagResources.equals(Resource.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, idPortType.getIdAccordoServizioParteComune().getVersione());
					}
					if(idPortType.getIdAccordoServizioParteComune().getIdSoggetto()!=null){
						pagResources.equals(Resource.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, 
								idPortType.getIdAccordoServizioParteComune().getIdSoggetto().getTipo());
						pagResources.equals(Resource.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, 
								idPortType.getIdAccordoServizioParteComune().getIdSoggetto().getNome());
					}
					
					if(StringUtils.isNotEmpty(input)){
						IExpression exprOrMethod = aspcResourceServiceSearch.newExpression();
						exprOrMethod.ilike(Resource.model().HTTP_METHOD, input , LikeMode.ANYWHERE);
						IExpression exprOrPath = aspcResourceServiceSearch.newExpression();
						exprOrPath.ilike(Resource.model().PATH, input , LikeMode.ANYWHERE);
						pagResources.or(exprOrMethod,exprOrPath);
					}
					List<Map<String, Object>> resultAzioni = null;
					try{
						resultAzioni = aspcResourceServiceSearch.select(pagResources, true, Resource.model().NOME, Resource.model().HTTP_METHOD, Resource.model().PATH);
					}catch(NotFoundException notFound){}
					if(resultAzioni!=null && resultAzioni.size()>0){
						for (Map<String, Object> azione : resultAzioni) {
							String az = (String) azione.get(Resource.model().NOME.getFieldName());
							String httpmethod = (String) azione.get(Resource.model().HTTP_METHOD.getFieldName());
							String path = (String) azione.get(Resource.model().PATH.getFieldName());
							String label = httpmethod + " " + path;
							if(list.contains(az)==false){
								list.add(az);
								mapAzioni.put(az,label); 
							}
						}
					}
					
				}
				else {
					if(idPortType.getNome()!=null){
						
						IOperationServiceSearch portTypeAzioneServiceSearch = manager.getOperationServiceSearch();
						IPaginatedExpression pagAzioni = portTypeAzioneServiceSearch.newPaginatedExpression();
						pagAzioni.and();
						pagAzioni.equals(Operation.model().ID_PORT_TYPE.NOME, idPortType.getNome());
						pagAzioni.equals(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, idPortType.getIdAccordoServizioParteComune().getNome());
						if(idPortType.getIdAccordoServizioParteComune().getVersione()!=null){
							pagAzioni.equals(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, idPortType.getIdAccordoServizioParteComune().getVersione());
						}
						if(idPortType.getIdAccordoServizioParteComune().getIdSoggetto()!=null){
							pagAzioni.equals(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, 
									idPortType.getIdAccordoServizioParteComune().getIdSoggetto().getTipo());
							pagAzioni.equals(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, 
									idPortType.getIdAccordoServizioParteComune().getIdSoggetto().getNome());
						}
						if(StringUtils.isNotEmpty(input)){
							pagAzioni.ilike(Operation.model().NOME,input,LikeMode.ANYWHERE);		
						}
						List<Object> resultAzioni = null;
						try{
							resultAzioni = portTypeAzioneServiceSearch.select(pagAzioni, true, Operation.model().NOME);
						}catch(NotFoundException notFound){}
						if(resultAzioni!=null && resultAzioni.size()>0){
							for (Object azione : resultAzioni) {
								String az = (String) azione;
								if(list.contains(az)==false){
									list.add(az);
									mapAzioni.put(az, az); 
								}
							}
						}
						
					}
					else{
						IAccordoServizioParteComuneAzioneServiceSearch aspcAzioneServiceSearch = manager.getAccordoServizioParteComuneAzioneServiceSearch();
						IPaginatedExpression pagAzioni = aspcAzioneServiceSearch.newPaginatedExpression();
						pagAzioni.and();
						pagAzioni.equals(AccordoServizioParteComuneAzione.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, idPortType.getIdAccordoServizioParteComune().getNome());
						if(idPortType.getIdAccordoServizioParteComune().getVersione()!=null){
							pagAzioni.equals(AccordoServizioParteComuneAzione.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, idPortType.getIdAccordoServizioParteComune().getVersione());
						}
						if(idPortType.getIdAccordoServizioParteComune().getIdSoggetto()!=null){
							pagAzioni.equals(AccordoServizioParteComuneAzione.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, 
									idPortType.getIdAccordoServizioParteComune().getIdSoggetto().getTipo());
							pagAzioni.equals(AccordoServizioParteComuneAzione.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, 
									idPortType.getIdAccordoServizioParteComune().getIdSoggetto().getNome());
						}
						if(StringUtils.isNotEmpty(input)){
							pagAzioni.ilike(AccordoServizioParteComuneAzione.model().NOME,input,LikeMode.ANYWHERE);		
						}
						List<Object> resultAzioni = null;
						try{
							resultAzioni = aspcAzioneServiceSearch.select(pagAzioni, true, AccordoServizioParteComuneAzione.model().NOME);
						}catch(NotFoundException notFound){}
						if(resultAzioni!=null && resultAzioni.size()>0){
							for (Object azione : resultAzioni) {
								String az = (String) azione;
								if(list.contains(az)==false){
									list.add(az);
									mapAzioni.put(az, az); 
								}
							}
						}
					}
				}
				
			}
		}
		
		Map<String,String> mapAzioniReturn = new HashMap<String,String>();
		
		if(list!=null && list.size()>0){
			Collections.sort(list);
			
			for (String key : list) {
				mapAzioniReturn.put(key, mapAzioni.get(key));
			}
		}
		
		return mapAzioniReturn;
	}
	
	public static List<IDServizioApplicativo> getServiziApplicativiErogatori(JDBCServiceManager manager, String protocollo, 
			String tipoSoggettoErogatore, String nomeSoggettoErogatore, String tipoServizio, String nomeServizio, Integer versioneServizio,
			String azione) throws Exception{
		List<String> protocolli = null;
		if(protocollo!=null) {
			protocolli = new ArrayList<>();
			protocolli.add(protocollo);
		}
		return getServiziApplicativiErogatori(manager, protocolli, 
				tipoSoggettoErogatore, nomeSoggettoErogatore, tipoServizio, nomeServizio, versioneServizio, 
				azione);
	}
	public static List<IDServizioApplicativo> getServiziApplicativiErogatori(JDBCServiceManager manager,List<String> protocolli, 
			String tipoSoggettoErogatore, String nomeSoggettoErogatore, String tipoServizio, String nomeServizio, Integer versioneServizio,
			String azione) throws Exception{
		List<IDServizioApplicativo> list = new ArrayList<IDServizioApplicativo>();
		
		IPortaApplicativaServiceSearch paServiceSearch = manager.getPortaApplicativaServiceSearch();
		IPaginatedExpression pag = paServiceSearch.newPaginatedExpression();
		pag.and();
		
		if(protocolli!=null && protocolli.size()>0){
			List<String> types = new ArrayList<>();
			for (String protocollo : protocolli) {
				types.addAll(ProtocolFactoryReflectionUtils.getOrganizationTypes(protocollo));
			}
			pag.in(PortaApplicativa.model().ID_SOGGETTO.TIPO, types);
		}
		
		if(tipoSoggettoErogatore!=null && nomeSoggettoErogatore!=null){
			pag.equals(PortaApplicativa.model().ID_SOGGETTO.TIPO, tipoSoggettoErogatore);
			pag.equals(PortaApplicativa.model().ID_SOGGETTO.NOME, nomeSoggettoErogatore);
		}
		
		if(tipoServizio!=null && nomeServizio!=null && versioneServizio!=null){
			pag.equals(PortaApplicativa.model().TIPO_SERVIZIO, tipoServizio);
			pag.equals(PortaApplicativa.model().NOME_SERVIZIO, nomeServizio);
			pag.equals(PortaApplicativa.model().VERSIONE_SERVIZIO, versioneServizio);
		}
		
		if(azione!=null){
			IExpression azioneExpr = paServiceSearch.newExpression();
			azioneExpr.or();
			azioneExpr.equals(PortaApplicativa.model().NOME_AZIONE, azione);
			azioneExpr.isNull(PortaApplicativa.model().NOME_AZIONE);
			azioneExpr.isEmpty(PortaApplicativa.model().NOME_AZIONE);
			azioneExpr.equals(PortaApplicativa.model().PORTA_APPLICATIVA_AZIONE.NOME, azione);
			pag.and(azioneExpr);
		}
		
		pag.addOrder(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.NOME,SortOrder.ASC);
		pag.addOrder(PortaApplicativa.model().ID_SOGGETTO.NOME,SortOrder.ASC);
		pag.addOrder(PortaApplicativa.model().ID_SOGGETTO.TIPO,SortOrder.ASC);
		
		List<Map<String, Object>> result = null;
		try{
			result = paServiceSearch.select(pag, true, 
					PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.NOME,
					PortaApplicativa.model().ID_SOGGETTO.TIPO,
					PortaApplicativa.model().ID_SOGGETTO.NOME);
		}catch(NotFoundException notFound){}
		if(result!=null && result.size()>0){
			for (Map<String, Object> map : result) {
				
				String nomeSA = (String) map.get(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO.getBaseField().getFieldName()+
						"."+
						PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.getBaseField().getFieldName()+
						"."+
						PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.NOME.getFieldName());
				
				String tipoSoggetto = (String) map.get(PortaApplicativa.model().ID_SOGGETTO.getBaseField().getFieldName()+
						"."+
						PortaApplicativa.model().ID_SOGGETTO.TIPO.getFieldName());
				String nomeSoggetto = (String) map.get(PortaApplicativa.model().ID_SOGGETTO.getBaseField().getFieldName()+
						"."+
						PortaApplicativa.model().ID_SOGGETTO.NOME.getFieldName());
		
				IDServizioApplicativo idSA = new IDServizioApplicativo();
				IDSoggetto idSoggetto = new IDSoggetto(tipoSoggetto,nomeSoggetto);
				idSA.setIdSoggettoProprietario(idSoggetto);
				idSA.setNome(nomeSA);
				
				list.add(idSA);
			}	
		}
		
		return list;
	}
	
	
	public static List<IDServizioApplicativo> getServiziApplicativiFruitore(JDBCServiceManager manager, String protocollo, 
			String tipoSoggettoFruitore, String nomeSoggettoFruitore, 
			String tipoSoggettoErogatore, String nomeSoggettoErogatore, String tipoServizio, String nomeServizio, Integer versioneServizio, 
			String azione) throws Exception{
		List<String> protocolli = null;
		if(protocollo!=null) {
			protocolli = new ArrayList<>();
			protocolli.add(protocollo);
		}
		return getServiziApplicativiFruitore(manager, protocolli, 
				tipoSoggettoFruitore, nomeSoggettoFruitore, 
				tipoSoggettoErogatore, nomeSoggettoErogatore, tipoServizio, nomeServizio, versioneServizio, 
				azione);
	}
	public static List<IDServizioApplicativo> getServiziApplicativiFruitore(JDBCServiceManager manager, List<String> protocolli, 
			String tipoSoggettoFruitore, String nomeSoggettoFruitore, 
			String tipoSoggettoErogatore, String nomeSoggettoErogatore, String tipoServizio, String nomeServizio, Integer versioneServizio, 
			String azione) throws Exception{
		List<IDServizioApplicativo> list = new ArrayList<IDServizioApplicativo>();
			
		IPortaDelegataServiceSearch pdServiceSearch = manager.getPortaDelegataServiceSearch();
		IPaginatedExpression pag = pdServiceSearch.newPaginatedExpression();
		pag.and();
		
		if(protocolli!=null && protocolli.size()>0){
			List<String> types = new ArrayList<>();
			for (String protocollo : protocolli) {
				types.addAll(ProtocolFactoryReflectionUtils.getOrganizationTypes(protocollo));
			}
			pag.in(PortaDelegata.model().ID_SOGGETTO.TIPO, types);
		}
		
		if(tipoSoggettoFruitore!=null && nomeSoggettoFruitore!=null){
			pag.equals(PortaDelegata.model().ID_SOGGETTO.TIPO, tipoSoggettoFruitore);
			pag.equals(PortaDelegata.model().ID_SOGGETTO.NOME, nomeSoggettoFruitore);
		}
		
		if(tipoSoggettoErogatore!=null && nomeSoggettoErogatore!=null){
			pag.equals(PortaDelegata.model().TIPO_SOGGETTO_EROGATORE, tipoSoggettoErogatore);
			pag.equals(PortaDelegata.model().NOME_SOGGETTO_EROGATORE, nomeSoggettoErogatore);
		}
		
		if(tipoServizio!=null && nomeServizio!=null && versioneServizio!=null){
			pag.equals(PortaDelegata.model().TIPO_SERVIZIO, tipoServizio);
			pag.equals(PortaDelegata.model().NOME_SERVIZIO, nomeServizio);
			pag.equals(PortaDelegata.model().VERSIONE_SERVIZIO, versioneServizio);
		}
		
		if(azione!=null){
			// L'azione può essere null se abbiamo modalità di riconoscimento differente da static
			IExpression azioneExpr = pdServiceSearch.newExpression();
			azioneExpr.or();
			azioneExpr.equals(PortaDelegata.model().NOME_AZIONE, azione);
			azioneExpr.isNull(PortaDelegata.model().NOME_AZIONE);
			azioneExpr.isEmpty(PortaDelegata.model().NOME_AZIONE);
			azioneExpr.equals(PortaDelegata.model().PORTA_DELEGATA_AZIONE.NOME, azione);
			pag.and(azioneExpr);
		}
		
		pag.addOrder(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.NOME,SortOrder.ASC);
		pag.addOrder(PortaDelegata.model().ID_SOGGETTO.NOME,SortOrder.ASC);
		pag.addOrder(PortaDelegata.model().ID_SOGGETTO.TIPO,SortOrder.ASC);
		
		List<Map<String, Object>> result = null;
		try{
			result = pdServiceSearch.select(pag, true, PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.NOME,
					PortaDelegata.model().ID_SOGGETTO.TIPO,
					PortaDelegata.model().ID_SOGGETTO.NOME);
		}catch(NotFoundException notFound){}
		if(result!=null && result.size()>0){
			for (Map<String, Object> map : result) {
				
				String nomeSA = (String) map.get(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.getBaseField().getFieldName()+
						"."+
						PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.getBaseField().getFieldName()+
						"."+
						PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.NOME.getFieldName());
				
				String tipoSoggetto = (String) map.get(PortaDelegata.model().ID_SOGGETTO.getBaseField().getFieldName()+
						"."+
						PortaDelegata.model().ID_SOGGETTO.TIPO.getFieldName());
				String nomeSoggetto = (String) map.get(PortaDelegata.model().ID_SOGGETTO.getBaseField().getFieldName()+
						"."+
						PortaDelegata.model().ID_SOGGETTO.NOME.getFieldName());
		
				IDServizioApplicativo idSA = new IDServizioApplicativo();
				IDSoggetto idSoggetto = new IDSoggetto(tipoSoggetto,nomeSoggetto);
				idSA.setIdSoggettoProprietario(idSoggetto);
				idSA.setNome(nomeSA);
				
				list.add(idSA);
			}	
		}
		
		return list;
	}
	
	
	
	public static List<IDServizioApplicativo> getServiziApplicativiFruitore(JDBCServiceManager manager, String protocollo, 
			String tipoSoggettoFruitore, String nomeSoggettoFruitore) throws Exception{
		List<String> protocolli = null;
		if(protocollo!=null) {
			protocolli = new ArrayList<>();
			protocolli.add(protocollo);
		}
		return getServiziApplicativiFruitore(manager, protocolli, 
				tipoSoggettoFruitore, nomeSoggettoFruitore);
	}
	public static List<IDServizioApplicativo> getServiziApplicativiFruitore(JDBCServiceManager manager, List<String> protocolli, 
			String tipoSoggettoFruitore, String nomeSoggettoFruitore) throws Exception{
		return _getServiziApplicativi(manager, protocolli, 
				tipoSoggettoFruitore, nomeSoggettoFruitore, true);
	}
	
	public static List<IDServizioApplicativo> getServiziApplicativi(JDBCServiceManager manager, String protocollo, 
			String tipoSoggettoProprietario, String nomeSoggettoProprietario) throws Exception{
		List<String> protocolli = null;
		if(protocollo!=null) {
			protocolli = new ArrayList<>();
			protocolli.add(protocollo);
		}
		return getServiziApplicativi(manager, protocolli, 
				tipoSoggettoProprietario, nomeSoggettoProprietario);
	}
	public static List<IDServizioApplicativo> getServiziApplicativi(JDBCServiceManager manager, List<String> protocolli, 
			String tipoSoggettoProprietario, String nomeSoggettoProprietario) throws Exception{
		return _getServiziApplicativi(manager, protocolli, 
				tipoSoggettoProprietario, nomeSoggettoProprietario, false);
	}
	
	private static List<IDServizioApplicativo> _getServiziApplicativi(JDBCServiceManager manager, List<String> protocolli, 
			String tipoSoggettoProprietario, String nomeSoggettoProprietario, boolean onlyClient) throws Exception{
		List<IDServizioApplicativo> list = new ArrayList<IDServizioApplicativo>();
			
		IServizioApplicativoServiceSearch saServiceSearch = manager.getServizioApplicativoServiceSearch();
		IPaginatedExpression pag = saServiceSearch.newPaginatedExpression();
		pag.and();
		
		if(protocolli!=null && protocolli.size()>0){
			List<String> types = new ArrayList<>();
			for (String protocollo : protocolli) {
				types.addAll(ProtocolFactoryReflectionUtils.getOrganizationTypes(protocollo));
			}
			pag.in(ServizioApplicativo.model().ID_SOGGETTO.TIPO, types);
		}
		
		if(tipoSoggettoProprietario!=null && nomeSoggettoProprietario!=null){
			pag.equals(ServizioApplicativo.model().ID_SOGGETTO.TIPO, tipoSoggettoProprietario);
			pag.equals(ServizioApplicativo.model().ID_SOGGETTO.NOME, nomeSoggettoProprietario);
		}
		
		if(onlyClient) {
			// pag.notEquals(ServizioApplicativo.model().TIPOLOGIA_FRUIZIONE, TipologiaFruizione.DISABILITATO.getValue());
			// pag.isNotNull(ServizioApplicativo.model().TIPOLOGIA_FRUIZIONE);
			IExpression exprOr = saServiceSearch.newExpression();
			exprOr.equals(ServizioApplicativo.model().TIPO, CostantiConfigurazione.CLIENT);
			exprOr.or();
			exprOr.equals(ServizioApplicativo.model().AS_CLIENT, CostantiDB.TRUE);
			pag.and(exprOr);
		}
		else {
			IExpression exprOr = saServiceSearch.newExpression();
			exprOr.equals(ServizioApplicativo.model().TIPO, CostantiConfigurazione.CLIENT);
			exprOr.or();
			exprOr.equals(ServizioApplicativo.model().TIPO, CostantiConfigurazione.SERVER);
			pag.and(exprOr);
		}
		
		pag.addOrder(ServizioApplicativo.model().NOME,SortOrder.ASC);
		pag.addOrder(ServizioApplicativo.model().ID_SOGGETTO.NOME,SortOrder.ASC);
		pag.addOrder(ServizioApplicativo.model().ID_SOGGETTO.TIPO,SortOrder.ASC);
		
		List<Map<String, Object>> result = null;
		try{
			result = saServiceSearch.select(pag, true, ServizioApplicativo.model().NOME,
					ServizioApplicativo.model().ID_SOGGETTO.TIPO,
					ServizioApplicativo.model().ID_SOGGETTO.NOME);
		}catch(NotFoundException notFound){}
		if(result!=null && result.size()>0){
			for (Map<String, Object> map : result) {
				
				String nomeSA = (String) map.get(ServizioApplicativo.model().NOME.getFieldName());
				
				String tipoSoggetto = (String) map.get(ServizioApplicativo.model().ID_SOGGETTO.getBaseField().getFieldName()+
						"."+
						PortaDelegata.model().ID_SOGGETTO.TIPO.getFieldName());
				String nomeSoggetto = (String) map.get(ServizioApplicativo.model().ID_SOGGETTO.getBaseField().getFieldName()+
						"."+
						PortaDelegata.model().ID_SOGGETTO.NOME.getFieldName());
		
				IDServizioApplicativo idSA = new IDServizioApplicativo();
				IDSoggetto idSoggetto = new IDSoggetto(tipoSoggetto,nomeSoggetto);
				idSA.setIdSoggettoProprietario(idSoggetto);
				idSA.setNome(nomeSA);
				
				list.add(idSA);
			}	
		}
		
		return list;
	}
	
	
	
	// API
	
	public static AccordoServizioParteComune getAPIImplemented(JDBCServiceManager manager,
			IDSoggetto idSoggetto, String tipoServizio, String nomeServizio, Integer versioneServizio) throws Exception {
		
		IAccordoServizioParteComuneServiceSearch aspcDAO = manager.getAccordoServizioParteComuneServiceSearch();
		IAccordoServizioParteSpecificaServiceSearch aspsDAO = manager.getAccordoServizioParteSpecificaServiceSearch();
		IExpression expr = aspsDAO.newExpression();

		expr.equals(
			AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO,
				idSoggetto.getTipo());
		expr.and()
			.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME,
				idSoggetto.getNome());

		expr.equals(AccordoServizioParteSpecifica.model().TIPO, tipoServizio);
		expr.and();
		expr.equals(AccordoServizioParteSpecifica.model().NOME, nomeServizio);
		expr.and();
		expr.equals(AccordoServizioParteSpecifica.model().VERSIONE, versioneServizio);
					
		IPaginatedExpression pagExpr = aspsDAO
				.toPaginatedExpression(expr);

		CustomField cf = new CustomField("idAccordo", Long.class, "id_accordo", "servizi");

		List<Object> list = aspsDAO.select(pagExpr,true,cf);

		if(list==null || list.size()<0) {
			throw new NotFoundException("NonPresente");
		}
		if(list.size()>1) {
			throw new MultipleResultException("Trovati '"+list.size()+"' api ?");
		}
		
		Long idAccordoLong = (Long) list.get(0);
		return ((JDBCAccordoServizioParteComuneServiceSearch)aspcDAO).get(idAccordoLong);

	}
	
	
	
	
	// Erogazioni
	
	public static List<IDServizio> getErogazioni(JDBCServiceManager manager, List<String> protocolli, 
			String gruppo,
			String tipoSoggetto, String nomeSoggetto) throws Exception {
		return  getErogazioni(manager, protocolli, 
				gruppo,
				tipoSoggetto, nomeSoggetto,
				null, null, null,
				null);
	}
	public static List<IDServizio> getErogazioni(JDBCServiceManager manager, List<String> protocolli, 
			String gruppo,
			String tipoSoggetto, String nomeSoggetto,
			String tipoServizio, String nomeServizio, Integer versioneServizio,
			String nomeAzione) throws Exception {
		List<IDServizio> lista = new ArrayList<IDServizio>();

		IPortaApplicativaServiceSearch portaApplicativaDAO = manager.getPortaApplicativaServiceSearch();
		IAccordoServizioParteComuneGruppoServiceSearch aspcGruppiDAO = null;
		if(gruppo!=null && !"".equals(gruppo)) {
			aspcGruppiDAO = manager.getAccordoServizioParteComuneGruppoServiceSearch();
		}
		
		IExpression pAExpr = getExpressionPA(portaApplicativaDAO, protocolli, 
				tipoSoggetto, nomeSoggetto, 
				tipoServizio, nomeServizio, versioneServizio, 
				nomeAzione);
			 
		List<PortaApplicativa> listaPorte = new ArrayList<PortaApplicativa>();
		IPaginatedExpression pagPaExpr = portaApplicativaDAO.toPaginatedExpression(pAExpr );
		
		pagPaExpr.addOrder(PortaApplicativa.model().ID_SOGGETTO.TIPO, SortOrder.ASC);
		pagPaExpr.addOrder(PortaApplicativa.model().ID_SOGGETTO.NOME, SortOrder.ASC);
		pagPaExpr.addOrder(PortaApplicativa.model().TIPO_SERVIZIO, SortOrder.ASC);
		pagPaExpr.addOrder(PortaApplicativa.model().NOME_SERVIZIO, SortOrder.ASC);
		pagPaExpr.addOrder(PortaApplicativa.model().VERSIONE_SERVIZIO, SortOrder.ASC);
		
		ExpressionProperties.enableSoloDatiIdentificativiServizio(pagPaExpr);
		listaPorte = portaApplicativaDAO.findAll(pagPaExpr);
		
		List<String> lstTmp = new ArrayList<String>();
		for (PortaApplicativa portaApplicativa : listaPorte) {
			IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(portaApplicativa.getTipoServizio(), portaApplicativa.getNomeServizio(),
						portaApplicativa.getIdSoggetto().getTipo(), portaApplicativa.getIdSoggetto().getNome(), portaApplicativa.getVersioneServizio());
			String uriFromIDServizio = IDServizioFactory.getInstance().getUriFromIDServizio(idServizio);
			
			if(gruppo!=null && !"".equals(gruppo)) {
				
				AccordoServizioParteComune aspc =  getAPIImplemented(manager, idServizio.getSoggettoErogatore(), idServizio.getTipo(), idServizio.getNome(), idServizio.getVersione());

				IPaginatedExpression gruppiExpr = aspcGruppiDAO.newPaginatedExpression();
				gruppiExpr.equals(AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, aspc.getNome());
				gruppiExpr.equals(AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, aspc.getVersione());
				gruppiExpr.equals(AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, aspc.getIdReferente().getNome());
				gruppiExpr.equals(AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, aspc.getIdReferente().getTipo());
				List<IdAccordoServizioParteComuneGruppo> lGruppi = aspcGruppiDAO.findAllIds(gruppiExpr);
				boolean found = false;
				if(lGruppi!=null && !lGruppi.isEmpty()) {
					for (IdAccordoServizioParteComuneGruppo gruppoCheck : lGruppi) {
						if(gruppoCheck.getIdGruppo().getNome().equals(gruppo)) {
							found = true;
							break;
						}
					}
				}
				if(!found) {
					continue;
				}
			}
			
			// elimino duplicati
			if(!lstTmp.contains(uriFromIDServizio)) {
				lstTmp.add(uriFromIDServizio);
				lista.add(idServizio);
			}
		}

		return lista;
	}
	
	private static IExpression getExpressionPA(IPortaApplicativaServiceSearch portaApplicativaDAO, List<String> protocolli, 
			String tipoSoggetto, String nomeSoggetto, 
			String tipoServizio, String nomeServizio, Integer versioneServizio,
			String nomeAzione) throws Exception {
		
		IExpression paExpr = portaApplicativaDAO.newExpression();
		

		if(StringUtils.isNotEmpty(tipoSoggetto ))
			paExpr.equals(PortaApplicativa.model().ID_SOGGETTO.TIPO, tipoSoggetto);
		if(StringUtils.isNotEmpty(nomeSoggetto ))
			paExpr.and().equals(PortaApplicativa.model().ID_SOGGETTO.NOME, nomeSoggetto);
		
		// Se ho selezionato un servizio, oppure se non ho selezionato niente 
		if(StringUtils.isNotEmpty(nomeServizio ))
			paExpr.equals(PortaApplicativa.model().NOME_SERVIZIO, nomeServizio).and();
		if(StringUtils.isNotEmpty(tipoServizio ))
			paExpr.equals(PortaApplicativa.model().TIPO_SERVIZIO, tipoServizio).and();
		if(versioneServizio != null)
			paExpr.equals(PortaApplicativa.model().VERSIONE_SERVIZIO, versioneServizio).and();

		if(StringUtils.isNotEmpty(nomeAzione )){
			IExpression azioneExpr =  portaApplicativaDAO.newExpression();
			azioneExpr.equals(PortaApplicativa.model().NOME_AZIONE, nomeAzione).or().isNull(PortaApplicativa.model().NOME_AZIONE); 
			paExpr.and(azioneExpr);
		}

		if(protocolli!=null && protocolli.size()>0){
			List<String> types = new ArrayList<>();
			for (String protocollo : protocolli) {
				types.addAll(ProtocolFactoryReflectionUtils.getOrganizationTypes(protocollo));
			}
			paExpr.in(PortaApplicativa.model().ID_SOGGETTO.TIPO, types);
		}
	
		paExpr.notEquals(PortaApplicativa.model().MODE_AZIONE, org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione.DELEGATED_BY);
		return paExpr;
	}
	
	
	
	
	// Fruizioni
	
	public static List<IDFruizione> getFruizioni(JDBCServiceManager manager, List<String> protocolli, 
			String gruppo,
			String tipoSoggetto, String nomeSoggetto) throws Exception {
		return getFruizioni(manager, protocolli, 
				gruppo,
				tipoSoggetto, nomeSoggetto, 
				null, null,
				null ,null, null, 
				null);
	}
	public static List<IDFruizione> getFruizioni(JDBCServiceManager manager, List<String> protocolli, 
			String gruppo,
			String tipoSoggetto, String nomeSoggetto, 
			String tipoErogatore, String nomeErogatore,
			String tipoServizio ,String nomeServizio, Integer versioneServizio, 
			String nomeAzione) throws Exception {
		List<IDFruizione> lista = new ArrayList<IDFruizione>();

		IPortaDelegataServiceSearch portaDelegataDAO = manager.getPortaDelegataServiceSearch();
		IAccordoServizioParteComuneGruppoServiceSearch aspcGruppiDAO = null;
		if(gruppo!=null && !"".equals(gruppo)) {
			aspcGruppiDAO = manager.getAccordoServizioParteComuneGruppoServiceSearch();
		}
		
		IExpression pdExpr = getExpressionPD(portaDelegataDAO, protocolli, 
				tipoSoggetto, nomeSoggetto, 
				tipoErogatore, nomeErogatore,	
				tipoServizio, nomeServizio, versioneServizio, nomeAzione);
		
		List<PortaDelegata> listaPorte = new ArrayList<PortaDelegata>();
		IPaginatedExpression pagPdExpr = portaDelegataDAO.toPaginatedExpression(pdExpr );
		
		pagPdExpr.addOrder(PortaDelegata.model().ID_SOGGETTO.TIPO, SortOrder.ASC);
		pagPdExpr.addOrder(PortaDelegata.model().ID_SOGGETTO.NOME, SortOrder.ASC);
		pagPdExpr.addOrder(PortaDelegata.model().TIPO_SOGGETTO_EROGATORE, SortOrder.ASC);
		pagPdExpr.addOrder(PortaDelegata.model().NOME_SOGGETTO_EROGATORE, SortOrder.ASC);
		pagPdExpr.addOrder(PortaDelegata.model().TIPO_SERVIZIO, SortOrder.ASC);
		pagPdExpr.addOrder(PortaDelegata.model().NOME_SERVIZIO, SortOrder.ASC);
		pagPdExpr.addOrder(PortaDelegata.model().VERSIONE_SERVIZIO, SortOrder.ASC);
		
		ExpressionProperties.enableSoloDatiIdentificativiServizio(pagPdExpr);
		
		listaPorte = portaDelegataDAO.findAll(pagPdExpr);
		
		List<String> lstTmp = new ArrayList<String>();
		for (PortaDelegata portaDelegata : listaPorte) {
			IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(portaDelegata.getTipoServizio(), portaDelegata.getNomeServizio(), portaDelegata.getTipoSoggettoErogatore(), portaDelegata.getNomeSoggettoErogatore(), portaDelegata.getVersioneServizio());
			IDSoggetto idFruitore = new IDSoggetto(portaDelegata.getIdSoggetto().getTipo(), portaDelegata.getIdSoggetto().getNome());
			IDFruizione idFruizione = new IDFruizione();
			idFruizione.setIdServizio(idServizio);
			idFruizione.setIdFruitore(idFruitore);
			String uriFromIDServizio = IDServizioFactory.getInstance().getUriFromIDServizio(idServizio);
			String uriFruizione = idFruitore.toString()+"_"+uriFromIDServizio;
			
			if(gruppo!=null && !"".equals(gruppo)) {
				
				AccordoServizioParteComune aspc =  getAPIImplemented(manager, idServizio.getSoggettoErogatore(), idServizio.getTipo(), idServizio.getNome(), idServizio.getVersione());

				IPaginatedExpression gruppiExpr = aspcGruppiDAO.newPaginatedExpression();
				gruppiExpr.equals(AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, aspc.getNome());
				gruppiExpr.equals(AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, aspc.getVersione());
				gruppiExpr.equals(AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, aspc.getIdReferente().getNome());
				gruppiExpr.equals(AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, aspc.getIdReferente().getTipo());
				List<IdAccordoServizioParteComuneGruppo> lGruppi = aspcGruppiDAO.findAllIds(gruppiExpr);
				boolean found = false;
				if(lGruppi!=null && !lGruppi.isEmpty()) {
					for (IdAccordoServizioParteComuneGruppo gruppoCheck : lGruppi) {
						if(gruppoCheck.getIdGruppo().getNome().equals(gruppo)) {
							found = true;
							break;
						}
					}
				}
				if(!found) {
					continue;
				}
			}
			
			// elimino duplicati
			if(!lstTmp.contains(uriFruizione)) {
				lstTmp.add(uriFruizione);
				lista.add(idFruizione);
			}
		}

		return lista;
	}
	
	private static IExpression getExpressionPD(IPortaDelegataServiceSearch portaDelegataDAO, List<String> protocolli, 
			String tipoSoggetto, String nomeSoggetto, 
			String tipoErogatore, String nomeErogatore, 
			String tipoServizio, String nomeServizio, Integer versioneServizio,
			String nomeAzione) throws Exception {
		
		IExpression pdExpr = portaDelegataDAO.newExpression();

		// Se ho selezionato un servizio, oppure se non ho selezionato niente 
		if(StringUtils.isNotEmpty(tipoSoggetto ))
			pdExpr.equals(PortaDelegata.model().ID_SOGGETTO.TIPO, tipoSoggetto);
		if(StringUtils.isNotEmpty(nomeSoggetto ))
			pdExpr.and().equals(PortaDelegata.model().ID_SOGGETTO.NOME, nomeSoggetto);
		
		if(StringUtils.isNotEmpty(nomeErogatore ))
			pdExpr.equals(PortaDelegata.model().NOME_SOGGETTO_EROGATORE, nomeErogatore).and();
		if(StringUtils.isNotEmpty(tipoErogatore ))
			pdExpr.equals(PortaDelegata.model().TIPO_SOGGETTO_EROGATORE, tipoErogatore).and();

		if(StringUtils.isNotEmpty(tipoServizio ))
			pdExpr.equals(PortaDelegata.model().TIPO_SERVIZIO,		tipoServizio);
		if(StringUtils.isNotEmpty(nomeServizio ))
			pdExpr.and().equals(PortaDelegata.model().NOME_SERVIZIO, nomeServizio);
		if(versioneServizio != null)
			pdExpr.equals(PortaDelegata.model().VERSIONE_SERVIZIO, versioneServizio).and();
		
		if(StringUtils.isNotEmpty(nomeAzione )){
			IExpression azioneExpr =  portaDelegataDAO.newExpression();
			azioneExpr.equals(PortaDelegata.model().NOME_AZIONE, nomeAzione).or().isNull(PortaDelegata.model().NOME_AZIONE); 
			pdExpr.and(azioneExpr);
		}
		
		if(protocolli!=null && protocolli.size()>0){
			List<String> types = new ArrayList<>();
			for (String protocollo : protocolli) {
				types.addAll(ProtocolFactoryReflectionUtils.getOrganizationTypes(protocollo));
			}
			pdExpr.in(PortaDelegata.model().ID_SOGGETTO.TIPO, types);
		}
		
		pdExpr.notEquals(PortaDelegata.model().MODE_AZIONE, org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione.DELEGATED_BY);
		return pdExpr;
	}
	
}
