/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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


package org.openspcoop2.web.ctrlstat.servlet.ruoli;

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
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteApplicative;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.config.driver.FiltroRicercaServiziApplicativi;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * RuoliChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class RuoliChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);	

		try {
			RuoliHelper ruoliHelper = new RuoliHelper(request, pd, session);

			String id = ruoliHelper.getParameter(RuoliCostanti.PARAMETRO_RUOLO_ID);
			long ruoloId = Long.parseLong(id);
			String nome = ruoliHelper.getParameter(RuoliCostanti.PARAMETRO_RUOLO_NOME);
			String descrizione = ruoliHelper.getParameter(RuoliCostanti.PARAMETRO_RUOLO_DESCRIZIONE);
			String tipologia = ruoliHelper.getParameter(RuoliCostanti.PARAMETRO_RUOLO_TIPOLOGIA);
			String nomeEsterno = ruoliHelper.getParameter(RuoliCostanti.PARAMETRO_RUOLO_NOME_ESTERNO);
			String contesto = ruoliHelper.getParameter(RuoliCostanti.PARAMETRO_RUOLO_CONTESTO);
			
			RuoliCore ruoliCore = new RuoliCore();

			// Preparo il menu
			ruoliHelper.makeMenu();

			// Prendo il ruolo
			Ruolo ruolo  = ruoliCore.getRuolo(ruoloId);
			
			// Se nomehid = null, devo visualizzare la pagina per la
			// modifica dati
			if (ServletUtils.isEditModeInProgress(request)) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletChange(pd, RuoliCostanti.LABEL_RUOLI, 
						RuoliCostanti.SERVLET_NAME_RUOLI_LIST, ruolo.getNome());
				

				// Prendo i dati dal db solo se non sono stati passati
				if (nome == null) {
					nome = ruolo.getNome();
				}
				if (descrizione == null) {
					descrizione = ruolo.getDescrizione();
				}
				if (tipologia == null) {
					tipologia = ruolo.getTipologia().getValue();
					nomeEsterno = ruolo.getNomeEsterno();
				}
				if (contesto == null) {
					contesto = ruolo.getContestoUtilizzo().getValue();
				}


				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = ruoliHelper.addRuoloToDati(TipoOperazione.CHANGE, ruoloId, nome, descrizione, tipologia, nomeEsterno, contesto, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						RuoliCostanti.OBJECT_NAME_RUOLI, 
						ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			boolean isOk = ruoliHelper.ruoloCheckData(TipoOperazione.CHANGE, ruolo);
			if (!isOk) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletChange(pd, RuoliCostanti.LABEL_RUOLI, 
						RuoliCostanti.SERVLET_NAME_RUOLI_LIST, ruolo.getNome());

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = ruoliHelper.addRuoloToDati(TipoOperazione.CHANGE, ruoloId, nome, descrizione, tipologia, nomeEsterno, contesto, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						RuoliCostanti.OBJECT_NAME_RUOLI, 
						ForwardParams.CHANGE());
			}

			// Modifico i dati del registro nel db
			Ruolo ruoloNEW = new Ruolo();
			ruoloNEW.setNome(nome);
			ruoloNEW.setDescrizione(descrizione);
			ruoloNEW.setTipologia(RuoloTipologia.toEnumConstant(tipologia, true));
			if(ruoloNEW.getTipologia()!=null && (RuoloTipologia.QUALSIASI.equals(ruoloNEW.getTipologia()) || RuoloTipologia.ESTERNO.equals(ruoloNEW.getTipologia()))) {
				String n = nomeEsterno;
				if(n!=null) {
					n = n.trim();
				}
				ruoloNEW.setNomeEsterno(n);
			}
			ruoloNEW.setContestoUtilizzo(RuoloContesto.toEnumConstant(contesto, true));
			ruoloNEW.setSuperUser(userLogin);
			
			ruoloNEW.setOldIDRuoloForUpdate(new IDRuolo(nome));

			List<Object> listOggettiDaAggiornare = new ArrayList<>();
			
			listOggettiDaAggiornare.add(ruoloNEW);
			
			if(ruolo.getNome().equals(nome)==false){
				
				// e' stato modificato il nome
				
				IDRuolo oldIdRuolo = ruoloNEW.getOldIDRuoloForUpdate();
				oldIdRuolo.setNome(ruolo.getNome());
				
				
				
				
				// Cerco se utilizzato in soggetti
				SoggettiCore soggettiCore = new SoggettiCore(ruoliCore);
				FiltroRicercaSoggetti filtroRicercaSoggetti = new FiltroRicercaSoggetti();
				filtroRicercaSoggetti.setIdRuolo(oldIdRuolo);
				List<IDSoggetto> listSoggetti = soggettiCore.getAllIdSoggettiRegistro(filtroRicercaSoggetti);
				if(listSoggetti!=null && listSoggetti.size()>0){
					for (IDSoggetto idSoggettoWithRuolo : listSoggetti) {
						Soggetto soggettoWithRuolo = soggettiCore.getSoggettoRegistro(idSoggettoWithRuolo);
						if(soggettoWithRuolo.getRuoli()!=null){
							for (org.openspcoop2.core.registry.RuoloSoggetto ruoloSoggetto : soggettoWithRuolo.getRuoli().getRuoloList()) {
								if(ruoloSoggetto.getNome().equals(oldIdRuolo.getNome())){
									ruoloSoggetto.setNome(ruoloNEW.getNome());
								}
							}
						}
						listOggettiDaAggiornare.add(soggettoWithRuolo);
					}
				}
				
				
				
				
				// Cerco se utilizzato in servizi applicativi
				ServiziApplicativiCore saCore = new ServiziApplicativiCore(ruoliCore);
				FiltroRicercaServiziApplicativi filtroRicercaSA = new FiltroRicercaServiziApplicativi();
				filtroRicercaSA.setIdRuolo(oldIdRuolo);
				List<IDServizioApplicativo> listSA = saCore.getAllIdServiziApplicativi(filtroRicercaSA);
				if(listSA!=null && listSA.size()>0){
					for (IDServizioApplicativo idServizioApplicativo : listSA) {
						ServizioApplicativo sa = saCore.getServizioApplicativo(idServizioApplicativo);
						if(sa.getInvocazionePorta()!=null && sa.getInvocazionePorta().getRuoli()!=null){
							for (org.openspcoop2.core.config.Ruolo ruoloConfig : sa.getInvocazionePorta().getRuoli().getRuoloList()) {
								if(ruoloConfig.getNome().equals(oldIdRuolo.getNome())){
									ruoloConfig.setNome(ruoloNEW.getNome());
								}
							}
						}
						listOggettiDaAggiornare.add(sa);
					}
				}
				
				
				// Cerco se utilizzato in porte delegate
				PorteDelegateCore pdCore = new PorteDelegateCore(ruoliCore);
				FiltroRicercaPorteDelegate filtroRicercaPD = new FiltroRicercaPorteDelegate();
				filtroRicercaPD.setIdRuolo(oldIdRuolo);
				List<IDPortaDelegata> listPD = pdCore.getAllIdPorteDelegate(filtroRicercaPD);
				if(listPD!=null && listPD.size()>0){
					for (IDPortaDelegata idPD : listPD) {
						PortaDelegata portaDelegata = pdCore.getPortaDelegata(idPD);
						if(portaDelegata.getRuoli()!=null){
							for (org.openspcoop2.core.config.Ruolo ruoloConfig : portaDelegata.getRuoli().getRuoloList()) {
								if(ruoloConfig.getNome().equals(oldIdRuolo.getNome())){
									ruoloConfig.setNome(ruoloNEW.getNome());
								}
							}
						}
						listOggettiDaAggiornare.add(portaDelegata);
					}
				}
				
				
				
				// Cerco se utilizzato in porte applicative
				PorteApplicativeCore paCore = new PorteApplicativeCore(ruoliCore);
				FiltroRicercaPorteApplicative filtroRicercaPA = new FiltroRicercaPorteApplicative();
				filtroRicercaPA.setIdRuolo(oldIdRuolo);
				List<IDPortaApplicativa> listPA = paCore.getAllIdPorteApplicative(filtroRicercaPA);
				if(listPA!=null && listPA.size()>0){
					for (IDPortaApplicativa idPA : listPA) {
						PortaApplicativa portaApplicativa = paCore.getPortaApplicativa(idPA);
						if(portaApplicativa.getRuoli()!=null){
							for (org.openspcoop2.core.config.Ruolo ruoloConfig : portaApplicativa.getRuoli().getRuoloList()) {
								if(ruoloConfig.getNome().equals(oldIdRuolo.getNome())){
									ruoloConfig.setNome(ruoloNEW.getNome());
								}
							}
						}
						listOggettiDaAggiornare.add(portaApplicativa);
					}
				}
				
			}
			
			
			ruoliCore.performUpdateOperation(userLogin, ruoliHelper.smista(), listOggettiDaAggiornare.toArray());

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			List<Ruolo> lista = null;
			if(ruoliCore.isVisioneOggettiGlobale(userLogin)){
				lista = ruoliCore.ruoliList(null, ricerca);
			}else{
				lista = ruoliCore.ruoliList(userLogin, ricerca);
			}
			
			ruoliHelper.prepareRuoliList(ricerca, lista);
			
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					RuoliCostanti.OBJECT_NAME_RUOLI,
					ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					RuoliCostanti.OBJECT_NAME_RUOLI, ForwardParams.CHANGE());
		}
	}
}
