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
package org.openspcoop2.web.ctrlstat.servlet.connettori;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.web.ctrlstat.core.Connettori;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.costanti.TipologiaConnettori;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettoreConverter;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneUtilities;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.InterfaceType;
import org.openspcoop2.web.lib.users.dao.User;

/**
 * ConnettoriHelper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoriHelper extends ConsoleHelper {

	public ConnettoriHelper(HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(request, pd,  session);
	}

	public String getAutenticazioneHttp(String autenticazioneHttp,String endpointtype, String user){
		if((endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString()))){
			if ( autenticazioneHttp==null && user!=null && !"".equals(user) ){
				autenticazioneHttp =  Costanti.CHECK_BOX_ENABLED;
			}  
		}
		else{
			autenticazioneHttp = null;
		}
		return autenticazioneHttp;
	}
	
	@SuppressWarnings("deprecation")
	private static String getParametroAccordoServizio(String tipo,String appendChar){
		return AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipo, appendChar);
	}
	
	public void setTitleProprietaConnettoriCustom(PageData pd,TipoOperazione tipoOperazione,
			String servlet, String id, String nomeprov, String tipoprov,String nomeservizio,String tiposervizio,
			String myId, String correlato, String idSoggErogatore, String nomeservizioApplicativo,String idsil,String tipoAccordo,
			String provider) throws DriverConfigurazioneNotFound, DriverConfigurazioneException, DriverRegistroServiziNotFound, DriverRegistroServiziException, DriverControlStationException{
		
		if (AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_EROGATORI_FRUITORI_CHANGE.equals(servlet)) {
			int idInt = Integer.parseInt(id);
			AccordoServizioParteComune as = this.apcCore.getAccordoServizio(idInt);
			String uriAccordo = this.idAccordoFactory.getUriFromAccordo(as);
			int myIdInt = Integer.parseInt(myId);
			Fruitore myAccErFru = this.apsCore.getErogatoreFruitore(myIdInt);
			String nomefru = myAccErFru.getNome();
			String tipofru = myAccErFru.getTipo();

			ServletUtils.setPageDataTitle(pd, 
					// t1
					new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo),null), 
					// t2
					new Parameter(
						Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
						AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+getParametroAccordoServizio(tipoAccordo, "?")), 
					// t3
					new Parameter(
						AccordiServizioParteComuneCostanti.LABEL_ACCORDI_EROGATORI_DI+ uriAccordo, 
						AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_EROGATORI_LIST+
						"?"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID+"=" + id+getParametroAccordoServizio(tipoAccordo, "&")), 
					// t4
					new Parameter(
						"Fruitori del servizio " + tipoprov + "/" + nomeprov + " - " + tiposervizio + "/" + nomeservizio,
						AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_EROGATORI_FRUITORI_LIST+
						"?"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID+"=" + id + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SOGGETTO+"=" + nomeprov + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SOGGETTO+"=" + tipoprov + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO+"=" + nomeservizio + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SERVIZIO+"=" + tiposervizio + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_CORRELATO+"=" + correlato+
						getParametroAccordoServizio(tipoAccordo, "&")),
					// t5
					new Parameter(
						"Connettore del fruitore "+tipofru + "/" + nomefru,
						AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_EROGATORI_FRUITORI_CHANGE+
						"?"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID+"=" + id + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SOGGETTO+"=" + nomeprov + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SOGGETTO+"=" + tipoprov + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO+"=" + nomeservizio + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SERVIZIO+"=" + tiposervizio + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_MY_ID+"=" + myId + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_CORRELATO+"=" + 
						correlato+getParametroAccordoServizio(tipoAccordo, "&"))
					);

		}
		
		if (AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE.equals(servlet)) {
			int idServizioInt = Integer.parseInt(id);
			AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(idServizioInt);
			Servizio servizio = asps.getServizio();

			ServletUtils.setPageDataTitle(pd, 
					// t1
					new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null),
					// t2
					new Parameter(
						Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST), 
					// t3
					new Parameter(
						"Connettore del servizio "+tiposervizio + "/" + nomeservizio+" erogato dal soggetto "+servizio.getTipoSoggettoErogatore()+"/"+servizio.getNomeSoggettoErogatore(), 
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE+
						"?"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID+"=" + id+
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO+"=" + nomeservizio + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SERVIZIO+"=" + tiposervizio
						)
					);

		}
		
		if (AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE.equals(servlet)) {
			int idServizioInt = Integer.parseInt(id);
			AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(idServizioInt);
			Servizio servizio = asps.getServizio();
			String tipoSoggettoErogatore = servizio.getTipoSoggettoErogatore();
			String nomesoggettoErogatore = servizio.getNomeSoggettoErogatore();
			int idServizioFruitoreInt = Integer.parseInt(myId);
			Fruitore servFru = this.apsCore.getServizioFruitore(idServizioFruitoreInt);
			String nomefru = servFru.getNome();
			String tipofru = servFru.getTipo();

			ServletUtils.setPageDataTitle(pd, 
					// t1
					new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS,null), 
					// t2
					new Parameter(
						Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST), 
					// t3
					new Parameter(
						"Fruitori del servizio " + servizio.getTipo() + "/" + servizio.getNome() + " erogato da " + tipoSoggettoErogatore + "/" + nomesoggettoErogatore,
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST+
						"?"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID+"=" + id + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SOGGETTO_EROGATORE+"=" + idSoggErogatore),
					// t4
					new Parameter(
						"Connettore del fruitore "+tipofru + "/" + nomefru,
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE+
						"?"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID+"=" + id + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_MY_ID+"=" + myId + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SOGGETTO_EROGATORE+"=" + idSoggErogatore
						)
					);

		}
		
		if (ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT.equals(servlet)) {
			int idInt = Integer.parseInt(idsil);
			ServizioApplicativo sa = this.saCore.getServizioApplicativo(idInt);

			ServletUtils.setPageDataTitle(pd, 
					// t1
					new Parameter(ServiziApplicativiCostanti.LABEL_SERVIZIO_APPLICATIVO,null), 
					// t2
					new Parameter(
							Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
							ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST), 
					// t3
					new Parameter(
						"Connettore del servizio applicativo (InvocazioneServizio) " + nomeservizioApplicativo+" del soggetto "+sa.getTipoSoggettoProprietario()+"/"+sa.getNomeSoggettoProprietario(),
						ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT+
						"?"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO_APPLICATIVO+"=" + nomeservizioApplicativo + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO+"=" + idsil +
						"&"+ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER+"=" + provider
						)
					);
		}
		
		if (ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA.equals(servlet)) {
			int idInt = Integer.parseInt(idsil);
			ServizioApplicativo sa = this.saCore.getServizioApplicativo(idInt);

			ServletUtils.setPageDataTitle(pd, 
					// t1
					new Parameter(ServiziApplicativiCostanti.LABEL_SERVIZIO_APPLICATIVO, null),
					// t2
					new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
								 ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST), 
					// t3
					new Parameter(			 
						"Connettore del servizio applicativo (RispostaAsincrona) " + nomeservizioApplicativo+" del soggetto "+sa.getTipoSoggettoProprietario()+"/"+sa.getNomeSoggettoProprietario(),
						ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA+
						"?"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO_APPLICATIVO+"=" + nomeservizioApplicativo + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO+"=" + idsil +
						"&"+ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER+"=" + provider
					)
					);

		}
		
		if (SoggettiCostanti.SERVLET_NAME_SOGGETTI_ENDPOINT.equals(servlet)) {
			int idInt = Integer.parseInt(id);
			SoggettoCtrlStat scs = this.soggettiCore.getSoggettoCtrlStat(idInt);
			String nomeprov1 = scs.getNome();
			String tipoprov1 = scs.getTipo();

			ServletUtils.setPageDataTitle(pd, 
					// t1
					new Parameter(SoggettiCostanti.LABEL_SOGGETTI,null), 
					// t2
					new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
								  SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST), 
					// t3
					new Parameter(
						"Connettore del soggetto " + tipoprov1 + "/" + nomeprov1,
						SoggettiCostanti.SERVLET_NAME_SOGGETTI_ENDPOINT+
						"?"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID+"=" + id + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SOGGETTO+"=" + nomeprov + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SOGGETTO+"=" + tipoprov
						)
					);

		}

		if(!TipoOperazione.LIST.equals(tipoOperazione)){
			ServletUtils.appendPageDataTitle(pd,
					// t1
					new Parameter(
						ConnettoriCostanti.LABEL_CONNETTORE_PROPRIETA,
						ConnettoriCostanti.SERVLET_NAME_CONNETTORI_CUSTOM_PROPERTIES_LIST+
						"?"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET+"=" + servlet + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID+"=" + id + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SOGGETTO+"=" + nomeprov + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SOGGETTO+"=" + tipoprov +
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO+"=" + nomeservizio + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SERVIZIO+"=" + tiposervizio + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_CORRELATO+"=" + correlato +
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_MY_ID+"=" + myId + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SOGGETTO_EROGATORE+"=" + idSoggErogatore +
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO_APPLICATIVO+"=" + nomeservizioApplicativo + 
						"&"+ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO+"=" + idsil
							)
					);
		}
		ServletUtils.appendPageDataTitle(pd,
				new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI,null));
	}

	
	
	
	public void prepareConnettorePropList(List<?> lista, ISearch ricerca,
			int newMyId,String tipoAccordo)
	throws Exception {
		try {
			
			String servlet = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET);
			String id = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID);
			String nomeprov = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SOGGETTO);
			String tipoprov = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SOGGETTO);
			String nomeservizio = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO);
			String tiposervizio = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SERVIZIO);
			String myId = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_MY_ID);
			if (newMyId != 0)
				myId = ""+newMyId;
			String correlato = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_CORRELATO);
			String idSoggErogatore = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SOGGETTO_EROGATORE);
			String nomeservizioApplicativo = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO_APPLICATIVO);
			String idsil = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO);
			String provider = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			
			ServletUtils.addListElementIntoSession(this.session, ConnettoriCostanti.OBJECT_NAME_CONNETTORI_CUSTOM_PROPERTIES,
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET, servlet),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID, id),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SOGGETTO, nomeprov),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SOGGETTO, tipoprov),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO, nomeservizio),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SERVIZIO, tiposervizio),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_MY_ID, myId),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_CORRELATO, correlato),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SOGGETTO_EROGATORE, idSoggErogatore),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO_APPLICATIVO, nomeservizioApplicativo),
					new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO, idsil),
					new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, provider));
			
//			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
//			
			//int idLista = Liste.SERVIZI_SERVIZIO_APPLICATIVO;
			//int limit = ricerca.getPageSize(idLista);
			//int offset = ricerca.getIndexIniziale(idLista);
			//String search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

			//this.pd.setIndex(offset);
			//this.pd.setPageSize(limit);
			this.pd.setNumEntries(lista.size());

			// setto la barra del titolo
			setTitleProprietaConnettoriCustom(this.pd, TipoOperazione.LIST, servlet, id, nomeprov, tipoprov, nomeservizio, tiposervizio, 
					myId, correlato, idSoggErogatore, nomeservizioApplicativo, idsil, tipoAccordo, provider);
			
			ServletUtils.disabledPageDataSearch(this.pd);
			

			// setto le label delle colonne
			String[] labels = { ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_CUSTOM_NOME, ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_CUSTOM_VALORE };
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<?> it = lista.iterator();

				while (it.hasNext()) {
					String nome = "", valore = "";
					if (servlet.equals(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT) ||
							servlet.equals(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA)) {
						org.openspcoop2.core.config.Property cp =
							(org.openspcoop2.core.config.Property) it.next();
						nome = cp.getNome();
						valore = cp.getValore();
					} else {
						Property cp = (Property) it.next();
						nome = cp.getNome();
						valore = cp.getValore();
					}

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setValue(nome);
					de.setIdToRemove(nome);
					e.addElement(de);

					de = new DataElement();
					de.setValue(valore);
					e.addElement(de);

					dati.addElement(e);
				}
			}

			// inserisco i campi hidden
			Hashtable<String, String> hidden = new Hashtable<String, String>();
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET, servlet);
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID, id != null ? id : "");
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SOGGETTO, nomeprov != null ? nomeprov : "");
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SOGGETTO, tipoprov != null ? tipoprov : "");
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO, nomeservizio != null ? nomeservizio : "");
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SERVIZIO, tiposervizio != null ? tiposervizio : "");
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_MY_ID, myId != null ? myId : "");
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_CORRELATO, correlato != null ? correlato : "");
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SOGGETTO_EROGATORE, idSoggErogatore != null ? idSoggErogatore : "");
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO_APPLICATIVO, nomeservizioApplicativo != null ? nomeservizioApplicativo : "");
			hidden.put(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO, idsil != null ? idsil : "");

			this.pd.setHidden(hidden);

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public Vector<DataElement> addEndPointToDati(Vector<DataElement> dati,String connettoreDebug,
			String endpointtype, String autenticazioneHttp, String prefix, String url, String nome, String tipo,
			String user, String password, String initcont, String urlpgk,
			String provurl, String connfact, String sendas, String objectName, TipoOperazione tipoOperazione,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey,
			String tipoconn, String servletChiamante, String elem1, String elem2, String elem3,
			String elem4, String elem5, String elem6, String elem7,
			boolean showSectionTitle,
			Boolean isConnettoreCustomUltimaImmagineSalvata,
			List<ExtendedConnettore> listExtendedConnettore) {
		return addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, prefix, url, nome, tipo, user,
				password, initcont, urlpgk, provurl, connfact, sendas,
				objectName,tipoOperazione, httpsurl, httpstipologia, httpshostverify,
				httpspath, httpstipo, httpspwd, httpsalgoritmo, httpsstato,
				httpskeystore, httpspwdprivatekeytrust, httpspathkey,
				httpstipokey, httpspwdkey, httpspwdprivatekey,
				httpsalgoritmokey, tipoconn, servletChiamante, elem1, elem2, elem3,
				elem4, elem5, elem6, elem7, null, showSectionTitle,
				isConnettoreCustomUltimaImmagineSalvata,listExtendedConnettore);
	}

	// Controlla i dati del connettore custom
	boolean connettorePropCheckData() throws Exception {
		try {
			String servlet = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET);
			String id = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID);
			//String nomeprov = this.request.getParameter("nomeprov");
			//String tipoprov = this.request.getParameter("tipoprov");
			//String nomeservizio = this.request.getParameter("nomeservizio");
			//String tiposervizio = this.request.getParameter("tiposervizio");
			String myId = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_MY_ID);
			//String correlato = this.request.getParameter("correlato");
			//String idSoggErogatore = this.request.getParameter("idSoggErogatore");
			//String nomeservizioApplicativo = this.request.getParameter("nomeservizioApplicativo");
			String idsil = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO);

			String nome = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME);
			String valore = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_VALORE);

			// Campi obbligatori
			if (nome.equals("") || valore.equals("")) {
				String tmpElenco = "";
				if (nome.equals("")) {
					tmpElenco = ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_CUSTOM_NOME;
				}
				if (valore.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_CUSTOM_VALORE;
					} else {
						tmpElenco = tmpElenco + ", "+ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_CUSTOM_VALORE;
					}
				}
				this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if ((nome.indexOf(" ") != -1) || (valore.indexOf(" ") != -1)) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}

			// Controllo che la property non sia gia' stata
			// registrata
			boolean giaRegistratoProprietaNormale = false;
			boolean giaRegistratoProprietaDebug = false;
			if (servlet.equals(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_EROGATORI_FRUITORI_CHANGE)) {
				int myIdInt = Integer.parseInt(myId);
				Fruitore myAccErFru = this.apsCore.getErogatoreFruitore(myIdInt);
				org.openspcoop2.core.registry.Connettore connettore = myAccErFru.getConnettore();
				for (int j = 0; j < connettore.sizePropertyList(); j++) {
					Property tmpProp = connettore.getProperty(j);
					if (tmpProp.getNome().equals(nome)) {
						if(CostantiDB.CONNETTORE_DEBUG.equals(nome)){
							giaRegistratoProprietaDebug = true;
						}else{
							giaRegistratoProprietaNormale = true;
						}
						break;
					}
				}
			}
			if (servlet.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE)) {
				AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(Long.parseLong(id));
				Servizio servizio = asps.getServizio();
				org.openspcoop2.core.registry.Connettore connettore = servizio.getConnettore();
				for (int j = 0; j < connettore.sizePropertyList(); j++) {
					Property tmpProp = connettore.getProperty(j);
					if (tmpProp.getNome().equals(nome)) {
						if(CostantiDB.CONNETTORE_DEBUG.equals(nome)){
							giaRegistratoProprietaDebug = true;
						}else{
							giaRegistratoProprietaNormale = true;
						}
						break;
					}
				}
			}
			if (servlet.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE)) {
				int idServizioFruitoreInt = Integer.parseInt(myId);
				Fruitore servFru = this.apsCore.getServizioFruitore(idServizioFruitoreInt);
				org.openspcoop2.core.registry.Connettore connettore = servFru.getConnettore();
				for (int j = 0; j < connettore.sizePropertyList(); j++) {
					Property tmpProp = connettore.getProperty(j);
					if (tmpProp.getNome().equals(nome)) {
						if(CostantiDB.CONNETTORE_DEBUG.equals(nome)){
							giaRegistratoProprietaDebug = true;
						}else{
							giaRegistratoProprietaNormale = true;
						}
						break;
					}
				}
			}
			if (servlet.equals(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT)) {
				int idSilInt = Integer.parseInt(idsil);
				ServizioApplicativo sa = this.saCore.getServizioApplicativo(idSilInt);
				InvocazioneServizio is = sa.getInvocazioneServizio();
				org.openspcoop2.core.config.Connettore connettore = is.getConnettore();
				for (int j = 0; j < connettore.sizePropertyList(); j++) {
					org.openspcoop2.core.config.Property tmpProp = connettore.getProperty(j);
					if (tmpProp.getNome().equals(nome)) {
						if(CostantiDB.CONNETTORE_DEBUG.equals(nome)){
							giaRegistratoProprietaDebug = true;
						}else{
							giaRegistratoProprietaNormale = true;
						}
						break;
					}
				}
			}
			if (servlet.equals(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA)) {
				int idSilInt = Integer.parseInt(idsil);
				ServizioApplicativo sa = this.saCore.getServizioApplicativo(idSilInt);
				RispostaAsincrona ra = sa.getRispostaAsincrona();
				org.openspcoop2.core.config.Connettore connettore = ra.getConnettore();
				for (int j = 0; j < connettore.sizePropertyList(); j++) {
					org.openspcoop2.core.config.Property tmpProp = connettore.getProperty(j);
					if (tmpProp.getNome().equals(nome)) {
						if(CostantiDB.CONNETTORE_DEBUG.equals(nome)){
							giaRegistratoProprietaDebug = true;
						}else{
							giaRegistratoProprietaNormale = true;
						}
						break;
					}
				}
			}
			if (servlet.equals(SoggettiCostanti.SERVLET_NAME_SOGGETTI_ENDPOINT)) {
				int idInt = Integer.parseInt(id);
				SoggettoCtrlStat scs = this.soggettiCore.getSoggettoCtrlStat(idInt);
				Soggetto ss = scs.getSoggettoReg();
				org.openspcoop2.core.registry.Connettore connettore = ss.getConnettore();
				for (int j = 0; j < connettore.sizePropertyList(); j++) {
					Property tmpProp = connettore.getProperty(j);
					if (tmpProp.getNome().equals(nome)) {
						if(CostantiDB.CONNETTORE_DEBUG.equals(nome)){
							giaRegistratoProprietaDebug = true;
						}else{
							giaRegistratoProprietaNormale = true;
						}
						break;
					}
				}
			}

			if (giaRegistratoProprietaNormale) {
				this.pd.setMessage("La propriet&agrave; '" + nome + "' &egrave; gi&agrave; stata associata al connettore");
				return false;
			}
			if (giaRegistratoProprietaDebug) {
				this.pd.setMessage("La keyword '" + nome + "' non &egrave; associabile come nome ad una propriet&agrave; del connettore");
				return false;
			}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public String readEndPointType(){
		String endpointtype = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
		String endpointtype_check = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_CHECK);
		String endpointtype_ssl = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTPS);
		return this.readEndPointType(endpointtype, endpointtype_check, endpointtype_ssl);
	}
	public String readEndPointType(String endpointtype,String endpointtype_check,String endpointtype_ssl){
				
		TipologiaConnettori tipologiaConnettori = null;
		try {
			tipologiaConnettori = Utilities.getTipologiaConnettori(this.core);
		} catch (Exception e) {
			// default
			tipologiaConnettori = TipologiaConnettori.TIPOLOGIA_CONNETTORI_ALL;
		}
		
		if(endpointtype_check!=null && !"".equals(endpointtype_check)){
			if (TipologiaConnettori.TIPOLOGIA_CONNETTORI_HTTP.equals(tipologiaConnettori)) {
				if(ServletUtils.isCheckBoxEnabled(endpointtype_check)){
					if(ServletUtils.isCheckBoxEnabled(endpointtype_ssl)){
						endpointtype = TipiConnettore.HTTPS.toString();
					}
					else{
						endpointtype = TipiConnettore.HTTP.toString();
					}
				}
				else{
					endpointtype = TipiConnettore.DISABILITATO.toString();
				}
			}
		}
		return endpointtype;
	}
	
	public Vector<DataElement> addCredenzialiToDati(Vector<DataElement> dati, String tipoauth, String utente, String password, String confpw, String subject, 
			String toCall, boolean showLabelCredenzialiAccesso, String endpointtype,boolean connettore,boolean visualizzaTipoAutenticazione,
			String prefix) {

		User user = ServletUtils.getUserFromSession(this.session);
		
		DataElement de = null;

		if(prefix==null){
			prefix = "";
		}

		String[] tipoA = null;
		String[] labelTipoA = null;
		if(visualizzaTipoAutenticazione){
			if (ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT.equals(toCall) || 
					ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA.equals(toCall)) {
				if(TipiConnettore.HTTPS.toString().equals(endpointtype) || TipiConnettore.HTTP.toString().equals(endpointtype) || 
						TipiConnettore.JMS.toString().equals(endpointtype) || TipiConnettore.CUSTOM.toString().equals(endpointtype)){
					tipoA = new String[] { ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_NESSUNA, 
							ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_BASIC };
					if(TipiConnettore.HTTPS.toString().equals(endpointtype) || TipiConnettore.HTTP.toString().equals(endpointtype) ){
						labelTipoA = new String[] { CostantiConfigurazione.DISABILITATO.toString(), 
								ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_BASIC };
					}else{
						labelTipoA = new String[] { CostantiConfigurazione.DISABILITATO.toString(), 
								CostantiConfigurazione.ABILITATO.toString() };
					}
				}else{
					tipoA = new String[] { ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_NESSUNA };
					labelTipoA = new String[] { CostantiConfigurazione.DISABILITATO.toString() };
					tipoauth = ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_NESSUNA;
				}
			} else {
				if (InterfaceType.AVANZATA.equals(user.getInterfaceType())) {
					tipoA = new String[] { ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_NESSUNA,
							ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_BASIC, 
							ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SSL };
					labelTipoA = new String[] { ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_NESSUNA,
							ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_BASIC, 
							ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SSL };
				}
				else{
					tipoA = new String[] { 
							ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_BASIC, 
							ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SSL };
					labelTipoA = new String[] { 
							ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_BASIC, 
							ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SSL };
					if(tipoauth==null){
						tipoauth = ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SSL;
					}
				}
			}
		}

		boolean showSezioneCredenziali = true;
		if(visualizzaTipoAutenticazione==false){
			showSezioneCredenziali = true;
		}
		else if(tipoA.length == 1){
			showSezioneCredenziali = false;
		}

		if(showSezioneCredenziali) {
			if(showLabelCredenzialiAccesso){
				de = new DataElement();
				if(connettore){
					if(TipiConnettore.HTTPS.toString().equals(endpointtype) ||  TipiConnettore.HTTP.toString().equals(endpointtype) ){
						de.setLabel(prefix+ServiziApplicativiCostanti.LABEL_CREDENZIALI_ACCESSO_SERVIZIO_APPLICATIVO_HTTP);
					}
					else{
						de.setLabel(prefix+ServiziApplicativiCostanti.LABEL_CREDENZIALI_ACCESSO_SERVIZIO_APPLICATIVO);
					}
				}else{
					de.setLabel(prefix+ServiziApplicativiCostanti.LABEL_CREDENZIALI_ACCESSO_PORTA);
				}
				de.setType(DataElementType.TITLE);
				dati.addElement(de);
			}

			de = new DataElement();
			if(showLabelCredenzialiAccesso){
				de.setLabel(ServiziApplicativiCostanti.LABEL_TIPO_CREDENZIALE);
			}else{
				de.setLabel(ServiziApplicativiCostanti.LABEL_CREDENZIALE_ACCESSO);
			}
			if(connettore){
				de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_CONNETTORE);
			}
			else{
				de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SA);
			}
			if(visualizzaTipoAutenticazione){
				de.setType(DataElementType.SELECT);
				de.setLabels(labelTipoA);
				de.setValues(tipoA);
				de.setSelected(tipoauth);
				//		de.setOnChange("CambiaAuth('" + toCall + "')");
				de.setPostBack(true);
			}
			else{
				de.setType(DataElementType.HIDDEN);
				de.setValue(tipoauth);
			}
			dati.addElement(de);
			
			if (ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_BASIC.equals(tipoauth)) {
				de = new DataElement();
				de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_USERNAME);
				de.setValue(utente);
				de.setType(DataElementType.TEXT_EDIT);
				if(connettore){
					de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_USERNAME_CONNETTORE);
				}
				else{
					de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_USERNAME_SA);
				}
				de.setSize(this.getSize());
				de.setRequired(true);
				dati.addElement(de);

				de = new DataElement();
				de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_PASSWORD);
				de.setValue(password);
				// de.setType("crypt");
				de.setType(DataElementType.TEXT_EDIT);
				if(connettore){
					de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_PASSWORD_CONNETTORE);
				}
				else{
					de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_PASSWORD_SA);
				}
				de.setSize(this.getSize());
				de.setRequired(true);
				dati.addElement(de);

				de = new DataElement();
				// de.setLabel("Conferma password");
				de.setValue(confpw);
				de.setType(DataElementType.HIDDEN);
				de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_CONFERMA_PASSWORD_CONNETTORE);
				// de.setSize(this.getSize());
				dati.addElement(de);
			}

			if (ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SSL.equals(tipoauth)) {
				de = new DataElement();
				de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_SUBJECT);
				de.setValue(subject);
				de.setType(DataElementType.TEXT_EDIT);
				if(connettore){
					de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_SUBJECT_CONNETTORE);
				}
				else{
					de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_SUBJECT_SA);
				}
				de.setSize(this.getSize());
				de.setRequired(true);
				dati.addElement(de);
			}

		}  
		else{
			de = new DataElement();
			de.setType(DataElementType.HIDDEN);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_CONNETTORE);
			de.setValue(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_NESSUNA);
			dati.addElement(de);
		}
		
		return dati;
	}
	
	public Vector<DataElement> addEndPointToDati(Vector<DataElement> dati,String connettoreDebug,
			String endpointtype, String autenticazioneHttp,String prefix, String url, String nome, String tipo,
			String user, String password, String initcont, String urlpgk,
			String provurl, String connfact, String sendas, String objectName, TipoOperazione tipoOperazione,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey,
			String tipoconn, String servletChiamante, String elem1, String elem2, String elem3,
			String elem4, String elem5, String elem6, String elem7, String stato,
			boolean showSectionTitle,
			Boolean isConnettoreCustomUltimaImmagineSalvata,
			List<ExtendedConnettore> listExtendedConnettore) {


		Boolean confPers = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_CONFIGURAZIONI_PERSONALIZZATE);

		TipologiaConnettori tipologiaConnettori = null;
		try {
			tipologiaConnettori = Utilities.getTipologiaConnettori(this.core);
		} catch (Exception e) {
			// default
			tipologiaConnettori = TipologiaConnettori.TIPOLOGIA_CONNETTORI_ALL;
		}

		// override tipologiaconnettori :
		// se standard allora la tipologia connettori e' sempre http
		// indipendentemente
		// dalla proprieta settata
		if (InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
			tipologiaConnettori = TipologiaConnettori.TIPOLOGIA_CONNETTORI_HTTP;
		}

		if(prefix==null){
			prefix="";
		}
		
		if(showSectionTitle){
			DataElement de = new DataElement();
			de.setLabel(prefix+ConnettoriCostanti.LABEL_CONNETTORE);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}


		/** VISUALIZZAZIONE HTTP ONLY MODE */

		if (TipologiaConnettori.TIPOLOGIA_CONNETTORI_HTTP.equals(tipologiaConnettori)) {
			
			DataElement de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_ABILITATO);

			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_CHECK);
			if(!TipiConnettore.HTTP.toString().equals(endpointtype) &&
					!TipiConnettore.HTTPS.toString().equals(endpointtype) &&
					!TipiConnettore.DISABILITATO.toString().equals(endpointtype)){
				de.setLabel(CostantiControlStation.LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_SHORT_MESSAGE);
				de.setType(DataElementType.TEXT);
				de.setValue(" ");
				
				this.pd.disableEditMode();
				this.pd.setMessage(CostantiControlStation.LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_LONG_MESSAGE);
			}
			else if( (  (AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS.equals(objectName) && TipoOperazione.CHANGE.equals(tipoOperazione))
					|| 
					(AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI.equals(objectName) && TipoOperazione.CHANGE.equals(tipoOperazione))
					||
					(AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_EROGATORI_FRUITORI.equals(objectName))
					)
					&& StatiAccordo.finale.toString().equals(stato) && this.core.isShowGestioneWorkflowStatoDocumenti() ){
				if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())) {
					de.setType(DataElementType.HIDDEN);
					de.setSelected(true);
				}
				else{
					de.setType(DataElementType.TEXT);
					de.setLabel(TipiConnettore.DISABILITATO.toString());
					de.setValue(" ");
				}
			}else{
				de.setType(DataElementType.CHECKBOX);
				if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())) {
					de.setSelected(true);
				}
				de.setPostBack(true);
				//				de.setOnClick("AbilitaEndPointHTTP(\"" + tipoOp + "\")");
			}
			dati.addElement(de);
			
			
			de = new DataElement();
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
			if (endpointtype.equals(TipiConnettore.HTTP.toString())) {
				de.setValue(TipiConnettore.HTTP.toString());
			}
			else if (endpointtype.equals(TipiConnettore.HTTP.toString())) {
				de.setValue(TipiConnettore.HTTPS.toString());
			} 
			else {
				de.setValue(TipiConnettore.DISABILITATO.toString());
			}
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);

			
			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_DEBUG);
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);
//			if(this.core.isShowDebugOptionConnettore() && !TipiConnettore.DISABILITATO.toString().equals(endpointtype)){
//				de.setType(DataElementType.CHECKBOX);
//				if ( ServletUtils.isCheckBoxEnabled(connettoreDebug)) {
//					de.setSelected(true);
//				}
//			}
//			else{
			de.setType(DataElementType.HIDDEN);
			//}
			if ( ServletUtils.isCheckBoxEnabled(connettoreDebug)) {
				de.setValue("true");
			}
			else{
				de.setValue("false");
			}
			dati.addElement(de);
			
			
			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_URL);
			String tmpUrl = url;
			if(url==null || "".equals(url)){
				if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())) {
					tmpUrl =endpointtype+"://";
				}
			}
			de.setValue(tmpUrl);
			if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())) {
				if ( !this.core.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)) {
					de.setType(DataElementType.TEXT_EDIT);
					de.setRequired(true);
				} else {
					de.setType(DataElementType.TEXT);
				}
			}else{
				de.setType(DataElementType.HIDDEN);
			}
			if (endpointtype.equals(TipiConnettore.HTTPS.toString())) {
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_URL);
			}
			else{
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);
			}
			de.setSize(this.getSize());
			dati.addElement(de);
			
//			if (endpointtype.equals(TipiConnettore.HTTPS.toString())) {
//				de = new DataElement();
//				de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_URL);
//				de.setValue(tmpUrl);
//				de.setType(DataElementType.HIDDEN);
//				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_URL);
//				dati.addElement(de);
//			}
			
			if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())) {
				
//				if(!ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT.equals(servletChiamante) &&
//						!ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA.equals(servletChiamante) ){
				de = new DataElement();
				de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_HTTP);
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
				de.setType(DataElementType.CHECKBOX);
				if ( ServletUtils.isCheckBoxEnabled(autenticazioneHttp)) {
					de.setSelected(true);
				}
				de.setPostBack(true);
				dati.addElement(de);		
				//}
				
				de = new DataElement();
				de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_HTTPS);
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTPS);
				de.setType(DataElementType.CHECKBOX);
				if (endpointtype.equals(TipiConnettore.HTTPS.toString())) {
					de.setSelected(true);
				}
				de.setPostBack(true);
				dati.addElement(de);
				
				// Extended
				if(listExtendedConnettore!=null && listExtendedConnettore.size()>0){
					ServletExtendedConnettoreUtils.addToDatiEnabled(dati, listExtendedConnettore);
				}
			}	
			
			if (ServletUtils.isCheckBoxEnabled(autenticazioneHttp)) {
				this.addCredenzialiToDati(dati, CostantiConfigurazione.CREDENZIALE_BASIC.getValue(), user, password, password, null,
						servletChiamante,true,endpointtype,true,false, prefix);
			}
			
			if (endpointtype.equals(TipiConnettore.HTTPS.toString())) {
				ConnettoreHTTPSUtils.addHTTPSDati(dati, httpsurl, httpstipologia, httpshostverify, httpspath, httpstipo, 
						httpspwd, httpsalgoritmo, httpsstato, httpskeystore, httpspwdprivatekeytrust, httpspathkey, 
						httpstipokey, httpspwdkey, httpspwdprivatekey, httpsalgoritmokey, stato, this.core, this.getSize(), false, prefix);
			}
			
			// Extended
			if(listExtendedConnettore!=null && listExtendedConnettore.size()>0){
				ServletExtendedConnettoreUtils.addToDatiExtendedInfo(dati, listExtendedConnettore);
			}

		} else {

			/** VISUALIZZAZIONE COMPLETA CONNETTORI MODE */


			int sizeEP = Connettori.getList().size();
			if (!Connettori.getList().contains(TipiConnettore.HTTPS.toString()))
				sizeEP++;
			if (confPers &&
					TipologiaConnettori.TIPOLOGIA_CONNETTORI_ALL.equals(tipologiaConnettori))
				sizeEP++;
			String[] tipoEP = new String[sizeEP];
			Connettori.getList().toArray(tipoEP);
			int newCount = Connettori.getList().size();
			if (!Connettori.getList().contains(TipiConnettore.HTTPS.toString())) {
				tipoEP[newCount] = TipiConnettore.HTTPS.toString();
				newCount++;
			}
			if (confPers &&
					TipologiaConnettori.TIPOLOGIA_CONNETTORI_ALL.equals(tipologiaConnettori))
				tipoEP[newCount] = TipiConnettore.CUSTOM.toString();
			//String[] tipoEP = { TipiConnettore.DISABILITATO.toString(), TipiConnettore.HTTP.toString(), TipiConnettore.JMS.toString(), TipiConnettore.NULL.toString(), TipiConnettore.NULL_ECHO.toString() };

			DataElement de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
			de.setType(DataElementType.SELECT);
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
			de.setValues(tipoEP);
			de.setSelected(endpointtype);
			//		    de.setOnChange("CambiaEndPoint('" + tipoOp + "')");
			de.setPostBack(true);
			dati.addElement(de);

			
			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
			if (endpointtype == null || !endpointtype.equals(TipiConnettore.CUSTOM.toString()))
				de.setType(DataElementType.HIDDEN);
			else{
				de.setType(DataElementType.TEXT_EDIT);
				de.setRequired(true);
			}
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
			de.setValue(tipoconn);
			dati.addElement(de);
			
			
			
			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_DEBUG);
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);
			if(this.core.isShowDebugOptionConnettore() && !TipiConnettore.DISABILITATO.toString().equals(endpointtype)){
				de.setType(DataElementType.CHECKBOX);
				if ( ServletUtils.isCheckBoxEnabled(connettoreDebug)) {
					de.setSelected(true);
				}
			}
			else{
				de.setType(DataElementType.HIDDEN);
			}
			if ( ServletUtils.isCheckBoxEnabled(connettoreDebug)) {
				de.setValue("true");
			}
			else{
				de.setValue("false");
			}
			dati.addElement(de);	
			
			
			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_URL);
			String tmpUrl = url;
			if(url==null || "".equals(url)){
				if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())) {
					tmpUrl =endpointtype+"://";
				}
			}
			de.setValue(tmpUrl);
			if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())){
				if (!this.core.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)) {
					de.setType(DataElementType.TEXT_EDIT);
					de.setRequired(true);
				} else {
					de.setType(DataElementType.TEXT);
				}
			}
			else{
				de.setType(DataElementType.HIDDEN);
			}
			if (endpointtype.equals(TipiConnettore.HTTPS.toString())) {
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_URL);
			}
			else{
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);
			}
			de.setSize(this.getSize());
			dati.addElement(de);
			
//			if (endpointtype.equals(TipiConnettore.HTTPS.toString())) {
//				de = new DataElement();
//				de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_URL);
//				de.setValue(tmpUrl);
//				de.setType(DataElementType.HIDDEN);
//				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_URL);
//				dati.addElement(de);
//			}

			if (endpointtype.equals(TipiConnettore.HTTP.toString()) || endpointtype.equals(TipiConnettore.HTTPS.toString())){
//				if(!ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT.equals(servletChiamante) &&
//						!ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA.equals(servletChiamante) ){
				de = new DataElement();
				de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_HTTP);
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
				de.setType(DataElementType.CHECKBOX);
				if ( ServletUtils.isCheckBoxEnabled(autenticazioneHttp)) {
					de.setSelected(true);
				}
				de.setPostBack(true);
				dati.addElement(de);		
				//}
			}
			
			// Extended
			if(listExtendedConnettore!=null && listExtendedConnettore.size()>0){
				ServletExtendedConnettoreUtils.addToDatiEnabled(dati, listExtendedConnettore);
			}
			
			
			if (ServletUtils.isCheckBoxEnabled(autenticazioneHttp)) {
				this.addCredenzialiToDati(dati, CostantiConfigurazione.CREDENZIALE_BASIC.getValue(), user, password, password, null,
						servletChiamante,true,endpointtype,true,false, prefix);
			}
			
			if (endpointtype != null && endpointtype.equals(TipiConnettore.CUSTOM.toString()) &&
					!servletChiamante.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD) &&
					!servletChiamante.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD) &&
					(isConnettoreCustomUltimaImmagineSalvata!=null && isConnettoreCustomUltimaImmagineSalvata)) {
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_PROPRIETA);
				int numProp = 0;
				try {
					if (servletChiamante.equals(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_EROGATORI_FRUITORI_CHANGE)) {
						de.setUrl(ConnettoriCostanti.SERVLET_NAME_CONNETTORI_CUSTOM_PROPERTIES_LIST,
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET, servletChiamante),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID, elem1),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SOGGETTO, elem2),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SOGGETTO, elem3),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO, elem4),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SERVIZIO, elem5),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_MY_ID, elem6),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_CORRELATO, elem7));
						int myIdInt = Integer.parseInt(elem6);
						Fruitore myAccErFru = this.apsCore.getErogatoreFruitore(myIdInt);
						org.openspcoop2.core.registry.Connettore connettore = myAccErFru.getConnettore();
						if (connettore != null && (connettore.getCustom()!=null && connettore.getCustom()) ){
							for (int i = 0; i < connettore.sizePropertyList(); i++) {
								if(CostantiDB.CONNETTORE_DEBUG.equals(connettore.getProperty(i).getNome())==false  &&
										connettore.getProperty(i).getNome().startsWith(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX)==false){
									numProp++;
								}
							}
							// Non devo contare la proprietà debug: numProp = connettore.sizePropertyList();
						}
					}
					if (servletChiamante.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE)) {
						de.setUrl(ConnettoriCostanti.SERVLET_NAME_CONNETTORI_CUSTOM_PROPERTIES_LIST,
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET, servletChiamante),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID, elem1),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO, elem2),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SERVIZIO, elem3));
						AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(Long.parseLong(elem1));
						Servizio servizio = asps.getServizio();
						org.openspcoop2.core.registry.Connettore connettore = servizio.getConnettore();
						if (connettore != null && (connettore.getCustom()!=null && connettore.getCustom()) ){
							for (int i = 0; i < connettore.sizePropertyList(); i++) {
								if(CostantiDB.CONNETTORE_DEBUG.equals(connettore.getProperty(i).getNome())==false  &&
										connettore.getProperty(i).getNome().startsWith(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX)==false){
									numProp++;
								}
							}
							// Non devo contare la proprietà debug: numProp = connettore.sizePropertyList();
						}
					}
					if (servletChiamante.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE)) {
						de.setUrl(ConnettoriCostanti.SERVLET_NAME_CONNETTORI_CUSTOM_PROPERTIES_LIST,
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET, servletChiamante),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID, elem1),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_MY_ID, elem2),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SOGGETTO_EROGATORE, elem3));
						int idServizioFruitoreInt = Integer.parseInt(elem2);
						Fruitore servFru = this.apsCore.getServizioFruitore(idServizioFruitoreInt);
						org.openspcoop2.core.registry.Connettore connettore = servFru.getConnettore();
						if (connettore != null && (connettore.getCustom()!=null && connettore.getCustom()) ){
							for (int i = 0; i < connettore.sizePropertyList(); i++) {
								if(CostantiDB.CONNETTORE_DEBUG.equals(connettore.getProperty(i).getNome())==false  &&
										connettore.getProperty(i).getNome().startsWith(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX)==false){
									numProp++;
								}
							}
							// Non devo contare la proprietà debug: numProp = connettore.sizePropertyList();
						}
					}
					if (servletChiamante.equals(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT) ||
							servletChiamante.equals(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA)) {
						int idSilInt = Integer.parseInt(elem2);
						ServizioApplicativo sa = this.saCore.getServizioApplicativo(idSilInt);
						Soggetto soggetto = this.soggettiCore.getSoggettoRegistro(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
						de.setUrl(ConnettoriCostanti.SERVLET_NAME_CONNETTORI_CUSTOM_PROPERTIES_LIST,
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET, servletChiamante),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO_APPLICATIVO, elem1),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO, elem2),
								new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, soggetto.getId()+""));
						
						
						if (servletChiamante.equals(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT)) {
							InvocazioneServizio is = sa.getInvocazioneServizio();
							Connettore connettore = is.getConnettore();
							if(connettore.getCustom()==null || !connettore.getCustom()){
								// è cambiato il tipo
								de.setType(DataElementType.HIDDEN);
							}
							if (connettore != null && connettore.getCustom()!=null && connettore.getCustom()){
								for (int i = 0; i < connettore.sizePropertyList(); i++) {
									if(CostantiDB.CONNETTORE_DEBUG.equals(connettore.getProperty(i).getNome())==false  &&
											connettore.getProperty(i).getNome().startsWith(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX)==false){
										numProp++;
									}
								}
								// Non devo contare la proprietà debug: numProp = connettore.sizePropertyList();
							}
						} else {
							RispostaAsincrona ra = sa.getRispostaAsincrona();
							Connettore connettore = ra.getConnettore();
							if(connettore.getCustom()==null || !connettore.getCustom()){
								// è cambiato il tipo
								de.setType(DataElementType.HIDDEN);
							}
							if (connettore != null && connettore.getCustom()!=null && connettore.getCustom()){
								for (int i = 0; i < connettore.sizePropertyList(); i++) {
									if(CostantiDB.CONNETTORE_DEBUG.equals(connettore.getProperty(i).getNome())==false  &&
											connettore.getProperty(i).getNome().startsWith(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX)==false){
										numProp++;
									}
								}
								// Non devo contare la proprietà debug: numProp = connettore.sizePropertyList();
							}
						}
					}
					if (servletChiamante.equals(SoggettiCostanti.SERVLET_NAME_SOGGETTI_ENDPOINT)) {
						de.setUrl(ConnettoriCostanti.SERVLET_NAME_CONNETTORI_CUSTOM_PROPERTIES_LIST,
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET, servletChiamante),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID, elem1),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SOGGETTO, elem2),
								new Parameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SOGGETTO, elem3));
						int idInt = Integer.parseInt(elem1);
						SoggettoCtrlStat scs = this.soggettiCore.getSoggettoCtrlStat(idInt);
						Soggetto ss = scs.getSoggettoReg();
						org.openspcoop2.core.registry.Connettore connettore = ss.getConnettore();
						if (connettore != null && (connettore.getCustom()!=null && connettore.getCustom()) ){
							for (int i = 0; i < connettore.sizePropertyList(); i++) {
								if(CostantiDB.CONNETTORE_DEBUG.equals(connettore.getProperty(i).getNome())==false  &&
										connettore.getProperty(i).getNome().startsWith(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX)==false){
									numProp++;
								}
							}
							// Non devo contare la proprietà debug: numProp = connettore.sizePropertyList();
						}
					}
				} catch (Exception ex) {
					this.log.error("Exception: " + ex.getMessage(), ex);
				}
				de.setValue(ConnettoriCostanti.LABEL_CONNETTORE_PROPRIETA+"("+numProp+")");
				dati.addElement(de);
			}
			
			if (endpointtype.equals(TipiConnettore.HTTPS.toString())) {
				ConnettoreHTTPSUtils.addHTTPSDati(dati, httpsurl, httpstipologia, httpshostverify, httpspath, httpstipo, httpspwd, httpsalgoritmo, 
						httpsstato, httpskeystore, httpspwdprivatekeytrust, httpspathkey, httpstipokey, httpspwdkey, httpspwdprivatekey, httpsalgoritmokey, stato,
						this.core,this.getSize(), false, prefix);
			}

			if (endpointtype.equals(TipiConnettore.JMS.getNome())) {
				ConnettoreJMSUtils.addJMSDati(dati, nome, tipoconn, user, password, initcont, urlpgk, 
						provurl, connfact, sendas, objectName, tipoOperazione, stato,
						this.core,this.getSize());
			}
			
			// Extended
			if(listExtendedConnettore!=null && listExtendedConnettore.size()>0){
				ServletExtendedConnettoreUtils.addToDatiExtendedInfo(dati, listExtendedConnettore);
			}
		}


		return dati;
	}
	


	public Vector<DataElement> addEndPointToDatiAsHidden(Vector<DataElement> dati,
			String endpointtype, String url, String nome, String tipo,
			String user, String password, String initcont, String urlpgk,
			String provurl, String connfact, String sendas, String objectName, TipoOperazione tipoOperazione,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey,
			String tipoconn, String servletChiamante, String elem1, String elem2, String elem3,
			String elem4, String elem5, String elem6, String elem7, String stato) {


		Boolean confPers = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_CONFIGURAZIONI_PERSONALIZZATE);

		TipologiaConnettori tipologiaConnettori = null;
		try {
			tipologiaConnettori = Utilities.getTipologiaConnettori(this.core);
		} catch (Exception e) {
			// default
			tipologiaConnettori = TipologiaConnettori.TIPOLOGIA_CONNETTORI_ALL;
		}

		// override tipologiaconnettori :
		// se standard allora la tipologia connettori e' sempre http
		// indipendentemente
		// dalla proprieta settata
		if (InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
			tipologiaConnettori = TipologiaConnettori.TIPOLOGIA_CONNETTORI_HTTP;
		}

		DataElement de = new DataElement();
//		de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE);
//		de.setType(DataElementType.TITLE);
//		dati.addElement(de);


		/** VISUALIZZAZIONE HTTP ONLY MODE */

		if (TipologiaConnettori.TIPOLOGIA_CONNETTORI_HTTP.equals(tipologiaConnettori)) {
			
			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_ABILITATO);

			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_CHECK);
			if(!TipiConnettore.HTTP.toString().equals(endpointtype) &&
					!TipiConnettore.DISABILITATO.toString().equals(endpointtype)){
//				de.setLabel(ConnettoriCostanti.LABEL_CONNETTORE_CONNETTORE_IMPOSTATO_MODALITA_AVANZATA);
				de.setType(DataElementType.HIDDEN);
//				de.setValue(" ");
				de.setValue(Costanti.CHECK_BOX_DISABLED);
			}
			else if( (  (AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS.equals(objectName) && TipoOperazione.CHANGE.equals(tipoOperazione))
					|| 
					(AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI.equals(objectName) && TipoOperazione.CHANGE.equals(tipoOperazione))
					||
					(AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_EROGATORI_FRUITORI.equals(objectName))
					)
					&& StatiAccordo.finale.toString().equals(stato) && this.core.isShowGestioneWorkflowStatoDocumenti() ){
				if (endpointtype.equals(TipiConnettore.HTTP.toString())) {
					de.setType(DataElementType.HIDDEN);
//					de.setSelected(true);
					de.setValue(Costanti.CHECK_BOX_ENABLED);
				}else{
					de.setType(DataElementType.HIDDEN);
					de.setLabel(TipiConnettore.DISABILITATO.toString());
					de.setValue(Costanti.CHECK_BOX_DISABLED);
				}
			}else{
				de.setType(DataElementType.HIDDEN);
				if (endpointtype.equals(TipiConnettore.HTTP.toString())) {
//					de.setSelected(true);
					de.setValue(Costanti.CHECK_BOX_ENABLED);
				}
//				de.setPostBack(true);
				//				de.setOnClick("AbilitaEndPointHTTP(\"" + tipoOp + "\")");
			}
			dati.addElement(de);

			de = new DataElement();
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
			if (endpointtype.equals(TipiConnettore.HTTP.toString())) {
				de.setValue(TipiConnettore.HTTP.toString());
			} else {
				de.setValue(TipiConnettore.DISABILITATO.toString());
			}
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_URL);
			de.setValue((url != null) && !"".equals(url) ? url : "http://");
			de.setType(DataElementType.HIDDEN);
			if (endpointtype.equals(TipiConnettore.HTTPS.toString())) {
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_URL);
			}
			else{
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);
			}
			dati.addElement(de);

		} else {

			/** VISUALIZZAZIONE COMPLETA CONNETTORI MODE */


			int sizeEP = Connettori.getList().size();
			if (!Connettori.getList().contains(TipiConnettore.HTTPS.toString()))
				sizeEP++;
			if (confPers &&
					TipologiaConnettori.TIPOLOGIA_CONNETTORI_ALL.equals(tipologiaConnettori))
				sizeEP++;
			String[] tipoEP = new String[sizeEP];
			Connettori.getList().toArray(tipoEP);
			int newCount = Connettori.getList().size();
			if (!Connettori.getList().contains(TipiConnettore.HTTPS.toString())) {
				tipoEP[newCount] = TipiConnettore.HTTPS.toString();
				newCount++;
			}
			if (confPers &&
					TipologiaConnettori.TIPOLOGIA_CONNETTORI_ALL.equals(tipologiaConnettori))
				tipoEP[newCount] = TipiConnettore.CUSTOM.toString();
			//String[] tipoEP = { TipiConnettore.DISABILITATO.toString(), TipiConnettore.HTTP.toString(), TipiConnettore.JMS.toString(), TipiConnettore.NULL.toString(), TipiConnettore.NULL_ECHO.toString() };

			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
			de.setType(DataElementType.HIDDEN);
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
//			de.setValues(tipoEP);
			de.setValue(endpointtype);
			//		    de.setOnChange("CambiaEndPoint('" + tipoOp + "')");
//			de.setPostBack(true);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_URL);
			de.setValue(url);
//			if (endpointtype.equals(TipiConnettore.HTTP.toString())){
//				if (!this.core.isShowGestioneWorkflowStatoDocumenti() || !StatiAccordo.finale.toString().equals(stato)) {
//					de.setType(DataElementType.TEXT_EDIT);
//					de.setRequired(true);
//				} else {
//					de.setType(DataElementType.TEXT);
//				}
//			}
//			else{
				de.setType(DataElementType.HIDDEN);
//		}
			if (endpointtype.equals(TipiConnettore.HTTPS.toString())) {
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_URL);
			}
			else{
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);
			}
			de.setSize(this.getSize());
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
//			if (endpointtype == null || !endpointtype.equals(TipiConnettore.CUSTOM.toString()))
				de.setType(DataElementType.HIDDEN);
//			else{
//				de.setType(DataElementType.TEXT_EDIT);
//				de.setRequired(true);
//			}
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
			de.setValue(tipoconn);
			dati.addElement(de);

//			if (endpointtype != null && endpointtype.equals(TipiConnettore.CUSTOM.toString()) &&
//					!servletChiamante.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD) &&
//					!servletChiamante.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD)) {
//				 Eliminati Link non dovrebbero servire... eventualmente riportare da su
//			}
		}

		if (endpointtype.equals(TipiConnettore.HTTPS.toString())) {
			ConnettoreHTTPSUtils.addHTTPSDatiAsHidden(dati, httpsurl, httpstipologia, httpshostverify, httpspath, httpstipo, httpspwd, 
					httpsalgoritmo, httpsstato, httpskeystore, httpspwdprivatekeytrust, httpspathkey, httpstipokey, httpspwdkey, httpspwdprivatekey, httpsalgoritmokey, stato,
					this.core,this.getSize());
			
		}

		if (endpointtype.equals(TipiConnettore.JMS.getNome())) {
			ConnettoreJMSUtils.addJMSDatiAsHidden(dati, nome, tipoconn, user, password, initcont, urlpgk, 
					provurl, connfact, sendas, objectName, tipoOperazione, stato,
					this.core,this.getSize());
		}

		return dati;
	}




	// Controlla i dati dell'end-point
	public boolean endPointCheckData(List<ExtendedConnettore> listExtendedConnettore) throws Exception {
		try {

			//String endpointtype = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
			String endpointtype = this.readEndPointType();
			String tipoconn = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
			String autenticazioneHttp = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
			
			// http
			String url = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);

			// jms
			String nome = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
			String tipo = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
			String user = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
			String password = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
			String initcont = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
			String urlpgk = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
			String provurl = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
			String connfact = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
			String sendas = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);

			// https
			String httpsurl = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_URL);
			String httpstipologia = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE);
			String httpshostverifyS = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			boolean httpshostverify = ServletUtils.isCheckBoxEnabled(httpshostverifyS);
			String httpspath = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
			String httpstipo = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			String httpspwd = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			String httpsalgoritmo = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			String httpsstatoS = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
			boolean httpsstato = ServletUtils.isCheckBoxEnabled(httpsstatoS);
			String httpskeystore = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
			String httpspwdprivatekeytrust = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
			String httpspathkey = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			String httpstipokey = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
			String httpspwdkey = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
			String httpspwdprivatekey = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
			String httpsalgoritmokey = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);

			if(ServletUtils.isCheckBoxEnabled(autenticazioneHttp)){
				user = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_USERNAME);
				password = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_PASSWORD);
			}
			
			return endPointCheckData(endpointtype, url, nome, tipo, user,
					password, initcont, urlpgk, provurl, connfact, sendas,
					httpsurl, httpstipologia, httpshostverify,
					httpspath, httpstipo, httpspwd, httpsalgoritmo, httpsstato,
					httpskeystore, httpspwdprivatekeytrust, httpspathkey,
					httpstipokey, httpspwdkey, httpspwdprivatekey,
					httpsalgoritmokey, tipoconn, autenticazioneHttp,
					listExtendedConnettore);
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Controlla i dati dell'end-point
	public boolean endPointCheckData(String endpointtype, String url, String nome,
			String tipo, String user, String password, String initcont,
			String urlpgk, String provurl, String connfact, String sendas,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey,
			String tipoconn, String autenticazioneHttp,
			List<ExtendedConnettore> listExtendedConnettore)
					throws Exception {
		try{
			if (url == null)
				url = "";
			if (nome == null)
				nome = "";
			if (tipo == null)
				tipo = "";
			if (user == null)
				user = "";
			if (password == null)
				password = "";
			if (initcont == null)
				initcont = "";
			if (urlpgk == null)
				urlpgk = "";
			if (provurl == null)
				provurl = "";
			if (connfact == null)
				connfact = "";
			if (sendas == null)
				sendas = "";
			if (httpsurl == null)
				httpsurl = "";
			if (httpstipologia == null)
				httpstipologia = "";
			if (httpspath == null)
				httpspath = "";
			if (httpstipo == null)
				httpstipo = "";
			if (httpspwd == null)
				httpspwd = "";
			if (httpsalgoritmo == null)
				httpsalgoritmo = "";
			if (httpskeystore == null)
				httpskeystore = "";
			if (httpspwdprivatekeytrust == null)
				httpspwdprivatekeytrust = "";
			if (httpspathkey == null)
				httpspathkey = "";
			if (httpstipokey == null)
				httpstipokey = "";
			if (httpspwdkey == null)
				httpspwdkey = "";
			if (httpspwdprivatekey == null)
				httpspwdprivatekey = "";
			if (httpsalgoritmokey == null)
				httpsalgoritmokey = "";
			if (tipoconn == null)
				tipoconn = "";

			// Controllo che non ci siano spazi nei campi di testo
			if ((url.indexOf(" ") != -1) || 
					(nome.indexOf(" ") != -1) ||
					(user.indexOf(" ") != -1) || 
					(password.indexOf(" ") != -1) || 
					(initcont.indexOf(" ") != -1) || 
					(urlpgk.indexOf(" ") != -1) || 
					(provurl.indexOf(" ") != -1) || 
					(connfact.indexOf(" ") != -1) ||
					(httpsurl.indexOf(" ") != -1) ||
					(httpspath.indexOf(" ") != -1) ||
					(httpspwd.indexOf(" ") != -1) ||
					(httpsalgoritmo.indexOf(" ") != -1) ||
					(httpskeystore.indexOf(" ") != -1) ||
					(httpspwdprivatekeytrust.indexOf(" ") != -1) ||
					(httpspathkey.indexOf(" ") != -1) ||
					(httpspwdkey.indexOf(" ") != -1) ||
					(httpspwdprivatekey.indexOf(" ") != -1) ||
					(httpsalgoritmokey.indexOf(" ") != -1) ||
					(tipoconn.indexOf(" ") != -1)) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}
			
			if(ServletUtils.isCheckBoxEnabled(autenticazioneHttp)){
				if (user == null || "".equals(user)) {
					this.pd.setMessage("Username obbligatoria per l'autenticazione http");
					return false;
				}
				if (password == null || "".equals(password)) {
					this.pd.setMessage("Password obbligatoria per l'autenticazione http");
					return false;
				}
			}

			// Controllo che i campi "select" abbiano uno dei valori ammessi
			if (!Connettori.contains(endpointtype) && !endpointtype.equals(TipiConnettore.CUSTOM.toString())) {
				this.pd.setMessage("Tipo Connettore dev'essere uno tra : " + Connettori.getList());
				return false;
			}

			if (!httpstipologia.equals("") &&
					!Utilities.contains(httpstipologia, ConnettoriCostanti.TIPOLOGIE_HTTPS)) {
				this.pd.setMessage("Il campo Tipologia può assumere uno tra i seguenti valori: "+Utilities.toString(ConnettoriCostanti.TIPOLOGIE_HTTPS, ","));
				return false;
			}

			if (!httpstipo.equals("") &&
					!Utilities.contains(httpstipo, ConnettoriCostanti.TIPOLOGIE_KEYSTORE)) {
				this.pd.setMessage("Il campo Tipo per l'Autenticazione Server può assumere uno tra i seguenti valori: "+Utilities.toString(ConnettoriCostanti.TIPOLOGIE_KEYSTORE, ","));
				return false;
			}
			if (!httpstipokey.equals("") &&
					!Utilities.contains(httpstipokey, ConnettoriCostanti.TIPOLOGIE_KEYSTORE)) {
				this.pd.setMessage("Il campo Tipo per l'Autenticazione Client può assumere uno tra i seguenti valori: "+Utilities.toString(ConnettoriCostanti.TIPOLOGIE_KEYSTORE, ","));
				return false;
			}

			// Controllo campi obbligatori per il tipo di connettore custom
			if (endpointtype.equals(TipiConnettore.CUSTOM.toString()) && (tipoconn == null || "".equals(tipoconn))) {
				this.pd.setMessage("Tipo connettore personalizzato obbligatorio per il tipo di connettore custom");
				return false;
			}

			// Controllo campi obbligatori per il tipo di connettore http
			if (endpointtype.equals(TipiConnettore.HTTP.toString()) && (url == null || "".equals(url))) {
				this.pd.setMessage("Url obbligatoria per il tipo di connettore http");
				return false;
			}

			// Se il tipo di connettore è custom, tipoconn non può essere
			if (endpointtype.equals(TipiConnettore.CUSTOM.toString()) && (tipoconn.equals(TipiConnettore.HTTP.toString()) || tipoconn.equals(TipiConnettore.HTTPS.toString()) || tipoconn.equals(TipiConnettore.JMS.toString()) || tipoconn.equals(TipiConnettore.NULL.toString()) || tipoconn.equals(TipiConnettore.NULLECHO.toString()) || tipoconn.equals(TipiConnettore.DISABILITATO.toString()) )) {
				this.pd.setMessage("Tipo connettore personalizzato non può assumere i valori: disabilitato,http,https,jms,null,nullEcho");
				return false;
			}

			// Se e' stata specificata la url, dev'essere valida
			if (endpointtype.equals(TipiConnettore.HTTP.toString()) && !url.equals("") ){
				try{
					org.openspcoop2.utils.regexp.RegExpUtilities.validateUrl(url);
				}catch(Exception e){
					this.pd.setMessage("Url non correttamente formata: "+e.getMessage());
					return false;
				}
			}

			// Controllo campi obbligatori per il tipo di connettore jms
			if (endpointtype.equals(TipiConnettore.JMS.toString())) {
				if (nome == null || "".equals(nome)) {
					this.pd.setMessage("Nome della coda/topic obbligatorio per il tipo di connettore jms");
					return false;
				}
				if (tipo == null || "".equals(tipo)) {
					this.pd.setMessage("Tipo di coda obbligatorio per il tipo di connettore jms");
					return false;
				}
				if (sendas == null || "".equals(sendas)) {
					this.pd.setMessage("Tipo di messaggio (SendAs) obbligatorio per il tipo di connettore jms");
					return false;
				}
				if (connfact == null || "".equals(connfact)) {
					this.pd.setMessage("Connection Factory obbligatoria per il tipo di connettore jms");
					return false;
				}
			}

			if (endpointtype.equals(TipiConnettore.JMS.toString()) && !Utilities.contains(tipo, ConnettoriCostanti.TIPI_CODE_JMS)) {
				this.pd.setMessage("Tipo Jms dev'essere: "+Utilities.toString(ConnettoriCostanti.TIPI_CODE_JMS, ","));
				return false;
			}
			if (endpointtype.equals(TipiConnettore.JMS.toString()) && !Utilities.contains(sendas, ConnettoriCostanti.TIPO_SEND_AS) ) {
				this.pd.setMessage("Send As dev'essere: "+Utilities.toString(ConnettoriCostanti.TIPO_SEND_AS, ","));
				return false;
			}

			// Controllo campi obbligatori per il tipo di connettore https
			if (endpointtype.equals(TipiConnettore.HTTPS.toString())) {
				if ("".equals(httpsurl)) {
					this.pd.setMessage("Url obbligatorio per il tipo di connettore https");
					return false;
				}else{
					try{
						org.openspcoop2.utils.regexp.RegExpUtilities.validateUrl(httpsurl);
					}catch(Exception e){
						this.pd.setMessage("Url non correttamente formata: "+e.getMessage());
						return false;
					}
				}
				if ("".equals(httpspath)) {
					this.pd.setMessage("Il campo 'Path' è obbligatorio per l'Autenticazione Server");
					return false;
				}else{
					try{
						File f = new File(httpspath);
						f.getAbsolutePath();
					}catch(Exception e){
						this.pd.setMessage("Il campo 'Path', obbligatorio per l'Autenticazione Server, non è correttamente definito: "+e.getMessage());
						return false;
					}
				}
				if ("".equals(httpspwd)) {
					this.pd.setMessage("La password del TrustStore è necessaria per l'Autenticazione Server");
					return false;
				}
				if ("".equals(httpsalgoritmo)) {
					this.pd.setMessage("Il campo 'Algoritmo' è obbligatorio per l'Autenticazione Server");
					return false;
				}
				if (httpsstato) {
					if (ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT.equals(httpskeystore)) {
						if ("".equals(httpspwdprivatekeytrust)) {
							this.pd.setMessage("La password della chiave privata è necessaria in caso di Autenticazione Client abilitata");
							return false;
						}
						if ("".equals(httpsalgoritmokey)) {
							this.pd.setMessage("Il campo 'Algoritmo' è obbligatorio in caso di Autenticazione Client abilitata");
							return false;
						}
					} else {
						if ("".equals(httpspathkey)) {
							this.pd.setMessage("Il campo 'Path' è obbligatorio per l'Autenticazione Client, in caso di dati di accesso al KeyStore ridefiniti");
							return false;
						}else{
							try{
								File f = new File(httpspathkey);
								f.getAbsolutePath();
							}catch(Exception e){
								this.pd.setMessage("Il campo 'Path', obbligatorio per l'Autenticazione Client in caso di dati di accesso al KeyStore ridefiniti, non è correttamente definito: "+e.getMessage());
								return false;
							}
						}
						if ("".equals(httpspwdkey)) {
							this.pd.setMessage("La password del KeyStore è necessaria per l'Autenticazione Client, in caso di dati di accesso al KeyStore ridefiniti");
							return false;
						}
						if ("".equals(httpspwdprivatekey)) {
							this.pd.setMessage("La password della chiave privata è necessaria in caso di Autenticazione Client abilitata");
							return false;
						}
						if ("".equals(httpsalgoritmokey)) {
							this.pd.setMessage("Il campo 'Algoritmo' è obbligatorio per l'Autenticazione Client, in caso di dati di accesso al KeyStore ridefiniti");
							return false;
						}
					}
				}
			}
			
			
			try{
				ServletExtendedConnettoreUtils.checkInfo(listExtendedConnettore);
			}catch(Exception e){
				this.pd.setMessage(e.getMessage());
				return false;
			}
			
			return true;
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}


	public void fillConnettore(org.openspcoop2.core.registry.Connettore connettore,
			String connettoreDebug,
			String tipoConnettore, String oldtipo, String tipoconn, String http_url, String jms_nome,
			String jms_tipo, String user, String pwd,
			String jms_nf_initial, String jms_nf_urlPkg, String jms_np_url,
			String jms_connection_factory, String jms_send_as,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey,
			List<ExtendedConnettore> listExtendedConnettore)
					throws Exception {
		try {
			
			// azzero proprieta esistenti precedentemente
			// (se il connettore è custom lo faccio solo se prima
			// non era custom)
			if (!tipoConnettore.equals(TipiConnettore.CUSTOM.toString()) ||
					!tipoConnettore.equals(oldtipo)) {
				while(connettore.sizePropertyList()>0)
					connettore.removeProperty(0);
			}
			
			String debugValue = null;
			if(ServletUtils.isCheckBoxEnabled(connettoreDebug)){
				debugValue = "true";
			}
			else{
				debugValue = "false";
			}
			boolean found = false;
			for (int i = 0; i < connettore.sizePropertyList(); i++) {
				Property pCheck = connettore.getProperty(i);
				if(CostantiDB.CONNETTORE_DEBUG.equals(pCheck.getNome())){
					pCheck.setValore(debugValue);
					found = true;
					break;
				}
			}
			if(!found){
				Property p = new Property();
				p.setNome(CostantiDB.CONNETTORE_DEBUG);
				p.setValore(debugValue);
				connettore.addProperty(p);
			}

			org.openspcoop2.core.registry.Property prop = null;

			if (tipoConnettore.equals(TipiConnettore.CUSTOM.toString()))
				connettore.setTipo(tipoconn);
			else
				connettore.setTipo(tipoConnettore);
			// Inizializzo a false... Poi eventualmente lo setto a true
			connettore.setCustom(false);
			if (tipoConnettore.equals(TipiConnettore.HTTP.getNome())) {
				
				prop = new org.openspcoop2.core.registry.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTP_LOCATION);
				prop.setValore(http_url);
				connettore.addProperty(prop);
				
				if(user!=null){
					prop = new org.openspcoop2.core.registry.Property();
					prop.setNome(CostantiDB.CONNETTORE_USER);
					prop.setValore(user);
					connettore.addProperty(prop);
				}
				
				if(pwd!=null){
					prop = new org.openspcoop2.core.registry.Property();
					prop.setNome(CostantiDB.CONNETTORE_PWD);
					prop.setValore(pwd);
					connettore.addProperty(prop);
				}

			} else if (tipoConnettore.equals(TipiConnettore.JMS.getNome())) {
				ConnettoreJMSUtils.fillConnettoreRegistry(connettore, jms_nome, jms_tipo, user, pwd, 
						jms_nf_initial, jms_nf_urlPkg, jms_np_url, jms_connection_factory, jms_send_as);
			} else if (tipoConnettore.equals(TipiConnettore.NULL.getNome())) {
				// nessuna proprieta per connettore null
			} else if (tipoConnettore.equals(TipiConnettore.NULLECHO.getNome())) {
				// nessuna proprieta per connettore nullEcho
			} else if (tipoConnettore.equals("https")) {
				ConnettoreHTTPSUtils.fillConnettoreRegistry(connettore, httpsurl, httpstipologia, httpshostverify, httpspath, 
						httpstipo, httpspwd, httpsalgoritmo, httpsstato, httpskeystore, httpspwdprivatekeytrust, 
						httpspathkey, httpstipokey, httpspwdkey, httpspwdprivatekey, httpsalgoritmokey,
						user, pwd);
			} else if (tipoConnettore.equals(TipiConnettore.CUSTOM.toString())) {
				connettore.setCustom(true);
			} else if (!tipoConnettore.equals(TipiConnettore.DISABILITATO.getNome()) &&
					!tipoConnettore.equals(TipiConnettore.CUSTOM.toString())) {
				Property [] cp = this.connettoriCore.getPropertiesConnettore(tipoConnettore);
				List<Property> cps = new ArrayList<Property>();
				if(cp!=null){
					for (int i = 0; i < cp.length; i++) {
						cps.add(cp[i]);
					}
				}
				connettore.setPropertyList(cps);
			}
			
			// Extended
			ExtendedConnettoreConverter.fillExtendedInfoIntoConnettore(listExtendedConnettore, connettore);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}

	}


	public void fillConnettore(org.openspcoop2.core.config.Connettore connettore,
			String connettoreDebug,
			String tipoConnettore, String oldtipo, String tipoconn, String http_url, String jms_nome,
			String jms_tipo, String jms_user, String jms_pwd,
			String jms_nf_initial, String jms_nf_urlPkg, String jms_np_url,
			String jms_connection_factory, String jms_send_as,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey,
			List<ExtendedConnettore> listExtendedConnettore)
					throws Exception {
		try {
			
			// azzero proprieta esistenti precedentemente
			// (se il connettore è custom lo faccio solo se prima
			// non era custom)
			if (!tipoConnettore.equals(TipiConnettore.CUSTOM.toString()) ||
					!tipoConnettore.equals(oldtipo)) {
				while(connettore.sizePropertyList()>0)
					connettore.removeProperty(0);
			}

			
			String debugValue = null;
			if(ServletUtils.isCheckBoxEnabled(connettoreDebug)){
				debugValue = "true";
			}
			else{
				debugValue = "false";
			}
			boolean found = false;
			for (int i = 0; i < connettore.sizePropertyList(); i++) {
				org.openspcoop2.core.config.Property pCheck = connettore.getProperty(i);
				if(CostantiDB.CONNETTORE_DEBUG.equals(pCheck.getNome())){
					pCheck.setValore(debugValue);
					found = true;
					break;
				}
			}
			if(!found){
				org.openspcoop2.core.config.Property p = new org.openspcoop2.core.config.Property();
				p.setNome(CostantiDB.CONNETTORE_DEBUG);
				p.setValore(debugValue);
				connettore.addProperty(p);
			}
			
			org.openspcoop2.core.config.Property prop = null;

			if (tipoConnettore.equals(TipiConnettore.CUSTOM.toString()))
				connettore.setTipo(tipoconn);
			else
				connettore.setTipo(tipoConnettore);
			// Inizializzo a false... Poi eventualmente lo setto a true
			connettore.setCustom(false);
			if (tipoConnettore.equals(TipiConnettore.HTTP.getNome())) {
				prop = new org.openspcoop2.core.config.Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTP_LOCATION);
				prop.setValore(http_url);
				connettore.addProperty(prop);

			} else if (tipoConnettore.equals(TipiConnettore.JMS.getNome())) {
				ConnettoreJMSUtils.fillConnettoreConfig(connettore, jms_nome, jms_tipo, jms_user, jms_pwd, 
						jms_nf_initial, jms_nf_urlPkg, jms_np_url, jms_connection_factory, jms_send_as);
			} else if (tipoConnettore.equals(TipiConnettore.NULL.getNome())) {
				// nessuna proprieta per connettore null
			} else if (tipoConnettore.equals(TipiConnettore.NULLECHO.getNome())) {
				// nessuna proprieta per connettore nullEcho
			} else if (tipoConnettore.equals("https")) {
				ConnettoreHTTPSUtils.fillConnettoreConfig(connettore, httpsurl, httpstipologia, httpshostverify, httpspath, httpstipo, 
						httpspwd, httpsalgoritmo, httpsstato, httpskeystore, httpspwdprivatekeytrust, httpspathkey, httpstipokey, 
						httpspwdkey, httpspwdprivatekey, httpsalgoritmokey);
			} else if (tipoConnettore.equals(TipiConnettore.CUSTOM.toString())) {
				connettore.setCustom(true);
			}else if (!tipoConnettore.equals(TipiConnettore.DISABILITATO.getNome()) &&
					!tipoConnettore.equals(TipiConnettore.CUSTOM.toString())) {
				org.openspcoop2.core.config.Property [] cp = this.connettoriCore.getPropertiesConnettoreConfig(tipoConnettore);
				List<org.openspcoop2.core.config.Property> cps = new ArrayList<org.openspcoop2.core.config.Property>();
				if(cp!=null){
					for (int i = 0; i < cp.length; i++) {
						cps.add(cp[i]);
					}
				}
				connettore.setPropertyList(cps);
			}
			
			// Extended
			ExtendedConnettoreConverter.fillExtendedInfoIntoConnettore(listExtendedConnettore, connettore);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}

	}

}
