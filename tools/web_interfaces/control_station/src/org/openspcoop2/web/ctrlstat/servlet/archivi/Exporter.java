/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazioneWithSoggetto;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.archive.ArchiveCascadeConfiguration;
import org.openspcoop2.protocol.sdk.archive.ExportMode;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.ac.AccordiCooperazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * Exporter
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class Exporter extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			
			//String userLogin = ServletUtils.getUserLoginFromSession(session);
			
			ArchiviHelper archiviHelper = new ArchiviHelper(request, pd, session);
			ArchiviCore archiviCore = new ArchiviCore();
			SoggettiCore soggettiCore = new SoggettiCore(archiviCore);
		
			ExporterUtils exporterUtils = new ExporterUtils(archiviCore);
			
			
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
			
			
			// Recuperi eventuali identificativi logici degli oggetti
			Parameter provenienza = null;
			List<?> identificativi = null;
			List<String> protocolli = new ArrayList<String>();
			switch (archiveType) {
			case SOGGETTO:
				provenienza = new Parameter(SoggettiCostanti.LABEL_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST);
				identificativi = exporterUtils.getIdsSoggetti(objToExport);
				for (Object id : identificativi) {
					IDSoggetto idSoggetto = (IDSoggetto) id;
					String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(idSoggetto.getTipo());
					if(protocolli.contains(protocollo)==false){
						protocolli.add(protocollo);
					}
				}
				break;
			case ACCORDO_SERVIZIO_PARTE_COMUNE:
				provenienza = new Parameter(AccordiServizioParteComuneCostanti.LABEL_APC, AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST,
						new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO, AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE));
				identificativi = exporterUtils.getIdsAccordiServizioParteComune(objToExport);
				for (Object id : identificativi) {
					IDAccordo idAccordo = (IDAccordo) id;
					String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(idAccordo.getSoggettoReferente().getTipo());
					if(protocolli.contains(protocollo)==false){
						protocolli.add(protocollo);
					}
				}
				break;
			case ACCORDO_SERVIZIO_COMPOSTO:
				provenienza = new Parameter(AccordiServizioParteComuneCostanti.LABEL_ASC, AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST,
						new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO, AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_SERVIZIO_COMPOSTO));
				identificativi = exporterUtils.getIdsAccordiServizioComposti(objToExport);
				for (Object id : identificativi) {
					IDAccordo idAccordo = (IDAccordo) id;
					String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(idAccordo.getSoggettoReferente().getTipo());
					if(protocolli.contains(protocollo)==false){
						protocolli.add(protocollo);
					}
				}
				break;
			case ACCORDO_SERVIZIO_PARTE_SPECIFICA:
				provenienza = new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST);
				identificativi = exporterUtils.getIdsAccordiServizioParteSpecifica(objToExport);
				for (Object id : identificativi) {
					IDAccordo idAccordo = (IDAccordo) id;
					String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(idAccordo.getSoggettoReferente().getTipo());
					if(protocolli.contains(protocollo)==false){
						protocolli.add(protocollo);
					}
				}
				break;
			case ACCORDO_COOPERAZIONE:
				provenienza = new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE, AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST);
				identificativi = exporterUtils.getIdsAccordiCooperazione(objToExport);
				for (Object id : identificativi) {
					IDAccordoCooperazioneWithSoggetto idAccordo = (IDAccordoCooperazioneWithSoggetto) id;
					String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(idAccordo.getSoggettoReferente().getTipo());
					if(protocolli.contains(protocollo)==false){
						protocolli.add(protocollo);
					}
				}
				break;
			case CONFIGURAZIONE:
				provenienza = new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE, null);
				protocolli = archiviCore.getProtocolli();
				if(tipoConfigurazione==null){
					tipoConfigurazione = ArchiveType.ALL.toString();
				}
				break;
			default:
				throw new Exception("Archive type ["+servletSourceExport+"] non supportato");
			}
			
			
			
			// Eventuale PostBack element name che ha scaturito l'evento
			String postBackElementName = ServletUtils.getPostBackElementName(request);
			

					
			// protocolli disponibili compatibili con la sorgente degli oggetti selezionati
//			if(protocolli.size()>1){
//				protocolli = new ArrayList<String>();
//				protocolli.add(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO_UNDEFINDED);
//				protocolli.addAll(archiviCore.getProtocolli());
//			}
//			this.protocollo = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO);
//			if("".equals(this.protocollo) || ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO_UNDEFINDED.equals(this.protocollo)){
//				this.protocollo = null;
//			}
			// Volutamente per default si vogliono tutti!
//			if(this.protocollo==null){
//				this.protocollo = archiviCore.getProtocolloDefault();
//			}
			String protocollo = protocolli.get(0); // tanto poi selezionero' un export modes compatibile con tutti i protocolli presenti.
			
			
			// export modes (compatibile con tutti i protocolli degli oggetti selezionati)
			List<ExportMode> exportModes = exporterUtils.getExportModesCompatibleWithAllProtocol(protocolli, archiveType);
			boolean errore = false;
			String motivoErrore = null;
			if(exportModes.size()<1){
				errore = true;
				if(protocolli.size() > 1)
					motivoErrore = "E' stata richiesta una export di oggetti appartenenti a protocolli ("+protocolli.toString()+") che non prevedono il medesimo formato di esportazione";
				else 
					motivoErrore = "E' stata richiesta una export di oggetti appartenenti a protocolli ("+protocolli.toString()+") che non prevedono un formato di esportazione";
			}
			String exportMode = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPOLOGIA_ARCHIVIO);
			if(exportMode!=null){
				// verifico che esista nei modes disponibili per i protocolli selezionati
				if(exportModes.contains(new ExportMode(exportMode))==false){
					exportMode = null;
				}
			}
			if(exportMode==null && !errore){
				if(exportModes.contains(org.openspcoop2.protocol.basic.Costanti.OPENSPCOOP_EXPORT_ARCHIVE_MODE)){
					exportMode = org.openspcoop2.protocol.basic.Costanti.OPENSPCOOP_EXPORT_ARCHIVE_MODE.toString();
				}else{
					exportMode = exportModes.get(0).toString();
				}
			}
			
			
		

			
			
			
			// Controllo se ho terminato di impostare le proprieta' e devo procedere con l'esportazione.
			boolean fineSceltaOpzioniEsportazione = !ServletUtils.isEditModeInProgress(request)  &&
					(postBackElementName==null || "".equals(postBackElementName));
			boolean configurationEnabled = ArchiveType.CONFIGURAZIONE.equals(archiveType) && !archiviCore.isExportArchivi_standard();
			boolean cascadeEnabled = !ArchiveType.CONFIGURAZIONE.equals(archiveType) && archiviCore.isCascadeEnabled(exportModes, exportMode);
			boolean nonSonoPresentiScelteEsportazione = (exportModes.size()==1) && !cascadeEnabled && !configurationEnabled;
			if( !errore && 
					( fineSceltaOpzioniEsportazione ||  nonSonoPresentiScelteEsportazione ) 
				) {
				
				String send = ArchiviCostanti.SERVLET_NAME_PACKAGE_EXPORT+"?"+
						ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_TIPO+"="+servletSourceExport;
				if(objToExport!=null && !"".equals(objToExport)){
					send = send + "&" + Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE+"="+ objToExport;
				}
				if(protocollo!=null && !"".equals(protocollo)){
					send = send + "&" + ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO+"="+ protocollo;
				}
				if(exportMode!=null && !"".equals(exportMode)){
					send = send + "&" + ArchiviCostanti.PARAMETRO_ARCHIVI_TIPOLOGIA_ARCHIVIO+"="+ exportMode;
				}
				if(tipoConfigurazione!=null && !"".equals(tipoConfigurazione)){
					send = send + "&" + ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_TIPO_DUMP+"="+ tipoConfigurazione;
				}
				if(cascadeEnabled){
					send = send + "&" + ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE+"="+ (ServletUtils.isCheckBoxEnabled(cascade)?Costanti.CHECK_BOX_ENABLED:Costanti.CHECK_BOX_DISABLED);
					ArchiveCascadeConfiguration cascadeConfig = archiviCore.getCascadeConfig(exportModes, exportMode);
					if(cascadeConfig.isCascadePdd()){
						send = send + "&" + ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_PDD+"="+ (ServletUtils.isCheckBoxEnabled(cascadePdd)?Costanti.CHECK_BOX_ENABLED:Costanti.CHECK_BOX_DISABLED);
					}
					if(cascadeConfig.isCascadeSoggetti()){
						send = send + "&" + ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_SOGGETTI+"="+ (ServletUtils.isCheckBoxEnabled(cascadeSoggetti)?Costanti.CHECK_BOX_ENABLED:Costanti.CHECK_BOX_DISABLED);
					}
					if(cascadeConfig.isCascadeServiziApplicativi()){
						send = send + "&" + ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_SERVIZI_APPLICATIVI+"="+ (ServletUtils.isCheckBoxEnabled(cascadeServiziApplicativi)?Costanti.CHECK_BOX_ENABLED:Costanti.CHECK_BOX_DISABLED);
					}
					if(cascadeConfig.isCascadePorteDelegate()){
						send = send + "&" + ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_PORTE_DELEGATE+"="+ (ServletUtils.isCheckBoxEnabled(cascadePorteDelegate)?Costanti.CHECK_BOX_ENABLED:Costanti.CHECK_BOX_DISABLED);
					}
					if(cascadeConfig.isCascadePorteApplicative()){
						send = send + "&" + ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_PORTE_APPLICATIVE+"="+ (ServletUtils.isCheckBoxEnabled(cascadePorteApplicative)?Costanti.CHECK_BOX_ENABLED:Costanti.CHECK_BOX_DISABLED);
					}
					if(cascadeConfig.isCascadeAccordoCooperazione()){
						send = send + "&" + ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_COOPERAZIONE+"="+ (ServletUtils.isCheckBoxEnabled(cascadeAccordiCooperazione)?Costanti.CHECK_BOX_ENABLED:Costanti.CHECK_BOX_DISABLED);
					}
					if(cascadeConfig.isCascadeAccordoServizioParteComune()){
						send = send + "&" + ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_PARTE_COMUNE+"="+ (ServletUtils.isCheckBoxEnabled(cascadeAccordiServizioParteComune)?Costanti.CHECK_BOX_ENABLED:Costanti.CHECK_BOX_DISABLED);
					}
					if(cascadeConfig.isCascadeAccordoServizioComposto()){
						send = send + "&" + ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_COMPOSTO+"="+ (ServletUtils.isCheckBoxEnabled(cascadeAccordiServizioComposto)?Costanti.CHECK_BOX_ENABLED:Costanti.CHECK_BOX_DISABLED);
					}
					if(cascadeConfig.isCascadeAccordoServizioParteSpecifica()){
						send = send + "&" + ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_PARTE_SPECIFICA+"="+ (ServletUtils.isCheckBoxEnabled(cascadeAccordiServizioParteSpecifica)?Costanti.CHECK_BOX_ENABLED:Costanti.CHECK_BOX_DISABLED);
					}
					if(cascadeConfig.isCascadeFruizioni()){
						send = send + "&" + ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_FRUIZIONI+"="+ (ServletUtils.isCheckBoxEnabled(cascadeFruizioni)?Costanti.CHECK_BOX_ENABLED:Costanti.CHECK_BOX_DISABLED);
					}
				}
				//System.out.println("invoke EXPORT ["+send+"]....");
				response.sendRedirect(send);
				
			}
			
			
			
			// Visualizzo form delle opzioni	
				
			archiviHelper.makeMenu();
			
			
			// setto la barra del titolo			
			ServletUtils.setPageDataTitle(pd, 
					provenienza,
					new Parameter(ArchiviCostanti.LABEL_ARCHIVI_EXPORT,null));
		
			Vector<DataElement> dati = new Vector<DataElement>();
			
			// imposto grafica
			dati.addElement(ServletUtils.getDataElementForEditModeFinished());

			// init cascade
			if(ServletUtils.isCheckBoxEnabled(cascade) 
					&&
					(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE.equals(postBackElementName) || ArchiviCostanti.PARAMETRO_ARCHIVI_TIPOLOGIA_ARCHIVIO.equals(postBackElementName)) ){
				cascadePdd = Costanti.CHECK_BOX_ENABLED;
				cascadeSoggetti = Costanti.CHECK_BOX_ENABLED;
				cascadeServiziApplicativi = Costanti.CHECK_BOX_ENABLED;
				cascadePorteDelegate = Costanti.CHECK_BOX_ENABLED;
				cascadePorteApplicative = Costanti.CHECK_BOX_ENABLED;
				cascadeAccordiCooperazione = Costanti.CHECK_BOX_ENABLED;
				cascadeAccordiServizioComposto = Costanti.CHECK_BOX_ENABLED;
				cascadeAccordiServizioParteComune = Costanti.CHECK_BOX_ENABLED;
				cascadeAccordiServizioParteSpecifica = Costanti.CHECK_BOX_ENABLED;
				cascadeFruizioni = Costanti.CHECK_BOX_ENABLED;
			}
			
			archiviHelper.addExportToDati(dati, protocolli, protocollo, 
					exportModes, exportMode, 
					archiveType, objToExport,
					cascade,tipoConfigurazione,
					cascadePdd,cascadeSoggetti,
					cascadeServiziApplicativi, cascadePorteDelegate, cascadePorteApplicative,
					cascadeAccordiCooperazione, cascadeAccordiServizioParteComune, 
					cascadeAccordiServizioComposto, 
					cascadeAccordiServizioParteSpecifica, cascadeFruizioni);
							
			
			pd.setDati(dati);

			if(errore){
				pd.setMessage(motivoErrore);
				
				pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);
				
			}
			
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
		
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ArchiviCostanti.OBJECT_NAME_ARCHIVI_EXPORT, 
					ArchiviCostanti.TIPO_OPERAZIONE_EXPORT);
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ArchiviCostanti.OBJECT_NAME_ARCHIVI_EXPORT,
					ArchiviCostanti.TIPO_OPERAZIONE_EXPORT);
		}
	}
		

}
