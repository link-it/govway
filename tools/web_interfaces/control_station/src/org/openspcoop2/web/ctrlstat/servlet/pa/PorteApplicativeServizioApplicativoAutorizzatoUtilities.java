/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.web.ctrlstat.servlet.pa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServiziApplicativi;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRichiesta;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaServizioApplicativo;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.driver.FiltroRicercaProtocolPropertyConfig;
import org.openspcoop2.core.config.driver.db.IDServizioApplicativoDB;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.db.IDSoggettoDB;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaServiziApplicativi;
import org.openspcoop2.web.ctrlstat.servlet.ApiKeyState;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * PorteApplicativeServizioApplicativoAutorizzatoUtilities
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteApplicativeServizioApplicativoAutorizzatoUtilities {

	public String[] soggettiList = null;
	public String[] soggettiListLabel = null;
	public Map<String,List<IDServizioApplicativoDB>> listServiziApplicativi = new HashMap<>();
	public String idSoggettoToAdd = null;
	public int saSize;
	public PortaApplicativaAutorizzazioneServiziApplicativi saAutorizzatiList;
	public TrasformazioneRegolaApplicabilitaRichiesta saTrasformazioniList;
	
	public List<IDSoggettoDB> soggettiDBList_trasformazioniSoggettiToken = null;
	
	public void buildList(PortaApplicativa pa, boolean modipa, String protocollo, boolean escludiSoggettoErogatore,
			String idSoggettoToAdd,
			PorteApplicativeCore paCore, ConsoleHelper porteApplicativeHelper, boolean escludiSAServer,
			boolean isSupportatoAutenticazioneApplicativiEsterni,
			boolean isAutorizzazioneToken,
			boolean modiSicurezzaMessaggio) throws Exception {
		buildList(pa, modipa, protocollo, escludiSoggettoErogatore,
				idSoggettoToAdd,
				paCore, porteApplicativeHelper, escludiSAServer,
				isSupportatoAutenticazioneApplicativiEsterni,
				null,
				isAutorizzazioneToken,
				modiSicurezzaMessaggio);
	}
	
	public void buildListTrasformazioni(PortaApplicativa pa, boolean modipa, String protocollo, boolean escludiSoggettoErogatore,
			String idSoggettoToAdd,
			PorteApplicativeCore paCore, ConsoleHelper porteApplicativeHelper, boolean escludiSAServer,
			boolean isSupportatoAutenticazioneApplicativiEsterni,
			TrasformazioneRegola regola) throws Exception {
		buildList(pa, modipa, protocollo, escludiSoggettoErogatore,
				idSoggettoToAdd,
				paCore, porteApplicativeHelper, escludiSAServer,
				isSupportatoAutenticazioneApplicativiEsterni,
				regola,
				false,
				false);
	}
	
	public void buildListTrasformazioniSoggettiToken(PortaApplicativa pa, boolean modipa, String protocollo, boolean escludiSoggettoErogatore,
			String idSoggettoToAdd,
			PorteApplicativeCore paCore, ConsoleHelper porteApplicativeHelper, boolean escludiSAServer,
			boolean isSupportatoAutenticazioneApplicativiEsterni) throws Exception {
		this.soggettiDBList_trasformazioniSoggettiToken = new ArrayList<IDSoggettoDB>();
		buildList(pa, modipa, protocollo, escludiSoggettoErogatore,
				idSoggettoToAdd,
				paCore, porteApplicativeHelper, escludiSAServer,
				isSupportatoAutenticazioneApplicativiEsterni,
				null,
				true,
				false);
	}
	
	private void buildList(PortaApplicativa pa, boolean modipa, String protocollo, boolean escludiSoggettoErogatore,
			String idSoggettoToAdd,
			PorteApplicativeCore paCore, ConsoleHelper porteApplicativeHelper, boolean escludiSAServer,
			boolean isSupportatoAutenticazioneApplicativiEsterni,
			TrasformazioneRegola regola,
			boolean isAutorizzazioneToken,
			boolean isModiSicurezzaMessaggio) throws Exception {
		
		this.idSoggettoToAdd = idSoggettoToAdd;
		
		CredenzialeTipo tipoAutenticazione = null;
		boolean tokenWithHttpsEnabled = false;
		Boolean appId = null;
		String tokenPolicy = null;
		boolean tokenPolicyOR = false;
		if(isAutorizzazioneToken) {
			tipoAutenticazione = CredenzialeTipo.TOKEN;
			if(isModiSicurezzaMessaggio) {
				tokenWithHttpsEnabled=true;
			}
			if(pa.getGestioneToken()!=null && pa.getGestioneToken().getPolicy()!=null) {
				tokenPolicy = pa.getGestioneToken().getPolicy();
			}
		}
		else {
			tipoAutenticazione = CredenzialeTipo.toEnumConstant(pa.getAutenticazione());
			if(CredenzialeTipo.APIKEY.equals(tipoAutenticazione)) {
				ApiKeyState apiKeyState =  new ApiKeyState(paCore.getParametroAutenticazione(pa.getAutenticazione(), pa.getProprietaAutenticazioneList()));
				appId = apiKeyState.appIdSelected;
			}
			if(regola!=null) {
				if(pa.getGestioneToken()!=null && pa.getGestioneToken().getPolicy()!=null) {
					tokenPolicy = pa.getGestioneToken().getPolicy();
					if(tokenPolicy!=null && !"".equals(tokenPolicy)) {
						if(tipoAutenticazione!=null && !CredenzialeTipo.TOKEN.equals(tipoAutenticazione)) {
							tokenPolicyOR = true;
						}
						else {
							tipoAutenticazione = CredenzialeTipo.TOKEN;
						}
					}
				}
			}
		}
		
		SoggettiCore soggettiCore = new SoggettiCore(paCore);
		PddCore pddCore = new PddCore(paCore);
		ServiziApplicativiCore saCore = new ServiziApplicativiCore(paCore);
		
		String userLogin = ServletUtils.getUserLoginFromSession(porteApplicativeHelper.getSession());	
		
		boolean autenticazioneToken = pa!=null && pa.getGestioneToken()!=null && pa.getGestioneToken().getPolicy()!=null && !"".equals(pa.getGestioneToken().getPolicy());
		
		List<org.openspcoop2.core.registry.Soggetto> listSoggetti = null; 
		if(isSupportatoAutenticazioneApplicativiEsterni || isAutorizzazioneToken || (regola!=null && autenticazioneToken)) {
			listSoggetti = soggettiCore.getSoggetti(protocollo);
		}
		else {
			listSoggetti = soggettiCore.getSoggettiOperativi(protocollo);
		}
		if(listSoggetti!=null && !listSoggetti.isEmpty() && escludiSoggettoErogatore) {
			for (int i = 0; i < listSoggetti.size(); i++) {
				Soggetto soggettoCheck = listSoggetti.get(i);
				if(soggettoCheck.getTipo().equals(pa.getTipoSoggettoProprietario()) && soggettoCheck.getNome().equals(pa.getNomeSoggettoProprietario())) {
					listSoggetti.remove(i);
					break;
				}
			}
		}
		
		if(regola!=null) {
			this.saTrasformazioniList = regola.getApplicabilita();
			this.saSize = this.saTrasformazioniList!=null ? this.saTrasformazioniList.sizeServizioApplicativoList() : 0;
		}
		else {
			if(isAutorizzazioneToken && !modipa) {
				if(pa.getAutorizzazioneToken()!=null) {
					this.saAutorizzatiList = pa.getAutorizzazioneToken().getServiziApplicativi();
				}
			}
			else {
				this.saAutorizzatiList = pa.getServiziApplicativiAutorizzati();
			}
			this.saSize = this.saAutorizzatiList!=null ? this.saAutorizzatiList.sizeServizioApplicativoList() : 0;
		}

		this.listServiziApplicativi = new HashMap<>();
		
		String filtroTipoSA = escludiSAServer ? ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_CLIENT : null;
		if(listSoggetti!=null && !listSoggetti.isEmpty()) {
			List<String> soggettiListBuild = new ArrayList<>();
			List<String> soggettiLabelListBuild = new ArrayList<>();
			
			for (org.openspcoop2.core.registry.Soggetto soggetto : listSoggetti) {
				IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipo(), soggetto.getNome());
				List<IDServizioApplicativoDB> listServiziApplicativiTmp = null;
				if(regola!=null || !modipa || pddCore.isPddEsterna(soggetto.getPortaDominio())) {
					
					boolean bothSslAndToken = false;
					if(modipa && isModiSicurezzaMessaggio && isAutorizzazioneToken) {
						bothSslAndToken = true;
					}
					listServiziApplicativiTmp = saCore.soggettiServizioApplicativoList(idSoggetto,userLogin,tipoAutenticazione, appId, filtroTipoSA, bothSslAndToken, tokenPolicy, tokenPolicyOR);
					
					if(tokenWithHttpsEnabled) {
						// nop: il metodo sopra già recupera tutti gli applicativi che hanno definito un token o un ssl che contiene a sua volta un token se gli si passa tipoAutenticazione=TOKEN
					}
				}
				else {
					// modipa, soggetto interno
					
					if(isModiSicurezzaMessaggio && isAutorizzazioneToken) {
						ProtocolFiltroRicercaServiziApplicativi filtro = new ProtocolFiltroRicercaServiziApplicativi();
						filtro.setTipoSoggetto(idSoggetto.getTipo());
						filtro.setNomeSoggetto(idSoggetto.getNome());
						filtro.setProtocolProperties(new ArrayList<>());
						filtro.setTipo(filtroTipoSA);
						
						FiltroRicercaProtocolPropertyConfig ppSicMsg = new FiltroRicercaProtocolPropertyConfig();
						ppSicMsg.setName(CostantiDB.MODIPA_SICUREZZA_MESSAGGIO);
						ppSicMsg.setValueAsBoolean(true);
						filtro.getProtocolProperties().add(ppSicMsg);
						
						FiltroRicercaProtocolPropertyConfig ppSicToken = new FiltroRicercaProtocolPropertyConfig();
						ppSicToken.setName(CostantiDB.MODIPA_SICUREZZA_TOKEN);
						ppSicToken.setValueAsBoolean(true);
						filtro.getProtocolProperties().add(ppSicToken);
						
						List<IDServizioApplicativo> list = saCore.getAllIdServiziApplicativi(filtro); 
						if(list!=null && !list.isEmpty()) {
							listServiziApplicativiTmp = new ArrayList<>();
							for (IDServizioApplicativo idSA : list) {
								IDServizioApplicativoDB idSADB = new IDServizioApplicativoDB(idSA);
								idSADB.setId(saCore.getIdServizioApplicativo(idSA.getIdSoggettoProprietario(), idSA.getNome()));
								listServiziApplicativiTmp.add(idSADB);	
							}
						}
					}
					else {
					
						if(isModiSicurezzaMessaggio) {
							ProtocolFiltroRicercaServiziApplicativi filtro = new ProtocolFiltroRicercaServiziApplicativi();
							filtro.setTipoSoggetto(idSoggetto.getTipo());
							filtro.setNomeSoggetto(idSoggetto.getNome());
							filtro.setProtocolProperties(new ArrayList<>());
							filtro.setTipo(filtroTipoSA);
							FiltroRicercaProtocolPropertyConfig pp = new FiltroRicercaProtocolPropertyConfig();
							pp.setName(CostantiDB.MODIPA_SICUREZZA_MESSAGGIO);
							pp.setValueAsBoolean(true);
							filtro.getProtocolProperties().add(pp);
							List<IDServizioApplicativo> list = saCore.getAllIdServiziApplicativi(filtro); 
							if(list!=null && !list.isEmpty()) {
								listServiziApplicativiTmp = new ArrayList<>();
								for (IDServizioApplicativo idSA : list) {
									IDServizioApplicativoDB idSADB = new IDServizioApplicativoDB(idSA);
									idSADB.setId(saCore.getIdServizioApplicativo(idSA.getIdSoggettoProprietario(), idSA.getNome()));
									listServiziApplicativiTmp.add(idSADB);	
								}
							}
						}
						
						if(isAutorizzazioneToken) {
							ProtocolFiltroRicercaServiziApplicativi filtro = new ProtocolFiltroRicercaServiziApplicativi();
							filtro.setTipoSoggetto(idSoggetto.getTipo());
							filtro.setNomeSoggetto(idSoggetto.getNome());
							filtro.setProtocolProperties(new ArrayList<>());
							filtro.setTipo(filtroTipoSA);
							FiltroRicercaProtocolPropertyConfig pp = new FiltroRicercaProtocolPropertyConfig();
							pp.setName(CostantiDB.MODIPA_SICUREZZA_TOKEN);
							pp.setValueAsBoolean(true);
							filtro.getProtocolProperties().add(pp);
							List<IDServizioApplicativo> list = saCore.getAllIdServiziApplicativi(filtro); 
							if(list!=null && !list.isEmpty()) {
								if(listServiziApplicativiTmp==null) {
									listServiziApplicativiTmp = new ArrayList<>();
								}
								for (IDServizioApplicativo idSA : list) {
									IDServizioApplicativoDB idSADB = new IDServizioApplicativoDB(idSA);
									idSADB.setId(saCore.getIdServizioApplicativo(idSA.getIdSoggettoProprietario(), idSA.getNome()));
									if(!listServiziApplicativiTmp.contains(idSADB)) {
										listServiziApplicativiTmp.add(idSADB);
									}
								}
							}
						}
					}
				}
				List<IDServizioApplicativoDB> listServiziApplicativiTmpUnique = new ArrayList<>();
				
				// scarto i sa già associati
				if(listServiziApplicativiTmp!=null && listServiziApplicativiTmp.size()>0) {
					for (IDServizioApplicativoDB sa : listServiziApplicativiTmp) {
						boolean found = false;
						if(regola!=null) {
							if(this.saTrasformazioniList!=null && this.saTrasformazioniList.sizeServizioApplicativoList()>0) {
								for (TrasformazioneRegolaApplicabilitaServizioApplicativo saAssociatoPA : this.saTrasformazioniList.getServizioApplicativoList()) { 
									if(saAssociatoPA.getNome().equals(sa.getNome()) && 
											saAssociatoPA.getTipoSoggettoProprietario().equals(soggetto.getTipo()) && 
											saAssociatoPA.getNomeSoggettoProprietario().equals(soggetto.getNome())) {
										found = true;
										break;
									}
								}
							}
						}
						else {
							if(this.saAutorizzatiList!=null && this.saAutorizzatiList.sizeServizioApplicativoList()>0) {
								for (PortaApplicativaAutorizzazioneServizioApplicativo saAssociatoPA : this.saAutorizzatiList.getServizioApplicativoList()) { 
									if(saAssociatoPA.getNome().equals(sa.getNome()) && 
											saAssociatoPA.getTipoSoggettoProprietario().equals(soggetto.getTipo()) && 
											saAssociatoPA.getNomeSoggettoProprietario().equals(soggetto.getNome())) {
										found = true;
										break;
									}
								}
							}
						}
						if(!found) {
							listServiziApplicativiTmpUnique.add(sa);
						}
					}
				}

				if(listServiziApplicativiTmpUnique!=null && listServiziApplicativiTmpUnique.size()>0) {
					String id = soggetto.getId().toString();
					soggettiListBuild.add(id);
					soggettiLabelListBuild.add(porteApplicativeHelper.getLabelNomeSoggetto(protocollo, soggetto.getTipo() , soggetto.getNome()));
					this.listServiziApplicativi.put(id, listServiziApplicativiTmpUnique);
					if(this.idSoggettoToAdd==null || "".equals(this.idSoggettoToAdd)) {
						this.idSoggettoToAdd = id;
					}
					if(this.soggettiDBList_trasformazioniSoggettiToken!=null) {
						IDSoggettoDB idS = new IDSoggettoDB(soggetto.getTipo(), soggetto.getNome());
						idS.setId(soggetto.getId());
						this.soggettiDBList_trasformazioniSoggettiToken.add(idS);
					}
				}
			}
			
			if(soggettiListBuild!=null && soggettiListBuild.size()>0) {
				this.soggettiList = soggettiListBuild.toArray(new String[1]);
				this.soggettiListLabel = soggettiLabelListBuild.toArray(new String[1]);
			}
		}
	}
}
