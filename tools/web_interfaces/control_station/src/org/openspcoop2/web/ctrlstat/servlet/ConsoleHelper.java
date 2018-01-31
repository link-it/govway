/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.mail.BodyPart;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.CorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.MtomProcessor;
import org.openspcoop2.core.config.MtomProcessorFlowParameter;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.constants.RuoloTipoMatch;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.FiltroRicercaRuoli;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.protocol.sdk.constants.ConsoleInterfaceType;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemValueType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.AbstractConsoleItem;
import org.openspcoop2.protocol.sdk.properties.AbstractProperty;
import org.openspcoop2.protocol.sdk.properties.BaseConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BinaryConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BinaryProperty;
import org.openspcoop2.protocol.sdk.properties.BooleanConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BooleanProperty;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.NumberConsoleItem;
import org.openspcoop2.protocol.sdk.properties.NumberProperty;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesFactory;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.properties.StringConsoleItem;
import org.openspcoop2.protocol.sdk.properties.StringProperty;
import org.openspcoop2.utils.crypt.Password;
import org.openspcoop2.utils.mime.MimeMultipart;
import org.openspcoop2.utils.regexp.RegExpException;
import org.openspcoop2.utils.regexp.RegExpNotFoundException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.web.ctrlstat.config.ConsoleProperties;
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ControlStationLogger;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedMenuItem;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedMenu;
import org.openspcoop2.web.ctrlstat.servlet.ac.AccordiCooperazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.ac.AccordiCooperazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCore;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCostanti;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ExporterUtils;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCore;
import org.openspcoop2.web.ctrlstat.servlet.monitor.MonitorCostanti;
import org.openspcoop2.web.ctrlstat.servlet.operazioni.OperazioniCore;
import org.openspcoop2.web.ctrlstat.servlet.operazioni.OperazioniCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCostanti;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesCore;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesCostanti;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesUtilities;
import org.openspcoop2.web.ctrlstat.servlet.ruoli.RuoliCore;
import org.openspcoop2.web.ctrlstat.servlet.ruoli.RuoliCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.utenti.UtentiCore;
import org.openspcoop2.web.ctrlstat.servlet.utenti.UtentiCostanti;
import org.openspcoop2.web.lib.audit.web.AuditCostanti;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.MenuEntry;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.InterfaceType;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;
import org.openspcoop2.web.lib.users.dao.User;
import org.slf4j.Logger;

/**
 * ctrlstatHelper
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ConsoleHelper {

	protected HttpServletRequest request;
	public HttpServletRequest getRequest() {
		return this.request;
	}
	protected PageData pd;
	public PageData getPd() {
		return this.pd;
	}
	protected HttpSession session;
	public HttpSession getSession() {
		return this.session;
	}
	protected Password passwordManager;
	protected ControlStationCore core = null;
	public ControlStationCore getCore() {
		return this.core;
	}
	protected PddCore pddCore = null;
	protected SoggettiCore soggettiCore = null;
	protected UtentiCore utentiCore = null;
	protected ServiziApplicativiCore saCore = null;
	protected ArchiviCore archiviCore = null;
	protected AccordiServizioParteComuneCore apcCore = null;
	protected AccordiServizioParteSpecificaCore apsCore = null;
	protected PorteDelegateCore porteDelegateCore = null;
	protected PorteApplicativeCore porteApplicativeCore = null;
	protected AccordiCooperazioneCore acCore = null;
	protected ConfigurazioneCore confCore = null;
	protected ConnettoriCore connettoriCore = null;
	protected OperazioniCore operazioniCore = null;
	protected ProtocolPropertiesCore protocolPropertiesCore = null;
	protected RuoliCore ruoliCore = null;

	/** Gestione dei parametri unica sia per le chiamate multipart che per quelle normali*/
	private boolean multipart = false;
	public boolean isMultipart() {
		return this.multipart;
	}
	private String contentType = null; 
	public String getContentType() {
		return this.contentType;
	}
	private MimeMultipart mimeMultipart = null;
	private Map<String, InputStream> mapParametri = null;
	private Map<String, String> mapNomiFileParametri = null;
	private List<String> idBinaryParameterRicevuti = null;
	
	/** Logger utilizzato per debug. */
	protected Logger log = null;

	protected IDAccordoCooperazioneFactory idAccordoCooperazioneFactory = null;
	protected IDAccordoFactory idAccordoFactory = null;
	protected IDServizioFactory idServizioFactory = null;
	
	private static String tmpDirectory = null;
	
	public static String getTmpDir() throws Exception {
		if(tmpDirectory == null)
			initTmpDir();
		
		return tmpDirectory;
	}
	
	public static synchronized void initTmpDir() throws Exception {
		if(tmpDirectory == null){
			File file = File.createTempFile(CostantiControlStation.TEMP_FILE_PREFIX, CostantiControlStation.TEMP_FILE_SUFFIX);
			tmpDirectory = FilenameUtils.getFullPath(file.getAbsolutePath());
			if(file.exists())
				file.delete();
		}
	}
	
	/** Lunghezza label */
	protected int size = 50;
	
	
	/** Modalita Interfaccia */
	protected InterfaceType tipoInterfaccia = InterfaceType.STANDARD;
	
	public InterfaceType getTipoInterfaccia() {
		return this.tipoInterfaccia;
	}
	public void setTipoInterfaccia(InterfaceType tipoInterfaccia) {
		this.tipoInterfaccia = tipoInterfaccia;
	}
	public void updateTipoInterfaccia() {
		User user = ServletUtils.getUserFromSession(this.session);
		this.tipoInterfaccia = user.getInterfaceType();
	}
	public boolean isModalitaCompleta() {
		return InterfaceType.equals(this.tipoInterfaccia, 
				InterfaceType.COMPLETA); 
	}
	public boolean isModalitaAvanzata() {
		// considero anche l'interfaccia completa
		return InterfaceType.equals(this.tipoInterfaccia, 
				InterfaceType.AVANZATA,InterfaceType.COMPLETA); 
	}
	public boolean isModalitaStandard() {
		return InterfaceType.equals(this.tipoInterfaccia, 
				InterfaceType.STANDARD); 
	}


	public ConsoleHelper(HttpServletRequest request, PageData pd, HttpSession session) {
		this.request = request;
		this.pd = pd;
		this.session = session;
		this.passwordManager = new Password();
		this.log = ControlStationLogger.getPddConsoleCoreLogger();
		try {
			User user = ServletUtils.getUserFromSession(this.session);
			if(user != null) {
				this.tipoInterfaccia = user.getInterfaceType();
			}
			
			this.core = new ControlStationCore();
			this.pddCore = new PddCore(this.core);
			this.utentiCore = new UtentiCore(this.core);
			this.soggettiCore = new SoggettiCore(this.core);
			this.saCore = new ServiziApplicativiCore(this.core);
			this.archiviCore = new ArchiviCore(this.core);
			this.apcCore = new AccordiServizioParteComuneCore(this.core);
			this.apsCore = new AccordiServizioParteSpecificaCore(this.core);
			this.porteDelegateCore = new PorteDelegateCore(this.core);
			this.porteApplicativeCore = new PorteApplicativeCore(this.core);
			this.acCore = new AccordiCooperazioneCore(this.core);
			this.confCore = new ConfigurazioneCore(this.core);
			this.connettoriCore = new ConnettoriCore(this.core);
			this.operazioniCore = new OperazioniCore(this.core);
			this.protocolPropertiesCore = new ProtocolPropertiesCore(this.core);
			this.ruoliCore = new RuoliCore(this.core);

			this.idBinaryParameterRicevuti = new ArrayList<String>();
			// analisi dei parametri della request
			this.contentType = request.getContentType();
			if ((this.contentType != null) && (this.contentType.indexOf(Costanti.MULTIPART) != -1)) {
				this.multipart = true;
				this.mimeMultipart = new MimeMultipart(request.getInputStream(), this.contentType);
				this.mapParametri = new HashMap<String,InputStream>();
				this.mapNomiFileParametri = new HashMap<String,String>();

				for(int i = 0 ; i < this.mimeMultipart.countBodyParts() ;  i ++) {
					BodyPart bodyPart = this.mimeMultipart.getBodyPart(i);
					String partName = getBodyPartName(bodyPart);
					if(!this.mapParametri.containsKey(partName)) {
						if(partName.startsWith(Costanti.PARAMETER_FILEID_PREFIX)){
							this.idBinaryParameterRicevuti.add(partName);
						}
						this.mapParametri.put(partName, bodyPart.getInputStream());
						String fileName = getBodyPartFileName(bodyPart);
						if(fileName != null)
							this.mapNomiFileParametri.put(partName, fileName);

					}else throw new Exception("Parametro ["+partName+"] Duplicato.");
				}
			} else {
				@SuppressWarnings("unchecked")
				Enumeration<String> parameterNames = this.request.getParameterNames();
				while (parameterNames.hasMoreElements()) {
					String param = (String) parameterNames.nextElement();
					if(param.startsWith(Costanti.PARAMETER_FILEID_PREFIX)){
						this.idBinaryParameterRicevuti.add(param);
					}
				}
			}
			
			this.size = ConsoleProperties.getInstance().getConsoleLunghezzaLabel();
		} catch (Exception e) {
			this.log.error("Exception ctrlstatHelper: " + e.getMessage(), e);
		}
		this.idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();
		this.idAccordoFactory = IDAccordoFactory.getInstance();
		this.idServizioFactory = IDServizioFactory.getInstance();
	}
	
	public IDAccordo getIDAccordoFromValues(String nomeAS, String soggettoReferente, String versione) throws Exception{
		Soggetto s = this.soggettiCore.getSoggetto(Integer.parseInt(soggettoReferente));			
		IDSoggetto assr = new IDSoggetto();
		assr.setTipo(s.getTipo());
		assr.setNome(s.getNome());
		return this.idAccordoFactory.getIDAccordoFromValues(nomeAS, assr, Integer.parseInt(versione));
	}
	
	public IDServizio getIDServizioFromValues(String tipo, String nome, String soggettoErogatore, String versione) throws Exception{
		Soggetto s = this.soggettiCore.getSoggetto(Integer.parseInt(soggettoErogatore));			
		IDSoggetto assr = new IDSoggetto();
		assr.setTipo(s.getTipo());
		assr.setNome(s.getNome());
		return this.idServizioFactory.getIDServizioFromValues(tipo, nome, assr, Integer.parseInt(versione));
	}
	
	public IDServizio getIDServizioFromValues(String tipo, String nome, String tipoSoggettoErogatore, String soggettoErogatore, String versione) throws Exception{
		IDSoggetto assr = new IDSoggetto();
		assr.setTipo(tipoSoggettoErogatore);
		assr.setNome(soggettoErogatore);
		return this.idServizioFactory.getIDServizioFromValues(tipo, nome, assr, Integer.parseInt(versione));
	}
	
	public int getSize() {
		return this.size;
	}

	private String getBodyPartName (BodyPart bodyPart) throws Exception{
		String partName =  null;
		String[] headers = bodyPart.getHeader(CostantiControlStation.PARAMETRO_CONTENT_DISPOSITION);
		if(headers != null && headers.length > 0){
			String header = headers[0];

			// in due parti perche il suffisso con solo " imbrogliava il controllo
			int prefixIndex = header.indexOf(CostantiControlStation.PREFIX_CONTENT_DISPOSITION) + CostantiControlStation.PREFIX_CONTENT_DISPOSITION.length();
			partName = header.substring(prefixIndex);

			int suffixIndex = partName.indexOf(CostantiControlStation.SUFFIX_CONTENT_DISPOSITION);
			partName = partName.substring(0,suffixIndex);
		}

		return partName;
	}

	private String getBodyPartFileName (BodyPart bodyPart) throws Exception{
		String partName =  null;
		String[] headers = bodyPart.getHeader(CostantiControlStation.PARAMETRO_CONTENT_DISPOSITION);
		if(headers != null && headers.length > 0){
			String header = headers[0];

			// in due parti perche il suffisso con solo " imbrogliava il controllo
			int prefixIndex = header.indexOf(CostantiControlStation.PREFIX_FILENAME);
			if(prefixIndex > -1){
				partName = header.substring(prefixIndex + CostantiControlStation.PREFIX_FILENAME.length());

				int suffixIndex = partName.indexOf(CostantiControlStation.SUFFIX_FILENAME);
				partName = partName.substring(0,suffixIndex);
			}
		}

		return partName;
	}

	//	public String getProtocolloFromParameter(String parameterName) throws Exception {
	//		return getParameter(parameterName, String.class, this.core.getProtocolloDefault());
	//	}

	public String getParameter(String parameterName) throws Exception {
		return getParameter(parameterName, String.class, null);
	}

	public <T> T getParameter(String parameterName, Class<T> type) throws Exception {
		return getParameter(parameterName, type, null);
	}

	public <T> T getParameter(String parameterName, Class<T> type, T defaultValue) throws Exception {
		T toReturn = null;

		if(type == byte[].class){
			throw new Exception("Per leggere un parametro di tipo byte[] utilizzare il metodo getBinaryParameter");
		}

		String paramAsString = null;

		if(this.multipart){
			InputStream inputStream = this.mapParametri.get(parameterName);
			if(inputStream != null){
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				IOUtils.copy(inputStream, baos);
				baos.flush();
				baos.close();
				paramAsString = baos.toString();
			}
		}else{
			paramAsString = this.request.getParameter(parameterName);
		}

		if(paramAsString != null) {
			Constructor<T> constructor = type.getConstructor(String.class);
			if(constructor != null)
				toReturn = constructor.newInstance(paramAsString);
			else
				toReturn = type.cast(paramAsString);
		}

		if(toReturn == null && defaultValue != null)
			return defaultValue;


		return toReturn;
	}
	
	public byte[] getBinaryParameterContent(String parameterName) throws Exception {
		if(this.multipart){
			InputStream inputStream = this.mapParametri.get(parameterName);
			if(inputStream != null){
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				IOUtils.copy(inputStream, baos);
				return baos.toByteArray();
			}
		}else{
			String paramAsString = this.request.getParameter(parameterName);
			if(paramAsString != null)
				return paramAsString.getBytes();
		}

		return null;
	}

	public String getFileNameParameter(String parameterName) throws Exception {
		if(this.multipart){
			return this.mapNomiFileParametri.get(parameterName);
		} else 
			return this.request.getParameter(parameterName);

	}
	
	public BinaryParameter getBinaryParameter(String parameterName) throws Exception {
		BinaryParameter bp = new BinaryParameter();
		bp.setName(parameterName); 
		String filename = null;
		String fileId = null;
		File file = null;
		FileOutputStream fos =  null;
		FileInputStream fis =  null;
		ByteArrayInputStream bais = null;
		ByteArrayOutputStream baos = null;
		
		// 1. provo a prelevare il valore dalla request
		byte [] bpContent = this.getBinaryParameterContent(parameterName);
		
		if(bpContent != null && bpContent.length > 0) {
			//  cancello eventuale vecchio contenuto
			filename = this.getParameter(ProtocolPropertiesCostanti.PARAMETER_FILENAME_PREFIX + parameterName);
			fileId = this.getParameter(ProtocolPropertiesCostanti.PARAMETER_FILEID_PREFIX+ parameterName);
			if(StringUtils.isNotBlank(fileId) && StringUtils.isNotBlank(filename)){
				file = new File(getTmpDir() + File.separator + CostantiControlStation.TEMP_FILE_PREFIX +  fileId + CostantiControlStation.TEMP_FILE_SUFFIX);
				if(file.exists())
					file.delete();
			}

			//salvataggio nuovo contenuto
			filename = this.getFileNameParameter(parameterName);
			file = File.createTempFile(CostantiControlStation.TEMP_FILE_PREFIX, CostantiControlStation.TEMP_FILE_SUFFIX);
			fileId = file.getName().substring(0, file.getName().indexOf(CostantiControlStation.TEMP_FILE_SUFFIX));
			fileId = fileId.substring(fileId.indexOf(CostantiControlStation.TEMP_FILE_PREFIX) + CostantiControlStation.TEMP_FILE_PREFIX.length());
			
			try{
				bais = new ByteArrayInputStream(bpContent);
				fos = new FileOutputStream(file);
				
				IOUtils.copy(bais, fos);
			}
			finally {
				bais.close();
				fos.close();
			}
			
			
		} else {
			// provo a ricostruire il valore dai campi hidden
			filename = this.getParameter(ProtocolPropertiesCostanti.PARAMETER_FILENAME_PREFIX + parameterName);
			fileId = this.getParameter(ProtocolPropertiesCostanti.PARAMETER_FILEID_PREFIX+ parameterName);
			
			if(StringUtils.isNotBlank(fileId) && StringUtils.isNotBlank(filename)){
				file = new File(getTmpDir() + File.separator + CostantiControlStation.TEMP_FILE_PREFIX + fileId + CostantiControlStation.TEMP_FILE_SUFFIX);
				
				// puo' non esistere allora il valore e' vuoto
				if(file.exists()){
					try{
						fis = new FileInputStream(file);
						baos = new ByteArrayOutputStream();
						
						IOUtils.copy(fis, baos);
						
						baos.flush();
						bpContent = baos.toByteArray();
						
					}
					finally {
						fis.close();
						baos.close();
					}
				} else {
					bpContent = null;
				}
			} else {
				bpContent = null;
				filename = null;
				fileId = null;
			}
		}
		
		bp.setValue(bpContent);
		bp.setFilename(filename); 
		bp.setId(fileId);  
		return bp;
	}
	
	/***
	 * cancella i file temporanei dei parametri binari del protocollo, da usare dopo il salvataggio dell'oggetto.
	 * 
	 * @param protocolProperties
	 * @throws Exception
	 */
	public void deleteBinaryProtocolPropertiesTmpFiles(ProtocolProperties protocolProperties) throws Exception{
		if(protocolProperties!= null)
			for (int i = 0; i < protocolProperties.sizeProperties(); i++) {
				AbstractProperty<?> property = protocolProperties.getProperty(i);
				if(property instanceof BinaryProperty){
					BinaryProperty bProp = (BinaryProperty) property;
					BinaryParameter bp = new BinaryParameter();
					bp.setName(bProp.getId());
					bp.setFilename(bProp.getFileName());
					bp.setId(bProp.getFileId());
					bp.setValue(bProp.getValue());
					
					this.deleteBinaryParameter(bp); 
				}
			}
	}
	
	/***
	 * 
	 * cancella il file temporaneo del parametro binari del protocollo con id passato come parametro, da usare dopo il salvataggio dell'oggetto.
	 * 
	 * @param protocolProperties
	 * @param propertyId
	 * @throws Exception
	 */
	public void deleteBinaryProtocolPropertyTmpFiles(ProtocolProperties protocolProperties, String propertyId) throws Exception{
		if(protocolProperties!= null)
			for (int i = 0; i < protocolProperties.sizeProperties(); i++) {
				AbstractProperty<?> property = protocolProperties.getProperty(i);
				if(property instanceof BinaryProperty && property.getId().equals(propertyId)){
					BinaryProperty bProp = (BinaryProperty) property;
					BinaryParameter bp = new BinaryParameter();
					bp.setName(bProp.getId());
					bp.setFilename(bProp.getFileName());
					bp.setId(bProp.getFileId());
					bp.setValue(bProp.getValue());
					
					this.deleteBinaryParameter(bp); 
					break;
				}
			}
	}
	
	/****
	 * 
	 * cancella il file temporaneo del parametro binari del protocollo con id e alias passato come parametro, da usare dopo il salvataggio dell'oggetto nella schermata di modifica.
	 * 
	 * @param protocolProperties
	 * @param propertyId
	 * @param aliasId
	 * @throws Exception
	 */
	public void deleteBinaryProtocolPropertyTmpFiles(ProtocolProperties protocolProperties, String propertyId, String aliasId) throws Exception{
		if(protocolProperties!= null)
			for (int i = 0; i < protocolProperties.sizeProperties(); i++) {
				AbstractProperty<?> property = protocolProperties.getProperty(i);
				if(property instanceof BinaryProperty && property.getId().equals(propertyId)){
					BinaryProperty bProp = (BinaryProperty) property;
					BinaryParameter bp = new BinaryParameter();
					bp.setName(aliasId);
					bp.setFilename(bProp.getFileName());
					bp.setId(bProp.getFileId());
					bp.setValue(bProp.getValue());
					
					this.deleteBinaryParameter(bp); 
					break;
				}
			}
	}
	
	/****
	 * 
	 * Cancella tutti i file temporanei dei protocol properties ricevuti, da usare durante la fase di edit e postback.
	 * 
	 * 
	 * @param parameters
	 * @throws Exception
	 */
	public void deleteProtocolPropertiesBinaryParameters(BinaryParameter ... parameters) throws Exception{
		File file = null;
		if(this.idBinaryParameterRicevuti != null && this.idBinaryParameterRicevuti.size() >0){
			for (String bp : this.idBinaryParameterRicevuti) {
				boolean delete = true;
				if(parameters != null && parameters.length >0){
					for (BinaryParameter binaryParameter : parameters) {
						if(binaryParameter.getId() != null && binaryParameter.getId().equals(bp)){
							delete = false;
							break;
						}
					}
				}
				
				if(delete){
					String val = this.getParameter(bp);
					file = new File(getTmpDir() + File.separator + CostantiControlStation.TEMP_FILE_PREFIX +  val + CostantiControlStation.TEMP_FILE_SUFFIX);
					
					if(file.exists())
						file.delete();
				}
			}
		}
	}
	
	/***
	 * 
	 * Cancella i file temporanei per i parametri passati.
	 * 
	 * @param parameters
	 * @throws Exception
	 */
	public void deleteBinaryParameters(BinaryParameter ... parameters) throws Exception{
		if(parameters != null && parameters.length >0){
			for (BinaryParameter binaryParameter : parameters) {
				this.deleteBinaryParameter(binaryParameter);
			}
		}
	}
	
	private void deleteBinaryParameter(BinaryParameter bp) throws Exception{
		File file = null;
		if(StringUtils.isNotBlank(bp.getId())){
			file = new File(getTmpDir() + File.separator + CostantiControlStation.TEMP_FILE_PREFIX +  bp.getId() + CostantiControlStation.TEMP_FILE_SUFFIX);
			
			if(file.exists())
				file.delete();
		}
		
		bp.setValue(null);
		bp.setFilename(null);
		bp.setId(null); 
	}


	public ProtocolProperties estraiProtocolPropertiesDaRequest(ConsoleConfiguration consoleConfiguration,ConsoleOperationType consoleOperationType,
			String propertyId, BinaryParameter contenutoDocumentoParameter) throws Exception {
		
		String editMode = this.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
		String postBackElementName = this.getParameter(Costanti.POSTBACK_ELEMENT_NAME);
		boolean primoAccessoAdd = (ConsoleOperationType.ADD.equals(consoleOperationType) && 
				(editMode==null || 
				CostantiControlStation.PARAMETRO_PROTOCOLLO.equals(postBackElementName) ||  // per default in accordo parte comune
				AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ACCORDO.equals(postBackElementName))  // per default in accordo parte aspecifica
				);
		
		ProtocolProperties properties = new ProtocolProperties();

		List<BaseConsoleItem> consoleItems = consoleConfiguration.getConsoleItem();

		for (BaseConsoleItem item : consoleItems) {
			// per ora prelevo solo i parametri che possono avere un valore non considero titoli e note
			if(item instanceof AbstractConsoleItem<?>){
				ConsoleItemValueType consoleItemValueType = ProtocolPropertiesUtils.getConsoleItemValueType(item);
				if(consoleItemValueType != null){
					switch (consoleItemValueType) {
					case BINARY:
						BinaryParameter bp = contenutoDocumentoParameter;
						if(propertyId == null || !propertyId.equals(item.getId())){
							bp = this.getBinaryParameter(item.getId());
						} else {
							bp.setName(item.getId()); 
						}
						BinaryProperty binaryProperty = ProtocolPropertiesFactory.newProperty(bp.getName(), bp.getValue(), bp.getFilename(), bp.getId());
						properties.addProperty(binaryProperty); 
						break;
					case NUMBER:
						String lvS = this.getParameter(item.getId());
						Long longValue = StringUtils.isNotEmpty(lvS) ? Long.parseLong(lvS) : null;
						NumberProperty numberProperty = ProtocolPropertiesFactory.newProperty(item.getId(), longValue);
						if(primoAccessoAdd) {
							numberProperty.setValue(((NumberConsoleItem) item).getDefaultValue());
						}
						properties.addProperty(numberProperty); 
						break;
					case BOOLEAN:
						String bvS = this.getParameter(item.getId());
						Boolean booleanValue = ServletUtils.isCheckBoxEnabled(bvS);
						BooleanProperty booleanProperty = ProtocolPropertiesFactory.newProperty(item.getId(), booleanValue ? booleanValue : null);
						if(primoAccessoAdd) {
							booleanProperty.setValue(((BooleanConsoleItem) item).getDefaultValue());
						}
						properties.addProperty(booleanProperty); 
						break;
					case STRING:
					default:
						String parameterValue = this.getParameter(item.getId());
						StringProperty stringProperty = ProtocolPropertiesFactory.newProperty(item.getId(), parameterValue);
						if(primoAccessoAdd) {
							stringProperty.setValue(((StringConsoleItem) item).getDefaultValue());
						}
						properties.addProperty(stringProperty);
						break;
					}
				}
			}
		}

		return properties;
	}
	public ProtocolProperties estraiProtocolPropertiesDaRequest(ConsoleConfiguration consoleConfiguration,ConsoleOperationType consoleOperationType) throws Exception {
		return estraiProtocolPropertiesDaRequest(consoleConfiguration, consoleOperationType, null , null);
	}

	// Prepara il menu'
	public void makeMenu() throws Exception {
		try {
			String userLogin = ServletUtils.getUserLoginFromSession(this.session);

			ExporterUtils exporterUtils = new ExporterUtils(this.archiviCore);

			PermessiUtente pu = null;
			User u = this.utentiCore.getUser(userLogin);
			pu = u.getPermessi();

			Boolean singlePdD = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_SINGLE_PDD);

			Boolean showAccordiCooperazione = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_VISUALIZZA_ACCORDI_COOPERAZIONE);

			Boolean isModalitaAvanzata = this.isModalitaAvanzata();
			Boolean isVisualizzazioneCompattaElementiRegistro = this.core.isShowMenuAggregatoOggettiRegistro();

			List<IExtendedMenu> extendedMenu = this.core.getExtendedMenu();

			Vector<MenuEntry> menu = new Vector<MenuEntry>();

			// Vecchia Grafica
			if(!isVisualizzazioneCompattaElementiRegistro){
				if (pu.isServizi()) {
					if(this.core.isRegistroServiziLocale()){
						if (singlePdD == false) {
							MenuEntry me = new MenuEntry();
							me.setTitle(PddCostanti.LABEL_PORTE_DI_DOMINIO);
							String[][] entries = null;
							if(this.core.isShowPulsanteAggiungiMenu()){
								entries = new String[2][2];
							}else{
								entries = new String[1][2];
							}
							entries[0][0] = Costanti.PAGE_DATA_TITLE_LABEL_VISUALIZZA;
							entries[0][1] = PddCostanti.SERVLET_NAME_PDD_LIST;
							if(this.core.isShowPulsanteAggiungiMenu()){
								entries[1][0] = Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI;
								entries[1][1] = PddCostanti.SERVLET_NAME_PDD_ADD;
							}
							me.setEntries(entries);
							menu.addElement(me);
						} else {
							MenuEntry me = new MenuEntry();
							me.setTitle(PddCostanti.LABEL_PORTE_DI_DOMINIO);
							String[][] entries = null;
							if(this.core.isShowPulsanteAggiungiMenu()){
								entries = new String[2][2];
							}else{
								entries = new String[1][2];
							}
							entries[0][0] = Costanti.PAGE_DATA_TITLE_LABEL_VISUALIZZA;
							entries[0][1] = PddCostanti.SERVLET_NAME_PDD_SINGLEPDD_LIST;
							if(this.core.isShowPulsanteAggiungiMenu()){
								entries[1][0] = Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI;
								entries[1][1] = PddCostanti.SERVLET_NAME_PDD_SINGLEPDD_ADD;
							}
							me.setEntries(entries);
							menu.addElement(me);
						}
					}

					MenuEntry me = new MenuEntry();
					me.setTitle(SoggettiCostanti.LABEL_SOGGETTI);
					String[][] entries = null;
					if(this.core.isShowPulsanteAggiungiMenu()){
						entries = new String[2][2];
					}else{
						entries = new String[1][2];
					}
					entries[0][0] = Costanti.PAGE_DATA_TITLE_LABEL_VISUALIZZA;
					entries[0][1] = SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST;
					if(this.core.isShowPulsanteAggiungiMenu()){
						entries[1][0] = Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI;
						entries[1][1] = SoggettiCostanti.SERVLET_NAME_SOGGETTI_ADD;
					}
					me.setEntries(entries);
					menu.addElement(me);

					me = new MenuEntry();
					me.setTitle(ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI);
					if(this.core.isShowPulsanteAggiungiMenu()){
						entries = new String[2][2];
					}else{
						entries = new String[1][2];
					}
					entries[0][0] = Costanti.PAGE_DATA_TITLE_LABEL_VISUALIZZA;
					entries[0][1] = ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST;
					if(this.core.isShowPulsanteAggiungiMenu()){
						entries[1][0] = Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI;
						entries[1][1] = ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ADD;
					}
					me.setEntries(entries);
					menu.addElement(me);

					if(this.core.isRegistroServiziLocale()){

						me = new MenuEntry();
						me.setTitle(AccordiServizioParteComuneCostanti.LABEL_APC);
						if(this.core.isShowPulsanteAggiungiMenu()){
							entries = new String[2][2];
						}else{
							entries = new String[1][2];
						}
						entries[0][0] = Costanti.PAGE_DATA_TITLE_LABEL_VISUALIZZA;
						entries[0][1] = AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
								AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO+"="+
								AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE;
						int index = 1;
						if(this.core.isShowPulsanteAggiungiMenu()){
							entries[index][0] = Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI;
							entries[index][1] = AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_ADD+"?"+
									AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO+"="+
									AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE; 
							index++;
						}
						me.setEntries(entries);
						menu.addElement(me);

						me = new MenuEntry();
						me.setTitle(AccordiServizioParteSpecificaCostanti.LABEL_APS);
						if(this.core.isShowPulsanteAggiungiMenu()){
							entries = new String[2][2];
						}else{
							entries = new String[1][2];
						}
						entries[0][0] = Costanti.PAGE_DATA_TITLE_LABEL_VISUALIZZA;
						entries[0][1] = AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST;
						index = 1;
						if(this.core.isShowPulsanteAggiungiMenu()){
							entries[index][0] = Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI;
							entries[index][1] = AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD;
							index++;
						}
						me.setEntries(entries);
						menu.addElement(me);

					}
				}

				// Accordi di cooperazione 
				if(showAccordiCooperazione){
					if(this.core.isRegistroServiziLocale()){
						MenuEntry me = new MenuEntry();
						String[][] entries = null;


						me.setTitle(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE);
						if(this.core.isShowPulsanteAggiungiMenu()){
							entries = new String[2][2];
						}else{
							entries = new String[1][2];
						}
						entries[0][0] = Costanti.PAGE_DATA_TITLE_LABEL_VISUALIZZA;
						entries[0][1] = AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST;
						int index = 1;
						if(this.core.isShowPulsanteAggiungiMenu()){
							entries[index][0] = Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI;
							entries[index][1] = AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_ADD;
							index++;
						}
						me.setEntries(entries);
						menu.addElement(me);

						me = new MenuEntry();
						me.setTitle(AccordiServizioParteComuneCostanti.LABEL_ASC);
						if(this.core.isShowPulsanteAggiungiMenu()){
							entries = new String[2][2];
						}else{
							entries = new String[1][2];
						}
						entries[0][0] = Costanti.PAGE_DATA_TITLE_LABEL_VISUALIZZA;
						entries[0][1] = AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
								AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO+"="+
								AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_SERVIZIO_COMPOSTO;
						index = 1;
						if(this.core.isShowPulsanteAggiungiMenu()){
							entries[index][0] = Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI;
							entries[index][1] = AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_ADD+"?"+
									AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO+"="+
									AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_SERVIZIO_COMPOSTO;
							index++;
						}
						me.setEntries(entries);
						menu.addElement(me);
					}
				}
			}else {

				/**** INIZIO SEZIONE VISUALIZZAZIONE COMPATTA *****/

				if(pu.isServizi() || pu.isAccordiCooperazione()){
					// Oggetti del registro compatti
					MenuEntry me = new MenuEntry();
					String[][] entries = null;
					me.setTitle(Costanti.PAGE_DATA_TITLE_LABEL_REGISTRO);

					//Calcolo del numero di entries
					int totEntries = 0;
					// PdD, Soggetti, SA, ASPC e ASPS con permessi S
					if(pu.isServizi()){
						// Link PdD
						if(this.core.isRegistroServiziLocale()){
							if(this.core.isGestionePddAbilitata()) {
								totEntries ++;
							}
						}

						// Soggetti ed SA
						totEntries += 2;

						// ASPC e ASPS
						if(this.core.isRegistroServiziLocale()){
							totEntries +=2;
						}
					}

					// Cooperazione e Accordi Composti con permessi P
					if(pu.isAccordiCooperazione()){
						if(this.core.isRegistroServiziLocale()){
							totEntries +=2;
						}
					}
	
					// Ruoli
					if(pu.isServizi()){
						if(this.core.isRegistroServiziLocale()){
							totEntries +=1;
						}
					}
					
					// PA e PD con permessi S e interfaccia avanzata
					if(pu.isServizi() && isModalitaAvanzata){
						totEntries +=2;
					}

					// Extended Menu
					if(extendedMenu!=null){
						for (IExtendedMenu extMenu : extendedMenu) {
							List<ExtendedMenuItem> list = 
									extMenu.getExtendedItemsMenuRegistro(isModalitaAvanzata, 
											this.core.isRegistroServiziLocale(), singlePdD, pu);
							if(list!=null && list.size()>0){
								totEntries +=list.size();
							}
						}
					}

					// Creo le entries e le valorizzo
					entries = new String[totEntries][2];

					int index = 0;
					// PdD, Soggetti, SA, ASPC e ASPS con permessi S
					if(pu.isServizi()){
						//Link PdD
						if(this.core.isRegistroServiziLocale()){
							if(this.core.isGestionePddAbilitata()) {
								entries[index][0] = PddCostanti.LABEL_PDD_MENU_VISUALE_AGGREGATA;
								if (singlePdD == false) {
									entries[index][1] = PddCostanti.SERVLET_NAME_PDD_LIST;
								}else {
									entries[index][1] = PddCostanti.SERVLET_NAME_PDD_SINGLEPDD_LIST;
								}
								index++;
							}
						}

						// Soggetti 
						entries[index][0] = SoggettiCostanti.LABEL_SOGGETTI_MENU_VISUALE_AGGREGATA;
						entries[index][1] = SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST;
						index++;

						//SA
						entries[index][0] = ServiziApplicativiCostanti.LABEL_SA_MENU_VISUALE_AGGREGATA;
						entries[index][1] = ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST;
						index++;

						// ASPC e ASPS
						if(this.core.isRegistroServiziLocale()){
							//ASPC
							entries[index][0] = AccordiServizioParteComuneCostanti.LABEL_APC_MENU_VISUALE_AGGREGATA;
							entries[index][1] = AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
									AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO+"="+
									AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE;
							index++;

							//ASPS
							entries[index][0] = AccordiServizioParteSpecificaCostanti.LABEL_APS_MENU_VISUALE_AGGREGATA;
							entries[index][1] = AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST;
							index++;
						}
					}

					// Cooperazione e Accordi Composti con permessi P
					if(pu.isAccordiCooperazione()){
						if(this.core.isRegistroServiziLocale()){
							//COOPERAZIONE
							entries[index][0] = AccordiCooperazioneCostanti.LABEL_AC_MENU_VISUALE_AGGREGATA;
							entries[index][1] = AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST;
							index++;

							// COMPOSTO
							entries[index][0] = AccordiServizioParteComuneCostanti.LABEL_ASC_MENU_VISUALE_AGGREGATA;
							entries[index][1] = AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
									AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO+"="+
									AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_SERVIZIO_COMPOSTO;
							index++;
						}
					}
					
					// Ruoli
					if(pu.isServizi()){
						if(this.core.isRegistroServiziLocale()){
							entries[index][0] = RuoliCostanti.LABEL_RUOLI;
							entries[index][1] = RuoliCostanti.SERVLET_NAME_RUOLI_LIST;
							index++;
						}
					}

					// PA e PD con permessi S e interfaccia avanzata
					if(pu.isServizi() && isModalitaAvanzata){
						//PD
						entries[index][0] = PorteDelegateCostanti.LABEL_PD_MENU_VISUALE_AGGREGATA;
						entries[index][1] = PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST;
						index++;

						//PA
						entries[index][0] = PorteApplicativeCostanti.LABEL_PA_MENU_VISUALE_AGGREGATA;
						entries[index][1] = PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST;
						index++;
					}

					// Extended Menu
					if(extendedMenu!=null){
						for (IExtendedMenu extMenu : extendedMenu) {
							List<ExtendedMenuItem> list = 
									extMenu.getExtendedItemsMenuRegistro(isModalitaAvanzata, 
											this.core.isRegistroServiziLocale(), singlePdD, pu);
							if(list!=null){
								for (ExtendedMenuItem extendedMenuItem : list) {
									entries[index][0] = extendedMenuItem.getLabel();
									entries[index][1] = extendedMenuItem.getUrl();
									index++;
								}
							}
						}
					}

					me.setEntries(entries);
					menu.addElement(me);
				}
			}




			if (singlePdD) {

				// SinglePdD=true
				if (pu.isDiagnostica()) {
					MenuEntry me = new MenuEntry();
					me.setTitle(CostantiControlStation.LABEL_STRUMENTI);

					int totEntries = 2;
					// Se l'utente ha anche i permessi "auditing", la
					// sezione reportistica ha una voce in più
					if (pu.isAuditing())
						totEntries++;
					// Se l'utente ha anche i permessi "monitoraggio", la
					// sezione reportistica ha una voce in più
					if (pu.isCodeMessaggi())
						totEntries++;

					// Extended Menu
					if(extendedMenu!=null){
						for (IExtendedMenu extMenu : extendedMenu) {
							List<ExtendedMenuItem> list = 
									extMenu.getExtendedItemsMenuStrumenti(isModalitaAvanzata, 
											this.core.isRegistroServiziLocale(), singlePdD, pu);
							if(list!=null && list.size()>0){
								totEntries +=list.size();
							}
						}
					}

					String[][] entries = new String[totEntries][2];
					int i = 0;
					entries[i][0] = ArchiviCostanti.LABEL_DIAGNOSTICA;
					entries[i][1] = ArchiviCostanti.SERVLET_NAME_ARCHIVI_DIAGNOSTICA;
					i++;
					entries[i][0] = ArchiviCostanti.LABEL_TRACCIAMENTO;
					entries[i][1] = ArchiviCostanti.SERVLET_NAME_ARCHIVI_TRACCIAMENTO;
					i++;
					if (pu.isAuditing()) {
						entries[i][0] = AuditCostanti.LABEL_AUDIT;
						entries[i][1] = AuditCostanti.SERVLET_NAME_AUDITING;
						i++;
					}

					if (pu.isCodeMessaggi()) {
						entries[i][0] = MonitorCostanti.LABEL_MONITOR;
						entries[i][1] = MonitorCostanti.SERVLET_NAME_MONITOR;
						i++;
					}

					// Extended Menu
					if(extendedMenu!=null){
						for (IExtendedMenu extMenu : extendedMenu) {
							List<ExtendedMenuItem> list = 
									extMenu.getExtendedItemsMenuStrumenti(isModalitaAvanzata, 
											this.core.isRegistroServiziLocale(), singlePdD, pu);
							if(list!=null){								
								for (ExtendedMenuItem extendedMenuItem : list) {
									entries[i][0] = extendedMenuItem.getLabel();
									entries[i][1] = extendedMenuItem.getUrl();
									i++;
								}
							}
						}
					}

					me.setEntries(entries);
					menu.addElement(me);

				}


				// Label Configurazione
				if(pu.isSistema()){

					MenuEntry me = new MenuEntry();
					me.setTitle(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE);
					// Se l'utente ha anche i permessi "utenti", la
					// configurazione utente la gestisco dopo
					String[][] entries = null;
					String[][] entriesUtenti = null;
					int dimensioneEntries = 0;


					dimensioneEntries = 2; // configurazione e audit
					List<String> aliases = this.confCore.getJmxPdD_aliases();
					if(aliases!=null && aliases.size()>0){
						dimensioneEntries++; // runtime
					}
					if(this.core.isShowPulsantiImportExport() && pu.isServizi()){
						dimensioneEntries++; // importa
						if(exporterUtils.existsAtLeastOneExportMpde(ArchiveType.CONFIGURAZIONE, this.session)){
							dimensioneEntries++; // esporta
							if(isModalitaAvanzata){
								dimensioneEntries++; // elimina
							}
						}
					}
					if (!pu.isUtenti()){
						//dimensioneEntries++; // change password
					}else {
						entriesUtenti = getVoceMenuUtenti();
						dimensioneEntries += entriesUtenti.length;
					}

					// Extended Menu
					if(extendedMenu!=null){
						for (IExtendedMenu extMenu : extendedMenu) {
							List<ExtendedMenuItem> list = 
									extMenu.getExtendedItemsMenuConfigurazione(isModalitaAvanzata, 
											this.core.isRegistroServiziLocale(), singlePdD, pu);
							if(list!=null && list.size()>0){
								dimensioneEntries +=list.size();
							}
						}
					}

					entries = new String[dimensioneEntries][2];

					int index = 0;
					entries[index][0] = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE;
					entries[index][1] = ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE;
					index++;
					if(aliases!=null && aliases.size()>0){
						entries[index][0] = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SISTEMA;
						entries[index][1] = ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_SISTEMA_ADD;
						index++;
					}
					// link utenti sotto quello di configurazione  generale
					if (pu.isUtenti()) {
						for (int j = 0; j < entriesUtenti.length; j++) {
							entries[index][0] = entriesUtenti[j][0];
							entries[index][1] = entriesUtenti[j][1];
							index++;		
						}
					}
					if(this.core.isShowPulsantiImportExport() && pu.isServizi()){
						entries[index][0] = ArchiviCostanti.LABEL_ARCHIVI_IMPORT;
						entries[index][1] = ArchiviCostanti.SERVLET_NAME_ARCHIVI_IMPORT+"?"+
								ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORTER_MODALITA+"="+ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORTER_MODALITA_IMPORT;
						index++;
						if(exporterUtils.existsAtLeastOneExportMpde(ArchiveType.CONFIGURAZIONE, this.session)){
							entries[index][0] = ArchiviCostanti.LABEL_ARCHIVI_EXPORT;
							entries[index][1] = ArchiviCostanti.SERVLET_NAME_ARCHIVI_EXPORT+"?"+ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_TIPO+"="+ArchiveType.CONFIGURAZIONE.name();
							index++;

							if(isModalitaAvanzata){
								entries[index][0] = ArchiviCostanti.LABEL_ARCHIVI_ELIMINA;
								entries[index][1] = ArchiviCostanti.SERVLET_NAME_ARCHIVI_IMPORT+"?"+
										ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORTER_MODALITA+"="+ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORTER_MODALITA_ELIMINA;
								index++;
							}
						}
					}
					entries[index][0] = AuditCostanti.LABEL_AUDIT;
					entries[index][1] = AuditCostanti.SERVLET_NAME_AUDIT;
					index++;

					//link cambio password
					if (!pu.isUtenti()) {
//						entries[index][0] = UtentiCostanti.LABEL_UTENTE;
//						entries[index][1] = UtentiCostanti.SERVLET_NAME_UTENTE_CHANGE;
//						index++;
					}

					// Extended Menu
					if(extendedMenu!=null){
						for (IExtendedMenu extMenu : extendedMenu) {
							List<ExtendedMenuItem> list = 
									extMenu.getExtendedItemsMenuConfigurazione(isModalitaAvanzata, 
											this.core.isRegistroServiziLocale(), singlePdD, pu);
							if(list!=null){	
								for (ExtendedMenuItem extendedMenuItem : list) {
									entries[index][0] = extendedMenuItem.getLabel();
									entries[index][1] = extendedMenuItem.getUrl();
									index++;
								}
							}
						}
					}

					me.setEntries(entries);
					menu.addElement(me);

				}else {

					// se non esiste la configurazione, devo cmq gestire il caso se non ho i diritti utente e se posso comunque importare servizi
					int dimensioneEntries = 0; 
					String[][] entriesUtenti = null;
					if(this.core.isShowPulsantiImportExport() && pu.isServizi()){
						dimensioneEntries++; // importa
						if(exporterUtils.existsAtLeastOneExportMpde(ArchiveType.CONFIGURAZIONE, this.session)){
							dimensioneEntries++; // esporta
							if(isModalitaAvanzata){
								dimensioneEntries++; // elimina
							}
						}
					}
					if(!pu.isUtenti()){
						//dimensioneEntries++;  // change password
					}else {
						entriesUtenti = getVoceMenuUtenti();
						dimensioneEntries += entriesUtenti.length;
					}

					// Extended Menu
					if(extendedMenu!=null){
						for (IExtendedMenu extMenu : extendedMenu) {
							List<ExtendedMenuItem> list = 
									extMenu.getExtendedItemsMenuConfigurazione(isModalitaAvanzata, 
											this.core.isRegistroServiziLocale(), singlePdD, pu);
							if(list!=null && list.size()>0){
								dimensioneEntries +=list.size();
							}
						}
					}

					if(dimensioneEntries>0){
						// Comunque devo permettere di cambiare la password ad ogni utente, se l'utente stesso non puo' gestire gli utenti
						MenuEntry me = new MenuEntry();
						me.setTitle(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE);
						String[][] entries = new String[dimensioneEntries][2];
						int index = 0;
						// link utenti sotto quello di configurazione  generale
						if (pu.isUtenti()) {
							for (int j = 0; j < entriesUtenti.length; j++) {
								entries[index][0] = entriesUtenti[j][0];
								entries[index][1] = entriesUtenti[j][1];
								index++;		
							}
						}
						if(this.core.isShowPulsantiImportExport() && pu.isServizi()){
							entries[index][0] = ArchiviCostanti.LABEL_ARCHIVI_IMPORT;
							entries[index][1] = ArchiviCostanti.SERVLET_NAME_ARCHIVI_IMPORT+"?"+
									ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORTER_MODALITA+"="+ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORTER_MODALITA_IMPORT;
							index++;
							if(exporterUtils.existsAtLeastOneExportMpde(ArchiveType.CONFIGURAZIONE, this.session)){
								entries[index][0] = ArchiviCostanti.LABEL_ARCHIVI_EXPORT;
								entries[index][1] = ArchiviCostanti.SERVLET_NAME_ARCHIVI_EXPORT+"?"+ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_TIPO+"="+ArchiveType.CONFIGURAZIONE.name();
								index++;

								if(isModalitaAvanzata){
									entries[index][0] = ArchiviCostanti.LABEL_ARCHIVI_ELIMINA;
									entries[index][1] = ArchiviCostanti.SERVLET_NAME_ARCHIVI_IMPORT+"?"+
											ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORTER_MODALITA+"="+ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORTER_MODALITA_ELIMINA;
									index++;		
								}
							}
						}
						if (!pu.isUtenti()) {
//							entries[index][0] = UtentiCostanti.LABEL_UTENTE;
//							entries[index][1] = UtentiCostanti.SERVLET_NAME_UTENTE_CHANGE;
//							index++;
						}

						// Extended Menu
						if(extendedMenu!=null){
							for (IExtendedMenu extMenu : extendedMenu) {
								List<ExtendedMenuItem> list = 
										extMenu.getExtendedItemsMenuConfigurazione(isModalitaAvanzata, 
												this.core.isRegistroServiziLocale(), singlePdD, pu);
								if(list!=null){
									for (ExtendedMenuItem extendedMenuItem : list) {
										entries[index][0] = extendedMenuItem.getLabel();
										entries[index][1] = extendedMenuItem.getUrl();
										index++;
									}
								}
							}
						}

						me.setEntries(entries);
						menu.addElement(me);
					}
				}

				if ((pu.isCodeMessaggi() || pu.isAuditing()) && !pu.isDiagnostica()) {
					// Se l'utente non ha i permessi "diagnostica", devo
					// gestire la reportistica
					MenuEntry me = new MenuEntry();
					me.setTitle(CostantiControlStation.LABEL_STRUMENTI);

					int totEntries = 0;
					if (pu.isCodeMessaggi() && pu.isAuditing())
						totEntries = 2;
					else
						totEntries = 1;

					// Extended Menu
					if(extendedMenu!=null){
						for (IExtendedMenu extMenu : extendedMenu) {
							List<ExtendedMenuItem> list = 
									extMenu.getExtendedItemsMenuStrumenti(isModalitaAvanzata, 
											this.core.isRegistroServiziLocale(), singlePdD, pu);
							if(list!=null && list.size()>0){
								totEntries +=list.size();
							}
						}
					}

					String[][] entries = null;
					if (pu.isCodeMessaggi() && pu.isAuditing())
						entries = new String[totEntries][2];
					else
						entries = new String[totEntries][2];

					int i = 0;

					if (pu.isAuditing()) {
						entries[i][0] = AuditCostanti.LABEL_AUDIT;
						entries[i][1] = AuditCostanti.SERVLET_NAME_AUDITING;
						i++;
					}
					if (pu.isCodeMessaggi()) {
						entries[i][0] = MonitorCostanti.LABEL_MONITOR;
						entries[i][1] = MonitorCostanti.SERVLET_NAME_MONITOR;
						i++;
					}

					// Extended Menu
					if(extendedMenu!=null){
						for (IExtendedMenu extMenu : extendedMenu) {
							List<ExtendedMenuItem> list = 
									extMenu.getExtendedItemsMenuStrumenti(isModalitaAvanzata, 
											this.core.isRegistroServiziLocale(), singlePdD, pu);
							if(list!=null){
								for (ExtendedMenuItem extendedMenuItem : list) {
									entries[i][0] = extendedMenuItem.getLabel();
									entries[i][1] = extendedMenuItem.getUrl();
									i++;
								}
							}
						}
					}

					me.setEntries(entries);
					menu.addElement(me);
				}
			} else {

				// SinglePdD=false
				if(pu.isSistema()){

					MenuEntry me = new MenuEntry();
					me.setTitle(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE);

					// Se l'utente ha anche i permessi "utenti", la
					// configurazione utente la gestisco dopo
					String[][] entries = null;
					String[][] entriesUtenti = null;
					int dimensioneEntries = 1; //  audit
					if(this.core.isShowPulsantiImportExport() && pu.isServizi()){
						dimensioneEntries++; // importa
						if(exporterUtils.existsAtLeastOneExportMpde(ArchiveType.CONFIGURAZIONE, this.session)){
							dimensioneEntries++; // esporta
							if(isModalitaAvanzata){
								dimensioneEntries++; // elimina
							}
						}
					}
					if (!pu.isUtenti()){
						dimensioneEntries++; // change password
					}else {
						entriesUtenti = getVoceMenuUtenti();
						dimensioneEntries += entriesUtenti.length;
					}

					// Extended Menu
					if(extendedMenu!=null){
						for (IExtendedMenu extMenu : extendedMenu) {
							List<ExtendedMenuItem> list = 
									extMenu.getExtendedItemsMenuConfigurazione(isModalitaAvanzata, 
											this.core.isRegistroServiziLocale(), singlePdD, pu);
							if(list!=null && list.size()>0){
								dimensioneEntries +=list.size();
							}
						}
					}

					entries = new String[dimensioneEntries][2];

					int index = 0;
					// link utenti sotto quello di configurazione  generale
					if (pu.isUtenti()) {
						for (int j = 0; j < entriesUtenti.length; j++) {
							entries[index][0] = entriesUtenti[j][0];
							entries[index][1] = entriesUtenti[j][1];
							index++;		
						}
					}
					if(this.core.isShowPulsantiImportExport() && pu.isServizi()){
						entries[index][0] = ArchiviCostanti.LABEL_ARCHIVI_IMPORT;
						entries[index][1] = ArchiviCostanti.SERVLET_NAME_ARCHIVI_IMPORT+"?"+
								ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORTER_MODALITA+"="+ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORTER_MODALITA_IMPORT;
						index++;
						if(exporterUtils.existsAtLeastOneExportMpde(ArchiveType.CONFIGURAZIONE, this.session)){
							entries[index][0] = ArchiviCostanti.LABEL_ARCHIVI_EXPORT;
							entries[index][1] = ArchiviCostanti.SERVLET_NAME_ARCHIVI_EXPORT+"?"+ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_TIPO+"="+ArchiveType.CONFIGURAZIONE.name();
							index++;

							if(isModalitaAvanzata){
								entries[index][0] = ArchiviCostanti.LABEL_ARCHIVI_ELIMINA;
								entries[index][1] = ArchiviCostanti.SERVLET_NAME_ARCHIVI_IMPORT+"?"+
										ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORTER_MODALITA+"="+ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORTER_MODALITA_ELIMINA;
								index++;
							}
						}
					}
					entries[index][0] = AuditCostanti.LABEL_AUDIT;
					entries[index][1] = AuditCostanti.SERVLET_NAME_AUDIT;
					index++;
					if (!pu.isUtenti()) {
						entries[index][0] = UtentiCostanti.LABEL_UTENTE;
						entries[index][1] = UtentiCostanti.SERVLET_NAME_UTENTE_CHANGE;
						index++;
					}

					// Extended Menu
					if(extendedMenu!=null){
						for (IExtendedMenu extMenu : extendedMenu) {
							List<ExtendedMenuItem> list = 
									extMenu.getExtendedItemsMenuConfigurazione(isModalitaAvanzata, 
											this.core.isRegistroServiziLocale(), singlePdD, pu);
							if(list!=null){
								for (ExtendedMenuItem extendedMenuItem : list) {
									entries[index][0] = extendedMenuItem.getLabel();
									entries[index][1] = extendedMenuItem.getUrl();
									index++;
								}
							}
						}
					}

					me.setEntries(entries);
					menu.addElement(me);

				}else {

					// se non esiste la configurazione, devo cmq gestire il caso se non ho i diritti utente e se posso comunque importare servizi
					int dimensioneEntries = 0; 
					String[][] entriesUtenti = null;
					if(this.core.isShowPulsantiImportExport() && pu.isServizi()){
						dimensioneEntries++; // importa
						if(exporterUtils.existsAtLeastOneExportMpde(ArchiveType.CONFIGURAZIONE, this.session)){
							dimensioneEntries++; // esporta
							if(isModalitaAvanzata){
								dimensioneEntries++; // elimina
							}
						}
					}
					if(!pu.isUtenti()){
						dimensioneEntries++;  // change password
					}else {
						entriesUtenti = getVoceMenuUtenti();
						dimensioneEntries += entriesUtenti.length;
					}

					// Extended Menu
					if(extendedMenu!=null){
						for (IExtendedMenu extMenu : extendedMenu) {
							List<ExtendedMenuItem> list = 
									extMenu.getExtendedItemsMenuConfigurazione(isModalitaAvanzata, 
											this.core.isRegistroServiziLocale(), singlePdD, pu);
							if(list!=null && list.size()>0){
								dimensioneEntries +=list.size();
							}
						}
					}

					if(dimensioneEntries>0){
						// Comunque devo permettere di cambiare la password ad ogni utente, se l'utente stesso non puo' gestire gli utenti
						MenuEntry me = new MenuEntry();
						me.setTitle(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE);
						String[][] entries = new String[dimensioneEntries][2];
						int index = 0;
						// link utenti sotto quello di configurazione  generale
						if (pu.isUtenti()) {
							for (int j = 0; j < entriesUtenti.length; j++) {
								entries[index][0] = entriesUtenti[j][0];
								entries[index][1] = entriesUtenti[j][1];
								index++;		
							}
						}
						if(this.core.isShowPulsantiImportExport() && pu.isServizi()){
							entries[index][0] = ArchiviCostanti.LABEL_ARCHIVI_IMPORT;
							entries[index][1] = ArchiviCostanti.SERVLET_NAME_ARCHIVI_IMPORT+"?"+
									ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORTER_MODALITA+"="+ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORTER_MODALITA_IMPORT;
							index++;
							if(exporterUtils.existsAtLeastOneExportMpde(ArchiveType.CONFIGURAZIONE, this.session)){
								entries[index][0] = ArchiviCostanti.LABEL_ARCHIVI_EXPORT;
								entries[index][1] = ArchiviCostanti.SERVLET_NAME_ARCHIVI_EXPORT+"?"+ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_TIPO+"="+ArchiveType.CONFIGURAZIONE.name();
								index++;

								if(isModalitaAvanzata){
									entries[index][0] = ArchiviCostanti.LABEL_ARCHIVI_ELIMINA;
									entries[index][1] = ArchiviCostanti.SERVLET_NAME_ARCHIVI_IMPORT+"?"+
											ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORTER_MODALITA+"="+ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORTER_MODALITA_ELIMINA;
									index++;		
								}
							}
						}
						if (!pu.isUtenti()) {
							entries[index][0] = UtentiCostanti.LABEL_UTENTE;
							entries[index][1] = UtentiCostanti.SERVLET_NAME_UTENTE_CHANGE;
							index++;
						}

						// Extended Menu
						if(extendedMenu!=null){
							for (IExtendedMenu extMenu : extendedMenu) {
								List<ExtendedMenuItem> list = 
										extMenu.getExtendedItemsMenuConfigurazione(isModalitaAvanzata, 
												this.core.isRegistroServiziLocale(), singlePdD, pu);
								if(list!=null){
									for (ExtendedMenuItem extendedMenuItem : list) {
										entries[index][0] = extendedMenuItem.getLabel();
										entries[index][1] = extendedMenuItem.getUrl();
										index++;
									}
								}
							}
						}

						me.setEntries(entries);
						menu.addElement(me);
					}

				}

				if (pu.isAuditing() || pu.isSistema() || pu.isCodeMessaggi()) {
					MenuEntry me = new MenuEntry();
					me.setTitle(CostantiControlStation.LABEL_STRUMENTI);

					String[][] entries = null;
					int size = 0;
					if (pu.isAuditing()) {
						size++;
					}
					if (pu.isSistema()) {
						size++;
					}
					if (pu.isAuditing()) {
						size++;
					}

					// Extended Menu
					if(extendedMenu!=null){
						for (IExtendedMenu extMenu : extendedMenu) {
							List<ExtendedMenuItem> list = 
									extMenu.getExtendedItemsMenuStrumenti(isModalitaAvanzata, 
											this.core.isRegistroServiziLocale(), singlePdD, pu);
							if(list!=null && list.size()>0){
								size +=list.size();
							}
						}
					}

					entries = new String[size][2];

					int i = 0;

					if (pu.isAuditing()) {
						entries[i][0] = AuditCostanti.LABEL_AUDIT;
						entries[i][1] = AuditCostanti.SERVLET_NAME_AUDITING;
						i++;
					}
					if (pu.isSistema()) {
						entries[i][0] = OperazioniCostanti.LABEL_OPERAZIONI;
						entries[i][1] = OperazioniCostanti.SERVLET_NAME_OPERAZIONI;
						i++;
					}
					if (pu.isCodeMessaggi()) {
						entries[i][0] = MonitorCostanti.LABEL_MONITOR;
						entries[i][1] = MonitorCostanti.SERVLET_NAME_MONITOR;
						i++;
					}

					// Extended Menu
					if(extendedMenu!=null){
						for (IExtendedMenu extMenu : extendedMenu) {
							List<ExtendedMenuItem> list = 
									extMenu.getExtendedItemsMenuStrumenti(isModalitaAvanzata, 
											this.core.isRegistroServiziLocale(), singlePdD, pu);
							if(list!=null){
								for (ExtendedMenuItem extendedMenuItem : list) {
									entries[i][0] = extendedMenuItem.getLabel();
									entries[i][1] = extendedMenuItem.getUrl();
									i++;
								}
							}
						}
					}

					me.setEntries(entries);
					menu.addElement(me);
				}
			}

			this.pd.setMenu(menu);
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	private String[][] getVoceMenuUtenti() {
		String[][] entries = null;
		if(this.core.isShowPulsanteAggiungiMenu()){
			entries = new String[2][2];
		}else{
			entries = new String[1][2];
		}
		entries[0][0] = UtentiCostanti.LABEL_UTENTI;
		entries[0][1] = UtentiCostanti.SERVLET_NAME_UTENTI_LIST;
		if(this.core.isShowPulsanteAggiungiMenu()){
			entries[1][0] = Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI;
			entries[1][1] = UtentiCostanti.SERVLET_NAME_UTENTI_ADD;
		}
		return entries;
	}





	// *** Utilities generiche ***

	public Search checkSearchParameters(int idLista, Search ricerca)
			throws Exception {
		try {
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);

			String search = ricerca.getSearchString(idLista);

			if (this.request.getParameter("index") != null) {
				offset = Integer.parseInt(this.request.getParameter("index"));
				ricerca.setIndexIniziale(idLista, offset);
			}
			if (this.request.getParameter("pageSize") != null) {
				limit = Integer.parseInt(this.request.getParameter("pageSize"));
				ricerca.setPageSize(idLista, limit);
			}
			if (this.request.getParameter("search") != null) {
				search = this.request.getParameter("search");
				search = search.trim();
				if (search.equals("")) {
					ricerca.setSearchString(idLista, org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED);
				} else {
					ricerca.setSearchString(idLista, search);
				}
			}
			
			ricerca.clearFilters(idLista);

			int index=0;
			String nameFilter = PageData.GET_PARAMETRO_FILTER_NAME(index);
			while (this.request.getParameter(nameFilter) != null) {
				String paramFilterName = this.request.getParameter(nameFilter);
				paramFilterName = paramFilterName.trim();
				
				String paramFilterValue = this.request.getParameter( PageData.GET_PARAMETRO_FILTER_VALUE(index));
				if(paramFilterValue==null) {
					paramFilterValue = "";
				}
				paramFilterValue = paramFilterValue.trim();
				if (paramFilterValue.equals("")) {
					paramFilterValue = org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_FILTER_UNDEFINED;
				}
				
				ricerca.addFilter(idLista, paramFilterName, paramFilterValue);
				
				index++;
				nameFilter = PageData.GET_PARAMETRO_FILTER_NAME(index);
			}

			return ricerca;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	/**
	 * Indica se si vogliono inviare le operazioni allo smistatore
	 * 
	 * @return boolean
	 */
	public boolean smista() throws Exception {
		try {
			boolean usaSmistatore = true;
			Boolean singlePdD = this.core.isSinglePdD();
			if (singlePdD)
				usaSmistatore = false;
			return usaSmistatore;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public Vector<DataElement> addNomeValoreToDati( TipoOperazione tipoOp,Vector<DataElement> dati, String nome, String valore, boolean enableUpdate) {
		DataElement de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_NOME);
		de.setValue(nome);
		if (tipoOp.equals(TipoOperazione.ADD) || enableUpdate) {
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		} else {
			de.setType(DataElementType.TEXT);
		}
		de.setName(CostantiControlStation.PARAMETRO_NOME);
		de.setSize(this.getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_VALORE);
		de.setValue(valore);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(CostantiControlStation.PARAMETRO_VALORE);
		de.setSize(this.getSize());
		de.setRequired(true);
		dati.addElement(de);

		return dati;
	}
	
	public Vector<DataElement> addHiddenFieldsToDati(TipoOperazione tipoOp, String id, String idsogg, String idPorta,
			Vector<DataElement> dati) {
		return addHiddenFieldsToDati(tipoOp, id, idsogg, idPorta, null, dati);
	}
	
	public Vector<DataElement> addHiddenFieldsToDati(TipoOperazione tipoOp, String id, String idsogg, String idPorta, String idAsps,
			Vector<DataElement> dati) {
		return addHiddenFieldsToDati(tipoOp, id, idsogg, idPorta, idAsps, null, dati);
	}

	public Vector<DataElement> addHiddenFieldsToDati(TipoOperazione tipoOp, String id, String idsogg, String idPorta, String idAsps, String idFruizione,
			Vector<DataElement> dati) {

		DataElement de = new DataElement();
		if(id!= null){
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_ID);
			de.setValue(id);
			de.setType(DataElementType.HIDDEN);
			de.setName(CostantiControlStation.PARAMETRO_ID);
			dati.addElement(de);
		}
		if(idsogg != null){
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_ID_SOGGETTO);
			de.setValue(idsogg);
			de.setType(DataElementType.HIDDEN);
			de.setName(CostantiControlStation.PARAMETRO_ID_SOGGETTO);
			dati.addElement(de);
		}
		if(idPorta != null){
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_ID_PORTA);
			de.setValue(idPorta);
			de.setType(DataElementType.HIDDEN);
			de.setName(CostantiControlStation.PARAMETRO_ID_PORTA);
			dati.addElement(de);
		}
		
		if(idAsps != null){
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_ID_ASPS);
			de.setValue(idAsps);
			de.setType(DataElementType.HIDDEN);
			de.setName(CostantiControlStation.PARAMETRO_ID_ASPS);
			dati.addElement(de);
		}
		
		if(idFruizione != null){
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_ID_FRUIZIONE);
			de.setValue(idFruizione);
			de.setType(DataElementType.HIDDEN);
			de.setName(CostantiControlStation.PARAMETRO_ID_FRUIZIONE);
			dati.addElement(de);
		}

		return dati;
	}

	// *** Utilities per i nomi ***
	
	// In effetti iniziare con un '.' o un '-' e' brutto, per adesso si elimina questa possibilita
//	public boolean checkName(String name, String object) throws Exception{
//		// Il nome deve contenere solo lettere e numeri e '_' '-' '.'
//		if (!RegularExpressionEngine.isMatch(name,"^[0-9A-Za-z_\\-\\.]+$")) {
//			this.pd.setMessage("Il campo '"+object+"' deve essere formato solo da caratteri, cifre, '_' , '-' e '.'");
//			return false;
//		}
//		return true;
//	}	

	public boolean checkNCName(String name, String object) throws Exception{
		// Il nome deve contenere solo lettere e numeri e '_' '-' '.'
		if (!RegularExpressionEngine.isMatch(name,"^[_A-Za-z][\\-\\._A-Za-z0-9]*$")) {
			this.pd.setMessage("Il campo '"+object+"' può iniziare solo con un carattere [A-Za-z] o il simbolo '_' e dev'essere formato solo da caratteri, cifre, '_' , '-' e '.'");
			return false;
		}
		return true;
	}
	
	public boolean checkSimpleName(String name, String object) throws Exception{
		if (!RegularExpressionEngine.isMatch(name,"^[0-9A-Za-z]+$")) {
			this.pd.setMessage("Il campo '"+object+"' deve essere formato solo da caratteri e cifre");
			return false;
		}		
		return true;
	}
	
	public boolean checkIntegrationEntityName(String name, String object) throws Exception{
		// Il nome deve contenere solo lettere e numeri e '_' '-' '.' '/'
		if (!RegularExpressionEngine.isMatch(name,"^[_A-Za-z][\\-\\._/A-Za-z0-9]*$")) {
			this.pd.setMessage("Il campo '"+object+"' può iniziare solo con un carattere [A-Za-z] o il simbolo '_' e dev'essere formato solo da caratteri, cifre, '_' , '-', '.' e '/'");
			return false;
		}
		return true;
	}
	
	
	public boolean checkNumber(String value, String object, boolean permitZeroAsValue) throws Exception{
		if(permitZeroAsValue){
			if (!RegularExpressionEngine.isMatch(value,"^[0-9]+$")) {
				this.pd.setMessage("Il campo '"+object+"' deve essere formato solo da cifre");
				return false;
			}	
		}
		else{
			if (!RegularExpressionEngine.isMatch(value,"^[1-9]+[0-9]*$")) {
				if(value.charAt(0) == '0' && value.length()>1){
					this.pd.setMessage("Il campo '"+object+"' deve contenere un numero intero maggiore di zero e non deve iniziare con la cifra '0'");
				}else{
					this.pd.setMessage("Il campo '"+object+"' deve contenere un numero intero maggiore di zero");
				}
				return false;
			}
		}
		return true;
	}
	
	// *** Utilities condivise tra Porte Delegate e Porte Applicative ***
	
	public Vector<DataElement> addPorteServizioApplicativoToDati(TipoOperazione tipoOp, Vector<DataElement> dati, 
			String servizioApplicativo, String[] servizioApplicativoList, int sizeAttuale, 
			boolean addMsgServiziApplicativoNonDisponibili) {
		
		if(servizioApplicativoList!=null && servizioApplicativoList.length>0){
		
			DataElement de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_SERVIZIO_APPLICATIVO );
			de.setType(DataElementType.SELECT);
			de.setName(CostantiControlStation.PARAMETRO_SERVIZIO_APPLICATIVO);
			de.setValues(servizioApplicativoList);
			de.setSelected(servizioApplicativo);
			dati.addElement(de);
			
		}else{
			if(addMsgServiziApplicativoNonDisponibili){
				if(sizeAttuale>0){
					this.pd.setMessage("Non ulteriori esistono servizi applicativi associabili alla porta",org.openspcoop2.web.lib.mvc.MessageType.INFO);
				}
				else{
					this.pd.setMessage("Non esistono servizi applicativi associabili alla porta",org.openspcoop2.web.lib.mvc.MessageType.INFO);
				}
				this.pd.disableEditMode();
			}
		}

		return dati;
	}

	// Controlla i dati del Message-Security
	public boolean WSCheckData(TipoOperazione tipoOp) throws Exception {
		try{
			String messageSecurity = this.request.getParameter(CostantiControlStation.PARAMETRO_MESSAGE_SECURITY);

			// Controllo che i campi "select" abbiano uno dei valori ammessi
			if (!messageSecurity.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MESSAGE_SECURITY_ABILITATO) && 
					!messageSecurity.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MESSAGE_SECURITY_DISABILITATO)) {
				this.pd.setMessage("Stato dev'essere abilitato o disabilitato");
				return false;
			}
			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public Vector<DataElement> addMessageSecurityToDati(Vector<DataElement> dati,
			String messageSecurity, String url1, String url2, Boolean contaListe, int numWSreq, int numWSres, boolean showApplicaMTOMReq, String applicaMTOMReq,
			boolean showApplicaMTOMRes, String applicaMTOMRes) {

		DataElement de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_MESSAGE_SECURITY);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		
		de = new DataElement();
		String[] tipoWS = {
				CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MESSAGE_SECURITY_ABILITATO, 
				CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MESSAGE_SECURITY_DISABILITATO
		};

		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_STATO);
		de.setType(DataElementType.SELECT);
		de.setName(CostantiControlStation.PARAMETRO_MESSAGE_SECURITY);
		de.setValues(tipoWS);
		de.setSelected(messageSecurity);
		dati.addElement(de);

		if(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MESSAGE_SECURITY_ABILITATO.equals(messageSecurity)){
			//			de = new DataElement();
			//			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_MESSAGE_SECURITY);
			//			de.setType(DataElementType.TITLE);
			//			dati.addElement(de);

			// Sezione Richiesta
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_RICHIESTA);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);

			// Applica MTOM Richiesta
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_APPLICA_MTOM);
			if(showApplicaMTOMReq){
				de.setType(DataElementType.CHECKBOX);
				if( ServletUtils.isCheckBoxEnabled(applicaMTOMReq) || CostantiRegistroServizi.ABILITATO.equals(applicaMTOMReq) ){
					de.setSelected(true);
				}
			} else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(applicaMTOMReq); 
			}
			de.setName(CostantiControlStation.PARAMETRO_APPLICA_MTOM_RICHIESTA);
			dati.addElement(de);

			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(url1);
			if (contaListe)
				de.setValue(CostantiControlStation.LABEL_PARAMETRO_PARAMETRI+"(" + numWSreq + ")");
			else
				de.setValue(CostantiControlStation.LABEL_PARAMETRO_PARAMETRI);
			dati.addElement(de);

			// Sezione Risposta
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_RISPOSTA);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);

			// Applica MTOM Risposta
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_APPLICA_MTOM);
			if(showApplicaMTOMRes){
				de.setType(DataElementType.CHECKBOX);
				if( ServletUtils.isCheckBoxEnabled(applicaMTOMRes) || CostantiRegistroServizi.ABILITATO.equals(applicaMTOMRes) ){
					de.setSelected(true);
				}
			} else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(applicaMTOMRes); 
			}
			de.setName(CostantiControlStation.PARAMETRO_APPLICA_MTOM_RISPOSTA);
			dati.addElement(de);

			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(url2);
			if (contaListe)
				de.setValue(CostantiControlStation.LABEL_PARAMETRO_PARAMETRI+"(" + numWSres + ")");
			else
				de.setValue(CostantiControlStation.LABEL_PARAMETRO_PARAMETRI);
			dati.addElement(de);
		}

		dati = addParameterApplicaModifica(dati);
		
		return dati;
	}

	public Vector<DataElement> addMTOMToDati(Vector<DataElement> dati, String[] modeMtomListReq,String[] modeMtomListRes,
			String mtomRichiesta,String mtomRisposta, String url1, String url2, Boolean contaListe, int numMTOMreq, int numMTOMres) {

		DataElement de = new DataElement();

		// Sezione Richiesta
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_RICHIESTA);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		// Stato
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_STATO);
		de.setType(DataElementType.SELECT);
		de.setName(CostantiControlStation.PARAMETRO_MTOM_RICHIESTA);
		de.setValues(modeMtomListReq);
		de.setSelected(mtomRichiesta);
		de.setSize(this.getSize());
		dati.addElement(de);

		// Link
		if(url1 != null){
			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(url1);
			if (contaListe)
				de.setValue(CostantiControlStation.LABEL_PARAMETRO_PARAMETRI +"(" + numMTOMreq + ")");
			else
				de.setValue(CostantiControlStation.LABEL_PARAMETRO_PARAMETRI);
			dati.addElement(de);
		}


		// Sezione Richiesta
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_RISPOSTA);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		// Stato
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_STATO);
		de.setType(DataElementType.SELECT);
		de.setName(CostantiControlStation.PARAMETRO_MTOM_RISPOSTA);
		de.setValues(modeMtomListRes);
		de.setSelected(mtomRisposta);
		de.setSize(this.getSize());
		dati.addElement(de);

		// Link
		if(url2 != null){
			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(url2);
			if (contaListe)
				de.setValue(CostantiControlStation.LABEL_PARAMETRO_PARAMETRI +"(" + numMTOMres + ")");
			else
				de.setValue(CostantiControlStation.LABEL_PARAMETRO_PARAMETRI);
			dati.addElement(de);
		}
		
		dati = addParameterApplicaModifica(dati);

		return dati;
	}
	
	// Dati schermata correlazione applicativa
	public Vector<DataElement> addCorrelazioneApplicativaToDati(Vector<DataElement> dati,boolean portaDelegata,
			boolean riusoID,String scadcorr, String urlRichiesta, String urlRisposta, Boolean contaListe, int numCorrelazioneReq, int numCorrelazioneRes) {

		DataElement de = new DataElement();
		
		de = new DataElement();
		de.setType(DataElementType.TITLE);
		//de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CORRELAZIONE_APPLICATIVA);
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_RICHIESTA);
		dati.addElement(de);
		
		if(portaDelegata){		
			if (riusoID && numCorrelazioneReq > 0 && this.isModalitaAvanzata()) {
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_SCADENZA_CORRELAZIONE_APPLICATIVA_LABEL);
				de.setNote(CostantiControlStation.LABEL_PARAMETRO_SCADENZA_CORRELAZIONE_APPLICATIVA_NOTE);
				de.setValue(scadcorr);
				de.setType(DataElementType.TEXT_EDIT);
				de.setName(CostantiControlStation.PARAMETRO_SCADENZA_CORRELAZIONE_APPLICATIVA);
				dati.addElement(de);
			} else {
				this.pd.disableOnlyButton();
			}
		} else {
			boolean riuso = false; // riuso non abilitato nella porta applicativa
			if (riuso && numCorrelazioneReq > 0 && this.isModalitaAvanzata()) {
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_SCADENZA_CORRELAZIONE_APPLICATIVA_LABEL);
				de.setNote(CostantiControlStation.LABEL_PARAMETRO_SCADENZA_CORRELAZIONE_APPLICATIVA_NOTE);
				de.setValue(scadcorr);
				de.setType(DataElementType.TEXT_EDIT);
				de.setName(CostantiControlStation.PARAMETRO_SCADENZA_CORRELAZIONE_APPLICATIVA);
				dati.addElement(de);
			} else {
				this.pd.disableOnlyButton();
			}
		}
		
		de = new DataElement();
		de.setType(DataElementType.LINK);
		de.setUrl(urlRichiesta);

		if (contaListe) {
			ServletUtils.setDataElementCustomLabel(de,CostantiControlStation.LABEL_PARAMETRO_CORRELAZIONE_APPLICATIVA_RICHIESTA,new Long(numCorrelazioneReq));
		} else
			ServletUtils.setDataElementCustomLabel(de,CostantiControlStation.LABEL_PARAMETRO_CORRELAZIONE_APPLICATIVA_RICHIESTA);

		dati.addElement(de);

		
		de = new DataElement();
		de.setType(DataElementType.TITLE);
		//de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CORRELAZIONE_APPLICATIVA);
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_RISPOSTA);
		dati.addElement(de);
		
		de = new DataElement();
		de.setType(DataElementType.LINK);
		de.setUrl(urlRisposta);

		if (contaListe) {
			ServletUtils.setDataElementCustomLabel(de,CostantiControlStation.LABEL_PARAMETRO_CORRELAZIONE_APPLICATIVA_RISPOSTA,new Long(numCorrelazioneRes));
		} else
			ServletUtils.setDataElementCustomLabel(de,CostantiControlStation.LABEL_PARAMETRO_CORRELAZIONE_APPLICATIVA_RISPOSTA);

		dati.addElement(de);
		
		dati = addParameterApplicaModifica(dati);

		return dati;
	}

	public Vector<DataElement> addParameterApplicaModifica(Vector<DataElement> dati) {
		DataElement de;
		de = new DataElement();
		de.setType(DataElementType.HIDDEN);
		de.setName(CostantiControlStation.PARAMETRO_APPLICA_MODIFICA); 
		de.setValue(Costanti.CHECK_BOX_ENABLED);
		dati.addElement(de);
		return dati;
	}
	
	// Controlla i dati della correlazione applicativa
	public boolean correlazioneApplicativaCheckData(TipoOperazione tipoOp,boolean portaDelegata,String scadcorr) throws Exception {
		try{
			// scadenza correlazione intero > 0 opzionale
			if(scadcorr != null && !scadcorr.equals("")){
				int scadCorrInt = -1;
				try{
					scadCorrInt = Integer.parseInt(scadcorr);
				}catch(Exception e){
					this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_SCADENZA_CORRELAZIONE_APPLICATIVA_NON_VALIDA_INSERIRE_UN_NUMERO_INTERO_MAGGIORE_DI_ZERO); 
					return false;
				}
				
				if(scadCorrInt <= 0){
					this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_SCADENZA_CORRELAZIONE_APPLICATIVA_NON_VALIDA_INSERIRE_UN_NUMERO_INTERO_MAGGIORE_DI_ZERO);
					return false;
				}
			}
			
			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Controlla i dati della correlazione applicativa richiesta della porta delegata
	public boolean correlazioneApplicativaRichiestaCheckData(TipoOperazione tipoOp,boolean portaDelegata) throws Exception {
		try {
			String id = this.request.getParameter(CostantiControlStation.PARAMETRO_ID);
			int idInt = Integer.parseInt(id);
			// String idsogg = this.request.getParameter("idsogg");
			String elemxml = this.request.getParameter(CostantiControlStation.PARAMETRO_ELEMENTO_XML);
			String mode = this.request.getParameter(CostantiControlStation.PARAMETRO_MODE_CORRELAZIONE_APPLICATIVA);
			String pattern = this.request.getParameter(CostantiControlStation.PARAMETRO_PATTERN);
			String idcorr = this.request.getParameter(CostantiControlStation.PARAMETRO_ID_CORRELAZIONE);
			int idcorrInt = 0;
			if (idcorr != null) {
				idcorrInt = Integer.parseInt(idcorr);
			}

			// Campi obbligatori
			// if ( elemxml.equals("")||
			if (((mode.equals(CostantiControlStation.VALUE_PARAMETRO_MODE_CORRELAZIONE_URL_BASED) || mode.equals(CostantiControlStation.VALUE_PARAMETRO_MODE_CORRELAZIONE_CONTENT_BASED)) 
					&& pattern.equals(""))) {
				String tmpElenco = "";
				if ((mode.equals(CostantiControlStation.VALUE_PARAMETRO_MODE_CORRELAZIONE_URL_BASED) || mode.equals(CostantiControlStation.VALUE_PARAMETRO_MODE_CORRELAZIONE_CONTENT_BASED)) && pattern.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = CostantiControlStation.LABEL_PATTERN;
					} else {
						tmpElenco = tmpElenco + ", " + CostantiControlStation.LABEL_PATTERN;
					}
				}
				this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, tmpElenco));
				return false;
			}

			// Controllo che i campi "select" abbiano uno dei valori ammessi
			if (!mode.equals(CostantiControlStation.VALUE_PARAMETRO_MODE_CORRELAZIONE_URL_BASED) 
					&& !mode.equals(CostantiControlStation.VALUE_PARAMETRO_MODE_CORRELAZIONE_CONTENT_BASED) 
					&& !mode.equals(CostantiControlStation.VALUE_PARAMETRO_MODE_CORRELAZIONE_INPUT_BASED) 
					&& !mode.equals(CostantiControlStation.VALUE_PARAMETRO_MODE_CORRELAZIONE_DISABILITATO)) {
				this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_MODALITA_IDENTIFICAZIONE_CON_TIPI_POSSIBILI);
				return false;
			}

			// Controllo che non esistano altre correlazioni applicative con gli
			// stessi dati
			boolean giaRegistrato = false;

			if (pattern == null) {
				pattern = "";
			}

			int idCorrApp = 0;
			CorrelazioneApplicativa ca = null;
			String nomePorta = null;
			if(portaDelegata){
				PortaDelegata pde = null;
				pde = this.porteDelegateCore.getPortaDelegata(idInt);
				ca = pde.getCorrelazioneApplicativa();
				nomePorta = pde.getNome();
			}else{
				PortaApplicativa pda = null;
				pda = this.porteApplicativeCore.getPortaApplicativa(idInt);
				ca = pda.getCorrelazioneApplicativa();
				nomePorta = pda.getNome();
			}
			if (ca != null) {
				for (int i = 0; i < ca.sizeElementoList(); i++) {
					CorrelazioneApplicativaElemento cae = ca.getElemento(i);
					String caeNome = cae.getNome();
					if (caeNome == null)
						caeNome = "";
					if (elemxml.equals(caeNome) || ("*".equals(caeNome) && "".equals(elemxml))) {
						idCorrApp = cae.getId().intValue();
						break;
					}
				}
			}

			if ((idCorrApp != 0) && (idCorrApp != idcorrInt)) {
				giaRegistrato = true;
			}

			if (giaRegistrato) {
				String nomeElemento = CostantiControlStation.LABEL_NON_DEFINITO;
				if(elemxml!=null && ("".equals(elemxml)==false))
					nomeElemento = elemxml;
				String idPorta = null;
				if(portaDelegata)
					idPorta = MessageFormat.format(CostantiControlStation.LABEL_PORTA_DELEGATA_CON_PARAMETRI, nomePorta);
				else
					idPorta = MessageFormat.format(CostantiControlStation.LABEL_PORTA_APPLICATIVA_CON_PARAMETRI, nomePorta);
				this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CORRELAZIONE_APPLICATIVA_CON_ELEMENTO_XML_DEFINITA_GIA_ESISTENTE,	nomeElemento, idPorta));
				return false;
			}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}


	// Controlla i dati della correlazione applicativa della porta delegata
	public boolean correlazioneApplicativaRispostaCheckData(TipoOperazione tipoOp,boolean portaDelegata) throws Exception {
		try {
			String id = this.request.getParameter(CostantiControlStation.PARAMETRO_ID);
			int idInt = Integer.parseInt(id);
			// String idsogg = this.request.getParameter("idsogg");
			String elemxml = this.request.getParameter(CostantiControlStation.PARAMETRO_ELEMENTO_XML);
			String mode = this.request.getParameter(CostantiControlStation.PARAMETRO_MODE_CORRELAZIONE_APPLICATIVA);
			String pattern = this.request.getParameter(CostantiControlStation.PARAMETRO_PATTERN);
			String idcorr = this.request.getParameter(CostantiControlStation.PARAMETRO_ID_CORRELAZIONE);
			int idcorrInt = 0;
			if (idcorr != null) {
				idcorrInt = Integer.parseInt(idcorr);
			}

			// Campi obbligatori
			// if ( elemxml.equals("")||
			if (((mode.equals(CostantiControlStation.VALUE_PARAMETRO_MODE_CORRELAZIONE_URL_BASED) || mode.equals(CostantiControlStation.VALUE_PARAMETRO_MODE_CORRELAZIONE_CONTENT_BASED)) && pattern.equals(""))) {
				String tmpElenco = "";
				if ((mode.equals(CostantiControlStation.VALUE_PARAMETRO_MODE_CORRELAZIONE_URL_BASED) || mode.equals(CostantiControlStation.VALUE_PARAMETRO_MODE_CORRELAZIONE_CONTENT_BASED)) && pattern.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = CostantiControlStation.LABEL_PATTERN;
					} else {
						tmpElenco = tmpElenco + ", " + CostantiControlStation.LABEL_PATTERN;
					}
				}
				this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, tmpElenco));
				return false;
			}

			// Controllo che i campi "select" abbiano uno dei valori ammessi
			if (!mode.equals(CostantiControlStation.VALUE_PARAMETRO_MODE_CORRELAZIONE_URL_BASED) && !mode.equals(CostantiControlStation.VALUE_PARAMETRO_MODE_CORRELAZIONE_CONTENT_BASED) && !mode.equals(CostantiControlStation.VALUE_PARAMETRO_MODE_CORRELAZIONE_INPUT_BASED) && !mode.equals(CostantiControlStation.VALUE_PARAMETRO_MODE_CORRELAZIONE_DISABILITATO)) {
				this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_MODALITA_IDENTIFICAZIONE_CON_TIPI_POSSIBILI);
				return false;
			}

			// Controllo che non esistano altre correlazioni applicative con gli
			// stessi dati
			boolean giaRegistrato = false;

			if (pattern == null) {
				pattern = "";
			}

			int idCorrApp = 0;
			CorrelazioneApplicativaRisposta ca = null;
			String nomePorta = null;
			if(portaDelegata){
				PortaDelegata pde = null;
				pde = this.porteDelegateCore.getPortaDelegata(idInt);
				ca = pde.getCorrelazioneApplicativaRisposta();
				nomePorta = pde.getNome();
			}else{
				PortaApplicativa pda = null;
				pda = this.porteApplicativeCore.getPortaApplicativa(idInt);
				ca = pda.getCorrelazioneApplicativaRisposta();
				nomePorta = pda.getNome();
			}
			if (ca != null) {
				for (int i = 0; i < ca.sizeElementoList(); i++) {
					CorrelazioneApplicativaRispostaElemento cae = ca.getElemento(i);
					String caeNome = cae.getNome();
					if (caeNome == null)
						caeNome = "";
					if (elemxml.equals(caeNome) || ("*".equals(caeNome) && "".equals(elemxml))) {
						idCorrApp = cae.getId().intValue();
						break;
					}
				}
			}

			if ((idCorrApp != 0) && (idCorrApp != idcorrInt)) {
				giaRegistrato = true;
			}

			if (giaRegistrato) {
				String nomeElemento = CostantiControlStation.LABEL_NON_DEFINITO;
				if(elemxml!=null && ("".equals(elemxml)==false))
					nomeElemento = elemxml;
				String idPorta = null;
				if(portaDelegata)
					idPorta = MessageFormat.format(CostantiControlStation.LABEL_PORTA_DELEGATA_CON_PARAMETRI, nomePorta);
				else
					idPorta = MessageFormat.format(CostantiControlStation.LABEL_PORTA_APPLICATIVA_CON_PARAMETRI, nomePorta);
				this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CORRELAZIONE_APPLICATIVA_PER_LA_RISPOSTA_CON_ELEMENTO_DEFINITA_GIA_ESISTENTE,	nomeElemento, idPorta));
				return false;
			}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}





	// Controlla i dati del Message-Security
	public boolean MTOMCheckData(TipoOperazione tipoOp) throws Exception {
		try{
			String mtomRichiesta = this.request.getParameter(CostantiControlStation.PARAMETRO_MTOM_RICHIESTA);
			String mtomRisposta = this.request.getParameter(CostantiControlStation.PARAMETRO_MTOM_RISPOSTA);

			// Controllo che i campi "select" abbiano uno dei valori ammessi
			if (!mtomRichiesta.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MTOM_DISABLE) && 
					!mtomRichiesta.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MTOM_PACKAGING) && 
					!mtomRichiesta.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MTOM_VERIFY) && 
					!mtomRichiesta.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MTOM_UNPACKAGING)) {
				this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_STATO_DELLA_RICHIESTA_DEVE_ESSERE_DISABLED_PACKAGING_UNPACKAGING_O_VERIFY);
				return false;
			}

			if (!mtomRisposta.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MTOM_DISABLE) && 
					!mtomRisposta.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MTOM_PACKAGING) && 
					!mtomRisposta.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MTOM_VERIFY) && 
					!mtomRisposta.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MTOM_UNPACKAGING)) {
				this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_STATO_DELLA_RISPOSTA_DEVE_ESSERE_DISABLED_PACKAGING_UNPACKAGING_O_VERIFY);
				return false;
			}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Controlla i dati dei parametri MTOM 
	public boolean MTOMParameterCheckData(TipoOperazione tipoOp, boolean isRisposta, boolean isPortaDelegata) throws Exception {
		try {
			String id = this.request.getParameter(CostantiControlStation.PARAMETRO_ID);
			int idInt = Integer.parseInt(id);
			String nome = this.request.getParameter(CostantiControlStation.PARAMETRO_NOME);
			String contentType =this.request.getParameter(CostantiControlStation.PARAMETRO_CONTENT_TYPE);
			//	String obbligatorio = this.request.getParameter(CostantiControlStation.PARAMETRO_OBBLIGATORIO);
			String pattern = this.request.getParameter(CostantiControlStation.PARAMETRO_PATTERN);


			// Campi obbligatori
			if (nome.equals("") || pattern.equals("")) {
				String tmpElenco = "";
				if (nome.equals("")) {
					tmpElenco = CostantiControlStation.LABEL_PARAMETRO_NOME;
				}
				if (pattern.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = CostantiControlStation.LABEL_PATTERN;
					} else {
						tmpElenco = tmpElenco + ", " + CostantiControlStation.LABEL_PATTERN;
					}
				}
				this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, tmpElenco));
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			//if ((nome.indexOf(" ") != -1) || (valore.indexOf(" ") != -1)) {
			if ((nome.indexOf(" ") != -1) ) {
				this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_NEL_CAMPO_NOME);
				return false;
			}
			if(pattern.indexOf(" ") != -1){
				this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRROE_NON_INSERIRE_SPAZI_NEL_CAMPO_PATTERN);
				return false;
			}



			if(contentType.indexOf(" ") != -1){
				this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_NEL_CAMPO_CONTENT_TYPE);
				return false;
			}

			// Se tipoOp = add, controllo che il message-security non sia gia' stato
			// registrato per la porta delegata
			if (tipoOp.equals(TipoOperazione.ADD)) {
				MtomProcessor mtomProcessor = null;
				boolean giaRegistrato = false;
				String nomeporta =  null;

				if(isPortaDelegata){
					PortaDelegata pde = this.porteDelegateCore.getPortaDelegata(idInt);
					nomeporta = pde.getNome();
					mtomProcessor = pde.getMtomProcessor();
				} else {
					PortaApplicativa pa = this.porteApplicativeCore.getPortaApplicativa(idInt);
					nomeporta = pa.getNome();
					mtomProcessor = pa.getMtomProcessor();
				}

				if(mtomProcessor!=null){
					if(!isRisposta){
						if(mtomProcessor.getRequestFlow()!=null){
							for (int i = 0; i < mtomProcessor.getRequestFlow().sizeParameterList(); i++) {
								MtomProcessorFlowParameter tmpMTOM =mtomProcessor.getRequestFlow().getParameter(i);
								if (nome.equals(tmpMTOM.getNome())) {
									giaRegistrato = true;
									break;
								}
							}
						}
					} else {
						if(mtomProcessor.getResponseFlow()!=null){
							for (int i = 0; i < mtomProcessor.getResponseFlow().sizeParameterList(); i++) {
								MtomProcessorFlowParameter tmpMTOM =mtomProcessor.getResponseFlow().getParameter(i);
								if (nome.equals(tmpMTOM.getNome())) {
									giaRegistrato = true;
									break;
								}
							}
						}
					}
				}

				if (giaRegistrato) {
					if(isPortaDelegata)
						this.pd.setMessage(MessageFormat.format(
								CostantiControlStation.MESSAGGIO_ERRORE_PROPRIETA_DI_MTOM_GIA_ASSOCIATA_ALLA_PORTA_DELEGATA_XX, nome, nomeporta));
					else 
						this.pd.setMessage(MessageFormat.format(
								CostantiControlStation.MESSAGGIO_ERRORE_PROPRIETA_DI_MTOM_GIA_ASSOCIATA_ALLA_PORTA_APPLICATIVA_XX, nome, nomeporta));
					return false;
				}
			}

			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public Vector<DataElement> addMTOMParameterToDati(TipoOperazione tipoOp,Vector<DataElement> dati,boolean enableUpdate, String nome, String pattern,
			String contentType, String obbligatorio) {

		// Nome
		DataElement de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_NOME);
		de.setValue(nome);
		if (tipoOp.equals(TipoOperazione.ADD) || enableUpdate) {
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		} else {
			de.setType(DataElementType.TEXT);
		}
		de.setName(CostantiControlStation.PARAMETRO_NOME);
		de.setSize(this.getSize());
		dati.addElement(de);

		// Pattern
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PATTERN);
		de.setValue(pattern);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(CostantiControlStation.PARAMETRO_PATTERN);
		de.setSize(this.getSize());
		de.setRequired(true);
		dati.addElement(de);

		// COntent-type
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONTENT_TYPE); 
		de.setValue(contentType);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(CostantiControlStation.PARAMETRO_CONTENT_TYPE);
		de.setSize(this.getSize());
		dati.addElement(de);

		// Obbligatorio
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_OBBLIGATORIO);
		de.setType(DataElementType.CHECKBOX);
		if( ServletUtils.isCheckBoxEnabled(obbligatorio) || CostantiRegistroServizi.ABILITATO.equals(obbligatorio) ){
			de.setSelected(true);
		}
		de.setName(CostantiControlStation.PARAMETRO_OBBLIGATORIO);
		dati.addElement(de);

		return dati;
	}

	public Vector<DataElement> addProtocolPropertiesToDati(Vector<DataElement> dati, ConsoleConfiguration consoleConfiguration,ConsoleOperationType consoleOperationType,
			ConsoleInterfaceType consoleInterfaceType, ProtocolProperties protocolProperties) throws Exception{
		return addProtocolPropertiesToDati(dati, consoleConfiguration, consoleOperationType, consoleInterfaceType, protocolProperties, null, null);
	}

	public Vector<DataElement> addProtocolPropertiesToDati(Vector<DataElement> dati, ConsoleConfiguration consoleConfiguration,ConsoleOperationType consoleOperationType,
			ConsoleInterfaceType consoleInterfaceType, ProtocolProperties protocolProperties, List<ProtocolProperty> listaProtocolPropertiesDaDB ,Properties binaryPropertyChangeInfoProprietario) throws Exception{
		for (BaseConsoleItem item : consoleConfiguration.getConsoleItem()) {
			AbstractProperty<?> property = ProtocolPropertiesUtils.getAbstractPropertyById(protocolProperties, item.getId());
			// imposto il default value
			ProtocolPropertiesUtils.setDefaultValue(item, property); 

			ProtocolProperty protocolProperty = ProtocolPropertiesUtils.getProtocolProperty(item.getId(), listaProtocolPropertiesDaDB); 
			dati = ProtocolPropertiesUtilities.itemToDataElement(dati,item,  consoleOperationType, consoleInterfaceType, binaryPropertyChangeInfoProprietario, protocolProperty, this.getSize());
		}

		// Imposto il flag per indicare che ho caricato la configurazione
		DataElement de = new DataElement();
		de.setName(ProtocolPropertiesCostanti.PARAMETRO_PP_SET);
		de.setType(DataElementType.HIDDEN);
		de.setValue("ok");
		dati.add(de);

		return dati;
	}

	//	public void impostaDefaultValuesConsoleItems(ConsoleConfiguration consoleConfiguration,
	//			ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType,
	//			ProtocolProperties properties) throws ProtocolException {
	//
	//		for (int i = 0; i < properties.sizeProperties(); i++) {
	//			AbstractProperty<?> property = properties.getProperty(i);
	//			ProtocolPropertiesUtils.setDefaultValue(consoleConfiguration.getConsoleItem(), property);
	//		}
	//	}

	public void validaProtocolProperties(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, ProtocolProperties properties) throws ProtocolException{
		try {
			List<BaseConsoleItem> consoleItems = consoleConfiguration.getConsoleItem();
			for (int i = 0; i < properties.sizeProperties(); i++) {
				AbstractProperty<?> property = properties.getProperty(i);
				AbstractConsoleItem<?> consoleItem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleItems, property);

				if(consoleItem != null) {
					if(consoleItem instanceof StringConsoleItem){
						StringProperty sp = (StringProperty) property;
						if (consoleItem.isRequired() && StringUtils.isEmpty(sp.getValue())) {
							throw new ProtocolException(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, consoleItem.getLabel()));
						}

						if(StringUtils.isNotEmpty(consoleItem.getRegexpr())){
							if(!RegularExpressionEngine.isMatch(sp.getValue(),consoleItem.getRegexpr())){
								throw new ProtocolException(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_IL_CAMPO_XX_DEVE_RISPETTARE_IL_PATTERN_YY, consoleItem.getLabel(), consoleItem.getRegexpr()));
							}
						}
					}
					else if(consoleItem instanceof NumberConsoleItem){
						NumberProperty np = (NumberProperty) property;
						if (consoleItem.isRequired() && np.getValue() == null) {
							throw new ProtocolException(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, consoleItem.getLabel()));
						}
					}
					else if(consoleItem instanceof BinaryConsoleItem){
						BinaryProperty bp = (BinaryProperty) property;
						if (consoleOperationType.equals(ConsoleOperationType.ADD) && consoleItem.isRequired() && (bp.getValue() == null || bp.getValue().length == 0)) {
							throw new ProtocolException(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, consoleItem.getLabel()));
						}
					}
					else if(consoleItem instanceof BooleanConsoleItem){
						BooleanProperty bp = (BooleanProperty) property;
						// le checkbox obbligatorie non dovrebbero esserci...
						if (consoleItem.isRequired() && bp.getValue() == null) {
							throw new ProtocolException(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, consoleItem.getLabel()));
						}
					}
				}
			}

		} catch (RegExpException e) {
			throw new ProtocolException(e);
		} catch (RegExpNotFoundException e) {
			throw new ProtocolException(e);
		} catch (ProtocolException e) {
			throw e;
		}

	}

	public void validaProtocolPropertyBinaria(String nome, ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, ProtocolProperties properties) throws ProtocolException{
		try {
			List<BaseConsoleItem> consoleItems = consoleConfiguration.getConsoleItem();
			for (int i = 0; i < properties.sizeProperties(); i++) {
				AbstractProperty<?> property = properties.getProperty(i);
				AbstractConsoleItem<?> consoleItem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleItems, property);

				if(consoleItem != null) {
					if(consoleItem instanceof BinaryConsoleItem && consoleItem.getId().equals(nome)){ 
						BinaryProperty bp = (BinaryProperty) property;
						if (consoleItem.isRequired() && (bp.getValue() == null || bp.getValue().length == 0)) {
							throw new ProtocolException(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, consoleItem.getLabel()));
						}

					}
				}
			}

		} catch (ProtocolException e) {
			throw e;
		}

	}

	
	public Vector<DataElement> addRuoliToDati(TipoOperazione tipoOp,Vector<DataElement> dati,boolean enableUpdate, FiltroRicercaRuoli filtroRuoli, String nome, 
			List<String> ruoliGiaConfigurati, boolean addSelezioneVuota, boolean addMsgServiziApplicativoNonDisponibili, 
			boolean addTitoloSezione) throws DriverRegistroServiziException {
		return this.addRuoliToDati(tipoOp, dati, enableUpdate, filtroRuoli, nome, ruoliGiaConfigurati, 
				addSelezioneVuota, addMsgServiziApplicativoNonDisponibili, CostantiControlStation.LABEL_PARAMETRO_RUOLO, 
				addTitoloSezione);
	}
	public Vector<DataElement> addRuoliToDati(TipoOperazione tipoOp,Vector<DataElement> dati,boolean enableUpdate, FiltroRicercaRuoli filtroRuoli, String nome, 
			List<String> ruoliGiaConfigurati, boolean addSelezioneVuota, boolean addMsgServiziApplicativoNonDisponibili, String labelParametro,
			boolean addTitoloSezione) throws DriverRegistroServiziException {

		List<String> allRuoli = this.confCore.getAllRuoli(filtroRuoli);
		List<String> ruoliDaFarScegliere = new ArrayList<>();
		if(ruoliGiaConfigurati!=null && ruoliGiaConfigurati.size()>0){
			for (String ruolo : allRuoli) {
				if(ruoliGiaConfigurati.contains(ruolo)==false){
					ruoliDaFarScegliere.add(ruolo);
				}
			}
		}
		else{
			ruoliDaFarScegliere.addAll(allRuoli);
		}
		
		// Nome
		if(ruoliDaFarScegliere.size()>0){
			
			if(addTitoloSezione){
				DataElement de = new DataElement();
				de.setLabel(RuoliCostanti.LABEL_RUOLO);
				de.setType(DataElementType.TITLE);
				dati.addElement(de);
			}
			
			List<String> ruoli = new ArrayList<>();
			if(addSelezioneVuota){
				ruoli.add("-");
			}
			ruoli.addAll(ruoliDaFarScegliere);
			
			DataElement de = new DataElement();
			de.setLabel(labelParametro);
			de.setValue(nome);
			if (tipoOp.equals(TipoOperazione.ADD) || enableUpdate) {
				de.setType(DataElementType.SELECT);
				de.setValues(ruoli);
				de.setSelected(nome);
			} else {
				de.setType(DataElementType.TEXT);
			}
			de.setName(CostantiControlStation.PARAMETRO_RUOLO);
			de.setSize(this.getSize());
			dati.addElement(de);
		}
		else{
			if(addMsgServiziApplicativoNonDisponibili){
				if(allRuoli.size()>0){
					this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_NON_ESISTONO_ULTERIORI_RUOLI_ASSOCIABILI_AL_SOGGETTO);
				}
				else{
					this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_NON_ESISTONO_RUOLI_ASSOCIABILI_AL_SOGGETTO);
				}
				this.pd.disableEditMode();
			}
		}

		return dati;
	}
	
	public boolean ruoloCheckData(TipoOperazione tipoOp, String nome, List<String> ruoli) throws Exception {
		try {
			
			if(ruoli!=null && ruoli.contains(nome)){
				this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_IL_RUOLO_XX_E_GIA_STATO_ASSOCIATA_AL_SOGGETTO, nome));
				return false;
			}

			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void controlloAccessi(Vector<DataElement> dati) throws Exception{
		
		DataElement de = new DataElement();
		de.setType(DataElementType.TITLE);
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI);
		dati.addElement(de);
		
	}
	
	public void controlloAccessiAutenticazione(Vector<DataElement> dati, String autenticazione, String autenticazioneCustom, String autenticazioneOpzionale,
			boolean confPers, boolean isSupportatoAutenticazioneSoggetti){
		
		if(isSupportatoAutenticazioneSoggetti){
			
			DataElement de = new DataElement();
			de.setType(DataElementType.SUBTITLE);
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE);
			dati.addElement(de);
		
			List<String> autenticazioneValues = TipoAutenticazione.getValues();
			List<String> autenticazioneLabels = TipoAutenticazione.getLabels();
			int totEl = autenticazioneValues.size();
			if (confPers)
				totEl++;
			String[] tipoAutenticazione = new String[totEl];
			String[] labelTipoAutenticazione = new String[totEl];
			for (int i = 0; i < autenticazioneValues.size(); i++) {
				tipoAutenticazione[i]=autenticazioneValues.get(i);
				labelTipoAutenticazione[i]=autenticazioneLabels.get(i);
			}
			if (confPers ){
				tipoAutenticazione[totEl-1] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM;
				labelTipoAutenticazione[totEl-1] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM;
			}
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE);
			de.setType(DataElementType.SELECT);
			de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE);
			de.setValues(tipoAutenticazione);
			de.setLabels(labelTipoAutenticazione);
			//		de.setOnChange("CambiaTipoAuth('" + tipoOp + "', " + numCorrApp + ")");
			de.setPostBack(true);
			de.setSelected(autenticazione);
			dati.addElement(de);
	
			de = new DataElement();
			de.setLabel("");
			if (autenticazione == null ||
					!autenticazione.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM))
				de.setType(DataElementType.HIDDEN);
			else
				de.setType(DataElementType.TEXT_EDIT);
			de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM);
			de.setValue(autenticazioneCustom);
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_OPZIONALE);
			de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_OPZIONALE);
			if(TipoAutenticazione.DISABILITATO.equals(autenticazione)==false){
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(ServletUtils.isCheckBoxEnabled(autenticazioneOpzionale));
			}
			else{
				de.setType(DataElementType.HIDDEN);
				de.setValue("");
			}
			dati.addElement(de);
			
		}
		
	}
	
	public void controlloAccessiAutorizzazione(Vector<DataElement> dati, TipoOperazione tipoOperazione, String servletChiamante,
			String autenticazione, 
			String autorizzazione, String autorizzazioneCustom, 
			String autorizzazioneAutenticati, String urlAutorizzazioneAutenticati, int numAutenticati, List<String> autenticati, String autenticato,
			String autorizzazioneRuoli,  String urlAutorizzazioneRuoli, int numRuoli, String ruolo, String autorizzazioneRuoliTipologia, String autorizzazioneRuoliMatch,
			boolean confPers, boolean isSupportatoAutenticazione, boolean contaListe, boolean isPortaDelegata,
			boolean addTitoloSezione) throws Exception{
		
		DataElement de = new DataElement();
		de.setType(DataElementType.SUBTITLE);
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE);
		dati.addElement(de);
		
		List<String> auturizzazioneValues = AutorizzazioneUtilities.getStati();
		int totEl = auturizzazioneValues.size();
		if (confPers )
			totEl++;
		String[] tipoAutorizzazione = new String[totEl];
		for (int i = 0; i < auturizzazioneValues.size(); i++) {
			tipoAutorizzazione[i]=auturizzazioneValues.get(i);
		}
		if (confPers ){
			tipoAutorizzazione[totEl-1] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM;
		}
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE);
		de.setType(DataElementType.SELECT);
		de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE);
		de.setValues(tipoAutorizzazione);
		de.setPostBack(true);
		de.setSelected(autorizzazione);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel("");
		if (autorizzazione == null ||
				!autorizzazione.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM))
			de.setType(DataElementType.HIDDEN);
		else
			de.setType(DataElementType.TEXT_EDIT);
		de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM);
		de.setValue(autorizzazioneCustom);
		dati.addElement(de);
		
		
		
		if(AutorizzazioneUtilities.STATO_DISABILITATO.equals(autorizzazione)==false){
		
			boolean autorizzazione_autenticazione =  false;
			
			if(AutorizzazioneUtilities.STATO_ABILITATO.equals(autorizzazione)){
			
				autorizzazione_autenticazione = ServletUtils.isCheckBoxEnabled(autorizzazioneAutenticati);
				
				de = new DataElement();
				if(isPortaDelegata){
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE_SERVIZI_APPLICATIVI);
				}
				else{
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE_SOGGETTI);
				}
				de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE);
				if( !isSupportatoAutenticazione ||  (autenticazione!=null && !TipoAutenticazione.DISABILITATO.equals(autenticazione)) ){   
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(autorizzazione_autenticazione);
					de.setPostBack(true);
				}
				else{
					de.setType(DataElementType.HIDDEN);
					de.setValue(Costanti.CHECK_BOX_DISABLED);
				}
				dati.addElement(de);
				
			}
			
			if(TipoOperazione.CHANGE.equals(tipoOperazione)){
				if(urlAutorizzazioneAutenticati!=null && (autorizzazione_autenticazione || CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM.equals(autorizzazione)) ){
					de = new DataElement();
					de.setType(DataElementType.LINK);
					de.setUrl(urlAutorizzazioneAutenticati);
					if(isPortaDelegata){
						if (contaListe) {
							ServletUtils.setDataElementCustomLabel(de,PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SERVIZI_APPLICATIVI,new Long(numAutenticati));
						} else
							ServletUtils.setDataElementCustomLabel(de,PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SERVIZI_APPLICATIVI);
					}
					else{
						if (contaListe) {
							ServletUtils.setDataElementCustomLabel(de,AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORI,new Long(numAutenticati));
						} else
							ServletUtils.setDataElementCustomLabel(de,AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORI);
					}
					dati.addElement(de);
				}
			}
			else{
				if(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD.equals(servletChiamante) &&
						autorizzazione_autenticazione && isPortaDelegata){
					String [] saArray = null;
					if(autenticati!=null && autenticati.size()>0){
						saArray = autenticati.toArray(new String[1]);
					}
					this.addPorteServizioApplicativoToDati(tipoOperazione, dati, autenticato, saArray, 0, false);
				}
			}
			
		}
		
		if(AutorizzazioneUtilities.STATO_DISABILITATO.equals(autorizzazione)==false){
				
			boolean autorizzazione_ruoli = false;
			
			if(AutorizzazioneUtilities.STATO_ABILITATO.equals(autorizzazione)){
			
				autorizzazione_ruoli = ServletUtils.isCheckBoxEnabled(autorizzazioneRuoli);
				
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI);
				de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI);
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(autorizzazione_ruoli);
				de.setPostBack(true);
				dati.addElement(de);
			
			}
					
			if(autorizzazione_ruoli || AutorizzazioneUtilities.STATO_XACML_POLICY.equals(autorizzazione)){
				
				de = new DataElement();
				if(autorizzazione_ruoli){
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_RUOLO_TIPOLOGIA);
				}
				else{
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_RUOLO_TIPOLOGIA_XACML_POLICY);
				}
				de.setName(CostantiControlStation.PARAMETRO_RUOLO_TIPOLOGIA);
				de.setValue(autorizzazioneRuoliTipologia);			
				de.setType(DataElementType.SELECT);
				de.setValues(RuoliCostanti.RUOLI_TIPOLOGIA);
				de.setLabels(RuoliCostanti.RUOLI_TIPOLOGIA_LABEL);
				de.setSelected(autorizzazioneRuoliTipologia);
				if(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD.equals(servletChiamante) ||
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD.equals(servletChiamante)){
					de.setPostBack(true);
				}
				dati.add(de);
				
			}
			
			if(autorizzazione_ruoli){
				String[] tipoRole = { RuoloTipoMatch.ALL.getValue(),
						RuoloTipoMatch.ANY.getValue() };
				String[] labelRole = { CostantiControlStation.LABEL_PARAMETRO_RUOLO_MATCH_ALL, CostantiControlStation.LABEL_PARAMETRO_RUOLO_MATCH_ANY };
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_RUOLO_MATCH);
				de.setName(CostantiControlStation.PARAMETRO_RUOLO_MATCH);
				de.setValue(autorizzazioneRuoliMatch);			
				de.setType(DataElementType.SELECT);
				de.setValues(tipoRole);
				de.setLabels(labelRole);
				de.setSelected(autorizzazioneRuoliMatch);
				dati.add(de);
			}
			
			if(TipoOperazione.CHANGE.equals(tipoOperazione)){
				if(urlAutorizzazioneRuoli!=null && (autorizzazione_ruoli || CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM.equals(autorizzazione)) ){
					
					de = new DataElement();
					de.setType(DataElementType.LINK);
					de.setUrl(urlAutorizzazioneRuoli);
					if (contaListe) {
						ServletUtils.setDataElementCustomLabel(de,RuoliCostanti.LABEL_RUOLI,new Long(numRuoli));
					} else
						ServletUtils.setDataElementCustomLabel(de,RuoliCostanti.LABEL_RUOLI);
					dati.addElement(de);
								
				}
			}
			else{
				if( (AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD.equals(servletChiamante) ||
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD.equals(servletChiamante))
						&& autorizzazione_ruoli){
					FiltroRicercaRuoli filtroRuoli = new FiltroRicercaRuoli();
					if(isPortaDelegata){
						filtroRuoli.setContesto(RuoloContesto.PORTA_DELEGATA);
					}
					else{
						filtroRuoli.setContesto(RuoloContesto.PORTA_APPLICATIVA);
					}
					filtroRuoli.setTipologia(RuoloTipologia.QUALSIASI);
					if(RuoloTipologia.INTERNO.equals(autorizzazioneRuoliTipologia) ){
						filtroRuoli.setTipologia(RuoloTipologia.INTERNO);
					}
					else if(RuoloTipologia.ESTERNO.equals(autorizzazioneRuoliTipologia) ){
						filtroRuoli.setTipologia(RuoloTipologia.ESTERNO);
					}
					this.addRuoliToDati(tipoOperazione, dati, false, filtroRuoli, ruolo, null, true, false,
							AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_RUOLO, addTitoloSezione);
				}
			}
			
		}
	}
	
	public void controlloAccessiAutorizzazioneContenuti(Vector<DataElement> dati, String autorizzazioneContenuti){
		
		if (this.isModalitaAvanzata()) {
			DataElement de = new DataElement();
			de.setType(DataElementType.SUBTITLE);
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI);
			dati.addElement(de);
		}
		
		DataElement de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_AUTORIZZAZIONE_CONTENUTI);
		if (this.isModalitaStandard()) 
			de.setType(DataElementType.HIDDEN);
		else
			de.setType(DataElementType.TEXT_EDIT);

		de.setName(CostantiControlStation.PARAMETRO_AUTORIZZAZIONE_CONTENUTI);
		de.setValue(autorizzazioneContenuti);
		dati.addElement(de);
	}
	
	public boolean controlloAccessiCheck(TipoOperazione tipoOperazione, 
			String autenticazione, String autenticazioneOpzionale, 
			String autorizzazione, String autorizzazioneAutenticati, String autorizzazioneRuoli,  
			String autorizzazioneRuoliTipologia, String autorizzazioneRuoliMatch,
			boolean isSupportatoAutenticazione, boolean isPortaDelegata,
			List<String> ruoli) throws Exception{
		try {
			
			if(AutorizzazioneUtilities.STATO_ABILITATO.equals(autorizzazione)){
				
				String labelAutenticati = null;
				if(isPortaDelegata){
					labelAutenticati = CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE_SERVIZI_APPLICATIVI;
				}
				else{
					labelAutenticati = CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE_SOGGETTI;
				}
				
				// autorizzazione abilitata
				
				if(ServletUtils.isCheckBoxEnabled(autorizzazioneAutenticati)==false && 
						ServletUtils.isCheckBoxEnabled(autorizzazioneRuoli)==false){
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_SELEZIONARE_ALMENO_UNA_MODALITÀ_DI_AUTORIZZAZIONE_TRA_XX_E_YY,
							labelAutenticati, CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI));
					return false;
				}
				
				if(ServletUtils.isCheckBoxEnabled(autorizzazioneAutenticati) && 
						ServletUtils.isCheckBoxEnabled(autorizzazioneRuoli)==false){
					// Se l'autorizzazione è solamente basata sull'autenticazione dei chiamanti, una autenticazione DEVE essere presente e non deve essere opzionale
					if(isSupportatoAutenticazione){
						if(TipoAutenticazione.DISABILITATO.equals(autenticazione)){
							this.pd.setMessage(MessageFormat.format(
									CostantiControlStation.MESSAGGIO_ERRORE_CON_LA_SOLA_MODALITA_DI_AUTORIZZAZIONE_XX_DEVE_ESSERE_INDICATA_ANCHE_UNA_MODALITA_DI_AUTENTICAZIONE_YY,
									labelAutenticati));
							return false;
						}
						if(ServletUtils.isCheckBoxEnabled(autenticazioneOpzionale)){
							this.pd.setMessage(MessageFormat.format(
									CostantiControlStation.MESSAGGIO_ERRORE_CON_LA_SOLA_MODALITA_DI_AUTORIZZAZIONE_XX_NON_E_POSSIBILE_ASSOCIATA_UNA_MODALITÀ_DI_AUTENTICAZIONE_OPZIONALE,
									labelAutenticati));
							return false;
						}
					}
				}
				
				if(ServletUtils.isCheckBoxEnabled(autorizzazioneAutenticati) && 
						ServletUtils.isCheckBoxEnabled(autorizzazioneRuoli)){
					if(isSupportatoAutenticazione && ServletUtils.isCheckBoxEnabled(autenticazioneOpzionale)==false){
						this.pd.setMessage(MessageFormat.format(
								CostantiControlStation.MESSAGGIO_ERRORE_CON_UNA_MODALITA_DI_AUTENTICAZIONE_OBBLIGATORIA_NON_E_POSSIBILE_SELEZIONARE_ENTRAMBE_LE_MODALITA_DI_AUTORIZZAZIONE,
								labelAutenticati, CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI));
						return false;
					}
				}
				
			}
			
			if(AutorizzazioneUtilities.STATO_ABILITATO.equals(autorizzazione) ||
					AutorizzazioneUtilities.STATO_XACML_POLICY.equals(autorizzazione) ){
				
				if(AutorizzazioneUtilities.STATO_XACML_POLICY.equals(autorizzazione)){
					if(ruoli!=null && ruoli.size()>0){
						this.pd.setMessage(MessageFormat.format(
								CostantiControlStation.MESSAGGIO_ERRORE_LA_PORTA_CONTIENE_GIA_DEI_RUOLI_CHE_NON_SONO_COMPATIBILI_CON_LA_NUOVA_AUTORIZZAZIONE,
								AutorizzazioneUtilities.STATO_XACML_POLICY));
						return false;
					}
				}
				
				RuoloTipologia ruoloTipologia = RuoloTipologia.toEnumConstant(autorizzazioneRuoliTipologia);
				if(RuoloTipologia.INTERNO.equals(ruoloTipologia)){
					if(isSupportatoAutenticazione){
						if(TipoAutenticazione.DISABILITATO.equals(autenticazione)){
							this.pd.setMessage(MessageFormat.format(
									CostantiControlStation.MESSAGGIO_ERRORE_CON_UNA_FONTE_PER_I_RUOLI_DI_TIPO_XX_DEVE_ESSERE_ASSOCIATA_UNA_MODALITÀ_DI_AUTENTICAZIONE,
									RuoliCostanti.LABEL_PARAMETRO_RUOLO_TIPOLOGIA.toLowerCase(), RuoliCostanti.RUOLI_TIPOLOGIA_LABEL_INTERNO));
							return false;
						}
						if(ServletUtils.isCheckBoxEnabled(autenticazioneOpzionale)){
							this.pd.setMessage(MessageFormat.format(
									CostantiControlStation.MESSAGGIO_ERRORE_CON_UNA_FONTE_PER_I_RUOLI_DI_TIPO_XX_NON_E_POSSIBILE_ASSOCIATA_UNA_MODALITÀ_DI_AUTENTICAZIONE_OPZIONALE,
									RuoliCostanti.LABEL_PARAMETRO_RUOLO_TIPOLOGIA.toLowerCase(), RuoliCostanti.RUOLI_TIPOLOGIA_LABEL_INTERNO));
							return false;
						}
					}
				}
				
				// check tipologia rispetto ai ruoli esistenti
				List<String> ruoliNonCompatibili = new ArrayList<>();
				if(ruoli!=null && ruoli.size()>0){
					for (String ruolo : ruoli) {
						Ruolo ruoloObject = this.ruoliCore.getRuolo(ruolo);
						if(RuoloTipologia.INTERNO.equals(ruoloTipologia)){
							if(RuoloTipologia.ESTERNO.equals(ruoloObject.getTipologia())){
								ruoliNonCompatibili.add(ruolo);
							}
						}
						if(RuoloTipologia.ESTERNO.equals(ruoloTipologia)){
							if(RuoloTipologia.INTERNO.equals(ruoloObject.getTipologia())){
								ruoliNonCompatibili.add(ruolo);
							}
						}
					}
				}
				if(ruoliNonCompatibili.size()>0){
					String label = "";
					if(RuoloTipologia.INTERNO.equals(ruoloTipologia)){
						label = RuoliCostanti.RUOLI_TIPOLOGIA_LABEL_INTERNO;
					}
					else{
						label = RuoliCostanti.RUOLI_TIPOLOGIA_LABEL_ESTERNO;
					}
					this.pd.setMessage(MessageFormat.format(
							CostantiControlStation.MESSAGGIO_ERRORE_LA_PORTA_CONTIENE_DEI_RUOLI_XX_CHE_NON_SONO_COMPATIBILI_CON_LA_NUOVA_FONTE_SCELTA,
							ruoliNonCompatibili.toString(), RuoliCostanti.LABEL_PARAMETRO_RUOLO_TIPOLOGIA.toLowerCase(), label));
					return false;
				}
			}

			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public String getLabelStatoControlloAccessi(String autenticazione, String autenticazioneOpzionale, String autenticazioneCustom,
			String autorizzazione, String autorizzazioneContenuti,String autorizzazioneCustom) {
		String label = CostantiControlStation.DEFAULT_VALUE_DISABILITATO;
		
		if(autenticazione != null && !TipoAutenticazione.DISABILITATO.equals(autenticazione))
			return CostantiControlStation.DEFAULT_VALUE_ABILITATO;
		
		if(autenticazioneOpzionale != null && ServletUtils.isCheckBoxEnabled(autenticazioneOpzionale))
			return CostantiControlStation.DEFAULT_VALUE_ABILITATO;
		
		if(!AutorizzazioneUtilities.STATO_DISABILITATO.equals(autorizzazione))
			return CostantiControlStation.DEFAULT_VALUE_ABILITATO;
		
		if(StringUtils.isNotEmpty(autorizzazioneContenuti))
			return CostantiControlStation.DEFAULT_VALUE_ABILITATO;
		
		return label;
	}
	
	
	public DataElement getServiceBindingDataElement(IProtocolFactory<?> protocolFactory, boolean used, ServiceBinding serviceBinding) throws Exception{
		DataElement de = null;
		try {
			List<ServiceBinding> serviceBindingList = this.core.getServiceBindingList(protocolFactory);
			
			de = new DataElement();
			de.setName(CostantiControlStation.PARAMETRO_SERVICE_BINDING);
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING);
			
			if(serviceBindingList != null && serviceBindingList.size() > 1){
				if(used){
					de.setType(DataElementType.TEXT);
					de.setValue(serviceBinding.toString());
				}else {
					de.setSelected(serviceBinding.toString());
					de.setType(DataElementType.SELECT);
					de.setPostBack(true);

					String [] values = new String[serviceBindingList.size()];
					String [] labels = new String[serviceBindingList.size()];
					for (int i =0; i < serviceBindingList.size() ; i ++) {
						ServiceBinding serviceBinding2 = serviceBindingList.get(i);
						switch (serviceBinding2) {
						case REST:
							labels[i] = CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_REST;
							values[i] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_REST;
							break;
						case SOAP:
						default:
							labels[i] = CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_SOAP;
							values[i] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_SOAP;
							break;
						}
					}
					
					de.setValues(values);
					de.setLabels(labels);
				}
			} else {
				de.setValue(serviceBinding.toString());
				de.setType(DataElementType.HIDDEN);
			}
			de.setSize(this.getSize());
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
		return de;
	}
	public DataElement getMessageTypeDataElement(String parametroMessageType, IProtocolFactory<?> protocolFactory, ServiceBinding serviceBinding,MessageType value) throws Exception{
		return this.getMessageTypeDataElement(parametroMessageType, protocolFactory, serviceBinding, value, false);
	}
	
	public DataElement getMessageTypeDataElement(String parametroMessageType, IProtocolFactory<?> protocolFactory, ServiceBinding serviceBinding,MessageType value, boolean hidden) throws Exception{
		DataElement de = null;
		try {
			List<MessageType> messageTypeList = this.core.getMessageTypeList(protocolFactory, serviceBinding);
			
			de = new DataElement();
			de.setName(parametroMessageType);
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_MESSAGE_TYPE);
			
			if(!hidden && messageTypeList != null && messageTypeList.size() > 1){
					de.setSelected(value != null ? value.toString() : null);
					de.setType(DataElementType.SELECT);
					//de.setPostBack(true);

					String [] values = new String[messageTypeList.size()+ 1];
					String [] labels = new String[messageTypeList.size()+ 1];
					labels[0] = CostantiControlStation.LABEL_PARAMETRO_MESSAGE_TYPE_DEFAULT;
					values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_DEFAULT;
					for (int i = 1 ; i <= messageTypeList.size() ; i ++) {
						MessageType type = messageTypeList.get(i-1);
						switch (type) {
						case BINARY:
							labels[i] = CostantiControlStation.LABEL_PARAMETRO_MESSAGE_TYPE_BINARY;
							values[i] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_BINARY;
							break;
						case JSON:
							labels[i] = CostantiControlStation.LABEL_PARAMETRO_MESSAGE_TYPE_JSON;
							values[i] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_JSON;
							break;
						case MIME_MULTIPART:
							labels[i] = CostantiControlStation.LABEL_PARAMETRO_MESSAGE_TYPE_MIME_MULTIPART;
							values[i] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_MIME_MULTIPART;
							break;
						case SOAP_11:
							labels[i] = CostantiControlStation.LABEL_PARAMETRO_MESSAGE_TYPE_SOAP_11;
							values[i] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_SOAP_11;
							break;
						case SOAP_12:
							labels[i] = CostantiControlStation.LABEL_PARAMETRO_MESSAGE_TYPE_SOAP_12;
							values[i] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_SOAP_12;
							break;
						case XML:
						default:
							labels[i] = CostantiControlStation.LABEL_PARAMETRO_MESSAGE_TYPE_XML;
							values[i] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_XML;
							break;
						}
					}
					
					de.setValues(values);
					de.setLabels(labels);
			} else {
				de.setValue(value != null ? value.toString() : null);
				de.setType(DataElementType.HIDDEN);
			}
			de.setSize(this.getSize());
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
		return de;
	}
	
	public DataElement getInterfaceTypeDataElement(TipoOperazione tipoOperazione, IProtocolFactory<?> protocolFactory, ServiceBinding serviceBinding,org.openspcoop2.protocol.manifest.constants.InterfaceType value) throws Exception{
		DataElement de = null;
		try {
			List<org.openspcoop2.protocol.manifest.constants.InterfaceType> interfaceTypeList = this.core.getInterfaceTypeList(protocolFactory, serviceBinding);
			
			de = new DataElement();
			de.setName(CostantiControlStation.PARAMETRO_INTERFACE_TYPE);
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_INTERFACE_TYPE);
			
			switch (tipoOperazione) {
			case ADD:
				if(interfaceTypeList != null && interfaceTypeList.size() > 1){
					de.setSelected(value != null ? value.toString() : null);
					de.setType(DataElementType.SELECT);
					de.setPostBack(true);

					String [] values = new String[interfaceTypeList.size()];
					String [] labels = new String[interfaceTypeList.size()];
					for (int i =0; i < interfaceTypeList.size() ; i ++) {
						org.openspcoop2.protocol.manifest.constants.InterfaceType type = interfaceTypeList.get(i);
						switch (type) {
						case OPEN_API_3:
							labels[i] = CostantiControlStation.LABEL_PARAMETRO_INTERFACE_TYPE_OPEN_API_3;
							values[i] = CostantiControlStation.VALUE_PARAMETRO_INTERFACE_TYPE_OPEN_API_3;
							break;
						case SWAGGER_2:
							labels[i] = CostantiControlStation.LABEL_PARAMETRO_INTERFACE_TYPE_SWAGGER_2;
							values[i] = CostantiControlStation.VALUE_PARAMETRO_INTERFACE_TYPE_SWAGGER_2;
							break;
						case WADL:
							labels[i] = CostantiControlStation.LABEL_PARAMETRO_INTERFACE_TYPE_WADL;
							values[i] = CostantiControlStation.VALUE_PARAMETRO_INTERFACE_TYPE_WADL;
							break;
						case WSDL_11:
						default:
							labels[i] = CostantiControlStation.LABEL_PARAMETRO_INTERFACE_TYPE_WSDL_11;
							values[i] = CostantiControlStation.VALUE_PARAMETRO_INTERFACE_TYPE_WSDL_11;
							break;
						}
					}
					
					de.setValues(values);
					de.setLabels(labels);
			} else {
				de.setValue(value != null ? value.toString() : null);
				de.setType(DataElementType.HIDDEN);
			}
				break;
			case CHANGE:
			case DEL:
			case LIST:
			case LOGIN:
			case LOGOUT:
			case OTHER:
			default:
				de.setValue(value != null ? value.toString() : null);
				de.setType(DataElementType.HIDDEN);
				break;
			}
			
			de.setSize(this.getSize());
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
		return de;
	}
	
	public boolean porteAppAzioneCheckData(TipoOperazione add, List<String> azioniOccupate) {
		String[] azionis = this.request.getParameterValues(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AZIONI);
		
		if(azionis == null || azionis.length == 0) {
			this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_AZIONE_PORTA_NON_PUO_ESSERE_VUOTA);
			return false;
		}
		
		for (String azione : azionis) {
			if(azioniOccupate.contains(azione)) {
				this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_AZIONE_PORTA_GIA_PRESENTE);
				return false;			
			}
		}
		
		return true;
	}

	public Vector<DataElement> addPorteAzioneToDati(TipoOperazione add, Vector<DataElement> dati, String string,String[] azioniDisponibiliList, String[] azioni) {
		
		DataElement de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_AZIONI);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		// Azione
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_AZIONI);
		de.setValues(azioniDisponibiliList);
		de.setSelezionati(azioni);
		de.setType(DataElementType.MULTI_SELECT);
		de.setName(CostantiControlStation.PARAMETRO_AZIONI);
		de.setRequired(true); 
		dati.addElement(de);
		
		return dati;
	}
	
	// Prepara la lista di azioni delle porte
	public void preparePorteAzioneList(List<String> listaAzioni, String idPorta, Integer parentConfigurazione, List<Parameter> lstParametriBreadcrumbs, String nomePorta, String objectName, List<Parameter> listaParametriSessione) throws Exception {
		try {
			ServletUtils.addListElementIntoSession(this.session, objectName,listaParametriSessione);

			// setto la barra del titolo

			this.pd.setSearchDescription("");
			lstParametriBreadcrumbs.add(new Parameter(CostantiControlStation.LABEL_PARAMETRO_AZIONI_DI + nomePorta,null));

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParametriBreadcrumbs.toArray(new Parameter[lstParametriBreadcrumbs.size()]));

			// setto le label delle colonne
			String[] labels = { CostantiControlStation.LABEL_PARAMETRO_AZIONE};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (listaAzioni != null) {
				Iterator<String> it = listaAzioni.iterator();
				while (it.hasNext()) {
					String nomeAzione = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setValue(nomeAzione);
					de.setIdToRemove(nomeAzione);
					e.addElement(de);

					dati.addElement(e);
				}
				this.pd.setNumEntries(listaAzioni.size());
			}

			
			this.pd.setDati(dati);
			this.pd.setAddButton(true);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	

	public void addFilterServiceBinding(String serviceBinding, boolean postBack, boolean showAPISuffix) throws Exception{
		try {
			ServiceBinding[] serviceBindings = ServiceBinding.values();
			
			String [] values = new String[serviceBindings.length + 1];
			String [] labels = new String[serviceBindings.length + 1];
			labels[0] = CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_QUALSIASI;
			for (int i =0; i < serviceBindings.length ; i ++) {
				ServiceBinding serviceBinding2 = serviceBindings[i];
				switch (serviceBinding2) {
				case REST:
					labels[i+1] = CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_REST;
					values[i+1] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_REST.toLowerCase();
					break;
				case SOAP:
				default:
					labels[i+1] = CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_SOAP;
					values[i+1] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_SOAP.toLowerCase();
					break;
				}
			}
			
			String selectedValue = serviceBinding != null ? serviceBinding : CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_QUALSIASI;
			
			String label = null;
			if(showAPISuffix) {
				label = CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_API;
			}
			else {
				label = CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING;
			}
			
			this.pd.addFilter(Filtri.FILTRO_SERVICE_BINDING, label, selectedValue, values, labels, postBack, this.getSize());
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void setFilterSelectedProtocol(ISearch ricerca, int idLista, int positionFilter) throws Exception{
		List<String> protocolli = this.core.getProtocolli(this.session);
		if(protocolli!=null && protocolli.size()>0) {
			if(protocolli.size()==1) {
				ricerca.addFilter(idLista, Filtri.FILTRO_PROTOCOLLO, protocolli.get(0));
			}
			else {
				ricerca.addFilter(idLista, Filtri.FILTRO_PROTOCOLLI, Filtri.convertToString(protocolli));
			}
		}
	}
	public void addFilterProtocol(ISearch ricerca, int idLista) throws Exception{
		List<String> protocolli = this.core.getProtocolli(this.session);
		if(protocolli!=null && protocolli.size()>1) {
			String filterProtocol = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLO);
			this.addFilterProtocollo(protocolli, filterProtocol, false);
		}
	}
	private void addFilterProtocollo(List<String> protocolli, String protocolloSelected,boolean postBack) throws Exception{
		try {
			
			if(protocolli!=null && protocolli.size()>1) {

				String [] values = new String[protocolli.size() + 1];
				String [] labels = new String[protocolli.size() + 1];
				labels[0] = CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_QUALSIASI;
				values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_QUALSIASI;
				for (int i =0; i < protocolli.size() ; i ++) {
					String protocollo = protocolli.get(i);
					labels[i+1] = getLabelProtocollo(protocollo);
					values[i+1] = protocollo;
				}
				
				String selectedValue = protocolloSelected != null ? protocolloSelected : CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROTOCOLLO_QUALSIASI;
				
				this.pd.addFilter(Filtri.FILTRO_PROTOCOLLO, CostantiControlStation.LABEL_PARAMETRO_PROTOCOLLO, selectedValue, values, labels, postBack, this.getSize());
				
			}
				
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public List<String> getLabelsProtocolli(List<String> protocolli) throws Exception{
		if(protocolli==null || protocolli.size()<=0) {
			return null;
		}
		List<String> l = new ArrayList<>();
		for (String protocollo : protocolli) {
			l.add(this.getLabelProtocollo(protocollo));
		}
		return l;
	}
	
	public String getLabelProtocollo(String protocollo) throws Exception{
		ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
		return protocolFactoryManager.getProtocolFactoryByName(protocollo).getInformazioniProtocol().getLabel();
	}
	
	public String getDescrizioneProtocollo(String protocollo) throws Exception{
		ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
		return protocolFactoryManager.getProtocolFactoryByName(protocollo).getInformazioniProtocol().getDescription();
	}
	
	public String getWebSiteProtocollo(String protocollo) throws Exception{
		ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
		return protocolFactoryManager.getProtocolFactoryByName(protocollo).getInformazioniProtocol().getWebSite();
	}
	
	public String getLabelNomeSoggetto(String protocollo, String tipoSoggetto, String nomeSoggetto) throws Exception{
		ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
		if(protocolFactoryManager.getOrganizationTypes().get(protocollo).size()>1) {
			return tipoSoggetto+"/"+nomeSoggetto;
		}
		else {
			return nomeSoggetto;
		}
	}
	public String getLabelIdAccordo(String protocollo, IDAccordo idAccordo) throws Exception{
		StringBuffer bf = new StringBuffer();
		if(idAccordo.getSoggettoReferente()!=null){
			bf.append(this.getLabelNomeSoggetto(protocollo, idAccordo.getSoggettoReferente().getTipo(), idAccordo.getSoggettoReferente().getNome()));
			bf.append(":");
		}
		bf.append(idAccordo.getNome());
		bf.append(":");
		bf.append(idAccordo.getVersione());
		return bf.toString();
	}
}
