/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.archive.ArchiveCascadeConfiguration;
import org.openspcoop2.protocol.sdk.archive.ExportMode;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.ac.AccordiCooperazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti;
import org.openspcoop2.web.ctrlstat.servlet.ruoli.RuoliCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.scope.ScopeCostanti;
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
			
			
			// Recuperi eventuali identificativi logici degli oggetti
			Parameter provenienza = null;
			String urlTitle = null;
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
				
				String tipologia = ServletUtils.getObjectFromSession(session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
				boolean gestioneFruitori = false;
				if(tipologia!=null) {
					if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
						gestioneFruitori = true;
					}
				}
				
				Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, session);
				if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
					if(gestioneFruitori) {
						provenienza = new Parameter(ErogazioniCostanti.LABEL_ASPS_FRUIZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST);
					}
					else {
						provenienza = new Parameter(ErogazioniCostanti.LABEL_ASPS_EROGAZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST);
					}
				}
				else {
					if(gestioneFruitori) {
						provenienza = new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORI, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST);
					}
					else {
						provenienza = new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST);
					}
				}
				identificativi = exporterUtils.getIdsAccordiServizioParteSpecifica(objToExport);
				for (Object id : identificativi) {
					IDServizio idServizio = (IDServizio) id;
					String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(idServizio.getSoggettoErogatore().getTipo());
					if(protocolli.contains(protocollo)==false){
						protocolli.add(protocollo);
					}
				}
				break;
			case ACCORDO_COOPERAZIONE:
				provenienza = new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE, AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST);
				identificativi = exporterUtils.getIdsAccordiCooperazione(objToExport);
				for (Object id : identificativi) {
					IDAccordoCooperazione idAccordo = (IDAccordoCooperazione) id;
					String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(idAccordo.getSoggettoReferente().getTipo());
					if(protocolli.contains(protocollo)==false){
						protocolli.add(protocollo);
					}
				}
				break;
			case SERVIZIO_APPLICATIVO:
				provenienza = new Parameter(ServiziApplicativiCostanti.LABEL_APPLICATIVI, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST);
				identificativi = exporterUtils.getIdsServiziApplicativi(objToExport);
				for (Object id : identificativi) {
					IDServizioApplicativo idServizioApplicativo = (IDServizioApplicativo) id;
					String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(idServizioApplicativo.getIdSoggettoProprietario().getTipo());
					if(protocolli.contains(protocollo)==false){
						protocolli.add(protocollo);
					}
				}
				break;
			case RUOLO:
				provenienza = new Parameter(RuoliCostanti.LABEL_RUOLI, RuoliCostanti.SERVLET_NAME_RUOLI_LIST);
				identificativi = exporterUtils.getIdsRuoli(objToExport);
				protocolli = archiviCore.getProtocolli(session);
				break;
			case SCOPE:
				provenienza = new Parameter(ScopeCostanti.LABEL_SCOPE, ScopeCostanti.SERVLET_NAME_SCOPE_LIST);
				identificativi = exporterUtils.getIdsScope(objToExport);
				protocolli = archiviCore.getProtocolli(session);
				break;
			case CONFIGURAZIONE:
				//provenienza = new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE, null); e' al primo livello
				protocolli = archiviCore.getProtocolli(session);
				if(tipoConfigurazione==null){
					tipoConfigurazione = ArchiveType.ALL.toString();
				}
				// al primo livello il link non ci vuole
				//urlTitle = ArchiviCostanti.SERVLET_NAME_ARCHIVI_EXPORT+"?"+ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_TIPO+"="+ArchiveType.CONFIGURAZIONE.name();
				break;
			default:
				throw new Exception("Archive type ["+servletSourceExport+"] non supportato");
			}
			
			
			
			// Eventuale PostBack element name che ha scaturito l'evento
			String postBackElementName = archiviHelper.getPostBackElementName();
			

					
			// protocolli disponibili compatibili con la sorgente degli oggetti selezionati
//			if(protocolli.size()>1){
//				protocolli = new ArrayList<String>();
//				protocolli.add(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO_UNDEFINDED);
//				protocolli.addAll(archiviCore.getProtocolli());
//			}
//			this.protocollo = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO);
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
			String exportMode = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPOLOGIA_ARCHIVIO);
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
			boolean fineSceltaOpzioniEsportazione = !archiviHelper.isEditModeInProgress()  &&
					(postBackElementName==null || "".equals(postBackElementName));
			boolean configurationEnabled = ArchiveType.CONFIGURAZIONE.equals(archiveType) && !archiviCore.isExportArchive_configurazione_soloDumpCompleto();
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
					if(cascadeConfig.isCascadeRuoli()){
						send = send + "&" + ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_RUOLI+"="+ (ServletUtils.isCheckBoxEnabled(cascadeRuoli)?Costanti.CHECK_BOX_ENABLED:Costanti.CHECK_BOX_DISABLED);
					}
					if(cascadeConfig.isCascadeScope()){
						send = send + "&" + ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_SCOPE+"="+ (ServletUtils.isCheckBoxEnabled(cascadeScope)?Costanti.CHECK_BOX_ENABLED:Costanti.CHECK_BOX_DISABLED);
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
			if(provenienza!=null) {
				ServletUtils.setPageDataTitle(pd, 
						provenienza,
						new Parameter(ArchiviCostanti.LABEL_ARCHIVI_EXPORT,urlTitle));
			}
			else {
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(ArchiviCostanti.LABEL_ARCHIVI_EXPORT,urlTitle));
			}
		
			Vector<DataElement> dati = new Vector<DataElement>();
			
			// imposto grafica
			dati.addElement(ServletUtils.getDataElementForEditModeFinished());

			// init cascade
			if(ServletUtils.isCheckBoxEnabled(cascade) 
					&&
					(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE.equals(postBackElementName) || ArchiviCostanti.PARAMETRO_ARCHIVI_TIPOLOGIA_ARCHIVIO.equals(postBackElementName)) ){
				cascadePdd = Costanti.CHECK_BOX_ENABLED;
				cascadeRuoli = Costanti.CHECK_BOX_ENABLED;
				cascadeScope = Costanti.CHECK_BOX_ENABLED;
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
					cascadePdd,cascadeRuoli,cascadeScope,cascadeSoggetti,
					cascadeServiziApplicativi, cascadePorteDelegate, cascadePorteApplicative,
					cascadeAccordiCooperazione, cascadeAccordiServizioParteComune, 
					cascadeAccordiServizioComposto, 
					cascadeAccordiServizioParteSpecifica, cascadeFruizioni);
							
			
			pd.setDati(dati);

			pd.setLabelBottoneInvia(ArchiviCostanti.LABEL_ARCHIVI_EXPORT);
			
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
