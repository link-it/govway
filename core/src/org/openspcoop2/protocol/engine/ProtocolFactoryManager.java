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

package org.openspcoop2.protocol.engine;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.openspcoop2.protocol.engine.mapping.InformazioniServizioURLMapping;
import org.openspcoop2.protocol.manifest.Openspcoop2;
import org.openspcoop2.protocol.manifest.Web;
import org.openspcoop2.protocol.manifest.constants.Costanti;
import org.openspcoop2.protocol.manifest.utils.XMLUtils;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.MapReader;

/**
 * Protocol Factory Manager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class ProtocolFactoryManager {

	private static ProtocolFactoryManager protocolFactoryManager = null;
	public synchronized static void initialize(Logger log,ConfigurazionePdD configPdD,String protocolDefault) throws ProtocolException {
		if(ProtocolFactoryManager.protocolFactoryManager==null){
			ProtocolFactoryManager.protocolFactoryManager = new ProtocolFactoryManager(log,configPdD,protocolDefault,false);
			// Inizializzo anche Esiti.properties
			EsitiProperties.initialize(configPdD.getConfigurationDir(), log, configPdD.getLoader());
		}
	}
	public synchronized static void initializeSingleProtocol(Logger log,ConfigurazionePdD configPdD,String protocol) throws ProtocolException {
		if(ProtocolFactoryManager.protocolFactoryManager==null){
			ProtocolFactoryManager.protocolFactoryManager = new ProtocolFactoryManager(log,configPdD,protocol,true);
			// Inizializzo anche Esiti.properties
			EsitiProperties.initialize(configPdD.getConfigurationDir(), log, configPdD.getLoader());
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
	
	
	private MapReader<String, Openspcoop2> manifests = null;
	@SuppressWarnings("unused")
	private MapReader<String, URL> manifestURLs = null;
	private MapReader<String, IProtocolFactory> factories = null;
	private StringBuffer protocolLoaded = new StringBuffer();
	private String protocolDefault = null;
	private MapReader<String, List<String>> tipiSoggettiValidi = null;
	private MapReader<String, String> tipiSoggettiDefault = null;
	private MapReader<String, List<String>> tipiServiziValidi = null;
	private MapReader<String, String> tipiServiziDefault = null;
	private MapReader<String, List<String>> versioniValide = null;
	private MapReader<String, String> versioniDefault = null;
	private Logger log = null;
	ProtocolFactoryManager(Logger log,ConfigurazionePdD configPdD,String protocolDefault, boolean searchSingleManifest) throws ProtocolException {
		try {

			Hashtable<String, Openspcoop2> tmp_manifests = new Hashtable<String, Openspcoop2>();
			Hashtable<String, URL> tmp_manifestURLs = new Hashtable<String, URL>();
			Hashtable<String, IProtocolFactory> tmp_factories = new Hashtable<String, IProtocolFactory>();
			
			Hashtable<String, List<String>> tmp_tipiSoggettiValidi = new Hashtable<String, List<String>>();
			Hashtable<String, String> tmp_tipiSoggettiDefault = new Hashtable<String, String>();
			
			Hashtable<String, List<String>> tmp_tipiServiziValidi = new Hashtable<String, List<String>>();
			Hashtable<String, String> tmp_tipiServiziDefault = new Hashtable<String, String>();
			
			Hashtable<String, List<String>> tmp_versioniValide = new Hashtable<String, List<String>>();
			Hashtable<String, String> tmp_versioniDefault = new Hashtable<String, String>();
			
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
					tmp_tipiServiziValidi, tmp_tipiServiziDefault,
					tmp_versioniValide, tmp_versioniDefault);
			configPdD.getLog().debug("Validate Manifests ok");
			
			// Init protocol factory
			Enumeration<String> protocolManifestEnum = tmp_manifests.keys();
			while (protocolManifestEnum.hasMoreElements()) {
				
				String protocolManifest = protocolManifestEnum.nextElement();
				configPdD.getLog().debug("Init ProtocolFactory for protocol ["+protocolManifest+"] ...");
				Openspcoop2 manifestOpenspcoop2 = tmp_manifests.get(protocolManifest);				
				
				// Factory
				IProtocolFactory p = this.getProtocolFactoryEngine(manifestOpenspcoop2);
				p.init(configPdD.getLog(), protocolManifest, configPdD,manifestOpenspcoop2);
				if(!p.createValidazioneConSchema().initialize()){
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
				StringBuffer context = new StringBuffer();
				if(manifestOpenspcoop2.getWeb().getEmptyContext()!=null && manifestOpenspcoop2.getWeb().getEmptyContext().getEnabled()){
					context.append("@EMPTY-CONTEXT@");
				}
				for (int i = 0; i < manifestOpenspcoop2.getWeb().sizeContextList(); i++) {
					if(context.length()>0){
						context.append(",");
					}
					context.append(manifestOpenspcoop2.getWeb().getContext(i));
				}
				log.info("Protocol loaded with id["+protocolManifest+"] factory["+manifestOpenspcoop2.getFactory()+"] contexts["+context.toString()+"]");
				
				configPdD.getLog().debug("Init ProtocolFactory for protocol ["+protocolManifest+"] ok");
			}
			
			// init
			this.manifests = new MapReader<String, Openspcoop2>(tmp_manifests,true);
			this.manifestURLs = new MapReader<String, URL>(tmp_manifestURLs,true);
			this.factories = new MapReader<String, IProtocolFactory>(tmp_factories,true);
			
			this.tipiSoggettiValidi = new MapReader<String, List<String>>(tmp_tipiSoggettiValidi,true);
			this.tipiSoggettiDefault = new MapReader<String, String>(tmp_tipiSoggettiDefault,true);
			
			this.tipiServiziValidi = new MapReader<String, List<String>>(tmp_tipiServiziValidi,true);
			this.tipiServiziDefault = new MapReader<String, String>(tmp_tipiServiziDefault,true);
			
			this.versioniValide = new MapReader<String, List<String>>(tmp_versioniValide,true);
			this.versioniDefault = new MapReader<String, String>(tmp_versioniDefault,true);
			
		} catch (Exception e) {
			configPdD.getLog().error("Init ProtocolFactoryManager failed: "+e.getMessage(),e);
			throw new ProtocolException("Inizializzazione ProtocolFactoryManager fallita: " + e, e);
		}
	}
	
	private void loadManifest(ConfigurazionePdD configPdD,URL pluginURL,boolean filtraSenzaErroreProtocolloGiaCaricato,
			Hashtable<String, Openspcoop2> tmp_manifests, Hashtable<String, URL> tmp_manifestURLs) throws Exception{
		// Manifest
		configPdD.getLog().debug("Analyze manifest ["+pluginURL.toString()+"] ...");
		
		InputStream openStream = null;
		byte[] manifest = null;
		try{
			openStream = pluginURL.openStream();
			manifest = Utilities.getAsByteArray(openStream);
		}finally{
			try{
				openStream.close();
			}catch(Exception e){}
		}
		//System.out.println("CARICATO ["+new String(manifest)+"]");

		configPdD.getLog().debug("Analyze manifest ["+pluginURL.toString()+"] convertToOpenSPCoop2Manifest...");
		Openspcoop2 manifestOpenspcoop2 = XMLUtils.getOpenspcoop2Manifest(configPdD.getLog(),manifest);
		String protocollo = manifestOpenspcoop2.getProtocolName();
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
	
	private void validateProtocolFactoryLoaded(Hashtable<String, Openspcoop2> tmp_manifests,
			Hashtable<String, List<String>> tmp_tipiSoggettiValidi,Hashtable<String, String> tmp_tipiSoggettiDefault,
			Hashtable<String, List<String>> tmp_tipiServiziValidi, Hashtable<String, String> tmp_tipiServiziDefault,
			Hashtable<String, List<String>> tmp_versioniValide,Hashtable<String, String> tmp_versioniDefault) throws Exception{
				
		// 1. controllare che solo uno possieda il contesto vuoto
		Enumeration<String> protocolManifestEnum = tmp_manifests.keys();
		String protocolContextEmpty = null;
		while (protocolManifestEnum.hasMoreElements()) {
			
			String protocolManifest = protocolManifestEnum.nextElement();
			Openspcoop2 manifestOpenspcoop2 = tmp_manifests.get(protocolManifest);
			if(manifestOpenspcoop2.getWeb().getEmptyContext()!=null && manifestOpenspcoop2.getWeb().getEmptyContext().getEnabled()){
				if(protocolContextEmpty==null){
					protocolContextEmpty = protocolManifest;
				}
				else{
					throw new Exception("Protocols ["+protocolContextEmpty+"] and ["+protocolManifest+"] with empty context. Only one is permitted");
				}
			}
			
		}
		
		// 2. controllare che i contesti siano tutti diversi
		Hashtable<String, String> mappingContextToProtocol = new Hashtable<String, String>();
		protocolManifestEnum = tmp_manifests.keys();
		while (protocolManifestEnum.hasMoreElements()) {
			
			String protocolManifest = protocolManifestEnum.nextElement();
			Openspcoop2 manifestOpenspcoop2 = tmp_manifests.get(protocolManifest);
			
			for (int i = 0; i < manifestOpenspcoop2.getWeb().sizeContextList(); i++) {
				String context = manifestOpenspcoop2.getWeb().getContext(i);
				if(!mappingContextToProtocol.containsKey(context)){
					mappingContextToProtocol.put(context, protocolManifest);
				}
				else{
					throw new Exception("Protocols ["+mappingContextToProtocol.get(context)+"] and ["+protocolManifest+"] with same context ["+context+"]");
				}
			}
			
		}
		
		// 3. controllare e inizializzare i tipi di soggetti in modo che siano tutti diversi
		Hashtable<String, String> mappingTipiSoggettiToProtocol = new Hashtable<String, String>();
		protocolManifestEnum = tmp_manifests.keys();
		while (protocolManifestEnum.hasMoreElements()) {
			
			String protocolManifest = protocolManifestEnum.nextElement();
			Openspcoop2 manifestOpenspcoop2 = tmp_manifests.get(protocolManifest);
			
			int size = manifestOpenspcoop2.getRegistroServizi().getSoggetti().getTipi().sizeTipoList();
			if(size<=0){
				throw new Exception("Subject type not defined for protocol ["+protocolManifest+"]");
			}
			
			for (int i = 0; i < size; i++) {
				String tipo = manifestOpenspcoop2.getRegistroServizi().getSoggetti().getTipi().getTipo(i);
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
					throw new Exception("Protocols ["+mappingTipiSoggettiToProtocol.get(tipo)+"] and ["+protocolManifest+"] with same subject type ["+tipo+"]");
				}
			}
			
			String tipoDefault = manifestOpenspcoop2.getRegistroServizi().getSoggetti().getTipi().getDefault();
			if(tipoDefault!=null){
				if(tmp_tipiSoggettiValidi.get(protocolManifest).contains(tipoDefault)==false){
					throw new Exception("Unknown default subject type ["+tipoDefault+"] defined in protocol ["+protocolManifest+"]");
				}
			}
			else{
				if(size>1){
					throw new Exception("Default subject type not defined. It's required if more than one type is defined (found "+size+" subject types)");
				}
				else{
					tipoDefault = manifestOpenspcoop2.getRegistroServizi().getSoggetti().getTipi().getTipo(0);
				}
			}
			tmp_tipiSoggettiDefault.put(protocolManifest, tipoDefault);
			
		}
		
		
		// 4. controllare e inizializzare i tipi di servizi in modo che siano tutti diversi
		Hashtable<String, String> mappingTipiServiziToProtocol = new Hashtable<String, String>();
		protocolManifestEnum = tmp_manifests.keys();
		while (protocolManifestEnum.hasMoreElements()) {
			
			String protocolManifest = protocolManifestEnum.nextElement();
			Openspcoop2 manifestOpenspcoop2 = tmp_manifests.get(protocolManifest);
			
			int size = manifestOpenspcoop2.getRegistroServizi().getServizi().getTipi().sizeTipoList();
			if(size<=0){
				throw new Exception("Service type not defined for protocol ["+protocolManifest+"]");
			}
			
			for (int i = 0; i < size; i++) {
				String tipo = manifestOpenspcoop2.getRegistroServizi().getServizi().getTipi().getTipo(i);
				if(!mappingTipiServiziToProtocol.containsKey(tipo)){
					mappingTipiServiziToProtocol.put(tipo, protocolManifest);
					
					List<String> tipiServiziPerProtocollo = null;
					if(tmp_tipiServiziValidi.containsKey(protocolManifest)){
						tipiServiziPerProtocollo = tmp_tipiServiziValidi.remove(protocolManifest);
					}
					else{
						tipiServiziPerProtocollo = new ArrayList<String>();
					}
					tipiServiziPerProtocollo.add(tipo);
					tmp_tipiServiziValidi.put(protocolManifest, tipiServiziPerProtocollo);
				}
				else{
					throw new Exception("Protocols ["+mappingTipiServiziToProtocol.get(tipo)+"] and ["+protocolManifest+"] with same service type ["+tipo+"]");
				}
			}
			
			String tipoDefault = manifestOpenspcoop2.getRegistroServizi().getServizi().getTipi().getDefault();
			if(tipoDefault!=null){
				if(tmp_tipiServiziValidi.get(protocolManifest).contains(tipoDefault)==false){
					throw new Exception("Unknown default service type ["+tipoDefault+"] defined in protocol ["+protocolManifest+"]");
				}
			}
			else{
				if(size>1){
					throw new Exception("Default service type not defined. It's required if more than one type is defined (found "+size+" service types)");
				}
				else{
					tipoDefault = manifestOpenspcoop2.getRegistroServizi().getServizi().getTipi().getTipo(0);
				}
			}
			tmp_tipiServiziDefault.put(protocolManifest, tipoDefault);
		}
		
		
		
		// 5. controllare e inizializzare le versioni dei protocolli
		protocolManifestEnum = tmp_manifests.keys();
		while (protocolManifestEnum.hasMoreElements()) {
			
			String protocolManifest = protocolManifestEnum.nextElement();
			Openspcoop2 manifestOpenspcoop2 = tmp_manifests.get(protocolManifest);
			
			int size = manifestOpenspcoop2.getRegistroServizi().getVersioni().sizeVersioneList();
			if(size<=0){
				throw new Exception("Version not defined for protocol ["+protocolManifest+"]");
			}
			
			for (int i = 0; i < size; i++) {
				String version = manifestOpenspcoop2.getRegistroServizi().getVersioni().getVersione(i);
				
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
			
			String versioneDefault = manifestOpenspcoop2.getRegistroServizi().getVersioni().getDefault();
			if(versioneDefault!=null){
				if(tmp_versioniValide.get(protocolManifest).contains(versioneDefault)==false){
					throw new Exception("Unknown default version ["+versioneDefault+"] defined in protocol ["+protocolManifest+"]");
				}
			}
			else{
				if(size>1){
					throw new Exception("Default version not defined. It's required if more than one type is defined (found "+size+" version)");
				}
				else{
					versioneDefault = manifestOpenspcoop2.getRegistroServizi().getVersioni().getVersione(0);
				}
			}
			tmp_versioniDefault.put(protocolManifest, versioneDefault);
		}
		
	}
	
	private IProtocolFactory getProtocolFactoryEngine(Openspcoop2 openspcoop2Manifest) throws ProtocolException {
		
		String factoryClass = null;
		try{
			factoryClass = openspcoop2Manifest.getFactory();
			Class<?> c = Class.forName(factoryClass);
			IProtocolFactory p = (IProtocolFactory) c.newInstance();
			return  p;
		} catch (Exception e) {
			throw new ProtocolException("Impossibile caricare la factory indicata ["+factoryClass+"] " + e, e);
		}
	}
	
	
	public Openspcoop2 getProtocolManifest(HttpServletRequest request) throws ProtocolException {
		
		URLProtocolContext urlProtocolContext = null;
		try {
			urlProtocolContext = new URLProtocolContext(request, this.log);
		} catch (Exception e) {
			throw new ProtocolException("Impossibile recuperare il nome del contesto dalla request: ServletContext["+request.getContextPath()+"] RequestURI["+request.getRequestURI()+"]",e);
		}
		return getProtocolManifest(urlProtocolContext.getProtocol()); 
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
						if(webContext.getContext(i).equals(servletContextName)){
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
	
	public IProtocolFactory getProtocolFactoryByServletContext(HttpServletRequest request) throws ProtocolException {
		Openspcoop2 m = this.getProtocolManifest(request);
		if(this.factories.containsKey(m.getProtocolName())){
			return this.factories.get(m.getProtocolName());
		}
		else{
			throw new ProtocolException("ProtocolPlugin with name ["+m.getProtocolName()+"] not found");
		}
	}
	
	public IProtocolFactory getProtocolFactoryByServletContext(String servletContext) throws ProtocolException {
		Openspcoop2 m = this.getProtocolManifest(servletContext);
		if(this.factories.containsKey(m.getProtocolName())){
			return this.factories.get(m.getProtocolName());
		}
		else{
			throw new ProtocolException("ProtocolPlugin with name ["+m.getProtocolName()+"] not found");
		}
	}
	
	public IProtocolFactory getProtocolFactoryByName(String protocol) throws ProtocolException {
		if(this.factories.containsKey(protocol)){
			return this.factories.get(protocol);
		}
		else{
			throw new ProtocolException("ProtocolPlugin with name ["+protocol+"] not found");
		}
	}
	
	public IProtocolFactory getProtocolFactoryBySubjectType(String subjectType) throws ProtocolException {
		String protocol = this.getProtocolBySubjectType(subjectType);
		return this.getProtocolFactoryByName(protocol);
	}
	
	public IProtocolFactory getProtocolFactoryByServiceType(String serviceType) throws ProtocolException {
		String protocol = this.getProtocolByServiceType(serviceType);
		return this.getProtocolFactoryByName(protocol);
	}
	
	
	public IProtocolFactory getDefaultProtocolFactory() throws ProtocolException {
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
	
	
	public MapReader<String, List<String>> getSubjectTypes() {
		return this.tipiSoggettiValidi;
	}
	public String[] getSubjectTypesAsArray() {
		Enumeration<List<String>> listeTipiSoggettiValidi = this.tipiSoggettiValidi.elements();
		List<String> listaTipiSoggetti = new ArrayList<String>();
		while(listeTipiSoggettiValidi.hasMoreElements()){
			listaTipiSoggetti.addAll(listeTipiSoggettiValidi.nextElement());
		}
		return listaTipiSoggetti.toArray(new String[1]);
	}
	public List<String> getSubjectTypesAsList() {
		Enumeration<List<String>> listeTipiSoggettiValidi = this.tipiSoggettiValidi.elements();
		List<String> listaTipiSoggetti = new ArrayList<String>();
		while(listeTipiSoggettiValidi.hasMoreElements()){
			listaTipiSoggetti.addAll(listeTipiSoggettiValidi.nextElement());
		}
		return listaTipiSoggetti;
	}
	public MapReader<String, String> getDefaultSubjectTypes() {
		return this.tipiSoggettiDefault;
	}
	
	
	public MapReader<String, List<String>> getServiceTypes() {
		return this.tipiServiziValidi;
	}
	public String[] getServiceTypesAsArray() {
		Enumeration<List<String>> listeTipiServiziValidi = this.tipiServiziValidi.elements();
		List<String> listaTipiServizi = new ArrayList<String>();
		while(listeTipiServiziValidi.hasMoreElements()){
			listaTipiServizi.addAll(listeTipiServiziValidi.nextElement());
		}
		return listaTipiServizi.toArray(new String[1]);
	}
	public List<String> getServiceTypesAsList() {
		Enumeration<List<String>> listeTipiServiziValidi = this.tipiServiziValidi.elements();
		List<String> listaTipiServizi = new ArrayList<String>();
		while(listeTipiServiziValidi.hasMoreElements()){
			listaTipiServizi.addAll(listeTipiServiziValidi.nextElement());
		}
		return listaTipiServizi;
	}
	public MapReader<String, String> getDefaultServiceTypes() {
		return this.tipiServiziDefault;
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
	
	
	public MapReader<String, IProtocolFactory> getProtocolFactories() {
		return this.factories;
	}
	
	public Enumeration<String> getProtocolNames(){
		return this.factories.keys();
	}
	
	
	// ***** Utilities *****
	
	public String getProtocolBySubjectType(String subjectType) throws ProtocolException {
		
		Enumeration<String> protocolli = this.factories.keys();
		while (protocolli.hasMoreElements()) {
				
			String protocollo = protocolli.nextElement();
			IProtocolFactory protocolFactory = this.factories.get(protocollo);
			List<String> tipiP = protocolFactory.createProtocolConfiguration().getTipiSoggetti();
			if(tipiP.contains(subjectType)){
				return protocollo;
			}
				
		}
			
		throw new ProtocolException("Non esiste un protocollo associato al tipo di soggetto ["+subjectType+"]");
			
	}
	
	public String getProtocolByServiceType(String serviceType) throws ProtocolException {
		
		Enumeration<String> protocolli = this.factories.keys();
		while (protocolli.hasMoreElements()) {
				
			String protocollo = protocolli.nextElement();
			IProtocolFactory protocolFactory = this.factories.get(protocollo);
			List<String> tipiP = protocolFactory.createProtocolConfiguration().getTipiServizi();
			if(tipiP.contains(serviceType)){
				return protocollo;
			}
				
		}
			
		throw new ProtocolException("Non esiste un protocollo associato al tipo di servizio ["+serviceType+"]");
			
	}
}
