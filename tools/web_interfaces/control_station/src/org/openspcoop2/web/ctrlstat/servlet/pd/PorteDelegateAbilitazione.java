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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataServizio;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaHelper;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * porteDelegateChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteDelegateAbilitazione extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session);
		if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;

		try {
			
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			
			String changeAbilitato = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ABILITA);
//			String idPorta = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			String nomePorta = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA);
			String idsogg = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			String idAsps = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			
			String idFruizione = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null)
				idFruizione = "";
			
			String actionConferma = porteDelegateHelper.getParameter(Costanti.PARAMETRO_ACTION_CONFIRM);
			
			// check su oldNomePD
			PageData pdOld =  ServletUtils.getPageDataFromSession(session);
			String oldNomePD = pdOld.getHidden(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_OLD_NOME_PD);
			oldNomePD = (((oldNomePD != null) && !oldNomePD.equals("")) ? oldNomePD : nomePorta);
			// Preparo il menu
			porteDelegateHelper.makeMenu();

			// Prendo nome e tipo del soggetto
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteDelegateCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteDelegateCore);
			AccordiServizioParteSpecificaCore aspsCore = new AccordiServizioParteSpecificaCore(porteDelegateCore);
			
			IDSoggetto idSoggettoFruitore = null;
			if(porteDelegateCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(soggInt);
				idSoggettoFruitore = new IDSoggetto(soggetto.getTipo(), soggetto.getNome());
			}else{
				org.openspcoop2.core.config.Soggetto soggetto = soggettiCore.getSoggetto(soggInt);
				idSoggettoFruitore = new IDSoggetto(soggetto.getTipo(), soggetto.getNome());
			}

			// Prendo la porta delegata
			IDPortaDelegata idpd = new IDPortaDelegata();
			idpd.setNome(oldNomePD);
			PortaDelegata oldPD = porteDelegateCore.getPortaDelegata(idpd);
			
			Long idAspsLong = -1L;
			if(idAsps.equals("")) {
				PortaDelegataServizio servizio2 = oldPD.getServizio();
				idAspsLong = servizio2.getId();
			} else {
				idAspsLong = Long.parseLong(idAsps);
			}
			
			AccordoServizioParteSpecifica asps = aspsCore.getAccordoServizioParteSpecifica(idAspsLong);
			AccordoServizioParteComune aspc = apcCore.getAccordoServizio(IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
			ServiceBinding serviceBinding = apcCore.toMessageServiceBinding(aspc.getServiceBinding());
			
			// in progress segnalo l'azione che si sta effettuando
			if(actionConferma == null) {
				String messaggio = porteDelegateHelper.getMessaggioConfermaModificaRegolaMappingFruizionePortaDelegata(oldPD,serviceBinding,ServletUtils.isCheckBoxEnabled(changeAbilitato), true, true);
				
				pd.setMessage(messaggio, MessageType.CONFIRM);
				
				String[][] bottoni = { 
						{ Costanti.LABEL_MONITOR_BUTTON_ANNULLA, 
							Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_PREFIX +
							Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_SUFFIX
							
						},
						{ Costanti.LABEL_MONITOR_BUTTON_CONFERMA,
							Costanti.LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_PREFIX +
							Costanti.LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_SUFFIX }};

				pd.setBottoni(bottoni);
				
			} 

			// se ho confermato effettuo la modifica altrimenti torno direttamente alla lista
			if(actionConferma != null && actionConferma.equals(Costanti.PARAMETRO_ACTION_CONFIRM_VALUE_OK)) {
				long idPA = oldPD.getId();
				PortaDelegata portaDelegata = (PortaDelegata) oldPD.clone();// new
				portaDelegata.setId(idPA);
				portaDelegata.setNome(nomePorta);
				
				// cambio solo la modalita'
	            if(ServletUtils.isCheckBoxEnabled(changeAbilitato)) {
	            	portaDelegata.setStato(StatoFunzionalita.ABILITATO);
	            }
	            else{
	                portaDelegata.setStato(StatoFunzionalita.DISABILITATO);
	            }
	            String userLogin = ServletUtils.getUserLoginFromSession(session);
				porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), portaDelegata);
			}
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);


			int idLista = -1;
			List<PortaDelegata> lista = null;
			
			switch (parentPD) {
			case PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE:
				idLista = Liste.CONFIGURAZIONE_FRUIZIONE;
				ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
				IDServizio idServizio2 = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps); 
				
				List<MappingFruizionePortaDelegata> listaMapping = aspsCore.serviziFruitoriMappingList((long) Integer.parseInt(idFruizione), idSoggettoFruitore, idServizio2, ricerca);
				AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
				apsHelper.serviziFruitoriMappingList(listaMapping, idAsps, idsogg, idFruizione, ricerca); 
				
				break;
			case PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_SOGGETTO:
				idLista = Liste.PORTE_DELEGATE_BY_SOGGETTO;
				ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
				lista = porteDelegateCore.porteDelegateList(soggInt, ricerca);
				porteDelegateHelper.preparePorteDelegateList(ricerca, lista,idLista);
				break;
			case PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE:
			default:
				idLista = Liste.PORTE_DELEGATE;
				ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
				lista = porteDelegateCore.porteDelegateList(null, ricerca);
				porteDelegateHelper.preparePorteDelegateList(ricerca, lista,idLista);
				break;
			}

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_ABILITAZIONE,
					ForwardParams.OTHER(""));

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_ABILITAZIONE, 
					ForwardParams.OTHER(""));
		}  
	}
}
