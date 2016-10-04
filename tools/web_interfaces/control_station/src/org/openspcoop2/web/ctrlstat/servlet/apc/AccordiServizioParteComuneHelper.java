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
package org.openspcoop2.web.ctrlstat.servlet.apc;

import java.net.URI;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Message;
import org.openspcoop2.core.registry.MessagePart;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;
import org.openspcoop2.protocol.sdk.validator.ValidazioneResult;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCostanti;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ExporterUtils;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.AreaBottoni;
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
 * AccordiServizioParteComuneHelper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordiServizioParteComuneHelper extends ConsoleHelper {

	private ConnettoriHelper connettoriHelper = null;
	public AccordiServizioParteComuneHelper(HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(request, pd,  session);
		this.connettoriHelper = new ConnettoriHelper(request, pd, session);
	}



	/**
	 * Utilita converte i valori CostantiRegistroServizi in una Stringa
	 * presentabile all'utente
	 * 
	 * @param toConvert
	 * @return valore convertito
	 */
	public static String convertProfiloCollaborazioneDB2View(String toConvert) {
		String profilo = "";
		if ((toConvert == null) || toConvert.equals("")) {
			profilo = "";
		} else if (toConvert.equals(CostantiRegistroServizi.ONEWAY)) {
			profilo = "oneway";
		} else if (toConvert.equals(CostantiRegistroServizi.SINCRONO)) {
			profilo = "sincrono";
		} else if (toConvert.equals(CostantiRegistroServizi.ASINCRONO_SIMMETRICO)) {
			profilo = "asincronoSimmetrico";
		} else if (toConvert.equals(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO)) {
			profilo = "asincronoAsimmetrico";
		}

		return profilo;
	}
	public static String convertProfiloCollaborazioneDB2View(ProfiloCollaborazione toConvert) {
		String profilo = "";
		if ((toConvert == null) || toConvert.equals("")) {
			profilo = "";
		} else if (toConvert.equals(CostantiRegistroServizi.ONEWAY)) {
			profilo = "oneway";
		} else if (toConvert.equals(CostantiRegistroServizi.SINCRONO)) {
			profilo = "sincrono";
		} else if (toConvert.equals(CostantiRegistroServizi.ASINCRONO_SIMMETRICO)) {
			profilo = "asincronoSimmetrico";
		} else if (toConvert.equals(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO)) {
			profilo = "asincronoAsimmetrico";
		}

		return profilo;
	}


	/**
	 * Utilita di conversione della stringa passata dalla vista, a valori
	 * CostantiRegistroServizi come da specifica
	 * 
	 * @param toConvert
	 * @return valore convertito
	 */
	public static String convertProfiloCollaborazioneView2DB(String toConvert) {
		String profilo = "";
		if ((toConvert == null) || toConvert.equals("")) {
			profilo = "";
		} else if (toConvert.equals("oneway")) {
			profilo = CostantiRegistroServizi.ONEWAY.toString();
		} else if (toConvert.equals("sincrono")) {
			profilo = CostantiRegistroServizi.SINCRONO.toString();
		} else if (toConvert.equals("asincronoSimmetrico")) {
			profilo = CostantiRegistroServizi.ASINCRONO_SIMMETRICO.toString();
		} else if (toConvert.equals("asincronoAsimmetrico")) {
			profilo = CostantiRegistroServizi.ASINCRONO_ASIMMETRICO.toString();
		}

		return profilo;
	}

	/**
	 * Utilita di conversione della stringa passata dalla vista, a valori
	 * CostantiRegistroServizi come da specifica
	 * 
	 * @param toConvert
	 * @return valore convertito
	 */
	public static String convertAbilitatoDisabilitatoView2DB(String toConvert) {
		String value = null;
		if ((toConvert == null) || ("".equals(toConvert))) {
			value = null;
		} else {
			if (toConvert.equals("yes")) {
				value = CostantiRegistroServizi.ABILITATO.toString();
			} else {
				value = CostantiRegistroServizi.DISABILITATO.toString();
			}
		}

		return value;
	}

	/**
	 * Utilita di conversione della stringa passata dalla vista, a valori
	 * CostantiRegistroServizi come da specifica
	 * 
	 * @param toConvert
	 * @return valore convertito
	 */
	public static String convertAbilitatoDisabilitatoDB2View(String toConvert) {
		if (CostantiRegistroServizi.ABILITATO.equals(toConvert))
			return "yes";
		else
			return "no";
	}
	public static String convertAbilitatoDisabilitatoDB2View(StatoFunzionalita toConvert) {
		if (CostantiRegistroServizi.ABILITATO.equals(toConvert))
			return "yes";
		else
			return "no";
	}



	public void prepareAccordiErogatoriFruitoriList(List<Fruitore> lista, ISearch ricerca,String tipoAccordo)
			throws Exception {
		try {
			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
			String superUser = ServletUtils.getUserLoginFromSession(this.session);

			String id = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			int idInt = Integer.parseInt(id);


			ServletUtils.addListElementIntoSession(this.session, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_EROGATORI_FRUITORI,
					new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id),
					new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO, tipoAccordo));


			String nomeprov = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_NOME_SOGGETTO);
			String tipoprov = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_TIPO_SOGGETTO);
			String nomeservizio = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_NOME_SERVIZIO);
			String tiposervizio = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_TIPO_SERVIZIO);
			String correlato = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_CORRELATO);

			int idLista = Liste.ACCORDI_EROGATORI_FRUITORI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			Hashtable<String, String> campiHidden = new Hashtable<String, String>();
			campiHidden.put(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_NOME_SOGGETTO, nomeprov);
			campiHidden.put(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_TIPO_SOGGETTO, tipoprov);
			campiHidden.put(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_NOME_SERVIZIO, nomeservizio);
			campiHidden.put(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_TIPO_SERVIZIO, tiposervizio);
			this.pd.setHidden(campiHidden);

			// Prendo il nome dell'accordo
			AccordoServizioParteComune as = this.apcCore.getAccordoServizio(idInt);
			String uriAS = this.idAccordoFactory.getUriFromAccordo(as);

			// setto la barra del titolo
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo),null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
										new Parameter(AccordiServizioParteComuneCostanti.LABEL_ACCORDI_EROGATORI_DI + " di " + uriAS, 
												AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_EROGATORI_LIST+"?"+
														AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID+"="+id+"&"+
														AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME+"="+uriAS+"&"+
														AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
														AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
														new Parameter(AccordiServizioParteComuneCostanti.LABEL_ACCORDI_FRUITORI_DI+ 
																tipoprov + "/" + nomeprov + " - " + tiposervizio + "/" + nomeservizio, null)
						);
			}else{
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo),null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
										new Parameter(AccordiServizioParteComuneCostanti.LABEL_ACCORDI_EROGATORI_DI + " di " + uriAS, 
												AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_EROGATORI_LIST+"?"+
														AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID+"="+id+"&"+
														AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME+"="+uriAS+"&"+
														AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
														AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
														new Parameter(AccordiServizioParteComuneCostanti.LABEL_ACCORDI_FRUITORI_DI+ 
																tipoprov + "/" + nomeprov + " - " + tiposervizio + "/" + nomeservizio, 
																AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_EROGATORI_FRUITORI_LIST+"?"+
																		AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID+"="+id+"&"+
																		AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_NOME_SOGGETTO+"="+nomeprov+"&"+
																		AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_TIPO_SOGGETTO+"="+tipoprov+"&"+
																		AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_NOME_SERVIZIO+"="+nomeservizio+"&"+
																		AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_TIPO_SERVIZIO+"="+tiposervizio+"&"+
																		AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_CORRELATO+"="+correlato+"&"+
																		AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
																		AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
																		new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null)
						);
			}


			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, AccordiServizioParteComuneCostanti.LABEL_ACCORDI_FRUITORI_DI+ 
						tipoprov + "/" + nomeprov + " - " + tiposervizio + "/" + nomeservizio, search);
			}

			IDSoggetto ids = new IDSoggetto(tipoprov, nomeprov);
			Soggetto sogg = this.soggettiCore.getSoggettoRegistro(ids);
			IDServizio idserv = new IDServizio(ids, tiposervizio, nomeservizio);
			AccordoServizioParteSpecifica servCfg = null;
			if ("true".equals(correlato)) {
				idserv.setTipologiaServizio(TipologiaServizio.CORRELATO.toString());
			}
			servCfg = this.apsCore.getServizio(idserv);
			AccordoServizioParteSpecifica serv = this.apsCore.getAccordoServizioParteSpecifica(servCfg.getId());

			// setto le label delle colonne
			User user = ServletUtils.getUserFromSession(this.session);
			String[] labels;
			String labelFruitore = AccordiServizioParteComuneCostanti.LABEL_FRUITORE;
			if (InterfaceType.STANDARD.equals(user.getInterfaceType())) {
				if(this.core.isShowGestioneWorkflowStatoDocumenti()){
					String[] l = { labelFruitore,AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_STATO_PACKAGE };
					labels = l;
				}else{
					String[] l = { labelFruitore };
					labels = l;
				}
			} else {
				if(this.core.isShowGestioneWorkflowStatoDocumenti()){
					String[] l = { labelFruitore,AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_STATO_PACKAGE, 
							AccordiServizioParteComuneCostanti.LABEL_POLITICHE_SICUREZZA, 
							AccordiServizioParteComuneCostanti.LABEL_POLITICHE_SLA};
					labels = l;
				}else{
					String[] l = { labelFruitore, 
							AccordiServizioParteComuneCostanti.LABEL_POLITICHE_SICUREZZA, 
							AccordiServizioParteComuneCostanti.LABEL_POLITICHE_SLA};
					labels = l;
				}
			}
			this.pd.setLabels(labels);

			// Prendo la lista di soggetti dell'utente connesso
			List<Long> idsSogg = new ArrayList<Long>();
			List<Soggetto> listaSog = this.soggettiCore.soggettiRegistroList(superUser, new Search());
			Iterator<Soggetto> itS = listaSog.iterator();
			while (itS.hasNext()) {
				Soggetto ss = itS.next();
				idsSogg.add(ss.getId());
			}

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			Iterator<Fruitore> it = lista.iterator();
			Fruitore fru = null;
			while (it.hasNext()) {
				fru = it.next();
				Vector<DataElement> e = new Vector<DataElement>();

				String nomefruit = fru.getNome();
				String tipofruit = fru.getTipo();

				// IDSoggetto fruitore
				long idSoggettoFruitore = this.soggettiCore.getSoggettoRegistro(new IDSoggetto(tipofruit, nomefruit)).getId();

				DataElement de = new DataElement();
				if (idsSogg.contains(id))
					de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_EROGATORI_FRUITORI_CHANGE, 
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_NOME_SOGGETTO, nomeprov),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_TIPO_SOGGETTO, tipoprov),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_NOME_SERVIZIO, nomeservizio),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_TIPO_SERVIZIO, tiposervizio),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_CORRELATO, correlato),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_ID_FRUITORE, fru.getId()+""),
							AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo)
							);
				de.setValue(tipofruit + "/" + nomefruit);
				e.addElement(de);

				if(this.core.isShowGestioneWorkflowStatoDocumenti()){
					de = new DataElement();
					de.setValue(fru.getStatoPackage());
					e.addElement(de);
				}

				if (!InterfaceType.STANDARD.equals(user.getInterfaceType())) {
					de = new DataElement();
					// de.setValue("non disp.");
					de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_SERVIZI_APPLICATIVI_LIST, 
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SERVIZI_APPLICATIVI_ID_SOGGETTO, sogg.getId()+""),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_NOME_SOGGETTO, nomeprov),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_TIPO_SOGGETTO, tipoprov),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_NOME_SERVIZIO, nomeservizio),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_TIPO_SERVIZIO, tiposervizio),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_CORRELATO, correlato),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_ID_FRUITORE, fru.getId()+""),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SERVIZI_APPLICATIVI_ID_SERVIZIO, serv.getId()+""),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SERVIZI_APPLICATIVI_ID_SOGGETTO_FRUITORE_SERVIZIO, idSoggettoFruitore+""),
							AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo));
					if (contaListe) {
						List<String> polSic = this.apsCore.getPoliticheSicurezza(serv.getId(), idSoggettoFruitore);
						int numPolitiche = polSic.size();
						ServletUtils.setDataElementVisualizzaLabel(de, new Long(numPolitiche));
					} else {
						ServletUtils.setDataElementVisualizzaLabel(de);
					}
					e.addElement(de);

					de = new DataElement();
					de.setValue(AccordiServizioParteComuneCostanti.LABEL_NON_DISPONIBILE);
					e.addElement(de);
				}

				dati.addElement(e);
			}

			this.pd.setDati(dati);
			this.pd.setSelect(false);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public void prepareAccordiErogatoriList(AccordoServizioParteComune as, List<org.openspcoop2.core.registry.Soggetto> lista, ISearch ricerca,String tipoAccordo)
			throws Exception {
		try {

			String id = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);

			String uri = null;
			uri = this.idAccordoFactory.getUriFromAccordo(as);

			ServletUtils.addListElementIntoSession(this.session, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_EROGATORI,
					new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id),
					new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO, tipoAccordo),
					new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, uri));


			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
			//String superUser = (String) this.session.getAttribute("Login");

			int idLista = Liste.ACCORDI_EROGATORI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			// setto la barra del titolo
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo),null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
										new Parameter(AccordiServizioParteComuneCostanti.LABEL_ACCORDI_EROGATORI_DI + uri, null)
						);
			}else{
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo),null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
										new Parameter(AccordiServizioParteComuneCostanti.LABEL_ACCORDI_EROGATORI_DI + " di " + uri, 
												AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_EROGATORI_LIST+"?"+
														AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID+"="+id+"&"+
														AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME+"="+uri+"&"+
														AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
														AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
														new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null)
						);
			}


			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, AccordiServizioParteComuneCostanti.LABEL_ACCORDI_EROGATORI, search);
			}

			// setto le label delle colonne
			// String[] labels = { "Soggetto", "Servizio",
			// "Accordo unilaterale", "Fruitori" };
			String[] labels = { AccordiServizioParteComuneCostanti.LABEL_SOGGETTO, 
					AccordiServizioParteComuneCostanti.LABEL_ACCORDO_SERVIZIO_PARTE_SPECIFICA, 
					AccordiServizioParteComuneCostanti.LABEL_FRUITORI};
			this.pd.setLabels(labels);

			// Prendo la lista di soggetti dell'utente connesso
			/*List<Long> idsSogg = new ArrayList<Long>();
					List<Soggetto> listaSog = this.core.soggettiRegistroList(superUser, new Search());
					Iterator<Soggetto> itS = listaSog.iterator();
					while (itS.hasNext()) {
						Soggetto ss = (Soggetto) itS.next();
						idsSogg.add(ss.getId());
					}*/

			// Prendo la lista di servizi dell'utente connesso
			/*List<Long> idsServ = new ArrayList<Long>();
					List<AccordoServizioParteSpecifica> listaServ = this.core.soggettiAccordoServizioParteSpecificaoList(superUser, new Search());
					Iterator<AccordoServizioParteSpecifica> itServ = listaServ.iterator();
					while (itServ.hasNext()) {
						AccordoServizioParteSpecifica ss = (AccordoServizioParteSpecifica) itServ.next();
						idsServ.add(ss.getId());
					}*/

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<org.openspcoop2.core.registry.Soggetto> it = lista.iterator();

				while (it.hasNext()) {
					org.openspcoop2.core.registry.Soggetto sog = it.next();

					// per ogni soggetto stampo i servizi
					ArrayList<AccordoServizioParteSpecifica> listaServizi = new ArrayList<AccordoServizioParteSpecifica>();
					// i servizi
					for (int i = 0; i < sog.sizeAccordoServizioParteSpecificaList(); i++) {
						listaServizi.add(sog.getAccordoServizioParteSpecifica(i));
					}

					// Nota: sog e' il soggetto erogatore
					// Non posso usare sog.getId per ottenere l'id
					// del soggetto
					IDSoggetto tmpIdSoggetto = new IDSoggetto(sog.getTipo(), sog.getNome());
					org.openspcoop2.core.registry.Soggetto tmpSog = null;
					try {
						tmpSog = this.soggettiCore.getSoggettoRegistro(tmpIdSoggetto);

					} catch (DriverRegistroServiziNotFound drsnf) {
						// ok
					}

					// creo la lista
					for (int i = 0; i < listaServizi.size(); i++) {

						AccordoServizioParteSpecifica asps = listaServizi.get(i);
						Servizio servizio = asps.getServizio();

						Vector<DataElement> e = new Vector<DataElement>();

						DataElement de = new DataElement();
						//if (idsSogg.contains(tmpSog.getId()))
						de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE, 
								new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID,tmpSog.getId()+""),
								new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME,sog.getNome()),
								new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO,sog.getTipo()));
						de.setValue(sog.getTipo() + "/" + sog.getNome());
						e.addElement(de);

						boolean isServizioCorrelato = TipologiaServizio.CORRELATO.equals(servizio.getTipologiaServizio());

						int idServ = asps.getId().intValue();
						de = new DataElement();
						//if (idsServ.contains(servizio.getId()))
						de.setUrl(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE,
								new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID,idServ+""),
								new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO,servizio.getNome()),
								new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO,servizio.getTipo()));
						de.setValue(servizio.getTipo() + "/" + servizio.getNome() + (isServizioCorrelato ? " ["+AccordiServizioParteSpecificaCostanti.LABEL_APS_CORRELATO+"]" : ""));
						e.addElement(de);

						de = new DataElement();
						Parameter pIdsoggErogatore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+sog.getId());
						Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, servizio.getNome());
						Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, servizio.getTipo());
						de.setUrl(
								AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST,
								new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId() + ""),
								pNomeServizio, pTipoServizio, pIdsoggErogatore);
						//						de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_EROGATORI_FRUITORI_CHANGE, 
						//								new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID,id),
						//								new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_NOME_SOGGETTO,sog.getNome()),
						//								new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_TIPO_SOGGETTO,sog.getTipo()),
						//								new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_NOME_SERVIZIO,servizio.getNome()),
						//								new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_TIPO_SERVIZIO,servizio.getTipo()),
						//								new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_NOME_ACCORDO,uri),
						//								new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_CORRELATO,isServizioCorrelato+""),
						//								AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo));

						if (contaListe) {
							List<Fruitore> lista1 = this.apcCore.accordiErogatoriFruitoriList(idServ, new Search(true));
							int numEr = lista1.size();
							ServletUtils.setDataElementVisualizzaLabel(de, new Long(numEr));
						} else
							ServletUtils.setDataElementVisualizzaLabel(de);
						e.addElement(de);

						dati.addElement(e);
					}// eo for servizi
				}// eo while soggetti

			}

			this.pd.setDati(dati);
			this.pd.setSelect(false);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public void prepareAccordiAllegatiList(AccordoServizioParteComune as, ISearch ricerca, List<Documento> lista,String tipoAccordo) throws Exception {
		try {

			String id = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);

			String uri = null;
			uri = this.idAccordoFactory.getUriFromAccordo(as);

			ServletUtils.addListElementIntoSession(this.session, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_ALLEGATI,
					new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id),
					new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO, tipoAccordo),
					new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, uri));

			int idLista = Liste.ACCORDI_ALLEGATI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));


			// setto la barra del titolo
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo),null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
										new Parameter(AccordiServizioParteComuneCostanti.LABEL_ALLEGATI + " di " + this.idAccordoFactory.getUriFromAccordo(as), null)
						);
			}else{
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo),null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
										new Parameter(AccordiServizioParteComuneCostanti.LABEL_ALLEGATI + " di " + uri, 
												AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_ALLEGATI_LIST+"?"+
														AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID+"="+id+"&"+
														AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME+"="+as.getNome()+"&"+
														AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
														AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
														new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null)
						);
			}


			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, AccordiServizioParteComuneCostanti.LABEL_ALLEGATI, search);
			}

			// setto le label delle colonne
			String[] labels = { AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_NOME, 
					AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_ALLEGATI_RUOLO, 
					AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_ALLEGATI_TIPO_FILE , 
					AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_ALLEGATI_DOCUMENTO };
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<Documento> it = lista.iterator();
				while (it.hasNext()) {
					Documento doc = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_ALLEGATI_CHANGE, 
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_ID_ALLEGATO, doc.getId()+""),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_ID_ACCORDO, as.getId()+""),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_NOME_DOCUMENTO, doc.getFile()),
							AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo));
					de.setValue(doc.getFile());
					de.setIdToRemove(""+doc.getId());
					e.addElement(de);

					de = new DataElement();
					de.setValue(doc.getRuolo());
					e.addElement(de);

					de = new DataElement();
					de.setValue(doc.getTipo());
					e.addElement(de);

					de = new DataElement();
					de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_ALLEGATI_VIEW, 
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_ID_ALLEGATO, doc.getId()+""),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_ID_ACCORDO, as.getId()+""),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_NOME_DOCUMENTO, doc.getFile()),
							AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo));
					de.setValue(Costanti.LABEL_VISUALIZZA);
					e.addElement(de);

					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);

			if(this.core.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(as.getStatoPackage())){
				this.pd.setAddButton(false);
				this.pd.setRemoveButton(false);
				this.pd.setSelect(false);
			}else{
				this.pd.setAddButton(true);
				this.pd.setRemoveButton(true);
				this.pd.setSelect(true);
			}

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}



	public Vector<Object> addAccordiAllegatiToDati(Vector<Object> dati,TipoOperazione tipoOperazione,String idAccordo,
			String ruolo,String [] ruoli,String []tipiAmmessi,String []tipiAmmessiLabel,String tipoAccordo,
			String idAllegato, Documento doc, AccordoServizioParteComune as, String errore,StringBuffer contenutoAllegato) throws Exception {
		try{


			if(TipoOperazione.ADD.equals(tipoOperazione)){
				DataElement de = new DataElement();
				de.setValue(idAccordo);
				de.setType(DataElementType.HIDDEN);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
				dati.addElement(de);
			}
			else {
				DataElement de = new DataElement();
				de.setValue(idAllegato);
				de.setType(DataElementType.HIDDEN);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_ID_ALLEGATO);
				dati.addElement(de);

				de = new DataElement();
				de.setValue(idAccordo);
				de.setType(DataElementType.HIDDEN);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_ID_ACCORDO);
				dati.addElement(de);
			}

			DataElement de = new DataElement();
			if(TipoOperazione.ADD.equals(tipoOperazione)){
				de.setValue(idAccordo);
				de.setType(DataElementType.SELECT);
				de.setSelected(ruolo!=null ? ruolo : "");
				de.setValues(ruoli);
			}else{
				de.setValue(doc.getRuolo());
				de.setType(DataElementType.TEXT);
			}
			de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_ALLEGATI_RUOLO);			
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_RUOLO);
			de.setPostBack(true);
			//			de.setOnChange("CambiaTipoDocumento('accordiAllegati')");
			de.setSize(this.getSize());
			dati.addElement(de);


			if(tipiAmmessi!=null){
				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_ALLEGATI_TIPO_FILE);
				de.setType(DataElementType.SELECT);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_TIPO_FILE);
				de.setValues(tipiAmmessi);
				de.setLabels(tipiAmmessiLabel);
				de.setSize(this.getSize());
				dati.addElement(de);
			}

			if(TipoOperazione.ADD.equals(tipoOperazione)==false){
				de = new DataElement();
				de.setValue(doc.getFile());
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_NOME);
				de.setType(DataElementType.TEXT);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_NOME_DOCUMENTO);
				de.setSize(this.getSize());
				dati.addElement(de);
			}

			if(TipoOperazione.ADD.equals(tipoOperazione)){
				de = new DataElement();
				de.setValue(idAccordo);
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_ALLEGATI_DOCUMENTO);
				de.setType(DataElementType.FILE);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_DOCUMENTO);
				de.setSize(this.getSize());
				dati.addElement(de);
			}

			if(TipoOperazione.ADD.equals(tipoOperazione)==false){
				de = new DataElement();
				de.setValue(doc.getTipo());
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_ALLEGATI_TIPO_FILE);
				de.setType(DataElementType.TEXT);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_TIPO_FILE);
				de.setSize(this.getSize());
				dati.addElement(de);
			}

			de = new DataElement();
			de.setValue(tipoAccordo);
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			dati.addElement(de);

			if(TipoOperazione.CHANGE.equals(tipoOperazione)){

				if(this.core.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(as.getStatoPackage())){
					this.pd.disableEditMode();
				}
				else{
					de = new DataElement();
					de.setType(DataElementType.FILE);
					de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_ALLEGATI_DOCUMENTO);
					de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_DOCUMENTO);
					de.setSize(this.getSize());
					dati.addElement(de);
				}
			}

			if(TipoOperazione.OTHER.equals(tipoOperazione)){

				if(errore!=null){
					de = new DataElement();
					de.setValue(errore);
					de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_ALLEGATI_DOCUMENTO);
					de.setType(DataElementType.TEXT);
					de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_DOCUMENTO_VIEW);
					de.setSize(this.getSize());
					dati.addElement(de);
				}
				else{
					de = new DataElement();
					de.setLabel("");//AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_ALLEGATI_DOCUMENTO);
					de.setType(DataElementType.TEXT_AREA_NO_EDIT);
					de.setValue(contenutoAllegato.toString());
					de.setRows(30);
					de.setCols(80);
					dati.addElement(de);
				}
				
				DataElement saveAs = new DataElement();
				saveAs.setValue(AccordiServizioParteComuneCostanti.LABEL_DOWNLOAD);
				saveAs.setType(DataElementType.LINK);
				saveAs.setUrl(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT, 
						new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_ID_ACCORDO, idAccordo),
						new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_ID_ALLEGATO, idAllegato),
						new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO, ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_PARTE_COMUNE));
				dati.add(saveAs);

				this.pd.disableEditMode();

			}

			return dati;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public void prepareAccordiPorttypeOperationsList(ISearch ricerca, List<Operation> lista, AccordoServizioParteComune as,String tipoAccordo,String nomePT) throws Exception {
		try {
			String id = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);

			String uri = null;
			uri = this.idAccordoFactory.getUriFromAccordo(as);

			ServletUtils.addListElementIntoSession(this.session, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_PORT_TYPE_OPERATIONS,
					new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id),
					new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO, tipoAccordo),
					new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, uri),
					new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME,nomePT));

			String nomept = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME);
			Hashtable<String, String> campiHidden = new Hashtable<String, String>();
			campiHidden.put("nomept", nomept);
			this.pd.setHidden(campiHidden);

			int idLista = Liste.ACCORDI_PORTTYPE_AZIONI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			// setto la barra del titolo			
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo),null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
										new Parameter(AccordiServizioParteComuneCostanti.LABEL_PORT_TYPES + " di " + uri, 
												AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPES_LIST+"?"+
														AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID+"="+id+"&"+
														AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME+"="+as.getNome()+"&"+
														AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
														AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
														new Parameter(AccordiServizioParteComuneCostanti.LABEL_AZIONI + " di " + nomept, null)
						);
			}else{
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo),null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
										new Parameter(AccordiServizioParteComuneCostanti.LABEL_PORT_TYPES + " di " + uri, 
												AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPES_LIST+"?"+
														AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID+"="+id+"&"+
														AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME+"="+as.getNome()+"&"+
														AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
														AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
														new Parameter(AccordiServizioParteComuneCostanti.LABEL_AZIONI + " di " + nomept, 
																AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_LIST+"?"+
																		AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID+"="+id+"&"+
																		AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME+"="+nomept+"&"+
																		AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
																		AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
																		new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null)
						);
			}



			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, AccordiServizioParteComuneCostanti.LABEL_AZIONI, search);
			}

			// setto le label delle colonne
			String[] labels = { AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_NOME };
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<Operation> it = lista.iterator();
				while (it.hasNext()) {
					Operation op = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_CHANGE,
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME, nomept),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_NOME, op.getNome()),
							AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo)
							);
					de.setValue(op.getNome());
					de.setIdToRemove(op.getNome());
					e.addElement(de);

					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);

			if(this.core.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(as.getStatoPackage())){
				this.pd.setAddButton(false);
				this.pd.setRemoveButton(false);
				this.pd.setSelect(false);
			}else{
				this.pd.setAddButton(true);
				this.pd.setRemoveButton(true);
				this.pd.setSelect(true);
			}

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public boolean accordiPorttypeOperationCheckData(TipoOperazione tipoOperazione)
			throws Exception {

		try{

			// String userLogin = (String) this.session.getAttribute("Login");

			String id = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			String nomept = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME);
			String nomeop = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_NOME);
			String profProtocollo = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_PROFILO_BUSTA);
			String filtrodupop = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_FILTRO_DUPLICATI);
			if ((filtrodupop != null) && filtrodupop.equals("null")) {
				filtrodupop = null;
			}
			String confricop = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_CONFERMA_RICEZIONE);
			if ((confricop != null) && confricop.equals("null")) {
				confricop = null;
			}
			String idcollop = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_COLLABORAZIONE);
			if ((idcollop != null) && idcollop.equals("null")) {
				idcollop = null;
			}
			String consordop = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_CONSEGNA_ORDINE);
			if ((consordop != null) && consordop.equals("null")) {
				consordop = null;
			}
			String scadenzaop = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_SCADENZA);
			String servcorr = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_SERVIZIO_CORRELATO);
			String azicorr = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_AZIONE_CORRELATA);
			if (servcorr == null || servcorr.equals(""))
				servcorr = "-";
			if (azicorr == null || azicorr.equals(""))
				azicorr = "-";

			String profcollop = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_PROFILO_COLLABORAZIONE);
			if(profcollop == null)
				profcollop = "";
			//parametri WSDL
			String styleOp = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_STYLE);
			if(styleOp == null)
				styleOp = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_STYLE;
			String soapActionOp = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_SOAP_ACTION);
			if(soapActionOp == null)
				soapActionOp = "";
			String useOp = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_USE);
			if(useOp == null)
				useOp = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_USE;
			String opTypeOp = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE);
			String nsWSDLOp = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_NS_WSDL);
			if(nsWSDLOp == null)
				nsWSDLOp = "";

			// Campi obbligatori
			if (nomeop.equals("")) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare un Nome");
				return false;
			}

			// Se isShowCorrelazioneAsincronaInAccordi ed è
			// stato specificato un servcorr, azicorr non può
			// essere "-"
			if (this.core.isShowCorrelazioneAsincronaInAccordi()) {
				if (!servcorr.equals("-") && azicorr.equals("-")) {
					this.pd.setMessage("Indicare un'azione correlata");
					return false;
				}
			}

			// Il nome deve contenere solo lettere e numeri e '_' '-' '.'
			//if (!this.procToCall.isOk("^[0-9A-Za-z_\\-\\.]+$", nomeop)) {
			if (!RegularExpressionEngine.isMatch(nomeop,"^[_A-Za-z][\\-\\._A-Za-z0-9]*$")) {
				this.pd.setMessage("Il nome dell'azione puo' iniziare solo con un carattere [A-Za-z] o il simbolo '_' e dev'essere formato solo da caratteri, cifre, '_' , '-' e '.'");
				return false;
			}

			// Controllo che i campi "select" abbiano uno dei valori ammessi
			if (!profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT) && !profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO)) {
				this.pd.setMessage("Il profilo dev'essere \""+AccordiServizioParteComuneCostanti.LABEL_INFORMAZIONI_PROTOCOLLO_PORT_TYPE[0]+"\" o \""+
						AccordiServizioParteComuneCostanti.LABEL_INFORMAZIONI_PROTOCOLLO_PORT_TYPE[1]+"\"");
				return false;
			}

			// profilo : default || ridefinisci
			if (AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO.equals(profProtocollo)) {
				// Controllo che i campi "checkbox" abbiano uno dei valori ammessi
				if ((filtrodupop != null) && !filtrodupop.equals(Costanti.CHECK_BOX_ENABLED) && !filtrodupop.equals(CostantiRegistroServizi.ABILITATO) && !filtrodupop.equals(Costanti.CHECK_BOX_DISABLED) && !filtrodupop.equals(CostantiRegistroServizi.DISABILITATO)) {
					this.pd.setMessage("Filtro duplicati dev'essere selezionato o deselezionato");
					return false;
				}
				if ((confricop != null) && !confricop.equals(Costanti.CHECK_BOX_ENABLED) && !confricop.equals(CostantiRegistroServizi.ABILITATO) && !confricop.equals(Costanti.CHECK_BOX_DISABLED) && !confricop.equals(CostantiRegistroServizi.DISABILITATO)) {
					this.pd.setMessage("Conferma Ricezione dev'essere selezionata o deselezionata");
					return false;
				}
				if ((idcollop != null) && !idcollop.equals(Costanti.CHECK_BOX_ENABLED) && !idcollop.equals(CostantiRegistroServizi.ABILITATO) && !idcollop.equals(Costanti.CHECK_BOX_DISABLED) && !idcollop.equals(CostantiRegistroServizi.DISABILITATO)) {
					this.pd.setMessage("ID Collaborazione dev'essere selezionata o deselezionata");
					return false;
				}
				if ((consordop != null) && !consordop.equals(Costanti.CHECK_BOX_ENABLED) && !consordop.equals(CostantiRegistroServizi.ABILITATO) && !consordop.equals(Costanti.CHECK_BOX_DISABLED) && !consordop.equals(CostantiRegistroServizi.DISABILITATO)) {
					this.pd.setMessage("Consegna in ordine dev'essere selezionata o deselezionata");
					return false;
				}

				// scadenzapt dev'essere numerico
				if (!scadenzaop.equals("") && !RegularExpressionEngine.isMatch(scadenzaop,"^[0-9]+$")) {
					this.pd.setMessage("Scadenza dev'essere numerico");
					return false;
				}
			}

			AccordoServizioParteComune as = this.apcCore.getAccordoServizio(Integer.parseInt(id));
			PortType pt = null;
			for (int i = 0; i < as.sizePortTypeList(); i++) {
				pt =as.getPortType(i);
				if (nomept.equals(pt.getNome()))
					break;
			}

			// Se tipoOp = add, controllo che l'operation non sia gia' stato
			// registrato per il port-type
			if (tipoOperazione.equals(TipoOperazione.ADD)) {
				boolean giaRegistrato = this.apcCore.existsAccordoServizioPorttypeOperation(nomeop, pt.getId());
				if (giaRegistrato) {
					this.pd.setMessage("L'azione " + nomeop + " &egrave; gi&agrave; stata associata al servizio");
					return false;
				}
			}

			// Controllo che l'azione possieda SOLO azioni correlate o solo azioni NON correlate
			if(this.core.isShowCorrelazioneAsincronaInAccordi() && pt!=null){
				for (int i = 0; i < pt.sizeAzioneList(); i++) {
					Operation op = pt.getAzione(i);
					if (tipoOperazione.equals(TipoOperazione.CHANGE)) {
						if(nomeop.equals(op.getNome()))
							continue;
					}
					if(servcorr!=null && !"".equals(servcorr) && !"-".equals(servcorr) && azicorr!=null && !"".equals(azicorr) && !"-".equals(azicorr)){
						// controllo che il port type non possieda gia azioni non correlate
						if(op.getCorrelata()==null){
							this.pd.setMessage("L'azione " + nomeop + ", essendo correlata ad un'altra azione ("+azicorr+") di un altro servizio ("+servcorr+"), non pu&ograve; essere aggiunta ad un servizio che possiede azioni che non risultano correlate verso altri servizi asincroni");
							return false;
						}
					}
					else{
						// controllo che il port type non possieda gia azioni correlate ad altri servizi
						if(op.getCorrelataServizio()!=null && !pt.getNome().equals(op.getCorrelataServizio())){
							this.pd.setMessage("L'azione " + nomeop + " non pu&ograve; essere aggiunta poich&egrave; il servizio "+pt.getNome()+" possiede azioni che risultano correlate in profili asincroni (correlazione verso altri servizi)");
							return false;
						}
					}
				}
			}


			// Informazioni Wsdl
			// se il profilo dell'azione e' oneway operation type puo' essere solo input
			if(profcollop.equals(CostantiRegistroServizi.ONEWAY.getValue()) && 
					opTypeOp!=null && !opTypeOp.equals(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_IN)){
				this.pd.setMessage("L'operationType di tipo " + opTypeOp + " non pu&ograve; essere utilizzata con l'azione "+nomeop+" poich&egrave; possiede un profilo "+profcollop);
				return false;		
			}

			if(profcollop.equals(CostantiRegistroServizi.SINCRONO.getValue()) && 
					opTypeOp!=null && !opTypeOp.equals(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_INOUT)){
				this.pd.setMessage("L'operationType di tipo " + opTypeOp + " non pu&ograve; essere utilizzata con l'azione "+nomeop+" poich&egrave; possiede un profilo "+profcollop);
				return false;		
			}

			// contenuto del campo namespace opzionale
			if(nsWSDLOp != null  && !nsWSDLOp.equals("")){
				if(nsWSDLOp.indexOf(" ") != -1){
					this.pd.setMessage("Il campo Namespace non pu&ograve; contenere spazi");
					return false; 
				}

				try{
					URI.create(nsWSDLOp);
				}catch(Exception e){
					this.pd.setMessage("Il campo Namespace non contiene una URI valida");
					return false;
				}
			}

			if(soapActionOp != null && !soapActionOp.equals("")){
				if(soapActionOp.indexOf(" ") != -1){
					this.pd.setMessage("Il campo SOAPAction non pu&ograve; contenere spazi");
					return false; 
				}
			}

			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public Vector<Object> addAccordiPorttypeOperationToDati(Vector<Object> dati, String id, String nomept, String nomeop, String profProtocollo, 
			String filtrodupop, String deffiltrodupop, String confricop, String defconfricop, String idcollop, String defidcollop, String consordop, String defconsordop, 
			String scadenzaop, String defscadenzaop, TipoOperazione tipoOperazione, String defProfiloCollaborazioneOp, String profiloCollaborazioneOp, 
			String opcorr, String[] opList, String stato,String tipoSICA, String[] servCorrList, String servcorr, String[] aziCorrList, String azicorr
			,String protocollo,
			String soapActionOp, String styleOp, String useOp, String nsWSDLOp, 
			String operationTypeOp, int messageInputCnt, int messageOutputCnt)
					throws Exception {
		try {
			boolean modificheAbilitate = false;
			if( tipoOperazione.equals(TipoOperazione.ADD) ){
				modificheAbilitate = true;
			}else if(this.core.isShowGestioneWorkflowStatoDocumenti()==false){
				modificheAbilitate = true;
			}else if(StatiAccordo.finale.toString().equals(stato)==false){
				modificheAbilitate = true;
			}

			DataElement de = new DataElement();
			de.setValue(tipoSICA);
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			dati.addElement(de);

			de = new DataElement();
			de.setValue(id);
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			dati.addElement(de);

			de = new DataElement();
			de.setValue(nomept);
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME);
			de.setValue(nomeop);
			if (tipoOperazione.equals(TipoOperazione.ADD)) {
				de.setType(DataElementType.TEXT_EDIT);
				de.setRequired(true);
			} else {
				de.setType(DataElementType.TEXT);
			}
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_NOME);
			de.setSize(this.getSize());
			dati.addElement(de);

			Vector<DataElement> dataElements = new Vector<DataElement>();
			if (this.core.isShowCorrelazioneAsincronaInAccordi()) {
				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_CORRELATA_AL_SERVIZIO);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_SERVIZIO_CORRELATO);
				if ((servCorrList != null) && ((profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT) && (defProfiloCollaborazioneOp.equals(AccordiServizioParteComuneCostanti.TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO) || defProfiloCollaborazioneOp.equals(AccordiServizioParteComuneCostanti.TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO))) || (profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO) && (profiloCollaborazioneOp.equals(AccordiServizioParteComuneCostanti.TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO) || profiloCollaborazioneOp.equals(AccordiServizioParteComuneCostanti.TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO))))) {
					de.setType(DataElementType.SELECT);
					de.setValues(servCorrList);
					de.setSelected(servcorr);
					//							de.setOnChange("CambiaServCorr('" + tipoOp + "')");
					de.setPostBack(true);
					dataElements.add(de);
				} else {
					de.setType(DataElementType.HIDDEN);
					de.setValue(servcorr);
					dati.addElement(de);
				}

				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_CORRELATA_A_AZIONE);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_AZIONE_CORRELATA);
				if ((aziCorrList != null) && ((profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT) && (defProfiloCollaborazioneOp.equals(AccordiServizioParteComuneCostanti.TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO) || defProfiloCollaborazioneOp.equals(AccordiServizioParteComuneCostanti.TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO))) || (profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO) && (profiloCollaborazioneOp.equals(AccordiServizioParteComuneCostanti.TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO) || profiloCollaborazioneOp.equals(AccordiServizioParteComuneCostanti.TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO))))) {
					de.setType(DataElementType.SELECT);
					de.setValues(aziCorrList);
					de.setSelected(azicorr);
					if(servcorr!=null && !"".equals(servcorr) && !"-".equals(servcorr)){
						if(modificheAbilitate)
							de.setRequired(true);
					}
					dataElements.add(de);
				} else {
					de.setType(DataElementType.HIDDEN);
					de.setValue(azicorr);
					dati.addElement(de);
				}
			} else {
				// boolean isOpCorr = false;
				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_AZIONE_CORRELATA);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_CORRELATA);
				if ((opList != null) && ((profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT) && defProfiloCollaborazioneOp.equals(AccordiServizioParteComuneCostanti.TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO)) || (profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO) && profiloCollaborazioneOp.equals(AccordiServizioParteComuneCostanti.TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO)))) {
					// isOpCorr = true;
					de.setType(DataElementType.SELECT);
					de.setValues(opList);
					de.setSelected(opcorr);
					dataElements.add(de);
				} else {
					de.setType(DataElementType.HIDDEN);
					de.setValue(opcorr);
					dati.addElement(de);
				}
			}

			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(AccordiServizioParteComuneCostanti.LABEL_INFORMAZIONI);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PROFILO_PROTOCOLLO);
			de.setType(DataElementType.SELECT);
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_PROFILO_BUSTA);
			if(modificheAbilitate){
				de.setValues(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA);
				de.setLabels(AccordiServizioParteComuneCostanti.LABEL_INFORMAZIONI_PROTOCOLLO_PORT_TYPE);
				de.setSelected(profProtocollo);
				//						de.setOnChange("CambiaProfOp('" + tipoOp + "')");
				de.setPostBack(true);
			}else{
				de.setType(DataElementType.TEXT);
				if(profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT)){
					de.setValue(AccordiServizioParteComuneCostanti.LABEL_INFORMAZIONI_PROTOCOLLO_DEFAULT_PORT_TYPE);
				}else{
					de.setValue(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO);
				}
			}
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_PROFILO_COLLABORAZIONE);
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_PROFILO_COLLABORAZIONE);
			if (profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT)) {
				de.setType(DataElementType.HIDDEN);
				de.setValue(defProfiloCollaborazioneOp);
			} else {
				de.setType(DataElementType.SELECT);
				de.setValues(this.core.getProfiliDiCollaborazioneSupportatiDalProtocollo(protocollo));
				// de.setLabels(tipoProfcollLabel);
				de.setSelected(profiloCollaborazioneOp);
				//						de.setOnChange("CambiaProfOp('" + tipoOp + "', true)");
				de.setPostBack(true);
			}
			dati.addElement(de);

			User user = ServletUtils.getUserFromSession(this.session);
			InterfaceType gui = user.getInterfaceType();

			de = new DataElement();
			de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_FILTRO_DUPLICATI);
			if (profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT)) {
				de.setType(DataElementType.HIDDEN);
				de.setValue(deffiltrodupop);
			} else if (InterfaceType.STANDARD.equals(gui) && AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO.equals(profProtocollo)) {
				de.setType(DataElementType.HIDDEN);
				de.setValue(Costanti.CHECK_BOX_ENABLED);
			} else if (InterfaceType.AVANZATA.equals(gui) &&
					AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO.equals(profProtocollo) &&
					!this.core.isFunzionalitaProtocolloSupportataDalProtocollo(protocollo, FunzionalitaProtocollo.FILTRO_DUPLICATI)) {
				de.setType(DataElementType.HIDDEN);
				de.setValue(Costanti.CHECK_BOX_ENABLED);
			}  else {
				if(modificheAbilitate){
					de.setType(DataElementType.CHECKBOX);
					if(ServletUtils.isCheckBoxEnabled(filtrodupop)){
						de.setSelected(true);
					}
				}else{
					de.setType(DataElementType.TEXT);
					if(ServletUtils.isCheckBoxEnabled(filtrodupop)){
						de.setValue(CostantiRegistroServizi.ABILITATO.toString());
					}else{
						de.setValue(CostantiRegistroServizi.DISABILITATO.toString());
					}
				}	
			}
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_FILTRO_DUPLICATI);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_CONFERMA_RICEZIONE);
			if (profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT)) {
				de.setType(DataElementType.HIDDEN);
				de.setValue(defconfricop);
			} else if (InterfaceType.STANDARD.equals(gui) && AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO.equals(profProtocollo)) {
				de.setType(DataElementType.HIDDEN);
				de.setValue(Costanti.CHECK_BOX_DISABLED);
			}else if (InterfaceType.AVANZATA.equals(gui) &&
					AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO.equals(profProtocollo) &&
					!this.core.isFunzionalitaProtocolloSupportataDalProtocollo(protocollo, FunzionalitaProtocollo.CONFERMA_RICEZIONE)) {
				de.setType(DataElementType.HIDDEN);
				de.setValue(Costanti.CHECK_BOX_DISABLED);
			}  else {
				if(modificheAbilitate){
					de.setType(DataElementType.CHECKBOX);
					if(ServletUtils.isCheckBoxEnabled(confricop)){
						de.setSelected(true);
					}
				}else{
					de.setType(DataElementType.TEXT);
					if(ServletUtils.isCheckBoxEnabled(confricop)){
						de.setValue(CostantiRegistroServizi.ABILITATO.toString());
					}else{
						de.setValue(CostantiRegistroServizi.DISABILITATO.toString());
					}
				}	
			}
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_CONFERMA_RICEZIONE);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_COLLABORAZIONE);
			if (profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT)) {
				de.setType(DataElementType.HIDDEN);
				de.setValue(defidcollop);
			} else if (InterfaceType.STANDARD.equals(gui) && AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO.equals(profProtocollo)) {
				de.setType(DataElementType.HIDDEN);
				de.setValue(Costanti.CHECK_BOX_DISABLED);
			}else if (InterfaceType.AVANZATA.equals(gui) &&
					AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO.equals(profProtocollo) &&
					!this.core.isFunzionalitaProtocolloSupportataDalProtocollo(protocollo, FunzionalitaProtocollo.COLLABORAZIONE)) {
				de.setType(DataElementType.HIDDEN);
				de.setValue(Costanti.CHECK_BOX_DISABLED);
			} else {
				if(modificheAbilitate){
					de.setType(DataElementType.CHECKBOX);
					if(ServletUtils.isCheckBoxEnabled(idcollop)){
						de.setSelected(true);
					}
				}else{
					de.setType(DataElementType.TEXT);
					if(ServletUtils.isCheckBoxEnabled(idcollop)){
						de.setValue(CostantiRegistroServizi.ABILITATO.toString());
					}else{
						de.setValue(CostantiRegistroServizi.DISABILITATO.toString());
					}
				}	
			}
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_COLLABORAZIONE);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_CONSEGNA_ORDINE);
			if (profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT)) {
				de.setType(DataElementType.HIDDEN);
				de.setValue(defconsordop);
			} else if (InterfaceType.STANDARD.equals(gui) && AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO.equals(profProtocollo)) {
				de.setType(DataElementType.HIDDEN);
				de.setValue(Costanti.CHECK_BOX_DISABLED);
			} else if (InterfaceType.AVANZATA.equals(gui) &&
					AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO.equals(profProtocollo) &&
					!this.core.isFunzionalitaProtocolloSupportataDalProtocollo(protocollo, FunzionalitaProtocollo.CONSEGNA_IN_ORDINE)) {
				de.setType(DataElementType.HIDDEN);
				de.setValue(Costanti.CHECK_BOX_DISABLED);
			}else {
				if(modificheAbilitate){
					de.setType(DataElementType.CHECKBOX);
					if(ServletUtils.isCheckBoxEnabled(consordop)){
						de.setSelected(true);
					}
				}else{
					de.setType(DataElementType.TEXT);
					if(ServletUtils.isCheckBoxEnabled(consordop)){
						de.setValue(CostantiRegistroServizi.ABILITATO.toString());
					}else{
						de.setValue(CostantiRegistroServizi.DISABILITATO.toString());
					}
				}	
			}
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_CONSEGNA_ORDINE);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_SCADENZA);
			de.setValue(scadenzaop);
			if (profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT)) {
				de.setType(DataElementType.HIDDEN);
				de.setValue(defscadenzaop);
			}  else if (InterfaceType.STANDARD.equals(gui) &&
					AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO.equals(profProtocollo)) {
				de.setType(DataElementType.HIDDEN);
				de.setValue(defscadenzaop);
			}else if (InterfaceType.AVANZATA.equals(gui) &&
					AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO.equals(profProtocollo) &&
					!this.core.isFunzionalitaProtocolloSupportataDalProtocollo(protocollo, FunzionalitaProtocollo.SCADENZA)) {
				de.setType(DataElementType.HIDDEN);
				de.setValue(defscadenzaop);
			} else {
				de.setType(DataElementType.TEXT_EDIT);
				de.setValue(scadenzaop);
				if( !modificheAbilitate && (scadenzaop==null || "".equals(scadenzaop)) )
					de.setValue(" ");
			}
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_SCADENZA);
			de.setSize(this.getSize());
			dati.addElement(de);

			if(dataElements.size()>0){
				de = new DataElement();
				de.setType(DataElementType.TITLE);
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_CORRELAZIONE_ASINCRONA);
				dati.addElement(de);

				while(dataElements.size()>0){
					dati.addElement(dataElements.remove(0));
				}
			}

			boolean isStrutturaMSG = this.core.isShowAccordoParteComuneInformazioniStrutturaMessaggiWsdl();

			if(InterfaceType.AVANZATA.equals(gui) && isStrutturaMSG){
				de = new DataElement();
				de.setType(DataElementType.TITLE);
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_INFORMAZIONI_WSDL);
				dati.addElement(de);

				// SOAP Action
				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_SOAP_ACTION);
				de.setValue(soapActionOp);
				de.setType(DataElementType.TEXT_EDIT);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_SOAP_ACTION);
				de.setSize(this.getSize());
				dati.addElement(de);

				// Style
				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_STYLE);
				de.setType(DataElementType.SELECT);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_STYLE);
				de.setValues(AccordiServizioParteComuneCostanti.PORT_TYPES_OPERATION_STYLE);
				de.setLabels(AccordiServizioParteComuneCostanti.LABEL_PORT_TYPES_OPERATION_STYLE);
				de.setSelected(styleOp);
				dati.addElement(de);

				// Use
				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_USE);
				de.setType(DataElementType.SELECT);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_USE);
				de.setValues(AccordiServizioParteComuneCostanti.PORT_TYPES_OPERATION_USE);
				de.setLabels(AccordiServizioParteComuneCostanti.LABEL_PORT_TYPES_OPERATION_USE);
				de.setSelected(useOp);
				dati.addElement(de);

				// Namesapce WSDL
				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_NS_WSDL);
				de.setValue(nsWSDLOp);
				de.setType(DataElementType.TEXT_EDIT);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_NS_WSDL);
				de.setSize(this.getSize());
				dati.addElement(de);

				// Operation Type
				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE);
				if(!profiloCollaborazioneOp.equalsIgnoreCase(CostantiRegistroServizi.ONEWAY.getValue()) && !profiloCollaborazioneOp.equalsIgnoreCase(CostantiRegistroServizi.SINCRONO.getValue())){
					de.setType(DataElementType.SELECT);
					de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE);
					de.setValues(AccordiServizioParteComuneCostanti.PORT_TYPE_OPERATION_TYPE);
					de.setLabels(AccordiServizioParteComuneCostanti.LABEL_PORT_TYPE_OPERATION_TYPE);
					de.setSelected(operationTypeOp);
					de.setPostBack(true);
				} else{
					// operation type hidden
					de.setType(DataElementType.HIDDEN);
					de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE);
					de.setValue(operationTypeOp);
					dati.addElement(de);

					// operation type label
					de = new DataElement();
					de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE);
					de.setType(DataElementType.TEXT);
					de.setValue(operationTypeOp);
				}
				dati.addElement(de);

				if(tipoOperazione.equals(TipoOperazione.CHANGE)){
					// messageInput
					de = new DataElement();
					de.setType(DataElementType.LINK);
					de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE_LIST,
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO, tipoSICA),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME, nomept),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_NOME, nomeop),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_TYPE, AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_INPUT )
							);

					ServletUtils.setDataElementCustomLabel(de, AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_INPUT, (long) messageInputCnt);
					dati.addElement(de); 

					if(operationTypeOp.equals(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_OPERATION_TYPE_INOUT)){
						// messageInput
						de = new DataElement();
						de.setType(DataElementType.LINK);
						de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE_LIST,
								new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id),
								new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO, tipoSICA),
								new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME, nomept),
								new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_NOME, nomeop),
								new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_TYPE,
										AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_OUTPUT )
								);

						ServletUtils.setDataElementCustomLabel(de, AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_OUTPUT, (long) messageOutputCnt);
						dati.addElement(de);
					}

				}
			}

			return dati;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public void prepareAccordiPorttypeList(AccordoServizioParteComune as, List<org.openspcoop2.core.registry.PortType> lista, ISearch ricerca, String tipoAccordo)
			throws Exception {
		try {
			String id = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);

			String uri = null;
			uri = this.idAccordoFactory.getUriFromAccordo(as);

			ServletUtils.addListElementIntoSession(this.session, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_PORT_TYPES,
					new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id),
					new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO, tipoAccordo),
					new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, uri));

			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

			int idLista = Liste.ACCORDI_PORTTYPE;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			// setto la barra del titolo
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo),null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
										new Parameter(AccordiServizioParteComuneCostanti.LABEL_PORT_TYPES + " di " + uri, null)
						);
			}else{
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo),null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
										new Parameter(AccordiServizioParteComuneCostanti.LABEL_PORT_TYPES + " di " + uri, 
												AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPES_LIST+"?"+
														AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID+"="+id+"&"+
														AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME+"="+uri+"&"+
														AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
														AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
														new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null)
						);
			}

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, AccordiServizioParteComuneCostanti.LABEL_PORT_TYPES, search);
				this.pd.setSearch("on");
			}

			// setto le label delle colonne
			// String[] labels = { "Soggetto", "Servizio",
			// "Accordo unilaterale", "Fruitori" };
			String[] labels = {AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_NOME,AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_DESCRIZIONE , 
					AccordiServizioParteComuneCostanti.LABEL_AZIONI };
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<org.openspcoop2.core.registry.PortType> it = lista.iterator();

				while (it.hasNext()) {
					org.openspcoop2.core.registry.PortType pt = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPES_CHANGE, 
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME, pt.getNome()),
							AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo)
							);
					de.setValue(pt.getNome());
					de.setIdToRemove(pt.getNome());
					e.addElement(de);

					de = new DataElement();
					de.setValue(pt.getDescrizione());
					e.addElement(de);

					de = new DataElement();
					de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_LIST, 
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME, pt.getNome()),
							AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo)
							);
					if (contaListe) {
						// Prendo l'id del port-type
						int idPortType = 0;
						for (int i = 0; i < as.sizePortTypeList(); i++) {
							PortType pt1 = as.getPortType(i);
							if ((pt.getNome()).equals(pt1.getNome())) {
								idPortType = pt1.getId().intValue();
								break;
							}
						}
						List<Operation> tmpLista = this.apcCore.accordiPorttypeOperationList(idPortType, new Search(true));
						ServletUtils.setDataElementVisualizzaLabel(de, new Long(tmpLista.size()));
					} else
						ServletUtils.setDataElementVisualizzaLabel(de);
					e.addElement(de);

					dati.addElement(e);
				}

			}

			this.pd.setDati(dati);

			if(this.core.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(as.getStatoPackage())){
				this.pd.setAddButton(false);
				this.pd.setRemoveButton(false);
				this.pd.setSelect(false);
			}else{
				this.pd.setAddButton(true);
				this.pd.setRemoveButton(true);
				this.pd.setSelect(true);
			}

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Controlla i dati del porttype dell'accordo
	public boolean accordiPorttypeCheckData(TipoOperazione tipoOperazione) throws Exception {

		try{
			// String userLogin = (String) this.session.getAttribute("Login");

			String id = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			String nomept = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME);
			String profProtocollo = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_PROFILO_BUSTA);
			String filtroduppt = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_FILTRO_DUPLICATI);
			if ((filtroduppt != null) && filtroduppt.equals("null")) {
				filtroduppt = null;
			}
			String confricpt = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_CONFERMA_RICEZIONE);
			if ((confricpt != null) && confricpt.equals("null")) {
				confricpt = null;
			}
			String idcollpt = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_COLLABORAZIONE);
			if ((idcollpt != null) && idcollpt.equals("null")) {
				idcollpt = null;
			}
			String consordpt = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_CONSEGNA_ORDINE);
			if ((consordpt != null) && consordpt.equals("null")) {
				consordpt = null;
			}
			String scadenzapt = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_SCADENZA);

			// Campi obbligatori
			if (nomept.equals("")) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare un Nome");
				return false;
			}

			// Il nome deve contenere solo lettere e numeri e '_' '-' '.'
			//if (!this.procToCall.isOk("^[0-9A-Za-z_\\-\\.]+$", nomept)) {
			if (!RegularExpressionEngine.isMatch(nomept,"^[_A-Za-z][\\-\\._A-Za-z0-9]*$")) {
				this.pd.setMessage("Il nome del servizio puo' iniziare solo con un carattere [A-Za-z] o il simbolo '_' e dev'essere formato solo da caratteri, cifre, '_' , '-' e '.'");
				return false;
			}

			// Controllo che i campi DataElementType.SELECT abbiano uno dei valori ammessi
			if (!profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT) && !profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO)) {
				this.pd.setMessage("Il profilo dev'essere \""+AccordiServizioParteComuneCostanti.LABEL_INFORMAZIONI_PROTOCOLLO_ACCORDO[0]
						+"\" o \""+AccordiServizioParteComuneCostanti.LABEL_INFORMAZIONI_PROTOCOLLO_ACCORDO[1]+"\"");
				return false;
			}

			if (AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO.equals(profProtocollo)) {
				// Controllo che i campi DataElementType.CHECKBOX abbiano uno dei valori ammessi
				if ((filtroduppt != null) && !filtroduppt.equals(Costanti.CHECK_BOX_ENABLED) && !filtroduppt.equals(CostantiRegistroServizi.ABILITATO) && !filtroduppt.equals(Costanti.CHECK_BOX_DISABLED) && !filtroduppt.equals(CostantiRegistroServizi.DISABILITATO)) {
					this.pd.setMessage(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_FILTRO_DUPLICATI+" dev'essere selezionato o deselezionato");
					return false;
				}
				if ((confricpt != null) && !confricpt.equals(Costanti.CHECK_BOX_ENABLED) && !confricpt.equals(CostantiRegistroServizi.ABILITATO) && !confricpt.equals(Costanti.CHECK_BOX_DISABLED) && !confricpt.equals(CostantiRegistroServizi.DISABILITATO)) {
					this.pd.setMessage(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_CONFERMA_RICEZIONE+" dev'essere selezionata o deselezionata");
					return false;
				}
				if ((idcollpt != null) && !idcollpt.equals(Costanti.CHECK_BOX_ENABLED) && !idcollpt.equals(CostantiRegistroServizi.ABILITATO) && !idcollpt.equals(Costanti.CHECK_BOX_DISABLED) && !idcollpt.equals(CostantiRegistroServizi.DISABILITATO)) {
					this.pd.setMessage(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_COLLABORAZIONE+" dev'essere selezionata o deselezionata");
					return false;
				}
				if ((consordpt != null) && !consordpt.equals(Costanti.CHECK_BOX_ENABLED) && !consordpt.equals(CostantiRegistroServizi.ABILITATO) && !consordpt.equals(Costanti.CHECK_BOX_DISABLED) && !consordpt.equals(CostantiRegistroServizi.DISABILITATO)) {
					this.pd.setMessage(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_CONSEGNA_ORDINE+" dev'essere selezionata o deselezionata");
					return false;
				}

				// scadenzapt dev'essere numerico
				if (!scadenzapt.equals("") && !RegularExpressionEngine.isMatch(scadenzapt,"^[0-9]+$")) {
					this.pd.setMessage(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_SCADENZA+" dev'essere numerico");
					return false;
				}
			}

			// Se tipoOp = add, controllo che il porttype non sia gia' stato
			// registrato per l'accordo
			if (tipoOperazione.equals(TipoOperazione.ADD)) {
				boolean giaRegistrato = this.apcCore.existsAccordoServizioPorttype(nomept, Integer.parseInt(id));
				if (giaRegistrato) {
					this.pd.setMessage("Il servizio " + nomept + " &egrave; gi&agrave; stato associato all'accordo");
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public Vector<Object> addAccordiPorttypeToDati(Vector<Object> dati, String id, String nomept, String profProtocollo, 
			String filtroduppt, String deffiltroduppt, String confricpt, String defconfricpt, String idcollpt, String defidcollpt, 
			String consordpt, String defconsordpt, String scadenzapt, String defscadenzapt, 
			TipoOperazione tipoOperazione, String defProfiloCollaborazionePT, String profiloCollaborazionePT, 
			String descr, String stato, String tipoAccordo,String protocollo,String servizioStyle)
					throws Exception {
		try {
			boolean modificheAbilitate = false;
			if( tipoOperazione.equals(TipoOperazione.ADD) ){
				modificheAbilitate = true;
			}else if(this.core.isShowGestioneWorkflowStatoDocumenti()==false){
				modificheAbilitate = true;
			}else if(StatiAccordo.finale.toString().equals(stato)==false){
				modificheAbilitate = true;
			}

			DataElement de = new DataElement();
			de.setValue(id);
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			dati.addElement(de);

			de = new DataElement();
			de.setValue(tipoAccordo);
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_NOME);
			de.setValue(nomept);
			if (tipoOperazione.equals(TipoOperazione.ADD)) {
				de.setType(DataElementType.TEXT_EDIT);
				de.setRequired(true);
			} else {
				de.setType(DataElementType.TEXT);
			}
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME);
			de.setSize(this.getSize());
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_DESCRIZIONE);
			de.setValue(descr);
			de.setType(DataElementType.TEXT_EDIT);
			if( !modificheAbilitate && (descr==null || "".equals(descr)) )
				de.setValue(" ");
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_DESCRIZIONE);
			de.setSize(this.getSize());
			dati.addElement(de);

			User user = ServletUtils.getUserFromSession(this.session);
			InterfaceType gui = user.getInterfaceType();

			if (InterfaceType.STANDARD.equals(gui)) {
				profProtocollo = AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO;
			}




			if (InterfaceType.STANDARD.equals(gui)) {

				de = new DataElement();
				de.setType(DataElementType.TITLE);
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_INFORMAZIONI);
				dati.addElement(de);


				de = new DataElement();
				de.setType(DataElementType.HIDDEN);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_PROFILO_BUSTA);
				de.setValue(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO);
				dati.addElement(de);


				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_PROFILO_COLLABORAZIONE);
				de.setType(DataElementType.SELECT);
				de.setValues(this.core.getProfiliDiCollaborazioneSupportatiDalProtocollo(protocollo));
				// de.setLabels(tipoProfcollLabel);
				de.setSelected(profiloCollaborazionePT);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_PROFILO_COLLABORAZIONE);
				dati.addElement(de);

				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_FILTRO_DUPLICATI);
				de.setType(DataElementType.HIDDEN);
				if(ServletUtils.isCheckBoxEnabled(filtroduppt) || CostantiRegistroServizi.ABILITATO.equals(filtroduppt)){
					de.setValue(Costanti.CHECK_BOX_ENABLED);
				}else {
					de.setValue(Costanti.CHECK_BOX_DISABLED);
				}
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_FILTRO_DUPLICATI);
				dati.addElement(de);

				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_CONFERMA_RICEZIONE);
				de.setType(DataElementType.HIDDEN);
				if(ServletUtils.isCheckBoxEnabled(confricpt) || CostantiRegistroServizi.ABILITATO.equals(confricpt)){
					de.setValue(Costanti.CHECK_BOX_ENABLED);
				}else {
					de.setValue(Costanti.CHECK_BOX_DISABLED);
				}
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_CONFERMA_RICEZIONE);
				dati.addElement(de);

				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_COLLABORAZIONE);
				de.setType(DataElementType.HIDDEN);
				if(ServletUtils.isCheckBoxEnabled(idcollpt) || CostantiRegistroServizi.ABILITATO.equals(idcollpt)){
					de.setValue(Costanti.CHECK_BOX_ENABLED);
				}else {
					de.setValue(Costanti.CHECK_BOX_DISABLED);
				}
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_COLLABORAZIONE);
				dati.addElement(de);

				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_CONSEGNA_ORDINE);
				de.setType(DataElementType.HIDDEN);
				if(ServletUtils.isCheckBoxEnabled(consordpt) || CostantiRegistroServizi.ABILITATO.equals(consordpt)){
					de.setValue(Costanti.CHECK_BOX_ENABLED);
				}else {
					de.setValue(Costanti.CHECK_BOX_DISABLED);
				}
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_CONSEGNA_ORDINE);
				dati.addElement(de);

				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_SCADENZA);
				de.setType(DataElementType.HIDDEN);
				de.setValue(scadenzapt);
				if( !modificheAbilitate && (scadenzapt==null || "".equals(scadenzapt)) )
					de.setValue(" ");
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_SCADENZA);
				dati.addElement(de);

			} else {

				de = new DataElement();
				de.setType(DataElementType.TITLE);
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_INFORMAZIONI);
				dati.addElement(de);

				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PROFILO_PROTOCOLLO);
				de.setType(DataElementType.SELECT);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_PROFILO_BUSTA);
				if(modificheAbilitate){
					de.setValues(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA);
					de.setLabels(AccordiServizioParteComuneCostanti.LABEL_INFORMAZIONI_PROTOCOLLO_ACCORDO);
					de.setSelected(profProtocollo);
					//							de.setOnChange("CambiaProfPT('" + tipoOp + "')");
					de.setPostBack(true);
				}else{
					de.setType(DataElementType.TEXT);
					if(profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT)){
						de.setValue(AccordiServizioParteComuneCostanti.LABEL_INFORMAZIONI_PROTOCOLLO_DEFAULT_ACCORDO);
					}else{
						de.setValue(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO);
					}
				}
				dati.addElement(de);

				de = new DataElement();
				de.setType(DataElementType.HIDDEN);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_PROFILO_BUSTA);
				de.setValue(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO);
				dati.addElement(de);

				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_PROFILO_COLLABORAZIONE);
				if (profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT)) {
					de.setType(DataElementType.HIDDEN);
					de.setValue(defProfiloCollaborazionePT);
				} else {
					de.setType(DataElementType.SELECT);
					de.setValues(this.core.getProfiliDiCollaborazioneSupportatiDalProtocollo(protocollo));
					// de.setLabels(tipoProfcollLabel);
					de.setSelected(profiloCollaborazionePT);
				}
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_PROFILO_COLLABORAZIONE);
				dati.addElement(de);

				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_FILTRO_DUPLICATI);
				if (profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT)) {
					de.setType(DataElementType.HIDDEN);
					de.setValue(deffiltroduppt);
				} else {
					if(!this.core.isFunzionalitaProtocolloSupportataDalProtocollo(protocollo, FunzionalitaProtocollo.FILTRO_DUPLICATI)){
						de.setType(DataElementType.HIDDEN);
						de.setValue(Costanti.CHECK_BOX_ENABLED);
					}else {
						if(modificheAbilitate){
							de.setType(DataElementType.CHECKBOX);
							if(ServletUtils.isCheckBoxEnabled(filtroduppt) || CostantiRegistroServizi.ABILITATO.equals(filtroduppt)){
								de.setSelected(true);
							}
						}else{
							de.setType(DataElementType.TEXT);
							if(ServletUtils.isCheckBoxEnabled(filtroduppt) || CostantiRegistroServizi.ABILITATO.equals(filtroduppt)){
								de.setValue(CostantiRegistroServizi.ABILITATO.toString());
							}else{
								de.setValue(CostantiRegistroServizi.DISABILITATO.toString());
							}
						}
					}
				}
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_FILTRO_DUPLICATI);
				dati.addElement(de);

				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_CONFERMA_RICEZIONE);
				if (profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT)) {
					de.setType(DataElementType.HIDDEN);
					de.setValue(defconfricpt);
				} else {
					if(!this.core.isFunzionalitaProtocolloSupportataDalProtocollo(protocollo, FunzionalitaProtocollo.CONFERMA_RICEZIONE)){
						de.setType(DataElementType.HIDDEN);
						de.setValue(Costanti.CHECK_BOX_DISABLED);
					}else {
						if(modificheAbilitate){
							de.setType(DataElementType.CHECKBOX);
							if(ServletUtils.isCheckBoxEnabled(confricpt) || CostantiRegistroServizi.ABILITATO.equals(confricpt)){
								de.setSelected(true);
							}
						}else{
							de.setType(DataElementType.TEXT);
							if(ServletUtils.isCheckBoxEnabled(confricpt) || CostantiRegistroServizi.ABILITATO.equals(confricpt)){
								de.setValue(CostantiRegistroServizi.ABILITATO.toString());
							}else{
								de.setValue(CostantiRegistroServizi.DISABILITATO.toString());
							}
						}
					}
				}
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_CONFERMA_RICEZIONE);
				dati.addElement(de);

				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_COLLABORAZIONE);
				if (profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT)) {
					de.setType(DataElementType.HIDDEN);
					de.setValue(defidcollpt);
				} else {
					if(!this.core.isFunzionalitaProtocolloSupportataDalProtocollo(protocollo, FunzionalitaProtocollo.COLLABORAZIONE)){
						de.setType(DataElementType.HIDDEN);
						de.setValue(Costanti.CHECK_BOX_DISABLED);
					}else {
						if(modificheAbilitate){
							de.setType(DataElementType.CHECKBOX);
							if(ServletUtils.isCheckBoxEnabled(idcollpt) || CostantiRegistroServizi.ABILITATO.equals(idcollpt)){
								de.setSelected(true);
							}
						}else{
							de.setType(DataElementType.TEXT);
							if(ServletUtils.isCheckBoxEnabled(idcollpt) || CostantiRegistroServizi.ABILITATO.equals(idcollpt)){
								de.setValue(CostantiRegistroServizi.ABILITATO.toString());
							}else{
								de.setValue(CostantiRegistroServizi.DISABILITATO.toString());
							}
						}
					}
				}
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_COLLABORAZIONE);
				dati.addElement(de);

				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_CONSEGNA_ORDINE);
				if (profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT)) {
					de.setType(DataElementType.HIDDEN);
					de.setValue(defconsordpt);
				} else {
					if(!this.core.isFunzionalitaProtocolloSupportataDalProtocollo(protocollo, FunzionalitaProtocollo.CONSEGNA_IN_ORDINE)){
						de.setType(DataElementType.HIDDEN);
						de.setValue(Costanti.CHECK_BOX_DISABLED);
					}else {
						if(modificheAbilitate){
							de.setType(DataElementType.CHECKBOX);
							if(ServletUtils.isCheckBoxEnabled(consordpt) || CostantiRegistroServizi.ABILITATO.equals(consordpt)){
								de.setSelected(true);
							}
						}else{
							de.setType(DataElementType.TEXT);
							if(ServletUtils.isCheckBoxEnabled(consordpt) || CostantiRegistroServizi.ABILITATO.equals(consordpt)){
								de.setValue(CostantiRegistroServizi.ABILITATO.toString());
							}else{
								de.setValue(CostantiRegistroServizi.DISABILITATO.toString());
							}
						}	
					}
				}
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_CONSEGNA_ORDINE);
				dati.addElement(de);


				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_SCADENZA);
				de.setValue(scadenzapt);
				if (profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT)) {
					de.setType(DataElementType.HIDDEN);
					de.setValue(defscadenzapt);
				} else {
					if(!this.core.isFunzionalitaProtocolloSupportataDalProtocollo(protocollo, FunzionalitaProtocollo.SCADENZA)){
						de.setType(DataElementType.HIDDEN);
						de.setValue(defscadenzapt);
					}else {
						de.setType(DataElementType.TEXT_EDIT);
						de.setValue(scadenzapt);
						if( !modificheAbilitate && (scadenzapt==null || "".equals(scadenzapt)) )
							de.setValue(" ");
					}
				}
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_SCADENZA);
				de.setSize(this.getSize());
				dati.addElement(de);

				boolean isStrutturaMSG = this.core.isShowAccordoParteComuneInformazioniStrutturaMessaggiWsdl();
				if(isStrutturaMSG){

					// Informazione WSDL
					de = new DataElement();
					de.setType(DataElementType.TITLE);
					de.setLabel(AccordiServizioParteComuneCostanti.LABEL_INFORMAZIONI_WSDL);
					dati.addElement(de);

					// Style
					de = new DataElement();
					de.setLabel(AccordiServizioParteComuneCostanti.LABEL_STYLE);
					de.setType(DataElementType.SELECT);
					de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_STYLE);
					de.setValues(AccordiServizioParteComuneCostanti.PORT_TYPES_STYLE);
					de.setLabels(AccordiServizioParteComuneCostanti.LABEL_PORT_TYPES_STYLE);
					de.setSelected(servizioStyle);
					dati.addElement(de);
				}

			}
			return dati;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public void prepareAccordiAzioniList(AccordoServizioParteComune as, List<Azione> lista, ISearch ricerca,String tipoAccordo)
			throws Exception {
		try {

			String id = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);

			String uri = null;
			uri = this.idAccordoFactory.getUriFromAccordo(as);

			ServletUtils.addListElementIntoSession(this.session, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_AZIONI,
					new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id),
					new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO, tipoAccordo),
					new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, uri));


			int idLista = Liste.ACCORDI_AZIONI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);	

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			// setto la barra del titolo
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo),null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
										new Parameter(AccordiServizioParteComuneCostanti.LABEL_AZIONI + " di " + uri, null)
						);
			}else{
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo),null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
										new Parameter(AccordiServizioParteComuneCostanti.LABEL_AZIONI + " di " + uri, 
												AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_AZIONI_LIST+"?"+
														AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID+"="+id+"&"+
														AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME+"="+uri+"&"+
														AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
														AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
														new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null)
						);
			}




			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, AccordiServizioParteComuneCostanti.LABEL_AZIONI, search);
			}

			// setto le label delle colonne
			String[] labels = { AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_NOME, AccordiServizioParteComuneCostanti.LABEL_PROFILO_PROTOCOLLO };
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<Azione> it = lista.iterator();

				while (it.hasNext()) {
					Azione az = it.next();
					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_AZIONI_CHANGE, 
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_NOME, az.getNome()),
							AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo));
					de.setValue(az.getNome());
					de.setIdToRemove(az.getNome());
					e.addElement(de);

					de = new DataElement();
					if (az.getProfAzione().equals(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT))
						de.setValue(AccordiServizioParteComuneCostanti.LABEL_INFORMAZIONI_PROTOCOLLO_DEFAULT_ACCORDO);
					else
						de.setValue(az.getProfAzione());
					e.addElement(de);

					dati.addElement(e);
				}

			}

			this.pd.setDati(dati);

			if(this.core.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(as.getStatoPackage())){
				this.pd.setAddButton(false);
				this.pd.setRemoveButton(false);
				this.pd.setSelect(false);
			}else{
				this.pd.setAddButton(true);
				this.pd.setRemoveButton(true);
				this.pd.setSelect(true);
			}
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Controlla i dati dell'azione dell'accordo
	public boolean accordiAzioniCheckData(TipoOperazione tipoOperazione) throws Exception {
		try {
			String id = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			String nomeaz = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_NOME);
			String profProtocollo = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_PROFILO_BUSTA);
			String filtrodupaz = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_FILTRO_DUPLICATI);
			if ((filtrodupaz != null) && (filtrodupaz.equals("null") || filtrodupaz.equals(Costanti.CHECK_BOX_DISABLED))) {
				filtrodupaz = null;
			}
			String confricaz = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_CONFERMA_RICEZIONE);
			if ((confricaz != null) && (confricaz.equals("null") || confricaz.equals(Costanti.CHECK_BOX_DISABLED))) {
				confricaz = null;
			}
			String idcollaz = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_COLLABORAZIONE);
			if ((idcollaz != null) && (idcollaz.equals("null") || idcollaz.equals(Costanti.CHECK_BOX_DISABLED))) {
				idcollaz = null;
			}
			String consordaz = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_CONSEGNA_ORDINE);
			if ((consordaz != null) && (consordaz.equals("null") || consordaz.equals(Costanti.CHECK_BOX_DISABLED))) {
				consordaz = null;
			}
			String scadenzaaz = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_SCADENZA);

			if (nomeaz.equals("")) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare un Nome");
				return false;
			}

			if ((nomeaz.indexOf(" ") != -1)) {
				this.pd.setMessage("Non inserire spazi nel nome dell'azione");
				return false;
			}

			// Il nome deve contenere solo lettere e numeri e '_' '-' '.'
			//if (!this.procToCall.isOk("^[0-9A-Za-z_\\-\\.]+$", nomeaz)) {
			if (!RegularExpressionEngine.isMatch(nomeaz,"^[_A-Za-z][\\-\\._A-Za-z0-9]*$")) {
				this.pd.setMessage("Il nome dell'azione puo' iniziare solo con un carattere [A-Za-z] o il simbolo '_' e dev'essere formato solo da caratteri, cifre, '_' , '-' e '.'");
				return false;
			}

			// Controllo che i campi DataElementType.SELECT abbiano uno dei valori ammessi
			if (!profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT) && !profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO)) {
				this.pd.setMessage("Il profilo  dev'essere \""+AccordiServizioParteComuneCostanti.LABEL_INFORMAZIONI_PROTOCOLLO_ACCORDO[0]
						+"\" o \""+AccordiServizioParteComuneCostanti.LABEL_INFORMAZIONI_PROTOCOLLO_ACCORDO[1]+"\"");
				return false;
			}

			if (profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO)) {
				// Controllo che i campi DataElementType.CHECKBOX abbiano uno dei valori
				// ammessi
				if ((filtrodupaz != null) && !filtrodupaz.equals(Costanti.CHECK_BOX_ENABLED)) {
					this.pd.setMessage(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_FILTRO_DUPLICATI+" dev'essere selezionato o deselezionato");
					return false;
				}
				if ((confricaz != null) && !confricaz.equals(Costanti.CHECK_BOX_ENABLED)) {
					this.pd.setMessage(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_CONFERMA_RICEZIONE+" dev'essere selezionata o deselezionata");
					return false;
				}
				if ((idcollaz != null) && !idcollaz.equals(Costanti.CHECK_BOX_ENABLED)) {
					this.pd.setMessage(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_COLLABORAZIONE+" dev'essere selezionata o deselezionata");
					return false;
				}
				if ((consordaz != null) && !consordaz.equals(Costanti.CHECK_BOX_ENABLED)) {
					this.pd.setMessage(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_CONSEGNA_ORDINE+" dev'essere selezionata o deselezionata");
					return false;
				}

				// scadenzaaz dev'essere numerico
				if (!scadenzaaz.equals("") && !RegularExpressionEngine.isMatch(scadenzaaz,"^[0-9]+$")) {
					this.pd.setMessage(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_SCADENZA+" dev'essere numerico");
					return false;
				}
			}

			// Se tipoOp = add, controllo che l'azione non sia gia' stata
			// registrata per l'accordo
			if (tipoOperazione.equals(TipoOperazione.ADD)) {
				int idInt = Integer.parseInt(id);
				boolean giaRegistrato = this.apcCore.existsAccordoServizioAzione(nomeaz, idInt);
				if (giaRegistrato) {
					this.pd.setMessage("L'azione " + nomeaz + " &egrave; gi&agrave; stata associata all'accordo " + id);
					return false;
				}
			}

			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public Vector<DataElement> addAccordiAzioniToDati(Vector<DataElement> dati, String id, String nomeaz,  String profProtocollo, 
			String filtrodupaz, String deffiltrodupaz, String confricaz, String defconfricaz, 
			String idcollaz, String defidcollaz, String consordaz, String defconsordaz, String scadenzaaz, 
			String defscadenzaaz, String defprofcoll, String profcoll, 
			TipoOperazione tipoOperazione, String azicorr, String[] azioniList,String stato, String tipoSICA,String protocollo) throws Exception{

		DataElement de = new DataElement();
		de.setValue(id);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
		dati.addElement(de);

		boolean modificheAbilitate = false;
		if( tipoOperazione.equals(TipoOperazione.ADD) ){
			modificheAbilitate = true;
		}else if(this.core.isShowGestioneWorkflowStatoDocumenti()==false){
			modificheAbilitate = true;
		}else if(StatiAccordo.finale.toString().equals(stato)==false){
			modificheAbilitate = true;
		}

		de = new DataElement();
		de.setValue(tipoSICA);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_NOME);
		de.setValue(nomeaz);
		if (tipoOperazione.equals(TipoOperazione.ADD)) {
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		} else {
			de.setType(DataElementType.TEXT);
		}
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_NOME);
		de.setSize(this.getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setType(DataElementType.TITLE);
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_INFORMAZIONI);
		dati.addElement(de);

		//boolean isAziCorr = false;
		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_AZIONE_CORRELATA);
		if ((azioniList != null) && 
				((profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT) && defprofcoll.equals(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO.toString())) 
						|| 
						(profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO) && profcoll.equals(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO.toString())))) {
			//isAziCorr = true;
			de.setType(DataElementType.SELECT);
			de.setValues(azioniList);
			de.setSelected(azicorr);
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(azicorr);
		}
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_CORRELATA);
		dati.addElement(de);



		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PROFILO_PROTOCOLLO);
		if(modificheAbilitate){
			de.setType(DataElementType.SELECT);
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_PROFILO_BUSTA);
			de.setValues(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA);
			de.setLabels(AccordiServizioParteComuneCostanti.LABEL_INFORMAZIONI_PROTOCOLLO_ACCORDO);
			de.setSelected(profProtocollo);
			//			de.setOnChange("CambiaProf(" + tipoOp + "', '" + isAziCorr + "')");
			de.setPostBack(true);
		}else{
			de.setType(DataElementType.TEXT);
			if(profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT)){
				de.setValue(AccordiServizioParteComuneCostanti.LABEL_INFORMAZIONI_PROTOCOLLO_DEFAULT_ACCORDO);
			}else{
				de.setValue(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO);
			}
		}
		dati.addElement(de);

		// Gli elementi qui sotto devono essere visualizzati solo in modalita' avanzata  && profilobusta = 'ridefinito' && se la corrispondente proprieta e' abilitata nel protocollo

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_PROFILO_COLLABORAZIONE);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_PROFILO_COLLABORAZIONE);
		if (profProtocollo.equals(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT)) {
			de.setType(DataElementType.HIDDEN);
			de.setValue(defprofcoll);
		} else {
			de.setType(DataElementType.SELECT);
			de.setValues(this.core.getProfiliDiCollaborazioneSupportatiDalProtocollo(protocollo));
			de.setSelected(profcoll);
			if(modificheAbilitate){
				//				de.setOnChange("CambiaProf('" + tipoOp + "', '" + true + "')");
				de.setPostBack(true);
			}
		}
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_FILTRO_DUPLICATI);
		if (profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT)
				) {
			de.setType(DataElementType.HIDDEN);
			de.setValue(deffiltrodupaz);
		} else {
			if((profProtocollo.equals(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO) 
					&& !this.core.isFunzionalitaProtocolloSupportataDalProtocollo(protocollo, FunzionalitaProtocollo.FILTRO_DUPLICATI)) ){
				de.setType(DataElementType.HIDDEN);
				de.setValue(Costanti.CHECK_BOX_ENABLED);
			}
			else
			{
				if(modificheAbilitate){
					de.setType(DataElementType.CHECKBOX);
					if( ServletUtils.isCheckBoxEnabled(filtrodupaz) || CostantiRegistroServizi.ABILITATO.equals(filtrodupaz) ){
						de.setSelected(true);
					}
				}else{
					de.setType(DataElementType.TEXT);
					if( ServletUtils.isCheckBoxEnabled(filtrodupaz) || CostantiRegistroServizi.ABILITATO.equals(filtrodupaz) ){
						de.setValue(CostantiRegistroServizi.ABILITATO.toString());
					}else{
						de.setValue(CostantiRegistroServizi.DISABILITATO.toString());
					}
				}
			}
		}
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_FILTRO_DUPLICATI);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_CONFERMA_RICEZIONE);
		if (profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT)
				) {
			de.setType(DataElementType.HIDDEN);
			de.setValue(defconfricaz);
		} else {
			if((profProtocollo.equals(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO) 
					&& ! this.core.isFunzionalitaProtocolloSupportataDalProtocollo(protocollo, FunzionalitaProtocollo.CONFERMA_RICEZIONE))){
				de.setType(DataElementType.HIDDEN);
				de.setValue(Costanti.CHECK_BOX_DISABLED);
			}else{

				if(modificheAbilitate){
					de.setType(DataElementType.CHECKBOX);
					if( ServletUtils.isCheckBoxEnabled(confricaz) || CostantiRegistroServizi.ABILITATO.equals(confricaz) ){
						de.setSelected(true);
					}
				}else{
					de.setType(DataElementType.TEXT);
					if( ServletUtils.isCheckBoxEnabled(confricaz) || CostantiRegistroServizi.ABILITATO.equals(confricaz) ){
						de.setValue(CostantiRegistroServizi.ABILITATO.toString());
					}else{
						de.setValue(CostantiRegistroServizi.DISABILITATO.toString());
					}
				}
			}
		}
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_CONFERMA_RICEZIONE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_COLLABORAZIONE);
		if (profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT)
				) {
			de.setType(DataElementType.HIDDEN);
			de.setValue(defidcollaz);
		} else {
			if(profProtocollo.equals(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO) 
					&& ! this.core.isFunzionalitaProtocolloSupportataDalProtocollo(protocollo, FunzionalitaProtocollo.COLLABORAZIONE)) {
				de.setType(DataElementType.HIDDEN);
				de.setValue(Costanti.CHECK_BOX_DISABLED);
			}else {

				if(modificheAbilitate){
					de.setType(DataElementType.CHECKBOX);
					if( ServletUtils.isCheckBoxEnabled(idcollaz) || CostantiRegistroServizi.ABILITATO.equals(idcollaz) ){
						de.setSelected(true);
					}
				}else{
					de.setType(DataElementType.TEXT);
					if( ServletUtils.isCheckBoxEnabled(idcollaz) || CostantiRegistroServizi.ABILITATO.equals(idcollaz) ){
						de.setValue(CostantiRegistroServizi.ABILITATO.toString());
					}else{
						de.setValue(CostantiRegistroServizi.DISABILITATO.toString());
					}
				}
			}
		}
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_COLLABORAZIONE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_CONSEGNA_ORDINE);
		if (profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT)
				) {
			de.setType(DataElementType.HIDDEN);
			de.setValue(defconsordaz);
		} else {
			if((profProtocollo.equals(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO) 
					&& ! this.core.isFunzionalitaProtocolloSupportataDalProtocollo(protocollo, FunzionalitaProtocollo.CONSEGNA_IN_ORDINE)) ){
				de.setType(DataElementType.HIDDEN);
				de.setValue(Costanti.CHECK_BOX_DISABLED);
			}else {
				if(modificheAbilitate){
					de.setType(DataElementType.CHECKBOX);
					if( ServletUtils.isCheckBoxEnabled(consordaz) || CostantiRegistroServizi.ABILITATO.equals(consordaz) ){
						de.setSelected(true);
					}
				}else{
					de.setType(DataElementType.TEXT);
					if( ServletUtils.isCheckBoxEnabled(consordaz) || CostantiRegistroServizi.ABILITATO.equals(consordaz) ){
						de.setValue(CostantiRegistroServizi.ABILITATO.toString());
					}else{
						de.setValue(CostantiRegistroServizi.DISABILITATO.toString());
					}
				}
			}
		}
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_CONSEGNA_ORDINE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_SCADENZA);
		de.setValue(scadenzaaz);
		if (profProtocollo.equals(AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT)
				) {
			de.setType(DataElementType.HIDDEN);
			de.setValue(defscadenzaaz);
		} else {
			if((profProtocollo.equals(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO) 
					&& ! this.core.isFunzionalitaProtocolloSupportataDalProtocollo(protocollo, FunzionalitaProtocollo.SCADENZA))){
				de.setType(DataElementType.HIDDEN);
				de.setValue(defscadenzaaz);
			}else {
				de.setType(DataElementType.TEXT_EDIT);
				de.setValue(scadenzaaz);
				if( !modificheAbilitate && (scadenzaaz==null || "".equals(scadenzaaz)) )
					de.setValue(" ");
			}
		}
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_AZIONI_SCADENZA);
		de.setSize(this.getSize());
		dati.addElement(de);

		return dati;
	}


	public void addAccordiWSDLChangeToDati(Vector<DataElement> dati,String id,String tipoAccordo,String tipo,String label,
			String oldwsdl,String statoPackage,boolean validazioneDocumenti, String tipologiaDocumentoScaricare){

		DataElement de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
		de.setValue(id);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
		dati.addElement(de);

		de = new DataElement();
		de.setValue(tipoAccordo);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
		de.setSize(this.getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_WSDL);
		de.setValue(tipo);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_WSDL);
		dati.addElement(de);

		if(this.core.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(statoPackage)){
			this.pd.disableEditMode();

			de = new DataElement();
			de.setType(DataElementType.TEXT_AREA_NO_EDIT);
			de.setValue(oldwsdl);
			de.setRows(30);
			de.setCols(110);
			de.setLabel("");
			dati.addElement(de);
			
			if(oldwsdl != null && !oldwsdl.isEmpty()){
				DataElement saveAs = new DataElement();
				saveAs.setValue(AccordiServizioParteComuneCostanti.LABEL_DOWNLOAD);
				saveAs.setType(DataElementType.LINK);
				saveAs.setUrl(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT, 
						new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_ID_ACCORDO, id),
						new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO, tipologiaDocumentoScaricare),
						new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO, ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_PARTE_COMUNE));
				dati.add(saveAs);
			}
			
		}
		else{
			//			de.setLabel(label.replace(" di ", " di <BR/>")+"<BR/>Attuale:");
			if(oldwsdl != null && !oldwsdl.isEmpty()){
				de = new DataElement();
				de.setType(DataElementType.TEXT_AREA_NO_EDIT);
				de.setValue(oldwsdl);
				de.setRows(30);
				de.setCols(110);
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_WSDL_ATTUALE +":");
				dati.addElement(de);
				
				DataElement saveAs = new DataElement();
				saveAs.setValue(AccordiServizioParteComuneCostanti.LABEL_DOWNLOAD);
				saveAs.setType(DataElementType.LINK);
				saveAs.setUrl(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT, 
						new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_ID_ACCORDO, id),
						new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO, tipologiaDocumentoScaricare),
						new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO, ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_PARTE_COMUNE));
				dati.add(saveAs);

				de = new DataElement();
				de.setType(DataElementType.TITLE);
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_WSDL_AGGIORNAMENTO);
				de.setValue("");
				de.setSize(this.getSize());
				dati.addElement(de);

			}else {
				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_WSDL_ATTUALE +":");
				de.setType(DataElementType.TEXT);
				de.setValue(AccordiServizioParteComuneCostanti.LABEL_WSDL_NOT_FOUND);
				dati.addElement(de);
			}

			de = new DataElement();
			de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_VALIDAZIONE_DOCUMENTI);
			de.setValue(""+validazioneDocumenti);
			if (InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
				de.setType(DataElementType.CHECKBOX);
				de.setSelected(validazioneDocumenti);
			}else{
				de.setType(DataElementType.HIDDEN);
			}
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_VALIDAZIONE_DOCUMENTI);
			de.setSize(this.getSize());
			dati.addElement(de);

			de = new DataElement();
			//			de.setLabel(label.replace(" di ", " di <BR/>")+"<BR/>Nuovo:");
			de.setLabel(AccordiServizioParteComuneCostanti.LABEL_WSDL_NUOVO +":");
			de.setValue("");
			de.setType(DataElementType.FILE);
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL);
			de.setSize(this.getSize());
			dati.addElement(de);

			if(oldwsdl != null && !oldwsdl.isEmpty()){
				de = new DataElement();
				de.setBold(true);
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_WSDL_CHANGE_CLEAR_WARNING);
				de.setValue(AccordiServizioParteComuneCostanti.LABEL_WSDL_CHANGE_CLEAR);
				de.setType(DataElementType.NOTE);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_WARN);
				de.setSize(this.getSize());
				dati.addElement(de);
			}

		}
	}

	public void addAccordiWSDLChangeToDatiAsHidden(Vector<DataElement> dati,String id,String tipoAccordo,String tipo,String label,
			String oldwsdl,String statoPackage,boolean validazioneDocumenti){

		DataElement de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
		de.setValue(id);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
		dati.addElement(de);

		de = new DataElement();
		de.setValue(tipoAccordo);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
		de.setSize(this.getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_WSDL);
		de.setValue(tipo);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_WSDL);
		dati.addElement(de);

		if(this.core.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(statoPackage)){
			this.pd.disableEditMode();
		}
		else{
			de = new DataElement();
			de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_VALIDAZIONE_DOCUMENTI);
			de.setValue(""+validazioneDocumenti);
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_VALIDAZIONE_DOCUMENTI);
			dati.addElement(de);
		}
	}

	// Controlla i dati dei WSDL degli Accordi e dei Servizi
	public boolean accordiWSDLCheckData(PageData pd,String tipo, String wsdl, AccordoServizioParteComune as,boolean validazioneDocumenti) throws Exception {

		if(validazioneDocumenti){

			boolean validazioneParteComune = false;
			boolean validazioneSpecificaConversazione = false;

			if (tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_DEFINITORIO)) {
				byte [] tmp = wsdl != null && !wsdl.trim().replaceAll("\n", "").equals("") ? wsdl.trim().getBytes() : null;
				as.setByteWsdlDefinitorio(tmp);
				validazioneParteComune = true;
			}
			else if (tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_CONCETTUALE)) {
				byte [] tmp = wsdl != null && !wsdl.trim().replaceAll("\n", "").equals("") ? wsdl.trim().getBytes() : null;
				as.setByteWsdlConcettuale(tmp);
				validazioneParteComune = true;
			}
			else if (tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_EROGATORE)) {
				byte [] tmp = wsdl != null && !wsdl.trim().replaceAll("\n", "").equals("") ? wsdl.trim().getBytes() : null;
				as.setByteWsdlLogicoErogatore(tmp);
				validazioneParteComune = true;
			}
			else if (tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_FRUITORE)) {
				byte [] tmp = wsdl != null && !wsdl.trim().replaceAll("\n", "").equals("") ? wsdl.trim().getBytes() : null;
				as.setByteWsdlLogicoFruitore(tmp);
				validazioneParteComune = true;
			}

			else if (tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_CONCETTUALE)) {
				byte [] tmp = wsdl != null && !wsdl.trim().replaceAll("\n", "").equals("") ? wsdl.trim().getBytes() : null;
				as.setByteSpecificaConversazioneConcettuale(tmp);
				validazioneSpecificaConversazione = true;
			}
			else if (tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_EROGATORE)) {
				byte [] tmp = wsdl != null && !wsdl.trim().replaceAll("\n", "").equals("") ? wsdl.trim().getBytes() : null;
				as.setByteSpecificaConversazioneErogatore(tmp);
				validazioneSpecificaConversazione = true;
			}
			else if (tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_FRUITORE)) {
				byte [] tmp = wsdl != null && !wsdl.trim().replaceAll("\n", "").equals("") ? wsdl.trim().getBytes() : null;
				as.setByteSpecificaConversazioneFruitore(tmp);
				validazioneSpecificaConversazione = true;
			}

			if(validazioneParteComune){
				ValidazioneResult result = this.apcCore.validaInterfacciaWsdlParteComune(as, this.soggettiCore);
				if(result.isEsito()==false){
					pd.setMessage(result.getMessaggioErrore());
				}
				return result.isEsito();
			}
			if(validazioneSpecificaConversazione){
				ValidazioneResult result = this.apcCore.validaSpecificaConversazione(as, this.soggettiCore);
				if(result.isEsito()==false){
					pd.setMessage(result.getMessaggioErrore());
				}
				return result.isEsito();
			}
		}

		return true;
	}

	public Vector<DataElement> addAccordiToDati(Vector<DataElement> dati, 
			String nome, String descr, String profcoll, String wsdldef, String wsdlconc, String wsdlserv, String wsdlservcorr, 
			String filtrodup, String confric, String idcoll, String consord, String scadenza, String id, 
			TipoOperazione tipoOperazione, boolean showUtilizzoSenzaAzione, boolean utilizzoSenzaAzione,
			String referente, String versione, 
			String[] providersList, String[] providersListLabel,boolean privato,
			boolean isServizioComposto,String[] accordiCooperazioneEsistenti,String[] accordiCooperazioneEsistentiLabel,String accordoCooperazione,
			String stato,String oldStato,String tipoAccordo,boolean validazioneDocumenti,
			String tipoProtocollo, List<String> listaTipiProtocollo, boolean used, boolean asWithAllegati
			) throws Exception {

		Boolean showAccordiAzioni = (Boolean) this.session.getAttribute("ShowAccordiAzioni");
		Boolean showAccordiPortTypes = (Boolean) this.session.getAttribute("ShowAccordiPortTypes");
		Boolean showAccordiCooperazione = (Boolean) this.session.getAttribute("ShowAccordiCooperazione");
		boolean isInterfacciaAvanzata = !InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType());
		boolean ripristinoStatoOperativo = this.core.isGestioneWorkflowStatoDocumenti_ripristinoStatoOperativoDaFinale();

		boolean modificheAbilitate = false;
		if( tipoOperazione.equals(TipoOperazione.ADD) ){
			modificheAbilitate = true;
		}else if(this.core.isShowGestioneWorkflowStatoDocumenti()==false){
			modificheAbilitate = true;
		}else if(StatiAccordo.finale.toString().equals(oldStato)==false){
			modificheAbilitate = true;
		}

		boolean modificaAbilitataServizioComposto = true;
		if (AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_SERVIZIO_COMPOSTO.equals(tipoAccordo)){
			modificaAbilitataServizioComposto = tipoOperazione.equals(TipoOperazione.ADD);
		}

		boolean showServizioCompostoCheck = AccordiServizioParteComuneUtilities.showFlagServizioComposto();

		DataElement de = new DataElement();
		de.setValue(tipoAccordo);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
		de.setSize(this.getSize());
		dati.addElement(de);

		// Protocollo

		if(TipoOperazione.CHANGE.equals(tipoOperazione)){
			de = new DataElement();
			de.setLabel(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			de.setValue(id);
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_PROTOCOLLO);

			if(listaTipiProtocollo != null && listaTipiProtocollo.size() > 1){
				if(used){
					de.setValue(tipoProtocollo);
					de.setType(DataElementType.TEXT);
					de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROTOCOLLO);
				}else {
					de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_PROTOCOLLO);
					de.setValues(listaTipiProtocollo);
					de.setSelected(tipoProtocollo);
					de.setType(DataElementType.SELECT);
					de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROTOCOLLO);
					de.setPostBack(true);
				}
			} else {
				de.setValue(tipoProtocollo);
				de.setType(DataElementType.HIDDEN);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROTOCOLLO);
			}
			de.setSize(this.getSize());
			dati.addElement(de);
		}

		// Gestione del tipo protocollo per la maschera add
		if(TipoOperazione.ADD.equals(tipoOperazione)){
			de = new DataElement();
			if(listaTipiProtocollo != null && listaTipiProtocollo.size() > 1){
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_PROTOCOLLO);
				de.setValues(listaTipiProtocollo);
				de.setSelected(tipoProtocollo);
				de.setType(DataElementType.SELECT);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROTOCOLLO);
				de.setPostBack(true);
			}else {
				de.setValue(tipoProtocollo);
				de.setType(DataElementType.HIDDEN);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROTOCOLLO);
			}
			de.setSize(this.getSize());
			dati.addElement(de);
		}

		// Referente

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_REFERENTE);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_REFERENTE);
		de.setPostBack(true);
		Soggetto sogg = null;
		if (referente != null && !"".equals(referente) && !"-".equals(referente)) {
			if(Integer.parseInt(referente)>0){
				sogg = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(referente));
			}
		}
		if( modificheAbilitate && modificaAbilitataServizioComposto ){

			de.setType(DataElementType.SELECT);
			de.setValues(providersList);
			de.setLabels(providersListLabel);
			if (referente != null && !"".equals(referente) && !"-".equals(referente)) {
				de.setSelected(referente);
			}else{
				de.setSelected("-");
			}
			//if(this.core.isBackwardCompatibilityAccordo11()==false){
			de.setRequired(true);

		}else{
			de.setType(DataElementType.HIDDEN);
			de.setValue(referente);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_REFERENTE);
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_REFERENTE_1_2);
			de.setType(DataElementType.TEXT);
			if (referente != null && !"".equals(referente) && !"-".equals(referente)) {
				de.setValue(sogg.getTipo()+"/"+sogg.getNome());
			}else{
				de.setValue("-");
			}
		}	
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_NOME);
		de.setValue(nome);
		//if (tipoOp.equals("add")) {
		if( tipoOperazione.equals(TipoOperazione.ADD) || modificheAbilitate){
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		}else{
			de.setType(DataElementType.TEXT);
		}
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME);
		de.setSize(this.getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_DESCRIZIONE);
		de.setValue(descr);
		if( tipoOperazione.equals(TipoOperazione.ADD) || modificheAbilitate){
			de.setType(DataElementType.TEXT_EDIT);
		}else{
			de.setType(DataElementType.TEXT);
			if( !modificheAbilitate && (descr==null || "".equals(descr)) )
				de.setValue(" ");
		}
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_DESCRIZIONE);
		de.setSize(this.getSize());
		dati.addElement(de);		

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_VERSIONE);
		de.setValue(versione);
		if( modificheAbilitate ){
			de.setType(DataElementType.TEXT_EDIT);
		}else{
			de.setType(DataElementType.TEXT);
		}
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_VERSIONE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_PRIVATO);
		if(privato){
			de.setValue(privato ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED );
			de.setSelected(privato ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED );
		}

		if (this.core.isShowFlagPrivato() &&  modificheAbilitate  && isInterfacciaAvanzata) {
			de.setType(DataElementType.CHECKBOX);
		} else {
			de.setType(DataElementType.HIDDEN);
		}
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PRIVATO);
		dati.addElement(de);

		if(this.core.isShowFlagPrivato() && !modificheAbilitate && isInterfacciaAvanzata){
			de = new DataElement();
			de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_PRIVATO_INFORMATIVA_VISIBILITA);
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PRIVATO_INFORMATIVA_VISIBILITA);
			if(privato){
				de.setValue(AccordiServizioParteComuneCostanti.INFORMATIVA_VISIBILITA_PRIVATA);
			}else{
				de.setValue(AccordiServizioParteComuneCostanti.INFORMATIVA_VISIBILITA_PUBBLICA);
			}
			dati.addElement(de);
		}

		if(showAccordiCooperazione){
			de = new DataElement();
			de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_IS_SERVIZIO_COMPOSTO);
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_IS_SERVIZIO_COMPOSTO);
			de.setValue(isServizioComposto ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED);
			if( modificheAbilitate && showServizioCompostoCheck){
				de.setSelected(isServizioComposto ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED);
				de.setType(DataElementType.CHECKBOX);
				de.setPostBack(true);
				//de.setOnClick("CambiaAccordoCooperazione(\"" + tipoOp + "\")");
			}else{
				de.setType(DataElementType.HIDDEN);
			}
			de.setSize(getSize());
			dati.addElement(de);
		}


		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_STATO_PACKAGE);
		if(this.core.isShowGestioneWorkflowStatoDocumenti()){
			String[] stati = StatiAccordo.toArray();
			if( tipoOperazione.equals(TipoOperazione.ADD)){
				//				if(isInterfacciaAvanzata)
				//					de.setType(DataElementType.TEXT);
				//				else 
				// Lascio sempre HIDDEN in ADD tanto poi se viene caricato il wsdl viene automaticamente impostato ad operativo
				// altrimenti in edit sara' possibile modificarlo.
				de.setType(DataElementType.HIDDEN);
				de.setValue(StatiAccordo.bozza.toString());
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_STATO_PACKAGE);
			} 
			else if(StatiAccordo.finale.toString().equals(oldStato)==false ){
				de.setType(DataElementType.SELECT);
				de.setValues(stati);
				de.setSelected(stato);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_STATO_PACKAGE);
			}else{
				de.setType(DataElementType.TEXT);
				de.setValue(StatiAccordo.finale.toString());
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_STATO_PACKAGE);

				if(ripristinoStatoOperativo){
					dati.addElement(de);

					de = new DataElement();

					de.setType(DataElementType.LINK);
					de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_CHANGE,
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, nome),
							AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RIPRISTINA_STATO, StatiAccordo.operativo.toString()),
							new Parameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME, Costanti.DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_IN_PROGRESS));
					de.setValue(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_RIPRISTINA_STATO_OPERATIVO);
				} 
			}
		}else{
			de.setType(DataElementType.HIDDEN);
			de.setValue(StatiAccordo.finale.toString());
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_STATO_PACKAGE);
		}

		dati.addElement(de);		

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_VALIDAZIONE_DOCUMENTI);
		de.setValue(""+validazioneDocumenti);
		//if ( tipoOperazione.equals(TipoOperazione.ADD) && InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType()) ) {
		if ( tipoOperazione.equals(TipoOperazione.ADD) ) {
			de.setType(DataElementType.CHECKBOX);
			de.setSelected(validazioneDocumenti);
		}else{
			de.setType(DataElementType.HIDDEN);
		}
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_VALIDAZIONE_DOCUMENTI);
		de.setSize(this.getSize());
		dati.addElement(de);

		if (tipoOperazione.equals(TipoOperazione.ADD) == false) {

			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

			if (showAccordiAzioni) {
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_AZIONI_LIST, 
						new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID,id),
						new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME,nome),
						AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo));
				if(contaListe){
					int num = this.apcCore.accordiAzioniList(Integer.parseInt(id), new Search(true)).size();
					de.setValue(AccordiServizioParteComuneCostanti.LABEL_AZIONI+" ("+num+")");
				}else{
					de.setValue(AccordiServizioParteComuneCostanti.LABEL_AZIONI);
				}
				dati.addElement(de);
			}

			if (showAccordiPortTypes) {
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPES_LIST, 
						new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID,id),
						new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME,nome),
						AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo));
				if(contaListe){
					int num = this.apcCore.accordiPorttypeList(Integer.parseInt(id), new Search(true)).size();
					de.setValue(AccordiServizioParteComuneCostanti.LABEL_PORT_TYPES+" ("+num+")");
				}else{
					de.setValue(AccordiServizioParteComuneCostanti.LABEL_PORT_TYPES);
				}
				dati.addElement(de);
			}

			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_EROGATORI_LIST, 
					new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID,id),
					new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME,nome),
					AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo));
			if(contaListe){
				int num = this.apcCore.accordiErogatoriList(Integer.parseInt(id), new Search(true)).size();
				de.setValue(AccordiServizioParteComuneCostanti.LABEL_ACCORDI_EROGATORI+" ("+num+")");
			}else{
				de.setValue(AccordiServizioParteComuneCostanti.LABEL_ACCORDI_EROGATORI);
			}
			dati.addElement(de);

			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_ALLEGATI_LIST, 
					new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID,id),
					new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME,nome),
					AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo));
			if(contaListe){
				int num = this.apcCore.accordiAllegatiList(Integer.parseInt(id), new Search(true)).size();
				de.setValue(AccordiServizioParteComuneCostanti.LABEL_ALLEGATI+" ("+num+")");
			}else{
				de.setValue(AccordiServizioParteComuneCostanti.LABEL_ALLEGATI);
			}
			dati.addElement(de);

			if(showAccordiCooperazione && isServizioComposto){
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_COMPONENTI_LIST, 
						new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID,id),
						new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME,nome));
				if(contaListe){
					int num = this.apcCore.accordiComponentiList(Integer.parseInt(id), new Search(true)).size();
					de.setValue(AccordiServizioParteComuneCostanti.LABEL_SERVIZI_COMPONENTI+" ("+num+")");
				}else{
					de.setValue(AccordiServizioParteComuneCostanti.LABEL_SERVIZI_COMPONENTI);
				}
				dati.addElement(de);
			}

		}

		if(showAccordiCooperazione && isServizioComposto){

			de = new DataElement();
			de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_ACCORDO_COOPERAZIONE);
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ACCORDO_COOPERAZIONE);
			if( modificheAbilitate && modificaAbilitataServizioComposto ){
				de.setType(DataElementType.SELECT);
				de.setValues(accordiCooperazioneEsistenti);
				de.setLabels(accordiCooperazioneEsistentiLabel);
				de.setSelected(accordoCooperazione!=null ? accordoCooperazione : "-");
				de.setRequired(true);
				de.setPostBack(true);
			}else{
				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_ACCORDO_COOPERAZIONE);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ACCORDO_COOPERAZIONE);
				de.setType(DataElementType.HIDDEN);
				de.setValue(accordoCooperazione);
				dati.addElement(de);


				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_ACCORDO_COOPERAZIONE);
				de.setType(DataElementType.TEXT);
				for(int i=0;i<accordiCooperazioneEsistenti.length;i++){
					if(accordiCooperazioneEsistenti[i].equals(accordoCooperazione)){
						de.setValue(accordiCooperazioneEsistentiLabel[i]);
						break;
					}
				}
			}
			dati.addElement(de);
		}

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_SPECIFICA_INTERFACCE);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		boolean showWsdlDefinitorio = this.core.isShowWsdlDefinitorioAccordoServizioParteComune();
		if(showWsdlDefinitorio){
			if (referente != null && !"".equals(referente) && !"-".equals(referente)) {
				showWsdlDefinitorio = this.apcCore.showWsdlDefinitorio(sogg.getTipo(),this.soggettiCore);
			}
			else{
				if(tipoProtocollo!=null){
					showWsdlDefinitorio = this.apcCore.showWsdlDefinitorio(tipoProtocollo);
				}
			}
		}

		
		if(isInterfacciaAvanzata){
			if (tipoOperazione.equals(TipoOperazione.ADD)) {
				//visualizza il link al wsdl definitorio se e' abilitato
				if(showWsdlDefinitorio){
					de = new DataElement();
					de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL_DEFINITORIO);
					de.setValue("");
					de.setType(DataElementType.FILE);
					de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_DEFINITORIO);
					de.setSize(this.getSize());
					dati.addElement(de);
				} else {
					de = new DataElement();
					de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL_DEFINITORIO);
					de.setValue(wsdldef);
					de.setType(DataElementType.HIDDEN);
					de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_DEFINITORIO);
					dati.addElement(de);
				}

				//se il protocollo supporta almeno un profilo asincorono tengo la visualizzazione attuale altrimenti mostro solo un elemento WSDL Logico.

				if(this.core.isProfiloDiCollaborazioneAsincronoSupportatoDalProtocollo(tipoProtocollo)){
					de = new DataElement();
					de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL_CONCETTUALE);
					de.setValue("");
					de.setType(DataElementType.FILE);
					de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_CONCETTUALE);
					de.setSize(this.getSize());
					dati.addElement(de);

					de = new DataElement();
					de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL_EROGATORE );
					de.setValue("");
					de.setType(DataElementType.FILE);
					de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_EROGATORE);
					de.setSize(this.getSize());
					dati.addElement(de);

					de = new DataElement();
					de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL_FRUITORE);
					de.setValue("");
					de.setType(DataElementType.FILE);
					de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_FRUITORE);
					de.setSize(this.getSize());
					dati.addElement(de);
				} else {
					de = new DataElement();
					de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL_CONCETTUALE);
					de.setValue(wsdlconc);
					de.setType(DataElementType.HIDDEN);
					de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_CONCETTUALE);
					dati.addElement(de);

					de = new DataElement();
					de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL_LOGICO);
					de.setValue("");
					de.setType(DataElementType.FILE);
					de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_EROGATORE);
					de.setSize(this.getSize());
					dati.addElement(de);

					de = new DataElement();
					de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL_FRUITORE);
					de.setValue(wsdlservcorr);
					de.setType(DataElementType.HIDDEN);
					de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_FRUITORE);
					dati.addElement(de);
				}
			} else {
				//visualizza il link al wsdl definitorio se e' abilitato
				if(showWsdlDefinitorio){
					de = new DataElement();
					de.setType(DataElementType.LINK);
					de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_WSDL_CHANGE, 
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_WSDL,AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_DEFINITORIO),
							AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo));
					de.setValue(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL_DEFINITORIO);
					dati.addElement(de);
				}

				//se il protocollo supporta almeno un profilo asincorono tengo la visualizzazione attuale altrimenti mostro solo un elemento WSDL Logico.

				if(this.core.isProfiloDiCollaborazioneAsincronoSupportatoDalProtocollo(tipoProtocollo)){

					de = new DataElement();
					de.setType(DataElementType.LINK);
					de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_WSDL_CHANGE, 
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_WSDL,AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_CONCETTUALE),
							AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo));
					de.setValue(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL_CONCETTUALE);
					dati.addElement(de);

					de = new DataElement();
					de.setType(DataElementType.LINK);
					de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_WSDL_CHANGE, 
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_WSDL,AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_EROGATORE),
							AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo));
					de.setValue(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL_EROGATORE);
					dati.addElement(de);

					de = new DataElement();
					de.setType(DataElementType.LINK);
					de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_WSDL_CHANGE, 
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_WSDL,AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_FRUITORE),
							AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo));
					de.setValue(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL_FRUITORE);
					dati.addElement(de);
				}else {
					de = new DataElement();
					de.setType(DataElementType.LINK);
					de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_WSDL_CHANGE, 
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_WSDL,AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_EROGATORE),
							AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo));
					de.setValue(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL_LOGICO);
					dati.addElement(de);
				}
				
				if(asWithAllegati){
					DataElement saveAs = new DataElement();
					saveAs.setValue(AccordiServizioParteComuneCostanti.LABEL_XSD_SCHEMA_COLLECTION);
					saveAs.setType(DataElementType.LINK);
					saveAs.setUrl(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT, 
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_ID_ACCORDO, id),
							new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO, ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_PARTE_COMUNE),
							new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO, ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_XSD_SCHEMA_COLLECTION));
					dati.add(saveAs);
				}
					
			}
		}else{
			// Interfaccia standard mostro solo quello LogicoErogatore, lascio gli altri campi hidden


			if (tipoOperazione.equals(TipoOperazione.ADD)) {
				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL_DEFINITORIO);
				de.setValue(wsdldef);
				de.setType(DataElementType.HIDDEN);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_DEFINITORIO);
				dati.addElement(de);

				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL_CONCETTUALE);
				de.setValue(wsdlconc);
				de.setType(DataElementType.HIDDEN);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_CONCETTUALE);
				dati.addElement(de);

				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL );
				de.setValue("");
				de.setType(DataElementType.FILE);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_EROGATORE);
				de.setSize(this.getSize());
				dati.addElement(de);

				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL_FRUITORE);
				de.setValue(wsdlservcorr);
				de.setType(DataElementType.HIDDEN);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_FRUITORE);
				dati.addElement(de);
			} else {
				//L'interfaccia standard deve far vedere solo il link al wsdl del servzio

				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_WSDL_CHANGE, 
						new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id),
						new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_WSDL,AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_EROGATORE),
						AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo));
				de.setValue(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL);
				dati.addElement(de);

			}
		}

		boolean showConversazioni = false;
		if (referente != null && !"".equals(referente) && !"-".equals(referente)) {
			showConversazioni = this.apcCore.showConversazioni(sogg.getTipo(),this.soggettiCore);
		}
		else{
			if(tipoProtocollo!=null){
				showConversazioni = this.apcCore.showConversazioni(tipoProtocollo);
			}
			else{
				showConversazioni = false;
			}
		}

		showConversazioni = showConversazioni && isInterfacciaAvanzata;

		if(showConversazioni){

			de = new DataElement();
			de.setLabel(AccordiServizioParteComuneCostanti.LABEL_SPECIFICA_CONVERSAZIONI);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);

			if (tipoOperazione.equals(TipoOperazione.ADD)) {
				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_CONCETTUALE);
				de.setValue("");
				de.setType(DataElementType.FILE);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_CONCETTUALE);
				de.setSize(this.getSize());
				dati.addElement(de);

				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_EROGATORE);
				de.setValue("");
				de.setType(DataElementType.FILE);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_EROGATORE);
				de.setSize(this.getSize());
				dati.addElement(de);

				de = new DataElement();
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_FRUITORE);
				de.setValue("");
				de.setType(DataElementType.FILE);
				de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_FRUITORE);
				de.setSize(this.getSize());
				dati.addElement(de);

			} else {

				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_WSDL_CHANGE, 
						new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id),
						new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_WSDL,AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_CONCETTUALE),
						AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo));
				de.setValue(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_CONCETTUALE);
				dati.addElement(de);

				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_WSDL_CHANGE, 
						new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id),
						new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_WSDL,AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_EROGATORE),
						AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo));
				de.setValue(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_EROGATORE);
				dati.addElement(de);

				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_WSDL_CHANGE, 
						new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id),
						new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_WSDL,AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_FRUITORE),
						AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo));
				de.setValue(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_FRUITORE);
				dati.addElement(de);
			}

		}

		Boolean gestioneInfoProtocollo = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_INFO_PROTOCOLLO);

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_INFORMAZIONI);
		if (gestioneInfoProtocollo) {
			de.setType(DataElementType.TITLE);
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue("");
		}
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_PROFILO_COLLABORAZIONE);
		if (gestioneInfoProtocollo  ) {
			de.setType(DataElementType.SELECT);
			de.setValues(this.core.getProfiliDiCollaborazioneSupportatiDalProtocollo(tipoProtocollo));
			de.setSelected(profcoll);
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(profcoll);
		}
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROFILO_COLLABORAZIONE);
		dati.addElement(de);

		// nel caso di change visualizzo il checkbox utilizzoSenzaAzione
		// solo se showUtilizzoSenzaAzione e' true
		if (showUtilizzoSenzaAzione && gestioneInfoProtocollo) {
			de = new DataElement();
			de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_UTILIZZO_SENZA_AZIONE);
			if(modificheAbilitate){
				de.setType(DataElementType.CHECKBOX);
			}else{
				de.setType(DataElementType.TEXT);
				if(utilizzoSenzaAzione){
					de.setValue(CostantiRegistroServizi.ABILITATO.toString());
				}else{
					de.setValue(CostantiRegistroServizi.DISABILITATO.toString());
				}
			}
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_UTILIZZO_SENZA_AZIONE);
			if (utilizzoSenzaAzione) {
				de.setSelected(true);
			}
			dati.addElement(de);
		}

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_FILTRO_DUPLICATI);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_FILTRO_DUPLICATI);
		if (gestioneInfoProtocollo && this.core.isFunzionalitaProtocolloSupportataDalProtocollo(tipoProtocollo, FunzionalitaProtocollo.FILTRO_DUPLICATI)) {
			if(modificheAbilitate){
				de.setType(DataElementType.CHECKBOX);
				if ( ServletUtils.isCheckBoxEnabled(filtrodup) || CostantiRegistroServizi.ABILITATO.equals(filtrodup) ) {
					de.setSelected(true);
				}
			}else{
				de.setType(DataElementType.TEXT);
				if ( ServletUtils.isCheckBoxEnabled(filtrodup) || CostantiRegistroServizi.ABILITATO.equals(filtrodup) ) {
					de.setValue(CostantiRegistroServizi.ABILITATO.toString());
				}else{
					if(modificheAbilitate){
						de.setValue(CostantiRegistroServizi.DISABILITATO.toString());
					}else{
						de.setValue(CostantiRegistroServizi.DISABILITATO.toString());
					}
				}
			}	
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(Costanti.CHECK_BOX_ENABLED);
		}
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_CONFERMA_RICEZIONE);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_CONFERMA_RICEZIONE);
		if (gestioneInfoProtocollo&& this.core.isFunzionalitaProtocolloSupportataDalProtocollo(tipoProtocollo, FunzionalitaProtocollo.CONFERMA_RICEZIONE)) {
			if(modificheAbilitate){
				de.setType(DataElementType.CHECKBOX);
				if (ServletUtils.isCheckBoxEnabled(confric) || CostantiRegistroServizi.ABILITATO.equals(confric)) {
					de.setSelected(true);
				}
			}else{
				de.setType(DataElementType.TEXT);
				if (ServletUtils.isCheckBoxEnabled(confric) || CostantiRegistroServizi.ABILITATO.equals(confric)) {
					de.setValue(CostantiRegistroServizi.ABILITATO.toString());
				}else{
					de.setValue(CostantiRegistroServizi.DISABILITATO.toString());
				}
			}
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(Costanti.CHECK_BOX_DISABLED);
		}
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_COLLABORAZIONE);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_COLLABORAZIONE);
		if (gestioneInfoProtocollo && this.core.isFunzionalitaProtocolloSupportataDalProtocollo(tipoProtocollo, FunzionalitaProtocollo.COLLABORAZIONE)) {
			if(modificheAbilitate){
				de.setType(DataElementType.CHECKBOX);
				if (ServletUtils.isCheckBoxEnabled(idcoll) || CostantiRegistroServizi.ABILITATO.equals(idcoll)) {
					de.setSelected(true);
				}
			}else{
				de.setType(DataElementType.TEXT);
				if (ServletUtils.isCheckBoxEnabled(idcoll) || CostantiRegistroServizi.ABILITATO.equals(idcoll)) {
					de.setValue(CostantiRegistroServizi.ABILITATO.toString());
				}else{
					de.setValue(CostantiRegistroServizi.DISABILITATO.toString());
				}
			}
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(Costanti.CHECK_BOX_DISABLED);
		}
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_CONSEGNA_ORDINE);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_CONSEGNA_ORDINE);
		if (gestioneInfoProtocollo && this.core.isFunzionalitaProtocolloSupportataDalProtocollo(tipoProtocollo, FunzionalitaProtocollo.CONSEGNA_IN_ORDINE)) {
			if(modificheAbilitate){
				de.setType(DataElementType.CHECKBOX);
				if (ServletUtils.isCheckBoxEnabled(consord) || CostantiRegistroServizi.ABILITATO.equals(consord)) {
					de.setSelected(true);
				}
			}else{
				de.setType(DataElementType.TEXT);
				if (ServletUtils.isCheckBoxEnabled(consord) || CostantiRegistroServizi.ABILITATO.equals(consord)) {
					de.setValue(CostantiRegistroServizi.ABILITATO.toString());
				}else{
					de.setValue(CostantiRegistroServizi.DISABILITATO.toString());
				}
			}
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(Costanti.CHECK_BOX_DISABLED);
		}
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_SCADENZA);
		de.setValue(scadenza);
		if (gestioneInfoProtocollo && this.core.isFunzionalitaProtocolloSupportataDalProtocollo(tipoProtocollo, FunzionalitaProtocollo.SCADENZA)) {
			de.setType(DataElementType.TEXT_EDIT);
			if( !modificheAbilitate && (scadenza==null || "".equals(scadenza)) )
				de.setValue(" ");
		} else {
			de.setType(DataElementType.HIDDEN);
		}
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SCADENZA);
		de.setSize(this.getSize());
		dati.addElement(de);

		return dati;
	}


	public Vector<DataElement> addAccordiToDatiAsHidden(Vector<DataElement> dati, 
			String nome, String descr, String profcoll, String wsdldef, String wsdlconc, String wsdlserv, String wsdlservcorr, 
			String filtrodup, String confric, String idcoll, String consord, String scadenza, String id, 
			TipoOperazione tipoOperazione, boolean showUtilizzoSenzaAzione, boolean utilizzoSenzaAzione,
			String referente, String versione, 
			String[] providersList, String[] providersListLabel,boolean privato,
			boolean isServizioComposto,String[] accordiCooperazioneEsistenti,String[] accordiCooperazioneEsistentiLabel,String accordoCooperazione,
			String stato,String oldStato,String tipoAccordo,boolean validazioneDocumenti,
			String tipoProtocollo, List<String> listaTipiProtocollo, boolean used
			) throws Exception {

		Boolean showAccordiCooperazione = (Boolean) this.session.getAttribute("ShowAccordiCooperazione");

		boolean modificheAbilitate = false;
		if( tipoOperazione.equals(TipoOperazione.ADD) ){
			modificheAbilitate = true;
		}else if(this.core.isShowGestioneWorkflowStatoDocumenti()==false){
			modificheAbilitate = true;
		}else if(StatiAccordo.finale.toString().equals(oldStato)==false){
			modificheAbilitate = true;
		}

		boolean modificaAbilitataServizioComposto = true;
		if (AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_SERVIZIO_COMPOSTO.equals(tipoAccordo)){
			modificaAbilitataServizioComposto = tipoOperazione.equals(TipoOperazione.ADD);
		}

		DataElement de = new DataElement();
		de.setValue(tipoAccordo);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
		de.setSize(this.getSize());
		dati.addElement(de);

		if(TipoOperazione.CHANGE.equals(tipoOperazione)){
			de = new DataElement();
			de.setLabel(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			de.setValue(id);
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			dati.addElement(de);
		}

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_PROTOCOLLO);
		de.setValue(tipoProtocollo);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROTOCOLLO);
		de.setSize(this.getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_NOME);
		de.setValue(nome);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME);
		de.setSize(this.getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_DESCRIZIONE);
		de.setValue(descr);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_DESCRIZIONE);
		de.setSize(this.getSize());
		dati.addElement(de);		

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_REFERENTE);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_REFERENTE);

		Soggetto sogg = null;
		if (referente != null && !"".equals(referente) && !"-".equals(referente)) {
			if(Integer.parseInt(referente)>0){
				sogg = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(referente));
			}
		}

		if( modificheAbilitate && modificaAbilitataServizioComposto ){
			de.setType(DataElementType.HIDDEN);
			if (referente != null && !"".equals(referente) && !"-".equals(referente)) {
				de.setValue(referente);
			}else{
				de.setValue("-");
			}
		}else{
			de.setType(DataElementType.HIDDEN);
			de.setValue(referente);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_REFERENTE);
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_REFERENTE_1_2);
			de.setType(DataElementType.HIDDEN);
			if (referente != null && !"".equals(referente) && !"-".equals(referente)) {
				de.setValue(sogg.getTipo()+"/"+sogg.getNome());
			}else{
				de.setValue("-");
			}
		}	
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_VERSIONE);
		de.setValue(versione);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_VERSIONE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_PRIVATO);
		if(privato){
			de.setValue(privato ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED );
			//			de.setSelected(privato ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED );
		} else 
			de.setValue(null);

		de.setType(DataElementType.HIDDEN);
		//		}
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PRIVATO);
		dati.addElement(de);

		if(showAccordiCooperazione){
			de = new DataElement();
			de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_IS_SERVIZIO_COMPOSTO);
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_IS_SERVIZIO_COMPOSTO);
			de.setValue(isServizioComposto ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED);
			de.setType(DataElementType.HIDDEN);
			de.setSize(getSize());
			dati.addElement(de);
		}


		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_STATO_PACKAGE);
		de.setType(DataElementType.HIDDEN);
		de.setValue(stato);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_STATO_PACKAGE);
		dati.addElement(de);		

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_VALIDAZIONE_DOCUMENTI);
		de.setValue(""+validazioneDocumenti);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_VALIDAZIONE_DOCUMENTI);
		de.setSize(this.getSize());
		dati.addElement(de);

		if(showAccordiCooperazione && isServizioComposto){

			de = new DataElement();
			de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_ACCORDO_COOPERAZIONE);
			de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ACCORDO_COOPERAZIONE);
			de.setType(DataElementType.HIDDEN);
			de.setValue(accordoCooperazione);
			dati.addElement(de);
			dati.addElement(de);
		}

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_PROFILO_COLLABORAZIONE);
		de.setType(DataElementType.HIDDEN);
		de.setValue(profcoll);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROFILO_COLLABORAZIONE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_UTILIZZO_SENZA_AZIONE);
		de.setType(DataElementType.HIDDEN);
		if(utilizzoSenzaAzione){
			de.setValue(CostantiRegistroServizi.ABILITATO.toString());
		}else{
			de.setValue(CostantiRegistroServizi.DISABILITATO.toString());
		}
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_UTILIZZO_SENZA_AZIONE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_FILTRO_DUPLICATI);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_FILTRO_DUPLICATI);
		if ( ServletUtils.isCheckBoxEnabled(filtrodup) || CostantiRegistroServizi.ABILITATO.equals(filtrodup) )
			de.setValue(Costanti.CHECK_BOX_ENABLED);
		else 
			de.setValue(Costanti.CHECK_BOX_DISABLED);
		de.setType(DataElementType.HIDDEN);

		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_CONFERMA_RICEZIONE);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_CONFERMA_RICEZIONE);
		if (ServletUtils.isCheckBoxEnabled(confric) || CostantiRegistroServizi.ABILITATO.equals(confric)) 
			de.setValue(Costanti.CHECK_BOX_ENABLED);
		else 
			de.setValue(Costanti.CHECK_BOX_DISABLED);

		de.setType(DataElementType.HIDDEN);

		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_COLLABORAZIONE);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_COLLABORAZIONE);
		if (ServletUtils.isCheckBoxEnabled(idcoll) || CostantiRegistroServizi.ABILITATO.equals(idcoll)) {
			de.setValue(Costanti.CHECK_BOX_ENABLED);
		}else 
			de.setValue(Costanti.CHECK_BOX_DISABLED);

		de.setType(DataElementType.HIDDEN);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_CONSEGNA_ORDINE);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_CONSEGNA_ORDINE);
		if (ServletUtils.isCheckBoxEnabled(consord) || CostantiRegistroServizi.ABILITATO.equals(consord)) {
			de.setValue(Costanti.CHECK_BOX_ENABLED);
		}else 
			de.setValue(Costanti.CHECK_BOX_DISABLED);

		de.setType(DataElementType.HIDDEN);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_SCADENZA);
		de.setValue(scadenza);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SCADENZA);
		de.setSize(this.getSize());
		dati.addElement(de);

		return dati;
	}


	// Controlla i dati del connettore del servizio
	boolean accordiErogatoriCheckData(List<ExtendedConnettore> listExtendedConnettore) throws Exception {
		try {
			// String id = this.request.getParameter("id");
			// int idInt = 0;
			// if (tipoOp.equals("change")) {
			// idInt = Integer.parseInt(id);
			// }
			String nomeprov = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_NOME_SOGGETTO);
			String tipoprov = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_TIPO_SOGGETTO);
			// String nomeservizio = this.request.getParameter("nomeservizio");
			// String tiposervizio = this.request.getParameter("tiposervizio");
			String endpointtype = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_EROGATORI_ENDPOINT_TYPE);

			if (!this.connettoriHelper.endPointCheckData(listExtendedConnettore)) {
				return false;
			}

			// Se il connettore e' disabilitato devo controllare che il
			// connettore del soggetto non sia disabilitato
			if (endpointtype.equals(TipiConnettore.DISABILITATO.toString())) {
				IDSoggetto ids = new IDSoggetto(tipoprov, nomeprov);
				Soggetto sogg = this.soggettiCore.getSoggettoRegistro(ids);
				Connettore conn = sogg.getConnettore();
				String eptypeprov = conn.getTipo();
				if (eptypeprov.equals(TipiConnettore.DISABILITATO.toString())) {
					this.pd.setMessage("Il connettore del servizio deve essere specificato se non &egrave; stato definito un connettore per il relativo soggetto");
					return false;
				}
			}

			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Controlla i dati degli Accordi
	boolean accordiCheckData(TipoOperazione tipoOperazione, String nome, String descr, String profcoll, 
			String wsdldef, String wsdlconc, String wsdlserv, String wsdlservcorr, 
			String filtrodup, String confric, String idcoll, String consord, String scadenza, String id,
			String referente,String versione,String accordoCooperazione, 
			boolean visibilitaAccordoServizio,boolean visibilitaAccordoCooperazione,
			IDAccordo idAccordoOLD, String wsblconc, String wsblserv, String wsblservcorr,boolean validazioneDocumenti,
			String tipoProtocollo, String backToStato)
					throws Exception {
		try {
			int idInt = 0;
			if (tipoOperazione.equals(TipoOperazione.CHANGE)) {
				idInt = Integer.parseInt(id);
			}
			if (referente == null) {
				referente = "";
			}

			// ripristina dello stato solo in modalita change
			if(backToStato != null && tipoOperazione.equals(TipoOperazione.CHANGE)){
				return true;
			}

			// Campi obbligatori
			if (nome==null || nome.equals("")) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare un Nome");
				return false;
			}
			//if(this.core.isBackwardCompatibilityAccordo11()==false){
			if (versione==null || versione.equals("")) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare una Versione dell'accordo");
				return false;
			}
			if(referente==null || referente.equals("") || referente.equals("-")){
				this.pd.setMessage("Dati incompleti. E' necessario indicare un Soggetto Referente");
				return false;
			}
			//}

			// Controllo che non ci siano spazi nei campi di testo
			if ((nome.indexOf(" ") != -1) || (scadenza.indexOf(" ") != -1)) {
				this.pd.setMessage("Non inserire spazi nei campi di testo 'nome' e 'scadenza'");
				return false;
			}


			// Il nome deve contenere solo lettere e numeri e '_' '-' '.'
			if (!RegularExpressionEngine.isMatch(nome,"^[0-9A-Za-z_\\-\\.]+$")) {
				this.pd.setMessage("Il nome dell'accordo di servizio dev'essere formato solo caratteri, cifre, '_' , '-' e '.'");
				return false;
			}

			// La versione deve contenere solo lettere e numeri e '.'
			/*if (!versione.equals("") && !this.procToCall.isOk("^[1-9]+[\\.][0-9]+[0-9A-Za-z]*$", versione)  && !this.procToCall.isOk("^[0-9]+$",versione)) {
					this.pd.setMessage("La versione dev'essere scritto come MajorVersion[.MinorVersion*] (MajorVersion [1-9]) (MinorVersion [0-9]) (* [0-9A-Za-z]) ");
					return false;
				}*/
			if (!versione.equals("") && !RegularExpressionEngine.isMatch(versione,"^[1-9]+[0-9]*$")) {
				this.pd.setMessage("La versione dev'essere rappresentata da un numero intero");
				return false;
			}

			// Controllo che i campi DataElementType.SELECT abbiano uno dei valori ammessi
			if (!profcoll.equals(AccordiServizioParteComuneCostanti.TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO) && 
					!profcoll.equals(AccordiServizioParteComuneCostanti.TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO) && 
					!profcoll.equals(AccordiServizioParteComuneCostanti.TIPO_PROFILO_COLLABORAZIONE_SINCRONO) && 
					!profcoll.equals((AccordiServizioParteComuneCostanti.TIPO_PROFILO_COLLABORAZIONE_ONEWAY) )) {
				this.pd.setMessage("Il profilo di collaborazione dev'essere "+AccordiServizioParteComuneCostanti.TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO+
						", "+AccordiServizioParteComuneCostanti.TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO+
						", "+AccordiServizioParteComuneCostanti.TIPO_PROFILO_COLLABORAZIONE_SINCRONO+
						" o "+AccordiServizioParteComuneCostanti.TIPO_PROFILO_COLLABORAZIONE_ONEWAY);
				return false;
			}

			// Controllo che i campi DataElementType.CHECKBOX abbiano uno dei valori ammessi
			if ((filtrodup != null) && !filtrodup.equals(Costanti.CHECK_BOX_ENABLED) && !filtrodup.equals(CostantiRegistroServizi.ABILITATO) && !filtrodup.equals(Costanti.CHECK_BOX_DISABLED) && !filtrodup.equals(CostantiRegistroServizi.DISABILITATO)) {
				this.pd.setMessage("Filtro duplicati dev'essere selezionato o deselezionato");
				return false;
			}
			if ((confric != null) && !confric.equals(Costanti.CHECK_BOX_ENABLED) && !confric.equals(CostantiRegistroServizi.ABILITATO) && !confric.equals(Costanti.CHECK_BOX_DISABLED) && !confric.equals(CostantiRegistroServizi.DISABILITATO)) {
				this.pd.setMessage("Conferma ricezione dev'essere selezionata o deselezionata");
				return false;
			}
			if ((idcoll != null) && !idcoll.equals(Costanti.CHECK_BOX_ENABLED) && !idcoll.equals(CostantiRegistroServizi.ABILITATO) && !idcoll.equals(Costanti.CHECK_BOX_DISABLED) && !idcoll.equals(CostantiRegistroServizi.DISABILITATO)) {
				this.pd.setMessage("ID Collaborazione dev'essere selezionata o deselezionata");
				return false;
			}
			if ((consord != null) && !consord.equals(Costanti.CHECK_BOX_ENABLED) && !consord.equals(CostantiRegistroServizi.ABILITATO) && !consord.equals(Costanti.CHECK_BOX_DISABLED) && !consord.equals(CostantiRegistroServizi.DISABILITATO)) {
				this.pd.setMessage("Consegna in ordine dev'essere selezionata o deselezionata");
				return false;
			}

			// scadenza dev'essere numerica
			if (!scadenza.equals("") && !RegularExpressionEngine.isMatch(scadenza,"^[0-9]+$")) {
				this.pd.setMessage("Scadenza dev'essere numerico");
				return false;
			}

			if ("-".equals(accordoCooperazione)) {
				this.pd.setMessage("L'accordo di cooperazione e' richiesto.");
				return false;
			}
			if("-".equals(accordoCooperazione)==false && "".equals(accordoCooperazione)==false  && accordoCooperazione!=null){
				// Visibilita rispetto all'accordo
				boolean nonVisibile = true;
				boolean visibile = false;
				if(visibilitaAccordoServizio==visibile){
					if(visibilitaAccordoCooperazione==nonVisibile){
						this.pd.setMessage("Non e' possibile utilizzare un accordo di cooperazione con visibilita' privata, in un accordo di servizio con visibilita' pubblica.");
						return false;
					}
				}
			}

			// Controllo che il referente appartenga alla lista di
			// providers disponibili
			IDSoggetto soggettoReferente = null;
			Soggetto sRef = null;
			//if (gestioneWSBL.equals(Costanti.CHECK_BOX_ENABLED)) {
			if(referente!=null && !referente.equals("") && !referente.equals("-")){
				boolean trovatoProv = this.soggettiCore.existsSoggetto(Integer.parseInt(referente));
				if (!trovatoProv) {
					this.pd.setMessage("Il Soggetto referente dev'essere scelto tra quelli definiti nel pannello Soggetti");
					return false;
				}else{
					sRef = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(referente));
					// Visibilita rispetto all'accordo
					boolean visibile = false;
					if(visibilitaAccordoServizio==visibile){
						if(sRef.getPrivato()!=null && sRef.getPrivato()==true){
							this.pd.setMessage("Non e' possibile utilizzare un soggetto referente con visibilita' privata, in un accordo di servizio con visibilita' pubblica.");
							return false;
						}
					}
					soggettoReferente = new IDSoggetto(sRef.getTipo(),sRef.getNome());
				}
			}
			//	}

			// Controllo che non esistano altri accordi con stesso nome
			// Se tipoOp = change, devo fare attenzione a non escludere nome
			// del servizio selezionato
			int idAcc = 0;
			IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromValues(nome,soggettoReferente,versione);
			boolean esisteAS = this.apcCore.existsAccordoServizio(idAccordo);
			AccordoServizioParteComune as = null;
			if (esisteAS) {
				as = this.apcCore.getAccordoServizio(idAccordo);
				idAcc = as.getId().intValue();
			}
			if ((idAcc != 0) && (tipoOperazione.equals(TipoOperazione.ADD) || (tipoOperazione.equals(TipoOperazione.CHANGE) && (idInt != idAcc)))) {
				if(this.idAccordoFactory.versioneNonDefinita(versione) && soggettoReferente==null)
					this.pd.setMessage("Esiste gi&agrave; un accordo con nome " + nome);
				else if(this.idAccordoFactory.versioneNonDefinita(versione) && soggettoReferente!=null)
					this.pd.setMessage("Esiste gi&agrave; un accordo con nome " + nome+" del soggetto referente "+soggettoReferente.toString());
				else if(!this.idAccordoFactory.versioneNonDefinita(versione) && soggettoReferente==null)
					this.pd.setMessage("Esiste gi&agrave; un accordo (versione "+versione+") con nome " + nome);
				else
					this.pd.setMessage("Esiste gi&agrave; un accordo (versione "+versione+") con nome " + nome+" del soggetto referente "+soggettoReferente.toString());
				return false;
			}

			// Controllo visibilita servizi componenti
			if (tipoOperazione.equals(TipoOperazione.CHANGE)) {

				as = this.apcCore.getAccordoServizio(idAccordoOLD);

				if((as.getPrivato()==null || as.getPrivato()==false) && as.getServizioComposto()!=null){
					for(int i=0;i<as.getServizioComposto().sizeServizioComponenteList(); i++){
						AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(as.getServizioComposto().getServizioComponente(i).getIdServizioComponente());
						Servizio s = asps.getServizio();
						if(asps.getPrivato()!=null && asps.getPrivato()){
							this.pd.setMessage("Non e' possibile impostare una visibilita' pubblica all'accordo di servizio, poiche' possiede un servizio componente ["+s.getTipo()+"/"+s.getNome()+"_"
									+s.getTipoSoggettoErogatore()+"/"+s.getNomeSoggettoErogatore()+"] con visibilita' privata.");
							return false;
						}
					}
				}		
			}


			AccordoServizioParteComune accordoServizioParteComune = new AccordoServizioParteComune();
			accordoServizioParteComune.setNome(nome);
			accordoServizioParteComune.setDescrizione(descr);
			accordoServizioParteComune.setProfiloCollaborazione(ProfiloCollaborazione.toEnumConstant(profcoll));
			accordoServizioParteComune.setFiltroDuplicati(StatoFunzionalita.toEnumConstant(filtrodup));
			accordoServizioParteComune.setConfermaRicezione(StatoFunzionalita.toEnumConstant(confric));
			accordoServizioParteComune.setIdCollaborazione(StatoFunzionalita.toEnumConstant(idcoll));
			accordoServizioParteComune.setConsegnaInOrdine(StatoFunzionalita.toEnumConstant(consord));
			accordoServizioParteComune.setScadenza(scadenza);
			if(sRef!=null){
				IdSoggetto soggRef = new IdSoggetto();
				soggRef.setTipo(sRef.getTipo());
				soggRef.setNome(sRef.getNome());
				accordoServizioParteComune.setSoggettoReferente(soggRef);
			}
			accordoServizioParteComune.setVersione(versione);

			String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(accordoServizioParteComune.getSoggettoReferente().getTipo());

			ValidazioneResult v = this.apcCore.validazione(accordoServizioParteComune, this.soggettiCore);
			if(v.isEsito()==false){
				this.pd.setMessage("[validazione-"+protocollo+"] "+v.getMessaggioErrore());
				if(v.getException()!=null)
					this.log.error("[validazione-"+protocollo+"] "+v.getMessaggioErrore(),v.getException());
				else
					this.log.error("[validazione-"+protocollo+"] "+v.getMessaggioErrore());
				return false;
			}	

			// Validazione wsdl/sbl
			if(validazioneDocumenti && tipoOperazione.equals(TipoOperazione.ADD)){

				byte [] wsdlDefinitorio = wsdldef != null && !wsdldef.trim().replaceAll("\n", "").equals("") ? wsdldef.trim().getBytes() : null;
				byte [] wsdlConcettuale = wsdlconc != null && !wsdlconc.trim().replaceAll("\n", "").equals("") ? wsdlconc.trim().getBytes() : null;
				byte [] wsdlLogicoErogatore = wsdlserv != null && !wsdlserv.trim().replaceAll("\n", "").equals("") ? wsdlserv.trim().getBytes() : null;
				byte [] wsdlLogicoFruitore = wsdlservcorr != null && !wsdlservcorr.trim().replaceAll("\n", "").equals("") ? wsdlservcorr.trim().getBytes() : null;
				byte [] wsblConcettuale = wsblconc != null && !wsblconc.trim().replaceAll("\n", "").equals("") ? wsblconc.trim().getBytes() : null;
				byte [] wsblLogicoErogatore = wsblserv != null && !wsblserv.trim().replaceAll("\n", "").equals("") ? wsblserv.trim().getBytes() : null;
				byte [] wsblLogicoFruitore = wsblservcorr != null && !wsblservcorr.trim().replaceAll("\n", "").equals("") ? wsblservcorr.trim().getBytes() : null;

				accordoServizioParteComune.setByteWsdlDefinitorio(wsdlDefinitorio);
				accordoServizioParteComune.setByteWsdlConcettuale(wsdlConcettuale);
				accordoServizioParteComune.setByteWsdlLogicoErogatore(wsdlLogicoErogatore);
				accordoServizioParteComune.setByteWsdlLogicoFruitore(wsdlLogicoFruitore);

				accordoServizioParteComune.setByteSpecificaConversazioneConcettuale(wsblConcettuale);
				accordoServizioParteComune.setByteSpecificaConversazioneErogatore(wsblLogicoErogatore);
				accordoServizioParteComune.setByteSpecificaConversazioneFruitore(wsblLogicoFruitore);

				v = this.apcCore.validaInterfacciaWsdlParteComune(accordoServizioParteComune, this.soggettiCore);
				if(v.isEsito()==false){
					this.pd.setMessage("[validazione-"+protocollo+"] "+v.getMessaggioErrore());
					if(v.getException()!=null)
						this.log.error("[validazione-"+protocollo+"] "+v.getMessaggioErrore(),v.getException());
					else
						this.log.error("[validazione-"+protocollo+"] "+v.getMessaggioErrore());
					return false;
				}

				v = this.apcCore.validaSpecificaConversazione(accordoServizioParteComune, this.soggettiCore);
				if(v.isEsito()==false){
					this.pd.setMessage("[validazione-"+protocollo+"] "+v.getMessaggioErrore());
					if(v.getException()!=null)
						this.log.error("[validazione-"+protocollo+"] "+v.getMessaggioErrore(),v.getException());
					else
						this.log.error("[validazione-"+protocollo+"] "+v.getMessaggioErrore());
					return false;
				}

			}



			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public void prepareAccordiList(List<AccordoServizioParteComune> lista, ISearch ricerca, String tipoAccordo) throws Exception {
		try {
			ServletUtils.addListElementIntoSession(this.session, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC,
					new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO, tipoAccordo));

			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

			int idLista = Liste.ACCORDI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			String termine = AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo);
			//String parametroSICA = Utilities.getParametroAccordoServizio(tipoAccordo,"&");
			boolean showColonnaServizioComponenti = AccordiServizioParteComuneUtilities.showInformazioniServiziComponenti(tipoAccordo);


			// setto la barra del titolo
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(termine, null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO,null));
			}
			else{
				ServletUtils.setPageDataTitle(this.pd,
						new Parameter(termine, null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO,AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}

			Boolean gestioneInfoProtocollo = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_INFO_PROTOCOLLO);
			Boolean showAccordiAzioni = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_VISUALIZZA_ACCORDI_AZIONI);
			Boolean showAccordiPortTypes = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_VISUALIZZA_ACCORDI_PORT_TYPES);
			Boolean showAccordiCooperazione = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_VISUALIZZA_ACCORDI_COOPERAZIONE);
			Boolean showColonnaAccordiCooperazione = tipoAccordo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_SERVIZIO_COMPOSTO);

			IDAccordoCooperazioneFactory idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, termine, search);
			}

			// setto le label delle colonne
			int totEl = 2;

			//Accordo cooperazione
			if(showColonnaAccordiCooperazione)
				totEl ++;

			if(this.core.isShowGestioneWorkflowStatoDocumenti())
				totEl++;
			if (gestioneInfoProtocollo && showAccordiAzioni)
				totEl++;
			if (showAccordiPortTypes)
				totEl++;
			if (showAccordiCooperazione && showColonnaServizioComponenti)
				totEl++;
			String[] labels = new String[totEl+1];

			labels[0] = AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_NOME;
			//labels[1] = "Descrizione";

			int index = 1;

			// Accordo cooperazione
			if(showColonnaAccordiCooperazione){
				labels[index] = AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_ACCORDO_COOPERAZIONE ;
				index++;
			}

			if(this.core.isShowGestioneWorkflowStatoDocumenti()){
				labels[index] = AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_STATO_PACKAGE;
				index++;
			}

			labels[index] = AccordiServizioParteComuneCostanti.LABEL_ACCORDI_EROGATORI;
			index++;

			if (gestioneInfoProtocollo && showAccordiAzioni) {
				labels[index] = AccordiServizioParteComuneCostanti.LABEL_AZIONI;
				index++;
				if (showAccordiPortTypes){
					labels[index] = AccordiServizioParteComuneCostanti.LABEL_PORT_TYPES;
					index++;
				}
			} else {
				if (showAccordiPortTypes){
					labels[index] = AccordiServizioParteComuneCostanti.LABEL_PORT_TYPES;
					index++;
				}
			}

			if(showAccordiCooperazione && showColonnaServizioComponenti){
				labels[index] = AccordiServizioParteComuneCostanti.LABEL_SERVIZI_COMPONENTI;
				index++;
			}

			labels[index] = AccordiServizioParteComuneCostanti.LABEL_ALLEGATI;
			index++;

			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			// TODO da vedere come recuperare il numero totale di entries

			// inizializzo i dati
			if (lista != null) {
				Iterator<AccordoServizioParteComune> it = lista.iterator();
				AccordoServizioParteComune accordoServizio = null;
				while (it.hasNext()) {
					accordoServizio = it.next();
					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_CHANGE,
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, accordoServizio.getId()+""),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, accordoServizio.getNome()),
							AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo));
					String uriAccordo = this.idAccordoFactory.getUriFromAccordo(accordoServizio);
					de.setValue(uriAccordo);
					de.setIdToRemove("" + accordoServizio.getId());
					de.setToolTip(accordoServizio.getDescrizione());
					e.addElement(de);

					/*de = new DataElement();
					de.setValue(accordoServizio.getDescrizione());
					e.addElement(de);*/

					if(showColonnaAccordiCooperazione){
						de = new DataElement();
						// calcolo l'accordo di cooperazione
						AccordoServizioParteComuneServizioComposto servizioComposto = accordoServizio.getServizioComposto();

						if(servizioComposto  != null){
							AccordoCooperazione accordoCooperazione = this.acCore.getAccordoCooperazione(servizioComposto.getIdAccordoCooperazione());

							String uriAccordoCooperazione = idAccordoCooperazioneFactory.getUriFromAccordo(accordoCooperazione);
							de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_CHANGE,
									new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, accordoCooperazione.getId()+""),
									new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, accordoCooperazione.getNome()));

							de.setValue(uriAccordoCooperazione);
						}
						e.addElement(de);
					}

					if(this.core.isShowGestioneWorkflowStatoDocumenti()){
						de = new DataElement();
						de.setValue(accordoServizio.getStatoPackage());
						e.addElement(de);
					}

					/*if (gestioneWSBL.equals(Costanti.CHECK_BOX_ENABLED)) {
						de = new DataElement();
						AccordoServizioSoggettoReferente assr = accordoServizio.getSoggettoReferente();
						if (assr != null)
							de.setValue(assr.getTipo() + "/" + assr.getNome());
						e.addElement(de);
					}*/

					de = new DataElement();
					de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_EROGATORI_LIST,
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, accordoServizio.getId()+""),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, accordoServizio.getNome()),
							AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo));
					if (contaListe) {
						List<org.openspcoop2.core.registry.Soggetto> tmpLista = this.apcCore.accordiErogatoriList(accordoServizio.getId().intValue(), new Search(true));
						ServletUtils.setDataElementVisualizzaLabel(de,(long)tmpLista.size());
					} else
						ServletUtils.setDataElementVisualizzaLabel(de);
					e.addElement(de);

					if (gestioneInfoProtocollo && showAccordiAzioni) {
						de = new DataElement();
						de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_AZIONI_LIST,
								new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, accordoServizio.getId()+""),
								new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, accordoServizio.getNome()),
								AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo));
						if (contaListe) {
							List<Azione> tmpLista = this.apcCore.accordiAzioniList(accordoServizio.getId().intValue(), new Search(true));
							ServletUtils.setDataElementVisualizzaLabel(de,(long)tmpLista.size());
						} else
							ServletUtils.setDataElementVisualizzaLabel(de);
						e.addElement(de);
					}

					if (showAccordiPortTypes) {
						de = new DataElement();
						de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPES_LIST,
								new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, accordoServizio.getId()+""),
								new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, accordoServizio.getNome()),
								AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo));
						if (contaListe) {
							List<PortType> tmpLista = this.apcCore.accordiPorttypeList(accordoServizio.getId().intValue(), new Search(true));
							ServletUtils.setDataElementVisualizzaLabel(de,(long)tmpLista.size());
						} else
							ServletUtils.setDataElementVisualizzaLabel(de);
						e.addElement(de);
					}

					if (showAccordiCooperazione && showColonnaServizioComponenti){
						de = new DataElement();
						if(accordoServizio.getServizioComposto()!=null){
							de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_COMPONENTI_LIST,
									new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, accordoServizio.getId()+""),
									new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, accordoServizio.getNome()),
									AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo));
							if (contaListe) {
								AccordoServizioParteComuneServizioComposto assc = accordoServizio.getServizioComposto();
								ServletUtils.setDataElementVisualizzaLabel(de,(long)assc.sizeServizioComponenteList());
							} else
								ServletUtils.setDataElementVisualizzaLabel(de);
						}else{
							de.setValue("");
						}
						e.addElement(de);
					}

					//allegati
					de = new DataElement();
					de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_ALLEGATI_LIST,
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, accordoServizio.getId()+""),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, accordoServizio.getNome()),
							AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo));
					if (contaListe) {
						List<org.openspcoop2.core.registry.Documento> tmpLista = this.apcCore.accordiAllegatiList(accordoServizio.getId().intValue(), new Search(true));
						ServletUtils.setDataElementVisualizzaLabel(de,(long)tmpLista.size());
					} else
						ServletUtils.setDataElementVisualizzaLabel(de);
					e.addElement(de);

					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(this.core.isShowPulsanteAggiungiElenchi());

			// preparo bottoni
			if(lista!=null && lista.size()>0){
				if (this.core.isShowPulsantiImportExport()) {

					ExporterUtils exporterUtils = new ExporterUtils(this.archiviCore);
					boolean exists = false;
					if(AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE.equals(tipoAccordo)){
						exists = exporterUtils.existsAtLeastOneExportMpde(ArchiveType.ACCORDO_SERVIZIO_PARTE_COMUNE);
					}
					else{
						exists = exporterUtils.existsAtLeastOneExportMpde(ArchiveType.ACCORDO_SERVIZIO_COMPOSTO);
					}
					if(exists){

						Vector<AreaBottoni> bottoni = new Vector<AreaBottoni>();

						AreaBottoni ab = new AreaBottoni();
						Vector<DataElement> otherbott = new Vector<DataElement>();
						DataElement de = new DataElement();
						de.setValue(AccordiServizioParteComuneCostanti.LABEL_APC_ESPORTA_SELEZIONATI);
						if(AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE.equals(tipoAccordo)){
							de.setOnClick(AccordiServizioParteComuneCostanti.LABEL_APC_ESPORTA_SELEZIONATI_ONCLICK);
						}
						else{
							de.setOnClick(AccordiServizioParteComuneCostanti.LABEL_ASC_ESPORTA_SELEZIONATI_ONCLICK);
						}
						otherbott.addElement(de);
						ab.setBottoni(otherbott);
						bottoni.addElement(ab);

						this.pd.setAreaBottoni(bottoni);

					}

				}
			}

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}


	public boolean accordiComponentiCheckData(TipoOperazione tipoOp,String idServizioComponente)
			throws Exception {

		try{

			if(idServizioComponente==null || "".equals(idServizioComponente) || "-1".equals(idServizioComponente)){
				this.pd.setMessage("E' necessario selezionare un componente.");
				return false;
			}
			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public void prepareAccordiComponentiList(AccordoServizioParteComune as, ISearch ricerca, List<AccordoServizioParteComuneServizioCompostoServizioComponente> lista,String tipoAccordo) throws Exception {
		try {

			ServletUtils.addListElementIntoSession(this.session, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_COMPONENTI,
					new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, ""+as.getId())
					);

			int idLista = Liste.ACCORDI_COMPONENTI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo), null));
			lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
					AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST,
					AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo)
					));

			if (search.equals("")) {
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(AccordiServizioParteComuneCostanti.LABEL_COMPONENTI + " di " + this.idAccordoFactory.getUriFromAccordo(as), null));
			}else {
				lstParam.add(new Parameter(AccordiServizioParteComuneCostanti.LABEL_COMPONENTI + " di " + this.idAccordoFactory.getUriFromAccordo(as),
						AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_COMPONENTI_LIST,
						new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, "" + as.getId()),
						AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo)
						));

				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null)); 
			}

			ServletUtils.setPageDataTitle(this.pd, lstParam);

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription("Componenti contenenti la stringa '" + search + "'");
			}

			// setto le label delle colonne
			String l1 = AccordiServizioParteSpecificaCostanti.LABEL_APS; 
			String[] labels = { l1, AccordiServizioParteSpecificaCostanti.LABEL_APS_SERVIZIO };
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<AccordoServizioParteComuneServizioCompostoServizioComponente> it = lista.iterator();
				while (it.hasNext()) {
					AccordoServizioParteComuneServizioCompostoServizioComponente componente = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					IDServizio idServizio = new IDServizio(componente.getTipoSoggetto(),componente.getNomeSoggetto(),
							componente.getTipo(),componente.getNome());
					IDAccordo idAccordoParteSpecifica = this.apsCore.getIDAccordoServizioParteSpecifica(idServizio);
					de.setValue(this.idAccordoFactory.getUriFromIDAccordo(idAccordoParteSpecifica));
					de.setIdToRemove(""+componente.getIdServizioComponente());
					e.addElement(de);

					de = new DataElement();
					de.setValue(componente.getTipo()+"/"+componente.getNome());
					e.addElement(de);

					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);

			if(this.core.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(as.getStatoPackage())){
				this.pd.setAddButton(false);
				this.pd.setRemoveButton(false);
				this.pd.setSelect(false);
			}else{
				this.pd.setAddButton(true);
				this.pd.setRemoveButton(true);
				this.pd.setSelect(true);
			}

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public Vector<DataElement> addAccordiServizioComponentiToDati(TipoOperazione tipoOp, String idAccordo,
			String idServizioComponente, String tipoAccordo,
			String[] serviziList, String[] serviziListLabel,
			Vector<DataElement> dati) {
		DataElement de = new DataElement();

		de.setValue(idAccordo);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
		dati.addElement(de);

		de = new DataElement();
		de.setValue(tipoAccordo);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_COMPONENTI_TIPO_SICA);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SERVIZIO);
		de.setType(DataElementType.SELECT);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_COMPONENTI_COMPONENTE);
		de.setValues(serviziList);
		de.setLabels(serviziListLabel);
		//de.setOnChange("CambiaAccordoComponente('componentiAdd')");
		if (idServizioComponente != null) {
			de.setSelected(idServizioComponente);
		}
		dati.addElement(de);

		return dati;
	}


	public void prepareAccordiPorttypeOperationMessagePartList(ISearch ricerca, List<MessagePart> lista, AccordoServizioParteComune as,String tipoAccordo,String nomePT, String nomeOp, boolean isMessageInput) throws Exception {
		try {
			String id = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			int idLista = Liste.ACCORDI_PORTTYPE_AZIONI_MESSAGE_INPUT;

			String uri = null;
			uri = this.idAccordoFactory.getUriFromAccordo(as);

			Parameter pIdAccordo  = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id);
			Parameter pNomeAccordo  = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, as.getNome());
			Parameter pNomePt  = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME, nomePT);
			Parameter pNomeOp  = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_NOME, nomeOp);
			Parameter pTipoMsg  = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_TYPE, 
					AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_INPUT);

			if(!isMessageInput){
				pTipoMsg.setValue(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_OUTPUT);
				idLista = Liste.ACCORDI_PORTTYPE_AZIONI_MESSAGE_OUTPUT;
			}

			ServletUtils.addListElementIntoSession(this.session, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE,
					pIdAccordo, AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo),  pNomePt, pNomeOp, pTipoMsg);
			//					new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id),
			//					new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO, tipoAccordo),
			//					new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, uri),
			//					new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME,nomePT));

			//			String nomept = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME);
			//			Hashtable<String, String> campiHidden = new Hashtable<String, String>();
			//			campiHidden.put("nomeop", nomeOp);
			//			this.pd.setHidden(campiHidden);




			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));


			List<Parameter> listaParametri = new ArrayList<Parameter>();


			listaParametri.add(new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo),null));
			listaParametri.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
					AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST , AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo)));

			listaParametri.add(new Parameter(AccordiServizioParteComuneCostanti.LABEL_PORT_TYPES + " di " + uri,AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPES_LIST,
					pIdAccordo,pNomeAccordo,AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo)));

			listaParametri.add(	new Parameter(AccordiServizioParteComuneCostanti.LABEL_AZIONI + " di " + nomePT,AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_LIST,
					pIdAccordo,pNomeAccordo,pNomePt,AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo)));

			listaParametri.add(	new Parameter(nomeOp,AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_CHANGE,
					pIdAccordo,pNomeOp,pNomePt,AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo)));

			String labelMessage = AccordiServizioParteComuneCostanti.LABEL_OPERATION_MESSAGE_INPUT;
			if(!isMessageInput){
				labelMessage = AccordiServizioParteComuneCostanti.LABEL_OPERATION_MESSAGE_OUTPUT;
			}

			// setto la barra del titolo			
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				listaParametri.add(	new Parameter(labelMessage + " di " + nomeOp, null));
			}else{
				listaParametri.add(	new Parameter(labelMessage + " di " + nomeOp,AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE_LIST,
						pIdAccordo,pNomeAccordo,pNomePt, pNomePt , pTipoMsg,AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo)));
				listaParametri.add (new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}

			ServletUtils.setPageDataTitle(this.pd,listaParametri);


			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, AccordiServizioParteComuneCostanti.LABEL_AZIONI, search);
			}

			// setto le label delle colonne
			String[] labels = { AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_NOME };
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<MessagePart> it = lista.iterator();
				while (it.hasNext()) {
					MessagePart op = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					Parameter pPart = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_NAME, op.getName());
					de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE_CHANGE,
							pIdAccordo, pNomeOp, pNomePt, pTipoMsg, pPart,
							AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo)
							);
					de.setValue(op.getName());
					de.setIdToRemove(op.getName());
					e.addElement(de);

					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);

			if(this.core.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(as.getStatoPackage())){
				this.pd.setAddButton(false);
				this.pd.setRemoveButton(false);
				this.pd.setSelect(false);
			}else{
				this.pd.setAddButton(true);
				this.pd.setRemoveButton(true);
				this.pd.setSelect(true);
			}

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public Vector<DataElement> addAccordiPorttypeOperationMessageToDati(TipoOperazione tipoOp, Vector<DataElement> dati,String idAccordo,String tipoAccordo,
			String nomept, String nomeop, String messagePartName, String messagePartType, String messagePartLocalName,
			String messagePartNS, String tipoMessage){

		DataElement de = new DataElement();

		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_INFORMAZIONI_WSDL_PART);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
		de.setValue(idAccordo);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
		dati.addElement(de);

		de = new DataElement();
		de.setValue(tipoAccordo);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
		de.setSize(this.getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setValue(nomept);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME);
		de.setSize(this.getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setValue(nomeop);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_NOME);
		dati.addElement(de);

		de = new DataElement();
		de.setValue(tipoMessage);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_TYPE);
		dati.addElement(de);

		de = new DataElement();
		de.setValue(messagePartName);
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_NAME);
		if(tipoOp.equals(TipoOperazione.ADD)){
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true); 
		}else{
			de.setType(DataElementType.TEXT);
		}
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_NAME);
		de.setSize(this.getSize()); 
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_TYPE);
		de.setType(DataElementType.SELECT);
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_TYPE);
		de.setValues(AccordiServizioParteComuneCostanti.PORT_TYPE_OPERATION_MESSAGE_PART_TYPE);
		de.setLabels(AccordiServizioParteComuneCostanti.LABEL_PORT_TYPE_OPERATION_MESSAGE_PART_TYPE);
		de.setSelected(messagePartType);
		de.setSize(this.getSize()); 
		dati.addElement(de);

		de = new DataElement();
		de.setValue(messagePartLocalName);
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_LOCAL_NAME);
		de.setType(DataElementType.TEXT_EDIT);
		de.setRequired(true); 
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_LOCAL_NAME);
		de.setSize(this.getSize()); 
		dati.addElement(de);

		de = new DataElement();
		de.setValue(messagePartNS);
		de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_NS);
		de.setType(DataElementType.TEXT_EDIT);
		de.setRequired(true); 
		de.setName(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_NS);
		de.setSize(this.getSize()); 
		dati.addElement(de);

		return dati;
	}


	public boolean accordiPorttypeOperationMessageCheckData(TipoOperazione tipoOperazione)
			throws Exception {

		try{

			String id = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			int idInt = Integer.parseInt(id);
			String nomept = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME);
			String nomeop = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_NOME);
			String tipoAccordo = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			if("".equals(tipoAccordo))
				tipoAccordo = null;

			String tipoMessage = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_TYPE);

			boolean isMessageInput = tipoMessage.equals(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_INPUT);

			String messagePartName = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_NAME);
			String messagePartLocalName = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_LOCAL_NAME);
			String messagePartNs = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_NS);
			//			String messagePartType = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_TYPE);

			AccordoServizioParteComune as = this.apcCore.getAccordoServizio(idInt);

			// Prendo il port-type e l'operation
			PortType pt = null;
			Operation operation = null;
			for (int i = 0; i < as.sizePortTypeList(); i++) {
				pt = as.getPortType(i);
				if (nomept.equals(pt.getNome())){
					for (Operation opTmp : pt.getAzioneList()) {
						if(opTmp.getNome().equals(nomeop)){
							operation = opTmp;
							break;
						}
					}
				}
			}

			Message m = null;
			if(isMessageInput)
				m = operation.getMessageInput();
			else 
				m = operation.getMessageOutput();

			if (messagePartName == null || messagePartName.equals("")) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare il name.");
				return false;
			}

			if (messagePartName.indexOf(" ") != -1 ) {
				this.pd.setMessage("Il campo name non pu&ograve; contenere spazi");
				return false;
			}


			if (messagePartLocalName == null || messagePartLocalName.equals("")) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare il localname.");
				return false;
			}

			if (messagePartLocalName.indexOf(" ") != -1 ) {
				this.pd.setMessage("Il campo localname non pu&ograve; contenere spazi");
				return false;
			}

			if (messagePartNs == null || messagePartNs.equals("")) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare il namespace.");
				return false;
			}

			if (messagePartNs.indexOf(" ") != -1 ) {
				this.pd.setMessage("Il campo namespace non pu&ograve; contenere spazi");
				return false;
			}	

			try{
				URI.create(messagePartNs);
			}catch(Exception e){
				this.pd.setMessage("Il campo namespacezio non contiene una URI valida");
				return false;
			}


			// controllo esistenza
			if (tipoOperazione.equals(TipoOperazione.ADD)) {
				if(m!=null){
					for (MessagePart part : m.getPartList()) {
						if(part.getName().equals(messagePartName)){
							this.pd.setMessage("Il part '" + nomeop + "' &egrave; gi&agrave; stata associato al "+(isMessageInput ? "messaggio di input." : "messaggio di output."));
							return false;
						}
					}
				}
			}

			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
}
