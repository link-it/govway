/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
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
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.costanti.InUsoType;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneUtilities;
import org.openspcoop2.web.ctrlstat.servlet.apc.api.ApiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaUtilities;
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
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
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

	public void checkGestione(HttpServletRequest request, HttpSession session, ConsoleSearch ricerca, int idLista, String tipologiaParameterName) throws Exception {
		this.checkGestione(request, session, ricerca, idLista, tipologiaParameterName, false);
	}

	public void checkGestione(HttpServletRequest request, HttpSession session, ConsoleSearch ricerca, int idLista, String tipologiaParameterName, boolean addFilterToRicerca) throws Exception { 
		String tipologia = this.getParameter(tipologiaParameterName);
		if(tipologia==null) {
			// guardo se sto entrando da altri link fuori dal menu di sinistra
			// in tal caso e' gia' impostato
			tipologia = ServletUtils.getObjectFromSession(request, session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
		}

		if(tipologia!=null) {
			if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia)) {
				ServletUtils.setObjectIntoSession(request, session, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
				if(addFilterToRicerca)
					ricerca.addFilter(idLista, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE);
			}
			else if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
				ServletUtils.setObjectIntoSession(request, session, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
				
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
				ServletUtils.removeObjectFromSession(request, session, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
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
			tipologia = ServletUtils.getObjectFromSession(this.request, this.session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
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
		int numeroConfigurazioniSchedulingDisabilitato = 0;
		boolean allActionRedefined = false;
		String msgControlloAccessiMalConfigurato = null;
		// stato gruppi
		numeroConfigurazioni = listaMappingErogazionePortaApplicativa.size();
		
		if(listaMappingErogazionePortaApplicativa.size()>1) {
			List<String> azioniL = new ArrayList<>();
			Map<String,String> azioni = this.apsCore.getAzioniConLabel(asps, apc, false, true, new ArrayList<>());
			if(azioni != null && azioni.size() > 0)
				azioniL.addAll(azioni.keySet());
			allActionRedefined = this.allActionsRedefinedMappingErogazione(azioniL, listaMappingErogazionePortaApplicativa);
		}
		
		for (PortaApplicativa paAssociata : listaPorteApplicativeAssociate) {
			
			String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(paAssociata.getTipoSoggettoProprietario());
			boolean modipa = this.isProfiloModIPA(protocollo);
			
			boolean statoPA = paAssociata.getStato().equals(StatoFunzionalita.ABILITATO);
			if(statoPA) {
				if(!allActionRedefined || !paAssociata.getNome().equals(nomePortaDefault)) {
					numeroAbilitate ++;
				}
			}
			
			if(allActionRedefined && paAssociata.getNome().equals(nomePortaDefault)) {
				// se tutte le azioni sono state ridefinite non devo controllare la porta di default
				continue;
			}
			
			if(StatoFunzionalita.ABILITATO.equals(paAssociata.getStato()) && msgControlloAccessiMalConfigurato==null){
				if(TipoAutorizzazione.isAuthenticationRequired(paAssociata.getAutorizzazione())) {
					if( 
							(paAssociata.getSoggetti()==null || paAssociata.getSoggetti().sizeSoggettoList()<=0) 
							&&
							(paAssociata.getServiziApplicativiAutorizzati()==null || paAssociata.getServiziApplicativiAutorizzati().sizeServizioApplicativoList()<=0)
							) {
						if(!modipa) {
							msgControlloAccessiMalConfigurato = ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_TRASPORTO_NO_FRUITORI;
						}
						else {
							msgControlloAccessiMalConfigurato = ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_CANALE_NO_FRUITORI;
						}
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
						if(!modipa) {
							msgControlloAccessiMalConfigurato = ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_TRASPORTO_NO_RUOLI;
						}
						else {
							msgControlloAccessiMalConfigurato = ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_CANALE_NO_RUOLI;
						}
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
						paAssociata.getAutorizzazioneToken()!=null && StatoFunzionalita.ABILITATO.equals(paAssociata.getAutorizzazioneToken().getAutorizzazioneApplicativi())) {
					if(modipa) {
						if (paAssociata.getServiziApplicativiAutorizzati()==null || paAssociata.getServiziApplicativiAutorizzati().sizeServizioApplicativoList()<=0) {
							msgControlloAccessiMalConfigurato = ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_MESSAGGIO_NO_FRUITORI;
						}
					}
					else if( 
							paAssociata.getAutorizzazioneToken().getServiziApplicativi()==null || 	(paAssociata.getAutorizzazioneToken().getServiziApplicativi().sizeServizioApplicativoList()<=0) 
						) {
						msgControlloAccessiMalConfigurato = ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_TOKEN_NO_FRUITORI;
					}
					if(msgControlloAccessiMalConfigurato!=null) {
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
						paAssociata.getAutorizzazioneToken()!=null && StatoFunzionalita.ABILITATO.equals(paAssociata.getAutorizzazioneToken().getAutorizzazioneRuoli())) {
					if( 
							paAssociata.getAutorizzazioneToken().getRuoli()==null || 	(paAssociata.getAutorizzazioneToken().getRuoli().sizeRuoloList()<=0) 
						) {
						if(!modipa) {
							msgControlloAccessiMalConfigurato = ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_TOKEN_NO_RUOLI;
						}
						else {
							msgControlloAccessiMalConfigurato = ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_MESSAGGIO_NO_RUOLI;
						}
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
						msgControlloAccessiMalConfigurato = ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_TOKEN_NO_SCOPE;
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
				
				if(paAssociata.getBehaviour()!=null && paAssociata.sizeServizioApplicativoList()>0) {
					for (PortaApplicativaServizioApplicativo paSA : paAssociata.getServizioApplicativoList()) {
						if(paSA!=null && paSA.getDatiConnettore()!=null && 
								StatoFunzionalita.DISABILITATO.equals(paSA.getDatiConnettore().getScheduling()) &&
								!StatoFunzionalita.DISABILITATO.equals(paSA.getDatiConnettore().getStato())) {
							numeroConfigurazioniSchedulingDisabilitato++;
						}
					}
				}
			}
		}
		
		return newDataElementStatoApi(de, setWidthPx, msgControlloAccessiMalConfigurato, null, numeroAbilitate, numeroConfigurazioni, numeroConfigurazioniSchedulingDisabilitato, allActionRedefined);
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
			Map<String,String> azioni = this.porteDelegateCore.getAzioniConLabel(asps, apc, false, true, new ArrayList<>());
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
			
			if(allActionRedefined && pdAssociata.getNome().equals(nomePortaDefault)) {
				// se tutte le azioni sono state ridefinite non devo controllare la porta di default
				continue;
			}
			
			if(StatoFunzionalita.ABILITATO.equals(pdAssociata.getStato()) && msgControlloAccessiMalConfigurato==null){
				if(TipoAutorizzazione.isAuthenticationRequired(pdAssociata.getAutorizzazione())) {
					if( 
							(pdAssociata.sizeServizioApplicativoList()<=0)
							) {
						msgControlloAccessiMalConfigurato = ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_TRASPORTO_NO_FRUITORI;
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
						msgControlloAccessiMalConfigurato = ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_TRASPORTO_NO_RUOLI;
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
						pdAssociata.getAutorizzazioneToken()!=null && StatoFunzionalita.ABILITATO.equals(pdAssociata.getAutorizzazioneToken().getAutorizzazioneApplicativi())) {
					if( 
							pdAssociata.getAutorizzazioneToken().getServiziApplicativi()==null || 	(pdAssociata.getAutorizzazioneToken().getServiziApplicativi().sizeServizioApplicativoList()<=0) 
						) {
						msgControlloAccessiMalConfigurato = ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_TOKEN_NO_FRUITORI;
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
						pdAssociata.getAutorizzazioneToken()!=null && StatoFunzionalita.ABILITATO.equals(pdAssociata.getAutorizzazioneToken().getAutorizzazioneRuoli())) {
					if( 
							pdAssociata.getAutorizzazioneToken().getRuoli()==null || (pdAssociata.getAutorizzazioneToken().getRuoli().sizeRuoloList()<=0) 
						) {
						msgControlloAccessiMalConfigurato = ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_TOKEN_NO_RUOLI;
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
						msgControlloAccessiMalConfigurato = ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_TOKEN_NO_SCOPE;
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
		
		return newDataElementStatoApi(de, setWidthPx, msgControlloAccessiMalConfigurato, null, numeroAbilitate, numeroConfigurazioni, -1, allActionRedefined);
	}
	
	private DataElement newDataElementStatoApi(DataElement deParam, boolean setWidthPx, String msgControlloAccessiMalConfiguratoError,  String msgControlloAccessiMalConfiguratoWarning, 
			int numeroAbilitate, int numeroConfigurazioni, int numeroConfigurazioniSchedulingDisabilitato, 
			boolean allActionRedefined) {
		
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
			
			if(numeroConfigurazioniSchedulingDisabilitato>0) {
				de.setStatusType(CheckboxStatusType.WARNING_ONLY);
				de.setStatusToolTip(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONNETTORI_MULTIPLI_SCHEDULING_DISABILITATO_TOOLTIP);
			}
			else {
				de.setStatusType(CheckboxStatusType.ABILITATO);
				de.setStatusToolTip(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_TUTTE_ABILITATE_TOOLTIP);
			}
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

			ServletUtils.addListElementIntoSession(this.request, this.session, ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI);

			String tipologia = ServletUtils.getObjectFromSession(this.request, this.session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			boolean gestioneFruitori = this.isGestioneFruitori(tipologia);
			boolean visualizzaGruppi = false;

			if(gestioneFruitori)
				this.pd.setCustomListViewName(ErogazioniCostanti.ASPS_EROGAZIONI_NOME_VISTA_CUSTOM_LISTA_FRUIZIONI);
			else 
				this.pd.setCustomListViewName(ErogazioniCostanti.ASPS_EROGAZIONI_NOME_VISTA_CUSTOM_LISTA_EROGAZIONI);

			boolean showProtocolli = this.core.countProtocolli(this.request, this.session)>1;
			boolean showServiceBinding = true;
			if( !showProtocolli ) {
				List<String> l = this.core.getProtocolli(this.request, this.session);
				if(!l.isEmpty()) {
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
				List<String> protocolli = this.core.getProtocolli(this.request, this.session);
				if(protocolli!=null && protocolli.size()==1) {
					protocolloS = protocolli.get(0);
				}
			}
			if( (filterProtocollo!=null && 
					//!"".equals(filterProtocollo) &&
					!CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROTOCOLLO_QUALSIASI.equals(filterProtocollo))
					||
				(filterProtocollo==null && protocolloS!=null)
					) {
				profiloSelezionato = true;
			}
			
			String protocolloPerFiltroProprieta = protocolloS;
			// valorizzato con il protocollo nel menu in alto a destra oppure null, controllo se e' stato selezionato nel filtro di ricerca
			if(protocolloPerFiltroProprieta == null) {
				if(
						//"".equals(filterProtocollo) || 
						CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROTOCOLLO_QUALSIASI.equals(filterProtocollo)) {
					protocolloPerFiltroProprieta = null;
				} else {
					protocolloPerFiltroProprieta = filterProtocollo;
				}
			}
			
			String soggettoPerFiltroProprieta = null;
			if(profiloSelezionato) {
				// soggetto non selezionato nel menu' in alto a dx
				if(!this.isSoggettoMultitenantSelezionato()) {
					soggettoPerFiltroProprieta = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_SOGGETTO);
					if(
							//"".equals(soggettoPerFiltroProprieta) || 
							CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SOGGETTO_QUALSIASI.equals(soggettoPerFiltroProprieta)) {
						soggettoPerFiltroProprieta = null;
					}
				} else {
					soggettoPerFiltroProprieta = this.getSoggettoMultitenantSelezionato();
				}
			}
			
			// filtri MODIPA da visualizzare solo se non e' stato selezionato un protocollo in alto a dx  (opzione pilota da file di proprieta')
			// oppure e' selezionato MODIPA
			// oppure non e' stato selezionato un protocollo in alto e nessun protocollo nei filtri  (opzione pilota da file di proprieta')
			// oppure MODIPA nei filtri
			boolean profiloModipaSelezionato = false;
			// solo se il protocollo modipa e' caricato faccio la verifica
			if(this.core.getProtocolli().contains(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROTOCOLLO_MODIPA)) {
				List<String> profiloModipaSelezionatoOpzioniAccettate = new ArrayList<>();
				profiloModipaSelezionatoOpzioniAccettate.add(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROTOCOLLO_MODIPA);
				if(this.core.isModipaFiltroRicercaProfiloQualsiasiVisualizzaDatiModi()) {
					profiloModipaSelezionatoOpzioniAccettate.add(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROTOCOLLO_QUALSIASI);
				}
				if( (filterProtocollo!=null && profiloModipaSelezionatoOpzioniAccettate.contains(filterProtocollo))
						||
					(filterProtocollo==null && protocolloS!=null && profiloModipaSelezionatoOpzioniAccettate.contains(protocolloS))
					) {
					profiloModipaSelezionato = true;
				}
			}
			
			if( profiloSelezionato && 
					(!this.isSoggettoMultitenantSelezionato())) {
				String filterSoggetto = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_SOGGETTO);
				this.addFilterSoggetto(filterSoggetto,protocolloS,true,true);
			}
						
			String filterTipoAccordo = null;
			if(showServiceBinding) {
				filterTipoAccordo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_SERVICE_BINDING);
				boolean postBackServiceBinding = profiloSelezionato // serve se poi si fa vedere le API
						||
						profiloModipaSelezionato; // serve per pilotare la label audience e sulla sicurezza messaggio
				this.addFilterServiceBinding(filterTipoAccordo,postBackServiceBinding,true);
			}
			
			String filterGruppo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_GRUPPO);
			boolean postBackGruppo = profiloSelezionato; // serve se poi si fa vedere le API
			addFilterGruppo(filterProtocollo, filterGruppo, postBackGruppo);

			if( profiloSelezionato ) {
				String filterApi = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_API);
				addFilterApi(filterProtocollo, filterTipoAccordo,filterGruppo, filterApi, false);
			}
			
			CanaliConfigurazione canali = this.confCore.getCanaliConfigurazione(false);
			if(canali!=null && StatoFunzionalita.ABILITATO.equals(canali.getStato())) {
				String filterCanale = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CANALE);
				addFilterCanale(canali, filterCanale, false);
			}
			
			if(this.isShowGestioneWorkflowStatoDocumenti() &&
				this.core.isGestioneWorkflowStatoDocumenti_visualizzaStatoLista()) {
				String filterStatoAccordo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_STATO_ACCORDO);
				this.addFilterStatoAccordo(filterStatoAccordo,false);
			}
			
			if(gestioneFruitori) {
				addFilterSoggettoErogatoreStringaLiberaContains(ricerca, idLista);
			}
			
						
			// **** filtro connettore ****
			
			this.addFilterSubtitle(ConnettoriCostanti.NAME_SUBTITLE_DATI_CONNETTORE, ConnettoriCostanti.LABEL_SUBTITLE_DATI_CONNETTORE, false);
			
			// filtro tipo connettore con voce IM solo sulle erogazioni
			String filterTipoConnettore = this.addFilterTipoConnettore(ricerca, idLista, !gestioneFruitori);
			
			// filtro plugin
			this.addFilterConnettorePlugin(ricerca, idLista, filterTipoConnettore);
			
			// filtro debug
			if(!this.isModalitaStandard()) {
				this.addFilterConnettoreDebug(ricerca, idLista, filterTipoConnettore);
			}
			
			// filtro token policy
			this.addFilterConnettoreTokenPolicy(ricerca, idLista, filterTipoConnettore);
			
			// filtro endpoint
			this.addFilterConnettoreEndpoint(ricerca, idLista, filterTipoConnettore);
			
			// filtro keystore
			this.addFilterConnettoreKeystore(ricerca, idLista, filterTipoConnettore);
						
			// imposto apertura sezione
			this.impostaAperturaSubtitle(ConnettoriCostanti.NAME_SUBTITLE_DATI_CONNETTORE);
						
			// **** fine filtro connettore ****
			
			
			// **** filtro modi ****
			
			if(profiloModipaSelezionato) {
				this.addFilterSubtitle(CostantiControlStation.NAME_SUBTITLE_FILTRI_MODIPA, CostantiControlStation.LABEL_SUBTITLE_FILTRI_MODIPA, false);
				
				// filtro sicurezza canale
				this.addFilterModISicurezzaCanale(ricerca, idLista);
				
				// filtro sicurezza messaggio
				this.addFilterModISicurezzaMessaggio(ricerca, idLista, filterTipoAccordo);
				
				// filtro sorgente token
				this.addFilterModIGenerazioneToken(ricerca, idLista);
				
				// filtro sicurezza canale
				this.addFilterModIDigestRichiesta(ricerca, idLista);
				
				// filtro sicurezza canale
				this.addFilterModIInfoUtente(ricerca, idLista);
				
				// filtro keystore
				this.addFilterModIKeystorePath(ricerca, idLista);
				
				// filtro audience
				this.addFilterModIAudience(ricerca, idLista, false, filterTipoAccordo, null);
				
				// imposto apertura sezione
				this.impostaAperturaSubtitle(CostantiControlStation.NAME_SUBTITLE_FILTRI_MODIPA);
			}
			
			// **** fine filtro modi ****

			
			// **** filtro configurazione ****
			
			this.addFilterSubtitle(CostantiControlStation.NAME_SUBTITLE_DATI_CONFIGURAZIONE, CostantiControlStation.LABEL_SUBTITLE_DATI_CONFIGURAZIONE, false);
			
			// filtro stato
			this.addFilterStatoImplementazioneAPI(ricerca, idLista);
			
			// filtro autenticazione token
			this.addFilterTipoAutenticazioneToken(ricerca, idLista);
			
			// filtro autenticazione trasporto
			boolean modiErogazione = profiloModipaSelezionato && !gestioneFruitori;
			Boolean confPers = ServletUtils.getObjectFromSession(this.request, this.session, Boolean.class, CostantiControlStation.SESSION_PARAMETRO_GESTIONE_CONFIGURAZIONI_PERSONALIZZATE);
			String filterTipoAutenticazioneTrasporto = this.addFilterTipoAutenticazioneTrasporto(ricerca, idLista, true, modiErogazione, confPers);
			
			// filtro plugin autenticazione trasporto
			this.addFilterTipoAutenticazioneTrasportoPlugin(ricerca, idLista, filterTipoAutenticazioneTrasporto, gestioneFruitori);
			
			// filtro stato rate limiting
			this.addFilterConfigurazioneRateLimiting(ricerca, idLista);
			
			// filtro stato validazione
			this.addFilterConfigurazioneValidazione(ricerca, idLista);
			
			// filtro stato caching risposta
			this.addFilterConfigurazioneCacheRisposta(ricerca, idLista);
			
			// filtro stato message security
			this.addFilterConfigurazioneMessageSecurity(ricerca, idLista);
			
			// filtro stato mtom
			this.addFilterConfigurazioneMTOM(ricerca, idLista, filterTipoAccordo);
			
			// filtro trasformazione
			this.addFilterConfigurazioneTrasformazione(ricerca, idLista);
			
			// filtro correlazione applicativa
			this.addFilterConfigurazioneCorrelazioneApplicativa(ricerca, idLista);
			
			// filtro registrazione messaggi
			this.addFilterConfigurazioneTipoDump(ricerca, idLista, false);
			
			// filtro cors
			String filterCORS = this.addFilterConfigurazioneCORS(ricerca, idLista);
			
			// filtro cors origin
			this.addFilterConfigurazioneCORSOrigin(ricerca, idLista, filterCORS);
			
			// imposto apertura sezione
			this.impostaAperturaSubtitle(CostantiControlStation.NAME_SUBTITLE_DATI_CONFIGURAZIONE);
			
			// **** fine filtro configurazione ****
			
			
			// **** filtro proprieta ****
			
			List<String> nomiProprieta =null;
			if(gestioneFruitori) {
				nomiProprieta = this.nomiProprietaPD(protocolloPerFiltroProprieta,soggettoPerFiltroProprieta);
			} else {
				nomiProprieta = this.nomiProprietaPA(protocolloPerFiltroProprieta,soggettoPerFiltroProprieta);
			}
			
			if(nomiProprieta != null && !nomiProprieta.isEmpty()) {
				this.addFilterSubtitle(CostantiControlStation.NAME_SUBTITLE_PROPRIETA, CostantiControlStation.LABEL_SUBTITLE_PROPRIETA, false);
				
				// filtro nome
				this.addFilterProprietaNome(ricerca, idLista, nomiProprieta);
				
				// filtro valore
				this.addFilterProprietaValore(ricerca, idLista, nomiProprieta);
				
				// imposto apertura sezione
				this.impostaAperturaSubtitle(CostantiControlStation.NAME_SUBTITLE_PROPRIETA);
			}
			
			// **** fine filtro proprieta ****
			

			boolean showConfigurazionePA = false;
			boolean showConfigurazionePD = false;

			if(tipologia==null) {
				throw new DriverControlStationException("Parametro TipologiaErogazione non puo' essere vuoto");
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
			List<Parameter> lstParm = new ArrayList<>();

			if(gestioneFruitori) {
				lstParm.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_FRUIZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
			} else {
				lstParm.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_EROGAZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
			}
			
			if(gestioneFruitori) {
				this.pd.setSearchLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_RICERCA_API_FRUIZIONE);
			}
			else {
				this.pd.setSearchLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_RICERCA_API_EROGAZIONE);
			}
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

			List<AccordoServizioParteComuneSintetico> listApc = new ArrayList<>();
			List<String> protocolli = new ArrayList<>();
			
			// colleziono i tags registrati
			List<String> tagsDisponibili = this.gruppiCore.getAllGruppiOrdinatiPerDataRegistrazione();
			
			// configurazione dei canali
			CanaliConfigurazione gestioneCanali = this.confCore.getCanaliConfigurazione(false);
			boolean gestioneCanaliEnabled = gestioneCanali != null && org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO.equals(gestioneCanali.getStato());
			List<CanaleConfigurazione> canaleList = gestioneCanali != null ? gestioneCanali.getCanaleList() : new ArrayList<>();
			CanaleConfigurazione canaleConfigurazioneDefault = gestioneCanaliEnabled ? this.getCanaleDefault(canaleList) : null;

			if(lista!=null) {
				for (AccordoServizioParteSpecifica asps : lista) {
					AccordoServizioParteComuneSintetico apc = this.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
					String tipoSoggetto = asps.getTipoSoggettoErogatore();
					String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoSoggetto);
	
					listApc.add(apc);
					protocolli.add(protocollo);
				}
			}


			// preparo i dati
			List<List<DataElement>> dati = new ArrayList<>();

			if(lista!=null) {
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
	
					List<DataElement> e = new ArrayList<>();
	
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
			
					boolean showVerificaCertificati = false;
					if(gestioneFruitori) {
						showVerificaCertificati = this.core.isFruizioniVerificaCertificati();
					}
					else {
						showVerificaCertificati = this.core.isErogazioniVerificaCertificati();
					}
					
					
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
					e.add(de);
					
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
							labelCanalePorta =  canaleConfigurazioneDefault!=null ? canaleConfigurazioneDefault.getNome() : null;
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
					e.add(de);
					
					// tags
					List<GruppoSintetico> gruppo = apc.getGruppo();
					if(gruppo != null && !gruppo.isEmpty()) {
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
							
							e.add(de);
						}
					}
					
					
					
					if(dataElementStatoApi!=null) {
						e.add(dataElementStatoApi);
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
							ServletUtils.setObjectIntoSession(this.request, this.session, PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE, PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT);
							List<MappingErogazionePortaApplicativa> listaMappingErogazionePortaApplicativa = this.apsCore.mappingServiziPorteAppList(idServizio,asps.getId(), null);
							List<PortaApplicativa> listaPorteApplicativeAssociate = new ArrayList<>();
	
							for(MappingErogazionePortaApplicativa mappinErogazione : listaMappingErogazionePortaApplicativa) {
								listaPorteApplicativeAssociate.add(this.porteApplicativeCore.getPortaApplicativa(mappinErogazione.getIdPortaApplicativa()));
							}
	
							/**					// controllo accessi
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
							//						e.add(de);
							//					}*/
	
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
								e.add(de);
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
								e.add(de);
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
								de.setValue(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_DIFFERENTE_DA_TRASPORTO_E_TOKEN);
								e.add(de);
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
								e.add(de);
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
									e.add(de);
								}
							}
	
							//mtom
							if(visualizzaMTOM) {
								boolean controlloMTOMAbilitato = false;
								for (PortaApplicativa paAssociata : listaPorteApplicativeAssociate) {
									String statoMTOM = this.getStatoMTOMPortaApplicativa(paAssociata);
	
									if(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_ABILITATO.equals(statoMTOM)) {
										controlloMTOMAbilitato = true;
										break;
									}
	
								}
	
								if(controlloMTOMAbilitato) {
									de = new DataElement();
									de.setType(DataElementType.IMAGE);
									de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM);
									e.add(de);
								}
							}
	
							// correlazione applicativa
							if(visualizzaCorrelazione) {
								boolean controlloCorrelazioneApplicativaAbilitato = false;
								for (PortaApplicativa paAssociata : listaPorteApplicativeAssociate) {
									String statoTracciamento = this.getStatoTracciamentoPortaApplicativa(paAssociata);
	
	
									if(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_ABILITATA.equals(statoTracciamento)){
										controlloCorrelazioneApplicativaAbilitato = true;
										break;
									}
	
								}
	
								if(controlloCorrelazioneApplicativaAbilitato) {
									de = new DataElement();
									de.setType(DataElementType.IMAGE);
									de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRACCIAMENTO);
									e.add(de);
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
								e.add(de);
							}
	
						} else if(showConfigurazionePD) {
	
							// Utilizza la configurazione come parent
							ServletUtils.setObjectIntoSession(this.request, this.session, PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE, PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT);
	
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
	
							/**					// controllo accessi
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
							//						e.add(de);
							//					}*/
	
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
								e.add(de);
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
								e.add(de);
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
								de.setValue(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_DIFFERENTE_DA_TRASPORTO_E_TOKEN);
								e.add(de);
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
								e.add(de);
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
									e.add(de);
								}
							}
	
							//mtom
							if(visualizzaMTOM) {
								boolean controlloMTOMAbilitato = false;
								for (PortaDelegata pdAssociata : listaPorteDelegateAssociate) {
									String statoMTOM = this.getStatoMTOMPortaDelegata(pdAssociata);
	
									if(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_ABILITATO.equals(statoMTOM)) {
										controlloMTOMAbilitato = true;
										break;
									}
	
								}
	
								if(controlloMTOMAbilitato) {
									de = new DataElement();
									de.setType(DataElementType.IMAGE);
									de.setValue(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM);
									e.add(de);
								}
							}
	
							// correlazione applicativa
							if(visualizzaCorrelazione) {
								boolean controlloCorrelazioneApplicativaAbilitato = false;
								for (PortaDelegata pdAssociata : listaPorteDelegateAssociate) {
									String statoTracciamento = this.getStatoTracciamentoPortaDelegata(pdAssociata);
	
	
									if(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_ABILITATA.equals(statoTracciamento)){
										controlloCorrelazioneApplicativaAbilitato = true;
										break;
									}
	
								}
	
								if(controlloCorrelazioneApplicativaAbilitato) {
									de = new DataElement();
									de.setType(DataElementType.IMAGE);
									de.setValue(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA);
									e.add(de);
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
								e.add(de);
							}
						}
					}
					
					// In Uso Button
					this.addInUsoInfoButton(e, 
							labelServizio,
							gestioneFruitori ? uriASPS+"@"+fruitore.getTipo()+"/"+fruitore.getNome() : uriASPS,
							gestioneFruitori ? InUsoType.FRUIZIONE_INFO : InUsoType.EROGAZIONE_INFO);
					
					// Verifica Certificati
					if(showVerificaCertificati) {
						
						List<Parameter> listParametersServizioModificaProfilo = null;
						List<Parameter> listParametersServizioFruitoriModificaProfilo = null;
						listParametersServizioModificaProfilo = new ArrayList<>();
						listParametersServizioModificaProfilo.addAll(listParameters);
						listParametersServizioModificaProfilo.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_PROFILO, true+""));
						listParametersServizioModificaProfilo.add(new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CERTIFICATI_FROM_LISTA, true+""));
						
						if(gestioneFruitori) {
							listParametersServizioFruitoriModificaProfilo = new ArrayList<>();
							listParametersServizioFruitoriModificaProfilo.addAll(listParametersServizioModificaProfilo);
							Parameter pIdFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID, fruitore.getId()+ "");
							listParametersServizioFruitoriModificaProfilo.add(pIdFruitore);
						}
						
						if(gestioneFruitori) {
							this.addComandoVerificaCertificatiButton(e, labelServizio, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_VERIFICA_CERTIFICATI, listParametersServizioFruitoriModificaProfilo);
						}
						else {
							this.addComandoVerificaCertificatiButton(e, labelServizio, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_VERIFICA_CERTIFICATI, listParametersServizioModificaProfilo);
						}
					}
					
					// se e' abilitata l'opzione reset cache per elemento, visualizzo il comando nell'elenco dei comandi disponibili nella lista
					if(this.core.isElenchiVisualizzaComandoResetCacheSingoloElemento()){
						List<Parameter> listaParametriChange = new ArrayList<>();		
						listaParametriChange.addAll(listParameters);
						
						this.addComandoResetCacheButton(e, labelServizio, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_CHANGE, listaParametriChange);
					}
									
					// aggiungo entry
					dati.add(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

			// preparo bottoni
			if(lista!=null && !lista.isEmpty() &&
				this.core.isShowPulsantiImportExport()) {

				ExporterUtils exporterUtils = new ExporterUtils(this.archiviCore);
				if(exporterUtils.existsAtLeastOneExportMode(ArchiveType.ACCORDO_SERVIZIO_PARTE_SPECIFICA, this.request, this.session)){

					List<AreaBottoni> bottoni = new ArrayList<>();

					AreaBottoni ab = new AreaBottoni();
					List<DataElement> otherbott = new ArrayList<>();
					DataElement de = new DataElement();
					de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_APS_ESPORTA_SELEZIONATI);
					if(gestioneFruitori) {
						de.setOnClick(AccordiServizioParteSpecificaCostanti.LABEL_FRUIZIONI_ESPORTA_SELEZIONATI_ONCLICK);
					}
					else {
						de.setOnClick(AccordiServizioParteSpecificaCostanti.LABEL_EROGAZIONI_ESPORTA_SELEZIONATI_ONCLICK);
					}
					de.setDisabilitaAjaxStatus();
					otherbott.add(de);
					ab.setBottoni(otherbott);
					bottoni.add(ab);

					this.pd.setAreaBottoni(bottoni);

				}
				
			}

		} catch (Exception e) {
			this.logError("Exception: " + e.getMessage(), e);
			throw new DriverControlStationException(e);
		}
	}

	public List<List<DataElement>> addErogazioneToDati(List<List<DataElement>> datiPagina, TipoOperazione tipoOp,
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

		if(asps==null) {
			throw new DriverControlStationException("Param asps is null");
		}
		
		boolean showProtocolli = this.core.countProtocolli(this.request, this.session)>1;
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
		
		String uriASPS = this.idServizioFactory.getUriFromAccordo(asps);
		
		IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
		
		IDSoggetto idSoggettoFruitore = null;
		if(gestioneFruitori) {
			idSoggettoFruitore = new IDSoggetto(fruitore.getTipo(), fruitore.getNome());
		}
		
		String labelServizioConFruitore = null;
		if(gestioneFruitori) {
			labelServizioConFruitore = this.getLabelServizioFruizione(protocollo, idSoggettoFruitore, idServizio);
		}
		else {
			labelServizioConFruitore = this.getLabelServizioErogazione(protocollo, idServizio);
		}
		
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
		boolean modi = this.core.isProfiloModIPA(protocollo);
		
		boolean showVerificaCertificati = false;
		if(gestioneFruitori) {
			showVerificaCertificati = this.core.isFruizioniVerificaCertificati();
		}
		else {
			showVerificaCertificati = this.core.isErogazioniVerificaCertificati();
		}
		
		boolean showVerificaCertificatiModi = false; // scudo solo per ModI
		if(modi && modificaDatiProfilo) {
			if(gestioneFruitori) {
				showVerificaCertificatiModi = this.core.isModipaFruizioniVerificaCertificati();
			}
			else {
				showVerificaCertificatiModi = this.core.isModipaErogazioniVerificaCertificati();
			}
		}
		
		List<Parameter> listaParametriChange = new ArrayList<>();		
		listaParametriChange.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId() + ""));
		listaParametriChange.add(pNomeServizio);
		listaParametriChange.add(pTipoServizio);
		listaParametriChange.add(pIdSoggettoErogatore);
		if(gestioneFruitori) {
			listaParametriChange.add(pTipoSoggettoFruitore);
			listaParametriChange.add(pNomeSoggettoFruitore);
			listaParametriChange.add(pIdProviderFruitore);
		}
		
		List<Parameter> listParametersServizioModificaProfiloOrVerificaCertificati = null;
		List<Parameter> listParametersServizioFruitoriModificaProfiloOrVerificaCertificati = null;
		if(modificaDatiProfilo || showVerificaCertificati) {
			listParametersServizioModificaProfiloOrVerificaCertificati = new ArrayList<>();
			listParametersServizioModificaProfiloOrVerificaCertificati.addAll(listaParametriChange);
			
			if(gestioneFruitori) {
				listParametersServizioFruitoriModificaProfiloOrVerificaCertificati = new ArrayList<>();
				listParametersServizioFruitoriModificaProfiloOrVerificaCertificati.addAll(listParametersServizioModificaProfiloOrVerificaCertificati);
				listParametersServizioFruitoriModificaProfiloOrVerificaCertificati.add(pIdFruitore);
			}
		}
		
		// sezione 1 riepilogo
		List<DataElement> dati = datiPagina.get(0);
		
		// In Uso Button
		this.addComandoInUsoInfoButton(dati, labelServizioConFruitore,
				gestioneFruitori ? uriASPS+"@"+fruitore.getTipo()+"/"+fruitore.getNome() : uriASPS,
				gestioneFruitori ? InUsoType.FRUIZIONE_INFO : InUsoType.EROGAZIONE_INFO);
		
		// Verifica Certificati
		if(showVerificaCertificati) {
			if(gestioneFruitori) {
				this.pd.addComandoVerificaCertificatiElementoButton(ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_VERIFICA_CERTIFICATI, listParametersServizioFruitoriModificaProfiloOrVerificaCertificati);
			}
			else {
				this.pd.addComandoVerificaCertificatiElementoButton(ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_VERIFICA_CERTIFICATI, listParametersServizioModificaProfiloOrVerificaCertificati);
			}
		}
		
		// se e' abilitata l'opzione reset cache per elemento, visualizzo il comando nell'elenco dei comandi disponibili nella lista
		if(this.core.isElenchiVisualizzaComandoResetCacheSingoloElemento()){
			this.pd.addComandoResetCacheElementoButton(ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_CHANGE, listaParametriChange);
		}
		
		// Titolo Servizio
		DataElement de = new DataElement();
		String labelServizio = this.getLabelIdServizioSenzaErogatore(idServizio);
		String labelServizioConPortType = labelServizio;
		if(asps.getPortType()!=null && !"".equals(asps.getPortType()) && !asps.getNome().equals(asps.getPortType())) {
			labelServizioConPortType = labelServizioConPortType +" ("+asps.getPortType()+")";
		}
		de.setLabel(ErogazioniCostanti.LABEL_ASPS_MODIFICA_SERVIZIO_NOME);
		de.setValue(labelServizioConPortType);
		de.setStatusValue(labelServizioConPortType);
		de.setType(DataElementType.TEXT);
		List<Parameter> listParametersServizioModifica = new ArrayList<>();
		listParametersServizioModifica.addAll(listaParametriChange);
		listParametersServizioModifica.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_API, false+"")); 
		
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
				listParametersServizioModifica.toArray(new Parameter[1]));
		imageChangeName.setToolTip(MessageFormat.format(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,
				AccordiServizioParteSpecificaCostanti.LABEL_APS_INFO_GENERALI));
		imageChangeName.setImage(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
		
		if(imageChangeStato!=null) {
			de.addImage(imageChangeName);
			de.addImage(imageChangeStato);
		}
		else {
			de.setImage(imageChangeName);
		}
		
		dati.add(de);
		
		// soggetto erogatore
		if(gestioneFruitori || showSoggettoErogatoreInErogazioni) {
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SOGGETTO_EROGATORE);
			de.setValue(this.getLabelNomeSoggetto(protocollo,asps.getTipoSoggettoErogatore(),asps.getNomeSoggettoErogatore()));
			de.setType(DataElementType.TEXT);
			dati.add(de);
			
			List<Parameter> listParametersServizioModificaSoggettoErogatore = new ArrayList<>();
			listParametersServizioModificaSoggettoErogatore.addAll(listaParametriChange);
			listParametersServizioModificaSoggettoErogatore.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_CAMBIA_SOGGETTO_EROGATORE, true+""));
			
			DataElementImage image = new DataElementImage();
			image.setUrl(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE,	listParametersServizioModificaSoggettoErogatore.toArray(new Parameter[1]));
			image.setToolTip(MessageFormat.format(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_CAMBIA_API_TOOLTIP_CON_PARAMETRO, 
					AccordiServizioParteSpecificaCostanti.LABEL_APS_SOGGETTO_EROGATORE));
			image.setImage(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
			image.setTarget(TargetType.SELF);
			de.addImage(image);
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
		
		List<String> labelsGruppi = new ArrayList<>();
		List<String> valuesGruppi = new ArrayList<>();
		
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
				valuesGruppi.add("label-info-"+ indexOf);
			}
		}	
		
		de.setLabels(labelsGruppi);
		de.setValues(valuesGruppi);
		
		
		// Lista di Accordi Compatibili
		List<AccordoServizioParteComune> asParteComuneCompatibili = null;
		try{
			boolean soloAccordiConsistentiRest = false;
			boolean soloAccordiConsistentiSoap = false;
			if(!this.isModalitaCompleta()) {
				// filtro accordi senza risorse o senza pt/operation
				soloAccordiConsistentiRest = true;
				soloAccordiConsistentiSoap = true;
			}
			
			List<AccordoServizioParteComune> asParteComuneCompatibiliTmp = this.apsCore.findAccordiParteComuneBySoggettoAndNome(as.getNome(), 
					new IDSoggetto(as.getSoggettoReferente().getTipo(), as.getSoggettoReferente().getNome()));
			if(asParteComuneCompatibiliTmp!=null && !asParteComuneCompatibiliTmp.isEmpty()) {
				for (AccordoServizioParteComune accordoServizioParteComune : asParteComuneCompatibiliTmp) {
					boolean isValid = false;
					
					if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(accordoServizioParteComune.getServiceBinding())) {
						if(soloAccordiConsistentiRest) {
							isValid = accordoServizioParteComune.sizeResourceList()>0;
						}
					}
					else {
						if(soloAccordiConsistentiSoap) {
							if(asps!=null && asps.getPortType()!=null) {
								for (int i = 0; i < accordoServizioParteComune.sizePortTypeList(); i++) {
									if(accordoServizioParteComune.getPortType(i).getNome().equals(asps.getPortType()) &&
										accordoServizioParteComune.getPortType(i).sizeAzioneList()>0) {
										isValid = true;
										break;
									}
								}
							}
							else {
								isValid = accordoServizioParteComune.sizeAzioneList()>0;
							}
						}
					}
					
					if(isValid) {
						if(asParteComuneCompatibili==null) {
							asParteComuneCompatibili = new ArrayList<>();
						}
						asParteComuneCompatibili.add(accordoServizioParteComune);
					}
					
				}
			}			
			
			
		}catch(Exception e){
			ControlStationCore.logError("Errore durante la ricerca degli accordi parte comune compatibili", e);
		}
		
		// lista icone a dx 
		// n.b. iniziare ad aggiungerle da quella che deve stare piu' a dx perche' la regola css float right le allinea al contrario
		
		boolean apiImplementataCambiabile = ErogazioniUtilities.isChangeAPIEnabled(asps, this.apsCore);
		if(apiImplementataCambiabile) {
			List<Parameter> listParametersServizioModificaApi = new ArrayList<>();
			listParametersServizioModificaApi.addAll(listaParametriChange);
			listParametersServizioModificaApi.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_CAMBIA_API, true+""));
			
			DataElementImage image = new DataElementImage();
			image.setUrl(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE,	listParametersServizioModificaApi.toArray(new Parameter[1]));
			image.setToolTip(MessageFormat.format(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_CAMBIA_API_TOOLTIP_CON_PARAMETRO, 
					AccordiServizioParteSpecificaCostanti.LABEL_APC_COMPOSTO_SOLO_PARTE_COMUNE));
			image.setImage(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
			image.setTarget(TargetType.SELF);
			de.addImage(image);
		}
		
		if(asParteComuneCompatibili!=null && asParteComuneCompatibili.size()>1) {
			List<Parameter> listParametersServizioModificaApi = new ArrayList<>();
			listParametersServizioModificaApi.addAll(listaParametriChange);
			listParametersServizioModificaApi.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_API, true+""));
			
			DataElementImage image = new DataElementImage();
			image.setUrl(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE,	listParametersServizioModificaApi.toArray(new Parameter[1]));
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
		dati.add(de);

		
		// ProtocolProperties

		if(showProtocolli || modificaDatiProfilo) {
			
			de = new DataElement();
			String labelProtocollo =this.getLabelProtocollo(protocollo);
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_PROTOCOLLO);
			de.setValue(labelProtocollo);
			de.setType(DataElementType.TEXT);
								
			List<Parameter> listParametersServizioModificaProfilo = new ArrayList<>();
			if(gestioneFruitori) {
				listParametersServizioModificaProfilo.addAll(listParametersServizioFruitoriModificaProfiloOrVerificaCertificati);
			}
			else {
				listParametersServizioModificaProfilo.addAll(listParametersServizioModificaProfiloOrVerificaCertificati);
			}
			listParametersServizioModificaProfilo.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_PROFILO, true+""));
			
			if(modificaDatiProfilo) {
				
				image = new DataElementImage();
				if(gestioneFruitori) {
					image.setUrl(
							AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE,
							listParametersServizioModificaProfilo.toArray(new Parameter[1]));
				}
				else {
					image.setUrl(
							AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE,
							listParametersServizioModificaProfilo.toArray(new Parameter[1]));
				}
				image.setToolTip(MessageFormat.format(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,
						AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_PROTOCOLLO));
				image.setImage(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
				de.addImage(image);
				
			}
			
			if(showVerificaCertificatiModi)	{
				image = new DataElementImage();
				image.setUrl(
						ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_VERIFICA_CERTIFICATI,
						listParametersServizioModificaProfilo.toArray(new Parameter[1]));
				image.setToolTip(MessageFormat.format(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_VERIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,
						AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_PROTOCOLLO));
				image.setImage(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_VERIFICA_CERTIFICATI);
				de.addImage(image);
				
			}
			
			dati.add(de);
			
		}
		
		
		// Fruitore
		if(showSoggettoFruitoreInFruizioni) {
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SOGGETTO_FRUITORE);
			de.setValue(this.getLabelNomeSoggetto(protocollo,fruitore.getTipo(),fruitore.getNome()));
			de.setType(DataElementType.TEXT);
			dati.add(de);
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
			
			ServletUtils.setObjectIntoSession(this.request, this.session, PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE, PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT);
			
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
			dati.add(de);
			
			boolean visualizzaConnettore = true;
			boolean checkConnettore = false;
			boolean connettoreMultiploEnabled = false;
			long idConnettore = 1;
			for (int i = 0; i < listaPorteApplicativeAssociate.size(); i++) {
				PortaApplicativa paAssociata = listaPorteApplicativeAssociate.get(i);
				MappingErogazionePortaApplicativa mapping = listaMappingErogazionePortaApplicativa.get(i);
				
				if(!mapping.isDefault()) {
					PortaApplicativaServizioApplicativo portaApplicativaAssociataServizioApplicativo = paAssociata.getServizioApplicativoList().get(0);
					boolean connettoreConfigurazioneRidefinito = this.isConnettoreRidefinito(paDefault, paSADefault, paAssociata, portaApplicativaAssociataServizioApplicativo, paAssociata.getServizioApplicativoList());
					if(connettoreConfigurazioneRidefinito) {
						visualizzaConnettore = false;
						break;
					}
				} else {
					canalePorta = paAssociata.getCanale();
				}
				
			}
			
			if(visualizzaConnettore) {
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
				String urlConnettore = this.getLabelConnettore(sa,is,true);
				
				if(!connettoreMultiploEnabled) {	
					de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORE);
					de.setValue(formatInfoForView(urlConnettore));
					String tooltipConnettore = this.getTooltipConnettore(sa,is,true);
					de.setToolTip(tooltipConnettore);
				} else {
					de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI);
					de.setValue(this.getNomiConnettoriMultipliPortaApplicativa(paDefault));
					de.setToolTip(this.getToolTipConnettoriMultipliPortaApplicativa(paDefault));
				}
				
				boolean visualizzaLinkConfigurazioneConnettore = 
						(!this.core.isConnettoriMultipliEnabled()) 
						|| 
						(!connettoreMultiploEnabled );
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

				boolean visualizzaLinkCheckConnettore = 
						checkConnettore && 
						(
								(!this.core.isConnettoriMultipliEnabled())
								|| 
								( !connettoreMultiploEnabled )
						);
				if(visualizzaLinkCheckConnettore) {
					List<Parameter> listParametersVerificaConnettore = new ArrayList<>();
					paIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, asps.getIdSoggetto() + "");
					listParametersVerificaConnettore.add(paIdSogg);
					listParametersVerificaConnettore.add(paIdPorta);
					listParametersVerificaConnettore.add(paIdAsps);
					listParametersVerificaConnettore.add(paConnettoreDaListaAPS);
					listParametersVerificaConnettore.add(new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ID, idConnettore+""));
					listParametersVerificaConnettore.add(new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI, false+""));
					listParametersVerificaConnettore.add(new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO, false+""));
					
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
					listParametersConfigutazioneConnettoriMultipli.add(new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI, false+""));
					listParametersConfigutazioneConnettoriMultipli.add(new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO, false+""));
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
				
				dati.add(de);
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
			dati.add(de);
			
			
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
				
				dati.add(de);
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
				dati.add(de);
			}
		}
		
		if(gestioneFruitori) {
			
			ServletUtils.setObjectIntoSession(this.request, this.session, PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE, PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT);
			
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
			dati.add(de);
			
			
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
				if(listaAzioni!=null && !listaAzioni.isEmpty()) {
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
				String urlConnettore = this.getLabelConnettore(connettore, true, false);
				String tooltipConnettore = this.getLabelConnettore(connettore, true, true);
				
				// Controllo se richiedere il connettore
				boolean connettoreStatic = false;
				if(gestioneFruitori) {
					connettoreStatic = this.apsCore.isConnettoreStatic(protocollo);
				}
				
				if(!connettoreStatic) {
					
					de.setValue(formatInfoForView(urlConnettore));
					de.setToolTip(tooltipConnettore);
					
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
						listParametersVerificaConnettore.add(new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI, false+""));
						listParametersVerificaConnettore.add(new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO, true+""));
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
				
				dati.add(de);
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
			dati.add(de);
			
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
				
				dati.add(de);
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
				dati.add(de);
			}
		}
		

		Parameter pGruppiTrue = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_GRUPPI,true+"");
		Parameter pGruppiFalse = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_GRUPPI,false+"");
		
		Parameter pConfigurazioneTrue =new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_CONFIGURAZIONI,true+"");
		Parameter pConfigurazioneFalse =new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_CONFIGURAZIONI,false+"");
		

		
		
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
		dati.add(de);
		
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
			dati.add(de);
		}
		
		if(visualizzaGruppi)
			this.aggiungiListaConfigurazioni(datiPagina, serviceBinding, gestioneErogatori, listaMappingErogazionePortaApplicativa,	listaPorteApplicativeAssociate, listaMappingFruzionePortaDelegata, listaPorteDelegateAssociate,  pIdAsps, pIdFruitore, asps, as);

		return datiPagina;
	}


	@SuppressWarnings("unused")
	private void aggiungiListaConfigurazioni(List<List<DataElement>> datiPagina, ServiceBinding serviceBinding,
			boolean gestioneErogatori, List<MappingErogazionePortaApplicativa> listaMappingErogazionePortaApplicativa,
			List<PortaApplicativa> listaPorteApplicativeAssociate,
			List<MappingFruizionePortaDelegata> listaMappingFruzionePortaDelegata,
			List<PortaDelegata> listaPorteDelegateAssociate,
			Parameter pIdAsps,
			Parameter pIdFruitore, AccordoServizioParteSpecifica asps, AccordoServizioParteComuneSintetico as)	throws Exception {

		Map<String,String> azioni = this.core.getAzioniConLabel(asps, as, false, true, new ArrayList<>());
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

			if(azioni!=null && azioni.size()>1) {
				List<String> azioniL = new ArrayList<>();
				if(azioni != null && azioni.size() > 0)
					azioniL.addAll(azioni.keySet());
				allActionRedefined = this.allActionsRedefinedMappingErogazionePaAssociate(azioniL, listaPorteApplicativeAssociate);
			}

			for (int d = 0; d < listaMappingErogazionePortaApplicativa.size() ; d++) {
				MappingErogazionePortaApplicativa mapping =listaMappingErogazionePortaApplicativa.get(d);
				PortaApplicativa paAssociata = listaPorteApplicativeAssociate.get(d);

				// controllo se la configurazione deve essere visualizzata
				boolean showConfigurazione = !(mapping.isDefault() && allActionRedefined);

				int numeroConfigurazioniAttive = 0;

				List<String> labelDisponibili = new ArrayList<>();
				List<String> urlDisponibili = new ArrayList<>();

				if(showConfigurazione) {
					List<DataElement> gruppoList = new ArrayList<>();
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
					gruppoList.add(de);

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
						gruppoList.add(de);

						de = new DataElement();
						de.setType(DataElementType.TEXT);
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_GESTIONE_TOKEN);				
						de.setValue(this.getStatoGestioneTokenPortaApplicativa(paAssociata));
						gruppoList.add(de);

						de = new DataElement();
						de.setType(DataElementType.TEXT);
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE);				
						de.setValue(this.getStatoAutenticazionePortaApplicativa(paAssociata));
						gruppoList.add(de);

						de = new DataElement();
						de.setType(DataElementType.TEXT);
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_DIFFERENTE_DA_TRASPORTO_E_TOKEN);				
						de.setValue(this.getStatoAutorizzazionePortaApplicativa(paAssociata));
						gruppoList.add(de);

						de = new DataElement();
						de.setType(DataElementType.BUTTON);
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI, pIdSoggPA, pIdPorta, pIdAsps);
						de.setValue(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
						de.setToolTip(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP); 
						gruppoList.add(de);
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
						gruppoList.add(de);

						de = new DataElement();
						de.setType(DataElementType.TEXT);
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_STATO);				
						de.setValue(statoValidazione);
						gruppoList.add(de);

						String tipoValidazione = this.getTipoValidazionePortaApplicativa(paAssociata);
						if(tipoValidazione != null) {
							de = new DataElement();
							de.setType(DataElementType.TEXT);
							de.setLabel(CostantiControlStation.LABEL_PARAMETRO_PORTE_TIPO);				
							de.setValue(tipoValidazione);
							gruppoList.add(de);
						}

						de = new DataElement();
						de.setType(DataElementType.BUTTON);
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI, pIdSoggPA, pIdPorta, pIdAsps);
						de.setValue(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
						de.setToolTip(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP); 
						gruppoList.add(de);
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
							gruppoList.add(de);

							de = new DataElement();
							de.setType(DataElementType.TEXT);
							de.setLabel(CostantiControlStation.LABEL_PARAMETRO_STATO);				
							de.setValue(statoMessageSecurity);
							gruppoList.add(de);

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
							gruppoList.add(de);

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
							gruppoList.add(de);

							de = new DataElement();
							de.setType(DataElementType.BUTTON);
							de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY,pIdSoggPA, pIdPorta, pIdAsps);
							de.setValue(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
							de.setToolTip(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP); 
							gruppoList.add(de);
						}
					}

					//mtom
					if(visualizzaMTOM) {
						boolean controlloMTOMAbilitato = false;
						String statoMTOM = this.getStatoMTOMPortaApplicativa(paAssociata);

						if(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_ABILITATO.equals(statoMTOM)) {
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
							gruppoList.add(de);

							de = new DataElement();
							de.setType(DataElementType.TEXT);
							de.setLabel(CostantiControlStation.LABEL_PARAMETRO_STATO);				
							de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_ABILITATO);
							gruppoList.add(de);

							de = new DataElement();
							de.setType(DataElementType.TEXT);
							de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_RICHIESTA);	
							MTOMProcessorType modeReq = this.getProcessorTypeRequestMTOMPortaApplicativa(paAssociata);
							if(modeReq != null) {
								de.setValue(modeReq.getValue());
							} else {
								de.setValue(MTOMProcessorType.DISABLE.getValue());
							}
							gruppoList.add(de);

							de = new DataElement();
							de.setType(DataElementType.TEXT);
							de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_RISPOSTA);		
							MTOMProcessorType modeRes = this.getProcessorTypeResponseMTOMPortaApplicativa(paAssociata);
							if(modeRes != null) {
								de.setValue(modeRes.getValue());
							} else {
								de.setValue(MTOMProcessorType.DISABLE.getValue());
							}
							gruppoList.add(de);

							de = new DataElement();
							de.setType(DataElementType.BUTTON);
							de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM,pIdPorta, pIdSoggPA, pIdAsps);
							de.setValue(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
							de.setToolTip(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP); 
							gruppoList.add(de);
						}
					}

					// correlazione applicativa
					if(visualizzaCorrelazione) {
						boolean tracciamentoAbilitato = false;
						String statoTracciamento = this.getStatoTracciamentoPortaApplicativa(paAssociata);

						if(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_ABILITATA.equals(statoTracciamento)){
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
							gruppoList.add(de);

							if(this.isRidefinitoTransazioniRegistratePortaApplicativa(paAssociata)) {
								de = new DataElement();
								de.setType(DataElementType.TEXT);
								de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI);				
								de.setValue(this.getStatoTransazioniRegistratePortaApplicativa(paAssociata));
								gruppoList.add(de);
							}

							if(this.isRidefinitoMessaggiDiagnosticiPortaApplicativa(paAssociata)) {
								de = new DataElement();
								de.setType(DataElementType.TEXT);
								de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_MESSAGGI_DIAGNOSTICI);				
								de.setValue(this.getStatoMessaggiDiagnosticiPortaApplicativa(paAssociata));
								gruppoList.add(de);
							}

							if(this.isEnabledCorrelazioneApplicativaPortaApplicativa(paAssociata)) {
								de = new DataElement();
								de.setType(DataElementType.TEXT);
								de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA);				
								de.setValue(this.getStatoCorrelazioneApplicativaPortaApplicativa(paAssociata)); 
								gruppoList.add(de);
							}

							de = new DataElement();
							de.setType(DataElementType.BUTTON);
							de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA, pIdSoggPA, pIdPorta, pIdNome,pIdAsps);
							de.setValue(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
							de.setToolTip(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP); 
							gruppoList.add(de);
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
						gruppoList.add(de);

						de = new DataElement();
						de.setType(DataElementType.TEXT);
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_RICHIESTA);				
						de.setValue(this.getStatoDumpRichiestaPortaApplicativa(paAssociata, true));
						gruppoList.add(de);

						de = new DataElement();
						de.setType(DataElementType.TEXT);
						de.setLabel(CostantiControlStation.LABEL_PARAMETRO_DUMP_SEZIONE_RISPOSTA);				
						de.setValue(this.getStatoDumpRispostaPortaApplicativa(paAssociata, true));
						gruppoList.add(de);

						de = new DataElement();
						de.setType(DataElementType.BUTTON);
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE, pIdSoggPA, pIdPorta, pIdAsps);
						de.setValue(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
						de.setToolTip(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP); 
						gruppoList.add(de);
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
							gruppoList.add(de);

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
								gruppoList.add(de);
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
								gruppoList.add(de);
							}

							de = new DataElement();
							de.setType(DataElementType.BUTTON);
							de.setUrl(servletConnettore, pIdSoggPA, pIdPorta, pIdAsps);
							de.setValue(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE);
							de.setToolTip(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP); 
							gruppoList.add(de);
						} else {
							labelDisponibili.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORE);
							DataElement deTmp = new DataElement();
							deTmp.setUrl(servletConnettore, pIdSoggPA, pIdPorta, pIdAsps);
							urlDisponibili.add(deTmp.getUrl());
						}
					}

					if(this.isModalitaAvanzata()) {
						// proprieta'

/**
						//						// opzioni avanzate
						//						de = new DataElement();
						//						//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
						//						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_PROPRIETA_PROTOCOLLO_LIST, pIdSogg, pIdPorta,pIdAsps);
						//						if (contaListe) {
						//							int numProp = paAssociata.sizeProprietaList();
						//							ServletUtils.setDataElementVisualizzaLabel(de, (long) numProp );
						//						} else
						//							ServletUtils.setDataElementVisualizzaLabel(de);
						//						e.add(de);
						//
						//					// Altro
						//						de = new DataElement();
						//						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CHANGE,pIdSogg, pNomePorta, pIdPorta,pIdAsps,pConfigurazioneAltro);
						//						ServletUtils.setDataElementVisualizzaLabel(de);
						//						e.add(de);*/


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
					gruppoList.add(de);


					// select per modale con configurazioni disponibili
					de = new DataElement();
					de.setName(ErogazioniCostanti.ASPS_EROGAZIONI_PARAMETRO_NUOVA_CONFIGURAZIONE+"_"+ d);
					de.setLabel(ErogazioniCostanti.LABEL_ASPS_EROGAZIONI_PARAMETRO_NUOVA_CONFIGURAZIONE);
					if(!labelDisponibili.isEmpty()) {
						de.setType(DataElementType.SELECT);
						de.setValues(urlDisponibili);
						de.setLabels(labelDisponibili);
						de.setSelected(urlDisponibili.get(0));
					} else {
						de.setType(DataElementType.HIDDEN);
					}
					gruppoList.add(de);

					// informazioni disegno in testa
					de = new DataElement();
					de.setType(DataElementType.HIDDEN);
					de.setName(ErogazioniCostanti.ASPS_EROGAZIONI_PARAMETRO_NUMERO_CONFIGURAZIONI_ATTIVE);
					de.setValue(numeroConfigurazioniAttive + "");
					gruppoList.add(0,de);

					int numeroConfigurazioniDisponibili = labelDisponibili.size();

					de = new DataElement();
					de.setType(DataElementType.HIDDEN);
					de.setName(ErogazioniCostanti.ASPS_EROGAZIONI_PARAMETRO_NUMERO_CONFIGURAZIONI_DISPONIBILI);
					de.setValue(numeroConfigurazioniDisponibili + "");
					gruppoList.add(0,de);

					datiPagina.add(gruppoList);
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

			if(azioni!=null && azioni.size()>1) {
				List<String> azioniL = new ArrayList<>();
				if(azioni != null && azioni.size() > 0)
					azioniL.addAll(azioni.keySet());
				allActionRedefined = this.allActionsRedefinedMappingFruizionePdAssociate(azioniL, listaPorteDelegateAssociate);
			}


			for (int d = 0; d < listaMappingFruzionePortaDelegata.size() ; d++) {
				MappingFruizionePortaDelegata mapping = listaMappingFruzionePortaDelegata.get(d);
				PortaDelegata pdAssociata = listaPorteDelegateAssociate.get(d);

				// controllo se la configurazione deve essere visualizzata
				boolean showConfigurazione = !(mapping.isDefault() && allActionRedefined);

				int numeroConfigurazioniDisponibili = 1;
				int numeroConfigurazioniAttive = 0;

				if(showConfigurazione) {
					List<DataElement> gruppoList = new ArrayList<>();
					Parameter pIdPD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, "" + pdAssociata.getId());
					Parameter pNomePD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA, pdAssociata.getNome());
					Parameter pIdSoggPD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, pdAssociata.getIdSoggetto() + "");

					// nome Gruppo
					de = new DataElement();
					de.setType(DataElementType.TITLE); 
					de.setLabel(mapping.getDescrizione());
					gruppoList.add(de);




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
					gruppoList.add(de);


					// informazioni disegno in testa
					de = new DataElement();
					de.setType(DataElementType.HIDDEN);
					de.setName(ErogazioniCostanti.ASPS_EROGAZIONI_PARAMETRO_NUMERO_CONFIGURAZIONI_ATTIVE);
					de.setValue(numeroConfigurazioniAttive + "");
					gruppoList.add(0,de);

					de = new DataElement();
					de.setType(DataElementType.HIDDEN);
					de.setName(ErogazioniCostanti.ASPS_EROGAZIONI_PARAMETRO_NUMERO_CONFIGURAZIONI_DISPONIBILI);
					de.setValue(numeroConfigurazioniDisponibili + "");
					gruppoList.add(0,de);

					datiPagina.add(gruppoList);
				}
			}
		}
	}
	
	public void prepareErogazioneChange(TipoOperazione tipoOp, AccordoServizioParteSpecifica asps, IDSoggetto idSoggettoFruitore) throws Exception {
		
		String tipologia = ServletUtils.getObjectFromSession(this.request, this.session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
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
			if(fruitore!=null) {
				listaMappingFruzionePortaDelegata = this.apsCore.serviziFruitoriMappingList(fruitore.getId(), idSoggettoFruitore, idServizio, null);	
				for(MappingFruizionePortaDelegata mappingFruizione : listaMappingFruzionePortaDelegata) {
					listaPorteDelegateAssociate.add(this.porteDelegateCore.getPortaDelegata(mappingFruizione.getIdPortaDelegata()));
				}
			}
		}
		
		this.makeMenu();
		
		// setto la barra del titolo
		List<Parameter> lstParm = new ArrayList<>();

		if(gestioneFruitori) {
			lstParm.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_FRUIZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
		}
		else {
			lstParm.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_EROGAZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
		}
		lstParm.add(new Parameter(tmpTitle, null));

		// setto la barra del titolo
		ServletUtils.setPageDataTitle(this.pd, lstParm );

		List<List<DataElement>> datiPagina = new ArrayList<>();
		List<DataElement> dati = new ArrayList<>();
		datiPagina.add(dati);
		dati.add(ServletUtils.getDataElementForEditModeFinished());

		String fromAPIPageInfo = this.getParameter(CostantiControlStation.PARAMETRO_API_PAGE_INFO);
		boolean fromApi = Costanti.CHECK_BOX_ENABLED_TRUE.equalsIgnoreCase(fromAPIPageInfo);
		if(fromApi) {
			DataElement de = new DataElement();
			de.setValue(fromAPIPageInfo);
			de.setType(DataElementType.HIDDEN);
			de.setName(CostantiControlStation.PARAMETRO_API_PAGE_INFO);
			dati.add(de);
			
			String nomePorta = this.getParameter(CostantiControlStation.PARAMETRO_NOME_PORTA);
			de = new DataElement();
			de.setValue(nomePorta);
			de.setType(DataElementType.HIDDEN);
			de.setName(CostantiControlStation.PARAMETRO_NOME_PORTA);
			dati.add(de);
			
			String abilita = this.getParameter(CostantiControlStation.PARAMETRO_ABILITA);
			de = new DataElement();
			de.setValue(abilita);
			de.setType(DataElementType.HIDDEN);
			de.setName(CostantiControlStation.PARAMETRO_ABILITA);
			dati.add(de);
			
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
			
			this.addHiddenFieldsToDati(tipoOp, id, idsogg, id, asps.getId()+"", 
					idFruizione, tipoSoggettoFruitore, nomeSoggettoFruitore,
					dati);
		}
		else {
			this.addHiddenFieldsToDati(tipoOp, asps.getId()+"", null, null, dati);
		}
		
		datiPagina = this.addErogazioneToDati(datiPagina, tipoOp, asps, as, tipoProtocollo, serviceBinding, gestioneErogatori, gestioneFruitori, listaMappingErogazionePortaApplicativa, listaPorteApplicativeAssociate, listaMappingFruzionePortaDelegata, listaPorteDelegateAssociate, fruitore);
		
		this.pd.setDati(datiPagina);
		this.pd.disableEditMode();
	}
	
	public ActionForward prepareErogazioneChangeResetCache(ActionMapping mapping, GeneralData gd, ConsoleSearch ricerca, TipoOperazione tipoOp, AccordoServizioParteSpecifica asps, IDSoggetto idSoggettoFruitore) throws Exception {
		
		String tipologia = ServletUtils.getObjectFromSession(this.request, this.session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
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
		
		String tmpTitle = null;
		if(gestioneFruitori) {
			tmpTitle = this.getLabelServizioFruizione(tipoProtocollo, idSoggettoFruitore, idServizio);
		}
		else {
			tmpTitle = this.getLabelServizioErogazione(tipoProtocollo, idServizio);
		}
						
		List<PortaApplicativa> listaPorteApplicativeAssociate = new ArrayList<>();
		if(gestioneErogatori) {
			// lettura delle configurazioni associate
			List<MappingErogazionePortaApplicativa> listaMappingErogazionePortaApplicativa = this.apsCore.mappingServiziPorteAppList(idServizio,asps.getId(), null);
			for(MappingErogazionePortaApplicativa mappinErogazione : listaMappingErogazionePortaApplicativa) {
				listaPorteApplicativeAssociate.add(this.porteApplicativeCore.getPortaApplicativa(mappinErogazione.getIdPortaApplicativa()));
			}
		}

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
			if(fruitore!=null) {
				List<MappingFruizionePortaDelegata> listaMappingFruzionePortaDelegata = this.apsCore.serviziFruitoriMappingList(fruitore.getId(), idSoggettoFruitore, idServizio, null);	
				for(MappingFruizionePortaDelegata mappingFruizione : listaMappingFruzionePortaDelegata) {
					listaPorteDelegateAssociate.add(this.porteDelegateCore.getPortaDelegata(mappingFruizione.getIdPortaDelegata()));
				}
			}
		}
		
		this.makeMenu();
		
		// setto la barra del titolo
		List<Parameter> lstParm = new ArrayList<>();

		if(gestioneFruitori) {
			lstParm.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_FRUIZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
		}
		else {
			lstParm.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_EROGAZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
		}
		lstParm.add(new Parameter(tmpTitle, null));

		// setto la barra del titolo
		ServletUtils.setPageDataTitle(this.pd, lstParm );

		String labelServizio = null;
		if(gestioneFruitori) {
			labelServizio = this.getLabelServizioFruizione(tipoProtocollo, idSoggettoFruitore, idServizio);
		}
		else {
			labelServizio = this.getLabelServizioErogazione(tipoProtocollo, idServizio);
		}
		
		// Uso lo stessoAlias
		List<String> aliases = this.apcCore.getJmxPdDAliases();
		String alias = null;
		if(aliases!=null && !aliases.isEmpty()) {
			alias = aliases.get(0);
		}
		
		long idOjectLong = -1;
		if(gestioneFruitori) {
			if(fruitore==null) {
				throw new DriverControlStationException("Fruitore non trovato");
			}
			idOjectLong = fruitore.getId();
		}
		else {
			idOjectLong = asps.getId();
		}
		
		this.apcCore.invokeJmxMethodAllNodesAndSetResult(this.pd, this.apcCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
				gestioneFruitori ?
						this.apcCore.getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheFruizione(alias) :
						this.apcCore.getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheErogazione(alias),
				MessageFormat.format(CostantiControlStation.LABEL_ELIMINATO_CACHE_SUCCESSO,labelServizio),
				MessageFormat.format(CostantiControlStation.LABEL_ELIMINATO_CACHE_FALLITO_PREFIX,labelServizio),
				idOjectLong);
		
		String resetElementoCacheS = this.getParameter(CostantiControlStation.PARAMETRO_ELIMINA_ELEMENTO_DALLA_CACHE);
		boolean resetElementoCache = ServletUtils.isCheckBoxEnabled(resetElementoCacheS);
		
		// reset delle cache richiesto dal link nella lista, torno alla lista
		if(resetElementoCache) {
			
			String userLogin = ServletUtils.getUserLoginFromSession(this.session);	
			
			int idLista = Liste.SERVIZI;
			
			// poiche' esistono filtri che hanno necessita di postback salvo in sessione
			List<AccordoServizioParteSpecifica> lista = null;
			if(!ServletUtils.isSearchDone(this)) {
				lista = ServletUtils.getRisultatiRicercaFromSession(this.request, this.session, idLista,  AccordoServizioParteSpecifica.class);
			}
			
			ricerca = this.checkSearchParameters(idLista, ricerca);
			
			this.clearFiltroSoggettoByPostBackProtocollo(0, ricerca, idLista);
								
			this.checkGestione(this.request, this.session, ricerca, idLista, tipologia,true);
			
			// preparo lista
			boolean [] permessi = AccordiServizioParteSpecificaUtilities.getPermessiUtente(this);
			
			if(lista==null) {
				if(this.apsCore.isVisioneOggettiGlobale(userLogin)){
					lista = this.apsCore.soggettiServizioList(null, ricerca,permessi, gestioneFruitori, gestioneErogatori);
				}else{
					lista = this.apsCore.soggettiServizioList(userLogin, ricerca,permessi, gestioneFruitori, gestioneErogatori);
				}
			}

			
			if(!this.isPostBackFilterElement()) {
				ServletUtils.setRisultatiRicercaIntoSession(this.request, this.session, idLista, lista); // salvo poiche' esistono filtri che hanno necessita di postback
			}
			
			this.prepareErogazioniList(ricerca, lista);
			
			// salvo l'oggetto ricerca nella sessione
			ServletUtils.setSearchObjectIntoSession(this.request, this.session, ricerca);
			
			ServletUtils.setGeneralAndPageDataIntoSession(this.request, this.session, gd, this.pd);
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI, CostantiControlStation.TIPO_OPERAZIONE_RESET_CACHE_ELEMENTO);
		} else { // reset richiesto dal dettaglio, torno al dettaglio
			this.prepareErogazioneChange(tipoOp, asps, idSoggettoFruitore);
			ServletUtils.setGeneralAndPageDataIntoSession(this.request, this.session, gd, this.pd);
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI, ForwardParams.CHANGE());
		}
	}
	
}
