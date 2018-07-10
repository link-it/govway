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

package org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.config.ConfigurazioneProtocollo;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.MessageSecurity;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.MTOMProcessorType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.mvc.properties.Config;
import org.openspcoop2.core.mvc.properties.utils.ConfigManager;
import org.openspcoop2.core.mvc.properties.utils.PropertiesSourceConfiguration;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.protocol.utils.PorteNamingUtils;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaHelper;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ExporterUtils;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.AreaBottoni;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ErogazioniHelper
 * 
 * @author Andrea Poli (poli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author: apoli $
 * @version $Rev: 14207 $, $Date: 2018-06-25 10:55:15 +0200 (Mon, 25 Jun 2018) $
 * 
 */
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
			boolean visualizzaGruppi = false;

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

			List<String> labelLst = new ArrayList<>();
			labelLst.add(ErogazioniCostanti.LABEL_ASPS_LISTA_EROGAZIONI_COLONNA_SERVIZIO);
			
			if(visualizzaGruppi)
				labelLst.add(ErogazioniCostanti.LABEL_ASPS_LISTA_EROGAZIONI_COLONNA_CONFIGURAZIONE);
			
			String [] labels = labelLst.toArray(new String[labelLst.size()]);
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

				String uriASPS = this.idServizioFactory.getUriFromAccordo(asps);

				// SCHEMA Assegnazione dati -> DataElement:
				// Data Element 1. colonna 1, riga superiore: Titolo Servizio
				// Data Element 2. colonna 1, riga inferiore: Metadati Servizio
				// Data Element 3..n . colonna 2, lista riepilogo configurazioni

				// Titolo Servizio
				DataElement de = new DataElement();
				String labelServizio = gestioneFruitori ? this.getLabelIdServizio(idServizio) :  this.getLabelIdServizioSenzaErogatore(idServizio);

				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_NOME_SERVIZIO); 
				de.setUrl(ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_CHANGE, new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId() + ""), pNomeServizio, pTipoServizio, pIdsoggErogatore);
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

				if(visualizzaGruppi) {
					// Data Element 3..n . colonna 2, lista riepilogo configurazioni
					if(showConfigurazionePA) {

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

						// Utilizza la configurazione come parent
						ServletUtils.setObjectIntoSession(this.session, PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE, PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT);
						List<MappingErogazionePortaApplicativa> listaMappingErogazionePortaApplicativa = this.apsCore.mappingServiziPorteAppList(idServizio,asps.getId(), null);
						List<PortaApplicativa> listaPorteApplicativeAssociate = new ArrayList<>();

						for(MappingErogazionePortaApplicativa mappinErogazione : listaMappingErogazionePortaApplicativa) {
							listaPorteApplicativeAssociate.add(this.porteApplicativeCore.getPortaApplicativa(mappinErogazione.getIdPortaApplicativa()));
						}

						//					// controllo accessi
						//					boolean controlloAccessiAbilitato = false;
						//					for (PortaApplicativa paAssociata : listaPorteApplicativeAssociate) {
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
								de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRACCIAMENTO);
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
						List<MappingFruizionePortaDelegata> listaMappingFruzionePortaDelegata = this.apsCore.serviziFruitoriMappingList(fruitore.getId(), idSoggettoFruitore, idServizio, null);	
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
						//					for (PortaDelegata pdAssociata : listaPorteDelegateAssociate) {
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

	public Vector<Vector<DataElement>> addErogazioneToDati(Vector<Vector<DataElement>> datiPagina, TipoOperazione tipoOp,
			AccordoServizioParteSpecifica asps, AccordoServizioParteComune as, String protocollo,ServiceBinding serviceBinding ,
			boolean gestioneErogatori, boolean gestioneFruitori,
			List<MappingErogazionePortaApplicativa> listaMappingErogazionePortaApplicativa,
			List<PortaApplicativa> listaPorteApplicativeAssociate,
			List<MappingFruizionePortaDelegata> listaMappingFruzionePortaDelegata,
			List<PortaDelegata> listaPorteDelegateAssociate, Fruitore fruitore) throws Exception { 

		boolean visualizzaGruppi = false;

		if(gestioneFruitori)
			this.pd.setCustomListViewName(ErogazioniCostanti.ASPS_EROGAZIONI_NOME_VISTA_CUSTOM_FORM_FRUIZIONE);
		else 
			this.pd.setCustomListViewName(ErogazioniCostanti.ASPS_EROGAZIONI_NOME_VISTA_CUSTOM_FORM_EROGAZIONE);

		boolean showProtocolli = this.core.countProtocolli(this.session)>1;
		Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, asps.getNome());
		Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, asps.getTipo());
		Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, asps.getId()+"");
		Parameter pId = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId()+"");
		Parameter pIdSoggettoErogatore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, asps.getIdSoggetto()+"");
		Parameter pIdFruitore = null;
		Parameter pIdSogg = null;
		if(gestioneFruitori) {
			pIdFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID, fruitore.getId()+ "");
			pIdSogg = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO, fruitore.getIdSoggetto() + "");
		}

		Soggetto sog = this.soggettiCore.getSoggettoRegistro(asps.getIdSoggetto());
		boolean isPddEsterna = this.pddCore.isPddEsterna(sog.getPortaDominio());

		// sezione 1 riepilogo
		Vector<DataElement> dati = datiPagina.elementAt(0);

//		DataElement de = new DataElement();
//		de.setLabel(ErogazioniCostanti.LABEL_ASPS_RIEPILOGO);
//		de.setType(DataElementType.TITLE);
//		dati.addElement(de);

		// Titolo Servizio
		DataElement de = new DataElement();
		IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
		String labelServizio = gestioneFruitori ? this.getLabelIdServizio(idServizio) :  this.getLabelIdServizioSenzaErogatore(idServizio);
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SERVIZIO);
		de.setValue(labelServizio);
		de.setType(DataElementType.TEXT);
		de.setUrl(
				AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE,
				new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId() + ""),
				pNomeServizio, pTipoServizio, pIdSoggettoErogatore);
		de.setToolTip(MessageFormat.format(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO, AccordiServizioParteSpecificaCostanti.LABEL_APS_SERVIZIO));
		dati.addElement(de);
		
		// soggetto erogatore
		
		

		// Metadati Servizio 
		de = new DataElement();
		String labelAPI = this.getLabelIdAccordo(as);
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
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_ACCORDO); 
		de.setValue(MessageFormat.format(ErogazioniCostanti.MESSAGE_METADATI_SERVIZIO_EROGAZIONI, labelServiceBinding, labelAPI));
		de.setType(DataElementType.TEXT);
		dati.addElement(de);


		if(showProtocolli) {
			de = new DataElement();
			String labelProtocollo =this.getLabelProtocollo(protocollo);
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_PROTOCOLLO);
			de.setValue(labelProtocollo);
			de.setType(DataElementType.TEXT);
			dati.addElement(de);
		}

		// Url Invocazione
		Parameter paIdSogg = null;
		Parameter paNomePorta = null;
		Parameter paIdPorta = null;
		Parameter paIdAsps = null;
		Parameter paConfigurazioneDati = null;
		Parameter paIdProvider = null;
		Parameter paIdPortaPerSA = null;
		Parameter paConnettoreDaListaAPS = null;
		IDPortaApplicativa idPA = null;
		PortaApplicativa paDefault = null;
		PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo =  null;

		if(gestioneErogatori) {
			
			ServletUtils.setObjectIntoSession(this.session, PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE, PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT);
			
			de = new DataElement();
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_TITOLO_PORTE_APPLICATIVE_DATI_INVOCAZIONE);
			de.setToolTip(MessageFormat.format(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO, PorteApplicativeCostanti.LABEL_PARAMETRO_TITOLO_PORTE_APPLICATIVE_DATI_INVOCAZIONE));
			de.setType(DataElementType.TEXT);
			String urlInvocazione = "";

			if(!isPddEsterna){
				idPA = this.porteApplicativeCore.getIDPortaApplicativaAssociataDefault(idServizio);
				paDefault = this.porteApplicativeCore.getPortaApplicativa(idPA);
				portaApplicativaServizioApplicativo = paDefault.getServizioApplicativoList().get(0);

				paIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, asps.getIdSoggetto() + "");
				paNomePorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA, paDefault.getNome());
				paIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, ""+paDefault.getId());
				paIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, asps.getId()+ "");
				paConfigurazioneDati = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_DATI_INVOCAZIONE, Costanti.CHECK_BOX_ENABLED_TRUE);
				paIdProvider = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_PROVIDER, paDefault.getIdSoggetto() + "");
				paIdPortaPerSA = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_PORTA, ""+paDefault.getId());
				paConnettoreDaListaAPS = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS, Costanti.CHECK_BOX_ENABLED_TRUE);

				ConfigurazioneProtocollo configProt = this.confCore.getConfigurazioneProtocollo(protocollo);

				boolean useInterfaceNameInInvocationURL = this.useInterfaceNameInImplementationInvocationURL(protocollo, serviceBinding);

				String prefix = configProt.getUrlInvocazioneServizioPA();
				prefix = prefix.trim();
				if(useInterfaceNameInInvocationURL) {
					if(prefix.endsWith("/")==false) {
						prefix = prefix + "/";
					}
				}

				urlInvocazione = prefix;
				if(useInterfaceNameInInvocationURL) {
					PorteNamingUtils utils = new PorteNamingUtils(ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo));
					urlInvocazione = urlInvocazione + utils.normalizePA(paDefault.getNome());
				}
			} else {
				urlInvocazione = "-";
			}
			de.setValue(urlInvocazione);
			de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CHANGE,paIdSogg, paNomePorta, paIdPorta,paIdAsps,paConfigurazioneDati);
			dati.addElement(de);

			// Connettore
			de = new DataElement();
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORE);
			de.setToolTip(MessageFormat.format(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORE));
			de.setType(DataElementType.TEXT);
			ServizioApplicativo sa = this.saCore.getServizioApplicativo(portaApplicativaServizioApplicativo.getId());
			InvocazioneServizio is = sa.getInvocazioneServizio();
			Connettore connis = is.getConnettore();
			List<Property> cp = connis.getPropertyList();
			String urlConnettore = "";
			for (int i = 0; i < connis.sizePropertyList(); i++) {
				Property singlecp = cp.get(i);
				if (singlecp.getNome().equals(CostantiDB.CONNETTORE_HTTP_LOCATION)) {
					urlConnettore = singlecp.getValore();
					break;
				}
			}
			de.setValue(urlConnettore);
			de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT, paIdProvider, paIdPortaPerSA, paIdAsps,
					new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME_SERVIZIO_APPLICATIVO, portaApplicativaServizioApplicativo.getNome()),
					new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO, portaApplicativaServizioApplicativo.getId()+""),
					paConnettoreDaListaAPS);
			dati.addElement(de);
		}
		
		if(gestioneFruitori) {
			
			ServletUtils.setObjectIntoSession(this.session, PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE, PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT);
			
			IDSoggetto idFruitore = new IDSoggetto(fruitore.getTipo(), fruitore.getNome());
			IDPortaDelegata idPD = this.porteDelegateCore.getIDPortaDelegataAssociataDefault(idServizio, idFruitore);
			PortaDelegata pdDefault = this.porteDelegateCore.getPortaDelegata(idPD);
				
			Parameter pIdPD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, "" + pdDefault.getId());
			Parameter pNomePD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA, pdDefault.getNome());
			Parameter pIdSoggPD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, pdDefault.getIdSoggetto() + "");
			Parameter pConfigurazioneDati = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_CONFIGURAZIONE_DATI_INVOCAZIONE, Costanti.CHECK_BOX_ENABLED_TRUE);
			Parameter pConnettoreDaListaAPS = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_CONNETTORE_DA_LISTA_APS, Costanti.CHECK_BOX_ENABLED_TRUE);
			
			
			Long idSoggettoLong = fruitore.getIdSoggetto();
			if(idSoggettoLong==null) {
				idSoggettoLong = this.soggettiCore.getIdSoggetto(fruitore.getNome(), fruitore.getTipo());
				pIdSogg = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO, idSoggettoLong + "");
			}
			Parameter pIdProviderFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE, idSoggettoLong + "");
		
			// url invocazione
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_TITOLO_PORTE_DELEGATE_DATI_INVOCAZIONE);
			de.setToolTip(MessageFormat.format(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO, PorteDelegateCostanti.LABEL_PARAMETRO_TITOLO_PORTE_DELEGATE_DATI_INVOCAZIONE));
			de.setType(DataElementType.TEXT);
			String urlInvocazione = "";
			ConfigurazioneProtocollo configProt = this.confCore.getConfigurazioneProtocollo(protocollo);
			
			boolean useInterfaceNameInInvocationURL = this.useInterfaceNameInSubscriptionInvocationURL(protocollo, serviceBinding);
			
			String prefix = configProt.getUrlInvocazioneServizioPD();
			prefix = prefix.trim();
			if(useInterfaceNameInInvocationURL) {
				if(prefix.endsWith("/")==false) {
					prefix = prefix + "/";
				}
			}
			
			urlInvocazione = prefix;
			if(useInterfaceNameInInvocationURL) {
				PorteNamingUtils utils = new PorteNamingUtils(ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo));
				urlInvocazione = urlInvocazione + utils.normalizePD(pdDefault.getNome());
			}
			
			de.setValue(urlInvocazione);
			de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CHANGE,pIdPD,pNomePD,pIdSoggPD, pIdAsps, pIdFruitore, pConfigurazioneDati);
			dati.addElement(de);
			
			
			// Connettore
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE);
			de.setToolTip(MessageFormat.format(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE));
			de.setType(DataElementType.TEXT);
			org.openspcoop2.core.registry.Connettore connettore = fruitore.getConnettore();
			Map<String, String> props = connettore.getProperties();
			String urlConnettore = props.get(CostantiDB.CONNETTORE_HTTP_LOCATION);
			
			// Controllo se richiedere il connettore
			boolean connettoreStatic = false;
			if(gestioneFruitori) {
				connettoreStatic = this.apsCore.isConnettoreStatic(protocollo);
			}
			
			if(!connettoreStatic) {
				List<Parameter> listParameter = new ArrayList<>();
				listParameter.add(pId);
				listParameter.add(pIdFruitore);
				listParameter.add(pIdSoggettoErogatore);
				listParameter.add(pIdProviderFruitore);
				listParameter.add(pConnettoreDaListaAPS);
				de.setUrl(
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE,
						listParameter.toArray(new Parameter[1])
						);
				de.setValue(urlConnettore);
			}
			else {
				de.setValue("-");
			}
			
			dati.addElement(de);
		}
		

		Parameter pGruppiTrue = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_GRUPPI,"true");
		Parameter pGruppiFalse = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_GRUPPI,"false");
		
		Parameter pConfigurazioneTrue =new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_CONFIGURAZIONI,"true");
		Parameter pConfigurazioneFalse =new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_CONFIGURAZIONI,"false");
		
		// Gestione Gruppi
		de = new DataElement();
		de.setType(DataElementType.LINK);
		if(gestioneErogatori)
			de.setUrl(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_PORTE_APPLICATIVE_LIST, pId, pNomeServizio, pTipoServizio, pIdSoggettoErogatore, pConfigurazioneFalse, pGruppiTrue);
		if(gestioneFruitori)
			de.setUrl(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_PORTE_DELEGATE_LIST,	pId, pIdSogg, pIdSoggettoErogatore, pNomeServizio, pTipoServizio, pIdFruitore, pConfigurazioneFalse, pGruppiTrue);
		de.setValue(MessageFormat.format(ErogazioniCostanti.LABEL_ASPS_GESTIONE_GRUPPI_CON_PARAMETRO, this.getLabelAzioni(serviceBinding))); 
		dati.addElement(de);
		
		// configurazioni
		de = new DataElement();
		de.setType(DataElementType.LINK);
		if(gestioneErogatori)
			de.setUrl(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_PORTE_APPLICATIVE_LIST, pId, pNomeServizio, pTipoServizio, pIdSoggettoErogatore,pConfigurazioneTrue,pGruppiFalse );
		if(gestioneFruitori)
			de.setUrl(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_PORTE_DELEGATE_LIST,	pId, pIdSogg, pIdSoggettoErogatore, pNomeServizio, pTipoServizio, pIdFruitore,pConfigurazioneTrue,pGruppiFalse);
		de.setValue(ErogazioniCostanti.LABEL_ASPS_GESTIONE_CONFIGURAZIONI);
		dati.addElement(de);

		if(visualizzaGruppi)
			this.aggiungiListaConfigurazioni(datiPagina, serviceBinding, gestioneErogatori, listaMappingErogazionePortaApplicativa,	listaPorteApplicativeAssociate, listaMappingFruzionePortaDelegata, listaPorteDelegateAssociate,  pIdAsps, pIdFruitore, asps, as);


		return datiPagina;
	}
	/*
	public Vector<Vector<DataElement>> addServiziToDatiWithGroups(Vector<Vector<DataElement>> datiPagina, String nomeServizio, String tipoServizio, String oldNomeServizio, String oldTipoServizio,
			String provider, String tipoSoggetto, String nomeSoggetto, String[] soggettiList, String[] soggettiListLabel,
			String accordo, ServiceBinding serviceBinding, org.openspcoop2.protocol.manifest.constants.InterfaceType interfaceType, String[] accordiList, String[] accordiListLabel, String servcorr, BinaryParameter wsdlimpler,
			BinaryParameter wsdlimplfru, TipoOperazione tipoOp, String id, List<String> tipi, String profilo, String portType, 
			String[] ptList, boolean privato, String uriAccordo, String descrizione, long idSoggettoErogatore,
			String statoPackage,String oldStato,String versione,
			List<String> versioni,boolean validazioneDocumenti, 
			String [] saSoggetti, String nomeSA, boolean generaPACheckSoggetto,
			List<AccordoServizioParteComune> asCompatibili,
			String erogazioneRuolo,String erogazioneAutenticazione,String erogazioneAutenticazioneOpzionale,String erogazioneAutorizzazione, boolean erogazioneIsSupportatoAutenticazioneSoggetti,
			String erogazioneAutorizzazioneAutenticati, String erogazioneAutorizzazioneRuoli, String erogazioneAutorizzazioneRuoliTipologia, String erogazioneAutorizzazioneRuoliMatch,
			List<String> soggettiAutenticati, List<String> soggettiAutenticatiLabel, String soggettoAutenticato,
			String tipoProtocollo, List<String> listaTipiProtocollo,
			String[] soggettiFruitoriList, String[] soggettiFruitoriListLabel, String providerSoggettoFruitore, String tipoSoggettoFruitore, String nomeSoggettoFruitore,
			String fruizioneServizioApplicativo,String fruizioneRuolo,String fruizioneAutenticazione,String fruizioneAutenticazioneOpzionale, String fruizioneAutorizzazione,
			String fruizioneAutorizzazioneAutenticati,String fruizioneAutorizzazioneRuoli, String fruizioneAutorizzazioneRuoliTipologia, String fruizioneAutorizzazioneRuoliMatch,
			List<String> saList,String gestioneToken, String[] gestioneTokenPolicyLabels, String[] gestioneTokenPolicyValues,
			String gestioneTokenPolicy, String gestioneTokenOpzionale, 
			String gestioneTokenValidazioneInput, String gestioneTokenIntrospection, String gestioneTokenUserInfo, String gestioneTokenForward,
			String autenticazioneTokenIssuer,String autenticazioneTokenClientId,String autenticazioneTokenSubject,String autenticazioneTokenUsername,String autenticazioneTokenEMail,
			String autorizzazione_tokenOptions,
			String autorizzazioneScope,  String scope, String autorizzazioneScopeMatch) throws Exception{

		// calcolo dei gruppi

		if (tipoOp.equals(TipoOperazione.ADD) == false) {
			if(gestioneFruitori) {

			} else {

				Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(this.session, Search.class);
				int idLista = Liste.CONFIGURAZIONE_EROGAZIONE;
				ricerca = this.checkSearchParameters(idLista, ricerca);
				AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(Long.parseLong(id));
				IDServizio idServizio = this.idServizioFactory.getIDServizioFromAccordo(asps);
				List<MappingErogazionePortaApplicativa> listaMappingErogazionePortaApplicativa = this.apsCore.mappingServiziPorteAppList(idServizio,asps.getId(), ricerca);

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

				// link ai gruppi
				Parameter pIdsoggErogatore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+asps.getIdSoggetto());
				Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, asps.getNome());
				Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, asps.getTipo());
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_PORTE_APPLICATIVE_LIST,	new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId() + ""),	pNomeServizio, pTipoServizio, pIdsoggErogatore );
				de.setValue("Gestione Gruppi");
				dati.addElement(de);


				for (int d = 0; d < listaMappingErogazionePortaApplicativa.size() ; d++) {
					MappingErogazionePortaApplicativa mapping =listaMappingErogazionePortaApplicativa.get(d);
					Vector<DataElement> gruppoVector = new Vector<DataElement>();
					PortaApplicativa paAssociata = this.porteApplicativeCore.getPortaApplicativa(mapping.getIdPortaApplicativa());

					Parameter pNomePorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA, paAssociata.getNome());
					Parameter pIdNome = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, paAssociata.getNome());
					Parameter pIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, paAssociata.getIdSoggetto() + "");
					Parameter pIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, ""+paAssociata.getId());
					Parameter pIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, asps.getId()+ "");
					Parameter pIdProvider = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_PROVIDER, paAssociata.getIdSoggetto() + "");
					Parameter pIdPortaPerSA = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_PORTA, ""+paAssociata.getId());

					@SuppressWarnings("unused")
					Parameter pConfigurazioneDati = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_DATI_INVOCAZIONE, Costanti.CHECK_BOX_ENABLED_TRUE);
					Parameter pConfigurazioneAltro = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_ALTRO, Costanti.CHECK_BOX_ENABLED_TRUE);

					// spostata direttamente nell'elenco delle erogazioni
//					// nome mapping
//					DataElement de = new DataElement();
//					//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
//					if(mapping.isDefault()) {
//						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CHANGE,pIdSogg, pNomePorta, pIdPorta,pIdAsps,pConfigurazioneDati);
//					}
//					de.setValue(mapping.isDefault() ? PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MAPPING_EROGAZIONE_PA_NOME_DEFAULT : mapping.getNome());
//					de.setIdToRemove(paAssociata.getNome());
//					//de.setToolTip(StringUtils.isNotEmpty(paAssociata.getDescrizione()) ? paAssociata.getDescrizione() : paAssociata.getNome()); 
//					e.addElement(de);

					// azioni
					de = new DataElement();
					de.setType(DataElementType.TITLE); 
					if(listaMappingErogazionePortaApplicativa.size() == 1) {
						de.setLabel("Funzionalit&agrave; attive");
					} else {
						de.setLabel("Funzionalit&agrave; attive per le risorse del Gruppo " + (listaMappingErogazionePortaApplicativa.size() - d));	
					}

//					if(!mapping.isDefault()) {
//						List<String> listaAzioni = paAssociata.getAzione()!= null ?  paAssociata.getAzione().getAzioneDelegataList() : new ArrayList<String>();
//						//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
//						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_AZIONE_LIST,pIdSogg, pIdPorta, pIdAsps);
//						if (contaListe) {
//							int numAzioni = listaAzioni.size();
//							ServletUtils.setDataElementVisualizzaLabel(de, (long) numAzioni );
//						} else
//							ServletUtils.setDataElementVisualizzaLabel(de);
//						if(listaAzioni.size() > 0) {
//							StringBuffer sb = new StringBuffer();
//							for (String string : listaAzioni) {
//								if(sb.length() >0)
//									sb.append(", ");
//								
//								sb.append(azioni.get(string));
//								
////								switch (serviceBinding) {
////								case REST:
////									sb.append(string);
////									break;
////								case SOAP:
////								default:
////									sb.append(string);
////									break;
////								}
//							}
//							de.setToolTip(sb.toString());
//						}
//					} else {
//						de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MAPPING_EROGAZIONE_PA_AZIONE_DEFAULT);
//					}
					gruppoVector.addElement(de);

					// connettore
//					if(this.isModalitaAvanzata()) {
//						de = new DataElement();
//						//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
//						String servletConnettore = ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT;
//						PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo = paAssociata.getServizioApplicativoList().get(0);
//						if(mapping.isDefault()) {
//							ServletUtils.setDataElementVisualizzaLabel(de);
//							de.setUrl(servletConnettore, pIdProvider, pIdPortaPerSA, pIdAsps,
//									new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME_SERVIZIO_APPLICATIVO, portaApplicativaServizioApplicativo.getNome()),
//									new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO, portaApplicativaServizioApplicativo.getId()+""));
//						}else {
//							if(!portaApplicativaServizioApplicativo.getNome().equals(paAssociata.getNome())) { 
//								servletConnettore = PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORE_DEFAULT;
//								de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODALITA_CONNETTORE_DEFAULT); 
//								
//							} else {
//								servletConnettore = PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORE_RIDEFINITO;
//								de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODALITA_CONNETTORE_RIDEFINITO);
//							}
//							de.setUrl(servletConnettore, pIdSogg, pIdPorta, pIdAsps);
//						}
//						
//						e.addElement(de);
//					}


					// controllo accessi
					gestioneToken = null;
					if(paAssociata.getGestioneToken()!=null && paAssociata.getGestioneToken().getPolicy()!=null &&
							!"".equals(paAssociata.getGestioneToken().getPolicy()) &&
							!"-".equals(paAssociata.getGestioneToken().getPolicy())) {
						gestioneToken = StatoFunzionalita.ABILITATO.getValue();
					}

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

					String statoControlloAccessi = this.getLabelStatoControlloAccessi(gestioneToken,autenticazione, autenticazioneOpzionale, autenticazioneCustom, autorizzazione, autorizzazioneContenuti,autorizzazioneCustom); 

					// blocco controllo accessi 
					if(statoControlloAccessi.equals(CostantiControlStation.DEFAULT_VALUE_ABILITATO)) {
						de = new DataElement();
						de.setType(DataElementType.SUBTITLE);
						de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI);
						de.setValue("white_controlloaccessi.png");
						gruppoVector.addElement(de);

						de = new DataElement();
						de.setType(DataElementType.TEXT);
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_GESTIONE_TOKEN);				
						de.setValue((gestioneToken!=null && StatoFunzionalita.ABILITATO.getValue().equals(gestioneToken)) ? CostantiControlStation.DEFAULT_VALUE_ABILITATO : CostantiControlStation.DEFAULT_VALUE_DISABILITATO );
						gruppoVector.addElement(de);

						de = new DataElement();
						de.setType(DataElementType.TEXT);
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE);				
						de.setValue((autenticazione!=null && !TipoAutenticazione.DISABILITATO.equals(autenticazione)) ? CostantiControlStation.DEFAULT_VALUE_ABILITATO : CostantiControlStation.DEFAULT_VALUE_DISABILITATO );
						gruppoVector.addElement(de);

						de = new DataElement();
						de.setType(DataElementType.TEXT);
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE);				
						de.setValue((!AutorizzazioneUtilities.STATO_DISABILITATO.equals(autorizzazione)) ? CostantiControlStation.DEFAULT_VALUE_ABILITATO : CostantiControlStation.DEFAULT_VALUE_DISABILITATO );
						gruppoVector.addElement(de);

						de = new DataElement();
						de.setType(DataElementType.BUTTON);
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI, pIdSogg, pIdPorta, pIdAsps);
						de.setValue("modifica");
						gruppoVector.addElement(de);

						de = new DataElement();
						de.setType(DataElementType.BUTTON);
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI, pIdSogg, pIdPorta, pIdAsps);
						de.setValue("elimina");
						gruppoVector.addElement(de);
					}

					// validazione contenuti
					String statoValidazione = null;
					String tipoValidazione = null;
					ValidazioneContenutiApplicativi vx = paAssociata.getValidazioneContenutiApplicativi();
					if (vx == null) {
						statoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
					} else {
						if(vx.getStato()!=null)
							statoValidazione = vx.getStato().toString();
						if ((statoValidazione == null) || "".equals(statoValidazione)) {
							statoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
						}
						if(vx.getTipo()!=null) {
							tipoValidazione = vx.getTipo().getValue();
						}
					}

					if(!statoValidazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO)) {
						de = new DataElement();
						de.setType(DataElementType.SUBTITLE);
						de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI);
						de.setValue("white_validazioneContenuti.png");
						gruppoVector.addElement(de);

						de = new DataElement();
						de.setType(DataElementType.TEXT);
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_STATO);				
						de.setValue(statoValidazione);
						gruppoVector.addElement(de);

						if(tipoValidazione != null) {
							de = new DataElement();
							de.setType(DataElementType.TEXT);
							de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_TIPO);				
							de.setValue(tipoValidazione);
							gruppoVector.addElement(de);
						}

						de = new DataElement();
						de.setType(DataElementType.BUTTON);
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI, pIdSogg, pIdPorta, pIdAsps);
						de.setValue("modifica");
						gruppoVector.addElement(de);

						de = new DataElement();
						de.setType(DataElementType.BUTTON);
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI, pIdSogg, pIdPorta, pIdAsps);
						de.setValue("elimina");
						gruppoVector.addElement(de);
					}

					// message security
					if(visualizzaSicurezza) {
						String statoMessageSecurity = paAssociata.getStatoMessageSecurity();
						MessageSecurity messageSecurity = paAssociata.getMessageSecurity();
						String requestMode = null;
						String responseMode = null; 

						if(messageSecurity != null) {
							if(messageSecurity.getRequestFlow() != null){
								requestMode = messageSecurity.getRequestFlow().getMode();
							}
							if(messageSecurity.getResponseFlow() != null){
								responseMode = messageSecurity.getResponseFlow().getMode();
							}
						}

						if(statoMessageSecurity.equals(CostantiConfigurazione.ABILITATO.toString())) {
							de = new DataElement();
							de.setType(DataElementType.SUBTITLE);
							de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY);
							de.setValue("white_messageSecurity.png");
							gruppoVector.addElement(de);

							de = new DataElement();
							de.setType(DataElementType.TEXT);
							de.setLabel(CostantiControlStation.LABEL_PARAMETRO_STATO);				
							de.setValue(statoMessageSecurity);
							gruppoVector.addElement(de);

							de = new DataElement();
							de.setType(DataElementType.TEXT);
							de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_FLOW_PROPERTIES_CONFIG + " " + CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_RICHIESTA);				


							if(StringUtils.isNotEmpty(requestMode)) {
								if(requestMode.equals(CostantiControlStation.VALUE_SICUREZZA_MESSAGGIO_SCHEMA_DEFAULT)) {
									de.setValue(CostantiControlStation.LABEL_SICUREZZA_MESSAGGIO_SCHEMA_CONFIGURAZIONE_MANUALE);
								} else {
									de.setValue(requestMode);
								}
							} else {
								de.setValue(CostantiControlStation.LABEL_SICUREZZA_MESSAGGIO_SCHEMA_NESSUNO);
							}
							gruppoVector.addElement(de);

							de = new DataElement();
							de.setType(DataElementType.TEXT);
							de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_FLOW_PROPERTIES_CONFIG + " " + CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_RISPOSTA);				
							if(StringUtils.isNotEmpty(responseMode)) {
								if(responseMode.equals(CostantiControlStation.VALUE_SICUREZZA_MESSAGGIO_SCHEMA_DEFAULT)) {
									de.setValue(CostantiControlStation.LABEL_SICUREZZA_MESSAGGIO_SCHEMA_CONFIGURAZIONE_MANUALE);
								} else {
									de.setValue(responseMode);
								}
							} else {
								de.setValue(CostantiControlStation.LABEL_SICUREZZA_MESSAGGIO_SCHEMA_NESSUNO);
							}
							gruppoVector.addElement(de);

							de = new DataElement();
							de.setType(DataElementType.BUTTON);
							de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY,pIdSogg, pIdPorta, pIdAsps);
							de.setValue("modifica");
							gruppoVector.addElement(de);

							de = new DataElement();
							de.setType(DataElementType.BUTTON);
							de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY,pIdSogg, pIdPorta, pIdAsps);
							de.setValue("elimina");
							gruppoVector.addElement(de);
						}
					}

					//mtom
					if(visualizzaMTOM) {
						boolean isMTOMAbilitatoReq = false;
						boolean isMTOMAbilitatoRes= false;
						MTOMProcessorType modeReq = null; MTOMProcessorType modeRes = null;
						if(paAssociata.getMtomProcessor()!= null){
							if(paAssociata.getMtomProcessor().getRequestFlow() != null){
								if(paAssociata.getMtomProcessor().getRequestFlow().getMode() != null){
									modeReq = paAssociata.getMtomProcessor().getRequestFlow().getMode();
									if(!modeReq.equals(MTOMProcessorType.DISABLE))
										isMTOMAbilitatoReq = true;
								}
							}

							if(paAssociata.getMtomProcessor().getResponseFlow() != null){
								if(paAssociata.getMtomProcessor().getResponseFlow().getMode() != null){
									modeRes = paAssociata.getMtomProcessor().getResponseFlow().getMode();
									if(!modeRes.equals(MTOMProcessorType.DISABLE))
										isMTOMAbilitatoRes = true;
								}
							}
						}

						if(isMTOMAbilitatoReq || isMTOMAbilitatoRes) {
							de = new DataElement();
							de.setType(DataElementType.SUBTITLE);
							de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM);
							de.setValue("white_mtom.png");
							gruppoVector.addElement(de);

							de = new DataElement();
							de.setType(DataElementType.TEXT);
							de.setLabel(CostantiControlStation.LABEL_PARAMETRO_STATO);				
							de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_ABILITATO);
							gruppoVector.addElement(de);

							de = new DataElement();
							de.setType(DataElementType.TEXT);
							de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_RICHIESTA);	
							if(modeReq != null) {
								de.setValue(modeReq.getValue());
							} else {
								de.setValue(MTOMProcessorType.DISABLE.getValue());
							}
							gruppoVector.addElement(de);

							de = new DataElement();
							de.setType(DataElementType.TEXT);
							de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_RISPOSTA);		
							if(modeRes != null) {
								de.setValue(modeRes.getValue());
							} else {
								de.setValue(MTOMProcessorType.DISABLE.getValue());
							}
							gruppoVector.addElement(de);

							de = new DataElement();
							de.setType(DataElementType.BUTTON);
							de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM,pIdPorta, pIdSogg, pIdAsps);
							de.setValue("modifica");
							gruppoVector.addElement(de);

							de = new DataElement();
							de.setType(DataElementType.BUTTON);
							de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM,pIdPorta, pIdSogg, pIdAsps);
							de.setValue("elimina");
							gruppoVector.addElement(de);
						}
					}

					// correlazione applicativa
					if(visualizzaCorrelazione) {
						de = new DataElement();
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA, pIdSogg, pIdPorta, pIdNome,pIdAsps);

						boolean isCorrelazioneApplicativaAbilitataReq = false;
						boolean isCorrelazioneApplicativaAbilitataRes = false;

						if (paAssociata.getCorrelazioneApplicativa() != null)
							isCorrelazioneApplicativaAbilitataReq = paAssociata.getCorrelazioneApplicativa().sizeElementoList() > 0;

						if (paAssociata.getCorrelazioneApplicativaRisposta() != null)
							isCorrelazioneApplicativaAbilitataRes = paAssociata.getCorrelazioneApplicativaRisposta().sizeElementoList() > 0;

						if(isCorrelazioneApplicativaAbilitataReq || isCorrelazioneApplicativaAbilitataRes) {
							de = new DataElement();
							de.setType(DataElementType.SUBTITLE);
							de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA);
							de.setValue("white_tracciamento.png");
							gruppoVector.addElement(de);

							if(isCorrelazioneApplicativaAbilitataReq) {
								de = new DataElement();
								de.setType(DataElementType.TEXT);
								de.setLabel(CostantiControlStation.LABEL_PARAMETRO_STATO + " " + CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_RICHIESTA);				
								de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_ABILITATO);
								gruppoVector.addElement(de);
							}

							if(isCorrelazioneApplicativaAbilitataRes) {
								de = new DataElement();
								de.setType(DataElementType.TEXT);
								de.setLabel(CostantiControlStation.LABEL_PARAMETRO_STATO + " " + CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_RISPOSTA);				
								de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_ABILITATO);
								gruppoVector.addElement(de);
							}

							de = new DataElement();
							de.setType(DataElementType.BUTTON);
							de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA, pIdSogg, pIdPorta, pIdNome,pIdAsps);
							de.setValue("modifica");
							gruppoVector.addElement(de);

							de = new DataElement();
							de.setType(DataElementType.BUTTON);
							de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA, pIdSogg, pIdPorta, pIdNome,pIdAsps);
							de.setValue("elimina");
							gruppoVector.addElement(de);

						}
					}

					DumpConfigurazione dumpConfigurazione = paAssociata.getDump();
					String statoDump = dumpConfigurazione == null ? CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO_DEFAULT : 
						(this.isDumpConfigurazioneAbilitato(dumpConfigurazione) ? CostantiControlStation.DEFAULT_VALUE_ABILITATO : CostantiControlStation.DEFAULT_VALUE_DISABILITATO);

					if(!statoDump.equals(CostantiControlStation.DEFAULT_VALUE_DISABILITATO)) {
						de = new DataElement();
						de.setType(DataElementType.SUBTITLE);
						de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE);
						de.setValue("white_dump.png"); 
						gruppoVector.addElement(de);

						de = new DataElement();
						de.setType(DataElementType.TEXT);
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_STATO);				
						de.setValue(statoDump);
						gruppoVector.addElement(de);

						de = new DataElement();
						de.setType(DataElementType.BUTTON);
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE, pIdSogg, pIdPorta, pIdAsps);
						de.setValue("modifica");
						gruppoVector.addElement(de);

						de = new DataElement();
						de.setType(DataElementType.BUTTON);
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE, pIdSogg, pIdPorta, pIdAsps);
						de.setValue("elimina");
						gruppoVector.addElement(de);
					}

					// Protocol Properties
//					if(this.isModalitaAvanzata()){
//						de = new DataElement();
//						//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
//						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_PROPRIETA_PROTOCOLLO_LIST, pIdSogg, pIdPorta,pIdAsps);
//						if (contaListe) {
//							int numProp = paAssociata.sizeProprietaList();
//							ServletUtils.setDataElementVisualizzaLabel(de, (long) numProp );
//						} else
//							ServletUtils.setDataElementVisualizzaLabel(de);
//						e.addElement(de);
//					}

//					// Altro
//					if(this.isModalitaAvanzata()){
//						de = new DataElement();
//						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CHANGE,pIdSogg, pNomePorta, pIdPorta,pIdAsps,pConfigurazioneAltro);
//						ServletUtils.setDataElementVisualizzaLabel(de);
//						e.addElement(de);
//					}

					// Extended Servlet List
//					if(extendedServletList!=null && extendedServletList.showExtendedInfo(this.request, this.session)){
//						de = new DataElement();
//						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_EXTENDED_LIST, pIdPorta,pIdNome,pIdPorta, pIdSogg);
//						if (contaListe) {
//							int numExtended = extendedServletList.sizeList(paAssociata);
//							ServletUtils.setDataElementVisualizzaLabel(de,Long.valueOf(numExtended));
//						} else
//							ServletUtils.setDataElementVisualizzaLabel(de);
//						e.addElement(de);
//					}

					// Abilitato
//					de = new DataElement();
//					de.setType(DataElementType.CHECKBOX);
//					boolean statoPA = paAssociata.getStato().equals(StatoFunzionalita.ABILITATO);
//					String statoMapping = statoPA ? CostantiControlStation.LABEL_PARAMETRO_PORTA_ABILITATO_TOOLTIP : CostantiControlStation.LABEL_PARAMETRO_PORTA_DISABILITATO_TOOLTIP;
//					boolean url = true;
//					if(mapping.isDefault() && allActionRedefined) {
//						statoPA = false;
//						statoMapping = this.getLabelAllAzioniRidefiniteTooltip(serviceBindingMessage);
//						url = false;
//					}
//					de.setToolTip(statoMapping);
//					de.setSelected(statoPA);
//					if(url) {
//						Parameter pAbilita = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ABILITA,  (statoPA ? Costanti.CHECK_BOX_DISABLED : Costanti.CHECK_BOX_ENABLED_TRUE));
//						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_ABILITAZIONE,pIdSogg, pNomePorta, pIdPorta,pIdAsps, pAbilita);
//					}
//					e.addElement(de);

					datiPagina.add(1,gruppoVector);
				}
			}
		}

		return datiPagina;

	}

	 */

	@SuppressWarnings("unused")
	private void aggiungiListaConfigurazioni(Vector<Vector<DataElement>> datiPagina, ServiceBinding serviceBinding,
			boolean gestioneErogatori, List<MappingErogazionePortaApplicativa> listaMappingErogazionePortaApplicativa,
			List<PortaApplicativa> listaPorteApplicativeAssociate,
			List<MappingFruizionePortaDelegata> listaMappingFruzionePortaDelegata,
			List<PortaDelegata> listaPorteDelegateAssociate,
			Parameter pIdAsps,
			Parameter pIdFruitore, AccordoServizioParteSpecifica asps, AccordoServizioParteComune as)	throws Exception {

		Map<String,String> azioni = this.core.getAzioniConLabel(asps, as, false, true, new ArrayList<String>());
		boolean allActionRedefined = false;

		PropertiesSourceConfiguration propertiesSourceConfiguration = this.core.getMessageSecurityPropertiesSourceConfiguration();
		ConfigManager configManager = ConfigManager.getinstance(ControlStationCore.getLog());
		configManager.leggiConfigurazioni(propertiesSourceConfiguration, true);

		DataElement de = null;
		// sezione 2 gruppi e funzionalita'

		if(gestioneErogatori) { // erogazioni
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

			if(azioni.size()>1) {
				List<String> azioniL = new ArrayList<>();
				if(azioni != null && azioni.size() > 0)
					azioniL.addAll(azioni.keySet());
				allActionRedefined = this.allActionsRedefinedMappingErogazionePaAssociate(azioniL, listaPorteApplicativeAssociate);
			}

			for (int d = 0; d < listaMappingErogazionePortaApplicativa.size() ; d++) {
				MappingErogazionePortaApplicativa mapping =listaMappingErogazionePortaApplicativa.get(d);
				PortaApplicativa paAssociata = listaPorteApplicativeAssociate.get(d);

				// controllo se la configurazione deve essere visualizzata
				boolean showConfigurazione = (mapping.isDefault() && allActionRedefined) ? false : true;

				int numeroConfigurazioniAttive = 0;

				List<String> labelDisponibili = new ArrayList<>();
				List<String> urlDisponibili = new ArrayList<>();

				if(showConfigurazione) {
					Vector<DataElement> gruppoVector = new Vector<DataElement>();
					Parameter pNomePorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA, paAssociata.getNome());
					Parameter pIdNome = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, paAssociata.getNome());
					Parameter pIdSoggPA = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, paAssociata.getIdSoggetto() + "");
					Parameter pIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, ""+paAssociata.getId());
					Parameter pIdProvider = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_PROVIDER, paAssociata.getIdSoggetto() + "");
					Parameter pIdPortaPerSA = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_PORTA, ""+paAssociata.getId());

					// nome Gruppo
					de = new DataElement();
					de.setType(DataElementType.TITLE); 
					de.setLabel(mapping.getDescrizione());
					gruppoVector.addElement(de);

					// controllo accessi
					boolean controlloAccessiAbilitato = false;
					String statoControlloAccessi = this.getStatoControlloAccessiPortaApplicativa(paAssociata);

					if(statoControlloAccessi.equals(CostantiControlStation.DEFAULT_VALUE_ABILITATO)) {
						controlloAccessiAbilitato = true;
						numeroConfigurazioniAttive ++ ;
					} else {
						labelDisponibili.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI);
						DataElement deTmp = new DataElement();
						deTmp.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI, pIdSoggPA, pIdPorta, pIdAsps);
						urlDisponibili.add(deTmp.getUrl());
					}

					if(controlloAccessiAbilitato) {
						de = new DataElement();
						de.setType(DataElementType.SUBTITLE);
						de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI);
						gruppoVector.addElement(de);

						de = new DataElement();
						de.setType(DataElementType.TEXT);
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_GESTIONE_TOKEN);				
						de.setValue(this.getStatoGestioneTokenPortaApplicativa(paAssociata));
						gruppoVector.addElement(de);

						de = new DataElement();
						de.setType(DataElementType.TEXT);
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE);				
						de.setValue(this.getStatoAutenticazionePortaApplicativa(paAssociata));
						gruppoVector.addElement(de);

						de = new DataElement();
						de.setType(DataElementType.TEXT);
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE);				
						de.setValue(this.getStatoAutorizzazionePortaApplicativa(paAssociata));
						gruppoVector.addElement(de);

						de = new DataElement();
						de.setType(DataElementType.BUTTON);
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI, pIdSoggPA, pIdPorta, pIdAsps);
						de.setValue(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
						de.setToolTip(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP); 
						gruppoVector.addElement(de);
					}

					// validazione contenuti
					boolean validazioneContenutiAbilitato = false;
					String statoValidazione = this.getStatoValidazionePortaApplicativa(paAssociata);

					if(!statoValidazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO)) {
						validazioneContenutiAbilitato = true;
						numeroConfigurazioniAttive ++ ;
					} else {
						labelDisponibili.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI);
						DataElement deTmp = new DataElement();
						deTmp.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI, pIdSoggPA, pIdPorta, pIdAsps);
						urlDisponibili.add(deTmp.getUrl());
					}

					if(validazioneContenutiAbilitato) {
						de = new DataElement();
						de.setType(DataElementType.SUBTITLE);
						de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI);
						gruppoVector.addElement(de);

						de = new DataElement();
						de.setType(DataElementType.TEXT);
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_STATO);				
						de.setValue(statoValidazione);
						gruppoVector.addElement(de);

						String tipoValidazione = this.getTipoValidazionePortaApplicativa(paAssociata);
						if(tipoValidazione != null) {
							de = new DataElement();
							de.setType(DataElementType.TEXT);
							de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_TIPO);				
							de.setValue(tipoValidazione);
							gruppoVector.addElement(de);
						}

						de = new DataElement();
						de.setType(DataElementType.BUTTON);
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI, pIdSoggPA, pIdPorta, pIdAsps);
						de.setValue(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
						de.setToolTip(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP); 
						gruppoVector.addElement(de);
					}


					// message security
					if(visualizzaSicurezza) {
						boolean controlloMessageSecurityAbilitato = false;
						String statoMessageSecurity = this.getStatoMessageSecurityPortaApplicativa(paAssociata);

						if(statoMessageSecurity.equals(CostantiConfigurazione.ABILITATO.toString())) {
							controlloMessageSecurityAbilitato = true;
							numeroConfigurazioniAttive ++ ;
						} else {
							labelDisponibili.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY);
							DataElement deTmp = new DataElement();
							deTmp.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY,pIdSoggPA, pIdPorta, pIdAsps);
							urlDisponibili.add(deTmp.getUrl());
						}

						if(controlloMessageSecurityAbilitato) {
							de = new DataElement();
							de.setType(DataElementType.SUBTITLE);
							de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY);
							gruppoVector.addElement(de);

							de = new DataElement();
							de.setType(DataElementType.TEXT);
							de.setLabel(CostantiControlStation.LABEL_PARAMETRO_STATO);				
							de.setValue(statoMessageSecurity);
							gruppoVector.addElement(de);

							MessageSecurity messageSecurity = paAssociata.getMessageSecurity();
							String requestMode = null;
							String responseMode = null; 

							if(messageSecurity != null) {
								if(messageSecurity.getRequestFlow() != null){
									requestMode = messageSecurity.getRequestFlow().getMode();
								}
								if(messageSecurity.getResponseFlow() != null){
									responseMode = messageSecurity.getResponseFlow().getMode();
								}
							}

							de = new DataElement();
							de.setType(DataElementType.TEXT);
							de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_RICHIESTA);				


							if(StringUtils.isNotEmpty(requestMode)) {
								if(requestMode.equals(CostantiControlStation.VALUE_SICUREZZA_MESSAGGIO_SCHEMA_DEFAULT)) {
									de.setValue(CostantiControlStation.LABEL_SICUREZZA_MESSAGGIO_SCHEMA_CONFIGURAZIONE_MANUALE);
								} else {
									Config configurazione = configManager.getConfigurazione(propertiesSourceConfiguration, requestMode);
									de.setValue(configurazione.getLabel());
								}
							} else {
								de.setValue(CostantiControlStation.LABEL_SICUREZZA_MESSAGGIO_SCHEMA_NESSUNO);
							}
							gruppoVector.addElement(de);

							de = new DataElement();
							de.setType(DataElementType.TEXT);
							de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_RISPOSTA);				
							if(StringUtils.isNotEmpty(responseMode)) {
								if(responseMode.equals(CostantiControlStation.VALUE_SICUREZZA_MESSAGGIO_SCHEMA_DEFAULT)) {
									de.setValue(CostantiControlStation.LABEL_SICUREZZA_MESSAGGIO_SCHEMA_CONFIGURAZIONE_MANUALE);
								} else {
									Config configurazione = configManager.getConfigurazione(propertiesSourceConfiguration, responseMode);
									de.setValue(configurazione.getLabel());
								}
							} else {
								de.setValue(CostantiControlStation.LABEL_SICUREZZA_MESSAGGIO_SCHEMA_NESSUNO);
							}
							gruppoVector.addElement(de);

							de = new DataElement();
							de.setType(DataElementType.BUTTON);
							de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY,pIdSoggPA, pIdPorta, pIdAsps);
							de.setValue(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
							de.setToolTip(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP); 
							gruppoVector.addElement(de);
						}
					}

					//mtom
					if(visualizzaMTOM) {
						boolean controlloMTOMAbilitato = false;
						String statoMTOM = this.getStatoMTOMPortaApplicativa(paAssociata);

						if(statoMTOM == PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_ABILITATO) {
							controlloMTOMAbilitato = true;
							numeroConfigurazioniAttive ++ ;
						} else {
							labelDisponibili.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM);
							DataElement deTmp = new DataElement();
							deTmp.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM,pIdPorta, pIdSoggPA, pIdAsps);
							urlDisponibili.add(deTmp.getUrl());
						}

						if(controlloMTOMAbilitato) {
							de = new DataElement();
							de.setType(DataElementType.SUBTITLE);
							de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM);
							gruppoVector.addElement(de);

							de = new DataElement();
							de.setType(DataElementType.TEXT);
							de.setLabel(CostantiControlStation.LABEL_PARAMETRO_STATO);				
							de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_ABILITATO);
							gruppoVector.addElement(de);

							de = new DataElement();
							de.setType(DataElementType.TEXT);
							de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_RICHIESTA);	
							MTOMProcessorType modeReq = this.getProcessorTypeRequestMTOMPortaApplicativa(paAssociata);
							if(modeReq != null) {
								de.setValue(modeReq.getValue());
							} else {
								de.setValue(MTOMProcessorType.DISABLE.getValue());
							}
							gruppoVector.addElement(de);

							de = new DataElement();
							de.setType(DataElementType.TEXT);
							de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_RISPOSTA);		
							MTOMProcessorType modeRes = this.getProcessorTypeResponseMTOMPortaApplicativa(paAssociata);
							if(modeRes != null) {
								de.setValue(modeRes.getValue());
							} else {
								de.setValue(MTOMProcessorType.DISABLE.getValue());
							}
							gruppoVector.addElement(de);

							de = new DataElement();
							de.setType(DataElementType.BUTTON);
							de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM,pIdPorta, pIdSoggPA, pIdAsps);
							de.setValue(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
							de.setToolTip(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP); 
							gruppoVector.addElement(de);
						}
					}

					// correlazione applicativa
					if(visualizzaCorrelazione) {
						boolean tracciamentoAbilitato = false;
						String statoTracciamento = this.getStatoTracciamentoPortaApplicativa(paAssociata);

						if(statoTracciamento == PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_ABILITATA){
							tracciamentoAbilitato = true;
							numeroConfigurazioniAttive ++ ;
						} else {
							labelDisponibili.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRACCIAMENTO);
							DataElement deTmp = new DataElement();
							deTmp.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA, pIdSoggPA, pIdPorta, pIdNome,pIdAsps);
							urlDisponibili.add(deTmp.getUrl());
						}

						if(tracciamentoAbilitato) {
							de = new DataElement();
							de.setType(DataElementType.SUBTITLE);
							de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRACCIAMENTO);
							gruppoVector.addElement(de);

							if(this.isRidefinitoTransazioniRegistratePortaApplicativa(paAssociata)) {
								de = new DataElement();
								de.setType(DataElementType.TEXT);
								de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI);				
								de.setValue(this.getStatoTransazioniRegistratePortaApplicativa(paAssociata));
								gruppoVector.addElement(de);
							}

							if(this.isRidefinitoMessaggiDiagnosticiPortaApplicativa(paAssociata)) {
								de = new DataElement();
								de.setType(DataElementType.TEXT);
								de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_MESSAGGI_DIAGNOSTICI);				
								de.setValue(this.getStatoMessaggiDiagnosticiPortaApplicativa(paAssociata));
								gruppoVector.addElement(de);
							}

							if(this.isEnabledCorrelazioneApplicativaPortaApplicativa(paAssociata)) {
								de = new DataElement();
								de.setType(DataElementType.TEXT);
								de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA);				
								de.setValue(this.getStatoCorrelazioneApplicativaPortaApplicativa(paAssociata)); 
								gruppoVector.addElement(de);
							}

							de = new DataElement();
							de.setType(DataElementType.BUTTON);
							de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA, pIdSoggPA, pIdPorta, pIdNome,pIdAsps);
							de.setValue(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
							de.setToolTip(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP); 
							gruppoVector.addElement(de);
						}
					}

					// dump
					boolean controlloDumpAbilitato = false;
					String statoDump = this.getStatoDumpPortaApplicativa(paAssociata);
					if(!statoDump.startsWith(CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO_DEFAULT)){
						controlloDumpAbilitato = true;
						numeroConfigurazioniAttive ++ ;
					} else {
						labelDisponibili.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE);
						DataElement deTmp = new DataElement();
						deTmp.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE, pIdSoggPA, pIdPorta, pIdAsps);
						urlDisponibili.add(deTmp.getUrl());
					}

					if(controlloDumpAbilitato) {
						de = new DataElement();
						de.setType(DataElementType.SUBTITLE);
						de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE);
						gruppoVector.addElement(de);

						de = new DataElement();
						de.setType(DataElementType.TEXT);
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_RICHIESTA);				
						de.setValue(this.getStatoDumpRichiestaPortaApplicativa(paAssociata));
						gruppoVector.addElement(de);

						de = new DataElement();
						de.setType(DataElementType.TEXT);
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_RISPOSTA);				
						de.setValue(this.getStatoDumpRispostaPortaApplicativa(paAssociata));
						gruppoVector.addElement(de);

						de = new DataElement();
						de.setType(DataElementType.BUTTON);
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE, pIdSoggPA, pIdPorta, pIdAsps);
						de.setValue(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
						de.setToolTip(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP); 
						gruppoVector.addElement(de);
					}

					// connettore
					if(!mapping.isDefault()) {
						PortaApplicativaServizioApplicativo portaApplicativaAssociataServizioApplicativo = paAssociata.getServizioApplicativoList().get(0);
						boolean connettoreConfigurazioneRidefinito = portaApplicativaAssociataServizioApplicativo.getNome().equals(paAssociata.getNome());

						String servletConnettore = PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORE_DEFAULT;

						if(connettoreConfigurazioneRidefinito) {
							servletConnettore = PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORE_RIDEFINITO;
							numeroConfigurazioniAttive ++ ;
							de = new DataElement();
							de.setType(DataElementType.SUBTITLE);
							de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORE);
							gruppoVector.addElement(de);

							ServizioApplicativo sa = this.saCore.getServizioApplicativo(portaApplicativaAssociataServizioApplicativo.getId());
							InvocazioneServizio is = sa.getInvocazioneServizio();
							Connettore connis = is.getConnettore();

							String endpointtype = "";
							if ((connis.getCustom()!=null && connis.getCustom()) && 
									!connis.getTipo().equals(TipiConnettore.HTTPS.toString()) && 
									!connis.getTipo().equals(TipiConnettore.FILE.toString())) {
								endpointtype = TipiConnettore.CUSTOM.toString();
							} else
								endpointtype = connis.getTipo();

							if(StringUtils.isNotEmpty(endpointtype)) {
								de = new DataElement();
								de.setType(DataElementType.TEXT);
								de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_ENDPOINT_TYPE);				
								de.setValue(endpointtype);
								gruppoVector.addElement(de);
							}

							List<Property> cp = connis.getPropertyList();
							String urlConnettore = "";
							for (int i = 0; i < connis.sizePropertyList(); i++) {
								Property singlecp = cp.get(i);
								if (singlecp.getNome().equals(CostantiDB.CONNETTORE_HTTP_LOCATION)) {
									urlConnettore = singlecp.getValore();
									break;
								}
							}

							if(StringUtils.isNotEmpty(urlConnettore)) {
								de = new DataElement();
								de.setType(DataElementType.TEXT);
								de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_URL);				
								de.setValue(urlConnettore.length() > 20 ? (urlConnettore.substring(0, 17) + "...") : urlConnettore);
								de.setToolTip(urlConnettore);
								gruppoVector.addElement(de);
							}

							de = new DataElement();
							de.setType(DataElementType.BUTTON);
							de.setUrl(servletConnettore, pIdSoggPA, pIdPorta, pIdAsps);
							de.setValue(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
							de.setToolTip(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP); 
							gruppoVector.addElement(de);
						} else {
							labelDisponibili.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORE);
							DataElement deTmp = new DataElement();
							deTmp.setUrl(servletConnettore, pIdSoggPA, pIdPorta, pIdAsps);
							urlDisponibili.add(deTmp.getUrl());
						}
					}

					if(this.isModalitaAvanzata()) {
						// proprieta'


						//						// opzioni avanzate
						//						de = new DataElement();
						//						//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
						//						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_PROPRIETA_PROTOCOLLO_LIST, pIdSogg, pIdPorta,pIdAsps);
						//						if (contaListe) {
						//							int numProp = paAssociata.sizeProprietaList();
						//							ServletUtils.setDataElementVisualizzaLabel(de, (long) numProp );
						//						} else
						//							ServletUtils.setDataElementVisualizzaLabel(de);
						//						e.addElement(de);
						//
						//					// Altro
						//						de = new DataElement();
						//						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CHANGE,pIdSogg, pNomePorta, pIdPorta,pIdAsps,pConfigurazioneAltro);
						//						ServletUtils.setDataElementVisualizzaLabel(de);
						//						e.addElement(de);


					}


					// check box abilitazione
					de = new DataElement();
					de.setType(DataElementType.CHECKBOX);
					boolean statoPA = paAssociata.getStato().equals(StatoFunzionalita.ABILITATO);
					String statoMapping = statoPA ? CostantiControlStation.LABEL_PARAMETRO_PORTA_ABILITATO_TOOLTIP : CostantiControlStation.LABEL_PARAMETRO_PORTA_DISABILITATO_TOOLTIP;
					de.setToolTip(statoMapping);
					de.setSelected(statoPA);
					de.setLabelRight(ErogazioniCostanti.LABEL_ASPS_ABILITA_CONFIGURAZIONE);
					Parameter pAbilita = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ABILITA,  (statoPA ? Costanti.CHECK_BOX_DISABLED : Costanti.CHECK_BOX_ENABLED_TRUE));
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_ABILITAZIONE,pIdSoggPA, pNomePorta, pIdPorta,pIdAsps, pAbilita);
					gruppoVector.addElement(de);


					// select per modale con configurazioni disponibili
					de = new DataElement();
					de.setName(ErogazioniCostanti.ASPS_EROGAZIONI_PARAMETRO_NUOVA_CONFIGURAZIONE+"_"+ d);
					de.setLabel(ErogazioniCostanti.LABEL_ASPS_EROGAZIONI_PARAMETRO_NUOVA_CONFIGURAZIONE);
					if(labelDisponibili.size() > 0) {
						de.setType(DataElementType.SELECT);
						de.setValues(urlDisponibili);
						de.setLabels(labelDisponibili);
						de.setSelected(urlDisponibili.get(0));
					} else {
						de.setType(DataElementType.HIDDEN);
					}
					gruppoVector.addElement(de);

					// informazioni disegno in testa
					de = new DataElement();
					de.setType(DataElementType.HIDDEN);
					de.setName(ErogazioniCostanti.ASPS_EROGAZIONI_PARAMETRO_NUMERO_CONFIGURAZIONI_ATTIVE);
					de.setValue(numeroConfigurazioniAttive + "");
					gruppoVector.add(0,de);

					int numeroConfigurazioniDisponibili = labelDisponibili.size();

					de = new DataElement();
					de.setType(DataElementType.HIDDEN);
					de.setName(ErogazioniCostanti.ASPS_EROGAZIONI_PARAMETRO_NUMERO_CONFIGURAZIONI_DISPONIBILI);
					de.setValue(numeroConfigurazioniDisponibili + "");
					gruppoVector.add(0,de);

					datiPagina.addElement(gruppoVector);
				}
			}

		} else { // fruizioni
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

			if(azioni.size()>1) {
				List<String> azioniL = new ArrayList<>();
				if(azioni != null && azioni.size() > 0)
					azioniL.addAll(azioni.keySet());
				allActionRedefined = this.allActionsRedefinedMappingFruizionePdAssociate(azioniL, listaPorteDelegateAssociate);
			}


			for (int d = 0; d < listaMappingFruzionePortaDelegata.size() ; d++) {
				MappingFruizionePortaDelegata mapping = listaMappingFruzionePortaDelegata.get(d);
				PortaDelegata pdAssociata = listaPorteDelegateAssociate.get(d);

				// controllo se la configurazione deve essere visualizzata
				boolean showConfigurazione = (mapping.isDefault() && allActionRedefined) ? false : true;

				int numeroConfigurazioniDisponibili = 1;
				int numeroConfigurazioniAttive = 0;

				if(showConfigurazione) {
					Vector<DataElement> gruppoVector = new Vector<DataElement>();
					Parameter pIdPD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, "" + pdAssociata.getId());
					Parameter pNomePD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA, pdAssociata.getNome());
					Parameter pIdSoggPD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, pdAssociata.getIdSoggetto() + "");

					// nome Gruppo
					de = new DataElement();
					de.setType(DataElementType.TITLE); 
					de.setLabel(mapping.getDescrizione());
					gruppoVector.addElement(de);




					// check box abilitazione
					de = new DataElement();
					de.setType(DataElementType.CHECKBOX);
					boolean statoPD = pdAssociata.getStato().equals(StatoFunzionalita.ABILITATO);
					String statoMapping = statoPD ? CostantiControlStation.LABEL_PARAMETRO_PORTA_ABILITATO_TOOLTIP : CostantiControlStation.LABEL_PARAMETRO_PORTA_DISABILITATO_TOOLTIP;
					de.setToolTip(statoMapping);
					de.setSelected(statoPD);
					de.setLabelRight(ErogazioniCostanti.LABEL_ASPS_ABILITA_CONFIGURAZIONE);
					Parameter pAbilita = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ABILITA,  (statoPD ? Costanti.CHECK_BOX_DISABLED : Costanti.CHECK_BOX_ENABLED_TRUE));
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_ABILITAZIONE,pIdPD,pNomePD,pIdSoggPD, pIdAsps, pIdFruitore, pAbilita);
					gruppoVector.addElement(de);


					// informazioni disegno in testa
					de = new DataElement();
					de.setType(DataElementType.HIDDEN);
					de.setName(ErogazioniCostanti.ASPS_EROGAZIONI_PARAMETRO_NUMERO_CONFIGURAZIONI_ATTIVE);
					de.setValue(numeroConfigurazioniAttive + "");
					gruppoVector.add(0,de);

					de = new DataElement();
					de.setType(DataElementType.HIDDEN);
					de.setName(ErogazioniCostanti.ASPS_EROGAZIONI_PARAMETRO_NUMERO_CONFIGURAZIONI_DISPONIBILI);
					de.setValue(numeroConfigurazioniDisponibili + "");
					gruppoVector.add(0,de);

					datiPagina.addElement(gruppoVector);
				}
			}
		}
	}
}
