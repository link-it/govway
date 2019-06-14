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


package org.openspcoop2.web.ctrlstat.servlet.pa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServiziApplicativi;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * PorteApplicativeServizioApplicativoAutorizzatoAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteApplicativeServizioApplicativoAutorizzatoAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		String userLogin = ServletUtils.getUserLoginFromSession(session);	
		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session);
		if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;

		try {
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			String idPorta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(idPorta);
			String idsogg = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			
			String idSoggettoToAdd = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SOGGETTO);
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			
			String idSAToAdd = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_AUTORIZZATO);

			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteApplicativeCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(porteApplicativeCore);

			boolean escludiSoggettoErogatore = false;

			
			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			// Prendo nome, tipo e pdd del soggetto
			String protocollo = null;
			if(porteApplicativeCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(soggInt);
				protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(soggetto.getTipo());
			}
			else{
				org.openspcoop2.core.config.Soggetto soggetto = soggettiCore.getSoggetto(soggInt);
				protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(soggetto.getTipo());
			}

			//decodifica soggetto scelto
			String tipoSoggettoScelto = null;
			String nomeSoggettoScelto = null;
			String nomeSAScelto = null;
			if(idSoggettoToAdd != null) {
				long soggettoToAddInt = Long.parseLong(idSoggettoToAdd);
				
				if(porteApplicativeCore.isRegistroServiziLocale()){
					org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(soggettoToAddInt);
					tipoSoggettoScelto = soggetto.getTipo();
					nomeSoggettoScelto = soggetto.getNome();
				}
				else{
					org.openspcoop2.core.config.Soggetto soggetto = soggettiCore.getSoggetto(soggettoToAddInt);
					tipoSoggettoScelto = soggetto.getTipo();
					nomeSoggettoScelto = soggetto.getNome();
				}
				
				if(idSAToAdd != null) {
					long idSAToAddInt = Long.parseLong(idSAToAdd);
					ServizioApplicativo servizioApplicativo = saCore.getServizioApplicativo(idSAToAddInt);
					nomeSAScelto = servizioApplicativo.getNome();
				}
			}
			
			// Prendo nome della porta applicativa
			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			String nomePorta = pa.getNome();
			CredenzialeTipo tipoAutenticazione = CredenzialeTipo.toEnumConstant(pa.getAutenticazione());
			

			// lista soggetti disponibili
			String[] soggettiList = null;
			String[] soggettiListLabel = null;
			List<org.openspcoop2.core.registry.Soggetto> listSoggetti = soggettiCore.getSoggettiOperativi(protocollo);
			if(listSoggetti!=null && !listSoggetti.isEmpty() && escludiSoggettoErogatore) {
				for (int i = 0; i < listSoggetti.size(); i++) {
					Soggetto soggettoCheck = listSoggetti.get(i);
					if(soggettoCheck.getTipo().equals(pa.getTipoSoggettoProprietario()) && soggettoCheck.getNome().equals(pa.getNomeSoggettoProprietario())) {
						listSoggetti.remove(i);
						break;
					}
				}
			}
			
			
			PortaApplicativaAutorizzazioneServiziApplicativi saList = pa.getServiziApplicativiAutorizzati();
			int saSize = saList!=null ? saList.sizeServizioApplicativoList() : 0;
			Map<String,List<ServizioApplicativo>> listServiziApplicativi = new HashMap<>();
			if(listSoggetti!=null && !listSoggetti.isEmpty()) {
				List<String> soggettiListBuild = new ArrayList<>();
				List<String> soggettiLabelListBuild = new ArrayList<>();
				
				for (org.openspcoop2.core.registry.Soggetto soggetto : listSoggetti) {
					IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipo(), soggetto.getNome());
					List<ServizioApplicativo> listServiziApplicativiTmp = saCore.soggettiServizioApplicativoList(idSoggetto,userLogin,tipoAutenticazione);
					List<ServizioApplicativo> listServiziApplicativiTmpUnique = new ArrayList<>();
					
					// scarto i sa già associati
					if(listServiziApplicativiTmp!=null && listServiziApplicativiTmp.size()>0) {
						for (ServizioApplicativo sa : listServiziApplicativiTmp) {
							boolean found = false;
							if(saList!=null && saList.sizeServizioApplicativoList()>0) {
								for (PortaApplicativaAutorizzazioneServizioApplicativo saAssociatoPA : saList.getServizioApplicativoList()) { 
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
						listServiziApplicativi.put(id, listServiziApplicativiTmpUnique);
						if(idSoggettoToAdd==null || "".equals(idSoggettoToAdd)) {
							idSoggettoToAdd = id;
						}
					}
				}
				
				if(soggettiListBuild!=null && soggettiListBuild.size()>0) {
					soggettiList = soggettiListBuild.toArray(new String[1]);
					soggettiListLabel = soggettiLabelListBuild.toArray(new String[1]);
				}
			}

		
			
			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);
			
			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI,
						pa);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI_CONFIG_DI+nomePorta;
			}
			
			lstParam.add(new Parameter(labelPerPorta, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI, 
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)));
			
			String labelPagLista = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_APPLICATIVI_CONFIG;
			
			lstParam.add(new Parameter(labelPagLista,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_AUTORIZZATO_LIST,
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
					));
			lstParam.add(ServletUtils.getParameterAggiungi());
			
			// Se servizioApplicativohid = null, devo visualizzare la pagina per
			// l'inserimento dati
			if (porteApplicativeHelper.isEditModeInProgress()) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteApplicativeHelper.addPorteServizioApplicativoAutorizzatiToDati(TipoOperazione.ADD, dati, soggettiListLabel, soggettiList, idSoggettoToAdd, saSize, 
						listServiziApplicativi, idSAToAdd, true);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta, idAsps,dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_AUTORIZZATO,
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.porteAppServizioApplicativoAutorizzatiCheckData(TipoOperazione.ADD);
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addPorteServizioApplicativoAutorizzatiToDati(TipoOperazione.ADD, dati, soggettiListLabel, soggettiList, idSoggettoToAdd, saSize, 
						listServiziApplicativi, idSAToAdd, true);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta, idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_AUTORIZZATO, 
						ForwardParams.ADD());
			}

			// Inserisco il servizioApplicativo nel db
			PortaApplicativaAutorizzazioneServizioApplicativo paSaAutorizzato = new PortaApplicativaAutorizzazioneServizioApplicativo();
			paSaAutorizzato.setNome(nomeSAScelto);
			paSaAutorizzato.setTipoSoggettoProprietario(tipoSoggettoScelto);
			paSaAutorizzato.setNomeSoggettoProprietario(nomeSoggettoScelto);
				
			if(saList != null)
				saList.addServizioApplicativo(paSaAutorizzato);
			else {
				saList = new PortaApplicativaAutorizzazioneServiziApplicativi();
				saList.addServizioApplicativo(paSaAutorizzato);
				pa.setServiziApplicativiAutorizzati(saList);
			}

			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_AUTORIZZATO;

			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);

			List<PortaApplicativaAutorizzazioneServizioApplicativo> lista = porteApplicativeCore.porteAppServiziApplicativiAutorizzatiList(Integer.parseInt(idPorta), ricerca);

			porteApplicativeHelper.preparePorteAppServizioApplicativoAutorizzatoList(nomePorta, ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_AUTORIZZATO, 
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_AUTORIZZATO,
					ForwardParams.ADD());
		} 
	}
}
