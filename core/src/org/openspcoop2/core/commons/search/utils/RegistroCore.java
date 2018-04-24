/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.ProtocolFactoryReflectionUtils;
import org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione;
import org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica;
import org.openspcoop2.core.commons.search.Fruitore;
import org.openspcoop2.core.commons.search.Operation;
import org.openspcoop2.core.commons.search.PortaApplicativa;
import org.openspcoop2.core.commons.search.PortaDelegata;
import org.openspcoop2.core.commons.search.dao.IAccordoServizioParteComuneAzioneServiceSearch;
import org.openspcoop2.core.commons.search.dao.IAccordoServizioParteSpecificaServiceSearch;
import org.openspcoop2.core.commons.search.dao.IFruitoreServiceSearch;
import org.openspcoop2.core.commons.search.dao.IOperationServiceSearch;
import org.openspcoop2.core.commons.search.dao.IPortaApplicativaServiceSearch;
import org.openspcoop2.core.commons.search.dao.IPortaDelegataServiceSearch;
import org.openspcoop2.core.commons.search.dao.jdbc.JDBCServiceManager;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.slf4j.Logger;

/***
 * 
 * 
 * 
 * @author pintori
 *
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

	public static List<String> getProtocolli() throws Exception{
		return ProtocolFactoryReflectionUtils.getProtocolli();
	}
	
	public static List<String> getSoggettiFruitori(JDBCServiceManager manager, String protocollo, String tipoSoggettoErogatore,String nomeSoggettoErogatore, String tipoServizio, String nomeServizio) throws Exception{
		List<String> list = new ArrayList<String>();
		
		IFruitoreServiceSearch fruitoreServiceSearch = manager.getFruitoreServiceSearch();
		IPaginatedExpression pag = fruitoreServiceSearch.newPaginatedExpression();
		pag.and();
		
		if(protocollo!=null){
			pag.in(Fruitore.model().ID_FRUITORE.TIPO, ProtocolFactoryReflectionUtils.getOrganizationTypes(protocollo));
		}
		if(tipoSoggettoErogatore!=null && nomeSoggettoErogatore!=null){
			pag.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE.TIPO, tipoSoggettoErogatore);
			pag.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.ID_EROGATORE.NOME, nomeSoggettoErogatore);
		}
		if(tipoServizio!=null && nomeServizio!=null){
			pag.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.TIPO, tipoServizio);
			pag.equals(Fruitore.model().ID_ACCORDO_SERVIZIO_PARTE_SPECIFICA.NOME, nomeServizio);
		}
		
		pag.addOrder(Fruitore.model().ID_FRUITORE.TIPO,SortOrder.ASC);
		pag.addOrder(Fruitore.model().ID_FRUITORE.NOME,SortOrder.ASC);
		
		List<Map<String, Object>> result = null;
		try{
			result = fruitoreServiceSearch.select(pag, true, Fruitore.model().ID_FRUITORE.TIPO, Fruitore.model().ID_FRUITORE.NOME);
		}catch(NotFoundException notFound){}
		if(result!=null && result.size()>0){
			for (Map<String, Object> map : result) {
				String tipo = (String) map.get(Fruitore.model().ID_FRUITORE.getBaseField().getFieldName()+"."+Fruitore.model().ID_FRUITORE.TIPO.getFieldName());
				String nome = (String) map.get(Fruitore.model().ID_FRUITORE.getBaseField().getFieldName()+"."+Fruitore.model().ID_FRUITORE.NOME.getFieldName());
				list.add(tipo+"/"+nome);
			}
		}
		
		return list;
	}
	
	public static List<String> getSoggettiErogatori(JDBCServiceManager manager, String protocollo) throws Exception{
		List<String> list = new ArrayList<String>();
		
		IAccordoServizioParteSpecificaServiceSearch aspsServiceSearch = manager.getAccordoServizioParteSpecificaServiceSearch();
		IPaginatedExpression pag = aspsServiceSearch.newPaginatedExpression();
		pag.and();
		
		if(protocollo!=null){
			pag.in(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, ProtocolFactoryReflectionUtils.getOrganizationTypes(protocollo));
		}

		pag.addOrder(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO,SortOrder.ASC);
		pag.addOrder(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME,SortOrder.ASC);
		
		List<Map<String, Object>> result = null;
		try{
			result = aspsServiceSearch.select(pag, true, AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME);
		}catch(NotFoundException notFound){}
		if(result!=null && result.size()>0){
			for (Map<String, Object> map : result) {
				String tipo = (String) map.get(AccordoServizioParteSpecifica.model().ID_EROGATORE.getBaseField().getFieldName()+"."+AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO.getFieldName());
				String nome = (String) map.get(AccordoServizioParteSpecifica.model().ID_EROGATORE.getBaseField().getFieldName()+"."+AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME.getFieldName());
				list.add(tipo+"/"+nome);
			}
		}
		
		return list;
	}
	
	public static List<String> getServizi(JDBCServiceManager manager, String protocollo, String tipoSoggettoErogatore, String nomeSoggettoErogatore) throws Exception{
		List<String> list = new ArrayList<String>();
		
		IAccordoServizioParteSpecificaServiceSearch aspsServiceSearch = manager.getAccordoServizioParteSpecificaServiceSearch();
		IPaginatedExpression pag = aspsServiceSearch.newPaginatedExpression();
		pag.and();
		
		if(protocollo!=null){
			pag.in(AccordoServizioParteSpecifica.model().TIPO, ProtocolFactoryReflectionUtils.getServiceTypes(protocollo));
		}
		if(tipoSoggettoErogatore!=null && nomeSoggettoErogatore!=null){
			pag.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoSoggettoErogatore);
			pag.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME, nomeSoggettoErogatore);
		}
		
		pag.addOrder(AccordoServizioParteSpecifica.model().TIPO,SortOrder.ASC);
		pag.addOrder(AccordoServizioParteSpecifica.model().NOME,SortOrder.ASC);
		
		List<Map<String, Object>> result = null;
		try{
			result = aspsServiceSearch.select(pag, true, AccordoServizioParteSpecifica.model().TIPO, AccordoServizioParteSpecifica.model().NOME);
		}catch(NotFoundException notFound){}
		if(result!=null && result.size()>0){
			for (Map<String, Object> map : result) {
				String tipo = (String) map.get(AccordoServizioParteSpecifica.model().TIPO.getFieldName());
				String nome = (String) map.get(AccordoServizioParteSpecifica.model().NOME.getFieldName());
				list.add(tipo+"/"+nome);
			}
		}
		
		return list;
	}
	
	public static List<String> getAzioni(JDBCServiceManager manager, String protocollo, String tipoSoggettoErogatore, String nomeSoggettoErogatore,	String tipoServizio, String nomeServizio) throws Exception{
		List<String> list = new ArrayList<String>();
			
//			if(tipoServizio==null || nomeServizio==null){
//				return list;
//			}
			
		// Localizzo Accordi di Servizio Parte Comune dalle Parti Specifiche
		List<IDPortType> idPortTypes = new ArrayList<IDPortType>();
		IAccordoServizioParteSpecificaServiceSearch aspsServiceSearch = manager.getAccordoServizioParteSpecificaServiceSearch();
		IPaginatedExpression pag = aspsServiceSearch.newPaginatedExpression();
		pag.and();
		if(protocollo!=null){
			pag.in(AccordoServizioParteSpecifica.model().TIPO, ProtocolFactoryReflectionUtils.getServiceTypes(protocollo));
		}
		if(tipoSoggettoErogatore!=null && nomeSoggettoErogatore!=null){
			pag.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO, tipoSoggettoErogatore);
			pag.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME, nomeSoggettoErogatore);
		}
		if(tipoServizio!=null && nomeServizio!=null){
			pag.equals(AccordoServizioParteSpecifica.model().TIPO, tipoServizio);
			pag.equals(AccordoServizioParteSpecifica.model().NOME, nomeServizio);
		}		
		List<Map<String, Object>> result = null;
		try{
			result = aspsServiceSearch.select(pag, true, AccordoServizioParteSpecifica.model().PORT_TYPE, 
					AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME,
					AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO,
					AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME,
					AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE);
		}catch(NotFoundException notFound){}
		if(result!=null && result.size()>0){
			for (Map<String, Object> map : result) {
				
				IDPortType idPortType = new IDPortType();
				
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
				
				IDSoggetto idSoggettoReferente = null;
				if(tipoSoggettoReferenteAccordo!=null && (tipoSoggettoReferenteAccordo instanceof String) &&
						nomeSoggettoReferenteAccordo!=null && (nomeSoggettoReferenteAccordo instanceof String)){
					idSoggettoReferente = new IDSoggetto((String)tipoSoggettoReferenteAccordo, (String)nomeSoggettoReferenteAccordo);
				}
				String v = null;
				if(versioneAccordo!=null && (versioneAccordo instanceof String)){
					v = (String) versioneAccordo;
				} 
				Integer versione = null;
				if(v != null) {
					try {
						versione = Integer.parseInt(v); 
					}catch(Exception e) {}
				}
				
				idPortType.setIdAccordo(IDAccordoFactory.getInstance().getIDAccordoFromValues(nomeAccordo, idSoggettoReferente, versione));
				
				idPortTypes.add(idPortType);
			}
		}
		
		// Localizzo Azioni dagli Accordi Implementati
		if(idPortTypes!=null && idPortTypes.size()>0){
			for (IDPortType idPortType : idPortTypes) {
				
				if(idPortType.getNome()!=null){
					
					IOperationServiceSearch portTypeAzioneServiceSearch = manager.getOperationServiceSearch();
					IPaginatedExpression pagAzioni = portTypeAzioneServiceSearch.newPaginatedExpression();
					pagAzioni.and();
					pagAzioni.equals(Operation.model().ID_PORT_TYPE.NOME, idPortType.getNome());
					pagAzioni.equals(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, idPortType.getIdAccordo().getNome());
					if(idPortType.getIdAccordo().getVersione()!=null){
						pagAzioni.equals(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, idPortType.getIdAccordo().getVersione());
					}
					if(idPortType.getIdAccordo().getSoggettoReferente()!=null){
						pagAzioni.equals(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, 
								idPortType.getIdAccordo().getSoggettoReferente().getTipo());
						pagAzioni.equals(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, 
								idPortType.getIdAccordo().getSoggettoReferente().getNome());
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
							}
						}
					}
					
				}
				else{
					IAccordoServizioParteComuneAzioneServiceSearch aspcAzioneServiceSearch = manager.getAccordoServizioParteComuneAzioneServiceSearch();
					IPaginatedExpression pagAzioni = aspcAzioneServiceSearch.newPaginatedExpression();
					pagAzioni.and();
					pagAzioni.equals(AccordoServizioParteComuneAzione.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, idPortType.getIdAccordo().getNome());
					if(idPortType.getIdAccordo().getVersione()!=null){
						pagAzioni.equals(AccordoServizioParteComuneAzione.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, idPortType.getIdAccordo().getVersione());
					}
					if(idPortType.getIdAccordo().getSoggettoReferente()!=null){
						pagAzioni.equals(AccordoServizioParteComuneAzione.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, 
								idPortType.getIdAccordo().getSoggettoReferente().getTipo());
						pagAzioni.equals(AccordoServizioParteComuneAzione.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, 
								idPortType.getIdAccordo().getSoggettoReferente().getNome());
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
							}
						}
					}
				}
				
			}
		}
		
		if(list!=null && list.size()>0){
			Collections.sort(list);
		}
		
		return list;
	}
	
	public static List<String> getServiziApplicativiErogatori(JDBCServiceManager manager, String protocollo, String tipoSoggettoErogatore, String nomeSoggettoErogatore, String tipoServizio, String nomeServizio,	String azione) throws Exception{
		List<String> list = new ArrayList<String>();
		
		IPortaApplicativaServiceSearch paServiceSearch = manager.getPortaApplicativaServiceSearch();
		IPaginatedExpression pag = paServiceSearch.newPaginatedExpression();
		pag.and();
		
		if(protocollo!=null){
			pag.in(PortaApplicativa.model().ID_SOGGETTO.TIPO, ProtocolFactoryReflectionUtils.getOrganizationTypes(protocollo));
		}
		
		if(tipoSoggettoErogatore!=null && nomeSoggettoErogatore!=null){
			pag.equals(PortaApplicativa.model().ID_SOGGETTO.TIPO, tipoSoggettoErogatore);
			pag.equals(PortaApplicativa.model().ID_SOGGETTO.NOME, nomeSoggettoErogatore);
		}
		
		if(tipoServizio!=null && nomeServizio!=null){
			pag.equals(PortaApplicativa.model().TIPO_SERVIZIO, tipoServizio);
			pag.equals(PortaApplicativa.model().NOME_SERVIZIO, nomeServizio);
		}
		
		if(azione!=null){
			IExpression azioneExpr = paServiceSearch.newExpression();
			azioneExpr.or();
			azioneExpr.equals(PortaApplicativa.model().NOME_AZIONE, azione);
			azioneExpr.isNull(PortaApplicativa.model().NOME_AZIONE);
			azioneExpr.isEmpty(PortaApplicativa.model().NOME_AZIONE);
			pag.and(azioneExpr);
		}
		
		pag.addOrder(PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.NOME,SortOrder.ASC);
		
		List<Object> result = null;
		try{
			result = paServiceSearch.select(pag, true, PortaApplicativa.model().PORTA_APPLICATIVA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.NOME);
		}catch(NotFoundException notFound){}
		if(result!=null && result.size()>0){
			for (Object sa : result) {
				String nome = (String) sa;
				list.add(nome);
			}
		}
		
		return list;
	}
	
	
	public static List<String> getServiziApplicativiFruitore(JDBCServiceManager manager, String protocollo, String tipoSoggettoFruitore, String nomeSoggettoFruitore, 
			String tipoSoggettoErogatore, String nomeSoggettoErogatore, String tipoServizio, String nomeServizio, String azione) throws Exception{
		List<String> list = new ArrayList<String>();
			
		IPortaDelegataServiceSearch pdServiceSearch = manager.getPortaDelegataServiceSearch();
		IPaginatedExpression pag = pdServiceSearch.newPaginatedExpression();
		pag.and();
		
		if(protocollo!=null){
			pag.in(PortaDelegata.model().ID_SOGGETTO.TIPO, ProtocolFactoryReflectionUtils.getOrganizationTypes(protocollo));
		}
		
		if(tipoSoggettoFruitore!=null && nomeSoggettoFruitore!=null){
			pag.equals(PortaDelegata.model().ID_SOGGETTO.TIPO, tipoSoggettoFruitore);
			pag.equals(PortaDelegata.model().ID_SOGGETTO.NOME, nomeSoggettoFruitore);
		}
		
		if(tipoSoggettoErogatore!=null && nomeSoggettoErogatore!=null){
			pag.equals(PortaDelegata.model().TIPO_SOGGETTO_EROGATORE, tipoSoggettoErogatore);
			
			// Il Soggetto Erogatore può essere null se abbiamo modalità di riconoscimento differente da static
			IExpression erogatoreExpr = pdServiceSearch.newExpression();
			erogatoreExpr.or();
			erogatoreExpr.equals(PortaDelegata.model().NOME_SOGGETTO_EROGATORE, nomeSoggettoErogatore);
			erogatoreExpr.isNull(PortaDelegata.model().NOME_SOGGETTO_EROGATORE);
			erogatoreExpr.isEmpty(PortaDelegata.model().NOME_SOGGETTO_EROGATORE);
			pag.and(erogatoreExpr);
		}
		
		if(tipoServizio!=null && nomeServizio!=null){
			pag.equals(PortaDelegata.model().TIPO_SERVIZIO, tipoServizio);

			// Il Servizio può essere null se abbiamo modalità di riconoscimento differente da static
			IExpression servizioExpr = pdServiceSearch.newExpression();
			servizioExpr.or();
			servizioExpr.equals(PortaDelegata.model().NOME_SERVIZIO, nomeServizio);
			servizioExpr.isNull(PortaDelegata.model().NOME_SERVIZIO);
			servizioExpr.isEmpty(PortaDelegata.model().NOME_SERVIZIO);
			pag.and(servizioExpr);
		}
		
		if(azione!=null){
			// L'azione può essere null se abbiamo modalità di riconoscimento differente da static
			IExpression azioneExpr = pdServiceSearch.newExpression();
			azioneExpr.or();
			azioneExpr.equals(PortaDelegata.model().NOME_AZIONE, azione);
			azioneExpr.isNull(PortaDelegata.model().NOME_AZIONE);
			azioneExpr.isEmpty(PortaDelegata.model().NOME_AZIONE);
			pag.and(azioneExpr);
		}
		
		pag.addOrder(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.NOME,SortOrder.ASC);
		
		List<Object> result = null;
		try{
			result = pdServiceSearch.select(pag, true, PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.NOME);
		}catch(NotFoundException notFound){}
		if(result!=null && result.size()>0){
			for (Object sa : result) {
				String nome = (String) sa;
				list.add(nome);
			}
		}
		
		return list;
	}
}
