package org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni;

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
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaHelper;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ExporterUtils;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.AreaBottoni;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;

public class ErogazioniHelper extends AccordiServizioParteSpecificaHelper{

	public ErogazioniHelper(HttpServletRequest request, PageData pd, HttpSession session) throws Exception {
		super(request, pd, session);
	}
	
	public boolean checkGestioneFruitori(HttpSession session, Search ricerca, int idLista, String tipologiaParameterName) throws Exception {
		return this.checkGestioneFruitori(session, ricerca, idLista, tipologiaParameterName, false);
	}
	
	public boolean checkGestioneFruitori(HttpSession session, Search ricerca, int idLista, String tipologiaParameterName, boolean addFilterToRicerca) throws Exception { 
		String tipologia = this.getParameter(tipologiaParameterName);
		boolean gestioneFruitori = false;
		if(tipologia==null) {
			// guardo se sto entrando da altri link fuori dal menu di sinistra
			// in tal caso e' gia' impostato
			tipologia = ServletUtils.getObjectFromSession(session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
		}
		
		if(tipologia!=null) {
			if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia)) {
				ServletUtils.setObjectIntoSession(session, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
				if(addFilterToRicerca)
					ricerca.addFilter(idLista, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE);
			}
			else if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
				ServletUtils.setObjectIntoSession(session, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
				if(addFilterToRicerca)
					ricerca.addFilter(idLista, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE);
				gestioneFruitori = true;
			}
			else if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_MULTI_TENANT.equals(tipologia)) {
				ServletUtils.removeObjectFromSession(session, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			}
		}
		return gestioneFruitori;
	}
	
	public boolean isGestioneFruitori(String tipologia) throws Exception { 
		boolean gestioneFruitori = false;
		if(tipologia==null) {
			// guardo se sto entrando da altri link fuori dal menu di sinistra in tal caso e' gia' impostato
			tipologia = ServletUtils.getObjectFromSession(this.session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
		}
		
		if(tipologia!=null && AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
			gestioneFruitori = true;
		}
	
		return gestioneFruitori;
	}

	public void prepareErogazioniList(ISearch ricerca, List<AccordoServizioParteSpecifica> lista) throws Exception {
		try {

			ServletUtils.addListElementIntoSession(this.session, ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI);

			String tipologia = ServletUtils.getObjectFromSession(this.session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			boolean gestioneFruitori = this.isGestioneFruitori(tipologia);
			
			if(gestioneFruitori)
				this.pd.setCustomListViewName(ErogazioniCostanti.ASPS_EROGAZIONI_NOME_VISTA_CUSTOM_LISTA_FRUIZIONI);
			else 
				this.pd.setCustomListViewName(ErogazioniCostanti.ASPS_EROGAZIONI_NOME_VISTA_CUSTOM_LISTA_EROGAZIONI);
			
			boolean showProtocolli = this.core.countProtocolli(this.session)>1;
			boolean showServiceBinding = true;
			if( !showProtocolli ) {
				List<String> l = this.core.getProtocolli(this.session);
				if(l.size()>0) {
					IProtocolFactory<?> p = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(l.get(0));
					if(p.getManifest().getBinding().getRest()==null || p.getManifest().getBinding().getSoap()==null) {
						showServiceBinding = false;
					}
				}
			}
			
			int idLista = Liste.SERVIZI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			addFilterProtocol(ricerca, idLista);
			
			if(showServiceBinding) {
				String filterTipoAccordo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_SERVICE_BINDING);
				this.addFilterServiceBinding(filterTipoAccordo,false,true);
			}
			
			if(this.core.isShowGestioneWorkflowStatoDocumenti()){
				if(this.core.isGestioneWorkflowStatoDocumenti_visualizzaStatoLista()) {
					String filterStatoAccordo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_STATO_ACCORDO);
					this.addFilterStatoAccordo(filterStatoAccordo,false);
				}
			}
			
			boolean showConfigurazionePA = false;
			boolean showConfigurazionePD = false;
			
			if(tipologia==null) {
				throw new Exception("Parametro TipologiaErogazione non puo' essere vuoto");
			}

			if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia)) {
				showConfigurazionePA = true;
			}
			if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
				showConfigurazionePD = true;
			}
			
			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
		
			String [] labels = {ErogazioniCostanti.LABEL_ASPS_LISTA_EROGAZIONI_COLONNA_SERVIZIO,ErogazioniCostanti.LABEL_ASPS_LISTA_EROGAZIONI_COLONNA_CONFIGURAZIONE};
			this.pd.setLabels(labels);
			
			// setto la barra del titolo
			List<Parameter> lstParm = new ArrayList<Parameter>();

			if(gestioneFruitori) {
				lstParm.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_FRUIZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
			} else {
				lstParm.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_EROGAZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
			}

			this.pd.setSearchLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_RICERCA_SERVIZIO_SOGGETTO);
			if(search.equals("")){
				this.pd.setSearchDescription("");
			}else{
				lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParm );

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, ErogazioniCostanti.LABEL_ASPS_EROGAZIONI, search);
			}

			List<AccordoServizioParteComune> listApc = new ArrayList<AccordoServizioParteComune>();
			List<String> protocolli = new ArrayList<String>();
			
			for (AccordoServizioParteSpecifica asps : lista) {
				AccordoServizioParteComune apc = this.apcCore.getAccordoServizio(asps.getIdAccordo());
				String tipoSoggetto = asps.getTipoSoggettoErogatore();
				String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoSoggetto);
				
				listApc.add(apc);
				protocolli.add(protocollo);
			}
						

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			for (int i = 0; i < lista.size(); i++) {
				AccordoServizioParteSpecifica asps = lista.get(i);
				IDServizio idServizio = this.idServizioFactory.getIDServizioFromAccordo(asps);

				Fruitore fruitore = null;
				if(showConfigurazionePD) {
					fruitore = asps.getFruitore(0);
				}
				
				String protocollo = protocolli.get(i);
				
				Vector<DataElement> e = new Vector<DataElement>();

				Parameter pIdsoggErogatore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+asps.getIdSoggetto());
				Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, asps.getNome());
				Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, asps.getTipo());
				@SuppressWarnings("unused")
				Parameter pVersioneServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VERSIONE, asps.getVersione().intValue()+"");

				String uriASPS = this.idServizioFactory.getUriFromAccordo(asps);
				
				// SCHEMA Assegnazione dati -> DataElement:
				// Data Element 1. colonna 1, riga superiore: Titolo Servizio
				// Data Element 2. colonna 1, riga inferiore: Metadati Servizio
				// Data Element 3..n . colonna 2, lista riepilogo configurazioni
				
				// Titolo Servizio
				DataElement de = new DataElement();
				String labelServizio = gestioneFruitori ? this.getLabelIdServizio(idServizio) :  this.getLabelIdServizioSenzaErogatore(idServizio);
				
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_NOME_SERVIZIO); 
				de.setUrl(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE,
					new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId() + ""),
					pNomeServizio, pTipoServizio, pIdsoggErogatore);
				de.setValue(labelServizio);
				de.setIdToRemove(uriASPS);
				de.setType(DataElementType.TITLE);
				e.addElement(de);
				
				// Metadati Servizio 
				de = new DataElement();
				AccordoServizioParteComune apc = listApc.get(i);
				ServiceBinding serviceBinding = this.apcCore.toMessageServiceBinding(apc.getServiceBinding());
				
				String labelAPI = this.getLabelIdAccordo(apc);
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
				
				if(showProtocolli) {
					String labelProtocollo =this.getLabelProtocollo(protocollo); 
					de.setValue(MessageFormat.format(ErogazioniCostanti.MESSAGE_METADATI_SERVIZIO_EROGAZIONI_CON_PROFILO, labelServiceBinding, labelAPI, labelProtocollo));
				} else {
					de.setValue(MessageFormat.format(ErogazioniCostanti.MESSAGE_METADATI_SERVIZIO_EROGAZIONI, labelServiceBinding, labelAPI));
				}
				de.setType(DataElementType.SUBTITLE);
				e.addElement(de);
				
				// Data Element 3..n . colonna 2, lista riepilogo configurazioni
				if(showConfigurazionePA) {
					
					boolean visualizzaMTOM = true;
					boolean visualizzaSicurezza = true;
					boolean visualizzaCorrelazione = true;
					switch (serviceBinding) {
					case REST:
						visualizzaMTOM = false;
						visualizzaSicurezza = true;
						visualizzaCorrelazione = false;
						break;
					case SOAP:
					default:
						visualizzaMTOM = true;
						visualizzaSicurezza = true;
						visualizzaCorrelazione = true;
						break;
					}
					
					// Utilizza la configurazione come parent
					ServletUtils.setObjectIntoSession(this.session, PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE, PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT);
					List<MappingErogazionePortaApplicativa> listaMappingErogazionePortaApplicativa = this.apsCore.mappingServiziPorteAppList(idServizio,asps.getId(), ricerca);
					List<PortaApplicativa> listaPorteApplicativeAssociate = new ArrayList<>();
					
					for(MappingErogazionePortaApplicativa mappinErogazione : listaMappingErogazionePortaApplicativa) {
						listaPorteApplicativeAssociate.add(this.porteApplicativeCore.getPortaApplicativa(mappinErogazione.getIdPortaApplicativa()));
					}
					
//					// controllo accessi
//					boolean controlloAccessiAbilitato = false;
//					for(MappingErogazionePortaApplicativa mappinErogazione : listaMappingErogazionePortaApplicativa) {
//						PortaApplicativa paAssociata = this.porteApplicativeCore.getPortaApplicativa(mappinErogazione.getIdPortaApplicativa());
//						String statoControlloAccessi = this.getStatoControlloAccessiPortaApplicativa(paAssociata); 
//						
//						if(statoControlloAccessi.equals(CostantiControlStation.DEFAULT_VALUE_ABILITATO)) {
//							controlloAccessiAbilitato = true;
//							break;
//						}
//					}
//
//					if(controlloAccessiAbilitato) {
//						de = new DataElement();
//						de.setType(DataElementType.IMAGE);
//						de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI);
//						e.addElement(de);
//					}
					
					// controllo accessi (tre box: gestioneToken, autenticazione, autorizzazione)
					boolean gestioneTokenAbilitato = false;
					for (PortaApplicativa paAssociata : listaPorteApplicativeAssociate) {
						String statoGestioneToken = this.getStatoGestioneTokenPortaApplicativa(paAssociata);
						
						if(statoGestioneToken.equals(CostantiControlStation.DEFAULT_VALUE_ABILITATO)) {
							gestioneTokenAbilitato = true;
							break;
						}
					}
					
					if(gestioneTokenAbilitato) {
						de = new DataElement();
						de.setType(DataElementType.IMAGE);
						de.setValue(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_GESTIONE_TOKEN);
						e.addElement(de);
					}
					
					boolean autenticazioneAbilitato = false;
					for (PortaApplicativa paAssociata : listaPorteApplicativeAssociate) {
						String statoAutenticazione = this.getStatoAutenticazionePortaApplicativa(paAssociata);
						
						if(statoAutenticazione.equals(CostantiControlStation.DEFAULT_VALUE_ABILITATO)) {
							autenticazioneAbilitato = true;
							break;
						}
					}
					
					if(autenticazioneAbilitato) {
						de = new DataElement();
						de.setType(DataElementType.IMAGE);
						de.setValue(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE);
						e.addElement(de);
					}
					
					boolean autorizzazioneAbilitato = false;
					for (PortaApplicativa paAssociata : listaPorteApplicativeAssociate) {
						String statoAutorizzazione = this.getStatoAutorizzazionePortaApplicativa(paAssociata);
						
						if(statoAutorizzazione.equals(CostantiControlStation.DEFAULT_VALUE_ABILITATO)) {
							autorizzazioneAbilitato = true;
							break;
						}
					}
					
					if(autorizzazioneAbilitato) {
						de = new DataElement();
						de.setType(DataElementType.IMAGE);
						de.setValue(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE);
						e.addElement(de);
					}
					
					// validazione contenuti
					boolean controlloValidazioneContentuiAbilitato = false;
					for (PortaApplicativa paAssociata : listaPorteApplicativeAssociate) {
						String statoValidazione = this.getStatoValidazionePortaApplicativa(paAssociata);
						if(!statoValidazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO)) {
							controlloValidazioneContentuiAbilitato = true;
							break;
						}
					}
					
					if(controlloValidazioneContentuiAbilitato) {
						de = new DataElement();
						de.setType(DataElementType.IMAGE);
						de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI);
						e.addElement(de);
					}
					
					
					// message security
					if(visualizzaSicurezza) {
						boolean controlloMessageSecurityAbilitato = false;
						for (PortaApplicativa paAssociata : listaPorteApplicativeAssociate) {	
							String statoMessageSecurity = this.getStatoMessageSecurityPortaApplicativa(paAssociata);
							
							if(statoMessageSecurity.equals(CostantiConfigurazione.ABILITATO.toString())) {
								controlloMessageSecurityAbilitato = true;
								break;
							}
						}
						
						if(controlloMessageSecurityAbilitato) {
							de = new DataElement();
							de.setType(DataElementType.IMAGE);
							de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY);
							e.addElement(de);
						}
					}
					
					//mtom
					if(visualizzaMTOM) {
						boolean controlloMTOMAbilitato = false;
						for (PortaApplicativa paAssociata : listaPorteApplicativeAssociate) {
							String statoMTOM = this.getStatoMTOMPortaApplicativa(paAssociata);
		
							if(statoMTOM == PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_ABILITATO) {
								controlloMTOMAbilitato = true;
								break;
							}
							 
						}
						
						if(controlloMTOMAbilitato) {
							de = new DataElement();
							de.setType(DataElementType.IMAGE);
							de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM);
							e.addElement(de);
						}
					}
					
					// correlazione applicativa
					if(visualizzaCorrelazione) {
						boolean controlloCorrelazioneApplicativaAbilitato = false;
						for (PortaApplicativa paAssociata : listaPorteApplicativeAssociate) {
							String statoTracciamento = this.getStatoTracciamentoPortaApplicativa(paAssociata);
					 
							
							if(statoTracciamento == PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_ABILITATA){
								controlloCorrelazioneApplicativaAbilitato = true;
								break;
							}
							 
						}
						
						if(controlloCorrelazioneApplicativaAbilitato) {
							de = new DataElement();
							de.setType(DataElementType.IMAGE);
							de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA);
							e.addElement(de);
						}
					}
					
					// dump
					boolean controlloDumpAbilitato = false;
					for (PortaApplicativa paAssociata : listaPorteApplicativeAssociate) {
						String statoDump = this.getStatoDumpPortaApplicativa(paAssociata);
						if(!statoDump.startsWith(CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO_DEFAULT)){
							controlloDumpAbilitato = true;
							break;
						}
					}
					
					if(controlloDumpAbilitato) {
						de = new DataElement();
						de.setType(DataElementType.IMAGE);
						de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE);
						e.addElement(de);
					}
					
				} else if(showConfigurazionePD) {
					
					// Utilizza la configurazione come parent
					ServletUtils.setObjectIntoSession(this.session, PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE, PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT);
					
					IDSoggetto idSoggettoFruitore = new IDSoggetto(fruitore.getTipo(), fruitore.getNome());
					List<MappingFruizionePortaDelegata> listaMappingFruzionePortaDelegata = this.apsCore.serviziFruitoriMappingList(fruitore.getId(), idSoggettoFruitore, idServizio, ricerca);	
					List<PortaDelegata> listaPorteDelegateAssociate = new ArrayList<>();
					
					for(MappingFruizionePortaDelegata mappingFruizione : listaMappingFruzionePortaDelegata) {
						listaPorteDelegateAssociate.add(this.porteDelegateCore.getPortaDelegata(mappingFruizione.getIdPortaDelegata()));
					}
					
					boolean visualizzaMTOM = true;
					boolean visualizzaSicurezza = true;
					boolean visualizzaCorrelazione = true;
					
					switch (serviceBinding) {
					case REST:
						visualizzaMTOM = false;
						visualizzaSicurezza = true;
						visualizzaCorrelazione = true;
						break;
					case SOAP:
					default:
						visualizzaMTOM = true;
						visualizzaSicurezza = true;
						visualizzaCorrelazione = true;
						break;
					}
					
//					// controllo accessi
//					boolean controlloAccessiAbilitato = false;
//					for(MappingFruizionePortaDelegata mappingFruizione : listaMappingFruzionePortaDelegata) {
//						PortaDelegata pdAssociata = this.porteDelegateCore.getPortaDelegata(mappingFruizione.getIdPortaDelegata());
//						String statoControlloAccessi = this.getStatoControlloAccessiPortaDelegata(pdAssociata); 
//						
//						if(statoControlloAccessi.equals(CostantiControlStation.DEFAULT_VALUE_ABILITATO)) {
//							controlloAccessiAbilitato = true;
//							break;
//						}
//					}
//					
//					if(controlloAccessiAbilitato) {
//						de = new DataElement();
//						de.setType(DataElementType.IMAGE);
//						de.setValue(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONTROLLO_ACCESSI);
//						e.addElement(de);
//					}
					
					// controllo accessi (tre box: gestioneToken, autenticazione, autorizzazione)
					boolean gestioneTokenAbilitato = false;
					for (PortaDelegata pdAssociata : listaPorteDelegateAssociate) {
						String statoGestioneToken = this.getStatoGestioneTokenPortaDelegata(pdAssociata); 
						
						if(statoGestioneToken.equals(CostantiControlStation.DEFAULT_VALUE_ABILITATO)) {
							gestioneTokenAbilitato = true;
							break;
						}
					}
					
					if(gestioneTokenAbilitato) {
						de = new DataElement();
						de.setType(DataElementType.IMAGE);
						de.setValue(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_GESTIONE_TOKEN);
						e.addElement(de);
					}
					
					boolean autenticazioneAbilitato = false;
					for (PortaDelegata pdAssociata : listaPorteDelegateAssociate) {
						String statoAutenticazione = this.getStatoAutenticazionePortaDelegata(pdAssociata); 
						
						if(statoAutenticazione.equals(CostantiControlStation.DEFAULT_VALUE_ABILITATO)) {
							autenticazioneAbilitato = true;
							break;
						}
					}
					
					if(autenticazioneAbilitato) {
						de = new DataElement();
						de.setType(DataElementType.IMAGE);
						de.setValue(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE);
						e.addElement(de);
					}
					
					boolean autorizzazioneAbilitato = false;
					for (PortaDelegata pdAssociata : listaPorteDelegateAssociate) {
						String statoAutorizzazione = this.getStatoAutorizzazionePortaDelegata(pdAssociata); 
						
						if(statoAutorizzazione.equals(CostantiControlStation.DEFAULT_VALUE_ABILITATO)) {
							autorizzazioneAbilitato = true;
							break;
						}
					}
					
					if(autorizzazioneAbilitato) {
						de = new DataElement();
						de.setType(DataElementType.IMAGE);
						de.setValue(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE);
						e.addElement(de);
					}
					
					
					// validazione contenuti
					boolean controlloValidazioneContentuiAbilitato = false;
					for (PortaDelegata pdAssociata : listaPorteDelegateAssociate) {
						String statoValidazione = this.getStatoValidazionePortaDelegata(pdAssociata);
						if(!statoValidazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO)) {
							controlloValidazioneContentuiAbilitato = true;
							break;
						}
					}
					
					if(controlloValidazioneContentuiAbilitato) {
						de = new DataElement();
						de.setType(DataElementType.IMAGE);
						de.setValue(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI);
						e.addElement(de);
					}
					
					
					// message security
					if(visualizzaSicurezza) {
						boolean controlloMessageSecurityAbilitato = false;
						for (PortaDelegata pdAssociata : listaPorteDelegateAssociate) {
							String statoMessageSecurity = this.getStatoMessageSecurityPortaDelegata(pdAssociata);
							
							if(statoMessageSecurity.equals(CostantiConfigurazione.ABILITATO.toString())) {
								controlloMessageSecurityAbilitato = true;
								break;
							}
						}
						
						if(controlloMessageSecurityAbilitato) {
							de = new DataElement();
							de.setType(DataElementType.IMAGE);
							de.setValue(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY);
							e.addElement(de);
						}
					}
					
					//mtom
					if(visualizzaMTOM) {
						boolean controlloMTOMAbilitato = false;
						for (PortaDelegata pdAssociata : listaPorteDelegateAssociate) {
							String statoMTOM = this.getStatoMTOMPortaDelegata(pdAssociata);
		
							if(statoMTOM == PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_ABILITATO) {
								controlloMTOMAbilitato = true;
								break;
							}
							 
						}
						
						if(controlloMTOMAbilitato) {
							de = new DataElement();
							de.setType(DataElementType.IMAGE);
							de.setValue(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM);
							e.addElement(de);
						}
					}
					
					// correlazione applicativa
					if(visualizzaCorrelazione) {
						boolean controlloCorrelazioneApplicativaAbilitato = false;
						for (PortaDelegata pdAssociata : listaPorteDelegateAssociate) {
							String statoTracciamento = this.getStatoTracciamentoPortaDelegata(pdAssociata);
					 
							
							if(statoTracciamento == PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_ABILITATA){
								controlloCorrelazioneApplicativaAbilitato = true;
								break;
							}
							 
						}
						
						if(controlloCorrelazioneApplicativaAbilitato) {
							de = new DataElement();
							de.setType(DataElementType.IMAGE);
							de.setValue(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA);
							e.addElement(de);
						}
					}
					
					// dump
					boolean controlloDumpAbilitato = false;
					for (PortaDelegata pdAssociata : listaPorteDelegateAssociate) {
						String statoDump = this.getStatoDumpPortaDelegata(pdAssociata);
						if(!statoDump.startsWith(CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO_DEFAULT)){
							controlloDumpAbilitato = true;
							break;
						}
					}
					
					if(controlloDumpAbilitato) {
						de = new DataElement();
						de.setType(DataElementType.IMAGE);
						de.setValue(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_DUMP_CONFIGURAZIONE);
						e.addElement(de);
					}
				}
				
				// aggiungo entry
				dati.addElement(e);
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

			// preparo bottoni
			// String gestioneWSBL = (String) this.session
			// .getAttribute("GestioneWSBL");
			if(lista!=null && lista.size()>0){
				if (this.core.isShowPulsantiImportExport()) {

					ExporterUtils exporterUtils = new ExporterUtils(this.archiviCore);
					if(exporterUtils.existsAtLeastOneExportMpde(ArchiveType.ACCORDO_SERVIZIO_PARTE_SPECIFICA, this.session)){

						Vector<AreaBottoni> bottoni = new Vector<AreaBottoni>();

						AreaBottoni ab = new AreaBottoni();
						Vector<DataElement> otherbott = new Vector<DataElement>();
						DataElement de = new DataElement();
						de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_APS_ESPORTA_SELEZIONATI);
						de.setOnClick(AccordiServizioParteSpecificaCostanti.LABEL_APS_ESPORTA_SELEZIONATI_ONCLICK);
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
}
