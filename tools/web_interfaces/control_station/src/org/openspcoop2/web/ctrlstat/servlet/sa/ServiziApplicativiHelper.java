/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipologiaErogazione;
import org.openspcoop2.core.config.constants.TipologiaFruizione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteApplicative;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.config.driver.db.IDServizioApplicativoDB;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ConfigurazioneFiltroServiziApplicativi;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.ArchiveType;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.certificate.KeystoreParams;
import org.openspcoop2.utils.certificate.byok.BYOKManager;
import org.openspcoop2.utils.crypt.PasswordGenerator;
import org.openspcoop2.utils.crypt.PasswordVerifier;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ControlStationCoreException;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.costanti.InUsoType;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationNotFound;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCostanti;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ExporterUtils;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.ruoli.RuoliCostanti;
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
	public ServiziApplicativiHelper(ControlStationCore core, HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(core, request, pd,  session);
	}

	// Controlla i dati dell'invocazione servizio del servizioApplicativo
	public boolean servizioApplicativoEndPointCheckData(String protocollo, List<ExtendedConnettore> listExtendedConnettore, ServizioApplicativo saOld)
			throws Exception {
		try{
			String sbustamento= this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_SOAP);
			String sbustamentoInformazioniProtocolloRichiesta = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RICHIESTA);
			String getmsg = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_MESSAGE_BOX);
			String getmsgUsername = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME);
			String getmsgPassword = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			String tipoauth = this.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_TIPO_AUTENTICAZIONE);
			if (tipoauth == null) {
				tipoauth = ConnettoriCostanti.DEFAULT_AUTENTICAZIONE_TIPO;
			}
			String utente = this.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
			String password = this.getLockedParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			// String confpw = this.getParameter("confpw");
			
			String servizioApplicativoServerEnabledS = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ABILITA_USO_APPLICATIVO_SERVER);
			boolean servizioApplicativoServerEnabled = ServletUtils.isCheckBoxEnabled(servizioApplicativoServerEnabledS);
//			String servizioApplicativoServer = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ID_APPLICATIVO_SERVER);
			
			if(!servizioApplicativoServerEnabled) {		
			
				// Campi obbligatori
				if (tipoauth.equals("")) {
					this.pd.setMessage("Dati incompleti. &Egrave; necessario indicare il Tipo");
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
					this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_PREFISSO_DATI_INCOMPLETI_NECESSARIO_INDICARE + tmpElenco);
					return false;
				}
	
				// Controllo che non ci siano spazi nei campi di testo
				if (tipoauth.equals(CostantiConfigurazione.CREDENZIALE_BASIC.toString()) && ((utente.indexOf(" ") != -1) || (password.indexOf(" ") != -1))) {
					this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_NEI_CAMPI_DI_TESTO);
					return false;
				}
	
				// Controllo che i campi DataElementType.SELECT abbiano uno dei valori ammessi
				if (!CostantiConfigurazione.ABILITATO.toString().equals(getmsg) && !CostantiConfigurazione.DISABILITATO.toString().equals(getmsg)) {
					this.pd.setMessage("Servizio '"+ServiziApplicativiCostanti.LABEL_SERVIZIO_MESSAGE_BOX+"' dev'essere "+CostantiConfigurazione.ABILITATO+" o "+CostantiConfigurazione.DISABILITATO);
					return false;
				}
				if (getmsg!=null && getmsg.equals(CostantiConfigurazione.ABILITATO.toString()) ){
					
					boolean add = true;
					if(saOld!=null && saOld.getInvocazionePorta()!=null && saOld.getInvocazionePorta().sizeCredenzialiList()>0) {
						Credenziali c = saOld.getInvocazionePorta().getCredenziali(0);
						if(CredenzialeTipo.BASIC.equals(c.getTipo())) {
							add = false;
						}
					}
					boolean encryptEnabled = this.saCore.isApplicativiPasswordEncryptEnabled();
					
					boolean validaPassword = false;
					if(add || !encryptEnabled) {
						validaPassword = true;
					}
					else {
						String changePwd = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CHANGE_PASSWORD);
						if(ServletUtils.isCheckBoxEnabled(changePwd)) {
							validaPassword = true;
						}
					}
					
					boolean passwordEmpty = false;
					if(validaPassword) {
						if(getmsgPassword==null || getmsgPassword.equals("")) {
							passwordEmpty = true;
						}
					}
					
					if(getmsgUsername==null || "".equals(getmsgUsername)) {
						this.pd.setMessage("Dati incompleti. &Egrave; necessario indicare 'Username' per il servizio '"+ServiziApplicativiCostanti.LABEL_SERVIZIO_MESSAGE_BOX+"'");
						return false;
					}
					if(passwordEmpty) {
						this.pd.setMessage("Dati incompleti. &Egrave; necessario indicare 'Password' per il servizio '"+ServiziApplicativiCostanti.LABEL_SERVIZIO_MESSAGE_BOX+"'");
						return false;
					}
					if (((getmsgUsername.indexOf(" ") != -1) || (validaPassword && getmsgPassword.indexOf(" ") != -1))) {
						this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_NEI_CAMPI_DI_TESTO);
						return false;
					}
					
					if(validaPassword) {
						PasswordVerifier passwordVerifier = this.saCore.getApplicativiPasswordVerifier();
						if(passwordVerifier!=null){
							StringBuilder motivazioneErrore = new StringBuilder();
							if(passwordVerifier.validate(getmsgUsername, getmsgPassword, motivazioneErrore)==false){
								this.pd.setMessage(motivazioneErrore.toString());
								return false;
							}
						}
					}
					
					// recupera lista servizi applicativi con stesse credenziali
					boolean checkPassword = this.saCore.isApplicativiCredenzialiBasicCheckUniqueUsePassword(); // la password non viene utilizzata per riconoscere se l'username e' già utilizzato.
					List<ServizioApplicativo> saList = this.saCore.servizioApplicativoWithCredenzialiBasicList(getmsgUsername, getmsgPassword, checkPassword);

					for (int i = 0; i < saList.size(); i++) {
						ServizioApplicativo sa = saList.get(i);

						if(saOld!=null && saOld.getId().longValue() == sa.getId().longValue()) {
							continue;
						}

						// Messaggio di errore
						IDSoggetto idSoggettoProprietario = new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario());
						String labelSoggetto = this.getLabelNomeSoggetto(idSoggettoProprietario);
						if(sa.getTipo()!=null && StringUtils.isNotEmpty(sa.getTipo())) {
							this.pd.setMessage("L'applicativo "+sa.getNome()+" (soggetto: "+labelSoggetto+") possiede già l'utente (http-basic) indicato");
						}
						else {
							IDServizioApplicativo idSA = new IDServizioApplicativo();
							idSA.setIdSoggettoProprietario(idSoggettoProprietario);
							idSA.setNome(sa.getNome());
							List<IDPortaApplicativa> list = this.porteApplicativeCore.porteApplicativeWithApplicativoErogatore(idSA);
							String labelErogazione = sa.getNome();
							if(list!=null && !list.isEmpty()) {
								try {
									PortaApplicativa paFound = this.porteApplicativeCore.getPortaApplicativa(list.get(0));
									MappingErogazionePortaApplicativa mappingPA = this.porteApplicativeCore.getMappingErogazionePortaApplicativa(paFound);
									labelErogazione = this.getLabelIdServizio(mappingPA.getIdServizio());
									if(!mappingPA.isDefault()) {
										labelErogazione = labelErogazione+" (gruppo:"+mappingPA.getDescrizione()+")";
									}
								}catch(Throwable t) {
									this.logError("Errore durante l'identificazione dell'erogazione: "+t.getMessage(),t);
								}
							}
							this.pd.setMessage("L'erogazione "+labelErogazione+" possiede già l'utente (http-basic) indicato per il servizio '"+ServiziApplicativiCostanti.LABEL_SERVIZIO_MESSAGE_BOX+"'");
						}
						return false;
					}
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

			}
			if (!this.endPointCheckData(protocollo, true, listExtendedConnettore)) {
				return false;
			}

			return true;
		} catch (Exception e) {
			this.logError("Exception: " + e.getMessage(), e);
			throw new Exception(e.getMessage(),e);
		}
	}

	public List<DataElement> addServizioApplicativoToDati(List<DataElement> dati, String oldNomeSA, String nome, String descrizione, String tipoENomeSoggetto, String fault, TipoOperazione tipoOperazione,  
			long idSA, Boolean contaListe,String[] soggettiList,String[] soggettiListLabel, String provider, String dominio,
			String utente,String password, String subject, String principal, String tipoauth,
			String faultactor,String genericfault,String prefixfault, String invrif, String sbustamentoInformazioniProtocolloRisposta,
			String servlet,String id, String nomeProtocollo,
			String ruoloFruitore, String ruoloErogatore,
			String sbustamento, String sbustamentoInformazioniProtocolloRichiesta, String getmsg,
			String invrifRichiesta, String risprif,
			String endpointtype, String autenticazioneHttp, String url, String nomeCodaJMS, String tipoCodaJMS,
			String userRichiesta, String passwordRichiesta, String initcont, String urlpgk,
			String provurl, String connfact, String sendas,  
			String httpsurl, String httpstipologia, boolean httpshostverify,
			boolean httpsTrustVerifyCert, String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey,
			String httpsKeyAlias, String httpsTrustStoreCRLs, String httpsTrustStoreOCSPPolicy, String httpsKeyStoreBYOKPolicy,
			String tipoconn,
			String connettoreDebug,
			Boolean isConnettoreCustomUltimaImmagineSalvata,
			String proxyEnabled, String proxyHost, String proxyPort, String proxyUsername, String proxyPassword,
			String tempiRispostaEnabled, String tempiRispostaConnectionTimeout, String tempiRispostaReadTimeout, String tempiRispostaTempoMedioRisposta,
			String opzioniAvanzate, String transferMode, String transferModeChunkSize, String redirectMode, String redirectMaxHop, String httpImpl,
			String requestOutputFileName, String requestOutputFileNamePermissions, String requestOutputFileNameHeaders, String requestOutputFileNameHeadersPermissions,
			String requestOutputParentDirCreateIfNotExists,String requestOutputOverwriteIfExists,
			String responseInputMode, String responseInputFileName, String responseInputFileNameHeaders, String responseInputDeleteAfterRead, String responseInputWaitTime,
			String tipoProtocollo, List<String> listaTipiProtocollo, List<ExtendedConnettore> listExtendedConnettore,String tipoCredenzialiSSLSorgente, ArchiveType tipoCredenzialiSSLTipoArchivio, BinaryParameter tipoCredenzialiSSLFileCertificato,
			String tipoCredenzialiSSLFileCertificatoPassword,
			List<String> listaAliasEstrattiCertificato, String tipoCredenzialiSSLAliasCertificato,	String tipoCredenzialiSSLAliasCertificatoSubject, String tipoCredenzialiSSLAliasCertificatoIssuer,
			String tipoCredenzialiSSLAliasCertificatoType, String tipoCredenzialiSSLAliasCertificatoVersion, String tipoCredenzialiSSLAliasCertificatoSerialNumber, String tipoCredenzialiSSLAliasCertificatoSelfSigned,
			String tipoCredenzialiSSLAliasCertificatoNotBefore, String tipoCredenzialiSSLAliasCertificatoNotAfter, String tipoCredenzialiSSLVerificaTuttiICampi, String tipoCredenzialiSSLConfigurazioneManualeSelfSigned,
			String issuer,String tipoCredenzialiSSLStatoElaborazioneCertificato,
			String changepwd,
			String multipleApiKey, String appId, String apiKey,
			boolean autenticazioneToken, String tokenPolicy, String tipoSA, boolean useAsClient,
			boolean integrationManagerEnabled, 
			boolean visualizzaModificaCertificato, boolean visualizzaAddCertificato, String servletCredenzialiList, List<Parameter> parametersServletCredenzialiList, Integer numeroCertificati, String servletCredenzialiAdd,
			String tokenPolicySA, String tokenClientIdSA, boolean tokenWithHttpsEnabledByConfigSA,
			String autenticazioneApiKey, boolean useOAS3Names, boolean useAppId, String apiKeyHeader, String apiKeyValue, String appIdHeader, String appIdValue) throws Exception {

		if(oldNomeSA!=null && invrifRichiesta!=null) {
			// nop
		}
		
		ServizioApplicativo sa = null;
		if(TipoOperazione.CHANGE.equals(tipoOperazione)){
			
			sa = this.saCore.getServizioApplicativo(idSA);

			if(sa==null) {
				throw new ControlStationCoreException("Servizi applicativo con id '"+idSA+"' non trovato");
			}
			
			IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
			idServizioApplicativo.setNome(sa.getNome());
			idServizioApplicativo.setIdSoggettoProprietario(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
			
			String labelApplicativo = (this.isSoggettoMultitenantSelezionato() ? sa.getNome() : this.getLabelServizioApplicativoConDominioSoggetto(idServizioApplicativo));
			
			Parameter pSAId = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID, sa.getId()+"");
			Parameter pSAIdSoggetto = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, sa.getIdSoggetto()+"");
			Parameter pDominio = new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO, dominio);
			List<Parameter> listaParametriChange = new ArrayList<>();
			listaParametriChange.add(pSAId);
			listaParametriChange.add(pSAIdSoggetto);
			listaParametriChange.add(pDominio);

			
			// In Uso Button
			this.addComandoInUsoButton(labelApplicativo,
					idSA+"",
					InUsoType.SERVIZIO_APPLICATIVO);
			
			// Verifica Certificati
			if(this.core.isApplicativiVerificaCertificati()) {
				
				// Verifica certificati visualizzato solo se il soggetto ha credenziali https
				boolean ssl = false;
				InvocazionePorta ip = sa.getInvocazionePorta();
				for (int i = 0; i < ip.sizeCredenzialiList(); i++) {
					Credenziali c = ip.getCredenziali(i);
					if(org.openspcoop2.core.config.constants.CredenzialeTipo.SSL.equals(c.getTipo())) { 
							//&& c.getCertificate()!=null) { non viene ritornato dalla lista
						ssl = true;
					}
				}
	
				// Verifica configurazione modi di sicurezza
				boolean modi = this.core.isProfiloModIPA(tipoProtocollo);
				boolean sicurezzaMessaggioModi = false;
				boolean server = ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(sa.getTipo());
				if(modi){
					KeystoreParams keystoreParams =	org.openspcoop2.protocol.utils.ModIUtils.getApplicativoKeystoreParams(sa.getProtocolPropertyList());
					sicurezzaMessaggioModi = keystoreParams!= null;
				}
				
				if(ssl || sicurezzaMessaggioModi || server) {
					this.pd.addComandoVerificaCertificatiElementoButton(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_VERIFICA_CERTIFICATI, listaParametriChange);
				}
			}

			// Verifica connettività 
			if(ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(sa.getTipo())) {
				List<Parameter> listaParametriVerificaConnettivitaChange = new ArrayList<>();
				listaParametriVerificaConnettivitaChange.addAll(listaParametriChange);
				listaParametriVerificaConnettivitaChange.add(new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTIVITA, "true"));
				this.pd.addComandoVerificaConnettivitaElementoButton(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_VERIFICA_CERTIFICATI, listaParametriVerificaConnettivitaChange);
			}
			
			// se e' abilitata l'opzione reset cache per elemento, visualizzo il comando nell'elenco dei comandi disponibili nella lista
			if(this.core.isElenchiVisualizzaComandoResetCacheSingoloElemento()){
				listaParametriChange.add(new Parameter(CostantiControlStation.PARAMETRO_ELIMINA_ELEMENTO_DALLA_CACHE, "true"));
				this.pd.addComandoResetCacheElementoButton(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE, listaParametriChange);
			}
			
			// Proprieta Button
			if(sa!=null && this.existsProprietaOggetto(sa.getProprietaOggetto(), sa.getDescrizione())) {
				this.addComandoProprietaOggettoButton(labelApplicativo,
						idSA+"", InUsoType.SERVIZIO_APPLICATIVO);
			}
		}

		
		if(ruoloFruitore==null){
			ruoloFruitore = TipologiaFruizione.DISABILITATO.getValue();
		}
		if(ruoloErogatore==null){
			ruoloErogatore = TipologiaErogazione.DISABILITATO.getValue();
		}
		
		boolean multitenant = this.saCore.isMultitenant();
		
		boolean configurazioneStandardNonApplicabile = false;
		
		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione
		Integer parentSA = ServletUtils.getIntegerAttributeFromSession(ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT, this.session, this.request);
		if(parentSA == null) parentSA = ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE;
		Boolean useIdSoggObject = parentSA == ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_SOGGETTO;
		boolean useIdSogg = false;
		if(useIdSoggObject!=null) {
			useIdSogg = useIdSoggObject.booleanValue();
		}

		IProtocolFactory<?> p = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(tipoProtocollo);
		IProtocolConfiguration config = p.createProtocolConfiguration();
		
		if(TipoOperazione.CHANGE.equals(tipoOperazione)){
			DataElement de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID);
			de.setValue(id);
			de.setType(DataElementType.HIDDEN);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID);
			dati.add(de);
		}

		DataElement de = new DataElement();
		if(this.isModalitaCompleta()) {
			de.setLabel(ServiziApplicativiCostanti.LABEL_SERVIZIO_APPLICATIVO);
		}
		else {
			de.setLabel(ServiziApplicativiCostanti.LABEL_APPLICATIVO);
		}
		de.setType(DataElementType.TITLE);
		dati.add(de);


		if(!useIdSogg) {
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROTOCOLLO);
	
			boolean showProtocolli = TipoOperazione.CHANGE.equals(tipoOperazione) && (this.core.countProtocolli(this.request, this.session)>1);
			
			if( (listaTipiProtocollo != null && listaTipiProtocollo.size() > 1) || showProtocolli){
				if(TipoOperazione.CHANGE.equals(tipoOperazione)){
					
					DataElement deLABEL = new DataElement();
					deLABEL.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROTOCOLLO);
					deLABEL.setType(DataElementType.TEXT);
					deLABEL.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROTOCOLLO+CostantiControlStation.PARAMETRO_SUFFIX_LABEL);
					deLABEL.setValue(this.getLabelProtocollo(tipoProtocollo));
					dati.add(deLABEL);
					
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
			dati.add(de);
		}
		
		boolean dominioEsterno = false;
		boolean isSupportatoAutenticazioneApplicativiEsterni = this.saCore.isSupportatoAutenticazioneApplicativiEsterniErogazione(nomeProtocollo);
		if(isSupportatoAutenticazioneApplicativiEsterni) {
			de = new DataElement();
			de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_DOMINIO);
			de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO);
			if(TipoOperazione.CHANGE.equals(tipoOperazione)){
				de.setType(DataElementType.HIDDEN);
				de.setValue(dominio);
				dati.add(de);
				
				de = new DataElement();
				de.setType(DataElementType.TEXT);
				de.setLabel(SoggettiCostanti.LABEL_PARAMETRO_SOGGETTO_DOMINIO);
				de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO+CostantiControlStation.PARAMETRO_SUFFIX_LABEL);
				String valueDom = dominio;
				String [] sdValues = SoggettiCostanti.getSoggettiDominiValue();
				String [] sdLabels = SoggettiCostanti.getSoggettiDominiLabel();
				for (int i = 0; i < sdValues.length; i++) {
					if(sdValues[i].equals(dominio)) {
						valueDom = sdLabels[i];
						break;
					}
				}
				de.setValue(valueDom);
			}
			else {
				de.setType(DataElementType.SELECT);
				de.setValues(SoggettiCostanti.getSoggettiDominiValue());
				de.setLabels(SoggettiCostanti.getSoggettiDominiLabel());
				de.setSelected(dominio);
				de.setPostBack(true);
			}
			
			dominioEsterno = SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE.equals(dominio);
			
			dati.add(de);
		}
		
		
		
		String oldtipoauth = null;
		String nomePdd = null;
		// se operazione change visualizzo i link per invocazione servizio,
		// risposta asincrona
		// e ruoli
		if (TipoOperazione.CHANGE.equals(tipoOperazione)) {

			if(sa==null) {
				sa = this.saCore.getServizioApplicativo(idSA);
			}
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

			// oldtipoauth
			if(sa.getInvocazionePorta()!=null && sa.getInvocazionePorta().sizeCredenzialiList()>0) {
				// prendo il primo
				CredenzialeTipo tipo = sa.getInvocazionePorta().getCredenziali(0).getTipo();
				if(tipo!=null) {
					oldtipoauth = tipo.getValue();
				}
				else {
					oldtipoauth = ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA;
				}
			}
			
			// soggetto proprietario
			de = new DataElement();
			if(multitenant && !this.isSoggettoMultitenantSelezionato()) {
				de.setType(DataElementType.TEXT);
			}
			else if(isSupportatoAutenticazioneApplicativiEsterni) {
				de.setType(DataElementType.TEXT);
			}
			else {
				de.setType(DataElementType.HIDDEN);
			}
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			de.setValue(tipoENomeSoggetto);
			dati.add(de);
			
			if(this.isModalitaCompleta()) {
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setValue(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_VISUALIZZA_DATI_PROVIDER);
				de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE,
						new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID, sa.getIdSoggetto()+""),
						new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_TIPO_SOGGETTO, tipoSoggetto),
						new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME_SOGGETTO, nomeSoggetto)
						);
				dati.add(de);
			}

			de = new DataElement();
			de.setType(DataElementType.HIDDEN);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);				
			de.setValue(provider);
			dati.add(de);
			
		}else{
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);				
			// Aggiunta di un servizio applicativo passando dal menu' 
			if(!useIdSogg){
				
				boolean visualizzaSoggetto = isSupportatoAutenticazioneApplicativiEsterni;
				if(visualizzaSoggetto) {
					if(!dominioEsterno) {
						visualizzaSoggetto = soggettiList!=null && soggettiList.length>1;
					}
				}
				
				if((multitenant && !this.isSoggettoMultitenantSelezionato()) || visualizzaSoggetto) {
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
				}
				else {
					de.setType(DataElementType.HIDDEN);
					de.setValue(provider);
				}
								
			} else {
				de.setType(DataElementType.HIDDEN);
				de.setValue(provider);
				dati.add(de);

				de = new DataElement();
				de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);

				// Aggiunta di un servizio applicativo passando dalla schermata soggetti
				org.openspcoop2.core.config.Soggetto sog = this.soggettiCore.getSoggetto(Integer.parseInt(provider));
				
				de.setType(DataElementType.TEXT);
				de.setValue(this.getLabelNomeSoggetto(tipoProtocollo, sog.getTipo(), sog.getNome()));
				de.setSize(this.getSize());

			}
			dati.add(de);

		}
		
		
		
				
		de = new DataElement();
		de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_NOME);
		de.setValue(nome);
		de.setSize(this.getSize());
		de.setType(DataElementType.TEXT_EDIT);
		de.setRequired(true);
		de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME);
		dati.add(de);
		
		
		
		de = new DataElement();
		de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_DESCRIZIONE);
		de.setValue(descrizione);
		de.setType(DataElementType.TEXT_AREA);
		de.setRows(2);
		de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_DESCRIZIONE);
		de.setSize( getSize());
		dati.add(de);
		
		
		
		

		boolean applicativiServerEnabled = this.saCore.isApplicativiServerEnabled(this);
		
		// Tipo SA
		de = new DataElement();
		de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_TIPO);
		de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_TIPO_SA);
		de.setSize(this.getSize());
		
		boolean applicativoServerSelezionabile = true;
		if(isSupportatoAutenticazioneApplicativiEsterni &&
			PddTipologia.ESTERNO.toString().equals(dominio)){
			applicativoServerSelezionabile = false;
			tipoSA = ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_CLIENT;
		}

		if(applicativiServerEnabled) {
			if(tipoOperazione.equals(TipoOperazione.ADD) && applicativoServerSelezionabile) {
				de.setSelected(tipoSA);
				de.setType(DataElementType.SELECT);
				de.setLabels(ServiziApplicativiCostanti.getLabelsServiziApplicativiTipo());
				de.setValues(ServiziApplicativiCostanti.getValuesServiziApplicativiTipo());
				de.setPostBack(true);
			} else {
				de.setValue(tipoSA);
				de.setType(DataElementType.HIDDEN);
				dati.add(de);
				
				de = new DataElement();
				de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_TIPO);
				de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_TIPO_SA+CostantiControlStation.PARAMETRO_SUFFIX_LABEL);
				de.setType(DataElementType.TEXT);
				de.setValue(this.getTipo(tipoSA, (applicativiServerEnabled && useAsClient)));
			}
		} else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(tipoSA);
		}
		dati.add(de);
		
		
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
//			dati.add(de);
//			
//			de = new DataElement();
//			de.setLabel(ServiziApplicativiCostanti.LABEL_EROGATORE);
//			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RUOLO_EROGATORE);
//			de.setPostBack(true);
//			de.setType(DataElementType.CHECKBOX);
//			if(!TipologiaErogazione.DISABILITATO.equals(ruoloErogatore)){
//				de.setSelected(true);
//			}
//			dati.add(de);
			
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_TIPOLOGIA);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RUOLO_SA);
			//de.setPostBack(true);
			//de.setType(DataElementType.SELECT);
			//de.setValues(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_RUOLO);
			
			// forzo HIDDEN sempre a meno che non e' in modalita' completa
			de.setType(DataElementType.HIDDEN);
			
			if(!TipologiaFruizione.DISABILITATO.equals(ruoloFruitore) && !TipologiaErogazione.DISABILITATO.equals(ruoloErogatore)){
				if(!applicativiServerEnabled) {
					de.setLabel(null);
					de.setValue(CostantiControlStation.LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_SHORT_MESSAGE);
					de.setType(DataElementType.TEXT);
					de.setPostBack(false);
					
					configurazioneStandardNonApplicabile = true;
				}
			}
			else if(!TipologiaFruizione.DISABILITATO.equals(ruoloFruitore)){
				de.setValue(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_RUOLO_FRUITORE);
				if(!applicativiServerEnabled) {
					if(tipoauth==null || tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA)){
						tipoauth = this.saCore.getAutenticazione_generazioneAutomaticaPorteDelegate();
					} 
				} else {
					if(ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_CLIENT.equals(tipoSA)) {
						if(tipoauth==null || tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA)){
							tipoauth = this.saCore.getAutenticazione_generazioneAutomaticaPorteDelegate();
						} 
					}
				}
			}
			else if(!TipologiaErogazione.DISABILITATO.equals(ruoloErogatore)){
				de.setValue(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_RUOLO_EROGATORE);
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
					
					// label del tasto invia
					if(tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL) && StringUtils.isNotEmpty(tipoCredenzialiSSLStatoElaborazioneCertificato) && ( tipoCredenzialiSSLStatoElaborazioneCertificato.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CARICA_CERTIFICATO)
							||tipoCredenzialiSSLStatoElaborazioneCertificato.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_PASSWORD_NON_PRESENTE) 
							|| tipoCredenzialiSSLStatoElaborazioneCertificato.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_ERRORE_LETTURA_CERTIFICATO)
							)) {
						if(tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO)) {
							this.pd.setLabelBottoneInvia(ConnettoriCostanti.LABEL_BUTTON_INVIA_CARICA_CERTIFICATO);
						}
					}
					
				} 
				de.setType(DataElementType.HIDDEN);
				
			}
						
			dati.add(de);
			
		}// fine !modalitàCompleta
		
		// Link Proprieta
		
		if(tipoOperazione.equals(TipoOperazione.CHANGE)) {
			de = new DataElement();
			de.setType(DataElementType.LINK);
			
			List<Parameter> parametersServletSAChange = new ArrayList<>();
			Parameter pIdSA = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID, sa.getId()+"");
			parametersServletSAChange.add(pIdSA);
			int idProv = sa.getIdSoggetto().intValue();
			Parameter pIdSoggettoSA = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, idProv+"");
			parametersServletSAChange.add(pIdSoggettoSA);
			if(dominio != null) {
				Parameter pDominio = new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO, dominio);
				parametersServletSAChange.add(pDominio);
			}
			
			de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_PROPRIETA_LIST, parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()]));
			if (contaListe) {
				de.setValue(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROPRIETA+"(" + sa.sizeProprietaList() + ")");
			} else {
				de.setValue(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROPRIETA);
			}
			
			dati.add(de);
		}
		
		
		boolean showLinkInvocazioneServizio = false;
		boolean showLinkRispostaAsincrona = false;
		boolean supportAsincroni = false;
		if (TipoOperazione.CHANGE.equals(tipoOperazione)) {
			if (this.isModalitaCompleta() && !this.pddCore.isPddEsterna(nomePdd)) {
				showLinkInvocazioneServizio = true;
				showLinkRispostaAsincrona = true;
			}
			else {
				if(!this.pddCore.isPddEsterna(nomePdd)){
					List<ServiceBinding> serviceBindingListProtocollo = this.core.getServiceBindingListProtocollo(nomeProtocollo);
					for (ServiceBinding serviceBinding : serviceBindingListProtocollo) {
						supportAsincroni = this.core.isProfiloDiCollaborazioneSupportatoDalProtocollo(nomeProtocollo,serviceBinding, ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO)
								|| this.core.isProfiloDiCollaborazioneSupportatoDalProtocollo(nomeProtocollo, serviceBinding, ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO);
						if(supportAsincroni) {
							break;
						}
					}
				}
			}
		}
		
		
		if(supportAsincroni) {
			RispostaAsincrona rispAsin = sa.getRispostaAsincrona();
			Connettore connettoreRis = rispAsin != null ? rispAsin.getConnettore() : null;
			StatoFunzionalita getMSGRisp = rispAsin != null ? rispAsin.getGetMessage() : null;

			boolean attualeConnettoreDisabilitato = (connettoreRis == null || TipiConnettore.DISABILITATO.getNome().equals(connettoreRis.getTipo())) && CostantiConfigurazione.DISABILITATO.equals(getMSGRisp);
			
			if(!attualeConnettoreDisabilitato || !this.isModalitaStandard()) {
			
				de = new DataElement();
				de.setType(DataElementType.LINK);
				de.setLabel(ServiziApplicativiCostanti.LABEL_RISPOSTA_ASINCRONA);
				if(this.pddCore.isPddEsterna(nomePdd)){
					de.setType(DataElementType.TEXT);
					de.setValue("(non presente)");
				}
				else{
					if (attualeConnettoreDisabilitato) {
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
	
				dati.add(de);
				
			}
		}
		
		
		
		boolean showFruitore = false;
		boolean showErogatore = false;
		
		boolean connettoreErogatoreForceEnabled = false;
		if(applicativiServerEnabled) {
			showFruitore = ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_CLIENT.equals(tipoSA);
			showErogatore = ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(tipoSA);
			
			if(
					//va visualizzato comunque se già configurato: !this.isModalitaStandard() && 
					getmsg!=null && CostantiConfigurazione.ABILITATO.toString().equals(getmsg)) {
				connettoreErogatoreForceEnabled = false;
			}
			else {
				connettoreErogatoreForceEnabled = true;
				if(TipiConnettore.DISABILITATO.getNome().equals(endpointtype)) {
					endpointtype = TipiConnettore.HTTP.getNome();
				}
			}
		}
		else {
			showFruitore = !TipologiaFruizione.DISABILITATO.equals(ruoloFruitore);
			showErogatore = this.isModalitaStandard() && !TipologiaErogazione.DISABILITATO.equals(ruoloErogatore);
		}
		
		boolean postBackViaPost = false;
		if(showErogatore) {
			postBackViaPost = true;
		}
		
		
		
		
		
		// ************ FRUITORE ********************
		
		if (this.isModalitaCompleta() ||  showFruitore) {
				
//			if(this.isModalitaStandard()){
//				de = new DataElement();
//				de.setLabel(ServiziApplicativiCostanti.LABEL_FRUITORE);
//				de.setType(DataElementType.TITLE);
//				dati.add(de);
//				
//				de = new DataElement();
//				de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO + " '" + nomeProtocollo + "' Risposta");
//				de.setType(DataElementType.SELECT);
//				de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RISPOSTA);
//				de.setValues(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_SBUSTAMENTO_PROTOCOLLO);
//				de.setSelected(sbustamentoInformazioniProtocolloRisposta);
//				dati.add(de);
//			}
//			else{
//				de = new DataElement();
//				de.setLabel(ServiziApplicativiCostanti.LABEL_CREDENZIALI_ACCESSO_PORTA);
//				de.setType(DataElementType.TITLE);
//				dati.add(de);
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
			boolean showLabelCredenzialiAccesso = true;
			boolean visualizzaTipoAutenticazione = true;
			String titleConfigSslCredenziali = null;
			String subtitleConfigSslCredenziali = null;
			boolean dominioEsternoProfiloModIPA = dominioEsterno && this.isProfiloModIPA(nomeProtocollo);
			if(dominioEsternoProfiloModIPA) {
				
				visualizzaTipoAutenticazione = false;
				boolean trovato = false;
				for (String tipoAuthModIEsterno: ConnettoriCostanti.CREDENZIALI_MODI_ESTERNO_VALUES) {
					if(tipoAuthModIEsterno.equals(tipoauth)) {
						trovato = true;
					}
				}
				if(!trovato && !ConnettoriCostanti.AUTENTICAZIONE_TIPO_TOKEN.equals(tipoauth)) {
					tipoauth = ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL;
				}
				
				String label = CostantiLabel.MODIPA_PROTOCOL_LABEL;
				if(label!=null && !"".equals(label)) {
					showLabelCredenzialiAccesso = false;
					titleConfigSslCredenziali = label;
				}
				
				subtitleConfigSslCredenziali = CostantiLabel.MODIPA_SICUREZZA_MESSAGGIO_SUBTITLE_LABEL;
			}
			
			if(!dominioEsternoProfiloModIPA) {
				dati = this.addCredenzialiToDati(tipoOperazione, dati, tipoauth, oldtipoauth, utente, password, subject, principal, servlet, showLabelCredenzialiAccesso, null, false, visualizzaTipoAutenticazione, null, true,
						tipoCredenzialiSSLSorgente, tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato, tipoCredenzialiSSLFileCertificatoPassword,listaAliasEstrattiCertificato, 
						tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLAliasCertificatoSubject, tipoCredenzialiSSLAliasCertificatoIssuer,
						tipoCredenzialiSSLAliasCertificatoType, tipoCredenzialiSSLAliasCertificatoVersion, tipoCredenzialiSSLAliasCertificatoSerialNumber, 
						tipoCredenzialiSSLAliasCertificatoSelfSigned, tipoCredenzialiSSLAliasCertificatoNotBefore, tipoCredenzialiSSLAliasCertificatoNotAfter, 
						tipoCredenzialiSSLVerificaTuttiICampi, tipoCredenzialiSSLConfigurazioneManualeSelfSigned, issuer, tipoCredenzialiSSLStatoElaborazioneCertificato,
						changepwd,
						multipleApiKey, appId, apiKey,
						subtitleConfigSslCredenziali, visualizzaModificaCertificato, visualizzaAddCertificato, servletCredenzialiList, parametersServletCredenzialiList, numeroCertificati, servletCredenzialiAdd,
						true, tokenPolicySA, tokenClientIdSA, tokenWithHttpsEnabledByConfigSA,
						dominioEsterno, tipoProtocollo,
						postBackViaPost);
			}
			else {
				// aggiungo dopo il link sui ruoli
			}
			
			
			if (TipoOperazione.CHANGE.equals(tipoOperazione)) {
			
				de = new DataElement();
				de.setLabel(RuoliCostanti.LABEL_RUOLI);
				de.setType(DataElementType.TITLE);
				dati.add(de);
				
				de = new DataElement();
				de.setType(DataElementType.LINK);
				if(useIdSogg){
					de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_RUOLI_LIST,
							new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO,sa.getId()+""),
							new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,sa.getIdSoggetto()+""));
				}
				else{
					if(this.isModalitaCompleta()) {
						de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_RUOLI_LIST,
								new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO,sa.getId()+""));	
					}
					else {
						// Imposto Accesso da Change!
						de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_RUOLI_LIST,
								new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO,sa.getId()+""),
								new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_ACCESSO_DA_CHANGE,Costanti.CHECK_BOX_ENABLED));
					}
				}
				if (contaListe) {
					// BugFix OP-674
					//List<String> lista1 = this.saCore.servizioApplicativoRuoliList(sa.getId(),new Search(true));
					ConsoleSearch searchForCount = new ConsoleSearch(true,1);
					this.saCore.servizioApplicativoRuoliList(sa.getId(),searchForCount);
					//int numRuoli = lista1.size();
					int numRuoli = searchForCount.getNumEntries(Liste.SERVIZIO_APPLICATIVO_RUOLI);
					ServletUtils.setDataElementVisualizzaLabel(de,(long)numRuoli);
				} else
					ServletUtils.setDataElementVisualizzaLabel(de);
				dati.add(de);
				
			}
			
			if(dominioEsternoProfiloModIPA) {
				
				if(showLabelCredenzialiAccesso == false) {
					de = new DataElement();
					de.setLabel(titleConfigSslCredenziali);
					de.setType(DataElementType.TITLE);
					dati.add(de);
				}
				
				dati = this.addCredenzialiToDati(tipoOperazione, dati, tipoauth, oldtipoauth, utente, password, subject, principal, servlet, showLabelCredenzialiAccesso, null, false, visualizzaTipoAutenticazione, null, true,
						tipoCredenzialiSSLSorgente, tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato, tipoCredenzialiSSLFileCertificatoPassword,listaAliasEstrattiCertificato, 
						tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLAliasCertificatoSubject, tipoCredenzialiSSLAliasCertificatoIssuer,
						tipoCredenzialiSSLAliasCertificatoType, tipoCredenzialiSSLAliasCertificatoVersion, tipoCredenzialiSSLAliasCertificatoSerialNumber, 
						tipoCredenzialiSSLAliasCertificatoSelfSigned, tipoCredenzialiSSLAliasCertificatoNotBefore, tipoCredenzialiSSLAliasCertificatoNotAfter, 
						tipoCredenzialiSSLVerificaTuttiICampi, tipoCredenzialiSSLConfigurazioneManualeSelfSigned, issuer, tipoCredenzialiSSLStatoElaborazioneCertificato,
						changepwd,
						multipleApiKey, appId, apiKey,
						subtitleConfigSslCredenziali, visualizzaModificaCertificato, visualizzaAddCertificato, servletCredenzialiList, parametersServletCredenzialiList, numeroCertificati, servletCredenzialiAdd,
						true, tokenPolicySA, tokenClientIdSA, tokenWithHttpsEnabledByConfigSA,
						dominioEsterno, tipoProtocollo,
						postBackViaPost);
			}
			
		}
		else {
	
			// Devo far vedere il link dei ruoli anche se il server è utilizzabile come client
			
			if (TipoOperazione.CHANGE.equals(tipoOperazione) && applicativiServerEnabled && ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(tipoSA) && useAsClient) {
				
				de = new DataElement();
				de.setLabel(RuoliCostanti.LABEL_RUOLI);
				de.setType(DataElementType.TITLE);
				dati.add(de);
				
				de = new DataElement();
				de.setType(DataElementType.LINK);
				if(useIdSogg){
					de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_RUOLI_LIST,
							new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO,sa.getId()+""),
							new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,sa.getIdSoggetto()+""));
				}
				else{
					if(this.isModalitaCompleta()) {
						de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_RUOLI_LIST,
								new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO,sa.getId()+""));	
					}
					else {
						// Imposto Accesso da Change!
						de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_RUOLI_LIST,
								new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO,sa.getId()+""),
								new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_ACCESSO_DA_CHANGE,Costanti.CHECK_BOX_ENABLED));
					}
				}
				if (contaListe) {
					// BugFix OP-674
					//List<String> lista1 = this.saCore.servizioApplicativoRuoliList(sa.getId(),new Search(true));
					ConsoleSearch searchForCount = new ConsoleSearch(true,1);
					this.saCore.servizioApplicativoRuoliList(sa.getId(),searchForCount);
					//int numRuoli = lista1.size();
					int numRuoli = searchForCount.getNumEntries(Liste.SERVIZIO_APPLICATIVO_RUOLI);
					ServletUtils.setDataElementVisualizzaLabel(de,(long)numRuoli);
				} else
					ServletUtils.setDataElementVisualizzaLabel(de);
				dati.add(de);
				
			}
			
		}


		boolean avanzatoFruitore = this.isModalitaAvanzata() &&
				!TipologiaFruizione.DISABILITATO.equals(ruoloFruitore);
		
		boolean faultChoice = avanzatoFruitore && config.isSupportoSceltaFault();
		if (faultChoice) {
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_ERRORE_APPLICATIVO);
			de.setType(DataElementType.TITLE);
			dati.add(de);
		}

		if (TipoOperazione.ADD.equals(tipoOperazione)) {
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_FAULT);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_FAULT);
			if (faultChoice) {
				de.setType(DataElementType.SELECT);
				de.setValues(ServiziApplicativiCostanti.getServiziApplicativiFault());
				de.setSelected(fault);
			}
			else{
				de.setType(DataElementType.HIDDEN);
				de.setValue(fault);
			}
			dati.add(de);
		}
		else{
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_FAULT);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_FAULT);
			if (faultChoice) {
				de.setType(DataElementType.SELECT);
				de.setValues(ServiziApplicativiCostanti.getServiziApplicativiFault());
				de.setSelected(fault);
				if(postBackViaPost) {
					de.setPostBack_viaPOST(true);
				}
				else {
					de.setPostBack(true);
				}
			}
			else{
				de.setType(DataElementType.HIDDEN);
				de.setValue(fault);
			}
			dati.add(de);

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
				dati.add(de);
			}


			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_FAULT_GENERIC_CODE);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_FAULT_GENERIC_CODE);
			if (faultChoice) {
				de.setType(DataElementType.SELECT);
				de.setValues(ServiziApplicativiCostanti.getServiziApplicativiFaultGenericCode());
				de.setSelected(genericfault);
			}
			else{
				de.setType(DataElementType.HIDDEN);
				de.setValue(genericfault);
			}
			dati.add(de);

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
			dati.add(de);
		}

		
		if( (this.isModalitaCompleta() && !TipoOperazione.ADD.equals(tipoOperazione))  || 
				(avanzatoFruitore && config.isSupportoSbustamentoProtocollo()) ) {
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_TRATTAMENTO_MESSAGGIO);
			de.setType(DataElementType.TITLE);
			dati.add(de);
		}
		
		
		de = new DataElement();
		de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RISPOSTA);
		de.setLabel(ServiziApplicativiCostanti.getLabelSbustamentoProtocollo(nomeProtocollo));
		if(avanzatoFruitore && config.isSupportoSbustamentoProtocollo()){
			de.setType(DataElementType.SELECT);
			de.setValues(ServiziApplicativiCostanti.getServiziApplicativiSbustamentoProtocollo());
			de.setSelected(sbustamentoInformazioniProtocolloRisposta);
		}
		else {
			de.setType(DataElementType.HIDDEN);
			de.setValue(sbustamentoInformazioniProtocolloRisposta);
		}
		dati.add(de);
			
					

		if (this.isModalitaCompleta()==false) {
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO);
			de.setType(DataElementType.HIDDEN);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_RISPOSTA);
			de.setValue(invrif == null || "".equals(invrif) ? CostantiConfigurazione.DISABILITATO.toString() : invrif);
			dati.add(de);
		} else {
			if (!TipoOperazione.ADD.equals(tipoOperazione)) {
				de = new DataElement();
				de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO);
				de.setType(DataElementType.SELECT);
				de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_RISPOSTA);
				de.setValues(ServiziApplicativiCostanti.getServiziApplicativiInvioPerRiferimento());
				de.setSelected(invrif);
				dati.add(de);
			}else{
				de = new DataElement();
				de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO);
				de.setType(DataElementType.HIDDEN);
				de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_RISPOSTA);
				de.setValue(invrif == null || "".equals(invrif) ? ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_DISABILITATO : invrif);
				dati.add(de);
			}
		}



		// se operazione change visualizzo i link per invocazione servizio,
		// risposta asincrona
		
		if (showLinkInvocazioneServizio || showLinkRispostaAsincrona) {

			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_INFO_INTEGRAZIONE);
			de.setType(DataElementType.TITLE);
			dati.add(de);
			
			// invocazione servizio
			if(showLinkInvocazioneServizio) {
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
				dati.add(de);
			}

			// risposta asincrona
			if(showLinkRispostaAsincrona) {
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
	
				dati.add(de);
			}

		}

		
		
		
		
		
		
		
		
		
		
		
		// ************ EROGATORE ********************
		
		if(showErogatore){
			
			boolean servizioApplicativoServerEnabled = false;
			
			if (utente == null) {
				utente = "";
			}
			if (password == null) {
				password = "";
			}
					
			this.addEndPointToDati(dati,id,nome,sbustamento,sbustamentoInformazioniProtocolloRichiesta,
					getmsg, utente, password, true, invrif,risprif,nomeProtocollo,false,true, true,
					parentSA,null,null,servizioApplicativoServerEnabled,
					tipoSA, useAsClient,
					integrationManagerEnabled,
					tipoOperazione, tipoCredenzialiSSLVerificaTuttiICampi, changepwd, 
					postBackViaPost);
			
			if(!applicativiServerEnabled && TipologiaFruizione.DISABILITATO.equals(ruoloFruitore) &&
					CostantiConfigurazione.ABILITATO.equals(getmsg)){
				
				if(tipoauth==null || tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA)){
					tipoauth = this.saCore.getAutenticazione_generazioneAutomaticaPorteDelegate();
				} 
					
				// Credenziali di accesso
//				if (utente == null) {
//					utente = "";
//				}
//				if (password == null) {
//					password = "";
//				}
				if (subject == null) {
					subject = "";
				}
				if (principal == null) {
					principal = "";
				}
				dati = this.addCredenzialiToDati(tipoOperazione, dati, tipoauth, oldtipoauth, utente, password, subject, principal, servlet, true, null, false, true, null, true,
						tipoCredenzialiSSLSorgente, tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato, tipoCredenzialiSSLFileCertificatoPassword, listaAliasEstrattiCertificato, 
						tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLAliasCertificatoSubject, tipoCredenzialiSSLAliasCertificatoIssuer,
						tipoCredenzialiSSLAliasCertificatoType, tipoCredenzialiSSLAliasCertificatoVersion, tipoCredenzialiSSLAliasCertificatoSerialNumber, 
						tipoCredenzialiSSLAliasCertificatoSelfSigned, tipoCredenzialiSSLAliasCertificatoNotBefore, tipoCredenzialiSSLAliasCertificatoNotAfter, 
						tipoCredenzialiSSLVerificaTuttiICampi, tipoCredenzialiSSLConfigurazioneManualeSelfSigned, issuer, tipoCredenzialiSSLStatoElaborazioneCertificato,
						changepwd,
						multipleApiKey, appId, apiKey,
						visualizzaModificaCertificato, visualizzaAddCertificato, servletCredenzialiList, parametersServletCredenzialiList, numeroCertificati, servletCredenzialiAdd,
						true, tokenPolicySA, tokenClientIdSA, tokenWithHttpsEnabledByConfigSA,
						dominioEsterno, tipoProtocollo,
						postBackViaPost
						);
				
			}
			
			dati = this.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, "",//ServiziApplicativiCostanti.LABEL_EROGATORE+" ",
					url, nomeCodaJMS,
					tipoCodaJMS, userRichiesta, passwordRichiesta, initcont, urlpgk, provurl,
					connfact, sendas, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, TipoOperazione.CHANGE, 
					httpsurl, httpstipologia, httpshostverify, 
					httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
					httpsalgoritmo, httpsstato, httpskeystore,
					httpspwdprivatekeytrust, httpspathkey,
					httpstipokey, httpspwdkey, 
					httpspwdprivatekey,	httpsalgoritmokey, 
					httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
					tipoconn, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT,
					nome, id, null, null, null, null,
					null, null, true,
					isConnettoreCustomUltimaImmagineSalvata, 
					proxyEnabled, proxyHost, proxyPort, proxyUsername, proxyPassword,
					tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
					opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
					requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
					requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
					responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
					autenticazioneToken, tokenPolicy, false, false,
					listExtendedConnettore, connettoreErogatoreForceEnabled,
					nomeProtocollo, false, false
					, false, servizioApplicativoServerEnabled, null, null,
					autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
					postBackViaPost
					);
		}
		
		
		
		
		
		
		if(configurazioneStandardNonApplicabile){
			this.pd.setMessage(CostantiControlStation.LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_LONG_MESSAGE,Costanti.MESSAGE_TYPE_INFO);
			this.pd.disableEditMode();
			
			for (int i = 0; i < dati.size(); i++) {
				DataElement deCheck = dati.get(i);
				if(ConnettoriCostanti.PARAMETRO_CONNETTORE_ABILITA_USO_APPLICATIVO_SERVER.equals(deCheck.getName())) {
					dati.remove(i);
					break;
				}
			}
		}
		
		
		return dati;
	}


	public boolean servizioApplicativoCheckData(TipoOperazione tipoOperazione, String[] soggettiList, long idProvOld,
			String ruoloFruitoreParam, String ruoloErogatore, List<ExtendedConnettore> listExtendedConnettore,
			ServizioApplicativo saOld, StringBuilder sbWarningChange)
			throws Exception {
		try {
			
			if(ruoloFruitoreParam!=null) {
				// nop
			}
			
			String tipoSA = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_TIPO_SA);
			boolean isApplicativiServerEnabled = this.saCore.isApplicativiServerEnabled(this);
			
			boolean useAsClient = false;
			if(isApplicativiServerEnabled &&
				tipoSA.equals(ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER)) {
				String tmp = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_UTILIZZABILE_COME_CLIENT);
				useAsClient = ServletUtils.isCheckBoxEnabled(tmp);
			}
			
			if(ruoloErogatore==null){
				ruoloErogatore = TipologiaErogazione.DISABILITATO.getValue();
			}
			
			String nome = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME);
			String provider = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			String descrizione = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_DESCRIZIONE);
			int newProv = 0;
			if (provider == null) {
				provider = "";
			} else {
				newProv = Integer.parseInt(provider);
			}
			String fault = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_FAULT);
/**			String tipoauth = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SA);
//			if (tipoauth == null) {
//				tipoauth = ServiziApplicativiCostanti.DEFAULT_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE;
//			}
//			String utente = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_USERNAME_SA);
//			String password = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_PASSWORD_SA);
//			// String confpw = this.getParameter("confpw");
//			String subject = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_SUBJECT_SA);*/

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
				this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_PREFISSO_DATI_INCOMPLETI_NECESSARIO_INDICARE + tmpElenco);
				return false;
			}
			
			// descrizione
			if( (descrizione!=null && StringUtils.isNotEmpty(descrizione)) 
				    &&
				(!this.checkLength4000(descrizione, ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_DESCRIZIONE)) 
				){
				return false;
			}
			
			// Controllo che non ci siano spazi nei campi di testo
			if (nome.indexOf(" ") != -1 || nome.indexOf('\"') != -1) {
				this.pd.setMessage("Non inserire spazi o doppi apici nei campi di testo");
				return false;
			}
			if(this.checkIntegrationEntityName(nome, ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_NOME)==false){
				return false;
			}
			if(this.checkLength255(nome, ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_NOME)==false) {
				return false;
			}
			
			
/**			if (tipoauth.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_BASIC) && ((utente.indexOf(" ") != -1) || (password.indexOf(" ") != -1))) {
//				this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_NEI_CAMPI_DI_TESTO);
//				return false;
//			}*/
			/**if (tipoauth.equals("ssl") && (subject.indexOf(" ") != -1)) {
						this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_NEI_CAMPI_DI_TESTO);
						return false;
					}*/

			// Controllo che i campi DataElementType.SELECT abbiano uno dei valori ammessi
			if (!fault.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_FAULT_SOAP) && !fault.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_FAULT_XML)) {
				this.pd.setMessage("Modalit&agrave; di fault dev'essere "+
						ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_FAULT_SOAP+" o "+ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_FAULT_XML);
				return false;
			}
/**			if (!tipoauth.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_BASIC) && 
//					!tipoauth.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SSL) && 
//					!tipoauth.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_NESSUNA)) {
//				this.pd.setMessage("Tipo dev'essere "+
//						ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_BASIC+", "+
//						ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SSL+" o "+
//						ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_NESSUNA);
//				return false;
//			}*/

			// Controllo che le password corrispondano
			/**
			 * if (tipoauth.equals("basic") && !password.equals(confpw)) {
			 * this.pd.setMessage("Le password non corrispondono"); return
			 * false; }
			 */

			// Controllo che il provider appartenga alla lista di
			// providers disponibili
			if (tipoOperazione.equals(TipoOperazione.ADD)) {
				boolean trovatoProv = false;
				if(soggettiList!=null) {
					for (int i = 0; i < soggettiList.length; i++) {
						String tmpSogg = soggettiList[i];
						if (tmpSogg.equals(provider)) {
							trovatoProv = true;
						}
					}
				}
				if (!trovatoProv) {
					this.pd.setMessage("Il soggetto dev'essere scelto tra quelli definiti nel pannello Soggetti");
					return false;
				}
			}
			
			if (tipoOperazione.equals(TipoOperazione.CHANGE)) {
				if(isApplicativiServerEnabled && ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(tipoSA) && !useAsClient && 
						saOld.getInvocazionePorta()!=null && saOld.getInvocazionePorta().getRuoli()!=null && saOld.getInvocazionePorta().getRuoli().sizeRuoloList()>0) {
					this.pd.setMessage("Prima di disabilitare l'opzione '"+ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_UTILIZZABILE_COME_CLIENT+"' devono essere rimossi i ruoli assegnati all'applicativo");
					return false;
				}
			}

			IDSoggetto ids = null;
			String portaDominioSoggetto = null;
			if(this.core.isRegistroServiziLocale()){
				Soggetto mySogg = this.soggettiCore.getSoggettoRegistro(newProv);
				ids = new IDSoggetto(mySogg.getTipo(), mySogg.getNome());
				portaDominioSoggetto = mySogg.getPortaDominio();
			}else{
				org.openspcoop2.core.config.Soggetto mySogg = this.soggettiCore.getSoggetto(newProv);
				ids = new IDSoggetto(mySogg.getTipo(), mySogg.getNome());
			}
			IDServizioApplicativo idSA = new IDServizioApplicativo();
			idSA.setIdSoggettoProprietario(ids);
			idSA.setNome(nome);
			String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(idSA.getIdSoggettoProprietario().getTipo());
			
			boolean dominioEsternoModI = this.isProfiloModIPA(protocollo) && this.pddCore.isPddEsterna(portaDominioSoggetto);
			
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
			else if(tipoOperazione.equals(TipoOperazione.CHANGE)) {
				long idSa = 0;
				boolean giaRegistrato = this.saCore.existsServizioApplicativo(idSA);
				if (giaRegistrato) {
					ServizioApplicativo sa = this.saCore.getServizioApplicativo(idSA);
					idSa = sa.getId().longValue();
					if ( saOld==null || (saOld.getId().longValue() != idSa)) {
						this.pd.setMessage("Il Servizio Applicativo " + nome + " &egrave; gi&agrave; stato registrato per il soggetto scelto.");
						return false;
					}
				}
			}

			if (tipoOperazione.equals(TipoOperazione.CHANGE)) {
				
				String oldTipoAuth = null;
				String oldTokenPolicy = null;
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
					oldTokenPolicy = saOld.getInvocazionePorta().getCredenziali(0).getTokenPolicy();
				}
				
				String tipoauthAttuale = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE);
				String tipoauth = tipoauthAttuale;
				boolean tokenWithHttpsEnabledByConfigSA = ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL_E_TOKEN_PDND.equals(tipoauth) || ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL_E_TOKEN_OAUTH.equals(tipoauth);
				if(tokenWithHttpsEnabledByConfigSA) {
					tipoauth = ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL;
				}
				boolean tokenModiPDNDOauth = ConnettoriCostanti.AUTENTICAZIONE_TIPO_TOKEN_PDND.equals(tipoauth) || ConnettoriCostanti.AUTENTICAZIONE_TIPO_TOKEN_OAUTH.equals(tipoauth);
				if(tokenModiPDNDOauth) {
					tipoauth = ConnettoriCostanti.AUTENTICAZIONE_TIPO_TOKEN;
				}
				
				boolean changeTipoAuth = false;
				if(oldTipoAuth!=null && !oldTipoAuth.equals(tipoauth)) {
					changeTipoAuth=true;
				}
				boolean segnalaWarning = false;
				if(changeTipoAuth && oldTokenPolicy!=null) {
					
					boolean tokenWithHttsSupportato = false;
					boolean dominioEsterno = this.pddCore.isPddEsterna(portaDominioSoggetto);
					if(dominioEsterno && protocollo!=null) {
						ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
						tokenWithHttsSupportato = protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().isSupportatoAutenticazioneApplicativiHttpsConToken();
					}
					
					boolean tokenModIPDND = this.saCore.isPolicyGestioneTokenPDND(oldTokenPolicy);
					if(tokenWithHttsSupportato) {
						if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL.equals(oldTipoAuth)) {
							if(tokenModIPDND) {
								oldTipoAuth = ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL_E_TOKEN_PDND;
							}
							else {
								oldTipoAuth = ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL_E_TOKEN_OAUTH;
							}
						}
						else {
							if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_TOKEN.equals(oldTipoAuth)){
								if(tokenModIPDND) {
									oldTipoAuth = ConnettoriCostanti.AUTENTICAZIONE_TIPO_TOKEN_PDND;
								}
								else {
									oldTipoAuth = ConnettoriCostanti.AUTENTICAZIONE_TIPO_TOKEN_OAUTH;
								}
							}
						}
					}

					if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_TOKEN_PDND.equals(oldTipoAuth) && ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL_E_TOKEN_PDND.equals(tipoauthAttuale)) {
						// aggiungere il certificato deve essere possibile
						changeTipoAuth=false;
					}
					else if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL_E_TOKEN_PDND.equals(oldTipoAuth) && ConnettoriCostanti.AUTENTICAZIONE_TIPO_TOKEN_PDND.equals(tipoauthAttuale)) {
						// eliminare il certificato non andrebbe permesso se usato
						// questo vincolo viene rilasciato per consentirlo da console, 
						// tanto poi eventualmente il runtime lo segnala e una nuova aggiunta dell'applicativo non è concessa nell'erogazione che richiede il certificato.
						// mentre quello autorizzato andrà poi eliminato.
						segnalaWarning = true;
					}
					else if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_TOKEN_OAUTH.equals(oldTipoAuth) && ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL_E_TOKEN_OAUTH.equals(tipoauthAttuale)) {
						// aggiungere il certificato deve essere possibile
						changeTipoAuth=false;
					}
					else if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL_E_TOKEN_OAUTH.equals(oldTipoAuth) && ConnettoriCostanti.AUTENTICAZIONE_TIPO_TOKEN_OAUTH.equals(tipoauthAttuale)) {
						// eliminare il certificato non andrebbe permesso se usato
						// questo vincolo viene rilasciato per consentirlo da console, 
						// tanto poi eventualmente il runtime lo segnala e una nuova aggiunta dell'applicativo non è concessa nell'erogazione che richiede il certificato.
						// mentre quello autorizzato andrà poi eliminato.
						segnalaWarning = true;
					}
					
				}
				
				if(changeTipoAuth) {
					// controllo che non sia usato in qualche PD
					
					StringBuilder sbWarningDetails = new StringBuilder();
										
					FiltroRicercaPorteDelegate filtro = new FiltroRicercaPorteDelegate();
					filtro.setTipoSoggetto(idSA.getIdSoggettoProprietario().getTipo());
					filtro.setNomeSoggetto(idSA.getIdSoggettoProprietario().getNome());
					filtro.setNomeServizioApplicativo(idSA.getNome());
					List<IDPortaDelegata> list = this.porteDelegateCore.getAllIdPorteDelegate(filtro);
					if(list!=null && list.size()>0) {
						String msg = "l'applicativo viene utilizzato all'interno del controllo degli accessi (autorizzazione trasporto) di "+
								list.size()+" fruizion"+(list.size()>1?"i":"e");
						if(segnalaWarning) {
							if(sbWarningDetails.length()>0) {
								sbWarningDetails.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
							}
							sbWarningDetails.append("- "+msg);
						}
						else {
							this.pd.setMessage("Non &egrave; possibile modificare il tipo di credenziali poich&egrave; "+msg);
							return false;
						}
					}
					
					String tipoTrasporto = "trasporto";
					if(this.isProfiloModIPA(protocollo)) {
						tipoTrasporto = "messaggio";
					}
					
					FiltroRicercaPorteApplicative filtroPA = new FiltroRicercaPorteApplicative();
					IDServizioApplicativo idServizioApplicativoAutorizzato = new IDServizioApplicativo();
					idServizioApplicativoAutorizzato.setIdSoggettoProprietario(new IDSoggetto(idSA.getIdSoggettoProprietario().getTipo(), idSA.getIdSoggettoProprietario().getNome()));
					idServizioApplicativoAutorizzato.setNome(idSA.getNome());
					filtroPA.setIdServizioApplicativoAutorizzato(idServizioApplicativoAutorizzato);
					List<IDPortaApplicativa> listPA = this.porteApplicativeCore.getAllIdPorteApplicative(filtroPA);
					if(listPA!=null && listPA.size()>0) {
						String msg = "l'applicativo viene utilizzato all'interno del controllo degli accessi (autorizzazione "+tipoTrasporto+") di "+
								listPA.size()+" erogazion"+(listPA.size()>1?"i":"e");
						if(segnalaWarning) {
							if(sbWarningDetails.length()>0) {
								sbWarningDetails.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
							}
							sbWarningDetails.append("- "+msg);
						}
						else {
							this.pd.setMessage("Non &egrave; possibile modificare il tipo di credenziali poich&egrave; "+msg);
							return false;
						}
					}
					
					filtro = new FiltroRicercaPorteDelegate();
					filtro.setTipoSoggetto(idSA.getIdSoggettoProprietario().getTipo());
					filtro.setNomeSoggetto(idSA.getIdSoggettoProprietario().getNome());
					filtro.setNomeServizioApplicativoToken(idSA.getNome());
					list = this.porteDelegateCore.getAllIdPorteDelegate(filtro);
					if(list!=null && !list.isEmpty()) {
						String msg = "l'applicativo viene utilizzato all'interno del controllo degli accessi (autorizzazione token) di "+
								list.size()+" fruizion"+(list.size()>1?"i":"e");
						if(segnalaWarning) {
							if(sbWarningDetails.length()>0) {
								sbWarningDetails.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
							}
							sbWarningDetails.append("- "+msg);
						}
						else {
							this.pd.setMessage("Non &egrave; possibile modificare il tipo di credenziali poich&egrave; "+msg);
							return false;
						}
					}
					
					filtroPA = new FiltroRicercaPorteApplicative();
					filtroPA.setIdServizioApplicativoAutorizzatoToken(idServizioApplicativoAutorizzato);
					listPA = this.porteApplicativeCore.getAllIdPorteApplicative(filtroPA);
					if(listPA!=null && !listPA.isEmpty()) {
						String msg = "l'applicativo viene utilizzato all'interno del controllo degli accessi (autorizzazione token) di "+
								listPA.size()+" erogazion"+(listPA.size()>1?"i":"e");
						if(segnalaWarning) {
							if(sbWarningDetails.length()>0) {
								sbWarningDetails.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
							}
							sbWarningDetails.append("- "+msg);
						}
						else {
							this.pd.setMessage("Non &egrave; possibile modificare il tipo di credenziali poich&egrave; "+msg);
							return false;
						}
					}
					
					if(segnalaWarning && sbWarningDetails.length()>0) {
						sbWarningChange.append("Il certificato di firma dell'applicativo potrebbe essere richiesto per fruire di una API.");
						sbWarningChange.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
						sbWarningChange.append("Verificare le configurazioni dove risulta associato l'applicativo:");
						sbWarningChange.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
						sbWarningChange.append(sbWarningDetails.toString());
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

			
			boolean oldPasswordCifrata = false;
			if(saOld!=null && saOld.getInvocazionePorta()!=null && saOld.getInvocazionePorta().sizeCredenzialiList()>0 && saOld.getInvocazionePorta().getCredenziali(0).isCertificateStrictVerification()) {
				oldPasswordCifrata = true;
			}
			boolean encryptEnabled = this.saCore.isApplicativiPasswordEncryptEnabled();
			if(this.credenzialiCheckData(tipoOperazione,oldPasswordCifrata, encryptEnabled, this.saCore.getApplicativiPasswordVerifier())==false){
				return false;
			}
			
			String tipoauth = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE);
			if (tipoauth == null) {
				tipoauth = ConnettoriCostanti.DEFAULT_AUTENTICAZIONE_TIPO;
			}
			String utente = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME);
			String password = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			String subject = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT);
			String issuer = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_ISSUER);
			if("".equals(issuer)) {
				issuer = null;
			}
			String principal = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL);

			String tokenPolicy = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_TOKEN_POLICY);
			String tokenClientId = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_TOKEN_CLIENT_ID);
			@SuppressWarnings("unused")
			boolean tokenByPDND = ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL_E_TOKEN_PDND.equals(tipoauth) || ConnettoriCostanti.AUTENTICAZIONE_TIPO_TOKEN_PDND.equals(tipoauth);
			boolean tokenWithHttpsEnabledByConfigSA = ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL_E_TOKEN_PDND.equals(tipoauth) || ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL_E_TOKEN_OAUTH.equals(tipoauth);
			if(tokenWithHttpsEnabledByConfigSA) {
				tipoauth = ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL;
			}
			boolean tokenModiPDNDOauth = ConnettoriCostanti.AUTENTICAZIONE_TIPO_TOKEN_PDND.equals(tipoauth) || ConnettoriCostanti.AUTENTICAZIONE_TIPO_TOKEN_OAUTH.equals(tipoauth);
			if(tokenModiPDNDOauth) {
				tipoauth = ConnettoriCostanti.AUTENTICAZIONE_TIPO_TOKEN;
			}
			
			// Se sono presenti credenziali, controllo che non siano gia'
			// utilizzate da altri soggetti
			if (tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC)) {
				// recupera lista servizi applicativi con stesse credenziali
				boolean checkPassword = this.saCore.isApplicativiCredenzialiBasicCheckUniqueUsePassword(); // la password non viene utilizzata per riconoscere se l'username e' già utilizzato.
				List<ServizioApplicativo> saList = this.saCore.servizioApplicativoWithCredenzialiBasicList(utente, password, checkPassword);

				String portaDominio = null;
				if(this.core.isRegistroServiziLocale()){
					Soggetto soggettoToCheck = tipoOperazione.equals(TipoOperazione.CHANGE) ? 
							this.soggettiCore.getSoggettoRegistro(idProvOld) : this.soggettiCore.getSoggettoRegistro(newProv);
							portaDominio = soggettoToCheck.getPortaDominio();
				}

				for (int i = 0; i < saList.size(); i++) {
					ServizioApplicativo sa = saList.get(i);

					/**String tipoNomeSoggetto = null;*/

					if(!this.core.isSinglePdD()){

						// bugfix #66
						// controllo se soggetto appartiene a nal diversi, in tal
						// caso e' possibile
						// avere stesse credenziali
						// Raccolgo informazioni soggetto
						Soggetto tmpSoggettoProprietarioSa = this.soggettiCore.getSoggettoRegistro(sa.getIdSoggetto());
						/**tipoNomeSoggetto = tmpSoggettoProprietarioSa.getTipo() + "/" + tmpSoggettoProprietarioSa.getNome();*/

						// se appartengono a nal diversi allora va bene continuo
						if (!portaDominio.equals(tmpSoggettoProprietarioSa.getPortaDominio()))
							continue;
					}
					else{

						/**org.openspcoop2.core.config.Soggetto tmpSoggettoProprietarioSa = this.soggettiCore.getSoggetto(sa.getIdSoggetto());
						//tipoNomeSoggetto = tmpSoggettoProprietarioSa.getTipo() + "/" + tmpSoggettoProprietarioSa.getNome();*/

					}

					if ((tipoOperazione.equals(TipoOperazione.CHANGE)) && (nome.equals(sa.getNome())) && (idProvOld == sa.getIdSoggetto())) {
						continue;
					}
					if(saOld!=null && tipoOperazione.equals(TipoOperazione.CHANGE) && saOld.getId().longValue() == sa.getId().longValue()) {
						continue;
					}

					// Messaggio di errore
					String labelSoggetto = this.getLabelNomeSoggetto(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
					if(sa.getTipo()!=null && StringUtils.isNotEmpty(sa.getTipo())) {
						this.pd.setMessage("L'applicativo "+sa.getNome()+" (soggetto: "+labelSoggetto+") possiede già l'utente (http-basic) indicato");
					}
					else {
						this.pd.setMessage("L'erogazione "+sa.getNome()+" possiede già l'utente (http-basic) indicato per il servizio '"+ServiziApplicativiCostanti.LABEL_SERVIZIO_MESSAGE_BOX+"'");
					}
					return false;
				}
				
				if(!this.soggettiCore.isSoggettiApplicativiCredenzialiBasicPermitSameCredentials()) {
					// Verifico soggetti
					
					boolean checkPasswordSoggetti = this.soggettiCore.isSoggettiCredenzialiBasicCheckUniqueUsePassword(); // la password non viene utilizzata per riconoscere se l'username e' già utilizzato.
					Soggetto soggettoAutenticato = this.soggettiCore.soggettoWithCredenzialiBasic(utente, password, checkPasswordSoggetti);
					
					// Messaggio di errore
					if(soggettoAutenticato!=null){
						String labelSoggettoAutenticato = this.getLabelNomeSoggetto(new IDSoggetto(soggettoAutenticato.getTipo(), soggettoAutenticato.getNome()));
						this.pd.setMessage("Il soggetto "+labelSoggettoAutenticato+" possiede già l'utente (http-basic) indicato");
						return false;
					}
				}
			}
			else if (tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY)) {
				// Univocita garantita dal meccanismo di generazione delle chiavi
				/**
				// Viene calcolato String appId = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID);
				String multipleApiKeys = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_MULTIPLE_API_KEYS);
				
				// recupera lista servizi applicativi con stesse credenziali
				boolean multipleApiKeysEnabled = ServletUtils.isCheckBoxEnabled(multipleApiKeys);
				String appId = this.saCore.toAppId(protocollo, idSA, multipleApiKeysEnabled);
				List<ServizioApplicativo> saList = this.saCore.servizioApplicativoWithCredenzialiApiKeyList(appId,multipleApiKeysEnabled);

				String portaDominio = null;
				if(this.core.isRegistroServiziLocale()){
					Soggetto soggettoToCheck = tipoOperazione.equals(TipoOperazione.CHANGE) ? 
							this.soggettiCore.getSoggettoRegistro(idProvOld) : this.soggettiCore.getSoggettoRegistro(newProv);
							portaDominio = soggettoToCheck.getPortaDominio();
				}

				for (int i = 0; i < saList.size(); i++) {
					ServizioApplicativo sa = saList.get(i);

					//String tipoNomeSoggetto = null;

					if(!this.core.isSinglePdD()){

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
					if(saOld!=null && tipoOperazione.equals(TipoOperazione.CHANGE) && saOld.getId().longValue() == sa.getId().longValue()) {
						continue;
					}

					// Messaggio di errore
					String labelSoggetto = this.getLabelNomeSoggetto(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
					String tipoCredenzialiApiKey = ServletUtils.isCheckBoxEnabled(multipleApiKeys) ? ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_MULTIPLE_API_KEYS_DESCR : ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_API_KEY;
					this.pd.setMessage("L'applicativo "+sa.getNome()+" (soggetto: "+labelSoggetto+") possiede già una credenziale '"+tipoCredenzialiApiKey+"' con identico '"+ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID+"'");

					return false;
				}
				*/
			}
			else if (tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL) &&
					!tokenWithHttpsEnabledByConfigSA // se e' abilitato il token non deve essere controllata l'univocita' del certificato
					) {
				String tipoCredenzialiSSLSorgente = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL);
				if(tipoCredenzialiSSLSorgente == null) {
					tipoCredenzialiSSLSorgente = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE;
				}
				String tipoCredenzialiSSLConfigurazioneManualeSelfSigned= this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_MANUALE_SELF_SIGNED);
				if (tipoCredenzialiSSLConfigurazioneManualeSelfSigned == null) {
					tipoCredenzialiSSLConfigurazioneManualeSelfSigned = Costanti.CHECK_BOX_ENABLED;
				}
				
				String details = "";
				List<ServizioApplicativo> saList = null;
				String tipoSsl = null;
				Certificate cSelezionato = null;
				boolean strictVerifier = false;
				
				
				
				if(tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE)) { 
					saList = this.saCore.servizioApplicativoWithCredenzialiSslList(subject,issuer,
							dominioEsternoModI ? ConfigurazioneFiltroServiziApplicativi.getFiltroApplicativiModIFirma() : ConfigurazioneFiltroServiziApplicativi.getFiltroApplicativiHttps());
					tipoSsl = "subject/issuer";
				}
				else {
					
					BinaryParameter tipoCredenzialiSSLFileCertificato = this.getBinaryParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO);
					String tipoCredenzialiSSLVerificaTuttiICampi = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI);
					strictVerifier = ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLVerificaTuttiICampi);
					String tipoCredenzialiSSLTipoArchivioS = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO);
					String tipoCredenzialiSSLFileCertificatoPassword = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_PASSWORD);
					String tipoCredenzialiSSLAliasCertificato = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO);
					if (tipoCredenzialiSSLAliasCertificato == null) {
						tipoCredenzialiSSLAliasCertificato = "";
					}
					org.openspcoop2.utils.certificate.ArchiveType tipoCredenzialiSSLTipoArchivio= null;
					if(tipoCredenzialiSSLTipoArchivioS == null) {
						tipoCredenzialiSSLTipoArchivio = org.openspcoop2.utils.certificate.ArchiveType.CER; 
					} else {
						tipoCredenzialiSSLTipoArchivio = org.openspcoop2.utils.certificate.ArchiveType.valueOf(tipoCredenzialiSSLTipoArchivioS);
					}
					
					byte [] archivio = tipoCredenzialiSSLFileCertificato.getValue();
					if(TipoOperazione.CHANGE.equals(tipoOperazione) && archivio==null) {
						archivio = saOld.getInvocazionePorta().getCredenziali(0).getCertificate();
					}
					if(tipoCredenzialiSSLTipoArchivio.equals(org.openspcoop2.utils.certificate.ArchiveType.CER)) {
						cSelezionato = ArchiveLoader.load(archivio);
					}else {
						cSelezionato = ArchiveLoader.load(tipoCredenzialiSSLTipoArchivio, archivio, tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLFileCertificatoPassword);
					}
					saList = this.saCore.servizioApplicativoWithCredenzialiSslList(cSelezionato.getCertificate(), strictVerifier,
							dominioEsternoModI ? ConfigurazioneFiltroServiziApplicativi.getFiltroApplicativiModIFirma() : ConfigurazioneFiltroServiziApplicativi.getFiltroApplicativiHttps());
					if(!strictVerifier && saList!=null && !saList.isEmpty()) {
						List<ServizioApplicativo> saListCheck = this.saCore.servizioApplicativoWithCredenzialiSslList(cSelezionato.getCertificate(), true,
								dominioEsternoModI ? ConfigurazioneFiltroServiziApplicativi.getFiltroApplicativiModIFirma() : ConfigurazioneFiltroServiziApplicativi.getFiltroApplicativiHttps());
						if(saListCheck==null || saListCheck.isEmpty() ) {
							details=ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_DETAILS;
						}
					}
					tipoSsl = "certificato";
					
				}
				
				String portaDominio = null;
				if(this.core.isRegistroServiziLocale()){
					Soggetto soggettoToCheck = tipoOperazione.equals(TipoOperazione.CHANGE) ? 
							this.soggettiCore.getSoggettoRegistro(idProvOld) : this.soggettiCore.getSoggettoRegistro(newProv);
							portaDominio = soggettoToCheck.getPortaDominio();
				}

				if(saList!=null) {
					for (int i = 0; i < saList.size(); i++) {
						ServizioApplicativo sa = saList.get(i);
	
						/**String tipoNomeSoggetto = null;*/
	
						if(!this.core.isSinglePdD()){
	
							// bugfix #66
							// controllo se soggetto appartiene a nal diversi, in tal
							// caso e' possibile
							// avere stesse credenziali
							// Raccolgo informazioni soggetto
							Soggetto tmpSoggettoProprietarioSa = this.soggettiCore.getSoggettoRegistro(sa.getIdSoggetto());
							/**tipoNomeSoggetto = tmpSoggettoProprietarioSa.getTipo() + "/" + tmpSoggettoProprietarioSa.getNome();*/
	
							// se appartengono a nal diversi allora va bene continuo
							if (!portaDominio.equals(tmpSoggettoProprietarioSa.getPortaDominio()))
								continue;
	
						}else{
	
							/**org.openspcoop2.core.config.Soggetto tmpSoggettoProprietarioSa = this.soggettiCore.getSoggetto(sa.getIdSoggetto());
							//tipoNomeSoggetto = tmpSoggettoProprietarioSa.getTipo() + "/" + tmpSoggettoProprietarioSa.getNome();*/
	
						}
	
						if ((tipoOperazione.equals(TipoOperazione.CHANGE)) && (nome.equals(sa.getNome())) && (idProvOld == sa.getIdSoggetto())) {
							continue;
						}
						if(saOld!=null && tipoOperazione.equals(TipoOperazione.CHANGE) && saOld.getId().longValue() == sa.getId().longValue()) {
							continue;
						}
	
						// Raccolgo informazioni soggetto
						// Messaggio di errore
						String labelSoggetto = this.getLabelNomeSoggetto(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
						this.pd.setMessage("L'applicativo "+sa.getNome()+" (soggetto: "+labelSoggetto+") possiede già le credenziali ssl ("+tipoSsl+") indicate."+details);
						return false;
					}
				}

				if(!tokenWithHttpsEnabledByConfigSA &&  // se e' abilitato il token non deve essere controllata l'univocita' del certificato)
					!this.soggettiCore.isSoggettiApplicativiCredenzialiSslPermitSameCredentials() &&
					!dominioEsternoModI
					) {
					// Verifico soggetti
				
					details = "";
					Soggetto soggettoAutenticato = null;
					if(tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE)) { 
				
						// recupero soggetto con stesse credenziali
						soggettoAutenticato = this.soggettiCore.getSoggettoRegistroAutenticatoSsl(subject, issuer);
						tipoSsl = "subject/issuer";
				
					} else {
						
						/**soggettoAutenticato = this.soggettiCore.getSoggettoRegistroAutenticatoSsl(cSelezionato.getCertificate(), strictVerifier);*/
						// Fix: usando il metodo sopra e' permesso caricare due soggetti con lo stesso certificato (anche serial number) uno in strict e uno no, e questo e' sbagliato.
						List<org.openspcoop2.core.registry.Soggetto> soggettiAutenticati = this.soggettiCore.soggettoWithCredenzialiSslList(cSelezionato.getCertificate(), strictVerifier);
						if(soggettiAutenticati!=null && !soggettiAutenticati.isEmpty()) {
							soggettoAutenticato = soggettiAutenticati.get(0);
							if(!strictVerifier) {
								List<org.openspcoop2.core.registry.Soggetto> soggettiAutenticatiCheck = this.soggettiCore.soggettoWithCredenzialiSslList(cSelezionato.getCertificate(), true);
								if(soggettiAutenticatiCheck==null || soggettiAutenticatiCheck.isEmpty() ) {
									details=ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_DETAILS;
								}
							}
						}
						tipoSsl = "certificato";
					}
					
					// Messaggio di errore
					if(soggettoAutenticato!=null){
						String labelSoggettoAutenticato = this.getLabelNomeSoggetto(new IDSoggetto(soggettoAutenticato.getTipo(), soggettoAutenticato.getNome()));
						this.pd.setMessage("Il soggetto "+labelSoggettoAutenticato+" possiede già le credenziali ssl ("+tipoSsl+") indicate."+details);
						return false;
					}
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

					/**String tipoNomeSoggetto = null;*/

					if(!this.core.isSinglePdD()){

						// bugfix #66
						// controllo se soggetto appartiene a nal diversi, in tal
						// caso e' possibile
						// avere stesse credenziali
						// Raccolgo informazioni soggetto
						Soggetto tmpSoggettoProprietarioSa = this.soggettiCore.getSoggettoRegistro(sa.getIdSoggetto());
						/**tipoNomeSoggetto = tmpSoggettoProprietarioSa.getTipo() + "/" + tmpSoggettoProprietarioSa.getNome();*/

						// se appartengono a nal diversi allora va bene continuo
						if (!portaDominio.equals(tmpSoggettoProprietarioSa.getPortaDominio()))
							continue;
					}
					else{

						/**org.openspcoop2.core.config.Soggetto tmpSoggettoProprietarioSa = this.soggettiCore.getSoggetto(sa.getIdSoggetto());
						//tipoNomeSoggetto = tmpSoggettoProprietarioSa.getTipo() + "/" + tmpSoggettoProprietarioSa.getNome();*/

					}

					if ((tipoOperazione.equals(TipoOperazione.CHANGE)) && (nome.equals(sa.getNome())) && (idProvOld == sa.getIdSoggetto())) {
						continue;
					}
					if(saOld!=null && tipoOperazione.equals(TipoOperazione.CHANGE) && saOld.getId().longValue() == sa.getId().longValue()) {
						continue;
					}

					// Messaggio di errore
					String labelSoggetto = this.getLabelNomeSoggetto(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
					this.pd.setMessage("L'applicativo "+sa.getNome()+" (soggetto: "+labelSoggetto+") possiede già il principal indicato");
					return false;
				}
				
				
				if(!this.soggettiCore.isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentials()) {
					// Verifico applicativi
					
					Soggetto soggettoAutenticato = this.soggettiCore.getSoggettoRegistroAutenticatoPrincipal(principal);
					
					// Messaggio di errore
					if(soggettoAutenticato!=null){
						String labelSoggettoAutenticato = this.getLabelNomeSoggetto(new IDSoggetto(soggettoAutenticato.getTipo(), soggettoAutenticato.getNome()));
						this.pd.setMessage("Il soggetto "+labelSoggettoAutenticato+" possiede già il principal indicato");
						return false;
					}
				}
				
			} 
			//else per gestire la possibilita' con https 
			if (ConnettoriCostanti.AUTENTICAZIONE_TIPO_TOKEN.equals(tipoauth) || tokenWithHttpsEnabledByConfigSA) {
				
				if(tokenPolicy==null || StringUtils.isEmpty(tokenPolicy) || CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO.equals(tokenPolicy)) {
					this.pd.setMessage("TokenPolicy non indicata");
				}
				if(ControlStationCore.isAPIMode()) {
					// Altrimenti la select list è valorizzata con quelli esistenti
					GenericProperties gp = null;
					try {
						gp = this.confCore.getGenericProperties(tokenPolicy, org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA, false);
					}catch(DriverConfigurazioneNotFound notFound) {}
					if(gp==null) {
						this.pd.setMessage("TokenPolicy indicata '"+tokenPolicy+"' non esiste");
						return false;
					}
				}
				
				// recupera lista servizi applicativi con stesse credenziali
				List<ServizioApplicativo> saList = this.saCore.servizioApplicativoWithCredenzialiTokenList(tokenPolicy, tokenClientId);

				String portaDominio = null;
				if(this.core.isRegistroServiziLocale()){
					Soggetto soggettoToCheck = tipoOperazione.equals(TipoOperazione.CHANGE) ? 
							this.soggettiCore.getSoggettoRegistro(idProvOld) : this.soggettiCore.getSoggettoRegistro(newProv);
							portaDominio = soggettoToCheck.getPortaDominio();
				}

				for (int i = 0; i < saList.size(); i++) {
					ServizioApplicativo sa = saList.get(i);

					/**String tipoNomeSoggetto = null;*/

					if(!this.core.isSinglePdD()){

						// bugfix #66
						// controllo se soggetto appartiene a nal diversi, in tal
						// caso e' possibile
						// avere stesse credenziali
						// Raccolgo informazioni soggetto
						Soggetto tmpSoggettoProprietarioSa = this.soggettiCore.getSoggettoRegistro(sa.getIdSoggetto());
						/**tipoNomeSoggetto = tmpSoggettoProprietarioSa.getTipo() + "/" + tmpSoggettoProprietarioSa.getNome();*/

						// se appartengono a nal diversi allora va bene continuo
						if (!portaDominio.equals(tmpSoggettoProprietarioSa.getPortaDominio()))
							continue;
					}
					else{

						/**org.openspcoop2.core.config.Soggetto tmpSoggettoProprietarioSa = this.soggettiCore.getSoggetto(sa.getIdSoggetto());
						//tipoNomeSoggetto = tmpSoggettoProprietarioSa.getTipo() + "/" + tmpSoggettoProprietarioSa.getNome();*/

					}

					if ((tipoOperazione.equals(TipoOperazione.CHANGE)) && (nome.equals(sa.getNome())) && (idProvOld == sa.getIdSoggetto())) {
						continue;
					}
					if(saOld!=null && tipoOperazione.equals(TipoOperazione.CHANGE) && saOld.getId().longValue() == sa.getId().longValue()) {
						continue;
					}

					// Messaggio di errore
					String labelSoggetto = this.getLabelNomeSoggetto(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
					if(protocollo!=null && this.core.isProfiloModIPA(protocollo)) {
						this.pd.setMessage("L'applicativo "+sa.getNome()+" (soggetto: "+labelSoggetto+") possiede già l'identificativo client indicato");
					}
					else {
						this.pd.setMessage("L'applicativo "+sa.getNome()+" (soggetto: "+labelSoggetto+") possiede già le credenziali indicate");
					}
					return false;
				}
				
				String oldTokenPolicy = null;
				if (tipoOperazione.equals(TipoOperazione.CHANGE)) {
					if(saOld!=null && saOld.getInvocazionePorta()!=null && saOld.getInvocazionePorta().sizeCredenzialiList()>0) {
						// prendo il primo
						oldTokenPolicy = saOld.getInvocazionePorta().getCredenziali(0).getTokenPolicy();
					}
				}
				
				if(oldTokenPolicy!=null && !oldTokenPolicy.equals(tokenPolicy)) {
					// controllo che non sia usato in qualche PD
					
					FiltroRicercaPorteDelegate filtro = new FiltroRicercaPorteDelegate();
					filtro.setTipoSoggetto(idSA.getIdSoggettoProprietario().getTipo());
					filtro.setNomeSoggetto(idSA.getIdSoggettoProprietario().getNome());
					filtro.setNomeServizioApplicativo(idSA.getNome());
					List<IDPortaDelegata> list = this.porteDelegateCore.getAllIdPorteDelegate(filtro);
					if(list!=null && !list.isEmpty()) {
						this.pd.setMessage("Non &egrave; possibile modificare la token policy poich&egrave; l'applicativo viene utilizzato all'interno del controllo degli accessi (autorizzazione trasporto) di "+
								list.size()+" fruizion"+(list.size()>1?"i":"e"));
						return false;
					}
					
					String tipoTrasporto = "trasporto";
					if(this.isProfiloModIPA(protocollo)) {
						tipoTrasporto = "messaggio";
					}
					
					FiltroRicercaPorteApplicative filtroPA = new FiltroRicercaPorteApplicative();
					IDServizioApplicativo idServizioApplicativoAutorizzato = new IDServizioApplicativo();
					idServizioApplicativoAutorizzato.setIdSoggettoProprietario(new IDSoggetto(idSA.getIdSoggettoProprietario().getTipo(), idSA.getIdSoggettoProprietario().getNome()));
					idServizioApplicativoAutorizzato.setNome(idSA.getNome());
					filtroPA.setIdServizioApplicativoAutorizzato(idServizioApplicativoAutorizzato);
					List<IDPortaApplicativa> listPA = this.porteApplicativeCore.getAllIdPorteApplicative(filtroPA);
					if(listPA!=null && !listPA.isEmpty()) {
						this.pd.setMessage("Non &egrave; possibile modificare la token policy poich&egrave; l'applicativo viene utilizzato all'interno del controllo degli accessi (autorizzazione "+tipoTrasporto+") di "+
								listPA.size()+" erogazion"+(listPA.size()>1?"i":"e"));
						return false;
					}
					
					filtro = new FiltroRicercaPorteDelegate();
					filtro.setTipoSoggetto(idSA.getIdSoggettoProprietario().getTipo());
					filtro.setNomeSoggetto(idSA.getIdSoggettoProprietario().getNome());
					filtro.setNomeServizioApplicativoToken(idSA.getNome());
					list = this.porteDelegateCore.getAllIdPorteDelegate(filtro);
					if(list!=null && !list.isEmpty()) {
						this.pd.setMessage("Non &egrave; possibile modificare la token policy poich&egrave; l'applicativo viene utilizzato all'interno del controllo degli accessi (autorizzazione token) di "+
								list.size()+" fruizion"+(list.size()>1?"i":"e"));
						return false;
					}
					
					filtroPA = new FiltroRicercaPorteApplicative();
					filtroPA.setIdServizioApplicativoAutorizzatoToken(idServizioApplicativoAutorizzato);
					listPA = this.porteApplicativeCore.getAllIdPorteApplicative(filtroPA);
					if(listPA!=null && !listPA.isEmpty()) {
						this.pd.setMessage("Non &egrave; possibile modificare la token policy poich&egrave; l'applicativo viene utilizzato all'interno del controllo degli accessi (autorizzazione token) di "+
								listPA.size()+" erogazion"+(listPA.size()>1?"i":"e"));
						return false;
					}
				}
				
				
			} 

			// erogatore
			boolean validaEndPoint = this.isModalitaStandard() && !TipologiaErogazione.DISABILITATO.equals(ruoloErogatore);
			if(isApplicativiServerEnabled) {
				validaEndPoint = ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(tipoSA);
			}
			
			if(validaEndPoint){
				boolean isOk = this.servizioApplicativoEndPointCheckData(protocollo, listExtendedConnettore, saOld);
				if (!isOk) {
					return false;
				}
			}
			
			return true;

		} catch (Exception e) {
			this.logError("Exception: " + e.getMessage(), e);
			throw new Exception(e.getMessage(),e);
		}
	}

	private void addFilterRuoloServizioApplicativo(String ruoloSA, boolean postBack) throws Exception{
		try {
			String [] tmp = ServiziApplicativiCostanti.getServiziApplicativiRuolo();
			
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
			this.logError("Exception: " + e.getMessage(), e);
			throw new Exception(e.getMessage(),e);
		}
	}
	
	private void addFilterTipoServizioApplicativo(String tipoSA, boolean postBack) throws Exception{
		try {
			String [] tmp_labels = ServiziApplicativiCostanti.getLabelsServiziApplicativiTipo();
			String [] tmp_values = ServiziApplicativiCostanti.getValuesServiziApplicativiTipo();
			
			String [] values = new String[tmp_values.length + 1];
			String [] labels = new String[tmp_labels.length + 1];
			labels[0] = ServiziApplicativiCostanti.LABEL_PARAMETRO_FILTRO_RUOLO_QUALSIASI;
			values[0] = ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_QUALSIASI;
			for (int i =0; i < tmp_labels.length ; i ++) {
				labels[i+1] = tmp_labels[i];
				values[i+1] = tmp_values[i];
			}
			
			String selectedValue = tipoSA != null ? tipoSA : ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_QUALSIASI;
			
			String label = ServiziApplicativiCostanti.LABEL_TIPO;

			this.pd.addFilter(Filtri.FILTRO_TIPO_SERVIZIO_APPLICATIVO, label, selectedValue, values, labels, postBack, this.getSize());
			
		} catch (Exception e) {
			this.logError("Exception: " + e.getMessage(), e);
			throw new Exception(e.getMessage(),e);
		}
	}


	public void prepareServizioApplicativoList(ISearch ricerca, List<ServizioApplicativo> lista, boolean useIdSoggetto) throws Exception {
		prepareServizioApplicativoList(ricerca, lista, useIdSoggetto, true);
	}
	public void prepareServizioApplicativoList(ISearch ricerca, List<ServizioApplicativo> lista, boolean useIdSoggetto, boolean readProvider) throws Exception {
		try {
			boolean modalitaCompleta = this.isModalitaCompleta();
			
			String idProvider = null;
			if(readProvider) {
				idProvider = this.getParametroLong(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			}
			
			boolean multitenant = this.saCore.isMultitenant();
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione
			Integer parentSA = ServletUtils.getIntegerAttributeFromSession(ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT, this.session, this.request);
			if(parentSA == null) parentSA = ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE;
			Boolean useIdSoggObject = parentSA == ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_SOGGETTO;
			boolean useIdSogg = false;
			if(useIdSoggObject!=null) {
				useIdSogg = useIdSoggObject.booleanValue();
			}

			if(useIdSogg){
				Parameter pProvider = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, idProvider); 
				ServletUtils.addListElementIntoSession(this.request, this.session, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI,pProvider );
			}else 
				ServletUtils.addListElementIntoSession(this.request, this.session, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI);

			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

			@SuppressWarnings("unused")
			Boolean singlePdD = ServletUtils.getObjectFromSession(this.request, this.session, Boolean.class, CostantiControlStation.SESSION_PARAMETRO_SINGLE_PDD);
			
			if(!modalitaCompleta && !useIdSogg) {
				this.pd.setCustomListViewName(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_NOME_VISTA_CUSTOM_LISTA);
			}
			
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
				
				if(!supportAsincroni &&
					this.isModalitaAvanzata()){
					supportAsincroni = this.core.isElenchiSAAsincroniNonSupportatiVisualizzaRispostaAsincrona();
				}
			}

			int idLista = useIdSogg ? Liste.SERVIZI_APPLICATIVI_BY_SOGGETTO : Liste.SERVIZIO_APPLICATIVO;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			String filterProtocollo = null;
			String filterSoggetto = null;
			boolean profiloSelezionato = false;
			boolean supportatoAutenticazioneApplicativiEsterni = false;
			String protocolloS = null;
			if(!useIdSogg) {
				filterProtocollo = addFilterProtocol(ricerca, idLista, true);

				protocolloS = filterProtocollo;
				if(protocolloS==null) {
					// significa che e' stato selezionato un protocollo nel menu in alto a destra
					List<String> protocolli = this.core.getProtocolli(this.request, this.session);
					if(protocolli!=null && protocolli.size()==1) {
						protocolloS = protocolli.get(0);
					}
				}
				if(protocolloS!=null && ! "".equals(protocolloS)) {
					supportatoAutenticazioneApplicativiEsterni = this.saCore.isSupportatoAutenticazioneApplicativiEsterniErogazione(protocolloS); // devono essere fatti vedere tutti i soggetti, anche quelli esterni.
				}
				
				if( (filterProtocollo!=null && 
						//!"".equals(filterProtocollo) &&
						!CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROTOCOLLO_QUALSIASI.equals(filterProtocollo))
						||
					(filterProtocollo==null && protocolloS!=null)
						) {
					profiloSelezionato = true;
				}
				
				if( profiloSelezionato && 
						(!this.isSoggettoMultitenantSelezionato() || supportatoAutenticazioneApplicativiEsterni)) {
					
					boolean soloSoggettiOperativi = true;
					if(supportatoAutenticazioneApplicativiEsterni) {
						soloSoggettiOperativi = false;
					}
					filterSoggetto = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_SOGGETTO);
					this.addFilterSoggetto(filterSoggetto,protocolloS,soloSoggettiOperativi,true);
				}
			}
			if(this.isSoggettoMultitenantSelezionato()){
				filterSoggetto = getSoggettoMultitenantSelezionato();
			}
			
			String filterDominio = null;
			if(this.core.isGestionePddAbilitata(this)==false && multitenant && supportatoAutenticazioneApplicativiEsterni) {
				filterDominio = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_DOMINIO);
				addFilterDominio(filterDominio, true);
			}
			
			if(modalitaCompleta) {
				String filterRuoloSA = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_RUOLO_SERVIZIO_APPLICATIVO);
				this.addFilterRuoloServizioApplicativo(filterRuoloSA,false);
			}
			
			boolean applicativiServerEnabled = this.core.isApplicativiServerEnabled(this); 
			String filterTipoSA = null;
			if(applicativiServerEnabled) {
				filterTipoSA = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_TIPO_SERVIZIO_APPLICATIVO);
				this.addFilterTipoServizioApplicativo(filterTipoSA, true);
			}
			
			String filterTipoCredenziali = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_TIPO_CREDENZIALI);
			this.addFilterTipoCredenziali(filterTipoCredenziali,true,true);
			
			if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_TOKEN.equals(filterTipoCredenziali)) {
				String filterCredenzialeTokenPolicy = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CREDENZIALE_TOKEN_POLICY);
				this.addFilterCredenzialeTokenPolicy(filterCredenzialeTokenPolicy, false);
			}
			
			String filterCredenziale = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CREDENZIALE);
			this.addFilterCredenziale(filterTipoCredenziali, filterCredenziale);
			
			String filterCredenzialeIssuer = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CREDENZIALE_ISSUER);
			this.addFilterCredenzialeIssuer(filterTipoCredenziali, filterCredenzialeIssuer);
					
			String filterRuolo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_RUOLO);
			addFilterRuolo(filterRuolo, false);
			
			String filterGruppo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_GRUPPO);
			addFilterGruppo(filterProtocollo, filterGruppo, true);
			
			String filterApiContesto = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_API_CONTESTO);
			this.addFilterApiContesto(filterApiContesto, true);
			
			if(profiloSelezionato &&
					filterApiContesto!=null && 
					//!"".equals(filterApiContesto) &&
					!CostantiControlStation.DEFAULT_VALUE_PARAMETRO_API_CONTESTO_QUALSIASI.equals(filterApiContesto)) {
				String filterApiImplementazione = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_API_IMPLEMENTAZIONE);
				this.addFilterApiImplementazione(filterProtocollo, filterSoggetto, filterGruppo, filterApiContesto, filterApiImplementazione, false);
			}
			else {
				SearchUtils.clearFilter(ricerca, idLista, Filtri.FILTRO_API_IMPLEMENTAZIONE);
			}
			
			// filtro sui connettori solo se tipoSA = qualsiasi o server
			if(applicativiServerEnabled &&
				(filterTipoSA ==null || (ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_QUALSIASI.equals(filterTipoSA) || 
						ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(filterTipoSA))) 
				){
					
				// **** filtro connettore ****
				
				// nuovi filtri connettore
				this.addFilterSubtitle(ConnettoriCostanti.NAME_SUBTITLE_DATI_CONNETTORE, ConnettoriCostanti.LABEL_SUBTITLE_DATI_CONNETTORE, false);
				
				// filtro tipo connettore con voce IM solo sulle erogazioni
				String filterTipoConnettore = this.addFilterTipoConnettore(ricerca, idLista, true);
				
				// filtro plugin
				this.addFilterConnettorePlugin(ricerca, idLista, filterTipoConnettore);

				// filtro debug
				if(!this.isModalitaStandard()) {
					this.addFilterConnettoreDebug(ricerca, idLista, filterTipoConnettore);
				}
				
				// filtro token policy
				this.addFilterConnettoreTokenPolicy(ricerca, idLista, filterTipoConnettore);
				
				// filtro endpoint
				this.addFilterConnettoreEndpoint(ricerca, idLista, filterTipoConnettore);
				
				// filtro keystore
				this.addFilterConnettoreKeystore(ricerca, idLista, filterTipoConnettore);
									
				// imposto apertura sezione
				this.impostaAperturaSubtitle(ConnettoriCostanti.NAME_SUBTITLE_DATI_CONNETTORE);
				
				// **** fine filtro connettore ****
				
			}
			
			// filtri modipa solo se tipoSA = client o qualsiasi
			if(filterTipoSA ==null || (ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_QUALSIASI.equals(filterTipoSA) || 
					ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_CLIENT.equals(filterTipoSA))) {
				// filtri MODIPA da visualizzare solo se non e' stato selezionato un protocollo in alto a dx  (opzione pilota da file di proprieta')
				// oppure e' selezionato MODIPA
				// oppure non e' stato selezionato un protocollo in alto e nessun protocollo nei filtri  (opzione pilota da file di proprieta')
				// oppure MODIPA nei filtri
				boolean profiloModipaSelezionato = false;
				// solo se il protocollo modipa e' caricato faccio la verifica
				if(this.core.getProtocolli().contains(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROTOCOLLO_MODIPA)) {
					List<String> profiloModipaSelezionatoOpzioniAccettate = new ArrayList<>();
					profiloModipaSelezionatoOpzioniAccettate.add(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROTOCOLLO_MODIPA);
					if(this.core.isModipaFiltroRicercaProfiloQualsiasiVisualizzaDatiModi()) {
						profiloModipaSelezionatoOpzioniAccettate.add(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROTOCOLLO_QUALSIASI);
					}
					if( (filterProtocollo!=null && profiloModipaSelezionatoOpzioniAccettate.contains(filterProtocollo))
							||
						(filterProtocollo==null && protocolloS!=null && profiloModipaSelezionatoOpzioniAccettate.contains(protocolloS))
						) {
						profiloModipaSelezionato = true;
					}
				}
				
				if(profiloModipaSelezionato) {
					
					// **** filtro modi ****
					
					this.addFilterSubtitle(CostantiControlStation.NAME_SUBTITLE_FILTRI_MODIPA, CostantiControlStation.LABEL_SUBTITLE_FILTRI_MODIPA, false);
					
					// ulteriore condizione per la visualizzazione della sezione e' che il Dominio non deve essere esterno
					if(!this.core.isGestionePddAbilitata(this) && multitenant) {
						
						if(!SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE.equals(filterDominio)) {
							
							// filtro sicurezza messaggio
							String sicurezzaMessaggio = this.addFilterModISicurezzaMessaggioSA(ricerca, idLista, true);
							
							if(sicurezzaMessaggio!=null && !CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SOGGETTO_QUALSIASI.equals(sicurezzaMessaggio) &&
									StatoFunzionalita.ABILITATO.getValue().equals(sicurezzaMessaggio)) {
							
								// filtro keystore
								this.addFilterModIKeystorePath(ricerca, idLista);
								this.addFilterModIKeystoreSubject(ricerca, idLista);
								this.addFilterModIKeystoreIssuer(ricerca, idLista);
								
							}
							
							// filtro sicurezza token
							String sicurezzaToken = this.addFilterModISicurezzaTokenSA(ricerca, idLista, true);
							
							if(sicurezzaToken!=null && !CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SOGGETTO_QUALSIASI.equals(sicurezzaToken) &&
									StatoFunzionalita.ABILITATO.getValue().equals(sicurezzaToken)) {
							
								// filtro token
								this.addFilterModITokenPolicy(ricerca, idLista, false);
								this.addFilterModITokenClientId(ricerca, idLista);
								
							}
																
						}
					}
					
					// filtro audience
					this.addFilterModIAudience(ricerca, idLista, true, null, filterDominio);
					
					// imposto apertura sezione
					this.impostaAperturaSubtitle(CostantiControlStation.NAME_SUBTITLE_FILTRI_MODIPA);
					
					// **** fine filtro modi ****
				}
			}
			
			String protocolloPerFiltroProprieta = protocolloS;
			// valorizzato con il protocollo nel menu in alto a destra oppure null, controllo se e' stato selezionato nel filtro di ricerca
			if(protocolloPerFiltroProprieta == null) {
				if(
						//"".equals(filterProtocollo) || 
						CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROTOCOLLO_QUALSIASI.equals(filterProtocollo)) {
					protocolloPerFiltroProprieta = null;
				} else {
					protocolloPerFiltroProprieta = filterProtocollo;
				}
			}
			
			String soggettoPerFiltroProprieta = null;
			if(profiloSelezionato) {
				// soggetto non selezionato nel menu' in alto a dx
				if(!this.isSoggettoMultitenantSelezionato()) {
					soggettoPerFiltroProprieta = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_SOGGETTO);
					if(
							//"".equals(soggettoPerFiltroProprieta) || 
							CostantiControlStation.DEFAULT_VALUE_PARAMETRO_SOGGETTO_QUALSIASI.equals(soggettoPerFiltroProprieta)) {
						soggettoPerFiltroProprieta = null;
					}
				} else {
					soggettoPerFiltroProprieta = this.getSoggettoMultitenantSelezionato();
				}
			}
			
			// **** filtro proprieta ****
			
			List<String> nomiProprieta = this.nomiProprietaSA(protocolloPerFiltroProprieta,soggettoPerFiltroProprieta);
			if(nomiProprieta != null && !nomiProprieta.isEmpty()) {
				this.addFilterSubtitle(CostantiControlStation.NAME_SUBTITLE_PROPRIETA, CostantiControlStation.LABEL_SUBTITLE_PROPRIETA, false);
				
				// filtro nome
				this.addFilterProprietaNome(ricerca, idLista, nomiProprieta);
				
				// filtro valore
				this.addFilterProprietaValore(ricerca, idLista, nomiProprieta);
				
				// imposto apertura sezione
				this.impostaAperturaSubtitle(CostantiControlStation.NAME_SUBTITLE_PROPRIETA);
			}
			
			// **** fine filtro proprieta ****
			
			
			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));


			// setto la barra del titolo
			
			String labelApplicativi = ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI;
			String labelApplicativiDi = ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_DI;
			if(!modalitaCompleta) {
				labelApplicativi = ServiziApplicativiCostanti.LABEL_APPLICATIVI;
				labelApplicativiDi = ServiziApplicativiCostanti.LABEL_PARAMETRO_APPLICATIVI_DI;
			}
			
			if(!useIdSogg){
				this.pd.setSearchLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_NOME);
				if (search.equals("")) {
					this.pd.setSearchDescription("");
					ServletUtils.setPageDataTitle(this.pd, 
							new Parameter(labelApplicativi,ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST));
				}
				else{
					ServletUtils.setPageDataTitle(this.pd, 
							new Parameter(labelApplicativi,ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA,null));	
				}
			} else {
				List<Parameter> lstParam = new ArrayList<>();

				lstParam.add(new Parameter(SoggettiCostanti.LABEL_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));

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

			boolean showProtocolli = this.core.countProtocolli(this.request, this.session)>1;
			
			supportatoAutenticazioneApplicativiEsterni = false;
			if (lista != null) {
				Iterator<ServizioApplicativo> it = lista.iterator();
				while (it.hasNext()) {
					ServizioApplicativo sa = it.next();
					String protocolloCheck = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(sa.getTipoSoggettoProprietario());
					if(this.saCore.isSupportatoAutenticazioneApplicativiEsterniErogazione(protocolloCheck)) {
						supportatoAutenticazioneApplicativiEsterni = true;
						break;
					}
				}
			}
			
			// setto le label delle colonne
			this.setLabelColonne(modalitaCompleta, useIdSogg, multitenant, supportatoAutenticazioneApplicativiEsterni, showProtocolli, supportAsincroni);

			// preparo i dati
			List<List<DataElement>> dati = new ArrayList<>();

			if (lista != null) {
				Iterator<ServizioApplicativo> it = lista.iterator();
				while (it.hasNext()) {
					List<DataElement> e = (!modalitaCompleta && !useIdSogg) ? 
							
							this.creaEntryCustom(multitenant, useIdSogg, showProtocolli, supportatoAutenticazioneApplicativiEsterni, applicativiServerEnabled, it) :
							
							this.creaEntry(modalitaCompleta, multitenant, useIdSogg, contaListe, supportAsincroni, showProtocolli, supportatoAutenticazioneApplicativiEsterni, applicativiServerEnabled, it);

					dati.add(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

			// preparo bottoni
			if(lista!=null && !lista.isEmpty()){
				if (this.core.isShowPulsantiImportExport()) {

					ExporterUtils exporterUtils = new ExporterUtils(this.archiviCore);
					if(exporterUtils.existsAtLeastOneExportMode(org.openspcoop2.protocol.sdk.constants.ArchiveType.SERVIZIO_APPLICATIVO, this.request, this.session)){

						List<AreaBottoni> bottoni = new ArrayList<>();

						AreaBottoni ab = new AreaBottoni();
						List<DataElement> otherbott = new ArrayList<>();
						DataElement de = new DataElement();
						de.setValue(ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI_ESPORTA_SELEZIONATI);
						de.setOnClick(ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI_ESPORTA_SELEZIONATI_ONCLICK);
						de.setDisabilitaAjaxStatus();
						otherbott.add(de);
						ab.setBottoni(otherbott);
						bottoni.add(ab);

						this.pd.setAreaBottoni(bottoni);

					}

				}
			}
			
		} catch (Exception e) {
			this.logError("Exception: " + e.getMessage(), e);
			throw new Exception(e.getMessage(),e);
		}
	}
	private List<DataElement> creaEntry(boolean modalitaCompleta, boolean multitenant, Boolean useIdSogg,
			Boolean contaListe, boolean supportAsincroni, boolean showProtocolli, boolean supportatoAutenticazioneApplicativiEsterni, boolean applicativiServerEnabled,
			Iterator<ServizioApplicativo> it) throws DriverRegistroServiziNotFound, DriverRegistroServiziException,
			DriverControlStationException, DriverControlStationNotFound, Exception, DriverConfigurazioneException {
		ServizioApplicativo sa = it.next();
		List<DataElement> e = new ArrayList<>();

		DataElement de = new DataElement();
		de.setType(DataElementType.HIDDEN);
		de.setValue("" + sa.getId());
		e.add(de);

		String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(sa.getTipoSoggettoProprietario());
		
		IDSoggetto idSoggettoProprietario = new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario());
		Soggetto soggettoProprietario = this.soggettiCore.getSoggettoRegistro(idSoggettoProprietario);
		String dominio = this.pddCore.isPddEsterna(soggettoProprietario.getPortaDominio()) ? SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE : SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE;
		
		de = new DataElement();
		de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE, 
				new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID, sa.getId()+""),
				new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, sa.getIdSoggetto()+""),
				new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO, dominio));
		de.setSize(this.core.getElenchiMenuIdentificativiLunghezzaMassima());
		de.setValue(sa.getNome());
		de.setIdToRemove(sa.getId().toString());
		e.add(de);

		if(!useIdSogg && (
				(multitenant && !this.isSoggettoMultitenantSelezionato())
				||
				(supportatoAutenticazioneApplicativiEsterni)
				)) {
			de = new DataElement();
			if(modalitaCompleta) {
				de.setUrl(SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE, 
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID, sa.getIdSoggetto()+""),
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME, sa.getNomeSoggettoProprietario()),
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO, sa.getTipoSoggettoProprietario()));
			}
			de.setValue(this.getLabelNomeSoggetto(protocollo, sa.getTipoSoggettoProprietario() , sa.getNomeSoggettoProprietario()));
			e.add(de);
		}

		if( showProtocolli ) {
			de = new DataElement();
			de.setValue(this.getLabelProtocollo(protocollo));
			e.add(de);
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
		
		// Tipo
		boolean isServer = false;
		
		if(applicativiServerEnabled) {
			de = new DataElement();
			isServer = ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(sa.getTipo());
			String tipoLabel = this.getTipo(sa);
			if(sa.isUseAsClient()) {
				isServer = false;
				// tipoLabel = tipoLabel+" / "+ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI_TIPO_CLIENT; gia aggiunto in metodo getTipo
			}
			de.setValue(tipoLabel);
			e.add(de);
		}

		if(!modalitaCompleta) {
			de = new DataElement();
			if(isServer) {
				de.setValue("-");
			}
			else {
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
					ConsoleSearch searchForCount = new ConsoleSearch(true,1);
					this.saCore.servizioApplicativoRuoliList(sa.getId(),searchForCount);
					//int numRuoli = lista1.size();
					int numRuoli = searchForCount.getNumEntries(Liste.SERVIZIO_APPLICATIVO_RUOLI);
					ServletUtils.setDataElementVisualizzaLabel(de,(long)numRuoli);
				} else
					ServletUtils.setDataElementVisualizzaLabel(de);
			}
			e.add(de);
		}
		
		if (modalitaCompleta){
			de = new DataElement();
			de.setValue(this.getTipologia(sa));
			e.add(de);
		}
		
		if (modalitaCompleta){
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
					boolean messageBoxDisabilitato = ((CostantiConfigurazione.DISABILITATO.equals(is.getGetMessage())) || (is.getGetMessage() == null) || ("".equals(is.getGetMessage().getValue())));
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
			e.add(de);
		}

		if(supportAsincroni && modalitaCompleta){
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
						supportoAsincronoPuntualeSoggetto = this.core.isElenchiSAAsincroniNonSupportatiVisualizzaRispostaAsincrona();
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
					boolean messageBoxDisabilitato = ((CostantiConfigurazione.DISABILITATO.equals(ra.getGetMessage())) || (ra.getGetMessage() == null) || ("".equals(ra.getGetMessage().getValue())) );
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
			e.add(de);
		}
		return e;
	}
	private List<DataElement> creaEntryCustom(boolean multitenant, Boolean useIdSoggParam, boolean showProtocolli, boolean supportatoAutenticazioneApplicativiEsterni,
			boolean applicativiServerEnabled, Iterator<ServizioApplicativo> it) throws DriverRegistroServiziNotFound, DriverRegistroServiziException,
		DriverControlStationException, DriverControlStationNotFound, DriverConfigurazioneException {
			ServizioApplicativo sa = it.next();
			List<DataElement> e = new ArrayList<>();
		String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(sa.getTipoSoggettoProprietario());
		
		IDSoggetto idSoggettoProprietario = new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario());
		Soggetto soggettoProprietario = this.soggettiCore.getSoggettoRegistro(idSoggettoProprietario);
		String dominio = this.pddCore.isPddEsterna(soggettoProprietario.getPortaDominio()) ? SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE : SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE;
		
		Parameter pSAId = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID, sa.getId()+"");
		Parameter pSAIdSoggetto = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, sa.getIdSoggetto()+"");
		Parameter pDominio = new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO, dominio);
		
		
		List<Parameter> listaParametriChange = new ArrayList<>();
		listaParametriChange.add(pSAId);
		listaParametriChange.add(pSAIdSoggetto);
		listaParametriChange.add(pDominio);
		// TITOLO (nome + soggetto)
		String nome = sa.getNome();
		DataElement de = new DataElement();
		de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE, pSAId, pSAIdSoggetto, pDominio);
		de.setSize(this.core.getElenchiMenuIdentificativiLunghezzaMassima());
		
		de.setIdToRemove(sa.getId().toString());
		de.setType(DataElementType.TITLE);
		
		boolean useIdSogg = false;
		if(useIdSoggParam!=null) {
			useIdSogg = useIdSoggParam.booleanValue();
		}
		
		if(!useIdSogg && ((multitenant && !this.isSoggettoMultitenantSelezionato()) || (supportatoAutenticazioneApplicativiEsterni))) {
			nome = nome + "@" + this.getLabelNomeSoggetto(protocollo, sa.getTipoSoggettoProprietario() , sa.getNomeSoggettoProprietario());
		}
		de.setValue(nome);
		e.add(de);

		// Metadati (profilo + tipo)
		
		String tipoLabel = "";
		boolean isServer = false;
		
		if(applicativiServerEnabled || showProtocolli) {
			de = new DataElement();
			boolean addMetadati = true;
			
			if(applicativiServerEnabled) {
				isServer = ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(sa.getTipo());
				tipoLabel = this.getTipo(sa);
				if(sa.isUseAsClient()) {
					isServer = false;
					//tipoLabel = tipoLabel+" / "+ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI_TIPO_CLIENT; gia aggiunto in metodo getTipo
				}
				
				if(showProtocolli) {
					String labelProtocollo =this.getLabelProtocollo(protocollo); 
					de.setValue(MessageFormat.format(ServiziApplicativiCostanti.MESSAGE_METADATI_SERVIZIO_APPLICATIVO_CON_PROFILO, labelProtocollo, tipoLabel));
				} else {
					de.setValue(MessageFormat.format(ServiziApplicativiCostanti.MESSAGE_METADATI_SERVIZIO_APPLICATIVO_SENZA_PROFILO, tipoLabel));
				}
			} else {
				if(showProtocolli) {
					String labelProtocollo =this.getLabelProtocollo(protocollo); 
					de.setValue(MessageFormat.format(ServiziApplicativiCostanti.MESSAGE_METADATI_SERVIZIO_APPLICATIVO_SOLO_PROFILO, labelProtocollo));
				} else {
					de.setValue(ServiziApplicativiCostanti.MESSAGE_METADATI_SERVIZIO_APPLICATIVO_VUOTI);
					addMetadati = false;
				}
			}
			de.setType(DataElementType.SUBTITLE);
			
			if(addMetadati)
				e.add(de);
		}
		
		// Ruoli e' come i tag per ora
		if(!isServer) {
			List<String> listaRuoli = this.saCore.servizioApplicativoRuoliList(sa.getId(),new ConsoleSearch(true));
			for (int j = 0; j < listaRuoli.size(); j++) {
				String ruolo = listaRuoli.get(j);
				
				de = new DataElement();
				de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RUOLO + "_" + j);
				de.setType(DataElementType.BUTTON);
				de.setLabel(ruolo);
				
/**					int indexOf = ruoliDisponibili.indexOf(ruolo);
//					if(indexOf == -1)
//						indexOf = 0;
//					
//					indexOf = indexOf % CostantiControlStation.NUMERO_GRUPPI_CSS;*/
				
				de.setStyleClass("ruolo-label-info-0"); /**+indexOf);*/
				
				de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_RUOLI_LIST,
						new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO, sa.getId()+""));
				
				de.setToolTip(RuoliCostanti.LABEL_RUOLI); 
				
				e.add(de);
			}
		}
		
		
		IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
		idServizioApplicativo.setNome(sa.getNome());
		idServizioApplicativo.setIdSoggettoProprietario(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
		
		listaParametriChange.add(new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CERTIFICATI_FROM_LISTA, "true"));
		listaParametriChange.add(new Parameter(CostantiControlStation.PARAMETRO_RESET_CACHE_FROM_LISTA, "true"));
		
		// In Uso Button
		this.addInUsoButton(e, 
				(this.isSoggettoMultitenantSelezionato() ? sa.getNome() : this.getLabelServizioApplicativoConDominioSoggetto(idServizioApplicativo)), 
				sa.getId()+"", InUsoType.SERVIZIO_APPLICATIVO);
		
		// Verifica certificati visualizzato solo se l'applicativo ha credenziali https oppure una configurazione modi di sicurezza
		if(this.core.isApplicativiVerificaCertificati()) {
			
			// Verifica certificati visualizzato solo se il soggetto ha credenziali https
			boolean ssl = false;
			InvocazionePorta ip = sa.getInvocazionePorta();
			for (int i = 0; i < ip.sizeCredenzialiList(); i++) {
				Credenziali c = ip.getCredenziali(i);
				if(org.openspcoop2.core.config.constants.CredenzialeTipo.SSL.equals(c.getTipo())) { 
						//&& c.getCertificate()!=null) { non viene ritornato dalla lista
					ssl = true;
				}
			}

			// Verifica configurazione modi di sicurezza
			boolean modi = this.core.isProfiloModIPA(protocollo);
			boolean sicurezzaMessaggioModi = false;
			boolean server = ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(sa.getTipo());
			if(modi){
				KeystoreParams keystoreParams =	org.openspcoop2.protocol.utils.ModIUtils.getApplicativoKeystoreParams(sa.getProtocolPropertyList());
				sicurezzaMessaggioModi = keystoreParams!= null;
			}
			
			if(ssl || sicurezzaMessaggioModi || server) {
				this.addVerificaCertificatiButton(e, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_VERIFICA_CERTIFICATI, listaParametriChange);
			}
		}
		
		// Verifica connettività 
		if(ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(sa.getTipo())) {
			List<Parameter> listaParametriVerificaConnettivitaChange = new ArrayList<>();
			listaParametriVerificaConnettivitaChange.addAll(listaParametriChange);
			listaParametriVerificaConnettivitaChange.add(new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTIVITA, "true"));
			this.addVerificaConnettivitaButton(e, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_VERIFICA_CERTIFICATI, listaParametriVerificaConnettivitaChange);
		}
		
		// se e' abilitata l'opzione reset cache per elemento, visualizzo il comando nell'elenco dei comandi disponibili nella lista
		if(this.core.isElenchiVisualizzaComandoResetCacheSingoloElemento()){
			this.addComandoResetCacheButton(e, (this.isSoggettoMultitenantSelezionato() ? sa.getNome() : this.getLabelServizioApplicativoConDominioSoggetto(idServizioApplicativo)), 
					 ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE, listaParametriChange);
		}
		
		// Proprieta Button
		/**if(this.existsProprietaOggetto(sa.getProprietaOggetto(), sa.getDescrizione())) {
		 ** la lista non riporta le proprietà. Ma esistono e poi sarà la servlet a gestirlo
		 */
		this.addProprietaOggettoButton(e, (this.isSoggettoMultitenantSelezionato() ? sa.getNome() : this.getLabelServizioApplicativoConDominioSoggetto(idServizioApplicativo)), 
				sa.getId()+"", InUsoType.SERVIZIO_APPLICATIVO);
	
		
		return e;
	}
	
	private void setLabelColonne(boolean modalitaCompleta, boolean useIdSogg, boolean multitenant, boolean supportatoAutenticazioneApplicativiEsterni, boolean showProtocolli, boolean supportAsincroni) {
		if(!modalitaCompleta && !useIdSogg) {
			List<String> labels = new ArrayList<>();
			//			labels.add("");//ServiziApplicativiCostanti.LABEL_SA_STATO); // colonna info
			labels.add(ServiziApplicativiCostanti.LABEL_APPLICATIVI);
			
			this.pd.setLabels(labels.toArray(new String[1]));
		} else {
			List<String> labels = new ArrayList<>();
			labels.add(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_NOME);
			if(!useIdSogg && (
					(multitenant && !this.isSoggettoMultitenantSelezionato())
					||
					(supportatoAutenticazioneApplicativiEsterni)
					)) {
				labels.add(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			}
			if( showProtocolli ) {
				labels.add(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROTOCOLLO_COMPACT);
			}
			if(this.core.isApplicativiServerEnabled(this)) {
				labels.add(ServiziApplicativiCostanti.LABEL_TIPO);
			}
			if(!modalitaCompleta) {
				labels.add(RuoliCostanti.LABEL_RUOLI);
			}
			if(modalitaCompleta) {
				labels.add(ServiziApplicativiCostanti.LABEL_TIPOLOGIA);
			}
			if (modalitaCompleta){
				labels.add(ServiziApplicativiCostanti.LABEL_INVOCAZIONE_SERVIZIO);
			}
			if(supportAsincroni && modalitaCompleta){
				labels.add(ServiziApplicativiCostanti.LABEL_RISPOSTA_ASINCRONA);
			}
			this.pd.setLabels(labels.toArray(new String[1]));
		}
	}
	
	private String getTipo(ServizioApplicativo sa){
		return getTipo(sa.getTipo(),sa.isUseAsClient());
	}
	
	private String getTipo(String tipo, boolean useServerAsClient){
		if(tipo == null)
			return ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_NON_CONFIGURATO;
		
		if(tipo.equals(ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_CLIENT))
			return ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI_TIPO_CLIENT;
		else if(tipo.equals(ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER)) {
			if(useServerAsClient) {
				return ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI_TIPO_SERVER+" / "+ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI_TIPO_CLIENT;
			}
			else {
				return ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI_TIPO_SERVER;
			}
		}
		
		return tipo;
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
	
	public void addEndPointToDati(List<DataElement> dati,
			String idsil,String nomeservizioApplicativo,String sbustamento,String sbustamentoInformazioniProtocolloRichiesta,
			String getmsg,String usernameGetMsg, String passwordGetMsg,boolean gestioneCredenzialiGetMsg,
			String invrif,String risprif, String nomeProtocollo, boolean showName,
			boolean isInvocazioneServizio, boolean showTitleTrattamentoMessaggio,
			Integer parentSA, ServiceBinding serviceBinding,
			String accessoDaAPSParametro, boolean servizioApplicativoServerEnabled, 
			String tipoSA, boolean useAsClient,
			boolean integrationManagerEnabled,
			TipoOperazione tipoOperazione, String tipoCredenzialiSSLVerificaTuttiICampi, String changepwd,
			boolean postBackViaPost) throws Exception{
		
		if(servizioApplicativoServerEnabled) {
			this.addEndPointToDatiAsHidden(dati, idsil, nomeservizioApplicativo, sbustamento,
					sbustamentoInformazioniProtocolloRichiesta, getmsg, usernameGetMsg, passwordGetMsg, 
					gestioneCredenzialiGetMsg, invrif, risprif, nomeProtocollo, showName, isInvocazioneServizio, 
					showTitleTrattamentoMessaggio, parentSA, serviceBinding, accessoDaAPSParametro, servizioApplicativoServerEnabled);
			return;
		}

		IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(nomeProtocollo);
		IProtocolConfiguration config = protocolFactory.createProtocolConfiguration();
		
		DataElement de = new DataElement();
		de.setLabel(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO);
		de.setValue(idsil);
		de.setType(DataElementType.HIDDEN);
		de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO);
		dati.add(de);

		if(accessoDaAPSParametro!=null && !"".equals(accessoDaAPSParametro)) {
			de = new DataElement();
			de.setType(DataElementType.HIDDEN);
			de.setValue(accessoDaAPSParametro);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS);
			dati.add(de);
		}
		
		boolean showFromConfigurazione = false;
		if(parentSA!=null && (parentSA.intValue() == ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_CONFIGURAZIONE)) {
			showFromConfigurazione = true;
		}
		
		if(showName && !showFromConfigurazione){
			
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_SERVIZIO_APPLICATIVO);
			de.setType(DataElementType.TITLE);
			dati.add(de);
			
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_NOME);
			de.setValue(nomeservizioApplicativo);
			de.setType(DataElementType.TEXT);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME_SERVIZIO_APPLICATIVO);
			de.setSize(this.getSize());
			dati.add(de);
		}
		
		
			boolean sbustamentoSoapEnabled = !this.isModalitaStandard() && (serviceBinding == null || serviceBinding.equals(ServiceBinding.SOAP));
			boolean sbustamentoProtocolloEnabled = !this.isModalitaStandard() &&  config.isSupportoSbustamentoProtocollo();
			
			//controllo aggiunta sezione trattamento messaggio appare se c'e' almeno un elemento sui 4 previsti che puo' essere visualizzato.
			showTitleTrattamentoMessaggio = showTitleTrattamentoMessaggio && (
					sbustamentoSoapEnabled ||
					sbustamentoProtocolloEnabled || 
					this.isModalitaCompleta()			
					);
			
			if(showTitleTrattamentoMessaggio){
				de = new DataElement();
				de.setLabel(ServiziApplicativiCostanti.LABEL_TRATTAMENTO_MESSAGGIO);
				de.setType(DataElementType.TITLE);
				dati.add(de);
			}
			
			String[] tipoSbustamentoSOAP = { CostantiConfigurazione.ABILITATO.toString(), CostantiConfigurazione.DISABILITATO.toString() };
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_SOAP);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_SOAP);
			if(sbustamentoSoapEnabled) {
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
			
			dati.add(de);
	
			String[] tipoSbustamentoInformazioniProtocollo = { CostantiConfigurazione.ABILITATO.toString(), CostantiConfigurazione.DISABILITATO.toString() };
			de = new DataElement();
			if(nomeProtocollo!=null && !"".equals(nomeProtocollo)){
				de.setLabel(ServiziApplicativiCostanti.getLabelSbustamentoProtocollo(nomeProtocollo));
			}else{
				de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_INFO_PROTOCOLLO);
			}
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RICHIESTA);
			if(sbustamentoProtocolloEnabled) {
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
			dati.add(de);
	
			if (!this.isModalitaCompleta()) {
				de = new DataElement();
				de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO);
				de.setType(DataElementType.HIDDEN);
				de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_RICHIESTA);
				de.setValue(invrif);
				dati.add(de);
	
				de = new DataElement();
				de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_RISPOSTA_PER_RIFERIMENTO);
				de.setType(DataElementType.HIDDEN);
				de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RISPOSTA_PER_RIFERIMENTO);
				de.setValue(risprif);
				dati.add(de);
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
				dati.add(de);
	
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
				dati.add(de);
			}
			
			
			if(integrationManagerEnabled) {
				de = new DataElement();
				de.setLabel(ServiziApplicativiCostanti.LABEL_SERVIZIO_MESSAGE_BOX);
				de.setType(DataElementType.TITLE);
				dati.add(de);
			}
			
			String[] tipoGM = { CostantiConfigurazione.ABILITATO.toString(), CostantiConfigurazione.DISABILITATO.toString() };
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_MESSAGE_BOX);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_MESSAGE_BOX);
			
			if(integrationManagerEnabled) {
				de.setType(DataElementType.SELECT);
				de.setValues(tipoGM);
				if(getmsg==null){
					de.setSelected(CostantiConfigurazione.DISABILITATO.toString());
				}else{
					de.setSelected(getmsg);
				}
				if(postBackViaPost) {
					de.setPostBack_viaPOST(true);
				}
				else {
					de.setPostBack(true);
				}
			} else {
				de.setType(DataElementType.HIDDEN);
				if(getmsg==null){
					de.setValue(CostantiConfigurazione.DISABILITATO.toString());
				}else{
					de.setValue(getmsg);
				}
			}
			dati.add(de);
			
			if(gestioneCredenzialiGetMsg && CostantiConfigurazione.ABILITATO.toString().equals(getmsg)) {
				de = new DataElement();
				de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				de.setValue(StringEscapeUtils.escapeHtml(usernameGetMsg));
				if(integrationManagerEnabled) {
					de.setType(DataElementType.TEXT_EDIT);
				}
				else {
					de.setType(DataElementType.HIDDEN);
				}
				de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				de.setSize(this.getSize());
				de.setRequired(true);
				dati.add(de);
	
				boolean change = TipoOperazione.CHANGE.equals(tipoOperazione);
				
				boolean passwordCifrata = ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLVerificaTuttiICampi); // tipoCredenzialiSSLVerificaTuttiICampi usata come informazione per sapere se una password e' cifrata o meno
				if(change && passwordCifrata) {
					try {
						long idS = -1;
						if(idsil!=null) {
							idS = Long.valueOf(idsil);
						}
						if(idS>0) {
							ServizioApplicativo oldSA = this.saCore.getServizioApplicativo(idS);
							boolean oldIMState = false;
							if(isInvocazioneServizio) {
								oldIMState = oldSA.getInvocazioneServizio()!=null && oldSA.getInvocazioneServizio().getGetMessage()!=null && 
										StatoFunzionalita.ABILITATO.equals(oldSA.getInvocazioneServizio().getGetMessage());
							}
							else {
								oldIMState = oldSA.getRispostaAsincrona()!=null && oldSA.getRispostaAsincrona().getGetMessage()!=null && 
										StatoFunzionalita.ABILITATO.equals(oldSA.getRispostaAsincrona().getGetMessage());
							}
							if(!oldIMState) {
								// se prima di questa modifica l'integration manager non era abilitato, devo far impostare la password
								passwordCifrata = false;
							}
						}
					}catch(Throwable t) {}
				}
				
				if(change && passwordCifrata ){
					DataElement deModifica = new DataElement();
					deModifica.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_MODIFICA_PASSWORD);
					deModifica.setType(DataElementType.CHECKBOX);
					deModifica.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CHANGE_PASSWORD);
					deModifica.setPostBack(true);
					deModifica.setSelected(changepwd);
					deModifica.setSize(this.getSize());
					dati.add(deModifica);
					
					DataElement deCifratura = new DataElement();
					deCifratura.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI);
					deCifratura.setType(DataElementType.HIDDEN);
					deCifratura.setValue(tipoCredenzialiSSLVerificaTuttiICampi);
					dati.add(deCifratura);
					
				}
				
				if( (!change) || (!passwordCifrata) || (ServletUtils.isCheckBoxEnabled(changepwd)) ){
				
					de = new DataElement();
					if(ServletUtils.isCheckBoxEnabled(changepwd)) {
						de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_NUOVA_PASSWORD);
					}
					else {
						de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
					}
					if(change && passwordCifrata && ServletUtils.isCheckBoxEnabled(changepwd) ){
						de.setValue(null); // non faccio vedere una password cifrata
					}
					else{
						de.setValue(StringEscapeUtils.escapeHtml(passwordGetMsg));
					}
					if(integrationManagerEnabled) {
						//de.setType(DataElementType.TEXT_EDIT);
						// Nuova visualizzazione Password con bottone genera password
						de.setType(DataElementType.CRYPT);
						de.getPassword().setVisualizzaPasswordChiaro(true);
						de.getPassword().setVisualizzaBottoneGeneraPassword(true);
						
						PasswordVerifier passwordVerifier = null;
						boolean isBasicPasswordEnableConstraints = this.connettoriCore.isApplicativiBasicPasswordEnableConstraints();
						int lunghezzaPasswordGenerate= this.connettoriCore.getApplicativiBasicLunghezzaPasswordGenerate();
						if(isBasicPasswordEnableConstraints) {
							passwordVerifier = this.connettoriCore.getApplicativiPasswordVerifier();
						}
						if(passwordVerifier != null) {
							PasswordGenerator passwordGenerator = new PasswordGenerator(passwordVerifier);
							de.getPassword().setPasswordGenerator(passwordGenerator);
							de.setNote(passwordVerifier.help(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE));
						}
						de.getPassword().getPasswordGenerator().setDefaultLength(lunghezzaPasswordGenerate);						
					}
					else {
						de.setType(DataElementType.HIDDEN);
					}
					de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
					de.setSize(this.getSize());
					de.setRequired(true);
					dati.add(de);
					
				}
				else {
					
					de = new DataElement();
					de.setType(DataElementType.HIDDEN);
					de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
					de.setValue(StringEscapeUtils.escapeHtml(passwordGetMsg));
					dati.add(de);
					
				}
				
				if(ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(tipoSA)) {
					de = new DataElement();
					de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_UTILIZZABILE_COME_CLIENT);
					de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_UTILIZZABILE_COME_CLIENT);
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(useAsClient);
					//de.setPostBack(true); Non abilitare senno spunta il link 'ruoli' prima di aver salvata effettivamente l'opzione usaComeClient
					dati.add(de);
				}
			}
			
	}
	
	public void addEndPointToDatiAsHidden(List<DataElement> dati,
			String idsil,String nomeservizioApplicativo,String sbustamento,String sbustamentoInformazioniProtocolloRichiesta,
			String getmsg,String usernameGetMsg, String passwordGetMsg,boolean gestioneCredenzialiGetMsg,
			String invrif,String risprif, String nomeProtocollo, boolean showName,
			boolean isInvocazioneServizio, boolean showTitleTrattamentoMessaggio,
			Integer parentSA, ServiceBinding serviceBinding,
			String accessoDaAPSParametro, boolean servizioApplicativoServerEnabled) throws Exception{

		IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(nomeProtocollo);
		IProtocolConfiguration config = protocolFactory.createProtocolConfiguration();
		
		DataElement de = new DataElement();
		de.setLabel(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO);
		de.setValue(idsil);
		de.setType(DataElementType.HIDDEN);
		de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO);
		dati.add(de);

		if(accessoDaAPSParametro!=null && !"".equals(accessoDaAPSParametro)) {
			de = new DataElement();
			de.setType(DataElementType.HIDDEN);
			de.setValue(accessoDaAPSParametro);
			de.setName(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS);
			dati.add(de);
		}
		
		boolean showFromConfigurazione = false;
		if(parentSA!=null && (parentSA.intValue() == ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_CONFIGURAZIONE)) {
			showFromConfigurazione = true;
		}
		
		if(showName && !showFromConfigurazione){
			
//			de = new DataElement();
//			de.setLabel(ServiziApplicativiCostanti.LABEL_SERVIZIO_APPLICATIVO);
//			de.setType(DataElementType.TITLE);
//			dati.add(de);
			
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_NOME);
			de.setValue(nomeservizioApplicativo);
			de.setType(DataElementType.HIDDEN);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME_SERVIZIO_APPLICATIVO);
			de.setSize(this.getSize());
			dati.add(de);
		}
		
			boolean sbustamentoSoapEnabled = !this.isModalitaStandard() && (serviceBinding == null || serviceBinding.equals(ServiceBinding.SOAP));
			boolean sbustamentoProtocolloEnabled = !this.isModalitaStandard() &&  config.isSupportoSbustamentoProtocollo();
			
			//controllo aggiunta sezione trattamento messaggio appare se c'e' almeno un elemento sui 4 previsti che puo' essere visualizzato.
			showTitleTrattamentoMessaggio = showTitleTrattamentoMessaggio && (
					sbustamentoSoapEnabled ||
					sbustamentoProtocolloEnabled || 
					this.isModalitaCompleta()			
					);
			
//			if(showTitleTrattamentoMessaggio){
//				de = new DataElement();
//				de.setLabel(ServiziApplicativiCostanti.LABEL_TRATTAMENTO_MESSAGGIO);
//				de.setType(DataElementType.TITLE);
//				dati.add(de);
//			}
			
//			String[] tipoSbustamentoSOAP = { CostantiConfigurazione.ABILITATO.toString(), CostantiConfigurazione.DISABILITATO.toString() };
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_SOAP);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_SOAP);
//			if(sbustamentoSoapEnabled) {
//				de.setType(DataElementType.SELECT);
//				de.setValues(tipoSbustamentoSOAP);
//				if(sbustamento==null){
//					de.setSelected(CostantiConfigurazione.DISABILITATO.toString());
//				}else{
//					de.setSelected(sbustamento);
//				}
//			} else {
				de.setType(DataElementType.HIDDEN);
				if(sbustamento==null){
					de.setValue(CostantiConfigurazione.DISABILITATO.toString());
				}else{
					de.setValue(sbustamento);
				}
//			}
			
			dati.add(de);
	
//			String[] tipoSbustamentoInformazioniProtocollo = { CostantiConfigurazione.ABILITATO.toString(), CostantiConfigurazione.DISABILITATO.toString() };
			de = new DataElement();
//			if(nomeProtocollo!=null && !"".equals(nomeProtocollo)){
//				de.setLabel(ServiziApplicativiCostanti.getLabelSbustamentoProtocollo(nomeProtocollo));
//			}else{
//				de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_INFO_PROTOCOLLO);
//			}
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RICHIESTA);
//			if(sbustamentoProtocolloEnabled) {
//				de.setType(DataElementType.SELECT);
//				de.setValues(tipoSbustamentoInformazioniProtocollo);
//				if(sbustamentoInformazioniProtocolloRichiesta==null){
//					de.setSelected(CostantiConfigurazione.ABILITATO.toString());
//				}else{
//					de.setSelected(sbustamentoInformazioniProtocolloRichiesta);
//				}
//			}
//			else {
				de.setType(DataElementType.HIDDEN);
				if(sbustamentoInformazioniProtocolloRichiesta==null){
					de.setValue(CostantiConfigurazione.ABILITATO.toString());
				}else{
					de.setValue(sbustamentoInformazioniProtocolloRichiesta);
				}
//			}
			dati.add(de);
	
//			if (!this.isModalitaCompleta()) {
				de = new DataElement();
				de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO);
				de.setType(DataElementType.HIDDEN);
				de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_RICHIESTA);
				if(invrif==null){
					de.setSelected(CostantiConfigurazione.DISABILITATO.toString());
				}else{
					de.setSelected(invrif);
				}
				dati.add(de);
	
				de = new DataElement();
				de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_RISPOSTA_PER_RIFERIMENTO);
				de.setType(DataElementType.HIDDEN);
				de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RISPOSTA_PER_RIFERIMENTO);
				if(risprif==null){
					de.setSelected(CostantiConfigurazione.DISABILITATO.toString());
				}else{
					de.setSelected(risprif);
				}
				dati.add(de);
//			} else {
//				String[] tipoInvRif = { CostantiConfigurazione.ABILITATO.toString(), CostantiConfigurazione.DISABILITATO.toString() };
//				de = new DataElement();
//				de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO);
//				de.setType(DataElementType.SELECT);
//				de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_RICHIESTA);
//				de.setValues(tipoInvRif);
//				if(invrif==null){
//					de.setSelected(CostantiConfigurazione.DISABILITATO.toString());
//				}else{
//					de.setSelected(invrif);
//				}
//				dati.add(de);
//	
//				String[] tipoRispRif = { CostantiConfigurazione.ABILITATO.toString(), CostantiConfigurazione.DISABILITATO.toString() };
//				de = new DataElement();
//				de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_RISPOSTA_PER_RIFERIMENTO);
//				de.setType(DataElementType.SELECT);
//				de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RISPOSTA_PER_RIFERIMENTO);
//				de.setValues(tipoRispRif);
//				if(risprif==null){
//					de.setSelected(CostantiConfigurazione.DISABILITATO.toString());
//				}else{
//					de.setSelected(risprif);
//				}
//				dati.add(de);
//			}
			
			
//			boolean integrationManagerEnabled = !this.isModalitaStandard() && this.core.isIntegrationManagerEnabled();
			
//			if(integrationManagerEnabled) {
//				de = new DataElement();
//				de.setLabel(ServiziApplicativiCostanti.LABEL_SERVIZIO_MESSAGE_BOX);
//				de.setType(DataElementType.TITLE);
//				dati.add(de);
//			}
			
//			String[] tipoGM = { CostantiConfigurazione.ABILITATO.toString(), CostantiConfigurazione.DISABILITATO.toString() };
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_MESSAGE_BOX);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_MESSAGE_BOX);
			
//			if(integrationManagerEnabled) {
//				de.setType(DataElementType.SELECT);
//				de.setValues(tipoGM);
//				if(getmsg==null){
//					de.setSelected(CostantiConfigurazione.DISABILITATO.toString());
//				}else{
//					de.setSelected(getmsg);
//				}
//				de.setPostBack(true);
//			} else {
				de.setType(DataElementType.HIDDEN);
				if(getmsg==null){
					de.setValue(CostantiConfigurazione.DISABILITATO.toString());
				}else{
					de.setValue(getmsg);
				}
//			}
			dati.add(de);
			
			if(gestioneCredenzialiGetMsg && CostantiConfigurazione.ABILITATO.toString().equals(getmsg)) {
				de = new DataElement();
				de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				de.setValue(StringEscapeUtils.escapeHtml(usernameGetMsg));
//				if(!this.isModalitaStandard()) {
//					de.setType(DataElementType.TEXT_EDIT);
//				}
//				else {
					de.setType(DataElementType.HIDDEN);
//				}
				de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				de.setSize(this.getSize());
//				de.setRequired(true);
				dati.add(de);
	
				de = new DataElement();
				de.setLabel(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
				de.setValue(StringEscapeUtils.escapeHtml(passwordGetMsg));
//				if(!this.isModalitaStandard()) {
//					de.setType(DataElementType.TEXT_EDIT);
//				}
//				else {
					de.setType(DataElementType.HIDDEN);
//				}
				de.setName(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
				de.setSize(this.getSize());
//				de.setRequired(true);
				dati.add(de);
			}
			
	}


	public List<DataElement> addHiddenFieldsToDati(List<DataElement> dati, String provider,
			String idAsps, String idPorta){

		DataElement de = new DataElement();
		de.setLabel(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
		de.setValue(provider);
		de.setType(DataElementType.HIDDEN);
		de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_ASPS);
		de.setValue(idAsps);
		de.setType(DataElementType.HIDDEN);
		de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_ASPS);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_PORTA);
		de.setValue(idPorta);
		de.setType(DataElementType.HIDDEN);
		de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_PORTA);
		dati.add(de);

		return dati;

	}

	public void prepareRuoliList(ISearch ricerca, List<String> lista)
			throws Exception {
		try {
			String idsil = this.getParametroLong(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO);
			String idProvider = this.getParametroLong(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			String accessDaChangeTmp = this.getParametroBoolean(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_ACCESSO_DA_CHANGE);
			boolean accessDaChange = ServletUtils.isCheckBoxEnabled(accessDaChangeTmp);
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione
			Integer parentSA = ServletUtils.getIntegerAttributeFromSession(ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT, this.session, this.request);
			if(parentSA == null) parentSA = ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE;
			Boolean useIdSogg = parentSA == ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_SOGGETTO;

			Parameter pSA = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID_SERVIZIO_APPLICATIVO, idsil); 
			
			if(useIdSogg){
				Parameter pProvider = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, idProvider); 
				ServletUtils.addListElementIntoSession(this.request, this.session, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_RUOLI,pSA,pProvider );
			}else 
				ServletUtils.addListElementIntoSession(this.request, this.session, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_RUOLI,pSA,
						new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_ACCESSO_DA_CHANGE, accessDaChangeTmp));
			
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
			
			if(accessDaChange) {
				ServletUtils.setPageDataTitleServletFirst(this.pd, labelApplicativi, 
						ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST);
				ServletUtils.appendPageDataTitle(this.pd, 
						new Parameter(nomeservizioApplicativo, 
								ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE, 
								new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID, sa.getId()+""),
								new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, sa.getIdSoggetto()+"")));
				ServletUtils.appendPageDataTitle(this.pd, 
						new Parameter(RuoliCostanti.LABEL_RUOLI, null));
			}
			else {
				
				if(!useIdSogg){
					if (search.equals("")) {
						this.pd.setSearchDescription("");
						ServletUtils.setPageDataTitle(this.pd, 
								new Parameter(labelApplicativi,ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST),
								new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_DI + nomeservizioApplicativo, 
										ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE,
										listSA));
					}
					else{
						ServletUtils.setPageDataTitle(this.pd, 
								new Parameter(labelApplicativi,ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST),
								new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_DI + nomeservizioApplicativo, 
										ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE,
										listSA),
								new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA,null));	
					}
				} else {
					List<Parameter> lstParam = new ArrayList<>();
	
					lstParam.add(new Parameter(SoggettiCostanti.LABEL_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
					lstParam.add(new Parameter(labelApplicativiDi + tipoENomeSoggetto,
							ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST,
							new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,idProvider)));
	
					if(search.equals("")){
						this.pd.setSearchDescription("");
						lstParam.add(new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_DI + nomeservizioApplicativo, 
								ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE,
								listSA));
					}else{
						lstParam.add(new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_RUOLI_DI + nomeservizioApplicativo, 
								ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE,
								listSA));
						lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
					}
	
					ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));
				}
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
			List<List<DataElement>> dati = new ArrayList<>();
		
			if (lista != null) {
				Iterator<String> it = lista.iterator();
				while (it.hasNext()) {
					String ruolo = it.next();
		
					List<DataElement> e = new ArrayList<>();
		
					DataElement de = new DataElement();
					de.setValue(ruolo);
					de.setIdToRemove(ruolo);
					
					if(!this.isModalitaCompleta()) {
						Ruolo ruoloObj = this.ruoliCore.getRuolo(ruolo);
						Parameter pIdRuolo = new Parameter(RuoliCostanti.PARAMETRO_RUOLO_ID, ruoloObj.getId()+"");
						
						String url = new Parameter("", RuoliCostanti.SERVLET_NAME_RUOLI_CHANGE , pIdRuolo).getValue();
						String tooltip = ruolo;
						
						this.newDataElementVisualizzaInNuovoTab(de, url, tooltip);
					}
					
					e.add(de);
		
					dati.add(e);
				}
			}
		
			this.pd.setDati(dati);
			this.pd.setAddButton(true);
		
		} catch (Exception e) {
			this.logError("Exception: " + e.getMessage(), e);
			throw new Exception(e.getMessage(),e);
		}
	}
	
	public List<Parameter> getTitoloSA(Integer parentSA, String idsogg, String idAsps, String idPorta)	throws Exception, DriverRegistroServiziNotFound, DriverRegistroServiziException {
		
		if(parentSA==null) {
			throw new Exception("Param parentSA is null");
		}
		if(idsogg==null) {
			throw new Exception("Param idsogg is null");
		}
		
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
		
		if(parentSA==null) {
			throw new Exception("Param parentSA is null");
		}
		
		List<Parameter> lstParam = new ArrayList<>();
		switch (parentSA) {
		case ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_CONFIGURAZIONE:
			// Prendo il nome e il tipo del servizio
			AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
			String servizioTmpTile = this.getLabelIdServizio(asps);
			Parameter pIdServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId()+ "");
			Parameter pIdsoggErogatore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+asps.getIdSoggetto());

			Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, this.session, this.request).getValue();
			if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
				lstParam.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_EROGAZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
				lstParam.add(new Parameter(servizioTmpTile, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_CHANGE, pIdServizio));
			} else {
				lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
			}
			//lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_CONFIGURAZIONI_DI + servizioTmpTile, 
			lstParam.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_GESTIONE_CONFIGURAZIONI, 
					AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_PORTE_APPLICATIVE_LIST ,pIdServizio, pIdsoggErogatore));
			break;
		case ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_SOGGETTO:
			lstParam.add(new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
			lstParam.add(new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_DI + soggettoTitle,
					ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST,
					new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,idsogg)));
			break;
		case ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE:
		default:
			String labelApplicativi = ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI;
			if(this.isModalitaCompleta()==false) {
				labelApplicativi = ServiziApplicativiCostanti.LABEL_APPLICATIVI;
			}
			lstParam.add(new Parameter(labelApplicativi,ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST));
			break;
		}
		return lstParam;
	}
	
	public void impostaSADefaultAlleConfigurazioniCheUsanoConnettoreDelMappingDiDefault(String idAsps, PortaApplicativa pa, ServizioApplicativo sa, List<Object> oggettiDaAggiornare) throws DriverConfigurazioneException, Exception,
			DriverRegistroServiziNotFound, DriverRegistroServiziException, DriverConfigurazioneNotFound {
		// se ho modificato un mapping di default aggiorno le porte che hanno il utilizzano la configurazione di default 
		MappingErogazionePortaApplicativa mappingErogazionePortaApplicativa = this.porteApplicativeCore.getMappingErogazionePortaApplicativa(pa);
		if(mappingErogazionePortaApplicativa.isDefault()) {
			String nomeSA = sa.getNome();
			String tipoSA = sa.getTipo();
			String servizioApplicativoDefault = pa.getServizioApplicativoDefault();
			List<MappingErogazionePortaApplicativa> listaMappingErogazionePortaApplicativa = new ArrayList<>();
			// lettura delle configurazioni associate
			AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
			IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
			listaMappingErogazionePortaApplicativa = this.apsCore.mappingServiziPorteAppList(idServizio,asps.getId(), null);
			for(MappingErogazionePortaApplicativa mappinErogazione : listaMappingErogazionePortaApplicativa) {
				// scarto il default
				if(!mappinErogazione.isDefault()) { 
					PortaApplicativa portaApplicativaTmp = this.porteApplicativeCore.getPortaApplicativa(mappinErogazione.getIdPortaApplicativa());
					
					// la porta e' da aggiorare se e' default oppure ridefinita e il SA originale e' lo stesso
					if((portaApplicativaTmp.getServizioApplicativoDefault() != null && servizioApplicativoDefault != null &&
							portaApplicativaTmp.getServizioApplicativoDefault().equals(servizioApplicativoDefault) ) ){ 
						 
						// prelevo l'associazione con il vecchio servizio applicativo
						PortaApplicativaServizioApplicativo paSAtmpInner = null;
						for (PortaApplicativaServizioApplicativo paSAInner : portaApplicativaTmp.getServizioApplicativoList()) {
							if(paSAInner.getNome().equals(nomeSA)) {
								paSAtmpInner = paSAInner;
								break;
							}
						}

						if(paSAtmpInner!= null) {
							// se ho modificato il server che sto utilizzando lo rimuovo
							if(ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(tipoSA)){
								portaApplicativaTmp.getServizioApplicativoList().remove(paSAtmpInner); 	
							} 
						}

						// nuovo SA da aggiungere
						
						PortaApplicativaServizioApplicativo paSAInner = new PortaApplicativaServizioApplicativo();
						paSAInner.setNome(servizioApplicativoDefault);
						portaApplicativaTmp.getServizioApplicativoList().add(paSAInner);
						portaApplicativaTmp.setServizioApplicativoDefault(null);
						oggettiDaAggiornare.add(portaApplicativaTmp);
					 }
					
				}
			}
		}
	}

	public void impostaSAServerAlleConfigurazioniCheUsanoConnettoreDelMappingDiDefault(String idAsps, String nuovoSAServer, PortaApplicativa pa, ServizioApplicativo sa,
			List<Object> oggettiDaAggiornare)
			throws DriverConfigurazioneException, Exception, DriverRegistroServiziNotFound,
			DriverRegistroServiziException, DriverConfigurazioneNotFound {
		// se ho modificato un mapping di default aggiorno le porte che hanno il utilizzano la configurazione di default 
		MappingErogazionePortaApplicativa mappingErogazionePortaApplicativa = this.porteApplicativeCore.getMappingErogazionePortaApplicativa(pa);
		if(mappingErogazionePortaApplicativa.isDefault()) {
			String servizioApplicativoDefault = pa.getServizioApplicativoDefault();
			List<MappingErogazionePortaApplicativa> listaMappingErogazionePortaApplicativa = new ArrayList<>();
			// lettura delle configurazioni associate
			AccordoServizioParteSpecifica asps = this.apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
			IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
			listaMappingErogazionePortaApplicativa = this.apsCore.mappingServiziPorteAppList(idServizio,asps.getId(), null);
			for(MappingErogazionePortaApplicativa mappinErogazione : listaMappingErogazionePortaApplicativa) {
				// scarto il default
				if(!mappinErogazione.isDefault()) { 
					PortaApplicativa portaApplicativaTmp = this.porteApplicativeCore.getPortaApplicativa(mappinErogazione.getIdPortaApplicativa());
					
					// la porta e' da aggiorare se e' default oppure ridefinita e il SA originale e' lo stesso
					if((portaApplicativaTmp.getServizioApplicativoDefault() == null && servizioApplicativoDefault == null) ||
					(portaApplicativaTmp.getServizioApplicativoDefault() != null && servizioApplicativoDefault != null &&
							portaApplicativaTmp.getServizioApplicativoDefault().equals(servizioApplicativoDefault) ) ){ 
						 
						// prelevo l'associazione con il vecchio servizio applicativo
						PortaApplicativaServizioApplicativo paSAtmpInner = null;
						for (PortaApplicativaServizioApplicativo paSAInner : portaApplicativaTmp.getServizioApplicativoList()) {
							if(paSAInner.getNome().equals(sa.getNome())) {
								paSAtmpInner = paSAInner;
								break;
							}
						}

						if(paSAtmpInner!= null) {
							// se ho modificato il server che sto utilizzando lo rimuovo
							if(ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(sa.getTipo())){
								portaApplicativaTmp.getServizioApplicativoList().remove(paSAtmpInner); 	
							} else {
								// SA di default da conservare
								portaApplicativaTmp.getServizioApplicativoList().remove(paSAtmpInner);
								portaApplicativaTmp.setServizioApplicativoDefault(sa.getNome());
							}
						}

						// nuovo SA da aggiungere
						PortaApplicativaServizioApplicativo paSAInner = new PortaApplicativaServizioApplicativo();
						paSAInner.setNome(nuovoSAServer);
						portaApplicativaTmp.getServizioApplicativoList().add(paSAInner);
						oggettiDaAggiornare.add(portaApplicativaTmp);
					 }
				}
			}
		}
	}
	
	public static String[] toArray(List<IDServizioApplicativoDB> listIDSa) {
		if(listIDSa==null || listIDSa.isEmpty()) {
			return null;
		}
		String [] l = new String [listIDSa.size()];
		for (int i = 0; i < listIDSa.size(); i++) {
			l[i] = listIDSa.get(i).getNome();
		}
		return l;
	}
	
	public void prepareServizioApplicativoCredenzialiList(ServizioApplicativo sa, String id) throws Exception { 	
		try {
			boolean modalitaCompleta = this.isModalitaCompleta();
			
			String idProvider = this.getParametroLong(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione
			Integer parentSA = ServletUtils.getIntegerAttributeFromSession(ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT, this.session, this.request);
			if(parentSA == null) parentSA = ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE;
			Boolean useIdSogg = parentSA == ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_SOGGETTO;

			IDSoggetto idSoggettoProprietario = new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario());
			Soggetto soggettoProprietario = this.soggettiCore.getSoggettoRegistro(idSoggettoProprietario);
			String dominio = this.pddCore.isPddEsterna(soggettoProprietario.getPortaDominio()) ? SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE : SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE;
		
			List<Parameter> parametersServletSAChange = new ArrayList<>();
			Parameter pIdSA = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID, sa.getId()+"");
			parametersServletSAChange.add(pIdSA);
			Parameter pIdSoggettoSA = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, sa.getIdSoggetto()+"");
			parametersServletSAChange.add(pIdSoggettoSA);
			if(dominio != null) {
				Parameter pDominio = new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO, dominio);
				parametersServletSAChange.add(pDominio);
			}
			
			if(useIdSogg){
				Parameter pProvider = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, idProvider); 
				List<Parameter> parametersServletSAChangeProvider = new ArrayList<>();
				parametersServletSAChangeProvider.add(pProvider);
				parametersServletSAChangeProvider.addAll(parametersServletSAChange);
				ServletUtils.addListElementIntoSession(this.request, this.session, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_CREDENZIALI, parametersServletSAChangeProvider.toArray(new Parameter[parametersServletSAChangeProvider.size()]));
			}else 
				ServletUtils.addListElementIntoSession(this.request, this.session, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_CREDENZIALI, parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()]));

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
						supportAsincroni = this.core.isElenchiSAAsincroniNonSupportatiVisualizzaRispostaAsincrona();
					}
				}
			}

			this.pd.setIndex(0);
			this.pd.setPageSize(sa.getInvocazionePorta().sizeCredenzialiList());
			this.pd.setNumEntries(sa.getInvocazionePorta().sizeCredenzialiList());

			// ricerca disattivata
			ServletUtils.disabledPageDataSearch(this.pd); 

			// setto la barra del titolo
			
			String labelApplicativi = ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI;
			String labelApplicativiDi = ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_DI;
			if(modalitaCompleta==false) {
				labelApplicativi = ServiziApplicativiCostanti.LABEL_APPLICATIVI;
				labelApplicativiDi = ServiziApplicativiCostanti.LABEL_PARAMETRO_APPLICATIVI_DI;
			}
			
			if(!useIdSogg){
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(labelApplicativi, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST),
						new Parameter(sa.getNome(), ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE, parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])),
						new Parameter(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CERTIFICATI, null)
						);
			} else {
				String provider = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
						new Parameter(labelApplicativiDi + tmpTitle,
								ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST,
								new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,provider)),								
						new Parameter(sa.getNome(), ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE, parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])),
						new Parameter(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CERTIFICATI, null)
						);
			}

			// setto le label delle colonne
			List<String> labels = new ArrayList<>();
			
			labels.add(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_PRINCIPALE);
			labels.add(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SUBJECT);
			labels.add(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_ISSUER);
			labels.add(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI);
			labels.add(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_NOT_BEFORE);
			labels.add(ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_NOT_AFTER);
			
			this.pd.setLabels(labels.toArray(new String[1]));

			// preparo i dati
			List<List<DataElement>> dati = new ArrayList<>();
			
			List<Credenziali> lista = sa.getInvocazionePorta().getCredenzialiList();

			int i = 0;
			
			
			if (lista != null) {
				Iterator<Credenziali> it = lista.iterator();
				while (it.hasNext()) {
					
					List<DataElement> e = new ArrayList<>();
					Credenziali credenziali = it.next();
					Certificate cSelezionato = ArchiveLoader.load(credenziali.getCertificate());
					String tipoCredenzialiSSLAliasCertificatoIssuer = cSelezionato.getCertificate().getIssuer().getNameNormalized();
					String tipoCredenzialiSSLAliasCertificatoSubject = cSelezionato.getCertificate().getSubject().getNameNormalized();
					boolean verificaTuttiCampi = credenziali.getCertificateStrictVerification();
					Date notBefore = cSelezionato.getCertificate().getNotBefore();
					String tipoCredenzialiSSLAliasCertificatoNotBefore = this.getSdfCredenziali().format(notBefore);
					Date notAfter = cSelezionato.getCertificate().getNotAfter();
					String tipoCredenzialiSSLAliasCertificatoNotAfter = this.getSdfCredenziali().format(notAfter);
					
					Parameter pIdCredenziale = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_CREDENZIALI_ID, i+"");
					List<Parameter> parametersServletCredenzialeChange = new ArrayList<>();
					parametersServletCredenzialeChange.add(pIdCredenziale);
					parametersServletCredenzialeChange.addAll(parametersServletSAChange);
					parametersServletCredenzialeChange.add(new Parameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_MULTI_AGGIORNA,Costanti.CHECK_BOX_ENABLED));
				
					//  Principale: si/no
					DataElement de = new DataElement();
					de.setType(DataElementType.TEXT);
					de.setValue(i == 0 ? CostantiControlStation.LABEL_SI : CostantiControlStation.LABEL_NO);
					de.allineaTdAlCentro();
					de.setWidthPx(60);
					e.add(de);
					
					// Subject con link edit
					de = new DataElement();
					de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CREDENZIALI_CHANGE, 
							parametersServletCredenzialeChange.toArray(new Parameter[parametersServletCredenzialeChange.size()]));
					de.setSize(ConnettoriCostanti.NUMERO_CARATTERI_SUBJECT_DA_VISUALIZZARE_IN_LISTA_CERTIFICATI);
					de.setValue(StringEscapeUtils.escapeHtml(tipoCredenzialiSSLAliasCertificatoSubject));
					de.setToolTip(StringEscapeUtils.escapeHtml(tipoCredenzialiSSLAliasCertificatoSubject)); 
					de.setIdToRemove(i+"");
					e.add(de);
					
					// Issuer 
					de = new DataElement();
					String issuerValue = StringEscapeUtils.escapeHtml(tipoCredenzialiSSLAliasCertificatoIssuer);
					if(issuerValue.length() > ConnettoriCostanti.NUMERO_CARATTERI_SUBJECT_DA_VISUALIZZARE_IN_LISTA_CERTIFICATI) {
						issuerValue = issuerValue.substring(0,(ConnettoriCostanti.NUMERO_CARATTERI_SUBJECT_DA_VISUALIZZARE_IN_LISTA_CERTIFICATI-3)) + "...";
					}
					de.setValue(issuerValue);
					de.setToolTip(StringEscapeUtils.escapeHtml(tipoCredenzialiSSLAliasCertificatoIssuer)); 
					e.add(de);
					
					// verifica tutti i campi
					de = new DataElement();
					de.setType(DataElementType.TEXT);
					de.setValue(verificaTuttiCampi ? ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI_ENABLE : 
						ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI_DISABLE);
					de.allineaTdAlCentro();
					de.setWidthPx(70);
					e.add(de);
					
					// not before
					de = new DataElement();
					if(verificaTuttiCampi) {
						de.setValue(tipoCredenzialiSSLAliasCertificatoNotBefore);
					}
					else {
						de.setValue("-");
					}
					de.setType(DataElementType.TEXT);
					de.allineaTdAlCentro();
					de.setWidthPx(140);
					if(verificaTuttiCampi && notBefore.after(new Date())) {
						// bold
						de.setLabelStyleClass(Costanti.INPUT_TEXT_BOLD_CSS_CLASS);
						de.setWidthPx(150);
					}
					e.add(de);	
					
					// not after
					de = new DataElement();
					if(verificaTuttiCampi) {
						de.setValue(tipoCredenzialiSSLAliasCertificatoNotAfter);
					}
					else {
						de.setValue("-");
					}
					de.setType(DataElementType.TEXT);
					de.allineaTdAlCentro();
					de.setWidthPx(140);
					if(verificaTuttiCampi && notAfter.before(new Date())) {
						// bold e rosso
						de.setLabelStyleClass(Costanti.INPUT_TEXT_BOLD_RED_CSS_CLASS);
						de.setWidthPx(150);
					}
					
					e.add(de);

					dati.add(e);
					i++;
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);
		} catch (Exception e) {
			this.logError("Exception: " + e.getMessage(), e);
			throw new Exception(e.getMessage(),e);
		}
		
	}
	
	public List<DataElement> addServizioApplicativoHiddenToDati(List<DataElement> dati, String idSA, String idSoggettoSA, String dominio, String nomeSA) throws Exception {
		
		DataElement de = new DataElement();
		de.setLabel(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID);
		de.setValue(idSA);
		de.setType(DataElementType.HIDDEN);
		de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID);
		dati.add(de);
		
		if(idSoggettoSA != null) {
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			de.setValue(idSoggettoSA);
			de.setType(DataElementType.HIDDEN);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			dati.add(de);
		}
		
		if(dominio != null) {
			de = new DataElement();
			de.setLabel(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO);
			de.setValue(dominio);
			de.setType(DataElementType.HIDDEN);
			de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO);
			dati.add(de);
		}
		
		if(nomeSA != null) {
			de = new DataElement();
			de.setLabel(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME);
			de.setValue(nomeSA);
			de.setType(DataElementType.HIDDEN);
			de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME);
			dati.add(de);
		}
		
		return dati;
	}
	
	public boolean servizioApplicativoCredenzialiCertificatiCheckData(TipoOperazione tipoOperazione, long idProvOld, ServizioApplicativo saOld, int idxCertificato, String protocollo, int idxCredenziale) throws Exception {
		try {
			String nome = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME);
			String provider = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			int newProv = 0;
			if (provider != null) {
				newProv = Integer.parseInt(provider);
			}
			String tipoCredenzialiSSLSorgente = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL);
			if(tipoCredenzialiSSLSorgente == null) {
				tipoCredenzialiSSLSorgente = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE;
			}
			String tipoCredenzialiSSLConfigurazioneManualeSelfSigned= this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_MANUALE_SELF_SIGNED);
			if (tipoCredenzialiSSLConfigurazioneManualeSelfSigned == null) {
				tipoCredenzialiSSLConfigurazioneManualeSelfSigned = Costanti.CHECK_BOX_ENABLED;
			}
			
			String subject = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT);
			String issuer = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_ISSUER);
			if("".equals(issuer)) {
				issuer = null;
			}
			
			boolean tokenWithHttpsEnabledByConfigSA = false;
			if(saOld!=null && saOld.getInvocazionePorta()!=null && saOld.getInvocazionePorta().sizeCredenzialiList()>0) {
				Credenziali c = saOld.getInvocazionePorta().getCredenziali(0);
				if(c!=null && c.getTokenPolicy()!=null && StringUtils.isNotEmpty(c.getTokenPolicy())) {
					// se entro in questa servlet sono sicuramente con credenziale ssl, se esiste anche token policy abbiamo la combo
					tokenWithHttpsEnabledByConfigSA = true;
				}
			}
			
			boolean dominioEsternoModI = false;
			if(saOld!=null) {
				Soggetto soggetto = this.soggettiCore.getSoggettoRegistro(saOld.getIdSoggetto());
				dominioEsternoModI = this.isProfiloModIPA(protocollo) && this.pddCore.isPddEsterna(soggetto.getPortaDominio());
			}
			
			String details = "";
			List<ServizioApplicativo> saList = null;
			String tipoSsl = null;
			Certificate cSelezionato = null;
			boolean strictVerifier = false;
			if(tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE)) {
				saList = this.saCore.servizioApplicativoWithCredenzialiSslList(subject,issuer,
						dominioEsternoModI ? ConfigurazioneFiltroServiziApplicativi.getFiltroApplicativiModIFirma() : ConfigurazioneFiltroServiziApplicativi.getFiltroApplicativiHttps());
				tipoSsl = "subject/issuer";
			}
			else {
				
				BinaryParameter tipoCredenzialiSSLFileCertificato = this.getBinaryParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO);
				String tipoCredenzialiSSLVerificaTuttiICampi = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI);
				strictVerifier = ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLVerificaTuttiICampi);
				String tipoCredenzialiSSLTipoArchivioS = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO);
				String tipoCredenzialiSSLFileCertificatoPassword = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_PASSWORD);
				String tipoCredenzialiSSLAliasCertificato = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO);
				if (tipoCredenzialiSSLAliasCertificato == null) {
					tipoCredenzialiSSLAliasCertificato = "";
				}
				org.openspcoop2.utils.certificate.ArchiveType tipoCredenzialiSSLTipoArchivio= null;
				if(tipoCredenzialiSSLTipoArchivioS == null) {
					tipoCredenzialiSSLTipoArchivio = org.openspcoop2.utils.certificate.ArchiveType.CER; 
				} else {
					tipoCredenzialiSSLTipoArchivio = org.openspcoop2.utils.certificate.ArchiveType.valueOf(tipoCredenzialiSSLTipoArchivioS);
				}
				byte [] archivio = tipoCredenzialiSSLFileCertificato.getValue();
				if(TipoOperazione.CHANGE.equals(tipoOperazione) && archivio==null) {
					archivio = saOld.getInvocazionePorta().getCredenziali(idxCertificato).getCertificate();
				}
				if(tipoCredenzialiSSLTipoArchivio.equals(org.openspcoop2.utils.certificate.ArchiveType.CER)) {
					cSelezionato = ArchiveLoader.load(archivio);
				}else {
					cSelezionato = ArchiveLoader.load(tipoCredenzialiSSLTipoArchivio, archivio, tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLFileCertificatoPassword);
				}
				saList = this.saCore.servizioApplicativoWithCredenzialiSslList(cSelezionato.getCertificate(), strictVerifier,
						dominioEsternoModI ? ConfigurazioneFiltroServiziApplicativi.getFiltroApplicativiModIFirma() : ConfigurazioneFiltroServiziApplicativi.getFiltroApplicativiHttps());
				if(!strictVerifier && saList!=null && !saList.isEmpty()) {
					List<ServizioApplicativo> saListCheck = this.saCore.servizioApplicativoWithCredenzialiSslList(cSelezionato.getCertificate(), true,
							dominioEsternoModI ? ConfigurazioneFiltroServiziApplicativi.getFiltroApplicativiModIFirma() : ConfigurazioneFiltroServiziApplicativi.getFiltroApplicativiHttps());
					if(saListCheck==null || saListCheck.isEmpty() ) {
						details=ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_DETAILS;
					}
				}
				tipoSsl = "certificato";
				
			}
			
			String portaDominio = null;
			if(this.core.isRegistroServiziLocale()){
				Soggetto soggettoToCheck = tipoOperazione.equals(TipoOperazione.CHANGE) ? 
						this.soggettiCore.getSoggettoRegistro(idProvOld) : this.soggettiCore.getSoggettoRegistro(newProv);
						portaDominio = soggettoToCheck.getPortaDominio();
			}
	
			if(saList!=null &&
					!tokenWithHttpsEnabledByConfigSA  // se e' abilitato il token non deve essere controllata l'univocita' del certificato
					) {
				for (int i = 0; i < saList.size(); i++) {
					ServizioApplicativo sa = saList.get(i);
	
					if(!this.core.isSinglePdD()){
	
						// bugfix #66
						// controllo se soggetto appartiene a nal diversi, in tal
						// caso e' possibile
						// avere stesse credenziali
						// Raccolgo informazioni soggetto
						Soggetto tmpSoggettoProprietarioSa = this.soggettiCore.getSoggettoRegistro(sa.getIdSoggetto());
	
						// se appartengono a nal diversi allora va bene continuo
						if (!portaDominio.equals(tmpSoggettoProprietarioSa.getPortaDominio()))
							continue;
	
					}
	
					if ((tipoOperazione.equals(TipoOperazione.CHANGE)) && (nome.equals(sa.getNome())) && (idProvOld == sa.getIdSoggetto())) {
						continue;
					}
					if(saOld!=null && tipoOperazione.equals(TipoOperazione.CHANGE) && saOld.getId().longValue() == sa.getId().longValue()) {
						continue;
					}
	
					// Raccolgo informazioni soggetto
					// Messaggio di errore
					String labelSoggetto = this.getLabelNomeSoggetto(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
					this.pd.setMessage("L'applicativo "+sa.getNome()+" (soggetto: "+labelSoggetto+") possiede già le credenziali ssl ("+tipoSsl+") indicate."+details);
					return false;
				}
			}
			
			
			
			if(!tokenWithHttpsEnabledByConfigSA && // se e' abilitato il token non deve essere controllata l'univocita' del certificato)
				!this.soggettiCore.isSoggettiApplicativiCredenzialiSslPermitSameCredentials() &&
				!dominioEsternoModI) {
				// Verifico soggetti
				
				details = "";
				Soggetto soggettoAutenticato = null;
				List<org.openspcoop2.core.registry.Soggetto> soggettiAutenticati = null;
				if(tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE)) { 
			
					// recupero soggetto con stesse credenziali
					soggettoAutenticato = this.soggettiCore.getSoggettoRegistroAutenticatoSsl(subject, issuer);
					tipoSsl = "subject/issuer";
			
				} else {
					
					soggettiAutenticati = this.soggettiCore.soggettoWithCredenzialiSslList(cSelezionato.getCertificate(), strictVerifier);
					if(soggettiAutenticati!=null && !soggettiAutenticati.isEmpty()) {
						soggettoAutenticato = soggettiAutenticati.get(0);
						if(!strictVerifier) {
							List<org.openspcoop2.core.registry.Soggetto> soggettiAutenticatiCheck = this.soggettiCore.soggettoWithCredenzialiSslList(cSelezionato.getCertificate(), true);
							if(soggettiAutenticatiCheck==null || soggettiAutenticatiCheck.isEmpty() ) {
								details=ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_DETAILS;
							}
						}
					}
					tipoSsl = "certificato";
				}
				
				// Messaggio di errore
				if(soggettoAutenticato!=null){
					String labelSoggettoAutenticato = this.getLabelNomeSoggetto(new IDSoggetto(soggettoAutenticato.getTipo(), soggettoAutenticato.getNome()));
					this.pd.setMessage("Il soggetto "+labelSoggettoAutenticato+" possiede già le credenziali ssl ("+tipoSsl+") indicate."+details);
					return false;
				}
				
			}
			
			
			// Verifico che non sia già associato all'applicativo
			
			String actionConfirm = this.getParameter(Costanti.PARAMETRO_ACTION_CONFIRM);
			boolean promuoviInCorso = false;
			if(actionConfirm != null && actionConfirm.equals(Costanti.PARAMETRO_ACTION_CONFIRM_VALUE_OK)){
				promuoviInCorso = true;
			}
			
			String aggiornatoCertificatoPrecaricatoTmp = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_MULTI_AGGIORNA);
			boolean aggiornatoCertificatoPrecaricato = ServletUtils.isCheckBoxEnabled(aggiornatoCertificatoPrecaricatoTmp);
			
			if(!promuoviInCorso && !aggiornatoCertificatoPrecaricato && cSelezionato!=null &&
					/**saList!=null && !saList.isEmpty()) {*/
					saOld!=null
					) {
				// dovrebbe essere 1 grazie al precedente controllo
				ServizioApplicativo saCheck = saOld; /** saList.get(0);*/ 
				if(saCheck.getInvocazionePorta()!=null && saCheck.getInvocazionePorta().sizeCredenzialiList()>0) {
					int i = 0;
					for (Credenziali c : saCheck.getInvocazionePorta().getCredenzialiList()) {
						Certificate check = ArchiveLoader.load(c.getCertificate());
						if(check.getCertificate().equals(cSelezionato.getCertificate()) && i!=idxCredenziale) {
							this.pd.setMessage("Il certificato selezionato risulta già associato all'applicativo");
							return false;
						}
						i++;
					}
				}
			}
			
			return true;
	
		} catch (Exception e) {
			this.logError("Exception: " + e.getMessage(), e);
			throw new Exception(e.getMessage(),e);
		}
	}
	
	public  List<DataElement> addProprietaToDati(TipoOperazione tipoOp, int size, String nome, String valore, List<DataElement> dati) {

		DataElement de = new DataElement();
		de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROPRIETA);
		de.setType(DataElementType.TITLE);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROP_NOME);
		de.setValue(nome);
		if(TipoOperazione.ADD.equals(tipoOp)){
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		}
		else{
			de.setType(DataElementType.TEXT);
		}
		de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROP_NOME);
		de.setSize(size);
		dati.add(de);

		de = new DataElement();
		de.setLabel(CostantiControlStation.LABEL_PARAMETRO_VALORE);
		de.setName(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROP_VALORE);
		this.core.getLockUtilities().lockProperty(de, valore);
		de.setRequired(true);
		de.setSize(size);
		dati.add(de);

		return dati;
	}
	
	public boolean serviziApplicativiProprietaCheckData(TipoOperazione tipoOp) throws DriverControlStationException {
		try {
			String id = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID);
			int idServizioApplicativo = Integer.parseInt(id);
			String nome = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROP_NOME);
			String valore = this.getLockedParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROP_VALORE, false);

			// Campi obbligatori
			if (nome.equals("") || valore.equals("")) {
				String tmpElenco = "";
				if (nome.equals("")) {
					tmpElenco = ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROP_NOME;
				}
				if (valore.equals("")) {
					if (tmpElenco.equals("")) {
						tmpElenco = ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROP_VALORE;
					} else {
						tmpElenco = tmpElenco + ", " + ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROP_VALORE;
					}
				}
				this.pd.setMessage(MessageFormat.format(ServiziApplicativiCostanti.MESSAGGIO_ERRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX, tmpElenco));
				return false;
			}

			if (nome.indexOf(" ") != -1) {
				this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_NEI_CAMPI_DI_TESTO);
				return false;
			}
			if(!this.checkLength255(nome, ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROP_NOME)) {
				return false;
			}
			
			if( !this.core.getDriverBYOKUtilities().isEnabledBYOK() || !this.core.getDriverBYOKUtilities().isWrappedWithAnyPolicy(valore) ){
				if (valore.indexOf(" ") != -1) {
					this.pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_NEI_CAMPI_DI_TESTO);
					return false;
				}
				if(!this.checkLength4000(valore, ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROP_VALORE)) {
					return false;
				}
			}

			// Se tipoOp = add, controllo che la property non sia gia'
			// stata
			// registrata per l'applicativo
			if (tipoOp.equals(TipoOperazione.ADD)) {
				boolean giaRegistrato = false;
				ServizioApplicativo sa = this.saCore.getServizioApplicativo(idServizioApplicativo);
				String nomeporta = sa.getNome();

				for (int i = 0; i < sa.sizeProprietaList(); i++) {
					Proprieta tmpProp = sa.getProprieta(i);
					if (nome.equals(tmpProp.getNome())) {
						giaRegistrato = true;
						break;
					}
				}

				if (giaRegistrato) {
					this.pd.setMessage(MessageFormat.format(
							ServiziApplicativiCostanti.MESSAGGIO_ERRORE_LA_PROPRIETA_XX_E_GIA_STATO_ASSOCIATA_AL_SA_YY, nome,
							nomeporta));
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			this.logError("Exception: " + e.getMessage(), e);
			throw new DriverControlStationException(e.getMessage(),e);
		}
	}
	
	public void prepareServiziApplicativiProprietaList(ServizioApplicativo sa, ConsoleSearch ricerca, List<Proprieta> lista) throws DriverControlStationException {
		try {
			boolean modalitaCompleta = this.isModalitaCompleta();
			
			String idProvider = this.getParametroLong(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione
			Integer parentSA = ServletUtils.getIntegerAttributeFromSession(ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT, this.session, this.request);
			if(parentSA == null) parentSA = ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE;
			Boolean useIdSogg = parentSA == ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_SOGGETTO;

			IDSoggetto idSoggettoProprietario = new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario());
			Soggetto soggettoProprietario = this.soggettiCore.getSoggettoRegistro(idSoggettoProprietario);
			String dominio = this.pddCore.isPddEsterna(soggettoProprietario.getPortaDominio()) ? SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE : SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE;
		
			List<Parameter> parametersServletSAChange = new ArrayList<>();
			Parameter pIdSA = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID, sa.getId()+"");
			parametersServletSAChange.add(pIdSA);
			Parameter pIdSoggettoSA = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, sa.getIdSoggetto()+"");
			parametersServletSAChange.add(pIdSoggettoSA);
			if(dominio != null) {
				Parameter pDominio = new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO, dominio);
				parametersServletSAChange.add(pDominio);
			}
			
			if(useIdSogg!=null && useIdSogg.booleanValue()){
				Parameter pProvider = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, idProvider); 
				List<Parameter> parametersServletSAChangeProvider = new ArrayList<>();
				parametersServletSAChangeProvider.add(pProvider);
				parametersServletSAChangeProvider.addAll(parametersServletSAChange);
				ServletUtils.addListElementIntoSession(this.request, this.session, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_PROPRIETA, parametersServletSAChangeProvider.toArray(new Parameter[parametersServletSAChangeProvider.size()]));
			}else 
				ServletUtils.addListElementIntoSession(this.request, this.session, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_PROPRIETA, parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()]));

			int idLista = Liste.SERVIZI_APPLICATIVI_PROP;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
			
			// Prendo il soggetto
			String tmpTitle = null;
			String protocolloSoggetto = null;
			boolean supportAsincroni = true;
			if(useIdSogg!=null && useIdSogg.booleanValue()){
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
				
				if(!supportAsincroni &&
					this.isModalitaAvanzata()){
					supportAsincroni = this.core.isElenchiSAAsincroniNonSupportatiVisualizzaRispostaAsincrona();
				}
			}
			if(supportAsincroni) {
				// nop
			}
			
			// setto la barra del titolo
			String labelApplicativi = ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI;
			String labelApplicativiDi = ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_DI;
			if(!modalitaCompleta) {
				labelApplicativi = ServiziApplicativiCostanti.LABEL_APPLICATIVI;
				labelApplicativiDi = ServiziApplicativiCostanti.LABEL_PARAMETRO_APPLICATIVI_DI;
			}
			
			List<Parameter> lstParam = new ArrayList<>();
			if(useIdSogg==null || !useIdSogg.booleanValue()){
				lstParam.add(new Parameter(labelApplicativi, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST));
				lstParam.add(new Parameter(sa.getNome(), ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE, parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])));
			} else {
				String provider = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
				lstParam.add(new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
				lstParam.add(new Parameter(labelApplicativiDi + tmpTitle, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST, new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,provider)));
				lstParam.add(new Parameter(sa.getNome(), ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE, parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])));
			}

			this.pd.setSearchLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_NOME);
			if(search.equals("")){
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROPRIETA, null));
			}else{
				lstParam.add(new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROPRIETA,
						ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_PROPRIETA_LIST, parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])));
				lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE_RISULTATI_RICERCA, null));
			}

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(this.pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROPRIETA, search);
			}

			// setto le label delle colonne
			String valueLabel = ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROP_VALORE;
			String[] labels = { ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_PROP_NOME, valueLabel };
			this.pd.setLabels(labels);

			// preparo i dati
			List<List<DataElement>> dati = new ArrayList<>();

			if (lista != null) {
				Iterator<Proprieta> it = lista.iterator();
				while (it.hasNext()) {
					Proprieta ssp = it.next();

					List<DataElement> e = new ArrayList<>();

					Parameter pNomeProprieta = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROP_NOME, ssp.getNome());
					List<Parameter> parametersServletProprietaChange = new ArrayList<>();
					parametersServletProprietaChange.add(pNomeProprieta);
					parametersServletProprietaChange.addAll(parametersServletSAChange);
				
					DataElement de = new DataElement();
					de.setUrl(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_PROPRIETA_CHANGE, 
							parametersServletProprietaChange.toArray(new Parameter[parametersServletProprietaChange.size()]));
					de.setValue(ssp.getNome());
					de.setIdToRemove(ssp.getNome());
					e.add(de);

					de = new DataElement();
					if(ssp.getValore()!=null) {
						if(StringUtils.isNotEmpty(ssp.getValore()) &&
								BYOKManager.isEnabledBYOK() &&
								this.core.getDriverBYOKUtilities().isWrappedWithAnyPolicy(ssp.getValore())) {
							de.setValue(CostantiControlStation.VALORE_CIFRATO);
						}
						else {
							de.setValue(ssp.getValore());
						}
					}
					e.add(de);

					dati.add(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

		} catch (Exception e) {
			this.logError("Exception: " + e.getMessage(), e);
			throw new DriverControlStationException(e.getMessage(),e);
		}
	}

	public void addDescrizioneVerificaConnettivitaToDati(List<DataElement> dati, Connettore connettore,
			String server, String aliasConnettore) throws Exception {
						
		List<Parameter> downloadCertServerParameters = new ArrayList<>();
		
		downloadCertServerParameters.add(new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_CERTIFICATI_SERVER_ID_CONNETTORE, connettore.getId().longValue()+""));
		
		this.addDescrizioneVerificaConnettivitaToDati(dati, connettore,
				server, false, aliasConnettore,
				downloadCertServerParameters,
				true, false);
		
	}
}
