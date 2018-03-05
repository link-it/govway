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
package org.openspcoop2.web.ctrlstat.servlet.sa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipologiaErogazione;
import org.openspcoop2.core.config.constants.TipologiaFruizione;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.ruoli.RuoliCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ServiziApplicativiHelper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServiziApplicativiHelper extends ConnettoriHelper {

	public ServiziApplicativiHelper(HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(request, pd,  session);
	}

	// Controlla i dati dell'invocazione servizio del servizioApplicativo
	public boolean servizioApplicativoEndPointCheckData(List<ExtendedConnettore> listExtendedConnettore)
			throws Exception {
		try{
			String sbustamento= this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_SOAP);
			String sbustamentoInformazioniProtocolloRichiesta = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RICHIESTA);
			String getmsg = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_MESSAGE_BOX);
			String tipoauth = this.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_TIPO_AUTENTICAZIONE);
			if (tipoauth == null) {
				tipoauth = ConnettoriCostanti.DEFAULT_AUTENTICAZIONE_TIPO;
			}
			String utente = this.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
			String password = this.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			// String confpw = this.getParameter("confpw");
			
			// Campi obbligatori
			if (tipoauth.equals("")) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare il Tipo");
				return false;
			}
			if (tipoauth.equals(CostantiConfigurazione.CREDENZIALE_BASIC.toString()) && (utente.equals("") || password.equals("") /*
			 * ||
			 * confpw
			 * .
			 * equals
			 * (
			 * ""
			 * )
			 */)) {
				String tmpElenco = "";
				if (utente.equals("")) {
					tmpElenco = ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME;
				}
				if (password.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD;
					} else {
						tmpElenco = tmpElenco + ", "+ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD;
					}
				}
				/*
				 * if (confpw.equals("")) { if (tmpElenco.equals("")) { tmpElenco =
				 * "Conferma password"; } else { tmpElenco = tmpElenco + ", Conferma
				 * password"; } }
				 */
				this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if (tipoauth.equals(CostantiConfigurazione.CREDENZIALE_BASIC.toString()) && ((utente.indexOf(" ") != -1) || (password.indexOf(" ") != -1))) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}

			// Controllo che i campi DataElementType.SELECT abbiano uno dei valori ammessi
			if (!getmsg.equals(CostantiConfigurazione.ABILITATO.toString()) && !getmsg.equals(CostantiConfigurazione.DISABILITATO.toString())) {
				this.pd.setMessage("Get Message dev'essere "+CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);
				return false;
			}
			if (!tipoauth.equals(CostantiConfigurazione.CREDENZIALE_BASIC.toString()) && 
					!tipoauth.equals(CostantiConfigurazione.CREDENZIALE_SSL.toString()) && 
					!tipoauth.equals("nessuna")) {
				this.pd.setMessage("Tipo Autenticazione dev'essere "+CostantiConfigurazione.CREDENZIALE_BASIC.toString()+", "
						+ ""+CostantiConfigurazione.CREDENZIALE_SSL.toString()+" o nessuna");
				return false;
			}
			if (!sbustamento.equals(CostantiConfigurazione.ABILITATO.toString()) && !sbustamento.equals(CostantiConfigurazione.DISABILITATO.toString())) {
				this.pd.setMessage("Sbustamento SOAP dev'essere "+CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);
				return false;
			}
			if (!sbustamentoInformazioniProtocolloRichiesta.equals(CostantiConfigurazione.ABILITATO.toString()) && !sbustamentoInformazioniProtocolloRichiesta.equals(CostantiConfigurazione.DISABILITATO.toString())) {
				this.pd.setMessage("Sbustamento Informazioni del Protocollo dev'essere "+CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);
				return false;
			}


			// Controllo che le password corrispondano
			/*
			 * if (tipoauth.equals("basic") && !password.equals(confpw)) {
			 * this.pd.setMessage("Le password non corrispondono"); return false; }
			 */

			if (!this.endPointCheckData(listExtendedConnettore)) {
				return false;
			}

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public Vector<DataElement> addServizioApplicativoToDati(Vector<DataElement> dati, String nome, String tipoENomeSoggetto, String fault, TipoOperazione tipoOperazione,  
			long idSA, Boolean contaListe,String[] soggettiList,String[] soggettiListLabel, String provider, 
			String utente,String password, String subject, String principal, String tipoauth,
			String faultactor,String genericfault,String prefixfault, String invrif, String sbustamentoInformazioniProtocolloRisposta,
			String servlet,String id, String nomeProtocollo,
			String ruoloFruitore, String ruoloErogatore,
			String sbustamento, String sbustamentoInformazioniProtocolloRichiesta, String getmsg,
			String invrifRichiesta, String risprif,
			String endpointtype, String autenticazioneHttp, String url, String nomeCodaJMS, String tipo,
			String userRichiesta, String passwordRichiesta, String initcont, String urlpgk,
			String provurl, String connfact, String sendas,  
			String httpsurl, String httpstipologia, boolean httpshostverify,
			String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey,
			String tipoconn,
			String connettoreDebug,
			Boolean isConnettoreCustomUltimaImmagineSalvata,
			String proxyEnabled, String proxyHost, String proxyPort, String proxyUsername, String proxyPassword,
			String opzioniAvanzate, String transfer_mode, String transfer_mode_chunk_size, String redirect_mode, String redirect_max_hop,
			String requestOutputFileName,String requestOutputFileNameHeaders,String requestOutputParentDirCreateIfNotExists,String requestOutputOverwriteIfExists,
			String responseInputMode, String responseInputFileName, String responseInputFileNameHeaders, String responseInputDeleteAfterRead, String responseInputWaitTime,
			String tipoProtocollo, List<String> listaTipiProtocollo, List<ExtendedConnettore> listExtendedConnettore) throws Exception {

		if(ruoloFruitore==null){
			ruoloFruitore = TipologiaFruizione.DISABILITATO.getValue();
		}
		if(ruoloErogatore==null){
			ruoloErogatore = TipologiaErogazione.DISABILITATO.getValue();
		}
		
		boolean configurazioneStandardNonApplicabile = false;
		
		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione
		Integer parentSA = ServletUtils.getIntegerAttributeFromSession(ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT, this.session);
		if(parentSA == null) parentSA = ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE;
		Boolean useIdSogg = parentSA == ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_SOGGETTO;

		IProtocolFactory<?> p = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(tipoProtocollo);
		IProtocolConfiguration config = p.createProtocolConfiguration();
		
		if(TipoOperazione.CHANGE.equals(tipoOperazione)){
			DataElement de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID);
			de.setValue(id);
			de.setType(DataElementType.HIDDEN);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID);
			dati.addElement(de);
		}

		DataElement de = new DataElement();
		de.setLabel(ServiziApplicativiCostanti.LABEL_SERVIZIO_APPLICATIVO);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);


		if(!useIdSogg) {
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROTOCOLLO);
	
			if(listaTipiProtocollo != null && listaTipiProtocollo.size() > 1){
				if(TipoOperazione.CHANGE.equals(tipoOperazione)){
					
					DataElement deLABEL = new DataElement();
					deLABEL.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROTOCOLLO);
					deLABEL.setType(DataElementType.TEXT);
					deLABEL.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROTOCOLLO+"__label");
					deLABEL.setValue(this.getLabelProtocollo(tipoProtocollo));
					dati.addElement(deLABEL);
					
					de.setValue(tipoProtocollo);
					de.setType(DataElementType.HIDDEN);
					de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROTOCOLLO);
				}else {
					de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROTOCOLLO);
					de.setValues(listaTipiProtocollo);
					de.setLabels(this.getLabelsProtocolli(listaTipiProtocollo));
					de.setSelected(tipoProtocollo);
					de.setType(DataElementType.SELECT);
					de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROTOCOLLO);
					de.setPostBack(true);
				}
			} else {
				de.setValue(tipoProtocollo);
				de.setType(DataElementType.HIDDEN);
				de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROTOCOLLO);
			}
			de.setSize(this.getSize());
			dati.addElement(de);
		}
		
				
		de = new DataElement();
		de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_NOME);
		de.setValue(nome);
		de.setSize(this.getSize());
		if (tipoOperazione.equals(TipoOperazione.ADD)) {
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		} else {
			de.setType(DataElementType.TEXT);
		}
		de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME);
		dati.addElement(de);


		
		
		if (!this.isModalitaCompleta()) {
//			
//			de = new DataElement();
//			de.setLabel(ServiziApplicativiCostanti.LABEL_FRUITORE);
//			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RUOLO_FRUITORE);
//			de.setPostBack(true);
//			de.setType(DataElementType.CHECKBOX);
//			if(!TipologiaFruizione.DISABILITATO.equals(ruoloFruitore)){
//				de.setSelected(true);
//			}
//			dati.addElement(de);
//			
//			de = new DataElement();
//			de.setLabel(ServiziApplicativiCostanti.LABEL_EROGATORE);
//			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RUOLO_EROGATORE);
//			de.setPostBack(true);
//			de.setType(DataElementType.CHECKBOX);
//			if(!TipologiaErogazione.DISABILITATO.equals(ruoloErogatore)){
//				de.setSelected(true);
//			}
//			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_TIPOLOGIA);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RUOLO_SA);
			//de.setPostBack(true);
			//de.setType(DataElementType.SELECT);
			//de.setValues(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_RUOLO);
			
			// forzo HIDDEN sempre a meno che non e' in modalita' completa
			de.setType(DataElementType.HIDDEN);
			
			if(!TipologiaFruizione.DISABILITATO.equals(ruoloFruitore) && !TipologiaErogazione.DISABILITATO.equals(ruoloErogatore)){
				de.setLabel(null);
				de.setValue(CostantiControlStation.LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_SHORT_MESSAGE);
				de.setType(DataElementType.TEXT);
				de.setPostBack(false);
				
				configurazioneStandardNonApplicabile = true;
			}
			else if(!TipologiaFruizione.DISABILITATO.equals(ruoloFruitore)){
				de.setSelected(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_RUOLO_FRUITORE);
				if(tipoauth==null || tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA)){
					tipoauth = this.saCore.getAutenticazione_generazioneAutomaticaPorteDelegate();
				} 
			}
			else if(!TipologiaErogazione.DISABILITATO.equals(ruoloErogatore)){
				de.setSelected(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_RUOLO_EROGATORE);
				if( (endpointtype==null || TipiConnettore.DISABILITATO.getNome().equals(endpointtype)) 
						&&
						(getmsg==null || CostantiConfigurazione.DISABILITATO.equals(getmsg))){
					// forzo connettoreAbilitato
					this.pd.setMessage("Abilitare il servizio di IntegrationManager prima di disabilitare il connettore");
					endpointtype = TipiConnettore.HTTP.getNome();
				}
			}
			else{
				// VECCHIO DEFAULT
//				de.setSelected(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_RUOLO_EROGATORE);
//				// forzo default
//				ruoloErogatore = TipologiaErogazione.TRASPARENTE.getValue();
//				// forzo connettoreAbilitato
//				endpointtype = TipiConnettore.HTTP.getNome();

				de.setValue(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_RUOLO_FRUITORE);
				// forzo default
				ruoloFruitore = TipologiaFruizione.NORMALE.getValue();
				if(tipoauth==null || tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA)){
					tipoauth = this.saCore.getAutenticazione_generazioneAutomaticaPorteDelegate();
				} 
				de.setType(DataElementType.HIDDEN);
				
			}
						
			dati.addElement(de);
			
		}// fine !modalitàCompleta
		
		
		
		
		
		ServizioApplicativo sa = null;
		String nomePdd = null;
		// se operazione change visualizzo i link per invocazione servizio,
		// risposta asincrona
		// e ruoli
		if (TipoOperazione.CHANGE.equals(tipoOperazione)) {

			sa = this.saCore.getServizioApplicativo(idSA);
			String tipoSoggetto = null;
			String nomeSoggetto = null;
			if(this.core.isRegistroServiziLocale()){
				Soggetto soggetto = this.soggettiCore.getSoggettoRegistro(sa.getIdSoggetto());
				tipoSoggetto = soggetto.getTipo();
				nomeSoggetto = soggetto.getNome();
				nomePdd = soggetto.getPortaDominio();
			}
			else{
				org.openspcoop2.core.config.Soggetto soggetto = this.soggettiCore.getSoggetto(sa.getIdSoggetto());
				tipoSoggetto = soggetto.getTipo();
				nomeSoggetto = soggetto.getNome();
			}

			// soggetto proprietario
			de = new DataElement();
			de.setType(DataElementType.TEXT);
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			de.setValue(tipoENomeSoggetto);
			dati.addElement(de);
			
			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setValue(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_VISUALIZZA_DATI_PROVIDER);
			de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE,
					new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID, sa.getIdSoggetto()+""),
					new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_TIPO_SOGGETTO, tipoSoggetto),
					new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME_SOGGETTO, nomeSoggetto)
					);
			dati.addElement(de);

			de = new DataElement();
			de.setType(DataElementType.HIDDEN);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);				
			de.setValue(provider);
			dati.addElement(de);
			
		}else{
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);				
			// Aggiunta di un servizio applicativo passando dal menu' 
			if(!useIdSogg){
				de.setType(DataElementType.SELECT);
				de.setPostBack(true);
				
				de.setValues(soggettiList);
				de.setLabels(soggettiListLabel);
				// selezion il provider (se)/che era stato precedentemente
				// selezionato
				// fix 2866
				if ((provider != null) && !provider.equals("")) {
					de.setSelected(provider);
				}
			} else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(provider);
				dati.addElement(de);

				de = new DataElement();
				de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);

				// Aggiunta di un servizio applicativo passando dalla schermata soggetti
				org.openspcoop2.core.config.Soggetto sog = this.soggettiCore.getSoggetto(Integer.parseInt(provider));
				
				de.setType(DataElementType.TEXT);
				de.setValue(this.getLabelNomeSoggetto(tipoProtocollo, sog.getTipo(), sog.getNome()));
				de.setSize(this.getSize());

			}
			dati.addElement(de);

		}
		
		
		
		
		

		
		
		// ************ FRUITORE ********************
		
		if (this.isModalitaCompleta() || 
				!TipologiaFruizione.DISABILITATO.equals(ruoloFruitore) ) {
				
//			if(this.isModalitaStandard()){
//				de = new DataElement();
//				de.setLabel(ServiziApplicativiCostanti.LABEL_FRUITORE);
//				de.setType(DataElementType.TITLE);
//				dati.addElement(de);
//				
//				de = new DataElement();
//				de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO + " '" + nomeProtocollo + "' Risposta");
//				de.setType(DataElementType.SELECT);
//				de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RISPOSTA);
//				de.setValues(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_SBUSTAMENTO_PROTOCOLLO);
//				de.setSelected(sbustamentoInformazioniProtocolloRisposta);
//				dati.addElement(de);
//			}
//			else{
//				de = new DataElement();
//				de.setLabel(ServiziApplicativiCostanti.LABEL_CREDENZIALI_ACCESSO_PORTA);
//				de.setType(DataElementType.TITLE);
//				dati.addElement(de);
//			}

			// Credenziali di accesso
			if (utente == null) {
				utente = "";
			}
			if (password == null) {
				password = "";
			}
			if (subject == null) {
				subject = "";
			}
			if (principal == null) {
				principal = "";
			}
			dati = this.addCredenzialiToDati(dati, tipoauth, utente, password, subject, principal, servlet, true, null, false, true, null, true);
			
			
			if (TipoOperazione.CHANGE.equals(tipoOperazione)) {
			
				de = new DataElement();
				de.setLabel(RuoliCostanti.LABEL_RUOLI);
				de.setType(DataElementType.TITLE);
				dati.addElement(de);
				
				de = new DataElement();
				de.setType(DataElementType.LINK);
				if(useIdSogg){
					de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_RUOLI_LIST,
							new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO,sa.getId()+""),
							new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,sa.getIdSoggetto()+""));
				}
				else{
					de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_RUOLI_LIST,
							new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO,sa.getId()+""));
				}
				if (contaListe) {
					// BugFix OP-674
					//List<String> lista1 = this.saCore.servizioApplicativoRuoliList(sa.getId(),new Search(true));
					Search searchForCount = new Search(true,1);
					this.saCore.servizioApplicativoRuoliList(sa.getId(),searchForCount);
					//int numRuoli = lista1.size();
					int numRuoli = searchForCount.getNumEntries(Liste.SERVIZIO_APPLICATIVO_RUOLI);
					ServletUtils.setDataElementVisualizzaLabel(de,(long)numRuoli);
				} else
					ServletUtils.setDataElementVisualizzaLabel(de);
				dati.addElement(de);
				
			}
			
		}



		boolean avanzatoFruitore = this.isModalitaAvanzata() &&
				!TipologiaFruizione.DISABILITATO.equals(ruoloFruitore);
		
		boolean faultChoice = avanzatoFruitore && config.isSupportoSceltaFault();
		if (faultChoice) {
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_ERRORE_APPLICATIVO);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}

		if (TipoOperazione.ADD.equals(tipoOperazione)) {
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_FAULT);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_FAULT);
			if (faultChoice) {
				de.setType(DataElementType.SELECT);
				de.setValues(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_FAULT);
				de.setSelected(fault);
			}
			else{
				de.setType(DataElementType.HIDDEN);
				de.setValue(fault);
			}
			dati.addElement(de);
		}
		else{
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_FAULT);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_FAULT);
			if (faultChoice) {
				de.setType(DataElementType.SELECT);
				de.setValues(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_FAULT);
				de.setSelected(fault);
				//			de.setOnChange("CambiaFault()");
				de.setPostBack(true);
			}
			else{
				de.setType(DataElementType.HIDDEN);
				de.setValue(fault);
			}
			dati.addElement(de);

			if (fault.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_FAULT_SOAP)) {
				de = new DataElement();
				de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_FAULT_ACTOR);
				de.setValue(faultactor);
				if (faultChoice) {
					de.setType(DataElementType.TEXT_EDIT);
				}
				else{
					de.setType(DataElementType.HIDDEN);
				}
				de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_FAULT_ACTOR);
				de.setSize(this.getSize());
				dati.addElement(de);
			}


			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_FAULT_GENERIC_CODE);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_FAULT_GENERIC_CODE);
			if (faultChoice) {
				de.setType(DataElementType.SELECT);
				de.setValues(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_FAULT_GENERIC_CODE);
				de.setSelected(genericfault);
			}
			else{
				de.setType(DataElementType.HIDDEN);
				de.setValue(genericfault);
			}
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_FAULT_PREFIX);
			de.setValue(prefixfault);
			if (faultChoice) {
				de.setType(DataElementType.TEXT_EDIT);
			}
			else{
				de.setType(DataElementType.HIDDEN);
			}
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_FAULT_PREFIX);
			de.setSize(this.getSize());
			dati.addElement(de);
		}

		
		if( (this.isModalitaCompleta() && !TipoOperazione.ADD.equals(tipoOperazione))  || 
				(avanzatoFruitore && config.isSupportoSbustamentoProtocollo()) ) {
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_TRATTAMENTO_MESSAGGIO);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}
		
		
		de = new DataElement();
		de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RISPOSTA);
		de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO + " '" + nomeProtocollo + "'"); // non uso label perch' troppo grande
		if(avanzatoFruitore && config.isSupportoSbustamentoProtocollo()){
			de.setType(DataElementType.SELECT);
			de.setValues(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_SBUSTAMENTO_PROTOCOLLO);
			de.setSelected(sbustamentoInformazioniProtocolloRisposta);
		}
		else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(sbustamentoInformazioniProtocolloRisposta);
		}
		dati.addElement(de);
			
					

		if (this.isModalitaCompleta()==false) {
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO);
			de.setType(DataElementType.HIDDEN);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_RISPOSTA);
			de.setValue(invrif == null || "".equals(invrif) ? CostantiConfigurazione.DISABILITATO.toString() : invrif);
			dati.addElement(de);
		} else {
			if (!TipoOperazione.ADD.equals(tipoOperazione)) {
				de = new DataElement();
				de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO);
				de.setType(DataElementType.SELECT);
				de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_RISPOSTA);
				de.setValues(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO);
				de.setSelected(invrif);
				dati.addElement(de);
			}else{
				de = new DataElement();
				de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO);
				de.setType(DataElementType.HIDDEN);
				de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_RISPOSTA);
				de.setValue(invrif == null || "".equals(invrif) ? ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_DISABILITATO : invrif);
				dati.addElement(de);
			}
		}



		// se operazione change visualizzo i link per invocazione servizio,
		// risposta asincrona
		// e ruoli
		if (TipoOperazione.CHANGE.equals(tipoOperazione) && this.isModalitaCompleta() && !this.pddCore.isPddEsterna(nomePdd)) {

			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_INFO_INTEGRAZIONE);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
			
			// invocazione servizio
			InvocazioneServizio invServ = sa.getInvocazioneServizio();
			Connettore connettoreInv = invServ != null ? invServ.getConnettore() : null;
			StatoFunzionalita getMSGInv = invServ != null ? invServ.getGetMessage() : null;

			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_INVOCAZIONE_SERVIZIO);
			de.setType(DataElementType.LINK);
			if(this.pddCore.isPddEsterna(nomePdd)){
				de.setType(DataElementType.TEXT);
				de.setValue("(non presente)");
			} else {
				if ((connettoreInv == null || TipiConnettore.DISABILITATO.getNome().equals(connettoreInv.getTipo())) && CostantiConfigurazione.DISABILITATO.equals(getMSGInv)) {
					de.setValue(ServiziApplicativiCostanti.LABEL_INVOCAZIONE_SERVIZIO+" (disabilitato)");
				} else {
					de.setValue(ServiziApplicativiCostanti.LABEL_INVOCAZIONE_SERVIZIO+" (visualizza)");
				}
				de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT,
						new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,sa.getIdSoggetto()+""),
						new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME_SERVIZIO_APPLICATIVO,sa.getNome()),
						new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO,sa.getId()+"")
						);
			}
			dati.addElement(de);

			// risposta asincrona
			RispostaAsincrona rispAsin = sa.getRispostaAsincrona();
			Connettore connettoreRis = rispAsin != null ? rispAsin.getConnettore() : null;
			StatoFunzionalita getMSGRisp = rispAsin != null ? rispAsin.getGetMessage() : null;

			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setLabel(ServiziApplicativiCostanti.LABEL_RISPOSTA_ASINCRONA);
			if(this.pddCore.isPddEsterna(nomePdd)){
				de.setType(DataElementType.TEXT);
				de.setValue("(non presente)");
			}
			else{
				if ((connettoreRis == null || TipiConnettore.DISABILITATO.getNome().equals(connettoreRis.getTipo())) && CostantiConfigurazione.DISABILITATO.equals(getMSGRisp)) {
					// de.setValue(CostantiConfigurazione.DISABILITATO);
					de.setValue(ServiziApplicativiCostanti.LABEL_RISPOSTA_ASINCRONA+" (disabilitato)");
				} else {
					// de.setValue("visualizza");
					de.setValue(ServiziApplicativiCostanti.LABEL_RISPOSTA_ASINCRONA+" (visualizza)");
				}
				de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA,
						new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,sa.getIdSoggetto()+""),
						new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME_SERVIZIO_APPLICATIVO,sa.getNome()),
						new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO,sa.getId()+""));
			}

			dati.addElement(de);

		}

		
		
		
		
		
		
		
		
		
		
		
		// ************ EROGATORE ********************
		
		if(this.isModalitaStandard()){
			
			if(!TipologiaErogazione.DISABILITATO.equals(ruoloErogatore)){
						
				this.addEndPointToDati(dati,id,nome,sbustamento,sbustamentoInformazioniProtocolloRichiesta,
						getmsg,invrif,risprif,nomeProtocollo,false,true, true,
						parentSA,null);
				
				if(TipologiaFruizione.DISABILITATO.equals(ruoloFruitore) &&
						CostantiConfigurazione.ABILITATO.equals(getmsg)){
					
					if(tipoauth==null || tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA)){
						tipoauth = this.saCore.getAutenticazione_generazioneAutomaticaPorteDelegate();
					} 
						
					// Credenziali di accesso
					if (utente == null) {
						utente = "";
					}
					if (password == null) {
						password = "";
					}
					if (subject == null) {
						subject = "";
					}
					if (principal == null) {
						principal = "";
					}
					dati = this.addCredenzialiToDati(dati, tipoauth, utente, password, subject, principal, servlet, true, null, false, true, null, true);
					
				}
				
				dati = this.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, "",//ServiziApplicativiCostanti.LABEL_EROGATORE+" ",
						url, nomeCodaJMS,
						tipo, userRichiesta, passwordRichiesta, initcont, urlpgk, provurl,
						connfact, sendas, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, TipoOperazione.CHANGE, httpsurl, httpstipologia,
						httpshostverify, httpspath, httpstipo, httpspwd,
						httpsalgoritmo, httpsstato, httpskeystore,
						httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey, httpspwdprivatekey,
						httpsalgoritmokey, tipoconn, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT,
						nome, id, null, null, null, null,
						null, null, true,
						isConnettoreCustomUltimaImmagineSalvata, 
						proxyEnabled, proxyHost, proxyPort, proxyUsername, proxyPassword,
						opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
						requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
						listExtendedConnettore, false);
			}
		}
		
		
		
		
		
		
		if(configurazioneStandardNonApplicabile){
			this.pd.setMessage(CostantiControlStation.LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_LONG_MESSAGE,Costanti.MESSAGE_TYPE_INFO);
			this.pd.disableEditMode();
		}
		
		
		return dati;
	}


	public boolean servizioApplicativoCheckData(TipoOperazione tipoOperazione, String[] soggettiList, long idProvOld,
			String ruoloFruitore, String ruoloErogatore, List<ExtendedConnettore> listExtendedConnettore,
			ServizioApplicativo saOld)
			throws Exception {
		try {
			
			if(ruoloFruitore==null){
				ruoloFruitore = TipologiaFruizione.DISABILITATO.getValue();
			}
			if(ruoloErogatore==null){
				ruoloErogatore = TipologiaErogazione.DISABILITATO.getValue();
			}
			
			String nome = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME);
			String provider = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			int newProv = 0;
			if (provider == null) {
				provider = "";
			} else {
				newProv = Integer.parseInt(provider);
			}
			String fault = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_FAULT);
//			String tipoauth = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SA);
//			if (tipoauth == null) {
//				tipoauth = ServiziApplicativiCostanti.DEFAULT_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE;
//			}
//			String utente = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_USERNAME_SA);
//			String password = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_PASSWORD_SA);
//			// String confpw = this.getParameter("confpw");
//			String subject = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_SUBJECT_SA);

			// Campi obbligatori
			if (nome.equals("") || (tipoOperazione.equals(TipoOperazione.ADD) && provider.equals(""))) {
				String tmpElenco = "";
				if (nome.equals("")) {
					tmpElenco = ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_NOME;
				}
				if (tipoOperazione.equals(TipoOperazione.ADD) && provider.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER;
					} else {
						tmpElenco = tmpElenco + ", "+ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER;
					}
				}
				this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
				return false;
			}
//			if (tipoauth.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_BASIC) && (utente.equals("") || password.equals("") /*
//			 * ||
//			 * confpw
//			 * .
//			 * equals
//			 * (
//			 * ""
//			 * )
//			 */)) {
//				String tmpElenco = "";
//				if (utente.equals("")) {
//					tmpElenco = ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_USERNAME;
//				}
//				if (password.equals("")) {
//					if (tmpElenco.equals("")) {
//						tmpElenco = ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_PASSWORD;
//					} else {
//						tmpElenco = tmpElenco + ", "+ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_PASSWORD;
//					}
//				}
//				/*
//				 * if (confpw.equals("")) { if (tmpElenco.equals("")) {
//				 * tmpElenco = "Conferma password"; } else { tmpElenco =
//				 * tmpElenco + ", Conferma password"; } }
//				 */
//				this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
//				return false;
//			}
//			if (tipoauth.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SSL)){
//				if (subject.equals("")) {
//					this.pd.setMessage("Dati incompleti. E' necessario indicare il "+
//							ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_SUBJECT);
//					return false;
//				}else{
//					try{
//						org.openspcoop2.utils.Utilities.validaSubject(subject);
//					}catch(Exception e){
//						this.pd.setMessage("Le credenziali di tipo ssl  possiedono un subject non valido: "+e.getMessage());
//						return false;
//					}
//				}
//			}

			// Controllo che non ci siano spazi nei campi di testo
			if (nome.indexOf(" ") != -1 || nome.indexOf('\"') != -1) {
				this.pd.setMessage("Non inserire spazi o doppi apici nei campi di testo");
				return false;
			}
			if(this.checkIntegrationEntityName(nome, ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_NOME)==false){
				return false;
			}
			
			
			
//			if (tipoauth.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_BASIC) && ((utente.indexOf(" ") != -1) || (password.indexOf(" ") != -1))) {
//				this.pd.setMessage("Non inserire spazi nei campi di testo");
//				return false;
//			}
			/*if (tipoauth.equals("ssl") && (subject.indexOf(" ") != -1)) {
						this.pd.setMessage("Non inserire spazi nei campi di testo");
						return false;
					}*/

			// Controllo che i campi DataElementType.SELECT abbiano uno dei valori ammessi
			if (!fault.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_FAULT_SOAP) && !fault.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_FAULT_XML)) {
				this.pd.setMessage("Modalit&agrave; di fault dev'essere "+
						ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_FAULT_SOAP+" o "+ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_FAULT_XML);
				return false;
			}
//			if (!tipoauth.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_BASIC) && 
//					!tipoauth.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SSL) && 
//					!tipoauth.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_NESSUNA)) {
//				this.pd.setMessage("Tipo dev'essere "+
//						ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_BASIC+", "+
//						ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SSL+" o "+
//						ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_NESSUNA);
//				return false;
//			}

			// Controllo che le password corrispondano
			/*
			 * if (tipoauth.equals("basic") && !password.equals(confpw)) {
			 * this.pd.setMessage("Le password non corrispondono"); return
			 * false; }
			 */

			// Controllo che il provider appartenga alla lista di
			// providers disponibili
			if (tipoOperazione.equals(TipoOperazione.ADD)) {
				boolean trovatoProv = false;
				for (int i = 0; i < soggettiList.length; i++) {
					String tmpSogg = soggettiList[i];
					if (tmpSogg.equals(provider)) {
						trovatoProv = true;
					}
				}
				if (!trovatoProv) {
					this.pd.setMessage("Il soggetto dev'essere scelto tra quelli definiti nel pannello Soggetti");
					return false;
				}
			}

			IDSoggetto ids = null;
			if(this.core.isRegistroServiziLocale()){
				Soggetto mySogg = this.soggettiCore.getSoggettoRegistro(newProv);
				ids = new IDSoggetto(mySogg.getTipo(), mySogg.getNome());
			}else{
				org.openspcoop2.core.config.Soggetto mySogg = this.soggettiCore.getSoggetto(newProv);
				ids = new IDSoggetto(mySogg.getTipo(), mySogg.getNome());
			}
			IDServizioApplicativo idSA = new IDServizioApplicativo();
			idSA.setIdSoggettoProprietario(ids);
			idSA.setNome(nome);
			
			// Se tipoOp = add, controllo che il servizioApplicativo non sia
			// gia'
			// stato registrato
			if (tipoOperazione.equals(TipoOperazione.ADD)) {
				boolean giaRegistrato = this.saCore.existsServizioApplicativo(idSA);
				if (giaRegistrato) {
					this.pd.setMessage("Il Servizio Applicativo " + nome + " &egrave; gi&agrave; stato registrato per il soggetto scelto.");
					return false;
				}
			}

			if (tipoOperazione.equals(TipoOperazione.CHANGE)) {
				
				String oldTipoAuth = null;
				TipologiaFruizione tipologiaFruizione = null;
				if(saOld!=null) {
					tipologiaFruizione = TipologiaFruizione.toEnumConstant(saOld.getTipologiaFruizione());
				}
				if(tipologiaFruizione!=null && !TipologiaFruizione.DISABILITATO.equals(tipologiaFruizione) &&
						saOld.getInvocazionePorta()!=null && saOld.getInvocazionePorta().sizeCredenzialiList()>0) {
					// prendo il primo
					CredenzialeTipo tipo = saOld.getInvocazionePorta().getCredenziali(0).getTipo();
					if(tipo!=null) {
						oldTipoAuth = tipo.getValue();
					}
					else {
						oldTipoAuth = ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA;
					}
				}
				
				String tipoauth = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE);
				
				if(oldTipoAuth!=null && !oldTipoAuth.equals(tipoauth)) {
					// controllo che non sia usato in qualche PD
					
					FiltroRicercaPorteDelegate filtro = new FiltroRicercaPorteDelegate();
					filtro.setTipoSoggetto(idSA.getIdSoggettoProprietario().getTipo());
					filtro.setNomeSoggetto(idSA.getIdSoggettoProprietario().getNome());
					filtro.setNomeServizioApplicativo(idSA.getNome());
					List<IDPortaDelegata> list = this.porteDelegateCore.getAllIdPorteDelegate(filtro);
					if(list!=null && list.size()>0) {
						this.pd.setMessage("Non &egrave; possibile modificare il tipo di credenziali poich&egrave; l'applicativo viene utilizzato all'interno del controllo degli accessi di "+
								list.size()+" configurazioni di fruizione di servizio");
						return false;
					}
				}
				
			}
			
			// Se tipoOp = change, se sto cambiando provider controllo che
			// il servizioApplicativo non sia associato al vecchio provider
			// Ovvero, che non sia associato ad una delle porte delegate
			// o applicative del vecchio provider
			if (tipoOperazione.equals(TipoOperazione.CHANGE)) {
				String nomeProv = "";
				boolean servizioApplicativoInUso = false;

				if (newProv != idProvOld) {
					// Prendo nome e tipo del provider
					org.openspcoop2.core.config.Soggetto oldSogg = this.soggettiCore.getSoggetto(idProvOld);
					nomeProv = oldSogg.getTipo() + "/" + oldSogg.getNome();

					for (int i = 0; i < oldSogg.sizePortaDelegataList(); i++) {
						PortaDelegata pde = oldSogg.getPortaDelegata(i);
						for (int j = 0; j < pde.sizeServizioApplicativoList(); j++) {
							PortaDelegataServizioApplicativo tmpSA = pde.getServizioApplicativo(j);
							if (nome.equals(tmpSA.getNome())) {
								servizioApplicativoInUso = true;
								break;
							}
						}
						if (servizioApplicativoInUso)
							break;
					}

					if (!servizioApplicativoInUso) {
						for (int i = 0; i < oldSogg.sizePortaApplicativaList(); i++) {
							PortaApplicativa pa = oldSogg.getPortaApplicativa(i);
							for (int j = 0; j < pa.sizeServizioApplicativoList(); j++) {
								PortaApplicativaServizioApplicativo tmpSA = pa.getServizioApplicativo(j);
								if (nome.equals(tmpSA.getNome())) {
									servizioApplicativoInUso = true;
									break;
								}
							}
							if (servizioApplicativoInUso)
								break;
						}
					}
				}

				if (servizioApplicativoInUso) {
					this.pd.setMessage("Il Servizio Applicativo " + nome + " &egrave; gi&agrave; stato associato ad alcune porte delegate e/o applicative del Soggetto " + nomeProv + ". Se si desidera modificare il Soggetto, &egrave; necessario eliminare prima tutte le occorrenze del Servizio Applicativo");
					return false;
				}
			}

			
			if(this.credenzialiCheckData()==false){
				return false;
			}
			
			String tipoauth = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE);
			if (tipoauth == null) {
				tipoauth = ConnettoriCostanti.DEFAULT_AUTENTICAZIONE_TIPO;
			}
			String utente = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME);
			String password = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			// String confpw = this.getParameter("confpw");
			String subject = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT);
			String principal = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL);
			
			// Se sono presenti credenziali, controllo che non siano gia'
			// utilizzate da altri soggetti
			if (tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC)) {
				// recupera lista servizi applicativi con stesse credenziali
				List<ServizioApplicativo> saList = this.saCore.servizioApplicativoWithCredenzialiBasicList(utente, password);

				String portaDominio = null;
				if(this.core.isRegistroServiziLocale()){
					Soggetto soggettoToCheck = tipoOperazione.equals(TipoOperazione.CHANGE) ? 
							this.soggettiCore.getSoggettoRegistro(idProvOld) : this.soggettiCore.getSoggettoRegistro(newProv);
							portaDominio = soggettoToCheck.getPortaDominio();
				}

				for (int i = 0; i < saList.size(); i++) {
					ServizioApplicativo sa = saList.get(i);

					//String tipoNomeSoggetto = null;

					if(this.core.isRegistroServiziLocale()){

						// bugfix #66
						// controllo se soggetto appartiene a nal diversi, in tal
						// caso e' possibile
						// avere stesse credenziali
						// Raccolgo informazioni soggetto
						Soggetto tmpSoggettoProprietarioSa = this.soggettiCore.getSoggettoRegistro(sa.getIdSoggetto());
						//tipoNomeSoggetto = tmpSoggettoProprietarioSa.getTipo() + "/" + tmpSoggettoProprietarioSa.getNome();

						// se appartengono a nal diversi allora va bene continuo
						if (!portaDominio.equals(tmpSoggettoProprietarioSa.getPortaDominio()))
							continue;
					}
					else{

						//org.openspcoop2.core.config.Soggetto tmpSoggettoProprietarioSa = this.soggettiCore.getSoggetto(sa.getIdSoggetto());
						//tipoNomeSoggetto = tmpSoggettoProprietarioSa.getTipo() + "/" + tmpSoggettoProprietarioSa.getNome();

					}

					if ((tipoOperazione.equals(TipoOperazione.CHANGE)) && (nome.equals(sa.getNome())) && (idProvOld == sa.getIdSoggetto())) {
						continue;
					}

					// Messaggio di errore
					this.pd.setMessage("Esistono gia' altri servizi applicativi che possiedono le credenziali basic indicate.");
					return false;
				}
			} else if (tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL)) {
				List<ServizioApplicativo> saList = this.saCore.servizioApplicativoWithCredenzialiSslList(subject);

				String portaDominio = null;
				if(this.core.isRegistroServiziLocale()){
					Soggetto soggettoToCheck = tipoOperazione.equals(TipoOperazione.CHANGE) ? 
							this.soggettiCore.getSoggettoRegistro(idProvOld) : this.soggettiCore.getSoggettoRegistro(newProv);
							portaDominio = soggettoToCheck.getPortaDominio();
				}

				for (int i = 0; i < saList.size(); i++) {
					ServizioApplicativo sa = saList.get(i);

					//String tipoNomeSoggetto = null;

					if(this.core.isRegistroServiziLocale()){

						// bugfix #66
						// controllo se soggetto appartiene a nal diversi, in tal
						// caso e' possibile
						// avere stesse credenziali
						// Raccolgo informazioni soggetto
						Soggetto tmpSoggettoProprietarioSa = this.soggettiCore.getSoggettoRegistro(sa.getIdSoggetto());
						//tipoNomeSoggetto = tmpSoggettoProprietarioSa.getTipo() + "/" + tmpSoggettoProprietarioSa.getNome();

						// se appartengono a nal diversi allora va bene continuo
						if (!portaDominio.equals(tmpSoggettoProprietarioSa.getPortaDominio()))
							continue;

					}else{

						//org.openspcoop2.core.config.Soggetto tmpSoggettoProprietarioSa = this.soggettiCore.getSoggetto(sa.getIdSoggetto());
						//tipoNomeSoggetto = tmpSoggettoProprietarioSa.getTipo() + "/" + tmpSoggettoProprietarioSa.getNome();

					}

					if ((tipoOperazione.equals(TipoOperazione.CHANGE)) && (nome.equals(sa.getNome())) && (idProvOld == sa.getIdSoggetto())) {
						continue;
					}

					// Raccolgo informazioni soggetto
					// Messaggio di errore
					this.pd.setMessage("Esistono gia' altri servizi applicativi che possiedono le credenziali ssl indicate.");
					return false;
				}
			}
			else if (tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL)) {
				// recupera lista servizi applicativi con stesse credenziali
				List<ServizioApplicativo> saList = this.saCore.servizioApplicativoWithCredenzialiPrincipalList(principal);

				String portaDominio = null;
				if(this.core.isRegistroServiziLocale()){
					Soggetto soggettoToCheck = tipoOperazione.equals(TipoOperazione.CHANGE) ? 
							this.soggettiCore.getSoggettoRegistro(idProvOld) : this.soggettiCore.getSoggettoRegistro(newProv);
							portaDominio = soggettoToCheck.getPortaDominio();
				}

				for (int i = 0; i < saList.size(); i++) {
					ServizioApplicativo sa = saList.get(i);

					//String tipoNomeSoggetto = null;

					if(this.core.isRegistroServiziLocale()){

						// bugfix #66
						// controllo se soggetto appartiene a nal diversi, in tal
						// caso e' possibile
						// avere stesse credenziali
						// Raccolgo informazioni soggetto
						Soggetto tmpSoggettoProprietarioSa = this.soggettiCore.getSoggettoRegistro(sa.getIdSoggetto());
						//tipoNomeSoggetto = tmpSoggettoProprietarioSa.getTipo() + "/" + tmpSoggettoProprietarioSa.getNome();

						// se appartengono a nal diversi allora va bene continuo
						if (!portaDominio.equals(tmpSoggettoProprietarioSa.getPortaDominio()))
							continue;
					}
					else{

						//org.openspcoop2.core.config.Soggetto tmpSoggettoProprietarioSa = this.soggettiCore.getSoggetto(sa.getIdSoggetto());
						//tipoNomeSoggetto = tmpSoggettoProprietarioSa.getTipo() + "/" + tmpSoggettoProprietarioSa.getNome();

					}

					if ((tipoOperazione.equals(TipoOperazione.CHANGE)) && (nome.equals(sa.getNome())) && (idProvOld == sa.getIdSoggetto())) {
						continue;
					}

					// Messaggio di errore
					this.pd.setMessage("Esistono gia' altri servizi applicativi che possiedono il 'principal' indicato.");
					return false;
				}
			} 

			// erogatore
			if(this.isModalitaStandard()){
				if(!TipologiaErogazione.DISABILITATO.equals(ruoloErogatore)){
					boolean isOk = this.servizioApplicativoEndPointCheckData(listExtendedConnettore);
					if (!isOk) {
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

	private void addFilterRuoloServizioApplicativo(String ruoloSA, boolean postBack) throws Exception{
		try {
			String [] tmp = ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_RUOLO;
			
			String [] values = new String[tmp.length + 1];
			String [] labels = new String[tmp.length + 1];
			labels[0] = ServiziApplicativiCostanti.LABEL_PARAMETRO_FILTRO_RUOLO_QUALSIASI;
			values[0] = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_QUALSIASI;
			for (int i =0; i < tmp.length ; i ++) {
				labels[i+1] = tmp[i];
				values[i+1] = tmp[i];
			}
			
			String selectedValue = ruoloSA != null ? ruoloSA : CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_QUALSIASI;
			
			String label = ServiziApplicativiCostanti.LABEL_TIPOLOGIA;

			this.pd.addFilter(Filtri.FILTRO_RUOLO_SERVIZIO_APPLICATIVO, label, selectedValue, values, labels, postBack, this.getSize());
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}



	public void prepareServizioApplicativoList(ISearch ricerca, List<ServizioApplicativo> lista, boolean useIdSoggetto)
			throws Exception {
		try {
			String idProvider = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione
			Integer parentSA = ServletUtils.getIntegerAttributeFromSession(ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT, this.session);
			if(parentSA == null) parentSA = ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE;
			Boolean useIdSogg = parentSA == ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_SOGGETTO;

			if(useIdSogg){
				Parameter pProvider = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, idProvider); 
				ServletUtils.addListElementIntoSession(this.session, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI,pProvider );
			}else 
				ServletUtils.addListElementIntoSession(this.session, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI);

			@SuppressWarnings("unused")
			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

			@SuppressWarnings("unused")
			Boolean singlePdD = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_SINGLE_PDD);


			// Prendo il soggetto
			String tmpTitle = null;
			String protocolloSoggetto = null;
			boolean supportAsincroni = true;
			if(useIdSogg){
				if(this.core.isRegistroServiziLocale()){
					Soggetto tmpSogg = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(idProvider));
					protocolloSoggetto = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(tmpSogg.getTipo());
					tmpTitle = this.getLabelNomeSoggetto(protocolloSoggetto, tmpSogg.getTipo() , tmpSogg.getNome());
				}else{
					org.openspcoop2.core.config.Soggetto tmpSogg = this.soggettiCore.getSoggetto(Integer.parseInt(idProvider));
					protocolloSoggetto = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(tmpSogg.getTipo());
					tmpTitle = this.getLabelNomeSoggetto(protocolloSoggetto, tmpSogg.getTipo() , tmpSogg.getNome());
				}
				
				List<ServiceBinding> serviceBindingListProtocollo = this.core.getServiceBindingListProtocollo(protocolloSoggetto);
				for (ServiceBinding serviceBinding : serviceBindingListProtocollo) {
					supportAsincroni = this.core.isProfiloDiCollaborazioneSupportatoDalProtocollo(protocolloSoggetto,serviceBinding, ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO)
							|| this.core.isProfiloDiCollaborazioneSupportatoDalProtocollo(protocolloSoggetto, serviceBinding, ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO);
				}
				
				if(supportAsincroni==false){
					if (this.isModalitaAvanzata()){
						supportAsincroni = this.core.isElenchiSA_asincroniNonSupportati_VisualizzaRispostaAsincrona();
					}
				}
			}

			int idLista = useIdSogg ? Liste.SERVIZI_APPLICATIVI_BY_SOGGETTO : Liste.SERVIZIO_APPLICATIVO;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			if(!useIdSogg) {
				addFilterProtocol(ricerca, idLista);
			}
			
			if(this.isModalitaCompleta()) {
				String filterRuoloSA = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_RUOLO_SERVIZIO_APPLICATIVO);
				this.addFilterRuoloServizioApplicativo(filterRuoloSA,false);
			}
			
			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));


			// setto la barra del titolo
			
			String labelApplicativi = ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI;
			String labelApplicativiDi = ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_DI;
			if(this.isModalitaCompleta()==false) {
				labelApplicativi = ServiziApplicativiCostanti.LABEL_APPLICATIVI;
				labelApplicativiDi = ServiziApplicativiCostanti.LABEL_PARAMETRO_APPLICATIVI_DI;
			}
			
			if(!useIdSogg){
				this.pd.setSearchLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_NOME);
				if (search.equals("")) {
					this.pd.setSearchDescription("");
					ServletUtils.setPageDataTitle(this.pd, 
							new Parameter(labelApplicativi,null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO,null));
				}
				else{
					ServletUtils.setPageDataTitle(this.pd, 
							new Parameter(labelApplicativi,null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA,null));	
				}
			} else {
				List<Parameter> lstParam = new ArrayList<Parameter>();

				lstParam.add(new Parameter(SoggettiCostanti.LABEL_SOGGETTI, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));

				this.pd.setSearchLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_NOME);
				if(search.equals("")){
					this.pd.setSearchDescription("");
					lstParam.add(new Parameter(labelApplicativiDi + tmpTitle,null));
				}else{
					lstParam.add(new Parameter(labelApplicativiDi + tmpTitle,
							ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST ,
							new Parameter( ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, idProvider)));
					lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE_RISULTATI_RICERCA, null));
				}

				ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));
			}

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, labelApplicativi, search);
			}

			boolean showProtocolli = this.core.countProtocolli(this.session)>1;
			
			// setto le label delle colonne
			List<String> labels = new ArrayList<String>();
			labels.add(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_NOME);
			if(!useIdSogg) {
				labels.add(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			}
			if( showProtocolli ) {
				labels.add(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROTOCOLLO);
			}
			if(!this.isModalitaCompleta()) {
				labels.add(RuoliCostanti.LABEL_RUOLI);
			}
			if(this.isModalitaCompleta()) {
				labels.add(ServiziApplicativiCostanti.LABEL_TIPOLOGIA);
			}
			if (this.isModalitaCompleta()){
				labels.add(ServiziApplicativiCostanti.LABEL_INVOCAZIONE_SERVIZIO);
			}
			if(supportAsincroni && this.isModalitaCompleta()){
				labels.add(ServiziApplicativiCostanti.LABEL_RISPOSTA_ASINCRONA);
			}
			this.pd.setLabels(labels.toArray(new String[1]));

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<ServizioApplicativo> it = lista.iterator();
				while (it.hasNext()) {
					ServizioApplicativo sa = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setType(DataElementType.HIDDEN);
					de.setValue("" + sa.getId());
					e.addElement(de);

					String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(sa.getTipoSoggettoProprietario());
					
					de = new DataElement();
					de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE, 
							new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID, sa.getId()+""),
							new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, sa.getIdSoggetto()+""));
					de.setValue(sa.getNome());
					de.setIdToRemove(sa.getId().toString());
					e.addElement(de);

					if(!useIdSogg) {
						de = new DataElement();
						de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE, 
								new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID, sa.getIdSoggetto()+""),
								new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME, sa.getNomeSoggettoProprietario()),
								new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO, sa.getTipoSoggettoProprietario()));
						de.setValue(this.getLabelNomeSoggetto(protocollo, sa.getTipoSoggettoProprietario() , sa.getNomeSoggettoProprietario()));
						e.addElement(de);
					}

					if( showProtocolli ) {
						de = new DataElement();
						de.setValue(this.getLabelProtocollo(protocollo));
						e.addElement(de);
					}
					
					boolean pddEsterna = false;
					if(this.core.isRegistroServiziLocale()){
						IDSoggetto tmpIDS = new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario());
						Soggetto tmpSogg = this.soggettiCore.getSoggettoRegistro(tmpIDS);
						String nomePdd = tmpSogg.getPortaDominio();
						if(this.pddCore.isPddEsterna(nomePdd)){
							pddEsterna = true;
						}
					}

					if(!this.isModalitaCompleta()) {
						de = new DataElement();
						if(useIdSogg){
							de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_RUOLI_LIST,
									new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO,sa.getId()+""),
									new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,sa.getIdSoggetto()+""));
						}
						else{
							de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_RUOLI_LIST,
									new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO,sa.getId()+""));
						}
						if (contaListe) {
							// BugFix OP-674
							//List<String> lista1 = this.saCore.servizioApplicativoRuoliList(sa.getId(),new Search(true));
							Search searchForCount = new Search(true,1);
							this.saCore.servizioApplicativoRuoliList(sa.getId(),searchForCount);
							//int numRuoli = lista1.size();
							int numRuoli = searchForCount.getNumEntries(Liste.SERVIZIO_APPLICATIVO_RUOLI);
							ServletUtils.setDataElementVisualizzaLabel(de,(long)numRuoli);
						} else
							ServletUtils.setDataElementVisualizzaLabel(de);
						e.addElement(de);
					}
					
					if (this.isModalitaCompleta()){
						de = new DataElement();
						de.setValue(this.getTipologia(sa));
						e.addElement(de);
					}
					
					if (this.isModalitaCompleta()){
						de = new DataElement();
						// se la pdd e' esterna non e' possibile modificare il
						// connettore invocazione servizio
						if (pddEsterna) {
							de.setValue("-");// non visualizzo nulla e il link e'
							// disabilitato
						} else {
							de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT, 
									new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME_SERVIZIO_APPLICATIVO, sa.getNome()),
									new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO, sa.getId()+""),
									new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, sa.getIdSoggetto()+""));
							InvocazioneServizio is = sa.getInvocazioneServizio();
							if (is == null) {
								de.setValue(CostantiConfigurazione.ABILITATO.toString());
							} else {
								org.openspcoop2.core.config.Connettore connettore = is.getConnettore();
								boolean connettoreDisabilitato = ((CostantiConfigurazione.DISABILITATO.equals(connettore.getTipo())) || ("".equals(connettore.getTipo())) || (connettore.getTipo() == null));
								boolean messageBoxDisabilitato = ((CostantiConfigurazione.DISABILITATO.equals(is.getGetMessage())) || ("".equals(is.getGetMessage())) || (is.getGetMessage() == null));
								if ( connettoreDisabilitato && messageBoxDisabilitato) {
									de.setValue(CostantiConfigurazione.DISABILITATO.toString());
								} else {
									if(connettoreDisabilitato){
										de.setValue(ServiziApplicativiCostanti.LABEL_CONNETTORE_ABILITATO_SOLO_IM);
									}
									else{
										de.setValue(CostantiConfigurazione.ABILITATO.toString());
									}
								}
							}
						}
						e.addElement(de);
					}

					if(supportAsincroni && this.isModalitaCompleta()){
						de = new DataElement();
						// se la pdd e' esterna non e' possibile modificare il
						// connettore risposta asincrona
						
						boolean supportoAsincronoPuntualeSoggetto = true;
						if(useIdSogg==false){
							String protocolloPuntuale = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(sa.getTipoSoggettoProprietario()); 
							List<ServiceBinding> serviceBindingListProtocollo = this.core.getServiceBindingListProtocollo(protocolloPuntuale);
							for (ServiceBinding serviceBinding : serviceBindingListProtocollo) {
								supportoAsincronoPuntualeSoggetto = this.core.isProfiloDiCollaborazioneSupportatoDalProtocollo(protocolloPuntuale,serviceBinding, ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO)
										|| this.core.isProfiloDiCollaborazioneSupportatoDalProtocollo(protocolloPuntuale, serviceBinding, ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO);
							}

							if(supportoAsincronoPuntualeSoggetto==false){
								if (this.isModalitaAvanzata()){
									supportoAsincronoPuntualeSoggetto = this.core.isElenchiSA_asincroniNonSupportati_VisualizzaRispostaAsincrona();
								}
							}
						}
						
						if (pddEsterna || !supportoAsincronoPuntualeSoggetto) {
							de.setValue("-");// non visualizzo nulla e il link e'
							// disabilitato
						} else {
							de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA, 
									new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME_SERVIZIO_APPLICATIVO, sa.getNome()),
									new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO, sa.getId()+""),
									new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, sa.getIdSoggetto()+""));
							RispostaAsincrona ra = sa.getRispostaAsincrona();
							if (ra == null) {
								de.setValue(CostantiConfigurazione.DISABILITATO.toString());
							} else {
								org.openspcoop2.core.config.Connettore connettore = ra.getConnettore();
								boolean connettoreDisabilitato = ((CostantiConfigurazione.DISABILITATO.equals(connettore.getTipo())) || ("".equals(connettore.getTipo())) || (connettore.getTipo() == null));
								boolean messageBoxDisabilitato = ((CostantiConfigurazione.DISABILITATO.equals(ra.getGetMessage())) || ("".equals(ra.getGetMessage())) || (ra.getGetMessage() == null));
								if ( connettoreDisabilitato && messageBoxDisabilitato) {
									de.setValue(CostantiConfigurazione.DISABILITATO.toString());
								} else {
									if(connettoreDisabilitato){
										de.setValue(ServiziApplicativiCostanti.LABEL_CONNETTORE_ABILITATO_SOLO_IM);
									}
									else{
										de.setValue(CostantiConfigurazione.ABILITATO.toString());
									}
								}
							}
						}
						e.addElement(de);
					}

					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	
	private String getTipologia(ServizioApplicativo sa){
		
		String ruoloFruitore = sa.getTipologiaFruizione();
		String ruoloErogatore = sa.getTipologiaErogazione();
		TipologiaFruizione tipologiaFruizione = TipologiaFruizione.toEnumConstant(ruoloFruitore);
		TipologiaErogazione tipologiaErogazione = TipologiaErogazione.toEnumConstant(ruoloErogatore);
		
		
		if(tipologiaFruizione==null){
			
			// cerco di comprenderlo dalla configurazione del sa
			if(sa.getInvocazionePorta()!=null && sa.getInvocazionePorta().sizeCredenzialiList()>0){
				tipologiaFruizione = TipologiaFruizione.NORMALE;
			}
			else{
				tipologiaFruizione = TipologiaFruizione.DISABILITATO;
			}
			
		}
		
		if(tipologiaErogazione==null){
			
			// cerco di comprenderlo dalla configurazione del sa
			
			if(sa.getInvocazioneServizio()!=null){
				if(StatoFunzionalita.ABILITATO.equals(sa.getInvocazioneServizio().getGetMessage())){
					tipologiaErogazione = TipologiaErogazione.MESSAGE_BOX;
				}
				else if(sa.getInvocazioneServizio().getConnettore()!=null && 
						!TipiConnettore.DISABILITATO.getNome().equals(sa.getInvocazioneServizio().getConnettore().getTipo())){
					tipologiaErogazione = TipologiaErogazione.TRASPARENTE;
				}
				else{
					tipologiaErogazione = TipologiaErogazione.DISABILITATO;
				}
			}
			else{
				tipologiaErogazione = TipologiaErogazione.DISABILITATO;
			}
		}
		
		
		if(!TipologiaFruizione.DISABILITATO.equals(tipologiaFruizione) && 
				!TipologiaErogazione.DISABILITATO.equals(tipologiaErogazione) ){
			return ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_RUOLO_FRUITORE + "/" + ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_RUOLO_EROGATORE;
		}
		else if(!TipologiaFruizione.DISABILITATO.equals(tipologiaFruizione)){
			return ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_RUOLO_FRUITORE;
		}
		else if(!TipologiaErogazione.DISABILITATO.equals(tipologiaErogazione)){
			return ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_RUOLO_EROGATORE;
		}
		else{
			return ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_RUOLO_NON_CONFIGURATO;
		}
		
	}
	

	public void addEndPointToDati(Vector<DataElement> dati,
			String idsil,String nomeservizioApplicativo,String sbustamento,String sbustamentoInformazioniProtocolloRichiesta,
			String getmsg,String invrif,String risprif, String nomeProtocollo, boolean showName,
			boolean isInvocazioneServizio, boolean showTitleTrattamentoMessaggio,
			Integer parentSA, ServiceBinding serviceBinding) throws Exception{

		IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(nomeProtocollo);
		IProtocolConfiguration config = protocolFactory.createProtocolConfiguration();
		
		DataElement de = new DataElement();
		de.setLabel(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO);
		de.setValue(idsil);
		de.setType(DataElementType.HIDDEN);
		de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO);
		dati.addElement(de);

		boolean showFromConfigurazione = false;
		if(parentSA!=null && (parentSA.intValue() == ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_CONFIGURAZIONE)) {
			showFromConfigurazione = true;
		}
		
		if(showName && !showFromConfigurazione){
			
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_SERVIZIO_APPLICATIVO);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_NOME);
			de.setValue(nomeservizioApplicativo);
			de.setType(DataElementType.TEXT);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME_SERVIZIO_APPLICATIVO);
			de.setSize(this.getSize());
			dati.addElement(de);
		}
		
		//controllo aggiunta sezione trattamento messaggio appare se c'e' almeno un elemento sui 4 previsti che puo' essere visualizzato.
		showTitleTrattamentoMessaggio = showTitleTrattamentoMessaggio && (
				(!this.isModalitaStandard() && (serviceBinding == null || serviceBinding.equals(ServiceBinding.SOAP))) ||
				(!this.isModalitaStandard() && config.isSupportoSbustamentoProtocollo()) || 
				!this.isModalitaStandard()				
				);

		if(showTitleTrattamentoMessaggio){
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_TRATTAMENTO_MESSAGGIO);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}
		
		String[] tipoSbustamentoSOAP = { CostantiConfigurazione.ABILITATO.toString(), CostantiConfigurazione.DISABILITATO.toString() };
		de = new DataElement();
		de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_SOAP);
		de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_SOAP);
		if(!this.isModalitaStandard() && (serviceBinding == null || serviceBinding.equals(ServiceBinding.SOAP))) {
			de.setType(DataElementType.SELECT);
			de.setValues(tipoSbustamentoSOAP);
			if(sbustamento==null){
				de.setSelected(CostantiConfigurazione.DISABILITATO.toString());
			}else{
				de.setSelected(sbustamento);
			}
		} else {
			de.setType(DataElementType.HIDDEN);
			if(sbustamento==null){
				de.setValue(CostantiConfigurazione.DISABILITATO.toString());
			}else{
				de.setValue(sbustamento);
			}
		}
		
		dati.addElement(de);

		String[] tipoSbustamentoInformazioniProtocollo = { CostantiConfigurazione.ABILITATO.toString(), CostantiConfigurazione.DISABILITATO.toString() };
		de = new DataElement();
		if(nomeProtocollo!=null && !"".equals(nomeProtocollo)){
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO  + " '" + nomeProtocollo + "'");
		}else{
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_INFO_PROTOCOLLO);
		}
		de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RICHIESTA);
		if(!this.isModalitaStandard() &&  config.isSupportoSbustamentoProtocollo()) {
			de.setType(DataElementType.SELECT);
			de.setValues(tipoSbustamentoInformazioniProtocollo);
			if(sbustamentoInformazioniProtocolloRichiesta==null){
				de.setSelected(CostantiConfigurazione.ABILITATO.toString());
			}else{
				de.setSelected(sbustamentoInformazioniProtocolloRichiesta);
			}
		}
		else {
			de.setType(DataElementType.HIDDEN);
			if(sbustamentoInformazioniProtocolloRichiesta==null){
				de.setValue(CostantiConfigurazione.ABILITATO.toString());
			}else{
				de.setValue(sbustamentoInformazioniProtocolloRichiesta);
			}
		}
		dati.addElement(de);

		if (this.isModalitaStandard()) {
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO);
			de.setType(DataElementType.HIDDEN);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_RICHIESTA);
			de.setValue(invrif);
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_RISPOSTA_PER_RIFERIMENTO);
			de.setType(DataElementType.HIDDEN);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RISPOSTA_PER_RIFERIMENTO);
			de.setValue(risprif);
			dati.addElement(de);
		} else {
			String[] tipoInvRif = { CostantiConfigurazione.ABILITATO.toString(), CostantiConfigurazione.DISABILITATO.toString() };
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO);
			de.setType(DataElementType.SELECT);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_RICHIESTA);
			de.setValues(tipoInvRif);
			if(invrif==null){
				de.setSelected(CostantiConfigurazione.DISABILITATO.toString());
			}else{
				de.setSelected(invrif);
			}
			dati.addElement(de);

			String[] tipoRispRif = { CostantiConfigurazione.ABILITATO.toString(), CostantiConfigurazione.DISABILITATO.toString() };
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_RISPOSTA_PER_RIFERIMENTO);
			de.setType(DataElementType.SELECT);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RISPOSTA_PER_RIFERIMENTO);
			de.setValues(tipoRispRif);
			if(risprif==null){
				de.setSelected(CostantiConfigurazione.DISABILITATO.toString());
			}else{
				de.setSelected(risprif);
			}
			dati.addElement(de);
		}
		
		de = new DataElement();
		de.setLabel(ServiziApplicativiCostanti.LABEL_SERVIZIO_MESSAGE_BOX);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		String[] tipoGM = { CostantiConfigurazione.ABILITATO.toString(), CostantiConfigurazione.DISABILITATO.toString() };
		de = new DataElement();
		de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_MESSAGE_BOX);
		de.setType(DataElementType.SELECT);
		de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_MESSAGE_BOX);
		de.setValues(tipoGM);
		if (this.isModalitaStandard() && isInvocazioneServizio) {
			de.setPostBack(true);
		}
		if(getmsg==null){
			de.setSelected(CostantiConfigurazione.DISABILITATO.toString());
		}else{
			de.setSelected(getmsg);
		}
		dati.addElement(de);
	}


	public Vector<DataElement> addHiddenFieldsToDati(Vector<DataElement> dati, String provider,
			String idAsps, String idPorta){

		DataElement de = new DataElement();
		de.setLabel(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
		de.setValue(provider);
		de.setType(DataElementType.HIDDEN);
		de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_ASPS);
		de.setValue(idAsps);
		de.setType(DataElementType.HIDDEN);
		de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_ASPS);
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_PORTA);
		de.setValue(idPorta);
		de.setType(DataElementType.HIDDEN);
		de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_PORTA);
		dati.addElement(de);

		return dati;

	}

	public void prepareRuoliList(ISearch ricerca, List<String> lista)
			throws Exception {
		try {
			String idsil = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO);
			String idProvider = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione
			Integer parentSA = ServletUtils.getIntegerAttributeFromSession(ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT, this.session);
			if(parentSA == null) parentSA = ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE;
			Boolean useIdSogg = parentSA == ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_SOGGETTO;

			Parameter pSA = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO, idsil); 
			
			if(useIdSogg){
				Parameter pProvider = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, idProvider); 
				ServletUtils.addListElementIntoSession(this.session, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_RUOLI,pSA,pProvider );
			}else 
				ServletUtils.addListElementIntoSession(this.session, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_RUOLI,pSA);
			
			int idLista = Liste.SERVIZIO_APPLICATIVO_RUOLI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);
		
			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
		
			// Prendo il servizio applicativo
			int idSilInt = Integer.parseInt(idsil);
			ServizioApplicativo sa = this.saCore.getServizioApplicativo(idSilInt);
			String nomeservizioApplicativo = sa.getNome();		
			
			// Prendo il soggetto
			String tipoENomeSoggetto = null;
			String nomeProtocollo = null;
			if(useIdSogg){
				if(this.core.isRegistroServiziLocale()){
					Soggetto tmpSogg = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(idProvider));
					nomeProtocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(tmpSogg.getTipo());
					tipoENomeSoggetto = this.getLabelNomeSoggetto(nomeProtocollo, tmpSogg.getTipo() , tmpSogg.getNome());
				}else{
					org.openspcoop2.core.config.Soggetto tmpSogg = this.soggettiCore.getSoggetto(Integer.parseInt(idProvider));
					nomeProtocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(tmpSogg.getTipo());
					tipoENomeSoggetto = this.getLabelNomeSoggetto(nomeProtocollo, tmpSogg.getTipo() , tmpSogg.getNome());
				}
			}
		
			// setto la barra del titolo
			List<Parameter> listSA = new ArrayList<>();
			listSA.add(new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID, idsil));
			listSA.add(new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO, idsil));
			if(useIdSogg){
				listSA.add(new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, idProvider));
			}
			
			String labelApplicativi = ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI;
			String labelApplicativiDi = ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_DI;
			if(this.isModalitaCompleta()==false) {
				labelApplicativi = ServiziApplicativiCostanti.LABEL_APPLICATIVI;
				labelApplicativiDi = ServiziApplicativiCostanti.LABEL_PARAMETRO_APPLICATIVI_DI;
			}
			
			if(!useIdSogg){
				if (search.equals("")) {
					this.pd.setSearchDescription("");
					ServletUtils.setPageDataTitle(this.pd, 
							new Parameter(labelApplicativi,null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO,
									ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST),
							new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_DI + nomeservizioApplicativo, 
									ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE,
									listSA),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO,null));
				}
				else{
					ServletUtils.setPageDataTitle(this.pd, 
							new Parameter(labelApplicativi,null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO,
									ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST),
							new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_DI + nomeservizioApplicativo, 
									ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE,
									listSA),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA,null));	
				}
			} else {
				List<Parameter> lstParam = new ArrayList<Parameter>();

				lstParam.add(new Parameter(SoggettiCostanti.LABEL_SOGGETTI, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
				lstParam.add(new Parameter(labelApplicativiDi + tipoENomeSoggetto,
						ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST,
						new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,idProvider)));

				if(search.equals("")){
					this.pd.setSearchDescription("");
					lstParam.add(new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_DI + nomeservizioApplicativo, 
							ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE,
							listSA));
					lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO,null));
				}else{
					lstParam.add(new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_DI + nomeservizioApplicativo, 
							ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE,
							listSA));
					lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
				}

				ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));
			}
			
		
			// controllo eventuali risultati ricerca
			this.pd.setSearchLabel(CostantiControlStation.LABEL_PARAMETRO_RUOLO);
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, RuoliCostanti.LABEL_RUOLI, search);
			}
		
			// setto le label delle colonne
			String[] labels = { 
					CostantiControlStation.LABEL_PARAMETRO_RUOLO
			};
			this.pd.setLabels(labels);
		
			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();
		
			if (lista != null) {
				Iterator<String> it = lista.iterator();
				while (it.hasNext()) {
					String ruolo = it.next();
		
					Vector<DataElement> e = new Vector<DataElement>();
		
					DataElement de = new DataElement();
					de.setValue(ruolo);
					de.setIdToRemove(ruolo);
					e.addElement(de);
		
					dati.addElement(e);
				}
			}
		
			this.pd.setDati(dati);
			this.pd.setAddButton(true);
		
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	public List<Parameter> getTitoloSA(Integer parentSA, String idsogg, String idAsps, String idPorta)	throws Exception, DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String soggettoTitle = null;
		if(this.core.isRegistroServiziLocale()){
			Soggetto mySogg = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(idsogg));
			String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(mySogg.getTipo());
			soggettoTitle = this.getLabelNomeSoggetto(protocollo, mySogg.getTipo() , mySogg.getNome());
		}
		else{
			org.openspcoop2.core.config.Soggetto mySogg = this.soggettiCore.getSoggetto(Integer.parseInt(idsogg));
			String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(mySogg.getTipo());
			soggettoTitle = this.getLabelNomeSoggetto(protocollo, mySogg.getTipo() , mySogg.getNome());
		}
		return _getTitoloSA(parentSA, idsogg, idAsps, soggettoTitle,idPorta);
	}

	private List<Parameter> _getTitoloSA(Integer parentSA, String idsogg, String idAsps, String soggettoTitle, String idPorta)	throws Exception, DriverRegistroServiziNotFound, DriverRegistroServiziException {
		List<Parameter> lstParam = new ArrayList<>();
		switch (parentSA) {
		case ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_CONFIGURAZIONE:
			// Prendo il nome e il tipo del servizio
			AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
			String servizioTmpTile = this.getLabelIdServizio(asps);
			Parameter pIdServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId()+ "");
			Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, asps.getNome());
			Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, asps.getTipo());
			Parameter pIdsoggErogatore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+asps.getIdSoggetto());
			
			lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
			lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
			lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_CONFIGURAZIONI_DI + servizioTmpTile, 
					AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_PORTE_APPLICATIVE_LIST ,pIdServizio,pNomeServizio, pTipoServizio, pIdsoggErogatore));
			
			break;
		case ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_SOGGETTO:
			lstParam.add(new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SOGGETTI, null));
			lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
			lstParam.add(new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_DI + soggettoTitle,
					ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST,
					new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,idsogg)));
			break;
		case ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE:
		default:
			lstParam.add(new Parameter(ServiziApplicativiCostanti.LABEL_SERVIZIO_APPLICATIVO,null));
			lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO,ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST));
			break;
		}
		return lstParam;
	}
	
	
}
