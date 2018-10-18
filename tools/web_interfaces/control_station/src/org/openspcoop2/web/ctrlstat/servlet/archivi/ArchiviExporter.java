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

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
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
import org.openspcoop2.web.ctrlstat.servlet.login.LoginCostanti;
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
			String cascade = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE);
			String cascadePdd = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_PDD);
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
				identificativi = exporterUtils.getIdsAccordiServizioParteSpecifica(objToExport);
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
					fileName = idSoggetto.getTipo()+idSoggetto.getNome()+"."+extSingleArchive;
				}
				break;
			case ACCORDO_SERVIZIO_PARTE_COMUNE:
				if(identificativi.size()>1){
					fileName = prefix+"APIs."+ext;
				}
				else{
					IDAccordo idAccordo = ((IDAccordo)identificativi.get(0));
					fileName = idAccordo.getNome();
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
					fileName = idAccordo.getNome();
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
					fileName = idServizio.getTipo()+idServizio.getNome();
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
					fileName = idAccordo.getNome();
					if(idAccordo.getSoggettoReferente()!=null){
						fileName+="_"+idAccordo.getSoggettoReferente().getTipo()+idAccordo.getSoggettoReferente().getNome();
	                }
	                if(idAccordo.getVersione()!=null && !"".equals(idAccordo.getVersione())){
	                	fileName+="_"+idAccordo.getVersione();
	                }
					fileName +="."+extSingleArchive;
				}
				break;
			default:
				// altri tipi che non prevedono la lista degli identificativi: e' la configurazione
				fileName = prefix+"Config."+extSingleArchive;
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
