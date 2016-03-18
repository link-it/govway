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
package org.openspcoop2.web.ctrlstat.servlet.sa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipologiaErogazione;
import org.openspcoop2.core.config.constants.TipologiaFruizione;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.dao.Ruolo;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddTipologia;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.GeneralLink;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.InterfaceType;
import org.openspcoop2.web.lib.users.dao.User;

/**
 * ServiziApplicativiHelper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServiziApplicativiHelper extends ConsoleHelper {

	private ConnettoriHelper connettoriHelper = null;
	public ServiziApplicativiHelper(HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(request, pd,  session);
		this.connettoriHelper = new ConnettoriHelper(request, pd, session);
	}

	// Controlla i dati dell'invocazione servizio del servizioApplicativo
	public boolean servizioApplicativoEndPointCheckData(List<ExtendedConnettore> listExtendedConnettore)
			throws Exception {
		try{
			String sbustamento= this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_SOAP);
			String sbustamentoInformazioniProtocolloRichiesta = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RICHIESTA);
			String getmsg = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_MESSAGE_BOX);
			String tipoauth = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_CONNETTORE);
			if (tipoauth == null) {
				tipoauth = ServiziApplicativiCostanti.DEFAULT_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE;
			}
			String utente = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_USERNAME_CONNETTORE);
			String password = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_PASSWORD_CONNETTORE);
			// String confpw = this.request.getParameter("confpw");
			String subject = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_SUBJECT_CONNETTORE);

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
					tmpElenco = ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_USERNAME;
				}
				if (password.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_PASSWORD;
					} else {
						tmpElenco = tmpElenco + ", "+ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_PASSWORD;
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
			if (tipoauth.equals(CostantiConfigurazione.CREDENZIALE_SSL.toString()) && subject.equals("")) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare il "+ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_SUBJECT);
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if (tipoauth.equals(CostantiConfigurazione.CREDENZIALE_BASIC.toString()) && ((utente.indexOf(" ") != -1) || (password.indexOf(" ") != -1))) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}
			if (tipoauth.equals(CostantiConfigurazione.CREDENZIALE_SSL.toString()) && (subject.indexOf(" ") != -1)) {
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

			if (!this.connettoriHelper.endPointCheckData(listExtendedConnettore)) {
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
			String utente,String password, String confpw, String subject, String tipoauth,
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
			List<ExtendedConnettore> listExtendedConnettore) throws Exception {

		if(ruoloFruitore==null){
			ruoloFruitore = TipologiaFruizione.DISABILITATO.getValue();
		}
		if(ruoloErogatore==null){
			ruoloErogatore = TipologiaErogazione.DISABILITATO.getValue();
		}
		
		boolean configurazioneStandardNonApplicabile = false;
		
		User user = ServletUtils.getUserFromSession(this.session);

		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_USA_ID_SOGGETTO , this.session);


		if(TipoOperazione.CHANGE.equals(tipoOperazione)){
			DataElement de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID);
			de.setValue(id);
			de.setType(DataElementType.HIDDEN);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID);
			dati.addElement(de);
		}

		DataElement de = new DataElement();
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

		ServizioApplicativo sa = null;
		PdDControlStation pdd = null;

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
				pdd = this.pddCore.getPdDControlStation(soggetto.getPortaDominio());
			}
			else{
				org.openspcoop2.core.config.Soggetto soggetto = this.soggettiCore.getSoggetto(sa.getIdSoggetto());
				tipoSoggetto = soggetto.getTipo();
				nomeSoggetto = soggetto.getNome();
			}

			// soggetto proprietario
			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			de.setValue(tipoENomeSoggetto);
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
				de.setType(DataElementType.TEXT);
				int find = -1;
				for(int i= 0 ; i < soggettiList.length ; i++)
					if(Integer.parseInt(soggettiList[i]) == Integer.parseInt(provider)){
						find = i;
						break;
					}

				de.setValue(soggettiListLabel[find]);
				de.setSize(this.getSize());

			}
			dati.addElement(de);

		}
		
		
		if (InterfaceType.STANDARD.equals(user.getInterfaceType())) {
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
			de.setPostBack(true);
			de.setType(DataElementType.SELECT);
			de.setValues(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_RUOLO);
			if(!TipologiaFruizione.DISABILITATO.equals(ruoloFruitore) && !TipologiaErogazione.DISABILITATO.equals(ruoloErogatore)){
				de.setLabel(CostantiControlStation.LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_SHORT_MESSAGE);
				de.setType(DataElementType.TEXT);
				de.setValue(" ");
				de.setPostBack(false);
				
				configurazioneStandardNonApplicabile = true;
			}
			else if(!TipologiaFruizione.DISABILITATO.equals(ruoloFruitore)){
				de.setSelected(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_RUOLO_FRUITORE);
				if(tipoauth==null || tipoauth.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_NESSUNA)){
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
				de.setSelected(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_RUOLO_EROGATORE);
				// forzo default
				ruoloErogatore = TipologiaErogazione.TRASPARENTE.getValue();
				// forzo connettoreAbilitato
				endpointtype = TipiConnettore.HTTP.getNome();
			}
			dati.addElement(de);
			
		}
		
		
		
		

		
		
		// ************ FRUITORE ********************
		
		if (InterfaceType.AVANZATA.equals(user.getInterfaceType()) || 
				!TipologiaFruizione.DISABILITATO.equals(ruoloFruitore) ) {
				
//			if(InterfaceType.STANDARD.equals(user.getInterfaceType())){
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
			if (confpw == null) {
				confpw = "";
			}
			if (subject == null) {
				subject = "";
			}
			dati = this.connettoriHelper.addCredenzialiToDati(dati, tipoauth, utente, password, confpw, subject, servlet, true, null, false, true, null);
			
		}


		if (InterfaceType.AVANZATA.equals(user.getInterfaceType())) {
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_ERRORE_APPLICATIVO);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}

		if (TipoOperazione.ADD.equals(tipoOperazione)) {
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_FAULT);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_FAULT);
			if (InterfaceType.AVANZATA.equals(user.getInterfaceType())) {
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
			if (InterfaceType.AVANZATA.equals(user.getInterfaceType())) {
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
				if (InterfaceType.AVANZATA.equals(user.getInterfaceType())) {
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
			if (InterfaceType.AVANZATA.equals(user.getInterfaceType())) {
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
			if (InterfaceType.AVANZATA.equals(user.getInterfaceType())) {
				de.setType(DataElementType.TEXT_EDIT);
			}
			else{
				de.setType(DataElementType.HIDDEN);
			}
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_FAULT_PREFIX);
			de.setSize(this.getSize());
			dati.addElement(de);
		}

		
		if(InterfaceType.AVANZATA.equals(user.getInterfaceType()) || 
			!TipologiaFruizione.DISABILITATO.equals(ruoloFruitore)){
		
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_TRATTAMENTO_MESSAGGIO);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO + " '" + nomeProtocollo + "'");
			de.setType(DataElementType.SELECT);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RISPOSTA);
			de.setValues(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_SBUSTAMENTO_PROTOCOLLO);
			de.setSelected(sbustamentoInformazioniProtocolloRisposta);
			dati.addElement(de);
			
		}
			

		if (InterfaceType.STANDARD.equals(user.getInterfaceType())) {
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
		if (TipoOperazione.CHANGE.equals(tipoOperazione) && InterfaceType.AVANZATA.equals(user.getInterfaceType())) {

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
			if (pdd!=null && PddTipologia.ESTERNO.toString().equals(pdd.getTipo())) {
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

			dati.addElement(de);

			// Ruoli
			Boolean gestioneRuoliSA = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_RUOLI_SA);
			Boolean singlePdD = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_SINGLE_PDD);
			if(!singlePdD && gestioneRuoliSA){
				de = new DataElement();
				de.setType(DataElementType.LINK);
				if (contaListe) {
					List<Ruolo> lista = this.saCore.ruoliWithIdServizioApplicativo(idSA);
					de.setValue(ServiziApplicativiCostanti.LABEL_RUOLI+"(" + lista.size() + ")");
				} else
					de.setValue(ServiziApplicativiCostanti.LABEL_RUOLI);
				de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_RUOLI_LIST,
						new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID,sa.getId()+""),
						new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_ACTION,TipoOperazione.LIST.toString()));
				dati.addElement(de);
			}
		}

		
		
		
		
		
		
		
		
		
		
		
		// ************ EROGATORE ********************
		
		if(InterfaceType.STANDARD.equals(user.getInterfaceType())){
			
			if(!TipologiaErogazione.DISABILITATO.equals(ruoloErogatore)){
						
				this.addEndPointToDati(dati,id,nome,sbustamento,sbustamentoInformazioniProtocolloRichiesta,
						getmsg,invrif,risprif,nomeProtocollo,false,true, true);
				
				if(TipologiaFruizione.DISABILITATO.equals(ruoloFruitore) &&
						CostantiConfigurazione.ABILITATO.equals(getmsg)){
					
					if(tipoauth==null || tipoauth.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_NESSUNA)){
						tipoauth = this.saCore.getAutenticazione_generazioneAutomaticaPorteDelegate();
					} 
						
					// Credenziali di accesso
					if (utente == null) {
						utente = "";
					}
					if (password == null) {
						password = "";
					}
					if (confpw == null) {
						confpw = "";
					}
					if (subject == null) {
						subject = "";
					}
					dati = this.connettoriHelper.addCredenzialiToDati(dati, tipoauth, utente, password, confpw, subject, servlet, true, null, false, true, null);
					
				}
				
				dati = this.connettoriHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, "",//ServiziApplicativiCostanti.LABEL_EROGATORE+" ",
						url, nomeCodaJMS,
						tipo, userRichiesta, passwordRichiesta, initcont, urlpgk, provurl,
						connfact, sendas, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, TipoOperazione.CHANGE, httpsurl, httpstipologia,
						httpshostverify, httpspath, httpstipo, httpspwd,
						httpsalgoritmo, httpsstato, httpskeystore,
						httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey, httpspwdprivatekey,
						httpsalgoritmokey, tipoconn, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT,
						nome, id, null, null, null,
						null, null, true,
						isConnettoreCustomUltimaImmagineSalvata, listExtendedConnettore);
			}
		}
		
		
		
		
		
		
		if(configurazioneStandardNonApplicabile){
			this.pd.setMessage(CostantiControlStation.LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_LONG_MESSAGE);
			this.pd.disableEditMode();
		}
		
		
		return dati;
	}



	public boolean servizioApplicativoCredenzialiCheckData(){
		String tipoauth = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SA);
		if (tipoauth == null) {
			tipoauth = ServiziApplicativiCostanti.DEFAULT_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE;
		}
		String utente = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_USERNAME_SA);
		String password = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_PASSWORD_SA);
		// String confpw = this.request.getParameter("confpw");
		String subject = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_SUBJECT_SA);
		
		if (tipoauth.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_BASIC) && (utente.equals("") || password.equals("") /*
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
				tmpElenco = ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_USERNAME;
			}
			if (password.equals("")) {
				if (tmpElenco.equals("")) {
					tmpElenco = ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_PASSWORD;
				} else {
					tmpElenco = tmpElenco + ", "+ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_PASSWORD;
				}
			}
			/*
			 * if (confpw.equals("")) { if (tmpElenco.equals("")) {
			 * tmpElenco = "Conferma password"; } else { tmpElenco =
			 * tmpElenco + ", Conferma password"; } }
			 */
			this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
			return false;
		}
		if (tipoauth.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SSL)){
			if (subject.equals("")) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare il "+
						ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_SUBJECT);
				return false;
			}else{
				try{
					org.openspcoop2.utils.Utilities.validaSubject(subject);
				}catch(Exception e){
					this.pd.setMessage("Le credenziali di tipo ssl  possiedono un subject non valido: "+e.getMessage());
					return false;
				}
			}
		}
		
		if (tipoauth.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_BASIC) && ((utente.indexOf(" ") != -1) || (password.indexOf(" ") != -1))) {
			this.pd.setMessage("Non inserire spazi nei campi di testo");
			return false;
		}
		
		if (!tipoauth.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_BASIC) && 
				!tipoauth.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SSL) && 
				!tipoauth.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_NESSUNA)) {
			this.pd.setMessage("Tipo dev'essere "+
					ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_BASIC+", "+
					ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SSL+" o "+
					ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_NESSUNA);
			return false;
		}
		
		return true;
	}

	public boolean servizioApplicativoCheckData(TipoOperazione tipoOperazione, String[] soggettiList, long idProvOld,
			String ruoloFruitore, String ruoloErogatore, List<ExtendedConnettore> listExtendedConnettore)
			throws Exception {
		try {
			
			if(ruoloFruitore==null){
				ruoloFruitore = TipologiaFruizione.DISABILITATO.getValue();
			}
			if(ruoloErogatore==null){
				ruoloErogatore = TipologiaErogazione.DISABILITATO.getValue();
			}
			
			User user = ServletUtils.getUserFromSession(this.session);
			
			String nome = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME);
			String provider = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			int newProv = 0;
			if (provider == null) {
				provider = "";
			} else {
				newProv = Integer.parseInt(provider);
			}
			String fault = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_FAULT);
//			String tipoauth = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SA);
//			if (tipoauth == null) {
//				tipoauth = ServiziApplicativiCostanti.DEFAULT_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE;
//			}
//			String utente = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_USERNAME_SA);
//			String password = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_PASSWORD_SA);
//			// String confpw = this.request.getParameter("confpw");
//			String subject = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_SUBJECT_SA);

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

			// Se tipoOp = add, controllo che il servizioApplicativo non sia
			// gia'
			// stato registrato
			if (tipoOperazione.equals(TipoOperazione.ADD)) {
				IDSoggetto ids = null;
				if(this.core.isRegistroServiziLocale()){
					Soggetto mySogg = this.soggettiCore.getSoggettoRegistro(newProv);
					ids = new IDSoggetto(mySogg.getTipo(), mySogg.getNome());
				}else{
					org.openspcoop2.core.config.Soggetto mySogg = this.soggettiCore.getSoggetto(newProv);
					ids = new IDSoggetto(mySogg.getTipo(), mySogg.getNome());
				}
				boolean giaRegistrato = this.saCore.existsServizioApplicativo(ids, nome);
				if (giaRegistrato) {
					this.pd.setMessage("Il Servizio Applicativo " + nome + " &egrave; gi&agrave; stato registrato per il soggetto scelto.");
					return false;
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
							ServizioApplicativo tmpSA = pde.getServizioApplicativo(j);
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
								ServizioApplicativo tmpSA = pa.getServizioApplicativo(j);
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

			
			if(servizioApplicativoCredenzialiCheckData()==false){
				return false;
			}
			
			String tipoauth = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SA);
			if (tipoauth == null) {
				tipoauth = ServiziApplicativiCostanti.DEFAULT_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE;
			}
			String utente = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_USERNAME_SA);
			String password = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_PASSWORD_SA);
			// String confpw = this.request.getParameter("confpw");
			String subject = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_SUBJECT_SA);
			
			// Se sono presenti credenziali, controllo che non siano gia'
			// utilizzate da altri soggetti
			if (tipoauth.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_BASIC)) {
				// recupera lista servizi applicativi con stesse credenziali
				List<ServizioApplicativo> saList = this.saCore.servizioApplicativoWithCredenzialiList(utente, password);

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
			} else if (tipoauth.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SSL)) {
				List<ServizioApplicativo> saList = this.saCore.servizioApplicativoWithCredenzialiList(subject);

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

			// erogatore
			if(InterfaceType.STANDARD.equals(user.getInterfaceType())){
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





	public void prepareServizioApplicativoList(ISearch ricerca, List<ServizioApplicativo> lista)
			throws Exception {
		try {
			String idProvider = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_USA_ID_SOGGETTO , this.session);

			if(useIdSogg){
				Parameter pProvider = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, idProvider); 
				ServletUtils.addListElementIntoSession(this.session, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI,pProvider );
			}else 
				ServletUtils.addListElementIntoSession(this.session, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI);

			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

			Boolean gestioneRuoliSA = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_RUOLI_SA);
			Boolean singlePdD = (Boolean) this.session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_SINGLE_PDD);



			User user = ServletUtils.getUserFromSession(this.session);

			// Prendo il soggetto
			String tmpTitle = null;
			String protocollo = null;
			boolean supportAsincroni = true;
			if(useIdSogg){
				if(this.core.isRegistroServiziLocale()){
					Soggetto tmpSogg = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(idProvider));
					tmpTitle = tmpSogg.getTipo() + "/" + tmpSogg.getNome();
					protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(tmpSogg.getTipo());
				}else{
					org.openspcoop2.core.config.Soggetto tmpSogg = this.soggettiCore.getSoggetto(Integer.parseInt(idProvider));
					tmpTitle = tmpSogg.getTipo() + "/" + tmpSogg.getNome();
					protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(tmpSogg.getTipo());
				}
				
				supportAsincroni = this.core.isProfiloDiCollaborazioneSupportatoDalProtocollo(protocollo, ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO)
						|| this.core.isProfiloDiCollaborazioneSupportatoDalProtocollo(protocollo, ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO);
				if(supportAsincroni==false){
					if (InterfaceType.AVANZATA.equals(user.getInterfaceType())){
						supportAsincroni = this.core.isElenchiSA_asincroniNonSupportati_VisualizzaRispostaAsincrona();
					}
				}
			}

			int idLista = useIdSogg ? Liste.SERVIZI_APPLICATIVI_BY_SOGGETTO : Liste.SERVIZIO_APPLICATIVO;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));


			// setto la barra del titolo
			if(!useIdSogg){
				if (search.equals("")) {
					this.pd.setSearchDescription("");
					ServletUtils.setPageDataTitle(this.pd, 
							new Parameter(ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI,null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO,null));
				}
				else{
					ServletUtils.setPageDataTitle(this.pd, 
							new Parameter(ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI,null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA,null));	
				}
			} else {
				List<Parameter> lstParam = new ArrayList<Parameter>();

				lstParam.add(new Parameter(SoggettiCostanti.LABEL_SOGGETTI, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));

				if(search.equals("")){
					this.pd.setSearchDescription("");
					lstParam.add(new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_DI + tmpTitle,null));
				}else{
					lstParam.add(new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_DI + tmpTitle,
							ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST ,
							new Parameter( ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, idProvider)));
					lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE_RISULTATI_RICERCA, null));
				}

				ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));
			}

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI, search);
			}

			// setto le label delle colonne
			List<String> labels = new ArrayList<String>();
			labels.add(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_NOME);
			labels.add(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			labels.add(ServiziApplicativiCostanti.LABEL_TIPOLOGIA);
			if (InterfaceType.AVANZATA.equals(user.getInterfaceType())){
				labels.add(ServiziApplicativiCostanti.LABEL_INVOCAZIONE_SERVIZIO);
			}
			if(supportAsincroni){
				labels.add(ServiziApplicativiCostanti.LABEL_RISPOSTA_ASINCRONA);
			}
			if(!singlePdD && gestioneRuoliSA){
				labels.add(ServiziApplicativiCostanti.LABEL_RUOLI);
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

					de = new DataElement();
					de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE, 
							new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID, sa.getId()+""),
							new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, sa.getIdSoggetto()+""));
					de.setValue(sa.getNome());
					de.setIdToRemove(sa.getId().toString());
					e.addElement(de);

					de = new DataElement();
					de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE, 
							new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID, sa.getIdSoggetto()+""),
							new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME, sa.getNomeSoggettoProprietario()),
							new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO, sa.getTipoSoggettoProprietario()));
					de.setValue(sa.getTipoSoggettoProprietario() + "/" + sa.getNomeSoggettoProprietario());
					e.addElement(de);

					boolean pddEsterna = false;
					if(this.core.isRegistroServiziLocale()){
						IDSoggetto tmpIDS = new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario());
						Soggetto tmpSogg = this.soggettiCore.getSoggettoRegistro(tmpIDS);
						String nomePdd = tmpSogg.getPortaDominio();
						PdDControlStation pdd = null;
						if("-".equals(nomePdd)==false)
							pdd = this.pddCore.getPdDControlStation(nomePdd);

						if( (pdd==null) || "esterno".equals(pdd.getTipo())){
							pddEsterna = true;
						}
					}

					de = new DataElement();
					de.setValue(this.getTipologia(sa));
					e.addElement(de);
					
					if (InterfaceType.AVANZATA.equals(user.getInterfaceType())){
						de = new DataElement();
						// se la pdd e' esterna non e' possibile modificare il
						// connettore invocazione servizio
						if (pddEsterna) {
							de.setValue("");// non visualizzo nulla e il link e'
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

					if(supportAsincroni){
						de = new DataElement();
						// se la pdd e' esterna non e' possibile modificare il
						// connettore risposta asincrona
						
						boolean supportoAsincronoPuntualeSoggetto = true;
						if(useIdSogg==false){
							String protocolloPuntuale = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(sa.getTipoSoggettoProprietario()); 
							supportoAsincronoPuntualeSoggetto = this.core.isProfiloDiCollaborazioneSupportatoDalProtocollo(protocolloPuntuale, ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO)
									|| this.core.isProfiloDiCollaborazioneSupportatoDalProtocollo(protocolloPuntuale, ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO);
							if(supportoAsincronoPuntualeSoggetto==false){
								if (InterfaceType.AVANZATA.equals(user.getInterfaceType())){
									supportoAsincronoPuntualeSoggetto = this.core.isElenchiSA_asincroniNonSupportati_VisualizzaRispostaAsincrona();
								}
							}
						}
						
						if (pddEsterna || !supportoAsincronoPuntualeSoggetto) {
							de.setValue("");// non visualizzo nulla e il link e'
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

					if(!singlePdD && gestioneRuoliSA){
						de = new DataElement();
						de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RUOLI);
						if (contaListe) {
							List<Ruolo> tmpLista = this.saCore.ruoliWithIdServizioApplicativo(sa.getId());
							ServletUtils.setDataElementVisualizzaLabel(de,new Long(tmpLista.size()));
						} else
							ServletUtils.setDataElementVisualizzaLabel(de);
						de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_RUOLI_LIST, 
								new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID, sa.getId()+""),
								new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_ACTION, TipoOperazione.LIST.toString()));
						e.addElement(de);
					}

					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(this.core.isShowPulsanteAggiungiElenchi());

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
			boolean isInvocazioneServizio, boolean showTitleTrattamentoMessaggio){

		User user = ServletUtils.getUserFromSession(this.session);
		
		DataElement de = new DataElement();
		de.setLabel(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO);
		de.setValue(idsil);
		de.setType(DataElementType.HIDDEN);
		de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO);
		dati.addElement(de);

		if(showName){
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_NOME);
			de.setValue(nomeservizioApplicativo);
			de.setType(DataElementType.TEXT);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME_SERVIZIO_APPLICATIVO);
			de.setSize(this.getSize());
			dati.addElement(de);
		}

		if(showTitleTrattamentoMessaggio){
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_TRATTAMENTO_MESSAGGIO);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}
		
		String[] tipoSbustamentoSOAP = { CostantiConfigurazione.ABILITATO.toString(), CostantiConfigurazione.DISABILITATO.toString() };
		de = new DataElement();
		de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_SOAP);
		de.setType(DataElementType.SELECT);
		de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_SOAP);
		de.setValues(tipoSbustamentoSOAP);
		if(sbustamento==null){
			de.setSelected(CostantiConfigurazione.DISABILITATO.toString());
		}else{
			de.setSelected(sbustamento);
		}
		dati.addElement(de);

		String[] tipoSbustamentoInformazioniProtocollo = { CostantiConfigurazione.ABILITATO.toString(), CostantiConfigurazione.DISABILITATO.toString() };
		de = new DataElement();
		if(nomeProtocollo!=null && !"".equals(nomeProtocollo)){
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO  + " '" + nomeProtocollo + "'");
		}else{
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_INFO_PROTOCOLLO);
		}
		de.setType(DataElementType.SELECT);
		de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RICHIESTA);
		de.setValues(tipoSbustamentoInformazioniProtocollo);
		if(sbustamentoInformazioniProtocolloRichiesta==null){
			de.setSelected(CostantiConfigurazione.ABILITATO.toString());
		}else{
			de.setSelected(sbustamentoInformazioniProtocolloRichiesta);
		}
		dati.addElement(de);

		if (InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
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
		if (InterfaceType.STANDARD.equals(user.getInterfaceType()) && isInvocazioneServizio) {
			de.setPostBack(true);
		}
		if(getmsg==null){
			de.setSelected(CostantiConfigurazione.DISABILITATO.toString());
		}else{
			de.setSelected(getmsg);
		}
		dati.addElement(de);
	}

	public Vector<DataElement> addRuoliToDati(Vector<DataElement> dati, List<Ruolo> ruoli, TipoOperazione tipoOp, boolean correlato,
			String idServizioApplicativo) {

		String[] ruoliValues = new String[ruoli.size()];

		for (int i = 0; i < ruoli.size(); i++) {
			Ruolo ruolo = ruoli.get(i);
			ruoliValues[i] = ruolo.getNome();
		}

		DataElement de = new DataElement();
		de.setLabel(ServiziApplicativiCostanti.LABEL_RUOLO);
		de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME);
		de.setValues(ruoliValues);
		de.setType(DataElementType.SELECT);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_CORRELATO);
		de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_CORRELATO);
		ServletUtils.setCheckBox(de, correlato);
		de.setType(DataElementType.CHECKBOX);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_ACTION);
		de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_ACTION);
		de.setType(DataElementType.HIDDEN);
		de.setValue(tipoOp.toString());
		dati.addElement(de);

		// id sa
		if(idServizioApplicativo!=null){
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID);
			de.setValue(idServizioApplicativo);
			de.setType(DataElementType.HIDDEN);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID);
			dati.addElement(de);
		}

		return dati;
	}

	public void prepareRuoliList(List<Ruolo> lista, ISearch ricerca)
			throws Exception {
		try {
			ServletUtils.addListElementIntoSession(this.session, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_RUOLI);

			int idLista = Liste.RUOLI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
			// setto la barra del titolo
			Vector<GeneralLink> titlelist = this.pd.getTitleList();

			if (!search.equals("")) {
				GeneralLink tl = new GeneralLink();
				tl.setLabel(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA);
				titlelist.addElement(tl);
			}
			this.pd.setTitleList(titlelist);

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, ServiziApplicativiCostanti.LABEL_RUOLI, search);
			}

			// setto le label delle colonne
			String[] labels = { ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_NOME, 
					ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_CORRELATO};
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			Iterator<Ruolo> it = lista.iterator();
			Ruolo ruolo = null;
			while (it.hasNext()) {
				ruolo = it.next();
				Vector<DataElement> e = new Vector<DataElement>();

				// idRuoloSA
				DataElement de = new DataElement();
				de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID);
				de.setValue(ruolo.getId() + "");
				de.setType(DataElementType.HIDDEN);
				e.addElement(de);

				// nome
				try {
					AccordoServizioParteComune as = this.apcCore.getAccordoServizio(this.idAccordoFactory.getIDAccordoFromUri(ruolo.getNome()));
					de = new DataElement();
					de.setName(ruolo.getNome());
					de.setUrl(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_CHANGE, 
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID,as.getId()+""),
							new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME,as.getNome()));
					de.setValue(ruolo.getNome());
					de.setIdToRemove(ruolo.getId() + ":" + as.getId());
					e.addElement(de);
				} catch (DriverRegistroServiziException drse) {
					// ok
				} catch (DriverRegistroServiziNotFound drsnf) {
					// ok
				}

				// correlato
				de = new DataElement();
				de.setValue("" + ruolo.isCorrelato());
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

	public Vector<DataElement> addHiddenFieldsToDati(Vector<DataElement> dati, String provider){

		DataElement de = new DataElement();
		de.setLabel(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
		de.setValue(provider);
		de.setType(DataElementType.HIDDEN);
		de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
		dati.addElement(de);

		return dati;

	}
}
