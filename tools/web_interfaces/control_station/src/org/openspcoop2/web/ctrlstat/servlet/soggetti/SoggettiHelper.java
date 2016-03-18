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
package org.openspcoop2.web.ctrlstat.servlet.soggetti;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ExporterUtils;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddTipologia;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
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
 * SoggettiHelper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoggettiHelper extends ConsoleHelper {

	private ConnettoriHelper connettoriHelper = null;
	public SoggettiHelper(HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(request, pd,  session);
		this.connettoriHelper = new ConnettoriHelper(request, pd, session);
	}

	public Vector<DataElement> addSoggettiToDati(TipoOperazione tipoOp,Vector<DataElement> dati, String nomeprov, String tipoprov, String portadom, String descr, 
			boolean isRouter, List<String> tipiSoggetti, String profilo, boolean privato, String codiceIpa, List<String> versioni, boolean isSupportatoCodiceIPA,
			String [] pddList,String nomePddGestioneLocale, List<String> listaTipiProtocollo, String protocollo) {
		return addSoggettiToDati(tipoOp, dati, nomeprov, tipoprov, portadom, descr, 
				isRouter, tipiSoggetti, profilo, privato, codiceIpa, versioni, isSupportatoCodiceIPA, pddList, nomePddGestioneLocale,
				null,null,null,null,null,
				-1,null,-1,null,listaTipiProtocollo,protocollo);
	}
	public Vector<DataElement> addSoggettiToDati(TipoOperazione tipoOp,Vector<DataElement> dati, String nomeprov, String tipoprov, String portadom, String descr, 
			boolean isRouter, List<String> tipiSoggetti, String profilo, boolean privato, String codiceIpa, List<String> versioni, boolean isSupportatoCodiceIPA,
			String [] pddList,String nomePddGestioneLocale,
			String pdd, String id, String oldnomeprov, String oldtipoprov, org.openspcoop2.core.registry.Connettore connettore,
			long numPD,String pd_url_prefix_rewriter,long numPA, String pa_url_prefix_rewriter, List<String> listaTipiProtocollo, String protocollo) {

		Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

		if(TipoOperazione.CHANGE.equals(tipoOp)){
			DataElement de = new DataElement();
			de.setLabel(SoggettiCostanti.PARAMETRO_SOGGETTO_ID);
			de.setValue(id);
			de.setType("hidden");
			de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_ID);
			dati.addElement(de);
		}

		if(TipoOperazione.ADD.equals(tipoOp)){
			if(this.core.isRegistroServiziLocale()){
				DataElement de = new DataElement();
				de.setLabel(PddCostanti.LABEL_PORTA_DI_DOMINIO);
				de.setType(DataElementType.SELECT);
				de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_PDD);
				de.setValues(pddList);
				if (this.core.isSinglePdD()) {
					if(nomePddGestioneLocale!=null)
						de.setSelected(nomePddGestioneLocale);
				}else{
					de.setRequired(true);
				}
				dati.addElement(de);
			}
		}
		else{		
			if(this.core.isSinglePdD()){
				if(this.core.isRegistroServiziLocale()){
					DataElement de = new DataElement();
					de.setLabel(PddCostanti.LABEL_PORTA_DI_DOMINIO);
					de.setType(DataElementType.SELECT);
					de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_PDD);
					de.setValues(pddList);
					de.setSelected(pdd);
					dati.addElement(de);
				}
			}else{
				DataElement de = new DataElement();
				de.setLabel(PddCostanti.LABEL_PORTA_DI_DOMINIO);
				de.setType(DataElementType.TEXT);
				de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_PDD);
				de.setValue(pdd);
				dati.addElement(de);
			}
		}

		DataElement de = new DataElement();
		de.setLabel(SoggettiCostanti.LABEL_SOGGETTO);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);


		de = new DataElement();
		de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_PROTOCOLLO);
		if(TipoOperazione.ADD.equals(tipoOp)){
			if(listaTipiProtocollo != null && listaTipiProtocollo.size() > 1){
				de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_PROTOCOLLO);
				de.setValues(listaTipiProtocollo);
				de.setSelected(protocollo);
				de.setType(DataElementType.SELECT);
				de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_PROTOCOLLO);
				de.setPostBack(true);
			} else {
				de.setValue(protocollo);
				de.setType(DataElementType.HIDDEN);
				de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_PROTOCOLLO);
			}
		} else {
			if(listaTipiProtocollo != null && listaTipiProtocollo.size() > 1){
				de.setValue(protocollo);
				de.setType(DataElementType.TEXT);
				de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_PROTOCOLLO);
			} else {
				de.setValue(protocollo);
				de.setType(DataElementType.HIDDEN);
				de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_PROTOCOLLO);
			}
		}
		de.setSize(this.getSize());
		dati.addElement(de);

		String[] tipiLabel = new String[tipiSoggetti.size()];
		for (int i = 0; i < tipiSoggetti.size(); i++) {
			String nomeTipo = tipiSoggetti.get(i);
			tipiLabel[i] = nomeTipo;
		}

		String[] versioniLabel = new String[versioni.size()];
		for (int i = 0; i < versioni.size(); i++) {
			String versione = versioni.get(i);
			versioniLabel[i] = versione;
		}

		de = new DataElement();
		de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_TIPO);
		de.setValues(tipiLabel);
		de.setSelected(tipoprov);
		de.setType(DataElementType.SELECT);
		de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO);
		de.setSize(this.getSize());
		//		de.setOnChange("CambiaDatiSoggetto('" + tipoOp + "')");
		de.setPostBack(true);
		dati.addElement(de);
		//}

		de = new DataElement();
		de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_NOME);
		de.setValue(nomeprov);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME);
		de.setSize(this.getSize());
		de.setRequired(true);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_CODICE_PORTA);
		de.setValue(portadom);
		if (InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
			de.setType(DataElementType.HIDDEN);
		}else{
			de.setType(DataElementType.TEXT_EDIT);
		}
		de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_CODICE_PORTA);
		de.setSize(this.getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_CODICE_IPA);
		de.setValue(codiceIpa);
		if (!isSupportatoCodiceIPA) {
			de.setType(DataElementType.HIDDEN);
		}else{
			if(this.core.isRegistroServiziLocale()){
				de.setType(DataElementType.TEXT_EDIT);
			}else{
				de.setType(DataElementType.HIDDEN);
			}
		}
		de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_CODICE_IPA);
		de.setSize(this.getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_DESCRIZIONE);
		de.setValue(descr);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_DESCRIZIONE);
		de.setSize(this.getSize());
		dati.addElement(de);


		User user = ServletUtils.getUserFromSession(this.session);
		de = new DataElement();
		de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_VERSIONE_PROTOCOLLO);
		de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_VERSIONE_PROTOCOLLO);

		if(this.core.isRegistroServiziLocale() && InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())){
			de.setValues(versioniLabel);
			de.setSelected(profilo);
			de.setType(DataElementType.SELECT);
		}
		else{
			de.setValue(profilo);
			de.setType(DataElementType.HIDDEN);
		}
		de.setSize(this.getSize());
		dati.addElement(de);
		//}

		de = new DataElement();
		de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_IS_PRIVATO);
		if (this.core.isShowFlagPrivato() && !InterfaceType.STANDARD.equals(user.getInterfaceType()) && this.core.isRegistroServiziLocale() ) {
			de.setType(DataElementType.CHECKBOX);
		} else {
			de.setType(DataElementType.HIDDEN);
		}
		de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_IS_PRIVATO);
		de.setSelected(privato ? "yes" : "");
		de.setSize(this.getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_IS_ROUTER);
		de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_IS_ROUTER);
		//if (!this.core.isSinglePdD() && !InterfaceType.STANDARD.equals(user.getInterfaceType())) {
		// Un router lo si puo' voler creare anche in singlePdD.
		if (!InterfaceType.STANDARD.equals(user.getInterfaceType())) {
			de.setType(DataElementType.CHECKBOX);
			if (isRouter) {
				de.setSelected(true);
			}
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(Costanti.CHECK_BOX_DISABLED);
		}
		dati.addElement(de);


		if(TipoOperazione.CHANGE.equals(tipoOp)){

			if(this.core.isRegistroServiziLocale()){
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_ENDPOINT,
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID,id+""),
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME,oldnomeprov),
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO,oldtipoprov));
				Utilities.setDataElementLabelTipoConnettore(de, connettore);
				dati.addElement(de);
			}

			de = new DataElement();
			de.setLabel(SoggettiCostanti.LABEL_CLIENT);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);

			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST,
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,id+""),
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_SOGGETTO,oldnomeprov),
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_SOGGETTO,oldtipoprov));
			if (contaListe) {
				ServletUtils.setDataElementVisualizzaLabel(de,numPD);
			} else
				ServletUtils.setDataElementVisualizzaLabel(de);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_PD_URL_PREFIX_REWRITER);
			if (InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType()) == false) {	
				de.setType(DataElementType.TEXT_EDIT);
			}else{
				de.setType(DataElementType.HIDDEN);
			}
			de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_PD_URL_PREFIX_REWRITER);
			de.setValue(pd_url_prefix_rewriter);
			de.setSize(this.getSize());
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(SoggettiCostanti.LABEL_SERVER);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);

			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,id+""),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_SOGGETTO,oldnomeprov),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TIPO_SOGGETTO,oldtipoprov));
			if (contaListe) {
				ServletUtils.setDataElementVisualizzaLabel(de,numPA);
			} else
				ServletUtils.setDataElementVisualizzaLabel(de);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_PA_URL_PREFIX_REWRITER);
			if (InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType()) == false) {	
				de.setType(DataElementType.TEXT_EDIT);
			}else{
				de.setType(DataElementType.HIDDEN);
			}
			de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_PA_URL_PREFIX_REWRITER);
			de.setValue(pa_url_prefix_rewriter);
			de.setSize(this.getSize());
			dati.addElement(de);

		}

		return dati;
	}

	boolean soggettiCheckData(TipoOperazione tipoOp) throws Exception {
		try {
			String id = this.request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID);
			int idInt = 0;
			if (tipoOp.equals(TipoOperazione.CHANGE)) {
				idInt = Integer.parseInt(id);
			}
			String nomeprov = this.request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME);
			String tipoprov = this.request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO);
			String codiceIpa = this.request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_CODICE_IPA);
			String pd_url_prefix_rewriter = this.request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_PD_URL_PREFIX_REWRITER);
			String pa_url_prefix_rewriter = this.request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_PA_URL_PREFIX_REWRITER);

			// Campi obbligatori
			if (nomeprov.equals("") || tipoprov.equals("") ) {
				String tmpElenco = "";
				if (nomeprov.equals("")) {
					tmpElenco = SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_NOME;
				}
				if (tipoprov.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_TIPO;
					} else {
						tmpElenco = tmpElenco + ", "+SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_TIPO;
					}
				}
				this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if ((nomeprov.indexOf(" ") != -1) || (tipoprov.indexOf(" ") != -1)) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}

			// Il nome deve contenere solo lettere e numeri
			if (!RegularExpressionEngine.isMatch(nomeprov,"^[0-9A-Za-z]+$")) {
				this.pd.setMessage("Il nome del soggetto dev'essere formato solo da caratteri e cifre");
				return false;
			}
			// Il tipo deve contenere solo lettere e numeri
			if (!RegularExpressionEngine.isMatch(tipoprov,"^[0-9A-Za-z]+$")) {
				this.pd.setMessage("Il tipo del soggetto dev'essere formato solo da caratteri e cifre");
				return false;
			}

			// check Codice IPA
			// TODO CODICE IPA
			/*try{
				if(codiceIpa!=null && !"".equals(codiceIpa)){
					SICAtoOpenSPCoopUtilities.validateIDSoggettoSICA(codiceIpa);
				}
			}catch(Exception e){
				this.pd.setMessage("Codice IPA non corretto: " + e.getMessage());
				return false;
			}*/
			// TODO CODICE IPA

			// Controllo che eventuali PdUrlPrefixRewriter o PaUrlPrefixRewriter rispettino l'espressione regolare: [A-Za-z]+:\/\/(.*)
			if(pd_url_prefix_rewriter!=null && !"".equals(pd_url_prefix_rewriter)){
				if(RegularExpressionEngine.isMatch(pd_url_prefix_rewriter, "[A-Za-z]+:\\/\\/(.*)")==false){
					this.pd.setMessage("Il campo UrlPrefix rewriter del profilo client contiene un valore errato. Il valore atteso deve seguire la sintassi: "+
							StringEscapeUtils.escapeHtml("protocoll://hostname[:port][/*]"));
					return false;
				}
			}
			if(pa_url_prefix_rewriter!=null && !"".equals(pa_url_prefix_rewriter)){
				if(RegularExpressionEngine.isMatch(pa_url_prefix_rewriter, "[A-Za-z]+:\\/\\/(.*)")==false){
					this.pd.setMessage("Il campo UrlPrefix rewriter del profilo server contiene un valore errato. Il valore atteso deve seguire la sintassi: "+
							StringEscapeUtils.escapeHtml("protocoll://hostname[:port][/*]"));
					return false;
				}
			}

			// Se tipoOp = add o tipoOp = change, controllo che non esistano
			// altri soggetti con stessi nome e tipo
			// Se tipoOp = change, devo fare attenzione a non escludere nome e
			// tipo del soggetto selezionato
			if (tipoOp.equals(TipoOperazione.ADD) || tipoOp.equals(TipoOperazione.CHANGE)) {
				int idSogg = 0;
				IDSoggetto ids = new IDSoggetto(tipoprov, nomeprov);
				boolean existsSogg = this.soggettiCore.existsSoggetto(ids);
				if (existsSogg) {
					if(this.core.isRegistroServiziLocale()){
						Soggetto mySogg = this.soggettiCore.getSoggettoRegistro(ids);
						idSogg = mySogg.getId().intValue();
					}else{
						org.openspcoop2.core.config.Soggetto mySogg = this.soggettiCore.getSoggetto(ids);
						idSogg = mySogg.getId().intValue();
					}
				}
				if ((idSogg != 0) && (tipoOp.equals(TipoOperazione.ADD) || (tipoOp.equals(TipoOperazione.CHANGE) && (idInt != idSogg)))) {
					this.pd.setMessage("Esiste gi&agrave; un soggetto con nome " + nomeprov + " e tipo " + tipoprov);
					return false;
				}

				// Controllo che il codiceIPA non sia gia utilizzato. Il fatto che non esista in base al nome, e' gia garantito rispetto all'univocita' del nome.
				if(this.core.isRegistroServiziLocale()){
					if(codiceIpa!=null && !"".equals(codiceIpa)){
						if(tipoOp.equals(TipoOperazione.ADD)){
							if(this.soggettiCore.existsSoggetto(codiceIpa)){
								this.pd.setMessage("Esiste gi&agrave; un soggetto con Codice IPA: " + codiceIpa);
								return false;
							}
						}
						else{
							Soggetto mySogg = null;
							try{
								mySogg = this.soggettiCore.getSoggettoByCodiceIPA(codiceIpa);
							}catch(DriverRegistroServiziNotFound dnot){}
							if(mySogg!=null){
								if(mySogg.getId()!=idInt){
									this.pd.setMessage("Esiste gi&agrave; un soggetto con Codice IPA: " + codiceIpa);
									return false;
								}
							}
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

	public void prepareSoggettiList(List<org.openspcoop2.core.registry.Soggetto> lista, ISearch ricerca) throws Exception {
		try {
			ServletUtils.addListElementIntoSession(this.session, SoggettiCostanti.OBJECT_NAME_SOGGETTI);

			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
			
			int idLista = Liste.SOGGETTI;
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
						new Parameter(SoggettiCostanti.LABEL_SOGGETTI, null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO,null));
			}
			else{
				ServletUtils.setPageDataTitle(this.pd,
						new Parameter(SoggettiCostanti.LABEL_SOGGETTI, null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO,SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}


			// setto le label delle colonne
			int totEl = 6;
			String[] labels = new String[totEl];
			labels[0] = SoggettiCostanti.LABEL_SOGGETTO;
			labels[1] = PddCostanti.LABEL_PORTA_DI_DOMINIO;
			labels[2] = ConnettoriCostanti.LABEL_CONNETTORE;
			labels[3] = ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI;
			labels[4] = PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE;
			labels[5] = PorteDelegateCostanti.LABEL_PORTE_DELEGATE;
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, SoggettiCostanti.LABEL_SOGGETTI, search);
			}

			// Prendo la lista di pdd dell'utente connesso
			/*List<String> nomiPdd = new ArrayList<String>();
			List<PdDControlStation> listaPdd = this.core.pddList(superUser, new Search());
			Iterator<PdDControlStation> itP = listaPdd.iterator();
			while (itP.hasNext()) {
				PdDControlStation pds = (PdDControlStation) itP.next();
				nomiPdd.add(pds.getNome());
			}*/

			Iterator<org.openspcoop2.core.registry.Soggetto> it = lista.listIterator();
			while (it.hasNext()) {
				org.openspcoop2.core.registry.Soggetto elem = it.next();

				PdDControlStation pdd = null;
				String nomePdD = elem.getPortaDominio();
				if (nomePdD!=null && (!nomePdD.equals("-")) )
					pdd = this.pddCore.getPdDControlStation(nomePdD);
				boolean pddEsterna = false;
				if( (pdd==null) || PddTipologia.ESTERNO.toString().equals(pdd.getTipo())){
					pddEsterna = true;
				}


				Vector<DataElement> e = new Vector<DataElement>();

				DataElement de = new DataElement();
				de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE,
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID,elem.getId()+""),
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME,elem.getNome()),
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO,elem.getTipo()));
				de.setValue(elem.getTipo() + "/" + elem.getNome());
				de.setIdToRemove(elem.getId().toString());
				de.setToolTip(elem.getCodiceIpa());
				e.addElement(de);

				de = new DataElement();
				if (pdd != null && (!nomePdD.equals("-"))){
					if (!nomePdD.equals("-")){
						//if (nomiPdd.contains(nomePdD)) {
						if(this.core.isSinglePdD()){
							de.setUrl(PddCostanti.SERVLET_NAME_PDD_SINGLEPDD_CHANGE,
									new Parameter(PddCostanti.PARAMETRO_PDD_ID,pdd.getId()+""),
									new Parameter(PddCostanti.PARAMETRO_PDD_NOME,pdd.getNome()));
						}else{
							de.setUrl(PddCostanti.SERVLET_NAME_PDD_CHANGE,
									new Parameter(PddCostanti.PARAMETRO_PDD_ID,pdd.getId()+""));
						}
						//}
					}
					de.setValue(nomePdD);
				}
				else{
					de.setValue("-");
				}
				e.addElement(de);

				de = new DataElement();
				de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_ENDPOINT,
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID,elem.getId()+""),
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME,elem.getNome()),
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO,elem.getTipo()));
				ServletUtils.setDataElementVisualizzaLabel(de);
				e.addElement(de);

				
				//Servizi Appicativi
				de = new DataElement();
				if (pddEsterna) {
					de.setType(DataElementType.TEXT);
					de.setValue("-");
				}
				else {
					de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST,
							new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,elem.getId()+""));
					if (contaListe) {
						List<ServizioApplicativo> lista1 = this.saCore.soggettiServizioApplicativoList(new Search(true), elem.getId());
						int numSA = lista1.size();
						ServletUtils.setDataElementVisualizzaLabel(de,(long)numSA);
					} else
						ServletUtils.setDataElementVisualizzaLabel(de);
				}
				e.addElement(de);
				
				
				de = new DataElement();
				if (pddEsterna) {
					// se la pdd e' esterna non e' possibile
					// inseririre porte applicative
					de.setType(DataElementType.TEXT);
					de.setValue("-");
				} else {
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST,
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,elem.getId()+""),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_SOGGETTO,elem.getNome()),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TIPO_SOGGETTO,elem.getTipo()));
					if (contaListe) {
						List<PortaApplicativa> lista1 = this.porteApplicativeCore.porteAppList(elem.getId().intValue(), new Search(true));
						int numPA = lista1.size();
						ServletUtils.setDataElementVisualizzaLabel(de,(long)numPA);
					} else
						ServletUtils.setDataElementVisualizzaLabel(de);

				}
				e.addElement(de);

				de = new DataElement();
				if (pddEsterna) {
					// se la pdd e' esterna non e' possibile
					// inseririre porte delegate
					de.setType(DataElementType.TEXT);
					de.setValue("-");
				} else {
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,elem.getId()+""),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_SOGGETTO,elem.getNome()),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_SOGGETTO,elem.getTipo()));
					if (contaListe) {
						List<PortaDelegata> lista1 = this.porteDelegateCore.porteDelegateList(elem.getId().intValue(), new Search(true));
						int numPD = lista1.size();
						ServletUtils.setDataElementVisualizzaLabel(de,(long)numPD);
					} else
						ServletUtils.setDataElementVisualizzaLabel(de);
				}
				e.addElement(de);


				dati.addElement(e);
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(this.core.isShowPulsanteAggiungiElenchi());

			// preparo bottoni
			if(lista!=null && lista.size()>0){
				if (this.core.isShowPulsantiImportExport()) {

					ExporterUtils exporterUtils = new ExporterUtils(this.archiviCore);
					if(exporterUtils.existsAtLeastOneExportMpde(ArchiveType.SOGGETTO)){

						Vector<AreaBottoni> bottoni = new Vector<AreaBottoni>();

						AreaBottoni ab = new AreaBottoni();
						Vector<DataElement> otherbott = new Vector<DataElement>();
						DataElement de = new DataElement();
						de.setValue(SoggettiCostanti.LABEL_SOGGETTI_ESPORTA_SELEZIONATI);
						de.setOnClick(SoggettiCostanti.LABEL_SOGGETTI_ESPORTA_SELEZIONATI_ONCLICK);
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

	public void prepareSoggettiConfigList(List<org.openspcoop2.core.config.Soggetto> lista, ISearch ricerca) throws Exception {
		try {
			ServletUtils.addListElementIntoSession(this.session, SoggettiCostanti.OBJECT_NAME_SOGGETTI);

			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

			int idLista = Liste.SOGGETTI;
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
						new Parameter(SoggettiCostanti.LABEL_SOGGETTI, null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO,null));
			}
			else{
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(SoggettiCostanti.LABEL_SOGGETTI, null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO,SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}

			// setto le label delle colonne
			int totEl = 4;
			String[] labels = new String[totEl];
			labels[0] = SoggettiCostanti.LABEL_SOGGETTO;
			labels[1] = PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE;
			labels[2] = PorteDelegateCostanti.LABEL_PORTE_DELEGATE;
			labels[3] = ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI;
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, SoggettiCostanti.LABEL_SOGGETTI, search);
			}

			Iterator<org.openspcoop2.core.config.Soggetto> it = lista.listIterator();
			while (it.hasNext()) {
				org.openspcoop2.core.config.Soggetto elem = (org.openspcoop2.core.config.Soggetto) it.next();

				Vector<DataElement> e = new Vector<DataElement>();

				//Soggetto
				DataElement de = new DataElement();
				de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE,
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID,elem.getId()+""),
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME,elem.getNome()),
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO,elem.getTipo()));
				de.setValue(elem.getTipo() + "/" + elem.getNome());
				de.setIdToRemove(elem.getId().toString());
				e.addElement(de);

				//Porte Applicative
				de = new DataElement();
				de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST,
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,elem.getId()+""),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_SOGGETTO,elem.getNome()),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TIPO_SOGGETTO,elem.getTipo()));
				if (contaListe) {
					List<PortaApplicativa> lista1 = this.porteApplicativeCore.porteAppList(elem.getId().intValue(), new Search(true));
					int numPA = lista1.size();
					ServletUtils.setDataElementVisualizzaLabel(de,(long)numPA);
				} else
					ServletUtils.setDataElementVisualizzaLabel(de);
				e.addElement(de);

				//Porte Delegate
				de = new DataElement();
				de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST,
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,elem.getId()+""),
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_SOGGETTO,elem.getNome()),
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_SOGGETTO,elem.getTipo()));
				if (contaListe) {
					List<PortaDelegata> lista1 = this.porteDelegateCore.porteDelegateList(elem.getId().intValue(), new Search(true));
					int numPD = lista1.size();
					ServletUtils.setDataElementVisualizzaLabel(de,(long)numPD);
				} else
					ServletUtils.setDataElementVisualizzaLabel(de);
				e.addElement(de);


				//Servizi Appicativi
				de = new DataElement();
				de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST,
						new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,elem.getId()+""));
				if (contaListe) {
					List<ServizioApplicativo> lista1 = this.saCore.soggettiServizioApplicativoList(new Search(true), elem.getId());
					int numSA = lista1.size();
					ServletUtils.setDataElementVisualizzaLabel(de,(long)numSA);
				} else
					ServletUtils.setDataElementVisualizzaLabel(de);
				e.addElement(de);

				dati.addElement(e);
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(this.core.isShowPulsanteAggiungiElenchi());

			// preparo bottoni
			if(lista!=null && lista.size()>0){
				if (this.core.isShowPulsantiImportExport()) {

					ExporterUtils exporterUtils = new ExporterUtils(this.archiviCore);
					if(exporterUtils.existsAtLeastOneExportMpde(ArchiveType.SOGGETTO)){

						Vector<AreaBottoni> bottoni = new Vector<AreaBottoni>();

						AreaBottoni ab = new AreaBottoni();
						Vector<DataElement> otherbott = new Vector<DataElement>();
						DataElement de = new DataElement();
						de.setValue(SoggettiCostanti.LABEL_SOGGETTI_ESPORTA_SELEZIONATI);
						de.setOnClick(SoggettiCostanti.LABEL_SOGGETTI_ESPORTA_SELEZIONATI_ONCLICK);
						otherbott.addElement(de);
						ab.setBottoni(otherbott);
						bottoni.addElement(ab);

						this.pd.setAreaBottoni(bottoni);

					}

				}
			}

		} catch (Exception e) {
			this.log.error("Exception prepareSoggetti(Config)List: " + e.getMessage(), e);
			throw new Exception(e);
		}

	}



	public boolean soggettiEndPointCheckData(TipoOperazione tipoOp,List<ExtendedConnettore> listExtendedConnettore) throws Exception {
		try {
			String id = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ID);
			int idInt = 0;
			if (tipoOp.equals(TipoOperazione.CHANGE)) {
				idInt = Integer.parseInt(id);
			}
			//String endpointtype = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
			String endpointtype = this.connettoriHelper.readEndPointType();

			if (!this.connettoriHelper.endPointCheckData(listExtendedConnettore)) {
				return false;
			}

			// Se il connettore e' disabilitato devo controllare che i
			// connettore dei suoi servizi non siano disabilitati
			if (endpointtype.equals(TipiConnettore.DISABILITATO.toString())) {
				boolean trovatoServ = this.soggettiCore.existsSoggettoServiziWithoutConnettore(idInt);
				if (trovatoServ) {
					this.pd.setMessage("Il connettore deve essere specificato in quanto alcuni servizi del soggetto non hanno un connettore definito");
					return false;
				}
			}

			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
}
