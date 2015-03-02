/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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


import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openspcoop2.pdd.config.OpenSPCoop2ConfigurationException;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;


/**
* ConsoleProperties
*
* @author Andrea Poli <apoli@link.it>
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
	public ConsoleProperties(String confDir,Logger log) throws Exception {

		if(log!=null)
			this.log = log;
		else
			this.log = Logger.getLogger(ConsoleProperties.class);
		
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
		    }catch(Exception er){}
		}

		this.reader = new ConsoleInstanceProperties(propertiesReader, this.log, confDir);
	}


	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 *
	 * 
	 */
	public static boolean initialize(String confDir,Logger log){

		try {
		    ConsoleProperties.consoleProperties = new ConsoleProperties(confDir,log);	
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
	private Boolean readBooleanProperty(boolean required,String property) throws UtilsException{
		String tmp = this.readProperty(required, property);
		if("true".equalsIgnoreCase(tmp)==false && "false".equalsIgnoreCase(tmp)==false){
			throw new UtilsException("Property ["+property+"] with uncorrect value ["+tmp+"] (true/value expected)");
		}
		return Boolean.parseBoolean(tmp);
	}
	private Integer readIntegerProperty(boolean required,String property) throws UtilsException{
		String tmp = this.readProperty(required, property);
		try{
			return Integer.parseInt(tmp);
		}catch(Exception e){
			throw new UtilsException("Property ["+property+"] with uncorrect value ["+tmp+"] (int value expected)");
		}
	}
	private Long readLongProperty(boolean required,String property) throws UtilsException{
		String tmp = this.readProperty(required, property);
		try{
			return Long.parseLong(tmp);
		}catch(Exception e){
			throw new UtilsException("Property ["+property+"] with uncorrect value ["+tmp+"] (int value expected)");
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
		return this.readBooleanProperty(true, "singlePdD");
	}
	
	public Boolean isGenerazioneAutomaticaPorteDelegate() throws UtilsException{
		return this.readBooleanProperty(true, "generazioneAutomaticaPorteDelegate");
	}
	
	public Boolean isForceWsdlBasedAzione_GenerazioneAutomaticaPorteDelegate() throws UtilsException{
		return this.readBooleanProperty(true, "generazioneAutomaticaPorteDelegate.azione.forceWsdlBased");
	}
	
	public String getAutenticazione_GenerazioneAutomaticaPorteDelegate() throws UtilsException{
		return this.readProperty(true, "generazioneAutomaticaPorteDelegate.autenticazione");
	}
	
	public String getAutorizzazione_GenerazioneAutomaticaPorteDelegate() throws UtilsException{
		return this.readProperty(true, "generazioneAutomaticaPorteDelegate.autorizzazione");
	}
	
	public Boolean isGenerazioneAutomaticaPorteApplicative() throws UtilsException{
		return this.readBooleanProperty(true, "generazioneAutomaticaPorteApplicative");
	}
	
	public Boolean isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto() throws UtilsException{
		return this.readBooleanProperty(true, "accordi.portType.implementazioneUnicaPerSoggetto");
	}
	
	public Boolean isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto() throws UtilsException{
		return this.readBooleanProperty(true, "accordi.implementazioneUnicaPerSoggetto");
	}
	
	public String getImportArchive_tipoPdD() throws UtilsException{
		return this.readProperty(true, "importArchive.tipoPdD");
	}
	
	public boolean isExportArchive_standard() throws UtilsException{
		return this.readBooleanProperty(true, "exportArchive.standard");
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
	
	public String getConsoleImmagineNomeApplicazione() throws UtilsException{
		return this.readProperty(true, "console.img.nomeApplicazione");
	}
	
	public String getConsoleLanguage() throws UtilsException{
		return this.readProperty(true, "console.language");
	}
	
	
	/* ----- Opzioni Accesso JMX della PdD ------- */
	
	public List<String> getJmxPdD_aliases() throws UtilsException {
		List<String> list = new ArrayList<String>();
		String tipo = this.readProperty(false, "risorseJmxPdd.aliases");
		if(tipo!=null && !"".equals(tipo)){
			String [] tmp = tipo.split(",");
			for (int i = 0; i < tmp.length; i++) {
				list.add(tmp[i].trim());
			}
		}
		return list;
	}
	
	public String getJmxPdD_descrizione(String alias) throws UtilsException {
		return this.readProperty(false, alias+".risorseJmxPdd.descrizione");
	}
	
	private String _getJmxPdD_value(boolean required, String alias, String prop) throws UtilsException{
		String tmp = this.readProperty(false, alias+"."+prop);
		if(tmp==null || "".equals(tmp)){
			tmp = this.readProperty(required, prop);
		}
		return tmp;
	}
	
	public String getJmxPdD_tipoAccesso(String alias) throws UtilsException {
		String tipo = _getJmxPdD_value(true, alias, "risorseJmxPdd.tipoAccesso");
		if(!CostantiControlStation.RESOURCE_JMX_PDD_TIPOLOGIA_ACCESSO_JMX.equals(tipo) && !CostantiControlStation.RESOURCE_JMX_PDD_TIPOLOGIA_ACCESSO_OPENSPCOOP.equals(tipo)){
			throw new UtilsException("Tipo ["+tipo+"] non supportato per la proprieta' 'risorseJmxPdd.tipoAccesso'");
		}
		return tipo;
	}
	public String getJmxPdD_remoteAccess_username(String alias) throws UtilsException {
		return _getJmxPdD_value(false, alias, "risorseJmxPdd.remoteAccess.username");
	}
	public String getJmxPdD_remoteAccess_password(String alias) throws UtilsException {
		return _getJmxPdD_value(false, alias, "risorseJmxPdd.remoteAccess.password");
	}
	public String getJmxPdD_remoteAccess_applicationServer(String alias) throws UtilsException {
		return _getJmxPdD_value(false, alias, "risorseJmxPdd.remoteAccess.as");
	}
	public String getJmxPdD_remoteAccess_factory(String alias) throws UtilsException {
		return _getJmxPdD_value(false, alias, "risorseJmxPdd.remoteAccess.factory");
	}
	public String getJmxPdD_remoteAccess_url(String alias) throws UtilsException {
		return _getJmxPdD_value(false, alias, "risorseJmxPdd.remoteAccess.url");
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
	public String getJmxPdD_configurazioneSistema_nomeMetodo_tipoDatabase(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.tipoDatabase");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniDatabase(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.infoDatabase");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_directoryConfigurazione(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.confDir");
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_pluginProtocols(String alias) throws UtilsException {
		return _getJmxPdD_value(true, alias, "risorseJmxPdd.configurazioneSistema.nomeMetodo.pluginProtocols");
	}
	public List<String> getJmxPdD_caches(String alias) throws UtilsException {
		List<String> list = new ArrayList<String>();
		String tipo = _getJmxPdD_value(false, alias, "risorseJmxPdd.caches");
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
		return this.readBooleanProperty(true, "console.configurazioniPersonalizzate");
	}
	
	public Boolean isConsoleGestioneSoggettiVirtuali() throws UtilsException{
		return this.readBooleanProperty(true, "console.gestioneSoggettiVirtuali");
	}
	
	public Boolean isConsoleGestioneRuoliServiziApplicativi() throws UtilsException{
		return this.readBooleanProperty(true, "console.gestioneRuoliServiziApplicativi");
	}
	
	public Boolean isConsoleGestioneWorkflowStatoDocumenti() throws UtilsException{
		return this.readBooleanProperty(true, "console.gestioneWorkflowStatoDocumenti");
	}
	
	public Boolean isConsoleGestioneWorkflowStatoDocumenti_ripristinoStatoOperativoDaFinale() throws UtilsException{
		return this.readBooleanProperty(true, "console.gestioneWorkflowStatoDocumenti.finale.ripristinoStatoOperativo");
	}
	
	public Boolean isConsoleModalitaInterfacciaSwitchRapido() throws UtilsException{
		return this.readBooleanProperty(true, "console.modalitaInterfaccia.switchRapido");
	}
	
	public String getLabelConsoleInterfacciaSwitchRapidoModalitaAvanzata() throws UtilsException{
		return this.readProperty(true, "console.modalitaInterfaccia.switchRapido.modalitaAvanzata.label");
	}
	
	public String getLabelConsoleInterfacciaSwitchRapidoModalitaStandard() throws UtilsException{
		return this.readProperty(true, "console.modalitaInterfaccia.switchRapido.modalitaStandard.label");
	}
	
	public Boolean isEnableAutoMappingWsdlIntoAccordo() throws UtilsException{
		return this.readBooleanProperty(true, "console.gestioneWsdl.autoMappingInAccordo");
	}
	
	public Boolean isMenuPorteApplicative_ProtocolProperties_VisualizzaListaValoriPredefiniti() throws UtilsException{
		return this.readBooleanProperty(true, "menu.pa.protocolProperties.visualizzaListaValoriPredefiniti");
	}
	
	public Boolean isMenuVisualizzazioneAggregataOggettiRegistro() throws UtilsException{
		return this.readBooleanProperty(true, "menu.visualizzazioneAggregataOggettiRegistro");
	}
	
	public Boolean isMenuVisualizzazioneLinkAggiungi() throws UtilsException{
		return this.readBooleanProperty(true, "menu.visualizzazioneLinkAggiungi");
	}
	
	public Boolean isMenuVisualizzaFlagPrivato() throws UtilsException{
		return this.readBooleanProperty(true, "menu.visualizzaFlagPrivato");
	}
	
	public Boolean isMenuVisualizzaListaCompletaConnettori() throws UtilsException{
		return this.readBooleanProperty(true, "menu.visualizzaListaCompletaConnettori");
	}
	
	public Boolean isMenuAccordiVisualizzaCorrelazioneAsincrona() throws UtilsException{
		return this.readBooleanProperty(true, "menu.accordi.visualizzaCorrelazioneAsincrona");
	}
	
	public Boolean isMenuAccordiVisualizzazioneGestioneInformazioniProtocollo() throws UtilsException{
		return this.readBooleanProperty(true, "menu.accordi.visualizzazioneGestioneInformazioniProtocollo");
	}
	
	public Boolean isMenuAccordiServizioParteComuneVisualizzazioneWSDLDefinitorio() throws UtilsException{
		return this.readBooleanProperty(true, "menu.accordiServizioParteComune.visualizzazioneWSDLDefinitorio");
	}
	
	public Boolean isMenuAccordiServizioParteSpecificaVisualizzazioneVersione() throws UtilsException{
		return this.readBooleanProperty(true, "menu.accordiServizioParteSpecifica.visualizzazioneVersione");
	}
	
	public Boolean isMenuMTOMVisualizzazioneCompleta() throws UtilsException{
		return this.readBooleanProperty(true, "menu.mtom.visualizzazioneCompleta");
	}
	
	public Boolean isMenuPortaDelegataVisualizzazioneUrlInvocazione() throws UtilsException{
		return this.readBooleanProperty(true, "menu.pd.visualizzazioneUrlInvocazione");
	}
	
	public Boolean isElenchiVisualizzaPulsanteAggiungi() throws UtilsException{
		return this.readBooleanProperty(true, "elenchi.visualizzaPulsanteAggiungi");
	}
	
	public Boolean isElenchiVisualizzaCountElementi() throws UtilsException{
		return this.readBooleanProperty(true, "elenchi.visualizzaCountElementi");
	}
	
	public Boolean isElenchiAccordiVisualizzaColonnaAzioni() throws UtilsException{
		return this.readBooleanProperty(true, "elenchi.accordi.visualizzaColonnaAzioni");
	}
	
	public Boolean isElenchiAccordiVisualizzaColonnaServizi() throws UtilsException{
		return this.readBooleanProperty(true, "elenchi.accordi.visualizzaColonnaServizi");
	}
	
	public Boolean isElenchiSA_asincroniNonSupportati_VisualizzaRispostaAsincrona() throws UtilsException{
		return this.readBooleanProperty(true, "elenchi.serviziApplicativi.asincroniNonSupportati.visualizzazioneRispostaAsincrona");
	}
	
	public Boolean isElenchiMenuVisualizzazionePulsantiImportExportPackage() throws UtilsException{
		return this.readBooleanProperty(true, "elenchi_menu.visualizzazionePulsantiImportExportPackage");
	}
	
	public Boolean isElenchiMenuVisualizzazioneCampiInserimentoStrutturaMessaggiWsdl() throws UtilsException{
		return this.readBooleanProperty(true, "elenchi_menu.accordiServizioParteComune.strutturaWsdlMessaggi.visualizzaCampi");
	}
	

	
	/* ---------------- Gestione pddConsole centralizzata ----------------------- */

	public Boolean isGestioneCentralizzata_SincronizzazionePdd() throws UtilsException{
		return this.readBooleanProperty(true, "sincronizzazionePdd");
	}
	
	public Boolean isGestioneCentralizzata_SincronizzazioneRegistro() throws UtilsException{
		return this.readBooleanProperty(true, "sincronizzazioneRegistro");
	}
	
	public Boolean isGestioneCentralizzata_SincronizzazioneRepositoryAutorizzazioni() throws UtilsException{
		return this.readBooleanProperty(true, "sincronizzazioneRepositoryAutorizzazioni");
	}
	
	public Boolean isGestioneCentralizzata_SincronizzazioneGestoreEventi() throws UtilsException{
		return this.readBooleanProperty(true, "sincronizzazioneGE");
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
	
	public String getGestioneCentralizzata_NomeCodaRepositoryAutorizzazioni() throws UtilsException{
		return this.readProperty(true, "RepositoryAutorizzazioniQueue");
	}
	
	public String getGestioneCentralizzata_RepositoryAutorizzazioniClassName() throws UtilsException{
		return this.readProperty(true, "RepositoryAutorizzazioniClassName");
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
	
	
	
	/* ---------------- Gestione pddConsole locale ----------------------- */
	
	public Boolean isSinglePdD_RegistroServiziLocale() throws UtilsException{
		return this.readBooleanProperty(true, "singlePdD.registroServizi.locale");
	}
	
	public Boolean isSinglePdD_TracceConfigurazioneCustomAppender() throws UtilsException{
		return this.readBooleanProperty(true, "tracce.configurazioneCustomAppender");
	}
	
	public Boolean isSinglePdD_TracceGestioneSorgentiDatiPrelevataDaDatabase() throws UtilsException{
		return this.readBooleanProperty(true, "tracce.sorgentiDati.database");
	}
	
	public Boolean isSinglePdD_MessaggiDiagnosticiConfigurazioneCustomAppender() throws UtilsException{
		return this.readBooleanProperty(true, "msgDiagnostici.configurazioneCustomAppender");
	}
	
	public Boolean isSinglePdD_MessaggiDiagnosticiGestioneSorgentiDatiPrelevataDaDatabase() throws UtilsException{
		return this.readBooleanProperty(true, "msgDiagnostici.sorgentiDati.database");
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

}
