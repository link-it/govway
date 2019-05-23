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
package org.openspcoop2.web.ctrlstat.servlet.apc.api;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.driver.BeanUtilities;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.manifest.constants.InterfaceType;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneUtilities;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCostanti;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ExporterUtils;
import org.openspcoop2.web.lib.mvc.AreaBottoni;
import org.openspcoop2.web.lib.mvc.CheckboxStatusType;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementImage;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TargetType;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ApiHelper
 *
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ApiHelper extends AccordiServizioParteComuneHelper {

	public ApiHelper(HttpServletRequest request, PageData pd, HttpSession session) throws Exception {
		super(request, pd, session);
	}


	public void prepareApiList(List<AccordoServizioParteComuneSintetico> lista, ISearch ricerca, String tipoAccordo) throws Exception {
		try {

			ServletUtils.addListElementIntoSession(this.session, ApiCostanti.OBJECT_NAME_APC_API,
					new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO, tipoAccordo));
			
			ServletUtils.setObjectIntoSession(this.session, new Boolean(true), ApiCostanti.SESSION_ATTRIBUTE_VISTA_APC_API);
			
			this.pd.setCustomListViewName(ApiCostanti.APC_API_NOME_VISTA_CUSTOM_LISTA_API);
			
			boolean showProtocolli = this.core.countProtocolli(this.session)>1;
			boolean showServiceBinding = true;
			boolean showResources = true;
			boolean showServices = true;
			if( !showProtocolli ) {
				List<String> l = this.core.getProtocolli(this.session);
				if(l.size()>0) {
					IProtocolFactory<?> p = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(l.get(0));
					if(p.getManifest().getBinding().getRest()==null) {
						showResources=false;
					}
					if(p.getManifest().getBinding().getSoap()==null) {
						showServices=false;
					}
					if( (!showResources) || (!showServices) ) {
						showServiceBinding = false;
					}
				}
			}
			
			int idLista = Liste.ACCORDI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
			
			addFilterProtocol(ricerca, idLista);
			
			String filterTipoAccordo = null;
			if(showServiceBinding) {
				filterTipoAccordo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_SERVICE_BINDING);
				this.addFilterServiceBinding(filterTipoAccordo,false,false);
			}
			
			if(this.isShowGestioneWorkflowStatoDocumenti()){
				if(this.core.isGestioneWorkflowStatoDocumenti_visualizzaStatoLista()) {
					String filterStatoAccordo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_STATO_ACCORDO);
					this.addFilterStatoAccordo(filterStatoAccordo,false);
				}
			}
									
			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			String termine = AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo);
			
			// setto la barra del titolo
			this.pd.setSearchLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_NOME);
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(termine, AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST));
			}
			else{
				ServletUtils.setPageDataTitle(this.pd,
						new Parameter(termine, AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, termine, search);
			}
			
			List<String> labelLst = new ArrayList<>();
			labelLst.add("");//AccordiServizioParteComuneCostanti.LABEL_APC_STATO); // colonna stato
			labelLst.add(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_NOME);
			
			String [] labels = labelLst.toArray(new String[labelLst.size()]);
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			for (int i = 0; i < lista.size(); i++) {
				Vector<DataElement> e = new Vector<DataElement>();
				AccordoServizioParteComuneSintetico accordoServizio = lista.get(i);
				
				Parameter pIdAccordo = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, accordoServizio.getId()+"");
				Parameter pNomeAccordo = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, accordoServizio.getNome());
				Parameter pTipoAccordo = AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo);
				
				ServiceBinding serviceBinding = this.apcCore.toMessageServiceBinding(accordoServizio.getServiceBinding());
				
				String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(accordoServizio.getSoggettoReferente().getTipo());
				
				// SCHEMA Assegnazione dati -> DataElement:
				// Data Element 1. colonna 1, riga superiore: Titolo Api
				// Data Element 2. colonna 1, riga inferiore: Metadati Api
				
				// Titolo API
				DataElement de = new DataElement();
				de.setType(DataElementType.TITLE);
				de.setUrl(ApiCostanti.SERVLET_NAME_APC_API_CHANGE, pIdAccordo, pNomeAccordo , pTipoAccordo);
				IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromAccordo(accordoServizio);
				de.setValue(getLabelIdAccordo(protocollo, idAccordo));
				de.setIdToRemove("" + accordoServizio.getId());
//				de.setToolTip(accordoServizio.getDescrizione());
				e.addElement(de);
				
				// Metadati Servizio 
				de = new DataElement();
				String labelServiceBinding = null;
				switch (serviceBinding) {
				case REST:
					labelServiceBinding= CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_REST;
					break;
				case SOAP:
				default:
					labelServiceBinding= CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_SOAP;
					break;
				}
				InterfaceType interfaceType = this.apcCore.formatoSpecifica2InterfaceType(accordoServizio.getFormatoSpecifica());
				labelServiceBinding = labelServiceBinding + " " + this.getLabelWSDLFromFormatoSpecifica(interfaceType);
				
				if(showProtocolli) {
					String labelProtocollo =this.getLabelProtocollo(protocollo); 
					de.setValue(MessageFormat.format(ApiCostanti.MESSAGE_METADATI_APC_API_CON_PROFILO, labelServiceBinding, labelProtocollo));
				} else {
					de.setValue(MessageFormat.format(ApiCostanti.MESSAGE_METADATI_APC_API_LIST, labelServiceBinding));
				}
				de.setType(DataElementType.SUBTITLE);
				e.addElement(de);
				
				de = new DataElement();
				de.setName(ApiCostanti.APC_API_PARAMETRO_STATO_SERVIZI);
				de.setType(DataElementType.CHECKBOX);
				
				// controllo stato dell'API
				Search searchForCount = new Search(true);
				switch (serviceBinding) {
				case REST:
					// caso REST: l'API e' abilitata se ha almeno una risorsa
					
					this.apcCore.accordiResourceList(accordoServizio.getId().intValue(), searchForCount);
					int numRisorse = searchForCount.getNumEntries(Liste.ACCORDI_API_RESOURCES);
					
					if(numRisorse > 0) {
						de.setStatusType(CheckboxStatusType.ABILITATO);
						de.setStatusToolTip(ApiCostanti.APC_API_ICONA_STATO_RISORSE_TUTTE_ABILITATE_TOOLTIP);
					} else {
						de.setStatusType(CheckboxStatusType.DISABILITATO);
						de.setStatusToolTip(ApiCostanti.APC_API_ICONA_STATO_RISORSE_TUTTE_DISABILITATE_TOOLTIP);
					}
					break;
				case SOAP:
				default:
					List<PortType> accordiPorttypeList = this.apcCore.accordiPorttypeList(accordoServizio.getId().intValue(), searchForCount);
					int numeroTotaleServizi = accordiPorttypeList.size();
					int numeroServiziAbilitati = 0;
					
					for (PortType portType : accordiPorttypeList) {
						if(portType.sizeAzioneList()>0) {
							numeroServiziAbilitati ++;
						}	
					}
					
					if(numeroTotaleServizi == 0) {
						de.setStatusType(CheckboxStatusType.DISABILITATO);
						de.setStatusToolTip(ApiCostanti.APC_API_ICONA_STATO_SERVIZI_TUTTI_DISABILITATI_TOOLTIP);
					}
					else if(numeroTotaleServizi==1 && numeroServiziAbilitati==0) {
						de.setStatusType(CheckboxStatusType.DISABILITATO);
						de.setStatusToolTip(ApiCostanti.APC_API_ICONA_STATO_SERVIZIO_PARZIALMENTE_CONFIGURATO_DISABILITATI_TOOLTIP);
					} 
					else if(numeroServiziAbilitati == numeroTotaleServizi) {
						de.setStatusType(CheckboxStatusType.ABILITATO);
						de.setStatusToolTip(ApiCostanti.APC_API_ICONA_STATO_SERVIZI_TUTTI_ABILITATI_TOOLTIP);
					} 
					else {
						de.setStatusType(CheckboxStatusType.WARNING_ONLY);
						de.setStatusToolTip(ApiCostanti.APC_API_ICONA_STATO_SERVIZI_PARZIALMENTE_ABILITATI_TOOLTIP);
					}
					break;
				}
				
				de.setWidthPx(16); 
				e.addElement(de);

				// aggiungo entry
				dati.addElement(e);
			}


			this.pd.setDati(dati);
			this.pd.setAddButton(true);

			// preparo bottoni
			if(lista!=null && lista.size()>0){
				if (this.core.isShowPulsantiImportExport()) {

					ExporterUtils exporterUtils = new ExporterUtils(this.archiviCore);
					boolean exists = false;
					if(AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE.equals(tipoAccordo)){
						exists = exporterUtils.existsAtLeastOneExportMpde(ArchiveType.ACCORDO_SERVIZIO_PARTE_COMUNE, this.session);
					}
					else{
						exists = exporterUtils.existsAtLeastOneExportMpde(ArchiveType.ACCORDO_SERVIZIO_COMPOSTO, this.session);
					}
					if(exists){

						Vector<AreaBottoni> bottoni = new Vector<AreaBottoni>();

						AreaBottoni ab = new AreaBottoni();
						Vector<DataElement> otherbott = new Vector<DataElement>();
						DataElement de = new DataElement();
						de.setValue(AccordiServizioParteComuneCostanti.LABEL_APC_ESPORTA_SELEZIONATI);
						if(AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE.equals(tipoAccordo)){
							de.setOnClick(AccordiServizioParteComuneCostanti.LABEL_APC_ESPORTA_SELEZIONATI_ONCLICK);
						}
						else{
							de.setOnClick(AccordiServizioParteComuneCostanti.LABEL_ASC_ESPORTA_SELEZIONATI_ONCLICK);
						}
						otherbott.addElement(de);
						ab.setBottoni(otherbott);
						bottoni.addElement(ab);

						this.pd.setAreaBottoni(bottoni);

					}

				}
			}

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}


	public void prepareApiChange(TipoOperazione tipoOp, AccordoServizioParteComune as) throws Exception{
		
		String tipoAccordo = this.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
		Parameter pTipoAccordo = AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo);
		
		IDAccordo idAccordoOLD = this.idAccordoFactory.getIDAccordoFromValues(as.getNome(),BeanUtilities.getSoggettoReferenteID(as.getSoggettoReferente()),as.getVersione());
		String tipoProtocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(as.getSoggettoReferente().getTipo());
		String labelASTitle = this.getLabelIdAccordo(tipoProtocollo, idAccordoOLD);
		
		ServletUtils.setObjectIntoSession(this.session, new Boolean(true), ApiCostanti.SESSION_ATTRIBUTE_VISTA_APC_API);
		
		// setto la barra del titolo
		List<Parameter> lstParm = new ArrayList<Parameter>();
		String termine = AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo);
		lstParm.add(new Parameter(termine, ApiCostanti.SERVLET_NAME_APC_API_LIST,pTipoAccordo));
		lstParm.add(new Parameter(labelASTitle, null));

		// setto la barra del titolo
		ServletUtils.setPageDataTitle(this.pd, lstParm);
		
		// Preparo il menu
		this.makeMenu();
		
		Vector<Vector<DataElement>> datiPagina = new Vector<Vector<DataElement>>();
		Vector<DataElement> dati = new Vector<DataElement>();
		datiPagina.add(dati);
		dati.addElement(ServletUtils.getDataElementForEditModeFinished());
		
		ServiceBinding serviceBinding = this.apcCore.toMessageServiceBinding(as.getServiceBinding());
		
		datiPagina = this.addApiToDati(datiPagina, tipoOp, as, tipoAccordo, tipoProtocollo, serviceBinding);
		
		this.pd.setDati(datiPagina);
		this.pd.disableEditMode();
	}


	private Vector<Vector<DataElement>> addApiToDati(Vector<Vector<DataElement>> datiPagina, TipoOperazione tipoOp,
			AccordoServizioParteComune as, String tipoAccordo, String tipoProtocollo, ServiceBinding serviceBinding) throws Exception {
		
		this.pd.setCustomListViewName(ApiCostanti.APC_API_NOME_VISTA_CUSTOM_FORM_API);
		
		Parameter pIdAccordo = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, as.getId()+"");
		Parameter pNomeAccordo = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, as.getNome());
		Parameter pTipoAccordo = AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo);
		
		IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromAccordo(as);
		boolean showProtocolli = this.core.countProtocolli(this.session)>1;
		// sezione 1 riepilogo
		// nome accordo + link edit
		// nome soggetto referente + link edit
		// tipo interfaccia + link wsdl change + link download
		Vector<DataElement> dati = datiPagina.elementAt(0);
		
		List<Parameter> listParametersApi = new ArrayList<>();
		listParametersApi.add(new Parameter(ApiCostanti.PARAMETRO_APC_API_GESTIONE_PARZIALE, "")); // lasciare per primo
		listParametersApi.add(pIdAccordo);
		listParametersApi.add(pNomeAccordo);
		listParametersApi.add(pTipoAccordo);
		
		// Titolo API
		DataElement de = new DataElement();
		de.setType(DataElementType.CHECKBOX);
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_NOME);
		de.setValue(getLabelIdAccordoSenzaReferente(tipoProtocollo, idAccordo));
		de.setStatusValue(getLabelIdAccordoSenzaReferente(tipoProtocollo, idAccordo));
		listParametersApi.get(0).setValue(ApiCostanti.VALORE_PARAMETRO_APC_API_INFORMAZIONI_GENERALI);
		DataElementImage image = new DataElementImage();
		
		image.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_CHANGE, listParametersApi.toArray(new Parameter[1]));
		image.setToolTip(MessageFormat.format(ApiCostanti.APC_API_ICONA_MODIFICA_API_TOOLTIP_CON_PARAMETRO, ApiCostanti.APC_API_LABEL_APS_INFO_GENERALI));
		image.setImage(ApiCostanti.APC_API_ICONA_MODIFICA_API);
		de.setImage(image);
		
		Search searchForCount = new Search(true);
		switch (serviceBinding) {
		case REST:
			// caso REST: l'API e' abilitata se ha almeno una risorsa
			
			this.apcCore.accordiResourceList(as.getId().intValue(), searchForCount);
			int numRisorse = searchForCount.getNumEntries(Liste.ACCORDI_API_RESOURCES);
			
			if(numRisorse > 0) {
				de.setStatusType(CheckboxStatusType.ABILITATO);
				de.setStatusToolTip(ApiCostanti.APC_API_ICONA_STATO_RISORSE_TUTTE_ABILITATE_TOOLTIP);
			} else {
				de.setStatusType(CheckboxStatusType.DISABILITATO);
				de.setStatusToolTip(ApiCostanti.APC_API_ICONA_STATO_RISORSE_TUTTE_DISABILITATE_TOOLTIP);
			}
			break;
		case SOAP:
		default:
			List<PortType> accordiPorttypeList = this.apcCore.accordiPorttypeList(as.getId().intValue(), searchForCount);
			int numeroTotaleServizi = accordiPorttypeList.size();
			int numeroServiziAbilitati = 0;
			
			for (PortType portType : accordiPorttypeList) {
				if(portType.sizeAzioneList()>0) {
					numeroServiziAbilitati ++;
				}	
			}
			
			if(numeroTotaleServizi == 0) {
				de.setStatusType(CheckboxStatusType.DISABILITATO);
				de.setStatusToolTip(ApiCostanti.APC_API_ICONA_STATO_SERVIZI_TUTTI_DISABILITATI_TOOLTIP);
			}
			else if(numeroTotaleServizi==1 && numeroServiziAbilitati==0) {
				de.setStatusType(CheckboxStatusType.DISABILITATO);
				de.setStatusToolTip(ApiCostanti.APC_API_ICONA_STATO_SERVIZIO_PARZIALMENTE_CONFIGURATO_DISABILITATI_TOOLTIP);
			} 
			else if(numeroServiziAbilitati == numeroTotaleServizi) {
				de.setStatusType(CheckboxStatusType.ABILITATO);
				de.setStatusToolTip(ApiCostanti.APC_API_ICONA_STATO_SERVIZI_TUTTI_ABILITATI_TOOLTIP);
			} 
			else {
				de.setStatusType(CheckboxStatusType.WARNING_ONLY);
				de.setStatusToolTip(ApiCostanti.APC_API_ICONA_STATO_SERVIZI_PARZIALMENTE_ABILITATI_TOOLTIP);
			}
			break;
		}
		
		dati.addElement(de);
		

		
		// soggetto referente
		ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
		boolean supportatoSoggettoReferente = protocolFactoryManager.getProtocolFactoryByName(tipoProtocollo).createProtocolConfiguration().isSupportoSoggettoReferenteAccordiParteComune();
		if(supportatoSoggettoReferente) {
			de = new DataElement();
			de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_REFERENTE);
			de.setValue(this.getLabelNomeSoggetto(tipoProtocollo,as.getSoggettoReferente().getTipo(),as.getSoggettoReferente().getNome())); 
			de.setType(DataElementType.TEXT);
			image = new DataElementImage();
			listParametersApi.get(0).setValue(ApiCostanti.VALORE_PARAMETRO_APC_API_SOGGETTO_REFERENTE);
			image.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_CHANGE, listParametersApi.toArray(new Parameter[1]));
			image.setToolTip(MessageFormat.format(ApiCostanti.APC_API_ICONA_MODIFICA_API_TOOLTIP_CON_PARAMETRO, AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_REFERENTE));
			image.setImage(ApiCostanti.APC_API_ICONA_MODIFICA_API);
			de.setImage(image);
			dati.addElement(de);
		}
		
		if(showProtocolli) {
			de = new DataElement();
			String labelProtocollo =this.getLabelProtocollo(tipoProtocollo);
			de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_PROTOCOLLO);
			de.setValue(labelProtocollo);
			de.setType(DataElementType.TEXT);
			dati.addElement(de);
		}
		
		// interfaccia
		de = new DataElement();
		de.setLabel(ApiCostanti.APC_API_LABEL_PARAMETRO_INTERFACCIA);
		de.setType(DataElementType.TEXT);
		
		String labelServiceBinding = null;
		String tipoWsdl = null;
		String tipologiaDocumentoScaricare = null;
		boolean download = false;
		switch (serviceBinding) {
		case REST:
			labelServiceBinding= CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_REST;
			tipoWsdl = AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_CONCETTUALE;
			tipologiaDocumentoScaricare = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_CONCETTUALE;
			download = as.getByteWsdlConcettuale()!=null;
			break;
		case SOAP:
		default:
			labelServiceBinding= CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_SOAP;
			tipoWsdl = AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_EROGATORE;
			tipologiaDocumentoScaricare = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_LOGICO_EROGATORE;
			download = as.getByteWsdlLogicoErogatore()!=null;
			break;
		}
		
		InterfaceType interfaceType = this.apcCore.formatoSpecifica2InterfaceType(as.getFormatoSpecifica());
		String labelWsdlCon = this.getLabelWSDLFromFormatoSpecifica(interfaceType);
		StringBuilder sb = new StringBuilder();
		sb.append(labelServiceBinding).append(" ").append(labelWsdlCon);
		de.setValue(sb.toString());
				
		// lista icone a dx 
		// wsdl change
		Parameter pNascondiSezioneDownload = new Parameter(ApiCostanti.PARAMETRO_APC_API_GESTIONE_PARZIALE, ApiCostanti.VALORE_PARAMETRO_APC_API_GESTIONE_PARZIALE_WSDL_CHANGE); 
		Parameter pTipoWsdl = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_WSDL, tipoWsdl);
		List<Parameter> listParametersApiWsdlChange = new ArrayList<>();
		listParametersApiWsdlChange.add(pIdAccordo);
		listParametersApiWsdlChange.add(pTipoAccordo);
		listParametersApiWsdlChange.add(pTipoWsdl);
		listParametersApiWsdlChange.add(pNascondiSezioneDownload);
		image = new DataElementImage();
		if(this.isModalitaStandard()) {
			image.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_WSDL_CHANGE, listParametersApiWsdlChange.toArray(new Parameter[1]));
		}
		else {
			listParametersApi.get(0).setValue(ApiCostanti.VALORE_PARAMETRO_APC_API_GESTIONE_SPECIFICA_INTERFACCE);
			image.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_CHANGE, listParametersApi.toArray(new Parameter[1]));
		}
		image.setToolTip(MessageFormat.format(ApiCostanti.APC_API_ICONA_MODIFICA_API_TOOLTIP_CON_PARAMETRO, ApiCostanti.APC_API_LABEL_PARAMETRO_INTERFACCIA));
		image.setImage(ApiCostanti.APC_API_ICONA_MODIFICA_API);
		image.setTarget(TargetType.SELF);
		
		de.addImage(image);
		// download
		if(download) {
			List<Parameter> listParametersApiWsdlDownload = new ArrayList<>();
			Parameter pIdAccordoDownload = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_ID_ACCORDO, as.getId()+"");
			Parameter pTipoDocumentoDownload = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO, tipologiaDocumentoScaricare);
			Parameter pTipoAccordoDownload = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO, ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_PARTE_COMUNE);
			listParametersApiWsdlDownload.add(pIdAccordoDownload);
			listParametersApiWsdlDownload.add(pTipoDocumentoDownload);
			listParametersApiWsdlDownload.add(pTipoAccordoDownload);
			
			image = new DataElementImage();
			image.setUrl(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT, listParametersApiWsdlDownload.toArray(new Parameter[1]));
			image.setToolTip(MessageFormat.format(ApiCostanti.APC_API_ICONA_DOWNLOAD_DOCUMENTO_INTERFACCIA_TOOLTIP_CON_PARAMETRO, ApiCostanti.APC_API_LABEL_PARAMETRO_INTERFACCIA));
			image.setImage(ApiCostanti.APC_API_ICONA_DOWNLOAD_DOCUMENTO_INTERFACCIA);
			image.setTarget(TargetType.SELF);
			
			de.addImage(image);
		}
		
		dati.addElement(de);
		
		
		// Descrizione
		de = new DataElement();
		de.setType(DataElementType.TEXT);
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_DESCRIZIONE);
		int length = 150;
		String descrizione = null;
		if(as.getDescrizione()!=null && as.getDescrizione().length()>length) {
			descrizione = as.getDescrizione().substring(0, (length-4)) + " ...";
		}
		else {
			descrizione =  as.getDescrizione() ;
		}
		de.setValue(descrizione);
		de.setToolTip(as.getDescrizione());
		listParametersApi.get(0).setValue(ApiCostanti.VALORE_PARAMETRO_APC_API_DESCRIZIONE);
		de.setIcon(ApiCostanti.APC_API_ICONA_MODIFICA_API);
		
		image = new DataElementImage();
		listParametersApi.get(0).setValue(ApiCostanti.VALORE_PARAMETRO_APC_API_DESCRIZIONE);
		image.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_CHANGE, listParametersApi.toArray(new Parameter[1]));
		image.setToolTip(MessageFormat.format(ApiCostanti.APC_API_ICONA_MODIFICA_API_TOOLTIP_CON_PARAMETRO, AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_DESCRIZIONE));
		image.setImage(ApiCostanti.APC_API_ICONA_MODIFICA_API);
		de.setImage(image);
		
		dati.addElement(de);
		
		
		// link
		// 1. risorse/servizi
		switch(serviceBinding) {
		case REST:
				de = new DataElement();
				de.setType(DataElementType.LINK);
				listParametersApi.get(0).setValue("");
				de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_RESOURCES_LIST, listParametersApi.toArray(new Parameter[1]));
				de.setValue(ApiCostanti.APC_API_LABEL_GESTIONE_RISORSE);
				de.setIcon(ApiCostanti.APC_API_ICONA_GESTIONE_RISORSE);
				dati.addElement(de);
			break;
		case SOAP:
		default:
			// PortType
			de = new DataElement();
			de.setType(DataElementType.LINK);
			listParametersApi.get(0).setValue("");
			de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPES_LIST, listParametersApi.toArray(new Parameter[1]));
			de.setValue(ApiCostanti.APC_API_LABEL_GESTIONE_SERVIZI);
			de.setIcon(ApiCostanti.APC_API_ICONA_GESTIONE_SERVIZI);
			dati.addElement(de);
			break;
		
		}

		// 2. allegati
		de = new DataElement();
		de.setType(DataElementType.LINK);
		listParametersApi.get(0).setValue("");
		de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_ALLEGATI_LIST, listParametersApi.toArray(new Parameter[1]));
		de.setValue(ApiCostanti.APC_API_LABEL_GESTIONE_ALLEGATI);
		de.setIcon(ApiCostanti.APC_API_ICONA_GESTIONE_ALLEGATI);
		dati.addElement(de);
		
		// 3. opzioni avanzate
		if(this.isModalitaAvanzata()) {
			de = new DataElement();
			de.setType(DataElementType.LINK);
			listParametersApi.get(0).setValue(ApiCostanti.VALORE_PARAMETRO_APC_API_OPZIONI_AVANZATE);
			de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_CHANGE, listParametersApi.toArray(new Parameter[1]));
			de.setValue(ApiCostanti.APC_API_LABEL_GESTIONE_OPZIONI_AVANZATE);
			de.setIcon(ApiCostanti.APC_API_ICONA_GESTIONE_OPZIONI_AVANZATE);
			dati.addElement(de);
		}
		
		return datiPagina;
	}
	
	
}
