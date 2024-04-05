/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.generic_project.beans.IProjectInfo;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.pdd.config.ConfigurazioneNodiRuntime;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticDriver;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaDriver;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.crypt.PasswordVerifier;
import org.openspcoop2.web.monitor.core.config.ApplicationProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.status.SondaPddStatus;
import org.slf4j.Logger;

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
//	public boolean isAttivoModuloAllarmi() throws Exception{
//		String tmp = this.appProperties.getProperty("modules.allarmi", false, true);
//		if(tmp!=null && !"".equals(tmp)) {
//			return "true".equalsIgnoreCase(tmp);
//		}
//		else {
//			return false; // default
//		}
//	}
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
	public boolean isAttivoTransazioniEsitoMessageBoxIntegrationManager() throws Exception{
		String v = this.appProperties.getProperty("transazioni.esitoMessageBox.enabled", false, true);
		if(v!=null) {
			return "true".equalsIgnoreCase(v);
		}
		return false;
	}
	public boolean isAttivoSqlFilterTransazioniIntegrationManager() throws Exception{
		return this.isAttivoTransazioniIntegrationManager() || "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni_sql_im_enabled", true, true));
	}
	public boolean isAttivoTransazioniEsitiLive() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.esitiLiveEnabled", true, true));
	}
	public boolean isAttivoTransazioniExportHeader() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.export.enableHeaderInfo", true, true));
	}
	public boolean isAttivoTransazioniExportConsegneMultiple() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.export.enableConsegneMultipleInfo", true, true));
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
		List<String> l = new ArrayList<>();
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
		try {
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
				return p;
	 		}
		}finally {
			try {
				if(is!=null) {
					is.close();
				}
			}catch(Throwable t) {
				// ignore
			}
		}
		return null;
	}
	
	public List<String> getTransazioniForceIndexAndamentoTemporaleFindAll(Properties externalRepository) throws Exception{
		return this.getIndexList("transazioni.forceIndex.andamentoTemporale.findAll", externalRepository);
	}
	public List<String> getTransazioniForceIndexAndamentoTemporaleCount(Properties externalRepository) throws Exception{
		return this.getIndexList("transazioni.forceIndex.andamentoTemporale.count", externalRepository);
	}
	
	public List<String> getTransazioniForceIndexIdApplicativoBaseRichiestaFindAll(Properties externalRepository) throws Exception{
		return this.getIndexList("transazioni.forceIndex.idApplicativo.base.richiesta.findAll", externalRepository);
	}
	public List<String> getTransazioniForceIndexIdApplicativoBaseRichiestaCount(Properties externalRepository) throws Exception{
		return this.getIndexList("transazioni.forceIndex.idApplicativo.base.richiesta.count", externalRepository);
	}
	
	public List<String> getTransazioniForceIndexIdApplicativoBaseRispostaFindAll(Properties externalRepository) throws Exception{
		return this.getIndexList("transazioni.forceIndex.idApplicativo.base.risposta.findAll", externalRepository);
	}
	public List<String> getTransazioniForceIndexIdApplicativoBaseRispostaCount(Properties externalRepository) throws Exception{
		return this.getIndexList("transazioni.forceIndex.idApplicativo.base.risposta.count", externalRepository);
	}
	
	public List<String> getTransazioniForceIndexIdApplicativoAvanzataFindAll(Properties externalRepository) throws Exception{
		return this.getIndexList("transazioni.forceIndex.idApplicativo.avanzata.findAll", externalRepository);
	}
	public List<String> getTransazioniForceIndexIdApplicativoAvanzataCount(Properties externalRepository) throws Exception{
		return this.getIndexList("transazioni.forceIndex.idApplicativo.avanzata.count", externalRepository);
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
	
	public List<String> getStatisticheForceIndexDistribuzioneErroriGroupBy(Properties externalRepository) throws Exception{
		return this.getIndexList("statistiche.forceIndex.distribuzioneErrori.groupBy", externalRepository);
	}
	public List<String> getStatisticheForceIndexDistribuzioneErroriCount(Properties externalRepository) throws Exception{
		return this.getIndexList("statistiche.forceIndex.distribuzioneErrori.count", externalRepository);
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
			List<String> l = new ArrayList<>();
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
	
	private static ConfigurazioneNodiRuntime externalConfigurazioneNodiRuntime = null;
	private static ConfigurazioneNodiRuntime backwardCompatibilityConfigurazioneNodiRuntime = null;
	private static synchronized void initConfigurazioneNodiRuntime(String prefix) {
		if(backwardCompatibilityConfigurazioneNodiRuntime==null) {
			externalConfigurazioneNodiRuntime = ConfigurazioneNodiRuntime.getConfigurazioneNodiRuntime();
			backwardCompatibilityConfigurazioneNodiRuntime = ConfigurazioneNodiRuntime.getConfigurazioneNodiRuntime(prefix);
		}
	}
	private ConfigurazioneNodiRuntime _getConfigurazioneNodiRuntime() {
		if(backwardCompatibilityConfigurazioneNodiRuntime==null) {
			initConfigurazioneNodiRuntime(this.appProperties.getJmxPdD_backwardCompatibilityPrefix());
		}
		return externalConfigurazioneNodiRuntime;
	}
	private ConfigurazioneNodiRuntime _getBackwardCompatibilityConfigurazioneNodiRuntime() {
		if(backwardCompatibilityConfigurazioneNodiRuntime==null) {
			initConfigurazioneNodiRuntime(this.appProperties.getJmxPdD_backwardCompatibilityPrefix());
		}
		return backwardCompatibilityConfigurazioneNodiRuntime;
	}
	public ConfigurazioneNodiRuntime getConfigurazioneNodiRuntime() {
		ConfigurazioneNodiRuntime config = _getConfigurazioneNodiRuntime();
		if(config==null) {
			config = _getBackwardCompatibilityConfigurazioneNodiRuntime();
		}
		return config;
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
	
	public Integer getNumeroLabelDefaultDistribuzioneTemporale() throws Exception{
		return Integer.parseInt(this.appProperties.getProperty("statistiche.distribuzioneTemporale.numeroLabel", true, true));
	}
	
	public boolean isNascondiComandoNumeroLabelSeInferioreAlNumeroRisultati() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("statistiche.distribuzioneTemporale.nascondiComandoSelezioneNumeroLabel", true, true));
	}
	
	public boolean isMostraUnitaTempoDistribuzioneNonTemporale() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("statistiche.distribuzioneNonTemporale.mostraUnitaTempo", true, true));
	}
	
	public boolean isMostraUnitaTempoDistribuzioneNonTemporale_periodoPersonalizzato() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("statistiche.distribuzioneNonTemporale.periodoPersonalizzato.mostraUnitaTempo", true, true));
	}
	
	public boolean isDistribuzioneTokenClientIdInformazioniPDNDAggiungiInformazioneApplicativoRegistrato() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("statistiche.distribuzioneToken.clientIdConInformazioniPDND.export.aggiuntiInformazioneApplicativoRegistrato", false, true));
	}
	
	public Integer getIntervalloTimeoutRicercaStatistiche() throws Exception{
		String timeoutS = this.appProperties.getProperty("statistiche.timeoutRicercaStatistiche", false, true);
		return StringUtils.isNotBlank(timeoutS) ? Integer.parseInt(timeoutS) : null;
	}
	
	public boolean isStatisticheLatenzaPortaEnabled() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("statistiche.latenzaPorta.enabled", true, true));
	}

	public boolean isStatisticheVisualizzaValoriNelleCelleDelGraficoHeatmap() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("statistiche.graficoHeatmap.visualizzaValoriNelleCelle.enabled", false, true));
	}
	
	public boolean isStatisticheVisualizzaValoreZeroNelGraficoHeatmap() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("statistiche.graficoHeatmap.visualizzaValoreZero.enabled", false, true));
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
	
	public Integer getIntervalloTimeoutRicercaTransazioniLive() throws Exception{
		String timeoutS = this.appProperties.getProperty("transazioni.live.timeoutRicercaTransazioni", false, true);
		return StringUtils.isNotBlank(timeoutS) ? Integer.parseInt(timeoutS) : null;
	}
	
	// status pdd
	
	public boolean isStatusPdDEnabled() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("statoPdD.enabled", true, true));
	}
	
	public String getIntervalloRefreshStatusPdD() throws Exception{
		return this.appProperties.getProperty("statoPdD.refresh_interval", this.isStatusPdDEnabled(), true);
	}
	
	public List<String> getListaPdDMonitorate_StatusPdD() throws Exception{
		List<String> lista = new ArrayList<>();
		String tmp = this.appProperties.getProperty("statoPdD.sonde.standard.nodi", this.isStatusPdDEnabled(), true);
		if(tmp!=null && !"".equals(tmp)){
			String[]split = tmp.split(",");
			for (int i = 0; i < split.length; i++) {
				lista.add(split[i].trim());
			}
		}
		
		if(SondaPddStatus.GATEWAY_DEFAULT.equals(tmp)) {
			// se sono definiti dei nodi tramite aliases uso quelli
			ConfigurazioneNodiRuntime config = getConfigurazioneNodiRuntime();
			
			if(config!=null) {
				List<String> listaAliases = config.getAliases();
				if(listaAliases!=null && !listaAliases.isEmpty()) {
					if(listaAliases.size()>1) {
						lista = listaAliases;
					}
					else {
						String alias = listaAliases.get(0);
						if(!SondaPddStatus.GATEWAY_DEFAULT.equals(alias) && !SondaPddStatus.ALIAS_DEFAULT.equals(alias)) {
							lista = listaAliases;
						}
					}
				}
			}
		}
		
		return lista;
	}

	public List<String> getListaSondePdd() throws Exception{
		List<String> lista = new ArrayList<>();
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
	
	
	// Numero thread ricerche con timeout
	public Integer getDimensionePoolRicercheConTimeout() throws Exception{
		return Integer.parseInt(this.appProperties.getProperty("console.search.numeroThreadGestioneTimeout", true, true));
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
	
	// Abilita il cluster dinamico
	public boolean isClusterDinamico() throws Exception{
		ConfigurazioneNodiRuntime config = getConfigurazioneNodiRuntime();
		if(config!=null) {
			return config.isClusterDinamico();
		}
		else {
			//return getBackwardCompatibilityConfigurazioneNodiRuntime().isClusterDinamico();
			// abbiamo cambiato il nome della proprietà nella gestione 'ConfigurazioneNodiRuntime'
			return "true".equalsIgnoreCase(this.appProperties.getProperty("cluster_dinamico.enabled", true, true));
		}
	}
	public int getClusterDinamicoRefresh() throws Exception{
		if(this.isClusterDinamico()) {
			return Integer.valueOf(this.appProperties.getProperty("cluster_dinamico.refresh", true, true));
		}
		return -1;
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
	
	public boolean isCheckPasswordExpire(PasswordVerifier passwordVerifier) throws Exception { 
		if(passwordVerifier != null) {
			return this.isLoginApplication() && passwordVerifier.isCheckPasswordExpire();
		}
		
		return false;
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
	
	public Locale getConsoleLocale() throws Exception {
		String localeLang = this.appProperties.getProperty("console.locale.lang", false, true);
		String localeCountry = this.appProperties.getProperty("console.locale.country", false, true);
		
		if(StringUtils.isNotBlank(localeLang)) {
			if(StringUtils.isBlank(localeCountry))
				localeCountry = "";
			return new Locale(localeLang, localeCountry);
		}
		
		return null;
	}

	public String getLoginUtenteNonAutorizzatoRedirectUrl() throws Exception{
		if(this.isLoginApplication()) {
			return "";
		}
		return this.appProperties.getProperty("login.utenteNonAutorizzato.redirectUrl", true, true);
	}

	public String getLoginUtenteNonValidoRedirectUrl() throws Exception{
		if(this.isLoginApplication()) {
			return "";
		}
		return this.appProperties.getProperty("login.utenteNonValido.redirectUrl", true, true);
	}
	
	public String getLoginErroreInternoRedirectUrl() throws Exception{
		if(this.isLoginApplication()) {
			return "";
		}
		return this.appProperties.getProperty("login.erroreInterno.redirectUrl", true, true);
	}

	public String getLoginSessioneScadutaRedirectUrl() throws Exception{
		if(this.isLoginApplication()) {
			return "";
		}
		return this.appProperties.getProperty("login.sessioneScaduta.redirectUrl", true, true);
	}

	public boolean isMostraButtonLogout() throws Exception{
		if(this.isLoginApplication()) {
			return true;
		}
		return "true".equalsIgnoreCase(this.appProperties.getProperty("logout.mostraButton.enabled", true, true));
	}

	public String getLogoutUrlDestinazione() throws Exception{
		if(this.isLoginApplication()) {
			return "";
		}
		return this.appProperties.getProperty("logout.urlDestinazione", true, true);
	}
	
	public boolean isGestionePasswordUtentiAttiva() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("utenti.gestionePassword.enabled", true, true));
	}
	
	public String getUtentiPassword() throws Exception{
		return this.appProperties.getProperty("utenti.password", true, true);
	}
	
	public boolean isRuoloConfiguratoreAttivo() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("utenti.ruoloConfiguratore.enabled", true, true));
	}
	
	public boolean isModificaProfiloUtenteDaLinkAggiornaDB() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("utenti.modificaProfiloUtenteDaLink.aggiornaInfoSuDb", true, true));
	}
	
	public boolean isModificaProfiloUtenteDaFormAggiornaSessione() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("utenti.modificaProfiloUtenteDaForm.aggiornaInfoInSessione", true, true));
	}
	
	// Gestore Filtri
	
	public boolean isGestoreFiltriEnabled() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("gestoreFiltri.enabled", true, true));
	}
	
	public List<String> getListaFiltri(String tipo) throws Exception{
		List<String> lista = new ArrayList<>();
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
	
	
	public Integer getIntervalloTimeoutRicercaTransazioniStorico() throws Exception{
		String timeoutS = this.appProperties.getProperty("transazioni.storico.timeoutRicercaTransazioni", false, true);
		return StringUtils.isNotBlank(timeoutS) ? Integer.parseInt(timeoutS) : null;
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
	
	// Acceduta da ApplicationProperties
	@Deprecated
	public int getTransazioniDettaglioVisualizzazioneMessaggiThreshold() throws Exception{
		return Integer.valueOf(this.appProperties.getProperty("transazioni.dettaglio.visualizzazioneMessaggi.threshold", true, true));
	}
	
	public int getTransazioniDettaglioAnalisiMultipartThreshold() throws Exception{
		return Integer.valueOf(this.appProperties.getProperty("transazioni.dettaglio.analisiMultipart.threshold", true, true));
	}
	
	public boolean isDataUscitaRispostaUseDateAfterResponseSent() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.dataUscitaRisposta.useDateAfterResponseSent", true, true));
	}
	
	public boolean isTransazioniLatenzaPortaEnabled() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("transazioni.latenzaPorta.enabled", true, true));
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

	public boolean isAttivoUtilizzaCountListaAllarmi() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("allarmi.lista.utilizzaCount.enabled", true, false));
	}
	
	public boolean isAttivoVisualizzazioneConfigurazioneEventiTimeout() throws Exception{
		String p = this.appProperties.getProperty("eventi.timeout.viewConfigurazione", false, true);
		return "true".equalsIgnoreCase(p);
	}
	
	public boolean isAttivoUtilizzaCountStatisticheListaConfigurazioni() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("statistiche.configurazioni.lista.utilizzaCount.enabled", true, true));
	}
	
	public boolean isUseStatisticheGiornaliereCalcoloDistribuzioneSettimanale() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("statistiche.distribuzioneSettimanale.usaStatisticheGiornaliere", true, true));
	}
	public boolean isUseStatisticheGiornaliereCalcoloDistribuzioneMensile() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("statistiche.distribuzioneMensile.usaStatisticheGiornaliere", true, true));
	}
	public boolean isMediaPesataCalcoloDistribuzioneSettimanaleMensileUtilizzandoStatisticheGiornaliere() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("statistiche.distribuzioneSettimanaleMensile.usaStatisticheGiornaliere.latenza.mediaPesata", true, true));
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
	
	public Integer getSearchFormLimit() throws Exception{
		return Integer.valueOf(this.appProperties.getProperty("console.searchForm.limit",true,true));
	}
	
	public Integer getValiditaTokenCsrf() throws Exception{
		return Integer.valueOf(this.appProperties.getProperty("console.csrf.token.validita", true, true));
	}
	
	public String getCspHeaderValue() throws Exception {
		return this.appProperties.getProperty("console.csp.header.value", false, true);
	}
	
	public String getXContentTypeOptionsHeaderValue() throws Exception{
		return this.appProperties.getProperty("console.xContentTypeOptions.header.value", false, true);
	}
	
	public String getXFrameOptionsHeaderValue() throws Exception{
		return this.appProperties.getProperty("console.xFrameOptions.header.value", false, true);
	}
	
	public String getXXssProtectionHeaderValue() throws Exception{
		return this.appProperties.getProperty("console.xXssProtection.header.value", false, true);
	}
	
	public Properties getConsoleSecurityConfiguration() throws Exception{
		return this.appProperties.readProperties("console.security.");
	}
	
	// allarmi
	
	public boolean isAllarmiEnabled() throws Exception{
		return "true".equalsIgnoreCase(this.appProperties.getProperty("allarmi.enabled", true, true));
	}
	
	public String getAllarmiConfigurazione() throws Exception{
		return this.appProperties.getProperty("allarmi.configurazione", true, true);
	}
	
	
	// Eventi
	
	public Integer getIntervalloTimeoutRicercaEventi() throws Exception{
		String timeoutS = this.appProperties.getProperty("eventi.lista.timeoutRicercaEventi", false, true);
		return StringUtils.isNotBlank(timeoutS) ? Integer.parseInt(timeoutS) : null;
	}
}
