/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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


package org.openspcoop2.web.ctrlstat.servlet.ac;

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
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoCooperazionePartecipanti;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * accordiCoopPartecipantiAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiCooperazionePartecipantiAdd extends Action {


	private String idAccordoCoop="";
	private String partecipante="" ;

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);

		IDAccordoCooperazioneFactory idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();



		try {
			AccordiCooperazioneHelper acHelper = new AccordiCooperazioneHelper(request, pd, session);

			this.idAccordoCoop = request.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID);
			this.partecipante = request.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_PARTECIPANTE);

			String tipoSICA = request.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_TIPO_SICA);
			if("".equals(tipoSICA))
				tipoSICA = null;

			AccordiCooperazioneCore acCore = new AccordiCooperazioneCore();
			SoggettiCore soggettiCore = new SoggettiCore(acCore);

			// Preparo il menu
			acHelper.makeMenu();

			AccordoCooperazione ac = acCore.getAccordoCooperazione(Long.parseLong(this.idAccordoCoop));

			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(ac.getSoggettoReferente().getTipo());
			//String profiloSoggettoReferente = soggettiCore.getSoggettoRegistro(new IDSoggetto(ac.getSoggettoReferente().getTipo(), ac.getSoggettoReferente().getNome())).getVersioneProtocollo();
			List<String> tipiSoggettiCompatibiliAccordo = soggettiCore.getTipiSoggettiGestitiProtocollo(protocollo);

			//prendo partecipanti gia' inseriti in accordo
			ArrayList<String> tmpInseriti = new ArrayList<String>();
			if(ac.getElencoPartecipanti()!=null){
				AccordoCooperazionePartecipanti partecipantiInseriti = ac.getElencoPartecipanti();
				for (int i = 0; i < partecipantiInseriti.sizeSoggettoPartecipanteList(); i++) {
					IdSoggetto accordoCooperazioneElencoPartecipanti = partecipantiInseriti.getSoggettoPartecipante(i);
					String tipo = accordoCooperazioneElencoPartecipanti.getTipo();
					String nome = accordoCooperazioneElencoPartecipanti.getNome();
					tmpInseriti.add(tipo+"/"+nome);
				}
			}

			//lista partecipanti non inseriti
			ArrayList<String> partecipantiNonInseriti = new ArrayList<String>();
			partecipantiNonInseriti.add("-");

			List<Soggetto> soggetti = null;
			if(acCore.isVisioneOggettiGlobale(userLogin)){
				soggetti = soggettiCore.soggettiRegistroList(null, new Search(true));
			}else{
				soggetti = soggettiCore.soggettiRegistroList(userLogin, new Search(true));
			}
			for (Soggetto sogg : soggetti) {
				String s = sogg.getTipo()+"/"+sogg.getNome();
				if(!tmpInseriti.contains(s)){
					if(tipiSoggettiCompatibiliAccordo.contains(sogg.getTipo()))
						partecipantiNonInseriti.add(s);
				}
			}



			// Se nome = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (ServletUtils.isEditModeInProgress(request)) {
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<Parameter>();
				lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST));
				lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_PARTECIPANTI_DI + idAccordoCooperazioneFactory.getUriFromAccordo(ac),
						AccordiCooperazioneCostanti.SERVLET_NAME_AC_PARTECIPANTI_LIST, 
						new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID, ac.getId() + ""),
						new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME, ac.getNome())
						));
				
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null));
				
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = acHelper.addPartecipanteToDati(TipoOperazione.ADD, this.idAccordoCoop,
						partecipantiNonInseriti.toArray(new String[partecipantiNonInseriti.size()]), dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiCooperazioneCostanti.OBJECT_NAME_AC_PARTECIPANTI,
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = true;

			if (!isOk) {
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<Parameter>();
				lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST));
				lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_PARTECIPANTI_DI + idAccordoCooperazioneFactory.getUriFromAccordo(ac),
						AccordiCooperazioneCostanti.SERVLET_NAME_AC_PARTECIPANTI_LIST, 
						new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID, ac.getId() + ""),
						new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME, ac.getNome())
						));
				
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null));

				pd.setSearchDescription("");


				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
 
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = acHelper.addPartecipanteToDati(TipoOperazione.ADD, this.idAccordoCoop,
						partecipantiNonInseriti.toArray(new String[partecipantiNonInseriti.size()]), dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiCooperazioneCostanti.OBJECT_NAME_AC_PARTECIPANTI, ForwardParams.ADD());
				
			}

			if(!"-".equals(this.partecipante)){
				String tipo = this.partecipante.split("/")[0];
				String nome = this.partecipante.split("/")[1];

				IdSoggetto asog = new IdSoggetto();
				asog.setTipo(tipo);
				asog.setNome(nome);
				if(ac.getElencoPartecipanti()==null){
					ac.setElencoPartecipanti(new AccordoCooperazionePartecipanti());
				}
				ac.getElencoPartecipanti().addSoggettoPartecipante(asog);

				acCore.performUpdateOperation(userLogin, acHelper.smista(), ac);
			}



			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			List<IDSoggetto> lista = acCore.accordiCoopPartecipantiList(ac.getId(),ricerca);

			acHelper.prepareAccordiCoopPartecipantiList(ac,lista,ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiCooperazioneCostanti.OBJECT_NAME_AC_PARTECIPANTI, ForwardParams.ADD());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiCooperazioneCostanti.OBJECT_NAME_AC_PARTECIPANTI, 
					ForwardParams.ADD());
		}  
	}


}
