/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
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


package org.openspcoop2.protocol.spcoop.sica;

import java.util.Enumeration;
import java.util.Hashtable;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;



/**
* Contesto per la classe SICAtoOpenSPCoopContext
*
* @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class SICAtoOpenSPCoopContext {

	private static SICAtoOpenSPCoopContext staticContext = null;
	public static void initSICAtoOpenSPCoopContext() throws SICAToOpenSPCoopUtilitiesException{
		initSICAtoOpenSPCoopContext(null);
	}
	public static synchronized void initSICAtoOpenSPCoopContext(String configurationFile) throws SICAToOpenSPCoopUtilitiesException{
		if(staticContext==null){
			staticContext = new SICAtoOpenSPCoopContext(configurationFile);
		}
	}
	public static SICAtoOpenSPCoopContext getInstance() throws SICAToOpenSPCoopUtilitiesException{
		if(staticContext==null){
			initSICAtoOpenSPCoopContext(DEFAULT_SICA_PROPERTIES_NAME);
		}
		return staticContext;
	}
	
	private static String DEFAULT_SICA_PROPERTIES_NAME = "sica";

	private String configurationFile;
	public SICAtoOpenSPCoopContext() throws SICAToOpenSPCoopUtilitiesException{
		this(DEFAULT_SICA_PROPERTIES_NAME);
	}
	public SICAtoOpenSPCoopContext(String configurationFile) throws SICAToOpenSPCoopUtilitiesException{
		try{
			this.configurationFile = configurationFile;
			
			this.SICAClient_generaProject = Boolean.parseBoolean(readProperty("SICAClient.project"));
			this.SICAClient_includiInfoRegistroGenerale = Boolean.parseBoolean(readProperty("SICAClient.includiInfoRegistroGenerale"));
			this.SICAClient_nomeAccordo_32CaratteriMax = Boolean.parseBoolean(readProperty("SICAClient.nomeAccordo.32CaratteriMax"));
			
			this.InformazioniEGov_specificaSemiformale = Boolean.parseBoolean(readProperty("InformazioniEGov.specificaSemiformale"));
			this.InformazioniEGov_wscp = Boolean.parseBoolean(readProperty("InformazioniEGov.wscp"));
			this.InformazioniEGov_wscpDisabled_namespaceCnipa = Boolean.parseBoolean(readProperty("InformazioniEGov.wscpDisabled.namespace.cnipa"));
			this.InformazioniEGov_wscpDisabled_childUnqualified = Boolean.parseBoolean(readProperty("InformazioniEGov.wscpDisabled.childUnqualified"));
			this.InformazioniEGov_wscpEnabled_childUnqualified = Boolean.parseBoolean(readProperty("InformazioniEGov.wscpEnabled.childUnqualified"));
			this.InformazioniEGov_nomiSPCoop_qualified = Boolean.parseBoolean(readProperty("InformazioniEGov.nomiSPCoop.qualified"));
			
			this.WSDL_XSD_prettyDocuments = Boolean.parseBoolean(readProperty("WSDL_XSD.prettyDocuments"));
			this.WSDL_XSD_allineaImportInclude = Boolean.parseBoolean(readProperty("WSDL_XSD.allineaImportInclude"));
			this.WSDL_XSD_accordiParteSpecifica_gestioneParteComune = Boolean.parseBoolean(readProperty("WSDL_XSD.accordiParteSpecifica.gestioneParteComune"));
			this.WSDL_XSD_accordiParteSpecifica_openspcoopToSica_eliminazioneImportParteComune = Boolean.parseBoolean(readProperty("WSDL_XSD.accordiParteSpecifica.openspcoopToSica.eliminazioneImportParteComune"));
			this.WSDL_XSD_accordiParteSpecifica_sicaToOpenspcoop_aggiuntaImportParteComune = Boolean.parseBoolean(readProperty("WSDL_XSD.accordiParteSpecifica.sicaToOpenspcoop.aggiuntaImportParteComune"));
			this.WSDL_XSD_accordiParteSpecifica_wsdlEmpty = Boolean.parseBoolean(readProperty("WSDL_XSD.accordiParteSpecifica.wsdlEmpty"));
			
			this.setSICAClientEnabledOptions(readProperty("SICAClient.opzioniAbilitate"));
			this.setSICAClientDisabledOptions(readProperty("SICAClient.opzioniDisabilitate"));
		}
		catch(Exception e){
			throw new SICAToOpenSPCoopUtilitiesException(e);
		}
	}	
	

	private java.util.ResourceBundle bundle = null;
	public java.util.ResourceBundle getBundle() {
		return this.bundle;
	}
	public void setBundle(java.util.ResourceBundle bundle) {
		this.bundle = bundle;
	}
	private synchronized void initResourceBundle(){
		if(this.bundle==null){
			this.bundle = java.util.ResourceBundle.getBundle(this.configurationFile);
		} 
	}
	
	private String readProperty(String key) throws Exception{
		if(this.bundle==null){
			initResourceBundle();
		}
		String properties = this.bundle.getString(key);
		if(properties==null)
			throw new Exception("Proprieta ["+key+"] non trovata");
		return properties.trim();
	}
	
	
	
	
	// -------- Opzioni di compatibilita' con SICA Client -----------------

	/** Indicazione se i package vengono generati con il .project
	  Tale file e' necessario per importare i package nel SICAClient */
	private boolean SICAClient_generaProject = true;

	/** Indicazione se i package vengono generate con le informazioni 
	 inserite dal RegistroSICA Generale in fase di pubblicazione:
	 - versione
	 - soggetto erogatore/referente */
	private boolean SICAClient_includiInfoRegistroGenerale = true;

	/** Indica se il nome degli accordi deve essere limitato al numero di caratteri limite uguale a 32. */
	private boolean  SICAClient_nomeAccordo_32CaratteriMax = true;


	// --------- Documento di Modalita' esplicita delle informazioni eGov -------------

	/** Indicazione se le informazioni sul protocollo spcoop vengono inserite
	 come documento di specifica semiformale (proprietà = true) o come allegato generico (proprietà = false) */ 
	private boolean InformazioniEGov_specificaSemiformale = true;

	/** Indicazione se le informazioni sul protocollo spcoop vengono generate tramite lo schema xsd
	 utilizzato nel ClientSICA  o tramite lo schema definito dal documento
	 'Struttura dell'accordo di servizio e dell'accordo di cooperazione: versione 1.0' del CNIPA */
	private boolean InformazioniEGov_wscp = false;
	
	/** Opzione valida SOLO SE InformazioniEGov.wscp = false
	 Indicare il namespace da utilizzare per le informazioni sul protocollo spcoop.
	 Sul documento  'Struttura dell'accordo di servizio e dell'accordo di cooperazione: versione 1.0' del cnipa viene indicato "http://www.cnipa.it/collProfiles" (true)
	 Da comunicazioni CNIPA il namespace sembrerebbe comunque cambiato in "http://spcoop.gov.it/collProfiles" (false) */
	private boolean InformazioniEGov_wscpDisabled_namespaceCnipa = false;
	
	/** Opzione valida SOLO SE InformazioniEGov.wscp = false
	 il Client SICA permette di costruire XML che possiedono un prefix nel root element,
	 ma poi non usato negli elementi interni.
	 Es:
	 <tns:egovDecllElement xmlns:tns="http://spcoop.gov.it/collProfiles" ...>
	 		<e-govVersion>....
	 Questo XML non e' validabile rispetto all'XSD poiche' e-govVersion non e' qualificato.
	
	 Se si abilita la proprieta' sottostante, vengono accettati anche tali file XML malformati. */
	private boolean InformazioniEGov_wscpDisabled_childUnqualified=true;

	/** Opzione valida SOLO SE InformazioniEGov.wscp = true
	 Il client SICA si aspetta un file wscp che possiedono un prefix nel root element wscp,
	 e che negli elementi interni tale prefix non venga utilizzato.
	 Se si costruisce un xml regolare (con anche gli elementi interni correttamente qualificati) il client SICA da errore
	 Es di xml atteso:
	 <wspc:profiloCollaborazioneEGOV xmlns:wscp="http://spcoop.gov.it/sica/wscp" ...>
	 		<versioneEGOV>....
	
	 Se si abilita la proprieta' sottostante, vengono sia gestiti (e accettati) che prodotti xml compatibili con il ClientSICA (non validabili rispetto all'xsd) */
	private boolean InformazioniEGov_wscpEnabled_childUnqualified=true;
	
	/** Indicazione se nelle informazioni sul protocollo spcoop vengono inseriti nomi di azioni e servizi qualificati con prefisso e namespace
	  Il Namespace utilizzato sara' il target namespace del wsdl concettuale */
	private boolean  InformazioniEGov_nomiSPCoop_qualified=true;



	// --------- Gestione documenti WSDL/XSD ----------------------

	/** Indicazione se i documenti trattati devono essere formattati */
	private boolean WSDL_XSD_prettyDocuments = false;

	/** Indicazione se i wsdl e gli xsd presenti nei package devono essere preprocessati prima
	 di importarli o esportali nel registro dei servizi di OpenSPCoop.
	 In caso di pre-processamento attivo, tutti gli import e gli include presenti vengono allineati
	 con la struttura dei package del registro dei servizi di OpenSPCoop (in caso di import) 
	 o con la struttura dei package CNIPA (in caso di export) */
	private boolean WSDL_XSD_allineaImportInclude = true;

	/** Se viene abilitata l'opzione:
	 - in fase di ESPORTAZIONE, dal registro dei servizi OpenSPCoop, 
	 	i package degli accordi parte specifica conterranno dei porti di accesso WSDL che includono la parte comune direttamente nel WSDL.
	 - in fase di IMPORTAZIONE dei package viene attuato il processo inverso.*/ 
	private boolean WSDL_XSD_accordiParteSpecifica_gestioneParteComune = true;
	
	/** Questa opzione viene interpretata solo se l'opzione  'WSDL_XSD.accordiParteSpecifica.gestioneParteComune' e' disabilitata
	 Se si abilita, in fase di esportazione viene controllato che all'interno del wsdl implementativo l'import della parte comune non sia presente (e se presente viene eliminato).*/
	private boolean WSDL_XSD_accordiParteSpecifica_openspcoopToSica_eliminazioneImportParteComune = false;

	/** Questa opzione viene interpretata solo se l'opzione  'WSDL_XSD.accordiParteSpecifica.gestioneParteComune' e' disabilitata
	 Se si abilita, in fase di importazione viene controllato che all'interno del wsdl implementativo l'import della parte comune sia presente (e se non presente viene aggiunto). */
	private boolean WSDL_XSD_accordiParteSpecifica_sicaToOpenspcoop_aggiuntaImportParteComune = false;
	
	/** Indicazione se devono essere prodotti dei WSDL vuoti, in caso non sia definita la parte implementativa fruitore o erogatore
	 negli accordi di servizio parte specifica */
	private boolean WSDL_XSD_accordiParteSpecifica_wsdlEmpty = true;
	
	
	
	
	// --------- Compatibilita' Client SICA ----------------------
	/** Indicazione se i package devono essere creati SICAClient compatibility */
	private boolean SICAClientCompatibility = false;
	
	/** Lista di proprieta' sopra definite, separate da virgola, che si vogliono utilizzare come abilitate per creare package compatibili per il ClientSICA */
	private Hashtable<String, Boolean> SICAClientEnabledOptions = new Hashtable<String, Boolean>();
	
	/** Lista di proprieta' sopra definite, separate da virgola, che si vogliono utilizzare come disabilitate per creare package compatibili per il ClientSICA */
	private Hashtable<String, Boolean> SICAClientDisabledOptions = new Hashtable<String, Boolean>();
	
	
	
	
	// --------- Array list contenente il mapping fra Soggetti e CodiciIPA
	private Hashtable<String, String> mappingSoggettoSPCoopToCodiceIPA = new Hashtable<String, String>();
	
	public void addMappingSoggettoSPCoopToCodiceIPA(IDSoggetto idSoggettoSPCoop,String codiceIPA){
		if(codiceIPA==null || idSoggettoSPCoop==null || idSoggettoSPCoop.toString() ==null)
			return;
		if(this.mappingSoggettoSPCoopToCodiceIPA.containsKey(idSoggettoSPCoop.toString())==false)
			this.mappingSoggettoSPCoopToCodiceIPA.put(idSoggettoSPCoop.toString(), codiceIPA);
	}
	
	public String getCodiceIPA(IDSoggetto idSoggettoSPCoop){
		return this.mappingSoggettoSPCoopToCodiceIPA.get(idSoggettoSPCoop.toString());
	}
	
	public String getCodiceIPA(String nomeSoggetto){
		return this.getCodiceIPA(new IDSoggetto("SPC", nomeSoggetto));
	}
	
	public IDSoggetto getIDSoggetto(String codiceIPA){
		if(this.mappingSoggettoSPCoopToCodiceIPA.contains(codiceIPA)){
			Enumeration<String> keys = this.mappingSoggettoSPCoopToCodiceIPA.keys();
			while(keys.hasMoreElements()){
				String key = keys.nextElement();
				String value = this.mappingSoggettoSPCoopToCodiceIPA.get(key);
				if(value.equals(codiceIPA)){
					String [] split = key.split("/");
					return new IDSoggetto(split[0],split[1]);
				}
			}
		}
		return null;
	}
	
	
	
	
	// --------- Array list contenente il mapping fra uri AccordiServizioParteSpecifica e IDServizio OpenSPCoop
	private Hashtable<String,String> mappingServizioSPCoopToUriAPS = new Hashtable<String,String>();
	
	public void addMappingServizioToUriAPS(IDServizio idServizioSPCoop,IDAccordo idAccordoServizioParteSpecifica) throws SICAToOpenSPCoopUtilitiesException{
		if(idServizioSPCoop==null || 
				idServizioSPCoop.getTipoServizio()==null || idServizioSPCoop.getServizio()==null ||
				idServizioSPCoop.getSoggettoErogatore()==null ||
				idServizioSPCoop.getSoggettoErogatore().getTipo()==null || idServizioSPCoop.getSoggettoErogatore().getNome()==null){
			return;
		}
		if(idAccordoServizioParteSpecifica==null || idAccordoServizioParteSpecifica.getNome()==null || idAccordoServizioParteSpecifica.getVersione()==null ||
				idAccordoServizioParteSpecifica.getSoggettoReferente()==null ||
				idAccordoServizioParteSpecifica.getSoggettoReferente().getTipo()==null ||
				idAccordoServizioParteSpecifica.getSoggettoReferente().getNome()==null){
			return;
		}
		idServizioSPCoop.setAzione(null); // per il toString non deve essere usato senno viene processato
		String keyServizioSPCoop = idServizioSPCoop.toString();
		if(this.mappingServizioSPCoopToUriAPS.containsKey(keyServizioSPCoop)==false){
			this.mappingServizioSPCoopToUriAPS.put(keyServizioSPCoop, SICAtoOpenSPCoopUtilities.idAccordoServizioParteSpecifica_openspcoopToSica(idAccordoServizioParteSpecifica, this));
		}
	}
	
	public String getUriAPS(IDServizio idServizioSPCoop){
		return this.mappingServizioSPCoopToUriAPS.get(idServizioSPCoop.toString());
	}
	
	public IDServizio getIDServizio(IDAccordo idAccordoServizioParteSpecifica) throws SICAToOpenSPCoopUtilitiesException{
		return getIDServizio(SICAtoOpenSPCoopUtilities.idAccordoServizioParteSpecifica_openspcoopToSica(idAccordoServizioParteSpecifica, this));
	}
	
	public IDServizio getIDServizio(String uriAPS){
		if(this.mappingServizioSPCoopToUriAPS.contains(uriAPS)){
			Enumeration<String> keys = this.mappingServizioSPCoopToUriAPS.keys();
			while(keys.hasMoreElements()){
				String key = keys.nextElement();
				String value = this.mappingServizioSPCoopToUriAPS.get(key);
				if(value.equals(uriAPS)){
					try{
						String [] split = key.split("--");
						
						String soggetto = split[0];
						String tipoSoggetto = soggetto.split("/")[0];
						String nomeSoggetto = soggetto.split("/")[1];
						
						String servizio = split[1];
						String tipoServizio = servizio.split("/")[0];
						String nomeServizio = servizio.split("/")[1];
						int indexOf = nomeServizio.lastIndexOf(":");
						if(indexOf>0){
							nomeServizio = nomeServizio.substring(0,indexOf);
						}
						
						IDServizio idS = new IDServizio(tipoSoggetto,nomeSoggetto,
								tipoServizio,nomeServizio); 
						return idS;
					}catch(Exception e){
						throw new RuntimeException("(key["+key+"] value["+value+"]) "+ e.getMessage(),e);
					}
				}
			}
		}
		return null;
	}
	
	
	
	
	
	// ----------- Metodi di Get/Set ----------------------------
	
	public boolean isSICAClient_generaProject() {
		return checkOption("SICAClient.project",this.SICAClient_generaProject);
	}

	public void setSICAClient_generaProject(boolean client_generaProject) {
		this.SICAClient_generaProject = client_generaProject;
	}

	public boolean isSICAClient_includiInfoRegistroGenerale() {
		return checkOption("SICAClient.includiInfoRegistroGenerale",this.SICAClient_includiInfoRegistroGenerale);
	}

	public void setSICAClient_includiInfoRegistroGenerale(
			boolean client_includiInfoRegistroGenerale) {
		this.SICAClient_includiInfoRegistroGenerale = client_includiInfoRegistroGenerale;
	}

	public boolean isSICAClient_nomeAccordo_32CaratteriMax() {
		return checkOption("SICAClient.nomeAccordo.32CaratteriMax",this.SICAClient_nomeAccordo_32CaratteriMax);
	}
	public void setSICAClient_nomeAccordo_32CaratteriMax(
			boolean client_nomeAccordo_32CaratteriMax) {
		this.SICAClient_nomeAccordo_32CaratteriMax = client_nomeAccordo_32CaratteriMax;
	}
	
	public boolean isWSDL_XSD_accordiParteSpecifica_wsdlEmpty() {
		return checkOption("WSDL_XSD.accordiParteSpecifica.wsdlEmpty",this.WSDL_XSD_accordiParteSpecifica_wsdlEmpty);
	}

	public void setWSDL_XSD_accordiParteSpecifica_wsdlEmpty(
			boolean client_accordiParteSpecifica_wsdlEmpty) {
		this.WSDL_XSD_accordiParteSpecifica_wsdlEmpty = client_accordiParteSpecifica_wsdlEmpty;
	}

	public boolean isInformazioniEGov_specificaSemiformale() {
		return checkOption("InformazioniEGov.specificaSemiformale",this.InformazioniEGov_specificaSemiformale);
	}

	public void setInformazioniEGov_specificaSemiformale(
			boolean informazioniEGov_specificaSemiformale) {
		this.InformazioniEGov_specificaSemiformale = informazioniEGov_specificaSemiformale;
	}

	public boolean isInformazioniEGov_wscp() {
		return checkOption("InformazioniEGov.wscp",this.InformazioniEGov_wscp);
	}

	public void setInformazioniEGov_wscp(boolean informazioniEGov_wscp) {
		this.InformazioniEGov_wscp = informazioniEGov_wscp;
	}

	public boolean isInformazioniEGov_wscpDisabled_namespaceCnipa() {
		return this.InformazioniEGov_wscpDisabled_namespaceCnipa;
	}
	public void setInformazioniEGov_wscpDisabled_namespaceCnipa(
			boolean informazioniEGovWscpDisabledNamespaceCnipa) {
		this.InformazioniEGov_wscpDisabled_namespaceCnipa = informazioniEGovWscpDisabledNamespaceCnipa;
	}
	
	public boolean isInformazioniEGov_wscpDisabled_childUnqualified() {
		return this.InformazioniEGov_wscpDisabled_childUnqualified;
	}
	public void setInformazioniEGov_wscpDisabled_childUnqualified(
			boolean informazioniEGovWscpDisabledChildUnqualified) {
		this.InformazioniEGov_wscpDisabled_childUnqualified = informazioniEGovWscpDisabledChildUnqualified;
	}
	public boolean isInformazioniEGov_wscpEnabled_childUnqualified() {
		return this.InformazioniEGov_wscpEnabled_childUnqualified;
	}
	public void setInformazioniEGov_wscpEnabled_childUnqualified(
			boolean informazioniEGovWscpEnabledChildUnqualified) {
		this.InformazioniEGov_wscpEnabled_childUnqualified = informazioniEGovWscpEnabledChildUnqualified;
	}
	
	public boolean isInformazioniEGov_nomiSPCoop_qualified() {
		return this.InformazioniEGov_nomiSPCoop_qualified;
	}
	public void setInformazioniEGov_nomiSPCoop_qualified(
			boolean informazioniEGovNomiSPCoopQualified) {
		this.InformazioniEGov_nomiSPCoop_qualified = informazioniEGovNomiSPCoopQualified;
	}
	
	public boolean isWSDL_XSD_prettyDocuments() {
		return checkOption("WSDL_XSD.prettyDocuments",this.WSDL_XSD_prettyDocuments);
	}

	public void setWSDL_XSD_prettyDocuments(boolean documents) {
		this.WSDL_XSD_prettyDocuments = documents;
	}

	public boolean isWSDL_XSD_allineaImportInclude() {
		return checkOption("WSDL_XSD.allineaImportInclude",this.WSDL_XSD_allineaImportInclude);
	}

	public void setWSDL_XSD_allineaImportInclude(boolean importInclude) {
		this.WSDL_XSD_allineaImportInclude = importInclude;
	}

	public boolean isWSDL_XSD_accordiParteSpecifica_gestioneParteComune() {
		return checkOption("WSDL_XSD.accordiParteSpecifica.gestioneParteComune",this.WSDL_XSD_accordiParteSpecifica_gestioneParteComune);
	}

	public void setWSDL_XSD_accordiParteSpecifica_gestioneParteComune(
			boolean parteSpecifica_gestioneParteComune) {
		this.WSDL_XSD_accordiParteSpecifica_gestioneParteComune = parteSpecifica_gestioneParteComune;
	}

	public boolean isWSDL_XSD_accordiParteSpecifica_openspcoopToSica_eliminazioneImportParteComune() {
		return checkOption("WSDL_XSD.accordiParteSpecifica.openspcoopToSica.eliminazioneImportParteComune",this.WSDL_XSD_accordiParteSpecifica_openspcoopToSica_eliminazioneImportParteComune);
	}

	public void setWSDL_XSD_accordiParteSpecifica_openspcoopToSica_eliminazioneImportParteComune(
			boolean parteSpecifica_openspcoopToSica_eliminazioneImportParteComune) {
		this.WSDL_XSD_accordiParteSpecifica_openspcoopToSica_eliminazioneImportParteComune = parteSpecifica_openspcoopToSica_eliminazioneImportParteComune;
	}

	public boolean isWSDL_XSD_accordiParteSpecifica_sicaToOpenspcoop_aggiuntaImportParteComune() {
		return checkOption("WSDL_XSD.accordiParteSpecifica.sicaToOpenspcoop.aggiuntaImportParteComune",this.WSDL_XSD_accordiParteSpecifica_sicaToOpenspcoop_aggiuntaImportParteComune);
	}

	public void setWSDL_XSD_accordiParteSpecifica_sicaToOpenspcoop_aggiuntaImportParteComune(
			boolean parteSpecifica_sicaToOpenspcoop_aggiuntaImportParteComune) {
		this.WSDL_XSD_accordiParteSpecifica_sicaToOpenspcoop_aggiuntaImportParteComune = parteSpecifica_sicaToOpenspcoop_aggiuntaImportParteComune;
	}
	
	public boolean isSICAClientCompatibility() {
		return this.SICAClientCompatibility;
	}

	public void setSICAClientCompatibility(boolean clientCompatibility) {
		this.SICAClientCompatibility = clientCompatibility;
	}
	
	public void setSICAClientEnabledOptions(String enabledOptions){
		if(enabledOptions==null)
			return;
		String [] options = enabledOptions.trim().split(",");
		if(options!=null && options.length>0){
			for(int i=0; i<options.length; i++){
				//System.out.println("ABILITO PER SICA ["+options[i].trim()+"]");
				this.SICAClientEnabledOptions.put(options[i].trim(), true);
			}
		}
	}
	
	public void setSICAClientDisabledOptions(String disabledOptions){
		if(disabledOptions==null)
			return;
		String [] options = disabledOptions.trim().split(",");
		if(options!=null && options.length>0){
			for(int i=0; i<options.length; i++){
				//System.out.println("DISABILITO PER SICA ["+options[i].trim()+"]");
				this.SICAClientDisabledOptions.put(options[i].trim(), true);
			}
		}
	}
	
	private boolean checkOption(String name,boolean defaultValue){
		if(this.SICAClientCompatibility){
			if(this.SICAClientEnabledOptions.containsKey(name)){
				//System.out.println("ABILITO FORCED ["+name+"]");
				return true;
			}
			else if(this.SICAClientDisabledOptions.containsKey(name)){
				//System.out.println("DISABILITO FORCED ["+name+"]");
				return false;
			}
		}
		//System.out.println("NORMALE VALORE ["+name+"");
		return defaultValue;
	}
}
