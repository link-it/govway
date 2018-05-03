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
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.CorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.DumpConfigurazioneRegola;
import org.openspcoop2.core.config.MtomProcessor;
import org.openspcoop2.core.config.MtomProcessorFlowParameter;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.constants.RuoloTipoMatch;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.constants.StatiAccordo;
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
import org.openspcoop2.web.lib.audit.web.AuditHelper;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.MenuEntry;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.mvc.properties.beans.BaseItemBean;
import org.openspcoop2.web.lib.mvc.properties.beans.ConfigBean;
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
	
	public boolean isEditModeInProgress() throws Exception{
		String editMode = this.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
		return ServletUtils.isEditModeInProgress(editMode);		
	}

	public boolean isEditModeFinished() throws Exception{
		String editMode = this.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
		return ServletUtils.isEditModeFinished(editMode);		
	}
	
	public String getPostBackElementName() throws Exception{
		return this.getParameter(Costanti.POSTBACK_ELEMENT_NAME);
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

	protected AuditHelper auditHelper;
	public AuditHelper getAuditHelper() {
		return this.auditHelper;
	}
	
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
	private Map<String, Object> mapParametriReaded = null;
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

	private boolean errorInit = false;
	private Exception eErrorInit;
	
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
			
			this.auditHelper = new AuditHelper(request, pd, session);

			this.idBinaryParameterRicevuti = new ArrayList<String>();
			// analisi dei parametri della request
			this.contentType = request.getContentType();
			if ((this.contentType != null) && (this.contentType.indexOf(Costanti.MULTIPART) != -1)) {
				this.multipart = true;
				this.mimeMultipart = new MimeMultipart(request.getInputStream(), this.contentType);
				this.mapParametri = new HashMap<String,InputStream>();
				this.mapParametriReaded = new HashMap<>();
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
			this.errorInit = true;
			this.eErrorInit = e;
		}
		this.idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();
		this.idAccordoFactory = IDAccordoFactory.getInstance();
		this.idServizioFactory = IDServizioFactory.getInstance();
	}
	
//	public boolean isUseIdSogg() {
//		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
//		return ServletUtils.getBooleanAttributeFromSession(CostantiControlStation.PARAMETRO_USAIDSOGG , this.session, false);
//	}
	
	public IDAccordo getIDAccordoFromValues(String nomeAS, String soggettoReferente, String versione) throws Exception{
		Soggetto s = this.soggettiCore.getSoggetto(Integer.parseInt(soggettoReferente));			
		IDSoggetto assr = new IDSoggetto();
		assr.setTipo(s.getTipo());
		assr.setNome(s.getNome());
		int versioneInt = 1;
		if(versione!=null && !"".equals(versione)) {
			versioneInt = Integer.parseInt(versione);
		}
		return this.idAccordoFactory.getIDAccordoFromValues(nomeAS, assr, versioneInt);
	}
	
	public IDServizio getIDServizioFromValues(String tipo, String nome, String soggettoErogatore, String versione) throws Exception{
		Soggetto s = this.soggettiCore.getSoggetto(Integer.parseInt(soggettoErogatore));			
		IDSoggetto assr = new IDSoggetto();
		assr.setTipo(s.getTipo());
		assr.setNome(s.getNome());
		int versioneInt = 1;
		if(versione!=null && !"".equals(versione)) {
			versioneInt = Integer.parseInt(versione);
		}
		return this.idServizioFactory.getIDServizioFromValues(tipo, nome, assr, versioneInt);
	}
	
	public IDServizio getIDServizioFromValues(String tipo, String nome, String tipoSoggettoErogatore, String soggettoErogatore, String versione) throws Exception{
		IDSoggetto assr = new IDSoggetto();
		assr.setTipo(tipoSoggettoErogatore);
		assr.setNome(soggettoErogatore);
		int versioneInt = 1;
		if(versione!=null && !"".equals(versione)) {
			versioneInt = Integer.parseInt(versione);
		}
		return this.idServizioFactory.getIDServizioFromValues(tipo, nome, assr,versioneInt);
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

	private void checkErrorInit() throws Exception {
		if(this.errorInit) {
			throw new Exception("Inizializzazione fallita: "+this.eErrorInit.getMessage(),this.eErrorInit);
		}
	}
	
	public Enumeration<?> getParameterNames() throws Exception {
		this.checkErrorInit();
		if(this.multipart){
			throw new Exception("Not Implemented");
		}
		else {
			return this.request.getParameterNames();
		}
	}
	
	public String [] getParameterValues(String parameterName) throws Exception {
		this.checkErrorInit();
		if(this.multipart){
			throw new Exception("Not Implemented");
		}
		else {
			return this.request.getParameterValues(parameterName);
		}
	}
	
	public String getParameter(String parameterName) throws Exception {
		return getParameter(parameterName, String.class, null);
	}

	public <T> T getParameter(String parameterName, Class<T> type) throws Exception {
		return getParameter(parameterName, type, null);
	}

	public <T> T getParameter(String parameterName, Class<T> type, T defaultValue) throws Exception {
		
		this.checkErrorInit();
		
		
		T toReturn = null;

		if(type == byte[].class){
			throw new Exception("Per leggere un parametro di tipo byte[] utilizzare il metodo getBinaryParameter");
		}

		String paramAsString = null;

		if(this.multipart){
			if(this.mapParametriReaded.containsKey(parameterName)) {
				paramAsString = (String) this.mapParametriReaded.get(parameterName);
			}
			else {
				InputStream inputStream = this.mapParametri.get(parameterName);
				if(inputStream != null){
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					IOUtils.copy(inputStream, baos);
					baos.flush();
					baos.close();
					paramAsString = baos.toString();
					this.mapParametriReaded.put(parameterName, paramAsString);
				}
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
		
		this.checkErrorInit();
		
		
		if(this.multipart){
			if(this.mapParametriReaded.containsKey(parameterName)) {
				return (byte[]) this.mapParametriReaded.get(parameterName);
			}
			else {
				InputStream inputStream = this.mapParametri.get(parameterName);
				if(inputStream != null){
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					IOUtils.copy(inputStream, baos);
					byte[] b = baos.toByteArray();
					this.mapParametriReaded.put(parameterName, b);
					return b;
				}
			}
		}else{
			String paramAsString = this.request.getParameter(parameterName);
			if(paramAsString != null)
				return paramAsString.getBytes();
		}

		return null;
	}

	public String getFileNameParameter(String parameterName) throws Exception {
		
		this.checkErrorInit();
				
		if(this.multipart){
			return this.mapNomiFileParametri.get(parameterName);
		} else 
			return this.request.getParameter(parameterName);

	}
	
	public BinaryParameter getBinaryParameter(String parameterName) throws Exception {
		
		this.checkErrorInit();
				
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
		
		this.checkErrorInit();
				
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
		
		this.checkErrorInit();
				
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
		
		this.checkErrorInit();
				
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
		
		this.checkErrorInit();
				
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
		
		this.checkErrorInit();
				
		if(parameters != null && parameters.length >0){
			for (BinaryParameter binaryParameter : parameters) {
				this.deleteBinaryParameter(binaryParameter);
			}
		}
	}
	
	private void deleteBinaryParameter(BinaryParameter bp) throws Exception{
		
		this.checkErrorInit();
				
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
		
		this.checkErrorInit();
		
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
						Long longValue = null;
						try{
							// soluzione necessaria perche' il tipo di dato number puo' essere utilizzato anche negli input di tipo text che possono non controllare il tipo di dato inserito
							longValue = StringUtils.isNotEmpty(lvS) ? Long.parseLong(lvS) : null;
						}catch(NumberFormatException e) {
							longValue = null;
						}
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
		
		this.checkErrorInit();
				
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

			boolean isModalitaAvanzata = this.isModalitaAvanzata();
			
			boolean multiTenant = ServletUtils.getUserFromSession(this.session).isPermitMultiTenant();
			
			List<IExtendedMenu> extendedMenu = this.core.getExtendedMenu();

			Vector<MenuEntry> menu = new Vector<MenuEntry>();

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
						if(this.core.isGestionePddAbilitata(this)) {
							totEntries ++;
						}
					}

					// Soggetti ed SA
					totEntries += 2;

					// ASPC e ASPS
					if(this.core.isRegistroServiziLocale()){
						if(multiTenant) {
							totEntries +=2;
						}
						else {
							totEntries +=3;
						}
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
				if(pu.isServizi() && this.isModalitaCompleta()){
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
				boolean pddVisualizzate = false;
				// PdD, Soggetti, SA, ASPC e ASPS con permessi S
				if(pu.isServizi()){
					
					//Link PdD
					if(this.core.isRegistroServiziLocale()){
						if(this.core.isGestionePddAbilitata(this)) {
							pddVisualizzate = true;
							entries[index][0] = PddCostanti.LABEL_PDD_MENU_VISUALE_AGGREGATA;
							if (singlePdD == false) {
								entries[index][1] = PddCostanti.SERVLET_NAME_PDD_LIST;
							}else {
								entries[index][1] = PddCostanti.SERVLET_NAME_PDD_SINGLEPDD_LIST;
							}
							index++;
						}
					}

					if(pddVisualizzate) {
					
						// Soggetti 
						entries[index][0] = SoggettiCostanti.LABEL_SOGGETTI_MENU_VISUALE_AGGREGATA;
						entries[index][1] = SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST;
						index++;
	
						//SA
						entries[index][0] = ServiziApplicativiCostanti.LABEL_SA_MENU_VISUALE_AGGREGATA;
						entries[index][1] = ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST;
						index++;
						
					}

					// ASPC e ASPS
					if(this.core.isRegistroServiziLocale()){
						//ASPC
						entries[index][0] = AccordiServizioParteComuneCostanti.LABEL_APC_MENU_VISUALE_AGGREGATA;
						entries[index][1] = AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
								AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO+"="+
								AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE;
						index++;

						//ASPS
						if(multiTenant) {
							entries[index][0] = AccordiServizioParteSpecificaCostanti.LABEL_APS_MENU_VISUALE_AGGREGATA;
							entries[index][1] = AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST+"?"+
									AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE+"="+
									AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_MULTI_TENANT;
							index++;
						}
						else {
							entries[index][0] = AccordiServizioParteSpecificaCostanti.LABEL_APS_MENU_VISUALE_AGGREGATA;
							entries[index][1] = AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST+"?"+
									AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE+"="+
									AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE;
							index++;
							
							entries[index][0] = AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUIZIONI_MENU_VISUALE_AGGREGATA;
							entries[index][1] = AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST+"?"+
									AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE+"="+
									AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE;
							index++;
						}
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
				
				if(!pddVisualizzate) {
					
					// Soggetti 
					entries[index][0] = SoggettiCostanti.LABEL_SOGGETTI_MENU_VISUALE_AGGREGATA;
					entries[index][1] = SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST;
					index++;

					//SA
					if(this.isModalitaCompleta()) {
						entries[index][0] = ServiziApplicativiCostanti.LABEL_SA_MENU_VISUALE_AGGREGATA;
					}
					else {
						entries[index][0] = ServiziApplicativiCostanti.LABEL_APPLICATIVI_MENU_VISUALE_AGGREGATA;
					}
					entries[index][1] = ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST;
					index++;
					
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
				if(pu.isServizi() && this.isModalitaCompleta()){
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




			if (singlePdD) {

				List<ExtendedMenuItem> listStrumenti = null;
				if(extendedMenu!=null){
					for (IExtendedMenu extMenu : extendedMenu) {
						listStrumenti = 
								extMenu.getExtendedItemsMenuStrumenti(isModalitaAvanzata, 
										this.core.isRegistroServiziLocale(), singlePdD, pu);
					}
				}
				
				if ( pu.isCodeMessaggi() || pu.isAuditing() || (listStrumenti!=null && listStrumenti.size()>0) ) {
					// Se l'utente non ha i permessi "diagnostica", devo
					// gestire la reportistica
					MenuEntry me = new MenuEntry();
					me.setTitle(CostantiControlStation.LABEL_STRUMENTI);

					int totEntries = 0;
					if(this.isModalitaAvanzata() && pu.isCodeMessaggi()) {
						totEntries++;
					}
					if(pu.isAuditing()) {
						totEntries++;
					}

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

					if (pu.isAuditing()) {
						entries[i][0] = AuditCostanti.LABEL_AUDIT;
						entries[i][1] = AuditCostanti.SERVLET_NAME_AUDITING;
						i++;
					}
					if (this.isModalitaAvanzata() && pu.isCodeMessaggi()) {
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


					dimensioneEntries = 4; // configurazione, tracciamento, controllo congestione e audit
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
					entries[index][0] = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE_MENU;
					entries[index][1] = ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE;
					index++;
					entries[index][0] = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TRACCIAMENTO_MENU;
					entries[index][1] = ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI;
					index++;
					entries[index][0] = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CONTROLLO_TRAFFICO;
					entries[index][1] = ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_CONGESTIONE;
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
					if (this.isModalitaAvanzata() && pu.isCodeMessaggi()) {
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
		String[][] entries = new String[1][2];
		entries[0][0] = UtentiCostanti.LABEL_UTENTI;
		entries[0][1] = UtentiCostanti.SERVLET_NAME_UTENTI_LIST;
		return entries;
	}





	// *** Utilities generiche ***

	public void initializeFilter(Search ricerca) throws Exception {
		initializeFilter(ricerca, Liste.SOGGETTI);
		initializeFilter(ricerca, Liste.SERVIZIO_APPLICATIVO);
		initializeFilter(ricerca, Liste.ACCORDI);
		initializeFilter(ricerca, Liste.ACCORDI_COOPERAZIONE);
		initializeFilter(ricerca, Liste.SERVIZI);
		initializeFilter(ricerca, Liste.PORTE_DELEGATE);
		initializeFilter(ricerca, Liste.PORTE_APPLICATIVE);
	}
	public void initializeFilter(Search ricerca, int idLista) throws Exception {
		this.setFilterSelectedProtocol(ricerca, idLista);
		this.setFilterRuoloServizioApplicativo(ricerca, idLista);
	}
	
	public Search checkSearchParameters(int idLista, Search ricerca)
			throws Exception {
		try {
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);

			String search = ricerca.getSearchString(idLista);

			if (this.getParameter("index") != null) {
				offset = Integer.parseInt(this.getParameter("index"));
				ricerca.setIndexIniziale(idLista, offset);
			}
			if (this.getParameter("pageSize") != null) {
				limit = Integer.parseInt(this.getParameter("pageSize"));
				ricerca.setPageSize(idLista, limit);
			}
			if (this.getParameter("search") != null) {
				search = this.getParameter("search");
				search = search.trim();
				if (search.equals("")) {
					ricerca.setSearchString(idLista, org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED);
				} else {
					ricerca.setSearchString(idLista, search);
				}
			}
			else {
				if(this.core.isConservaRisultatiRicerca()==false) {
					ricerca.setSearchString(idLista, org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED);
				}
			}
			
			int index=0;
			String nameFilter = PageData.GET_PARAMETRO_FILTER_NAME(index);
			if(this.core.isConservaRisultatiRicerca()==false) {
				ricerca.clearFilters(idLista);
				this.initializeFilter(ricerca,idLista);	
			}
			while (this.getParameter(nameFilter) != null) {
				String paramFilterName = this.getParameter(nameFilter);
				paramFilterName = paramFilterName.trim();
				
				String paramFilterValue = this.getParameter( PageData.GET_PARAMETRO_FILTER_VALUE(index));
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
			String labelApplicativo = CostantiControlStation.LABEL_PARAMETRO_SERVIZIO_APPLICATIVO;
			if(!this.isModalitaCompleta()) {
				labelApplicativo = CostantiControlStation.LABEL_PARAMETRO_APPLICATIVO;
			}
			de.setLabel(labelApplicativo );
			de.setType(DataElementType.SELECT);
			de.setName(CostantiControlStation.PARAMETRO_SERVIZIO_APPLICATIVO);
			de.setValues(servizioApplicativoList);
			de.setSelected(servizioApplicativo);
			dati.addElement(de);
			
		}else{
			if(addMsgServiziApplicativoNonDisponibili){
				if(sizeAttuale>0){
					this.pd.setMessage("Non esistono ulteriori servizi applicativi associabili alla porta",org.openspcoop2.web.lib.mvc.MessageType.INFO);
				}
				else{
					this.pd.setMessage("Non esistono servizi applicativi associabili alla porta",org.openspcoop2.web.lib.mvc.MessageType.INFO);
				}
				this.pd.disableEditMode();
			}
		}

		return dati;
	}
	
	public Vector<DataElement> addPorteSoggettoToDati(TipoOperazione tipoOp, Vector<DataElement> dati, 
			String[] soggettiLabelList, String[] soggettiList, String soggetto, int sizeAttuale, 
				boolean addMsgSoggettiNonDisponibili) {
			
			if(soggettiList!=null && soggettiList.length>0){
			
				DataElement de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_SOGGETTO);
				de.setType(DataElementType.SELECT);
				de.setName(CostantiControlStation.PARAMETRO_SOGGETTO);
				de.setLabels(soggettiLabelList);
				de.setValues(soggettiList);
				de.setSelected(soggetto);
				dati.addElement(de);
				
			}else{
				if(addMsgSoggettiNonDisponibili){
					if(sizeAttuale>0){
						this.pd.setMessage("Non esistono ulteriori soggetti associabili alla porta",org.openspcoop2.web.lib.mvc.MessageType.INFO);
					}
					else{
						this.pd.setMessage("Non esistono soggetti associabili alla porta",org.openspcoop2.web.lib.mvc.MessageType.INFO);
					}
					this.pd.disableEditMode();
				}
			}

			return dati;
		}

	// Controlla i dati del Message-Security
	public boolean WSCheckData(TipoOperazione tipoOp) throws Exception {
		try{
			String messageSecurity = this.getParameter(CostantiControlStation.PARAMETRO_MESSAGE_SECURITY);

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
			String id = this.getParameter(CostantiControlStation.PARAMETRO_ID);
			int idInt = Integer.parseInt(id);
			// String idsogg = this.getParameter("idsogg");
			String elemxml = this.getParameter(CostantiControlStation.PARAMETRO_ELEMENTO_XML);
			String mode = this.getParameter(CostantiControlStation.PARAMETRO_MODE_CORRELAZIONE_APPLICATIVA);
			String pattern = this.getParameter(CostantiControlStation.PARAMETRO_PATTERN);
			String idcorr = this.getParameter(CostantiControlStation.PARAMETRO_ID_CORRELAZIONE);
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
			String id = this.getParameter(CostantiControlStation.PARAMETRO_ID);
			int idInt = Integer.parseInt(id);
			// String idsogg = this.getParameter("idsogg");
			String elemxml = this.getParameter(CostantiControlStation.PARAMETRO_ELEMENTO_XML);
			String mode = this.getParameter(CostantiControlStation.PARAMETRO_MODE_CORRELAZIONE_APPLICATIVA);
			String pattern = this.getParameter(CostantiControlStation.PARAMETRO_PATTERN);
			String idcorr = this.getParameter(CostantiControlStation.PARAMETRO_ID_CORRELAZIONE);
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
			String mtomRichiesta = this.getParameter(CostantiControlStation.PARAMETRO_MTOM_RICHIESTA);
			String mtomRisposta = this.getParameter(CostantiControlStation.PARAMETRO_MTOM_RISPOSTA);

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
			String id = this.getParameter(CostantiControlStation.PARAMETRO_ID);
			int idInt = Integer.parseInt(id);
			String nome = this.getParameter(CostantiControlStation.PARAMETRO_NOME);
			String contentType =this.getParameter(CostantiControlStation.PARAMETRO_CONTENT_TYPE);
			//	String obbligatorio = this.getParameter(CostantiControlStation.PARAMETRO_OBBLIGATORIO);
			String pattern = this.getParameter(CostantiControlStation.PARAMETRO_PATTERN);


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
			// imposto nel default value il valore attuale.
			// Mi tengo cmq il default value attuale per le opzioni di selected
			Object defaultItemValue = null;
			if(item instanceof AbstractConsoleItem<?> ) {
				AbstractConsoleItem<?> itemConsole = (AbstractConsoleItem<?>) item;
				defaultItemValue = itemConsole.getDefaultValue();
			}
			ProtocolPropertiesUtils.setDefaultValue(item, property); 

			ProtocolProperty protocolProperty = ProtocolPropertiesUtils.getProtocolProperty(item.getId(), listaProtocolPropertiesDaDB); 
			dati = ProtocolPropertiesUtilities.itemToDataElement(dati,item, defaultItemValue,
					consoleOperationType, consoleInterfaceType, binaryPropertyChangeInfoProprietario, protocolProperty, this.getSize());
		}

		// Imposto il flag per indicare che ho caricato la configurazione
		DataElement de = new DataElement();
		de.setName(ProtocolPropertiesCostanti.PARAMETRO_PP_SET);
		de.setType(DataElementType.HIDDEN);
		de.setValue("ok");
		dati.add(de);

		return dati;
	}
	
	
	public Vector<DataElement> addProtocolPropertiesToDatiAsHidden(Vector<DataElement> dati, ConsoleConfiguration consoleConfiguration,ConsoleOperationType consoleOperationType,
			ConsoleInterfaceType consoleInterfaceType, ProtocolProperties protocolProperties) throws Exception{
		return addProtocolPropertiesToDatiAsHidden(dati, consoleConfiguration, consoleOperationType, consoleInterfaceType, protocolProperties, null, null);
	}

	public Vector<DataElement> addProtocolPropertiesToDatiAsHidden(Vector<DataElement> dati, ConsoleConfiguration consoleConfiguration,ConsoleOperationType consoleOperationType,
			ConsoleInterfaceType consoleInterfaceType, ProtocolProperties protocolProperties, List<ProtocolProperty> listaProtocolPropertiesDaDB ,Properties binaryPropertyChangeInfoProprietario) throws Exception{
		for (BaseConsoleItem item : consoleConfiguration.getConsoleItem()) {
			AbstractProperty<?> property = ProtocolPropertiesUtils.getAbstractPropertyById(protocolProperties, item.getId());
			// imposto nel default value il valore attuale.
			// Mi tengo cmq il default value attuale per le opzioni di selected
			Object defaultItemValue = null;
			if(item instanceof AbstractConsoleItem<?> ) {
				AbstractConsoleItem<?> itemConsole = (AbstractConsoleItem<?>) item;
				defaultItemValue = itemConsole.getDefaultValue();
			}
			ProtocolPropertiesUtils.setDefaultValue(item, property); 

			ProtocolProperty protocolProperty = ProtocolPropertiesUtils.getProtocolProperty(item.getId(), listaProtocolPropertiesDaDB); 
			dati = ProtocolPropertiesUtilities.itemToDataElementAsHidden(dati,item, defaultItemValue,
					consoleOperationType, consoleInterfaceType, binaryPropertyChangeInfoProprietario, protocolProperty, this.getSize());
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
	
	public void controlloAccessiAutorizzazione(Vector<DataElement> dati, TipoOperazione tipoOperazione, String servletChiamante, Object oggetto,
			String autenticazione, 
			String autorizzazione, String autorizzazioneCustom, 
			String autorizzazioneAutenticati, String urlAutorizzazioneAutenticati, int numAutenticati, List<String> autenticati, String autenticato,
			String autorizzazioneRuoli,  String urlAutorizzazioneRuoli, int numRuoli, String ruolo, String autorizzazioneRuoliTipologia, String autorizzazioneRuoliMatch,
			boolean confPers, boolean isSupportatoAutenticazione, boolean contaListe, boolean isPortaDelegata,
			boolean addTitoloSezione) throws Exception{
		this.controlloAccessiAutorizzazione(dati, tipoOperazione, servletChiamante, oggetto, 
				autenticazione, autorizzazione, autorizzazioneCustom, 
				autorizzazioneAutenticati, urlAutorizzazioneAutenticati, numAutenticati, autenticati, null, autenticato, 
				autorizzazioneRuoli, urlAutorizzazioneRuoli, numRuoli, ruolo, autorizzazioneRuoliTipologia, autorizzazioneRuoliMatch, 
				confPers, isSupportatoAutenticazione, contaListe, isPortaDelegata, addTitoloSezione);
		
	}
	
	public void controlloAccessiAutorizzazione(Vector<DataElement> dati, TipoOperazione tipoOperazione, String servletChiamante, Object oggetto,
			String autenticazione, String autorizzazione, String autorizzazioneCustom, 
			String autorizzazioneAutenticati, String urlAutorizzazioneAutenticati, int numAutenticati, List<String> autenticati, List<String> autenticatiLabel, String autenticato,
			String autorizzazioneRuoli,  String urlAutorizzazioneRuoli, int numRuoli, String ruolo, String autorizzazioneRuoliTipologia, String autorizzazioneRuoliMatch,
			boolean confPers, boolean isSupportatoAutenticazione, boolean contaListe, boolean isPortaDelegata, boolean addTitoloSezione) throws Exception{
		
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
		
		
		boolean old_autorizzazione_autenticazione = false;
		boolean old_autorizzazione_ruoli = false;
		String old_autorizzazione = null;
		
		String nomePostback = this.getPostBackElementName();
		if(!CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE.equals(nomePostback) &&
				TipoOperazione.CHANGE.equals(tipoOperazione) && (numAutenticati>0)) {
			autorizzazioneAutenticati = Costanti.CHECK_BOX_ENABLED;
		}
		if(!CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI.equals(nomePostback) &&
				TipoOperazione.CHANGE.equals(tipoOperazione) && (numRuoli>0)) {
			autorizzazioneRuoli = Costanti.CHECK_BOX_ENABLED;
		}
		
		if(TipoOperazione.CHANGE.equals(tipoOperazione) && oggetto!=null){
			if(isPortaDelegata){
				PortaDelegata pd = (PortaDelegata) oggetto;
				old_autorizzazione = AutorizzazioneUtilities.convertToStato(pd.getAutorizzazione());
				old_autorizzazione_autenticazione = TipoAutorizzazione.isAuthenticationRequired(pd.getAutorizzazione());
				old_autorizzazione_ruoli = TipoAutorizzazione.isRolesRequired(pd.getAutorizzazione());
			}
			else {
				PortaApplicativa pa = (PortaApplicativa) oggetto;
				old_autorizzazione = AutorizzazioneUtilities.convertToStato(pa.getAutorizzazione());
				old_autorizzazione_autenticazione = TipoAutorizzazione.isAuthenticationRequired(pa.getAutorizzazione());
				old_autorizzazione_ruoli = TipoAutorizzazione.isRolesRequired(pa.getAutorizzazione());
			}
		}
		
		if(AutorizzazioneUtilities.STATO_DISABILITATO.equals(autorizzazione)==false){
		
			boolean autorizzazione_autenticazione =  false;
			
			if(AutorizzazioneUtilities.STATO_ABILITATO.equals(autorizzazione)){
			
				autorizzazione_autenticazione = ServletUtils.isCheckBoxEnabled(autorizzazioneAutenticati);
				
				de = new DataElement();
				if(isPortaDelegata){
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE_SERVIZI_APPLICATIVI);
				}
				else{
					String labelSoggetti = (isSupportatoAutenticazione && (autenticazione!=null && !TipoAutenticazione.DISABILITATO.equals(autenticazione))) ? CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE_SOGGETTI : CostantiControlStation.LABEL_PARAMETRO_SOGGETTI;
					de.setLabel(labelSoggetti);
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
				if(urlAutorizzazioneAutenticati!=null && autorizzazione_autenticazione && (old_autorizzazione_autenticazione || CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM.equals(old_autorizzazione)) ){
					de = new DataElement();
					de.setType(DataElementType.LINK);
					de.setUrl(urlAutorizzazioneAutenticati);
					if(isPortaDelegata){
						String labelApplicativi = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SERVIZI_APPLICATIVI;
						if(!this.isModalitaCompleta()) {
							labelApplicativi = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_APPLICATIVI;
						}
						if (contaListe) {
							ServletUtils.setDataElementCustomLabel(de,labelApplicativi,new Long(numAutenticati));
						} else
							ServletUtils.setDataElementCustomLabel(de,labelApplicativi);
					}
					else{
						if (contaListe) {
							ServletUtils.setDataElementCustomLabel(de,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOGGETTI,new Long(numAutenticati));
						} else
							ServletUtils.setDataElementCustomLabel(de,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOGGETTI);
					}
					dati.addElement(de);
				}
			}
			else{
				if(!isSupportatoAutenticazione ||  (autenticazione!=null && !TipoAutenticazione.DISABILITATO.equals(autenticazione))) {
					if(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD.equals(servletChiamante) && autorizzazione_autenticazione && isPortaDelegata){
						String [] saArray = null;
						if(autenticati!=null && autenticati.size()>0){
							saArray = autenticati.toArray(new String[1]);
						}
						this.addPorteServizioApplicativoToDati(tipoOperazione, dati, autenticato, saArray, 0, false);
					}
//					if(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD.equals(servletChiamante) && autorizzazione_autenticazione && !isPortaDelegata && isSupportatoAutenticazione) {
					if(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD.equals(servletChiamante) && autorizzazione_autenticazione && !isPortaDelegata) {
						String soggettiList [] = null;
						String soggettiLabelList[] = null;
						if(autenticati!=null && autenticati.size()>0){
							soggettiList = autenticati.toArray(new String[1]);
							if(autenticatiLabel!=null && autenticatiLabel.size()>0){
								soggettiLabelList = autenticatiLabel.toArray(new String[1]);
							}
						}
						this.addPorteSoggettoToDati(tipoOperazione, dati, soggettiLabelList, soggettiList, autenticato, 0, false);
					}
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
				if(urlAutorizzazioneRuoli!=null && autorizzazione_ruoli && (old_autorizzazione_ruoli || CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM.equals(old_autorizzazione)) ){
					
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
			boolean isSupportatoAutenticazione, boolean isPortaDelegata, Object oggetto,
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
						// Rilasso questo vincolo alla solta autenticazione di tipo http-basic, poiche' nelle altre l'identificazione di un applicativo o soggetto non e' obbligatoria
						if(TipoAutenticazione.BASIC.equals(autenticazione)){
							this.pd.setMessage(MessageFormat.format(
									CostantiControlStation.MESSAGGIO_ERRORE_CON_UNA_MODALITA_DI_AUTENTICAZIONE_BASIC_OBBLIGATORIA_NON_E_POSSIBILE_SELEZIONARE_ENTRAMBE_LE_MODALITA_DI_AUTORIZZAZIONE,
									labelAutenticati, CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI));
							return false;
						}
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

			if(oggetto!=null){
				if(isPortaDelegata){
					PortaDelegata pd = (PortaDelegata) oggetto;
					if(AutorizzazioneUtilities.STATO_DISABILITATO.equals(autorizzazione) ||
							(ServletUtils.isCheckBoxEnabled(autorizzazioneRuoli)==false) ){
						if(pd.getRuoli()!=null && pd.getRuoli().sizeRuoloList()>0) {
							this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_RUOLI_PRESENTI_AUTORIZZAZIONE_DISABILITATA);
							return false;
						}
					}
					if(AutorizzazioneUtilities.STATO_DISABILITATO.equals(autorizzazione) ||
							(ServletUtils.isCheckBoxEnabled(autorizzazioneAutenticati)==false) ){
						if(pd.sizeServizioApplicativoList()>0) {
							this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_APPLICATIVI_PRESENTI_AUTORIZZAZIONE_DISABILITATA);
							return false;
						}
					}
					if(isSupportatoAutenticazione && pd.getAutenticazione()!=null && 
							!pd.getAutenticazione().equals(autenticazione)){
						// modiifcata autenticazione
						if(pd.sizeServizioApplicativoList()>0) {
							this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_APPLICATIVI_PRESENTI_AUTENTICAZIONE_MODIFICATA);
							return false;
						}
					}
				}
				else {
					PortaApplicativa pa = (PortaApplicativa) oggetto;
					if(AutorizzazioneUtilities.STATO_DISABILITATO.equals(autorizzazione) ||
							(ServletUtils.isCheckBoxEnabled(autorizzazioneRuoli)==false) ){
						if(pa.getRuoli()!=null && pa.getRuoli().sizeRuoloList()>0) {
							this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_RUOLI_PRESENTI_AUTORIZZAZIONE_DISABILITATA);
							return false;
						}
					}
					if(AutorizzazioneUtilities.STATO_DISABILITATO.equals(autorizzazione) ||
							(ServletUtils.isCheckBoxEnabled(autorizzazioneAutenticati)==false) ){
						if(pa.getSoggetti()!=null && pa.getSoggetti().sizeSoggettoList()>0) {
							this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_SOGGETTI_PRESENTI_AUTORIZZAZIONE_DISABILITATA);
							return false;
						}
					}
					if(isSupportatoAutenticazione && pa.getAutenticazione()!=null && 
							!pa.getAutenticazione().equals(autenticazione)){
						// modiifcata autenticazione
						if(pa.getSoggetti()!=null && pa.getSoggetti().sizeSoggettoList()>0) {
							this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_SOGGETTI_PRESENTI_AUTENTICAZIONE_MODIFICATA);
							return false;
						}
					}
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
	
	public DataElement getServiceBindingDataElement(ServiceBinding serviceBinding) throws Exception{
		return getServiceBindingDataElement(null, false, serviceBinding, true);
	}
	
	public DataElement getServiceBindingDataElement(IProtocolFactory<?> protocolFactory, boolean used, ServiceBinding serviceBinding) throws Exception{
		return getServiceBindingDataElement(protocolFactory, used, serviceBinding, false);
	}
	
	public DataElement getServiceBindingDataElement(IProtocolFactory<?> protocolFactory, boolean used, ServiceBinding serviceBinding, boolean forceHidden) throws Exception{
		DataElement de = null;
		if(!forceHidden) {
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
		} else {
			de = new DataElement();
			de.setName(CostantiControlStation.PARAMETRO_SERVICE_BINDING);
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING);
			de.setValue(serviceBinding !=null ? serviceBinding.toString() : null);
			de.setType(DataElementType.HIDDEN);
		}
		return de;
	}
	public DataElement getMessageTypeDataElement(String parametroMessageType, IProtocolFactory<?> protocolFactory, ServiceBinding serviceBinding,MessageType value) throws Exception{
		return this.getMessageTypeDataElement(parametroMessageType, protocolFactory, serviceBinding, value, this.isModalitaStandard()); // per defaut viene visualizzato solo se siamo in interfaccia avanzata
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
		String[] azionis = this.request.getParameterValues(CostantiControlStation.PARAMETRO_AZIONI);
		
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

	public String getMessaggioConfermaModificaRegolaMapping(boolean isDefault,List<String> listaAzioni,ServiceBinding serviceBinding, 
			boolean abilitazione, boolean multiline,boolean listElement) throws DriverConfigurazioneException {
		String pre = Costanti.HTML_MODAL_SPAN_PREFIX;
		String post = Costanti.HTML_MODAL_SPAN_SUFFIX;
		
		if(isDefault) {
			return pre + ( abilitazione ? CostantiControlStation.MESSAGGIO_CONFERMA_ABILITAZIONE_PORTA_DEFAULT : CostantiControlStation.MESSAGGIO_CONFERMA_DISABILITAZIONE_PORTA_DEFAULT)  + post;
		}
		else {
			//return mapping.getNome();
			if(listaAzioni!=null && listaAzioni.size() > 0) {
				StringBuffer sb = new StringBuffer();
				sb.append(post);
				sb.append("<ul class=\"contenutoModal\">");
				for (String string : listaAzioni) {
					sb.append((listElement ? "<li>" : "") + string + (listElement ? "</li>" : ""));
				}
				sb.append("</ul>");
				sb.append(pre);
				return pre + ( abilitazione ? MessageFormat.format(getLabelAzioneConfermaAbilitazione(serviceBinding), sb.toString()) : MessageFormat.format(getLabelAzioneConfermaDisabilitazione(serviceBinding),sb.toString()))  + post;
			}
			else {
				return pre + ( abilitazione ? MessageFormat.format(getLabelAzioneConfermaAbilitazione(serviceBinding), " ??? ") : MessageFormat.format(getLabelAzioneConfermaDisabilitazione(serviceBinding)," ??? "))  + post;
			}
		}
	}
	private String getLabelAzioneConfermaAbilitazione(ServiceBinding serviceBinding) {
		return ServiceBinding.REST.equals(serviceBinding) ? CostantiControlStation.MESSAGGIO_CONFERMA_ABILITAZIONE_PORTA_RISORSE : CostantiControlStation.MESSAGGIO_CONFERMA_ABILITAZIONE_PORTA_AZIONI;
	}
	private String getLabelAzioneConfermaDisabilitazione(ServiceBinding serviceBinding) {
		return ServiceBinding.REST.equals(serviceBinding) ? CostantiControlStation.MESSAGGIO_CONFERMA_DISABILITAZIONE_PORTA_RISORSE : CostantiControlStation.MESSAGGIO_CONFERMA_DISABILITAZIONE_PORTA_AZIONI;
	}
	public String getLabelAzione(ServiceBinding serviceBinding) {
		return ServiceBinding.REST.equals(serviceBinding) ? CostantiControlStation.LABEL_PARAMETRO_RISORSA : CostantiControlStation.LABEL_PARAMETRO_AZIONE;
	}
	public String getLabelAzioni(ServiceBinding serviceBinding) {
		return ServiceBinding.REST.equals(serviceBinding) ? CostantiControlStation.LABEL_PARAMETRO_RISORSE : CostantiControlStation.LABEL_PARAMETRO_AZIONI;
	}
	public String getLabelAzioniDi(ServiceBinding serviceBinding) {
		return ServiceBinding.REST.equals(serviceBinding) ? CostantiControlStation.LABEL_PARAMETRO_RISORSE_CONFIG_DI : CostantiControlStation.LABEL_PARAMETRO_AZIONI_CONFIG_DI;
	}
	public String getLabelAllAzioniRidefiniteTooltip(ServiceBinding serviceBinding) {
		return ServiceBinding.REST.equals(serviceBinding) ? CostantiControlStation.LABEL_PARAMETRO_DEFAULT_ALL_RISORSE_RIDEFINITE_TOOLTIP : CostantiControlStation.LABEL_PARAMETRO_DEFAULT_ALL_AZIONI_RIDEFINITE_TOOLTIP;
	}
	public String getLabelAllAzioniConfigurate(ServiceBinding serviceBinding) {
		return ServiceBinding.REST.equals(serviceBinding) ? CostantiControlStation.LABEL_AGGIUNTA_RISORSE_COMPLETATA : CostantiControlStation.LABEL_AGGIUNTA_AZIONI_COMPLETATA;
	}
	
	public Vector<DataElement> addPorteAzioneToDati(TipoOperazione add, Vector<DataElement> dati, String string,
			String[] azioniDisponibiliList, String[] azioni, ServiceBinding serviceBinding) {
		
		String label = this.getLabelAzioni(serviceBinding);
		
		DataElement de = new DataElement();
		de.setLabel(label);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		// Azione
		de = new DataElement();
		de.setLabel(label);
		de.setValues(azioniDisponibiliList);
		de.setSelezionati(azioni);
		de.setType(DataElementType.MULTI_SELECT);
		de.setName(CostantiControlStation.PARAMETRO_AZIONI);
		de.setRequired(true); 
		dati.addElement(de);
		
		return dati;
	}
	
	// Prepara la lista di azioni delle porte
	public void preparePorteAzioneList(List<String> listaAzioni, String idPorta, Integer parentConfigurazione, List<Parameter> lstParametriBreadcrumbs, 
			String nomePorta, String objectName, List<Parameter> listaParametriSessione,
			String labelPerPorta, ServiceBinding serviceBinding) throws Exception {
		try {
			ServletUtils.addListElementIntoSession(this.session, objectName,listaParametriSessione);

			// setto la barra del titolo

			String label = this.getLabelAzione(serviceBinding);
			
			this.pd.setSearchLabel(label);
			this.pd.setSearchDescription("");
			
			lstParametriBreadcrumbs.add(new Parameter(labelPerPorta,null));

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParametriBreadcrumbs.toArray(new Parameter[lstParametriBreadcrumbs.size()]));

			// setto le label delle colonne
			String[] labels = { label };
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
	
	public void addFilterStatoAccordo(String statoAccordo, boolean postBack) throws Exception{
		try {
			String [] stati = StatiAccordo.toArray();
			String [] statiLabel = StatiAccordo.toLabel();
			String [] values = new String[stati.length + 1];
			String [] labels = new String[stati.length + 1];
			labels[0] = CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_QUALSIASI;
			for (int i =0; i < stati.length ; i ++) {
				labels[i+1] = statiLabel[i];
				values[i+1] = stati[i];
			}
			
			String selectedValue = statoAccordo != null ? statoAccordo : CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_QUALSIASI;
			
			String label = CostantiControlStation.LABEL_PARAMETRO_STATO_PACKAGE;
			
			this.pd.addFilter(Filtri.FILTRO_STATO_ACCORDO, label, selectedValue, values, labels, postBack, this.getSize());
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void addFilterHttpMethod(String httpMethod, boolean postBack) throws Exception{
		try {
			String [] metodi = org.openspcoop2.core.registry.constants.HttpMethod.toArray();
			String [] metodiLabel = metodi;
			String [] values = new String[metodi.length + 1];
			String [] labels = new String[metodi.length + 1];
			labels[0] = CostantiControlStation.LABEL_PARAMETRO_HTTP_METHOD_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_HTTP_METHOD_QUALSIASI;
			for (int i =0; i < metodi.length ; i ++) {
				labels[i+1] = metodiLabel[i];
				values[i+1] = metodi[i];
			}
			
			String selectedValue = httpMethod != null ? httpMethod : CostantiControlStation.DEFAULT_VALUE_PARAMETRO_HTTP_METHOD_QUALSIASI;
			
			String label = CostantiControlStation.LABEL_PARAMETRO_HTTP_METHOD;
			
			this.pd.addFilter(Filtri.FILTRO_HTTP_METHOD, label, selectedValue, values, labels, postBack, this.getSize());
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void addFilterRuoloTipologia(String ruoloTipologia, boolean postBack) throws Exception{
		try {
			String [] metodi = new String[2];
			metodi[0] = RuoloTipologia.INTERNO.getValue();
			metodi[1] = RuoloTipologia.ESTERNO.getValue();
			String [] metodiLabel = new String[2];
			metodiLabel[0] = CostantiControlStation.RUOLI_TIPOLOGIA_LABEL_INTERNO;
			metodiLabel[1] = CostantiControlStation.RUOLI_TIPOLOGIA_LABEL_ESTERNO;
			String [] values = new String[metodi.length + 1];
			String [] labels = new String[metodi.length + 1];
			labels[0] = CostantiControlStation.LABEL_PARAMETRO_RUOLO_TIPOLOGIA_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_RUOLO_TIPOLOGIA_QUALSIASI;
			for (int i =0; i < metodi.length ; i ++) {
				labels[i+1] = metodiLabel[i];
				values[i+1] = metodi[i];
			}
			
			String selectedValue = ruoloTipologia != null ? ruoloTipologia : CostantiControlStation.DEFAULT_VALUE_PARAMETRO_RUOLO_TIPOLOGIA_QUALSIASI;
			
			String label = CostantiControlStation.LABEL_PARAMETRO_RUOLO_TIPOLOGIA;
			
			this.pd.addFilter(Filtri.FILTRO_RUOLO_TIPOLOGIA, label, selectedValue, values, labels, postBack, this.getSize());
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void addFilterRuoloContesto(String ruoloContesto, boolean postBack) throws Exception{
		try {
			String [] metodi = new String[2];
			metodi[0] = RuoloContesto.PORTA_APPLICATIVA.getValue();
			metodi[1] = RuoloContesto.PORTA_DELEGATA.getValue();
			String [] metodiLabel = new String[2];
			metodiLabel[0] = CostantiControlStation.RUOLI_CONTESTO_UTILIZZO_LABEL_EROGAZIONE;
			metodiLabel[1] = CostantiControlStation.RUOLI_CONTESTO_UTILIZZO_LABEL_FRUIZIONE;
			String [] values = new String[metodi.length + 1];
			String [] labels = new String[metodi.length + 1];
			labels[0] = CostantiControlStation.LABEL_PARAMETRO_RUOLO_CONTESTO_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_RUOLO_CONTESTO_QUALSIASI;
			for (int i =0; i < metodi.length ; i ++) {
				labels[i+1] = metodiLabel[i];
				values[i+1] = metodi[i];
			}
			
			String selectedValue = ruoloContesto != null ? ruoloContesto : CostantiControlStation.DEFAULT_VALUE_PARAMETRO_RUOLO_CONTESTO_QUALSIASI;
			
			String label = CostantiControlStation.LABEL_PARAMETRO_RUOLO_CONTESTO;
			
			this.pd.addFilter(Filtri.FILTRO_RUOLO_CONTESTO, label, selectedValue, values, labels, postBack, this.getSize());
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void addFilterDominio(String dominio, boolean postBack) throws Exception{
		try {
			
			String [] values = new String[SoggettiCostanti.SOGGETTI_DOMINI_VALUE.length + 1];
			String [] labels = new String[SoggettiCostanti.SOGGETTI_DOMINI_VALUE.length + 1];
			labels[0] = SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_DOMINIO_QUALSIASI;
			values[0] = SoggettiCostanti.DEFAULT_VALUE_PARAMETRO_SOGGETTO_DOMINIO_QUALSIASI;
			for (int i =0; i < SoggettiCostanti.SOGGETTI_DOMINI_VALUE.length ; i ++) {
				labels[i+1] = SoggettiCostanti.SOGGETTI_DOMINI_LABEL[i];
				values[i+1] = SoggettiCostanti.SOGGETTI_DOMINI_VALUE[i];
			}
			
			String selectedValue = dominio != null ? dominio : CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_QUALSIASI;
			
			this.pd.addFilter(Filtri.FILTRO_DOMINIO, SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_DOMINIO, selectedValue, values, labels, postBack, this.getSize());
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}

	}
	
	public void setFilterRuoloServizioApplicativo(ISearch ricerca, int idLista) throws Exception{
		if( (this.isModalitaCompleta()==false) && 
				(Liste.SERVIZIO_APPLICATIVO==idLista || Liste.SERVIZI_APPLICATIVI_BY_SOGGETTO==idLista)) {
			ricerca.addFilter(idLista, Filtri.FILTRO_RUOLO_SERVIZIO_APPLICATIVO, Filtri.VALUE_FILTRO_RUOLO_SERVIZIO_APPLICATIVO_FRUITORE);
		}
	}
	
	public void setFilterSelectedProtocol(ISearch ricerca, int idLista) throws Exception{
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
		return _getLabelProtocollo(protocollo);
	}
		
	public static String _getLabelProtocollo(String protocollo) throws Exception{
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
		StringBuffer bf = new StringBuffer();
		ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
		if(protocolFactoryManager.getOrganizationTypes().get(protocollo).size()>1) {
			IProtocolFactory<?> protocolFactory = protocolFactoryManager.getProtocolFactoryByName(protocollo);
			if(tipoSoggetto.equals(protocolFactory.createProtocolConfiguration().getTipoSoggettoDefault())) {
				bf.append(nomeSoggetto);
			}
			else{
				bf.append(tipoSoggetto).append("/").append(nomeSoggetto);
			}
		}
		else {
			bf.append(nomeSoggetto);
		}
		return bf.toString();
	}
	public String getLabelIdAccordo(AccordoServizioParteComune as) throws Exception{
		return this.getLabelIdAccordo(this.soggettiCore.getProtocolloAssociatoTipoSoggetto(as.getSoggettoReferente().getTipo()), 
				this.idAccordoFactory.getIDAccordoFromAccordo(as));
	}
	public String getLabelIdAccordo(String protocollo, IDAccordo idAccordo) throws Exception{
		StringBuffer bf = new StringBuffer();
		bf.append(idAccordo.getNome());
		bf.append(":");
		bf.append(idAccordo.getVersione());
		if(this.apcCore.isSupportatoSoggettoReferente(protocollo)) {
			if(idAccordo.getSoggettoReferente()!=null){
				bf.append(" (");
				bf.append(this.getLabelNomeSoggetto(protocollo, idAccordo.getSoggettoReferente().getTipo(), idAccordo.getSoggettoReferente().getNome()));
				bf.append(")");
			}
		}
		return bf.toString();
	}
	public String getLabelNomeServizio(String protocollo, String tipoServizio, String nomeServizio, Integer versioneInt) throws Exception{
		
		String versione = "";
		if(ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo).createProtocolConfiguration().isSupportoVersionamentoAccordiParteSpecifica()) {
			versione = ":"+versioneInt;
		}
		
		ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
		if(protocolFactoryManager._getServiceTypes().get(protocollo).size()>1) {
			IProtocolFactory<?> protocolFactory = protocolFactoryManager.getProtocolFactoryByName(protocollo);
			if(tipoServizio.equals(protocolFactory.createProtocolConfiguration().getTipoServizioDefault(null))) {
				return nomeServizio+versione;
			}
			else {
				return tipoServizio+"/"+nomeServizio+versione;	
			}
		}
		else {
			return nomeServizio+versione;
		}
	}
	public String getLabelIdServizio(AccordoServizioParteSpecifica as) throws Exception{
		return this.getLabelIdServizio(
				this.soggettiCore.getProtocolloAssociatoTipoSoggetto(as.getTipoSoggettoErogatore()), 
				this.idServizioFactory.getIDServizioFromAccordo(as));
	}
	public String getLabelIdServizio(String protocollo, IDServizio idServizio) throws Exception{
		StringBuffer bf = new StringBuffer();
		bf.append(this.getLabelNomeServizio(protocollo, idServizio.getTipo(), idServizio.getNome(), idServizio.getVersione()));
		bf.append(" (");
		bf.append(this.getLabelNomeSoggetto(protocollo, idServizio.getSoggettoErogatore().getTipo(), idServizio.getSoggettoErogatore().getNome()));
		bf.append(")");
		return bf.toString();
	}
	public String getLabelIdAccordoCooperazione(AccordoCooperazione ac) throws Exception{
		return this.getLabelIdAccordoCooperazione(this.soggettiCore.getProtocolloAssociatoTipoSoggetto(ac.getSoggettoReferente().getTipo()), 
				this.idAccordoCooperazioneFactory.getIDAccordoFromAccordo(ac));
	}
	public String getLabelIdAccordoCooperazione(String protocollo, IDAccordoCooperazione idAccordo) throws Exception{
		StringBuffer bf = new StringBuffer();
		bf.append(idAccordo.getNome());
		bf.append(":");
		bf.append(idAccordo.getVersione());
		//if(this.apcCore.isSupportatoSoggettoReferente(protocollo)) {
		if(idAccordo.getSoggettoReferente()!=null){
			bf.append(" (");
			bf.append(this.getLabelNomeSoggetto(protocollo, idAccordo.getSoggettoReferente().getTipo(), idAccordo.getSoggettoReferente().getNome()));
			bf.append(")");
		}
		//}
		return bf.toString();
	}
	
	// Validazione contenuti
	
	public void validazioneContenuti(TipoOperazione tipoOperazione,Vector<DataElement> dati, boolean isPortaDelegata, String xsd, String tipoValidazione, String applicaMTOM,
			ServiceBinding serviceBinding, FormatoSpecifica formatoSpecifica) throws Exception{
		validazioneContenuti(tipoOperazione, dati, true,isPortaDelegata,xsd,tipoValidazione,applicaMTOM, serviceBinding, formatoSpecifica);
	}
	
	public void validazioneContenuti(TipoOperazione tipoOperazione,Vector<DataElement> dati, boolean addSezione,boolean isPortaDelegata, String xsd, String tipoValidazione, String applicaMTOM,
			ServiceBinding serviceBinding, FormatoSpecifica formatoSpecifica) {
		DataElement de = new DataElement();
		
		if(addSezione) {
			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI);
			dati.addElement(de);
		}
		
		String[] tipoXsd = { CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_ABILITATO,
				CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_DISABILITATO,
				CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_WARNING_ONLY };
		
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_STATO);
		de.setType(DataElementType.SELECT);
		de.setName(CostantiControlStation.PARAMETRO_PORTE_XSD);
		de.setValues(tipoXsd);
		//		de.setOnChange("CambiaMode('" + tipoOp + "')");
		de.setPostBack(true);
		de.setSelected(xsd);
		dati.addElement(de);
		
		if (CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_ABILITATO.equals(xsd) ||
				CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_WARNING_ONLY.equals(xsd)) {
			
			List<String> tipiValidazione = new ArrayList<>();
			tipiValidazione.add(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_TIPO_VALIDAZIONE_INTERFACE);
			tipiValidazione.add(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_TIPO_VALIDAZIONE_XSD);
			if(!this.isModalitaStandard()) {
				tipiValidazione.add(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_TIPO_VALIDAZIONE_OPENSPCOOP);
			}
			
			List<String> labelTipiValidazione = new ArrayList<>();
			switch (formatoSpecifica) {
			case OPEN_API_3:
				labelTipiValidazione.add(CostantiControlStation.LABEL_PARAMETRO_INTERFACE_TYPE_OPEN_API_3);
				break;
			case SWAGGER_2:
				labelTipiValidazione.add(CostantiControlStation.LABEL_PARAMETRO_INTERFACE_TYPE_SWAGGER_2);
				break;
			case WADL:
				labelTipiValidazione.add(CostantiControlStation.LABEL_PARAMETRO_INTERFACE_TYPE_WADL);
				break;
			case WSDL_11:
				labelTipiValidazione.add(CostantiControlStation.LABEL_PARAMETRO_INTERFACE_TYPE_WSDL_11);
				break;
			}
			labelTipiValidazione.add(CostantiControlStation.LABEL_PARAMETRO_SCHEMI_XSD);
			if(!this.isModalitaStandard()) {
				labelTipiValidazione.add(CostantiControlStation.LABEL_PARAMETRO_REGISTRO_OPENSPCOOP);
			}
			
			//String[] tipi_validazione = { "xsd", "wsdl" };
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_TIPO);
			de.setName(CostantiControlStation.PARAMETRO_PORTE_TIPO_VALIDAZIONE);
			if(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_TIPO_VALIDAZIONE_OPENSPCOOP.equals(tipoValidazione) && this.isModalitaStandard()) {
				de.setType(DataElementType.HIDDEN);
				de.setValue(tipoValidazione);
			}
			else {
				de.setType(DataElementType.SELECT);
				de.setValues(tipiValidazione);
				de.setLabels(labelTipiValidazione);
				de.setSelected(tipoValidazione);
			}
			dati.addElement(de);
			
			if(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_TIPO_VALIDAZIONE_OPENSPCOOP.equals(tipoValidazione) && this.isModalitaStandard()) {
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_TIPO);
				de.setName(CostantiControlStation.PARAMETRO_PORTE_TIPO_VALIDAZIONE+"__LABEL");
				de.setType(DataElementType.TEXT);
				de.setValue(CostantiControlStation.LABEL_PARAMETRO_REGISTRO_OPENSPCOOP);
				dati.addElement(de);
			}
			
			
			// Applica MTOM 
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_ACCETTA_MTOM);
			if(ServiceBinding.SOAP.equals(serviceBinding)) {
				de.setType(DataElementType.CHECKBOX);
				if( ServletUtils.isCheckBoxEnabled(applicaMTOM) || CostantiRegistroServizi.ABILITATO.equals(applicaMTOM) ){
					de.setSelected(true);
				}
			}
			else{
				de.setType(DataElementType.HIDDEN);
				de.setValue(applicaMTOM);
			}		 
			de.setName(CostantiControlStation.PARAMETRO_PORTE_APPLICA_MTOM);
			dati.addElement(de);
		}
	}
	
	public boolean validazioneContenutiCheck(TipoOperazione tipoOperazione,boolean isPortaDelegata) throws Exception {
		String xsd = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_XSD);
		
		// Controllo che i campi "select" abbiano uno dei valori ammessi
		if (!xsd.equals(CostantiControlStation.DEFAULT_VALUE_ABILITATO) && !xsd.equals(CostantiControlStation.DEFAULT_VALUE_DISABILITATO) && !xsd.equals(CostantiControlStation.DEFAULT_VALUE_WARNING_ONLY)) {
			this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_VALIDAZIONE_XSD_DEV_ESSERE_ABILITATO_DISABILITATO_O_WARNING_ONLY);
			return false;
		}
		
		return true;
	}
	
	public void addFilterAzione(List<String> azioni, String azione, ServiceBinding serviceBinding) throws Exception{
		String [] azioniS = azioni != null ?  azioni.toArray(new String[azioni.size()]) : new String [0];
		this.addFilterAzione(azioniS, azione, serviceBinding);		  
	}
	
	public void addFilterAzione(String []azioni, String azione, ServiceBinding serviceBinding) throws Exception{
		try {
			String [] values = new String[azioni.length + 1];
			String [] labels = new String[azioni.length + 1];
			labels[0] = CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_AZIONE_NON_SELEZIONATA;
			for (int i =0; i < azioni.length ; i ++) {
				labels[i+1] = azioni[i];
				values[i+1] = azioni[i];
			}
			
			String selectedValue = StringUtils.isNotEmpty(azione) ? azione : CostantiControlStation.DEFAULT_VALUE_AZIONE_NON_SELEZIONATA;
			
			this.pd.addFilter(Filtri.FILTRO_AZIONE, 
					this.getLabelAzione(serviceBinding),
					selectedValue, values, labels, false, this.getSize());
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	
	public void addConfigurazioneDumpToDati(TipoOperazione tipoOperazione,Vector<DataElement> dati, boolean showStato, String statoDump, boolean showRealtime, String realtime, String statoDumpRichiesta, String statoDumpRisposta,
			String dumpRichiestaIngressoHeader, String dumpRichiestaIngressoBody, String dumpRichiestaIngressoAttachments, 
			String dumpRichiestaUscitaHeader, String dumpRichiestaUscitaBody, String dumpRichiestaUscitaAttachments, 
			String dumpRispostaIngressoHeader, String dumpRispostaIngressoBody , String dumpRispostaIngressoAttachments,
			String dumpRispostaUscitaHeader, String dumpRispostaUscitaBody, String dumpRispostaUscitaAttachments) throws Exception{
		
		if(showStato || showRealtime) {
			DataElement de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_GENERALE);
			dati.addElement(de);
		}
			
		String valuesProp [] = {StatoFunzionalita.ABILITATO.getValue(), StatoFunzionalita.DISABILITATO.getValue()};
		String labelsProp [] = {CostantiControlStation.DEFAULT_VALUE_ABILITATO, CostantiControlStation.DEFAULT_VALUE_DISABILITATO};
		
		// stato generale dump
		DataElement de = new DataElement();
		de.setName(CostantiControlStation.PARAMETRO_DUMP_STATO); 
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO);
		if(showStato) {
			de.setType(DataElementType.SELECT);
			String valuesStato [] = {CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT, CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO};
			String labelsStato [] = {CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO_DEFAULT, CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO_RIDEFINITO};
			de.setSelected(statoDump);
			de.setLabels(labelsStato);
			de.setValues(valuesStato); 
			de.setPostBack(true);
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(statoDump);
		}
		dati.addElement(de);
		
		if(!showStato || statoDump.equals(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO)) {
		
			// Realtime
			de = new DataElement();
			de.setName(CostantiControlStation.PARAMETRO_DUMP_REALTIME); 
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_REALTIME);
			if(showRealtime) {
				de.setType(DataElementType.SELECT);
				de.setSelected(realtime);
				de.setLabels(labelsProp);
				de.setValues(valuesProp);
			} else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(realtime);
			}
			dati.addElement(de);
			
			// Sezione Richiesta
			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_RICHIESTA);
			dati.addElement(de);

			// Stato Dump Richiesta
			de = new DataElement();
			de.setName(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_STATO); 
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_STATO);
			de.setType(DataElementType.SELECT);
			de.setSelected(statoDumpRichiesta);
			de.setLabels(labelsProp);
			de.setValues(valuesProp);
			de.setPostBack(true);
			dati.addElement(de);
			
			if(statoDumpRichiesta.equals(StatoFunzionalita.ABILITATO.getValue())) {
				// sotto sezione ingresso
				de = new DataElement();
				de.setType(DataElementType.SUBTITLE);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_INGRESSO);
				dati.addElement(de);
				
				// header ingresso
				de = new DataElement();
				de.setName(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_HEADERS);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_HEADERS);
				de.setType(DataElementType.SELECT);
				de.setSelected(dumpRichiestaIngressoHeader);
				de.setLabels(labelsProp);
				de.setValues(valuesProp);
				dati.addElement(de);
				
				// body ingresso
				de = new DataElement();
				de.setName(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_BODY);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_BODY);
				de.setType(DataElementType.SELECT);
				de.setSelected(dumpRichiestaIngressoBody);
				de.setLabels(labelsProp);
				de.setValues(valuesProp);
				dati.addElement(de);
				
				// attachments ingresso
				de = new DataElement();
				de.setName(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_ATTACHMENTS);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_ATTACHMENTS);
				de.setType(DataElementType.SELECT);
				de.setSelected(dumpRichiestaIngressoAttachments);
				de.setLabels(labelsProp);
				de.setValues(valuesProp);
				dati.addElement(de);
				
				// sotto sezione uscita
				de = new DataElement();
				de.setType(DataElementType.SUBTITLE);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_USCITA);
				dati.addElement(de);
				
				// header ingresso
				de = new DataElement();
				de.setName(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_HEADERS);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_USCITA_HEADERS);
				de.setType(DataElementType.SELECT);
				de.setSelected(dumpRichiestaUscitaHeader);
				de.setLabels(labelsProp);
				de.setValues(valuesProp);
				dati.addElement(de);
				
				// body ingresso
				de = new DataElement();
				de.setName(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_BODY);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_USCITA_BODY);
				de.setType(DataElementType.SELECT);
				de.setSelected(dumpRichiestaUscitaBody); 
				de.setLabels(labelsProp);
				de.setValues(valuesProp);
				dati.addElement(de);
				
				// attachments ingresso
				de = new DataElement();
				de.setName(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_ATTACHMENTS);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_USCITA_ATTACHMENTS);
				de.setType(DataElementType.SELECT);
				de.setSelected(dumpRichiestaUscitaAttachments);
				de.setLabels(labelsProp);
				de.setValues(valuesProp);
				dati.addElement(de);

			}
			
			
			// Sezione Risposta
			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_RISPOSTA);
			dati.addElement(de);

			// Stato Dump Richiesta
			de = new DataElement();
			de.setName(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_STATO); 
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_STATO);
			de.setType(DataElementType.SELECT);
			de.setSelected(statoDumpRisposta);
			de.setLabels(labelsProp);
			de.setValues(valuesProp);
			de.setPostBack(true);	
			dati.addElement(de);
			
			if(statoDumpRisposta.equals(StatoFunzionalita.ABILITATO.getValue())) {
				// sotto sezione ingresso
				de = new DataElement();
				de.setType(DataElementType.SUBTITLE);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_INGRESSO);
				dati.addElement(de);
				
				// header ingresso
				de = new DataElement();
				de.setName(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_HEADERS);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_INGRESSO_HEADERS);
				de.setType(DataElementType.SELECT);
				de.setSelected(dumpRispostaIngressoHeader);
				de.setLabels(labelsProp);
				de.setValues(valuesProp);
				dati.addElement(de);
				
				// body ingresso
				de = new DataElement();
				de.setName(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_BODY);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_INGRESSO_BODY);
				de.setType(DataElementType.SELECT);
				de.setSelected(dumpRispostaIngressoBody);
				de.setLabels(labelsProp);
				de.setValues(valuesProp);
				dati.addElement(de);
				
				// attachments ingresso
				de = new DataElement();
				de.setName(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_ATTACHMENTS);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_INGRESSO_ATTACHMENTS);
				de.setType(DataElementType.SELECT);
				de.setSelected(dumpRispostaIngressoAttachments);
				de.setLabels(labelsProp);
				de.setValues(valuesProp);
				dati.addElement(de);
				
				// sotto sezione uscita
				de = new DataElement();
				de.setType(DataElementType.SUBTITLE);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_USCITA);
				dati.addElement(de);
				
				// header ingresso
				de = new DataElement();
				de.setName(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_HEADERS);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_USCITA_HEADERS);
				de.setType(DataElementType.SELECT);
				de.setSelected(dumpRispostaUscitaHeader);
				de.setLabels(labelsProp);
				de.setValues(valuesProp);
				dati.addElement(de);
				
				// body ingresso
				de = new DataElement();
				de.setName(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_BODY);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_USCITA_BODY);
				de.setType(DataElementType.SELECT);
				de.setSelected(dumpRispostaUscitaBody); 
				de.setLabels(labelsProp);
				de.setValues(valuesProp);
				dati.addElement(de);
				
				// attachments ingresso
				de = new DataElement();
				de.setName(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_ATTACHMENTS);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_USCITA_ATTACHMENTS);
				de.setType(DataElementType.SELECT);
				de.setSelected(dumpRispostaUscitaAttachments); 
				de.setLabels(labelsProp);
				de.setValues(valuesProp);
				dati.addElement(de);

			}
		}
	}
	
	public boolean checkDataConfigurazioneDump(TipoOperazione tipoOperazione,boolean showStato, String statoDump, boolean showRealtime, String realtime, String statoDumpRichiesta, String statoDumpRisposta,
			String dumpRichiestaIngressoHeader, String dumpRichiestaIngressoBody, String dumpRichiestaIngressoAttachments, 
			String dumpRichiestaUscitaHeader, String dumpRichiestaUscitaBody, String dumpRichiestaUscitaAttachments, 
			String dumpRispostaIngressoHeader, String dumpRispostaIngressoBody , String dumpRispostaIngressoAttachments,
			String dumpRispostaUscitaHeader, String dumpRispostaUscitaBody, String dumpRispostaUscitaAttachments) throws Exception{
		
		if(showStato) {
			if(StringUtils.isEmpty(statoDump) || !(statoDump.equals(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT) || statoDump.equals(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO))) {
				this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_NON_VALIDO, CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO));
				return false;
			}
		}
		
		if(!showStato || statoDump.equals(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO)) {
			// realtime
			if(StringUtils.isEmpty(realtime) || !(realtime.equals(StatoFunzionalita.ABILITATO.getValue()) || realtime.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
				this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_NON_VALIDO, CostantiControlStation.LABEL_PARAMETRO_DUMP_REALTIME));
				return false;
			}
			
			// statoDumpRichiesta
			if(StringUtils.isEmpty(statoDumpRichiesta) || !(statoDumpRichiesta.equals(StatoFunzionalita.ABILITATO.getValue()) || statoDumpRichiesta.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
				this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_DELLA_YY_NON_VALIDO, 
						CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO, CostantiControlStation.LABEL_PARAMETRO_RICHIESTA));
				return false;
			}
			
			if(statoDumpRichiesta.equals(StatoFunzionalita.ABILITATO.getValue())){
				// dumpRichiestaIngressoHeader
				if(StringUtils.isEmpty(dumpRichiestaIngressoHeader) || !(dumpRichiestaIngressoHeader.equals(StatoFunzionalita.ABILITATO.getValue()) || dumpRichiestaIngressoHeader.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_YY_DELLA_ZZ_NON_VALIDO, 
							CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_HEADERS, CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_INGRESSO, CostantiControlStation.LABEL_PARAMETRO_RICHIESTA));
					return false;
				}
				
				// dumpRichiestaIngressoBody
				if(StringUtils.isEmpty(dumpRichiestaIngressoBody) || !(dumpRichiestaIngressoBody.equals(StatoFunzionalita.ABILITATO.getValue()) || dumpRichiestaIngressoBody.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_YY_DELLA_ZZ_NON_VALIDO, 
							CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_BODY, CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_INGRESSO, CostantiControlStation.LABEL_PARAMETRO_RICHIESTA));
					return false;
				}
				
				// dumpRichiestaIngressoAttachments
				if(StringUtils.isEmpty(dumpRichiestaIngressoAttachments) || !(dumpRichiestaIngressoAttachments.equals(StatoFunzionalita.ABILITATO.getValue()) || dumpRichiestaIngressoAttachments.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_YY_DELLA_ZZ_NON_VALIDO, 
							CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_ATTACHMENTS, CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_INGRESSO, CostantiControlStation.LABEL_PARAMETRO_RICHIESTA));
					return false;
				}
				
				// dumpRichiestaUscitaHeader
				if(StringUtils.isEmpty(dumpRichiestaUscitaHeader) || !(dumpRichiestaUscitaHeader.equals(StatoFunzionalita.ABILITATO.getValue()) || dumpRichiestaUscitaHeader.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_YY_DELLA_ZZ_NON_VALIDO, 
							CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_HEADERS, CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_USCITA, CostantiControlStation.LABEL_PARAMETRO_RICHIESTA));
					return false;
				}
				
				// dumpRichiestaUscitaBody
				if(StringUtils.isEmpty(dumpRichiestaUscitaBody) || !(dumpRichiestaUscitaBody.equals(StatoFunzionalita.ABILITATO.getValue()) || dumpRichiestaUscitaBody.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_YY_DELLA_ZZ_NON_VALIDO, 
							CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_BODY, CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_USCITA, CostantiControlStation.LABEL_PARAMETRO_RICHIESTA));
					return false;
				}
				
				// dumpRichiestaUscitaAttachments
				if(StringUtils.isEmpty(dumpRichiestaUscitaAttachments) || !(dumpRichiestaUscitaAttachments.equals(StatoFunzionalita.ABILITATO.getValue()) || dumpRichiestaUscitaAttachments.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_YY_DELLA_ZZ_NON_VALIDO, 
							CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_ATTACHMENTS, CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_USCITA, CostantiControlStation.LABEL_PARAMETRO_RICHIESTA));
					return false;
				}
			}
			
			// statoDumpRisposta
			if(StringUtils.isEmpty(statoDumpRisposta) || !(statoDumpRisposta.equals(StatoFunzionalita.ABILITATO.getValue()) || statoDumpRisposta.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
				this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_DELLA_YY_NON_VALIDO, 
						CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO, CostantiControlStation.LABEL_PARAMETRO_RISPOSTA));
				return false;
			}
			
			if(statoDumpRisposta.equals(StatoFunzionalita.ABILITATO.getValue())) {
				// dumpRispostaIngressoHeader
				if(StringUtils.isEmpty(dumpRispostaIngressoHeader) || !(dumpRispostaIngressoHeader.equals(StatoFunzionalita.ABILITATO.getValue()) || dumpRispostaIngressoHeader.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_YY_DELLA_ZZ_NON_VALIDO, 
							CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_HEADERS, CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_INGRESSO, CostantiControlStation.LABEL_PARAMETRO_RISPOSTA));
					return false;
				}
				
				// dumpRispostaIngressoBody
				if(StringUtils.isEmpty(dumpRispostaIngressoBody) || !(dumpRispostaIngressoBody.equals(StatoFunzionalita.ABILITATO.getValue()) || dumpRispostaIngressoBody.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_YY_DELLA_ZZ_NON_VALIDO, 
							CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_BODY, CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_INGRESSO, CostantiControlStation.LABEL_PARAMETRO_RISPOSTA));
					return false;
				}
				
				// dumpRispostaIngressoAttachments
				if(StringUtils.isEmpty(dumpRispostaIngressoAttachments) || !(dumpRispostaIngressoAttachments.equals(StatoFunzionalita.ABILITATO.getValue()) || dumpRispostaIngressoAttachments.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_YY_DELLA_ZZ_NON_VALIDO, 
							CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_ATTACHMENTS, CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_INGRESSO, CostantiControlStation.LABEL_PARAMETRO_RISPOSTA));
					return false;
				}
				
				// dumpRispostaUscitaHeader
				if(StringUtils.isEmpty(dumpRispostaUscitaHeader) || !(dumpRispostaUscitaHeader.equals(StatoFunzionalita.ABILITATO.getValue()) || dumpRispostaUscitaHeader.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_YY_DELLA_ZZ_NON_VALIDO, 
							CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_HEADERS, CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_USCITA, CostantiControlStation.LABEL_PARAMETRO_RISPOSTA));
					return false;
				}
				
				// dumpRispostaUscitaBody
				if(StringUtils.isEmpty(dumpRispostaUscitaBody) || !(dumpRispostaUscitaBody.equals(StatoFunzionalita.ABILITATO.getValue()) || dumpRispostaUscitaBody.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_YY_DELLA_ZZ_NON_VALIDO, 
							CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_BODY, CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_USCITA, CostantiControlStation.LABEL_PARAMETRO_RISPOSTA));
					return false;
				}
				
				// dumpRispostaUscitaAttachments
				if(StringUtils.isEmpty(dumpRispostaUscitaAttachments) || !(dumpRispostaUscitaAttachments.equals(StatoFunzionalita.ABILITATO.getValue()) || dumpRispostaUscitaAttachments.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_YY_DELLA_ZZ_NON_VALIDO, 
							CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_ATTACHMENTS, CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_USCITA, CostantiControlStation.LABEL_PARAMETRO_RISPOSTA));
					return false;
				}
			}
		}
		
		
		return true;
	}
	
	public DumpConfigurazione getConfigurazioneDump(TipoOperazione tipoOperazione,boolean showStato, String statoDump, boolean showRealtime, String realtime, String statoDumpRichiesta, String statoDumpRisposta,
			String dumpRichiestaIngressoHeader, String dumpRichiestaIngressoBody, String dumpRichiestaIngressoAttachments, 
			String dumpRichiestaUscitaHeader, String dumpRichiestaUscitaBody, String dumpRichiestaUscitaAttachments, 
			String dumpRispostaIngressoHeader, String dumpRispostaIngressoBody , String dumpRispostaIngressoAttachments,
			String dumpRispostaUscitaHeader, String dumpRispostaUscitaBody, String dumpRispostaUscitaAttachments) throws Exception{
		
		DumpConfigurazione newConfigurazione = null;
		
		if(showStato) {
			// impostazioni dump di default impostate nelle PD/PA
			if(statoDump.equals(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT))
				return newConfigurazione;
		}
		
		if(!showStato || statoDump.equals(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO)) {
			
			newConfigurazione = new DumpConfigurazione();
			newConfigurazione.setRichiestaIngresso(new DumpConfigurazioneRegola());
			newConfigurazione.setRichiestaUscita(new DumpConfigurazioneRegola());
			newConfigurazione.setRispostaIngresso(new DumpConfigurazioneRegola());
			newConfigurazione.setRispostaUscita(new DumpConfigurazioneRegola());
			
			// realtime			
			newConfigurazione.set_value_realtime(realtime);

			if(statoDumpRichiesta.equals(StatoFunzionalita.ABILITATO.getValue())) {
				newConfigurazione.getRichiestaIngresso().set_value_headers(dumpRichiestaIngressoHeader);
				newConfigurazione.getRichiestaIngresso().set_value_body(dumpRichiestaIngressoBody);
				newConfigurazione.getRichiestaIngresso().set_value_attachments(dumpRichiestaIngressoAttachments);
				newConfigurazione.getRichiestaUscita().set_value_headers(dumpRichiestaUscitaHeader);
				newConfigurazione.getRichiestaUscita().set_value_body(dumpRichiestaUscitaBody);
				newConfigurazione.getRichiestaUscita().set_value_attachments(dumpRichiestaUscitaAttachments);
			} else {
				newConfigurazione.getRichiestaIngresso().setHeaders(StatoFunzionalita.DISABILITATO);
				newConfigurazione.getRichiestaIngresso().setBody(StatoFunzionalita.DISABILITATO);
				newConfigurazione.getRichiestaIngresso().setAttachments(StatoFunzionalita.DISABILITATO);
				newConfigurazione.getRichiestaUscita().setHeaders(StatoFunzionalita.DISABILITATO);
				newConfigurazione.getRichiestaUscita().setBody(StatoFunzionalita.DISABILITATO);
				newConfigurazione.getRichiestaUscita().setAttachments(StatoFunzionalita.DISABILITATO);
			}
			
			if(statoDumpRisposta.equals(StatoFunzionalita.ABILITATO.getValue())) {
				newConfigurazione.getRispostaIngresso().set_value_headers(dumpRispostaIngressoHeader);
				newConfigurazione.getRispostaIngresso().set_value_body(dumpRispostaIngressoBody);
				newConfigurazione.getRispostaIngresso().set_value_attachments(dumpRispostaIngressoAttachments);
				newConfigurazione.getRispostaUscita().set_value_headers(dumpRispostaUscitaHeader);
				newConfigurazione.getRispostaUscita().set_value_body(dumpRispostaUscitaBody);
				newConfigurazione.getRispostaUscita().set_value_attachments(dumpRispostaUscitaAttachments);
			} else {
				newConfigurazione.getRispostaIngresso().setHeaders(StatoFunzionalita.DISABILITATO);
				newConfigurazione.getRispostaIngresso().setBody(StatoFunzionalita.DISABILITATO);
				newConfigurazione.getRispostaIngresso().setAttachments(StatoFunzionalita.DISABILITATO);
				newConfigurazione.getRispostaUscita().setHeaders(StatoFunzionalita.DISABILITATO);
				newConfigurazione.getRispostaUscita().setBody(StatoFunzionalita.DISABILITATO);
				newConfigurazione.getRispostaUscita().setAttachments(StatoFunzionalita.DISABILITATO);
			}
		}
		
		return newConfigurazione;
	}
	
	public boolean isDumpConfigurazioneAbilitato(DumpConfigurazione configurazione, boolean isRisposta) {
		boolean abilitato = false;
		
		if(isRisposta) {
			DumpConfigurazioneRegola rispostaIngresso = configurazione.getRispostaIngresso();
			
			if(rispostaIngresso != null) {
				if(rispostaIngresso.getHeaders().equals(StatoFunzionalita.ABILITATO))
					return true;
				
				if(rispostaIngresso.getBody().equals(StatoFunzionalita.ABILITATO))
					return true;
				
				if(rispostaIngresso.getAttachments().equals(StatoFunzionalita.ABILITATO))
					return true;
			}
			
			DumpConfigurazioneRegola rispostaUscita = configurazione.getRispostaUscita();
			
			if(rispostaUscita != null) {
				if(rispostaUscita.getHeaders().equals(StatoFunzionalita.ABILITATO))
					return true;
				
				if(rispostaUscita.getBody().equals(StatoFunzionalita.ABILITATO))
					return true;
				
				if(rispostaUscita.getAttachments().equals(StatoFunzionalita.ABILITATO))
					return true;
			}
		} else {
			DumpConfigurazioneRegola richiestaIngresso = configurazione.getRichiestaIngresso();
			
			if(richiestaIngresso != null) {
				if(richiestaIngresso.getHeaders().equals(StatoFunzionalita.ABILITATO))
					return true;
				
				if(richiestaIngresso.getBody().equals(StatoFunzionalita.ABILITATO))
					return true;
				
				if(richiestaIngresso.getAttachments().equals(StatoFunzionalita.ABILITATO))
					return true;
			}
			
			DumpConfigurazioneRegola richiestaUscita = configurazione.getRichiestaUscita();
			
			if(richiestaUscita != null) {
				if(richiestaUscita.getHeaders().equals(StatoFunzionalita.ABILITATO))
					return true;
				
				if(richiestaUscita.getBody().equals(StatoFunzionalita.ABILITATO))
					return true;
				
				if(richiestaUscita.getAttachments().equals(StatoFunzionalita.ABILITATO))
					return true;
			}
		}
		
		return abilitato;
	}
	
	public boolean isDumpConfigurazioneAbilitato(DumpConfigurazione configurazione) {
		return isDumpConfigurazioneAbilitato(configurazione, true) || isDumpConfigurazioneAbilitato(configurazione, false);
	}

	/** Gestione Properties MVC */
	
	public void aggiornaConfigurazioneProperties(ConfigBean configurazione) throws Exception {
		for (String key : configurazione.getListakeys()) {
			configurazione.getItem(key).setValueFromRequest(this.getParameter(key)); 
		}
	}
	
	public Vector<DataElement> addPropertiesConfigToDati(TipoOperazione tipoOperazione, Vector<DataElement> dati, String configName, ConfigBean configurazioneBean) throws Exception {
		DataElement de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PROPERTIES_CONFIG_NAME);
		de.setValue(configName);
		de.setType(DataElementType.HIDDEN);
		de.setName(CostantiControlStation.PARAMETRO_PROPERTIES_CONFIG_NAME);
		dati.addElement(de);
		
		for (BaseItemBean<?> item : configurazioneBean.getListaItem()) {
			dati.addElement(item.toDataElement());
		}
		
		return dati;
	}
}
