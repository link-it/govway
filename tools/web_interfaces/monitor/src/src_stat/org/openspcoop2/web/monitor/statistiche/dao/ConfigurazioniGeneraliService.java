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
package org.openspcoop2.web.monitor.statistiche.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.core.commons.search.AccordoServizioParteComune;
import org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo;
import org.openspcoop2.core.commons.search.PortaApplicativa;
import org.openspcoop2.core.commons.search.PortaDelegata;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.commons.search.constants.TipoPdD;
import org.openspcoop2.core.commons.search.dao.IDBPortaApplicativaServiceSearch;
import org.openspcoop2.core.commons.search.dao.IDBPortaDelegataServiceSearch;
import org.openspcoop2.core.commons.search.dao.IPortaApplicativaServiceSearch;
import org.openspcoop2.core.commons.search.dao.IPortaDelegataServiceSearch;
import org.openspcoop2.core.commons.search.model.PortaApplicativaModel;
import org.openspcoop2.core.commons.search.model.PortaDelegataModel;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazione;
import org.openspcoop2.core.config.constants.RuoloContesto;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.IdPolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.controllo_traffico.dao.IAttivazionePolicyServiceSearch;
import org.openspcoop2.core.controllo_traffico.dao.IConfigurazionePolicyServiceSearch;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.id.IdentificativiErogazione;
import org.openspcoop2.core.id.IdentificativiFruizione;
import org.openspcoop2.core.mapping.DBMappingUtils;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.dao.IServiceSearchWithId;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.pdd.config.UrlInvocazioneAPI;
import org.openspcoop2.pdd.core.autorizzazione.canali.CanaliUtils;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.utils.ModIUtils;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.utils.PorteNamingUtils;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.dao.DynamicUtilsService;
import org.openspcoop2.web.monitor.core.dao.DynamicUtilsServiceEngine;
import org.openspcoop2.web.monitor.core.dao.IDynamicUtilsService;
import org.openspcoop2.web.monitor.core.exception.UserInvalidException;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.DynamicPdDBeanUtils;
import org.openspcoop2.web.monitor.core.utils.ParseUtility;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioneGenerale;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioneGeneralePK;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioniGeneraliSearchForm;
import org.openspcoop2.web.monitor.statistiche.bean.DettaglioPA;
import org.openspcoop2.web.monitor.statistiche.bean.DettaglioPD;
import org.openspcoop2.web.monitor.statistiche.bean.DettaglioRateLimiting;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiConfigurazioni;
import org.openspcoop2.web.monitor.statistiche.utils.ConfigurazioniUtils;
import org.slf4j.Logger;

/**
 * ConfigurazioniGeneraliService
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ConfigurazioniGeneraliService implements IConfigurazioniGeneraliService {

	private ConfigurazioniGeneraliSearchForm search = null;

	private static Logger log =  LoggerManager.getPddMonitorSqlLogger();

	private transient IDynamicUtilsService dynamicService = null;

	private transient org.openspcoop2.core.commons.search.dao.IServiceManager utilsServiceManager;
	private transient org.openspcoop2.core.plugins.dao.IServiceManager pluginsBaseServiceManager;
	private transient org.openspcoop2.core.controllo_traffico.dao.IServiceManager controlloTrafficoServiceManager;

	private IPortaDelegataServiceSearch portaDelegataDAO = null;
	private IPortaApplicativaServiceSearch portaApplicativaDAO  = null;
	private IAttivazionePolicyServiceSearch attivazionePolicyDAO = null;
	private IConfigurazionePolicyServiceSearch configurazionePolicyDAO = null;

	private transient DriverConfigurazioneDB driverConfigDB = null;
	private transient DriverRegistroServiziDB driverRegistroDB = null;

	private ConfigurazioneUrlInvocazione configurazioneUrlInvocazione;
	
	protected DynamicPdDBeanUtils dynamicUtils = null;
	
	public ConfigurazioniGeneraliService(){
		try{
			this.dynamicService = new DynamicUtilsService();
			
			this.utilsServiceManager = (org.openspcoop2.core.commons.search.dao.IServiceManager) DAOFactory
					.getInstance( ConfigurazioniGeneraliService.log).getServiceManager(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance(), ConfigurazioniGeneraliService.log);
		
			this.pluginsBaseServiceManager = (org.openspcoop2.core.plugins.dao.IServiceManager) DAOFactory
					.getInstance( ConfigurazioniGeneraliService.log).getServiceManager(org.openspcoop2.core.plugins.utils.ProjectInfo.getInstance(),ConfigurazioniGeneraliService.log);
			
			this.controlloTrafficoServiceManager = (org.openspcoop2.core.controllo_traffico.dao.IServiceManager) DAOFactory
					.getInstance( ConfigurazioniGeneraliService.log).getServiceManager(org.openspcoop2.core.controllo_traffico.utils.ProjectInfo.getInstance(), ConfigurazioniGeneraliService.log);
			
			this._init(null, null);
			
		}catch(Exception e){
			ConfigurazioniGeneraliService.log.error("Errore durante la creazione del Service: " + e.getMessage(),e);
		}
	}
	public ConfigurazioniGeneraliService(Connection con, boolean autoCommit){
		this(con, autoCommit, null, ConfigurazioniGeneraliService.log);
	}
	public ConfigurazioniGeneraliService(Connection con, boolean autoCommit, Logger log){
		this(con, autoCommit, null, log);
	}
	public ConfigurazioniGeneraliService(Connection con, boolean autoCommit, ServiceManagerProperties serviceManagerProperties){
		this(con, autoCommit, serviceManagerProperties, ConfigurazioniGeneraliService.log);
	}
	public ConfigurazioniGeneraliService(Connection con, boolean autoCommit, ServiceManagerProperties serviceManagerProperties, Logger log){
		try{
			this.dynamicService = new DynamicUtilsService(con,autoCommit,serviceManagerProperties,log);
			
			this.utilsServiceManager = (org.openspcoop2.core.commons.search.dao.IServiceManager) DAOFactory
					.getInstance( ConfigurazioniGeneraliService.log).getServiceManager(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance(), con,autoCommit,serviceManagerProperties,log);
		
			this.pluginsBaseServiceManager = (org.openspcoop2.core.plugins.dao.IServiceManager) DAOFactory
					.getInstance( ConfigurazioniGeneraliService.log).getServiceManager(org.openspcoop2.core.plugins.utils.ProjectInfo.getInstance(), con,autoCommit,serviceManagerProperties,log);
			
			this.controlloTrafficoServiceManager = (org.openspcoop2.core.controllo_traffico.dao.IServiceManager) DAOFactory
					.getInstance( ConfigurazioniGeneraliService.log).getServiceManager(org.openspcoop2.core.controllo_traffico.utils.ProjectInfo.getInstance(), con,autoCommit,serviceManagerProperties, ConfigurazioniGeneraliService.log);
			
			this._init(con, serviceManagerProperties);
			
		}catch(Exception e){
			ConfigurazioniGeneraliService.log.error("Errore durante la creazione del Service: " + e.getMessage(),e);
		}
	}
	private void _init(Connection con, ServiceManagerProperties serviceManagerProperties) {
		try{
			this.portaApplicativaDAO = this.utilsServiceManager.getPortaApplicativaServiceSearch();
			this.portaDelegataDAO = this.utilsServiceManager.getPortaDelegataServiceSearch();
			
			this.attivazionePolicyDAO = this.controlloTrafficoServiceManager.getAttivazionePolicyServiceSearch();
			this.configurazionePolicyDAO = this.controlloTrafficoServiceManager.getConfigurazionePolicyServiceSearch();

			String tipoDatabase = null;
			if(serviceManagerProperties!=null) {
				tipoDatabase = serviceManagerProperties.getDatabaseType();
			}
			else {
				tipoDatabase = DAOFactoryProperties.getInstance(ConfigurazioniGeneraliService.log).getTipoDatabase(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
			}
			
			if(con!=null) {
				this.driverConfigDB = new DriverConfigurazioneDB(con, ConfigurazioniGeneraliService.log, tipoDatabase);
				this.driverRegistroDB = new DriverRegistroServiziDB(con, ConfigurazioniGeneraliService.log, tipoDatabase);
			}
			else {
				String datasourceJNDIName = DAOFactoryProperties.getInstance(ConfigurazioniGeneraliService.log).getDatasourceJNDIName(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
				Properties datasourceJNDIContext = DAOFactoryProperties.getInstance(ConfigurazioniGeneraliService.log).getDatasourceJNDIContext(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
	
				this.driverConfigDB = new DriverConfigurazioneDB(datasourceJNDIName,datasourceJNDIContext, ConfigurazioniGeneraliService.log, tipoDatabase);
				this.driverRegistroDB = new DriverRegistroServiziDB(datasourceJNDIName,datasourceJNDIContext, ConfigurazioniGeneraliService.log, tipoDatabase);
			}

			Configurazione config = this.driverConfigDB.getConfigurazioneGenerale();
			if(config!=null && config.getUrlInvocazione()!=null) {
				this.configurazioneUrlInvocazione = config.getUrlInvocazione();
			}
			
			this.dynamicUtils = new DynamicPdDBeanUtils(this.utilsServiceManager, this.pluginsBaseServiceManager,
					this.driverRegistroDB, this.driverConfigDB,
					log);

		}catch(Exception e){
			ConfigurazioniGeneraliService.log.error("Errore durante la creazione del Service: " + e.getMessage(),e);
		}
	}

	@Override
	public List<ConfigurazioneGenerale> findAll() {
		ConfigurazioniGeneraliService.log.debug("Metodo FindAll");

		try {
			
			String tipoProtocollo =this.search.getProtocollo();
			
			String tipoSoggetto = this.search.getTipoSoggettoLocale();
			String nomeSoggetto = this.search.getSoggettoLocale();
			
			String input = null;
			
			String gruppo = this.search.getGruppo();
			
			boolean apiImplSelected = StringUtils.isNotBlank(this.search.getNomeServizio());
			
			IDAccordo idAccordo = null;
			IDAccordoFactory idAccordoFactory = null;
			String api = this.search.getApi();
			if( !apiImplSelected && (api!=null && !"".equals(api)) ) {
				idAccordoFactory = IDAccordoFactory.getInstance();
				idAccordo = idAccordoFactory.getIDAccordoFromUri(api);
			}
			
			List<IDServizio> listIDServizio = getListServizi(apiImplSelected, gruppo, idAccordo,
					tipoProtocollo, tipoSoggetto, nomeSoggetto,
					input);

			if(PddRuolo.DELEGATA.equals(this.search.getTipologiaTransazioni())) {
				IExpression expr = this.createPDExpression(this.portaDelegataDAO, this.search, false);
				IPaginatedExpression pagExpr = this.portaDelegataDAO.toPaginatedExpression(expr);

				setListServiziPD(apiImplSelected, listIDServizio, pagExpr);

				List<PortaDelegata> findAll = this.portaDelegataDAO.findAll(pagExpr);
				if(findAll != null && findAll.size() > 0){
					List<ConfigurazioneGenerale> lst = new ArrayList<ConfigurazioneGenerale>();

					for (PortaDelegata portaDelegata : findAll) {
						lst.add(this.fromPD(portaDelegata));
					}
					return lst;
				}
			} else {
				IExpression expr = this.createPAExpression(this.portaApplicativaDAO, this.search, false);
				IPaginatedExpression pagExpr = this.portaApplicativaDAO.toPaginatedExpression(expr);

				setListServiziPA(apiImplSelected, listIDServizio, pagExpr);
				
				List<PortaApplicativa> findAll = this.portaApplicativaDAO.findAll(pagExpr);
				if(findAll != null && findAll.size() > 0){
					List<ConfigurazioneGenerale> lst = new ArrayList<ConfigurazioneGenerale>();

					for (PortaApplicativa portaApplicativa : findAll) {
						lst.add(this.fromPA(portaApplicativa));
					}
					return lst;
				}
			}
		} catch (ExpressionNotImplementedException  e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (ServiceException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (CoreException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (UserInvalidException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (Exception e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		}

		return new ArrayList<ConfigurazioneGenerale>();
	}

	/*****
	 * 
	 * INFORMAZIONI GENERALI
	 * 
	 * 
	 */
	
	/**
	 * 
	 * @return numero degli applicativi registrati
	 */
	public ConfigurazioneGenerale getApplicativi(){
		ConfigurazioneGenerale configurazione = new ConfigurazioneGenerale(); 
		configurazione.setLabel(CostantiConfigurazioni.CONF_SERVIZI_APPLICATIVI_LABEL);

		ConfigurazioniGeneraliService.log.debug("Calcolo numero " + CostantiConfigurazioni.CONF_SERVIZI_APPLICATIVI_LABEL); 
		int count = 0;
		String tipoProtocollo = this.search.getProtocollo();
		try { 
			if(this.getSearch().getTipoNomeSoggettoLocale()!=null && !StringUtils.isEmpty(this.getSearch().getTipoNomeSoggettoLocale()) 
					&& !"--".equals(this.getSearch().getTipoNomeSoggettoLocale())){
				Soggetto soggetto = new Soggetto();
				soggetto.setTipoSoggetto(this.getSearch().getTipoSoggettoLocale());
				soggetto.setNomeSoggetto(this.getSearch().getSoggettoLocale());
				count = this.dynamicService.countElencoServiziApplicativi(tipoProtocollo, soggetto);
			}else {
				count = this.dynamicService.countElencoServiziApplicativi(tipoProtocollo, null);
			}
			ConfigurazioniGeneraliService.log.debug("Trovati " + (count) + " " + CostantiConfigurazioni.CONF_SERVIZI_APPLICATIVI_LABEL);
		}catch(Exception e){
			ConfigurazioniGeneraliService.log.error("Errore durante il calcolo del numero degli applicativi: " + e.getMessage(),e);
		}

		ConfigurazioniGeneraliService.log.debug("Trovate " + (count) + " " + CostantiConfigurazioni.CONF_SERVIZI_APPLICATIVI_LABEL);
		configurazione.setValue(""+count); 

		return configurazione;
	}

	/**
	 * 
	 * @return numero delle PdD Registrate
	 */
	public ConfigurazioneGenerale getPdd(){
		ConfigurazioneGenerale configurazione = new ConfigurazioneGenerale(); 
		configurazione.setLabel(CostantiConfigurazioni.CONF_PORTE_DI_DOMINIO_LABEL);

		ConfigurazioniGeneraliService.log.debug("Calcolo numero " + CostantiConfigurazioni.CONF_PORTE_DI_DOMINIO_LABEL); 
		int count = 0;
		try { 
			count =   this.dynamicService.countPdD(this.search.getProtocollo());

		}catch(Exception e){
			ConfigurazioniGeneraliService.log.error("Errore durante il calcolo del numero delle pdd: " + e.getMessage(),e);
		}
		ConfigurazioniGeneraliService.log.debug("Trovate " + (count) + " " + CostantiConfigurazioni.CONF_PORTE_DI_DOMINIO_LABEL);
		configurazione.setValue("" + count); 

		return configurazione;
	}

	public ConfigurazioneGenerale getSoggettiOperativi() throws Exception{
		ConfigurazioneGenerale configurazione = new ConfigurazioneGenerale(); 
		configurazione.setLabel(CostantiConfigurazioni.CONF_SOGGETTI_OPERATIVI_LABEL);

		ConfigurazioniGeneraliService.log.debug("Calcolo numero " + CostantiConfigurazioni.CONF_SOGGETTI_OPERATIVI_LABEL); 
		int count = 0;
		try { 
			count =   this.dynamicService.countElencoSoggettiFromTipoTipoPdD(this.search.getProtocollo(),TipoPdD.OPERATIVO);

		}catch(Exception e){
			ConfigurazioniGeneraliService.log.error("Errore durante il calcolo del numero dei soggetti operativi: " + e.getMessage(),e);
		}
		ConfigurazioniGeneraliService.log.debug("Trovati " + (count) + " " + CostantiConfigurazioni.CONF_SOGGETTI_OPERATIVI_LABEL);
		configurazione.setValue("" + count); 

		return configurazione;
	}

	public ConfigurazioneGenerale getSoggettiEsterni() throws Exception{
		ConfigurazioneGenerale configurazione = new ConfigurazioneGenerale(); 
		configurazione.setLabel(CostantiConfigurazioni.CONF_SOGGETTI_ESTERNI_LABEL);

		ConfigurazioniGeneraliService.log.debug("Calcolo numero " + CostantiConfigurazioni.CONF_SOGGETTI_ESTERNI_LABEL); 
		int count = 0; 
		try { 
			count =   this.dynamicService.countElencoSoggettiFromTipoTipoPdD(this.search.getProtocollo(),TipoPdD.ESTERNO);
			count = count + this.dynamicService.countElencoSoggettiFromTipoTipoPdD(this.search.getProtocollo(),null);

		}catch(Exception e){
			ConfigurazioniGeneraliService.log.error("Errore durante il calcolo del numero dei soggetti esterni: " + e.getMessage(),e);
		}
		ConfigurazioniGeneraliService.log.debug("Trovati " + (count) + " " + CostantiConfigurazioni.CONF_SOGGETTI_ESTERNI_LABEL);
		configurazione.setValue("" + count); 

		return configurazione;
	}

	/**
	 * 
	 * 
	 * INFORMAZIONI SUI SERVIZI
	 * 
	 * **/


	/**
	 * 
	 * @return Numero degli Accordi Servizio Parte Comune
	 * @throws Exception
	 */
	public ConfigurazioneGenerale getAccordiServizioParteComune() throws Exception{
		ConfigurazioneGenerale configurazione = new ConfigurazioneGenerale(); 
		configurazione.setLabel(CostantiConfigurazioni.CONF_ASPC_LABEL);
		int count = 0; 
		String value = null;
		boolean isErogatore = false;
		
		String tipoProtocollo = this.search.getProtocollo();
		String gruppo = this.search.getGruppo();

		
		try {
			
			IDAccordo idAccordo = null;
			String api = this.search.getApi();
			if((api!=null && !"".equals(api)) ) {
				idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(api);
			}
			if(idAccordo!=null) {
				value = ""+1;
			}
			else if (StringUtils.isNotBlank(this.getSearch().getNomeServizio())){
				String servizioString = this.getSearch().getNomeServizio();
				IDServizio idServizio = ParseUtility.parseServizioSoggetto(servizioString);
				AccordoServizioParteComune aspc = this.dynamicService.getAccordoServizio(tipoProtocollo, idServizio.getSoggettoErogatore(), idServizio.getTipo(), idServizio.getNome(), idServizio.getVersione());
				if(aspc != null){
					String nomeAspc = aspc.getNome();

					Integer versioneAspc = aspc.getVersione();

					String nomeReferenteAspc = (aspc.getIdReferente() != null) ? aspc.getIdReferente().getNome() : null;

					String tipoReferenteAspc= (aspc.getIdReferente() != null) ? aspc.getIdReferente().getTipo() : null;

					idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromValues(nomeAspc,tipoReferenteAspc,nomeReferenteAspc,versioneAspc); 
					value = tipoProtocollo != null ? NamingUtils.getLabelAccordoServizioParteComune(tipoProtocollo, idAccordo) : NamingUtils.getLabelAccordoServizioParteComune(idAccordo);
				} else {
					value = "--";
				}
			}else {
				ConfigurazioniGeneraliService.log.debug("Calcolo numero " + CostantiConfigurazioni.CONF_ASPC_LABEL);
				if(this.getSearch().getTipoNomeSoggettoLocale()!=null && !StringUtils.isEmpty(this.getSearch().getTipoNomeSoggettoLocale()) 
						&& !"--".equals(this.getSearch().getTipoNomeSoggettoLocale())
						&& !Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(this.getSearch().getTipoNomeSoggettoLocale())){
					String tipoSoggetto = this.getSearch().getTipoSoggettoLocale();
					String nomeSoggetto = this.getSearch().getSoggettoLocale();
					
					boolean supportoReferente = false;
					try{
						supportoReferente = ProtocolFactoryManager.getInstance().getProtocolFactoryByOrganizationType(tipoSoggetto).createProtocolConfiguration().isSupportoSoggettoReferenteAccordiParteComune();
					}catch(Throwable e) {
						log.error("Analisi se il protocollo supporta il soggetto referente: "+e.getMessage());
					}
					if(supportoReferente) {
						count =   this.dynamicService.countAccordiServizio(tipoProtocollo,tipoSoggetto, nomeSoggetto, true, isErogatore, gruppo);
					}
					else {
						count =   this.dynamicService.countAccordiServizio(tipoProtocollo,null, null, false, isErogatore, gruppo);
					}
				}else {

					String protocollo = Utility.getLoggedUtenteModalita();
					boolean supportoReferente = false;
					try{
						if(protocollo!=null && !StringUtils.isEmpty(protocollo) 
								&& !"--".equals(protocollo)
								&& !Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(protocollo)) {
							supportoReferente = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo).createProtocolConfiguration().isSupportoSoggettoReferenteAccordiParteComune();
						}
					}catch(Throwable e) {
						log.error("Analisi se il protocollo supporta il soggetto referente: "+e.getMessage());
					}
					
					count =   this.dynamicService.countAccordiServizio(tipoProtocollo,null, null, supportoReferente, isErogatore, gruppo);

				}
				ConfigurazioniGeneraliService.log.debug("Trovati " + (count) + " " + CostantiConfigurazioni.CONF_ASPC_LABEL);
				value = ""+count;
			}
		}catch(Exception e){
			ConfigurazioniGeneraliService.log.error("Errore durante il calcolo del numero degli accordi di servizio parte comune: " + e.getMessage(),e);
		}

		configurazione.setValue(value); 

		return configurazione;
	}

	public ConfigurazioneGenerale getAzioni(){
		ConfigurazioneGenerale configurazione = new ConfigurazioneGenerale(); 
		configurazione.setLabel(CostantiConfigurazioni.CONF_AZIONI_LABEL);
		String tipoProtocollo = this.search.getProtocollo();
		String nomeServizio = null, tipoServizio = null;
		String nomeErogatore= null;
		String tipoErogatore = null;
		Integer versioneServizio = null;
		ConfigurazioniGeneraliService.log.debug("Calcolo numero " + CostantiConfigurazioni.CONF_AZIONI_LABEL); 
		int count = 0; 
		try { 
			if (StringUtils.isNotBlank(this.getSearch().getNomeServizio())){
				String servizioString = this.getSearch().getNomeServizio();
				IDServizio idServizio = ParseUtility.parseServizioSoggetto(servizioString);
				nomeServizio = idServizio.getNome();
				tipoServizio = idServizio.getTipo();
				nomeErogatore = idServizio.getSoggettoErogatore().getNome();
				tipoErogatore = idServizio.getSoggettoErogatore().getTipo();
				versioneServizio = idServizio.getVersione();
				count =   this.dynamicService.countAzioniFromServizio(tipoProtocollo, tipoServizio, nomeServizio, tipoErogatore, nomeErogatore, versioneServizio,null);
			}
		}catch(Exception e){
			ConfigurazioniGeneraliService.log.error("Errore durante il calcolo del numero delle azioni: " + e.getMessage(),e);
		}
		ConfigurazioniGeneraliService.log.debug("Trovate " + (count) + " " + CostantiConfigurazioni.CONF_AZIONI_LABEL);
		configurazione.setValue("" + count); 

		return configurazione;
	}

	public ConfigurazioneGenerale getErogazioniServizio() throws Exception{
		ConfigurazioneGenerale configurazione = new ConfigurazioneGenerale(); 
		configurazione.setLabel(CostantiConfigurazioni.CONF_ASPS_LABEL);

		String tipoSoggetto = null;
		String nomeSoggetto =  null;
		String tipoProtocollo = this.search.getProtocollo();
		String nomeServizio = null, tipoServizio = null;
		String nomeErogatore = null, tipoErogatore = null;
		Integer versioneServizio = null;
		ConfigurazioniGeneraliService.log.debug("Calcolo numero " + CostantiConfigurazioni.CONF_ASPS_LABEL); 
		int count = 0;
		try {
			if (StringUtils.isNotBlank(this.getSearch().getNomeServizio())){
				String servizioString = this.getSearch().getNomeServizio();
				IDServizio idServizio = ParseUtility.parseServizioSoggetto(servizioString);

				// valutare
				//				tipoSoggetto = this.getSearch().getTipoSoggettoLocale();
				//				nomeSoggetto = this.getSearch().getSoggettoLocale();
				nomeServizio = idServizio.getNome();
				tipoServizio = idServizio.getTipo();
				nomeErogatore = idServizio.getSoggettoErogatore().getNome();
				tipoErogatore = idServizio.getSoggettoErogatore().getTipo();
				versioneServizio = idServizio.getVersione();
			}else {
				if(this.getSearch().getTipoNomeSoggettoLocale()!=null && 
						!StringUtils.isEmpty(this.getSearch().getTipoNomeSoggettoLocale()) 
						&& !"--".equals(this.getSearch().getTipoNomeSoggettoLocale())){
					tipoSoggetto = this.getSearch().getTipoSoggettoLocale();
					nomeSoggetto = this.getSearch().getSoggettoLocale();
				}else {
					// non ho selezionato ne servizio ne soggetto
				}
			}

			String gruppo = this.search.getGruppo();
			
			IDAccordo idAccordo = null;
			String api = this.search.getApi();
			if((api!=null && !"".equals(api)) ) {
				idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(api);
			}
			
			if( ((gruppo!=null && !"".equals(gruppo))) || idAccordo!=null ) {
			
				boolean distinct = false; // uso per contare
				List<SelectItem> l = this.dynamicUtils.getListaSelectItemsElencoConfigurazioneServiziErogazione(tipoProtocollo, gruppo, idAccordo, tipoSoggetto, nomeSoggetto, 
						tipoErogatore, nomeErogatore, tipoServizio, nomeServizio, versioneServizio, null, 
						null, false, this.search.getPermessiUtenteOperatore(), distinct);
				count = (l!=null) ? l.size() : 0;
				
			}
			else {
			
				count = this.dynamicService.
						countConfigurazioneServiziErogazione(tipoProtocollo, tipoSoggetto, nomeSoggetto, tipoServizio, nomeServizio, tipoErogatore, nomeErogatore, versioneServizio, null, null, false, this.search.getPermessiUtenteOperatore());
				
			}
		}catch(Exception e){
			ConfigurazioniGeneraliService.log.error("Errore durante il calcolo del numero degli accordi servizio parte specifica: " + e.getMessage(),e);
		}
		ConfigurazioniGeneraliService.log.debug("Trovati " + (count) + " " + CostantiConfigurazioni.CONF_ASPS_LABEL);
		configurazione.setValue("" + count); 

		return configurazione;
	}

	public ConfigurazioneGenerale getFruizioniServizio() throws Exception{
		ConfigurazioneGenerale configurazione = new ConfigurazioneGenerale(); 
		configurazione.setLabel(CostantiConfigurazioni.CONF_FRUIZIONI_SERVIZIO_LABEL);

		String tipoSoggetto = null;
		String nomeSoggetto =  null;
		String tipoProtocollo =this.search.getProtocollo();
		String nomeServizio = null, tipoServizio = null;
		String nomeErogatore = null , tipoErogatore = null;
		Integer versioneServizio = null;
		ConfigurazioniGeneraliService.log.debug("Calcolo numero " + CostantiConfigurazioni.CONF_FRUIZIONI_SERVIZIO_LABEL); 
		int count = 0;
		try { 
			if (StringUtils.isNotBlank(this.getSearch().getNomeServizio())){
				String servizioString = this.getSearch().getNomeServizio();
				IDServizio idServizio = ParseUtility.parseServizioSoggetto(servizioString);

				nomeServizio = idServizio.getNome();
				tipoServizio = idServizio.getTipo();
				nomeErogatore = idServizio.getSoggettoErogatore().getNome();
				tipoErogatore = idServizio.getSoggettoErogatore().getTipo();
				versioneServizio = idServizio.getVersione();

			}else {
				if(this.getSearch().getTipoNomeSoggettoLocale()!=null && 
						!StringUtils.isEmpty(this.getSearch().getTipoNomeSoggettoLocale()) 
						&& !"--".equals(this.getSearch().getTipoNomeSoggettoLocale())){
					tipoSoggetto = this.getSearch().getTipoSoggettoLocale();
					nomeSoggetto = this.getSearch().getSoggettoLocale();
				}else {
					// non ho selezionato ne servizio ne soggetto
				}
			}

			String gruppo = this.search.getGruppo();
			
			IDAccordo idAccordo = null;
			String api = this.search.getApi();
			if((api!=null && !"".equals(api)) ) {
				idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(api);
			}
			
			if( ((gruppo!=null && !"".equals(gruppo))) || idAccordo!=null ) {
			
				boolean distinct = false; // uso per contare
				List<SelectItem> l = this.dynamicUtils.getListaSelectItemsElencoConfigurazioneServiziFruizione(tipoProtocollo, gruppo, idAccordo, tipoSoggetto, nomeSoggetto, 
						tipoErogatore, nomeErogatore, tipoServizio, nomeServizio, versioneServizio, null, 
						null, false, this.search.getPermessiUtenteOperatore(), distinct);
				count = (l!=null) ? l.size() : 0;
				
			}
			else {
				count =   this.dynamicService.countConfigurazioneServiziFruizione(tipoProtocollo, tipoSoggetto, nomeSoggetto, tipoServizio, nomeServizio, tipoErogatore, nomeErogatore, versioneServizio, null, null, false,  this.search.getPermessiUtenteOperatore());
			}
		}catch(Exception e){
			ConfigurazioniGeneraliService.log.error("Errore durante il calcolo del numero delle fruizioni servizio: " + e.getMessage(),e);
		}
		ConfigurazioniGeneraliService.log.debug("Trovate " + (count) + " " + CostantiConfigurazioni.CONF_FRUIZIONI_SERVIZIO_LABEL);
		configurazione.setValue("" + count); 

		return configurazione;
	}

	@Override
	public List<ConfigurazioneGenerale> findAll(int start, int limit) {
		ConfigurazioniGeneraliService.log.debug("Metodo FindAll: start[" + start + "], limit: [" + limit + "]");

		try {
			
			String tipoProtocollo =this.search.getProtocollo();
			
			String tipoSoggetto = this.search.getTipoSoggettoLocale();
			String nomeSoggetto = this.search.getSoggettoLocale();
			
			String input = null;
			
			String gruppo = this.search.getGruppo();
			
			boolean apiImplSelected = StringUtils.isNotBlank(this.search.getNomeServizio());
			
			IDAccordo idAccordo = null;
			IDAccordoFactory idAccordoFactory = null;
			String api = this.search.getApi();
			if( !apiImplSelected && (api!=null && !"".equals(api)) ) {
				idAccordoFactory = IDAccordoFactory.getInstance();
				idAccordo = idAccordoFactory.getIDAccordoFromUri(api);
			}
			
			List<IDServizio> listIDServizio = getListServizi(apiImplSelected, gruppo, idAccordo,
					tipoProtocollo, tipoSoggetto, nomeSoggetto,
					input);
			
			if(PddRuolo.DELEGATA.equals(this.search.getTipologiaTransazioni())) {
				IExpression expr = this.createPDExpression(this.portaDelegataDAO, this.search, false);
				IPaginatedExpression pagExpr = this.portaDelegataDAO.toPaginatedExpression(expr);
				pagExpr.offset(start).limit(limit);

				setListServiziPD(apiImplSelected, listIDServizio, pagExpr);

				List<PortaDelegata> findAll = this.portaDelegataDAO.findAll(pagExpr);
				if(findAll != null && findAll.size() > 0){
					List<ConfigurazioneGenerale> lst = new ArrayList<ConfigurazioneGenerale>();

					for (PortaDelegata portaDelegata : findAll) {						
						lst.add(this.fromPD(portaDelegata));
					}
					return lst;
				}
			} else {
				IExpression expr = this.createPAExpression(this.portaApplicativaDAO, this.search, false);
				IPaginatedExpression pagExpr = this.portaApplicativaDAO.toPaginatedExpression(expr);
				pagExpr.offset(start).limit(limit);

				setListServiziPA(apiImplSelected, listIDServizio, pagExpr);
				
				List<PortaApplicativa> findAll = this.portaApplicativaDAO.findAll(pagExpr);
				if(findAll != null && findAll.size() > 0){
					List<ConfigurazioneGenerale> lst = new ArrayList<ConfigurazioneGenerale>();

					for (PortaApplicativa portaApplicativa : findAll) {
						lst.add(this.fromPA(portaApplicativa));
					}

					return lst;
				}
			}
		} catch (ExpressionNotImplementedException  e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (ServiceException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (CoreException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (UserInvalidException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (Exception e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		}

		return new ArrayList<ConfigurazioneGenerale>();
	}
	@Override
	public List<ConfigurazioneGenerale> findAllDettagli(int start, int limit) {
		ConfigurazioniGeneraliService.log.debug("Metodo findAllDettagli: start[" + start + "], limit: [" + limit + "]");

		try {
			String tipoProtocollo =this.search.getProtocollo();
			
			String tipoSoggetto = this.search.getTipoSoggettoLocale();
			String nomeSoggetto = this.search.getSoggettoLocale();
			
			String input = null;
			
			String gruppo = this.search.getGruppo();
			
			boolean apiImplSelected = StringUtils.isNotBlank(this.search.getNomeServizio());
			
			IDAccordo idAccordo = null;
			IDAccordoFactory idAccordoFactory = null;
			String api = this.search.getApi();
			if( !apiImplSelected && (api!=null && !"".equals(api)) ) {
				idAccordoFactory = IDAccordoFactory.getInstance();
				idAccordo = idAccordoFactory.getIDAccordoFromUri(api);
			}
			
			List<IDServizio> listIDServizio = getListServizi(apiImplSelected, gruppo, idAccordo,
					tipoProtocollo, tipoSoggetto, nomeSoggetto,
					input);
			
			if(PddRuolo.DELEGATA.equals(this.search.getTipologiaTransazioni())) {
				IExpression expr = this.createPDExpression(this.portaDelegataDAO, this.search, false);
				IPaginatedExpression pagExpr = this.portaDelegataDAO.toPaginatedExpression(expr);
				pagExpr.offset(start).limit(limit);

				setListServiziPD(apiImplSelected, listIDServizio, pagExpr);

				List<PortaDelegata> findAll = this.portaDelegataDAO.findAll(pagExpr);
				if(findAll != null && findAll.size() > 0){
					List<ConfigurazioneGenerale> lst = new ArrayList<ConfigurazioneGenerale>();

					for (PortaDelegata portaDelegata : findAll) {
						ConfigurazioneGenerale dettaglioPD = this.fillDettaglioPD(portaDelegata);
						lst.add(dettaglioPD);
						
						List<ConfigurazioneGenerale> findConfigurazioniFiglie = this.findConfigurazioniFiglie(portaDelegata.getNome(),dettaglioPD.getRuolo());
						if(findConfigurazioniFiglie != null && findConfigurazioniFiglie.size() > 0)
							lst.addAll(findConfigurazioniFiglie);
					}
					return lst;
				}
			} else {
				IExpression expr = this.createPAExpression(this.portaApplicativaDAO, this.search, false);
				IPaginatedExpression pagExpr = this.portaApplicativaDAO.toPaginatedExpression(expr);
				pagExpr.offset(start).limit(limit);

				setListServiziPA(apiImplSelected, listIDServizio, pagExpr);

				List<PortaApplicativa> findAll = this.portaApplicativaDAO.findAll(pagExpr);
				if(findAll != null && findAll.size() > 0){
					List<ConfigurazioneGenerale> lst = new ArrayList<ConfigurazioneGenerale>();

					for (PortaApplicativa portaApplicativa : findAll) {
						ConfigurazioneGenerale dettaglioPA = this.fillDettaglioPA(portaApplicativa);
						lst.add(dettaglioPA);
						
						List<ConfigurazioneGenerale> findConfigurazioniFiglie = this.findConfigurazioniFiglie(portaApplicativa.getNome(),dettaglioPA.getRuolo());
						if(findConfigurazioniFiglie != null && findConfigurazioniFiglie.size() > 0)
							lst.addAll(findConfigurazioniFiglie);
					}

					return lst;
				}
			}
		} catch (ExpressionNotImplementedException  e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (ServiceException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (CoreException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (MultipleResultException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (DriverConfigurazioneException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (DriverConfigurazioneNotFound e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (DriverRegistroServiziException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (DriverRegistroServiziNotFound e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (ProtocolException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (UserInvalidException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (Exception e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		}

		return new ArrayList<ConfigurazioneGenerale>();
	}
	
	@Override
	public List<ConfigurazioneGenerale> findConfigurazioniFiglie(String nome, PddRuolo ruolo) throws ServiceException {
		ConfigurazioniGeneraliService.log.debug("Metodo findConfigurazioniFiglie: nome[" + nome + "], ruolo: [" + ruolo + "]");

		try {
			if(PddRuolo.DELEGATA.equals(ruolo)) {
				IExpression expr = this.portaDelegataDAO.newExpression();
						
				// seleziono solo le porte figlie
				expr.equals(PortaDelegata.model().NOME_PORTA_DELEGANTE_AZIONE, nome);
				expr.sortOrder(SortOrder.ASC);
				expr.addOrder(PortaDelegata.model().ID_SOGGETTO.TIPO);
				expr.addOrder(PortaDelegata.model().ID_SOGGETTO.NOME);
				expr.addOrder(PortaDelegata.model().TIPO_SERVIZIO);
				expr.addOrder(PortaDelegata.model().NOME_SERVIZIO);
				expr.addOrder(PortaDelegata.model().VERSIONE_SERVIZIO);
				expr.addOrder(PortaDelegata.model().NOME_AZIONE);
				
				IPaginatedExpression pagExpr = this.portaDelegataDAO.toPaginatedExpression(expr);
				List<PortaDelegata> findAll = this.portaDelegataDAO.findAll(pagExpr);
				if(findAll != null && findAll.size() > 0){
					List<ConfigurazioneGenerale> lst = new ArrayList<ConfigurazioneGenerale>();

					for (PortaDelegata portaDelegata : findAll) {
						ConfigurazioneGenerale dettaglioPD = this.fillDettaglioPD(portaDelegata);
						lst.add(dettaglioPD);
					}
					return lst;
				}
			} else {
				IExpression expr = this.portaApplicativaDAO.newExpression();
				
				// seleziono solo le porte figlie
				expr.equals(PortaApplicativa.model().NOME_PORTA_DELEGANTE_AZIONE, nome);
				expr.sortOrder(SortOrder.ASC);
				expr.addOrder(PortaApplicativa.model().ID_SOGGETTO.TIPO);
				expr.addOrder(PortaApplicativa.model().ID_SOGGETTO.NOME);
				expr.addOrder(PortaApplicativa.model().TIPO_SERVIZIO);
				expr.addOrder(PortaApplicativa.model().NOME_SERVIZIO);
				expr.addOrder(PortaApplicativa.model().VERSIONE_SERVIZIO);
				expr.addOrder(PortaApplicativa.model().NOME_AZIONE);
				
				IPaginatedExpression pagExpr = this.portaApplicativaDAO.toPaginatedExpression(expr);

				List<PortaApplicativa> findAll = this.portaApplicativaDAO.findAll(pagExpr);
				if(findAll != null && findAll.size() > 0){
					List<ConfigurazioneGenerale> lst = new ArrayList<ConfigurazioneGenerale>();

					for (PortaApplicativa portaApplicativa : findAll) {
						ConfigurazioneGenerale dettaglioPA = this.fillDettaglioPA(portaApplicativa);
						lst.add(dettaglioPA);
					}

					return lst;
				}
			}
		} catch (ExpressionNotImplementedException  e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (ServiceException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (MultipleResultException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (DriverConfigurazioneException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (DriverConfigurazioneNotFound e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (DriverRegistroServiziException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (DriverRegistroServiziNotFound e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (ProtocolException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		}

		return new ArrayList<ConfigurazioneGenerale>();
	}

	@Override
	public int totalCount() {
		ConfigurazioniGeneraliService.log.debug("Metodo TotalCount");

		NonNegativeNumber nnn = null;
		try {
			String tipoProtocollo =this.search.getProtocollo();
			
			String tipoSoggetto = this.search.getTipoSoggettoLocale();
			String nomeSoggetto = this.search.getSoggettoLocale();
			
			String input = null;
			
			String gruppo = this.search.getGruppo();
			
			boolean apiImplSelected = StringUtils.isNotBlank(this.search.getNomeServizio());
			
			IDAccordo idAccordo = null;
			IDAccordoFactory idAccordoFactory = null;
			String api = this.search.getApi();
			if( !apiImplSelected && (api!=null && !"".equals(api)) ) {
				idAccordoFactory = IDAccordoFactory.getInstance();
				idAccordo = idAccordoFactory.getIDAccordoFromUri(api);
			}
			
			List<IDServizio> listIDServizio = getListServizi(apiImplSelected, gruppo, idAccordo,
					tipoProtocollo, tipoSoggetto, nomeSoggetto,
					input);
			
			if(PddRuolo.DELEGATA.equals(this.search.getTipologiaTransazioni())) {
				IExpression expr = this.createPDExpression(this.portaDelegataDAO, this.search, true);
				
				setListServiziPD(apiImplSelected, listIDServizio, expr);
				
				nnn = this.portaDelegataDAO.count(expr);
			} else {
				IExpression expr = this.createPAExpression(this.portaApplicativaDAO, this.search, true);
				
				setListServiziPA(apiImplSelected, listIDServizio, expr);
				
				nnn = this.portaApplicativaDAO.count(expr);				
			}

			if(nnn != null)
				return (int) nnn.longValue();
		} catch (ExpressionNotImplementedException  e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (ServiceException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (CoreException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (UserInvalidException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (Exception e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		}

		return 0;
	}

	@Override
	public void store(ConfigurazioneGenerale obj) throws Exception {
	}

	@Override
	public void deleteById(ConfigurazioneGeneralePK key) {
	}

	@Override
	public void delete(ConfigurazioneGenerale obj) throws Exception {
	}

	@Override
	public void deleteAll() throws Exception {
	}

	@Override
	public ConfigurazioneGenerale findById(ConfigurazioneGeneralePK key) {
		ConfigurazioniGeneraliService.log.debug("Metodo FindById: [" + key + "]");
		ConfigurazioneGenerale configurazione =  null;
		try {
			if(PddRuolo.DELEGATA.equals(key.getRuolo())){
				IDBPortaDelegataServiceSearch search = (IDBPortaDelegataServiceSearch) this.portaDelegataDAO;
				PortaDelegata portaDelegata = search.get(key.getId());
				configurazione = fillDettaglioPD(portaDelegata);
			} else {
				IDBPortaApplicativaServiceSearch search= (IDBPortaApplicativaServiceSearch) this.portaApplicativaDAO;
				PortaApplicativa portaApplicativa = search.get(key.getId());
				configurazione = fillDettaglioPA(portaApplicativa);
			}
		} catch (ServiceException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (NotFoundException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (MultipleResultException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (NotImplementedException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (DriverConfigurazioneException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (DriverConfigurazioneNotFound e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (ExpressionNotImplementedException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (ExpressionException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (ProtocolException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (DriverRegistroServiziException e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		} catch (DriverRegistroServiziNotFound e) {
			ConfigurazioniGeneraliService.log.error(e.getMessage(), e);
		}

		return configurazione;
	}

	@SuppressWarnings("deprecation")
	private ConfigurazioneGenerale fillDettaglioPD(PortaDelegata portaDelegata)
			throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException,
			DriverConfigurazioneException, DriverConfigurazioneNotFound, DriverRegistroServiziException,
			DriverRegistroServiziNotFound, ExpressionNotImplementedException, ExpressionException, ProtocolException {
		DettaglioPD dettaglioPD = new DettaglioPD();
		dettaglioPD.setPortaDelegata(portaDelegata);
		ConfigurazioneGenerale configurazione = this.fromPD(portaDelegata);

		// carico dettaglio
		IDPortaDelegata idPD = new IDPortaDelegata();
		idPD.setNome(dettaglioPD.getPortaDelegata().getNome());
		IdentificativiFruizione identificativiFruizione = new IdentificativiFruizione();
		identificativiFruizione.setSoggettoFruitore(new IDSoggetto(dettaglioPD.getPortaDelegata().getIdSoggetto().getTipo(), dettaglioPD.getPortaDelegata().getIdSoggetto().getNome()));
		IDServizio idServizio = new IDServizio();
		idServizio.setSoggettoErogatore(new IDSoggetto(dettaglioPD.getPortaDelegata().getTipoSoggettoErogatore(), dettaglioPD.getPortaDelegata().getNomeSoggettoErogatore()));
		idServizio.setTipo(dettaglioPD.getPortaDelegata().getTipoServizio());
		idServizio.setNome(dettaglioPD.getPortaDelegata().getNomeServizio());
		idServizio.setAzione(dettaglioPD.getPortaDelegata().getNomeAzione());
		idServizio.setVersione(dettaglioPD.getPortaDelegata().getVersioneServizio());
		identificativiFruizione.setIdServizio(idServizio);
		idPD.setIdentificativiFruizione(identificativiFruizione );
		dettaglioPD.setPortaDelegataOp2(this.driverConfigDB.getPortaDelegata(idPD));
		List<String> azioniGruppo = null;
		Connection con = null;
		try {
			con = this.driverConfigDB.getConnection("fillDettaglioPD_getMappingFruizione");
			dettaglioPD.setMappingFruizionePortaDelegataOp2(DBMappingUtils.getMappingFruizione(idServizio, identificativiFruizione.getSoggettoFruitore(), idPD, con, this.driverConfigDB.getTipoDB()));
			if(dettaglioPD.getMappingFruizionePortaDelegataOp2().isDefault()) {
				dettaglioPD.setIdPortaDelegataDefaultOp2(idPD);
			}
			else {
				dettaglioPD.setIdPortaDelegataDefaultOp2(DBMappingUtils.getIDPortaDelegataAssociataDefault(idServizio, identificativiFruizione.getSoggettoFruitore(), con, this.driverConfigDB.getTipoDB()));
				dettaglioPD.setPortaDelegataDefaultOp2(this.driverConfigDB.getPortaDelegata(dettaglioPD.getIdPortaDelegataDefaultOp2()));
				if(dettaglioPD.getPortaDelegataOp2().getAzione()!=null) {
					azioniGruppo = dettaglioPD.getPortaDelegataOp2().getAzione().getAzioneDelegataList();
				}
			}
		}catch(Exception e) {
			throw new ServiceException(e.getMessage(),e);
		}
		finally {
			this.driverConfigDB.releaseConnection(con);
		}
		dettaglioPD.setConnettore(ConfigurazioniUtils.getConnettore(dettaglioPD.getPortaDelegata().getIdSoggetto().getTipo(), dettaglioPD.getPortaDelegata().getIdSoggetto().getNome(), 
				dettaglioPD.getPortaDelegata().getTipoSoggettoErogatore(), dettaglioPD.getPortaDelegata().getNomeSoggettoErogatore(), 
				dettaglioPD.getPortaDelegata().getTipoServizio(), dettaglioPD.getPortaDelegata().getNomeServizio(),dettaglioPD.getPortaDelegata().getVersioneServizio(), 
				azioniGruppo,
				this.driverRegistroDB));
		dettaglioPD.setPropertyConnettore(ConfigurazioniUtils.printConnettore(dettaglioPD.getConnettore(), CostantiConfigurazioni.LABEL_MODALITA_INOLTRO, null));

		//		if(dettaglioPD.getPortaDelegata().getNomeAzione()==null){
		ConfigurazioniUtils.fillAzioniPD(dettaglioPD, this.utilsServiceManager);
		//		}

		AccordoServizioParteComune aspc = this.utilsServiceManager.getAccordoServizioParteComuneServiceSearch().get(dettaglioPD.getIdAccordoServizioParteComune());
		IPaginatedExpression gruppiExpr = this.utilsServiceManager.getAccordoServizioParteComuneGruppoServiceSearch().newPaginatedExpression();
		gruppiExpr.equals(AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, aspc.getNome());
		gruppiExpr.equals(AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, aspc.getVersione());
		gruppiExpr.equals(AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, aspc.getIdReferente().getNome());
		gruppiExpr.equals(AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, aspc.getIdReferente().getTipo());
		List<IdAccordoServizioParteComuneGruppo> lGruppi = this.utilsServiceManager.getAccordoServizioParteComuneGruppoServiceSearch().findAllIds(gruppiExpr);
		List<String> tags = new ArrayList<String>();
		if(lGruppi!=null && !lGruppi.isEmpty()) {
			for (IdAccordoServizioParteComuneGruppo gruppoCheck : lGruppi) {
				tags.add(gruppoCheck.getIdGruppo().getNome());
			}
		}
		
		String canaleApi = null;
		if(aspc!=null) {
			canaleApi = aspc.getCanale();
		}
		String canale = CanaliUtils.getCanale(this.driverConfigDB.getCanaliConfigurazione(false), canaleApi, dettaglioPD.getPortaDelegata().getCanale());
		
//	a questo punto ho tutto!
//		
		dettaglioPD.setPropertyGenerali(ConfigurazioniUtils.getPropertiesGeneraliPD(dettaglioPD));
		dettaglioPD.setPropertyAutenticazione(ConfigurazioniUtils.getPropertiesAutenticazionePD(dettaglioPD));
		dettaglioPD.setPropertyAutorizzazione(ConfigurazioniUtils.getPropertiesAutorizzazionePD(dettaglioPD, idPD, this.driverConfigDB, this.driverRegistroDB));  

		try {
			dettaglioPD.setRateLimiting(getDettaglioRateLimiting(idPD.getNome(), RuoloPolicy.DELEGATA));
		}catch(Exception e) {
			ConfigurazioniGeneraliService.log.error("Read RateLimiting PD["+idPD.getNome()+"] failed: "+e.getMessage(),e);
		}
		
		IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByOrganizationType(idPD.getIdentificativiFruizione().getSoggettoFruitore().getTipo());

		String nomePorta = idPD.getNome();
		if(dettaglioPD.getMappingFruizionePortaDelegataOp2()!=null && !dettaglioPD.getMappingFruizionePortaDelegataOp2().isDefault()) {
			if(dettaglioPD.getIdPortaDelegataDefaultOp2()!=null) {
				nomePorta = dettaglioPD.getIdPortaDelegataDefaultOp2().getNome();
			}
		}
		UrlInvocazioneAPI urlInvocazioneAPI = UrlInvocazioneAPI.getConfigurazioneUrlInvocazione(this.configurazioneUrlInvocazione, protocolFactory, RuoloContesto.PORTA_DELEGATA, 
				ConfigurazioniUtils.getServiceBindingFromValues(dettaglioPD.getPortaDelegata().getTipoSoggettoErogatore(), dettaglioPD.getPortaDelegata().getNomeSoggettoErogatore(), 
						dettaglioPD.getPortaDelegata().getTipoServizio(), dettaglioPD.getPortaDelegata().getNomeServizio(),dettaglioPD.getPortaDelegata().getVersioneServizio(), 
						this.utilsServiceManager), 
						nomePorta, idPD.getIdentificativiFruizione().getSoggettoFruitore(),
						tags, canale);
		String urlInvocazione = urlInvocazioneAPI.getUrl();
		dettaglioPD.setUrlInvocazione(urlInvocazione);
		
		dettaglioPD.setPropertyIntegrazione(ConfigurazioniUtils.getPropertiesIntegrazionePD(dettaglioPD));

		if(CostantiLabel.MODIPA_PROTOCOL_NAME.equals(protocolFactory.getProtocol())) {
			dettaglioPD.setConfigurazioneProfilo(this.readConfigurazioneProfiloFruizione(idServizio, urlInvocazione, identificativiFruizione.getSoggettoFruitore()));
		}
		
		configurazione.setPd(dettaglioPD);
		return configurazione;
	}
	
	private String readConfigurazioneProfiloFruizione(IDServizio idServizio, String urlInvocazione, IDSoggetto idFruitore) {
		try {
			AccordoServizioParteSpecifica asps = this.driverRegistroDB.getAccordoServizioParteSpecifica(idServizio,false);
	
			boolean useOldMethod = false;
			if(useOldMethod) {
				return this._old_readConfigurazioneProfiloFruizione(asps, idFruitore);
			}
			else {
				
				Fruitore fruitore = null;
				for (Fruitore check : asps.getFruitoreList()) {
					if(check.getTipo().equals(idFruitore.getTipo()) && check.getNome().equals(idFruitore.getNome())) {
						fruitore = check;
						break;
					}
				}
					
				String urlConnettoreFruitoreModI = null;
				org.openspcoop2.core.registry.Connettore connettore = fruitore.getConnettore();
				if(connettore!=null && connettore.sizePropertyList()>0) {
					for (Property p : connettore.getPropertyList()) {
						if(CostantiDB.CONNETTORE_HTTP_LOCATION.equals(p.getNome())) {
							urlConnettoreFruitoreModI = p.getValore();
						}
					}
				}
				
				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
				org.openspcoop2.core.registry.AccordoServizioParteComune aspc = this.driverRegistroDB.getAccordoServizioParteComune(idAccordo, false);
				Map<String, String> map = ModIUtils.configToMap(aspc, asps, urlInvocazione, fruitore, urlConnettoreFruitoreModI);
				if(map!=null && !map.isEmpty()) {
					List<String> keys = new ArrayList<String>();
					keys.addAll(map.keySet());
					Collections.sort(keys);
					StringBuilder sb = new StringBuilder();
					for (String key : keys) {
						
						if(ModIUtils.isBooleanIndicator(key)) {
							continue;
						}
						
						if(sb.length()>0) {
							sb.append("\n");
						}
						sb.append(key);
						sb.append(":");
						String v = map.get(key);
						if(v!=null && StringUtils.isNotEmpty(v)) {
							if(v.contains("\n")){
								v = v.replaceAll("\n"," ");
							}
							sb.append(v);
						}
					}
					
					if(sb.length()>0) {
						return sb.toString();
					}
				}
			}
		}catch(Exception e) {
			ConfigurazioniGeneraliService.log.error("Read ProfiloInteroperabilità ModI configuration idServizio["+idServizio+"] idFruitore["+idFruitore+"] failed: "+e.getMessage(),e);
		}
		
		return null;
	}
	
	private String _old_readConfigurazioneProfiloFruizione(AccordoServizioParteSpecifica asps, IDSoggetto idFruitore) {
		if(asps.sizeFruitoreList()>0) {
			for (Fruitore fr : asps.getFruitoreList()) {
				if(fr.getTipo().equals(idFruitore.getTipo()) && fr.getNome().equals(idFruitore.getNome())) {
					
					List<ProtocolProperty> ppList = fr.getProtocolPropertyList();
					if(ppList!=null && !ppList.isEmpty()) {
						StringBuilder sb = new StringBuilder();
						
						boolean trustStore = false;
						boolean trustStoreSsl = false;
						for (ProtocolProperty protocolProperty : ppList) {
							
							if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE.equals(protocolProperty.getName()) &&
									StringUtils.isNotEmpty(protocolProperty.getValue())) {
								boolean ridefinisci = CostantiDB.MODIPA_PROFILO_RIDEFINISCI.equals(protocolProperty.getValue());
								if(ridefinisci) {
									trustStore = true;
								}
							}
							else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE.equals(protocolProperty.getName()) &&
									StringUtils.isNotEmpty(protocolProperty.getValue())) {
								boolean ridefinisci = CostantiDB.MODIPA_PROFILO_RIDEFINISCI.equals(protocolProperty.getValue());
								if(ridefinisci) {
									trustStoreSsl = true;
								}
							}
							
						}
						
						
						for (ProtocolProperty protocolProperty : ppList) {
							
							if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE.equals(protocolProperty.getName()) &&
									StringUtils.isNotEmpty(protocolProperty.getValue())) {
								if(sb.length()>0) {
									sb.append("\n");
								}
								sb.append("request-audience:").append(protocolProperty.getValue());
							}
							else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_AUDIENCE_INTEGRITY.equals(protocolProperty.getName()) &&
									StringUtils.isNotEmpty(protocolProperty.getValue())) {
								if(sb.length()>0) {
									sb.append("\n");
								}
								sb.append("request-integrity-audience:").append(protocolProperty.getValue());
							}
							else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE.equals(protocolProperty.getName()) &&
									StringUtils.isNotEmpty(protocolProperty.getValue())) {
								if(sb.length()>0) {
									sb.append("\n");
								}
								sb.append("response-audience:").append(protocolProperty.getValue());
							}
							else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_AUDIENCE_INTEGRITY.equals(protocolProperty.getName()) &&
									StringUtils.isNotEmpty(protocolProperty.getValue())) {
								if(sb.length()>0) {
									sb.append("\n");
								}
								sb.append("response-integrity-audience:").append(protocolProperty.getValue());
							}
							else {
								if(trustStoreSsl) {
									if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE.equals(protocolProperty.getName()) &&
											StringUtils.isNotEmpty(protocolProperty.getValue())) {
										if(sb.length()>0) {
											sb.append("\n");
										}
										sb.append("response-truststore-ssl-type:").append(protocolProperty.getValue());
										continue;
									}
									else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH.equals(protocolProperty.getName()) &&
											StringUtils.isNotEmpty(protocolProperty.getValue())) {
										if(sb.length()>0) {
											sb.append("\n");
										}
										sb.append("response-truststore-ssl-path:").append(protocolProperty.getValue());
										continue;
									}
									else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS.equals(protocolProperty.getName()) &&
											StringUtils.isNotEmpty(protocolProperty.getValue())) {
										if(sb.length()>0) {
											sb.append("\n");
										}
										sb.append("response-truststore-ssl-crls:").append(protocolProperty.getValue());
										continue;
									}
								}
								if(trustStore) {
									if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE.equals(protocolProperty.getName()) &&
											StringUtils.isNotEmpty(protocolProperty.getValue())) {
										if(sb.length()>0) {
											sb.append("\n");
										}
										sb.append("response-truststore-type:").append(protocolProperty.getValue());
										continue;
									}
									else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH.equals(protocolProperty.getName()) &&
											StringUtils.isNotEmpty(protocolProperty.getValue())) {
										if(sb.length()>0) {
											sb.append("\n");
										}
										sb.append("response-truststore-path:").append(protocolProperty.getValue());
										continue;
									}
									else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS.equals(protocolProperty.getName()) &&
											StringUtils.isNotEmpty(protocolProperty.getValue())) {
										if(sb.length()>0) {
											sb.append("\n");
										}
										sb.append("response-truststore-crls:").append(protocolProperty.getValue());
										continue;
									}
								}
							}
							
						}
						
						if(sb.length()>0) {
							return sb.toString();
						}
					}
					
					break;
				}
			}
		}
		
		return null;
	}

	@SuppressWarnings("deprecation")
	private ConfigurazioneGenerale fillDettaglioPA(PortaApplicativa portaApplicativa) throws ServiceException, NotFoundException,
	MultipleResultException, NotImplementedException, ExpressionNotImplementedException, ExpressionException,
	DriverConfigurazioneException, DriverConfigurazioneNotFound, ProtocolException,
	DriverRegistroServiziException, DriverRegistroServiziNotFound {
		DettaglioPA dettaglioPA = new DettaglioPA();
		dettaglioPA.setPortaApplicativa(portaApplicativa);
		ConfigurazioneGenerale configurazione = this.fromPA(portaApplicativa);

		//		if(dettaglioPA.getPortaApplicativa().getNomeAzione()==null){
		ConfigurazioniUtils.fillAzioniPA(dettaglioPA, this.utilsServiceManager);
		//		}

		IDPortaApplicativa idPA = new IDPortaApplicativa();
		idPA.setNome(dettaglioPA.getPortaApplicativa().getNome());
		IdentificativiErogazione identificativiErogazione = new IdentificativiErogazione();
		IDServizio idServizio = new IDServizio();
		idServizio.setSoggettoErogatore(new IDSoggetto(dettaglioPA.getPortaApplicativa().getIdSoggetto().getTipo(), dettaglioPA.getPortaApplicativa().getIdSoggetto().getNome()));
		idServizio.setTipo(dettaglioPA.getPortaApplicativa().getTipoServizio());
		idServizio.setNome(dettaglioPA.getPortaApplicativa().getNomeServizio());
		idServizio.setAzione(dettaglioPA.getPortaApplicativa().getNomeAzione());
		idServizio.setVersione(dettaglioPA.getPortaApplicativa().getVersioneServizio());
		identificativiErogazione.setIdServizio(idServizio);
		idPA.setIdentificativiErogazione(identificativiErogazione );
		dettaglioPA.setPortaApplicativaOp2(this.driverConfigDB.getPortaApplicativa(idPA));
		Connection con = null;
		try {
			con = this.driverConfigDB.getConnection("fillDettaglioPA_getMappingErogazione");
			dettaglioPA.setMappingErogazionePortaApplicativaOp2(DBMappingUtils.getMappingErogazione(idServizio, idPA, con, this.driverConfigDB.getTipoDB()));
			if(dettaglioPA.getMappingErogazionePortaApplicativaOp2().isDefault()) {
				dettaglioPA.setIdPortaApplicativaDefaultOp2(idPA);
			}
			else {
				dettaglioPA.setIdPortaApplicativaDefaultOp2(DBMappingUtils.getIDPortaApplicativaAssociataDefault(idServizio, con, this.driverConfigDB.getTipoDB()));
				dettaglioPA.setPortaApplicativaDefaultOp2(this.driverConfigDB.getPortaApplicativa(dettaglioPA.getIdPortaApplicativaDefaultOp2()));
			}
		}catch(Exception e) {
			throw new ServiceException(e.getMessage(),e);
		}
		finally {
			this.driverConfigDB.releaseConnection(con);
		}
		
		IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByOrganizationType(idPA.getIdentificativiErogazione().getIdServizio().getSoggettoErogatore().getTipo());

		AccordoServizioParteComune aspc = this.utilsServiceManager.getAccordoServizioParteComuneServiceSearch().get(dettaglioPA.getIdAccordoServizioParteComune());
		IPaginatedExpression gruppiExpr = this.utilsServiceManager.getAccordoServizioParteComuneGruppoServiceSearch().newPaginatedExpression();
		gruppiExpr.equals(AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME, aspc.getNome());
		gruppiExpr.equals(AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE, aspc.getVersione());
		gruppiExpr.equals(AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME, aspc.getIdReferente().getNome());
		gruppiExpr.equals(AccordoServizioParteComuneGruppo.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO, aspc.getIdReferente().getTipo());
		List<IdAccordoServizioParteComuneGruppo> lGruppi = this.utilsServiceManager.getAccordoServizioParteComuneGruppoServiceSearch().findAllIds(gruppiExpr);
		List<String> tags = new ArrayList<String>();
		if(lGruppi!=null && !lGruppi.isEmpty()) {
			for (IdAccordoServizioParteComuneGruppo gruppoCheck : lGruppi) {
				tags.add(gruppoCheck.getIdGruppo().getNome());
			}
		}
		
		String canaleApi = null;
		if(aspc!=null) {
			canaleApi = aspc.getCanale();
		}
		String canale = CanaliUtils.getCanale(this.driverConfigDB.getCanaliConfigurazione(false), canaleApi, dettaglioPA.getPortaApplicativa().getCanale());
		
		try {
			dettaglioPA.setRateLimiting(getDettaglioRateLimiting(idPA.getNome(), RuoloPolicy.APPLICATIVA));
		}catch(Exception e) {
			ConfigurazioniGeneraliService.log.error("Read RateLimiting PA["+idPA.getNome()+"] failed: "+e.getMessage(),e);
		}
		
		String nomePorta = idPA.getNome();
		if(dettaglioPA.getMappingErogazionePortaApplicativaOp2()!=null && !dettaglioPA.getMappingErogazionePortaApplicativaOp2().isDefault()) {
			if(dettaglioPA.getIdPortaApplicativaDefaultOp2()!=null) {
				nomePorta = dettaglioPA.getIdPortaApplicativaDefaultOp2().getNome();
			}
		}
		UrlInvocazioneAPI urlInvocazioneAPI = UrlInvocazioneAPI.getConfigurazioneUrlInvocazione(this.configurazioneUrlInvocazione, protocolFactory, RuoloContesto.PORTA_APPLICATIVA, 
				ConfigurazioniUtils.getServiceBindingFromValues(idServizio.getSoggettoErogatore().getTipo(), idServizio.getSoggettoErogatore().getNome(), 
						idServizio.getTipo(), idServizio.getNome(),idServizio.getVersione(), 
						this.utilsServiceManager), 
						nomePorta, idServizio.getSoggettoErogatore(),
						tags, canale);
		String urlInvocazione = urlInvocazioneAPI.getUrl();
		dettaglioPA.setUrlInvocazione(urlInvocazione);

		if("trasparente".equals(protocolFactory.getProtocol()))	{
			dettaglioPA.setTrasparente(true);
			dettaglioPA.setPropertyIntegrazione(ConfigurazioniUtils.getPropertiesIntegrazionePA(dettaglioPA));
		}	

		dettaglioPA.setPropertyGenerali(ConfigurazioniUtils.getPropertiesGeneraliPA(dettaglioPA));
		boolean supportatoAutenticazione = protocolFactory.createProtocolConfiguration().isSupportoAutenticazioneSoggetti();
		dettaglioPA.setSupportatoAutenticazione(supportatoAutenticazione);
		dettaglioPA.setPropertyAutenticazione(ConfigurazioniUtils.getPropertiesAutenticazionePA(dettaglioPA));
		dettaglioPA.setPropertyAutorizzazione(ConfigurazioniUtils.getPropertiesAutorizzazionePA(dettaglioPA, this.utilsServiceManager, this.driverRegistroDB)); 

		dettaglioPA.setListaSA(ConfigurazioniUtils.getPropertiesServiziApplicativiPA(dettaglioPA, this.driverConfigDB, idPA));

		if(CostantiLabel.MODIPA_PROTOCOL_NAME.equals(protocolFactory.getProtocol())) {
			dettaglioPA.setConfigurazioneProfilo(this.readConfigurazioneProfiloErogazione(idServizio, urlInvocazione));
		}
		
		configurazione.setPa(dettaglioPA); 
		return configurazione;
	}
	

	private String readConfigurazioneProfiloErogazione(IDServizio idServizio, String urlInvocazione) {
		
		try {
		
			AccordoServizioParteSpecifica asps = this.driverRegistroDB.getAccordoServizioParteSpecifica(idServizio,false);
			
			boolean useOldMethod = false;
			if(useOldMethod) {
				return this._old_readConfigurazioneProfiloErogazione(asps);
			}
			else {
				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
				org.openspcoop2.core.registry.AccordoServizioParteComune aspc = this.driverRegistroDB.getAccordoServizioParteComune(idAccordo, false);
				Map<String, String> map = ModIUtils.configToMap(aspc, asps, urlInvocazione, null, null);
				if(map!=null && !map.isEmpty()) {
					List<String> keys = new ArrayList<String>();
					keys.addAll(map.keySet());
					Collections.sort(keys);
					StringBuilder sb = new StringBuilder();
					for (String key : keys) {
						
						if(ModIUtils.isBooleanIndicator(key)) {
							continue;
						}
						
						if(sb.length()>0) {
							sb.append("\n");
						}
						sb.append(key);
						sb.append(":");
						String v = map.get(key);
						if(v!=null && StringUtils.isNotEmpty(v)) {
							if(v.contains("\n")){
								v = v.replaceAll("\n"," ");
							}
							sb.append(v);
						}
					}
					
					if(sb.length()>0) {
						return sb.toString();
					}
				}
			}
		
		}catch(Exception e) {
			ConfigurazioniGeneraliService.log.error("Read ProfiloInteroperabilità ModI configuration idServizio["+idServizio+"] failed: "+e.getMessage(),e);
		}
		return null;
	}
	private String _old_readConfigurazioneProfiloErogazione(AccordoServizioParteSpecifica asps) {
		List<ProtocolProperty> ppList = asps.getProtocolPropertyList();
		if(ppList!=null && !ppList.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			
			boolean keyStore = false;
			boolean trustStore = false;
			boolean trustStoreSsl = false;
			for (ProtocolProperty protocolProperty : ppList) {
				
				if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE.equals(protocolProperty.getName()) &&
						StringUtils.isNotEmpty(protocolProperty.getValue())) {
					boolean ridefinisci = CostantiDB.MODIPA_PROFILO_RIDEFINISCI.equals(protocolProperty.getValue());
					if(ridefinisci) {
						keyStore = true;
					}
				}
				else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE.equals(protocolProperty.getName()) &&
						StringUtils.isNotEmpty(protocolProperty.getValue())) {
					boolean ridefinisci = CostantiDB.MODIPA_PROFILO_RIDEFINISCI.equals(protocolProperty.getValue());
					if(ridefinisci) {
						trustStore = true;
					}
				}
				else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE.equals(protocolProperty.getName()) &&
						StringUtils.isNotEmpty(protocolProperty.getValue())) {
					boolean ridefinisci = CostantiDB.MODIPA_PROFILO_RIDEFINISCI.equals(protocolProperty.getValue());
					if(ridefinisci) {
						trustStoreSsl = true;
					}
				}
				
			}
			
			
			for (ProtocolProperty protocolProperty : ppList) {
				
				if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE.equals(protocolProperty.getName()) &&
						StringUtils.isNotEmpty(protocolProperty.getValue())) {
					if(sb.length()>0) {
						sb.append("\n");
					}
					sb.append("request-audience:").append(protocolProperty.getValue());
				}
				else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_AUDIENCE_INTEGRITY.equals(protocolProperty.getName()) &&
						StringUtils.isNotEmpty(protocolProperty.getValue())) {
					if(sb.length()>0) {
						sb.append("\n");
					}
					sb.append("request-integrity-audience:").append(protocolProperty.getValue());
				}
				else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE.equals(protocolProperty.getName()) &&
						StringUtils.isNotEmpty(protocolProperty.getValue())) {
					if(sb.length()>0) {
						sb.append("\n");
					}
					sb.append("response-audience:").append(protocolProperty.getValue());
				}
				else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_AUDIENCE_INTEGRITY.equals(protocolProperty.getName()) &&
						StringUtils.isNotEmpty(protocolProperty.getValue())) {
					if(sb.length()>0) {
						sb.append("\n");
					}
					sb.append("response-integrity-audience:").append(protocolProperty.getValue());
				}
				else {
					if(keyStore) {
						if(CostantiDB.MODIPA_KEYSTORE_MODE.equals(protocolProperty.getName()) &&
								StringUtils.isNotEmpty(protocolProperty.getValue())) {
							if(sb.length()>0) {
								sb.append("\n");
							}
							sb.append("response-keystore-mode:").append(protocolProperty.getValue());
							continue;
						}
						else if(CostantiDB.MODIPA_KEYSTORE_TYPE.equals(protocolProperty.getName()) &&
								StringUtils.isNotEmpty(protocolProperty.getValue())) {
							if(sb.length()>0) {
								sb.append("\n");
							}
							sb.append("response-keystore-type:").append(protocolProperty.getValue());
							continue;
						}
						else if(CostantiDB.MODIPA_KEYSTORE_PATH.equals(protocolProperty.getName()) &&
								StringUtils.isNotEmpty(protocolProperty.getValue())) {
							if(sb.length()>0) {
								sb.append("\n");
							}
							sb.append("response-keystore-path:").append(protocolProperty.getValue());
							continue;
						}
						else if(CostantiDB.MODIPA_KEY_ALIAS.equals(protocolProperty.getName()) &&
								StringUtils.isNotEmpty(protocolProperty.getValue())) {
							if(sb.length()>0) {
								sb.append("\n");
							}
							sb.append("response-key-alias:").append(protocolProperty.getValue());
							continue;
						}
					}
					if(trustStoreSsl) {
						if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE.equals(protocolProperty.getName()) &&
								StringUtils.isNotEmpty(protocolProperty.getValue())) {
							if(sb.length()>0) {
								sb.append("\n");
							}
							sb.append("request-truststore-ssl-type:").append(protocolProperty.getValue());
							continue;
						}
						else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH.equals(protocolProperty.getName()) &&
								StringUtils.isNotEmpty(protocolProperty.getValue())) {
							if(sb.length()>0) {
								sb.append("\n");
							}
							sb.append("request-truststore-ssl-path:").append(protocolProperty.getValue());
							continue;
						}
						else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS.equals(protocolProperty.getName()) &&
								StringUtils.isNotEmpty(protocolProperty.getValue())) {
							if(sb.length()>0) {
								sb.append("\n");
							}
							sb.append("request-truststore-ssl-crls:").append(protocolProperty.getValue());
							continue;
						}
					}
					if(trustStore) {
						if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE.equals(protocolProperty.getName()) &&
								StringUtils.isNotEmpty(protocolProperty.getValue())) {
							if(sb.length()>0) {
								sb.append("\n");
							}
							sb.append("request-truststore-type:").append(protocolProperty.getValue());
							continue;
						}
						else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH.equals(protocolProperty.getName()) &&
								StringUtils.isNotEmpty(protocolProperty.getValue())) {
							if(sb.length()>0) {
								sb.append("\n");
							}
							sb.append("request-truststore-path:").append(protocolProperty.getValue());
							continue;
						}
						else if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS.equals(protocolProperty.getName()) &&
								StringUtils.isNotEmpty(protocolProperty.getValue())) {
							if(sb.length()>0) {
								sb.append("\n");
							}
							sb.append("request-truststore-crls:").append(protocolProperty.getValue());
							continue;
						}
					}
				}
				
			}
			
			if(sb.length()>0) {
				return sb.toString();
			}
		}
		
		return null;
	}
	
	@Override
	public ConfigurazioniGeneraliSearchForm getSearch() {
		return this.search;
	}
	@Override
	public void setSearch(ConfigurazioniGeneraliSearchForm search) {
		this.search = search;
	}

	@Override
	public List<ConfigurazioneGenerale> findAllInformazioniGenerali()
			throws ServiceException {

		List<ConfigurazioneGenerale> lista = new ArrayList<ConfigurazioneGenerale>();
		try{
			ConfigurazioniGeneraliService.log.debug("--------------- findAllInformazioniGenerali ------------");
			ConfigurazioniGeneraliService.log.debug("--------------- Filtro Impostato ------------");
			ConfigurazioniGeneraliService.log.debug("Protocollo: ["+this.search.getProtocollo()+"]"); 
			ConfigurazioniGeneraliService.log.debug("Soggetto : ["+this.search.getSoggettoLocale()+"]");
			ConfigurazioniGeneraliService.log.debug("Servizio: ["+this.search.getNomeServizio()+"]");

			ConfigurazioniGeneraliService.log.debug("------------ Fine Filtro Impostato ----------");

			// user no multitenant
			if(!Utility.isMultitenantAbilitato()) {
				lista.add(getSoggettiEsterni());
			} else {
			// user multi tenant
				lista.add(getSoggettiOperativi()); 
				lista.add(getSoggettiEsterni());
			}
			// applicativi
			lista.add(getApplicativi());

		}catch(Exception e){
			ConfigurazioniGeneraliService.log.error("Errore durante la ricerca delle Configurazioni: " + e.getMessage(),e);
		}
		return lista;
	}

	@Override
	public List<ConfigurazioneGenerale> findAllInformazioniServizi()
			throws ServiceException {
		List<ConfigurazioneGenerale> lista = new ArrayList<ConfigurazioneGenerale>();
		try{
			ConfigurazioniGeneraliService.log.debug("--------------- findAllInformazioniServizi ------------");
			ConfigurazioniGeneraliService.log.debug("--------------- Filtro Impostato ------------");
			ConfigurazioniGeneraliService.log.debug("Protocollo: ["+this.search.getProtocollo()+"]");
			ConfigurazioniGeneraliService.log.debug("Soggetto : ["+this.search.getSoggettoLocale()+"]");
			ConfigurazioniGeneraliService.log.debug("Servizio: ["+this.search.getNomeServizio()+"]");

			ConfigurazioniGeneraliService.log.debug("------------ Fine Filtro Impostato ----------");

			lista.add(getAccordiServizioParteComune());
			if (StringUtils.isNotBlank(this.getSearch().getNomeServizio()))
				lista.add(getAzioni());
			lista.add(getErogazioniServizio()); 
			lista.add(getFruizioniServizio());
		}catch(Exception e){
			ConfigurazioniGeneraliService.log.error("Errore durante la ricerca delle Configurazioni: " + e.getMessage(),e);
		}
		return lista;
	}

	private IExpression createPDExpression(IServiceSearchWithId<?, ?> dao, ConfigurazioniGeneraliSearchForm searchForm, boolean count)
			throws ExpressionNotImplementedException, ExpressionException, ServiceException, NotImplementedException, CoreException, UserInvalidException {
		IExpression expr = dao.newExpression();

		if(searchForm == null)
			return expr;

		expr.and();
		expr.isNotNull(PortaDelegata.model().TIPO_SOGGETTO_EROGATORE);
		expr.isNotNull(PortaDelegata.model().NOME_SOGGETTO_EROGATORE);
		expr.isNotNull(PortaDelegata.model().TIPO_SERVIZIO);
		expr.isNotNull(PortaDelegata.model().NOME_SERVIZIO);
		expr.isNotNull(PortaDelegata.model().VERSIONE_SERVIZIO);

		if(searchForm.getPermessiUtenteOperatore()!=null){
			IExpression permessi = searchForm.getPermessiUtenteOperatore().toExpressionConfigurazioneServizi(dao, 
					PortaDelegata.model().ID_SOGGETTO.TIPO, PortaDelegata.model().ID_SOGGETTO.NOME, 
					PortaDelegata.model().TIPO_SOGGETTO_EROGATORE, PortaDelegata.model().NOME_SOGGETTO_EROGATORE, 
					PortaDelegata.model().TIPO_SERVIZIO, PortaDelegata.model().NOME_SERVIZIO, PortaDelegata.model().VERSIONE_SERVIZIO,
					false);
			expr.and(permessi);
		}

		// Protocollo
		String protocollo = null;
		// aggiungo la condizione sul protocollo se e' impostato e se e' presente piu' di un protocollo
		// protocollo e' impostato anche scegliendo la modalita'
		//		if (StringUtils.isNotEmpty(searchForm.getProtocollo()) && searchForm.isShowListaProtocolli()) {
		if (searchForm.isSetFiltroProtocollo()) {
			protocollo = searchForm.getProtocollo();
			impostaTipiCompatibiliConProtocollo(dao, PortaDelegata.model(), expr, protocollo);
		}


		if(searchForm.getTipoNomeSoggettoLocale()!=null && 
				!StringUtils.isEmpty(searchForm.getTipoNomeSoggettoLocale()) && !"--".equals(searchForm.getTipoNomeSoggettoLocale())){

			String tipoSoggettoDestinatario = searchForm.getTipoSoggettoLocale();
			String nomeSoggettoDestinatario = searchForm.getSoggettoLocale();

			if(tipoSoggettoDestinatario!=null && !Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(tipoSoggettoDestinatario)) {
				expr.equals(PortaDelegata.model().ID_SOGGETTO.TIPO,tipoSoggettoDestinatario);
			}
			if(nomeSoggettoDestinatario!=null && !Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(nomeSoggettoDestinatario)) {
				expr.equals(PortaDelegata.model().ID_SOGGETTO.NOME,nomeSoggettoDestinatario);
			}
		}
		if (StringUtils.isNotBlank(searchForm.getNomeServizio())) {

			String servizioString = searchForm.getNomeServizio();
			IDServizio idServizio = ParseUtility.parseServizioSoggetto(servizioString);

			expr.equals(PortaDelegata.model().TIPO_SOGGETTO_EROGATORE,idServizio.getSoggettoErogatore().getTipo());
			expr.equals(PortaDelegata.model().NOME_SOGGETTO_EROGATORE,idServizio.getSoggettoErogatore().getNome());
			expr.equals(PortaDelegata.model().TIPO_SERVIZIO,idServizio.getTipo());
			expr.equals(PortaDelegata.model().NOME_SERVIZIO,idServizio.getNome());
			expr.equals(PortaDelegata.model().VERSIONE_SERVIZIO,idServizio.getVersione());
		}
		
		// seleziono solo le porte di default
		expr.notEquals(PortaDelegata.model().MODE_AZIONE, CostantiConfigurazioni.VALUE_PORTE_DELEGATED_BY);

		if(!count) {
			expr.sortOrder(SortOrder.ASC);
			expr.addOrder(PortaDelegata.model().ID_SOGGETTO.TIPO);
			expr.addOrder(PortaDelegata.model().ID_SOGGETTO.NOME);
			expr.addOrder(PortaDelegata.model().TIPO_SOGGETTO_EROGATORE);
			expr.addOrder(PortaDelegata.model().NOME_SOGGETTO_EROGATORE);
			expr.addOrder(PortaDelegata.model().TIPO_SERVIZIO);
			expr.addOrder(PortaDelegata.model().NOME_SERVIZIO);
			expr.addOrder(PortaDelegata.model().VERSIONE_SERVIZIO);
			expr.addOrder(PortaDelegata.model().NOME_AZIONE);
		}
		return expr;
	}

	private IExpression createPAExpression(IServiceSearchWithId<?, ?> dao, ConfigurazioniGeneraliSearchForm searchForm, boolean count) 
			throws ExpressionNotImplementedException, ExpressionException, ServiceException, NotImplementedException, CoreException, UserInvalidException{
		IExpression expr = dao.newExpression();

		if(searchForm == null)
			return expr;

		expr.isNotNull(PortaApplicativa.model().TIPO_SERVIZIO);
		expr.isNotNull(PortaApplicativa.model().NOME_SERVIZIO);
		expr.isNotNull(PortaApplicativa.model().VERSIONE_SERVIZIO);

		if(searchForm.getPermessiUtenteOperatore()!=null){
			IExpression permessi = searchForm.getPermessiUtenteOperatore().toExpressionConfigurazioneServizi(dao, 
					PortaApplicativa.model().ID_SOGGETTO.TIPO, PortaApplicativa.model().ID_SOGGETTO.NOME, 
					PortaApplicativa.model().ID_SOGGETTO.TIPO, PortaApplicativa.model().ID_SOGGETTO.NOME, 
					PortaApplicativa.model().TIPO_SERVIZIO, PortaApplicativa.model().NOME_SERVIZIO,PortaApplicativa.model().VERSIONE_SERVIZIO,
					false);
			expr.and(permessi);
		}

		// Protocollo
		String protocollo = null;
		// aggiungo la condizione sul protocollo se e' impostato e se e' presente piu' di un protocollo
		// protocollo e' impostato anche scegliendo la modalita'
		//		if (StringUtils.isNotEmpty(searchForm.getProtocollo()) && searchForm.isShowListaProtocolli()) {
		if (searchForm.isSetFiltroProtocollo()) {
			protocollo = searchForm.getProtocollo();
			impostaTipiCompatibiliConProtocollo(dao, PortaApplicativa.model(), expr, protocollo);
		}

		boolean setSoggettoProprietario = false;
		if(searchForm.getTipoNomeSoggettoLocale()!=null && 
				!StringUtils.isEmpty(searchForm.getTipoNomeSoggettoLocale()) && !"--".equals(searchForm.getTipoNomeSoggettoLocale())){

			String tipoSoggettoDestinatario = searchForm.getTipoSoggettoLocale();
			String nomeSoggettoDestinatario = searchForm.getSoggettoLocale();

			if(tipoSoggettoDestinatario!=null && !Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(tipoSoggettoDestinatario)) {
				expr.equals(PortaApplicativa.model().ID_SOGGETTO.TIPO,tipoSoggettoDestinatario);
			}
			if(nomeSoggettoDestinatario!=null && !Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(nomeSoggettoDestinatario)) {
				expr.equals(PortaApplicativa.model().ID_SOGGETTO.NOME,nomeSoggettoDestinatario);
			}
			setSoggettoProprietario = true;
		}

		if (StringUtils.isNotBlank(searchForm.getNomeServizio())) {

			String servizioString = searchForm.getNomeServizio();
			IDServizio idServizio = ParseUtility.parseServizioSoggetto(servizioString);

			expr.equals(PortaApplicativa.model().TIPO_SERVIZIO,idServizio.getTipo());
			expr.equals(PortaApplicativa.model().NOME_SERVIZIO,idServizio.getNome());
			expr.equals(PortaApplicativa.model().VERSIONE_SERVIZIO,idServizio.getVersione());
			if(setSoggettoProprietario==false){
				expr.equals(PortaApplicativa.model().ID_SOGGETTO.TIPO,idServizio.getSoggettoErogatore().getTipo());
				expr.equals(PortaApplicativa.model().ID_SOGGETTO.NOME,idServizio.getSoggettoErogatore().getNome());
			}
		}
		
		// seleziono solo le porte di default
		expr.notEquals(PortaApplicativa.model().MODE_AZIONE, CostantiConfigurazioni.VALUE_PORTE_DELEGATED_BY);

		if(!count){
			expr.sortOrder(SortOrder.ASC);
			expr.addOrder(PortaApplicativa.model().ID_SOGGETTO.TIPO);
			expr.addOrder(PortaApplicativa.model().ID_SOGGETTO.NOME);
			expr.addOrder(PortaApplicativa.model().TIPO_SERVIZIO);
			expr.addOrder(PortaApplicativa.model().NOME_SERVIZIO);
			expr.addOrder(PortaApplicativa.model().VERSIONE_SERVIZIO);
			expr.addOrder(PortaApplicativa.model().NOME_AZIONE);
		}

		return expr;		

	}

	private ConfigurazioneGenerale fromPD(PortaDelegata portaDelegata){
		ConfigurazioneGenerale configurazioneGenerale = new ConfigurazioneGenerale(portaDelegata.getId(),PddRuolo.DELEGATA);

		configurazioneGenerale.setStato(portaDelegata.getStato());
		try {
			String tipoProtocollo = this.search.getProtocollo();
			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(tipoProtocollo);
			PorteNamingUtils n = new PorteNamingUtils(protocolFactory);
			configurazioneGenerale.setLabel(n.normalizePD(portaDelegata.getNome())); 

			configurazioneGenerale.setProtocollo(tipoProtocollo); 

			if(portaDelegata.getNomeAzione()==null){
				configurazioneGenerale.setAzione(CostantiConfigurazioni.LABEL_AZIONE_STAR);
			}
			else{
				configurazioneGenerale.setAzione(portaDelegata.getNomeAzione());
			}

			configurazioneGenerale.setErogatore(NamingUtils.getLabelSoggetto(tipoProtocollo, portaDelegata.getTipoSoggettoErogatore(),portaDelegata.getNomeSoggettoErogatore()));
			configurazioneGenerale.setFruitore(NamingUtils.getLabelSoggetto(tipoProtocollo, portaDelegata.getIdSoggetto().getTipo(),portaDelegata.getIdSoggetto().getNome()));

			String labelServizio = NamingUtils.getLabelAccordoServizioParteSpecificaSenzaErogatore(tipoProtocollo, portaDelegata.getTipoServizio(), portaDelegata.getNomeServizio(), portaDelegata.getVersioneServizio());
			configurazioneGenerale.setServizio(labelServizio);
		}catch(Exception e){
			log.error(e.getMessage(),e);
		}
		return configurazioneGenerale;
	}

	private ConfigurazioneGenerale fromPA(PortaApplicativa portaApplicativa){
		ConfigurazioneGenerale configurazioneGenerale = new ConfigurazioneGenerale(portaApplicativa.getId(),PddRuolo.APPLICATIVA);
		configurazioneGenerale.setStato(portaApplicativa.getStato());
		try {
			String tipoProtocollo = this.search.getProtocollo();
			
			configurazioneGenerale.setProtocollo(tipoProtocollo);

			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(tipoProtocollo);
			PorteNamingUtils n = new PorteNamingUtils(protocolFactory);
			configurazioneGenerale.setLabel(n.normalizePA(portaApplicativa.getNome())); 

			configurazioneGenerale.setErogatore(NamingUtils.getLabelSoggetto(tipoProtocollo, portaApplicativa.getIdSoggetto().getTipo(), portaApplicativa.getIdSoggetto().getNome()));
			String labelServizio = NamingUtils.getLabelAccordoServizioParteSpecificaSenzaErogatore(tipoProtocollo, portaApplicativa.getTipoServizio(), portaApplicativa.getNomeServizio(), portaApplicativa.getVersioneServizio());
			configurazioneGenerale.setServizio(labelServizio);
			if(portaApplicativa.getNomeAzione()==null){
				configurazioneGenerale.setAzione(CostantiConfigurazioni.LABEL_AZIONE_STAR);
			}
			else{
				configurazioneGenerale.setAzione(portaApplicativa.getNomeAzione()); 
			}
		}catch(Exception e){
			log.error(e.getMessage(),e);
		}
		return configurazioneGenerale;
	}

	private void impostaTipiCompatibiliConProtocollo(IServiceSearchWithId<?, ?> dao, PortaDelegataModel model,	IExpression expr, String protocollo) throws ExpressionNotImplementedException, ExpressionException {
		// Se ho selezionato il protocollo il tipo dei servizi da includere nei risultati deve essere compatibile col protocollo scelto.
		IExpression expressionTipoServiziCompatibili = null;
		try {
			if(protocollo != null) {
				expressionTipoServiziCompatibili = DynamicUtilsServiceEngine.getExpressionTipiServiziCompatibiliConProtocollo(dao, model.TIPO_SERVIZIO, protocollo);
			}
		} catch (Exception e) {
			log.error("Si e' verificato un errore durante il calcolo dei tipi servizio compatibili con il protocollo scelto: "+ e.getMessage(), e);
		}

		if(expressionTipoServiziCompatibili != null)
			expr.and(expressionTipoServiziCompatibili);

		// Se ho selezionato il protocollo il tipo dei servizi da includere nei risultati deve essere compatibile col protocollo scelto.
		IExpression expressionTipoSoggettiMittenteCompatibili = null;
		try {
			if(protocollo != null) {
				expressionTipoSoggettiMittenteCompatibili = DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(dao, model.ID_SOGGETTO.TIPO, protocollo);
			}
		} catch (Exception e) {
			log.error("Si e' verificato un errore durante il calcolo dei tipi soggetto mittente compatibili con il protocollo scelto: "+ e.getMessage(), e);
		}

		if(expressionTipoSoggettiMittenteCompatibili != null)
			expr.and(expressionTipoSoggettiMittenteCompatibili);


		// Se ho selezionato il protocollo il tipo dei servizi da includere nei risultati deve essere compatibile col protocollo scelto.
		IExpression expressionTipoSoggettiDestinatarioCompatibili = null;
		try {
			if(protocollo != null) {
				expressionTipoSoggettiDestinatarioCompatibili = DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(dao, model.TIPO_SOGGETTO_EROGATORE, protocollo);
			}
		} catch (Exception e) {
			log.error("Si e' verificato un errore durante il calcolo dei tipi soggetto destinatario compatibili con il protocollo scelto: "+ e.getMessage(), e);
		}

		if(expressionTipoSoggettiDestinatarioCompatibili != null)
			expr.and(expressionTipoSoggettiDestinatarioCompatibili);
	}

	private void impostaTipiCompatibiliConProtocollo(IServiceSearchWithId<?, ?> dao, PortaApplicativaModel model,	IExpression expr, String protocollo) throws ExpressionNotImplementedException, ExpressionException {
		// Se ho selezionato il protocollo il tipo dei servizi da includere nei risultati deve essere compatibile col protocollo scelto.
		IExpression expressionTipoServiziCompatibili = null;
		try {
			if(protocollo != null) {
				expressionTipoServiziCompatibili = DynamicUtilsServiceEngine.getExpressionTipiServiziCompatibiliConProtocollo(dao, model.TIPO_SERVIZIO, protocollo);
			}
		} catch (Exception e) {
			log.error("Si e' verificato un errore durante il calcolo dei tipi servizio compatibili con il protocollo scelto: "+ e.getMessage(), e);
		}

		if(expressionTipoServiziCompatibili != null)
			expr.and(expressionTipoServiziCompatibili);

		// Se ho selezionato il protocollo il tipo dei servizi da includere nei risultati deve essere compatibile col protocollo scelto.
		IExpression expressionTipoSoggettiDestinatarioCompatibili = null;
		try {
			if(protocollo != null) {
				expressionTipoSoggettiDestinatarioCompatibili = DynamicUtilsServiceEngine.getExpressionTipiSoggettiCompatibiliConProtocollo(dao, model.ID_SOGGETTO.TIPO, protocollo);
			}
		} catch (Exception e) {
			log.error("Si e' verificato un errore durante il calcolo dei tipi soggetto destinatario compatibili con il protocollo scelto: "+ e.getMessage(), e);
		}

		if(expressionTipoSoggettiDestinatarioCompatibili != null)
			expr.and(expressionTipoSoggettiDestinatarioCompatibili);
	}
	
	private DettaglioRateLimiting getDettaglioRateLimiting(String nomePorta, org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy ruoloPorta) throws Exception {
		
		IPaginatedExpression expr = this.attivazionePolicyDAO.newPaginatedExpression();
		
		expr.equals(AttivazionePolicy.model().FILTRO.RUOLO_PORTA, ruoloPorta);
		expr.equals(AttivazionePolicy.model().FILTRO.NOME_PORTA, nomePorta);
		
		int offset = 0;
		int limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
		expr.offset(offset).limit(limit);
		
		expr.sortOrder(SortOrder.ASC);
		expr.addOrder(AttivazionePolicy.model().ID_POLICY);
		expr.addOrder(AttivazionePolicy.model().ALIAS);
		
		List<AttivazionePolicy> l = this.attivazionePolicyDAO.findAll(expr);
		if(l!=null && !l.isEmpty()) {
			
			DettaglioRateLimiting d = new DettaglioRateLimiting();
			
			for (AttivazionePolicy attivazionePolicy : l) {
				
				IdPolicy idPolicy = new IdPolicy();
				idPolicy.setNome(attivazionePolicy.getIdPolicy());
				ConfigurazionePolicy cp = this.configurazionePolicyDAO.get(idPolicy);
				
				d.addDetail(attivazionePolicy, cp);
			}
			
			return d;
		}
		
		return null;
	}
	
	private List<IDServizio> getListServizi(boolean apiImplSelected, String gruppo, IDAccordo idAccordo,
			String tipoProtocollo, String tipoSoggetto, String nomeSoggetto,
			String input) throws Exception {
		List<IDServizio> listIDServizio = null;
		if(!apiImplSelected && ( (gruppo!=null && !"".equals(gruppo)) || idAccordo!=null ) ) {
			listIDServizio = new ArrayList<IDServizio>();
			List<SelectItem> list = null;
			boolean distinct = true;
			if(PddRuolo.DELEGATA.equals(this.search.getTipologiaTransazioni())) {
				list = this.dynamicUtils.getListaSelectItemsElencoConfigurazioneServiziFruizione(tipoProtocollo, gruppo, idAccordo, tipoSoggetto, nomeSoggetto,null,null,input, false, this.search.getPermessiUtenteOperatore(), distinct);
			}else {
				// bisogna filtrare per soggetti operativi
				list = this.dynamicUtils.getListaSelectItemsElencoConfigurazioneServiziErogazione(tipoProtocollo, gruppo, idAccordo, tipoSoggetto, nomeSoggetto,input, true, this.search.getPermessiUtenteOperatore(), distinct);
			}
			if(list!=null && list.size()>0) {
				for (SelectItem selectItem : list) {
					String servizioString = (String) selectItem.getValue();
					IDServizio idServizio = ParseUtility.parseServizioSoggetto(servizioString);
					listIDServizio.add(idServizio);
				}
			}
			else {
				// creo un servizio non esistente per fornire 0 dati
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues("-", "-", "-", "-", -1);
				listIDServizio.add(idServizio);
			}
		}
		return listIDServizio;
	}
	private void setListServiziPD(boolean apiImplSelected, List<IDServizio> listIDServizio, IExpression pagExpr) throws Exception {
		if(!apiImplSelected && listIDServizio!=null && !listIDServizio.isEmpty()) {

			List<IExpression> exprOrList = new ArrayList<IExpression>();
			for (IDServizio idServizio : listIDServizio) {
				
				IExpression exprIdServizio = this.portaDelegataDAO.newExpression();
				exprIdServizio.equals(PortaDelegata.model().TIPO_SOGGETTO_EROGATORE,idServizio.getSoggettoErogatore().getTipo());
				exprIdServizio.equals(PortaDelegata.model().NOME_SOGGETTO_EROGATORE,idServizio.getSoggettoErogatore().getNome());
				exprIdServizio.equals(PortaDelegata.model().TIPO_SERVIZIO,idServizio.getTipo());
				exprIdServizio.equals(PortaDelegata.model().NOME_SERVIZIO,idServizio.getNome());
				exprIdServizio.equals(PortaDelegata.model().VERSIONE_SERVIZIO,idServizio.getVersione());
				exprOrList.add(exprIdServizio);
				
			}
			pagExpr.or(exprOrList.toArray(new IExpression[1]));

		}
	}
	private void setListServiziPA(boolean apiImplSelected, List<IDServizio> listIDServizio, IExpression pagExpr) throws Exception {
		if(!apiImplSelected && listIDServizio!=null && !listIDServizio.isEmpty()) {

			List<IExpression> exprOrList = new ArrayList<IExpression>();
			for (IDServizio idServizio : listIDServizio) {
				IExpression exprIdServizio = this.portaApplicativaDAO.newExpression();
				exprIdServizio.equals(PortaApplicativa.model().TIPO_SERVIZIO,idServizio.getTipo());
				exprIdServizio.equals(PortaApplicativa.model().NOME_SERVIZIO,idServizio.getNome());
				exprIdServizio.equals(PortaApplicativa.model().VERSIONE_SERVIZIO,idServizio.getVersione());
				boolean setSoggettoProprietario = false;
				if(this.search.getTipoNomeSoggettoLocale()!=null && 
						!StringUtils.isEmpty(this.search.getTipoNomeSoggettoLocale()) && !"--".equals(this.search.getTipoNomeSoggettoLocale())){
					setSoggettoProprietario = true; // impostato dentro createPAExpression
				}
				if(setSoggettoProprietario==false){
					exprIdServizio.equals(PortaApplicativa.model().ID_SOGGETTO.TIPO,idServizio.getSoggettoErogatore().getTipo());
					exprIdServizio.equals(PortaApplicativa.model().ID_SOGGETTO.NOME,idServizio.getSoggettoErogatore().getNome());
				}
				exprOrList.add(exprIdServizio);
				
			}
			pagExpr.or(exprOrList.toArray(new IExpression[1]));

		}
	}
}
