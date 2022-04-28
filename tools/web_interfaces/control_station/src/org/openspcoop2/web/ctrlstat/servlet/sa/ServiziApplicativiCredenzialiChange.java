/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.ArchiveType;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ServiziApplicativiCredenzialiChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ServiziApplicativiCredenzialiChange extends Action {
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
		
		TipoOperazione tipoOperazione = TipoOperazione.CHANGE;
		
		try {
			ServiziApplicativiHelper saHelper = new ServiziApplicativiHelper(request, pd, session);
			
			ServiziApplicativiCore saCore = new ServiziApplicativiCore();
			SoggettiCore soggettiCore = new SoggettiCore(saCore);
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione
			Integer parentSA = ServletUtils.getIntegerAttributeFromSession(ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT, session, request);
			if(parentSA == null) parentSA = ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE;
			Boolean useIdSogg = parentSA == ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_SOGGETTO;
			
			String id = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID);
			int idServizioApplicativo = Integer.parseInt(id);
			
			String idCredenziale = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_CREDENZIALI_ID);
			int idxCredenziale = Integer.parseInt(idCredenziale);
			
			String provider = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			String dominio = saHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO);			
			
			String tipoauthSA = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE);
			String subjectSA = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT);
					
			String tipoCredenzialiSSLSorgente = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL);
			if(tipoCredenzialiSSLSorgente == null) {
				tipoCredenzialiSSLSorgente = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO;
			}
			String tipoCredenzialiSSLTipoArchivioS = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO);
			org.openspcoop2.utils.certificate.ArchiveType tipoCredenzialiSSLTipoArchivio= null;
			if(tipoCredenzialiSSLTipoArchivioS == null) {
				tipoCredenzialiSSLTipoArchivio = org.openspcoop2.utils.certificate.ArchiveType.CER; 
			} else {
				tipoCredenzialiSSLTipoArchivio = org.openspcoop2.utils.certificate.ArchiveType.valueOf(tipoCredenzialiSSLTipoArchivioS);
			}
			BinaryParameter tipoCredenzialiSSLFileCertificato = saHelper.getBinaryParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO);
			String tipoCredenzialiSSLFileCertificatoPassword = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_PASSWORD);
			List<String> listaAliasEstrattiCertificato = new ArrayList<String>();
			String tipoCredenzialiSSLAliasCertificato = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO);
			if (tipoCredenzialiSSLAliasCertificato == null) {
				tipoCredenzialiSSLAliasCertificato = "";
			}
			String tipoCredenzialiSSLAliasCertificatoSubject= saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SUBJECT);
			String tipoCredenzialiSSLAliasCertificatoIssuer= saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_ISSUER);
			String tipoCredenzialiSSLAliasCertificatoType= saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_TYPE);
			String tipoCredenzialiSSLAliasCertificatoVersion= saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_VERSION);
			String tipoCredenzialiSSLAliasCertificatoSerialNumber= saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SERIAL_NUMBER);
			String tipoCredenzialiSSLAliasCertificatoSelfSigned= saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SELF_SIGNED);
			String tipoCredenzialiSSLAliasCertificatoNotBefore= saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_NOT_BEFORE);
			String tipoCredenzialiSSLAliasCertificatoNotAfter = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_NOT_AFTER); 
			String tipoCredenzialiSSLVerificaTuttiICampi = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI);
			if (tipoCredenzialiSSLVerificaTuttiICampi == null || StringUtils.isEmpty(tipoCredenzialiSSLVerificaTuttiICampi)) {
				if(saHelper.isEditModeInProgress() && saHelper.getPostBackElementName()==null) { // prima volta
					tipoCredenzialiSSLVerificaTuttiICampi = ConnettoriCostanti.DEFAULT_VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI;
				}
			}
			String tipoCredenzialiSSLConfigurazioneManualeSelfSigned= saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_MANUALE_SELF_SIGNED);
			if (tipoCredenzialiSSLConfigurazioneManualeSelfSigned == null) {
				tipoCredenzialiSSLConfigurazioneManualeSelfSigned = Costanti.CHECK_BOX_DISABLED;
			}
			String issuerSA = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_ISSUER);

			String tipoCredenzialiSSLWizardStep = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP);
			if (tipoCredenzialiSSLWizardStep == null) {
				tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD;
			}
			String oldTipoCredenzialiSSLWizardStep = tipoCredenzialiSSLWizardStep;
			
			String promuoviCertificato = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_PROMUOVI);
			if(promuoviCertificato == null) {
				promuoviCertificato = Costanti.CHECK_BOX_DISABLED;
			}
						
			String actionConfirm = saHelper.getParameter(Costanti.PARAMETRO_ACTION_CONFIRM);
						
			boolean visualizzaPromuoviCertificato = idxCredenziale != 0; // CHANGE dipende dal certificato scelto (idx != 0) 
			String servletCredenzialiChange = ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CREDENZIALI_CHANGE;
			
			String aggiornatoCertificatoPrecaricatoTmp = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_MULTI_AGGIORNA);
			
//			Integer numeroCertificati = null;
			String toCall = ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CREDENZIALI_CHANGE;
			
			// Preparo il menu
			saHelper.makeMenu();

			// Prendo il nome e il provider del servizioApplicativo
			ServizioApplicativo sa = saCore.getServizioApplicativo(idServizioApplicativo);
			String oldNome = sa.getNome();
			IDSoggetto oldIdSoggetto = new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario());
			IDServizioApplicativo oldIdServizioApplicativo = new IDServizioApplicativo();
			oldIdServizioApplicativo.setIdSoggettoProprietario(oldIdSoggetto);
			oldIdServizioApplicativo.setNome(oldNome);
			int idProv = sa.getIdSoggetto().intValue();

			InvocazionePorta ip = sa.getInvocazionePorta();
			Credenziali credenziali = null;
			Credenziali oldCredenziali = null;
			if (ip != null) {
//				numeroCertificati = ip.sizeCredenzialiList();
				if(ip.sizeCredenzialiList()>0) {
					oldCredenziali = ip.getCredenziali(idxCredenziale);
					credenziali = ip.getCredenziali(idxCredenziale);
				}
			}
			
			List<Parameter> parametersServletSAChange = new ArrayList<Parameter>();
			Parameter pIdSA = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID, sa.getId()+"");
			parametersServletSAChange.add(pIdSA);
			Parameter pIdSoggettoSA = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, idProv+"");
			parametersServletSAChange.add(pIdSoggettoSA);
			if(dominio != null) {
				Parameter pDominio = new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO, dominio);
				parametersServletSAChange.add(pDominio);
			}
			List<Parameter> parametersServletCredenzialiChange = new ArrayList<Parameter>();
			Parameter pIdCredenziale = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_CREDENZIALI_ID, idCredenziale);
			parametersServletCredenzialiChange.add(pIdCredenziale);
			parametersServletCredenzialiChange.addAll(parametersServletSAChange);
			
			String nomeProtocollo = null;
			String tipoENomeSoggetto = null;
			String nomePdd = null;
			@SuppressWarnings("unused")
			IDSoggetto idSoggetto = null;
			if(saCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(idProv);
				nomeProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(soggetto.getTipo());
				tipoENomeSoggetto = saHelper.getLabelNomeSoggetto(nomeProtocollo, soggetto.getTipo() , soggetto.getNome());
				nomePdd = soggetto.getPortaDominio();
				idSoggetto = new IDSoggetto(soggetto.getTipo() , soggetto.getNome());
			}
			else{
				Soggetto soggetto = soggettiCore.getSoggetto(idProv);
				nomeProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(soggetto.getTipo());
				tipoENomeSoggetto = saHelper.getLabelNomeSoggetto(nomeProtocollo, soggetto.getTipo() , soggetto.getNome());
				idSoggetto = new IDSoggetto(soggetto.getTipo() , soggetto.getNome());
			}

			if(dominio==null) {
				if(saHelper.isProfiloModIPA(nomeProtocollo)) {
					PddCore pddCore = new PddCore(saCore);
					dominio = pddCore.isPddEsterna(nomePdd) ? SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE : SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE;
				}
			}
			
			String labelApplicativi = ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI;
			String labelApplicativiDi = ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_DI;
			if(saHelper.isModalitaCompleta()==false) {
				labelApplicativi = ServiziApplicativiCostanti.LABEL_APPLICATIVI;
				labelApplicativiDi = ServiziApplicativiCostanti.LABEL_PARAMETRO_APPLICATIVI_DI;
			}
			
			String postBackElementName = saHelper.getPostBackElementName();
			String labelButtonSalva = Costanti.LABEL_MONITOR_BUTTON_INVIA;

			boolean visualizzaConfirm = false;
			// Controllo se ho modificato il protocollo, ricalcolo il default della versione del protocollo
			if(postBackElementName != null ){
				// tipo di configurazione SSL
				if(postBackElementName.equalsIgnoreCase(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL) || 
						postBackElementName.equalsIgnoreCase(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_LINK_MODIFICA)) {
					listaAliasEstrattiCertificato = new ArrayList<String>();
					tipoCredenzialiSSLTipoArchivio = ArchiveType.CER;
					tipoCredenzialiSSLVerificaTuttiICampi = ConnettoriCostanti.DEFAULT_VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI;
					tipoCredenzialiSSLAliasCertificato = "";
					tipoCredenzialiSSLAliasCertificatoSubject= "";
					tipoCredenzialiSSLAliasCertificatoIssuer= "";
					tipoCredenzialiSSLAliasCertificatoType= "";
					tipoCredenzialiSSLAliasCertificatoVersion= "";
					tipoCredenzialiSSLAliasCertificatoSerialNumber= "";
					tipoCredenzialiSSLAliasCertificatoSelfSigned= "";
					tipoCredenzialiSSLAliasCertificatoNotBefore= "";
					tipoCredenzialiSSLAliasCertificatoNotAfter = "";
					saHelper.deleteBinaryParameters(tipoCredenzialiSSLFileCertificato); 
					tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CARICA_CERTIFICATO;

					if(tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE)) {
						subjectSA = "";
						issuerSA = "";
						tipoCredenzialiSSLConfigurazioneManualeSelfSigned = Costanti.CHECK_BOX_DISABLED;
						tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD;
					}
				}

				// cambio il tipo archivio butto via il vecchio certificato				
				if(postBackElementName.equalsIgnoreCase(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO)) {
					saHelper.deleteBinaryParameters(tipoCredenzialiSSLFileCertificato); 
					tipoCredenzialiSSLAliasCertificato = "";
					tipoCredenzialiSSLAliasCertificatoSubject= "";
					tipoCredenzialiSSLAliasCertificatoIssuer= "";
					tipoCredenzialiSSLAliasCertificatoType= "";
					tipoCredenzialiSSLAliasCertificatoVersion= "";
					tipoCredenzialiSSLAliasCertificatoSerialNumber= "";
					tipoCredenzialiSSLAliasCertificatoSelfSigned= "";
					tipoCredenzialiSSLAliasCertificatoNotBefore= "";
					tipoCredenzialiSSLAliasCertificatoNotAfter = "";
					listaAliasEstrattiCertificato = new ArrayList<String>();
					tipoCredenzialiSSLVerificaTuttiICampi = ConnettoriCostanti.DEFAULT_VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI;
					tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CARICA_CERTIFICATO;
				}

				// selezione alias
				if(postBackElementName.equalsIgnoreCase(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO)) {
					if(StringUtils.isNotEmpty(tipoCredenzialiSSLAliasCertificato)) {
						tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CERTIFICATO_OK;
					} else {
						tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_ALIAS_NON_SCELTO;
					}
					tipoCredenzialiSSLAliasCertificatoSubject= "";
					tipoCredenzialiSSLAliasCertificatoIssuer= "";
					tipoCredenzialiSSLAliasCertificatoType= "";
					tipoCredenzialiSSLAliasCertificatoVersion= "";
					tipoCredenzialiSSLAliasCertificatoSerialNumber= "";
					tipoCredenzialiSSLAliasCertificatoSelfSigned= "";
					tipoCredenzialiSSLAliasCertificatoNotBefore= "";
					tipoCredenzialiSSLAliasCertificatoNotAfter = "";
				}
				
				// mostro la modale di conferma
				if(postBackElementName.equalsIgnoreCase(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_LINK_PROMUOVI)) {
					visualizzaConfirm = true;
				}
			}
			
			// ho schiacciato uno dei due tasti della modale
			if(actionConfirm != null ) {
				if(actionConfirm.equals(Costanti.PARAMETRO_ACTION_CONFIRM_VALUE_NO)) {
					tipoauthSA = null; // Imposto tipoauthSA = null cosi mi viene visualizzata nuovamente la form completa.
				}
					tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD; // reset dell'eventuale wizard
					oldTipoCredenzialiSSLWizardStep = tipoCredenzialiSSLWizardStep;
			}
			
			boolean checkWizard = false;
			if(tipoauthSA != null && tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL)) {
				if(tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO) &&
						!ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI.equals(postBackElementName)) {
					if(tipoCredenzialiSSLFileCertificato.getValue() != null && tipoCredenzialiSSLFileCertificato.getValue().length > 0) {
						tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_ALIAS_NON_SCELTO;
						if(!tipoCredenzialiSSLTipoArchivio.equals(ArchiveType.CER)) {
							if(StringUtils.isNotEmpty(tipoCredenzialiSSLFileCertificatoPassword)) {
								try {
									listaAliasEstrattiCertificato = ArchiveLoader.readAliases(tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato.getValue(), tipoCredenzialiSSLFileCertificatoPassword);
									Collections.sort(listaAliasEstrattiCertificato);
	
									//se ho un solo alias lo imposto
									if(!listaAliasEstrattiCertificato.isEmpty() && listaAliasEstrattiCertificato.size() == 1) {
										tipoCredenzialiSSLAliasCertificato = listaAliasEstrattiCertificato.get(0);
									}
									
									// ho appena caricato il file devo solo far vedere la form senza segnalare il messaggio di errore
									if(oldTipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CARICA_CERTIFICATO)
											||oldTipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_PASSWORD_NON_PRESENTE) 
											|| oldTipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_ERRORE_LETTURA_CERTIFICATO)) {
										checkWizard = true;
										if(listaAliasEstrattiCertificato.size() > 1) {
											pd.setMessage("Il file caricato contiene pi&ugrave; certificati, selezionare un'"+ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO, MessageType.INFO);
										}  
									}
								}catch(UtilsException e) {
									pd.setMessage("Il Certificato selezionato non &egrave; valido: "+e.getMessage());
									tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_ERRORE_LETTURA_CERTIFICATO;
									tipoCredenzialiSSLAliasCertificato = "";
									tipoCredenzialiSSLAliasCertificatoSubject= "";
									tipoCredenzialiSSLAliasCertificatoIssuer= "";
									tipoCredenzialiSSLAliasCertificatoType= "";
									tipoCredenzialiSSLAliasCertificatoVersion= "";
									tipoCredenzialiSSLAliasCertificatoSerialNumber= "";
									tipoCredenzialiSSLAliasCertificatoSelfSigned= "";
									tipoCredenzialiSSLAliasCertificatoNotBefore= "";
									tipoCredenzialiSSLAliasCertificatoNotAfter = "";
								}
	
								if(!listaAliasEstrattiCertificato.isEmpty() && StringUtils.isNotEmpty(tipoCredenzialiSSLAliasCertificato)) {
									try {
										Certificate cSelezionato = ArchiveLoader.load(tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato.getValue(), tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLFileCertificatoPassword);
										tipoCredenzialiSSLAliasCertificatoIssuer = cSelezionato.getCertificate().getIssuer().getNameNormalized();
										tipoCredenzialiSSLAliasCertificatoSubject = cSelezionato.getCertificate().getSubject().getNameNormalized();
										tipoCredenzialiSSLAliasCertificatoSelfSigned = cSelezionato.getCertificate().isSelfSigned() ? CostantiControlStation.LABEL_SI : CostantiControlStation.LABEL_NO;
										tipoCredenzialiSSLAliasCertificatoSerialNumber = cSelezionato.getCertificate().getSerialNumber() + "";
										tipoCredenzialiSSLAliasCertificatoType = cSelezionato.getCertificate().getType();
										tipoCredenzialiSSLAliasCertificatoVersion = cSelezionato.getCertificate().getVersion() + "";
										tipoCredenzialiSSLAliasCertificatoNotBefore = saHelper.getSdfCredenziali().format(cSelezionato.getCertificate().getNotBefore());
										tipoCredenzialiSSLAliasCertificatoNotAfter = saHelper.getSdfCredenziali().format(cSelezionato.getCertificate().getNotAfter());
										tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CERTIFICATO_OK;
									}catch(UtilsException e) {
										pd.setMessage("Il Certificato selezionato non &egrave; valido: "+e.getMessage());
										tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_ERRORE_LETTURA_CERTIFICATO;
										tipoCredenzialiSSLAliasCertificato = "";
										tipoCredenzialiSSLAliasCertificatoSubject= "";
										tipoCredenzialiSSLAliasCertificatoIssuer= "";
										tipoCredenzialiSSLAliasCertificatoType= "";
										tipoCredenzialiSSLAliasCertificatoVersion= "";
										tipoCredenzialiSSLAliasCertificatoSerialNumber= "";
										tipoCredenzialiSSLAliasCertificatoSelfSigned= "";
										tipoCredenzialiSSLAliasCertificatoNotBefore= "";
										tipoCredenzialiSSLAliasCertificatoNotAfter = "";
									}
								} 
							} else {
								tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_PASSWORD_NON_PRESENTE;
							}
						} else {
							try {
								Certificate cSelezionato = ArchiveLoader.load(tipoCredenzialiSSLFileCertificato.getValue());
								tipoCredenzialiSSLAliasCertificatoIssuer = cSelezionato.getCertificate().getIssuer().getNameNormalized();
								tipoCredenzialiSSLAliasCertificatoSubject = cSelezionato.getCertificate().getSubject().getNameNormalized();
								tipoCredenzialiSSLAliasCertificatoSelfSigned = cSelezionato.getCertificate().isSelfSigned() ? CostantiControlStation.LABEL_SI : CostantiControlStation.LABEL_NO;
								tipoCredenzialiSSLAliasCertificatoSerialNumber = cSelezionato.getCertificate().getSerialNumber() + "";
								tipoCredenzialiSSLAliasCertificatoType = cSelezionato.getCertificate().getType();
								tipoCredenzialiSSLAliasCertificatoVersion = cSelezionato.getCertificate().getVersion() + "";
								tipoCredenzialiSSLAliasCertificatoNotBefore = saHelper.getSdfCredenziali().format(cSelezionato.getCertificate().getNotBefore());
								tipoCredenzialiSSLAliasCertificatoNotAfter = saHelper.getSdfCredenziali().format(cSelezionato.getCertificate().getNotAfter());
								
								// dalla seconda volta che passo, posso salvare, la prima mostro il recap del certificato estratto
								
								if(oldTipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CERTIFICATO_OK_TIPO_CER)||
										oldTipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CERTIFICATO_OK)) {
									tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CERTIFICATO_OK;
								} else {
									tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CERTIFICATO_OK_TIPO_CER;
									checkWizard = true;
								}
							}catch(UtilsException e) {
								pd.setMessage("Il Certificato selezionato non &egrave; valido: "+e.getMessage());
								tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_ERRORE_LETTURA_CERTIFICATO;
								tipoCredenzialiSSLAliasCertificato = "";
								tipoCredenzialiSSLAliasCertificatoSubject= "";
								tipoCredenzialiSSLAliasCertificatoIssuer= "";
								tipoCredenzialiSSLAliasCertificatoType= "";
								tipoCredenzialiSSLAliasCertificatoVersion= "";
								tipoCredenzialiSSLAliasCertificatoSerialNumber= "";
								tipoCredenzialiSSLAliasCertificatoSelfSigned= "";
								tipoCredenzialiSSLAliasCertificatoNotBefore= "";
								tipoCredenzialiSSLAliasCertificatoNotAfter = "";
							}
						}
					} else {
						tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CARICA_CERTIFICATO;
					}
				}
				
				if(StringUtils.isNotEmpty(tipoCredenzialiSSLWizardStep) && ( tipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CARICA_CERTIFICATO)
						||tipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_PASSWORD_NON_PRESENTE) 
						|| tipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_ERRORE_LETTURA_CERTIFICATO)
						)) {
					if(tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO)) {
						labelButtonSalva = ConnettoriCostanti.LABEL_BUTTON_INVIA_CARICA_CERTIFICATO;
					}
				}else { 
					labelButtonSalva = Costanti.LABEL_MONITOR_BUTTON_INVIA;
				}
			} else {
				labelButtonSalva = Costanti.LABEL_MONITOR_BUTTON_INVIA;
			}
			
			if(postBackElementName == null && oldTipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD))
				tipoCredenzialiSSLWizardStep  = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD;
			
			if(labelButtonSalva!= null) {
				pd.setLabelBottoneInvia(labelButtonSalva);
			}
			
			Parameter nomeCredenziale = new Parameter(saHelper.getLabelCredenzialeCertificato(oldCredenziali.getCnSubject()), null);
			
			if(visualizzaConfirm) {
				if(useIdSogg){
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
							new Parameter(labelApplicativiDi + tipoENomeSoggetto, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST, new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,provider)),
							new Parameter(sa.getNome(), ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE, parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])), 
							new Parameter(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CERTIFICATI, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CREDENZIALI_LIST , parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])),							
							nomeCredenziale
							);
				}else {
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(labelApplicativi, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST),
							new Parameter(sa.getNome(), ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE, parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])),
							new Parameter(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CERTIFICATI, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CREDENZIALI_LIST , parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])),							
							nomeCredenziale
							);
				}
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = saHelper.addServizioApplicativoHiddenToDati(dati, id, idProv+"", dominio, sa.getNome());
				
				dati = saHelper.addCredenzialiCertificatiToDati(dati, tipoOperazione, idCredenziale, tipoauthSA, subjectSA, toCall, 
						tipoCredenzialiSSLSorgente, tipoCredenzialiSSLTipoArchivio, 
						tipoCredenzialiSSLFileCertificato, tipoCredenzialiSSLFileCertificatoPassword, listaAliasEstrattiCertificato, 
						tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLAliasCertificatoSubject, tipoCredenzialiSSLAliasCertificatoIssuer,
						tipoCredenzialiSSLAliasCertificatoType, tipoCredenzialiSSLAliasCertificatoVersion, tipoCredenzialiSSLAliasCertificatoSerialNumber, 
						tipoCredenzialiSSLAliasCertificatoSelfSigned, tipoCredenzialiSSLAliasCertificatoNotBefore, tipoCredenzialiSSLAliasCertificatoNotAfter,
						tipoCredenzialiSSLVerificaTuttiICampi, tipoCredenzialiSSLConfigurazioneManualeSelfSigned, issuerSA, tipoCredenzialiSSLWizardStep, 
						promuoviCertificato,
						visualizzaPromuoviCertificato, null, servletCredenzialiChange, parametersServletCredenzialiChange);
				
				dati = saHelper.addCredenzialiCertificatiToDatiAsHidden(dati, tipoOperazione, idCredenziale, tipoauthSA, subjectSA, toCall, 
						tipoCredenzialiSSLSorgente, tipoCredenzialiSSLTipoArchivio, 
						tipoCredenzialiSSLFileCertificato, tipoCredenzialiSSLFileCertificatoPassword, listaAliasEstrattiCertificato, 
						tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLAliasCertificatoSubject, tipoCredenzialiSSLAliasCertificatoIssuer,
						tipoCredenzialiSSLAliasCertificatoType, tipoCredenzialiSSLAliasCertificatoVersion, tipoCredenzialiSSLAliasCertificatoSerialNumber, 
						tipoCredenzialiSSLAliasCertificatoSelfSigned, tipoCredenzialiSSLAliasCertificatoNotBefore, tipoCredenzialiSSLAliasCertificatoNotAfter,
						tipoCredenzialiSSLVerificaTuttiICampi, tipoCredenzialiSSLConfigurazioneManualeSelfSigned, issuerSA, tipoCredenzialiSSLWizardStep, 
						promuoviCertificato,
						visualizzaPromuoviCertificato, servletCredenzialiChange, parametersServletCredenzialiChange);
				
				pd.setDati(dati);
				
				String pre = Costanti.HTML_MODAL_SPAN_PREFIX;
				String post = Costanti.HTML_MODAL_SPAN_SUFFIX;
				
				String msg = "Vuoi promuovere il certificato visualizzato come principale per l''Applicativo {0}?";

				pd.setMessage(pre + MessageFormat.format(msg, sa.getNome()) + post, Costanti.MESSAGE_TYPE_CONFIRM);

				String[][] bottoni = { 
						{ Costanti.LABEL_MONITOR_BUTTON_ANNULLA, 
							Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_PREFIX +
							Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_SUFFIX

						},
						{ Costanti.LABEL_MONITOR_BUTTON_CONFERMA,
							Costanti.LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_PREFIX +
							Costanti.LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_SUFFIX }};

				pd.setBottoni(bottoni );
				
				// disabilito la form
				pd.disableEditMode();

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_CREDENZIALI, ForwardParams.CHANGE());
			}
			
			// Se nomehid = null, devo visualizzare la pagina per la modifica
			// dati
			if(saHelper.isEditModeInProgress() || checkWizard){
				
				if(useIdSogg){
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
							new Parameter(labelApplicativiDi + tipoENomeSoggetto, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST, new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,provider)),
							new Parameter(sa.getNome(), ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE, parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])), 
							new Parameter(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CERTIFICATI, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CREDENZIALI_LIST , parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])),							
							nomeCredenziale
							);
				}else {
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(labelApplicativi, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST),
							new Parameter(sa.getNome(), ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE, parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])),
							new Parameter(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CERTIFICATI, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CREDENZIALI_LIST , parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])),							
							nomeCredenziale
							);
				}
				
				if (tipoauthSA == null){
					if (credenziali != null){
						if(credenziali.getTipo()!=null)
							tipoauthSA = credenziali.getTipo().toString();
						
						if(credenziali.getCertificate() != null) {
							tipoCredenzialiSSLFileCertificato.setValue(credenziali.getCertificate());
							tipoCredenzialiSSLSorgente = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO;
							tipoCredenzialiSSLVerificaTuttiICampi = credenziali.isCertificateStrictVerification() ? Costanti.CHECK_BOX_ENABLED :Costanti.CHECK_BOX_DISABLED;
							
							try {
								Certificate cSelezionato = ArchiveLoader.load(tipoCredenzialiSSLFileCertificato.getValue());
								tipoCredenzialiSSLAliasCertificatoIssuer = cSelezionato.getCertificate().getIssuer().getNameNormalized();
								tipoCredenzialiSSLAliasCertificatoSubject = cSelezionato.getCertificate().getSubject().getNameNormalized();
								tipoCredenzialiSSLAliasCertificatoSelfSigned = cSelezionato.getCertificate().isSelfSigned() ? CostantiControlStation.LABEL_SI : CostantiControlStation.LABEL_NO;
								tipoCredenzialiSSLAliasCertificatoSerialNumber = cSelezionato.getCertificate().getSerialNumber() + "";
								tipoCredenzialiSSLAliasCertificatoType = cSelezionato.getCertificate().getType();
								tipoCredenzialiSSLAliasCertificatoVersion = cSelezionato.getCertificate().getVersion() + "";
								tipoCredenzialiSSLAliasCertificatoNotBefore = saHelper.getSdfCredenziali().format(cSelezionato.getCertificate().getNotBefore());
								tipoCredenzialiSSLAliasCertificatoNotAfter = saHelper.getSdfCredenziali().format(cSelezionato.getCertificate().getNotAfter());
							}catch(UtilsException e) {
								pd.setMessage("Il Certificato selezionato non &egrave; valido: "+e.getMessage());
								tipoCredenzialiSSLAliasCertificato = "";
								tipoCredenzialiSSLAliasCertificatoSubject= "";
								tipoCredenzialiSSLAliasCertificatoIssuer= "";
								tipoCredenzialiSSLAliasCertificatoType= "";
								tipoCredenzialiSSLAliasCertificatoVersion= "";
								tipoCredenzialiSSLAliasCertificatoSerialNumber= "";
								tipoCredenzialiSSLAliasCertificatoSelfSigned= "";
								tipoCredenzialiSSLAliasCertificatoNotBefore= "";
								tipoCredenzialiSSLAliasCertificatoNotAfter = "";
							}
						} else {
							subjectSA = credenziali.getSubject();
							issuerSA = credenziali.getIssuer();
							tipoCredenzialiSSLSorgente = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE;
							tipoCredenzialiSSLConfigurazioneManualeSelfSigned = ( subjectSA != null && subjectSA.equals(issuerSA)) ? Costanti.CHECK_BOX_ENABLED :Costanti.CHECK_BOX_DISABLED;
						}
					}
				}
				if (tipoauthSA == null) {
					tipoauthSA = ConnettoriCostanti.DEFAULT_AUTENTICAZIONE_TIPO;
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = saHelper.addServizioApplicativoHiddenToDati(dati, id, idProv+"", dominio, sa.getNome());
				
				dati = saHelper.addCredenzialiCertificatiToDati(dati, tipoOperazione, idCredenziale, tipoauthSA, subjectSA, toCall, 
						tipoCredenzialiSSLSorgente, tipoCredenzialiSSLTipoArchivio, 
						tipoCredenzialiSSLFileCertificato, tipoCredenzialiSSLFileCertificatoPassword, listaAliasEstrattiCertificato, 
						tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLAliasCertificatoSubject, tipoCredenzialiSSLAliasCertificatoIssuer,
						tipoCredenzialiSSLAliasCertificatoType, tipoCredenzialiSSLAliasCertificatoVersion, tipoCredenzialiSSLAliasCertificatoSerialNumber, 
						tipoCredenzialiSSLAliasCertificatoSelfSigned, tipoCredenzialiSSLAliasCertificatoNotBefore, tipoCredenzialiSSLAliasCertificatoNotAfter,
						tipoCredenzialiSSLVerificaTuttiICampi, tipoCredenzialiSSLConfigurazioneManualeSelfSigned, issuerSA, tipoCredenzialiSSLWizardStep, 
						promuoviCertificato,
						visualizzaPromuoviCertificato, 
						checkWizard ? null : aggiornatoCertificatoPrecaricatoTmp,
						servletCredenzialiChange, parametersServletCredenzialiChange);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_CREDENZIALI, ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			boolean isOk = saHelper.servizioApplicativoCredenzialiCertificatiCheckData(TipoOperazione.CHANGE, idProv, sa, idxCredenziale);
					
			if (!isOk) {
				
				// setto la barra del titolo
				if(useIdSogg){
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
						new Parameter(labelApplicativiDi + tipoENomeSoggetto, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST, new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,provider)),
						new Parameter(sa.getNome(), ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE, parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])), 
						new Parameter(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CERTIFICATI, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CREDENZIALI_LIST , parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])),							
						nomeCredenziale
						);
				}else {
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(labelApplicativi, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST),
							new Parameter(sa.getNome(), ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE, parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])),
							new Parameter(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CERTIFICATI, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CREDENZIALI_LIST , parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])),							
							nomeCredenziale
							);
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = saHelper.addServizioApplicativoHiddenToDati(dati, id, idProv+"", dominio, sa.getNome());
				
				dati = saHelper.addCredenzialiCertificatiToDati(dati, tipoOperazione, idCredenziale, tipoauthSA, subjectSA, toCall, 
						tipoCredenzialiSSLSorgente, tipoCredenzialiSSLTipoArchivio, 
						tipoCredenzialiSSLFileCertificato, tipoCredenzialiSSLFileCertificatoPassword, listaAliasEstrattiCertificato, 
						tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLAliasCertificatoSubject, tipoCredenzialiSSLAliasCertificatoIssuer,
						tipoCredenzialiSSLAliasCertificatoType, tipoCredenzialiSSLAliasCertificatoVersion, tipoCredenzialiSSLAliasCertificatoSerialNumber, 
						tipoCredenzialiSSLAliasCertificatoSelfSigned, tipoCredenzialiSSLAliasCertificatoNotBefore, tipoCredenzialiSSLAliasCertificatoNotAfter,
						tipoCredenzialiSSLVerificaTuttiICampi, tipoCredenzialiSSLConfigurazioneManualeSelfSigned, issuerSA, tipoCredenzialiSSLWizardStep, 
						promuoviCertificato,
						visualizzaPromuoviCertificato, 
						aggiornatoCertificatoPrecaricatoTmp,
						servletCredenzialiChange, parametersServletCredenzialiChange);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_CREDENZIALI, ForwardParams.CHANGE());
			}

			
			// Aggiorno credenziali
			//credenziali = new Credenziali();
			credenziali.setTipo(CredenzialeTipo.toEnumConstant(tipoauthSA));
			credenziali.setUser("");
			credenziali.setPassword("");
			
			credenziali.setAppId(false);
			
			if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL.equals(tipoauthSA)) {
				if(tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO)) {
					Certificate cSelezionato = null;
					
					// il certificato non e' cambiato 
					if(tipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD)) {
						cSelezionato = ArchiveLoader.load(oldCredenziali.getCertificate());
					} else {
						if(tipoCredenzialiSSLTipoArchivio.equals(ArchiveType.CER)) {
							cSelezionato = ArchiveLoader.load(tipoCredenzialiSSLFileCertificato.getValue());
						}else {
							cSelezionato = ArchiveLoader.load(tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato.getValue(), tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLFileCertificatoPassword);
						}
					}
					
					credenziali.setCnIssuer(cSelezionato.getCertificate().getIssuer().getCN());
					credenziali.setCnSubject(cSelezionato.getCertificate().getSubject().getCN()); 
					credenziali.setCertificate(cSelezionato.getCertificate().getCertificate().getEncoded());
					credenziali.setCertificateStrictVerification(ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLVerificaTuttiICampi));
				} else { // configurazione manuale
					credenziali.setSubject(subjectSA);
					if(ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLConfigurazioneManualeSelfSigned)) {
						credenziali.setIssuer(subjectSA);
					} else {
						credenziali.setIssuer(issuerSA);
					}
				}
			}
			
			if(actionConfirm != null ) {
				if(actionConfirm.equals(Costanti.PARAMETRO_ACTION_CONFIRM_VALUE_OK)) {
					// switch tra le posizioni
					// Rimuovo la credenziale principale dalla posizione 0
					Credenziali exCredenzialiPrincipali = sa.getInvocazionePorta().getCredenzialiList().remove(0);
					// imposto l'attuale credenziale in posizione 0
					sa.getInvocazionePorta().getCredenzialiList().add(0, credenziali); // promozione = set in posizione 0
					// assegno la posizione attuale all'ex credenziale principale
					sa.getInvocazionePorta().getCredenzialiList().set(idxCredenziale, exCredenzialiPrincipali);
				}
			}
			
			String userLogin = ServletUtils.getUserLoginFromSession(session);
			
			// eseguo l'aggiornamento
			List<Object> listOggettiDaAggiornare = ServiziApplicativiUtilities.getOggettiDaAggiornare(saCore, oldIdServizioApplicativo, sa);
			saCore.performUpdateOperation(userLogin, saHelper.smista(), listOggettiDaAggiornare.toArray());

			saHelper.deleteBinaryParameters(tipoCredenzialiSSLFileCertificato); 
		
			sa = saCore.getServizioApplicativo(idServizioApplicativo);

			saHelper.prepareServizioApplicativoCredenzialiList(sa, id);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_CREDENZIALI, ForwardParams.CHANGE());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_CREDENZIALI, ForwardParams.CHANGE());
		}
	}
}
