/*
 * OpenSPCoop - Customizable API Gateway 
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


package org.openspcoop2.web.ctrlstat.servlet.soggetti;

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
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddTipologia;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.InterfaceType;

/**
 * soggettiAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class SoggettiAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);


		try {
			SoggettiHelper soggettiHelper = new SoggettiHelper(request, pd, session);

			String nomeprov = request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME);
			String tipoprov = request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO);
			String portadom = request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_CODICE_PORTA);
			String descr = request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_DESCRIZIONE);
			String versioneProtocollo = request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_VERSIONE_PROTOCOLLO);
			String pdd = request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_PDD);
			String is_router = request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_IS_ROUTER);
			boolean privato = ServletUtils.isCheckBoxEnabled(request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_IS_PRIVATO));
			String codiceIpa = request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_CODICE_IPA);

			String protocollo = request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_PROTOCOLLO);

			boolean isRouter = ServletUtils.isCheckBoxEnabled(is_router);

			String userLogin = ServletUtils.getUserLoginFromSession(session);
			Boolean singlePdD = (Boolean) session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_SINGLE_PDD);

			// Preparo il menu
			soggettiHelper.makeMenu();

			SoggettiCore soggettiCore = new SoggettiCore();
			PddCore pddCore = new PddCore(soggettiCore);

			// Prendo la lista di pdd e la metto in un array
			String[] pddList = null;
			List<String> tipiSoggetti = null;
			int totPdd = 1;
			String nomePddGestioneLocale = null;
			List<String> versioniProtocollo = null;


			if(soggettiCore.isRegistroServiziLocale()){
				List<PdDControlStation> lista = new ArrayList<PdDControlStation>();

				// aggiungo un elemento di comodo
				PdDControlStation tmp = new PdDControlStation();
				tmp.setNome("-");
				lista.add(tmp);

				// aggiungo gli altri elementi
				if(soggettiCore.isVisioneOggettiGlobale(userLogin)){
					lista.addAll(pddCore.pddList(null, new Search(true)));
				}else{
					lista.addAll(pddCore.pddList(userLogin, new Search(true)));
				}

				totPdd = lista.size();

				pddList = new String[lista.size()];

				int i = 0;
				for (PdDControlStation pddTmp : lista) {
					pddList[i] = pddTmp.getNome();
					i++;
					if( singlePdD && (nomePddGestioneLocale==null) && (PddTipologia.OPERATIVO.toString().equals(pddTmp.getTipo())) ){
						nomePddGestioneLocale = pddTmp.getNome();
					}
				}
			}

			// Tipi protocollo supportati
			List<String> listaTipiProtocollo = soggettiCore.getProtocolli();
			// primo accesso inizializzo con il protocollo di default
			if(protocollo == null){
				protocollo = soggettiCore.getProtocolloDefault();
			}

			//Carico la lista dei tipi di soggetti gestiti dal protocollo
			 tipiSoggetti = soggettiCore.getTipiSoggettiGestitiProtocollo(protocollo);

			// lista tipi
//			tipiSoggetti = soggettiCore.getTipiSoggettiGestiti(); // all tipi soggetti gestiti
			if(tipoprov==null){
				tipoprov = soggettiCore.getTipoSoggettoDefault();
			}
			
			String postBackElementName = ServletUtils.getPostBackElementName(request);

			// Controllo se ho modificato il protocollo, ricalcolo il default della versione del protocollo
			if(postBackElementName != null ){
				if(postBackElementName.equalsIgnoreCase(SoggettiCostanti.PARAMETRO_SOGGETTO_PROTOCOLLO)){
					versioneProtocollo = null;
				}  
			}
			
			
//			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoprov);
			if(versioneProtocollo == null){
				versioneProtocollo = soggettiCore.getVersioneDefaultProtocollo(protocollo);
			}
			
			if(InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(session).getInterfaceType())){
				versioniProtocollo = soggettiCore.getVersioniProtocollo(protocollo);
			}else {
				versioniProtocollo = new ArrayList<String>();
//				versioneProtocollo = soggettiCore.getVersioneDefaultProtocollo(protocollo);
				versioniProtocollo.add(versioneProtocollo);
			}
			boolean isSupportatoCodiceIPA = soggettiCore.isSupportatoCodiceIPA(protocollo); 

			// Se nomehid = null, devo visualizzare la pagina per
			// l'inserimento dati
			if(ServletUtils.isEditModeInProgress(request)){

				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletAdd(pd, SoggettiCostanti.LABEL_SOGGETTI);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				if(nomeprov==null){
					nomeprov = "";
				}
				if(portadom==null){
					portadom = "";
				}
				if(descr==null){
					descr = "";
				}

				dati = soggettiHelper.addSoggettiToDati(TipoOperazione.ADD,dati, nomeprov, tipoprov, portadom, descr, 
						isRouter, tipiSoggetti, versioneProtocollo, privato,codiceIpa,versioniProtocollo,isSupportatoCodiceIPA,
						pddList,nomePddGestioneLocale, listaTipiProtocollo, protocollo );

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = soggettiHelper.soggettiCheckData(TipoOperazione.ADD);
			if (isOk) {

				if(soggettiCore.isRegistroServiziLocale()){
					if (!singlePdD) {
						isOk = false;
						// Controllo che pdd appartenga alla lista di pdd
						// esistenti
						for (int i = 0; i < totPdd; i++) {
							String tmpPdd = pddList[i];
							if (tmpPdd.equals(pdd) && !pdd.equals("-")) {
								isOk = true;
							}
						}
						if (!isOk) {
							pd.setMessage("La Porta di Dominio dev'essere scelta tra quelle definite nel pannello Porte di Dominio");
						}
					}
				}
			}
			if (!isOk) {

				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletAdd(pd, SoggettiCostanti.LABEL_SOGGETTI);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = soggettiHelper.addSoggettiToDati(TipoOperazione.ADD,dati, nomeprov, tipoprov, portadom, descr, 
						isRouter, tipiSoggetti, versioneProtocollo, privato,codiceIpa,versioniProtocollo,isSupportatoCodiceIPA,
						pddList,nomePddGestioneLocale, listaTipiProtocollo, protocollo);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.ADD());
			}

			// Inserisco il soggetto nel db

			if (codiceIpa==null || codiceIpa.equals("")) {
				codiceIpa = soggettiCore.getCodiceIPADefault(protocollo, new IDSoggetto(tipoprov,nomeprov), false);
			}

			if (portadom==null || portadom.equals("")) {
				portadom=soggettiCore.getIdentificativoPortaDefault(protocollo, new IDSoggetto(tipoprov,nomeprov));
			}

			// utilizzo il soggetto del registro che e' un
			// sovrainsieme di quello del config
			Soggetto soggettoRegistro = null;
			if(soggettiCore.isRegistroServiziLocale()){
				soggettoRegistro = new Soggetto();
			}
			org.openspcoop2.core.config.Soggetto soggettoConfig = new org.openspcoop2.core.config.Soggetto();

			// imposto soggettoRegistro
			if(soggettiCore.isRegistroServiziLocale()){
				soggettoRegistro.setNome(nomeprov);
				soggettoRegistro.setTipo(tipoprov);
				soggettoRegistro.setDescrizione(descr);
				soggettoRegistro.setVersioneProtocollo(versioneProtocollo);
				soggettoRegistro.setIdentificativoPorta(portadom);
				soggettoRegistro.setCodiceIpa(codiceIpa);
				if(soggettiCore.isSinglePdD()){
					if (pdd.equals("-"))
						soggettoRegistro.setPortaDominio(null);
					else
						soggettoRegistro.setPortaDominio(pdd);
				}else{
					soggettoRegistro.setPortaDominio(pdd);
				}
				soggettoRegistro.setSuperUser(userLogin);
				soggettoRegistro.setPrivato(privato);
			}

			Connettore connettore = null;
			if(soggettiCore.isRegistroServiziLocale()){
				connettore = new Connettore();
				connettore.setTipo(CostantiDB.CONNETTORE_TIPO_DISABILITATO);
			}

			if ( soggettiCore.isRegistroServiziLocale() && !pdd.equals("-")) {

				PdDControlStation aPdD = pddCore.getPdDControlStation(pdd);
				int porta = aPdD.getPorta() <= 0 ? 80 : aPdD.getPorta();

				// nel caso in cui e' stata selezionato un nal
				// e la PdD e' di tipo operativo oppure non-operativo
				// allora setto come default il tipo HTTP
				// altrimenti il connettore e' disabilitato
				String tipoPdD = aPdD.getTipo();
				if ((tipoPdD != null) && (!singlePdD) && (tipoPdD.equals(PddTipologia.OPERATIVO.toString()) || tipoPdD.equals(PddTipologia.NONOPERATIVO.toString()))) {
					String ipPdd = aPdD.getIp();

					String url = aPdD.getProtocollo() + "://" + ipPdd + ":" + porta + "/" + soggettiCore.getSuffissoConnettoreAutomatico();
					url = url.replace(CostantiControlStation.PLACEHOLDER_SOGGETTO_ENDPOINT_CREAZIONE_AUTOMATICA, 
							soggettiCore.getWebContextProtocolAssociatoTipoSoggetto(tipoprov));
					connettore.setTipo(CostantiDB.CONNETTORE_TIPO_HTTP);

					Property property = new Property();
					property.setNome(CostantiDB.CONNETTORE_HTTP_LOCATION);
					property.setValore(url);
					connettore.addProperty(property);
				}

			}

			if(soggettiCore.isRegistroServiziLocale()){
				soggettoRegistro.setConnettore(connettore);
			}

			// imposto soggettoConfig
			soggettoConfig.setNome(nomeprov);
			soggettoConfig.setTipo(tipoprov);
			soggettoConfig.setDescrizione(descr);
			soggettoConfig.setIdentificativoPorta(portadom);
			soggettoConfig.setRouter(isRouter);
			soggettoConfig.setSuperUser(userLogin);

			SoggettoCtrlStat sog = new SoggettoCtrlStat(soggettoRegistro, soggettoConfig);
			// eseguo le operazioni
			soggettiCore.performCreateOperation(userLogin, soggettiHelper.smista(), sog);

			// recupero la lista dei soggetti
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			// la lista dei soggetti del registro e' un sovrainsieme
			// di quella di config
			// cioe' ha piu informazioni, ma lo stesso numero di
			// soggetti.
			// quindi posso utilizzare solo quella
			if(soggettiCore.isRegistroServiziLocale()){
				List<Soggetto> listaSoggettiRegistro = null;
				if(soggettiCore.isVisioneOggettiGlobale(userLogin)){
					listaSoggettiRegistro = soggettiCore.soggettiRegistroList(null, ricerca);
				}else{
					listaSoggettiRegistro = soggettiCore.soggettiRegistroList(userLogin, ricerca);
				}

				soggettiHelper.prepareSoggettiList(listaSoggettiRegistro, ricerca);
			}
			else{
				List<org.openspcoop2.core.config.Soggetto> listaSoggettiConfig = null;
				if(soggettiCore.isVisioneOggettiGlobale(userLogin)){
					listaSoggettiConfig = soggettiCore.soggettiList(null, ricerca);
				}else{
					listaSoggettiConfig = soggettiCore.soggettiList(userLogin, ricerca);
				}

				soggettiHelper.prepareSoggettiConfigList(listaSoggettiConfig, ricerca);
			}


			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.ADD());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.ADD());
		}
	}

}
