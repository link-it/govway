/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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



package org.openspcoop2.web.ctrlstat.config;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.core.mvc.properties.utils.PropertiesSourceConfiguration;
import org.openspcoop2.pdd.config.ConfigurazioneNodiRuntime;
import org.openspcoop2.pdd.config.ConfigurazionePriorita;
import org.openspcoop2.pdd.config.OpenSPCoop2ConfigurationException;
import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.ZipUtilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.slf4j.Logger;



/**
* ConsoleProperties
*
* @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/


public class ConsoleProperties {	

	/** Logger utilizzato per errori eventuali. */
	private Logger log = null;



	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'console.properties' */
	private ConsoleInstanceProperties reader;

	/** Copia Statica */
	private static ConsoleProperties consoleProperties = null;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	public ConsoleProperties(String confDir, String confPropertyName, String confLocalPathPrefix,Logger log) throws Exception {

		if(log!=null)
			this.log = log;
		else
			this.log = LoggerWrapperFactory.getLogger(ConsoleProperties.class);
		
		/* ---- Lettura del cammino del file di configurazione ---- */
		Properties propertiesReader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = ConsoleProperties.class.getResourceAsStream("/console.properties");
			if(properties==null){
				throw new Exception("File '/console.properties' not found");
			}
			propertiesReader.load(properties);
		}catch(Exception e) {
			this.log.error("Riscontrato errore durante la lettura del file 'console.properties': \n\n"+e.getMessage());
		    throw new Exception("ConsoleProperties initialize error: "+e.getMessage());
		}finally{
		    try{
				if(properties!=null)
				    properties.close();
		    }catch(Throwable er){
		    	// close
		    }
		}

		this.reader = new ConsoleInstanceProperties(propertiesReader, this.log, confDir, confPropertyName, confLocalPathPrefix);
	}


	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 *
	 * 
	 */
	public static boolean initialize(String confDir, String confPropertyName, String confLocalPathPrefix,Logger log){

		try {
		    ConsoleProperties.consoleProperties = new ConsoleProperties(confDir,confPropertyName,confLocalPathPrefix,log);	
		    return true;
		}
		catch(Exception e) {
			log.error("Inizializzazione fallita: "+e.getMessage(),e);
		    return false;
		}
	}
    
	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di ClassNameProperties
	 * 
	 */
	public static ConsoleProperties getInstance() throws OpenSPCoop2ConfigurationException{
		if(ConsoleProperties.consoleProperties==null){
	    	throw new OpenSPCoop2ConfigurationException("ConsoleProperties non inizializzato");
	    }
	    return ConsoleProperties.consoleProperties;
	}
    
	public static void updateLocalImplementation(Properties prop){
		ConsoleProperties.consoleProperties.reader.setLocalObjectImplementation(prop);
	}








	/* ********  M E T O D I  ******** */

	private String readProperty(boolean required,String property) throws UtilsException{
		String tmp = this.reader.getValue_convertEnvProperties(property);
		if(tmp==null){
			if(required){
				throw new UtilsException("Property ["+property+"] not found");
			}
			else{
				return null;
			}
		}else{
			return tmp.trim();
		}
	}
	private boolean readBooleanRequiredProperty(String property) throws UtilsException{
		return readBooleanProperty(true,property).getValue();
	}
	private BooleanNullable readBooleanProperty(boolean required,String property) throws UtilsException{
		String tmp = this.readProperty(required, property);
		if(tmp==null && !required) {
			return BooleanNullable.NULL(); // se e' required viene sollevata una eccezione dal metodo readProperty
		}
		if("true".equalsIgnoreCase(tmp)==false && "false".equalsIgnoreCase(tmp)==false){
			throw new UtilsException("Property ["+property+"] with uncorrect value ["+tmp+"] (true/value expected)");
		}
		return Boolean.parseBoolean(tmp) ? BooleanNullable.TRUE() : BooleanNullable.FALSE();
	}
	private Integer readIntegerProperty(boolean required,String property) throws UtilsException{
		String tmp = this.readProperty(required, property);
		if(tmp==null && !required) {
			return null; // se e' required viene sollevata una eccezione dal metodo readProperty
		}
		try{
			return Integer.parseInt(tmp);
		}catch(Exception e){
			throw new UtilsException("Property ["+property+"] with uncorrect value ["+tmp+"] (int value expected)");
		}
	}
	private Long readLongProperty(boolean required,String property) throws UtilsException{
		String tmp = this.readProperty(required, property);
		if(tmp==null && !required) {
			return null; // se e' required viene sollevata una eccezione dal metodo readProperty
		}
		try{
			return Long.parseLong(tmp);
		}catch(Exception e){
			throw new UtilsException("Property ["+property+"] with uncorrect value ["+tmp+"] (long value expected)");
		}
	}
	

	
	/* ----- Funzionalità Generiche -------- */
	
	public String getConfDirectory() throws UtilsException{
		return this.readProperty(false, "confDirectory");
	}
	
	public String getProtocolloDefault() throws UtilsException{
		return this.readProperty(false, "protocolloDefault");
	}
	
	public long getGestioneSerializableDB_AttesaAttiva() throws UtilsException {	
		return this.readLongProperty(false, "jdbc.serializable.attesaAttiva");
	}
	
	public int getGestioneSerializableDB_CheckInterval() throws UtilsException {	
		return this.readIntegerProperty(false, "jdbc.serializable.check");
	}
	
	public Boolean isSinglePdD() throws UtilsException{
		return this.readBooleanRequiredProperty("singlePdD");
	}
	
	public Boolean isToken_GenerazioneAutomaticaPorteDelegate_enabled() throws UtilsException{
		return this.readBooleanRequiredProperty("generazioneAutomaticaPorteDelegate.token.enabled");
	}
	
	public Boolean isAutenticazione_GenerazioneAutomaticaPorteDelegate_enabled() throws UtilsException{
		return this.readBooleanRequiredProperty("generazioneAutomaticaPorteDelegate.autenticazione.enabled");
	}
	public String getAutenticazione_GenerazioneAutomaticaPorteDelegate() throws UtilsException{
		return this.readProperty(true, "generazioneAutomaticaPorteDelegate.autenticazione");
	}
	
	public Boolean isAutorizzazione_GenerazioneAutomaticaPorteDelegate_enabled() throws UtilsException{
		return this.readBooleanRequiredProperty("generazioneAutomaticaPorteDelegate.autorizzazione.enabled");
	}
	public String getAutorizzazione_GenerazioneAutomaticaPorteDelegate() throws UtilsException{
		return this.readProperty(true, "generazioneAutomaticaPorteDelegate.autorizzazione");
	}
	
	public Boolean isToken_GenerazioneAutomaticaPorteApplicative_enabled() throws UtilsException{
		return this.readBooleanRequiredProperty("generazioneAutomaticaPorteApplicative.token.enabled");
	}
	
	public Boolean isAutenticazione_GenerazioneAutomaticaPorteApplicative_enabled() throws UtilsException{
		return this.readBooleanRequiredProperty("generazioneAutomaticaPorteApplicative.autenticazione.enabled");
	}
	public String getAutenticazione_GenerazioneAutomaticaPorteApplicative() throws UtilsException{
		return this.readProperty(true, "generazioneAutomaticaPorteApplicative.autenticazione");
	}
	
	public Boolean isAutorizzazione_GenerazioneAutomaticaPorteApplicative_enabled() throws UtilsException{
		return this.readBooleanRequiredProperty("generazioneAutomaticaPorteApplicative.autorizzazione.enabled");
	}
	public String getAutorizzazione_GenerazioneAutomaticaPorteApplicative() throws UtilsException{
		return this.readProperty(true, "generazioneAutomaticaPorteApplicative.autorizzazione");
	}
	
	public Boolean isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto() throws UtilsException{
		return this.readBooleanRequiredProperty("accordi.portType.implementazioneUnicaPerSoggetto");
	}
	
	public Boolean isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto() throws UtilsException{
		return this.readBooleanRequiredProperty("accordi.implementazioneUnicaPerSoggetto");
	}
	
	public String getImportArchive_tipoPdD() throws UtilsException{
		return this.readProperty(true, "importArchive.tipoPdD");
	}
	
	public boolean isExportArchive_configurazione_soloDumpCompleto() throws UtilsException{
		return this.readBooleanRequiredProperty("exportArchive.configurazione.soloDumpCompleto");
	}
	
	public boolean isExportArchive_servizi_standard() throws UtilsException{
		return this.readBooleanRequiredProperty("exportArchive.servizi.standard");
	}
	
	public boolean isGestoreConsistenzaDatiEnabled() throws UtilsException{
		return this.readBooleanRequiredProperty("gestoreConsistenzaDati");
	}
	
	public boolean isGestoreConsistenzaDati_forceCheckMapping() throws UtilsException{
		return this.readBooleanRequiredProperty("gestoreConsistenzaDati.forceCheckMapping");
	}
		
	public PropertiesSourceConfiguration getMessageSecurityPropertiesSourceConfiguration() throws UtilsException {
		return _getSourceConfiguration("messageSecurity", 
				"messageSecurity.dir", "messageSecurity.dir.refresh", 
				"messageSecurity.builtIn", "messageSecurity.builtIn.refresh");
	}
	
	public PropertiesSourceConfiguration getPolicyGestioneTokenPropertiesSourceConfiguration() throws UtilsException {
		return _getSourceConfiguration("policyGestioneToken", 
				"policyGestioneToken.dir", "policyGestioneToken.dir.refresh", 
				"policyGestioneToken.builtIn", "policyGestioneToken.builtIn.refresh");
	}
	
	public boolean isPolicyGestioneTokenVerificaCertificati() throws UtilsException{
		return this.readBooleanRequiredProperty("policyGestioneToken.verificaCertificati");
	}
	
	public List<String> getPolicyGestioneTokenPDND() throws UtilsException{
		List<String> l = new ArrayList<String>();
		String p = this.readProperty(false, "policyGestioneToken.policy");
		if(p!=null && !"".equals(p.trim())){
			String [] tmp = p.trim().split(",");
			for (int i = 0; i < tmp.length; i++) {
				tmp[i] = tmp[i].trim();
				l.add(tmp[i]);
			}
		}
		return l;
	}
	
	public PropertiesSourceConfiguration getAttributeAuthorityPropertiesSourceConfiguration() throws UtilsException {
		return _getSourceConfiguration("attributeAuthority", 
				"attributeAuthority.dir", "attributeAuthority.dir.refresh", 
				"attributeAuthority.builtIn", "attributeAuthority.builtIn.refresh");
	}
	
	public boolean isAttributeAuthorityVerificaCertificati() throws UtilsException{
		return this.readBooleanRequiredProperty("attributeAuthority.verificaCertificati");
	}
	
	public boolean isControlloTrafficoPolicyGlobaleGroupByApi() throws UtilsException{
		return this.readBooleanRequiredProperty("controlloTraffico.policyGlobale.groupBy.api");
	}
	
	public boolean isControlloTrafficoPolicyGlobaleFiltroApi() throws UtilsException{
		return this.readBooleanRequiredProperty("controlloTraffico.policyGlobale.filtro.api");
	}
	
	public boolean isControlloTrafficoPolicyGlobaleFiltroApiSoggettoErogatore() throws UtilsException{
		return this.readBooleanRequiredProperty("controlloTraffico.policyGlobale.filtro.api.soggettoErogatore");
	}
	
	public List<PolicyGroupByActiveThreadsType> getControlloTrafficoPolicyRateLimitingTipiGestori() throws UtilsException{
		List<PolicyGroupByActiveThreadsType> l = new ArrayList<PolicyGroupByActiveThreadsType>();
		String p = this.readProperty(false, "controlloTraffico.policyRateLimiting.tipiGestori");
		if(p!=null && !"".equals(p.trim())){
			String [] tmp = p.trim().split(",");
			for (int i = 0; i < tmp.length; i++) {
				tmp[i] = tmp[i].trim();
				l.add(PolicyGroupByActiveThreadsType.valueOf(tmp[i]));
			}
		}
		return l;
	}
	
	public boolean isAuditingRegistrazioneElementiBinari() throws UtilsException{
		return this.readBooleanRequiredProperty("auditing.registrazioneElementiBinari");
	}
	
	public boolean isIntegrationManagerEnabled() throws UtilsException{
		return this.readBooleanRequiredProperty("integrationManager.enabled");
	}
	
	public boolean isIntegrationManagerTraceMessageBoxOperationEnabled() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "integrationManager.traceMessageBoxOperation.enabled");
		if(b==null || b.getValue()==null) {
			return false;
		}
		return b.getValue();
	}
	
	public Integer getSoggettiNomeMaxLength() throws UtilsException{
		return this.readIntegerProperty(false, "soggetti.nome.maxLength");
	}
	
	public boolean isSoggettiVerificaCertificati() throws UtilsException{
		return this.readBooleanRequiredProperty("soggetti.verificaCertificati");
	}
	public boolean isSoggettiVerificaCertificati_checkCertificatoSoggettoById_useApi() throws UtilsException{
		return this.readBooleanRequiredProperty("soggetti.verificaCertificati.checkCertificatoSoggettoById.useApi");
	}
	
	public boolean isApplicativiVerificaCertificati() throws UtilsException{
		return this.readBooleanRequiredProperty("applicativi.verificaCertificati");
	}
	public boolean isApplicativiVerificaCertificati_checkCertificatoApplicativoById_useApi() throws UtilsException{
		return this.readBooleanRequiredProperty("applicativi.verificaCertificati.checkCertificatoApplicativoById.useApi");
	}
	
	public boolean isApiResourcePathValidatorEnabled() throws UtilsException{
		return this.readBooleanRequiredProperty("api.resource.pathValidator");
	}
	
	public boolean isApiResourceHttpMethodAndPathQualsiasiEnabled() throws UtilsException{
		return this.readBooleanRequiredProperty("api.resource.httpMethodAndPathQualsiasi.enabled");
	}
	
	public List<String> getApiResourcePathQualsiasiSpecialChar() throws UtilsException{
		List<String> l = new ArrayList<String>();
		String p = this.readProperty(false, "api.resource.pathQualsiasi.specialChar");
		if(p!=null && !"".equals(p.trim())){
			String [] tmp = p.trim().split(",");
			for (int i = 0; i < tmp.length; i++) {
				tmp[i] = tmp[i].trim();
				l.add(tmp[i]);
			}
		}
		return l;
	}
	
	public boolean isApiOpenAPIValidateUriReferenceAsUrl() throws UtilsException{
		return this.readBooleanRequiredProperty("api.openApi.openapi4j.validateUriReferenceAsUrl");
	}
	
	public boolean isApiRestResourceRepresentationMessageTypeOverride() throws UtilsException{
		return this.readBooleanRequiredProperty("api.resource.representation.messageTypeOverride");
	}
	
	public Properties getApiYamlSnakeLimits() throws UtilsException{

		String pName = "api.yaml.snakeLimits";
		
		try{  
			String file = this.readProperty(false, pName);
			if(file!=null && StringUtils.isNotEmpty(file)) {
				File f = new File(file);
				if(f.exists()) {
					if(!f.isFile()) {
						throw new Exception("Il file indicato '"+f.getAbsolutePath()+"' non è un file");
					}
					if(!f.canRead()) {
						throw new Exception("Il file indicato '"+f.getAbsolutePath()+"' non è accessibile in lettura");
					}
					try(InputStream is = new FileInputStream(f)){
						Properties p = new Properties();
						p.load(is);
						if (p != null && !p.isEmpty()){
							return p;
						}
					}
				}
			}
		
			return null;
			
		}catch(java.lang.Exception e) {
			throw new UtilsException("Proprieta' '"+pName+"' non impostate, errore:"+e.getMessage(),e);
		}
	}
	
	public boolean isAccordiCooperazioneEnabled() throws UtilsException{
		return this.readBooleanRequiredProperty("accordiCooperazione.enabled");
	}
	
	public boolean isErogazioniVerificaCertificati() throws UtilsException{
		return this.readBooleanRequiredProperty("erogazioni.verificaCertificati");
	}
	public boolean isFruizioniVerificaCertificati() throws UtilsException{
		return this.readBooleanRequiredProperty("fruizioni.verificaCertificati");
	}

	public List<String> getMessageEngines() throws UtilsException{
		String s = this.readProperty(false, "messageEngine");
		List<String>  l = null;
		if(s!=null && !"".equals(s)) {
			l = new ArrayList<>();
			if(s.contains(",")) {
				String [] tmp = s.split(",");
				for (String ss : tmp) {
					String sTmp = ss.trim();
					if(sTmp!=null && !"".equals(sTmp)) {
						l.add(sTmp);
					}
				}
			}
			else {
				l.add(s);
			}
		}
		return l;
	}
	
	public boolean isSoggettiCredenzialiBasicCheckUniqueUsePassword() throws UtilsException{
		return this.readBooleanRequiredProperty("soggetti.credenzialiBasic.checkUnique.usePassword");
	}
	
	public boolean isApplicativiCredenzialiBasicCheckUniqueUsePassword() throws UtilsException{
		return this.readBooleanRequiredProperty("applicativi.credenzialiBasic.checkUnique.usePassword");
	}
	
	public boolean isSoggettiApplicativiCredenzialiBasicPermitSameCredentials() throws UtilsException{
		return this.readBooleanRequiredProperty("soggettiApplicativi.credenzialiBasic.permitSameCredentials");
	}
	public boolean isSoggettiApplicativiCredenzialiSslPermitSameCredentials() throws UtilsException{
		return this.readBooleanRequiredProperty("soggettiApplicativi.credenzialiSsl.permitSameCredentials");
	}
	public boolean isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentials() throws UtilsException{
		return this.readBooleanRequiredProperty("soggettiApplicativi.credenzialiPrincipal.permitSameCredentials");
	}

	public boolean isConnettoriAllTypesEnabled() throws UtilsException{
		return this.readBooleanRequiredProperty("connettori.allTypes.enabled");
	}
	
	public boolean isConnettoriMultipliEnabled() throws UtilsException{
		return this.readBooleanRequiredProperty("connettoriMultipli.enabled");
	}
	
	public boolean isConnettoriMultipliConsegnaCondizionaleStessFiltroPermesso() throws UtilsException{
		return this.readBooleanRequiredProperty("connettoriMultipli.consegnaCondizionale.stessoFiltro");
	}
	
	public boolean isConnettoriMultipliConsegnaMultiplaEnabled() throws UtilsException{
		return this.readBooleanRequiredProperty("connettoriMultipli.consegnaMultipla.enabled");
	}
	
	public boolean isApplicativiServerEnabled() throws UtilsException{
		return this.readBooleanRequiredProperty("applicativiServer.enabled");
	}
	
	public List<String> getConsegnaNotificaCode() throws UtilsException{
		List<String> l = new ArrayList<String>();
		String p = this.readProperty(true, "consegnaNotifiche.code");
		if(p!=null && !"".equals(p.trim())){
			String [] tmp = p.trim().split(",");
			for (int i = 0; i < tmp.length; i++) {
				tmp[i] = tmp[i].trim();
				l.add(tmp[i]);
			}
		}
		return l;
	}
	public String getConsegnaNotificaCodaLabel(String nome) throws UtilsException{
		return this.readProperty(true, "consegnaNotifiche.coda."+nome+".label");
	}
	
	public List<String> getConsegnaNotificaPriorita() throws UtilsException{
		List<String> l = new ArrayList<String>();
		String p = this.readProperty(true, "consegnaNotifiche.priorita");
		if(p!=null && !"".equals(p.trim())){
			String [] tmp = p.trim().split(",");
			for (int i = 0; i < tmp.length; i++) {
				tmp[i] = tmp[i].trim();
				l.add(tmp[i]);
			}
		}
		return l;
	}
	
	public ConfigurazionePriorita getConsegnaNotificaConfigurazionePriorita(String nome) throws Exception{
		Properties p = this.reader.readProperties_convertEnvProperties("consegnaNotifiche.priorita."+nome+".");
		return new ConfigurazionePriorita(nome, p);
	}
	
	public boolean isModipaErogazioniVerificaCertificati() throws UtilsException{
		return this.readBooleanRequiredProperty("modipa.erogazioni.verificaCertificati");
	}
	public boolean isModipaFruizioniVerificaCertificati() throws UtilsException{
		return this.readBooleanRequiredProperty("modipa.fruizioni.verificaCertificati");
	}
	
	public boolean isModipaFruizioniConnettoreCheckHttps() throws UtilsException{
		return this.readBooleanRequiredProperty("modipa.fruizioni.connettore.checkHttps");
	}
	public boolean isModipaFiltroRicercaProfiloQualsiasiVisualizzaDatiModi() throws UtilsException{
		return this.readBooleanRequiredProperty("modipa.filtroRicerca.profiloQualsiasi.visualizzaDatiModi");
	}
	
	public Boolean isConfigurazionePluginsEnabled() throws UtilsException{
		return this.readBooleanRequiredProperty("plugins.enabled");
	}
	
	public Integer getPluginsSeconds() throws Exception{
		String cacheV = this.readProperty(true, "plugins.seconds");
		if(cacheV!=null && StringUtils.isNotEmpty(cacheV)) {
			Integer i = Integer.valueOf(cacheV);
			if(i.intValue()>0) {
				return i;
			}
		}
		return 300;
	}
	
	public Boolean isConfigurazioneHandlersEnabled() throws UtilsException{
		return this.readBooleanRequiredProperty("handlers.enabled");
	}
	
	public Boolean isConfigurazioneAllarmiEnabled() throws UtilsException{
		return this.readBooleanRequiredProperty("allarmi.enabled");
	}
	
	public String getAllarmiConfigurazione() throws Exception{
		return this.readProperty(true, "allarmi.configurazione");
	}
	
	public Boolean isShowAllarmiIdentificativoRuntime() throws UtilsException{
		return this.readBooleanRequiredProperty("allarmi.identificativoRuntime");
	}
	public Boolean isShowAllarmiFormNomeSuggeritoCreazione() throws UtilsException{
		return this.readBooleanRequiredProperty("allarmi.form.nomeSuggeritoCreazione");
	}
	public Boolean isShowAllarmiFormStatoAllarme() throws UtilsException{
		return this.readBooleanRequiredProperty("allarmi.form.statoAllarme");
	}
	public Boolean isShowAllarmiFormStatoAllarmeHistory() throws UtilsException{
		return this.readBooleanRequiredProperty("allarmi.form.statoAllarme.history");
	}
	public Boolean isShowAllarmiSearchStatiAllarmi() throws UtilsException{
		return this.readBooleanRequiredProperty("allarmi.search.statoAllarme");
	}
	public Boolean isShowAllarmiElenchiStatiAllarmi() throws UtilsException{
		return this.readBooleanRequiredProperty("allarmi.elenchi.statoAllarme");
	}
	
	public Boolean isRegistrazioneMessaggi_multipartPayloadParsing_enabled() throws UtilsException{
		return this.readBooleanRequiredProperty("registrazioneMessaggi.multipartPayloadParsing.enabled");
	}
	
	public Boolean isClusterDinamico_enabled() throws UtilsException{
		ConfigurazioneNodiRuntime config = getConfigurazioneNodiRuntime();
		if(config!=null) {
			return config.isClusterDinamico();
		}
		else {
			//return getBackwardCompatibilityConfigurazioneNodiRuntime().isClusterDinamico();
			// abbiamo cambiato il nome della proprietà nella gestione 'ConfigurazioneNodiRuntime'
			return this.readBooleanRequiredProperty("cluster_dinamico.enabled");
		}
	}

	public String getHSMConfigurazione() throws Exception{
		return this.readProperty(false, "hsm.config");
	}
	public boolean isHSMRequired() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "hsm.required");
		return (b!=null && b.getValue()!=null) ? b.getValue() : false;
	}
	public boolean isHSMKeyPasswordConfigurable() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "hsm.keyPassword");
		return (b!=null && b.getValue()!=null) ? b.getValue() : false;
	}
	
	public Integer getVerificaCertificati_warning_expirationDays() throws Exception{
		String cacheV = this.readProperty(true, "verificaCertificati.warning.expirationDays");
		if(cacheV!=null && StringUtils.isNotEmpty(cacheV)) {
			Integer i = Integer.valueOf(cacheV);
			if(i.intValue()>0) {
				return i;
			}
		}
		return 10;
	}
	
	public String getOCSPConfigurazione() throws Exception{
		return this.readProperty(false, "ocsp.config");
	}
	public boolean isOCSPRequired() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "ocsp.required");
		return (b!=null && b.getValue()!=null) ? b.getValue() : false;
	}
	public boolean isOCSPLoadDefault() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "ocsp.loadDefault");
		return (b!=null && b.getValue()!=null) ? b.getValue() : true;
	}
	public boolean isOCSPPolicyChoiceConnettoreHTTPSVerificaServerDisabilitata() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "ocsp.https.verificaServerDisabilitata.policyChoice");
		return (b!=null && b.getValue()!=null) ? b.getValue() : false;
	}
	
	public boolean isVerificaCertificati_sceltaClusterId() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "verificaCertificati.sceltaClusterId");
		return (b!=null && b.getValue()!=null) ? b.getValue() : true;
	}
	
	
	/* ----- Gestione Password ------- */
	
	// Utenze Console
	
	public String getConsoleUtenzePassword() throws UtilsException{
		return this.readProperty(true, "console.utenze.password");
	}
	
	public int getConsoleUtenzeLunghezzaPasswordGenerate() throws UtilsException{
		String lunghezzaS = this.readProperty(true, "console.utenze.passwordGenerated.length");
		return Integer.parseInt(lunghezzaS); 
	}
	
	public boolean isConsoleUtenzeModificaProfiloUtenteDaLinkAggiornaDB() throws UtilsException{
		return this.readBooleanRequiredProperty("console.utenze.modificaProfiloUtenteDaLink.aggiornaInfoSuDb");
	}
	
	public boolean isConsoleUtenzeModificaProfiloUtenteDaFormAggiornaSessione() throws UtilsException{
		return this.readBooleanRequiredProperty("console.utenze.modificaProfiloUtenteDaForm.aggiornaInfoInSessione");
	}
	
	// Applicativi
	
	public String getConsoleApplicativiPassword() throws UtilsException{
		return this.readProperty(true, "console.applicativi.password");
	}
	
	public int getConsoleApplicativiBasicLunghezzaPasswordGenerate() throws UtilsException{
		String lunghezzaS = this.readProperty(true, "console.applicativi.basic.passwordGenerated.length");
		return Integer.parseInt(lunghezzaS); 
	}
	
	public int getConsoleApplicativiApiKeyLunghezzaPasswordGenerate() throws UtilsException{
		String lunghezzaS = this.readProperty(true, "console.applicativi.api_key.passwordGenerated.length");
		return Integer.parseInt(lunghezzaS); 
	}
	
	public boolean isConsoleApplicativiBasicPasswordEnableConstraints() throws UtilsException{
		return this.readBooleanRequiredProperty("console.applicativi.basic.password.enableConstraints");
	}
	
	// Soggetti
	
	public String getConsoleSoggettiPassword() throws UtilsException{
		return this.readProperty(true, "console.soggetti.password");
	}
	
	public int getConsoleSoggettiBasicLunghezzaPasswordGenerate() throws UtilsException{
		String lunghezzaS = this.readProperty(true, "console.soggetti.basic.passwordGenerated.length");
		return Integer.parseInt(lunghezzaS); 
	}
	
	public int getConsoleSoggettiApiKeyLunghezzaPasswordGenerate() throws UtilsException{
		String lunghezzaS = this.readProperty(true, "console.soggetti.api_key.passwordGenerated.length");
		return Integer.parseInt(lunghezzaS); 
	}
	
	public boolean isConsoleSoggettiBasicPasswordEnableConstraints() throws UtilsException{
		return this.readBooleanRequiredProperty("console.soggetti.basic.password.enableConstraints");
	}


	/* ----- Impostazioni grafiche ------- */
	
	public String getConsoleNomeSintesi() throws UtilsException{
		return this.readProperty(true, "console.nome.sintesi");
	}
	
	public String getConsoleNomeEsteso() throws UtilsException{
		return this.readProperty(true, "console.nome.esteso");
	}
	
	public String getConsoleCSS() throws UtilsException{
		return this.readProperty(true, "console.css");
	}
	
	public String getConsoleLanguage() throws UtilsException{
		return this.readProperty(true, "console.language");
	}
	
	public int getConsoleLunghezzaLabel() throws UtilsException{
		String lunghezzaS = this.readProperty(true, "console.lunghezzaLabel");
		return Integer.parseInt(lunghezzaS); 
	}
	
	public int getConsoleNumeroColonneDefaultTextArea() throws UtilsException{
		String lunghezzaS = this.readProperty(true, "console.colonneTextArea.default");
		return Integer.parseInt(lunghezzaS); 
	}
	
	public String getLogoHeaderImage() throws Exception{
		return this.readProperty(false,"console.header.logo.image");
	}

	public String getLogoHeaderTitolo() throws Exception{
		return this.readProperty(false,"console.header.logo.titolo");
	}

	public String getLogoHeaderLink() throws Exception{
		return this.readProperty(false,"console.header.logo.link");
	}
	
	public boolean isVisualizzaLinkHomeHeader() throws UtilsException{
		return this.readBooleanRequiredProperty("console.header.home.link.enabled");
	}
	
	/* ----- Opzioni Accesso JMX della PdD ------- */
	
	public boolean isVisualizzaLinkClearAllCaches_remoteCheckCacheStatus() throws UtilsException{
		return this.readBooleanRequiredProperty("risorseJmxPdd.linkClearAllCaches.remoteCheckCacheStatus");
	}
	
	public String getJmxPdD_externalConfiguration() throws UtilsException{
		return this.readProperty(false, "risorseJmxPdd.configurazioneNodiRun");
	}
	
	public String getJmxPdD_backwardCompatibilityPrefix() {
		return "risorseJmxPdd.";
	}
	
	public Properties getJmxPdD_backwardCompatibilityProperties() throws UtilsException{
		
		String prefix = getJmxPdD_backwardCompatibilityPrefix();
		
		Properties p = new Properties();
		Enumeration<?> en = this.reader.propertyNames();
		while (en.hasMoreElements()) {
			Object object = (Object) en.nextElement();
			if(object !=null && object instanceof String) {
				String key = (String) object;
				if(key.contains(prefix)) {
					String newKey = key.replace(prefix, "");
					p.put(newKey, this.reader.getValue_convertEnvProperties(key));
				}
			}
		}
		return p;
		
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
			initConfigurazioneNodiRuntime(getJmxPdD_backwardCompatibilityPrefix());
		}
		return externalConfigurazioneNodiRuntime;
	}
	private ConfigurazioneNodiRuntime _getBackwardCompatibilityConfigurazioneNodiRuntime() {
		if(backwardCompatibilityConfigurazioneNodiRuntime==null) {
			initConfigurazioneNodiRuntime(getJmxPdD_backwardCompatibilityPrefix());
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
	
	public List<String> getJmxPdD_aliases() throws UtilsException {
		ConfigurazioneNodiRuntime config = getConfigurazioneNodiRuntime();
		if(config!=null) {
			return config.getAliases();
		}
		else {
			return null;
		}
	}
	
	public Map<String,List<String>> getJmxPdD_gruppi_aliases() throws UtilsException {
		ConfigurazioneNodiRuntime config = getConfigurazioneNodiRuntime();
		if(config!=null) {
			return config.getGruppi_aliases();
		}
		else {
			return null;
		}
	}
	
	public String getJmxPdD_descrizione(String alias) throws UtilsException {
		ConfigurazioneNodiRuntime config = getConfigurazioneNodiRuntime();
		if(config!=null) {
			return config.getDescrizione(alias);
		}
		else {
			return null;
		}
	}
	
	private String _getJmxPdD_value(boolean required, String alias, String prop) throws UtilsException{
		String tmp = this.readProperty(false, alias+"."+prop);
		if(tmp==null || "".equals(tmp)){
			tmp = this.readProperty(required, prop);
		}
		return tmp;
	}
	
	public String getJmxPdD_dominio(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.dominio");
	}
	public String getJmxPdD_configurazioneSistema_type(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.tipo");
	}
	public String getJmxPdD_configurazioneSistema_nomeRisorsa(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeRisorsa");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_versionePdD(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.versionePdD");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_versioneBaseDati(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.versioneBaseDati");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_versioneJava(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.versioneJava");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_vendorJava(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.vendorJava");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_tipoDatabase(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.tipoDatabase");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniDatabase(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoDatabase");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniSSL(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoSSL");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteSSL(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoSSLComplete");
	}
	public boolean isJmxPdD_configurazioneSistema_showInformazioniCryptographyKeyLength() throws UtilsException {
		String tmp = this.readProperty(false, "risorseJmxPdd.configurazioneSistema.infoCryptographyKeyLength.show");
		if(tmp==null || "".equals(tmp)){
			return false;
		}
		return "true".equalsIgnoreCase(tmp.trim());
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniCryptographyKeyLength(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoCryptographyKeyLength");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniCharset(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoCharset");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniInternazionalizzazione(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoInternazionalizzazione");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteInternazionalizzazione(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoInternazionalizzazioneComplete");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniTimeZone(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoTimeZone");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteTimeZone(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoTimeZoneComplete");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniProprietaJavaNetworking(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoProprietaJavaNetworking");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteProprietaJavaNetworking(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoProprietaJavaNetworkingComplete");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniProprietaJavaAltro(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoProprietaJavaAltro");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniProprietaSistema(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoProprietaSistema");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_messageFactory(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.messageFactory");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_directoryConfigurazione(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.confDir");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_pluginProtocols(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.pluginProtocols");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniInstallazione(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoInstallazione");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_getFileTrace(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.getFileTrace");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_updateFileTrace(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.updateFileTrace");
	}
	public String getJmxPdD_configurazioneSistema_nomeRisorsaMonitoraggio(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeRisorsaMonitoraggio");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_connessioniDB(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.connessioniDB");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_connessioniJMS(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.connessioniJMS");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_idTransazioniAttive(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.transazioniID");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_idProtocolloTransazioniAttive(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.transazioniIDProtocollo");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_connessioniPD(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.connessioniPD");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_connessioniPA(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.connessioniPA");
	}
	public String getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeRisorsaConfigurazionePdD");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_severitaDiagnostici(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.severitaDiagnostici");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_severitaDiagnosticiLog4j(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.severitaDiagnosticiLog4j");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_tracciamento(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.tracciamento");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_dumpPD(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.dumpBinarioPD");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_dumpPA(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.dumpBinarioPA");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_log4jDiagnostica(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.log4jDiagnostica");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_log4jOpenspcoop(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.log4jOpenspcoop");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_log4jIntegrationManager(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.log4jIntegrationManager");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_log4jTracciamento(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.log4jTracciamento");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_log4jDump(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.log4jDump");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_transactionErrorStatusCode(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.transactionErrorStatusCode");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_transactionErrorInstanceId(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.transactionErrorInstanceId");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeBadResponse(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.transactionSpecificErrorTypeBadResponse");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalResponseError(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.transactionSpecificErrorTypeInternalResponseError");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalRequestError(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.transactionSpecificErrorTypeInternalRequestError");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalError(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.transactionSpecificErrorTypeInternalError");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorDetails(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.transactionSpecificErrorDetails");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_transactionErrorUseStatusCodeAsFaultCode(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.transactionErrorUseStatusCodeAsFaultCode");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_transactionErrorGenerateHttpHeaderGovWayCode(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.transactionErrorGenerateHttpHeaderGovWayCode");
	}	
	public String getJmxPdD_configurazioneSistema_nomeAttributo_timerConsegnaContenutiApplicativi(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerConsegnaContenutiApplicativi");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_timerEventi(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerEventi");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_timerFileSystemRecovery(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerFileSystemRecovery");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_timerGestoreBusteOnewayNonRiscontrate(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerGestoreBusteOnewayNonRiscontrate");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_timerGestoreBusteAsincroneNonRiscontrate(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerGestoreBusteAsincroneNonRiscontrate");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_timerGestoreMessaggiPuliziaMessaggiEliminati(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerGestoreMessaggiPuliziaMessaggiEliminati");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_timerGestoreMessaggiPuliziaMessaggiScaduti(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerGestoreMessaggiPuliziaMessaggiScaduti");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_timerGestoreMessaggiPuliziaMessaggiNonGestiti(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerGestoreMessaggiPuliziaMessaggiNonGestiti");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_timerGestoreMessaggiPuliziaCorrelazioneApplicativa(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerGestoreMessaggiPuliziaCorrelazioneApplicativa");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_timerGestoreMessaggiVerificaConnessioniAttive(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerGestoreMessaggiVerificaConnessioniAttive");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_timerGestorePuliziaMessaggiAnomali(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerGestorePuliziaMessaggiAnomali");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_timerGestoreRepositoryBuste(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerGestoreRepositoryBuste");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_timerMonitoraggioRisorseThread(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerMonitoraggioRisorseThread");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_timerRepositoryStatefulThread(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerRepositoryStatefulThread");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_timerStatisticheOrarie(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerStatisticheOrarie");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_timerStatisticheGiornaliere(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerStatisticheGiornaliere");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_timerStatisticheSettimanali(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerStatisticheSettimanali");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_timerStatisticheMensili(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerStatisticheMensili");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_timerThresholdThread(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerThresholdThread");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_checkConnettoreById(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkConnettoreById");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_getCertificatiConnettoreById(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.getCertificatiConnettoreById");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_checkConnettoreTokenPolicyValidazione(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkConnettoreTokenPolicyValidazione");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_checkConnettoreTokenPolicyNegoziazione(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkConnettoreTokenPolicyNegoziazione");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_checkConnettoreAttributeAuthority(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkConnettoreAttributeAuthority");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_getCertificatiConnettoreTokenPolicyValidazione(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.getCertificatiConnettoreTokenPolicyValidazione");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_getCertificatiConnettoreTokenPolicyNegoziazione(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.getCertificatiConnettoreTokenPolicyNegoziazione");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_getCertificatiConnettoreAttributeAuthority(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.getCertificatiConnettoreAttributeAuthority");
	}	
	public String getJmxPdD_configurazioneSistema_nomeMetodo_enablePortaDelegata(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.enablePortaDelegata");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_disablePortaDelegata(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.disablePortaDelegata");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_enablePortaApplicativa(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.enablePortaApplicativa");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_disablePortaApplicativa(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.disablePortaApplicativa");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_enableConnettoreMultiplo(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.enableConnettoreMultiplo");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_disableConnettoreMultiplo(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.disableConnettoreMultiplo");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_enableSchedulingConnettoreMultiplo(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.enableSchedulingConnettoreMultiplo");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_disableSchedulingConnettoreMultiplo(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.disableSchedulingConnettoreMultiplo");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_enableSchedulingConnettoreMultiploRuntimeRepository(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.enableSchedulingConnettoreMultiploRuntimeRepository");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_disableSchedulingConnettoreMultiploRuntimeRepository(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.disableSchedulingConnettoreMultiploRuntimeRepository");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_ripulisciRiferimentiCacheAccordoCooperazione(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.ripulisciRiferimentiCacheAccordoCooperazione");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_ripulisciRiferimentiCacheApi(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.ripulisciRiferimentiCacheApi");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_ripulisciRiferimentiCacheErogazione(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.ripulisciRiferimentiCacheErogazione");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_ripulisciRiferimentiCacheFruizione(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.ripulisciRiferimentiCacheFruizione");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_ripulisciRiferimentiCacheSoggetto(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.ripulisciRiferimentiCacheSoggetto");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_ripulisciRiferimentiCacheApplicativo(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.ripulisciRiferimentiCacheApplicativo");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_ripulisciRiferimentiCacheRuolo(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.ripulisciRiferimentiCacheRuolo");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_ripulisciRiferimentiCacheScope(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.ripulisciRiferimentiCacheScope");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_ripulisciRiferimentiCacheTokenPolicyValidazione(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.ripulisciRiferimentiCacheTokenPolicyValidazione");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_ripulisciRiferimentiCacheTokenPolicyNegoziazione(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.ripulisciRiferimentiCacheTokenPolicyNegoziazione");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_ripulisciRiferimentiCacheAttributeAuthority(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.ripulisciRiferimentiCacheAttributeAuthority");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatoApplicativoById(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatoApplicativoById");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatoModIApplicativoById(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatoModIApplicativoById");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatiConnettoreHttpsById(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatiConnettoreHttpsById");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatiJvm(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatiJvm");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatiConnettoreHttpsTokenPolicyValidazione(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatiConnettoreHttpsTokenPolicyValidazione");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatiValidazioneJwtTokenPolicyValidazione(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatiValidazioneJwtTokenPolicyValidazione");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatiForwardToJwtTokenPolicyValidazione(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatiForwardToJwtTokenPolicyValidazione");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatiConnettoreHttpsTokenPolicyNegoziazione(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatiConnettoreHttpsTokenPolicyNegoziazione");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatiSignedJwtTokenPolicyNegoziazione(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatiSignedJwtTokenPolicyNegoziazione");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatiConnettoreHttpsAttributeAuthority(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatiConnettoreHttpsAttributeAuthority");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatiAttributeAuthorityJwtRichiesta(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatiAttributeAuthorityJwtRichiesta");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatiAttributeAuthorityJwtRisposta(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatiAttributeAuthorityJwtRisposta");
	}
	public String getJmxPdD_configurazioneSistema_nomeRisorsaAccessoRegistroServizi(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeRisorsaAccessoRegistroServizi");
	}
	public String getJmxPdD_configurazioneSistema_nomeRisorsaStatoServiziPdD(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeRisorsaStatoServiziPdD");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaDelegata(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.statoServizioPortaDelegata");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaDelegataAbilitazioniPuntuali(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.statoServizioPortaDelegataAbilitazioniPuntuali");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaDelegataDisabilitazioniPuntuali(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.statoServizioPortaDelegataDisabilitazioniPuntuali");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaApplicativa(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.statoServizioPortaApplicativa");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaApplicativaAbilitazioniPuntuali(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.statoServizioPortaApplicativaAbilitazioniPuntuali");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaApplicativaDisabilitazioniPuntuali(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.statoServizioPortaApplicativaDisabilitazioniPuntuali");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioIntegrationManager(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.statoServizioIntegrationManager");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioPortaDelegata(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.abilitaServizioPortaDelegata");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioPortaDelegata(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.disabilitaServizioPortaDelegata");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioPortaApplicativa(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.abilitaServizioPortaApplicativa");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioPortaApplicativa(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.disabilitaServizioPortaApplicativa");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioIntegrationManager(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.abilitaServizioIntegrationManager");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioIntegrationManager(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.disabilitaServizioIntegrationManager");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatoSoggettoById(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatoSoggettoById");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatiConnettoreHttpsByIdRegistro(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatiConnettoreHttpsByIdRegistro");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatiModIErogazioneById(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatiModIErogazioneById");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_checkCertificatiModIFruizioneById(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatiModIFruizioneById");
	}
	public String getJmxPdD_configurazioneSistema_nomeRisorsaDatasourceGW(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeRisorsaDatasourceGW");
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_numeroDatasourceGW(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.numeroDatasourceGW");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_getDatasourcesGW(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.getDatasourcesGW");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_getUsedConnectionsDatasourcesGW(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.getUsedConnectionsDatasourcesGW");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_getInformazioniDatabaseDatasourcesGW(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.getInformazioniDatabaseDatasourcesGW");
	}
	public String getJmxPdD_configurazioneSistema_nomeRisorsaGestioneConsegnaApplicativi(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeRisorsaGestioneConsegnaApplicativi");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_getThreadPoolStatus(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.getThreadPoolStatus");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_getQueueConfig(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.getQueueConfig");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_getApplicativiPrioritari(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.getApplicativiPrioritari");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_getConnettoriPrioritari(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.getConnettoriPrioritari");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_updateConnettoriPrioritari(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.updateConnettoriPrioritari");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_resetConnettoriPrioritari(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.resetConnettoriPrioritari");
	}
	public String getJmxPdD_configurazioneSistema_nomeRisorsaSystemPropertiesPdD(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeRisorsaSystemPropertiesPdD");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_refreshPersistentConfiguration(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.refreshPersistentConfiguration");
	}
	public String getJmxPdD_configurazioneSistema_nomeRisorsaDatiRichieste(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeRisorsaDatiRichieste");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_removeRateLimitingGlobalConfigCache(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.removeRateLimitingGlobalConfigCache");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_removeRateLimitingAPIConfigCache(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.removeRateLimitingAPIConfigCache");
	}
	public List<String> getJmxPdD_caches(String alias) throws UtilsException {
		return this.read_jmx_caches(alias, "risorseJmxPdd.caches");
	}
	public List<String> getJmxPdD_caches_prefill(String alias) throws UtilsException {
		return this.read_jmx_caches(alias, "risorseJmxPdd.caches.prefill");
	}
	private List<String> read_jmx_caches(String alias,String property) throws UtilsException {
		List<String> list = new ArrayList<String>();
		String tipo = _getJmxPdD_value(false, alias, property);
		if(tipo!=null && !"".equals(tipo)){
			String [] tmp = tipo.split(",");
			for (int i = 0; i < tmp.length; i++) {
				list.add(tmp[i].trim());
			}
		}
		return list;
	}
	public String getJmxPdD_cache_type(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.cache.tipo");
	}
	public String getJmxPdD_cache_nomeAttributo_cacheAbilitata(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.cache.nomeAttributo.cacheAbilitata");
	}
	public String getJmxPdD_cache_nomeMetodo_statoCache(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.cache.nomeMetodo.statoCache");
	}
	public String getJmxPdD_cache_nomeMetodo_resetCache(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.cache.nomeMetodo.resetCache");
	}
	public String getJmxPdD_cache_nomeMetodo_prefillCache(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.cache.nomeMetodo.prefillCache");
	}
	
	
	/* ----- Opzioni di Visualizzazione ----- */
	
	public Boolean isShowJ2eeOptions() throws UtilsException{
		String tmp = this.readProperty(true, "server.tipo");
		if("web".equals(tmp)){
			return false;
		}
		else{
			return true;
		}
	}
	
	public Boolean isConsoleConfigurazioniPersonalizzate() throws UtilsException{
		return this.readBooleanRequiredProperty("console.configurazioniPersonalizzate");
	}
	
	public Boolean isConsoleGestioneSoggettiVirtuali() throws UtilsException{
		return this.readBooleanRequiredProperty("console.gestioneSoggettiVirtuali");
	}
	
	public Boolean isConsoleGestioneSoggettiRouter() throws UtilsException{
		return this.readBooleanRequiredProperty("console.gestioneSoggettiRouter");
	}
	
	public Boolean isConsoleGestioneWorkflowStatoDocumenti() throws UtilsException{
		return this.readBooleanRequiredProperty("console.gestioneWorkflowStatoDocumenti");
	}
	
	public Boolean isConsoleGestioneWorkflowStatoDocumenti_visualizzaStatoLista() throws UtilsException{
		return this.readBooleanRequiredProperty("console.gestioneWorkflowStatoDocumenti.visualizzaStatoLista");
	}
	
	public Boolean isConsoleGestioneWorkflowStatoDocumenti_ripristinoStatoOperativoDaFinale() throws UtilsException{
		return this.readBooleanRequiredProperty("console.gestioneWorkflowStatoDocumenti.finale.ripristinoStatoOperativo");
	}
	
	public Boolean isConsoleInterfacceAPI_visualizza() throws UtilsException{
		return this.readBooleanRequiredProperty("console.interfacceAPI.visualizza");
	}
	
	public Boolean isConsoleAllegati_visualizza() throws UtilsException{
		return this.readBooleanRequiredProperty("console.allegati.visualizza");
	}
	
	public Boolean isEnableAutoMappingWsdlIntoAccordo() throws UtilsException{
		return this.readBooleanRequiredProperty("console.gestioneWsdl.autoMappingInAccordo");
	}
	
	public Boolean isEnableAutoMappingWsdlIntoAccordo_estrazioneSchemiInWsdlTypes() throws UtilsException{
		return this.readBooleanRequiredProperty("console.gestioneWsdl.autoMappingInAccordo.estrazioneSchemiInWsdlTypes");
	}
	
	public Boolean isMenuVisualizzaFlagPrivato() throws UtilsException{
		return this.readBooleanRequiredProperty("menu.visualizzaFlagPrivato");
	}
	
	public Boolean isMenuVisualizzaListaCompletaConnettori() throws UtilsException{
		return this.readBooleanRequiredProperty("menu.visualizzaListaCompletaConnettori");
	}
	
	public Boolean isMenuVisualizzaOpzioneDebugConnettore() throws UtilsException{
		return this.readBooleanRequiredProperty("menu.visualizzaOpzioneDebugConnettore");
	}
	
	public Boolean isMenuAccordiVisualizzaCorrelazioneAsincrona() throws UtilsException{
		return this.readBooleanRequiredProperty("menu.accordi.visualizzaCorrelazioneAsincrona");
	}
	
	public Boolean isMenuAccordiVisualizzazioneGestioneInformazioniProtocollo() throws UtilsException{
		return this.readBooleanRequiredProperty("menu.accordi.visualizzazioneGestioneInformazioniProtocollo");
	}
	
	public Boolean isMenuMTOMVisualizzazioneCompleta() throws UtilsException{
		return this.readBooleanRequiredProperty("menu.mtom.visualizzazioneCompleta");
	}
	
	public Integer getPortaCorrelazioneApplicativaMaxLength() throws UtilsException{
		return this.readIntegerProperty(true, "menu.porte.correlazioneApplicativa.maxLength");
	}
	
	public Boolean isMenuPortaDelegataLocalForward() throws UtilsException{
		return this.readBooleanRequiredProperty("menu.porte.localForward");
	}
	
	public boolean isProprietaErogazioni_showModalitaStandard() throws UtilsException{
		return this.readBooleanRequiredProperty("menu.proprietaErogazioni.showModalitaStandard");
	}
	
	public boolean isProprietaFruizioni_showModalitaStandard() throws UtilsException{
		return this.readBooleanRequiredProperty("menu.proprietaFruizioni.showModalitaStandard");
	}
	
	public boolean isPortTypeObbligatorioImplementazioniSOAP() throws UtilsException{
		return this.readBooleanRequiredProperty("menu.servizi.portTypeObbligatorio");
	}
	
	public Boolean isVisualizzazioneConfigurazioneDiagnosticaLog4J() throws UtilsException{
		String p = "menu.configurazione.visualizzazioneDiagnostica.standard";
		String tmp = this.readProperty(false, p);
		if(tmp==null){
			return true; // standard per default
		}
		if("true".equalsIgnoreCase(tmp)==false && "false".equalsIgnoreCase(tmp)==false){
			throw new UtilsException("Property ["+p+"] with uncorrect value ["+tmp+"] (true/value expected)");
		}
		return Boolean.parseBoolean(tmp);
	}
	
	public Boolean isElenchiVisualizzaCountElementi() throws UtilsException{
		return this.readBooleanRequiredProperty("elenchi.visualizzaCountElementi");
	}
	
	public boolean isElenchiAbilitaResetCacheSingoloElemento() throws UtilsException{
		return this.readBooleanRequiredProperty("elenchi.risultati.abilitaResetCacheSingoloElemento");
	}
	
	public Boolean isElenchiRicercaConservaCriteri() throws UtilsException{
		return this.readBooleanRequiredProperty("elenchi.ricerca.conservaCriteri");
	}
	
	public Boolean isElenchiAccordiVisualizzaColonnaAzioni() throws UtilsException{
		return this.readBooleanRequiredProperty("elenchi.accordi.visualizzaColonnaAzioni");
	}
	
	public Boolean isElenchiSA_asincroniNonSupportati_VisualizzaRispostaAsincrona() throws UtilsException{
		return this.readBooleanRequiredProperty("elenchi.serviziApplicativi.asincroniNonSupportati.visualizzazioneRispostaAsincrona");
	}
	
	public Boolean isElenchiMenuVisualizzazionePulsantiImportExportPackage() throws UtilsException{
		return this.readBooleanRequiredProperty("elenchi_menu.visualizzazionePulsantiImportExportPackage");
	}
	
	public Integer getElenchiMenuIdentificativiLunghezzaMassima() throws UtilsException{
		return this.readIntegerProperty(true, "elenchi_menu.identificativi.lunghezzaMassima");
	}
	
	public String getTokenPolicyForceId() throws UtilsException{
		return this.readProperty(false, "console.tokenPolicy.forceId");
	}
	
	public Properties getTokenPolicyTipologia() throws UtilsException{
		return this.reader.readProperties_convertEnvProperties("console.tokenPolicy.mapping.");
	}
	
	public String getAttributeAuthorityForceId() throws UtilsException{
		return this.readProperty(false, "console.attributeAuthority.forceId");
	}
	
	public Properties getAttributeAuthorityTipologia() throws UtilsException{
		return this.reader.readProperties_convertEnvProperties("console.attributeAuthority.mapping.");
	}
	
	public Boolean isEnableServiziVisualizzaModalitaElenco() throws UtilsException{
		return this.readBooleanRequiredProperty("console.servizi.visualizzaModalitaElenco");
	}
	
	public Integer getNumeroMassimoSoggettiOperativiMenuUtente() throws UtilsException{
		return this.readIntegerProperty(true, "console.selectListSoggettiOperativi.numeroMassimoSoggettiVisualizzati");
	}
	
	public Integer getLunghezzaMassimaLabelSoggettiOperativiMenuUtente() throws Exception{
		return this.readIntegerProperty(true, "console.selectListSoggettiOperativi.lunghezzaMassimaLabel");
	}
	
	public Integer getLunghezzaMassimaInformazioneView() throws Exception{
		return this.readIntegerProperty(true, "console.view.lunghezzaMassimaInformazione");
	}
	
	public boolean isSetSearchAfterAdd() throws UtilsException{
		return this.readBooleanRequiredProperty("console.setSearchAfterAdd");
	}
	
	/* ----- Gestione vulnerabilita' console ------- */
	public Integer getValiditaTokenCsrf() throws Exception{
		return this.readIntegerProperty(true, "console.csrf.token.validita");
	}
	
	public String getCSPHeaderValue() throws UtilsException{
		return this.readProperty(true, "console.csp.header.value");
	}
	
	/* ---------------- Gestione govwayConsole centralizzata ----------------------- */

	public Boolean isGestioneCentralizzata_SincronizzazionePdd() throws UtilsException{
		return this.readBooleanRequiredProperty("sincronizzazionePdd");
	}
	
	public Boolean isGestioneCentralizzata_SincronizzazioneRegistro() throws UtilsException{
		return this.readBooleanRequiredProperty("sincronizzazioneRegistro");
	}
	
	public Boolean isGestioneCentralizzata_SincronizzazioneGestoreEventi() throws UtilsException{
		return this.readBooleanRequiredProperty("sincronizzazioneGE");
	}
	
	public String getGestioneCentralizzata_NomeCodaSmistatore() throws UtilsException{
		return this.readProperty(true, "SmistatoreQueue");
	}
	
	public String getGestioneCentralizzata_NomeCodaRegistroServizi() throws UtilsException{
		return this.readProperty(true, "RegistroServiziQueue");
	}
	
	public String getGestioneCentralizzata_WSRegistroServizi_endpointPdd() throws UtilsException{
		return this.readProperty(true, "RegistroServiziWS.endpoint.portaDominio");
	}
	
	public String getGestioneCentralizzata_WSRegistroServizi_endpointSoggetto() throws UtilsException{
		return this.readProperty(true, "RegistroServiziWS.endpoint.soggetto");
	}
	
	public String getGestioneCentralizzata_WSRegistroServizi_endpointAccordoCooperazione() throws UtilsException{
		return this.readProperty(true, "RegistroServiziWS.endpoint.accordoCooperazione");
	}
	
	public String getGestioneCentralizzata_WSRegistroServizi_endpointAccordoServizioParteComune() throws UtilsException{
		return this.readProperty(true, "RegistroServiziWS.endpoint.accordoServizioParteComune");
	}
	
	public String getGestioneCentralizzata_WSRegistroServizi_endpointAccordoServizioParteSpecifica() throws UtilsException{
		return this.readProperty(true, "RegistroServiziWS.endpoint.accordoServizioParteSpecifica");
	}
	
	public String getGestioneCentralizzata_WSRegistroServizi_credenzialiBasic_username() throws UtilsException{
		return this.readProperty(false, "RegistroServiziWS.username");
	}
	
	public String getGestioneCentralizzata_WSRegistroServizi_credenzialiBasic_password() throws UtilsException{
		return this.readProperty(false, "RegistroServiziWS.password");
	}
	
	public String getGestioneCentralizzata_PrefissoNomeCodaConfigurazionePdd() throws UtilsException{
		return this.readProperty(true, "PdDQueuePrefix");
	}
	
	public String getGestioneCentralizzata_GestorePddd_ScriptShell_Path() throws UtilsException{
		return this.readProperty(false, "GestorePdD.script.path");
	}
	
	public String getGestioneCentralizzata_GestorePddd_ScriptShell_Args() throws UtilsException{
		return this.readProperty(false, "GestorePdD.script.args");
	}
	
	public String getGestioneCentralizzata_WSConfigurazione_endpointSuffixPortaApplicativa() throws UtilsException{
		return this.readProperty(true, "ConfigurazioneWS.endpoint.suffix.portaApplicativa");
	}
	
	public String getGestioneCentralizzata_WSConfigurazione_endpointSuffixPortaDelegata() throws UtilsException{
		return this.readProperty(true, "ConfigurazioneWS.endpoint.suffix.portaDelegata");
	}
	
	public String getGestioneCentralizzata_WSConfigurazione_endpointSuffixServizioApplicativo() throws UtilsException{
		return this.readProperty(true, "ConfigurazioneWS.endpoint.suffix.servizioApplicativo");
	}
	
	public String getGestioneCentralizzata_WSConfigurazione_endpointSuffixSoggetto() throws UtilsException{
		return this.readProperty(true, "ConfigurazioneWS.endpoint.suffix.soggetto");
	}
	
	public String getGestioneCentralizzata_WSConfigurazione_credenzialiBasic_username() throws UtilsException{
		return this.readProperty(false, "ConfigurazioneWS.username");
	}
	
	public String getGestioneCentralizzata_WSConfigurazione_credenzialiBasic_password() throws UtilsException{
		return this.readProperty(false, "ConfigurazioneWS.password");
	}
	
	public String getGestioneCentralizzata_NomeCodaGestoreEventi() throws UtilsException{
		return this.readProperty(true, "GestoreEventiQueue");
	}
	
	public String getGestioneCentralizzata_PrefissoWSGestoreEventi() throws UtilsException{
		return this.readProperty(true, "UrlWebServiceGestoreEventi");
	}
	
	public String getGestioneCentralizzata_GestoreEventiTipoSoggetto() throws UtilsException{
		return this.readProperty(true, "gestoreEventi.tipo_soggetto");
	}
	
	public String getGestioneCentralizzata_GestoreEventiNomeSoggetto() throws UtilsException{
		return this.readProperty(true, "gestoreEventi.nome_soggetto");
	}
	
	public String getGestioneCentralizzata_GestoreEventiNomeServizioApplicativo() throws UtilsException{
		return this.readProperty(true, "gestoreEventi.nome_servizio_applicativo");
	}
	
	public String getGestioneCentralizzata_WSMonitor_pddDefault() throws UtilsException{
		return this.readProperty(true, "MonitoraggioWS.pdd.default");
	}
	
	public String getGestioneCentralizzata_WSMonitor_endpointSuffixStatoPdd() throws UtilsException{
		return this.readProperty(true, "MonitoraggioWS.endpoint.suffix.statoPdd");
	}
	
	public String getGestioneCentralizzata_WSMonitor_endpointSuffixMessaggio() throws UtilsException{
		return this.readProperty(true, "MonitoraggioWS.endpoint.suffix.messaggio");
	}
	
	public String getGestioneCentralizzata_WSMonitor_credenzialiBasic_username() throws UtilsException{
		return this.readProperty(false, "MonitoraggioWS.username");
	}
	
	public String getGestioneCentralizzata_WSMonitor_credenzialiBasic_password() throws UtilsException{
		return this.readProperty(false, "MonitoraggioWS.password");
	}
	
	public String getGestioneCentralizzata_URLContextCreazioneAutomaticaSoggetto() throws UtilsException{
		return this.readProperty(true, "UrlConnettoreSoggetto");
	}
	
	public String getGestioneCentralizzata_PddIndirizzoIpPubblico() throws UtilsException{
		return this.readProperty(true, "pdd.indirizzoIP.pubblico");
	}
	
	public Integer getGestioneCentralizzata_PddPortaPubblica() throws UtilsException{
		return this.readIntegerProperty(true, "pdd.porta.pubblica");
	}
	
	public String getGestioneCentralizzata_PddIndirizzoIpGestione() throws UtilsException{
		return this.readProperty(true, "pdd.indirizzoIP.gestione");
	}
	
	public Integer getGestioneCentralizzata_PddPortaGestione() throws UtilsException{
		return this.readIntegerProperty(true, "pdd.porta.gestione");
	}
	
	
	
	/* ---------------- Gestione govwayConsole locale ----------------------- */

	public Boolean isSinglePdD_GestionePdd() throws UtilsException{
		return this.readBooleanRequiredProperty("singlePdD.pdd.enabled");
	}
	
	public Boolean isSinglePdD_RegistroServiziLocale() throws UtilsException{
		return this.readBooleanRequiredProperty("singlePdD.registroServizi.locale");
	}
	
	public Boolean isSinglePdD_TracceConfigurazioneCustomAppender() throws UtilsException{
		return this.readBooleanRequiredProperty("tracce.configurazioneCustomAppender");
	}
	
	public Boolean isSinglePdD_TracceGestioneSorgentiDatiPrelevataDaDatabase() throws UtilsException{
		return this.readBooleanRequiredProperty("tracce.sorgentiDati.database");
	}
	
	public Boolean isSinglePdD_MessaggiDiagnosticiConfigurazioneCustomAppender() throws UtilsException{
		return this.readBooleanRequiredProperty("msgDiagnostici.configurazioneCustomAppender");
	}
	
	public Boolean isSinglePdD_MessaggiDiagnosticiGestioneSorgentiDatiPrelevataDaDatabase() throws UtilsException{
		return this.readBooleanRequiredProperty("msgDiagnostici.sorgentiDati.database");
	}
	
	public Boolean isSinglePdD_DumpConfigurazioneCustomAppender() throws UtilsException{
		return this.readBooleanRequiredProperty("dump.configurazioneCustomAppender");
	}
	
	public Boolean isSinglePdD_DumpConfigurazioneRealtime() throws UtilsException{
		return this.readBooleanRequiredProperty("dump.configurazioneRealtime");
	}
	
	
	/* ---- Plugins ------ */
	
	public String getExtendedInfoDriverConfigurazione() throws UtilsException{
		return this.readProperty(false, "extendedInfo.configurazione");
	}
	public String getExtendedInfoDriverPortaDelegata() throws UtilsException{
		return this.readProperty(false, "extendedInfo.portaDelegata");
	}
	public String getExtendedInfoDriverPortaApplicativa() throws UtilsException{
		return this.readProperty(false, "extendedInfo.portaApplicativa");
	}
	
	public String[] getPlugins_Menu() throws UtilsException{
		String p = this.readProperty(false, "plugins.menu");
		if(p!=null && !"".equals(p.trim())){
			String [] tmp = p.trim().split(",");
			for (int i = 0; i < tmp.length; i++) {
				tmp[i] = tmp[i].trim();
			}
			return tmp;
		}
		return null;
	}
	
	public String[] getPlugins_Configurazione() throws UtilsException{
		String p = this.readProperty(false, "plugins.configurazione");
		if(p!=null && !"".equals(p.trim())){
			String [] tmp = p.trim().split(",");
			for (int i = 0; i < tmp.length; i++) {
				tmp[i] = tmp[i].trim();
			}
			return tmp;
		}
		return null;
	}
	
	public String getPlugins_ConfigurazioneList() throws UtilsException{
		return this.readProperty(false, "plugins.configurazione.list");
	}
	
	public String[] getPlugins_Connettore() throws UtilsException{
		String p = this.readProperty(false, "plugins.connettore");
		if(p!=null && !"".equals(p.trim())){
			String [] tmp = p.trim().split(",");
			for (int i = 0; i < tmp.length; i++) {
				tmp[i] = tmp[i].trim();
			}
			return tmp;
		}
		return null;
	}
	
	public String getPlugins_PortaDelegata() throws UtilsException{
		return this.readProperty(false, "plugins.portaDelegata");
	}
	
	public String getPlugins_PortaApplicativa() throws UtilsException{
		return this.readProperty(false, "plugins.portaApplicativa");
	}
	
	
	
	/* ---- Gestione Visibilità utenti ------ */

	public Boolean isVisibilitaOggettiGlobale() throws UtilsException{
		String tmp = this.readProperty(true, "visibilitaOggetti");
		if("locale".equals(tmp)){
			return false;
		}else{
			return true;
		}
	}
	
	public List<String> getUtentiConVisibilitaGlobale() throws UtilsException{
		String utenti = this.readProperty(false, "utentiConVisibilitaGlobale");
		List<String> lista = new ArrayList<String>();
		if(utenti!=null){
			String [] tmp = utenti.trim().split(",");
			if(tmp!=null){
				for(int i=0; i<tmp.length;i++){
					lista.add(tmp[i].trim());
				}
			}
		}
		return lista;
	}


	/* ---- Utiltiies interne ------ */
	
	public PropertiesSourceConfiguration _getSourceConfiguration(String id, 
			String propertyExternalDir, String propertyExternalDirRefresh,
			String propertyBuiltIn, String propertyBuiltInRefresh) throws UtilsException {
		
		PropertiesSourceConfiguration config = new PropertiesSourceConfiguration();
		config.setId(id);
		
		String dir = this.readProperty(false, propertyExternalDir);
		if(dir!=null) {
			File f = new File(dir);
			if(f.exists()) {
				config.setDirectory(f.getAbsolutePath());
			}
		}
		
		String dir_refresh = this.readProperty(false, propertyExternalDirRefresh);
		if(dir_refresh!=null) {
			config.setUpdate("true".equalsIgnoreCase(dir_refresh.trim()));
		}
		
		String buildInt = this.readProperty(false, propertyBuiltIn);
		if(buildInt!=null) {
			try {
				//System.out.println("BASE: "+buildInt);
				InputStream is = ConsoleProperties.class.getResourceAsStream(buildInt);
				try {
					if(is!=null) {
						byte [] zipContent = Utilities.getAsByteArray(is);
						is.close();
						is = null;
						File fTmp = File.createTempFile("propertiesSourceConfiguration", ".zip");
						//System.out.println("TMP: "+fTmp.getAbsolutePath());
						try {
							FileSystemUtilities.writeFile(fTmp, zipContent);
							ZipFile zip = new ZipFile(fTmp);
							Iterator<ZipEntry> itZip = ZipUtilities.entries(zip, true);
							List<byte[]> builtIdList = new ArrayList<>();
							while (itZip.hasNext()) {
								ZipEntry zipEntry = (ZipEntry) itZip.next();
								if(zipEntry.isDirectory() == false) {
									InputStream isZip = zip.getInputStream(zipEntry);
									try {
										//System.out.println("LEGGO ["+zipEntry.getName()+"]");
										byte [] bytes = Utilities.getAsByteArray(isZip);
										builtIdList.add(bytes);
									}finally {
										try {
											if(isZip!=null) {
												isZip.close();
											}
										}catch(Exception e) {
											// close
										}
									}
								}
							}
							config.setBuiltIn(builtIdList);
						}finally {
							fTmp.delete();
						}
					}
				}finally {
					try {
						if(is!=null) {
							is.close();
						}
					}catch(Exception e) {
						// close
					}
				}
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
		
		String builtIn_refresh = this.readProperty(false, propertyBuiltInRefresh);
		if(builtIn_refresh!=null) {
			config.setUpdate("true".equalsIgnoreCase(builtIn_refresh.trim()));
		}
		
		return config;

	}
	
	// propertiy per la gestione del console.font
	private String consoleFontName = null;
	private String consoleFontFamilyName = null;
	private int consoleFontStyle = -1;
	
	public String getConsoleFont() throws Exception{
		return this.readProperty(true,"console.font");
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
	
	// properties per la gestione del login
	public String getLoginTipo() throws Exception{
		return this.readProperty(true,"login.tipo");
	}

	public boolean isLoginApplication() throws Exception{
		return this.readBooleanRequiredProperty("login.application");
	}

	public Properties getLoginProperties() throws Exception{
		return this.reader.readProperties("login.props.");
	}
	
	public String getLoginUtenteNonAutorizzatoRedirectUrl() throws Exception{
		if(this.isLoginApplication()) {
			return "";
		}
		return this.readProperty(true,"login.utenteNonAutorizzato.redirectUrl");
	}

	public String getLoginUtenteNonValidoRedirectUrl() throws Exception{
		if(this.isLoginApplication()) {
			return "";
		}
		return this.readProperty(true,"login.utenteNonValido.redirectUrl");
	}
	
	public String getLoginErroreInternoRedirectUrl() throws Exception{
		if(this.isLoginApplication()) {
			return "";
		}
		return this.readProperty(true,"login.erroreInterno.redirectUrl");
	}

	public String getLoginSessioneScadutaRedirectUrl() throws Exception{
		if(this.isLoginApplication()) {
			return "";
		}
		return this.readProperty(true,"login.sessioneScaduta.redirectUrl");
	}

	public boolean isMostraButtonLogout() throws Exception{
		if(this.isLoginApplication()) {
			return true;
		}
		return this.readBooleanRequiredProperty("logout.mostraButton.enabled");
	}

	public String getLogoutUrlDestinazione() throws Exception{
		if(this.isLoginApplication()) {
			return "";
		}
		return this.readProperty(true,"logout.urlDestinazione");
	}
}
