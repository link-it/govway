/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
import org.openspcoop2.core.config.CanaleConfigurazione;
import org.openspcoop2.core.config.CanaliConfigurazione;
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
import org.openspcoop2.core.config.constants.RuoloContesto;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.mvc.properties.Config;
import org.openspcoop2.core.mvc.properties.utils.ConfigManager;
import org.openspcoop2.core.mvc.properties.utils.PropertiesSourceConfiguration;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.beans.GruppoSintetico;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.config.UrlInvocazioneAPI;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneUtilities;
import org.openspcoop2.web.ctrlstat.servlet.apc.api.ApiCostanti;
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
 * ErogazioniHelper
 * 
 * @author Andrea Poli (poli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ErogazioniHelper extends AccordiServizioParteSpecificaHelper{

	public ErogazioniHelper(HttpServletRequest request, PageData pd, HttpSession session) throws Exception {
		super(request, pd, session);
	}
	public ErogazioniHelper(ControlStationCore core, HttpServletRequest request, PageData pd, HttpSession session) throws Exception {
		super(core, request, pd, session);
	}

	public void checkGestione(HttpSession session, Search ricerca, int idLista, String tipologiaParameterName) throws Exception {
		this.checkGestione(session, ricerca, idLista, tipologiaParameterName, false);
	}

	public void checkGestione(HttpSession session, Search ricerca, int idLista, String tipologiaParameterName, boolean addFilterToRicerca) throws Exception { 
		String tipologia = this.getParameter(tipologiaParameterName);
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
				
				boolean filtraSoloEsterni = true;
				if(addFilterToRicerca) {
					if(this.apsCore.isMultitenant() && this.apsCore.getMultitenantSoggettiFruizioni()!=null) {
						switch (this.apsCore.getMultitenantSoggettiFruizioni()) {
						case SOLO_SOGGETTI_ESTERNI:
							filtraSoloEsterni = true;
							break;
						case ESCLUDI_SOGGETTO_FRUITORE:
						case TUTTI:
							filtraSoloEsterni = false;
							break;
						}
					}
				}
				
				if(addFilterToRicerca && filtraSoloEsterni)
					ricerca.addFilter(idLista, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE);
			}
			else if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_COMPLETA.equals(tipologia)) {
				ServletUtils.removeObjectFromSession(session, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			}
			
			if(addFilterToRicerca && this.isSoggettoMultitenantSelezionato()) {
				ricerca.addFilter(idLista, Filtri.FILTRO_SOGGETTO, this.getSoggettoMultitenantSelezionato());
			}
		}
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

	public DataElement newDataElementStatoApiErogazione(DataElement de, boolean setWidthPx,
			AccordoServizioParteSpecifica asps,AccordoServizioParteComuneSintetico apc, String nomePortaDefault,
			List<MappingErogazionePortaApplicativa> listaMappingErogazionePortaApplicativa, List<PortaApplicativa> listaPorteApplicativeAssociate) throws Exception {
		int numeroAbilitate = 0;
		int numeroConfigurazioni = 0;
		boolean allActionRedefined = false;
		String msgControlloAccessiMalConfigurato = null;
		// stato gruppi
		numeroConfigurazioni = listaMappingErogazionePortaApplicativa.size();
		
		if(listaMappingErogazionePortaApplicativa.size()>1) {
			List<String> azioniL = new ArrayList<>();
			Map<String,String> azioni = this.apsCore.getAzioniConLabel(asps, apc, false, true, new ArrayList<String>());
			if(azioni != null && azioni.size() > 0)
				azioniL.addAll(azioni.keySet());
			allActionRedefined = this.allActionsRedefinedMappingErogazione(azioniL, listaMappingErogazionePortaApplicativa);
		}
		
		for (PortaApplicativa paAssociata : listaPorteApplicativeAssociate) {
			boolean statoPA = paAssociata.getStato().equals(StatoFunzionalita.ABILITATO);
			if(statoPA) {
				if(!allActionRedefined || !paAssociata.getNome().equals(nomePortaDefault)) {
					numeroAbilitate ++;
				}
			}
			
			if(StatoFunzionalita.ABILITATO.equals(paAssociata.getStato()) && msgControlloAccessiMalConfigurato==null){
				if(TipoAutorizzazione.isAuthenticationRequired(paAssociata.getAutorizzazione())) {
					if( 
							(paAssociata.getSoggetti()==null || paAssociata.getSoggetti().sizeSoggettoList()<=0) 
							&&
							(paAssociata.getServiziApplicativiAutorizzati()==null || paAssociata.getServiziApplicativiAutorizzati().sizeServizioApplicativoList()<=0)
							) {
						msgControlloAccessiMalConfigurato = ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_PUNTUALE_NO_FRUITORI;
						if(listaMappingErogazionePortaApplicativa.size()>1) {
							for(MappingErogazionePortaApplicativa mappinErogazione : listaMappingErogazionePortaApplicativa) {
								if(mappinErogazione.getIdPortaApplicativa().getNome().equals(paAssociata.getNome())) {
									msgControlloAccessiMalConfigurato = "(Gruppo: '"+mappinErogazione.getDescrizione()+"') "+ msgControlloAccessiMalConfigurato;
									break;
								}
							}
						}
					}
				}
				
				if(msgControlloAccessiMalConfigurato==null && 
						TipoAutorizzazione.isRolesRequired(paAssociata.getAutorizzazione())) {
					if( 
							(paAssociata.getRuoli()==null || paAssociata.getRuoli().sizeRuoloList()<=0) 
						) {
						msgControlloAccessiMalConfigurato = ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_PUNTUALE_NO_RUOLI;
						if(listaMappingErogazionePortaApplicativa.size()>1) {
							for(MappingErogazionePortaApplicativa mappinErogazione : listaMappingErogazionePortaApplicativa) {
								if(mappinErogazione.getIdPortaApplicativa().getNome().equals(paAssociata.getNome())) {
									msgControlloAccessiMalConfigurato = "(Gruppo: '"+mappinErogazione.getDescrizione()+"') "+ msgControlloAccessiMalConfigurato;
									break;
								}
							}
						}
					}
				}
				
				if(msgControlloAccessiMalConfigurato==null && 
						paAssociata.getGestioneToken()!=null && StatoFunzionalita.ABILITATO.equals(paAssociata.getStato()) &&
						paAssociata.getScope()!=null && StatoFunzionalita.ABILITATO.equals(paAssociata.getScope().getStato())) {
					if( 
							(paAssociata.getScope().sizeScopeList()<=0) 
						) {
						msgControlloAccessiMalConfigurato = ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_PUNTUALE_NO_SCOPE;
						if(listaMappingErogazionePortaApplicativa.size()>1) {
							for(MappingErogazionePortaApplicativa mappinErogazione : listaMappingErogazionePortaApplicativa) {
								if(mappinErogazione.getIdPortaApplicativa().getNome().equals(paAssociata.getNome())) {
									msgControlloAccessiMalConfigurato = "(Gruppo: '"+mappinErogazione.getDescrizione()+"') "+ msgControlloAccessiMalConfigurato;
									break;
								}
							}
						}
					}
				}
			}
		}
		
		return newDataElementStatoApi(de, setWidthPx, msgControlloAccessiMalConfigurato, null, numeroAbilitate, numeroConfigurazioni, allActionRedefined);
	}
	
	public DataElement newDataElementStatoApiFruizione(DataElement de, boolean setWidthPx,
			AccordoServizioParteSpecifica asps,AccordoServizioParteComuneSintetico apc, String nomePortaDefault,
			List<MappingFruizionePortaDelegata> listaMappingFruzionePortaDelegata, List<PortaDelegata> listaPorteDelegateAssociate) throws Exception {
		int numeroAbilitate = 0;
		int numeroConfigurazioni = 0;
		boolean allActionRedefined = false;
		String msgControlloAccessiMalConfigurato = null;
		// stato gruppi

		numeroConfigurazioni = listaMappingFruzionePortaDelegata.size();
		
		if(listaMappingFruzionePortaDelegata.size()>1) {
			List<String> azioniL = new ArrayList<>();
			Map<String,String> azioni = this.porteDelegateCore.getAzioniConLabel(asps, apc, false, true, new ArrayList<String>());
			if(azioni != null && azioni.size() > 0)
				azioniL.addAll(azioni.keySet());
			allActionRedefined = this.allActionsRedefinedMappingFruizione(azioniL, listaMappingFruzionePortaDelegata);
		}
		
		for (PortaDelegata pdAssociata : listaPorteDelegateAssociate) {
			boolean statoPD = pdAssociata.getStato().equals(StatoFunzionalita.ABILITATO);
			if(statoPD) {
				if(!allActionRedefined || !pdAssociata.getNome().equals(nomePortaDefault)) {
					numeroAbilitate ++;
				}
			}
			
			if(StatoFunzionalita.ABILITATO.equals(pdAssociata.getStato()) && msgControlloAccessiMalConfigurato==null){
				if(TipoAutorizzazione.isAuthenticationRequired(pdAssociata.getAutorizzazione())) {
					if( 
							(pdAssociata.sizeServizioApplicativoList()<=0)
							) {
						msgControlloAccessiMalConfigurato = ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_PUNTUALE_NO_FRUITORI;
						if(listaMappingFruzionePortaDelegata.size()>1) {
							for(MappingFruizionePortaDelegata mappingFruizione : listaMappingFruzionePortaDelegata) {
								if(mappingFruizione.getIdPortaDelegata().getNome().equals(pdAssociata.getNome())) {
									msgControlloAccessiMalConfigurato = "(Gruppo: '"+mappingFruizione.getDescrizione()+"') "+ msgControlloAccessiMalConfigurato;
									break;
								}
							}
						}
					}
				}
				
				if(msgControlloAccessiMalConfigurato==null && 
						TipoAutorizzazione.isRolesRequired(pdAssociata.getAutorizzazione())) {
					if( 
							(pdAssociata.getRuoli()==null || pdAssociata.getRuoli().sizeRuoloList()<=0) 
						) {
						msgControlloAccessiMalConfigurato = ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_PUNTUALE_NO_RUOLI;
						if(listaMappingFruzionePortaDelegata.size()>1) {
							for(MappingFruizionePortaDelegata mappingFruizione : listaMappingFruzionePortaDelegata) {
								if(mappingFruizione.getIdPortaDelegata().getNome().equals(pdAssociata.getNome())) {
									msgControlloAccessiMalConfigurato = "(Gruppo: '"+mappingFruizione.getDescrizione()+"') "+ msgControlloAccessiMalConfigurato;
									break;
								}
							}
						}
					}
				}
				
				if(msgControlloAccessiMalConfigurato==null && 
						pdAssociata.getGestioneToken()!=null && StatoFunzionalita.ABILITATO.equals(pdAssociata.getStato()) &&
						pdAssociata.getScope()!=null && StatoFunzionalita.ABILITATO.equals(pdAssociata.getScope().getStato())) {
					if( 
							(pdAssociata.getScope().sizeScopeList()<=0) 
						) {
						msgControlloAccessiMalConfigurato = ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_PUNTUALE_NO_SCOPE;
						if(listaMappingFruzionePortaDelegata.size()>1) {
							for(MappingFruizionePortaDelegata mappingFruizione : listaMappingFruzionePortaDelegata) {
								if(mappingFruizione.getIdPortaDelegata().getNome().equals(pdAssociata.getNome())) {
									msgControlloAccessiMalConfigurato = "(Gruppo: '"+mappingFruizione.getDescrizione()+"') "+ msgControlloAccessiMalConfigurato;
									break;
								}
							}
						}
					}
				}
			}
		}
		
		return newDataElementStatoApi(de, setWidthPx, msgControlloAccessiMalConfigurato, null, numeroAbilitate, numeroConfigurazioni, allActionRedefined);
	}
	
	private DataElement newDataElementStatoApi(DataElement deParam, boolean setWidthPx, String msgControlloAccessiMalConfiguratoError,  String msgControlloAccessiMalConfiguratoWarning, 
			int numeroAbilitate, int numeroConfigurazioni, boolean allActionRedefined) {
		
		DataElement de = deParam;
		boolean list = false;
		if(de==null) {
			de = new DataElement();
			list = true;
		}
		if(list) {
			de.setName(ErogazioniCostanti.ASPS_EROGAZIONI_PARAMETRO_STATO_CONFIGURAZIONI);
		}
		de.setType(DataElementType.CHECKBOX);
		if(msgControlloAccessiMalConfiguratoError!=null) {
			de.setStatusType(CheckboxStatusType.DISABILITATO);
			de.setStatusToolTip(msgControlloAccessiMalConfiguratoError);
		}
		else if(msgControlloAccessiMalConfiguratoWarning!=null) {
			de.setStatusType(CheckboxStatusType.WARNING_ONLY);
			de.setStatusToolTip(msgControlloAccessiMalConfiguratoWarning);
		}
		else if(numeroAbilitate == 0) {
			de.setStatusType(CheckboxStatusType.DISABILITATO);
			de.setStatusToolTip(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_TUTTE_DISABILITATE_TOOLTIP);
		} else if( 
				(!allActionRedefined && numeroAbilitate == numeroConfigurazioni) 
				||
				(allActionRedefined && numeroAbilitate == (numeroConfigurazioni-1)) // escludo la regola che non viene usata poiche' tutte le azioni sono ridefinite 
				) {
			de.setStatusType(CheckboxStatusType.ABILITATO);
			de.setStatusToolTip(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_TUTTE_ABILITATE_TOOLTIP);
		} else  {
			de.setStatusType(CheckboxStatusType.WARNING_ONLY);
			de.setStatusToolTip(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_PARZIALMENTE_ABILITATE_TOOLTIP);
		}
		if(setWidthPx) {
			de.setWidthPx(16);
		}

		return de;
		
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

			String filterProtocollo = addFilterProtocol(ricerca, idLista, true);
			boolean profiloSelezionato = false;
			String protocolloS = filterProtocollo;
			if(protocolloS==null) {
				// significa che e' stato selezionato un protocollo nel menu in alto a destra
				List<String> protocolli = this.core.getProtocolli(this.session);
				if(protocolli!=null && protocolli.size()==1) {
					protocolloS = protocolli.get(0);
				}
			}
			if( (filterProtocollo!=null && !"".equals(filterProtocollo) &&
					!CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROTOCOLLO_QUALSIASI.equals(filterProtocollo))
					||
				(filterProtocollo==null && protocolloS!=null)
					) {
				profiloSelezionato = true;
			}
			
			if( profiloSelezionato && 
					(!this.isSoggettoMultitenantSelezionato())) {
				String filterSoggetto = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_SOGGETTO);
				this.addFilterSoggetto(filterSoggetto,protocolloS,true,false);
			}
			
			if(showServiceBinding) {
				String filterTipoAccordo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_SERVICE_BINDING);
				this.addFilterServiceBinding(filterTipoAccordo,false,true);
			}
			
			String filterGruppo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_GRUPPO);
			addFilterGruppo(filterProtocollo, filterGruppo, false);

			CanaliConfigurazione canali = this.confCore.getCanaliConfigurazione(false);
			if(canali!=null && StatoFunzionalita.ABILITATO.equals(canali.getStato())) {
				String filterCanale = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CANALE);
				addFilterCanale(canali, filterCanale, false);
			}
			
			if(this.isShowGestioneWorkflowStatoDocumenti()){
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
			labelLst.add("");//AccordiServizioParteSpecificaCostanti.LABEL_APS_STATO); // colonna stato
			//labelLst.add(ErogazioniCostanti.LABEL_ASPS_LISTA_EROGAZIONI_COLONNA_SERVIZIO);
			if(gestioneFruitori) {
				labelLst.add(ErogazioniCostanti.LABEL_ASPS_FRUIZIONI);
			}
			else {
				labelLst.add(ErogazioniCostanti.LABEL_ASPS_EROGAZIONI);
			}
			
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

			this.pd.setSearchLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_RICERCA_API_SOGGETTO);
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

			List<AccordoServizioParteComuneSintetico> listApc = new ArrayList<AccordoServizioParteComuneSintetico>();
			List<String> protocolli = new ArrayList<String>();
			
			// colleziono i tags registrati
			List<String> tagsDisponibili = this.gruppiCore.getAllGruppiOrdinatiPerDataRegistrazione();
			
			// configurazione dei canali
			CanaliConfigurazione gestioneCanali = this.confCore.getCanaliConfigurazione(false);
			boolean gestioneCanaliEnabled = gestioneCanali != null && org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO.equals(gestioneCanali.getStato());
			List<CanaleConfigurazione> canaleList = gestioneCanali != null ? gestioneCanali.getCanaleList() : new ArrayList<>();
			CanaleConfigurazione canaleConfigurazioneDefault = gestioneCanaliEnabled ? canaleList.stream().filter((c) -> c.isCanaleDefault()).findFirst().get(): null;

			for (AccordoServizioParteSpecifica asps : lista) {
				AccordoServizioParteComuneSintetico apc = this.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
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

				String protocollo = protocolli.get(i);
				
				Fruitore fruitore = null;
				IDSoggetto idSoggettoFruitore = null;
				if(showConfigurazionePD || gestioneFruitori) {
					fruitore = asps.getFruitore(0); // NOTA: il metodo 'soggettiServizioList' ritorna un unico fruitore in caso di gestioneFruitori abiltata per ogni entry. Crea cioè un accordo con fruitore per ogni fruitore esistente
					idSoggettoFruitore = new IDSoggetto(fruitore.getTipo(), fruitore.getNome());
				}

				Vector<DataElement> e = new Vector<DataElement>();

				Parameter pIdsoggErogatore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+asps.getIdSoggetto());
				Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, asps.getNome());
				Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, asps.getTipo());
				Parameter pTipoSoggettoFruitore = null;
				Parameter pNomeSoggettoFruitore = null;
				if(gestioneFruitori) {
					pTipoSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SOGGETTO_FRUITORE, fruitore.getTipo());
					pNomeSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SOGGETTO_FRUITORE, fruitore.getNome());
				}

				String uriASPS = this.idServizioFactory.getUriFromAccordo(asps);

				// SCHEMA Assegnazione dati -> DataElement:
				// Data Element 1. colonna 1, riga superiore: Titolo Servizio
				// Data Element 2. colonna 1, riga inferiore: Metadati Servizio
				// Data Element 3..n . colonna 2, lista riepilogo configurazioni

				// Titolo Servizio
				DataElement de = new DataElement();			
				String labelServizio = null;
				if(gestioneFruitori) {
					labelServizio = this.getLabelServizioFruizione(protocollo, idSoggettoFruitore, idServizio);
				}
				else {
					labelServizio = this.getLabelServizioErogazione(protocollo, idServizio);
				}
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_NOME_SERVIZIO);
				List<Parameter> listParameters = new ArrayList<>();
				listParameters.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId() + ""));
				listParameters.add(pNomeServizio);
				listParameters.add(pTipoServizio);
				listParameters.add(pIdsoggErogatore);
				if(gestioneFruitori) {
					listParameters.add(pTipoSoggettoFruitore);
					listParameters.add(pNomeSoggettoFruitore);
				}
				de.setUrl(ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_CHANGE, listParameters.toArray(new Parameter[1]));
				de.setValue(labelServizio);
				if(gestioneFruitori) {
					de.setIdToRemove(uriASPS+"@"+fruitore.getTipo()+"/"+fruitore.getNome());
				}else {
					de.setIdToRemove(uriASPS);
				}
				de.setType(DataElementType.TITLE);
				e.addElement(de);
				
				AccordoServizioParteComuneSintetico apc = listApc.get(i);
				ServiceBinding serviceBinding = this.apcCore.toMessageServiceBinding(apc.getServiceBinding());
				
				DataElement dataElementStatoApi = null;
				String labelCanalePorta = null;
				// stato gruppi
				if(showConfigurazionePA) {
					List<MappingErogazionePortaApplicativa> listaMappingErogazionePortaApplicativa = this.apsCore.mappingServiziPorteAppList(idServizio,asps.getId(), null);
					List<PortaApplicativa> listaPorteApplicativeAssociate = new ArrayList<>();

					String nomePortaDefault = null;
					for(MappingErogazionePortaApplicativa mappinErogazione : listaMappingErogazionePortaApplicativa) {
						PortaApplicativa pa = this.porteApplicativeCore.getPortaApplicativa(mappinErogazione.getIdPortaApplicativa());
						if(mappinErogazione.isDefault()) {
							nomePortaDefault = mappinErogazione.getIdPortaApplicativa().getNome();
							labelCanalePorta = pa.getCanale();
						}
						listaPorteApplicativeAssociate.add(pa);
					}
					
					dataElementStatoApi = this.newDataElementStatoApiErogazione(null, true, asps, apc, nomePortaDefault, listaMappingErogazionePortaApplicativa, listaPorteApplicativeAssociate);
				}
				
				if(showConfigurazionePD) {
					List<MappingFruizionePortaDelegata> listaMappingFruzionePortaDelegata = this.apsCore.serviziFruitoriMappingList(fruitore.getId(), idSoggettoFruitore, idServizio, null);	
					List<PortaDelegata> listaPorteDelegateAssociate = new ArrayList<>();

					String nomePortaDefault = null;
					for(MappingFruizionePortaDelegata mappingFruizione : listaMappingFruzionePortaDelegata) {
						PortaDelegata pd = this.porteDelegateCore.getPortaDelegata(mappingFruizione.getIdPortaDelegata());
						if(mappingFruizione.isDefault()) {
							nomePortaDefault = mappingFruizione.getIdPortaDelegata().getNome();
							labelCanalePorta = pd.getCanale();
						}					
						
						listaPorteDelegateAssociate.add(pd);
					}
					
					dataElementStatoApi = this.newDataElementStatoApiFruizione(null, true, asps, apc, nomePortaDefault, listaMappingFruzionePortaDelegata, listaPorteDelegateAssociate);
				}

				// Metadati Servizio 
				de = new DataElement();
				
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

				if(gestioneCanaliEnabled) {
					if(labelCanalePorta == null) { // default dell'accordo
						labelCanalePorta =  apc.getCanale();
					}
					
					if(labelCanalePorta == null) { // default del sistema
						labelCanalePorta =  canaleConfigurazioneDefault.getNome();
					}
					
					if(showProtocolli) {
						String labelProtocollo =this.getLabelProtocollo(protocollo); 
						de.setValue(MessageFormat.format(ErogazioniCostanti.MESSAGE_METADATI_SERVIZIO_EROGAZIONI_CON_PROFILO_CON_CANALE, labelServiceBinding, labelAPI, labelCanalePorta, labelProtocollo));
					} else {
						de.setValue(MessageFormat.format(ErogazioniCostanti.MESSAGE_METADATI_SERVIZIO_EROGAZIONI_LIST_CON_CANALE, labelServiceBinding, labelAPI, labelCanalePorta));
					}
				} else {
					if(showProtocolli) {
						String labelProtocollo =this.getLabelProtocollo(protocollo); 
						de.setValue(MessageFormat.format(ErogazioniCostanti.MESSAGE_METADATI_SERVIZIO_EROGAZIONI_CON_PROFILO, labelServiceBinding, labelAPI, labelProtocollo));
					} else {
						de.setValue(MessageFormat.format(ErogazioniCostanti.MESSAGE_METADATI_SERVIZIO_EROGAZIONI_LIST, labelServiceBinding, labelAPI));
					}
				}
				de.setType(DataElementType.SUBTITLE);
				e.addElement(de);
				
				// tags
				List<GruppoSintetico> gruppo = apc.getGruppo();
				if(gruppo != null && gruppo.size() > 0) {
					for (int j = 0; j < gruppo.size(); j++) {
						GruppoSintetico gruppoSintetico = gruppo.get(j);
						de = new DataElement();
						de.setName(ApiCostanti.PARAMETRO_APC_API_GESTIONE_GRUPPO + "_" + j);
						de.setType(DataElementType.BUTTON);
						de.setLabel(gruppoSintetico.getNome());
						
						int indexOf = tagsDisponibili.indexOf(gruppoSintetico.getNome());
						if(indexOf == -1)
							indexOf = 0;
						
						indexOf = indexOf % CostantiControlStation.NUMERO_GRUPPI_CSS;
						
						de.setStyleClass("label-info-"+indexOf);
						
						e.addElement(de);
					}
				}
				
				
				
				if(dataElementStatoApi!=null) {
					e.addElement(dataElementStatoApi);
				}

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
							String statoDump = this.getStatoDumpPortaApplicativa(paAssociata, true);
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
							String statoDump = this.getStatoDumpPortaDelegata(pdAssociata, true);
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
						if(gestioneFruitori) {
							de.setOnClick(AccordiServizioParteSpecificaCostanti.LABEL_FRUIZIONI_ESPORTA_SELEZIONATI_ONCLICK);
						}
						else {
							de.setOnClick(AccordiServizioParteSpecificaCostanti.LABEL_EROGAZIONI_ESPORTA_SELEZIONATI_ONCLICK);
						}
						de.setDisabilitaAjaxStatus();
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
			AccordoServizioParteSpecifica asps, AccordoServizioParteComuneSintetico as, String protocollo,ServiceBinding serviceBinding ,
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
		Parameter pTipoSoggettoFruitore = null;
		Parameter pNomeSoggettoFruitore = null;
		Parameter pIdProviderFruitore = null;
		if(gestioneFruitori) {
			pIdFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID, fruitore.getId()+ "");
			pIdSogg = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO, fruitore.getIdSoggetto() + "");
			pTipoSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SOGGETTO_FRUITORE, fruitore.getTipo());
			pNomeSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SOGGETTO_FRUITORE, fruitore.getNome());
			
			Long idSoggettoLong = fruitore.getIdSoggetto();
			if(idSoggettoLong==null) {
				idSoggettoLong = this.soggettiCore.getIdSoggetto(fruitore.getNome(), fruitore.getTipo());
				pIdSogg = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO, idSoggettoLong + "");
			}
			pIdProviderFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE, idSoggettoLong + "");
		
		}

		Soggetto sog = this.soggettiCore.getSoggettoRegistro(asps.getIdSoggetto());
		boolean isPddEsterna = this.pddCore.isPddEsterna(sog.getPortaDominio());

		boolean showSoggettoErogatoreInErogazioni = this.core.isMultitenant() && 
				!this.isSoggettoMultitenantSelezionato();
		
		boolean showSoggettoFruitoreInFruizioni = gestioneFruitori &&  this.core.isMultitenant() && 
				!this.isSoggettoMultitenantSelezionato();
		
		
		// sezione 1 riepilogo
		Vector<DataElement> dati = datiPagina.elementAt(0);

		// Titolo Servizio
		DataElement de = new DataElement();
		IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
		String labelServizio = gestioneFruitori ? this.getLabelIdServizioSenzaErogatore(idServizio) :  this.getLabelIdServizioSenzaErogatore(idServizio);
		String labelServizioConPortType = labelServizio;
		if(asps.getPortType()!=null && !"".equals(asps.getPortType()) && !asps.getNome().equals(asps.getPortType())) {
			labelServizioConPortType = labelServizioConPortType +" ("+asps.getPortType()+")";
		}
		de.setLabel(ErogazioniCostanti.LABEL_ASPS_MODIFICA_SERVIZIO_NOME);
		de.setValue(labelServizioConPortType);
		de.setStatusValue(labelServizioConPortType);
		de.setType(DataElementType.TEXT);
		List<Parameter> listParametersServizio = new ArrayList<>();
		listParametersServizio.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId() + ""));
		listParametersServizio.add(pNomeServizio);
		listParametersServizio.add(pTipoServizio);
		listParametersServizio.add(pIdSoggettoErogatore);
		if(gestioneFruitori) {
			listParametersServizio.add(pTipoSoggettoFruitore);
			listParametersServizio.add(pNomeSoggettoFruitore);
			listParametersServizio.add(pIdProviderFruitore);
		}
		listParametersServizio.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_API, "false")); // lasciare come ultimo! Si leva e si riaggiunge dopo
		
		DataElementImage imageChangeStato = null;
		if(gestioneErogatori) {			
			String nomePortaDefault = null;
			for(MappingErogazionePortaApplicativa mappinErogazione : listaMappingErogazionePortaApplicativa) {
				if(mappinErogazione.isDefault()) {
					nomePortaDefault = mappinErogazione.getIdPortaApplicativa().getNome();
				}
			}
			this.newDataElementStatoApiErogazione(de, false, asps, as, nomePortaDefault, listaMappingErogazionePortaApplicativa, listaPorteApplicativeAssociate);
			if(listaMappingErogazionePortaApplicativa.size()==1) {
				PortaApplicativa pa = this.porteApplicativeCore.getPortaApplicativa(listaMappingErogazionePortaApplicativa.get(0).getIdPortaApplicativa());
				Parameter pIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, ""+pa.getId());
				Parameter pNomePorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA, pa.getNome());
				Parameter pIdSoggPA = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, pa.getIdSoggetto() + "");
				Parameter pFromApi = new Parameter(CostantiControlStation.PARAMETRO_API_PAGE_INFO, Costanti.CHECK_BOX_ENABLED_TRUE);
				boolean statoPA = pa.getStato().equals(StatoFunzionalita.ABILITATO);
				Parameter pAbilita = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ABILITA,  (statoPA ? Costanti.CHECK_BOX_DISABLED : Costanti.CHECK_BOX_ENABLED_TRUE));
				imageChangeStato = new DataElementImage();
				imageChangeStato.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_ABILITAZIONE,pIdSoggPA, pNomePorta, pIdPorta,pIdAsps, pAbilita,pFromApi);
				String statoMapping = statoPA ? CostantiControlStation.LABEL_PARAMETRO_PORTA_ABILITATO_TOOLTIP : CostantiControlStation.LABEL_PARAMETRO_PORTA_DISABILITATO_TOOLTIP;
				imageChangeStato.setToolTip(statoMapping);
				imageChangeStato.setImage(statoPA ? CostantiControlStation.ICONA_MODIFICA_TOGGLE_ON : CostantiControlStation.ICONA_MODIFICA_TOGGLE_OFF);
			}
		}	
		if(gestioneFruitori) {		
			String nomePortaDefault = null;
			for(MappingFruizionePortaDelegata mappingFruizione : listaMappingFruzionePortaDelegata) {
				if(mappingFruizione.isDefault()) {
					nomePortaDefault = mappingFruizione.getIdPortaDelegata().getNome();
				}
			}			
			this.newDataElementStatoApiFruizione(de, false, asps, as, nomePortaDefault, listaMappingFruzionePortaDelegata, listaPorteDelegateAssociate);	
			if(listaMappingFruzionePortaDelegata.size()==1) {
				PortaDelegata pd = this.porteDelegateCore.getPortaDelegata(listaMappingFruzionePortaDelegata.get(0).getIdPortaDelegata());
				Parameter pIdPD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, "" + pd.getId());
				Parameter pNomePD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA, pd.getNome());
				Parameter pIdSoggPD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, pd.getIdSoggetto() + "");
				Parameter pFromApi = new Parameter(CostantiControlStation.PARAMETRO_API_PAGE_INFO, Costanti.CHECK_BOX_ENABLED_TRUE);
				boolean statoPD = pd.getStato().equals(StatoFunzionalita.ABILITATO);
				Parameter pAbilita = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ABILITA,  (statoPD ? Costanti.CHECK_BOX_DISABLED : Costanti.CHECK_BOX_ENABLED_TRUE));
				imageChangeStato = new DataElementImage();
				imageChangeStato.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_ABILITAZIONE,pIdPD,pNomePD,pIdSoggPD, pIdAsps, pIdFruitore, pAbilita,pFromApi);
				String statoMapping = statoPD ? CostantiControlStation.LABEL_PARAMETRO_PORTA_ABILITATO_TOOLTIP : CostantiControlStation.LABEL_PARAMETRO_PORTA_DISABILITATO_TOOLTIP;
				imageChangeStato.setToolTip(statoMapping);
				imageChangeStato.setImage(statoPD ? CostantiControlStation.ICONA_MODIFICA_TOGGLE_ON : CostantiControlStation.ICONA_MODIFICA_TOGGLE_OFF);
			}
		}
		
		DataElementImage imageChangeName = new DataElementImage();
		imageChangeName.setUrl(
				AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE,
				listParametersServizio.toArray(new Parameter[1]));
		imageChangeName.setToolTip(MessageFormat.format(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,
				AccordiServizioParteSpecificaCostanti.LABEL_APS_INFO_GENERALI));
				//AccordiServizioParteSpecificaCostanti.LABEL_APS_SERVIZIO));
		imageChangeName.setImage(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
		
		if(imageChangeStato!=null) {
			de.addImage(imageChangeName);
			de.addImage(imageChangeStato);
		}
		else {
			de.setImage(imageChangeName);
		}
		
		dati.addElement(de);
		
		// soggetto erogatore
		if(gestioneFruitori || showSoggettoErogatoreInErogazioni) {
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SOGGETTO_EROGATORE);
			de.setValue(this.getLabelNomeSoggetto(protocollo,asps.getTipoSoggettoErogatore(),asps.getNomeSoggettoErogatore()));
			de.setType(DataElementType.TEXT);
			dati.addElement(de);
		}

		// API
		de = new DataElement();
		Parameter pTipoAccordo = AccordiServizioParteComuneUtilities.getParametroAccordoServizio(as);
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
		de.setValue(MessageFormat.format(ErogazioniCostanti.MESSAGE_METADATI_SERVIZIO_EROGAZIONI_EDIT, labelServiceBinding, labelAPI));
		de.setType(DataElementType.BUTTON);
		
		List<String> labelsGruppi = new ArrayList<String>();
		List<String> ValuesGruppi = new ArrayList<String>();
		
		List<GruppoSintetico> gruppi = as.getGruppo();
		if(gruppi != null) {
			// colleziono i tags registrati
			List<String> tagsDisponibili = this.gruppiCore.getAllGruppiOrdinatiPerDataRegistrazione();
			
			for (int i = 0; i < gruppi.size(); i++) {
				GruppoSintetico gruppo = gruppi.get(i);
				
				int indexOf = tagsDisponibili.indexOf(gruppo.getNome());
				if(indexOf == -1)
					indexOf = 0;
				
				indexOf = indexOf % CostantiControlStation.NUMERO_GRUPPI_CSS;
				
				labelsGruppi.add(gruppo.getNome());
				ValuesGruppi.add("label-info-"+ indexOf);
			}
		}	
		
		de.setLabels(labelsGruppi);
		de.setValues(ValuesGruppi);
		
		
		// Lista di Accordi Compatibili
		List<AccordoServizioParteComune> asParteComuneCompatibili = null;
		try{
			asParteComuneCompatibili = this.apsCore.findAccordiParteComuneBySoggettoAndNome(as.getNome(), 
					new IDSoggetto(as.getSoggettoReferente().getTipo(), as.getSoggettoReferente().getNome()));
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la ricerca degli accordi parte comune compatibili", e);
		}
		
		// lista icone a dx 
		// n.b. iniziare ad aggiungerle da quella che deve stare piu' a dx perche' la regola css float right le allinea al contrario
		
		boolean apiImplementataCambiabile = ErogazioniUtilities.isChangeAPIEnabled(asps, this.apsCore);
		if(apiImplementataCambiabile) {
			listParametersServizio.remove(listParametersServizio.size()-1);
			listParametersServizio.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_CAMBIA_API, "true"));
			
			DataElementImage image = new DataElementImage();
			image.setUrl(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE,	listParametersServizio.toArray(new Parameter[1]));
			image.setToolTip(MessageFormat.format(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_CAMBIA_API_TOOLTIP_CON_PARAMETRO, 
					AccordiServizioParteSpecificaCostanti.LABEL_APC_COMPOSTO_SOLO_PARTE_COMUNE));
			image.setImage(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
			image.setTarget(TargetType.SELF);
			de.addImage(image);
		}
		
		if(asParteComuneCompatibili!=null && asParteComuneCompatibili.size()>1) {
			listParametersServizio.remove(listParametersServizio.size()-1);
			listParametersServizio.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_API, "true"));
			
			DataElementImage image = new DataElementImage();
			image.setUrl(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE,	listParametersServizio.toArray(new Parameter[1]));
			image.setToolTip(MessageFormat.format(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_UPGRADE_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO, 
					"Versione "+AccordiServizioParteSpecificaCostanti.LABEL_APC_COMPOSTO_SOLO_PARTE_COMUNE));
			image.setImage(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_UPGRADE_CONFIGURAZIONE);
			image.setTarget(TargetType.SELF);
			de.addImage(image);
		}
		
		DataElementImage image = new DataElementImage();
		image.setUrl(ApiCostanti.SERVLET_NAME_APC_API_CHANGE, new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getIdAccordo() + ""),
				new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME, as.getNome()), pTipoAccordo);
		image.setToolTip(MessageFormat.format(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_VISUALIZZA_TOOLTIP_CON_PARAMETRO, AccordiServizioParteSpecificaCostanti.LABEL_APC_COMPOSTO_SOLO_PARTE_COMUNE));
		image.setImage(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_VISUALIZZA);
		image.setTarget(TargetType.BLANK); 
		image.setDisabilitaAjaxStatus();
		
		de.addImage(image);
		dati.addElement(de);

		
		// ProtocolProperties
		
		IDServizio idAps = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
		idAps.setPortType(asps.getPortType());
		idAps.setUriAccordoServizioParteComune(asps.getAccordoServizioParteComune());
		IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
		IConsoleDynamicConfiguration consoleDynamicConfiguration = protocolFactory.createDynamicConfigurationConsole();
		IRegistryReader registryReader = this.apcCore.getRegistryReader(protocolFactory); 
		IConfigIntegrationReader configRegistryReader = this.apcCore.getConfigIntegrationReader(protocolFactory);
		ConsoleConfiguration consoleConfiguration = null;
		if(gestioneErogatori) {
			consoleConfiguration = consoleDynamicConfiguration.getDynamicConfigAccordoServizioParteSpecifica(ConsoleOperationType.CHANGE, this, 
					registryReader, configRegistryReader, idAps );
		}
		else {
			IDFruizione idFruizione = new IDFruizione();
			idFruizione.setIdServizio(idAps);
			idFruizione.setIdFruitore(new IDSoggetto(fruitore.getTipo(), fruitore.getNome()));
			consoleConfiguration = consoleDynamicConfiguration.getDynamicConfigFruizioneAccordoServizioParteSpecifica(ConsoleOperationType.CHANGE, this,  
					registryReader, configRegistryReader, idFruizione);
		}
		boolean modificaDatiProfilo = false;
		if(consoleConfiguration!=null && consoleConfiguration.getConsoleItem()!=null && !consoleConfiguration.getConsoleItem().isEmpty()) {
			modificaDatiProfilo = true;
		}
		if(showProtocolli || modificaDatiProfilo) {
			
			de = new DataElement();
			String labelProtocollo =this.getLabelProtocollo(protocollo);
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_PROTOCOLLO);
			de.setValue(labelProtocollo);
			de.setType(DataElementType.TEXT);
			
			if(modificaDatiProfilo) {
				
				listParametersServizio.remove(listParametersServizio.size()-1);
				listParametersServizio.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_PROFILO, "true"));
				
				image = new DataElementImage();
				if(gestioneFruitori) {
					List<Parameter> list = new ArrayList<>();
					list.addAll(listParametersServizio);
					list.add(pIdFruitore);
					image.setUrl(
							AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE,
							list.toArray(new Parameter[1]));
				}
				else {
					image.setUrl(
							AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE,
							listParametersServizio.toArray(new Parameter[1]));
				}
				image.setToolTip(MessageFormat.format(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,
						AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_PROTOCOLLO));
				image.setImage(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
				de.setImage(image);
				
			}
			
			dati.addElement(de);
			
		}
		
		
		// Fruitore
		if(showSoggettoFruitoreInFruizioni) {
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SOGGETTO_FRUITORE);
			de.setValue(this.getLabelNomeSoggetto(protocollo,fruitore.getTipo(),fruitore.getNome()));
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
		Parameter paConfigurazioneAltroApi = null;
		IDPortaApplicativa idPA = null;
		PortaApplicativa paDefault = null;
		PortaApplicativaServizioApplicativo paSADefault =  null;
		String canalePorta = null;

		if(gestioneErogatori) {
			
			ServletUtils.setObjectIntoSession(this.session, PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE, PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT);
			
			de = new DataElement();
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_TITOLO_PORTE_APPLICATIVE_DATI_INVOCAZIONE);
			de.setType(DataElementType.TEXT);
			String urlInvocazione = "";

			if(!isPddEsterna){
				idPA = this.porteApplicativeCore.getIDPortaApplicativaAssociataDefault(idServizio);
				paDefault = this.porteApplicativeCore.getPortaApplicativa(idPA);
				paSADefault = paDefault.getServizioApplicativoList().get(0);

				paIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, asps.getIdSoggetto() + "");
				paNomePorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA, paDefault.getNome());
				paIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, ""+paDefault.getId());
				paIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, asps.getId()+ "");
				paConfigurazioneDati = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_DATI_INVOCAZIONE, Costanti.CHECK_BOX_ENABLED_TRUE);
				paIdProvider = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_PROVIDER, paDefault.getIdSoggetto() + "");
				paIdPortaPerSA = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_PORTA, ""+paDefault.getId());
				paConnettoreDaListaAPS = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS, Costanti.CHECK_BOX_ENABLED_TRUE);
				paConfigurazioneAltroApi = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_ALTRO_API, Costanti.CHECK_BOX_ENABLED_TRUE);
				
				UrlInvocazioneAPI urlInvocazioneConfig = this.confCore.getConfigurazioneUrlInvocazione(protocollo, RuoloContesto.PORTA_APPLICATIVA, serviceBinding, paDefault.getNome(), 
						new IDSoggetto(paDefault.getTipoSoggettoProprietario(), paDefault.getNomeSoggettoProprietario()),
								as, paDefault.getCanale());
				urlInvocazione = urlInvocazioneConfig.getUrl();
			} else {
				urlInvocazione = "-";
			}
			de.setValue(urlInvocazione);
			List<Parameter> listParametersUrlInvocazione = new ArrayList<>();
			listParametersUrlInvocazione.add(paIdSogg);
			listParametersUrlInvocazione.add(paNomePorta);
			listParametersUrlInvocazione.add(paIdPorta);
			listParametersUrlInvocazione.add(paIdAsps);
			listParametersUrlInvocazione.add(paConfigurazioneDati);
			
			image = new DataElementImage();
			image.setToolTip(MessageFormat.format(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO, PorteApplicativeCostanti.LABEL_PARAMETRO_TITOLO_PORTE_APPLICATIVE_DATI_INVOCAZIONE));
			image.setImage(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
			image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CHANGE, listParametersUrlInvocazione.toArray(new Parameter[1]));
			de.setImage(image);
			dati.addElement(de);
			
			boolean visualizzaConnettore = true;
			boolean checkConnettore = false;
			boolean connettoreMultiploEnabled = false;
			long idConnettore = 1;
			for (int i = 0; i < listaPorteApplicativeAssociate.size(); i++) {
				PortaApplicativa paAssociata = listaPorteApplicativeAssociate.get(i);
				MappingErogazionePortaApplicativa mapping = listaMappingErogazionePortaApplicativa.get(i);
				
				if(!mapping.isDefault()) {
					PortaApplicativaServizioApplicativo portaApplicativaAssociataServizioApplicativo = paAssociata.getServizioApplicativoList().get(0);
					boolean connettoreConfigurazioneRidefinito = this.isConnettoreRidefinito(paDefault, paSADefault, paAssociata, portaApplicativaAssociataServizioApplicativo);
					if(connettoreConfigurazioneRidefinito) {
						visualizzaConnettore = false;
						break;
					}
				} else {
					canalePorta = paAssociata.getCanale();
				}
				
			}
			
			if(visualizzaConnettore) {
				//PortaApplicativaServizioApplicativo paDefautServizioApplicativo = paDefault.getServizioApplicativoList().get(0);
				IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
				idServizioApplicativo.setIdSoggettoProprietario(new IDSoggetto(paDefault.getTipoSoggettoProprietario(), paDefault.getNomeSoggettoProprietario()));
				idServizioApplicativo.setNome(paSADefault.getNome());
				ServizioApplicativo sa = this.saCore.getServizioApplicativo(idServizioApplicativo);
				Connettore connettore = sa.getInvocazioneServizio().getConnettore();
				idConnettore = connettore.getId();
				checkConnettore = org.openspcoop2.pdd.core.connettori.ConnettoreCheck.checkSupported(connettore);
				
				connettoreMultiploEnabled = paDefault.getBehaviour() != null;

				
				de = new DataElement();
				
				de.setType(DataElementType.TEXT);
				InvocazioneServizio is = sa.getInvocazioneServizio();
				String urlConnettore = this.getLabelConnettore(sa,is);
				
				if(!connettoreMultiploEnabled) {	
					de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORE);
					de.setValue(urlConnettore);
					String tooltipConnettore = this.getTooltipConnettore(sa,is);
					de.setToolTip(tooltipConnettore);
				} else {
					de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI);
					de.setValue(this.getNomiConnettoriMultipliPortaApplicativa(paDefault));
					de.setToolTip(this.getToolTipConnettoriMultipliPortaApplicativa(paDefault));
				}
				
				boolean visualizzaLinkConfigurazioneConnettore = !this.core.isConnettoriMultipliEnabled() || ( this.core.isConnettoriMultipliEnabled() && !connettoreMultiploEnabled );
				if(visualizzaLinkConfigurazioneConnettore) {
					List<Parameter> listParametersConnettore = new ArrayList<>();
					listParametersConnettore.add(paIdProvider);
					listParametersConnettore.add(paIdPortaPerSA);
					listParametersConnettore.add(paIdAsps);
					listParametersConnettore.add(new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME_SERVIZIO_APPLICATIVO, paSADefault.getNome()));
					listParametersConnettore.add(new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO, paSADefault.getIdServizioApplicativo()+""));
					listParametersConnettore.add(paConnettoreDaListaAPS);
					
					image = new DataElementImage();
					image.setToolTip(MessageFormat.format(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORE));
					image.setImage(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
					image.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT, 
							listParametersConnettore.toArray(new Parameter[1]));
					de.addImage(image);
				}

				boolean visualizzaLinkCheckConnettore = checkConnettore && (!this.core.isConnettoriMultipliEnabled() || ( this.core.isConnettoriMultipliEnabled() && !connettoreMultiploEnabled ));
				if(visualizzaLinkCheckConnettore) {
					List<Parameter> listParametersVerificaConnettore = new ArrayList<>();
					paIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, asps.getIdSoggetto() + "");
					listParametersVerificaConnettore.add(paIdSogg);
					listParametersVerificaConnettore.add(paIdPorta);
					listParametersVerificaConnettore.add(paIdAsps);
					listParametersVerificaConnettore.add(paConnettoreDaListaAPS);
					listParametersVerificaConnettore.add(new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ID, idConnettore+""));
					listParametersVerificaConnettore.add(new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI, "false"));
					listParametersVerificaConnettore.add(new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO, "false"));
					
					image = new DataElementImage();
					image.setToolTip(MessageFormat.format(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_VERIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO, CostantiControlStation.LABEL_CONFIGURAZIONE_CONNETTIVITA));
					image.setImage(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_VERIFICA_CONFIGURAZIONE);
					image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VERIFICA_CONNETTORE, 
							listParametersVerificaConnettore.toArray(new Parameter[1]));
					de.addImage(image);
				}
				
				// link alla configurazione connettori multipli e alla lista dei connettori multipli
				if(this.core.isConnettoriMultipliEnabled()) {
					List<Parameter> listParametersConfigutazioneConnettoriMultipli = new ArrayList<>();
					paIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, asps.getIdSoggetto() + "");
					listParametersConfigutazioneConnettoriMultipli.add(paIdSogg);
					listParametersConfigutazioneConnettoriMultipli.add(paIdPorta);
					listParametersConfigutazioneConnettoriMultipli.add(paIdAsps);
					listParametersConfigutazioneConnettoriMultipli.add(paConnettoreDaListaAPS);
//					listParametersConfigutazioneConnettoriMultipli.add(new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ID, idConnettore+""));
					listParametersConfigutazioneConnettoriMultipli.add(new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI, "false"));
					listParametersConfigutazioneConnettoriMultipli.add(new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO, "false"));
					listParametersConfigutazioneConnettoriMultipli.add(new Parameter(CostantiControlStation.PARAMETRO_ID_CONN_TAB, "0"));
					
					image = new DataElementImage();
					image.setToolTip(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_CONFIGURAZIONE_CONNETTORI_MULTIPLI_TOOLTIP);
					image.setImage(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_CONFIGURAZIONE_CONNETTORI_MULTIPLI);
					image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONFIGURAZIONE_CONNETTORI_MULTIPLI, 
							listParametersConfigutazioneConnettoriMultipli.toArray(new Parameter[1]));
					de.addImage(image);
					
					if(connettoreMultiploEnabled) {
						image = new DataElementImage();
						image.setToolTip(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_ELENCO_CONNETTORI_MULTIPLI_TOOLTIP);
						image.setImage(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_ELENCO_CONNETTORI_MULTIPLI);
						image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_LIST, 
								listParametersConfigutazioneConnettoriMultipli.toArray(new Parameter[1]));
						de.addImage(image);
					}
				}
				
				dati.addElement(de);
			}
			
			// CORS
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CORS);
			de.setType(DataElementType.TEXT);
			de.setValue(this.getStatoGestioneCorsPortaApplicativa(paDefault, false));
			if(!this.isModalitaCompleta()) {
				this.setStatoGestioneCORS(de, paDefault.getGestioneCors(), this.confCore.getConfigurazioneGenerale());
			}
			paIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, asps.getIdSoggetto() + "");
			
			image = new DataElementImage();
			image.setToolTip(MessageFormat.format(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO, ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CORS));
			image.setImage(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
			image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_GESTIONE_CORS, paIdSogg, paIdPorta, pIdAsps);
			
			de.setImage(image);
			dati.addElement(de);
			
			
			// Canale
			CanaliConfigurazione gestioneCanali = this.confCore.getCanaliConfigurazione(false);
			boolean gestioneCanaliEnabled = gestioneCanali != null && org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO.equals(gestioneCanali.getStato());
			if(gestioneCanaliEnabled) {
				List<CanaleConfigurazione> canaleList = gestioneCanali != null ? gestioneCanali.getCanaleList() : new ArrayList<>();
				de = new DataElement();
				de.setType(DataElementType.TEXT);
				de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CANALE);
				this.setStatoCanalePorta(de, canalePorta, as.getCanale(), canaleList, true);
				
				image = new DataElementImage();
				image.setToolTip(MessageFormat.format(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO, ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CANALE));
				image.setImage(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
				image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_GESTIONE_CANALE, paIdSogg, paIdPorta, pIdAsps);
				de.setImage(image);
				
				dati.addElement(de);
			}
			
			
			// Opzioni Avanzate
			
			if(!this.isModalitaStandard() && this.apsCore.getMessageEngines()!=null && !this.apsCore.getMessageEngines().isEmpty()) {
				de = new DataElement();
				de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_OPZIONI_AVANZATE);
				de.setType(DataElementType.TEXT);
				de.setValue(this.getStatoOpzioniAvanzatePortaApplicativaDefault(paDefault));
				if(!this.isModalitaCompleta()) {
					this.setStatoOpzioniAvanzatePortaApplicativaDefault(de, paDefault.getOptions());
				}
				paIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, asps.getIdSoggetto() + "");
				
				image = new DataElementImage();
				image.setToolTip(MessageFormat.format(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_OPZIONI_AVANZATE));
				image.setImage(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
				image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CHANGE,paIdSogg, paNomePorta, paIdPorta,pIdAsps,paConfigurazioneAltroApi);
				
				de.setImage(image);
				dati.addElement(de);
			}
		}
		
		if(gestioneFruitori) {
			
			ServletUtils.setObjectIntoSession(this.session, PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE, PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT);
			
			IDSoggetto idFruitore = new IDSoggetto(fruitore.getTipo(), fruitore.getNome());
			IDPortaDelegata idPD = this.porteDelegateCore.getIDPortaDelegataAssociataDefault(idServizio, idFruitore);
			PortaDelegata pdDefault = this.porteDelegateCore.getPortaDelegata(idPD);
			canalePorta = pdDefault.getCanale();
				
			Parameter pIdPD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, "" + pdDefault.getId());
			Parameter pNomePD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA, pdDefault.getNome());
			Parameter pIdSoggPD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, pdDefault.getIdSoggetto() + "");
			Parameter pConfigurazioneDati = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_CONFIGURAZIONE_DATI_INVOCAZIONE, Costanti.CHECK_BOX_ENABLED_TRUE);
			Parameter pConnettoreDaListaAPS = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_CONNETTORE_DA_LISTA_APS, Costanti.CHECK_BOX_ENABLED_TRUE);
			Parameter pdConfigurazioneAltroApi = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_CONFIGURAZIONE_ALTRO_API, Costanti.CHECK_BOX_ENABLED_TRUE);
			
			
			// url invocazione
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_TITOLO_PORTE_DELEGATE_DATI_INVOCAZIONE);
			de.setType(DataElementType.TEXT);
			UrlInvocazioneAPI urlInvocazione = this.confCore.getConfigurazioneUrlInvocazione(protocollo, RuoloContesto.PORTA_DELEGATA, serviceBinding, pdDefault.getNome(), idFruitore,
					as, pdDefault.getCanale());
			de.setValue(urlInvocazione.getUrl());
			List<Parameter> listParametersUrlInvocazione = new ArrayList<>();
			listParametersUrlInvocazione.add(pIdPD);
			listParametersUrlInvocazione.add(pNomePD);
			listParametersUrlInvocazione.add(pIdSoggPD);
			listParametersUrlInvocazione.add(pIdAsps);
			listParametersUrlInvocazione.add(pIdFruitore);
			listParametersUrlInvocazione.add(pConfigurazioneDati);
			listParametersUrlInvocazione.add(pTipoSoggettoFruitore);
			listParametersUrlInvocazione.add(pNomeSoggettoFruitore);
			
			image = new DataElementImage();
			image.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CHANGE, listParametersUrlInvocazione.toArray(new Parameter[1]));
			image.setToolTip(MessageFormat.format(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO, PorteDelegateCostanti.LABEL_PARAMETRO_TITOLO_PORTE_DELEGATE_DATI_INVOCAZIONE));
			image.setImage(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
			
			de.setImage(image);
			dati.addElement(de);
			
			
			boolean visualizzaConnettore = true;
			boolean checkConnettore = false;
			long idConnettore = 1;
			
			for (int i = 0; i < listaPorteDelegateAssociate.size(); i++) {
				PortaDelegata pdAssociata = listaPorteDelegateAssociate.get(i);
				MappingFruizionePortaDelegata mapping = listaMappingFruzionePortaDelegata.get(i);

				List<String> listaAzioni = null;
				if(!mapping.isDefault()) {
					listaAzioni = pdAssociata.getAzione().getAzioneDelegataList();
				}
				
				String azioneConnettore =  null;
				if(listaAzioni!=null && listaAzioni.size()>0) {
					azioneConnettore = listaAzioni.get(0);
				}
				
				boolean connettoreConfigurazioneRidefinito = false;
				if(azioneConnettore!=null && !"".equals(azioneConnettore)) {
					for (ConfigurazioneServizioAzione check : fruitore.getConfigurazioneAzioneList()) {
						if(check.getAzioneList().contains(azioneConnettore)) {
							connettoreConfigurazioneRidefinito = true;
							break;
						}
					}
				}
				
				
				if(connettoreConfigurazioneRidefinito) {
					visualizzaConnettore = false;
					break;
				}
			}
			
			if(visualizzaConnettore) {
				org.openspcoop2.core.registry.Connettore connettore = fruitore.getConnettore();
				idConnettore = connettore.getId();
				checkConnettore = org.openspcoop2.pdd.core.connettori.ConnettoreCheck.checkSupported(connettore);
			}

			// Connettore
			if(visualizzaConnettore) {
				de = new DataElement();
				de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE);
				de.setType(DataElementType.TEXT);
				org.openspcoop2.core.registry.Connettore connettore = fruitore.getConnettore();
				String urlConnettore = this.getLabelConnettore(connettore);
				
				// Controllo se richiedere il connettore
				boolean connettoreStatic = false;
				if(gestioneFruitori) {
					connettoreStatic = this.apsCore.isConnettoreStatic(protocollo);
				}
				
				if(!connettoreStatic) {
					
					de.setValue(urlConnettore);
					de.setToolTip(urlConnettore);
					
					List<Parameter> listParametersConnettore = new ArrayList<>();
					listParametersConnettore.add(pId);
					listParametersConnettore.add(pIdFruitore);
					listParametersConnettore.add(pIdSoggettoErogatore);
					listParametersConnettore.add(pIdProviderFruitore);
					listParametersConnettore.add(pConnettoreDaListaAPS);
					listParametersConnettore.add(pTipoSoggettoFruitore);
					listParametersConnettore.add(pNomeSoggettoFruitore);
					image = new DataElementImage();
					image.setToolTip(MessageFormat.format(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE));
					image.setImage(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
					image.setUrl(
							AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE,
							listParametersConnettore.toArray(new Parameter[1])
							);
					de.addImage(image);
					
					if(checkConnettore) {
						List<Parameter> listParametersVerificaConnettore = new ArrayList<>();
						listParametersVerificaConnettore.add(pIdPD);
						listParametersVerificaConnettore.add(pIdFruitore);
						listParametersVerificaConnettore.add(pIdSoggettoErogatore);
						listParametersVerificaConnettore.add(pIdProviderFruitore);
						listParametersVerificaConnettore.add(pConnettoreDaListaAPS);
						listParametersVerificaConnettore.add(pTipoSoggettoFruitore);
						listParametersVerificaConnettore.add(pNomeSoggettoFruitore);
						listParametersVerificaConnettore.add(pIdSoggPD);
						listParametersVerificaConnettore.add(pIdAsps);
						listParametersVerificaConnettore.add(new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ID, idConnettore+""));
						listParametersVerificaConnettore.add(new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI, "false"));
						listParametersVerificaConnettore.add(new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO, "true"));
						image = new DataElementImage();
						
						image.setToolTip(MessageFormat.format(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_VERIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO, CostantiControlStation.LABEL_CONFIGURAZIONE_CONNETTIVITA));
						image.setImage(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_VERIFICA_CONFIGURAZIONE);
						image.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_VERIFICA_CONNETTORE, 
								listParametersVerificaConnettore.toArray(new Parameter[1]));
						
						de.addImage(image);
					}
				}
				else {
					de.setValue("-");
				}
				
				dati.addElement(de);
			}
			
			// CORS
			de = new DataElement();
			de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CORS);
			de.setType(DataElementType.TEXT);
			de.setValue(this.getStatoGestioneCorsPortaDelegata(pdDefault, false)); 
			if(!this.isModalitaCompleta()) {
				this.setStatoGestioneCORS(de, pdDefault.getGestioneCors(), this.confCore.getConfigurazioneGenerale());
			}
			image = new DataElementImage();
			image.setToolTip(MessageFormat.format(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO, ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CORS));
			image.setImage(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
			image.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_GESTIONE_CORS, pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore);
			de.setImage(image);
			dati.addElement(de);
			
			// Canale
			CanaliConfigurazione gestioneCanali = this.confCore.getCanaliConfigurazione(false);
			boolean gestioneCanaliEnabled = gestioneCanali != null && org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO.equals(gestioneCanali.getStato());
			if(gestioneCanaliEnabled) {
				List<CanaleConfigurazione> canaleList = gestioneCanali != null ? gestioneCanali.getCanaleList() : new ArrayList<>();
				de = new DataElement();
				de.setType(DataElementType.TEXT);
				de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CANALE);
				this.setStatoCanalePorta(de, canalePorta, as.getCanale(), canaleList, false);
				
				image = new DataElementImage();
				image.setToolTip(MessageFormat.format(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO, ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CANALE));
				image.setImage(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
				image.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_GESTIONE_CANALE, pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore);
				de.setImage(image);
				
				dati.addElement(de);
			}
			
			
			// Opzioni Avanzate
			
			if(!this.isModalitaStandard() && this.apsCore.getMessageEngines()!=null && !this.apsCore.getMessageEngines().isEmpty()) {
				de = new DataElement();
				de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_OPZIONI_AVANZATE);
				de.setType(DataElementType.TEXT);
				de.setValue(this.getStatoOpzioniAvanzatePortaDelegataDefault(pdDefault));
				if(!this.isModalitaCompleta()) {
					this.setStatoOpzioniAvanzatePortaDelegataDefault(de, pdDefault.getOptions());
				}
				image = new DataElementImage();
				image.setToolTip(MessageFormat.format(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_OPZIONI_AVANZATE));
				image.setImage(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
				image.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CHANGE,pIdPD,pNomePD,pIdSoggPD, pIdAsps, pIdFruitore, pdConfigurazioneAltroApi);
				de.setImage(image);
				dati.addElement(de);
			}
		}
		

		Parameter pGruppiTrue = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_GRUPPI,"true");
		Parameter pGruppiFalse = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_GRUPPI,"false");
		
		Parameter pConfigurazioneTrue =new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_CONFIGURAZIONI,"true");
		Parameter pConfigurazioneFalse =new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_CONFIGURAZIONI,"false");
		

		
		
		boolean visualizzazioneTabs = !this.isModalitaCompleta();
		
		Parameter pIdTab = new Parameter(CostantiControlStation.PARAMETRO_ID_TAB, "0");
		
		// configurazioni
		de = new DataElement();
		de.setType(DataElementType.LINK);
		if(gestioneErogatori)
			de.setUrl(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_PORTE_APPLICATIVE_LIST, 
					pId, pNomeServizio, pTipoServizio, pIdSoggettoErogatore,pConfigurazioneTrue,pGruppiFalse,pIdTab);
		if(gestioneFruitori)
			de.setUrl(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_PORTE_DELEGATE_LIST,	
					pId, pIdSogg, pIdSoggettoErogatore, pNomeServizio, pTipoServizio, pIdFruitore,pConfigurazioneTrue,pGruppiFalse,pTipoSoggettoFruitore,pNomeSoggettoFruitore,pIdTab);
		if(visualizzazioneTabs)
			de.setValue(ErogazioniCostanti.LABEL_ASPS_GESTIONE_CONFIGURAZIONI_CONFIGURA);			
		else
			de.setValue(ErogazioniCostanti.LABEL_ASPS_GESTIONE_CONFIGURAZIONI);
		de.setIcon(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_GESTIONE_CONFIGURAZIONI);
		dati.addElement(de);
		
		if(!visualizzazioneTabs) {
			// Gestione Gruppi
			de = new DataElement();
			de.setType(DataElementType.LINK);
			if(gestioneErogatori)
				de.setUrl(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_PORTE_APPLICATIVE_LIST, 
						pId, pNomeServizio, pTipoServizio, pIdSoggettoErogatore, pConfigurazioneFalse, pGruppiTrue);
			if(gestioneFruitori)
				de.setUrl(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_PORTE_DELEGATE_LIST,	
						pId, pIdSogg, pIdSoggettoErogatore, pNomeServizio, pTipoServizio, pIdFruitore, pConfigurazioneFalse, pGruppiTrue,pTipoSoggettoFruitore,pNomeSoggettoFruitore);
			de.setValue(MessageFormat.format(ErogazioniCostanti.LABEL_ASPS_GESTIONE_GRUPPI_CON_PARAMETRO, this.getLabelAzioni(serviceBinding)));
			de.setIcon(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_GESTIONE_GRUPPI_CON_PARAMETRO);
			dati.addElement(de);
		}
		
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
				de.setValue("Gruppi");
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
//							StringBuilder sb = new StringBuilder();
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
			Parameter pIdFruitore, AccordoServizioParteSpecifica asps, AccordoServizioParteComuneSintetico as)	throws Exception {

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
					String statoControlloAccessi = this.getStatoControlloAccessiPortaApplicativa(this.apsCore.getProtocolloAssociatoTipoServizio(asps.getTipo()), paAssociata);

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
					String statoDump = this.getStatoDumpPortaApplicativa(paAssociata, true);
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
						de.setValue(this.getStatoDumpRichiestaPortaApplicativa(paAssociata, true));
						gruppoVector.addElement(de);

						de = new DataElement();
						de.setType(DataElementType.TEXT);
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_RISPOSTA);				
						de.setValue(this.getStatoDumpRispostaPortaApplicativa(paAssociata, true));
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

							ServizioApplicativo sa = this.saCore.getServizioApplicativo(portaApplicativaAssociataServizioApplicativo.getIdServizioApplicativo());
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
					de.setIcon(ErogazioniCostanti.LABEL_ASPS_ABILITA_CONFIGURAZIONE);
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
					de.setIcon(ErogazioniCostanti.LABEL_ASPS_ABILITA_CONFIGURAZIONE);
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
	
	public void prepareErogazioneChange(TipoOperazione tipoOp, AccordoServizioParteSpecifica asps, IDSoggetto idSoggettoFruitore) throws Exception {
		
		String tipologia = ServletUtils.getObjectFromSession(this.session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
		boolean gestioneFruitori = false;
		boolean gestioneErogatori = false;
		if(tipologia!=null) {
			if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
				gestioneFruitori = true;
			}
			else if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia)) {
				gestioneErogatori = true;
			}
		}
		
		IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
		String tipoProtocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(asps.getTipoSoggettoErogatore());
		AccordoServizioParteComuneSintetico as = this.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
		ServiceBinding serviceBinding = this.apcCore.toMessageServiceBinding(as.getServiceBinding());
		
		String tmpTitle = null;
		if(gestioneFruitori) {
			tmpTitle = this.getLabelServizioFruizione(tipoProtocollo, idSoggettoFruitore, idServizio);
		}
		else {
			tmpTitle = this.getLabelServizioErogazione(tipoProtocollo, idServizio);
		}
						
		List<MappingErogazionePortaApplicativa> listaMappingErogazionePortaApplicativa = new ArrayList<>();
		List<PortaApplicativa> listaPorteApplicativeAssociate = new ArrayList<>();
		if(gestioneErogatori) {
			// lettura delle configurazioni associate
			listaMappingErogazionePortaApplicativa = this.apsCore.mappingServiziPorteAppList(idServizio,asps.getId(), null);
			for(MappingErogazionePortaApplicativa mappinErogazione : listaMappingErogazionePortaApplicativa) {
				listaPorteApplicativeAssociate.add(this.porteApplicativeCore.getPortaApplicativa(mappinErogazione.getIdPortaApplicativa()));
			}
		}

		List<MappingFruizionePortaDelegata> listaMappingFruzionePortaDelegata = new ArrayList<>();
		List<PortaDelegata> listaPorteDelegateAssociate = new ArrayList<>();
		Fruitore fruitore = null;
		if(gestioneFruitori) {
			// In questa modalità ci deve essere un fruitore indirizzato
			for (Fruitore check : asps.getFruitoreList()) {
				if(check.getTipo().equals(idSoggettoFruitore.getTipo()) && check.getNome().equals(idSoggettoFruitore.getNome())) {
					fruitore = check;
					break;
				}
			}
			listaMappingFruzionePortaDelegata = this.apsCore.serviziFruitoriMappingList(fruitore.getId(), idSoggettoFruitore, idServizio, null);	
			for(MappingFruizionePortaDelegata mappingFruizione : listaMappingFruzionePortaDelegata) {
				listaPorteDelegateAssociate.add(this.porteDelegateCore.getPortaDelegata(mappingFruizione.getIdPortaDelegata()));
			}
		}
		
		this.makeMenu();
		
		// setto la barra del titolo
		List<Parameter> lstParm = new ArrayList<Parameter>();

		if(gestioneFruitori) {
			lstParm.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_FRUIZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
		}
		else {
			lstParm.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_EROGAZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
		}
		lstParm.add(new Parameter(tmpTitle, null));

		// setto la barra del titolo
		ServletUtils.setPageDataTitle(this.pd, lstParm );

		Vector<Vector<DataElement>> datiPagina = new Vector<Vector<DataElement>>();
		Vector<DataElement> dati = new Vector<DataElement>();
		datiPagina.add(dati);
		dati.addElement(ServletUtils.getDataElementForEditModeFinished());

		String fromAPIPageInfo = this.getParameter(CostantiControlStation.PARAMETRO_API_PAGE_INFO);
		boolean fromApi = Costanti.CHECK_BOX_ENABLED_TRUE.equalsIgnoreCase(fromAPIPageInfo);
		if(fromApi) {
			DataElement de = new DataElement();
			de.setValue(fromAPIPageInfo);
			de.setType(DataElementType.HIDDEN);
			de.setName(CostantiControlStation.PARAMETRO_API_PAGE_INFO);
			dati.addElement(de);
			
			String nomePorta = this.getParameter(CostantiControlStation.PARAMETRO_NOME_PORTA);
			de = new DataElement();
			de.setValue(nomePorta);
			de.setType(DataElementType.HIDDEN);
			de.setName(CostantiControlStation.PARAMETRO_NOME_PORTA);
			dati.addElement(de);
			
			String abilita = this.getParameter(CostantiControlStation.PARAMETRO_ABILITA);
			de = new DataElement();
			de.setValue(abilita);
			de.setType(DataElementType.HIDDEN);
			de.setName(CostantiControlStation.PARAMETRO_ABILITA);
			dati.addElement(de);
			
			String idsogg = this.getParameter(CostantiControlStation.PARAMETRO_ID_SOGGETTO);
			String id = this.getParameter(CostantiControlStation.PARAMETRO_ID);
			
			String idFruizione = null;
			String tipoSoggettoFruitore = null;
			String nomeSoggettoFruitore = null;
			if(fruitore!=null) {
				idFruizione = fruitore.getId()+"";
				tipoSoggettoFruitore = fruitore.getTipo();
				nomeSoggettoFruitore = fruitore.getNome();
			}
			
			dati = this.addHiddenFieldsToDati(tipoOp, id, idsogg, id, asps.getId()+"", 
					idFruizione, tipoSoggettoFruitore, nomeSoggettoFruitore,
					dati);
		}
		else {
			dati = this.addHiddenFieldsToDati(tipoOp, asps.getId()+"", null, null, dati);
		}
		
		datiPagina = this.addErogazioneToDati(datiPagina, tipoOp, asps, as, tipoProtocollo, serviceBinding, gestioneErogatori, gestioneFruitori, listaMappingErogazionePortaApplicativa, listaPorteApplicativeAssociate, listaMappingFruzionePortaDelegata, listaPorteDelegateAssociate, fruitore);

		this.pd.setDati(datiPagina);
		this.pd.disableEditMode();
	}
	
}
