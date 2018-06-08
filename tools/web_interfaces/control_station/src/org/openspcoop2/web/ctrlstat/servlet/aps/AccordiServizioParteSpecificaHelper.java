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
package org.openspcoop2.web.ctrlstat.servlet.aps;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.MTOMProcessorType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Soggetto;
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
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
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
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
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
			String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey, String tipoconn, String versione,
			boolean validazioneDocumenti,  String backToStato,
			String autenticazioneHttp,
			String proxyEnabled, String proxyHost, String proxyPort, String proxyUsername, String proxyPassword,
			String tempiRisposta_enabled, String tempiRisposta_connectionTimeout, String tempiRisposta_readTimeout, String tempiRisposta_tempoMedioRisposta,
			String opzioniAvanzate, String transfer_mode, String transfer_mode_chunk_size, String redirect_mode, String redirect_max_hop,
			String requestOutputFileName,String requestOutputFileNameHeaders,String requestOutputParentDirCreateIfNotExists,String requestOutputOverwriteIfExists,
			String responseInputMode, String responseInputFileName, String responseInputFileNameHeaders, String responseInputDeleteAfterRead, String responseInputWaitTime,
			String erogazioneSoggetto,String erogazioneRuolo,String erogazioneAutenticazione,String erogazioneAutenticazioneOpzionale, String erogazioneAutorizzazione,
			String erogazioneAutorizzazioneAutenticati,String erogazioneAutorizzazioneRuoli, String erogazioneAutorizzazioneRuoliTipologia, String erogazioneAutorizzazioneRuoliMatch,boolean isSupportatoAutenticazione,
			boolean generaPACheckSoggetto, List<ExtendedConnettore> listExtendedConnettore,
			String fruizioneServizioApplicativo,String fruizioneRuolo,String fruizioneAutenticazione,String fruizioneAutenticazioneOpzionale, String fruizioneAutorizzazione,
			String fruizioneAutorizzazioneAutenticati,String fruizioneAutorizzazioneRuoli, String fruizioneAutorizzazioneRuoliTipologia, String fruizioneAutorizzazioneRuoliMatch,
			String protocollo
			)
					throws Exception {

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
			if (!isModalitaAvanzata) {
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
			
			// Controllo dell'end-point
			// Non li puo' prendere dalla servtlet
			boolean connettoreStatic = false;
			if(gestioneFruitori) {
				connettoreStatic = this.apsCore.isConnettoreStatic(protocollo);
			}
			if(!connettoreStatic) {
				if (!this.endPointCheckData(endpointtype, url, nome, tipo,
						user, password, initcont, urlpgk, provurl, connfact,
						sendas, httpsurl, httpstipologia, httpshostverify,
						httpspath, httpstipo, httpspwd, httpsalgoritmo, httpsstato,
						httpskeystore, httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey, httpspwdprivatekey,
						httpsalgoritmokey, tipoconn,autenticazioneHttp,
						proxyEnabled, proxyHost, proxyPort, proxyUsername, proxyPassword,
						tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta,
						opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
						requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
						listExtendedConnettore)) {
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
				this.pd.setMessage(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_IL_SOGGETTO_DEV_ESSERE_SCELTO_TRA_QUELLI_DEFINITI_NEL_PANNELLO_SOGGETTI);
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
				if (this.apsCore.existServizio(nomeservizio, tiposervizio, versioneInt, idSoggettoErogatore) > 0) {
					String labelServizio = this.getLabelNomeServizio(protocollo, tiposervizio, nomeservizio, versioneInt);
					String labelSoggetto = this.getLabelNomeSoggetto(protocollo, tipoErogatore, nomeErogatore);
					String msg = AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_ESISTE_UN_SERVIZIO_CON_IL_TIPO_E_NOME_DEFINITO_EROGATO_DAL_SOGGETTO_CON_PARAMETRI;
					if(gestioneFruitori) {
						msg = AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_ESISTE_UN_SERVIZIO_CON_IL_TIPO_E_NOME_DEFINITO_EROGATO_DAL_SOGGETTO_CON_PARAMETRI_FRUIZIONE;
					}
					this.pd.setMessage(MessageFormat.format(
							msg,
							labelServizio, labelSoggetto));
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
			
			IDServizio idAccordoServizioParteSpecifica = this.idServizioFactory.getIDServizioFromValues(tiposervizio, nomeservizio, 
					tipoErogatore, nomeErogatore, versioneInt);
			
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
				if(this.apsCore.existsAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica)){
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
				as = this.apcCore.getAccordoServizio(Long.parseLong(idAccordo));
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
				String autorizzazioneScope = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_SCOPE);
				String autorizzazioneScopeMatch = this.getParameter(CostantiControlStation.PARAMETRO_SCOPE_MATCH);

				if(gestioneFruitori) {
					
					if(this.controlloAccessiCheck(tipoOp, fruizioneAutenticazione, fruizioneAutenticazioneOpzionale, 
							fruizioneAutorizzazione, fruizioneAutorizzazioneAutenticati, fruizioneAutorizzazioneRuoli, 
							fruizioneAutorizzazioneRuoliTipologia, fruizioneAutorizzazioneRuoliMatch,
							true, true, null, null,gestioneToken, gestioneTokenPolicy, 
							gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,autorizzazioneScope,autorizzazioneScopeMatch)==false){
						return false;
					}
				}
				else {
				
					if(this.controlloAccessiCheck(tipoOp, erogazioneAutenticazione, erogazioneAutenticazioneOpzionale, 
							erogazioneAutorizzazione, erogazioneAutorizzazioneAutenticati, erogazioneAutorizzazioneRuoli, 
							erogazioneAutorizzazioneRuoliTipologia, erogazioneAutorizzazioneRuoliMatch,
							isSupportatoAutenticazione, false, null, null,gestioneToken, gestioneTokenPolicy, 
							gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,autorizzazioneScope,autorizzazioneScopeMatch)==false){
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
			String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey, String tipoconn, 
			boolean validazioneDocumenti, String backToStato,
			String autenticazioneHttp,
			String proxyEnabled, String proxyHost, String proxyPort, String proxyUsername, String proxyPassword,
			String tempiRisposta_enabled, String tempiRisposta_connectionTimeout, String tempiRisposta_readTimeout, String tempiRisposta_tempoMedioRisposta,
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
			
//			String tipologia = ServletUtils.getObjectFromSession(this.session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
//			boolean gestioneFruitori = false;
//			if(tipologia!=null) {
//				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
//					gestioneFruitori = true;
//				}
//			}
//			boolean connettoreOnly = gestioneFruitori;
			
			// Se il connettore e' disabilitato devo controllare che il
			// connettore del soggetto non sia disabilitato se è di tipo operativo
			//if (this.isModalitaAvanzata() || connettoreOnly) {
			if (this.isModalitaAvanzata() || TipoOperazione.CHANGE.equals(tipoOp)) {
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
						tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta,
						opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
						requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
						listExtendedConnettore)) {
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
					as = this.apcCore.getAccordoServizio(idAccordo);
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
				String autorizzazioneScope = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_SCOPE);
				String autorizzazioneScopeMatch = this.getParameter(CostantiControlStation.PARAMETRO_SCOPE_MATCH);
				
				if(this.controlloAccessiCheck(tipoOp, fruizioneAutenticazione, fruizioneAutenticazioneOpzionale, 
						fruizioneAutorizzazione, fruizioneAutorizzazioneAutenticati, fruizioneAutorizzazioneRuoli, 
						fruizioneAutorizzazioneRuoliTipologia, fruizioneAutorizzazioneRuoliMatch,
						true, true, null, null,gestioneToken, gestioneTokenPolicy, 
						gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,autorizzazioneScope,autorizzazioneScopeMatch)==false){
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

			if(this.archiviCore.existsDocumento(documento,ProprietariDocumento.servizio)){

				//check se stesso documento
				Documento existing = this.archiviCore.getDocumento(documento.getFile(),documento.getTipo(),documento.getRuolo(),documento.getIdProprietarioDocumento(),false,ProprietariDocumento.servizio);
				if(existing.getId() == documento.getId())
					return true;

				if(RuoliDocumento.allegato.toString().equals(documento.getRuolo()))
					this.pd.setMessage(MessageFormat.format(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_ALLEGATO_CON_NOME_TIPO_GIA_PRESENTE_NEL_SERVIZIO_CON_PARAMETRI,
							documento.getFile(), documento.getTipo()));
				else
					this.pd.setMessage(MessageFormat.format(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_LA_SPECIFICA_CON_NOME_TIPO_GIA_PRESENTE_NEL_SERVIZIO, documento.getRuolo(),
							documento.getFile(), documento.getTipo()));

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
			String id = this.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			ServletUtils.addListElementIntoSession(this.session, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_ALLEGATI, 
					new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, id));

			int idLista = Liste.SERVIZI_ALLEGATI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			String tipologia = ServletUtils.getObjectFromSession(this.session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			boolean gestioneFruitori = false;
			if(tipologia!=null) {
				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
					gestioneFruitori = true;
				}
			}
			
			String tmpTitle = this.getLabelIdServizio(asps);

			// setto la barra del titolo
			List<Parameter> lstParm = new ArrayList<Parameter>();

			if(gestioneFruitori) {
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORI, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
			}
			else {
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
			}
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
					}
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
			
			if(this.core.isShowGestioneWorkflowStatoDocumenti()){
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
				showFruitori = pddTipologiaFilter==null || user.isPermitMultiTenant() || PddTipologia.ESTERNO.equals(pddTipologiaFilter);
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

			
			List<AccordoServizioParteComune> listApc = new ArrayList<AccordoServizioParteComune>();
			List<String> protocolli = new ArrayList<String>();
			
			boolean showRuoli = false;
			for (AccordoServizioParteSpecifica asps : lista) {
				AccordoServizioParteComune apc = this.apcCore.getAccordoServizio(asps.getIdAccordo());
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
				listaLabelTabella.add(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_PROTOCOLLO);
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
			if(this.core.isShowGestioneWorkflowStatoDocumenti()) {
				if(this.core.isGestioneWorkflowStatoDocumenti_visualizzaStatoLista()) {
					listaLabelTabella.add(AccordiServizioParteSpecificaCostanti.LABEL_APS_STATO);
				}
			}
			if(showFruitori) {
				listaLabelTabella.add(fruitoriLabel);
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

			for (int i = 0; i < lista.size(); i++) {
				AccordoServizioParteSpecifica asps = lista.get(i);

				Fruitore fruitore = null;
				if(showConfigurazionePD) {
					fruitore = asps.getFruitore(0);
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
				boolean isPddEsterna = 
						this.pddCore.isPddEsterna(sog.getPortaDominio());
				
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
				AccordoServizioParteComune apc = listApc.get(i);
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
					if(!isPddEsterna){
						idPA = this.porteApplicativeCore.getIDPortaApplicativaAssociataDefault(idServizio);
						paDefault = this.porteApplicativeCore.getPortaApplicativa(idPA);
						
						paIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, asps.getIdSoggetto() + "");
						paNomePorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA, paDefault.getNome());
						paIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, ""+paDefault.getId());
						paIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, asps.getId()+ "");
						paConfigurazioneDati = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_DATI_INVOCAZIONE, Costanti.CHECK_BOX_ENABLED_TRUE);
						paIdProvider = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_PROVIDER, paDefault.getIdSoggetto() + "");
						paIdPortaPerSA = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_PORTA, ""+paDefault.getId());
						paConnettoreDaListaAPS = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS, Costanti.CHECK_BOX_ENABLED_TRUE);
					}
					
					
					// url invocazione
					de = new DataElement();
					if(isPddEsterna){
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
						if(isPddEsterna){
							de.setType(DataElementType.TEXT);
							de.setValue("-");
						}
						else{	
							PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo = paDefault.getServizioApplicativoList().get(0);
							//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
							de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT, paIdProvider, paIdPortaPerSA, paIdAsps,
									new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME_SERVIZIO_APPLICATIVO, portaApplicativaServizioApplicativo.getNome()),
									new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO, portaApplicativaServizioApplicativo.getId()+""),
									paConnettoreDaListaAPS);
							ServletUtils.setDataElementVisualizzaLabel(de);
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
								pNomeServizio, pTipoServizio, pIdsoggErogatore );
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
							pId, pIdSogg, pIdSoggettoErogatore, pNomeServizio, pTipoServizio, pIdFruitore);
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

				if(this.core.isShowGestioneWorkflowStatoDocumenti()){
					if(this.core.isGestioneWorkflowStatoDocumenti_visualizzaStatoLista()) {
						de = new DataElement();
						de.setValue(StatiAccordo.upper(asps.getStatoPackage()));
						e.addElement(de);
					}
				}

				if(showFruitori) {
					de = new DataElement();
					if(!user.isPermitMultiTenant() && !isPddEsterna) {
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

			if(this.core.isShowGestioneWorkflowStatoDocumenti()){
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
			if(this.core.isShowGestioneWorkflowStatoDocumenti() && this.core.isGestioneWorkflowStatoDocumenti_visualizzaStatoLista()){
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

				if(this.core.isShowGestioneWorkflowStatoDocumenti()){
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
							pId, pIdSogg, pIdSoggettoErogatore, pNomeServizio, pTipoServizio, pMyId);
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

	public void prepareServiziConfigurazioneList(List<MappingErogazionePortaApplicativa> lista, String id, String idSoggettoErogatoreDelServizio, ISearch ricerca)
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
			
			// Prendo il nome e il tipo del servizio
			AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(id));
			AccordoServizioParteComune apc = this.apcCore.getAccordoServizio(asps.getIdAccordo()); 
			org.openspcoop2.core.registry.constants.ServiceBinding serviceBinding = apc.getServiceBinding();
			ServiceBinding serviceBindingMessage = this.apcCore.toMessageServiceBinding(apc.getServiceBinding());
			
			int idLista = Liste.CONFIGURAZIONE_EROGAZIONE;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			
			List<String> azioni = this.porteApplicativeCore.getAzioni(asps, apc, false, true, new ArrayList<String>());
			String filtroAzione = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_AZIONE);
			this.addFilterAzione(azioni, filtroAzione, serviceBindingMessage);
			
			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setSearch("");
			
			lista = this.impostaFiltroAzioneMappingErogazione(filtroAzione, lista,ricerca, idLista);
			boolean allActionRedefined = false;
			if(lista.size()>1) {
				allActionRedefined = this.allActionsRedefinedMappingErogazione(azioni, lista);
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
			List<Parameter> lstParm = new ArrayList<Parameter>();

			lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
			lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_CONFIGURAZIONI_DI + servizioTmpTile, null));
			
			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParm );

			boolean visualizzaMTOM = true;
			boolean visualizzaSicurezza = true;
			boolean visualizzaCorrelazione = true;
			switch (serviceBinding) {
			case REST:
				visualizzaMTOM = false;
				visualizzaSicurezza = true;
				visualizzaCorrelazione = false;
				break;
			case SOAP:
			default:
				visualizzaMTOM = true;
				visualizzaSicurezza = true;
				visualizzaCorrelazione = true;
				break;
			}
			
			// setto le label delle colonne
			List<String> listaLabel = new ArrayList<String>();

			//listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE); // spostata direttamente nell'elenco delle erogazioni
			listaLabel.add(this.getLabelAzioni(serviceBindingMessage));
			if(this.isModalitaAvanzata()) {
				listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORE);
			}
			listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONTROLLO_ACCESSI);
			listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI);
			if(visualizzaSicurezza) {
				listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY);
			}
			if(visualizzaMTOM) {
				listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM);
			}
			if(visualizzaCorrelazione) {
				listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA);
			}
			listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE);
			
			if(this.isModalitaAvanzata()) {
				listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PROTOCOL_PROPERTIES);
				listaLabel.add(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_OPZIONI_AVANZATE);
			}
			if(extendedServletList!=null && extendedServletList.showExtendedInfo(this.request, this.session)){
				listaLabel.add(extendedServletList.getListTitle(this));
			}
			listaLabel.add(PorteApplicativeCostanti.LABEL_COLUMN_PORTE_APPLICATIVE_STATO_PORTA); 
			String[] labels = listaLabel.toArray(new String[listaLabel.size()]);
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			Iterator<MappingErogazionePortaApplicativa> it = lista.iterator();
			MappingErogazionePortaApplicativa mapping= null;
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
				
				@SuppressWarnings("unused")
				Parameter pConfigurazioneDati = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_DATI_INVOCAZIONE, Costanti.CHECK_BOX_ENABLED_TRUE);
				Parameter pConfigurazioneAltro = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONFIGURAZIONE_ALTRO, Costanti.CHECK_BOX_ENABLED_TRUE);

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
				
				// azioni
				DataElement de = new DataElement();
				if(!mapping.isDefault()) {
					List<String> listaAzioni = paAssociata.getAzione()!= null ?  paAssociata.getAzione().getAzioneDelegataList() : new ArrayList<String>();
					//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_AZIONE_LIST,pIdSogg, pIdPorta, pIdAsps);
					if (contaListe) {
						int numAzioni = listaAzioni.size();
						ServletUtils.setDataElementVisualizzaLabel(de, (long) numAzioni );
					} else
						ServletUtils.setDataElementVisualizzaLabel(de);
					if(listaAzioni.size() > 0) {
						StringBuffer sb = new StringBuffer();
						for (String string : listaAzioni) {
							if(sb.length() >0)
								sb.append(", ");
							
							sb.append(string);
						}
						de.setToolTip(sb.toString());
					}
				} else {
					de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MAPPING_EROGAZIONE_PA_AZIONE_DEFAULT);
				}
				de.setIdToRemove(paAssociata.getNome());
				e.addElement(de);
				
				// connettore
				if(this.isModalitaAvanzata()) {
					de = new DataElement();
					//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
					String servletConnettore = ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT;
					PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo = paAssociata.getServizioApplicativoList().get(0);
					if(mapping.isDefault()) {
						ServletUtils.setDataElementVisualizzaLabel(de);
						de.setUrl(servletConnettore, pIdProvider, pIdPortaPerSA, pIdAsps,
								new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME_SERVIZIO_APPLICATIVO, portaApplicativaServizioApplicativo.getNome()),
								new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO, portaApplicativaServizioApplicativo.getId()+""));
					}else {
						if(!portaApplicativaServizioApplicativo.getNome().equals(paAssociata.getNome())) { 
							servletConnettore = PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORE_DEFAULT;
							de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODALITA_CONNETTORE_DEFAULT); 
							
						} else {
							servletConnettore = PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORE_RIDEFINITO;
							de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODALITA_CONNETTORE_RIDEFINITO);
						}
						de.setUrl(servletConnettore, pIdSogg, pIdPorta, pIdAsps);
					}
					
					e.addElement(de);
				}
				
				// controllo accessi
				de = new DataElement();
				//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
				de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONTROLLO_ACCESSI, pIdSogg, pIdPorta, pIdAsps);
				
				String gestioneToken = null;
				if(paAssociata.getGestioneToken()!=null && paAssociata.getGestioneToken().getPolicy()!=null &&
						!"".equals(paAssociata.getGestioneToken().getPolicy()) &&
						!"-".equals(paAssociata.getGestioneToken().getPolicy())) {
					gestioneToken = StatoFunzionalita.ABILITATO.getValue();
				}
				
				String autenticazione = paAssociata.getAutenticazione();
				String autenticazioneCustom = null;
				if (autenticazione != null && !TipoAutenticazione.getValues().contains(autenticazione)) {
					autenticazioneCustom = autenticazione;
					autenticazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM;
				}
				String autenticazioneOpzionale = "";
				if(paAssociata.getAutenticazioneOpzionale()!=null){
					if (paAssociata.getAutenticazioneOpzionale().equals(StatoFunzionalita.ABILITATO)) {
						autenticazioneOpzionale = Costanti.CHECK_BOX_ENABLED;
					}
				}
				String autorizzazioneContenuti = paAssociata.getAutorizzazioneContenuto();
				
				String autorizzazione= null, autorizzazioneCustom = null;
				if (paAssociata.getAutorizzazione() != null &&
						!TipoAutorizzazione.getAllValues().contains(paAssociata.getAutorizzazione())) {
					autorizzazioneCustom = paAssociata.getAutorizzazione();
					autorizzazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM;
				}
				else{
					autorizzazione = AutorizzazioneUtilities.convertToStato(paAssociata.getAutorizzazione());
				}
				
				String statoControlloAccessi = this.getLabelStatoControlloAccessi(gestioneToken,autenticazione, autenticazioneOpzionale, autenticazioneCustom, autorizzazione, autorizzazioneContenuti,autorizzazioneCustom); 
				de.setValue(statoControlloAccessi);
				e.addElement(de);
				
				// validazione contenuti
				de = new DataElement();
				//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
				de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI, pIdSogg, pIdPorta, pIdAsps);
				String statoValidazione = null;
				
				ValidazioneContenutiApplicativi vx = paAssociata.getValidazioneContenutiApplicativi();
				if (vx == null) {
					statoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
				} else {
					if(vx.getStato()!=null)
						statoValidazione = vx.getStato().toString();
					if ((statoValidazione == null) || "".equals(statoValidazione)) {
						statoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
					}
				}
				
				de.setValue(statoValidazione);
				e.addElement(de);
				
				// message security
				if(visualizzaSicurezza) {
					de = new DataElement();
					//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
					de.setUrl(
							PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY,
							pIdSogg, pIdPorta, pIdAsps);
					de.setValue(paAssociata.getStatoMessageSecurity());
					e.addElement(de);
				}
				
				//mtom
				if(visualizzaMTOM) {
					de = new DataElement();
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM,pIdPorta, pIdSogg, pIdAsps);
					boolean isMTOMAbilitatoReq = false;
					boolean isMTOMAbilitatoRes= false;
					if(paAssociata.getMtomProcessor()!= null){
						if(paAssociata.getMtomProcessor().getRequestFlow() != null){
							if(paAssociata.getMtomProcessor().getRequestFlow().getMode() != null){
								MTOMProcessorType mode = paAssociata.getMtomProcessor().getRequestFlow().getMode();
								if(!mode.equals(MTOMProcessorType.DISABLE))
									isMTOMAbilitatoReq = true;
							}
						}
	
						if(paAssociata.getMtomProcessor().getResponseFlow() != null){
							if(paAssociata.getMtomProcessor().getResponseFlow().getMode() != null){
								MTOMProcessorType mode = paAssociata.getMtomProcessor().getResponseFlow().getMode();
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
				}
				
				// correlazione applicativa
				if(visualizzaCorrelazione) {
					de = new DataElement();
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA, pIdSogg, pIdPorta, pIdNome,pIdAsps);
					
					boolean isCorrelazioneApplicativaAbilitataReq = false;
					boolean isCorrelazioneApplicativaAbilitataRes = false;
					
					if (paAssociata.getCorrelazioneApplicativa() != null)
						isCorrelazioneApplicativaAbilitataReq = paAssociata.getCorrelazioneApplicativa().sizeElementoList() > 0;
	
					if (paAssociata.getCorrelazioneApplicativaRisposta() != null)
						isCorrelazioneApplicativaAbilitataRes = paAssociata.getCorrelazioneApplicativaRisposta().sizeElementoList() > 0;
					
					if(isCorrelazioneApplicativaAbilitataReq || isCorrelazioneApplicativaAbilitataRes)
						de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_ABILITATA);
					else 
						de.setValue(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_DISABILITATA);
					e.addElement(de);
				}
				
				// dump
				de = new DataElement();
				//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
				de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE, pIdSogg, pIdPorta, pIdAsps);
				DumpConfigurazione dumpConfigurazione = paAssociata.getDump();
				String statoDump = dumpConfigurazione == null ? CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO_DEFAULT : 
					(this.isDumpConfigurazioneAbilitato(dumpConfigurazione) ? CostantiControlStation.DEFAULT_VALUE_ABILITATO : CostantiControlStation.DEFAULT_VALUE_DISABILITATO);
				de.setValue(statoDump);
				e.addElement(de);
				
				// Protocol Properties
				if(this.isModalitaAvanzata()){
					de = new DataElement();
					//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_PROPRIETA_PROTOCOLLO_LIST, pIdSogg, pIdPorta,pIdAsps);
					if (contaListe) {
						int numProp = paAssociata.sizeProprietaList();
						ServletUtils.setDataElementVisualizzaLabel(de, (long) numProp );
					} else
						ServletUtils.setDataElementVisualizzaLabel(de);
					e.addElement(de);
				}

				// Altro
				if(this.isModalitaAvanzata()){
					de = new DataElement();
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CHANGE,pIdSogg, pNomePorta, pIdPorta,pIdAsps,pConfigurazioneAltro);
					ServletUtils.setDataElementVisualizzaLabel(de);
					e.addElement(de);
				}
				
				// Extended Servlet List
				if(extendedServletList!=null && extendedServletList.showExtendedInfo(this.request, this.session)){
					de = new DataElement();
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_EXTENDED_LIST, pIdPorta,pIdNome,pIdPorta, pIdSogg);
					if (contaListe) {
						int numExtended = extendedServletList.sizeList(paAssociata);
						ServletUtils.setDataElementVisualizzaLabel(de,Long.valueOf(numExtended));
					} else
						ServletUtils.setDataElementVisualizzaLabel(de);
					e.addElement(de);
				}
				
				// Abilitato
				de = new DataElement();
				de.setType(DataElementType.CHECKBOX);
				boolean statoPA = paAssociata.getStato().equals(StatoFunzionalita.ABILITATO);
				String statoMapping = statoPA ? CostantiControlStation.LABEL_PARAMETRO_PORTA_ABILITATO_TOOLTIP : CostantiControlStation.LABEL_PARAMETRO_PORTA_DISABILITATO_TOOLTIP;
				boolean url = true;
				if(mapping.isDefault() && allActionRedefined) {
					statoPA = false;
					statoMapping = this.getLabelAllAzioniRidefiniteTooltip(serviceBindingMessage);
					url = false;
				}
				de.setToolTip(statoMapping);
				de.setSelected(statoPA);
				if(url) {
					Parameter pAbilita = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ABILITA,  (statoPA ? Costanti.CHECK_BOX_DISABLED : Costanti.CHECK_BOX_ENABLED_TRUE));
					de.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_ABILITAZIONE,pIdSogg, pNomePorta, pIdPorta,pIdAsps, pAbilita);
				}
				e.addElement(de);
				
				dati.addElement(e);
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	
	private List<MappingErogazionePortaApplicativa> impostaFiltroAzioneMappingErogazione(String filtroAzione, List<MappingErogazionePortaApplicativa> lista, 
			ISearch ricerca, int idLista) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		if(StringUtils.isNotEmpty(filtroAzione) && !filtroAzione.equals(CostantiControlStation.DEFAULT_VALUE_AZIONE_NON_SELEZIONATA)) {
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
			List<MappingErogazionePortaApplicativa> newList = new ArrayList<>();
			newList.add(mappingTmp);
			this.pd.setNumEntries(1);
			return newList;
		} else {
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
			return lista;
		}
	}
	
	private boolean allActionsRedefinedMappingErogazione(List<String> azioni, List<MappingErogazionePortaApplicativa> lista) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		// verifico se tutte le azioni sono definite in regole specifiche
		boolean all = true;
		if(azioni!=null && azioni.size()>0) {
			for (String azione : azioni) {
				if(lista==null || lista.size()<=0) {
					all  = false;
					break;
				}
				boolean found = false;
				for (MappingErogazionePortaApplicativa mappingErogazionePortaApplicativa : lista) {
					PortaApplicativa paAssociata = this.porteApplicativeCore.getPortaApplicativa(mappingErogazionePortaApplicativa.getIdPortaApplicativa());
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
	
	private List<MappingFruizionePortaDelegata> impostaFiltroAzioneMappingFruizione(String filtroAzione, List<MappingFruizionePortaDelegata> lista, 
			ISearch ricerca, int idLista) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		if(StringUtils.isNotEmpty(filtroAzione) && !filtroAzione.equals(CostantiControlStation.DEFAULT_VALUE_AZIONE_NON_SELEZIONATA)) {
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
			List<MappingFruizionePortaDelegata> newList = new ArrayList<>();
			newList.add(mappingTmp);
			this.pd.setNumEntries(1);
			return newList;
		} else {
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
			return lista;
		}
	}
	
	private boolean allActionsRedefinedMappingFruizione(List<String> azioni, List<MappingFruizionePortaDelegata> lista) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
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
	
	public void serviziFruitoriMappingList(List<MappingFruizionePortaDelegata> lista, String idServizio, String idSoggettoFruitore, String idFruzione, ISearch ricerca)	throws Exception {
		try {
			
			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

			ServletUtils.addListElementIntoSession(this.session, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI_PORTE_DELEGATE,
					new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, idServizio),
					new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO, idSoggettoFruitore),
					new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID, idFruzione));

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
			
			// Prendo il nome e il tipo del servizio
			AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idServizio));
			AccordoServizioParteComune apc = this.apcCore.getAccordoServizio(asps.getIdAccordo()); 
			org.openspcoop2.core.registry.constants.ServiceBinding serviceBinding = apc.getServiceBinding();
			ServiceBinding serviceBindingMessage = this.apcCore.toMessageServiceBinding(apc.getServiceBinding());
			
			List<String> azioni = this.porteApplicativeCore.getAzioni(asps, apc, false, true, new ArrayList<String>());
			String filtroAzione = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_AZIONE);
			this.addFilterAzione(azioni, filtroAzione, serviceBindingMessage);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setSearch("");

			// Utilizza la configurazione come parent
			ServletUtils.setObjectIntoSession(this.session, PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE, PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT);
			
			lista = this.impostaFiltroAzioneMappingFruizione(filtroAzione, lista,ricerca, idLista);
			boolean allActionRedefined = false;
			if(lista.size()>1) {
				allActionRedefined = this.allActionsRedefinedMappingFruizione(azioni, lista);
			}
			
			boolean visualizzaMTOM = true;
			boolean visualizzaSicurezza = true;
			boolean visualizzaCorrelazione = true;
			
			switch (serviceBinding) {
			case REST:
				visualizzaMTOM = false;
				visualizzaSicurezza = true;
				visualizzaCorrelazione = false;
				break;
			case SOAP:
			default:
				visualizzaMTOM = true;
				visualizzaSicurezza = true;
				visualizzaCorrelazione = true;
				break;
			}

			// Prendo il nome e il tipo del soggetto fruitore
			Soggetto soggFruitore = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(idSoggettoFruitore));
		
			String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(soggFruitore.getTipo());
			
			String servizioTmpTile = this.getLabelIdServizio(asps);
			Parameter pIdServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, idServizio+ "");
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idServizio);
			Parameter pIdSoggettoErogatore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, asps.getIdSoggetto()+"");
			
			String fruitoreTmpTile = this.getLabelNomeSoggetto(protocollo, soggFruitore.getTipo(), soggFruitore.getNome());
		
			Fruitore fru = null;
			if(gestioneFruitori) {
				fru = asps.getFruitore(0);
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

			// setto la barra del titolo
			List<Parameter> lstParm = new ArrayList<Parameter>();

			if(gestioneFruitori) {
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORI, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
			}
			else {
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
			}
			if(gestioneFruitori) {
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_CONFIGURAZIONI_DI + servizioTmpTile, null));
			}
			else {
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FUITORI_DI  + servizioTmpTile, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST , pIdServizio,pIdSoggettoErogatore));
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_CONFIGURAZIONI_DI + fruitoreTmpTile, null));
			}
			
			this.pd.setSearchLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_NOME);
			this.pd.setSearchDescription("");
						
			// Controllo se richiedere il connettore
			boolean connettoreStatic = false;
			if(gestioneFruitori) {
				connettoreStatic = this.apsCore.isConnettoreStatic(protocollo);
			}
			
			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParm );

			List<String> listaLabel = new ArrayList<String>();

			listaLabel.add(this.getLabelAzioni(serviceBindingMessage));
			if(this.isModalitaAvanzata() && !connettoreStatic) {
				listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE);
			}
			listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONTROLLO_ACCESSI);
			listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI);
			if(visualizzaSicurezza) {
				listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY);
			}
			if(visualizzaMTOM) {
				listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM);
			}
			if(visualizzaCorrelazione) {
				listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA);
			}
			
			listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_DUMP_CONFIGURAZIONE);
			
			if(this.isModalitaAvanzata()) {
				listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PROTOCOL_PROPERTIES);
				listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_OPZIONI_AVANZATE);
			}
			if(extendedServletList!=null && extendedServletList.showExtendedInfo(this.request, this.session)){
				listaLabel.add(extendedServletList.getListTitle(this));
			}
			listaLabel.add(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_ABILITATO);
			
			String[] labels = listaLabel.toArray(new String[listaLabel.size()]);
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			Iterator<MappingFruizionePortaDelegata> it = lista.iterator();
			MappingFruizionePortaDelegata mapping = null;
			while (it.hasNext()) {
				mapping = it.next();
				PortaDelegata pdAssociata = this.porteDelegateCore.getPortaDelegata(mapping.getIdPortaDelegata());
				Vector<DataElement> e = new Vector<DataElement>();

				Parameter pIdPD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, "" + pdAssociata.getId());
				Parameter pNomePD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA, pdAssociata.getNome());
				Parameter pIdSoggPD = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, pdAssociata.getIdSoggetto() + "");

				@SuppressWarnings("unused")
				Parameter pConfigurazioneDati = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_CONFIGURAZIONE_DATI_INVOCAZIONE, Costanti.CHECK_BOX_ENABLED_TRUE);
				Parameter pConfigurazioneAltro = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_CONFIGURAZIONE_ALTRO, Costanti.CHECK_BOX_ENABLED_TRUE);
				
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
				
				// lista delle azioni
				DataElement de = new DataElement();
				List<String> listaAzioni = null;
				if(!mapping.isDefault()) {
					listaAzioni = pdAssociata.getAzione().getAzioneDelegataList();
					//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_AZIONE_LIST,pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore);
					if (contaListe) {
						int numAzioni = listaAzioni.size();
						ServletUtils.setDataElementVisualizzaLabel(de, (long) numAzioni );
					} else
						ServletUtils.setDataElementVisualizzaLabel(de);
					if(listaAzioni.size() > 0) {
						StringBuffer sb = new StringBuffer();
						for (String string : listaAzioni) {
							if(sb.length() >0)
								sb.append(", ");
							
							sb.append(string);
						}
						de.setToolTip(sb.toString());
					}
				} else {
					de.setValue(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MAPPING_FRUIZIONE_PD_AZIONE_DEFAULT);
				}
				de.setIdToRemove(pdAssociata.getNome());
				e.addElement(de);

				
				//if(gestioneFruitori) {
				// connettore	
				if(this.isModalitaAvanzata() && !connettoreStatic) {
					de = new DataElement();
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
						if(listaAzioni!=null && listaAzioni.size()>0) {
							azioneConnettore = listaAzioni.get(0);
						}
						
						String servletConnettore = AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE;
						if(mapping.isDefault()) {
							ServletUtils.setDataElementVisualizzaLabel(de);
							Parameter pId = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId()+"");
							Parameter pMyId = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID, fru.getId() + "");				
							Parameter actionIdPorta = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_FRUITORE_VIEW_CONNETTORE_MAPPING_AZIONE_ID_PORTA,pdAssociata.getId()+"");
							listParameter.add(actionIdPorta);
							listParameter.add(pId);
							listParameter.add(pMyId);
							listParameter.add(pIdSoggettoErogatore);
							listParameter.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE, idSoggettoLong + ""));
							if(azioneConnettore!=null && !"".equals(azioneConnettore)) {
								listParameter.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_FRUITORE_VIEW_CONNETTORE_MAPPING_AZIONE,azioneConnettore));
							}
							de.setUrl(servletConnettore, listParameter.toArray(new Parameter[1]));
						} else {
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
								servletConnettore = PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CONNETTORE_RIDEFINITO;
								de.setValue(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODALITA_CONNETTORE_RIDEFINITO); 
								de.setUrl(servletConnettore, pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore);
							} else {
								servletConnettore = PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CONNETTORE_DEFAULT;
								de.setValue(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MODALITA_CONNETTORE_DEFAULT); 
								de.setUrl(servletConnettore, pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore);
							}
						}
						
					}
					e.addElement(de);
				}
				//}

				// Controllo Accessi
				de = new DataElement();
				de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CONTROLLO_ACCESSI, pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore);
				
				String gestioneToken = null;
				if(pdAssociata.getGestioneToken()!=null && pdAssociata.getGestioneToken().getPolicy()!=null &&
						!"".equals(pdAssociata.getGestioneToken().getPolicy()) &&
						!"-".equals(pdAssociata.getGestioneToken().getPolicy())) {
					gestioneToken = StatoFunzionalita.ABILITATO.getValue();
				}
				
				String autenticazione = pdAssociata.getAutenticazione();
				String autenticazioneCustom = null;
				if (autenticazione != null && !TipoAutenticazione.getValues().contains(autenticazione)) {
					autenticazioneCustom = autenticazione;
					autenticazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM;
				}
				String autenticazioneOpzionale = "";
				if(pdAssociata.getAutenticazioneOpzionale()!=null){
					if (pdAssociata.getAutenticazioneOpzionale().equals(StatoFunzionalita.ABILITATO)) {
						autenticazioneOpzionale = Costanti.CHECK_BOX_ENABLED;
					}
				}
				String autorizzazioneContenuti = pdAssociata.getAutorizzazioneContenuto();
				
				String autorizzazione= null, autorizzazioneCustom = null;
				if (pdAssociata.getAutorizzazione() != null &&
						!TipoAutorizzazione.getAllValues().contains(pdAssociata.getAutorizzazione())) {
					autorizzazioneCustom = pdAssociata.getAutorizzazione();
					autorizzazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM;
				}
				else{
					autorizzazione = AutorizzazioneUtilities.convertToStato(pdAssociata.getAutorizzazione());
				}
				
				String statoControlloAccessi = this.getLabelStatoControlloAccessi(gestioneToken, autenticazione, autenticazioneOpzionale, autenticazioneCustom, autorizzazione, autorizzazioneContenuti,autorizzazioneCustom); 
				de.setValue(statoControlloAccessi);
				e.addElement(de);
				
				// validazione contenuti
				de = new DataElement();
				de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI, pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore);
				String statoValidazione = null;
				
				ValidazioneContenutiApplicativi vx = pdAssociata.getValidazioneContenutiApplicativi();
				if (vx == null) {
					statoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
				} else {
					if(vx.getStato()!=null)
						statoValidazione = vx.getStato().toString();
					if ((statoValidazione == null) || "".equals(statoValidazione)) {
						statoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_VALIDAZIONE_DISABILITATO;
					}
				}
				de.setValue(statoValidazione);
				e.addElement(de);
				
				// Message Security
				if(visualizzaSicurezza) {
					de = new DataElement();
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY, pIdPD, pIdSoggPD, pIdAsps, pIdFruitore);
					de.setValue(pdAssociata.getStatoMessageSecurity());
					e.addElement(de);
				}

				// MTOM (solo per servizi SOAP)
				if(visualizzaMTOM) {
					de = new DataElement();
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM, pIdPD, pIdSoggPD, pIdAsps, pIdFruitore);

					boolean isMTOMAbilitatoReq = false;
					boolean isMTOMAbilitatoRes= false;
					if(pdAssociata.getMtomProcessor()!= null){
						if(pdAssociata.getMtomProcessor().getRequestFlow() != null){
							if(pdAssociata.getMtomProcessor().getRequestFlow().getMode() != null){
								MTOMProcessorType mode = pdAssociata.getMtomProcessor().getRequestFlow().getMode();
								if(!mode.equals(MTOMProcessorType.DISABLE))
									isMTOMAbilitatoReq = true;
							}
						}
	
						if(pdAssociata.getMtomProcessor().getResponseFlow() != null){
							if(pdAssociata.getMtomProcessor().getResponseFlow().getMode() != null){
								MTOMProcessorType mode = pdAssociata.getMtomProcessor().getResponseFlow().getMode();
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
				}

				// Correlazione applicativa
				if(visualizzaCorrelazione) {
					de = new DataElement();
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA, pIdPD, pIdSoggPD, pIdAsps, pIdFruitore);
					
					boolean isCorrelazioneApplicativaAbilitataReq = false;
					boolean isCorrelazioneApplicativaAbilitataRes = false;
					
					if (pdAssociata.getCorrelazioneApplicativa() != null)
						isCorrelazioneApplicativaAbilitataReq = pdAssociata.getCorrelazioneApplicativa().sizeElementoList() > 0;
	
					if (pdAssociata.getCorrelazioneApplicativaRisposta() != null)
						isCorrelazioneApplicativaAbilitataRes = pdAssociata.getCorrelazioneApplicativaRisposta().sizeElementoList() > 0;
						
					if(isCorrelazioneApplicativaAbilitataReq || isCorrelazioneApplicativaAbilitataRes)
						de.setValue(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_ABILITATA);
					else 
						de.setValue(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_DISABILITATA);
					e.addElement(de);
				}
				
				// dump
				de = new DataElement();
				de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_DUMP_CONFIGURAZIONE, pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore);
				DumpConfigurazione dumpConfigurazione = pdAssociata.getDump();
				String statoDump = dumpConfigurazione == null ? CostantiControlStation.LABEL_PARAMETRO_DUMP_STATO_DEFAULT : 
					(this.isDumpConfigurazioneAbilitato(dumpConfigurazione) ? CostantiControlStation.DEFAULT_VALUE_ABILITATO : CostantiControlStation.DEFAULT_VALUE_DISABILITATO);
				de.setValue(statoDump);
				e.addElement(de);
				
				// Protocol Properties
				if(this.isModalitaAvanzata()){
					de = new DataElement();
					//fix: idsogg e' il soggetto proprietario della porta applicativa, e nn il soggetto virtuale
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_PROPRIETA_PROTOCOLLO_LIST, pIdSoggPD, pIdPD,pIdAsps,pIdFruitore);
					if (contaListe) {
						int numProp = pdAssociata.sizeProprietaList();
						ServletUtils.setDataElementVisualizzaLabel(de, (long) numProp );
					} else
						ServletUtils.setDataElementVisualizzaLabel(de);
					e.addElement(de);
				}
			
				// Altro
				if(this.isModalitaAvanzata()){
					de = new DataElement();
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CHANGE,pIdPD,pNomePD,pIdSoggPD, pIdAsps, pIdFruitore, pConfigurazioneAltro);
					ServletUtils.setDataElementVisualizzaLabel(de);
					e.addElement(de);
				}
				
				// pd exdended list
				if(extendedServletList!=null && extendedServletList.showExtendedInfo(this.request, this.session)){
					de = new DataElement();
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_EXTENDED_LIST, pIdPD, pNomePD, pIdSoggPD, pIdAsps, pIdFruitore);
					if (contaListe) {
						int numExtended = extendedServletList.sizeList(pdAssociata);
						ServletUtils.setDataElementVisualizzaLabel(de,Long.valueOf(numExtended));
					} else
						ServletUtils.setDataElementVisualizzaLabel(de);
					e.addElement(de);
				}
				
				// Abilitato
				de = new DataElement();
				de.setType(DataElementType.CHECKBOX);
				boolean statoPD = pdAssociata.getStato().equals(StatoFunzionalita.ABILITATO);
				String statoMapping = statoPD ? CostantiControlStation.LABEL_PARAMETRO_PORTA_ABILITATO_TOOLTIP : CostantiControlStation.LABEL_PARAMETRO_PORTA_DISABILITATO_TOOLTIP;
				boolean url = true;
				if(mapping.isDefault() && allActionRedefined) {
					statoPD = false;
					statoMapping = this.getLabelAllAzioniRidefiniteTooltip(serviceBindingMessage);
					url = false;
				}
				de.setToolTip(statoMapping);
				de.setSelected(statoPD);
				if(url) {
					Parameter pAbilita = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ABILITA,  (statoPD ? Costanti.CHECK_BOX_DISABLED : Costanti.CHECK_BOX_ENABLED_TRUE));
					de.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_ABILITAZIONE,pIdPD,pNomePD,pIdSoggPD, pIdAsps, pIdFruitore, pAbilita);
				}
				e.addElement(de);
				
				dati.addElement(e);
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

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
		else if(tipoOp.equals(TipoOperazione.ADD) || modificaAbilitata){
			de.setType(DataElementType.CHECKBOX);
			infoCorrelataShow = true;
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
			String erogazioneRuolo,String erogazioneAutenticazione,String erogazioneAutenticazioneOpzionale,String erogazioneAutorizzazione, boolean erogazioneIsSupportatoAutenticazioneSoggetti,
			String erogazioneAutorizzazioneAutenticati, String erogazioneAutorizzazioneRuoli, String erogazioneAutorizzazioneRuoliTipologia, String erogazioneAutorizzazioneRuoliMatch,
			List<String> soggettiAutenticati, List<String> soggettiAutenticatiLabel, String soggettoAutenticato,
			String tipoProtocollo, List<String> listaTipiProtocollo,
			String[] soggettiFruitoriList, String[] soggettiFruitoriListLabel, String providerSoggettoFruitore, String tipoSoggettoFruitore, String nomeSoggettoFruitore,
			String fruizioneServizioApplicativo,String fruizioneRuolo,String fruizioneAutenticazione,String fruizioneAutenticazioneOpzionale, String fruizioneAutorizzazione,
			String fruizioneAutorizzazioneAutenticati,String fruizioneAutorizzazioneRuoli, String fruizioneAutorizzazioneRuoliTipologia, String fruizioneAutorizzazioneRuoliMatch,
			List<String> saList,String gestioneToken, String[] gestioneTokenPolicyLabels, String[] gestioneTokenPolicyValues,
			String gestioneTokenPolicy, String gestioneTokenValidazioneInput, String gestioneTokenIntrospection, String gestioneTokenUserInfo, String gestioneTokenForward,
			String autorizzazioneScope,  String scope, String autorizzazioneScopeMatch) throws Exception{

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

		boolean visualizzaVersione = this.apsCore.isSupportatoVersionamentoAccordiServizioParteSpecifica(tipoProtocollo);

		boolean modificaAbilitata = ( (this.core.isShowGestioneWorkflowStatoDocumenti()==false) || (StatiAccordo.finale.toString().equals(oldStato)==false) );

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
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_INFO_GENERALI);
		de.setType(DataElementType.TITLE);
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
		
		
		de = new DataElement();
		de.setLabel(asLabel);
		de.setType(DataElementType.SUBTITLE);
		dati.addElement(de);
		
		// Accordo 
		//if(tipoOp.equals("add") || modificaAbilitata){
		IDAccordo idAccordoParteComune = null;
		if(tipoOp.equals(TipoOperazione.ADD) ){
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
		}else{
			if(!modificaAbilitata || (asCompatibili==null || asCompatibili.size()<=1)){
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
			if(showReferente) {
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

		switch(serviceBinding) {
		case REST:
			
			de = new DataElement();
			de.setType(DataElementType.TEXT);
			de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_SERVICE_BINDING);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PORT_TYPE+"__LABEL");
			de.setValue(CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_REST);
			dati.addElement(de);
			
			//Servizio (portType)  nascosto nel caso REST
			de = new DataElement();
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PORT_TYPE);
			de.setValue(portType);
			dati.addElement(de);
			break;
		case SOAP:
		default:
			
			de = new DataElement();
			de.setType(DataElementType.TEXT);
			de.setLabel(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_SERVICE_BINDING);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PORT_TYPE+"__LABEL");
			de.setValue(CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_SOAP);
			dati.addElement(de);
			
			//Servizio (portType)  visibile nel caso SOAP
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
					de.setValue(portType);
					dati.addElement(de);

					de = new DataElement();
					de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SERVIZIO);
					de.setType(DataElementType.TEXT);
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
		

		//Sezione Soggetto Erogatore (provider)

		if(!gestioneErogatori) {
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SOGGETTO_EROGATORE);
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
		}

		de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_PROVIDER_EROGATORE);
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_EROGATORE);
		if (tipoOp.equals(TipoOperazione.ADD) && !gestioneErogatori) {
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

			if(!gestioneErogatori) {
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


		//Sezione Servizio

		Vector<DataElement> datiCorrelati = new Vector<>();
		boolean showInfoCorrelata = this.addInfoCorrelata(tipoOp, portType, modificaAbilitata, servcorr, oldStato,
				tipoProtocollo, serviceBinding, datiCorrelati);
		
		boolean modificaAbilitataOrOperazioneAdd = tipoOp.equals(TipoOperazione.ADD) || modificaAbilitata;
		
		boolean showSceltaNomeServizioDisabilitata = !isModalitaAvanzata && 
				(
						TipoOperazione.ADD.equals(tipoOp) || 
						(ServiceBinding.SOAP.equals(serviceBinding) && nomeServizio!=null && nomeServizio.equals(portType)) ||
						(ServiceBinding.REST.equals(serviceBinding) && nomeServizio!=null && nomeServizio.equals(idAccordoParteComune.getNome()))
				);
		
		boolean showFlagPrivato = this.core.isShowFlagPrivato() &&  (tipoOp.equals(TipoOperazione.ADD) || 
				modificaAbilitata) && isModalitaAvanzata;
		
		boolean showFlagPrivatoLabel = this.core.isShowFlagPrivato() && tipoOp.equals(TipoOperazione.CHANGE ) && 
			(this.core.isShowGestioneWorkflowStatoDocumenti() && 
					StatiAccordo.finale.toString().equals(oldStato) ) &&
					isModalitaAvanzata;
		
		boolean showTipoServizio = (this.apsCore.getTipiServiziGestitiProtocollo(tipoProtocollo,serviceBinding).size()>1);
		
		boolean showVersioneProtocollo = modificaAbilitataOrOperazioneAdd && (this.apsCore.getVersioniProtocollo(tipoProtocollo).size()>1);
		
		
		boolean showLabelServizio = this.isModalitaAvanzata() || 
				showTipoServizio ||
				(!showSceltaNomeServizioDisabilitata) ||
				visualizzaVersione ||
				showInfoCorrelata ||
				showFlagPrivato ||
				showFlagPrivatoLabel ||
				showVersioneProtocollo ||
				(this.core.isShowGestioneWorkflowStatoDocumenti() && (TipoOperazione.CHANGE.equals(tipoOp) || this.isModalitaAvanzata()));
		
		if(showLabelServizio){
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SERVIZIO);
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
		}

		if(isModalitaAvanzata){
			
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
				de.setValues(tipiLabel);
				de.setSelected(tipoServizio);
				de.setType(DataElementType.SELECT);
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO);
				de.setSize(this.getSize());
				if(tipoOp.equals(TipoOperazione.ADD)) {
					de.setPostBack(true);
				}
				dati.addElement(de);
			}else{
				de = new DataElement();
				de.setValue(tipoServizio);
				de.setType(DataElementType.TEXT);
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_TIPO);
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO);
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
		
		if(visualizzaVersione){

			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_VERSIONE);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VERSIONE);
			de.setValue(((versione==null || "".equals(versione)) ? "1" : versione));
			if(visualizzaVersione){
				if( modificaAbilitata ){
					de.setType(DataElementType.NUMBER);
					de.setMinValue(1);
					de.setMaxValue(999);
					
					//de.setRequired(true);
				}else{
					de.setType(DataElementType.TEXT);
				}
			}else{
				de.setType(DataElementType.HIDDEN);
			}
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VERSIONE);
			dati.addElement(de);
			
		}
		
		dati.addAll(datiCorrelati);

		de = new DataElement();
		de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_PRIVATO);
		if (showFlagPrivato) {
			de.setType(DataElementType.CHECKBOX);
			de.setSelected(privato ? Costanti.CHECK_BOX_ENABLED : "");
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(privato ? Costanti.CHECK_BOX_ENABLED : "");
		}
		de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PRIVATO);
		dati.addElement(de);

		if(showFlagPrivatoLabel){
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
		if(showVersioneProtocollo){
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
				if( tipoOp.equals(TipoOperazione.ADD) || StatiAccordo.finale.toString().equals(oldStato)==false ){
					de.setType(DataElementType.SELECT);
					de.setValues(StatiAccordo.toArray());
					de.setLabels(StatiAccordo.toLabel());
					de.setSelected(statoPackage);
					de.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO_PACKAGE);
				}else{
					
					DataElement deLabel = new DataElement();
					deLabel.setType(DataElementType.TEXT);
					deLabel.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_STATO);
					deLabel.setValue(StatiAccordo.upper(StatiAccordo.finale.toString()));
					deLabel.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO_PACKAGE+"__label");
					dati.addElement(deLabel);
					
					de.setType(DataElementType.HIDDEN);
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
					de.setType(DataElementType.SELECT);
					de.setValues(StatiAccordo.toArray());
					de.setLabels(StatiAccordo.toLabel());
					de.setSelected(statoPackage);
				}else{
					
					DataElement deLabel = new DataElement();
					deLabel.setType(DataElementType.TEXT);
					deLabel.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_STATO);
					deLabel.setValue(StatiAccordo.upper(StatiAccordo.finale.toString()));
					deLabel.setName(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO_PACKAGE+"__label");
					dati.addElement(deLabel);
					
					de.setType(DataElementType.HIDDEN);
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
		
		
		//Sezione Soggetto Fruitore
		if(gestioneFruitori) {
			
			boolean visualizzaSoggetto = false; // si e' deciso di non farlo vedere essendo solo uno
			
			if(visualizzaSoggetto) {
				de = new DataElement();
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_SOGGETTO_FRUITORE);
				de.setType(DataElementType.SUBTITLE);
				dati.addElement(de);
			}
	
			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_PROVIDER_FRUITORE);
			de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE);
			if (tipoOp.equals(TipoOperazione.ADD) && visualizzaSoggetto) {
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
	
				if(visualizzaSoggetto) {
					de = new DataElement();
					de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_PROVIDER_FRUITORE);
					de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE_AS_TEXT);
					de.setType(DataElementType.TEXT);
					de.setValue(this.getLabelNomeSoggetto(tipoProtocollo, tipoSoggettoFruitore, nomeSoggettoFruitore));
					dati.addElement(de);
				}
			}
		}
		
		if(serviceBinding.equals(ServiceBinding.SOAP) && interfaceType.equals(org.openspcoop2.protocol.manifest.constants.InterfaceType.WSDL_11) && showPortiAccesso){

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
					if(isSupportoAsincrono){
						if(isRuoloNormale){
							de = new DataElement();
							de.setType(DataElementType.LINK);
							de.setUrl(
									AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_WSDL_CHANGE,
									new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, id),
									new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_EROGATORE));
	
							de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_EROGATORE_ESTESO);
							dati.addElement(de);
						}else{
							de = new DataElement();
							de.setType(DataElementType.LINK);
							de.setUrl(
									AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_WSDL_CHANGE,
									new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, id),
									new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_FRUITORE));
							de.setValue(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_WSDL_IMPLEMENTATIVO_FRUITORE_ESTESO);
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
		
		boolean visualizzaSezioneScope = (gestioneToken!= null && gestioneToken.equals(StatoFunzionalita.ABILITATO.getValue())) && (ServletUtils.isCheckBoxEnabled(gestioneTokenIntrospection) || ServletUtils.isCheckBoxEnabled(gestioneTokenValidazioneInput)); 

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
			
			this.controlloAccessiAutenticazione(dati, erogazioneAutenticazione, null, erogazioneAutenticazioneOpzionale, false, erogazioneIsSupportatoAutenticazioneSoggetti);
			
			this.controlloAccessiGestioneToken(dati, tipoOp, gestioneToken, gestioneTokenPolicyLabels, gestioneTokenPolicyValues, gestioneTokenPolicy, 
					gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenForward, null);
			
			this.controlloAccessiAutorizzazione(dati, tipoOp, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD, null,
					erogazioneAutenticazione, erogazioneAutorizzazione, null, 
					erogazioneAutorizzazioneAutenticati, null, 0, soggettiAutenticati, soggettiAutenticatiLabel, soggettoAutenticato,
					erogazioneAutorizzazioneRuoli, null, 0, erogazioneRuolo,
					erogazioneAutorizzazioneRuoliTipologia, erogazioneAutorizzazioneRuoliMatch, 
					false, erogazioneIsSupportatoAutenticazioneSoggetti, contaListe, false, false,autorizzazioneScope,null,0,scope,autorizzazioneScopeMatch,visualizzaSezioneScope);
			
		}
		
		if(tipoOp.equals(TipoOperazione.ADD) && gestioneFruitori) {
			
			// Controllo Accesso Fruizione
			
			this.controlloAccessiAutenticazione(dati, fruizioneAutenticazione, null, fruizioneAutenticazioneOpzionale, false, true);
		
			this.controlloAccessiGestioneToken(dati, tipoOp, gestioneToken, gestioneTokenPolicyLabels, gestioneTokenPolicyValues, gestioneTokenPolicy, gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenForward, null);

			this.controlloAccessiAutorizzazione(dati, tipoOp, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD,null,
					fruizioneAutenticazione, fruizioneAutorizzazione, null, 
					fruizioneAutorizzazioneAutenticati, null, 0, saList, fruizioneServizioApplicativo,
					fruizioneAutorizzazioneRuoli, null, 0, fruizioneRuolo,
					fruizioneAutorizzazioneRuoliTipologia, fruizioneAutorizzazioneRuoliMatch, 
					false, true, contaListe, true, false,autorizzazioneScope,null,0,scope,autorizzazioneScopeMatch,visualizzaSezioneScope);
			
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

		boolean modificaAbilitata = ( (this.core.isShowGestioneWorkflowStatoDocumenti()==false) || (StatiAccordo.finale.toString().equals(oldStato)==false) );

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

		if( this.apsCore.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(asps.getStatoPackage())){
			this.pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);

			if(this.core.isShowInterfacceAPI()) {
				de = new DataElement();
				de.setLabel("");
				de.setType(DataElementType.TEXT_AREA_NO_EDIT);
				de.setValue(oldwsdl);
				de.setRows(30);
				de.setCols(110);
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
					de.setRows(30);
					de.setCols(110);
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



		if(this.apsCore.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(myFru.getStatoPackage())){
			
			this.pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);
			
			if(wsdl != null && !wsdl.isEmpty()){
				this.pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);
				
				if(this.core.isShowInterfacceAPI()) {
					de = new DataElement();
					de.setLabel("");
					de.setType(DataElementType.TEXT_AREA_NO_EDIT);
					de.setValue( wsdl);
					de.setRows(30);
					de.setCols(110);
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
					de.setRows(30);
					de.setCols(110);
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
			List<String> saList, ServiceBinding serviceBinding, org.openspcoop2.protocol.manifest.constants.InterfaceType interfaceType,
			String azioneConnettore, String azioneConnettoreIdPorta, String accessoDaAPSParametro, Integer parentPD,
			String gestioneToken, String[] gestioneTokenPolicyLabels, String[] gestioneTokenPolicyValues,
			String gestioneTokenPolicy, String gestioneTokenValidazioneInput, String gestioneTokenIntrospection, String gestioneTokenUserInfo, String gestioneTokenForward,
			String autorizzazioneScope,  String scope, String autorizzazioneScopeMatch) throws Exception {
		
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

			
			if(this.core.isShowGestioneWorkflowStatoDocumenti()){
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
			if(soggettiList!=null && soggettiList.length>0){
				String soggettoId = idSoggettoFruitore;
				if(soggettoId==null || "".equals(soggettoId)){
					soggettoId = soggettiList[0];
				}
				Soggetto sogg = this.soggettiCore.getSoggettoRegistro(Long.parseLong(soggettoId));
				if(!this.pddCore.isPddEsterna(sogg.getPortaDominio())){
					isSoggettoGestitoPorta = true;	
				}
			}




			if(isSoggettoGestitoPorta){
				
				boolean visualizzaSezioneScope = (gestioneToken!= null && gestioneToken.equals(StatoFunzionalita.ABILITATO.getValue())) && (ServletUtils.isCheckBoxEnabled(gestioneTokenIntrospection) || ServletUtils.isCheckBoxEnabled(gestioneTokenValidazioneInput));
				
				this.controlloAccessiAutenticazione(dati, fruizioneAutenticazione, null, fruizioneAutenticazioneOpzionale, false, true);
				
				this.controlloAccessiGestioneToken(dati, tipoOp, gestioneToken, gestioneTokenPolicyLabels, gestioneTokenPolicyValues, gestioneTokenPolicy, gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenForward, null);

				this.controlloAccessiAutorizzazione(dati, tipoOp, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD,null,
						fruizioneAutenticazione, fruizioneAutorizzazione, null, 
						fruizioneAutorizzazioneAutenticati, null, 0, saList, fruizioneServizioApplicativo,
						fruizioneAutorizzazioneRuoli, null, 0, fruizioneRuolo,
						fruizioneAutorizzazioneRuoliTipologia, fruizioneAutorizzazioneRuoliMatch, 
						false, true, contaListe, true, false,autorizzazioneScope,null,0,scope,autorizzazioneScopeMatch,visualizzaSezioneScope);
	
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

						
			if(this.core.isShowGestioneWorkflowStatoDocumenti() && this.isModalitaCompleta() && !viewOnlyConnettore) {
				de = new DataElement();
				de.setType(DataElementType.TITLE);
				de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_STATO);
				dati.addElement(de);
			}

			de = new DataElement();
			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_STATO);
			if(this.core.isShowGestioneWorkflowStatoDocumenti()){
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
		
		
// [TODO] perche' e' stato messo?		
//		if(isModalitaAvanzata){
//			DataElement de = new DataElement();
//			de.setLabel(AccordiServizioParteSpecificaCostanti.LABEL_APS_ALTRE_INFORMAZIONI );
//			de.setType(DataElementType.TITLE);
//			dati.addElement(de);
//		}


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
			Vector<DataElement> dati) throws Exception {
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

		if(this.isEditModeInProgress() && this.apsCore.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(asps.getStatoPackage())){
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
				de.setRows(30);
				de.setCols(80);
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
		dati.add(saveAs);

		return dati;
	}
	
	public Vector<DataElement> addConfigurazioneErogazioneToDati(TipoOperazione tipoOperazione, Vector<DataElement> dati, String nome,
			String[] azioni, String[] azioniDisponibiliList, 
			String idAsps, String idSoggettoErogatoreDelServizio, String identificazione, 
			AccordoServizioParteSpecifica asps, AccordoServizioParteComune as, ServiceBinding serviceBinding, String modeCreazione, String modeCreazioneConnettore,
			String[] listaMappingLabels, String[] listaMappingValues, String mapping, String mappingLabel, String nomeSA, String [] saSoggetti, 
			String erogazioneAutenticazione, String erogazioneAutenticazioneOpzionale, boolean erogazioneIsSupportatoAutenticazioneSoggetti,
			String erogazioneAutorizzazione, String erogazioneAutorizzazioneAutenticati, String erogazioneAutorizzazioneRuoli,
			String erogazioneRuolo, String erogazioneAutorizzazioneRuoliTipologia, String erogazioneAutorizzazioneRuoliMatch,
			List<String> soggettiAutenticati,  List<String> soggettiAutenticatiLabel, String soggettoAutenticato,
			String gestioneToken, String[] gestioneTokenPolicyLabels, String[] gestioneTokenPolicyValues,
			String gestioneTokenPolicy, String gestioneTokenValidazioneInput, String gestioneTokenIntrospection, String gestioneTokenUserInfo, String gestioneTokenForward,
			String autorizzazioneScope, String scope, String autorizzazioneScopeMatch) throws Exception {
		
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
		
		
		// Azione
		de = new DataElement();
		de.setLabel(this.getLabelAzioni(serviceBinding));
		de.setValues(azioniDisponibiliList);
		de.setSelezionati(azioni);
		de.setType(DataElementType.MULTI_SELECT);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AZIONI);
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
		
		// 	modo creazione: se erediti la configurazione da una precedente allora devi solo sezionarla, altrimenti devi compilare le sezioni SA e controllo accessi
		if(modeCreazione.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_MODO_CREAZIONE_EREDITA)) {
			// mapping
			de = new DataElement();
			de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MAPPING);
			de.setLabels(listaMappingLabels); 
			de.setValues(listaMappingValues);
			de.setSelected(mapping);
			de.setToolTip(mappingLabel);
			de.setType(DataElementType.SELECT);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MAPPING);
			de.setPostBack(true);
			dati.addElement(de);
		} 
		
		// mode Connettore
		de = new DataElement();
		de.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MODO_CREAZIONE_CONNETTORE);
		de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_CREAZIONE_CONNETTORE);
		if(this.isModalitaStandard()) {
			de.setType(DataElementType.HIDDEN);
			de.setValue(modeCreazione);
		}
		else {
			de.setType(DataElementType.CHECKBOX);
			de.setSelected(ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore));
			de.setPostBack(true);
		}
		dati.addElement(de);
		
		if(!modeCreazione.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_MODO_CREAZIONE_EREDITA)) {
			
			boolean visualizzaSezioneScope = (gestioneToken!= null && gestioneToken.equals(StatoFunzionalita.ABILITATO.getValue())) && (ServletUtils.isCheckBoxEnabled(gestioneTokenIntrospection) || ServletUtils.isCheckBoxEnabled(gestioneTokenValidazioneInput));
			// Controllo Accesso
			
			this.controlloAccessiAutenticazione(dati, erogazioneAutenticazione, null, erogazioneAutenticazioneOpzionale, false, erogazioneIsSupportatoAutenticazioneSoggetti);
			
			this.controlloAccessiGestioneToken(dati, tipoOperazione, gestioneToken, gestioneTokenPolicyLabels, gestioneTokenPolicyValues, gestioneTokenPolicy, gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenForward, null);

			this.controlloAccessiAutorizzazione(dati, tipoOperazione, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_ADD, null,
					erogazioneAutenticazione, erogazioneAutorizzazione, null, 
					erogazioneAutorizzazioneAutenticati, null, 0, soggettiAutenticati, soggettiAutenticatiLabel, soggettoAutenticato,
					erogazioneAutorizzazioneRuoli, null, 0, erogazioneRuolo,
					erogazioneAutorizzazioneRuoliTipologia, erogazioneAutorizzazioneRuoliMatch, 
					false, erogazioneIsSupportatoAutenticazioneSoggetti, contaListe, false, false,autorizzazioneScope,null,0,scope,autorizzazioneScopeMatch,visualizzaSezioneScope);
		}
		
		
		return dati;
	}

	public boolean configurazioneErogazioneCheckData(TipoOperazione tipoOp, String nome, String[] azioni,
			AccordoServizioParteSpecifica asps, List<String> azioniOccupate,
			String modeCreazione, String idPorta, boolean isSupportatoAutenticazione) throws Exception{
		
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
		
		if(modeCreazione.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_MODO_CREAZIONE_EREDITA)) {
			
		} else {
			String autenticazione = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE);
			String autenticazioneCustom = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM);
			String autenticazioneOpzionale = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_OPZIONALE);
			String autorizzazione = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE);
			String autorizzazioneCustom = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM);
			String autorizzazioneAutenticati = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE);
			String autorizzazioneRuoli = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI);
			String autorizzazioneRuoliTipologia = this.getParameter(CostantiControlStation.PARAMETRO_RUOLO_TIPOLOGIA);
			String ruoloMatch = this.getParameter(CostantiControlStation.PARAMETRO_RUOLO_MATCH);
			
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
			String autorizzazioneScope = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_SCOPE);
			String autorizzazioneScopeMatch = this.getParameter(CostantiControlStation.PARAMETRO_SCOPE_MATCH);
			
			if(this.controlloAccessiCheck(tipoOp, autenticazione, autenticazioneOpzionale, 
					autorizzazione, autorizzazioneAutenticati, autorizzazioneRuoli, 
					autorizzazioneRuoliTipologia, ruoloMatch, 
					isSupportatoAutenticazione, false, pa, ruoli,gestioneToken, gestioneTokenPolicy, 
					gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,autorizzazioneScope,autorizzazioneScopeMatch)==false){
				return false;
			}
		}
		
		return true;
	}
	
	public boolean configurazioneFruizioneCheckData(TipoOperazione tipoOp, String nome, String [] azioni,
			AccordoServizioParteSpecifica asps, List<String> azioniOccupate,
			String modeCreazione, String idPorta, boolean isSupportatoAutenticazione) throws Exception{
		
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
		
		if(modeCreazione.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_MODO_CREAZIONE_EREDITA)) {
			
		} else {
			String autenticazione = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE);
			String autenticazioneCustom = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM);
			String autenticazioneOpzionale = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_OPZIONALE);
			String autorizzazione = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE);
			String autorizzazioneCustom = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM);
			String autorizzazioneAutenticati = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE);
			String autorizzazioneRuoli = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI);
			String autorizzazioneRuoliTipologia = this.getParameter(CostantiControlStation.PARAMETRO_RUOLO_TIPOLOGIA);
			String ruoloMatch = this.getParameter(CostantiControlStation.PARAMETRO_RUOLO_MATCH);
			
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
			String autorizzazioneScope = this.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_SCOPE);
			String autorizzazioneScopeMatch = this.getParameter(CostantiControlStation.PARAMETRO_SCOPE_MATCH);
			
			if(this.controlloAccessiCheck(tipoOp, autenticazione, autenticazioneOpzionale, 
					autorizzazione, autorizzazioneAutenticati, autorizzazioneRuoli, 
					autorizzazioneRuoliTipologia, ruoloMatch, 
					isSupportatoAutenticazione, true, pd, ruoli,gestioneToken, gestioneTokenPolicy, 
					gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,autorizzazioneScope,autorizzazioneScopeMatch)==false){
				return false;
			}
		}
		
		return true;
	}


	public Vector<DataElement> addConfigurazioneFruizioneToDati(TipoOperazione tipoOp, Vector<DataElement> dati, String nome,
			String [] azioni, String[] azioniDisponibiliList, String idAsps,
			IDSoggetto idSoggettoFruitore, String identificazione, AccordoServizioParteSpecifica asps,
			AccordoServizioParteComune as, ServiceBinding serviceBinding, String modeCreazione, String modeCreazioneConnettore,
			String[] listaMappingLabels, String[] listaMappingValues, String mapping, String mappingLabel, List<String> saList,
			String nomeSA, String fruizioneAutenticazione, String fruizioneAutenticazioneOpzionale, boolean erogazioneIsSupportatoAutenticazioneSoggetti,
			String fruizioneAutorizzazione, String fruizioneAutorizzazioneAutenticati,
			String fruizioneAutorizzazioneRuoli, String fruizioneRuolo, String fruizioneAutorizzazioneRuoliTipologia,
			String fruizioneAutorizzazioneRuoliMatch, String fruizioneServizioApplicativo,
			String gestioneToken, String[] gestioneTokenPolicyLabels, String[] gestioneTokenPolicyValues,
			String gestioneTokenPolicy, String gestioneTokenValidazioneInput, String gestioneTokenIntrospection, String gestioneTokenUserInfo, String gestioneTokenForward,
			String autorizzazioneScope,  String scope, String autorizzazioneScopeMatch) throws Exception{
		
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
		
		// Azione
		de = new DataElement();
		de.setLabel(this.getLabelAzioni(serviceBinding));
		de.setValues(azioniDisponibiliList);
		de.setSelezionati(azioni); 
		de.setType(DataElementType.MULTI_SELECT);
		de.setName(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONI);
//		de.setPostBack(true, true);
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
			de.setLabel(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MAPPING);
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
			
			boolean visualizzaSezioneScope = (gestioneToken!= null && gestioneToken.equals(StatoFunzionalita.ABILITATO.getValue())) && (ServletUtils.isCheckBoxEnabled(gestioneTokenIntrospection) || ServletUtils.isCheckBoxEnabled(gestioneTokenValidazioneInput));
			
			this.controlloAccessiAutenticazione(dati, fruizioneAutenticazione, null, fruizioneAutenticazioneOpzionale, false, true);
			
			this.controlloAccessiGestioneToken(dati, tipoOp, gestioneToken, gestioneTokenPolicyLabels, gestioneTokenPolicyValues, gestioneTokenPolicy, gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenForward, null);
		
			this.controlloAccessiAutorizzazione(dati, tipoOp, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD,null,
				fruizioneAutenticazione, fruizioneAutorizzazione, null, 
				fruizioneAutorizzazioneAutenticati, null, 0, saList, fruizioneServizioApplicativo,
				fruizioneAutorizzazioneRuoli, null, 0, fruizioneRuolo,
				fruizioneAutorizzazioneRuoliTipologia, fruizioneAutorizzazioneRuoliMatch, 
				false, erogazioneIsSupportatoAutenticazioneSoggetti, contaListe, true, false,autorizzazioneScope,null,0,scope,autorizzazioneScopeMatch,visualizzaSezioneScope);
		}
		
		return dati;
	}

}
