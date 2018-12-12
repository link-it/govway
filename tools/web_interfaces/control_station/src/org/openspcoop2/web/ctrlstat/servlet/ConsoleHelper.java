/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
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
import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.CorsConfigurazioneHeaders;
import org.openspcoop2.core.config.CorsConfigurazioneMethods;
import org.openspcoop2.core.config.CorsConfigurazioneOrigin;
import org.openspcoop2.core.config.Dump;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.DumpConfigurazioneRegola;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.MtomProcessor;
import org.openspcoop2.core.config.MtomProcessorFlowParameter;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneGenerale;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneHashGenerator;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.RuoloTipoMatch;
import org.openspcoop2.core.config.constants.ScopeTipoMatch;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.config.constants.TipoGestioneCORS;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mvc.properties.Config;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.core.mvc.properties.utils.ConfigManager;
import org.openspcoop2.core.mvc.properties.utils.PropertiesSourceConfiguration;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.constants.ScopeContesto;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.TipiDocumentoSicurezza;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.FiltroRicercaRuoli;
import org.openspcoop2.core.registry.driver.FiltroRicercaScope;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.token.TokenUtilities;
import org.openspcoop2.pdd.logger.LogLevels;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.protocol.sdk.constants.ConsoleInterfaceType;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemValueType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
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
import org.openspcoop2.protocol.sdk.validator.ValidazioneResult;
import org.openspcoop2.protocol.utils.EsitiConfigUtils;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.crypt.Password;
import org.openspcoop2.utils.mime.MimeMultipart;
import org.openspcoop2.utils.regexp.RegExpException;
import org.openspcoop2.utils.regexp.RegExpNotFoundException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
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
import org.openspcoop2.web.ctrlstat.servlet.apc.api.ApiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti;
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
import org.openspcoop2.web.ctrlstat.servlet.scope.ScopeCore;
import org.openspcoop2.web.ctrlstat.servlet.scope.ScopeCostanti;
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
import org.openspcoop2.web.lib.mvc.properties.exception.UserInputValidationException;
import org.openspcoop2.web.lib.mvc.properties.utils.ReadPropertiesUtilities;
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
	protected ScopeCore scopeCore = null;

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

	/** Soggetto Selezionato */
	public boolean isSoggettoMultitenantSelezionato() {
		return this.core.isMultitenant() && StringUtils.isNotEmpty(this.getSoggettoMultitenantSelezionato());
	}
	public String getSoggettoMultitenantSelezionato() {
		return ServletUtils.getUserFromSession(this.session).getSoggettoSelezionatoPddConsole();
	}
	
	/** Soggetto Selezionato da govwayMonitor */
	public boolean isSoggettoMultitenantSelezionatoConsoleMonitoraggio() {
		return this.core.isMultitenant() && StringUtils.isNotEmpty(this.getSoggettoMultitenantSelezionatoConsoleMonitoraggio());
	}
	public String getSoggettoMultitenantSelezionatoConsoleMonitoraggio() {
		return ServletUtils.getUserFromSession(this.session).getSoggettoSelezionatoPddMonitor();
	}
	
	private boolean errorInit = false;
	private Exception eErrorInit;
	
	public ConsoleHelper(HttpServletRequest request, PageData pd, HttpSession session) {
		ControlStationCore core = null;
		try {
			core = new ControlStationCore();
		} catch (Exception e) {
			this.log.error("Exception ctrlstatHelper: " + e.getMessage(), e);
			this.errorInit = true;
			this.eErrorInit = e;
		}
		this.init(core, request, pd, session);
	}
	public ConsoleHelper(ControlStationCore core, HttpServletRequest request, PageData pd, HttpSession session) {
		this.init(core, request, pd, session);
	}
	private void init(ControlStationCore core, HttpServletRequest request, PageData pd, HttpSession session) {
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
			
			this.core = core;
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
			this.scopeCore = new ScopeCore(this.core);
			
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
			
			try {
				this.size = ConsoleProperties.getInstance().getConsoleLunghezzaLabel();
			}catch(Exception e) {
				this.size = 50;
			}
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
	
	public boolean isShowGestioneWorkflowStatoDocumenti() {
		return this.core.isShowGestioneWorkflowStatoDocumenti(this);
	}
	
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
			boolean isModalitaCompleta = this.isModalitaCompleta();
			
			List<IExtendedMenu> extendedMenu = this.core.getExtendedMenu();

			Vector<MenuEntry> menu = new Vector<MenuEntry>();

			boolean showAccordiCooperazione = pu.isAccordiCooperazione() && this.core.isAccordiCooperazioneEnabled();
			
			if(pu.isServizi() || showAccordiCooperazione){
				Boolean serviziVisualizzaModalitaElenco = ConsoleProperties.getInstance().isEnableServiziVisualizzaModalitaElenco();
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
						// ASPC 
						totEntries ++;
						
						if(isModalitaCompleta) {
							totEntries +=1;
						} else {
							// ASPS vecchia visualizzazione 
							if(serviziVisualizzaModalitaElenco) {
								totEntries +=2;
							} 
							// ASPS nuova visualizzazione
							totEntries +=2;
						}
					}
				}

				// Cooperazione e Accordi Composti con permessi P
				if(showAccordiCooperazione){
					if(this.core.isRegistroServiziLocale()){
						totEntries +=2;
					}
				}

				// Ruoli, Token Policy e Scope
				if(pu.isServizi()){
					if(this.core.isRegistroServiziLocale()){
						// ruoli
						totEntries +=1;
						// scope
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
						if(isModalitaCompleta) {
							entries[index][0] = AccordiServizioParteComuneCostanti.LABEL_APC_MENU_VISUALE_AGGREGATA;
							entries[index][1] = AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
									AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO+"="+
									AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE;
							index++; 
						} else {
							entries[index][0] = AccordiServizioParteComuneCostanti.LABEL_APC_MENU_VISUALE_AGGREGATA;
							entries[index][1] = ApiCostanti.SERVLET_NAME_APC_API_LIST +"?"+
									AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO+"="+
									AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE;
							index++; 
						}

						//ASPS
						if(isModalitaCompleta) {
							entries[index][0] = AccordiServizioParteSpecificaCostanti.LABEL_APS_MENU_VISUALE_AGGREGATA;
							entries[index][1] = AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST+"?"+
									AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE+"="+
									AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_COMPLETA;
							index++;
						}
						else {
							// ASPS vecchia visualizzazione 
							if(serviziVisualizzaModalitaElenco) {
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
							
							// ASPS nuova visualizzazione
							entries[index][0] = ErogazioniCostanti.LABEL_ASPS_EROGAZIONI;
							entries[index][1] = ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST+"?"+
									AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE+"="+
									AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE;
							index++;
							
							entries[index][0] = ErogazioniCostanti.LABEL_ASPS_FRUIZIONI;
							entries[index][1] = ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST+"?"+
									AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE+"="+
									AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE;
							index++;
						}
					}
				}

				// Cooperazione e Accordi Composti con permessi P
				if(showAccordiCooperazione){
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

					// SA
					if(this.isModalitaCompleta()) {
						entries[index][0] = ServiziApplicativiCostanti.LABEL_SA_MENU_VISUALE_AGGREGATA;
					}
					else {
						entries[index][0] = ServiziApplicativiCostanti.LABEL_APPLICATIVI_MENU_VISUALE_AGGREGATA;
					}
					entries[index][1] = ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST;
					index++;
					
				}
				
				// Ruoli, PolicyToken e Scopes
				if(pu.isServizi()){
					if(this.core.isRegistroServiziLocale()){
						entries[index][0] = RuoliCostanti.LABEL_RUOLI;
						entries[index][1] = RuoliCostanti.SERVLET_NAME_RUOLI_LIST;
						index++;
												
						entries[index][0] = ScopeCostanti.LABEL_SCOPES;
						entries[index][1] = ScopeCostanti.SERVLET_NAME_SCOPE_LIST;
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
				
				List<String> aliases = this.confCore.getJmxPdD_aliases();
				
				boolean showCodaMessaggi = pu.isCodeMessaggi() && this.core.showCodaMessage();
				
				if ( showCodaMessaggi || pu.isAuditing() || 
						(pu.isSistema() && aliases!=null && aliases.size()>0) ||
						(listStrumenti!=null && listStrumenti.size()>0) ) {
					// Se l'utente non ha i permessi "diagnostica", devo
					// gestire la reportistica
					MenuEntry me = new MenuEntry();
					me.setTitle(CostantiControlStation.LABEL_STRUMENTI);

					int totEntries = 0;
					if(pu.isSistema() && aliases!=null && aliases.size()>0){
						totEntries++; // runtime
					}
					if(this.isModalitaAvanzata() && showCodaMessaggi) {
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

					if(pu.isSistema() && aliases!=null && aliases.size()>0){
						entries[i][0] = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RUNTIME;
						entries[i][1] = ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_SISTEMA_ADD;
						i++;
					}
					
					if (pu.isAuditing()) {
						entries[i][0] = AuditCostanti.LABEL_AUDIT;
						entries[i][1] = AuditCostanti.SERVLET_NAME_AUDITING;
						i++;
					}
					if (this.isModalitaAvanzata() && showCodaMessaggi) {
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


					dimensioneEntries = 5; // configurazione, tracciamento, controllo del traffico, policy e audit

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
					entries[index][1] = ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO;
					index++;
					entries[index][0] = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN;
					entries[index][1] = ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN_LIST;
					index++;			
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

				boolean showCodaMessaggi = pu.isCodeMessaggi() && this.core.showCodaMessage();
				
				if (pu.isAuditing() || pu.isSistema() || showCodaMessaggi) {
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
					if (this.isModalitaAvanzata() && showCodaMessaggi) {
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

			for (MenuEntry menuEntry : menu) {
				String [][] entries = menuEntry.getEntries();
				if(entries!=null && entries.length>0) {
					for (int i = 0; i < entries.length; i++) {
						String [] voce = entries[i];
						if(voce[1]!=null && !"".equals(voce[1])) {
							String newUrl = voce[1];
							if(newUrl.contains("?")) {
								newUrl = newUrl + "&";
							}
							else {
								newUrl = newUrl + "?";
							}
							newUrl = newUrl + CostantiControlStation.PARAMETRO_RESET_SEARCH;
							newUrl = newUrl + "=";
							newUrl = newUrl + Costanti.CHECK_BOX_ENABLED;
							voce[1] = newUrl;
						}
					}
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
		// Non devo inizializzare la lista degli utenti
		if(Liste.UTENTI_SERVIZI != idLista && Liste.UTENTI_SOGGETTI != idLista) {
			this.setFilterSelectedProtocol(ricerca, idLista);
		}
		this.setFilterRuoloServizioApplicativo(ricerca, idLista);
	}
	
	public Search checkSearchParameters(int idLista, Search ricerca)
			throws Exception {
		try {
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);

			String search = ricerca.getSearchString(idLista);

			if (this.getParameter(Costanti.SEARCH_INDEX) != null) {
				offset = Integer.parseInt(this.getParameter(Costanti.SEARCH_INDEX));
				ricerca.setIndexIniziale(idLista, offset);
			}
			if (this.getParameter(Costanti.SEARCH_PAGE_SIZE) != null) {
				limit = Integer.parseInt(this.getParameter(Costanti.SEARCH_PAGE_SIZE));
				ricerca.setPageSize(idLista, limit);
			}
			if (this.getParameter(Costanti.SEARCH) != null) {
				search = this.getParameter(Costanti.SEARCH);
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
			if(this.core.isUsedByApi()) {
				return false;
			}
			
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
		return addHiddenFieldsToDati(tipoOp, id, idsogg, idPorta, idAsps, null, null, null, dati);
	}

	public Vector<DataElement> addHiddenFieldsToDati(TipoOperazione tipoOp, String id, String idsogg, String idPorta, String idAsps, 
			String idFruizione, String tipoSoggettoFruitore, String nomeSoggettoFruitore,
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
		
		if(tipoSoggettoFruitore != null){
			de = new DataElement();
			de.setValue(tipoSoggettoFruitore);
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SOGGETTO_FRUITORE);
			dati.addElement(de);
		}
		
		if(nomeSoggettoFruitore != null){
			de = new DataElement();
			de.setValue(nomeSoggettoFruitore);
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SOGGETTO_FRUITORE);
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
	
	public boolean checkLength255(String value, String object) throws Exception{
		return this.checkLength(value, object, -1, 255);
	}
	public boolean checkLength(String value, String object, int minLength, int maxLength) throws Exception{
		if(minLength>0) {
			if(value==null || value.length()<minLength) {
				this.pd.setMessage("L'informazione fornita nel campo '"+object+"' deve possedere una lunghezza maggiore di "+(minLength-1));
				return false;
			}
		}
		if(maxLength>0) {
			if(value!=null && value.length()>maxLength) {
				this.pd.setMessage("L'informazione fornita nel campo '"+object+"' deve possedere una lunghezza minore di "+maxLength);
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
		
			String labelApplicativo = CostantiControlStation.LABEL_PARAMETRO_SERVIZIO_APPLICATIVO;
			if(!this.isModalitaCompleta()) {
				labelApplicativo = CostantiControlStation.LABEL_PARAMETRO_APPLICATIVO;
			}
			
			DataElement de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(labelApplicativo);
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel( CostantiControlStation.LABEL_PARAMETRO_NOME );
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
				de.setType(DataElementType.TITLE);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_SOGGETTO);
				dati.addElement(de);
				
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_NOME);
				de.setType(DataElementType.SELECT);
				de.setName(CostantiControlStation.PARAMETRO_SOGGETTO);
				de.setLabels(soggettiLabelList);
				de.setValues(soggettiList);
				de.setSelected(soggetto);
				dati.addElement(de);
				
			}else{
				if(addMsgSoggettiNonDisponibili){
					if(sizeAttuale>0){
						this.pd.setMessage("Non esistono ulteriori soggetti associabili",org.openspcoop2.web.lib.mvc.MessageType.INFO);
					}
					else{
						this.pd.setMessage("Non esistono soggetti associabili",org.openspcoop2.web.lib.mvc.MessageType.INFO);
					}
					this.pd.disableEditMode();
				}
			}

			return dati;
		}

	public Vector<DataElement> addPorteServizioApplicativoAutorizzatiToDati(TipoOperazione tipoOp, Vector<DataElement> dati, 
			String[] soggettiLabelList, String[] soggettiList, String soggetto, int sizeAttuale, 
			Map<String,List<ServizioApplicativo>> listServiziApplicativi, String sa,
				boolean addMsgApplicativiNonDisponibili) {
			
			if(soggettiList!=null && soggettiList.length>0 && listServiziApplicativi!=null && listServiziApplicativi.size()>0){
			
				DataElement de = new DataElement();
				de.setType(DataElementType.TITLE);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_APPLICATIVO);
				dati.addElement(de);
				
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_SOGGETTO);
				de.setName(CostantiControlStation.PARAMETRO_SOGGETTO);
				de.setValue(soggetto);
				if(this.core.isMultitenant()) {
					de.setType(DataElementType.SELECT);
					de.setLabels(soggettiLabelList);
					de.setValues(soggettiList);
					de.setSelected(soggetto);
					de.setPostBack(true);
				}
				else {
					de.setType(DataElementType.HIDDEN);
				}
				dati.addElement(de);
				
				List<ServizioApplicativo> listSA = null;
				if(soggetto!=null && !"".equals(soggetto)) {
					listSA = listServiziApplicativi.get(soggetto);
				}
				
				if(listSA!=null && !listSA.isEmpty()) {
					
					String [] saValues = new String[listSA.size()];
					String [] saLabels = new String[listSA.size()];
					int index =0;
					for (ServizioApplicativo saObject : listSA) {
						saValues[index] = saObject.getId().longValue()+"";
						saLabels[index] = saObject.getNome();
						index++;
					}
					
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_NOME);
					de.setType(DataElementType.SELECT);
					de.setName(CostantiControlStation.PARAMETRO_SERVIZIO_APPLICATIVO_AUTORIZZATO);
					de.setLabels(saLabels);
					de.setValues(saValues);
					de.setSelected(sa);
					dati.addElement(de);
					
				}
				else {
					this.pd.setMessage("Non esistono applicativi associabili per il soggetto selezionato",org.openspcoop2.web.lib.mvc.MessageType.INFO);
					this.pd.disableEditMode();
				}
				
			}else{
				if(addMsgApplicativiNonDisponibili){
					if(sizeAttuale>0){
						this.pd.setMessage("Non esistono ulteriori applicativi associabili",org.openspcoop2.web.lib.mvc.MessageType.INFO);
					}
					else{
						this.pd.setMessage("Non esistono applicativi associabili",org.openspcoop2.web.lib.mvc.MessageType.INFO);
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
			
			if (messageSecurity.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MESSAGE_SECURITY_ABILITATO)){
				String req = this.getParameter(CostantiControlStation.PARAMETRO_REQUEST_FLOW_PROPERTIES_CONFIG_NAME); 
				String res = this.getParameter(CostantiControlStation.PARAMETRO_RESPONSE_FLOW_PROPERTIES_CONFIG_NAME); 
				if( 
						(req==null || "".equals(req) || CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO.equals(req))
						&&
						(res==null || "".equals(res) || CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO.equals(res))
						) {
					this.pd.setMessage("Almeno uno schema di sicurezza per la richiesta o per la risposta dev'essere abilitato");
					return false;
				}
			}
			
			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public Vector<DataElement> addMessageSecurityToDati(Vector<DataElement> dati,boolean delegata,long idPorta,
                String messageSecurity, String url1, String url2, Boolean contaListe, int numWSreq, int numWSres, boolean showApplicaMTOMReq, String applicaMTOMReq,
                boolean showApplicaMTOMRes, String applicaMTOMRes,String idPropConfigReq, String idPropConfigRes, 
                String[] propConfigReqLabelList, String[] propConfigReqList, String[] propConfigResLabelList, String[] propConfigResList,String oldIdPropConfigReq, String oldIdPropConfigRes) {


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
		de.setPostBack(true);
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
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_REQUEST_FLOW_PROPERTIES_CONFIG_NAME);
			de.setName(CostantiControlStation.PARAMETRO_REQUEST_FLOW_PROPERTIES_CONFIG_NAME);
			if(propConfigReqList.length > 1){
				de.setType(DataElementType.SELECT);
				de.setLabels(propConfigReqLabelList);
				de.setValues(propConfigReqList);
				de.setSelected(idPropConfigReq);
				de.setPostBack(true);
			}else{
				de.setType(DataElementType.HIDDEN);
				de.setValue(idPropConfigReq); 
			}
			dati.addElement(de);

			if(idPropConfigReq.equals(oldIdPropConfigReq) && !idPropConfigReq.equals(CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO)) { // finche' non applico la modifica questi due valori saranno diversi
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(url1);
				if(idPropConfigReq.equals(CostantiControlStation.VALUE_PARAMETRO_PROPERTIES_MODE_DEFAULT)) {
					if (contaListe)
						de.setValue(CostantiControlStation.LABEL_PARAMETRO_PARAMETRI+"(" + numWSreq + ")");
					else
						de.setValue(CostantiControlStation.LABEL_PARAMETRO_PARAMETRI);
				} else {
					if(numWSreq<=0) {
						boolean editModeInProgress = true;
						try {
							editModeInProgress = this.isEditModeInProgress();
						}catch(Exception e) {}
						if(!editModeInProgress) {
							de.setValue(CostantiControlStation.LABEL_CONFIGURAZIONE_PROPERTIES_PROCEDI);
						}
						else {
							
							DataElement note = new DataElement();
							note.setBold(true);
							note.setLabel(CostantiControlStation.LABEL_CONFIGURAZIONE_INCOMPLETA_LABEL);
							note.setValue(CostantiControlStation.LABEL_CONFIGURAZIONE_INCOMPLETA);
							note.setType(DataElementType.NOTE);
							dati.addElement(note);
							
							de.setValue(CostantiControlStation.LABEL_CONFIGURAZIONE_PROPERTIES_COMPLETA);
						}
					}
					else {
						// Se cambia l'xml potrebbe succedere
						boolean valida = false;
						try {
							PropertiesSourceConfiguration propertiesSourceConfiguration = this.core.getMessageSecurityPropertiesSourceConfiguration();
							ConfigManager configManager = ConfigManager.getinstance(ControlStationCore.getLog());
							Config configurazione = configManager.getConfigurazione(propertiesSourceConfiguration, idPropConfigReq);
							Map<String, Properties> mappaDB = null;
							if(delegata) {
								mappaDB = this.porteDelegateCore.readMessageSecurityRequestPropertiesConfiguration(idPorta); 
							}
							else {
								mappaDB = this.porteApplicativeCore.readMessageSecurityRequestPropertiesConfiguration(idPorta); 
							}
							ConfigBean configurazioneBean = ReadPropertiesUtilities.leggiConfigurazione(configurazione, mappaDB);
							valida = this.checkPropertiesConfigurationData(TipoOperazione.OTHER, configurazioneBean, configurazione);
						}catch(Exception e) {
							this.log.error(e.getMessage(),e);
						}
						if(valida) {
							de.setValue(CostantiControlStation.LABEL_CONFIGURAZIONE_PROPERTIES);
						}
						else {
							de.setValue(CostantiControlStation.LABEL_CONFIGURAZIONE_PROPERTIES_COMPLETA);
						}
					}
				}
				dati.addElement(de);
			}

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
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_RESPONSE_FLOW_PROPERTIES_CONFIG_NAME);
			de.setName(CostantiControlStation.PARAMETRO_RESPONSE_FLOW_PROPERTIES_CONFIG_NAME);
			if(propConfigResList.length > 1){
				de.setType(DataElementType.SELECT);
				de.setLabels(propConfigResLabelList);
				de.setValues(propConfigResList);
				de.setSelected(idPropConfigRes);
				de.setPostBack(true);
			}else{
				de.setType(DataElementType.HIDDEN);
				de.setValue(idPropConfigReq); 
			}
			dati.addElement(de);

			if(idPropConfigRes.equals(oldIdPropConfigRes) && !idPropConfigRes.equals(CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO)) { // finche' non applico la modifica questi due valori saranno diversi
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(url2);
				if(idPropConfigRes.equals(CostantiControlStation.VALUE_PARAMETRO_PROPERTIES_MODE_DEFAULT)) {
					if (contaListe)
						de.setValue(CostantiControlStation.LABEL_PARAMETRO_PARAMETRI+"(" + numWSres + ")");
					else
						de.setValue(CostantiControlStation.LABEL_PARAMETRO_PARAMETRI);
				} else {
					if(numWSres<=0) {
						boolean editModeInProgress = true;
						try {
							editModeInProgress = this.isEditModeInProgress();
						}catch(Exception e) {}
						if(!editModeInProgress) {
							de.setValue(CostantiControlStation.LABEL_CONFIGURAZIONE_PROPERTIES_PROCEDI);
						}
						else {
							
							DataElement note = new DataElement();
							note.setBold(true);
							note.setLabel(CostantiControlStation.LABEL_CONFIGURAZIONE_INCOMPLETA_LABEL);
							note.setValue(CostantiControlStation.LABEL_CONFIGURAZIONE_INCOMPLETA);
							note.setType(DataElementType.NOTE);
							dati.addElement(note);
							
							de.setValue(CostantiControlStation.LABEL_CONFIGURAZIONE_PROPERTIES_COMPLETA);
						}
					}
					else {
						// Se cambia l'xml potrebbe succedere
						boolean valida = false;
						try {
							PropertiesSourceConfiguration propertiesSourceConfiguration = this.core.getMessageSecurityPropertiesSourceConfiguration();
							ConfigManager configManager = ConfigManager.getinstance(ControlStationCore.getLog());
							Config configurazione = configManager.getConfigurazione(propertiesSourceConfiguration, idPropConfigRes);
							Map<String, Properties> mappaDB = null;
							if(delegata) {
								mappaDB = this.porteDelegateCore.readMessageSecurityResponsePropertiesConfiguration(idPorta); 
							}
							else {
								mappaDB = this.porteApplicativeCore.readMessageSecurityResponsePropertiesConfiguration(idPorta); 
							}
							ConfigBean configurazioneBean = ReadPropertiesUtilities.leggiConfigurazione(configurazione, mappaDB);
							valida = this.checkPropertiesConfigurationData(TipoOperazione.OTHER, configurazioneBean, configurazione);
						}catch(Exception e) {
							this.log.error(e.getMessage(),e);
						}
						if(valida) {
							de.setValue(CostantiControlStation.LABEL_CONFIGURAZIONE_PROPERTIES);
						}
						else {
							de.setValue(CostantiControlStation.LABEL_CONFIGURAZIONE_PROPERTIES_COMPLETA);
						}
					}
				}
				dati.addElement(de);
			}
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
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CORRELAZIONE_APPLICATIVA);
		dati.addElement(de);
		
		de = new DataElement();
		de.setType(DataElementType.SUBTITLE);
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
			} 
		}
		
		de = new DataElement();
		de.setType(DataElementType.LINK);
		de.setUrl(urlRichiesta);

		if (contaListe) {
			ServletUtils.setDataElementCustomLabel(de,CostantiControlStation.LABEL_PARAMETRO_CORRELAZIONE_APPLICATIVA_RICHIESTA,Long.valueOf(numCorrelazioneReq));
		} else
			ServletUtils.setDataElementCustomLabel(de,CostantiControlStation.LABEL_PARAMETRO_CORRELAZIONE_APPLICATIVA_RICHIESTA);

		dati.addElement(de);

		
		de = new DataElement();
		de.setType(DataElementType.SUBTITLE);
		//de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CORRELAZIONE_APPLICATIVA);
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_RISPOSTA);
		dati.addElement(de);
		
		de = new DataElement();
		de.setType(DataElementType.LINK);
		de.setUrl(urlRisposta);

		if (contaListe) {
			ServletUtils.setDataElementCustomLabel(de,CostantiControlStation.LABEL_PARAMETRO_CORRELAZIONE_APPLICATIVA_RISPOSTA,Long.valueOf(numCorrelazioneRes));
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

			if(elemxml!=null && !"".equals(elemxml)) {
				if(this.checkLength255(elemxml, CostantiControlStation.LABEL_PARAMETRO_PORTE_ELEMENTO_XML)==false) {
					return false;
				}
			}
			
			// Campi obbligatori
			// if ( elemxml.equals("")||
			if ( 
					(
							mode.equals(CostantiControlStation.VALUE_PARAMETRO_MODE_CORRELAZIONE_URL_BASED) || 
							mode.equals(CostantiControlStation.VALUE_PARAMETRO_MODE_CORRELAZIONE_HEADER_BASED) || 
							mode.equals(CostantiControlStation.VALUE_PARAMETRO_MODE_CORRELAZIONE_CONTENT_BASED)
					) 
					&& 
					pattern.equals("")
				) {
				String label = "";
				if(mode.equals(CostantiControlStation.VALUE_PARAMETRO_MODE_CORRELAZIONE_HEADER_BASED)) {
					label = CostantiControlStation.LABEL_PARAMETRO_NOME;
				}
				else {
					label = CostantiControlStation.LABEL_PATTERN;
				}
				
				this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, label));
				return false;
			}

			// Controllo che i campi "select" abbiano uno dei valori ammessi
			if (!mode.equals(CostantiControlStation.VALUE_PARAMETRO_MODE_CORRELAZIONE_URL_BASED) 
					&& !mode.equals(CostantiControlStation.VALUE_PARAMETRO_MODE_CORRELAZIONE_HEADER_BASED) 
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

			if(elemxml!=null && !"".equals(elemxml)) {
				if(this.checkLength255(elemxml, CostantiControlStation.LABEL_PARAMETRO_PORTE_ELEMENTO_XML)==false) {
					return false;
				}
			}
			
			// Campi obbligatori
			// if ( elemxml.equals("")||
			if ( 
					(
							mode.equals(CostantiControlStation.VALUE_PARAMETRO_MODE_CORRELAZIONE_URL_BASED) || 
							mode.equals(CostantiControlStation.VALUE_PARAMETRO_MODE_CORRELAZIONE_HEADER_BASED) || 
							mode.equals(CostantiControlStation.VALUE_PARAMETRO_MODE_CORRELAZIONE_CONTENT_BASED)
					) 
					&& 
					pattern.equals("")
				) {
				String label = "";
				if(mode.equals(CostantiControlStation.VALUE_PARAMETRO_MODE_CORRELAZIONE_HEADER_BASED)) {
					label = CostantiControlStation.LABEL_PARAMETRO_NOME;
				}
				else {
					label = CostantiControlStation.LABEL_PATTERN;
				}
				
				this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, label));
				return false;
			}

			// Controllo che i campi "select" abbiano uno dei valori ammessi
			if (!mode.equals(CostantiControlStation.VALUE_PARAMETRO_MODE_CORRELAZIONE_URL_BASED) 
					&& !mode.equals(CostantiControlStation.VALUE_PARAMETRO_MODE_CORRELAZIONE_HEADER_BASED)
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

			// length
			if(this.checkLength255(nome, CostantiControlStation.LABEL_PARAMETRO_NOME)==false) {
				return false;
			}
			if(contentType!=null && !"".equals(contentType)){
				if(this.checkLength255(contentType, CostantiControlStation.LABEL_PARAMETRO_CONTENT_TYPE)==false) {
					return false;
				}
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
		de.setType(DataElementType.TEXT_AREA);
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
			boolean addTitoloSezione, 
			String accessDaChangeTmp) throws DriverRegistroServiziException {
		return this.addRuoliToDati(tipoOp, dati, enableUpdate, filtroRuoli, nome, ruoliGiaConfigurati, 
				addSelezioneVuota, addMsgServiziApplicativoNonDisponibili, CostantiControlStation.LABEL_PARAMETRO_RUOLO, 
				addTitoloSezione, accessDaChangeTmp);
	}
	public Vector<DataElement> addRuoliToDati(TipoOperazione tipoOp,Vector<DataElement> dati,boolean enableUpdate, FiltroRicercaRuoli filtroRuoli, String nome, 
			List<String> ruoliGiaConfigurati, boolean addSelezioneVuota, boolean addMsgServiziApplicativoNonDisponibili, String labelParametro,
			boolean addTitoloSezione,
			String accessDaChangeTmp) throws DriverRegistroServiziException {

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
		
		DataElement de = new DataElement();
		de.setName(CostantiControlStation.PARAMETRO_ACCESSO_DA_CHANGE);
		de.setType(DataElementType.HIDDEN);
		de.setValue(accessDaChangeTmp);
		dati.addElement(de);
		
		// Nome
		if(ruoliDaFarScegliere.size()>0){
			
			if(addTitoloSezione){
				de = new DataElement();
				de.setLabel(RuoliCostanti.LABEL_RUOLO);
				de.setType(DataElementType.TITLE);
				dati.addElement(de);
			}
			
			List<String> ruoli = new ArrayList<>();
			if(addSelezioneVuota){
				ruoli.add("-");
			}
			ruoli.addAll(ruoliDaFarScegliere);
			
			de = new DataElement();
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
					this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_NON_ESISTONO_ULTERIORI_RUOLI_ASSOCIABILI);
				}
				else{
					this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_NON_ESISTONO_RUOLI_ASSOCIABILI);
				}
				this.pd.disableEditMode();
			}
		}

		return dati;
	}
	public Vector<DataElement> addScopeToDati(TipoOperazione tipoOp,Vector<DataElement> dati,boolean enableUpdate, FiltroRicercaScope filtroScope, String nome, 
			List<String> ruoliGiaConfigurati, boolean addSelezioneVuota, boolean addMsgServiziApplicativoNonDisponibili, 
			boolean addTitoloSezione) throws DriverRegistroServiziException {
		return this.addScopeToDati(tipoOp, dati, enableUpdate, filtroScope, nome, ruoliGiaConfigurati, 
				addSelezioneVuota, addMsgServiziApplicativoNonDisponibili, CostantiControlStation.LABEL_PARAMETRO_SCOPE, 
				addTitoloSezione);
	}
	public Vector<DataElement> addScopeToDati(TipoOperazione tipoOp,Vector<DataElement> dati,boolean enableUpdate, FiltroRicercaScope filtroScope, String nome, 
			List<String> scopeGiaConfigurati, boolean addSelezioneVuota, boolean addMsgServiziApplicativoNonDisponibili, String labelParametro,
			boolean addTitoloSezione) throws DriverRegistroServiziException {

		List<String> allRuoli = this.confCore.getAllScope(filtroScope);
		List<String> scopeDaFarScegliere = new ArrayList<>();
		if(scopeGiaConfigurati!=null && scopeGiaConfigurati.size()>0){
			for (String ruolo : allRuoli) {
				if(scopeGiaConfigurati.contains(ruolo)==false){
					scopeDaFarScegliere.add(ruolo);
				}
			}
		}
		else{
			scopeDaFarScegliere.addAll(allRuoli);
		}
		
		// Nome
		if(scopeDaFarScegliere.size()>0){
			
			if(addTitoloSezione){
				DataElement de = new DataElement();
				de.setLabel(ScopeCostanti.LABEL_SCOPE);
				de.setType(DataElementType.TITLE);
				dati.addElement(de);
			}
			
			List<String> ruoli = new ArrayList<>();
			if(addSelezioneVuota){
				ruoli.add("-");
			}
			ruoli.addAll(scopeDaFarScegliere);
			
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
			de.setName(CostantiControlStation.PARAMETRO_SCOPE);
			de.setSize(this.getSize());
			dati.addElement(de);
		}
		else{
			if(addMsgServiziApplicativoNonDisponibili){
				if(allRuoli.size()>0){
					this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_NON_ESISTONO_ULTERIORI_SCOPE_ASSOCIABILI);
				}
				else{
					this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_NON_ESISTONO_SCOPE_ASSOCIABILI);
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
	
	public boolean scopeCheckData(TipoOperazione tipoOp, String nome, List<String> scopes) throws Exception {
		try {
			
			if(scopes!=null && scopes.contains(nome)){
				this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_LO_SCOPE_XX_E_GIA_STATO_ASSOCIATA_AL_SOGGETTO, nome));
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
	
	public void controlloAccessiAutenticazione(Vector<DataElement> dati, TipoOperazione tipoOperazione, String autenticazione, String autenticazioneCustom, String autenticazioneOpzionale,
			boolean confPers, boolean isSupportatoAutenticazioneSoggetti,boolean isPortaDelegata,
			String gestioneToken,String gestioneTokenPolicy,String autenticazioneTokenIssuer,String autenticazioneTokenClientId,String autenticazioneTokenSubject,String autenticazioneTokenUsername,String autenticazioneTokenEMail){
		
		boolean tokenAbilitato = StatoFunzionalita.ABILITATO.getValue().equalsIgnoreCase(gestioneToken) &&
				gestioneTokenPolicy != null && !gestioneTokenPolicy.equals(CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO);
		
		boolean mostraSezione = !tipoOperazione.equals(TipoOperazione.ADD) || 
				(isPortaDelegata ? this.core.isEnabledAutenticazione_generazioneAutomaticaPorteDelegate() : this.core.isEnabledAutenticazione_generazioneAutomaticaPorteApplicative());
		
		if(mostraSezione){
			
			if(isSupportatoAutenticazioneSoggetti || tokenAbilitato) {
				DataElement de = new DataElement();
				de.setType(DataElementType.TITLE); //SUBTITLE);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE);
				dati.addElement(de);
			}
			
			if(isSupportatoAutenticazioneSoggetti){
				
				DataElement de = new DataElement();
				de.setType(DataElementType.SUBTITLE); //SUBTITLE);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE_TRASPORTO);
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
				if(TipoAutenticazione.DISABILITATO.getValue().equals(autenticazione)==false){
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(ServletUtils.isCheckBoxEnabled(autenticazioneOpzionale));
				}
				else{
					de.setType(DataElementType.HIDDEN);
					de.setValue("");
				}
				dati.addElement(de);
			}
			
			if(tokenAbilitato) {

				DataElement de = new DataElement();
				de.setType(DataElementType.SUBTITLE); //SUBTITLE);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE_TOKEN);
				dati.addElement(de);
				
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_ISSUER);
				de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_ISSUER);
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(ServletUtils.isCheckBoxEnabled(autenticazioneTokenIssuer));
				dati.addElement(de);
				
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_CLIENT_ID);
				de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_CLIENT_ID);
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(ServletUtils.isCheckBoxEnabled(autenticazioneTokenClientId));
				dati.addElement(de);
				
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_SUBJECT);
				de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_SUBJECT);
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(ServletUtils.isCheckBoxEnabled(autenticazioneTokenSubject));
				dati.addElement(de);
				
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_USERNAME);
				de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_USERNAME);
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(ServletUtils.isCheckBoxEnabled(autenticazioneTokenUsername));
				dati.addElement(de);
				
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_MAIL);
				de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_MAIL);
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(ServletUtils.isCheckBoxEnabled(autenticazioneTokenEMail));
				dati.addElement(de);
				
			}
			
		} else {
			DataElement de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE);
			de.setType(DataElementType.HIDDEN);
			de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE);
			de.setValue(TipoAutenticazione.DISABILITATO.getValue());
			dati.addElement(de);
		}
	}
	
	public void controlloAccessiGestioneToken(Vector<DataElement> dati, TipoOperazione tipoOperazione, String gestioneToken, String[] gestioneTokenPolicyLabels, String[] gestioneTokenPolicyValues,
			String gestioneTokenPolicy, String gestioneTokenOpzionale, String gestioneTokenValidazioneInput, String gestioneTokenIntrospection, String gestioneTokenUserInfo, String gestioneTokenForward,Object oggetto,boolean isPortaDelegata) throws Exception {
		
		boolean mostraSezione = !tipoOperazione.equals(TipoOperazione.ADD) || 
				(isPortaDelegata ? this.core.isEnabledToken_generazioneAutomaticaPorteDelegate() : this.core.isEnabledToken_generazioneAutomaticaPorteApplicative());
		
		if(mostraSezione) {
			
			DataElement de = new DataElement();
			de.setType(DataElementType.TITLE); //SUBTITLE);
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_GESTIONE_TOKEN);
			dati.addElement(de);
			
			String [] valoriAbilitazione = {StatoFunzionalita.DISABILITATO.getValue(), StatoFunzionalita.ABILITATO.getValue()};
			
			// stato abilitazione
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN);
			de.setType(DataElementType.SELECT);
			de.setName(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN);
			de.setValues(valoriAbilitazione);
			de.setPostBack(true);
			de.setSelected(gestioneToken);
			dati.addElement(de);
			
			if(StatoFunzionalita.ABILITATO.getValue().equals(gestioneToken)){
				// nome della policy da utilizzare
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN_POLICY);
				de.setType(DataElementType.SELECT);
				de.setName(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_POLICY);
				de.setValues(gestioneTokenPolicyValues);
				de.setValues(gestioneTokenPolicyLabels);
				de.setSelected(gestioneTokenPolicy);
				de.setRequired(true);
				de.setPostBack(true);
				dati.addElement(de);
				
				if(gestioneTokenPolicy != null && !gestioneTokenPolicy.equals(CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO)) {
					
					GenericProperties policySelezionata = this.confCore.getGenericProperties(gestioneTokenPolicy, CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_GESTIONE_POLICY_TOKEN);
					Map<String, Properties> mappaDB = this.confCore.readGestorePolicyTokenPropertiesConfiguration(policySelezionata.getId()); 
					
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN_OPZIONALE);
					de.setName(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_OPZIONALE);
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(ServletUtils.isCheckBoxEnabled(gestioneTokenOpzionale));
					dati.addElement(de);
					
					// validazione input
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN_VALIDAZIONE_INPUT);
					de.setName(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_VALIDAZIONE_INPUT);
					if(TokenUtilities.isValidazioneEnabled(mappaDB)) {
						de.setType(DataElementType.SELECT);
						de.setValues(CostantiControlStation.SELECT_VALUES_STATO_FUNZIONALITA_CON_WARNING);
						de.setSelected(gestioneTokenValidazioneInput);
						de.setPostBack(true);
					}else {
						de.setType(DataElementType.HIDDEN);
						de.setValue(StatoFunzionalita.DISABILITATO.getValue());
					}
					dati.addElement(de);
					
					// introspection
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN_INTROSPECTION);
					de.setName(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_INTROSPECTION);
					if(TokenUtilities.isIntrospectionEnabled(mappaDB)) {
						de.setType(DataElementType.SELECT);
						de.setValues(CostantiControlStation.SELECT_VALUES_STATO_FUNZIONALITA_CON_WARNING);
						de.setSelected(gestioneTokenIntrospection);
						de.setPostBack(true);
					}else {
						de.setType(DataElementType.HIDDEN);
						de.setValue(StatoFunzionalita.DISABILITATO.getValue());
					}
					dati.addElement(de);
					
					// userInfo
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN_USERINFO);
					de.setName(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_USERINFO);
					if(TokenUtilities.isUserInfoEnabled(mappaDB)) {
						de.setType(DataElementType.SELECT);
						de.setValues(CostantiControlStation.SELECT_VALUES_STATO_FUNZIONALITA_CON_WARNING);
						de.setSelected(gestioneTokenUserInfo);
						de.setPostBack(true);
					}else {
						de.setType(DataElementType.HIDDEN);
						de.setValue(StatoFunzionalita.DISABILITATO.getValue());
					}
					dati.addElement(de);
					
					// token forward
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN_TOKEN_FORWARD);
					de.setName(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_TOKEN_FORWARD);
					if(TokenUtilities.isTokenForwardEnabled(mappaDB)) {
						de.setType(DataElementType.SELECT);
						de.setValues(CostantiControlStation.SELECT_VALUES_STATO_FUNZIONALITA);
						de.setSelected(gestioneTokenForward);
						de.setPostBack(true);
					}else {
						de.setType(DataElementType.HIDDEN);
						de.setValue(StatoFunzionalita.DISABILITATO.getValue());
					}
					dati.addElement(de);
				}
			}
		} else {
			// stato abilitazione
			DataElement de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN);
			de.setType(DataElementType.HIDDEN);
			de.setName(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN);
			de.setValue(gestioneToken);
			dati.addElement(de);
		}
	}
	
	public void controlloAccessiAutorizzazione(Vector<DataElement> dati, TipoOperazione tipoOperazione, String servletChiamante, Object oggetto,
			String autenticazione, 
			String autorizzazione, String autorizzazioneCustom, 
			String autorizzazioneAutenticati, String urlAutorizzazioneAutenticati, int numAutenticati, List<String> autenticati, String autenticato,
			String autorizzazioneRuoli,  String urlAutorizzazioneRuoli, int numRuoli, String ruolo, String autorizzazioneRuoliTipologia, String autorizzazioneRuoliMatch,
			boolean confPers, boolean isSupportatoAutenticazione, boolean contaListe, boolean isPortaDelegata,
			boolean addTitoloSezione,String autorizzazioneScope,  String urlAutorizzazioneScope, int numScope, String scope, String autorizzazioneScopeMatch,
			String gestioneToken, String gestioneTokenPolicy, String autorizzazione_token, String autorizzazione_tokenOptions,BinaryParameter allegatoXacmlPolicy,
			String urlAutorizzazioneErogazioneApplicativiAutenticati, int numErogazioneApplicativiAutenticati) throws Exception{
		this.controlloAccessiAutorizzazione(dati, tipoOperazione, servletChiamante, oggetto, 
				autenticazione, autorizzazione, autorizzazioneCustom, 
				autorizzazioneAutenticati, urlAutorizzazioneAutenticati, numAutenticati, autenticati, null, autenticato, 
				autorizzazioneRuoli, urlAutorizzazioneRuoli, numRuoli, ruolo, autorizzazioneRuoliTipologia, autorizzazioneRuoliMatch, 
				confPers, isSupportatoAutenticazione, contaListe, isPortaDelegata, addTitoloSezione,autorizzazioneScope,urlAutorizzazioneScope,numScope,scope,autorizzazioneScopeMatch,
				gestioneToken, gestioneTokenPolicy, autorizzazione_token, autorizzazione_tokenOptions,allegatoXacmlPolicy,
				urlAutorizzazioneErogazioneApplicativiAutenticati, numErogazioneApplicativiAutenticati);
		
	}
	
	public void controlloAccessiAutorizzazione(Vector<DataElement> dati, TipoOperazione tipoOperazione, String servletChiamante, Object oggetto,
			String autenticazione, String autorizzazione, String autorizzazioneCustom, 
			String autorizzazioneAutenticati, String urlAutorizzazioneAutenticati, int numAutenticati, List<String> autenticati, List<String> autenticatiLabel, String autenticato,
			String autorizzazioneRuoli,  String urlAutorizzazioneRuoli, int numRuoli, String ruolo, String autorizzazioneRuoliTipologia, String autorizzazioneRuoliMatch,
			boolean confPers, boolean isSupportatoAutenticazione, boolean contaListe, boolean isPortaDelegata, boolean addTitoloSezione,
			String autorizzazioneScope,  String urlAutorizzazioneScope, int numScope, String scope, String autorizzazioneScopeMatch,
			String gestioneToken, String gestioneTokenPolicy, String autorizzazione_token, String autorizzazione_tokenOptions, BinaryParameter allegatoXacmlPolicy,
			String urlAutorizzazioneErogazioneApplicativiAutenticati, int numErogazioneApplicativiAutenticati) throws Exception{
		
		String protocollo = null;
		if(oggetto!=null){
			if(isPortaDelegata){
				PortaDelegata pd = (PortaDelegata) oggetto;
				if(pd!=null && pd.getServizio()!=null && pd.getServizio().getTipo()!=null) {
					protocollo = this.apsCore.getProtocolloAssociatoTipoServizio(pd.getServizio().getTipo());
				}
			}
			else {
				PortaApplicativa pa = (PortaApplicativa) oggetto;
				if(pa!=null && pa.getServizio()!=null && pa.getServizio().getTipo()!=null) {
					protocollo = this.apsCore.getProtocolloAssociatoTipoServizio(pa.getServizio().getTipo());
				}
			}
		}
		
		boolean mostraSezione = !tipoOperazione.equals(TipoOperazione.ADD) || 
				(isPortaDelegata ? this.core.isEnabledAutorizzazione_generazioneAutomaticaPorteDelegate() : 
					this.core.isEnabledAutorizzazione_generazioneAutomaticaPorteApplicative(protocollo==null ? true : this.soggettiCore.isSupportatoAutenticazioneSoggetti(protocollo)));
		
		boolean tokenAbilitato = StatoFunzionalita.ABILITATO.getValue().equalsIgnoreCase(gestioneToken) &&
				gestioneTokenPolicy != null && !gestioneTokenPolicy.equals(CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO);
		
		if(mostraSezione) {
			DataElement de = new DataElement();
			de.setType(DataElementType.TITLE); //SUBTITLE);
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
			boolean old_autorizzazione_scope = false;
			boolean old_xacmlPolicy = false;
			String old_autorizzazione = null;
			Long idPorta = null;
			
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
					old_autorizzazione_scope = pd.getScope() != null && pd.getScope().getStato().equals(StatoFunzionalita.ABILITATO);
					old_xacmlPolicy = StringUtils.isNotEmpty(pd.getXacmlPolicy());
					idPorta = pd.getId();
				}
				else {
					PortaApplicativa pa = (PortaApplicativa) oggetto;
					old_autorizzazione = AutorizzazioneUtilities.convertToStato(pa.getAutorizzazione());
					old_autorizzazione_autenticazione = TipoAutorizzazione.isAuthenticationRequired(pa.getAutorizzazione());
					old_autorizzazione_ruoli = TipoAutorizzazione.isRolesRequired(pa.getAutorizzazione());
					old_autorizzazione_scope = pa.getScope() != null && pa.getScope().getStato().equals(StatoFunzionalita.ABILITATO);
					old_xacmlPolicy = StringUtils.isNotEmpty(pa.getXacmlPolicy());
					idPorta = pa.getId();
				}
			}
			
			if(AutorizzazioneUtilities.STATO_DISABILITATO.equals(autorizzazione)==false){
			
				boolean autorizzazione_autenticazione =  false;
							
				if(AutorizzazioneUtilities.STATO_ABILITATO.equals(autorizzazione)){
				
					if( !isSupportatoAutenticazione ||  (autenticazione!=null && !TipoAutenticazione.DISABILITATO.equals(autenticazione)) ){   
						de = new DataElement();
						de.setType(DataElementType.SUBTITLE);
						if(isPortaDelegata){
							de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE_SERVIZI_APPLICATIVI);
						}
						else{
							String labelSoggetti = (isSupportatoAutenticazione && (autenticazione!=null && !TipoAutenticazione.DISABILITATO.equals(autenticazione))) ? CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE_SOGGETTI : CostantiControlStation.LABEL_PARAMETRO_SOGGETTI;
							de.setLabel(labelSoggetti);
						}
						dati.addElement(de);
					}
					
					autorizzazione_autenticazione = ServletUtils.isCheckBoxEnabled(autorizzazioneAutenticati);
					
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_ABILITATO);
					
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
					
					if((autenticazione!=null && !TipoAutenticazione.DISABILITATO.equals(autenticazione))) {
					
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
									ServletUtils.setDataElementCustomLabel(de,labelApplicativi,Long.valueOf(numAutenticati));
								} else
									ServletUtils.setDataElementCustomLabel(de,labelApplicativi);
							}
							else{
								if (contaListe) {
									ServletUtils.setDataElementCustomLabel(de,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOGGETTI,Long.valueOf(numAutenticati));
								} else
									ServletUtils.setDataElementCustomLabel(de,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOGGETTI);
							}
							dati.addElement(de);
						}
						
						if(!isPortaDelegata && this.saCore.isSupportatoAutenticazioneApplicativiErogazione(protocollo)){
							if(urlAutorizzazioneErogazioneApplicativiAutenticati!=null && autorizzazione_autenticazione && (old_autorizzazione_autenticazione || CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM.equals(old_autorizzazione)) ){
								de = new DataElement();
								de.setType(DataElementType.LINK);
								de.setUrl(urlAutorizzazioneErogazioneApplicativiAutenticati);
								String labelApplicativi = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SERVIZI_APPLICATIVI; // uso cmq label PD
								if(!this.isModalitaCompleta()) {
									labelApplicativi = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_APPLICATIVI;// uso cmq label PD
								}
								if (contaListe) {
									ServletUtils.setDataElementCustomLabel(de,labelApplicativi,Long.valueOf(numErogazioneApplicativiAutenticati));
								} else {
									ServletUtils.setDataElementCustomLabel(de,labelApplicativi);
								}
								dati.addElement(de);
							}
						}
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
					
					de = new DataElement();
					de.setType(DataElementType.SUBTITLE);
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI);
					dati.addElement(de);
				
					autorizzazione_ruoli = ServletUtils.isCheckBoxEnabled(autorizzazioneRuoli);
					
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_ABILITATO);
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
				
				String postbackElement = this.getPostBackElementName();
				boolean aggiornatoFile = false;
				if(postbackElement != null) {
					if(postbackElement.equals(allegatoXacmlPolicy.getName())) {
						aggiornatoFile = true;
					}
				}
				if(AutorizzazioneUtilities.STATO_XACML_POLICY.equals(autorizzazione)) {
					String filePolicyLabel = CostantiControlStation.LABEL_PARAMETRO_DOCUMENTO_SICUREZZA_XACML_POLICY;
					if(old_xacmlPolicy && !aggiornatoFile) {
						filePolicyLabel = CostantiControlStation.LABEL_PARAMETRO_DOCUMENTO_SICUREZZA_XACML_NUOVA_POLICY;
						DataElement saveAs = new DataElement();
						saveAs.setValue(CostantiControlStation.LABEL_DOWNLOAD_DOCUMENTO_SICUREZZA_XACML_POLICY);
						saveAs.setType(DataElementType.LINK);
						Parameter pIdAccordo = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_ID_ACCORDO, idPorta+ "");
						Parameter pTipoAllegato = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO, isPortaDelegata ? "pd" : "pa");
						Parameter pTipoDoc = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO, isPortaDelegata ? ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_XACML_POLICY : ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_XACML_POLICY);
						saveAs.setUrl(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT, pIdAccordo, pTipoAllegato, pTipoDoc);
						dati.add(saveAs);
						
						de = new DataElement();
						de.setType(DataElementType.SUBTITLE);
						de.setLabel(CostantiControlStation.LABEL_AGGIORNAMENTO_DOCUMENTO_SICUREZZA_XACML_POLICY);
						de.setLabelStyleClass("noBold");
						dati.add(de);
					}
					dati.add(allegatoXacmlPolicy.getFileDataElement(filePolicyLabel, "", getSize()));
					dati.addAll(allegatoXacmlPolicy.getFileNameDataElement());
					dati.add(allegatoXacmlPolicy.getFileIdDataElement());
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
							ServletUtils.setDataElementCustomLabel(de,RuoliCostanti.LABEL_RUOLI,Long.valueOf(numRuoli));
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
								AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_RUOLO, addTitoloSezione, null);
					}
				}
				
			}
			if(AutorizzazioneUtilities.STATO_DISABILITATO.equals(autorizzazione)==false){
				boolean autorizzazione_scope = false;
			
				if(AutorizzazioneUtilities.STATO_ABILITATO.equals(autorizzazione) && tokenAbilitato){
					
					de = new DataElement();
					de.setType(DataElementType.SUBTITLE);
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_SCOPE);
					dati.addElement(de);
				
					autorizzazione_scope = ServletUtils.isCheckBoxEnabled(autorizzazioneScope);
					
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_ABILITATO);
					de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_SCOPE);
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(autorizzazione_scope);
					de.setPostBack(true);
					dati.addElement(de);
				
					if(autorizzazione_scope){
						String[] tipoScope = { ScopeTipoMatch.ALL.getValue(),	ScopeTipoMatch.ANY.getValue() };
						String[] labelScope = { CostantiControlStation.LABEL_PARAMETRO_SCOPE_MATCH_ALL, CostantiControlStation.LABEL_PARAMETRO_SCOPE_MATCH_ANY };
						de = new DataElement();
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_SCOPE_MATCH);
						de.setName(CostantiControlStation.PARAMETRO_SCOPE_MATCH);
						de.setType(DataElementType.SELECT);
						de.setValues(tipoScope);
						de.setLabels(labelScope);
						de.setSelected(autorizzazioneScopeMatch);
						dati.add(de);
					}
					
					if(TipoOperazione.CHANGE.equals(tipoOperazione)){
						if(urlAutorizzazioneScope!=null && autorizzazione_scope && old_autorizzazione_scope){
							
							de = new DataElement();
							de.setType(DataElementType.LINK);
							de.setUrl(urlAutorizzazioneScope);
							if (contaListe) {
								ServletUtils.setDataElementCustomLabel(de,ScopeCostanti.LABEL_SCOPE,Long.valueOf(numScope));
							} else
								ServletUtils.setDataElementCustomLabel(de,ScopeCostanti.LABEL_SCOPE);
							dati.addElement(de);
										
						}
					}
					else{
						if( (AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD.equals(servletChiamante) ||
								AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD.equals(servletChiamante))
								&& autorizzazione_scope){
							FiltroRicercaScope filtroScope = new FiltroRicercaScope();
							if(isPortaDelegata){
								filtroScope.setContesto(ScopeContesto.PORTA_DELEGATA); 
							}
							else{
								filtroScope.setContesto(ScopeContesto.PORTA_APPLICATIVA);
							}
							// tipologia non impostata
						//	filtroScope.setTipologia(RuoloTipologia.QUALSIASI);
	//						if(RuoloTipologia.INTERNO.equals(autorizzazioneRuoliTipologia) ){
	//							filtroScope.setTipologia(RuoloTipologia.INTERNO);
	//						}
	//						else if(RuoloTipologia.ESTERNO.equals(autorizzazioneRuoliTipologia) ){
	//							filtroScope.setTipologia(RuoloTipologia.ESTERNO);
	//						}
							this.addScopeToDati(tipoOperazione, dati, false, filtroScope, scope, null, true, false,
									AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_SCOPE, addTitoloSezione);
						}
					}
					
				}
				
				if(tokenAbilitato && !AutorizzazioneUtilities.STATO_XACML_POLICY.equals(autorizzazione)) {
					de = new DataElement();
					de.setType(DataElementType.SUBTITLE);
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_SUBTITLE);
					dati.addElement(de);

					boolean autorizzazioneTokenEnabled = ServletUtils.isCheckBoxEnabled(autorizzazione_token);
					
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_ABILITATO);
					de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN);
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(autorizzazioneTokenEnabled);
					de.setPostBack(true);
					dati.addElement(de);
					
					if(autorizzazioneTokenEnabled) {
						de = new DataElement();
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN);
						de.setNote(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_NOTE);
						de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_OPTIONS);
						de.setType(DataElementType.TEXT_AREA);
						de.setRows(6);
						de.setCols(55);
						de.setValue(autorizzazione_tokenOptions);
						dati.addElement(de);
					}
				}
			}
		} else {
			DataElement de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE);
			de.setType(DataElementType.HIDDEN);
			de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE);
			de.setValue(AutorizzazioneUtilities.STATO_DISABILITATO);
			dati.addElement(de);
		}
	}
	
	public void controlloAccessiAutorizzazioneContenuti(Vector<DataElement> dati, String autorizzazioneContenuti){
		
		if (this.isModalitaAvanzata()) {
			DataElement de = new DataElement();
			de.setType(DataElementType.TITLE);
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
			List<String> ruoli,String gestioneToken, 
			String policy, String validazioneInput, String introspection, String userInfo, String forward,
			String autorizzazione_token, String autorizzazione_tokenOptions,
			String autorizzazioneScope, String autorizzazioneScopeMatch, BinaryParameter allegatoXacmlPolicy,
			String autorizzazioneContenuto,
			String protocollo) throws Exception{
		try {
			
			// check token
			if(AutorizzazioneUtilities.STATO_ABILITATO.equals(gestioneToken)){
				
				if(StringUtils.isEmpty(policy) || policy.equals(CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO)){
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,	CostantiControlStation.LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN_POLICY));
					return false;
				}
				
				boolean validazioneInputB = !validazioneInput.equals(StatoFunzionalitaConWarning.DISABILITATO.getValue());
				boolean introspectionB = !introspection.equals(StatoFunzionalitaConWarning.DISABILITATO.getValue());
				boolean userInfoB = !userInfo.equals(StatoFunzionalitaConWarning.DISABILITATO.getValue());
				boolean forwardB = !forward.equals(StatoFunzionalita.DISABILITATO.getValue());
				
				if(!validazioneInputB && !introspectionB && !userInfoB && !forwardB) {
					StringBuffer sb = new StringBuffer();
					sb.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN_VALIDAZIONE_INPUT).append(", ");
					sb.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN_INTROSPECTION).append(", ");
					sb.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN_USERINFO).append(" o ");
					sb.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN_TOKEN_FORWARD);
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_POLICY_TOKEN_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_UNA_MODALITA, sb.toString()));
					return false;
				}
				
			}
			
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
						ServletUtils.isCheckBoxEnabled(autorizzazioneRuoli)==false && 
						ServletUtils.isCheckBoxEnabled(autorizzazioneScope)==false &&
						(autorizzazione_tokenOptions==null || "".equals(autorizzazione_tokenOptions))){
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_SELEZIONARE_ALMENO_UNA_MODALITÀ_DI_AUTORIZZAZIONE,
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
				
				if(ServletUtils.isCheckBoxEnabled(autorizzazione_token)) {
					if(autorizzazione_tokenOptions==null || "".equals(autorizzazione_tokenOptions)) {
						this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_TOKEN_OPTIONS_NON_INDICATI);
						return false;
					}
				}
				if(autorizzazione_tokenOptions!=null) {
					Scanner scanner = new Scanner(autorizzazione_tokenOptions);
					try {
						while (scanner.hasNextLine()) {
							String line = scanner.nextLine();
							if(line==null || line.trim().equals("")) {
								continue;
							}
							if(line.contains("=")==false) {
								this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_AUTORIZZAZIONE_TOKEN);
								return false;
							}
						}
					}finally {
						scanner.close();
					}
					
					if(this.checkLength(autorizzazione_tokenOptions, CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_OPTIONS,-1,4000)==false) {
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
					
					// se questo parametro e' diverso da null vuol dire che ho aggiornato il valore dell'allegato e devo validare il contenuto
					if(allegatoXacmlPolicy.getValue() != null) {
						IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
						Documento documento = new Documento();
						documento.setFile("xacmlPolicy");
						documento.setByteContenuto(allegatoXacmlPolicy.getValue());
						documento.setTipo(TipiDocumentoSicurezza.XACML_POLICY.getNome());
						documento.setRuolo(RuoliDocumento.specificaSicurezza.toString()); 
						ValidazioneResult valida = pf.createValidazioneDocumenti().valida (documento);
						if(!valida.isEsito()) {
							this.pd.setMessage(valida.getMessaggioErrore());
							return false;
						}
					} else {
						if(oggetto!=null){
							if(isPortaDelegata){
								PortaDelegata pd = (PortaDelegata) oggetto;
								if(StringUtils.isEmpty(pd.getXacmlPolicy())) {
									this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_POLICY_OBBLIGATORIA_CON_LA_NUOVA_AUTORIZZAZIONE, AutorizzazioneUtilities.STATO_XACML_POLICY));
									return false;
								}
							}else {
								PortaApplicativa pa = (PortaApplicativa) oggetto;
								if(StringUtils.isEmpty(pa.getXacmlPolicy())) {
									this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_POLICY_OBBLIGATORIA_CON_LA_NUOVA_AUTORIZZAZIONE, AutorizzazioneUtilities.STATO_XACML_POLICY));
									return false;
								}
							}
						}
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
					
					if((ServletUtils.isCheckBoxEnabled(autorizzazioneScope)==false) ){
						if(pd.getScope()!=null && pd.getScope().sizeScopeList()>0) {
							this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_SCOPE_PRESENTI_AUTORIZZAZIONE_DISABILITATA); 
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
						if(pa.getServiziApplicativiAutorizzati()!=null && pa.getServiziApplicativiAutorizzati().sizeServizioApplicativoList()>0) {
							this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_APPLICATIVI_PRESENTI_AUTORIZZAZIONE_DISABILITATA);
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
						if(pa.getServiziApplicativiAutorizzati()!=null && pa.getServiziApplicativiAutorizzati().sizeServizioApplicativoList()>0) {
							this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_APPLICATIVI_PRESENTI_AUTENTICAZIONE_MODIFICATA);
							return false;
						}
					}
					if((ServletUtils.isCheckBoxEnabled(autorizzazioneScope)==false) ){
						if(pa.getScope()!=null && pa.getScope().sizeScopeList()>0) {
							this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_SCOPE_PRESENTI_AUTORIZZAZIONE_DISABILITATA);
							return false;
						}
					}
				}
			}
			
			if(autorizzazioneContenuto!=null && !"".equalsIgnoreCase(autorizzazioneContenuto)) {
				if(this.checkLength255(autorizzazioneContenuto,
						CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI+" - "+CostantiControlStation.LABEL_PARAMETRO_AUTORIZZAZIONE_CONTENUTI)==false) {
					return false;
				}
			}
			
			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public String getLabelStatoControlloAccessi(
			String gestioneToken,
			String autenticazione, String autenticazioneOpzionale, String autenticazioneCustom,
			String autorizzazione, String autorizzazioneContenuti,String autorizzazioneCustom) {
		String label = CostantiControlStation.DEFAULT_VALUE_DISABILITATO;
		
		if(gestioneToken!=null && StatoFunzionalita.ABILITATO.getValue().equals(gestioneToken)) {
			return CostantiControlStation.DEFAULT_VALUE_ABILITATO;
		}
		
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
	
	public String getLabelStatoGestioneToken(String gestioneToken) {
		String label = CostantiControlStation.DEFAULT_VALUE_DISABILITATO;
		
		if(gestioneToken!=null && StatoFunzionalita.ABILITATO.getValue().equals(gestioneToken)) {
			return CostantiControlStation.DEFAULT_VALUE_ABILITATO;
		}
		return label;
	}
	
	public String getLabelStatoAutenticazione(String autenticazione, String autenticazioneOpzionale, String autenticazioneCustom) {
		String label = CostantiControlStation.DEFAULT_VALUE_DISABILITATO;
		
		if(autenticazione != null && !TipoAutenticazione.DISABILITATO.equals(autenticazione))
			return CostantiControlStation.DEFAULT_VALUE_ABILITATO;
		
		if(autenticazioneOpzionale != null && ServletUtils.isCheckBoxEnabled(autenticazioneOpzionale))
			return CostantiControlStation.DEFAULT_VALUE_ABILITATO;
		
		return label;
	}
	
	public String getLabelStatoAutorizzazione(String autorizzazione, String autorizzazioneContenuti,String autorizzazioneCustom) {
		String label = CostantiControlStation.DEFAULT_VALUE_DISABILITATO;
		
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
			String[] azioniDisponibiliList, String[] azioniDisponibiliLabelList, String[] azioni, ServiceBinding serviceBinding) {
		
		String label = this.getLabelAzioni(serviceBinding);
		
		DataElement de = new DataElement();
		de.setLabel(label);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		// Azione
		de = new DataElement();
		de.setLabel(label);
		de.setValues(azioniDisponibiliList);
		de.setLabels(azioniDisponibiliLabelList);
		de.setSelezionati(azioni);
		de.setType(DataElementType.MULTI_SELECT);
		de.setName(CostantiControlStation.PARAMETRO_AZIONI);
		de.setRows(15);
		de.setRequired(true); 
		dati.addElement(de);
		
		return dati;
	}
	
	// Prepara la lista di azioni delle porte
	public void preparePorteAzioneList(List<String> listaAzioniParam, String idPorta, Integer parentConfigurazione, List<Parameter> lstParametriBreadcrumbs, 
			String nomePorta, String objectName, List<Parameter> listaParametriSessione,
			String labelPerPorta, ServiceBinding serviceBinding, AccordoServizioParteComune aspc) throws Exception {
		try {
			ServletUtils.addListElementIntoSession(this.session, objectName,listaParametriSessione);

			// setto la barra del titolo

			String label = this.getLabelAzione(serviceBinding);
			
			ServletUtils.disabledPageDataSearch(this.pd);
			//this.pd.setSearchLabel(label);
			//this.pd.setSearchDescription("");
			this.pd.setPageSize(1000);
			
			List<String> listaAzioni = new ArrayList<>();
			HashMap<String, Resource> mapToResource = new HashMap<>();
			for (String azione : listaAzioniParam) {
				if(ServiceBinding.SOAP.equals(serviceBinding)) {
					listaAzioni.add(azione);
				}
				else {
					Resource risorsa = null;
					for (Resource resourceTmp : aspc.getResourceList()) {
						if(resourceTmp.getNome().equals(azione)) {
							risorsa = resourceTmp;
							break;
						}
					}
					String nomeRisorsaConPathPerOrderBy = 
							(risorsa.getPath()==null ? "*" : risorsa.getPath())
							+" " +
							(risorsa.getMethod()==null ? "ALL" : risorsa.getMethod())	;
					listaAzioni.add(nomeRisorsaConPathPerOrderBy);
					mapToResource.put(nomeRisorsaConPathPerOrderBy, risorsa);
				}
			}
			Collections.sort(listaAzioni);
			
			lstParametriBreadcrumbs.add(new Parameter(labelPerPorta,null));

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParametriBreadcrumbs.toArray(new Parameter[lstParametriBreadcrumbs.size()]));

			// setto le label delle colonne
			String[] labels = null;
			if(ServiceBinding.SOAP.equals(serviceBinding)) {
				labels = new String[1];
				labels[0] = label;
			}
			else {
				labels = new String[2];
				//labels[0] = label;
				//labels[0] = AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_RESOURCES_HTTP_METHOD;
				labels[0] = CostantiControlStation.LABEL_PARAMETRO_HTTP_METHOD_COMPACT;
				labels[1] = AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_RESOURCES_PATH;
			}
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (listaAzioni != null) {
				
				Iterator<String> it = listaAzioni.iterator();
				while (it.hasNext()) {
					String nomeAzione = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					if(ServiceBinding.SOAP.equals(serviceBinding)) {
						DataElement de = new DataElement();
						de.setValue(nomeAzione);
						de.setIdToRemove(nomeAzione);
						e.addElement(de);
					}
					else {
						Resource risorsa = mapToResource.get(nomeAzione);
						String labelParametroApcResourcesHttpMethodQualsiasi = AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_RESOURCES_HTTP_METHOD_QUALSIASI;
						//HTTP Method
						DataElement de = getDataElementHTTPMethodResource(risorsa, labelParametroApcResourcesHttpMethodQualsiasi);   
						e.addElement(de);
						
						de = new DataElement();
						if(risorsa.getPath()==null || "".equals(risorsa.getPath())) {
							de.setValue(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_RESOURCES_PATH_QUALSIASI);
						}
						else {
							de.setValue(risorsa.getPath());
						}
						de.setToolTip(risorsa.getNome());
						de.setIdToRemove(risorsa.getNome());
						e.addElement(de);
					}

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
	
	public DataElement getDataElementHTTPMethodResource(org.openspcoop2.core.registry.Resource risorsa,	String labelParametroApcResourcesHttpMethodQualsiasi) {
		DataElement de = new DataElement();

		String styleClass = "resource-method-block resource-method-default";
		if(risorsa.getMethod()==null) {
			de.setValue(labelParametroApcResourcesHttpMethodQualsiasi);
		}
		else {
			de.setValue(risorsa.getMethod().toString());
			
			switch (risorsa.getMethod()) {
			case DELETE:
				styleClass = "resource-method-block resource-method-delete";
				break;
			case GET:
				styleClass = "resource-method-block resource-method-get";
				break;
			case HEAD:
				styleClass = "resource-method-block resource-method-head";
				break;
			case LINK:
				styleClass = "resource-method-block resource-method-link";
				break;
			case OPTIONS:
				styleClass = "resource-method-block resource-method-options";
				break;
			case PATCH:
				styleClass = "resource-method-block resource-method-patch";
				break;
			case POST:
				styleClass = "resource-method-block resource-method-post";
				break;
			case PUT:
				styleClass = "resource-method-block resource-method-put";
				break;
			case TRACE:
				styleClass = "resource-method-block resource-method-trace";
				break;
			case UNLINK:
				styleClass = "resource-method-block resource-method-unlink";
				break;
			default:
				styleClass = "resource-method-block resource-method-default";
				break;
			}
		}
		de.setLabelStyleClass(styleClass); 
		de.setWidthPx(75);
		return de;
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
	
	public void addFilterScopeTipologia(String scopeTipologia, boolean postBack) throws Exception{
		try {
			String [] metodi = new String[2];
			metodi[0] = "interno"; //RuoloTipologia.INTERNO.getValue();
			metodi[1] = "esterno"; //RuoloTipologia.ESTERNO.getValue();
			String [] metodiLabel = new String[2];
			metodiLabel[0] = CostantiControlStation.SCOPE_TIPOLOGIA_LABEL_INTERNO;
			metodiLabel[1] = CostantiControlStation.SCOPE_TIPOLOGIA_LABEL_ESTERNO;
			String [] values = new String[metodi.length + 1];
			String [] labels = new String[metodi.length + 1];
			labels[0] = CostantiControlStation.LABEL_PARAMETRO_SCOPE_TIPOLOGIA_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SCOPE_TIPOLOGIA_QUALSIASI;
			for (int i =0; i < metodi.length ; i ++) {
				labels[i+1] = metodiLabel[i];
				values[i+1] = metodi[i];
			}
			
			String selectedValue = scopeTipologia != null ? scopeTipologia : CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SCOPE_TIPOLOGIA_QUALSIASI;
			
			String label = CostantiControlStation.LABEL_PARAMETRO_SCOPE_TIPOLOGIA;
			
			this.pd.addFilter(Filtri.FILTRO_SCOPE_TIPOLOGIA, label, selectedValue, values, labels, postBack, this.getSize());
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void addFilterScopeContesto(String scopeContesto, boolean postBack) throws Exception{
		try {
			String [] metodi = new String[2];
			metodi[0] = ScopeContesto.PORTA_APPLICATIVA.getValue();
			metodi[1] = ScopeContesto.PORTA_DELEGATA.getValue();
			String [] metodiLabel = new String[2];
			metodiLabel[0] = CostantiControlStation.SCOPE_CONTESTO_UTILIZZO_LABEL_EROGAZIONE;
			metodiLabel[1] = CostantiControlStation.SCOPE_CONTESTO_UTILIZZO_LABEL_FRUIZIONE;
			String [] values = new String[metodi.length + 1];
			String [] labels = new String[metodi.length + 1];
			labels[0] = CostantiControlStation.LABEL_PARAMETRO_SCOPE_CONTESTO_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SCOPE_CONTESTO_QUALSIASI;
			for (int i =0; i < metodi.length ; i ++) {
				labels[i+1] = metodiLabel[i];
				values[i+1] = metodi[i];
			}
			
			String selectedValue = scopeContesto != null ? scopeContesto : CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SCOPE_CONTESTO_QUALSIASI;
			
			String label = CostantiControlStation.LABEL_PARAMETRO_SCOPE_CONTESTO;
			
			this.pd.addFilter(Filtri.FILTRO_SCOPE_CONTESTO, label, selectedValue, values, labels, postBack, this.getSize());
			
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
	
	public void addFilterRuolo(String ruolo, boolean postBack) throws Exception{
		try {
			
			FiltroRicercaRuoli filtroRuoli = new FiltroRicercaRuoli();
			filtroRuoli.setContesto(RuoloContesto.QUALSIASI);
			filtroRuoli.setTipologia(RuoloTipologia.INTERNO);
			List<IDRuolo> listRuoli = this.ruoliCore.getAllIdRuoli(filtroRuoli);
			int length = 1;
			if(listRuoli!=null && listRuoli.size()>0) {
				length+=listRuoli.size();
			}
			String [] values = new String[length];
			String [] labels = new String[length];
			labels[0] = CostantiControlStation.LABEL_PARAMETRO_RUOLO_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_RUOLO_QUALSIASI;
			if(listRuoli!=null && listRuoli.size()>0) {
				for (int i =0; i < listRuoli.size() ; i ++) {
					labels[i+1] = listRuoli.get(i).getNome();
					values[i+1] = listRuoli.get(i).getNome();
				}
			}
			
			this.pd.addFilter(Filtri.FILTRO_RUOLO, RuoliCostanti.LABEL_RUOLO, ruolo, values, labels, postBack, this.getSize());
			
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
		List<String> protocolli = null;
		if(this.core.isUsedByApi()) {
			protocolli = ProtocolFactoryManager.getInstance().getProtocolNamesAsList();
		}else {
			protocolli = this.core.getProtocolli(this.session);
		}
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
		_addFilterProtocol(ricerca, idLista, protocolli);
	}
	
	public void addFilterProtocol(ISearch ricerca, int idLista,List<String> protocolli) throws Exception{
		_addFilterProtocol(ricerca, idLista, protocolli);
	}

	private void _addFilterProtocol(ISearch ricerca, int idLista, List<String> protocolli) throws Exception {
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
	
	
	// LABEL PROTOCOLLI
	
	public List<String> getLabelsProtocolli(List<String> protocolli) throws Exception{
		return NamingUtils.getLabelsProtocolli(protocolli);
	}
	
	public String getLabelProtocollo(String protocollo) throws Exception{
		return NamingUtils.getLabelProtocollo(protocollo);
	}
		
	public static String _getLabelProtocollo(String protocollo) throws Exception{
		return NamingUtils.getLabelProtocollo(protocollo);
	}
	
	public String getDescrizioneProtocollo(String protocollo) throws Exception{
		return NamingUtils.getDescrizioneProtocollo(protocollo);
	}
	
	public String getWebSiteProtocollo(String protocollo) throws Exception{
		return NamingUtils.getWebSiteProtocollo(protocollo);
	}
	
	
	// LABEL SOGGETTI
	
	public String getLabelNomeSoggetto(IDSoggetto idSoggetto) throws Exception{
		return NamingUtils.getLabelSoggetto(idSoggetto);
	}
	
	public static String _getLabelNomeSoggetto(IDSoggetto idSoggetto) throws Exception{
		return NamingUtils.getLabelSoggetto(idSoggetto);
	}
	public String getLabelNomeSoggetto(String protocollo, IDSoggetto idSoggetto) throws Exception{
		return NamingUtils.getLabelSoggetto(protocollo, idSoggetto);
	}
	public String getLabelNomeSoggetto(String protocollo, String tipoSoggetto, String nomeSoggetto) throws Exception{
		return NamingUtils.getLabelSoggetto(protocollo, tipoSoggetto, nomeSoggetto);
	}
	
	
	// LABEL API
	
	public String getLabelIdAccordo(AccordoServizioParteComune as) throws Exception{
		return NamingUtils.getLabelAccordoServizioParteComune(as);
	}
	public String getLabelIdAccordo(IDAccordo idAccordo) throws Exception{
		return NamingUtils.getLabelAccordoServizioParteComune(idAccordo);
	}
	public String getLabelIdAccordo(String protocollo, IDAccordo idAccordo) throws Exception{
		return NamingUtils.getLabelAccordoServizioParteComune(protocollo, idAccordo);
	}
	public String getLabelIdAccordoSenzaReferente(String protocollo, IDAccordo idAccordo) throws Exception{
		return NamingUtils.getLabelAccordoServizioParteComune(protocollo, idAccordo,false);
	}
	
	
	// LABEL SERVIZI
	
	public String getLabelNomeServizio(String protocollo, String tipoServizio, String nomeServizio, Integer versioneInt) throws Exception{
		return NamingUtils.getLabelAccordoServizioParteSpecificaSenzaErogatore(protocollo, tipoServizio, nomeServizio, versioneInt);
	}
	public String getLabelIdServizio(AccordoServizioParteSpecifica as) throws Exception{
		return NamingUtils.getLabelAccordoServizioParteSpecifica(as);
	}
	public String getLabelIdServizio(IDServizio idServizio) throws Exception{
		return NamingUtils.getLabelAccordoServizioParteSpecifica(idServizio);
	}
	public String getLabelIdServizio(String protocollo, IDServizio idServizio) throws Exception{
		return NamingUtils.getLabelAccordoServizioParteSpecifica(protocollo, idServizio);
	}
	public String getLabelIdServizioSenzaErogatore(String protocollo, IDServizio idServizio) throws Exception{
		return NamingUtils.getLabelAccordoServizioParteSpecificaSenzaErogatore(protocollo, idServizio);
	}
	public String getLabelIdServizioSenzaErogatore(IDServizio idServizio) throws Exception{
		return NamingUtils.getLabelAccordoServizioParteSpecificaSenzaErogatore(idServizio);
	}
	public String getLabelServizioFruizione(String protocollo, IDSoggetto idSoggettoFruitore, AccordoServizioParteSpecifica asps) throws Exception{
		return this.getLabelServizioFruizione(protocollo, idSoggettoFruitore, this.idServizioFactory.getIDServizioFromAccordo(asps));
	}
	public String getLabelServizioFruizione(String protocollo, IDSoggetto idSoggettoFruitore, IDServizio idServizio) throws Exception{
		String labelServizio = this.getLabelIdServizio(protocollo, idServizio);
		boolean showSoggettoFruitoreInFruizioni = this.core.isMultitenant() && 
				!this.isSoggettoMultitenantSelezionato();
		if(showSoggettoFruitoreInFruizioni) {
			String labelFruitore = this.getLabelNomeSoggetto(protocollo, idSoggettoFruitore);
			return labelFruitore + " -> " + labelServizio;
		}
		else {
			return labelServizio;
		}
	}
	public String getLabelServizioErogazione(String protocollo, AccordoServizioParteSpecifica asps) throws Exception{
		return this.getLabelServizioErogazione(protocollo, this.idServizioFactory.getIDServizioFromAccordo(asps));
	}
	public String getLabelServizioErogazione(String protocollo, IDServizio idServizio) throws Exception{
		boolean showSoggettoErogatoreInErogazioni = this.core.isMultitenant() && 
				!this.isSoggettoMultitenantSelezionato();
		if(showSoggettoErogatoreInErogazioni) {
			return this.getLabelIdServizio(protocollo, idServizio);
		}
		else {
			return this.getLabelIdServizioSenzaErogatore(protocollo, idServizio);
		}
		
	}
	
	// LABEL ACCORDI COOPERAZIONE
	
	public String getLabelIdAccordoCooperazione(AccordoCooperazione ac) throws Exception{
		return NamingUtils.getLabelAccordoCooperazione(ac);
	}
	public String getLabelIdAccordoCooperazione(IDAccordoCooperazione idAccordo) throws Exception{
		return NamingUtils.getLabelAccordoCooperazione(idAccordo);
	}
	public String getLabelIdAccordoCooperazione(String protocollo, IDAccordoCooperazione idAccordo) throws Exception{
		return NamingUtils.getLabelAccordoCooperazione(protocollo, idAccordo);
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
	
	public void addFilterAzione(Map<String,String> azioni, String azione, ServiceBinding serviceBinding) throws Exception{
		String[] azioniDisponibiliList = new String [0];
		String[] azioniDisponibiliLabelList = new String [0];
		if(azioni!=null && azioni.size()>0) {
			azioniDisponibiliList = new String[azioni.size()];
			azioniDisponibiliLabelList = new String[azioni.size()];
			int i = 0;
			for (String string : azioni.keySet()) {
				azioniDisponibiliList[i] = string;
				azioniDisponibiliLabelList[i] = azioni.get(string);
				if("Qualsiasi".equals(azioniDisponibiliLabelList[i])) {
					azioniDisponibiliLabelList[i] = "Method e Path Qualsiasi";
				}
				i++;
			}
		}
		
		this.addFilterAzione(azioniDisponibiliList,azioniDisponibiliLabelList, azione, serviceBinding);		  
	}
	
	public void addFilterAzione(String []azioni, String []azioniLabels, String azione, ServiceBinding serviceBinding) throws Exception{
		try {
			String [] values = new String[azioni.length + 1];
			String [] labels = new String[azioni.length + 1];
			labels[0] = CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_AZIONE_NON_SELEZIONATA;
			for (int i =0; i < azioni.length ; i ++) {
				labels[i+1] = azioniLabels[i];
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
			String labelsStato [] = {this.getDumpLabelDefault(true), CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO_RIDEFINITO};
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
	
	protected String getGestioneCorsLabelDefault(boolean usePrefixDefault) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		
		StringBuffer bf = new StringBuffer();
		if(usePrefixDefault) {
			bf.append(CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO_DEFAULT);
			bf.append(" (");
		}
		CorsConfigurazione cc = this.confCore.getConfigurazioneGenerale().getGestioneCors();
		if(cc==null || cc.getStato()==null) {
			bf.append(StatoFunzionalita.DISABILITATO.getValue());
		}
		else {
			if(StatoFunzionalita.DISABILITATO.equals(cc.getStato())) {
				bf.append(cc.getStato().getValue());
			}
			else {
				bf.append(getLabelTipoGestioneCors(cc.getTipo()));
			}
		}
		if(usePrefixDefault) {
			bf.append(")");
		}
		return bf.toString();
	}
	
	protected String getLabelTipoGestioneCors(TipoGestioneCORS tipo) {
		if(tipo==null || "".equals(tipo.toString())) {
			return "undefined";
		}
		if(TipoGestioneCORS.GATEWAY.equals(tipo)) {
			return StatoFunzionalita.ABILITATO.getValue();
		}
		else {
			return StatoFunzionalita.ABILITATO.getValue()+" (applicativo)";
		}
	}
	
	protected String getResponseCachingLabelDefault(boolean usePrefixDefault) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		
		StringBuffer bf = new StringBuffer();
		if(usePrefixDefault) {
			bf.append(CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO_DEFAULT);
			bf.append(" (");
		}
		ResponseCachingConfigurazioneGenerale rg = this.confCore.getConfigurazioneGenerale().getResponseCaching();
		if(rg==null || rg.getConfigurazione()==null || rg.getConfigurazione().getStato()==null) {
			bf.append(StatoFunzionalita.DISABILITATO.getValue());
		}
		else {
			bf.append(rg.getConfigurazione().getStato().getValue());
		}
		if(usePrefixDefault) {
			bf.append(")");
		}
		return bf.toString();
	}
	
	protected String getDumpLabelDefault(boolean usePrefixDefault) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		String labelDefault = CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO_DEFAULT;
		
		Dump dConfig = this.confCore.getConfigurazioneGenerale().getDump();
		if(dConfig==null || dConfig.getConfigurazione()==null) {
			if(usePrefixDefault) {
				labelDefault = CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO_DEFAULT +" (disabilitato)";
			}
			else {
				labelDefault = "disabilitato";
			}
		}
		else {
			boolean richiesta = (
						dConfig.getConfigurazione().getRichiestaIngresso()!=null 
						&& 
						(
								StatoFunzionalita.ABILITATO.equals(dConfig.getConfigurazione().getRichiestaIngresso().getHeaders())
								||
								StatoFunzionalita.ABILITATO.equals(dConfig.getConfigurazione().getRichiestaIngresso().getBody())
								||
								StatoFunzionalita.ABILITATO.equals(dConfig.getConfigurazione().getRichiestaIngresso().getAttachments())
						)
					)
					||
					(
						dConfig.getConfigurazione().getRichiestaUscita()!=null 
						&& 
						(
								StatoFunzionalita.ABILITATO.equals(dConfig.getConfigurazione().getRichiestaUscita().getHeaders())
								||
								StatoFunzionalita.ABILITATO.equals(dConfig.getConfigurazione().getRichiestaUscita().getBody())
								||
								StatoFunzionalita.ABILITATO.equals(dConfig.getConfigurazione().getRichiestaUscita().getAttachments())
						)
						)
					;
			boolean risposta = (
					dConfig.getConfigurazione().getRispostaIngresso()!=null 
					&& 
					(
							StatoFunzionalita.ABILITATO.equals(dConfig.getConfigurazione().getRispostaIngresso().getHeaders())
							||
							StatoFunzionalita.ABILITATO.equals(dConfig.getConfigurazione().getRispostaIngresso().getBody())
							||
							StatoFunzionalita.ABILITATO.equals(dConfig.getConfigurazione().getRispostaIngresso().getAttachments())
					)
				)
				||
				(
					dConfig.getConfigurazione().getRispostaUscita()!=null 
					&& 
					(
							StatoFunzionalita.ABILITATO.equals(dConfig.getConfigurazione().getRispostaUscita().getHeaders())
							||
							StatoFunzionalita.ABILITATO.equals(dConfig.getConfigurazione().getRispostaUscita().getBody())
							||
							StatoFunzionalita.ABILITATO.equals(dConfig.getConfigurazione().getRispostaUscita().getAttachments())
					)
					)
				;
			if(richiesta && risposta) {
				if(usePrefixDefault) {
					labelDefault = CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO_DEFAULT +" (abilitato)";
				}
				else {
					labelDefault = "abilitato";
				}
			}
			else if(richiesta) {
				if(usePrefixDefault) {
					labelDefault = CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO_DEFAULT +" (abilitato richiesta)";
				}
				else {
					labelDefault = "abilitato richiesta";
				}
			}
			else if(risposta) {
				if(usePrefixDefault) {
					labelDefault = CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO_DEFAULT +" (abilitato risposta)";
				}
				else {
					labelDefault = "abilitato risposta";
				}
			}
			else {
				if(usePrefixDefault) {
					labelDefault = CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO_DEFAULT +" (disabilitato)";
				}
				else {
					labelDefault = "disabilitato";
				}
			}
			
		}
		return labelDefault;
	}
	
	public void addConfigurazioneDumpToDatiAsHidden(TipoOperazione tipoOperazione,Vector<DataElement> dati, boolean showStato, String statoDump, boolean showRealtime, String realtime, String statoDumpRichiesta, String statoDumpRisposta,
			String dumpRichiestaIngressoHeader, String dumpRichiestaIngressoBody, String dumpRichiestaIngressoAttachments, 
			String dumpRichiestaUscitaHeader, String dumpRichiestaUscitaBody, String dumpRichiestaUscitaAttachments, 
			String dumpRispostaIngressoHeader, String dumpRispostaIngressoBody , String dumpRispostaIngressoAttachments,
			String dumpRispostaUscitaHeader, String dumpRispostaUscitaBody, String dumpRispostaUscitaAttachments) throws Exception{
		
		// stato generale dump
		DataElement de = new DataElement();
		de.setName(CostantiControlStation.PARAMETRO_DUMP_STATO); 
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO);
		de.setType(DataElementType.HIDDEN);
		de.setValue(statoDump);
		dati.addElement(de);
		
		if(!showStato || statoDump.equals(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO)) {
		
			// Realtime
			de = new DataElement();
			de.setName(CostantiControlStation.PARAMETRO_DUMP_REALTIME); 
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_REALTIME);
			de.setType(DataElementType.HIDDEN);
			de.setValue(realtime);
			dati.addElement(de);
			
			// Stato Dump Richiesta
			de = new DataElement();
			de.setName(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_STATO); 
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_STATO);
			de.setType(DataElementType.HIDDEN);
			de.setValue(statoDumpRichiesta);
			dati.addElement(de);
			
			if(statoDumpRichiesta.equals(StatoFunzionalita.ABILITATO.getValue())) {
				// header ingresso
				de = new DataElement();
				de.setName(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_HEADERS);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_HEADERS);
				de.setType(DataElementType.HIDDEN);
				de.setValue(dumpRichiestaIngressoHeader);
				dati.addElement(de);
				
				// body ingresso
				de = new DataElement();
				de.setName(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_BODY);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_BODY);
				de.setType(DataElementType.HIDDEN);
				de.setValue(dumpRichiestaIngressoBody);
				dati.addElement(de);
				
				// attachments ingresso
				de = new DataElement();
				de.setName(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_ATTACHMENTS);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_ATTACHMENTS);
				de.setType(DataElementType.HIDDEN);
				de.setValue(dumpRichiestaIngressoAttachments);
				dati.addElement(de);
				
				// header ingresso
				de = new DataElement();
				de.setName(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_HEADERS);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_USCITA_HEADERS);
				de.setType(DataElementType.HIDDEN);
				de.setValue(dumpRichiestaUscitaHeader);
				dati.addElement(de);
				
				// body ingresso
				de = new DataElement();
				de.setName(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_BODY);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_USCITA_BODY);
				de.setType(DataElementType.HIDDEN);
				de.setValue(dumpRichiestaUscitaBody);
				dati.addElement(de);
				
				// attachments ingresso
				de = new DataElement();
				de.setName(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_ATTACHMENTS);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_USCITA_ATTACHMENTS);
				de.setType(DataElementType.HIDDEN);
				de.setValue(dumpRichiestaUscitaAttachments);
				dati.addElement(de);

			}
			

			// Stato Dump Richiesta
			de = new DataElement();
			de.setName(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_STATO); 
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_STATO);
			de.setType(DataElementType.HIDDEN);
			de.setValue(statoDumpRisposta);
			dati.addElement(de);
			
			if(statoDumpRisposta.equals(StatoFunzionalita.ABILITATO.getValue())) {
				// header ingresso
				de = new DataElement();
				de.setName(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_HEADERS);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_INGRESSO_HEADERS);
				de.setType(DataElementType.HIDDEN);
				de.setValue(dumpRispostaIngressoHeader);
				dati.addElement(de);
				
				// body ingresso
				de = new DataElement();
				de.setName(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_BODY);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_INGRESSO_BODY);
				de.setType(DataElementType.HIDDEN);
				de.setValue(dumpRispostaIngressoBody);
				dati.addElement(de);
				
				// attachments ingresso
				de = new DataElement();
				de.setName(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_ATTACHMENTS);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_INGRESSO_ATTACHMENTS);
				de.setType(DataElementType.HIDDEN);
				de.setValue(dumpRispostaIngressoAttachments);
				dati.addElement(de);
				
				
				// header ingresso
				de = new DataElement();
				de.setName(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_HEADERS);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_USCITA_HEADERS);
				de.setType(DataElementType.HIDDEN);
				de.setValue(dumpRispostaUscitaHeader);
				dati.addElement(de);
				
				// body ingresso
				de = new DataElement();
				de.setName(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_BODY);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_USCITA_BODY);
				de.setType(DataElementType.HIDDEN);
				de.setValue(dumpRispostaUscitaBody);
				dati.addElement(de);
				
				// attachments ingresso
				de = new DataElement();
				de.setName(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_ATTACHMENTS);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_USCITA_ATTACHMENTS);
				de.setType(DataElementType.HIDDEN);
				de.setValue(dumpRispostaUscitaAttachments);
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
				
				// se e' abilitato il dump per la richiesta almeno una singola voce deve essere abilitata
				if(dumpRichiestaIngressoHeader.equals(StatoFunzionalita.DISABILITATO.getValue()) && dumpRichiestaIngressoBody.equals(StatoFunzionalita.DISABILITATO.getValue()) 
						&& dumpRichiestaIngressoAttachments.equals(StatoFunzionalita.DISABILITATO.getValue())
						&& dumpRichiestaUscitaHeader.equals(StatoFunzionalita.DISABILITATO.getValue()) && dumpRichiestaUscitaBody.equals(StatoFunzionalita.DISABILITATO.getValue()) 
						&& dumpRichiestaUscitaAttachments.equals(StatoFunzionalita.DISABILITATO.getValue())) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMP_DATI_INCOMPLETI_E_NECESSARIO_ABILITARE_UNA_VOCE, CostantiControlStation.LABEL_PARAMETRO_RICHIESTA));
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
				
				// se e' abilitato il dump per la risposta almeno una singola voce deve essere abilitata
				if(dumpRispostaIngressoHeader.equals(StatoFunzionalita.DISABILITATO.getValue()) && dumpRispostaIngressoBody.equals(StatoFunzionalita.DISABILITATO.getValue()) 
						&& dumpRispostaIngressoAttachments.equals(StatoFunzionalita.DISABILITATO.getValue())
						&& dumpRispostaUscitaHeader.equals(StatoFunzionalita.DISABILITATO.getValue()) && dumpRispostaUscitaBody.equals(StatoFunzionalita.DISABILITATO.getValue()) 
						&& dumpRispostaUscitaAttachments.equals(StatoFunzionalita.DISABILITATO.getValue())) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMP_DATI_INCOMPLETI_E_NECESSARIO_ABILITARE_UNA_VOCE, CostantiControlStation.LABEL_PARAMETRO_RISPOSTA));
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
		
		if(configurazione == null)
			return false;
		
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
		ConfigBean oldConfigurazione = ServletUtils.readConfigurazioneBeanFromSession(this.session, configurazione.getId());
		
		for (String key : configurazione.getListakeys()) {
			Boolean oldItemVisible = oldConfigurazione != null ? oldConfigurazione.getItem(key).getVisible() : null;
			configurazione.getItem(key).setOldVisible(oldItemVisible); 
			configurazione.getItem(key).setValueFromRequest(this.getParameter(key)); 
		}
	}
	public Vector<DataElement> addPropertiesConfigToDati(TipoOperazione tipoOperazione, Vector<DataElement> dati, String configName, ConfigBean configurazioneBean) throws Exception {
		return addPropertiesConfigToDati(tipoOperazione, dati, configName, configurazioneBean, true);
	}
	
	public Vector<DataElement> addPropertiesConfigToDati(TipoOperazione tipoOperazione, Vector<DataElement> dati, String configName, ConfigBean configurazioneBean, boolean addHiddenConfigName) throws Exception {
		if(addHiddenConfigName) {
			DataElement de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PROPERTIES_CONFIG_NAME);
			de.setValue(configName);
			de.setType(DataElementType.HIDDEN);
			de.setName(CostantiControlStation.PARAMETRO_PROPERTIES_CONFIG_NAME);
			dati.addElement(de);
		}
		if(configurazioneBean != null) {
			for (BaseItemBean<?> item : configurazioneBean.getListaItem()) {
				if(item.isVisible())
					dati.addElement(item.toDataElement());
			}
		}
		
		return dati;
	}
	
	public boolean checkPropertiesConfigurationData(TipoOperazione tipoOperazione,ConfigBean configurazioneBean, Config config) throws Exception{
		// Controlli sui campi immessi
		try {
			configurazioneBean.validazioneInputUtente(config);
			return true;
		}catch(UserInputValidationException e) {
			this.pd.setMessage(e.getMessage());  
			return false;
		}catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			this.pd.setMessage("Si &egrave; verificato un errore durante la validazione dello Schema Sicurezza, impossibile caricare il plugin di validazione previsto dalla configurazione"); 
			return false;		
		} catch(ProviderException e) {
			this.pd.setMessage("Si &egrave; verificato un errore durante la validazione dello Schema Sicurezza, impossibile utilizzare il plugin di validazione previsto dalla configurazione"); 
			return false;
		} catch(ProviderValidationException e) {
			this.pd.setMessage(e.getMessage());  
			return false;
		}
	}
	
	public boolean isFirstTimeFromHttpParameters(String firstTimeParameter) throws Exception{
		
		String tmp = this.getParameter(firstTimeParameter);
		if(tmp!=null && !"".equals(tmp.trim())){
			return "true".equals(tmp.trim());
		}
		return true;
		
	}
	
	public void addToDatiFirstTimeDisabled(Vector<DataElement> dati,String firstTimeParameter){
		DataElement de = new DataElement();
		de.setName(firstTimeParameter);
		de.setType(DataElementType.HIDDEN);
		de.setValue("false");
		dati.addElement(de);
	}
	
	public boolean hasOnlyPermessiDiagnosticaReportistica(User user) throws Exception {
		PermessiUtente pu = user.getPermessi();
		Boolean singlePdD = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_SINGLE_PDD);

		String isServizi = (pu.isServizi() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED);
		String isDiagnostica = (pu.isDiagnostica() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED);
		String isReportistica = (pu.isReportistica() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED);
		String isSistema = (pu.isSistema() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED);
		String isMessaggi = (pu.isCodeMessaggi() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED);
		String isUtenti = (pu.isUtenti() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED);
		String isAuditing = (pu.isAuditing() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED);
		String isAccordiCooperazione = (pu.isAccordiCooperazione() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED);
		
		return this.hasOnlyPermessiDiagnosticaReportistica(isServizi, isDiagnostica, isReportistica, isSistema, isMessaggi, isUtenti, isAuditing, isAccordiCooperazione, singlePdD);

	}
	
	public boolean hasOnlyPermessiDiagnosticaReportistica(String isServizi,String isDiagnostica,String isReportistica,String isSistema,String isMessaggi,
			String isUtenti,String isAuditing, String isAccordiCooperazione,boolean singlePdD) {
		return (((isServizi == null) || !ServletUtils.isCheckBoxEnabled(isServizi)) &&
				(
						!singlePdD 
						|| 
						checkPermessiDiagnosticaReportistica(isDiagnostica, isReportistica, singlePdD)
				) &&
				((isSistema == null) || !ServletUtils.isCheckBoxEnabled(isSistema)) &&
				((isMessaggi == null) || !ServletUtils.isCheckBoxEnabled(isMessaggi)) &&
				((isUtenti != null) || !ServletUtils.isCheckBoxEnabled(isUtenti)) &&
				((isAuditing == null) || !ServletUtils.isCheckBoxEnabled(isAuditing)) &&
				((isAccordiCooperazione == null) || !ServletUtils.isCheckBoxEnabled(isAccordiCooperazione)));
	}

	private boolean checkPermessiDiagnosticaReportistica(String isDiagnostica, String isReportistica, boolean singlePdD) {
		return singlePdD 
		&& 
		(
				(isDiagnostica == null) || ServletUtils.isCheckBoxEnabled(isDiagnostica)
		)
		||
		(
				(isReportistica == null) || ServletUtils.isCheckBoxEnabled(isReportistica)
		);
	}
	
	public String readConfigurazioneRegistrazioneEsitiFromHttpParameters(String configurazioneEsiti, boolean first) throws Exception {
		
		
		StringBuffer bf = new StringBuffer();
		EsitiProperties esiti = EsitiConfigUtils.getEsitiPropertiesForConfiguration(ControlStationCore.getLog());
		List<Integer> esitiCodes = esiti.getEsitiCode();
		if(esitiCodes!=null){
			for (Integer esito : esitiCodes) {
				String esitoParam = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_STATO+esito);
				boolean checked = ServletUtils.isCheckBoxEnabled(esitoParam);
				if(checked){
					if(bf.length()>0){
						bf.append(",");
					}
					bf.append(esito);
				}
			}
		}
		if(bf.length()>0){
			return bf.toString();
		}
		else{
			if(first==false){
				return null;
			}
			else{
				if(configurazioneEsiti == null || "".equals(configurazioneEsiti.trim())){
					// creo un default composto da tutti ad eccezione dell'esito 84 (MaxThreads)
					this.getRegistrazioneEsiti(configurazioneEsiti, bf);
					if(bf.length()>0){
						return bf.toString();
					}
					else{
						return null;
					}
				}
			}
		}
		return configurazioneEsiti;
	}
	
	public List<String> getRegistrazioneEsiti(String configurazioneEsiti, StringBuffer bf) throws Exception{
		
		EsitiProperties esiti = EsitiConfigUtils.getEsitiPropertiesForConfiguration(ControlStationCore.getLog());
		return EsitiConfigUtils.getRegistrazioneEsiti(configurazioneEsiti, ControlStationCore.getLog(), bf, esiti);
		
	}
	
	public boolean isCompleteEnabled(List<String> attivi, List<Integer> listCheck) {

		boolean all = true;
		for (int i = 0; i < listCheck.size(); i++) {
			String okString = listCheck.get(i).intValue()+"";
			if(attivi.contains(okString)==false) {
				all = false;
				break;
			}
		}
		return all;
	}
	
	public boolean isCompleteDisabled(List<String> attivi, List<Integer> listCheck) {

		for (int i = 0; i < listCheck.size(); i++) {
			String okString = listCheck.get(i).intValue()+"";
			if(attivi.contains(okString)) {
				return false;
			}
		}
		return true;
	}
	
	public List<Integer> getListaEsitiFalliteSenzaMaxThreads(EsitiProperties esiti) throws ProtocolException{
		List<Integer> listFallite = esiti.getEsitiCodeKo_senzaFaultApplicativo();
		int esitoViolazione = esiti.convertoToCode(EsitoTransazioneName.CONTROLLO_TRAFFICO_MAX_THREADS);
		List<Integer> listFalliteSenzaMax = new ArrayList<>(); 
		int i = 0;
		for (; i < listFallite.size(); i++) {
			if(listFallite.get(i).intValue() != esitoViolazione) {
				listFalliteSenzaMax.add(listFallite.get(i));
			}
		}
		return listFalliteSenzaMax;
	}
	
	public List<Integer> getListaEsitiOkSenzaCors(EsitiProperties esiti) throws ProtocolException{
		List<Integer> listOk = esiti.getEsitiCodeOk_senzaFaultApplicativo();
		int esitoCorsGateway = esiti.convertoToCode(EsitoTransazioneName.CORS_PREFLIGHT_REQUEST_VIA_GATEWAY);
		int esitoCorsTrasparente = esiti.convertoToCode(EsitoTransazioneName.CORS_PREFLIGHT_REQUEST_TRASPARENTE);
		List<Integer> listOkSenzaCors = new ArrayList<>(); 
		int i = 0;
		for (; i < listOk.size(); i++) {
			if((listOk.get(i).intValue() != esitoCorsGateway) && (listOk.get(i).intValue() != esitoCorsTrasparente)) {
				listOkSenzaCors.add(listOk.get(i));
			}
		}
		return listOkSenzaCors;
	}
	
	public List<Integer> getListaEsitiCors(EsitiProperties esiti) throws ProtocolException{
		int esitoCorsGateway = esiti.convertoToCode(EsitoTransazioneName.CORS_PREFLIGHT_REQUEST_VIA_GATEWAY);
		int esitoCorsTrasparente = esiti.convertoToCode(EsitoTransazioneName.CORS_PREFLIGHT_REQUEST_TRASPARENTE);
		List<Integer> listCors = new ArrayList<>();
		listCors.add(esitoCorsGateway);
		listCors.add(esitoCorsTrasparente);
		return listCors;
	}
	
	public void addToDatiRegistrazioneEsiti(Vector<DataElement> dati, TipoOperazione tipoOperazione, 
			String tracciamentoEsitiStato,
			String nuovaConfigurazioneEsiti,
			String tracciamentoEsitiSelezionePersonalizzataOk, String tracciamentoEsitiSelezionePersonalizzataFault, 
			String tracciamentoEsitiSelezionePersonalizzataFallite, String tracciamentoEsitiSelezionePersonalizzataMax,
			String tracciamentoEsitiSelezionePersonalizzataCors) throws Exception {
		
	
		DataElement de = new DataElement();
		//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
				
		if(tracciamentoEsitiStato!=null) {
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_STATO);
			de.setName(CostantiControlStation.PARAMETRO_PORTE_TRACCIAMENTO_ESITO);
			de.setType(DataElementType.SELECT);
			String valuesStato [] = {CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT, CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO};
			String labelsStato [] = {CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO_DEFAULT, CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO_RIDEFINITO};
			de.setSelected(tracciamentoEsitiStato);
			de.setLabels(labelsStato);
			de.setValues(valuesStato); 
			de.setPostBack(true);
			dati.addElement(de);
		}
		
		if(tracciamentoEsitiStato==null || CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO.equals(tracciamentoEsitiStato) ) {
			de = new DataElement();
			//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
			de.setValue(ConfigurazioneCostanti.LABEL_NOTE_CONFIGURAZIONE_REGISTRAZIONE_ESITI);
			de.setType(DataElementType.NOTE);
			dati.addElement(de);
			
			List<String> attivi = new ArrayList<String>();
			if(nuovaConfigurazioneEsiti!=null){
				String [] tmp = nuovaConfigurazioneEsiti.split(",");
				if(tmp!=null){
					for (int i = 0; i < tmp.length; i++) {
						attivi.add(tmp[i].trim());
					}
				}
			}
			
			
			EsitiProperties esiti = EsitiConfigUtils.getEsitiPropertiesForConfiguration(ControlStationCore.getLog());
			
			List<String> values = new ArrayList<>();
			values.add(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO);
			values.add(ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO);
			values.add(ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO);
			
			List<String> values_senza_personalizzato = new ArrayList<>();
			values_senza_personalizzato.add(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO);
			values_senza_personalizzato.add(ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO);
			
			
			// ok
			
			List<Integer> listOk = getListaEsitiOkSenzaCors(esiti);
			
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_OK);
			//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
					
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_STATO);
			//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_OK);
			de.setType(DataElementType.SELECT);
			de.setValues(values);
			de.setLabels(values);
			de.setSelected(tracciamentoEsitiSelezionePersonalizzataOk);
			de.setPostBack(true);
			dati.addElement(de);
					
			if(ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO.equals(tracciamentoEsitiSelezionePersonalizzataOk) ||
					ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO.equals(tracciamentoEsitiSelezionePersonalizzataOk)) {
				for (Integer esito : listOk) {
					
					EsitoTransazioneName esitoTransactionName = esiti.getEsitoTransazioneName(esito);
					boolean integrationManagerSpecific = EsitoTransazioneName.isIntegrationManagerSpecific(esitoTransactionName);		
					
					de = new DataElement();
					de.setLabelRight(esiti.getEsitoLabel(esito));
					//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
	//				de.setNote(esiti.getEsitoLabel(esito));
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_STATO+esito);
					if(ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO.equals(tracciamentoEsitiSelezionePersonalizzataOk)) {
						if(integrationManagerSpecific && this.isModalitaStandard()) {
							de.setType(DataElementType.HIDDEN);
							de.setValue(attivi.contains((esito+""))+"");
						}
						else {
							de.setType(DataElementType.CHECKBOX);
							de.setSelected(attivi.contains((esito+"")));
						}
					}
					else {
						de.setType(DataElementType.HIDDEN);
						de.setValue("true");
					}
					dati.addElement(de);
				}
			}
			
			
			
			// fault
			
			List<Integer> listFault = esiti.getEsitiCodeFaultApplicativo();
			
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_FAULT);
			//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
					
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_STATO);
			//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_FAULT);
			de.setType(DataElementType.SELECT);
			if(listFault.size()>1) {
				de.setValues(values);
				de.setLabels(values);
			}
			else {
				de.setValues(values_senza_personalizzato);
				de.setLabels(values_senza_personalizzato);
			}
			de.setSelected(tracciamentoEsitiSelezionePersonalizzataFault);
			de.setPostBack(true);
			dati.addElement(de);
					
			if(ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO.equals(tracciamentoEsitiSelezionePersonalizzataFault) ||
					ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO.equals(tracciamentoEsitiSelezionePersonalizzataFault)) {
				for (Integer esito : listFault) {
					de = new DataElement();
					de.setLabelRight(esiti.getEsitoLabel(esito));
					//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
	//						de.setNote(esiti.getEsitoLabel(esito));
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_STATO+esito);
					if(ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO.equals(tracciamentoEsitiSelezionePersonalizzataFault)) {
						de.setType(DataElementType.CHECKBOX);
						de.setSelected(attivi.contains((esito+"")));
					}
					else {
						de.setType(DataElementType.HIDDEN);
						de.setValue("true");
					}
					dati.addElement(de);
				}
			}
			
			
			
			// fallite
			
			List<Integer> listFalliteSenzaMax = getListaEsitiFalliteSenzaMaxThreads(esiti);
			
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_FALLITE);
			//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
					
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_STATO);
			//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_FALLITE);
			de.setType(DataElementType.SELECT);
			de.setValues(values);
			de.setLabels(values);
			de.setSelected(tracciamentoEsitiSelezionePersonalizzataFallite);
			de.setPostBack(true);
			dati.addElement(de);
					
			if(ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO.equals(tracciamentoEsitiSelezionePersonalizzataFallite) ||
					ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO.equals(tracciamentoEsitiSelezionePersonalizzataFallite)) {
				for (Integer esito : listFalliteSenzaMax) {
					
					EsitoTransazioneName esitoTransactionName = esiti.getEsitoTransazioneName(esito);
					boolean integrationManagerSpecific = EsitoTransazioneName.isIntegrationManagerSpecific(esitoTransactionName);		
					
					de = new DataElement();
					de.setLabelRight(esiti.getEsitoLabel(esito));
					//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
	//						de.setNote(esiti.getEsitoLabel(esito));
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_STATO+esito);
					if(ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO.equals(tracciamentoEsitiSelezionePersonalizzataFallite)) {
						if(integrationManagerSpecific && this.isModalitaStandard()) {
							de.setType(DataElementType.HIDDEN);
							de.setValue(attivi.contains((esito+""))+"");
						}
						else {
							de.setType(DataElementType.CHECKBOX);
							de.setSelected(attivi.contains((esito+"")));
						}
					}
					else {
						de.setType(DataElementType.HIDDEN);
						de.setValue("true");
					}
					dati.addElement(de);
				}
			}
					
			
			
			
			// max
			
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_MAX_REQUESTS);
			//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
			
			String esitoViolazioneAsString = esiti.convertoToCode(EsitoTransazioneName.CONTROLLO_TRAFFICO_MAX_THREADS) + "";
			
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_STATO);
			//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_MAX_REQUEST);
			de.setType(DataElementType.SELECT);
			de.setValues(values_senza_personalizzato);
			de.setLabels(values_senza_personalizzato);
			de.setSelected(tracciamentoEsitiSelezionePersonalizzataMax);
			de.setPostBack(true);
			dati.addElement(de);
			
			if(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO.equals(tracciamentoEsitiSelezionePersonalizzataMax)) {
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_STATO+esitoViolazioneAsString);
				de.setType(DataElementType.HIDDEN);
				de.setValue("true");
				dati.addElement(de);
			}
			
			
			
			
			// cors
			
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_CORS);
			//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_STATO);
			//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_CORS);
			de.setType(DataElementType.SELECT);
			de.setValues(values);
			de.setLabels(values);
			de.setSelected(tracciamentoEsitiSelezionePersonalizzataCors);
			de.setPostBack(true);
			dati.addElement(de);
					
			if(ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO.equals(tracciamentoEsitiSelezionePersonalizzataCors) ||
					ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO.equals(tracciamentoEsitiSelezionePersonalizzataCors)) {
				
				List<Integer> listCors = this.getListaEsitiCors(esiti);
				
				for (Integer esito : listCors) {
					
					EsitoTransazioneName esitoTransactionName = esiti.getEsitoTransazioneName(esito);
					boolean integrationManagerSpecific = EsitoTransazioneName.isIntegrationManagerSpecific(esitoTransactionName);		
					
					de = new DataElement();
					de.setLabelRight(esiti.getEsitoLabel(esito));
					//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
	//				de.setNote(esiti.getEsitoLabel(esito));
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_STATO+esito);
					if(ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO.equals(tracciamentoEsitiSelezionePersonalizzataCors)) {
						if(integrationManagerSpecific && this.isModalitaStandard()) {
							de.setType(DataElementType.HIDDEN);
							de.setValue(attivi.contains((esito+""))+"");
						}
						else {
							de.setType(DataElementType.CHECKBOX);
							de.setSelected(attivi.contains((esito+"")));
						}
					}
					else {
						de.setType(DataElementType.HIDDEN);
						de.setValue("true");
					}
					dati.addElement(de);
				}
			}
			
		}

	}
	
	public void addToDatiRegistrazioneTransazione(Vector<DataElement> dati, TipoOperazione tipoOperazione, 
			String transazioniTempiElaborazione, String transazioniToken) throws Exception {
		
		if(!this.isModalitaStandard()) {
			
			DataElement de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_INFORMAZIONI_TRANSAZIONE);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
			
		}
			
		List<String> values = new ArrayList<>();
		values.add(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO);
		values.add(ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO);
			
		DataElement de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_INFORMAZIONI_TRANSAZIONE_TEMPI_ELABORAZIONE);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_TRANSAZIONE_TEMPI);
		if(!this.isModalitaStandard()) {
			de.setType(DataElementType.SELECT);
			de.setValues(values);
			de.setLabels(values);
			de.setSelected(transazioniTempiElaborazione);
		}
		else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(transazioniTempiElaborazione);
		}
		dati.addElement(de);	
		
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_INFORMAZIONI_TRANSAZIONE_TOKEN);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_TRANSAZIONE_TOKEN);
		if(!this.isModalitaStandard()) {
			de.setType(DataElementType.SELECT);
			de.setValues(values);
			de.setLabels(values);
			de.setSelected(transazioniToken);
		}
		else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(transazioniToken);
		}
		dati.addElement(de);	
		
	}
	
	public void addSeveritaMessaggiDiagnosticiToDati(String severita, String severita_log4j, Vector<DataElement> dati) {
		
		DataElement de;
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_MESSAGGI_DIAGNOSTICI);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		//					String[] tipoMsg = { "off", "fatalOpenspcoop", "errorSpcoop", "errorOpenspcoop", "infoSpcoop", "infoOpenspcoop",
		//							"debugLow", "debugMedium", "debugHigh", "all" };
		String[] tipoMsg = { LogLevels.LIVELLO_OFF, LogLevels.LIVELLO_FATAL, LogLevels.LIVELLO_ERROR_PROTOCOL, LogLevels.LIVELLO_ERROR_INTEGRATION, 
				LogLevels.LIVELLO_INFO_PROTOCOL, LogLevels.LIVELLO_INFO_INTEGRATION,
				LogLevels.LIVELLO_DEBUG_LOW, LogLevels.LIVELLO_DEBUG_MEDIUM, LogLevels.LIVELLO_DEBUG_HIGH,
				LogLevels.LIVELLO_ALL};
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA);
		//		de.setLabel("Livello Severita");
		de.setType(DataElementType.SELECT);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA);
		de.setValues(tipoMsg);
		de.setSelected(severita);
		dati.addElement(de);

		de = new DataElement();
		//		de.setLabel("Livello Severita Log4J");
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA_LOG4J);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA_LOG4J);
		if(this.core.isShowConfigurazioneTracciamentoDiagnostica()){
			de.setType(DataElementType.SELECT);
			de.setValues(tipoMsg);
			de.setSelected(severita_log4j);
		}
		else{
			de.setType(DataElementType.HIDDEN);
			de.setValue(severita_log4j);
		}
		dati.addElement(de);
		
	}
	
	public void addPortaSeveritaMessaggiDiagnosticiToDati(String stato, String severita, Vector<DataElement> dati) {
		
		DataElement de;
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_MESSAGGI_DIAGNOSTICI);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_STATO);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA_RIDEFINITO);
		de.setType(DataElementType.SELECT);
		String valuesStato [] = {CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT, CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO};
		String labelsStato [] = {CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO_DEFAULT, CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO_RIDEFINITO};
		de.setSelected(stato);
		de.setLabels(labelsStato);
		de.setValues(valuesStato); 
		de.setPostBack(true);
		dati.addElement(de);
		
		//					String[] tipoMsg = { "off", "fatalOpenspcoop", "errorSpcoop", "errorOpenspcoop", "infoSpcoop", "infoOpenspcoop",
		//							"debugLow", "debugMedium", "debugHigh", "all" };
		if(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO.equals(stato)) {
			String[] tipoMsg = { LogLevels.LIVELLO_OFF, LogLevels.LIVELLO_FATAL, LogLevels.LIVELLO_ERROR_PROTOCOL, LogLevels.LIVELLO_ERROR_INTEGRATION, 
					LogLevels.LIVELLO_INFO_PROTOCOL, LogLevels.LIVELLO_INFO_INTEGRATION,
					LogLevels.LIVELLO_DEBUG_LOW, LogLevels.LIVELLO_DEBUG_MEDIUM, LogLevels.LIVELLO_DEBUG_HIGH,
					LogLevels.LIVELLO_ALL};
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA);
			//		de.setLabel("Livello Severita");
			de.setType(DataElementType.SELECT);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA);
			de.setValues(tipoMsg);
			de.setSelected(severita);
			dati.addElement(de);
		}
		
	}

	public Vector<DataElement> configurazioneCambiaNome(Vector<DataElement> dati, TipoOperazione other, String nomeGruppo,boolean isPortaDelegata) throws Exception{
		 
		DataElement de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_NOME_GRUPPO);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		// Azione
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_NOME);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(CostantiControlStation.PARAMETRO_NOME_GRUPPO);
		de.setValue(nomeGruppo);  
		de.setRequired(true); 
		dati.addElement(de);
		
		return dati;
	}
	
	public boolean configurazioneCambiaNomeCheck(TipoOperazione other, String nomeGruppo, List<String> listaNomiGruppiOccupati,boolean isPortaDelegata) throws Exception{
		if(StringUtils.isEmpty(nomeGruppo)) {
			this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_NOME_GRUPPO_NON_PUO_ESSERE_VUOTA);
			return false;
		}
		
		for (String nomeOccupato : listaNomiGruppiOccupati) {
			if(nomeOccupato.equalsIgnoreCase(nomeGruppo)) {
				this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_NOME_GRUPPO_GIA_PRESENTE);
				return false;			
			}
		}
		
		return true;
	}

	public DataElement getDataElementNotCorrelazioneApplicativa() {
		DataElement de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PORTE_CORRELAZIONE_APPLICATIVA_ATTENZIONE);
		de.setBold(true);
		de.setValue(CostantiControlStation.getLABEL_PORTE_CORRELAZIONE_APPLICATIVA_ATTENZIONE_MESSAGGIO(this.core.getPortaCorrelazioneApplicativaMaxLength()));
		de.setType(DataElementType.NOTE);
		return de;
	}
	
	public boolean useInterfaceNameInImplementationInvocationURL(String protocollo, ServiceBinding serviceBinding) throws ProtocolException{
		return ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo).
				createProtocolIntegrationConfiguration().useInterfaceNameInImplementationInvocationURL(serviceBinding);
	}
	public boolean useInterfaceNameInSubscriptionInvocationURL(String protocollo, ServiceBinding serviceBinding) throws ProtocolException{
		return ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo).
				createProtocolIntegrationConfiguration().useInterfaceNameInSubscriptionInvocationURL(serviceBinding);
	}
	
	public static String normalizeLabel(String label, int maxWidth) {
		if(label.length() > maxWidth) {
			return label.substring(0, maxWidth - 3) + "...";
		}
		return label;
	}
	
	public void addConfigurazioneCorsPorteToDati(TipoOperazione tipoOperazione,Vector<DataElement> dati, boolean showStato, String statoCorsPorta, boolean corsStato, TipoGestioneCORS corsTipo,
			boolean corsAllAllowOrigins, String corsAllowHeaders, String corsAllowOrigins, String corsAllowMethods,
			boolean corsAllowCredential, String corsExposeHeaders, boolean corsMaxAge, int corsMaxAgeSeconds) throws Exception {
		
		if(showStato) {
			DataElement de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_CONFIGURAZIONE_CORS);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}
		
		// stato generale cors
		DataElement de = new DataElement();
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_STATO_PORTA); 
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CORS_STATO_PORTA);
		if(showStato) {
			
			de.setType(DataElementType.SELECT);
			String valuesStato [] = {CostantiControlStation.VALUE_PARAMETRO_CORS_STATO_DEFAULT, CostantiControlStation.VALUE_PARAMETRO_CORS_STATO_RIDEFINITO};
			String labelsStato [] = { this.getGestioneCorsLabelDefault(true), CostantiControlStation.LABEL_PARAMETRO_CORS_STATO_PORTA_RIDEFINITO};
			de.setSelected(statoCorsPorta);
			de.setLabels(labelsStato);
			de.setValues(valuesStato); 
			de.setPostBack(true);
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(statoCorsPorta);
		}
		dati.addElement(de);
		
		if(!showStato || statoCorsPorta.equals(CostantiControlStation.VALUE_PARAMETRO_CORS_STATO_RIDEFINITO)) {
			this.addConfigurazioneCorsToDati(dati, corsStato, corsTipo, corsAllAllowOrigins, corsAllowHeaders, corsAllowOrigins, corsAllowMethods, corsAllowCredential, corsExposeHeaders, corsMaxAge, corsMaxAgeSeconds, false);
		}
	}
			
	
	// CORS
	public void addConfigurazioneCorsToDati(Vector<DataElement> dati, boolean corsStato, TipoGestioneCORS corsTipo,
			boolean corsAllAllowOrigins, String corsAllowHeaders, String corsAllowOrigins, String corsAllowMethods,
			boolean corsAllowCredential, String corsExposeHeaders, boolean corsMaxAge, int corsMaxAgeSeconds,
			boolean addTitle) {
		
		DataElement de;
		if(addTitle) {
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_CONFIGURAZIONE_CORS);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}
		
		de = new DataElement();
		de.setLabel(addTitle ? CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_STATO : "");
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_STATO);
		de.setType(DataElementType.SELECT);
		de.setPostBack(true);
		de.setValues(CostantiControlStation.SELECT_VALUES_STATO_FUNZIONALITA);
		de.setSelected(corsStato ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue());
		de.setValue(corsStato ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue());
		dati.addElement(de);
		
		if(corsStato) {
			
			String [] corsTipiValues = new String [] { TipoGestioneCORS.GATEWAY.getValue(), TipoGestioneCORS.TRASPARENTE.getValue()};
			String [] corsTipiLabels = new String [] { 
					CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_TIPO_GESTITO_GATEWAY,
					CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_TIPO_GESTITO_APPLICATIVO
					};
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_TIPO);
			de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_TIPO);
			de.setType(DataElementType.SELECT);
			de.setPostBack(true);
			de.setValues(corsTipiValues);
			de.setLabels(corsTipiLabels);
			de.setSelected(corsTipo.getValue());
			de.setValue(corsTipo.getValue());
			dati.addElement(de);
			
			if(TipoGestioneCORS.GATEWAY.equals(corsTipo)) {
				de = new DataElement();
				de.setType(DataElementType.SUBTITLE);
				de.setLabel(CostantiControlStation.LABEL_CONFIGURAZIONE_CORS_ACCESS_CONTROL);
				dati.addElement(de);
				
				
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_ORIGINS);
				de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_ORIGINS);
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(corsAllAllowOrigins);
				de.setPostBack(true);
				dati.addElement(de);
				
				if(!corsAllAllowOrigins) {
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_ORIGINS);
					de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_ORIGINS);
					de.setType(DataElementType.TEXT_EDIT);
					de.setValue(corsAllowOrigins);
					de.setRequired(true);
					de.enableTags();
					dati.addElement(de);
				}
			
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_HEADERS);
				de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_HEADERS);
				de.setType(DataElementType.TEXT_EDIT);
				de.setValue(corsAllowHeaders);
				de.setRequired(true);
				de.enableTags();
				dati.addElement(de);
				
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_METHODS);
				de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_METHODS);
				de.setType(DataElementType.TEXT_EDIT);
				de.setValue(corsAllowMethods);
				de.setRequired(true);
				de.enableTags();
				dati.addElement(de);
				
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_CREDENTIALS);
				de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_CREDENTIALS);
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(corsAllowCredential);
				dati.addElement(de);
				
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_EXPOSE_HEADERS);
				de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_EXPOSE_HEADERS);
				if(this.isModalitaStandard()) {
					de.setType(DataElementType.HIDDEN);
				}else {
					de.setType(DataElementType.TEXT_EDIT);
				}
				de.setValue(corsExposeHeaders);
				de.enableTags();
				dati.addElement(de);
				
				
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_MAX_AGE);
				de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_MAX_AGE);
				if(this.isModalitaStandard()) {
					de.setType(DataElementType.HIDDEN);
				}else {
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(corsMaxAge);
					de.setPostBack(true);
				}
				de.setValue(corsMaxAge+"");
				dati.addElement(de);
				
				if(corsMaxAge) {
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_MAX_AGE_SECONDS);
					de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_MAX_AGE_SECONDS);
					de.setValue(corsMaxAgeSeconds+"");
					if(this.isModalitaStandard()) {
						de.setType(DataElementType.HIDDEN);
					}else {
						de.setType(DataElementType.NUMBER);
						de.setMinValue(-1);
						de.setMaxValue(Integer.MAX_VALUE);
					}
					dati.addElement(de);
				}
			}
		}
	}
	
	public CorsConfigurazione getGestioneCors(boolean corsStato, TipoGestioneCORS corsTipo, boolean corsAllAllowOrigins,
			String corsAllowHeaders, String corsAllowOrigins, String corsAllowMethods, boolean corsAllowCredential,
			String corsExposeHeaders, boolean corsMaxAge, int corsMaxAgeSeconds) {
		CorsConfigurazione gestioneCors = new CorsConfigurazione();
		gestioneCors.setStato(corsStato ? StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO); 
		if(corsStato) {
			gestioneCors.setTipo(corsTipo);

			if(corsTipo.equals(TipoGestioneCORS.GATEWAY)) {
				gestioneCors.setAccessControlAllAllowOrigins(corsAllAllowOrigins ? StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO);
				if(!corsAllAllowOrigins) {
					CorsConfigurazioneOrigin accessControlAllowOrigins = new CorsConfigurazioneOrigin();
					accessControlAllowOrigins.setOriginList(Arrays.asList(corsAllowOrigins.split(",")));
					gestioneCors.setAccessControlAllowOrigins(accessControlAllowOrigins );
				}

				CorsConfigurazioneHeaders accessControlAllowHeaders = new CorsConfigurazioneHeaders();
				accessControlAllowHeaders.setHeaderList(Arrays.asList(corsAllowHeaders.split(",")));
				gestioneCors.setAccessControlAllowHeaders(accessControlAllowHeaders);

				CorsConfigurazioneMethods accessControlAllowMethods = new CorsConfigurazioneMethods();
				accessControlAllowMethods.setMethodList(Arrays.asList(corsAllowMethods.split(",")));
				gestioneCors.setAccessControlAllowMethods(accessControlAllowMethods);

				gestioneCors.setAccessControlAllowCredentials(corsAllowCredential ? StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO);

				CorsConfigurazioneHeaders accessControlExposeHeaders = new CorsConfigurazioneHeaders();
				accessControlExposeHeaders.setHeaderList(Arrays.asList(corsExposeHeaders.split(",")));
				gestioneCors.setAccessControlExposeHeaders(accessControlExposeHeaders );

				gestioneCors.setAccessControlMaxAge(corsMaxAge ? corsMaxAgeSeconds : null);
			}
		}
		return gestioneCors;
	}
	
	public boolean checkDataConfigurazioneCorsPorta(TipoOperazione tipoOperazione,boolean showStato, String statoCorsPorta) throws Exception{
		
		if(showStato) {
			if(StringUtils.isEmpty(statoCorsPorta) || !(statoCorsPorta.equals(CostantiControlStation.VALUE_PARAMETRO_CORS_STATO_DEFAULT) || statoCorsPorta.equals(CostantiControlStation.VALUE_PARAMETRO_CORS_STATO_RIDEFINITO))) {
				this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_NON_VALIDO, CostantiControlStation.LABEL_PARAMETRO_CORS_STATO_PORTA));
				return false;
			}
		}
		
		if(!showStato || statoCorsPorta.equals(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO)) {
			return this.checkDataCors();
		}
		
		return true;
	}
	
	public boolean checkDataCors() throws Exception {
		String corsStatoTmp = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_STATO);
		boolean corsStato = ServletUtils.isCheckBoxEnabled(corsStatoTmp);
		if(corsStato) {
			String corsTipoTmp = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_TIPO);
			TipoGestioneCORS corsTipo = corsTipoTmp != null ? TipoGestioneCORS.toEnumConstant(corsTipoTmp) : TipoGestioneCORS.GATEWAY;
			if(corsTipo.equals(TipoGestioneCORS.GATEWAY)) {
				String corsAllAllowOriginsTmp = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_ORIGINS);
				boolean corsAllAllowOrigins = ServletUtils.isCheckBoxEnabled(corsAllAllowOriginsTmp);
				if(!corsAllAllowOrigins) {
					String corsAllowOrigins =  this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_ORIGINS);
					if(StringUtils.isNotEmpty(corsAllowOrigins)) {
						List<String> asList = Arrays.asList(corsAllowOrigins.split(","));
						for (String string : asList) {
							if(string.contains(" ")) {
								this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CORS_SPAZI_BIANCHI_NON_AMMESSI, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_ORIGINS));   
								return false;
							}
						}
					} else {
						this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CORS_CAMPO_OBBLIGATORIO, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_ORIGINS));   
						return false;
					}
				}
				
				String corsAllowHeaders =  this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_HEADERS);
				if(StringUtils.isNotEmpty(corsAllowHeaders)) {
					List<String> asList = Arrays.asList(corsAllowHeaders.split(","));
					for (String string : asList) {
						if(string.contains(" ")) {
							this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CORS_SPAZI_BIANCHI_NON_AMMESSI, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_HEADERS));   
							return false;
						}
					}
				} else {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CORS_CAMPO_OBBLIGATORIO, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_HEADERS));   
					return false;
				}
				
				String corsAllowMethods =  this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_METHODS);
				if(StringUtils.isNotEmpty(corsAllowMethods)) {
					List<String> asList = Arrays.asList(corsAllowMethods.split(","));
					for (String string : asList) {
						if(string.contains(" ")) {
							this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CORS_SPAZI_BIANCHI_NON_AMMESSI, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_METHODS));   
							return false;
						}
						
						try {
							// check che HTTP-Method sia supportato
							Enum.valueOf(HttpRequestMethod.class, string.toUpperCase());
						} catch(Exception e) {
							this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CORS_ALLOW_METHOD_NON_VALIDO, string, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_METHODS));   
							return false;
						}
					}
				}else {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CORS_CAMPO_OBBLIGATORIO, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_METHODS));   
					return false;
				}
				
//				String corsAllowCredentialTmp = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_CREDENTIALS);
//				boolean corsAllowCredential =  ServletUtils.isCheckBoxEnabled(corsAllowCredentialTmp);
				
				String corsExposeHeaders = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_EXPOSE_HEADERS);
				if(StringUtils.isNotEmpty(corsExposeHeaders)) {
					List<String> asList = Arrays.asList(corsExposeHeaders.split(","));
					for (String string : asList) {
						if(string.contains(" ")) {
							this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CORS_SPAZI_BIANCHI_NON_AMMESSI, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_EXPOSE_HEADERS));   
							return false;
						}
					}
				}
				
				
//				String corsMaxAgeTmp = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_MAX_AGE);
//				boolean corsMaxAge =  ServletUtils.isCheckBoxEnabled(corsMaxAgeTmp);
//				if(corsMaxAge) {
//					String corsMaxAgeSecondsTmp = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_MAX_AGE_SECONDS);
//					int corsMaxAgeSeconds = -1;
//					if(corsMaxAgeSecondsTmp != null) {
//						try {
//							corsMaxAgeSeconds = Integer.parseInt(corsMaxAgeSecondsTmp);
//						}catch(Exception e) {}
//					}
//				}
			}
		}
		return true;
	}
	
	public void addConfigurazioneResponseCachingPorteToDati(TipoOperazione tipoOperazione,Vector<DataElement> dati, boolean showStato, String statoResponseCachingPorta, boolean responseCachingEnabled, int responseCachingSeconds,
			boolean responseCachingMaxResponseSize, long responseCachingMaxResponseSizeBytes,
			boolean responseCachingDigestUrlInvocazione, boolean responseCachingDigestHeaders,
			boolean responseCachingDigestPayload) throws Exception {
		
		if(showStato) {
			DataElement de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_CONFIGURAZIONE_RESPONSE_CACHING);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}
		
		// stato generale cors
		DataElement de = new DataElement();
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_STATO_PORTA); 
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_RESPONSE_CACHING_STATO_PORTA);
		if(showStato) {
			
			de.setType(DataElementType.SELECT);
			String valuesStato [] = {CostantiControlStation.VALUE_PARAMETRO_RESPONSE_CACHING_STATO_DEFAULT, CostantiControlStation.VALUE_PARAMETRO_RESPONSE_CACHING_STATO_RIDEFINITO};
			String labelsStato [] = { this.getResponseCachingLabelDefault(true), CostantiControlStation.LABEL_PARAMETRO_RESPONSE_CACHING_STATO_PORTA_RIDEFINITO};
			de.setSelected(statoResponseCachingPorta);
			de.setLabels(labelsStato);
			de.setValues(valuesStato); 
			de.setPostBack(true);
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(statoResponseCachingPorta);
		}
		dati.addElement(de);
		
		if(!showStato || statoResponseCachingPorta.equals(CostantiControlStation.VALUE_PARAMETRO_CORS_STATO_RIDEFINITO)) {
			this.addResponseCachingToDati(dati, responseCachingEnabled, responseCachingSeconds, responseCachingMaxResponseSize, responseCachingMaxResponseSizeBytes, responseCachingDigestUrlInvocazione, responseCachingDigestHeaders, responseCachingDigestPayload, false);
		}
	}
	
	public void addResponseCachingToDati(Vector<DataElement> dati, boolean responseCachingEnabled, int responseCachingSeconds,
			boolean responseCachingMaxResponseSize, long responseCachingMaxResponseSizeBytes,
			boolean responseCachingDigestUrlInvocazione, boolean responseCachingDigestHeaders,
			boolean responseCachingDigestPayload, boolean addTitle) {
		DataElement de;
		if(addTitle) {
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_CONFIGURAZIONE_RESPONSE_CACHING);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}
		
		de = new DataElement();
		de.setLabel(addTitle ?  CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_STATO : "");
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_STATO);
		de.setType(DataElementType.SELECT);
		de.setPostBack(true);
		de.setValues(CostantiControlStation.SELECT_VALUES_STATO_FUNZIONALITA);
		de.setSelected(responseCachingEnabled ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue());
		de.setValue(responseCachingEnabled ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue());
		dati.addElement(de);
		
		if(responseCachingEnabled) {
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_TIMEOUT);
			de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_TIMEOUT);
			de.setValue(responseCachingSeconds+"");
			de.setType(DataElementType.NUMBER);
			de.setMinValue(1);
			de.setMaxValue(Integer.MAX_VALUE);
			dati.addElement(de);
			
			
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_MAX_RESPONSE_SIZE);
			de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_MAX_RESPONSE_SIZE);
			de.setType(DataElementType.CHECKBOX);
			de.setSelected(responseCachingMaxResponseSize);
			de.setPostBack(true);
			de.setValue(responseCachingMaxResponseSize+"");
			dati.addElement(de);
			
			if(responseCachingMaxResponseSize) {
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_MAX_RESPONSE_SIZE_BYTES);
				de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_MAX_RESPONSE_SIZE_BYTES);
				de.setValue(responseCachingMaxResponseSizeBytes+"");
				de.setType(DataElementType.NUMBER);
				de.setMinValue(1);
				de.setMaxValue(Integer.MAX_VALUE);
				dati.addElement(de);
			}
			
			de = new DataElement();
			de.setType(DataElementType.SUBTITLE);
			de.setLabel(CostantiControlStation.LABEL_CONFIGURAZIONE_RESPONSE_CACHING_GENERAZIONE_HASH);
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_URI_INVOCAZIONE);
			de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_URI_INVOCAZIONE);
			de.setType(DataElementType.SELECT);
			de.setValues(CostantiControlStation.SELECT_VALUES_STATO_FUNZIONALITA);
			de.setSelected(responseCachingDigestUrlInvocazione ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue());
			de.setValue(responseCachingDigestUrlInvocazione ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue());
			dati.addElement(de);
			
			
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_HEADERS);
			de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_HEADERS);
			de.setType(DataElementType.SELECT);
			de.setValues(CostantiControlStation.SELECT_VALUES_STATO_FUNZIONALITA);
			de.setSelected(responseCachingDigestHeaders ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue());
			de.setValue(responseCachingDigestHeaders ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue());
			dati.addElement(de);
			
			
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_PAYLOAD);
			de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_PAYLOAD);
			de.setType(DataElementType.SELECT);
			de.setValues(CostantiControlStation.SELECT_VALUES_STATO_FUNZIONALITA);
			de.setSelected(responseCachingDigestPayload ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue());
			de.setValue(responseCachingDigestPayload ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue());
			dati.addElement(de);
			
		}
	}
	
	public ResponseCachingConfigurazione getResponseCaching(boolean responseCachingEnabled, int responseCachingSeconds, boolean responseCachingMaxResponseSize, long responseCachingMaxResponseSizeBytes,
			boolean responseCachingDigestUrlInvocazione, boolean responseCachingDigestHeaders,	boolean responseCachingDigestPayload) {
		
		ResponseCachingConfigurazione responseCaching  = new ResponseCachingConfigurazione();
		
		responseCaching.setStato(responseCachingEnabled ? StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO); 
		if(responseCachingEnabled) {
			responseCaching.setCacheTimeoutSeconds(responseCachingSeconds);
			
			if(responseCachingMaxResponseSize) {
				responseCaching.setMaxMessageSize(responseCachingMaxResponseSizeBytes);
			}
			
			if(responseCachingDigestUrlInvocazione || responseCachingDigestHeaders || responseCachingDigestPayload) {
				ResponseCachingConfigurazioneHashGenerator hashGenerator = new ResponseCachingConfigurazioneHashGenerator();
				
				hashGenerator.setPayload(responseCachingDigestPayload ? StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO);
				hashGenerator.setRequestUri(responseCachingDigestUrlInvocazione ? StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO);
				hashGenerator.setHeaders(responseCachingDigestHeaders ? StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO);
				
				responseCaching.setHashGenerator(hashGenerator);
			}
		}
		
		return responseCaching;
	}
	
	public boolean isResponseCachingAbilitato(ResponseCachingConfigurazione configurazione) {
		boolean abilitato = false;
		
		if(configurazione == null)
			return false;
		
		if(configurazione.getStato().equals(StatoFunzionalita.ABILITATO))
			return true;
		
		
		return abilitato;
	}
	
	public boolean isCorsAbilitato(CorsConfigurazione configurazione) {
		boolean abilitato = false;
		
		if(configurazione == null)
			return false;
		
		if(configurazione.getStato().equals(StatoFunzionalita.ABILITATO))
			return true;
		
		
		return abilitato;
	}
	
	public boolean checkDataConfigurazioneResponseCachingPorta(TipoOperazione tipoOperazione,boolean showStato, String statoResponseCachingPorta) throws Exception{
		
		if(showStato) {
			if(StringUtils.isEmpty(statoResponseCachingPorta) || 
					!(statoResponseCachingPorta.equals(CostantiControlStation.VALUE_PARAMETRO_RESPONSE_CACHING_STATO_DEFAULT) || statoResponseCachingPorta.equals(CostantiControlStation.VALUE_PARAMETRO_RESPONSE_CACHING_STATO_RIDEFINITO))) {
				this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_NON_VALIDO, CostantiControlStation.LABEL_PARAMETRO_RESPONSE_CACHING_STATO_PORTA));
				return false;
			}
		}
		
		if(!showStato || statoResponseCachingPorta.equals(CostantiControlStation.VALUE_PARAMETRO_RESPONSE_CACHING_STATO_RIDEFINITO)) {
			return this.checkDataResponseCaching();
		}
		
		return true;
	}
	
	public boolean checkDataResponseCaching() throws Exception {
		return true;
	}
}
