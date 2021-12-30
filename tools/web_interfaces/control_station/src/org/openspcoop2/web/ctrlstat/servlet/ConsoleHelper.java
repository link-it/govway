/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
import java.net.URL;
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
import java.util.stream.Collectors;

import javax.mail.BodyPart;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.constants.RuoloPorta;
import org.openspcoop2.core.allarmi.constants.StatoAllarme;
import org.openspcoop2.core.allarmi.utils.AllarmiConverterUtils;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.ModalitaIdentificazione;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.config.AttributeAuthority;
import org.openspcoop2.core.config.AutorizzazioneScope;
import org.openspcoop2.core.config.CanaleConfigurazione;
import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.ConfigurazionePortaHandler;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazione;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.CorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.CorsConfigurazioneHeaders;
import org.openspcoop2.core.config.CorsConfigurazioneMethods;
import org.openspcoop2.core.config.CorsConfigurazioneOrigin;
import org.openspcoop2.core.config.Dump;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.DumpConfigurazioneRegola;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.GestioneToken;
import org.openspcoop2.core.config.MessageSecurity;
import org.openspcoop2.core.config.MtomProcessor;
import org.openspcoop2.core.config.MtomProcessorFlowParameter;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataLocalForward;
import org.openspcoop2.core.config.PortaTracciamento;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneControl;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneGenerale;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneHashGenerator;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.TrasformazioneRegolaRichiesta;
import org.openspcoop2.core.config.TrasformazioneRegolaRisposta;
import org.openspcoop2.core.config.TrasformazioneSoap;
import org.openspcoop2.core.config.TrasformazioneSoapRisposta;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.MTOMProcessorType;
import org.openspcoop2.core.config.constants.PluginCostanti;
import org.openspcoop2.core.config.constants.RuoloTipoMatch;
import org.openspcoop2.core.config.constants.ScopeTipoMatch;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaCacheDigestQueryParameter;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.config.constants.TipoBehaviour;
import org.openspcoop2.core.config.constants.TipoGestioneCORS;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.db.IDServizioApplicativoDB;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsaPolicyAttiva;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.mvc.properties.Config;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.core.mvc.properties.utils.ConfigManager;
import org.openspcoop2.core.mvc.properties.utils.PropertiesSourceConfiguration;
import org.openspcoop2.core.plugins.Plugin;
import org.openspcoop2.core.plugins.constants.TipoPlugin;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.beans.ResourceSintetica;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.core.registry.constants.HttpMethod;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.constants.ScopeContesto;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.TipiDocumentoSicurezza;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaAccordi;
import org.openspcoop2.core.registry.driver.FiltroRicercaGruppi;
import org.openspcoop2.core.registry.driver.FiltroRicercaRuoli;
import org.openspcoop2.core.registry.driver.FiltroRicercaScope;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.transazioni.utils.PropertiesSerializator;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.monitor.engine.alarm.wrapper.ConfigurazioneAllarmeBean;
import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazioneApiKey;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazionePrincipal;
import org.openspcoop2.pdd.core.autorizzazione.CostantiAutorizzazione;
import org.openspcoop2.pdd.core.behaviour.built_in.load_balance.ConfigurazioneLoadBalancer;
import org.openspcoop2.pdd.core.behaviour.built_in.load_balance.LoadBalancerType;
import org.openspcoop2.pdd.core.behaviour.built_in.load_balance.sticky.StickyUtils;
import org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils;
import org.openspcoop2.pdd.core.connettori.ConnettoreNULL;
import org.openspcoop2.pdd.core.connettori.ConnettoreNULLEcho;
import org.openspcoop2.pdd.core.dynamic.DynamicHelperCostanti;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.core.integrazione.GruppoIntegrazione;
import org.openspcoop2.pdd.core.integrazione.TipoIntegrazione;
import org.openspcoop2.pdd.core.token.TokenUtilities;
import org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione;
import org.openspcoop2.pdd.logger.LogLevels;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemType;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemValueType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;
import org.openspcoop2.protocol.sdk.properties.AbstractConsoleItem;
import org.openspcoop2.protocol.sdk.properties.AbstractProperty;
import org.openspcoop2.protocol.sdk.properties.BaseConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BinaryConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BinaryProperty;
import org.openspcoop2.protocol.sdk.properties.BooleanConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BooleanProperty;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleHelper;
import org.openspcoop2.protocol.sdk.properties.NumberConsoleItem;
import org.openspcoop2.protocol.sdk.properties.NumberProperty;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesFactory;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.properties.StringConsoleItem;
import org.openspcoop2.protocol.sdk.properties.StringProperty;
import org.openspcoop2.protocol.sdk.properties.SubtitleConsoleItem;
import org.openspcoop2.protocol.sdk.properties.TitleConsoleItem;
import org.openspcoop2.protocol.sdk.validator.ValidazioneResult;
import org.openspcoop2.protocol.utils.EsitiConfigUtils;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.mime.MimeMultipart;
import org.openspcoop2.utils.properties.PropertiesUtilities;
import org.openspcoop2.utils.regexp.RegExpException;
import org.openspcoop2.utils.regexp.RegExpNotFoundException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.web.ctrlstat.config.ConsoleProperties;
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.Connettori;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ControlStationLogger;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.costanti.InUsoType;
import org.openspcoop2.web.ctrlstat.costanti.TipologiaConnettori;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationNotFound;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedMenuItem;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedMenu;
import org.openspcoop2.web.ctrlstat.servlet.ac.AccordiCooperazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.ac.AccordiCooperazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.api.ApiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaFruitoriPorteDelegateMappingInfo;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaPorteApplicativeMappingInfo;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCore;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCostanti;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ExporterUtils;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCore;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.gruppi.GruppiCore;
import org.openspcoop2.web.ctrlstat.servlet.gruppi.GruppiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.monitor.MonitorCostanti;
import org.openspcoop2.web.ctrlstat.servlet.operazioni.OperazioniCore;
import org.openspcoop2.web.ctrlstat.servlet.operazioni.OperazioniCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeServizioApplicativoAutorizzatoUtilities;
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
import org.openspcoop2.web.ctrlstat.servlet.utils.UtilsCostanti;
import org.openspcoop2.web.lib.audit.web.AuditCostanti;
import org.openspcoop2.web.lib.audit.web.AuditHelper;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.CheckboxStatusType;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElement.STATO_APERTURA_SEZIONI;
import org.openspcoop2.web.lib.mvc.DataElementInfo;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.Dialog;
import org.openspcoop2.web.lib.mvc.Dialog.BodyElement;
import org.openspcoop2.web.lib.mvc.MenuEntry;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TargetType;
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
public class ConsoleHelper implements IConsoleHelper {
	
	protected HttpServletRequest request;
	@Override
	public HttpServletRequest getRequest() {
		return this.request;
	}
	protected PageData pd;
	public PageData getPd() {
		return this.pd;
	}
	protected HttpSession session;
	@Override
	public HttpSession getSession() {
		return this.session;
	}
	
	@Override
	public boolean isEditModeInProgress() throws Exception{
		String editMode = this.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
		return ServletUtils.isEditModeInProgress(editMode);		
	}

	@Override
	public boolean isEditModeFinished() throws Exception{
		String editMode = this.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
		return ServletUtils.isEditModeFinished(editMode);		
	}
	
	@Override
	public String getPostBackElementName() throws Exception{
		return this.getParameter(Costanti.POSTBACK_ELEMENT_NAME);
	}
	
	@Override
	public boolean isPostBack() throws Exception{
		String postbackElementName = this.getPostBackElementName();
		return postbackElementName!=null && !"".equals(postbackElementName);
	}
	
	@Override
	public boolean isPostBackFilterElement() throws Exception{
		String postbackElementName = this.getPostBackElementName();
		return postbackElementName!=null && postbackElementName.startsWith(Costanti.PARAMETRO_FILTER_VALUE);
	}
	
	public void clearFiltroSoggettoByPostBackProtocollo(int posizioneFiltroProtocollo, ISearch ricerca, int idLista) throws Exception {
		String postBackElement = this.getPostBackElementName();
		if((Costanti.PARAMETRO_FILTER_VALUE+posizioneFiltroProtocollo).equals(postBackElement)) {
			// verifico se si tratta del profilo di interoperabilita.
			if(Filtri.FILTRO_PROTOCOLLO.equals(this.getParameter((Costanti.PARAMETRO_FILTER_NAME+posizioneFiltroProtocollo)))) {
				String value = this.getParameter((Costanti.PARAMETRO_FILTER_VALUE+posizioneFiltroProtocollo));
				if( (value==null || "".equals(value) ||
						CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROTOCOLLO_QUALSIASI.equals(value))){
					ricerca.clearFilter(idLista, Filtri.FILTRO_SOGGETTO);
				}
			}
		}
	}
	
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
	protected GruppiCore gruppiCore = null;

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
	private Map<String, List<InputStream>> mapParametri = null;
	private Map<String, Object> mapParametriReaded = null;
	private Map<String, List<String>> mapNomiFileParametri = null;
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
	@Override
	public boolean isModalitaCompleta() {
		return InterfaceType.equals(this.tipoInterfaccia, 
				InterfaceType.COMPLETA); 
	}
	@Override
	public boolean isModalitaAvanzata() {
		// considero anche l'interfaccia completa
		return InterfaceType.equals(this.tipoInterfaccia, 
				InterfaceType.AVANZATA,InterfaceType.COMPLETA); 
	}
	@Override
	public boolean isModalitaStandard() {
		return InterfaceType.equals(this.tipoInterfaccia, 
				InterfaceType.STANDARD); 
	}

	/** Soggetto Selezionato */
	public boolean isSoggettoMultitenantSelezionato() {
		return this.core.isMultitenant() && StringUtils.isNotEmpty(this.getSoggettoMultitenantSelezionato());
	}
	@Override
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
		try {
			if(this.isPostBack()) {
				pd.setPostBackResult(true);
			}
		} catch (Exception e) {
			this.log.error("Exception ctrlstatHelper: " + e.getMessage(), e);
		}
	}
	public ConsoleHelper(ControlStationCore core, HttpServletRequest request, PageData pd, HttpSession session) {
		this.init(core, request, pd, session);
	}
	private void init(ControlStationCore core, HttpServletRequest request, PageData pd, HttpSession session) {
		this.request = request;
		this.pd = pd;
		this.session = session;
		this.log = ControlStationLogger.getPddConsoleCoreLogger();
		try {
			
			if (this.request.getCharacterEncoding() == null) { 
		        this.request.setCharacterEncoding(Charset.UTF_8.getValue());
			}
			
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
			this.gruppiCore = new GruppiCore(this.core);
			
			this.auditHelper = new AuditHelper(request, pd, session);

			this.idBinaryParameterRicevuti = new ArrayList<String>();
			// analisi dei parametri della request
			this.contentType = request.getContentType();
			if ((this.contentType != null) && (this.contentType.indexOf(Costanti.MULTIPART) != -1)) {
				this.multipart = true;
				this.mimeMultipart = new MimeMultipart(request.getInputStream(), this.contentType);
				this.mapParametri = new HashMap<String, List<InputStream>>();
				this.mapParametriReaded = new HashMap<>();
				this.mapNomiFileParametri = new HashMap<String,List<String>>();

				for(int i = 0 ; i < this.mimeMultipart.countBodyParts() ;  i ++) {
					BodyPart bodyPart = this.mimeMultipart.getBodyPart(i);
					String partName = getBodyPartName(bodyPart);
					
					List<InputStream> list = null;
					if(this.mapParametri.containsKey(partName)) {
						list = this.mapParametri.get(partName);
					}
					else {
						list = new ArrayList<>();
						this.mapParametri.put(partName, list);
					}
					
					//if(!this.mapParametri.containsKey(partName)) {
					if(partName.startsWith(Costanti.PARAMETER_FILEID_PREFIX)){
						this.idBinaryParameterRicevuti.add(partName);
					}
					//this.mapParametri.put(partName, bodyPart.getInputStream());
					list.add(bodyPart.getInputStream());
					String fileName = getBodyPartFileName(bodyPart);
					if(fileName != null) {
						List<String> nomiFiles = null;
						if(this.mapNomiFileParametri.containsKey(partName)) {
							nomiFiles = this.mapNomiFileParametri.get(partName);
						} else {
							nomiFiles = new ArrayList<String>();
							this.mapNomiFileParametri.put(partName, nomiFiles);
						}
						
						nomiFiles.add(fileName);
					}
					//}
					//else throw new Exception("Parametro ["+partName+"] Duplicato.");
				}
			} else {
				Enumeration<String> parameterNames = this.request.getParameterNames();
				while (parameterNames.hasMoreElements()) {
					String param = (String) parameterNames.nextElement();
					if(param.startsWith(Costanti.PARAMETER_FILEID_PREFIX)){
						this.idBinaryParameterRicevuti.add(param);
					}
				}
			}
			
			try {
				if(!ControlStationCore.isAPIMode()) {
					ConsoleProperties consoleProperties = ConsoleProperties.getInstance();
					this.size = consoleProperties.getConsoleLunghezzaLabel();
				}
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
	
	public IDAccordo getIDAccordoFromUri(String uriAccordo) throws Exception{
		return this.idAccordoFactory.getIDAccordoFromUri(uriAccordo);
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
	
	@Override
	public Enumeration<?> getParameterNames() throws Exception {
		this.checkErrorInit();
		if(this.multipart){
			throw new Exception("Not Implemented");
		}
		else {
			return this.request.getParameterNames();
		}
	}
	
	@Override
	public String [] getParameterValues(String parameterName) throws Exception {
		this.checkErrorInit();
		if(this.multipart){
			
			String [] array = null;
			if(this.mapParametriReaded.containsKey(parameterName)) {
				array = (String[]) this.mapParametriReaded.get(parameterName);
			}
			else {
				List<InputStream> list = this.mapParametri.get(parameterName);
				if(list != null && !list.isEmpty()){
					StringBuilder sb = new StringBuilder();
					for (InputStream inputStream : list) {
						if(inputStream != null){
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							IOUtils.copy(inputStream, baos);
							baos.flush();
							baos.close();
							if(sb.length()>0) {
								sb.append(",");
							}
							sb.append(baos.toString());
						}
					}
					if(sb.length()>0) {
						String v = sb.toString();
						array = v.split(",");
						this.mapParametriReaded.put(parameterName, array);
					}
				}
			}
			return array;
			
		}
		else {
			return this.request.getParameterValues(parameterName);
		}
	}
	
	@Override
	public String getParameter(String parameterName) throws Exception {
		return getParameter(parameterName, String.class, null);
	}

	@Override
	public <T> T getParameter(String parameterName, Class<T> type) throws Exception {
		return getParameter(parameterName, type, null);
	}

	@Override
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
				// La lista dovrebbe al massimo avere sempre un solo valore
				List<InputStream> list = this.mapParametri.get(parameterName);
				if(list != null && !list.isEmpty()){
					
					if(list.size()>1) {
						throw new Exception("Parametro ["+parameterName+"] Duplicato.");
					}
					InputStream inputStream = list.get(0);
					if(inputStream != null){
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						IOUtils.copy(inputStream, baos);
						baos.flush();
						baos.close();
						paramAsString = baos.toString();
						this.mapParametriReaded.put(parameterName, paramAsString);
					}
				}
			}
		}else{
			paramAsString = this.request.getParameter(parameterName);
		}

		if(paramAsString != null) {
			
			// Fix: textarea un ritorno a capo viene codificato con \r\n, anche se il valore fornito iniziale possedeva solo lo \n.
			if(paramAsString.contains("\r\n")) {
				while(paramAsString.contains("\r\n")) {
					paramAsString = paramAsString.replace("\r\n", "\n");
				}
			}
			
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
	
	@Override
	public byte[] getBinaryParameterContent(String parameterName) throws Exception {
		
//		this.checkErrorInit();
//		
//		
//		if(this.multipart){
//			if(this.mapParametriReaded.containsKey(parameterName)) {
//				return (byte[]) this.mapParametriReaded.get(parameterName);
//			}
//			else {
//				// La lista dovrebbe al massimo avere sempre un solo valore
//				List<InputStream> list = this.mapParametri.get(parameterName);
//				if(list != null && !list.isEmpty()){
//					
//					if(list.size()>1) {
//						throw new Exception("Parametro ["+parameterName+"] Duplicato.");
//					}
//					InputStream inputStream = list.get(0);
//					if(inputStream != null){
//						ByteArrayOutputStream baos = new ByteArrayOutputStream();
//						IOUtils.copy(inputStream, baos);
//						byte[] b = baos.toByteArray();
//						this.mapParametriReaded.put(parameterName, b);
//						return b;
//					}
//				}
//			}
//		}else{
//			String paramAsString = this.request.getParameter(parameterName);
//			if(paramAsString != null)
//				return paramAsString.getBytes();
//		}
//
//		return null;
		
		List<byte[]> bpContents = this._getBinaryParameterContent(parameterName);
		
		if(bpContents != null && bpContents.size() > 0)
			return bpContents.get(0);
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<byte[]> _getBinaryParameterContent(String parameterName) throws Exception {
		
		this.checkErrorInit();
		
		List<byte[]> bpContents = null;
		if(this.multipart){
			if(this.mapParametriReaded.containsKey(parameterName)) {
				return (List<byte[]>) this.mapParametriReaded.get(parameterName);
			}
			else {
				// La lista dovrebbe al massimo avere sempre un solo valore
				List<InputStream> list = this.mapParametri.get(parameterName);
				if(list != null && !list.isEmpty()){
					
					bpContents = new ArrayList<byte[]>();
					for (InputStream inputStream : list) {
						if(inputStream != null){
							ByteArrayOutputStream baosInner = new ByteArrayOutputStream();
							IOUtils.copy(inputStream, baosInner);
							byte[] b = baosInner.toByteArray();
							bpContents.add(b);
						}
					}
					if(bpContents.size() > 0) {
						this.mapParametriReaded.put(parameterName, bpContents);
						return bpContents;
					}
				}
			}
		}else{
			// caso che non dovrebbe mai succedere
			String[] paramAsString = this.request.getParameterValues(parameterName);
			if(paramAsString != null) {
				List<String> tmpStrings = Arrays.asList(paramAsString);
				bpContents = tmpStrings.stream().map(e -> e.getBytes()).collect(Collectors.toList());
			}
		}

		return bpContents;
	}

	@Override
	public String getFileNameParameter(String parameterName) throws Exception {
		
		this.checkErrorInit();
				
		if(this.multipart){
			List<String> tmp = this.mapNomiFileParametri.get(parameterName);
			return (tmp != null && tmp.size() > 0) ? tmp.get(0) : null; 
		} else 
			return this.request.getParameter(parameterName);

	}
	
	public List<String> getFileNamesParameter(String parameterName) throws Exception {
		
		this.checkErrorInit();
				
		if(this.multipart){
			List<String> tmp = this.mapNomiFileParametri.get(parameterName);
			return tmp;
		} else {
			String[] tmp = this.request.getParameterValues(parameterName);
			return (tmp != null && tmp.length > 0) ? Arrays.asList(tmp): null; 
		}

	}
	
	private HashMap<String, byte[]> customBinaryParameters = new HashMap<>();
	public void registerBinaryParameter(String parameterName, byte[] content) {
		this.customBinaryParameters.put(parameterName, content);
	}
	
	
	public BinaryParameter newBinaryParameter(String parameterName) throws Exception {
		BinaryParameter bp = new BinaryParameter();
		bp.setName(parameterName); 
		return bp;
	}
	public BinaryParameter getBinaryParameter(String parameterName) throws Exception {
		
		this.checkErrorInit();
				
		BinaryParameter bp = newBinaryParameter(parameterName);
		String filename = null;
		String fileId = null;
		File file = null;
		FileOutputStream fos =  null;
		FileInputStream fis =  null;
		ByteArrayInputStream bais = null;
		ByteArrayOutputStream baos = null;
		
		byte [] bpContent = null;
		
		// 0. provo a prelevarlo dai custom parameters (servizio rs)
		if(this.customBinaryParameters.containsKey(parameterName)) {
			bpContent = this.customBinaryParameters.get(parameterName);
		}
		else {
			// 1. provo a prelevare il valore dalla request
			bpContent = this.getBinaryParameterContent(parameterName);
		}
		
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
		
		// se ho provocato io l'evento di cancellazione via postback allora svuoto il contenuto
		if(this.isPostBack()) {
			String postBackElementName = this.getPostBackElementName();
			if(postBackElementName!=null && postBackElementName.startsWith(parameterName) && postBackElementName.contains(Costanti.PARAMETER_FILENAME_REMOVE_PLACEHOLDER)) {
				bp.setValue(null);
				bp.setFilename(null); 
				bp.setId(null);  
			}
		}
		
		return bp;
	}
	
	
	public List<BinaryParameter> getBinaryParameters(String parameterName) throws Exception {
		
		this.checkErrorInit();
				
//		BinaryParameter bp = newBinaryParameter(parameterName);
		String filename = null;
		String fileId = null;
		File file = null;
		FileOutputStream fos =  null;
		FileInputStream fis =  null;
		ByteArrayInputStream bais = null;
		ByteArrayOutputStream baos = null;
		
		byte [] bpContent = null;
		
		List<byte[]> bpContents = this._getBinaryParameterContent(parameterName);
//		// 0. provo a prelevarlo dai custom parameters (servizio rs)
//		if(this.customBinaryParameters.containsKey(parameterName)) {
//			bpContent = this.customBinaryParameters.get(parameterName);
//		}
//		else {
//			// 1. provo a prelevare il valore dalla request
//			bpContent = this.getBinaryParameterContent(parameterName);
//		}
		
		List<BinaryParameter> toRet = new ArrayList<BinaryParameter>();
		
		// in questa fase dopo la submit successiva alla scelta del file tramite una postback ricevo un array di contenuti vuoti
		// che devo ignorare per prelevare i valori dai file temporanei
		boolean ricevutoContenuto = bpContents != null && bpContents.size() >0;
		if(ricevutoContenuto) {
			for (int i = 0; i < bpContents.size() ; i++ ) {
				bpContent = bpContents.get(i);
				// come nel caso singolo, ma questa volta sull'array dei file
				if(!(bpContent != null && bpContent.length > 0)) {
					ricevutoContenuto = false;
					break;
				}
			}
		}
		
		if(ricevutoContenuto) {
			String fileNames = this.getParameter(ProtocolPropertiesCostanti.PARAMETER_FILENAME_PREFIX + parameterName);
			String fileIds = this.getParameter(ProtocolPropertiesCostanti.PARAMETER_FILEID_PREFIX+ parameterName);
			if(StringUtils.isNotBlank(fileIds) && StringUtils.isNotBlank(fileNames)){
				String[] ids = fileIds.split(",");
				for (String id : ids) {
					file = new File(getTmpDir() + File.separator + CostantiControlStation.TEMP_FILE_PREFIX +  id + CostantiControlStation.TEMP_FILE_SUFFIX);
					if(file.exists())
						file.delete();
				}
			}
			
			List<String> fileNamesParameter = this.getFileNamesParameter(parameterName);
			//salvataggio nuovo contenuto
			for (int i = 0; i < bpContents.size() ; i++ ) {
				bpContent = bpContents.get(i);
				filename = fileNamesParameter.get(i);
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
				
				BinaryParameter bp = newBinaryParameter(parameterName);
				bp.setValue(bpContent);
				bp.setFilename(filename); 
				bp.setId(fileId);  
				
				toRet.add(bp);
			}
		} else {
			// provo a ricostruire il valore dai campi hidden
			String fileNames = this.getParameter(ProtocolPropertiesCostanti.PARAMETER_FILENAME_PREFIX + parameterName);
			String fileIds = this.getParameter(ProtocolPropertiesCostanti.PARAMETER_FILEID_PREFIX+ parameterName);
			
			if(StringUtils.isNotBlank(fileIds) && StringUtils.isNotBlank(fileNames)){
				List<String> ids = Arrays.asList(fileIds.split(","));
				List<String> fileNamesParameter = Arrays.asList(fileNames.split(","));
				
				for (int i = 0; i < ids.size() ; i++ ) {
					fileId = ids.get(i);
					filename = fileNamesParameter.get(i);
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
					
					BinaryParameter bp = newBinaryParameter(parameterName);
					bp.setValue(bpContent);
					bp.setFilename(filename); 
					bp.setId(fileId);  
					
					toRet.add(bp);
				}
				
			}else {
				// serve per creare il caso del primo accesso
				bpContent = null;
				filename = null;
				fileId = null;
				
				BinaryParameter bp = newBinaryParameter(parameterName);
				bp.setValue(bpContent);
				bp.setFilename(filename); 
				bp.setId(fileId);  
				
				toRet.add(bp);
			}
			
		}
		
		// se ho provocato io l'evento di cancellazione via postback allora svuoto il contenuto
		if(this.isPostBack()) {
			String postBackElementName = this.getPostBackElementName();
			if(postBackElementName!=null && postBackElementName.startsWith(parameterName) && postBackElementName.contains(Costanti.PARAMETER_FILENAME_REMOVE_PLACEHOLDER)) {
				String[] split = postBackElementName.split(Costanti.PARAMETER_FILENAME_REMOVE_PLACEHOLDER);
				try {
					int pos = Integer.parseInt(split[1]);
					toRet.get(pos).setValue(null); // TODO
					toRet.get(pos).setFilename(null);
					toRet.get(pos).setId(null);  
				}catch(Exception e) {
					
				}
			}
		}
		
		return toRet;
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
						Boolean falseValue = null;
						if(item instanceof BooleanConsoleItem) {
							BooleanConsoleItem b = (BooleanConsoleItem) item;
							if(!b.isConvertFalseAsNull()) {
								falseValue = false;
							}
						}
						BooleanProperty booleanProperty = ProtocolPropertiesFactory.newProperty(item.getId(), booleanValue ? booleanValue : falseValue);
						if(primoAccessoAdd) {
							booleanProperty.setValue(((BooleanConsoleItem) item).getDefaultValue());
						}
						properties.addProperty(booleanProperty); 
						break;
					case STRING:
					default:
						StringProperty stringProperty = null;
						if(ConsoleItemType.MULTI_SELECT.equals(item.getType())) {
							String [] parameterValues = this.getParameterValues(item.getId());
							String value = parameterValues!=null && parameterValues.length>0 ? ProtocolPropertiesUtils.getValueMultiSelect(parameterValues) : null;
							stringProperty = ProtocolPropertiesFactory.newProperty(item.getId(), value);
						}
						else {
							String parameterValue = this.getParameter(item.getId());
							stringProperty = ProtocolPropertiesFactory.newProperty(item.getId(), parameterValue);
						}
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
			if(singlePdD==null) {
				singlePdD = this.core!=null ? this.core.isSinglePdD() : true;
			}

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


					dimensioneEntries = 6; // configurazione, tracciamento, controllo del traffico, policy, aa e audit
					
					if(this.core.isConfigurazioneAllarmiEnabled())
						dimensioneEntries++; // configurazione allarmi
					
					dimensioneEntries++; // gruppi

					if(!isModalitaStandard()) {
						dimensioneEntries++; // caches
					}
					
					if(this.core.isShowPulsantiImportExport() && pu.isServizi()){
						dimensioneEntries++; // importa
						if(exporterUtils.existsAtLeastOneExportMode(ArchiveType.CONFIGURAZIONE, this.session)){
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
					if(!isModalitaStandard()) {
						entries[index][0] = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHES;
						entries[index][1] = ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE+"?"+
								ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CACHES+"="+Costanti.CHECK_BOX_ENABLED;
						index++;
					}
					entries[index][0] = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TRACCIAMENTO_MENU;
					entries[index][1] = ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI;
					index++;
					entries[index][0] = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CONTROLLO_TRAFFICO;
					entries[index][1] = ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO;
					index++;
					if(this.core.isConfigurazioneAllarmiEnabled()) { // configurazione allarmi
						entries[index][0] = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ALLARMI;
						entries[index][1] = ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_ALLARMI_LIST;
						index++;	
					}
					entries[index][0] = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN;
					entries[index][1] = ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN_LIST+"?"+
							ConfigurazioneCostanti.PARAMETRO_TOKEN_POLICY_TIPOLOGIA_INFORMAZIONE+"="+ConfigurazioneCostanti.PARAMETRO_TOKEN_POLICY_TIPOLOGIA_INFORMAZIONE_VALORE_TOKEN;
					index++;
					entries[index][0] = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY;
					entries[index][1] = ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN_LIST+"?"+
							ConfigurazioneCostanti.PARAMETRO_TOKEN_POLICY_TIPOLOGIA_INFORMAZIONE+"="+ConfigurazioneCostanti.PARAMETRO_TOKEN_POLICY_TIPOLOGIA_INFORMAZIONE_VALORE_ATTRIBUTE_AUTHORITY;
					index++;
					
					entries[index][0] = GruppiCostanti.LABEL_GRUPPI;
					entries[index][1] = GruppiCostanti.SERVLET_NAME_GRUPPI_LIST;
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
						if(exporterUtils.existsAtLeastOneExportMode(ArchiveType.CONFIGURAZIONE, this.session)){
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
						if(exporterUtils.existsAtLeastOneExportMode(ArchiveType.CONFIGURAZIONE, this.session)){
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
							if(exporterUtils.existsAtLeastOneExportMode(ArchiveType.CONFIGURAZIONE, this.session)){
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
						if(exporterUtils.existsAtLeastOneExportMode(ArchiveType.CONFIGURAZIONE, this.session)){
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
						if(exporterUtils.existsAtLeastOneExportMode(ArchiveType.CONFIGURAZIONE, this.session)){
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
						if(exporterUtils.existsAtLeastOneExportMode(ArchiveType.CONFIGURAZIONE, this.session)){
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
							if(exporterUtils.existsAtLeastOneExportMode(ArchiveType.CONFIGURAZIONE, this.session)){
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
	
	public boolean checkNCNameAndSerial(String name, String object) throws Exception{
		// Il nome deve contenere solo lettere e numeri e '_' '-' '.'
		if (!RegularExpressionEngine.isMatch(name,"^[_A-Za-z][\\-\\._A-Za-z0-9]*@[0-9]*$")) {
			this.pd.setMessage("Il campo '"+object+"' può iniziare solo con un carattere [A-Za-z] o il simbolo '_' e dev'essere formato solo da caratteri, cifre, '_' , '-' e '.', infine deve terminare con @ e numeri interi");
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
	
	public boolean checkLength4000(String value, String object) throws Exception{
		return this.checkLength(value, object, -1, 4000);
	}
	public boolean checkLength255(String value, String object) throws Exception{
		return this.checkLength(value, object, -1, 255);
	}
	public boolean checkLengthSubject_SSL_Principal(String value, String object) throws Exception{
		return this.checkLength(value, object, -1, 2800);
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
	
	public boolean checkSpazi(String value, String object) throws Exception{
		if(value.contains(" ")) {
			this.pd.setMessage("L'informazione fornita nel campo '"+object+"' non deve contenere spazi");
			return false;
		}
		return true;
	}

	public boolean checkEmail(String email, String object) throws Exception{
		// Email deve rispettare il pattern
		if (!RegularExpressionEngine.isMatch(email,CostantiControlStation.EMAIL_PATTERN)) {
			this.pd.setMessage("Il campo '"+object+"' non contiene un indirizzo e-mail valido");
			return false;
		}
		return true;
	}
	
	// *** Utilities condivise tra Porte Delegate e Porte Applicative ***
	
	public Vector<DataElement> addPorteServizioApplicativoToDati(TipoOperazione tipoOp, Vector<DataElement> dati, 
			String servizioApplicativo, String[] servizioApplicativoList, int sizeAttuale, 
			boolean addMsgServiziApplicativoNonDisponibili, boolean addTitle) {
		
		if(servizioApplicativoList!=null && servizioApplicativoList.length>0){
		
			String labelApplicativo = CostantiControlStation.LABEL_PARAMETRO_SERVIZIO_APPLICATIVO;
			if(!this.isModalitaCompleta()) {
				labelApplicativo = CostantiControlStation.LABEL_PARAMETRO_APPLICATIVO;
			}
			
			if(addTitle) {
				DataElement de = new DataElement();
				de.setType(DataElementType.TITLE);
				de.setLabel(labelApplicativo);
				dati.addElement(de);
			}
			
			DataElement de = new DataElement();
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
				boolean addMsgSoggettiNonDisponibili, boolean addTitle) {
			
			if(soggettiList!=null && soggettiList.length>0){
			
				if(addTitle) {
					DataElement de = new DataElement();
					de.setType(DataElementType.TITLE);
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_SOGGETTO);
					dati.addElement(de);
				}
				
				DataElement de = new DataElement();
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
			Map<String,List<IDServizioApplicativoDB>> listServiziApplicativi, String sa,
			boolean addMsgApplicativiNonDisponibili, boolean showTitle, boolean modipa) {
			
		if(modipa) {
			DataElement	de = new DataElement();
			de.setType(DataElementType.HIDDEN);
			de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_MODIPA);
			de.setValue("true");
			dati.addElement(de);
		}
		
		if(soggettiList!=null && soggettiList.length>0 && listServiziApplicativi!=null && listServiziApplicativi.size()>0){
		
			if(showTitle) {
				DataElement de = new DataElement();
				de.setType(DataElementType.TITLE);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_APPLICATIVO);
				dati.addElement(de);
			}
			
			DataElement de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_SOGGETTO);
			de.setName(CostantiControlStation.PARAMETRO_SOGGETTO);
			de.setValue(soggetto);
			if(this.core.isMultitenant() || modipa) {
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
			
			List<IDServizioApplicativoDB> listSA = null;
			if(soggetto!=null && !"".equals(soggetto)) {
				listSA = listServiziApplicativi.get(soggetto);
			}
			
			if(listSA!=null && !listSA.isEmpty()) {
				
				String [] saValues = new String[listSA.size()];
				String [] saLabels = new String[listSA.size()];
				int index =0;
				for (IDServizioApplicativoDB saObject : listSA) {
					saValues[index] = saObject.getId().longValue()+"";
					saLabels[index] = saObject.getNome();
					index++;
				}
				
				de = new DataElement();
				if(showTitle) {
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_NOME);
				}
				else {
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_APPLICATIVO);
				}
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
							valida = this.checkPropertiesConfigurationData(TipoOperazione.OTHER, configurazioneBean, null, null, configurazione);
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
							valida = this.checkPropertiesConfigurationData(TipoOperazione.OTHER, configurazioneBean, null, null, configurazione);
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
			StringBuilder existsMessage = new StringBuilder();
			if(portaDelegata) {
				giaRegistrato = ConsoleUtilities.alreadyExistsCorrelazioneApplicativaRichiesta(this.porteDelegateCore, idInt, elemxml, idcorrInt, existsMessage);
			}
			else {
				giaRegistrato = ConsoleUtilities.alreadyExistsCorrelazioneApplicativaRichiesta(this.porteApplicativeCore, idInt, elemxml, idcorrInt, existsMessage);
			}
			
			if (giaRegistrato) {
				this.pd.setMessage(existsMessage.toString());
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
			StringBuilder existsMessage = new StringBuilder();
			if(portaDelegata) {
				giaRegistrato = ConsoleUtilities.alreadyExistsCorrelazioneApplicativaRisposta(this.porteDelegateCore, idInt, elemxml, idcorrInt, existsMessage);
			}
			else {
				giaRegistrato = ConsoleUtilities.alreadyExistsCorrelazioneApplicativaRisposta(this.porteApplicativeCore, idInt, elemxml, idcorrInt, existsMessage);
			}
			
			if (giaRegistrato) {
				this.pd.setMessage(existsMessage.toString());
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
			String contentType, String obbligatorio, MTOMProcessorType type) {

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
		de.setRequired(true);
		DataElementInfo dInfoPattern = new DataElementInfo(CostantiControlStation.LABEL_PARAMETRO_NOME);
		switch (type) {
		case PACKAGING:
		case VERIFY:
			dInfoPattern.setBody(CostantiControlStation.LABEL_CONFIGURAZIONE_MTOM_INFO_NOME_SOAP_PACKAGE);
			break;
		default:
			dInfoPattern = null;
			break;
		}
		de.setInfo(dInfoPattern);
		dati.addElement(de);

		// Pattern
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PATTERN);
		de.setValue(pattern);
		de.setType(DataElementType.TEXT_AREA);
		de.setName(CostantiControlStation.PARAMETRO_PATTERN);
		de.setSize(this.getSize());
		de.setRequired(true);
		dInfoPattern = new DataElementInfo(CostantiControlStation.LABEL_PARAMETRO_PATTERN);
		switch (type) {
		case PACKAGING:
			dInfoPattern.setBody(CostantiControlStation.LABEL_CONFIGURAZIONE_MTOM_INFO_PATTERN_SOAP_PACKAGE);
			break;
		case VERIFY:
			dInfoPattern.setBody(CostantiControlStation.LABEL_CONFIGURAZIONE_MTOM_INFO_PATTERN_SOAP_VERIFY);
			break;
		default:
			dInfoPattern = null;
			break;
		}
		de.setInfo(dInfoPattern);
		dati.addElement(de);

		// Content-type
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONTENT_TYPE); 
		de.setValue(contentType);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(CostantiControlStation.PARAMETRO_CONTENT_TYPE);
		de.setSize(this.getSize());
		dInfoPattern = new DataElementInfo(CostantiControlStation.LABEL_PARAMETRO_CONTENT_TYPE);
		switch (type) {
		case PACKAGING:
			dInfoPattern.setBody(CostantiControlStation.LABEL_CONFIGURAZIONE_MTOM_INFO_CONTENT_TYPE_SOAP_PACKAGE);
			break;
		case VERIFY:
			dInfoPattern.setBody(CostantiControlStation.LABEL_CONFIGURAZIONE_MTOM_INFO_CONTENT_TYPE_SOAP_VERIFY);
			break;
		default:
			dInfoPattern = null;
			break;
		}
		de.setInfo(dInfoPattern);
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

	public Vector<DataElement> addProtocolPropertiesToDatiRegistry(Vector<DataElement> dati, ConsoleConfiguration consoleConfiguration,ConsoleOperationType consoleOperationType,
			ProtocolProperties protocolProperties) throws Exception{
		return addProtocolPropertiesToDatiRegistry(dati, consoleConfiguration, consoleOperationType, protocolProperties, null, null);
	}
	public Vector<DataElement> addProtocolPropertiesToDatiRegistry(Vector<DataElement> dati, ConsoleConfiguration consoleConfiguration,ConsoleOperationType consoleOperationType,
			ProtocolProperties protocolProperties, List<ProtocolProperty> listaProtocolPropertiesDaDB ,Properties binaryPropertyChangeInfoProprietario) throws Exception{
		
		String titleId = null;
		String endTitleId = null;
		
		String subtitleId = null;
		String endSubtitleId = null;
		
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

			ProtocolProperty protocolProperty = ProtocolPropertiesUtils.getProtocolPropertyRegistry(item.getId(), listaProtocolPropertiesDaDB); 
			dati = ProtocolPropertiesUtilities.itemToDataElement(dati,this,item, defaultItemValue,
					consoleOperationType, binaryPropertyChangeInfoProprietario, protocolProperty, this.getSize());
			
			if(ConsoleItemType.TITLE.equals(item.getType()) && item instanceof TitleConsoleItem) {
				TitleConsoleItem titleItem = (TitleConsoleItem) item;
				if(titleItem.isCloseable()) {
					titleId = titleItem.getId();
					endTitleId = titleItem.getLastItemId();
				}
			}
			else if(ConsoleItemType.SUBTITLE.equals(item.getType()) && item instanceof SubtitleConsoleItem) {
				SubtitleConsoleItem subItem = (SubtitleConsoleItem) item;
				if(subItem.isCloseable()) {
					subtitleId = subItem.getId();
					endSubtitleId = subItem.getLastItemId();
				}
			}
			else {
				if(endTitleId!=null && endTitleId.equals(item.getId())) {
					this.impostaAperturaTitle(dati, titleId);
				}
				else if(endSubtitleId!=null && endSubtitleId.equals(item.getId())) {
					this.impostaAperturaSubTitle(dati, subtitleId);
				}
			}
		}

		// Imposto il flag per indicare che ho caricato la configurazione
		DataElement de = new DataElement();
		de.setName(ProtocolPropertiesCostanti.PARAMETRO_PP_SET);
		de.setType(DataElementType.HIDDEN);
		de.setValue("ok");
		dati.add(de);

		return dati;
	}
	
	public Vector<DataElement> addProtocolPropertiesToDatiConfig(Vector<DataElement> dati, ConsoleConfiguration consoleConfiguration,ConsoleOperationType consoleOperationType,
			ProtocolProperties protocolProperties) throws Exception{
		return addProtocolPropertiesToDatiRegistry(dati, consoleConfiguration, consoleOperationType, protocolProperties, null, null);
	}
	public Vector<DataElement> addProtocolPropertiesToDatiConfig(Vector<DataElement> dati, ConsoleConfiguration consoleConfiguration,ConsoleOperationType consoleOperationType,
			ProtocolProperties protocolProperties, List<org.openspcoop2.core.config.ProtocolProperty> listaProtocolPropertiesDaDB ,Properties binaryPropertyChangeInfoProprietario) throws Exception{
	
		String titleId = null;
		String endTitleId = null;
		
		String subtitleId = null;
		String endSubtitleId = null;
		
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
			
			org.openspcoop2.core.config.ProtocolProperty protocolProperty = ProtocolPropertiesUtils.getProtocolPropertyConfig(item.getId(), listaProtocolPropertiesDaDB); 
			dati = ProtocolPropertiesUtilities.itemToDataElement(dati,this,item, defaultItemValue,
					consoleOperationType, binaryPropertyChangeInfoProprietario, protocolProperty, this.getSize());
			
			if(ConsoleItemType.TITLE.equals(item.getType()) && item instanceof TitleConsoleItem) {
				TitleConsoleItem titleItem = (TitleConsoleItem) item;
				if(titleItem.isCloseable()) {
					titleId = titleItem.getId();
					endTitleId = titleItem.getLastItemId();
				}
			}
			else if(ConsoleItemType.SUBTITLE.equals(item.getType()) && item instanceof SubtitleConsoleItem) {
				SubtitleConsoleItem subItem = (SubtitleConsoleItem) item;
				if(subItem.isCloseable()) {
					subtitleId = subItem.getId();
					endSubtitleId = subItem.getLastItemId();
				}
			}
			else {
				if(endTitleId!=null && endTitleId.equals(item.getId())) {
					this.impostaAperturaTitle(dati, titleId);
				}
				else if(endSubtitleId!=null && endSubtitleId.equals(item.getId())) {
					this.impostaAperturaSubTitle(dati, subtitleId);
				}
			}
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
			ProtocolProperties protocolProperties) throws Exception{
		return addProtocolPropertiesToDatiAsHidden(dati, consoleConfiguration, consoleOperationType, protocolProperties, null, null);
	}

	public Vector<DataElement> addProtocolPropertiesToDatiAsHidden(Vector<DataElement> dati, ConsoleConfiguration consoleConfiguration,ConsoleOperationType consoleOperationType,
			ProtocolProperties protocolProperties, List<ProtocolProperty> listaProtocolPropertiesDaDB ,Properties binaryPropertyChangeInfoProprietario) throws Exception{
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

			ProtocolProperty protocolProperty = ProtocolPropertiesUtils.getProtocolPropertyRegistry(item.getId(), listaProtocolPropertiesDaDB); 
			dati = ProtocolPropertiesUtilities.itemToDataElementAsHidden(dati,item, defaultItemValue,
					consoleOperationType, binaryPropertyChangeInfoProprietario, protocolProperty, this.getSize());
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

	public void validaProtocolProperties(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ProtocolProperties properties) throws ProtocolException{
		try {
			List<BaseConsoleItem> consoleItems = consoleConfiguration.getConsoleItem();
			for (int i = 0; i < properties.sizeProperties(); i++) {
				AbstractProperty<?> property = properties.getProperty(i);
				AbstractConsoleItem<?> consoleItem = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleItems, property);

				if(consoleItem != null) {
					if(!ConsoleItemType.HIDDEN.equals(consoleItem.getType())) {
						if(consoleItem instanceof StringConsoleItem){
							StringProperty sp = (StringProperty) property;
							if (consoleItem.isRequired() && StringUtils.isEmpty(sp.getValue())) {
								if(consoleItem.getLabel()==null || "".equals(consoleItem.getLabel())) {
									throw new ProtocolException(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI);
								}
								else {
									throw new ProtocolException(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, consoleItem.getLabel()));
								}
							}
	
							if(StringUtils.isNotEmpty(consoleItem.getRegexpr())){
								if(!RegularExpressionEngine.isMatch(sp.getValue(),consoleItem.getRegexpr())){
									throw new ProtocolException(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_IL_CAMPO_XX_DEVE_RISPETTARE_IL_PATTERN_YY, consoleItem.getLabel(), consoleItem.getRegexpr()));
								}
							}
							
							if(StringUtils.isNotEmpty(sp.getValue())) {
								try {
									if(!this.checkLength4000(sp.getValue(), consoleItem.getLabel())) {
										throw new ProtocolException(this.pd.getMessage());
									}
								}catch(Exception e) {
									throw new ProtocolException(e.getMessage(),e);
								}
							}
						}
						else if(consoleItem instanceof NumberConsoleItem){
							NumberProperty np = (NumberProperty) property;
							if (consoleItem.isRequired() && np.getValue() == null) {
								if(consoleItem.getLabel()==null || "".equals(consoleItem.getLabel())) {
									throw new ProtocolException(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI);
								}
								else {
									throw new ProtocolException(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, consoleItem.getLabel()));
								}
							}
							if(np.getValue()!=null) {
								if(ConsoleItemType.NUMBER.equals(consoleItem.getType()) && (consoleItem instanceof NumberConsoleItem)) {
									long v = np.getValue();
									NumberConsoleItem nci = (NumberConsoleItem) consoleItem;
									if(v<nci.getMin()) {
										throw new ProtocolException(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_VALORE_MINORE_DEL_MINIMO, consoleItem.getLabel(), nci.getMin()));
									}
									if(v>nci.getMax()) {
										throw new ProtocolException(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_VALORE_MINORE_DEL_MASSIMO, consoleItem.getLabel(), nci.getMax()));
									}
								}
							}
						}
						else if(consoleItem instanceof BinaryConsoleItem){
							BinaryProperty bp = (BinaryProperty) property;
							if (consoleOperationType.equals(ConsoleOperationType.ADD) && consoleItem.isRequired() && (bp.getValue() == null || bp.getValue().length == 0)) {
								if(consoleItem.getLabel()==null || "".equals(consoleItem.getLabel())) {
									throw new ProtocolException(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI);
								}
								else {
									throw new ProtocolException(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, consoleItem.getLabel()));
								}
							}
						}
						else if(consoleItem instanceof BooleanConsoleItem){
							BooleanProperty bp = (BooleanProperty) property;
							// le checkbox obbligatorie non dovrebbero esserci...
							if (consoleItem.isRequired() && bp.getValue() == null) {
								if(consoleItem.getLabel()==null || "".equals(consoleItem.getLabel())) {
									throw new ProtocolException(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI);
								}
								else {
									throw new ProtocolException(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, consoleItem.getLabel()));
								}
							}
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

	public void validaProtocolPropertyBinaria(String nome, ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ProtocolProperties properties) throws ProtocolException{
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
	
	public List<String> convertFromDataElementValue_parametroAutenticazioneList(String autenticazione, TipoAutenticazionePrincipal autenticazionePrincipal) throws Exception{
		List<String> l = null;
		
		if(TipoAutenticazione.BASIC.equals(autenticazione)) {
			
			// posizione 0: clean
			String v = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+0);
			if(v!=null && !"".equals(v)) {
				l = new ArrayList<>();
				l.add(v);
			}
			
		}
		else if(TipoAutenticazione.PRINCIPAL.equals(autenticazione)) {
								
			if(autenticazionePrincipal==null) {
				autenticazionePrincipal = TipoAutenticazionePrincipal.CONTAINER;	
			}
			
			switch (autenticazionePrincipal) {
			case CONTAINER:
			case INDIRIZZO_IP:
			case INDIRIZZO_IP_X_FORWARDED_FOR:
				break;
			case HEADER:
			case FORM:
				
				// posizione 0: nome
				String v = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+0);
				if(v!=null && !"".equals(v)) {
					l = new ArrayList<>();
					l.add(v);
				}
				
				// posizione 1: clean
				if(l==null) {
					break;
				}
				v = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+1);
				if(v!=null && !"".equals(v)) {
					l.add(v);
				}

				break;
			case URL:
				
				// posizione 0: pattern
				v = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+0);
				if(v!=null && !"".equals(v)) {
					l = new ArrayList<>();
					l.add(v);
				}
				break;
			case TOKEN:
				
				// posizione 0: tipoToken
				v = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+0);
				if(v!=null && !"".equals(v)) {
					l = new ArrayList<>();
					l.add(v);
				}
				
				// posizione 1: nome claim proprietario
				if(l==null) {
					break;
				}
				v = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+1);
				if(v!=null && !"".equals(v)) {
					l.add(v);
				}

				break;
			}
		}
		else if(TipoAutenticazione.APIKEY.equals(autenticazione)) {
			
			// DATA ELEMENT
			// 0: appId (CHECKBOX)
			// 1: posizione (MULTI_SELECT)
			// 2: useOAS3Names (CHECKBOX)
			// 3: forwardApiKey (CHECKBOX)
			// 4: queryParameterApiKey (TEXT_EDIT)
			// 5: headerApiKey (TEXT_EDIT)
			// 6: cookieApiKey (TEXT_EDIT)
			// 7: forwardAppId (CHECKBOX)
			// 8: queryParameterAppId (TEXT_EDIT)
			// 9: headerAppId (TEXT_EDIT)
			// 10: cookieAppId (TEXT_EDIT)
			
			l = new ArrayList<>();
			
			// posizione 0: appId
			String v = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+0);
			l.add(ServletUtils.isCheckBoxEnabled(v)+"");
			
			// posizione 1: queryParameter
			// posizione 2: header
			// posizione 3: cookie
			String [] vPos = this.getParameterValues(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+1);
			boolean query = false;
			boolean header = false;
			boolean cookie = false;
			if(vPos!=null && vPos.length>0) {
				for (int i = 0; i < vPos.length; i++) {
					String sel = vPos[i];
					if(ParametriAutenticazioneApiKey.QUERY_PARAMETER.equals(sel)) {
						query = true;
					}
					else if(ParametriAutenticazioneApiKey.HEADER.equals(sel)) {
						header = true;
					}
					else if(ParametriAutenticazioneApiKey.COOKIE.equals(sel)) {
						cookie = true;
					}
				}
			}
			l.add(query+"");
			l.add(header+"");
			l.add(cookie+"");
			
			// posizione 4: useOAS3Names
			v = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+2);
			l.add(ServletUtils.isCheckBoxEnabled(v)+"");
			
			// posizione 5: cleanApiKey
			String forwardApiKey = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+3);
			if (ServletUtils.isCheckBoxEnabled(forwardApiKey)) {
				l.add(false+"");
			}
			else {
				l.add(true+"");
			}

			// posizione 6: cleanAppId
			String forwardAppId = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+7);
			if (ServletUtils.isCheckBoxEnabled(forwardAppId)) {
				l.add(false+"");
			}
			else {
				l.add(true+"");
			}

			// posizione 7: queryParameterApiKey
			v = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+4);
			l.add(v);
			
			// posizione 8: headerApiKey
			v = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+5);
			l.add(v);
			
			// posizione 9: cookieApiKey
			v = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+6);
			l.add(v);
			
			// posizione 10: queryParameterAppId
			v = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+8);
			l.add(v);
			
			// posizione 11: headerAppId
			v = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+9);
			l.add(v);
			
			// posizione 12: cookieAppId
			v = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+10);
			l.add(v);
		}
		
		return l;
	}
	
	public void controlloAccessiAdd(Vector<DataElement> dati, TipoOperazione tipoOperazione, String statoControlloAccessiAdd, boolean forceAutenticato) {
		
		if(!this.isModalitaCompleta() && TipoOperazione.ADD.equals(tipoOperazione)) {
			
			if(!forceAutenticato) {
				DataElement de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI );
				de.setType(DataElementType.TITLE);
				dati.addElement(de);
			}
			
			DataElement de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_STATO);
			de.setName(CostantiControlStation.PARAMETRO_PORTE_CONTROLLO_ACCESSI_STATO);
			if(forceAutenticato) {
				de.setType(DataElementType.HIDDEN);
				de.setValue(CostantiControlStation.VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_STATO_AUTENTICATO);
			}
			else {
				de.setType(DataElementType.SELECT);
				de.setValues(CostantiControlStation.SELECT_VALUES_PARAMETRO_PORTE_CONTROLLO_ACCESSI_STATO);
				de.setLabels(CostantiControlStation.SELECT_VALUES_PARAMETRO_PORTE_CONTROLLO_ACCESSI_STATO);
				de.setSelected(statoControlloAccessiAdd);
			}
			dati.addElement(de);
			
		}
		
	}
			
	
	public void controlloAccessiAutenticazione(Vector<DataElement> dati, TipoOperazione tipoOperazione, String servletChiamante, Object oggetto, String protocolloParam,
			String autenticazione, String autenticazioneCustom, String autenticazioneOpzionale,
			TipoAutenticazionePrincipal autenticazionePrincipal,  List<String> autenticazioneParametroList,
			boolean confPers, boolean isSupportatoAutenticazioneSoggetti,boolean isPortaDelegata,
			String gestioneToken,String gestioneTokenPolicy,String autenticazioneTokenIssuer,String autenticazioneTokenClientId,String autenticazioneTokenSubject,String autenticazioneTokenUsername,String autenticazioneTokenEMail,
			boolean old_autenticazione_custom, String urlAutenticazioneCustomProperties, int numAutenticazioneCustomPropertiesList,
			boolean forceHttps, boolean forceDisableOptional) throws Exception{
		
		boolean tokenAbilitato = StatoFunzionalita.ABILITATO.getValue().equalsIgnoreCase(gestioneToken) &&
				gestioneTokenPolicy != null && !gestioneTokenPolicy.equals(CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO);
		
		boolean mostraSezione = !tipoOperazione.equals(TipoOperazione.ADD) || 
				(isPortaDelegata ? this.core.isEnabledAutenticazione_generazioneAutomaticaPorteDelegate() : this.core.isEnabledAutenticazione_generazioneAutomaticaPorteApplicative());
		
		boolean allHidden = false;
		if(!this.isModalitaCompleta() && TipoOperazione.ADD.equals(tipoOperazione)) {
			allHidden = true;
		}
		
		String protocollo = protocolloParam;
		if((protocollo==null || "".equals(protocollo)) && oggetto!=null){
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
		
		boolean existsAutorizzazioniPuntuali = false;
		if(oggetto!=null) {
			if(isPortaDelegata){
				PortaDelegata pd = (PortaDelegata) oggetto;
				if(pd!=null && pd.sizeServizioApplicativoList()>0) {
					existsAutorizzazioniPuntuali = true;
				}
			}
			else {
				PortaApplicativa pa = (PortaApplicativa) oggetto;
				if(pa!=null) {
					if(pa.getSoggetti()!=null && pa.getSoggetti().sizeSoggettoList()>0) {
						existsAutorizzazioniPuntuali = true;
					}
					else if(pa.getServiziApplicativiAutorizzati()!=null && pa.getServiziApplicativiAutorizzati().sizeServizioApplicativoList()>0) {
						existsAutorizzazioniPuntuali = true;
					}
				}
			}
		}
		
		boolean modipa = this.isProfiloModIPA(protocollo);
		
		if(forceHttps) {
			autenticazione = TipoAutenticazione.SSL.getValue(); 
		}
		if(forceDisableOptional) {
			// Fix: se l'autenticazioneOpzionale risulta abilitata e sono nella servlet di cambio controllo degli accessi, significa che siamo arrivati da una condizione in cui l'erogazione era stata creata con API C01 e poi successivamente l'API è stata modificata in C02
			if(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI.equals(servletChiamante) && ServletUtils.isCheckBoxEnabled(autenticazioneOpzionale)) {
				forceDisableOptional=false;
			}
			else {
				autenticazioneOpzionale = Costanti.CHECK_BOX_DISABLED;
			}
		}
				
		if(mostraSezione){
			
			if(tokenAbilitato) {

				if(!allHidden) {
					DataElement de = new DataElement();
					de.setType(DataElementType.SUBTITLE); //SUBTITLE);
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE_TOKEN);
					dati.addElement(de);
				}
				
				DataElement de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_ISSUER);
				de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_ISSUER);
				if(allHidden) {
					de.setType(DataElementType.HIDDEN);
					de.setValue(ServletUtils.isCheckBoxEnabled(autenticazioneTokenIssuer)+"");
				}
				else {
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(ServletUtils.isCheckBoxEnabled(autenticazioneTokenIssuer));
				}
				dati.addElement(de);
				
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_CLIENT_ID);
				de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_CLIENT_ID);
				if(allHidden) {
					de.setType(DataElementType.HIDDEN);
					de.setValue(ServletUtils.isCheckBoxEnabled(autenticazioneTokenClientId)+"");
				}
				else {
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(ServletUtils.isCheckBoxEnabled(autenticazioneTokenClientId));
				}
				dati.addElement(de);
				
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_SUBJECT);
				de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_SUBJECT);
				if(allHidden) {
					de.setType(DataElementType.HIDDEN);
					de.setValue(ServletUtils.isCheckBoxEnabled(autenticazioneTokenSubject)+"");
				}
				else {
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(ServletUtils.isCheckBoxEnabled(autenticazioneTokenSubject));
				}
				dati.addElement(de);
				
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_USERNAME);
				de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_USERNAME);
				if(allHidden) {
					de.setType(DataElementType.HIDDEN);
					de.setValue(ServletUtils.isCheckBoxEnabled(autenticazioneTokenUsername)+"");
				}
				else {
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(ServletUtils.isCheckBoxEnabled(autenticazioneTokenUsername));
				}
				dati.addElement(de);
				
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_MAIL);
				de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_MAIL);
				if(allHidden) {
					de.setType(DataElementType.HIDDEN);
					de.setValue(ServletUtils.isCheckBoxEnabled(autenticazioneTokenEMail)+"");
				}
				else {
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(ServletUtils.isCheckBoxEnabled(autenticazioneTokenEMail));
				}
				dati.addElement(de);
				
			}
			
			boolean addTitle = !allHidden && isSupportatoAutenticazioneSoggetti;
			if(addTitle) {
				DataElement de = new DataElement();
				de.setType(DataElementType.TITLE); //SUBTITLE);
				de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TITLE);
				if(modipa && !isPortaDelegata) {
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE+" "+CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE_CANALE);
				}
				else {
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE+" "+CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE_TRASPORTO);
				}
				de.setStatoAperturaSezioni(STATO_APERTURA_SEZIONI.APERTO);
				dati.addElement(de);
			}
			
			if(isSupportatoAutenticazioneSoggetti){
				
//				if(!allHidden) {
//					DataElement de = new DataElement();
//					de.setType(DataElementType.SUBTITLE); //SUBTITLE);
//					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE_TRASPORTO);
//					dati.addElement(de);
//				}
			
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
					labelTipoAutenticazione[totEl-1] = CostantiControlStation.DEFAULT_LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM;
				}
				DataElement de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE);
				de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE);
				if(allHidden) {
					de.setType(DataElementType.HIDDEN);
					de.setValue(autenticazione);
				}
				else {
					if(forceHttps) {
						de.setType(DataElementType.HIDDEN);
						de.setValue(autenticazione);
						dati.addElement(de);
						
						de = new DataElement();
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE);
						de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE+"__LABEL");
						de.setType(DataElementType.TEXT);
						de.setValue(TipoAutenticazione.SSL.getLabel());
						de.setValoreDefault(TipoAutenticazione.DISABILITATO.getLabel());
					}
					else if(existsAutorizzazioniPuntuali) {
						de.setType(DataElementType.HIDDEN);
						de.setValue(autenticazione);
						dati.addElement(de);
						
						de = new DataElement();
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE);
						de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE+"__LABEL");
						de.setType(DataElementType.TEXT);
						String labelAutenticazione = null;
						for (int i = 0; i < tipoAutenticazione.length; i++) {
							if(autenticazione!=null && autenticazione.equals(tipoAutenticazione[i])) {
								labelAutenticazione = labelTipoAutenticazione[i];
								break;
							}
						}
						de.setValue(labelAutenticazione!=null ? labelAutenticazione : autenticazione);
						de.setValoreDefault(TipoAutenticazione.DISABILITATO.getLabel());
					}
					else {
						de.setType(DataElementType.SELECT);
						de.setValues(tipoAutenticazione);
						de.setLabels(labelTipoAutenticazione);
						//		de.setOnChange("CambiaTipoAuth('" + tipoOp + "', " + numCorrApp + ")");
						de.setPostBack(true);
						de.setSelected(autenticazione);
						de.setValoreDefaultSelect(TipoAutenticazione.DISABILITATO.getValue());
					}
				}
				dati.addElement(de);
		
				String tipoAutenticazioneCustom = isPortaDelegata ? Filtri.FILTRO_RUOLO_VALORE_FRUIZIONE : Filtri.FILTRO_RUOLO_VALORE_EROGAZIONE;
				boolean autenticazioneCustomHidden = (allHidden || (autenticazione == null) || (!autenticazione.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM)));
				this.addCustomField(TipoPlugin.AUTENTICAZIONE,
						tipoAutenticazioneCustom,
						null,
						CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE,
						CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM, 
						CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM, 
						autenticazioneCustom, autenticazioneCustomHidden, dati,
						false); 	
				/*
				de = new DataElement();
				de.setLabel("");
				if(allHidden) {
					de.setType(DataElementType.HIDDEN);
				}
				else if (autenticazione == null ||
						!autenticazione.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM)) {
					de.setType(DataElementType.HIDDEN);
				}else {
					de.setType(DataElementType.TEXT_EDIT);
					de.setRequired(true);
				}
				de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM);
				de.setValue(autenticazioneCustom);
				dati.addElement(de);
				*/
				
				// se ho salvato il tipo custom faccio vedere il link alle proprieta'
				if(autenticazione != null && autenticazione.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM)) {
					if(old_autenticazione_custom) {
						Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
						de = new DataElement();
						de.setType(DataElementType.LINK);
						de.setUrl(urlAutenticazioneCustomProperties);
						String labelCustomProperties = CostantiControlStation.LABEL_PARAMETRO_AUTENTICAZIONE_CUSTOM_PROPERTIES; 
						if (contaListe) {
							ServletUtils.setDataElementCustomLabel(de,labelCustomProperties,Long.valueOf(numAutenticazioneCustomPropertiesList));
						} else {
							ServletUtils.setDataElementCustomLabel(de,labelCustomProperties);
						}
						dati.addElement(de);
					}
				}
						
				try {
					if(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE.equals(this.getPostBackElementName())) {
						autenticazioneParametroList = null;
					}
				}catch(Exception e) {}
				
				boolean addOpzionale = true;
				
				if(TipoAutenticazione.BASIC.equals(autenticazione)) {
					
					// posizione 0: clean
					String autenticazioneParametro = null;
					if(autenticazioneParametroList!=null && !autenticazioneParametroList.isEmpty()) {
						autenticazioneParametro = autenticazioneParametroList.get(0);
					}
					
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_BASIC_FORWARD);
					de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+0);
					if(allHidden) {
						de.setType(DataElementType.HIDDEN);
						de.setValue(ServletUtils.isCheckBoxEnabled(autenticazioneParametro)+"");
					}
					else {
						de.setType(DataElementType.CHECKBOX);
						if(autenticazioneParametro==null || "".equals(autenticazioneParametro)) {
							autenticazioneParametro = Costanti.CHECK_BOX_DISABLED;
						}
						de.setSelected(ServletUtils.isCheckBoxEnabled(autenticazioneParametro));
					}
					dati.addElement(de);
				}
				else if(TipoAutenticazione.PRINCIPAL.equals(autenticazione)) {
										
					List<String> autenticazionePrincipalValues = TipoAutenticazionePrincipal.getValues(tokenAbilitato);
					List<String> autenticazionePrincipalLabels = TipoAutenticazionePrincipal.getLabels(tokenAbilitato);
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_TIPO);
					de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_TIPO);
					if(autenticazionePrincipal==null) {
						autenticazionePrincipal = TipoAutenticazionePrincipal.CONTAINER;	
					}
					if(allHidden) {
						de.setType(DataElementType.HIDDEN);
						de.setValue(autenticazionePrincipal.getValue());
					}
					else {
						de.setType(DataElementType.SELECT);
						de.setValues(autenticazionePrincipalValues);
						de.setLabels(autenticazionePrincipalLabels);
						de.setPostBack(true);
						de.setSelected(autenticazionePrincipal.getValue());
					}
					dati.addElement(de);
					
					switch (autenticazionePrincipal) {
					case CONTAINER:
					case INDIRIZZO_IP:
					case INDIRIZZO_IP_X_FORWARDED_FOR:
						break;
					case HEADER:
					case FORM:
						
						// posizione 0: nome
						String autenticazioneParametro = null;
						if(autenticazioneParametroList!=null && !autenticazioneParametroList.isEmpty()) {
							autenticazioneParametro = autenticazioneParametroList.get(0);
						}
						de = new DataElement();
						if(TipoAutenticazionePrincipal.HEADER.equals(autenticazionePrincipal)) {
							de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_HEADER);
						}
						else {
							de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_FORM);
						}
						de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+0);
						de.setValue(autenticazioneParametro);
						if(allHidden) {
							de.setType(DataElementType.HIDDEN);
						}
						else {
							de.setType(DataElementType.TEXT_EDIT);
							de.setRequired(true);
						}
						dati.addElement(de);
						
						// posizione 1: clean
						if(autenticazioneParametroList!=null && !autenticazioneParametroList.isEmpty() &&
								autenticazioneParametroList.size()>1) {
							autenticazioneParametro = autenticazioneParametroList.get(1);
						}
						de = new DataElement();
						de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+1);
						if(TipoAutenticazionePrincipal.HEADER.equals(autenticazionePrincipal)) {
							de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_FORWARD_HEADER);
						}else {
							de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_FORWARD_FORM);
						}
						if(autenticazioneParametro==null || "".equals(autenticazioneParametro)) {
							autenticazioneParametro = Costanti.CHECK_BOX_DISABLED;
						}
						if(allHidden) {
							de.setType(DataElementType.HIDDEN);
							de.setValue(autenticazioneParametro);
						}
						else {
							de.setType(DataElementType.CHECKBOX);
							de.setSelected(ServletUtils.isCheckBoxEnabled(autenticazioneParametro));
						}
						dati.addElement(de);
						
						break;
						
					case URL:
						
						// posizione 0: pattern
						autenticazioneParametro = null;
						if(autenticazioneParametroList!=null && !autenticazioneParametroList.isEmpty()) {
							autenticazioneParametro = autenticazioneParametroList.get(0);
						}
						de = new DataElement();
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_ESPRESSIONE);
						de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+0);
						de.setValue(autenticazioneParametro);
						if(allHidden) {
							de.setType(DataElementType.HIDDEN);
						}
						else {
							de.setType(DataElementType.TEXT_AREA);
							de.setRequired(true);
						}
						dati.addElement(de);
						
						break;
						
					case TOKEN:
						
						// posizione 0: tipoToken
						autenticazioneParametro = null;
						if(autenticazioneParametroList!=null && !autenticazioneParametroList.isEmpty()) {
							autenticazioneParametro = autenticazioneParametroList.get(0);
						}
						de = new DataElement();
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_TOKEN_CLAIM);
						de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+0);
						de.setValue(autenticazioneParametro);
						if(allHidden) {
							de.setType(DataElementType.HIDDEN);
						}
						else {
							de.setType(DataElementType.SELECT);
							
							List<String> values = new ArrayList<>();
							//values.add(ParametriAutenticazionePrincipal.TOKEN_CLAIM_ISSUER);
							values.add(ParametriAutenticazionePrincipal.TOKEN_CLAIM_SUBJECT);
							values.add(ParametriAutenticazionePrincipal.TOKEN_CLAIM_CLIENT_ID);
							values.add(ParametriAutenticazionePrincipal.TOKEN_CLAIM_EMAIL);
							values.add(ParametriAutenticazionePrincipal.TOKEN_CLAIM_USERNAME);
							List<String> labels = new ArrayList<>();
							labels.addAll(values);
							values.add(ParametriAutenticazionePrincipal.TOKEN_CLAIM_CUSTOM);
							labels.add(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_TOKEN_CLAIM_PERSONALIZZATO);
							
							de.setValues(values);
							de.setLabels(labels);
							de.setSelected(autenticazioneParametro);
							de.setRequired(true);
							de.setPostBack(true);
						}
						dati.addElement(de);
						
						// posizione 1: nome claim proprietario
						boolean claimProprietario = ParametriAutenticazionePrincipal.TOKEN_CLAIM_CUSTOM.equals(autenticazioneParametro);
						if(claimProprietario) {
							if(autenticazioneParametroList!=null && !autenticazioneParametroList.isEmpty() &&
									autenticazioneParametroList.size()>1) {
								autenticazioneParametro = autenticazioneParametroList.get(1);
							}
							else {
								autenticazioneParametro = null;
							}
							de = new DataElement();
							de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+1);
							de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_TOKEN_CLAIM_PERSONALIZZATO_NOME);
							de.setValue(autenticazioneParametro);
							if(allHidden) {
								de.setType(DataElementType.HIDDEN);
							}
							else {
								de.setType(DataElementType.TEXT_EDIT);
								de.setRequired(true);
							}
							dati.addElement(de);
						}
						
						break;
					}
				}
				else if(TipoAutenticazione.APIKEY.equals(autenticazione)) {
					
					ApiKeyState apiKeyState = new ApiKeyState(autenticazioneParametroList);
										
					// 0: appId (CHECKBOX)
					// 1: posizione (MULTI_SELECT)
					// 2: useOAS3Names (CHECKBOX)
					// 3: forwardApiKey (CHECKBOX)
					// 4: queryParameterApiKey (TEXT_EDIT)
					// 5: headerApiKey (TEXT_EDIT)
					// 6: cookieApiKey (TEXT_EDIT)
					// 7: forwardAppId (CHECKBOX)
					// 8: queryParameterAppId (TEXT_EDIT)
					// 9: headerAppId (TEXT_EDIT)
					// 10: cookieAppId (TEXT_EDIT)
					
					// appId				
					de = new DataElement();
					de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+0);
					de.setValue(apiKeyState.appId);
					if(allHidden) {
						de.setType(DataElementType.HIDDEN);
					}
					else {
						de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID);
						de.setType(DataElementType.CHECKBOX);
						de.setSelected(apiKeyState.appIdSelected);
						de.setPostBack(true);
					}
					dati.addElement(de);
					
					// opzionale
					addOpzionale(dati, allHidden, forceDisableOptional, autenticazioneOpzionale, autenticazione);
					addOpzionale = false;
					
					// posizione
					de = new DataElement();
					de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+1);
					if(allHidden) {
						de.setType(DataElementType.HIDDEN);
						de.setSelezionati(apiKeyState.posizioneSelected);
					}
					else {
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_APIKEY_POSIZIONE);
						de.setType(DataElementType.MULTI_SELECT);
						de.setSelezionati(apiKeyState.posizioneSelected);
						de.setValues(apiKeyState.posizioneValues);
						de.setLabels(apiKeyState.posizioneLabels);
						de.setRows(3);
						de.setPostBack(!apiKeyState.useOAS3NamesSelected);
					}
					dati.addElement(de);
					
					// useOAS3Names				
					de = new DataElement();
					de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+2);
					de.setValue(apiKeyState.useOAS3Names);
					if(allHidden) {
						de.setType(DataElementType.HIDDEN);
					}
					else {
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_APIKEY_NOMI_STANDARD_OAS3);
						de.setType(DataElementType.CHECKBOX);
						de.setSelected(apiKeyState.useOAS3NamesSelected);
						de.setPostBack(true);
					}
					dati.addElement(de);
					
					// ** sezione apiKey **
					if(!apiKeyState.useOAS3NamesSelected) {
						de = new DataElement();
						de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_API_KEY);
						de.setType(DataElementType.SUBTITLE);
						dati.addElement(de);
					}
					
					// forwardApiKey
					de = new DataElement();
					de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+3);
					de.setValue(apiKeyState.forwardApiKey);
					if(allHidden) {
						de.setType(DataElementType.HIDDEN);
					}
					else {
						if(apiKeyState.useOAS3NamesSelected) {
							de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_APIKEY_FORWARD_PREFIX+
									ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_API_KEY);
						}
						else {
							de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_APIKEY_FORWARD);
						}
						de.setType(DataElementType.CHECKBOX);
						de.setSelected(apiKeyState.forwardApiKeySelected);
					}
					dati.addElement(de);
					
					// queryParameterApiKey
					de = new DataElement();
					de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+4);
					de.setValue(apiKeyState.queryParameterApiKey);
					if(allHidden || apiKeyState.useOAS3NamesSelected || !apiKeyState.queryParameterEnabled) {
						de.setType(DataElementType.HIDDEN);
					}
					else {
						de.setLabel(ModalitaIdentificazione.FORM_BASED.getLabel());
						de.setType(DataElementType.TEXT_EDIT);
						de.setValue(apiKeyState.queryParameterApiKey);
						de.setRequired(true);
					}
					dati.addElement(de);
					
					// headerApiKey
					de = new DataElement();
					de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+5);
					de.setValue(apiKeyState.headerApiKey);
					if(allHidden || apiKeyState.useOAS3NamesSelected || !apiKeyState.headerEnabled) {
						de.setType(DataElementType.HIDDEN);
					}
					else {
						de.setLabel(ModalitaIdentificazione.HEADER_BASED.getLabel());
						de.setType(DataElementType.TEXT_EDIT);
						de.setValue(apiKeyState.headerApiKey);
						de.setRequired(true);
					}
					dati.addElement(de);
					
					// cookieApiKey
					de = new DataElement();
					de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+6);
					de.setValue(apiKeyState.cookieApiKey);
					if(allHidden || apiKeyState.useOAS3NamesSelected || !apiKeyState.cookieEnabled) {
						de.setType(DataElementType.HIDDEN);
					}
					else {
						de.setLabel(ModalitaIdentificazione.COOKIE_BASED.getLabel());
						de.setType(DataElementType.TEXT_EDIT);
						de.setValue(apiKeyState.cookieApiKey);
						de.setRequired(true);
					}
					dati.addElement(de);
					
					
					
					// ** sezione appId **
					if(!apiKeyState.useOAS3NamesSelected && apiKeyState.appIdSelected) {
						de = new DataElement();
						de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID);
						de.setType(DataElementType.SUBTITLE);
						dati.addElement(de);
					}
					
					// forwardAppId
					de = new DataElement();
					de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+7);
					de.setValue(apiKeyState.forwardAppId);
					if(allHidden || !apiKeyState.appIdSelected) {
						de.setType(DataElementType.HIDDEN);
					}
					else {
						if(apiKeyState.useOAS3NamesSelected) {
							de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_APIKEY_FORWARD_PREFIX+
									ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID);
						}
						else {
							de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_APIKEY_FORWARD);
						}
						de.setType(DataElementType.CHECKBOX);
						de.setSelected(apiKeyState.forwardAppIdSelected);
					}
					dati.addElement(de);
					
					// queryParameterAppId
					de = new DataElement();
					de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+8);
					de.setValue(apiKeyState.queryParameterAppId);
					if(allHidden || apiKeyState.useOAS3NamesSelected || !apiKeyState.queryParameterEnabled || !apiKeyState.appIdSelected) {
						de.setType(DataElementType.HIDDEN);
					}
					else {
						de.setLabel(ModalitaIdentificazione.FORM_BASED.getLabel());
						de.setType(DataElementType.TEXT_EDIT);
						de.setValue(apiKeyState.queryParameterAppId);
						de.setRequired(true);
					}
					dati.addElement(de);
					
					// headerAppId
					de = new DataElement();
					de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+9);
					de.setValue(apiKeyState.headerAppId);
					if(allHidden || apiKeyState.useOAS3NamesSelected || !apiKeyState.headerEnabled || !apiKeyState.appIdSelected) {
						de.setType(DataElementType.HIDDEN);
					}
					else {
						de.setLabel(ModalitaIdentificazione.HEADER_BASED.getLabel());
						de.setType(DataElementType.TEXT_EDIT);
						de.setValue(apiKeyState.headerAppId);
						de.setRequired(true);
					}
					dati.addElement(de);
					
					// cookieAppId
					de = new DataElement();
					de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST+10);
					de.setValue(apiKeyState.cookieAppId);
					if(allHidden || apiKeyState.useOAS3NamesSelected || !apiKeyState.cookieEnabled || !apiKeyState.appIdSelected) {
						de.setType(DataElementType.HIDDEN);
					}
					else {
						de.setLabel(ModalitaIdentificazione.COOKIE_BASED.getLabel());
						de.setType(DataElementType.TEXT_EDIT);
						de.setValue(apiKeyState.cookieAppId);
						de.setRequired(true);
					}
					dati.addElement(de);
				}
				
				if(addOpzionale) {
					addOpzionale(dati, allHidden, forceDisableOptional, autenticazioneOpzionale, autenticazione);
				}
				
			}
			
			if(addTitle) {
				this.impostaAperturaTitle(dati, CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TITLE);
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
	
	private void addOpzionale(Vector<DataElement> dati, boolean allHidden, boolean forceDisableOptional, String autenticazioneOpzionale, String autenticazione) {
		DataElement de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_OPZIONALE);
		de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_OPZIONALE);
		if(forceDisableOptional) {
			de.setType(DataElementType.HIDDEN);
			de.setSelected(ServletUtils.isCheckBoxEnabled(autenticazioneOpzionale));
		}
		else if(!allHidden && TipoAutenticazione.DISABILITATO.getValue().equals(autenticazione)==false){
			de.setType(DataElementType.CHECKBOX);
			de.setSelected(ServletUtils.isCheckBoxEnabled(autenticazioneOpzionale));
		}
		else{
			de.setType(DataElementType.HIDDEN);
			de.setValue("");
		}
		dati.addElement(de);
	}
	
	public void controlloAccessiGestioneToken(Vector<DataElement> dati, TipoOperazione tipoOperazione, String gestioneToken, String[] gestioneTokenPolicyLabels, String[] gestioneTokenPolicyValues,
			String gestioneTokenPolicy, String gestioneTokenOpzionale, String gestioneTokenValidazioneInput, String gestioneTokenIntrospection, String gestioneTokenUserInfo, String gestioneTokenForward,Object oggetto, String protocolloParam ,boolean isPortaDelegata) throws Exception {
		
		boolean mostraSezione = !tipoOperazione.equals(TipoOperazione.ADD) || 
				(isPortaDelegata ? this.core.isEnabledToken_generazioneAutomaticaPorteDelegate() : this.core.isEnabledToken_generazioneAutomaticaPorteApplicative());
		
		if(mostraSezione) {
			
			DataElement de = new DataElement();
			de.setType(DataElementType.TITLE); //SUBTITLE);
			de.setName(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_TITLE);
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_GESTIONE_TOKEN);
			de.setStatoAperturaSezioni(STATO_APERTURA_SEZIONI.CHIUSO);
			dati.addElement(de);
			
			String [] valoriAbilitazione = {StatoFunzionalita.DISABILITATO.getValue(), StatoFunzionalita.ABILITATO.getValue()};
			
			// stato abilitazione
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN);
			de.setType(DataElementType.SELECT);
			de.setName(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN);
			de.setValues(valoriAbilitazione);
			de.setValoreDefaultSelect(StatoFunzionalita.DISABILITATO.getValue());
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
					
					GenericProperties policySelezionata = this.confCore.getGenericProperties(gestioneTokenPolicy, CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_GESTIONE_POLICY_TOKEN,true);
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
			
			this.impostaAperturaTitle(dati, CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_TITLE);
			
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
	
	public void controlloAccessiAutorizzazione(Vector<DataElement> dati, TipoOperazione tipoOperazione, String servletChiamante, Object oggetto, String protocolloParam,
			String autenticazione, String autenticazioneCustom, 
			String autorizzazione, String autorizzazioneCustom, 
			String autorizzazioneAutenticati, String urlAutorizzazioneAutenticati, int numAutenticati, List<String> autenticati, String autenticato,
			String autorizzazioneRuoli,  String urlAutorizzazioneRuoli, int numRuoli, String ruolo, String autorizzazioneRuoliTipologia, String autorizzazioneRuoliMatch,
			boolean confPers, boolean isSupportatoAutenticazione, boolean contaListe, boolean isPortaDelegata,
			boolean addTitoloSezione,String autorizzazioneScope,  String urlAutorizzazioneScope, int numScope, String scope, String autorizzazioneScopeMatch,
			String gestioneToken, String gestioneTokenPolicy, String autorizzazione_token, String autorizzazione_tokenOptions,BinaryParameter allegatoXacmlPolicy,
			String urlAutorizzazioneErogazioneApplicativiAutenticati, int numErogazioneApplicativiAutenticati,
			String urlAutorizzazioneCustomPropertiesList, int numAutorizzazioneCustomPropertiesList,
			String identificazioneAttributiStato, String[] attributeAuthorityLabels, String[] attributeAuthorityValues, String [] attributeAuthoritySelezionate, String attributeAuthorityAttributi) throws Exception{
		this.controlloAccessiAutorizzazione(dati, tipoOperazione, servletChiamante, oggetto, protocolloParam,
				autenticazione, autenticazioneCustom,
				autorizzazione, autorizzazioneCustom, 
				autorizzazioneAutenticati, urlAutorizzazioneAutenticati, numAutenticati, autenticati, null, autenticato, 
				autorizzazioneRuoli, urlAutorizzazioneRuoli, numRuoli, ruolo, autorizzazioneRuoliTipologia, autorizzazioneRuoliMatch, 
				confPers, isSupportatoAutenticazione, contaListe, isPortaDelegata, addTitoloSezione,autorizzazioneScope,urlAutorizzazioneScope,numScope,scope,autorizzazioneScopeMatch,
				gestioneToken, gestioneTokenPolicy, autorizzazione_token, autorizzazione_tokenOptions,allegatoXacmlPolicy,
				urlAutorizzazioneErogazioneApplicativiAutenticati, numErogazioneApplicativiAutenticati,
				urlAutorizzazioneCustomPropertiesList, numAutorizzazioneCustomPropertiesList,
				identificazioneAttributiStato, attributeAuthorityLabels, attributeAuthorityValues, attributeAuthoritySelezionate, attributeAuthorityAttributi);
		
	}
	
	public void controlloAccessiAutorizzazione(Vector<DataElement> dati, TipoOperazione tipoOperazione, String servletChiamante, Object oggetto, String protocolloParam,
			String autenticazione, String autenticazioneCustom, 
			String autorizzazione, String autorizzazioneCustom, 
			String autorizzazioneAutenticati, String urlAutorizzazioneAutenticati, int numAutenticati, List<String> autenticati, List<String> autenticatiLabel, String autenticato,
			String autorizzazioneRuoli,  String urlAutorizzazioneRuoli, int numRuoli, String ruolo, String autorizzazioneRuoliTipologia, String autorizzazioneRuoliMatch,
			boolean confPers, boolean isSupportatoAutenticazione, boolean contaListe, boolean isPortaDelegata, boolean addTitoloSezione,
			String autorizzazioneScope,  String urlAutorizzazioneScope, int numScope, String scope, String autorizzazioneScopeMatch,
			String gestioneToken, String gestioneTokenPolicy, String autorizzazione_token, String autorizzazione_tokenOptions, BinaryParameter allegatoXacmlPolicy,
			String urlAutorizzazioneErogazioneApplicativiAutenticati, int numErogazioneApplicativiAutenticati,
			String urlAutorizzazioneCustomPropertiesList, int numAutorizzazioneCustomPropertiesList,
			String identificazioneAttributiStato, String[] attributeAuthorityLabels, String[] attributeAuthorityValues, String [] attributeAuthoritySelezionate, String attributeAuthorityAttributi) throws Exception{
		
		boolean allHidden = false;
		if(!this.isModalitaCompleta() && TipoOperazione.ADD.equals(tipoOperazione)) {
			allHidden = true;
		}
		
		String protocollo = protocolloParam;
		if((protocollo==null || "".equals(protocollo)) && oggetto!=null){
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
		
		String oldAutenticazione = null;
		if(oggetto!=null) {
			if(isPortaDelegata){
				PortaDelegata pd = (PortaDelegata) oggetto;
				if(pd!=null) {
					oldAutenticazione = pd.getAutenticazione();
				}
			}
			else {
				PortaApplicativa pa = (PortaApplicativa) oggetto;
				if(pa!=null) {
					oldAutenticazione = pa.getAutenticazione();
				}
			}
		}
				
		
		boolean mostraSezione = !tipoOperazione.equals(TipoOperazione.ADD) || 
				(isPortaDelegata ? this.core.isEnabledAutorizzazione_generazioneAutomaticaPorteDelegate() : 
					this.core.isEnabledAutorizzazione_generazioneAutomaticaPorteApplicative(protocollo==null ? true : this.soggettiCore.isSupportatoAutenticazioneSoggetti(protocollo)));
		
		boolean tokenAbilitato = StatoFunzionalita.ABILITATO.getValue().equalsIgnoreCase(gestioneToken) &&
				gestioneTokenPolicy != null && !gestioneTokenPolicy.equals(CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO);
		
		boolean profiloModi = this.isProfiloModIPA(protocollo);
		
		if(mostraSezione) {
			
			if(!tipoOperazione.equals(TipoOperazione.ADD) && attributeAuthorityValues!=null && attributeAuthorityValues.length>0){
				DataElement de = new DataElement();
				de.setType(DataElementType.TITLE); //SUBTITLE);
				de.setName(CostantiControlStation.PARAMETRO_PORTE_ATTRIBUTI_STATO_TITLE);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_ATTRIBUTI_TITLE);
				de.setStatoAperturaSezioni(STATO_APERTURA_SEZIONI.CHIUSO);
				dati.addElement(de);
			
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_ATTRIBUTI_STATO);
				de.setName(CostantiControlStation.PARAMETRO_PORTE_ATTRIBUTI_STATO);
				de.setValue(identificazioneAttributiStato);
				if(allHidden) {
					de.setType(DataElementType.HIDDEN);
				}
				else {
					de.setType(DataElementType.SELECT);
					String [] valoriAbilitazione = {StatoFunzionalita.DISABILITATO.getValue(), StatoFunzionalita.ABILITATO.getValue()};
					de.setValues(valoriAbilitazione);
					//de.setLabels(valoriAbilitazione);
					de.setPostBack(true);
					de.setSelected(identificazioneAttributiStato);
					de.setValoreDefaultSelect(StatoFunzionalita.DISABILITATO.getValue());
				}
				dati.addElement(de);
				
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_ATTRIBUTI_AUTHORITY);
				de.setName(CostantiControlStation.PARAMETRO_PORTE_ATTRIBUTI_AUTHORITY);
				if(allHidden || !StatoFunzionalita.ABILITATO.getValue().equals(identificazioneAttributiStato)) {
					de.setType(DataElementType.HIDDEN);
					de.setSelezionati(attributeAuthoritySelezionate);
				}
				else {
					de.setRequired(true);
					de.setType(DataElementType.MULTI_SELECT);
					de.setValues(attributeAuthorityValues);
					de.setLabels(attributeAuthorityLabels);
					de.setPostBack(true); // cambia la descrizione in autorizzazione per claims
					de.setSelezionati(attributeAuthoritySelezionate);
				}
				dati.addElement(de);
				
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_ATTRIBUTI_AUTHORITY_ATTRIBUTI);
				de.setName(CostantiControlStation.PARAMETRO_PORTE_ATTRIBUTI_AUTHORITY_ATTRIBUTI);
				int sizeAA = 0;
				if(attributeAuthoritySelezionate!=null) {
					sizeAA = attributeAuthoritySelezionate.length;
				}
				de.setValue(attributeAuthorityAttributi);
				if(allHidden || !StatoFunzionalita.ABILITATO.getValue().equals(identificazioneAttributiStato)) {
					de.setType(DataElementType.HIDDEN);
				}
				else if(sizeAA>1) {
					de.setType(DataElementType.TEXT_AREA);
					de.setNote(CostantiControlStation.LABEL_PARAMETRO_PORTE_ATTRIBUTI_AUTHORITY_ATTRIBUTI_NOTE_MULTIPLE_AA);
					de.setRows(sizeAA<=10 ? sizeAA : 10);
				}
				else {
					de.setType(DataElementType.TEXT_EDIT);
					de.setNote(CostantiControlStation.LABEL_PARAMETRO_PORTE_ATTRIBUTI_AUTHORITY_ATTRIBUTI_NOTE_SINGLE_AA);
				}
				dati.addElement(de);
				
				this.impostaAperturaTitle(dati, CostantiControlStation.PARAMETRO_PORTE_ATTRIBUTI_STATO_TITLE);
			}
			
			
			boolean addTitle = false;
			if(!allHidden) {
				addTitle = true;
				DataElement de = new DataElement();
				de.setType(DataElementType.TITLE); //SUBTITLE);
				de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TITLE);
				if(profiloModi && !isPortaDelegata) {
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CANALE);
				}
				else {
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE);
				}
				de.setStatoAperturaSezioni(STATO_APERTURA_SEZIONI.APERTO);
				dati.addElement(de);
			}
			
			List<String> auturizzazioneValues = AutorizzazioneUtilities.getStati();
			int totEl = auturizzazioneValues.size();
			if (confPers )
				totEl++;
			String[] tipoAutorizzazione = new String[totEl];
			String[] tipoAutorizzazione_label = new String[totEl];
			for (int i = 0; i < auturizzazioneValues.size(); i++) {
				tipoAutorizzazione[i]=auturizzazioneValues.get(i);
				tipoAutorizzazione_label[i]=auturizzazioneValues.get(i);
			}
			if (confPers ){
				tipoAutorizzazione[totEl-1] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM;
				tipoAutorizzazione_label[totEl-1] = CostantiControlStation.DEFAULT_LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM;
			}
			DataElement de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE);
			de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE);
			if(allHidden) {
				de.setType(DataElementType.HIDDEN);
				de.setValue(autorizzazione);
			}
			else {
				de.setType(DataElementType.SELECT);
				de.setValues(tipoAutorizzazione);
				de.setLabels(tipoAutorizzazione_label);
				de.setPostBack(true);
				de.setSelected(autorizzazione);
				de.setValoreDefaultSelect(StatoFunzionalita.DISABILITATO.getValue());
			}
			dati.addElement(de);
			
			String tipoAutorizzazioneCustom = isPortaDelegata ? Filtri.FILTRO_RUOLO_VALORE_FRUIZIONE : Filtri.FILTRO_RUOLO_VALORE_EROGAZIONE;
			boolean autorizzazioneCustomHidden = (allHidden || (autorizzazione == null) || (!autorizzazione.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM)));
			this.addCustomField(TipoPlugin.AUTORIZZAZIONE,
					tipoAutorizzazioneCustom,
					null,
					CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE,
					CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM, 
					CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM, 
					autorizzazioneCustom, autorizzazioneCustomHidden, dati,
					false); 
			
//			de = new DataElement();
//			de.setLabel("");
//			if(allHidden) {
//				de.setType(DataElementType.HIDDEN);
//			}
//			else if (autorizzazione == null ||
//					!autorizzazione.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM)) {
//				de.setType(DataElementType.HIDDEN);
//			} else {
//				de.setType(DataElementType.TEXT_EDIT);
//				de.setRequired(true); 
//			}
//			de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM);
//			de.setValue(autorizzazioneCustom);
//			dati.addElement(de);
			
			boolean old_autorizzazione_autenticazione = false;
			boolean old_autorizzazione_ruoli = false;
			boolean old_autorizzazione_scope = false;
			boolean old_xacmlPolicy = false;
			boolean old_autorizzazione_custom = false;
			String old_autorizzazione = null;
			Long idPorta = null;
			IDServizio idServizio = null;
			
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
					idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(pd.getServizio().getTipo(), pd.getServizio().getNome(), 
							pd.getSoggettoErogatore().getTipo(), pd.getSoggettoErogatore().getNome(), 
							pd.getServizio().getVersione());
					old_autorizzazione_custom = pd.getAutorizzazione() != null && !TipoAutorizzazione.getAllValues().contains(pd.getAutorizzazione());
				}
				else {
					PortaApplicativa pa = (PortaApplicativa) oggetto;
					old_autorizzazione = AutorizzazioneUtilities.convertToStato(pa.getAutorizzazione());
					old_autorizzazione_autenticazione = TipoAutorizzazione.isAuthenticationRequired(pa.getAutorizzazione());
					old_autorizzazione_ruoli = TipoAutorizzazione.isRolesRequired(pa.getAutorizzazione());
					old_autorizzazione_scope = pa.getScope() != null && pa.getScope().getStato().equals(StatoFunzionalita.ABILITATO);
					old_xacmlPolicy = StringUtils.isNotEmpty(pa.getXacmlPolicy());
					idPorta = pa.getId();
					idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(pa.getServizio().getTipo(), pa.getServizio().getNome(), 
							pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario(), 
							pa.getServizio().getVersione());
					old_autorizzazione_custom = pa.getAutorizzazione() != null && !TipoAutorizzazione.getAllValues().contains(pa.getAutorizzazione());
				}
			}
			
			// se ho salvato il tipo custom faccio vedere il link alle proprieta'
			if(autorizzazione != null && autorizzazione.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM)) {
				if(old_autorizzazione_custom) {
					de = new DataElement();
					de.setType(DataElementType.LINK);
					de.setUrl(urlAutorizzazioneCustomPropertiesList);
					String labelCustomProperties = CostantiControlStation.LABEL_PARAMETRO_AUTORIZZAZIONE_CUSTOM_PROPERTIES; 
					if (contaListe) {
						ServletUtils.setDataElementCustomLabel(de,labelCustomProperties,Long.valueOf(numAutorizzazioneCustomPropertiesList));
					} else {
						ServletUtils.setDataElementCustomLabel(de,labelCustomProperties);
					}
					dati.addElement(de);
				}
			}
			
			
			if(AutorizzazioneUtilities.STATO_DISABILITATO.equals(autorizzazione)==false){
			
				boolean autorizzazione_autenticazione =  false;
				boolean isSupportatoAutorizzazioneRichiedentiSenzaAutenticazione = this.soggettiCore.isSupportatoAutorizzazioneRichiedenteSenzaAutenticazioneErogazione(protocollo);
				
							
				if(AutorizzazioneUtilities.STATO_ABILITATO.equals(autorizzazione)){
				
					if(!allHidden) {
						if( !isSupportatoAutenticazione 
								|| 
								(autenticazione!=null && !TipoAutenticazione.DISABILITATO.equals(autenticazione)) 
								||
								isSupportatoAutorizzazioneRichiedentiSenzaAutenticazione
								){   
							de = new DataElement();
							de.setType(DataElementType.SUBTITLE);
							if(isPortaDelegata){
								de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE_SERVIZI_APPLICATIVI);
							}
							else{
								String labelSoggetti = (isSupportatoAutenticazione && 
										(
												(autenticazione!=null && !TipoAutenticazione.DISABILITATO.equals(autenticazione)) 
												||
												isSupportatoAutorizzazioneRichiedentiSenzaAutenticazione
										) 
										) ? CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE_SOGGETTI : CostantiControlStation.LABEL_PARAMETRO_SOGGETTI;
								de.setLabel(labelSoggetti);
							}
							dati.addElement(de);
						}
					}
					
					autorizzazione_autenticazione = ServletUtils.isCheckBoxEnabled(autorizzazioneAutenticati);
					
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_ABILITATO);
					de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE);
					if( !isSupportatoAutenticazione 
							|| 
							(autenticazione!=null && !TipoAutenticazione.DISABILITATO.equals(autenticazione)) 
							||
							isSupportatoAutorizzazioneRichiedentiSenzaAutenticazione
							){   
						if(allHidden) {
							de.setType(DataElementType.HIDDEN);
							de.setValue(autorizzazione_autenticazione+"");
						}
						else {
							de.setType(DataElementType.CHECKBOX);
							de.setSelected(autorizzazione_autenticazione);
							de.setPostBack(true);
						}
					}
					else{
						de.setType(DataElementType.HIDDEN);
						de.setValue(Costanti.CHECK_BOX_DISABLED);
					}
					dati.addElement(de);
					
				}
				
				if(TipoOperazione.CHANGE.equals(tipoOperazione)){
					
					if( !isSupportatoAutenticazione 
							|| 
							(autenticazione!=null && !TipoAutenticazione.DISABILITATO.equals(autenticazione)) 
							||
							isSupportatoAutorizzazioneRichiedentiSenzaAutenticazione
							){   
					
						if(urlAutorizzazioneAutenticati!=null && autorizzazione_autenticazione && 
								(old_autorizzazione_autenticazione || CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM.equals(old_autorizzazione)) &&
								(oldAutenticazione!=null && (
											oldAutenticazione.equals(autenticazione) 
											||  
											(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM.equals(autenticazione) && oldAutenticazione.equals(autenticazioneCustom) )
										) 
								)
								){
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
						
						if(!isPortaDelegata && this.saCore.isSupportatoAutenticazioneApplicativiErogazione(protocollo) 
								&& isSupportatoAutenticazione // il link degli applicativi sulla pa deve essere visualizzato SOLO se è abilitata l'autenticazione
								&& !profiloModi // e non siamo nel profilo ModI
								&&
								(oldAutenticazione!=null && (
										oldAutenticazione.equals(autenticazione) 
										||  
										(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM.equals(autenticazione) && oldAutenticazione.equals(autenticazioneCustom) )
									) 
								)
								){
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
					if(!allHidden) {
						if(!isSupportatoAutenticazione ||  (autenticazione!=null && !TipoAutenticazione.DISABILITATO.equals(autenticazione))) {
							if(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD.equals(servletChiamante) && autorizzazione_autenticazione && isPortaDelegata){
								String [] saArray = null;
								if(autenticati!=null && autenticati.size()>0){
									saArray = autenticati.toArray(new String[1]);
								}
								this.addPorteServizioApplicativoToDati(tipoOperazione, dati, autenticato, saArray, 0, false, false);
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
								this.addPorteSoggettoToDati(tipoOperazione, dati, soggettiLabelList, soggettiList, autenticato, 0, false, false);
							}
						}
					}
				}
			}
			
			if(AutorizzazioneUtilities.STATO_DISABILITATO.equals(autorizzazione)==false){
					
				boolean autorizzazione_ruoli = false;
				
				if(AutorizzazioneUtilities.STATO_ABILITATO.equals(autorizzazione)){
					
					if(!allHidden) {
						de = new DataElement();
						de.setType(DataElementType.SUBTITLE);
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI);
						dati.addElement(de);
					}
				
					autorizzazione_ruoli = ServletUtils.isCheckBoxEnabled(autorizzazioneRuoli);
					
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_ABILITATO);
					de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI);
					if(allHidden) {
						de.setType(DataElementType.HIDDEN);
						de.setValue(autorizzazione_ruoli+"");
					}
					else {
						de.setType(DataElementType.CHECKBOX);
						de.setSelected(autorizzazione_ruoli);
						de.setPostBack(true);
					}
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
					if(allHidden) {
						de.setType(DataElementType.HIDDEN);
					}
					else {
						de.setType(DataElementType.SELECT);
						de.setValues(RuoliCostanti.RUOLI_TIPOLOGIA);
						de.setLabels(RuoliCostanti.RUOLI_TIPOLOGIA_LABEL);
						de.setSelected(autorizzazioneRuoliTipologia);
						if(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD.equals(servletChiamante) ||
								AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD.equals(servletChiamante)){
							de.setPostBack(true);
						}
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
						saveAs.setDisabilitaAjaxStatus();
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
					if(allHidden) {
						de.setType(DataElementType.HIDDEN);
					}
					else {
						de.setType(DataElementType.SELECT);
						de.setValues(tipoRole);
						de.setLabels(labelRole);
						de.setSelected(autorizzazioneRuoliMatch);
					}
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
					if(!allHidden) {
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
				
			}
			if(AutorizzazioneUtilities.STATO_DISABILITATO.equals(autorizzazione)==false){
				boolean autorizzazione_scope = false;
			
				if(AutorizzazioneUtilities.STATO_ABILITATO.equals(autorizzazione) && tokenAbilitato){
					
					if(!allHidden) {
						de = new DataElement();
						de.setType(DataElementType.SUBTITLE);
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_SCOPE);
						dati.addElement(de);
					}
				
					autorizzazione_scope = ServletUtils.isCheckBoxEnabled(autorizzazioneScope);
					
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_ABILITATO);
					de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_SCOPE);
					if(allHidden) {
						de.setType(DataElementType.HIDDEN);
						de.setValue(autorizzazione_scope+"");
					}
					else {
						de.setType(DataElementType.CHECKBOX);
						de.setSelected(autorizzazione_scope);
						de.setPostBack(true);
					}
					dati.addElement(de);
				
					if(autorizzazione_scope){
						String[] tipoScope = { ScopeTipoMatch.ALL.getValue(),	ScopeTipoMatch.ANY.getValue() };
						String[] labelScope = { CostantiControlStation.LABEL_PARAMETRO_SCOPE_MATCH_ALL, CostantiControlStation.LABEL_PARAMETRO_SCOPE_MATCH_ANY };
						de = new DataElement();
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_SCOPE_MATCH);
						de.setName(CostantiControlStation.PARAMETRO_SCOPE_MATCH);
						if(allHidden) {
							de.setType(DataElementType.HIDDEN);
							de.setValue(autorizzazioneScopeMatch+"");
						}
						else {
							de.setType(DataElementType.SELECT);
							de.setValues(tipoScope);
							de.setLabels(labelScope);
							de.setSelected(autorizzazioneScopeMatch);
						}
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
						if(!allHidden) {
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
					
				}
				
				if(tokenAbilitato && !AutorizzazioneUtilities.STATO_XACML_POLICY.equals(autorizzazione)) {
					
					if(!allHidden) {
						de = new DataElement();
						de.setType(DataElementType.SUBTITLE);
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_SUBTITLE);
						dati.addElement(de);
					}

					boolean autorizzazioneTokenEnabled = ServletUtils.isCheckBoxEnabled(autorizzazione_token);
					
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_ABILITATO);
					de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN);
					if(allHidden) {
						de.setType(DataElementType.HIDDEN);
						de.setValue(autorizzazioneTokenEnabled+"");
					}
					else {
						de.setType(DataElementType.CHECKBOX);
						de.setSelected(autorizzazioneTokenEnabled);
						de.setPostBack(true);
					}
					dati.addElement(de);
					
					if(autorizzazioneTokenEnabled) {
						de = new DataElement();
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN);
						de.setNote(DynamicHelperCostanti.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_NOTE);
						de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_OPTIONS);
						de.setValue(autorizzazione_tokenOptions);
						if(allHidden) {
							de.setType(DataElementType.HIDDEN);
						}
						else {
							de.setType(DataElementType.TEXT_AREA);
							de.setRows(6);
							de.setCols(55);
						}
						
						org.openspcoop2.core.registry.constants.ServiceBinding serviceBinding = org.openspcoop2.core.registry.constants.ServiceBinding.REST;
						if(idServizio!=null) {
							AccordoServizioParteSpecifica asps = this.apsCore.getServizio(idServizio,false);
							AccordoServizioParteComuneSintetico aspc = this.apcCore.getAccordoServizioSintetico(this.idAccordoFactory.getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
							serviceBinding = aspc.getServiceBinding();
						}
						int sizeAA = 0;
						if(attributeAuthoritySelezionate!=null) {
							sizeAA = attributeAuthoritySelezionate.length;
						}
						
						DataElementInfo dInfoTokenClaims = new DataElementInfo(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN);
						if(sizeAA>1) {
							dInfoTokenClaims.setHeaderBody(DynamicHelperCostanti.LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_MULTI_ATTRIBUTE_AUTHORITY);
						}
						else if(sizeAA==1) {
							dInfoTokenClaims.setHeaderBody(DynamicHelperCostanti.LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SINGLE_ATTRIBUTE_AUTHORITY);
						}
						else {
							dInfoTokenClaims.setHeaderBody(DynamicHelperCostanti.LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_NO_ATTRIBUTE_AUTHORITY);
						}
						if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(serviceBinding)) {
							dInfoTokenClaims.setListBody(DynamicHelperCostanti.LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI);
						}
						else {
							dInfoTokenClaims.setListBody(DynamicHelperCostanti.LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI);
						}
						de.setInfo(dInfoTokenClaims);
						
						dati.addElement(de);
					}
				}
			}
			
			if(addTitle) {
				this.impostaAperturaTitle(dati, CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TITLE);
			}
			
		} else {
			DataElement de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE);
			de.setType(DataElementType.HIDDEN);
			de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE);
			de.setValue(AutorizzazioneUtilities.STATO_DISABILITATO);
			dati.addElement(de);
		}
		
		
		
		if(!isPortaDelegata && profiloModi && !allHidden && PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI.equals(servletChiamante)) {
			
			DataElement de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_MODIPA_STATO_TITLE);
			//de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE+" "+this.getProfiloModIPASectionTitle());
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_MESSAGGIO);
			de.setStatoAperturaSezioni(STATO_APERTURA_SEZIONI.CHIUSO);
			dati.addElement(de);
			
//			de = new DataElement();
//			de.setType(DataElementType.SUBTITLE);
//			de.setLabel(this.getProfiloModIPASectionSicurezzaMessaggioSubTitle());
//			dati.addElement(de);
			
			String stato = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_MODIPA_STATO);
			if(stato==null || "".equals(stato)) {
				stato = (numErogazioneApplicativiAutenticati>0) ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
			}
			
			String [] valoriAbilitazione = {StatoFunzionalita.DISABILITATO.getValue(), StatoFunzionalita.ABILITATO.getValue()};
			
			// stato abilitazione
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_MODIPA_STATO);
			de.setType(DataElementType.SELECT);
			de.setName(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_MODIPA_STATO);
			de.setValues(valoriAbilitazione);
			de.setPostBack(true);
			de.setSelected(stato);
			de.setValoreDefaultSelect(StatoFunzionalita.DISABILITATO.getValue());
			dati.addElement(de);
			
			if(StatoFunzionalita.ABILITATO.getValue().equals(stato)) {
			
				if(numErogazioneApplicativiAutenticati<=0) {
					
					String idSoggettoToAdd = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SOGGETTO);
					String idSAToAdd = this.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_AUTORIZZATO);
					
					// Calcolo liste
					PortaApplicativa pa = (PortaApplicativa) oggetto;
					PorteApplicativeServizioApplicativoAutorizzatoUtilities utilities = new PorteApplicativeServizioApplicativoAutorizzatoUtilities();
					boolean escludiSAServer = this.porteApplicativeCore.isApplicativiServerEnabled(this);					
					utilities.buildList(pa, profiloModi, protocollo, true,
							idSoggettoToAdd,
							this.porteApplicativeCore, this, escludiSAServer);
					
					String[] soggettiList = utilities.soggettiList;
					String[] soggettiListLabel = utilities.soggettiListLabel;
					Map<String,List<IDServizioApplicativoDB>> listServiziApplicativi = utilities.listServiziApplicativi;
					idSoggettoToAdd = utilities.idSoggettoToAdd;
					int saSize = utilities.saSize;
					
					dati = this.addPorteServizioApplicativoAutorizzatiToDati(TipoOperazione.ADD, dati, soggettiListLabel, soggettiList, idSoggettoToAdd, saSize, 
							listServiziApplicativi, idSAToAdd, true, false, profiloModi);
					
				}
				else {
			
					de = new DataElement();
					de.setType(DataElementType.LINK);
					de.setUrl(urlAutorizzazioneErogazioneApplicativiAutenticati,
							new Parameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_MODIPA, "true"));
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
			
			this.impostaAperturaTitle(dati, CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_MODIPA_STATO_TITLE);
		}
		
	}
	
	public void controlloAccessiAutorizzazioneContenuti(Vector<DataElement> dati, TipoOperazione tipoOperazione, boolean isPortaDelegata,
			String autorizzazioneContenutiStato, String autorizzazioneContenuti, String autorizzazioneContenutiProperties, ServiceBinding serviceBinding,
			boolean old_autorizzazione_contenuti_custom, String urlAutorizzazioneContenutiCustomPropertiesList, int numAutorizzazioneContenutiCustomPropertiesList,
			boolean confPers) throws Exception{
		
		DataElement de = new DataElement();
		de.setType(DataElementType.TITLE);
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI);
		de.setName(CostantiControlStation.PARAMETRO_AUTORIZZAZIONE_CONTENUTI_STATO_TITLE);
		de.setStatoAperturaSezioni(STATO_APERTURA_SEZIONI.CHIUSO);
		dati.addElement(de);
		
		List<String> authContenutiLabels = new ArrayList<>();
		List<String> authContenutiValues = new ArrayList<>();
		
		authContenutiLabels.addAll(Arrays.asList(CostantiControlStation.PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_LABELS));
		authContenutiValues.addAll(Arrays.asList(CostantiControlStation.PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_VALUES));
		
		if(confPers || autorizzazioneContenutiStato.equals(CostantiControlStation.VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_CUSTOM)) {
			authContenutiLabels.add(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_CUSTOM);
			authContenutiValues.add(CostantiControlStation.VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_CUSTOM);
		}
		
		de = new DataElement();
		de.setType(DataElementType.SELECT);
		de.setSelected(autorizzazioneContenutiStato);
		de.setValues(authContenutiValues);
		de.setLabels(authContenutiLabels);
		de.setName(CostantiControlStation.PARAMETRO_AUTORIZZAZIONE_CONTENUTI_STATO);
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_AUTORIZZAZIONE_CONTENUTI_STATO);
		de.setPostBack(true);
		de.setValoreDefaultSelect(CostantiControlStation.VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_DISABILITATO); 
		dati.addElement(de);
		
		if(!autorizzazioneContenutiStato.equals(CostantiControlStation.VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_DISABILITATO)) {
			// abilitato 
			if(autorizzazioneContenutiStato.equals(CostantiControlStation.VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_ABILITATO)) {
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_AUTORIZZAZIONE_CONTENUTI);
				de.setType(DataElementType.HIDDEN);
				de.setName(CostantiControlStation.PARAMETRO_AUTORIZZAZIONE_CONTENUTI);
				de.setValue(autorizzazioneContenuti);
				dati.addElement(de);
				
				
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_AUTORIZZAZIONE_CONTENUTI);
				de.setType(DataElementType.TEXT_AREA);
				de.setName(CostantiControlStation.PARAMETRO_AUTORIZZAZIONE_CONTENUTI_PROPERTIES);
				de.setValue(autorizzazioneContenutiProperties);
				de.setNote(DynamicHelperCostanti.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_CONTENUTI_NOTE);
				DataElementInfo info = new DataElementInfo(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI);
				info.setHeaderBody(DynamicHelperCostanti.LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI);
				if(ServiceBinding.REST.equals(serviceBinding)) {
					info.setListBody(DynamicHelperCostanti.LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_REST_VALORI);
				}
				else {
					info.setListBody(DynamicHelperCostanti.LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_SOAP_VALORI);
				}
				de.setValoreDefault("");
				de.setInfo(info );
				dati.addElement(de);
			}
			
			// custom
			if(autorizzazioneContenutiStato.equals(CostantiControlStation.VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_CUSTOM)) {
				
				String tipoAutorizzazioneCustom = isPortaDelegata ? Filtri.FILTRO_RUOLO_VALORE_FRUIZIONE : Filtri.FILTRO_RUOLO_VALORE_EROGAZIONE;
				boolean autorizzazioneCustomHidden = false;
				this.addCustomField(TipoPlugin.AUTORIZZAZIONE_CONTENUTI,
						tipoAutorizzazioneCustom,
						null,
						CostantiControlStation.PARAMETRO_AUTORIZZAZIONE_CONTENUTI_STATO,
						CostantiControlStation.PARAMETRO_AUTORIZZAZIONE_CONTENUTI, 
						CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM, 
						autorizzazioneContenuti, autorizzazioneCustomHidden, dati,
						false); 
				
//				de = new DataElement();
//				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_AUTORIZZAZIONE_CONTENUTI);
//				de.setType(DataElementType.TEXT_EDIT);
//				de.setName(CostantiControlStation.PARAMETRO_AUTORIZZAZIONE_CONTENUTI);
//				de.setValue(autorizzazioneContenuti);
//				de.setRequired(true); 
//				dati.addElement(de);
				
				// link proprieta
				if(old_autorizzazione_contenuti_custom) {
					Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
					de = new DataElement();
					de.setType(DataElementType.LINK);
					de.setUrl(urlAutorizzazioneContenutiCustomPropertiesList);
					String labelCustomProperties = CostantiControlStation.LABEL_PARAMETRO_AUTORIZZAZIONE_CONTENUTI_CUSTOM_PROPERTIES; 
					if (contaListe) {
						ServletUtils.setDataElementCustomLabel(de,labelCustomProperties,Long.valueOf(numAutorizzazioneContenutiCustomPropertiesList));
					} else {
						ServletUtils.setDataElementCustomLabel(de,labelCustomProperties);
					}
					dati.addElement(de);
				}
			}
		}
		
		this.impostaAperturaTitle(dati, CostantiControlStation.PARAMETRO_AUTORIZZAZIONE_CONTENUTI_STATO_TITLE);
	}
	
	public boolean controlloAccessiCheck(TipoOperazione tipoOperazione, 
			String autenticazione, String autenticazioneOpzionale, TipoAutenticazionePrincipal autenticazionePrincipal, List<String> autenticazioneParametroList,
			String autorizzazione, String autorizzazioneAutenticati, String autorizzazioneRuoli,  
			String autorizzazioneRuoliTipologia, String autorizzazioneRuoliMatch,
			boolean isSupportatoAutenticazione, boolean isPortaDelegata, Object oggetto,
			List<String> ruoli,String gestioneToken, 
			String policy, String validazioneInput, String introspection, String userInfo, String forward,
			String autorizzazione_token, String autorizzazione_tokenOptions,
			String autorizzazioneScope, String autorizzazioneScopeMatch, BinaryParameter allegatoXacmlPolicy,
			String autorizzazioneContenutiStato, String autorizzazioneContenuto, String autorizzazioneContenutiProperties,
			String protocollo,
			String identificazioneAttributiStato, String [] attributeAuthoritySelezionate, String attributeAuthorityAttributi) throws Exception{
		try {
			
			if(TipoAutenticazione.PRINCIPAL.equals(autenticazione) &&  autenticazionePrincipal!=null) {
				switch (autenticazionePrincipal) {
				case CONTAINER:
				case INDIRIZZO_IP:
				case INDIRIZZO_IP_X_FORWARDED_FOR:
					break;
				case HEADER:
					if(autenticazioneParametroList==null || autenticazioneParametroList.isEmpty() || StringUtils.isEmpty(autenticazioneParametroList.get(0))){
						this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,	CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_HEADER));
						return false;
					}
					break;
				case FORM:
					if(autenticazioneParametroList==null || autenticazioneParametroList.isEmpty() || StringUtils.isEmpty(autenticazioneParametroList.get(0))){
						this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,	CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_FORM));
						return false;
					}
					break;
				case URL:
					if(autenticazioneParametroList==null || autenticazioneParametroList.isEmpty() || StringUtils.isEmpty(autenticazioneParametroList.get(0))){
						this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,	CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_ESPRESSIONE));
						return false;
					}
					break;
				case TOKEN:
					if(autenticazioneParametroList==null || autenticazioneParametroList.isEmpty() || StringUtils.isEmpty(autenticazioneParametroList.get(0))){
						this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,	CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_TOKEN_CLAIM));
						return false;
					}
					String tipo = autenticazioneParametroList.get(0);
					if(ParametriAutenticazionePrincipal.TOKEN_CLAIM_CUSTOM.equals(tipo)) {
						if(autenticazioneParametroList.size()<=1 || StringUtils.isEmpty(autenticazioneParametroList.get(1))){
							this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,	
									CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_TOKEN_CLAIM_PERSONALIZZATO_ESTESO));
							return false;
						}
					}
					break;
				}
			}
			
			if(TipoAutenticazione.APIKEY.equals(autenticazione)) {
				if(autenticazioneParametroList==null || autenticazioneParametroList.isEmpty()){
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,	
							CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_APIKEY_POSIZIONE));
					return false;
				}
				
				ApiKeyState apiKeyState = new ApiKeyState(autenticazioneParametroList);
				if(!apiKeyState.queryParameterEnabled && !apiKeyState.headerEnabled && !apiKeyState.cookieEnabled) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,	
							CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_APIKEY_POSIZIONE));
					return false;
				}
				
				if(!apiKeyState.useOAS3NamesSelected) {
					if(apiKeyState.queryParameterEnabled) {
						if(apiKeyState.queryParameterApiKey==null || "".equals(apiKeyState.queryParameterApiKey)) {
							this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,	
									ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_API_KEY+" - "+ModalitaIdentificazione.FORM_BASED.getLabel()));
							return false;
						}
					}
					if(apiKeyState.headerEnabled) {
						if(apiKeyState.headerApiKey==null || "".equals(apiKeyState.headerApiKey)) {
							this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,	
									ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_API_KEY+" - "+ModalitaIdentificazione.HEADER_BASED.getLabel()));
							return false;
						}
					}
					if(apiKeyState.cookieEnabled) {
						if(apiKeyState.cookieApiKey==null || "".equals(apiKeyState.cookieApiKey)) {
							this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,	
									ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_API_KEY+" - "+ModalitaIdentificazione.COOKIE_BASED.getLabel()));
							return false;
						}
					}
					
					if(apiKeyState.appIdSelected) {
						if(apiKeyState.queryParameterEnabled) {
							if(apiKeyState.queryParameterAppId==null || "".equals(apiKeyState.queryParameterAppId)) {
								this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,	
										ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID+" - "+ModalitaIdentificazione.FORM_BASED.getLabel()));
								return false;
							}
							if(apiKeyState.queryParameterAppId.equalsIgnoreCase(apiKeyState.queryParameterApiKey)) {
								this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_CAMPI_DIFFERENTI,	
										"'"+ModalitaIdentificazione.FORM_BASED.getLabel()+"' tra "+ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_API_KEY,
										ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID));
								return false;
							}
						}
						if(apiKeyState.headerEnabled) {
							if(apiKeyState.headerAppId==null || "".equals(apiKeyState.headerAppId)) {
								this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,	
										ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID+" - "+ModalitaIdentificazione.HEADER_BASED.getLabel()));
								return false;
							}
							if(apiKeyState.headerAppId.equalsIgnoreCase(apiKeyState.headerApiKey)) {
								this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_CAMPI_DIFFERENTI,	
										"'"+ModalitaIdentificazione.HEADER_BASED.getLabel()+"' tra "+ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_API_KEY,
										ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID));
								return false;
							}
						}
						if(apiKeyState.cookieEnabled) {
							if(apiKeyState.cookieAppId==null || "".equals(apiKeyState.cookieAppId)) {
								this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,	
										ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID+" - "+ModalitaIdentificazione.COOKIE_BASED.getLabel()));
								return false;
							}
							if(apiKeyState.cookieAppId.equalsIgnoreCase(apiKeyState.cookieApiKey)) {
								this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_CAMPI_DIFFERENTI,	
										"'"+ModalitaIdentificazione.COOKIE_BASED.getLabel()+"' tra "+ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_API_KEY,
										ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID));
								return false;
							}
						}
					}
				}
			}
			
			// tipo autenticazione custom
			String autenticazioneCustom = null;
			if(autenticazione != null && autenticazione.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM)) {
				autenticazioneCustom = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM );
				
				if(StringUtils.isEmpty(autenticazioneCustom) || CostantiControlStation.PARAMETRO_TIPO_PERSONALIZZATO_VALORE_UNDEFINED.equals(autenticazioneCustom)){
					if(this.confCore.isConfigurazionePluginsEnabled()) {
						this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_AUTENTICAZIONE_CUSTOM_NON_INDICATA);
					}
					else {
						this.pd.setMessage(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM+" obbligatorio per il tipo di autenticazione selezionato");
					}
					return false;
				}
				
				if(this.checkLength255(autenticazioneCustom, CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE_CUSTOM)==false) {
					return false;
				}
			}
			
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
					StringBuilder sb = new StringBuilder();
					sb.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN_VALIDAZIONE_INPUT).append(", ");
					sb.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN_INTROSPECTION).append(", ");
					sb.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN_USERINFO).append(" o ");
					sb.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN_TOKEN_FORWARD);
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_POLICY_TOKEN_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_UNA_MODALITA, sb.toString()));
					return false;
				}
				
			}
			
			// check attribute authority
			if(AutorizzazioneUtilities.STATO_ABILITATO.equals(identificazioneAttributiStato)){
				if(attributeAuthoritySelezionate==null || attributeAuthoritySelezionate.length<=0) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,	
							CostantiControlStation.LABEL_PARAMETRO_PORTE_ATTRIBUTI_AUTHORITY));
					return false;
				}
				if (!checkAttributes(attributeAuthoritySelezionate, attributeAuthorityAttributi)) {
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
						if(isPortaDelegata || !this.soggettiCore.isSupportatoAutorizzazioneRichiedenteSenzaAutenticazioneErogazione(protocollo)) {
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
						
						/*
						 * Vale solo per l'autenticazione
						Trasformazioni trasformazioni = pd.getTrasformazioni();
						if(trasformazioni != null) {
							StringBuilder sb = new StringBuilder();
							for(TrasformazioneRegola regola: trasformazioni.getRegolaList()) {
								if(regola.getApplicabilita()!= null) {
									if(regola.getApplicabilita().sizeServizioApplicativoList() > 0) {
										sb.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
										sb.append("- ");
										sb.append(regola.getNome());
									}
								}
							}
							
							if(sb.length() > 0) {
								this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_APPLICATIVI_PRESENTI_TRASFORMAZIONI_AUTORIZZAZIONE_DISABILITATA + sb.toString());
								return false;
							}
						}
						*/
					}
					if(isSupportatoAutenticazione && pd.getAutenticazione()!=null){
						
						String prefix = "";
						
						boolean modificataAutenticazione = !pd.getAutenticazione().equals(autenticazione);
						if(autenticazioneCustom!=null && !"".equals(autenticazioneCustom)) {
							modificataAutenticazione = !pd.getAutenticazione().equals(autenticazioneCustom);
						}
						if(!modificataAutenticazione) {
							if (ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY.equals(autenticazione)) {
								// verifico che non sia stato cambiato 'AppId'
								ApiKeyState immagineAttuale = new ApiKeyState(autenticazioneParametroList);
								ApiKeyState immaginePd = new ApiKeyState(this.porteDelegateCore.getParametroAutenticazione(autenticazione, pd.getProprietaAutenticazioneList()));
								if(immagineAttuale.appIdSelected!=immaginePd.appIdSelected) {
									modificataAutenticazione = true;
									prefix = "(Modifica "+ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID+") ";
								}
							}
						}
						
						// modiifcata autenticazione
						if(modificataAutenticazione) {
							if(pd.sizeServizioApplicativoList()>0) {
								this.pd.setMessage(prefix+CostantiControlStation.MESSAGGIO_ERRORE_APPLICATIVI_PRESENTI_AUTENTICAZIONE_MODIFICATA);
								return false;
							}
							
							Trasformazioni trasformazioni = pd.getTrasformazioni();
							if(trasformazioni != null) {
								StringBuilder sb = new StringBuilder();
								for(TrasformazioneRegola regola: trasformazioni.getRegolaList()) {
									if(regola.getApplicabilita()!= null) {
										if(regola.getApplicabilita().sizeServizioApplicativoList() > 0) {
											sb.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
											sb.append("- ");
											sb.append(regola.getNome());
										}
									}
								}
								
								if(sb.length() > 0) {
									this.pd.setMessage(prefix+CostantiControlStation.MESSAGGIO_ERRORE_APPLICATIVI_PRESENTI_TRASFORMAZIONI_AUTENTICAZIONE_MODIFICATA + sb.toString());
									return false;
								}
							}
							
							Search ricercaAll = new Search(true);
							List<AttivazionePolicy> listaRateLimiting = this.confCore.attivazionePolicyList(ricercaAll, RuoloPolicy.DELEGATA, pd.getNome());
							if(listaRateLimiting!=null && !listaRateLimiting.isEmpty()) {
								StringBuilder sb = new StringBuilder();
								for(AttivazionePolicy policyRT: listaRateLimiting) {
									if(policyRT.getFiltro()!= null && policyRT.getFiltro().getServizioApplicativoFruitore()!=null) {
										sb.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
										sb.append("- ");
										sb.append(policyRT.getAlias()!=null ? policyRT.getAlias() : policyRT.getIdActivePolicy());
									}
								}
								
								if(sb.length() > 0) {
									this.pd.setMessage(prefix+CostantiControlStation.MESSAGGIO_ERRORE_APPLICATIVI_PRESENTI_RATE_LIMITING_AUTENTICAZIONE_MODIFICATA + sb.toString());
									return false;
								}
							}
							
							if(this.confCore.isConfigurazioneAllarmiEnabled()) {
								ricercaAll = new Search(true);
								List<ConfigurazioneAllarmeBean> allarmi = this.confCore.allarmiList(ricercaAll, RuoloPorta.DELEGATA, pd.getNome());
								if(allarmi!=null && !allarmi.isEmpty()) {
									StringBuilder sb = new StringBuilder();
									for(ConfigurazioneAllarmeBean allarme: allarmi) {
										if(allarme.getFiltro()!= null && allarme.getFiltro().getServizioApplicativoFruitore()!=null) {
											sb.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
											sb.append("- ");
											sb.append(allarme.getAlias()!=null ? allarme.getAlias() : allarme.getNome());
										}
									}
									
									if(sb.length() > 0) {
										this.pd.setMessage(prefix+CostantiControlStation.MESSAGGIO_ERRORE_APPLICATIVI_PRESENTI_ALLARMI_AUTENTICAZIONE_MODIFICATA + sb.toString());
										return false;
									}
								}
							}
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
						if(!this.isProfiloModIPA(protocollo)) {
							if(pa.getServiziApplicativiAutorizzati()!=null && pa.getServiziApplicativiAutorizzati().sizeServizioApplicativoList()>0) {
								this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_APPLICATIVI_PRESENTI_AUTORIZZAZIONE_DISABILITATA);
								return false;
							}
						}
						
						/*
						 * Vale solo per l'autenticazione
						Trasformazioni trasformazioni = pa.getTrasformazioni();
						if(trasformazioni != null) {
							StringBuilder sbSoggetti = new StringBuilder();
							StringBuilder sbApplicativi = new StringBuilder();
							
							for(TrasformazioneRegola regola: trasformazioni.getRegolaList()) {
								if(regola.getApplicabilita()!= null) {
									if(regola.getApplicabilita().sizeSoggettoList() > 0) {
										sbSoggetti.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
										sbSoggetti.append("- ");
										sbSoggetti.append(regola.getNome());
									}
									
									if(regola.getApplicabilita().sizeServizioApplicativoList() > 0) {
										sbApplicativi.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
										sbApplicativi.append("- ");
										sbApplicativi.append(regola.getNome());
									}
								}
							}
							
							if(sbSoggetti.length() > 0) {
								this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_SOGGETTI_PRESENTI_TRASFORMAZIONI_AUTORIZZAZIONE_DISABILITATA + sbSoggetti.toString());
								return false;
							}
							
							if(sbApplicativi.length() > 0) {
								this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_APPLICATIVI_PRESENTI_TRASFORMAZIONI_AUTORIZZAZIONE_DISABILITATA + sbApplicativi.toString());
								return false;
							}
						}
						*/
					}
					if(isSupportatoAutenticazione && pa.getAutenticazione()!=null ){
						
						String prefix = "";
						
						boolean modificataAutenticazione = !pa.getAutenticazione().equals(autenticazione);
						if(!modificataAutenticazione) {
							if (ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY.equals(autenticazione)) {
								// verifico che non sia stato cambiato 'AppId'
								ApiKeyState immagineAttuale = new ApiKeyState(autenticazioneParametroList);
								ApiKeyState immaginePa = new ApiKeyState(this.porteApplicativeCore.getParametroAutenticazione(autenticazione, pa.getProprietaAutenticazioneList()));
								if(immagineAttuale.appIdSelected!=immaginePa.appIdSelected) {
									modificataAutenticazione = true;
									prefix = "(Modifica "+ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID+") ";
								}
							}
						}
						
						// modiifcata autenticazione
						if(modificataAutenticazione) {
							if(pa.getSoggetti()!=null && pa.getSoggetti().sizeSoggettoList()>0) {
								this.pd.setMessage(prefix+CostantiControlStation.MESSAGGIO_ERRORE_SOGGETTI_PRESENTI_AUTENTICAZIONE_MODIFICATA);
								return false;
							}
							if(pa.getServiziApplicativiAutorizzati()!=null && pa.getServiziApplicativiAutorizzati().sizeServizioApplicativoList()>0) {
								this.pd.setMessage(prefix+CostantiControlStation.MESSAGGIO_ERRORE_APPLICATIVI_PRESENTI_AUTENTICAZIONE_MODIFICATA);
								return false;
							}
							
							Trasformazioni trasformazioni = pa.getTrasformazioni();
							if(trasformazioni != null) {
								StringBuilder sbSoggetti = new StringBuilder();
								StringBuilder sbApplicativi = new StringBuilder();
								
								for(TrasformazioneRegola regola: trasformazioni.getRegolaList()) {
									if(regola.getApplicabilita()!= null) {
										if(regola.getApplicabilita().sizeSoggettoList() > 0) {
											sbSoggetti.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
											sbSoggetti.append("- ");
											sbSoggetti.append(regola.getNome());
										}
										
										if(regola.getApplicabilita().sizeServizioApplicativoList() > 0) {
											sbApplicativi.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
											sbApplicativi.append("- ");
											sbApplicativi.append(regola.getNome());
										}
									}
								}
								
								if(sbSoggetti.length() > 0) {
									this.pd.setMessage(prefix+CostantiControlStation.MESSAGGIO_ERRORE_SOGGETTI_PRESENTI_TRASFORMAZIONI_AUTENTICAZIONE_MODIFICATA + sbSoggetti.toString());
									return false;
								}
								
								if(sbApplicativi.length() > 0) {
									this.pd.setMessage(prefix+CostantiControlStation.MESSAGGIO_ERRORE_APPLICATIVI_PRESENTI_TRASFORMAZIONI_AUTENTICAZIONE_MODIFICATA + sbApplicativi.toString());
									return false;
								}
							}
							
							Search ricercaAll = new Search(true);
							List<AttivazionePolicy> listaRateLimiting = this.confCore.attivazionePolicyList(ricercaAll, RuoloPolicy.APPLICATIVA, pa.getNome());
							if(listaRateLimiting!=null && !listaRateLimiting.isEmpty()) {
								StringBuilder sb = new StringBuilder();
								for(AttivazionePolicy policyRT: listaRateLimiting) {
									if(policyRT.getFiltro()!= null && 
											(policyRT.getFiltro().getServizioApplicativoFruitore()!=null || policyRT.getFiltro().getNomeFruitore()!=null)) {
										sb.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
										sb.append("- ");
										sb.append(policyRT.getAlias()!=null ? policyRT.getAlias() : policyRT.getIdActivePolicy());
									}
								}
								
								if(sb.length() > 0) {
									this.pd.setMessage(prefix+CostantiControlStation.MESSAGGIO_ERRORE_APPLICATIVI_PRESENTI_RATE_LIMITING_AUTENTICAZIONE_MODIFICATA + sb.toString());
									return false;
								}
							}
							
							if(this.confCore.isConfigurazioneAllarmiEnabled()) {
								ricercaAll = new Search(true);
								List<ConfigurazioneAllarmeBean> allarmi = this.confCore.allarmiList(ricercaAll, RuoloPorta.APPLICATIVA, pa.getNome());
								if(allarmi!=null && !allarmi.isEmpty()) {
									StringBuilder sb = new StringBuilder();
									for(ConfigurazioneAllarmeBean policyRT: allarmi) {
										if(policyRT.getFiltro()!= null && 
												(policyRT.getFiltro().getServizioApplicativoFruitore()!=null || policyRT.getFiltro().getNomeFruitore()!=null)) {
											sb.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
											sb.append("- ");
											sb.append(policyRT.getAlias()!=null ? policyRT.getAlias() : policyRT.getNome());
										}
									}
									
									if(sb.length() > 0) {
										this.pd.setMessage(prefix+CostantiControlStation.MESSAGGIO_ERRORE_APPLICATIVI_PRESENTI_ALLARMI_AUTENTICAZIONE_MODIFICATA + sb.toString());
										return false;
									}
								}
							}
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
			
			// tipo autorizzazione custom
			if(autorizzazione != null && autorizzazione.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM)) {
				String autorizzazioneCustom = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM);
				
				if(StringUtils.isEmpty(autorizzazioneCustom) || CostantiControlStation.PARAMETRO_TIPO_PERSONALIZZATO_VALORE_UNDEFINED.equals(autorizzazioneCustom)){
					if(this.confCore.isConfigurazionePluginsEnabled()) {
						this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_AUTORIZZAZIONE_CUSTOM_NON_INDICATA);
					}
					else {
						this.pd.setMessage(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM+" obbligatorio per il tipo di autorizzazione selezionato");
					}
					return false;
				}
				
				if(this.checkLength255(autorizzazioneCustom,CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CUSTOM)==false) {
					return false;
				}
			}
			
			if(autorizzazioneContenutiStato!= null && !autorizzazioneContenutiStato.equals(StatoFunzionalita.DISABILITATO.getValue())) {
				if(autorizzazioneContenutiStato.equals(StatoFunzionalita.ABILITATO.getValue())) {
					if(autorizzazioneContenutiProperties==null || "".equals(autorizzazioneContenutiProperties)) {
						this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_AUTORIZZAZIONE_CONTENUTO_NON_INDICATA);
						return false;
					}
					if(autorizzazioneContenutiProperties!=null) {
						Scanner scanner = new Scanner(autorizzazioneContenutiProperties);
						try {
							while (scanner.hasNextLine()) {
								String line = scanner.nextLine();
								if(line==null || line.trim().equals("")) {
									continue;
								}
								if(line.contains("=")==false) {
									this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_AUTORIZZAZIONE_CONTENUTO_TOKEN_NON_VALIDI);
									return false;
								}
								String key = line.split("=")[0];
								if(key==null || !key.contains("$")) {
									this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_AUTORIZZAZIONE_CONTENUTO_TOKEN_NON_VALIDI_RISORSA_NON_DEFINITA_PREFIX+line);
									return false;
								}
							}
						}finally {
							scanner.close();
						}
						
						if(this.checkLength(autorizzazioneContenutiProperties, CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CONTROLLI_AUTORIZZAZIONE,-1,4000)==false) {
							return false;
						}
					}
				}
				
				if(autorizzazioneContenutiStato.equals(CostantiControlStation.VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_CUSTOM)) {
					if(StringUtils.isEmpty(autorizzazioneContenuto) || CostantiControlStation.PARAMETRO_TIPO_PERSONALIZZATO_VALORE_UNDEFINED.equals(autorizzazioneContenuto)){
						if(this.confCore.isConfigurazionePluginsEnabled()) {
							this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_AUTORIZZAZIONE_CONTENUTO_CUSTOM_NON_INDICATA);
						}
						else {
							this.pd.setMessage(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM+" obbligatorio per il tipo di autorizzazione selezionato");
						}
						return false;
					}
					
					if(this.checkLength255(autorizzazioneContenuto,	CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CONTROLLI_AUTORIZZAZIONE_CUSTOM)==false) {
						return false;
					}
				}
			}
			
			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	private boolean checkAttributes(String [] attributeAuthoritySelezionate, String attributeAuthorityAttributi) {
		if(attributeAuthoritySelezionate!=null && attributeAuthoritySelezionate.length>1) {
			Properties properties = null;
			if(attributeAuthorityAttributi!=null && StringUtils.isNotEmpty(attributeAuthorityAttributi)) {
				properties = PropertiesUtilities.convertTextToProperties(attributeAuthorityAttributi);
				if(properties!=null && properties.size()>0) {
					for (Object oKey : properties.keySet()) {
						if(oKey instanceof String) {
							String aaName = (String) oKey;
							boolean find = false;
							for (String aa : attributeAuthoritySelezionate) {
								if(aa.equals(aaName)) {
									find = true;
									break;
								}
							}
							if(!find) {
								this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_ATTRIBUTE_AUTHORITY_NON_ESISTENTE_XX,	
										aaName, CostantiControlStation.LABEL_PARAMETRO_PORTE_ATTRIBUTI_AUTHORITY_ATTRIBUTI));
								return false;
							}
							String p = properties.getProperty(aaName);
							List<String> attributi = DBUtils.convertToList(p);
							if(attributi==null || attributi.isEmpty()) {
								this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_ATTRIBUTE_AUTHORITY_VUOTA_XX,	
										aaName, CostantiControlStation.LABEL_PARAMETRO_PORTE_ATTRIBUTI_AUTHORITY_ATTRIBUTI));
								return false;
							}
						}
					}
				}
				else {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_ATTRIBUTE_AUTHORITY_NON_ESISTENTE_XX,	
							attributeAuthorityAttributi, CostantiControlStation.LABEL_PARAMETRO_PORTE_ATTRIBUTI_AUTHORITY_ATTRIBUTI));
					return false;	
				}
			}
		}
		return true;
	}
	
	
	// Stato PA
	
	public String getStatoMessageSecurityPortaApplicativa(PortaApplicativa paAssociata) {
		String statoMessageSecurity = paAssociata.getStatoMessageSecurity();
		return statoMessageSecurity;
	}


	public String getStatoDumpPortaApplicativa(PortaApplicativa paAssociata, boolean usePrefixDefault) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		DumpConfigurazione dumpConfigurazione = paAssociata.getDump();
		String statoDump = dumpConfigurazione == null ? this.getDumpLabelDefault(usePrefixDefault, true) : 
			(this.isDumpConfigurazioneAbilitato(dumpConfigurazione) ? CostantiControlStation.DEFAULT_VALUE_ABILITATO : CostantiControlStation.DEFAULT_VALUE_DISABILITATO);
		return statoDump;
	}
	
	public String getStatoDumpRichiestaPortaApplicativa(PortaApplicativa paAssociata, boolean usePrefixDefault) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		DumpConfigurazione dumpConfigurazione = paAssociata.getDump();
		String statoDump = dumpConfigurazione == null ? this.getDumpLabelDefault(usePrefixDefault, true) : 
			(this.isDumpConfigurazioneAbilitato(dumpConfigurazione, false) ? CostantiControlStation.DEFAULT_VALUE_ABILITATO : CostantiControlStation.DEFAULT_VALUE_DISABILITATO);
		return statoDump;
	}
	
	public String getStatoDumpRispostaPortaApplicativa(PortaApplicativa paAssociata, boolean usePrefixDefault) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		DumpConfigurazione dumpConfigurazione = paAssociata.getDump();
		String statoDump = dumpConfigurazione == null ? this.getDumpLabelDefault(usePrefixDefault, true) : 
			(this.isDumpConfigurazioneAbilitato(dumpConfigurazione, true) ? CostantiControlStation.DEFAULT_VALUE_ABILITATO : CostantiControlStation.DEFAULT_VALUE_DISABILITATO);
		return statoDump;
	}


	public String getStatoTracciamentoPortaApplicativa(PortaApplicativa paAssociata) {
		String statoTracciamento = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_DISABILITATA;
		
		boolean isCorrelazioneApplicativaAbilitataReq = false;
		if (paAssociata.getCorrelazioneApplicativa() != null)
			isCorrelazioneApplicativaAbilitataReq = paAssociata.getCorrelazioneApplicativa().sizeElementoList() > 0;
			
		boolean isCorrelazioneApplicativaAbilitataRes = false;
		if (paAssociata.getCorrelazioneApplicativaRisposta() != null)
			isCorrelazioneApplicativaAbilitataRes = paAssociata.getCorrelazioneApplicativaRisposta().sizeElementoList() > 0;
			
		boolean tracciamento = false;
		if(paAssociata.getTracciamento()!=null &&
				(
					(
							paAssociata.getTracciamento().getEsiti()!=null 
							&& 
							!(EsitiConfigUtils.TUTTI_ESITI_DISABILITATI+"").equals(paAssociata.getTracciamento().getEsiti())
					) 
					|| 
					paAssociata.getTracciamento().getSeverita()!=null)
				) {
			tracciamento = true;
		}
		
		if(tracciamento || isCorrelazioneApplicativaAbilitataReq || isCorrelazioneApplicativaAbilitataRes)
			statoTracciamento = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_ABILITATA;
		else 
			statoTracciamento = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_DISABILITATA;
		return statoTracciamento;
	}
	
	public boolean isRidefinitoTransazioniRegistratePortaApplicativa(PortaApplicativa paAssociata) {
		return paAssociata.getTracciamento()!=null && paAssociata.getTracciamento().getEsiti()!=null;
	}
	
	public String getStatoTransazioniRegistratePortaApplicativa(PortaApplicativa paAssociata) {
		if(paAssociata.getTracciamento()!=null && paAssociata.getTracciamento().getEsiti()!=null) {
			return CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO;
		}
		else {
			return CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT;
		}
	}
	
	public boolean isRidefinitoMessaggiDiagnosticiPortaApplicativa(PortaApplicativa paAssociata) {
		return paAssociata.getTracciamento()!=null && paAssociata.getTracciamento().getSeverita()!=null;
	}
	
	public String getStatoMessaggiDiagnosticiPortaApplicativa(PortaApplicativa paAssociata) {
		if(paAssociata.getTracciamento()!=null && paAssociata.getTracciamento().getSeverita()!=null) {
			return paAssociata.getTracciamento().getSeverita().getValue();
		}
		else {
			return CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT;
		}
	}

	public boolean isEnabledCorrelazioneApplicativaPortaApplicativa(PortaApplicativa paAssociata) {
		boolean isCorrelazioneApplicativaAbilitataReq = false;
		if (paAssociata.getCorrelazioneApplicativa() != null)
			isCorrelazioneApplicativaAbilitataReq = paAssociata.getCorrelazioneApplicativa().sizeElementoList() > 0;
			
		boolean isCorrelazioneApplicativaAbilitataRes = false;
		if (paAssociata.getCorrelazioneApplicativaRisposta() != null)
			isCorrelazioneApplicativaAbilitataRes = paAssociata.getCorrelazioneApplicativaRisposta().sizeElementoList() > 0;
			
		return  isCorrelazioneApplicativaAbilitataReq || isCorrelazioneApplicativaAbilitataRes;
	}
	
	public String getStatoCorrelazioneApplicativaPortaApplicativa(PortaApplicativa paAssociata) {
		boolean isCorrelazioneApplicativaAbilitataReq = false;
		if (paAssociata.getCorrelazioneApplicativa() != null)
			isCorrelazioneApplicativaAbilitataReq = paAssociata.getCorrelazioneApplicativa().sizeElementoList() > 0;
			
		boolean isCorrelazioneApplicativaAbilitataRes = false;
			if (paAssociata.getCorrelazioneApplicativaRisposta() != null)
				isCorrelazioneApplicativaAbilitataRes = paAssociata.getCorrelazioneApplicativaRisposta().sizeElementoList() > 0;
				
		if(isCorrelazioneApplicativaAbilitataReq && isCorrelazioneApplicativaAbilitataRes)
			return PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_ABILITATA;
		
		if(isCorrelazioneApplicativaAbilitataReq)
			return CostantiControlStation.VALUE_PARAMETRO_DUMP_SEZIONE_RICHIESTA;
		
		if(isCorrelazioneApplicativaAbilitataRes)
			return CostantiControlStation.VALUE_PARAMETRO_DUMP_SEZIONE_RISPOSTA;
			
		return PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_DISABILITATA;
	}


	public String getStatoMTOMPortaApplicativa(PortaApplicativa paAssociata) {
		String statoMTOM = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_DISABILITATO;
		boolean isMTOMAbilitatoReq = false;
		boolean isMTOMAbilitatoRes= false;
		if(paAssociata.getMtomProcessor()!= null){
			if(paAssociata.getMtomProcessor().getRequestFlow() != null){
				if(paAssociata.getMtomProcessor().getRequestFlow().getMode() != null){
					MTOMProcessorType mode = paAssociata.getMtomProcessor().getRequestFlow().getMode();
					if(!mode.equals(MTOMProcessorType.DISABLE))
						isMTOMAbilitatoReq = true;
				}
			}

			if(paAssociata.getMtomProcessor().getResponseFlow() != null){
				if(paAssociata.getMtomProcessor().getResponseFlow().getMode() != null){
					MTOMProcessorType mode = paAssociata.getMtomProcessor().getResponseFlow().getMode();
					if(!mode.equals(MTOMProcessorType.DISABLE))
						isMTOMAbilitatoRes = true;
				}
			}
		}

		if(isMTOMAbilitatoReq || isMTOMAbilitatoRes)
			statoMTOM = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_ABILITATO;
		else 
			statoMTOM = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_DISABILITATO;
		return statoMTOM;
	}

	public MTOMProcessorType getProcessorTypeRequestMTOMPortaApplicativa(PortaApplicativa paAssociata) {
		if(paAssociata.getMtomProcessor()!= null){
			if(paAssociata.getMtomProcessor().getRequestFlow() != null){
				if(paAssociata.getMtomProcessor().getRequestFlow().getMode() != null){
					MTOMProcessorType mode = paAssociata.getMtomProcessor().getRequestFlow().getMode();
					return mode;
				}
			}
		}
		return null;
	}
	
	public MTOMProcessorType getProcessorTypeResponseMTOMPortaApplicativa(PortaApplicativa paAssociata) {
		if(paAssociata.getMtomProcessor()!= null){
			if(paAssociata.getMtomProcessor().getResponseFlow() != null){
				if(paAssociata.getMtomProcessor().getResponseFlow().getMode() != null){
					MTOMProcessorType mode = paAssociata.getMtomProcessor().getResponseFlow().getMode();
					return mode;
				}
			}
		}
		return null;
	}

	public String getStatoValidazionePortaApplicativa(PortaApplicativa paAssociata) {
		String statoValidazione = null;
		
		ValidazioneContenutiApplicativi vx = paAssociata.getValidazioneContenutiApplicativi();
		if (vx == null) {
			statoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
		} else {
			if(vx.getStato()!=null)
				statoValidazione = vx.getStato().toString();
			if ((statoValidazione == null) || "".equals(statoValidazione)) {
				statoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
			}
		}
		return statoValidazione;
	}

	public String getTipoValidazionePortaApplicativa(PortaApplicativa paAssociata) {
		String tipoValidazione = null;
		
		ValidazioneContenutiApplicativi vx = paAssociata.getValidazioneContenutiApplicativi();
		if (vx != null) {
			if(vx.getTipo()!=null) {
				tipoValidazione = vx.getTipo().getValue();
			}
		}
		return tipoValidazione;
	}

	public String getStatoResponseCachingPortaApplicativa(PortaApplicativa paAssociata, boolean usePrefixDefault) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		String stato = null;
		ResponseCachingConfigurazione rc = paAssociata.getResponseCaching();
		if (rc == null) {
			stato = this.getResponseCachingLabelDefault(usePrefixDefault);
		} else {
			if(rc.getStato()!=null) {
				stato = rc.getStato().getValue();
			}
			else {
				stato = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
			}
		}
		return stato;
	}
	
	public String getStatoGestioneCorsPortaApplicativa(PortaApplicativa paAssociata, boolean usePrefixDefault) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		String stato = null;
		CorsConfigurazione cc = paAssociata.getGestioneCors();
		if (cc == null) {
			stato = this.getGestioneCorsLabelDefault(usePrefixDefault);
		} else {
			if(cc.getStato()==null || StatoFunzionalita.DISABILITATO.equals(cc.getStato())) {
				stato = StatoFunzionalita.DISABILITATO.getValue();
			}
			else {
				stato = getLabelTipoGestioneCors(cc.getTipo());
			}
		}
		return stato;
	}
	
	public String getStatoOpzioniAvanzatePortaDelegataDefault(PortaDelegata pdAssociata) throws Exception {
		return _getStatoOpzioniAvanzatePortaApplicativaDefault(pdAssociata.getOptions());
	}
	public String getStatoOpzioniAvanzatePortaApplicativaDefault(PortaApplicativa paAssociata) throws Exception {
		return _getStatoOpzioniAvanzatePortaApplicativaDefault(paAssociata.getOptions());
	}
	private String _getStatoOpzioniAvanzatePortaApplicativaDefault(String options) throws Exception {
		String stato = null;
		
		Map<String, List<String>> props = PropertiesSerializator.convertoFromDBColumnValue(options);
		if(props==null || props.size()<=0) {
			stato = StatoFunzionalita.DISABILITATO.getValue();
		}
		else {
			stato = StatoFunzionalita.ABILITATO.getValue();
		}
		
		return stato;
	}
	
	public String getStatoControlloAccessiPortaApplicativa(String protocollo, PortaApplicativa paAssociata) throws DriverControlStationException, DriverControlStationNotFound {
		return this._getStatoControlloAccessiPortaApplicativa(protocollo, paAssociata, null);
	}
	public void setStatoControlloAccessiPortaApplicativa(String protocollo, PortaApplicativa paAssociata, DataElement de) throws DriverControlStationException, DriverControlStationNotFound {
		this._getStatoControlloAccessiPortaApplicativa(protocollo, paAssociata, de);
	}
	private String _getStatoControlloAccessiPortaApplicativa(String protocollo, PortaApplicativa paAssociata, DataElement de) throws DriverControlStationException, DriverControlStationNotFound {
		String gestioneToken = null;
		String gestioneTokenPolicy = null;
		String gestioneTokenOpzionale = "";
		GestioneToken gestioneTokenConfig = null;
		AutorizzazioneScope autorizzazioneScope = null;
		if(paAssociata.getGestioneToken()!=null && paAssociata.getGestioneToken().getPolicy()!=null &&
				!"".equals(paAssociata.getGestioneToken().getPolicy()) &&
				!"-".equals(paAssociata.getGestioneToken().getPolicy())) {
			gestioneToken = StatoFunzionalita.ABILITATO.getValue();
			gestioneTokenPolicy = paAssociata.getGestioneToken().getPolicy();
			
			if(paAssociata.getGestioneToken()!=null && paAssociata.getGestioneToken().getTokenOpzionale()!=null){
				if (paAssociata.getGestioneToken().getTokenOpzionale().equals(StatoFunzionalita.ABILITATO)) {
					gestioneTokenOpzionale = Costanti.CHECK_BOX_ENABLED;
				}
			}
			
			gestioneTokenConfig = paAssociata.getGestioneToken();
			autorizzazioneScope = paAssociata.getScope();
		}

		String autenticazione = paAssociata.getAutenticazione();
		String autenticazioneCustom = null;
		if (autenticazione != null && !TipoAutenticazione.getValues().contains(autenticazione)) {
			autenticazioneCustom = autenticazione;
			autenticazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM;
					
			Plugin plugin = null;
			try {
				plugin = this.confCore.getPlugin(TipoPlugin.AUTENTICAZIONE,autenticazioneCustom, false);
			}catch(Throwable e) {}
			
			if(plugin!=null && plugin.getLabel()!=null) {
				autenticazioneCustom = plugin.getLabel();
			}
		}
		String autenticazioneOpzionale = "";
		if(paAssociata.getAutenticazioneOpzionale()!=null){
			if (paAssociata.getAutenticazioneOpzionale().equals(StatoFunzionalita.ABILITATO)) {
				autenticazioneOpzionale = Costanti.CHECK_BOX_ENABLED;
			}
		}
		
		String autorizzazione= null, autorizzazioneCustom = null;
		int sizeApplicativi = 0;
		int sizeSoggetti = 0;
		int sizeRuoli = 0;
		if (paAssociata.getAutorizzazione() != null &&
				!TipoAutorizzazione.getAllValues().contains(paAssociata.getAutorizzazione())) {
			autorizzazioneCustom = paAssociata.getAutorizzazione();
			autorizzazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM;
			
			Plugin plugin = null;
			try {
				plugin = this.confCore.getPlugin(TipoPlugin.AUTORIZZAZIONE,autorizzazioneCustom, false);
			}catch(Throwable e) {}
			
			if(plugin!=null && plugin.getLabel()!=null) {
				autorizzazioneCustom = plugin.getLabel();
			}
		}
		else{
			if(de!=null) {
				autorizzazione = paAssociata.getAutorizzazione();
			}
			else {
				autorizzazione = AutorizzazioneUtilities.convertToStato(paAssociata.getAutorizzazione());
			}
			autorizzazione = paAssociata.getAutorizzazione();
		}
		if(paAssociata.getServiziApplicativiAutorizzati()!=null) {
			sizeApplicativi = paAssociata.getServiziApplicativiAutorizzati().sizeServizioApplicativoList();
		}
		if(paAssociata.getSoggetti()!=null) {
			sizeSoggetti = paAssociata.getSoggetti().sizeSoggettoList();
		}
		if(paAssociata.getRuoli()!=null) {
			sizeRuoli = paAssociata.getRuoli().sizeRuoloList();
		}
		
		String autorizzazioneContenuti = paAssociata.getAutorizzazioneContenuto();
		if(StringUtils.isNotEmpty(autorizzazioneContenuti) && !CostantiAutorizzazione.AUTORIZZAZIONE_CONTENUTO_BUILT_IN.equals(autorizzazioneContenuti)) {
			Plugin plugin = null;
			try {
				plugin = this.confCore.getPlugin(TipoPlugin.AUTORIZZAZIONE_CONTENUTI,autorizzazioneContenuti, false);
			}catch(Throwable e) {}
			
			if(plugin!=null && plugin.getLabel()!=null) {
				autorizzazioneContenuti = plugin.getLabel();
			}
		}
		
		List<AttributeAuthority> aa = paAssociata.getAttributeAuthorityList();
		List<String> attributeAuthority = new ArrayList<String>();
		if(aa!=null && !aa.isEmpty()) {
			for (AttributeAuthority a : aa) {
				attributeAuthority.add(a.getNome());
			}
		}
		
		if(de!=null) {
			this.setStatoControlloAccessi(de, false, 
					gestioneToken, gestioneTokenOpzionale, gestioneTokenPolicy, gestioneTokenConfig,
					autenticazione,  autenticazioneOpzionale, autenticazioneCustom,
					autorizzazione, autorizzazioneCustom, sizeApplicativi, sizeSoggetti, sizeRuoli, autorizzazioneScope,
					autorizzazioneContenuti,
					protocollo,
					attributeAuthority);
			return  null;
		}
		else {
			return this.getLabelStatoControlloAccessi(
				false, 
				gestioneToken, gestioneTokenOpzionale, gestioneTokenPolicy, gestioneTokenConfig,
				autenticazione,  autenticazioneOpzionale, autenticazioneCustom,
				autorizzazione, autorizzazioneCustom, sizeApplicativi, sizeSoggetti, sizeRuoli, autorizzazioneScope,
				autorizzazioneContenuti,
				attributeAuthority);
		}
	}
	
	public String getStatoGestioneTokenPortaApplicativa(PortaApplicativa paAssociata) {
		String gestioneToken = null;
		if(paAssociata.getGestioneToken()!=null && paAssociata.getGestioneToken().getPolicy()!=null &&
				!"".equals(paAssociata.getGestioneToken().getPolicy()) &&
				!"-".equals(paAssociata.getGestioneToken().getPolicy())) {
			gestioneToken = StatoFunzionalita.ABILITATO.getValue();
		}
		
		return this.getLabelStatoGestioneToken(gestioneToken);
	}
	
	public String getStatoAutenticazionePortaApplicativa(PortaApplicativa paAssociata) {
		String autenticazione = paAssociata.getAutenticazione();
		String autenticazioneCustom = null;
		if (autenticazione != null && !TipoAutenticazione.getValues().contains(autenticazione)) {
			autenticazioneCustom = autenticazione;
			autenticazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM;
		}
		String autenticazioneOpzionale = "";
		if(paAssociata.getAutenticazioneOpzionale()!=null){
			if (paAssociata.getAutenticazioneOpzionale().equals(StatoFunzionalita.ABILITATO)) {
				autenticazioneOpzionale = Costanti.CHECK_BOX_ENABLED;
			}
		}

		return this.getLabelStatoAutenticazione(autenticazione, autenticazioneOpzionale, autenticazioneCustom);
	}
	
	public String getStatoAutorizzazionePortaApplicativa(PortaApplicativa paAssociata) {
		String autorizzazioneContenuti = paAssociata.getAutorizzazioneContenuto();
		
		String autorizzazione= null, autorizzazioneCustom = null;
		if (paAssociata.getAutorizzazione() != null &&
				!TipoAutorizzazione.getAllValues().contains(paAssociata.getAutorizzazione())) {
			autorizzazioneCustom = paAssociata.getAutorizzazione();
			autorizzazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM;
		}
		else{
			autorizzazione = AutorizzazioneUtilities.convertToStato(paAssociata.getAutorizzazione());
		}
		
		return this.getLabelStatoAutorizzazione(autorizzazione, autorizzazioneContenuti, autorizzazioneCustom);
	}
	
	
	
	// Stato Porta Delegata
	
	public String getStatoDumpPortaDelegata(PortaDelegata pdAssociata, boolean usePrefixDefault)
			throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		DumpConfigurazione dumpConfigurazione = pdAssociata.getDump();
		String statoDump = dumpConfigurazione == null ? this.getDumpLabelDefault(usePrefixDefault, false) : 
			(this.isDumpConfigurazioneAbilitato(dumpConfigurazione) ? CostantiControlStation.DEFAULT_VALUE_ABILITATO : CostantiControlStation.DEFAULT_VALUE_DISABILITATO);
		return statoDump;
	}


	public String getStatoTracciamentoPortaDelegata(PortaDelegata pdAssociata) {
		String statoTracciamento = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_DISABILITATA;
		boolean tracciamento = false;
		boolean isCorrelazioneApplicativaAbilitataReq = false;
		boolean isCorrelazioneApplicativaAbilitataRes = false;
		
		if (pdAssociata.getCorrelazioneApplicativa() != null)
			isCorrelazioneApplicativaAbilitataReq = pdAssociata.getCorrelazioneApplicativa().sizeElementoList() > 0;

		if (pdAssociata.getCorrelazioneApplicativaRisposta() != null)
			isCorrelazioneApplicativaAbilitataRes = pdAssociata.getCorrelazioneApplicativaRisposta().sizeElementoList() > 0;
		
		if(pdAssociata.getTracciamento()!=null &&
				(
					(
							pdAssociata.getTracciamento().getEsiti()!=null 
							&& 
							!(EsitiConfigUtils.TUTTI_ESITI_DISABILITATI+"").equals(pdAssociata.getTracciamento().getEsiti())
					) 
					|| 
					pdAssociata.getTracciamento().getSeverita()!=null)
				) {
			tracciamento = true;
		}
		
		if(tracciamento || isCorrelazioneApplicativaAbilitataReq || isCorrelazioneApplicativaAbilitataRes)
			statoTracciamento = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_ABILITATA;
		else 
			statoTracciamento = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_DISABILITATA;
		return statoTracciamento;
	}


	public String getStatoMTOMPortaDelegata(PortaDelegata pdAssociata) {
		String statoMTOM = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_DISABILITATO;
		
		boolean isMTOMAbilitatoReq = false;
		boolean isMTOMAbilitatoRes= false;
		if(pdAssociata.getMtomProcessor()!= null){
			if(pdAssociata.getMtomProcessor().getRequestFlow() != null){
				if(pdAssociata.getMtomProcessor().getRequestFlow().getMode() != null){
					MTOMProcessorType mode = pdAssociata.getMtomProcessor().getRequestFlow().getMode();
					if(!mode.equals(MTOMProcessorType.DISABLE))
						isMTOMAbilitatoReq = true;
				}
			}

			if(pdAssociata.getMtomProcessor().getResponseFlow() != null){
				if(pdAssociata.getMtomProcessor().getResponseFlow().getMode() != null){
					MTOMProcessorType mode = pdAssociata.getMtomProcessor().getResponseFlow().getMode();
					if(!mode.equals(MTOMProcessorType.DISABLE))
						isMTOMAbilitatoRes = true;
				}
			}
		}

		if(isMTOMAbilitatoReq || isMTOMAbilitatoRes)
			statoMTOM = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_ABILITATO;
		else 
			statoMTOM = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_DISABILITATO;
		return statoMTOM;
	}


	public String getStatoMessageSecurityPortaDelegata(PortaDelegata pdAssociata) {
		String statoMessageSecurity = pdAssociata.getStatoMessageSecurity();
		return statoMessageSecurity;
	}


	public String getStatoValidazionePortaDelegata(PortaDelegata pdAssociata) {
		String statoValidazione = null;
		ValidazioneContenutiApplicativi vx = pdAssociata.getValidazioneContenutiApplicativi();
		if (vx == null) {
			statoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
		} else {
			if(vx.getStato()!=null)
				statoValidazione = vx.getStato().toString();
			if ((statoValidazione == null) || "".equals(statoValidazione)) {
				statoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
			}
		}
		return statoValidazione;
	}

	public String getStatoResponseCachingPortaDelegata(PortaDelegata pdAssociata, boolean usePrefixDefault) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		String stato = null;
		ResponseCachingConfigurazione rc = pdAssociata.getResponseCaching();
		if (rc == null) {
			stato = this.getResponseCachingLabelDefault(usePrefixDefault);
		} else {
			if(rc.getStato()!=null) {
				stato = rc.getStato().getValue();
			}
			else {
				stato = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
			}
		}
		return stato;
	}
	
	public String getStatoGestioneCorsPortaDelegata(PortaDelegata pdAssociata, boolean usePrefixDefault) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		String stato = null;
		CorsConfigurazione cc = pdAssociata.getGestioneCors();
		if (cc == null) {
			stato = this.getGestioneCorsLabelDefault(usePrefixDefault);
		} else {
			if(cc.getStato()==null || StatoFunzionalita.DISABILITATO.equals(cc.getStato())) {
				stato = StatoFunzionalita.DISABILITATO.getValue();
			}
			else {
				stato = getLabelTipoGestioneCors(cc.getTipo());
			}
		}
		return stato;
	}

	public String getStatoControlloAccessiPortaDelegata(String protocollo, PortaDelegata pdAssociata) throws DriverControlStationException, DriverControlStationNotFound {
		return this._getStatoControlloAccessiPortaDelegata(protocollo, pdAssociata, null);
	}
	public void setStatoControlloAccessiPortaDelegata(String protocollo, PortaDelegata pdAssociata, DataElement de) throws DriverControlStationException, DriverControlStationNotFound {
		this._getStatoControlloAccessiPortaDelegata(protocollo, pdAssociata, de);
	}
	private String _getStatoControlloAccessiPortaDelegata(String protocollo, PortaDelegata pdAssociata, DataElement de) throws DriverControlStationException, DriverControlStationNotFound {
		String gestioneToken = null;
		String gestioneTokenPolicy = null;
		String gestioneTokenOpzionale = "";
		GestioneToken gestioneTokenConfig = null;
		AutorizzazioneScope autorizzazioneScope = null;
		if(pdAssociata.getGestioneToken()!=null && pdAssociata.getGestioneToken().getPolicy()!=null &&
				!"".equals(pdAssociata.getGestioneToken().getPolicy()) &&
				!"-".equals(pdAssociata.getGestioneToken().getPolicy())) {
			gestioneToken = StatoFunzionalita.ABILITATO.getValue();
			gestioneTokenPolicy = pdAssociata.getGestioneToken().getPolicy();
			
			if(pdAssociata.getGestioneToken()!=null && pdAssociata.getGestioneToken().getTokenOpzionale()!=null){
				if (pdAssociata.getGestioneToken().getTokenOpzionale().equals(StatoFunzionalita.ABILITATO)) {
					gestioneTokenOpzionale = Costanti.CHECK_BOX_ENABLED;
				}
			}
			
			gestioneTokenConfig = pdAssociata.getGestioneToken();
			autorizzazioneScope = pdAssociata.getScope();
		}
		
		String autenticazione = pdAssociata.getAutenticazione();
		String autenticazioneCustom = null;
		if (autenticazione != null && !TipoAutenticazione.getValues().contains(autenticazione)) {
			autenticazioneCustom = autenticazione;
			autenticazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM;
			
			Plugin plugin = null;
			try {
				plugin = this.confCore.getPlugin(TipoPlugin.AUTENTICAZIONE,autenticazioneCustom, false);
			}catch(Throwable e) {}
			
			if(plugin!=null && plugin.getLabel()!=null) {
				autenticazioneCustom = plugin.getLabel();
			}
		}
		String autenticazioneOpzionale = "";
		if(pdAssociata.getAutenticazioneOpzionale()!=null){
			if (pdAssociata.getAutenticazioneOpzionale().equals(StatoFunzionalita.ABILITATO)) {
				autenticazioneOpzionale = Costanti.CHECK_BOX_ENABLED;
			}
		}
		
		String autorizzazione= null, autorizzazioneCustom = null;
		int sizeApplicativi = 0;
		int sizeSoggetti = 0;
		int sizeRuoli = 0;
		if (pdAssociata.getAutorizzazione() != null &&
				!TipoAutorizzazione.getAllValues().contains(pdAssociata.getAutorizzazione())) {
			autorizzazioneCustom = pdAssociata.getAutorizzazione();
			autorizzazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM;
			
			Plugin plugin = null;
			try {
				plugin = this.confCore.getPlugin(TipoPlugin.AUTORIZZAZIONE,autorizzazioneCustom, false);
			}catch(Throwable e) {}
			
			if(plugin!=null && plugin.getLabel()!=null) {
				autorizzazioneCustom = plugin.getLabel();
			}
		}
		else{
			if(de!=null) {
				autorizzazione = pdAssociata.getAutorizzazione();
			}
			else {
				autorizzazione = AutorizzazioneUtilities.convertToStato(pdAssociata.getAutorizzazione());
			}
		}
		sizeApplicativi = pdAssociata.sizeServizioApplicativoList();
		if(pdAssociata.getRuoli()!=null) {
			sizeRuoli = pdAssociata.getRuoli().sizeRuoloList();
		}
		
		String autorizzazioneContenuti = pdAssociata.getAutorizzazioneContenuto();
		if(StringUtils.isNotEmpty(autorizzazioneContenuti) && !CostantiAutorizzazione.AUTORIZZAZIONE_CONTENUTO_BUILT_IN.equals(autorizzazioneContenuti)) {
			Plugin plugin = null;
			try {
				plugin = this.confCore.getPlugin(TipoPlugin.AUTORIZZAZIONE_CONTENUTI,autorizzazioneContenuti, false);
			}catch(Throwable e) {}
			
			if(plugin!=null && plugin.getLabel()!=null) {
				autorizzazioneContenuti = plugin.getLabel();
			}
		}
		
		List<AttributeAuthority> aa = pdAssociata.getAttributeAuthorityList();
		List<String> attributeAuthority = new ArrayList<String>();
		if(aa!=null && !aa.isEmpty()) {
			for (AttributeAuthority a : aa) {
				attributeAuthority.add(a.getNome());
			}
		}
		
		if(de!=null) {
			this.setStatoControlloAccessi(de, true, 
					gestioneToken, gestioneTokenOpzionale, gestioneTokenPolicy, gestioneTokenConfig,
					autenticazione,  autenticazioneOpzionale, autenticazioneCustom,
					autorizzazione, autorizzazioneCustom, sizeApplicativi, sizeSoggetti, sizeRuoli, autorizzazioneScope,
					autorizzazioneContenuti, protocollo,
					attributeAuthority);
			return  null;
		}
		else {
			return this.getLabelStatoControlloAccessi(
				true, 
				gestioneToken, gestioneTokenOpzionale, gestioneTokenPolicy, gestioneTokenConfig,
				autenticazione,  autenticazioneOpzionale, autenticazioneCustom,
				autorizzazione, autorizzazioneCustom, sizeApplicativi, sizeSoggetti, sizeRuoli, autorizzazioneScope,
				autorizzazioneContenuti,
				attributeAuthority);
		}
	}
	
	public String getStatoAutenticazionePortaDelegata(PortaDelegata pdAssociata) {
		String autenticazione = pdAssociata.getAutenticazione();
		String autenticazioneCustom = null;
		if (autenticazione != null && !TipoAutenticazione.getValues().contains(autenticazione)) {
			autenticazioneCustom = autenticazione;
			autenticazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM;
		}
		String autenticazioneOpzionale = "";
		if(pdAssociata.getAutenticazioneOpzionale()!=null){
			if (pdAssociata.getAutenticazioneOpzionale().equals(StatoFunzionalita.ABILITATO)) {
				autenticazioneOpzionale = Costanti.CHECK_BOX_ENABLED;
			}
		}
		return this.getLabelStatoAutenticazione(autenticazione, autenticazioneOpzionale, autenticazioneCustom);
	}
	
	public String getStatoAutorizzazionePortaDelegata(PortaDelegata pdAssociata) {
		String autorizzazioneContenuti = pdAssociata.getAutorizzazioneContenuto();
		String autorizzazione= null, autorizzazioneCustom = null;
		if (pdAssociata.getAutorizzazione() != null &&
				!TipoAutorizzazione.getAllValues().contains(pdAssociata.getAutorizzazione())) {
			autorizzazioneCustom = pdAssociata.getAutorizzazione();
			autorizzazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM;
		}
		else{
			autorizzazione = AutorizzazioneUtilities.convertToStato(pdAssociata.getAutorizzazione());
		}
		
		return this.getLabelStatoAutorizzazione(autorizzazione, autorizzazioneContenuti, autorizzazioneCustom);
	}
	
	public String getStatoGestioneTokenPortaDelegata(PortaDelegata pdAssociata) {
		String gestioneToken = null;
		if(pdAssociata.getGestioneToken()!=null && pdAssociata.getGestioneToken().getPolicy()!=null &&
				!"".equals(pdAssociata.getGestioneToken().getPolicy()) &&
				!"-".equals(pdAssociata.getGestioneToken().getPolicy())) {
			gestioneToken = StatoFunzionalita.ABILITATO.getValue();
		}
		 
		return this.getLabelStatoGestioneToken(gestioneToken);
	}
	
	
	// Stato Generico
	
	public String getLabelStatoControlloAccessi(
			boolean portaDelegata,
			String gestioneToken, String gestioneTokenOpzionale, String gestioneTokenPolicy, GestioneToken gestioneTokenConfig,
			String autenticazione, String autenticazioneOpzionale, String autenticazioneCustom,
			String autorizzazione, String autorizzazioneCustom, int sizeApplicativi, int sizeSoggetti, int sizeRuoli, AutorizzazioneScope autorizzazioneScope,
			String autorizzazioneContenuti,
			List<String> attributeAuthority
			) {
		
		if(gestioneToken!=null && StatoFunzionalita.ABILITATO.getValue().equals(gestioneToken)) {
			return CostantiControlStation.DEFAULT_VALUE_ABILITATO;
		}
		
		if(autenticazione != null && !TipoAutenticazione.DISABILITATO.equals(autenticazione))
			return CostantiControlStation.DEFAULT_VALUE_ABILITATO;
		
		if(autenticazioneOpzionale != null && ServletUtils.isCheckBoxEnabled(autenticazioneOpzionale))
			return CostantiControlStation.DEFAULT_VALUE_ABILITATO;
		
		if(attributeAuthority!=null && attributeAuthority.size()>0) {
			return CostantiControlStation.DEFAULT_VALUE_ABILITATO;
		}
		
		if(!AutorizzazioneUtilities.STATO_DISABILITATO.equals(autorizzazione))
			return CostantiControlStation.DEFAULT_VALUE_ABILITATO;
		
		if(StringUtils.isNotEmpty(autorizzazioneContenuti))
			return CostantiControlStation.DEFAULT_VALUE_ABILITATO;
		
		return CostantiControlStation.DEFAULT_VALUE_DISABILITATO;
		
	}
	
	public void setStatoControlloAccessi(DataElement de, 
			boolean portaDelegata,
			String gestioneToken, String gestioneTokenOpzionale, String gestioneTokenPolicy, GestioneToken gestioneTokenConfig,
			String autenticazione, String autenticazioneOpzionale, String autenticazioneCustom,
			String autorizzazione, String autorizzazioneCustom, int sizeApplicativi, int sizeSoggetti, int sizeRuoli, AutorizzazioneScope autorizzazioneScope,
			String autorizzazioneContenuti,
			String protocollo,
			List<String> attributeAuthority
			) throws DriverControlStationException, DriverControlStationNotFound {
		
		boolean modipa = this.isProfiloModIPA(protocollo);
		
		de.setType(DataElementType.MULTI_SELECT);
		
		// gestione token
		if(gestioneToken!=null && StatoFunzionalita.ABILITATO.getValue().equals(gestioneToken)) {
			
			CheckboxStatusType statusGestioneToken = null;
			
			StringBuilder bf = new StringBuilder();
			StringBuilder bfToolTip = new StringBuilder();
			bf.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_GESTIONE_TOKEN);
			bfToolTip.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_GESTIONE_TOKEN);
			if(ServletUtils.isCheckBoxEnabled(gestioneTokenOpzionale)) {
				bfToolTip.append(" (").append(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_OPZIONALE).append(") ");
				statusGestioneToken = CheckboxStatusType.CONFIG_WARNING;
			}
			else {
				statusGestioneToken = CheckboxStatusType.CONFIG_ENABLE;
			}
			bfToolTip.append(": ").append(gestioneTokenPolicy);
			
			if(gestioneTokenConfig!=null && gestioneTokenConfig.getAutenticazione()!=null) {
				StringBuilder bfTokenAuth = new StringBuilder();
				if(StatoFunzionalita.ABILITATO.equals(gestioneTokenConfig.getAutenticazione().getIssuer())) {
					if(bfTokenAuth.length()>0) {
						bfTokenAuth.append(", ");
					}
					bfTokenAuth.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_ISSUER);
				}
				if(StatoFunzionalita.ABILITATO.equals(gestioneTokenConfig.getAutenticazione().getClientId())) {
					if(bfTokenAuth.length()>0) {
						bfTokenAuth.append(", ");
					}
					bfTokenAuth.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_CLIENT_ID);
				}
				if(StatoFunzionalita.ABILITATO.equals(gestioneTokenConfig.getAutenticazione().getSubject())) {
					if(bfTokenAuth.length()>0) {
						bfTokenAuth.append(", ");
					}
					bfTokenAuth.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_SUBJECT);
				}
				if(StatoFunzionalita.ABILITATO.equals(gestioneTokenConfig.getAutenticazione().getUsername())) {
					if(bfTokenAuth.length()>0) {
						bfTokenAuth.append(", ");
					}
					bfTokenAuth.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_USERNAME);
				}
				if(StatoFunzionalita.ABILITATO.equals(gestioneTokenConfig.getAutenticazione().getEmail())) {
					if(bfTokenAuth.length()>0) {
						bfTokenAuth.append(", ");
					}
					bfTokenAuth.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_MAIL);
				}
				if(bfTokenAuth.length()>0) {
					bfToolTip.append("\n");
				}
				bfToolTip.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE_TOKEN);
				bfToolTip.append(": ").append(bfTokenAuth.toString());
			}
			
			de.addStatus(bfToolTip.toString(), bf.toString(), statusGestioneToken);
		}
		
		// autenticazione
		if(autenticazione != null && !TipoAutenticazione.DISABILITATO.equals(autenticazione)) {
			StringBuilder bf = new StringBuilder();
			StringBuilder bfToolTip = new StringBuilder();
			boolean opzionale = false;
			String authTrasporto = autenticazione;
			if(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM.equals(autenticazione)) {
				authTrasporto = autenticazioneCustom;
			}
			bf.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE);
			bfToolTip.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE);
			if(modipa && !portaDelegata) {
				bf.append(" ").append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE_CANALE);
				bfToolTip.append(" ").append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE_CANALE);
			}
			else {
				bf.append(" ").append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE_TRASPORTO);
				bfToolTip.append(" ").append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE_TRASPORTO);
			}
			if(ServletUtils.isCheckBoxEnabled(autenticazioneOpzionale)) {
				bfToolTip.append(" (").append(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_OPZIONALE).append(") ");
				opzionale = true;
			}
			bfToolTip.append(": ");
			if(TipoAutenticazione.contains(authTrasporto)) {
				String labelAuth = TipoAutenticazione.toEnumConstant(authTrasporto).getLabel();
				bf.append(" [ ").append(labelAuth).append(" ]");
				bfToolTip.append(labelAuth);	
			}
			else {
				//bf.append(" [ ").append(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM).append(" ]");
				bf.append(" [ ").append(CostantiControlStation.DEFAULT_LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM).append(" '").append(authTrasporto).append("'").append(" ]");
				bfToolTip.append(authTrasporto);
			}
			
			CheckboxStatusType statusAutenticazione = null;
			if(opzionale) {
				statusAutenticazione =CheckboxStatusType.CONFIG_WARNING;
			}
			else {
				statusAutenticazione = CheckboxStatusType.CONFIG_ENABLE;
			}
			de.addStatus(bfToolTip.toString(), bf.toString(), statusAutenticazione);
		}
		
		// attribute authority
		if(attributeAuthority!=null && attributeAuthority.size()>0) {
			StringBuilder bf = new StringBuilder();
			StringBuilder bfToolTip = new StringBuilder();
			bf.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_ATTRIBUTI_AUTHORITY);
			bfToolTip.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_ATTRIBUTI_AUTHORITY);
			if(!CostantiAutorizzazione.AUTORIZZAZIONE_CONTENUTO_BUILT_IN.equals(autorizzazioneContenuti)) {
				bfToolTip.append(": ").append(attributeAuthority.toString());
			}
			de.addStatus(bfToolTip.toString(), bf.toString(), CheckboxStatusType.CONFIG_ENABLE);
		}
		
		// autorizzazione
		if(!AutorizzazioneUtilities.STATO_DISABILITATO.equals(autorizzazione)) {
			StringBuilder bf = new StringBuilder();
			StringBuilder bfToolTip = new StringBuilder();
			StringBuilder bfToolTipNotValid = new StringBuilder();
			int rowsToolTip = 0;
			
			Boolean validPuntuale = null;
			if(TipoAutorizzazione.isAuthenticationRequired(autorizzazione)) {
				if(bf.length()>0) {
					bf.append(",");
				}
				if(bf.length()<=0) {
					if(modipa && !portaDelegata) {
						bf.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CANALE);
					}
					else {
						bf.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE);
					}
					bf.append(" [ ");
				}
				bf.append(" ");
				bf.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE_SERVIZI_APPLICATIVI_SUFFIX);
			
				if(modipa && !portaDelegata) {
					validPuntuale = sizeSoggetti>0;
				}
				else {
					validPuntuale = sizeApplicativi>0 || sizeSoggetti>0;
				}
				
				if(validPuntuale) {
					rowsToolTip++;
					if(bfToolTip.length()>0) {
						bfToolTip.append("\n");
					}
					bfToolTip.append("- ").append(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE_SERVIZI_APPLICATIVI_SUFFIX);
					if(!portaDelegata) {
						bfToolTip.append(" ").append(CostantiControlStation.LABEL_SOGGETTI).append(" (").append(sizeSoggetti).append(")");
					}
					if(!modipa || portaDelegata) {
						if(!portaDelegata) {
							bfToolTip.append(",");
						}
						bfToolTip.append(" ").append(CostantiControlStation.LABEL_APPLICATIVI).append(" (").append(sizeApplicativi).append(")");
					}
				}
				else {
					if(bfToolTipNotValid.length()>0) {
						bfToolTipNotValid.append("\n");
					}
					bfToolTipNotValid.append(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_PUNTUALE_NO_FRUITORI);
				}
			}
			
			Boolean validRuoli = null;
			if(TipoAutorizzazione.isRolesRequired(autorizzazione)) {
				if(bf.length()>0) {
					bf.append(",");
				}
				if(bf.length()<=0) {
					if(modipa && !portaDelegata) {
						bf.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CANALE);
					}
					else {
						bf.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE);
					}
					bf.append(" [ ");
				}
				bf.append(" ");
				bf.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI_SUFFIX);
				
				validRuoli = sizeRuoli>0;
				
				if(validRuoli) {
					rowsToolTip++;
					if(bfToolTip.length()>0) {
						bfToolTip.append("\n");
					}
					bfToolTip.append("- ").append(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI_SUFFIX);
					bfToolTip.append(" (").append(sizeRuoli).append(")");
				}
				else {
					if(bfToolTipNotValid.length()>0) {
						bfToolTipNotValid.append("\n");
					}
					bfToolTipNotValid.append(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_PUNTUALE_NO_RUOLI);
				}
			}
			
			Boolean validScopes = null;
			if(gestioneToken!=null && StatoFunzionalita.ABILITATO.getValue().equals(gestioneToken)) {
				if(autorizzazioneScope!=null && StatoFunzionalita.ABILITATO.equals(autorizzazioneScope.getStato())) {
					if(bf.length()>0) {
						bf.append(",");
					}
					if(bf.length()<=0) {
						if(modipa && !portaDelegata) {
							bf.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CANALE);
						}
						else {
							bf.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE);
						}
						bf.append(" [ ");
					}
					bf.append(" ");
					bf.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_SCOPE_SUFFIX);
					
					validScopes = autorizzazioneScope.sizeScopeList()>0;
					
					if(validScopes) {
						rowsToolTip++;
						if(bfToolTip.length()>0) {
							bfToolTip.append("\n");
						}
						bfToolTip.append("- ").append(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_SCOPE_SUFFIX);
						bfToolTip.append(" (").append(autorizzazioneScope.sizeScopeList()).append(")");
					}
					else {
						if(bfToolTipNotValid.length()>0) {
							bfToolTipNotValid.append("\n");
						}
						bfToolTipNotValid.append(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_PUNTUALE_NO_SCOPE);
					}
				}
				if(gestioneTokenConfig!=null && gestioneTokenConfig.getOptions()!=null && !"".equals(gestioneTokenConfig.getOptions())) {
					if(bf.length()>0) {
						bf.append(",");
					}
					if(bf.length()<=0) {
						if(modipa && !portaDelegata) {
							bf.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CANALE);
						}
						else {
							bf.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE);
						}
						bf.append(" [ ");
					}
					bf.append(" ");
					bf.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_SUBTITLE_SUFFIX);
					
					rowsToolTip++;
					if(bfToolTip.length()>0) {
						bfToolTip.append("\n");
					}
					String [] tmp = gestioneTokenConfig.getOptions().split("\n");
					bfToolTip.append("- ").append(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_SUBTITLE_SUFFIX);
					bfToolTip.append(" (").append(tmp!=null ? tmp.length : 0).append(")");
				}
			}
			
			if(TipoAutorizzazione.isXacmlPolicyRequired(autorizzazione)) {
				if(bf.length()>0) {
					bf.append(",");
				}
				if(bf.length()<=0) {
					if(modipa && !portaDelegata) {
						bf.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CANALE);
					}
					else {
						bf.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE);
					}
					bf.append(" [ ");
				}
				bf.append(" ");
				bf.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_XACML_SUFFIX);
			}
			
			if(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM.equals(autorizzazione)) {
				if(bf.length()>0) {
					bf.append(",");
				}
				if(bf.length()<=0) {
					if(modipa && !portaDelegata) {
						bf.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CANALE);
					}
					else {
						bf.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE);
					}
					bf.append(" [ ");
				}
				bf.append(" ");
				bf.append(CostantiControlStation.DEFAULT_LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM).append(" '").append(autorizzazioneCustom).append("'");
				//bf.append(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM);
				bfToolTip.append(": ").append(autorizzazioneCustom);
			}
			
			if(bf.length()>0) {
								
				bf.append(" ]");
				
				CheckboxStatusType statusAutorizzazione = null;
				String tooltip = null;
				
				if(bfToolTipNotValid.length()>0) {
					statusAutorizzazione = CheckboxStatusType.CONFIG_ERROR;
					tooltip = bfToolTipNotValid.toString();
				}
				else {
					if(bfToolTip.length()>0) {
						if(modipa && !portaDelegata) {
							tooltip = CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CANALE;
						}
						else {
							tooltip = CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE;
						}
						if(rowsToolTip>1) {
							tooltip+="\n";
						}
						tooltip+=bfToolTip.toString();
					}
					
					statusAutorizzazione = CheckboxStatusType.CONFIG_ENABLE;
				}
				
				de.addStatus(tooltip, bf.toString(), statusAutorizzazione);
			}
		}
		
		if(modipa && !portaDelegata) {
			// autorizzazione sicurezza messaggio
		
			if(sizeApplicativi>0) {
				StringBuilder bf = new StringBuilder();
				StringBuilder bfToolTip = new StringBuilder();
				//bf.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE+" "+this.getProfiloModIPASectionTitle());
				//bf.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE+" "+this.getProfiloModIPASectionSicurezzaMessaggioSubTitle());
				bf.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_MESSAGGIO);
				bfToolTip.append(bf.toString());
				bfToolTip.append(" ")
				.append(" Applicativi").append(" (").append(sizeApplicativi).append(")");
				de.addStatus(bfToolTip.toString(), bf.toString(), CheckboxStatusType.CONFIG_ENABLE);
			}
			
		}
		
		// autorizzazione contenuti
		if(StringUtils.isNotEmpty(autorizzazioneContenuti)) {
			StringBuilder bf = new StringBuilder();
			StringBuilder bfToolTip = new StringBuilder();
			bf.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI);
			bfToolTip.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI);
			if(!CostantiAutorizzazione.AUTORIZZAZIONE_CONTENUTO_BUILT_IN.equals(autorizzazioneContenuti)) {
				//bf.append(" [ ").append(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM).append(" ]");
				bf.append(" [ ").append(CostantiControlStation.DEFAULT_LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM).append(" '").append(autorizzazioneContenuti).append("'").append(" ]");
				bfToolTip.append(": ").append(autorizzazioneContenuti);
			}
			de.addStatus(bfToolTip.toString(), bf.toString(), CheckboxStatusType.CONFIG_ENABLE);
		}
		
		if(de.getStatusValuesAsList()==null || de.getStatusValuesAsList().size()<=0) {
			de.addStatus(this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_DISABILITATO),CheckboxStatusType.CONFIG_DISABLE);
		}
		
	}
	
	public void setStatoRateLimiting(DataElement de, List<AttivazionePolicy> listaPolicy) throws DriverControlStationException, DriverControlStationNotFound {
		setStatoRateLimiting(de, listaPolicy, true);
	}
	public void setStatoRateLimiting(DataElement de, List<AttivazionePolicy> listaPolicy, boolean upperFirstChar) throws DriverControlStationException, DriverControlStationNotFound {
		de.setType(DataElementType.CHECKBOX);
		if(listaPolicy!=null && listaPolicy.size()>0) {
			Map<String, Integer> mapActive = new HashMap<>();
			Map<String, Integer> mapWarningOnly = new HashMap<>();
			for (AttivazionePolicy attivazionePolicy : listaPolicy) {
				if(attivazionePolicy.getEnabled()==false) {
					continue;
				}
				ConfigurazionePolicy policy = this.confCore.getConfigurazionePolicy(attivazionePolicy.getIdPolicy());
				String risorsa = policy.getRisorsa();
				boolean richiesteSimultanee = policy.isSimultanee();
				if(richiesteSimultanee) {
					risorsa = risorsa + "Simultanee";
				}
				
				Integer count = null;
				if(attivazionePolicy.isWarningOnly()) {
					if(mapWarningOnly.containsKey(risorsa)){
						count = mapWarningOnly.remove(risorsa);
					}
					else {
						count = 0;
					}
				}
				else {
					if(mapActive.containsKey(risorsa)){
						count = mapActive.remove(risorsa);
					}
					else {
						count = 0;
					}
				}
				count ++;
				if(attivazionePolicy.isWarningOnly()) {
					mapWarningOnly.put(risorsa, count);
				}
				else {
					mapActive.put(risorsa, count);	
				}
			}
			
			if(mapActive.size()>0 && mapWarningOnly.size()>0) {
				de.setType(DataElementType.MULTI_SELECT);
			}
			
			if(mapActive.size()>0 || mapWarningOnly.size()>0) {
				
				if(mapActive.size()>0) {
					StringBuilder bf = new StringBuilder();
					for (String risorsa : mapActive.keySet()) {
						Integer count = mapActive.get(risorsa);
						if(bf.length()>0) {
							bf.append(", ");
						}
						bf.append(risorsa);
						if(count>1) {
							bf.append("(").append(count).append(")");
						}
					}
					String vAbilitato = upperFirstChar ? this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_ABILITATO) : CostantiControlStation.DEFAULT_VALUE_ABILITATO;
					if(bf.length()>0 && bf.length()<CostantiControlStation.MAX_LENGTH_VALORE_STATO_RATE_LIMITING) {
						String value = vAbilitato+" [ "+bf.toString()+" ]";
						de.addStatus(value, CheckboxStatusType.CONFIG_ENABLE);
					}
					else {
						String value = vAbilitato;
						de.addStatus(bf.toString(), value, CheckboxStatusType.CONFIG_ENABLE);
					}
				} 
				
				if(mapWarningOnly.size()>0) {
					StringBuilder bf = new StringBuilder();
					for (String risorsa : mapWarningOnly.keySet()) {
						Integer count = mapWarningOnly.get(risorsa);
						if(bf.length()>0) {
							bf.append(", ");
						}
						bf.append(risorsa);
						if(count>1) {
							bf.append("(").append(count).append(")");
						}
					}
					String vWarningOnly = upperFirstChar ? this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_WARNING_ONLY) : CostantiControlStation.DEFAULT_VALUE_WARNING_ONLY;
					if(bf.length()>0 && bf.length()<CostantiControlStation.MAX_LENGTH_VALORE_STATO_RATE_LIMITING) {
						String value = vWarningOnly+" [ "+bf.toString()+" ]";
						de.addStatus(value, CheckboxStatusType.CONFIG_WARNING);
					}
					else {
						String value = vWarningOnly;
						de.addStatus(bf.toString(), value, CheckboxStatusType.CONFIG_WARNING);
					}
				} 
				
			}
			else {
				de.setStatusType(CheckboxStatusType.CONFIG_DISABLE);
				String vDisable = upperFirstChar ? this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_DISABILITATO) :  CostantiControlStation.DEFAULT_VALUE_DISABILITATO;
				de.setStatusValue(vDisable);
				de.setStatusToolTip("Sull'API sono registrate "+listaPolicy.size()+" politiche di Rate Limiting tutte con stato disabilitato");
			}
			
		}
		else {
			de.setStatusType(CheckboxStatusType.CONFIG_DISABLE);
			String vDisable = upperFirstChar ? this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_DISABILITATO) :  CostantiControlStation.DEFAULT_VALUE_DISABILITATO;
			de.setStatusValue(vDisable);
		}
	}
	
	public void setStatoValidazioneContenuti(DataElement de, ValidazioneContenutiApplicativi val, FormatoSpecifica formatoSpecifica) throws DriverControlStationException, DriverControlStationNotFound {
		de.setType(DataElementType.CHECKBOX);
		if(val!=null && !StatoFunzionalitaConWarning.DISABILITATO.equals(val.getStato())) {
			String valore = null;
			if(StatoFunzionalitaConWarning.ABILITATO.equals(val.getStato())) {
				de.setStatusType(CheckboxStatusType.CONFIG_ENABLE);
				valore = CostantiControlStation.DEFAULT_VALUE_ABILITATO;
			}
			else {
				de.setStatusType(CheckboxStatusType.CONFIG_WARNING);
				valore = CostantiControlStation.DEFAULT_VALUE_WARNING_ONLY;
			}
			String label = null;
			switch (val.getTipo()) {
			case INTERFACE:
				switch (formatoSpecifica) {
				case OPEN_API_3:
					label=CostantiControlStation.LABEL_PARAMETRO_INTERFACE_TYPE_OPEN_API_3;
					break;
				case SWAGGER_2:
					label=CostantiControlStation.LABEL_PARAMETRO_INTERFACE_TYPE_SWAGGER_2;
					break;
				case WADL:
					label=CostantiControlStation.LABEL_PARAMETRO_INTERFACE_TYPE_WADL;
					break;
				case WSDL_11:
					label=CostantiControlStation.LABEL_PARAMETRO_INTERFACE_TYPE_WSDL_11;
					break;
				}
				break;
			case XSD:
				label=CostantiControlStation.LABEL_PARAMETRO_SCHEMI_XSD;
				break;
			case OPENSPCOOP:
				label=CostantiControlStation.LABEL_PARAMETRO_REGISTRO_OPENSPCOOP;
				break;
			}
			de.setStatusValue(this.getUpperFirstChar(valore)+" [ "+label+" ]");
		}
		else {
			de.setStatusType(CheckboxStatusType.CONFIG_DISABLE);
			de.setStatusValue(this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_DISABILITATO));
		}
	}
	
	public void setStatoGestioneCORS(DataElement de, CorsConfigurazione ccPorta, Configurazione configurazioneGenerale) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		
		de.setType(DataElementType.CHECKBOX);
		
		CheckboxStatusType statusType = null;
		String statusValue = null;
		String statusTooltip = null;
		
		if (ccPorta == null) {
			
			CorsConfigurazione cc = configurazioneGenerale.getGestioneCors();
			if(cc==null || cc.getStato()==null || StatoFunzionalita.DISABILITATO.equals(cc.getStato())) {
				statusType = CheckboxStatusType.CONFIG_DISABLE;
				statusValue = this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_DISABILITATO);
			}
			else {
				statusType = CheckboxStatusType.CONFIG_ENABLE;
				if(TipoGestioneCORS.GATEWAY.equals(cc.getTipo())) {
					statusValue = this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_ABILITATO);
				}
				else {
					statusValue = this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_ABILITATO)+" [ "+CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_TIPO_GESTITO_APPLICATIVO_DEMANDATO+" ]";
				}
			}
			
			statusTooltip = CostantiControlStation.LABEL_CONFIGURAZIONE_DEFAULT;
			
		} else {
			
			if(ccPorta.getStato()==null || StatoFunzionalita.DISABILITATO.equals(ccPorta.getStato())) {
				statusType = CheckboxStatusType.CONFIG_DISABLE;
				statusValue = this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_DISABILITATO);
			}
			else {
				statusType = CheckboxStatusType.CONFIG_ENABLE;
				if(TipoGestioneCORS.GATEWAY.equals(ccPorta.getTipo())) {
					statusValue = this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_ABILITATO);
				}
				else {
					statusValue = this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_ABILITATO)+" [ "+CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_TIPO_GESTITO_APPLICATIVO_DEMANDATO+" ]";
				}
			}
			
			statusTooltip = CostantiControlStation.LABEL_CONFIGURAZIONE_RIDEFINITA;
		}
		
		de.setStatusType(statusType);
		de.setStatusValue(statusValue);
		de.setStatusToolTip(statusTooltip);
	}

	public void setStatoCanale(DataElement de, String canaleNome, List<CanaleConfigurazione> canaleList) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		de.setType(DataElementType.TEXT);
		
		String canaleTooltip;
		if(canaleNome == null) { // default
			CanaleConfigurazione canaleConfigurazioneDefault = canaleList.stream().filter((c) -> c.isCanaleDefault()).findFirst().get();
			canaleNome =  canaleConfigurazioneDefault.getNome();
			canaleTooltip = CostantiControlStation.LABEL_CONFIGURAZIONE_CANALE_DEFAULT;
		} else {
			canaleTooltip = CostantiControlStation.LABEL_CONFIGURAZIONE_CANALE_RIDEFINITO_API;
		}
		
		de.setValue(canaleNome);
		de.setToolTip(canaleTooltip);
	}
	public void setStatoCanalePorta(DataElement de, String canaleNome, String canaleAPINome,  List<CanaleConfigurazione> canaleList, boolean erogazione) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		
		de.setType(DataElementType.TEXT);
		
		String canaleTooltip;
		if(canaleNome == null) { // default
			if(canaleAPINome == null) { // default sistema
				CanaleConfigurazione canaleConfigurazioneDefault = canaleList.stream().filter((c) -> c.isCanaleDefault()).findFirst().get();
				canaleNome =  canaleConfigurazioneDefault.getNome();
				canaleTooltip = CostantiControlStation.LABEL_CONFIGURAZIONE_CANALE_DEFAULT;	
			} else { // default API
				canaleNome = canaleAPINome;
				canaleTooltip = CostantiControlStation.LABEL_CONFIGURAZIONE_CANALE_DEFAULT_API;
			}
		} else {
			if(erogazione) {
				canaleTooltip = CostantiControlStation.LABEL_CONFIGURAZIONE_CANALE_RIDEFINITO_EROGAZIONE;
			}
			else {
				canaleTooltip = CostantiControlStation.LABEL_CONFIGURAZIONE_CANALE_RIDEFINITO_FRUIZIONE;
			}
		}
		
		de.setValue(canaleNome);
		de.setToolTip(canaleTooltip);
	}
	
	public void setStatoAllarmi(DataElement de, List<ConfigurazioneAllarmeBean> listaAllarmi) throws DriverControlStationException, DriverControlStationNotFound, UtilsException {
		if(this.confCore.isShowAllarmiElenchiStatiAllarmi()) {
			setStatoAllarmi_showStati(de, listaAllarmi);
		}
		else {
			setStatoAllarmi_showTipi(de, listaAllarmi);
		}
	}
	public void setStatoAllarmi_showStati(DataElement de, List<ConfigurazioneAllarmeBean> listaAllarmi) throws DriverControlStationException, DriverControlStationNotFound {
		
		de.setType(DataElementType.CHECKBOX);
		if(listaAllarmi!=null && listaAllarmi.size()>0) {
			Integer countOk = 0;
			Integer countError = 0;
			Integer countWarn = 0;
			
			for (ConfigurazioneAllarmeBean allarme : listaAllarmi) {
				if(allarme.getEnabled() == 0) {// skip dei disabilitati
					continue;
				}
				
				StatoAllarme statoAllarme = AllarmiConverterUtils.toStatoAllarme(allarme.getStato());
				switch (statoAllarme) {
				case OK:
					countOk ++;
					break;
				case WARNING:
					countWarn ++;
					break;
				case ERROR:
					countError ++;
					break;
				}
			}
			
			int multi = 0;
			
			if(countOk > 0)
				multi++;
			
			if(countError > 0)
				multi++;
			
			if(countWarn > 0)
				multi++;
			
			if(multi > 1) {
				de.setType(DataElementType.MULTI_SELECT);
			}
			
			if(countOk > 0 || countError > 0 || countWarn > 0) {
				
				if(countOk > 0) {
					StringBuilder bf = new StringBuilder();
					bf.append(ConfigurazioneCostanti.CONFIGURAZIONE_ALLARME_LABEL_STATO_OK);
					bf.append(" (").append(countOk).append(")");
					de.addStatus(bf.toString(), CheckboxStatusType.CONFIG_ENABLE);
				}
				
				if(countWarn > 0) {
					StringBuilder bf = new StringBuilder();
					bf.append(ConfigurazioneCostanti.CONFIGURAZIONE_ALLARME_LABEL_STATO_WARNING);
					bf.append(" (").append(countWarn).append(")");
					de.addStatus(bf.toString(), CheckboxStatusType.CONFIG_WARNING);
				}
				
				if(countError > 0) {
					StringBuilder bf = new StringBuilder();
					bf.append(ConfigurazioneCostanti.CONFIGURAZIONE_ALLARME_LABEL_STATO_ERROR);
					bf.append(" (").append(countError).append(")");
					de.addStatus(bf.toString(), CheckboxStatusType.CONFIG_ERROR);
				}
			}
			else {
				de.setStatusType(CheckboxStatusType.CONFIG_DISABLE);
				de.setStatusValue(this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_DISABILITATO));
				de.setStatusToolTip("Sull'API sono registrati "+listaAllarmi.size()+" allarmi tutti con stato disabilitato");
			}
			
		}
		else {
			de.setStatusType(CheckboxStatusType.CONFIG_DISABLE);
			de.setStatusValue(this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_DISABILITATO));
		}
	}
	public void setStatoAllarmi_showTipi(DataElement de, List<ConfigurazioneAllarmeBean> listaAllarmi) throws DriverControlStationException, DriverControlStationNotFound {
		de.setType(DataElementType.CHECKBOX);
		if(listaAllarmi!=null && listaAllarmi.size()>0) {
			Map<String, Integer> mapActive = new HashMap<>();
			for (ConfigurazioneAllarmeBean allarme : listaAllarmi) {
				if(allarme.getEnabled().intValue()==0) {
					continue;
				}
				//String tipoPlugin = allarme.getPlugin().getLabel();
				String tipoPlugin = allarme.getPlugin().getTipo(); // più compatto
				
				Integer count = null;
				if(mapActive.containsKey(tipoPlugin)){
					count = mapActive.remove(tipoPlugin);
				}
				else {
					count = 0;
				}
				count ++;
				mapActive.put(tipoPlugin, count);	
			}
			
			if(mapActive.size()>0) {
				
				if(mapActive.size()>0) {
					StringBuilder bf = new StringBuilder();
					for (String risorsa : mapActive.keySet()) {
						Integer count = mapActive.get(risorsa);
						if(bf.length()>0) {
							bf.append(", ");
						}
						bf.append(risorsa);
						if(count>1) {
							bf.append("(").append(count).append(")");
						}
					}
					if(bf.length()>0 && bf.length()<CostantiControlStation.MAX_LENGTH_VALORE_STATO_ALLARMI) {
						String value = this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_ABILITATO)+" [ "+bf.toString()+" ]";
						de.addStatus(value, CheckboxStatusType.CONFIG_ENABLE);
					}
					else {
						String value = this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_ABILITATO);
						de.addStatus(bf.toString(), value, CheckboxStatusType.CONFIG_ENABLE);
					}
				} 
				
			}
			else {
				de.setStatusType(CheckboxStatusType.CONFIG_DISABLE);
				de.setStatusValue(this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_DISABILITATO));
				de.setStatusToolTip("Sull'API sono registrati "+listaAllarmi.size()+" allarmi tutti con stato disabilitato");
			}
			
		}
		else {
			de.setStatusType(CheckboxStatusType.CONFIG_DISABLE);
			de.setStatusValue(this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_DISABILITATO));
		}
	}
	
	
	public void setStatoOpzioniAvanzatePortaDelegataDefault(DataElement de, String options) throws Exception {
		this._setStatoOpzioniAvanzatePortaDefault(de, options);
	}
	public void setStatoOpzioniAvanzatePortaApplicativaDefault(DataElement de, String options) throws Exception {
		this._setStatoOpzioniAvanzatePortaDefault(de, options);
	}
	private void _setStatoOpzioniAvanzatePortaDefault(DataElement de, String options) throws Exception {
		
		de.setType(DataElementType.CHECKBOX);
		
		Map<String, List<String>> props = PropertiesSerializator.convertoFromDBColumnValue(options);
		StringBuilder bf = new StringBuilder();
		StringBuilder bfTooltip = new StringBuilder();
		if(props!=null && props.size()>0) {
			for (String key : props.keySet()) {
				List<String> values = props.get(key);
				if(values!=null && !values.isEmpty()) {
					for (String value : values) {
						if(bf.length()>0) {
							bf.append(", ");
						}
						bf.append(key);
						
						if(bfTooltip.length()>0) {
							bfTooltip.append(", ");
						}
						bfTooltip.append(key);
						bfTooltip.append(" '");
						bfTooltip.append(value);
						bfTooltip.append("'");		
					}
				}
			}
		}
		
		
		if(bf.length()>0) {
			de.setStatusType(CheckboxStatusType.CONFIG_ENABLE);
			de.setStatusValue(this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_ABILITATO)+" [ "+bf.toString()+" ]");
			de.setStatusToolTip(bfTooltip.toString());
		}
		else {
			de.setStatusType(CheckboxStatusType.CONFIG_DISABLE);
			de.setStatusValue(this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_DISABILITATO));
		}
	}
	
	public void setStatoCachingRisposta(DataElement de, ResponseCachingConfigurazione rcPorta, Configurazione configurazioneGenerale) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		
		de.setType(DataElementType.CHECKBOX);
		
		CheckboxStatusType statusType = null;
		String statusValue = null;
		String statusTooltip = null;
		
		if (rcPorta == null) {
			
			ResponseCachingConfigurazioneGenerale rg = configurazioneGenerale.getResponseCaching();
			if(rg==null || rg.getConfigurazione()==null || rg.getConfigurazione().getStato()==null || 
					StatoFunzionalita.DISABILITATO.equals(rg.getConfigurazione().getStato())) {
				statusType = CheckboxStatusType.CONFIG_DISABLE;
				statusValue = this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_DISABILITATO);
			}
			else {
				statusType = CheckboxStatusType.CONFIG_ENABLE;
				statusValue = this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_ABILITATO);
			}
			
			statusTooltip = CostantiControlStation.LABEL_CONFIGURAZIONE_DEFAULT;
			
		} else {
			
			if(rcPorta.getStato()==null || StatoFunzionalita.DISABILITATO.equals(rcPorta.getStato()) ) {
				statusType = CheckboxStatusType.CONFIG_DISABLE;
				statusValue = this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_DISABILITATO);
			}
			else {
				statusType = CheckboxStatusType.CONFIG_ENABLE;
				statusValue = this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_ABILITATO);
			}
			
			statusTooltip = CostantiControlStation.LABEL_CONFIGURAZIONE_RIDEFINITA;
		}
		
		de.setStatusType(statusType);
		de.setStatusValue(statusValue);
		de.setStatusToolTip(statusTooltip);
	}
	
	public void setStatoSicurezzaMessaggio(DataElement de, MessageSecurity securityPorta, 
			ConfigManager configManager, PropertiesSourceConfiguration propertiesSourceConfiguration) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		setStatoSicurezzaMessaggio(de, securityPorta, 
				configManager, propertiesSourceConfiguration, true);
	}
	public void setStatoSicurezzaMessaggio(DataElement de, MessageSecurity securityPorta, 
			ConfigManager configManager, PropertiesSourceConfiguration propertiesSourceConfiguration, boolean upperFirstChar) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		
		boolean request = (
				securityPorta!=null &&
				securityPorta.getRequestFlow()!=null && 
				securityPorta.getRequestFlow().getMode()!=null &&
				!"".equals(securityPorta.getRequestFlow().getMode()) &&
				!CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO.equals( securityPorta.getRequestFlow().getMode())
				);
		
		boolean response = (
				securityPorta!=null &&
				securityPorta.getResponseFlow()!=null &&
				securityPorta.getResponseFlow().getMode()!=null &&
				!"".equals(securityPorta.getResponseFlow().getMode()) &&
				!CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO.equals( securityPorta.getResponseFlow().getMode())
				);
		
		if (securityPorta == null || (!request && !response) ) {
			de.setType(DataElementType.CHECKBOX);
			de.setStatusType(CheckboxStatusType.CONFIG_DISABLE);
			String value = upperFirstChar ? this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_DISABILITATO) : CostantiControlStation.DEFAULT_VALUE_DISABILITATO; 
			de.setStatusValue(value);		
		} else {
			
			de.setType(DataElementType.MULTI_SELECT);
			
			if(request) {
				CheckboxStatusType type = securityPorta.getRequestFlow().sizeParameterList()>0 ? CheckboxStatusType.CONFIG_ENABLE : CheckboxStatusType.CONFIG_ERROR;
				String tooltip = securityPorta.getRequestFlow().sizeParameterList()>0 ? null : CostantiControlStation.LABEL_CONFIGURAZIONE_INCOMPLETA;
				String label = null;
				if(CostantiControlStation.VALUE_PARAMETRO_PROPERTIES_MODE_DEFAULT.equals( securityPorta.getRequestFlow().getMode())) {
					label = CostantiControlStation.LABEL_CONFIGURAZIONE_PROPERTIES_CONFIGURAZIONE_MANUALE;
				}
				else {
					List<String> nome =new ArrayList<>();
					nome.add(securityPorta.getRequestFlow().getMode());
					List<String> labelConfigurazione = configManager.convertToLabel(propertiesSourceConfiguration, nome);
					label = labelConfigurazione.get(0);
				}
				String value = (upperFirstChar ? CostantiControlStation.LABEL_PARAMETRO_RICHIESTA : CostantiControlStation.LABEL_PARAMETRO_RICHIESTA.toLowerCase())+" [ "+label+" ]";
				de.addStatus(tooltip, value, type);
			}
			
			if(response) {
				CheckboxStatusType type = securityPorta.getResponseFlow().sizeParameterList()>0 ? CheckboxStatusType.CONFIG_ENABLE : CheckboxStatusType.CONFIG_ERROR;
				String tooltip = securityPorta.getResponseFlow().sizeParameterList()>0 ? null : CostantiControlStation.LABEL_CONFIGURAZIONE_INCOMPLETA;
				String label = null;
				if(CostantiControlStation.VALUE_PARAMETRO_PROPERTIES_MODE_DEFAULT.equals( securityPorta.getResponseFlow().getMode())) {
					label = CostantiControlStation.LABEL_CONFIGURAZIONE_PROPERTIES_CONFIGURAZIONE_MANUALE;
				}
				else {
					List<String> nome =new ArrayList<>();
					nome.add(securityPorta.getResponseFlow().getMode());
					List<String> labelConfigurazione = configManager.convertToLabel(propertiesSourceConfiguration, nome);
					label = labelConfigurazione.get(0);
				}
				String value = (upperFirstChar ? CostantiControlStation.LABEL_PARAMETRO_RISPOSTA : CostantiControlStation.LABEL_PARAMETRO_RISPOSTA.toLowerCase())+" [ "+label+" ]";
				de.addStatus(tooltip, value, type);
			}
		}

	}
	
	public void setStatoMTOM(DataElement de, MtomProcessor mtomPorta) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		setStatoMTOM(de, mtomPorta, true);
	}
	public void setStatoMTOM(DataElement de, MtomProcessor mtomPorta, boolean upperFirstChar) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		
		boolean request = false;
		boolean response= false;
		if(mtomPorta!= null){
			if(mtomPorta.getRequestFlow() != null){
				if(mtomPorta.getRequestFlow().getMode() != null){
					MTOMProcessorType mode = mtomPorta.getRequestFlow().getMode();
					if(!mode.equals(MTOMProcessorType.DISABLE))
						request = true;
				}
			}

			if(mtomPorta.getResponseFlow() != null){
				if(mtomPorta.getResponseFlow().getMode() != null){
					MTOMProcessorType mode = mtomPorta.getResponseFlow().getMode();
					if(!mode.equals(MTOMProcessorType.DISABLE))
						response = true;
				}
			}
		}
		
		if (mtomPorta == null || (!request && !response) ) {
			de.setType(DataElementType.CHECKBOX);
			de.setStatusType(CheckboxStatusType.CONFIG_DISABLE);
			String vUpper = upperFirstChar ? this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_DISABILITATO) :  CostantiControlStation.DEFAULT_VALUE_DISABILITATO; 
			de.setStatusValue(vUpper);		
		} else {
			
			de.setType(DataElementType.MULTI_SELECT);
			
			if(request) {
				
				CheckboxStatusType type = null;
				String tooltip =  null;
				switch (mtomPorta.getRequestFlow().getMode()) {
				case PACKAGING:
				case VERIFY:	
					type = mtomPorta.getRequestFlow().sizeParameterList()>0 ? CheckboxStatusType.CONFIG_ENABLE : CheckboxStatusType.CONFIG_ERROR;
					tooltip = mtomPorta.getRequestFlow().sizeParameterList()>0 ? null : CostantiControlStation.LABEL_CONFIGURAZIONE_MTOM_INCOMPLETA;
					break;
				default:
					type = CheckboxStatusType.CONFIG_ENABLE;
					break;
				}
				String v = mtomPorta.getRequestFlow().getMode().getValue();
				String value = (upperFirstChar ? CostantiControlStation.LABEL_PARAMETRO_RICHIESTA : CostantiControlStation.LABEL_PARAMETRO_RICHIESTA.toLowerCase())+" [ "+this.getUpperFirstChar(v)+" ]";
				de.addStatus(tooltip, value, type);
			}
			
			if(response) {
				
				CheckboxStatusType type = null;
				String tooltip =  null;
				switch (mtomPorta.getResponseFlow().getMode()) {
				case PACKAGING:
				case VERIFY:	
					type = mtomPorta.getResponseFlow().sizeParameterList()>0 ? CheckboxStatusType.CONFIG_ENABLE : CheckboxStatusType.CONFIG_ERROR;
					tooltip = mtomPorta.getResponseFlow().sizeParameterList()>0 ? null : CostantiControlStation.LABEL_CONFIGURAZIONE_MTOM_INCOMPLETA;
					break;
				default:
					type = CheckboxStatusType.CONFIG_ENABLE;
					break;
				}
				String v = mtomPorta.getResponseFlow().getMode().getValue();
				String value = (upperFirstChar ? CostantiControlStation.LABEL_PARAMETRO_RISPOSTA : CostantiControlStation.LABEL_PARAMETRO_RISPOSTA.toLowerCase())+" [ "+this.getUpperFirstChar(v)+" ]";
				de.addStatus(tooltip, value, type);
			}
			
		}
		
	}
	
	public void setStatoTrasformazioni(DataElement de, Trasformazioni trasformazioni, ServiceBinding serviceBindingMessage) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		setStatoTrasformazioni(de, trasformazioni, serviceBindingMessage, true);
	}
	public void setStatoTrasformazioni(DataElement de, Trasformazioni trasformazioni, ServiceBinding serviceBindingMessage, boolean upperFirstChar) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		
		de.setType(DataElementType.CHECKBOX);
		
		List<TrasformazioneRegola> listaTrasformazioni = null;
		if(trasformazioni!=null) {
			listaTrasformazioni = trasformazioni.getRegolaList();
		}
		if(listaTrasformazioni==null || listaTrasformazioni.size()<=0) {
			de.setStatusType(CheckboxStatusType.CONFIG_DISABLE);
			String vDisabilitato = upperFirstChar ? this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_DISABILITATO) :  CostantiControlStation.DEFAULT_VALUE_DISABILITATO;
			de.setStatusValue(vDisabilitato);		
		}
		else {
			StringBuilder bfToolTip = new StringBuilder();
			CheckboxStatusType type = null;
			
			int regoleAbilitate = 0;
			int regoleDisabilitate = 0;
			
			for (TrasformazioneRegola trasformazioneRegola : listaTrasformazioni) {
								
				if(trasformazioneRegola.getStato()!=null // backward compatibility 
						&&
						StatoFunzionalita.DISABILITATO.equals(trasformazioneRegola.getStato())){
					regoleDisabilitate++;
					continue;
				}
				else {
					regoleAbilitate++;
				}
				
				boolean richiestaDefinita = false;
				if(trasformazioneRegola.getRichiesta()!=null) {
					richiestaDefinita = trasformazioneRegola.getRichiesta().isConversione() || 
							trasformazioneRegola.getRichiesta().sizeHeaderList()>0 ||
							trasformazioneRegola.getRichiesta().sizeParametroUrlList()>0 ||
							(
									ServiceBinding.REST.equals(serviceBindingMessage) && 
									trasformazioneRegola.getRichiesta().getTrasformazioneRest()!=null &&
									(
										StringUtils.isNotEmpty(trasformazioneRegola.getRichiesta().getTrasformazioneRest().getMetodo())
										||
										StringUtils.isNotEmpty(trasformazioneRegola.getRichiesta().getTrasformazioneRest().getPath())
									)
							)
						;
				}
				
				if(trasformazioneRegola.sizeRispostaList()>0) {
					for (TrasformazioneRegolaRisposta trasformazioneRegolaRisposta : trasformazioneRegola.getRispostaList()) {
						boolean rispostaDefinita = false;
						if(trasformazioneRegolaRisposta!=null) {
							rispostaDefinita = trasformazioneRegolaRisposta.isConversione() || 
									trasformazioneRegolaRisposta.sizeHeaderList()>0 ||
									(trasformazioneRegolaRisposta.getReturnCode()!=null && trasformazioneRegolaRisposta.getReturnCode()>199);
						}
						if(!rispostaDefinita) {
							type = CheckboxStatusType.CONFIG_ERROR;
							if(bfToolTip.length()>0) {
								bfToolTip.append("\n");
							}
							bfToolTip.append("La regola '"+trasformazioneRegola.getNome()+"' possiede una configurazione per la risposta ('"+trasformazioneRegolaRisposta.getNome()+"') senza alcuna trasformazione attiva");
							break;
						}
					}				
				}
				else if(!richiestaDefinita) {
					type = CheckboxStatusType.CONFIG_ERROR;
					if(bfToolTip.length()>0) {
						bfToolTip.append("\n");
					}	
					bfToolTip.append("La regola '"+trasformazioneRegola.getNome()+"' non effettua trasformazioni nè della richiesta nè della risposta");
				}
				
			}
			
			if(regoleAbilitate==0) {
				de.setStatusType(CheckboxStatusType.CONFIG_DISABLE);
				String vDisabilitato = upperFirstChar ? this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_DISABILITATO) :  CostantiControlStation.DEFAULT_VALUE_DISABILITATO;
				de.setStatusValue(vDisabilitato);	
				if(regoleDisabilitate>0) {
					de.setStatusToolTip("Sono registrate, con stato disabilitato, "+regoleDisabilitate+" regole di trasformazione dei messaggi");
				}
			}
			else {
			
				if(type==null) {
					type = CheckboxStatusType.CONFIG_ENABLE;
				}
				de.setStatusType(type);
				String vAbilitato = upperFirstChar ? this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_ABILITATO) :  CostantiControlStation.DEFAULT_VALUE_ABILITATO;
				de.setStatusValue(vAbilitato);
				if(bfToolTip.length()>0) {
					de.setStatusToolTip(bfToolTip.toString());
				}
				else {
					if(listaTrasformazioni.size()>1) {
						de.setStatusToolTip("Sono attive "+regoleAbilitate+" regole di trasformazione dei messaggi");
					}
				}
				
			}
		}
	}
	
	public void setStatoTracciamento(DataElement de, 
			CorrelazioneApplicativa correlazioneApplicativa,
			CorrelazioneApplicativaRisposta correlazioneApplicativaRisposta,
			PortaTracciamento tracciamentoConfig, Configurazione configurazioneGenerale) throws Exception {
		
		de.setType(DataElementType.MULTI_SELECT);
				
		String tooltipTransazioni = null;
		boolean transazioni = false;
		if(tracciamentoConfig!=null && tracciamentoConfig.getEsiti()!=null) {
			tooltipTransazioni = CostantiControlStation.LABEL_CONFIGURAZIONE_RIDEFINITA;
			transazioni = !(EsitiConfigUtils.TUTTI_ESITI_DISABILITATI+"").equals(tracciamentoConfig.getEsiti());
		}
		else {
			tooltipTransazioni = CostantiControlStation.LABEL_CONFIGURAZIONE_DEFAULT;
			String esitiTransazioni = this.readConfigurazioneRegistrazioneEsitiFromHttpParameters((configurazioneGenerale!=null && configurazioneGenerale.getTracciamento()!=null) ? configurazioneGenerale.getTracciamento().getEsiti() : null , true);
			transazioni = (
					esitiTransazioni!=null 
					&& 
					!(EsitiConfigUtils.TUTTI_ESITI_DISABILITATI+"").equals(esitiTransazioni)
			);
		}
		de.addStatus(tooltipTransazioni, 
				ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_TRANSAZIONI, 
				transazioni? CheckboxStatusType.CONFIG_ENABLE :CheckboxStatusType.CONFIG_DISABLE);
		
		
		String tooltipSeverita = null;
		boolean tracciamentoSeverita = false;
		if(tracciamentoConfig!=null && tracciamentoConfig.getSeverita()!=null) {
			tooltipSeverita = CostantiControlStation.LABEL_CONFIGURAZIONE_RIDEFINITA+"\nLivello Severità: "+tracciamentoConfig.getSeverita().getValue();
			tracciamentoSeverita = (!LogLevels.LIVELLO_OFF.equals(tracciamentoConfig.getSeverita().getValue()));
		}
		else {
			tooltipSeverita = CostantiControlStation.LABEL_CONFIGURAZIONE_DEFAULT;
			if(configurazioneGenerale!=null && configurazioneGenerale.getMessaggiDiagnostici()!=null && configurazioneGenerale.getMessaggiDiagnostici().getSeverita()!=null) {
				tooltipSeverita = tooltipSeverita+"\nLivello Severità: "+configurazioneGenerale.getMessaggiDiagnostici().getSeverita().getValue();
			}
			tracciamentoSeverita = (
					configurazioneGenerale!=null && configurazioneGenerale.getMessaggiDiagnostici()!=null &&
							configurazioneGenerale.getMessaggiDiagnostici().getSeverita()!=null 
					&& 
					!(LogLevels.LIVELLO_OFF.equals(configurazioneGenerale.getMessaggiDiagnostici().getSeverita().getValue()))
					);
		}
		de.addStatus(tooltipSeverita, 
				ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_DIAGNOSTICI, 
				tracciamentoSeverita? CheckboxStatusType.CONFIG_ENABLE :CheckboxStatusType.CONFIG_DISABLE);
			
		boolean isCorrelazioneApplicativaAbilitataReq = false;
		if (correlazioneApplicativa != null)
			isCorrelazioneApplicativaAbilitataReq = correlazioneApplicativa.sizeElementoList() > 0;
		if(isCorrelazioneApplicativaAbilitataReq) {
			de.addStatus(correlazioneApplicativa.sizeElementoList() > 1 ? "Sono attive "+correlazioneApplicativa.sizeElementoList()+" regole" : null, 
					CostantiControlStation.LABEL_PARAMETRO_RICHIESTA+" [ "+CostantiControlStation.LABEL_PARAMETRO_CORRELAZIONE_APPLICATIVA+" ]", 
					CheckboxStatusType.CONFIG_ENABLE);
		}
			
		boolean isCorrelazioneApplicativaAbilitataRes = false;
		if (correlazioneApplicativaRisposta != null)
			isCorrelazioneApplicativaAbilitataRes = correlazioneApplicativaRisposta.sizeElementoList() > 0;
		if(isCorrelazioneApplicativaAbilitataRes) {
			de.addStatus(correlazioneApplicativaRisposta.sizeElementoList() > 1 ? "Sono attive "+correlazioneApplicativaRisposta.sizeElementoList()+" regole" : null, 
					CostantiControlStation.LABEL_PARAMETRO_RISPOSTA+" [ "+CostantiControlStation.LABEL_PARAMETRO_CORRELAZIONE_APPLICATIVA+" ]", 
					CheckboxStatusType.CONFIG_ENABLE);
		}
		
	}
	
	public void setStatoDump(DataElement de, DumpConfigurazione dumpConfigurazionePorta, Configurazione configurazioneGenerale, boolean portaApplicativa) {
		setStatoDump(de, dumpConfigurazionePorta, configurazioneGenerale, portaApplicativa, true);
	}
	public void setStatoDump(DataElement de, DumpConfigurazione dumpConfigurazionePorta, Configurazione configurazioneGenerale, boolean portaApplicativa, boolean upperFirstChar) {
		
		de.setType(DataElementType.MULTI_SELECT);
		
		DumpConfigurazione dumpConfigurazione = null;
		
		String tooltip = null;
		if(dumpConfigurazionePorta!=null) {
			tooltip = CostantiControlStation.LABEL_CONFIGURAZIONE_RIDEFINITA;
			dumpConfigurazione = dumpConfigurazionePorta;
		}
		else {
			tooltip = CostantiControlStation.LABEL_CONFIGURAZIONE_DEFAULT;
			if(configurazioneGenerale!=null && configurazioneGenerale.getDump()!=null) {
				if(portaApplicativa) {
					dumpConfigurazione = configurazioneGenerale.getDump().getConfigurazionePortaApplicativa();
				}
				else {
					dumpConfigurazione = configurazioneGenerale.getDump().getConfigurazionePortaDelegata();
				}
			}
		}

		StringBuilder bfRichiesta = new StringBuilder();
		StringBuilder bfRichiestaOptions = new StringBuilder();
		StringBuilder bfRisposta = new StringBuilder();
		StringBuilder bfRispostaOptions = new StringBuilder();
		
		if(dumpConfigurazione!=null) {
					
			if(dumpConfigurazione.getRichiestaIngresso()!=null) {
				StringBuilder bf = new StringBuilder();
				if(StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRichiestaIngresso().getHeaders())) {
					if(bf.length()>0) {
						bf.append(", ");
					}
					bf.append(CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_HEADERS);
				}
				if(StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRichiestaIngresso().getPayload())) {
					if(StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRichiestaIngresso().getPayloadParsing())) {
						if(StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRichiestaIngresso().getBody())) {
							if(bf.length()>0) {
								bf.append(", ");
							}
							bf.append(CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_BODY);
						}
						if(StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRichiestaIngresso().getAttachments())) {
							if(bf.length()>0) {
								bf.append(", ");
							}
							bf.append(CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_ATTACHMENTS);
						}		
					}
					else {
						if(bf.length()>0) {
							bf.append(", ");
						}
						bf.append(CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_PAYLOAD);
					}
				}
				if(bf.length()>0) {
					if(bfRichiesta.length()>0) {
						bfRichiesta.append("\n");
					}
					bfRichiesta.append(CostantiControlStation.LABEL_PARAMETRO_RICHIESTA+" "+CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_INGRESSO).append(": ").append(bf.toString());
					
					if(bfRichiestaOptions.length()>0) {
						bfRichiestaOptions.append(", ");
					}
					bfRichiestaOptions.append(CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_INGRESSO);
				}
			}
			
			if(dumpConfigurazione.getRichiestaUscita()!=null) {
				StringBuilder bf = new StringBuilder();
				if(StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRichiestaUscita().getHeaders())) {
					if(bf.length()>0) {
						bf.append(", ");
					}
					bf.append(CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_USCITA_HEADERS);
				}
				if(StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRichiestaUscita().getPayload())) {
					if(StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRichiestaUscita().getPayloadParsing())) {
						if(StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRichiestaUscita().getBody())) {
							if(bf.length()>0) {
								bf.append(", ");
							}
							bf.append(CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_USCITA_BODY);
						}
						if(StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRichiestaUscita().getAttachments())) {
							if(bf.length()>0) {
								bf.append(", ");
							}
							bf.append(CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_USCITA_ATTACHMENTS);
						}		
					}
					else {
						if(bf.length()>0) {
							bf.append(", ");
						}
						bf.append(CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_USCITA_PAYLOAD);
					}
				}
				if(bf.length()>0) {
					if(bfRichiesta.length()>0) {
						bfRichiesta.append("\n");
					}
					bfRichiesta.append(CostantiControlStation.LABEL_PARAMETRO_RICHIESTA+" "+CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_USCITA).append(": ").append(bf.toString());
					
					if(bfRichiestaOptions.length()>0) {
						bfRichiestaOptions.append(", ");
					}
					bfRichiestaOptions.append(CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_USCITA);
				}
			}
			
			if(dumpConfigurazione.getRispostaIngresso()!=null) {
				StringBuilder bf = new StringBuilder();
				if(StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRispostaIngresso().getHeaders())) {
					if(bf.length()>0) {
						bf.append(", ");
					}
					bf.append(CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_INGRESSO_HEADERS);
				}
				if(StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRispostaIngresso().getPayload())) {
					if(StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRispostaIngresso().getPayloadParsing())) {
						if(StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRispostaIngresso().getBody())) {
							if(bf.length()>0) {
								bf.append(", ");
							}
							bf.append(CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_INGRESSO_BODY);
						}
						if(StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRispostaIngresso().getAttachments())) {
							if(bf.length()>0) {
								bf.append(", ");
							}
							bf.append(CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_INGRESSO_ATTACHMENTS);
						}		
					}
					else {
						if(bf.length()>0) {
							bf.append(", ");
						}
						bf.append(CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_INGRESSO_PAYLOAD);
					}
				}
				if(bf.length()>0) {
					if(bfRisposta.length()>0) {
						bfRisposta.append("\n");
					}
					bfRisposta.append(CostantiControlStation.LABEL_PARAMETRO_RISPOSTA+" "+CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_INGRESSO).append(": ").append(bf.toString());
					
					if(bfRispostaOptions.length()>0) {
						bfRispostaOptions.append(", ");
					}
					bfRispostaOptions.append(CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_INGRESSO);
				}
			}
			
			if(dumpConfigurazione.getRispostaUscita()!=null) {
				StringBuilder bf = new StringBuilder();
				if(StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRispostaUscita().getHeaders())) {
					if(bf.length()>0) {
						bf.append(", ");
					}
					bf.append(CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_USCITA_HEADERS);
				}
				if(StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRispostaUscita().getPayload())) {
					if(StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRispostaUscita().getPayloadParsing())) {
						if(StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRispostaUscita().getBody())) {
							if(bf.length()>0) {
								bf.append(", ");
							}
							bf.append(CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_USCITA_BODY);
						}
						if(StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRispostaUscita().getAttachments())) {
							if(bf.length()>0) {
								bf.append(", ");
							}
							bf.append(CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_USCITA_ATTACHMENTS);
						}		
					}
					else {
						if(bf.length()>0) {
							bf.append(", ");
						}
						bf.append(CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_USCITA_PAYLOAD);
					}
				}
				if(bf.length()>0) {
					if(bfRisposta.length()>0) {
						bfRisposta.append("\n");
					}
					bfRisposta.append(CostantiControlStation.LABEL_PARAMETRO_RISPOSTA+" "+CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_USCITA).append(": ").append(bf.toString());
					
					if(bfRispostaOptions.length()>0) {
						bfRispostaOptions.append(", ");
					}
					bfRispostaOptions.append(CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_USCITA);
				}
			}
		}
		
		if(bfRichiesta.length()>0 || bfRisposta.length()>0) {
			
			if(bfRichiesta.length()>0) {
				de.addStatus(tooltip+"\n"+bfRichiesta.toString(), 
						(upperFirstChar ? CostantiControlStation.LABEL_PARAMETRO_RICHIESTA : CostantiControlStation.LABEL_PARAMETRO_RICHIESTA.toLowerCase())+" [ "+bfRichiestaOptions.toString()+" ]", 
						CheckboxStatusType.CONFIG_ENABLE);
			}
			
			if(bfRisposta.length()>0) {
				de.addStatus(tooltip+"\n"+bfRisposta.toString(), 
						(upperFirstChar ? CostantiControlStation.LABEL_PARAMETRO_RISPOSTA : CostantiControlStation.LABEL_PARAMETRO_RISPOSTA.toLowerCase())+" [ "+bfRispostaOptions.toString()+" ]", 
						CheckboxStatusType.CONFIG_ENABLE);
			}
			
		}
		else {
			de.addStatus(tooltip, 
					(upperFirstChar ? this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_DISABILITATO) : CostantiControlStation.DEFAULT_VALUE_DISABILITATO), 
					CheckboxStatusType.CONFIG_DISABLE);
		}
	}
	
	public void setStatoProprieta(DataElement de, int size) {
		
		de.setType(DataElementType.CHECKBOX);
		
		if(size>0) {
			de.setStatusType(CheckboxStatusType.CONFIG_ENABLE);
			de.setStatusValue(this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_ABILITATO));
			de.setStatusToolTip("Sono registrate "+size+" proprietà");
		}
		else {
			de.setStatusType(CheckboxStatusType.CONFIG_DISABLE);
			de.setStatusValue(this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_DISABILITATO));
		}
		
	}
	
	public void setStatoOpzioniAvanzate(DataElement de, 
			String protocollo, ServiceBinding serviceBinding,
			StatoFunzionalita allegaBody, StatoFunzionalita scartaBody, 
			String integrazione, String behaviour,
			StatoFunzionalita stateless, PortaDelegataLocalForward localForward,
			StatoFunzionalita ricevutaAsincronaSimmetrica, StatoFunzionalita ricevutaAsincronaAsimmetrica,
			StatoFunzionalita gestioneManifest, ConfigurazionePortaHandler configPortaHandler) throws DriverRegistroServiziNotFound, DriverRegistroServiziException, DriverConfigurazioneException {
		setStatoOpzioniAvanzate(de, 
				protocollo, serviceBinding,
				allegaBody, scartaBody, 
				integrazione, behaviour,
				stateless, localForward,
				ricevutaAsincronaSimmetrica, ricevutaAsincronaAsimmetrica,
				gestioneManifest, configPortaHandler, true);
	}
	public void setStatoOpzioniAvanzate(DataElement de, 
			String protocollo, ServiceBinding serviceBinding,
			StatoFunzionalita allegaBody, StatoFunzionalita scartaBody, 
			String integrazione, String behaviour,
			StatoFunzionalita stateless, PortaDelegataLocalForward localForward,
			StatoFunzionalita ricevutaAsincronaSimmetrica, StatoFunzionalita ricevutaAsincronaAsimmetrica,
			StatoFunzionalita gestioneManifest, ConfigurazionePortaHandler configPortaHandler, boolean upperFirstChar) throws DriverRegistroServiziNotFound, DriverRegistroServiziException, DriverConfigurazioneException {
		
		boolean supportoAsincroni = this.core.isProfiloDiCollaborazioneAsincronoSupportatoDalProtocollo(protocollo,serviceBinding);
		boolean supportoGestioneManifest = isFunzionalitaProtocolloSupportataDalProtocollo(protocollo, serviceBinding, FunzionalitaProtocollo.MANIFEST_ATTACHMENTS);
		
		de.setType(DataElementType.CHECKBOX);
		
		StringBuilder bf = new StringBuilder();
		StringBuilder bfTooltips = new StringBuilder();
		if(allegaBody!=null && StatoFunzionalita.ABILITATO.equals(allegaBody)) {
			if(bf.length()>0) {
				bf.append(", ");
			}
			bf.append("Allega SOAPBody");
			if(bfTooltips.length()>0) {
				bfTooltips.append("\n");
			}
			bfTooltips.append("Allega SOAP Body come Attachment");
		}
		if(scartaBody!=null && StatoFunzionalita.ABILITATO.equals(scartaBody)) {
			if(bf.length()>0) {
				bf.append(", ");
			}
			bf.append("Scarta SOAPBody");
			if(bfTooltips.length()>0) {
				bfTooltips.append("\n");
			}
			bfTooltips.append("Scarta SOAP Body");
		}
		if(integrazione!=null && !"".equals(integrazione)) {
			String [] tmp = integrazione.split(",");
			if(bf.length()>0) {
				bf.append(", ");
			}
			bf.append(CostantiControlStation.LABEL_METADATI_INTEGRAZIONE);
			if(tmp.length>1) {
				bf.append(" (").append(tmp.length).append(")");
			}
			if(bfTooltips.length()>0) {
				bfTooltips.append("\n");
			}
			
			List<String> listGruppi = new ArrayList<String>();
			List<String> listPlugins = new ArrayList<String>();
			if(tmp.length>0) {
				for (String tmpIntegrazione : tmp) {
					if(tmpIntegrazione!=null) {
						tmpIntegrazione = tmpIntegrazione.trim();
						TipoIntegrazione tipo = TipoIntegrazione.toEnumConstant(tmpIntegrazione);
						if(tipo!=null) {
							String label = null;
							if(tipo.getGroup()!=null) {
								label = tipo.getGroup().getCompactLabel();
							}
							else {
								label = tipo.getLabel();
							}
							if(!listGruppi.contains(label)) {
								listGruppi.add(label);
							}
						}
						else {
							listPlugins.add(tmpIntegrazione);
							String label = GruppoIntegrazione.PLUGIN.getCompactLabel();
							if(!listGruppi.contains(label)) {
								listGruppi.add(label);
							}
						}
					}
					else {
						listPlugins.add(tmpIntegrazione);
						String label = GruppoIntegrazione.PLUGIN.getCompactLabel();
						if(!listGruppi.contains(label)) {
							listGruppi.add(label);
						}
					}
				}
			}
			StringBuilder integrazioneString = new StringBuilder("");
			if(!listGruppi.isEmpty()) {
				for (String gruppo : listGruppi) {
					if(integrazioneString.length()>0) {
						integrazioneString.append(", ");
					}
					integrazioneString.append(gruppo);
					if(GruppoIntegrazione.PLUGIN.getCompactLabel().equals(gruppo)) {
						if(!listPlugins.isEmpty()) {
							integrazioneString.append(" (");
							for (int i = 0; i < listPlugins.size(); i++) {
								String plug = listPlugins.get(i);
								if(i>0) {
									integrazioneString.append(",");
								}
								integrazioneString.append(plug);
							}
							integrazioneString.append(")");
						}
					}
				}
			}
			
			bfTooltips.append(CostantiControlStation.LABEL_METADATI_INTEGRAZIONE).append(": ").append(integrazioneString.toString());
		}
		if(behaviour!=null && !"".equals(behaviour)) {
			String [] tmp = behaviour.split(",");
			if(bf.length()>0) {
				bf.append(", ");
			}
			bf.append(CostantiControlStation.LABEL_BEHAVIOUR);
			if(tmp.length>1) {
				bf.append(" (").append(tmp.length).append(")");
			}
			if(bfTooltips.length()>0) {
				bfTooltips.append("\n");
			}
			bfTooltips.append(CostantiControlStation.LABEL_BEHAVIOUR).append(": ").append(behaviour);
		}
		if(stateless!=null) {
			if(bf.length()>0) {
				bf.append(", ");
			}
			if(bfTooltips.length()>0) {
				bfTooltips.append("\n");
			}
			if(StatoFunzionalita.ABILITATO.equals(stateless)) {
				bf.append(CostantiControlStation.LABEL_GESTIONE_STATELESS);
				bfTooltips.append(CostantiControlStation.LABEL_GESTIONE_STATELESS);
			}
			else {
				bf.append(CostantiControlStation.LABEL_GESTIONE_STATEFUL);
				bfTooltips.append(CostantiControlStation.LABEL_GESTIONE_STATEFUL);
			}
		}
		if(localForward!=null && StatoFunzionalita.ABILITATO.equals(localForward.getStato())) {
			if(bf.length()>0) {
				bf.append(", ");
			}
			bf.append(CostantiControlStation.LABEL_LOCAL_FORWARD);
			if(bfTooltips.length()>0) {
				bfTooltips.append("\n");
			}
			bfTooltips.append(CostantiControlStation.LABEL_LOCAL_FORWARD);
			if(localForward.getPortaApplicativa()!=null) {
				bfTooltips.append(", ").append(CostantiControlStation.LABEL_LOCAL_FORWARD_PA).append(": ").append(localForward.getPortaApplicativa());
			}
		}
		if(supportoAsincroni) {
			if(ricevutaAsincronaSimmetrica!=null && StatoFunzionalita.ABILITATO.equals(ricevutaAsincronaSimmetrica)) {
				if(bf.length()>0) {
					bf.append(", ");
				}
				bf.append(CostantiControlStation.LABEL_RICEVUTA_ASINCRONA_SIMMETRICA);
				if(bfTooltips.length()>0) {
					bfTooltips.append("\n");
				}
				bfTooltips.append(CostantiControlStation.LABEL_RICEVUTA_ASINCRONA_SIMMETRICA);
			}
			if(ricevutaAsincronaAsimmetrica!=null && StatoFunzionalita.ABILITATO.equals(ricevutaAsincronaAsimmetrica)) {
				if(bf.length()>0) {
					bf.append(", ");
				}
				bf.append(CostantiControlStation.LABEL_RICEVUTA_ASINCRONA_ASIMMETRICA);
				if(bfTooltips.length()>0) {
					bfTooltips.append("\n");
				}
				bfTooltips.append(CostantiControlStation.LABEL_RICEVUTA_ASINCRONA_ASIMMETRICA);
			}
		}
		if(supportoGestioneManifest) {
			if(gestioneManifest!=null && StatoFunzionalita.ABILITATO.equals(gestioneManifest)) {
				if(bf.length()>0) {
					bf.append(", ");
				}
				bf.append(CostantiControlStation.LABEL_GESTIONE_MANIFEST);
				if(bfTooltips.length()>0) {
					bfTooltips.append("\n");
				}
				bfTooltips.append(CostantiControlStation.LABEL_GESTIONE_MANIFEST);
			}
		}
		if(configPortaHandler!=null) {
			StringBuilder sbRequest = new StringBuilder();
			if(configPortaHandler.getRequest()!=null) {
				if(configPortaHandler.getRequest().sizePreInList()>0) {
					if(sbRequest.length()>0) {
						sbRequest.append(", ");
					}
					sbRequest.append(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_LABEL_PRE_IN_SHORT).append(" (").append(configPortaHandler.getRequest().sizePreInList()).append(")");
				}
				if(configPortaHandler.getRequest().sizeInList()>0) {
					if(sbRequest.length()>0) {
						sbRequest.append(", ");
					}
					sbRequest.append(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_LABEL_IN_SHORT).append(" (").append(configPortaHandler.getRequest().sizeInList()).append(")");
				}
				if(configPortaHandler.getRequest().sizeInProtocolInfoList()>0) {
					if(sbRequest.length()>0) {
						sbRequest.append(", ");
					}
					sbRequest.append(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_LABEL_IN_PROTOCOL_INFO_SHORT).append(" (").append(configPortaHandler.getRequest().sizeInProtocolInfoList()).append(")");
				}
				if(configPortaHandler.getRequest().sizeOutList()>0) {
					if(sbRequest.length()>0) {
						sbRequest.append(", ");
					}
					sbRequest.append(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_LABEL_OUT_SHORT).append(" (").append(configPortaHandler.getRequest().sizeOutList()).append(")");
				}	
				if(configPortaHandler.getRequest().sizePostOutList()>0) {
					if(sbRequest.length()>0) {
						sbRequest.append(", ");
					}
					sbRequest.append(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_LABEL_POST_OUT_SHORT).append(" (").append(configPortaHandler.getRequest().sizePostOutList()).append(")");
				}	
			}
			StringBuilder sbResponse = new StringBuilder();
			if(configPortaHandler.getResponse()!=null) {
				if(configPortaHandler.getResponse().sizePreInList()>0) {
					if(sbResponse.length()>0) {
						sbResponse.append(", ");
					}
					sbResponse.append(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_LABEL_PRE_IN_SHORT).append(" (").append(configPortaHandler.getResponse().sizePreInList()).append(")");
				}
				if(configPortaHandler.getResponse().sizeInList()>0) {
					if(sbResponse.length()>0) {
						sbResponse.append(", ");
					}
					sbResponse.append(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_LABEL_IN_SHORT).append(" (").append(configPortaHandler.getResponse().sizeInList()).append(")");
				}
				if(configPortaHandler.getResponse().sizeInProtocolInfoList()>0) {
					if(sbResponse.length()>0) {
						sbResponse.append(", ");
					}
					sbResponse.append(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_LABEL_IN_PROTOCOL_INFO_SHORT).append(" (").append(configPortaHandler.getResponse().sizeInProtocolInfoList()).append(")");
				}
				if(configPortaHandler.getResponse().sizeOutList()>0) {
					if(sbResponse.length()>0) {
						sbResponse.append(", ");
					}
					sbResponse.append(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_LABEL_OUT_SHORT).append(" (").append(configPortaHandler.getResponse().sizeOutList()).append(")");
				}	
				if(configPortaHandler.getResponse().sizePostOutList()>0) {
					if(sbResponse.length()>0) {
						sbResponse.append(", ");
					}
					sbResponse.append(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_LABEL_POST_OUT_SHORT).append(" (").append(configPortaHandler.getResponse().sizePostOutList()).append(")");
				}	
			}
			if(sbRequest.length()>0 || sbResponse.length()>0) {
				if(bf.length()>0) {
					bf.append(", ");
				}
				bf.append(CostantiControlStation.LABEL_MESSAGE_HANDLER);
				if(sbRequest.length()>0) {
					if(bfTooltips.length()>0) {
						bfTooltips.append("\n");
					}
					bfTooltips.append(CostantiControlStation.LABEL_REQUEST_MESSAGE_HANDLER).append(": ").append(sbRequest.toString());
				}
				if(sbResponse.length()>0) {
					if(bfTooltips.length()>0) {
						bfTooltips.append("\n");
					}
					bfTooltips.append(CostantiControlStation.LABEL_RESPONSE_MESSAGE_HANDLER).append(": ").append(sbResponse.toString());
				}
			}
		}
		
		if(bf.length()>0) {
			de.setStatusType(CheckboxStatusType.CONFIG_ENABLE);
			String vAbilitato = upperFirstChar ? this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_ABILITATO) : CostantiControlStation.DEFAULT_VALUE_ABILITATO; 
			de.setStatusValue(vAbilitato+" [ "+bf.toString()+" ]");
			de.setStatusToolTip(bfTooltips.toString());
		}
		else {
			de.setStatusType(CheckboxStatusType.CONFIG_DISABLE);
			String vDisabilitato = upperFirstChar ? this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_DISABILITATO) : CostantiControlStation.DEFAULT_VALUE_DISABILITATO; 
			de.setStatusValue(vDisabilitato);
		}
		
	}
	
	public void setStatoExtendedList(DataElement de, int size, String stato, String statoTooltip) {
		setStatoExtendedList(de, size, stato, statoTooltip, true);
	}
	public void setStatoExtendedList(DataElement de, int size, String stato, String statoTooltip, boolean upperFirstChar) {
		
		de.setType(DataElementType.CHECKBOX);
		
		if(size>0) {
			de.setStatusType(CheckboxStatusType.CONFIG_ENABLE);
			String vAbilitato = upperFirstChar ? this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_ABILITATO) : CostantiControlStation.DEFAULT_VALUE_ABILITATO; 
			if(stato!=null) {
				de.setStatusValue(vAbilitato+" [" +stato+ " ]");
			}
			else {
				de.setStatusValue(vAbilitato);
			}
			if(statoTooltip!=null) {
				de.setStatusToolTip(statoTooltip);
			}
			else {
				de.setStatusToolTip("Sono registrate "+size+" proprietà");
			}
		}
		else {
			de.setStatusType(CheckboxStatusType.CONFIG_DISABLE);
			String vDisabilitato = upperFirstChar ? this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_DISABILITATO) : CostantiControlStation.DEFAULT_VALUE_DISABILITATO; 
			de.setStatusValue(vDisabilitato);
		}
		
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
	
	public boolean porteAppAzioneCheckData(TipoOperazione add, List<String> azioniOccupate, List<MappingErogazionePortaApplicativa> list) throws Exception {
		String[] azionis = this.getParameterValues(CostantiControlStation.PARAMETRO_AZIONI);
		
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
		
		if(checkAzioniUtilizzateErogazione(list, azionis)==false) {
			return false;
		}
		
		return true;
	}
	
	public boolean porteDelAzioneCheckData(TipoOperazione add, List<String> azioniOccupate, List<MappingFruizionePortaDelegata> list) throws Exception {
		String[] azionis = this.getParameterValues(CostantiControlStation.PARAMETRO_AZIONI);
		
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
		
		if(checkAzioniUtilizzateFruizione(list, azionis)==false) {
			return false;
		}
		
		return true;
	}

	public String getMessaggioConfermaModificaRegolaMapping(boolean fromAPI, boolean isDefault,List<String> listaAzioni,ServiceBinding serviceBinding, String gruppo,
			boolean abilitazione, boolean multiline,boolean listElement) throws DriverConfigurazioneException {
		String pre = Costanti.HTML_MODAL_SPAN_PREFIX;
		String post = Costanti.HTML_MODAL_SPAN_SUFFIX;
		
		if(fromAPI) {
			return pre + ( abilitazione ? CostantiControlStation.MESSAGGIO_CONFERMA_ABILITAZIONE_FROM_API : CostantiControlStation.MESSAGGIO_CONFERMA_DISABILITAZIONE_FROM_API)  + post;
		}
		else {
			return pre + ( abilitazione ? MessageFormat.format(CostantiControlStation.MESSAGGIO_CONFERMA_ABILITAZIONE_GRUPPO,gruppo) : MessageFormat.format(CostantiControlStation.MESSAGGIO_CONFERMA_DISABILITAZIONE_GRUPPO,gruppo) )  + post;
		}
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
	public void preparePorteAzioneList(ISearch ricerca,
			List<String> listaAzioniParamDaPaginare, String idPorta, Integer parentConfigurazione, List<Parameter> lstParametriBreadcrumbs, 
			String nomePorta, String objectName, List<Parameter> listaParametriSessione,
			String labelPerPorta, ServiceBinding serviceBinding, AccordoServizioParteComuneSintetico aspc) throws Exception {
		try {
			ServletUtils.addListElementIntoSession(this.session, objectName,listaParametriSessione);

			// setto la barra del titolo

			String label = this.getLabelAzione(serviceBinding);
						
			int idLista = -1;
			if(PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_AZIONE.equals(objectName)) {
				idLista = Liste.PORTE_DELEGATE_AZIONI;
			}
			else {
				idLista = Liste.PORTE_APPLICATIVE_AZIONI;
			}
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			org.openspcoop2.core.registry.constants.HttpMethod httpMethod = null;
			if(!ServiceBinding.SOAP.equals(serviceBinding)) {
				String filterHttpMethod = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_HTTP_METHOD);
				this.addFilterHttpMethod(filterHttpMethod, false);
				
				if(filterHttpMethod!=null) {
					httpMethod = org.openspcoop2.core.registry.constants.HttpMethod.toEnumConstant(filterHttpMethod);
				}
			}
			
			HashMap<String, ResourceSintetica> mapIdResourceToMethodPath = new HashMap<String, ResourceSintetica>();
			
			List<String> listaAzioniPaginata = new ArrayList<>();
			if(listaAzioniParamDaPaginare!=null && !listaAzioniParamDaPaginare.isEmpty()) {
				List<String> listaAzioniDopoSearch = new ArrayList<>();
				
				if(ServiceBinding.SOAP.equals(serviceBinding)) {
					for (int i = 0; i < listaAzioniParamDaPaginare.size(); i++) {
						String idRisorsa = listaAzioniParamDaPaginare.get(i);
						
						// verifico nome
						if(search!=null && !"".equals(search)) {
							if(!idRisorsa.toLowerCase().contains(search.toLowerCase())) {
								continue;
							}
						}
						
						// azione che ha un match
						listaAzioniDopoSearch.add(idRisorsa);
					}
				}
				else {
					for (int i = 0; i < listaAzioniParamDaPaginare.size(); i++) {
						String idRisorsa = listaAzioniParamDaPaginare.get(i);
						ResourceSintetica risorsa = null;
						for (ResourceSintetica resourceTmp : aspc.getResource()) {
							if(resourceTmp.getNome().equals(idRisorsa)) {
								risorsa = resourceTmp;
								break;
							}
						}
						
						if(risorsa==null) {
							throw new Exception("Risorsa con id '"+idRisorsa+"' non esistente ?");
						}
						
						// verifico path
						if(search!=null && !"".equals(search)) {
							if(risorsa.getPath()==null || !risorsa.getPath().toLowerCase().contains(search.toLowerCase())) {
								continue;
							}
						}
						
						// verifico metodo
						if(httpMethod!=null) {
							if(risorsa.getMethod()==null || !risorsa.getMethod().equals(httpMethod)) {
								continue;
							}
						}
						
						// azione che ha un match sia con path che con metodo
						listaAzioniDopoSearch.add(idRisorsa);
						if(risorsa!=null) {
							mapIdResourceToMethodPath.put(idRisorsa, risorsa);
						}
					}
				}
				
				ricerca.setNumEntries(idLista, listaAzioniDopoSearch.size());
				
				if(listaAzioniDopoSearch!=null && !listaAzioniDopoSearch.isEmpty()) {
					for (int i = 0; i < listaAzioniDopoSearch.size(); i++) {
						if(i>=offset && i<(offset+limit)) {
							listaAzioniPaginata.add(listaAzioniDopoSearch.get(i));
						}
					}
				}
			}
			else {
				ricerca.setNumEntries(idLista, listaAzioniParamDaPaginare.size());
			}
			
			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
			
			if(ServiceBinding.SOAP.equals(serviceBinding)) {
				this.pd.setSearchLabel(this.getLabelAzione(serviceBinding));
			}
			else {
				this.pd.setSearchLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_RESOURCES_PATH);
			}
			
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, label, search);
			}
			
			List<String> listaAzioni = new ArrayList<>();
			HashMap<String, ResourceSintetica> mapToResource = new HashMap<>();
			for (String azione : listaAzioniPaginata) {
				if(ServiceBinding.SOAP.equals(serviceBinding)) {
					listaAzioni.add(azione);
				}
				else {
					ResourceSintetica risorsa = mapIdResourceToMethodPath.get(azione);
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
						ResourceSintetica risorsa = mapToResource.get(nomeAzione);
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
			}

			
			this.pd.setDati(dati);
			this.pd.setAddButton(true);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public DataElement getDataElementHTTPMethodResource(org.openspcoop2.core.registry.beans.ResourceSintetica risorsa,	String labelParametroApcResourcesHttpMethodQualsiasi) {
		return getDataElementHTTPMethodResource(risorsa.getMethod(), labelParametroApcResourcesHttpMethodQualsiasi, null);
	}
	public DataElement getDataElementHTTPMethodResource(org.openspcoop2.core.registry.Resource risorsa,	String labelParametroApcResourcesHttpMethodQualsiasi, String detailURL) {
		return getDataElementHTTPMethodResource(risorsa.getMethod(), labelParametroApcResourcesHttpMethodQualsiasi, detailURL);
	}
	public DataElement getDataElementHTTPMethodResource(HttpMethod httpMethod,	String labelParametroApcResourcesHttpMethodQualsiasi, String detailUrl) {
		
		DataElement de = new DataElement();

		String styleClass = "resource-method-block resource-method-default";
		if(httpMethod==null) {
			de.setValue(labelParametroApcResourcesHttpMethodQualsiasi);
		}
		else {
			de.setValue(httpMethod.toString());
			
			switch (httpMethod) {
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
		if(StringUtils.isNotEmpty(detailUrl)) {
			StringBuilder onClickFunction = new StringBuilder();
			onClickFunction.append(Costanti.JS_FUNCTION_GO_TO_PREFIX);
			onClickFunction.append(detailUrl);
			onClickFunction.append(Costanti.JS_FUNCTION_GO_TO_SUFFIX);
			de.setOnClick(onClickFunction.toString());
			styleClass += " resource-method-block-pointer";
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
		
	public void addFilterTipoPolicy(String tipoPolicy, boolean postBack) throws Exception{
		try {

			String selectedValue = tipoPolicy != null ? tipoPolicy : CostantiControlStation.DEFAULT_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPO;

			this.pd.addFilter(Filtri.FILTRO_TIPO_POLICY, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPO, 
					selectedValue, 
					CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPI_VALORI, 
					CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPI_LABELS, 
					postBack, this.getSize());
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void addFilterGruppo(String filterProtocollo, String gruppo, boolean postBack) throws Exception{
		try {
			
			boolean isFilterProtocollo = filterProtocollo!=null && !"".equals(filterProtocollo) && !CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROTOCOLLO_QUALSIASI.equals(filterProtocollo);
			
			List<String> protocolli = this.core.getProtocolli(this.session);
			if(isFilterProtocollo) {
				protocolli.clear();
				protocolli.add(filterProtocollo);
			}
			
			FiltroRicercaGruppi filtroGruppi = new FiltroRicercaGruppi();
			filtroGruppi.setProtocolli(protocolli);
			List<IDGruppo> listGruppi = this.gruppiCore.getAllIdGruppi(filtroGruppi);
			int length = 1;
			if(listGruppi!=null && listGruppi.size()>0) {
				length+=listGruppi.size();
			}
			String [] values = new String[length];
			String [] labels = new String[length];
			labels[0] = CostantiControlStation.LABEL_PARAMETRO_GRUPPO_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_GRUPPO_QUALSIASI;
			if(listGruppi!=null && listGruppi.size()>0) {
				for (int i =0; i < listGruppi.size() ; i ++) {
					labels[i+1] = listGruppi.get(i).getNome();
					values[i+1] = listGruppi.get(i).getNome();
				}
			}
			
			this.pd.addFilter(Filtri.FILTRO_GRUPPO, GruppiCostanti.LABEL_GRUPPO, gruppo, values, labels, postBack, this.getSize());
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void addFilterApi(String filterProtocollo, String filterTipoAccordo, String gruppo, String api, boolean postBack) throws Exception{
		try {
			
			boolean isFilterProtocollo = filterProtocollo!=null && !"".equals(filterProtocollo) && !CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROTOCOLLO_QUALSIASI.equals(filterProtocollo);
			
			List<String> protocolli = this.core.getProtocolli(this.session);
			if(isFilterProtocollo) {
				protocolli.clear();
				protocolli.add(filterProtocollo);
			}
			
			FiltroRicercaAccordi filtroRicerca = new FiltroRicercaAccordi();
			
			filtroRicerca.setOrder(true);
			
			filtroRicerca.setProtocolli(protocolli);
			
			if(gruppo!=null && !CostantiControlStation.DEFAULT_VALUE_PARAMETRO_GRUPPO_QUALSIASI.equals(gruppo)) {
				IDGruppo idGruppo = new IDGruppo(gruppo);
				filtroRicerca.setIdGruppo(idGruppo);
			}
			
			if(filterTipoAccordo!=null && !CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_QUALSIASI.equals(filterTipoAccordo)) {
				filtroRicerca.setServiceBinding(org.openspcoop2.core.registry.constants.ServiceBinding.toEnumConstant(filterTipoAccordo));
			}
			
			List<IDAccordo> listAccordi = null;
			try {
				listAccordi = this.apcCore.getAllIdAccordiServizio(filtroRicerca);
			}catch(DriverRegistroServiziNotFound notFound) {
				listAccordi = new ArrayList<IDAccordo>();
			}
			int length = 1;
			if(listAccordi!=null && listAccordi.size()>0) {
				length+=listAccordi.size();
			}
			String [] values = new String[length];
			String [] labels = new String[length];
			labels[0] = CostantiControlStation.LABEL_PARAMETRO_API_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_API_QUALSIASI;
			if(listAccordi!=null && listAccordi.size()>0) {
				for (int i =0; i < listAccordi.size() ; i ++) {
					labels[i+1] = this.getLabelIdAccordo(listAccordi.get(i));
					values[i+1] = IDAccordoFactory.getInstance().getUriFromIDAccordo(listAccordi.get(i));
				}
			}
			
			this.pd.addFilter(Filtri.FILTRO_API, AccordiServizioParteComuneCostanti.LABEL_APC, api, values, labels, postBack, this.getSize());
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void addFilterCanale(CanaliConfigurazione canali, String canale, boolean postBack) throws Exception{
		try {
			
			int length = 1;
			if(canali!=null && canali.sizeCanaleList()>0) {
				length+=canali.sizeCanaleList();
			}
			String [] values = new String[length];
			String [] labels = new String[length];
			labels[0] = CostantiControlStation.LABEL_PARAMETRO_CANALE_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CANALE_QUALSIASI;
			if(canali!=null && canali.sizeCanaleList()>0) {
				for (int i =0; i < canali.sizeCanaleList() ; i ++) {
					labels[i+1] = canali.getCanale(i).getNome();
					if(canali.getCanale(i).isCanaleDefault()) {
						values[i+1] = CostantiControlStation.PREFIX_VALUE_PARAMETRO_CANALE_DEFAULT+canali.getCanale(i).getNome();
					}
					else {
						values[i+1] = canali.getCanale(i).getNome();
					}
				}
			}
			
			this.pd.addFilter(Filtri.FILTRO_CANALE, ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CANALE, canale, values, labels, postBack, this.getSize());
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void addFilterApiContesto(String apiContesto, boolean postBack) throws Exception{
		this._addFilterApiContesto(false, false, false, apiContesto, postBack);
	}
	public void addFilterApiContestoRuoli(String apiContesto, boolean postBack) throws Exception{
		this._addFilterApiContesto(true, true, false, apiContesto, postBack);
	}
	private void _addFilterApiContesto(boolean soggetti, boolean applicativi, boolean erogazioneFruizione, String apiContesto, boolean postBack) throws Exception{
		try {
			int size = 2;
			if(soggetti) {
				size++;
			}
			if(applicativi) {
				size++;
			}
			if(erogazioneFruizione) {
				size++;
			}
			
			String [] metodi = new String[size];
			int index = 0;
			if(soggetti) {
				metodi[index++] = Filtri.FILTRO_API_CONTESTO_VALUE_SOGGETTI;
			}
			if(applicativi) {
				metodi[index++] = Filtri.FILTRO_API_CONTESTO_VALUE_APPLICATIVI;
			}
			metodi[index++] = TipoPdD.APPLICATIVA.getTipo();
			metodi[index++] = TipoPdD.DELEGATA.getTipo();
			if(erogazioneFruizione) {
				metodi[index++] = Filtri.FILTRO_API_CONTESTO_VALUE_EROGAZIONE_FRUIZIONE;
			}
			String [] metodiLabel = new String[size];
			index = 0;
			if(soggetti) {
				metodiLabel[index++] = CostantiControlStation.API_CONTESTO_UTILIZZO_LABEL_SOGGETTI;
			}
			if(applicativi) {
				metodiLabel[index++] = CostantiControlStation.API_CONTESTO_UTILIZZO_LABEL_APPLICATIVI;
			}
			metodiLabel[index++] = CostantiControlStation.API_CONTESTO_UTILIZZO_LABEL_EROGAZIONE;
			metodiLabel[index++] = CostantiControlStation.API_CONTESTO_UTILIZZO_LABEL_FRUIZIONE;
			if(erogazioneFruizione) {
				metodi[index++] = CostantiControlStation.API_CONTESTO_UTILIZZO_LABEL_EROGAZIONE_FRUIZIONE;
			}
			String [] values = new String[metodi.length + 1];
			String [] labels = new String[metodi.length + 1];
			labels[0] = CostantiControlStation.LABEL_PARAMETRO_API_CONTESTO_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_API_CONTESTO_QUALSIASI;
			for (int i =0; i < metodi.length ; i ++) {
				labels[i+1] = metodiLabel[i];
				values[i+1] = metodi[i];
			}
			
			String selectedValue = apiContesto != null ? apiContesto : CostantiControlStation.DEFAULT_VALUE_PARAMETRO_API_CONTESTO_QUALSIASI;
			
			String label = CostantiControlStation.LABEL_PARAMETRO_API_CONTESTO;
			
			this.pd.addFilter(Filtri.FILTRO_API_CONTESTO, label, selectedValue, values, labels, postBack, this.getSize());
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void addFilterApiImplementazione(String filterProtocollo, String filterSoggetto, String filterGruppo, String filterApiContesto, String apiImplementazione, boolean postBack) throws Exception{
		try {
			boolean isFruizione = TipoPdD.DELEGATA.getTipo().equals(filterApiContesto);
			boolean isErogazione = TipoPdD.APPLICATIVA.getTipo().equals(filterApiContesto);
			
			if(!isErogazione && !isFruizione) {
				return;
			}
			
			boolean isFilterProtocollo = filterProtocollo!=null && !"".equals(filterProtocollo) && !CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROTOCOLLO_QUALSIASI.equals(filterProtocollo);
			boolean isFilterSoggetto = filterSoggetto!=null && !"".equals(filterSoggetto) && !CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SOGGETTO_QUALSIASI.equals(filterSoggetto);
			boolean isFilterGruppo = filterGruppo!=null && !"".equals(filterGruppo) && !CostantiControlStation.DEFAULT_VALUE_PARAMETRO_GRUPPO_QUALSIASI.equals(filterGruppo);
			
			List<String> protocolli = this.core.getProtocolli(this.session);
			if(isFilterProtocollo) {
				protocolli.clear();
				protocolli.add(filterProtocollo);
			}
			
			String tipoSoggetto = null;
			String nomeSoggetto = null;
			String protocolloSelezionato = null; 
			if(isFilterSoggetto) {
				tipoSoggetto = filterSoggetto.split("/")[0];
				nomeSoggetto = filterSoggetto.split("/")[1];
				protocolloSelezionato = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoSoggetto);
			}
			else {
				protocolloSelezionato = protocolli.get(0);
			}
			
			String gruppo = null;
			if(isFilterGruppo) {
				gruppo = filterGruppo;
			}
			
			List<String> valuesL = new ArrayList<String>();
			List<String> labelsL = new ArrayList<String>();
			if(isFruizione) {
				List<IDFruizione> listFruizioni = this.apsCore.getFruizioni(protocolli, gruppo, tipoSoggetto, nomeSoggetto);
				if(listFruizioni!=null && !listFruizioni.isEmpty()) {
					for (IDFruizione idFruizione : listFruizioni) {
						valuesL.add(idFruizione.toFormatString());
						if(isFilterSoggetto) {
							labelsL.add(this.getLabelIdServizio(idFruizione.getIdServizio()));
						}
						else {
							labelsL.add(this.getLabelServizioFruizione(protocolloSelezionato, idFruizione.getIdFruitore(), idFruizione.getIdServizio()));
						}
					}
				}
			}
			else {
				List<IDServizio> listErogazioni = this.apsCore.getErogazioni(protocolli, gruppo, tipoSoggetto, nomeSoggetto);
				if(listErogazioni!=null && !listErogazioni.isEmpty()) {
					for (IDServizio idServizio : listErogazioni) {
						valuesL.add(idServizio.toFormatString());
						if(isFilterSoggetto) {
							labelsL.add(this.getLabelIdServizioSenzaErogatore(idServizio));
						}
						else {
							labelsL.add(this.getLabelIdServizio(idServizio));
						}
					}
				}
			}
			
			String [] values = new String[valuesL.size() + 1];
			String [] labels = new String[labelsL.size() + 1];
			labels[0] = CostantiControlStation.LABEL_PARAMETRO_API_IMPLEMENTAZIONE_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_API_IMPLEMENTAZIONE_QUALSIASI;
			for (int i =0; i < valuesL.size() ; i ++) {
				labels[i+1] = labelsL.get(i);
				values[i+1] = valuesL.get(i);
			}
			
			String selectedValue = apiImplementazione != null ? apiImplementazione : CostantiControlStation.DEFAULT_VALUE_PARAMETRO_API_IMPLEMENTAZIONE_QUALSIASI;
			
			String label = CostantiControlStation.LABEL_PARAMETRO_API_IMPLEMENTAZIONE;
			
			this.pd.addFilter(Filtri.FILTRO_API_IMPLEMENTAZIONE, label, selectedValue, values, labels, postBack, this.getSize());
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void addFilterApplicativo(String filterProtocollo, String filterSoggetto, String applicativo, boolean postBack) throws Exception{
		try {
			
			boolean isFilterProtocollo = filterProtocollo!=null && !"".equals(filterProtocollo) && !CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROTOCOLLO_QUALSIASI.equals(filterProtocollo);
			boolean isFilterSoggetto = filterSoggetto!=null && !"".equals(filterSoggetto) && !CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SOGGETTO_QUALSIASI.equals(filterSoggetto);
						
			List<String> protocolli = this.core.getProtocolli(this.session);
			if(isFilterProtocollo) {
				protocolli.clear();
				protocolli.add(filterProtocollo);
			}
			
			String filterSoggettoTipo = null;
			String filterSoggettoNome = null;
			String protocolloSelezionato = null; 
			if(isFilterSoggetto) {
				filterSoggettoTipo = filterSoggetto.split("/")[0];
				filterSoggettoNome = filterSoggetto.split("/")[1];
				protocolloSelezionato = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(filterSoggettoTipo);
			}
			else {
				protocolloSelezionato = protocolli.get(0);
			}
			
			List<String> valuesL = new ArrayList<String>();
			List<String> labelsL = new ArrayList<String>();
			List<IDServizioApplicativo> listApplicativi = this.confCore.getServiziApplicativi(null, protocolli, filterSoggettoTipo, filterSoggettoNome);
			if(listApplicativi!=null && !listApplicativi.isEmpty()) {
				for (IDServizioApplicativo idApplicativo : listApplicativi) {
					valuesL.add(idApplicativo.toFormatString());
					if(isFilterSoggetto) {
						labelsL.add(idApplicativo.getNome());
					}
					else {
						labelsL.add(this.getLabelServizioApplicativo(protocolloSelezionato, idApplicativo));
					}
				}
			}
			
			String [] values = new String[valuesL.size() + 1];
			String [] labels = new String[labelsL.size() + 1];
			labels[0] = CostantiControlStation.LABEL_PARAMETRO_APPLICATIVO_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_APPLICATIVO_QUALSIASI;
			for (int i =0; i < valuesL.size() ; i ++) {
				labels[i+1] = labelsL.get(i);
				values[i+1] = valuesL.get(i);
			}
			
			String selectedValue = applicativo != null ? applicativo : CostantiControlStation.DEFAULT_VALUE_PARAMETRO_APPLICATIVO_QUALSIASI;
			
			String label = CostantiControlStation.LABEL_PARAMETRO_APPLICATIVO;
			
			this.pd.addFilter(Filtri.FILTRO_SERVIZIO_APPLICATIVO, label, selectedValue, values, labels, postBack, this.getSize());
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void addFilterTipoCredenziali(String tipo, boolean postBack) throws Exception{
		try {
			String [] tmp_labels = ConnettoriCostanti.CREDENZIALI_LABELS;
			String [] tmp_values = ConnettoriCostanti.CREDENZIALI_VALUES;
			
			String [] values = new String[tmp_values.length + 1];
			String [] labels = new String[tmp_labels.length + 1];
			labels[0] = CostantiControlStation.LABEL_PARAMETRO_TIPO_CREDENZIALI_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_TIPO_CREDENZIALI_QUALSIASI;
			for (int i =0; i < tmp_labels.length ; i ++) {
				labels[i+1] = tmp_labels[i];
				values[i+1] = tmp_values[i];
			}
			
			String selectedValue = tipo != null ? tipo : CostantiControlStation.DEFAULT_VALUE_PARAMETRO_TIPO_CREDENZIALI_QUALSIASI;
			
			String label = ServiziApplicativiCostanti.LABEL_CREDENZIALE_ACCESSO;

			this.pd.addFilter(Filtri.FILTRO_TIPO_CREDENZIALI, label, selectedValue, values, labels, postBack, this.getSize());
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void addFilterCredenziale(String tipoCredenziale, String credenziale) throws Exception{
		try {
			String label = null;
			if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL.equals(tipoCredenziale)) {
				label = ServiziApplicativiCostanti.LABEL_CREDENZIALE_ACCESSO_HTTPS;
			}
			else if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC.equals(tipoCredenziale)) {
				label = ServiziApplicativiCostanti.LABEL_CREDENZIALE_ACCESSO_USERNAME;
			}
			else if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL.equals(tipoCredenziale)) {
				label = ServiziApplicativiCostanti.LABEL_CREDENZIALE_ACCESSO_PRINCIPAL;
			}
			
			if(label!=null) {
				this.pd.addTextFilter(Filtri.FILTRO_CREDENZIALE, label, credenziale, this.getSize());
			}
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void addFilterStato(String stato, boolean addStatiAllarme, boolean postBack) throws Exception{
		try {
			String [] statiValues = new String[addStatiAllarme?5:2];
			statiValues[0] = Filtri.FILTRO_STATO_VALORE_ABILITATO;
			statiValues[1] = Filtri.FILTRO_STATO_VALORE_DISABILITATO;
			if(addStatiAllarme) {
				statiValues[2] = Filtri.FILTRO_STATO_VALORE_OK;
				statiValues[3] = Filtri.FILTRO_STATO_VALORE_WARNING;
				statiValues[4] = Filtri.FILTRO_STATO_VALORE_ERROR;
			}
			String [] statiLabel = new String[addStatiAllarme?5:2];
			statiLabel[0] = Filtri.FILTRO_STATO_VALORE_ABILITATO;
			statiLabel[1] = Filtri.FILTRO_STATO_VALORE_DISABILITATO;
			if(addStatiAllarme) {
				statiLabel[2] = Filtri.FILTRO_STATO_VALORE_OK;
				statiLabel[3] = Filtri.FILTRO_STATO_VALORE_WARNING;
				statiLabel[4] = Filtri.FILTRO_STATO_VALORE_ERROR;
			}
			String [] values = new String[statiValues.length + 1];
			String [] labels = new String[statiValues.length + 1];
			labels[0] = CostantiControlStation.LABEL_PARAMETRO_STATO_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_STATO_QUALSIASI;
			for (int i =0; i < statiValues.length ; i ++) {
				labels[i+1] = statiLabel[i];
				values[i+1] = statiValues[i];
			}
			
			String selectedValue = stato != null ? stato : CostantiControlStation.DEFAULT_VALUE_PARAMETRO_STATO_QUALSIASI;
			
			String label = CostantiControlStation.LABEL_PARAMETRO_STATO;
			
			this.pd.addFilter(Filtri.FILTRO_STATO, label, selectedValue, values, labels, postBack, this.getSize());
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public String getLabelTipoRisorsaPolicyAttiva(TipoRisorsaPolicyAttiva tipo) {
		return this.getLabelTipoRisorsaPolicyAttiva(tipo.getValue());
	}
	public String getLabelTipoRisorsaPolicyAttiva(String tipo) {
		String labelRisorsaPolicyAttiva = null;
		for (int j = 0; j < CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPI_RISORSE_VALORI.length; j++) {
			if(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPI_RISORSE_VALORI[j].equals(tipo)) {
				labelRisorsaPolicyAttiva = CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPI_RISORSE_LABELS[j];
				break;
			}
		}
		return labelRisorsaPolicyAttiva;
	}
	
	public void addFilterTipoRisorsaPolicy(List<TipoRisorsaPolicyAttiva> listaTipoRisorsa, String tipoRisorsaPolicy, boolean postBack) throws Exception{
		try {
			if(listaTipoRisorsa!=null && listaTipoRisorsa.size()>1) {
				String selectedValue = null;
				if(tipoRisorsaPolicy != null) {
					selectedValue = tipoRisorsaPolicy;
				}
				else {
					selectedValue =  CostantiControlStation.DEFAULT_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_RISORSA_TIPO_VALUE.getValue();
				}
	
				List<String> values = new ArrayList<>();
				List<String> labels = new ArrayList<>();
				for (int j = 0; j < CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPI_RISORSE_VALORI.length; j++) {
					TipoRisorsaPolicyAttiva check = TipoRisorsaPolicyAttiva.toEnumConstant(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPI_RISORSE_VALORI[j]);
					if(listaTipoRisorsa.contains(check)) {
						values.add(check.getValue());
						labels.add(this.getLabelTipoRisorsaPolicyAttiva(check));
					}
				}
				
				this.pd.addFilter(Filtri.FILTRO_TIPO_RISORSA_POLICY, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_RISORSA_TIPO, 
						selectedValue, 
						values.toArray(new String[1]), 
						labels.toArray(new String[1]), 
						postBack, this.getSize());
			}
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	public void removeFilterTipoRisorsaPolicy() throws Exception{
		try {
			this.pd.removeFilter(Filtri.FILTRO_TIPO_RISORSA_POLICY);
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void addFilterTipoTokenPolicy(String tipo, boolean postBack,
			List<String> nomiConfigurazioniPolicyGestioneToken, List<String> labelConfigurazioniPolicyGestioneToken) throws Exception{
		try {
			int length = nomiConfigurazioniPolicyGestioneToken.size() +1;
			String [] values = new String[length];
			String [] labels = new String[length];
			labels[0] = CostantiControlStation.LABEL_PARAMETRO_TIPO_TOKEN_POLICY_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_TIPO_TOKEN_POLICY_QUALSIASI;
			if(nomiConfigurazioniPolicyGestioneToken!=null && nomiConfigurazioniPolicyGestioneToken.size()>0) {
				for (int i =0; i < nomiConfigurazioniPolicyGestioneToken.size() ; i ++) {
					labels[i+1] = labelConfigurazioniPolicyGestioneToken.get(i);
					values[i+1] = nomiConfigurazioniPolicyGestioneToken.get(i);
				}
			}
			
			this.pd.addFilter(Filtri.FILTRO_TIPO_TOKEN_POLICY, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPO, tipo, values, labels, postBack, this.getSize());
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	
	
	public void setFilterRuoloServizioApplicativo(ISearch ricerca, int idLista) throws Exception{
		if(this.core.isApplicativiServerEnabled(this)) {
			if(Liste.SERVIZIO_APPLICATIVO==idLista || Liste.SERVIZI_APPLICATIVI_BY_SOGGETTO==idLista) {
				ricerca.addFilter(idLista, Filtri.FILTRO_TIPO_SERVIZIO_APPLICATIVO, CostantiConfigurazione.CLIENT_OR_SERVER);
			}
		} else {
			if( (this.isModalitaCompleta()==false) && 
					(Liste.SERVIZIO_APPLICATIVO==idLista || Liste.SERVIZI_APPLICATIVI_BY_SOGGETTO==idLista)) {
				ricerca.addFilter(idLista, Filtri.FILTRO_RUOLO_SERVIZIO_APPLICATIVO, Filtri.VALUE_FILTRO_RUOLO_SERVIZIO_APPLICATIVO_FRUITORE);
			}
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
	public String addFilterProtocol(ISearch ricerca, int idLista) throws Exception{
		return this.addFilterProtocol(ricerca, idLista, false);
	}
	public String addFilterProtocol(ISearch ricerca, int idLista, boolean postBack) throws Exception{
		List<String> protocolli = this.core.getProtocolli(this.session);
		return _addFilterProtocol(ricerca, idLista, protocolli, postBack);
	}
	
	public String addFilterProtocol(ISearch ricerca, int idLista,List<String> protocolli) throws Exception{
		return addFilterProtocol(ricerca, idLista, protocolli, false);
	}
	public String addFilterProtocol(ISearch ricerca, int idLista,List<String> protocolli, boolean postBack) throws Exception{
		return _addFilterProtocol(ricerca, idLista, protocolli, postBack);
	}

	private String _addFilterProtocol(ISearch ricerca, int idLista, List<String> protocolli, boolean postBack) throws Exception {
		if(protocolli!=null && protocolli.size()>1) {
			String filterProtocol = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLO);
			this.addFilterProtocollo(protocolli, filterProtocol, postBack);
			return filterProtocol;
		}
		return null;
	}
	private void addFilterProtocollo(List<String> protocolli, String protocolloSelected,boolean postBack) throws Exception{
		try {
			
			if(protocolli!=null && protocolli.size()>1) {

				String [] values = new String[protocolli.size() + 1];
				String [] labels = new String[protocolli.size() + 1];
				labels[0] = CostantiControlStation.LABEL_PARAMETRO_PROTOCOLLO_QUALSIASI;
				values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROTOCOLLO_QUALSIASI;
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
	
	public void addFilterSoggetto(String soggetto, String protocollo, boolean soloSoggettiOperativi, boolean postBack) throws Exception{
		try {
			String userLogin = ServletUtils.getUserLoginFromSession(this.session);
			
			Search searchSoggetti = new Search(true);
			searchSoggetti.addFilter(Liste.SOGGETTI, Filtri.FILTRO_PROTOCOLLO, protocollo);
			if(soloSoggettiOperativi) {
				searchSoggetti.addFilter(Liste.SOGGETTI, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE);
			}
			List<org.openspcoop2.core.registry.Soggetto> listSoggetti = null;
			if(this.apsCore.isVisioneOggettiGlobale(userLogin)){
				listSoggetti = this.soggettiCore.soggettiRegistroList(null, searchSoggetti);
			}else{
				listSoggetti = this.soggettiCore.soggettiRegistroList(userLogin, searchSoggetti);
			}
			
			int length = 1;
			if(listSoggetti!=null && listSoggetti.size()>0) {
				length+=listSoggetti.size();
			}
			String [] values = new String[length];
			String [] labels = new String[length];
			labels[0] = CostantiControlStation.LABEL_PARAMETRO_SOGGETTO_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SOGGETTO_QUALSIASI;
			if(listSoggetti!=null && listSoggetti.size()>0) {
				for (int i =0; i < listSoggetti.size() ; i ++) {
					IDSoggetto idSoggetto = new IDSoggetto(listSoggetti.get(i).getTipo(), listSoggetti.get(i).getNome());
					labels[i+1] = this.getLabelNomeSoggetto(idSoggetto);
					values[i+1] = idSoggetto.toString();
				}
			}
			
			this.pd.addFilter(Filtri.FILTRO_SOGGETTO, SoggettiCostanti.LABEL_SOGGETTO, soggetto, values, labels, postBack, this.getSize());
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void addFilterSoggettoErogatoreStringaLiberaContains(ISearch ricerca, int idLista) throws Exception{
		try {
			String soggettoErogatoreValue = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_SOGGETTO_EROGATORE_CONTAINS);
				
			String filterLabel = SoggettiCostanti.LABEL_SOGGETTO_EROGATORE;
			
			this.pd.addTextFilter(Filtri.FILTRO_SOGGETTO_EROGATORE_CONTAINS, filterLabel, soggettoErogatoreValue, this.getSize());
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void addFilterSubtitle(String subtitleName, String subtitleLabel, boolean visualizzaSottosezioneAperta) throws Exception{
		this.pd.addSubtitleFilter(subtitleName, subtitleLabel, visualizzaSottosezioneAperta);
	}
	
	public void impostaAperturaSubtitle(String subtitleName) throws Exception{
		this.pd.impostaAperturaSubtitle(subtitleName, null, this.getPostBackElementName());
	}
	
	public void impostaAperturaSubtitle(String subtitleName, boolean visualizzaSottosezioneAperta) throws Exception{
		this.pd.impostaAperturaSubtitle(subtitleName, visualizzaSottosezioneAperta, this.getPostBackElementName());
	}
	
	public void addFilterHidden(String name, String value) throws Exception{
		this.pd.addHiddenFilter(name, value, this.getSize());
	}
	
	public String addFilterTipoConnettore(ISearch ricerca, int idLista, boolean visualizzaVoceIM) throws Exception{
		return this.addFilterTipoConnettore(ricerca, idLista, visualizzaVoceIM, true);
	}
	public String addFilterTipoConnettore(ISearch ricerca, int idLista, boolean visualizzaVoceIM, boolean postBack) throws Exception{
		String tipoConnettoreValue = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SOGGETTO_QUALSIASI;
		try {
			
			
			List<String> connettoriList = Connettori.getList();
			// rimozione disabilitato
			int indexDisabled = -1;
			for (int i = 0; i < connettoriList.size(); i++) {
				if(TipiConnettore.DISABILITATO.getNome().equals(connettoriList.get(i))) {
					indexDisabled = i;
					break;
				}
			}
			if(indexDisabled>=0) {
				connettoriList.remove(indexDisabled);
			}
			if (!connettoriList.contains(TipiConnettore.HTTPS.toString())) {
				connettoriList.add(TipiConnettore.HTTPS.toString());
			}
			
			Boolean confPers = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_CONFIGURAZIONI_PERSONALIZZATE);
			if(confPers==null) {
				confPers = this.core!=null ? this.core.isShowConfigurazioniPersonalizzate() : true;
			}

			TipologiaConnettori tipologiaConnettori = null;
			try {
				tipologiaConnettori = Utilities.getTipologiaConnettori(this.core);
			} catch (Exception e) {
				// default
				tipologiaConnettori = TipologiaConnettori.TIPOLOGIA_CONNETTORI_ALL;
			}
			
			// custom come da schermata configurazione connettore
			if (confPers && TipologiaConnettori.TIPOLOGIA_CONNETTORI_ALL.equals(tipologiaConnettori))
				connettoriList.add(TipiConnettore.CUSTOM.toString());
			
			List<String> connettoriListLabels = new ArrayList<>();
			for (String tipoConnettore : connettoriList) {
				TipiConnettore tipoC = TipiConnettore.toEnumFromName(tipoConnettore);
				if(tipoC!=null) {
					connettoriListLabels.add(tipoC.getLabel());
				}
				else {
					connettoriListLabels.add(tipoConnettore);
				}
			}
			
			// IM 
			if(visualizzaVoceIM) {
				if(this.core.isIntegrationManagerEnabled()) {
					connettoriList.add(Filtri.FILTRO_CONNETTORE_TIPO_VALORE_IM);
					connettoriListLabels.add(ConnettoriCostanti.LABEL_FILTRO_TIPO_CONNETTORE_IM);
				}
			}
			
			int length = connettoriList.size() + 1;
			
			String [] values = new String[length];
			String [] labels = new String[length];
			labels[0] = CostantiControlStation.LABEL_PARAMETRO_SOGGETTO_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SOGGETTO_QUALSIASI;
			for (int i =0; i < connettoriList.size() ; i ++) {
				labels[i+1] = connettoriListLabels.get(i);
				values[i+1] = connettoriList.get(i);
			}
			
			tipoConnettoreValue = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONNETTORE_TIPO);
			
			this.pd.addFilter(Filtri.FILTRO_CONNETTORE_TIPO, ConnettoriCostanti.LABEL_FILTRO_TIPO_CONNETTORE, tipoConnettoreValue, values, labels, postBack, this.getSize());
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
		
		return tipoConnettoreValue;
	}
	
	public void addFilterConnettorePlugin(ISearch ricerca, int idLista, String tipoConnettore) throws Exception{
		try {
			if(tipoConnettore != null && tipoConnettore.equals(TipiConnettore.CUSTOM.toString())) {
				
				String pluginValue = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONNETTORE_TIPO_PLUGIN);
				String pluginLabel = "";
			
				Vector<DataElement> dati = new Vector<DataElement>();
				
				this.addCustomFieldSearchForm(TipoPlugin.CONNETTORE,
						null,
						null,
						Filtri.FILTRO_CONNETTORE_TIPO,
						Filtri.FILTRO_CONNETTORE_TIPO_PLUGIN, 
						pluginLabel, 
						pluginValue, false, dati,
						false); 
				
				// dentro dati c'e' solo un elemento
				DataElement dataElement = dati.get(0);
				
				if(dataElement.getValues() != null && dataElement.getValues().length > 0) {
					this.pd.addFilter(Filtri.FILTRO_CONNETTORE_TIPO_PLUGIN, pluginLabel, pluginValue, dataElement.getValues(), dataElement.getLabels(), false, this.getSize());
				}
			}
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void addFilterConnettoreTokenPolicy(ISearch ricerca, int idLista, String tipoConnettore) throws Exception{
		try {
			if(tipoConnettore != null &&
				(tipoConnettore.equals("") || 
						tipoConnettore.equals(TipiConnettore.HTTPS.toString())|| 
						tipoConnettore.equals(TipiConnettore.HTTP.toString()))) {
			
				// controllo esistenza token policy
				List<GenericProperties> gestorePolicyTokenList = this.confCore.gestorePolicyTokenList(null, ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_RETRIEVE_POLICY_TOKEN, null);
			
				if(gestorePolicyTokenList.size() > 0) {
					int length = gestorePolicyTokenList.size() + 1;
					
					String [] values = new String[length];
					String [] labels = new String[length];
					labels[0] = CostantiControlStation.LABEL_PARAMETRO_SOGGETTO_QUALSIASI;
					values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SOGGETTO_QUALSIASI;
				
					for (int i = 0; i < gestorePolicyTokenList.size(); i++) {
						GenericProperties genericProperties = gestorePolicyTokenList.get(i);
						labels[(i+1)] = genericProperties.getNome();
						values[(i+1)] = genericProperties.getNome();
					}
					
					String tokenPolicyValue = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONNETTORE_TOKEN_POLICY);
					
					this.pd.addFilter(Filtri.FILTRO_CONNETTORE_TOKEN_POLICY, ConnettoriCostanti.LABEL_FILTRO_CONNETTORE_TOKEN_POLICY, tokenPolicyValue, values, labels, false, this.getSize());
				}
			}
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void addFilterConnettoreEndpoint(ISearch ricerca, int idLista, String tipoConnettore) throws Exception{
		try {
			if(tipoConnettore != null &&
				(tipoConnettore.equals("") || 
						tipoConnettore.equals(TipiConnettore.HTTPS.toString())|| 
						tipoConnettore.equals(TipiConnettore.HTTP.toString())|| 
						tipoConnettore.equals(TipiConnettore.JMS.toString())|| 
						tipoConnettore.equals(TipiConnettore.FILE.toString()))) {
			
				String endpointValue = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONNETTORE_ENDPOINT);
				
				String filterLabel = ConnettoriCostanti.LABEL_FILTRO_CONNETTORE_ENDPOINT;
				
				if(tipoConnettore.equals(TipiConnettore.JMS.toString())) {
					filterLabel = ConnettoriCostanti.LABEL_FILTRO_CONNETTORE_ENDPOINT_JMS;
				}
				
				if(tipoConnettore.equals(TipiConnettore.FILE.toString())) {
					filterLabel = ConnettoriCostanti.LABEL_FILTRO_CONNETTORE_ENDPOINT_FILE;
				}
					
				this.pd.addTextFilter(Filtri.FILTRO_CONNETTORE_ENDPOINT, filterLabel, endpointValue, this.getSize());
			}
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void addFilterConnettoreKeystore(ISearch ricerca, int idLista, String tipoConnettore) throws Exception{
		try {
			if(tipoConnettore != null &&
				(tipoConnettore.equals(TipiConnettore.HTTPS.toString()))) {
			
				String keystoreValue = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONNETTORE_KEYSTORE);
				
				String filterLabel = ConnettoriCostanti.LABEL_FILTRO_CONNETTORE_KEYSTORE;
					
				this.pd.addTextFilter(Filtri.FILTRO_CONNETTORE_KEYSTORE, filterLabel, keystoreValue, this.getSize());
			}
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public String addFilterNomeConnettoreMultiplo(ISearch ricerca, int idLista) throws Exception{
		try {
			String nomeValue = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONNETTORE_MULTIPLO_NOME);
			
			String filterLabel = ConnettoriCostanti.LABEL_FILTRO_CONNETTORE_MULTIPLO_NOME;
				
			this.pd.addTextFilter(Filtri.FILTRO_CONNETTORE_MULTIPLO_NOME, filterLabel, nomeValue, this.getSize());
			
			return nomeValue;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void addFilterFiltroConnettoreMultiplo(ISearch ricerca, int idLista, boolean behaviourConFiltri) throws Exception{
		try {
			if(behaviourConFiltri) {
			
				String filtroValue = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONNETTORE_MULTIPLO_FILTRO);
				
				String filterLabel = ConnettoriCostanti.LABEL_FILTRO_CONNETTORE_MULTIPLO_FILTRO;
					
				this.pd.addTextFilter(Filtri.FILTRO_CONNETTORE_MULTIPLO_FILTRO, filterLabel, filtroValue, this.getSize());
			}
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void addFilterModIKeystorePath(ISearch ricerca, int idLista) throws Exception{
		try {
			
			String keystorePathValue = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_KEYSTORE_PATH);
			String filterPathLabel = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_PATH_MODE_LABEL;
			this.pd.addTextFilter(Filtri.FILTRO_MODI_KEYSTORE_PATH, filterPathLabel, keystorePathValue, this.getSize());
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void addFilterModIKeystoreSubject(ISearch ricerca, int idLista) throws Exception{
		try {
			
			String keystoreSubjectValue = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_KEYSTORE_SUBJECT);
			String filterSubjectLabel = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_SUBJECT_MODE_LABEL;
			this.pd.addTextFilter(Filtri.FILTRO_MODI_KEYSTORE_SUBJECT, filterSubjectLabel, keystoreSubjectValue, this.getSize());
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void addFilterModIAudience(ISearch ricerca, int idLista, boolean applicativi, String filterTipoAccordo, String filterDominio) throws Exception{
		try {
			String audienceValue = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_AUDIENCE);
			
			String filterLabel = null;
			if(applicativi) {
				if(SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE.equals(filterDominio)) {
					filterLabel = CostantiLabel.MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_INFO_DOMINIO_ESTERNO_LABEL;
				}
				else {
					filterLabel = CostantiLabel.MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_INFO_DOMINIO_INTERNO_LABEL;
				}
			}
			else {
				if(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_REST.toLowerCase().equals(filterTipoAccordo)) {
					filterLabel = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_REST_LABEL;
				}
				else if(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_SOAP.toLowerCase().equals(filterTipoAccordo)) {
					filterLabel = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_SOAP_LABEL;
				}
				else {
					filterLabel = CostantiLabel.MODIPA_APPLICATIVI_AUDIENCE_WSATO_LABEL;
				}
			}
				
			this.pd.addTextFilter(Filtri.FILTRO_MODI_AUDIENCE, filterLabel, audienceValue, this.getSize());
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void addFilterModISicurezzaCanale(ISearch ricerca, int idLista) throws Exception{
		try {
			// controllo esistenza token policy
			List<String> labelsList = this.getProfiloModIPAFiltroSicurezzaCanaleLabels();
			List<String> valuesList = this.getProfiloModIPAFiltroSicurezzaCanaleValues();
			int length = valuesList.size() + 1;
				
			String [] values = new String[length];
			String [] labels = new String[length];
			labels[0] = CostantiControlStation.LABEL_PARAMETRO_SOGGETTO_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SOGGETTO_QUALSIASI;
			
			for (int i = 0; i < valuesList.size(); i++) {
				labels[(i+1)] = labelsList.get(i);
				values[(i+1)] = valuesList.get(i);
			}
			
			String sicurezzaCanaleValue = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_SICUREZZA_CANALE);
			
			this.pd.addFilter(Filtri.FILTRO_MODI_SICUREZZA_CANALE, CostantiLabel.MODIPA_API_PROFILO_CANALE_LABEL, sicurezzaCanaleValue, values, labels, false, this.getSize());
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void addFilterModISicurezzaMessaggio(ISearch ricerca, int idLista, String serviceBindingValue) throws Exception{
		try {
			// controllo esistenza token policy
			List<String> labelsList = this.getProfiloModIPAFiltroSicurezzaMessaggioLabels(serviceBindingValue);
			List<String> valuesList = this.getProfiloModIPAFiltroSicurezzaMessaggioValues();
			int length = valuesList.size() + 1;
				
			String [] values = new String[length];
			String [] labels = new String[length];
			labels[0] = CostantiControlStation.LABEL_PARAMETRO_SOGGETTO_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SOGGETTO_QUALSIASI;
			
			for (int i = 0; i < valuesList.size(); i++) {
				labels[(i+1)] = labelsList.get(i);
				values[(i+1)] = valuesList.get(i);
			}
			
			String sicurezzaCanaleValue = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_SICUREZZA_MESSAGGIO);
			
			this.pd.addFilter(Filtri.FILTRO_MODI_SICUREZZA_MESSAGGIO, CostantiLabel.MODIPA_API_PROFILO_SICUREZZA_MESSAGGIO_LABEL, sicurezzaCanaleValue, values, labels, false, this.getSize());
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public String addFilterModISicurezzaMessaggioSA(ISearch ricerca, int idLista, boolean postback) throws Exception{
		try {
			//List<String> valuesList = Arrays.asList(CostantiControlStation.SELECT_VALUES_STATO_FUNZIONALITA);
			// non ha senso in modi controllare quelli disabilitati
			String [] valueAbilitatoOnly = {StatoFunzionalita.ABILITATO.getValue()};
			List<String> valuesList = Arrays.asList(valueAbilitatoOnly);
			int length = valuesList.size() + 1;
				
			String [] values = new String[length];
			String [] labels = new String[length];
			labels[0] = CostantiControlStation.LABEL_PARAMETRO_SOGGETTO_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SOGGETTO_QUALSIASI;
			
			for (int i = 0; i < valuesList.size(); i++) {
				labels[(i+1)] = valuesList.get(i);
				values[(i+1)] = valuesList.get(i);
			}
			
			String sicurezzaMessaggioValue = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_SICUREZZA_MESSAGGIO);
			
			this.pd.addFilter(Filtri.FILTRO_MODI_SICUREZZA_MESSAGGIO, CostantiLabel.MODIPA_API_PROFILO_SICUREZZA_MESSAGGIO_LABEL, sicurezzaMessaggioValue, values, labels, postback, this.getSize());
			
			return sicurezzaMessaggioValue;
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void addFilterModIDigestRichiesta(ISearch ricerca, int idLista) throws Exception{
		try {
			//List<String> valuesList = Arrays.asList(CostantiControlStation.SELECT_VALUES_STATO_FUNZIONALITA);
			// non ha senso in modi controllare quelli disabilitati
			String [] valueAbilitatoOnly = {StatoFunzionalita.ABILITATO.getValue()};
			List<String> valuesList = Arrays.asList(valueAbilitatoOnly);
			int length = valuesList.size() + 1;
				
			String [] values = new String[length];
			String [] labels = new String[length];
			labels[0] = CostantiControlStation.LABEL_PARAMETRO_SOGGETTO_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SOGGETTO_QUALSIASI;
			
			for (int i = 0; i < valuesList.size(); i++) {
				labels[(i+1)] = valuesList.get(i);
				values[(i+1)] = valuesList.get(i);
			}
			
			String digestRichiestaValue = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_DIGEST_RICHIESTA);
			
			this.pd.addFilter(Filtri.FILTRO_MODI_DIGEST_RICHIESTA, CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REQUEST_DIGEST_LABEL, 
					digestRichiestaValue, values, labels, false, this.getSize());
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void addFilterModIInfoUtente(ISearch ricerca, int idLista) throws Exception{
		try {
			//List<String> valuesList = Arrays.asList(CostantiControlStation.SELECT_VALUES_STATO_FUNZIONALITA);
			// non ha senso in modi controllare quelli disabilitati
			String [] valueAbilitatoOnly = {StatoFunzionalita.ABILITATO.getValue()};
			List<String> valuesList = Arrays.asList(valueAbilitatoOnly);
			int length = valuesList.size() + 1;
				
			String [] values = new String[length];
			String [] labels = new String[length];
			labels[0] = CostantiControlStation.LABEL_PARAMETRO_SOGGETTO_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SOGGETTO_QUALSIASI;
			
			for (int i = 0; i < valuesList.size(); i++) {
				labels[(i+1)] = valuesList.get(i);
				values[(i+1)] = valuesList.get(i);
			}
			
			String infoUtenteValue = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_INFORMAZIONI_UTENTE);
			
			this.pd.addFilter(Filtri.FILTRO_MODI_INFORMAZIONI_UTENTE, CostantiControlStation.LABEL_FILTRO_MODIPA_INFO_UTENTE, infoUtenteValue, values, labels, false, this.getSize());
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public List<String> getProfiloModIPAFiltroSicurezzaCanaleValues() {
		try {
			List<String> values = new ArrayList<String>();
			values.add(CostantiDB.MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC01);
			values.add(CostantiDB.MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC02);
			return values;
		}catch(Throwable t) {
			return null;
		}
	}
	
	public List<String> getProfiloModIPAFiltroSicurezzaCanaleLabels() {
		try {
			List<String> labels = new ArrayList<String>();
			labels.add(CostantiLabel.MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC01);
			labels.add(CostantiLabel.MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC02);
			return labels;
		}catch(Throwable t) {
			return null;
		}
	}
	
	public List<String> getProfiloModIPAFiltroSicurezzaMessaggioValues() {
		try {
			List<String> values = new ArrayList<String>();
			values.add(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01);
			values.add(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02);
			values.add(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301);
			values.add(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302);
			return values;
		}catch(Throwable t) {
			return null;
		}
	}
	
	public List<String> getProfiloModIPAFiltroSicurezzaMessaggioLabels(String serviceBindingValue) {
		try {
			List<String> labels = new ArrayList<String>();
			
			if(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_REST.toLowerCase().equals(serviceBindingValue) ) {
				labels.add(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_REST);
				labels.add(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_REST);
				labels.add(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_REST);
				labels.add(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_REST);
			} 
			else if(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_SOAP.toLowerCase().equals(serviceBindingValue) ) {
				labels.add(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_SOAP);
				labels.add(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_SOAP);
				labels.add(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_SOAP);
				labels.add(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_SOAP);
			}
			else {
				labels.add(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01);
				labels.add(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02);
				labels.add(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301);
				labels.add(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302);
			}
			
			return labels;
		}catch(Throwable t) {
			return null;
		}
	}
	
	
	public void addFilterProprietaNome(ISearch ricerca, int idLista, List<String> nomiProprieta) throws Exception{
		try {
			// controllo esistenza token policy
			int length = nomiProprieta.size() + 1;
				
			String [] values = new String[length];
			String [] labels = new String[length];
			labels[0] = CostantiControlStation.LABEL_PARAMETRO_SOGGETTO_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SOGGETTO_QUALSIASI;
			
			for (int i = 0; i < nomiProprieta.size(); i++) {
				labels[(i+1)] = nomiProprieta.get(i);
				values[(i+1)] = nomiProprieta.get(i);
			}
			
			String nomeProprietaValue = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROPRIETA_NOME);
			
			this.pd.addFilter(Filtri.FILTRO_PROPRIETA_NOME, CostantiControlStation.LABEL_FILTRO_PROPRIETA_NOME, nomeProprietaValue, values, labels, false, this.getSize());
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public void addFilterProprietaValore(ISearch ricerca, int idLista, List<String> nomiProprieta) throws Exception{
		try {
			String valoreProprietaValue = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROPRIETA_VALORE);
			
			String filterLabel = CostantiControlStation.LABEL_FILTRO_PROPRIETA_VALORE;
				
			this.pd.addTextFilter(Filtri.FILTRO_PROPRIETA_VALORE, filterLabel, valoreProprietaValue, this.getSize());
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
	
	
	// APPLICATIVI
	
	public String getLabelServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws Exception{
		return NamingUtils.getLabelServizioApplicativo(idServizioApplicativo);
	}
	public String getLabelServizioApplicativo(String protocollo, IDServizioApplicativo idServizioApplicativo) throws Exception{
		return NamingUtils.getLabelServizioApplicativo(protocollo, idServizioApplicativo);
	}
	public String getLabelServizioApplicativoConDominioSoggetto(IDServizioApplicativo idServizioApplicativo) throws Exception{
		return NamingUtils.getLabelServizioConDominioErogatore(idServizioApplicativo.getNome(),NamingUtils.getLabelSoggetto(idServizioApplicativo.getIdSoggettoProprietario()));
	}
	public String getLabelServizioApplicativoConDominioSoggetto(String protocollo, IDServizioApplicativo idServizioApplicativo) throws Exception{
		return NamingUtils.getLabelServizioConDominioErogatore(idServizioApplicativo.getNome(),NamingUtils.getLabelSoggetto(protocollo, idServizioApplicativo.getIdSoggettoProprietario()));
	}
	
	
	// LABEL API
	
	public String getLabelIdAccordo(AccordoServizioParteComune as) throws Exception{
		return NamingUtils.getLabelAccordoServizioParteComune(as);
	}
	public String getLabelIdAccordo(AccordoServizioParteComuneSintetico as) throws Exception{
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
		//String labelServizio = this.getLabelIdServizio(protocollo, idServizio);
		String servizio = this.getLabelIdServizioSenzaErogatore(protocollo, idServizio);
		String soggetto = this.getLabelNomeSoggetto(protocollo, idServizio.getSoggettoErogatore());
		String labelServizio = NamingUtils.getLabelServizioConDominioErogatore(servizio, soggetto);
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
			//return this.getLabelIdServizio(protocollo, idServizio);
			String servizio = this.getLabelIdServizioSenzaErogatore(protocollo, idServizio);
			String soggetto = this.getLabelNomeSoggetto(protocollo, idServizio.getSoggettoErogatore());
			return NamingUtils.getLabelServizioConDominioErogatore(servizio, soggetto);
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
			labels[0] = CostantiControlStation.LABEL_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_AZIONE_RISORSA_NON_SELEZIONATA;
			for (int i =0; i < azioni.length ; i ++) {
				labels[i+1] = azioniLabels[i];
				values[i+1] = azioni[i];
			}
			
			String selectedValue = StringUtils.isNotEmpty(azione) ? azione : CostantiControlStation.DEFAULT_VALUE_AZIONE_RISORSA_NON_SELEZIONATA;
			
			this.pd.addFilter(Filtri.FILTRO_AZIONE, 
					this.getLabelAzione(serviceBinding),
					selectedValue, values, labels, false, this.getSize());
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	
	public void addConfigurazioneDumpToDati(TipoOperazione tipoOperazione,Vector<DataElement> dati, boolean showStato, String statoDump, boolean showRealtime, String realtime, String statoDumpRichiesta, String statoDumpRisposta,
			String dumpRichiestaIngressoHeader, String dumpRichiestaIngressoPayload, String dumpRichiestaIngressoPayloadParsing, String dumpRichiestaIngressoBody, String dumpRichiestaIngressoAttachments, 
			String dumpRichiestaUscitaHeader, String dumpRichiestaUscitaPayload, String dumpRichiestaUscitaPayloadParsing, String dumpRichiestaUscitaBody, String dumpRichiestaUscitaAttachments, 
			String dumpRispostaIngressoHeader, String dumpRispostaIngressoPayload, String dumpRispostaIngressoPayloadParsing, String dumpRispostaIngressoBody , String dumpRispostaIngressoAttachments,
			String dumpRispostaUscitaHeader, String dumpRispostaUscitaPayload, String dumpRispostaUscitaPayloadParsing, String dumpRispostaUscitaBody, String dumpRispostaUscitaAttachments,
			boolean portaApplicativa) throws Exception{
		
		// TipoPdD
		DataElement de = new DataElement();
		de.setName(CostantiControlStation.PARAMETRO_DUMP_TIPO_CONFIGURAZIONE); 
		de.setType(DataElementType.HIDDEN);
		de.setValue(portaApplicativa ? TipoPdD.APPLICATIVA.getTipo() : TipoPdD.DELEGATA.getTipo());
		dati.addElement(de);
		
		if(showStato || showRealtime) {
			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_GENERALE);
			dati.addElement(de);
		}
			
		String valuesProp [] = {StatoFunzionalita.ABILITATO.getValue(), StatoFunzionalita.DISABILITATO.getValue()};
		String labelsProp [] = {CostantiControlStation.DEFAULT_VALUE_ABILITATO, CostantiControlStation.DEFAULT_VALUE_DISABILITATO};
		
		// stato generale dump
		de = new DataElement();
		de.setName(CostantiControlStation.PARAMETRO_DUMP_STATO); 
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO);
		if(showStato) {
			
			de.setType(DataElementType.SELECT);
			String valuesStato [] = {CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT, CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO};
			String labelsStato [] = {this.getDumpLabelDefault(true, portaApplicativa), CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO_RIDEFINITO};
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
				addSottoSezioneDump(dati, 
						valuesProp, labelsProp, 
						CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_INGRESSO,
						CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_HEADERS, CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_HEADERS,dumpRichiestaIngressoHeader,
						CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_PAYLOAD, CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_PAYLOAD, dumpRichiestaIngressoPayload,
						CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_PAYLOAD_PARSING, CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_PAYLOAD_PARSING, dumpRichiestaIngressoPayloadParsing,
						CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_BODY, CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_BODY, dumpRichiestaIngressoBody,
						CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_ATTACHMENTS, CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_ATTACHMENTS, dumpRichiestaIngressoAttachments
						);

								
				// sotto sezione uscita
				addSottoSezioneDump(dati, 
						valuesProp, labelsProp, 
						CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_USCITA,
						CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_HEADERS, CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_USCITA_HEADERS,dumpRichiestaUscitaHeader,
						CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_PAYLOAD, CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_USCITA_PAYLOAD, dumpRichiestaUscitaPayload,
						CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_PAYLOAD_PARSING, CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_USCITA_PAYLOAD_PARSING, dumpRichiestaUscitaPayloadParsing,
						CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_BODY, CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_USCITA_BODY, dumpRichiestaUscitaBody,
						CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_ATTACHMENTS, CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_USCITA_ATTACHMENTS, dumpRichiestaUscitaAttachments
						);

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
				addSottoSezioneDump(dati, 
						valuesProp, labelsProp, 
						CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_INGRESSO,
						CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_HEADERS, CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_INGRESSO_HEADERS,dumpRispostaIngressoHeader,
						CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_PAYLOAD, CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_INGRESSO_PAYLOAD, dumpRispostaIngressoPayload,
						CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_PAYLOAD_PARSING, CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_INGRESSO_PAYLOAD_PARSING, dumpRispostaIngressoPayloadParsing,
						CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_BODY, CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_INGRESSO_BODY, dumpRispostaIngressoBody,
						CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_ATTACHMENTS, CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_INGRESSO_ATTACHMENTS, dumpRispostaIngressoAttachments
						);

								
				// sotto sezione uscita
				addSottoSezioneDump(dati, 
						valuesProp, labelsProp, 
						CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_USCITA,
						CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_HEADERS, CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_USCITA_HEADERS,dumpRispostaUscitaHeader,
						CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_PAYLOAD, CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_USCITA_PAYLOAD, dumpRispostaUscitaPayload,
						CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_PAYLOAD_PARSING, CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_USCITA_PAYLOAD_PARSING, dumpRispostaUscitaPayloadParsing,
						CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_BODY, CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_USCITA_BODY, dumpRispostaUscitaBody,
						CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_ATTACHMENTS, CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_USCITA_ATTACHMENTS, dumpRispostaUscitaAttachments
						);

			}
		}
	}
	
	private void addSottoSezioneDump(Vector<DataElement> dati, String valuesProp [], String labelsProp [], 
			String labelSezione,
			String paramHeaders, String labelHeaders, String dumpHeader,
			String paramPayload, String labelPayload, String dumpPayload,
			String paramPayloadParsing, String labelPayloadParsing, String dumpPayloadParsing,
			String paramBody, String labelBody, String dumpBody,
			String paramAttachments, String labelAttachments, String dumpAttachments) throws UtilsException {
		
		DataElement de = new DataElement();
		de.setType(DataElementType.SUBTITLE);
		de.setLabel(labelSezione);
		dati.addElement(de);
		
		// header 
		de = new DataElement();
		de.setName(paramHeaders);
		de.setLabel(labelHeaders);
		de.setType(DataElementType.SELECT);
		de.setSelected(dumpHeader);
		de.setLabels(labelsProp);
		de.setValues(valuesProp);
		dati.addElement(de);
		
		boolean multipartPayloadParsing = this.core.isRegistrazioneMessaggi_multipartPayloadParsing_enabled();
		if(!multipartPayloadParsing) {
			// regole configurate con modalita' attiva
			if(StatoFunzionalita.ABILITATO.getValue().equals(dumpPayload) && 
					StatoFunzionalita.ABILITATO.getValue().equals(dumpPayloadParsing)) {
				multipartPayloadParsing = true;
			}
		}
		
		// payload
		de = new DataElement();
		de.setName(paramPayload);
		de.setLabel(labelPayload);
		de.setType(DataElementType.SELECT);
		de.setSelected(dumpPayload);
		de.setLabels(labelsProp);
		de.setValues(valuesProp);
		if(multipartPayloadParsing) {
			de.setPostBack(true);
		}
		dati.addElement(de);
		
		// payload parsing ingresso
		de = new DataElement();
		de.setName(paramPayloadParsing);
		de.setLabel(labelPayloadParsing);
		de.setValue(dumpPayloadParsing);
		if(multipartPayloadParsing && dumpPayload!=null && dumpPayload.equals(StatoFunzionalita.ABILITATO.getValue())) {
			de.setType(DataElementType.SELECT);
			de.setSelected(dumpPayloadParsing);
			de.setLabels(labelsProp);
			de.setValues(valuesProp);
			de.setPostBack(true);
		}
		else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(StatoFunzionalita.DISABILITATO.getValue());
		}
		dati.addElement(de);
		
		// body ingresso
		de = new DataElement();
		de.setName(paramBody);
		de.setLabel(labelBody);
		de.setValue(dumpBody);
		if(multipartPayloadParsing && dumpPayloadParsing!=null && dumpPayloadParsing.equals(StatoFunzionalita.ABILITATO.getValue())) {
			de.setType(DataElementType.SELECT);
			de.setSelected(dumpBody);
			de.setLabels(labelsProp);
			de.setValues(valuesProp);
		}
		else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(StatoFunzionalita.DISABILITATO.getValue());
		}
		dati.addElement(de);
		
		// attachments ingresso
		de = new DataElement();
		de.setName(paramAttachments);
		de.setLabel(labelAttachments);
		de.setValue(dumpAttachments);
		if(multipartPayloadParsing && dumpPayloadParsing!=null && dumpPayloadParsing.equals(StatoFunzionalita.ABILITATO.getValue())) {
			de.setType(DataElementType.SELECT);
			de.setSelected(dumpAttachments);
			de.setLabels(labelsProp);
			de.setValues(valuesProp);
		}
		else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(StatoFunzionalita.DISABILITATO.getValue());
		}
		dati.addElement(de);
	}
	
	protected String getGestioneCorsLabelDefault(boolean usePrefixDefault) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		
		StringBuilder bf = new StringBuilder();
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
		
		StringBuilder bf = new StringBuilder();
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
	
	protected String getDumpLabelDefault(boolean usePrefixDefault, boolean portaApplicativa) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		String labelDefault = CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO_DEFAULT;
		
		Dump dConfig = this.confCore.getConfigurazioneGenerale().getDump();
		DumpConfigurazione dumpConfigurazione = null;
		if(dConfig!=null) {
			dumpConfigurazione = portaApplicativa ? dConfig.getConfigurazionePortaApplicativa() : dConfig.getConfigurazionePortaDelegata();
		}
		
		if(dConfig==null || dumpConfigurazione==null) {
			if(usePrefixDefault) {
				labelDefault = CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO_DEFAULT +" (disabilitato)";
			}
			else {
				labelDefault = "disabilitato";
			}
		}
		else {
			boolean richiesta = 
					(
						dumpConfigurazione.getRichiestaIngresso()!=null 
						&& 
						(
								StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRichiestaIngresso().getHeaders())
								||
								(StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRichiestaIngresso().getPayload()) 
										&&
										(
										StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRichiestaIngresso().getBody())
										||
										StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRichiestaIngresso().getAttachments())
										)
								)
					    )
					)
					||
					(
						dumpConfigurazione.getRichiestaUscita()!=null 
						&& 
						(
								StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRichiestaUscita().getHeaders())
								||
								(StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRichiestaUscita().getPayload()) 
										&&
										(
										StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRichiestaUscita().getBody())
										||
										StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRichiestaUscita().getAttachments())
										)
								)
						)
					)
					;
			boolean risposta = 
				(
					dumpConfigurazione.getRispostaIngresso()!=null 
					&& 
					(
							StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRispostaIngresso().getHeaders())
							||
							(StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRispostaIngresso().getPayload()) 
									&&
									(
									StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRispostaIngresso().getBody())
									||
									StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRispostaIngresso().getAttachments())
									)
							)
					)
				)
				||
				(
					dumpConfigurazione.getRispostaUscita()!=null 
					&& 
					(
							StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRispostaUscita().getHeaders())
							||
							(StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRispostaUscita().getPayload()) 
									&&
									(
									StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRispostaUscita().getBody())
									||
									StatoFunzionalita.ABILITATO.equals(dumpConfigurazione.getRispostaUscita().getAttachments())
									)
							)
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
			String dumpRichiestaIngressoHeader, String dumpRichiestaIngressoPayload, String dumpRichiestaIngressoPayloadParsing, String dumpRichiestaIngressoBody, String dumpRichiestaIngressoAttachments, 
			String dumpRichiestaUscitaHeader, String dumpRichiestaUscitaPayload, String dumpRichiestaUscitaPayloadParsing, String dumpRichiestaUscitaBody, String dumpRichiestaUscitaAttachments, 
			String dumpRispostaIngressoHeader, String dumpRispostaIngressoPayload, String dumpRispostaIngressoPayloadParsing, String dumpRispostaIngressoBody , String dumpRispostaIngressoAttachments,
			String dumpRispostaUscitaHeader, String dumpRispostaUscitaPayload, String dumpRispostaUscitaPayloadParsing, String dumpRispostaUscitaBody, String dumpRispostaUscitaAttachments,
			boolean portaApplicativa) throws Exception{
		
		// stato generale dump
		DataElement de = new DataElement();
		de.setName(CostantiControlStation.PARAMETRO_DUMP_STATO); 
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO);
		de.setType(DataElementType.HIDDEN);
		de.setValue(statoDump);
		dati.addElement(de);
		
		// TipoPdD
		de = new DataElement();
		de.setName(CostantiControlStation.PARAMETRO_DUMP_TIPO_CONFIGURAZIONE); 
		de.setType(DataElementType.HIDDEN);
		de.setValue(portaApplicativa ? TipoPdD.APPLICATIVA.getTipo() : TipoPdD.DELEGATA.getTipo());
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
				
				// sotto sezione ingresso
				addSottoSezioneDumpHidden(dati, 
						CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_HEADERS, CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_HEADERS,dumpRichiestaIngressoHeader,
						CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_PAYLOAD, CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_PAYLOAD, dumpRichiestaIngressoPayload,
						CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_PAYLOAD_PARSING, CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_PAYLOAD_PARSING, dumpRichiestaIngressoPayloadParsing,
						CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_BODY, CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_BODY, dumpRichiestaIngressoBody,
						CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_ATTACHMENTS, CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_ATTACHMENTS, dumpRichiestaIngressoAttachments
						);

								
				// sotto sezione uscita
				addSottoSezioneDumpHidden(dati, 
						CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_HEADERS, CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_USCITA_HEADERS,dumpRichiestaUscitaHeader,
						CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_PAYLOAD, CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_USCITA_PAYLOAD, dumpRichiestaUscitaPayload,
						CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_PAYLOAD_PARSING, CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_USCITA_PAYLOAD_PARSING, dumpRichiestaUscitaPayloadParsing,
						CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_BODY, CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_USCITA_BODY, dumpRichiestaUscitaBody,
						CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_ATTACHMENTS, CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_USCITA_ATTACHMENTS, dumpRichiestaUscitaAttachments
						);
				
			}
			

			// Stato Dump Richiesta
			de = new DataElement();
			de.setName(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_STATO); 
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_STATO);
			de.setType(DataElementType.HIDDEN);
			de.setValue(statoDumpRisposta);
			dati.addElement(de);
			
			if(statoDumpRisposta.equals(StatoFunzionalita.ABILITATO.getValue())) {
				
				// sotto sezione ingresso
				addSottoSezioneDumpHidden(dati, 
						CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_HEADERS, CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_INGRESSO_HEADERS,dumpRispostaIngressoHeader,
						CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_PAYLOAD, CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_INGRESSO_PAYLOAD, dumpRispostaIngressoPayload,
						CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_PAYLOAD_PARSING, CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_INGRESSO_PAYLOAD_PARSING, dumpRispostaIngressoPayloadParsing,
						CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_BODY, CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_INGRESSO_BODY, dumpRispostaIngressoBody,
						CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_ATTACHMENTS, CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_INGRESSO_ATTACHMENTS, dumpRispostaIngressoAttachments
						);

								
				// sotto sezione uscita
				addSottoSezioneDumpHidden(dati, 
						CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_HEADERS, CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_USCITA_HEADERS,dumpRispostaUscitaHeader,
						CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_PAYLOAD, CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_USCITA_PAYLOAD, dumpRispostaUscitaPayload,
						CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_PAYLOAD_PARSING, CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_USCITA_PAYLOAD_PARSING, dumpRispostaUscitaPayloadParsing,
						CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_BODY, CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_USCITA_BODY, dumpRispostaUscitaBody,
						CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_ATTACHMENTS, CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_USCITA_ATTACHMENTS, dumpRispostaUscitaAttachments
						);

			}
		}
	}
	
	private void addSottoSezioneDumpHidden(Vector<DataElement> dati, 
			String paramHeaders, String labelHeaders, String dumpHeader,
			String paramPayload, String labelPayload, String dumpPayload,
			String paramPayloadParsing, String labelPayloadParsing, String dumpPayloadParsing,
			String paramBody, String labelBody, String dumpBody,
			String paramAttachments, String labelAttachments, String dumpAttachments) {
		
		// header
		DataElement de = new DataElement();
		de.setName(paramHeaders);
		de.setLabel(labelHeaders);
		de.setType(DataElementType.HIDDEN);
		de.setValue(dumpHeader);
		dati.addElement(de);
		
		// payload
		de = new DataElement();
		de.setName(paramPayload);
		de.setLabel(labelPayload);
		de.setType(DataElementType.HIDDEN);
		de.setValue(dumpPayload);
		dati.addElement(de);
		
		// payload parsing
		de = new DataElement();
		de.setName(paramPayloadParsing);
		de.setLabel(labelPayloadParsing);
		de.setType(DataElementType.HIDDEN);
		de.setValue(dumpPayloadParsing);
		dati.addElement(de);
		
		// body
		de = new DataElement();
		de.setName(paramBody);
		de.setLabel(labelBody);
		de.setType(DataElementType.HIDDEN);
		de.setValue(dumpBody);
		dati.addElement(de);
		
		// attachments
		de = new DataElement();
		de.setName(paramAttachments);
		de.setLabel(labelAttachments);
		de.setType(DataElementType.HIDDEN);
		de.setValue(dumpAttachments);
		dati.addElement(de);
	}
	
	public boolean checkDataConfigurazioneDump(TipoOperazione tipoOperazione,boolean showStato, String statoDump, boolean showRealtime, String realtime, String statoDumpRichiesta, String statoDumpRisposta,
			String dumpRichiestaIngressoHeader, String dumpRichiestaIngressoPayload, String dumpRichiestaIngressoPayloadParsing, String dumpRichiestaIngressoBody, String dumpRichiestaIngressoAttachments, 
			String dumpRichiestaUscitaHeader, String dumpRichiestaUscitaPayload, String dumpRichiestaUscitaPayloadParsing, String dumpRichiestaUscitaBody, String dumpRichiestaUscitaAttachments, 
			String dumpRispostaIngressoHeader, String dumpRispostaIngressoPayload, String dumpRispostaIngressoPayloadParsing, String dumpRispostaIngressoBody , String dumpRispostaIngressoAttachments,
			String dumpRispostaUscitaHeader, String dumpRispostaUscitaPayload, String dumpRispostaUscitaPayloadParsing, String dumpRispostaUscitaBody, String dumpRispostaUscitaAttachments) throws Exception{
		
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
				// dumpRichiestaIngressoPayload
				if(StringUtils.isEmpty(dumpRichiestaIngressoPayload) || !(dumpRichiestaIngressoPayload.equals(StatoFunzionalita.ABILITATO.getValue()) || dumpRichiestaIngressoPayload.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_YY_DELLA_ZZ_NON_VALIDO, 
							CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_PAYLOAD, CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_INGRESSO, CostantiControlStation.LABEL_PARAMETRO_RICHIESTA));
					return false;
				}
				// dumpRichiestaIngressoPayloadParsing
				if(StringUtils.isEmpty(dumpRichiestaIngressoPayloadParsing) || !(dumpRichiestaIngressoPayloadParsing.equals(StatoFunzionalita.ABILITATO.getValue()) || dumpRichiestaIngressoPayloadParsing.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_YY_DELLA_ZZ_NON_VALIDO, 
							CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_PAYLOAD_PARSING, CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_INGRESSO, CostantiControlStation.LABEL_PARAMETRO_RICHIESTA));
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
							CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_USCITA_HEADERS, CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_USCITA, CostantiControlStation.LABEL_PARAMETRO_RICHIESTA));
					return false;
				}
				// dumpRichiestaUscitaPayload
				if(StringUtils.isEmpty(dumpRichiestaUscitaPayload) || !(dumpRichiestaUscitaPayload.equals(StatoFunzionalita.ABILITATO.getValue()) || dumpRichiestaUscitaPayload.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_YY_DELLA_ZZ_NON_VALIDO, 
							CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_USCITA_PAYLOAD, CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_USCITA, CostantiControlStation.LABEL_PARAMETRO_RICHIESTA));
					return false;
				}	
				// dumpRichiestaUscitaPayloadParsing
				if(StringUtils.isEmpty(dumpRichiestaUscitaPayloadParsing) || !(dumpRichiestaUscitaPayloadParsing.equals(StatoFunzionalita.ABILITATO.getValue()) || dumpRichiestaUscitaPayloadParsing.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_YY_DELLA_ZZ_NON_VALIDO, 
							CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_USCITA_PAYLOAD_PARSING, CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_USCITA, CostantiControlStation.LABEL_PARAMETRO_RICHIESTA));
					return false;
				}	
				// dumpRichiestaUscitaBody
				if(StringUtils.isEmpty(dumpRichiestaUscitaBody) || !(dumpRichiestaUscitaBody.equals(StatoFunzionalita.ABILITATO.getValue()) || dumpRichiestaUscitaBody.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_YY_DELLA_ZZ_NON_VALIDO, 
							CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_USCITA_BODY, CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_USCITA, CostantiControlStation.LABEL_PARAMETRO_RICHIESTA));
					return false;
				}				
				// dumpRichiestaUscitaAttachments
				if(StringUtils.isEmpty(dumpRichiestaUscitaAttachments) || !(dumpRichiestaUscitaAttachments.equals(StatoFunzionalita.ABILITATO.getValue()) || dumpRichiestaUscitaAttachments.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_YY_DELLA_ZZ_NON_VALIDO, 
							CostantiControlStation.LABEL_PARAMETRO_DUMP_RICHIESTA_USCITA_ATTACHMENTS, CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_USCITA, CostantiControlStation.LABEL_PARAMETRO_RICHIESTA));
					return false;
				}
				
				
				// se e' abilitato il dump per la richiesta almeno una singola voce deve essere abilitata
				if(dumpRichiestaIngressoHeader.equals(StatoFunzionalita.DISABILITATO.getValue()) && dumpRichiestaIngressoPayload.equals(StatoFunzionalita.DISABILITATO.getValue()) 
					&& dumpRichiestaUscitaHeader.equals(StatoFunzionalita.DISABILITATO.getValue()) && dumpRichiestaUscitaPayload.equals(StatoFunzionalita.DISABILITATO.getValue()) 
					) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMP_DATI_INCOMPLETI_E_NECESSARIO_ABILITARE_UNA_VOCE, CostantiControlStation.LABEL_PARAMETRO_RICHIESTA));
					return false;
				}
				if(dumpRichiestaIngressoPayload.equals(StatoFunzionalita.ABILITATO.getValue()) && dumpRichiestaIngressoPayloadParsing.equals(StatoFunzionalita.ABILITATO.getValue())) {
					if(dumpRichiestaIngressoBody.equals(StatoFunzionalita.DISABILITATO.getValue()) 
							&& dumpRichiestaIngressoAttachments.equals(StatoFunzionalita.DISABILITATO.getValue())
					) {
						this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMP_DATI_INCOMPLETI_E_NECESSARIO_ABILITARE_UNA_VOCE_PAYLOAD_PARSING, 
								CostantiControlStation.LABEL_PARAMETRO_RICHIESTA+" "+CostantiControlStation.LABEL_PARAMETRO_INGRESSO));
						return false;
					}
				}
				if(dumpRichiestaUscitaPayload.equals(StatoFunzionalita.ABILITATO.getValue()) && dumpRichiestaUscitaPayloadParsing.equals(StatoFunzionalita.ABILITATO.getValue())) {
					if(dumpRichiestaUscitaBody.equals(StatoFunzionalita.DISABILITATO.getValue()) 
							&& dumpRichiestaUscitaAttachments.equals(StatoFunzionalita.DISABILITATO.getValue())
					) {
						this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMP_DATI_INCOMPLETI_E_NECESSARIO_ABILITARE_UNA_VOCE_PAYLOAD_PARSING, 
								CostantiControlStation.LABEL_PARAMETRO_RICHIESTA+" "+CostantiControlStation.LABEL_PARAMETRO_USCITA));
						return false;
					}
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
							CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_INGRESSO_HEADERS, CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_INGRESSO, CostantiControlStation.LABEL_PARAMETRO_RISPOSTA));
					return false;
				}
				// dumpRispostaIngressoPayload
				if(StringUtils.isEmpty(dumpRispostaIngressoPayload) || !(dumpRispostaIngressoPayload.equals(StatoFunzionalita.ABILITATO.getValue()) || dumpRispostaIngressoPayload.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_YY_DELLA_ZZ_NON_VALIDO, 
							CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_INGRESSO_PAYLOAD, CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_INGRESSO, CostantiControlStation.LABEL_PARAMETRO_RISPOSTA));
					return false;
				}
				// dumpRispostaIngressoPayloadParsing
				if(StringUtils.isEmpty(dumpRispostaIngressoPayloadParsing) || !(dumpRispostaIngressoPayloadParsing.equals(StatoFunzionalita.ABILITATO.getValue()) || dumpRispostaIngressoPayloadParsing.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_YY_DELLA_ZZ_NON_VALIDO, 
							CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_INGRESSO_PAYLOAD_PARSING, CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_INGRESSO, CostantiControlStation.LABEL_PARAMETRO_RISPOSTA));
					return false;
				}
				// dumpRispostaIngressoBody
				if(StringUtils.isEmpty(dumpRispostaIngressoBody) || !(dumpRispostaIngressoBody.equals(StatoFunzionalita.ABILITATO.getValue()) || dumpRispostaIngressoBody.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_YY_DELLA_ZZ_NON_VALIDO, 
							CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_INGRESSO_BODY, CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_INGRESSO, CostantiControlStation.LABEL_PARAMETRO_RISPOSTA));
					return false;
				}
				// dumpRispostaIngressoAttachments
				if(StringUtils.isEmpty(dumpRispostaIngressoAttachments) || !(dumpRispostaIngressoAttachments.equals(StatoFunzionalita.ABILITATO.getValue()) || dumpRispostaIngressoAttachments.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_YY_DELLA_ZZ_NON_VALIDO, 
							CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_INGRESSO_ATTACHMENTS, CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_INGRESSO, CostantiControlStation.LABEL_PARAMETRO_RISPOSTA));
					return false;
				}
								
				
				// dumpRispostaUscitaHeader
				if(StringUtils.isEmpty(dumpRispostaUscitaHeader) || !(dumpRispostaUscitaHeader.equals(StatoFunzionalita.ABILITATO.getValue()) || dumpRispostaUscitaHeader.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_YY_DELLA_ZZ_NON_VALIDO, 
							CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_USCITA_HEADERS, CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_USCITA, CostantiControlStation.LABEL_PARAMETRO_RISPOSTA));
					return false;
				}
				// dumpRispostaUscitaPayload
				if(StringUtils.isEmpty(dumpRispostaUscitaPayload) || !(dumpRispostaUscitaPayload.equals(StatoFunzionalita.ABILITATO.getValue()) || dumpRispostaUscitaPayload.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_YY_DELLA_ZZ_NON_VALIDO, 
							CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_USCITA_PAYLOAD, CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_USCITA, CostantiControlStation.LABEL_PARAMETRO_RISPOSTA));
					return false;
				}	
				// dumpRispostaUscitaPayloadParsing
				if(StringUtils.isEmpty(dumpRispostaUscitaPayloadParsing) || !(dumpRispostaUscitaPayloadParsing.equals(StatoFunzionalita.ABILITATO.getValue()) || dumpRispostaUscitaPayloadParsing.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_YY_DELLA_ZZ_NON_VALIDO, 
							CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_USCITA_PAYLOAD_PARSING, CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_USCITA, CostantiControlStation.LABEL_PARAMETRO_RISPOSTA));
					return false;
				}	
				// dumpRispostaUscitaBody
				if(StringUtils.isEmpty(dumpRispostaUscitaBody) || !(dumpRispostaUscitaBody.equals(StatoFunzionalita.ABILITATO.getValue()) || dumpRispostaUscitaBody.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_YY_DELLA_ZZ_NON_VALIDO, 
							CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_USCITA_BODY, CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_USCITA, CostantiControlStation.LABEL_PARAMETRO_RISPOSTA));
					return false;
				}				
				// dumpRispostaUscitaAttachments
				if(StringUtils.isEmpty(dumpRispostaUscitaAttachments) || !(dumpRispostaUscitaAttachments.equals(StatoFunzionalita.ABILITATO.getValue()) || dumpRispostaUscitaAttachments.equals(StatoFunzionalita.DISABILITATO.getValue()))) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_YY_DELLA_ZZ_NON_VALIDO, 
							CostantiControlStation.LABEL_PARAMETRO_DUMP_RISPOSTA_USCITA_ATTACHMENTS, CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_USCITA, CostantiControlStation.LABEL_PARAMETRO_RISPOSTA));
					return false;
				}
				
				
				// se e' abilitato il dump per la richiesta almeno una singola voce deve essere abilitata
				if(dumpRispostaIngressoHeader.equals(StatoFunzionalita.DISABILITATO.getValue()) && dumpRispostaIngressoPayload.equals(StatoFunzionalita.DISABILITATO.getValue()) 
					&& dumpRispostaUscitaHeader.equals(StatoFunzionalita.DISABILITATO.getValue()) && dumpRispostaUscitaPayload.equals(StatoFunzionalita.DISABILITATO.getValue()) 
					) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMP_DATI_INCOMPLETI_E_NECESSARIO_ABILITARE_UNA_VOCE, CostantiControlStation.LABEL_PARAMETRO_RISPOSTA));
					return false;
				}
				if(dumpRispostaIngressoPayload.equals(StatoFunzionalita.ABILITATO.getValue()) && dumpRispostaIngressoPayloadParsing.equals(StatoFunzionalita.ABILITATO.getValue())) {
					if(dumpRispostaIngressoBody.equals(StatoFunzionalita.DISABILITATO.getValue()) 
							&& dumpRispostaIngressoAttachments.equals(StatoFunzionalita.DISABILITATO.getValue())
					) {
						this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMP_DATI_INCOMPLETI_E_NECESSARIO_ABILITARE_UNA_VOCE_PAYLOAD_PARSING, 
								CostantiControlStation.LABEL_PARAMETRO_RISPOSTA+" "+CostantiControlStation.LABEL_PARAMETRO_INGRESSO));
						return false;
					}
				}
				if(dumpRispostaUscitaPayload.equals(StatoFunzionalita.ABILITATO.getValue()) && dumpRispostaUscitaPayloadParsing.equals(StatoFunzionalita.ABILITATO.getValue())) {
					if(dumpRispostaUscitaBody.equals(StatoFunzionalita.DISABILITATO.getValue()) 
							&& dumpRispostaUscitaBody.equals(StatoFunzionalita.DISABILITATO.getValue())
					) {
						this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMP_DATI_INCOMPLETI_E_NECESSARIO_ABILITARE_UNA_VOCE_PAYLOAD_PARSING, 
								CostantiControlStation.LABEL_PARAMETRO_RISPOSTA+" "+CostantiControlStation.LABEL_PARAMETRO_USCITA));
						return false;
					}
				}
			}
			
			/*
			 * Vorrei poterla ridefinire disabilitata
			if (!statoDumpRichiesta.equals(StatoFunzionalita.ABILITATO.getValue())  && !statoDumpRisposta.equals(StatoFunzionalita.ABILITATO.getValue())) {
				this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMP_DATI_INCOMPLETI_E_NECESSARIO_ABILITARE_UNA_VOCE_TRA_RICHIESTA_E_RISPOSTA);
				return false;
			}
			*/
		}
		
		
		return true;
	}
	
	public DumpConfigurazione getConfigurazioneDump(TipoOperazione tipoOperazione,boolean showStato, String statoDump, boolean showRealtime, String realtime, String statoDumpRichiesta, String statoDumpRisposta,
			String dumpRichiestaIngressoHeader, String dumpRichiestaIngressoPayload, String dumpRichiestaIngressoPayloadParsing, String dumpRichiestaIngressoBody, String dumpRichiestaIngressoAttachments, 
			String dumpRichiestaUscitaHeader, String dumpRichiestaUscitaPayload, String dumpRichiestaUscitaPayloadParsing, String dumpRichiestaUscitaBody, String dumpRichiestaUscitaAttachments, 
			String dumpRispostaIngressoHeader, String dumpRispostaIngressoPayload, String dumpRispostaIngressoPayloadParsing, String dumpRispostaIngressoBody , String dumpRispostaIngressoAttachments,
			String dumpRispostaUscitaHeader, String dumpRispostaUscitaPayload, String dumpRispostaUscitaPayloadParsing, String dumpRispostaUscitaBody, String dumpRispostaUscitaAttachments) throws Exception{
		
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
				newConfigurazione.getRichiestaIngresso().set_value_payload(dumpRichiestaIngressoPayload);
				if(StatoFunzionalita.ABILITATO.equals(newConfigurazione.getRichiestaIngresso().getPayload())){
					newConfigurazione.getRichiestaIngresso().set_value_payloadParsing(dumpRichiestaIngressoPayloadParsing);
					if(StatoFunzionalita.ABILITATO.equals(newConfigurazione.getRichiestaIngresso().getPayloadParsing())){
						newConfigurazione.getRichiestaIngresso().set_value_body(dumpRichiestaIngressoBody);
						newConfigurazione.getRichiestaIngresso().set_value_attachments(dumpRichiestaIngressoAttachments);
					}
					else {
						newConfigurazione.getRichiestaIngresso().setBody(StatoFunzionalita.DISABILITATO);
						newConfigurazione.getRichiestaIngresso().setAttachments(StatoFunzionalita.DISABILITATO);
					}
				}
				else {
					newConfigurazione.getRichiestaIngresso().setPayloadParsing(StatoFunzionalita.DISABILITATO);
					newConfigurazione.getRichiestaIngresso().setBody(StatoFunzionalita.DISABILITATO);
					newConfigurazione.getRichiestaIngresso().setAttachments(StatoFunzionalita.DISABILITATO);
				}
				
				newConfigurazione.getRichiestaUscita().set_value_headers(dumpRichiestaUscitaHeader);
				newConfigurazione.getRichiestaUscita().set_value_payload(dumpRichiestaUscitaPayload);
				if(StatoFunzionalita.ABILITATO.equals(newConfigurazione.getRichiestaUscita().getPayload())){
					newConfigurazione.getRichiestaUscita().set_value_payloadParsing(dumpRichiestaUscitaPayloadParsing);
					if(StatoFunzionalita.ABILITATO.equals(newConfigurazione.getRichiestaUscita().getPayloadParsing())){
						newConfigurazione.getRichiestaUscita().set_value_body(dumpRichiestaUscitaBody);
						newConfigurazione.getRichiestaUscita().set_value_attachments(dumpRichiestaUscitaAttachments);
					}
					else {
						newConfigurazione.getRichiestaUscita().setBody(StatoFunzionalita.DISABILITATO);
						newConfigurazione.getRichiestaUscita().setAttachments(StatoFunzionalita.DISABILITATO);
					}
				}
				else {
					newConfigurazione.getRichiestaUscita().setPayloadParsing(StatoFunzionalita.DISABILITATO);
					newConfigurazione.getRichiestaUscita().setBody(StatoFunzionalita.DISABILITATO);
					newConfigurazione.getRichiestaUscita().setAttachments(StatoFunzionalita.DISABILITATO);
				}
			} else {
				newConfigurazione.getRichiestaIngresso().setHeaders(StatoFunzionalita.DISABILITATO);
				newConfigurazione.getRichiestaIngresso().setPayload(StatoFunzionalita.DISABILITATO);
				newConfigurazione.getRichiestaIngresso().setPayloadParsing(StatoFunzionalita.DISABILITATO);
				newConfigurazione.getRichiestaIngresso().setBody(StatoFunzionalita.DISABILITATO);
				newConfigurazione.getRichiestaIngresso().setAttachments(StatoFunzionalita.DISABILITATO);
				newConfigurazione.getRichiestaUscita().setHeaders(StatoFunzionalita.DISABILITATO);
				newConfigurazione.getRichiestaUscita().setPayload(StatoFunzionalita.DISABILITATO);
				newConfigurazione.getRichiestaUscita().setPayloadParsing(StatoFunzionalita.DISABILITATO);
				newConfigurazione.getRichiestaUscita().setBody(StatoFunzionalita.DISABILITATO);
				newConfigurazione.getRichiestaUscita().setAttachments(StatoFunzionalita.DISABILITATO);
			}
			
			if(statoDumpRisposta.equals(StatoFunzionalita.ABILITATO.getValue())) {
				newConfigurazione.getRispostaIngresso().set_value_headers(dumpRispostaIngressoHeader);
				newConfigurazione.getRispostaIngresso().set_value_payload(dumpRispostaIngressoPayload);
				if(StatoFunzionalita.ABILITATO.equals(newConfigurazione.getRispostaIngresso().getPayload())){
					newConfigurazione.getRispostaIngresso().set_value_payloadParsing(dumpRispostaIngressoPayloadParsing);
					if(StatoFunzionalita.ABILITATO.equals(newConfigurazione.getRispostaIngresso().getPayloadParsing())){
						newConfigurazione.getRispostaIngresso().set_value_body(dumpRispostaIngressoBody);
						newConfigurazione.getRispostaIngresso().set_value_attachments(dumpRispostaIngressoAttachments);
					}
					else {
						newConfigurazione.getRispostaIngresso().setBody(StatoFunzionalita.DISABILITATO);
						newConfigurazione.getRispostaIngresso().setAttachments(StatoFunzionalita.DISABILITATO);
					}
				}
				else {
					newConfigurazione.getRispostaIngresso().setPayloadParsing(StatoFunzionalita.DISABILITATO);
					newConfigurazione.getRispostaIngresso().setBody(StatoFunzionalita.DISABILITATO);
					newConfigurazione.getRispostaIngresso().setAttachments(StatoFunzionalita.DISABILITATO);
				}
				
				newConfigurazione.getRispostaUscita().set_value_headers(dumpRispostaUscitaHeader);
				newConfigurazione.getRispostaUscita().set_value_payload(dumpRispostaUscitaPayload);
				if(StatoFunzionalita.ABILITATO.equals(newConfigurazione.getRispostaUscita().getPayload())){
					newConfigurazione.getRispostaUscita().set_value_payloadParsing(dumpRispostaUscitaPayloadParsing);
					if(StatoFunzionalita.ABILITATO.equals(newConfigurazione.getRispostaUscita().getPayloadParsing())){
						newConfigurazione.getRispostaUscita().set_value_body(dumpRispostaUscitaBody);
						newConfigurazione.getRispostaUscita().set_value_attachments(dumpRispostaUscitaAttachments);
					}
					else {
						newConfigurazione.getRispostaUscita().setBody(StatoFunzionalita.DISABILITATO);
						newConfigurazione.getRispostaUscita().setAttachments(StatoFunzionalita.DISABILITATO);
					}
				}
				else {
					newConfigurazione.getRispostaUscita().setPayloadParsing(StatoFunzionalita.DISABILITATO);
					newConfigurazione.getRispostaUscita().setBody(StatoFunzionalita.DISABILITATO);
					newConfigurazione.getRispostaUscita().setAttachments(StatoFunzionalita.DISABILITATO);
				}
			} else {
				newConfigurazione.getRispostaIngresso().setHeaders(StatoFunzionalita.DISABILITATO);
				newConfigurazione.getRispostaIngresso().setPayload(StatoFunzionalita.DISABILITATO);
				newConfigurazione.getRispostaIngresso().setPayloadParsing(StatoFunzionalita.DISABILITATO);
				newConfigurazione.getRispostaIngresso().setBody(StatoFunzionalita.DISABILITATO);
				newConfigurazione.getRispostaIngresso().setAttachments(StatoFunzionalita.DISABILITATO);
				newConfigurazione.getRispostaUscita().setHeaders(StatoFunzionalita.DISABILITATO);
				newConfigurazione.getRispostaUscita().setBody(StatoFunzionalita.DISABILITATO);
				newConfigurazione.getRispostaUscita().setPayload(StatoFunzionalita.DISABILITATO);
				newConfigurazione.getRispostaUscita().setPayloadParsing(StatoFunzionalita.DISABILITATO);
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
				
				if(rispostaIngresso.getPayload().equals(StatoFunzionalita.ABILITATO)) {
					if(rispostaIngresso.getPayloadParsing().equals(StatoFunzionalita.ABILITATO)) {
						if(rispostaIngresso.getBody().equals(StatoFunzionalita.ABILITATO))
							return true;
						
						if(rispostaIngresso.getAttachments().equals(StatoFunzionalita.ABILITATO))
							return true;		
					}
					else {
						return true;
					}
				}
				
			}
			
			DumpConfigurazioneRegola rispostaUscita = configurazione.getRispostaUscita();
			
			if(rispostaUscita != null) {
				if(rispostaUscita.getHeaders().equals(StatoFunzionalita.ABILITATO))
					return true;
				
				if(rispostaUscita.getPayload().equals(StatoFunzionalita.ABILITATO)) {
					if(rispostaUscita.getPayloadParsing().equals(StatoFunzionalita.ABILITATO)) {
						if(rispostaUscita.getBody().equals(StatoFunzionalita.ABILITATO))
							return true;
						
						if(rispostaUscita.getAttachments().equals(StatoFunzionalita.ABILITATO))
							return true;		
					}
					else {
						return true;
					}
				}
			}
		} else {
			DumpConfigurazioneRegola richiestaIngresso = configurazione.getRichiestaIngresso();
			
			if(richiestaIngresso != null) {
				if(richiestaIngresso.getHeaders().equals(StatoFunzionalita.ABILITATO))
					return true;
				
				if(richiestaIngresso.getPayload().equals(StatoFunzionalita.ABILITATO)) {
					if(richiestaIngresso.getPayloadParsing().equals(StatoFunzionalita.ABILITATO)) {
						if(richiestaIngresso.getBody().equals(StatoFunzionalita.ABILITATO))
							return true;
						
						if(richiestaIngresso.getAttachments().equals(StatoFunzionalita.ABILITATO))
							return true;		
					}
					else {
						return true;
					}
				}
			}
			
			DumpConfigurazioneRegola richiestaUscita = configurazione.getRichiestaUscita();
			
			if(richiestaUscita != null) {
				if(richiestaUscita.getHeaders().equals(StatoFunzionalita.ABILITATO))
					return true;
				
				if(richiestaUscita.getPayload().equals(StatoFunzionalita.ABILITATO)) {
					if(richiestaUscita.getPayloadParsing().equals(StatoFunzionalita.ABILITATO)) {
						if(richiestaUscita.getBody().equals(StatoFunzionalita.ABILITATO))
							return true;
						
						if(richiestaUscita.getAttachments().equals(StatoFunzionalita.ABILITATO))
							return true;		
					}
					else {
						return true;
					}
				}
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
			Map<String, String> mapNameValue = new HashMap<String, String>();
			for (BaseItemBean<?> item : configurazioneBean.getListaItem()) {
				if(item.isVisible())
					dati.addElement(item.toDataElement(configurazioneBean, mapNameValue));
			}
		}
		
		return dati;
	}
	
	public boolean checkPropertiesConfigurationData(TipoOperazione tipoOperazione,ConfigBean configurazioneBean, String nome, String descrizione, Config config) throws Exception{
		// Controlli sui campi immessi
		try {
			configurazioneBean.validazioneInputUtente(nome, descrizione, config);
			return true;
		}catch(UserInputValidationException e) {
			this.pd.setMessage(e.getMessage());  
			return false;
		}catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			this.pd.setMessage("Si &egrave; verificato un errore durante la validazione, impossibile caricare il plugin di validazione previsto dalla configurazione"); 
			return false;		
		} catch(ProviderException e) {
			this.pd.setMessage("Si &egrave; verificato un errore durante la validazione, impossibile utilizzare il plugin di validazione previsto dalla configurazione"); 
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
		if(singlePdD==null) {
			singlePdD = this.core!=null ? this.core.isSinglePdD() : true;
		}

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
		
		
		StringBuilder bf = new StringBuilder();
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
	
	public List<String> getRegistrazioneEsiti(String configurazioneEsiti, StringBuilder bf) throws Exception{
		
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
	
	public List<Integer> getListaEsitiFalliteSenza_RateLimiting_MaxThreads_Scartate(EsitiProperties esiti) throws ProtocolException{
		List<Integer> listFallite = esiti.getEsitiCodeKo_senzaFaultApplicativo();
		
		List<Integer> listDaScartare = new ArrayList<>();
		listDaScartare.addAll(esiti.getEsitiCodeRichiestaScartate());
		int esitoViolazioneRateLimiting = esiti.convertoToCode(EsitoTransazioneName.CONTROLLO_TRAFFICO_POLICY_VIOLATA);
		listDaScartare.add(esitoViolazioneRateLimiting);
		int esitoViolazione = esiti.convertoToCode(EsitoTransazioneName.CONTROLLO_TRAFFICO_MAX_THREADS);
		listDaScartare.add(esitoViolazione);
		
		List<Integer> listFalliteSenza_rateLimiting_e_max_e_scartate = new ArrayList<>(); 
		int i = 0;
		for (; i < listFallite.size(); i++) {
			boolean findDaScartare = false;
			for (Integer daScartare : listDaScartare) {
				if(listFallite.get(i).intValue() == daScartare.intValue()) {
					findDaScartare = true;
					break;
				}
			}
			if(!findDaScartare) {
				
				boolean statiConsegnaMultipla = EsitoTransazioneName.isStatiConsegnaMultipla(esiti.getEsitoTransazioneName(listFallite.get(i).intValue()));
				if(statiConsegnaMultipla) {
					continue; // non vengono gestiti in questa configurazione
				}
				
				listFalliteSenza_rateLimiting_e_max_e_scartate.add(listFallite.get(i));
			}
		}
		return listFalliteSenza_rateLimiting_e_max_e_scartate;
	}
	
	public List<Integer> getListaEsitiOkSenzaCors(EsitiProperties esiti) throws ProtocolException{
		List<Integer> listOk = esiti.getEsitiCodeOk_senzaFaultApplicativo();
		int esitoCorsGateway = esiti.convertoToCode(EsitoTransazioneName.CORS_PREFLIGHT_REQUEST_VIA_GATEWAY);
		int esitoCorsTrasparente = esiti.convertoToCode(EsitoTransazioneName.CORS_PREFLIGHT_REQUEST_TRASPARENTE);
		List<Integer> listOkSenzaCors = new ArrayList<>(); 
		int i = 0;
		for (; i < listOk.size(); i++) {
			
			boolean statiConsegnaMultipla = EsitoTransazioneName.isStatiConsegnaMultipla(esiti.getEsitoTransazioneName(listOk.get(i).intValue()));
			if(statiConsegnaMultipla) {
				continue; // non vengono gestiti in questa configurazione
			}
			
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
			boolean selectAll,
			String tracciamentoEsitiSelezionePersonalizzataOk, String tracciamentoEsitiSelezionePersonalizzataFault, 
			String tracciamentoEsitiSelezionePersonalizzataFallite, String tracciamentoEsitiSelezionePersonalizzataScartate, 
			String tracciamentoEsitiSelezionePersonalizzataRateLimiting, String tracciamentoEsitiSelezionePersonalizzataMax, String tracciamentoEsitiSelezionePersonalizzataCors) throws Exception {
		
	
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
			de.setPostBack_viaPOST(true);
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
			
			
			// select all
			
			de = new DataElement();
			de.setLabelRight(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_ALL);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_ALL);
			de.setType(DataElementType.CHECKBOX);
			de.setSelected(selectAll);
			de.setPostBack_viaPOST(true);
			dati.addElement(de);
			
			
			// ok
			
			List<Integer> listOk = getListaEsitiOkSenzaCors(esiti);
			
			if(!selectAll) {
				de = new DataElement();
				de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_OK);
				//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
				de.setType(DataElementType.SUBTITLE);
				dati.addElement(de);
			}
					
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_STATO);
			//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_OK);
			if(!selectAll) {
				de.setType(DataElementType.SELECT);
				de.setValues(values);
				de.setLabels(values);
				de.setSelected(tracciamentoEsitiSelezionePersonalizzataOk);
				de.setPostBack_viaPOST(true);
			}
			else {
				de.setType(DataElementType.HIDDEN);
				de.setSelected(tracciamentoEsitiSelezionePersonalizzataOk);
			}
			dati.addElement(de);
					
			if(ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO.equals(tracciamentoEsitiSelezionePersonalizzataOk) ||
					ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO.equals(tracciamentoEsitiSelezionePersonalizzataOk) ||
					selectAll) {
				for (Integer esito : listOk) {
					
					EsitoTransazioneName esitoTransactionName = esiti.getEsitoTransazioneName(esito);
					
					boolean statiConsegnaMultipla = EsitoTransazioneName.isStatiConsegnaMultipla(esitoTransactionName);
					if(statiConsegnaMultipla) {
						continue; // non vengono gestiti in questa configurazione
					}
					
					boolean integrationManagerSpecific = EsitoTransazioneName.isIntegrationManagerSpecific(esitoTransactionName);		
					boolean integrationManagerSavedInMessageBox = EsitoTransazioneName.isSavedInMessageBox(esitoTransactionName);		
										
					de = new DataElement();
					if(EsitoTransazioneName.CONSEGNA_MULTIPLA.equals(esitoTransactionName)) {
						de.setLabelRight(EsitoUtils.LABEL_ESITO_CONSEGNA_MULTIPLA_SENZA_STATI);
					}
					else {
						de.setLabelRight(esiti.getEsitoLabel(esito));
					}
					//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
	//				de.setNote(esiti.getEsitoLabel(esito));
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_STATO+esito);
					if(!selectAll && ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO.equals(tracciamentoEsitiSelezionePersonalizzataOk)) {
						if(integrationManagerSpecific && (this.isModalitaStandard() || !this.core.isIntegrationManagerTraceMessageBoxOperationEnabled())) {
							de.setType(DataElementType.HIDDEN);
							de.setValue(attivi.contains((esito+""))+"");
						}
						else if(integrationManagerSavedInMessageBox && (!this.core.isIntegrationManagerEnabled())) {
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
			
			if(!selectAll) {
				de = new DataElement();
				de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_FAULT);
				//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
				de.setType(DataElementType.SUBTITLE);
				dati.addElement(de);
			}
					
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_STATO);
			//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_FAULT);
			if(!selectAll) {
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
				de.setPostBack_viaPOST(true);
			}
			else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(tracciamentoEsitiSelezionePersonalizzataFault);
			}
			dati.addElement(de);
					
			if(ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO.equals(tracciamentoEsitiSelezionePersonalizzataFault) ||
					ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO.equals(tracciamentoEsitiSelezionePersonalizzataFault) ||
					selectAll) {
				for (Integer esito : listFault) {
					de = new DataElement();
					de.setLabelRight(esiti.getEsitoLabel(esito));
					//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
	//						de.setNote(esiti.getEsitoLabel(esito));
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_STATO+esito);
					if(!selectAll && ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO.equals(tracciamentoEsitiSelezionePersonalizzataFault)) {
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
			
			List<Integer> listFalliteSenza_MaxThreads_Scartate = getListaEsitiFalliteSenza_RateLimiting_MaxThreads_Scartate(esiti);
			
			if(!selectAll) {
				de = new DataElement();
				de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_FALLITE);
				//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
				de.setType(DataElementType.SUBTITLE);
				dati.addElement(de);
			}
					
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_STATO);
			//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_FALLITE);
			if(!selectAll) {
				de.setType(DataElementType.SELECT);
				de.setValues(values);
				de.setLabels(values);
				de.setSelected(tracciamentoEsitiSelezionePersonalizzataFallite);
				de.setPostBack_viaPOST(true);
			}
			else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(tracciamentoEsitiSelezionePersonalizzataFallite);
			}
			dati.addElement(de);
					
			if(ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO.equals(tracciamentoEsitiSelezionePersonalizzataFallite) ||
					ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO.equals(tracciamentoEsitiSelezionePersonalizzataFallite) ||
					selectAll) {
				for (Integer esito : listFalliteSenza_MaxThreads_Scartate) {
					
					EsitoTransazioneName esitoTransactionName = esiti.getEsitoTransazioneName(esito);
					
					boolean statiConsegnaMultipla = EsitoTransazioneName.isStatiConsegnaMultipla(esitoTransactionName);
					if(statiConsegnaMultipla) {
						continue; // non vengono gestiti in questa configurazione
					}
					
					boolean integrationManagerSpecific = EsitoTransazioneName.isIntegrationManagerSpecific(esitoTransactionName);		
					
					de = new DataElement();
					de.setLabelRight(esiti.getEsitoLabel(esito));
					//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
	//						de.setNote(esiti.getEsitoLabel(esito));
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_STATO+esito);
					if(!selectAll && ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO.equals(tracciamentoEsitiSelezionePersonalizzataFallite)) {
						if(integrationManagerSpecific && (this.isModalitaStandard() || !this.core.isIntegrationManagerTraceMessageBoxOperationEnabled())) {
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
			
			
			
			// Scartate
			
			List<Integer> listScartate = esiti.getEsitiCodeRichiestaScartate();
			
			if(!selectAll) {
				de = new DataElement();
				de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_SCARTATE);
				//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
				de.setType(DataElementType.SUBTITLE);
				dati.addElement(de);
			}
					
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_STATO);
			//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_SCARTATE);
			if(!selectAll) {
				de.setType(DataElementType.SELECT);
				de.setValues(values);
				de.setLabels(values);
				de.setSelected(tracciamentoEsitiSelezionePersonalizzataScartate);
				de.setPostBack_viaPOST(true);
			}
			else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(tracciamentoEsitiSelezionePersonalizzataScartate);
			}
			dati.addElement(de);
					
			if(ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO.equals(tracciamentoEsitiSelezionePersonalizzataScartate) ||
					ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO.equals(tracciamentoEsitiSelezionePersonalizzataScartate) ||
					selectAll) {
				for (Integer esito : listScartate) {
					
					EsitoTransazioneName esitoTransactionName = esiti.getEsitoTransazioneName(esito);
					
					boolean statiConsegnaMultipla = EsitoTransazioneName.isStatiConsegnaMultipla(esitoTransactionName);
					if(statiConsegnaMultipla) {
						continue; // non vengono gestiti in questa configurazione
					}
					
					boolean integrationManagerSpecific = EsitoTransazioneName.isIntegrationManagerSpecific(esitoTransactionName);		
					
					de = new DataElement();
					de.setLabelRight(esiti.getEsitoLabel(esito));
					//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
	//						de.setNote(esiti.getEsitoLabel(esito));
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_STATO+esito);
					if(!selectAll && ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO.equals(tracciamentoEsitiSelezionePersonalizzataScartate)) {
						if(integrationManagerSpecific && (this.isModalitaStandard() || !this.core.isIntegrationManagerTraceMessageBoxOperationEnabled())) {
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
					
			
			
			
			// rate limiting
			
			if(!selectAll) {
				de = new DataElement();
				de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_RATE_LIMITING);
				//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
				de.setType(DataElementType.SUBTITLE);
				dati.addElement(de);
			}
			
			String esitoViolazioneRateLimitingAsString = esiti.convertoToCode(EsitoTransazioneName.CONTROLLO_TRAFFICO_POLICY_VIOLATA) + "";
			
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_STATO);
			//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_RATE_LIMITING);
			if(!selectAll) {
				de.setType(DataElementType.SELECT);
				de.setValues(values_senza_personalizzato);
				de.setLabels(values_senza_personalizzato);
				de.setSelected(tracciamentoEsitiSelezionePersonalizzataRateLimiting);
				de.setPostBack_viaPOST(true);
			}
			else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(tracciamentoEsitiSelezionePersonalizzataRateLimiting);
			}
			dati.addElement(de);
			
			if(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO.equals(tracciamentoEsitiSelezionePersonalizzataRateLimiting) ||
					selectAll) {
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_STATO+esitoViolazioneRateLimitingAsString);
				de.setType(DataElementType.HIDDEN);
				de.setValue("true");
				dati.addElement(de);
			}
			
			
			
			
			
			// max
			
			if(!selectAll) {
				de = new DataElement();
				de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_MAX_REQUESTS);
				//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
				de.setType(DataElementType.SUBTITLE);
				dati.addElement(de);
			}
			
			String esitoViolazioneAsString = esiti.convertoToCode(EsitoTransazioneName.CONTROLLO_TRAFFICO_MAX_THREADS) + "";
			
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_STATO);
			//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_MAX_REQUEST);
			if(!selectAll) {
				de.setType(DataElementType.SELECT);
				de.setValues(values_senza_personalizzato);
				de.setLabels(values_senza_personalizzato);
				de.setSelected(tracciamentoEsitiSelezionePersonalizzataMax);
				de.setPostBack_viaPOST(true);
			}
			else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(tracciamentoEsitiSelezionePersonalizzataMax);
			}
			dati.addElement(de);
			
			if(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO.equals(tracciamentoEsitiSelezionePersonalizzataMax) ||
					selectAll) {
				de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_STATO+esitoViolazioneAsString);
				de.setType(DataElementType.HIDDEN);
				de.setValue("true");
				dati.addElement(de);
			}
			
			
			
			
			// cors
			
			if(!selectAll) {
				de = new DataElement();
				de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_CORS);
				//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
				de.setType(DataElementType.SUBTITLE);
				dati.addElement(de);
			}
			
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_STATO);
			//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_CORS);
			if(!selectAll) {
				de.setType(DataElementType.SELECT);
				de.setValues(values);
				de.setLabels(values);
				de.setSelected(tracciamentoEsitiSelezionePersonalizzataCors);
				de.setPostBack_viaPOST(true);
			}
			else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(tracciamentoEsitiSelezionePersonalizzataCors);
			}
			dati.addElement(de);
					
			if(ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO.equals(tracciamentoEsitiSelezionePersonalizzataCors) ||
					ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO.equals(tracciamentoEsitiSelezionePersonalizzataCors) ||
					selectAll) {
				
				List<Integer> listCors = this.getListaEsitiCors(esiti);
				
				for (Integer esito : listCors) {
					
					EsitoTransazioneName esitoTransactionName = esiti.getEsitoTransazioneName(esito);
					boolean integrationManagerSpecific = EsitoTransazioneName.isIntegrationManagerSpecific(esitoTransactionName);		
					
					de = new DataElement();
					de.setLabelRight(esiti.getEsitoLabel(esito));
					//de.setLabelStyleClass(Costanti.LABEL_LONG_CSS_CLASS);
	//				de.setNote(esiti.getEsitoLabel(esito));
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_STATO+esito);
					if(!selectAll && ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO.equals(tracciamentoEsitiSelezionePersonalizzataCors)) {
						if(integrationManagerSpecific && (this.isModalitaStandard() || !this.core.isIntegrationManagerTraceMessageBoxOperationEnabled())) {
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
		if(this.core.isVisualizzazioneConfigurazioneDiagnosticaLog4J()){
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

	
	public static String normalizeLabel(String label, int maxWidth) {
		if(label.length() > maxWidth) {
			return label.substring(0, maxWidth - 3) + "...";
		}
		return label;
	}
	
	public void addConfigurazioneCorsPorteToDati(TipoOperazione tipoOperazione,Vector<DataElement> dati, boolean showStato, String statoCorsPorta, boolean corsStato, TipoGestioneCORS corsTipo,
			boolean corsAllAllowOrigins, boolean corsAllAllowHeaders, boolean corsAllAllowMethods,
			String corsAllowHeaders, String corsAllowOrigins, String corsAllowMethods,
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
			this.addConfigurazioneCorsToDati(dati, corsStato, corsTipo, 
					corsAllAllowOrigins, corsAllAllowHeaders, corsAllAllowMethods, 
					corsAllowHeaders, corsAllowOrigins, corsAllowMethods, 
					corsAllowCredential, corsExposeHeaders, corsMaxAge, corsMaxAgeSeconds, 
					false,
					false);
		}
	}
			
	
	// CORS
	public void addConfigurazioneCorsToDati(Vector<DataElement> dati, boolean corsStato, TipoGestioneCORS corsTipo,
			boolean corsAllAllowOrigins, boolean corsAllAllowHeaders, boolean corsAllAllowMethods, 
			String corsAllowHeaders, String corsAllowOrigins, String corsAllowMethods,
			boolean corsAllowCredential, String corsExposeHeaders, boolean corsMaxAge, int corsMaxAgeSeconds,
			boolean addTitle,
			boolean allHidden) {
		
		DataElement de;
		if(!allHidden && addTitle) {
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_CONFIGURAZIONE_CORS);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}
		
		de = new DataElement();
		de.setLabel(addTitle ? CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_STATO : "");
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_STATO);
		if(allHidden) {
			de.setType(DataElementType.HIDDEN);
		}
		else {
			de.setType(DataElementType.SELECT);
			de.setPostBack(true);
			de.setValues(CostantiControlStation.SELECT_VALUES_STATO_FUNZIONALITA);
			de.setSelected(corsStato ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue());
		}
		de.setValue(corsStato ? CostantiConfigurazione.ABILITATO.getValue() : CostantiConfigurazione.DISABILITATO.getValue());
		dati.addElement(de);
		
		if(corsStato) {
			
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_TIPO);
			de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_TIPO);
			de.setValue(corsTipo.getValue());
			if(allHidden) {
				de.setType(DataElementType.HIDDEN);
			}
			else if(
					TipoGestioneCORS.TRASPARENTE.equals(corsTipo) // impostato in avanzato
					||
					!this.isModalitaStandard()
					) {
			
				String [] corsTipiValues = new String [] { TipoGestioneCORS.GATEWAY.getValue(), TipoGestioneCORS.TRASPARENTE.getValue()};
				String [] corsTipiLabels = new String [] { 
						CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_TIPO_GESTITO_GATEWAY,
						CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_TIPO_GESTITO_APPLICATIVO
						};
				de.setType(DataElementType.SELECT);
				de.setPostBack(true);
				de.setValues(corsTipiValues);
				de.setLabels(corsTipiLabels);
				de.setSelected(corsTipo.getValue());
			}
			else {
				de.setType(DataElementType.HIDDEN);
			}
			dati.addElement(de);
			
			if(TipoGestioneCORS.GATEWAY.equals(corsTipo)) {
				
				if(!allHidden) {
					de = new DataElement();
					de.setType(DataElementType.SUBTITLE);
					de.setLabel(CostantiControlStation.LABEL_CONFIGURAZIONE_CORS_ACCESS_CONTROL);
					dati.addElement(de);
				}
				
				
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_ORIGINS);
				de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_ORIGINS);
				if(allHidden) {
					de.setType(DataElementType.HIDDEN);
				}
				else {
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(corsAllAllowOrigins);
					de.setPostBack(true);
				}
				de.setValue(corsAllAllowOrigins+"");
				dati.addElement(de);
				
				if(!corsAllAllowOrigins) {
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_ORIGINS);
					de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_ORIGINS);
					if(allHidden) {
						de.setType(DataElementType.HIDDEN);
					}else {
						de.setType(DataElementType.TEXT_EDIT);
						de.setRequired(true);
						de.enableTags();
					}
					de.setValue(corsAllowOrigins);
					dati.addElement(de);
				}
				else {
					corsAllAllowMethods = false;
					corsAllAllowHeaders = false;
				}
										
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_METHODS);
				de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_METHODS);
				if(allHidden || corsAllAllowOrigins) {
					de.setType(DataElementType.HIDDEN);
				}
				else {
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(corsAllAllowMethods);
					de.setPostBack(true);
				}
				de.setValue(corsAllAllowMethods+"");
				dati.addElement(de);
				
				if(!corsAllAllowMethods || corsAllAllowOrigins) {
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_METHODS);
					de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_METHODS);
					if(allHidden) {
						de.setType(DataElementType.HIDDEN);
					}else {
						de.setType(DataElementType.TEXT_EDIT);
						de.setRequired(true);
						de.enableTags();
					}
					de.setValue(corsAllowMethods);
					dati.addElement(de);
				}
							
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_HEADERS);
				de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_HEADERS);
				if(allHidden || corsAllAllowOrigins) {
					de.setType(DataElementType.HIDDEN);
				}
				else {
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(corsAllAllowHeaders);
					de.setPostBack(true);
				}
				de.setValue(corsAllAllowHeaders+"");
				dati.addElement(de);
				
				if(!corsAllAllowHeaders || corsAllAllowOrigins) {
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_HEADERS);
					de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_HEADERS);
					if(allHidden) {
						de.setType(DataElementType.HIDDEN);
					}else {
						de.setType(DataElementType.TEXT_EDIT);
						de.setRequired(true);
						de.enableTags();
					}
					de.setValue(corsAllowHeaders);
					dati.addElement(de);
				}
				
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_CREDENTIALS);
				de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_CREDENTIALS);
				if(allHidden) {
					de.setType(DataElementType.HIDDEN);
				}else {
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(corsAllowCredential);
				}
				de.setValue(corsAllowCredential+"");
				dati.addElement(de);
				
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_EXPOSE_HEADERS);
				de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_EXPOSE_HEADERS);
				if(allHidden) {
					de.setType(DataElementType.HIDDEN);
				}else {
					de.setType(DataElementType.TEXT_EDIT);
					de.enableTags();
				}
				de.setValue(corsExposeHeaders);
				dati.addElement(de);
				
				
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_MAX_AGE);
				de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_MAX_AGE);
				//if(allHidden || this.isModalitaStandard()) {
				if(allHidden) {
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
					//if(allHidden || this.isModalitaStandard()) {
					if(allHidden) {
						de.setType(DataElementType.HIDDEN);
					}else {
						de.setType(DataElementType.NUMBER);
						de.setMinValue(-1);
						de.setMaxValue(Integer.MAX_VALUE);
						de.setNote(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_MAX_AGE_SECONDS_NOTE);
					}
					dati.addElement(de);
				}
			}
		}
	}
	
	public CorsConfigurazione getGestioneCors(boolean corsStato, TipoGestioneCORS corsTipo, 
			boolean corsAllAllowOrigins, boolean corsAllAllowHeaders, boolean corsAllAllowMethods,
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

				gestioneCors.setAccessControlAllAllowHeaders(corsAllAllowHeaders ? StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO);
				if(!corsAllAllowHeaders) {
					CorsConfigurazioneHeaders accessControlAllowHeaders = new CorsConfigurazioneHeaders();
					accessControlAllowHeaders.setHeaderList(Arrays.asList(corsAllowHeaders.split(",")));
					gestioneCors.setAccessControlAllowHeaders(accessControlAllowHeaders);
				}
				
				gestioneCors.setAccessControlAllAllowMethods(corsAllAllowMethods ? StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO);
				if(!corsAllAllowMethods) {
					CorsConfigurazioneMethods accessControlAllowMethods = new CorsConfigurazioneMethods();
					accessControlAllowMethods.setMethodList(Arrays.asList(corsAllowMethods.split(",")));
					gestioneCors.setAccessControlAllowMethods(accessControlAllowMethods);
				}

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
	
	public boolean checkDataURLInvocazione() throws Exception {
		
		String urlInvocazionePA = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_URL_INVOCAZIONE_PA);
		
		if(StringUtils.isEmpty(urlInvocazionePA)){
			this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_URL_INVOCAZIONE_PA));
			return false;
		}
		
		if(urlInvocazionePA.contains(" ")) {
			this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CORS_SPAZI_BIANCHI_NON_AMMESSI, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_URL_INVOCAZIONE_PA));   
			return false;
		}
		
		if(!this.checkLength(urlInvocazionePA, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_URL_INVOCAZIONE_PA, 1, 255)) {
			return false;
		}
		
		try{
			org.openspcoop2.utils.regexp.RegExpUtilities.validateUrl(urlInvocazionePA);
		}catch(Exception e){
			this.pd.setMessage(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_URL_INVOCAZIONE_PA + " non correttamente formata: "+e.getMessage());
			return false;
		}
		
		String urlInvocazionePD = this.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_URL_INVOCAZIONE_PD);
		
		/*
		if(StringUtils.isEmpty(urlInvocazionePD)){
			this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX,ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_URL_INVOCAZIONE_PD));
			return false;
		}
		*/
		if(!StringUtils.isEmpty(urlInvocazionePD)){
			if(urlInvocazionePD.contains(" ")) {
				this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CORS_SPAZI_BIANCHI_NON_AMMESSI, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_URL_INVOCAZIONE_PD));   
				return false;
			}
			
			if(!this.checkLength(urlInvocazionePD, ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_URL_INVOCAZIONE_PD, 1, 255)) {
				return false;
			}
		
			try{
				org.openspcoop2.utils.regexp.RegExpUtilities.validateUrl(urlInvocazionePD);
			}catch(Exception e){
				this.pd.setMessage(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_URL_INVOCAZIONE_PD + " non correttamente formata: "+e.getMessage());
				return false;
			}
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
				
				String corsAllAllowHeadersTmp = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_HEADERS);
				boolean corsAllAllowHeaders = ServletUtils.isCheckBoxEnabled(corsAllAllowHeadersTmp);
				if(corsAllAllowHeaders && corsAllAllowOrigins) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CORS_DIPENDENZA, 
							CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_ORIGINS, 
							CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_HEADERS));   
					return false;
				}
				if(!corsAllAllowHeaders) {
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
				}
				
				String corsAllAllowMethodsTmp = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_METHODS);
				boolean corsAllAllowMethods = ServletUtils.isCheckBoxEnabled(corsAllAllowMethodsTmp);
				if(corsAllAllowMethods && corsAllAllowOrigins) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CORS_DIPENDENZA, 
							CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_ORIGINS, 
							CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_METHODS));   
					return false;
				}
				if(!corsAllAllowMethods) {
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
	
	public void addDescrizioneVerificaConnettoreToDati(Vector<DataElement> dati, String server, String labelConnettore, 
			Connettore connettore, boolean registro, String aliasConnettore) throws Exception {
		
		if(server!=null && !"".equals(server)) {
			DataElement de = new DataElement();
			de.setType(DataElementType.TEXT);
			de.setLabel(ConnettoriCostanti.LABEL_SERVER);
			de.setValue(server);
			dati.add(de);
		}
		
		DataElement de = new DataElement();
		de.setType(DataElementType.TEXT);
		de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE);
		de.setValue(labelConnettore);
		dati.add(de);
		
		if(aliasConnettore!=null && !"".equals(aliasConnettore) &&
				(TipiConnettore.HTTP.getNome().equalsIgnoreCase(connettore.getTipo()) ||
				TipiConnettore.HTTPS.getNome().equalsIgnoreCase(connettore.getTipo()))
				){
			Map<String,String> properties = connettore.getProperties();
			String location = properties!=null ? properties.get(CostantiConnettori.CONNETTORE_LOCATION) : null;	
			if(location!=null && !"".equals(location) && location.toLowerCase().startsWith("https")) {
				String nomeConnettore = null;
				try {
					URL url = new URL( location );
					String host = url.getHost();
					if(host==null || "".equals(host)) {
						throw new Exception("L'endpoint '"+host+"' non contiene un host");
					}
					nomeConnettore = host;
					int port = url.getPort();
					if(port>0 && port!=443) {
						nomeConnettore=nomeConnettore+"_"+port;
					}
					
					de = new DataElement();
					de.setType(DataElementType.LINK);
					de.setValue(ConnettoriCostanti.LABEL_DOWNLOAD_CERTIFICATI_SERVER);
					de.setUrl(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT, 
							new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO, ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_CONNETTORE_CERTIFICATO_SERVER),
							new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO, ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_CONNETTORE_CERTIFICATO_SERVER),
							new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_CERTIFICATI_SERVER_ID_CONNETTORE, connettore.getId().longValue()+""),
							new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_CERTIFICATI_SERVER_TIPO_CONNETTORE_REGISTRO, registro ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED),
							new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_CERTIFICATI_SERVER_ALIAS_CONNETTORE, aliasConnettore),
							new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_CERTIFICATI_SERVER_NOME_CONNETTORE, nomeConnettore));
					dati.add(de);
					
				}catch(Exception e) {
					this.log.error("Errore durante l'identificazione dell'endpoint: "+e.getMessage(),e);
				}
			}
		}
		
		if(connettore.getProperties().containsKey(CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT)) {
			
			de = new DataElement();
			de.setType(DataElementType.TEXT);
			de.setLabel(ConnettoriCostanti.LABEL_VERIFICA_CONNETTORE_DETAILS_CONNECTION_TIMEOUT);
			de.setValue(connettore.getProperties().get(CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT));
			dati.add(de);
			
		}
		
		if(connettore.getProperties().containsKey(CostantiConnettori.CONNETTORE_USERNAME)) {
			
			de = new DataElement();
			de.setType(DataElementType.SUBTITLE);
			de.setLabel(ConnettoriCostanti.LABEL_VERIFICA_CONNETTORE_DETAILS_HTTP);
			de.setValue(labelConnettore);
			dati.add(de);
		
			de = new DataElement();
			de.setType(DataElementType.TEXT);
			de.setLabel(ConnettoriCostanti.LABEL_VERIFICA_CONNETTORE_DETAILS_HTTP_USERNAME);
			de.setValue(connettore.getProperties().get(CostantiConnettori.CONNETTORE_USERNAME));
			dati.add(de);
			
			if(connettore.getProperties().containsKey(CostantiConnettori.CONNETTORE_PASSWORD)) {
				
				de = new DataElement();
				de.setType(DataElementType.TEXT);
				de.setLabel(ConnettoriCostanti.LABEL_VERIFICA_CONNETTORE_DETAILS_HTTP_PASSWORD);
				de.setValue(connettore.getProperties().get(CostantiConnettori.CONNETTORE_PASSWORD));
				dati.add(de);
				
			}
			
		}
		
		if(connettore.getProperties().containsKey(CostantiConnettori.CONNETTORE_TOKEN_POLICY)) {
			
			de = new DataElement();
			de.setType(DataElementType.SUBTITLE);
			de.setLabel(ConnettoriCostanti.LABEL_VERIFICA_CONNETTORE_DETAILS_TOKEN);
			de.setValue(labelConnettore);
			dati.add(de);
		
			de = new DataElement();
			de.setType(DataElementType.TEXT);
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_TOKEN_POLICY);
			de.setValue(connettore.getProperties().get(CostantiConnettori.CONNETTORE_TOKEN_POLICY));
			dati.add(de);
			
		}
		
		boolean trustAllCerts = false;
		if(connettore.getProperties().containsKey(CostantiConnettori.CONNETTORE_HTTPS_TRUST_ALL_CERTS)) {
			String v = connettore.getProperties().get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_ALL_CERTS);
			if("true".equalsIgnoreCase(v)) {
				trustAllCerts = true;
			}
		}
		
		if(connettore.getProperties().containsKey(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_LOCATION) || 
				trustAllCerts) {
		
			de = new DataElement();
			de.setType(DataElementType.SUBTITLE);
			de.setLabel(ConnettoriCostanti.LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS);
			de.setValue(labelConnettore);
			dati.add(de);
		
			if(connettore.getProperties().containsKey(CostantiConnettori.CONNETTORE_HTTPS_SSL_TYPE)) {
				
				de = new DataElement();
				de.setType(DataElementType.TEXT);
				de.setLabel(ConnettoriCostanti.LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_SSL_TYPE);
				de.setValue(connettore.getProperties().get(CostantiConnettori.CONNETTORE_HTTPS_SSL_TYPE));
				dati.add(de);
				
			}
			if(connettore.getProperties().containsKey(CostantiConnettori.CONNETTORE_HTTPS_HOSTNAME_VERIFIER)) {
				
				de = new DataElement();
				de.setType(DataElementType.TEXT);
				de.setLabel(ConnettoriCostanti.LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_HOSTNAME_VERIFIER);
				de.setValue(connettore.getProperties().get(CostantiConnettori.CONNETTORE_HTTPS_HOSTNAME_VERIFIER));
				dati.add(de);
				
			}
				
			de = new DataElement();
			de.setType(DataElementType.TEXT);
			de.setLabel(ConnettoriCostanti.LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_TRUSTSTORE);
			if(trustAllCerts) {
				de.setValue(ConnettoriCostanti.LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_TRUST_ALL_CERTS);
			}
			else {
				de.setValue(connettore.getProperties().get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_LOCATION));
			}
			dati.add(de);
				
			if(connettore.getProperties().containsKey(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_CRLs)) {
				
				de = new DataElement();
				de.setType(DataElementType.TEXT);
				de.setLabel(ConnettoriCostanti.LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_TRUSTSTORE_CRLs);
				de.setValue(connettore.getProperties().get(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_CRLs));
				dati.add(de);
				
			}
			
			if(connettore.getProperties().containsKey(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_LOCATION)) {
				
				de = new DataElement();
				de.setType(DataElementType.TEXT);
				de.setLabel(ConnettoriCostanti.LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_KEYSTORE);
				de.setValue(connettore.getProperties().get(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_LOCATION));
				dati.add(de);
				
				if(connettore.getProperties().containsKey(CostantiConnettori.CONNETTORE_HTTPS_KEY_ALIAS)) {
					
					de = new DataElement();
					de.setType(DataElementType.TEXT);
					de.setLabel(ConnettoriCostanti.LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_KEY_ALIAS);
					de.setValue(connettore.getProperties().get(CostantiConnettori.CONNETTORE_HTTPS_KEY_ALIAS));
					dati.add(de);
					
				}
				
			}
		}
		
		if(connettore.getProperties().containsKey(CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME)) {
			
			de = new DataElement();
			de.setType(DataElementType.SUBTITLE);
			de.setLabel(ConnettoriCostanti.LABEL_VERIFICA_CONNETTORE_DETAILS_PROXY);
			de.setValue(labelConnettore);
			dati.add(de);
		
			de = new DataElement();
			de.setType(DataElementType.TEXT);
			de.setLabel(ConnettoriCostanti.LABEL_VERIFICA_CONNETTORE_DETAILS_PROXY_HOSTNAME);
			de.setValue(connettore.getProperties().get(CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME));
			dati.add(de);
			
			if(connettore.getProperties().containsKey(CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT)) {
				
				de = new DataElement();
				de.setType(DataElementType.TEXT);
				de.setLabel(ConnettoriCostanti.LABEL_VERIFICA_CONNETTORE_DETAILS_PROXY_PORT);
				de.setValue(connettore.getProperties().get(CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT));
				dati.add(de);
				
			}
			
			if(connettore.getProperties().containsKey(CostantiConnettori.CONNETTORE_HTTP_PROXY_USERNAME)) {
				
				de = new DataElement();
				de.setType(DataElementType.TEXT);
				de.setLabel(ConnettoriCostanti.LABEL_VERIFICA_CONNETTORE_DETAILS_PROXY_USERNAME);
				de.setValue(connettore.getProperties().get(CostantiConnettori.CONNETTORE_HTTP_PROXY_USERNAME));
				dati.add(de);
				
			}

			if(connettore.getProperties().containsKey(CostantiConnettori.CONNETTORE_HTTP_PROXY_PASSWORD)) {
				
				de = new DataElement();
				de.setType(DataElementType.TEXT);
				de.setLabel(ConnettoriCostanti.LABEL_VERIFICA_CONNETTORE_DETAILS_PROXY_PASSWORD);
				de.setValue(connettore.getProperties().get(CostantiConnettori.CONNETTORE_HTTP_PROXY_PASSWORD));
				dati.add(de);
				
			}
			
		}
		
	}
	
	public void addVerificaConnettoreSceltaAlias(List<String> aliases,Vector<DataElement> dati) throws Exception {
		
		DataElement de = new DataElement();
		de.setType(DataElementType.SELECT);
		List<String> values = new ArrayList<String>();
		List<String> labels = new ArrayList<String>();
		values.add(CostantiControlStation.LABEL_VERIFICA_CONNETTORE_TUTTI_I_NODI);
		labels.add(CostantiControlStation.LABEL_VERIFICA_CONNETTORE_TUTTI_I_NODI);
		values.addAll(this.confCore.getJmxPdD_aliases());
		for (String alias : this.confCore.getJmxPdD_aliases()) {
			labels.add(this.confCore.getJmxPdD_descrizione(alias));
		}
		de.setValues(values);
		de.setLabels(labels);
		de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER);
		de.setLabel(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER);
		de.setSize(this.getSize());
		//de.setPostBack(true);
		dati.addElement(de);
		
	}
	
	public void addVerificaConnettoreHidden(Vector<DataElement> dati,
			String id, String idsogg,  String idAsps, String idFruizione,
			long idConnettore, boolean accessoDaGruppi, boolean connettoreRegistro) throws Exception {

		DataElement de = new DataElement();
		de.setType(DataElementType.HIDDEN);
		de.setName(CostantiControlStation.PARAMETRO_ID);
		de.setValue(id);
		dati.addElement(de);
		
		de = new DataElement();
		de.setType(DataElementType.HIDDEN);
		de.setName(CostantiControlStation.PARAMETRO_ID_SOGGETTO);
		de.setValue(idsogg);
		dati.addElement(de);
		
		de = new DataElement();
		de.setType(DataElementType.HIDDEN);
		de.setName(CostantiControlStation.PARAMETRO_ID_ASPS);
		de.setValue(idAsps);
		dati.addElement(de);
		
		de = new DataElement();
		de.setType(DataElementType.HIDDEN);
		de.setName(CostantiControlStation.PARAMETRO_ID_FRUIZIONE);
		de.setValue(idFruizione);
		dati.addElement(de);
		
		de = new DataElement();
		de.setType(DataElementType.HIDDEN);
		de.setName(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ID);
		de.setValue(idConnettore+"");
		dati.addElement(de);
		
		de = new DataElement();
		de.setType(DataElementType.HIDDEN);
		de.setName(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI);
		de.setValue(accessoDaGruppi+"");
		dati.addElement(de);
		
		de = new DataElement();
		de.setType(DataElementType.HIDDEN);
		de.setName(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO);
		de.setValue(connettoreRegistro+"");
		dati.addElement(de);
		
	}
	
	public boolean isConnettoreRidefinito(PortaApplicativa paDefault,	PortaApplicativaServizioApplicativo paSADefault,
			PortaApplicativa paCurrent, PortaApplicativaServizioApplicativo paSACurrent) {
		boolean connettoreRidefinito = (
				(
					paDefault.getServizioApplicativoDefault() == null && (paCurrent.getServizioApplicativoDefault() != null || paSACurrent.getNome().equals(paCurrent.getNome()))
				) || (
					paDefault.getServizioApplicativoDefault() != null && 
						(
							paCurrent.getServizioApplicativoDefault() == null 
							|| 
							!paDefault.getServizioApplicativoDefault().equals(paCurrent.getServizioApplicativoDefault())
							||
							paDefault.sizeServizioApplicativoList()!=paCurrent.sizeServizioApplicativoList()
							||
							(paDefault.sizeServizioApplicativoList()==1 && paCurrent.sizeServizioApplicativoList()==1 && !paDefault.getServizioApplicativo(0).getNome().equals(paCurrent.getServizioApplicativo(0).getNome()))
						)
					//!paSADefault.getNome().equals(paSACurrent.getNome()))
				)
			);
		return connettoreRidefinito;
	}
	
	public String getTooltipConnettore(ServizioApplicativo sa, org.openspcoop2.core.config.InvocazioneServizio is, boolean addExtInfo) {
		StringBuilder sbCon = new StringBuilder();
		if(ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(sa.getTipo())) {
			sbCon.append(ConnettoriCostanti.LABEL_SERVER);
			sbCon.append(": ");
			sbCon.append(sa.getNome());
			sbCon.append(CostantiControlStation.TOOLTIP_BREAK_LINE);
		}
		sbCon.append(this.getLabelConnettore(is, addExtInfo, true));
		return sbCon.toString();
	}
	
	public String getLabelConnettore(ServizioApplicativo sa, org.openspcoop2.core.config.InvocazioneServizio is, boolean addExtInfo) {
		StringBuilder sbCon = new StringBuilder();
		if(ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(sa.getTipo())) {
			//sbCon.append(sa.getNome());
			//sbCon.append(" ");
		}
		sbCon.append(this.getLabelConnettore(is, addExtInfo, false));
		return sbCon.toString();
	}
	
	public String getLabelConnettore(org.openspcoop2.core.config.InvocazioneServizio is, boolean addExtInfo, boolean tooltip) {
		String urlConnettore = this.getLabelConnettore(is.getConnettore(), addExtInfo, tooltip);
		
		if(is.getGetMessage()!=null && StatoFunzionalita.ABILITATO.equals(is.getGetMessage())) {
			urlConnettore = urlConnettore + " [MessageBox]";
		}
		
		return urlConnettore;
	}
	public String getLabelConnettore(org.openspcoop2.core.registry.Connettore connettore, boolean addExtInfo, boolean tooltip) {
		return this.getLabelConnettore(connettore.mappingIntoConnettoreConfigurazione(), addExtInfo, tooltip);
	}
	public String getLabelConnettore(org.openspcoop2.core.config.Connettore connettore, boolean addExtInfo, boolean tooltip) {
		String urlConnettore = "";
		
		List<org.openspcoop2.core.config.Property> cp = connettore.getPropertyList();
		
		//TipiConnettore.HTTP.getNome() e anche TipiConnettore.HTTPS.getNome() -> location
		//TipiConnettore.DISABILITATO.getNome() ci scrivi "disabilitato"
		//TipiConnettore.FILE.getNome() CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_FILE
		//TipiConnettore.JMS.compareTo() CostantiConnettori.CONNETTORE_LOCATION
//			TipiConnettore.NULL 
//			TipiConnettore.CUSTOM -> connettore custom
		String tipo = connettore.getTipo();
		
		TipiConnettore tipoC = TipiConnettore.toEnumFromName(connettore.getTipo());
		String labelC = connettore.getTipo();
		if(tipoC!=null) {
			labelC = tipoC.getLabel();
		}
		String tipoLabel = "[" + labelC + "] ";
		if ((connettore.getCustom()!=null && connettore.getCustom()) && 
				!connettore.getTipo().equals(CostantiDB.CONNETTORE_TIPO_HTTPS) && 
				!connettore.getTipo().equals(CostantiDB.CONNETTORE_TIPO_FILE)) {
			tipo = ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM;
		}  

		if(tipo.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM)) {
			if(this.connettoriCore.isConfigurazionePluginsEnabled()) {
				tipoLabel = "[" + TipiConnettore.CUSTOM.getLabel() + "] ";
				Plugin plugin = null;
				try {
					plugin = this.confCore.getPlugin(TipoPlugin.CONNETTORE,connettore.getTipo(), false);
				}catch(Throwable e) {}
				if(plugin!=null) {
					urlConnettore = tipoLabel + plugin.getLabel();
				}
				else {
					// backward compatibility
					urlConnettore = tipoLabel + connettore.getTipo();
				}
			}
			else {
				urlConnettore = tipoLabel + ConnettoriCostanti.LABEL_CONNETTORE_CUSTOM;
			}
		} else	if(tipo.equals(TipiConnettore.DISABILITATO.getNome())) {
			urlConnettore = CostantiControlStation.DEFAULT_VALUE_DISABILITATO;
		} else if(tipo.equals(TipiConnettore.NULL.getNome())) {
			urlConnettore = tipoLabel + ConnettoreNULL.LOCATION;
		} else if(tipo.equals(TipiConnettore.NULLECHO.getNome())) {
			urlConnettore = tipoLabel + ConnettoreNULLEcho.LOCATION;
		} else {  
			String propertyName = CostantiConnettori.CONNETTORE_LOCATION;
			if(tipo.equals(TipiConnettore.FILE.getNome()))
				propertyName = CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_FILE;
		
			// Prefix token
			String token = "";
			if(addExtInfo) {
				if(tipo.equals(TipiConnettore.HTTP.getNome()) || tipo.equals(TipiConnettore.HTTPS.getNome())) {
					for (int i = 0; i < connettore.sizePropertyList(); i++) {
						org.openspcoop2.core.config.Property singlecp = cp.get(i);
						if (singlecp.getNome().equals(CostantiConnettori.CONNETTORE_TOKEN_POLICY) && 
								singlecp.getValore()!=null && StringUtils.isNotEmpty(singlecp.getValore())) {
							if(tooltip) {
								token = "[token: "+singlecp.getValore()+"]\n";
							}
							else {
								token = "[token] ";
							}
						}
					}
				}
			}
			
			for (int i = 0; i < connettore.sizePropertyList(); i++) {
				org.openspcoop2.core.config.Property singlecp = cp.get(i);
				if (singlecp.getNome().equals(propertyName)) {
					if(!tipo.equals(TipiConnettore.HTTP.getNome()) && !tipo.equals(TipiConnettore.HTTPS.getNome())) {
						urlConnettore = tipoLabel + singlecp.getValore();
					}
					else {
						urlConnettore = token + singlecp.getValore();
					}
					
					break;
				}
			}
		}
		return urlConnettore;
	}
	
	public void addConfigurazioneResponseCachingPorteToDati(TipoOperazione tipoOperazione,Vector<DataElement> dati, boolean showStato, String statoResponseCachingPorta, boolean responseCachingEnabled, int responseCachingSeconds,
			boolean responseCachingMaxResponseSize, long responseCachingMaxResponseSizeBytes,
			boolean responseCachingDigestUrlInvocazione, boolean responseCachingDigestHeaders,
			boolean responseCachingDigestPayload, String responseCachingDigestHeadersNomiHeaders, StatoFunzionalitaCacheDigestQueryParameter responseCachingDigestQueryParameter, String responseCachingDigestNomiParametriQuery, 
			boolean responseCachingCacheControlNoCache, boolean responseCachingCacheControlMaxAge, boolean responseCachingCacheControlNoStore, boolean visualizzaLinkConfigurazioneRegola, 
			String servletResponseCachingConfigurazioneRegolaList, List<Parameter> paramsResponseCachingConfigurazioneRegolaList, int numeroResponseCachingConfigurazioneRegola) throws Exception {
		
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
			this.addResponseCachingToDati(dati, responseCachingEnabled, responseCachingSeconds, responseCachingMaxResponseSize, 
					responseCachingMaxResponseSizeBytes, responseCachingDigestUrlInvocazione, responseCachingDigestHeaders, responseCachingDigestPayload, false, responseCachingDigestHeadersNomiHeaders, responseCachingDigestQueryParameter, responseCachingDigestNomiParametriQuery,
					responseCachingCacheControlNoCache, responseCachingCacheControlMaxAge, responseCachingCacheControlNoStore, visualizzaLinkConfigurazioneRegola,
					servletResponseCachingConfigurazioneRegolaList, paramsResponseCachingConfigurazioneRegolaList, numeroResponseCachingConfigurazioneRegola,
					false);
		}
	}
	
	public void addResponseCachingToDati(Vector<DataElement> dati, boolean responseCachingEnabled, int responseCachingSeconds,
			boolean responseCachingMaxResponseSize, long responseCachingMaxResponseSizeBytes,
			boolean responseCachingDigestUrlInvocazione, boolean responseCachingDigestHeaders,
			boolean responseCachingDigestPayload, boolean addTitle, String responseCachingDigestHeadersNomiHeaders, StatoFunzionalitaCacheDigestQueryParameter responseCachingDigestQueryParameter, String responseCachingDigestNomiParametriQuery,  
			boolean responseCachingCacheControlNoCache, boolean responseCachingCacheControlMaxAge, boolean responseCachingCacheControlNoStore, boolean visualizzaLinkConfigurazioneRegola,
			String servletResponseCachingConfigurazioneRegolaList, List<Parameter> paramsResponseCachingConfigurazioneRegolaList, int numeroResponseCachingConfigurazioneRegola,
			boolean allHidden) {
		DataElement de;
		if(!allHidden && addTitle) {
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_CONFIGURAZIONE_RESPONSE_CACHING);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}
		
		de = new DataElement();
		de.setLabel(addTitle ?  CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_STATO : "");
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_STATO);
		if(allHidden) {
			de.setType(DataElementType.HIDDEN);
		}
		else {
			de.setType(DataElementType.SELECT);
			de.setPostBack(true);
			de.setValues(CostantiControlStation.SELECT_VALUES_STATO_FUNZIONALITA);
			de.setSelected(responseCachingEnabled ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue());
		}
		de.setValue(responseCachingEnabled ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue());
		dati.addElement(de);
		
		if(responseCachingEnabled) {
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_TIMEOUT);
			de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_TIMEOUT);
			de.setValue(responseCachingSeconds+"");
			if(allHidden) {
				de.setType(DataElementType.HIDDEN);
			}
			else {
				de.setType(DataElementType.NUMBER);
				de.setMinValue(1);
				de.setMaxValue(Integer.MAX_VALUE);
			}
			dati.addElement(de);
			
			
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_MAX_RESPONSE_SIZE);
			de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_MAX_RESPONSE_SIZE);
			if(allHidden) {
				de.setType(DataElementType.HIDDEN);
			}
			else {
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(responseCachingMaxResponseSize);
				de.setPostBack(true);
			}
			de.setValue(responseCachingMaxResponseSize+"");
			dati.addElement(de);
			
			if(responseCachingMaxResponseSize) {
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_MAX_RESPONSE_SIZE_BYTES);
				de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_MAX_RESPONSE_SIZE_BYTES);
				de.setValue(responseCachingMaxResponseSizeBytes+"");
				if(allHidden) {
					de.setType(DataElementType.HIDDEN);
				}
				else {
					de.setType(DataElementType.NUMBER);
					de.setMinValue(1);
					de.setMaxValue(Integer.MAX_VALUE);
				}
				dati.addElement(de);
			}
			
			if(!allHidden) {
				de = new DataElement();
				de.setType(DataElementType.SUBTITLE);
				de.setLabel(CostantiControlStation.LABEL_CONFIGURAZIONE_RESPONSE_CACHING_GENERAZIONE_HASH);
				dati.addElement(de);
			}
			
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_URI_INVOCAZIONE);
			de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_URI_INVOCAZIONE);
			if(allHidden) {
				de.setType(DataElementType.HIDDEN);
			}
			else {
				de.setType(DataElementType.SELECT);
				de.setValues(CostantiControlStation.SELECT_VALUES_STATO_FUNZIONALITA);
				de.setSelected(responseCachingDigestUrlInvocazione ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue());
			}
			de.setValue(responseCachingDigestUrlInvocazione ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue());
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_QUERY_PARAMETERS);
			de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_QUERY_PARAMETERS);
			if(allHidden) {
				de.setType(DataElementType.HIDDEN);
			}
			else {
				de.setType(DataElementType.SELECT);
				de.setValues(CostantiControlStation.SELECT_VALUES_STATO_FUNZIONALITA_RESPONSE_CACHING_DIGEST_QUERY_PARAMETERS);
				if(responseCachingDigestQueryParameter!=null) {
					de.setSelected(responseCachingDigestQueryParameter.getValue());
				}
				de.setPostBack(true);
			}
			if(responseCachingDigestQueryParameter!=null) {
				de.setValue(responseCachingDigestQueryParameter.getValue());
			}
			dati.addElement(de);
			
			if(StatoFunzionalitaCacheDigestQueryParameter.SELEZIONE_PUNTUALE.equals(responseCachingDigestQueryParameter)) {
				de = new DataElement();
//				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_QUERY_PARAMETERS_NOMI);
				de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_QUERY_PARAMETERS_NOMI);
				if(allHidden) {
					de.setType(DataElementType.HIDDEN);
				}
				else {
					de.setType(DataElementType.TEXT_EDIT);
					de.enableTags();
					de.setNote(CostantiControlStation.NOTE_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_QUERY_PARAMETERS_NOMI);
//					de.setRequired(true);
				}
				de.setValue(responseCachingDigestNomiParametriQuery);
				dati.addElement(de);
			}
			
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_PAYLOAD);
			de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_PAYLOAD);
			if(allHidden) {
				de.setType(DataElementType.HIDDEN);
			}
			else {
				de.setType(DataElementType.SELECT);
				de.setValues(CostantiControlStation.SELECT_VALUES_STATO_FUNZIONALITA);
				de.setSelected(responseCachingDigestPayload ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue());
			}
			de.setValue(responseCachingDigestPayload ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue());
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_HEADERS);
			de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_HEADERS);
			if(allHidden) {
				de.setType(DataElementType.HIDDEN);
			}
			else {
				de.setType(DataElementType.SELECT);
				de.setValues(CostantiControlStation.SELECT_VALUES_STATO_FUNZIONALITA);
				de.setSelected(responseCachingDigestHeaders ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue());
				de.setPostBack(true);
			}
			de.setValue(responseCachingDigestHeaders ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue());
			dati.addElement(de);
			
			if(responseCachingDigestHeaders) {
				de = new DataElement();
//				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_HEADERS_NOMI_HEADERS);
				de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_HEADERS_NOMI_HEADERS);
				if(allHidden) {
					de.setType(DataElementType.HIDDEN);
				}
				else {
					de.setType(DataElementType.TEXT_EDIT);
					de.enableTags();
					de.setNote(CostantiControlStation.NOTE_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_HEADERS_NOMI_HEADERS);
//					de.setRequired(true);
				}
				de.setValue(responseCachingDigestHeadersNomiHeaders);
				dati.addElement(de);
			}
			
			if(!allHidden) {
				de = new DataElement();
				de.setType(DataElementType.SUBTITLE);
				de.setLabel(CostantiControlStation.LABEL_CONFIGURAZIONE_RESPONSE_CACHING_CACHE_CONTROL);
				dati.addElement(de);
			}
			
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CACHE_CONTROL_NO_CACHE);
			de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CACHE_CONTROL_NO_CACHE);
			if(allHidden) {
				de.setType(DataElementType.HIDDEN);
			}
			else {
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(responseCachingCacheControlNoCache);
			}
			de.setValue(responseCachingCacheControlNoCache+"");
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CACHE_CONTROL_MAX_AGE);
			de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CACHE_CONTROL_MAX_AGE);
			if(allHidden) {
				de.setType(DataElementType.HIDDEN);
			}
			else {
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(responseCachingCacheControlMaxAge);
			}
			de.setValue(responseCachingCacheControlMaxAge+"");
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CACHE_CONTROL_NO_STORE);
			de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CACHE_CONTROL_NO_STORE);
			if(allHidden) {
				de.setType(DataElementType.HIDDEN);
			}
			else {
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(responseCachingCacheControlNoStore);
			}
			de.setValue(responseCachingCacheControlNoStore+"");
			dati.addElement(de);
			
			
						
			if(!allHidden && visualizzaLinkConfigurazioneRegola) {
				
				de = new DataElement();
				de.setType(DataElementType.SUBTITLE);
				de.setLabel(CostantiControlStation.LABEL_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIOME_AVANZATA);
				dati.addElement(de);
				
				de = new DataElement();
				de.setType(DataElementType.LINK);
				boolean contaListeFromSession = ServletUtils.getContaListeFromSession(this.session) != null ? ServletUtils.getContaListeFromSession(this.session) : false;
				if (contaListeFromSession)
					de.setValue(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLE+" (" + numeroResponseCachingConfigurazioneRegola + ")");
				else
					de.setValue(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLE);
				de.setUrl(servletResponseCachingConfigurazioneRegolaList, paramsResponseCachingConfigurazioneRegolaList.toArray(new Parameter[paramsResponseCachingConfigurazioneRegolaList.size()]));
				dati.addElement(de);
			}
		}
	}
	
	public ResponseCachingConfigurazione getResponseCaching(boolean responseCachingEnabled, int responseCachingSeconds, boolean responseCachingMaxResponseSize, long responseCachingMaxResponseSizeBytes,
			boolean responseCachingDigestUrlInvocazione, boolean responseCachingDigestHeaders,	boolean responseCachingDigestPayload, String responseCachingDigestHeadersNomiHeaders, StatoFunzionalitaCacheDigestQueryParameter responseCachingDigestQueryParameter, String responseCachingDigestNomiParametriQuery, 
			boolean responseCachingCacheControlNoCache, boolean responseCachingCacheControlMaxAge, boolean responseCachingCacheControlNoStore,List<ResponseCachingConfigurazioneRegola> listaRegoleCachingConfigurazione) {
		
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
				hashGenerator.setQueryParameters(responseCachingDigestQueryParameter);
				if(StringUtils.isNotEmpty(responseCachingDigestNomiParametriQuery)) {
					hashGenerator.setQueryParameterList(Arrays.asList(responseCachingDigestNomiParametriQuery.split(",")));
				}
				hashGenerator.setHeaders(responseCachingDigestHeaders ? StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO);
				if(StringUtils.isNotEmpty(responseCachingDigestHeadersNomiHeaders)) {
					hashGenerator.setHeaderList(Arrays.asList(responseCachingDigestHeadersNomiHeaders.split(",")));
				}
				
				responseCaching.setHashGenerator(hashGenerator);
			}
			
			ResponseCachingConfigurazioneControl control = new ResponseCachingConfigurazioneControl();
			
			control.setNoCache(responseCachingCacheControlNoCache);
			control.setMaxAge(responseCachingCacheControlMaxAge);
			control.setNoStore(responseCachingCacheControlNoStore);
			
			responseCaching.setControl(control);
			
			if(listaRegoleCachingConfigurazione!= null) {
				for (ResponseCachingConfigurazioneRegola regola : listaRegoleCachingConfigurazione) {
					responseCaching.addRegola(regola);
				}
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
	
	public int numeroRegoleResponseCaching(ResponseCachingConfigurazione configurazione) {
		if(configurazione == null)
			return 0;
		
		if(configurazione.getStato().equals(StatoFunzionalita.ABILITATO))
			return configurazione.sizeRegolaList();
		
		return 0;
	}
	
	public int numeroRegoleProxyPass(ConfigurazioneUrlInvocazione configurazione) {
		if(configurazione == null)
			return 0;
		
		return configurazione.sizeRegolaList();
	}
	
	public int numeroPluginsRegistroArchivi()  throws Exception {
		return this.confCore.numeroPluginsArchiviList();
	}
	
	public int numeroPluginsRegistroClassi() throws Exception {
		return this.confCore.numeroPluginsClassiList();
	}
	
	public boolean isCorsAbilitato(CorsConfigurazione configurazione) {
		boolean abilitato = false;
		
		if(configurazione == null)
			return false;
		
		if(configurazione.getStato().equals(StatoFunzionalita.ABILITATO))
			return true;
		
		
		return abilitato;
	}
	
	public int numeroCanali(CanaliConfigurazione configurazione) { 
		if(configurazione == null || configurazione.getStato() == null)
			return 0;
		
		if(configurazione.getStato().equals(StatoFunzionalita.ABILITATO))
			return configurazione.sizeCanaleList();
		
		return 0;
	}
	
	public int numeroNodi(CanaliConfigurazione configurazione) { 
		if(configurazione == null || configurazione.getStato() == null)
			return 0;
		
		if(configurazione.getStato().equals(StatoFunzionalita.ABILITATO))
			return configurazione.sizeNodoList();
		
		return 0;
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
		
		String responseCachingDigestQueryTmp = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_QUERY_PARAMETERS);
		StatoFunzionalitaCacheDigestQueryParameter stato = null; 
		if(responseCachingDigestQueryTmp!=null) {
			stato = StatoFunzionalitaCacheDigestQueryParameter.toEnumConstant(responseCachingDigestQueryTmp, true);
		}
		if(StatoFunzionalitaCacheDigestQueryParameter.SELEZIONE_PUNTUALE.equals(stato)) {
			// se e' abilitato il salvataggio dei parametri della query bisogna indicare quali si vuole salvare
			String responseCachingDigestNomiQueryParameters =  this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_QUERY_PARAMETERS_NOMI);
			if(StringUtils.isNotEmpty(responseCachingDigestNomiQueryParameters)) {
				List<String> asList = Arrays.asList(responseCachingDigestNomiQueryParameters.split(","));
				for (String string : asList) {
					if(string.contains(" ")) {
						this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CORS_SPAZI_BIANCHI_NON_AMMESSI, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_QUERY_PARAMETERS_NOMI));   
						return false;
					}
				}
			} else {
				this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CORS_CAMPO_OBBLIGATORIO, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_QUERY_PARAMETERS_NOMI));   
				return false;
			}
		}
		
		String responseCachingDigestHeadersTmp = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_HEADERS);
		boolean responseCachingDigestHeaders = ServletUtils.isCheckBoxEnabled(responseCachingDigestHeadersTmp);
		if(responseCachingDigestHeaders) {
			// se e' abilitato il salvataggio degli headers bisogna indicare quali si vuole salvare
			String responseCachingDigestHeadersNomiHeaders =  this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_HEADERS_NOMI_HEADERS);
			if(StringUtils.isNotEmpty(responseCachingDigestHeadersNomiHeaders)) {
				List<String> asList = Arrays.asList(responseCachingDigestHeadersNomiHeaders.split(","));
				for (String string : asList) {
					if(string.contains(" ")) {
						this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CORS_SPAZI_BIANCHI_NON_AMMESSI, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_HEADERS_NOMI_HEADERS));   
						return false;
					}
				}
			} else {
				this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_CORS_CAMPO_OBBLIGATORIO, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_HEADERS_NOMI_HEADERS));   
				return false;
			}
		}
		
		return true;
	}
	
	public boolean checkRegolaResponseCaching() throws Exception {
		
		String returnCode = getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE);
		String statusMinS = getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE_MIN);
		String statusMaxS = getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE_MAX);
		@SuppressWarnings("unused")
		String faultS = getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_FAULT);
		String cacheSecondsS = getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_CACHE_TIMEOUT_SECONDS);
		
		if(_checkReturnCode(returnCode, statusMinS, statusMaxS, 
				CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE, 
				CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE_MIN, 
				CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE_MAX)==false) {
			return false;
		}
		
		Integer cacheSeconds = null;
		if(StringUtils.isNotEmpty(cacheSecondsS)) {
			try {
				cacheSeconds = Integer.parseInt(cacheSecondsS);
				
				if(cacheSeconds < 1) {
					this.pd.setMessage("Il valore inserito nel campo "+ CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_CACHE_TIMEOUT_SECONDS + " non &egrave; valido, sono ammessi valori compresi tra 1 e 999.");
					return false;
				}
			}catch(Exception e) {
				this.pd.setMessage("Il formato del campo "+ CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_CACHE_TIMEOUT_SECONDS + " non &egrave; valido.");
				return false;
			}
		}
		
		return true;
	}
	
	public boolean _checkReturnCode(String returnCode, String statusMinS, String statusMaxS,
			String labelReturnCode, String labelReturnCodeMin, String labelReturnCodeMax) throws Exception {
		
		Integer statusMin = null;
		Integer statusMax = null;
		
		if(!returnCode.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_QUALSIASI)) {
			
			if(returnCode.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_ESATTO)) {
				if(StringUtils.isEmpty(statusMinS)) {
					this.pd.setMessage("Il campo "+ labelReturnCode + " &egrave; obbligatorio.");
					return false;
				}
			}
			
			if(returnCode.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_INTERVALLO)) {
				if(StringUtils.isEmpty(statusMinS) || StringUtils.isEmpty(statusMaxS)) {
					this.pd.setMessage("Tutt gli intervalli del campo "+ labelReturnCode + " sono obbligatori.");
					return false;
				}
			}
			
			if(StringUtils.isNotEmpty(statusMinS)) {
				try {
					statusMin = Integer.parseInt(statusMinS);
					
					if(statusMin < 200 || statusMin > 599) {
						if(returnCode.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_ESATTO)) {
							this.pd.setMessage("Il valore inserito nel campo "+ labelReturnCode + " non &egrave; valido, sono ammessi valori compresi tra 200 e 599.");
						}
						else {
							this.pd.setMessage("Il valore inserito nell'intervallo sinistro non &egrave; valido, sono ammessi valori compresi tra 200 e 599.");
						}
						return false;
					}
					// return code esatto, ho salvato lo stesso valore nel campo return code;
					if(returnCode.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_ESATTO))
						statusMax = statusMin;
				}catch(Exception e) {
					this.pd.setMessage("Il formato del campo "+ labelReturnCodeMin + " non &egrave; valido.");
					return false;
				}
			}
			
			if(returnCode.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_INTERVALLO)) {
				if(StringUtils.isNotEmpty(statusMaxS)) {
					try {
						statusMax = Integer.parseInt(statusMaxS);
						
						if(statusMax < 200 || statusMax > 599) {
							this.pd.setMessage("Il valore inserito nell'intervallo destro non &egrave; valido, sono ammessi valori compresi tra 200 e 599.");
							return false;
						}
					}catch(Exception e) {
						this.pd.setMessage("Il formato del campo "+ labelReturnCodeMax + " non &egrave; valido.");
						return false;
					}
				}
			}
			
			if(returnCode.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_INTERVALLO)) {
				if(statusMax!=null && statusMin!=null) {
					if(statusMin>=statusMax) {
						this.pd.setMessage("Il valore inserito nell'intervallo sinistro deve essere minore del valore inserito nell'intervallo destro.");
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	public Vector<DataElement> addResponseCachingConfigurazioneRegola(TipoOperazione tipoOP, String returnCode, String statusMin, String statusMax, String fault, String cacheSeconds, Vector<DataElement> dati) {
		
		DataElement dataElement = new DataElement();
		dataElement.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA);
		dataElement.setType(DataElementType.TITLE);
		dati.add(dataElement);
		
		DataElement de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE);
		de.setLabels(CostantiControlStation.SELECT_LABELS_CONFIGURAZIONE_RETURN_CODE);
		de.setValues(CostantiControlStation.SELECT_VALUES_CONFIGURAZIONE_RETURN_CODE);
		de.setType(DataElementType.SELECT);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE);
		de.setPostBack(true);
		de.setSelected(returnCode);
		dati.addElement(de);
		
		if(!CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_QUALSIASI.equals(returnCode)) {
			if(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_ESATTO.equals(returnCode)) {
				de = this.getHttpReturnCodeDataElement(CostantiControlStation.LABEL_EMPTY, 
						CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE_MIN, 
						statusMin, true);
				dati.addElement(de);
			}
			
			if(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_INTERVALLO.equals(returnCode)) {
				de = getHttpReturnCodeIntervallDataElement(CostantiControlStation.LABEL_EMPTY, 
						CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE_MIN,
						CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE_MAX,
						statusMin,
						statusMax,
						true);
				dati.addElement(de);
			}
		} 
		
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_CACHE_TIMEOUT_SECONDS);
		de.setValue(cacheSeconds+ "");
		de.setType(DataElementType.NUMBER);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_CACHE_TIMEOUT_SECONDS);
		de.setSize( getSize());
		de.setMinValue(1);
		de.reloadMinValue(false);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_FAULT);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_FAULT);
		de.setType(DataElementType.CHECKBOX);
		de.setSelected(fault);
		de.setValue(fault+"");
		dati.addElement(de);

		return dati;
	}
	
	public Vector<DataElement> addTrasformazioneRispostaToDatiOpAdd(Vector<DataElement> dati, String idTrasformazione, 
			org.openspcoop2.core.registry.constants.ServiceBinding serviceBinding,
			String nome, String returnCode, String statusMin, String statusMax, String pattern, String contentType) throws Exception {
		return addTrasformazioneRispostaToDati(TipoOperazione.ADD, dati, 0, null, false, idTrasformazione, null, 
				serviceBinding,
				nome, returnCode, statusMin, statusMax, pattern, contentType, 
				null, null, 0,
				false,false,false,
				false,null,
				null,null,null,null,
				null,
				false,null,
				null,null,null);
	}
	
	public Vector<DataElement> addTrasformazioneRispostaToDati(TipoOperazione tipoOP, Vector<DataElement> dati, long idPorta, TrasformazioneRegolaRisposta risposta, boolean isPortaDelegata, String idTrasformazione, String idTrasformazioneRisposta,
			org.openspcoop2.core.registry.constants.ServiceBinding serviceBindingParam,
			String nome, String returnCode, String statusMin, String statusMax, String pattern, String contentType,
			String servletTrasformazioniRispostaHeadersList, List<Parameter> parametriInvocazioneServletTrasformazioniRispostaHeaders, int numeroTrasformazioniRispostaHeaders,
			boolean trasformazioneContenutoRichiestaAbilitato, boolean trasformazioneRichiestaRestAbilitato, boolean trasformazioneRichiestaSoapAbilitato,
			boolean trasformazioneContenutoRispostaAbilitato, org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione trasformazioneContenutoRispostaTipo, 
			BinaryParameter trasformazioneContenutoRispostaTemplate, String trasformazioneContenutoRispostaTipoCheck, String trasformazioneContenutoRispostaContentType, String trasformazioneContenutoRispostaReturnCode,
			ServiceBinding serviceBindingMessage, 
			boolean trasformazioneRispostaSoapAbilitato, String trasformazioneRispostaSoapEnvelope,  
			org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione trasformazioneRispostaSoapEnvelopeTipo, BinaryParameter trasformazioneRispostaSoapEnvelopeTemplate, String trasformazioneRispostaSoapEnvelopeTipoCheck
			
			) throws Exception {
		
		
		org.openspcoop2.core.registry.constants.ServiceBinding infoServiceBinding = serviceBindingParam;
		if(trasformazioneRichiestaRestAbilitato) {
			infoServiceBinding = org.openspcoop2.core.registry.constants.ServiceBinding.REST;
		}
		else if(trasformazioneRichiestaSoapAbilitato){
			infoServiceBinding = org.openspcoop2.core.registry.constants.ServiceBinding.SOAP;
		}
		
		DataElement de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_CONFIGURAZIONE_TRASFORMAZIONI_TRASFORMAZIONE);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		// Nome
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_NOME);
		de.setValue(nome);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_NOME);
		de.setRequired(true); 
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA);
		de.setType(DataElementType.SUBTITLE);
		dati.addElement(de);
		
		// Id trasformazione hidden
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE);
		de.setName(CostantiControlStation.PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE);
		de.setType(DataElementType.HIDDEN);
		de.setValue(idTrasformazione);
		dati.addElement(de);
		
		// First
		de = new DataElement();
		de.setValue("first");
		de.setType(DataElementType.HIDDEN);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_FIRST);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE);
		de.setName(CostantiControlStation.PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE_RISPOSTA);
		de.setType(DataElementType.HIDDEN);
		de.setValue(idTrasformazioneRisposta);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS);
		de.setLabels(CostantiControlStation.SELECT_LABELS_CONFIGURAZIONE_RETURN_CODE);
		de.setValues(CostantiControlStation.SELECT_VALUES_CONFIGURAZIONE_RETURN_CODE);
		de.setType(DataElementType.SELECT);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS);
		de.setPostBack(true);
		de.setSelected(returnCode);
		dati.addElement(de);
		
		if(!CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_QUALSIASI.equals(returnCode)) {
			if(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_ESATTO.equals(returnCode)) {
				de = this.getHttpReturnCodeDataElement(CostantiControlStation.LABEL_EMPTY, 
						CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS_MIN, 
						statusMin, true);
				dati.addElement(de);
			}
			
			if(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_INTERVALLO.equals(returnCode)) {
				de = getHttpReturnCodeIntervallDataElement(CostantiControlStation.LABEL_EMPTY, 
						CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS_MIN,
						CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS_MAX,
						statusMin,
						statusMax,
						true);
				dati.addElement(de);
			}
		} 
				
		// Content-type
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_CT);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_CT);
		de.setType(DataElementType.TEXT_EDIT);
		de.setValue(contentType);
		de.enableTags();
//		de.setRequired(true);
		DataElementInfo dInfoCT = new DataElementInfo(CostantiControlStation.LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA+" - "+CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_CT);
		dInfoCT.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE);
		dInfoCT.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORI);
		de.setInfo(dInfoCT);
		dati.addElement(de);
		
		// Pattern
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_PATTERN);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_PATTERN);
		de.setType(DataElementType.TEXT_AREA);
		de.setRows(CostantiControlStation.LABEL_PARAMETRO_TEXT_AREA_SIZE);
		de.setSize(this.getSize());
		de.setValue(pattern);
//		de.setRequired(true);
		DataElementInfo dInfoPattern = new DataElementInfo(CostantiControlStation.LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA+" - "+CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_PATTERN);
		if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(infoServiceBinding)) {
			dInfoPattern.setHeaderBody(CostantiControlStation.LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_PATTERN_REST_RISPOSTA);
			dInfoPattern.setListBody(CostantiControlStation.LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_PATTERN_VALORI_REST);
		}
		else {
			dInfoPattern.setBody(CostantiControlStation.LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_PATTERN_SOAP_RISPOSTA);
		}
		de.setInfo(dInfoPattern);
		dati.addElement(de);
		
		
		// in edit faccio vedere i link per configurare la richiesta e le risposte
				
		if(tipoOP.equals(TipoOperazione.CHANGE)) {
			
			String postbackElement = this.getPostBackElementName();
			
			boolean old_trasformazioneRispostaContenutoTemplate = false;
			boolean old_trasformazioneRispostaSoapEnvelopeTemplate = false;
			
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_CONFIGURAZIONE_TRASFORMAZIONI_REGOLE_TRASFORMAZIONE);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
			
			// sezione trasporto
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_TRASPORTO);
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
			
			// Return Code e Header Risposta
			
			de = this.getHttpReturnCodeDataElement(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_RETURN_CODE, 
					CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_RETURN_CODE, 
					trasformazioneContenutoRispostaReturnCode, false);
			dati.addElement(de);
			
			de = new DataElement();
			de.setType(DataElementType.LINK);
			boolean contaListeFromSession = ServletUtils.getContaListeFromSession(this.session) != null ? ServletUtils.getContaListeFromSession(this.session) : false;
			if (contaListeFromSession)
				de.setValue(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADERS+" (" + numeroTrasformazioniRispostaHeaders + ")");
			else
				de.setValue(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADERS);
			de.setUrl(servletTrasformazioniRispostaHeadersList, parametriInvocazioneServletTrasformazioniRispostaHeaders.toArray(new Parameter[parametriInvocazioneServletTrasformazioniRispostaHeaders.size()]));
			dati.addElement(de);
			
			// sezione contenuto
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONTENUTO);
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
			
			// abilitato
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_ENABLED);
			de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_ENABLED);
			//if(!trasformazioneContenutoRichiestaAbilitato) {
			de.setType(DataElementType.CHECKBOX);
			de.setSelected(trasformazioneContenutoRispostaAbilitato);
			de.setPostBack(true);
			//} else {
			//de.setType(DataElementType.HIDDEN);
			//}
			de.setValue(trasformazioneContenutoRispostaAbilitato+"");
			dati.addElement(de);
			
			if(trasformazioneContenutoRispostaAbilitato) {
				// tipo
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TIPO);
				de.setLabels(org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toLabelList(serviceBindingMessage, false));
				de.setValues(org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toStringList(serviceBindingMessage, false));
				de.setType(DataElementType.SELECT);
				de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TIPO);
				de.setPostBack(true);
				de.setSelected(trasformazioneContenutoRispostaTipo.getValue());
				setTemplateInfo(de, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TIPO, trasformazioneContenutoRispostaTipo, infoServiceBinding, true);
				dati.addElement(de);
				
				if(trasformazioneContenutoRispostaTipo.isTemplateRequired()) {	
					
					// richiesta null in add
					boolean templateRequired = true;
					
					if(risposta!=null){
						old_trasformazioneRispostaContenutoTemplate = risposta.getConversioneTemplate() != null && risposta.getConversioneTemplate().length > 0;
						TipoTrasformazione oldTrasformazioneRispostaContenutoTipo = StringUtils.isNotEmpty(risposta.getConversioneTipo()) 
								? org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(risposta.getConversioneTipo()) : org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
						if(trasformazioneContenutoRispostaTipo.equals(oldTrasformazioneRispostaContenutoTipo)) {
							templateRequired = false;
						} 
					}
								
					if(postbackElement != null) {
						if(postbackElement.equals(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TIPO)) {
							old_trasformazioneRispostaContenutoTemplate = false;
							templateRequired = true;
						} 
//						if(postbackElement.equals(trasformazioneContenutoTemplate.getName())) {
//							if(StringUtils.isEmpty(trasformazioneContenutoTipoCheck))
//								trasformazioneContenutoTipoCheck = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK_UPDATE_FILE;
//						}
					}
					
					de = new DataElement();
					de.setLabel("");
					de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TIPO_CHECK);
					de.setType(DataElementType.HIDDEN);
					de.setValue(trasformazioneContenutoRispostaTipoCheck);
					dati.addElement(de);
					
					if(StringUtils.isNotEmpty(trasformazioneContenutoRispostaTipoCheck) && trasformazioneContenutoRispostaTipoCheck.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK_UPDATE_TIPO))
						templateRequired = true;
					
					String trasformazioneRispostaContenutoLabel = CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TEMPLATE;
					if(old_trasformazioneRispostaContenutoTemplate && StringUtils.isEmpty(trasformazioneContenutoRispostaTipoCheck)) {
						trasformazioneRispostaContenutoLabel = "";
						DataElement saveAs = new DataElement();
						saveAs.setValue(CostantiControlStation.LABEL_DOWNLOAD_DOCUMENTO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TEMPLATE);
						saveAs.setType(DataElementType.LINK);
					
						Parameter pIdTrasformazioneRegola = new Parameter(CostantiControlStation.PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE, idTrasformazione);
						Parameter pIdTrasformazioneRegolaRisposta = new Parameter(CostantiControlStation.PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE_RISPOSTA, idTrasformazioneRisposta);
						Parameter pIdAccordo = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_ID_ACCORDO, idPorta+"");
						Parameter pTipoAllegato = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO, isPortaDelegata ? "pd" : "pa");
						Parameter pTipoDoc = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO, isPortaDelegata ? 
								ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TEMPLATE : 
									ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TEMPLATE);
						saveAs.setUrl(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT, pIdAccordo, pTipoAllegato, pTipoDoc, pIdTrasformazioneRegola,pIdTrasformazioneRegolaRisposta);
						saveAs.setDisabilitaAjaxStatus();
						dati.add(saveAs);
					}
					
					// template
					DataElement trasformazioneContenutoTemplateDataElement = trasformazioneContenutoRispostaTemplate.getFileDataElement(trasformazioneRispostaContenutoLabel, "", getSize());
					trasformazioneContenutoTemplateDataElement.setRequired(templateRequired);
					dati.add(trasformazioneContenutoTemplateDataElement);
					dati.addAll(trasformazioneContenutoRispostaTemplate.getFileNameDataElement());
					dati.add(trasformazioneContenutoRispostaTemplate.getFileIdDataElement());
				}
				
				boolean contentTypePerAttachmentSOAP = false;
				if(trasformazioneContenutoRispostaTipo.isTrasformazioneProtocolloEnabled() && trasformazioneRichiestaRestAbilitato &&
						!TipoTrasformazione.EMPTY.equals(trasformazioneContenutoRispostaTipo) &&
						CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_ATTACHMENT.equals(trasformazioneRispostaSoapEnvelope)) {
					contentTypePerAttachmentSOAP = true;
				}
				
				if(!contentTypePerAttachmentSOAP && trasformazioneContenutoRispostaTipo.isContentTypeEnabled()) {
					// Content-type
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONTENT_TYPE);
					de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONTENT_TYPE);
					de.setType(DataElementType.TEXT_EDIT);
					de.setValue(trasformazioneContenutoRispostaContentType);
					if(trasformazioneRichiestaRestAbilitato) { // devo restituire un soap e il ct e' deciso dall'engine 
						de.setType(DataElementType.HIDDEN);
						de.setValue("");
					}   
					dati.addElement(de);
				}
								
				if(trasformazioneContenutoRispostaTipo.isTrasformazioneProtocolloEnabled() && trasformazioneRichiestaRestAbilitato &&
						!TipoTrasformazione.EMPTY.equals(trasformazioneContenutoRispostaTipo)) {
					
					trasformazioneRispostaSoapAbilitato = true; // forzo
					
					// sezione trasformazione SOAP
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP);
					de.setType(DataElementType.SUBTITLE);
					dati.addElement(de);
					
					// abilitato
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_TRANSFORMATION);
					de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_TRANSFORMATION);
					de.setType(DataElementType.HIDDEN);
					de.setValue(trasformazioneRispostaSoapAbilitato+"");
					dati.addElement(de);
					
					//if(trasformazioneRispostaSoapAbilitato) {
					// Envelope
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE);
					de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE);
					de.setLabels(CostantiControlStation.SELECT_LABELS_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE);
					de.setValues(CostantiControlStation.SELECT_VALUES_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE);
					de.setType(DataElementType.SELECT);
					de.setSelected(trasformazioneRispostaSoapEnvelope);
					de.setPostBack(true);
					dati.addElement(de);
											
					if(trasformazioneRispostaSoapEnvelope!=null && CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_ATTACHMENT.equals(trasformazioneRispostaSoapEnvelope)) {
						
						// Content-type
						de = new DataElement();
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONTENT_TYPE_ATTACHMENT);
						de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONTENT_TYPE);
						de.setType(DataElementType.TEXT_EDIT);
						de.setValue(trasformazioneContenutoRispostaContentType);
						de.setRequired(true);
						dati.addElement(de);
						
						de = new DataElement();
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TITLE_BODY);
						de.setType(DataElementType.SUBTITLE);
						dati.addElement(de);	
						
						// tipo envelope attachement
						de = new DataElement();
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TIPO);
						de.setLabels(org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toLabelList(ServiceBinding.SOAP, true));
						de.setValues(org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toStringList(ServiceBinding.SOAP, true));
						de.setType(DataElementType.SELECT);
						de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TIPO);
						de.setSelected(trasformazioneRispostaSoapEnvelopeTipo.getValue());
						de.setPostBack(true);
						setTemplateInfo(de, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TIPO, trasformazioneRispostaSoapEnvelopeTipo, infoServiceBinding, true);
						dati.addElement(de);
						
						if(trasformazioneRispostaSoapEnvelopeTipo!=null && trasformazioneRispostaSoapEnvelopeTipo.isTemplateRequired()) {
							
							// richiesta null in add
							boolean templateRequired = true;
							if(risposta!=null){
								TrasformazioneSoapRisposta oldTrasformazioneSoapRisposta = risposta.getTrasformazioneSoap();
								old_trasformazioneRispostaSoapEnvelopeTemplate = oldTrasformazioneSoapRisposta != null && oldTrasformazioneSoapRisposta.getEnvelopeBodyConversioneTemplate() != null && oldTrasformazioneSoapRisposta.getEnvelopeBodyConversioneTemplate().length > 0;
								String oldTrasformazioneSoapEnvelopeTipoS = oldTrasformazioneSoapRisposta != null ? oldTrasformazioneSoapRisposta.getEnvelopeBodyConversioneTipo() : null;
								TipoTrasformazione oldTrasformazioneSoapEnvelopeTipo = StringUtils.isNotEmpty(oldTrasformazioneSoapEnvelopeTipoS) 
										? org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(oldTrasformazioneSoapEnvelopeTipoS) : org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
								if(trasformazioneRispostaSoapEnvelopeTipo.equals(oldTrasformazioneSoapEnvelopeTipo)) {
									templateRequired = false;
								}  
							}
							
							if(postbackElement != null) {
								if(postbackElement.equals(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TIPO)) {
									old_trasformazioneRispostaSoapEnvelopeTemplate = false;
									templateRequired = true;
								}
								
//								if(postbackElement.equals(trasformazioneSoapEnvelopeTemplate.getName())) {
//									trasformazioneSoapEnvelopeTipoCheck = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK_UPDATE_FILE;
//								}
							}
							
							de = new DataElement();
							de.setLabel("");
							de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TIPO_CHECK);
							de.setType(DataElementType.HIDDEN);
							de.setValue(trasformazioneRispostaSoapEnvelopeTipoCheck);
							dati.addElement(de);
							
							if(StringUtils.isNotEmpty(trasformazioneRispostaSoapEnvelopeTipoCheck) && trasformazioneRispostaSoapEnvelopeTipoCheck.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK_UPDATE_TIPO))
								templateRequired = true;
							
							String trasformazioneSoapEnvelopeTemplateLabel = CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TEMPLATE;
							if(old_trasformazioneRispostaSoapEnvelopeTemplate && StringUtils.isEmpty(trasformazioneRispostaSoapEnvelopeTipoCheck)) {
								trasformazioneSoapEnvelopeTemplateLabel = "";
								DataElement saveAs = new DataElement();
								saveAs.setValue(CostantiControlStation.LABEL_DOWNLOAD_DOCUMENTO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TEMPLATE);
								saveAs.setType(DataElementType.LINK);

								Parameter pIdTrasformazioneRegola = new Parameter(CostantiControlStation.PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE, idTrasformazione);
								Parameter pIdTrasformazioneRegolaRisposta = new Parameter(CostantiControlStation.PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE_RISPOSTA, idTrasformazioneRisposta);
								Parameter pIdAccordo = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_ID_ACCORDO, idPorta+"");
								Parameter pTipoAllegato = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO, isPortaDelegata ? "pd" : "pa");
								Parameter pTipoDoc = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO, isPortaDelegata 
										? ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TEMPLATE 
												: ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TEMPLATE);
								saveAs.setUrl(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT, pIdAccordo, pTipoAllegato, pTipoDoc,pIdTrasformazioneRegola,pIdTrasformazioneRegolaRisposta);
								dati.add(saveAs);
							}
							
							
							// 	template envelope attachement
							DataElement trasformazioneSoapEnvelopeTemplateDataElement = trasformazioneRispostaSoapEnvelopeTemplate.getFileDataElement(trasformazioneSoapEnvelopeTemplateLabel, "", getSize());
							trasformazioneSoapEnvelopeTemplateDataElement.setRequired(templateRequired);
							dati.add(trasformazioneSoapEnvelopeTemplateDataElement);
							dati.addAll(trasformazioneRispostaSoapEnvelopeTemplate.getFileNameDataElement());
							dati.add(trasformazioneRispostaSoapEnvelopeTemplate.getFileIdDataElement());
						}
					}
					
				}
			}
		}
		
		
		return dati;
	}
	
	public boolean trasformazioniCheckData(TipoOperazione tipoOp, long idPorta, String nome, TrasformazioneRegola regolaDBCheck_criteri, TrasformazioneRegola trasformazioneDBCheck_nome,  TrasformazioneRegola oldRegola) throws Exception {
		try{
//			String [] azioni = this.getParameterValues(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_AZIONI);
//			String pattern = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_PATTERN);
//			String contentType = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_CT);
			
			if(nome==null || "".equals(nome)) {
				this.pd.setMessage("Indicare un valore nel campo '"+CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_NOME+"'");
				return false;
			}
			if(!this.checkLength255(nome, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_NOME)) {
				return false;
			}
			
			// Se tipoOp = add, controllo che la trasformazione non sia gia' stato registrata
			if (tipoOp.equals(TipoOperazione.ADD)) {
				if (regolaDBCheck_criteri != null) {
					this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_REGOLA_TRASFORMAZIONE_APPLICABILITA_DUPLICATA);
					return false;
				}
				else if (trasformazioneDBCheck_nome != null) {
					this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_REGOLA_TRASFORMAZIONE_APPLICABILITA_NOME);
					return false;
				}
			} else {
				// controllo che le modifiche ai parametri non coincidano con altre regole gia' presenti
//				TrasformazioneRegola trasformazione = this.porteApplicativeCore.getTrasformazione(idPorta, azioniDBCheck, patternDBCheck, contentTypeDBCheck);
				if(regolaDBCheck_criteri != null && regolaDBCheck_criteri.getId().longValue() != oldRegola.getId().longValue()) {
					this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_REGOLA_TRASFORMAZIONE_APPLICABILITA_DUPLICATA);
					return false;
				}
				else if (trasformazioneDBCheck_nome != null && trasformazioneDBCheck_nome.getId().longValue() != oldRegola.getId().longValue()) {
					this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_REGOLA_TRASFORMAZIONE_APPLICABILITA_NOME);
					return false;
				}
			}
			
			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public boolean trasformazioniRichiestaCheckData(TipoOperazione tipoOp, TrasformazioneRegola oldRegola , ServiceBinding serviceBindingMessage) throws Exception {
		try{
		
			String trasformazioneContenutoAbilitatoS  = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_ENABLED);
			boolean trasformazioneContenutoAbilitato = trasformazioneContenutoAbilitatoS != null ? ServletUtils.isCheckBoxEnabled(trasformazioneContenutoAbilitatoS) : false;
			
			if(trasformazioneContenutoAbilitato) {
				String trasformazioneContenutoTipoS = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO);
				org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione trasformazioneContenutoTipo = 
						trasformazioneContenutoTipoS != null ? org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(trasformazioneContenutoTipoS) : 
							org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
				
				if(trasformazioneContenutoTipo.isTemplateRequired()) {
					BinaryParameter trasformazioneContenutoTemplate = this.getBinaryParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TEMPLATE);
					
					String trasformazioneContenutoTipoCheck = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK);
					
					if(StringUtils.isNotEmpty(trasformazioneContenutoTipoCheck)) { // ho cambiato il tipo conversione
						if((trasformazioneContenutoTemplate.getValue() == null || trasformazioneContenutoTemplate.getValue().length == 0)) {
							this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, 
									CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TEMPLATE));
							return false;
						}
					} else { // non ho cambiato il template
						if((oldRegola.getRichiesta() == null || oldRegola.getRichiesta().getConversioneTemplate() == null)) {
							this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, 
									CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TEMPLATE));
							return false;
						}
					}
				}
				
				String trasformazioneRichiestaContentType = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONTENT_TYPE);
				
				switch (serviceBindingMessage) { 
				case REST:
					String trasformazioneSoapAbilitatoS = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_TRANSFORMATION);
					boolean trasformazioneSoapAbilitato =  trasformazioneSoapAbilitatoS != null ? ServletUtils.isCheckBoxEnabled(trasformazioneSoapAbilitatoS) : false;
					
					if(trasformazioneSoapAbilitato) {
						String trasformazioneSoapAction = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ACTION);
						
						if(!this.checkLength255(trasformazioneSoapAction, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ACTION)) {
							return false;
						}
						
						if(trasformazioneSoapAction!=null && !"".equals(trasformazioneSoapAction)) {
							try{
								DynamicUtils.validate(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ACTION, trasformazioneSoapAction, true, false);
							}catch(Exception e){
								this.pd.setMessage("Il valore indicato nel parametro '"+CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ACTION+"' non risulta corretto: "+e.getMessage());
								return false;
							}
						}
						
						String trasformazioneSoapEnvelope = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE);
						
						if(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_ATTACHMENT.equals(trasformazioneSoapEnvelope)) {
							
							// content-type obbligatorio
							if (StringUtils.isEmpty(trasformazioneRichiestaContentType)) {
								this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, 
										CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONTENT_TYPE_ATTACHMENT));
								return false;
							}
							if(!this.checkLength255(trasformazioneRichiestaContentType, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONTENT_TYPE_ATTACHMENT)) {
								return false;
							}
							
							String trasformazioneSoapEnvelopeTipoS = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TIPO);
							org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione trasformazioneSoapEnvelopeTipo =
									trasformazioneSoapEnvelopeTipoS != null ? org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(trasformazioneSoapEnvelopeTipoS) : 
									org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
									
							if(trasformazioneSoapEnvelopeTipo.isTemplateRequired()) {
								BinaryParameter trasformazioneSoapEnvelopeTemplate = this.getBinaryParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TEMPLATE);
								
								String trasformazioneSoapEnvelopeTipoCheck = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TIPO_CHECK);
								
								if(StringUtils.isNotEmpty(trasformazioneSoapEnvelopeTipoCheck)) { // ho cambiato il tipo conversione
									if((trasformazioneSoapEnvelopeTemplate.getValue() == null || trasformazioneSoapEnvelopeTemplate.getValue().length == 0)) {
										this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, 
												CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TEMPLATE));
										return false;
									}
								} else { // non ho cambiato il template
									if((oldRegola.getRichiesta() == null || oldRegola.getRichiesta().getTrasformazioneSoap() == null || oldRegola.getRichiesta().getTrasformazioneSoap().getEnvelopeBodyConversioneTemplate() == null)) {
										this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, 
												CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TEMPLATE));
										return false;
									}
								}
							}
							
						}
					} else {
						// dimensione content-type
						if(!this.checkLength255(trasformazioneRichiestaContentType, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONTENT_TYPE)) {
							return false;
						}
					}
					break;
				case SOAP:
					String trasformazioneRestAbilitatoS = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REST_TRANSFORMATION);
					boolean trasformazioneRestAbilitato =  trasformazioneRestAbilitatoS != null ? ServletUtils.isCheckBoxEnabled(trasformazioneRestAbilitatoS) : false;
					if(trasformazioneRestAbilitato) {
						// content-type obbligatorio
						if(trasformazioneContenutoTipo.isContentTypeEnabled()) {
							if (StringUtils.isEmpty(trasformazioneRichiestaContentType)) {
								this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, 
										CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONTENT_TYPE));
								return false;
							}
						}
						// dimensione content-type
						if(!this.checkLength255(trasformazioneRichiestaContentType, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONTENT_TYPE)) {
							return false;
						}
						
						
						String trasformazioneRestMethod = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REST_METHOD);
						
						if (StringUtils.isEmpty(trasformazioneRestMethod)) {
							this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, 
									CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REST_METHOD));
							return false;
						}
						
						String trasformazioneRestPath = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REST_PATH);
						
						if (StringUtils.isEmpty(trasformazioneRestPath)) {
							this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, 
									CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REST_PATH));
							return false;
						}
						if (!this.checkLength4000(trasformazioneRestPath, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REST_PATH)) {
							return false;
						}
					}
					break;
				}
			}
			
			
			if(ServiceBinding.REST.equals(serviceBindingMessage)) {
			
				String trasformazioneRestMethod = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REST_METHOD);
				if (!StringUtils.isEmpty(trasformazioneRestMethod)) {
					if (!this.checkLength255(trasformazioneRestMethod, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REST_METHOD)) {
						return false;
					}
				}
				
				String trasformazioneRestPath = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REST_PATH);
				if (!StringUtils.isEmpty(trasformazioneRestPath)) {
					if (!this.checkLength4000(trasformazioneRestPath, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REST_PATH)) {
						return false;
					}
				}
				
			}
			
			
			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public boolean trasformazioniRispostaCheckData(TipoOperazione tipoOp, TrasformazioneRegola regolaRichiesta, TrasformazioneRegolaRisposta oldRegolaRisposta) throws Exception {
		try{

			String nome = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_NOME);
			if(nome==null || "".equals(nome)) {
				this.pd.setMessage("Indicare un valore nel campo '"+CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_NOME+"'");
				return false;
			}
			if(!this.checkLength255(nome, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_NOME)) {
				return false;
			}
			
			String returnCode = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS);
			String statusMinS = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS_MIN);
			String statusMaxS = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS_MAX);
			
			if(_checkReturnCode(returnCode, statusMinS, statusMaxS, 
					CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS, 
					CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS_MIN, 
					CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS_MAX)==false) {
				return false;
			}
			
			// Se tipoOp = add, controllo che la trasformazione risposta non sia gia' stato registrata
			if (tipoOp.equals(TipoOperazione.CHANGE)) {
				
				boolean trasformazioneRichiestaRestAbilitato = false;
				if(regolaRichiesta.getRichiesta() != null) {
					trasformazioneRichiestaRestAbilitato = regolaRichiesta.getRichiesta().getTrasformazioneRest() != null;
				}
				
				String trasformazioneContenutoRispostaAbilitatoS  = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_ENABLED);
				boolean trasformazioneContenutoRispostaAbilitato = trasformazioneContenutoRispostaAbilitatoS != null ? ServletUtils.isCheckBoxEnabled(trasformazioneContenutoRispostaAbilitatoS) : false;
				
				if(trasformazioneContenutoRispostaAbilitato) {
					String trasformazioneContenutoRispostaTipoS = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TIPO);
					org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione trasformazioneContenutoRispostaTipo = 
							trasformazioneContenutoRispostaTipoS != null ? org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(trasformazioneContenutoRispostaTipoS) : 
								org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
					
					if(trasformazioneContenutoRispostaTipo.isTemplateRequired()) { 
						BinaryParameter trasformazioneContenutoTemplate = this.getBinaryParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TEMPLATE);
						
						String trasformazioneContenutoRispostaTipoCheck = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TIPO_CHECK);
						
						if(StringUtils.isNotEmpty(trasformazioneContenutoRispostaTipoCheck)) { // ho cambiato il tipo conversione
							if((trasformazioneContenutoTemplate.getValue() == null || trasformazioneContenutoTemplate.getValue().length == 0)) {
								this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, 
										CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TEMPLATE));
								return false;
							}
						} else { // non ho cambiato il template
							if(oldRegolaRisposta.getConversioneTemplate() == null) {
								this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, 
										CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TEMPLATE));
								return false;
							}
						}
					}
					
					String trasformazioneRispostaContentType = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONTENT_TYPE);
					
					String trasformazioneRispostaReturnCode = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_RETURN_CODE);
					
					if(StringUtils.isNotEmpty(trasformazioneRispostaReturnCode)) {
						try {
							int returnCodeEsatto = Integer.parseInt(trasformazioneRispostaReturnCode);
							
							if(returnCodeEsatto < 1) {
								this.pd.setMessage("Il valore inserito nel campo "+ CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_RETURN_CODE + " non &egrave; valido, sono ammessi valori compresi tra 1 e 999.");
								return false;
							}
							if(returnCodeEsatto > 999) {
								this.pd.setMessage("Il valore inserito nel campo "+ CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_RETURN_CODE + " non &egrave; valido, sono ammessi valori compresi tra 1 e 999.");
								return false;
							}
						}catch(Exception e) {
							this.pd.setMessage("Il formato del campo "+ CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_RETURN_CODE + " non &egrave; valido.");
							return false;
						}
					}
					
					if(trasformazioneContenutoRispostaTipo.isTrasformazioneProtocolloEnabled() && trasformazioneRichiestaRestAbilitato) {
					
						String trasformazioneSoapAbilitatoS = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_TRANSFORMATION);
						boolean trasformazioneSoapAbilitato =  trasformazioneSoapAbilitatoS != null ? ServletUtils.isCheckBoxEnabled(trasformazioneSoapAbilitatoS) : false;
						
						if(trasformazioneSoapAbilitato) {
							String trasformazioneSoapEnvelope = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE);
							
							if(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_ATTACHMENT.equals(trasformazioneSoapEnvelope)) {
								
								// content-type obbligatorio
								if (StringUtils.isEmpty(trasformazioneRispostaContentType)) {
									this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, 
											CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONTENT_TYPE_ATTACHMENT));
									return false;
								}
								if(!this.checkLength255(trasformazioneRispostaContentType, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONTENT_TYPE_ATTACHMENT)) {
									return false;
								}
								
								String trasformazioneSoapEnvelopeTipoS = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TIPO);
								org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione trasformazioneSoapEnvelopeTipo =
										trasformazioneSoapEnvelopeTipoS != null ? org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(trasformazioneSoapEnvelopeTipoS) : 
										org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
										
								if(trasformazioneSoapEnvelopeTipo.isTemplateRequired()) {
									BinaryParameter trasformazioneSoapEnvelopeTemplate = this.getBinaryParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TEMPLATE);
									
									String trasformazioneSoapEnvelopeTipoCheck = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TIPO_CHECK);
									
									if(StringUtils.isNotEmpty(trasformazioneSoapEnvelopeTipoCheck)) { // ho cambiato il tipo conversione
										if((trasformazioneSoapEnvelopeTemplate.getValue() == null || trasformazioneSoapEnvelopeTemplate.getValue().length == 0)) {
											this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, 
													CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TEMPLATE));
											return false;
										}
									} else { // non ho cambiato il template
										if(oldRegolaRisposta.getTrasformazioneSoap().getEnvelopeBodyConversioneTemplate() == null) {
											this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, 
													CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TEMPLATE));
											return false;
										}
									}
								}
							}
						} else {
							// dimensione content-type
							if(!this.checkLength255(trasformazioneRispostaContentType, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_CONTENT_TYPE)) {
								return false;
							}
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
	
	public Vector<DataElement> addTrasformazioneToDatiOpAdd(Vector<DataElement> dati, Object oggetto, String nome, 
			String stato, boolean azioniAll, String[] azioniDisponibiliList, String[] azioniDisponibiliLabelList, String[] azioni, String pattern, String contentType,
			org.openspcoop2.core.registry.constants.ServiceBinding serviceBinding, boolean isPortaDelegata) throws Exception {
		return addTrasformazioneToDati(TipoOperazione.ADD, dati, oggetto, null, nome, 
				stato, azioniAll, azioniDisponibiliList, azioniDisponibiliLabelList, azioni, pattern, contentType, 
				serviceBinding,
				null, null, null, null, 0, isPortaDelegata, null,null,0,null,null,0);
	}
	
	public Vector<DataElement> addTrasformazioneToDati(TipoOperazione tipoOP, Vector<DataElement> dati, Object oggetto, String idTrasformazione, String nome, 
			String stato, boolean azioniAll, String[] azioniDisponibiliList, String[] azioniDisponibiliLabelList, String[] azioni, String pattern, String contentType,
			org.openspcoop2.core.registry.constants.ServiceBinding serviceBinding,
			String servletTrasformazioniRichiesta, List<Parameter> parametriInvocazioneServletTrasformazioniRichiesta, String servletTrasformazioniRispostaList, List<Parameter> parametriInvocazioneServletTrasformazioniRisposta,
			int numeroTrasformazioniRisposte, boolean isPortaDelegata, String servletTrasformazioniAutorizzazioneAutenticati,  List<Parameter> parametriInvocazioneServletTrasformazioniAutorizzazioneAutenticati , int numAutenticati,
			String servletTrasformazioniApplicativiAutenticati,  List<Parameter> parametriInvocazioneServletTrasformazioniApplicativiAutenticati , int numApplicativiAutenticati) throws Exception {
		
		// Id hidden
		DataElement de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE);
		de.setName(CostantiControlStation.PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE);
		de.setType(DataElementType.HIDDEN);
		de.setValue(idTrasformazione);
		dati.addElement(de);
		
		// First
		de = new DataElement();
		de.setValue("first");
		de.setType(DataElementType.HIDDEN);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_FIRST);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_CONFIGURAZIONE_TRASFORMAZIONI_TRASFORMAZIONE);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		// Nome
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_NOME);
		de.setValue(nome);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_NOME);
		de.setRequired(true); 
		dati.addElement(de);
		
		// Stato
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_STATO);
		if(stato==null || "".equals(stato)) {
			stato = StatoFunzionalita.ABILITATO.getValue();
		}
		de.setSelected(stato);
		de.setType(DataElementType.SELECT);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_STATO);
		de.setValues(ConfigurazioneCostanti.STATI);
		de.setLabels(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATI);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA);
		de.setType(DataElementType.SUBTITLE);
		dati.addElement(de);
				
		// Azione
		
		de = new DataElement();
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_AZIONI_ALL);
		de.setPostBack(true);
		de.setValues(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_AZIONI_ALL_VALUES);
		if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(serviceBinding)) {
			de.setLabels(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_RISORSE_ALL_VALUES);
		}
		else {
			de.setLabels(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_AZIONI_ALL_VALUES);
		}
		if(azioniAll) {
			de.setSelected(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_AZIONI_ALL_VALUE_TRUE);
		}
		else {
			de.setSelected(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_AZIONI_ALL_VALUE_FALSE);
		}
		if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(serviceBinding)) {
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_RISORSE);
		}
		else {
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_AZIONI);
		}
		de.setType(DataElementType.SELECT);
		dati.addElement(de);
		
		if(!azioniAll) {
			de = new DataElement();
			de.setLabel("");
			de.setValues(azioniDisponibiliList);
			de.setLabels(azioniDisponibiliLabelList);
			de.setSelezionati(azioni);
			de.setType(DataElementType.MULTI_SELECT);
			de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_AZIONI);
			de.setRows(15);
	//		de.setRequired(true); 
			dati.addElement(de);
		}
		
		// Content-type
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_CT);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_CT);
		de.setType(DataElementType.TEXT_EDIT);
		de.setValue(contentType);
		de.enableTags();
//		de.setRequired(true);
		DataElementInfo dInfoCT = new DataElementInfo(CostantiControlStation.LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA+" - "+CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_CT);
		dInfoCT.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE);
		dInfoCT.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORI);
		de.setInfo(dInfoCT);
		dati.addElement(de);
		
		// Pattern
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_PATTERN);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_PATTERN);
		de.setType(DataElementType.TEXT_AREA);
		de.setRows(CostantiControlStation.LABEL_PARAMETRO_TEXT_AREA_SIZE);
		de.setSize(this.getSize());
		de.setValue(pattern);
//		de.setRequired(true);
		DataElementInfo dInfoPattern = new DataElementInfo(CostantiControlStation.LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA+" - "+CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_PATTERN);
		if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(serviceBinding)) {
			dInfoPattern.setHeaderBody(CostantiControlStation.LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_PATTERN_REST_RICHIESTA);
			dInfoPattern.setListBody(CostantiControlStation.LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_PATTERN_VALORI_REST);
		}
		else {
			dInfoPattern.setBody(CostantiControlStation.LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_PATTERN_SOAP_RICHIESTA);
		}
		de.setInfo(dInfoPattern);
		dati.addElement(de);
		
						
		// in edit faccio vedere i link per configurare la richiesta e le risposte
		if(tipoOP.equals(TipoOperazione.CHANGE)) {
			
			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
			boolean autenticazione = false;
			String protocollo = null;
			boolean isSupportatoAutenticazione;
			
			if(isPortaDelegata){
				PortaDelegata pd = (PortaDelegata) oggetto;
				autenticazione = !TipoAutenticazione.DISABILITATO.equals(pd.getAutenticazione());
				isSupportatoAutenticazione = true;
				if(pd!=null && pd.getServizio()!=null && pd.getServizio().getTipo()!=null) {
					protocollo = this.apsCore.getProtocolloAssociatoTipoServizio(pd.getServizio().getTipo());
				}
			}
			else {
				PortaApplicativa pa = (PortaApplicativa) oggetto;
				autenticazione = !TipoAutenticazione.DISABILITATO.equals(pa.getAutenticazione());
				if(pa!=null && pa.getServizio()!=null && pa.getServizio().getTipo()!=null) {
					protocollo = this.apsCore.getProtocolloAssociatoTipoServizio(pa.getServizio().getTipo());
				}
				isSupportatoAutenticazione = this.soggettiCore.isSupportatoAutenticazioneApplicativiErogazione(protocollo);
			}
			
			
			// soggetti
			if(servletTrasformazioniAutorizzazioneAutenticati !=null && (autenticazione) ){
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(servletTrasformazioniAutorizzazioneAutenticati, parametriInvocazioneServletTrasformazioniAutorizzazioneAutenticati.toArray(new Parameter[parametriInvocazioneServletTrasformazioniAutorizzazioneAutenticati.size()]));
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
			
			// servizi applicativi
			
			if(!isPortaDelegata && isSupportatoAutenticazione // il link degli applicativi sulla pa deve essere visualizzato SOLO se è abilitata l'autenticazione
					){
				if(servletTrasformazioniApplicativiAutenticati!=null && (autenticazione) ){
					de = new DataElement();
					de.setType(DataElementType.LINK);
					de.setUrl(servletTrasformazioniApplicativiAutenticati, parametriInvocazioneServletTrasformazioniApplicativiAutenticati.toArray(new Parameter[parametriInvocazioneServletTrasformazioniApplicativiAutenticati.size()]));
					String labelApplicativi = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SERVIZI_APPLICATIVI; // uso cmq label PD
					if(!this.isModalitaCompleta()) {
						labelApplicativi = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_APPLICATIVI;// uso cmq label PD
					}
					if (contaListe) {
						ServletUtils.setDataElementCustomLabel(de,labelApplicativi,Long.valueOf(numApplicativiAutenticati));
					} else {
						ServletUtils.setDataElementCustomLabel(de,labelApplicativi);
					}
					dati.addElement(de);
				}
			}
		
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_CONFIGURAZIONE_TRASFORMAZIONI_REGOLE_TRASFORMAZIONE);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
			
			// Richiesta
			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setValue(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA);
			de.setUrl(servletTrasformazioniRichiesta, parametriInvocazioneServletTrasformazioniRichiesta.toArray(new Parameter[parametriInvocazioneServletTrasformazioniRichiesta.size()]));
			dati.addElement(de);
			
			// Risposta
			de = new DataElement();
			de.setType(DataElementType.LINK);
			boolean contaListeFromSession = ServletUtils.getContaListeFromSession(this.session) != null ? ServletUtils.getContaListeFromSession(this.session) : false;
			if (contaListeFromSession)
				de.setValue(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTE+" (" + numeroTrasformazioniRisposte + ")");
			else
				de.setValue(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTE);
			de.setUrl(servletTrasformazioniRispostaList, parametriInvocazioneServletTrasformazioniRisposta.toArray(new Parameter[parametriInvocazioneServletTrasformazioniRisposta.size()]));
			dati.addElement(de);
		}
		
		return dati;
	}
	
	public Vector<DataElement> addTrasformazioneRichiestaHeaderToDati(TipoOperazione tipoOP, Vector<DataElement> dati, 
			String idTrasformazione, String idTrasformazioneRichiestaHeader, String nome, String tipo, String valore, String identificazione,
			org.openspcoop2.core.registry.constants.ServiceBinding serviceBinding) {
		
		DataElement de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		// Id trasformazione hidden
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE);
		de.setName(CostantiControlStation.PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE);
		de.setType(DataElementType.HIDDEN);
		de.setValue(idTrasformazione);
		dati.addElement(de);
		
		// id trasformazione richiesta header
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE);
		de.setName(CostantiControlStation.PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE_RICHIESTA_HEADER);
		de.setType(DataElementType.HIDDEN);
		de.setValue(idTrasformazioneRichiestaHeader);
		dati.addElement(de);
		
		// Tipo
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_TIPO);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_TIPO);
		//if(tipoOP.equals(TipoOperazione.ADD)) {
		de.setLabels(CostantiControlStation.SELECT_VALUES_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO);
		de.setValues(CostantiControlStation.SELECT_VALUES_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO);
		de.setType(DataElementType.SELECT);
		de.setSelected(tipo);
		de.setRequired(true); 
		de.setPostBack(true);
		//} else {
		//	de.setType(DataElementType.TEXT);
		//	de.setValue(tipo);
		//}
		dati.addElement(de);
		
		// Nome
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_NOME);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_NOME);
		//if(tipoOP.equals(TipoOperazione.ADD)) {
		de.setType(DataElementType.TEXT_EDIT);
		de.setRequired(true);
		//} else {
		//	de.setType(DataElementType.TEXT);
		//}
		de.setValue(nome);
		dati.addElement(de);
		
		// Valore
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_VALORE);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_VALORE);
		if(!CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_DELETE.equals(tipo)) {
			de.setType(DataElementType.TEXT_AREA);
			de.setRows(CostantiControlStation.LABEL_PARAMETRO_TEXT_AREA_SIZE);
			de.setSize(this.getSize());
			de.setRequired(true);
			
			DataElementInfo dInfoPattern = new DataElementInfo(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_VALORE);
			dInfoPattern.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASPORTO);
			if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(serviceBinding)) {
				dInfoPattern.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI);
			}
			else {
				dInfoPattern.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI);
			}
			de.setInfo(dInfoPattern);
		}
		else {
			de.setType(DataElementType.HIDDEN);
		}
		de.setValue(valore);
		dati.addElement(de);
		
		// Identificazione Risorsa Fallita
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_IDENTIFICAZIONE);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_IDENTIFICAZIONE);
		if(!CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_DELETE.equals(tipo)) {
			de.setLabels(CostantiControlStation.SELECT_LABELS_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_IDENTIFICAZIONE_FALLITA_HEADER);
			de.setValues(CostantiControlStation.SELECT_VALUES_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_IDENTIFICAZIONE_FALLITA);
			de.setType(DataElementType.SELECT);
			de.setSelected(identificazione);
			
			DataElementInfo dInfoPattern = new DataElementInfo(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_IDENTIFICAZIONE);
			dInfoPattern.setHeaderBody(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_NOTE);
			dInfoPattern.setListBody(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_NOTE_VALORI_HEADER);
			de.setInfo(dInfoPattern);
		} else {
			de.setType(DataElementType.HIDDEN);
		}
		de.setValue(identificazione);
		dati.addElement(de);
		
		return dati;
	}
	
	public Vector<DataElement> addTrasformazioneRichiestaUrlParameterToDati(TipoOperazione tipoOP, Vector<DataElement> dati, 
			String idTrasformazione, String idTrasformazioneRichiestaUrlParameter, String nome, String tipo, String valore, String identificazione,
			org.openspcoop2.core.registry.constants.ServiceBinding serviceBinding) {
		
		DataElement de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRO);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		// Id trasformazione hidden
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE);
		de.setName(CostantiControlStation.PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE);
		de.setType(DataElementType.HIDDEN);
		de.setValue(idTrasformazione);
		dati.addElement(de);
		
		// id trasformazione richiesta header
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE);
		de.setName(CostantiControlStation.PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE_RICHIESTA_PARAMETRO);
		de.setType(DataElementType.HIDDEN);
		de.setValue(idTrasformazioneRichiestaUrlParameter);
		dati.addElement(de);
		
		// Tipo
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_TIPO);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_TIPO);
		//if(tipoOP.equals(TipoOperazione.ADD)) {
		de.setLabels(CostantiControlStation.SELECT_VALUES_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO);
		de.setValues(CostantiControlStation.SELECT_VALUES_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO);
		de.setType(DataElementType.SELECT);
		de.setSelected(tipo);
		de.setRequired(true);
		de.setPostBack(true);
		//} else {
		//de.setType(DataElementType.TEXT);
		//de.setValue(tipo);
		//}
		dati.addElement(de);
		
		// Nome
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_NOME);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_NOME);
		//if(tipoOP.equals(TipoOperazione.ADD)) {
		de.setType(DataElementType.TEXT_EDIT);
		de.setRequired(true);
		//} else {
		//	de.setType(DataElementType.TEXT);
		//}
		de.setValue(nome);
		dati.addElement(de);
		
		// Valore
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_VALORE);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_VALORE);
		if(!CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_DELETE.equals(tipo)) {
			de.setType(DataElementType.TEXT_AREA);
			de.setRows(CostantiControlStation.LABEL_PARAMETRO_TEXT_AREA_SIZE);
			de.setSize(this.getSize());
			de.setRequired(true);
			
			DataElementInfo dInfoPattern = new DataElementInfo(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_VALORE);
			dInfoPattern.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASPORTO);
			if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(serviceBinding)) {
				dInfoPattern.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI);
			}
			else {
				dInfoPattern.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI);
			}
			de.setInfo(dInfoPattern);
		}
		else {
			de.setType(DataElementType.HIDDEN);
		}
		de.setValue(valore);
		dati.addElement(de);
		
		// Identificazione Risorsa Fallita
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_IDENTIFICAZIONE);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_IDENTIFICAZIONE);
		if(!CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_DELETE.equals(tipo)) {
			de.setLabels(CostantiControlStation.SELECT_LABELS_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_IDENTIFICAZIONE_FALLITA_PARAMETRO);
			de.setValues(CostantiControlStation.SELECT_VALUES_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_IDENTIFICAZIONE_FALLITA);
			de.setType(DataElementType.SELECT);
			de.setSelected(identificazione);
			
			DataElementInfo dInfoPattern = new DataElementInfo(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_IDENTIFICAZIONE);
			dInfoPattern.setHeaderBody(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_NOTE);
			dInfoPattern.setListBody(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_NOTE_VALORI_PARAMETRO);
			de.setInfo(dInfoPattern);
		} else {
			de.setType(DataElementType.HIDDEN);
		}
		de.setValue(identificazione);
		dati.addElement(de);
		
		return dati;
	}
	
	public Vector<DataElement> addTrasformazioneRispostaHeaderToDati(TipoOperazione tipoOP, Vector<DataElement> dati, 
			String idTrasformazione, String idTrasformazioneRisposta, String idTrasformazioneRispostaHeader, String nome, String tipo, String valore, String identificazione,
			org.openspcoop2.core.registry.constants.ServiceBinding serviceBinding) {
		
		DataElement de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		// Id trasformazione hidden
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE);
		de.setName(CostantiControlStation.PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE);
		de.setType(DataElementType.HIDDEN);
		de.setValue(idTrasformazione);
		dati.addElement(de);
		
		// id trasformazione risposta
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE);
		de.setName(CostantiControlStation.PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE_RISPOSTA);
		de.setType(DataElementType.HIDDEN);
		de.setValue(idTrasformazioneRisposta);
		dati.addElement(de);
		
		// id trasformazione risposta header
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE);
		de.setName(CostantiControlStation.PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE_RISPOSTA_HEADER);
		de.setType(DataElementType.HIDDEN);
		de.setValue(idTrasformazioneRispostaHeader);
		dati.addElement(de);
		
		// Tipo
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER_TIPO);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER_TIPO);
		//if(tipoOP.equals(TipoOperazione.ADD)) {
		de.setLabels(CostantiControlStation.SELECT_VALUES_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO);
		de.setValues(CostantiControlStation.SELECT_VALUES_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO);
		de.setType(DataElementType.SELECT);
		de.setSelected(tipo);
		de.setPostBack(true);
		//} else {
		//	de.setType(DataElementType.TEXT);
		//	de.setValue(tipo);
		//}
		dati.addElement(de);
		
		// Nome
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER_NOME);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER_NOME);
		//if(tipoOP.equals(TipoOperazione.ADD)) {
		de.setType(DataElementType.TEXT_EDIT);
		de.setRequired(true);
		//} else {
		//	de.setType(DataElementType.TEXT);
		//}
		
		de.setValue(nome);
		dati.addElement(de);
		
		// Valore
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER_VALORE);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER_VALORE);
		if(!CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_DELETE.equals(tipo)) {
			de.setType(DataElementType.TEXT_AREA);
			de.setRows(CostantiControlStation.LABEL_PARAMETRO_TEXT_AREA_SIZE);
			de.setRequired(true);
			
			DataElementInfo dInfoPattern = new DataElementInfo(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER_VALORE);
			dInfoPattern.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASPORTO);
			if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(serviceBinding)) {
				dInfoPattern.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI_CON_RISPOSTE);
			}
			else {
				dInfoPattern.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE);
			}
			de.setInfo(dInfoPattern);
		}
		else {
			de.setType(DataElementType.HIDDEN);
		}
		de.setValue(valore);
		dati.addElement(de);
		
		// Identificazione Risorsa Fallita
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER_IDENTIFICAZIONE);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER_IDENTIFICAZIONE);
		if(!CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_DELETE.equals(tipo)) {
			de.setLabels(CostantiControlStation.SELECT_LABELS_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_IDENTIFICAZIONE_FALLITA_HEADER);
			de.setValues(CostantiControlStation.SELECT_VALUES_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_IDENTIFICAZIONE_FALLITA);
			de.setType(DataElementType.SELECT);
			de.setSelected(identificazione);
			
			DataElementInfo dInfoPattern = new DataElementInfo(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER_IDENTIFICAZIONE);
			dInfoPattern.setHeaderBody(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_NOTE);
			dInfoPattern.setListBody(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_NOTE_VALORI_HEADER);
			de.setInfo(dInfoPattern);
		} else {
			de.setType(DataElementType.HIDDEN);
		}
		de.setValue(identificazione);
		dati.addElement(de);
		
		return dati;
	}
	
	public DataElement getHttpMethodDataElementTrasformazione(TipoOperazione tipoOperazione, String httpMethod) {
		return this.getHttpMethodDataElement(tipoOperazione, httpMethod, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REST_METHOD, 
				CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REST_METHOD,
				false, null, null);
	}
	
	public Vector<DataElement> addTrasformazioneRichiestaToDati(TipoOperazione tipoOP, Vector<DataElement> dati, long idPorta, TrasformazioneRegolaRichiesta richiesta, boolean isPortaDelegata, String idTrasformazione,
			boolean trasformazioneContenutoAbilitato, org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione trasformazioneContenutoTipo, BinaryParameter trasformazioneContenutoTemplate, String trasformazioneContenutoTipoCheck,
			String trasformazioneRichiestaContentType, 
			ServiceBinding serviceBindingMessage, boolean trasformazioneRestAbilitato, String trasformazioneRestMethod, String trasformazioneRestPath,
			boolean trasformazioneSoapAbilitato, String trasformazioneSoapAction, String trasformazioneSoapVersion, String trasformazioneSoapEnvelope,  
			org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione trasformazioneSoapEnvelopeTipo, BinaryParameter trasformazioneSoapEnvelopeTemplate, String trasformazioneSoapEnvelopeTipoCheck,
			String servletTrasformazioniRichiestaHeadersList, List<Parameter> parametriInvocazioneServletTrasformazioniRichiestaHeaders, int numeroTrasformazioniRichiestaHeaders,
			String servletTrasformazioniRichiestaParametriList, List<Parameter> parametriInvocazioneServletTrasformazioniRichiestaParametri, int numeroTrasformazioniRichiestaParametri,
			org.openspcoop2.core.registry.constants.ServiceBinding serviceBinding) throws Exception {
		
		DataElementInfo dInfoPatternTrasporto = new DataElementInfo(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER_VALORE);
		dInfoPatternTrasporto.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASPORTO);
		if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(serviceBinding)) {
			dInfoPatternTrasporto.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI);
		}
		else {
			dInfoPatternTrasporto.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI);
		}
		
		// Id trasformazione hidden
		DataElement  de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE);
		de.setName(CostantiControlStation.PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE);
		de.setType(DataElementType.HIDDEN);
		de.setValue(idTrasformazione);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_CONFIGURAZIONE_TRASFORMAZIONI_TRASFORMAZIONE);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		// sezione trasporto
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_TRASPORTO);
		de.setType(DataElementType.SUBTITLE);
		dati.addElement(de);
		
		if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(serviceBinding) && !trasformazioneSoapAbilitato) {
			
			// method
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REST_METHOD);
			de.setValue(trasformazioneRestMethod);
			de.setType(DataElementType.TEXT_EDIT);
			de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REST_METHOD);
			de.setSize(this.getSize());
			de.setInfo(dInfoPatternTrasporto);
			dati.addElement(de);
			
			//  path
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REST_PATH);
			de.setValue(trasformazioneRestPath);
			de.setType(DataElementType.TEXT_AREA);
			de.setRows(1);
			de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REST_PATH);
			de.setSize(this.getSize());
			de.setInfo(dInfoPatternTrasporto);
			de.setNote(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REST_PATH_NOTE);
			dati.addElement(de);
						
		}
		
		
		// Header
		de = new DataElement();
		de.setType(DataElementType.LINK);
		boolean contaListeFromSession = ServletUtils.getContaListeFromSession(this.session) != null ? ServletUtils.getContaListeFromSession(this.session) : false;
		if (contaListeFromSession)
			de.setValue(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADERS+" (" + numeroTrasformazioniRichiestaHeaders + ")");
		else
			de.setValue(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADERS);
		de.setUrl(servletTrasformazioniRichiestaHeadersList, parametriInvocazioneServletTrasformazioniRichiestaHeaders.toArray(new Parameter[parametriInvocazioneServletTrasformazioniRichiestaHeaders.size()]));
		dati.addElement(de);
		
		// url parameters
		de = new DataElement();
		de.setType(DataElementType.LINK);
		if (contaListeFromSession)
			de.setValue(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRI+" (" + numeroTrasformazioniRichiestaParametri + ")");
		else
			de.setValue(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRI);
		de.setUrl(servletTrasformazioniRichiestaParametriList, parametriInvocazioneServletTrasformazioniRichiestaParametri.toArray(new Parameter[parametriInvocazioneServletTrasformazioniRichiestaParametri.size()]));
		dati.addElement(de);
		
		
		
		
		// sezione contenuto
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_CONTENUTO);
		de.setType(DataElementType.SUBTITLE);
		dati.addElement(de);
		
		// abilitato
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_ENABLED);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_ENABLED);
		de.setType(DataElementType.CHECKBOX);
		de.setSelected(trasformazioneContenutoAbilitato);
		de.setValue(trasformazioneContenutoAbilitato+"");
		de.setPostBack(true);
		dati.addElement(de);
		
		String postbackElement = this.getPostBackElementName();
		
		boolean old_trasformazioneContenutoTemplate = false;
		boolean old_trasformazioneSoapEnvelopeTemplate = false;
		
		if(trasformazioneContenutoAbilitato) {
			// tipo
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO);
			List<String> labels = org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toLabelList(serviceBindingMessage, false);
			if(trasformazioneRestAbilitato) {
				List<String> newLabels = new ArrayList<>();
				String payloadVuotoSoap = org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY.getLabel(ServiceBinding.SOAP);
				for (String l : labels) {
					if(l.equals(payloadVuotoSoap)) {
						newLabels.add(org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY.getLabel(ServiceBinding.REST));
					}
					else {
						newLabels.add(l);
					}
				}
				labels = newLabels;
			}
			else if(trasformazioneSoapAbilitato) {
				List<String> newLabels = new ArrayList<>();
				String payloadVuotoSoap = org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY.getLabel(ServiceBinding.REST);
				for (String l : labels) {
					if(l.equals(payloadVuotoSoap)) {
						newLabels.add(org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY.getLabel(ServiceBinding.SOAP));
					}
					else {
						newLabels.add(l);
					}
				}
				labels = newLabels;
			}
			de.setLabels(labels);
			de.setValues(org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toStringList(serviceBindingMessage, false));
			de.setType(DataElementType.SELECT);
			de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO);
			de.setPostBack(true);
			de.setSelected(trasformazioneContenutoTipo.getValue());
			setTemplateInfo(de, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO, trasformazioneContenutoTipo, serviceBinding, false);
			dati.addElement(de);
			
			if(trasformazioneContenutoTipo.isTemplateRequired()) {
				
				// richiesta null in add
				boolean templateRequired = true;
				
				if(richiesta!=null){
					old_trasformazioneContenutoTemplate = richiesta.getConversioneTemplate() != null && richiesta.getConversioneTemplate().length > 0;
					TipoTrasformazione oldTrasformazioneContenutoTipo = StringUtils.isNotEmpty(richiesta.getConversioneTipo()) 
							? org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(richiesta.getConversioneTipo()) : org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
					if(trasformazioneContenutoTipo.equals(oldTrasformazioneContenutoTipo)) {
						templateRequired = false;
					} 
				}
							
				if(postbackElement != null) {
					if(postbackElement.equals(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO)) {
						old_trasformazioneContenutoTemplate = false;
						templateRequired = true;
					} 
//					if(postbackElement.equals(trasformazioneContenutoTemplate.getName())) {
//						if(StringUtils.isEmpty(trasformazioneContenutoTipoCheck))
//							trasformazioneContenutoTipoCheck = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK_UPDATE_FILE;
//					}
				}
				
				de = new DataElement();
				de.setLabel("");
				de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK);
				de.setType(DataElementType.HIDDEN);
				de.setValue(trasformazioneContenutoTipoCheck);
				dati.addElement(de);
				
				if(StringUtils.isNotEmpty(trasformazioneContenutoTipoCheck) && trasformazioneContenutoTipoCheck.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK_UPDATE_TIPO))
					templateRequired = true;
				
				String trasformazioneContenutoLabel = CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TEMPLATE;
				if(old_trasformazioneContenutoTemplate && StringUtils.isEmpty(trasformazioneContenutoTipoCheck)) {
					trasformazioneContenutoLabel = "";
					DataElement saveAs = new DataElement();
					saveAs.setValue(CostantiControlStation.LABEL_DOWNLOAD_DOCUMENTO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TEMPLATE);
					saveAs.setType(DataElementType.LINK);
				
					Parameter pIdTrasformazioneRegola = new Parameter(CostantiControlStation.PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE, idTrasformazione);
					Parameter pIdAccordo = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_ID_ACCORDO, idPorta+"");
					Parameter pTipoAllegato = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO, isPortaDelegata ? "pd" : "pa");
					Parameter pTipoDoc = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO, isPortaDelegata ? 
							ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TEMPLATE : 
								ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TEMPLATE);
					saveAs.setUrl(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT, pIdAccordo, pTipoAllegato, pTipoDoc, pIdTrasformazioneRegola);
					saveAs.setDisabilitaAjaxStatus();
					dati.add(saveAs);
				}
				
				// template
				DataElement trasformazioneContenutoTemplateDataElement = trasformazioneContenutoTemplate.getFileDataElement(trasformazioneContenutoLabel, "", getSize());
				trasformazioneContenutoTemplateDataElement.setRequired(templateRequired);
				dati.add(trasformazioneContenutoTemplateDataElement);
				dati.addAll(trasformazioneContenutoTemplate.getFileNameDataElement());
				dati.add(trasformazioneContenutoTemplate.getFileIdDataElement());
			}
			
			boolean contentTypePerAttachmentSOAP = false;
			if(trasformazioneContenutoTipo.isTrasformazioneProtocolloEnabled()) {
				if(ServiceBinding.REST.equals(serviceBindingMessage) && 
						trasformazioneSoapAbilitato &&
						(!TipoTrasformazione.EMPTY.equals(trasformazioneContenutoTipo)) &&
						CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_ATTACHMENT.equals(trasformazioneSoapEnvelope)) {
					contentTypePerAttachmentSOAP = true;
				}
			}
			
			if(!contentTypePerAttachmentSOAP && trasformazioneContenutoTipo.isContentTypeEnabled()) {
				// Content-type
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONTENT_TYPE);
				de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONTENT_TYPE);
				de.setType(DataElementType.TEXT_EDIT);
				de.setValue(StringEscapeUtils.escapeHtml(trasformazioneRichiestaContentType));
			
				switch (serviceBindingMessage) {
				case REST:
					if(trasformazioneSoapAbilitato) {
						de.setType(DataElementType.HIDDEN);
						de.setValue("");
					}
					break;
				case SOAP:
					if(trasformazioneRestAbilitato) {
						de.setRequired(true);
					}
					break;
				}
		
				dati.addElement(de);
			}
			
			if(trasformazioneContenutoTipo.isTrasformazioneProtocolloEnabled()) {
				switch (serviceBindingMessage) {
				case REST:
					// sezione trasformazione SOAP
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_SOAP);
					de.setType(DataElementType.SUBTITLE);
					dati.addElement(de);
					
					// abilitato
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_TRANSFORMATION);
					de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_TRANSFORMATION);
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(trasformazioneSoapAbilitato);
					de.setValue(trasformazioneSoapAbilitato+"");
					de.setPostBack(true);
					dati.addElement(de);
					
					if(trasformazioneSoapAbilitato) {
						// soap versione
						de = new DataElement();
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_VERSION);
						String soapVersionLabels[] = {CostantiControlStation.LABEL_SOAP_11, CostantiControlStation.LABEL_SOAP_12};
						String soapVersionValues[] = {CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_SOAP_VERSION_11, CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_SOAP_VERSION_12};
						de.setLabels(soapVersionLabels);
						de.setValues(soapVersionValues);
						de.setType(DataElementType.SELECT);
						de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_VERSION);
						de.setSelected(trasformazioneSoapVersion);
						dati.addElement(de);
						
						// soap action
						de = new DataElement();
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ACTION);
						de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ACTION);
						de.setType(DataElementType.TEXT_EDIT);
						de.setValue(trasformazioneSoapAction);
						de.setInfo(dInfoPatternTrasporto);
						dati.addElement(de);
						
						if(!TipoTrasformazione.EMPTY.equals(trasformazioneContenutoTipo)) {
							// Envelope
							de = new DataElement();
							de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE);
							de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE);
							de.setLabels(CostantiControlStation.SELECT_LABELS_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE);
							de.setValues(CostantiControlStation.SELECT_VALUES_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE);
							de.setType(DataElementType.SELECT);
							de.setSelected(trasformazioneSoapEnvelope);
							de.setPostBack(true);
							dati.addElement(de);
													
							if(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_ATTACHMENT.equals(trasformazioneSoapEnvelope)) {
								
								// Content-type
								de = new DataElement();
								de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONTENT_TYPE_ATTACHMENT);
								de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONTENT_TYPE);
								de.setType(DataElementType.TEXT_EDIT);
								de.setValue(trasformazioneRichiestaContentType);
								de.setRequired(true);
								dati.addElement(de);
								
								de = new DataElement();
								de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TITLE_BODY);
								de.setType(DataElementType.SUBTITLE);
								dati.addElement(de);							
								
								// tipo envelope attachement
								de = new DataElement();
								de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TIPO);
								de.setLabels(org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toLabelList(ServiceBinding.SOAP, true));
								de.setValues(org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toStringList(ServiceBinding.SOAP, true));
								de.setType(DataElementType.SELECT);
								de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TIPO);
								de.setSelected(trasformazioneSoapEnvelopeTipo.getValue());
								de.setPostBack(true);
								setTemplateInfo(de, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TIPO, trasformazioneSoapEnvelopeTipo, serviceBinding, false);
								dati.addElement(de);
								
								if(trasformazioneSoapEnvelopeTipo.isTemplateRequired()) {
									
									// richiesta null in add
									boolean templateRequired = true;
									if(richiesta!=null){
										TrasformazioneSoap oldTrasformazioneSoap = richiesta.getTrasformazioneSoap();
										old_trasformazioneSoapEnvelopeTemplate = oldTrasformazioneSoap != null && oldTrasformazioneSoap.getEnvelopeBodyConversioneTemplate() != null && oldTrasformazioneSoap.getEnvelopeBodyConversioneTemplate().length > 0;
										String oldTrasformazioneSoapEnvelopeTipoS = oldTrasformazioneSoap != null ? oldTrasformazioneSoap.getEnvelopeBodyConversioneTipo() : null;
										TipoTrasformazione oldTrasformazioneSoapEnvelopeTipo = StringUtils.isNotEmpty(oldTrasformazioneSoapEnvelopeTipoS) 
												? org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(oldTrasformazioneSoapEnvelopeTipoS) : org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
										if(trasformazioneSoapEnvelopeTipo.equals(oldTrasformazioneSoapEnvelopeTipo)) {
											templateRequired = false;
										}  
									}
									
									if(postbackElement != null) {
										if(postbackElement.equals(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TIPO)) {
											old_trasformazioneSoapEnvelopeTemplate = false;
											templateRequired = true;
										}
										
//										if(postbackElement.equals(trasformazioneSoapEnvelopeTemplate.getName())) {
//											trasformazioneSoapEnvelopeTipoCheck = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK_UPDATE_FILE;
//										}
									}
									
									de = new DataElement();
									de.setLabel("");
									de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TIPO_CHECK);
									de.setType(DataElementType.HIDDEN);
									de.setValue(trasformazioneSoapEnvelopeTipoCheck);
									dati.addElement(de);
									
									if(StringUtils.isNotEmpty(trasformazioneSoapEnvelopeTipoCheck) && trasformazioneSoapEnvelopeTipoCheck.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK_UPDATE_TIPO))
										templateRequired = true;
									
									String trasformazioneSoapEnvelopeTemplateLabel = CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TEMPLATE;
									if(old_trasformazioneSoapEnvelopeTemplate && StringUtils.isEmpty(trasformazioneSoapEnvelopeTipoCheck)) {
										trasformazioneSoapEnvelopeTemplateLabel = "";
										DataElement saveAs = new DataElement();
										saveAs.setValue(CostantiControlStation.LABEL_DOWNLOAD_DOCUMENTO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TEMPLATE);
										saveAs.setType(DataElementType.LINK);

										Parameter pIdTrasformazioneRegola = new Parameter(CostantiControlStation.PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE, idTrasformazione);
										Parameter pIdAccordo = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_ID_ACCORDO, idPorta+"");
										Parameter pTipoAllegato = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO, isPortaDelegata ? "pd" : "pa");
										Parameter pTipoDoc = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO, isPortaDelegata 
												? ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_DELEGATA_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TEMPLATE 
														: ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PORTA_APPLICATIVA_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TEMPLATE);
										saveAs.setUrl(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT, pIdAccordo, pTipoAllegato, pTipoDoc,pIdTrasformazioneRegola);
										dati.add(saveAs);
									}
									
									// 	template envelope attachement
									DataElement trasformazioneSoapEnvelopeTemplateDataElement = trasformazioneSoapEnvelopeTemplate.getFileDataElement(trasformazioneSoapEnvelopeTemplateLabel, "", getSize());
									trasformazioneSoapEnvelopeTemplateDataElement.setRequired(templateRequired);
									dati.add(trasformazioneSoapEnvelopeTemplateDataElement);
									dati.addAll(trasformazioneSoapEnvelopeTemplate.getFileNameDataElement());
									dati.add(trasformazioneSoapEnvelopeTemplate.getFileIdDataElement());
								}
							}
						}
					}
					break;
				case SOAP:
				default:
					// sezione trasformazione REST
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_REST);
					de.setType(DataElementType.SUBTITLE);
					dati.addElement(de);
	
					// abilitato
					de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REST_TRANSFORMATION);
					de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REST_TRANSFORMATION);
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(trasformazioneRestAbilitato);
					de.setValue(trasformazioneRestAbilitato+"");
					de.setPostBack(true);
					dati.addElement(de);
					
					if(trasformazioneRestAbilitato) {
						//  path
						de = new DataElement();
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REST_PATH);
						de.setValue(trasformazioneRestPath);
						de.setType(DataElementType.TEXT_AREA);
						de.setRows(3);
						de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REST_PATH);
						de.setSize(this.getSize());
						de.setRequired(true);
						de.setInfo(dInfoPatternTrasporto);
						dati.addElement(de);
						
						// method
						de = getHttpMethodDataElementTrasformazione(tipoOP, trasformazioneRestMethod);
						dati.addElement(de);
						
					}
					
					break;
				}
			}
		}
		
		return dati;
	}
	
	private void setTemplateInfo(DataElement de, String label, org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione trasformazioneContenutoTipo, 
			org.openspcoop2.core.registry.constants.ServiceBinding serviceBinding, boolean risposta) {
		switch (trasformazioneContenutoTipo) {
		case TEMPLATE:
			DataElementInfo dInfoPatternContenuto = new DataElementInfo(label);
			dInfoPatternContenuto.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TEMPLATE);
			if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(serviceBinding)) {
				if(risposta) {
					dInfoPatternContenuto.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI_CON_RISPOSTE);
				}
				else {
					dInfoPatternContenuto.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI);
				}
			}
			else {
				if(risposta) {
					dInfoPatternContenuto.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE);
				}
				else {
					dInfoPatternContenuto.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI);
				}
			}
			de.setInfo(dInfoPatternContenuto);
			break;
		case FREEMARKER_TEMPLATE:
		case CONTEXT_FREEMARKER_TEMPLATE:
		case FREEMARKER_TEMPLATE_ZIP:
			dInfoPatternContenuto = new DataElementInfo(label);
			if(org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.FREEMARKER_TEMPLATE.equals(trasformazioneContenutoTipo) ||
					org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.CONTEXT_FREEMARKER_TEMPLATE.equals(trasformazioneContenutoTipo)) {
				dInfoPatternContenuto.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_FREEMARKER);
			}
			else {
				dInfoPatternContenuto.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_FREEMARKER_ZIP);
			}
			if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(serviceBinding)) {
				if(risposta) {
					dInfoPatternContenuto.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_FREEMARKER);
				}
				else {
					dInfoPatternContenuto.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_FREEMARKER);
				}
			}
			else {
				if(risposta) {
					dInfoPatternContenuto.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_FREEMARKER);
				}
				else {
					dInfoPatternContenuto.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_FREEMARKER);
				}
			}
			de.setInfo(dInfoPatternContenuto);
			break;
		case VELOCITY_TEMPLATE:
		case CONTEXT_VELOCITY_TEMPLATE:
		case VELOCITY_TEMPLATE_ZIP:
			dInfoPatternContenuto = new DataElementInfo(label);
			if(org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.VELOCITY_TEMPLATE.equals(trasformazioneContenutoTipo) ||
					org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.CONTEXT_VELOCITY_TEMPLATE.equals(trasformazioneContenutoTipo)) {
				dInfoPatternContenuto.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_VELOCITY);
			}
			else {
				dInfoPatternContenuto.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_VELOCITY_ZIP);
			}
			if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(serviceBinding)) {
				if(risposta) {
					dInfoPatternContenuto.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_VELOCITY);
				}
				else {
					dInfoPatternContenuto.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_VELOCITY);
				}
			}
			else {
				if(risposta) {
					dInfoPatternContenuto.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_VELOCITY);
				}
				else {
					dInfoPatternContenuto.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_VELOCITY);
				}
			}
			de.setInfo(dInfoPatternContenuto);
			break;
		case ZIP:
		case TGZ:
		case TAR:
			dInfoPatternContenuto = new DataElementInfo(label);
			dInfoPatternContenuto.setHeaderBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS);
			if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(serviceBinding)) {
				if(risposta) {
					dInfoPatternContenuto.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI_CON_RISPOSTE);
				}
				else {
					dInfoPatternContenuto.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI);
				}
			}
			else {
				if(risposta) {
					dInfoPatternContenuto.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI_CON_RISPOSTE);
				}
				else {
					dInfoPatternContenuto.setListBody(DynamicHelperCostanti.LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI);
				}
			}
			de.setInfo(dInfoPatternContenuto);
			break;
		case EMPTY:
		case XSLT:
			break;
		}
	}
	
	public boolean trasformazioniRispostaHeaderCheckData(TipoOperazione tipoOp) throws Exception {
		try{
			String tipo = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER_TIPO);
			String nome = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER_NOME);
			String valore = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER_VALORE);
			
			// Campi obbligatori
			if (StringUtils.isEmpty(nome)) {
				this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER_NOME));
				return false;
			}
			if ((nome.indexOf(" ") != -1) ) {
				this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_NEL_CAMPO_NOME);
				return false;
			}
			if(!this.checkLength255(nome, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER_NOME)) {
				return false;
			}
			
			if (StringUtils.isEmpty(tipo)) {
				this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER_TIPO));
				return false;
			}
			if(!this.checkLength255(tipo, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER_TIPO)) {
				return false;
			}
			
			if(!CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_DELETE.equals(tipo)) {
				if (StringUtils.isEmpty(valore)) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_VALORE));
					return false;
				}
				try{
					DynamicUtils.validate(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER_VALORE, valore, true, false);
				}catch(Exception e){
					this.pd.setMessage("Il valore indicato nel parametro '"+CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER_VALORE+"' non risulta corretto: "+e.getMessage());
					return false;
				}
			}
			
			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public boolean trasformazioniRichiestaHeaderCheckData(TipoOperazione tipoOp) throws Exception {
		try{
			String tipo = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_TIPO);
			String nome = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_NOME);
			String valore = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_VALORE);
			
			// Campi obbligatori
			if (StringUtils.isEmpty(nome)) {
				this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_NOME));
				return false;
			}
			if ((nome.indexOf(" ") != -1) ) {
				this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_NEL_CAMPO_NOME);
				return false;
			}
			if(!this.checkLength255(nome, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_NOME)) {
				return false;
			}
			
			if (StringUtils.isEmpty(tipo)) {
				this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_TIPO));
				return false;
			}
			if(!this.checkLength255(tipo, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_TIPO)) {
				return false;
			}
			
			if(!CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_DELETE.equals(tipo)) {
				if (StringUtils.isEmpty(valore)) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_VALORE));
					return false;
				}
				try{
					DynamicUtils.validate(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_VALORE, valore, true, false);
				}catch(Exception e){
					this.pd.setMessage("Il valore indicato nel parametro '"+CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_VALORE+"' non risulta corretto: "+e.getMessage());
					return false;
				}
			}
			
			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public boolean trasformazioniRichiestaUrlParameterCheckData(TipoOperazione tipoOp) throws Exception {
		try{
			String tipo = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_TIPO);
			String nome = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_NOME);
			String valore = this.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_VALORE);
			
			// Campi obbligatori
			if (StringUtils.isEmpty(nome)) {
				this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_NOME));
				return false;
			}
			if ((nome.indexOf(" ") != -1) ) {
				this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_NEL_CAMPO_NOME);
				return false;
			}
			if(!this.checkLength255(nome, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_NOME)) {
				return false;
			}
			
			if (StringUtils.isEmpty(tipo)) {
				this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_TIPO));
				return false;
			}
			if(!this.checkLength255(tipo, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_TIPO)) {
				return false;
			}
			
			if(!CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_DELETE.equals(tipo)) {
				if (StringUtils.isEmpty(valore)) {
					this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_VALORE));
					return false;
				}
				try{
					DynamicUtils.validate(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_VALORE, valore, true, false);
				}catch(Exception e){
					this.pd.setMessage("Il valore indicato nel parametro '"+CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_VALORE+"' non risulta corretto: "+e.getMessage());
					return false;
				}
			}
			
			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public Vector<DataElement> addPorteTrasformazioniServizioApplicativoToDati(TipoOperazione tipoOp, Vector<DataElement> dati, String idTrasformazione, boolean fromList,
		String servizioApplicativo, String[] servizioApplicativoList, int sizeAttuale, 
		boolean addMsgServiziApplicativoNonDisponibili, boolean addTitle) {
		
		if(fromList) {
			DataElement de = new DataElement();
			de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_LIST);
			de.setType(DataElementType.HIDDEN);
			de.setValue(fromList+"");
			dati.addElement(de);
		}
		
		DataElement  de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE);
		de.setName(CostantiControlStation.PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE);
		de.setType(DataElementType.HIDDEN);
		de.setValue(idTrasformazione);
		dati.addElement(de);
		
		if(servizioApplicativoList!=null && servizioApplicativoList.length>0){
		
			String labelApplicativo = CostantiControlStation.LABEL_PARAMETRO_SERVIZIO_APPLICATIVO;
			if(!this.isModalitaCompleta()) {
				labelApplicativo = CostantiControlStation.LABEL_PARAMETRO_APPLICATIVO;
			}
			
			if(addTitle) {
				de = new DataElement();
				de.setType(DataElementType.TITLE);
				de.setLabel(labelApplicativo);
				dati.addElement(de);
			}
			
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
					this.pd.setMessage("Non esistono ulteriori servizi applicativi associabili alla trasformazione",org.openspcoop2.web.lib.mvc.MessageType.INFO);
				}
				else{
					this.pd.setMessage("Non esistono servizi applicativi associabili alla trasformazione",org.openspcoop2.web.lib.mvc.MessageType.INFO);
				}
				this.pd.disableEditMode();
			}
		}

		return dati;
	}
	
	public Vector<DataElement> addPorteTrasformazioniSoggettoToDati(TipoOperazione tipoOp, Vector<DataElement> dati, String idTrasformazione, boolean fromList,
		String[] soggettiLabelList, String[] soggettiList, String soggetto, int sizeAttuale, 
			boolean addMsgSoggettiNonDisponibili, boolean addTitle) {
		
		if(fromList) {
			DataElement de = new DataElement();
			de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_LIST);
			de.setType(DataElementType.HIDDEN);
			de.setValue(fromList+"");
			dati.addElement(de);
		}
		
		DataElement  de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE);
		de.setName(CostantiControlStation.PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE);
		de.setType(DataElementType.HIDDEN);
		de.setValue(idTrasformazione);
		dati.addElement(de);
			
		if(soggettiList!=null && soggettiList.length>0){
		
			if(addTitle) {
				  de = new DataElement();
				de.setType(DataElementType.TITLE);
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_SOGGETTO);
				dati.addElement(de);
			}
			
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

	public Vector<DataElement> addPorteTrasformazioniServizioApplicativoAutorizzatiToDati(TipoOperazione tipoOp, Vector<DataElement> dati, String idTrasformazione, boolean fromList, 
		String[] soggettiLabelList, String[] soggettiList, String soggetto, int sizeAttuale, 
		Map<String,List<IDServizioApplicativoDB>> listServiziApplicativi, String sa,
			boolean addMsgApplicativiNonDisponibili) {
		
		if(fromList) {
			DataElement de = new DataElement();
			de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_LIST);
			de.setType(DataElementType.HIDDEN);
			de.setValue(fromList+"");
			dati.addElement(de);
		}
		
		if(soggettiList!=null && soggettiList.length>0 && listServiziApplicativi!=null && listServiziApplicativi.size()>0){
		
			DataElement de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_APPLICATIVO);
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE);
			de.setName(CostantiControlStation.PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE);
			de.setType(DataElementType.HIDDEN);
			de.setValue(idTrasformazione);
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
			
			List<IDServizioApplicativoDB> listSA = null;
			if(soggetto!=null && !"".equals(soggetto)) {
				listSA = listServiziApplicativi.get(soggetto);
			}
			
			if(listSA!=null && !listSA.isEmpty()) {
				
				String [] saValues = new String[listSA.size()];
				String [] saLabels = new String[listSA.size()];
				int index =0;
				for (IDServizioApplicativoDB saObject : listSA) {
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
	
	public DataElement getHttpMethodDataElement(TipoOperazione tipoOperazione, String httpMethod, String label, String name, boolean addQualsiasi, String labelQualsiasi, String valueQualsiasi) {
		DataElement de = new DataElement();
		
		de.setLabel(label);
		de.setSelected(httpMethod);
		de.setType(DataElementType.SELECT);
		de.setName(name);
		de.setSize(this.getSize());
		de.setPostBack(true);
		
		HttpMethod[] httpMethods = HttpMethod.values();
		int numeroOptions = !addQualsiasi ? httpMethods.length : httpMethods.length + 1;
		
		String [] values = new String[numeroOptions];
		String [] labels = new String[numeroOptions];

		if(addQualsiasi) {
			labels[0] = labelQualsiasi;
			values[0] = valueQualsiasi;
		}
		
		for (int i = 0; i < httpMethods.length; i++) {
			HttpMethod method = httpMethods[i];
			labels[(addQualsiasi ? i+1 : i)] = method.name();
			values[(addQualsiasi ? i+1 : i)] = method.name();
		}
		
		de.setLabels(labels);
		de.setValues(values);
		
		return de;
	}
	
	public boolean checkAzioniUtilizzateErogazione(AccordiServizioParteSpecificaPorteApplicativeMappingInfo mappingInfo, String [] azioni) throws Exception {
		List<MappingErogazionePortaApplicativa> list = mappingInfo.getListaMappingErogazione();
		return checkAzioniUtilizzateErogazione(list, azioni);
	}
	public boolean checkAzioniUtilizzateErogazione(List<MappingErogazionePortaApplicativa> list, String [] azioni) throws Exception {
		if(azioni==null || azioni.length<=0) {
			return true;
		}
		if(list!=null && !list.isEmpty()) {
			for (MappingErogazionePortaApplicativa mappingErogazionePortaApplicativa : list) {
				IDPortaApplicativa idPA = mappingErogazionePortaApplicativa.getIdPortaApplicativa();
				List<AttivazionePolicy> listPolicies = this.confCore.attivazionePolicyList(null, RuoloPolicy.APPLICATIVA, idPA.getNome());
				List<ConfigurazioneAllarmeBean> listAllarmi = null;
				if(this.confCore.isConfigurazioneAllarmiEnabled()) {
					listAllarmi = this.confCore.allarmiList(new Search(true), RuoloPorta.APPLICATIVA, idPA.getNome());
				}
				PortaApplicativa pa = this.porteApplicativeCore.getPortaApplicativa(idPA);
				if(this._checkAzioniUtilizzate(listPolicies, listAllarmi, pa.getTrasformazioni(), azioni, 
						mappingErogazionePortaApplicativa.getDescrizione(), list.size())==false) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public boolean checkAzioniUtilizzateFruizione(AccordiServizioParteSpecificaFruitoriPorteDelegateMappingInfo mappingInfo, String [] azioni) throws Exception {
		List<MappingFruizionePortaDelegata> list = mappingInfo.getListaMappingFruizione();
		return this.checkAzioniUtilizzateFruizione(list, azioni);
	}
	public boolean checkAzioniUtilizzateFruizione(List<MappingFruizionePortaDelegata> list, String [] azioni) throws Exception {
		if(azioni==null || azioni.length<=0) {
			return true;
		}
		if(list!=null && !list.isEmpty()) {
			for (MappingFruizionePortaDelegata mappingFruizionePortaDelegata : list) {
				IDPortaDelegata idPD = mappingFruizionePortaDelegata.getIdPortaDelegata();
				List<AttivazionePolicy> listPolicies = this.confCore.attivazionePolicyList(null, RuoloPolicy.DELEGATA, idPD.getNome());
				List<ConfigurazioneAllarmeBean> listAllarmi = null;
				if(this.confCore.isConfigurazioneAllarmiEnabled()) {
					listAllarmi = this.confCore.allarmiList(new Search(true), RuoloPorta.DELEGATA, idPD.getNome());
				}
				PortaDelegata pd = this.porteDelegateCore.getPortaDelegata(idPD);
				if(this._checkAzioniUtilizzate(listPolicies, listAllarmi, pd.getTrasformazioni(), azioni, 
						mappingFruizionePortaDelegata.getDescrizione(), list.size())==false) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	private boolean _checkAzioniUtilizzate(List<AttivazionePolicy> listPolicies, List<ConfigurazioneAllarmeBean> listAllarmi, Trasformazioni trasformazioni, String [] azioni, String descrizioneGruppo, int sizeGruppi) {
		
		// verifico rate limiting
		if(listPolicies!=null && !listPolicies.isEmpty()) {
			for (AttivazionePolicy policy : listPolicies) {
				if(policy.getFiltro()!=null && policy.getFiltro().getAzione()!=null) {
					for (String azioneTmp : azioni) {
						
						String [] tmp = policy.getFiltro().getAzione().split(",");
						if(tmp!=null && tmp.length>0) {
							for (String az : tmp) {
								if(azioneTmp.equals(az)) {
									String nomePolicy = policy.getAlias();
									if(nomePolicy==null || "".equals(nomePolicy)) {
										nomePolicy = policy.getIdActivePolicy();
									}
									if(sizeGruppi>1) {
										this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_AZIONE_NON_ASSEGNABILE_RATE_LIMITING_GRUPPO, 
												azioneTmp, nomePolicy, descrizioneGruppo));
									}
									else {
										this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_AZIONE_NON_ASSEGNABILE_RATE_LIMITING, 
												azioneTmp, nomePolicy, descrizioneGruppo));
									}
									return false;	
								}
							}
						}
						
					}
				}
			}
		}
		
		// verifico allarmi
		if(listAllarmi!=null && !listAllarmi.isEmpty()) {
			for (Allarme allarme : listAllarmi) {
				if(allarme.getFiltro()!=null && allarme.getFiltro().getAzione()!=null) {
					for (String azioneTmp : azioni) {
						
						String [] tmp = allarme.getFiltro().getAzione().split(",");
						if(tmp!=null && tmp.length>0) {
							for (String az : tmp) {
								if(azioneTmp.equals(az)) {
									String nomeAllarme = allarme.getAlias();
									if(nomeAllarme==null || "".equals(nomeAllarme)) {
										nomeAllarme = allarme.getNome();
									}
									if(sizeGruppi>1) {
										this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_AZIONE_NON_ASSEGNABILE_ALLARME_GRUPPO, 
												azioneTmp, nomeAllarme, descrizioneGruppo));
									}
									else {
										this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_AZIONE_NON_ASSEGNABILE_ALLARME, 
												azioneTmp, nomeAllarme, descrizioneGruppo));
									}
									return false;	
								}
							}
						}
						
					}
				}
			}
		}
		
		// verifico trasformazioni
		List<TrasformazioneRegola> trasformazione = null;
		if(trasformazioni!=null) {
			trasformazione = trasformazioni.getRegolaList();
		}
		if(trasformazione!=null && !trasformazione.isEmpty()) {
			for (TrasformazioneRegola regola : trasformazione) {
				if(regola.getApplicabilita()!=null && regola.getApplicabilita().sizeAzioneList()>0) {
					List<String> azioniTrasf = regola.getApplicabilita().getAzioneList();
					for (String azioneTmp : azioni) {
						if(azioniTrasf.contains(azioneTmp)) {
							String nomeRegola = regola.getNome();
							if(sizeGruppi>1) {
								this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_AZIONE_NON_ASSEGNABILE_TRASFORMAZIONE_GRUPPO, 
										azioneTmp, nomeRegola, descrizioneGruppo));
							}
							else {
								this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_AZIONE_NON_ASSEGNABILE_TRASFORMAZIONE, 
										azioneTmp, nomeRegola, descrizioneGruppo));
							}
							return false;	
						}
					}
				}
			}
		}
		return true;
	}
	
	public DataElement getHttpReturnCodeDataElement(String label, String name, String value, boolean required) {
		DataElement de = new DataElement();
		de.setLabel(label);
		de.setValue(value);
		de.setType(DataElementType.NUMBER);
		de.setName(name);
		de.setMinValue(200);
		de.setMaxValue(599);
		de.setSize(getSize());
		de.reloadMinValue(false);
		de.setRequired(required); 
		return de;
	}
	
	public DataElement getHttpReturnCodeIntervallDataElement(String label, 
			String nameMin, String nameMax, 
			String valueMin, String valueMax, 
			boolean required) {
		DataElement de = new DataElement();
		de.setLabel("&nbsp;");
		de.setValues(Arrays.asList(valueMin + "", valueMax + ""));
		de.setType(DataElementType.INTERVAL_NUMBER);
		de.setNames(Arrays.asList(nameMin, nameMax));
		de.setMinValue(200);
		de.setMaxValue(599);
		de.setSize(getSize());
		de.reloadMinValue(false);
		de.setRequired(required); 
		return de;
	}
	
	public DataElement getVersionDataElement(String label, String name, String value, boolean required) {
		DataElement de = new DataElement();
		de.setLabel(label);
		de.setValue(value);
		de.setType(DataElementType.NUMBER);
		de.setName(name);
		de.setMinValue(1);
		de.setMaxValue(999);
		de.setSize(getSize());
		//de.reloadMinValue(false);
		de.setRequired(required); 
		return de;
	}
	
	public String getUpperFirstChar(String value) {
		return (value.charAt(0)+"").toUpperCase() + value.substring(1);
	}
	
	public boolean isFunzionalitaProtocolloSupportataDalProtocollo(String protocollo, ServiceBinding serviceBinding,FunzionalitaProtocollo funzionalitaProtocollo)
			throws DriverRegistroServiziNotFound, DriverRegistroServiziException, DriverConfigurazioneException {
		if(serviceBinding == null) {
			List<ServiceBinding> serviceBindingListProtocollo = this.core.getServiceBindingListProtocollo(protocollo);
			
			boolean supportato = true;
			if(serviceBindingListProtocollo != null && serviceBindingListProtocollo.size() > 0) {
				for (ServiceBinding serviceBinding2 : serviceBindingListProtocollo) {
					boolean supportatoTmp = this.core.isFunzionalitaProtocolloSupportataDalProtocollo(protocollo, serviceBinding2, funzionalitaProtocollo);
					supportato = supportato || supportatoTmp;
				}
			}
			return supportato;
		} else {
			return this.core.isFunzionalitaProtocolloSupportataDalProtocollo(protocollo, serviceBinding, funzionalitaProtocollo);
		}
	}
	
	public Vector<DataElement>  addProprietaAutorizzazioneCustomToDati(Vector<DataElement> dati, TipoOperazione tipoOp, String nome, String valore) {

		DataElement de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_AUTORIZZAZIONE_CUSTOM_PROPERTIES);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_NOME);
		de.setValue(nome);
		if(TipoOperazione.ADD.equals(tipoOp)){
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		}
		else{
			de.setType(DataElementType.TEXT);
		}
		de.setName(CostantiControlStation.PARAMETRO_NOME);
		de.setSize(this.getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_VALORE);
		de.setType(DataElementType.TEXT_EDIT);
		de.setRequired(true);
		de.setName(CostantiControlStation.PARAMETRO_VALORE);
		de.setValue(valore);
		de.setSize(this.getSize());
		dati.addElement(de);

		return dati;
	}
	
	public Vector<DataElement>  addProprietaAutenticazioneCustomToDati(Vector<DataElement> dati, TipoOperazione tipoOp, String nome, String valore) {

		DataElement de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_AUTENTICAZIONE_CUSTOM_PROPERTIES);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_NOME);
		de.setValue(nome);
		if(TipoOperazione.ADD.equals(tipoOp)){
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		}
		else{
			de.setType(DataElementType.TEXT);
		}
		de.setName(CostantiControlStation.PARAMETRO_NOME);
		de.setSize(this.getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_VALORE);
		de.setType(DataElementType.TEXT_EDIT);
		de.setRequired(true);
		de.setName(CostantiControlStation.PARAMETRO_VALORE);
		de.setValue(valore);
		de.setSize(this.getSize());
		dati.addElement(de);

		return dati;
		
	}
	
	
	// ****** PROFILO MODI ******
	
	public boolean isProfiloModIPA(String protocollo) {
		return this.core.isProfiloModIPA(protocollo);
	}
	
	public boolean forceHttpsProfiloModiPA() {
		return true;
	}
	public boolean forceHttpsClientProfiloModiPA(IDAccordo idAccordoParteComune, String portType) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		AccordoServizioParteComune aspc = this.apcCore.getAccordoServizioFull(idAccordoParteComune,false);
		String propertyName = CostantiDB.MODIPA_PROFILO_SICUREZZA_CANALE;
		for (ProtocolProperty pp : aspc.getProtocolPropertyList()) {
			if(pp.getName().equals(propertyName)) {
				String value = pp.getValue();
				if(CostantiDB.MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC02.equals(value)) {
					return true;
				} 
				break;
			}
		}
		return false;
	}

	public Vector<DataElement> addProxyPassConfigurazioneRegola(TipoOperazione tipoOp, Vector<DataElement> dati,
			String idRegolaS, String nome, String descrizione, String stato, boolean regExpr, String regolaText,
			String contestoEsterno, String baseUrl, String protocollo, List<String> protocolli, String soggetto,
			List<IDSoggetto> soggetti, String ruolo, String serviceBinding, boolean multiTenant) throws Exception {
		
		DataElement dataElement = new DataElement();
		dataElement.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA);
		dataElement.setType(DataElementType.TITLE);
		dati.add(dataElement);
		
		DataElement de = new DataElement();
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_ID_REGOLA);
		de.setType(DataElementType.HIDDEN);
		de.setValue(idRegolaS);
		dati.add(de);
		
		// nome
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_NOME);
		de.setValue(nome);
//		if(TipoOperazione.ADD.equals(tipoOp)){
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
//		}
//		else{
//			de.setType(DataElementType.TEXT);
//		}
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_NOME);
		de.setSize(this.getSize());
		dati.addElement(de);
		
		// stato
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_STATO);
		String [] labelsStato = {StatoFunzionalita.ABILITATO.toString(), StatoFunzionalita.DISABILITATO.toString()};
		String [] valuesStato = {StatoFunzionalita.ABILITATO.toString(), StatoFunzionalita.DISABILITATO.toString()};
		de.setLabels(labelsStato);
		de.setValues(valuesStato);
		de.setType(DataElementType.SELECT);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_STATO);
		de.setSelected(stato);
		dati.addElement(de);
		
		// descrizione
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_DESCRIZIONE);
		de.setValue(descrizione);
		de.setType(DataElementType.TEXT_AREA);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_DESCRIZIONE);
		de.setSize(this.getSize());
		dati.addElement(de);
		
		
		dataElement = new DataElement();
		dataElement.setLabel(CostantiControlStation.LABEL_PROXY_PASS_REGOLA_CRITERI_APPLICABILITA);
		dataElement.setType(DataElementType.TITLE);
		dati.add(dataElement);
		
		// regExpr
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_REG_EXPR);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_REG_EXPR);
		de.setType(DataElementType.CHECKBOX);
		de.setSelected(regExpr);
		de.setValue(regExpr+"");
		de.setPostBack(true);
		dati.addElement(de);
		
		// regola
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_REGOLA_TEXT);
		de.setValue(regolaText);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_REGOLA_TEXT);
		de.setSize(this.getSize());
		de.setRequired(true);
		DataElementInfo deInfo = new DataElementInfo(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_REGOLA_TEXT);
		if(regExpr) {
			deInfo.setHeaderBody(CostantiControlStation.MESSAGGIO_INFO_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_REGOLA_TEXT_REGEXP);
		}
		else {
			deInfo.setHeaderBody(CostantiControlStation.MESSAGGIO_INFO_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_REGOLA_TEXT_STRINGA_LIBERA);
		}
		de.setInfo(deInfo);
		
		dati.addElement(de);
				
		// profilo
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_PROFILO);
		
		String [] labelsProfili = new String[protocolli.size() + 1];
		String [] valuesProfili = new String[protocolli.size() + 1];
		
		labelsProfili[0] = CostantiControlStation.LABEL_QUALSIASI;
		valuesProfili[0] = "";
		
		int i = 1;
		for (String protocolloS : protocolli) {
			labelsProfili[i] = _getLabelProtocollo(protocolloS);
			valuesProfili[i] = protocolloS;
			i++;
		}
				
		de.setLabels(labelsProfili);
		de.setValues(valuesProfili);
		de.setType(DataElementType.SELECT);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_PROFILO);
		de.setPostBack(true);
		de.setSelected(protocollo);
		dati.addElement(de);
		
		// soggetto
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_SOGGETTO);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_SOGGETTO);
		if(!"".equals(protocollo) && multiTenant) {
			String [] labelsSoggetti = new String[soggetti.size() + 1];
			String [] valuesSoggetti = new String[soggetti.size() + 1];
			
			labelsSoggetti[0] = CostantiControlStation.LABEL_QUALSIASI;
			valuesSoggetti[0] = "";
			
			i = 1;
			for (IDSoggetto idSoggetto : soggetti) { 
				labelsSoggetti[i] = _getLabelNomeSoggetto(idSoggetto);
				valuesSoggetti[i] = idSoggetto.toString();
				i++;
			}
			
			de.setLabels(labelsSoggetti);
			de.setValues(valuesSoggetti);
			de.setType(DataElementType.SELECT);
			de.setSelected(soggetto);
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(soggetto);
		}
		dati.addElement(de);
		
		// ruolo
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_RUOLO);
		de.setLabels(CostantiControlStation.SELECT_LABELS_PARAMETRO_PROXY_PASS_REGOLA_RUOLO);
		de.setValues(CostantiControlStation.SELECT_VALUES_PARAMETRO_PROXY_PASS_REGOLA_RUOLO);
		de.setType(DataElementType.SELECT);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_RUOLO);
		de.setSelected(ruolo);
		dati.addElement(de);
		
		// serviceBinding
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_SERVICE_BINDING);
		de.setLabels(CostantiControlStation.SELECT_LABELS_PARAMETRO_PROXY_PASS_REGOLA_SERVICE_BINDING);
		de.setValues(CostantiControlStation.SELECT_VALUES_PARAMETRO_PROXY_PASS_REGOLA_SERVICE_BINDING);
		de.setType(DataElementType.SELECT);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_SERVICE_BINDING);
		de.setSelected(serviceBinding);
		dati.addElement(de);

		dataElement = new DataElement();
		dataElement.setLabel(CostantiControlStation.LABEL_PROXY_PASS_REGOLA_NUOVA_URL);
		dataElement.setType(DataElementType.TITLE);
		dati.add(dataElement);
		
		StringBuilder sb = new StringBuilder();
		if(regExpr) {
			sb.append(CostantiControlStation.MESSAGGIO_INFO_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_EXPR_DATI_DINAMICI_REGXP);
		}
		CanaliConfigurazione canali = this.confCore.getCanaliConfigurazione(false);
		if(canali!=null && StatoFunzionalita.ABILITATO.equals(canali.getStato())) {
			sb.append(CostantiControlStation.MESSAGGIO_INFO_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_EXPR_DATI_DINAMICI_CANALE);
		}
		sb.append(CostantiControlStation.MESSAGGIO_INFO_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_EXPR_DATI_DINAMICI_TAG);
		
		// baseUrl
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_BASE_URL);
		de.setValue(baseUrl);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_BASE_URL);
		de.setSize(this.getSize());
		deInfo = new DataElementInfo(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_BASE_URL);
		deInfo.setHeaderBody(CostantiControlStation.MESSAGGIO_INFO_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_BASE_URL);
		deInfo.setHeaderBody(deInfo.getHeaderBody()+sb.toString());
		de.setInfo(deInfo);
		dati.addElement(de);
		
		// contesto
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_CONTESTO_ESTERNO);
		de.setValue(contestoEsterno);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_CONTESTO_ESTERNO);
		de.setSize(this.getSize());
		de.setRequired(false);
		deInfo = new DataElementInfo(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_CONTESTO_ESTERNO);
		deInfo.setHeaderBody(CostantiControlStation.MESSAGGIO_INFO_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_CONTESTO_ESTERNO);
		deInfo.setHeaderBody(deInfo.getHeaderBody()+sb.toString());
		de.setInfo(deInfo);
		dati.addElement(de);
				
		return dati;
	}
	
	public String getStatoConnettoriMultipliPortaApplicativa(PortaApplicativa paAssociata) throws DriverControlStationException, DriverControlStationNotFound {
		boolean connettoreMultiploEnabled = paAssociata.getBehaviour() != null;
		return connettoreMultiploEnabled ? CostantiConfigurazione.ABILITATO.toString() : CostantiConfigurazione.DISABILITATO.toString();
	}
	
	public String getNomiConnettoriMultipliPortaApplicativa(PortaApplicativa paAssociata) throws DriverControlStationException, DriverControlStationNotFound, NotFoundException {
		StringBuilder sbConnettoriMultipli = new StringBuilder();
		
		TipoBehaviour behaviourType = TipoBehaviour.toEnumConstant(paAssociata.getBehaviour().getNome());
		switch (behaviourType) {
		case CONSEGNA_LOAD_BALANCE:
			String loadBalanceStrategia = ConfigurazioneLoadBalancer.readLoadBalancerType(paAssociata.getBehaviour());
			LoadBalancerType type = LoadBalancerType.toEnumConstant(loadBalanceStrategia, true);
			sbConnettoriMultipli.append(behaviourType.getLabel()).append(" '");
			if(StickyUtils.isConfigurazioneSticky(paAssociata, ControlStationLogger.getPddConsoleCoreLogger())){
				sbConnettoriMultipli.append(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_STICKY).append(" ");
			}
			sbConnettoriMultipli.append(type.getLabel()).append("'");
			
			boolean condizionale = ConditionalUtils.isConfigurazioneCondizionale(paAssociata, ControlStationLogger.getPddConsoleCoreLogger());
			if(condizionale) {
				sbConnettoriMultipli.append(" ").append(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONSEGNA_CONDIZIONALE);
			}
			
			break;
		case CONSEGNA_MULTIPLA:
			sbConnettoriMultipli.append(behaviourType.getLabel());
			
			condizionale = ConditionalUtils.isConfigurazioneCondizionale(paAssociata, ControlStationLogger.getPddConsoleCoreLogger());
			if(condizionale) {
				sbConnettoriMultipli.append(" ").append(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONSEGNA_CONDIZIONALE);
			}
			
			break;
		case CONSEGNA_CON_NOTIFICHE:
			sbConnettoriMultipli.append(behaviourType.getLabel());
			
			condizionale = ConditionalUtils.isConfigurazioneCondizionale(paAssociata, ControlStationLogger.getPddConsoleCoreLogger());
			if(condizionale) {
				sbConnettoriMultipli.append(" ").append(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_NOTIFICHE_CONDIZIONALI);
			}
			
			break;
		case CONSEGNA_CONDIZIONALE:
			sbConnettoriMultipli.append(behaviourType.getLabel());
			
			break;
		case CUSTOM:
			
			String customLabel = paAssociata.getBehaviour().getNome();
			Plugin plugin = null;
			try {
				plugin = this.confCore.getPlugin(TipoPlugin.BEHAVIOUR,customLabel, false);
			}catch(Throwable e) {}
			if(plugin!=null) {
				customLabel = plugin.getLabel();
			}
			
			sbConnettoriMultipli.append("Consegna Personalizzata '"+customLabel+"'");
			break;
		}
		
		return sbConnettoriMultipli.toString();
	}
	
	public String getToolTipConnettoriMultipliPortaApplicativa(PortaApplicativa paAssociata) throws DriverControlStationException, DriverControlStationNotFound {
		StringBuilder sbConnettoriMultipli = new StringBuilder();
		for (PortaApplicativaServizioApplicativo paSA : paAssociata.getServizioApplicativoList()) {
			if(sbConnettoriMultipli.length() >0)
				sbConnettoriMultipli.append(", ");
			if(paSA.getDatiConnettore() == null) {
				sbConnettoriMultipli.append(CostantiConfigurazione.NOME_CONNETTORE_DEFAULT);
			} else {
				sbConnettoriMultipli.append(paSA.getDatiConnettore().getNome());
			}
		}
		return sbConnettoriMultipli.toString();
	}
	
	public Vector<DataElement> addInformazioniGruppiAsHiddenToDati(TipoOperazione tipoOp, Vector<DataElement> dati,	
			String idTabGruppo, String idTabConnettoriMultipli, String accessoDaAPS, String connettoreAccessoDaGruppi, String connettoreRegistro, String connettoreAccessoDaListaConnettoriMultipli) {
		
		if(idTabGruppo != null) {
			DataElement de = new DataElement();
			de.setName(CostantiControlStation.PARAMETRO_ID_TAB);
			de.setType(DataElementType.HIDDEN);
			de.setValue(idTabGruppo);
			dati.add(de);
		}
		
		if(idTabConnettoriMultipli != null) {
			DataElement de = new DataElement();
			de.setName(CostantiControlStation.PARAMETRO_ID_CONN_TAB);
			de.setType(DataElementType.HIDDEN);
			de.setValue(idTabConnettoriMultipli);
			dati.add(de);
		}
		
		if(accessoDaAPS != null) {
			DataElement de = new DataElement();
			de.setName(CostantiControlStation.PARAMETRO_CONNETTORE_DA_LISTA_APS);
			de.setType(DataElementType.HIDDEN);
			de.setValue(accessoDaAPS);
			dati.add(de);
		}
		
		if(connettoreAccessoDaGruppi != null) {
			DataElement de = new DataElement();
			de.setName(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI);
			de.setType(DataElementType.HIDDEN);
			de.setValue(connettoreAccessoDaGruppi);
			dati.add(de);
		}
		
		if(connettoreRegistro != null) {
			DataElement de = new DataElement();
			de.setName(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO);
			de.setType(DataElementType.HIDDEN);
			de.setValue(connettoreRegistro);
			dati.add(de);
		}
		
		if(connettoreAccessoDaListaConnettoriMultipli != null) {
			DataElement de = new DataElement();
			de.setName(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_LISTA_CONNETTORI_MULTIPLI);
			de.setType(DataElementType.HIDDEN);
			de.setValue(connettoreAccessoDaListaConnettoriMultipli);
			dati.add(de);
		}
		
		return dati;
	}
	
	public boolean isConnettoreDefault(PortaApplicativaServizioApplicativo paSA) {
		return paSA.getDatiConnettore()!= null ? !paSA.getDatiConnettore().isNotifica() : true;
	}
	
	public String getLabelNomePortaApplicativaServizioApplicativo(PortaApplicativaServizioApplicativo paSA) {
		String nomePaSA = paSA.getDatiConnettore()!= null ? paSA.getDatiConnettore().getNome() : CostantiConfigurazione.NOME_CONNETTORE_DEFAULT;
		return nomePaSA;
	}
	
	public int getIdxNuovoConnettoreMultiplo(PortaApplicativa pa) {
		int idxConfigurazione = 0;
		int listaMappingErogazioneSize = pa.sizeServizioApplicativoList();
		
		for (int i = 0; i < listaMappingErogazioneSize; i++) {
			PortaApplicativaServizioApplicativo paSA = pa.getServizioApplicativo(i);
			if(!this.isConnettoreDefault(paSA)) {
				int idx = paSA.getNome().indexOf(ConnettoriCostanti.PARAMETRO_CONNETTORI_MULTIPLI_SAX_PREFIX);
				if(idx > -1) {
					String idxTmp = paSA.getNome().substring(idx + ConnettoriCostanti.PARAMETRO_CONNETTORI_MULTIPLI_SAX_PREFIX.length());
					int idxMax = -1;
					try {
						idxMax = Integer.parseInt(idxTmp);
					}catch(Exception e) {
						idxMax = 0;
					}
					idxConfigurazione = Math.max(idxConfigurazione, idxMax);
				}
			}
		}
				
		return ( ++ idxConfigurazione);
	}

	
	public boolean allActionsRedefinedMappingErogazione(List<String> azioni, List<MappingErogazionePortaApplicativa> lista) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		// verifico se tutte le azioni sono definite in regole specifiche
		boolean all = true;
		if(azioni!=null && azioni.size()>0) {
			for (String azione : azioni) {
				if(lista==null || lista.size()<=0) {
					all  = false;
					break;
				}
				boolean found = false;
				for (MappingErogazionePortaApplicativa mappingErogazionePortaApplicativa : lista) {
					PortaApplicativa paAssociata = this.porteApplicativeCore.getPortaApplicativa(mappingErogazionePortaApplicativa.getIdPortaApplicativa());
					if(paAssociata.getAzione() != null && paAssociata.getAzione().getAzioneDelegataList().contains(azione)) {
						found = true;
						break;
					}
				}
				if(!found) {
					all  = false;
					break;
				}
			}
		}
		return all;
	}
	
	public List<String> getAllActionsNotRedefinedMappingErogazione(List<String> azioni, List<MappingErogazionePortaApplicativa> lista) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		// verifico se tutte le azioni sono definite in regole specifiche
		List<String> l = new ArrayList<>();
		if(lista==null || lista.size()<=0) {
			return azioni;
		}
		l.addAll(azioni);
		for (MappingErogazionePortaApplicativa mappingErogazionePortaApplicativa : lista) {
			PortaApplicativa paAssociata = this.porteApplicativeCore.getPortaApplicativa(mappingErogazionePortaApplicativa.getIdPortaApplicativa());
			if(paAssociata.getAzione() != null && !paAssociata.getAzione().getAzioneDelegataList().isEmpty()) {
				for (String azPA : paAssociata.getAzione().getAzioneDelegataList()) {
					l.remove(azPA);
				}
			}
		}
		return l;
	}
	
	public boolean isSoapOneWay(PortaApplicativa portaApplicativa, MappingErogazionePortaApplicativa mappingErogazionePortaApplicativa, 
			AccordoServizioParteSpecifica asps, AccordoServizioParteComuneSintetico as, ServiceBinding serviceBinding) 
					throws Exception, DriverRegistroServiziException, DriverConfigurazioneException, DriverConfigurazioneNotFound {
		boolean isSoapOneWay = false;
		
		if(serviceBinding.equals(ServiceBinding.SOAP)) {
			// controllo che tutte le azioni del gruppo siano oneway
			// se c'e' almeno un'azione non oneway visualizzo la sezione notifiche
			if(mappingErogazionePortaApplicativa.isDefault()) {
				Map<String,String> azioni = this.porteApplicativeCore.getAzioniConLabel(asps, as, false, true, new ArrayList<String>());
				IDServizio idServizio2 = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps); 
				List<MappingErogazionePortaApplicativa> lista = this.apsCore.mappingServiziPorteAppList(idServizio2,asps.getId(), null);
		
				boolean allActionRedefined = false;
				List<String> actionNonRidefinite = null;
		
				List<String> azioniL = new ArrayList<>();
				if(azioni != null && azioni.size() > 0)
					azioniL.addAll(azioni.keySet());
				allActionRedefined = this.allActionsRedefinedMappingErogazione(azioniL, lista);
				if(!allActionRedefined) {
					actionNonRidefinite = this.getAllActionsNotRedefinedMappingErogazione(azioniL, lista);
					isSoapOneWay = this.porteApplicativeCore.azioniTutteOneway(asps, as, actionNonRidefinite);
				} else {
					isSoapOneWay = false;
				} 
			} else {
				List<String> listaAzioni = portaApplicativa.getAzione()!= null ?  portaApplicativa.getAzione().getAzioneDelegataList() : new ArrayList<String>();
				isSoapOneWay = this.porteApplicativeCore.azioniTutteOneway(asps, as, listaAzioni);
			}
		} else {
			isSoapOneWay = false;
		}
		
		return isSoapOneWay;
	}

	public DataElement newDataElementVisualizzaInNuovoTab(DataElement deParam, String url, String tooltip ) { 
			
		DataElement de = deParam;
		if(de==null) 
			de = new DataElement();
		
		de.setUrl(url);
		de.setTarget(TargetType.BLANK);
		if(tooltip != null)
			de.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_VISUALIZZA_TOOLTIP_CON_PARAMETRO, tooltip));
		else  
			de.setToolTip(CostantiControlStation.ICONA_VISUALIZZA_TOOLTIP);
		
		de.setIcon(CostantiControlStation.ICONA_VISUALIZZA);
		de.setDisabilitaAjaxStatus();
		// link apri nuovo tab
		de.setVisualizzaLinkApriNuovaFinestra(true);
			
		return de;
	}
	
	public void setSecretPleaseCopy(String secret_password, String secret_user, boolean appId, String tipoAuth, OggettoDialogEnum oggettoDialog, String nome) {

		String nomeP = nome!=null ? " "+nome : "";
		String tipoOggetto = null;
		switch (oggettoDialog) {
		case UTENTE:
			tipoOggetto = "all'utente"+nomeP;
			break;
		case SOGGETTO:
			tipoOggetto = "al soggetto"+nomeP;
			break;
		case APPLICATIVO:
			tipoOggetto = "all'applicativo"+nomeP;
			break;
		case EROGAZIONE:
			tipoOggetto = "all'erogazione";
			break;
		case CONNETTORE_MULTIPLO:
			tipoOggetto = "al connettore"+nomeP+" dell'erogazione";
			break;
		}
		
		String header1= "";
		BodyElement utente = Dialog.newBodyElement();
		utente.setVisualizzaCopyAction(true);
		BodyElement password = Dialog.newBodyElement();
		password.setVisualizzaCopyAction(true);
		
		if (ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC.equals(tipoAuth)) {
			utente.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME);
			utente.setType(DataElementType.TEXT_EDIT);
			utente.setValue(StringEscapeUtils.escapeHtml(secret_user));
			utente.setTooltipCopyAction(MessageFormat.format(Costanti.TOOLTIP_ICONA_COPIA, ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME));
			
			password.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			password.setType(DataElementType.TEXT_EDIT);
			password.setValue(StringEscapeUtils.escapeHtml(secret_password));
			password.setTooltipCopyAction(MessageFormat.format(Costanti.TOOLTIP_ICONA_COPIA, ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD));
			
			header1 = ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME+" e " + ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD + " generata" ;
		}
		else if (ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY.equals(tipoAuth)) {
			if(appId) {
				utente.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID);
				utente.setType(DataElementType.TEXT_EDIT);
				utente.setValue(StringEscapeUtils.escapeHtml(secret_user));
				utente.setTooltipCopyAction(MessageFormat.format(Costanti.TOOLTIP_ICONA_COPIA, ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID));
			} else {
				utente = null;
			}
			
			password.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_API_KEY);
			password.setType(DataElementType.TEXT_AREA);
			password.setValue(StringEscapeUtils.escapeHtml(secret_password));
			password.setTooltipCopyAction(MessageFormat.format(Costanti.TOOLTIP_ICONA_COPIA, ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_API_KEY));
			password.setCols(44);
			
			header1 = ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_API_KEY + " generata" ;
			if(appId) {
				header1 = ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID+" e " + header1;
			}
		}
		
		boolean unaCredenziale = (ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY.equals(tipoAuth) && !appId);
		String singolare = unaCredenziale ? "a" : "e";
		String verbo = unaCredenziale ? "viene" : "vengono";
		String oggetto = unaCredenziale ? "la chiave" : "le credenziali";
		String intestazione = StringEscapeUtils.escapeHtml("Di seguito "+verbo+" riportat"+singolare+" "+oggetto+" associat"+singolare+" "+tipoOggetto+".")
				+org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE+
				StringEscapeUtils.escapeHtml("L'informazione viene visualizzata in questo avviso e successivamente non sarà più consultabile.");
		String attenzione = StringEscapeUtils.escapeHtml("Si prega di copiarl"+singolare+" e custodirl"+singolare+" attentamente.");
		
		Dialog dialog = new Dialog();
		
		dialog.setTitolo(Costanti.MESSAGE_TYPE_WARN_TITLE);
		dialog.setHeaderRiga1(header1);
		dialog.setHeaderRiga2(intestazione);
		
		if(utente!=null) {
			dialog.addBodyElement(utente);
		}
		if(password!=null) {
			dialog.addBodyElement(password);
		}

		dialog.setNotaFinale(attenzione);
		
		
		String[][] bottoni = { 
				{ Costanti.LABEL_MONITOR_BUTTON_CHIUDI, "" }
				};
		
		this.pd.setBottoni(bottoni);
		
		this.pd.setDialog(dialog);
		
	}
	
	public void addInUsoButtonVisualizzazioneClassica(Vector<DataElement> e, String titolo, String id, InUsoType inUsoType) {
		this.addInUsoButton(e, DataElementType.BUTTON, titolo, id, inUsoType,
				CostantiControlStation.LABEL_IN_USO_TOOLTIP, Costanti.ICON_USO,
				CostantiControlStation.LABEL_IN_USO_BODY_HEADER_RISULTATI,
				true, true);
	}
	
	public void addInUsoButton(Vector<DataElement> e, String titolo, String id, InUsoType inUsoType) {
		this.addInUsoButton(e, DataElementType.IMAGE, titolo, id, inUsoType,
				CostantiControlStation.LABEL_IN_USO_TOOLTIP, Costanti.ICON_USO,
				CostantiControlStation.LABEL_IN_USO_BODY_HEADER_RISULTATI,
				true, true);
	}
	
	public void addInUsoInfoButton(Vector<DataElement> e, String titolo, String id, InUsoType inUsoType) {
		this.addInUsoButton(e, DataElementType.IMAGE, titolo, id, inUsoType,
				CostantiControlStation.LABEL_IN_USO_INFORMAZIONI_TOOLTIP, Costanti.ICON_USO_INFO,
				CostantiControlStation.LABEL_IN_USO_BODY_HEADER_INFORMAZIONI, 
				true, true);
	}
	
	private void addInUsoButton(Vector<DataElement> e, DataElementType deType, String titolo, String id, InUsoType inUsoType,
			String tooltip, String icon, String headerRiga1, 
			Boolean resizable, Boolean draggable) {
		DataElement de = new DataElement();
		de.setType(deType);
		de.setToolTip(tooltip);
		de.setWidthPx(15);	
		Dialog deDialog = new Dialog();
		deDialog.setIcona(icon);
		deDialog.setTitolo(titolo);
		deDialog.setHeaderRiga1(headerRiga1);
		if(resizable!=null) {
			deDialog.setResizable(resizable);
		}
		if(draggable!=null) {
			deDialog.setDraggable(draggable);
		}
		deDialog.setWidth("800px"); // modifico il default, che è più piccolo e calibrato per la creazione delle credenziali
		
		// Inserire sempre la url come primo elemento del body
		BodyElement bodyElementURL = new Dialog().new BodyElement();
		bodyElementURL.setType(DataElementType.HIDDEN);
		bodyElementURL.setName(UtilsCostanti.PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_URL);
		Parameter pIdOggetto = new Parameter(UtilsCostanti.PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_ID_OGGETTO, id);
		Parameter pTipoOggetto = new Parameter(UtilsCostanti.PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_OGGETTO, inUsoType.toString());
		Parameter pTipoRisposta = new Parameter(UtilsCostanti.PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_RISPOSTA, UtilsCostanti.VALUE_PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_RISPOSTA_TEXT);
		bodyElementURL.setUrl(UtilsCostanti.SERVLET_NAME_INFORMAZIONI_UTILIZZO_OGGETTO, pIdOggetto,pTipoOggetto,pTipoRisposta);
		deDialog.addBodyElement(bodyElementURL);
		
		// TextArea
		BodyElement bodyElement = new Dialog().new BodyElement();
		bodyElement.setType(DataElementType.TEXT_AREA);
		bodyElement.setLabel("");
		bodyElement.setValue("");
		bodyElement.setRows(15);
		if(resizable!=null) {
			bodyElement.setResizable(resizable);
		}
		deDialog.addBodyElement(bodyElement );
		
		de.setDialog(deDialog );
		e.addElement(de);
	}
	
	public void addCanaleToDati(Vector<DataElement> dati, TipoOperazione tipoOperazione, String canaleStato, String canale, String canaleAPI,
			List<CanaleConfigurazione> canaleList, boolean gestioneCanaliEnabled) {
		this.addCanaleToDati(dati, tipoOperazione, canaleStato, canale, canaleAPI, canaleList, gestioneCanaliEnabled, true);
	}
	
	public void addCanaleToDati(Vector<DataElement> dati, TipoOperazione tipoOperazione, String canaleStato, String canale, String canaleAPI,
			List<CanaleConfigurazione> canaleList, boolean gestioneCanaliEnabled, boolean addTitle) {
		DataElement de;
		// canale
		if(gestioneCanaliEnabled) {
			if(addTitle) {
				DataElement dataElement = new DataElement();
				dataElement.setLabel(CostantiControlStation.LABEL_CONFIGURAZIONE_CANALE);
				dataElement.setType(DataElementType.TITLE);
				dati.add(dataElement);
			}
			
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_CANALE_STATO);
			de.setValues(CostantiControlStation.VALUES_PARAMETRO_CONFIGURAZIONE_CANALI_CANALE_STATO);
			
			List<String> labelsCanaleStato = new ArrayList<>();
			CanaleConfigurazione canaleConfigurazioneDefault = canaleList.stream().filter((c) -> c.isCanaleDefault()).findFirst().get();
			String nomeCanaleDefault = MessageFormat.format(CostantiControlStation.LABEL_DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_CANALI_CANALE_STATO_DEFAULT, canaleConfigurazioneDefault.getNome());
			if(canaleAPI != null) {
				nomeCanaleDefault = MessageFormat.format(CostantiControlStation.LABEL_DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_CANALI_CANALE_STATO_DEFAULT_API, canaleAPI);
			}
			labelsCanaleStato.add(nomeCanaleDefault);
			labelsCanaleStato.add(CostantiControlStation.LABEL_DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_CANALI_CANALE_STATO_RIDEFINITO);
			
			de.setLabels(labelsCanaleStato);
			de.setType(DataElementType.SELECT);
			de.setSelected(canaleStato);
			de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CANALI_CANALE_STATO);
			de.setSize(this.getSize());
			de.setPostBack(true);
			dati.addElement(de);
			
			if(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CANALE_STATO_RIDEFINITO.equals(canaleStato)) {
				de = new DataElement();
				de.setLabel(""); //(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_CANALE);
				List<String> canaliListValues = canaleList.stream().map(CanaleConfigurazione::getNome).collect(Collectors.toList());
				de.setValues(canaliListValues);
				de.setLabels(canaliListValues);
				de.setType(DataElementType.SELECT);
				de.setSelected(canale);
				de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CANALI_CANALE);
				de.setSize(this.getSize());
				dati.addElement(de);
			}
		}
	}
	
	public void addCanaleToDatiAsHidden(Vector<DataElement> dati, TipoOperazione tipoOperazione, String canaleStato, String canale, boolean gestioneCanaliEnabled) {
		DataElement de;
		// canale
		if(gestioneCanaliEnabled) {
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_CANALE_STATO);
			de.setType(DataElementType.HIDDEN);
			de.setValue(canaleStato);
			de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CANALI_CANALE_STATO);
			de.setSize(this.getSize());
			dati.addElement(de);
			
			if(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CANALE_STATO_RIDEFINITO.equals(canaleStato)) {
				de = new DataElement();
				de.setLabel(""); //(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_CANALE);
				de.setType(DataElementType.HIDDEN);
				de.setValue(canale);
				de.setName(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CANALI_CANALE);
				de.setSize(this.getSize());
				dati.addElement(de);
			}
		}
	}
	
	public boolean canaleCheckData(String canaleStato, String canale, boolean gestioneCanaliEnabled) throws Exception {
		// validazione canale
		if(gestioneCanaliEnabled) {
			if(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CANALE_STATO_RIDEFINITO.equals(canaleStato)) {
				if(this.checkNCName(canale, CostantiControlStation.LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_CANALE)==false){
					return false;
				}
			}
		}
		
		return true;
	}
	
	public void addCustomFieldSearchForm(TipoPlugin tipoPlugin,
			String ruolo, // applicativa/delegata o richiesta/risposta a seconda del tipo di plugin
			String fase,
			String nomeParametroSelezioneTipo,
			String nomeParametro, String label, String value, boolean hidden, Vector<DataElement> dati,
			boolean postBack_viaPOST) throws Exception {
		addCustomField(tipoPlugin,
				ruolo,
				fase,
				nomeParametroSelezioneTipo,
				nomeParametro, label, 
				value, null, false, 
				hidden, dati,
				postBack_viaPOST, false, null, null, true, null, null);
	}
	
	public void addCustomField(TipoPlugin tipoPlugin,
			String ruolo, // applicativa/delegata o richiesta/risposta a seconda del tipo di plugin
			String fase,
			String nomeParametroSelezioneTipo,
			String nomeParametro, String label, String value, boolean hidden, Vector<DataElement> dati,
			boolean postBack_viaPOST) throws Exception {
		this.addCustomFieldConValoreDefault(tipoPlugin,
				ruolo,
				fase,
				nomeParametroSelezioneTipo,
				nomeParametro, label, 
				value, 
				hidden, dati,
				postBack_viaPOST, null);
	}
	
	public void addCustomFieldConValoreDefault(TipoPlugin tipoPlugin,
			String ruolo, // applicativa/delegata o richiesta/risposta a seconda del tipo di plugin
			String fase,
			String nomeParametroSelezioneTipo,
			String nomeParametro, String label, String value, boolean hidden, Vector<DataElement> dati,
			boolean postBack_viaPOST, String valoreDefault) throws Exception {
		addCustomField(tipoPlugin,
				ruolo,
				fase,
				nomeParametroSelezioneTipo,
				nomeParametro, label, 
				value, null, false, 
				hidden, dati,
				postBack_viaPOST, false, null, null, false, valoreDefault, null);
	}
	public void addCustomFieldConValoriDaEscludere(TipoPlugin tipoPlugin,
			String ruolo, // applicativa/delegata o richiesta/risposta a seconda del tipo di plugin
			String fase,
			String nomeParametroSelezioneTipo,
			String nomeParametro, String label, String value, boolean hidden, Vector<DataElement> dati,
			boolean postBack_viaPOST, List<String> listaValuesDaEscludere, String messaggioErroreValoriDisponibiliTerminati) throws Exception {
		this.addCustomFieldConValoriDaEscludereConValoreDefault(tipoPlugin,ruolo,fase,nomeParametroSelezioneTipo,nomeParametro, label,
				value, hidden, dati,postBack_viaPOST, listaValuesDaEscludere, messaggioErroreValoriDisponibiliTerminati, null);
	}
	public void addCustomFieldConValoriDaEscludereConValoreDefault(TipoPlugin tipoPlugin,
			String ruolo, // applicativa/delegata o richiesta/risposta a seconda del tipo di plugin
			String fase,
			String nomeParametroSelezioneTipo,
			String nomeParametro, String label, String value, boolean hidden, Vector<DataElement> dati,
			boolean postBack_viaPOST, List<String> listaValuesDaEscludere, String messaggioErroreValoriDisponibiliTerminati, String valoreDefault) throws Exception {
		addCustomField(tipoPlugin,
				ruolo,
				fase,
				nomeParametroSelezioneTipo,
				nomeParametro, label, 
				value, null, false, 
				hidden, dati,
				postBack_viaPOST, true, listaValuesDaEscludere, messaggioErroreValoriDisponibiliTerminati,false, valoreDefault, null);
	}
	
	public void addMultiSelectCustomField(TipoPlugin tipoPlugin,
			String ruolo, // applicativa/delegata o richiesta/risposta a seconda del tipo di plugin
			String fase,
			String nomeParametroSelezioneTipo,
			String nomeParametro, String label, String [] value, boolean hidden, Vector<DataElement> dati,
			boolean postBack_viaPOST) throws Exception {
		addMultiSelectCustomFieldConValoreDefault(tipoPlugin,
				ruolo,
				fase,
				nomeParametroSelezioneTipo,
				nomeParametro, label, 
				value, 
				hidden, dati,
				postBack_viaPOST, null);
	}
	
	public void addMultiSelectCustomFieldConValoreDefault(TipoPlugin tipoPlugin,
			String ruolo, // applicativa/delegata o richiesta/risposta a seconda del tipo di plugin
			String fase,
			String nomeParametroSelezioneTipo,
			String nomeParametro, String label, String [] value, boolean hidden, Vector<DataElement> dati,
			boolean postBack_viaPOST, String [] valoriDefault) throws Exception {
		addCustomField(tipoPlugin,
				ruolo,
				fase,
				nomeParametroSelezioneTipo,
				nomeParametro, label, 
				null, value, true, 
				hidden, dati,
				postBack_viaPOST, false, null, null, false, null, valoriDefault);
	}
	private void addCustomField(TipoPlugin tipoPlugin,
			String ruolo, // applicativa/delegata o richiesta/risposta a seconda del tipo di plugin (o anche configurazione per gli allarmi)
			String fase,
			String nomeParametroSelezioneTipo,
			String nomeParametro, String label, 
			String value, String [] multiValue, boolean multiSelect,
			boolean hidden, Vector<DataElement> dati,
			boolean postBack_viaPOST, boolean mostraSempreLabel, List<String> listaValuesDaEscludere, 
			String messaggioErroreValoriDisponibiliTerminati, boolean isSearch, String valoreDefault, String[] valoriDefault) throws Exception {
		
		List<String> values = new ArrayList<String>();
		List<String> labels = new ArrayList<String>();
		String note = null;
		
		if(	this.confCore.isConfigurazionePluginsEnabled() ) {
			Search ricerca = new Search(true);
			ricerca.addFilter(Liste.CONFIGURAZIONE_PLUGINS_CLASSI,  Filtri.FILTRO_TIPO_PLUGIN_CLASSI, tipoPlugin.toString());
			switch (tipoPlugin) {
			case AUTENTICAZIONE:
			case AUTORIZZAZIONE:
			case AUTORIZZAZIONE_CONTENUTI:
			case INTEGRAZIONE:
				if(ruolo!=null && !"".equals(ruolo)) {
					ricerca.addFilter(Liste.CONFIGURAZIONE_PLUGINS_CLASSI,  Filtri.FILTRO_RUOLO_NOME, ruolo);
				}
				break;
			case SERVICE_HANDLER:{
				if(fase!=null && !"".equals(fase)) {
					ricerca.addFilter(Liste.CONFIGURAZIONE_PLUGINS_CLASSI,  PluginCostanti.FILTRO_SERVICE_HANDLER_NOME, fase);
				}
				break;
			}
			case MESSAGE_HANDLER:{
				// message handler e ruolo
				if(ruolo!=null && !"".equals(ruolo)) {
					ricerca.addFilter(Liste.CONFIGURAZIONE_PLUGINS_CLASSI,  PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_NOME, ruolo);
				}
				if(fase!=null && !"".equals(fase)) {
					ricerca.addFilter(Liste.CONFIGURAZIONE_PLUGINS_CLASSI,  PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_NOME, fase);
				}
				break;
			}
			case ALLARME:
				if(ruolo!=null && !"".equals(ruolo)) {
					ricerca.addFilter(Liste.CONFIGURAZIONE_PLUGINS_CLASSI,  Filtri.FILTRO_APPLICABILITA_NOME, ruolo);
				}
				break;
			case BEHAVIOUR:
			case CONNETTORE:
			case RATE_LIMITING:
			case RICERCA:
			case STATISTICA:
			case TRANSAZIONE:
				break;
			}
			
			
			List<Plugin> listaTmp = this.confCore.pluginsClassiList(ricerca);
			boolean nessunValueDisponibile = false;
			if(listaTmp!=null && !listaTmp.isEmpty()) {
				if(listaValuesDaEscludere != null && !listaValuesDaEscludere.isEmpty()) {
					for(int i = listaTmp.size() -1; i >= 0 ; i--) {
						if(listaValuesDaEscludere.contains(listaTmp.get(i).getTipo())) {
							listaTmp.remove(i);
						}
					}
				}
				
				for (Plugin plugin : listaTmp) {
					if(plugin.isStato()) {
						
						if(values.isEmpty()) {
							// se uso il componente in una form di ricerca imposto il valore qualsiasi
							if(isSearch) {
								values.add(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SOGGETTO_QUALSIASI);
								labels.add(CostantiControlStation.LABEL_PARAMETRO_SOGGETTO_QUALSIASI);
							} else {
								values.add(CostantiControlStation.PARAMETRO_TIPO_PERSONALIZZATO_VALORE_UNDEFINED);
								labels.add(CostantiControlStation.PARAMETRO_TIPO_PERSONALIZZATO_LABEL_UNDEFINED);
							}
						}
						
						values.add(plugin.getTipo());
						labels.add(plugin.getLabel());
						
						if(value!=null) {
							if(plugin.getTipo().equals(value)) {
								note = plugin.getDescrizione();
								if(note!=null) {
									note = note.trim();
									if("".equals(note)) {
										note = null;
									}
								}
							}
						}
					}
				}
			} 
			
			if(listaTmp ==null || listaTmp.isEmpty()) {
				nessunValueDisponibile = true;
			}
			
			// se non ci sono valori disponibili e voglio comunicarlo all'utente imposto il messaggio di info
			if(messaggioErroreValoriDisponibiliTerminati != null && nessunValueDisponibile) {
				this.pd.setMessage(messaggioErroreValoriDisponibiliTerminati, Costanti.MESSAGE_TYPE_INFO);
				this.pd.disableEditMode();
			}
			
			if(values.size()==2) {
				// se c'e' solo un plugin elimino la non selezione
				values.remove(0);
				labels.remove(0);
			}
		}
			
		boolean customValidType = true;
		if(	this.confCore.isConfigurazionePluginsEnabled() ) {
		
			if(!multiSelect) {
				if(value!=null && !"".equals(value) && !CostantiControlStation.PARAMETRO_TIPO_PERSONALIZZATO_VALORE_UNDEFINED.equals(value)) {
					customValidType = values.contains(value); // backward compatibility
				}
				
				String postBackElementName = this.getPostBackElementName();
				if(nomeParametroSelezioneTipo.equals(postBackElementName) && !customValidType) {
					value = null;
					customValidType = true;
				}
			}
		}
		else {
			customValidType = false;
		}
		
		DataElement de = new DataElement();
		de.setName(nomeParametro);
		if(!multiSelect) {
			de.setValue(value);
		}
		if (hidden)
			de.setType(DataElementType.HIDDEN);
		else{
			de.setRequired(true);
			if(!customValidType) {
				de.setLabel(label);
				de.setType(DataElementType.TEXT_EDIT);
				de.setValoreDefault(valoreDefault);
			}
			else {
				if(multiSelect) {
					de.setLabel(label);
					de.setType(DataElementType.MULTI_SELECT);
				}
				else {
					de.setType(DataElementType.SELECT);
				}
				de.setValues(values);
				de.setLabels(labels);
				if(multiSelect) {
					de.setSelezionati(multiValue);
					de.setValoreDefaultMultiSelect(valoriDefault);
				}
				else {
					de.setSelected(value);
					de.setValoreDefaultSelect(valoreDefault);
				}
				de.setNote(note);
				if(postBack_viaPOST) {
					de.setPostBack_viaPOST(true);
				}
				else {
					de.setPostBack(true);
				}
				if(!multiSelect && values.size()==1) {
					de.setRequired(false);
				}
				if(mostraSempreLabel) {
					de.setLabel(label);
				}
			}
		}
		
		dati.addElement(de);
		
	}
	
	public void addIntegrazioneMetadatiToDati(Vector<DataElement> dati, String integrazioneStato, String integrazione,
			String[] integrazioneGruppi, List<GruppoIntegrazione> integrazioneGruppiDaVisualizzare,
			Map<String, List<String>> integrazioneGruppiValoriDeiGruppi, Vector<DataElement> deIntegrazione,
			boolean nascondiSezioneOpzioniAvanzate, boolean isPortaDelegata,
			ServiceBinding serviceBinding) throws Exception {
		String ruoloConfigurazione = isPortaDelegata ? Filtri.FILTRO_RUOLO_VALORE_FRUIZIONE : 	Filtri.FILTRO_RUOLO_VALORE_EROGAZIONE;
		
		DataElement de;
		// stato metadati
		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_METADATI);
		de.setValue(integrazioneStato);
		de.setName(CostantiControlStation.PARAMETRO_PORTE_INTEGRAZIONE_STATO);
		if(nascondiSezioneOpzioniAvanzate ){
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);
		} else {
			de.setType(DataElementType.SELECT);
			de.setValues(CostantiControlStation.VALUES_PARAMETRO_PORTE_INTEGRAZIONE_STATO);
			de.setLabels(CostantiControlStation.LABELS_PARAMETRO_PORTE_INTEGRAZIONE_STATO);
			de.setSelected(integrazioneStato);
			de.setPostBack(true);
			DataElementInfo dInfo = new DataElementInfo(CostantiControlStation.LABEL_METADATI);
			dInfo.setBody(CostantiControlStation.LABEL_METADATI_INFO);
			de.setInfo(dInfo);
			deIntegrazione.addElement(de);
		}
		
		if(nascondiSezioneOpzioniAvanzate ){
			de = new DataElement();
			de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_METADATI);
			de.setValue(integrazione);
			de.setName(CostantiControlStation.PARAMETRO_PORTE_INTEGRAZIONE);
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);
		}else{
			// valore del campo integrazione 
			if(integrazioneStato.equals(CostantiControlStation.VALUE_PARAMETRO_PORTE_INTEGRAZIONE_STATO_DISABILITATO)) {
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_METADATI);
				de.setValue(integrazione);
				de.setName(CostantiControlStation.PARAMETRO_PORTE_INTEGRAZIONE);
				de.setType(DataElementType.HIDDEN);
				deIntegrazione.addElement(de);
			} else if(integrazioneStato.equals(CostantiControlStation.VALUE_PARAMETRO_PORTE_INTEGRAZIONE_STATO_RIDEFINITO)) {
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_METADATI);
				de.setValue(integrazione);
				de.setName(CostantiControlStation.PARAMETRO_PORTE_INTEGRAZIONE);
				de.setType(DataElementType.HIDDEN);
				deIntegrazione.addElement(de);
				
				de = new DataElement();
				de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_METADATI_GRUPPO);
				de.setSelezionati(integrazioneGruppi);
				de.setName(CostantiControlStation.PARAMETRO_PORTE_METADATI_GRUPPO);
				de.setType(DataElementType.MULTI_SELECT);
				de.setValues(GruppoIntegrazione.toValues(serviceBinding));
				de.setLabels(GruppoIntegrazione.toLabels(true, serviceBinding));
				de.setPostBack_viaPOST(true);
				de.setRequired(true);
				DataElementInfo dInfo = new DataElementInfo(CostantiControlStation.LABEL_METADATI);
				switch (serviceBinding) {
				case REST:
					dInfo.setBody(CostantiControlStation.LABEL_METADATI_RIDEFINITI_INFO_REST);	
					break;
				case SOAP:
					dInfo.setBody(CostantiControlStation.LABEL_METADATI_RIDEFINITI_INFO_SOAP);	
					break;
				}
				de.setInfo(dInfo);
				deIntegrazione.addElement(de);
				
				// gruppi singoli
				boolean subtitleOp2 = false;
				boolean subtitleOp1 = false;
				for (GruppoIntegrazione group : integrazioneGruppiDaVisualizzare) {
					String [] valoriMulti = null;
					String valoreSingolo = null;
					List<String> listaValori = integrazioneGruppiValoriDeiGruppi.get(group.getValue());
					if(listaValori != null && listaValori.size() > 0) {
						valoriMulti = listaValori.toArray(new String[listaValori.size()]);
						if(!group.isMulti()) {
							valoreSingolo = valoriMulti[0];
						}
					}
					
					
					if(!group.getValue().equals(GruppoIntegrazione.PLUGIN.getValue())) {
						
						de = new DataElement();
												
						if(group.getValue().equals(GruppoIntegrazione.BACKWARD_COMPATIBILITY_OPENSPCOOP2_HTTP.getValue()) 
								||
							group.getValue().equals(GruppoIntegrazione.BACKWARD_COMPATIBILITY_OPENSPCOOP2_URL.getValue()) 
								||	
							group.getValue().equals(GruppoIntegrazione.BACKWARD_COMPATIBILITY_OPENSPCOOP2_SOAP.getValue()) ) {
							if(!subtitleOp2) {
								
								DataElement deSub = new DataElement();
								deSub.setLabel(CostantiControlStation.LABEL_METADATI_BACKWARD_COMPATIBILITY_OPENSPCOOP_2);
								deSub.setType(DataElementType.SUBTITLE);
								deIntegrazione.addElement(deSub);
								
								subtitleOp2 = true;
							}
							if(group.getValue().equals(GruppoIntegrazione.BACKWARD_COMPATIBILITY_OPENSPCOOP2_HTTP.getValue())) {
								de.setLabel(GruppoIntegrazione.HTTP.getCompactLabel());	
							}
							else if(group.getValue().equals(GruppoIntegrazione.BACKWARD_COMPATIBILITY_OPENSPCOOP2_URL.getValue())) {
								de.setLabel(GruppoIntegrazione.URL.getCompactLabel());	
							}
							else {
								de.setLabel(CostantiControlStation.LABEL_METADATI_BACKWARD_COMPATIBILITY_HEADER_SOAP);	
							}
						}
						else if(group.getValue().equals(GruppoIntegrazione.BACKWARD_COMPATIBILITY_OPENSPCOOP1_HTTP.getValue()) 
								||
							group.getValue().equals(GruppoIntegrazione.BACKWARD_COMPATIBILITY_OPENSPCOOP1_URL.getValue()) 
								||	
							group.getValue().equals(GruppoIntegrazione.BACKWARD_COMPATIBILITY_OPENSPCOOP1_SOAP.getValue()) ) {
							if(!subtitleOp1) {
								
								DataElement deSub = new DataElement();
								deSub.setLabel(CostantiControlStation.LABEL_METADATI_BACKWARD_COMPATIBILITY_OPENSPCOOP_1);
								deSub.setType(DataElementType.SUBTITLE);
								deIntegrazione.addElement(deSub);
								
								subtitleOp1 = true;
							}
							if(group.getValue().equals(GruppoIntegrazione.BACKWARD_COMPATIBILITY_OPENSPCOOP1_HTTP.getValue())) {
								de.setLabel(GruppoIntegrazione.HTTP.getCompactLabel());	
							}
							else if(group.getValue().equals(GruppoIntegrazione.BACKWARD_COMPATIBILITY_OPENSPCOOP1_URL.getValue())) {
								de.setLabel(GruppoIntegrazione.URL.getCompactLabel());	
							}
							else {
								de.setLabel(CostantiControlStation.LABEL_METADATI_BACKWARD_COMPATIBILITY_HEADER_SOAP);	
							}
						}
						else {
							de.setLabel(group.getCompactLabel());	
						}
						
						de.setName(CostantiControlStation.PARAMETRO_PORTE_METADATI_GRUPPO_SINGOLO+group.getValue());
						
						if(group.isMulti()) {
							de.setType(DataElementType.MULTI_SELECT);
							de.setSelezionati(valoriMulti);
						} else {
							de.setType(DataElementType.SELECT);
							de.setSelected(valoreSingolo);
						}
						de.setValues(TipoIntegrazione.toValues(group));
						de.setLabels(TipoIntegrazione.toLabels(group));
						de.setRequired(true);
						deIntegrazione.addElement(de);
					} else {
						if(group.isMulti()) {
							this.addMultiSelectCustomField(TipoPlugin.INTEGRAZIONE, ruoloConfigurazione, null, 
									CostantiControlStation.PARAMETRO_PORTE_METADATI_GRUPPO, 
									CostantiControlStation.PARAMETRO_PORTE_METADATI_GRUPPO_SINGOLO+group.getValue(), 
									GruppoIntegrazione.PLUGIN.getCompactLabel(), valoriMulti, false, deIntegrazione, false);
						} else {
							this.addCustomField(TipoPlugin.INTEGRAZIONE, ruoloConfigurazione, null, 
									CostantiControlStation.PARAMETRO_PORTE_METADATI_GRUPPO, 
									CostantiControlStation.PARAMETRO_PORTE_METADATI_GRUPPO_SINGOLO+group.getValue(), 
									GruppoIntegrazione.PLUGIN.getCompactLabel(), valoreSingolo, false, deIntegrazione, false);
						}
					}
				}
			} else {
				// quando e' default ci deve finire null					
			}
//			deIntegrazione.addElement(de);
		}
	}
	
	
	public boolean validaIntegrazioneMetadati() throws Exception {
		String integrazioneStato = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_INTEGRAZIONE_STATO);
				
		if(integrazioneStato!=null && !"".equals(integrazioneStato)){
			if(integrazioneStato.equals(CostantiControlStation.VALUE_PARAMETRO_PORTE_INTEGRAZIONE_STATO_RIDEFINITO)) {
				String[] integrazioneGruppi = this.getParameterValues(CostantiControlStation.PARAMETRO_PORTE_METADATI_GRUPPO);
				
				if(integrazioneGruppi == null || integrazioneGruppi.length == 0) {
					this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_PORTE_INTEGRAZIONE_GRUPPI_VUOTI);
					return false;
				}
				
				List<GruppoIntegrazione> integrazioneGruppiDaVisualizzare = new ArrayList<GruppoIntegrazione>();  
				
				for (String gruppoSelezionato : integrazioneGruppi) {
					integrazioneGruppiDaVisualizzare.add(GruppoIntegrazione.toEnumConstant(gruppoSelezionato));
				}
				
				// leggere i valori selezionati per ogni gruppo selezionato
				Map<String, List<String>> integrazioneGruppiValoriDeiGruppi = new HashMap<String, List<String>>();
				for (GruppoIntegrazione group : integrazioneGruppiDaVisualizzare) {
					List<String> valoriGruppoList = new ArrayList<String>();
					if(group.isMulti()) {
						String[] valoriGruppo = this.getParameterValues(CostantiControlStation.PARAMETRO_PORTE_METADATI_GRUPPO_SINGOLO+group.getValue());
						if(valoriGruppo == null || valoriGruppo.length == 0) {
							this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_PORTE_INTEGRAZIONE_GRUPPO_VUOTO, group.getCompactLabel()));
							return false;
						}
						
						valoriGruppoList.addAll(Arrays.asList(valoriGruppo));
					} else {
						String valoreGruppo = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_METADATI_GRUPPO_SINGOLO+group.getValue());
						if(valoreGruppo == null) {
							this.pd.setMessage(MessageFormat.format(CostantiControlStation.MESSAGGIO_ERRORE_PORTE_INTEGRAZIONE_GRUPPO_VUOTO, group.getCompactLabel()));
							return false;
						}
						valoriGruppoList.add(valoreGruppo);
					}
					
					integrazioneGruppiValoriDeiGruppi.put(group.getValue(), valoriGruppoList);	
				}
				
				// controllo sulla lunghezza finale del campo integrazione
				List<String> valoriFinaliIntegrazione = new ArrayList<String>();
				for (GruppoIntegrazione group : integrazioneGruppiDaVisualizzare) {
					valoriFinaliIntegrazione.addAll(integrazioneGruppiValoriDeiGruppi.get(group.getValue()));
				}
				
				String valoreFinaleCampoItegrazione = StringUtils.join(valoriFinaliIntegrazione.toArray(new String[valoriFinaliIntegrazione.size()]), ",");
				if(valoreFinaleCampoItegrazione!=null && valoreFinaleCampoItegrazione.length()>4000) {
					this.pd.setMessage("L'informazione fornita nel campo '"+CostantiControlStation.LABEL_PARAMETRO_PORTE_METADATI+"' deve possedere una lunghezza minore di 4000");
					return false;
				}
			}
		}
		
		return true;
	}
	
	public void visualizzaLinkHandlers(Vector<DataElement> dati, boolean isConfigurazione, TipoPdD ruoloPorta, Long idPorta, ServiceBinding serviceBinding) throws DriverConfigurazioneException {
		DataElement de;
		boolean contaListeFromSession = ServletUtils.getContaListeFromSession(this.session) != null ? ServletUtils.getContaListeFromSession(this.session) : false;
		
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_HANDLERS);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		List<Parameter> listaParametriComuni = new ArrayList<Parameter>();
		
		Parameter parRuoloPorta = null;
		if(ruoloPorta!=null) {
			parRuoloPorta = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_RUOLO_PORTA, ruoloPorta.getTipo());
			listaParametriComuni.add(parRuoloPorta);
		}
		Parameter parIdPorta = null;
		if(idPorta!=null) {
			parIdPorta = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_ID_PORTA, idPorta +"");
			listaParametriComuni.add(parIdPorta);
		}
		Parameter parServiceBinding = null;
		if(serviceBinding!=null) {
			parServiceBinding = new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_SERVICE_BINDING, serviceBinding.name());
			listaParametriComuni.add(parServiceBinding);
		}
		
		
		// handler richiesta
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_HANDLERS_RICHIESTA);
		de.setType(DataElementType.SUBTITLE);
		dati.addElement(de);
		
		for (int i = 0; i < PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORI_RICHIESTA.size(); i++) {
			String valueRichiesta = PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORI_RICHIESTA.get(i);
			String labelRichiesta = PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_LABEL_RICHIESTA.get(i);

			int numeroHandlersRichiesta = this.confCore.numeroHandlersRichiesta(valueRichiesta, ruoloPorta, idPorta);
			
			List<Parameter> listaParametriRichiesta = new ArrayList<Parameter>();
			if(!listaParametriComuni.isEmpty()) {
				listaParametriRichiesta.addAll(listaParametriComuni);
			}
			listaParametriRichiesta.add(new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_FASE, valueRichiesta));
			
			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_HANDLERS_RICHIESTA_LIST, listaParametriRichiesta.toArray(new Parameter [listaParametriRichiesta.size()]));
			if (contaListeFromSession)
				de.setValue(labelRichiesta +" (" + numeroHandlersRichiesta + ")");
			else
				de.setValue(labelRichiesta);
			dati.addElement(de);
		}
		
		// handler risposta
		de = new DataElement();
		de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_HANDLERS_RISPOSTA);
		de.setType(DataElementType.SUBTITLE);
		dati.addElement(de);
		
		for (int i = 0; i < PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORI_RISPOSTA.size(); i++) {
			String valueRisposta = PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORI_RISPOSTA.get(i);
			String labelRisposta = PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_LABEL_RISPOSTA.get(i);
			
			int numeroHandlersRisposta = this.confCore.numeroHandlersRisposta(valueRisposta, ruoloPorta, idPorta);
			
			List<Parameter> listaParametriRisposta = new ArrayList<Parameter>();
			if(!listaParametriComuni.isEmpty()) {
				listaParametriRisposta.addAll(listaParametriComuni);
			}
			listaParametriRisposta.add(new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_FASE, valueRisposta));
			
			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_HANDLERS_RISPOSTA_LIST, listaParametriRisposta.toArray(new Parameter [listaParametriRisposta.size()]));
			if (contaListeFromSession)
				de.setValue(labelRisposta+" (" + numeroHandlersRisposta + ")");
			else
				de.setValue(labelRisposta);
			dati.addElement(de);
		}
		
		if(isConfigurazione) {
			// service handler
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_HANDLERS_SERVIZIO);
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
			
			boolean integrationManagerEnabled = this.confCore.isIntegrationManagerEnabled();
			
			if(integrationManagerEnabled) {
				for (int i = 0; i < PluginCostanti.FILTRO_SERVICE_HANDLER_VALORI_CON_INTEGRATION_MANAGER.size(); i++) {
					String valueServizio = PluginCostanti.FILTRO_SERVICE_HANDLER_VALORI_CON_INTEGRATION_MANAGER.get(i);
					String labelServizio = PluginCostanti.FILTRO_SERVICE_HANDLER_LABEL_CON_INTEGRATION_MANAGER.get(i);
					
					int numeroHandlersServizio = this.confCore.numeroHandlersServizio(valueServizio);
					
					List<Parameter> listaParametriServizio = new ArrayList<Parameter>();
					if(!listaParametriComuni.isEmpty()) {
						listaParametriServizio.addAll(listaParametriComuni);
					}
					listaParametriServizio.add(new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_FASE, valueServizio));
					
					de = new DataElement();
					de.setType(DataElementType.LINK);
					de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_HANDLERS_SERVIZIO_LIST, listaParametriServizio.toArray(new Parameter [listaParametriServizio.size()]));
					if (contaListeFromSession)
						de.setValue(labelServizio +" (" + numeroHandlersServizio + ")");
					else
						de.setValue(labelServizio);
					dati.addElement(de);
				}
			} else {
				for (int i = 0; i < PluginCostanti.FILTRO_SERVICE_HANDLER_VALORI_SENZA_INTEGRATION_MANAGER.size(); i++) {
					String valueServizio = PluginCostanti.FILTRO_SERVICE_HANDLER_VALORI_SENZA_INTEGRATION_MANAGER.get(i);
					String labelServizio = PluginCostanti.FILTRO_SERVICE_HANDLER_LABEL_SENZA_INTEGRATION_MANAGER.get(i);
					
					int numeroHandlersServizio = this.confCore.numeroHandlersServizio(valueServizio);
					
					List<Parameter> listaParametriServizio = new ArrayList<Parameter>();
					if(!listaParametriComuni.isEmpty()) {
						listaParametriServizio.addAll(listaParametriComuni);
					}
					listaParametriServizio.add(new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_HANDLERS_FASE, valueServizio));
					
					de = new DataElement();
					de.setType(DataElementType.LINK);
					de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_HANDLERS_SERVIZIO_LIST, listaParametriServizio.toArray(new Parameter [listaParametriServizio.size()]));
					if (contaListeFromSession)
						de.setValue(labelServizio +" (" + numeroHandlersServizio + ")");
					else
						de.setValue(labelServizio);
					dati.addElement(de);
				}
			}
		}
	}
	
	public List<String> nomiProprietaPD(String protocolloPerFiltroProprieta , String soggettoPerFiltroProprieta) throws DriverRegistroServiziException, DriverConfigurazioneException {
		List<String> tipoServiziProtocollo = null;
		String filterSoggettoTipo = null;
		String filterSoggettoNome = null;
		if(soggettoPerFiltroProprieta != null) {
			if(soggettoPerFiltroProprieta!=null && !"".equals(soggettoPerFiltroProprieta)) {
				filterSoggettoTipo = soggettoPerFiltroProprieta.split("/")[0];
				filterSoggettoNome = soggettoPerFiltroProprieta.split("/")[1];
			}
		} else {
			try {
				tipoServiziProtocollo = Filtri.convertToTipiServizi(protocolloPerFiltroProprieta, null);
			}catch(Exception e) {
				throw new DriverRegistroServiziException(e.getMessage(),e);
			}
		}
		return this.porteDelegateCore.nomiProprietaPD(filterSoggettoTipo, filterSoggettoNome, tipoServiziProtocollo);
	}
	
	public List<String> nomiProprietaPA(String protocolloPerFiltroProprieta , String soggettoPerFiltroProprieta) throws DriverRegistroServiziException, DriverConfigurazioneException {
		List<String> tipoServiziProtocollo = null;
		String filterSoggettoTipo = null;
		String filterSoggettoNome = null;
		if(soggettoPerFiltroProprieta != null) {
			if(soggettoPerFiltroProprieta!=null && !"".equals(soggettoPerFiltroProprieta)) {
				filterSoggettoTipo = soggettoPerFiltroProprieta.split("/")[0];
				filterSoggettoNome = soggettoPerFiltroProprieta.split("/")[1];
			}
		} else {
			try {
				tipoServiziProtocollo = Filtri.convertToTipiServizi(protocolloPerFiltroProprieta, null);
			}catch(Exception e) {
				throw new DriverRegistroServiziException(e.getMessage(),e);
			}
		}
		return this.porteApplicativeCore.nomiProprietaPA(filterSoggettoTipo, filterSoggettoNome, tipoServiziProtocollo);
	}
	
	public List<String> nomiProprietaSA(String protocolloPerFiltroProprieta , String soggettoPerFiltroProprieta) throws DriverRegistroServiziException, DriverConfigurazioneException {
		List<String> tipoSoggettiProtocollo = null;
		String filterSoggettoTipo = null;
		String filterSoggettoNome = null;
		if(soggettoPerFiltroProprieta != null) {
			if(soggettoPerFiltroProprieta!=null && !"".equals(soggettoPerFiltroProprieta)) {
				filterSoggettoTipo = soggettoPerFiltroProprieta.split("/")[0];
				filterSoggettoNome = soggettoPerFiltroProprieta.split("/")[1];
			}
		} else {
			try {
				tipoSoggettiProtocollo = Filtri.convertToTipiSoggetti(protocolloPerFiltroProprieta, null);
			}catch(Exception e) {
				throw new DriverRegistroServiziException(e.getMessage(),e);
			}
		}
		return this.saCore.nomiProprietaSA(filterSoggettoTipo, filterSoggettoNome, tipoSoggettiProtocollo);
	}
	
	public List<String> nomiProprietaSoggetti(String protocolloPerFiltroProprieta) throws DriverRegistroServiziException, DriverConfigurazioneException {
		List<String> tipoSoggettiProtocollo = null;
		try {
			tipoSoggettiProtocollo = Filtri.convertToTipiSoggetti(protocolloPerFiltroProprieta, null);
		}catch(Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		return this.soggettiCore.nomiProprietaSoggetti(tipoSoggettiProtocollo);
	}
	
	private boolean hasAlmostOneFormElementDefined(List<DataElement> form_elements_to_check) {
		if(form_elements_to_check!=null) {
			for (DataElement de : form_elements_to_check) {
				// solo i tipi di elemento da controllare
				if(de.isElementoDaControllarePerCheckDefaultNelleForm()) {
					// valgono solo gli elementi con il default definito
					if(de.getValoreDefault() != null) {
						if(DataElementType.CHECKBOX.toString().equals(de.getType())) {
							if(ServletUtils.isCheckBoxEnabled(de.getSelected()) != de.getValoreDefaultCheckbox()) {
								return true;
							}
						} else if(DataElementType.SELECT.toString().equals(de.getType())) {
							if(!de.getValoreDefault().equals(de.getSelected())) {
								return true;
							}
						} else if(DataElementType.MULTI_SELECT.toString().equals(de.getType())) {
							if(!de.getValoreDefault().equals(de.getSelezionatiAsString()))
								return true;
						} else { // text, textarea, numerici e radio
							if(!de.getValoreDefault().equals(de.getValue())) {
								return true;
							}
						}
						
					}
				}
			}
		}
		return false;
	}
	
	public void impostaAperturaTitle(Vector<DataElement> dati, String titleName) throws Exception{
		this.impostaAperturaTitle(dati, titleName, DataElementType.TITLE, null, this.getPostBackElementName());
	}
	
	public void impostaAperturaTitle(Vector<DataElement> dati, String titleName, boolean visualizzaSottosezioneAperta) throws Exception{
		this.impostaAperturaTitle(dati, titleName, DataElementType.TITLE, visualizzaSottosezioneAperta, this.getPostBackElementName());
	}
	
	public void impostaAperturaSubTitle(Vector<DataElement> dati, String titleName) throws Exception{
		this.impostaAperturaTitle(dati, titleName, DataElementType.SUBTITLE, null, this.getPostBackElementName());
	}
	
	public void impostaAperturaSubTitle(Vector<DataElement> dati, String titleName, boolean visualizzaSottosezioneAperta) throws Exception{
		this.impostaAperturaTitle(dati, titleName, DataElementType.SUBTITLE, visualizzaSottosezioneAperta, this.getPostBackElementName());
	}
	
	private void impostaAperturaTitle(Vector<DataElement> dati, String titleName, DataElementType titleType, Boolean visualizzaSottosezioneAperta, String postbackElementName) {
		if(dati != null) {
			int idxSubtitle = -1;
			for (int i = 0; i < dati.size(); i++) {
				if(titleName.equals(dati.get(i).getName())) {
					idxSubtitle = i;
					break;
				}
			}
			
			if(visualizzaSottosezioneAperta == null) {
				// se ho trovato il subtitle allora prendo i filtri successivi
				// finche non trovo un altro subtitle o finisce la lista
				if(idxSubtitle > -1) {
					List<DataElement> filter_values_to_check = new ArrayList<DataElement>();
					
					for (int i = idxSubtitle + 1; i < dati.size(); i++) {
						DataElement de = dati.get(i);
						if(de.getType().equals(titleType.toString())) {
							// ho trovato un'altra sezione mi fermo
							break;
						} else {
							filter_values_to_check.add(de);
						}
					}
					visualizzaSottosezioneAperta = this.hasAlmostOneFormElementDefined(filter_values_to_check);
					
					// se c'e' stata una postback la sezione dell'elemento che ha provocato il reload deve restare aperta 
					if(postbackElementName != null) {
						for (int i = 0; i < filter_values_to_check.size(); i++) {
							if(filter_values_to_check.get(i).getName().equals(postbackElementName)) {
								visualizzaSottosezioneAperta = true;
								break;
							}
						}
					}
				}
			}
			
			dati.get(idxSubtitle).setStatoAperturaSezioni(visualizzaSottosezioneAperta ? STATO_APERTURA_SEZIONI.APERTO : STATO_APERTURA_SEZIONI.CHIUSO);
		}
	}
	
	public String formatInfoForView(String info) {
		if(info==null) {
			return null;
		}
		if(info.length()>this.core.getViewLunghezzaMassimaInformazione()) {
			return info.substring(0, this.core.getViewLunghezzaMassimaInformazione()) + " ...";
		}
		else {
			return info;
		}
	}
}
