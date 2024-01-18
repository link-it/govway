/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.apc;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * accordiComponentiAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteComuneComponentiAdd extends Action {

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

			AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);

			String idAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			String idServizioComponente = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_COMPONENTI_COMPONENTE);
			long idAccordoLong = Long.parseLong(idAccordo);
			/**String portType = apcHelper.getParameter("port_type");*/

			String tipoAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_COMPONENTI_TIPO_SICA );
			if("".equals(tipoAccordo))
				tipoAccordo = null;

			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(apcCore);
			SoggettiCore soggettiCore = new SoggettiCore(apcCore);

			// Preparo il menu
			apcHelper.makeMenu();

			// Prendo il nome
			AccordoServizioParteComune as = apcCore.getAccordoServizioFull(idAccordoLong);
			String labelASTitle = apcHelper.getLabelIdAccordo(as); 
			ServiceBinding serviceBinding = org.openspcoop2.protocol.basic.Utilities.convert(as.getServiceBinding());


			/**String profiloReferente = soggettiCore.getSoggettoRegistro(new IDSoggetto(as.getSoggettoReferente().getTipo(),as.getSoggettoReferente().getNome())).getVersioneProtocollo();*/
			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(as.getSoggettoReferente().getTipo());
			List<String> tipiServiziCompatibili = apsCore.getTipiServiziGestitiProtocollo(protocollo,serviceBinding);
			List<String> tipiSoggettiCompatibili = soggettiCore.getTipiSoggettiGestitiProtocollo(protocollo);


			//calcolo la lista dei servizi da visualizzare
			String[] serviziList = null;
			String[] serviziListLabel = null;
			List<String[]> lst = apcCore.getAccordiServizioCompostoLabels(as, idAccordoLong, userLogin, tipiServiziCompatibili, tipiSoggettiCompatibili, apcHelper);

			serviziList = lst.get(0);
			serviziListLabel = lst.get(1);

			// Lista port-type associati all'accordo di servizio
			/**			AccordoServizioParteSpecifica ssComponente = null;
			//			if (idServizioComponente != null && !"".equals(idServizioComponente)) {
			//				ssComponente = core.getAccordoServizioParteSpecifica(Long.parseLong(idServizioComponente));
			//			} else {
			//				if (serviziList != null)
			//					ssComponente = core.getAccordoServizioParteSpecifica(Long.parseLong(serviziList[0]));
			//			}
			//			if(asComponente!=null){
			//				List<PortType> portTypes = core.accordiPorttypeList(asComponente.getId().intValue(), new Search(true));
			//				if (portTypes.size() > 0) {
			//					ptList = new String[portTypes.size() + 1];
			//					ptList[0] = "-";
			//					int i = 1;
			//					for (Iterator<PortType> iterator = portTypes.iterator(); iterator.hasNext();) {
			//						PortType portType2 = (PortType) iterator.next();
			//						ptList[i] = portType2.getNome();
			//						i++;
			//					}
			//				}
			//			}*/

			// Se idhid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (apcHelper.isEditModeInProgress()) {


				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<>();
				lstParam.add(new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo), 
						AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST,
						AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo)
						));
				lstParam.add(new Parameter(AccordiServizioParteComuneCostanti.LABEL_COMPONENTI + " di " + labelASTitle,
						AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_COMPONENTI_LIST,
						new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, "" + idAccordo),
						AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo)
						));
				lstParam.add(ServletUtils.getParameterAggiungi()); 
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = 	apcHelper.addAccordiServizioComponentiToDati(TipoOperazione.ADD,idAccordo,
						idServizioComponente, tipoAccordo, serviziList,
						serviziListLabel, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_COMPONENTI,
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = apcHelper.accordiComponentiCheckData(TipoOperazione.ADD,idServizioComponente);
			if (!isOk) {
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<>();
				lstParam.add(new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo), 
						AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST,
						AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo)
						));
				lstParam.add(new Parameter(AccordiServizioParteComuneCostanti.LABEL_COMPONENTI + " di " + labelASTitle,
						AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_COMPONENTI_LIST,
						new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, "" + idAccordo),
						AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo)
						));
				lstParam.add(ServletUtils.getParameterAggiungi()); 
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati = apcHelper.addAccordiServizioComponentiToDati(TipoOperazione.ADD,idAccordo,
						idServizioComponente, tipoAccordo, serviziList,
						serviziListLabel, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_COMPONENTI, 
						ForwardParams.ADD());
			}

			//inserimento accordo componente
			AccordoServizioParteSpecifica aspsComponente = apsCore.getAccordoServizioParteSpecifica(Long.parseLong(idServizioComponente));
			
			AccordoServizioParteComuneServizioComposto assc = as.getServizioComposto();
			if(assc==null) assc = new AccordoServizioParteComuneServizioComposto();

			AccordoServizioParteComuneServizioCompostoServizioComponente asscc = 
					new AccordoServizioParteComuneServizioCompostoServizioComponente();
			asscc.setIdServizioComponente(aspsComponente.getId());
			asscc.setNome(aspsComponente.getNome());
			asscc.setTipo(aspsComponente.getTipo());
			asscc.setVersione(aspsComponente.getVersione());
			asscc.setTipoSoggetto(aspsComponente.getTipoSoggettoErogatore());
			asscc.setNomeSoggetto(aspsComponente.getNomeSoggettoErogatore());
			//aggiungo componente
			assc.addServizioComponente(asscc);
			//aggiungo servizio componente ad accordo
			as.setServizioComposto(assc);


			// effettuo le operazioni
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), as);

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			List<AccordoServizioParteComuneServizioCompostoServizioComponente> lista = apcCore.accordiComponentiList(idAccordoLong, ricerca);

			apcHelper.prepareAccordiComponentiList(as, ricerca, lista, tipoAccordo);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_COMPONENTI, ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_COMPONENTI, ForwardParams.ADD());
		}  


	}




}
