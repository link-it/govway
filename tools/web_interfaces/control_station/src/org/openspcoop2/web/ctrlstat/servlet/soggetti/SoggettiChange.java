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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddTipologia;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
//import org.openspcoop2.core.registry.driver.SICAtoOpenSPCoopUtilities;
import org.openspcoop2.web.lib.users.dao.InterfaceType;

/**
 * soggettiChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class SoggettiChange extends Action {

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
			
			String id = request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID);
			int idSogg = Integer.parseInt(id);
			String nomeprov = request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME);
			String tipoprov = request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO);
			String portadom = request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_CODICE_PORTA);
			String descr = request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_DESCRIZIONE);
			String versioneProtocollo = request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_VERSIONE_PROTOCOLLO);
			String pdd = request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_PDD);
			String is_router = request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_IS_ROUTER);
			boolean isRouter = ServletUtils.isCheckBoxEnabled(is_router);
			boolean privato = ServletUtils.isCheckBoxEnabled(request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_IS_PRIVATO));
			String codiceIpa = request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_CODICE_IPA);
			String pd_url_prefix_rewriter = request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_PD_URL_PREFIX_REWRITER);
			String pa_url_prefix_rewriter = request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_PA_URL_PREFIX_REWRITER);
	
			// Preparo il menu
			soggettiHelper.makeMenu();
	
			// Prendo i vecchi nome e tipo
			String oldnomeprov = "", oldtipoprov = "";
	
			Boolean contaListe = ServletUtils.getContaListeFromSession(session);
			String userLogin = ServletUtils.getUserLoginFromSession(session);
	
			Soggetto soggettoRegistry = null;
			org.openspcoop2.core.config.Soggetto soggettoConfig = null;
			List<String> tipiSoggetti = null;
			int numPA = 0, numPD = 0,numSA = 0;
			String[] pddList = null;
			List<String> versioniProtocollo = null;

			SoggettiCore soggettiCore = new SoggettiCore();
			PddCore pddCore = new PddCore(soggettiCore);
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore(soggettiCore);
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(soggettiCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(soggettiCore);
			

			if(soggettiCore.isRegistroServiziLocale()){
				soggettoRegistry = soggettiCore.getSoggettoRegistro(idSogg);// core.getSoggettoRegistro(new
				// IDSoggetto(tipoprov,nomeprov));
			}

			soggettoConfig = soggettiCore.getSoggetto(idSogg);// core.getSoggetto(new
			// IDSoggetto(tipoprov,nomeprov));

			if(soggettiCore.isRegistroServiziLocale()){
				oldnomeprov = soggettoRegistry.getNome();
				oldtipoprov = soggettoRegistry.getTipo();
			}
			else{
				oldnomeprov = soggettoConfig.getNome();
				oldtipoprov = soggettoConfig.getTipo();
			}
			
			// Tipi protocollo supportati
						List<String> listaTipiProtocollo = soggettiCore.getProtocolli();
			//tipiSoggetti = soggettiCore.getTipiSoggettiGestiti(versioneProtocollo); // all tipi soggetti gestiti
			// Nella change non e' piu' possibile cambiare il protocollo
			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoprov);
			tipiSoggetti = soggettiCore.getTipiSoggettiGestitiProtocollo(protocollo);
			
			if(InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(session).getInterfaceType())){
				versioniProtocollo = soggettiCore.getVersioniProtocollo(protocollo);
			}else {
				versioniProtocollo = new ArrayList<String>();
				versioneProtocollo = soggettiCore.getVersioneDefaultProtocollo(protocollo);
				versioniProtocollo.add(versioneProtocollo);
			}
			
			boolean isSupportatoCodiceIPA = soggettiCore.isSupportatoCodiceIPA(protocollo); 
			

			if (contaListe) {
				// Conto il numero di porte applicative
				IDSoggetto soggetto = new IDSoggetto(soggettoConfig.getTipo(),soggettoConfig.getNome());
				numPA = porteApplicativeCore.porteAppList(idSogg, new Search(true)).size();// soggettoConfig.sizePortaApplicativaList();
				numPD = porteDelegateCore.porteDelegateList(idSogg, new Search(true)).size();// soggettoConfig.sizePortaDelegataList();
				numSA = saCore.servizioApplicativoList(soggetto, new Search(true)).size();
			}
			
			if(soggettiCore.isSinglePdD()){
				if(soggettiCore.isRegistroServiziLocale()){
					// Prendo la lista di pdd e la metto in un array
					// In pratica se un soggetto e' associato ad una PdD Operativa,
					// e possiede gia' delle PD o PA o SA,
					// non e' piu' possibile cambiargli la porta di dominio in una esterna.
					
					boolean pddOperativa = false;
					if(soggettoRegistry.getPortaDominio()!=null && !"".equals(soggettoRegistry.getPortaDominio())){
						PdDControlStation pddCtrlstat = pddCore.getPdDControlStation(soggettoRegistry.getPortaDominio());
						pddOperativa = PddTipologia.OPERATIVO.toString().equals(pddCtrlstat.getTipo());
					}
						
					List<PortaDominio> lista = new ArrayList<PortaDominio>();
					if( (numPA<=0 && numPD<=0 && numSA<=0) || !pddOperativa ){
						// aggiungo un elemento di comodo
						PortaDominio tmp = new PortaDominio();
						tmp.setNome("-");
						lista.add(tmp);
						// aggiungo gli altri elementi
						if(soggettiCore.isVisioneOggettiGlobale(userLogin)){
							lista.addAll(pddCore.porteDominioList(null, new Search(true)));
						}else{
							lista.addAll(pddCore.porteDominioList(userLogin, new Search(true)));
						}
						pddList = new String[lista.size()];
						int i = 0;
						for (PortaDominio pddTmp : lista) {
							pddList[i] = pddTmp.getNome();
							i++;
						}
					}
					else{
						// non posso modificare la pdd. Lascio solo quella operativa
						pddList = new String[1];
						pddList[0] = soggettoRegistry.getPortaDominio();
					}
				}
			}
			
			org.openspcoop2.core.registry.Connettore connettore = null;
			if(soggettiCore.isRegistroServiziLocale()){
				connettore = soggettoRegistry.getConnettore();
			}
	
			// Se nomehid = null, devo visualizzare la pagina per la modifica dati
			if(ServletUtils.isEditModeInProgress(request)){

				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletChange(pd, SoggettiCostanti.LABEL_SOGGETTI, 
						SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST, oldtipoprov + "/" + oldnomeprov);
	
				if (is_router == null) 
					isRouter = soggettoConfig.getRouter();
				if(soggettiCore.isRegistroServiziLocale()){
					if(portadom==null){
						portadom = soggettoRegistry.getIdentificativoPorta();
					}else{
						String old_protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(oldtipoprov);
						if(old_protocollo.equals(protocollo)==false){
							// e' cambiato il protocollo: setto a empty il portadom
							portadom = null;
						}
					}
					if(descr==null)
						descr = soggettoRegistry.getDescrizione();
					if(pdd==null)
						pdd = soggettoRegistry.getPortaDominio();
					if(versioneProtocollo==null)
						versioneProtocollo = soggettoRegistry.getVersioneProtocollo();
					if(request.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_IS_PRIVATO) == null){
						privato = soggettoRegistry.getPrivato()!=null && soggettoRegistry.getPrivato();
					}
					if(codiceIpa==null){
						codiceIpa = soggettoRegistry.getCodiceIpa();
					}
				}
				else{
					if(portadom==null){
						portadom = soggettoConfig.getIdentificativoPorta();
					}else{
						String old_protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(oldtipoprov);
						if(old_protocollo.equals(protocollo)==false){
							// e' cambiato il protocollo: setto a empty il portadom
							portadom = null;
						}
					}
					if(descr==null)
						descr = soggettoConfig.getDescrizione();
					if (is_router == null) 
						isRouter = soggettoConfig.getRouter();
				}
				
				if(pd_url_prefix_rewriter==null){
					pd_url_prefix_rewriter = soggettoConfig.getPdUrlPrefixRewriter();
				}
				if(pa_url_prefix_rewriter==null){
					pa_url_prefix_rewriter = soggettoConfig.getPaUrlPrefixRewriter();
				}
				if(tipoprov==null){
					tipoprov=oldtipoprov;
				}
				if(nomeprov==null){
					nomeprov=oldnomeprov;
				}
	
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
	
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
						
				dati = soggettiHelper.addSoggettiToDati(TipoOperazione.CHANGE,dati, nomeprov, tipoprov, portadom, descr, 
						isRouter, tipiSoggetti, versioneProtocollo, privato, codiceIpa, versioniProtocollo,isSupportatoCodiceIPA,
						pddList,null,pdd,id,oldnomeprov,oldtipoprov,connettore,
						numPD,pd_url_prefix_rewriter,numPA,pa_url_prefix_rewriter,listaTipiProtocollo,protocollo);
								
				pd.setDati(dati);
	
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.CHANGE());
				
			}
	
			// Controlli sui campi immessi
			boolean isOk = soggettiHelper.soggettiCheckData(TipoOperazione.CHANGE);
			
			if(isOk){
				// check change tipo/nome con gestione workflow abilitata
				if(soggettiCore.isRegistroServiziLocale() && soggettiCore.isShowGestioneWorkflowStatoDocumenti()){
					if( (oldnomeprov.equals(nomeprov)==false) || (oldtipoprov.equals(tipoprov)==false) ){
						HashMap<ErrorsHandlerCostant, String> whereIsInUso = new HashMap<ErrorsHandlerCostant, String>();
						if (soggettiCore.isSoggettoInUsoInPackageFinali(soggettoRegistry, whereIsInUso)) {
							Set<ErrorsHandlerCostant> keys = whereIsInUso.keySet();
							String tipoNome = soggettoRegistry.getTipo() + "/" + soggettoRegistry.getNome();
							StringBuffer bf = new StringBuffer();
							bf.append("Tipo o Nome del soggetto ");
							bf.append(tipoNome);
							bf.append(" non modificabile poiche' :<br>");
							for (ErrorsHandlerCostant key : keys) {
								String msg = whereIsInUso.get(key);
		
								if (ErrorsHandlerCostant.IN_USO_IN_SERVIZI.toString().equals(key.toString())) {
									bf.append("- erogatore di Servizi in uno stato finale: " + msg + "<BR>");
								}
								else if (ErrorsHandlerCostant.POSSIEDE_FRUITORI.toString().equals(key.toString())) {
									bf.append("- fruitore in uno stato finale: " + msg + "<BR>");
								}
								else if (ErrorsHandlerCostant.IS_REFERENTE.toString().equals(key.toString())) {
									bf.append("- referente di un accordo di servizio in uno stato finale: " + msg + "<BR>");
								}
								else if (ErrorsHandlerCostant.IS_REFERENTE_COOPERAZIONE.toString().equals(key.toString())) {
									bf.append("- referente di un accordo di cooperazione in uno stato finale: " + msg + "<BR>");
								}
								else if (ErrorsHandlerCostant.IS_PARTECIPANTE_COOPERAZIONE.toString().equals(key.toString())) {
									bf.append("- soggetto partecipante di un accordo di cooperazione in uno stato finale: " + msg + "<BR>");
								}
		
							}// chiudo for
		
							bf.append("<br>");
							isOk = false;
							pd.setMessage(bf.toString());
						} 
					}
				}
			}
			
			if(isOk){
				// check visibilita
				if (soggettiCore.isRegistroServiziLocale() && soggettiCore.isShowFlagPrivato() && privato) {
					HashMap<ErrorsHandlerCostant, String> whereIsInUso = new HashMap<ErrorsHandlerCostant, String>();
					if (soggettiCore.isSoggettoInUsoInPackagePubblici(soggettoRegistry, whereIsInUso)) {
						Set<ErrorsHandlerCostant> keys = whereIsInUso.keySet();
						String tipoNome = soggettoRegistry.getTipo() + "/" + soggettoRegistry.getNome();
						StringBuffer bf = new StringBuffer();
						bf.append("Visibilita' del soggetto ");
						bf.append(tipoNome);
						bf.append(" non impostabile a privata poiche' :<br>");
						for (ErrorsHandlerCostant key : keys) {
							String msg = whereIsInUso.get(key);
	
							if (ErrorsHandlerCostant.IN_USO_IN_SERVIZI.toString().equals(key.toString())) {
								bf.append("- erogatore di Servizi con visibilita' pubblica: " + msg + "<BR>");
							}
							else if (ErrorsHandlerCostant.POSSIEDE_FRUITORI.toString().equals(key.toString())) {
								bf.append("- fruitore di servizi con visibilita' pubblica: " + msg + "<BR>");
							}
							else if (ErrorsHandlerCostant.IS_REFERENTE.toString().equals(key.toString())) {
								bf.append("- referente di un accordo di servizio con visibilita' pubblica: " + msg + "<BR>");
							}
							else if (ErrorsHandlerCostant.IS_REFERENTE_COOPERAZIONE.toString().equals(key.toString())) {
								bf.append("- referente di un accordo di cooperazione con visibilita' pubblica: " + msg + "<BR>");
							}
							else if (ErrorsHandlerCostant.IS_PARTECIPANTE_COOPERAZIONE.toString().equals(key.toString())) {
								bf.append("- soggetto partecipante di un accordo di cooperazione con visibilita' pubblica: " + msg + "<BR>");
							}
	
						}// chiudo for
	
						bf.append("<br>");
						isOk = false;
						pd.setMessage(bf.toString());
					} 
				}
			}
			
			if (!isOk) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletChange(pd, SoggettiCostanti.LABEL_SOGGETTI, 
						SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST, oldtipoprov + "/" + oldnomeprov);
					
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
	
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = soggettiHelper.addSoggettiToDati(TipoOperazione.CHANGE,dati, nomeprov, tipoprov, portadom, descr, 
						isRouter, tipiSoggetti, versioneProtocollo, privato, codiceIpa, versioniProtocollo,isSupportatoCodiceIPA,
						pddList,null,pdd,id,oldnomeprov,oldtipoprov,connettore,
						numPD,pd_url_prefix_rewriter,numPA,pa_url_prefix_rewriter,listaTipiProtocollo,protocollo);
								
				pd.setDati(dati);
	
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.CHANGE());

			}
	
			// Modifico i dati del soggetto nel db

			// Se portadom = "", lo imposto
			// se identificativo porta e' di default allora aggiorno il nome
			String identificativoPortaCalcolato = null;
			String identificativoPortaAttuale = portadom;
			if(!soggettiCore.isRegistroServiziLocale()){
				identificativoPortaAttuale = soggettoConfig.getIdentificativoPorta();
			}
			IDSoggetto idSoggetto = new IDSoggetto(tipoprov,nomeprov);
			if(portadom!=null && !portadom.equals("")){
				
				IDSoggetto oldSoggetto = new IDSoggetto(oldtipoprov,oldnomeprov);
				String oldIdentificativoPorta = soggettiCore.getIdentificativoPortaDefault(protocollo, oldSoggetto);
				if (oldIdentificativoPorta.equals(portadom)) {
					// gli identificativi porta sono rimasti invariati
					// setto l identificativo porta di default (in caso sia
					// cambiato il nome)
					identificativoPortaCalcolato = soggettiCore.getIdentificativoPortaDefault(protocollo, idSoggetto);
				} else {					
					// in questo caso ho cambiato l'identificativo porta
					// e il valore inserito nel campo va inserito
					identificativoPortaCalcolato = identificativoPortaAttuale;
				}
			}else{
				identificativoPortaCalcolato = soggettiCore.getIdentificativoPortaDefault(protocollo, idSoggetto);
			}

			if(soggettiCore.isRegistroServiziLocale()){
				
				soggettoRegistry.setIdentificativoPorta(identificativoPortaCalcolato);
				soggettoRegistry.setNome(nomeprov);
				soggettoRegistry.setTipo(tipoprov);
				soggettoRegistry.setOldNomeForUpdate(oldnomeprov);
				soggettoRegistry.setOldTipoForUpdate(oldtipoprov);
				soggettoRegistry.setDescrizione(descr);
				soggettoRegistry.setVersioneProtocollo(versioneProtocollo);
				soggettoRegistry.setPrivato(privato);
				soggettoRegistry.setPortaDominio(pdd);
				
			}
			
			if(soggettiCore.isRegistroServiziLocale()){
				if ((codiceIpa != null && !"".equals(codiceIpa))) {
					String oldCodiceIpa = soggettiCore.getCodiceIPADefault(protocollo, new IDSoggetto(oldtipoprov,oldnomeprov), false);
					if (oldCodiceIpa.equals(codiceIpa)) {
						// il codice ipa e' rimasto invariato
						// setto il codice ipa di default (in caso sia cambiato il nome)
						soggettoRegistry.setCodiceIpa(soggettiCore.getCodiceIPADefault(protocollo, new IDSoggetto(tipoprov,nomeprov), false));
					} else {
						// in questo caso ho cambiato il codice ipa e il valore inserito nel campo va inserito
						soggettoRegistry.setCodiceIpa(codiceIpa);
					}
				} else {
					codiceIpa = soggettiCore.getCodiceIPADefault(protocollo, new IDSoggetto(tipoprov,nomeprov), false);
					soggettoRegistry.setCodiceIpa(codiceIpa);
				}
			}
			
			if(soggettiCore.isRegistroServiziLocale()){
				if(soggettiCore.isSinglePdD()){
					if (pdd.equals("-"))
						soggettoRegistry.setPortaDominio(null);
					else
						soggettoRegistry.setPortaDominio(pdd);
				}else{
					soggettoRegistry.setPortaDominio(pdd);
				}
			}
			
			soggettoConfig.setOldNomeForUpdate(oldnomeprov);
			soggettoConfig.setOldTipoForUpdate(oldtipoprov);
			soggettoConfig.setDescrizione(descr);
			soggettoConfig.setTipo(tipoprov);
			soggettoConfig.setNome(nomeprov);
			soggettoConfig.setDescrizione(descr);
			soggettoConfig.setIdentificativoPorta(identificativoPortaCalcolato);
			soggettoConfig.setRouter(isRouter);
			soggettoConfig.setPdUrlPrefixRewriter(pd_url_prefix_rewriter);
			soggettoConfig.setPaUrlPrefixRewriter(pa_url_prefix_rewriter);

			SoggettoCtrlStat sog = new SoggettoCtrlStat(soggettoRegistry, soggettoConfig);
			sog.setOldNomeForUpdate(oldnomeprov);
			sog.setOldTipoForUpdate(oldtipoprov);

			// Oggetti da modificare (per riflettere la modifica sul connettore)
			SoggettoUpdateUtilities soggettoUpdateUtilities = 
					new SoggettoUpdateUtilities(soggettiCore, oldnomeprov, nomeprov, oldtipoprov, tipoprov, sog);
			
			// Soggetto
			// aggiungo il soggetto da aggiornare
			soggettoUpdateUtilities.addSoggetto();
						
			// Servizi Applicativi
			// Se e' cambiato il tipo o il nome del soggetto devo effettuare la modifica dei servizi applicativi
			// poiche il cambio si riflette sul nome dei connettori del servizio applicativo
			soggettoUpdateUtilities.checkServiziApplicativi();
						
			// Accordi di Cooperazione
			// Se e' cambiato il tipo o il nome del soggetto devo effettuare la modifica degli accordi di cooperazione:
			// - soggetto referente
			// - soggetti partecipanti
			soggettoUpdateUtilities.checkAccordiCooperazione();
	
			// Accordi di Servizio Parte Comune
			// Se e' cambiato il tipo o il nome del soggetto devo effettuare la modifica degli accordi di servizio 
			// poiche il cambio si riflette sul soggetto gestore
			soggettoUpdateUtilities.checkAccordiServizioParteComune();
						
			// Accordi di Servizio Parte Specifica
			// Se e' cambiato il tipo o il nome del soggetto devo effettuare la modifica dei servizi 
			// poiche il cambio si riflette sul nome dei connettori del servizio 
			soggettoUpdateUtilities.checkAccordiServizioParteSpecifica();
			
			// Porte Delegate
			// Se e' cambiato il tipo o il nome del soggetto devo effettuare la modifica delle porte delegate
			// poiche il cambio si riflette sul nome della porta delegata
			soggettoUpdateUtilities.checkPorteDelegate();
	
			// Porte Applicative
			// Se e' cambiato il tipo o il nome del soggetto virtuale devo effettuare la modifica delle porte applicative
			// poiche il cambio si riflette all'interno delle informazioni delle porte applicative
			soggettoUpdateUtilities.checkPorteApplicative();	
							
			// Fruitori nei servizi 
			soggettoUpdateUtilities.checkFruitori();
			
			// eseguo l'aggiornamento
			soggettiCore.performUpdateOperation(userLogin, soggettiHelper.smista(), soggettoUpdateUtilities.getOggettiDaAggiornare().toArray());
				
			// preparo lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			if(soggettiCore.isRegistroServiziLocale()){
				List<Soggetto> listaSoggetti = null;
				if(soggettiCore.isVisioneOggettiGlobale(userLogin)){
					listaSoggetti = soggettiCore.soggettiRegistroList(null, ricerca);
				}else{
					listaSoggetti = soggettiCore.soggettiRegistroList(userLogin, ricerca);
				}
				soggettiHelper.prepareSoggettiList(listaSoggetti, ricerca);
			}
			else{
				List<org.openspcoop2.core.config.Soggetto> listaSoggetti = null;
				if(soggettiCore.isVisioneOggettiGlobale(userLogin)){
					listaSoggetti = soggettiCore.soggettiList(null, ricerca);
				}else{
					listaSoggetti = soggettiCore.soggettiList(userLogin, ricerca);
				}
				soggettiHelper.prepareSoggettiConfigList(listaSoggetti, ricerca);
			}

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.CHANGE());
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.CHANGE());
		} 
	}
}
