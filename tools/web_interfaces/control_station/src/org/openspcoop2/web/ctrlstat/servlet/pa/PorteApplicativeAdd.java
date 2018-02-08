/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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
import org.openspcoop2.core.config.AutorizzazioneRuoli;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaApplicativaServizio;
import org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.RuoloTipoMatch;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaServizi;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
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
 * porteAppAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteApplicativeAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);



		try {
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			String nomePorta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA);
			String idPorta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			String descr = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_DESCRIZIONE);
			String statoPorta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_STATO_PORTA);
			String soggvirt = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SOGGETTO_VIRTUALE);
			if (soggvirt == null || "".equals(soggvirt)) {
				soggvirt = "-";
			}
			String servizio = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SERVIZIO);
			String azione = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AZIONE);
			String stateless = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_STATELESS);
			String behaviour = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_BEHAVIOUR);
			String gestBody = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_GESTIONE_BODY);
			String gestManifest = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_GESTIONE_MANIFEST);
			String ricsim = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_RICEVUTA_ASINCRONA_SIMMETRICA);
			String ricasim = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_RICEVUTA_ASINCRONA_ASIMMETRICA);
			String xsd = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_XSD);
			String tipoValidazione = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TIPO_VALIDAZIONE);
			String autorizzazioneContenuti = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI);
			String applicaMTOM = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_APPLICA_MTOM);

			String autenticazione = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE);
			String autenticazioneOpzionale = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_OPZIONALE);
			String autenticazioneCustom = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM);
			String autorizzazione = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE);
			String autorizzazioneCustom = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM);
			
			String autorizzazioneAutenticati = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE);
			String autorizzazioneRuoli = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI);
			String autorizzazioneRuoliTipologia = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_RUOLO_TIPOLOGIA);
			String ruoloMatch = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_RUOLO_MATCH);
			
			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteApplicativeCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteApplicativeCore);
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteApplicativeCore);

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

			// String pdd = soggetto.getServer();

			boolean isSupportatoAutenticazioneSoggetti = soggettiCore.isSupportatoAutenticazioneSoggetti(protocollo);
			
			// Informazioni sul numero di ServiziApplicativi, Correlazione Applicativa e stato Message-Security
			int numSA = 0;
			int numRuoli =0;
			String statoMessageSecurity  =  "";
			String statoMTOM  = "";
			int numCorrelazioneReq =0; 
			int numCorrelazioneRes =0;
			int numProprProt = 0;

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
					String key = servizioInLista.getSoggettoErogatore().getTipo()+ "/" + servizioInLista.getSoggettoErogatore().getNome() + " " + 
							servizioInLista.getTipo() + "/" + servizioInLista.getNome() + "/"+
							servizioInLista.getVersione().intValue();
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
						servizio = servizioInLista.getSoggettoErogatore().getTipo()+ "/" + servizioInLista.getSoggettoErogatore().getNome() + " " + 
								servizioInLista.getTipo() + "/" + servizioInLista.getNome()+ "/"+
								servizioInLista.getVersione().intValue();
					serviziList[i] = servizioInLista.getSoggettoErogatore().getTipo()+ "/" + servizioInLista.getSoggettoErogatore().getNome() + " " + 
								servizioInLista.getTipo() + "/" + servizioInLista.getNome()+"/"+
								servizioInLista.getVersione().intValue();
					serviziListLabel[i] = servizioInLista.getTipo() + "/" + servizioInLista.getNome()+"/"+
							servizioInLista.getVersione().intValue();
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
					idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tmp[1].split("/")[0],tmp[1].split("/")[1], 
							tmp[0].split("/")[0],tmp[0].split("/")[1], 
							Integer.parseInt(tmp[1].split("/")[2])); 
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
						String []tmp = servizio.split(" ");
						idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tmp[1].split("/")[0],tmp[1].split("/")[1], 
								tmp[0].split("/")[0],tmp[0].split("/")[1], 
								Integer.parseInt(tmp[1].split("/")[2])); 
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
			
			AccordoServizioParteComune as = null;
			ServiceBinding serviceBinding = null;
			if (servS != null) {
				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(servS.getAccordoServizioParteComune());
				as = apcCore.getAccordoServizio(idAccordo);
				serviceBinding = apcCore.toMessageServiceBinding(as.getServiceBinding());
			}
			
			// Prendo le azioni associate al servizio
			String[] azioniList = null;
			List<String> azioni = null;
			try {
				azioni = apcCore.getAzioni(servS, as, true, false, null);
			} catch (Exception e) {
				// Il refresh, in seguito al cambio della validazione puo'
				// avvenire anche se il servizio non e' selezionato
			}
			if(azioni!=null && azioni.size()>0) {
				if (azione == null) {
					azione = "-";
				}
				azioniList = new String[azioni.size()];
				for (int i = 0; i < azioni.size(); i++) {
					azioniList[i] = "" + azioni.get(i);
				}
			}

			// Se idhid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (ServletUtils.isEditModeInProgress(request)) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(SoggettiCostanti.LABEL_SOGGETTI, null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
						new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PORTE_APPLICATIVE_DI + tipoNomeSoggettoProprietario, 
								PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST ,
								new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg)),
								new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null)
						);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta, dati);

				if (xsd == null) {
					if(porteApplicativeCore.isSinglePdD()){
						Configurazione config = porteApplicativeCore.getConfigurazioneGenerale();
						if(config.getValidazioneContenutiApplicativi()!=null){
							if(config.getValidazioneContenutiApplicativi().getStato()!=null)
								xsd = config.getValidazioneContenutiApplicativi().getStato().toString();
							if(config.getValidazioneContenutiApplicativi().getTipo()!=null)
								tipoValidazione = config.getValidazioneContenutiApplicativi().getTipo().toString();
							if(StatoFunzionalita.ABILITATO.equals(config.getValidazioneContenutiApplicativi().getAcceptMtomMessage())){
								applicaMTOM = Costanti.CHECK_BOX_ENABLED_ABILITATO;
							}
						}
					}
				}

				if (xsd == null) {
					xsd = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_XSD_DISABILITATO;
				}
				if (tipoValidazione == null) {
					tipoValidazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_VALIDAZIONE_XSD;
				}
				if (applicaMTOM == null) {
					applicaMTOM = "";
				}
				if (stateless == null) {
					stateless = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_STATELESS_DEFAULT;
				}
				if (gestManifest == null) {
					gestManifest = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_MANIFEST_DEFAULT;
				}
				
				if (autenticazione == null) {
					autenticazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_AUTENTICAZIONE;
				}
				if (autorizzazione == null) {
					String defaultAutorizzazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_AUTORIZZAZIONE;
					if (defaultAutorizzazione != null &&
							!TipoAutorizzazione.getAllValues().contains(defaultAutorizzazione)) {
						autorizzazioneCustom = defaultAutorizzazione;
						autorizzazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM;
					}
					else{
						autorizzazione = AutorizzazioneUtilities.convertToStato(defaultAutorizzazione);
						if(TipoAutorizzazione.isAuthenticationRequired(defaultAutorizzazione))
							autorizzazioneAutenticati = Costanti.CHECK_BOX_ENABLED;
						if(TipoAutorizzazione.isRolesRequired(defaultAutorizzazione))
							autorizzazioneRuoli = Costanti.CHECK_BOX_ENABLED;
						autorizzazioneRuoliTipologia = AutorizzazioneUtilities.convertToRuoloTipologia(defaultAutorizzazione).getValue();
					}
				}

				dati = porteApplicativeHelper.addPorteAppToDati(TipoOperazione.ADD,dati, nomePorta, descr, soggvirt, soggettiList,
						soggettiListLabel, servizio, serviziList, serviziListLabel, azione, azioniList, 
						stateless, ricsim, ricasim, null, null, xsd, tipoValidazione, gestBody, gestManifest,
						null,0,null,autorizzazioneContenuti,protocollo,
						numSA,numRuoli, ruoloMatch,
						statoMessageSecurity,statoMTOM,numCorrelazioneReq,numCorrelazioneRes,numProprProt,applicaMTOM,
						behaviour,null,null,
						autenticazione, autorizzazione,
						autenticazioneOpzionale, autenticazioneCustom, autorizzazioneCustom,
						isSupportatoAutenticazioneSoggetti,autorizzazioneAutenticati,autorizzazioneRuoli,autorizzazioneRuoliTipologia,
						servS,as,serviceBinding,
						statoPorta);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE,
						ForwardParams.ADD());

			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.porteAppCheckData(TipoOperazione.ADD, null, isSupportatoAutenticazioneSoggetti);
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(SoggettiCostanti.LABEL_SOGGETTI, null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
						new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PORTE_APPLICATIVE_DI + tipoNomeSoggettoProprietario, 
								PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST ,
								new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg)),
								new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null)
						);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta, dati);

				dati = porteApplicativeHelper.addPorteAppToDati(TipoOperazione.ADD,dati, nomePorta, descr, soggvirt, soggettiList,
						soggettiListLabel, servizio, serviziList,
						serviziListLabel, azione, azioniList,  stateless, ricsim, ricasim, 
						null, null, xsd, tipoValidazione, gestBody, gestManifest,null,0,null,autorizzazioneContenuti,protocollo,
						numSA,numRuoli, ruoloMatch,
						statoMessageSecurity,statoMTOM,numCorrelazioneReq,numCorrelazioneRes,numProprProt,applicaMTOM,
						behaviour,null,null,
						autenticazione, autorizzazione,
						autenticazioneOpzionale, autenticazioneCustom, autorizzazioneCustom,
						isSupportatoAutenticazioneSoggetti,autorizzazioneAutenticati,autorizzazioneRuoli,autorizzazioneRuoliTipologia,
						servS,as,serviceBinding,
						statoPorta);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE, 
						ForwardParams.ADD());
			}

			// Inserisco la porta applicativa nel db
			PortaApplicativa pa = new PortaApplicativa();
			pa.setNome(nomePorta);
			pa.setNomeSoggettoProprietario(nomeSoggettoProprietario);
			pa.setTipoSoggettoProprietario(tipoSoggettoProprietario);
			pa.setDescrizione(descr);
			if(statoPorta==null || "".equals(statoPorta) || CostantiConfigurazione.ABILITATO.toString().equals(statoPorta)){
				pa.setStato(StatoFunzionalita.ABILITATO);
			}
			else{
				pa.setStato(StatoFunzionalita.DISABILITATO);
			}
			pa.setAutorizzazioneContenuto(autorizzazioneContenuti);
			
			if (autenticazione == null ||
					!autenticazione.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM))
				pa.setAutenticazione(autenticazione);
			else
				pa.setAutenticazione(autenticazioneCustom);
			if(autenticazioneOpzionale != null){
				if(ServletUtils.isCheckBoxEnabled(autenticazioneOpzionale))
					pa.setAutenticazioneOpzionale(StatoFunzionalita.ABILITATO);
				else 
					pa.setAutenticazioneOpzionale(StatoFunzionalita.DISABILITATO);
			} else 
				pa.setAutenticazioneOpzionale(null);
			if (autorizzazione == null || 
					!autorizzazione.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM))
				pa.setAutorizzazione(AutorizzazioneUtilities.convertToTipoAutorizzazioneAsString(autorizzazione, 
						ServletUtils.isCheckBoxEnabled(autorizzazioneAutenticati), ServletUtils.isCheckBoxEnabled(autorizzazioneRuoli), 
						RuoloTipologia.toEnumConstant(autorizzazioneRuoliTipologia)));
			else
				pa.setAutorizzazione(autorizzazioneCustom);
						
			if(ruoloMatch!=null && !"".equals(ruoloMatch)){
				RuoloTipoMatch tipoRuoloMatch = RuoloTipoMatch.toEnumConstant(ruoloMatch);
				if(tipoRuoloMatch!=null){
					if(pa.getRuoli()==null){
						pa.setRuoli(new AutorizzazioneRuoli());
					}
					pa.getRuoli().setMatch(tipoRuoloMatch);
				}
			}
			
			if (stateless!=null && !stateless.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_STATELESS_DEFAULT))
				pa.setStateless(StatoFunzionalita.toEnumConstant(stateless));
			if (PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_BODY_ALLEGA.equals(gestBody))
				pa.setAllegaBody(StatoFunzionalita.toEnumConstant(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_ABILITATO));
			else
				pa.setAllegaBody(StatoFunzionalita.toEnumConstant(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_DISABILITATO));
			if (PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_BODY_SCARTA.equals(gestBody))
				pa.setScartaBody(StatoFunzionalita.toEnumConstant(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_ABILITATO));
			else
				pa.setScartaBody(StatoFunzionalita.toEnumConstant(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_DISABILITATO));
			if (gestManifest!=null && !gestManifest.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_MANIFEST_DEFAULT))
				pa.setGestioneManifest(StatoFunzionalita.toEnumConstant(gestManifest));
			pa.setRicevutaAsincronaSimmetrica(StatoFunzionalita.toEnumConstant(ricsim));
			pa.setRicevutaAsincronaAsimmetrica(StatoFunzionalita.toEnumConstant(ricasim));
			if ( (!soggvirt.equals("")) && (!soggvirt.equals("-")) ){
				String tipoSoggVirt = soggvirt.split("/")[0];
				String nomeSoggVirt = soggvirt.split("/")[1];
				PortaApplicativaSoggettoVirtuale pasv = new PortaApplicativaSoggettoVirtuale();
				pasv.setTipo(tipoSoggVirt);
				pasv.setNome(nomeSoggVirt);
				pa.setSoggettoVirtuale(pasv);
			}

			if (servizio!=null) {
				String [] tmp = servizio.split(" ");
				idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tmp[1].split("/")[0],tmp[1].split("/")[1], 
						tmp[0].split("/")[0],tmp[0].split("/")[1], 
						Integer.parseInt(tmp[1].split("/")[2])); 
				PortaApplicativaServizio pas = new PortaApplicativaServizio();
				pas.setTipo(idServizio.getTipo());
				pas.setNome(idServizio.getNome());
				pas.setVersione(idServizio.getVersione());
				pa.setServizio(pas);
			}

			if ((azione != null) && !azione.equals("") && !azione.equals("-")) {
				PortaApplicativaAzione paa = new PortaApplicativaAzione();
				paa.setNome(azione);
				pa.setAzione(paa);
			}

			pa.setStatoMessageSecurity(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_DISABILITATO);
			pa.setIdSoggetto(new Long(soggInt));

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

			if(behaviour!=null && !"".equals(behaviour))
				pa.setBehaviour(behaviour);
			else 
				pa.setBehaviour(null);
			
			String userLogin = ServletUtils.getUserLoginFromSession(session);		

			porteApplicativeCore.performCreateOperation(userLogin, porteApplicativeHelper.smista(), pa);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.PORTE_APPLICATIVE_BY_SOGGETTO;
			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
			List<PortaApplicativa> lista = porteApplicativeCore.porteAppList(soggInt, ricerca);

			porteApplicativeHelper.preparePorteAppList(ricerca, lista, idLista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE, 
					ForwardParams.ADD());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE,
					ForwardParams.ADD());
		}  
	}
}
