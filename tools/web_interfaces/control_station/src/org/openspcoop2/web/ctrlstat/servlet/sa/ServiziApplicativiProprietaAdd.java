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
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ServiziApplicativiProprietaAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ServiziApplicativiProprietaAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
 
		try {
			Boolean singlePdD = ServletUtils.getObjectFromSession(request, session, Boolean.class, CostantiControlStation.SESSION_PARAMETRO_SINGLE_PDD);
			
			ServiziApplicativiHelper saHelper = new ServiziApplicativiHelper(request, pd, session);
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione
			Integer parentSA = ServletUtils.getIntegerAttributeFromSession(ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT, session, request);
			if(parentSA == null) parentSA = ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE;
			Boolean useIdSogg = parentSA == ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_SOGGETTO;
			
			parentSA = useIdSogg ? ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_SOGGETTO : ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE;
			
			String id = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID);
			String provider = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			String dominio = saHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO);	
			int idServizioApplicativo = Integer.parseInt(id);
			String nome = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROP_NOME);
			String valore = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROP_VALORE);  
			
			// Preparo il menu
			saHelper.makeMenu();
			
			ServiziApplicativiCore saCore = new ServiziApplicativiCore();
			
			ServizioApplicativo sa = saCore.getServizioApplicativo(idServizioApplicativo);
			String oldNome = sa.getNome();
			IDSoggetto oldIdSoggetto = new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario());
			IDServizioApplicativo oldIdServizioApplicativo = new IDServizioApplicativo();
			oldIdServizioApplicativo.setIdSoggettoProprietario(oldIdSoggetto);
			oldIdServizioApplicativo.setNome(oldNome);
			int idProv = sa.getIdSoggetto().intValue();

			List<Parameter> parametersServletSAChange = new ArrayList<Parameter>();
			Parameter pIdSA = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID, sa.getId()+"");
			parametersServletSAChange.add(pIdSA);
			Parameter pIdSoggettoSA = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, idProv+"");
			parametersServletSAChange.add(pIdSoggettoSA);
			if(dominio != null) {
				Parameter pDominio = new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO, dominio);
				parametersServletSAChange.add(pDominio);
			}
			
			// Tipi protocollo supportati
			List<String> listaTipiProtocollo = saCore.getProtocolliByFilter(session, true, PddTipologia.OPERATIVO, false);
			String superUser = ServletUtils.getUserLoginFromSession(session);
			
			// Prendo la lista di soggetti
			String soggettoMultitenantSelezionato = null;
			if(!useIdSogg && saHelper.isSoggettoMultitenantSelezionato()) {
				soggettoMultitenantSelezionato = saHelper.getSoggettoMultitenantSelezionato();
			}
			ServiziApplicativiGeneralInfo generalInfo = ServiziApplicativiUtilities.getGeneralInfo(useIdSogg, provider, listaTipiProtocollo, 
					saCore, saHelper, superUser, singlePdD, soggettoMultitenantSelezionato, dominio);
					
			String tipoENomeSoggetto = generalInfo.getTipoENomeSoggetto();
			provider = generalInfo.getProvider();
			
			String labelApplicativi = ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI;
			String labelApplicativiDi = ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_DI;
			if(saHelper.isModalitaCompleta()==false) {
				labelApplicativi = ServiziApplicativiCostanti.LABEL_APPLICATIVI;
				labelApplicativiDi = ServiziApplicativiCostanti.LABEL_PARAMETRO_APPLICATIVI_DI;
			}
			
			List<Parameter> lstParam = new ArrayList<Parameter>();
			if(useIdSogg){
				lstParam.add(new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
				lstParam.add(new Parameter(labelApplicativiDi + tipoENomeSoggetto, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST, new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,provider)));
				lstParam.add(new Parameter(sa.getNome(), ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE, parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])));
				lstParam.add(new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROPRIETA, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_PROPRIETA_LIST , parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])));
			}else {
				lstParam.add(new Parameter(labelApplicativi, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST));
				lstParam.add(new Parameter(sa.getNome(), ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE, parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])));
				lstParam.add(new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROPRIETA, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_PROPRIETA_LIST , parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])));
			}
			lstParam.add(ServletUtils.getParameterAggiungi());

			// Se nome = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (saHelper.isEditModeInProgress()) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = saHelper.addServizioApplicativoHiddenToDati(dati, id, idProv+"", dominio, sa.getNome());

				dati = saHelper.addProprietaToDati(TipoOperazione.ADD, saHelper.getSize(), nome, valore, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_PROPRIETA,
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = saHelper.serviziApplicativiProprietaCheckData(TipoOperazione.ADD);
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = saHelper.addServizioApplicativoHiddenToDati(dati, id, idProv+"", dominio, sa.getNome());
				
				dati = saHelper.addProprietaToDati(TipoOperazione.ADD, saHelper.getSize(), nome, valore, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_PROPRIETA, 
						ForwardParams.ADD());
			}

			// Inserisco la property della porta applicativa nel db
			Proprieta ssp = new Proprieta();
			ssp.setNome(nome);
			ssp.setValore(valore);
			sa.addProprieta(ssp);

			String userLogin = ServletUtils.getUserLoginFromSession(session);
			
			saCore.performUpdateOperation(userLogin, saHelper.smista(), sa);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(request, session, Search.class);

			int idLista = Liste.SERVIZI_APPLICATIVI_PROP;

			ricerca = saHelper.checkSearchParameters(idLista, ricerca);

			List<Proprieta> lista = saCore.serviziApplicativiProprietaList(Integer.parseInt(id), ricerca);

			saHelper.prepareServiziApplicativiProprietaList(sa, ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_PROPRIETA, 
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_PROPRIETA, 
					ForwardParams.ADD());
		}  
	}

	
}
