/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.aps;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.upload.FormFile;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaApplicativaServizio;
import org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.MTOMProcessorType;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.ProprietariDocumento;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.protocol.sdk.validator.ValidazioneResult;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedListServlet;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneUtilities;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCostanti;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ExporterUtils;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.AreaBottoni;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
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
 * AccordiServizioParteSpecificaHelper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordiServizioParteSpecificaHelper extends ConnettoriHelper {

	public AccordiServizioParteSpecificaHelper(HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(request, pd,  session);
	}


	// Controlla i dati dei WSDL degli Accordi e dei Servizi
	boolean accordiParteSpecificaWSDLCheckData(PageData pd,String tipo, String wsdl, 
			AccordoServizioParteSpecifica aps, AccordoServizioParteComune apc,boolean validazioneDocumenti) throws Exception {

		if(validazioneDocumenti){

			boolean  validazioneParteSpecifica = false;
			if (tipo.equals(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_EROGATORE)) {
				byte [] tmp = wsdl != null && !wsdl.trim().replaceAll("\n", "").equals("") ? wsdl.trim().getBytes() : null;
				aps.setByteWsdlImplementativoErogatore(tmp);
				validazioneParteSpecifica = true;
			}
			else if (tipo.equals(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_FRUITORE)) {
				byte [] tmp = wsdl != null && !wsdl.trim().replaceAll("\n", "").equals("") ? wsdl.trim().getBytes() : null;
				aps.setByteWsdlImplementativoFruitore(tmp);
				validazioneParteSpecifica = true;
			}

			if(validazioneParteSpecifica){
				ValidazioneResult result = this.apsCore.validaInterfacciaWsdlParteSpecifica(aps, apc, this.soggettiCore);
				if(result.isEsito()==false){
					pd.setMessage(result.getMessaggioErrore());
				}
				return result.isEsito();
			}
		}

		return true;
	}
	
	// Controlla i dati dei WSDL degli Accordi e dei Servizi
	boolean accordiParteSpecificaFruitoreWSDLCheckData(PageData pd,String tipo, String wsdl, 
			Fruitore fruitore,AccordoServizioParteSpecifica aps, AccordoServizioParteComune apc,boolean validazioneDocumenti) throws Exception {

		if(validazioneDocumenti){

			boolean  validazioneParteSpecifica = false;
			if (tipo.equals(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_EROGATORE)) {
				byte [] tmp = wsdl != null && !wsdl.trim().replaceAll("\n", "").equals("") ? wsdl.trim().getBytes() : null;
				fruitore.setByteWsdlImplementativoErogatore(tmp);
				validazioneParteSpecifica = true;
			}
			else if (tipo.equals(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_FRUITORE)) {
				byte [] tmp = wsdl != null && !wsdl.trim().replaceAll("\n", "").equals("") ? wsdl.trim().getBytes() : null;
				fruitore.setByteWsdlImplementativoFruitore(tmp);
				validazioneParteSpecifica = true;
			}

			if(validazioneParteSpecifica){
				ValidazioneResult result = this.apsCore.validaInterfacciaWsdlParteSpecifica(fruitore, (AccordoServizioParteSpecifica) aps.clone(), apc, this.soggettiCore);
				if(result.isEsito()==false){
					pd.setMessage(result.getMessaggioErrore());
				}
				return result.isEsito();
			}
		}

		return true;
	}


	// Controlla i dati dei Servizi
	public boolean serviziCheckData(TipoOperazione tipoOp, String[] soggettiList,
			String[] accordiList, String oldNomeservizio,
			String oldTiposervizio, String nomeservizio,
			String tiposervizio, String idSoggErogatore,
			String nomeErogatore, String tipoErogatore, String idAccordo,
			String servcorr, String endpointtype, String url, String nome,
			String tipo, String user, String password, String initcont,
			String urlpgk, String provurl, String connfact, String sendas,
			BinaryParameter wsdlimpler, BinaryParameter wsdlimplfru, String id,
			String profilo, String portType,
			boolean visibilitaAccordoServizio,boolean visibilitaServizio,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey, String tipoconn, String versione,
			boolean validazioneDocumenti,String nomePA, String backToStato,
			String autenticazioneHttp,
			String proxyEnabled, String proxyHost, String proxyPort, String proxyUsername, String proxyPassword,
			String opzioniAvanzate, String transfer_mode, String transfer_mode_chunk_size, String redirect_mode, String redirect_max_hop,
			String requestOutputFileName,String requestOutputFileNameHeaders,String requestOutputParentDirCreateIfNotExists,String requestOutputOverwriteIfExists,
			String responseInputMode, String responseInputFileName, String responseInputFileNameHeaders, String responseInputDeleteAfterRead, String responseInputWaitTime,
			String erogazioneSoggetto,String erogazioneRuolo,String erogazioneAutenticazione,String erogazioneAutenticazioneOpzionale, String erogazioneAutorizzazione,
			String erogazioneAutorizzazioneAutenticati,String erogazioneAutorizzazioneRuoli, String erogazioneAutorizzazioneRuoliTipologia, String erogazioneAutorizzazioneRuoliMatch,boolean isSupportatoAutenticazione,
			List<ExtendedConnettore> listExtendedConnettore)
					throws Exception {

		boolean isModalitaAvanzata = InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType());

		try{

			// ripristina dello stato solo in modalita change
			if(backToStato != null && tipoOp.equals(TipoOperazione.CHANGE)){
				return true;
			}

			int idInt = 0;
			if (tipoOp.equals(TipoOperazione.CHANGE)) {
				idInt = Integer.parseInt(id);
			}
			if (idSoggErogatore == null) {
				idSoggErogatore = "";
			}
			if (idAccordo == null) {
				idAccordo = "";
			}
			if (url == null) {
				url = "";
			}
			if (nome == null) {
				nome = "";
			}
			if (tipo == null) {
				tipo = "";
			}
			if (user == null) {
				user = "";
			}
			if (password == null) {
				password = "";
			}
			if (initcont == null) {
				initcont = "";
			}
			if (urlpgk == null) {
				urlpgk = "";
			}
			if (provurl == null) {
				provurl = "";
			}
			if (connfact == null) {
				connfact = "";
			}
			if (sendas == null) {
				sendas = "";
			}
			if (versione == null) {
				versione = "";
			}

			// Visibilita rispetto all'accordo
			boolean nonVisibile = true;
			boolean visibile = false;
			if(visibilitaServizio==visibile){
				if(visibilitaAccordoServizio==nonVisibile){
					this.pd.setMessage("Non e' possibile utilizzare un accordo di servizio con visibilita' privata, in un servizio con visibilita' pubblica.");
					return false;
				}
			}


			if (tipoOp.equals(TipoOperazione.CHANGE)) {

				Integer versioneInt = 1;
				if(versione!=null){
					versioneInt = Integer.parseInt(versione);
				}
				@SuppressWarnings("unused")
				IDServizio idService = IDServizioFactory.getInstance().getIDServizioFromValues(tiposervizio, nomeservizio, 
						tipoErogatore, nomeErogatore, versioneInt);

				try {
					AccordoServizioParteSpecifica servizio = this.apsCore.getAccordoServizioParteSpecifica(idInt);
					idSoggErogatore = servizio.getIdSoggetto().toString();
				} catch (DriverRegistroServiziNotFound e) {
				} catch (DriverRegistroServiziException e) {
				}

			}

			// Campi obbligatori
			if (!isModalitaAvanzata) {
				if (portType == null || "".equals(portType) || "-".equals(portType)) {
					this.pd.setMessage("E' necessario indicare un Servizio.");
					return false;
				}
			}
			if (nomeservizio.equals("") || tiposervizio.equals("") || idSoggErogatore.equals("") || idAccordo.equals("") || versione.equals("")) {
				String tmpElenco = "";
				if (nomeservizio.equals("")) {
					tmpElenco = "Nome Servizio";
				}
				if (tiposervizio.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = "Tipo Servizio";
					} else {
						tmpElenco = tmpElenco + ", Tipo Servizio";
					}
				}
				if (idSoggErogatore.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = "Soggetto";
					} else {
						tmpElenco = tmpElenco + ", Soggetto";
					}
				}
				if (idAccordo.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = "Accordo Servizio Parte Comune";
					} else {
						tmpElenco = tmpElenco + ", Accordo Servizio Parte Comune";
					}
				}
				if (versione.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = "Versione Accordo Servizio Parte Specifica";
					} else {
						tmpElenco = tmpElenco + ", Versione Accordo Servizio Parte Specifica";
					}	
				}
				this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if ((nomeservizio.indexOf(" ") != -1) || (tiposervizio.indexOf(" ") != -1)) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}

			// Il nome deve contenere solo lettere e numeri e '_' '-' '.'
			if(this.checkNCName(nomeservizio,AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_NOME_FILE+" Servizio")==false){
				return false;
			}

			// Il tipo deve contenere solo lettere e numeri
			if(this.checkNCName(tiposervizio,AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_TIPO_FILE+" Servizio")==false){
				return false;
			}

			// Versione deve essere un numero intero
			if (!versione.equals("") && !this.checkNumber(versione, AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_VERSIONE, false)) {
				return false;
			}

			// Controllo dell'end-point
			// Non li puo' prendere dalla servtlet
			if (!this.endPointCheckData(endpointtype, url, nome, tipo,
					user, password, initcont, urlpgk, provurl, connfact,
					sendas, httpsurl, httpstipologia, httpshostverify,
					httpspath, httpstipo, httpspwd, httpsalgoritmo, httpsstato,
					httpskeystore, httpspwdprivatekeytrust, httpspathkey,
					httpstipokey, httpspwdkey, httpspwdprivatekey,
					httpsalgoritmokey, tipoconn,autenticazioneHttp,
					proxyEnabled, proxyHost, proxyPort, proxyUsername, proxyPassword,
					opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
					requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
					responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
					listExtendedConnettore)) {
				return false;
			}

			// Controllo che i campi "checkbox" abbiano uno dei valori
			// ammessi
			if ((servcorr != null) && !servcorr.equals(Costanti.CHECK_BOX_ENABLED) && !servcorr.equals(Costanti.CHECK_BOX_DISABLED)) {
				this.pd.setMessage("Servizio correlato dev'essere selezionato o deselezionato");
				return false;
			}

			/*
			 * if ((servpub != null) && !servpub.equals(Costanti.CHECK_BOX_ENABLED)) {
			 * this.pd.setMessage("Servizio pubblico dev'essere selezionato o
			 * deselezionato"); return false; }
			 */

			// Controllo che il provider appartenga alla lista di
			// providers disponibili
			boolean trovatoProv = false;
			for (int i = 0; i < soggettiList.length; i++) {
				String tmpSogg = soggettiList[i];
				if (tmpSogg.equals(idSoggErogatore)) {
					trovatoProv = true;
				}
			}
			if (!trovatoProv) {
				this.pd.setMessage("Il soggetto dev'essere scelto tra quelli definiti nel pannello Soggetti");
				return false;
			}

			// Controllo che l'accordo appartenga alla lista di
			// accordi disponibili
			boolean trovatoAcc = false;
			for (int i = 0; i < accordiList.length; i++) {
				String tmpAcc = accordiList[i];
				if (tmpAcc.equals(idAccordo)) {
					trovatoAcc = true;
				}
			}
			if (!trovatoAcc) {
				this.pd.setMessage("L'accordo servizio dev'essere scelto tra quelli definiti nel pannello Accordi servizio");
				return false;
			}


			// Visibilita rispetto al soggetto erogatore
			if(visibilitaServizio==visibile){
				org.openspcoop2.core.registry.Soggetto tmpSogg = this.soggettiCore.getSoggettoRegistro(new IDSoggetto(tipoErogatore, nomeErogatore));
				if(tmpSogg.getPrivato()!=null && tmpSogg.getPrivato()==true){
					this.pd.setMessage("Non e' possibile utilizzare un soggetto erogatore con visibilita' privata, in un servizio con visibilita' pubblica.");
					return false;
				}
			}


			// Se il connettore e' disabilitato devo controllare che il
			// connettore del soggetto non sia disabilitato se è di tipo operativo
			if (endpointtype.equals(TipiConnettore.DISABILITATO.getNome())) {
				String eptypeprov = TipiConnettore.DISABILITATO.getNome();

				org.openspcoop2.core.registry.Soggetto soggetto = this.soggettiCore.getSoggettoRegistro(new IDSoggetto(tipoErogatore, nomeErogatore));
				if(this.pddCore.isPddEsterna(soggetto.getPortaDominio())){
					Connettore connettore = soggetto.getConnettore();
					if ((connettore != null) && (connettore.getTipo() != null)) {
						eptypeprov = connettore.getTipo();
					}
	
					if (eptypeprov.equals(TipiConnettore.DISABILITATO.getNome())) {
						this.pd.setMessage("Il connettore del servizio deve essere specificato se non &egrave; stato definito un connettore per il soggetto erogatore");
						return false;
					}
				}
				else{
					if(tipoOp.equals(TipoOperazione.CHANGE)){
						boolean escludiSoggettiEsterni = true;
						boolean trovatoServ = this.apsCore.existFruizioniServizioWithoutConnettore(idInt,escludiSoggettiEsterni);
						if (trovatoServ) {
							this.pd.setMessage("Il connettore sul servizio non può essere disabilitato poichè non è stato definito un connettore sul soggetto erogatore ed esistono fruizioni del servizio, da parte di soggetti operativi, che non hanno un connettore definito");
							return false;
						}
					}
				}
			}

			// idSoggettoErogatore
			long idSoggettoErogatore = Long.parseLong(idSoggErogatore);
			if (idSoggettoErogatore < 0)
				throw new Exception("id Soggetto erogatore non definito");

			// idAccordo Servizio
			long idAccordoServizio = Long.parseLong(idAccordo);
			if (idAccordoServizio < 0)
				throw new Exception("id Accordo Servizio non definito");

			// Indicazione se e' un servizio correlato
			// boolean isServizioCorrelato = ((servcorr != null && servcorr
			// .equals(Costanti.CHECK_BOX_ENABLED)) ? true : false);

			// Controllo non esistenza gia' del servizio
			if ((!tipoOp.equals(TipoOperazione.CHANGE)) || ((tipoOp.equals(TipoOperazione.CHANGE)) && (!oldTiposervizio.equals(tiposervizio) || !oldNomeservizio.equals(nomeservizio)))) {
				if (this.apsCore.existServizio(nomeservizio, tiposervizio, idSoggettoErogatore) > 0) {
					this.pd.setMessage("Esiste gi&agrave; un servizio con il tipo e nome definito erogato dal Soggetto " + tipoErogatore + "/" + nomeErogatore);
					return false;
				}
			}

			/*
			 * Controllo che non esistano 2 servizi con stesso soggetto erogatore e accordo di servizio 
			 * che siano entrambi correlati o non correlati. 
			 * Al massimo possono esistere 2 servizi di uno stesso accordo erogati da uno stesso soggetto, 
			 * purche' siano uno correlato e uno no. 
			 * Se tipoOp = change, devo fare attenzione a non escludere il servizio selezionato che sto
			 * cambiando 
			 */
			long idSoggettoErogatoreLong = Long.parseLong(idSoggErogatore);
			Soggetto soggettoErogatore = this.soggettiCore.getSoggettoRegistro(idSoggettoErogatoreLong);
			long idAccordoServizioParteComuneLong = Long.parseLong(idAccordo);
			AccordoServizioParteComune accordoServizioParteComune = this.apcCore.getAccordoServizio(idAccordoServizioParteComuneLong);
			
			Integer versioneInt = 1;
			if(versione!=null){
				versioneInt = Integer.parseInt(versione);
			}
			IDServizio idAccordoServizioParteSpecifica = IDServizioFactory.getInstance().getIDServizioFromValues(tiposervizio, nomeservizio, 
					tipoErogatore, nomeErogatore, versioneInt);
			
			long idAccordoServizioParteSpecificaLong = idInt;
			boolean servizioCorrelato = false;
			if ((servcorr != null) && (servcorr.equals(Costanti.CHECK_BOX_ENABLED) || servcorr.equals(CostantiConfigurazione.ABILITATO))) {
				servizioCorrelato = true;
			}
			try{
				this.apsCore.controlloUnicitaImplementazioneAccordoPerSoggetto(portType, 
						new IDSoggetto(soggettoErogatore.getTipo(), soggettoErogatore.getNome()), idSoggettoErogatoreLong, 
						this.idAccordoFactory.getIDAccordoFromAccordo(accordoServizioParteComune), idAccordoServizioParteComuneLong, 
						idAccordoServizioParteSpecifica, idAccordoServizioParteSpecificaLong, 
						tipoOp, servizioCorrelato);
			}catch(Exception e){
				this.pd.setMessage(e.getMessage());
				return false;
			}


			// Controllo che non esistano altri accordi di servizio parte specifica con stesso nome, versione e soggetto
			if (tipoOp.equals(TipoOperazione.ADD)){
				if(this.apsCore.existsAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica)){
					if(this.core.isShowVersioneAccordoServizioParteSpecifica())
						this.pd.setMessage("Esiste gi&agrave; un accordo di servizio parte specifica con tipo, nome, versione e soggetto indicato.");
					else
						this.pd.setMessage("Esiste gi&agrave; un accordo di servizio parte specifica con tipo, nome, versione e soggetto indicato.");
					return false;
				}
			}else{
				// change
				try{
					AccordoServizioParteSpecifica servizio =  this.apsCore.getServizio(idAccordoServizioParteSpecifica);
					if(servizio!=null){
						if (idInt != servizio.getId()) {
							if(this.core.isShowVersioneAccordoServizioParteSpecifica())
								this.pd.setMessage("Esiste gi&agrave; un accordo di servizio parte specifica con tipo, nome, versione e soggetto indicato.");
							else
								this.pd.setMessage("Esiste gi&agrave; un accordo di servizio parte specifica con tipo, nome, versione e soggetto indicato.");
							return false;
						}
					}
				}catch(DriverRegistroServiziNotFound dNotFound){}
			}


			// Controllo che non esista un'altra Porta Applicativa con stesso nome
			if(this.core.isGenerazioneAutomaticaPorteApplicative()){
				if(nomePA!=null && !"".equals(nomePA)){

					IDPortaApplicativa idPA = new IDPortaApplicativa();
					idPA.setNome(nomePA);
					if(this.porteApplicativeCore.existsPortaApplicativa(idPA)){
						this.pd.setMessage("Esiste gi&agrave; una porta applicativa con nome "+nomePA);
						return false;
					}
				}
			}


			// Lettura accordo di servizio
			AccordoServizioParteComune as = null;
			try{
				as = this.apcCore.getAccordoServizio(Long.parseLong(idAccordo));
			}catch(Exception e){
				this.pd.setMessage("Accordo di servizio parte comune selezionato ("+idAccordo+") non esistente: "+e.getMessage());
				return false;
			}

			AccordoServizioParteSpecifica aps = new AccordoServizioParteSpecifica();
			aps.setTipo(tiposervizio);
			aps.setNome(nomeservizio);
			if(versione!=null)
				aps.setVersione(Integer.parseInt(versione));
			if(Costanti.CHECK_BOX_ENABLED.equals(servcorr)){
				aps.setTipologiaServizio(TipologiaServizio.CORRELATO);
			}
			else{
				aps.setTipologiaServizio(TipologiaServizio.NORMALE);
			}
			aps.setTipoSoggettoErogatore(tipoErogatore);
			aps.setNomeSoggettoErogatore(nomeErogatore);

			String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoErogatore);

			ValidazioneResult v = this.apsCore.validazione(aps, this.soggettiCore);
			if(v.isEsito()==false){
				this.pd.setMessage("[validazione-"+protocollo+"] "+v.getMessaggioErrore());
				if(v.getException()!=null)
					this.log.error("[validazione-"+protocollo+"] "+v.getMessaggioErrore(),v.getException());
				else
					this.log.error("[validazione-"+protocollo+"] "+v.getMessaggioErrore());
				return false;
			}	

			// Validazione Documenti
			if(validazioneDocumenti && tipoOp.equals(TipoOperazione.ADD)){

				String wsdlimplerS = wsdlimpler.getValue() != null ? new String(wsdlimpler.getValue()) : null; 
				byte [] wsdlImplementativoErogatore = wsdlimplerS != null && !wsdlimplerS.trim().replaceAll("\n", "").equals("") ? wsdlimplerS.trim().getBytes() : null;
				String wsdlimplfruS = wsdlimplfru.getValue() != null ? new String(wsdlimplfru.getValue()) : null; 
				byte [] wsdlImplementativoFruitore = wsdlimplfruS != null && !wsdlimplfruS.trim().replaceAll("\n", "").equals("") ? wsdlimplfruS.trim().getBytes() : null;
				
				aps.setByteWsdlImplementativoErogatore(wsdlImplementativoErogatore);
				aps.setByteWsdlImplementativoFruitore(wsdlImplementativoFruitore);

				ValidazioneResult result = this.apsCore.validaInterfacciaWsdlParteSpecifica(aps, as, this.soggettiCore);
				if(result.isEsito()==false){
					this.pd.setMessage(result.getMessaggioErrore());
					return false;
				}	

			}

			if(tipoOp.equals(TipoOperazione.ADD)){

				if(this.controlloAccessiCheck(tipoOp, erogazioneAutenticazione, erogazioneAutenticazioneOpzionale, 
						erogazioneAutorizzazione, erogazioneAutorizzazioneAutenticati, erogazioneAutorizzazioneRuoli, 
						erogazioneAutorizzazioneRuoliTipologia, erogazioneAutorizzazioneRuoliMatch,
						isSupportatoAutenticazione, false, null)==false){
					return false;
				}
				
			}
			
			
			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}


	// Controlla i dati del fruitore del servizio
	public boolean serviziFruitoriCheckData(TipoOperazione tipoOp, String[] soggettiList,
			String id, String nomeservizio, String tiposervizio, Integer versioneservizio,
			String nomeprov, String tipoprov, String idSoggettoFruitore,
			String endpointtype, String url, String nome, String tipo,
			String user, String password, String initcont,
			String urlpgk, String provurl, String connfact,
			String sendas, BinaryParameter wsdlimpler, BinaryParameter wsdlimplfru,
			String myId, String profilo,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey, String tipoconn, String clientAuth,
			boolean validazioneDocumenti, String backToStato,
			String autenticazioneHttp,
			String proxyEnabled, String proxyHost, String proxyPort, String proxyUsername, String proxyPassword,
			String opzioniAvanzate, String transfer_mode, String transfer_mode_chunk_size, String redirect_mode, String redirect_max_hop,
			String requestOutputFileName,String requestOutputFileNameHeaders,String requestOutputParentDirCreateIfNotExists,String requestOutputOverwriteIfExists,
			String responseInputMode, String responseInputFileName, String responseInputFileNameHeaders, String responseInputDeleteAfterRead, String responseInputWaitTime,
			String fruizioneServizioApplicativo,String fruizioneRuolo,String fruizioneAutenticazione,String fruizioneAutenticazioneOpzionale, String fruizioneAutorizzazione,
			String fruizioneAutorizzazioneAutenticati,String fruizioneAutorizzazioneRuoli, String fruizioneAutorizzazioneRuoliTipologia, String fruizioneAutorizzazioneRuoliMatch,
			List<ExtendedConnettore> listExtendedConnettore)
					throws Exception {
		try {
			// ripristina dello stato solo in modalita change
			if(backToStato != null && tipoOp.equals(TipoOperazione.CHANGE)){
				return true;
			}

			int myIdInt = 0;
			if (tipoOp.equals(TipoOperazione.CHANGE)) {
				myIdInt = Integer.parseInt(myId);
			}
			if (idSoggettoFruitore == null) {
				idSoggettoFruitore = "";
			}
			if (url == null) {
				url = "";
			}
			if (nome == null) {
				nome = "";
			}
			if (tipo == null) {
				tipo = "";
			}
			if (user == null) {
				user = "";
			}
			if (password == null) {
				password = "";
			}
			if (initcont == null) {
				initcont = "";
			}
			if (urlpgk == null) {
				urlpgk = "";
			}
			if (provurl == null) {
				provurl = "";
			}
			if (connfact == null) {
				connfact = "";
			}
			if (sendas == null) {
				sendas = "";
			}
			// Campi obbligatori
			if (idSoggettoFruitore.equals("")) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare un Soggetto");
				return false;
			}

			int idInt = 0;
			if (!nomeservizio.equals("")) {
				IDSoggetto ids = new IDSoggetto(tipoprov, nomeprov);
				IDServizio idserv = IDServizioFactory.getInstance().getIDServizioFromValues(tiposervizio, nomeservizio, ids, versioneservizio);
				AccordoServizioParteSpecifica myServ = this.apsCore.getServizio(idserv);
				idInt = myServ.getId().intValue();
			} else {
				idInt = Integer.parseInt(id);
			}
			AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(idInt);
			
			User userLogin = ServletUtils.getUserFromSession(this.session);
			
			
			// Se il connettore e' disabilitato devo controllare che il
			// connettore del soggetto non sia disabilitato se è di tipo operativo
			if (endpointtype.equals(TipiConnettore.DISABILITATO.getNome())) {
				String eptypeprov = TipiConnettore.DISABILITATO.getNome();

				org.openspcoop2.core.registry.Soggetto soggetto = this.soggettiCore.getSoggettoRegistro(Long.parseLong(idSoggettoFruitore));
				if(this.pddCore.isPddEsterna(soggetto.getPortaDominio())==false){
					// sto attivando una fruizione su un soggetto operativo

					Connettore connettore = asps.getConfigurazioneServizio().getConnettore();
					if ((connettore != null) && (connettore.getTipo() != null)) {
						eptypeprov = connettore.getTipo();
					}
					if (eptypeprov.equals(TipiConnettore.DISABILITATO.getNome())) {
					
						org.openspcoop2.core.registry.Soggetto soggettoErogatore = 
								this.soggettiCore.getSoggettoRegistro(new IDSoggetto(asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore()));
						connettore = soggettoErogatore.getConnettore();
						if ((connettore != null) && (connettore.getTipo() != null)) {
							eptypeprov = connettore.getTipo();
						}
		
						if (eptypeprov.equals(TipiConnettore.DISABILITATO.getNome())) {
							if(tipoOp.equals(TipoOperazione.CHANGE)){
								this.pd.setMessage("Per poter disabilitare il connettore deve prima essere definito un connettore sul servizio o sul soggetto erogatore");
							}
							else{
								if(InterfaceType.AVANZATA.equals(userLogin.getInterfaceType())){
									this.pd.setMessage("Per poter aggiungere il fruitore deve essere definito il connettore.<br/>In alternativa è possibile configurare un connettore sul servizio o sul soggetto erogatore prima di procedere con la creazione del fruitore.");
								}
								else{
									this.pd.setMessage("Per poter aggiungere il fruitore deve prima essere definito un connettore sul servizio o sul soggetto erogatore.");
								}
							}
							return false;
						}
						
					}
					
				}
				
			}
			
			// // Controllo che il provider appartenga alla lista di
			// // providers disponibili
			// boolean trovatoProv = false;
			// for (int i = 0; i < soggettiList.length; i++) {
			// String tmpSogg = soggettiList[i];
			// if (tmpSogg.equals(provider)) {
			// trovatoProv = true;
			// }
			// }
			// if (!trovatoProv) {
			// this.pd.setMessage("Il soggetto dev'essere scelto tra
			// quelli definiti nel pannello Soggetti");
			// return false;
			// }

			// Controllo dell'end-point
			// Non li puo' prendere dalla servtlet
			if (!this.endPointCheckData(endpointtype, url, nome, tipo,
					user, password, initcont, urlpgk, provurl, connfact,
					sendas, httpsurl, httpstipologia, httpshostverify,
					httpspath, httpstipo, httpspwd, httpsalgoritmo, httpsstato,
					httpskeystore, httpspwdprivatekeytrust, httpspathkey,
					httpstipokey, httpspwdkey, httpspwdprivatekey,
					httpsalgoritmokey, tipoconn,autenticazioneHttp,
					proxyEnabled, proxyHost, proxyPort, proxyUsername, proxyPassword,
					opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
					requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
					responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
					listExtendedConnettore)) {
				return false;
			}

			// 2 fruitori dello stesso servizio non possono avere lo stesso
			// provider
			if (tipoOp.equals(TipoOperazione.CHANGE)) {
				Fruitore myFru = this.apsCore.getServizioFruitore(myIdInt);
				String nomeSogg = myFru.getNome();
				String tipoSogg = myFru.getTipo();
				IDSoggetto idfru = new IDSoggetto(tipoSogg, nomeSogg);
				Soggetto mySogg = this.soggettiCore.getSoggettoRegistro(idfru);
				int idProv = mySogg.getId().intValue();

				IDServizio idserv = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
				int idFru = this.apsCore.getServizioFruitore(idserv, idProv);
				if ((idFru != 0) && (tipoOp.equals(TipoOperazione.ADD) || ((tipoOp.equals(TipoOperazione.CHANGE)) && (myIdInt != idFru)))) {
					this.pd.setMessage("Esiste gi&agrave; un fruitore del Servizio con lo stesso Soggetto");
					return false;
				}
			}
			else{

				// Lettura accordo di servizio
				AccordoServizioParteComune as = null;
				IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromUri(asps.getAccordoServizioParteComune());
				try{
					as = this.apcCore.getAccordoServizio(idAccordo);
				}catch(Exception e){
					this.pd.setMessage("Accordo di servizio parte comune selezionato ("+idAccordo+") non esistente: "+e.getMessage());
					return false;
				}

				// Validazione Documenti
				if(validazioneDocumenti && tipoOp.equals(TipoOperazione.ADD) && InterfaceType.AVANZATA.equals(userLogin.getInterfaceType()) ){
					
					String wsdlimplerS = wsdlimpler.getValue() != null ? new String(wsdlimpler.getValue()) : null; 
					byte [] wsdlImplementativoErogatore = wsdlimplerS != null && !wsdlimplerS.trim().replaceAll("\n", "").equals("") ? wsdlimplerS.trim().getBytes() : null;
					String wsdlimplfruS = wsdlimplfru.getValue() != null ? new String(wsdlimplfru.getValue()) : null; 
					byte [] wsdlImplementativoFruitore = wsdlimplfruS != null && !wsdlimplfruS.trim().replaceAll("\n", "").equals("") ? wsdlimplfruS.trim().getBytes() : null;

					//aps.setByteWsdlImplementativoErogatore(wsdlImplementativoErogatore);
					//aps.setByteWsdlImplementativoFruitore(wsdlImplementativoFruitore);

					Fruitore f = new Fruitore();
					f.setByteWsdlImplementativoErogatore(wsdlImplementativoErogatore);
					f.setByteWsdlImplementativoFruitore(wsdlImplementativoFruitore);

					ValidazioneResult result = this.apsCore.validaInterfacciaWsdlParteSpecifica(f,asps, as, this.soggettiCore);
					if(result.isEsito()==false){
						this.pd.setMessage(result.getMessaggioErrore());
						return false;
					}	

				}
				
				if(this.controlloAccessiCheck(tipoOp, fruizioneAutenticazione, fruizioneAutenticazioneOpzionale, 
						fruizioneAutorizzazione, fruizioneAutorizzazioneAutenticati, fruizioneAutorizzazioneRuoli, 
						fruizioneAutorizzazioneRuoliTipologia, fruizioneAutorizzazioneRuoliMatch,
						true, true, null)==false){
					return false;
				}

			}

			// Client-auth
			if(CostantiConfigurazione.ABILITATO.equals(clientAuth)){
				Soggetto mySogg = this.soggettiCore.getSoggettoRegistro(Long.parseLong(idSoggettoFruitore));
				IDSoggetto idfru = new IDSoggetto(mySogg.getTipo(),mySogg.getNome());
				PortaDominio pdd = this.pddCore.getPortaDominio(mySogg.getPortaDominio());
				if(pdd.getSubject()==null || "".equals(pdd.getSubject())){
					this.pd.setMessage("Funzionalità di client-auth non abilitabile, poichè non è stato specificato un subject nella Porta di Dominio "+pdd.getNome()+" del soggetto "+idfru.toString());
					return false;
				}
			}

			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public boolean serviziAllegatiCheckData(TipoOperazione tipoOp,FormFile formFile,Documento documento)
			throws Exception {

		try{

			// String userLogin = (String) this.session.getAttribute("Login");

			String ruolo = this.request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_RUOLO);

			// Campi obbligatori
			if (ruolo.equals("")) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare il Tipo di documento");
				return false;
			}

			if(formFile==null || formFile.getFileName()!=null && "".equals(formFile.getFileName())){
				this.pd.setMessage("E' necessario selezionare un documento.");
				return false;
			}

			if(formFile==null || formFile.getFileSize()<=0){
				this.pd.setMessage("Il documento selezionato non puo essere vuoto.");
				return false;
			}

			if(documento.getTipo()==null || "".equals(documento.getTipo()) || documento.getTipo().length()>30 || formFile.getFileName().lastIndexOf(".")==-1){
				if(documento.getTipo()==null || "".equals(documento.getTipo()) || formFile.getFileName().lastIndexOf(".")==-1){
					this.pd.setMessage("L'estensione del documento non e' valida.");
				}else{
					this.pd.setMessage("L'estensione del documento non e' valida. La dimensione dell'estensione e' troppo lunga.");
				}
				return false;
			}

			if(this.archiviCore.existsDocumento(documento,ProprietariDocumento.servizio)){

				//check se stesso documento
				Documento existing = this.archiviCore.getDocumento(documento.getFile(),documento.getTipo(),documento.getRuolo(),documento.getIdProprietarioDocumento(),false,ProprietariDocumento.servizio);
				if(existing.getId() == documento.getId())
					return true;

				if(RuoliDocumento.allegato.toString().equals(documento.getRuolo()))
					this.pd.setMessage("L'allegato con nome "+documento.getFile()+" (tipo: "+documento.getTipo()+") e' gia' presente nel servizio.");
				else
					this.pd.setMessage("La "+documento.getRuolo()+" con nome "+documento.getFile()+" (tipo: "+documento.getTipo()+") e' gia' presente nel servizio.");

				return false;
			}


			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public void prepareServiziAllegatiList(AccordoServizioParteSpecifica asps, ISearch ricerca, List<Documento> lista) throws Exception {
		try {
			String id = this.request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			ServletUtils.addListElementIntoSession(this.session, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_ALLEGATI, 
					new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, id));

			int idLista = Liste.SERVIZI_ALLEGATI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			String tmpTitle = IDServizioFactory.getInstance().getUriFromAccordo(asps); 

			// setto la barra del titolo
			List<Parameter> lstParm = new ArrayList<Parameter>();

			lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
			lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
					AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_ALLEGATI_DI + tmpTitle, null));
			}else{
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_ALLEGATI_DI + tmpTitle,
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ALLEGATI_LIST, 
						new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId() + "")
						));
				lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParm );

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription("Allegati contenenti la stringa '" + search + "'");
			}

			// setto le label delle colonne
			String[] labels = {
					AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_NOME_FILE,
					AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_RUOLO,
					AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_TIPO_FILE,
					AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_THE_FILE
			};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<Documento> it = lista.iterator();
				while (it.hasNext()) {
					Documento doc = it.next();

					Parameter pIdAllegato= new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_ALLEGATO, doc.getId() + "");
					Parameter pId= new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId() + "");
					Parameter pNomeDoc= new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_DOCUMENTO, doc.getFile());

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setUrl(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ALLEGATI_CHANGE,
							pIdAllegato,pId,pNomeDoc);
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
					de.setUrl(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ALLEGATI_VIEW,
							pIdAllegato,pId,pNomeDoc);
					de.setValue(Costanti.LABEL_VISUALIZZA);
					e.addElement(de);

					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);

			if(this.core.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(asps.getStatoPackage())){
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

	public void prepareServiziList(ISearch ricerca, List<AccordoServizioParteSpecifica> lista)
			throws Exception {
		try {

			ServletUtils.addListElementIntoSession(this.session, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS);

			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

			boolean isModalitaAvanzata = InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType());
			
			User user = ServletUtils.getUserFromSession(this.session);

			Boolean isAccordiCooperazione = user.getPermessi().isAccordiCooperazione();
			Boolean isServizi = user.getPermessi().isServizi();

			int idLista = Liste.SERVIZI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
			// setto la barra del titolo
			List<Parameter> lstParm = new ArrayList<Parameter>();

			lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
//			lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
//					AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, null));
			}else{
				lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParm );

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription("Servizi contenenti la stringa '" + search + "'");
			}

			// setto le label delle colonne
			String[] labels = null;
			//String servizioLabel = AccordiServizioParteSpecificaCostanti.LABEL_APS;

			String asLabel = AccordiServizioParteSpecificaCostanti.LABEL_APC_COMPOSTO;

			if(isServizi && !isAccordiCooperazione){
				asLabel = AccordiServizioParteSpecificaCostanti.LABEL_APC_COMPOSTO_SOLO_PARTE_COMUNE;
			}

			if(!isServizi  && isAccordiCooperazione){
				asLabel = AccordiServizioParteSpecificaCostanti.LABEL_APC_COMPOSTO_SOLO_COMPOSTO;
			}

			if(isServizi  && isAccordiCooperazione){
				asLabel = AccordiServizioParteSpecificaCostanti.LABEL_APC_COMPOSTO;
			}

			String correlatoLabel = AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_RUOLO;

			String fruitoriLabel = AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORI;

			// controllo visualizzazione colonna ruolo

			List<String> protocolli = this.core.getProtocolli();
			boolean showRuoli = false;
			for (String protocollo : protocolli) {
				showRuoli = showRuoli || this.core.isProfiloDiCollaborazioneAsincronoSupportatoDalProtocollo(protocollo);
			}

			List<String> listaLabelTabella = new ArrayList<String>();

			listaLabelTabella.add(AccordiServizioParteSpecificaCostanti.LABEL_APS_SERVIZIO);
			listaLabelTabella.add(AccordiServizioParteSpecificaCostanti.LABEL_APS_SOGGETTO_EROGATORE);
			listaLabelTabella.add(asLabel);
			if(showRuoli)
				listaLabelTabella.add(correlatoLabel);
			if(this.core.isShowGestioneWorkflowStatoDocumenti())
				listaLabelTabella.add(AccordiServizioParteSpecificaCostanti.LABEL_APS_STATO);
			listaLabelTabella.add(fruitoriLabel);
			if(isModalitaAvanzata){
				listaLabelTabella.add(AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_APPLICATIVE);
			}
			listaLabelTabella.add(AccordiServizioParteSpecificaCostanti.LABEL_APS_ALLEGATI);

			labels = listaLabelTabella.toArray(new String[listaLabelTabella.size()]);

			// "Politiche SLA" };
			this.pd.setLabels(labels);

			// Prendo la lista di accordi dell'utente connesso
			/*List<Long> idsAcc = new ArrayList<Long>();
			List<AccordoServizio> listaAcc = this.core.accordiList(superUser, new Search());
			Iterator<AccordoServizio> itA = listaAcc.iterator();
			while (itA.hasNext()) {
				AccordoServizio as = (AccordoServizio) itA.next();
				idsAcc.add(as.getId());
			}*/

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			for (AccordoServizioParteSpecifica asps : lista) {

				Vector<DataElement> e = new Vector<DataElement>();

				Parameter pIdsoggErogatore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+asps.getIdSoggetto());
				Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, asps.getNome());
				Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, asps.getTipo());
				@SuppressWarnings("unused")
				Parameter pVersioneServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VERSIONE, asps.getVersione().intValue()+"");

				Integer versione = asps.getVersione();
				if(this.core.isShowVersioneAccordoServizioParteSpecifica()==false){
					versione = null;
				}
				String uriASPS = this.idAccordoFactory.getUriFromValues(asps.getNome(), 
						asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore(), 
						versione);
				

				Soggetto sog = this.soggettiCore.getSoggettoRegistro(asps.getIdSoggetto());
				boolean isPddEsterna = 
						this.pddCore.isPddEsterna(sog.getPortaDominio());
				
//				DataElement de = new DataElement();
//				de.setUrl(
//						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE,
//						new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, servizio.getId() + ""),
//						pNomeServizio, pTipoServizio, pIdsoggErogatore);
//				de.setValue(uriASPS);
//				de.setIdToRemove(servizio.getTipo() + "/" + servizio.getNome() + "/" + servizio.getTipoSoggettoErogatore() + "/" + servizio.getNomeSoggettoErogatore());
//				de.setToolTip(asps.getDescrizione());
//				e.addElement(de);

				DataElement de = new DataElement();
				de.setUrl(
					AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE,
					new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId() + ""),
					pNomeServizio, pTipoServizio, pIdsoggErogatore);
				de.setValue(asps.getTipo()+"/"+asps.getNome());
				de.setIdToRemove(asps.getTipo() + "/" + asps.getNome() + "/" + asps.getTipoSoggettoErogatore() + "/" + asps.getNomeSoggettoErogatore());
				de.setToolTip(uriASPS);
				e.addElement(de);

				
				
				de = new DataElement();
				de.setUrl(
						SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE, 
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID, asps.getIdSoggetto() + ""),
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO, asps.getTipoSoggettoErogatore()),
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME, asps.getNomeSoggettoErogatore()));
				de.setValue(asps.getTipoSoggettoErogatore()+"/"+asps.getNomeSoggettoErogatore());
				e.addElement(de);
				

				
				de = new DataElement();
				//if (idsAcc.contains(servizio.getIdAccordo()))
				// accordiChange.do

				AccordoServizioParteComune apc = this.apcCore.getAccordoServizio(asps.getIdAccordo());

				Parameter pTipoAccordo = AccordiServizioParteComuneUtilities.getParametroAccordoServizio(apc);

				de.setUrl(
						AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_CHANGE, 
						new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getIdAccordo() + ""),
						new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME, apc.getNome()), pTipoAccordo);
				de.setValue(asps.getAccordoServizioParteComune());
				e.addElement(de);
				
				

				// Colonna ruoli
				String tipoSoggetto = asps.getTipoSoggettoErogatore();
				String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoSoggetto);

				if(showRuoli){
					de = new DataElement();
					if(this.core.isProfiloDiCollaborazioneAsincronoSupportatoDalProtocollo(protocollo)){ 
						de.setValue((TipologiaServizio.CORRELATO.equals(asps.getTipologiaServizio()) ?
								AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_CORRELATO :
									AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_NORMALE));
	
					}else {
						de.setValue("-");
					}
					e.addElement(de);
				}

				if(this.core.isShowGestioneWorkflowStatoDocumenti()){
					de = new DataElement();
					de.setValue(asps.getStatoPackage());
					e.addElement(de);
				}

				de = new DataElement();
				de.setUrl(
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST,
						new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId() + ""),
						pNomeServizio, pTipoServizio, pIdsoggErogatore);
				// +"&nomeprov="+soggErogatore+"&tipoprov="+tipoSoggEr);
				if (contaListe) {
					// BugFix OP-674
					//List<Fruitore> lista1 = this.apsCore.serviziFruitoriList(asps.getId().intValue(), new Search(true));
					Search searchForCount = new Search(true,1);
					this.apsCore.serviziFruitoriList(asps.getId().intValue(), searchForCount);
					//int numFru = lista1.size();
					int numFru = searchForCount.getNumEntries(Liste.SERVIZI_FRUITORI);
					ServletUtils.setDataElementVisualizzaLabel(de, (long) numFru);
				} else
					ServletUtils.setDataElementVisualizzaLabel(de);
				e.addElement(de);

				if(isModalitaAvanzata){
					de = new DataElement();
					if(isPddEsterna){
						de.setType(DataElementType.TEXT);
						de.setValue("-");
					}
					else{
						de.setUrl(
								AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_PORTE_APPLICATIVE_LIST,
								new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId() + ""),
								pNomeServizio, pTipoServizio, pIdsoggErogatore );
						// +"&nomeprov="+soggErogatore+"&tipoprov="+tipoSoggEr);
						if (contaListe) {
							// BugFix OP-674
							//List<PortaApplicativa> lista1 = this.apsCore.serviziPorteAppList(servizio.getTipo(),servizio.getNome(),asps.getVersione(),
							//		asps.getId().intValue(), asps.getIdSoggetto(), new Search(true));
							Search searchForCount = new Search(true,1);
							this.apsCore.serviziPorteAppList(asps.getTipo(),asps.getNome(),asps.getVersione(),
									asps.getId().intValue(), asps.getIdSoggetto(), searchForCount);
							//int numPA = lista1.size();
							int numPA = searchForCount.getNumEntries(Liste.SERVIZI_PORTE_APPLICATIVE);
							ServletUtils.setDataElementVisualizzaLabel(de, (long) numPA );
						} else
							ServletUtils.setDataElementVisualizzaLabel(de);
					}
					e.addElement(de);
				}

				// de = new DataElement();
				// de.setUrl("serviziRuoli.do?id="+servizio.getId());
				// de.setValue("visualizza");
				// e.addElement(de);
				//
				// de = new DataElement();
				// de.setValue("non disp.");
				// e.addElement(de);

				de = new DataElement();

				de.setUrl(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ALLEGATI_LIST,
						new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId() + ""));
				if (contaListe) {
					// BugFix OP-674
					//List<org.openspcoop2.core.registry.Documento> tmpLista = this.apsCore.serviziAllegatiList(asps.getId().intValue(), new Search(true));
					Search searchForCount = new Search(true,1);
					this.apsCore.serviziAllegatiList(asps.getId().intValue(), searchForCount);
					//int numAllegati = tmpLista.size();
					int numAllegati = searchForCount.getNumEntries(Liste.SERVIZI_ALLEGATI);
					ServletUtils.setDataElementVisualizzaLabel(de, (long) numAllegati);
				} else
					ServletUtils.setDataElementVisualizzaLabel(de);
				e.addElement(de);

				dati.addElement(e);
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(this.core.isShowPulsanteAggiungiElenchi());

			// preparo bottoni
			// String gestioneWSBL = (String) this.session
			// .getAttribute("GestioneWSBL");
			if(lista!=null && lista.size()>0){
				if (this.core.isShowPulsantiImportExport()) {

					ExporterUtils exporterUtils = new ExporterUtils(this.archiviCore);
					if(exporterUtils.existsAtLeastOneExportMpde(ArchiveType.ACCORDO_SERVIZIO_PARTE_SPECIFICA)){

						Vector<AreaBottoni> bottoni = new Vector<AreaBottoni>();

						AreaBottoni ab = new AreaBottoni();
						Vector<DataElement> otherbott = new Vector<DataElement>();
						DataElement de = new DataElement();
						de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_APS_ESPORTA_SELEZIONATI);
						de.setOnClick(AccordiServizioParteSpecificaCostanti.LABEL_APS_ESPORTA_SELEZIONATI_ONCLICK);
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


	public void prepareServiziFruitoriList(List<Fruitore> lista, String id, ISearch ricerca)
			throws Exception {
		try {
			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
			Boolean generazioneAutomaticaPD = ServletUtils.getGenerazioneAutomaticaPDFromSession(this.session);

			boolean isModalitaAvanzata = InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType());

			ServletUtils.addListElementIntoSession(this.session, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI, 
					new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, id));

			int idLista = Liste.SERVIZI_FRUITORI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			String idSoggettoErogatoreDelServizio = this.request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE);
			if ((idSoggettoErogatoreDelServizio == null) || idSoggettoErogatoreDelServizio.equals("")) {
				PageData oldPD = ServletUtils.getPageDataFromSession(this.session);

				idSoggettoErogatoreDelServizio = oldPD.getHidden(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE);

				if (idSoggettoErogatoreDelServizio == null || idSoggettoErogatoreDelServizio.equals("")) {
					AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(id));
					String tipoSoggettoErogatore = asps.getTipoSoggettoErogatore();
					String nomesoggettoErogatore = asps.getNomeSoggettoErogatore();
					IDSoggetto idSE = new IDSoggetto(tipoSoggettoErogatore, nomesoggettoErogatore);
					Soggetto SE = this.soggettiCore.getSoggettoRegistro(idSE);
					idSoggettoErogatoreDelServizio = "" + SE.getId();
				}
			}

			// setto i dati come campi hidden nel pd per portarmeli dietro
			this.pd.addHidden(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, idSoggettoErogatoreDelServizio);

			// Prendo il nome e il tipo del servizio
			AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(id));
			
			// Prendo il nome e il tipo del soggetto erogatore del servizio
			//Soggetto sogg = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(idSoggettoErogatoreDelServizio));

			String tmpTitle = IDServizioFactory.getInstance().getUriFromAccordo(asps); 

			// setto la barra del titolo
			List<Parameter> lstParm = new ArrayList<Parameter>();

			lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
			lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));

			if (search.equals("")) {
				this.pd.setSearchDescription("");
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FUITORI_DI  + tmpTitle, null));
			}else {
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FUITORI_DI  + tmpTitle, 
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST ,
						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+ id)
						));
				lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParm );


			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription("Fruitori di " + tmpTitle + " contenenti la stringa '" + search + "'");
			}

			// setto le label delle colonne
			//User user = ServletUtils.getUserFromSession(this.session);
			String[] labels;
			String labelFruitore = AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORE;
			//if(this.core.isTerminologiaSICA_RegistroServizi()){
			//	labelFruitore = "Adesione";
			//}
			boolean showPoliticheSLA = false;
			//if (!isModalitaAvanzata) {
			if(generazioneAutomaticaPD && isModalitaAvanzata){
				if(this.core.isShowGestioneWorkflowStatoDocumenti()){
					String[] l = { labelFruitore , AccordiServizioParteSpecificaCostanti.LABEL_APS_STATO ,
							AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_DELEGATE};
					labels = l;
				}else{
					String[] l = { labelFruitore , 
							AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_DELEGATE};
					labels = l;
				}
			}else{
				if(this.core.isShowGestioneWorkflowStatoDocumenti()){
					String[] l = { labelFruitore, AccordiServizioParteSpecificaCostanti.LABEL_APS_STATO };
					labels = l;
				}
				else{
					String[] l = { labelFruitore };
					labels = l;
				}
			}
			//			} else {
			//				if(this.core.isShowGestioneWorkflowStatoDocumenti()){
			//					String[] l = { labelFruitore,"Stato", "Servizi Applicativi Autorizzati", "Politiche SLA" };
			//					labels = l;
			//				}else{
			//					String[] l = { labelFruitore, "Servizi Applicativi Autorizzati", "Politiche SLA" };
			//					labels = l;
			//				}
			//				showPoliticheSicurezza = true;
			//				showPoliticheSLA = true;
			//			}
			this.pd.setLabels(labels);


			Parameter pIdSoggettoErogatore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, idSoggettoErogatoreDelServizio);
			Parameter pId = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, id);
			Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, asps.getNome());
			Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, asps.getTipo());
			@SuppressWarnings("unused")
			Parameter pVersioneServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VERSIONE, asps.getVersione().intValue()+"");

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			Iterator<Fruitore> it = lista.iterator();
			Fruitore fru = null;
			while (it.hasNext()) {
				fru = it.next();
				Vector<DataElement> e = new Vector<DataElement>();

				Parameter pMyId = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID, fru.getId() + "");				

				DataElement de = new DataElement();
				de.setType(DataElementType.HIDDEN);
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_FRUITORE);
				de.setValue("" + fru.getId());
				e.addElement(de);

				de = new DataElement();
				de.setUrl(
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE,
						pId,	pMyId, pIdSoggettoErogatore,
						new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE, fru.getIdSoggetto() + ""));

				de.setValue(fru.getTipo() + "/" + fru.getNome());
				de.setIdToRemove(fru.getId().toString());
				e.addElement(de);

				if(this.core.isShowGestioneWorkflowStatoDocumenti()){
					de = new DataElement();
					de.setValue(fru.getStatoPackage());
					e.addElement(de);
				}

				Parameter pIdSogg = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO, fru.getIdSoggetto() + "");
				// devo aggiungere le politiche di sicurezza come in
				// accordiServizioApplicativoList
				if(generazioneAutomaticaPD && isModalitaAvanzata){
										
					Soggetto fruitoreSogg = this.soggettiCore.getSoggettoRegistro(new IDSoggetto(fru.getTipo(), fru.getNome()));
					
					boolean esterno = this.pddCore.isPddEsterna(fruitoreSogg.getPortaDominio());

					de = new DataElement();
					if(esterno){
						de.setValue("-");
					}
					else{
						de.setUrl(
								AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_PORTE_DELEGATE_LIST,
								pId, pIdSogg, pIdSoggettoErogatore, pNomeServizio, pTipoServizio, pMyId);
						if (contaListe) {
							// BugFix OP-674
//							List<PortaDelegata> fruLista = this.apsCore.serviziFruitoriPorteDelegateList(this.soggettiCore.getIdSoggetto(fru.getNome(), fru.getTipo()), 
//									serv.getTipo(), serv.getNome(), asps.getId(), 
//									serv.getTipoSoggettoErogatore(), serv.getNomeSoggettoErogatore(), asps.getIdSoggetto(), 
//									ricerca);
							Search searchForCount = new Search(true,1);
							this.apsCore.serviziFruitoriPorteDelegateList(this.soggettiCore.getIdSoggetto(fru.getNome(), fru.getTipo()), 
									asps.getTipo(), asps.getNome(), asps.getId(), 
									asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore(), asps.getIdSoggetto(), 
									searchForCount);
							//int numPD = fruLista.size();
							int numPD = searchForCount.getNumEntries(Liste.SERVIZI_FRUITORI_PORTE_DELEGATE);
							ServletUtils.setDataElementVisualizzaLabel(de, (long) numPD );
						} else
							ServletUtils.setDataElementVisualizzaLabel(de);
					}
					e.addElement(de);
				}
				if(showPoliticheSLA){
					de = new DataElement();
					de.setValue(Costanti.LABEL_NON_DISPONIBILE);
					e.addElement(de);
				}

				dati.addElement(e);
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

			// preparo bottoni
			// String gestioneWSBL = (String)
			// this.session.getAttribute("GestioneWSBL");
			/*
			 * if(lista!=null && lista.size()>0){
			 * if (AccordiServizioParteComuneUtilities.getImportaEsporta()) {
				Vector<AreaBottoni> bottoni = new Vector<AreaBottoni>();

				AreaBottoni ab = new AreaBottoni();
				Vector<DataElement> otherbott = new Vector<DataElement>();
				DataElement de = new DataElement();
				de.setValue("Esporta Selezionati");
				de.setOnClick("Esporta()");
				otherbott.addElement(de);
				ab.setBottoni(otherbott);
				bottoni.addElement(ab);

				this.pd.setAreaBottoni(bottoni);
			}
			}*/

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public void prepareServiziPorteAppList(List<PortaApplicativa> lista, String id, ISearch ricerca)
			throws Exception {
		try {
			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

			IExtendedListServlet extendedServletList = this.core.getExtendedServletPortaApplicativa();
			
			ServletUtils.addListElementIntoSession(this.session, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE,
					new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, id));

			boolean isModalitaAvanzata = ServletUtils.getUserFromSession(this.session).getInterfaceType().equals(InterfaceType.AVANZATA);

			int idLista = Liste.SERVIZI_PORTE_APPLICATIVE;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			// Deve essere usato il soggetto per visualizzare i servizi applicativi
			ServletUtils.setObjectIntoSession(this.session, true, PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_USA_ID_SOGGETTO);
			
			//questo e' il soggetto virtuale
			String idSoggettoErogatoreDelServizio = this.request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE);
			if ((idSoggettoErogatoreDelServizio == null) || idSoggettoErogatoreDelServizio.equals("")) {
				PageData oldPD = ServletUtils.getPageDataFromSession(this.session);

				idSoggettoErogatoreDelServizio = oldPD.getHidden(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE);
			}

			// setto i dati come campi hidden nel pd per portarmeli dietro
			this.pd.addHidden(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, idSoggettoErogatoreDelServizio);

			// Prendo il nome e il tipo del servizio
			AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(id));

			// Prendo il nome e il tipo del soggetto erogatore del servizio
			Soggetto sogg = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(idSoggettoErogatoreDelServizio));

			String servizioTmpTile = sogg.getTipo() + "/" + sogg.getNome() + "-" + asps.getTipo() + "/" + asps.getNome();
			Parameter pIdServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId()+ "");
			Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, asps.getNome());
			Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, asps.getTipo());
			Parameter pVersioneServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VERSIONE, asps.getVersione().intValue()+"");
			
			// setto la barra del titolo
			List<Parameter> lstParm = new ArrayList<Parameter>();

			lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
			lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
			lstParm.add(new Parameter(servizioTmpTile, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, pIdServizio,pNomeServizio, pTipoServizio));
			
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_APPLICATIVE, null));
			}else {
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_APPLICATIVE, 
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_PORTE_APPLICATIVE_LIST ,
						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+ id)
						));
				lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParm );

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription(AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_APPLICATIVE + " contenenti la stringa '" + search + "'");
			}

			// setto le label delle colonne
			String labelServizio = AccordiServizioParteSpecificaCostanti.LABEL_APS;


			//			
			//			String[] labels = { 
			//					PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME,
			//					PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOGGETTO,
			//					PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_DESCRIZIONE, 
			//					PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZI_APPLICATIVI,
			//					PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_WS_SECURITY, 
			//					PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PROTOCOL_PROPERTIES, 
			//					labelServizio };
			//			this.pd.setLabels(labels);

			List<String> listaLabel = new ArrayList<String>();

			listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
			//listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOGGETTO);
			//listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_DESCRIZIONE);
			listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SERVIZI_APPLICATIVI);
			listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_RUOLI); 
			listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY);
			listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM);
			//}
			listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA);
			if(isModalitaAvanzata)
				listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PROTOCOL_PROPERTIES);
			listaLabel.add(labelServizio);
			if(extendedServletList!=null && extendedServletList.showExtendedInfo(this.request, this.session)){
				listaLabel.add(extendedServletList.getListTitle(this));
			}

			String[] labels = listaLabel.toArray(new String[listaLabel.size()]);
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			Iterator<PortaApplicativa> it = lista.iterator();
			PortaApplicativa pa = null;
			while (it.hasNext()) {
				pa = it.next();
				Vector<DataElement> e = new Vector<DataElement>();

				Parameter pNomePorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA, pa.getNome());
				Parameter pIdNome = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, ""+pa.getNome());
				
				Parameter pIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, pa.getIdSoggetto() + "");
				//pIdSogg = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO, idSoggettoErogatoreDelServizio);
				
				Parameter pIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, ""+pa.getId());
				

				DataElement de = new DataElement();
				//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
				de.setUrl(
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CHANGE, 
						pIdSogg, pNomePorta, pIdPorta);
				de.setValue(pa.getNome());
				de.setIdToRemove(pa.getId().toString());
				de.setToolTip(pa.getDescrizione());
				e.addElement(de);

				// NON SERVE: sono già dentro un soggetto erogatore del servizio
//				de = new DataElement();
//				de.setValue(pa.getTipoSoggettoProprietario()+"/"+pa.getNomeSoggettoProprietario());
//				e.addElement(de);

//				de = new DataElement();
//				de.setValue(pa.getDescrizione());
//				e.addElement(de);

				de = new DataElement();
				//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
				de.setUrl(
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_LIST,
						pIdSogg, pIdPorta);
				if (contaListe) {
					int numSA = pa.sizeServizioApplicativoList();
					ServletUtils.setDataElementVisualizzaLabel(de, (long) numSA );
				} else
					ServletUtils.setDataElementVisualizzaLabel(de);
				e.addElement(de);
				
				de = new DataElement();
				if(TipoAutorizzazione.isRolesRequired(pa.getAutorizzazione()) 
						|| 
						!TipoAutorizzazione.getAllValues().contains(pa.getAutorizzazione()) // custom 
						){
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_RUOLI_LIST,
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, "" + pa.getIdSoggetto()),
							new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, ""+pa.getId())
							);
					if (contaListe) {
						int numRuoli = 0;
						if(pa.getRuoli()!=null){
							numRuoli= pa.getRuoli().sizeRuoloList();
						}
						ServletUtils.setDataElementVisualizzaLabel(de,new Long(numRuoli));
					} else
						ServletUtils.setDataElementVisualizzaLabel(de);
				}
				else{
					de.setValue("-");
				}
				e.addElement(de);

				de = new DataElement();
				//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
				de.setUrl(
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY,
						pIdSogg, pIdPorta);
				de.setValue(pa.getStatoMessageSecurity());
				e.addElement(de);
				
				//if(isModalitaAvanzata){
				de = new DataElement();
				de.setUrl( 
						PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM,pIdPorta, pIdSogg);

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

				if(isMTOMAbilitatoReq || isMTOMAbilitatoRes)
					de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_ABILITATO);
				else 
					de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_DISABILITATO);
				e.addElement(de);
				
				de = new DataElement();
				de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA, pIdSogg, pIdPorta, pIdNome);
				
				boolean isCorrelazioneApplicativaAbilitataReq = false;
				boolean isCorrelazioneApplicativaAbilitataRes = false;
				
				if (pa.getCorrelazioneApplicativa() != null)
					isCorrelazioneApplicativaAbilitataReq = pa.getCorrelazioneApplicativa().sizeElementoList() > 0;

				if (pa.getCorrelazioneApplicativaRisposta() != null)
					isCorrelazioneApplicativaAbilitataRes = pa.getCorrelazioneApplicativaRisposta().sizeElementoList() > 0;
				
				if(isCorrelazioneApplicativaAbilitataReq || isCorrelazioneApplicativaAbilitataRes)
					de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_ABILITATA);
				else 
					de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_DISABILITATA);
				e.addElement(de);
				
				if(isModalitaAvanzata){
					de = new DataElement();
					//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
					de.setUrl(
							PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_PROPRIETA_PROTOCOLLO_LIST,
							pIdSogg, pIdPorta);
					if (contaListe) {
						int numProp = pa.sizeProprietaIntegrazioneProtocolloList();
						ServletUtils.setDataElementVisualizzaLabel(de, (long) numProp );
					} else
						ServletUtils.setDataElementVisualizzaLabel(de);
					e.addElement(de);
				}

				/*
				 * Visualizzo SoggettoErogatore/Servizio I soggetti erogatori
				 * possono essere identificati con due casi 1. PA che hanno
				 * soggetto proprietario, servizio e non hanno definito il
				 * soggetto virtuale. In questo caso il soggetto erogatore e' il
				 * soggetto proprietario. 2. PA che hanno un soggetto virtuale.
				 * In questo caso il soggetto erogatore e' il soggetto virtuale
				 */
				@SuppressWarnings("unused")
				int idSoggEr = 0;
				PortaApplicativaSoggettoVirtuale pasv = pa.getSoggettoVirtuale();
				if (pasv != null)
					idSoggEr = pasv.getId().intValue();
				else
					idSoggEr = pa.getIdSoggetto().intValue();
				PortaApplicativaServizio pas = pa.getServizio();
				int idServ = pas.getId().intValue();
				String tmpAz = "";
				PortaApplicativaAzione paa = pa.getAzione();
				if (paa != null)
					tmpAz = "-" + paa.getNome();
				de = new DataElement();

				pIdServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, idServ+ "");

				de.setUrl(
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE,
						pIdServizio,pNomeServizio, pTipoServizio, pVersioneServizio);
				de.setValue(asps.getTipoSoggettoErogatore() + "/" + asps.getNomeSoggettoErogatore() + "-" + asps.getTipo() + "/" + asps.getNome() + tmpAz);
				e.addElement(de);

				if(extendedServletList!=null && extendedServletList.showExtendedInfo(this.request, this.session)){
					de = new DataElement();
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_EXTENDED_LIST,
							pIdPorta,pIdNome,pIdPorta, pIdSogg
							);
					if (contaListe) {
						int numExtended = extendedServletList.sizeList(pa);
						ServletUtils.setDataElementVisualizzaLabel(de,new Long(numExtended));
					} else
						ServletUtils.setDataElementVisualizzaLabel(de);
					e.addElement(de);
				}
				
				dati.addElement(e);
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(false);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	
	public void serviziFruitoriPorteDelegateList(List<PortaDelegata> lista, String idServizio, String idSoggettoFruitore, String idFruzione, ISearch ricerca)
			throws Exception {
		try {
			
			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

			ServletUtils.addListElementIntoSession(this.session, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI_PORTE_DELEGATE,
					new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, idServizio),
					new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO, idSoggettoFruitore),
					new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID, idFruzione));

			@SuppressWarnings("unused")
			boolean isModalitaAvanzata = ServletUtils.getUserFromSession(this.session).getInterfaceType().equals(InterfaceType.AVANZATA);

			IExtendedListServlet extendedServletList = this.core.getExtendedServletPortaDelegata();
			
			int idLista = Liste.SERVIZI_FRUITORI_PORTE_DELEGATE;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			// Deve essere usato il soggetto per visualizzare i servizi applicativi
			ServletUtils.setObjectIntoSession(this.session, true, PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_USA_ID_SOGGETTO);
			

			// Prendo il nome e il tipo del servizio
			AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idServizio));

			// Prendo il nome e il tipo del soggetto fruitore
			Soggetto soggFruitore = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(idSoggettoFruitore));
		
			String servizioTmpTile = asps.getTipoSoggettoErogatore() + "/" + asps.getNomeSoggettoErogatore() + "-" + asps.getTipo() + "/" + asps.getNome();
			Parameter pIdServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, idServizio+ "");
			Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, asps.getNome());
			Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, asps.getTipo());
			
			String fruitoreTmpTile = "Fruitore "+soggFruitore.getTipo() + "/" + soggFruitore.getNome();
			Parameter pIdFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID, idFruzione+ "");

			// setto la barra del titolo
			List<Parameter> lstParm = new ArrayList<Parameter>();

			lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
			lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
			lstParm.add(new Parameter(servizioTmpTile, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, pIdServizio,pNomeServizio, pTipoServizio));
			lstParm.add(new Parameter(fruitoreTmpTile, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE, pIdServizio,pIdFruitore));
			
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_DELEGATE, null));
			}else {
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_DELEGATE,
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_PORTE_DELEGATE_LIST,
						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, idServizio),
						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO, idSoggettoFruitore)));
				lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}			
			
			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParm );

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription(AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_DELEGATE + " contenenti la stringa '" + search + "'");
			}


			List<String> listaLabel = new ArrayList<String>();

			listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
			//listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOGGETTO);
			//listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_DESCRIZIONE);
			listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SERVIZI_APPLICATIVI);
			listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_RUOLI); 
			listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY);
			//if(InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())){
			listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM);
			//}
			listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_BR_RICHIESTA); 
			listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_BR_RISPOSTA); 
			if(extendedServletList!=null && extendedServletList.showExtendedInfo(this.request, this.session)){
				listaLabel.add(extendedServletList.getListTitle(this));
			}

			String[] labels = listaLabel.toArray(new String[listaLabel.size()]);
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			Iterator<PortaDelegata> it = lista.iterator();
			PortaDelegata pd = null;
			while (it.hasNext()) {
				pd = it.next();
				Vector<DataElement> e = new Vector<DataElement>();

				
				DataElement de = new DataElement();
				de.setType(DataElementType.HIDDEN);
				de.setValue("" + pd.getId());
				e.addElement(de);
				
				
				de = new DataElement();
				de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CHANGE,
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, "" + pd.getIdSoggetto()),
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA,pd.getNome()),
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, "" + pd.getId())
						);
				de.setValue(pd.getNome());
				de.setIdToRemove(pd.getId().toString());
				de.setToolTip(pd.getDescrizione());
				e.addElement(de);
				
				// NON SERVE: sono già dentro un soggetto fruitore
//				de = new DataElement();
//				de.setValue(pd.getTipoSoggettoProprietario()+"/"+pd.getNomeSoggettoProprietario());
//				e.addElement(de);
				
//				de = new DataElement();
//				de.setValue(pd.getDescrizione());
//				e.addElement(de);
				
				de = new DataElement();
				if(TipoAutorizzazione.isAuthenticationRequired(pd.getAutorizzazione())
						|| 
						!TipoAutorizzazione.getAllValues().contains(pd.getAutorizzazione()) ){  // custom ){
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_SERVIZIO_APPLICATIVO_LIST,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, "" + pd.getIdSoggetto()),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, ""+pd.getId())
							);
					if (contaListe) {
						int numSA = pd.sizeServizioApplicativoList();
						ServletUtils.setDataElementVisualizzaLabel(de,new Long(numSA));
					} else
						ServletUtils.setDataElementVisualizzaLabel(de);
				}
				else{
					de.setValue("-");
				}
				e.addElement(de);
				
				de = new DataElement();
				if(TipoAutorizzazione.isRolesRequired(pd.getAutorizzazione())
						|| 
						!TipoAutorizzazione.getAllValues().contains(pd.getAutorizzazione()) ){  // custom ){
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_RUOLI_LIST,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, "" + pd.getIdSoggetto()),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, ""+pd.getId())
							);
					if (contaListe) {
						int numSA = 0;
						if(pd.getRuoli()!=null){
							numSA= pd.getRuoli().sizeRuoloList();
						}
						ServletUtils.setDataElementVisualizzaLabel(de,new Long(numSA));
					} else
						ServletUtils.setDataElementVisualizzaLabel(de);
				}
				else{
					de.setValue("-");
				}
				e.addElement(de);		

				de = new DataElement();
				de.setUrl( 
						PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY,
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, "" + pd.getIdSoggetto()),
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, ""+pd.getId())
						);
				de.setValue(pd.getStatoMessageSecurity());
				e.addElement(de);

				//if(InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())){
				de = new DataElement();
				de.setUrl( 
						PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM,
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, "" + pd.getIdSoggetto()),
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, ""+pd.getId())
						);

				boolean isMTOMAbilitatoReq = false;
				boolean isMTOMAbilitatoRes= false;
				if(pd.getMtomProcessor()!= null){
					if(pd.getMtomProcessor().getRequestFlow() != null){
						if(pd.getMtomProcessor().getRequestFlow().getMode() != null){
							MTOMProcessorType mode = pd.getMtomProcessor().getRequestFlow().getMode();
							if(!mode.equals(MTOMProcessorType.DISABLE))
								isMTOMAbilitatoReq = true;
						}
					}

					if(pd.getMtomProcessor().getResponseFlow() != null){
						if(pd.getMtomProcessor().getResponseFlow().getMode() != null){
							MTOMProcessorType mode = pd.getMtomProcessor().getResponseFlow().getMode();
							if(!mode.equals(MTOMProcessorType.DISABLE))
								isMTOMAbilitatoRes = true;
						}
					}
				}

				if(isMTOMAbilitatoReq || isMTOMAbilitatoRes)
					de.setValue(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_ABILITATO);
				else 
					de.setValue(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_DISABILITATO);
				e.addElement(de);
				//}

				de = new DataElement();
				de.setUrl(
						PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_REQUEST_LIST,
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, "" + pd.getIdSoggetto()),
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, ""+pd.getId()),
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME,pd.getNome())
						);
				if (contaListe) {
					int numCorrelazione = 0;
					if (pd.getCorrelazioneApplicativa() != null)
						numCorrelazione = pd.getCorrelazioneApplicativa().sizeElementoList();
					ServletUtils.setDataElementVisualizzaLabel(de,new Long(numCorrelazione));
				} else
					ServletUtils.setDataElementVisualizzaLabel(de);
				e.addElement(de);

				de = new DataElement();
				de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RESPONSE_LIST,
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, "" + pd.getIdSoggetto()),
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, ""+pd.getId()),
						new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME,pd.getNome())
						);
				if (contaListe) {
					int numCorrelazione = 0;
					if (pd.getCorrelazioneApplicativaRisposta() != null)
						numCorrelazione = pd.getCorrelazioneApplicativaRisposta().sizeElementoList();
					ServletUtils.setDataElementVisualizzaLabel(de,new Long(numCorrelazione));
				} else
					ServletUtils.setDataElementVisualizzaLabel(de);
				e.addElement(de);

				if(extendedServletList!=null && extendedServletList.showExtendedInfo(this.request, this.session)){
					de = new DataElement();
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_EXTENDED_LIST,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, "" + pd.getIdSoggetto()),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, ""+pd.getId()),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME,pd.getNome())
							);
					if (contaListe) {
						int numExtended = extendedServletList.sizeList(pd);
						ServletUtils.setDataElementVisualizzaLabel(de,new Long(numExtended));
					} else
						ServletUtils.setDataElementVisualizzaLabel(de);
					e.addElement(de);
				}
				
				dati.addElement(e);
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(false);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	

	public Vector<DataElement> addServiziToDati(Vector<DataElement> dati, String nomeServizio, String tipoServizio, String oldNomeServizio, String oldTipoServizio,
			String provider, String provString, String[] soggettiList, String[] soggettiListLabel,
			String accordo, String[] accordiList, String[] accordiListLabel, String servcorr, BinaryParameter wsdlimpler,
			BinaryParameter wsdlimplfru, TipoOperazione tipoOp, String id, List<String> tipi, String profilo, String portType, 
			String[] ptList, boolean privato, String uriAccordo, String descrizione, long idSoggettoErogatore,
			String statoPackage,String oldStato,String versione,
			List<String> versioni,boolean validazioneDocumenti,String nomePA,
			String [] saSoggetti, String nomeSA, String protocollo, boolean generaPACheckSoggetto,
			List<AccordoServizioParteComune> asCompatibili,
			String erogazioneRuolo,String erogazioneAutenticazione,String erogazioneAutenticazioneOpzionale,String erogazioneAutorizzazione, boolean erogazioneIsSupportatoAutenticazioneSoggetti,
			String erogazioneAutorizzazioneAutenticati, String erogazioneAutorizzazioneRuoli, String erogazioneAutorizzazioneRuoliTipologia, String erogazioneAutorizzazioneRuoliMatch) throws Exception{

		String tipoServizioEffettivo = oldTipoServizio!=null ? oldTipoServizio : tipoServizio; 
		String nomeServizioEffettivo = oldTipoServizio!=null ? oldNomeServizio : nomeServizio; 
		
		String[] tipiLabel = new String[tipi.size()];
		for (int i = 0; i < tipi.size(); i++) {
			String nomeTipo = tipi.get(i);
			tipiLabel[i] = nomeTipo;
		}

		String[] versioniValues = new String[versioni.size()+1];
		String[] versioniLabel = new String[versioni.size()+1];
		versioniLabel[0] = AccordiServizioParteSpecificaCostanti.LABEL_APS_USA_VERSIONE_EROGATORE;
		versioniValues[0] = "-";
		for (int i = 0; i < versioni.size(); i++) {
			String tmp = versioni.get(i);
			versioniLabel[i+1] = tmp;
			versioniValues[i+1] = tmp;
		}

		User user = ServletUtils.getUserFromSession(this.session);

		boolean visualizzaVersione = this.core.isShowVersioneAccordoServizioParteSpecifica();

		boolean modificaAbilitata = ( (this.core.isShowGestioneWorkflowStatoDocumenti()==false) || (StatiAccordo.finale.toString().equals(oldStato)==false) );

		boolean isModalitaAvanzata = user.getInterfaceType().equals(InterfaceType.AVANZATA);

		boolean ripristinoStatoOperativo = this.core.isGestioneWorkflowStatoDocumenti_ripristinoStatoOperativoDaFinale();

		Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
		
		// accordo di servizio parte comune 

		Boolean isAccordiCooperazione = user.getPermessi().isAccordiCooperazione();
		Boolean isServizi = user.getPermessi().isServizi();

		String asLabel = AccordiServizioParteSpecificaCostanti.LABEL_APC_COMPOSTO;

		if(isServizi && !isAccordiCooperazione){
			asLabel = AccordiServizioParteSpecificaCostanti.LABEL_APC_COMPOSTO_SOLO_PARTE_COMUNE;
		}

		if(!isServizi  && isAccordiCooperazione){
			asLabel = AccordiServizioParteSpecificaCostanti.LABEL_APC_COMPOSTO_SOLO_COMPOSTO;
		}

		if(isServizi  && isAccordiCooperazione){
			asLabel = AccordiServizioParteSpecificaCostanti.LABEL_APC_COMPOSTO;
		}

		DataElement de = new DataElement();
		de.setLabel(asLabel);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		// Accordo 
		//if(tipoOp.equals("add") || modificaAbilitata){
		if(tipoOp.equals(TipoOperazione.ADD) ){
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_ACCORDO);
			de.setType(DataElementType.SELECT);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ACCORDO);
			de.setValues(accordiList);
			de.setLabels(accordiListLabel);
			//			de.setOnChange("CambiaAccordoServizio('" + tipoOp + "')");
			de.setPostBack(true);
			if (accordo != null)
				de.setSelected(accordo);
			dati.addElement(de);
		}else{
			if(!modificaAbilitata || (asCompatibili==null || asCompatibili.size()<=1)){
				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_ACCORDO);
				de.setType(DataElementType.HIDDEN);
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ACCORDO);
				de.setValue(accordo);
				dati.addElement(de);
			}
				
			IDAccordo idAccordoParteComune = this.idAccordoFactory.getIDAccordoFromUri(uriAccordo);
			
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_ACCORDO_PARTE_COMUNE_REFERENTE);
			de.setType(DataElementType.TEXT);
			de.setName("param_"+AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_ACCORDO_PARTE_COMUNE_REFERENTE );
			de.setValue(idAccordoParteComune.getSoggettoReferente().toString());
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_ACCORDO_PARTE_COMUNE_NOME);
			de.setType(DataElementType.TEXT);
			de.setName("param_"+AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_ACCORDO_PARTE_COMUNE_NOME );
			de.setValue(idAccordoParteComune.getNome());
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_ACCORDO_PARTE_COMUNE_VERSIONE);
			if(!modificaAbilitata || (asCompatibili==null || asCompatibili.size()<=1)){
				de.setType(DataElementType.TEXT);
				de.setName("param_"+AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_ACCORDO_PARTE_COMUNE_VERSIONE );
				if(idAccordoParteComune.getVersione()!=null)
					de.setValue(idAccordoParteComune.getVersione().intValue()+"");
			}
			else{
				String [] accordiCompatibiliList = new String[asCompatibili.size()];
				String [] accordiCompatibiliLabelList = new String[asCompatibili.size()];
				for (int i = 0; i < asCompatibili.size(); i++) {
					accordiCompatibiliList[i] = asCompatibili.get(i).getId() + "";
					if(asCompatibili.get(i).getVersione()!=null){
						accordiCompatibiliLabelList[i] = asCompatibili.get(i).getVersione().intValue()+"";
					}
				}
				de.setType(DataElementType.SELECT);
				de.setValues(accordiCompatibiliList);
				de.setLabels(accordiCompatibiliLabelList);
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ACCORDO);
				de.setPostBack(true);
				de.setSelected(accordo);
				de.setValue(accordo);
			}
			dati.addElement(de);
			
		}

		//Servizio (portType) 
		if (ptList != null) {
			if(tipoOp.equals(TipoOperazione.ADD) || modificaAbilitata){
				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SERVIZIO);
				de.setType(DataElementType.SELECT);
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PORT_TYPE);
				de.setValues(ptList);
				de.setLabels(ptList);
				de.setSelected(portType);
				//				de.setOnChange("CambiaAccordoServizio('" + tipoOp + "')");
				de.setPostBack(true);
				if (!isModalitaAvanzata) {
					de.setRequired(true);
				}
				dati.addElement(de);
			}else{
				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SERVIZIO);
				de.setType(DataElementType.HIDDEN);
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PORT_TYPE);
				de.setValue(portType != null && !"".equals(portType) ? portType : portType);
				dati.addElement(de);

				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SERVIZIO);
				de.setType(DataElementType.TEXT);
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PORT_TYPE_LABEL);
				de.setValue(portType != null && !"".equals(portType) ? portType : portType);
				dati.addElement(de);
			}
		}else{
			de = new DataElement();
			de.setType(DataElementType.HIDDEN );
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PORT_TYPE);
			dati.addElement(de);
		}

		//Sezione Soggetto Erogatore (provider)

		de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SOGGETTO_EROGATORE);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_PROVIDER_EROGATORE);
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_EROGATORE);
		if (tipoOp.equals(TipoOperazione.ADD)) {
			de.setType(DataElementType.SELECT);
			de.setValues(soggettiList);
			de.setLabels(soggettiListLabel);
			de.setPostBack(true);
			de.setSelected(provider);
		} else {
			de.setValue(provider);
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_PROVIDER_EROGATORE);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_TEXT);
			//if(this.core.isTerminologiaSICA_RegistroServizi()==false){
			de.setType(DataElementType.TEXT);
			/*}else{
				Soggetto soggEr = this.core.getSoggettoRegistro(new IDSoggetto(provString.split("/")[0],provString.split("/")[1]));
				de.setType("link");
				de.setUrl("soggettiChange.do?id=" + soggEr.getId() + "&nomeprov=" + soggEr.getNome() + "&tipoprov=" + soggEr.getTipo());
			}*/
			de.setValue(provString);
		}
		dati.addElement(de);


		//Sezione Servizio

		de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SERVIZIO);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		if(isModalitaAvanzata){
			
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_DESCRIZIONE);
			de.setType(DataElementType.TEXT_EDIT);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_DESCRIZIONE);
			de.setSize(getSize());
			de.setValue(descrizione!=null ? descrizione : "");
			if( !modificaAbilitata && (descrizione==null || "".equals(descrizione)) )
				de.setValue(" ");
			dati.addElement(de);
			
		}
		else{
			
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_DESCRIZIONE);
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_DESCRIZIONE);
			de.setSize(getSize());
			de.setValue(descrizione!=null ? descrizione : "");
			if( !modificaAbilitata && (descrizione==null || "".equals(descrizione)) )
				de.setValue(" ");
			dati.addElement(de);
			
		}
		
		/*if (!isModalitaAvanzata) {
		    de = new DataElement();
		    de.setValue("SPC");
		    de.setType("hidden");
		    de.setName("tiposervizio");
		    dati.addElement(de);
		} else {*/


		if(tipoOp.equals(TipoOperazione.ADD) || modificaAbilitata){
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_TIPO  );
			de.setValues(tipiLabel);
			de.setSelected(tipoServizio);
			de.setType(DataElementType.SELECT);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO);
			de.setSize(this.getSize());
			dati.addElement(de);
		}else{
			de = new DataElement();
			de.setValue(tipoServizio);
			de.setType(DataElementType.TEXT);
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_TIPO);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO);
			dati.addElement(de);
		}

		//}

		if (!isModalitaAvanzata) {
			de = new DataElement();
			if (nomeServizio == null) {
				de.setValue("");
			} else {
				de.setValue(nomeServizio);
			}
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO  );
			dati.addElement(de);
		} else {
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_NOME_FILE);
			if (nomeServizio == null) {
				de.setValue("");
			} else {
				de.setValue(nomeServizio);
			}
			if(tipoOp.equals(TipoOperazione.ADD) || modificaAbilitata){
				de.setType(DataElementType.TEXT_EDIT);
				de.setRequired(true);
			}else{
				de.setType(DataElementType.TEXT);
			}
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO  );
			de.setSize(this.getSize());
			dati.addElement(de);
		}
		
		if(isModalitaAvanzata){

			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_VERSIONE);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VERSIONE);
			de.setValue(versione);
			if(visualizzaVersione){
				if( modificaAbilitata ){
					de.setType(DataElementType.TEXT_EDIT);
					//de.setRequired(true);
					this.session.setAttribute("version", "required");
				}else{
					de.setType(DataElementType.TEXT);
				}
			}else{
				de.setType(DataElementType.HIDDEN);
			}
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VERSIONE);
			dati.addElement(de);
			
		}
		else{
			
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_VERSIONE);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VERSIONE);
			de.setValue(versione);
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VERSIONE);
			dati.addElement(de);
			
		}
		

		de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_SERVIZIO_CORRELATO);
		if( this.core.isShowCorrelazioneAsincronaInAccordi() && 
				( !isModalitaAvanzata || (portType!=null && !"".equals(portType) && !"-".equals(portType)) ) ){
			de.setType(DataElementType.HIDDEN);
		}
		else if(tipoOp.equals(TipoOperazione.ADD) || modificaAbilitata){
			de.setType(DataElementType.CHECKBOX);
		}else{
			de.setType(DataElementType.HIDDEN);
		}
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_SERVIZIO_CORRELATO);
		if( this.core.isShowCorrelazioneAsincronaInAccordi() && ( !isModalitaAvanzata || (portType!=null && !"".equals(portType) && !"-".equals(portType)) ) ){
			if (  (servcorr != null) && ((servcorr.equals(Costanti.CHECK_BOX_ENABLED)) || servcorr.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_ABILITATO)) ) {
				de.setValue(Costanti.CHECK_BOX_ENABLED);
			}
			else{
				de.setValue(Costanti.CHECK_BOX_DISABLED);
			}
		}
		else if (  (servcorr != null) && ((servcorr.equals(Costanti.CHECK_BOX_ENABLED)) || servcorr.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_ABILITATO)) ) {
			if(tipoOp.equals(TipoOperazione.ADD) || modificaAbilitata){
				de.setSelected(Costanti.CHECK_BOX_ENABLED);
			}else{
				de.setValue(Costanti.CHECK_BOX_ENABLED);
			}
		}else{
			if(tipoOp.equals(TipoOperazione.CHANGE) && (this.core.isShowGestioneWorkflowStatoDocumenti() 
					&&  StatiAccordo.finale.toString().equals(oldStato) )){
				de.setValue(Costanti.CHECK_BOX_DISABLED);
			}
		}
		dati.addElement(de);

		if( (tipoOp.equals(TipoOperazione.CHANGE) &&
				(this.core.isShowGestioneWorkflowStatoDocumenti() &&  StatiAccordo.finale.toString().equals(oldStato) )) 
				||
				(this.core.isShowCorrelazioneAsincronaInAccordi() &&
						( !isModalitaAvanzata || 
								(portType!=null && !"".equals(portType) && !"-".equals(portType)) )) ){
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_TIPOLOGIA_SERVIZIO);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_SERVIZIO_CORRELATO_LABEL);
			if (  (servcorr != null) && ((servcorr.equals(Costanti.CHECK_BOX_ENABLED)) || servcorr.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_ABILITATO)) ) {
				de.setValue(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_CORRELATO);
			}else{
				de.setValue(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_NORMALE);
			}
			if(this.core.isProfiloDiCollaborazioneAsincronoSupportatoDalProtocollo(protocollo)){	
				de.setType(DataElementType.TEXT);
			} else {
				de.setType(DataElementType.HIDDEN);
			}
			dati.addElement(de);
		}

		de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_PRIVATO);
		if (this.core.isShowFlagPrivato() &&  (tipoOp.equals(TipoOperazione.ADD) || 
				modificaAbilitata) && isModalitaAvanzata) {
			de.setType(DataElementType.CHECKBOX);
			de.setSelected(privato ? Costanti.CHECK_BOX_ENABLED : "");
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(privato ? Costanti.CHECK_BOX_ENABLED : "");
		}
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PRIVATO);
		dati.addElement(de);

		if(this.core.isShowFlagPrivato() && tipoOp.equals(TipoOperazione.CHANGE ) && 
				(this.core.isShowGestioneWorkflowStatoDocumenti() && 
						StatiAccordo.finale.toString().equals(oldStato) ) &&
						isModalitaAvanzata){
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_VISIBILITA_SERVIZIO);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PRIVATO_LABEL);
			if(privato){
				de.setValue(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PRIVATA);
			}else{
				de.setValue(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PUBBLICA);
			}
			dati.addElement(de);
		}

		de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_VERSIONE_PROTOCOLLO);
		if(isModalitaAvanzata){
			if(tipoOp.equals(TipoOperazione.ADD) || modificaAbilitata){
				de.setValues(versioniValues);
				de.setLabels(versioniLabel);
				if(profilo==null){
					profilo="-";
				}
				de.setSelected(profilo);
				de.setType(DataElementType.SELECT);
			}else{
				de.setType(DataElementType.TEXT);
				de.setValue(profilo);
				if( profilo==null || "".equals(profilo) )
					de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_APS_USA_VERSIONE_EROGATORE);
			}
		}else {
			de.setType(DataElementType.HIDDEN);
			if(profilo==null){
				profilo="-";
			}
			de.setValue(profilo);
		}
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROFILO);
		de.setSize(this.getSize());
		dati.addElement(de);

		
		// Modalita' standard faccio vedere lo stato
		// stato
		if(isModalitaAvanzata){

			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_STATO);
			if(this.core.isShowGestioneWorkflowStatoDocumenti()){
				String[] stati = StatiAccordo.toArray();
				if( tipoOp.equals(TipoOperazione.ADD) || StatiAccordo.finale.toString().equals(oldStato)==false ){
					de.setType(DataElementType.SELECT);
					de.setValues(stati);
					de.setSelected(statoPackage);
					de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO_PACKAGE);
				}else{
					de.setType(DataElementType.TEXT);
					de.setValue(StatiAccordo.finale.toString());
					de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO_PACKAGE);

					if(ripristinoStatoOperativo){
						dati.addElement(de);

						de = new DataElement();
						de.setType(DataElementType.LINK);

						Parameter pIdsoggErogatore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, idSoggettoErogatore + "");
						Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, nomeServizioEffettivo);
						Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, tipoServizioEffettivo);

						de.setUrl(
								AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE,
								new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, id),
								pNomeServizio, pTipoServizio, pIdsoggErogatore,new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_RIPRISTINA_STATO, StatiAccordo.operativo.toString()),
								new Parameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME, Costanti.DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_IN_PROGRESS));
						de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_RIPRISTINA_STATO_OPERATIVO);
					}
				}
			}else{
				de.setType(DataElementType.HIDDEN);
				de.setValue(StatiAccordo.finale.toString());
				de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO_PACKAGE);
			}

			dati.addElement(de);

		}
		
		else{
			
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_STATO);
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO_PACKAGE);
			if(this.core.isShowGestioneWorkflowStatoDocumenti()){
				if(tipoOp.equals(TipoOperazione.ADD)){
					de.setType(DataElementType.HIDDEN);
					de.setValue(statoPackage);
				}else if( StatiAccordo.finale.toString().equals(oldStato)==false ){
					String[] stati = StatiAccordo.toArray();
					de.setType(DataElementType.SELECT);
					de.setValues(stati);
					de.setSelected(statoPackage);
				}else{
					de.setType(DataElementType.TEXT);
					de.setValue(StatiAccordo.finale.toString());

					if(ripristinoStatoOperativo){
						dati.addElement(de);

						de = new DataElement();
						de.setType(DataElementType.LINK);

						Parameter pIdsoggErogatore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, idSoggettoErogatore + "");
						Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, nomeServizioEffettivo);
						Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, tipoServizioEffettivo);

						de.setUrl(
								AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE,
								new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, id),
								pNomeServizio, pTipoServizio, pIdsoggErogatore,new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_RIPRISTINA_STATO, StatiAccordo.operativo.toString()),
								new Parameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME, Costanti.DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_IN_PROGRESS));
						de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_RIPRISTINA_STATO_OPERATIVO);
					}
				}				
			}else{
				de.setType(DataElementType.HIDDEN);
				de.setValue(StatiAccordo.finale.toString());
			}

			dati.addElement(de);
		}
		

		// Porta Applicativa e Servizio Applicativo Erogatore
		if (tipoOp.equals(TipoOperazione.ADD) && this.core.isGenerazioneAutomaticaPorteApplicative() && !ServletUtils.isCheckBoxEnabled(servcorr) && generaPACheckSoggetto) {

			if(isModalitaAvanzata){
				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SPECIFICA_PORTA_APPLICATIVA);
				de.setType(DataElementType.TITLE);
				dati.addElement(de);

				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_NOME_PA);
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_PA);
				de.setValue(nomePA);
				de.setSize(this.getSize());
				de.setType(DataElementType.TEXT_EDIT);
				dati.addElement(de);
			}else {
				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_NOME_PA);
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_PA);
				de.setValue(nomePA);
				de.setSize(this.getSize());
				de.setType(DataElementType.HIDDEN);
				dati.addElement(de);
			}

			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SERVIZIO_APPLICATIVO_EROGATORE );
			de.setType(DataElementType.TITLE);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_NOME_SERVIZIO_APPLICATIVO_EROGATORE);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SA);
			de.setSelected(nomeSA);
			de.setValues(saSoggetti);
			de.setType(DataElementType.SELECT);
			dati.addElement(de);
			
			// Controllo Accesso
			
			this.controlloAccessi(dati);
			
			this.controlloAccessiAutenticazione(dati, erogazioneAutenticazione, null, erogazioneAutenticazioneOpzionale, false, erogazioneIsSupportatoAutenticazioneSoggetti);
			
			this.controlloAccessiAutorizzazione(dati, tipoOp, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD, 
					erogazioneAutenticazione, erogazioneAutorizzazione, null, 
					erogazioneAutorizzazioneAutenticati, null, 0, null, null,
					erogazioneAutorizzazioneRuoli, null, 0, erogazioneRuolo,
					erogazioneAutorizzazioneRuoliTipologia, erogazioneAutorizzazioneRuoliMatch, 
					false, erogazioneIsSupportatoAutenticazioneSoggetti, contaListe, false, false);
			
		}

		//Specifica dei porti di accesso

		if(isModalitaAvanzata){
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SPECIFICA_PORTI_ACCESSO);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_VALIDAZIONE_DOCUMENTI_ESTESA);
			de.setValue(""+validazioneDocumenti);
			//		if (tipoOp.equals(TipoOperazione.ADD) && InterfaceType.AVANZATA.equals(user.getInterfaceType())) {
			if (tipoOp.equals(TipoOperazione.ADD) ) {
				de.setType(DataElementType.CHECKBOX);
				if(validazioneDocumenti){
					de.setSelected(Costanti.CHECK_BOX_ENABLED);
				}else{
					de.setSelected(Costanti.CHECK_BOX_DISABLED);
				}
			}else{
				de.setType(DataElementType.HIDDEN);
			}
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VALIDAZIONE_DOCUMENTI);
			de.setSize(this.getSize());
			dati.addElement(de);

			boolean isSupportoAsincrono = this.core.isProfiloDiCollaborazioneAsincronoSupportatoDalProtocollo(protocollo);
			boolean isRuoloNormale =  !( (servcorr != null) && ((servcorr.equals(Costanti.CHECK_BOX_ENABLED)) || servcorr.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_ABILITATO)) ) ;

			if (tipoOp.equals(TipoOperazione.ADD)) {
				if(isSupportoAsincrono){
					if(isRuoloNormale){
						dati.add(wsdlimpler.getFileDataElement(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_EROGATORE, "", getSize()));
						dati.addAll(wsdlimpler.getFileNameDataElement());
						dati.add(wsdlimpler.getFileIdDataElement());
						
//						de = new DataElement();
//						de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_EROGATORE);
//						de.setValue(wsdlimpler);
//						de.setType(DataElementType.FILE);
//						de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_EROGATORE);
//						de.setSize(this.getSize());
//						dati.addElement(de);
					} else {
						dati.add(wsdlimplfru.getFileDataElement(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_FRUITORE, "", getSize()));
						dati.addAll(wsdlimplfru.getFileNameDataElement());
						dati.add(wsdlimplfru.getFileIdDataElement());
						
//						de = new DataElement();
//						de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_FRUITORE);
//						de.setValue(wsdlimplfru);
//						de.setType(DataElementType.FILE);
//						de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_FRUITORE);
//						de.setSize(this.getSize());
//						dati.addElement(de);
					}
				}else {
					dati.add(wsdlimpler.getFileDataElement(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO, "", getSize()));
					dati.addAll(wsdlimpler.getFileNameDataElement());
					dati.add(wsdlimpler.getFileIdDataElement());
					
//					de = new DataElement();
//					de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO);
//					de.setValue(wsdlimpler);
//					de.setType(DataElementType.FILE);
//					de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_EROGATORE);
//					de.setSize(this.getSize());
//					dati.addElement(de);
				}
			} else {
				if(isSupportoAsincrono){
					if(isRuoloNormale){
						de = new DataElement();
						de.setType(DataElementType.LINK);
						de.setUrl(
								AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_WSDL_CHANGE,
								new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, id),
								new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_EROGATORE));

						de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_EROGATORE);
						dati.addElement(de);
					}else{
						de = new DataElement();
						de.setType(DataElementType.LINK);
						de.setUrl(
								AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_WSDL_CHANGE,
								new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, id),
								new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_FRUITORE));
						de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_FRUITORE);
						dati.addElement(de);
					}
				}else {
					de = new DataElement();
					de.setType(DataElementType.LINK);
					de.setUrl(
							AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_WSDL_CHANGE,
							new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, id),
							new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_EROGATORE));

					de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO);
					dati.addElement(de);
				}
			}

		} else {
			if (tipoOp.equals(TipoOperazione.ADD)) {
				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_EROGATORE);
				String wsdlimplerS =  wsdlimpler.getValue() != null ? new String(wsdlimpler.getValue()) : ""; 
				de.setValue(wsdlimplerS);
				de.setType(DataElementType.HIDDEN);
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_EROGATORE);
				dati.addElement(de);

				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_FRUITORE);
				String wsdlimplfruS =  wsdlimpler.getValue() != null ? new String(wsdlimpler.getValue()) : ""; 
				de.setValue(wsdlimplfruS);
				de.setType(DataElementType.HIDDEN);
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_FRUITORE);
				dati.addElement(de);
			}
		}

		/*
		 * de = new DataElement(); de.setLabel("Servizio pubblico");
		 * de.setType("checkbox"); de.setName("servpub"); if ((servpub != null)
		 * && servpub.equals(Costanti.CHECK_BOX_ENABLED)) { de.setSelected(Costanti.CHECK_BOX_ENABLED); }
		 * dati.addElement(de);
		 */

		if (tipoOp.equals(TipoOperazione.ADD) == false) {

			IDPortaApplicativa idPA = null;
			String [] tmp = provString.split("/");
			Integer versioneInt = Integer.parseInt(versione);
			IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tipoServizioEffettivo, nomeServizioEffettivo, 
					tmp[0], tmp[1], versioneInt); 
			if(isModalitaAvanzata==false){
				idPA = this.porteApplicativeCore.getIDPortaApplicativaAssociata(idServizio);
			}
			
			
			
			de = new DataElement();
			if(isModalitaAvanzata || idPA==null){
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_APPLICATIVE);
			}
			else{
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SPECIFICA_PORTA_APPLICATIVA);
			}
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
			
			de = new DataElement();
			de.setType(DataElementType.LINK);
			if(isModalitaAvanzata || idPA==null){
				de.setUrl(
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_PORTE_APPLICATIVE_LIST,
						new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, id),
						new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+idSoggettoErogatore),
						new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, nomeServizioEffettivo),
						new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, tipoServizioEffettivo));
				if(contaListe){
					try{
						// BugFix OP-674
						//int num = this.apsCore.serviziPorteAppList(tipoServizioEffettivo,nomeServizioEffettivo,Long.parseLong(id),idSoggettoErogatore, new Search(true)).size();
						Search searchForCount = new Search(true,1);
						this.apsCore.serviziPorteAppList(tipoServizioEffettivo,nomeServizioEffettivo,versioneInt,Long.parseLong(id),idSoggettoErogatore, searchForCount);
						int num = searchForCount.getNumEntries(Liste.SERVIZI_PORTE_APPLICATIVE);
						ServletUtils.setDataElementCustomLabel(de, AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_APPLICATIVE, (long) num);
					}catch(Exception e){
						this.log.error("Calcolo numero pa non riuscito",e);
						ServletUtils.setDataElementCustomLabel(de, AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_APPLICATIVE, "N.D.");
					}
				}else{
					de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_APPLICATIVE);
				}
			}
			else{
				// Deve essere usato il soggetto 
				ServletUtils.setObjectIntoSession(this.session, true, PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_USA_ID_SOGGETTO);
				
				PortaApplicativa pa = this.porteApplicativeCore.getPortaApplicativa(idPA);
				de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CHANGE,
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, pa.getId()+""),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, pa.getIdSoggetto()+""),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA, pa.getNome())
						);
				de.setValue(Costanti.LABEL_VISUALIZZA);
			}
			dati.addElement(de);
			
			
			
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_ALTRE_INFORMAZIONI);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);

			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(
					AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST,
					new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, id),
					new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+idSoggettoErogatore),
					new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, nomeServizioEffettivo),
					new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, tipoServizioEffettivo)
					);
			if(contaListe){
				try{
					// BugFix OP-674
					//int num = this.apsCore.serviziFruitoriList(Integer.parseInt(id), new Search(true)).size();
					Search searchForCount = new Search(true,1);
					this.apsCore.serviziFruitoriList(Integer.parseInt(id), searchForCount);
					int num = searchForCount.getNumEntries(Liste.SERVIZI_FRUITORI);
					ServletUtils.setDataElementCustomLabel(de, AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORI, (long) num);
				}catch(Exception e){
					this.log.error("Calcolo numero fruitori non riuscito",e);
					ServletUtils.setDataElementCustomLabel(de, AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORI, "N.D.");
				}
			}else{
				de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORI);
			}
			dati.addElement(de);

			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(
					AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ALLEGATI_LIST,
					new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, id));
			if(contaListe){
				try{
					// BugFix OP-674
					//int num = this.apsCore.serviziAllegatiList(Integer.parseInt(id), new Search(true)).size();
					Search searchForCount = new Search(true,1);
					this.apsCore.serviziAllegatiList(Integer.parseInt(id), searchForCount);
					int num = searchForCount.getNumEntries(Liste.SERVIZI_ALLEGATI);
					ServletUtils.setDataElementCustomLabel(de, AccordiServizioParteSpecificaCostanti.LABEL_APS_ALLEGATI, (long) num);
				}catch(Exception e){
					this.log.error("Calcolo numero Allegati non riuscito",e);
					ServletUtils.setDataElementCustomLabel(de, AccordiServizioParteSpecificaCostanti.LABEL_APS_ALLEGATI, "N.D.");
				}
			}else{
				de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_APS_ALLEGATI  );
			}
			dati.addElement(de);

		}

		return dati;
	}

	public Vector<DataElement> addServiziToDatiAsHidden(Vector<DataElement> dati, String nomeservizio, String tiposervizio,
			String provider, String provString, String[] soggettiList, String[] soggettiListLabel,
			String accordo, String[] accordiList, String[] accordiListLabel, String servcorr, String wsdlimpler,
			String wsdlimplfru, TipoOperazione tipoOp, String id, List<String> tipi, String profilo, String portType, 
			String[] ptList, boolean privato, String uriAccordo, String descrizione, long idSoggettoErogatore,
			String statoPackage,String oldStato,String versione,
			List<String> versioni,boolean validazioneDocumenti,String nomePA,String [] saSoggetti, String nomeSA, String protocollo, boolean generaPACheckSoggetto) throws Exception{

		String[] tipiLabel = new String[tipi.size()];
		for (int i = 0; i < tipi.size(); i++) {
			String nomeTipo = tipi.get(i);
			tipiLabel[i] = nomeTipo;
		}

		String[] versioniValues = new String[versioni.size()+1];
		String[] versioniLabel = new String[versioni.size()+1];
		versioniLabel[0] = AccordiServizioParteSpecificaCostanti.LABEL_APS_USA_VERSIONE_EROGATORE;
		versioniValues[0] = "-";
		for (int i = 0; i < versioni.size(); i++) {
			String tmp = versioni.get(i);
			versioniLabel[i+1] = tmp;
			versioniValues[i+1] = tmp;
		}

		User user = ServletUtils.getUserFromSession(this.session);

		boolean modificaAbilitata = ( (this.core.isShowGestioneWorkflowStatoDocumenti()==false) || (StatiAccordo.finale.toString().equals(oldStato)==false) );

		boolean isModalitaAvanzata = user.getInterfaceType().equals(InterfaceType.AVANZATA);

		// accordo di servizio parte comune 
		DataElement de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_ACCORDO);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ACCORDO);
		de.setValue(accordo);
		dati.addElement(de);

		//Servizio (portType) 
		de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SERVIZIO);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PORT_TYPE);
		de.setValue(portType != null && !"".equals(portType) ? portType : portType);
		dati.addElement(de);

		//Sezione Soggetto Erogatore (provider)

		de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_PROVIDER_EROGATORE);
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_EROGATORE);
		de.setValue(provider);
		de.setType(DataElementType.HIDDEN);
		dati.addElement(de);

		// Sezione Servizio 

		de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_DESCRIZIONE);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_DESCRIZIONE);
		de.setSize(getSize());
		de.setValue(descrizione!=null ? descrizione : "");
		if( !modificaAbilitata && (descrizione==null || "".equals(descrizione)) )
			de.setValue(" ");
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_STATO);
		de.setType(DataElementType.HIDDEN);
		de.setValue(statoPackage);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO_PACKAGE);
		dati.addElement(de);

		//Tipo Servizio
		de = new DataElement();
		de.setValue(tiposervizio);
		de.setType(DataElementType.HIDDEN);
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_TIPO);
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO);
		dati.addElement(de);

		//Servizio

		de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_NOME_FILE);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO  );
		de.setValue(nomeservizio);
		de.setSize(this.getSize());
		dati.addElement(de);
		
		// Versione Servizio
		de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_VERSIONE);
		de.setValue(versione);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VERSIONE);
		dati.addElement(de);

		// SErvizio Correlato
		de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_SERVIZIO_CORRELATO);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_SERVIZIO_CORRELATO);
		if( this.core.isShowCorrelazioneAsincronaInAccordi() && ( !isModalitaAvanzata || (portType!=null && !"".equals(portType) && !"-".equals(portType)) ) ){
			if (  (servcorr != null) && ((servcorr.equals(Costanti.CHECK_BOX_ENABLED)) || servcorr.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_ABILITATO)) ) {
				de.setValue(Costanti.CHECK_BOX_ENABLED);
			}
			else{
				de.setValue(Costanti.CHECK_BOX_DISABLED);
			}
		}
		else if (  (servcorr != null) && ((servcorr.equals(Costanti.CHECK_BOX_ENABLED)) || servcorr.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_ABILITATO)) ) {
			if(tipoOp.equals(TipoOperazione.ADD) || modificaAbilitata){
				de.setSelected(Costanti.CHECK_BOX_ENABLED);
			}else{
				de.setValue(Costanti.CHECK_BOX_ENABLED);
			}
		}else{
			if(tipoOp.equals(TipoOperazione.CHANGE) && (this.core.isShowGestioneWorkflowStatoDocumenti() 
					&&  StatiAccordo.finale.toString().equals(oldStato) )){
				de.setValue(Costanti.CHECK_BOX_DISABLED);
			}
		}
		dati.addElement(de);

		if( (tipoOp.equals(TipoOperazione.CHANGE) &&
				(this.core.isShowGestioneWorkflowStatoDocumenti() &&  StatiAccordo.finale.toString().equals(oldStato) )) 
				||
				(this.core.isShowCorrelazioneAsincronaInAccordi() &&
						( !isModalitaAvanzata || 
								(portType!=null && !"".equals(portType) && !"-".equals(portType)) )) ){
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_TIPOLOGIA_SERVIZIO);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_SERVIZIO_CORRELATO_LABEL);
			if (  (servcorr != null) && ((servcorr.equals(Costanti.CHECK_BOX_ENABLED)) || servcorr.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_ABILITATO)) ) {
				de.setValue(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_CORRELATO);
			}else{
				de.setValue(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_NORMALE);
			}
			//			if(this.core.isProfiloDiCollaborazioneAsincronoSupportatoDalProtocollo(protocollo)){	
			//				de.setType(DataElementType.TEXT);
			//			} else {
			de.setType(DataElementType.HIDDEN);
			//			}
			dati.addElement(de);
		}

		//Privato
		de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_PRIVATO);
		//		if (this.core.isShowFlagPrivato() &&  (tipoOp.equals(TipoOperazione.ADD) || 
		//				modificaAbilitata) && isModalitaAvanzata) {
		//			de.setType(DataElementType.CHECKBOX);
		//			de.setSelected(privato ? Costanti.CHECK_BOX_ENABLED : "");
		//		} else {
		de.setType(DataElementType.HIDDEN);
		de.setValue(privato ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED);
		//		}
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PRIVATO);
		dati.addElement(de);

		if(this.core.isShowFlagPrivato() && tipoOp.equals(TipoOperazione.CHANGE ) && 
				(this.core.isShowGestioneWorkflowStatoDocumenti() && 
						StatiAccordo.finale.toString().equals(oldStato) ) &&
						isModalitaAvanzata){
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_VISIBILITA_SERVIZIO);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PRIVATO_LABEL);
			if(privato){
				de.setValue(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PRIVATA);
			}else{
				de.setValue(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PUBBLICA);
			}
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);
		}

		de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_VERSIONE_PROTOCOLLO);
		//		if(isModalitaAvanzata){
		//			if(tipoOp.equals(TipoOperazione.ADD) || modificaAbilitata){
		//				de.setValues(versioniValues);
		//				de.setLabels(versioniLabel);
		//				if(profilo==null){
		//					profilo="-";
		//				}
		//				de.setSelected(profilo);
		//				de.setType(DataElementType.SELECT);
		//			}else{
		//				de.setType(DataElementType.TEXT);
		//				de.setValue(profilo);
		//				if( profilo==null || "".equals(profilo) )
		//					de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_APS_USA_VERSIONE_EROGATORE);
		//			}
		//		}else {
		de.setType(DataElementType.HIDDEN);
		if(profilo==null){
			profilo="-";
		}
		de.setValue(profilo);
		//		}
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROFILO);
		de.setSize(this.getSize());
		dati.addElement(de);


		// Porta Applicativa e Servizio Applicativo Erogatore
		if (tipoOp.equals(TipoOperazione.ADD) && this.core.isGenerazioneAutomaticaPorteApplicative() && !ServletUtils.isCheckBoxEnabled(servcorr) && generaPACheckSoggetto) {

			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_NOME_PA);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_PA);
			de.setValue(nomePA);
			de.setSize(this.getSize());
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_NOME_SERVIZIO_APPLICATIVO_EROGATORE);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SA);
			de.setType(DataElementType.HIDDEN);
			de.setValue(nomeSA);
			//			de.setValues(saSoggetti);
			//			de.setType(DataElementType.SELECT);
			dati.addElement(de);
		}

		//Specifica dei porti di accesso

		de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_VALIDAZIONE_DOCUMENTI_ESTESA);
		de.setValue(""+validazioneDocumenti);
		//		if (tipoOp.equals(TipoOperazione.ADD) && InterfaceType.AVANZATA.equals(user.getInterfaceType())) {
		//			if (tipoOp.equals(TipoOperazione.ADD) ) {
		//				de.setType(DataElementType.CHECKBOX);
		//				if(validazioneDocumenti){
		//					de.setSelected(Costanti.CHECK_BOX_ENABLED);
		//				}else{
		//					de.setSelected(Costanti.CHECK_BOX_DISABLED);
		//				}
		//			}else{
		de.setType(DataElementType.HIDDEN);
		//			}
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VALIDAZIONE_DOCUMENTI);
		de.setSize(this.getSize());
		dati.addElement(de);


		de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_EROGATORE);
		de.setValue(wsdlimpler);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_EROGATORE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_FRUITORE);
		de.setValue(wsdlimplfru);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_FRUITORE);
		dati.addElement(de);

		return dati;
	}



	public Vector<DataElement> addWSDLToDati(TipoOperazione tipoOp,  
			int size,
			AccordoServizioParteSpecifica asps, String oldwsdl,
			String tipo, boolean validazioneDocumenti,
			Vector<DataElement> dati,
			String tipologiaDocumentoScaricare, String label) {

		boolean isModalitaAvanzata = ServletUtils.getUserFromSession(this.session).getInterfaceType().equals(InterfaceType.AVANZATA);

		DataElement de = new DataElement();
		if(label.contains(" di ")){
			de.setLabel(label.split(" di")[0]);
		}else{
			de.setLabel(tipologiaDocumentoScaricare.toUpperCase().charAt(0)+tipologiaDocumentoScaricare.substring(1));
		}
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		de = new DataElement();
		de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_TIPO);
		de.setValue( tipo);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO);
		dati.addElement(de);

		if( this.apsCore.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(asps.getStatoPackage())){
			this.pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);

			de = new DataElement();
			de.setLabel("");
			de.setType(DataElementType.TEXT_AREA_NO_EDIT);
			de.setValue(oldwsdl);
			de.setRows(30);
			de.setCols(110);
			dati.addElement(de);
			
			if(oldwsdl != null && !oldwsdl.isEmpty()){
				DataElement saveAs = new DataElement();
				saveAs.setValue(AccordiServizioParteComuneCostanti.LABEL_DOWNLOAD);
				saveAs.setType(DataElementType.LINK);
				saveAs.setUrl(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT, 
						new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_ID_ACCORDO, asps.getId()+""),
						new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO, tipologiaDocumentoScaricare),
						new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO, ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_PARTE_SPECIFICA));
				dati.add(saveAs);
			}
			
		}
		else{

			if(oldwsdl != null && !oldwsdl.isEmpty()){
				de = new DataElement();
				de.setType(DataElementType.TEXT_AREA_NO_EDIT);
				de.setValue(oldwsdl);
				de.setRows(30);
				de.setCols(110);
				//de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_WSDL_ATTUALE);
				dati.addElement(de);

				DataElement saveAs = new DataElement();
				saveAs.setValue(AccordiServizioParteComuneCostanti.LABEL_DOWNLOAD);
				saveAs.setType(DataElementType.LINK);
				saveAs.setUrl(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT, 
						new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_ID_ACCORDO, asps.getId()+""),
						new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO, tipologiaDocumentoScaricare),
						new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO, ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_PARTE_SPECIFICA));
				dati.add(saveAs);
				
				de = new DataElement();
				de.setType(DataElementType.TITLE);
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_WSDL_AGGIORNAMENTO);
				de.setValue("");
				de.setSize(this.getSize());
				dati.addElement(de);
			}else {
				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_WSDL_ATTUALE );
				de.setType(DataElementType.TEXT);
				de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_WSDL_NOT_FOUND);
				dati.addElement(de);
			}

			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_VALIDAZIONE_DOCUMENTI);
			de.setValue("" + validazioneDocumenti);
			if (isModalitaAvanzata) {
				de.setType(DataElementType.CHECKBOX);
				if( validazioneDocumenti){
					de.setSelected(Costanti.CHECK_BOX_ENABLED);
				}else{
					de.setSelected(Costanti.CHECK_BOX_DISABLED);
				}
			}else{
				de.setType(DataElementType.HIDDEN);
			}
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VALIDAZIONE_DOCUMENTI);
			de.setSize(size);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_NUOVO);
			de.setValue("");
			de.setType(DataElementType.FILE);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL);
			de.setSize(size);
			dati.addElement(de);
			
			if(oldwsdl != null && !oldwsdl.isEmpty()){
				de = new DataElement();
				de.setBold(true);
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_WSDL_CHANGE_CLEAR_WARNING);
				de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_WSDL_CHANGE_CLEAR);
				de.setType(DataElementType.NOTE);
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_WARN);
				de.setSize(this.getSize());
				dati.addElement(de);
			}
		}

		return dati;
	}


	public Vector<DataElement> addTipoNomeServizioToDati(TipoOperazione tipoOp,  String myId, String tipoServizio, String nomeServizio, Integer versioneServizio, Vector<DataElement> dati ){
		DataElement de = new DataElement();

		if(nomeServizio !=null){
			de = new DataElement();
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO);
			de.setType(DataElementType.HIDDEN);
			de.setValue(nomeServizio);
			dati.addElement(de);
		}

		if(tipoServizio != null){
			de = new DataElement();
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO);
			de.setType(DataElementType.HIDDEN);
			de.setValue(tipoServizio);
			dati.addElement(de);
		}
		
		if(versioneServizio != null){
			de = new DataElement();
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VERSIONE);
			de.setType(DataElementType.HIDDEN);
			de.setValue(versioneServizio.intValue()+"");
			dati.addElement(de);
		}

		if(myId != null){
			de = new DataElement();
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID);
			de.setType(DataElementType.HIDDEN);
			de.setValue(myId);
			dati.addElement(de);
		}

		return dati;
	}


	public Vector<DataElement> addFruitoreWSDLToDati(TipoOperazione tipoOp, 
			String tipo, String idSoggettoErogatoreDelServizio, String idSoggettoFruitore, String wsdl, Boolean validazioneDocumenti,
			Fruitore myFru,
			Vector<DataElement> dati,
			String id, String tipologiaDocumentoScaricare,
			boolean finished, String label) {

		boolean isModalitaAvanzata = ServletUtils.getUserFromSession(this.session).getInterfaceType().equals(InterfaceType.AVANZATA);

		DataElement de = new DataElement();
		if(label.contains(" di ")){
			de.setLabel(label.split(" di")[0]);
		}else{
			de.setLabel(tipologiaDocumentoScaricare.toUpperCase().charAt(0)+tipologiaDocumentoScaricare.substring(1));
		}
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		de = new DataElement();
		de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_TIPO);
		de.setValue( tipo);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_ID_SOGGETTO_EROGATORE);
		de.setValue( idSoggettoErogatoreDelServizio);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_PROVIDER_FRUITORE);
		de.setValue( idSoggettoFruitore);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE);
		dati.addElement(de);



		if(this.apsCore.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(myFru.getStatoPackage())){
			
			this.pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);
			
			if(wsdl != null && !wsdl.isEmpty()){
				this.pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);
				de = new DataElement();
				de.setLabel("");
				de.setType(DataElementType.TEXT_AREA_NO_EDIT);
				de.setValue( wsdl);
				de.setRows(30);
				de.setCols(110);
				dati.addElement(de);
				
				if(wsdl != null && !wsdl.isEmpty()){
					DataElement saveAs = new DataElement();
					saveAs.setValue(AccordiServizioParteComuneCostanti.LABEL_DOWNLOAD);
					saveAs.setType(DataElementType.LINK);
					saveAs.setUrl(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT, 
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_ID_ACCORDO, id),
							new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO, tipologiaDocumentoScaricare),
							new Parameter(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_TIPO_SOGGETTO_FRUITORE, myFru.getTipo()),
							new Parameter(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_NOME_SOGGETTO_FRUITORE, myFru.getNome()),
							new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO, ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_PARTE_SPECIFICA));
					dati.add(saveAs);
				}
			}
			else{
				de = new DataElement();
				//de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_WSDL_ATTUALE );
				de.setType(DataElementType.TEXT);
				de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_WSDL_NOT_FOUND);
				dati.addElement(de);
			}
		}
		else{

			if(wsdl != null && !wsdl.isEmpty()){
				de = new DataElement();
				de.setType(DataElementType.TEXT_AREA_NO_EDIT);
				de.setValue(wsdl);
				de.setRows(30);
				de.setCols(110);
				//de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_WSDL_ATTUALE );
				dati.addElement(de);

				DataElement saveAs = new DataElement();
				saveAs.setValue(AccordiServizioParteComuneCostanti.LABEL_DOWNLOAD);
				saveAs.setType(DataElementType.LINK);
				saveAs.setUrl(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT, 
						new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_ID_ACCORDO, id),
						new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO, tipologiaDocumentoScaricare),
						new Parameter(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_TIPO_SOGGETTO_FRUITORE, myFru.getTipo()),
						new Parameter(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_NOME_SOGGETTO_FRUITORE, myFru.getNome()),
						new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO, ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_PARTE_SPECIFICA));
				dati.add(saveAs);
				
				if(!finished){
					de = new DataElement();
					de.setType(DataElementType.TITLE);
					de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_WSDL_AGGIORNAMENTO);
					de.setValue("");
					de.setSize(this.getSize());
					dati.addElement(de);
				}
			}else {
				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_WSDL_ATTUALE );
				de.setType(DataElementType.TEXT);
				de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_WSDL_NOT_FOUND);
				dati.addElement(de);
			}

			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_VALIDAZIONE_DOCUMENTI);
			de.setValue("" + validazioneDocumenti);
			if (isModalitaAvanzata && !finished) {
				de.setType(DataElementType.CHECKBOX);
				if( validazioneDocumenti){
					de.setSelected(Costanti.CHECK_BOX_ENABLED);
				}else{
					de.setSelected(Costanti.CHECK_BOX_DISABLED);
				}
			}else{
				de.setType(DataElementType.HIDDEN);
			}
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VALIDAZIONE_DOCUMENTI);
			de.setSize( getSize());
			dati.addElement(de);

			if(!finished){
				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_NUOVO);
				de.setValue("");
				de.setType(DataElementType.FILE);
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL);
				de.setSize( getSize());
				dati.addElement(de);
				
				if(wsdl != null && !wsdl.isEmpty()){
					de = new DataElement();
					de.setBold(true);
					de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_WSDL_CHANGE_CLEAR_WARNING);
					de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_WSDL_CHANGE_CLEAR);
					de.setType(DataElementType.NOTE);
					de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_WARN);
					de.setSize(this.getSize());
					dati.addElement(de);
				}
			}
		}
		return dati;
	}


	public Vector<DataElement> addServiziFruitoriToDati(Vector<DataElement> dati, String idSoggettoFruitore, BinaryParameter wsdlimpler, 
			BinaryParameter wsdlimplfru, String[] soggettiList, String[] soggettiListLabel, String idServ, String id, 
			TipoOperazione tipoOp, String idSoggettoErogatoreDelServizio, String nomeprov, String tipoprov,
			String nomeservizio, String tiposervizio, Integer versioneservizio, String correlato, String stato, String oldStato, String statoServizio,
			String tipoAccordo, boolean validazioneDocumenti,
			String fruizioneServizioApplicativo,String fruizioneRuolo,String fruizioneAutenticazione,String fruizioneAutenticazioneOpzionale, String fruizioneAutorizzazione,
			String fruizioneAutorizzazioneAutenticati,String fruizioneAutorizzazioneRuoli, String fruizioneAutorizzazioneRuoliTipologia, String fruizioneAutorizzazioneRuoliMatch,
			List<String> saList) throws Exception {
		
		boolean isModalitaAvanzata = ServletUtils.getUserFromSession(this.session).getInterfaceType().equals(InterfaceType.AVANZATA);

		boolean isRuoloNormale = !(correlato != null && correlato.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_CORRELATO));

		String protocollo = this.apsCore.getProtocolloAssociatoTipoServizio(tiposervizio);

		boolean isProfiloAsincronoSupportatoDalProtocollo = this.core.isProfiloDiCollaborazioneAsincronoSupportatoDalProtocollo(protocollo);

		boolean ripristinoStatoOperativo = this.core.isGestioneWorkflowStatoDocumenti_ripristinoStatoOperativoDaFinale();

		Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
		

		
		if (tipoOp.equals(TipoOperazione.ADD)) {

			DataElement de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORE);
			dati.addElement(de);
			
			// in caso di add allora visualizzo la lista dei soggetti
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_PROVIDER_FRUITORE);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE);
			de.setType(DataElementType.SELECT);
			de.setValues(soggettiList);
			de.setLabels(soggettiListLabel);
			de.setSelected(idSoggettoFruitore);
			if(this.core.isGenerazioneAutomaticaPorteDelegate()){
				de.setPostBack(true);
			}
			dati.addElement(de);

			
			if(this.core.isShowGestioneWorkflowStatoDocumenti()){
				String[] stati = StatiAccordo.toArray();
				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_STATO);
				if (!isModalitaAvanzata) {
					de.setType(DataElementType.HIDDEN);
					de.setValue(statoServizio);
				}else{
					de.setType(DataElementType.SELECT);
					de.setValues(stati);
					de.setSelected(stato);
				}
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_STATO);
				dati.addElement(de);
			}else{
				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_STATO);
				de.setType(DataElementType.HIDDEN);
				de.setValue(StatiAccordo.finale.toString());
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_STATO);
				dati.addElement(de);
			}

			boolean isSoggettoGestitoPorta = false;
			if(soggettiList!=null && soggettiList.length>0){
				String soggettoId = idSoggettoFruitore;
				if(soggettoId==null || "".equals(soggettoId)){
					soggettoId = soggettiList[0];
				}
				IDSoggetto idSoggetto = null;
				for (int i = 0; i < soggettiList.length; i++) {
					if(soggettiList[i].equals(soggettoId)){
						String[]tmp = soggettiListLabel[i].split("/");
						idSoggetto = new IDSoggetto(tmp[0], tmp[1]);
						break;
					}
				}
				if(idSoggetto!=null){
					Soggetto sogg = this.soggettiCore.getSoggettoRegistro(idSoggetto);
					if(!this.pddCore.isPddEsterna(sogg.getPortaDominio())){
						isSoggettoGestitoPorta = true;	
					}
				}
			}




			if(this.core.isGenerazioneAutomaticaPorteDelegate() && isSoggettoGestitoPorta){
				
				this.controlloAccessi(dati);
				
				this.controlloAccessiAutenticazione(dati, fruizioneAutenticazione, null, fruizioneAutenticazioneOpzionale, false, true);
				
				this.controlloAccessiAutorizzazione(dati, tipoOp, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD,
						fruizioneAutenticazione, fruizioneAutorizzazione, null, 
						fruizioneAutorizzazioneAutenticati, null, 0, saList, fruizioneServizioApplicativo,
						fruizioneAutorizzazioneRuoli, null, 0, fruizioneRuolo,
						fruizioneAutorizzazioneRuoliTipologia, fruizioneAutorizzazioneRuoliMatch, 
						false, true, contaListe, true, false);
	
			}

			if (isModalitaAvanzata) {
				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SPECIFICA_PORTI_ACCESSO );
				de.setType(DataElementType.TITLE);
				dati.addElement(de);
			}

			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_VALIDAZIONE_DOCUMENTI_ESTESA);
			de.setValue(""+validazioneDocumenti);
			if (tipoOp.equals(TipoOperazione.ADD) && isModalitaAvanzata) {
				de.setType(DataElementType.CHECKBOX);
				if(validazioneDocumenti){
					de.setSelected(Costanti.CHECK_BOX_ENABLED);
				}else{
					de.setSelected(Costanti.CHECK_BOX_DISABLED);
				}
			}else{
				de.setType(DataElementType.HIDDEN);
			}
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VALIDAZIONE_DOCUMENTI);
			de.setSize(this.getSize());
			dati.addElement(de);

			if(isModalitaAvanzata){
				if(isProfiloAsincronoSupportatoDalProtocollo){
					if(isRuoloNormale){
						dati.add(wsdlimpler.getFileDataElement(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_EROGATORE, "", getSize()));
						dati.addAll(wsdlimpler.getFileNameDataElement());
						dati.add(wsdlimpler.getFileIdDataElement());
						
//						de = new DataElement();
//						de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_EROGATORE);
//						de.setValue("");
//						de.setType(DataElementType.FILE);
//						de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_EROGATORE);
//						de.setSize(this.getSize());
//						dati.addElement(de);
					}else {
						dati.add(wsdlimplfru.getFileDataElement(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_FRUITORE, "", getSize()));
						dati.addAll(wsdlimplfru.getFileNameDataElement());
						dati.add(wsdlimplfru.getFileIdDataElement());
						
//						de = new DataElement();
//						de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_FRUITORE);
//						de.setValue("");
//						de.setType(DataElementType.FILE);
//						de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_FRUITORE);
//						de.setSize(this.getSize());
//						dati.addElement(de);
					}
				} else {
					dati.add(wsdlimpler.getFileDataElement(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO, "", getSize()));
					dati.addAll(wsdlimpler.getFileNameDataElement());
					dati.add(wsdlimpler.getFileIdDataElement());
					
//					de = new DataElement();
//					de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO);
//					de.setValue("");
//					de.setType(DataElementType.FILE);
//					de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_EROGATORE);
//					de.setSize(this.getSize());
//					dati.addElement(de);
				}
			} else {
				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_EROGATORE);
				de.setValue("");
				de.setType(DataElementType.HIDDEN);
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_EROGATORE);
				dati.addElement(de);


				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_FRUITORE);
				de.setValue("");
				de.setType(DataElementType.HIDDEN);
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_FRUITORE);
				dati.addElement(de);
			}
		} else {
					
			DataElement de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORE);
			dati.addElement(de);
			
			de = new DataElement();
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE);
			de.setValue(idSoggettoFruitore);
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);
						
			// in caso di change non visualizzo la select list ma il tipo e il
			// nome del soggetto
			String nomeSoggetto = "";
			for (int i = 0; i < soggettiList.length; i++) {
				if (soggettiList[i].equals(idSoggettoFruitore)) {
					nomeSoggetto = soggettiListLabel[i];
					break;
				}
			}
			
			if(this.apsCore.isRegistroServiziLocale()){
				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_PROVIDER_FRUITORE);
				de.setValue(nomeSoggetto);
				de.setType(DataElementType.TEXT);
				dati.addElement(de);
			}
			
			de = new DataElement();
			if(this.apsCore.isRegistroServiziLocale()){
				de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_VISUALIZZA_DATI_FRUITORE);
				//de.setValue(nomeSoggetto);
				String [] split = nomeSoggetto.split("/");
				IDSoggetto idSoggetto = new IDSoggetto(split[0], split[1]);
				long idSoggettoLong = this.soggettiCore.getIdSoggetto(idSoggetto.getNome(), idSoggetto.getTipo());
				de.setType(DataElementType.LINK);
				de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE,
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID,idSoggettoLong+""),
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME,idSoggetto.getNome()),
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO,idSoggetto.getTipo()));
			}
			else{
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE_AS_TEXT);
				de.setValue(nomeSoggetto);
				de.setType(DataElementType.TEXT);
			}
			dati.addElement(de);

						
			if(this.core.isShowGestioneWorkflowStatoDocumenti()){
				de = new DataElement();
				de.setType(DataElementType.TITLE);
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_STATO);
				dati.addElement(de);
			}

			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_STATO);
			if(this.core.isShowGestioneWorkflowStatoDocumenti()){
				String[] stati = StatiAccordo.toArray();
				if(StatiAccordo.finale.toString().equals(oldStato)==false ){
					de.setType(DataElementType.SELECT);
					de.setValues(stati);
					de.setSelected(stato);
					de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_STATO);
				}else{
//					if (!isModalitaAvanzata) {
//						de.setType(DataElementType.HIDDEN);
//						de.setValue(StatiAccordo.finale.toString());
//						de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_STATO);
//					}else{
					de.setValue(StatiAccordo.finale.toString());
					de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_STATO);

					if(ripristinoStatoOperativo){
						dati.addElement(de);

						de = new DataElement();
						de.setType(DataElementType.LINK);

						Parameter pIdSoggettoErogatore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, idSoggettoErogatoreDelServizio);
						Parameter pId = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, idServ);
						Parameter pMyId = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID, id);		
						Parameter pFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE, idSoggettoFruitore);		

						de.setUrl(
								AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE,
								pId,	pMyId, pIdSoggettoErogatore ,pFruitore,
								new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_RIPRISTINA_STATO, StatiAccordo.operativo.toString()),
								new Parameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME, Costanti.DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_IN_PROGRESS));
						de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_RIPRISTINA_STATO_OPERATIVO);
					}
//					}
				}
			}else{
				de.setType(DataElementType.HIDDEN);
				de.setValue(StatiAccordo.finale.toString());
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_STATO);
			}
			dati.addElement(de);
			
			
			// &correlato
			Parameter pCorrelato = null;
			if (correlato != null) {
				pCorrelato = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_CORRELATO, correlato);
			}

			Parameter pTipoWSDLEr = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_EROGATORE);
			Parameter pTipoWSDLFru = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_FRUITORE);
			Parameter pId = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, id);
			Parameter pIdSoggettoErogatore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, idSoggettoErogatoreDelServizio);
			Parameter pIdSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE, idSoggettoFruitore);
			
			Parameter pIdServ = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SERVIZIO, idServ);

			Parameter pNomeProv = new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME, nomeprov);
			Parameter pTipoProv = new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO, tipoprov);
			Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, nomeservizio);
			Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, tiposervizio);
			Parameter pVersioneServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VERSIONE, versioneservizio.intValue()+"");

			Parameter pTipoAccordo = AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo);


			if ( isModalitaAvanzata) {

				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SPECIFICA_PORTI_ACCESSO );
				de.setType(DataElementType.TITLE);
				dati.addElement(de);

				if(isProfiloAsincronoSupportatoDalProtocollo){
					List<Parameter> lstParam = new ArrayList<Parameter>();

					if(isRuoloNormale){
						lstParam = new ArrayList<Parameter>();
						lstParam.add(pId);
						lstParam.add(pIdSoggettoErogatore);
						lstParam.add(pIdSoggettoFruitore);
						lstParam.add(pTipoWSDLEr);
						if(pCorrelato != null){
							lstParam.add(pCorrelato);
						}

						de = new DataElement();
						de.setType(DataElementType.LINK);
						if (tipoOp.equals(TipoOperazione.CHANGE)) {
							de.setUrl( AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_WSDL_CHANGE, lstParam.toArray(new Parameter[lstParam.size()]));
						} else {
							lstParam.add(pIdServ);
							lstParam.add(pNomeProv);
							lstParam.add(pTipoProv);
							lstParam.add(pNomeServizio);
							lstParam.add(pTipoServizio);
							lstParam.add(pVersioneServizio);
							lstParam.add(pTipoAccordo);

							de.setUrl(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_WSDL_CHANGE,
									lstParam.toArray(new Parameter[lstParam.size()]));

							//					de.setUrl("accordiErogatoriFruitoriWSDLChange.do?id=" + id + "&tipo=wsdlimpler" + "&idSoggErogatore=" + idSoggettoErogatoreDelServizio + 
							//							"&idServ=" + idServ + "&nomeprov=" + nomeprov + "&tipoprov=" + tipoprov + "&nomeservizio=" + nomeservizio + 
							//							"&tiposervizio=" + tiposervizio + tmpCorrelato+
							//							AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo, "&"));
						}
						de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_EROGATORE);
						dati.addElement(de);
					}else {
						lstParam = new ArrayList<Parameter>();
						lstParam.add(pId);
						lstParam.add(pIdSoggettoErogatore);
						lstParam.add(pIdSoggettoFruitore);
						lstParam.add(pTipoWSDLFru);
						if(pCorrelato != null){
							lstParam.add(pCorrelato);
						}

						de = new DataElement();
						de.setType(DataElementType.LINK);
						if (tipoOp.equals(TipoOperazione.CHANGE)) {
							de.setUrl( AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_WSDL_CHANGE, lstParam.toArray(new Parameter[lstParam.size()]));
						} else {
							lstParam.add(pIdServ);
							lstParam.add(pNomeProv);
							lstParam.add(pTipoProv);
							lstParam.add(pNomeServizio);
							lstParam.add(pTipoServizio);
							lstParam.add(pVersioneServizio);
							lstParam.add(pTipoAccordo);

							de.setUrl(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_WSDL_CHANGE,
									lstParam.toArray(new Parameter[lstParam.size()]));

							//					de.setUrl("accordiErogatoriFruitoriWSDLChange.do?id=" + id + "&tipo=wsdlimplfru" + "&idSoggErogatore=" + idSoggettoErogatoreDelServizio + 
							//							"&idServ=" + idServ + "&nomeprov=" + nomeprov + "&tipoprov=" + tipoprov + "&nomeservizio=" + nomeservizio + "&tiposervizio=" + 
							//							tiposervizio + tmpCorrelato+
							//							AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo, "&"));
						}
						de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_FRUITORE);
						dati.addElement(de);
					}
				}else {
					List<Parameter> lstParam = new ArrayList<Parameter>();
					lstParam.add(pId);
					lstParam.add(pIdSoggettoErogatore);
					lstParam.add(pIdSoggettoFruitore);
					lstParam.add(pTipoWSDLEr);
					if(pCorrelato != null){
						lstParam.add(pCorrelato);
					}

					de = new DataElement();
					de.setType(DataElementType.LINK);
					if (tipoOp.equals(TipoOperazione.CHANGE)) {
						de.setUrl( AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_WSDL_CHANGE, lstParam.toArray(new Parameter[lstParam.size()]));
					} else {
						lstParam.add(pIdServ);
						lstParam.add(pNomeProv);
						lstParam.add(pTipoProv);
						lstParam.add(pNomeServizio);
						lstParam.add(pTipoServizio);
						lstParam.add(pVersioneServizio);
						lstParam.add(pTipoAccordo);

						de.setUrl(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_WSDL_CHANGE,
								lstParam.toArray(new Parameter[lstParam.size()]));

						//					de.setUrl("accordiErogatoriFruitoriWSDLChange.do?id=" + id + "&tipo=wsdlimpler" + "&idSoggErogatore=" + idSoggettoErogatoreDelServizio + 
						//							"&idServ=" + idServ + "&nomeprov=" + nomeprov + "&tipoprov=" + tipoprov + "&nomeservizio=" + nomeservizio + 
						//							"&tiposervizio=" + tiposervizio + tmpCorrelato+
						//							AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo, "&"));
					}
					de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO);
					dati.addElement(de);
				}
			}
		}

		return dati;
	}

	public Vector<DataElement> addServiziFruitoriToDatiAsHidden(Vector<DataElement> dati, String idSoggettoFruitore, String wsdlimpler, 
			String wsdlimplfru, String[] soggettiList, String[] soggettiListLabel, String idServ, String id, 
			TipoOperazione tipoOp, String idSoggettoErogatoreDelServizio, String nomeprov, String tipoprov,
			String nomeservizio, String tiposervizio, String correlato, String stato, String oldStato, String statoServizio,
			String tipoAccordo, boolean validazioneDocumenti) throws Exception {

		boolean isModalitaAvanzata = ServletUtils.getUserFromSession(this.session).getInterfaceType().equals(InterfaceType.AVANZATA);

		if (tipoOp.equals(TipoOperazione.ADD)) {
			// in caso di add allora visualizzo la lista dei soggetti
			DataElement de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_PROVIDER_FRUITORE);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE);
			de.setType(DataElementType.HIDDEN);
			de.setValue(idSoggettoFruitore);
			dati.addElement(de);

			if(this.core.isShowGestioneWorkflowStatoDocumenti()){
				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_STATO);
				if (!isModalitaAvanzata) {
					de.setType(DataElementType.HIDDEN);
					de.setValue(statoServizio);
				}else{
					de.setType(DataElementType.HIDDEN);
					de.setValue(stato);
				}
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_STATO);
				dati.addElement(de);
			}else{
				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_STATO);
				de.setType(DataElementType.HIDDEN);
				de.setValue(StatiAccordo.finale.toString());
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_STATO);
				dati.addElement(de);
			}

			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_VALIDAZIONE_DOCUMENTI_ESTESA);
			de.setValue(""+validazioneDocumenti);
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VALIDAZIONE_DOCUMENTI);
			de.setSize(this.getSize());
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_EROGATORE);
			de.setValue("");
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_EROGATORE);
			dati.addElement(de);


			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_FRUITORE);
			de.setValue("");
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_FRUITORE);
			dati.addElement(de);
		} else {
			// in caso di change non visualizzo la select list ma il tipo e il
			// nome del soggetto
			@SuppressWarnings("unused")
			String nomeSoggetto = "";
			for (int i = 0; i < soggettiList.length; i++) {
				if (soggettiList[i].equals(idSoggettoFruitore)) {
					nomeSoggetto = soggettiListLabel[i];
					break;
				}
			}
			DataElement de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_PROVIDER_FRUITORE);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE);
			de.setType(DataElementType.HIDDEN);
			//de.setValue(nomeSoggetto);
			de.setValue(idSoggettoFruitore);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_STATO);
			de.setType(DataElementType.HIDDEN);
			de.setValue(stato);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_STATO);
			dati.addElement(de);
		}

		return dati;
	}


	public Vector<DataElement> addFruitoreToDati(TipoOperazione tipoOp, String[] versioniLabel, String[] versioniValues,
			String profilo, String clientAuth,
			Vector<DataElement> dati,
			String oldStatoPackage, String idServizio, String idServizioFruitore, String idSoggettoErogatoreDelServizio,
			String nomeservizio, String tiposervizio, Integer versioneservizio, String idSoggettoFruitore,
			AccordoServizioParteSpecifica asps, Fruitore fruitore) throws Exception{

		boolean isModalitaAvanzata = ServletUtils.getUserFromSession(this.session).getInterfaceType().equals(InterfaceType.AVANZATA);

		
		if(tipoOp.equals(TipoOperazione.CHANGE)){

			Soggetto fruitoreSogg = this.soggettiCore.getSoggettoRegistro(new IDSoggetto(fruitore.getTipo(), fruitore.getNome()));
			
			boolean esterno = this.pddCore.isPddEsterna(fruitoreSogg.getPortaDominio());			
			if(!esterno){
			
				IDPortaDelegata idPD = null;
				IDSoggetto idSoggettoFruitoreObject = new IDSoggetto(fruitore.getTipo(), fruitore.getNome());
				if(isModalitaAvanzata==false){
					IDServizio idServizioObject = IDServizioFactory.getInstance().getIDServizioFromValues(asps.getTipo(), asps.getNome(), 
							asps.getTipoSoggettoErogatore(),asps.getNomeSoggettoErogatore(),asps.getVersione());
					idPD = this.porteDelegateCore.getIDPortaDelegataAssociata(idServizioObject, idSoggettoFruitoreObject);
				}
				
				
				DataElement de = new DataElement();
				if(isModalitaAvanzata || idPD==null){
					de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_DELEGATE);
				}
				else{
					de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SPECIFICA_PORTA_DELEGATA);
				}
				de.setType(DataElementType.TITLE);
				dati.addElement(de);
				
				de = new DataElement();
				de.setType(DataElementType.LINK);
				if(isModalitaAvanzata || idPD==null){
					
					Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
					
					de.setUrl(
							AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_PORTE_DELEGATE_LIST,
							new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID,idServizio),
							new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO, idSoggettoFruitore ),
							new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, idSoggettoErogatoreDelServizio),
							new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, nomeservizio),
							new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, tiposervizio),
							new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID, idServizioFruitore));
					if(contaListe){
						try{
							// BugFix OP-674
//							int num = this.apsCore.serviziFruitoriPorteDelegateList(this.soggettiCore.getIdSoggetto(fruitore.getNome(), fruitore.getTipo()), 
//									asps.getServizio().getTipo(),asps.getServizio().getNome(), asps.getId(), 
//									asps.getServizio().getTipoSoggettoErogatore(), asps.getServizio().getNomeSoggettoErogatore(), asps.getIdSoggetto(), 
//									new Search(true)).size();
							Search searchForCount = new Search(true,1);
							this.apsCore.serviziFruitoriPorteDelegateList(this.soggettiCore.getIdSoggetto(fruitore.getNome(), fruitore.getTipo()), 
									asps.getTipo(),asps.getNome(), asps.getId(), 
									asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore(), asps.getIdSoggetto(), 
									searchForCount);
							int num = searchForCount.getNumEntries(Liste.SERVIZI_FRUITORI_PORTE_DELEGATE);
							ServletUtils.setDataElementCustomLabel(de, AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_DELEGATE, (long) num);
						}catch(Exception e){
							this.log.error("Calcolo numero pa non riuscito",e);
							ServletUtils.setDataElementCustomLabel(de, AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_DELEGATE, "N.D.");
						}
					}else{
						de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_DELEGATE);
					}
					
				}
				else{
					// Deve essere usato il soggetto 
					ServletUtils.setObjectIntoSession(this.session, true, PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_USA_ID_SOGGETTO);
					
					PortaDelegata pd = this.porteDelegateCore.getPortaDelegata(idPD);
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CHANGE,
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, "" + pd.getIdSoggetto()),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA,pd.getNome()),
							new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, "" + pd.getId())
							);
					de.setValue(Costanti.LABEL_VISUALIZZA);
				}
				dati.addElement(de);
				
			}
			
		}
		
		
		
		if(isModalitaAvanzata){
			DataElement de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_ALTRE_INFORMAZIONI );
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}

		DataElement de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_PROFILO);

		if(isModalitaAvanzata){
			String tmp = profilo;
			if(tmp==null){
				tmp="-";
			}

			boolean modificaAbilitata = ( (this.apsCore.isShowGestioneWorkflowStatoDocumenti()==false)
					|| (StatiAccordo.finale.toString().equals(oldStatoPackage)==false) );

			if(modificaAbilitata){
				de.setLabels(versioniLabel);
				de.setValues(versioniValues);

				de.setSelected(tmp);
				de.setType(DataElementType.SELECT);
			} else {
				String profiloValue = null;
				for (int i =0; i < versioniValues.length ; i++) {
					String v = versioniValues[i];
					if(v.equals(tmp)){
						profiloValue = versioniLabel[i];
						break;

					}
				}

				if(profiloValue==null){
					profiloValue="-";
				}

				de.setType(DataElementType.TEXT);
				de.setValue(profiloValue);
			}
		} else {
			de.setType(DataElementType.HIDDEN);
			String tmp = profilo;
			if(tmp==null){
				tmp="-";
			}
			de.setValue(tmp);
		}
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROFILO);
		de.setSize(getSize());
		dati.addElement(de);
		//}



		String[] tipoCA = { 
				AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_DEFAULT,
				AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_ABILITATO, 
				AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_DISABILITATO
		};

		String[] labelTipiCA = new String[3];
		labelTipiCA[0] = AccordiServizioParteSpecificaCostanti.LABEL_APS_USA_PROFILO_PDD_FRUITORE;
		labelTipiCA[1] = tipoCA[1];
		labelTipiCA[2] = tipoCA[2];



		if(tipoOp.equals(TipoOperazione.ADD)){
			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_CLIENT_AUTH);
			if ( isModalitaAvanzata) {
				de.setType(DataElementType.SELECT);
				de.setLabels(labelTipiCA);
				de.setValues(tipoCA);
				de.setSelected( clientAuth);
			}else{
				de.setType(DataElementType.HIDDEN);
				de.setValue( clientAuth);
			}
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_CLIENT_AUTH);
			dati.addElement(de);
		}

		if(tipoOp.equals(TipoOperazione.CHANGE)){
			de = new DataElement();
			de.setValue(idServizioFruitore);
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID);
			dati.addElement(de);

			boolean modificaAbilitata = ( (this.apsCore.isShowGestioneWorkflowStatoDocumenti()==false)
					|| (StatiAccordo.finale.toString().equals(oldStatoPackage)==false) );

			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_CLIENT_AUTH);
			if ( isModalitaAvanzata) {
				de.setType(DataElementType.SELECT);
				de.setLabels(labelTipiCA);
				if(!modificaAbilitata && ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT.equals(clientAuth)){
					de.setValues(new String[]{AccordiServizioParteSpecificaCostanti.LABEL_APS_USA_PROFILO_PDD_FRUITORE});
					de.setSelected(AccordiServizioParteSpecificaCostanti.LABEL_APS_USA_PROFILO_PDD_FRUITORE);
				}else{
					de.setValues(tipoCA);
					de.setSelected(clientAuth);
				}
			}else{
				de.setType(DataElementType.HIDDEN);
				de.setValue(clientAuth);
			}
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_CLIENT_AUTH);
			if(clientAuth==null)
				clientAuth = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
			dati.addElement(de);

		}

		if (!isModalitaAvanzata) {
			de = new DataElement();
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
			de.setValue(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_DISABILITATO);
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);
		}

		return dati;
	}

	public Vector<DataElement> addFruitoreToDatiAsHidden(TipoOperazione tipoOp, String[] versioniLabel, String[] versioniValues,
			String profilo, String clientAuth,
			Vector<DataElement> dati,
			String oldStatoPackage, String idServizio, String idServizioFruitore, String idSoggettoErogatoreDelServizio,
			String nomeservizio, String tiposervizio, String idSoggettoFruitore){

		boolean isModalitaAvanzata = ServletUtils.getUserFromSession(this.session).getInterfaceType().equals(InterfaceType.AVANZATA);

		DataElement de = new DataElement();
		de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_PROFILO);

		de.setType(DataElementType.HIDDEN);
		String tmp = profilo;
		if(tmp==null){
			tmp="-";
		}
		de.setValue(tmp);
		de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROFILO);
		de.setSize(getSize());
		dati.addElement(de);

		//		String[] tipoCA = { 
		//				AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_DEFAULT,
		//				AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_ABILITATO, 
		//				AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_DISABILITATO
		//		};

		//		String[] labelTipiCA = new String[3];
		//		labelTipiCA[0] = AccordiServizioParteSpecificaCostanti.LABEL_APS_USA_PROFILO_PDD_FRUITORE;
		//		labelTipiCA[1] = tipoCA[1];
		//		labelTipiCA[2] = tipoCA[2];



		if(tipoOp.equals(TipoOperazione.ADD)){
			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_CLIENT_AUTH);
			de.setType(DataElementType.HIDDEN);
			de.setValue( clientAuth);
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_CLIENT_AUTH);
			dati.addElement(de);
		}

		if(tipoOp.equals(TipoOperazione.CHANGE)){
			de = new DataElement();
			de.setValue(idServizioFruitore);
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID);
			dati.addElement(de);

			//			boolean modificaAbilitata = ( (this.apsCore.isShowGestioneWorkflowStatoDocumenti()==false)
			//					|| (StatiAccordo.finale.toString().equals(oldStatoPackage)==false) );

			de = new DataElement();
			de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CONNETTORE_HTTPS_CLIENT_AUTH);
			//			if ( isModalitaAvanzata) {
			//				de.setType(DataElementType.SELECT);
			//				de.setLabels(labelTipiCA);
			//				if(!modificaAbilitata && ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT.equals(clientAuth)){
			//					de.setValues(new String[]{AccordiServizioParteSpecificaCostanti.LABEL_APS_USA_PROFILO_PDD_FRUITORE});
			//					de.setSelected(AccordiServizioParteSpecificaCostanti.LABEL_APS_USA_PROFILO_PDD_FRUITORE);
			//				}else{
			//					de.setValues(tipoCA);
			//					de.setSelected(clientAuth);
			//				}
			//			}else{
			de.setType(DataElementType.HIDDEN);
			de.setValue(clientAuth);
			//			}
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_CLIENT_AUTH);
			if(clientAuth==null)
				clientAuth = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
			dati.addElement(de);


		}

		if (!isModalitaAvanzata) {
			de = new DataElement();
			de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
			de.setValue(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_DISABILITATO);
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);
		}

		return dati;
	}

	public Vector<DataElement>  addTipiAllegatiToDati(TipoOperazione tipoOp, String idServizio, String ruolo,
			String[] ruoli, String[] tipiAmmessi, String[] tipiAmmessiLabel,
			Vector<DataElement> dati) {
		
		DataElement de = new DataElement();
		de.setType(DataElementType.TITLE);
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_ALLEGATO);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_RUOLO);
		de.setType(DataElementType.SELECT);
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_RUOLO);
		de.setValues(ruoli);
		//				de.setOnChange("CambiaTipoDocumento('serviziAllegati')");
		de.setPostBack(true);
		de.setSelected(ruolo!=null ? ruolo : "");
		de.setSize( getSize());
		dati.addElement(de);

		if(tipiAmmessi!=null){
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_TIPO_FILE);
			de.setType(DataElementType.SELECT);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_FILE);
			de.setValues(tipiAmmessi);
			de.setLabels(tipiAmmessiLabel);
			de.setSize( getSize());
			dati.addElement(de);
		}

		de = new DataElement();
		de.setValue(idServizio);
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_THE_FILE);
		de.setType(DataElementType.FILE);
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_THE_FILE);
		de.setSize( getSize());
		dati.addElement(de);

		return dati;
	}

	public Vector<DataElement>  addInfoAllegatiToDati(TipoOperazione tipoOp,    String idAllegato,
			AccordoServizioParteSpecifica asps, Documento doc,
			Vector<DataElement> dati) {
		DataElement de = new DataElement();


		de = new DataElement();
		de.setValue(idAllegato);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_ALLEGATO);
		dati.addElement(de);


		de = new DataElement();
		de.setValue(doc.getRuolo());
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_RUOLO);
		de.setType(DataElementType.TEXT);
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_RUOLO);
		de.setSize( getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setValue(doc.getFile());
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_NOME_FILE);
		de.setType(DataElementType.TEXT);
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_DOCUMENTO);
		de.setSize( getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setValue(doc.getTipo());
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_TIPO_FILE);
		de.setType(DataElementType.TEXT);
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_FILE);
		de.setSize( getSize());
		dati.addElement(de);

		if(ServletUtils.isEditModeInProgress(this.request) && this.apsCore.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(asps.getStatoPackage())){
			this.pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);
		}
		else{	
			de = new DataElement();
			de.setType(DataElementType.FILE);
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_THE_FILE);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_THE_FILE);
			de.setSize( getSize());
			dati.addElement(de);
		}

		return dati;
	}


	public Vector<DataElement> addViewAllegatiToDati(TipoOperazione tipoOp, String idAllegato, String idServizio,
			Documento doc, StringBuffer contenutoAllegato, String errore,
			Vector<DataElement> dati) {
		DataElement de = new DataElement();

		de.setValue(idAllegato);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_ALLEGATO);
		dati.addElement(de);

		de = new DataElement();
		de.setType(DataElementType.TITLE);
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_ALLEGATO);
		dati.addElement(de);
		
		de = new DataElement();
		de.setValue(doc.getRuolo());
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_RUOLO);
		de.setType(DataElementType.TEXT);
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_RUOLO);
		de.setSize( getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setValue(doc.getFile());
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_NOME_FILE);
		de.setType(DataElementType.TEXT);
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_DOCUMENTO);
		de.setSize( getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setValue(doc.getTipo());
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_TIPO_FILE);
		de.setType(DataElementType.TEXT);
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_FILE);
		de.setSize( getSize());
		dati.addElement(de);

		if(errore!=null){
			de = new DataElement();
			de.setValue(errore);
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_THE_FILE);
			de.setType(DataElementType.TEXT );
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_DOCUMENTO);
			de.setSize( getSize());
			dati.addElement(de);
		}
		else{
			de = new DataElement();
			de.setLabel("");
			de.setType(DataElementType.TEXT_AREA_NO_EDIT);
			de.setValue(contenutoAllegato.toString());
			de.setRows(30);
			de.setCols(80);
			dati.addElement(de);
		}
		
		DataElement saveAs = new DataElement();
		saveAs.setValue(AccordiServizioParteSpecificaCostanti.LABEL_APS_DOWNLOAD);
		saveAs.setType(DataElementType.LINK);
		Parameter pIdAccordo = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_ACCORDO, idServizio);
		Parameter pIdAllegato = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_ALLEGATO, idAllegato);
		Parameter pTipoDoc = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_DOCUMENTO, "asps");
		//			String params = "idAccordo="+idServizio+"&idAllegato="+idAllegato+"&tipoDocumento=asps";
		saveAs.setUrl(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT, pIdAccordo, pIdAllegato, pTipoDoc);
		dati.add(saveAs);

		return dati;
	}
}
