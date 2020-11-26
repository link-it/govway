/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.config.CanaleConfigurazione;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizio;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataServizio;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.MTOMProcessorType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.mvc.properties.utils.ConfigManager;
import org.openspcoop2.core.mvc.properties.utils.PropertiesSourceConfiguration;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.constants.ProprietariDocumento;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.protocol.sdk.validator.ValidazioneResult;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedListServlet;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneUtilities;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCostanti;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ExporterUtils;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.AreaBottoni;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.CheckboxStatusType;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementImage;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
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
	public AccordiServizioParteSpecificaHelper(ControlStationCore core, HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(core, request, pd,  session);
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
			String oldTiposervizio, int oldVersioneServizio,
			String nomeservizio,
			String tiposervizio, String idSoggErogatore,
			String nomeErogatore, String tipoErogatore, String idAccordo, ServiceBinding serviceBinding,
			String servcorr, String endpointtype, String url, String nome,
			String tipo, String user, String password, String initcont,
			String urlpgk, String provurl, String connfact, String sendas,
			BinaryParameter wsdlimpler, BinaryParameter wsdlimplfru, String id,
			String profilo, String portType, String[] ptList,
			boolean visibilitaAccordoServizio,boolean visibilitaServizio,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			boolean httpsTrustVerifyCert, String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey, 
			String httpsKeyAlias, String httpsTrustStoreCRLs,
			String tipoconn, String versione,
			boolean validazioneDocumenti,  String backToStato,
			String autenticazioneHttp,
			String proxyEnabled, String proxyHost, String proxyPort, String proxyUsername, String proxyPassword,
			String tempiRisposta_enabled, String tempiRisposta_connectionTimeout, String tempiRisposta_readTimeout, String tempiRisposta_tempoMedioRisposta,
			String opzioniAvanzate, String transfer_mode, String transfer_mode_chunk_size, String redirect_mode, String redirect_max_hop,
			String requestOutputFileName,String requestOutputFileNameHeaders,String requestOutputParentDirCreateIfNotExists,String requestOutputOverwriteIfExists,
			String responseInputMode, String responseInputFileName, String responseInputFileNameHeaders, String responseInputDeleteAfterRead, String responseInputWaitTime,
			String erogazioneSoggetto,String erogazioneRuolo,String erogazioneAutenticazione,String erogazioneAutenticazioneOpzionale, TipoAutenticazionePrincipal erogazioneAutenticazionePrincipal, List<String> erogazioneAutenticazioneParametroList, String erogazioneAutorizzazione,
			String erogazioneAutorizzazioneAutenticati,String erogazioneAutorizzazioneRuoli, String erogazioneAutorizzazioneRuoliTipologia, String erogazioneAutorizzazioneRuoliMatch,boolean isSupportatoAutenticazione,
			boolean generaPACheckSoggetto, List<ExtendedConnettore> listExtendedConnettore,
			String fruizioneServizioApplicativo,String fruizioneRuolo,String fruizioneAutenticazione,String fruizioneAutenticazioneOpzionale, TipoAutenticazionePrincipal fruizioneAutenticazionePrincipal, List<String> fruizioneAutenticazioneParametroList, String fruizioneAutorizzazione,
			String fruizioneAutorizzazioneAutenticati,String fruizioneAutorizzazioneRuoli, String fruizioneAutorizzazioneRuoliTipologia, String fruizioneAutorizzazioneRuoliMatch,
			String protocollo,BinaryParameter allegatoXacmlPolicy,
			String descrizione, String tipoFruitore, String nomeFruitore,
			boolean autenticazioneToken, String tokenPolicy, boolean erogazioneServizioApplicativoServerEnabled,
			String erogazioneServizioApplicativoServer, String canaleStato, String canale, boolean gestioneCanaliEnabled)	throws Exception {

		boolean isModalitaAvanzata = this.isModalitaAvanzata();

		try{

			String tipologia = ServletUtils.getObjectFromSession(this.session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			boolean gestioneFruitori = false;
			boolean gestioneErogatori = false;
			if(tipologia!=null) {
				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
					gestioneFruitori = true;
				}
				else if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia)) {
					gestioneErogatori = true;
				}
			}
			
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
					this.pd.setMessage(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_USO_ACCORDO_SERVIZIO_CON_VISIBILITA_PRIVATA_IN_UN_SERVIZIO_CON_VISIBILITA_PUBBLICA);
					return false;
				}
			}

			Integer versioneInt = 1;
			if(versione!=null && !"".equals(versione)){
				versioneInt = Integer.parseInt(versione);
			}
			else {
				versione = "1";
				versioneInt = 1;
			}
			if (tipoOp.equals(TipoOperazione.CHANGE)) {

				@SuppressWarnings("unused")
				IDServizio idService = this.idServizioFactory.getIDServizioFromValues(tiposervizio, nomeservizio, 
						tipoErogatore, nomeErogatore, versioneInt);

				try {
					AccordoServizioParteSpecifica servizio = this.apsCore.getAccordoServizioParteSpecifica(idInt);
					idSoggErogatore = servizio.getIdSoggetto().toString();
				} catch (DriverRegistroServiziNotFound e) {
				} catch (DriverRegistroServiziException e) {
				}

			}

			// Campi obbligatori
			if (!isModalitaAvanzata || this.apsCore.isPortTypeObbligatorioImplementazioniSOAP()) {
				switch (serviceBinding) {
				case REST:
					
					break;
				case SOAP:
				default:
					if (portType == null || "".equals(portType) || "-".equals(portType)) {
						if(ptList==null || ptList.length == 0){
							this.pd.setMessage(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_SERVIZIO_OBBLIGATORIO_PORT_TYPE_NON_PRESENTI);
							return false;
						}
						
						this.pd.setMessage(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_SERVIZIO_OBBLIGATORIO);
						return false;
					}
					break;
				}
			}
			if (nomeservizio.equals("") || tiposervizio.equals("") || idSoggErogatore.equals("") || idAccordo.equals("") || versione.equals("")) {
				String tmpElenco = "";
				if (nomeservizio.equals("")) {
					tmpElenco = AccordiServizioParteSpecificaCostanti.LABEL_APS_NOME_SERVIZIO;
				}
				if (tiposervizio.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = AccordiServizioParteSpecificaCostanti.LABEL_APS_TIPO_SERVIZIO;
					} else {
						tmpElenco = tmpElenco + ", " + AccordiServizioParteSpecificaCostanti.LABEL_APS_TIPO_SERVIZIO;
					}
				}
				if (idSoggErogatore.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = AccordiServizioParteSpecificaCostanti.LABEL_APS_SOGGETTO;
					} else {
						tmpElenco = tmpElenco + ", " + AccordiServizioParteSpecificaCostanti.LABEL_APS_SOGGETTO;
					}
				}
				if (idAccordo.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = AccordiServizioParteSpecificaCostanti.LABEL_APC_COMPOSTO_SOLO_PARTE_COMUNE;
					} else {
						tmpElenco = tmpElenco + ", " + AccordiServizioParteSpecificaCostanti.LABEL_APC_COMPOSTO_SOLO_PARTE_COMUNE;
					}
				}
				if (versione.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = AccordiServizioParteSpecificaCostanti.LABEL_APS_VERSIONE_APS;
					} else {
						tmpElenco = tmpElenco + ", " + AccordiServizioParteSpecificaCostanti.LABEL_APS_VERSIONE_APS;
					}	
				}
				this.pd.setMessage(MessageFormat.format(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_CON_PARAMETRO, tmpElenco));
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if ((nomeservizio.indexOf(" ") != -1) || (tiposervizio.indexOf(" ") != -1)) {
				this.pd.setMessage(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_SPAZI_BIANCHI_NON_CONSENTITI);
				return false;
			}

			// Il nome deve contenere solo lettere e numeri e '_' '-' '.'
			if(this.checkNCName(nomeservizio,AccordiServizioParteSpecificaCostanti.LABEL_APS_NOME_SERVIZIO)==false){
				return false;
			}

			// Il tipo deve contenere solo lettere e numeri
			if(this.checkNCName(tiposervizio,AccordiServizioParteSpecificaCostanti.LABEL_APS_TIPO_SERVIZIO)==false){
				return false;
			}

			// Versione deve essere un numero intero
			if (!versione.equals("") && !this.checkNumber(versione, AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_VERSIONE, false)) {
				return false;
			}
			
			// Check lunghezza
			if(this.checkLength255(nomeservizio, AccordiServizioParteSpecificaCostanti.LABEL_APS_NOME_SERVIZIO)==false) {
				return false;
			}
			if(descrizione!=null && !"".equals(descrizione)) {
				if(this.checkLength255(descrizione, AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_DESCRIZIONE)==false) {
					return false;
				}
			}
			
			if (tipoOp.equals(TipoOperazione.ADD)) {
				if(this.canaleCheckData(canaleStato, canale, gestioneCanaliEnabled) == false) {
					return false;
				}
			}
			
			// Controllo dell'end-point
			// Non li puo' prendere dalla servtlet
			boolean connettoreStatic = false;
			if(gestioneFruitori) {
				connettoreStatic = this.apsCore.isConnettoreStatic(protocollo);
			}
			if(!connettoreStatic) {
				if (!this.endPointCheckData(protocollo, gestioneErogatori,
						endpointtype, url, nome, tipo,
						user, password, initcont, urlpgk, provurl, connfact,
						sendas, httpsurl, httpstipologia, httpshostverify,
						httpsTrustVerifyCert, httpspath, httpstipo, httpspwd, httpsalgoritmo, httpsstato,
						httpskeystore, httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey, 
						httpspwdprivatekey, httpsalgoritmokey,
						httpsKeyAlias, httpsTrustStoreCRLs,
						tipoconn,autenticazioneHttp,
						proxyEnabled, proxyHost, proxyPort, proxyUsername, proxyPassword,
						tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta,
						opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
						requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
						autenticazioneToken, tokenPolicy,
						listExtendedConnettore,erogazioneServizioApplicativoServerEnabled,erogazioneServizioApplicativoServer)) {
					return false;
				}
			}

			// Controllo che i campi "checkbox" abbiano uno dei valori
			// ammessi
			if ((servcorr != null) && !servcorr.equals(Costanti.CHECK_BOX_ENABLED) && !servcorr.equals(Costanti.CHECK_BOX_DISABLED)) {
				this.pd.setMessage(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_SERVIZIO_CORRELATO_DEV_ESSERE_SELEZIONATO_O_DESELEZIONATO);
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
				this.pd.setMessage(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_IL_SOGGETTO_INDICATO_NON_AUTORIZZATO_A_EROGARE);
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
				this.pd.setMessage(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_ACCORDO_SERVIZIO_DEV_ESSERE_SCELTO_TRA_QUELLI_DEFINITI_NEL_PANNELLO_ACCORDI_SERVIZIO);
				return false;
			}


			// Visibilita rispetto al soggetto erogatore
			if(visibilitaServizio==visibile){
				org.openspcoop2.core.registry.Soggetto tmpSogg = this.soggettiCore.getSoggettoRegistro(new IDSoggetto(tipoErogatore, nomeErogatore));
				if(tmpSogg.getPrivato()!=null && tmpSogg.getPrivato()==true){
					this.pd.setMessage(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_USO_SOGGETTO_EROGATORE_CON_VISIBILITA_PRIVATA_IN_UN_SERVIZIO_CON_VISIBILITA_PUBBLICA);
					return false;
				}
			}


			// Se il connettore e' disabilitato devo controllare che il
			// connettore del soggetto non sia disabilitato se è di tipo operativo
			if(!gestioneFruitori && !gestioneErogatori && !connettoreStatic) {
				if (endpointtype.equals(TipiConnettore.DISABILITATO.getNome())) {
					String eptypeprov = TipiConnettore.DISABILITATO.getNome();
	
					org.openspcoop2.core.registry.Soggetto soggetto = this.soggettiCore.getSoggettoRegistro(new IDSoggetto(tipoErogatore, nomeErogatore));
					if(this.pddCore.isPddEsterna(soggetto.getPortaDominio())){
						Connettore connettore = soggetto.getConnettore();
						if ((connettore != null) && (connettore.getTipo() != null)) {
							eptypeprov = connettore.getTipo();
						}
		
						if (eptypeprov.equals(TipiConnettore.DISABILITATO.getNome())) {
							if(!this.isModalitaCompleta() && tipoOp.equals(TipoOperazione.ADD)) {
								this.pd.setMessage(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_IL_CONNETTORE_SUL_SERVIZIO_NON_PUO_ESSERE_DISABILITATO_POICHE_NON_E_STATO_DEFINITO_UN_CONNETTORE_EROGAZIONE);
							}
							else {
								this.pd.setMessage(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_IL_CONNETTORE_DEL_SERVIZIO_DEVE_ESSERE_SPECIFICATO_SE_NON_EGRAVE_STATO_DEFINITO_UN_CONNETTORE_PER_IL_SOGGETTO_EROGATORE);
							}
							return false;
						}
					}
					else{
						if(tipoOp.equals(TipoOperazione.CHANGE)){
							boolean escludiSoggettiEsterni = true;
							boolean trovatoServ = this.apsCore.existFruizioniServizioWithoutConnettore(idInt,escludiSoggettiEsterni);
							if (trovatoServ) {
								this.pd.setMessage(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_IL_CONNETTORE_SUL_SERVIZIO_NON_PUO_ESSERE_DISABILITATO_POICHE_NON_E_STATO_DEFINITO_UN_CONNETTORE_SUL_SOGGETTO_EROGATORE_ED_ESISTONO_FRUIZIONI_DEL_SERVIZIO_DA_PARTE_DI_SOGGETTI_OPERATIVI_CHE_NON_HANNO_UN_CONNETTORE_DEFINITO);
								return false;
							}
						}
		//				}
						
						else {
							if(!this.isModalitaCompleta() && generaPACheckSoggetto) {
								this.pd.setMessage(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_IL_CONNETTORE_SUL_SERVIZIO_NON_PUO_ESSERE_DISABILITATO_POICHE_NON_E_STATO_DEFINITO_UN_CONNETTORE_EROGAZIONE);
								return false;
							}
						}
					}
				}
			}

			// idSoggettoErogatore
			long idSoggettoErogatore = Long.parseLong(idSoggErogatore);
			if (idSoggettoErogatore < 0)
				throw new Exception(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_ID_SOGGETTO_EROGATORE_NON_DEFINITO);

			// idAccordo Servizio
			long idAccordoServizio = Long.parseLong(idAccordo);
			if (idAccordoServizio < 0)
				throw new Exception(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_ID_ACCORDO_SERVIZIO_NON_DEFINITO);

			// Dati Servizio 
			long idSoggettoErogatoreLong = Long.parseLong(idSoggErogatore);
			Soggetto soggettoErogatore = this.soggettiCore.getSoggettoRegistro(idSoggettoErogatoreLong);
			long idAccordoServizioParteComuneLong = Long.parseLong(idAccordo);
			AccordoServizioParteComuneSintetico accordoServizioParteComune = this.apcCore.getAccordoServizioSintetico(idAccordoServizioParteComuneLong);
			String uriAccordoServizioParteComune = this.idAccordoFactory.getUriFromAccordo(accordoServizioParteComune);
			IDServizio idAccordoServizioParteSpecifica = this.idServizioFactory.getIDServizioFromValues(tiposervizio, nomeservizio, 
					tipoErogatore, nomeErogatore, versioneInt);
			
			// Indicazione se e' un servizio correlato
			// boolean isServizioCorrelato = ((servcorr != null && servcorr
			// .equals(Costanti.CHECK_BOX_ENABLED)) ? true : false);

			// Controllo non esistenza gia' del servizio
			if (
					(!tipoOp.equals(TipoOperazione.CHANGE)) 
					|| 
					(
							tipoOp.equals(TipoOperazione.CHANGE)
							&& 
							( 
								(!oldTiposervizio.equals(tiposervizio)) 
									|| 
								(!oldNomeservizio.equals(nomeservizio)) 
									|| 
								(oldVersioneServizio!=versioneInt)
							) 
					)
				){
				
				StringBuilder inUsoMessage = new StringBuilder();
				
				boolean alreadyExists = AccordiServizioParteSpecificaUtilities.alreadyExists(this.apsCore, this, 
						idSoggettoErogatore, idAccordoServizioParteSpecifica, uriAccordoServizioParteComune,
						tipoFruitore, nomeFruitore,
						protocollo, profilo, portType,
						gestioneFruitori, gestioneErogatori,
						inUsoMessage);
				
				if(alreadyExists) {
					this.pd.setMessage(inUsoMessage.toString());
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
			
			long idAccordoServizioParteSpecificaLong = idInt;
			boolean servizioCorrelato = false;
			if ((servcorr != null) && (servcorr.equals(Costanti.CHECK_BOX_ENABLED) || servcorr.equals(CostantiConfigurazione.ABILITATO.getValue()))) {
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
				if(!gestioneFruitori && !gestioneErogatori && this.apsCore.existsAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica)){
					if(this.apsCore.isSupportatoVersionamentoAccordiServizioParteSpecifica(protocollo))
						this.pd.setMessage(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_ESISTE_GIA_UN_ACCORDO_DI_SERVIZIO_PARTE_SPECIFICA_CON_TIPO_NOME_VERSIONE_E_SOGGETTO_INDICATO);
					else
						this.pd.setMessage(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_ESISTE_GIA_UN_ACCORDO_DI_SERVIZIO_PARTE_SPECIFICA_CON_TIPO_NOME_VERSIONE_E_SOGGETTO_INDICATO);
					return false;
				}
			}else{
				// change
				try{
					AccordoServizioParteSpecifica servizio =  this.apsCore.getServizio(idAccordoServizioParteSpecifica);
					if(servizio!=null){
						if (idInt != servizio.getId()) {
							if(this.apsCore.isSupportatoVersionamentoAccordiServizioParteSpecifica(protocollo))
								this.pd.setMessage(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_ESISTE_GIA_UN_ACCORDO_DI_SERVIZIO_PARTE_SPECIFICA_CON_TIPO_NOME_VERSIONE_E_SOGGETTO_INDICATO);
							else
								this.pd.setMessage(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_ESISTE_GIA_UN_ACCORDO_DI_SERVIZIO_PARTE_SPECIFICA_CON_TIPO_NOME_VERSIONE_E_SOGGETTO_INDICATO);
							return false;
						}
					}
				}catch(DriverRegistroServiziNotFound dNotFound){}
			}

			// Lettura accordo di servizio
			AccordoServizioParteComune as = null;
			try{
				as = this.apcCore.getAccordoServizioFull(Long.parseLong(idAccordo));
			}catch(Exception e){
				this.pd.setMessage(MessageFormat.format(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_API_SELEZIONATA_NON_ESISTENTE_CON_PARAMETRI, idAccordo,
						e.getMessage()));
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

			ValidazioneResult v = this.apsCore.validazione(aps, this.soggettiCore);
			if(v.isEsito()==false){
				this.pd.setMessage(MessageFormat.format(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_VALIDAZIONE_PROTOCOLLO_CON_PARAMETRI, protocollo, v.getMessaggioErrore()));
				if(v.getException()!=null)
					this.log.error(MessageFormat.format(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_VALIDAZIONE_PROTOCOLLO_CON_PARAMETRI, protocollo, v.getMessaggioErrore()),v.getException());
				else
					this.log.error(MessageFormat.format(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_VALIDAZIONE_PROTOCOLLO_CON_PARAMETRI, protocollo, v.getMessaggioErrore()));
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
				
				String gestioneToken = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN);
				String gestioneTokenPolicy = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_POLICY);
				String gestioneTokenValidazioneInput = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_VALIDAZIONE_INPUT);
				String gestioneTokenIntrospection = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_INTROSPECTION);
				String gestioneTokenUserInfo = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_USERINFO);
				String gestioneTokenTokenForward = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_TOKEN_FORWARD);
				String autorizzazione_token = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN);
				String autorizzazione_tokenOptions = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_OPTIONS);
				String autorizzazioneScope = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_SCOPE);
				String autorizzazioneScopeMatch = this.getParameter(CostantiControlStation.PARAMETRO_SCOPE_MATCH);

				String autorizzazioneContenutiStato = this.getParameter(CostantiControlStation.PARAMETRO_AUTORIZZAZIONE_CONTENUTI_STATO);
				String autorizzazioneContenuti = this.getParameter(CostantiControlStation.PARAMETRO_AUTORIZZAZIONE_CONTENUTI);
				String autorizzazioneContenutiProperties = this.getParameter(CostantiControlStation.PARAMETRO_AUTORIZZAZIONE_CONTENUTI_PROPERTIES);
				
				if(gestioneFruitori) {
					
					if(this.controlloAccessiCheck(tipoOp, fruizioneAutenticazione, fruizioneAutenticazioneOpzionale, fruizioneAutenticazionePrincipal, fruizioneAutenticazioneParametroList, 
							fruizioneAutorizzazione, fruizioneAutorizzazioneAutenticati, fruizioneAutorizzazioneRuoli, 
							fruizioneAutorizzazioneRuoliTipologia, fruizioneAutorizzazioneRuoliMatch,
							true, true, null, null,gestioneToken, gestioneTokenPolicy, 
							gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
							autorizzazione_token,autorizzazione_tokenOptions,
							autorizzazioneScope,autorizzazioneScopeMatch,allegatoXacmlPolicy,
							autorizzazioneContenutiStato, autorizzazioneContenuti, autorizzazioneContenutiProperties,
							protocollo)==false){
						return false;
					}
				}
				else {
				
					
					if(this.controlloAccessiCheck(tipoOp, erogazioneAutenticazione, erogazioneAutenticazioneOpzionale, erogazioneAutenticazionePrincipal, erogazioneAutenticazioneParametroList,
							erogazioneAutorizzazione, erogazioneAutorizzazioneAutenticati, erogazioneAutorizzazioneRuoli, 
							erogazioneAutorizzazioneRuoliTipologia, erogazioneAutorizzazioneRuoliMatch,
							isSupportatoAutenticazione, false, null, null,gestioneToken, gestioneTokenPolicy, 
							gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
							autorizzazione_token,autorizzazione_tokenOptions,
							autorizzazioneScope,autorizzazioneScopeMatch,allegatoXacmlPolicy,
							autorizzazioneContenutiStato, autorizzazioneContenuti, autorizzazioneContenutiProperties,
							protocollo)==false){
						return false;
					}
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
			String myId, 
			String httpsurl, String httpstipologia, boolean httpshostverify,
			boolean httpsTrustVerifyCert, String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey, 
			String httpsKeyAlias, String httpsTrustStoreCRLs,
			String tipoconn, 
			boolean validazioneDocumenti, String backToStato,
			String autenticazioneHttp,
			String proxyEnabled, String proxyHost, String proxyPort, String proxyUsername, String proxyPassword,
			String tempiRisposta_enabled, String tempiRisposta_connectionTimeout, String tempiRisposta_readTimeout, String tempiRisposta_tempoMedioRisposta,
			String opzioniAvanzate, String transfer_mode, String transfer_mode_chunk_size, String redirect_mode, String redirect_max_hop,
			String requestOutputFileName,String requestOutputFileNameHeaders,String requestOutputParentDirCreateIfNotExists,String requestOutputOverwriteIfExists,
			String responseInputMode, String responseInputFileName, String responseInputFileNameHeaders, String responseInputDeleteAfterRead, String responseInputWaitTime,
			String fruizioneServizioApplicativo,String fruizioneRuolo,String fruizioneAutenticazione,String fruizioneAutenticazioneOpzionale, TipoAutenticazionePrincipal fruizioneAutenticazionePrincipal, List<String> fruizioneAutenticazioneParametroList, String fruizioneAutorizzazione,
			String fruizioneAutorizzazioneAutenticati,String fruizioneAutorizzazioneRuoli, String fruizioneAutorizzazioneRuoliTipologia, String fruizioneAutorizzazioneRuoliMatch,BinaryParameter allegatoXacmlPolicy,
			boolean autenticazioneToken, String tokenPolicy,
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
				this.pd.setMessage(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_SOGGETTO_MANCANTE);
				return false;
			}

			int idInt = 0;
			if (!nomeservizio.equals("")) {
				IDSoggetto ids = new IDSoggetto(tipoprov, nomeprov);
				IDServizio idserv = this.idServizioFactory.getIDServizioFromValues(tiposervizio, nomeservizio, ids, versioneservizio);
				AccordoServizioParteSpecifica myServ = this.apsCore.getServizio(idserv);
				idInt = myServ.getId().intValue();
			} else {
				idInt = Integer.parseInt(id);
			}
			AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(idInt);
			String protocollo = ProtocolFactoryManager.getInstance().getProtocolByServiceType(asps.getTipo());
			
//			String tipologia = ServletUtils.getObjectFromSession(this.session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
//			boolean gestioneFruitori = false;
//			if(tipologia!=null) {
//				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
//					gestioneFruitori = true;
//				}
//			}
//			boolean connettoreOnly = gestioneFruitori;
			
			String tmpModificaProfilo = this.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_PROFILO);
			boolean modificaProfilo = false;
			if(tmpModificaProfilo!=null) {
				modificaProfilo = "true".equals(tmpModificaProfilo);
			}
			
			// Se il connettore e' disabilitato devo controllare che il
			// connettore del soggetto non sia disabilitato se è di tipo operativo
			//if (this.isModalitaAvanzata() || connettoreOnly) {
			if ( !modificaProfilo && (this.isModalitaAvanzata() || TipoOperazione.CHANGE.equals(tipoOp)) ) {
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
			
							boolean connettoreStatic = this.apsCore.isConnettoreStatic(protocollo);
							
							if (!connettoreStatic && eptypeprov.equals(TipiConnettore.DISABILITATO.getNome())) {
								if(tipoOp.equals(TipoOperazione.CHANGE)){
									this.pd.setMessage(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_PER_POTER_DISABILITARE_IL_CONNETTORE_DEVE_PRIMA_ESSERE_DEFINITO_UN_CONNETTORE_SUL_SERVIZIO_O_SUL_SOGGETTO_EROGATORE);
								}
								else{
									if(this.isModalitaAvanzata()){
										this.pd.setMessage(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_PER_POTER_AGGIUNGERE_IL_FRUITORE_DEVE_ESSERE_DEFINITO_IL_CONNETTORE_BR_IN_ALTERNATIVA_È_POSSIBILE_CONFIGURARE_UN_CONNETTORE_SUL_SERVIZIO_O_SUL_SOGGETTO_EROGATORE_PRIMA_DI_PROCEDERE_CON_LA_CREAZIONE_DEL_FRUITORE);
									}
									else{
										this.pd.setMessage(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_PER_POTER_AGGIUNGERE_IL_FRUITORE_DEVE_PRIMA_ESSERE_DEFINITO_UN_CONNETTORE_SUL_SERVIZIO_O_SUL_SOGGETTO_EROGATORE);
									}
								}
								return false;
							}
							
						}
						
					}
					
				}
			
				boolean servizioApplicativoServerEnabled = false;
				String servizioApplicativoServer = null;
				// Controllo dell'end-point
				// Non li puo' prendere dalla servtlet
				if (!this.endPointCheckData(protocollo, false,
						endpointtype, url, nome, tipo,
						user, password, initcont, urlpgk, provurl, connfact,
						sendas, httpsurl, httpstipologia, httpshostverify,
						httpsTrustVerifyCert, httpspath, httpstipo, httpspwd, httpsalgoritmo, httpsstato,
						httpskeystore, httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey,
						httpspwdprivatekey, httpsalgoritmokey,
						httpsKeyAlias, httpsTrustStoreCRLs,
						tipoconn,autenticazioneHttp,
						proxyEnabled, proxyHost, proxyPort, proxyUsername, proxyPassword,
						tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta,
						opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
						requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
						autenticazioneToken, tokenPolicy,
						listExtendedConnettore,servizioApplicativoServerEnabled,servizioApplicativoServer)) {
					return false;
				}
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

				IDServizio idserv = this.idServizioFactory.getIDServizioFromAccordo(asps);
				long idFru = this.apsCore.getServizioFruitore(idserv, idProv);
				if ((idFru != 0) && (tipoOp.equals(TipoOperazione.ADD) || ((tipoOp.equals(TipoOperazione.CHANGE)) && (myIdInt != idFru)))) {
					this.pd.setMessage(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_ESISTE_GI_AGRAVE_UN_FRUITORE_DEL_SERVIZIO_CON_LO_STESSO_SOGGETTO);
					return false;
				}
			}
			else{

				// Lettura accordo di servizio
				AccordoServizioParteComune as = null;
				IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromUri(asps.getAccordoServizioParteComune());
				try{
					as = this.apcCore.getAccordoServizioFull(idAccordo);
				}catch(Exception e){
					this.pd.setMessage(MessageFormat.format(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_API_SELEZIONATA_NON_ESISTENTE_CON_PARAMETRI, idAccordo,e.getMessage()));
					return false;
				}

				// Validazione Documenti
				if(validazioneDocumenti && tipoOp.equals(TipoOperazione.ADD) && this.isModalitaAvanzata() ){
					
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
				
				String gestioneToken = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN);
				String gestioneTokenPolicy = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_POLICY);
				String gestioneTokenValidazioneInput = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_VALIDAZIONE_INPUT);
				String gestioneTokenIntrospection = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_INTROSPECTION);
				String gestioneTokenUserInfo = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_USERINFO);
				String gestioneTokenTokenForward = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_TOKEN_FORWARD);
				String autorizzazione_token = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN);
				String autorizzazione_tokenOptions = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_OPTIONS);
				String autorizzazioneScope = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_SCOPE);
				String autorizzazioneScopeMatch = this.getParameter(CostantiControlStation.PARAMETRO_SCOPE_MATCH);
								
				String autorizzazioneContenutiStato = this.getParameter(CostantiControlStation.PARAMETRO_AUTORIZZAZIONE_CONTENUTI_STATO);
				String autorizzazioneContenuti = this.getParameter(CostantiControlStation.PARAMETRO_AUTORIZZAZIONE_CONTENUTI);
				String autorizzazioneContenutiProperties = this.getParameter(CostantiControlStation.PARAMETRO_AUTORIZZAZIONE_CONTENUTI_PROPERTIES);
				
				if(this.controlloAccessiCheck(tipoOp, fruizioneAutenticazione, fruizioneAutenticazioneOpzionale, fruizioneAutenticazionePrincipal, fruizioneAutenticazioneParametroList,
						fruizioneAutorizzazione, fruizioneAutorizzazioneAutenticati, fruizioneAutorizzazioneRuoli, 
						fruizioneAutorizzazioneRuoliTipologia, fruizioneAutorizzazioneRuoliMatch,
						true, true, null, null,gestioneToken, gestioneTokenPolicy, 
						gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
						autorizzazione_token,autorizzazione_tokenOptions,
						autorizzazioneScope,autorizzazioneScopeMatch,allegatoXacmlPolicy,
						autorizzazioneContenutiStato, autorizzazioneContenuti, autorizzazioneContenutiProperties,
						protocollo)==false){
					return false;
				}
			}

			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public boolean serviziAllegatiCheckData(TipoOperazione tipoOp,FormFile formFile,Documento documento, IProtocolFactory<?> pf)
			throws Exception {

		try{

			// String userLogin = (String) this.session.getAttribute("Login");

			String ruolo = this.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_RUOLO);

			// Campi obbligatori
			if (ruolo.equals("")) {
				this.pd.setMessage(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_IL_TIPO_DI_DOCUMENTO);
				return false;
			}

			if(formFile==null || formFile.getFileName()!=null && "".equals(formFile.getFileName())){
				this.pd.setMessage(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_DOCUMENTO_OBBLIGATORIO);
				return false;
			}

			if(formFile==null || formFile.getFileSize()<=0){
				this.pd.setMessage(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_DOCUMENTO_SELEZIONATO_NON_PUO_ESSERE_VUOTO);
				return false;
			}

			if(documento.getTipo()==null || "".equals(documento.getTipo()) || documento.getTipo().length()>30 || formFile.getFileName().lastIndexOf(".")==-1){
				if(documento.getTipo()==null || "".equals(documento.getTipo()) || formFile.getFileName().lastIndexOf(".")==-1){
					this.pd.setMessage(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_ESTENSIONE_DEL_DOCUMENTO_NON_VALIDA);
				}else{
					this.pd.setMessage(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_ESTENSIONE_DEL_DOCUMENTO_NON_VALIDA_DIMENSIONE_ESTENSIONE_TROPPO_LUNGA);
				}
				return false;
			}

			boolean documentoUnivocoIndipendentementeTipo = true;
			if(this.archiviCore.existsDocumento(documento,ProprietariDocumento.servizio,documentoUnivocoIndipendentementeTipo)){

				String tipo = documento.getTipo();
				String ruoloDoc = documento.getRuolo();
				String allegatoMsg = AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_ALLEGATO_CON_NOME_TIPO_GIA_PRESENTE_NEL_SERVIZIO_CON_PARAMETRI;
				String specificaMsg = AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_LA_SPECIFICA_CON_NOME_TIPO_GIA_PRESENTE_NEL_SERVIZIO;
				if(documentoUnivocoIndipendentementeTipo) {
					tipo = null;
					ruoloDoc = null;
					allegatoMsg = AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_ALLEGATO_CON_NOME_TIPO_GIA_PRESENTE_NEL_SERVIZIO_CON_PARAMETRI_SENZA_TIPO;
					specificaMsg = AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_LA_SPECIFICA_CON_NOME_TIPO_GIA_PRESENTE_NEL_SERVIZIO_SENZA_TIPO;
				}
				
				//check se stesso documento
				Documento existing = this.archiviCore.getDocumento(documento.getFile(),tipo,ruoloDoc,documento.getIdProprietarioDocumento(),false,ProprietariDocumento.servizio);
				if(existing.getId().longValue() == documento.getId().longValue())
					return true;

				if(RuoliDocumento.allegato.toString().equals(documento.getRuolo()) || documentoUnivocoIndipendentementeTipo) {
					if(documentoUnivocoIndipendentementeTipo) {
						this.pd.setMessage(MessageFormat.format(allegatoMsg,
								documento.getFile()));
					}
					else {
						this.pd.setMessage(MessageFormat.format(allegatoMsg,
								documento.getFile(), documento.getTipo()));
					}
				}else {
					if(documentoUnivocoIndipendentementeTipo) {
						this.pd.setMessage(MessageFormat.format(specificaMsg, documento.getRuolo(),
								documento.getFile()));
					}
					else {
						this.pd.setMessage(MessageFormat.format(specificaMsg, documento.getRuolo(),
								documento.getFile(), documento.getTipo()));
					}
				}
				
				return false;
			}
			

			ValidazioneResult valida = pf.createValidazioneDocumenti().valida (documento);
			if(!valida.isEsito()) {
				this.pd.setMessage(valida.getMessaggioErrore());
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
			
			String tipologia = ServletUtils.getObjectFromSession(this.session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			boolean gestioneFruitori = false;
			if(tipologia!=null) {
				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
					gestioneFruitori = true;
				}
			}
			
			String id = this.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);		
			
			String tipoSoggettoFruitore = null;
			String nomeSoggettoFruitore = null;
			IDSoggetto idSoggettoFruitore = null;
			Parameter pTipoSoggettoFruitore = null;
			Parameter pNomeSoggettoFruitore = null;
			if(gestioneFruitori) {
				tipoSoggettoFruitore = this.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SOGGETTO_FRUITORE);
				nomeSoggettoFruitore = this.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SOGGETTO_FRUITORE);
				idSoggettoFruitore = new IDSoggetto(tipoSoggettoFruitore, nomeSoggettoFruitore);
				pTipoSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SOGGETTO_FRUITORE, tipoSoggettoFruitore);
				pNomeSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SOGGETTO_FRUITORE, nomeSoggettoFruitore);
			}
			
			Parameter pModificaAPI = null;
			String modificaAPI = this.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_API);
			if(modificaAPI!=null) {
				pModificaAPI = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_API, modificaAPI);
			}
			
			List<Parameter> listAllegatiSession = new ArrayList<>();
			listAllegatiSession.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, id));
			if(gestioneFruitori) {
				listAllegatiSession.add(pTipoSoggettoFruitore);
				listAllegatiSession.add(pNomeSoggettoFruitore);
			}
			if(pModificaAPI!=null) {
				listAllegatiSession.add(pModificaAPI);
			}
			ServletUtils.addListElementIntoSession(this.session, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_ALLEGATI, 
					listAllegatiSession.toArray(new Parameter[1]));

			int idLista = Liste.SERVIZI_ALLEGATI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
						
			String tipoProtocollo = this.apsCore.getProtocolloAssociatoTipoServizio(asps.getTipo());
			
			String tmpTitle = null;
			if(gestioneFruitori) {
				tmpTitle = this.getLabelServizioFruizione(tipoProtocollo, idSoggettoFruitore, asps);
			}
			else {
				tmpTitle = this.getLabelServizioErogazione(tipoProtocollo, asps);
			}
			
			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, this.session);
			if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
				if(gestioneFruitori) {
					lstParam.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_FRUIZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
				} else {
					lstParam.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_EROGAZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
				}
				List<Parameter> listErogazioniChange = new ArrayList<>();
				Parameter pIdServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId()+ "");
				Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, asps.getNome());
				Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, asps.getTipo());
				listErogazioniChange.add(pIdServizio);
				listErogazioniChange.add(pNomeServizio);
				listErogazioniChange.add(pTipoServizio);
				if(gestioneFruitori) {
					listErogazioniChange.add(pNomeSoggettoFruitore);
					listErogazioniChange.add(pTipoSoggettoFruitore);
				}
				if(pModificaAPI!=null) {
					listErogazioniChange.add(pModificaAPI);
				}
				lstParam.add(new Parameter(tmpTitle, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_CHANGE, 
						listErogazioniChange.toArray(new Parameter[1])));
				
				lstParam.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_MODIFICA_SERVIZIO_INFO_GENERALI, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, 
						listErogazioniChange.toArray(new Parameter[1])));
				
				if(search.equals("")){
					this.pd.setSearchDescription("");
					lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_ALLEGATI, null));
				}else{
					lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_ALLEGATI,
							AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ALLEGATI_LIST, 
							new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId() + "")
							));
					lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
				}
			} else {
				if(gestioneFruitori) {
					lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORI, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				}
				else {
					lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				}
				
				if(search.equals("")){
					this.pd.setSearchDescription("");
					lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_ALLEGATI_DI + tmpTitle, null));
				}else{
					lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_ALLEGATI_DI + tmpTitle,
							AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ALLEGATI_LIST, 
							new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId() + "")
							));
					lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
				}
			}
			

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParam );

			// controllo eventuali risultati ricerca
			this.pd.setSearchLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_NOME_FILE);
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, AccordiServizioParteSpecificaCostanti.LABEL_APS_ALLEGATI, search);
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
					List<Parameter> listAllegatiChange = new ArrayList<>();
					listAllegatiChange.add(pIdAllegato);
					listAllegatiChange.add(pId);
					listAllegatiChange.add(pNomeDoc);
					if(gestioneFruitori) {
						listAllegatiChange.add(pTipoSoggettoFruitore);
						listAllegatiChange.add(pNomeSoggettoFruitore);
					}
					if(pModificaAPI!=null) {
						listAllegatiChange.add(pModificaAPI);
					}
					de.setUrl(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ALLEGATI_CHANGE,
							listAllegatiChange.toArray(new Parameter[1]));
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
					if(this.core.isShowAllegati()) {
						de.setUrl(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ALLEGATI_VIEW,
								pIdAllegato,pId,pNomeDoc);
						de.setValue(Costanti.LABEL_VISUALIZZA);
					}
					else {
						Parameter pIdAccordo = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_ACCORDO, asps.getId() + "");
						Parameter pTipoDoc = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_DOCUMENTO, "asps");
						de.setUrl(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT,
								pIdAllegato,pId,pNomeDoc,pIdAccordo,pTipoDoc);
						de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_APS_DOWNLOAD.toLowerCase());
						de.setDisabilitaAjaxStatus();
					}
					e.addElement(de);

					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);

			if(this.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(asps.getStatoPackage())){
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

			User user = ServletUtils.getUserFromSession(this.session);

			Boolean isAccordiCooperazione = user.getPermessi().isAccordiCooperazione();
			Boolean isServizi = user.getPermessi().isServizi();

			String tipologia = ServletUtils.getObjectFromSession(this.session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			
			boolean showProtocolli = this.core.countProtocolli(this.session)>1;
			boolean showServiceBinding = true;
			if( !showProtocolli ) {
				List<String> l = this.core.getProtocolli(this.session);
				if(l.size()>0) {
					IProtocolFactory<?> p = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(l.get(0));
					if(p.getManifest().getBinding().getRest()==null || p.getManifest().getBinding().getSoap()==null) {
						showServiceBinding = false;
					}
				}
			}
			
			int idLista = Liste.SERVIZI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			addFilterProtocol(ricerca, idLista);
			
			if(showServiceBinding) {
				String filterTipoAccordo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_SERVICE_BINDING);
				this.addFilterServiceBinding(filterTipoAccordo,false,true);
			}
			
			if(this.isShowGestioneWorkflowStatoDocumenti()){
				if(this.core.isGestioneWorkflowStatoDocumenti_visualizzaStatoLista()) {
					String filterStatoAccordo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_STATO_ACCORDO);
					this.addFilterStatoAccordo(filterStatoAccordo,false);
				}
			}
			
			boolean showConfigurazionePA = false;
			boolean showConfigurazionePD = false;
			boolean showSoggettoFruitore = false;
			boolean showSoggettoErogatore = false;
			boolean showFruitori = false;
			boolean showConnettorePA = false;
			boolean showConnettorePD = false;
			boolean gestioneFruitori = false;
			
			if(tipologia==null) {
				String filterDominio = null;
				if(this.core.isGestionePddAbilitata(this)==false) {
					filterDominio = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_DOMINIO);
					this.addFilterDominio(filterDominio, false);
				}
				PddTipologia pddTipologiaFilter = null;
				if(filterDominio!=null && !"".equals(filterDominio)) {
					pddTipologiaFilter = PddTipologia.toPddTipologia(filterDominio);
				}
				
				showConfigurazionePA = pddTipologiaFilter==null || !PddTipologia.ESTERNO.equals(pddTipologiaFilter);
				if(showConfigurazionePA) {
					showConnettorePA = true; // altrimenti non vi e' un modo per modificarlo come invece succede per la fruizione nel caso multitenant
				}
				showFruitori = pddTipologiaFilter==null || this.isModalitaCompleta() || PddTipologia.ESTERNO.equals(pddTipologiaFilter);
				showSoggettoErogatore = true;
			}
			else {
				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia)) {
					showConfigurazionePA = true;
					showSoggettoErogatore = false; // si e' deciso che non si fa vedere essendo solo uno
					showConnettorePA = true;
				}
				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
					gestioneFruitori = true;
					showConfigurazionePD = true;
					//showSoggettoFruitore = true;
					showSoggettoFruitore = false; // si e' deciso che non si fa vedere essendo solo uno
					showSoggettoErogatore = true;
					showConnettorePD = true;
				}
			}
			
			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
			// setto la barra del titolo
			List<Parameter> lstParm = new ArrayList<Parameter>();

			if(showConfigurazionePD) {
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORI, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
			}
			else {
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
			}
//			lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
//					AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
			this.pd.setSearchLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_RICERCA_SERVIZIO_SOGGETTO);
			if(search.equals("")){
				this.pd.setSearchDescription("");
			}else{
				lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParm );

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, AccordiServizioParteSpecificaCostanti.LABEL_APS, search);
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

			@SuppressWarnings("unused")
			String correlatoLabel = AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_RUOLO;

			String fruitoriLabel = AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORI;

			
			List<AccordoServizioParteComuneSintetico> listApc = new ArrayList<AccordoServizioParteComuneSintetico>();
			List<String> protocolli = new ArrayList<String>();
			
			boolean showRuoli = false;
			for (AccordoServizioParteSpecifica asps : lista) {
				AccordoServizioParteComuneSintetico apc = this.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
				ServiceBinding serviceBinding = this.apcCore.toMessageServiceBinding(apc.getServiceBinding());
				String tipoSoggetto = asps.getTipoSoggettoErogatore();
				String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoSoggetto);
				showRuoli = showRuoli ||  this.core.isProfiloDiCollaborazioneAsincronoSupportatoDalProtocollo(protocollo,serviceBinding);
				
				listApc.add(apc);
				protocolli.add(protocollo);
			}
						
			// controllo visualizzazione colonna ruolo
			List<String> listaLabelTabella = new ArrayList<String>();

			listaLabelTabella.add(AccordiServizioParteSpecificaCostanti.LABEL_APS_SERVIZIO);
			if(showSoggettoErogatore) {
				listaLabelTabella.add(AccordiServizioParteSpecificaCostanti.LABEL_APS_SOGGETTO_EROGATORE);
			}
			if(showSoggettoFruitore) {
				listaLabelTabella.add(AccordiServizioParteSpecificaCostanti.LABEL_APS_SOGGETTO_FRUITORE);
			}
			if( showProtocolli ) {
				listaLabelTabella.add(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_PROTOCOLLO_COMPACT);
			}
			listaLabelTabella.add(asLabel);
			
			// serviceBinding
			if(showServiceBinding) {
				listaLabelTabella.add(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_SERVICE_BINDING);
			}

			if(showConfigurazionePA) {
				listaLabelTabella.add(AccordiServizioParteSpecificaCostanti.LABEL_APS_DATI_INVOCAZIONE);
				if(showConnettorePA) {
					listaLabelTabella.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORE);
					listaLabelTabella.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VERIFICA_CONNETTORE);
					listaLabelTabella.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_CONNETTORI_MULTIPLI);
					listaLabelTabella.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_ELENCO_CONNETTORI_MULTIPLI);
				}
				listaLabelTabella.add(AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_APPLICATIVE);
			}
			
			if(showConfigurazionePD) {
				listaLabelTabella.add(AccordiServizioParteSpecificaCostanti.LABEL_APS_DATI_INVOCAZIONE);
				if(showConnettorePD) {
					listaLabelTabella.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE);
				}
				listaLabelTabella.add(AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_DELEGATE);
			}
			
			if(showRuoli) {
				//listaLabelTabella.add(correlatoLabel);
			}
			if(this.isShowGestioneWorkflowStatoDocumenti()) {
				if(this.core.isGestioneWorkflowStatoDocumenti_visualizzaStatoLista()) {
					listaLabelTabella.add(AccordiServizioParteSpecificaCostanti.LABEL_APS_STATO);
				}
			}
			if(showFruitori) {
				listaLabelTabella.add(fruitoriLabel);
			}
			if(!this.isModalitaStandard()) {
				listaLabelTabella.add(AccordiServizioParteSpecificaCostanti.LABEL_APS_ALLEGATI);
			}

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

			for (int i = 0; i < lista.size(); i++) {
				AccordoServizioParteSpecifica asps = lista.get(i);

				Fruitore fruitore = null;
				if(showConfigurazionePD) {
					fruitore = asps.getFruitore(0); // NOTA: il metodo 'soggettiServizioList' ritorna un unico fruitore in caso di gestioneFruitori abiltata per ogni entry. Crea cioè un accordo con fruitore per ogni fruitore esistente
				}
				
				String protocollo = protocolli.get(i);
				
				Vector<DataElement> e = new Vector<DataElement>();

				Parameter pIdsoggErogatore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+asps.getIdSoggetto());
				Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, asps.getNome());
				Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, asps.getTipo());
				@SuppressWarnings("unused")
				Parameter pVersioneServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VERSIONE, asps.getVersione().intValue()+"");

				String uriASPS = this.idServizioFactory.getUriFromAccordo(asps);
				
				Soggetto sog = this.soggettiCore.getSoggettoRegistro(asps.getIdSoggetto());
				boolean isPddEsterna = this.pddCore.isPddEsterna(sog.getPortaDominio());
				
				DataElement de = new DataElement();
				de.setUrl(
					AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE,
					new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId() + ""),
					pNomeServizio, pTipoServizio, pIdsoggErogatore);
				de.setValue(this.getLabelNomeServizio(protocollo, asps.getTipo(), asps.getNome(), asps.getVersione()));
				de.setIdToRemove(uriASPS);
				e.addElement(de);

						
				if(showSoggettoErogatore) {
					de = new DataElement();
					de.setUrl(
							SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE, 
							new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID, asps.getIdSoggetto() + ""),
							new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO, asps.getTipoSoggettoErogatore()),
							new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME, asps.getNomeSoggettoErogatore()));
					de.setValue(this.getLabelNomeSoggetto(protocollo, asps.getTipoSoggettoErogatore() , asps.getNomeSoggettoErogatore()));
					e.addElement(de);
				}
				
				if(showSoggettoFruitore) {
					de = new DataElement();
					de.setUrl(
							SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE, 
							new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID, fruitore.getIdSoggetto() + ""),
							new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO, fruitore.getTipo()),
							new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME, fruitore.getNome()));
					de.setValue(this.getLabelNomeSoggetto(protocollo, fruitore.getTipo() , fruitore.getNome()));
					e.addElement(de);
				}
				
				if(showProtocolli) {
					de = new DataElement();
					de.setValue(this.getLabelProtocollo(protocollo));
					e.addElement(de);
				}
				
				de = new DataElement();
				AccordoServizioParteComuneSintetico apc = listApc.get(i);
				ServiceBinding serviceBinding = this.apcCore.toMessageServiceBinding(apc.getServiceBinding());

				Parameter pTipoAccordo = AccordiServizioParteComuneUtilities.getParametroAccordoServizio(apc);

				de.setUrl(
						AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_CHANGE, 
						new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getIdAccordo() + ""),
						new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME, apc.getNome()), pTipoAccordo);
				de.setValue(this.getLabelIdAccordo(apc));
				e.addElement(de);
				
				if(showServiceBinding) {
					de = new DataElement();
					switch (serviceBinding) {
					case REST:
						de.setValue(CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_REST);
						break;
					case SOAP:
					default:
						de.setValue(CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_SOAP);
						break;
					}
					e.addElement(de);
				}
				
				// colonna configurazioni
				if(showConfigurazionePA) {
					
					// Utilizza la configurazione come parent
					ServletUtils.setObjectIntoSession(this.session, PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE, PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT);
										
					IDServizio idServizio = this.idServizioFactory.getIDServizioFromAccordo(asps);
					
					Parameter paIdSogg = null;
					Parameter paNomePorta = null;
					Parameter paIdPorta = null;
					Parameter paIdAsps = null;
					Parameter paConfigurazioneDati = null;
					Parameter paIdProvider = null;
					Parameter paIdPortaPerSA = null;
					Parameter paConnettoreDaListaAPS = null;
					IDPortaApplicativa idPA = null;
					PortaApplicativa paDefault = null;
					PortaApplicativaServizioApplicativo paSADefault = null;
					boolean connettoreMultiploEnabled = false;
					boolean checkConnettore = false;
					boolean visualizzaConnettore = true;
					long idConnettore = 1;
					if(!isPddEsterna){
						idPA = this.porteApplicativeCore.getIDPortaApplicativaAssociataDefault(idServizio);
						if(idPA!=null) {
							paDefault = this.porteApplicativeCore.getPortaApplicativa(idPA);
						}
						paIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, asps.getIdSoggetto() + "");
						paIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, asps.getId()+ "");
						paConfigurazioneDati = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_DATI_INVOCAZIONE, Costanti.CHECK_BOX_ENABLED_TRUE);
						paConnettoreDaListaAPS = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS, Costanti.CHECK_BOX_ENABLED_TRUE);
						if(paDefault!=null) {
							paSADefault = paDefault.getServizioApplicativoList().get(0);
							paNomePorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA, paDefault.getNome());
							paIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, ""+paDefault.getId());
							paIdProvider = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_PROVIDER, paDefault.getIdSoggetto() + "");
							paIdPortaPerSA = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_PORTA, ""+paDefault.getId());
						}
						
						List<MappingErogazionePortaApplicativa> listaMappingErogazionePortaApplicativa = this.apsCore.mappingServiziPorteAppList(idServizio,asps.getId(), null);
						List<PortaApplicativa> listaPorteApplicativeAssociate = new ArrayList<>();
						for(MappingErogazionePortaApplicativa mappinErogazione : listaMappingErogazionePortaApplicativa) {
							listaPorteApplicativeAssociate.add(this.porteApplicativeCore.getPortaApplicativa(mappinErogazione.getIdPortaApplicativa()));
						}
						
						for (int z= 0; z < listaPorteApplicativeAssociate.size(); z++) {
							PortaApplicativa paAssociata = listaPorteApplicativeAssociate.get(z);
							MappingErogazionePortaApplicativa mapping = listaMappingErogazionePortaApplicativa.get(z);
							
							if(!mapping.isDefault()) {
								PortaApplicativaServizioApplicativo portaApplicativaAssociataServizioApplicativo = paAssociata.getServizioApplicativoList().get(0);
								boolean connettoreConfigurazioneRidefinito = this.isConnettoreRidefinito(paDefault, paSADefault, paAssociata, portaApplicativaAssociataServizioApplicativo);
								if(connettoreConfigurazioneRidefinito) {
									visualizzaConnettore = false;
									break;
								}
							}
							
						}
						
						if(visualizzaConnettore) {
							if(paDefault!=null) {
								connettoreMultiploEnabled = paDefault.getBehaviour() != null;
								PortaApplicativaServizioApplicativo paDefautServizioApplicativo = paDefault.getServizioApplicativoList().get(0);
								IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
								idServizioApplicativo.setIdSoggettoProprietario(new IDSoggetto(paDefault.getTipoSoggettoProprietario(), paDefault.getNomeSoggettoProprietario()));
								idServizioApplicativo.setNome(paDefautServizioApplicativo.getNome());
								ServizioApplicativo sa = this.saCore.getServizioApplicativo(idServizioApplicativo);
								org.openspcoop2.core.config.Connettore connettore = sa.getInvocazioneServizio().getConnettore();
								idConnettore = connettore.getId();
								checkConnettore = org.openspcoop2.pdd.core.connettori.ConnettoreCheck.checkSupported(connettore);
							}
						}
					}
					
					
					// url invocazione
					de = new DataElement();
					if(isPddEsterna || this.isModalitaCompleta()){
						de.setType(DataElementType.TEXT);
						de.setValue("-");
					}
					else{					
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CHANGE,paIdSogg, paNomePorta, paIdPorta,paIdAsps,paConfigurazioneDati);
						ServletUtils.setDataElementVisualizzaLabel(de);						
					}
					e.addElement(de);
					
					
					// connettore
					if(showConnettorePA) {
						de = new DataElement();
						if(isPddEsterna || this.isModalitaCompleta()){
							de.setType(DataElementType.TEXT);
							de.setValue("-");
						}
						else{	
							
							boolean visualizzaLinkConfigurazioneConnettore = 
									visualizzaConnettore && (!this.core.isConnettoriMultipliEnabled() || ( this.core.isConnettoriMultipliEnabled() && !connettoreMultiploEnabled ));
							if(visualizzaLinkConfigurazioneConnettore) {
								PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo = paDefault.getServizioApplicativoList().get(0);
								//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
								de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT, paIdProvider, paIdPortaPerSA, paIdAsps,
										new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME_SERVIZIO_APPLICATIVO, portaApplicativaServizioApplicativo.getNome()),
										new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO, portaApplicativaServizioApplicativo.getIdServizioApplicativo()+""),
										paConnettoreDaListaAPS);
								ServletUtils.setDataElementVisualizzaLabel(de);
							} else {
								de.setType(DataElementType.TEXT);
								de.setValue("-");
							}
						}
						e.addElement(de);
						
						// verifica
						de = new DataElement();
						Parameter pIdConnettore = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ID, idConnettore+"");
						Parameter pConnettoreAccessoDaGruppi = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI, "false");
						Parameter pConnettoreVerificaAccesso = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO, "false");
						if(isPddEsterna || this.isModalitaCompleta()){
							de.setType(DataElementType.TEXT);
							de.setValue("-");
						}
						else{
							boolean visualizzaLinkCheckConnettore =
									visualizzaConnettore && checkConnettore && (!this.core.isConnettoriMultipliEnabled() || ( this.core.isConnettoriMultipliEnabled() && !connettoreMultiploEnabled ));
							if(visualizzaLinkCheckConnettore) {
								List<Parameter> listParametersVerificaConnettore = new ArrayList<>();
								paIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, asps.getIdSoggetto() + "");
								listParametersVerificaConnettore.add(paIdSogg);
								listParametersVerificaConnettore.add(paIdPorta);
								listParametersVerificaConnettore.add(paIdAsps);
								listParametersVerificaConnettore.add(paConnettoreDaListaAPS);
								listParametersVerificaConnettore.add(pIdConnettore);
								listParametersVerificaConnettore.add(pConnettoreAccessoDaGruppi);
								listParametersVerificaConnettore.add(pConnettoreVerificaAccesso);
								
								de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VERIFICA_CONNETTORE, 
										listParametersVerificaConnettore.toArray(new Parameter[1]));
								ServletUtils.setDataElementVisualizzaLabel(de);
							} else {
								de.setType(DataElementType.TEXT);
								de.setValue("-");
							}
							
						}
						e.addElement(de);
						
						// configura connettori multipli
						de = new DataElement();
						if(isPddEsterna || this.isModalitaCompleta()){
							de.setType(DataElementType.TEXT);
							de.setValue("-");
						}
						else{
							if(visualizzaConnettore && this.core.isConnettoriMultipliEnabled()) {
								List<Parameter> listParametersConfigutazioneConnettoriMultipli = new ArrayList<>();
								paIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, asps.getIdSoggetto() + "");
								listParametersConfigutazioneConnettoriMultipli.add(paIdSogg);
								listParametersConfigutazioneConnettoriMultipli.add(paIdPorta);
								listParametersConfigutazioneConnettoriMultipli.add(paIdAsps);
								listParametersConfigutazioneConnettoriMultipli.add(paConnettoreDaListaAPS);
								listParametersConfigutazioneConnettoriMultipli.add(pIdConnettore);
								listParametersConfigutazioneConnettoriMultipli.add(pConnettoreAccessoDaGruppi);
								listParametersConfigutazioneConnettoriMultipli.add(pConnettoreVerificaAccesso);
								
								de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONFIGURAZIONE_CONNETTORI_MULTIPLI, 
										listParametersConfigutazioneConnettoriMultipli.toArray(new Parameter[1]));
								de.setValue(this.getStatoConnettoriMultipliPortaApplicativa(paDefault));
								
							} else {
								de.setType(DataElementType.TEXT);
								de.setValue("-");
							}
						}
						e.addElement(de);
						
						// lista  connettori multipli
						de = new DataElement();
						if(isPddEsterna || this.isModalitaCompleta()){
							de.setType(DataElementType.TEXT);
							de.setValue("-");
						}
						else{
							if(visualizzaConnettore && this.core.isConnettoriMultipliEnabled() && connettoreMultiploEnabled) {
								List<Parameter> listParametersConfigutazioneConnettoriMultipli = new ArrayList<>();
								paIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, asps.getIdSoggetto() + "");
								listParametersConfigutazioneConnettoriMultipli.add(paIdSogg);
								listParametersConfigutazioneConnettoriMultipli.add(paIdPorta);
								listParametersConfigutazioneConnettoriMultipli.add(paIdAsps);
								listParametersConfigutazioneConnettoriMultipli.add(paConnettoreDaListaAPS);
								listParametersConfigutazioneConnettoriMultipli.add(pIdConnettore);
								listParametersConfigutazioneConnettoriMultipli.add(pConnettoreAccessoDaGruppi);
								listParametersConfigutazioneConnettoriMultipli.add(pConnettoreVerificaAccesso);
								listParametersConfigutazioneConnettoriMultipli.add(new Parameter(CostantiControlStation.PARAMETRO_ID_CONN_TAB, "0"));
								
								de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_LIST, 
										listParametersConfigutazioneConnettoriMultipli.toArray(new Parameter[1]));
								
								if(contaListe)
									ServletUtils.setDataElementVisualizzaLabel(de, (long) paDefault.sizeServizioApplicativoList());
								else 
									ServletUtils.setDataElementVisualizzaLabel(de);
									
							}else {
								de.setType(DataElementType.TEXT);
								de.setValue("-");
							}
						}
						e.addElement(de);
					}
					
					// configurazione
					de = new DataElement();
					if(isPddEsterna){
						de.setType(DataElementType.TEXT);
						de.setValue("-");
					}
					else{
						de.setUrl(
								AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_PORTE_APPLICATIVE_LIST,
								new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId() + ""),
								pNomeServizio, pTipoServizio, pIdsoggErogatore,
								new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_GRUPPI,"true"),
								new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_CONFIGURAZIONI,"true"));
						// +"&nomeprov="+soggErogatore+"&tipoprov="+tipoSoggEr);
						if (contaListe) {
							// BugFix OP-674
							//List<PortaApplicativa> lista1 = this.apsCore.serviziPorteAppList(servizio.getTipo(),servizio.getNome(),asps.getVersione(),
							//		asps.getId().intValue(), asps.getIdSoggetto(), new Search(true));
							Search searchForCount = new Search(true,1);
							this.apsCore.mappingServiziPorteAppList(idServizio, asps.getId(), searchForCount);
							//int numPA = lista1.size();
							int numPA = searchForCount.getNumEntries(Liste.CONFIGURAZIONE_EROGAZIONE);
							ServletUtils.setDataElementVisualizzaLabel(de, (long) numPA );
						} else
							ServletUtils.setDataElementVisualizzaLabel(de);
					}
					e.addElement(de);
					
				}
				
				if(showConfigurazionePD) {
					
					// Utilizza la configurazione come parent
					ServletUtils.setObjectIntoSession(this.session, PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE, PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT);
										
					IDServizio idServizio = this.idServizioFactory.getIDServizioFromAccordo(asps);
					IDSoggetto idFruitore = new IDSoggetto(fruitore.getTipo(), fruitore.getNome());
					
					IDPortaDelegata idPD = this.porteDelegateCore.getIDPortaDelegataAssociataDefault(idServizio, idFruitore);
					PortaDelegata pdDefault = this.porteDelegateCore.getPortaDelegata(idPD);
						
					Parameter pIdPD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, "" + pdDefault.getId());
					Parameter pNomePD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA, pdDefault.getNome());
					Parameter pIdSoggPD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, pdDefault.getIdSoggetto() + "");
					Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, asps.getId()+"");
					Parameter pId = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId()+"");
					Parameter pIdSoggettoErogatore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, asps.getIdSoggetto()+"");
					Parameter pIdFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID, fruitore.getId()+ "");
					Parameter pConfigurazioneDati = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_CONFIGURAZIONE_DATI_INVOCAZIONE, Costanti.CHECK_BOX_ENABLED_TRUE);
					Parameter pConnettoreDaListaAPS = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_CONNETTORE_DA_LISTA_APS, Costanti.CHECK_BOX_ENABLED_TRUE);
										
					Long idSoggettoLong = fruitore.getIdSoggetto();
					if(idSoggettoLong==null) {
						idSoggettoLong = this.soggettiCore.getIdSoggetto(fruitore.getNome(), fruitore.getTipo());
					}
					Parameter pIdProviderFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE, idSoggettoLong + "");
					Parameter pIdSogg = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO, idSoggettoLong + "");
					
					// url invocazione
					de = new DataElement();				
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CHANGE,pIdPD,pNomePD,pIdSoggPD, pIdAsps, pIdFruitore, pConfigurazioneDati);
					ServletUtils.setDataElementVisualizzaLabel(de);						
					e.addElement(de);				
					
					// connettore	
					if(showConnettorePD) {
						
						// Controllo se richiedere il connettore
						boolean connettoreStatic = false;
						if(gestioneFruitori) {
							connettoreStatic = this.apsCore.isConnettoreStatic(protocollo);
						}
						
						de = new DataElement();
						if(!connettoreStatic) {
							List<Parameter> listParameter = new ArrayList<>();
							listParameter.add(pId);
							listParameter.add(pIdFruitore);
							listParameter.add(pIdSoggettoErogatore);
							listParameter.add(pIdProviderFruitore);
							listParameter.add(pConnettoreDaListaAPS);
							de.setUrl(
									AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE,
									listParameter.toArray(new Parameter[1])
									);
							ServletUtils.setDataElementVisualizzaLabel(de);
						}
						else {
							de.setValue("-");
						}
						e.addElement(de);
					}
					
					// configurazione
					de = new DataElement();
					de.setUrl(
							AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_PORTE_DELEGATE_LIST,
							pId, pIdSogg, pIdSoggettoErogatore, pNomeServizio, pTipoServizio, pIdFruitore,
							new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_GRUPPI,"true"),
							new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_CONFIGURAZIONI,"true"));
					if (contaListe) {
						// BugFix OP-674
//								List<PortaDelegata> fruLista = this.apsCore.serviziFruitoriPorteDelegateList(this.soggettiCore.getIdSoggetto(fru.getNome(), fru.getTipo()), 
//										serv.getTipo(), serv.getNome(), asps.getId(), 
//										serv.getTipoSoggettoErogatore(), serv.getNomeSoggettoErogatore(), asps.getIdSoggetto(), 
//										ricerca);
						Search searchForCount = new Search(true,1);
						IDServizio idServizioFromAccordo = this.idServizioFactory.getIDServizioFromAccordo(asps); 
						//long idSoggetto = this.soggettiCore.getIdSoggetto(fru.getNome(), fru.getTipo());
						this.apsCore.serviziFruitoriMappingList(fruitore.getId(), idFruitore , idServizioFromAccordo, searchForCount);
						//int numPD = fruLista.size();
						int numPD = searchForCount.getNumEntries(Liste.CONFIGURAZIONE_FRUIZIONE);
						ServletUtils.setDataElementVisualizzaLabel(de, (long) numPD );
					} else
						ServletUtils.setDataElementVisualizzaLabel(de);
					e.addElement(de);
				}
				
				// Colonna ruoli
//				if(showRuoli){
//					de = new DataElement();
//					if(this.core.isProfiloDiCollaborazioneAsincronoSupportatoDalProtocollo(protocollo,serviceBinding)){ 
//						de.setValue((TipologiaServizio.CORRELATO.equals(asps.getTipologiaServizio()) ?
//								AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_CORRELATO :
//									AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_NORMALE));
//	
//					}else {
//						de.setValue("-");
//					}
//					e.addElement(de);
//				}

				if(this.isShowGestioneWorkflowStatoDocumenti()){
					if(this.core.isGestioneWorkflowStatoDocumenti_visualizzaStatoLista()) {
						de = new DataElement();
						de.setValue(StatiAccordo.upper(asps.getStatoPackage()));
						e.addElement(de);
					}
				}

				if(showFruitori) {
					de = new DataElement();
					//if(!user.isPermitMultiTenant() && !isPddEsterna) {
					if(!isPddEsterna) {
						de.setValue("-");
					}
					else {
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

				if(!this.isModalitaStandard()) {
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
				}

				dati.addElement(e);
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

			// preparo bottoni
			// String gestioneWSBL = (String) this.session
			// .getAttribute("GestioneWSBL");
			if(lista!=null && lista.size()>0){
				if (this.core.isShowPulsantiImportExport()) {

					ExporterUtils exporterUtils = new ExporterUtils(this.archiviCore);
					if(exporterUtils.existsAtLeastOneExportMpde(ArchiveType.ACCORDO_SERVIZIO_PARTE_SPECIFICA, this.session)){

						Vector<AreaBottoni> bottoni = new Vector<AreaBottoni>();

						AreaBottoni ab = new AreaBottoni();
						Vector<DataElement> otherbott = new Vector<DataElement>();
						DataElement de = new DataElement();
						de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_APS_ESPORTA_SELEZIONATI);
						de.setOnClick(AccordiServizioParteSpecificaCostanti.LABEL_APS_ESPORTA_SELEZIONATI_ONCLICK);
						de.setDisabilitaAjaxStatus();
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
			
			ServletUtils.addListElementIntoSession(this.session, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI, 
					new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, id));

			int idLista = Liste.SERVIZI_FRUITORI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			if(this.isShowGestioneWorkflowStatoDocumenti()){
				if(this.core.isGestioneWorkflowStatoDocumenti_visualizzaStatoLista()) {
					String filterStatoAccordo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_STATO_ACCORDO);
					this.addFilterStatoAccordo(filterStatoAccordo,false);
				}
			}
			
			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			String idSoggettoErogatoreDelServizio = this.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE);
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
			
			String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(asps.getTipoSoggettoErogatore());
			
			// Prendo il nome e il tipo del soggetto erogatore del servizio
			//Soggetto sogg = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(idSoggettoErogatoreDelServizio));

			String tmpTitle = this.getLabelIdServizio(asps);

			// setto la barra del titolo
			List<Parameter> lstParm = new ArrayList<Parameter>();

			lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));

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
			this.pd.setSearchLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SOGGETTO);
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORI, search);
			}

			// setto le label delle colonne
			//User user = ServletUtils.getUserFromSession(this.session);
			String labelFruitore = AccordiServizioParteSpecificaCostanti.LABEL_APS_SOGGETTO;
			//if(this.core.isTerminologiaSICA_RegistroServizi()){
			//	labelFruitore = "Adesione";
			//}
			boolean showPoliticheSLA = false;
			
			List<String> listaLabelTabella = new ArrayList<String>();
			listaLabelTabella.add(labelFruitore);
			if(this.isShowGestioneWorkflowStatoDocumenti() && this.core.isGestioneWorkflowStatoDocumenti_visualizzaStatoLista()){
				listaLabelTabella.add(AccordiServizioParteSpecificaCostanti.LABEL_APS_STATO);
			}
			listaLabelTabella.add(AccordiServizioParteSpecificaCostanti.LABEL_APS_DATI_INVOCAZIONE);
			listaLabelTabella.add(AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_DELEGATE);
			
			this.pd.setLabels(listaLabelTabella.toArray(new String[1]));

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

				de.setValue(this.getLabelNomeSoggetto(protocollo, fru.getTipo() , fru.getNome()));
				de.setIdToRemove(fru.getId().toString());
				e.addElement(de);

				if(this.isShowGestioneWorkflowStatoDocumenti()){
					if(this.core.isGestioneWorkflowStatoDocumenti_visualizzaStatoLista()) {
						de = new DataElement();
						de.setValue(StatiAccordo.upper(fru.getStatoPackage()));
						e.addElement(de);
					}
				}

				Parameter pIdSogg = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO, fru.getIdSoggetto() + "");
				// devo aggiungere le politiche di sicurezza come in
				// accordiServizioApplicativoList
										
				IDSoggetto fruitore = new IDSoggetto(fru.getTipo(), fru.getNome());
				Soggetto fruitoreSogg = this.soggettiCore.getSoggettoRegistro(fruitore);
				
				boolean esterno = this.pddCore.isPddEsterna(fruitoreSogg.getPortaDominio());

				// Elimino eventuale configurazione messa come parent (tanto viene re-inserita se entro dentro la lista della configurazione)
				ServletUtils.removeObjectFromSession(this.session, PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT);
									
				IDServizio idServizio = this.idServizioFactory.getIDServizioFromAccordo(asps);
				
				Parameter pIdPD = null;
				Parameter pNomePD = null;
				Parameter pIdSoggPD = null;
				Parameter pIdAsps = null;
				Parameter pIdFruitore = null;
				Parameter pConfigurazioneDati = null;
				if(!esterno){
					IDPortaDelegata idPD = this.porteDelegateCore.getIDPortaDelegataAssociataDefault(idServizio, fruitore);
					PortaDelegata pdDefault = this.porteDelegateCore.getPortaDelegata(idPD);
					
					pIdPD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, "" + pdDefault.getId());
					pNomePD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA, pdDefault.getNome());
					pIdSoggPD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, pdDefault.getIdSoggetto() + "");
					pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, asps.getId()+"");
					pIdFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID, fru.getId()+ "");
					pConfigurazioneDati = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_CONFIGURAZIONE_DATI_INVOCAZIONE, Costanti.CHECK_BOX_ENABLED_TRUE);
				}

				de = new DataElement();
				if(esterno){
					de.setType(DataElementType.TEXT);
					de.setValue("-");
				}
				else{					
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CHANGE,pIdPD,pNomePD,pIdSoggPD, pIdAsps, pIdFruitore, pConfigurazioneDati);
					ServletUtils.setDataElementVisualizzaLabel(de);						
				}
				e.addElement(de);				
				
				de = new DataElement();
				if(esterno){
					de.setValue("-");
				}
				else{
					de.setUrl(
							AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_PORTE_DELEGATE_LIST,
							pId, pIdSogg, pIdSoggettoErogatore, pNomeServizio, pTipoServizio, pMyId,
							new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_GRUPPI,"true"),
							new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_CONFIGURAZIONI,"true"));
					if (contaListe) {
						// BugFix OP-674
//							List<PortaDelegata> fruLista = this.apsCore.serviziFruitoriPorteDelegateList(this.soggettiCore.getIdSoggetto(fru.getNome(), fru.getTipo()), 
//									serv.getTipo(), serv.getNome(), asps.getId(), 
//									serv.getTipoSoggettoErogatore(), serv.getNomeSoggettoErogatore(), asps.getIdSoggetto(), 
//									ricerca);
						Search searchForCount = new Search(true,1);
						IDServizio idServizioFromAccordo = this.idServizioFactory.getIDServizioFromAccordo(asps); 
						//long idSoggetto = this.soggettiCore.getIdSoggetto(fru.getNome(), fru.getTipo());
						IDSoggetto idSoggettoFruitore = new IDSoggetto(fru.getNome(), fru.getTipo());
						this.apsCore.serviziFruitoriMappingList(fru.getId(), idSoggettoFruitore , idServizioFromAccordo, searchForCount);
						//int numPD = fruLista.size();
						int numPD = searchForCount.getNumEntries(Liste.CONFIGURAZIONE_FRUIZIONE);
						ServletUtils.setDataElementVisualizzaLabel(de, (long) numPD );
					} else
						ServletUtils.setDataElementVisualizzaLabel(de);
				}
				e.addElement(de);
				
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
				de.setValue("Esporta");
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

	public void prepareServiziConfigurazioneList(List<MappingErogazionePortaApplicativa> listaParam, String id, String idSoggettoErogatoreDelServizio, ISearch ricerca)
			throws Exception {
		try {
			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

			IExtendedListServlet extendedServletList = this.core.getExtendedServletPortaApplicativa();
			
			ServletUtils.addListElementIntoSession(this.session, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE,
					new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, id),
					new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, idSoggettoErogatoreDelServizio));

			String tipologia = ServletUtils.getObjectFromSession(this.session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			@SuppressWarnings("unused")
			boolean gestioneErogatori = false;
			if(tipologia!=null) {
				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia)) {
					gestioneErogatori = true;
				}
			}
			
			boolean gestioneGruppi = true;
			String paramGestioneGruppi = ServletUtils.getObjectFromSession(this.session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_GRUPPI);
			if(paramGestioneGruppi!=null && !"".equals(paramGestioneGruppi)) {
				gestioneGruppi = Boolean.valueOf(paramGestioneGruppi);
			}
			
			boolean gestioneConfigurazioni = true;
			String paramGestioneConfigurazioni = ServletUtils.getObjectFromSession(this.session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_CONFIGURAZIONI);
			if(paramGestioneConfigurazioni!=null && !"".equals(paramGestioneConfigurazioni)) {
				gestioneConfigurazioni = Boolean.valueOf(paramGestioneConfigurazioni);
			}
			
			boolean visualizzazioneTabs = !this.isModalitaCompleta();
			if(visualizzazioneTabs) {
				gestioneGruppi = false;
				this.pd.setCustomListViewName(ErogazioniCostanti.ASPS_EROGAZIONI_NOME_VISTA_CUSTOM_CONFIGURAZIONE);
			}
			
			// Prendo il nome e il tipo del servizio
			AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(id));
			AccordoServizioParteComuneSintetico apc = this.apcCore.getAccordoServizioSintetico(asps.getIdAccordo()); 
			org.openspcoop2.core.registry.constants.ServiceBinding serviceBinding = apc.getServiceBinding();
			ServiceBinding serviceBindingMessage = this.apcCore.toMessageServiceBinding(apc.getServiceBinding());
			String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(asps.getTipoSoggettoErogatore());
			
			int idLista = Liste.CONFIGURAZIONE_EROGAZIONE;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			
			Map<String,String> azioni = this.porteApplicativeCore.getAzioniConLabel(asps, apc, false, true, new ArrayList<String>());
			String filtroAzione = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_AZIONE);
						
			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setSearch("");
			
			List<MappingErogazionePortaApplicativa> listaSenzaFiltro = this.impostaFiltroAzioneMappingErogazione(null, listaParam,ricerca, idLista);
			
			boolean showBottoneAggiungiNuovaConfigurazione = 
					(azioni!=null && azioni.size()>1) 
					||
					(listaSenzaFiltro.size()>1);
			if(showBottoneAggiungiNuovaConfigurazione) {
				// verifico modalita identificazione
				for (MappingErogazionePortaApplicativa mappingErogazionePortaApplicativa : listaParam) {
					if(mappingErogazionePortaApplicativa.isDefault()) {
						PortaApplicativa paDefaullt = this.porteApplicativeCore.getPortaApplicativa(mappingErogazionePortaApplicativa.getIdPortaApplicativa());
						if(paDefaullt.getAzione()!=null && 
								org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione.STATIC.equals(paDefaullt.getAzione().getIdentificazione())) {
							showBottoneAggiungiNuovaConfigurazione = false;
						}
						break;
					}
				}
			}
			
			List<MappingErogazionePortaApplicativa> lista = null;
			boolean chooseTabWithFilter = !this.isModalitaCompleta();
			if(chooseTabWithFilter) {
				lista = listaSenzaFiltro;
				if(lista.size()>1) {
					MappingErogazionePortaApplicativa mappingContenenteAzione = getFiltroAzioneMappingErogazione(filtroAzione, listaParam);
					if(mappingContenenteAzione!=null) {
						int tab = -1;
						for (int i = 0; i < lista.size(); i++) {
							if(lista.get(i).getNome().equals(mappingContenenteAzione.getNome())) {
								tab = i;
								break;
							}
						}
						if(tab>=0) {
							ServletUtils.setObjectIntoSession(this.session, tab+"", CostantiControlStation.PARAMETRO_ID_TAB);
						}
					}
					
					this.pd.setLabelBottoneFiltra(CostantiControlStation.LABEL_BOTTONE_INDIVIDUA_GRUPPO);
					//this.pd.setLabelBottoneRipulsci("Ripulisci Selezione");
				}
			}
			else {
				lista = this.impostaFiltroAzioneMappingErogazione(filtroAzione, listaParam,ricerca, idLista);
			}
			
			boolean allActionRedefined = false;
			List<String> actionNonRidefinite = null;
			if(listaSenzaFiltro.size()>1) {
				List<String> azioniL = new ArrayList<>();
				if(azioni != null && azioni.size() > 0)
					azioniL.addAll(azioni.keySet());
				allActionRedefined = this.allActionsRedefinedMappingErogazione(azioniL, listaSenzaFiltro);
				if(!allActionRedefined) {
					actionNonRidefinite = this.getAllActionsNotRedefinedMappingErogazione(azioniL, listaSenzaFiltro);
				}
			}
			

			if(!gestioneGruppi && listaParam.size()<=1) {
				ServletUtils.disabledPageDataSearch(this.pd);
			}
			else {
				this.addFilterAzione(azioni, filtroAzione, serviceBindingMessage);
			}
			
			// Utilizza la configurazione come parent
			ServletUtils.setObjectIntoSession(this.session, PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE, PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT);
			
			//questo e' il soggetto virtuale
			if ((idSoggettoErogatoreDelServizio == null) || idSoggettoErogatoreDelServizio.equals("")) {
				idSoggettoErogatoreDelServizio = this.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE);
			}
			
			if ((idSoggettoErogatoreDelServizio == null) || idSoggettoErogatoreDelServizio.equals("")) {
				PageData oldPD = ServletUtils.getPageDataFromSession(this.session);

				idSoggettoErogatoreDelServizio = oldPD.getHidden(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE);
			}

			// setto i dati come campi hidden nel pd per portarmeli dietro
			this.pd.addHidden(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, idSoggettoErogatoreDelServizio);

			String servizioTmpTile = this.getLabelIdServizio(asps);
			
			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			
			Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, this.session);
			String labelAzioni = this.getLabelAzioni(serviceBindingMessage); 
			if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
				lstParam.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_EROGAZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
				Parameter pIdServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId()+ "");
				Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, asps.getNome());
				Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, asps.getTipo());
				lstParam.add(new Parameter(servizioTmpTile, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_CHANGE, pIdServizio,pNomeServizio, pTipoServizio));
				String labelConfigurazione = gestioneConfigurazioni ? ErogazioniCostanti.LABEL_ASPS_GESTIONE_CONFIGURAZIONI : 
					(gestioneGruppi ? MessageFormat.format(ErogazioniCostanti.LABEL_ASPS_GESTIONE_GRUPPI_CON_PARAMETRO, labelAzioni) : AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_APPLICATIVE);
				
				lstParam.add(new Parameter(labelConfigurazione, null));
			} else {
				lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_CONFIGURAZIONI_DI + servizioTmpTile, null));
			}
			
			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParam );

			boolean visualizzaMTOM = true;
			boolean visualizzaSicurezza = true;
			boolean visualizzaCorrelazione = true;
			switch (serviceBinding) {
			case REST:
				visualizzaMTOM = false;
				visualizzaSicurezza = true;
				visualizzaCorrelazione = true;
				break;
			case SOAP:
			default:
				visualizzaMTOM = true;
				visualizzaSicurezza = true;
				visualizzaCorrelazione = true;
				break;
			}
			
			boolean showConnettoreLink = true;
			if(gestioneConfigurazioni && this.isModalitaStandard()) {
				showConnettoreLink = false;
				
				Iterator<MappingErogazionePortaApplicativa> it = lista.iterator();
				MappingErogazionePortaApplicativa mapping= null;
				while (it.hasNext()) {
					mapping = it.next();
					if(!mapping.isDefault()) {
						PortaApplicativa paAssociata = this.porteApplicativeCore.getPortaApplicativa(mapping.getIdPortaApplicativa());
						PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo = paAssociata.getServizioApplicativoList().get(0);
						if(portaApplicativaServizioApplicativo.getNome().equals(paAssociata.getNome())) { 
							showConnettoreLink = true;
							break;
						}
						// controllo che non sia un server
						if(this.saCore.isApplicativiServerEnabled(this)) {
							IDServizioApplicativo idSA = new IDServizioApplicativo();
							idSA.setIdSoggettoProprietario(new IDSoggetto(paAssociata.getTipoSoggettoProprietario(), paAssociata.getNomeSoggettoProprietario()));
							idSA.setNome(portaApplicativaServizioApplicativo.getNome());
							ServizioApplicativo sa = this.saCore.getServizioApplicativo(idSA);
							if(ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(sa.getTipo())) {
								showConnettoreLink = true;
								break;
							}
						}
						
					}
				}
			}
			else {
				// se non e' standard lo faccio vedere solo se ho più di un gruppo.
				showConnettoreLink = lista!=null && lista.size()>1;
			}
			
			Iterator<MappingErogazionePortaApplicativa> itDef = lista.iterator();
			MappingErogazionePortaApplicativa mappingDefault = null;
			PortaApplicativa paAssociataDefault = null; 
			PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativoDefault = null; 
			while (itDef.hasNext()) {
				mappingDefault = itDef.next();
				if(mappingDefault.isDefault()) {
					paAssociataDefault = this.porteApplicativeCore.getPortaApplicativa(mappingDefault.getIdPortaApplicativa());
					portaApplicativaServizioApplicativoDefault = paAssociataDefault.getServizioApplicativoList().get(0);
					break;
				}
			}
			
			@SuppressWarnings("unused")
			boolean isPaDefaultMulti = paAssociataDefault.getBehaviour() != null;
			
			// setto le label delle colonne
			List<String> listaLabel = new ArrayList<String>();

			if(visualizzazioneTabs && (gestioneGruppi || listaParam.size()>1)) {
				listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME_GRUPPO);
			}
			//listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE); // spostata direttamente nell'elenco delle erogazioni
			if(gestioneConfigurazioni) {
				//listaLabel.add(PorteApplicativeCostanti.LABEL_COLUMN_PORTE_APPLICATIVE_STATO_PORTA);
				listaLabel.add("");
			}
			if(!visualizzazioneTabs && (gestioneGruppi || listaParam.size()>1)) {
				listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME_GRUPPO);
			}
			if(gestioneGruppi || visualizzazioneTabs) { // visualizzata sia per i gruppi che per la modalita' tab
				if(this.isModalitaCompleta()) {
					listaLabel.add(labelAzioni);
				}
				else {
					listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_ELENCO_AZIONI_GRUPPI_PREFIX+labelAzioni);
				}
			}
			if(gestioneConfigurazioni) { 
				if(showConnettoreLink) {
					listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORE);
					listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VERIFICA_CONNETTORE);
					listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_CONNETTORI_MULTIPLI);
					listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_ELENCO_CONNETTORI_MULTIPLI);
				}
				
				listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI);
				listaLabel.add(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RATE_LIMITING);
				listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI);
				listaLabel.add(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RESPONSE_CACHING);
				if(visualizzaSicurezza) {
					listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY);
				}
				if(visualizzaMTOM) {
					listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM);
				}
				listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI);
				if(visualizzaCorrelazione) {
					listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRACCIAMENTO);
				}
				listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE);
				
				if(this.isModalitaAvanzata() || this.apsCore.isProprietaErogazioni_showModalitaStandard()) {
					listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PROTOCOL_PROPERTIES);
				}
				if(this.isModalitaAvanzata()) {
					listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_OPZIONI_AVANZATE);
				}
				if(extendedServletList!=null && extendedServletList.showExtendedInfo(this.request, this.session)){
					listaLabel.add(extendedServletList.getListTitle(this));
				}
			}
			String[] labels = listaLabel.toArray(new String[listaLabel.size()]);
			this.pd.setLabels(labels);

			PropertiesSourceConfiguration propertiesSourceConfiguration = null;
			ConfigManager configManager = null;
			Configurazione configurazioneGenerale = null;
			if(visualizzazioneTabs) {
				propertiesSourceConfiguration = this.apsCore.getMessageSecurityPropertiesSourceConfiguration();
				configManager = ConfigManager.getinstance(ControlStationCore.getLog());
				configManager.leggiConfigurazioni(propertiesSourceConfiguration, true);
				configurazioneGenerale = this.confCore.getConfigurazioneGenerale();
			}
			
			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			Iterator<MappingErogazionePortaApplicativa> it = lista.iterator();
			MappingErogazionePortaApplicativa mapping= null;
			int idTab = 0;
			while (it.hasNext()) {
				mapping = it.next();
				PortaApplicativa paAssociata = this.porteApplicativeCore.getPortaApplicativa(mapping.getIdPortaApplicativa());
				
				Vector<DataElement> e = new Vector<DataElement>();

				Parameter pNomePorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA, paAssociata.getNome());
				Parameter pIdNome = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, paAssociata.getNome());
				Parameter pIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, paAssociata.getIdSoggetto() + "");
				Parameter pIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, ""+paAssociata.getId());
				Parameter pIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, asps.getId()+ "");
				Parameter pIdProvider = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_PROVIDER, paAssociata.getIdSoggetto() + "");
				Parameter pIdPortaPerSA = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_PORTA, ""+paAssociata.getId());
				Parameter pIdTAb = new Parameter(CostantiControlStation.PARAMETRO_ID_TAB, ""+idTab);
				
				@SuppressWarnings("unused")
				Parameter pConfigurazioneDati = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_DATI_INVOCAZIONE, Costanti.CHECK_BOX_ENABLED_TRUE);
				Parameter pConfigurazioneAltroPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_ALTRO_PORTA, Costanti.CHECK_BOX_ENABLED_TRUE);

				// spostata direttamente nell'elenco delle erogazioni
//				// nome mapping
//				DataElement de = new DataElement();
//				//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
//				if(mapping.isDefault()) {
//					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CHANGE,pIdSogg, pNomePorta, pIdPorta,pIdAsps,pConfigurazioneDati);
//				}
//				de.setValue(mapping.isDefault() ? PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MAPPING_EROGAZIONE_PA_NOME_DEFAULT : mapping.getNome());
//				de.setIdToRemove(paAssociata.getNome());
//				//de.setToolTip(StringUtils.isNotEmpty(paAssociata.getDescrizione()) ? paAssociata.getDescrizione() : paAssociata.getNome()); 
//				e.addElement(de);
				
				if(gestioneConfigurazioni && mapping.isDefault() && allActionRedefined && (!showConnettoreLink)) {
					int numEntries = ricerca.getNumEntries(idLista);
					ricerca.setNumEntries(idLista, numEntries -1); 
					this.pd.setNumEntries(numEntries -1);
					continue; // non faccio vedere la riga "disconnessa"
				}
				
				
				boolean statoPA = paAssociata.getStato().equals(StatoFunzionalita.ABILITATO);
				String statoPAallRedefined = null;
				String statoMapping = statoPA ? CostantiControlStation.LABEL_PARAMETRO_PORTA_ABILITATO_TOOLTIP : CostantiControlStation.LABEL_PARAMETRO_PORTA_DISABILITATO_TOOLTIP;
				boolean urlCambiaStato = true;
				if(mapping.isDefault() && allActionRedefined) {
					statoPA = false;
					statoPAallRedefined = "off";
					statoMapping = this.getLabelAllAzioniRidefiniteTooltip(serviceBindingMessage);
					urlCambiaStato = false;
				}
				
				// Nome Gruppo
				if(visualizzazioneTabs && (gestioneGruppi || listaParam.size()>1)) {
					DataElement de = new DataElement();
					
					de.setWidthPx(10);
					de.setType(DataElementType.CHECKBOX);
					
					de.setStatusToolTip(statoPA ? CostantiControlStation.LABEL_PARAMETRO_PORTA_ABILITATO_TOOLTIP_NO_ACTION : CostantiControlStation.LABEL_PARAMETRO_PORTA_DISABILITATO_TOOLTIP_NO_ACTION);
					if(statoPAallRedefined!=null) {
						de.setStatusType(statoPAallRedefined);
					}
					else {
						de.setStatusType(statoPA ? CheckboxStatusType.ABILITATO : CheckboxStatusType.DISABILITATO);
					}
					
					de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME_GRUPPO);
					de.setValue(mapping.getDescrizione());
					de.setStatusValue(mapping.getDescrizione());
					
					if(!mapping.isDefault()) {
						DataElementImage image = new DataElementImage();
						
						image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONFIGURAZIONE_CHANGE,pIdSogg, pIdPorta, pIdAsps,pIdTAb);
						image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,	PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME_GRUPPO));
						image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
						
						de.addImage(image );
					}
					if(!mapping.isDefault()) 
						de.setIdToRemove(paAssociata.getNome());
					
					if(urlCambiaStato) {
						Parameter pAbilita = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ABILITA,  (statoPA ? Costanti.CHECK_BOX_DISABLED : Costanti.CHECK_BOX_ENABLED_TRUE));
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_ABILITAZIONE,pIdSogg, pNomePorta, pIdPorta,pIdAsps, pAbilita);
						
						DataElementImage image = new DataElementImage();
						image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_ABILITAZIONE,pIdSogg, pNomePorta, pIdPorta,pIdAsps, pAbilita,pIdTAb);
						image.setToolTip(statoMapping);
						image.setImage(statoPA ? CostantiControlStation.ICONA_MODIFICA_TOGGLE_ON : CostantiControlStation.ICONA_MODIFICA_TOGGLE_OFF);
						
						de.addImage(image);
					}
					
					e.addElement(de);
				}
				
				if(gestioneConfigurazioni && !visualizzazioneTabs) {
					
					// Abilitato
					DataElement de = new DataElement();
					if(visualizzazioneTabs)
						de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_STATO);
					
					de.setWidthPx(10);
					de.setType(DataElementType.CHECKBOX);
					if(visualizzazioneTabs) {
						de.setStatusValue(statoPA ? this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_ABILITATO) : this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_DISABILITATO));
						de.setStatusToolTip(statoPA ? CostantiControlStation.LABEL_PARAMETRO_PORTA_ABILITATO_TOOLTIP_NO_ACTION : CostantiControlStation.LABEL_PARAMETRO_PORTA_DISABILITATO_TOOLTIP_NO_ACTION);
						if(statoPAallRedefined!=null) {
							de.setStatusType(statoPAallRedefined);
						}
						else {
							de.setStatusType(statoPA ? CheckboxStatusType.ABILITATO : CheckboxStatusType.DISABILITATO);
						}
					} else { 
						de.setToolTip(statoMapping);
					}
					
					if(statoPAallRedefined!=null) {
						de.setSelected(statoPAallRedefined);
					}
					else {
						de.setSelected(statoPA);
					}
					if(urlCambiaStato) {
						Parameter pAbilita = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ABILITA,  (statoPA ? Costanti.CHECK_BOX_DISABLED : Costanti.CHECK_BOX_ENABLED_TRUE));
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_ABILITAZIONE,pIdSogg, pNomePorta, pIdPorta,pIdAsps, pAbilita);
						
						DataElementImage image = new DataElementImage();
						image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_ABILITAZIONE,pIdSogg, pNomePorta, pIdPorta,pIdAsps, pAbilita,pIdTAb);
						image.setToolTip(statoMapping);
						image.setImage(statoPA ? CostantiControlStation.ICONA_MODIFICA_TOGGLE_ON : CostantiControlStation.ICONA_MODIFICA_TOGGLE_OFF);
						
						de.setImage(image);
					}
					e.addElement(de);
				}
				
				// NomeGruppo
				if(!visualizzazioneTabs && (gestioneGruppi || listaParam.size()>1)) {
										
					DataElement de = new DataElement();
					de.setValue(mapping.getDescrizione());
					if(gestioneGruppi && !mapping.isDefault()) {
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONFIGURAZIONE_CHANGE,pIdSogg, pIdPorta, pIdAsps);
					}
					e.addElement(de);
				}
				
				// lista delle azioni
				if(gestioneGruppi || (visualizzazioneTabs && (listaParam.size()>1 || !mapping.isDefault()))) {	
										
					List<String> listaAzioni = null;
					String nomiAzioni = null;
					long countAzioni = 0;
					if(!mapping.isDefault()) {
						listaAzioni = paAssociata.getAzione()!= null ?  paAssociata.getAzione().getAzioneDelegataList() : new ArrayList<String>();
						
						if(listaAzioni.size() > 0 && azioni.size()>0) {
							
							StringBuilder sb = new StringBuilder();
							Iterator<String> itAz = azioni.keySet().iterator();
							while (itAz.hasNext()) {
								String idAzione = (String) itAz.next();
								if(listaAzioni.contains(idAzione)) {
									if(sb.length() >0)
										sb.append(", ");
									
									sb.append(azioni.get(idAzione));
									countAzioni++;
								}
							}
							nomiAzioni = sb.toString();
							
						}
					}
					
					DataElement de = new DataElement();
					de.setSize(200);
					de.setIdToRemove(paAssociata.getNome());
					
					if(visualizzazioneTabs) {
						de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_ELENCO_AZIONI_GRUPPI_PREFIX+labelAzioni);
					}
					
					if(listaSenzaFiltro.size()>1) {
						DataElementImage image = new DataElementImage();
						
						if(!mapping.isDefault()) {
							de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_AZIONE_LIST,pIdSogg, pIdPorta, pIdAsps);
							if(this.isModalitaCompleta()) {
								ServletUtils.setDataElementVisualizzaLabel(de, countAzioni);
							}
							else {
								de.setValue(nomiAzioni);
							}
							de.setToolTip(nomiAzioni);
							
							image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_AZIONE_LIST,pIdSogg, pIdPorta, pIdAsps,pIdTAb);
							image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_ELENCO_AZIONI_GRUPPI_PREFIX+labelAzioni));
							image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
							
							de.setImage(image);
						}
						else {
							if(actionNonRidefinite!=null && !actionNonRidefinite.isEmpty() && azioni.size()>0) {
								
								long countAzioniRidefinite = 0;
								StringBuilder sb = new StringBuilder();
								Iterator<String> itAz = azioni.keySet().iterator();
								while (itAz.hasNext()) {
									String idAzione = (String) itAz.next();
									if(actionNonRidefinite.contains(idAzione)) {
										if(sb.length() >0)
											sb.append(", ");
										
										sb.append(azioni.get(idAzione));
										countAzioniRidefinite++;
									}
								}
								String nomiAzioniNonRidefinite = sb.toString();
								de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_AZIONE_LIST,pIdSogg, pIdPorta, pIdAsps);
								if(this.isModalitaCompleta()) {
									ServletUtils.setDataElementVisualizzaLabel(de, countAzioniRidefinite);
								}
								else {
									de.setValue(nomiAzioniNonRidefinite);
								}
								de.setToolTip(nomiAzioniNonRidefinite);
								
								image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_AZIONE_LIST,pIdSogg, pIdPorta, pIdAsps,pIdTAb);
								image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_ELENCO_AZIONI_GRUPPI_PREFIX+labelAzioni));
								image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
								
								de.setImage(image);
							}
							else {
								if(allActionRedefined) {
									de.setValue(this.getLabelAllAzioniRidefiniteTooltip(serviceBindingMessage));
								}
								else {
									de.setValue("-"); // ??
								}
							}
						}
					}
					else {
						if(org.openspcoop2.core.registry.constants.ServiceBinding.SOAP.equals(serviceBinding)) {
							de.setValue(CostantiControlStation.LABEL_TUTTE_AZIONI_DEFAULT);	
						}
						else {
							de.setValue(CostantiControlStation.LABEL_TUTTE_RISORSE_DEFAULT);	
						}
					}
					e.addElement(de);
					
				}
				
				if(gestioneConfigurazioni) {
					
					// connettore
					if(showConnettoreLink) {
						
						Parameter paIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, asps.getIdSoggetto() + "");
						
						DataElement de = new DataElement();
						//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
						String servletConnettore = ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT;
						PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo = paAssociata.getServizioApplicativoList().get(0);
						
						boolean connettoreMultiploEnabled = paAssociata.getBehaviour() != null;
						
						if(visualizzazioneTabs) {
							if(!connettoreMultiploEnabled)
								de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORE);
							else 
								de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI);
						}
						
						IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
						idServizioApplicativo.setIdSoggettoProprietario(new IDSoggetto(paAssociata.getTipoSoggettoProprietario(), paAssociata.getNomeSoggettoProprietario()));
						idServizioApplicativo.setNome(portaApplicativaServizioApplicativo.getNome());
						ServizioApplicativo sa = this.saCore.getServizioApplicativo(idServizioApplicativo);
						org.openspcoop2.core.config.InvocazioneServizio is = sa.getInvocazioneServizio();
						org.openspcoop2.core.config.Connettore connettore = is.getConnettore();
						
						boolean connettoreMultiploEnabledDefault = false;
						if(!mapping.isDefault()) {
							connettoreMultiploEnabledDefault = paAssociataDefault!=null && paAssociataDefault.getBehaviour() != null;
						}
						
						
						boolean ridefinito = false;
						boolean connettoreRidefinito = false;
						boolean visualizzaLinkConfigurazioneConnettore = !this.core.isConnettoriMultipliEnabled() || ( this.core.isConnettoriMultipliEnabled() && !connettoreMultiploEnabled );
						if(mapping.isDefault()) {
							if(visualizzazioneTabs) {
								if(!connettoreMultiploEnabled) {								
									de.setValue(this.getLabelConnettore(sa,is));
									String tooltipConnettore = this.getTooltipConnettore(sa,is);
									de.setToolTip(tooltipConnettore);
								} else {
									de.setValue(this.getNomiConnettoriMultipliPortaApplicativa(paAssociata));
									de.setToolTip(this.getToolTipConnettoriMultipliPortaApplicativa(paAssociata));
								}
							}
							else {
								if(visualizzaLinkConfigurazioneConnettore) {
									ServletUtils.setDataElementVisualizzaLabel(de);
								} else {
									de.setType(DataElementType.TEXT);
									de.setValue("-");
								}
							}
							if(visualizzaLinkConfigurazioneConnettore)
								de.setUrl(servletConnettore, pIdProvider, pIdPortaPerSA, pIdAsps,
									new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME_SERVIZIO_APPLICATIVO, portaApplicativaServizioApplicativo.getNome()),
									new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO, portaApplicativaServizioApplicativo.getIdServizioApplicativo()+""));
							
							if(visualizzazioneTabs) {
								if(visualizzaLinkConfigurazioneConnettore) {
									DataElementImage image = new DataElementImage();
									image.setUrl(servletConnettore, pIdProvider, pIdPortaPerSA, pIdAsps,pIdTAb,
											new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME_SERVIZIO_APPLICATIVO, portaApplicativaServizioApplicativo.getNome()),
											new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO, portaApplicativaServizioApplicativo.getIdServizioApplicativo()+""));
									image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORE));
									image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
									
									de.addImage(image);
								}
							}
							
						}else {
							// Connettore e' ridefinito se
							connettoreRidefinito = isConnettoreRidefinito(paAssociataDefault, portaApplicativaServizioApplicativoDefault, paAssociata, portaApplicativaServizioApplicativo);
							
							if(!connettoreRidefinito) { 
								servletConnettore = PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORE_DEFAULT;
								if(visualizzazioneTabs) {									
									if(!connettoreMultiploEnabled && !connettoreMultiploEnabledDefault) {	
										de.setValue("["+org.openspcoop2.core.constants.Costanti.MAPPING_EROGAZIONE_PA_DESCRIZIONE_DEFAULT+"] "+this.getLabelConnettore(sa,is));
									} 
									else if(connettoreMultiploEnabledDefault) {
										de.setValue("["+org.openspcoop2.core.constants.Costanti.MAPPING_EROGAZIONE_PA_DESCRIZIONE_DEFAULT+"] "+this.getNomiConnettoriMultipliPortaApplicativa(paAssociataDefault));
										de.setToolTip(this.getToolTipConnettoriMultipliPortaApplicativa(paAssociataDefault));
										// Ci vuole, devo poter ridefinire il connettore visualizzaLinkConfigurazioneConnettore = false;
									}
									else {
										de.setValue(this.getNomiConnettoriMultipliPortaApplicativa(paAssociata));
										de.setToolTip(this.getToolTipConnettoriMultipliPortaApplicativa(paAssociata));
									}
								}
								else {
									if(visualizzaLinkConfigurazioneConnettore) {
										de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODALITA_CONNETTORE_DEFAULT); 
										String tooltipConnettore = this.getTooltipConnettore(sa, is);
										de.setToolTip(ConnettoriCostanti.LABEL_PARAMETRO_MODALITA_CONNETTORE_DEFAULT+CostantiControlStation.TOOLTIP_BREAK_LINE+tooltipConnettore);
									} else {
										de.setType(DataElementType.TEXT);
										de.setValue("-");
									}
								}
							} else {
								ridefinito = true;
								servletConnettore = PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORE_RIDEFINITO;
								if(visualizzazioneTabs) {
									if(!connettoreMultiploEnabled) {	
										de.setValue(this.getLabelConnettore(sa,is));
									} else {
										de.setValue(this.getNomiConnettoriMultipliPortaApplicativa(paAssociata));
										de.setToolTip(this.getToolTipConnettoriMultipliPortaApplicativa(paAssociata));
									}
								}
								else {
									if(visualizzaLinkConfigurazioneConnettore) {
										de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODALITA_CONNETTORE_RIDEFINITO);
										String tooltipConnettore = this.getTooltipConnettore(sa, is);
										de.setToolTip(ConnettoriCostanti.LABEL_PARAMETRO_MODALITA_CONNETTORE_RIDEFINITO+CostantiControlStation.TOOLTIP_BREAK_LINE+tooltipConnettore);
									} else {
										de.setType(DataElementType.TEXT);
										de.setValue("-");
									}
								}
							}
							if(visualizzaLinkConfigurazioneConnettore) {
//								if(!isPaDefaultMulti) {
									de.setUrl(servletConnettore, pIdSogg, pIdPorta, pIdAsps);
//								}else {
//									// solo modifica non ridefinizione
//									de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT, pIdProvider, pIdPortaPerSA, pIdAsps,
//											new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME_SERVIZIO_APPLICATIVO, portaApplicativaServizioApplicativo.getNome()),
//											new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO, portaApplicativaServizioApplicativo.getIdServizioApplicativo()+""));
//								}
							}
							
							if(visualizzazioneTabs) {
								if(visualizzaLinkConfigurazioneConnettore) {
									DataElementImage image = new DataElementImage();
//									if(!isPaDefaultMulti) {
										image.setUrl(servletConnettore, pIdSogg, pIdPorta, pIdAsps,pIdTAb);
//									}else {
//										image.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT, pIdProvider, pIdPortaPerSA, pIdAsps,pIdTAb,
//												new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME_SERVIZIO_APPLICATIVO, portaApplicativaServizioApplicativo.getNome()),
//												new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO, portaApplicativaServizioApplicativo.getIdServizioApplicativo()+""));
//									}
									
									image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORE));
									image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
									
									de.addImage(image);
								}
							}
						}
						
						boolean checkConnettore = org.openspcoop2.pdd.core.connettori.ConnettoreCheck.checkSupported(connettore);
						if(checkConnettore) {
							if(!mapping.isDefault() && !ridefinito) {
								checkConnettore = false;
							}
						}
						
						long idConnettore = connettore.getId();
						boolean visualizzaLinkCheckConnettore = checkConnettore && (!this.core.isConnettoriMultipliEnabled() || ( this.core.isConnettoriMultipliEnabled() && !connettoreMultiploEnabled ));
						
						Parameter pIdConnettore = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ID, idConnettore+"");
						Parameter pConnettoreAccessoDaGruppi = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI, "true");
						Parameter pConnettoreVerificaRegistro = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO, "false");
						if(visualizzazioneTabs) {
							if(visualizzaLinkCheckConnettore) {
								DataElementImage image = new DataElementImage();
								image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VERIFICA_CONNETTORE, paIdSogg, pIdPorta, pIdAsps,pIdTAb,
										pIdConnettore,
										pConnettoreAccessoDaGruppi,
										pConnettoreVerificaRegistro);
								image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_VERIFICA_TOOLTIP_CON_PARAMETRO, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORE));
								image.setImage(CostantiControlStation.ICONA_VERIFICA);
								
								de.addImage(image);
							}
						}
						
						de.allineaTdAlCentro();
						e.addElement(de);
						
						if(!visualizzazioneTabs) {
							DataElement deVerificaConnettore = new DataElement();
							
							if(visualizzaLinkCheckConnettore) {
								deVerificaConnettore.setValue(CostantiControlStation.LABEL_VERIFICA_CONNETTORE_VALORE_LINK);
								deVerificaConnettore.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VERIFICA_CONNETTORE, paIdSogg, pIdPorta, pIdAsps,
										pIdConnettore,
										pConnettoreAccessoDaGruppi,
										pConnettoreVerificaRegistro);
							}
							else {
								deVerificaConnettore.setValue("-");
							}
							deVerificaConnettore.allineaTdAlCentro();
							e.addElement(deVerificaConnettore);
						}
						
						
						// configurazione connettori multipli
						if(visualizzazioneTabs) {
							if(this.core.isConnettoriMultipliEnabled() && (connettoreRidefinito || mapping.isDefault())) {
								DataElementImage image = new DataElementImage();
								List<Parameter> listParametersConfigutazioneConnettoriMultipli = new ArrayList<>();
								listParametersConfigutazioneConnettoriMultipli.add(paIdSogg);
								listParametersConfigutazioneConnettoriMultipli.add(pIdPorta);
								listParametersConfigutazioneConnettoriMultipli.add(pIdAsps);
								listParametersConfigutazioneConnettoriMultipli.add(pIdTAb);
								listParametersConfigutazioneConnettoriMultipli.add(pConnettoreAccessoDaGruppi);
								listParametersConfigutazioneConnettoriMultipli.add(pConnettoreVerificaRegistro);
								listParametersConfigutazioneConnettoriMultipli.add(new Parameter(CostantiControlStation.PARAMETRO_ID_CONN_TAB, "0"));
								
								image = new DataElementImage();
								image.setToolTip(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_CONFIGURAZIONE_CONNETTORI_MULTIPLI_TOOLTIP);
								image.setImage(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_CONFIGURAZIONE_CONNETTORI_MULTIPLI);
								image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONFIGURAZIONE_CONNETTORI_MULTIPLI, 
										listParametersConfigutazioneConnettoriMultipli.toArray(new Parameter[1]));
								de.addImage(image);
							}
						} else {
							DataElement deConfiguraConnettoriMultipli = new DataElement();
							
							if(this.core.isConnettoriMultipliEnabled() && (connettoreRidefinito || mapping.isDefault())) {
								deConfiguraConnettoriMultipli.setValue(this.getStatoConnettoriMultipliPortaApplicativa(paAssociata));
								deConfiguraConnettoriMultipli.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONFIGURAZIONE_CONNETTORI_MULTIPLI, paIdSogg, pIdPorta, pIdAsps,
										pConnettoreAccessoDaGruppi,
										pConnettoreVerificaRegistro,new Parameter(CostantiControlStation.PARAMETRO_ID_CONN_TAB, "0"));
							} else {
								deConfiguraConnettoriMultipli.setValue("-");
							}
							
							deConfiguraConnettoriMultipli.allineaTdAlCentro();
							e.addElement(deConfiguraConnettoriMultipli);
						}
						
						
						// lista connettori multipli
						if(visualizzazioneTabs) {
							if(this.core.isConnettoriMultipliEnabled() && connettoreMultiploEnabled) {
								DataElementImage image = new DataElementImage();
								List<Parameter> listParametersConfigutazioneConnettoriMultipli = new ArrayList<>();
								listParametersConfigutazioneConnettoriMultipli.add(paIdSogg);
								listParametersConfigutazioneConnettoriMultipli.add(pIdPorta);
								listParametersConfigutazioneConnettoriMultipli.add(pIdAsps);
								listParametersConfigutazioneConnettoriMultipli.add(pIdTAb);
								listParametersConfigutazioneConnettoriMultipli.add(pConnettoreAccessoDaGruppi);
								listParametersConfigutazioneConnettoriMultipli.add(pConnettoreVerificaRegistro);
								
								image = new DataElementImage();
								image.setToolTip(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_ELENCO_CONNETTORI_MULTIPLI_TOOLTIP);
								image.setImage(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_ELENCO_CONNETTORI_MULTIPLI);
								image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_LIST, 
										listParametersConfigutazioneConnettoriMultipli.toArray(new Parameter[1]));
								de.addImage(image);
							}
						} else {
							DataElement deListaConnettoriMultipli = new DataElement();
							
							if(this.core.isConnettoriMultipliEnabled() && connettoreMultiploEnabled) {
								if(contaListe)
									ServletUtils.setDataElementVisualizzaLabel(deListaConnettoriMultipli, (long) paAssociata.sizeServizioApplicativoList());
								else 
									ServletUtils.setDataElementVisualizzaLabel(deListaConnettoriMultipli);
								deListaConnettoriMultipli.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_LIST, paIdSogg, pIdPorta, pIdAsps,
										pConnettoreAccessoDaGruppi,
										pConnettoreVerificaRegistro);
							} else {
								deListaConnettoriMultipli.setValue("-");
							}
							
							deListaConnettoriMultipli.allineaTdAlCentro();
							e.addElement(deListaConnettoriMultipli);
						}
						
					}
					 
					// controllo accessi
					DataElement de = new DataElement();
					if(visualizzazioneTabs)
						de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI);
					//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI, pIdSogg, pIdPorta, pIdAsps);
					if(visualizzazioneTabs) {
						this.setStatoControlloAccessiPortaApplicativa(protocollo, paAssociata, de);
					}
					else {
						String statoControlloAccessi = getStatoControlloAccessiPortaApplicativa(protocollo, paAssociata); 
						de.setValue(statoControlloAccessi);
					}
					de.allineaTdAlCentro();
					if(visualizzazioneTabs) {
						DataElementImage image = new DataElementImage();
						
						image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI, pIdSogg, pIdPorta, pIdAsps,pIdTAb);
						image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,	PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI));
						image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
						
						de.setImage(image);
					}
					e.addElement(de);
										
					// RateLimiting
					de = new DataElement();
					if(visualizzazioneTabs)
						de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RATE_LIMITING);
					
					de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_LIST+"?"+
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_RUOLO_PORTA+"="+RuoloPolicy.APPLICATIVA.getValue()+"&"+
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_NOME_PORTA+"="+paAssociata.getNome()+"&"+
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_SERVICE_BINDING+"="+serviceBindingMessage.name()
							);
					List<AttivazionePolicy> listaPolicy = null;
					if(contaListe || visualizzazioneTabs) {
						Search searchPolicy = new Search(true);
						listaPolicy = this.confCore.attivazionePolicyList(searchPolicy, RuoloPolicy.APPLICATIVA, paAssociata.getNome());
					}
					if(visualizzazioneTabs) {
						this.setStatoRateLimiting(de, listaPolicy);
					}
					else {
						if(contaListe) {
							ServletUtils.setDataElementVisualizzaLabel(de, (long) listaPolicy.size() );
						}
						else {
							ServletUtils.setDataElementVisualizzaLabel(de);
						}
					}
					de.allineaTdAlCentro();
					if(visualizzazioneTabs) {
						DataElementImage image = new DataElementImage();
						
						image.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_LIST, 
								new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_RUOLO_PORTA,RuoloPolicy.APPLICATIVA.getValue()),
								new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_NOME_PORTA,paAssociata.getNome()),
								new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_SERVICE_BINDING,serviceBindingMessage.name()),
								pIdTAb
								);
						image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,	ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RATE_LIMITING));
						image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
						
						de.setImage(image);
					}
					e.addElement(de);
					
					// validazione contenuti
					de = new DataElement();
					if(visualizzazioneTabs)
						de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI);
					//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI, pIdSogg, pIdPorta, pIdAsps);
					if(visualizzazioneTabs) {
						setStatoValidazioneContenuti(de, paAssociata.getValidazioneContenutiApplicativi(), apc.getFormatoSpecifica());
					}
					else {
						String statoValidazione = getStatoValidazionePortaApplicativa(paAssociata);
						de.setValue(statoValidazione);
					}
					de.allineaTdAlCentro();
					if(visualizzazioneTabs) {
						DataElementImage image = new DataElementImage();
						
						image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI, pIdSogg, pIdPorta, pIdAsps,pIdTAb);
						image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,	PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI));
						image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
						
						
						de.setImage(image);
					}
					e.addElement(de);
					
					// Response Caching
					de = new DataElement();
					if(visualizzazioneTabs)
						de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RESPONSE_CACHING);
					//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_RESPONSE_CACHING, pIdSogg, pIdPorta, pIdAsps);
					if(visualizzazioneTabs) {
						setStatoCachingRisposta(de, paAssociata.getResponseCaching(), configurazioneGenerale);
					}
					else {
						String statoResponseCaching = getStatoResponseCachingPortaApplicativa(paAssociata, false);
						de.setValue(statoResponseCaching);
					}
					de.allineaTdAlCentro();
					if(visualizzazioneTabs) {
						DataElementImage image = new DataElementImage();
						
						image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_RESPONSE_CACHING, pIdSogg, pIdPorta, pIdAsps,pIdTAb);
						image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,	ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RESPONSE_CACHING));
						image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
						
						
						de.setImage(image);
					}
					e.addElement(de);
					
					// message security
					if(visualizzaSicurezza) {
						de = new DataElement();
						if(visualizzazioneTabs)
							de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY);
						//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY, pIdSogg, pIdPorta, pIdAsps);
						if(visualizzazioneTabs) {
							setStatoSicurezzaMessaggio(de, paAssociata.getMessageSecurity(), configManager, propertiesSourceConfiguration);
						}
						else {
							String statoMessageSecurity = getStatoMessageSecurityPortaApplicativa(paAssociata);
							de.setValue(statoMessageSecurity);
						}
						de.allineaTdAlCentro();
						if(visualizzazioneTabs) {
							DataElementImage image = new DataElementImage();
							
							image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY, pIdSogg, pIdPorta, pIdAsps,pIdTAb);
							image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,	PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY));
							image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
							
							de.setImage(image);
						}
						e.addElement(de);
					}
					
					//mtom
					if(visualizzaMTOM) {
						de = new DataElement();
						if(visualizzazioneTabs)
							de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM);
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM,pIdPorta, pIdSogg, pIdAsps);
						if(visualizzazioneTabs) {
							setStatoMTOM(de, paAssociata.getMtomProcessor());
						}
						else {
							String statoMTOM = getStatoMTOMPortaApplicativa(paAssociata);
							de.setValue(statoMTOM);
						}
						de.allineaTdAlCentro();
						if(visualizzazioneTabs) {
							DataElementImage image = new DataElementImage();
							
							image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM,pIdPorta, pIdSogg, pIdAsps,pIdTAb);
							image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,	PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM));
							image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
							
							
							de.setImage(image);
						}
						e.addElement(de);
					}
					
					// trasformazioni
					de = new DataElement();
					if(visualizzazioneTabs)
						de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI);
					//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_LIST, pIdSogg, pIdPorta, pIdAsps);
					if(visualizzazioneTabs) {
						setStatoTrasformazioni(de, paAssociata.getTrasformazioni(), serviceBindingMessage);
					}
					else {
						if(contaListe) {
							long size = 0;
							if(paAssociata.getTrasformazioni()!=null) {
								size = paAssociata.getTrasformazioni().sizeRegolaList();
							}
							ServletUtils.setDataElementVisualizzaLabel(de, (long) size); 
						}
						else {
							ServletUtils.setDataElementVisualizzaLabel(de);
						}	
					}
					de.allineaTdAlCentro();
					if(visualizzazioneTabs) {
						DataElementImage image = new DataElementImage();
						
						image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_LIST, pIdSogg, pIdPorta, pIdAsps,pIdTAb);
						image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,	PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI));
						image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
						
						de.setImage(image);
					}
					e.addElement(de);
					
					// correlazione applicativa
					if(visualizzaCorrelazione) {
						de = new DataElement();
						if(visualizzazioneTabs)
							de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRACCIAMENTO);
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA, pIdSogg, pIdPorta, pIdNome,pIdAsps);
						if(visualizzazioneTabs) {
							setStatoTracciamento(de, paAssociata.getCorrelazioneApplicativa(), 
									paAssociata.getCorrelazioneApplicativaRisposta(), paAssociata.getTracciamento(), configurazioneGenerale);	
						}
						else {
							String statoTracciamento = getStatoTracciamentoPortaApplicativa(paAssociata);
							de.setValue(statoTracciamento);
						}
						de.allineaTdAlCentro();
						if(visualizzazioneTabs) {
							DataElementImage image = new DataElementImage();
							
							image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA, pIdSogg, pIdPorta, pIdNome,pIdAsps,pIdTAb);
							image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,	PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRACCIAMENTO));
							image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
							
							de.setImage(image);
						}
						e.addElement(de);
					}
					
					// dump
					de = new DataElement();
					if(visualizzazioneTabs)
						de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE);
					//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE, pIdSogg, pIdPorta, pIdAsps);
					if(visualizzazioneTabs) {
						setStatoDump(de, paAssociata.getDump(), configurazioneGenerale);
					}
					else {
						String statoDump = getStatoDumpPortaApplicativa(paAssociata,false);
						de.setValue(statoDump);
					}
					de.allineaTdAlCentro();
					if(visualizzazioneTabs) {
						DataElementImage image = new DataElementImage();
						
						image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE, pIdSogg, pIdPorta, pIdAsps,pIdTAb);
						image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,	PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE));
						image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
						
						de.setImage(image);
					}
					e.addElement(de);
					
					// Protocol Properties
					if(this.isModalitaAvanzata() || this.apsCore.isProprietaErogazioni_showModalitaStandard()) {
						de = new DataElement();
						if(visualizzazioneTabs)
							de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PROTOCOL_PROPERTIES);
						//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_PROPRIETA_PROTOCOLLO_LIST, pIdSogg, pIdPorta,pIdAsps);
						if(visualizzazioneTabs) {
							setStatoProprieta(de, paAssociata.sizeProprietaList());
						}
						else {
							if (contaListe) {
								int numProp = paAssociata.sizeProprietaList();
								ServletUtils.setDataElementVisualizzaLabel(de, (long) numProp );
							} else
								ServletUtils.setDataElementVisualizzaLabel(de);
						}
						de.allineaTdAlCentro();
						if(visualizzazioneTabs) {
							DataElementImage image = new DataElementImage();
							
							image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_PROPRIETA_PROTOCOLLO_LIST, pIdSogg, pIdPorta,pIdAsps,pIdTAb);
							image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,	PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PROTOCOL_PROPERTIES));
							image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
							
							de.setImage(image);
						}
						e.addElement(de);
					}
	
					// Altro
					if(this.isModalitaAvanzata()){
						de = new DataElement();
						if(visualizzazioneTabs)
							de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_OPZIONI_AVANZATE);
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CHANGE,pIdSogg, pNomePorta, pIdPorta,pIdAsps,pConfigurazioneAltroPorta);
						if(visualizzazioneTabs) {
							String behaviour = (!this.core.isConnettoriMultipliEnabled() && paAssociata.getBehaviour()!=null) ? paAssociata.getBehaviour().getNome() : null;
							setStatoOpzioniAvanzate(de, 
									protocollo, serviceBindingMessage,
									paAssociata.getAllegaBody(), paAssociata.getScartaBody(), 
									paAssociata.getIntegrazione(), behaviour,
									paAssociata.getStateless(), null, 
									paAssociata.getRicevutaAsincronaSimmetrica(), paAssociata.getRicevutaAsincronaAsimmetrica(),
									paAssociata.getGestioneManifest());
						}
						else {
							ServletUtils.setDataElementVisualizzaLabel(de);
						}
						de.allineaTdAlCentro();
						if(visualizzazioneTabs) {
							DataElementImage image = new DataElementImage();
							
							image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CHANGE,pIdSogg, pNomePorta, pIdPorta,pIdAsps,pConfigurazioneAltroPorta,pIdTAb);
							image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,	PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_OPZIONI_AVANZATE));
							image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
							
							de.setImage(image);
						}
						e.addElement(de);
					}
					
					// Extended Servlet List
					if(extendedServletList!=null && extendedServletList.showExtendedInfo(this.request, this.session)){
						de = new DataElement();
						if(visualizzazioneTabs)
							de.setLabel(extendedServletList.getListTitle(this));
						de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_EXTENDED_LIST, pIdPorta,pIdNome,pIdPorta, pIdSogg);
						if(visualizzazioneTabs) {
							int numExtended = extendedServletList.sizeList(paAssociata);
							setStatoExtendedList(de, numExtended);
						}
						else {
							if (contaListe) {
								int numExtended = extendedServletList.sizeList(paAssociata);
								ServletUtils.setDataElementVisualizzaLabel(de,Long.valueOf(numExtended));
							} else
								ServletUtils.setDataElementVisualizzaLabel(de);
						}
						de.allineaTdAlCentro();
						if(visualizzazioneTabs) {
							DataElementImage image = new DataElementImage();
							
							image.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_EXTENDED_LIST, pIdPorta,pIdNome,pIdPorta, pIdSogg,pIdTAb);
							image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,extendedServletList.getListTitle(this)));
							image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
							
							de.setImage(image);
						}
						e.addElement(de);
					}
					
				}
				
				dati.addElement(e);
				idTab ++;
			}

			this.pd.setDati(dati);
			if(visualizzazioneTabs) {
				this.pd.setSelect(true);
		        if(showBottoneAggiungiNuovaConfigurazione) {
		        	this.pd.setAddButton(true);
		        }
		        else {
		        	this.pd.setAddButton(false);
		        }
				if(lista.size() > 1)
					this.pd.setRemoveButton(true);
				else 
					this.pd.setRemoveButton(false);
			} else {
				if(gestioneGruppi) {
					if(listaSenzaFiltro!=null && listaSenzaFiltro.size()>1) {
						this.pd.setRemoveButton(true);
						this.pd.setSelect(true);
					}
					else {
						this.pd.setRemoveButton(false);
						this.pd.setSelect(false);
					}
					this.pd.setAddButton(true);
				}
				else {
					this.pd.setAddButton(false);
					this.pd.setRemoveButton(false);
					this.pd.setSelect(false);
				}
			}
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	public boolean isConnettoreRidefinito(PortaApplicativa paDefault,	PortaApplicativaServizioApplicativo paSADefault,
			PortaApplicativa paCurrent, PortaApplicativaServizioApplicativo paSACurrent) {
		boolean connettoreRidefinito = (
				(
					paDefault.getServizioApplicativoDefault() == null && (paCurrent.getServizioApplicativoDefault() != null || paSACurrent.getNome().equals(paCurrent.getNome()))
				) || (
					paDefault.getServizioApplicativoDefault() != null && 
						(
							paCurrent.getServizioApplicativoDefault() == null 
							|| 
							!paDefault.getServizioApplicativoDefault().equals(paCurrent.getServizioApplicativoDefault())
						)
					//!paSADefault.getNome().equals(paSACurrent.getNome()))
				)
			);
		return connettoreRidefinito;
	}


	
	private MappingErogazionePortaApplicativa getFiltroAzioneMappingErogazione(String filtroAzione, List<MappingErogazionePortaApplicativa> lista) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		if(StringUtils.isNotEmpty(filtroAzione) && !filtroAzione.equals(CostantiControlStation.DEFAULT_VALUE_AZIONE_RISORSA_NON_SELEZIONATA)) {
			MappingErogazionePortaApplicativa mappingTmp = null; 
			
			for (MappingErogazionePortaApplicativa mappingErogazionePortaApplicativa : lista) {
				PortaApplicativa paAssociata = this.porteApplicativeCore.getPortaApplicativa(mappingErogazionePortaApplicativa.getIdPortaApplicativa());
				if(paAssociata.getAzione() != null && paAssociata.getAzione().getAzioneDelegataList().contains(filtroAzione)) {
					mappingTmp = mappingErogazionePortaApplicativa;
					break;
				}
			}
			if(mappingTmp == null) {
				for (MappingErogazionePortaApplicativa mappingErogazionePortaApplicativa : lista) {
					if(mappingErogazionePortaApplicativa.isDefault()) {
						mappingTmp = mappingErogazionePortaApplicativa;
						break;
					}
				}
			}
			return mappingTmp;
		}
		else {
			return null;
		}
	}
	
	private List<MappingErogazionePortaApplicativa> impostaFiltroAzioneMappingErogazione(String filtroAzione, List<MappingErogazionePortaApplicativa> lista, 
			ISearch ricerca, int idLista) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		
		MappingErogazionePortaApplicativa mapping = getFiltroAzioneMappingErogazione(filtroAzione, lista);
		if(mapping!=null) {
			List<MappingErogazionePortaApplicativa> newList = new ArrayList<>();
			newList.add(mapping);
			this.pd.setNumEntries(1);
			return newList;
		}
		else {
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
			return lista;
		}
	}
	
	public boolean allActionsRedefinedMappingErogazionePaAssociate(List<String> azioni, List<PortaApplicativa> lista) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		// verifico se tutte le azioni sono definite in regole specifiche
		boolean all = true;
		if(azioni!=null && azioni.size()>0) {
			for (String azione : azioni) {
				if(lista==null || lista.size()<=0) {
					all  = false;
					break;
				}
				boolean found = false;
				for (PortaApplicativa paAssociata : lista) {
					if(paAssociata.getAzione() != null && paAssociata.getAzione().getAzioneDelegataList().contains(azione)) {
						found = true;
						break;
					}
				}
				if(!found) {
					all  = false;
					break;
				}
			}
		}
		return all;
	}
	
	private MappingFruizionePortaDelegata getFiltroAzioneMappingFruizione(String filtroAzione, List<MappingFruizionePortaDelegata> lista) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		if(StringUtils.isNotEmpty(filtroAzione) && !filtroAzione.equals(CostantiControlStation.DEFAULT_VALUE_AZIONE_RISORSA_NON_SELEZIONATA)) {
			MappingFruizionePortaDelegata mappingTmp = null; 
			
			for (MappingFruizionePortaDelegata mappingFruizionePortaDelegata : lista) {
				PortaDelegata pdAssociata = this.porteDelegateCore.getPortaDelegata(mappingFruizionePortaDelegata.getIdPortaDelegata());
				if(pdAssociata.getAzione() != null && pdAssociata.getAzione().getAzioneDelegataList().contains(filtroAzione)) {
					mappingTmp = mappingFruizionePortaDelegata;
					break;
				}
			}
			if(mappingTmp == null) {
				for (MappingFruizionePortaDelegata mappingFruizionePortaDelegata : lista) {
					if(mappingFruizionePortaDelegata.isDefault()) {
						mappingTmp = mappingFruizionePortaDelegata;
						break;
					}
				}
			}
			return mappingTmp;
		}
		else {
			return null;
		}
	}
	private List<MappingFruizionePortaDelegata> impostaFiltroAzioneMappingFruizione(String filtroAzione, List<MappingFruizionePortaDelegata> lista, 
			ISearch ricerca, int idLista) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		MappingFruizionePortaDelegata mappingTmp = this.getFiltroAzioneMappingFruizione(filtroAzione, lista);	
		if(mappingTmp!=null) {
			List<MappingFruizionePortaDelegata> newList = new ArrayList<>();
			newList.add(mappingTmp);
			this.pd.setNumEntries(1);
			return newList;
		} else {
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
			return lista;
		}
	}
	
	public List<String> getAllActionsNotRedefinedMappingFruizione(List<String> azioni, List<MappingFruizionePortaDelegata> lista) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		// verifico se tutte le azioni sono definite in regole specifiche
		List<String> l = new ArrayList<>();
		if(lista==null || lista.size()<=0) {
			return azioni;
		}
		l.addAll(azioni);
		for (MappingFruizionePortaDelegata mappingFruizionePortaDelegata : lista) {
			PortaDelegata pdAssociata = this.porteDelegateCore.getPortaDelegata(mappingFruizionePortaDelegata.getIdPortaDelegata());
			if(pdAssociata.getAzione() != null && !pdAssociata.getAzione().getAzioneDelegataList().isEmpty()) {
				for (String azPA : pdAssociata.getAzione().getAzioneDelegataList()) {
					l.remove(azPA);
				}
			}
		}
		return l;
	}
	
	public boolean allActionsRedefinedMappingFruizione(List<String> azioni, List<MappingFruizionePortaDelegata> lista) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		// verifico se tutte le azioni sono definite in regole specifiche
		boolean all = true;
		if(azioni!=null && azioni.size()>0) {
			for (String azione : azioni) {
				if(lista==null || lista.size()<=0) {
					all  = false;
					break;
				}
				boolean found = false;
				for (MappingFruizionePortaDelegata mappingFruizionePortaDelegata : lista) {
					PortaDelegata pdAssociata = this.porteDelegateCore.getPortaDelegata(mappingFruizionePortaDelegata.getIdPortaDelegata());
					if(pdAssociata.getAzione() != null && pdAssociata.getAzione().getAzioneDelegataList().contains(azione)) {
						found = true;
						break;
					}
				}
				if(!found) {
					all  = false;
					break;
				}
			}
		}
		return all;
	}
	
	public boolean allActionsRedefinedMappingFruizionePdAssociate(List<String> azioni, List<PortaDelegata> lista) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		// verifico se tutte le azioni sono definite in regole specifiche
		boolean all = true;
		if(azioni!=null && azioni.size()>0) {
			for (String azione : azioni) {
				if(lista==null || lista.size()<=0) {
					all  = false;
					break;
				}
				boolean found = false;
				for (PortaDelegata pdAssociata : lista) {
					if(pdAssociata.getAzione() != null && pdAssociata.getAzione().getAzioneDelegataList().contains(azione)) {
						found = true;
						break;
					}
				}
				if(!found) {
					all  = false;
					break;
				}
			}
		}
		return all;
	}


	public void serviziFruitoriPorteDelegateList(List<PortaDelegata> lista, String idServizio, String idSoggettoFruitore, String idFruzione, ISearch ricerca)
			throws Exception {
		try {
			
			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

			ServletUtils.addListElementIntoSession(this.session, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI_PORTE_DELEGATE,
					new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, idServizio),
					new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO, idSoggettoFruitore),
					new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID, idFruzione));

			IExtendedListServlet extendedServletList = this.core.getExtendedServletPortaDelegata();
			
			int idLista = Liste.SERVIZI_FRUITORI_PORTE_DELEGATE;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			// Utilizza la configurazione come parent
			ServletUtils.setObjectIntoSession(this.session, PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE, PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT);

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

			lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
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
				ServletUtils.enabledPageDataSearch(this.pd, AccordiServizioParteSpecificaCostanti.LABEL_APS_CONFIGURAZIONI, search);
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
						ServletUtils.setDataElementVisualizzaLabel(de,Long.valueOf(numSA));
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
						ServletUtils.setDataElementVisualizzaLabel(de,Long.valueOf(numSA));
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
					ServletUtils.setDataElementVisualizzaLabel(de,Long.valueOf(numCorrelazione));
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
					ServletUtils.setDataElementVisualizzaLabel(de,Long.valueOf(numCorrelazione));
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
						ServletUtils.setDataElementVisualizzaLabel(de,Long.valueOf(numExtended));
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
	
	public void serviziFruitoriMappingList(List<MappingFruizionePortaDelegata> listaParam, String idServizio, 
			String idSoggFruitoreDelServizio, IDSoggetto idSoggettoFruitore, String idFruzione, ISearch ricerca)	throws Exception {
		try {
			
			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
			
			Parameter parametroTipoSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SOGGETTO_FRUITORE, idSoggettoFruitore.getTipo());
			Parameter parametroNomeSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SOGGETTO_FRUITORE, idSoggettoFruitore.getNome());
			
			ServletUtils.addListElementIntoSession(this.session, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI_PORTE_DELEGATE,
					new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, idServizio),
					new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO, idSoggFruitoreDelServizio),
					new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID, idFruzione),
					parametroTipoSoggettoFruitore, parametroNomeSoggettoFruitore);

			IExtendedListServlet extendedServletList = this.core.getExtendedServletPortaDelegata();
			
			int idLista = Liste.CONFIGURAZIONE_FRUIZIONE;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);

			String tipologia = ServletUtils.getObjectFromSession(this.session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			boolean gestioneFruitori = false;
			if(tipologia!=null) {
				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
					gestioneFruitori = true;
				}
			}
			
			boolean gestioneGruppi = true;
			String paramGestioneGruppi = ServletUtils.getObjectFromSession(this.session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_GRUPPI);
			if(paramGestioneGruppi!=null && !"".equals(paramGestioneGruppi)) {
				gestioneGruppi = Boolean.valueOf(paramGestioneGruppi);
			}
			
			boolean gestioneConfigurazioni = true;
			String paramGestioneConfigurazioni = ServletUtils.getObjectFromSession(this.session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_GESTIONE_CONFIGURAZIONI);
			if(paramGestioneConfigurazioni!=null && !"".equals(paramGestioneConfigurazioni)) {
				gestioneConfigurazioni = Boolean.valueOf(paramGestioneConfigurazioni);
			}
			
			boolean visualizzazioneTabs = !this.isModalitaCompleta();
			if(visualizzazioneTabs) {
			        gestioneGruppi = false;
			        this.pd.setCustomListViewName(ErogazioniCostanti.ASPS_EROGAZIONI_NOME_VISTA_CUSTOM_CONFIGURAZIONE);
			}
			
			// Prendo il nome e il tipo del servizio
			AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idServizio));
			AccordoServizioParteComuneSintetico apc = this.apcCore.getAccordoServizioSintetico(asps.getIdAccordo()); 
			org.openspcoop2.core.registry.constants.ServiceBinding serviceBinding = apc.getServiceBinding();
			ServiceBinding serviceBindingMessage = this.apcCore.toMessageServiceBinding(apc.getServiceBinding());
			
			Map<String,String> azioni = this.porteApplicativeCore.getAzioniConLabel(asps, apc, false, true, new ArrayList<String>());
			String filtroAzione = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_AZIONE);
			
			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setSearch("");

			// Utilizza la configurazione come parent
			ServletUtils.setObjectIntoSession(this.session, PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE, PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT);
			
			List<MappingFruizionePortaDelegata> listaSenzaFiltro = this.impostaFiltroAzioneMappingFruizione(null, listaParam,ricerca, idLista);
			
			boolean showBottoneAggiungiNuovaConfigurazione = 
					(azioni!=null && azioni.size()>1) 
					||
					(listaSenzaFiltro.size()>1);
			if(showBottoneAggiungiNuovaConfigurazione) {
				// verifico modalita identificazione
				for (MappingFruizionePortaDelegata mappingFruizionePortaDelegata : listaParam) {
					if(mappingFruizionePortaDelegata.isDefault()) {
						PortaDelegata pdDefaullt = this.porteDelegateCore.getPortaDelegata(mappingFruizionePortaDelegata.getIdPortaDelegata());
						if(pdDefaullt.getAzione()!=null && 
								org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione.STATIC.equals(pdDefaullt.getAzione().getIdentificazione())) {
							showBottoneAggiungiNuovaConfigurazione = false;
						}
						break;
					}
				}
			}
						
			List<MappingFruizionePortaDelegata> lista = null;
			boolean chooseTabWithFilter = !this.isModalitaCompleta();
			if(chooseTabWithFilter) {
				lista = listaSenzaFiltro;
				if(lista.size()>1) {
					MappingFruizionePortaDelegata mappingContenenteAzione = getFiltroAzioneMappingFruizione(filtroAzione, listaParam);
					if(mappingContenenteAzione!=null) {
						int tab = -1;
						for (int i = 0; i < lista.size(); i++) {
							if(lista.get(i).getNome().equals(mappingContenenteAzione.getNome())) {
								tab = i;
								break;
							}
						}
						if(tab>=0) {
							ServletUtils.setObjectIntoSession(this.session, tab+"", CostantiControlStation.PARAMETRO_ID_TAB);
						}
					}
					
					this.pd.setLabelBottoneFiltra(CostantiControlStation.LABEL_BOTTONE_INDIVIDUA_GRUPPO);
					//this.pd.setLabelBottoneRipulsci("Ripulisci Selezione");
				}
			}
			else {
				lista = this.impostaFiltroAzioneMappingFruizione(filtroAzione, listaParam,ricerca, idLista);
			}
			
			boolean allActionRedefined = false;
			List<String> actionNonRidefinite = null;
            if(listaSenzaFiltro.size()>1) {
				List<String> azioniL = new ArrayList<>();
				if(azioni != null && azioni.size() > 0)
					azioniL.addAll(azioni.keySet());
				allActionRedefined = this.allActionsRedefinedMappingFruizione(azioniL, listaSenzaFiltro);
				if(!allActionRedefined) {
					actionNonRidefinite = this.getAllActionsNotRedefinedMappingFruizione(azioniL, listaSenzaFiltro);
				}
            }
			
			if(!gestioneGruppi && listaParam.size()<=1) {
				ServletUtils.disabledPageDataSearch(this.pd);
			}
			else {
				this.addFilterAzione(azioni, filtroAzione, serviceBindingMessage);
			}
			
			boolean visualizzaMTOM = true;
			boolean visualizzaSicurezza = true;
			boolean visualizzaCorrelazione = true;
			
			switch (serviceBinding) {
			case REST:
				visualizzaMTOM = false;
				visualizzaSicurezza = true;
				visualizzaCorrelazione = true;
				break;
			case SOAP:
			default:
				visualizzaMTOM = true;
				visualizzaSicurezza = true;
				visualizzaCorrelazione = true;
				break;
			}
			

			// Prendo il nome e il tipo del soggetto fruitore
			Soggetto soggFruitore = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(idSoggFruitoreDelServizio));
		
			String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(soggFruitore.getTipo());
			
			String servizioTmpTile = this.getLabelIdServizio(asps);
			if(gestioneFruitori) {
				servizioTmpTile = this.getLabelServizioFruizione(protocollo, idSoggettoFruitore, asps);
			}
			Parameter pIdServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, idServizio+ "");
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idServizio);
			Parameter pIdSoggettoErogatore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, asps.getIdSoggetto()+"");
			
			String fruitoreTmpTile = this.getLabelNomeSoggetto(protocollo, soggFruitore.getTipo(), soggFruitore.getNome());
		
			Fruitore fru = null;
			if(gestioneFruitori) {
				for (Fruitore fruCheck : asps.getFruitoreList()) {
					if(fruCheck.getTipo().equals(idSoggettoFruitore.getTipo()) &&
							fruCheck.getNome().equals(idSoggettoFruitore.getNome())) {
						fru = fruCheck;
						break;
					}
				}
			}
			else {
				for (Fruitore fruCheck : asps.getFruitoreList()) {
					if(fruCheck.getTipo().equals(soggFruitore.getTipo()) &&
							fruCheck.getNome().equals(soggFruitore.getNome())) {
						fru = fruCheck;
						break;
					}
				}
			}
			
			Parameter pIdFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID, idFruzione+ "");
			
			
			boolean showConnettoreLink = true;
			if(gestioneConfigurazioni && this.isModalitaStandard()) {
				showConnettoreLink = false;
				
				Iterator<MappingFruizionePortaDelegata> it = lista.iterator();
				MappingFruizionePortaDelegata mapping = null;
				while (it.hasNext()) {
					mapping = it.next();
					
					if(!mapping.isDefault()) {
						PortaDelegata pdAssociata = this.porteDelegateCore.getPortaDelegata(mapping.getIdPortaDelegata());

						List<String> listaAzioniPDAssociataMappingNonDefault = pdAssociata.getAzione().getAzioneDelegataList();
						
						String azioneConnettore =  null;
						if(listaAzioniPDAssociataMappingNonDefault!=null && listaAzioniPDAssociataMappingNonDefault.size()>0) {
							azioneConnettore = listaAzioniPDAssociataMappingNonDefault.get(0);
						}
						
						boolean ridefinito = false;
						if(azioneConnettore!=null && !"".equals(azioneConnettore)) {
							for (ConfigurazioneServizioAzione check : fru.getConfigurazioneAzioneList()) {
								if(check.getAzioneList().contains(azioneConnettore)) {
									ridefinito = true;
									break;
								}
							}
						}
						if(ridefinito) {
							showConnettoreLink = true;
							break;
						}
					}
				}

			}
			else {
				// se e' standard lo faccio vedere solo se ho più di un gruppo.
				showConnettoreLink = lista!=null && lista.size()>1;
			}
			
			
			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, this.session);
			String labelAzioni = this.getLabelAzioni(serviceBindingMessage);
			if(gestioneFruitori) {
				if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
					Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, asps.getNome());
					Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, asps.getTipo());
					lstParam.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_FRUIZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
					lstParam.add(new Parameter(servizioTmpTile, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_CHANGE, pIdServizio,pNomeServizio, pTipoServizio, 
							parametroTipoSoggettoFruitore, parametroNomeSoggettoFruitore));
					String labelConfigurazione = gestioneConfigurazioni ? ErogazioniCostanti.LABEL_ASPS_GESTIONE_CONFIGURAZIONI : 
						(gestioneGruppi ? MessageFormat.format(ErogazioniCostanti.LABEL_ASPS_GESTIONE_GRUPPI_CON_PARAMETRO, labelAzioni) : AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_APPLICATIVE);
					
					lstParam.add(new Parameter(labelConfigurazione, null));
					
				}else {
					lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORI, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
					lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_CONFIGURAZIONI_DI + servizioTmpTile, null));
				}
			}
			else {
				lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FUITORI_DI  + servizioTmpTile, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST , pIdServizio,pIdSoggettoErogatore));
				lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_CONFIGURAZIONI_DI + fruitoreTmpTile, null));
			}
			
			
			
			this.pd.setSearchLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
			this.pd.setSearchDescription("");
						
			// Controllo se richiedere il connettore
			boolean connettoreStatic = false;
			if(gestioneFruitori) {
				connettoreStatic = this.apsCore.isConnettoreStatic(protocollo);
			}
			
			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParam );

			List<String> listaLabel = new ArrayList<String>();
			if(visualizzazioneTabs && (gestioneGruppi || listaParam.size()>1)) {
		        listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME_GRUPPO);
			}
			if(gestioneConfigurazioni) {
				//listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_ABILITATO);
				listaLabel.add("");
			}
			if(!visualizzazioneTabs && (gestioneGruppi || listaParam.size()>1)) {
				listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME_GRUPPO);
			}
			if(gestioneGruppi || visualizzazioneTabs) {
				//listaLabel.add(this.getLabelAzioni(serviceBindingMessage));
				if(this.isModalitaCompleta()) {
					listaLabel.add(labelAzioni);
				}
				else {
					listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_ELENCO_AZIONI_GRUPPI_PREFIX+labelAzioni);
				}
			}
			if(gestioneConfigurazioni) {
				if(showConnettoreLink && !connettoreStatic) {
					listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE);
					listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VERIFICA_CONNETTORE);
				}
				listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONTROLLO_ACCESSI);
				listaLabel.add(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RATE_LIMITING);
				listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI);
				listaLabel.add(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RESPONSE_CACHING);
				if(visualizzaSicurezza) {
					listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY);
				}
				if(visualizzaMTOM) {
					listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM);
				}
				listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI);
				if(visualizzaCorrelazione) {
					listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRACCIAMENTO);
				}
				
				listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_DUMP_CONFIGURAZIONE);
				
				if((this.isModalitaAvanzata() || this.porteDelegateCore.isProprietaFruizioni_showModalitaStandard())) {
					listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PROTOCOL_PROPERTIES);
				}
				if(this.isModalitaAvanzata()) {
					listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_OPZIONI_AVANZATE);
				}
				if(extendedServletList!=null && extendedServletList.showExtendedInfo(this.request, this.session)){
					listaLabel.add(extendedServletList.getListTitle(this));
				}
			}
			
			String[] labels = listaLabel.toArray(new String[listaLabel.size()]);
			this.pd.setLabels(labels);

			PropertiesSourceConfiguration propertiesSourceConfiguration = null;
			ConfigManager configManager = null;
			Configurazione configurazioneGenerale = null;
			if(visualizzazioneTabs) {
				propertiesSourceConfiguration = this.apsCore.getMessageSecurityPropertiesSourceConfiguration();
				configManager = ConfigManager.getinstance(ControlStationCore.getLog());
				configManager.leggiConfigurazioni(propertiesSourceConfiguration, true);
				configurazioneGenerale = this.confCore.getConfigurazioneGenerale();
			}
			
			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			Iterator<MappingFruizionePortaDelegata> it = lista.iterator();
			MappingFruizionePortaDelegata mapping = null;
			int idTab = 0;
			while (it.hasNext()) {
				mapping = it.next();
				PortaDelegata pdAssociata = this.porteDelegateCore.getPortaDelegata(mapping.getIdPortaDelegata());
				Vector<DataElement> e = new Vector<DataElement>();

				Parameter pIdPD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, "" + pdAssociata.getId());
				Parameter pNomePD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA, pdAssociata.getNome());
				Parameter pIdSoggPD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, pdAssociata.getIdSoggetto() + "");
				Parameter pIdTAb = new Parameter(CostantiControlStation.PARAMETRO_ID_TAB, ""+idTab);
				
				@SuppressWarnings("unused")
				Parameter pConfigurazioneDati = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_CONFIGURAZIONE_DATI_INVOCAZIONE, Costanti.CHECK_BOX_ENABLED_TRUE);
				Parameter pConfigurazioneAltroPorta = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_CONFIGURAZIONE_ALTRO_PORTA, Costanti.CHECK_BOX_ENABLED_TRUE);
				
				List<String> listaAzioniPDAssociataMappingNonDefault = null;
				if(!mapping.isDefault()) {
					listaAzioniPDAssociataMappingNonDefault = pdAssociata.getAzione().getAzioneDelegataList();
				}
				
				// spostata direttamente nell'elenco delle fruizioni
//				// nome con link al PortaDeletagataChange
//				DataElement de = new DataElement();
//				if(mapping.isDefault()) {
//					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CHANGE,pIdPD,pNomePD,pIdSoggPD, pIdAsps, pIdFruitore, pConfigurazioneDati);
//				}
//				de.setValue(mapping.isDefault() ? PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MAPPING_FRUIZIONE_PD_NOME_DEFAULT : mapping.getNome());
//				de.setIdToRemove(pdAssociata.getNome());
//				//de.setToolTip(StringUtils.isNotEmpty(pdAssociata.getDescrizione()) ? pdAssociata.getDescrizione() : pdAssociata.getNome());
//				e.addElement(de);
				
				if(gestioneConfigurazioni && mapping.isDefault() && allActionRedefined && (!showConnettoreLink)) {
					int numEntries = ricerca.getNumEntries(idLista);
					ricerca.setNumEntries(idLista, numEntries -1); 
					this.pd.setNumEntries(numEntries -1);
					continue; // non faccio vedere la riga "disconnessa"
				}
				
				boolean statoPD = pdAssociata.getStato().equals(StatoFunzionalita.ABILITATO);
				String statoPDallRedefined = null;
				String statoMapping = statoPD ? CostantiControlStation.LABEL_PARAMETRO_PORTA_ABILITATO_TOOLTIP : CostantiControlStation.LABEL_PARAMETRO_PORTA_DISABILITATO_TOOLTIP;
				boolean urlCambiaStato = true;
				if(mapping.isDefault() && allActionRedefined) {
					statoPD = false;
					statoPDallRedefined = "off";
					statoMapping = this.getLabelAllAzioniRidefiniteTooltip(serviceBindingMessage);
					urlCambiaStato = false;
				}
				
				// Nome Gruppo
				if(visualizzazioneTabs && (gestioneGruppi || listaParam.size()>1)) {
					DataElement de = new DataElement();
					
					de.setWidthPx(10);
					de.setType(DataElementType.CHECKBOX);
					
					de.setStatusToolTip(statoPD ? CostantiControlStation.LABEL_PARAMETRO_PORTA_ABILITATO_TOOLTIP_NO_ACTION : CostantiControlStation.LABEL_PARAMETRO_PORTA_DISABILITATO_TOOLTIP_NO_ACTION);
					if(statoPDallRedefined!=null) {
						de.setStatusType(statoPDallRedefined);
					}
					else {
						de.setStatusType(statoPD ? CheckboxStatusType.ABILITATO : CheckboxStatusType.DISABILITATO);
					}
					
					de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME_GRUPPO);
					de.setValue(mapping.getDescrizione());
					de.setStatusValue(mapping.getDescrizione());
					
					if(!mapping.isDefault()) {
	                       DataElementImage image = new DataElementImage();
	                       
	                       image.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CONFIGURAZIONE_CHANGE,pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore,pIdTAb);
	                       image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME_GRUPPO));
	                       image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
	                       
	                       de.addImage(image );
					}
					if(!mapping.isDefault()) 
	                       de.setIdToRemove(pdAssociata.getNome());
	               
					if(urlCambiaStato) {
						Parameter pAbilita = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ABILITA,  (statoPD ? Costanti.CHECK_BOX_DISABLED : Costanti.CHECK_BOX_ENABLED_TRUE));
						de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_ABILITAZIONE,pIdPD,pNomePD,pIdSoggPD, pIdAsps, pIdFruitore, pAbilita);
						
						DataElementImage image = new DataElementImage();
	                    image.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_ABILITAZIONE,pIdPD,pNomePD,pIdSoggPD, pIdAsps, pIdFruitore, pAbilita,pIdTAb);
	                    image.setToolTip(statoMapping);
	                    image.setImage(statoPD ? CostantiControlStation.ICONA_MODIFICA_TOGGLE_ON : CostantiControlStation.ICONA_MODIFICA_TOGGLE_OFF);
	                      
	                    de.addImage(image);
					}
					
					e.addElement(de);
				}
				
				if(gestioneConfigurazioni && !visualizzazioneTabs) {
					
					// Abilitato
					DataElement de = new DataElement();
					if(visualizzazioneTabs)
				        de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_STATO);
					
					de.setWidthPx(10);
					de.setType(DataElementType.CHECKBOX);
					if(visualizzazioneTabs) {
						de.setStatusValue(statoPD ? this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_ABILITATO) : this.getUpperFirstChar(CostantiControlStation.DEFAULT_VALUE_DISABILITATO));
						de.setStatusToolTip(statoPD ? CostantiControlStation.LABEL_PARAMETRO_PORTA_ABILITATO_TOOLTIP_NO_ACTION : CostantiControlStation.LABEL_PARAMETRO_PORTA_DISABILITATO_TOOLTIP_NO_ACTION);
						if(statoPDallRedefined!=null) {
							de.setStatusType(statoPDallRedefined);
						}
						else {
							de.setStatusType(statoPD ? CheckboxStatusType.ABILITATO : CheckboxStatusType.DISABILITATO);
						}
					}
					else {
						de.setToolTip(statoMapping);
					}
					if(statoPDallRedefined!=null) {
						de.setSelected(statoPDallRedefined);
					}
					else {
						de.setSelected(statoPD);
					}
					if(urlCambiaStato) {
						Parameter pAbilita = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ABILITA,  (statoPD ? Costanti.CHECK_BOX_DISABLED : Costanti.CHECK_BOX_ENABLED_TRUE));
						de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_ABILITAZIONE,pIdPD,pNomePD,pIdSoggPD, pIdAsps, pIdFruitore, pAbilita);
						
						DataElementImage image = new DataElementImage();
	                    image.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_ABILITAZIONE,pIdPD,pNomePD,pIdSoggPD, pIdAsps, pIdFruitore, pAbilita,pIdTAb);
	                    image.setToolTip(statoMapping);
	                    image.setImage(statoPD ? CostantiControlStation.ICONA_MODIFICA_TOGGLE_ON : CostantiControlStation.ICONA_MODIFICA_TOGGLE_OFF);
	                      
	                    de.setImage(image);
					}
					e.addElement(de);

				}
				
				
				// NomeGruppo
				if(!visualizzazioneTabs && (gestioneGruppi || listaParam.size()>1)) {
					
					DataElement de = new DataElement();
					de.setValue(mapping.getDescrizione());
					if(gestioneGruppi && !mapping.isDefault()) {
						de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CONFIGURAZIONE_CHANGE,pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore);
					}
					e.addElement(de);
				}
					
				// lista delle azioni
				if(gestioneGruppi || (visualizzazioneTabs && (listaParam.size()>1 || !mapping.isDefault()))) {
					
					List<String> listaAzioni = null;
					String nomiAzioni = null;
					long countAzioni = 0;
					if(!mapping.isDefault()) {
						listaAzioni = listaAzioniPDAssociataMappingNonDefault!= null ? listaAzioniPDAssociataMappingNonDefault : new ArrayList<String>();
						
						if(listaAzioni.size() > 0 && azioni.size()>0) {
							
							StringBuilder sb = new StringBuilder();
							Iterator<String> itAz = azioni.keySet().iterator();
							while (itAz.hasNext()) {
								String idAzione = (String) itAz.next();
								if(listaAzioni.contains(idAzione)) {
									if(sb.length() >0)
										sb.append(", ");
									
									sb.append(azioni.get(idAzione));
									countAzioni++;
								}
							}
							nomiAzioni = sb.toString();
							
						}
					}
					
					DataElement de = new DataElement();
					de.setSize(200);
					de.setIdToRemove(pdAssociata.getNome());
					
					if(visualizzazioneTabs) {
				        de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_ELENCO_AZIONI_GRUPPI_PREFIX+labelAzioni);
					}

					if(listaSenzaFiltro.size()>1) {
						DataElementImage image = new DataElementImage();
						
						if(!mapping.isDefault()) {
							de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_AZIONE_LIST,pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore);
							if(this.isModalitaCompleta()) {
								ServletUtils.setDataElementVisualizzaLabel(de, countAzioni);
							}
							else {
								de.setValue(nomiAzioni);
							}
							de.setToolTip(nomiAzioni);
							image.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_AZIONE_LIST,pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore,pIdTAb);
							image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_ELENCO_AZIONI_GRUPPI_PREFIX+labelAzioni));
							image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
							
							de.setImage(image);
						}
						else {
							if(actionNonRidefinite!=null && !actionNonRidefinite.isEmpty() && azioni.size()>0) {
								
								long countAzioniRidefinite = 0;
								StringBuilder sb = new StringBuilder();
								Iterator<String> itAz = azioni.keySet().iterator();
								while (itAz.hasNext()) {
									String idAzione = (String) itAz.next();
									if(actionNonRidefinite.contains(idAzione)) {
										if(sb.length() >0)
											sb.append(", ");
										
										sb.append(azioni.get(idAzione));
										countAzioniRidefinite++;
									}
								}
								String nomiAzioniNonRidefinite = sb.toString();
								de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_AZIONE_LIST,pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore);
								if(this.isModalitaCompleta()) {
									ServletUtils.setDataElementVisualizzaLabel(de, countAzioniRidefinite);
								}
								else {
									de.setValue(nomiAzioniNonRidefinite);
								}
								de.setToolTip(nomiAzioniNonRidefinite);
								
								image.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_AZIONE_LIST,pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore,pIdTAb);
								image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_ELENCO_AZIONI_GRUPPI_PREFIX+labelAzioni));
								image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
								
								de.setImage(image);
							}
							else {
								if(allActionRedefined) {
									de.setValue(this.getLabelAllAzioniRidefiniteTooltip(serviceBindingMessage));
								}
								else {
									de.setValue("-"); // ??
								}
							}
						}
					}
					else {
						if(org.openspcoop2.core.registry.constants.ServiceBinding.SOAP.equals(serviceBinding)) {
							de.setValue(CostantiControlStation.LABEL_TUTTE_AZIONI_DEFAULT);	
						}
						else {
							de.setValue(CostantiControlStation.LABEL_TUTTE_RISORSE_DEFAULT);	
						}
					}
					e.addElement(de);
					
				}

				if(gestioneConfigurazioni) {
				
					//if(gestioneFruitori) {
					// connettore	
					if(showConnettoreLink && !connettoreStatic) {
						DataElement de = new DataElement();
						if(visualizzazioneTabs)
							de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE);
						
						boolean ridefinito = false;
						
						Connettore connettore = null;
						if(!gestioneFruitori && mapping.isDefault()) {
							de.setValue("-");
						}
						else {
							Long idSoggettoLong = fru.getIdSoggetto();
							if(idSoggettoLong==null) {
								idSoggettoLong = this.soggettiCore.getIdSoggetto(fru.getNome(), fru.getTipo());
							}
							List<Parameter> listParameter = new ArrayList<>();
							
							String azioneConnettore =  null;
							if(listaAzioniPDAssociataMappingNonDefault!=null && listaAzioniPDAssociataMappingNonDefault.size()>0) {
								azioneConnettore = listaAzioniPDAssociataMappingNonDefault.get(0);
							}
							
							String servletConnettore = AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE;
							if(mapping.isDefault()) {
								if(visualizzazioneTabs) {
									de.setValue(this.getLabelConnettore(fru.getConnettore()));
								}
								else {
									ServletUtils.setDataElementVisualizzaLabel(de);
								}
								Parameter pId = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId()+"");
								Parameter pMyId = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID, fru.getId() + "");				
								Parameter actionIdPorta = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_FRUITORE_VIEW_CONNETTORE_MAPPING_AZIONE_ID_PORTA,pdAssociata.getId()+"");
								listParameter.add(actionIdPorta);
								listParameter.add(pId);
								listParameter.add(pMyId);
								listParameter.add(pIdSoggettoErogatore);
								listParameter.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE, idSoggettoLong + ""));
								listParameter.add(pIdTAb);
								if(azioneConnettore!=null && !"".equals(azioneConnettore)) {
									listParameter.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_FRUITORE_VIEW_CONNETTORE_MAPPING_AZIONE,azioneConnettore));
								}
								de.setUrl(servletConnettore, listParameter.toArray(new Parameter[1]));
								
								if(visualizzazioneTabs) {
									DataElementImage image = new DataElementImage();
									image.setUrl(servletConnettore, listParameter.toArray(new Parameter[1]));
									image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE));
									image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
									de.setImage(image);
								}
								
								connettore = fru.getConnettore();
							} else {
								if(azioneConnettore!=null && !"".equals(azioneConnettore)) {
									for (ConfigurazioneServizioAzione check : fru.getConfigurazioneAzioneList()) {
										if(check.getAzioneList().contains(azioneConnettore)) {
											ridefinito = true;
											connettore = check.getConnettore();
											break;
										}
									}
								}
								if(ridefinito) {
									servletConnettore = PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CONNETTORE_RIDEFINITO;
									if(visualizzazioneTabs) {
										de.setValue(this.getLabelConnettore(connettore));
									}
									else {
										de.setValue(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODALITA_CONNETTORE_RIDEFINITO);
									}
									de.setUrl(servletConnettore, pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore);
									String tooltipConnettore = this.getLabelConnettore(connettore);
									de.setToolTip(ConnettoriCostanti.LABEL_PARAMETRO_MODALITA_CONNETTORE_RIDEFINITO+CostantiControlStation.TOOLTIP_BREAK_LINE+tooltipConnettore);
								} else {
									servletConnettore = PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CONNETTORE_DEFAULT;
									if(visualizzazioneTabs) {
										de.setValue("["+org.openspcoop2.core.constants.Costanti.MAPPING_FRUIZIONE_PD_DESCRIZIONE_DEFAULT+"] "+this.getLabelConnettore(fru.getConnettore()));
									}
									else {
										de.setValue(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODALITA_CONNETTORE_DEFAULT);
									}
									de.setUrl(servletConnettore, pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore);
									String tooltipConnettore = this.getLabelConnettore(fru.getConnettore());
									de.setToolTip(ConnettoriCostanti.LABEL_PARAMETRO_MODALITA_CONNETTORE_DEFAULT+CostantiControlStation.TOOLTIP_BREAK_LINE+tooltipConnettore);
									
									connettore = fru.getConnettore();
								}
								
								if(visualizzazioneTabs) {
									DataElementImage image = new DataElementImage();
									image.setUrl(servletConnettore, pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore,pIdTAb);
									image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE));
									image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
									de.setImage(image);
								}
							}
							
						}
						de.allineaTdAlCentro();
						e.addElement(de);
						
						if(connettore!=null) {
							
							long idConnettore = connettore.getId();
							boolean checkConnettore = org.openspcoop2.pdd.core.connettori.ConnettoreCheck.checkSupported(connettore);
							if(checkConnettore) {
								if(!mapping.isDefault() && !ridefinito) {
									checkConnettore = false;
								}
							}
							
							if(visualizzazioneTabs) {
								if(checkConnettore) {	
									DataElementImage image = new DataElementImage();
									image.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_VERIFICA_CONNETTORE, pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore,pIdTAb,
											new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ID, idConnettore+""),
											new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI, "true"),
											new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO, "true"));
									image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_VERIFICA_TOOLTIP_CON_PARAMETRO, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE));
									image.setImage(CostantiControlStation.ICONA_VERIFICA);
									de.addImage(image);
								}
							} else {
								DataElement deVerificaConnettore = new DataElement();
								if(checkConnettore) {								
									deVerificaConnettore.setValue(CostantiControlStation.LABEL_VERIFICA_CONNETTORE_VALORE_LINK);
									deVerificaConnettore.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_VERIFICA_CONNETTORE, pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore,
											new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ID, idConnettore+""),
											new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI, "true"),
											new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO, "true"));
								}
								else {
									deVerificaConnettore.setValue("-");
								}
								deVerificaConnettore.allineaTdAlCentro();
								e.addElement(deVerificaConnettore);
							}
						}
					}
					//}

					// Controllo Accessi
					DataElement de = new DataElement();
					if(visualizzazioneTabs)
						de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONTROLLO_ACCESSI);
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CONTROLLO_ACCESSI, pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore);
					if(visualizzazioneTabs) {
						this.setStatoControlloAccessiPortaDelegata(protocollo, pdAssociata, de); 
					}
					else {
						String statoControlloAccessi = this.getStatoControlloAccessiPortaDelegata(protocollo, pdAssociata); 				
						de.setValue(statoControlloAccessi);
					}
					de.allineaTdAlCentro();
					if(visualizzazioneTabs) {
						DataElementImage image = new DataElementImage();
						image.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CONTROLLO_ACCESSI, pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore,pIdTAb);
						image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONTROLLO_ACCESSI));
						image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
						de.setImage(image);
					}
					e.addElement(de);
														
					// RateLimiting
					de = new DataElement();
					if(visualizzazioneTabs)
						de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RATE_LIMITING);
					de.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_LIST+"?"+
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_RUOLO_PORTA+"="+RuoloPolicy.DELEGATA.getValue()+"&"+
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_NOME_PORTA+"="+pdAssociata.getNome()+"&"+
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_SERVICE_BINDING+"="+serviceBindingMessage.name()
							);
					List<AttivazionePolicy> listaPolicy = null;
					if(contaListe || visualizzazioneTabs) {
						Search searchPolicy = new Search(true);
						listaPolicy = this.confCore.attivazionePolicyList(searchPolicy, RuoloPolicy.DELEGATA, pdAssociata.getNome());
					}
					if(visualizzazioneTabs) {
						this.setStatoRateLimiting(de, listaPolicy);
					}
					else {
						if(contaListe) {
							ServletUtils.setDataElementVisualizzaLabel(de, (long) listaPolicy.size() );
						}
						else {
							ServletUtils.setDataElementVisualizzaLabel(de);
						}
					}
					de.allineaTdAlCentro();
					if(visualizzazioneTabs) {
						DataElementImage image = new DataElementImage();
						image.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO_ATTIVAZIONE_POLICY_LIST, 
								new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_RUOLO_PORTA,RuoloPolicy.DELEGATA.getValue()),
								new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_NOME_PORTA,pdAssociata.getNome()),
								new Parameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_RATE_LIMITING_POLICY_GLOBALI_LINK_SERVICE_BINDING,serviceBindingMessage.name()),
								pIdTAb
								);
						image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RATE_LIMITING));
						image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
						de.setImage(image);
					}
					e.addElement(de);
					
					// validazione contenuti
					de = new DataElement();
					if(visualizzazioneTabs)
						de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI);
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI, pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore);
					if(visualizzazioneTabs) {
						setStatoValidazioneContenuti(de, pdAssociata.getValidazioneContenutiApplicativi(), apc.getFormatoSpecifica());
					}
					else {
						String statoValidazione = this.getStatoValidazionePortaDelegata(pdAssociata);
						de.setValue(statoValidazione);
					}
					de.allineaTdAlCentro();
					if(visualizzazioneTabs) {
						DataElementImage image = new DataElementImage();
						image.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI, pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore,pIdTAb);
						image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI));
						image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
						de.setImage(image);
					}
					e.addElement(de);
					
					// Response Caching
					de = new DataElement();
					if(visualizzazioneTabs)
						de.setLabel(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RESPONSE_CACHING);
					//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_RESPONSE_CACHING, pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore);
					if(visualizzazioneTabs) {
						setStatoCachingRisposta(de, pdAssociata.getResponseCaching(), configurazioneGenerale);
					}
					else {
						String statoResponseCaching = getStatoResponseCachingPortaDelegata(pdAssociata, false);
						de.setValue(statoResponseCaching);
					}
					de.allineaTdAlCentro();
					if(visualizzazioneTabs) {
						DataElementImage image = new DataElementImage();
						image.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_RESPONSE_CACHING, pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore,pIdTAb);
						image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RESPONSE_CACHING));
						image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
						de.setImage(image);
					}
					e.addElement(de);
					
					// Message Security
					if(visualizzaSicurezza) {
						de = new DataElement();
						if(visualizzazioneTabs)
							de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY);
						de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY, pIdPD, pIdSoggPD, pIdAsps, pIdFruitore);
						if(visualizzazioneTabs) {
							setStatoSicurezzaMessaggio(de, pdAssociata.getMessageSecurity(), configManager, propertiesSourceConfiguration);
						}
						else {
							String statoMessageSecurity = getStatoMessageSecurityPortaDelegata(pdAssociata);
							de.setValue(statoMessageSecurity);
						}
						if(visualizzazioneTabs) {
							DataElementImage image = new DataElementImage();
							image.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY, pIdPD, pIdSoggPD, pIdAsps, pIdFruitore,pIdTAb);
							image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY));
							image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
							de.setImage(image);
						}
						e.addElement(de);
					}
	
					// MTOM (solo per servizi SOAP)
					if(visualizzaMTOM) {
						de = new DataElement();
						if(visualizzazioneTabs)
							de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM);
						de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM, pIdPD, pIdSoggPD, pIdAsps, pIdFruitore);
						if(visualizzazioneTabs) {
							setStatoMTOM(de, pdAssociata.getMtomProcessor());
						}
						else {
							String statoMTOM = getStatoMTOMPortaDelegata(pdAssociata);
							de.setValue(statoMTOM);
						}
						de.allineaTdAlCentro();
						if(visualizzazioneTabs) {
							DataElementImage image = new DataElementImage();
							image.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM, pIdPD, pIdSoggPD, pIdAsps, pIdFruitore,pIdTAb);
							image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM));
							image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
							de.setImage(image);
						}
						e.addElement(de);
					}
					
					// trasformazioni
					de = new DataElement();
					if(visualizzazioneTabs)
						de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI);
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_TRASFORMAZIONI_LIST, pIdPD, pIdSoggPD, pIdAsps, pIdFruitore);
					if(visualizzazioneTabs) {
						setStatoTrasformazioni(de, pdAssociata.getTrasformazioni(), serviceBindingMessage);
					}
					else {
						if(contaListe) {
							long size = 0;
							if(pdAssociata.getTrasformazioni()!=null) {
								size = pdAssociata.getTrasformazioni().sizeRegolaList();
							}
							ServletUtils.setDataElementVisualizzaLabel(de, (long) size); 
						}
						else {
							ServletUtils.setDataElementVisualizzaLabel(de);
						}	
					}
					de.allineaTdAlCentro();
					if(visualizzazioneTabs) {
						DataElementImage image = new DataElementImage();
						image.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_TRASFORMAZIONI_LIST, pIdPD, pIdSoggPD, pIdAsps, pIdFruitore,pIdTAb);
						image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI));
						image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
						de.setImage(image);
					}
					e.addElement(de);
	
					// Correlazione applicativa
					if(visualizzaCorrelazione) {
						de = new DataElement();
						if(visualizzazioneTabs)
							de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRACCIAMENTO);
						de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA, pIdPD, pIdSoggPD, pIdAsps, pIdFruitore);
						if(visualizzazioneTabs) {
							setStatoTracciamento(de, pdAssociata.getCorrelazioneApplicativa(), 
									pdAssociata.getCorrelazioneApplicativaRisposta(), pdAssociata.getTracciamento(), configurazioneGenerale);	
						}
						else {
							String statoTracciamento = getStatoTracciamentoPortaDelegata(pdAssociata);
							de.setValue(statoTracciamento);
						}
						de.allineaTdAlCentro();
						if(visualizzazioneTabs) {
							DataElementImage image = new DataElementImage();
							image.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA, pIdPD, pIdSoggPD, pIdAsps, pIdFruitore,pIdTAb);
							image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRACCIAMENTO));
							image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
							de.setImage(image);
						}
						e.addElement(de);
					}
					
					// dump
					de = new DataElement();
					if(visualizzazioneTabs)
						de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_DUMP_CONFIGURAZIONE);
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_DUMP_CONFIGURAZIONE, pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore);
					if(visualizzazioneTabs) {
						setStatoDump(de, pdAssociata.getDump(), configurazioneGenerale);
					}
					else {
						String statoDump = getStatoDumpPortaDelegata(pdAssociata, false);
						de.setValue(statoDump);
					}
					de.allineaTdAlCentro();
					if(visualizzazioneTabs) {
						DataElementImage image = new DataElementImage();
						image.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_DUMP_CONFIGURAZIONE, pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore,pIdTAb);
						image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_DUMP_CONFIGURAZIONE));
						image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
						de.setImage(image);
					}
					e.addElement(de);
					
					// Protocol Properties
					if((this.isModalitaAvanzata() || this.porteDelegateCore.isProprietaFruizioni_showModalitaStandard())){
						de = new DataElement();
						if(visualizzazioneTabs)
							de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PROTOCOL_PROPERTIES);
						//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
						de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_PROPRIETA_PROTOCOLLO_LIST, pIdSoggPD, pIdPD,pIdAsps,pIdFruitore);
						if(visualizzazioneTabs) {
							setStatoProprieta(de, pdAssociata.sizeProprietaList());
						}
						else {
							if (contaListe) {
								int numProp = pdAssociata.sizeProprietaList();
								ServletUtils.setDataElementVisualizzaLabel(de, (long) numProp );
							} else
								ServletUtils.setDataElementVisualizzaLabel(de);
						}
						de.allineaTdAlCentro();
						if(visualizzazioneTabs) {
							DataElementImage image = new DataElementImage();
							image.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_PROPRIETA_PROTOCOLLO_LIST, pIdSoggPD, pIdPD,pIdAsps,pIdFruitore,pIdTAb);
							image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PROTOCOL_PROPERTIES));
							image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
							de.setImage(image);
						}
						e.addElement(de);
					}
				
					// Altro
					if(this.isModalitaAvanzata()){
						de = new DataElement();
						if(visualizzazioneTabs)
							de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_OPZIONI_AVANZATE);
						de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CHANGE,pIdPD,pNomePD,pIdSoggPD, pIdAsps, pIdFruitore, pConfigurazioneAltroPorta);
						if(visualizzazioneTabs) {
							setStatoOpzioniAvanzate(de, 
									protocollo, serviceBindingMessage,
									pdAssociata.getAllegaBody(), pdAssociata.getScartaBody(), 
									pdAssociata.getIntegrazione(), null, 
									pdAssociata.getStateless(), pdAssociata.getLocalForward(), 
									pdAssociata.getRicevutaAsincronaSimmetrica(), pdAssociata.getRicevutaAsincronaAsimmetrica(),
									pdAssociata.getGestioneManifest());
						}
						else {
							ServletUtils.setDataElementVisualizzaLabel(de);
						}
						if(visualizzazioneTabs) {
							DataElementImage image = new DataElementImage();
							image.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CHANGE,pIdPD,pNomePD,pIdSoggPD, pIdAsps, pIdFruitore, pConfigurazioneAltroPorta,pIdTAb);
							image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_OPZIONI_AVANZATE));
							image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
							de.setImage(image);
						}
						e.addElement(de);
					}
					
					// pd exdended list
					if(extendedServletList!=null && extendedServletList.showExtendedInfo(this.request, this.session)){
						de = new DataElement();
						if(visualizzazioneTabs)
							de.setLabel(extendedServletList.getListTitle(this));
						de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_EXTENDED_LIST, pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore);
						if(visualizzazioneTabs) {
							int numExtended = extendedServletList.sizeList(pdAssociata);
							setStatoExtendedList(de, numExtended);
						}
						else {
							if (contaListe) {
								int numExtended = extendedServletList.sizeList(pdAssociata);
								ServletUtils.setDataElementVisualizzaLabel(de,Long.valueOf(numExtended));
							} else
								ServletUtils.setDataElementVisualizzaLabel(de);
						}
						de.allineaTdAlCentro();
						if(visualizzazioneTabs) {
							DataElementImage image = new DataElementImage();
							image.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_EXTENDED_LIST, pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore,pIdTAb);
							image.setToolTip(MessageFormat.format(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO,extendedServletList.getListTitle(this)));
							image.setImage(CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE);
							de.setImage(image);
						}
						e.addElement(de);
					}
					
				}
				
				dati.addElement(e);
				idTab ++;
			}

			this.pd.setDati(dati);
			if(visualizzazioneTabs) {
		        this.pd.setSelect(true);
		        if(showBottoneAggiungiNuovaConfigurazione) {
		        	this.pd.setAddButton(true);
		        }
		        else {
		        	this.pd.setAddButton(false);
		        }
		        if(lista.size() > 1)
		                this.pd.setRemoveButton(true);
		        else 
		                this.pd.setRemoveButton(false);
			} else {
				if(gestioneGruppi) {
					if(listaSenzaFiltro!=null && listaSenzaFiltro.size()>1) {
						this.pd.setRemoveButton(true);
						this.pd.setSelect(true);
					}
					else {
						this.pd.setRemoveButton(false);
						this.pd.setSelect(false);
					}
					this.pd.setAddButton(true);
				}
				else {
					this.pd.setAddButton(false);
					this.pd.setRemoveButton(false);
					this.pd.setSelect(false);
				}
			}

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}


	

	private boolean addInfoCorrelata(TipoOperazione tipoOp, String portType, boolean modificaAbilitata, String servcorr, String oldStato,
			String tipoProtocollo, ServiceBinding serviceBinding, Vector<DataElement> dati) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		boolean isModalitaAvanzata = this.isModalitaAvanzata();
		
		boolean infoCorrelataShow = false;
		
		DataElement de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_SERVIZIO_CORRELATO);
		if( this.core.isShowCorrelazioneAsincronaInAccordi() && 
				( !isModalitaAvanzata || (portType!=null && !"".equals(portType) && !"-".equals(portType)) ) ){
			de.setType(DataElementType.HIDDEN);
		}
		else if( tipoOp.equals(TipoOperazione.ADD) || modificaAbilitata ){
			if(this.core.isProfiloDiCollaborazioneAsincronoSupportatoDalProtocollo(tipoProtocollo,serviceBinding)) {
				de.setType(DataElementType.CHECKBOX);
				infoCorrelataShow = true;
			}
			else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(Costanti.CHECK_BOX_DISABLED);
			}
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
			if(tipoOp.equals(TipoOperazione.CHANGE) && (this.isShowGestioneWorkflowStatoDocumenti() 
					&&  StatiAccordo.finale.toString().equals(oldStato) )){
				de.setValue(Costanti.CHECK_BOX_DISABLED);
			}
		}
		dati.addElement(de);
		
		if( (tipoOp.equals(TipoOperazione.CHANGE) &&
				(this.isShowGestioneWorkflowStatoDocumenti() &&  StatiAccordo.finale.toString().equals(oldStato) )) 
				||
				(this.core.isShowCorrelazioneAsincronaInAccordi() &&
						( !isModalitaAvanzata || 
								(portType!=null && !"".equals(portType) && !"-".equals(portType)) )) ){
			DataElement deLabel = new DataElement();
			deLabel.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_TIPOLOGIA_SERVIZIO);
			deLabel.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_SERVIZIO_CORRELATO_LABEL);
			if (  (servcorr != null) && ((servcorr.equals(Costanti.CHECK_BOX_ENABLED)) || servcorr.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_ABILITATO)) ) {
				deLabel.setValue(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_CORRELATO);
			}else{
				deLabel.setValue(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_NORMALE);
			}
			if(this.core.isProfiloDiCollaborazioneAsincronoSupportatoDalProtocollo(tipoProtocollo,serviceBinding)){	
				deLabel.setType(DataElementType.TEXT);
				infoCorrelataShow = true;
			} else {
				deLabel.setType(DataElementType.HIDDEN);
			}
			dati.addElement(deLabel);
		}
		
		return infoCorrelataShow;
	}

	public Vector<DataElement> addServiziToDati(Vector<DataElement> dati, String nomeServizio, String tipoServizio, String oldNomeServizio, String oldTipoServizio,
			String provider, String tipoSoggetto, String nomeSoggetto, String[] soggettiList, String[] soggettiListLabel,
			String accordo, ServiceBinding serviceBinding, org.openspcoop2.protocol.manifest.constants.InterfaceType interfaceType, String[] accordiList, String[] accordiListLabel, String servcorr, BinaryParameter wsdlimpler,
			BinaryParameter wsdlimplfru, TipoOperazione tipoOp, String id, List<String> tipi, String profilo, String portType, 
			String[] ptList, boolean privato, String uriAccordo, String descrizione, long idSoggettoErogatore,
			String statoPackage,String oldStato,String versione,
			List<String> versioni,boolean validazioneDocumenti, 
			String [] saSoggetti, String nomeSA, boolean generaPACheckSoggetto,
			List<AccordoServizioParteComune> asCompatibili,
			String controlloAccessiStato,
			String erogazioneRuolo,String erogazioneAutenticazione,String erogazioneAutenticazioneOpzionale, TipoAutenticazionePrincipal erogazioneAutenticazionePrincipal, List<String> erogazioneAutenticazioneParametroList, String erogazioneAutorizzazione, boolean erogazioneIsSupportatoAutenticazioneSoggetti,
			String erogazioneAutorizzazioneAutenticati, String erogazioneAutorizzazioneRuoli, String erogazioneAutorizzazioneRuoliTipologia, String erogazioneAutorizzazioneRuoliMatch,
			List<String> soggettiAutenticati, List<String> soggettiAutenticatiLabel, String soggettoAutenticato,
			String tipoProtocollo, List<String> listaTipiProtocollo,
			String[] soggettiFruitoriList, String[] soggettiFruitoriListLabel, String providerSoggettoFruitore, String tipoSoggettoFruitore, String nomeSoggettoFruitore,
			String fruizioneServizioApplicativo,String fruizioneRuolo,String fruizioneAutenticazione,String fruizioneAutenticazioneOpzionale, TipoAutenticazionePrincipal fruizioneAutenticazionePrincipal, List<String> fruizioneAutenticazioneParametroList, String fruizioneAutorizzazione,
			String fruizioneAutorizzazioneAutenticati,String fruizioneAutorizzazioneRuoli, String fruizioneAutorizzazioneRuoliTipologia, String fruizioneAutorizzazioneRuoliMatch,
			List<String> saList,String gestioneToken, String[] gestioneTokenPolicyLabels, String[] gestioneTokenPolicyValues,
			String gestioneTokenPolicy, String gestioneTokenOpzionale, 
			String gestioneTokenValidazioneInput, String gestioneTokenIntrospection, String gestioneTokenUserInfo, String gestioneTokenForward,
			String autenticazioneTokenIssuer,String autenticazioneTokenClientId,String autenticazioneTokenSubject,String autenticazioneTokenUsername,String autenticazioneTokenEMail,
			String autorizzazione_token, String autorizzazione_tokenOptions,
			String autorizzazioneScope,  String scope, String autorizzazioneScopeMatch,BinaryParameter allegatoXacmlPolicy,
			boolean moreThenOneImplementation, String canaleStato, String canaleAPI, String canale, List<CanaleConfigurazione> canaleList, boolean gestioneCanaliEnabled) throws Exception{

		String tipologia = ServletUtils.getObjectFromSession(this.session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
		boolean gestioneFruitori = false;
		boolean gestioneErogatori = false;
		if(tipologia!=null) {
			if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
				gestioneFruitori = true;
			}
			else if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia)) {
				gestioneErogatori = true;
			}
		}
		
		boolean confirmInProgress = false;
		if(moreThenOneImplementation) {
			DataElement de = new DataElement();
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_CONFERMA_MODIFICA_DATI_SERVIZIO);
			de.setValue("true");
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);
			
			if(this.getPostBackElementName()==null || "".equals(this.getPostBackElementName())) {
				String tmp = this.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_CONFERMA_MODIFICA_DATI_SERVIZIO);
				if(tmp!=null && !"".equals(tmp)) {
					confirmInProgress = true;
				}
			}
		}
		
		if(gestioneFruitori) {
			
			DataElement de = new DataElement();
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SOGGETTO_FRUITORE);
			de.setValue(tipoSoggettoFruitore);
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);
			
			de = new DataElement();
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SOGGETTO_FRUITORE);
			de.setValue(nomeSoggettoFruitore);
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);
		}
		
		String tmpModificaAPI = this.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_API);
		Boolean showModificaAPIErogazioniFruizioniView = null;
		if(tmpModificaAPI!=null) {
			showModificaAPIErogazioniFruizioniView = "true".equals(tmpModificaAPI);
		
			DataElement de = new DataElement();
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_API);
			de.setValue(tmpModificaAPI);
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);
		}
		
		String tmpCambiaAPI = this.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_CAMBIA_API);
		boolean cambiaAPI = false;
		if(tmpCambiaAPI!=null) {
			cambiaAPI = "true".equals(tmpCambiaAPI);
			
			DataElement de = new DataElement();
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_CAMBIA_API);
			de.setValue(tmpCambiaAPI);
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);
		}
		
		String tmpModificaProfilo = this.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_PROFILO);
		boolean modificaProfilo = false;
		if(tmpModificaProfilo!=null) {
			modificaProfilo = "true".equals(tmpModificaProfilo);
			
			DataElement de = new DataElement();
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_PROFILO);
			de.setValue(tmpModificaProfilo);
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);
		}
		
		Boolean showInformazioniGeneraliErogazioniFruizioniView = null;
		if(gestioneFruitori || gestioneErogatori) {
			if(showModificaAPIErogazioniFruizioniView!=null && showModificaAPIErogazioniFruizioniView) {
				showInformazioniGeneraliErogazioniFruizioniView = false;
			}
			else if(modificaProfilo) {
				showInformazioniGeneraliErogazioniFruizioniView = false;
				showModificaAPIErogazioniFruizioniView = false; // forzo il false
			}
			else if(cambiaAPI) {
				showInformazioniGeneraliErogazioniFruizioniView = false;
				showModificaAPIErogazioniFruizioniView = false; // forzo il false
			}
			else {
				showInformazioniGeneraliErogazioniFruizioniView = true;
			}
		}
		
		
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

		boolean creaDataElementVersione = this.apsCore.isSupportatoVersionamentoAccordiServizioParteSpecifica(tipoProtocollo);
		boolean visualizzaSceltaVersioneServizio = creaDataElementVersione;
		if(this.isModalitaStandard() && TipoOperazione.ADD.equals(tipoOp)) {
			visualizzaSceltaVersioneServizio = false;
		}

		boolean modificaAbilitata = ( (this.isShowGestioneWorkflowStatoDocumenti()==false) || (StatiAccordo.finale.toString().equals(oldStato)==false) );

		boolean isModalitaAvanzata = this.isModalitaAvanzata();

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

		boolean showReferente = this.apcCore.isSupportatoSoggettoReferente(tipoProtocollo);
		
		boolean showPortiAccesso = this.apcCore.showPortiAccesso(tipoProtocollo, serviceBinding, interfaceType);
		
		DataElement de = new DataElement();
		if(gestioneFruitori || gestioneErogatori) {
			if((showModificaAPIErogazioniFruizioniView!=null && showModificaAPIErogazioniFruizioniView) || cambiaAPI) {
				de.setLabel(asLabel);
			}
			else {
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_INFO_GENERALI);
			}
		}
		else {
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_INFO_GENERALI);
		}
		if(modificaProfilo) {
			de.setType(DataElementType.HIDDEN);
		}
		else {
			de.setType(DataElementType.TITLE);
		}
		dati.addElement(de);

		// Gestione del tipo protocollo per la maschera add
		de = new DataElement();
		if( TipoOperazione.ADD.equals(tipoOp) && (listaTipiProtocollo != null && listaTipiProtocollo.size() > 1)){
			de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_PROTOCOLLO);
			de.setValues(listaTipiProtocollo);
			de.setLabels(this.getLabelsProtocolli(listaTipiProtocollo));
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
		
		
		// Selezione Soggetto Operativo
		if(gestioneFruitori) {
			
			boolean showSoggettoFruitoreInFruizioni = this.core.isMultitenant() && 
					!this.isSoggettoMultitenantSelezionato();
			
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SOGGETTO_FRUITORE);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE);
			if (tipoOp.equals(TipoOperazione.ADD) && showSoggettoFruitoreInFruizioni) {
				de.setType(DataElementType.SELECT);
				de.setValues(soggettiFruitoriList);
				de.setLabels(soggettiFruitoriListLabel);
				de.setPostBack(true);
				de.setSelected(providerSoggettoFruitore);
				dati.addElement(de);
			} else {
				de.setValue(providerSoggettoFruitore);
				de.setType(DataElementType.HIDDEN);
				dati.addElement(de);
	
				boolean showSoggettoInChange = false; // viene fatto vedere nella maschera riassuntiva
				if(showSoggettoFruitoreInFruizioni && showSoggettoInChange) {
					de = new DataElement();
					de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SOGGETTO_FRUITORE);
					de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE_AS_TEXT);
					de.setType(DataElementType.TEXT);
					de.setValue(this.getLabelNomeSoggetto(tipoProtocollo, tipoSoggettoFruitore, nomeSoggettoFruitore));
					dati.addElement(de);
				}
			}
		}
		else {
			// anche in modalità completa
			
			boolean showSoggettoErogatoreInErogazioni = this.core.isMultitenant() && 
					!this.isSoggettoMultitenantSelezionato();
			
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SOGGETTO_EROGATORE);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_EROGATORE);
			if (tipoOp.equals(TipoOperazione.ADD) && (showSoggettoErogatoreInErogazioni)) {
				de.setType(DataElementType.SELECT);
				de.setValues(soggettiList);
				de.setLabels(soggettiListLabel);
				de.setPostBack(true);
				de.setSelected(provider);
				dati.addElement(de);
			} else {
				de.setValue(provider);
				de.setType(DataElementType.HIDDEN);
				dati.addElement(de);

				boolean showSoggettoInChange = false; // viene fatto vedere nella maschera riassuntiva
				if(!gestioneErogatori || (showSoggettoErogatoreInErogazioni && showSoggettoInChange)) {
					de = new DataElement();
					de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_PROVIDER_EROGATORE);
					de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_TEXT);
					de.setType(DataElementType.TEXT);
					de.setValue(this.getLabelNomeSoggetto(tipoProtocollo, tipoSoggetto, nomeSoggetto));
					dati.addElement(de);
				}
			}
		}
		
		if(this.isModalitaCompleta() || tipoOp.equals(TipoOperazione.ADD) || cambiaAPI) {
			de = new DataElement();
			de.setLabel(asLabel);
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
		}
		
		// Accordo 
		//if(tipoOp.equals("add") || modificaAbilitata){
		IDAccordo idAccordoParteComune = null;
		boolean apiChanged = false;
		if(tipoOp.equals(TipoOperazione.ADD)){
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_ACCORDO_PARTE_COMUNE_NOME);
			de.setType(DataElementType.SELECT);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ACCORDO);
			de.setValues(accordiList);
			de.setLabels(accordiListLabel);
			//			de.setOnChange("CambiaAccordoServizio('" + tipoOp + "')");
			de.setPostBack(true);
			if (accordo != null)
				de.setSelected(accordo);
			dati.addElement(de);
		}
		else if(cambiaAPI && accordiListLabel!=null && accordiListLabel.length>0) {
			
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_ACCORDO_PARTE_COMUNE_NOME_ATTUALE);
			de.setType(DataElementType.TEXT);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ACCORDO+"__LABEL");
			de.setValue(accordiListLabel[0]);
			dati.addElement(de);
			
			if(accordiListLabel.length>1) {
				String [] newAccordi = new String[accordiListLabel.length];
				String [] newAccordiLabel = new String[accordiListLabel.length];
				for (int i = 0; i < accordiListLabel.length; i++) {
					newAccordi[i] = accordiList[i];
					if(i>0) {
						newAccordiLabel[i] = accordiListLabel[i];
					}
					else {
						newAccordiLabel[i] = "-";
					}
				}
				
				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_ACCORDO_PARTE_COMUNE_NOME_NUOVO);
				de.setType(DataElementType.SELECT);
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ACCORDO);
				de.setValues(newAccordi);
				de.setLabels(newAccordiLabel);
				//			de.setOnChange("CambiaAccordoServizio('" + tipoOp + "')");
				de.setPostBack(true);
				if (accordo != null)
					de.setSelected(accordo);
				dati.addElement(de);
			}
			else {
				de = new DataElement();
				de.setType(DataElementType.HIDDEN);
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ACCORDO);
				de.setValue(accordo);
				dati.addElement(de);
			}
			
			if(accordo!=null) {
				for (int i = 0; i < accordiList.length; i++) {
					if(accordo.equals(accordiList[i])) {
						apiChanged = i>0;
						break;
					}
				}
			}
		}
		else{
			if(!modificaAbilitata || 
					(asCompatibili==null || asCompatibili.size()<=1) || 
					(showModificaAPIErogazioniFruizioniView!=null && !showModificaAPIErogazioniFruizioniView)){
				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_ACCORDO);
				de.setType(DataElementType.HIDDEN);
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ACCORDO);
				de.setValue(accordo);
				dati.addElement(de);
			}
				
			idAccordoParteComune = this.idAccordoFactory.getIDAccordoFromUri(uriAccordo);
			
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_ACCORDO_PARTE_COMUNE_REFERENTE);
			if(showReferente && (showModificaAPIErogazioniFruizioniView==null || showModificaAPIErogazioniFruizioniView)) {
				de.setType(DataElementType.TEXT);
			}
			else {
				de.setType(DataElementType.HIDDEN);
			}
			de.setName("param_"+AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_ACCORDO_PARTE_COMUNE_REFERENTE );
			de.setValue(idAccordoParteComune.getSoggettoReferente().toString());
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_ACCORDO_PARTE_COMUNE_NOME);
			if(showModificaAPIErogazioniFruizioniView==null || showModificaAPIErogazioniFruizioniView) {
				de.setType(DataElementType.TEXT);
			}
			else {
				de.setType(DataElementType.HIDDEN);
			}
			de.setName("param_"+AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_ACCORDO_PARTE_COMUNE_NOME );
			de.setValue(idAccordoParteComune.getNome());
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_ACCORDO_PARTE_COMUNE_VERSIONE);
			if(!modificaAbilitata || 
					(asCompatibili==null || asCompatibili.size()<=1) || 
					(showModificaAPIErogazioniFruizioniView!=null && !showModificaAPIErogazioniFruizioniView)){
				if((showModificaAPIErogazioniFruizioniView!=null && !showModificaAPIErogazioniFruizioniView)) {
					de.setType(DataElementType.HIDDEN);
				}
				else {
					de.setType(DataElementType.TEXT);
				}
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
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ACCORDO);
				if(showModificaAPIErogazioniFruizioniView==null || showModificaAPIErogazioniFruizioniView) {
					if(confirmInProgress) {
						de.setType(DataElementType.HIDDEN);
						
						DataElement deLabel = new DataElement();
						deLabel.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_ACCORDO_PARTE_COMUNE_VERSIONE);
						deLabel.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ACCORDO+"__LABEL");
						deLabel.setType(DataElementType.TEXT);
						for (int i = 0; i < accordiCompatibiliList.length; i++) {
							if(accordo.equals(accordiCompatibiliList[i])) {
								deLabel.setValue(accordiCompatibiliLabelList[i]);
								break;
							}
						}
						dati.addElement(deLabel);
					}
					else {
						de.setType(DataElementType.SELECT);
						de.setValues(accordiCompatibiliList);
						de.setLabels(accordiCompatibiliLabelList);
						//if(showModificaAPIErogazioniFruizioniView==null) {
						// Lasciare il postback, l'evento serve ad allineare la versione dell'API con la versione del servizio in automatico
						de.setPostBack(true);
						//}
						de.setSelected(accordo);
					}
				}
				else {
					de.setType(DataElementType.HIDDEN);
				}
				de.setValue(accordo);
			}
			dati.addElement(de);
			
		}

		switch(serviceBinding) {
		case REST:
			
			if(showModificaAPIErogazioniFruizioniView==null || showModificaAPIErogazioniFruizioniView) {
				de = new DataElement();
				de.setType(DataElementType.TEXT);
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_SERVICE_BINDING);
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PORT_TYPE+"__LABEL");
				de.setValue(CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_REST);
				dati.addElement(de);
			}
			
			//Servizio (portType)  nascosto nel caso REST
			de = new DataElement();
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PORT_TYPE);
			de.setValue(portType);
			dati.addElement(de);
			break;
		case SOAP:
		default:
			
			if(showModificaAPIErogazioniFruizioniView==null || showModificaAPIErogazioniFruizioniView) {
				de = new DataElement();
				de.setType(DataElementType.TEXT);
				de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_SERVICE_BINDING);
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PORT_TYPE+"__LABEL");
				de.setValue(CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_SOAP);
				dati.addElement(de);
			}
			
			//Servizio (portType)  visibile nel caso SOAP
			if (ptList != null) {
				if(tipoOp.equals(TipoOperazione.ADD) || modificaAbilitata || apiChanged){
					de = new DataElement();
					de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SERVIZIO);
					de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PORT_TYPE);
					if(showInformazioniGeneraliErogazioniFruizioniView==null || showInformazioniGeneraliErogazioniFruizioniView || apiChanged) {
						if(showInformazioniGeneraliErogazioniFruizioniView!=null) {
							de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SERVIZIO_SOAP);				
						}
						de.setType(DataElementType.SELECT);
						de.setValues(ptList);
						de.setLabels(ptList);
						de.setSelected(portType);
						de.setPostBack(true);
						if (!isModalitaAvanzata || this.apsCore.isPortTypeObbligatorioImplementazioniSOAP()) {
							de.setRequired(true);
						}
					}
					else {
						if(showModificaAPIErogazioniFruizioniView!=null && showModificaAPIErogazioniFruizioniView) {
							de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SERVIZIO_SOAP);				
							de.setType(DataElementType.TEXT);
						}
						else {
							de.setType(DataElementType.HIDDEN);
						}
						de.setValue(portType);
					}
					dati.addElement(de);
				}else{
					de = new DataElement();
					de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SERVIZIO);
					de.setType(DataElementType.HIDDEN);
					de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PORT_TYPE);
					de.setValue(portType);
					dati.addElement(de);

					de = new DataElement();
					de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SERVIZIO);
					if(showModificaAPIErogazioniFruizioniView==null || showModificaAPIErogazioniFruizioniView) {
						de.setType(DataElementType.TEXT);
					}
					else {
						de.setType(DataElementType.HIDDEN);
					}
					de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PORT_TYPE_LABEL);
					de.setValue(portType);
					dati.addElement(de);
				}
			}else{
				de = new DataElement();
				de.setType(DataElementType.HIDDEN );
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PORT_TYPE);
				dati.addElement(de);
			}
			
			break;
		}
		

		

		//Sezione Servizio

		Vector<DataElement> datiCorrelati = new Vector<>();
		boolean showInfoCorrelata = this.addInfoCorrelata(tipoOp, portType, modificaAbilitata, servcorr, oldStato,
				tipoProtocollo, serviceBinding, datiCorrelati);
		
		boolean modificaAbilitataOrOperazioneAdd = tipoOp.equals(TipoOperazione.ADD) || modificaAbilitata;
		
		boolean showSceltaNomeServizioDisabilitata =false;
		if(TipoOperazione.ADD.equals(tipoOp)) {
			if(!isModalitaAvanzata) {
				showSceltaNomeServizioDisabilitata = true;
			}
		}
		else {
			// change
			if(gestioneErogatori || gestioneFruitori) {
				showSceltaNomeServizioDisabilitata = false; // lo vogliamo poter modificare sempre
			}
			else {
				if(!isModalitaAvanzata) {
					if((ServiceBinding.SOAP.equals(serviceBinding) && nomeServizio!=null && nomeServizio.equals(portType)) ||
						(ServiceBinding.REST.equals(serviceBinding) && nomeServizio!=null && nomeServizio.equals(idAccordoParteComune.getNome()))) {
						showSceltaNomeServizioDisabilitata = true;
					}
				}
			}
		}
		
		
		boolean showFlagPrivato = this.core.isShowFlagPrivato() &&  (tipoOp.equals(TipoOperazione.ADD) || 
				modificaAbilitata) && isModalitaAvanzata;
		
		boolean showFlagPrivatoLabel = this.core.isShowFlagPrivato() && tipoOp.equals(TipoOperazione.CHANGE ) && 
			(this.isShowGestioneWorkflowStatoDocumenti() && 
					StatiAccordo.finale.toString().equals(oldStato) ) &&
					isModalitaAvanzata;
		
		boolean showTipoServizio = (this.apsCore.getTipiServiziGestitiProtocollo(tipoProtocollo,serviceBinding).size()>1);
		
		boolean showVersioneProtocollo = modificaAbilitataOrOperazioneAdd && (this.apsCore.getVersioniProtocollo(tipoProtocollo).size()>1);
		
		
		boolean showLabelServizio = this.isModalitaAvanzata() || 
				showTipoServizio ||
				(!showSceltaNomeServizioDisabilitata) ||
				visualizzaSceltaVersioneServizio ||
				showInfoCorrelata ||
				showFlagPrivato ||
				showFlagPrivatoLabel ||
				showVersioneProtocollo ||
				(this.isShowGestioneWorkflowStatoDocumenti() && (TipoOperazione.CHANGE.equals(tipoOp) || this.isModalitaAvanzata()));
		
		if(showLabelServizio && 
				showInformazioniGeneraliErogazioniFruizioniView==null){
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SERVIZIO);
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
		}

		//if(isModalitaAvanzata || gestioneErogatori || gestioneFruitori){
		if(isModalitaCompleta()) {
			
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_DESCRIZIONE);
			de.setType(DataElementType.TEXT_EDIT);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_DESCRIZIONE);
			de.setSize(getSize());
			de.setValue(descrizione!=null ? descrizione : "");
			if( !modificaAbilitata && StringUtils.isBlank(descrizione))
				de.setValue("");
			
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


		if(showTipoServizio) {
			if(modificaAbilitataOrOperazioneAdd){
				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_TIPO  );
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO);
				if((showInformazioniGeneraliErogazioniFruizioniView==null || showInformazioniGeneraliErogazioniFruizioniView)) {
					de.setValues(tipiLabel);
					de.setSelected(tipoServizio);
					de.setType(DataElementType.SELECT);
					de.setSize(this.getSize());
					if(tipoOp.equals(TipoOperazione.ADD)) {
						de.setPostBack(true);
					}
				}
				else {
					de.setType(DataElementType.HIDDEN);
					de.setValue(tipoServizio);
				}
				dati.addElement(de);
			}else{
				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_TIPO);
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO);
				de.setValue(tipoServizio);
				if((showInformazioniGeneraliErogazioniFruizioniView==null || showInformazioniGeneraliErogazioniFruizioniView)) {
					de.setType(DataElementType.TEXT);
				}
				else {
					de.setType(DataElementType.HIDDEN);
				}
				dati.addElement(de);
			}
		}else{
			de = new DataElement();
			de.setValue(tipoServizio);
			de.setType(DataElementType.HIDDEN);
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_TIPO);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO);
			dati.addElement(de);
		}

		//}

		if(!showSceltaNomeServizioDisabilitata && cambiaAPI) {
			de = new DataElement();
			if(gestioneErogatori) {
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SINGOLO);
			}
			if(gestioneFruitori) {
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORE);
			}
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
		}
		
		if (showSceltaNomeServizioDisabilitata ) {
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
			if(tipoOp.equals(TipoOperazione.ADD) && isModalitaAvanzata() && (gestioneFruitori || gestioneErogatori)){
				if(gestioneErogatori) {
					de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_NOME_EROGAZIONE);
				}
				else {
					de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_NOME_FRUIZIONE);
				}
			}
			else {
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_NOME_FILE);
			}
			if (nomeServizio == null) {
				de.setValue("");
			} else {
				de.setValue(nomeServizio);
			}
			if((showInformazioniGeneraliErogazioniFruizioniView==null || showInformazioniGeneraliErogazioniFruizioniView) || cambiaAPI ) {
				if(cambiaAPI && !apiChanged) { // else rientrano in modificaAbilitata
					de.setType(DataElementType.TEXT);
				}
				else if(tipoOp.equals(TipoOperazione.ADD) || modificaAbilitata){
					de.setType(DataElementType.TEXT_EDIT);
					de.setRequired(true);
				}else{
					de.setType(DataElementType.TEXT);
				}
			}
			else {
				de.setType(DataElementType.HIDDEN);
			}
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO  );
			de.setSize(this.getSize());
			dati.addElement(de);
		}
		
		if(creaDataElementVersione){

			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_VERSIONE);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VERSIONE);
			String versioneSelezionata = ((versione==null || "".equals(versione)) ? "1" : versione);
			if(idAccordoParteComune==null) {
				idAccordoParteComune = this.idAccordoFactory.getIDAccordoFromUri(uriAccordo);
			}
			if( TipoOperazione.ADD.equals(tipoOp) && this.isModalitaStandard() ) {
				versioneSelezionata = idAccordoParteComune.getVersione().intValue()+"";
			}
			boolean versioneAllineataAccordoParteComune = false;
			if(TipoOperazione.CHANGE.equals(tipoOp) && !cambiaAPI) {
				versioneAllineataAccordoParteComune = (idAccordoParteComune.getVersione().intValue()+"").equals(versioneSelezionata); // modifica gestita in servlet chage, vedi postback PARAMETRO_APS_ACCORDO
			}
			
			de.setValue(versioneSelezionata);
			if(visualizzaSceltaVersioneServizio){
				if((showInformazioniGeneraliErogazioniFruizioniView==null || showInformazioniGeneraliErogazioniFruizioniView) || cambiaAPI ) {
					if(cambiaAPI && !apiChanged) { // else rientrano in modificaAbilitata
						de.setType(DataElementType.TEXT);
					}
					else if( modificaAbilitata ){
						if(this.isModalitaStandard() && versioneAllineataAccordoParteComune) {
							de.setType(DataElementType.HIDDEN);
						}
						else {
							de = this.getVersionDataElement(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_VERSIONE, 
									AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VERSIONE, 
									versioneSelezionata, false);
						}
					}else{
						de.setType(DataElementType.TEXT);
					}
				}
				else{
					de.setType(DataElementType.HIDDEN);
				}
			}else{
				de.setType(DataElementType.HIDDEN);
			}
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VERSIONE);
			dati.addElement(de);
			
		}
		
		if((showInformazioniGeneraliErogazioniFruizioniView!=null && !showInformazioniGeneraliErogazioniFruizioniView)) {
			for (DataElement dataElement : datiCorrelati) {
				dataElement.setType(DataElementType.HIDDEN);
			}
		}
		dati.addAll(datiCorrelati);

		de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_PRIVATO);
		if (showFlagPrivato && (showInformazioniGeneraliErogazioniFruizioniView==null || showInformazioniGeneraliErogazioniFruizioniView)) {
			de.setType(DataElementType.CHECKBOX);
			de.setSelected(privato ? Costanti.CHECK_BOX_ENABLED : "");
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(privato ? Costanti.CHECK_BOX_ENABLED : "");
		}
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PRIVATO);
		dati.addElement(de);

		if(showFlagPrivatoLabel && (showInformazioniGeneraliErogazioniFruizioniView==null || showInformazioniGeneraliErogazioniFruizioniView)){
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
		if(showVersioneProtocollo && (showInformazioniGeneraliErogazioniFruizioniView==null || showInformazioniGeneraliErogazioniFruizioniView)){
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
		
		
		// Gestione canali
		if(gestioneCanaliEnabled) {
			if( tipoOp.equals(TipoOperazione.ADD)) {
				DataElement dataElement = new DataElement();
				dataElement.setLabel(CostantiControlStation.LABEL_CONFIGURAZIONE_CANALE);
				dataElement.setType(DataElementType.SUBTITLE);
				dati.add(dataElement);
				
				this.addCanaleToDati(dati, tipoOp, canaleStato, canale, canaleAPI, canaleList, gestioneCanaliEnabled, false);
			} else {
				// sono dati delle porte applicative/delegate inserisco hidden i valori che mi vengono passati
				this.addCanaleToDatiAsHidden(dati, tipoOp, canaleStato, canale, gestioneCanaliEnabled);
			}
		}

		
		// Modalita' standard faccio vedere lo stato
		// stato
		if(isModalitaAvanzata){

			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_STATO);
			if(this.isShowGestioneWorkflowStatoDocumenti()){
				if( tipoOp.equals(TipoOperazione.ADD) || 
						(StatiAccordo.finale.toString().equals(oldStato)==false && 
								(showInformazioniGeneraliErogazioniFruizioniView==null || showInformazioniGeneraliErogazioniFruizioniView)) ){
					de.setType(DataElementType.SELECT);
					de.setValues(StatiAccordo.toArray());
					de.setLabels(StatiAccordo.toLabel());
					de.setSelected(statoPackage);
					de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO_PACKAGE);
				}else{
					
					if(showInformazioniGeneraliErogazioniFruizioniView==null || showInformazioniGeneraliErogazioniFruizioniView) {
						DataElement deLabel = new DataElement();
						deLabel.setType(DataElementType.TEXT);
						deLabel.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_STATO);
						deLabel.setValue(StatiAccordo.upper(StatiAccordo.finale.toString()));
						deLabel.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO_PACKAGE+"__label");
						dati.addElement(deLabel);
					}
					
					de.setType(DataElementType.HIDDEN);
					de.setValue(StatiAccordo.finale.toString());
					de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO_PACKAGE);

					if(ripristinoStatoOperativo){
						dati.addElement(de);

						if(showInformazioniGeneraliErogazioniFruizioniView==null || showInformazioniGeneraliErogazioniFruizioniView) {
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
			if(this.isShowGestioneWorkflowStatoDocumenti()){
				if(tipoOp.equals(TipoOperazione.ADD)){
					de.setType(DataElementType.HIDDEN);
					de.setValue(statoPackage);
				}else if( 
						(StatiAccordo.finale.toString().equals(oldStato)==false) && 
						(showInformazioniGeneraliErogazioniFruizioniView==null || showInformazioniGeneraliErogazioniFruizioniView) ){
					de.setType(DataElementType.SELECT);
					de.setValues(StatiAccordo.toArray());
					de.setLabels(StatiAccordo.toLabel());
					de.setSelected(statoPackage);
				}else{
					
					if(showInformazioniGeneraliErogazioniFruizioniView==null || showInformazioniGeneraliErogazioniFruizioniView) {
						DataElement deLabel = new DataElement();
						deLabel.setType(DataElementType.TEXT);
						deLabel.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_STATO);
						deLabel.setValue(StatiAccordo.upper(StatiAccordo.finale.toString()));
						deLabel.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO_PACKAGE+"__label");
						dati.addElement(deLabel);
					}
					
					de.setType(DataElementType.HIDDEN);
					de.setValue(StatiAccordo.finale.toString());

					if(ripristinoStatoOperativo){
						dati.addElement(de);

						if(showInformazioniGeneraliErogazioniFruizioniView==null || showInformazioniGeneraliErogazioniFruizioniView) {
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
				}				
			}else{
				de.setType(DataElementType.HIDDEN);
				de.setValue(StatiAccordo.finale.toString());
			}

			dati.addElement(de);
		}
		
		
		
		//Sezione Soggetto Erogatore (provider)

		if(gestioneFruitori) {
			
			boolean showSoggettoInChange = false; // viene fatto vedere nella maschera riassuntiva
			
			if (tipoOp.equals(TipoOperazione.ADD) || showSoggettoInChange) {
				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SOGGETTO_EROGATORE);
				de.setType(DataElementType.SUBTITLE);
				dati.addElement(de);
			}

			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_PROVIDER_EROGATORE);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_EROGATORE);
			if (tipoOp.equals(TipoOperazione.ADD)) {
				de.setType(DataElementType.SELECT);
				de.setValues(soggettiList);
				de.setLabels(soggettiListLabel);
				de.setPostBack(true);
				de.setSelected(provider);
				dati.addElement(de);
			} else {
				de.setValue(provider);
				de.setType(DataElementType.HIDDEN);
				dati.addElement(de);
	
				if(showSoggettoInChange) {
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
					de.setValue(this.getLabelNomeSoggetto(tipoProtocollo, tipoSoggetto, nomeSoggetto));
					dati.addElement(de);
				}
			}
		}
		
		
		
		// allegati
		
		if(tipoOp.equals(TipoOperazione.ADD) == false && !this.isModalitaCompleta() &&
				(showInformazioniGeneraliErogazioniFruizioniView==null || showInformazioniGeneraliErogazioniFruizioniView)) {
			de = new DataElement();
			de.setType(DataElementType.LINK);
			List<Parameter> listParametersAllegati = new ArrayList<>();
			listParametersAllegati.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, id));
			if(tmpModificaAPI!=null) {
				listParametersAllegati.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_API, tmpModificaAPI));
			}
			if(gestioneFruitori) {
				listParametersAllegati.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SOGGETTO_FRUITORE, tipoSoggettoFruitore));
				listParametersAllegati.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SOGGETTO_FRUITORE, nomeSoggettoFruitore));
			}
			de.setUrl(
					AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ALLEGATI_LIST,
					listParametersAllegati.toArray(new Parameter[1]));
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
					ServletUtils.setDataElementCustomLabel(de, AccordiServizioParteSpecificaCostanti.LABEL_APS_ALLEGATI, AccordiServizioParteSpecificaCostanti.LABEL_N_D);
				}
			}else{
				de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_APS_ALLEGATI  );
			}
			dati.addElement(de);
		}
		
		
		
		// specifica porti di accesso
		
		if(serviceBinding.equals(ServiceBinding.SOAP) && interfaceType.equals(org.openspcoop2.protocol.manifest.constants.InterfaceType.WSDL_11) && showPortiAccesso &&
				(showInformazioniGeneraliErogazioniFruizioniView==null || showInformazioniGeneraliErogazioniFruizioniView)){

			if(isModalitaAvanzata){
				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SPECIFICA_PORTI_ACCESSO);
				de.setType(DataElementType.SUBTITLE);
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
	
				boolean isSupportoAsincrono = this.core.isProfiloDiCollaborazioneAsincronoSupportatoDalProtocollo(tipoProtocollo,serviceBinding);
				boolean isRuoloNormale =  !( (servcorr != null) && ((servcorr.equals(Costanti.CHECK_BOX_ENABLED)) || servcorr.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_ABILITATO)) ) ;
	
				if (tipoOp.equals(TipoOperazione.ADD)) {
					if(isSupportoAsincrono){
						if(isRuoloNormale){
							dati.add(wsdlimpler.getFileDataElement(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_EROGATORE_COMPATTO, "", getSize()));
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
							dati.add(wsdlimplfru.getFileDataElement(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_FRUITORE_COMPATTO, "", getSize()));
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
					
					List<Parameter> listParametersWSDLChange = new ArrayList<>();
					listParametersWSDLChange.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, id));
					if(gestioneFruitori) {
						listParametersWSDLChange.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SOGGETTO_FRUITORE, tipoSoggettoFruitore));
						listParametersWSDLChange.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SOGGETTO_FRUITORE, nomeSoggettoFruitore));
					}
					
					if(isSupportoAsincrono){
						if(isRuoloNormale){
							de = new DataElement();
							de.setType(DataElementType.LINK);
							listParametersWSDLChange.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_EROGATORE));
							de.setUrl(
									AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_WSDL_CHANGE,
									listParametersWSDLChange.toArray(new Parameter[1]));
							de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_EROGATORE_ESTESO);
							dati.addElement(de);
						}else{
							de = new DataElement();
							de.setType(DataElementType.LINK);
							listParametersWSDLChange.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_FRUITORE));
							de.setUrl(
									AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_WSDL_CHANGE,
									listParametersWSDLChange.toArray(new Parameter[1]));
							de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_FRUITORE_ESTESO);
							dati.addElement(de);
						}
					}else {
						de = new DataElement();
						de.setType(DataElementType.LINK);
						listParametersWSDLChange.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_EROGATORE));
						de.setUrl(
								AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_WSDL_CHANGE,
								listParametersWSDLChange.toArray(new Parameter[1]));
	
						de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO);
						dati.addElement(de);
					}
				}
	
			} else {
				if (tipoOp.equals(TipoOperazione.ADD)) {
					de = new DataElement();
					String wsdlimplerS =  wsdlimpler.getValue() != null ? new String(wsdlimpler.getValue()) : ""; 
					de.setValue(wsdlimplerS);
					de.setType(DataElementType.HIDDEN);
					de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_EROGATORE);
					dati.addElement(de);
	
					de = new DataElement();
					String wsdlimplfruS =  wsdlimpler.getValue() != null ? new String(wsdlimpler.getValue()) : ""; 
					de.setValue(wsdlimplfruS);
					de.setType(DataElementType.HIDDEN);
					de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_FRUITORE);
					dati.addElement(de);
				}
			}
		}
		
		// Porta Applicativa e Servizio Applicativo Erogatore
		if (tipoOp.equals(TipoOperazione.ADD) && !ServletUtils.isCheckBoxEnabled(servcorr) && generaPACheckSoggetto) {

			if(this.isModalitaCompleta()) {
			
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
				
			}
			
			// Controllo Accesso
			
			// Creo un oggetto vuoto tanto per passare l'informazione sul tipo protocollo
			PortaApplicativa pa = new PortaApplicativa();
			pa.setServizio(new PortaApplicativaServizio());
			pa.getServizio().setTipo(tipoServizio);
			
			boolean forceAutenticato = false; 
			boolean forceHttps = false;
			boolean forceDisableOptional = false;
			if(this.isProfiloModIPA(tipoProtocollo)) {
				if(idAccordoParteComune==null) {
					idAccordoParteComune = this.idAccordoFactory.getIDAccordoFromUri(uriAccordo);
				}
				forceAutenticato = true; // in modI ci vuole sempre autenticazione https sull'erogazione (cambia l'opzionalita' o meno)
				forceHttps = forceAutenticato;
				forceDisableOptional = this.forceHttpsClientProfiloModiPA(idAccordoParteComune, portType);
			}
			
			this.controlloAccessiAdd(dati, tipoOp, controlloAccessiStato, forceAutenticato);
			
			this.controlloAccessiGestioneToken(dati, tipoOp, gestioneToken, gestioneTokenPolicyLabels, gestioneTokenPolicyValues, 
					gestioneTokenPolicy, gestioneTokenOpzionale, 
					gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenForward, null,tipoProtocollo, false);
			
			this.controlloAccessiAutenticazione(dati, tipoOp, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD, pa, tipoProtocollo,
					erogazioneAutenticazione, null, erogazioneAutenticazioneOpzionale, erogazioneAutenticazionePrincipal, erogazioneAutenticazioneParametroList, false, erogazioneIsSupportatoAutenticazioneSoggetti,false,
					gestioneToken, gestioneTokenPolicy, autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
					false, null, 0,
					forceHttps, forceDisableOptional);
						
			this.controlloAccessiAutorizzazione(dati, tipoOp, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD, pa, tipoProtocollo,
					erogazioneAutenticazione, erogazioneAutorizzazione, null, 
					erogazioneAutorizzazioneAutenticati, null, 0, soggettiAutenticati, soggettiAutenticatiLabel, soggettoAutenticato,
					erogazioneAutorizzazioneRuoli, null, 0, erogazioneRuolo,
					erogazioneAutorizzazioneRuoliTipologia, erogazioneAutorizzazioneRuoliMatch, 
					false, erogazioneIsSupportatoAutenticazioneSoggetti, contaListe, false, false,autorizzazioneScope,null,0,scope,autorizzazioneScopeMatch,
					gestioneToken, gestioneTokenPolicy, 
					autorizzazione_token, autorizzazione_tokenOptions,allegatoXacmlPolicy,
					null, 0, null, 0);
			
		}
		
		if(tipoOp.equals(TipoOperazione.ADD) && gestioneFruitori) {
			
			// Controllo Accesso Fruizione
			
			// Creo un oggetto vuoto tanto per passare l'informazione sul tipo protocollo
			PortaDelegata pd = new PortaDelegata();
			pd.setServizio(new PortaDelegataServizio());
			pd.getServizio().setTipo(tipoServizio);
			
			this.controlloAccessiAdd(dati, tipoOp, controlloAccessiStato, false);
			
			this.controlloAccessiGestioneToken(dati, tipoOp, gestioneToken, gestioneTokenPolicyLabels, gestioneTokenPolicyValues, 
					gestioneTokenPolicy, gestioneTokenOpzionale, 
					gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenForward, null,tipoProtocollo, true);

			this.controlloAccessiAutenticazione(dati, tipoOp, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD,pd, tipoProtocollo,
					fruizioneAutenticazione, null, fruizioneAutenticazioneOpzionale, fruizioneAutenticazionePrincipal, fruizioneAutenticazioneParametroList, false, true,true,
					gestioneToken, gestioneTokenPolicy, autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
					false, null, 0,
					false, false);
		
			this.controlloAccessiAutorizzazione(dati, tipoOp, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD,pd, tipoProtocollo,
					fruizioneAutenticazione, fruizioneAutorizzazione, null, 
					fruizioneAutorizzazioneAutenticati, null, 0, saList, fruizioneServizioApplicativo,
					fruizioneAutorizzazioneRuoli, null, 0, fruizioneRuolo,
					fruizioneAutorizzazioneRuoliTipologia, fruizioneAutorizzazioneRuoliMatch, 
					false, true, contaListe, true, false,autorizzazioneScope,null,0,scope,autorizzazioneScopeMatch,
					gestioneToken, gestioneTokenPolicy, 
					autorizzazione_token, autorizzazione_tokenOptions,allegatoXacmlPolicy,
					null, 0, null, 0);
			
		}


		/*
		 * de = new DataElement(); de.setLabel("Servizio pubblico");
		 * de.setType("checkbox"); de.setName("servpub"); if ((servpub != null)
		 * && servpub.equals(Costanti.CHECK_BOX_ENABLED)) { de.setSelected(Costanti.CHECK_BOX_ENABLED); }
		 * dati.addElement(de);
		 */

		if (tipoOp.equals(TipoOperazione.ADD) == false) {

//			IDPortaApplicativa idPA = null;
//			Integer versioneInt = Integer.parseInt(versione);
//			IDServizio idServizio = this.idServizioFactory.getIDServizioFromValues(tipoServizioEffettivo, nomeServizioEffettivo, 
//					tipoSoggetto, nomeSoggetto, versioneInt); 
//			if(isModalitaAvanzata==false){
//				idPA = this.porteApplicativeCore.getIDPortaApplicativaAssociataDefault(idServizio);
//			}
			
			
			
//			de = new DataElement();
//			if(isModalitaAvanzata || idPA==null){
//				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_APPLICATIVE);
//			}
//			else{
//				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SPECIFICA_PORTA_APPLICATIVA);
//			}
//			de.setType(DataElementType.TITLE);
//			dati.addElement(de);
//			
//			de = new DataElement();
//			de.setType(DataElementType.LINK);
//			if(isModalitaAvanzata || idPA==null){
//				de.setUrl(
//						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_PORTE_APPLICATIVE_LIST,
//						new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, id),
//						new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+idSoggettoErogatore),
//						new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, nomeServizioEffettivo),
//						new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, tipoServizioEffettivo));
//				if(contaListe){
//					try{
//						// BugFix OP-674
//						//int num = this.apsCore.serviziPorteAppList(tipoServizioEffettivo,nomeServizioEffettivo,Long.parseLong(id),idSoggettoErogatore, new Search(true)).size();
//						Search searchForCount = new Search(true,1);
//						this.apsCore.mappingServiziPorteAppList(idServizio, Integer.parseInt(id), (int) idSoggettoErogatore, searchForCount);
//						int num = searchForCount.getNumEntries(Liste.CONFIGURAZIONE_EROGAZIONE);
//						ServletUtils.setDataElementCustomLabel(de, AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_APPLICATIVE, (long) num);
//					}catch(Exception e){
//						this.log.error("Calcolo numero pa non riuscito",e);
//						ServletUtils.setDataElementCustomLabel(de, AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_APPLICATIVE, "N.D.");
//					}
//				}else{
//					de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_APPLICATIVE);
//				}
//			}
//			else{
//				// Utilizza la configurazione come parent
//				ServletUtils.setObjectIntoSession(this.session, PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE, PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT);
//				
//				PortaApplicativa pa = this.porteApplicativeCore.getPortaApplicativa(idPA);
//				de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CHANGE,
//						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, pa.getId()+""),
//						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, pa.getIdSoggetto()+""),
//						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA, pa.getNome())
//						);
//				de.setValue(Costanti.LABEL_VISUALIZZA);
//			}
//			dati.addElement(de);
			
			
			
			if(this.isModalitaCompleta()) {
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
						ServletUtils.setDataElementCustomLabel(de, AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORI, AccordiServizioParteSpecificaCostanti.LABEL_N_D);
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
						ServletUtils.setDataElementCustomLabel(de, AccordiServizioParteSpecificaCostanti.LABEL_APS_ALLEGATI, AccordiServizioParteSpecificaCostanti.LABEL_N_D);
					}
				}else{
					de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_APS_ALLEGATI  );
				}
				dati.addElement(de);

			}
		}

		if(cambiaAPI && !apiChanged) {
			this.pd.disableOnlyButton();
		}
		
		return dati;
	}

	public Vector<DataElement> addServiziToDatiAsHidden(Vector<DataElement> dati, String nomeservizio, String tiposervizio,
			String provider, String tipoSoggetto, String nomeSoggetto, String[] soggettiList, String[] soggettiListLabel,
			String accordo, ServiceBinding serviceBinding, String[] accordiList, String[] accordiListLabel, String servcorr, String wsdlimpler,
			String wsdlimplfru, TipoOperazione tipoOp, String id, List<String> tipi, String profilo, String portType, 
			String[] ptList, boolean privato, String uriAccordo, String descrizione, long idSoggettoErogatore,
			String statoPackage,String oldStato,String versione,
			List<String> versioni,boolean validazioneDocumenti,String [] saSoggetti, String nomeSA, String protocollo, boolean generaPACheckSoggetto) throws Exception{

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

		boolean modificaAbilitata = ( (this.isShowGestioneWorkflowStatoDocumenti()==false) || (StatiAccordo.finale.toString().equals(oldStato)==false) );

		boolean isModalitaAvanzata = this.isModalitaAvanzata();

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
			if(tipoOp.equals(TipoOperazione.CHANGE) && (this.isShowGestioneWorkflowStatoDocumenti() 
					&&  StatiAccordo.finale.toString().equals(oldStato) )){
				de.setValue(Costanti.CHECK_BOX_DISABLED);
			}
		}
		dati.addElement(de);

		if( (tipoOp.equals(TipoOperazione.CHANGE) &&
				(this.isShowGestioneWorkflowStatoDocumenti() &&  StatiAccordo.finale.toString().equals(oldStato) )) 
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
				(this.isShowGestioneWorkflowStatoDocumenti() && 
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
		if (tipoOp.equals(TipoOperazione.ADD) && !ServletUtils.isCheckBoxEnabled(servcorr) && generaPACheckSoggetto) {

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
		de.setValue(wsdlimpler);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_EROGATORE);
		dati.addElement(de);

		de = new DataElement();
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

		boolean isModalitaAvanzata = this.isModalitaAvanzata();

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

		if( this.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(asps.getStatoPackage())){
			this.pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);

			if(this.core.isShowInterfacceAPI()) {
				de = new DataElement();
				de.setLabel("");
				de.setType(DataElementType.TEXT_AREA_NO_EDIT);
				de.setValue(oldwsdl);
				de.setRows(CostantiControlStation.LABEL_PARAMETRO_TEXT_AREA_API_SIZE);
				de.setCols(CostantiControlStation.LABEL_PARAMETRO_TEXT_AREA_API_COLUMNS);
				dati.addElement(de);
			}
			
			if(oldwsdl != null && !oldwsdl.isEmpty()){
				DataElement saveAs = new DataElement();
				saveAs.setValue(AccordiServizioParteComuneCostanti.LABEL_DOWNLOAD);
				saveAs.setType(DataElementType.LINK);
				saveAs.setUrl(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT, 
						new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_ID_ACCORDO, asps.getId()+""),
						new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO, tipologiaDocumentoScaricare),
						new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO, ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_PARTE_SPECIFICA));
				saveAs.setDisabilitaAjaxStatus();
				dati.add(saveAs);
			}else {
				de = new DataElement();
//				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_WSDL_ATTUALE );
				de.setType(DataElementType.TEXT);
				de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_WSDL_NOT_FOUND);
				dati.addElement(de);
			}
			
		}
		else{

			if(oldwsdl != null && !oldwsdl.isEmpty()){
				if(this.core.isShowInterfacceAPI()) {
					de = new DataElement();
					de.setType(DataElementType.TEXT_AREA_NO_EDIT);
					de.setValue(oldwsdl);
					de.setRows(CostantiControlStation.LABEL_PARAMETRO_TEXT_AREA_API_SIZE);
					de.setCols(CostantiControlStation.LABEL_PARAMETRO_TEXT_AREA_API_COLUMNS);
					//de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_WSDL_ATTUALE);
					dati.addElement(de);
				}

				DataElement saveAs = new DataElement();
				saveAs.setValue(AccordiServizioParteComuneCostanti.LABEL_DOWNLOAD);
				saveAs.setType(DataElementType.LINK);
				saveAs.setUrl(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT, 
						new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_ID_ACCORDO, asps.getId()+""),
						new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO, tipologiaDocumentoScaricare),
						new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO, ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_PARTE_SPECIFICA));
				saveAs.setDisabilitaAjaxStatus();
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

		boolean isModalitaAvanzata = this.isModalitaAvanzata();

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



		if(this.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(myFru.getStatoPackage())){
			
			this.pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);
			
			if(wsdl != null && !wsdl.isEmpty()){
				this.pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);
				
				if(this.core.isShowInterfacceAPI()) {
					de = new DataElement();
					de.setLabel("");
					de.setType(DataElementType.TEXT_AREA_NO_EDIT);
					de.setValue( wsdl);
					de.setRows(CostantiControlStation.LABEL_PARAMETRO_TEXT_AREA_API_SIZE);
					de.setCols(CostantiControlStation.LABEL_PARAMETRO_TEXT_AREA_API_COLUMNS);
					dati.addElement(de);
				}
				
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
					saveAs.setDisabilitaAjaxStatus();
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
				if(this.core.isShowInterfacceAPI()) {
					de = new DataElement();
					de.setType(DataElementType.TEXT_AREA_NO_EDIT);
					de.setValue(wsdl);
					de.setRows(CostantiControlStation.LABEL_PARAMETRO_TEXT_AREA_API_SIZE);
					de.setCols(CostantiControlStation.LABEL_PARAMETRO_TEXT_AREA_API_COLUMNS);
					//de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_WSDL_ATTUALE );
					dati.addElement(de);
				}

				DataElement saveAs = new DataElement();
				saveAs.setValue(AccordiServizioParteComuneCostanti.LABEL_DOWNLOAD);
				saveAs.setType(DataElementType.LINK);
				saveAs.setUrl(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT, 
						new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_ID_ACCORDO, id),
						new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO, tipologiaDocumentoScaricare),
						new Parameter(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_TIPO_SOGGETTO_FRUITORE, myFru.getTipo()),
						new Parameter(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_NOME_SOGGETTO_FRUITORE, myFru.getNome()),
						new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO, ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_PARTE_SPECIFICA));
				saveAs.setDisabilitaAjaxStatus();
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
			String controlloAccessiStato,
			String fruizioneServizioApplicativo,String fruizioneRuolo,String fruizioneAutenticazione,String fruizioneAutenticazioneOpzionale, TipoAutenticazionePrincipal fruizioneAutenticazionePrincipal, List<String> fruizioneAutenticazioneParametroList, String fruizioneAutorizzazione,
			String fruizioneAutorizzazioneAutenticati,String fruizioneAutorizzazioneRuoli, String fruizioneAutorizzazioneRuoliTipologia, String fruizioneAutorizzazioneRuoliMatch,
			List<String> saList, ServiceBinding serviceBinding, org.openspcoop2.protocol.manifest.constants.InterfaceType interfaceType,
			String azioneConnettore, String azioneConnettoreIdPorta, String accessoDaAPSParametro, Integer parentPD,
			String gestioneToken, String[] gestioneTokenPolicyLabels, String[] gestioneTokenPolicyValues,
			String gestioneTokenPolicy, String gestioneTokenOpzionale, 
			String gestioneTokenValidazioneInput, String gestioneTokenIntrospection, String gestioneTokenUserInfo, String gestioneTokenForward,
			String autenticazioneTokenIssuer,String autenticazioneTokenClientId,String autenticazioneTokenSubject,String autenticazioneTokenUsername,String autenticazioneTokenEMail,
			String autorizzazione_token, String autorizzazione_tokenOptions,
			String autorizzazioneScope,  String scope, String autorizzazioneScopeMatch,BinaryParameter allegatoXacmlPolicy) throws Exception {
		
		boolean isRuoloNormale = !(correlato != null && correlato.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_CORRELATO));

		String protocollo = this.apsCore.getProtocolloAssociatoTipoServizio(tiposervizio);

		boolean isProfiloAsincronoSupportatoDalProtocollo = this.core.isProfiloDiCollaborazioneAsincronoSupportatoDalProtocollo(protocollo,serviceBinding);

		boolean ripristinoStatoOperativo = this.core.isGestioneWorkflowStatoDocumenti_ripristinoStatoOperativoDaFinale();

		Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
		
		boolean showPortiAccesso = this.apcCore.showPortiAccesso(protocollo, serviceBinding, interfaceType);
		
		String tipologia = ServletUtils.getObjectFromSession(this.session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
		boolean gestioneFruitori = false;
		if(tipologia!=null) {
			if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
				gestioneFruitori = true;
			}
		}
		
		String tmpModificaProfilo = this.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_PROFILO);
		@SuppressWarnings("unused")
		boolean modificaProfilo = false;
		if(tmpModificaProfilo!=null) {
			modificaProfilo = "true".equals(tmpModificaProfilo);
			
			DataElement de = new DataElement();
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_PROFILO);
			de.setValue(tmpModificaProfilo);
			de.setType(DataElementType.HIDDEN);
			dati.addElement(de);
		}
		
		if(azioneConnettore!=null && !"".equals(azioneConnettore)) {
			DataElement de = new DataElement();
			de.setType(DataElementType.HIDDEN);
			de.setValue(azioneConnettore);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_FRUITORE_VIEW_CONNETTORE_MAPPING_AZIONE);
			dati.addElement(de);
		}
		
		if(azioneConnettoreIdPorta!=null && !"".equals(azioneConnettoreIdPorta)) {
			DataElement de = new DataElement();
			de.setType(DataElementType.HIDDEN);
			de.setValue(azioneConnettoreIdPorta);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_FRUITORE_VIEW_CONNETTORE_MAPPING_AZIONE_ID_PORTA);
			dati.addElement(de);
		}
		
		if(accessoDaAPSParametro!=null && !"".equals(accessoDaAPSParametro)) {
			DataElement de = new DataElement();
			de.setType(DataElementType.HIDDEN);
			de.setValue(accessoDaAPSParametro);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_CONNETTORE_DA_LISTA_APS);
			dati.addElement(de);
		}
		
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
			de.setPostBack(true);
			dati.addElement(de);

			
			if(this.isShowGestioneWorkflowStatoDocumenti()){
				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_STATO);
				if (this.isModalitaStandard()) {
					de.setType(DataElementType.HIDDEN);
					de.setValue(statoServizio);
				}else{
					de.setType(DataElementType.SELECT);
					de.setValues(StatiAccordo.toArray());
					de.setLabels(StatiAccordo.toLabel());
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
			String protocolloSoggettoGestitoPorta = null;
			if(soggettiList!=null && soggettiList.length>0){
				String soggettoId = idSoggettoFruitore;
				if(soggettoId==null || "".equals(soggettoId)){
					soggettoId = soggettiList[0];
				}
				Soggetto sogg = this.soggettiCore.getSoggettoRegistro(Long.parseLong(soggettoId));
				if(!this.pddCore.isPddEsterna(sogg.getPortaDominio())){
					isSoggettoGestitoPorta = true;	
					protocolloSoggettoGestitoPorta = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(sogg.getTipo());
				}
			}




			if(isSoggettoGestitoPorta){
				
				this.controlloAccessiAdd(dati, tipoOp, controlloAccessiStato, false);
				
				this.controlloAccessiGestioneToken(dati, tipoOp, gestioneToken, gestioneTokenPolicyLabels, gestioneTokenPolicyValues, 
						gestioneTokenPolicy, gestioneTokenOpzionale,
						gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenForward, null,protocolloSoggettoGestitoPorta,true);

				this.controlloAccessiAutenticazione(dati, tipoOp, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD,null,protocolloSoggettoGestitoPorta,
						fruizioneAutenticazione, null, fruizioneAutenticazioneOpzionale, fruizioneAutenticazionePrincipal, fruizioneAutenticazioneParametroList, false, true,true,
						gestioneToken, gestioneTokenPolicy, autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
						false, null, 0,
						false, false);
				
				this.controlloAccessiAutorizzazione(dati, tipoOp, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD,null,protocolloSoggettoGestitoPorta,
						fruizioneAutenticazione, fruizioneAutorizzazione, null, 
						fruizioneAutorizzazioneAutenticati, null, 0, saList, fruizioneServizioApplicativo,
						fruizioneAutorizzazioneRuoli, null, 0, fruizioneRuolo,
						fruizioneAutorizzazioneRuoliTipologia, fruizioneAutorizzazioneRuoliMatch, 
						false, true, contaListe, true, false,autorizzazioneScope,null,0,scope,autorizzazioneScopeMatch,
						gestioneToken, gestioneTokenPolicy, 
						autorizzazione_token, autorizzazione_tokenOptions,allegatoXacmlPolicy,
						null, 0, null, 0);
	
			}

			if(serviceBinding.equals(ServiceBinding.SOAP) && interfaceType.equals(org.openspcoop2.protocol.manifest.constants.InterfaceType.WSDL_11) && showPortiAccesso){
			
				if (this.isModalitaAvanzata()) {
					de = new DataElement();
					de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SPECIFICA_PORTI_ACCESSO );
					de.setType(DataElementType.TITLE);
					dati.addElement(de);
				}
	
				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_VALIDAZIONE_DOCUMENTI_ESTESA);
				de.setValue(""+validazioneDocumenti);
				if (tipoOp.equals(TipoOperazione.ADD) && this.isModalitaAvanzata()) {
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
	
				if(this.isModalitaAvanzata()){
					if(isProfiloAsincronoSupportatoDalProtocollo){
						if(isRuoloNormale){
							dati.add(wsdlimpler.getFileDataElement(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_EROGATORE_COMPATTO, "", getSize()));
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
							dati.add(wsdlimplfru.getFileDataElement(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_FRUITORE_COMPATTO, "", getSize()));
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
					de.setValue("");
					de.setType(DataElementType.HIDDEN);
					de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_EROGATORE);
					dati.addElement(de);
	
	
					de = new DataElement();
					de.setValue("");
					de.setType(DataElementType.HIDDEN);
					de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_FRUITORE);
					dati.addElement(de);
				}
				
			}
				
		} else {
					
			boolean viewOnlyConnettore = gestioneFruitori || (PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE==parentPD);
			
			if(!viewOnlyConnettore) {
				DataElement de = new DataElement();
				de.setType(DataElementType.TITLE);
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORE);
				dati.addElement(de);
			}
			
			DataElement de = new DataElement();
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
				String [] split = nomeSoggetto.split("/");
				IDSoggetto idSoggetto = new IDSoggetto(split[0], split[1]);
				de.setValue(this.getLabelNomeSoggetto(protocollo, idSoggetto.getTipo(), idSoggetto.getNome()));
				if(!viewOnlyConnettore) {
					de.setType(DataElementType.TEXT);
				}
				else {
					de.setType(DataElementType.HIDDEN);
				}
				dati.addElement(de);
			}
			
			if(this.isModalitaCompleta() && !viewOnlyConnettore) {
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
			}

						
			if(this.isShowGestioneWorkflowStatoDocumenti() && this.isModalitaCompleta() && !viewOnlyConnettore) {
				de = new DataElement();
				de.setType(DataElementType.TITLE);
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_STATO);
				dati.addElement(de);
			}

			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_STATO);
			if(this.isShowGestioneWorkflowStatoDocumenti()){
				if(viewOnlyConnettore) {
					de.setType(DataElementType.HIDDEN);
					de.setValue(stato);
					de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_STATO);
				}
				else if(StatiAccordo.finale.toString().equals(oldStato)==false ){
					de.setType(DataElementType.SELECT);
					de.setValues(StatiAccordo.toArray());
					de.setLabels(StatiAccordo.toLabel());
					de.setSelected(stato);
					de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_STATO);
				}else{
//					if (!isModalitaAvanzata) {
//						de.setType(DataElementType.HIDDEN);
//						de.setValue(StatiAccordo.finale.toString());
//						de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_STATO);
//					}else{
					
					DataElement deLabel = new DataElement();
					deLabel.setType(DataElementType.TEXT);
					deLabel.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_STATO);
					deLabel.setValue(StatiAccordo.upper(StatiAccordo.finale.toString()));
					deLabel.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_STATO+"__label");
					dati.addElement(deLabel);
					
					de.setType(DataElementType.HIDDEN);
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


			if(!viewOnlyConnettore && serviceBinding.equals(ServiceBinding.SOAP) && interfaceType.equals(org.openspcoop2.protocol.manifest.constants.InterfaceType.WSDL_11) && showPortiAccesso){
				if (this.isModalitaAvanzata()) {
	
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
							de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_EROGATORE_ESTESO);
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
							de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_FRUITORE_ESTESO);
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
		}

		return dati;
	}

	public Vector<DataElement> addServiziFruitoriToDatiAsHidden(Vector<DataElement> dati, String idSoggettoFruitore, String wsdlimpler, 
			String wsdlimplfru, String[] soggettiList, String[] soggettiListLabel, String idServ, String id, 
			TipoOperazione tipoOp, String idSoggettoErogatoreDelServizio, String nomeprov, String tipoprov,
			String nomeservizio, String tiposervizio, String correlato, String stato, String oldStato, String statoServizio,
			String tipoAccordo, boolean validazioneDocumenti,
			String azioneConnettore) throws Exception {

		boolean isModalitaAvanzata = this.isModalitaAvanzata();

		if(azioneConnettore!=null && !"".equals(azioneConnettore)) {
			DataElement de = new DataElement();
			de.setType(DataElementType.HIDDEN);
			de.setValue(azioneConnettore);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_FRUITORE_VIEW_CONNETTORE_MAPPING_AZIONE);
			dati.addElement(de);
		}
		
		if (tipoOp.equals(TipoOperazione.ADD)) {
			// in caso di add allora visualizzo la lista dei soggetti
			DataElement de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_PROVIDER_FRUITORE);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE);
			de.setType(DataElementType.HIDDEN);
			de.setValue(idSoggettoFruitore);
			dati.addElement(de);

			if(this.isShowGestioneWorkflowStatoDocumenti()){
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
			de.setValue("");
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_EROGATORE);
			dati.addElement(de);


			de = new DataElement();
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
			Vector<DataElement> dati,
			String oldStatoPackage, String idServizio, String idServizioFruitore, String idSoggettoErogatoreDelServizio,
			String nomeservizio, String tiposervizio, Integer versioneservizio, String idSoggettoFruitore,
			AccordoServizioParteSpecifica asps, Fruitore fruitore) throws Exception{

		boolean isModalitaAvanzata = this.isModalitaAvanzata();

		boolean farVedere =false;
		// Occhio che sotto questo if ci sono dei campi HIDDEN
		if(farVedere && tipoOp.equals(TipoOperazione.CHANGE)){

			Soggetto fruitoreSogg = this.soggettiCore.getSoggettoRegistro(new IDSoggetto(fruitore.getTipo(), fruitore.getNome()));
			
			boolean esterno = this.pddCore.isPddEsterna(fruitoreSogg.getPortaDominio());			
			if(!esterno){
			
				IDPortaDelegata idPD = null;
				IDSoggetto idSoggettoFruitoreObject = new IDSoggetto(fruitore.getTipo(), fruitore.getNome());
				if(isModalitaAvanzata==false){
					IDServizio idServizioObject = this.idServizioFactory.getIDServizioFromValues(asps.getTipo(), asps.getNome(), 
							asps.getTipoSoggettoErogatore(),asps.getNomeSoggettoErogatore(),asps.getVersione());
					idPD = this.porteDelegateCore.getIDPortaDelegataAssociataDefault(idServizioObject, idSoggettoFruitoreObject);
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
							IDServizio idServizioFromAccordo = this.idServizioFactory.getIDServizioFromAccordo(asps); 
							//long idSoggetto = this.soggettiCore.getIdSoggetto(fruitore.getNome(), fruitore.getTipo());
							IDSoggetto idSoggettoFr = new IDSoggetto(fruitore.getNome(), fruitore.getTipo());
							this.apsCore.serviziFruitoriMappingList(fruitore.getId(), idSoggettoFr, idServizioFromAccordo, searchForCount);
							//int numPD = fruLista.size();
							int numPD = searchForCount.getNumEntries(Liste.CONFIGURAZIONE_FRUIZIONE);
							ServletUtils.setDataElementCustomLabel(de, AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_DELEGATE, (long) numPD );
							
						}catch(Exception e){
							this.log.error("Calcolo numero pa non riuscito",e);
							ServletUtils.setDataElementCustomLabel(de, AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_DELEGATE, AccordiServizioParteSpecificaCostanti.LABEL_N_D);
						}
					}else{
						de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_DELEGATE);
					}
					
				}
				else{
					// Utilizza la configurazione come parent
					ServletUtils.setObjectIntoSession(this.session, PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE, PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT);
					
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

		if(tipoOp.equals(TipoOperazione.CHANGE)){
			DataElement de = new DataElement();
			de.setValue(idServizioFruitore);
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID);
			dati.addElement(de);

		}

		return dati;
	}

	public Vector<DataElement> addFruitoreToDatiAsHidden(TipoOperazione tipoOp, String[] versioniLabel, String[] versioniValues,
			Vector<DataElement> dati,
			String oldStatoPackage, String idServizio, String idServizioFruitore, String idSoggettoErogatoreDelServizio,
			String nomeservizio, String tiposervizio, String idSoggettoFruitore){

	
		if(tipoOp.equals(TipoOperazione.CHANGE)){
			DataElement de = new DataElement();
			de.setValue(idServizioFruitore);
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID);
			dati.addElement(de);

			//			boolean modificaAbilitata = ( (this.apsCore.isShowGestioneWorkflowStatoDocumenti()==false)
			//					|| (StatiAccordo.finale.toString().equals(oldStatoPackage)==false) );


		}
		
		return dati;
	}

	public Vector<DataElement>  addTipiAllegatiToDati(TipoOperazione tipoOp, String idServizio, String ruolo,
			String[] ruoli, String[] tipiAmmessi, String[] tipiAmmessiLabel,
			Vector<DataElement> dati, String modificaAPI) {
		
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
		
		if(modificaAPI!=null) {
			de = new DataElement();
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_API);
			de.setValue(modificaAPI);
			dati.addElement(de);
		}

		return dati;
	}

	public Vector<DataElement>  addInfoAllegatiToDati(TipoOperazione tipoOp,    String idAllegato,
			AccordoServizioParteSpecifica asps, Documento doc,
			Vector<DataElement> dati, String modificaAPI) throws Exception {
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

		if(this.isEditModeInProgress() && this.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(asps.getStatoPackage())){
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

		if(modificaAPI!=null) {
			de = new DataElement();
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_API);
			de.setValue(modificaAPI);
			dati.addElement(de);
		}
		
		return dati;
	}


	public Vector<DataElement> addViewAllegatiToDati(TipoOperazione tipoOp, String idAllegato, String idServizio,
			Documento doc, StringBuilder contenutoAllegato, String errore,
			Vector<DataElement> dati, String modificaAPI) {
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

		if(this.core.isShowAllegati()) {
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
				de.setRows(CostantiControlStation.LABEL_PARAMETRO_TEXT_AREA_API_SIZE);
				de.setCols(CostantiControlStation.LABEL_PARAMETRO_TEXT_AREA_API_COLUMNS);
				dati.addElement(de);
			}
		}
		
		DataElement saveAs = new DataElement();
		saveAs.setValue(AccordiServizioParteSpecificaCostanti.LABEL_APS_DOWNLOAD);
		saveAs.setType(DataElementType.LINK);
		Parameter pIdAccordo = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_ACCORDO, idServizio);
		Parameter pIdAllegato = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_ALLEGATO, idAllegato);
		Parameter pTipoDoc = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_DOCUMENTO, "asps");
		//			String params = "idAccordo="+idServizio+"&idAllegato="+idAllegato+"&tipoDocumento=asps";
		saveAs.setUrl(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT, pIdAccordo, pIdAllegato, pTipoDoc);
		saveAs.setDisabilitaAjaxStatus();
		dati.add(saveAs);

		if(modificaAPI!=null) {
			de = new DataElement();
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_API);
			de.setValue(modificaAPI);
			dati.addElement(de);
		}
		
		return dati;
	}
	
	public Vector<DataElement> addConfigurazioneErogazioneToDati(TipoOperazione tipoOperazione, Vector<DataElement> dati, String nome, String nomeGruppo,
			String[] azioni, String[] azioniDisponibiliList, String[] azioniDisponibiliLabelList, 
			String idAsps, String idSoggettoErogatoreDelServizio, String identificazione, 
			AccordoServizioParteSpecifica asps, AccordoServizioParteComuneSintetico as, ServiceBinding serviceBinding, String modeCreazione, String modeCreazioneConnettore,
			String[] listaMappingLabels, String[] listaMappingValues, String mapping, String mappingLabel, boolean paMappingSelezionatoMulti, String nomeSA, String [] saSoggetti, 
			String controlloAccessiStato,
			String erogazioneAutenticazione, String erogazioneAutenticazioneOpzionale, TipoAutenticazionePrincipal erogazioneAutenticazionePrincipal, List<String> erogazioneAutenticazioneParametroList, boolean erogazioneIsSupportatoAutenticazioneSoggetti,
			String erogazioneAutorizzazione, String erogazioneAutorizzazioneAutenticati, String erogazioneAutorizzazioneRuoli,
			String erogazioneRuolo, String erogazioneAutorizzazioneRuoliTipologia, String erogazioneAutorizzazioneRuoliMatch,
			List<String> soggettiAutenticati,  List<String> soggettiAutenticatiLabel, String soggettoAutenticato,
			String gestioneToken, String[] gestioneTokenPolicyLabels, String[] gestioneTokenPolicyValues,
			String gestioneTokenPolicy, String gestioneTokenOpzionale, 
			String gestioneTokenValidazioneInput, String gestioneTokenIntrospection, String gestioneTokenUserInfo, String gestioneTokenForward,
			String autenticazioneTokenIssuer,String autenticazioneTokenClientId,String autenticazioneTokenSubject,String autenticazioneTokenUsername,String autenticazioneTokenEMail,
			String autorizzazione_token, String autorizzazione_tokenOptions,
			String autorizzazioneScope, String scope, String autorizzazioneScopeMatch,BinaryParameter allegatoXacmlPolicy) throws Exception {
		
		Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
		
		DataElement de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_APPLICATIVE);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		// idSoggetto erogatore
		de = new DataElement();
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE);
		de.setValue(idSoggettoErogatoreDelServizio); 
		dati.addElement(de);
		
		// Nome Gruppo
		de = new DataElement();
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_GRUPPO);
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME_GRUPPO);
		de.setValue(nomeGruppo);
		de.setType(DataElementType.TEXT_EDIT);
		de.setRequired(true); 
		dati.addElement(de);
		
		// Azione
		de = new DataElement();
		de.setLabel(this.getLabelAzioni(serviceBinding));
		de.setValues(azioniDisponibiliList);
		de.setLabels(azioniDisponibiliLabelList);
		de.setSelezionati(azioni);
		de.setType(DataElementType.MULTI_SELECT);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AZIONI);
		de.setRows(CostantiControlStation.RIGHE_MULTISELECT_AZIONI);
		de.setRequired(true); 
		dati.addElement(de);
		
		
		// Nome
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
		de.setValue(nome);
		de.setType(DataElementType.HIDDEN);
		de.setRequired(true);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME);
		dati.addElement(de);
		
		// mode
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODO_CREAZIONE);
		String[] modeLabels = {PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODO_CREAZIONE_EREDITA,PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODO_CREAZIONE_NUOVA};
		String[] modeValues = {PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_MODO_CREAZIONE_EREDITA, PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_MODO_CREAZIONE_NUOVA};
		de.setLabels(modeLabels); 
		de.setValues(modeValues);
		de.setSelected(modeCreazione);
		de.setType(DataElementType.SELECT);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_CREAZIONE);
		de.setPostBack(true, true);
		dati.addElement(de);
		
		
		boolean showModeConnettore = !this.isModalitaStandard();
		// 	modo creazione: se erediti la configurazione da una precedente allora devi solo sezionarla, altrimenti devi compilare le sezioni SA e controllo accessi
		if(modeCreazione.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_MODO_CREAZIONE_EREDITA)) {
			// mapping
			de = new DataElement();
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MAPPING_GRUPPO);
			de.setLabels(listaMappingLabels); 
			de.setValues(listaMappingValues);
			de.setSelected(mapping);
			de.setToolTip(mappingLabel);
			de.setType(DataElementType.SELECT);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MAPPING);
			de.setPostBack(true);
			dati.addElement(de);
			
			showModeConnettore = showModeConnettore && !paMappingSelezionatoMulti;
		} 
		
		// mode Connettore
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODO_CREAZIONE_CONNETTORE);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_CREAZIONE_CONNETTORE);
		if(showModeConnettore) {
			de.setType(DataElementType.CHECKBOX);
			de.setSelected(ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore));
			de.setPostBack(true);
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(modeCreazione);
		}
		dati.addElement(de);
		
		if(!modeCreazione.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_MODO_CREAZIONE_EREDITA)) {
			
			// Controllo Accesso
			
			boolean forceAutenticato = false; 
			boolean forceHttps = false;
			boolean forceDisableOptional = false;
			String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(asps.getTipoSoggettoErogatore());
			if(this.isProfiloModIPA(protocollo)) {
				forceAutenticato = true; // in modI ci vuole sempre autenticazione https sull'erogazione (cambia l'opzionalita' o meno)
				forceHttps = forceAutenticato;
				forceDisableOptional = this.forceHttpsClientProfiloModiPA(this.idAccordoFactory.getIDAccordoFromAccordo(as),asps.getPortType());
			}
			
			this.controlloAccessiAdd(dati, tipoOperazione, controlloAccessiStato, forceAutenticato);
			
			this.controlloAccessiGestioneToken(dati, tipoOperazione, gestioneToken, gestioneTokenPolicyLabels, gestioneTokenPolicyValues, 
					gestioneTokenPolicy, gestioneTokenOpzionale,
					gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenForward, null,protocollo, false);
			
			this.controlloAccessiAutenticazione(dati, tipoOperazione, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD, null,protocollo,
					erogazioneAutenticazione, null, erogazioneAutenticazioneOpzionale, erogazioneAutenticazionePrincipal, erogazioneAutenticazioneParametroList, false, erogazioneIsSupportatoAutenticazioneSoggetti,false,
					gestioneToken, gestioneTokenPolicy, autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
					false, null, 0,
					forceHttps, forceDisableOptional);
			
			this.controlloAccessiAutorizzazione(dati, tipoOperazione, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD, null,protocollo,
					erogazioneAutenticazione, erogazioneAutorizzazione, null, 
					erogazioneAutorizzazioneAutenticati, null, 0, soggettiAutenticati, soggettiAutenticatiLabel, soggettoAutenticato,
					erogazioneAutorizzazioneRuoli, null, 0, erogazioneRuolo,
					erogazioneAutorizzazioneRuoliTipologia, erogazioneAutorizzazioneRuoliMatch, 
					false, erogazioneIsSupportatoAutenticazioneSoggetti, contaListe, false, false,autorizzazioneScope,null,0,scope,autorizzazioneScopeMatch,
					gestioneToken, gestioneTokenPolicy, 
					autorizzazione_token, autorizzazione_tokenOptions,allegatoXacmlPolicy,
					null, 0, null, 0);
		}
		
		
		return dati;
	}

	public boolean configurazioneErogazioneCheckData(TipoOperazione tipoOp, String nome, String nomeGruppo, String[] azioni,
			AccordoServizioParteSpecifica asps, List<String> azioniOccupate,
			String modeCreazione, String idPorta, boolean isSupportatoAutenticazione,
			AccordiServizioParteSpecificaPorteApplicativeMappingInfo mappingInfo) throws Exception{
		
		if(nomeGruppo==null || "".equals(nomeGruppo.trim())) {
			this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_NOME_GRUPPO_NON_PUO_ESSERE_VUOTA);
			return false;
		}
		// Check lunghezza
		if(this.checkLength255(nomeGruppo, PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_GRUPPO)==false) {
			return false;
		}
		if(AccordiServizioParteSpecificaUtilities.getMappingPA_filterByDescription(mappingInfo.getListaMappingErogazione(), nomeGruppo)!=null) {
			this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_NOME_GRUPPO_GIA_ESISTENTE);
			return false;
		}
		
		if(azioni == null || azioni.length == 0) {
			this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_AZIONE_PORTA_NON_PUO_ESSERE_VUOTA);
			return false;
		}
		for (String azioneTmp : azioni) {
			if(azioneTmp == null || azioneTmp.equals("") || azioneTmp.equals("-")) {
				this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_AZIONE_PORTA_NON_PUO_ESSERE_VUOTA);
				return false;
			}
		}
		
		for (String azioneTmp : azioni) {
			if(azioniOccupate.contains(azioneTmp)) {
				this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_AZIONE_PORTA_GIA_PRESENTE);
				return false;			
			}
		}
		
		if(checkAzioniUtilizzateErogazioneRateLimiting(mappingInfo, azioni)==false) {
			return false;
		}
		
		if(modeCreazione.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_MODO_CREAZIONE_EREDITA)) {
			
		} else {
			String autenticazione = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE);
			String autenticazioneCustom = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM);
			String autenticazioneOpzionale = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_OPZIONALE);
			String autenticazionePrincipalTipo = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_TIPO);
			TipoAutenticazionePrincipal autenticazionePrincipal = TipoAutenticazionePrincipal.toEnumConstant(autenticazionePrincipalTipo, false);
			List<String> autenticazioneParametroList = this.convertFromDataElementValue_parametroAutenticazioneList(autenticazione, autenticazionePrincipal);
			
			String autorizzazione = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE);
			String autorizzazioneCustom = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM);
			String autorizzazioneAutenticati = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE);
			String autorizzazioneRuoli = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI);
			String autorizzazioneRuoliTipologia = this.getParameter(CostantiControlStation.PARAMETRO_RUOLO_TIPOLOGIA);
			String ruoloMatch = this.getParameter(CostantiControlStation.PARAMETRO_RUOLO_MATCH);
			
			String autorizzazioneContenutiStato = this.getParameter(CostantiControlStation.PARAMETRO_AUTORIZZAZIONE_CONTENUTI_STATO);
			String autorizzazioneContenuti = this.getParameter(CostantiControlStation.PARAMETRO_AUTORIZZAZIONE_CONTENUTI);
			String autorizzazioneContenutiProperties = this.getParameter(CostantiControlStation.PARAMETRO_AUTORIZZAZIONE_CONTENUTI_PROPERTIES);
			
//			if() {
//				
//			}
			
			// Se autenticazione = custom, nomeauth dev'essere specificato
			if (CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM.equals(autenticazione) && 
					(autenticazioneCustom == null || autenticazioneCustom.equals(""))) {
				this.pd.setMessage(MessageFormat.format(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_INDICARE_UN_NOME_PER_AUTENTICAZIONE_XX, CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM));
				return false;
			}

			// Se autorizzazione = custom, nomeautor dev'essere specificato
			if (CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM.equals(autorizzazione) && 
					(autorizzazioneCustom == null || autorizzazioneCustom.equals(""))) {
				this.pd.setMessage(MessageFormat.format(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_INDICARE_UN_NOME_PER_AUTORIZZAZIONE_XX, CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM));
				return false;
			}
			
			PortaApplicativa pa = null;
			if (TipoOperazione.CHANGE == tipoOp){
				pa = this.porteApplicativeCore.getPortaApplicativa(Long.parseLong(idPorta)); 
			}
			
			List<String> ruoli = new ArrayList<>();
			if(pa!=null && pa.getRuoli()!=null && pa.getRuoli().sizeRuoloList()>0){
				for (int i = 0; i < pa.getRuoli().sizeRuoloList(); i++) {
					ruoli.add(pa.getRuoli().getRuolo(i).getNome());
				}
			}
			
			String gestioneToken = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN);
			String gestioneTokenPolicy = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_POLICY);
			String gestioneTokenValidazioneInput = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_VALIDAZIONE_INPUT);
			String gestioneTokenIntrospection = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_INTROSPECTION);
			String gestioneTokenUserInfo = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_USERINFO);
			String gestioneTokenTokenForward = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_TOKEN_FORWARD);
			String autorizzazione_token = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN);
			String autorizzazione_tokenOptions = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_OPTIONS);
			String autorizzazioneScope = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_SCOPE);
			String autorizzazioneScopeMatch = this.getParameter(CostantiControlStation.PARAMETRO_SCOPE_MATCH);
			BinaryParameter allegatoXacmlPolicy = this.getBinaryParameter(CostantiControlStation.PARAMETRO_DOCUMENTO_SICUREZZA_XACML_POLICY);
			String protocollo = ProtocolFactoryManager.getInstance().getProtocolByServiceType(asps.getTipo());
			
			if(this.controlloAccessiCheck(tipoOp, autenticazione, autenticazioneOpzionale, autenticazionePrincipal, autenticazioneParametroList,
					autorizzazione, autorizzazioneAutenticati, autorizzazioneRuoli, 
					autorizzazioneRuoliTipologia, ruoloMatch, 
					isSupportatoAutenticazione, false, pa, ruoli,gestioneToken, gestioneTokenPolicy, 
					gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
					autorizzazione_token,autorizzazione_tokenOptions,
					autorizzazioneScope,autorizzazioneScopeMatch,allegatoXacmlPolicy,
					autorizzazioneContenutiStato, autorizzazioneContenuti, autorizzazioneContenutiProperties,
					protocollo)==false){
				return false;
			}
		}
		
		return true;
	}
		
	public boolean configurazioneFruizioneCheckData(TipoOperazione tipoOp, String nome, String nomeGruppo, String [] azioni,
			AccordoServizioParteSpecifica asps, List<String> azioniOccupate,
			String modeCreazione, String idPorta, boolean isSupportatoAutenticazione,
			AccordiServizioParteSpecificaFruitoriPorteDelegateMappingInfo mappingInfo) throws Exception{
		
		if(nomeGruppo==null || "".equals(nomeGruppo.trim())) {
			this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_NOME_GRUPPO_NON_PUO_ESSERE_VUOTA);
			return false;
		}
		
		// Check lunghezza
		if(this.checkLength255(nomeGruppo, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME_GRUPPO)==false) {
			return false;
		}
		
		if(AccordiServizioParteSpecificaUtilities.getMappingPD_filterByDescription(mappingInfo.getListaMappingFruizione(), nomeGruppo)!=null) {
			this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_NOME_GRUPPO_GIA_ESISTENTE);
			return false;
		}
		
		if(azioni == null || azioni.length == 0) {
			this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_AZIONE_PORTA_NON_PUO_ESSERE_VUOTA);
			return false;
		}
		for (String azioneTmp : azioni) {
			if(azioneTmp == null || azioneTmp.equals("") || azioneTmp.equals("-")) {
				this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_AZIONE_PORTA_NON_PUO_ESSERE_VUOTA);
				return false;
			}
		}
		
		for (String azioneTmp : azioni) {
			if(azioniOccupate.contains(azioneTmp)) {
				this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_AZIONE_PORTA_GIA_PRESENTE);
				return false;			
			}
		}
		
		if(checkAzioniUtilizzateFruizioneRateLimiting(mappingInfo, azioni)==false) {
			return false;
		}
		
		if(modeCreazione.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_MODO_CREAZIONE_EREDITA)) {
			
		} else {
			String autenticazione = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE);
			String autenticazioneCustom = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM);
			String autenticazioneOpzionale = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_OPZIONALE);
			String autenticazionePrincipalTipo = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_TIPO);
			TipoAutenticazionePrincipal autenticazionePrincipal = TipoAutenticazionePrincipal.toEnumConstant(autenticazionePrincipalTipo, false);
			List<String> autenticazioneParametroList = this.convertFromDataElementValue_parametroAutenticazioneList(autenticazione, autenticazionePrincipal);
			
			String autorizzazione = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE);
			String autorizzazioneCustom = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM);
			String autorizzazioneAutenticati = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE);
			String autorizzazioneRuoli = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI);
			String autorizzazioneRuoliTipologia = this.getParameter(CostantiControlStation.PARAMETRO_RUOLO_TIPOLOGIA);
			String ruoloMatch = this.getParameter(CostantiControlStation.PARAMETRO_RUOLO_MATCH);
			
			String autorizzazioneContenutiStato = this.getParameter(CostantiControlStation.PARAMETRO_AUTORIZZAZIONE_CONTENUTI_STATO);
			String autorizzazioneContenuti = this.getParameter(CostantiControlStation.PARAMETRO_AUTORIZZAZIONE_CONTENUTI);
			String autorizzazioneContenutiProperties = this.getParameter(CostantiControlStation.PARAMETRO_AUTORIZZAZIONE_CONTENUTI_PROPERTIES);
			
			// Se autenticazione = custom, nomeauth dev'essere specificato
			if (CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM.equals(autenticazione) && 
					(autenticazioneCustom == null || autenticazioneCustom.equals(""))) {
				this.pd.setMessage(MessageFormat.format(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_INDICARE_UN_NOME_PER_AUTENTICAZIONE_XX, CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM));
				return false;
			}

			// Se autorizzazione = custom, nomeautor dev'essere specificato
			if (CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM.equals(autorizzazione) && 
					(autorizzazioneCustom == null || autorizzazioneCustom.equals(""))) {
				this.pd.setMessage(MessageFormat.format(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_INDICARE_UN_NOME_PER_AUTORIZZAZIONE_XX, CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM));
				return false;
			}
			
			PortaDelegata pd = null;
			if (TipoOperazione.CHANGE == tipoOp){
				pd = this.porteDelegateCore.getPortaDelegata(Long.parseLong(idPorta)); 
			}
			
			List<String> ruoli = new ArrayList<>();
			if(pd!=null && pd.getRuoli()!=null && pd.getRuoli().sizeRuoloList()>0){
				for (int i = 0; i < pd.getRuoli().sizeRuoloList(); i++) {
					ruoli.add(pd.getRuoli().getRuolo(i).getNome());
				}
			}
			
			String gestioneToken = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN);
			String gestioneTokenPolicy = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_POLICY);
			String gestioneTokenValidazioneInput = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_VALIDAZIONE_INPUT);
			String gestioneTokenIntrospection = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_INTROSPECTION);
			String gestioneTokenUserInfo = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_USERINFO);
			String gestioneTokenTokenForward = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_TOKEN_FORWARD);
			String autorizzazione_token = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN);
			String autorizzazione_tokenOptions = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_OPTIONS);
			String autorizzazioneScope = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_SCOPE);
			String autorizzazioneScopeMatch = this.getParameter(CostantiControlStation.PARAMETRO_SCOPE_MATCH);
			
			BinaryParameter allegatoXacmlPolicy = this.getBinaryParameter(CostantiControlStation.PARAMETRO_DOCUMENTO_SICUREZZA_XACML_POLICY);
			String protocollo = ProtocolFactoryManager.getInstance().getProtocolByServiceType(asps.getTipo());
			
			if(this.controlloAccessiCheck(tipoOp, autenticazione, autenticazioneOpzionale, autenticazionePrincipal, autenticazioneParametroList,
					autorizzazione, autorizzazioneAutenticati, autorizzazioneRuoli, 
					autorizzazioneRuoliTipologia, ruoloMatch, 
					isSupportatoAutenticazione, true, pd, ruoli,gestioneToken, gestioneTokenPolicy, 
					gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
					autorizzazione_token,autorizzazione_tokenOptions,
					autorizzazioneScope,autorizzazioneScopeMatch,allegatoXacmlPolicy,
					autorizzazioneContenutiStato, autorizzazioneContenuti, autorizzazioneContenutiProperties,
					protocollo)==false){
				return false;
			}
		}
		
		return true;
	}


	public Vector<DataElement> addConfigurazioneFruizioneToDati(TipoOperazione tipoOp, Vector<DataElement> dati, 
			String nome, String nomeGruppo,
			String [] azioni, String[] azioniDisponibiliList, String[] azioniDisponibiliLabelList, String idAsps,
			IDSoggetto idSoggettoFruitore, String identificazione, AccordoServizioParteSpecifica asps,
			AccordoServizioParteComuneSintetico as, ServiceBinding serviceBinding, String modeCreazione, String modeCreazioneConnettore,
			String[] listaMappingLabels, String[] listaMappingValues, String mapping, String mappingLabel, List<String> saList, String nomeSA, 
			String controlloAccessiStato,
			String fruizioneAutenticazione, String fruizioneAutenticazioneOpzionale, TipoAutenticazionePrincipal fruizioneAutenticazionePrincipal, List<String> fruizioneAutenticazioneParametroList, boolean erogazioneIsSupportatoAutenticazioneSoggetti,
			String fruizioneAutorizzazione, String fruizioneAutorizzazioneAutenticati,
			String fruizioneAutorizzazioneRuoli, String fruizioneRuolo, String fruizioneAutorizzazioneRuoliTipologia,
			String fruizioneAutorizzazioneRuoliMatch, String fruizioneServizioApplicativo,
			String gestioneToken, String[] gestioneTokenPolicyLabels, String[] gestioneTokenPolicyValues,
			String gestioneTokenPolicy, String gestioneTokenOpzionale, 
			String gestioneTokenValidazioneInput, String gestioneTokenIntrospection, String gestioneTokenUserInfo, String gestioneTokenForward,
			String autenticazioneTokenIssuer,String autenticazioneTokenClientId,String autenticazioneTokenSubject,String autenticazioneTokenUsername,String autenticazioneTokenEMail,
			String autorizzazione_token, String autorizzazione_tokenOptions,
			String autorizzazioneScope,  String scope, String autorizzazioneScopeMatch, BinaryParameter allegatoXacmlPolicy) throws Exception{
		
		String tipologia = ServletUtils.getObjectFromSession(this.session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
		boolean gestioneFruitori = false;
		if(tipologia!=null) {
			if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
				gestioneFruitori = true;
			}
		}
		
		Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
		
		DataElement de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_DELEGATE);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		// Nome Gruppo
		de = new DataElement();
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_GRUPPO);
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME_GRUPPO);
		de.setValue(nomeGruppo);
		de.setType(DataElementType.TEXT_EDIT);
		de.setRequired(true); 
		dati.addElement(de);
		
		// Azione
		de = new DataElement();
		de.setLabel(this.getLabelAzioni(serviceBinding));
		de.setValues(azioniDisponibiliList);
		de.setLabels(azioniDisponibiliLabelList);
		de.setSelezionati(azioni); 
		de.setType(DataElementType.MULTI_SELECT);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONI);
		de.setRows(CostantiControlStation.RIGHE_MULTISELECT_AZIONI);
		de.setRequired(true); 
		dati.addElement(de);
		
		
		// Nome
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
		de.setValue(nome);
		de.setType(DataElementType.HIDDEN);
		de.setRequired(true);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME);
		dati.addElement(de);
		
		// mode
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODO_CREAZIONE);
		String[] modeLabels = {PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODO_CREAZIONE_EREDITA,PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODO_CREAZIONE_NUOVA};
		String[] modeValues = {PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODO_CREAZIONE_EREDITA, PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODO_CREAZIONE_NUOVA};
		de.setLabels(modeLabels); 
		de.setValues(modeValues);
		de.setSelected(modeCreazione);
		de.setType(DataElementType.SELECT);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_CREAZIONE);
		de.setPostBack(true, true);
		dati.addElement(de);
		
		// 	modo creazione: se erediti la configurazione da una precedente allora devi solo sezionarla, altrimenti devi compilare le sezioni SA e controllo accessi
		if(modeCreazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODO_CREAZIONE_EREDITA)) {
			// mapping
			de = new DataElement();
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MAPPING_GRUPPO);
			de.setLabels(listaMappingLabels); 
			de.setValues(listaMappingValues);
			de.setSelected(mapping);
			de.setToolTip(mappingLabel);
			de.setType(DataElementType.SELECT);
			de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MAPPING);
			de.setPostBack(true);
			dati.addElement(de);
		}
		
		// Controllo se richiedere il connettore
		boolean connettoreStatic = false;
		if(gestioneFruitori) {
			connettoreStatic = this.apsCore.isConnettoreStatic(this.soggettiCore.getProtocolloAssociatoTipoSoggetto(asps.getTipoSoggettoErogatore()));
		}
		
		// mode Connettore
		de = new DataElement();
		de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODO_CREAZIONE_CONNETTORE);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_CREAZIONE_CONNETTORE);
		if(this.isModalitaStandard() || connettoreStatic) {
			de.setType(DataElementType.HIDDEN);
			de.setValue(modeCreazione);
		}
		else {
			de.setType(DataElementType.CHECKBOX);
			de.setSelected(ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore));
			de.setPostBack(true);
		}
		dati.addElement(de);
		
		if(!modeCreazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODO_CREAZIONE_EREDITA)) {
			
			this.controlloAccessiAdd(dati, tipoOp, controlloAccessiStato, false);
			
			String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(asps.getTipoSoggettoErogatore());
			
			this.controlloAccessiGestioneToken(dati, tipoOp, gestioneToken, gestioneTokenPolicyLabels, gestioneTokenPolicyValues, 
					gestioneTokenPolicy, gestioneTokenOpzionale,
					gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenForward, null,protocollo,true);
		
			this.controlloAccessiAutenticazione(dati, tipoOp, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD,null,protocollo,
					fruizioneAutenticazione, null, fruizioneAutenticazioneOpzionale, fruizioneAutenticazionePrincipal, fruizioneAutenticazioneParametroList, false, true,true,
					gestioneToken, gestioneTokenPolicy, autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
					false, null, 0,
					false, false);
			
			this.controlloAccessiAutorizzazione(dati, tipoOp, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD,null,protocollo,
				fruizioneAutenticazione, fruizioneAutorizzazione, null, 
				fruizioneAutorizzazioneAutenticati, null, 0, saList, fruizioneServizioApplicativo,
				fruizioneAutorizzazioneRuoli, null, 0, fruizioneRuolo,
				fruizioneAutorizzazioneRuoliTipologia, fruizioneAutorizzazioneRuoliMatch, 
				false, erogazioneIsSupportatoAutenticazioneSoggetti, contaListe, true, false,autorizzazioneScope,null,0,scope,autorizzazioneScopeMatch,
				gestioneToken, gestioneTokenPolicy, 
				autorizzazione_token, autorizzazione_tokenOptions,allegatoXacmlPolicy,
				null, 0 , null, 0);
		}
		
		return dati;
	}

	
	public List<Parameter> getTitoloAps(TipoOperazione tipoOperazione, AccordoServizioParteSpecifica asps, boolean gestioneFruitori, String labelApsTitle, String servletNameApsChange, boolean addApsChange, String tipoSoggettoFruitore, String nomeSoggettoFruitore) throws Exception {
		List<Parameter> listaParams = new ArrayList<>();
		
		Parameter pIdServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId()+ "");
		Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, asps.getNome());
		Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, asps.getTipo());
		Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, this.session);
		String labelApsChange = null;
		
		List<Parameter> listParametersErogazioniChange = new ArrayList<>();
		listParametersErogazioniChange.add(pIdServizio);
		listParametersErogazioniChange.add(pNomeServizio);
		listParametersErogazioniChange.add(pTipoServizio);
		if(gestioneFruitori) {
			Parameter pTipoSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SOGGETTO_FRUITORE, tipoSoggettoFruitore);
			Parameter pNomeSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SOGGETTO_FRUITORE, nomeSoggettoFruitore);
			listParametersErogazioniChange.add(pTipoSoggettoFruitore);
			listParametersErogazioniChange.add(pNomeSoggettoFruitore);
		}
		if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
			if(gestioneFruitori) {
				listaParams.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_FRUIZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
				
			} else {
				listaParams.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_EROGAZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
			}
			
			
			listaParams.add(new Parameter(labelApsTitle, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_CHANGE, listParametersErogazioniChange));
			
			String paramModificaProfilo = this.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_PROFILO);
			String paramModificaAPI = this.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_API);
			String paramCambiaAPI = this.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_CAMBIA_API);
			
			User user = ServletUtils.getUserFromSession(this.session);
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
			
			if("true".equals(paramModificaAPI) || "true".equals(paramCambiaAPI)) {
				labelApsChange = asLabel;
			}
			else if("true".equals(paramModificaProfilo)) {
				labelApsChange = AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_PROTOCOLLO;
			}
			else {
				labelApsChange = ErogazioniCostanti.LABEL_ASPS_MODIFICA_SERVIZIO_INFO_GENERALI;
			}
			
		} else {
			if(gestioneFruitori) {
				listaParams.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORI, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
			}
			else {
				listaParams.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
			}
			labelApsChange = labelApsTitle;
		}
		
		if(addApsChange) {
			Parameter parameterApcChange = servletNameApsChange != null ? new Parameter(labelApsChange, servletNameApsChange, listParametersErogazioniChange) : new Parameter(labelApsChange, servletNameApsChange);
			listaParams.add(parameterApcChange);
		}
		
		return listaParams;
	}

}
