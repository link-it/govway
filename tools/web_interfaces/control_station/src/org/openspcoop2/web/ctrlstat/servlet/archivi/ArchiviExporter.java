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


package org.openspcoop2.web.ctrlstat.servlet.archivi;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazioneWithSoggetto;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.archive.Archive;
import org.openspcoop2.protocol.sdk.archive.ArchiveCascadeConfiguration;
import org.openspcoop2.protocol.sdk.archive.ExportMode;
import org.openspcoop2.protocol.sdk.archive.IArchive;
import org.openspcoop2.protocol.sdk.archive.MappingModeTypesExtensions;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.utils.resources.HttpUtilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.ac.AccordiCooperazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
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
			
			String userLogin = ServletUtils.getUserLoginFromSession(session);
		
			// Sorgente su cui e' stato invocato l'export
			String servletSourceExport = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_TIPO);
			ArchiveType archiveType = ArchiveType.valueOf(servletSourceExport);
			
			
			// Elementi selezionati per l'export
			String objToExport = request.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE); 
			
			
			// Cascade
			String cascade = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE);
			String cascadePdd = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_PDD);
			String cascadeSoggetti = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_SOGGETTI);
			String cascadeServiziApplicativi = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_SERVIZI_APPLICATIVI);
			String cascadePorteDelegate = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_PORTE_DELEGATE);
			String cascadePorteApplicative = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_PORTE_APPLICATIVE);
			String cascadeAccordiCooperazione = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_COOPERAZIONE);
			String cascadeAccordiServizioComposto = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_COMPOSTO);
			String cascadeAccordiServizioParteComune = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_PARTE_COMUNE);
			String cascadeAccordiServizioParteSpecifica = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_PARTE_SPECIFICA);
			String cascadeFruizioni = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_FRUIZIONI);
			
			// TipoConfigurazione
			String tipoConfigurazione = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_TIPO_DUMP);
					
			// protocollo
			String protocollo = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO);
			IProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
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
			String exportMode = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPOLOGIA_ARCHIVIO);
			List<ExportMode> exportModes = archiveFactory.getExportModes(tipoEsportazione);
			ExportMode exportModeObject = null;
			for (ExportMode tmp : exportModes) {
				if(tmp.equals(exportMode)){
					exportModeObject = tmp;
					break;
				}
			}
			
			// extension
			MappingModeTypesExtensions mappingModeTypeExt = archiveFactory.getMappingTypesExtensions(exportModeObject);
			String extSingleArchive = mappingModeTypeExt.mappingArchiveTypeToExt(archiveType);
			if(extSingleArchive==null){
				extSingleArchive = mappingModeTypeExt.getExtensions().get(0); // prendo la prima estensione supportata 
			}
			String ext = mappingModeTypeExt.getExtensions().get(0); // prendo la prima estensione supportata
			
			// cascadeConfig
			ArchiveCascadeConfiguration cascadeConfig = new ArchiveCascadeConfiguration(false); // se non e' abilitato il cascade deve essere tutto false
			if(ServletUtils.isCheckBoxEnabled(cascade)){
				cascadeConfig = exportModeObject.getCascade();
				
				if(archiviCore.isExportArchivi_standard()==false){
					if(cascadePdd!=null & !"".equals(cascadePdd)){
						cascadeConfig.setCascadePdd(ServletUtils.isCheckBoxEnabled(cascadePdd));
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
			String fileName = null;
			switch (archiveType) {
			case SOGGETTO:
				identificativi = exporterUtils.getIdsSoggetti(objToExport);
				if(identificativi.size()>1){
					fileName = "Soggetti."+ext;
				}else{
					IDSoggetto idSoggetto = ((IDSoggetto)identificativi.get(0));
					fileName = idSoggetto.getTipo()+idSoggetto.getNome()+"."+extSingleArchive;
				}
				redirect = SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST;
				break;
			case ACCORDO_SERVIZIO_PARTE_COMUNE:
				identificativi = exporterUtils.getIdsAccordiServizioParteComune(objToExport);
				if(identificativi.size()>1){
					fileName = "AccordiServizioParteComune."+ext;
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
				redirect = AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
						AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO+"="+
						AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE;
				break;
			case ACCORDO_SERVIZIO_COMPOSTO:
				identificativi = exporterUtils.getIdsAccordiServizioComposti(objToExport);
				if(identificativi.size()>1){
					fileName = "AccordiServizioComposto."+ext;
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
				redirect = AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
						AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO+"="+
						AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_SERVIZIO_COMPOSTO;
				break;
			case ACCORDO_SERVIZIO_PARTE_SPECIFICA:
				identificativi = exporterUtils.getIdsAccordiServizioParteSpecifica(objToExport);
				if(identificativi.size()>1){
					fileName = "AccordiServizioParteSpecifica."+ext;
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
				redirect = AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST;
				break;
			case ACCORDO_COOPERAZIONE:
				identificativi = exporterUtils.getIdsAccordiCooperazione(objToExport);
				if(identificativi.size()>1){
					fileName = "AccordiCooperazione."+ext;
				}
				else{
					IDAccordoCooperazioneWithSoggetto idAccordo = ((IDAccordoCooperazioneWithSoggetto)identificativi.get(0));
					fileName = idAccordo.getNome();
					if(idAccordo.getSoggettoReferente()!=null){
						fileName+="_"+idAccordo.getSoggettoReferente().getTipo()+idAccordo.getSoggettoReferente().getNome();
	                }
	                if(idAccordo.getVersione()!=null && !"".equals(idAccordo.getVersione())){
	                	fileName+="_"+idAccordo.getVersione();
	                }
					fileName +="."+extSingleArchive;
				}
				redirect = AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST;
				break;
			default:
				// altri tipi che non prevedono la lista degli identificativi: e' la configurazione
				fileName = "ConfigurazioneOpenspcoop."+extSingleArchive;
				redirect = ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE;
			}
			
			
			// Costruisco l'archivio da esportare
			Archive archive = archiviCore.readArchiveForExport(userLogin, archiviHelper.smista(), 
					tipoEsportazione, identificativi, cascadeConfig);
					
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
	            response.sendRedirect(redirect);
			}
			else{
				throw new ServletException(Costanti.MESSAGGIO_SISTEMA_NON_DISPONIBILE);
			}
		}
		
	}

	

}
