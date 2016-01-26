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


package org.openspcoop2.web.ctrlstat.servlet.pa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
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
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaApplicativaServizio;
import org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.MTOMProcessorType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaServizi;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * porteAppChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteApplicativeChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);


		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_USA_ID_SOGGETTO , session);


		try {
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			String nomePorta = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA);
			String idPorta = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			String descr = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_DESCRIZIONE);
			String soggvirt = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SOGGETTO_VIRTUALE);
			IDSoggetto idSoggettoVirtuale = null;
			if ((soggvirt != null) && !soggvirt.equals("") && !soggvirt.equals("-")) {
				idSoggettoVirtuale = new IDSoggetto(soggvirt.split("/")[0],soggvirt.split("/")[1]);
			}
			String servizio = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SERVIZIO);
			String azione = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AZIONE);
			String stateless = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_STATELESS);
			String behaviour = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_BEHAVIOUR);
			String gestBody = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_GESTIONE_BODY);
			String gestManifest = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_GESTIONE_MANIFEST);
			String ricsim = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_RICEVUTA_ASINCRONA_SIMMETRICA);
			String ricasim = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_RICEVUTA_ASINCRONA_ASIMMETRICA);
			String xsd = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_XSD);
			String tipoValidazione = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TIPO_VALIDAZIONE);
			String autorizzazioneContenuti = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI);
			String scadcorr = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SCADENZA_CORRELAZIONE_APPLICATIVA);
			String integrazione = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_INTEGRAZIONE);
			String applicaMTOM = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_APPLICA_MTOM);
			String servizioApplicativo = request.getParameter(CostantiControlStation.PARAMETRO_SERVIZIO_APPLICATIVO);

			// check su oldNomePD
			PageData pdOld =  ServletUtils.getPageDataFromSession(session);
			String oldNomePA = pdOld.getHidden(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_OLD_NOME_PA);
			oldNomePA = (((oldNomePA != null) && !oldNomePA.equals("")) ? oldNomePA : nomePorta);
			
			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteApplicativeCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteApplicativeCore);
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteApplicativeCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(porteApplicativeCore);

			// Prendo la porta applicativa
			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));

			// Prendo nome, tipo e pdd del soggetto
			String tipoNomeSoggettoProprietario = null;
			String tipoSoggettoProprietario = null;
			String nomeSoggettoProprietario = null;
			if(porteApplicativeCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(soggInt);
				tipoNomeSoggettoProprietario = soggetto.getTipo() + "/" + soggetto.getNome();
				tipoSoggettoProprietario = soggetto.getTipo();
				nomeSoggettoProprietario = soggetto.getNome();
			}else{
				org.openspcoop2.core.config.Soggetto soggetto = soggettiCore.getSoggetto(soggInt);
				tipoNomeSoggettoProprietario = soggetto.getTipo() + "/" + soggetto.getNome();
				tipoSoggettoProprietario = soggetto.getTipo();
				nomeSoggettoProprietario = soggetto.getNome();
			}

			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoSoggettoProprietario);
			//			List<String> tipiSoggettiCompatibiliAccordo = soggettiCore.getTipiSoggettiGestitiProtocollo(protocollo);
			//			List<String> tipiServizioCompatibiliAccordo = apsCore.getTipiServiziGestitiProtocollo(protocollo);
			// String pdd = ss.getServer();

			// Informazioni sul numero di ServiziApplicativi, Correlazione Applicativa e stato Message-Security
			int numSA =pa.sizeServizioApplicativoList();
			
			String[] servizioApplicativoList = null;
			if(numSA<=1){
				if(servizioApplicativo==null || "".equals(servizioApplicativo)){
					if(numSA==1){
						servizioApplicativo = pa.getServizioApplicativo(0).getNome();
					}
					else{
						servizioApplicativo = "-";
					}
				}
				String[]  servizioApplicativoList_tmp = PorteApplicativeServizioApplicativoAdd.loadSAErogatori(pa, saCore, soggInt, false);
				servizioApplicativoList = new String[servizioApplicativoList_tmp.length+1];
				servizioApplicativoList[0] = "-";
				for (int i = 0; i < servizioApplicativoList_tmp.length; i++) {
					servizioApplicativoList[(i+1)] = servizioApplicativoList_tmp[i];
				}
			}
			else{
				servizioApplicativo = null;
			}
			
			String statoMessageSecurity  = pa.getStatoMessageSecurity() ;
			int numCorrelazioneReq =0; 
			if(pa.getCorrelazioneApplicativa() != null)
				numCorrelazioneReq  = pa.getCorrelazioneApplicativa().sizeElementoList();

			int numCorrelazioneRes =0;
			if(pa.getCorrelazioneApplicativaRisposta() != null)
				numCorrelazioneRes = pa.getCorrelazioneApplicativaRisposta().sizeElementoList();

			int numProprProt = pa.sizeProprietaProtocolloList();

			// Stato MTOM
			boolean isMTOMAbilitatoReq = false;
			boolean isMTOMAbilitatoRes= false;
			if(pa.getMtomProcessor()!= null){
				if(pa.getMtomProcessor().getRequestFlow() != null){
					if(pa.getMtomProcessor().getRequestFlow().getMode() != null){
						MTOMProcessorType mode = pa.getMtomProcessor().getRequestFlow().getMode();
						if(!mode.equals(MTOMProcessorType.DISABLE))
							isMTOMAbilitatoReq = true;
					}
				}

				if(pa.getMtomProcessor().getResponseFlow() != null){
					if(pa.getMtomProcessor().getResponseFlow().getMode() != null){
						MTOMProcessorType mode = pa.getMtomProcessor().getResponseFlow().getMode();
						if(!mode.equals(MTOMProcessorType.DISABLE))
							isMTOMAbilitatoRes = true;
					}
				}
			}

			String statoMTOM  = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_DISABILITATO;

			if(isMTOMAbilitatoReq || isMTOMAbilitatoRes)
				statoMTOM  = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_ABILITATO;


			// Prendo il numero di correlazioni applicative
			CorrelazioneApplicativa ca = pa.getCorrelazioneApplicativa();
			int numCorrApp = 0;
			if (ca != null) {
				numCorrApp = ca.sizeElementoList();
			}

			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if (ServletUtils.isEditModeInProgress(request)) {

				// setto la barra del titolo
				if(useIdSogg){
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(SoggettiCostanti.LABEL_SOGGETTI, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
							new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PORTE_APPLICATIVE_DI + tipoNomeSoggettoProprietario, 
									PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST ,
									new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg)),
									new Parameter(oldNomePA , null)
							);
				} else {
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST),
							new Parameter(oldNomePA, null)
							);
				}


				if (descr == null) {
					descr = pa.getDescrizione();
				}
				if (stateless == null) {
					if(pa.getStateless()!=null)
						stateless = pa.getStateless().toString();
				}
				if (behaviour == null) {
					behaviour = pa.getBehaviour();
				}
				if (gestBody == null) {
					String allegaBody = null;
					if(pa.getAllegaBody()!=null)
						allegaBody = pa.getAllegaBody().toString();
					String scartaBody = null;
					if(pa.getScartaBody()!=null)
						scartaBody = pa.getScartaBody().toString();
					if (PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_ABILITATO.equals(allegaBody) &&
							PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_DISABILITATO.equals(scartaBody))
						gestBody = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_BODY_ALLEGA;
					else if (PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_DISABILITATO.equals(allegaBody) && 
							PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_ABILITATO.equals(scartaBody))
						gestBody = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_BODY_SCARTA;
					else if (PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_DISABILITATO.equals(allegaBody) && 
							PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_DISABILITATO.equals(scartaBody))
						gestBody = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_BODY_NONE;
				}
				if (gestManifest == null) {
					if(pa.getGestioneManifest()!=null)
						gestManifest = pa.getGestioneManifest().toString();
				}
				if (ricsim == null) {
					if(pa.getRicevutaAsincronaSimmetrica()!=null)
						ricsim = pa.getRicevutaAsincronaSimmetrica().toString();
					if ((ricsim == null) || "".equals(ricsim)) {
						ricsim = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_ABILITATO;
					}
				}
				if (ricasim == null) {
					if(pa.getRicevutaAsincronaAsimmetrica()!=null)
						ricasim = pa.getRicevutaAsincronaAsimmetrica().toString();
					if ((ricasim == null) || "".equals(ricasim)) {
						ricasim = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_ABILITATO;
					}
				}
				if (xsd == null) {
					ValidazioneContenutiApplicativi vx = pa.getValidazioneContenutiApplicativi();
					if (vx == null) {
						xsd = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_XSD_DISABILITATO;
					} else {
						if(vx.getStato()!=null)
							xsd = vx.getStato().toString();
						if ((xsd == null) || "".equals(xsd)) {
							xsd = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_XSD_DISABILITATO;
						}
					}
				}
				if (tipoValidazione == null) {
					ValidazioneContenutiApplicativi vx = pa.getValidazioneContenutiApplicativi();
					if (vx == null) {
						tipoValidazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_VALIDAZIONE_XSD;
					} else {
						if(vx.getTipo()!=null)
							tipoValidazione = vx.getTipo().toString();
						if (tipoValidazione == null || "".equals(tipoValidazione)) {
							tipoValidazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_VALIDAZIONE_XSD ;
						}
					}
				}
				if (applicaMTOM == null) {
					ValidazioneContenutiApplicativi vx = pa.getValidazioneContenutiApplicativi();
					applicaMTOM = "";
					if (vx != null) {
						if(vx.getAcceptMtomMessage()!=null)
							if (vx.getAcceptMtomMessage().equals(StatoFunzionalita.ABILITATO)) 
								applicaMTOM = "yes";
					}
				}

				if(autorizzazioneContenuti==null){
					autorizzazioneContenuti = pa.getAutorizzazioneContenuto();
				}

				if ((scadcorr == null) && (ca != null)) {
					scadcorr = ca.getScadenza();
				}

				if (integrazione == null) {
					integrazione = pa.getIntegrazione();
				}
				if (soggvirt == null) {
					// soggvirt = risultato.getString("soggvirt");
					PortaApplicativaSoggettoVirtuale pasv = pa.getSoggettoVirtuale();
					if (pasv == null) {
						soggvirt="-";					
					} else {
						soggvirt = pasv.getTipo()+"/"+pasv.getNome();
						servizio = soggvirt + " " +pa.getServizio().getTipo()+"/"+pa.getServizio().getNome();
					}
				}

				// recupero informazioni su servizio se non specificato l'id
				// del servizio
				if ( servizio==null || "".equals(servizio)) {
					if( !(soggvirt!=null && idSoggettoVirtuale!=null) ){
						servizio = tipoSoggettoProprietario+"/"+nomeSoggettoProprietario+" "+pa.getServizio().getTipo()+"/"+pa.getServizio().getNome();
					}
				}

				// Azione
				PortaApplicativaAzione paa = pa.getAzione();
				if (paa == null){
					azione = "-";
				}else{
					azione = paa.getNome();
					if(azione==null || "".equals(azione)){
						azione = "-";
					}
				}

				// Prendo la lista di soggetti (tranne il mio) e li metto in un
				// array per la funzione SoggettoVirtuale
				String[] soggettiList = null;
				String[] soggettiListLabel = null;
				Boolean soggVirt = (Boolean) session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_SOGGETTI_VIRTUALI);
				if (soggVirt) {
					List<IDServizio> list = null;
					List<IDSoggetto> listSoggetti = new ArrayList<IDSoggetto>();
					Vector<String> identitaSoggetti = new Vector<String>();
					try{
						list = apsCore.getAllIdServizi(new FiltroRicercaServizi());
					}catch(DriverRegistroServiziNotFound dNotFound){}
					if(list!=null){
						for (int i = 0; i < list.size(); i++) {
							String idSoggetto = list.get(i).getSoggettoErogatore().toString();
							if (!idSoggetto.equals(tipoNomeSoggettoProprietario)){ // non aggiungo il soggetto proprietario della porta applicativa
								if(identitaSoggetti.contains(idSoggetto)==false){
									identitaSoggetti.add(idSoggetto);
									IDSoggetto soggettoErogatore = list.get(i).getSoggettoErogatore();
									String protocolloSoggettoErogatore = soggettiCore.getProtocolloAssociatoTipoSoggetto(soggettoErogatore.getTipo());
									if(protocolloSoggettoErogatore.equals(protocollo)){
										listSoggetti.add(soggettoErogatore);
									}
								}
							}
						}
					}
					int totEl = listSoggetti.size() + 1;
					soggettiList = new String[totEl];
					soggettiListLabel = new String[totEl];
					soggettiList[0] = "-";
					soggettiListLabel[0] = "-";
					List<String> listSoggettiOrdered = new ArrayList<String>();
					for (IDSoggetto idSoggetto : listSoggetti) {
						listSoggettiOrdered.add(idSoggetto.getTipo() + "/" + idSoggetto.getNome());
					}
					Collections.sort(listSoggettiOrdered);
					int i = 1;
					for (String idSOrdered : listSoggettiOrdered) {
						soggettiList[i] = idSOrdered;
						soggettiListLabel[i] = idSOrdered;
						i++;
					}
					
				}

				// Prendo la lista di servizi e li metto in un array
				String[] serviziList = null;
				String[] serviziListLabel = null;
				List<IDServizio> list1 = new ArrayList<IDServizio>();
				FiltroRicercaServizi filtroServizi = new FiltroRicercaServizi();
				if ( (!soggvirt.equals("")) && (!soggvirt.equals("-")) ){
					filtroServizi.setTipoSoggettoErogatore(soggvirt.split("/")[0]);
					filtroServizi.setNomeSoggettoErogatore(soggvirt.split("/")[1]);
				}
				else{
					filtroServizi.setTipoSoggettoErogatore(tipoSoggettoProprietario);
					filtroServizi.setNomeSoggettoErogatore(nomeSoggettoProprietario);
				}
				try{
					List<IDServizio> listaServizi = apsCore.getAllIdServizi(filtroServizi);
					if(listaServizi!=null){
						for (int j = 0; j < listaServizi.size(); j++) {
							list1.add(listaServizi.get(j));			
						}
					}
				}catch(DriverRegistroServiziNotFound not){}
				if (list1.size() > 0) {
					serviziList = new String[list1.size()];
					serviziListLabel = new String[list1.size()];
					
					List<String> tmp = new ArrayList<String>();
					List<IDServizio> ordered = new ArrayList<IDServizio>();
					Hashtable<String, IDServizio> map = new Hashtable<String, IDServizio>();
					for (IDServizio servizioInLista : list1) {
						String key = servizioInLista.getSoggettoErogatore().getTipo()+ "/" + servizioInLista.getSoggettoErogatore().getNome() + " " + servizioInLista.getTipoServizio() + "/" + servizioInLista.getServizio();
						tmp.add(key);
						map.put(key, servizioInLista);
					}
					Collections.sort(tmp);
					for (String idSOrdered : tmp) {
						ordered.add(map.get(idSOrdered));
					}
					
					int i = 0;
					for (IDServizio servizioInLista : ordered) {
						if (servizio == null)
							servizio = servizioInLista.getSoggettoErogatore().getTipo()+ "/" + servizioInLista.getSoggettoErogatore().getNome() + " " + servizioInLista.getTipoServizio() + "/" + servizioInLista.getServizio();
						serviziList[i] = servizioInLista.getSoggettoErogatore().getTipo()+ "/" + servizioInLista.getSoggettoErogatore().getNome() + " " + servizioInLista.getTipoServizio() + "/" + servizioInLista.getServizio();
						serviziListLabel[i] = servizioInLista.getTipoServizio() + "/" + servizioInLista.getServizio();
						i++;
					}
				}

				IDServizio idServizio = null;
				AccordoServizioParteSpecifica servS = null;
				if (servizio != null) {
					boolean servizioPresenteInLista  = false;
					if(serviziList!=null && serviziList.length>0){
						for (int i = 0; i < serviziList.length; i++) {
							if(serviziList[i].equals(servizio)){
								servizioPresenteInLista = true;
								break;
							}
						}
					}
					if(servizioPresenteInLista){
						String [] tmp = servizio.split(" ");
						idServizio = new IDServizio(tmp[0].split("/")[0],tmp[0].split("/")[1],
								tmp[1].split("/")[0],tmp[1].split("/")[1]);
						try{
							servS = apsCore.getServizio(idServizio);
						}catch(DriverRegistroServiziNotFound dNotFound){
						}
					}
					if(servS==null){
						
						// è cambiato il soggetto erogatore. non è più valido il servizio
						servizio = null;
						idServizio = null;
						if(serviziList!=null && serviziList.length>0){
							servizio = serviziList[0];
							String [] tmp = servizio.split(" ");
							idServizio = new IDServizio(tmp[0].split("/")[0],tmp[0].split("/")[1],
									tmp[1].split("/")[0],tmp[1].split("/")[1]);
							try{
								servS = apsCore.getServizio(idServizio);
							}catch(DriverRegistroServiziNotFound dNotFound){
							}
							if(servS==null){
								servizio = null;
								idServizio = null;
							}
						}
					}
				}
				
				// Prendo le azioni associate al servizio
				String[] azioniList = null;
				try {
					if (servS != null) {
						IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(servS.getAccordoServizioParteComune());
						AccordoServizioParteComune as = apcCore.getAccordoServizio(idAccordo);

						if(servS.getPortType()!=null){
							// Bisogna prendere le operations del port type
							PortType pt = null;
							for (int i = 0; i < as.sizePortTypeList(); i++) {
								if(as.getPortType(i).getNome().equals(servS.getPortType())){
									pt = as.getPortType(i);
									break;
								}
							}
							if(pt==null){
								throw new Exception("Servizio ["+idServizio.toString()+"] possiede il port type ["+servS.getPortType()+"] che non risulta essere registrato nell'accordo di servizio ["+servS.getAccordoServizioParteComune()+"]");
							}
							if(pt.sizeAzioneList()>0){
								azioniList = new String[pt.sizeAzioneList()+1];
								azioniList[0] = "-";
								List<String> azioni = new ArrayList<String>();
								for (int i = 0; i < pt.sizeAzioneList(); i++) {
									if (azione == null) {
										azione = "-";
									}
									azioni.add(pt.getAzione(i).getNome());
								}
								Collections.sort(azioni);
								for (int i = 0; i < azioni.size(); i++) {
									azioniList[i+1] = "" + azioni.get(i);
								}
							}
						}else{
							if(as.sizeAzioneList()>0){
								azioniList = new String[as.sizeAzioneList()+1];
								azioniList[0] = "-";
								List<String> azioni = new ArrayList<String>();
								for (int i = 0; i < as.sizeAzioneList(); i++) {
									if (azione == null) {
										azione = "-";
									}
									azioni.add("" + as.getAzione(i).getNome());
								}
								Collections.sort(azioni);
								for (int i = 0; i < azioni.size(); i++) {
									azioniList[i+1] = "" + azioni.get(i);
								}
							}
						}				
					}
				} catch (Exception e) {
					// Il refresh, in seguito al cambio della validazione puo'
					// avvenire anche se il servizio non e' selezionato
				}

				// setto oldNomePD
				pd.addHidden(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_OLD_NOME_PA, oldNomePA);
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idPorta, idsogg, idPorta, dati);

				dati = porteApplicativeHelper.addPorteAppToDati(TipoOperazione.CHANGE,dati, 
						nomePorta, descr, soggvirt, soggettiList, soggettiListLabel, servizio,
						serviziList, serviziListLabel, azione, azioniList,  stateless, ricsim, ricasim, idsogg, 
						idPorta, xsd, tipoValidazione, gestBody, gestManifest,integrazione,
						numCorrApp,scadcorr,autorizzazioneContenuti,protocollo,
						numSA,statoMessageSecurity,statoMTOM,numCorrelazioneReq,numCorrelazioneRes,numProprProt,applicaMTOM,
						behaviour,servizioApplicativoList,servizioApplicativo);

				pd.setDati(dati);



				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE,
						ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.porteAppCheckData(TipoOperazione.CHANGE, oldNomePA);
			if (!isOk) {
				// setto la barra del titolo
				if(useIdSogg){
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(SoggettiCostanti.LABEL_SOGGETTI, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
							new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PORTE_APPLICATIVE_DI + tipoNomeSoggettoProprietario, 
									PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST ,
									new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg)),
									new Parameter(oldNomePA , null)
							);
				} else {
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST),
							new Parameter(oldNomePA, null)
							);
				}


				// Prendo la lista di soggetti (tranne il mio) e li metto in un
				// array per la funzione SoggettoVirtuale
				String[] soggettiList = null;
				String[] soggettiListLabel = null;
				Boolean soggVirt = (Boolean) session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_SOGGETTI_VIRTUALI);
				if (soggVirt) {
					List<IDServizio> list = null;
					List<IDSoggetto> listSoggetti = new ArrayList<IDSoggetto>();
					Vector<String> identitaSoggetti = new Vector<String>();
					try{
						list = apsCore.getAllIdServizi(new FiltroRicercaServizi());
					}catch(DriverRegistroServiziNotFound dNotFound){}
					if(list!=null){
						for (int i = 0; i < list.size(); i++) {
							String idSoggetto = list.get(i).getSoggettoErogatore().toString();
							if (!idSoggetto.equals(tipoNomeSoggettoProprietario)){ // non aggiungo il soggetto proprietario della porta applicativa
								if(identitaSoggetti.contains(idSoggetto)==false){
									identitaSoggetti.add(idSoggetto);
									listSoggetti.add(list.get(i).getSoggettoErogatore());
								}
							}
						}
					}
					int totEl = listSoggetti.size() + 1;
					soggettiList = new String[totEl];
					soggettiListLabel = new String[totEl];
					soggettiList[0] = "-";
					soggettiListLabel[0] = "-";
					int i = 1;
					for (IDSoggetto idSoggetto : listSoggetti) {
						soggettiList[i] = idSoggetto.getTipo() + "/" + idSoggetto.getNome();
						soggettiListLabel[i] = idSoggetto.getTipo() + "/" + idSoggetto.getNome();
						i++;
					}
				}

				// Prendo la lista di servizi e li metto in un array
				String[] serviziList = null;
				String[] serviziListLabel = null;
				List<IDServizio> list1 = new ArrayList<IDServizio>();
				FiltroRicercaServizi filtroServizi = new FiltroRicercaServizi();
				if ( (!soggvirt.equals("")) && (!soggvirt.equals("-")) ){
					filtroServizi.setTipoSoggettoErogatore(soggvirt.split("/")[0]);
					filtroServizi.setNomeSoggettoErogatore(soggvirt.split("/")[1]);
				}
				else{
					filtroServizi.setTipoSoggettoErogatore(tipoSoggettoProprietario);
					filtroServizi.setNomeSoggettoErogatore(nomeSoggettoProprietario);
				}
				try{
					List<IDServizio> listaServizi = apsCore.getAllIdServizi(filtroServizi);
					if(listaServizi!=null){
						for (int j = 0; j < listaServizi.size(); j++) {
							list1.add(listaServizi.get(j));			
						}
					}
				}catch(DriverRegistroServiziNotFound not){}
				if (list1.size() > 0) {
					serviziList = new String[list1.size()];
					serviziListLabel = new String[list1.size()];
					int i = 0;
					for (IDServizio servizioInLista : list1) {
						if (servizio == null)
							servizio = servizioInLista.getSoggettoErogatore().getTipo()+ "/" + servizioInLista.getSoggettoErogatore().getNome() + " " + servizioInLista.getTipoServizio() + "/" + servizioInLista.getServizio();
						serviziList[i] = servizioInLista.getSoggettoErogatore().getTipo()+ "/" + servizioInLista.getSoggettoErogatore().getNome() + " " + servizioInLista.getTipoServizio() + "/" + servizioInLista.getServizio();
						serviziListLabel[i] = servizioInLista.getTipoServizio() + "/" + servizioInLista.getServizio();
						i++;
					}
				}

				// Prendo le azioni associate al servizio
				String[] azioniList = null;
				try {
					if (servizio != null) {
						String [] tmp = servizio.split(" ");
						IDServizio idServizio = new IDServizio(tmp[0].split("/")[0],tmp[0].split("/")[1],
								tmp[1].split("/")[0],tmp[1].split("/")[1]);
						AccordoServizioParteSpecifica servS = null;
						try{
							servS = apsCore.getServizio(idServizio);
						}catch(DriverRegistroServiziNotFound dNotFound){
						}
						if(servS==null){
							throw new Exception("Servizio ["+idServizio.toString()+"] non riscontrato");
						}
						IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(servS.getAccordoServizioParteComune());
						AccordoServizioParteComune as = apcCore.getAccordoServizio(idAccordo);

						if(servS.getPortType()!=null){
							// Bisogna prendere le operations del port type
							PortType pt = null;
							for (int i = 0; i < as.sizePortTypeList(); i++) {
								if(as.getPortType(i).getNome().equals(servS.getPortType())){
									pt = as.getPortType(i);
									break;
								}
							}
							if(pt==null){
								throw new Exception("Servizio ["+idServizio.toString()+"] possiede il port type ["+servS.getPortType()+"] che non risulta essere registrato nell'accordo di servizio ["+servS.getAccordoServizioParteComune()+"]");
							}
							if(pt.sizeAzioneList()>0){
								azioniList = new String[pt.sizeAzioneList()+1];
								azioniList[0] = "-";
								for (int i = 0; i < pt.sizeAzioneList(); i++) {
									if (azione == null) {
										azione = "-";
									}
									azioniList[i+1] = "" + pt.getAzione(i).getNome();
								}
							}
						}else{
							if(as.sizeAzioneList()>0){
								azioniList = new String[as.sizeAzioneList()+1];
								azioniList[0] = "-";
								for (int i = 0; i < as.sizeAzioneList(); i++) {
									if (azione == null) {
										azione = "-";
									}
									azioniList[i+1] = "" + as.getAzione(i).getNome();
								}
							}
						}				
					}
				} catch (Exception e) {
					// Il refresh, in seguito al cambio della validazione puo'
					// avvenire anche se il servizio non e' selezionato
				}

				// setto oldNomePD
				pd.addHidden(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_OLD_NOME_PA, oldNomePA);
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idPorta, idsogg, idPorta, dati);

				dati = porteApplicativeHelper.addPorteAppToDati(TipoOperazione.CHANGE,dati,
						nomePorta, descr, soggvirt, soggettiList, soggettiListLabel, servizio, 
						serviziList, serviziListLabel, azione, azioniList,   stateless, ricsim,
						ricasim, idsogg, idPorta, xsd, tipoValidazione, gestBody, gestManifest,integrazione,
						numCorrApp,scadcorr,autorizzazioneContenuti,protocollo,
						numSA,statoMessageSecurity,statoMTOM,numCorrelazioneReq,numCorrelazioneRes,numProprProt,applicaMTOM,
						behaviour,servizioApplicativoList,servizioApplicativo);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE, 
						ForwardParams.CHANGE());
			}

			// Modifico i dati della porta applicativa nel db
			pa.setNome(nomePorta);
			pa.setOldNomeForUpdate(oldNomePA);
			pa.setOldTipoSoggettoProprietarioForUpdate(pa.getTipoSoggettoProprietario());
			pa.setOldNomeSoggettoProprietarioForUpdate(pa.getNomeSoggettoProprietario());
			pa.setDescrizione(descr);
			pa.setAutorizzazioneContenuto(autorizzazioneContenuti);
			if (stateless!=null && stateless.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_STATELESS_DEFAULT))
				pa.setStateless(null);
			else
				pa.setStateless(StatoFunzionalita.toEnumConstant(stateless));
			if (PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_BODY_ALLEGA.equals(gestBody))
				pa.setAllegaBody(StatoFunzionalita.toEnumConstant(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_ABILITATO));
			else
				pa.setAllegaBody(StatoFunzionalita.toEnumConstant(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_DISABILITATO));
			if (PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_BODY_SCARTA.equals(gestBody))
				pa.setScartaBody(StatoFunzionalita.toEnumConstant(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_ABILITATO));
			else
				pa.setScartaBody(StatoFunzionalita.toEnumConstant(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_DISABILITATO));
			if (gestManifest!=null && gestManifest.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_MANIFEST_DEFAULT))
				pa.setGestioneManifest(null);
			else
				pa.setGestioneManifest(StatoFunzionalita.toEnumConstant(gestManifest));
			pa.setRicevutaAsincronaSimmetrica(StatoFunzionalita.toEnumConstant(ricsim));
			pa.setRicevutaAsincronaAsimmetrica(StatoFunzionalita.toEnumConstant(ricasim));
			if (idSoggettoVirtuale!=null) {
				String tipoSoggVirt = idSoggettoVirtuale.getTipo();
				String nomeSoggVirt = idSoggettoVirtuale.getNome();
				PortaApplicativaSoggettoVirtuale pasv = new PortaApplicativaSoggettoVirtuale();
				pasv.setTipo(tipoSoggVirt);
				pasv.setNome(nomeSoggVirt);
				pa.setSoggettoVirtuale(pasv);
			} else
				pa.setSoggettoVirtuale(null);
			if (servizio!=null) {
				String [] tmp = servizio.split(" ");
				IDServizio idServizio = new IDServizio(tmp[0].split("/")[0],tmp[0].split("/")[1],
						tmp[1].split("/")[0],tmp[1].split("/")[1]);
				PortaApplicativaServizio pas = new PortaApplicativaServizio();
				pas.setTipo(idServizio.getTipoServizio());
				pas.setNome(idServizio.getServizio());
				pa.setServizio(pas);
			} else
				pa.setServizio(null);
			if ((azione != null) && !azione.equals("") && !azione.equals("-")) {
				PortaApplicativaAzione paa = new PortaApplicativaAzione();
				paa.setNome(azione);
				pa.setAzione(paa);
			} else
				pa.setAzione(null);

			ValidazioneContenutiApplicativi vx = new ValidazioneContenutiApplicativi();
			vx.setStato(StatoFunzionalitaConWarning.toEnumConstant(xsd));
			vx.setTipo(ValidazioneContenutiApplicativiTipo.toEnumConstant(tipoValidazione));
			if(applicaMTOM != null){
				if(ServletUtils.isCheckBoxEnabled(applicaMTOM))
					vx.setAcceptMtomMessage(StatoFunzionalita.ABILITATO);
				else 
					vx.setAcceptMtomMessage(StatoFunzionalita.DISABILITATO);
			} else 
				vx.setAcceptMtomMessage(null);
			
			pa.setValidazioneContenutiApplicativi(vx);
			// Cambio i dati della vecchia CorrelazioneApplicativa
			// Non ne creo una nuova, altrimenti mi perdo le vecchie entry
			if (ca != null)
				ca.setScadenza(scadcorr);
			pa.setCorrelazioneApplicativa(ca);
			pa.setIntegrazione(integrazione);
			
			if(behaviour!=null && !"".equals(behaviour))
				pa.setBehaviour(behaviour);
			else 
				pa.setBehaviour(null);

			if(servizioApplicativo!=null && !"".equals(servizioApplicativo)){
				// Se il servizioApplicativo e' valorizzato deve esistere un solo SA nella porta applicativa
				if(pa.sizeServizioApplicativoList()>0)
					pa.removeServizioApplicativo(0);
				if(!"-".equals(servizioApplicativo)){
					ServizioApplicativo sa = new ServizioApplicativo();
					sa.setNome(servizioApplicativo);
					pa.addServizioApplicativo(sa);
				}
			}
			
			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);


			List<PortaApplicativa> lista = null;

			if(useIdSogg){
				int idLista = Liste.PORTE_APPLICATIVE_BY_SOGGETTO;
				ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
				lista = porteApplicativeCore.porteAppList(soggInt, ricerca);
			}else{ 
				int idLista = Liste.PORTE_APPLICATIVE;
				ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
				lista = porteApplicativeCore.porteAppList(null, ricerca);
			}

			porteApplicativeHelper.preparePorteAppList(ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE, 
					ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE,
					ForwardParams.CHANGE());

		} 
	}
}
