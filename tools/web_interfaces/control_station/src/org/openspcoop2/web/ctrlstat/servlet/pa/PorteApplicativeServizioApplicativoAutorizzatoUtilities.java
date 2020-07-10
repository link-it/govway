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

package org.openspcoop2.web.ctrlstat.servlet.pa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServiziApplicativi;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.driver.FiltroRicercaProtocolProperty;
import org.openspcoop2.core.config.driver.db.IDServizioApplicativoDB;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaServiziApplicativi;
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
	public PortaApplicativaAutorizzazioneServiziApplicativi saList;
	
	public void buildList(PortaApplicativa pa, boolean modipa, String protocollo, boolean escludiSoggettoErogatore,
			String idSoggettoToAdd,
			PorteApplicativeCore paCore, ConsoleHelper porteApplicativeHelper, boolean escludiSAServer) throws Exception {
		
		this.idSoggettoToAdd = idSoggettoToAdd;
		
		CredenzialeTipo tipoAutenticazione = CredenzialeTipo.toEnumConstant(pa.getAutenticazione());
		Boolean appId = null;
		if(CredenzialeTipo.APIKEY.equals(tipoAutenticazione)) {
			ApiKeyState apiKeyState =  new ApiKeyState(paCore.getParametroAutenticazione(pa.getAutenticazione(), pa.getProprietaAutenticazioneList()));
			appId = apiKeyState.appIdSelected;
		}
		
		SoggettiCore soggettiCore = new SoggettiCore(paCore);
		PddCore pddCore = new PddCore(paCore);
		ServiziApplicativiCore saCore = new ServiziApplicativiCore(paCore);
		
		String userLogin = ServletUtils.getUserLoginFromSession(porteApplicativeHelper.getSession());	
		
		List<org.openspcoop2.core.registry.Soggetto> listSoggetti = null; 
		if(modipa) {
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
		
		this.saList = pa.getServiziApplicativiAutorizzati();
		this.saSize = this.saList!=null ? this.saList.sizeServizioApplicativoList() : 0;
		this.listServiziApplicativi = new HashMap<>();
		
		String filtroTipoSA = escludiSAServer ? ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_CLIENT : null;
		if(listSoggetti!=null && !listSoggetti.isEmpty()) {
			List<String> soggettiListBuild = new ArrayList<>();
			List<String> soggettiLabelListBuild = new ArrayList<>();
			
			for (org.openspcoop2.core.registry.Soggetto soggetto : listSoggetti) {
				IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipo(), soggetto.getNome());
				List<IDServizioApplicativoDB> listServiziApplicativiTmp = null;
				if(!modipa || pddCore.isPddEsterna(soggetto.getPortaDominio())) {
					listServiziApplicativiTmp = saCore.soggettiServizioApplicativoList(idSoggetto,userLogin,tipoAutenticazione, appId, filtroTipoSA);
				}
				else {
					// modipa, soggetto interno
					FiltroRicercaServiziApplicativi filtro = new FiltroRicercaServiziApplicativi();
					filtro.setTipoSoggetto(idSoggetto.getTipo());
					filtro.setNomeSoggetto(idSoggetto.getNome());
					filtro.setProtocolProperties(new ArrayList<>());
					filtro.setTipo(filtroTipoSA);
					FiltroRicercaProtocolProperty pp = new FiltroRicercaProtocolProperty();
					pp.setName(porteApplicativeHelper.getProfiloModIPASicurezzaMessaggioPropertyName());
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
				List<IDServizioApplicativoDB> listServiziApplicativiTmpUnique = new ArrayList<>();
				
				// scarto i sa già associati
				if(listServiziApplicativiTmp!=null && listServiziApplicativiTmp.size()>0) {
					for (IDServizioApplicativoDB sa : listServiziApplicativiTmp) {
						boolean found = false;
						if(this.saList!=null && this.saList.sizeServizioApplicativoList()>0) {
							for (PortaApplicativaAutorizzazioneServizioApplicativo saAssociatoPA : this.saList.getServizioApplicativoList()) { 
								if(saAssociatoPA.getNome().equals(sa.getNome()) && 
										saAssociatoPA.getTipoSoggettoProprietario().equals(soggetto.getTipo()) && 
										saAssociatoPA.getNomeSoggettoProprietario().equals(soggetto.getNome())) {
									found = true;
									break;
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
				}
			}
			
			if(soggettiListBuild!=null && soggettiListBuild.size()>0) {
				this.soggettiList = soggettiListBuild.toArray(new String[1]);
				this.soggettiListLabel = soggettiLabelListBuild.toArray(new String[1]);
			}
		}
	}
}
