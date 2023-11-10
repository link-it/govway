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
	public ConsoleProperties(String confDir, String confPropertyName, String confLocalPathPrefix,Logger log) throws UtilsException {

		if(log!=null)
			this.log = log;
		else
			this.log = LoggerWrapperFactory.getLogger(ConsoleProperties.class);
		
		/* ---- Lettura del cammino del file di configurazione ---- */
		Properties propertiesReader = new Properties();
		try (java.io.InputStream properties = ConsoleProperties.class.getResourceAsStream("/console.properties");){  
			if(properties==null){
				throw new UtilsException("File '/console.properties' not found");
			}
			propertiesReader.load(properties);
		}catch(Exception e) {
			String msg = "Riscontrato errore durante la lettura del file 'console.properties': \n\n"+e.getMessage();
			this.log.error(msg);
		    throw new UtilsException("ConsoleProperties initialize error: "+e.getMessage());
		}

		this.reader = new ConsoleInstanceProperties(propertiesReader, this.log, confDir, confPropertyName, confLocalPathPrefix);
	}

	private boolean parse(BooleanNullable b, boolean defaultValue) {
		return (b!=null && b.getValue()!=null) ? b.getValue() : defaultValue;
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

	private String getPropertyPrefix(String property) {
		return "Property ["+property+"] ";
	}
	private String getMessageUncorrectValue(String tmp) {
		return "with uncorrect value ["+tmp+"]"; 
	}
	
	private String readProperty(boolean required,String property) throws UtilsException{
		String tmp = this.reader.getValueConvertEnvProperties(property);
		if(tmp==null){
			if(required){
				throw new UtilsException(getPropertyPrefix(property)+"not found");
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
		if(!"true".equalsIgnoreCase(tmp) && !"false".equalsIgnoreCase(tmp)){
			throw new UtilsException(getPropertyPrefix(property)+getMessageUncorrectValue(tmp)+" (true/value expected)");
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
			throw new UtilsException(getPropertyPrefix(property)+getMessageUncorrectValue(tmp)+" (int value expected)");
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
			throw new UtilsException(getPropertyPrefix(property)+getMessageUncorrectValue(tmp)+" (long value expected)");
		}
	}
	

	
	/* ----- Funzionalità Generiche -------- */
	
	public String getConfDirectory() throws UtilsException{
		return this.readProperty(false, "confDirectory");
	}
	
	public String getProtocolloDefault() throws UtilsException{
		return this.readProperty(false, "protocolloDefault");
	}
	
	public long getGestioneSerializableDBattesaAttiva() throws UtilsException {	
		return this.readLongProperty(false, "jdbc.serializable.attesaAttiva");
	}
	
	public int getGestioneSerializableDBcheckInterval() throws UtilsException {	
		return this.readIntegerProperty(false, "jdbc.serializable.check");
	}
	
	public Boolean isSinglePdD() throws UtilsException{
		return this.readBooleanRequiredProperty("singlePdD");
	}
	
	public Boolean isTokenGenerazioneAutomaticaPorteDelegateEnabled() throws UtilsException{
		return this.readBooleanRequiredProperty("generazioneAutomaticaPorteDelegate.token.enabled");
	}
	
	public Boolean isAutenticazioneGenerazioneAutomaticaPorteDelegateEnabled() throws UtilsException{
		return this.readBooleanRequiredProperty("generazioneAutomaticaPorteDelegate.autenticazione.enabled");
	}
	public String getAutenticazioneGenerazioneAutomaticaPorteDelegate() throws UtilsException{
		return this.readProperty(true, "generazioneAutomaticaPorteDelegate.autenticazione");
	}
	
	public Boolean isAutorizzazioneGenerazioneAutomaticaPorteDelegateEnabled() throws UtilsException{
		return this.readBooleanRequiredProperty("generazioneAutomaticaPorteDelegate.autorizzazione.enabled");
	}
	public String getAutorizzazioneGenerazioneAutomaticaPorteDelegate() throws UtilsException{
		return this.readProperty(true, "generazioneAutomaticaPorteDelegate.autorizzazione");
	}
	
	public Boolean isTokenGenerazioneAutomaticaPorteApplicativeEnabled() throws UtilsException{
		return this.readBooleanRequiredProperty("generazioneAutomaticaPorteApplicative.token.enabled");
	}
	
	public Boolean isAutenticazioneGenerazioneAutomaticaPorteApplicativeEnabled() throws UtilsException{
		return this.readBooleanRequiredProperty("generazioneAutomaticaPorteApplicative.autenticazione.enabled");
	}
	public String getAutenticazioneGenerazioneAutomaticaPorteApplicative() throws UtilsException{
		return this.readProperty(true, "generazioneAutomaticaPorteApplicative.autenticazione");
	}
	
	public Boolean isAutorizzazioneGenerazioneAutomaticaPorteApplicativeEnabled() throws UtilsException{
		return this.readBooleanRequiredProperty("generazioneAutomaticaPorteApplicative.autorizzazione.enabled");
	}
	public String getAutorizzazioneGenerazioneAutomaticaPorteApplicative() throws UtilsException{
		return this.readProperty(true, "generazioneAutomaticaPorteApplicative.autorizzazione");
	}
	
	public Boolean isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto() throws UtilsException{
		return this.readBooleanRequiredProperty("accordi.portType.implementazioneUnicaPerSoggetto");
	}
	
	public Boolean isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto() throws UtilsException{
		return this.readBooleanRequiredProperty("accordi.implementazioneUnicaPerSoggetto");
	}
	
	public String getImportArchiveTipoPdD() throws UtilsException{
		return this.readProperty(true, "importArchive.tipoPdD");
	}
	
	public boolean isExportArchiveConfigurazioneSoloDumpCompleto() throws UtilsException{
		return this.readBooleanRequiredProperty("exportArchive.configurazione.soloDumpCompleto");
	}
	
	public boolean isExportArchiveServiziStandard() throws UtilsException{
		return this.readBooleanRequiredProperty("exportArchive.servizi.standard");
	}
	
	public boolean isGestoreConsistenzaDatiEnabled() throws UtilsException{
		return this.readBooleanRequiredProperty("gestoreConsistenzaDati");
	}
	
	public boolean isGestoreConsistenzaDatiForceCheckMapping() throws UtilsException{
		return this.readBooleanRequiredProperty("gestoreConsistenzaDati.forceCheckMapping");
	}
		
	public PropertiesSourceConfiguration getMessageSecurityPropertiesSourceConfiguration() throws UtilsException {
		return getSourceConfigurationEngine("messageSecurity", 
				"messageSecurity.dir", "messageSecurity.dir.refresh", 
				"messageSecurity.builtIn", "messageSecurity.builtIn.refresh");
	}
	
	public PropertiesSourceConfiguration getPolicyGestioneTokenPropertiesSourceConfiguration() throws UtilsException {
		return getSourceConfigurationEngine("policyGestioneToken", 
				"policyGestioneToken.dir", "policyGestioneToken.dir.refresh", 
				"policyGestioneToken.builtIn", "policyGestioneToken.builtIn.refresh");
	}
	
	public boolean isPolicyGestioneTokenVerificaCertificati() throws UtilsException{
		return this.readBooleanRequiredProperty("policyGestioneToken.verificaCertificati");
	}
	
	public List<String> getPolicyGestioneTokenPDND() throws UtilsException{
		List<String> l = new ArrayList<>();
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
		return getSourceConfigurationEngine("attributeAuthority", 
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
		List<PolicyGroupByActiveThreadsType> l = new ArrayList<>();
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
	public boolean isSoggettiVerificaCertificatiCheckCertificatoSoggettoByIdUseApi() throws UtilsException{
		return this.readBooleanRequiredProperty("soggetti.verificaCertificati.checkCertificatoSoggettoById.useApi");
	}
	
	public boolean isApplicativiVerificaCertificati() throws UtilsException{
		return this.readBooleanRequiredProperty("applicativi.verificaCertificati");
	}
	public boolean isApplicativiVerificaCertificatiCheckCertificatoApplicativoByIdUseApi() throws UtilsException{
		return this.readBooleanRequiredProperty("applicativi.verificaCertificati.checkCertificatoApplicativoById.useApi");
	}
	
	public boolean isApiResourcePathValidatorEnabled() throws UtilsException{
		return this.readBooleanRequiredProperty("api.resource.pathValidator");
	}
	
	public boolean isApiResourceHttpMethodAndPathQualsiasiEnabled() throws UtilsException{
		return this.readBooleanRequiredProperty("api.resource.httpMethodAndPathQualsiasi.enabled");
	}
	
	public List<String> getApiResourcePathQualsiasiSpecialChar() throws UtilsException{
		List<String> l = new ArrayList<>();
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
	
	public boolean isApiDescriptionTruncate255() throws UtilsException{
		return this.readBooleanRequiredProperty("api.description.truncate255");
	}
	
	public Properties getApiYamlSnakeLimits() throws UtilsException{

		String pName = "api.yaml.snakeLimits";
		
		try{  
			Properties pNull = null;
			
			String file = this.readProperty(false, pName);
			if(file!=null && StringUtils.isNotEmpty(file)) {
				File f = new File(file);
				if(f.exists()) {
					if(!f.isFile()) {
						throw new UtilsException("Il file indicato '"+f.getAbsolutePath()+"' non è un file");
					}
					if(!f.canRead()) {
						throw new UtilsException("Il file indicato '"+f.getAbsolutePath()+"' non è accessibile in lettura");
					}
					try(InputStream is = new FileInputStream(f)){
						Properties p = new Properties();
						p.load(is);
						if (!p.isEmpty()){
							return p;
						}
					}
				}
			}
		
			return pNull;
			
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
		List<String> l = new ArrayList<>();
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
		List<String> l = new ArrayList<>();
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
	
	public ConfigurazionePriorita getConsegnaNotificaConfigurazionePriorita(String nome) throws UtilsException{
		Properties p = this.reader.readPropertiesConvertEnvProperties("consegnaNotifiche.priorita."+nome+".");
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
	
	public Integer getPluginsSeconds() throws UtilsException{
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
	
	public String getAllarmiConfigurazione() throws UtilsException{
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
	
	public Boolean isRegistrazioneMessaggiMultipartPayloadParsingEnabled() throws UtilsException{
		return this.readBooleanRequiredProperty("registrazioneMessaggi.multipartPayloadParsing.enabled");
	}
	
	public Boolean isClusterDinamicoEnabled() throws UtilsException{
		ConfigurazioneNodiRuntime config = getConfigurazioneNodiRuntime();
		if(config!=null) {
			return config.isClusterDinamico();
		}
		else {
			/**return getBackwardCompatibilityConfigurazioneNodiRuntime().isClusterDinamico();*/
			// abbiamo cambiato il nome della proprietà nella gestione 'ConfigurazioneNodiRuntime'
			return this.readBooleanRequiredProperty("cluster_dinamico.enabled");
		}
	}

	public String getHSMConfigurazione() throws UtilsException{
		return this.readProperty(false, "hsm.config");
	}
	public boolean isHSMRequired() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "hsm.required");
		return parse(b, false);
	}
	public boolean isHSMKeyPasswordConfigurable() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "hsm.keyPassword");
		return parse(b, false);
	}
	
	public Integer getVerificaCertificatiWarningExpirationDays() throws UtilsException{
		String cacheV = this.readProperty(true, "verificaCertificati.warning.expirationDays");
		if(cacheV!=null && StringUtils.isNotEmpty(cacheV)) {
			Integer i = Integer.valueOf(cacheV);
			if(i.intValue()>0) {
				return i;
			}
		}
		return 10;
	}
	
	public String getOCSPConfigurazione() throws UtilsException{
		return this.readProperty(false, "ocsp.config");
	}
	public boolean isOCSPRequired() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "ocsp.required");
		return parse(b, false);
	}
	public boolean isOCSPLoadDefault() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "ocsp.loadDefault");
		return parse(b, true);
	}
	public boolean isOCSPPolicyChoiceConnettoreHTTPSVerificaServerDisabilitata() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "ocsp.https.verificaServerDisabilitata.policyChoice");
		return parse(b, false);
	}
	
	public boolean isVerificaCertificatiSceltaClusterId() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "verificaCertificati.sceltaClusterId");
		return parse(b, true);
	}
	
	public boolean isClusterAsyncUpdate() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "cluster.asyncUpdate");
		return parse(b, true);
	}
	public int getClusterAsyncUpdateCheckInterval() throws UtilsException{
		String cacheV = this.readProperty(true, "cluster.asyncUpdate.checkInterval");
		if(cacheV!=null && StringUtils.isNotEmpty(cacheV)) {
			Integer i = Integer.valueOf(cacheV);
			if(i.intValue()>0) {
				return i.intValue();
			}
		}
		return 60;
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
	
	public String getLogoHeaderImage() throws UtilsException{
		return this.readProperty(false,"console.header.logo.image");
	}

	public String getLogoHeaderTitolo() throws UtilsException{
		return this.readProperty(false,"console.header.logo.titolo");
	}

	public String getLogoHeaderLink() throws UtilsException{
		return this.readProperty(false,"console.header.logo.link");
	}
	
	public boolean isVisualizzaLinkHomeHeader() throws UtilsException{
		return this.readBooleanRequiredProperty("console.header.home.link.enabled");
	}
	
	/* ----- Opzioni Accesso JMX della PdD ------- */
	
	public boolean isVisualizzaLinkClearAllCachesRemoteCheckCacheStatus() throws UtilsException{
		return this.readBooleanRequiredProperty("risorseJmxPdd.linkClearAllCaches.remoteCheckCacheStatus");
	}
	
	public String getJmxPdDExternalConfiguration() throws UtilsException{
		return this.readProperty(false, "risorseJmxPdd.configurazioneNodiRun");
	}
	
	public String getJmxPdDBackwardCompatibilityPrefix() {
		return "risorseJmxPdd.";
	}
	
	public Properties getJmxPdDBackwardCompatibilityProperties() throws UtilsException{
		
		String prefix = getJmxPdDBackwardCompatibilityPrefix();
		
		Properties p = new Properties();
		Enumeration<?> en = this.reader.propertyNames();
		while (en.hasMoreElements()) {
			Object object = en.nextElement();
			if(object instanceof String) {
				String key = (String) object;
				if(key.contains(prefix)) {
					String newKey = key.replace(prefix, "");
					p.put(newKey, this.reader.getValueConvertEnvProperties(key));
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
	private ConfigurazioneNodiRuntime getConfigurazioneNodiRuntimeEngine() {
		if(backwardCompatibilityConfigurazioneNodiRuntime==null) {
			initConfigurazioneNodiRuntime(getJmxPdDBackwardCompatibilityPrefix());
		}
		return externalConfigurazioneNodiRuntime;
	}
	private ConfigurazioneNodiRuntime getBackwardCompatibilityConfigurazioneNodiRuntimeEngine() {
		if(backwardCompatibilityConfigurazioneNodiRuntime==null) {
			initConfigurazioneNodiRuntime(getJmxPdDBackwardCompatibilityPrefix());
		}
		return backwardCompatibilityConfigurazioneNodiRuntime;
	}
	public ConfigurazioneNodiRuntime getConfigurazioneNodiRuntime() {
		ConfigurazioneNodiRuntime config = getConfigurazioneNodiRuntimeEngine();
		if(config==null) {
			config = getBackwardCompatibilityConfigurazioneNodiRuntimeEngine();
		}
		return config;
	}
	
	public List<String> getJmxPdDAliases() {
		
		List<String> lReturn = null;
		
		ConfigurazioneNodiRuntime config = getConfigurazioneNodiRuntime();
		if(config!=null) {
			return config.getAliases();
		}
		else {
			return lReturn;
		}
	}
	
	public Map<String,List<String>> getJmxPdDGruppiAliases() {
		
		Map<String,List<String>> mapReturn = null;
		
		ConfigurazioneNodiRuntime config = getConfigurazioneNodiRuntime();
		if(config!=null) {
			return config.getGruppi_aliases();
		}
		else {
			return mapReturn;
		}
	}
	
	public String getJmxPdDDescrizione(String alias) throws UtilsException {
		ConfigurazioneNodiRuntime config = getConfigurazioneNodiRuntime();
		if(config!=null) {
			return config.getDescrizione(alias);
		}
		else {
			return null;
		}
	}
	
	private String getJmxPdDValueEngine(boolean required, String alias, String prop) throws UtilsException{
		String tmp = this.readProperty(false, alias+"."+prop);
		if(tmp==null || "".equals(tmp)){
			tmp = this.readProperty(required, prop);
		}
		return tmp;
	}
	
	public String getJmxPdDDominio(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.dominio");
	}
	public String getJmxPdDConfigurazioneSistemaType(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.tipo");
	}
	public String getJmxPdDConfigurazioneSistemaNomeRisorsa(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeRisorsa");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoVersionePdD(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.versionePdD");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoVersioneBaseDati(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.versioneBaseDati");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoVersioneJava(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.versioneJava");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoVendorJava(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.vendorJava");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoTipoDatabase(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.tipoDatabase");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniDatabase(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoDatabase");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniSSL(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoSSL");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteSSL(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoSSLComplete");
	}
	public boolean isJmxPdDConfigurazioneSistemaShowInformazioniCryptographyKeyLength() throws UtilsException {
		String tmp = this.readProperty(false, "risorseJmxPdd.configurazioneSistema.infoCryptographyKeyLength.show");
		if(tmp==null || "".equals(tmp)){
			return false;
		}
		return "true".equalsIgnoreCase(tmp.trim());
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniCryptographyKeyLength(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoCryptographyKeyLength");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniCharset(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoCharset");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniInternazionalizzazione(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoInternazionalizzazione");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteInternazionalizzazione(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoInternazionalizzazioneComplete");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniTimeZone(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoTimeZone");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteTimeZone(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoTimeZoneComplete");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniProprietaJavaNetworking(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoProprietaJavaNetworking");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteProprietaJavaNetworking(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoProprietaJavaNetworkingComplete");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniProprietaJavaAltro(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoProprietaJavaAltro");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniProprietaSistema(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoProprietaSistema");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoMessageFactory(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.messageFactory");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoDirectoryConfigurazione(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.confDir");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoPluginProtocols(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.pluginProtocols");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniInstallazione(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoInstallazione");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoGetFileTrace(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.getFileTrace");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoUpdateFileTrace(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.updateFileTrace");
	}
	public String getJmxPdDConfigurazioneSistemaNomeRisorsaMonitoraggio(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeRisorsaMonitoraggio");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoConnessioniDB(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.connessioniDB");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoConnessioniJMS(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.connessioniJMS");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoIdTransazioniAttive(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.transazioniID");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoIdProtocolloTransazioniAttive(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.transazioniIDProtocollo");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoConnessioniPD(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.connessioniPD");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoConnessioniPA(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.connessioniPA");
	}
	public String getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeRisorsaConfigurazionePdD");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoSeveritaDiagnostici(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.severitaDiagnostici");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoSeveritaDiagnosticiLog4j(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.severitaDiagnosticiLog4j");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTracciamento(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.tracciamento");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoDumpPD(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.dumpBinarioPD");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoDumpPA(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.dumpBinarioPA");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoLog4jDiagnostica(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.log4jDiagnostica");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoLog4jOpenspcoop(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.log4jOpenspcoop");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoLog4jIntegrationManager(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.log4jIntegrationManager");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoLog4jTracciamento(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.log4jTracciamento");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoLog4jDump(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.log4jDump");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorStatusCode(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.transactionErrorStatusCode");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorInstanceId(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.transactionErrorInstanceId");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeBadResponse(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.transactionSpecificErrorTypeBadResponse");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalResponseError(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.transactionSpecificErrorTypeInternalResponseError");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalRequestError(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.transactionSpecificErrorTypeInternalRequestError");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalError(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.transactionSpecificErrorTypeInternalError");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorDetails(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.transactionSpecificErrorDetails");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorUseStatusCodeAsFaultCode(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.transactionErrorUseStatusCodeAsFaultCode");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorGenerateHttpHeaderGovWayCode(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.transactionErrorGenerateHttpHeaderGovWayCode");
	}	
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerConsegnaContenutiApplicativi(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerConsegnaContenutiApplicativi");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerEventi(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerEventi");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerFileSystemRecovery(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerFileSystemRecovery");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreBusteOnewayNonRiscontrate(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerGestoreBusteOnewayNonRiscontrate");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreBusteAsincroneNonRiscontrate(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerGestoreBusteAsincroneNonRiscontrate");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiEliminati(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerGestoreMessaggiPuliziaMessaggiEliminati");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiScaduti(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerGestoreMessaggiPuliziaMessaggiScaduti");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiNonGestiti(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerGestoreMessaggiPuliziaMessaggiNonGestiti");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaCorrelazioneApplicativa(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerGestoreMessaggiPuliziaCorrelazioneApplicativa");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiVerificaConnessioniAttive(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerGestoreMessaggiVerificaConnessioniAttive");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestorePuliziaMessaggiAnomali(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerGestorePuliziaMessaggiAnomali");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreRepositoryBuste(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerGestoreRepositoryBuste");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerMonitoraggioRisorseThread(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerMonitoraggioRisorseThread");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerRepositoryStatefulThread(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerRepositoryStatefulThread");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheOrarie(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerStatisticheOrarie");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheGiornaliere(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerStatisticheGiornaliere");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheSettimanali(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerStatisticheSettimanali");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheMensili(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerStatisticheMensili");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreChiaviPDND(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerGestoreChiaviPDND");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreCacheChiaviPDND(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerGestoreCacheChiaviPDND");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreOperazioniRemote(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerGestoreOperazioniRemote");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerSvecchiamentoOperazioniRemote(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerSvecchiamentoOperazioniRemote");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerThresholdThread(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.timerThresholdThread");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreById(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkConnettoreById");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreById(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.getCertificatiConnettoreById");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreTokenPolicyValidazione(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkConnettoreTokenPolicyValidazione");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreTokenPolicyNegoziazione(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkConnettoreTokenPolicyNegoziazione");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreAttributeAuthority(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkConnettoreAttributeAuthority");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreTokenPolicyValidazione(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.getCertificatiConnettoreTokenPolicyValidazione");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreTokenPolicyNegoziazione(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.getCertificatiConnettoreTokenPolicyNegoziazione");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreAttributeAuthority(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.getCertificatiConnettoreAttributeAuthority");
	}	
	public String getJmxPdDConfigurazioneSistemaNomeMetodoEnablePortaDelegata(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.enablePortaDelegata");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoDisablePortaDelegata(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.disablePortaDelegata");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoEnablePortaApplicativa(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.enablePortaApplicativa");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoDisablePortaApplicativa(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.disablePortaApplicativa");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoEnableConnettoreMultiplo(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.enableConnettoreMultiplo");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoDisableConnettoreMultiplo(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.disableConnettoreMultiplo");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoEnableSchedulingConnettoreMultiplo(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.enableSchedulingConnettoreMultiplo");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoDisableSchedulingConnettoreMultiplo(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.disableSchedulingConnettoreMultiplo");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoEnableSchedulingConnettoreMultiploRuntimeRepository(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.enableSchedulingConnettoreMultiploRuntimeRepository");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoDisableSchedulingConnettoreMultiploRuntimeRepository(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.disableSchedulingConnettoreMultiploRuntimeRepository");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheAccordoCooperazione(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.ripulisciRiferimentiCacheAccordoCooperazione");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheApi(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.ripulisciRiferimentiCacheApi");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheErogazione(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.ripulisciRiferimentiCacheErogazione");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheFruizione(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.ripulisciRiferimentiCacheFruizione");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheSoggetto(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.ripulisciRiferimentiCacheSoggetto");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheApplicativo(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.ripulisciRiferimentiCacheApplicativo");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheRuolo(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.ripulisciRiferimentiCacheRuolo");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheScope(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.ripulisciRiferimentiCacheScope");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheTokenPolicyValidazione(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.ripulisciRiferimentiCacheTokenPolicyValidazione");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheTokenPolicyNegoziazione(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.ripulisciRiferimentiCacheTokenPolicyNegoziazione");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheAttributeAuthority(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.ripulisciRiferimentiCacheAttributeAuthority");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatoApplicativoById(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatoApplicativoById");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatoModIApplicativoById(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatoModIApplicativoById");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiConnettoreHttpsById(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatiConnettoreHttpsById");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiJvm(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatiJvm");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiConnettoreHttpsTokenPolicyValidazione(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatiConnettoreHttpsTokenPolicyValidazione");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiValidazioneJwtTokenPolicyValidazione(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatiValidazioneJwtTokenPolicyValidazione");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiForwardToJwtTokenPolicyValidazione(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatiForwardToJwtTokenPolicyValidazione");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiConnettoreHttpsTokenPolicyNegoziazione(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatiConnettoreHttpsTokenPolicyNegoziazione");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiSignedJwtTokenPolicyNegoziazione(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatiSignedJwtTokenPolicyNegoziazione");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiConnettoreHttpsAttributeAuthority(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatiConnettoreHttpsAttributeAuthority");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiAttributeAuthorityJwtRichiesta(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatiAttributeAuthorityJwtRichiesta");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiAttributeAuthorityJwtRisposta(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatiAttributeAuthorityJwtRisposta");
	}
	public String getJmxPdDConfigurazioneSistemaNomeRisorsaAccessoRegistroServizi(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeRisorsaAccessoRegistroServizi");
	}
	public String getJmxPdDConfigurazioneSistemaNomeRisorsaStatoServiziPdD(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeRisorsaStatoServiziPdD");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaDelegata(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.statoServizioPortaDelegata");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaDelegataAbilitazioniPuntuali(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.statoServizioPortaDelegataAbilitazioniPuntuali");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaDelegataDisabilitazioniPuntuali(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.statoServizioPortaDelegataDisabilitazioniPuntuali");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaApplicativa(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.statoServizioPortaApplicativa");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaApplicativaAbilitazioniPuntuali(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.statoServizioPortaApplicativaAbilitazioniPuntuali");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaApplicativaDisabilitazioniPuntuali(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.statoServizioPortaApplicativaDisabilitazioniPuntuali");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoStatoServizioIntegrationManager(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.statoServizioIntegrationManager");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoAbilitaServizioPortaDelegata(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.abilitaServizioPortaDelegata");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoDisabilitaServizioPortaDelegata(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.disabilitaServizioPortaDelegata");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoAbilitaServizioPortaApplicativa(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.abilitaServizioPortaApplicativa");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoDisabilitaServizioPortaApplicativa(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.disabilitaServizioPortaApplicativa");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoAbilitaServizioIntegrationManager(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.abilitaServizioIntegrationManager");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoDisabilitaServizioIntegrationManager(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.disabilitaServizioIntegrationManager");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatoSoggettoById(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatoSoggettoById");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiConnettoreHttpsByIdRegistro(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatiConnettoreHttpsByIdRegistro");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiModIErogazioneById(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatiModIErogazioneById");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoCheckCertificatiModIFruizioneById(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.checkCertificatiModIFruizioneById");
	}
	public String getJmxPdDConfigurazioneSistemaNomeRisorsaDatasourceGW(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeRisorsaDatasourceGW");
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoNumeroDatasourceGW(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeAttributo.numeroDatasourceGW");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoGetDatasourcesGW(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.getDatasourcesGW");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoGetUsedConnectionsDatasourcesGW(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.getUsedConnectionsDatasourcesGW");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoGetInformazioniDatabaseDatasourcesGW(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.getInformazioniDatabaseDatasourcesGW");
	}
	public String getJmxPdDConfigurazioneSistemaNomeRisorsaGestioneConsegnaApplicativi(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeRisorsaGestioneConsegnaApplicativi");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoGetThreadPoolStatus(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.getThreadPoolStatus");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoGetQueueConfig(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.getQueueConfig");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoGetApplicativiPrioritari(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.getApplicativiPrioritari");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoGetConnettoriPrioritari(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.getConnettoriPrioritari");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoUpdateConnettoriPrioritari(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.updateConnettoriPrioritari");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoResetConnettoriPrioritari(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.resetConnettoriPrioritari");
	}
	public String getJmxPdDConfigurazioneSistemaNomeRisorsaSystemPropertiesPdD(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeRisorsaSystemPropertiesPdD");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoRefreshPersistentConfiguration(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.refreshPersistentConfiguration");
	}
	public String getJmxPdDConfigurazioneSistemaNomeRisorsaDatiRichieste(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeRisorsaDatiRichieste");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoRemoveRateLimitingGlobalConfigCache(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.removeRateLimitingGlobalConfigCache");
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoRemoveRateLimitingAPIConfigCache(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.removeRateLimitingAPIConfigCache");
	}
	public List<String> getJmxPdDCaches(String alias) throws UtilsException {
		return this.readJmxCaches(alias, "risorseJmxPdd.caches");
	}
	public List<String> getJmxPdDCachesPrefill(String alias) throws UtilsException {
		return this.readJmxCaches(alias, "risorseJmxPdd.caches.prefill");
	}
	private List<String> readJmxCaches(String alias,String property) throws UtilsException {
		List<String> list = new ArrayList<>();
		String tipo = getJmxPdDValueEngine(false, alias, property);
		if(tipo!=null && !"".equals(tipo)){
			String [] tmp = tipo.split(",");
			for (int i = 0; i < tmp.length; i++) {
				list.add(tmp[i].trim());
			}
		}
		return list;
	}
	public String getJmxPdDCacheType(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.cache.tipo");
	}
	public String getJmxPdDCacheNomeAttributoCacheAbilitata(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.cache.nomeAttributo.cacheAbilitata");
	}
	public String getJmxPdDCacheNomeMetodoStatoCache(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.cache.nomeMetodo.statoCache");
	}
	public String getJmxPdDCacheNomeMetodoResetCache(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.cache.nomeMetodo.resetCache");
	}
	public String getJmxPdDCacheNomeMetodoPrefillCache(String alias) throws UtilsException {
		return getJmxPdDValueEngine(true, alias, "risorseJmxPdd.cache.nomeMetodo.prefillCache");
	}
	
	
	/* ----- Opzioni di Visualizzazione ----- */
	
	public Boolean isShowJ2eeOptions() throws UtilsException{
		String tmp = this.readProperty(true, "server.tipo");
		return !"web".equals(tmp);
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
	
	public Boolean isConsoleGestioneWorkflowStatoDocumentiVisualizzaStatoLista() throws UtilsException{
		return this.readBooleanRequiredProperty("console.gestioneWorkflowStatoDocumenti.visualizzaStatoLista");
	}
	
	public Boolean isConsoleGestioneWorkflowStatoDocumentiRipristinoStatoOperativoDaFinale() throws UtilsException{
		return this.readBooleanRequiredProperty("console.gestioneWorkflowStatoDocumenti.finale.ripristinoStatoOperativo");
	}
	
	public Boolean isConsoleInterfacceAPIVisualizza() throws UtilsException{
		return this.readBooleanRequiredProperty("console.interfacceAPI.visualizza");
	}
	
	public Boolean isConsoleAllegatiVisualizza() throws UtilsException{
		return this.readBooleanRequiredProperty("console.allegati.visualizza");
	}
	
	public Boolean isEnableAutoMappingWsdlIntoAccordo() throws UtilsException{
		return this.readBooleanRequiredProperty("console.gestioneWsdl.autoMappingInAccordo");
	}
	
	public Boolean isEnableAutoMappingWsdlIntoAccordoEstrazioneSchemiInWsdlTypes() throws UtilsException{
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
	
	public boolean isProprietaErogazioniShowModalitaStandard() throws UtilsException{
		return this.readBooleanRequiredProperty("menu.proprietaErogazioni.showModalitaStandard");
	}
	
	public boolean isProprietaFruizioniShowModalitaStandard() throws UtilsException{
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
		if(!"true".equalsIgnoreCase(tmp) && !"false".equalsIgnoreCase(tmp)){
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
	
	public Boolean isElenchiSAAsincroniNonSupportatiVisualizzaRispostaAsincrona() throws UtilsException{
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
		return this.reader.readPropertiesConvertEnvProperties("console.tokenPolicy.mapping.");
	}
	
	public String getAttributeAuthorityForceId() throws UtilsException{
		return this.readProperty(false, "console.attributeAuthority.forceId");
	}
	
	public Properties getAttributeAuthorityTipologia() throws UtilsException{
		return this.reader.readPropertiesConvertEnvProperties("console.attributeAuthority.mapping.");
	}
	
	public Boolean isEnableServiziVisualizzaModalitaElenco() throws UtilsException{
		return this.readBooleanRequiredProperty("console.servizi.visualizzaModalitaElenco");
	}
	
	public Integer getNumeroMassimoSoggettiOperativiMenuUtente() throws UtilsException{
		return this.readIntegerProperty(true, "console.selectListSoggettiOperativi.numeroMassimoSoggettiVisualizzati");
	}
	
	public Integer getLunghezzaMassimaLabelSoggettiOperativiMenuUtente() throws UtilsException{
		return this.readIntegerProperty(true, "console.selectListSoggettiOperativi.lunghezzaMassimaLabel");
	}
	
	public Integer getLunghezzaMassimaInformazioneView() throws UtilsException{
		return this.readIntegerProperty(true, "console.view.lunghezzaMassimaInformazione");
	}
	
	public boolean isSetSearchAfterAdd() throws UtilsException{
		return this.readBooleanRequiredProperty("console.setSearchAfterAdd");
	}
	
	/* ----- Gestione vulnerabilita' console ------- */
	public Integer getValiditaTokenCsrf() throws UtilsException{
		return this.readIntegerProperty(true, "console.csrf.token.validita");
	}
	
	public String getCSPHeaderValue() throws UtilsException{
		return this.readProperty(true, "console.csp.header.value");
	}
	
	/* ---------------- Gestione govwayConsole centralizzata ----------------------- */

	public Boolean isGestioneCentralizzataSincronizzazionePdd() throws UtilsException{
		return this.readBooleanRequiredProperty("sincronizzazionePdd");
	}
	
	public Boolean isGestioneCentralizzataSincronizzazioneRegistro() throws UtilsException{
		return this.readBooleanRequiredProperty("sincronizzazioneRegistro");
	}
	
	public Boolean isGestioneCentralizzataSincronizzazioneGestoreEventi() throws UtilsException{
		return this.readBooleanRequiredProperty("sincronizzazioneGE");
	}
	
	public String getGestioneCentralizzataNomeCodaSmistatore() throws UtilsException{
		return this.readProperty(true, "SmistatoreQueue");
	}
	
	public String getGestioneCentralizzataNomeCodaRegistroServizi() throws UtilsException{
		return this.readProperty(true, "RegistroServiziQueue");
	}
	
	public String getGestioneCentralizzataWSRegistroServiziEndpointPdd() throws UtilsException{
		return this.readProperty(true, "RegistroServiziWS.endpoint.portaDominio");
	}
	
	public String getGestioneCentralizzataWSRegistroServiziEndpointSoggetto() throws UtilsException{
		return this.readProperty(true, "RegistroServiziWS.endpoint.soggetto");
	}
	
	public String getGestioneCentralizzataWSRegistroServiziEndpointAccordoCooperazione() throws UtilsException{
		return this.readProperty(true, "RegistroServiziWS.endpoint.accordoCooperazione");
	}
	
	public String getGestioneCentralizzataWSRegistroServiziEndpointAccordoServizioParteComune() throws UtilsException{
		return this.readProperty(true, "RegistroServiziWS.endpoint.accordoServizioParteComune");
	}
	
	public String getGestioneCentralizzataWSRegistroServiziEndpointAccordoServizioParteSpecifica() throws UtilsException{
		return this.readProperty(true, "RegistroServiziWS.endpoint.accordoServizioParteSpecifica");
	}
	
	public String getGestioneCentralizzataWSRegistroServiziCredenzialiBasicUsername() throws UtilsException{
		return this.readProperty(false, "RegistroServiziWS.username");
	}
	
	public String getGestioneCentralizzataWSRegistroServiziCredenzialiBasicPassword() throws UtilsException{
		return this.readProperty(false, "RegistroServiziWS.password");
	}
	
	public String getGestioneCentralizzataPrefissoNomeCodaConfigurazionePdd() throws UtilsException{
		return this.readProperty(true, "PdDQueuePrefix");
	}
	
	public String getGestioneCentralizzataGestorePddScriptShellPath() throws UtilsException{
		return this.readProperty(false, "GestorePdD.script.path");
	}
	
	public String getGestioneCentralizzataGestorePddScriptShellArgs() throws UtilsException{
		return this.readProperty(false, "GestorePdD.script.args");
	}
	
	public String getGestioneCentralizzataWSConfigurazioneEndpointSuffixPortaApplicativa() throws UtilsException{
		return this.readProperty(true, "ConfigurazioneWS.endpoint.suffix.portaApplicativa");
	}
	
	public String getGestioneCentralizzataWSConfigurazioneEndpointSuffixPortaDelegata() throws UtilsException{
		return this.readProperty(true, "ConfigurazioneWS.endpoint.suffix.portaDelegata");
	}
	
	public String getGestioneCentralizzataWSConfigurazioneEndpointSuffixServizioApplicativo() throws UtilsException{
		return this.readProperty(true, "ConfigurazioneWS.endpoint.suffix.servizioApplicativo");
	}
	
	public String getGestioneCentralizzataWSConfigurazioneEndpointSuffixSoggetto() throws UtilsException{
		return this.readProperty(true, "ConfigurazioneWS.endpoint.suffix.soggetto");
	}
	
	public String getGestioneCentralizzataWSConfigurazioneCredenzialiBasicUsername() throws UtilsException{
		return this.readProperty(false, "ConfigurazioneWS.username");
	}
	
	public String getGestioneCentralizzataWSConfigurazioneCredenzialiBasicPassword() throws UtilsException{
		return this.readProperty(false, "ConfigurazioneWS.password");
	}
	
	public String getGestioneCentralizzataNomeCodaGestoreEventi() throws UtilsException{
		return this.readProperty(true, "GestoreEventiQueue");
	}
	
	public String getGestioneCentralizzataPrefissoWSGestoreEventi() throws UtilsException{
		return this.readProperty(true, "UrlWebServiceGestoreEventi");
	}
	
	public String getGestioneCentralizzataGestoreEventiTipoSoggetto() throws UtilsException{
		return this.readProperty(true, "gestoreEventi.tipo_soggetto");
	}
	
	public String getGestioneCentralizzataGestoreEventiNomeSoggetto() throws UtilsException{
		return this.readProperty(true, "gestoreEventi.nome_soggetto");
	}
	
	public String getGestioneCentralizzataGestoreEventiNomeServizioApplicativo() throws UtilsException{
		return this.readProperty(true, "gestoreEventi.nome_servizio_applicativo");
	}
	
	public String getGestioneCentralizzataWSMonitorPddDefault() throws UtilsException{
		return this.readProperty(true, "MonitoraggioWS.pdd.default");
	}
	
	public String getGestioneCentralizzataWSMonitorEndpointSuffixStatoPdd() throws UtilsException{
		return this.readProperty(true, "MonitoraggioWS.endpoint.suffix.statoPdd");
	}
	
	public String getGestioneCentralizzataWSMonitorEndpointSuffixMessaggio() throws UtilsException{
		return this.readProperty(true, "MonitoraggioWS.endpoint.suffix.messaggio");
	}
	
	public String getGestioneCentralizzataWSMonitorCredenzialiBasicUsername() throws UtilsException{
		return this.readProperty(false, "MonitoraggioWS.username");
	}
	
	public String getGestioneCentralizzataWSMonitorCredenzialiBasicPassword() throws UtilsException{
		return this.readProperty(false, "MonitoraggioWS.password");
	}
	
	public String getGestioneCentralizzataURLContextCreazioneAutomaticaSoggetto() throws UtilsException{
		return this.readProperty(true, "UrlConnettoreSoggetto");
	}
	
	public String getGestioneCentralizzataPddIndirizzoIpPubblico() throws UtilsException{
		return this.readProperty(true, "pdd.indirizzoIP.pubblico");
	}
	
	public Integer getGestioneCentralizzataPddPortaPubblica() throws UtilsException{
		return this.readIntegerProperty(true, "pdd.porta.pubblica");
	}
	
	public String getGestioneCentralizzataPddIndirizzoIpGestione() throws UtilsException{
		return this.readProperty(true, "pdd.indirizzoIP.gestione");
	}
	
	public Integer getGestioneCentralizzataPddPortaGestione() throws UtilsException{
		return this.readIntegerProperty(true, "pdd.porta.gestione");
	}
	
	
	
	/* ---------------- Gestione govwayConsole locale ----------------------- */

	public Boolean isSinglePddGestionePdd() throws UtilsException{
		return this.readBooleanRequiredProperty("singlePdD.pdd.enabled");
	}
	
	public Boolean isSinglePddRegistroServiziLocale() throws UtilsException{
		return this.readBooleanRequiredProperty("singlePdD.registroServizi.locale");
	}
	
	public Boolean isSinglePddTracceConfigurazioneCustomAppender() throws UtilsException{
		return this.readBooleanRequiredProperty("tracce.configurazioneCustomAppender");
	}
	
	public Boolean isSinglePddTracceGestioneSorgentiDatiPrelevataDaDatabase() throws UtilsException{
		return this.readBooleanRequiredProperty("tracce.sorgentiDati.database");
	}
	
	public Boolean isSinglePddMessaggiDiagnosticiConfigurazioneCustomAppender() throws UtilsException{
		return this.readBooleanRequiredProperty("msgDiagnostici.configurazioneCustomAppender");
	}
	
	public Boolean isSinglePddMessaggiDiagnosticiGestioneSorgentiDatiPrelevataDaDatabase() throws UtilsException{
		return this.readBooleanRequiredProperty("msgDiagnostici.sorgentiDati.database");
	}
	
	public Boolean isSinglePddDumpConfigurazioneCustomAppender() throws UtilsException{
		return this.readBooleanRequiredProperty("dump.configurazioneCustomAppender");
	}
	
	public Boolean isSinglePddDumpConfigurazioneRealtime() throws UtilsException{
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
	
	public String[] getPluginsMenu() throws UtilsException{

		String[] retNull = null;
		
		String p = this.readProperty(false, "plugins.menu");
		if(p!=null && !"".equals(p.trim())){
			String [] tmp = p.trim().split(",");
			for (int i = 0; i < tmp.length; i++) {
				tmp[i] = tmp[i].trim();
			}
			return tmp;
		}
		return retNull;
	}
	
	public String[] getPluginsConfigurazione() throws UtilsException{
		
		String[] retNull = null;
		
		String p = this.readProperty(false, "plugins.configurazione");
		if(p!=null && !"".equals(p.trim())){
			String [] tmp = p.trim().split(",");
			for (int i = 0; i < tmp.length; i++) {
				tmp[i] = tmp[i].trim();
			}
			return tmp;
		}
		return retNull;
	}
	
	public String getPluginsConfigurazioneList() throws UtilsException{
		return this.readProperty(false, "plugins.configurazione.list");
	}
	
	public String[] getPluginsConnettore() throws UtilsException{

		String[] retNull = null;
		
		String p = this.readProperty(false, "plugins.connettore");
		if(p!=null && !"".equals(p.trim())){
			String [] tmp = p.trim().split(",");
			for (int i = 0; i < tmp.length; i++) {
				tmp[i] = tmp[i].trim();
			}
			return tmp;
		}
		return retNull;
	}
	
	public String getPluginsPortaDelegata() throws UtilsException{
		return this.readProperty(false, "plugins.portaDelegata");
	}
	
	public String getPluginsPortaApplicativa() throws UtilsException{
		return this.readProperty(false, "plugins.portaApplicativa");
	}
	
	
	
	/* ---- Gestione Visibilità utenti ------ */

	public Boolean isVisibilitaOggettiGlobale() throws UtilsException{
		String tmp = this.readProperty(true, "visibilitaOggetti");
		return !"locale".equals(tmp);
	}
	
	public List<String> getUtentiConVisibilitaGlobale() throws UtilsException{
		String utenti = this.readProperty(false, "utentiConVisibilitaGlobale");
		List<String> lista = new ArrayList<>();
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
	
	private PropertiesSourceConfiguration getSourceConfigurationEngine(String id, 
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
		
		String dirRefresh = this.readProperty(false, propertyExternalDirRefresh);
		if(dirRefresh!=null) {
			config.setUpdate("true".equalsIgnoreCase(dirRefresh.trim()));
		}
		
		String buildInt = this.readProperty(false, propertyBuiltIn);
		if(buildInt!=null) {
			read(config, buildInt);
		}
		
		String builtInRefresh = this.readProperty(false, propertyBuiltInRefresh);
		if(builtInRefresh!=null) {
			config.setUpdate("true".equalsIgnoreCase(builtInRefresh.trim()));
		}
		
		return config;

	}
	private void read(PropertiesSourceConfiguration config, String buildInt) throws UtilsException {
		try {
			try (InputStream is = ConsoleProperties.class.getResourceAsStream(buildInt);){
				if(is!=null) {
					byte [] zipContent = Utilities.getAsByteArray(is);

					File fTmp = FileSystemUtilities.createTempFile("propertiesSourceConfiguration", ".zip");
					try {
						FileSystemUtilities.writeFile(fTmp, zipContent);
						try (ZipFile zip = new ZipFile(fTmp);){
							Iterator<ZipEntry> itZip = ZipUtilities.entries(zip, true);
							List<byte[]> builtIdList = new ArrayList<>();
							while (itZip.hasNext()) {
								ZipEntry zipEntry = itZip.next();
								if(!zipEntry.isDirectory()) {
									try (InputStream isZip = zip.getInputStream(zipEntry);){
										byte [] bytes = Utilities.getAsByteArray(isZip);
										builtIdList.add(bytes);
									}
								}
							}
							config.setBuiltIn(builtIdList);
						}
					}finally {
						deleteFile(fTmp);
					}
				}
			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	private void deleteFile(File fTmp) {
		try {
			java.nio.file.Files.delete(fTmp.toPath());
		}catch(Exception e) {
			// ignore
		}
	}
	
	// propertiy per la gestione del console.font
	private String consoleFontName = null;
	private String consoleFontFamilyName = null;
	private int consoleFontStyle = -1;
	
	public String getConsoleFont() throws UtilsException{
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
	public String getLoginTipo() throws UtilsException{
		return this.readProperty(true,"login.tipo");
	}

	public boolean isLoginApplication() throws UtilsException{
		return this.readBooleanRequiredProperty("login.application");
	}

	public Properties getLoginProperties() throws UtilsException{
		return this.reader.readProperties("login.props.");
	}
	
	public String getLoginUtenteNonAutorizzatoRedirectUrl() throws UtilsException{
		if(this.isLoginApplication()) {
			return "";
		}
		return this.readProperty(true,"login.utenteNonAutorizzato.redirectUrl");
	}

	public String getLoginUtenteNonValidoRedirectUrl() throws UtilsException{
		if(this.isLoginApplication()) {
			return "";
		}
		return this.readProperty(true,"login.utenteNonValido.redirectUrl");
	}
	
	public String getLoginErroreInternoRedirectUrl() throws UtilsException{
		if(this.isLoginApplication()) {
			return "";
		}
		return this.readProperty(true,"login.erroreInterno.redirectUrl");
	}

	public String getLoginSessioneScadutaRedirectUrl() throws UtilsException{
		if(this.isLoginApplication()) {
			return "";
		}
		return this.readProperty(true,"login.sessioneScaduta.redirectUrl");
	}

	public boolean isMostraButtonLogout() throws UtilsException{
		if(this.isLoginApplication()) {
			return true;
		}
		return this.readBooleanRequiredProperty("logout.mostraButton.enabled");
	}

	public String getLogoutUrlDestinazione() throws UtilsException{
		if(this.isLoginApplication()) {
			return "";
		}
		return this.readProperty(true,"logout.urlDestinazione");
	}
}
