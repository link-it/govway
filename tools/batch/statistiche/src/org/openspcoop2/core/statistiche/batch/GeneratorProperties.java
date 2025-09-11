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


package org.openspcoop2.core.statistiche.batch;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.monitor.engine.statistic.StatisticsForceIndexConfig;
import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.properties.PropertiesReader;
import org.openspcoop2.utils.transport.http.HttpRequestConfig;

/**
* GeneratorProperties
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class GeneratorProperties {
	
	private static GeneratorProperties staticInstance = null;
	private static synchronized void init() throws UtilsException{
		if(GeneratorProperties.staticInstance == null){
			GeneratorProperties.staticInstance = new GeneratorProperties();
		}
	}
	public static GeneratorProperties getInstance() throws UtilsException{
		if(GeneratorProperties.staticInstance == null){
			// spotbugs warning 'SING_SINGLETON_GETTER_NOT_SYNCHRONIZED': l'istanza viene creata allo startup
			synchronized (GeneratorProperties.class) {
				GeneratorProperties.init();
			}
		}
		return GeneratorProperties.staticInstance;
	}
	
	
	
	
	private static final String PROPERTIES_FILE = "/batch-statistiche.properties";
	
	private String protocolloDefault = null;
	
	private boolean statisticheGenerazioneBaseOrariaGestioneUltimaOra=false;
	private boolean statisticheGenerazioneBaseGiornalieraGestioneUltimoGiorno=false;
	private boolean statisticheGenerazioneBaseSettimanaleGestioneUltimaSettimana=false;
	private boolean statisticheGenerazioneBaseMensileGestioneUltimoMese=false;
	
	private StatisticsForceIndexConfig statisticheGenerazioneForceIndexConfig = null;
	
	private long waitMsBeforeNextInterval = -1;
	
	private boolean waitStatiInConsegna = false;
	
	private boolean generazioneStatisticheUseUnionForLatency = true;
	
	private boolean generazioneStatisticheCustom = false;
	private boolean analisiTransazioniCustom = false;

	private File pddMonitorFrameworkRepositoryJars = null;
	
	/** Richiesta configurata per effettuare chiamate al tracing pdnd **/
	private HttpRequestConfig pdndTracingRequestConfig;
	private boolean pdndTracingSoggettiDisabled = false;
	private Set<String> pdndTracingSoggettiEnabled;
	private Integer pdndTracingMaxAttempt = null;
	private boolean pdndTracingErogazioniEnabled = true;
	private boolean pdndTracingFruizioniEnabled = true;
	private List<Integer> pdndTracingPendingCheck = null;
	private int pdndTracingGenerazioneDelayMinutes = 0; 
	
	private PropertiesReader props;
	
	
	private GeneratorProperties() throws UtilsException {

		Properties pr = new Properties();
		try {
			InputStream is = GeneratorProperties.class.getResourceAsStream(GeneratorProperties.PROPERTIES_FILE);
			pr.load(is);
		} catch(Exception e) {
			throw new UtilsException("Errore durante l'init delle properties", e);
		}
		this.props = new PropertiesReader(pr, true);
		
		try {
			this.statisticheGenerazioneForceIndexConfig = new StatisticsForceIndexConfig(pr);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		
	}
	
	public void initProperties() throws UtilsException {
		
		// PROPERTIES
				
		this.protocolloDefault = this.getProperty("protocolloDefault", true);
		
		this.statisticheGenerazioneBaseOrariaGestioneUltimaOra = this.getBooleanProperty("statistiche.generazione.baseOraria.gestioneUltimaOra", true);
		this.statisticheGenerazioneBaseGiornalieraGestioneUltimoGiorno = this.getBooleanProperty("statistiche.generazione.baseGiornaliera.gestioneUltimoGiorno", true);
		this.statisticheGenerazioneBaseSettimanaleGestioneUltimaSettimana = this.getBooleanProperty("statistiche.generazione.baseSettimanale.gestioneUltimaSettimana", true);
		this.statisticheGenerazioneBaseMensileGestioneUltimoMese = this.getBooleanProperty("statistiche.generazione.baseMensile.gestioneUltimoMese", true);
	
		String p = this.getProperty("statistiche.generazione.tradeOffSeconds", false);
		this.waitMsBeforeNextInterval = p!=null ? Long.parseLong(p) : -1l;
				
		this.waitStatiInConsegna = this.getBooleanProperty("statistiche.generazione.attendiCompletamentoTransazioniInFasiIntermedie", false);
				
		this.generazioneStatisticheUseUnionForLatency = this.getBooleanProperty("statistiche.generazione.useUnionForLatency", true);
		
		this.generazioneStatisticheCustom = this.getBooleanProperty("statistiche.generazione.custom.enabled", true);
		this.analisiTransazioniCustom = this.getBooleanProperty("statistiche.generazione.custom.transazioniSdk.enabled", true);
		
		String tmp = this.getProperty("statistiche.pddmonitorframework.sdk.repositoryJars", false);
		if(tmp!=null){
			this.pddMonitorFrameworkRepositoryJars = new File(tmp);
		}
		
		this.pdndTracingRequestConfig = new HttpRequestConfig("statistiche.pdnd.tracciamento", this.props);
		
		String value = getProperty("statistiche.pdnd.tracciamento.soggetti.enabled", false);
		if (value == null || StringUtils.isEmpty(value.trim())) {
			this.pdndTracingSoggettiDisabled = true;
			this.pdndTracingSoggettiEnabled = Set.of();
		}
		else if("*".equals(value)) {
			this.pdndTracingSoggettiEnabled = Set.of();
		}
		else {
			this.pdndTracingSoggettiEnabled = Arrays.stream(value.split(","))
				.map(String::trim)
				.collect(Collectors.toSet());
		}
		value = getProperty("statistiche.pdnd.tracciamento.maxAttempts", false);
		if (value != null)
			this.pdndTracingMaxAttempt = Integer.valueOf(value);
		
		value = getProperty("statistiche.pdnd.tracciamento.erogazioni.enabled", false);
		if (Boolean.FALSE.toString().equals(value))
			this.pdndTracingErogazioniEnabled = false;
		
		value = getProperty("statistiche.pdnd.tracciamento.fruizioni.enabled", false);
		if (Boolean.FALSE.toString().equals(value))
			this.pdndTracingFruizioniEnabled = false;
		
		value = getProperty("statistiche.pdnd.tracciamento.pending.check", false);
		this.pdndTracingPendingCheck = (value == null || StringUtils.isEmpty(value.trim())) ? List.of(0) : Arrays.stream(value.split(","))
				.map(Integer::valueOf)
				.collect(Collectors.toList());
		
		value = getProperty("statistiche.pdnd.tracciamento.generazione.delayMinutes", false);
		if(value!=null && StringUtils.isNotEmpty(value.trim())) {
			this.pdndTracingGenerazioneDelayMinutes = Integer.parseInt(value);
		}

	}
	
	private String getProperty(String name,boolean required) throws UtilsException{
		String tmp = this.props.getValue_convertEnvProperties(name);
		if(tmp==null){
			if(required){
				throw new UtilsException("Property '"+name+"' not found");
			}
			else{
				return null;
			}
		}
		else{
			return tmp.trim();
		}
	}
	public String readProperty(boolean required,String property) throws UtilsException{
		return getProperty(property, required);
	}
	
	private boolean getBooleanProperty(String name,boolean required) throws UtilsException{
		String tmp = this.getProperty(name, required);
		if(tmp!=null){
			try{
				return Boolean.parseBoolean(tmp);
			}catch(Exception e){
				throw new UtilsException("Property '"+name+"' wrong int format: "+e.getMessage());
			}
		}
		else{
			return false;
		}
	}
	private BooleanNullable readBooleanProperty(boolean required,String property) throws UtilsException{
		String tmp = this.getProperty(property, required);
		if(tmp==null && !required) {
			return BooleanNullable.NULL(); // se e' required viene sollevata una eccezione dal metodo readProperty
		}
		if(!"true".equalsIgnoreCase(tmp) && !"false".equalsIgnoreCase(tmp)){
			throw new UtilsException("Property ["+property+"] with uncorrect value ["+tmp+"] (true/value expected)");
		}
		return Boolean.parseBoolean(tmp) ? BooleanNullable.TRUE() : BooleanNullable.FALSE();
	}
	private boolean parse(BooleanNullable b, boolean defaultValue) {
		return (b!=null && b.getValue()!=null) ? b.getValue() : defaultValue;
	}
	
	
	public String getProtocolloDefault() {
		return this.protocolloDefault;
	}
	
	public boolean isStatisticheGenerazioneDebug() throws UtilsException {
		return this.getBooleanProperty("statistiche.generazione.debug", true);
	}

	public boolean isStatisticheGenerazioneBaseOrariaGestioneUltimaOra() {
		return this.statisticheGenerazioneBaseOrariaGestioneUltimaOra;
	}
	public boolean isStatisticheGenerazioneBaseGiornalieraGestioneUltimoGiorno() {
		return this.statisticheGenerazioneBaseGiornalieraGestioneUltimoGiorno;
	}
	public boolean isStatisticheGenerazioneBaseSettimanaleGestioneUltimaSettimana() {
		return this.statisticheGenerazioneBaseSettimanaleGestioneUltimaSettimana;
	}
	public boolean isStatisticheGenerazioneBaseMensileGestioneUltimoMese() {
		return this.statisticheGenerazioneBaseMensileGestioneUltimoMese;
	}
	
	public StatisticsForceIndexConfig getStatisticheGenerazioneForceIndexConfig() {
		return this.statisticheGenerazioneForceIndexConfig;
	}
		
	public boolean isGenerazioneStatisticheUseUnionForLatency() {
		return this.generazioneStatisticheUseUnionForLatency;
	}
	
	public boolean isGenerazioneStatisticheCustom() {
		return this.generazioneStatisticheCustom;
	}
	public boolean isAnalisiTransazioniCustom() {
		return this.analisiTransazioniCustom;
	}
	
	public File getPddMonitorFrameworkRepositoryJars() {
		return this.pddMonitorFrameworkRepositoryJars;
	}
	
	public long getGenerazioneTradeOffMs() {	
		return this.waitMsBeforeNextInterval;
	}
	public boolean isGenerazioneAttendiCompletamentoTransazioniInFasiIntermedie() {	
		return this.waitStatiInConsegna;
	}
	
	public boolean isSecurityLoadBouncyCastleProvider() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "security.addBouncyCastleProvider");
		return parse(b, false);
	}
	
	
	public String getEnvMapConfig() throws UtilsException{
		return this.readProperty(false, "env.map.config");
	}
	public boolean isEnvMapConfigRequired() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "env.map.required");
		return this.parse(b, false);
	}
	
	
	public String getHSMConfigurazione() throws UtilsException {
		return this.readProperty(false, "hsm.config");
	}
	public boolean isHSMRequired() throws UtilsException {
		BooleanNullable b = this.readBooleanProperty(false, "hsm.required");
		return this.parse(b, false);
	}
	public boolean isHSMKeyPasswordConfigurable() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "hsm.keyPassword");
		return this.parse(b, false);
	}
	
	
	
	public String getBYOKConfigurazione() throws UtilsException{
		return this.readProperty(false, "byok.config");
	}
	public boolean isBYOKRequired() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "byok.required");
		return parse(b, false);
	}
	public String getBYOKEnvSecretsConfig() throws UtilsException{
		return this.readProperty(false, "byok.env.secrets.config");
	}
	public boolean isBYOKEnvSecretsConfigRequired() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "byok.env.secrets.required");
		return this.parse(b, false);
	}
	
	public HttpRequestConfig getPdndTracingRequestConfig() {
		return this.pdndTracingRequestConfig;
	}

	public void setPdndTracingRequestConfig(HttpRequestConfig pdndTracingRequestConfig) {
		this.pdndTracingRequestConfig = pdndTracingRequestConfig;
	}
	
	public Set<String> getPdndTracingSoggettiEnabled() {
		return this.pdndTracingSoggettiEnabled;
	}

	public void setPdndTracingSoggettiEnabled(Set<String> pdndTracingSoggettiEnabled) {
		this.pdndTracingSoggettiEnabled = pdndTracingSoggettiEnabled;
	}
	
	public boolean isPdndTracingSoggettiDisabled() {
		return this.pdndTracingSoggettiDisabled;
	}
	
	public void setPdndTracingSoggettiDisabled(boolean pdndTracingSoggettiDisabled) {
		this.pdndTracingSoggettiDisabled = pdndTracingSoggettiDisabled;
	}
	
	public boolean isPdndTracingErogazioniEnabled() {
		return this.pdndTracingErogazioniEnabled;
	}
	
	public void setPdndTracingErogazioniEnabled(boolean pdndTracingErogazioniEnabled) {
		this.pdndTracingErogazioniEnabled = pdndTracingErogazioniEnabled;
	}
	
	public boolean isPdndTracingFruizioniEnabled() {
		return this.pdndTracingFruizioniEnabled;
	}
	
	public void setPdndTracingFruizioniEnabled(boolean pdndTracingFruizioniEnabled) {
		this.pdndTracingFruizioniEnabled = pdndTracingFruizioniEnabled;
	}
	public Integer getPdndTracingMaxAttempt() {
		return this.pdndTracingMaxAttempt;
	}
	public void setPdndTracingMaxAttempt(Integer pdndTracingMaxAttempt) {
		this.pdndTracingMaxAttempt = pdndTracingMaxAttempt;
	}
	public List<Integer> getPdndTracingPendingCheck() {
		return this.pdndTracingPendingCheck;
	}
	public void setPdndTracingPendingCheck( List<Integer> pdndTracingPendingCheck) {
		this.pdndTracingPendingCheck = pdndTracingPendingCheck;
	}

	public int getPdndTracingGenerazioneDelayMinutes() {
		return this.pdndTracingGenerazioneDelayMinutes;
	}
	public void setPdndTracingGenerazioneDelayMinutes(int pdndTracingGenerazioneDelayMinutes) {
		this.pdndTracingGenerazioneDelayMinutes = pdndTracingGenerazioneDelayMinutes;
	}
}
