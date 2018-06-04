package org.openspcoop2.web.monitor.statistiche.dao;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.ConfigurazioneProtocolli;
import org.openspcoop2.core.config.ConfigurazioneProtocollo;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.id.IdentificativiErogazione;
import org.openspcoop2.core.id.IdentificativiFruizione;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
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
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.slf4j.Logger;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.core.commons.search.AccordoServizioParteComune;
import org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComune;
import org.openspcoop2.core.commons.search.PortaApplicativa;
import org.openspcoop2.core.commons.search.PortaDelegata;
import org.openspcoop2.core.commons.search.constants.TipoPdD;
import org.openspcoop2.core.commons.search.dao.IDBPortaApplicativaServiceSearch;
import org.openspcoop2.core.commons.search.dao.IDBPortaDelegataServiceSearch;
import org.openspcoop2.core.commons.search.dao.IPortaApplicativaServiceSearch;
import org.openspcoop2.core.commons.search.dao.IPortaDelegataServiceSearch;
import org.openspcoop2.core.commons.search.model.PortaApplicativaModel;
import org.openspcoop2.core.commons.search.model.PortaDelegataModel;
import org.openspcoop2.web.monitor.core.bean.UserDetailsBean;
import org.openspcoop2.web.monitor.core.utils.ParseUtility;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.dao.DynamicUtilsService;
import org.openspcoop2.web.monitor.core.dao.IDynamicUtilsService;
import org.openspcoop2.web.monitor.core.exception.UserInvalidException;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioneGenerale;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioneGeneralePK;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioniGeneraliSearchForm;
import org.openspcoop2.web.monitor.statistiche.bean.DettaglioPA;
import org.openspcoop2.web.monitor.statistiche.bean.DettaglioPD;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiConfigurazioni;
import org.openspcoop2.web.monitor.statistiche.utils.ConfigurazioniUtils;

public class ConfigurazioniGeneraliService implements IConfigurazioniGeneraliService {

	private ConfigurazioniGeneraliSearchForm search = null;

	private static Logger log =  LoggerManager.getPddMonitorSqlLogger();

	private transient IDynamicUtilsService dynamicService = null;

	private transient org.openspcoop2.core.commons.search.dao.IServiceManager utilsServiceManager;

	private IPortaDelegataServiceSearch portaDelegataDAO = null;
	private IPortaApplicativaServiceSearch portaApplicativaDAO  = null;

	private transient DriverConfigurazioneDB driverConfigDB = null;
	private transient DriverRegistroServiziDB driverRegistroDB = null;

	private Hashtable<String, String> endpointApplicativoPD = null;
	private Hashtable<String, String> endpointApplicativoPA = null;

	public ConfigurazioniGeneraliService(){
		try{
			this.dynamicService = new DynamicUtilsService();

			this.utilsServiceManager = (org.openspcoop2.core.commons.search.dao.IServiceManager) DAOFactory
					.getInstance( ConfigurazioniGeneraliService.log).getServiceManager(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance(), ConfigurazioniGeneraliService.log);

			this.portaApplicativaDAO = this.utilsServiceManager.getPortaApplicativaServiceSearch();
			this.portaDelegataDAO = this.utilsServiceManager.getPortaDelegataServiceSearch();

			String tipoDatabase = DAOFactoryProperties.getInstance(ConfigurazioniGeneraliService.log).getTipoDatabase(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
			String datasourceJNDIName = DAOFactoryProperties.getInstance(ConfigurazioniGeneraliService.log).getDatasourceJNDIName(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
			Properties datasourceJNDIContext = DAOFactoryProperties.getInstance(ConfigurazioniGeneraliService.log).getDatasourceJNDIContext(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());


			this.driverConfigDB = new DriverConfigurazioneDB(datasourceJNDIName,datasourceJNDIContext, ConfigurazioniGeneraliService.log, tipoDatabase);
			this.driverRegistroDB = new DriverRegistroServiziDB(datasourceJNDIName,datasourceJNDIContext, ConfigurazioniGeneraliService.log, tipoDatabase);

			ConfigurazioneProtocolli proto = this.driverConfigDB.getConfigurazioneGenerale().getProtocolli();
			if(proto!=null && proto.sizeProtocolloList()>0) {
				this.endpointApplicativoPD = new Hashtable<>();
				this.endpointApplicativoPA = new Hashtable<>();
			}
			for (ConfigurazioneProtocollo configProtocollo : proto.getProtocolloList()) {
				this.endpointApplicativoPD.put(configProtocollo.getNome(), configProtocollo.getUrlInvocazioneServizioPD());
				this.endpointApplicativoPA.put(configProtocollo.getNome(), configProtocollo.getUrlInvocazioneServizioPA());
			}

		}catch(Exception e){
			ConfigurazioniGeneraliService.log.error("Errore durante la creazione del Service: " + e.getMessage(),e);
		}
	}

	@Override
	public List<ConfigurazioneGenerale> findAll() {
		ConfigurazioniGeneraliService.log.debug("Metodo FindAll");

		try {
			if(PddRuolo.DELEGATA.equals(this.search.getTipologiaTransazioni())) {
				IExpression expr = this.createPDExpression(this.portaDelegataDAO, this.search, false);
				IPaginatedExpression pagExpr = this.portaDelegataDAO.toPaginatedExpression(expr);


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
		boolean isReferente = true;
		String tipoSoggetto = null;
		String nomeSoggetto =  null;

		String tipoProtocollo = this.search.getProtocollo();


		try {
			if (StringUtils.isNotBlank(this.getSearch().getNomeServizio())){
				String servizioString = this.getSearch().getNomeServizio();
				IDServizio idServizio = ParseUtility.parseServizioSoggetto(servizioString);
				AccordoServizioParteComune aspc = this.dynamicService.getAccordoServizio(tipoProtocollo, idServizio.getSoggettoErogatore(), idServizio.getTipo(), idServizio.getNome());
				if(aspc != null){
					String nomeAspc = aspc.getNome();

					Integer versioneAspc = aspc.getVersione();

					String nomeReferenteAspc = (aspc.getIdReferente() != null) ? aspc.getIdReferente().getNome() : null;

					String tipoReferenteAspc= (aspc.getIdReferente() != null) ? aspc.getIdReferente().getTipo() : null;

					value = IDAccordoFactory.getInstance().getUriFromValues(nomeAspc,tipoReferenteAspc,nomeReferenteAspc,versioneAspc);
				} else {
					value = "--";
				}
			}else {
				ConfigurazioniGeneraliService.log.debug("Calcolo numero " + CostantiConfigurazioni.CONF_ASPC_LABEL);
				if(this.getSearch().getTipoNomeSoggettoLocale()!=null && !StringUtils.isEmpty(this.getSearch().getTipoNomeSoggettoLocale()) 
						&& !"--".equals(this.getSearch().getTipoNomeSoggettoLocale())){
					tipoSoggetto = this.getSearch().getTipoSoggettoLocale();
					nomeSoggetto = this.getSearch().getSoggettoLocale();
					count =   this.dynamicService.countAccordiServizio(tipoProtocollo,tipoSoggetto, nomeSoggetto, isReferente, isErogatore);
				}else {
					// non ho selezionato ne servizio ne soggetto
					UserDetailsBean user = Utility.getLoggedUser();

					if(!user.isAdmin()) {
						List<AccordoServizioParteComune> accordiServizio = this.dynamicService.getAccordiServizio(tipoProtocollo,tipoSoggetto, nomeSoggetto, isReferente, isErogatore);

						for (AccordoServizioParteComune accordoServizioParteComune : accordiServizio) {
							// controllo sul soggetto
							boolean existsPermessoSoggetto = false;
							if(user.getSizeSoggetti()>0){
								for (IDSoggetto utenteSoggetto : user.getUtenteSoggettoList()) {
									if(accordoServizioParteComune.getIdReferente().getTipo().equals(utenteSoggetto.getTipo()) &&
											accordoServizioParteComune.getIdReferente().getNome().equals(utenteSoggetto.getNome())){
										existsPermessoSoggetto = true;
										break;
									}
								}
							}

							boolean existsPermessoServizio = false;
							if(!existsPermessoSoggetto){
								if(user.getSizeServizio()>0){
									for (IDServizio utenteSoggetto : user.getUtenteServizioList()) {
										AccordoServizioParteSpecifica asps = this.dynamicService.getAspsFromValues(utenteSoggetto.getTipo(), utenteSoggetto.getNome(),
												utenteSoggetto.getSoggettoErogatore().getTipo(), utenteSoggetto.getSoggettoErogatore().getNome());

										IdAccordoServizioParteComune idAccordoServizioParteComune = asps.getIdAccordoServizioParteComune();

										// l'accordo parte comune deve coincidere con l'erogazione associata all'utente 
										if(idAccordoServizioParteComune.getIdSoggetto().getTipo().equals(accordoServizioParteComune.getIdReferente().getTipo()) &&
												idAccordoServizioParteComune.getIdSoggetto().getNome().equals(accordoServizioParteComune.getIdReferente().getNome()) &&
												idAccordoServizioParteComune.getVersione().equals(accordoServizioParteComune.getVersione()) &&
												idAccordoServizioParteComune.getNome().equals(accordoServizioParteComune.getNome())){
											existsPermessoServizio = true;
											break;
										}
									}
								}
							}

							// colleziono le entry visibili all'operatore
							if(existsPermessoServizio || existsPermessoSoggetto)
								count ++;
						}

					}  else {
						// utente amministratore
						count =   this.dynamicService.countAccordiServizio(tipoProtocollo,tipoSoggetto, nomeSoggetto, isReferente, isErogatore);
					}
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

				count =   this.dynamicService.countAzioniFromAccordoServizio(tipoProtocollo, null, tipoServizio, nomeServizio,tipoErogatore,nomeErogatore,null);
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

			count = this.dynamicService.countPorteApplicative(tipoProtocollo,null,tipoSoggetto,nomeSoggetto,tipoServizio,nomeServizio,tipoErogatore,nomeErogatore,null, this.search.getPermessiUtenteOperatore());
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

			count =   this.dynamicService.countPorteDelegate(tipoProtocollo,null,tipoSoggetto,nomeSoggetto,tipoServizio,nomeServizio,tipoErogatore,nomeErogatore,null, this.search.getPermessiUtenteOperatore());
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
			if(PddRuolo.DELEGATA.equals(this.search.getTipologiaTransazioni())) {
				IExpression expr = this.createPDExpression(this.portaDelegataDAO, this.search, false);
				IPaginatedExpression pagExpr = this.portaDelegataDAO.toPaginatedExpression(expr);
				pagExpr.offset(start).limit(limit);


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
		}

		return new ArrayList<ConfigurazioneGenerale>();
	}
	@Override
	public List<ConfigurazioneGenerale> findAllDettagli(int start, int limit) {
		ConfigurazioniGeneraliService.log.debug("Metodo findAllDettagli: start[" + start + "], limit: [" + limit + "]");

		try {
			if(PddRuolo.DELEGATA.equals(this.search.getTipologiaTransazioni())) {
				IExpression expr = this.createPDExpression(this.portaDelegataDAO, this.search, false);
				IPaginatedExpression pagExpr = this.portaDelegataDAO.toPaginatedExpression(expr);
				pagExpr.offset(start).limit(limit);


				List<PortaDelegata> findAll = this.portaDelegataDAO.findAll(pagExpr);
				if(findAll != null && findAll.size() > 0){
					List<ConfigurazioneGenerale> lst = new ArrayList<ConfigurazioneGenerale>();

					for (PortaDelegata portaDelegata : findAll) {
						lst.add(this.fillDettaglioPD(portaDelegata));
					}
					return lst;
				}
			} else {
				IExpression expr = this.createPAExpression(this.portaApplicativaDAO, this.search, false);
				IPaginatedExpression pagExpr = this.portaApplicativaDAO.toPaginatedExpression(expr);
				pagExpr.offset(start).limit(limit);

				List<PortaApplicativa> findAll = this.portaApplicativaDAO.findAll(pagExpr);
				if(findAll != null && findAll.size() > 0){
					List<ConfigurazioneGenerale> lst = new ArrayList<ConfigurazioneGenerale>();

					for (PortaApplicativa portaApplicativa : findAll) {
						lst.add(this.fillDettaglioPA(portaApplicativa));
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
		}

		return new ArrayList<ConfigurazioneGenerale>();
	}

	@Override
	public int totalCount() {
		ConfigurazioniGeneraliService.log.debug("Metodo TotalCount");

		NonNegativeNumber nnn = null;
		try {
			if(PddRuolo.DELEGATA.equals(this.search.getTipologiaTransazioni())) {
				IExpression expr = this.createPDExpression(this.portaDelegataDAO, this.search, true);
				nnn = this.portaDelegataDAO.count(expr);
			} else {
				IExpression expr = this.createPAExpression(this.portaApplicativaDAO, this.search, true);
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
		idPD.setIdentificativiFruizione(identificativiFruizione );
		dettaglioPD.setPortaDelegataOp2(this.driverConfigDB.getPortaDelegata(idPD));
		dettaglioPD.setConnettore(ConfigurazioniUtils.getConnettore(dettaglioPD.getPortaDelegata().getIdSoggetto().getTipo(), dettaglioPD.getPortaDelegata().getIdSoggetto().getNome(), 
				dettaglioPD.getPortaDelegata().getTipoSoggettoErogatore(), dettaglioPD.getPortaDelegata().getNomeSoggettoErogatore(), 
				dettaglioPD.getPortaDelegata().getTipoServizio(), dettaglioPD.getPortaDelegata().getNomeServizio(),dettaglioPD.getPortaDelegata().getVersioneServizio(), 
				this.driverRegistroDB));
		dettaglioPD.setPropertyConnettore(ConfigurazioniUtils.printConnettore(dettaglioPD.getConnettore(), CostantiConfigurazioni.LABEL_MODALITA_INOLTRO, null));

		//		if(dettaglioPD.getPortaDelegata().getNomeAzione()==null){
		ConfigurazioniUtils.fillAzioniPD(dettaglioPD, this.utilsServiceManager);
		//		}

		dettaglioPD.setPropertyGenerali(ConfigurazioniUtils.getPropertiesGeneraliPD(dettaglioPD));
		dettaglioPD.setPropertyAutenticazione(ConfigurazioniUtils.getPropertiesAutenticazionePD(dettaglioPD));
		dettaglioPD.setPropertyAutorizzazione(ConfigurazioniUtils.getPropertiesAutorizzazionePD(dettaglioPD, idPD, this.driverConfigDB, this.driverRegistroDB));  

		IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByOrganizationType(idPD.getIdentificativiFruizione().getSoggettoFruitore().getTipo());
		String contesto = (protocolFactory.getManifest().getWeb().getEmptyContext()!=null && protocolFactory.getManifest().getWeb().getEmptyContext().getEnabled()) ?
				"" : protocolFactory.getManifest().getWeb().getContext(0).getName();

		dettaglioPD.setContesto(contesto);
		dettaglioPD.setEndpointApplicativoPD(this.endpointApplicativoPD.get(protocolFactory.getProtocol()));

		dettaglioPD.setPropertyIntegrazione(ConfigurazioniUtils.getPropertiesIntegrazionePD(dettaglioPD));

		configurazione.setPd(dettaglioPD);
		return configurazione;
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
		identificativiErogazione.setIdServizio(idServizio);
		idPA.setIdentificativiErogazione(identificativiErogazione );
		dettaglioPA.setPortaApplicativaOp2(this.driverConfigDB.getPortaApplicativa(idPA));


		IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByOrganizationType(idPA.getIdentificativiErogazione().getIdServizio().getSoggettoErogatore().getTipo());
		String contesto = (protocolFactory.getManifest().getWeb().getEmptyContext()!=null && protocolFactory.getManifest().getWeb().getEmptyContext().getEnabled()) ?
				"" : protocolFactory.getManifest().getWeb().getContext(0).getName();

		dettaglioPA.setContesto(contesto);
		dettaglioPA.setEndpointApplicativoPA(this.endpointApplicativoPA.get(protocolFactory.getProtocol()));

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

		configurazione.setPa(dettaglioPA); 
		return configurazione;
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

			lista.add(getPdd());
			lista.add(getSoggettiOperativi()); 
			lista.add(getSoggettiEsterni());
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

		if(searchForm.getPermessiUtenteOperatore()!=null){
			IExpression permessi = searchForm.getPermessiUtenteOperatore().toExpressionConfigurazioneServizi(dao, 
					PortaDelegata.model().ID_SOGGETTO.TIPO, PortaDelegata.model().ID_SOGGETTO.NOME, 
					PortaDelegata.model().TIPO_SOGGETTO_EROGATORE, PortaDelegata.model().NOME_SOGGETTO_EROGATORE, 
					PortaDelegata.model().TIPO_SERVIZIO, PortaDelegata.model().NOME_SERVIZIO,
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

			expr.equals(PortaDelegata.model().ID_SOGGETTO.TIPO,tipoSoggettoDestinatario);
			expr.equals(PortaDelegata.model().ID_SOGGETTO.NOME,nomeSoggettoDestinatario);
		}
		if (StringUtils.isNotBlank(searchForm.getNomeServizio())) {

			String servizioString = searchForm.getNomeServizio();
			IDServizio idServizio = ParseUtility.parseServizioSoggetto(servizioString);

			expr.equals(PortaDelegata.model().TIPO_SOGGETTO_EROGATORE,idServizio.getSoggettoErogatore().getTipo());
			expr.equals(PortaDelegata.model().NOME_SOGGETTO_EROGATORE,idServizio.getSoggettoErogatore().getNome());
			expr.equals(PortaDelegata.model().TIPO_SERVIZIO,idServizio.getTipo());
			expr.equals(PortaDelegata.model().NOME_SERVIZIO,idServizio.getNome());
		}

		if(!count) {
			expr.sortOrder(SortOrder.ASC);
			expr.addOrder(PortaDelegata.model().ID_SOGGETTO.TIPO);
			expr.addOrder(PortaDelegata.model().ID_SOGGETTO.NOME);
			expr.addOrder(PortaDelegata.model().TIPO_SOGGETTO_EROGATORE);
			expr.addOrder(PortaDelegata.model().NOME_SOGGETTO_EROGATORE);
			expr.addOrder(PortaDelegata.model().TIPO_SERVIZIO);
			expr.addOrder(PortaDelegata.model().NOME_SERVIZIO);
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

		if(searchForm.getPermessiUtenteOperatore()!=null){
			IExpression permessi = searchForm.getPermessiUtenteOperatore().toExpressionConfigurazioneServizi(dao, 
					PortaApplicativa.model().ID_SOGGETTO.TIPO, PortaApplicativa.model().ID_SOGGETTO.NOME, 
					PortaApplicativa.model().ID_SOGGETTO.TIPO, PortaApplicativa.model().ID_SOGGETTO.NOME, 
					PortaApplicativa.model().TIPO_SERVIZIO, PortaApplicativa.model().NOME_SERVIZIO,
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

			expr.equals(PortaApplicativa.model().ID_SOGGETTO.TIPO,tipoSoggettoDestinatario);
			expr.equals(PortaApplicativa.model().ID_SOGGETTO.NOME,nomeSoggettoDestinatario);
			setSoggettoProprietario = true;
		}

		if (StringUtils.isNotBlank(searchForm.getNomeServizio())) {

			String servizioString = searchForm.getNomeServizio();
			IDServizio idServizio = ParseUtility.parseServizioSoggetto(servizioString);

			expr.equals(PortaApplicativa.model().TIPO_SERVIZIO,idServizio.getTipo());
			expr.equals(PortaApplicativa.model().NOME_SERVIZIO,idServizio.getNome());
			if(setSoggettoProprietario==false){
				expr.equals(PortaApplicativa.model().ID_SOGGETTO.TIPO,idServizio.getSoggettoErogatore().getTipo());
				expr.equals(PortaApplicativa.model().ID_SOGGETTO.NOME,idServizio.getSoggettoErogatore().getNome());
			}
		}

		if(!count){
			expr.sortOrder(SortOrder.ASC);
			expr.addOrder(PortaApplicativa.model().ID_SOGGETTO.TIPO);
			expr.addOrder(PortaApplicativa.model().ID_SOGGETTO.NOME);
			expr.addOrder(PortaApplicativa.model().TIPO_SERVIZIO);
			expr.addOrder(PortaApplicativa.model().NOME_SERVIZIO);
			expr.addOrder(PortaApplicativa.model().NOME_AZIONE);
		}

		return expr;		

	}

	private ConfigurazioneGenerale fromPD(PortaDelegata portaDelegata) {
		ConfigurazioneGenerale configurazioneGenerale = new ConfigurazioneGenerale(portaDelegata.getId(),PddRuolo.DELEGATA);

		configurazioneGenerale.setStato(portaDelegata.getStato());
		configurazioneGenerale.setLabel(portaDelegata.getNome());
		if(portaDelegata.getNomeAzione()==null){
			configurazioneGenerale.setAzione(CostantiConfigurazioni.LABEL_AZIONE_STAR);
		}
		else{
			configurazioneGenerale.setAzione(portaDelegata.getNomeAzione());
		}
		configurazioneGenerale.setErogatore(portaDelegata.getTipoSoggettoErogatore()+CostantiConfigurazioni.SEPARATORE_TIPONOME+portaDelegata.getNomeSoggettoErogatore());
		configurazioneGenerale.setFruitore(portaDelegata.getIdSoggetto().getTipo()+CostantiConfigurazioni.SEPARATORE_TIPONOME+portaDelegata.getIdSoggetto().getNome());
		configurazioneGenerale.setServizio(portaDelegata.getTipoServizio()+CostantiConfigurazioni.SEPARATORE_TIPONOME+portaDelegata.getNomeServizio());

		return configurazioneGenerale;
	}

	private ConfigurazioneGenerale fromPA(PortaApplicativa portaApplicativa) {
		ConfigurazioneGenerale configurazioneGenerale = new ConfigurazioneGenerale(portaApplicativa.getId(),PddRuolo.APPLICATIVA);

		configurazioneGenerale.setStato(portaApplicativa.getStato());
		configurazioneGenerale.setLabel(portaApplicativa.getNome());
		configurazioneGenerale.setErogatore(portaApplicativa.getIdSoggetto().getTipo()+CostantiConfigurazioni.SEPARATORE_TIPONOME+portaApplicativa.getIdSoggetto().getNome());
		configurazioneGenerale.setServizio(portaApplicativa.getTipoServizio()+CostantiConfigurazioni.SEPARATORE_TIPONOME+portaApplicativa.getNomeServizio());
		if(portaApplicativa.getNomeAzione()==null){
			configurazioneGenerale.setAzione(CostantiConfigurazioni.LABEL_AZIONE_STAR);
		}
		else{
			configurazioneGenerale.setAzione(portaApplicativa.getNomeAzione()); 
		}

		return configurazioneGenerale;
	}
	
	private void impostaTipiCompatibiliConProtocollo(IServiceSearchWithId<?, ?> dao, PortaDelegataModel model,	IExpression expr, String protocollo) throws ExpressionNotImplementedException, ExpressionException {
		// Se ho selezionato il protocollo il tipo dei servizi da includere nei risultati deve essere compatibile col protocollo scelto.
		IExpression expressionTipoServiziCompatibili = null;
		try {
			if(protocollo != null) {
				expressionTipoServiziCompatibili = DynamicUtilsService.getExpressionTipiServiziCompatibiliConProtocollo(dao, model.TIPO_SERVIZIO, protocollo);
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
				expressionTipoSoggettiMittenteCompatibili = DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(dao, model.ID_SOGGETTO.TIPO, protocollo);
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
				expressionTipoSoggettiDestinatarioCompatibili = DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(dao, model.TIPO_SOGGETTO_EROGATORE, protocollo);
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
				expressionTipoServiziCompatibili = DynamicUtilsService.getExpressionTipiServiziCompatibiliConProtocollo(dao, model.TIPO_SERVIZIO, protocollo);
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
				expressionTipoSoggettiDestinatarioCompatibili = DynamicUtilsService.getExpressionTipiSoggettiCompatibiliConProtocollo(dao, model.ID_SOGGETTO.TIPO, protocollo);
			}
		} catch (Exception e) {
			log.error("Si e' verificato un errore durante il calcolo dei tipi soggetto destinatario compatibili con il protocollo scelto: "+ e.getMessage(), e);
		}

		if(expressionTipoSoggettiDestinatarioCompatibili != null)
			expr.and(expressionTipoSoggettiDestinatarioCompatibili);
	}
}
