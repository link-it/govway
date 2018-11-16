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


package org.openspcoop2.web.ctrlstat.servlet.sa;

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
import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.ServizioApplicativoRuoli;
import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.driver.FiltroRicercaRuoli;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.ruoli.RuoliCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ServiziApplicativiRuoliAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ServiziApplicativiRuoliAdd extends Action {

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
			ServiziApplicativiHelper saHelper = new ServiziApplicativiHelper(request, pd, session);

			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione
			Integer parentSA = ServletUtils.getIntegerAttributeFromSession(ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT, session);
			if(parentSA == null) parentSA = ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE;
			Boolean useIdSogg = parentSA == ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_SOGGETTO;
			
			String idsil = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO);
			int idSilInt = Integer.parseInt(idsil);
			
			String nome = saHelper.getParameter(CostantiControlStation.PARAMETRO_RUOLO);

			String provider = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);

			String accessDaChangeTmp = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_ACCESSO_DA_CHANGE);
			boolean accessDaChange = ServletUtils.isCheckBoxEnabled(accessDaChangeTmp);
			
			ServiziApplicativiCore saCore = new ServiziApplicativiCore();
			SoggettiCore soggettiCore = new SoggettiCore(saCore);

			ServizioApplicativo sa = saCore.getServizioApplicativo(idSilInt);
			String nomeservizioApplicativo = sa.getNome();		
			
			FiltroRicercaRuoli filtroRuoli = new FiltroRicercaRuoli();
			filtroRuoli.setContesto(RuoloContesto.QUALSIASI); // gli applicativi possono essere usati anche nelle erogazioni.
			filtroRuoli.setTipologia(RuoloTipologia.INTERNO);
			
			List<String> ruoli = new ArrayList<>();
			if(sa.getInvocazionePorta().getRuoli()!=null && sa.getInvocazionePorta().getRuoli().getRuoloList()!=null && sa.getInvocazionePorta().getRuoli().getRuoloList().size()>0){
				for (Ruolo ruolo : sa.getInvocazionePorta().getRuoli().getRuoloList()) {
					ruoli.add(ruolo.getNome());	
				}
			}

			String tipoENomeSoggetto = "";
			String nomeProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(sa.getTipoSoggettoProprietario());
			if(provider == null){
				provider = "-1";
			}else {
				org.openspcoop2.core.config.Soggetto soggetto = soggettiCore.getSoggetto(Long.parseLong(provider)); 
				tipoENomeSoggetto = saHelper.getLabelNomeSoggetto(nomeProtocollo, soggetto.getTipo() , soggetto.getNome());
			}
			
			@SuppressWarnings("unused")
			long soggLong = -1;
			// se ho fatto la add 
			if(useIdSogg)
				if(provider != null && !provider.equals("")){
				soggLong = Long.parseLong(provider);
			}
			
			
			// Preparo il menu
			saHelper.makeMenu();

			String labelApplicativi = ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI;
			String labelApplicativiDi = ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_DI;
			if(saHelper.isModalitaCompleta()==false) {
				labelApplicativi = ServiziApplicativiCostanti.LABEL_APPLICATIVI;
				labelApplicativiDi = ServiziApplicativiCostanti.LABEL_PARAMETRO_APPLICATIVI_DI;
			}
			
			
			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (saHelper.isEditModeInProgress()) {
				
				// setto la barra del titolo
				if(accessDaChange) {
					ServletUtils.setPageDataTitle_ServletFirst(pd, labelApplicativi, 
							ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST);
					ServletUtils.appendPageDataTitle(pd, 
							new Parameter(nomeservizioApplicativo, 
									ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE, 
									new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID, sa.getId()+""),
									new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, sa.getIdSoggetto()+"")));
					ServletUtils.appendPageDataTitle(pd, 
							new Parameter(RuoliCostanti.LABEL_RUOLI, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_RUOLI_LIST,
									new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO,sa.getId()+""),
									new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_ACCESSO_DA_CHANGE,accessDaChangeTmp)
									)
							);
					ServletUtils.appendPageDataTitle(pd, 
							ServletUtils.getParameterAggiungi());
				}
				else {
					List<Parameter> lstParm = new ArrayList<>();
					if(useIdSogg){
						lstParm.add(new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
						lstParm.add(new Parameter(labelApplicativiDi + tipoENomeSoggetto,
								ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST,
								new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,provider)));
					}else {
						lstParm.add(new Parameter(labelApplicativi, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST));
					}
					if(useIdSogg) {
						lstParm.add(new Parameter( "Ruoli di " + nomeservizioApplicativo, null));
					}
					else {
						lstParm.add(new Parameter( "Ruoli di " + nomeservizioApplicativo, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_RUOLI_LIST,
								new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO,sa.getId()+""),
								new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_ACCESSO_DA_CHANGE,accessDaChangeTmp)));
					}
					lstParm.add(ServletUtils.getParameterAggiungi());
					ServletUtils.setPageDataTitle(pd,lstParm); 
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				DataElement de = new DataElement();
				de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO);
				de.setValue(idsil);
				de.setType(DataElementType.HIDDEN);
				dati.addElement(de);
				
				if(useIdSogg){
					de = new DataElement();
					de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
					de.setValue(provider);
					de.setType(DataElementType.HIDDEN);
					dati.addElement(de);
				}
				
				dati = saHelper.addRuoliToDati(TipoOperazione.ADD, dati, false, filtroRuoli, nome, ruoli, false, true, true, accessDaChangeTmp);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_RUOLI,
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = saHelper.ruoloCheckData(TipoOperazione.ADD, nome, ruoli);
			if (!isOk) {
				// setto la barra del titolo
				List<Parameter> lstParm = new ArrayList<>();
				if(useIdSogg){
					lstParm.add(new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
					lstParm.add(new Parameter(labelApplicativiDi + tipoENomeSoggetto,
							ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST,
							new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,provider)));
				}else {
					lstParm.add(new Parameter(labelApplicativi, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST));
				}
				lstParm.add(new Parameter( "Ruoli di " + nomeservizioApplicativo, null));
				lstParm.add(ServletUtils.getParameterAggiungi());
				ServletUtils.setPageDataTitle(pd,lstParm); 
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				DataElement de = new DataElement();
				de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO);
				de.setValue(idsil);
				de.setType(DataElementType.HIDDEN);
				dati.addElement(de);
				
				if(useIdSogg){
					de = new DataElement();
					de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
					de.setValue(provider);
					de.setType(DataElementType.HIDDEN);
					dati.addElement(de);
				}
				
				dati = saHelper.addRuoliToDati(TipoOperazione.ADD, dati, false, filtroRuoli, nome, ruoli, false, true, true, accessDaChangeTmp);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_RUOLI, 
						ForwardParams.ADD());
			}

			// Inserisco
			Ruolo ruolo = new Ruolo();
			ruolo.setNome(nome);
			if(sa.getInvocazionePorta().getRuoli()==null){
				sa.getInvocazionePorta().setRuoli(new ServizioApplicativoRuoli());
			}
			sa.getInvocazionePorta().getRuoli().addRuolo(ruolo);
			
			saCore.performUpdateOperation(userLogin, saHelper.smista(), sa);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			List<String> lista = saCore.servizioApplicativoRuoliList(idSilInt, ricerca);
					
			saHelper.prepareRuoliList(ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_RUOLI,
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_RUOLI, ForwardParams.ADD());
		}
	}


}
