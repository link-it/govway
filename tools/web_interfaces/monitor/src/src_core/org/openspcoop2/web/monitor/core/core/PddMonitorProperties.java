package org.openspcoop2.web.monitor.core.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticDriver;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaDriver;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

import it.link.pdd.core.DAO;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.web.monitor.core.config.ApplicationProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;

public class PddMonitorProperties {

	/** Copia Statica */
	private static PddMonitorProperties pddMonitorProperties = null;

	private static synchronized void initialize(org.slf4j.Logger log) throws Exception{

		if(PddMonitorProperties.pddMonitorProperties==null)
			PddMonitorProperties.pddMonitorProperties = new PddMonitorProperties(log);	

	}

	public static PddMonitorProperties getInstance(org.slf4j.Logger log) throws Exception{

		if(PddMonitorProperties.pddMonitorProperties==null)
			PddMonitorProperties.initialize(log);

		return PddMonitorProperties.pddMonitorProperties;
	}



	private ApplicationProperties appProperties = null;
	private transient Logger log = null;
	public PddMonitorProperties(Logger log) throws Exception{
		this.appProperties = ApplicationProperties.getInstance(log);
		this.log = log;
	}


	public Enumeration<?> keys(){
		return this.appProperties.keys();
	}
	public String getProperty(String name,boolean required,boolean convertEnvProperty) throws Exception{
		return this.appProperties.getProperty(name, required, convertEnvProperty);
	}

	
	public String getConfDirectory() throws Exception{
		return this.appProperties.getProperty("confDirectory", true, true);
	}
	
	public String getPddMonitorTitle() throws Exception{
		return this.appProperties.getProperty("appTitle", true, true);
	}

	public boolean isAttivoModuloTransazioniBase() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("modules.transazioni_base", true, false));
	}
	public boolean isAttivoModuloTransazioniPersonalizzate() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("modules.transazioni_personalizzate", true, false));
	}
	public boolean isAttivoModuloRicerchePersonalizzate() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("modules.ricerche_personalizzate", true, false));
	}
	public boolean isAttivoModuloTransazioniStatisticheBase() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("modules.statistiche_base", true, false));
	}
	public boolean isAttivoModuloTransazioniStatistichePersonalizzate() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("modules.statistiche_personalizzate", true, false));
	}
	public boolean isAttivoModuloAllarmi() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("modules.allarmi", true, false));
	}
	public boolean isAttivoModuloProcessi() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("modules.processi", true, false));
	}
	public boolean isAttivoModuloSonde() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("modules.sonde", true, false));
	}
	public boolean isAttivoModuloEventi() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("modules.eventi", true, false));
	}
	public boolean isAttivoModuloReports() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("modules.reports", true, false));
	}
	public boolean isAttivoLiveRuoloOperatore() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.live.ruoloOperatore.enabled", true, false));
	}
	public boolean isAttivoTransazioniEsitiLive() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.esitiLiveEnabled", true, false));
	}
	public boolean isAttivoTransazioniExportHeader() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.export.enableHeaderInfo", true, false));
	}
	public boolean isTransazioniDownloadThrowExceptionMimeTypeNotFound() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.download.mime.throwExceptionIfMappingNotFound", true, false));
	}
	public boolean isTransazioniAllegatiDecodeBase64() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.allegati.decodeBase64", true, false));
	}
	public List<String> getTransazioniAllegatiDecodeBase64_noDecodeList() throws Exception{
		String tmp = this.appProperties.getProperty("transazioni.allegati.decodeBase64.noDecode", true, false);
		String [] tmpList = tmp.split(",");
		List<String> l = new ArrayList<String>();
		for (int i = 0; i < tmpList.length; i++) {
			l.add(tmpList[i].trim());
		}
		return l;
	}
	
	public Integer getTransazioniLiveUltimiGiorni() throws Exception{
		String tmp = this.appProperties.getProperty("transazioni.live.ultimiGiorni", false, false);
		if(tmp!=null){
			return Integer.parseInt(tmp.trim());
		}
		return null;
	}
	
	public boolean isAttivoTransazioniDataAccettazione() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.dataAccettazione.enabled", true, false));
	}
	
	public boolean isAttivoTransazioniUtilizzoSondaPdDListAsClusterId() throws Exception{
		String p = this.appProperties.getProperty("transazioni.idCluster.useSondaPdDList", false, true);
		if(p==null){
			return false; // default
		}
		return "true".equalsIgnoreCase(p);
	}
	
	public Properties getExternalForceIndexRepository() throws Exception{
		InputStream is = null;
		String s = this.appProperties.getProperty("forceIndex.repository", false, true);
		if(s!=null){
			File f = new File(s);
			if(f.exists()){
				is = new FileInputStream(f);
			}
			else{
				// provo a cercarlo nel classpath
				is = PddMonitorProperties.class.getResourceAsStream(s);
			}
		}
		if(is!=null){
			Properties p = new Properties();
			p.load(is);
			is.close();
			return p;
 		}
		return null;
	}
	
	public List<String> getTransazioniForceIndexAndamentoTemporaleFindAll(Properties externalRepository) throws Exception{
		return this.getIndexList("transazioni.forceIndex.andamentoTemporale.findAll", externalRepository);
	}
	public List<String> getTransazioniForceIndexAndamentoTemporaleCount(Properties externalRepository) throws Exception{
		return this.getIndexList("transazioni.forceIndex.andamentoTemporale.count", externalRepository);
	}
	
	public List<String> getTransazioniForceIndexIdApplicativoFindAll(Properties externalRepository) throws Exception{
		return this.getIndexList("transazioni.forceIndex.idApplicativo.findAll", externalRepository);
	}
	public List<String> getTransazioniForceIndexIdApplicativoCount(Properties externalRepository) throws Exception{
		return this.getIndexList("transazioni.forceIndex.idApplicativo.count", externalRepository);
	}
	
	public List<String> getTransazioniForceIndexIdMessaggioRichiestaFindAll(Properties externalRepository) throws Exception{
		return this.getIndexList("transazioni.forceIndex.idMessaggio.richiesta.findAll", externalRepository);
	}
	public List<String> getTransazioniForceIndexIdMessaggioRichiestaCount(Properties externalRepository) throws Exception{
		return this.getIndexList("transazioni.forceIndex.idMessaggio.richiesta.count", externalRepository);
	}
	
	public List<String> getTransazioniForceIndexIdMessaggioRispostaFindAll(Properties externalRepository) throws Exception{
		return this.getIndexList("transazioni.forceIndex.idMessaggio.risposta.findAll", externalRepository);
	}
	public List<String> getTransazioniForceIndexIdMessaggioRispostaCount(Properties externalRepository) throws Exception{
		return this.getIndexList("transazioni.forceIndex.idMessaggio.risposta.count", externalRepository);
	}
	
	public List<String> getTransazioniForceIndexIdTransazioneFindAll(Properties externalRepository) throws Exception{
		return this.getIndexList("transazioni.forceIndex.idTransazione.findAll", externalRepository);
	}
	public List<String> getTransazioniForceIndexIdTransazioneCount(Properties externalRepository) throws Exception{
		return this.getIndexList("transazioni.forceIndex.idTransazione.count", externalRepository);
	}
	
	public List<String> getTransazioniForceIndexLiveFindAll(Properties externalRepository) throws Exception{
		return this.getIndexList("transazioni.forceIndex.live.findAll", externalRepository);
	}
	
	public List<String> getTransazioniForceIndexEsitiCount(Properties externalRepository) throws Exception{
		return this.getIndexList("transazioni.forceIndex.esiti.count", externalRepository);
	}
	
	public List<String> getTransazioniForceIndexGetByIdTransazione(Properties externalRepository) throws Exception{
		return this.getIndexList("transazioni.forceIndex.getByIdTransazione", externalRepository);
	}
	
	public List<String> getEventiForceIndexFindAll(Properties externalRepository) throws Exception{
		return this.getIndexList("eventi.forceIndex.findAll", externalRepository);
	}
	public List<String> getEventiForceIndexCount(Properties externalRepository) throws Exception{
		return this.getIndexList("eventi.forceIndex.count", externalRepository);
	}
	
	public List<String> getReportForceIndexFindAll(Properties externalRepository) throws Exception{
		return this.getIndexList("reports.forceIndex.findAll", externalRepository);
	}
	public List<String> getReportForceIndexCount(Properties externalRepository) throws Exception{
		return this.getIndexList("reports.forceIndex.count", externalRepository);
	}
	
	public List<String> getStatisticheForceIndexAndamentoTemporaleGroupBy(StatisticType tipologia, Properties externalRepository) throws Exception{
		switch (tipologia) {
		case ORARIA:
			return this.getIndexList("statistiche.forceIndex.andamentoTemporale.orarie.groupBy", externalRepository);
		case GIORNALIERA:
			return this.getIndexList("statistiche.forceIndex.andamentoTemporale.giornaliere.groupBy", externalRepository);
		case SETTIMANALE:
			return this.getIndexList("statistiche.forceIndex.andamentoTemporale.settimanali.groupBy", externalRepository);
		case MENSILE:
			return this.getIndexList("statistiche.forceIndex.andamentoTemporale.mensili.groupBy", externalRepository);
		}
		return null;
	}
	public List<String> getStatisticheForceIndexAndamentoTemporaleCount(StatisticType tipologia, Properties externalRepository) throws Exception{
		switch (tipologia) {
		case ORARIA:
			return this.getIndexList("statistiche.forceIndex.andamentoTemporale.orarie.count", externalRepository);
		case GIORNALIERA:
			return this.getIndexList("statistiche.forceIndex.andamentoTemporale.giornaliere.count", externalRepository);
		case SETTIMANALE:
			return this.getIndexList("statistiche.forceIndex.andamentoTemporale.settimanali.count", externalRepository);
		case MENSILE:
			return this.getIndexList("statistiche.forceIndex.andamentoTemporale.mensili.count", externalRepository);
		}
		return null;
	}
	
	public List<String> getStatisticheForceIndexEsitiLiveGroupBy(StatisticType tipologia, Properties externalRepository) throws Exception{
		switch (tipologia) {
		case ORARIA:
			return this.getIndexList("statistiche.forceIndex.orarie.esiti.groupBy", externalRepository);
		case GIORNALIERA:
			return this.getIndexList("statistiche.forceIndex.giornaliere.esiti.groupBy", externalRepository);
		case SETTIMANALE:
			return this.getIndexList("statistiche.forceIndex.settimanali.esiti.groupBy", externalRepository);
		case MENSILE:
			return this.getIndexList("statistiche.forceIndex.mensili.esiti.groupBy", externalRepository);
		}
		return null;
	}
	
	public List<String> getStatisticheForceIndexDistribuzioneSoggettoGroupBy(Properties externalRepository) throws Exception{
		return this.getIndexList("statistiche.forceIndex.distribuzioneSoggetto.groupBy", externalRepository);
	}
	public List<String> getStatisticheForceIndexDistribuzioneSoggettoCount(Properties externalRepository) throws Exception{
		return this.getIndexList("statistiche.forceIndex.distribuzioneSoggetto.count", externalRepository);
	}
	
	public List<String> getStatisticheForceIndexDistribuzioneServizioGroupBy(Properties externalRepository) throws Exception{
		return this.getIndexList("statistiche.forceIndex.distribuzioneServizio.groupBy", externalRepository);
	}
	public List<String> getStatisticheForceIndexDistribuzioneServizioCount(Properties externalRepository) throws Exception{
		return this.getIndexList("statistiche.forceIndex.distribuzioneServizio.count", externalRepository);
	}
	
	public List<String> getStatisticheForceIndexDistribuzioneAzioneGroupBy(Properties externalRepository) throws Exception{
		return this.getIndexList("statistiche.forceIndex.distribuzioneAzione.groupBy", externalRepository);
	}
	public List<String> getStatisticheForceIndexDistribuzioneAzioneCount(Properties externalRepository) throws Exception{
		return this.getIndexList("statistiche.forceIndex.distribuzioneAzione.count", externalRepository);
	}
	
	public List<String> getStatisticheForceIndexDistribuzioneServizioApplicativoGroupBy(Properties externalRepository) throws Exception{
		return this.getIndexList("statistiche.forceIndex.distribuzioneServizioApplicativo.groupBy", externalRepository);
	}
	public List<String> getStatisticheForceIndexDistribuzioneServizioApplicativoCount(Properties externalRepository) throws Exception{
		return this.getIndexList("statistiche.forceIndex.distribuzioneServizioApplicativo.count", externalRepository);
	}
	
	public List<String> getStatisticheForceIndexPersonalizzataAndamentoTemporaleGroupBy(StatisticType tipologia, Properties externalRepository) throws Exception{
		switch (tipologia) {
		case ORARIA:
			return this.getIndexList("statistiche.forceIndex.personalizzate.andamentoTemporale.orarie.groupBy", externalRepository);
		case GIORNALIERA:
			return this.getIndexList("statistiche.forceIndex.personalizzate.andamentoTemporale.giornaliere.groupBy", externalRepository);
		case SETTIMANALE:
			return this.getIndexList("statistiche.forceIndex.personalizzate.andamentoTemporale.settimanali.groupBy", externalRepository);
		case MENSILE:
			return this.getIndexList("statistiche.forceIndex.personalizzate.andamentoTemporale.mensili.groupBy", externalRepository);
		}
		return null;
	}
	public List<String> getStatisticheForceIndexPersonalizzataAndamentoTemporaleCount(StatisticType tipologia, Properties externalRepository) throws Exception{
		switch (tipologia) {
		case ORARIA:
			return this.getIndexList("statistiche.forceIndex.personalizzate.andamentoTemporale.orarie.count", externalRepository);
		case GIORNALIERA:
			return this.getIndexList("statistiche.forceIndex.personalizzate.andamentoTemporale.giornaliere.count", externalRepository);
		case SETTIMANALE:
			return this.getIndexList("statistiche.forceIndex.personalizzate.andamentoTemporale.settimanali.count", externalRepository);
		case MENSILE:
			return this.getIndexList("statistiche.forceIndex.personalizzate.andamentoTemporale.mensili.count", externalRepository);
		}
		return null;
	}
	
	public List<String> getStatisticheForceIndexPersonalizzataDistribuzioneGroupBy(StatisticType tipologia, Properties externalRepository) throws Exception{
		switch (tipologia) {
		case ORARIA:
			return this.getIndexList("statistiche.forceIndex.personalizzate.andamentoTemporale.orarie.groupBy", externalRepository);
		case GIORNALIERA:
			return this.getIndexList("statistiche.forceIndex.personalizzate.andamentoTemporale.giornaliere.groupBy", externalRepository);
		case SETTIMANALE:
			return this.getIndexList("statistiche.forceIndex.personalizzate.andamentoTemporale.settimanali.groupBy", externalRepository);
		case MENSILE:
			return this.getIndexList("statistiche.forceIndex.personalizzate.andamentoTemporale.mensili.groupBy", externalRepository);
		}
		return null;
	}
	public List<String> getStatisticheForceIndexPersonalizzataDistribuzioneCount(StatisticType tipologia, Properties externalRepository) throws Exception{
		switch (tipologia) {
		case ORARIA:
			return this.getIndexList("statistiche.forceIndex.personalizzate.distribuzione.orarie.count", externalRepository);
		case GIORNALIERA:
			return this.getIndexList("statistiche.forceIndex.personalizzate.distribuzione.giornaliere.count", externalRepository);
		case SETTIMANALE:
			return this.getIndexList("statistiche.forceIndex.personalizzate.distribuzione.settimanali.count", externalRepository);
		case MENSILE:
			return this.getIndexList("statistiche.forceIndex.personalizzate.distribuzione.mensili.count", externalRepository);
		}
		return null;
	}
	
	private List<String> getIndexList(String propertyName,Properties externalRepository) throws Exception{
		
		String s = null;
		if(externalRepository!=null){
			String tmp = externalRepository.getProperty(propertyName);
			if(tmp!=null){
				s = tmp.trim();
			}
		}
		if(s==null){
			// provo a cercarlo nel file monitor
			s = this.appProperties.getProperty(propertyName, false, true);
		}
		
		if(s!=null){
			List<String> l = new ArrayList<String>();
			if(s.contains(",")){
				String [] split = s.split(",");
				for (int i = 0; i < split.length; i++) {
					l.add(split[i]);
				}
			}
			else{
				l.add(s);
			}
			return l;
		}
		return null;
	}
	
	public ITracciaDriver getDriverTracciamento() throws Exception{
		Logger log =  LoggerManager.getPddMonitorSqlLogger();
		return (ITracciaDriver) DAOFactory.getInstance(log).getServiceManager(DAO.TRACCE,log);
		
	}
	public IDiagnosticDriver getDriverMsgDiagnostici() throws Exception{
		Logger log =  LoggerManager.getPddMonitorSqlLogger();
		return (IDiagnosticDriver) DAOFactory.getInstance(log).getServiceManager(DAO.MESSAGGI_DIAGNOSTICI,log);
	}

	public boolean isBackwardCompatibilityOpenspcoop1() throws Exception{
		DAOFactoryProperties daoFactoryProperties = DAOFactoryProperties.getInstance(this.log);
		return daoFactoryProperties.isBackwardCompatibilityOpenspcoop1(DAO.TRANSAZIONI);
	}
	
	public boolean isBackwardCompatibilityOpenspcoop1(DAO tipoDAO) throws Exception{
		DAOFactoryProperties daoFactoryProperties = DAOFactoryProperties.getInstance(this.log);
		return daoFactoryProperties.isBackwardCompatibilityOpenspcoop1(tipoDAO);
	}

	public TipiDatabase  tipoDatabase() throws Exception{
		DAOFactoryProperties daoFactoryProperties = DAOFactoryProperties.getInstance(this.log);
		return daoFactoryProperties.getTipoDatabaseEnum(DAO.TRANSAZIONI);
	}
	
	public TipiDatabase  tipoDatabase(DAO tipoDAO) throws Exception{
		DAOFactoryProperties daoFactoryProperties = DAOFactoryProperties.getInstance(this.log);
		return daoFactoryProperties.getTipoDatabaseEnum(tipoDAO);
	}
	// configurazioni jmx
	
	private String _getJmxPdD_value(boolean required, String alias, String prop) throws Exception{
		String tmp = this.getProperty(alias+"."+prop ,false, true);
		if(tmp==null || "".equals(tmp)){
			tmp = this.getProperty(prop ,required, true);
		}
		return tmp;
	}
	
	public List<String> getJmxPdD_aliases() throws Exception {
		List<String> list = new ArrayList<String>();
		String tipo = this.getProperty("configurazioni.risorseJmxPdd.aliases",false,true);
		if(tipo!=null && !"".equals(tipo)){
			String [] tmp = tipo.split(",");
			for (int i = 0; i < tmp.length; i++) {
				list.add(tmp[i].trim());
			}
		}
		return list;
	}
	
	public final static String RESOURCE_JMX_PDD_TIPOLOGIA_ACCESSO_JMX = "jmx";
	public final static String RESOURCE_JMX_PDD_TIPOLOGIA_ACCESSO_OPENSPCOOP = "openspcoop";
	public String getJmxPdD_tipoAccesso(String alias) throws Exception {
		String tipo = _getJmxPdD_value(true, alias, "configurazioni.risorseJmxPdd.tipoAccesso");
		if(!RESOURCE_JMX_PDD_TIPOLOGIA_ACCESSO_JMX.equals(tipo) && !RESOURCE_JMX_PDD_TIPOLOGIA_ACCESSO_OPENSPCOOP.equals(tipo)){
			throw new UtilsException("Tipo ["+tipo+"] non supportato per la proprieta' 'configurazioni.risorseJmxPdd.tipoAccesso'");
		}
		return tipo;
	}
	
	public String getJmxPdD_remoteAccess_username(String alias) throws Exception {
		return _getJmxPdD_value(false, alias, "configurazioni.risorseJmxPdd.remoteAccess.username");
	}
	public String getJmxPdD_remoteAccess_password(String alias) throws Exception {
		return _getJmxPdD_value(false, alias, "configurazioni.risorseJmxPdd.remoteAccess.password");
	}
	
	public String getJmxPdD_remoteAccess_applicationServer(String alias) throws Exception {
		return _getJmxPdD_value(false, alias, "configurazioni.risorseJmxPdd.remoteAccess.as");
	}
	public String getJmxPdD_remoteAccess_factory(String alias) throws Exception {
		return _getJmxPdD_value(false, alias, "configurazioni.risorseJmxPdd.remoteAccess.factory");
	}
	public String getJmxPdD_remoteAccess_url(String alias) throws Exception {
		return _getJmxPdD_value(false, alias, "configurazioni.risorseJmxPdd.remoteAccess.url");
	}
	
	public String getJmxPdD_dominio(String alias) throws Exception {
		return _getJmxPdD_value(true, alias, "configurazioni.risorseJmxPdd.dominio");
	}
	
	public String getJmxPdD_cache_type(String alias) throws Exception {
		return _getJmxPdD_value(true, alias, "configurazioni.risorseJmxPdd.cache.tipo");
	}
	public String getJmxPdD_cache_nomeMetodo_resetCache(String alias) throws Exception {
		return _getJmxPdD_value(true, alias, "configurazioni.risorseJmxPdd.cache.nomeMetodo.resetCache");
	}
	
	public String getJmxPdD_cache_nomeRisorsa_dumpApplicativo(String alias) throws Exception {
		return _getJmxPdD_value(true, alias, "configurazioni.risorseJmxPdd.cache.dump.nomeRisorsa");
	}

	// configurazioni dump
	private static Boolean isAttivoDumpModeChoice_forceDisabled = null;
	public static void disabilitaFunzionalitaArchiviazioneMessaggi(){
		isAttivoDumpModeChoice_forceDisabled = false;
	}
	public boolean isAttivoDumpModeChoice() throws Exception{
		if(isAttivoDumpModeChoice_forceDisabled!=null){
			return isAttivoDumpModeChoice_forceDisabled;
		}
		return "true".equalsIgnoreCase(this.appProperties.getProperty("configurazioni.analisiContenuti.dumpModeChoice.enable", true, false));
	}
	
	public boolean isAttivoConfigurazioniResetCache() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("configurazioni.analisiContenuti.enableCacheReset", true, false));
	}
	
	public boolean isAttivoConfigurazioniDumpCriteriBusta() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("configurazioni.analisiContenuti.enableCriteriBusta", true, false));
	}
	

	// statistiche

	public boolean isAttivoStatisticheVisualizzazioneDimensione() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("statistiche.abilitaVisualizzaPerDimensione", true, false));
	}

	public String getOrientamentoDefaultLabelGrafici() throws Exception{
		return this.appProperties.getProperty("statistiche.orientamentoLabelGraficiDefault", true, false);
	}
	
	public boolean isStatisticheAttivoServizioEsportazioneReport() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("statistiche.service.reportExporter", true, false));
	}
	
	
	// allarmi
	
	public String getAllarmiConfigurazione() throws Exception{
		return this.appProperties.getProperty("allarmi.configurazione", true, true);
	}
	
	public String getAllarmiActiveServiceUrl() throws Exception{
		return this.appProperties.getProperty("allarmi.active.service.url", true, true);
	}
	
	public String getAllarmiActiveServiceUrl_SuffixStartAlarm() throws Exception{
		return this.appProperties.getProperty("allarmi.active.service.url.suffix.startAlarm", true, true);
	}
	
	public String getAllarmiActiveServiceUrl_SuffixStopAlarm() throws Exception{
		return this.appProperties.getProperty("allarmi.active.service.url.suffix.stopAlarm", true, true);
	}
	
	public String getAllarmiActiveServiceUrl_SuffixReStartAlarm() throws Exception{
		return this.appProperties.getProperty("allarmi.active.service.url.suffix.restartAlarm", true, true);
	}
	
	public String getAllarmiActiveServiceUrl_SuffixUpdateStateOkAlarm() throws Exception{
		return this.appProperties.getProperty("allarmi.active.service.url.suffix.updateStateAlarm.ok", true, true);
	}
	
	public String getAllarmiActiveServiceUrl_SuffixUpdateStateWarningAlarm() throws Exception{
		return this.appProperties.getProperty("allarmi.active.service.url.suffix.updateStateAlarm.warning", true, true);
	}
	
	public String getAllarmiActiveServiceUrl_SuffixUpdateStateErrorAlarm() throws Exception{
		return this.appProperties.getProperty("allarmi.active.service.url.suffix.updateStateAlarm.error", true, true);
	}
	
	public String getAllarmiActiveServiceUrl_SuffixUpdateAcknoledgementEnabledAlarm() throws Exception{
		return this.appProperties.getProperty("allarmi.active.service.url.suffix.updateAcknoledgement.enabled", true, true);
	}
	
	public String getAllarmiActiveServiceUrl_SuffixUpdateAcknoledgementDisabledAlarm() throws Exception{
		return this.appProperties.getProperty("allarmi.active.service.url.suffix.updateAcknoledgement.disabled", true, true);
	}
	
	public boolean isAllarmiConsultazioneModificaStatoAbilitata() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("allarmi.consultazione.modificaStatoAbilitata", true, false));
	}
	
	public boolean isAllarmiAssociazioneAcknowledgedStatoAllarme() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("allarmi.acknowledged.associazioneStatoAllarme", true, false));
	}
	
	public boolean isAllarmiNotificaMailVisualizzazioneCompleta() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("allarmi.notificaMail.visualizzazioneCompleta", true, false));
	}
	
	public boolean isAllarmiMonitoraggioEsternoVisualizzazioneCompleta() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("allarmi.monitoraggioEsterno.visualizzazioneCompleta", true, false));
	}

	public boolean isAllarmiConsultazioneSezioneNotificaMailReadOnly() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("allarmi.consultazione.sezioneNotificaMail.readOnly", true, false));
	}
	
	public boolean isAllarmiConsultazioneSezioneMonitoraggioEsternoReadOnly() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("allarmi.consultazione.sezioneMonitoraggioEsterno.readOnly", true, false));
	}
	
	public boolean isAllarmiConsultazioneParametriReadOnly() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("allarmi.consultazione.sezioneParametri.readOnly", true, false));
	}
	
	
	// sonde

	public boolean isTipoSondaEnabled() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("sonde.tipoSondaEnabled", true, false));
	}

	public String getTipoSondaDefault() throws Exception{
		return this.appProperties.getProperty("sonde.tipoSondaDefault", true, false);
	}

	public boolean isDisableContentTypeUser() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("sonde.disableContentTypeUser", true, false));
	}
	
	
	//auditing
	public boolean isAuditingEnabled() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("auditing.auditingEnabled", true, false));
	}
	
	// periodo refresh live
	
	public String getIntervalloRefreshTransazioniLive() throws Exception{
		return this.appProperties.getProperty("transazioni.intervalloRefreshTransazioniLive", true, false);
	}
	
	public String getIntervalloRefreshEsitiLive() throws Exception{
		return this.appProperties.getProperty("transazioni.intervalloRefreshEsitiLive", true, false);
	}
	
	public Integer getTempoMassimoRefreshLive() throws Exception{
		return Integer.parseInt(this.appProperties.getProperty("transazioni.tempoMassimoRefreshLive", true, false));
	}
	
	// status pdd
	
	public boolean isStatusPdDEnabled() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("statoPdD.enabled", true, false));
	}
	
	public String getIntervalloRefreshStatusPdD() throws Exception{
		return this.appProperties.getProperty("statoPdD.refresh_interval", this.isStatusPdDEnabled(), false);
	}
	
	public List<String> getListaPdDMonitorate_StatusPdD() throws Exception{
		List<String> lista = new ArrayList<String>();
		String tmp = this.appProperties.getProperty("statoPdD.sonde.pddOE.pdd", this.isStatusPdDEnabled(), false);
		if(tmp!=null && !"".equals(tmp)){
			String[]split = tmp.split(",");
			for (int i = 0; i < split.length; i++) {
				lista.add(split[i].trim());
			}
		}
		return lista;
	}

	public String getUrlCheckStatusPdD(String namePdD) throws Exception{
		return this.appProperties.getProperty("statoPdD.pdd."+namePdD+".url", this.isStatusPdDEnabled(), false);
	}
	
	public List<String> getListaSondePdd() throws Exception{
		List<String> lista = new ArrayList<String>();
		String tmp = this.appProperties.getProperty("statoPdD.sonde", this.isStatusPdDEnabled(), false);
		if(tmp!=null && !"".equals(tmp)){
			String[]split = tmp.split(",");
			for (int i = 0; i < split.length; i++) {
				lista.add(split[i].trim());
			}
		}
		return lista;
	}

	public Properties getPropertiesSonda(String nomeSondaPdd) throws Exception{
		String prefix = "statoPdD.sonde." + nomeSondaPdd+".";
		
		Properties p = this.appProperties.readProperties(prefix);

		return p;
	}
	
	
	// Visualizza idPdd
	
	public boolean isVisualizzaIdPdDEnabled() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("visualizzaIdPdD.enabled", true, false));
	}
	
	
	// Visualizza Plugins SDK
	private static Boolean isVisualizzaPluginsSDK_forceDisabled = null;
	public static void disabilitaFunzionalitaPluginsSDK(){
		isVisualizzaPluginsSDK_forceDisabled = false;
	}
	public boolean isVisualizzaPluginsSDK() throws Exception{
		if(isVisualizzaPluginsSDK_forceDisabled!=null){
			return isVisualizzaPluginsSDK_forceDisabled;
		}
		return "true".equalsIgnoreCase(this.appProperties.getProperty("analisiDati.visualizzaPluginsSDK", true, false));
	}
	
	// controllo abilitazione dei grafici svg
	
	public boolean isGraficiSvgEnabled() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("grafici.visualizzazioneSvg.enabled", true, false));
	}
	
	// Abilita il caching delle richieste multipart
	public boolean isMultipartRequestCache() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("abilitaMultipartRequestCache.enabled", true, false));
	}
	
	// properties per la gestione del login
	public String getLoginTipo() throws Exception{
		return this.appProperties.getProperty("login.tipo", true, false);
	}

	public boolean isLoginApplication() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("login.application", true, false));
	}

	public Properties getLoginProperties() throws Exception{
		return this.appProperties.readProperties("login.props.");
	}

	// propertiy per la gestione del console.font
	private String consoleFontName = null;
	private String consoleFontFamilyName = null;
	private int consoleFontStyle = -1;
	
	public String getConsoleFont() throws Exception{
		return this.appProperties.getProperty("console.font", true, false);
	}
	
	public String getConsoleFontName() {
		return this.consoleFontName;
	}

	public void setConsoleFontName(String consoleFontName) {
		this.consoleFontName = consoleFontName;
	}
	public String getConsoleFontFamilyName() {
		return this.consoleFontFamilyName;
	}

	public void setConsoleFontFamilyName(String consoleFontFamilyName) {
		this.consoleFontFamilyName = consoleFontFamilyName;
	}

	public int getConsoleFontStyle() {
		return this.consoleFontStyle;
	}

	public void setConsoleFontStyle(int consoleFontStyle) {
		this.consoleFontStyle = consoleFontStyle;
	}

	public String getLoginUtenteNonAutorizzatoRedirectUrl() throws Exception{
		return this.appProperties.getProperty("login.utenteNonAutorizzato.redirectUrl", true, false);
	}

	public String getLoginUtenteNonValidoRedirectUrl() throws Exception{
		return this.appProperties.getProperty("login.utenteNonValido.redirectUrl", true, false);
	}
	
	public String getLoginErroreInternoRedirectUrl() throws Exception{
		return this.appProperties.getProperty("login.erroreInterno.redirectUrl", true, false);
	}

	public String getLoginSessioneScadutaRedirectUrl() throws Exception{
		return this.appProperties.getProperty("login.sessioneScaduta.redirectUrl", true, false);
	}

	public boolean isMostraButtonLogout() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("logout.mostraButton.enabled", true, false));
	}

	public String getLogoutUrlDestinazione() throws Exception{
		return this.appProperties.getProperty("logout.urlDestinazione", true, false);
	}
	
	public boolean isGestionePasswordUtentiAttiva() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("utenti.gestionePassword.enabled", true, false));
	}
	
	public String getUtentiPasswordVerifier() throws Exception{
		return this.appProperties.getProperty("utenti.passwordVerifier", false, true);
	}
	
	// Gestore Filtri
	
	public boolean isGestoreFiltriEnabled() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("gestoreFiltri.enabled", true, false));
	}
	
	public List<String> getListaFiltri(String tipo) throws Exception{
		List<String> lista = new ArrayList<String>();
		String tmp = this.appProperties.getProperty(("gestoreFiltri.filters."+tipo), this.isGestoreFiltriEnabled(), false);
		if(tmp!=null && !"".equals(tmp)){
			String[]split = tmp.split(",");
			for (int i = 0; i < split.length; i++) {
				lista.add(split[i].trim());
			}
		}
		return lista;
	}
	
	public String getClassNameFiltro(String tipo,String nomeFiltro) throws Exception{
		String className = "gestoreFiltri.filters."+tipo +"." + nomeFiltro+".class";
		
		return this.appProperties.getProperty(className, this.isGestoreFiltriEnabled(), false);
	}
	
	public Properties getPropertiesFiltro(String tipo,String nomeFiltro) throws Exception{
		String prefix = "gestoreFiltri.filters."+tipo +"." + nomeFiltro+".props.";
		
		Properties p = this.appProperties.readProperties(prefix);

		return p;
	}
	
	public String getLogoHeaderImage() throws Exception{
		return this.appProperties.getProperty("console.header.logo.image", false, false);
	}

	public String getLogoHeaderTitolo() throws Exception{
		return this.appProperties.getProperty("console.header.logo.titolo", false, false);
	}

	public String getLogoHeaderLink() throws Exception{
		return this.appProperties.getProperty("console.header.logo.link", false, false);
	}
	

	
	public String getEndpointApplicativoPD() throws Exception{
		return this.appProperties.getProperty("endpointApplicativoPD.baseUrl", true, false);
	}
	
	public String getEndpointApplicativoPA() throws Exception{
		return this.appProperties.getProperty("endpointApplicativoPA.baseUrl", true, false);
	}
	
	
	/* Properties export report */
	
	public int getNumeroMaxRisultatiDistribuzioneStatistica() throws Exception{
		return Integer.parseInt(this.appProperties.getProperty("reports.distribuzioneStatistica.maxNumeroRisultati.default", true, false));
	}
	public int getLimiteSuperioNumeroMaxDistribuzioneServizi() throws Exception{
		return Integer.parseInt(this.appProperties.getProperty("reports.distribuzioneStatistica.maxNumeroRisultati.limiteSuperiore", true, false));
	}
	public boolean isAttivaVisualizzazioneRisultatiEsclusiComeCategoriaAltri() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("reports.distribuzioneStatistica.categoriaAltri", true, false));
	}
	public int getNumeroMaxCategorieErrore() throws Exception{
		return Integer.parseInt(this.appProperties.getProperty("reports.casisticheErrori.maxNumeroCategorie.default", true, false));
	}
	public int getLimiteSuperioNumeroMaxCategorieErrore() throws Exception{
		return Integer.parseInt(this.appProperties.getProperty("reports.casisticheErrori.maxNumeroCategorie.limiteSuperiore", true, false));
	}
	
	/* Properties gestione della paginazione delle liste con le count */
	
	public boolean isAttivoUtilizzaCountStoricoTransazioni() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.storico.utilizzaCount.enabled", true, false));
	}
	
	public boolean isAttivoUtilizzaCountListaUtenti() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("utenti.lista.utilizzaCount.enabled", true, false));
	}
	
	public boolean isAttivoUtilizzaCountListaSonde() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("sonde.lista.utilizzaCount.enabled", true, false));
	}
	
	public boolean isAttivoUtilizzaCountListaReports() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("reports.lista.utilizzaCount.enabled", true, false));
	}
	
	public boolean isAttivoUtilizzaCountListaEventi() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("eventi.lista.utilizzaCount.enabled", true, false));
	}
	
	public boolean isAttivoUtilizzaCountListaAllarmi() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("allarmi.lista.utilizzaCount.enabled", true, false));
	}

	public boolean isAttivoUtilizzaCountStatisticheListaConfigurazioni() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("statistiche.configurazioni.lista.utilizzaCount.enabled", true, false));
	}
}
