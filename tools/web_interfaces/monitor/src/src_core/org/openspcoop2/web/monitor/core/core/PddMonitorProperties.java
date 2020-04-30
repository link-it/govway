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
package org.openspcoop2.web.monitor.core.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticDriver;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaDriver;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.generic_project.beans.IProjectInfo;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.web.monitor.core.config.ApplicationProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;

/**
 * PddMonitorProperties
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class PddMonitorProperties {

	/** Copia Statica */
	private static PddMonitorProperties govwayMonitorProperties = null;

	private static synchronized void initialize(org.slf4j.Logger log) throws Exception{

		if(PddMonitorProperties.govwayMonitorProperties==null)
			PddMonitorProperties.govwayMonitorProperties = new PddMonitorProperties(log);	

	}

	public static PddMonitorProperties getInstance(org.slf4j.Logger log) throws Exception{

		if(PddMonitorProperties.govwayMonitorProperties==null)
			PddMonitorProperties.initialize(log);

		return PddMonitorProperties.govwayMonitorProperties;
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
		String tmp = this.appProperties.getProperty("modules.transazioni_base", false, true);
		if(tmp!=null && !"".equals(tmp)) {
			return "true".equalsIgnoreCase(tmp);
		}
		else {
			return true; // default
		}
	}
	public boolean isAttivoModuloTransazioniPersonalizzate() throws Exception{
		String tmp = this.appProperties.getProperty("modules.transazioni_personalizzate", false, true);
		if(tmp!=null && !"".equals(tmp)) {
			return "true".equalsIgnoreCase(tmp);
		}
		else {
			return false; // default
		}
	}
	public boolean isAttivoModuloRicerchePersonalizzate() throws Exception{
		String tmp = this.appProperties.getProperty("modules.ricerche_personalizzate", false, true);
		if(tmp!=null && !"".equals(tmp)) {
			return "true".equalsIgnoreCase(tmp);
		}
		else {
			return false; // default
		}
	}
	public boolean isAttivoModuloTransazioniStatisticheBase() throws Exception{
		String tmp = this.appProperties.getProperty("modules.statistiche_base", false, true);
		if(tmp!=null && !"".equals(tmp)) {
			return "true".equalsIgnoreCase(tmp);
		}
		else {
			return true; // default
		}
	}
	public boolean isAttivoModuloTransazioniStatistichePersonalizzate() throws Exception{
		String tmp = this.appProperties.getProperty("modules.statistiche_personalizzate", false, true);
		if(tmp!=null && !"".equals(tmp)) {
			return "true".equalsIgnoreCase(tmp);
		}
		else {
			return false; // default
		}
	}
	public boolean isAttivoModuloAllarmi() throws Exception{
		String tmp = this.appProperties.getProperty("modules.allarmi", false, true);
		if(tmp!=null && !"".equals(tmp)) {
			return "true".equalsIgnoreCase(tmp);
		}
		else {
			return false; // default
		}
	}
	public boolean isAttivoModuloProcessi() throws Exception{
		String tmp = this.appProperties.getProperty("modules.processi", false, true);
		if(tmp!=null && !"".equals(tmp)) {
			return "true".equalsIgnoreCase(tmp);
		}
		else {
			return false; // default
		}
	}
	public boolean isAttivoModuloSonde() throws Exception{
		String tmp = this.appProperties.getProperty("modules.sonde", false, true);
		if(tmp!=null && !"".equals(tmp)) {
			return "true".equalsIgnoreCase(tmp);
		}
		else {
			return false; // default
		}
	}
	public boolean isAttivoModuloEventi() throws Exception{
		String tmp = this.appProperties.getProperty("modules.eventi", false, true);
		if(tmp!=null && !"".equals(tmp)) {
			return "true".equalsIgnoreCase(tmp);
		}
		else {
			return true; // default
		}
	}
	public boolean isAttivoModuloReports() throws Exception{
		String tmp = this.appProperties.getProperty("modules.reports", false, true);
		if(tmp!=null && !"".equals(tmp)) {
			return "true".equalsIgnoreCase(tmp);
		}
		else {
			return false; // default
		}
	}
	public boolean isAttivoLiveRuoloOperatore() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.live.ruoloOperatore.enabled", true, true));
	}
	public boolean isAttivoTransazioniIntegrationManager() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni_im_enabled", true, true));
	}
	public boolean isAttivoTransazioniEsitiLive() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.esitiLiveEnabled", true, true));
	}
	public boolean isAttivoTransazioniExportHeader() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.export.enableHeaderInfo", true, true));
	}
	public boolean isAttivoTransazioniExportHeaderAsProperties() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.export.headers.asProperties", true, true));
	}
	public boolean isAttivoTransazioniExportContenutiAsProperties() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.export.contenuti.asProperties", true, true));
	}
	public boolean isTransazioniDownloadThrowExceptionMimeTypeNotFound() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.download.mime.throwExceptionIfMappingNotFound", true, true));
	}
	public boolean isTransazioniAllegatiDecodeBase64() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.allegati.decodeBase64", true, true));
	}
	public List<String> getTransazioniAllegatiDecodeBase64_noDecodeList() throws Exception{
		String tmp = this.appProperties.getProperty("transazioni.allegati.decodeBase64.noDecode", true, true);
		String [] tmpList = tmp.split(",");
		List<String> l = new ArrayList<String>();
		for (int i = 0; i < tmpList.length; i++) {
			l.add(tmpList[i].trim());
		}
		return l;
	}
	
	public Integer getTransazioniLiveUltimiGiorni() throws Exception{
		String tmp = this.appProperties.getProperty("transazioni.live.ultimiGiorni", false, true);
		if(tmp!=null){
			return Integer.parseInt(tmp.trim());
		}
		return null;
	}
	
	public boolean isAttivoTransazioniDataAccettazione() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.dataAccettazione.enabled", true, true));
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
	
	public List<String> getTransazioniForceIndexIdCollaborazioneFindAll(Properties externalRepository) throws Exception{
		return this.getIndexList("transazioni.forceIndex.idCollaborazione.findAll", externalRepository);
	}
	public List<String> getTransazioniForceIndexIdCollaborazioneCount(Properties externalRepository) throws Exception{
		return this.getIndexList("transazioni.forceIndex.idCollaborazione.count", externalRepository);
	}
	
	public List<String> getTransazioniForceIndexRiferimentoIdRichiestaFindAll(Properties externalRepository) throws Exception{
		return this.getIndexList("transazioni.forceIndex.riferimentoIdRichiesta.findAll", externalRepository);
	}
	public List<String> getTransazioniForceIndexRiferimentoIdRichiestaCount(Properties externalRepository) throws Exception{
		return this.getIndexList("transazioni.forceIndex.riferimentoIdRichiesta.count", externalRepository);
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
	
	public List<String> getIndexList(String propertyName,Properties externalRepository) throws Exception{
		
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
		return (ITracciaDriver) DAOFactory.getInstance(log).getServiceManager(org.openspcoop2.core.tracciamento.utils.ProjectInfo.getInstance(), log);	
	}
	public ITracciaDriver getDriverTracciamento(Connection con) throws Exception{
		Logger log =  LoggerManager.getPddMonitorSqlLogger();
		return (ITracciaDriver) DAOFactory.getInstance(log).getServiceManager(org.openspcoop2.core.tracciamento.utils.ProjectInfo.getInstance(), con, log);
	}
	public ITracciaDriver getDriverTracciamento(Connection con, ServiceManagerProperties serviceManagerProperties) throws Exception{
		Logger log =  LoggerManager.getPddMonitorSqlLogger();
		return (ITracciaDriver) DAOFactory.getInstance(log).getServiceManager(org.openspcoop2.core.tracciamento.utils.ProjectInfo.getInstance(), con, serviceManagerProperties, log);
	}
	
	public IDiagnosticDriver getDriverMsgDiagnostici() throws Exception{
		Logger log =  LoggerManager.getPddMonitorSqlLogger();
		return (IDiagnosticDriver) DAOFactory.getInstance(log).getServiceManager(org.openspcoop2.core.diagnostica.utils.ProjectInfo.getInstance(), log);
	}
	public IDiagnosticDriver getDriverMsgDiagnostici(Connection con) throws Exception{
		Logger log =  LoggerManager.getPddMonitorSqlLogger();
		return (IDiagnosticDriver) DAOFactory.getInstance(log).getServiceManager(org.openspcoop2.core.diagnostica.utils.ProjectInfo.getInstance(), con, log);
	}
	public IDiagnosticDriver getDriverMsgDiagnostici(Connection con, ServiceManagerProperties serviceManagerProperties) throws Exception{
		Logger log =  LoggerManager.getPddMonitorSqlLogger();
		return (IDiagnosticDriver) DAOFactory.getInstance(log).getServiceManager(org.openspcoop2.core.diagnostica.utils.ProjectInfo.getInstance(), con, serviceManagerProperties, log);
	}

	public TipiDatabase  tipoDatabase() throws Exception{
		DAOFactoryProperties daoFactoryProperties = DAOFactoryProperties.getInstance(this.log);
		return daoFactoryProperties.getTipoDatabaseEnum(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
	}
	
	public TipiDatabase  tipoDatabase(IProjectInfo projectInfo) throws Exception{
		DAOFactoryProperties daoFactoryProperties = DAOFactoryProperties.getInstance(this.log);
		return daoFactoryProperties.getTipoDatabaseEnum(projectInfo);
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


	

	// statistiche

	public boolean isAttivoStatisticheVisualizzazioneDimensione() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("statistiche.abilitaVisualizzaPerDimensione", true, true));
	}

	public String getOrientamentoDefaultLabelGrafici() throws Exception{
		return this.appProperties.getProperty("statistiche.orientamentoLabelGraficiDefault", true, true);
	}
	
	public boolean isStatisticheAttivoServizioEsportazioneReport() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("statistiche.service.reportExporter", true, true));
	}
	
	
	
	
	



	
	// periodo refresh live
	
	public String getIntervalloRefreshTransazioniLive() throws Exception{
		return this.appProperties.getProperty("transazioni.intervalloRefreshTransazioniLive", true, true);
	}
	
	public String getIntervalloRefreshEsitiLive() throws Exception{
		return this.appProperties.getProperty("transazioni.intervalloRefreshEsitiLive", true, true);
	}
	
	public Integer getTempoMassimoRefreshLive() throws Exception{
		return Integer.parseInt(this.appProperties.getProperty("transazioni.tempoMassimoRefreshLive", true, true));
	}
	
	// status pdd
	
	public boolean isStatusPdDEnabled() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("statoPdD.enabled", true, true));
	}
	
	public String getIntervalloRefreshStatusPdD() throws Exception{
		return this.appProperties.getProperty("statoPdD.refresh_interval", this.isStatusPdDEnabled(), true);
	}
	
	public List<String> getListaPdDMonitorate_StatusPdD() throws Exception{
		List<String> lista = new ArrayList<String>();
		String tmp = this.appProperties.getProperty("statoPdD.sonde.standard.nodi", this.isStatusPdDEnabled(), true);
		if(tmp!=null && !"".equals(tmp)){
			String[]split = tmp.split(",");
			for (int i = 0; i < split.length; i++) {
				lista.add(split[i].trim());
			}
		}
		return lista;
	}

	public String getUrlCheckStatusPdD(String namePdD) throws Exception{
		return this.appProperties.getProperty("statoPdD.sonde.standard."+namePdD+".url", this.isStatusPdDEnabled(), true);
	}
	
	public List<String> getListaSondePdd() throws Exception{
		List<String> lista = new ArrayList<String>();
		String tmp = this.appProperties.getProperty("statoPdD.sonde", this.isStatusPdDEnabled(), true);
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
		return "true".equalsIgnoreCase(this.appProperties.getProperty("visualizzaIdPdD.enabled", true, true));
	}
	
	

	
	// controllo abilitazione dei grafici svg
	
	public boolean isGraficiSvgEnabled() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("grafici.visualizzazioneSvg.enabled", true, true));
	}
	
	// Abilita il caching delle richieste multipart
	public boolean isMultipartRequestCache() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("abilitaMultipartRequestCache.enabled", true, true));
	}
	
	// properties per la gestione del login
	public String getLoginTipo() throws Exception{
		return this.appProperties.getProperty("login.tipo", true, true);
	}

	public boolean isLoginApplication() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("login.application", true, true));
	}

	public Properties getLoginProperties() throws Exception{
		return this.appProperties.readProperties("login.props.");
	}

	// propertiy per la gestione del console.font
	private String consoleFontName = null;
	private String consoleFontFamilyName = null;
	private int consoleFontStyle = -1;
	
	public String getConsoleFont() throws Exception{
		return this.appProperties.getProperty("console.font", true, true);
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
		return this.appProperties.getProperty("login.utenteNonAutorizzato.redirectUrl", true, true);
	}

	public String getLoginUtenteNonValidoRedirectUrl() throws Exception{
		return this.appProperties.getProperty("login.utenteNonValido.redirectUrl", true, true);
	}
	
	public String getLoginErroreInternoRedirectUrl() throws Exception{
		return this.appProperties.getProperty("login.erroreInterno.redirectUrl", true, true);
	}

	public String getLoginSessioneScadutaRedirectUrl() throws Exception{
		return this.appProperties.getProperty("login.sessioneScaduta.redirectUrl", true, true);
	}

	public boolean isMostraButtonLogout() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("logout.mostraButton.enabled", true, true));
	}

	public String getLogoutUrlDestinazione() throws Exception{
		return this.appProperties.getProperty("logout.urlDestinazione", true, true);
	}
	
	public boolean isGestionePasswordUtentiAttiva() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("utenti.gestionePassword.enabled", true, true));
	}
	
	public String getUtentiPasswordVerifier() throws Exception{
		return this.appProperties.getProperty("utenti.passwordVerifier", false, true);
	}
	
	public boolean isRuoloConfiguratoreAttivo() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("utenti.ruoloConfiguratore.enabled", true, true));
	}
	
	// Gestore Filtri
	
	public boolean isGestoreFiltriEnabled() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("gestoreFiltri.enabled", true, true));
	}
	
	public List<String> getListaFiltri(String tipo) throws Exception{
		List<String> lista = new ArrayList<String>();
		String tmp = this.appProperties.getProperty(("gestoreFiltri.filters."+tipo), this.isGestoreFiltriEnabled(), true);
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
		
		return this.appProperties.getProperty(className, this.isGestoreFiltriEnabled(), true);
	}
	
	public Properties getPropertiesFiltro(String tipo,String nomeFiltro) throws Exception{
		String prefix = "gestoreFiltri.filters."+tipo +"." + nomeFiltro+".props.";
		
		Properties p = this.appProperties.readProperties(prefix);

		return p;
	}
	
	public String getLogoHeaderImage() throws Exception{
		return this.appProperties.getProperty("console.header.logo.image", false, true);
	}

	public String getLogoHeaderTitolo() throws Exception{
		return this.appProperties.getProperty("console.header.logo.titolo", false, true);
	}

	public String getLogoHeaderLink() throws Exception{
		return this.appProperties.getProperty("console.header.logo.link", false, true);
	}
	

	public boolean isAttivoUtilizzaVisualizzazioneCustomTransazioni() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.storico.utilizzaVisualizzazioneCustom.enabled", true, true));
	}
	
	public boolean isAttivoVisualizzaColonnaRuoloTransazioneVisualizzazioneCustomTransazioni() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.storico.utilizzaVisualizzazioneCustom.visualizzaColonnaRuoloTransazione.enabled", true, true));
	}
	
	public boolean isAttivoUtilizzaVisualizzazioneCustomLive() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.live.utilizzaVisualizzazioneCustom.enabled", true, true));
	}
	
	public boolean isAttivoVisualizzaColonnaRuoloTransazioneVisualizzazioneCustomLive() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.live.utilizzaVisualizzazioneCustom.visualizzaColonnaRuoloTransazione.enabled", true, true));
	}
	
	public boolean isAttivoUtilizzaVisualizzazioneCustomConsegneMultiple() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.dettaglio.utilizzaVisualizzazioneCustomConsegneMultiple.enabled", true, true));
	}
	
	public boolean escludiRichiesteScartateDefaultValue() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.escludiRichiesteScartate.defaultValue", true, true));
	}
	
	/* Properties gestione finestra di export transazioni */
	
	public boolean isExportTransazioniZipTracceDefaultValue() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.storico.export.zip.tracce.defaultValue", true, true));
	}
	
	public boolean isExportTransazioniZipDiagnosticiDefaultValue() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.storico.export.zip.diagnostici.defaultValue", true, true));
	}
	
	public boolean isExportTransazioniZipContenutiDefaultValue() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.storico.export.zip.contenuti.defaultValue", true, true));
	}
	
	public boolean isExportTransazioniCsvTracceDefaultValue() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.storico.export.csv.tracce.defaultValue", true, true));
	}
	
	public boolean isExportTransazioniCsvDiagnosticiDefaultValue() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.storico.export.csv.diagnostici.defaultValue", true, true));
	}
	
	public boolean isExportTransazioniCsvVisualizzaCheckBoxSelezioneContenuti() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.storico.export.csv.visualizzaCheckBoxSelezioneContenuti.enabled", true, true));
	}
	
	/* Properties gestione della paginazione delle liste con le count */
	
	public boolean isAttivoUtilizzaCountStoricoTransazioni() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.storico.utilizzaCount.enabled", true, true));
	}
			
	public boolean isAttivoUtilizzaCountListaEventi() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("eventi.lista.utilizzaCount.enabled", true, true));
	}

	public boolean isAttivoUtilizzaCountStatisticheListaConfigurazioni() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("statistiche.configurazioni.lista.utilizzaCount.enabled", true, true));
	}
	
	public boolean visualizzaPaginaAboutExtendedInfo() throws Exception{
		String tmp = this.appProperties.getProperty("console.extendedInfo.enabled", false, true);
		if(tmp!=null && !"".equals(tmp)) {
			return "true".equalsIgnoreCase(tmp);
		}
		else {
			return false; // default
		}
	}
	
	public String getExtendedInfoAuthorizationClass() throws Exception{
		return this.appProperties.getProperty("console.extendedInfoAuthorizationClass", false, true);
	}
	
	/* Properties che pilotano la visualizzazione dei filtri di ricerca per soggetti, servizi e azioni */
	
	public boolean isVisualizzaFiltroSoggettiSelectList() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("console.filtri.soggetti.selectList.enabled", true, true));
	}
	
	public boolean isVisualizzaFiltroGruppiSelectList() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("console.filtri.gruppi.selectList.enabled", true, true));
	}
	
	public boolean isVisualizzaFiltroServiziSelectList() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("console.filtri.servizi.selectList.enabled", true, true));
	}
	
	public boolean isVisualizzaFiltroAzioniSelectList() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("console.filtri.azioni.selectList.enabled", true, true));
	}
	
	public boolean isVisualizzaVoceEntrambiFiltroRuolo() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("console.filtri.ruolo.selectList.visualizzaVoceEntrambi", true, true));
	}
	
	public boolean isVisualizzaFiltroRuoloSummary() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("console.filtri.summary.ruolo.enabled", true, true));
	}
	
	public boolean isVisualizzaVoceEntrambiFiltroRuoloSummary() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("console.filtri.summary.ruolo.selectList.visualizzaVoceEntrambi", true, true));
	}
	
	public Integer getNumeroMassimoSoggettiOperativiMenuUtente() throws Exception{
		return Integer.valueOf(this.appProperties.getProperty("console.selectListSoggettiOperativi.numeroMassimoSoggettiVisualizzati",true,true));
	}
	
	public Integer getLunghezzaMassimaLabelSelectListSoggettiOperativiMenuUtente() throws Exception{
		return Integer.valueOf(this.appProperties.getProperty("console.selectListSoggettiOperativi.lunghezzaMassimaLabel",true,true));
	}
	
//	public Integer getLunghezzaMassimaLabelButtonSoggettiOperativiMenuUtente() throws Exception{
//		return Integer.valueOf(this.appProperties.getProperty("console.buttonMenuSoggettiOperativi.lunghezzaMassimaLabel",true,true));
//	}

	public boolean isSearchFormEsitoConsegnaMultiplaEnabled() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("console.searchForm.esiti.consegnaMultiplaEnabled", true, true));
	}
}
