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
package org.openspcoop2.web.ctrlstat.servlet.pd;

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
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.TrasformazioneRegolaParametro;
import org.openspcoop2.core.config.TrasformazioneRegolaRichiesta;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.core.config.constants.TrasformazioneRegolaParametroTipoAzione;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * PorteDelegateTrasformazioniRichiestaHeaderAdd
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteDelegateTrasformazioniRichiestaHeaderAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		String userLogin = ServletUtils.getUserLoginFromSession(session);	
		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session);
		if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;

		try {
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			String id = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			
			String idAsps = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			
			String idFruizione = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null)
				idFruizione = "";			

			String idTrasformazioneS = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_TRASFORMAZIONE);
			long idTrasformazione = Long.parseLong(idTrasformazioneS);
			
			String tipo = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA_HEADER_TIPO);
			if(tipo == null)
				tipo = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_ADD;
			
			String nome = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA_HEADER_NOME);
			if(nome == null)
				nome = "";
			
			String valore = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA_HEADER_VALORE);
			if(valore == null)
				valore = "";
			
			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFruizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);
			
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteDelegateCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteDelegateCore);
			
			// Preparo il menu
			porteDelegateHelper.makeMenu();

			// Prendo nome della porta applicativa
			PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			String nomePorta = portaDelegata.getNome();
			
			Trasformazioni trasformazioni = portaDelegata.getTrasformazioni();
			TrasformazioneRegola regola = null;
			for (int j = 0; j < trasformazioni.sizeRegolaList(); j++) {
				TrasformazioneRegola regolaTmp = trasformazioni.getRegola(j);
				if (regolaTmp.getId().longValue() == idTrasformazione) {
					regola = regolaTmp;
					break;
				}
			}
			
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
			AccordoServizioParteComuneSintetico apc = apcCore.getAccordoServizioSintetico(asps.getIdAccordo()); 
			
			String nomeTrasformazione = regola.getNome();
			Parameter pIdTrasformazione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_TRASFORMAZIONE, idTrasformazione+"");

			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI,
						portaDelegata);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_DI+nomePorta;
			}

			lstParam.add(new Parameter(labelPerPorta, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_TRASFORMAZIONI_LIST, pId, pIdSoggetto, pIdAsps, pIdFruizione));
			
			lstParam.add(new Parameter(nomeTrasformazione, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_TRASFORMAZIONI_CHANGE, 
					pId, pIdSoggetto, pIdAsps, pIdFruizione, pIdTrasformazione));
			
			List<Parameter> parametriInvocazioneServletTrasformazioniRichiesta = new ArrayList<Parameter>();
			parametriInvocazioneServletTrasformazioniRichiesta.add(pId);
			parametriInvocazioneServletTrasformazioniRichiesta.add(pIdSoggetto);
			parametriInvocazioneServletTrasformazioniRichiesta.add(pIdAsps);
			parametriInvocazioneServletTrasformazioniRichiesta.add(pIdFruizione);
			parametriInvocazioneServletTrasformazioniRichiesta.add(pIdTrasformazione);
			
			lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA,
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA,parametriInvocazioneServletTrasformazioniRichiesta));
						
			List<Parameter> parametriInvocazioneServletTrasformazioniRichiestaHeaders = new ArrayList<Parameter>();
			parametriInvocazioneServletTrasformazioniRichiestaHeaders.add(pId);
			parametriInvocazioneServletTrasformazioniRichiestaHeaders.add(pIdSoggetto);
			parametriInvocazioneServletTrasformazioniRichiestaHeaders.add(pIdAsps);
			parametriInvocazioneServletTrasformazioniRichiestaHeaders.add(pIdFruizione);
			parametriInvocazioneServletTrasformazioniRichiestaHeaders.add(pIdTrasformazione);
			
			lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA_HEADERS,
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA_HEADER_LIST,parametriInvocazioneServletTrasformazioniRichiestaHeaders ));
			
			lstParam.add(ServletUtils.getParameterAggiungi());

			ServletUtils.setPageDataTitle(pd, lstParam);

			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (porteDelegateHelper.isEditModeInProgress()) {
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteDelegateHelper.addTrasformazioneRichiestaHeaderToDati(TipoOperazione.ADD, dati, idTrasformazioneS, null, nome, tipo, valore, apc.getServiceBinding());
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.ADD, id, idsogg, null, idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA_HEADER,	ForwardParams.ADD());
			}
			
			boolean isOk = porteDelegateHelper.trasformazioniRichiestaHeaderCheckData(TipoOperazione.ADD);
			
			if (isOk) {
				boolean giaRegistrato = porteDelegateCore.existsTrasformazioneRichiestaHeader(Long.parseLong(id), idTrasformazione, nome, tipo, CostantiControlStation.VALUE_TRASFORMAZIONI_CHECK_UNIQUE_NOME_TIPO_HEADER_URL);

				if (giaRegistrato) {
					pd.setMessage(CostantiControlStation.MESSAGGIO_TRASFORMAZIONI_CHECK_UNIQUE_NOME_TIPO_HEADER);
					isOk = false;
				}
			} 
			
			if (!isOk) {

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteDelegateHelper.addTrasformazioneRichiestaHeaderToDati(TipoOperazione.ADD, dati, idTrasformazioneS, null, nome, tipo, valore, apc.getServiceBinding());
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.ADD, id, idsogg, null, idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA_HEADER,	ForwardParams.ADD());
			}
			
			// salvataggio nuova regola
			for (int j = 0; j < trasformazioni.sizeRegolaList(); j++) {
				TrasformazioneRegola regolaTmp = trasformazioni.getRegola(j);
				if (regolaTmp.getId().longValue() == idTrasformazione) {
					regola = regolaTmp;
					break;
				}
			}
			
			if(regola.getRichiesta() == null)
				regola.setRichiesta(new TrasformazioneRegolaRichiesta());
			
			TrasformazioneRegolaParametro parametro = new TrasformazioneRegolaParametro();
			parametro.setNome(nome);
			parametro.setValore(valore);
			parametro.setConversioneTipo(TrasformazioneRegolaParametroTipoAzione.toEnumConstant(tipo));
					
			regola.getRichiesta().addHeader(parametro);
			
			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), portaDelegata);
			
			// ricaricare id trasformazione
			portaDelegata = porteDelegateCore.getPortaDelegata(Long.parseLong(id));

			TrasformazioneRegola trasformazioneAggiornata = porteDelegateCore.getTrasformazione(Long.parseLong(id), regola.getNome());

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			int idLista = Liste.PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA_HEADER; 
			
			ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
			
			List<TrasformazioneRegolaParametro> lista = porteDelegateCore.porteDelegateTrasformazioniRichiestaHeaderList(Long.parseLong(id), trasformazioneAggiornata.getId(), ricerca);
			
			porteDelegateHelper.preparePorteDelegateTrasformazioniRichiestaHeaderList(nomePorta, trasformazioneAggiornata.getId(), ricerca, lista); 
						
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA_HEADER, ForwardParams.ADD());


		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA_HEADER, ForwardParams.ADD());
		} 
	}
}
