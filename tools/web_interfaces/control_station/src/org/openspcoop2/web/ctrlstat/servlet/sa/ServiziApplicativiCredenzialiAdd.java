/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.ArchiveType;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
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
 * ServiziApplicativiCredenzialiAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ServiziApplicativiCredenzialiAdd extends Action {
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
		
		TipoOperazione tipoOperazione = TipoOperazione.ADD;
		
		try {
			Boolean singlePdD = ServletUtils.getObjectFromSession(request, session, Boolean.class, CostantiControlStation.SESSION_PARAMETRO_SINGLE_PDD);

			ServiziApplicativiCore saCore = new ServiziApplicativiCore();
			
			ServiziApplicativiHelper saHelper = new ServiziApplicativiHelper(request, pd, session);
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione
			Integer parentSA = ServletUtils.getIntegerAttributeFromSession(ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT, session, request);
			if(parentSA == null) parentSA = ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE;
			Boolean useIdSogg = parentSA == ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_SOGGETTO;
			
			String id = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID);
			int idServizioApplicativo = Integer.parseInt(id);
			
			String idCredenziale = null;
			
			String nome = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME);
			String provider = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			String dominio = saHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO);			
			
			String tipoauthSA = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE);
			if (tipoauthSA == null) {
				tipoauthSA = ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL; // forzato ad https 
			}
			
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
				tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CARICA_CERTIFICATO;
			}
			String oldTipoCredenzialiSSLWizardStep = tipoCredenzialiSSLWizardStep;
			
			String promuoviCertificato = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_PROMUOVI);
			if(promuoviCertificato == null) {
				promuoviCertificato = Costanti.CHECK_BOX_DISABLED;
			}
			
			boolean visualizzaPromuoviCertificato = true; // ADD sempre true per far spuntare la checkbox
			String servletCredenzialiChange = null;
			List<Parameter> parametersServletCredenzialiChange = null;
//			Integer numeroCertificati = null;
			String toCall = ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CREDENZIALI_ADD;
			
			// Prendo il nome e il provider del servizioApplicativo
			ServizioApplicativo sa = saCore.getServizioApplicativo(idServizioApplicativo);
			if(sa==null) {
				throw new Exception("ServizioApplicativo con id '"+idServizioApplicativo+"' non trovato");
			}
			String oldNome = sa.getNome();
			IDSoggetto oldIdSoggetto = new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario());
			IDServizioApplicativo oldIdServizioApplicativo = new IDServizioApplicativo();
			oldIdServizioApplicativo.setIdSoggettoProprietario(oldIdSoggetto);
			oldIdServizioApplicativo.setNome(oldNome);
			int idProv = sa.getIdSoggetto().intValue();

//			InvocazionePorta ip = sa.getInvocazionePorta();
//			InvocazionePortaGestioneErrore ipge = null;
//			Credenziali credenziali = null;
//			Credenziali oldCredenziali = null;
//			if (ip != null) {
//				ipge = ip.getGestioneErrore();
//				numeroCertificati = ip.sizeCredenzialiList();
//				if(ip.sizeCredenzialiList()>0) {
//					credenziali = ip.getCredenziali(0);
//					oldCredenziali = ip.getCredenziali(0);
//					
//					visualizzaAddCertificato = true;
//					if(ip.sizeCredenzialiList() == 1) {  // se ho definito solo un certificato c'e' il link diretto alla modifica
//						visualizzaModificaCertificato = true;
//					}
//				}
//			}
						
			String postBackElementName = saHelper.getPostBackElementName();
			String labelButtonSalva = Costanti.LABEL_MONITOR_BUTTON_INVIA;

			// Controllo se ho modificato l'accordo, se si allora suggerisco il referente dell'accordo
			if(postBackElementName != null ){
				if(!useIdSogg && postBackElementName.equalsIgnoreCase(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROTOCOLLO)) {
					provider = null;
				}
				
				// tipo autenticazione
				if(postBackElementName.equalsIgnoreCase(ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE)){
					if(tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL)) {
						// reset impostazioni sezione ssl
						tipoCredenzialiSSLSorgente = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO;
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
						listaAliasEstrattiCertificato = new ArrayList<String>();
						tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CARICA_CERTIFICATO;
					} else {
						tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD;
					}
				}

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
			}
			
			
			// Tipi protocollo supportati
			List<String> listaTipiProtocollo = saCore.getProtocolliByFilter(request, session, true, PddTipologia.OPERATIVO, false);

			// Preparo il menu
			saHelper.makeMenu();

			String superUser = ServletUtils.getUserLoginFromSession(session);

			// Prendo la lista di soggetti
			String soggettoMultitenantSelezionato = null;
			if(!useIdSogg && saHelper.isSoggettoMultitenantSelezionato()) {
				soggettoMultitenantSelezionato = saHelper.getSoggettoMultitenantSelezionato();
			}
			ServiziApplicativiGeneralInfo generalInfo = ServiziApplicativiUtilities.getGeneralInfo(useIdSogg, provider, listaTipiProtocollo, 
					saCore, saHelper, superUser, singlePdD, soggettoMultitenantSelezionato, dominio);
					
			String tipoENomeSoggetto = generalInfo.getTipoENomeSoggetto();
			IDSoggetto idSoggetto = generalInfo.getIdSoggetto();
			provider = generalInfo.getProvider();

			String labelApplicativi = ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI;
			String labelApplicativiDi = ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_DI;
			if(saHelper.isModalitaCompleta()==false) {
				labelApplicativi = ServiziApplicativiCostanti.LABEL_APPLICATIVI;
				labelApplicativiDi = ServiziApplicativiCostanti.LABEL_PARAMETRO_APPLICATIVI_DI;
			}
			
			boolean checkWizard = false;
			if(tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL)) {
				if(tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO)) {
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
			
			if(labelButtonSalva!= null) {
				pd.setLabelBottoneInvia(labelButtonSalva);
			}
			
			IDServizioApplicativo idSA = new IDServizioApplicativo();
			idSA.setNome(nome);
			idSA.setIdSoggettoProprietario(idSoggetto);
			
			List<Parameter> parametersServletSAChange = new ArrayList<Parameter>();
			Parameter pIdSA = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID, sa.getId()+"");
			parametersServletSAChange.add(pIdSA);
			Parameter pIdSoggettoSA = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, idProv+"");
			parametersServletSAChange.add(pIdSoggettoSA);
			if(dominio != null) {
				Parameter pDominio = new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO, dominio);
				parametersServletSAChange.add(pDominio);
			}
			
			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if(saHelper.isEditModeInProgress() || checkWizard){
				
				// setto la barra del titolo
				List<Parameter> listParameter = new ArrayList<Parameter>();
				if(useIdSogg){
					listParameter.add(new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
					listParameter.add(new Parameter(labelApplicativiDi + tipoENomeSoggetto, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST, new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,provider)));
					listParameter.add(new Parameter(sa.getNome(), ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE, parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])));
					if(sa!=null && sa.getInvocazionePorta()!=null && sa.getInvocazionePorta().sizeCredenzialiList()>1) {
						listParameter.add(new Parameter(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CERTIFICATI, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CREDENZIALI_LIST , parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])));
					}
					listParameter.add(ServletUtils.getParameterAggiungi());
				}else {
					listParameter.add(new Parameter(labelApplicativi, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST));
					listParameter.add(new Parameter(sa.getNome(), ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE, parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])));
					if(sa!=null && sa.getInvocazionePorta()!=null && sa.getInvocazionePorta().sizeCredenzialiList()>1) {
						listParameter.add(new Parameter(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CERTIFICATI, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CREDENZIALI_LIST , parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])));
					}
					listParameter.add(ServletUtils.getParameterAggiungi());
				}
				ServletUtils.setPageDataTitle(pd, listParameter.toArray(new Parameter[1]));

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

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_CREDENZIALI, ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = saHelper.servizioApplicativoCredenzialiCertificatiCheckData(TipoOperazione.CHANGE, idProv, sa, 0);
			
			if (!isOk) {
				
				// setto la barra del titolo
				List<Parameter> listParameter = new ArrayList<Parameter>();
				if(useIdSogg){
					listParameter.add(new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
					listParameter.add(new Parameter(labelApplicativiDi + tipoENomeSoggetto, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST, new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,provider)));
					listParameter.add(new Parameter(sa.getNome(), ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE, parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])));
					if(sa!=null && sa.getInvocazionePorta()!=null && sa.getInvocazionePorta().sizeCredenzialiList()>1) {
						listParameter.add(new Parameter(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CERTIFICATI, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CREDENZIALI_LIST , parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])));
					}
					listParameter.add(ServletUtils.getParameterAggiungi());
				}else {
					listParameter.add(new Parameter(labelApplicativi, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST));
					listParameter.add(new Parameter(sa.getNome(), ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE, parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])));
					if(sa!=null && sa.getInvocazionePorta()!=null && sa.getInvocazionePorta().sizeCredenzialiList()>1) {
						listParameter.add(new Parameter(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CERTIFICATI, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CREDENZIALI_LIST , parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])));
					}
					listParameter.add(ServletUtils.getParameterAggiungi());
				}
				ServletUtils.setPageDataTitle(pd, listParameter.toArray(new Parameter[1]));

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

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_CREDENZIALI, ForwardParams.ADD());
			}

			// Nuove Credenziali 
			Credenziali credenziali = new Credenziali();
			credenziali.setTipo(CredenzialeTipo.toEnumConstant(tipoauthSA));
			credenziali.setUser("");
			credenziali.setPassword("");
			
			credenziali.setAppId(false);
			
			if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL.equals(tipoauthSA)) {
				if(tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO)) {
					Certificate cSelezionato = null;
					if(tipoCredenzialiSSLTipoArchivio.equals(ArchiveType.CER)) {
						cSelezionato = ArchiveLoader.load(tipoCredenzialiSSLFileCertificato.getValue());
					}else {
						cSelezionato = ArchiveLoader.load(tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato.getValue(), tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLFileCertificatoPassword);
					}

					credenziali.setCertificateStrictVerification(ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLVerificaTuttiICampi)); 
					credenziali.setCnIssuer(cSelezionato.getCertificate().getIssuer().getCN());
					credenziali.setCnSubject(cSelezionato.getCertificate().getSubject().getCN()); 
					credenziali.setCertificate(cSelezionato.getCertificate().getCertificate().getEncoded());
				} else { // configurazione manuale
					credenziali.setSubject(subjectSA);
					if(ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLConfigurazioneManualeSelfSigned)) {
						credenziali.setIssuer(subjectSA);
					} else {
						credenziali.setIssuer(issuerSA);
					}
				}
			}
			
			boolean promuovi = ServletUtils.isCheckBoxEnabled(promuoviCertificato);
			
			if(promuovi) {
				sa.getInvocazionePorta().getCredenzialiList().add(0, credenziali); // promozione = set in posizione 0
			} else {
				sa.getInvocazionePorta().addCredenziali(credenziali);
			}
			
			List<Object> listOggettiDaAggiornare = ServiziApplicativiUtilities.getOggettiDaAggiornare(saCore, oldIdServizioApplicativo, sa);
			saCore.performUpdateOperation(superUser, saHelper.smista(), listOggettiDaAggiornare.toArray());

			saHelper.deleteBinaryParameters(tipoCredenzialiSSLFileCertificato); 
			
			sa = saCore.getServizioApplicativo(idServizioApplicativo);

			saHelper.prepareServizioApplicativoCredenzialiList(sa, id);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_CREDENZIALI, ForwardParams.ADD());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_CREDENZIALI, ForwardParams.ADD());
		} 

	}

}
