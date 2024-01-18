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

package org.openspcoop2.protocol.engine.archive;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.GruppiAccordo;
import org.openspcoop2.core.registry.GruppoAccordo;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.registry.utils.CleanerOpenSPCoop2Extensions;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.information_missing.constants.Costanti;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.resources.Loader;
import org.slf4j.Logger;

/**
 *  WizardAPIUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WizardAPIUtilities {

	private static final String EMPTY = "Empty?";
	private static final String SUFFIX_NON_ESISTENTE = " non esistente";
	private static final String SUFFIX_NON_LEGGIBILE = " non leggibile";
	
	// Use: java WizardAPIUtilities outputFile profile pathInterface pathProperties [pathProfileProperties]
	private static final String USE = "Usage: java WizardAPIUtilities pathOutputFile profile pathInterface pathProperties [pathProfileProperties]";
	
	private static final Logger log = LoggerWrapperFactory.getLogger(WizardAPIUtilities.class);
	
	private static final String PROPERTIES_NAME = "name";
	private static final String PROPERTIES_VERSION = "version";
	private static final String PROPERTIES_DESCRIPTION = "description";
	private static final String PROPERTIES_SERVICE_BINDING = "serviceBinding";
	private static final String PROPERTIES_INTERFACE_TYPE = "interfaceType";
	private static final String PROPERTIES_TAG = "tag";
	private static final String PROPERTIES_CANALE = "canale";
	
	public static void main(String[] args) throws ProtocolException {
		
		if(args==null || args.length<4) {
			throw new ProtocolException("ERROR: argomenti non forniti\n"+USE);
		}
		
		String pathOutputFile = readPathOutpuFile(args, 0);
		
		String profile = readProfile(args, 1);
		
		byte[] bytesInterface = readInterface(args, 2);
		
		Properties properties = readProperties(args, 3);
		
		Properties profileProperties = readProfileProperties(args, 4);
		
		IProtocolFactory<?> protocol = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(profile);
				
		AccordoServizioParteComune as = buildAccordo(properties, bytesInterface, protocol);
		
		addProfileProperties(as, profileProperties);
		
		protocol.createArchive().setProtocolInfo(as);
		
		CleanerOpenSPCoop2Extensions cleaner = new CleanerOpenSPCoop2Extensions();
		cleaner.clean(as);
		
		try(FileOutputStream fout = new FileOutputStream(pathOutputFile)){
			as.writeTo(fout, WriteToSerializerType.XML_JAXB);
		}catch(Exception e) {
			throw new ProtocolException("Serializzazione in '"+pathOutputFile+"' non riuscita: "+e.getMessage(),e);
		}
		
		log.info("Serializzazione completata");
	}
	

	private static String readPathOutpuFile(String [] args, int position) throws ProtocolException {
		String pathOutputFile = args[position];
		if(pathOutputFile==null || StringUtils.isEmpty(pathOutputFile)) {
			throw new ProtocolException("ERROR: argomento 'pathOutputFile' non fornito\n"+USE);
		}
		File fPathOutputFile = new File(pathOutputFile);
		if(fPathOutputFile.exists()) {
			throw new ProtocolException("ERROR: argomento 'pathOutputFile' contiene il riferimento ad un file "+fPathOutputFile.getAbsolutePath()+" già esistente");
		}
		return pathOutputFile;
	}
		
	private static String readProfile(String [] args, int position) throws ProtocolException {
		String profile = args[position];
		if(profile==null || StringUtils.isEmpty(profile)) {
			throw new ProtocolException("ERROR: argomento 'profile' non fornito\n"+USE);
		}
		try {
			ConfigurazionePdD configPdD = new ConfigurazionePdD();
			configPdD.setLoader(new Loader());
			configPdD.setLog(log);
			ProtocolFactoryManager.initialize(log, configPdD, profile);
		}catch(Exception e) {
			throw new ProtocolException("ERROR: argomento 'profile="+profile+"' non valido:"+e.getMessage(),e);
		}
		return profile;
	}
	
	private static byte[] readInterface(String [] args, int position) throws ProtocolException {
		String pathInterface = args[position];
		if(pathInterface==null || StringUtils.isEmpty(pathInterface)) {
			throw new ProtocolException("ERROR: argomento 'pathInterface' non fornito\n"+USE);
		}
		File fPathInterface = new File(pathInterface);
		checkExists(fPathInterface, "pathInterface");
		byte[] bytesInterface = null;
		try {
			bytesInterface = FileSystemUtilities.readBytesFromFile(fPathInterface);
			if(bytesInterface==null || bytesInterface.length<=0) {
				throw new ProtocolException(EMPTY);
			}
		}catch(Exception e) {
			throw new ProtocolException("ERROR: argomento 'pathInterface="+pathInterface+"' non valido:"+e.getMessage(),e);
		}
		return bytesInterface;
	}
	
	private static Properties readProperties(String [] args, int position) throws ProtocolException {
		String pathProperties = args[position];
		if(pathProperties==null || StringUtils.isEmpty(pathProperties)) {
			throw new ProtocolException("ERROR: argomento 'pathProperties' non fornito\n"+USE);
		}
		File fPathProperties = new File(pathProperties);
		checkExists(fPathProperties, "pathProperties");
		byte[] bytesProperties = null;
		try {
			bytesProperties = FileSystemUtilities.readBytesFromFile(fPathProperties);
			if(bytesProperties==null || bytesProperties.length<=0) {
				throw new ProtocolException(EMPTY);
			}
		}catch(Exception e) {
			throw new ProtocolException("ERROR: argomento 'pathProperties="+pathProperties+"' non è un file di proprietà corretto:"+e.getMessage(),e);
		}
		return convertToProperties("pathProperties="+pathProperties,bytesProperties);
	}
	
	private static Properties readProfileProperties(String [] args, int position) throws ProtocolException {
		Properties profileProperties = null;
		if(args.length>=(position+1)) {
			String pathProfileProperties = args[position];
			if(pathProfileProperties!=null && !StringUtils.isEmpty(pathProfileProperties)) {
				File fPathProfileProperties = new File(pathProfileProperties);
				checkExists(fPathProfileProperties, "pathProfileProperties");
				byte[] bytesProfileProperties = null;
				try {
					bytesProfileProperties = FileSystemUtilities.readBytesFromFile(fPathProfileProperties);
					if(bytesProfileProperties==null || bytesProfileProperties.length<=0) {
						throw new ProtocolException(EMPTY);
					}
				}catch(Exception e) {
					throw new ProtocolException("ERROR: argomento 'pathProfileProperties="+pathProfileProperties+"' non è un file di proprietà corretto:"+e.getMessage(),e);
				}
				profileProperties = convertToProperties("pathProfileProperties="+pathProfileProperties,bytesProfileProperties);
			}
		}
		return profileProperties;
	}
	
	private static void checkExists(File f, String name) throws ProtocolException {
		if(!f.exists()) {
			throw new ProtocolException("ERROR: argomento '"+name+"' contiene il riferimento ad un file "+f.getAbsolutePath()+SUFFIX_NON_ESISTENTE);
		}
		if(!f.canRead()) {
			throw new ProtocolException("ERROR: argomento '"+name+"' contiene il riferimento ad un file "+f.getAbsolutePath()+SUFFIX_NON_LEGGIBILE);
		}
	}
	
	private static Properties convertToProperties(String name,byte[]b) throws ProtocolException {
		Properties tmp = new Properties();
		try(ByteArrayInputStream bin = new ByteArrayInputStream(b)){
			tmp.load(bin);
		}catch(Exception e) {
			throw new ProtocolException("ERROR: conversione non  riuscita, argomento '"+name+"' non corretto:"+e.getMessage(),e);
		}
		return tmp;
	}
	
	private static AccordoServizioParteComune buildAccordo(Properties properties, byte[] bytesInterface, IProtocolFactory<?> protocol) throws ProtocolException {
		AccordoServizioParteComune as = new AccordoServizioParteComune();
		as.setNome(readProperty(properties, PROPERTIES_NAME, true));
		as.setDescrizione(readProperty(properties, PROPERTIES_DESCRIPTION, false));
		
		as.setByteWsdlConcettuale(bytesInterface);
		
		as.setProfiloCollaborazione(ProfiloCollaborazione.SINCRONO);
		/**as.setFiltroDuplicati(StatoFunzionalita.DISABILITATO);
		as.setConfermaRicezione(StatoFunzionalita.DISABILITATO);
		as.setConsegnaInOrdine(StatoFunzionalita.DISABILITATO);
		as.setIdCollaborazione(StatoFunzionalita.DISABILITATO);
		as.setIdRiferimentoRichiesta(StatoFunzionalita.DISABILITATO);
		as.setUtilizzoSenzaAzione(true);// default true*/
	
		String serviceBinding = null;
		try {
			serviceBinding = readProperty(properties, PROPERTIES_SERVICE_BINDING, true);
			as.setServiceBinding(ServiceBinding.toEnumConstant(serviceBinding, true));
		}catch(Exception e) {
			throw new ProtocolException("ERROR: 'pathProperties' contiene una proprietà '"+PROPERTIES_SERVICE_BINDING+"' valorizzata con un valore '"+serviceBinding+"' non supportato:"+e.getMessage(),e);
		}
		if(ServiceBinding.SOAP.equals(as.getServiceBinding())) {
			as.setByteWsdlLogicoErogatore(bytesInterface);
		}
		String formatoSpecifica = null;
		try{
			formatoSpecifica = readProperty(properties, PROPERTIES_INTERFACE_TYPE, true);
			as.setFormatoSpecifica(FormatoSpecifica.toEnumConstant(formatoSpecifica, true));
		}catch(Exception e) {
			throw new ProtocolException("ERROR: 'pathProperties' contiene una proprietà '"+PROPERTIES_INTERFACE_TYPE+"' valorizzata con un valore '"+formatoSpecifica+"' non supportato:"+e.getMessage(),e);
		}
		
		String tipoSoggettoDefault = protocol.getManifest().getRegistry().getOrganization().getTypes().getType(0).getName(); 
		IdSoggetto assr = new IdSoggetto();
		assr.setTipo(tipoSoggettoDefault);
		assr.setNome(Costanti.NOME_SOGGETTO_DEFAULT);
		as.setSoggettoReferente(assr);
		
		String versioneApi = null;
		try {
			versioneApi = readProperty(properties, PROPERTIES_VERSION, true);
			as.setVersione(Integer.parseInt(versioneApi));
		}catch(Exception e) {
			throw new ProtocolException("ERROR: argomento 'pathProperties' contiene una proprietà '"+PROPERTIES_VERSION+"' valorizzata con un numero '"+versioneApi+"' che non rappresenta un intero:"+e.getMessage(),e);
		}
		
		String gruppi = readProperty(properties, PROPERTIES_TAG, false);
		if(gruppi!=null && StringUtils.isNotEmpty(gruppi)) {
			List<String> nomiGruppi = Arrays.asList(gruppi.split(","));
			for (String nomeGruppo : nomiGruppi) {
				nomeGruppo = nomeGruppo.trim();
				GruppoAccordo gruppoAccordo = new GruppoAccordo();
				gruppoAccordo.setNome(nomeGruppo);
				if(as.getGruppi()==null) {
					as.setGruppi(new GruppiAccordo());
				}
				as.getGruppi().addGruppo(gruppoAccordo );
			}
		}
		
		String canale = readProperty(properties, PROPERTIES_CANALE, false);
		if(canale!=null && StringUtils.isNotEmpty(canale)) {
			as.setCanale(canale);
		}
		
		return as;
	}

	private static String readProperty(Properties properties, String property, boolean required) throws ProtocolException {
		String v = properties.getProperty(property);
		if(v!=null) {
			v = v.trim();
		}
		if(required && 
				(v==null || StringUtils.isEmpty(v))
			) {
			throw new ProtocolException("Property '"+property+"' not found");
		}
		return v;
	}
	
	private static final String CAST_BOOLEAN = "(boolean) ";
	private static final String CAST_NUMBER = "(number) ";
	private static final String CAST_INT = "(int) ";
	private static final String CAST_LONG = "(long) ";
	private static final String CAST_FILE = "(file) ";
	private static void addProfileProperties(AccordoServizioParteComune as, Properties profileProperties) throws ProtocolException {
		if(profileProperties!=null && !profileProperties.isEmpty()) {
			for (Map.Entry<Object,Object> entry : profileProperties.entrySet()) {
				addProfileProperty(as, entry);
			}
		}
	}
	private static void addProfileProperty(AccordoServizioParteComune as, Map.Entry<Object,Object> entry) throws ProtocolException {
		String key = (String) entry.getKey();
		String value = (String) entry.getValue();
		ProtocolProperty pp = new ProtocolProperty();
		pp.setName(key);
		if(value.startsWith(CAST_BOOLEAN)) {
			pp.setBooleanValue(Boolean.parseBoolean(value.substring(CAST_BOOLEAN.length())));
		}
		else if(value.startsWith(CAST_NUMBER)) {
			pp.setNumberValue(Long.parseLong(value.substring(CAST_NUMBER.length())));
		}
		else if(value.startsWith(CAST_INT)) {
			pp.setNumberValue(Long.parseLong(value.substring(CAST_INT.length())));
		}
		else if(value.startsWith(CAST_LONG)) {
			pp.setNumberValue(Long.parseLong(value.substring(CAST_LONG.length())));
		}
		else if(value.startsWith(CAST_FILE)) {
			String file = value.substring(CAST_FILE.length());
			pp.setFile(file);
			try {
				pp.setByteFile(FileSystemUtilities.readBytesFromFile(file));
			}catch(Exception e) {
				throw new ProtocolException("ERROR: argomento 'pathProfileProperties' contiene una proprietà '"+key+"' valorizzata con un file '"+file+"' non accessibile:"+e.getMessage(),e);
			}
		}
		else {
			pp.setValue(value);
		}
		as.addProtocolProperty(pp);
	}
}
