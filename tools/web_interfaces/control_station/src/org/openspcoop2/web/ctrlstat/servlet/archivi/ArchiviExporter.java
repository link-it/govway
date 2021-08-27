/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.archivi;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openspcoop2.core.allarmi.IdAllarme;
import org.openspcoop2.core.controllo_traffico.IdActivePolicy;
import org.openspcoop2.core.controllo_traffico.IdPolicy;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDGenericProperties;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.plugins.IdPlugin;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.archive.Archive;
import org.openspcoop2.protocol.sdk.archive.ArchiveCascadeConfiguration;
import org.openspcoop2.protocol.sdk.archive.ExportMode;
import org.openspcoop2.protocol.sdk.archive.IArchive;
import org.openspcoop2.protocol.sdk.archive.MappingModeTypesExtensions;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.ac.AccordiCooperazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.gruppi.GruppiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.login.LoginCostanti;
import org.openspcoop2.web.ctrlstat.servlet.ruoli.RuoliCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.scope.ScopeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * Questa servlet si occupa di esportare le informazioni di configurazione 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ArchiviExporter extends HttpServlet {

	private static final long serialVersionUID = -7341279067126334095L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.processRequest(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.processRequest(req, resp);
	}

	/**
	 * Processa la richiesta pervenuta e si occupa di fornire i dati richiesti
	 * in formato zip
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String redirect = null;
		try{
			
			HttpSession session = request.getSession(true);
			PageData pd = new PageData();
			ArchiviHelper archiviHelper = new ArchiviHelper(request, pd, session);
			ArchiviCore archiviCore = new ArchiviCore();
			SoggettiCore soggettiCore = new SoggettiCore(archiviCore);
			AccordiServizioParteSpecificaCore aspsCore = new AccordiServizioParteSpecificaCore(archiviCore);
			
			String userLogin = ServletUtils.getUserLoginFromSession(session);
		
			// Sorgente su cui e' stato invocato l'export
			String servletSourceExport = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_TIPO);
			ArchiveType archiveType = ArchiveType.valueOf(servletSourceExport);
			
			
			// Elementi selezionati per l'export
			String objToExport = archiviHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE); 
			
			
			// Cascade
			String cascadePolicyConfig = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_POLICY_CONFIG);
			String cascadePluginConfig = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_PLUGIN_CONFIG);
			String cascade = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE);
			String cascadePdd = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_PDD);
			String cascadeGruppi = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_GRUPPI);
			String cascadeRuoli = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_RUOLI);
			String cascadeScope = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_SCOPE);
			String cascadeSoggetti = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_SOGGETTI);
			String cascadeServiziApplicativi = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_SERVIZI_APPLICATIVI);
			String cascadePorteDelegate = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_PORTE_DELEGATE);
			String cascadePorteApplicative = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_PORTE_APPLICATIVE);
			String cascadeAccordiCooperazione = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_COOPERAZIONE);
			String cascadeAccordiServizioComposto = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_COMPOSTO);
			String cascadeAccordiServizioParteComune = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_PARTE_COMUNE);
			String cascadeAccordiServizioParteSpecifica = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_PARTE_SPECIFICA);
			String cascadeFruizioni = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_FRUIZIONI);
			
			// TipoConfigurazione
			String tipoConfigurazione = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_TIPO_DUMP);
					
			// protocollo
			String protocollo = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO);
			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			IArchive archiveFactory = protocolFactory.createArchive();
			
			// tipo esportazione
			ArchiveType tipoEsportazione = null;
			if(tipoConfigurazione!=null){
				// export da configurazione
				tipoEsportazione = ArchiveType.valueOf(tipoConfigurazione);
			}
			else{
				tipoEsportazione = archiveType;
			}
			
			// exportMode
			String exportMode = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPOLOGIA_ARCHIVIO);
			List<ExportMode> exportModes = archiveFactory.getExportModes(tipoEsportazione);
			ExportMode exportModeObject = null;
			for (ExportMode tmp : exportModes) {
				if(tmp.equals(exportMode)){
					exportModeObject = tmp;
					break;
				}
			}
						
			// cascadeConfig
			ArchiveCascadeConfiguration cascadeConfig = new ArchiveCascadeConfiguration(false); // se non e' abilitato il cascade deve essere tutto false
			if(ServletUtils.isCheckBoxEnabled(cascade)){
				cascadeConfig = exportModeObject.getCascade();
				
				if(archiviCore.isExportArchive_servizi_standard()==false){
					if(cascadePdd!=null & !"".equals(cascadePdd)){
						cascadeConfig.setCascadePdd(ServletUtils.isCheckBoxEnabled(cascadePdd));
					}
					
					if(cascadeGruppi!=null & !"".equals(cascadeGruppi)){
						cascadeConfig.setCascadeGruppi(ServletUtils.isCheckBoxEnabled(cascadeGruppi));
					}
					
					if(cascadeRuoli!=null & !"".equals(cascadeRuoli)){
						cascadeConfig.setCascadeRuoli(ServletUtils.isCheckBoxEnabled(cascadeRuoli));
					}
					
					if(cascadeScope!=null & !"".equals(cascadeScope)){
						cascadeConfig.setCascadeScope(ServletUtils.isCheckBoxEnabled(cascadeScope));
					}
					
					if(cascadeSoggetti!=null & !"".equals(cascadeSoggetti)){
						cascadeConfig.setCascadeSoggetti(ServletUtils.isCheckBoxEnabled(cascadeSoggetti));
					}
					
					if(cascadeServiziApplicativi!=null & !"".equals(cascadeServiziApplicativi)){
						cascadeConfig.setCascadeServiziApplicativi(ServletUtils.isCheckBoxEnabled(cascadeServiziApplicativi));
					}
					if(cascadePorteDelegate!=null & !"".equals(cascadePorteDelegate)){
						cascadeConfig.setCascadePorteDelegate(ServletUtils.isCheckBoxEnabled(cascadePorteDelegate));
					}
					if(cascadePorteApplicative!=null & !"".equals(cascadePorteApplicative)){
						cascadeConfig.setCascadePorteApplicative(ServletUtils.isCheckBoxEnabled(cascadePorteApplicative));
					}
					
					if(cascadeAccordiCooperazione!=null & !"".equals(cascadeAccordiCooperazione)){
						cascadeConfig.setCascadeAccordoCooperazione(ServletUtils.isCheckBoxEnabled(cascadeAccordiCooperazione));
					}
					if(cascadeAccordiServizioParteComune!=null & !"".equals(cascadeAccordiServizioParteComune)){
						cascadeConfig.setCascadeAccordoServizioParteComune(ServletUtils.isCheckBoxEnabled(cascadeAccordiServizioParteComune));
					}
					if(cascadeAccordiServizioComposto!=null & !"".equals(cascadeAccordiServizioComposto)){
						cascadeConfig.setCascadeAccordoServizioComposto(ServletUtils.isCheckBoxEnabled(cascadeAccordiServizioComposto));
					}
					if(cascadeAccordiServizioParteSpecifica!=null & !"".equals(cascadeAccordiServizioParteSpecifica)){
						cascadeConfig.setCascadeAccordoServizioParteSpecifica(ServletUtils.isCheckBoxEnabled(cascadeAccordiServizioParteSpecifica));
					}
					if(cascadeFruizioni!=null & !"".equals(cascadeFruizioni)){
						cascadeConfig.setCascadeFruizioni(ServletUtils.isCheckBoxEnabled(cascadeFruizioni));
					}
				}
			}
			cascadeConfig.setCascadePolicyConfigurazione(ServletUtils.isCheckBoxEnabled(cascadePolicyConfig));
			cascadeConfig.setCascadePluginConfigurazione(ServletUtils.isCheckBoxEnabled(cascadePluginConfig));
						
			
			// Recuperi eventuali identificativi logici degli oggetti
			ExporterUtils exporterUtils = new ExporterUtils(archiviCore);
			List<?> identificativi = null;
			switch (archiveType) {
			case SOGGETTO:
				identificativi = exporterUtils.getIdsSoggetti(objToExport);
				redirect = SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST;
				break;
			case ACCORDO_SERVIZIO_PARTE_COMUNE:
				identificativi = exporterUtils.getIdsAccordiServizioParteComune(objToExport);
				redirect = AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
						AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO+"="+
						AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE;
				break;
			case ACCORDO_SERVIZIO_COMPOSTO:
				identificativi = exporterUtils.getIdsAccordiServizioComposti(objToExport);
				redirect = AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
						AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO+"="+
						AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_SERVIZIO_COMPOSTO;
				break;
			case ACCORDO_SERVIZIO_PARTE_SPECIFICA:
			case EROGAZIONE:
			case FRUIZIONE:
				
				String tipologia = ServletUtils.getObjectFromSession(session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
				boolean gestioneFruitori = false;
				if(tipologia!=null) {
					if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
						gestioneFruitori = true;
					}
				}
				
				identificativi = exporterUtils.getIdsAccordiServizioParteSpecifica(objToExport, gestioneFruitori);
				Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, session);
				if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
					redirect = ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST;
				}
				else {
					redirect = AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST;
				}
				break;
			case ACCORDO_COOPERAZIONE:
				identificativi = exporterUtils.getIdsAccordiCooperazione(objToExport);
				redirect = AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST;
				break;
			case SERVIZIO_APPLICATIVO:
				identificativi = exporterUtils.getIdsServiziApplicativi(objToExport);
				redirect = ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST;
				break;
			case GRUPPO:
				identificativi = exporterUtils.getIdsGruppi(objToExport);
				redirect = GruppiCostanti.SERVLET_NAME_GRUPPI_LIST;
				break;
			case RUOLO:
				identificativi = exporterUtils.getIdsRuoli(objToExport);
				redirect = RuoliCostanti.SERVLET_NAME_RUOLI_LIST;
				break;
			case SCOPE:
				identificativi = exporterUtils.getIdsScope(objToExport);
				redirect = ScopeCostanti.SERVLET_NAME_SCOPE_LIST;
				break;
			case CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONFIG_POLICY:
				identificativi = exporterUtils.getIdsControlloTrafficoConfigPolicy(objToExport);
				redirect = ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONFIGURAZIONE_POLICY_LIST;
				break;
			case CONFIGURAZIONE_CONTROLLO_TRAFFICO_ACTIVE_POLICY:
				identificativi = exporterUtils.getIdsControlloTrafficoActivePolicy(objToExport);
				redirect = ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_LIST;
				break;
			case ALLARME:
				identificativi = exporterUtils.getIdsAllarmi(objToExport);
				redirect = ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_ALLARMI_LIST;
				break;
			case CONFIGURAZIONE_TOKEN_POLICY:
				identificativi = exporterUtils.getIdsTokenPolicy(objToExport);
				redirect = ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN_LIST+"?"+
						ConfigurazioneCostanti.PARAMETRO_TOKEN_POLICY_TIPOLOGIA_INFORMAZIONE+"="+ConfigurazioneCostanti.PARAMETRO_TOKEN_POLICY_TIPOLOGIA_INFORMAZIONE_VALORE_TOKEN;
				break;
			case CONFIGURAZIONE_ATTRIBUTE_AUTHORITY:
				identificativi = exporterUtils.getIdsAttributeAuthority(objToExport);
				redirect = ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_POLICY_GESTIONE_TOKEN_LIST+"?"+
						ConfigurazioneCostanti.PARAMETRO_TOKEN_POLICY_TIPOLOGIA_INFORMAZIONE+"="+ConfigurazioneCostanti.PARAMETRO_TOKEN_POLICY_TIPOLOGIA_INFORMAZIONE_VALORE_ATTRIBUTE_AUTHORITY;
				break;
			case CONFIGURAZIONE_PLUGIN_CLASSE:
				identificativi = exporterUtils.getIdsPluginClassi(objToExport);
				redirect = ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_PLUGINS_CLASSI_LIST;
				break;
			case CONFIGURAZIONE_PLUGIN_ARCHVIO:
				identificativi = exporterUtils.getIdsPluginArchivi(objToExport);
				redirect = ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_PLUGINS_ARCHIVI_LIST;
				break;
			case CONFIGURAZIONE_URL_INVOCAZIONE_REGOLA:
				identificativi = exporterUtils.getIdsUrlInvocazioneRegole(objToExport);
				redirect = ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_PROXY_PASS_REGOLA_LIST;
				break;
			default:
				// altri tipi che non prevedono la lista degli identificativi schermata di errore
				redirect = LoginCostanti.SERVLET_NAME_MESSAGE_PAGE;
			}
			
			
			// Costruisco l'archivio da esportare
			Archive archive = archiviCore.readArchiveForExport(userLogin, archiviHelper.smista(), 
					tipoEsportazione, identificativi, cascadeConfig);
					
			
			// Filtro per il protocollo attivo sulla console
			List<String> protocolli = archiviCore.getProtocolli(session);
			List<String> tipiSoggetti = new ArrayList<>();
			List<String> tipiServizi = new ArrayList<>();
			for (String protocolloUtente : protocolli) {
				tipiSoggetti.addAll(soggettiCore.getTipiSoggettiGestitiProtocollo(protocolloUtente));
				tipiServizi.addAll(aspsCore.getTipiServiziGestitiProtocollo(protocolloUtente,null));
			}
			exporterUtils.filterByProtocol(tipiSoggetti,tipiServizi,archive);
			
			// Filtro per il soggetto selezionato
			if(archiviHelper.isSoggettoMultitenantSelezionato()) {
				IDSoggetto idSoggettoSelezionato = archiviCore.convertSoggettoSelezionatoToID(archiviHelper.getSoggettoMultitenantSelezionato());
				exporterUtils.filterBySoggettoSelezionato(idSoggettoSelezionato, archive);
			}
			
			// extension
			MappingModeTypesExtensions mappingModeTypeExt = archiveFactory.getExportMappingTypesExtensions(archive, exportModeObject,
					archiviCore.getRegistryReader(protocolFactory),archiviCore.getConfigIntegrationReader(protocolFactory));
			String extSingleArchive = mappingModeTypeExt.getPreferExtSingleObject();
			if(extSingleArchive==null){
				extSingleArchive = mappingModeTypeExt.mappingArchiveTypeToExt(archiveType);
			}
			if(extSingleArchive==null){
				extSingleArchive = mappingModeTypeExt.getExtensions().get(0); // prendo la prima estensione supportata 
			}
			String ext = null;
			if(mappingModeTypeExt.getExtensions().size()==1 || (mappingModeTypeExt.getPreferExtSingleObject()==null)) {
				ext = mappingModeTypeExt.getExtensions().get(0); // prendo la prima estensione supportata
			}
			else {
				// prendo la prima estensione supportata escludento la preferita per la singola entita
				for (int i = 0; i < mappingModeTypeExt.getExtensions().size(); i++) {
					String extTmp = mappingModeTypeExt.getExtensions().get(i);
					if(extTmp.equals(mappingModeTypeExt.getPreferExtSingleObject())==false) {
						ext = extTmp;
						break;
					}
				}
			}
			
			
			// Costruisco file name da esportare
			String prefix = "GovWay";
			String fileName = null;
			switch (archiveType) {
			case SOGGETTO:
				if(identificativi.size()>1){
					fileName = prefix+"Soggetti."+ext;
				}else{
					IDSoggetto idSoggetto = ((IDSoggetto)identificativi.get(0));
					fileName = prefix+"Soggetto_"+idSoggetto.getTipo()+idSoggetto.getNome()+"."+extSingleArchive;
				}
				break;
			case ACCORDO_SERVIZIO_PARTE_COMUNE:
				if(identificativi.size()>1){
					fileName = prefix+"APIs."+ext;
				}
				else{
					IDAccordo idAccordo = ((IDAccordo)identificativi.get(0));
					fileName = prefix+"API_"+idAccordo.getNome();
					if(idAccordo.getSoggettoReferente()!=null){
						fileName+="_"+idAccordo.getSoggettoReferente().getTipo()+idAccordo.getSoggettoReferente().getNome();
	                }
	                if(idAccordo.getVersione()!=null && !"".equals(idAccordo.getVersione())){
	                	fileName+="_"+idAccordo.getVersione();
	                }
					fileName +="."+extSingleArchive;
				}
				break;
			case ACCORDO_SERVIZIO_COMPOSTO:
				if(identificativi.size()>1){
					fileName = prefix+"AccordiServizioComposto."+ext;
				}
				else{
					IDAccordo idAccordo = ((IDAccordo)identificativi.get(0));
					fileName = prefix+"AccordiServizioComposto_"+idAccordo.getNome();
					if(idAccordo.getSoggettoReferente()!=null){
						fileName+="_"+idAccordo.getSoggettoReferente().getTipo()+idAccordo.getSoggettoReferente().getNome();
	                }
	                if(idAccordo.getVersione()!=null && !"".equals(idAccordo.getVersione())){
	                	fileName+="_"+idAccordo.getVersione();
	                }
					fileName +="."+extSingleArchive;
				}
				break;
			case ACCORDO_SERVIZIO_PARTE_SPECIFICA:
				if(identificativi.size()>1){
					fileName = prefix+"Servizi."+ext;
				}
				else{
					IDServizio idServizio = ((IDServizio)identificativi.get(0));
					fileName = prefix+"Servizio_"+idServizio.getTipo()+idServizio.getNome();
					fileName+="_"+idServizio.getSoggettoErogatore().getTipo()+idServizio.getSoggettoErogatore().getNome();
	                if(idServizio.getVersione()!=null && !"".equals(idServizio.getVersione())){
	                	fileName+="_"+idServizio.getVersione();
	                }
					fileName +="."+extSingleArchive;
				}
				break;
			case ACCORDO_COOPERAZIONE:
				if(identificativi.size()>1){
					fileName = prefix+"AccordiCooperazione."+ext;
				}
				else{
					IDAccordoCooperazione idAccordo = ((IDAccordoCooperazione)identificativi.get(0));
					fileName = prefix+"AccordiCooperazione_"+idAccordo.getNome();
					if(idAccordo.getSoggettoReferente()!=null){
						fileName+="_"+idAccordo.getSoggettoReferente().getTipo()+idAccordo.getSoggettoReferente().getNome();
	                }
	                if(idAccordo.getVersione()!=null && !"".equals(idAccordo.getVersione())){
	                	fileName+="_"+idAccordo.getVersione();
	                }
					fileName +="."+extSingleArchive;
				}
				break;
			case EROGAZIONE:
				if(identificativi.size()>1){
					fileName = prefix+"Erogazioni."+ext;
				}
				else{
					IDServizio idServizio = ((IDServizio)identificativi.get(0));
					fileName = prefix+"Erogazione_"+idServizio.getTipo()+idServizio.getNome();
					fileName+="_"+idServizio.getSoggettoErogatore().getTipo()+idServizio.getSoggettoErogatore().getNome();
	                if(idServizio.getVersione()!=null && !"".equals(idServizio.getVersione())){
	                	fileName+="_"+idServizio.getVersione();
	                }
					fileName +="."+extSingleArchive;
				}
				break;
			case FRUIZIONE:
				if(identificativi.size()>1){
					fileName = prefix+"Fruizioni."+ext;
				}
				else{
					IDFruizione idFruizione = ((IDFruizione)identificativi.get(0));
					IDServizio idServizio = idFruizione.getIdServizio();
					fileName = prefix+"Fruizione_"+idFruizione.getIdFruitore().getTipo()+idFruizione.getIdFruitore().getNome();
					fileName+="_"+idServizio.getTipo()+idServizio.getNome();
					fileName+="_"+idServizio.getSoggettoErogatore().getTipo()+idServizio.getSoggettoErogatore().getNome();
	                if(idServizio.getVersione()!=null && !"".equals(idServizio.getVersione())){
	                	fileName+="_"+idServizio.getVersione();
	                }
					fileName +="."+extSingleArchive;
				}
				break;
			case SERVIZIO_APPLICATIVO:
				if(identificativi.size()>1){
					fileName = prefix+"Applicativi."+ext;
				}else{
					IDServizioApplicativo idServizioApplicativo = ((IDServizioApplicativo)identificativi.get(0));
					fileName = prefix+"Applicativo_"+idServizioApplicativo.getIdSoggettoProprietario().getTipo()+idServizioApplicativo.getIdSoggettoProprietario().getNome()+
							"_"+
							idServizioApplicativo.getNome()+
							"."+extSingleArchive;
				}
				break;
			case GRUPPO:
				if(identificativi.size()>1){
					fileName = prefix+"Tags."+ext;
				}else{
					IDGruppo idGruppo = ((IDGruppo)identificativi.get(0));
					fileName = prefix+"Tag_"+idGruppo.getNome()+"."+extSingleArchive;
				}
				break;
			case RUOLO:
				if(identificativi.size()>1){
					fileName = prefix+"Ruoli."+ext;
				}else{
					IDRuolo idRuolo = ((IDRuolo)identificativi.get(0));
					fileName = prefix+"Ruolo_"+idRuolo.getNome()+"."+extSingleArchive;
				}
				break;
			case SCOPE:
				if(identificativi.size()>1){
					fileName = prefix+"Scope."+ext;
				}else{
					IDScope idScope = ((IDScope)identificativi.get(0));
					fileName = prefix+"Scope_"+idScope.getNome()+"."+extSingleArchive;
				}
				break;
			case CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONFIG_POLICY:
				if(identificativi.size()>1){
					fileName = prefix+"ControlloTrafficoPolicy."+ext;
				}else{
					IdPolicy idPolicy = ((IdPolicy)identificativi.get(0));
					fileName = prefix+"ControlloTrafficoPolicy_"+idPolicy.getNome()+"."+extSingleArchive;
				}
				break;
			case CONFIGURAZIONE_CONTROLLO_TRAFFICO_ACTIVE_POLICY:
				if(identificativi.size()>1){
					fileName = prefix+"RateLimitingPolicy."+ext;
				}else{
					IdActivePolicy idActivePolicy = ((IdActivePolicy)identificativi.get(0));
					fileName = prefix+"RateLimitingPolicy_"+idActivePolicy.getAlias()+"."+extSingleArchive;
				}
				break;
			case ALLARME:
				if(identificativi.size()>1){
					fileName = prefix+"Allarmi."+ext;
				}else{
					IdAllarme idAllarme = ((IdAllarme)identificativi.get(0));
					fileName = prefix+"Allarme_"+idAllarme.getAlias()+"."+extSingleArchive;
				}
				break;
			case CONFIGURAZIONE_TOKEN_POLICY:
				if(identificativi.size()>1){
					fileName = prefix+"TokenPolicy."+ext;
				}else{
					IDGenericProperties idPolicy = ((IDGenericProperties)identificativi.get(0));
					fileName = prefix+"TokenPolicy_"+idPolicy.getTipologia()+"_"+idPolicy.getNome()+"."+extSingleArchive;
				}
				break;
			case CONFIGURAZIONE_ATTRIBUTE_AUTHORITY:
				if(identificativi.size()>1){
					fileName = prefix+"AttributeAuthority."+ext;
				}else{
					IDGenericProperties idPolicy = ((IDGenericProperties)identificativi.get(0));
					fileName = prefix+"AttributeAuthority_"+
							//idPolicy.getTipologia()+"_"+
							idPolicy.getNome()+"."+extSingleArchive;
				}
				break;
			case CONFIGURAZIONE_PLUGIN_CLASSE:
				if(identificativi.size()>1){
					fileName = prefix+"PluginClassi."+ext;
				}else{
					IdPlugin idPlugin = ((IdPlugin)identificativi.get(0));
					fileName = prefix+"Plugin_"+idPlugin.getTipoPlugin()+"_"+idPlugin.getTipo()+"."+extSingleArchive;
				}
				break;
			case CONFIGURAZIONE_PLUGIN_ARCHVIO:
				if(identificativi.size()>1){
					fileName = prefix+"PluginArchivi."+ext;
				}else{
					String idPlugin = ((String)identificativi.get(0));
					fileName = prefix+"ArchivioPlugins_"+idPlugin+"."+extSingleArchive;
				}
				break;
			case CONFIGURAZIONE_URL_INVOCAZIONE_REGOLA:
				if(identificativi.size()>1){
					fileName = prefix+"RegoleProxyPass."+ext;
				}else{
					String idRegola = ((String)identificativi.get(0));
					fileName = prefix+"RegolaProxyPass_"+idRegola+"."+extSingleArchive;
				}
				break;
			case CONFIGURAZIONE_URL_INVOCAZIONE:
				fileName = prefix+"ConfigurazioneUrlInvocazione."+extSingleArchive;
				break;
			default:
				// altri tipi che non prevedono la lista degli identificativi: e' la configurazione
				fileName = prefix+"Configurazione."+extSingleArchive;
			}
			
			// Setto Proprietà Export File
			HttpUtilities.setOutputFile(response, true, fileName);
								
			// export
			OutputStream out = response.getOutputStream();
			// archiviCore.export(userLogin, archiviHelper.smista(), protocollo, archive, out, exportModeObject);
			// Devo far serializzare prima tutto in memoria, altrimenti poi non mi accorgo di eventuali errori di un singolo archivio durante l'export
			out.write(archiviCore.export(userLogin, archiviHelper.smista(), protocollo, archive, exportModeObject));
			
		}catch(Exception e){
			ControlStationCore.logError("Errore durante l'esportazione dell'archivio: "+e.getMessage(), e);
			
			if(redirect!=null){
	            String msg = e.getMessage();
	            if(redirect.contains("?"))
	            	redirect+="&"+Costanti.PARAMETER_NAME_MSG_ERROR_EXPORT+"='"+msg+"'";
	            else
	            	redirect+="?"+Costanti.PARAMETER_NAME_MSG_ERROR_EXPORT+"='"+msg+"'";
	            
	            // parametro necessario per la servlet generica di messaggi errore
	            redirect+="&"+Costanti.PARAMETER_MESSAGE_TEXT+"='"+msg+"'";
	            redirect+="&"+Costanti.PARAMETER_MESSAGE_BREADCRUMB+"="+ArchiviCostanti.LABEL_ARCHIVI_EXPORT+"";
	            
	            response.sendRedirect(redirect);
			}
			else{
				response.sendRedirect(LoginCostanti.SERVLET_NAME_MESSAGE_PAGE);
//				throw new ServletException(Costanti.MESSAGGIO_SISTEMA_NON_DISPONIBILE);
			}
		}
		
	}

	

}
