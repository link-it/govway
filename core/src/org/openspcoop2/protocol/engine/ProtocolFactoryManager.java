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

package org.openspcoop2.protocol.engine;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.protocol.basic.BasicFactory;
import org.openspcoop2.protocol.engine.mapping.InformazioniServizioURLMapping;
import org.openspcoop2.protocol.manifest.DefaultIntegrationError;
import org.openspcoop2.protocol.manifest.Integration;
import org.openspcoop2.protocol.manifest.IntegrationConfiguration;
import org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName;
import org.openspcoop2.protocol.manifest.IntegrationConfigurationResourceIdentificationMode;
import org.openspcoop2.protocol.manifest.IntegrationError;
import org.openspcoop2.protocol.manifest.Openspcoop2;
import org.openspcoop2.protocol.manifest.RestConfiguration;
import org.openspcoop2.protocol.manifest.SoapConfiguration;
import org.openspcoop2.protocol.manifest.SubContextMapping;
import org.openspcoop2.protocol.manifest.Web;
import org.openspcoop2.protocol.manifest.constants.ActorType;
import org.openspcoop2.protocol.manifest.constants.Costanti;
import org.openspcoop2.protocol.manifest.constants.FunctionType;
import org.openspcoop2.protocol.manifest.constants.MessageType;
import org.openspcoop2.protocol.manifest.constants.ResourceIdentificationType;
import org.openspcoop2.protocol.manifest.constants.ServiceBinding;
import org.openspcoop2.protocol.manifest.utils.XMLUtils;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ContestoCodificaEccezione;
import org.openspcoop2.protocol.sdk.constants.IDService;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.LivelloRilevanza;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.sdk.state.FunctionContextsCustom;
import org.openspcoop2.protocol.sdk.state.URLProtocolContext;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.ClassLoaderUtilities;
import org.openspcoop2.utils.resources.MapReader;
import org.slf4j.Logger;

/**
 * Protocol Factory Manager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class ProtocolFactoryManager {

	private static ProtocolFactoryManager protocolFactoryManager = null;
	public static synchronized void initialize(Logger log,ConfigurazionePdD configPdD,String protocolDefault) throws ProtocolException {
		if(ProtocolFactoryManager.protocolFactoryManager==null){
			ProtocolFactoryManager.protocolFactoryManager = new ProtocolFactoryManager(log,configPdD,protocolDefault,false);
			// Inizializzo anche Esiti.properties
			EsitiProperties.initialize(configPdD.getConfigurationDir(), log, configPdD.getLoader(), protocolFactoryManager.getProtocolFactories());
			// Inizializzo risorse statiche che avevano bisogno di altre informazioni (es. esiti properties)
			protocolFactoryManager.initStaticInstance();
		}
	}
	public static synchronized void initializeSingleProtocol(Logger log,ConfigurazionePdD configPdD,String protocol) throws ProtocolException {
		if(ProtocolFactoryManager.protocolFactoryManager==null){
			ProtocolFactoryManager.protocolFactoryManager = new ProtocolFactoryManager(log,configPdD,protocol,true);
			// Inizializzo anche Esiti.properties
			EsitiProperties.initialize(configPdD.getConfigurationDir(), log, configPdD.getLoader(), protocolFactoryManager.getProtocolFactories());
			// Inizializzo risorse statiche che avevano bisogno di altre informazioni (es. esiti properties)
			protocolFactoryManager.initStaticInstance();
		}
	}
	public static ProtocolFactoryManager getInstance() throws ProtocolException {
		if(ProtocolFactoryManager.protocolFactoryManager==null){
			throw new ProtocolException("ProtocolFactoryManager not initialized");
		}
		return ProtocolFactoryManager.protocolFactoryManager;
	}
	public static void updateLogger(Logger log){
		if(ProtocolFactoryManager.protocolFactoryManager!=null){
			ProtocolFactoryManager.protocolFactoryManager.log = log;
		}
	}
	
	public boolean isSupportedProtocolLogger(String protocol) throws ProtocolException {
		IProtocolFactory<?> p = this.getProtocolFactoryByName(protocol);
		return p.getManifest().getProtocol().isLogger();
	}
	
	public void initializeAllProtocols() throws ProtocolException{
		//System.out.println("Init All Factories ["+this.factories.size()+"] ...");
		if(this.factories!=null){
			Enumeration<IProtocolFactory<?>> factories = this.factories.elements();
			while (factories.hasMoreElements()) {
				IProtocolFactory<?> iProtocolFactory = (IProtocolFactory<?>) factories.nextElement();
				//System.out.println("Init ["+iProtocolFactory.getProtocol()+"] ...");
				
				// INFO SERVIZIO
				iProtocolFactory.getProtocol();
				iProtocolFactory.getInformazioniProtocol();
				Openspcoop2 manifest = iProtocolFactory.getManifest();
				iProtocolFactory.getConfigurazionePdD();
				
				// LOGGER
				iProtocolFactory.getLogger();
				if(manifest.getProtocol().isLogger()) {
					iProtocolFactory.getProtocolLogger();
				}
								
				// PROTOCOL BUILDER
				iProtocolFactory.createBustaBuilder(null);
				iProtocolFactory.createErroreApplicativoBuilder();
				iProtocolFactory.createEsitoBuilder();
				
				// PROTOCOL VALIDATOR
				iProtocolFactory.createValidatoreErrori(null);
				iProtocolFactory.createValidazioneSintattica(null);
				iProtocolFactory.createValidazioneSemantica(null);
				iProtocolFactory.createValidazioneConSchema(null);
				iProtocolFactory.createValidazioneDocumenti();
				iProtocolFactory.createValidazioneAccordi();
				
				// DIAGNOSTICI 
				iProtocolFactory.createDiagnosticDriver();
				iProtocolFactory.createDiagnosticProducer();
				iProtocolFactory.createDiagnosticSerializer();
				
				// TRACCE
				iProtocolFactory.createTracciaDriver();
				iProtocolFactory.createTracciaProducer();
				iProtocolFactory.createTracciaSerializer();
								
				// ARCHIVE
				iProtocolFactory.createArchive();
				
				// CONFIG 
				List<String> versioni = iProtocolFactory.createProtocolConfiguration().getVersioni();
				for (String version : versioni) {
					iProtocolFactory.createProtocolVersionManager(version);	
				}
				iProtocolFactory.createProtocolManager();
				ITraduttore traduttore = iProtocolFactory.createTraduttore();
				traduttore.toString(CodiceErroreCooperazione.AZIONE);
				traduttore.toString(ContestoCodificaEccezione.INTESTAZIONE);
				traduttore.toString(Inoltro.CON_DUPLICATI);
				traduttore.toString(LivelloRilevanza.INFO);
				traduttore.toString(ProfiloDiCollaborazione.SINCRONO);
				traduttore.toString(TipoOraRegistrazione.LOCALE);
				
				// CONSOLE
				iProtocolFactory.createDynamicConfigurationConsole();
				
				//System.out.println("Init ["+iProtocolFactory.getProtocol()+"] ok");
			}
		}
	}
	
	
	private MapReader<String, Openspcoop2> manifests = null;
	@SuppressWarnings("unused")
	private MapReader<String, URL> manifestURLs = null;
	private MapReader<String, IProtocolFactory<?>> factories = null;
	private StringBuilder protocolLoaded = new StringBuilder();
	private String protocolDefault = null;
	private MapReader<String, List<String>> tipiSoggettiValidi = null;
	private MapReader<String, String> tipiSoggettiDefault = null;
	private MapReader<String, List<String>> tipiServiziValidi_soap = null;
	private MapReader<String, String> tipiServiziDefault_soap = null;
	private MapReader<String, List<String>> tipiServiziValidi_rest = null;
	private MapReader<String, String> tipiServiziDefault_rest = null;
	private MapReader<String, List<String>> versioniValide = null;
	private MapReader<String, String> versioniDefault = null;
	private Logger log = null;
	ProtocolFactoryManager(Logger log,ConfigurazionePdD configPdD,String protocolDefault, boolean searchSingleManifest) throws ProtocolException {
		try {

			Map<String, Openspcoop2> tmp_manifests = new HashMap<String, Openspcoop2>();
			Map<String, URL> tmp_manifestURLs = new HashMap<String, URL>();
			Map<String, IProtocolFactory<?>> tmp_factories = new HashMap<String, IProtocolFactory<?>>();
			
			Map<String, List<String>> tmp_tipiSoggettiValidi = new HashMap<String, List<String>>();
			Map<String, String> tmp_tipiSoggettiDefault = new HashMap<String, String>();
			
			Map<String, List<String>> tmp_tipiServiziValidi_soap = new HashMap<String, List<String>>();
			Map<String, String> tmp_tipiServiziDefault_soap = new HashMap<String, String>();
			Map<String, List<String>> tmp_tipiServiziValidi_rest = new HashMap<String, List<String>>();
			Map<String, String> tmp_tipiServiziDefault_rest = new HashMap<String, String>();
			
			Map<String, List<String>> tmp_versioniValide = new HashMap<String, List<String>>();
			Map<String, String> tmp_versioniDefault = new HashMap<String, String>();
			
			this.log = configPdD.getLog();
			this.protocolDefault = protocolDefault;
			
			configPdD.getLog().debug("Init ProtocolFactoryManager ...");
			
			// Loaded Manifest
			if(searchSingleManifest){
				// Quando si recuper il getClassLoader, nei command line, non viene tornato lo stesso loader delle classi per motivi di sicurezza.
				// Vedi API del metodo getClassLoader()
				URL pluginURL = ProtocolFactoryManager.class.getResource("/"+Costanti.MANIFEST_OPENSPCOOP2); // utile nei command line.
				loadManifest(configPdD, pluginURL, false, tmp_manifests, tmp_manifestURLs);
			}
			else{
				// 1. Cerco nel classloader (funziona per jboss5.x)
				Enumeration<URL> en = ProtocolFactoryManager.class.getClassLoader().getResources("/"+Costanti.MANIFEST_OPENSPCOOP2);
				while(en.hasMoreElements()){
					URL pluginURL = en.nextElement();
					loadManifest(configPdD, pluginURL, false, tmp_manifests, tmp_manifestURLs);
				}
				
				if(tmp_manifests.size()<=0){
					// 2. (funziona per jboss4.x) ma vengono forniti jar duplicati, quelli dentro ear e quelli dentro tmp.
					en = ProtocolFactoryManager.class.getClassLoader().getResources(Costanti.MANIFEST_OPENSPCOOP2);
					while(en.hasMoreElements()){
						URL pluginURL = en.nextElement();
						loadManifest(configPdD, pluginURL, true, tmp_manifests, tmp_manifestURLs);
					}
				}
			}
			
			if(tmp_manifests.size()<=0){
				throw new Exception("Protocol plugins not found");
			}
			
			// Validate Manifest Loaded
			configPdD.getLog().debug("Validate Manifests ...");
			validateProtocolFactoryLoaded(tmp_manifests,
					tmp_tipiSoggettiValidi,tmp_tipiSoggettiDefault,
					tmp_tipiServiziValidi_soap, tmp_tipiServiziDefault_soap,
					tmp_tipiServiziValidi_rest, tmp_tipiServiziDefault_rest,
					tmp_versioniValide, tmp_versioniDefault);
			configPdD.getLog().debug("Validate Manifests ok");
			
			// Init protocol factory
			for (String protocolManifest : tmp_manifests.keySet()) {

				configPdD.getLog().debug("Init ProtocolFactory for protocol ["+protocolManifest+"] ...");
				Openspcoop2 manifestOpenspcoop2 = tmp_manifests.get(protocolManifest);				
				
				// Factory
				IProtocolFactory<?> p = this.getProtocolFactoryEngine(manifestOpenspcoop2);
				p.init(configPdD.getLog(), protocolManifest, configPdD,manifestOpenspcoop2);
				if(!p.createValidazioneConSchema(null).initialize(OpenSPCoop2MessageFactory.getDefaultMessageFactory())){
					throw new Exception("[protocol:"+protocolManifest+"] Inizialize with error for ValidazioneConSchema");
				}
				tmp_factories.put(protocolManifest, p);
				
				// Lista di protocolli caricati
				if(this.protocolLoaded.length()>0){
					this.protocolLoaded.append(",");
				}
				this.protocolLoaded.append(protocolManifest);
				
				// Carico url-mapping
				InformazioniServizioURLMapping.initMappingProperties(p);
				
				// Info di debug
				StringBuilder context = new StringBuilder();
				if(manifestOpenspcoop2.getWeb().getEmptyContext()!=null && manifestOpenspcoop2.getWeb().getEmptyContext().getEnabled()){
					context.append("@EMPTY-CONTEXT@");
				}
				for (int i = 0; i < manifestOpenspcoop2.getWeb().sizeContextList(); i++) {
					if(context.length()>0){
						context.append(",");
					}
					context.append(manifestOpenspcoop2.getWeb().getContext(i).getName());
				}
				log.info("Protocol loaded with id["+protocolManifest+"] factory["+manifestOpenspcoop2.getProtocol().getFactory()+"] contexts["+context.toString()+"]");
				
				configPdD.getLog().debug("Init ProtocolFactory for protocol ["+protocolManifest+"] ok");
			}
			
			// init
			this.manifests = new MapReader<String, Openspcoop2>(tmp_manifests,true);
			this.manifestURLs = new MapReader<String, URL>(tmp_manifestURLs,true);
			this.factories = new MapReader<String, IProtocolFactory<?>>(tmp_factories,true);
			
			this.tipiSoggettiValidi = new MapReader<String, List<String>>(tmp_tipiSoggettiValidi,true);
			this.tipiSoggettiDefault = new MapReader<String, String>(tmp_tipiSoggettiDefault,true);
			
			this.tipiServiziValidi_soap = new MapReader<String, List<String>>(tmp_tipiServiziValidi_soap,true);
			this.tipiServiziDefault_soap = new MapReader<String, String>(tmp_tipiServiziDefault_soap,true);
			
			this.tipiServiziValidi_rest = new MapReader<String, List<String>>(tmp_tipiServiziValidi_rest,true);
			this.tipiServiziDefault_rest = new MapReader<String, String>(tmp_tipiServiziDefault_rest,true);
			
			this.versioniValide = new MapReader<String, List<String>>(tmp_versioniValide,true);
			this.versioniDefault = new MapReader<String, String>(tmp_versioniDefault,true);
			
		} catch (Exception e) {
			configPdD.getLog().error("Init ProtocolFactoryManager failed: "+e.getMessage(),e);
			throw new ProtocolException("Inizializzazione ProtocolFactoryManager fallita: " + e, e);
		}
	}
	
	private void loadManifest(ConfigurazionePdD configPdD,URL pluginURL,boolean filtraSenzaErroreProtocolloGiaCaricato,
			Map<String, Openspcoop2> tmp_manifests, Map<String, URL> tmp_manifestURLs) throws Exception{
		// Manifest
		configPdD.getLog().debug("Analyze manifest ["+pluginURL.toString()+"] ...");
		
		InputStream openStream = null;
		byte[] manifest = null;
		try{
			openStream = pluginURL.openStream();
			manifest = Utilities.getAsByteArray(openStream);
		}finally{
			try{
				if(openStream!=null) {
					openStream.close();
				}
			}catch(Exception e){
				// close
			}
		}
		//System.out.println("CARICATO ["+new String(manifest)+"]");

		configPdD.getLog().debug("Analyze manifest ["+pluginURL.toString()+"] convertToOpenSPCoop2Manifest...");
		Openspcoop2 manifestOpenspcoop2 = XMLUtils.getOpenspcoop2Manifest(configPdD.getLog(),manifest);
		String protocollo = manifestOpenspcoop2.getProtocol().getName();
		configPdD.getLog().debug("Analyze manifest ["+pluginURL.toString()+"] with protocolName: ["+protocollo+"]");
		if(tmp_manifests.containsKey(protocollo)){
		
			URL urlGiaPresente = tmp_manifestURLs.get(protocollo);
			if(filtraSenzaErroreProtocolloGiaCaricato){
				configPdD.getLog().warn("ProtocolName ["+protocollo+"] is same for more plugin ["+pluginURL.toString()+"] and ["+urlGiaPresente.toURI()+"]");
			}
			else{
				throw new Exception("ProtocolName ["+protocollo+"] is same for more plugin ["+pluginURL.toString()+"] and ["+urlGiaPresente.toURI()+"]");
			}
			
		}
		tmp_manifests.put(protocollo, manifestOpenspcoop2);
		tmp_manifestURLs.put(protocollo, pluginURL);
		configPdD.getLog().debug("Analyze manifest ["+pluginURL.toString()+"] with success");
	}
	
	private void validateProtocolFactoryLoaded(Map<String, Openspcoop2> tmp_manifests,
			Map<String, List<String>> tmp_tipiSoggettiValidi,Map<String, String> tmp_tipiSoggettiDefault,
			Map<String, List<String>> tmp_tipiServiziValidi_soap, Map<String, String> tmp_tipiServiziDefault_soap,
			Map<String, List<String>> tmp_tipiServiziValidi_rest, Map<String, String> tmp_tipiServiziDefault_rest,
			Map<String, List<String>> tmp_versioniValide,Map<String, String> tmp_versioniDefault) throws Exception{
				
		// 1. controllare che solo uno possieda il contesto vuoto
		String protocolContextEmpty = null;
		for (String protocolManifest : tmp_manifests.keySet()) {
			Openspcoop2 manifestOpenspcoop2 = tmp_manifests.get(protocolManifest);
			if(manifestOpenspcoop2.getWeb().getEmptyContext()!=null && manifestOpenspcoop2.getWeb().getEmptyContext().getEnabled()){
				if(protocolContextEmpty==null){
					protocolContextEmpty = protocolManifest;
				}
				else{
					throw new Exception("Protocol ["+protocolContextEmpty+"] and ["+protocolManifest+"] with empty context. Only one is permitted");
				}
			}
			
		}
		
		// 2. controllare che i contesti siano tutti diversi
		Map<String, String> mappingContextToProtocol = new HashMap<String, String>();
		for (String protocolManifest : tmp_manifests.keySet()) {
			Openspcoop2 manifestOpenspcoop2 = tmp_manifests.get(protocolManifest);
			
			for (int i = 0; i < manifestOpenspcoop2.getWeb().sizeContextList(); i++) {
				String context = manifestOpenspcoop2.getWeb().getContext(i).getName();
				if(!mappingContextToProtocol.containsKey(context)){
					mappingContextToProtocol.put(context, protocolManifest);
				}
				else{
					throw new Exception("Protocol ["+mappingContextToProtocol.get(context)+"] and ["+protocolManifest+"] with same context ["+context+"]");
				}
			}
			
		}
		
		// 2.b. controllare che le label siano tutti diversi
		Map<String, String> mappinLabelToProtocol = new HashMap<String, String>();
		for (String protocolManifest : tmp_manifests.keySet()) {
			Openspcoop2 manifestOpenspcoop2 = tmp_manifests.get(protocolManifest);
			
			String label = manifestOpenspcoop2.getProtocol().getLabel();
			if(!mappinLabelToProtocol.containsKey(label)){
				mappinLabelToProtocol.put(label, protocolManifest);
			}
			else{
				throw new Exception("Protocol ["+mappinLabelToProtocol.get(label)+"] and ["+protocolManifest+"] with same label ["+label+"]");
			}
			
		}
		
		// 3. controllare e inizializzare i tipi di soggetti in modo che siano tutti diversi
		Map<String, String> mappingTipiSoggettiToProtocol = new HashMap<String, String>();
		for (String protocolManifest : tmp_manifests.keySet()) {
			Openspcoop2 manifestOpenspcoop2 = tmp_manifests.get(protocolManifest);
			
			int size = manifestOpenspcoop2.getRegistry().getOrganization().getTypes().sizeTypeList();
			if(size<=0){
				throw new Exception("Organization type not defined for protocol ["+protocolManifest+"]");
			}
						
			for (int i = 0; i < size; i++) {
				String tipo = manifestOpenspcoop2.getRegistry().getOrganization().getTypes().getType(i).getName();
				if(!mappingTipiSoggettiToProtocol.containsKey(tipo)){
					mappingTipiSoggettiToProtocol.put(tipo, protocolManifest);
					
					List<String> tipiSoggettiPerProtocollo = null;
					if(tmp_tipiSoggettiValidi.containsKey(protocolManifest)){
						tipiSoggettiPerProtocollo = tmp_tipiSoggettiValidi.remove(protocolManifest);
					}
					else{
						tipiSoggettiPerProtocollo = new ArrayList<String>();
					}
					tipiSoggettiPerProtocollo.add(tipo);
					tmp_tipiSoggettiValidi.put(protocolManifest, tipiSoggettiPerProtocollo);
				}
				else{
					throw new Exception("Protocol ["+mappingTipiSoggettiToProtocol.get(tipo)+"] and ["+protocolManifest+"] with same subject type ["+tipo+"]");
				}
			}
			
			for (int i = 0; i < size; i++) {
				String protocolType = manifestOpenspcoop2.getRegistry().getOrganization().getTypes().getType(i).getProtocol();
				if(protocolType!=null) {
					int count = 0;
					for (int j = 0; j < size; j++) {
						String protocolTypeCheck = manifestOpenspcoop2.getRegistry().getOrganization().getTypes().getType(j).getProtocol();
						if(protocolTypeCheck!=null) {
							if(protocolTypeCheck.equals(protocolType)) {
								count++;
							}
						}
					}
					if(count>1) {
						throw new Exception("Protocol ["+protocolManifest+"] with same subject 'protocol conversion type' ["+protocolType+"]");
					}
				}
			}
			
			String tipoDefault = manifestOpenspcoop2.getRegistry().getOrganization().getTypes().getType(0).getName();
			if(tmp_tipiSoggettiValidi.get(protocolManifest).contains(tipoDefault)==false){
				throw new Exception("Unknown default subject type ["+tipoDefault+"] defined in protocol ["+protocolManifest+"]");
			}
			tmp_tipiSoggettiDefault.put(protocolManifest, tipoDefault);
			
		}
		
		
		// 4. controllare e inizializzare i tipi di servizi in modo che siano tutti diversi
		String tipoServizioDefaultSoap = null;
		String tipoServizioDefaultRest = null;
		Map<String, String> mappingTipiServiziToProtocol = new HashMap<String, String>();
		for (String protocolManifest : tmp_manifests.keySet()) {
			Openspcoop2 manifestOpenspcoop2 = tmp_manifests.get(protocolManifest);
			
			int size = manifestOpenspcoop2.getRegistry().getService().getTypes().sizeTypeList();
			if(size<=0){
				throw new Exception("Service type not defined for protocol ["+protocolManifest+"]");
			}
			
			for (int i = 0; i < size; i++) {
				String tipo = manifestOpenspcoop2.getRegistry().getService().getTypes().getType(i).getName();
				ServiceBinding serviceBinding = manifestOpenspcoop2.getRegistry().getService().getTypes().getType(i).getBinding();
				if(!mappingTipiServiziToProtocol.containsKey(tipo)){
					mappingTipiServiziToProtocol.put(tipo, protocolManifest);
					
					List<String> tipiServiziPerProtocollo_soap = null;
					if(tmp_tipiServiziValidi_soap.containsKey(protocolManifest)){
						tipiServiziPerProtocollo_soap = tmp_tipiServiziValidi_soap.get(protocolManifest);
					}
					else{
						tipiServiziPerProtocollo_soap = new ArrayList<String>();
						tmp_tipiServiziValidi_soap.put(protocolManifest, tipiServiziPerProtocollo_soap);
					}
					
					List<String> tipiServiziPerProtocollo_rest = null;
					if(tmp_tipiServiziValidi_rest.containsKey(protocolManifest)){
						tipiServiziPerProtocollo_rest = tmp_tipiServiziValidi_rest.get(protocolManifest);
					}
					else{
						tipiServiziPerProtocollo_rest = new ArrayList<String>();
						tmp_tipiServiziValidi_rest.put(protocolManifest, tipiServiziPerProtocollo_rest);
					}
					
					if(serviceBinding==null || ServiceBinding.SOAP.equals(serviceBinding)){
						tipiServiziPerProtocollo_soap.add(tipo);
						if(tipoServizioDefaultSoap==null){
							tipoServizioDefaultSoap = tipo;
						}
					}
					
					if(serviceBinding==null || ServiceBinding.REST.equals(serviceBinding)){
						tipiServiziPerProtocollo_rest.add(tipo);
						if(tipoServizioDefaultRest==null){
							tipoServizioDefaultRest = tipo;
						}
					}
				}
				else{
					throw new Exception("Protocol ["+mappingTipiServiziToProtocol.get(tipo)+"] and ["+protocolManifest+"] with same service type ["+tipo+"]");
				}
			}
			
			for (int i = 0; i < size; i++) {
				String protocolType = manifestOpenspcoop2.getRegistry().getService().getTypes().getType(i).getProtocol();
				if(protocolType!=null) {
					int count = 0;
					for (int j = 0; j < size; j++) {
						String protocolTypeCheck = manifestOpenspcoop2.getRegistry().getService().getTypes().getType(j).getProtocol();
						if(protocolTypeCheck!=null) {
							if(protocolTypeCheck.equals(protocolType)) {
								count++;
							}
						}
					}
					if(count>1) {
						throw new Exception("Protocol ["+protocolManifest+"] with same service 'protocol conversion type' ["+protocolType+"]");
					}
				}
			}
			
			if(tipoServizioDefaultSoap!=null){
				tmp_tipiServiziDefault_soap.put(protocolManifest, tipoServizioDefaultSoap);
			}
			if(tipoServizioDefaultRest!=null){
				tmp_tipiServiziDefault_rest.put(protocolManifest, tipoServizioDefaultRest);
			}
			
		}
		
		
		
		// 5. controllare e inizializzare le versioni dei protocolli
		for (String protocolManifest : tmp_manifests.keySet()) {
			Openspcoop2 manifestOpenspcoop2 = tmp_manifests.get(protocolManifest);
			
			int size = manifestOpenspcoop2.getRegistry().getVersions().sizeVersionList();
			if(size<=0){
				throw new Exception("Version not defined for protocol ["+protocolManifest+"]");
			}
			
			for (int i = 0; i < size; i++) {
				String version = manifestOpenspcoop2.getRegistry().getVersions().getVersion(i).getName();
				
				List<String> versioniPerProtocollo = null;
				if(tmp_versioniValide.containsKey(protocolManifest)){
					versioniPerProtocollo = tmp_versioniValide.remove(protocolManifest);
				}
				else{
					versioniPerProtocollo = new ArrayList<String>();
				}
				versioniPerProtocollo.add(version);
				tmp_versioniValide.put(protocolManifest, versioniPerProtocollo);

			}
			
			String versioneDefault = manifestOpenspcoop2.getRegistry().getVersions().getVersion(0).getName();
			if(tmp_versioniValide.get(protocolManifest).contains(versioneDefault)==false){
				throw new Exception("Unknown default version ["+versioneDefault+"] defined in protocol ["+protocolManifest+"]");
			}
			tmp_versioniDefault.put(protocolManifest, versioneDefault);
		}
		
		
		
		// 6. controllare e inizializzare i service binding di default
		for (String protocolManifest : tmp_manifests.keySet()) {
			Openspcoop2 manifestOpenspcoop2 = tmp_manifests.get(protocolManifest);
			
			if(manifestOpenspcoop2.getBinding()==null){
				throw new Exception("Binding not defined for protocol ["+protocolManifest+"]");
			}
			if(manifestOpenspcoop2.getBinding().getSoap()==null && manifestOpenspcoop2.getBinding().getRest()==null){
				throw new Exception("Binding (soap/rest) not defined for protocol ["+protocolManifest+"]");
			}
			if(manifestOpenspcoop2.getBinding().getSoap()!=null && manifestOpenspcoop2.getBinding().getRest()!=null){
				if(manifestOpenspcoop2.getBinding().getDefault()==null)
					throw new Exception("Unknown default binding for protocol ["+protocolManifest+"]");
			}
			
			boolean soapEnabled = false;
			if(manifestOpenspcoop2.getBinding().getSoap()!=null){
				if(!manifestOpenspcoop2.getBinding().getSoap().isSoap11() && !manifestOpenspcoop2.getBinding().getSoap().isSoap12()){
					throw new Exception("Binding Soap for protocol ["+protocolManifest+"]: none of the soap versions is enabled");
				}
				if(manifestOpenspcoop2.getBinding().getSoap().isSoap11Mtom() && !manifestOpenspcoop2.getBinding().getSoap().isSoap11()){
					throw new Exception("Binding Soap for protocol ["+protocolManifest+"]: if soap 1.1 is not enabled you can not enable MTOM");
				}
				if(manifestOpenspcoop2.getBinding().getSoap().isSoap11WithAttachments() && !manifestOpenspcoop2.getBinding().getSoap().isSoap11()){
					throw new Exception("Binding Soap for protocol ["+protocolManifest+"]: if soap 1.1 is not enabled you can not enable attachments");
				}
				if(manifestOpenspcoop2.getBinding().getSoap().isSoap12Mtom() && !manifestOpenspcoop2.getBinding().getSoap().isSoap12()){
					throw new Exception("Binding Soap for protocol ["+protocolManifest+"]: if soap 1.2 is not enabled you can not enable MTOM");
				}
				if(manifestOpenspcoop2.getBinding().getSoap().isSoap12WithAttachments() && !manifestOpenspcoop2.getBinding().getSoap().isSoap12()){
					throw new Exception("Binding Soap for protocol ["+protocolManifest+"]: if soap 1.2 is not enabled you can not enable attachments");
				}
				
				if(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError()==null){
					throw new Exception("Binding Soap for protocol ["+protocolManifest+"]: it has not defined the configuration for error handling");
				}
				if(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getInternal()==null){
					throw new Exception("Binding Soap for protocol ["+protocolManifest+"]: it has not defined the configuration for internal error handling");
				}
				if(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getInternal().getDefault()==null){
					throw new Exception("Binding Soap for protocol ["+protocolManifest+"]: it has not defined the default configuration for internal error handling");
				}
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getInternal().getDefault(),"default");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getInternal().getAuthentication(),"authentication");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getInternal().getAuthorization(),"authorization");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getInternal().getNotFound(),"notFound");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getInternal().getBadRequest(),"badRequest");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getInternal().getConflict(),"conflict");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getInternal().getLimitExceeded(),"limitExceeded");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getInternal().getTooManyRequests(),"tooManyRequests");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getInternal().getServiceUnavailable(),"serviceUnavailable");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getInternal().getEndpointRequestTimedOut(),"endpointRequestTimedOut");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getInternal().getBadResponse(),"badResponse");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getInternal().getInternalRequestError(),"internalRequestError");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getInternal().getInternalResponseError(),"internalResponseError");
				if(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getExternal()==null){
					throw new Exception("Binding Soap for protocol ["+protocolManifest+"]: it has not defined the configuration for external error handling");
				}
				if(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getExternal().getDefault()==null){
					throw new Exception("Binding Soap for protocol ["+protocolManifest+"]: it has not defined the default configuration for external error handling");
				}
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getExternal().getDefault(),"default");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getExternal().getAuthentication(),"authentication");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getExternal().getAuthorization(),"authorization");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getExternal().getNotFound(),"notFound");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getExternal().getBadRequest(),"badRequest");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getExternal().getConflict(),"conflict");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getExternal().getLimitExceeded(),"limitExceeded");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getExternal().getTooManyRequests(),"tooManyRequests");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getExternal().getServiceUnavailable(),"serviceUnavailable");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getExternal().getEndpointRequestTimedOut(),"endpointRequestTimedOut");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getExternal().getBadResponse(),"badResponse");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getExternal().getInternalRequestError(),"internalRequestError");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getSoap().getIntegrationError().getExternal().getInternalResponseError(),"internalResponseError");
				
				if(manifestOpenspcoop2.getBinding().getSoap().getMediaTypeCollection()==null){
					throw new Exception("Binding Soap for protocol ["+protocolManifest+"]: media type collection undefined");
				}
				if(manifestOpenspcoop2.getBinding().getSoap().getMediaTypeCollection().sizeMediaTypeList()<=0 && 
						manifestOpenspcoop2.getBinding().getSoap().getMediaTypeCollection().getDefault()==null && 
								manifestOpenspcoop2.getBinding().getSoap().getMediaTypeCollection().getUndefined()==null){
					throw new Exception("Binding Soap for protocol ["+protocolManifest+"]: media type collection undefined");
				}
				
				soapEnabled = true;
			}
			
			boolean restEnabled = false;
			if(manifestOpenspcoop2.getBinding().getRest()!=null){
				if(!manifestOpenspcoop2.getBinding().getRest().isBinary() && 
						!manifestOpenspcoop2.getBinding().getRest().isJson() && 
						!manifestOpenspcoop2.getBinding().getRest().isXml() && 
						!manifestOpenspcoop2.getBinding().getRest().isMimeMultipart()){
					throw new Exception("Binding Rest for protocol ["+protocolManifest+"]: none of the message types is enabled");
				}
				
				if(manifestOpenspcoop2.getBinding().getRest().getIntegrationError()==null){
					throw new Exception("Binding Rest for protocol ["+protocolManifest+"]: it has not defined the configuration for error handling");
				}
				if(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getInternal()==null){
					throw new Exception("Binding Rest for protocol ["+protocolManifest+"]: it has not defined the configuration for internal error handling");
				}
				if(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getInternal().getDefault()==null){
					throw new Exception("Binding Rest for protocol ["+protocolManifest+"]: it has not defined the default configuration for internal error handling");
				}
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getInternal().getDefault(),"default");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getInternal().getAuthentication(),"authentication");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getInternal().getAuthorization(),"authorization");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getInternal().getNotFound(),"notFound");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getInternal().getBadRequest(),"badRequest");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getInternal().getConflict(),"conflict");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getInternal().getLimitExceeded(),"limitExceeded");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getInternal().getTooManyRequests(),"tooManyRequests");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getInternal().getServiceUnavailable(),"serviceUnavailable");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getInternal().getEndpointRequestTimedOut(),"endpointRequestTimedOut");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getInternal().getBadResponse(),"badResponse");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getInternal().getInternalRequestError(),"internalRequestError");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getInternal().getInternalResponseError(),"internalResponseError");
				if(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getExternal()==null){
					throw new Exception("Binding Rest for protocol ["+protocolManifest+"]: it has not defined the configuration for external error handling");
				}
				if(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getExternal().getDefault()==null){
					throw new Exception("Binding Rest for protocol ["+protocolManifest+"]: it has not defined the default configuration for external error handling");
				}
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getExternal().getDefault(),"default");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getExternal().getAuthentication(),"authentication");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getExternal().getAuthorization(),"authorization");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getExternal().getNotFound(),"notFound");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getExternal().getBadRequest(),"badRequest");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getExternal().getConflict(),"conflict");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getExternal().getLimitExceeded(),"limitExceeded");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getExternal().getTooManyRequests(),"tooManyRequests");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getExternal().getServiceUnavailable(),"serviceUnavailable");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getExternal().getEndpointRequestTimedOut(),"endpointRequestTimedOut");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getExternal().getBadResponse(),"badResponse");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getExternal().getInternalRequestError(),"internalRequestError");
				checkHttpReturnCode(manifestOpenspcoop2.getBinding().getRest().getIntegrationError().getExternal().getInternalResponseError(),"internalResponseError");	
				
				if(manifestOpenspcoop2.getBinding().getRest().getMediaTypeCollection()==null){
					throw new Exception("Binding Rest for protocol ["+protocolManifest+"]: media type collection undefined");
				}
				if(manifestOpenspcoop2.getBinding().getRest().getMediaTypeCollection().sizeMediaTypeList()<=0 && 
						manifestOpenspcoop2.getBinding().getRest().getMediaTypeCollection().getDefault()==null && 
								manifestOpenspcoop2.getBinding().getRest().getMediaTypeCollection().getUndefined()==null){
					throw new Exception("Binding Rest for protocol ["+protocolManifest+"]: media type collection undefined");
				}
				
				restEnabled = true;
			}
			
			for (int i = 0; i < manifestOpenspcoop2.getWeb().sizeContextList(); i++) {
				String context = manifestOpenspcoop2.getWeb().getContext(i).getName();
				ServiceBinding serviceBinding = manifestOpenspcoop2.getWeb().getContext(i).getBinding();
				if(serviceBinding!=null){
					if(ServiceBinding.REST.equals(serviceBinding)){
						if(!restEnabled){
							throw new Exception("Binding Rest for protocol ["+protocolManifest+"] is disabled: cannot define restriction for web context "+context);
						}
					}
					else{
						if(!soapEnabled){
							throw new Exception("Binding Soap for protocol ["+protocolManifest+"] is disabled: cannot define restriction for web context "+context);
						}
					}
				}
				for (int j = 0; j < manifestOpenspcoop2.getWeb().getContext(i).sizeSubContextList(); j++) {
					SubContextMapping subContext = manifestOpenspcoop2.getWeb().getContext(i).getSubContext(j);
					checkMessageType(context,subContext.getBase(),subContext.getMessageType(),
							manifestOpenspcoop2.getBinding().getSoap(),
							manifestOpenspcoop2.getBinding().getRest());
				}
				if(manifestOpenspcoop2.getWeb().getContext(i).getEmptySubContext()!=null){
					checkMessageType(context,Costanti.CONTEXT_EMPTY,manifestOpenspcoop2.getWeb().getContext(i).getEmptySubContext().getMessageType(),
							manifestOpenspcoop2.getBinding().getSoap(),
							manifestOpenspcoop2.getBinding().getRest());
				}
				
				if(manifestOpenspcoop2.getWeb().getContext(i).getSoapMediaTypeCollection()!=null){
					if(manifestOpenspcoop2.getWeb().getContext(i).getSoapMediaTypeCollection().sizeMediaTypeList()>0 || 
							manifestOpenspcoop2.getWeb().getContext(i).getSoapMediaTypeCollection().getDefault()!=null || 
									manifestOpenspcoop2.getWeb().getContext(i).getSoapMediaTypeCollection().getUndefined()!=null){
						if(soapEnabled==false){
							throw new ProtocolException("(Context:"+context+") SoapMediaType not supported; Soap disabled");
						}
					}
				}
				if(manifestOpenspcoop2.getWeb().getContext(i).getRestMediaTypeCollection()!=null){
					if(manifestOpenspcoop2.getWeb().getContext(i).getRestMediaTypeCollection().sizeMediaTypeList()>0 || 
							manifestOpenspcoop2.getWeb().getContext(i).getRestMediaTypeCollection().getDefault()!=null || 
									manifestOpenspcoop2.getWeb().getContext(i).getRestMediaTypeCollection().getUndefined()!=null){
						if(restEnabled==false){
							throw new ProtocolException("(Context:"+context+") RestMediaType not supported; Rest disabled");
						}
					}
				}
			}
			
			if(manifestOpenspcoop2.getWeb().getEmptyContext()!=null){
				ServiceBinding serviceBinding = manifestOpenspcoop2.getWeb().getEmptyContext().getBinding();
				if(serviceBinding!=null){
					if(ServiceBinding.REST.equals(serviceBinding)){
						if(!restEnabled){
							throw new Exception("Binding Rest for protocol ["+protocolManifest+"] is disabled: cannot define restriction for empty web context");
						}
					}
					else{
						if(!soapEnabled){
							throw new Exception("Binding Soap for protocol ["+protocolManifest+"] is disabled: cannot define restriction for empty web context");
						}
					}
				}
				for (int j = 0; j < manifestOpenspcoop2.getWeb().getEmptyContext().sizeSubContextList(); j++) {
					SubContextMapping subContext = manifestOpenspcoop2.getWeb().getEmptyContext().getSubContext(j);
					checkMessageType(Costanti.CONTEXT_EMPTY,subContext.getBase(),subContext.getMessageType(),
							manifestOpenspcoop2.getBinding().getSoap(),
							manifestOpenspcoop2.getBinding().getRest());
				}
				if(manifestOpenspcoop2.getWeb().getEmptyContext().getEmptySubContext()!=null){
					checkMessageType(Costanti.CONTEXT_EMPTY,Costanti.CONTEXT_EMPTY,manifestOpenspcoop2.getWeb().getEmptyContext().getEmptySubContext().getMessageType(),
							manifestOpenspcoop2.getBinding().getSoap(),
							manifestOpenspcoop2.getBinding().getRest());
				}
				
				if(manifestOpenspcoop2.getWeb().getEmptyContext().getSoapMediaTypeCollection()!=null){
					if(manifestOpenspcoop2.getWeb().getEmptyContext().getSoapMediaTypeCollection().sizeMediaTypeList()>0 || 
							manifestOpenspcoop2.getWeb().getEmptyContext().getSoapMediaTypeCollection().getDefault()!=null || 
									manifestOpenspcoop2.getWeb().getEmptyContext().getSoapMediaTypeCollection().getUndefined()!=null){
						if(soapEnabled==false){
							throw new ProtocolException("(Context:"+Costanti.CONTEXT_EMPTY+") SoapMediaType not supported; Soap disabled");
						}
					}
				}
				if(manifestOpenspcoop2.getWeb().getEmptyContext().getRestMediaTypeCollection()!=null){
					if(manifestOpenspcoop2.getWeb().getEmptyContext().getRestMediaTypeCollection().sizeMediaTypeList()>0 || 
							manifestOpenspcoop2.getWeb().getEmptyContext().getRestMediaTypeCollection().getDefault()!=null || 
									manifestOpenspcoop2.getWeb().getEmptyContext().getRestMediaTypeCollection().getUndefined()!=null){
						if(restEnabled==false){
							throw new ProtocolException("(Context:"+Costanti.CONTEXT_EMPTY+") RestMediaType not supported; Rest disabled");
						}
					}
				}
			}
			
			for (int i = 0; i < manifestOpenspcoop2.getRegistry().getService().getTypes().sizeTypeList(); i++) {
				String serviceType = manifestOpenspcoop2.getRegistry().getService().getTypes().getType(i).getName();
				ServiceBinding serviceBinding = manifestOpenspcoop2.getRegistry().getService().getTypes().getType(i).getBinding();
				if(serviceBinding!=null){
					if(ServiceBinding.REST.equals(serviceBinding)){
						if(!restEnabled){
							throw new Exception("Binding Rest for protocol ["+protocolManifest+"] is disabled: cannot define restriction for service type "+serviceType);
						}
					}
					else{
						if(!soapEnabled){
							throw new Exception("Binding Soap for protocol ["+protocolManifest+"] is disabled: cannot define restriction for service type "+serviceType);
						}
					}
				}
				
				if( manifestOpenspcoop2.getRegistry().getService().getTypes().getType(i).getSoapMediaTypeCollection()!=null){
					if( manifestOpenspcoop2.getRegistry().getService().getTypes().getType(i).getSoapMediaTypeCollection().sizeMediaTypeList()>0 || 
							 manifestOpenspcoop2.getRegistry().getService().getTypes().getType(i).getSoapMediaTypeCollection().getDefault()!=null || 
									 manifestOpenspcoop2.getRegistry().getService().getTypes().getType(i).getSoapMediaTypeCollection().getUndefined()!=null){
						if(soapEnabled==false){
							throw new ProtocolException("(ServiceType:"+serviceType+") SoapMediaType not supported; Soap disabled");
						}
					}
				}
				if( manifestOpenspcoop2.getRegistry().getService().getTypes().getType(i).getRestMediaTypeCollection()!=null){
					if( manifestOpenspcoop2.getRegistry().getService().getTypes().getType(i).getRestMediaTypeCollection().sizeMediaTypeList()>0 || 
							 manifestOpenspcoop2.getRegistry().getService().getTypes().getType(i).getRestMediaTypeCollection().getDefault()!=null || 
									 manifestOpenspcoop2.getRegistry().getService().getTypes().getType(i).getRestMediaTypeCollection().getUndefined()!=null){
						if(restEnabled==false){
							throw new ProtocolException("(ServiceType:"+serviceType+") RestMediaType not supported; Rest disabled");
						}
					}
				}
			}
		
			if(soapEnabled){
				if(tipoServizioDefaultSoap==null){
					throw new Exception("Service type not defined for protocol ["+protocolManifest+"] compatible with soap");
				}
			}
			if(restEnabled){
				if(tipoServizioDefaultRest==null){
					throw new Exception("Service type not defined for protocol ["+protocolManifest+"] compatible with rest");
				}
			}
		}
		
		// 7. controllo configurazione dei parametri di integrazione
		for (String protocolManifest : tmp_manifests.keySet()) {
			Openspcoop2 manifestOpenspcoop2 = tmp_manifests.get(protocolManifest);
			
			if(manifestOpenspcoop2.getBinding().getRest()!=null) {
				if(manifestOpenspcoop2.getBinding().getRest().getIntegration()==null){
					throw new Exception("BindingIntegrationRest not defined for protocol ["+protocolManifest+"]");
				}
				Integration integrationRestConfig = manifestOpenspcoop2.getBinding().getRest().getIntegration();
				checkIntegration(integrationRestConfig, false, protocolManifest);
			}
			
			if(manifestOpenspcoop2.getBinding().getSoap()!=null) {
				if(manifestOpenspcoop2.getBinding().getSoap().getIntegration()==null){
					throw new Exception("BindingIntegrationSoap not defined for protocol ["+protocolManifest+"]");
				}
				Integration integrationSoapConfig = manifestOpenspcoop2.getBinding().getSoap().getIntegration();
				checkIntegration(integrationSoapConfig, true, protocolManifest);
			}
		}
	}
	private void checkIntegration(Integration integrationConfig, boolean soap, String protocolManifest) throws Exception {
		String prefix = soap?"Soap":"Rest";
		
		if(integrationConfig.getImplementation()==null){
			throw new Exception("BindingIntegration"+prefix+", implementation not defined for protocol ["+protocolManifest+"]");
		}
		IntegrationConfiguration integrationConfigImpl = integrationConfig.getImplementation();
		checkIntegration(integrationConfigImpl, soap, true, protocolManifest);
		
		if(integrationConfig.getSubscription()==null){
			throw new Exception("BindingIntegration"+prefix+", subscription not defined for protocol ["+protocolManifest+"]");
		}
		IntegrationConfiguration integrationConfigSub = integrationConfig.getSubscription();
		checkIntegration(integrationConfigSub, soap, false, protocolManifest);
	}
	private void checkIntegration(IntegrationConfiguration integrationConfig, boolean soap, boolean impl, String protocolManifest) throws Exception {
		
		String prefix = soap?"Soap":"Rest";
		prefix = prefix + (impl?"Implementation":"Subscription");
		
		if(integrationConfig.getName()==null || integrationConfig.getName().sizeParamList()<=0){
			throw new Exception("BindingIntegration"+prefix+", name not defined for protocol ["+protocolManifest+"]");
		}
		this.checkIntegrationName(integrationConfig.getName().getParamList(), protocolManifest, prefix, "name", false, false);

		if(integrationConfig.getResourceIdentification()==null) {
			throw new Exception("BindingIntegration"+prefix+", resource identification not defined for protocol ["+protocolManifest+"]");
		}
		if(integrationConfig.getResourceIdentification().getIdentificationModes()==null || integrationConfig.getResourceIdentification().getIdentificationModes().sizeModeList()<=0) {
			throw new Exception("BindingIntegration"+prefix+", resource identification modes not defined for protocol ["+protocolManifest+"]");
		}
		ResourceIdentificationType defaultR = integrationConfig.getResourceIdentification().getIdentificationModes().getDefault();
		if(defaultR==null) {
			if(integrationConfig.getResourceIdentification().getIdentificationModes().sizeModeList()>1) {
				throw new Exception("BindingIntegration"+prefix+", resource identification default mode undefined for protocol ["+protocolManifest+"]");
			}
			else {
				defaultR = integrationConfig.getResourceIdentification().getIdentificationModes().getMode(0).getName();
			}
		}
		boolean defaultFound = false;
		for (IntegrationConfigurationResourceIdentificationMode mode : integrationConfig.getResourceIdentification().getIdentificationModes().getModeList()) {
			if(!soap) {
				if(ResourceIdentificationType.SOAP_ACTION.equals(mode.getName())) {
					throw new Exception("BindingIntegration"+prefix+", resource identification mode '"+mode.getName()+"' not permit in rest binding, founded in protocol ["+protocolManifest+"]");
				}
			}
			if(!impl) {
				if(ResourceIdentificationType.PROTOCOL.equals(mode.getName())) {
					throw new Exception("BindingIntegration"+prefix+", resource identification mode '"+mode.getName()+"' not permit in subscription binding, founded in protocol ["+protocolManifest+"]");
				}
			}
			if(defaultR!=null) {
				if(mode.getName().equals(defaultR)) {
					defaultFound = true;
				}
			}
		}
		if(!defaultFound) {
			throw new Exception("BindingIntegration"+prefix+", resource identification default mode '"+defaultR+"' not found in supported modes for protocol ["+protocolManifest+"]");
		}
		if(ResourceIdentificationType.CONTENT.equals(defaultR) || 
				ResourceIdentificationType.HEADER.equals(defaultR) || 
				ResourceIdentificationType.URL.equals(defaultR)) {
			if(integrationConfig.getResourceIdentification().getIdentificationParameter()==null ||
					integrationConfig.getResourceIdentification().getIdentificationParameter().sizeParamList()<=0) {
				throw new Exception("BindingIntegration"+prefix+", resource identification default mode '"+defaultR+"' require identification parameter for protocol ["+protocolManifest+"]");
			}
			this.checkIntegrationName(integrationConfig.getResourceIdentification().getIdentificationParameter().getParamList(), 
					protocolManifest, prefix, "identificationParameter", true, false);
		}
		
		if(integrationConfig.getResourceIdentification().getSpecificResource()==null) {
			throw new Exception("BindingIntegration"+prefix+", specific resource config not defined for protocol ["+protocolManifest+"]");
		}
		if(integrationConfig.getResourceIdentification().getSpecificResource().getName()==null ||
				integrationConfig.getResourceIdentification().getSpecificResource().getName().sizeParamList()<=0) {
			throw new Exception("BindingIntegration"+prefix+", specific resource name undefined for protocol ["+protocolManifest+"]");
		}
		this.checkIntegrationName(integrationConfig.getResourceIdentification().getSpecificResource().getName().getParamList(), 
				protocolManifest, prefix, "specific resource name", true, true);
	}
	private void checkIntegrationName(List<IntegrationConfigurationElementName> list, String protocolManifest, String prefix, String object,
			boolean namePermit, boolean ruleNamePermit) throws Exception {
		for (IntegrationConfigurationElementName name : list) {
			if(name==null) {
				throw new Exception("BindingIntegration"+prefix+", "+object+" not defined for protocol ["+protocolManifest+"]");
			}
			if( (name.getPrefix()==null || "".equals(name.getPrefix())) &&
					(name.getActor()==null) &&
					(name.getSuffix()==null || "".equals(name.getSuffix()))) {
				throw new Exception("BindingIntegration"+prefix+", "+object+" with wrong param (prefix, actor and suffix undefined) for protocol ["+protocolManifest+"]");
			}
			if(ActorType.NAME.equals(name.getActor()) && !namePermit) {
				throw new Exception("BindingIntegration"+prefix+", "+object+" with wrong param (actor '"+name.getActor()+"' not permit in this context) for protocol ["+protocolManifest+"]");
			}
			if(ActorType.RULE_NAME.equals(name.getActor()) && !ruleNamePermit) {
				throw new Exception("BindingIntegration"+prefix+", "+object+" with wrong param (actor '"+name.getActor()+"' not permit in this context) for protocol ["+protocolManifest+"]");
			}
			
		}
	}
	
	private void checkHttpReturnCode(DefaultIntegrationError ir,String function) throws ProtocolException {
		if(ir!=null){
			int httpReturnCode = ir.getErrorCode().getHttp();
			this.checkHttpReturnCode(httpReturnCode, function+".http");
			
			int govwayReturnCode = ir.getErrorCode().getGovway();
			this.checkHttpReturnCode(govwayReturnCode, function+".govway");
		}
	}
	private void checkHttpReturnCode(IntegrationError ir,String function) throws ProtocolException {
		if(ir!=null){
			int httpReturnCode = ir.getErrorCode().getHttp();
			this.checkHttpReturnCode(httpReturnCode, function+".http");
			
			int govwayReturnCode = ir.getErrorCode().getGovway();
			this.checkHttpReturnCode(govwayReturnCode, function+".rfc7807");
		}
	}
	private void checkHttpReturnCode(int httpReturnCode,String function) throws ProtocolException {
		String msgError = "The value provided ("+httpReturnCode+") is not used as http return code (use [200,299],[400-599])";
		if(httpReturnCode<200){
			throw new ProtocolException(msgError);
		}
		if(httpReturnCode>=300 && httpReturnCode<400){
			throw new ProtocolException(msgError);
		}
		if(httpReturnCode>=600){
			throw new ProtocolException(msgError);
		}
	}
	private void checkMessageType(String context, String subContext, MessageType messageType,SoapConfiguration soapBinding, RestConfiguration restBinding) throws ProtocolException {
		if(MessageType.XML.equals(messageType)){
			if(restBinding==null){
				throw new ProtocolException("(Context:"+context+" SubContext:"+subContext+") MessageType ["+messageType+"] not supported; Rest disabled");
			}
			if(!restBinding.isXml()){
				throw new ProtocolException("(Context:"+context+" SubContext:"+subContext+") MessageType ["+messageType+"] not supported in RestBinding; Xml disabled");
			}
		}
		else if(MessageType.JSON.equals(messageType)){
			if(restBinding==null){
				throw new ProtocolException("(Context:"+context+" SubContext:"+subContext+") MessageType ["+messageType+"] not supported; Rest disabled");
			}
			if(!restBinding.isJson()){
				throw new ProtocolException("(Context:"+context+" SubContext:"+subContext+") MessageType ["+messageType+"] not supported in RestBinding; Json disabled");
			}
		}
		else if(MessageType.BINARY.equals(messageType)){
			if(restBinding==null){
				throw new ProtocolException("(Context:"+context+" SubContext:"+subContext+") MessageType ["+messageType+"] not supported; Rest disabled");
			}
			if(!restBinding.isBinary()){
				throw new ProtocolException("(Context:"+context+" SubContext:"+subContext+") MessageType ["+messageType+"] not supported in RestBinding; Binary disabled");
			}
		}
		else if(MessageType.MIME_MULTIPART.equals(messageType)){
			if(restBinding==null){
				throw new ProtocolException("(Context:"+context+" SubContext:"+subContext+") MessageType ["+messageType+"] not supported; Rest disabled");
			}
			if(!restBinding.isMimeMultipart()){
				throw new ProtocolException("(Context:"+context+" SubContext:"+subContext+") MessageType ["+messageType+"] not supported in RestBinding; MimeMultipart disabled");
			}
		}
		else if(MessageType.SOAP_11.equals(messageType)){
			if(soapBinding==null){
				throw new ProtocolException("(Context:"+context+" SubContext:"+subContext+") MessageType ["+messageType+"] not supported; Soap disabled");
			}
			if(!soapBinding.isSoap11()){
				throw new ProtocolException("(Context:"+context+" SubContext:"+subContext+") MessageType ["+messageType+"] not supported in SoapBinding; Soap11 disabled");
			}
		}
		else if(MessageType.SOAP_12.equals(messageType)){
			if(soapBinding==null){
				throw new ProtocolException("(Context:"+context+" SubContext:"+subContext+") MessageType ["+messageType+"] not supported; Soap disabled");
			}
			if(!soapBinding.isSoap12()){
				throw new ProtocolException("(Context:"+context+" SubContext:"+subContext+") MessageType ["+messageType+"] not supported in SoapBinding; Soap12 disabled");
			}
		}
		else{
			throw new ProtocolException("(Context:"+context+" SubContext:"+subContext+") MessageType ["+messageType+"] not supported in Binding");
		}
	}
	
	private IProtocolFactory<?> getProtocolFactoryEngine(Openspcoop2 openspcoop2Manifest) throws ProtocolException {
		
		String factoryClass = null;
		try{
			factoryClass = openspcoop2Manifest.getProtocol().getFactory();
			IProtocolFactory<?> p = (IProtocolFactory<?>) ClassLoaderUtilities.newInstance(factoryClass);
			return  p;
		} catch (Exception e) {
			throw new ProtocolException("Impossibile caricare la factory indicata ["+factoryClass+"] " + e, e);
		}
	}
	
	
	public Openspcoop2 getProtocolManifest(HttpServletRequest request, FunctionContextsCustom customContexts) throws ProtocolException {
		
		URLProtocolContext urlProtocolContext = null;
		try {
			urlProtocolContext = new URLProtocolContextImpl(request, this.log, true, customContexts);
		} catch (Exception e) {
			throw new ProtocolException("Impossibile recuperare il nome del contesto dalla request: ServletContext["+request.getContextPath()+"] RequestURI["+request.getRequestURI()+"]",e);
		}
		return getProtocolManifest(urlProtocolContext.getProtocolWebContext()); 
	}
	
	
	public Openspcoop2 getProtocolManifest(String servletContextName) throws ProtocolException {
		try {
			
			Iterator<Openspcoop2> itProtocols = this.manifests.values().iterator();
			while (itProtocols.hasNext()) {
				Openspcoop2 openspcoop2Manifest = itProtocols.next();
				Web webContext = openspcoop2Manifest.getWeb();
				
				if(Costanti.CONTEXT_EMPTY.equals(servletContextName)){
					if(webContext.getEmptyContext()!=null && webContext.getEmptyContext().getEnabled()){
						return openspcoop2Manifest;
					}
				}else{
					for (int i = 0; i < webContext.sizeContextList(); i++) {
						if(webContext.getContext(i).getName().equals(servletContextName)){
							return openspcoop2Manifest;
						}
					}
				}
				
			}
			
			throw new ProtocolException("Contesto [" + servletContextName + "] non assegnato a nessun protocollo");

		} catch (Exception e) {
			throw new ProtocolException("Impossibile individuare il protocollo assegnato al contesto [" + servletContextName + "]: " + e, e);
		}
	}
	
	public IProtocolFactory<?> getProtocolFactoryByServletContext(HttpServletRequest request, FunctionContextsCustom customContexts) throws ProtocolException {
		Openspcoop2 m = this.getProtocolManifest(request,customContexts);
		if(this.factories.containsKey(m.getProtocol().getName())){
			return this.factories.get(m.getProtocol().getName());
		}
		else{
			throw new ProtocolException("ProtocolPlugin with name ["+m.getProtocol().getName()+"] not found");
		}
	}
	
	public IProtocolFactory<?> getProtocolFactoryByServletContext(String servletContext) throws ProtocolException {
		Openspcoop2 m = this.getProtocolManifest(servletContext);
		if(this.factories.containsKey(m.getProtocol().getName())){
			return this.factories.get(m.getProtocol().getName());
		}
		else{
			throw new ProtocolException("ProtocolPlugin with name ["+m.getProtocol().getName()+"] not found");
		}
	}
	
	public IProtocolFactory<?> getProtocolFactoryByName(String protocol) throws ProtocolException {
		if(this.factories.containsKey(protocol)){
			return this.factories.get(protocol);
		}
		else{
			throw new ProtocolException("ProtocolPlugin with name ["+protocol+"] not found");
		}
	}
	
	public IProtocolFactory<?> getProtocolFactoryByOrganizationType(String organizationType) throws ProtocolException {
		String protocol = this.getProtocolByOrganizationType(organizationType);
		return this.getProtocolFactoryByName(protocol);
	}
	
	public IProtocolFactory<?> getProtocolFactoryByServiceType(String serviceType) throws ProtocolException {
		String protocol = this.getProtocolByServiceType(serviceType);
		return this.getProtocolFactoryByName(protocol);
	}
	
	public IProtocolFactory<?> getProtocolFactoryWithEmptyContext() throws ProtocolException {
		Openspcoop2 m = this.getProtocolManifest(Costanti.CONTEXT_EMPTY);
		if(this.factories.containsKey(m.getProtocol().getName())){
			return this.factories.get(m.getProtocol().getName());
		}
		return null;
	}
	public IDService getDefaultServiceForWebContext(String context) throws ProtocolException {
		IProtocolFactory<?> pf = this.getProtocolFactoryByServletContext(context);
		if(pf==null) {
			return null;
		}
		if(pf.getManifest().getWeb().sizeContextList()<=0) {
			return null;
		}
		FunctionType fType = null;
		for (int i = 0; i < pf.getManifest().getWeb().sizeContextList(); i++) {
			if(pf.getManifest().getWeb().getContext(i).getName().equals(context)){
				fType = pf.getManifest().getWeb().getContext(i).getEmptyFunction();
				break;
			}
		}
		if(fType==null) {
			return null;
		}
		switch (fType) {
		case PD:
			return IDService.PORTA_DELEGATA;
		case PA:
			return IDService.PORTA_APPLICATIVA;
		case PDTO_SOAP:
			return IDService.PORTA_DELEGATA_XML_TO_SOAP;
		default:
			return null;
		}
	}
	public IDService getDefaultServiceForEmptyContext() throws ProtocolException {
		IProtocolFactory<?> pf = this.getProtocolFactoryWithEmptyContext();
		if(pf==null) {
			return null;
		}
		if(pf.getManifest().getWeb().getEmptyContext()==null) {
			return null;
		}
		FunctionType fType = pf.getManifest().getWeb().getEmptyContext().getEmptyFunction();
		if(fType==null) {
			return null;
		}
		switch (fType) {
		case PD:
			return IDService.PORTA_DELEGATA;
		case PA:
			return IDService.PORTA_APPLICATIVA;
		case PDTO_SOAP:
			return IDService.PORTA_DELEGATA_XML_TO_SOAP;
		default:
			return null;
		}
	}
	
	public IProtocolFactory<?> getDefaultProtocolFactory() throws ProtocolException {
		try {
			if(this.factories.size()==1){
				return this.factories.get((String)this.factories.keys().nextElement());
			}
			else{
				if(this.protocolDefault==null){
					throw new Exception("Non e' stato definito un protocollo di default e sono stati riscontrati piu' protocolli disponibili (size:"+this.factories.size()+")");
				}
				else{
					if(this.factories.containsKey(this.protocolDefault)){
						return this.factories.get(this.protocolDefault);
					}else{
						throw new Exception("Il protocollo di default ["+this.protocolDefault+"] indicato non corrisponde a nessuno di quelli caricati");
					}
				}
			}
		} catch (Exception e) {
			throw new ProtocolException("Impossibile individuare il protocollo assegnato al contesto: " + e, e);
		}
	}
	
	
	public MapReader<String, List<String>> getOrganizationTypes() {
		return this.tipiSoggettiValidi;
	}
	public String[] getOrganizationTypesAsArray() {
		Enumeration<List<String>> listeTipiSoggettiValidi = this.tipiSoggettiValidi.elements();
		List<String> listaTipiSoggetti = new ArrayList<String>();
		while(listeTipiSoggettiValidi.hasMoreElements()){
			listaTipiSoggetti.addAll(listeTipiSoggettiValidi.nextElement());
		}
		return listaTipiSoggetti.toArray(new String[1]);
	}
	public List<String> getOrganizationTypesAsList() {
		Enumeration<List<String>> listeTipiSoggettiValidi = this.tipiSoggettiValidi.elements();
		List<String> listaTipiSoggetti = new ArrayList<String>();
		while(listeTipiSoggettiValidi.hasMoreElements()){
			listaTipiSoggetti.addAll(listeTipiSoggettiValidi.nextElement());
		}
		return listaTipiSoggetti;
	}
	public MapReader<String, String> getDefaultOrganizationTypes() {
		return this.tipiSoggettiDefault;
	}
	
	
	public HashMap<String, List<String>> _getServiceTypes() {
		HashMap<String, List<String>> map = new HashMap<>();
		
		MapReader<String, List<String>> rest = this.getServiceTypes(ServiceBinding.REST);
		if(rest!=null && rest.size()>0) {
			Enumeration<String> en = rest.keys();
			while (en.hasMoreElements()) {
				String protocollo = (String) en.nextElement();
				map.put(protocollo, rest.get(protocollo));
			}
		}
		
		MapReader<String, List<String>> soap = this.getServiceTypes(ServiceBinding.SOAP);
		if(soap!=null && soap.size()>0) {
			Enumeration<String> en = soap.keys();
			while (en.hasMoreElements()) {
				String protocollo = (String) en.nextElement();
				if(map.containsKey(protocollo)) {
					List<String> restP = map.get(protocollo);
					List<String> soapP = soap.get(protocollo);
					for (String tipo : soapP) {
						if(restP.contains(tipo)==false) {
							restP.add(tipo);
						}
					}
				}
				else {
					map.put(protocollo, soap.get(protocollo));
				}
			}
		}
		
		return map;
	}
	public MapReader<String, List<String>> getServiceTypes(ServiceBinding serviceBinding) {
		if(ServiceBinding.SOAP.equals(serviceBinding)){
			return this.tipiServiziValidi_soap;
		}
		else{
			return this.tipiServiziValidi_rest;
		}
	}
	public String[] getServiceTypesAsArray(ServiceBinding serviceBinding) {
		Enumeration<List<String>> listeTipiServiziValidi = null;
		if(ServiceBinding.SOAP.equals(serviceBinding)){
			listeTipiServiziValidi = this.tipiServiziValidi_soap.elements();
		}
		else{
			listeTipiServiziValidi = this.tipiServiziValidi_rest.elements();
		}
		List<String> listaTipiServizi = new ArrayList<String>();
		while(listeTipiServiziValidi.hasMoreElements()){
			listaTipiServizi.addAll(listeTipiServiziValidi.nextElement());
		}
		return listaTipiServizi.toArray(new String[1]);
	}
	public List<String> getServiceTypesAsList(ServiceBinding serviceBinding) {
		Enumeration<List<String>> listeTipiServiziValidi = null;
		if(ServiceBinding.SOAP.equals(serviceBinding)){
			listeTipiServiziValidi = this.tipiServiziValidi_soap.elements();
		}
		else{
			listeTipiServiziValidi = this.tipiServiziValidi_rest.elements();
		}
		List<String> listaTipiServizi = new ArrayList<String>();
		while(listeTipiServiziValidi.hasMoreElements()){
			listaTipiServizi.addAll(listeTipiServiziValidi.nextElement());
		}
		return listaTipiServizi;
	}
	public MapReader<String, String> getDefaultServiceTypes(ServiceBinding serviceBinding) {
		if(ServiceBinding.SOAP.equals(serviceBinding)){
			return this.tipiServiziDefault_soap;
		}
		else{
			return this.tipiServiziDefault_rest;
		}
	}
	
	
	public MapReader<String, List<String>> getVersion() {
		return this.versioniValide;
	}
	public String[] getVersionAsArray() {
		Enumeration<List<String>> listeVersioniValidi = this.versioniValide.elements();
		List<String> listaVersioni = new ArrayList<String>();
		while(listeVersioniValidi.hasMoreElements()){
			listaVersioni.addAll(listeVersioniValidi.nextElement());
		}
		return listaVersioni.toArray(new String[1]);
	}
	public List<String> getVersionAsList() {
		Enumeration<List<String>> listeVersioniValide = this.versioniValide.elements();
		List<String> listaVersioni = new ArrayList<String>();
		while(listeVersioniValide.hasMoreElements()){
			listaVersioni.addAll(listeVersioniValide.nextElement());
		}
		return listaVersioni;
	}
	public MapReader<String, String> getDefaultVersion() {
		return this.versioniDefault;
	}
	
	
	public MapReader<String, IProtocolFactory<?>> getProtocolFactories() {
		return this.factories;
	}
	
	public Enumeration<String> getProtocolNames(){
		return this.factories.keys();
	}
	
	public List<String> getProtocolNamesAsList(){
		List<String> protocolli = new ArrayList<>();
		Enumeration<String> en = getProtocolNames();
		while (en.hasMoreElements()) {
			String protocollo = (String) en.nextElement();
			protocolli.add(protocollo);
		}
		return protocolli;
	}
	
	public void initStaticInstance() throws ProtocolException {
		if(this.factories!=null && this.factories.size()>0) {
			Enumeration<IProtocolFactory<?>> en = this.factories.elements();
			if(en!=null) {
				while (en.hasMoreElements()) {
					IProtocolFactory<?> iProtocolFactory = (IProtocolFactory<?>) en.nextElement();
					if(iProtocolFactory!=null && iProtocolFactory instanceof BasicFactory<?> ) {
						((BasicFactory<?>)iProtocolFactory).initStaticInstance();
					}
				}
			}
		}
	}
	
	// ***** Utilities *****
	
	public String getProtocolByOrganizationType(String organizationType) throws ProtocolException {
		
		Enumeration<String> protocolli = this.factories.keys();
		while (protocolli.hasMoreElements()) {
				
			String protocollo = protocolli.nextElement();
			IProtocolFactory<?> protocolFactory = this.factories.get(protocollo);
			List<String> tipiP = protocolFactory.createProtocolConfiguration().getTipiSoggetti();
			if(tipiP.contains(organizationType)){
				return protocollo;
			}
				
		}
			
		throw new ProtocolException("Non esiste un protocollo associato al tipo di soggetto ["+organizationType+"]");
			
	}
	
	public String getProtocolByServiceType(String serviceType) throws ProtocolException {
		
		Enumeration<String> protocolli = this.factories.keys();
		while (protocolli.hasMoreElements()) {
				
			String protocollo = protocolli.nextElement();
			IProtocolFactory<?> protocolFactory = this.factories.get(protocollo);
			List<String> tipiP = protocolFactory.createProtocolConfiguration().getTipiServizi(org.openspcoop2.message.constants.ServiceBinding.SOAP);
			if(tipiP.contains(serviceType)){
				return protocollo;
			}
			tipiP = protocolFactory.createProtocolConfiguration().getTipiServizi(org.openspcoop2.message.constants.ServiceBinding.REST);
			if(tipiP.contains(serviceType)){
				return protocollo;
			}
				
		}
			
		throw new ProtocolException("Non esiste un protocollo associato al tipo di servizio ["+serviceType+"]");
			
	}
}
