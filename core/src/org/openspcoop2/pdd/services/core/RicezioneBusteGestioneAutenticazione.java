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
package org.openspcoop2.pdd.services.core;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.GestioneTokenAutenticazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.transazioni.utils.CredenzialiMittente;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.GestoreRichieste;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.autenticazione.GestoreAutenticazione;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazione;
import org.openspcoop2.pdd.core.autenticazione.pa.EsitoAutenticazionePortaApplicativa;
import org.openspcoop2.pdd.core.credenziali.Credenziali;
import org.openspcoop2.pdd.core.handlers.InRequestContext;
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.core.transazioni.TransactionDeletedException;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.Tracciamento;
import org.openspcoop2.pdd.services.error.RicezioneBusteExternalErrorGenerator;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.engine.validator.Validatore;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.BustaRawContent;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.CostantiProtocollo;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.state.RequestFruitore;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.tracciamento.EsitoElaborazioneMessaggioTracciato;
import org.openspcoop2.protocol.sdk.tracciamento.TracciamentoException;
import org.openspcoop2.protocol.utils.ModIUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;

/**
 * RicezioneBusteGestioneAutenticazione
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RicezioneBusteGestioneAutenticazione {

	private MsgDiagnostico msgDiag;
	private Logger logCore;
	private Tracciamento tracciamento;
	private String correlazioneApplicativa;
	private BustaRawContent<?> soapHeaderElement;
	private Busta bustaRichiesta;
	
	private PortaApplicativa pa;
	private IDPortaApplicativa idPA;
	private PortaDelegata pd;
	private IDPortaDelegata idPD;
	
	private IDSoggetto soggettoFruitore;
	private Credenziali credenziali;
	private String servizioApplicativoFruitore;
	private boolean autenticazioneOpzionale;
	private GestioneTokenAutenticazione gestioneTokenAutenticazione;
	
	private OpenSPCoop2Message requestMessage;
	
	private boolean asincronoSimmetricoRisposta;
	private boolean functionAsRouter;
	
	private RicezioneBusteContext msgContext;
	private RicezioneBusteExternalErrorGenerator generatoreErrore;
	private InRequestContext inRequestContext;
	
	private ConfigurazionePdDManager configurazionePdDReader;
	private RegistroServiziManager registroServiziReader;
	
	private PdDContext pddContext;
	private String idTransazione;
	private IDSoggetto identitaPdD;
	private IOpenSPCoopState openspcoopstate;
	private Transaction transaction;
	private RequestInfo requestInfo;
	
	private IProtocolFactory<?> protocolFactory;
	private Validatore validatore;
	
	private RicezioneBusteParametriGenerazioneBustaErrore parametriGenerazioneBustaErrore;
	private RicezioneBusteParametriInvioBustaErrore parametriInvioBustaErrore;
	
	public RicezioneBusteGestioneAutenticazione(MsgDiagnostico msgDiag, Logger logCore,
			Tracciamento tracciamento, String correlazioneApplicativa, BustaRawContent<?> soapHeaderElement, Busta bustaRichiesta,
			PortaApplicativa pa, IDPortaApplicativa idPA, PortaDelegata pd, IDPortaDelegata idPD,
			IDSoggetto soggettoFruitore, Credenziali credenziali, String servizioApplicativoFruitore, GestioneTokenAutenticazione gestioneTokenAutenticazione,
			OpenSPCoop2Message requestMessage,
			boolean asincronoSimmetricoRisposta, boolean functionAsRouter,
			RicezioneBusteContext msgContext, RicezioneBusteExternalErrorGenerator generatoreErrore, InRequestContext inRequestContext,
			ConfigurazionePdDManager configurazionePdDReader, RegistroServiziManager registroServiziReader,
			PdDContext pddContext, String idTransazione, IDSoggetto identitaPdD,
			IOpenSPCoopState openspcoopstate, Transaction transaction, RequestInfo requestInfo,
			IProtocolFactory<?> protocolFactory, Validatore validatore,
			RicezioneBusteParametriGenerazioneBustaErrore parametriGenerazioneBustaErrore, RicezioneBusteParametriInvioBustaErrore parametriInvioBustaErrore) {
		this.msgDiag = msgDiag;
		this.logCore = logCore;
		
		this.tracciamento = tracciamento;
		this.correlazioneApplicativa = correlazioneApplicativa;
		this.soapHeaderElement = soapHeaderElement;
		this.bustaRichiesta = bustaRichiesta;
		
		this.pa = pa;
		this.idPA = idPA;
		this.pd = pd;
		this.idPD = idPD;
		
		this.soggettoFruitore = soggettoFruitore;
		this.credenziali = credenziali;
		this.servizioApplicativoFruitore = servizioApplicativoFruitore;
		this.gestioneTokenAutenticazione = gestioneTokenAutenticazione;
		
		this.requestMessage = requestMessage;
		
		this.asincronoSimmetricoRisposta = asincronoSimmetricoRisposta;
		this.functionAsRouter = functionAsRouter;
		
		this.msgContext = msgContext;
		this.generatoreErrore = generatoreErrore;
		this.inRequestContext = inRequestContext;
		
		this.configurazionePdDReader = configurazionePdDReader;
		this.registroServiziReader = registroServiziReader;
		
		this.pddContext = pddContext;
		this.idTransazione = idTransazione;
		this.identitaPdD = identitaPdD;
		this.openspcoopstate = openspcoopstate;
		this.transaction = transaction;
		this.requestInfo = requestInfo; 
		
		this.protocolFactory = protocolFactory;
		this.validatore = validatore;
		
		this.parametriGenerazioneBustaErrore = parametriGenerazioneBustaErrore;
		this.parametriInvioBustaErrore = parametriInvioBustaErrore;
	}
	
	// Result
	private boolean soggettoFruitoreIdentificatoTramiteProtocollo = false;
	private boolean soggettoAutenticato = false;
	private boolean supportatoAutenticazioneSoggetti = false;
	private IDServizioApplicativo idApplicativoToken = null;

	public boolean isSoggettoFruitoreIdentificatoTramiteProtocollo() {
		return this.soggettoFruitoreIdentificatoTramiteProtocollo;
	}
	public boolean isSoggettoAutenticato() {
		return this.soggettoAutenticato;
	}
	public boolean isSupportatoAutenticazioneSoggetti() {
		return this.supportatoAutenticazioneSoggetti;
	}
	public IDServizioApplicativo getIdApplicativoToken() {
		return this.idApplicativoToken;
	}
	public IDSoggetto getSoggettoFruitore() {
		return this.soggettoFruitore;
	}
	public String getServizioApplicativoFruitore() {
		return this.servizioApplicativoFruitore;
	}
	public boolean isAutenticazioneOpzionale() {
		return this.autenticazioneOpzionale;
	}
	
	public boolean process() throws TracciamentoException, ProtocolException {
		
		OpenSPCoop2Properties propertiesReader = OpenSPCoop2Properties.getInstance();
		
		RequestFruitore requestFruitoreTrasporto = null;
		IDServizioApplicativo idSAFruitore = null;
		ServizioApplicativo sa = null;
		Soggetto soggettoFruitoreObject = null;
		
		if(this.soggettoFruitore==null && this.validatore.getSoggettoMittente()!=null) {
			this.soggettoFruitoreIdentificatoTramiteProtocollo = true;
		}
		this.soggettoFruitore = this.validatore.getSoggettoMittente();

		// ACCEDE DUE VOLTE ALLA CACHE DEI FRUITORI, QUA CON SA NULL E POI SI RIPETE CON SA VALORIZZATO SE E' ATTIVA L'AUTENTICAZIONE
		/**
		requestFruitore = GestoreRichieste.readFruitore(requestInfo, this.soggettoFruitore, idSAFruitore);
		Soggetto soggettoFruitoreObject = null;
		if(this.soggettoFruitore!=null) {
			try {
				soggettoFruitoreObject = this.registroServiziReader.getSoggetto(this.soggettoFruitore, null, requestInfo);
				Map<String, String> configProperties = this.registroServiziReader.getProprietaConfigurazione(soggettoFruitoreObject);
	            if (configProperties != null && !configProperties.isEmpty()) {
	            	this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_SOGGETTO_FRUITORE, configProperties);
				}	
			}catch(Throwable t) {}	
		}
		if(useRequestThreadContext && soggettoFruitoreObject!=null) {
			try {
				requestInfo.getRequestThreadContext().setSoggettoFruitoreRegistry(soggettoFruitoreObject);
			}catch(Throwable t) {}	
			try {
				org.openspcoop2.core.config.Soggetto soggettoConfig = this.configurazionePdDReader.getSoggetto(this.soggettoFruitore, requestInfo); 
				requestInfo.getRequestThreadContext().setSoggettoFruitoreConfig(soggettoConfig);
			}catch(Throwable t) {}	
			try {
				String idPorta = this.configurazionePdDReader.getIdentificativoPorta(this.soggettoFruitore, this.protocolFactory, requestInfo);
				requestInfo.getRequestThreadContext().setSoggettoFruitoreIdentificativoPorta(idPorta);
			}catch(Throwable t) {}	
			try {
				boolean soggettoVirtualeFRU = this.configurazionePdDReader.isSoggettoVirtuale(this.soggettoFruitore, requestInfo);
				requestInfo.getRequestThreadContext().setSoggettoFruitoreSoggettoVirtuale(soggettoVirtualeFRU);
			}catch(Throwable t) {}	
			try {
				if(requestInfo.getRequestThreadContext().getSoggettoFruitoreRegistry()!=null) {
					if(requestInfo.getRequestThreadContext().getSoggettoFruitoreRegistry().getPortaDominio()!=null &&
							StringUtils.isNotEmpty(requestInfo.getRequestThreadContext().getSoggettoFruitoreRegistry().getPortaDominio())) {
						PortaDominio pdd = this.registroServiziReader.getPortaDominio(requestInfo.getRequestThreadContext().getSoggettoFruitoreRegistry().getPortaDominio(), null, requestInfo);
						requestInfo.getRequestThreadContext().setSoggettoFruitorePddReaded(true);
						requestInfo.getRequestThreadContext().setSoggettoFruitorePdd(pdd);
					}
					else {
						requestInfo.getRequestThreadContext().setSoggettoFruitorePddReaded(true);
					}
				}
			}catch(Throwable t) {}	
			try {
				String impl = this.registroServiziReader.getImplementazionePdD(this.soggettoFruitore, null, requestInfo);
				requestInfo.getRequestConfig().setSoggettoFruitoreImplementazionePdd(impl);
			}catch(Throwable t) {}	
		}*/

		ParametriAutenticazione parametriAutenticazione = null;
		ServizioApplicativo saApplicativoToken = null;
		Soggetto soggettoToken = null;
		RequestFruitore requestFruitoreToken = null;
		if(!this.functionAsRouter){
			this.supportatoAutenticazioneSoggetti = this.protocolFactory.createProtocolConfiguration().isSupportoAutenticazioneSoggetti();
			String credenzialeTrasporto = null;
			String tipoAutenticazione = null;
			
			org.openspcoop2.pdd.core.autenticazione.pa.DatiInvocazionePortaApplicativa datiInvocazioneAutenticazione = new org.openspcoop2.pdd.core.autenticazione.pa.DatiInvocazionePortaApplicativa();
			datiInvocazioneAutenticazione.setInfoConnettoreIngresso(this.inRequestContext.getConnettore());
			if(this.openspcoopstate!=null) {
				datiInvocazioneAutenticazione.setState(this.openspcoopstate.getStatoRichiesta());
			}
			datiInvocazioneAutenticazione.setIdPA(this.idPA);
			datiInvocazioneAutenticazione.setPa(this.pa);	
			datiInvocazioneAutenticazione.setIdPD(this.idPD);
			datiInvocazioneAutenticazione.setPd(this.pd);		
			
			if(this.supportatoAutenticazioneSoggetti && !this.asincronoSimmetricoRisposta && (this.pa!=null || this.pd!=null)){
			
				this.msgDiag.mediumDebug("Autenticazione del soggetto...");
				try{
					if(this.pa!=null){
						tipoAutenticazione = this.configurazionePdDReader.getAutenticazione(this.pa);
						this.autenticazioneOpzionale = this.configurazionePdDReader.isAutenticazioneOpzionale(this.pa);
						parametriAutenticazione = new ParametriAutenticazione(this.pa.getProprietaAutenticazioneList());
					}
					else{
						tipoAutenticazione = this.configurazionePdDReader.getAutenticazione(this.pd);
						this.autenticazioneOpzionale = this.configurazionePdDReader.isAutenticazioneOpzionale(this.pd);
						parametriAutenticazione = new ParametriAutenticazione(this.pd.getProprietaAutenticazioneList());
					}
				}catch(Exception exception){
					// ignore
				}
				this.msgContext.getIntegrazione().setTipoAutenticazione(tipoAutenticazione);
				this.msgContext.getIntegrazione().setAutenticazioneOpzionale(this.autenticazioneOpzionale);
				if(tipoAutenticazione!=null){
					this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_AUTENTICAZIONE, tipoAutenticazione);
				}
				/** String soggettoFruitore = CostantiPdD.SOGGETTO_ANONIMO; */
				if (CostantiConfigurazione.AUTENTICAZIONE_NONE.equalsIgnoreCase(tipoAutenticazione)) {
					this.msgDiag.logPersonalizzato("autenticazioneDisabilitata");
				}	
				else{
					
					// annullo eventuale applicativo letto da header di integrazione (vale solo in caso di autenticazione 'none')
					if(this.servizioApplicativoFruitore!=null) {
						this.servizioApplicativoFruitore = null;
						this.parametriGenerazioneBustaErrore.setServizioApplicativoFruitore(this.servizioApplicativoFruitore);
						this.parametriInvioBustaErrore.setServizioApplicativoFruitore(this.servizioApplicativoFruitore);
						this.generatoreErrore.updateInformazioniCooperazione(this.servizioApplicativoFruitore);
						this.msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE, this.servizioApplicativoFruitore);
						this.msgContext.getIntegrazione().setServizioApplicativoFruitore(this.servizioApplicativoFruitore);
					}
					
					this.transaction.getTempiElaborazione().startAutenticazione();
					try {
					
						this.msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_MITTENTE_MSG, this.credenziali.toString());
						this.msgDiag.logPersonalizzato("autenticazioneInCorso");
						
						ErroreCooperazione erroreCooperazione = null;
						ErroreIntegrazione erroreIntegrazione = null;
						Exception eAutenticazione = null;
						OpenSPCoop2Message errorMessageAutenticazione = null;
						String wwwAuthenticateErrorHeader = null;
						boolean detailsSet = false;
						IntegrationFunctionError integrationFunctionError = null;
						boolean erroreMissmatchSoggettoProtocolloConCredenziali = false;
						Exception eMissmatchSoggettoProtocolloConCredenziali = null;
						boolean erroreMissmatchSoggettoProtocolloConCredenzialiAuthorization = false;
						try {						
							EsitoAutenticazionePortaApplicativa esito = 
									GestoreAutenticazione.verificaAutenticazionePortaApplicativa(tipoAutenticazione,
											datiInvocazioneAutenticazione, parametriAutenticazione,
											this.pddContext, this.protocolFactory, this.requestMessage); 
							CostantiPdD.addKeywordInCache(this.msgDiag, esito.isEsitoPresenteInCache(),
									this.pddContext, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_AUTENTICAZIONE);
							if(esito.getDetails()==null){
								this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, "");
							}else{
								this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, " ("+esito.getDetails()+")");
							}
							detailsSet = true;
							credenzialeTrasporto = esito.getCredential();
							if(esito.isClientAuthenticated() && credenzialeTrasporto!=null && esito.isEnrichPrincipal()) {
								// Aggiorno Principal
						    	if(datiInvocazioneAutenticazione.getInfoConnettoreIngresso()!=null && datiInvocazioneAutenticazione.getInfoConnettoreIngresso().getUrlProtocolContext()!=null && 
						    			datiInvocazioneAutenticazione.getInfoConnettoreIngresso().getUrlProtocolContext().getCredential()!=null &&
						    					datiInvocazioneAutenticazione.getInfoConnettoreIngresso().getUrlProtocolContext().getCredential().getPrincipal()==null) {
						    		datiInvocazioneAutenticazione.getInfoConnettoreIngresso().getUrlProtocolContext().getCredential().setPrincipal(credenzialeTrasporto);
						    	}
						    	if(datiInvocazioneAutenticazione.getInfoConnettoreIngresso()!=null && datiInvocazioneAutenticazione.getInfoConnettoreIngresso().getCredenziali()!=null &&
						    			datiInvocazioneAutenticazione.getInfoConnettoreIngresso().getCredenziali().getPrincipal()==null) {
						    		datiInvocazioneAutenticazione.getInfoConnettoreIngresso().getCredenziali().setPrincipal(credenzialeTrasporto);
						    	}
							}
							
							if(credenzialeTrasporto!=null) {
								this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.IDENTIFICATIVO_AUTENTICATO, credenzialeTrasporto);
							}
							
							String fullCredential = esito.getFullCredential();
							if(fullCredential!=null && !"".equals(fullCredential)) {
								String c = this.transaction.getCredenziali();
								if(c!=null && !"".equals(c)) {
									c = c + "\n" + fullCredential;
								}
								else {
									c = fullCredential;
								}
								this.transaction.setCredenziali(c);
							}
							
							if(!esito.isClientAuthenticated()) {
								erroreCooperazione = esito.getErroreCooperazione();
								erroreIntegrazione = esito.getErroreIntegrazione();
								eAutenticazione = esito.getEccezioneProcessamento();
								errorMessageAutenticazione = esito.getErrorMessage();
								wwwAuthenticateErrorHeader = esito.getWwwAuthenticateErrorHeader();
								integrationFunctionError = esito.getIntegrationFunctionError();
								this.msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_MITTENTE_MSG, this.credenziali.toString(
										propertiesReader.isAutenticazioneBasicLogPassword() ? Credenziali.SHOW_BASIC_PASSWORD : !Credenziali.SHOW_BASIC_PASSWORD,
										!Credenziali.SHOW_ISSUER,
										!Credenziali.SHOW_DIGEST_CLIENT_CERT,
										!Credenziali.SHOW_SERIAL_NUMBER_CLIENT_CERT)); // Aggiungo la password se presente, evito inoltre di stampare l'issuer e altre info del cert nei diagnostici
							}
							else {
								if(esito.isClientIdentified()) {
									this.soggettoAutenticato = true;
									IDSoggetto idSoggettoFruitoreIdentificato = esito.getIdSoggetto();
									boolean intermediarioModi = false;
									if(this.soggettoFruitore!=null &&  this.soggettoFruitore.getNome()!=null && this.soggettoFruitore.getTipo()!=null
										&&	
										!this.soggettoFruitore.equals(idSoggettoFruitoreIdentificato)) {
										if(Costanti.MODIPA_PROTOCOL_NAME.equals(this.protocolFactory.getProtocol())) {
											Soggetto soggettoCanale = RegistroServiziManager.getInstance(this.openspcoopstate!=null ? this.openspcoopstate.getStatoRichiesta(): null).
												getSoggetto(idSoggettoFruitoreIdentificato, null, this.requestInfo);
											intermediarioModi= ModIUtils.isSoggettoCanaleIntermediario(soggettoCanale, this.logCore);
										}
										if(intermediarioModi) {
											registraIntermediario(idSoggettoFruitoreIdentificato, this.msgDiag, this.idTransazione, this.transaction, this.pddContext);
										}
										else {
											erroreMissmatchSoggettoProtocolloConCredenziali = true;
											if(Costanti.MODIPA_PROTOCOL_NAME.equals(this.protocolFactory.getProtocol()) && this.bustaRichiesta!=null && this.bustaRichiesta.getServizioApplicativoFruitore()!=null) {
												// Sul profilo ModI entrambi le autenticazioni hanno avuto successo, e si tratta di un discorso di autorizzazione
												// poichè non viene concesso di ricevere un applicativo di un soggetto differente da quello del canale
												this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTORIZZAZIONE, "true");
												erroreMissmatchSoggettoProtocolloConCredenzialiAuthorization = true;
												IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
												idServizioApplicativo.setNome(this.bustaRichiesta.getServizioApplicativoFruitore());
												idServizioApplicativo.setIdSoggettoProprietario(this.soggettoFruitore);
												String msgError = ModIUtils.getMessaggioErroreDominioCanaleDifferenteDominioApplicativo(idServizioApplicativo, idSoggettoFruitoreIdentificato);
												eMissmatchSoggettoProtocolloConCredenziali = new Exception(msgError);
												throw eMissmatchSoggettoProtocolloConCredenziali;
											}
											this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTENTICAZIONE, "true");
											eMissmatchSoggettoProtocolloConCredenziali = new Exception(IDENTIFICAZIONE_SOGGETTO_TRAMITE_PROFILO+this.soggettoFruitore
													+"' differente da quello identificato tramite il processo di autenticazione '"+idSoggettoFruitoreIdentificato+"'");
											throw eMissmatchSoggettoProtocolloConCredenziali;
										}
									}
									if(!intermediarioModi) {
										this.soggettoFruitore = idSoggettoFruitoreIdentificato;
									}
									if(esito.getIdServizioApplicativo()!=null) {
										this.servizioApplicativoFruitore = esito.getIdServizioApplicativo().getNome();
										this.parametriGenerazioneBustaErrore.setServizioApplicativoFruitore(this.servizioApplicativoFruitore);
										this.parametriInvioBustaErrore.setServizioApplicativoFruitore(this.servizioApplicativoFruitore);
										this.generatoreErrore.updateInformazioniCooperazione(this.servizioApplicativoFruitore);
										this.msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE, this.servizioApplicativoFruitore);
										this.msgContext.getIntegrazione().setServizioApplicativoFruitore(this.servizioApplicativoFruitore);
										
										idSAFruitore = new IDServizioApplicativo();
										idSAFruitore.setIdSoggettoProprietario(this.soggettoFruitore);
										idSAFruitore.setNome(this.servizioApplicativoFruitore);
										try {
											requestFruitoreTrasporto = GestoreRichieste.readFruitoreTrasporto(this.requestInfo, this.soggettoFruitore, idSAFruitore);
											sa = this.configurazionePdDReader.getServizioApplicativo(idSAFruitore, this.requestInfo);
											Map<String, String> configProperties = this.configurazionePdDReader.getProprietaConfigurazione(sa);
								            if (configProperties != null && !configProperties.isEmpty()) {
								            	this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_APPLICATIVO, configProperties);
											}	
										}catch(Throwable t) {
											// ignore
										}
									}
									this.msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_MITTENTE_MSG, ""); // per evitare di visualizzarle anche nei successivi diagnostici
									this.msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI, "");
								}
								else {
									// l'errore puo' non esserci se l'autenticazione utilizzata non prevede una identificazione obbligatoria
									erroreCooperazione = esito.getErroreCooperazione();
									erroreIntegrazione = esito.getErroreIntegrazione();
									eAutenticazione = esito.getEccezioneProcessamento();
									errorMessageAutenticazione = esito.getErrorMessage();
									wwwAuthenticateErrorHeader = esito.getWwwAuthenticateErrorHeader();
									integrationFunctionError = esito.getIntegrationFunctionError();
									
									// evito comunque di ripresentarle nei successivi diagnostici, l'informazione l'ho gia' visualizzata nei diagnostici dell'autenticazione
									this.msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_MITTENTE_MSG, ""); // per evitare di visualizzarle anche nei successivi diagnostici
									this.msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI, "");
									
									if(this.soggettoFruitore!=null && soggettoFruitoreObject==null) {
										try {
											if(requestFruitoreTrasporto==null) {
												requestFruitoreTrasporto = GestoreRichieste.readFruitoreTrasporto(this.requestInfo, this.soggettoFruitore, idSAFruitore);
											}
										}catch(Throwable t) {
											this.logCore.error(ERRORE_LETTURA_SOGGETTO+this.soggettoFruitore+OGGETTO_REQUEST_INFO+t.getMessage(),t);
										}
										try {
											soggettoFruitoreObject = this.registroServiziReader.getSoggetto(this.soggettoFruitore, null, this.requestInfo);
										}catch(Throwable t) {
											this.logCore.error(ERRORE_LETTURA_SOGGETTO+this.soggettoFruitore+DAL_REGISTRO+t.getMessage(),t);
										}
									}
									
									if(this.soggettoFruitoreIdentificatoTramiteProtocollo &&
											this.soggettoFruitore!=null &&  this.soggettoFruitore.getNome()!=null && this.soggettoFruitore.getTipo()!=null &&
											soggettoFruitoreObject!=null &&
											soggettoFruitoreObject.sizeCredenzialiList()>0) {
										erroreMissmatchSoggettoProtocolloConCredenziali = true;
										this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTENTICAZIONE, "true");
										throw new CoreException(IDENTIFICAZIONE_SOGGETTO_TRAMITE_PROFILO+this.soggettoFruitore
												+"' registrato con credenziali differenti da quelle ricevute");
									}
								}
							}
							
							if (erroreIntegrazione != null || erroreCooperazione!=null) {
								if(!this.autenticazioneOpzionale){
									this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTENTICAZIONE, "true");
								}
							}
							else {
								this.msgDiag.logPersonalizzato("autenticazioneEffettuata");							
							}
							
						} catch (Exception e) {
							CostantiPdD.addKeywordInCache(this.msgDiag, false,
									this.pddContext, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_AUTENTICAZIONE);
							erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento("["+ RicezioneBuste.ID_MODULO+ PROCESSO_AUTENTICAZIONE
											+ tipoAutenticazione + "] fallito, " + e.getMessage(),CodiceErroreIntegrazione.CODICE_503_AUTENTICAZIONE);
							erroreCooperazione = null;
							eAutenticazione = e;
							this.logCore.error("processo di autenticazione ["
									+ tipoAutenticazione + "] fallito, " + e.getMessage(),e);
							integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
						}
						if (erroreIntegrazione != null || erroreCooperazione!=null) {
							if(!detailsSet) {
								this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, "");
							}
							String descrizioneErrore = null;
							try{
								if(erroreCooperazione != null)
									descrizioneErrore = erroreCooperazione.getDescrizione(this.protocolFactory);
								else
									descrizioneErrore = erroreIntegrazione.getDescrizione(this.protocolFactory);
								if(descrizioneErrore!=null && descrizioneErrore.startsWith(CostantiProtocollo.PREFISSO_AUTENTICAZIONE_FALLITA) &&
										descrizioneErrore.length()>CostantiProtocollo.PREFISSO_AUTENTICAZIONE_FALLITA.length()) {
									String dettaglioErroreInterno = descrizioneErrore.substring(CostantiProtocollo.PREFISSO_AUTENTICAZIONE_FALLITA.length());
									CostantiPdD.addKeywordAutenticazioneFallita(this.msgDiag, dettaglioErroreInterno, this.pddContext, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_AUTENTICAZIONE_FALLITA);
								}
								else {
									this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, descrizioneErrore);
								}
							}catch(Exception e){
								this.logCore.error(DESCRIZIONE_ERROR+e.getMessage(),e);
							}
							String errorMsg =  "Riscontrato errore durante il processo di Autenticazione per il messaggio con identificativo di transazione ["+this.idTransazione+"]: "+descrizioneErrore;
							if(this.autenticazioneOpzionale && !erroreMissmatchSoggettoProtocolloConCredenziali){
								this.msgDiag.logPersonalizzato("autenticazioneFallita.opzionale");
								if(eAutenticazione!=null){
									this.logCore.debug(errorMsg,eAutenticazione);
								}
								else{
									this.logCore.debug(errorMsg);
								}
							}
							else{
								this.msgDiag.logPersonalizzato("autenticazioneFallita");
								if(eAutenticazione!=null){
									this.logCore.error(errorMsg,eAutenticazione);
								}
								else{
									this.logCore.error(errorMsg);
								}
							}
							
							if(!this.autenticazioneOpzionale || erroreMissmatchSoggettoProtocolloConCredenziali){
							
								// Tracciamento richiesta: non ancora registrata
								if(this.msgContext.isTracciamentoAbilitato()){
									EsitoElaborazioneMessaggioTracciato esitoTraccia = 
											EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("["+ RicezioneBuste.ID_MODULO+ PROCESSO_AUTENTICAZIONE
												+ tipoAutenticazione + PROCESSO_FALLITO);
									this.tracciamento.registraRichiesta(this.requestMessage,null,this.soapHeaderElement,this.bustaRichiesta,esitoTraccia,
											Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
											this.correlazioneApplicativa);
								}
								
								if(this.msgContext.isGestioneRisposta()){
		
									if(errorMessageAutenticazione!=null) {
										this.msgContext.setMessageResponse(errorMessageAutenticazione);
									}
									else {
										RicezioneBusteGeneratoreBustaErrore ricezioneBusteGeneratoreBustaErrore = new RicezioneBusteGeneratoreBustaErrore(this.msgContext, this.generatoreErrore);
									
										this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);	
										if(erroreIntegrazione != null){
											this.parametriGenerazioneBustaErrore.setErroreIntegrazione(erroreIntegrazione);
										}
										else{
											this.parametriGenerazioneBustaErrore.setErroreCooperazione(erroreCooperazione);
										}
			
										OpenSPCoop2Message errorOpenSPCoopMsg = null;
										if(erroreMissmatchSoggettoProtocolloConCredenziali) {
											if(erroreMissmatchSoggettoProtocolloConCredenzialiAuthorization) {
												integrationFunctionError = IntegrationFunctionError.AUTHORIZATION;
											}
											else {
												integrationFunctionError = IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS;
											}
											this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
											String msgErroreDetailNonValide = eAutenticazione!=null ? eAutenticazione.getMessage() : 
												IDENTIFICAZIONE_SOGGETTO_TRAMITE_PROFILO+this.soggettoFruitore+"' registrato con credenziali differenti da quelle ricevute";
											if(eMissmatchSoggettoProtocolloConCredenziali!=null) {
												msgErroreDetailNonValide = eMissmatchSoggettoProtocolloConCredenziali.getMessage();
											}
											if(erroreMissmatchSoggettoProtocolloConCredenzialiAuthorization) {
												this.parametriGenerazioneBustaErrore.
													setErroreCooperazione(ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(msgErroreDetailNonValide, CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA));
											}
											else {
												this.parametriGenerazioneBustaErrore.
													setErroreCooperazione(ErroriCooperazione.AUTENTICAZIONE_FALLITA_CREDENZIALI_FORNITE_NON_CORRETTE.getErroreCredenzialiForniteNonCorrette(msgErroreDetailNonValide));
											}
											errorOpenSPCoopMsg = ricezioneBusteGeneratoreBustaErrore.generaBustaErroreValidazione(this.parametriGenerazioneBustaErrore);
										}
										else if(erroreCooperazione!=null){
											if(CodiceErroreCooperazione.MITTENTE_NON_PRESENTE.equals(erroreCooperazione.getCodiceErrore()) ||
													CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.equals(erroreCooperazione.getCodiceErrore()) ||
													CodiceErroreCooperazione.MITTENTE.equals(erroreCooperazione.getCodiceErrore())) {
												if(integrationFunctionError==null) {
													integrationFunctionError = IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND;
												}
												this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
												errorOpenSPCoopMsg = ricezioneBusteGeneratoreBustaErrore.generaBustaErroreValidazione(this.parametriGenerazioneBustaErrore);
											}
											else if(CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO.equals(erroreCooperazione.getCodiceErrore()) ||
													CodiceErroreCooperazione.MITTENTE_NON_VALIDO.equals(erroreCooperazione.getCodiceErrore())) {
												if(integrationFunctionError==null) {
													integrationFunctionError = IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS;
												}
												this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
												errorOpenSPCoopMsg = ricezioneBusteGeneratoreBustaErrore.generaBustaErroreValidazione(this.parametriGenerazioneBustaErrore);
											}
											else {
												if(integrationFunctionError==null) {
													integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
												}
												this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
												errorOpenSPCoopMsg = ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,eAutenticazione);
											}
										}
										else {
											if(integrationFunctionError==null) {
												integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
											}
											this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
											errorOpenSPCoopMsg = ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,eAutenticazione);
										}	
										
										if(wwwAuthenticateErrorHeader!=null) {
											errorOpenSPCoopMsg.forceTransportHeader(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE, wwwAuthenticateErrorHeader);
										}
										
										/**if(ServiceBinding.REST.equals(requestMessage.getServiceBinding())){
											if(wwwAuthenticateErrorHeader!=null && wwwAuthenticateErrorHeader.startsWith(HttpConstants.AUTHORIZATION_PREFIX_BASIC) && 
													ServiceBinding.REST.equals(errorOpenSPCoopMsg.getServiceBinding())) {
												try {
													errorOpenSPCoopMsg.castAsRest().updateContent(null);
												}catch(Throwable e) {}
											}
										}*/
										
										// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
										this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
										this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
										ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
									}
		
								}
								
								updateCredenzialiSafe(credenzialeTrasporto, tipoAutenticazione, null);
								
								if(this.openspcoopstate!=null) {
									this.openspcoopstate.releaseResource();
								}
								return false;
								
							}
						}
					}
					finally {
						this.transaction.getTempiElaborazione().endAutenticazione();
					}
				}
			}
			
			
			boolean autenticazioneToken = false;
			if(this.gestioneTokenAutenticazione!=null) {
				autenticazioneToken = GestoreAutenticazione.isAutenticazioneTokenEnabled(this.gestioneTokenAutenticazione);
			}
			if(autenticazioneToken) {
				
				this.transaction.getTempiElaborazione().startAutenticazioneToken();
				try {
				
					String checkAuthnToken = GestoreAutenticazione.getLabel(this.gestioneTokenAutenticazione);
					this.msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_AUTHN_CHECK, checkAuthnToken);
					this.msgDiag.logPersonalizzato("autenticazioneTokenInCorso");
					this.msgContext.getIntegrazione().setTokenPolicyAuthn(GestoreAutenticazione.getActions(this.gestioneTokenAutenticazione));
					
					ErroreCooperazione erroreCooperazione = null;
					ErroreIntegrazione erroreIntegrazione = null;
					Exception eAutenticazione = null;
					OpenSPCoop2Message errorMessageAutenticazione = null;
					String wwwAuthenticateErrorHeader = null;
					String errorMessage = null;
					IntegrationFunctionError integrationFunctionError = null;
					try {
						EsitoAutenticazionePortaApplicativa	esito = 
								GestoreAutenticazione.verificaAutenticazioneTokenPortaApplicativa(this.gestioneTokenAutenticazione, datiInvocazioneAutenticazione, this.pddContext, this.protocolFactory, this.requestMessage);
	
						if(!esito.isClientAuthenticated()) {
							erroreCooperazione = esito.getErroreCooperazione();
							erroreIntegrazione = esito.getErroreIntegrazione();
							eAutenticazione = esito.getEccezioneProcessamento();
							errorMessageAutenticazione = esito.getErrorMessage();
							wwwAuthenticateErrorHeader = esito.getWwwAuthenticateErrorHeader();
							errorMessage = esito.getDetails();
							integrationFunctionError = esito.getIntegrationFunctionError();
						}
						
						if (erroreIntegrazione != null || erroreCooperazione!=null) {
							this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTENTICAZIONE_TOKEN, "true");
						}
						else {
							this.msgDiag.logPersonalizzato("autenticazioneTokenEffettuata");							
						}
						
					} catch (Exception e) {
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento("["+ RicezioneBuste.ID_MODULO+ "] processo di autenticazione token ["
										+ checkAuthnToken + "] fallito, " + e.getMessage(),CodiceErroreIntegrazione.CODICE_503_AUTENTICAZIONE);
						erroreCooperazione = null;
						eAutenticazione = e;
						this.logCore.error("processo di autenticazione token ["
								+ checkAuthnToken + "] fallito, " + e.getMessage(),e);
						integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
					}
					if (erroreIntegrazione != null || erroreCooperazione!=null) {
						String descrizioneErrore = null;
						try{
							if(errorMessage!=null) {
								descrizioneErrore = errorMessage;
							}
							else {
								if(erroreCooperazione != null)
									descrizioneErrore = erroreCooperazione.getDescrizione(this.protocolFactory);
								else
									descrizioneErrore = erroreIntegrazione.getDescrizione(this.protocolFactory);
							}
							this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, descrizioneErrore);
						}catch(Exception e){
							this.logCore.error(DESCRIZIONE_ERROR+e.getMessage(),e);
						}
						String errorMsg =  "Riscontrato errore durante il processo di Autenticazione Token per il messaggio con identificativo di transazione ["+this.idTransazione+"]: "+descrizioneErrore;
						this.msgDiag.logPersonalizzato("autenticazioneTokenFallita");
						if(eAutenticazione!=null){
							this.logCore.error(errorMsg,eAutenticazione);
						}
						else{
							this.logCore.error(errorMsg);
						}
						
						// Tracciamento richiesta: non ancora registrata
						if(this.msgContext.isTracciamentoAbilitato()){
							EsitoElaborazioneMessaggioTracciato esitoTraccia = 
									EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("["+ RicezioneBuste.ID_MODULO+ PROCESSO_AUTENTICAZIONE
										+ tipoAutenticazione + PROCESSO_FALLITO);
							this.tracciamento.registraRichiesta(this.requestMessage,null,this.soapHeaderElement,this.bustaRichiesta,esitoTraccia,
									Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
									this.correlazioneApplicativa);
						}
						
						if(this.msgContext.isGestioneRisposta()){
	
							if(errorMessageAutenticazione!=null) {
								this.msgContext.setMessageResponse(errorMessageAutenticazione);
							}
							else {
							
								RicezioneBusteGeneratoreBustaErrore ricezioneBusteGeneratoreBustaErrore = new RicezioneBusteGeneratoreBustaErrore(this.msgContext, this.generatoreErrore);
								
								this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);	
								if(erroreIntegrazione != null){
									this.parametriGenerazioneBustaErrore.setErroreIntegrazione(erroreIntegrazione);
								}
								else{
									this.parametriGenerazioneBustaErrore.setErroreCooperazione(erroreCooperazione);
								}
	
								OpenSPCoop2Message errorOpenSPCoopMsg = null;
								if(erroreCooperazione!=null){
									if(CodiceErroreCooperazione.SICUREZZA_TOKEN_AUTORIZZAZIONE_FALLITA.equals(erroreCooperazione.getCodiceErrore())) {
										if(integrationFunctionError==null) {
											integrationFunctionError = IntegrationFunctionError.TOKEN_REQUIRED_CLAIMS_NOT_FOUND;
										}
										this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
										errorOpenSPCoopMsg = ricezioneBusteGeneratoreBustaErrore.generaBustaErroreValidazione(this.parametriGenerazioneBustaErrore);
									}
									else {
										if(integrationFunctionError==null) {
											integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
										}
										this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
										errorOpenSPCoopMsg = ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,eAutenticazione);
									}
								}
								else {
									if(integrationFunctionError==null) {
										integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
									}
									this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
									errorOpenSPCoopMsg = ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,eAutenticazione);
								}	
								
								if(wwwAuthenticateErrorHeader!=null) {
									errorOpenSPCoopMsg.forceTransportHeader(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE, wwwAuthenticateErrorHeader);
								}
								
								/**if(ServiceBinding.REST.equals(requestMessage.getServiceBinding())){
									if(wwwAuthenticateErrorHeader!=null && wwwAuthenticateErrorHeader.startsWith(HttpConstants.AUTHORIZATION_PREFIX_BASIC) && 
											ServiceBinding.REST.equals(errorOpenSPCoopMsg.getServiceBinding())) {
										try {
											errorOpenSPCoopMsg.castAsRest().updateContent(null);
										}catch(Throwable e) {}
									}
								}*/
								
								// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
								this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
								this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
								ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
							}
	
						}
						
						updateCredenzialiSafe(credenzialeTrasporto, tipoAutenticazione, null);
						
						if(this.openspcoopstate!=null) {
							this.openspcoopstate.releaseResource();
						}
						return false;
						
					}
				}finally {
					this.transaction.getTempiElaborazione().endAutenticazioneToken();
				}
			}
			else {
				
				this.msgDiag.logPersonalizzato("autenticazioneTokenDisabilitata");
				
			}
			
			
			InformazioniToken informazioniTokenNormalizzate = null;
			if(this.pddContext.containsKey(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE)) {
				informazioniTokenNormalizzate = (InformazioniToken) this.pddContext.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE);
			}
			
			if(informazioniTokenNormalizzate!=null && 
					informazioniTokenNormalizzate.getClientId()!=null && StringUtils.isNotEmpty(informazioniTokenNormalizzate.getClientId())) {
				this.transaction.getTempiElaborazione().startAutenticazioneApplicativoToken();
				try {
				
					if(parametriAutenticazione==null) {
						try{
							if(this.pa!=null){
								parametriAutenticazione = new ParametriAutenticazione(this.pa.getProprietaAutenticazioneList());
							}
							else{
								parametriAutenticazione = new ParametriAutenticazione(this.pd.getProprietaAutenticazioneList());
							}
						}catch(Exception exception){
							// ignore
						}
					}
					
					this.msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_CLIENT_ID, informazioniTokenNormalizzate.getClientId());
					this.msgDiag.logPersonalizzato("autenticazioneApplicativoTokenInCorso");
					
					ErroreIntegrazione erroreIntegrazione = null;
					ErroreCooperazione erroreCooperazione = null;
					Exception eAutenticazione = null;
					IntegrationFunctionError integrationFunctionError = null;
					try {						
						EsitoAutenticazionePortaApplicativa esito = 
								GestoreAutenticazione.verificaAutenticazionePortaApplicativa(CostantiConfigurazione.AUTENTICAZIONE_TOKEN,
										datiInvocazioneAutenticazione, parametriAutenticazione,
										this.pddContext, this.protocolFactory, this.requestMessage); 
						CostantiPdD.addKeywordInCache(this.msgDiag, esito.isEsitoPresenteInCache(),
								this.pddContext, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_AUTENTICAZIONE_TOKEN);
						if(!esito.isClientAuthenticated()) {
							erroreCooperazione = esito.getErroreCooperazione();
							erroreIntegrazione = esito.getErroreIntegrazione();
							eAutenticazione = esito.getEccezioneProcessamento();
							integrationFunctionError = esito.getIntegrationFunctionError();
							this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTENTICAZIONE_TOKEN, "true");
						}
						else {
							if(esito.isClientIdentified()) {
								this.idApplicativoToken = esito.getIdServizioApplicativo();
								this.msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_SERVIZIO_APPLICATIVO, this.idApplicativoToken.getNome()+NamingUtils.LABEL_DOMINIO+this.idApplicativoToken.getIdSoggettoProprietario().toString());
								this.msgDiag.logPersonalizzato("autenticazioneApplicativoTokenEffettuata.identificazioneRiuscita");
								
								this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ID_APPLICATIVO_TOKEN, this.idApplicativoToken);
								try {
									requestFruitoreToken = GestoreRichieste.readFruitoreToken(this.requestInfo, this.idApplicativoToken.getIdSoggettoProprietario(), this.idApplicativoToken);
									saApplicativoToken = this.configurazionePdDReader.getServizioApplicativo(this.idApplicativoToken, this.requestInfo);
									Map<String, String> configProperties = this.configurazionePdDReader.getProprietaConfigurazione(saApplicativoToken);
						            if (configProperties != null && !configProperties.isEmpty()) {
						            	this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_APPLICATIVO_TOKEN, configProperties);
									}	
								}catch(Throwable t) {
									// ignore
								}
								try {
									soggettoToken = this.registroServiziReader.getSoggetto(this.idApplicativoToken.getIdSoggettoProprietario(), null, this.requestInfo);
									Map<String, String> configProperties = this.registroServiziReader.getProprietaConfigurazione(soggettoToken);
						            if (configProperties != null && !configProperties.isEmpty()) {
						            	this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN, configProperties);
									}	
								}catch(Throwable t) {
									// ignore
								}	
							}
							else {
								erroreIntegrazione = esito.getErroreIntegrazione();
								erroreCooperazione = esito.getErroreCooperazione();
								eAutenticazione = esito.getEccezioneProcessamento();
								integrationFunctionError = esito.getIntegrationFunctionError();
								if(erroreIntegrazione==null && erroreCooperazione==null) {
									this.msgDiag.logPersonalizzato("autenticazioneApplicativoTokenEffettuata.identificazioneFallita");
								}
							}
						}
					} catch (Exception e) {
						CostantiPdD.addKeywordInCache(this.msgDiag, false,
								this.pddContext, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_AUTENTICAZIONE_TOKEN);
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento("["+ RicezioneBuste.ID_MODULO+ "] processo di autenticazione applicativo token fallito, " + e.getMessage(),CodiceErroreIntegrazione.CODICE_503_AUTENTICAZIONE);
						eAutenticazione = e;
						this.logCore.error("processo di autenticazione applicativo token fallito, " + e.getMessage(),e);
						integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
					}
					if (erroreIntegrazione != null || erroreCooperazione!=null) {
						String descrizioneErrore = null;
						try{
							if(erroreCooperazione != null)
								descrizioneErrore = erroreCooperazione.getDescrizione(this.protocolFactory);
							else
								descrizioneErrore = erroreIntegrazione.getDescrizione(this.protocolFactory);
							this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, descrizioneErrore);
						}catch(Exception e){
							this.logCore.error(DESCRIZIONE_ERROR+e.getMessage(),e);
						}
						String errorMsg =  "Riscontrato errore durante il processo di autenticazione dell'applicativo token per il messaggio con identificativo di transazione ["+this.idTransazione+"]: "+descrizioneErrore;
						this.msgDiag.logPersonalizzato("autenticazioneApplicativoTokenFallita");
						if(eAutenticazione!=null){
							this.logCore.error(errorMsg,eAutenticazione);
						}
						else{
							this.logCore.error(errorMsg);
						}
						
						// Tracciamento richiesta: non ancora registrata
						if(this.msgContext.isTracciamentoAbilitato()){
							EsitoElaborazioneMessaggioTracciato esitoTraccia = 
									EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("["+ RicezioneBuste.ID_MODULO+ PROCESSO_AUTENTICAZIONE
										+ tipoAutenticazione + PROCESSO_FALLITO);
							this.tracciamento.registraRichiesta(this.requestMessage,null,this.soapHeaderElement,this.bustaRichiesta,esitoTraccia,
									Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
									this.correlazioneApplicativa);
						}
						
						if(this.msgContext.isGestioneRisposta()){

							RicezioneBusteGeneratoreBustaErrore ricezioneBusteGeneratoreBustaErrore = new RicezioneBusteGeneratoreBustaErrore(this.msgContext, this.generatoreErrore);
							
							this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);	
							if(erroreIntegrazione != null){
								this.parametriGenerazioneBustaErrore.setErroreIntegrazione(erroreIntegrazione);
							}
							else{
								this.parametriGenerazioneBustaErrore.setErroreCooperazione(erroreCooperazione);
							}
							
							OpenSPCoop2Message errorOpenSPCoopMsg = null;
							if(integrationFunctionError==null) {
								integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
							}
							this.parametriGenerazioneBustaErrore.setIntegrationFunctionError(integrationFunctionError);
							errorOpenSPCoopMsg = ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,eAutenticazione);
							
							// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento
							this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
							this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
							ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);
							
						}
						
						updateCredenzialiSafe(credenzialeTrasporto, tipoAutenticazione, informazioniTokenNormalizzate);
						
						if(this.openspcoopstate!=null) {
							this.openspcoopstate.releaseResource();
						}
						return false;
					}
				}
				finally {
					this.transaction.getTempiElaborazione().endAutenticazioneApplicativoToken();
				}
			}
			
			if(propertiesReader.isTransazioniEnabled() && 
					(credenzialeTrasporto!=null || informazioniTokenNormalizzate!=null)) {
				CredenzialiMittente credenzialiMittente = new CredenzialiMittente();
				
				try{
				
					if(credenzialeTrasporto!=null) {
						GestoreAutenticazione.updateCredenzialiTrasporto(this.identitaPdD, RicezioneBuste.ID_MODULO, this.idTransazione, tipoAutenticazione, credenzialeTrasporto, credenzialiMittente, 
								this.openspcoopstate, "RicezioneBuste.credenzialiTrasporto", this.requestInfo);
					}
					
					if(informazioniTokenNormalizzate!=null) {
						GestoreAutenticazione.updateCredenzialiToken(this.identitaPdD, RicezioneBuste.ID_MODULO, this.idTransazione, informazioniTokenNormalizzate, this.idApplicativoToken, credenzialiMittente, 
								this.openspcoopstate, "RicezioneBuste.credenzialiToken", this.requestInfo,
								this.pddContext);
					}
					
					this.transaction.setCredenzialiMittente(credenzialiMittente);
								
				} catch (Exception e) {
					this.msgDiag.addKeywordErroreProcessamento(e,"Aggiornamento Credenziali Fallito");
					this.msgDiag.logErroreGenerico(e,"GestoreAutenticazione.updateCredenziali");
					this.logCore.error("GestoreAutenticazione.updateCredenziali error: "+e.getMessage(),e);
					
					// Tracciamento richiesta: non ancora registrata
					if(this.msgContext.isTracciamentoAbilitato()){
						EsitoElaborazioneMessaggioTracciato esitoTraccia = 
								EsitoElaborazioneMessaggioTracciato.getEsitoElaborazioneConErrore("GestoreAutenticazione.updateCredenziali, non riuscito: "+e.getMessage());
						this.tracciamento.registraRichiesta(this.requestMessage,null,this.soapHeaderElement,this.bustaRichiesta,esitoTraccia,
								Tracciamento.createLocationString(true,this.msgContext.getSourceLocation()),
								this.correlazioneApplicativa);
					}
					if(this.msgContext.isGestioneRisposta()){

						RicezioneBusteGeneratoreBustaErrore ricezioneBusteGeneratoreBustaErrore = new RicezioneBusteGeneratoreBustaErrore(this.msgContext, this.generatoreErrore);
						
						this.parametriGenerazioneBustaErrore.setBusta(this.bustaRichiesta);
						this.parametriGenerazioneBustaErrore.
							setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
									get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_503_AUTENTICAZIONE));
						OpenSPCoop2Message errorOpenSPCoopMsg = ricezioneBusteGeneratoreBustaErrore.generaBustaErroreProcessamento(this.parametriGenerazioneBustaErrore,e);
						
						// Nota: la bustaRichiesta e' stata trasformata da generaErroreProcessamento

						this.parametriInvioBustaErrore.setOpenspcoopMsg(errorOpenSPCoopMsg);
						this.parametriInvioBustaErrore.setBusta(this.parametriGenerazioneBustaErrore.getBusta());
						ricezioneBusteGeneratoreBustaErrore.sendRispostaBustaErrore(this.parametriInvioBustaErrore);

					}
					if(this.openspcoopstate!=null) {
						this.openspcoopstate.releaseResource();
					}
					return false;
					
				}
				
			}


		}
		
		// ** trasporto **
		try {
			this.pddContext.removeObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_SOGGETTO_FRUITORE);
		}catch(Throwable t) {
			// ignore
		}
		/** Avendo commentato il pezzo prima dell'autenticazione, non dovrebbe servire neanche questo
		requestInfo.getRequestThreadContext().clearSoggettoFruitore(); */
		if(this.soggettoFruitore!=null) {
			try {
				if(requestFruitoreTrasporto==null) {
					requestFruitoreTrasporto = GestoreRichieste.readFruitoreTrasporto(this.requestInfo, this.soggettoFruitore, idSAFruitore);
				}
			}catch(Throwable t) {
				this.logCore.error(ERRORE_LETTURA_SOGGETTO+this.soggettoFruitore+OGGETTO_REQUEST_INFO+t.getMessage(),t);
			}
			if(soggettoFruitoreObject==null) {
				try {
					soggettoFruitoreObject = this.registroServiziReader.getSoggetto(this.soggettoFruitore, null, this.requestInfo);
				}catch(Throwable t) {
					this.logCore.error(ERRORE_LETTURA_SOGGETTO+this.soggettoFruitore+DAL_REGISTRO+t.getMessage(),t);
				}
				if(soggettoFruitoreObject!=null) {
					try {
						Map<String, String> configProperties = this.registroServiziReader.getProprietaConfigurazione(soggettoFruitoreObject);
			            if (configProperties != null && !configProperties.isEmpty()) {
			            	this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_SOGGETTO_FRUITORE, configProperties);
						}	
					}catch(Throwable t) {
						this.logCore.error("Errore durante la lettura delle proprietà del soggetto '"+this.soggettoFruitore+DAL_REGISTRO+t.getMessage(),t);
					}	
				}
			}
		}
		if(requestFruitoreTrasporto==null && soggettoFruitoreObject!=null) {
			
			RequestFruitore rf = new RequestFruitore(); 
			
			rf.setIdSoggettoFruitore(this.soggettoFruitore);
			rf.setSoggettoFruitoreRegistry(soggettoFruitoreObject);	
			try {
				org.openspcoop2.core.config.Soggetto soggettoConfig = this.configurazionePdDReader.getSoggetto(this.soggettoFruitore, this.requestInfo); 
				rf.setSoggettoFruitoreConfig(soggettoConfig);
			}catch(Throwable t) {
				this.logCore.error(ERRORE_LETTURA_SOGGETTO+this.soggettoFruitore+"' dalla configurazione: "+t.getMessage(),t);
			}	
			try {
				String idPorta = this.configurazionePdDReader.getIdentificativoPorta(this.soggettoFruitore, this.protocolFactory, this.requestInfo);
				rf.setSoggettoFruitoreIdentificativoPorta(idPorta);
			}catch(Throwable t) {
				this.logCore.error("Errore durante la lettura dell'identificativo porta del soggetto '"+this.soggettoFruitore+DAL_REGISTRO+t.getMessage(),t);
			}	
			try {
				boolean soggettoVirtualeFRU = this.configurazionePdDReader.isSoggettoVirtuale(this.soggettoFruitore, this.requestInfo);
				rf.setSoggettoFruitoreSoggettoVirtuale(soggettoVirtualeFRU);
			}catch(Throwable t) {
				this.logCore.error("Errore durante la lettura dell'indicazione sul soggetto virtuale associato al soggetto '"+this.soggettoFruitore+DAL_REGISTRO+t.getMessage(),t);
			}	
			try {
				if(rf!=null) {
					if(rf.getSoggettoFruitoreRegistry().getPortaDominio()!=null &&
							StringUtils.isNotEmpty(rf.getSoggettoFruitoreRegistry().getPortaDominio())) {
						PortaDominio pdd = this.registroServiziReader.getPortaDominio(rf.getSoggettoFruitoreRegistry().getPortaDominio(), null, this.requestInfo);
						rf.setSoggettoFruitorePddReaded(true);
						rf.setSoggettoFruitorePdd(pdd);
					}
					else {
						rf.setSoggettoFruitorePddReaded(true);
					}
				}
			}catch(Throwable t) {
				this.logCore.error("Errore durante la lettura dei dati della pdd del soggetto '"+this.soggettoFruitore+DAL_REGISTRO+t.getMessage(),t);
			}	
			try {
				String impl = this.registroServiziReader.getImplementazionePdD(this.soggettoFruitore, null, this.requestInfo);
				rf.setSoggettoFruitoreImplementazionePdd(impl);
			}catch(Throwable t) {
				this.logCore.error("Errore durante la lettura dell'implementazione pdd del soggetto '"+this.soggettoFruitore+DAL_REGISTRO+t.getMessage(),t);
			}	
				
			rf.setIdServizioApplicativoFruitore(idSAFruitore);
			rf.setServizioApplicativoFruitore(sa);
			// Supportato solo per ricezione contenuti applicativi, trasporto: rf.setServizioApplicativoFruitoreAnonimo(sa==null);
			
			try {
				GestoreRichieste.saveRequestFruitoreTrasporto(this.requestInfo, rf);
			} catch (Throwable e) {
				this.logCore.error("Errore durante il salvataggio nella cache e nel thread context delle informazioni sul fruitore trasporto: "+e.getMessage(),e);
			}
			
		}
		
		// ** token **
		if(this.idApplicativoToken!=null) {
			try {
				if(requestFruitoreToken==null) {
					requestFruitoreToken = GestoreRichieste.readFruitoreToken(this.requestInfo, this.idApplicativoToken.getIdSoggettoProprietario(), this.idApplicativoToken);
				}
			}catch(Throwable t) {
				this.logCore.error("Errore durante la lettura del fruitore '"+this.idApplicativoToken+OGGETTO_REQUEST_INFO+t.getMessage(),t);
			}
			
			if(saApplicativoToken==null) {
				try {
					saApplicativoToken = this.configurazionePdDReader.getServizioApplicativo(this.idApplicativoToken, this.requestInfo);
				}catch(Throwable t) {
					this.logCore.error("Errore durante la lettura dell'applicativo '"+this.idApplicativoToken+DAL_REGISTRO+t.getMessage(),t);
				}
				if(saApplicativoToken!=null) {
					try {
						Map<String, String> configProperties = this.configurazionePdDReader.getProprietaConfigurazione(saApplicativoToken);
						if (configProperties != null && !configProperties.isEmpty()) {
							this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_APPLICATIVO_TOKEN, configProperties);
						}	
					}catch(Throwable t) {
						this.logCore.error("Errore durante la lettura delle proprietà dell'applicativo '"+this.idApplicativoToken+DAL_REGISTRO+t.getMessage(),t);
					}
				}
			}
			
			if(soggettoToken==null) {
				try {
					soggettoToken = this.registroServiziReader.getSoggetto(this.idApplicativoToken.getIdSoggettoProprietario(), null, this.requestInfo);
				}catch(Throwable t) {
					this.logCore.error(ERRORE_LETTURA_SOGGETTO+this.idApplicativoToken.getIdSoggettoProprietario()+DAL_REGISTRO+t.getMessage(),t);
				}
				if(soggettoToken!=null) {
					try {
						Map<String, String> configProperties = this.registroServiziReader.getProprietaConfigurazione(soggettoToken);
			            if (configProperties != null && !configProperties.isEmpty()) {
			            	this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.PROPRIETA_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN, configProperties);
						}	
					}catch(Throwable t) {
						this.logCore.error("Errore durante la lettura delle proprietà del soggetto '"+this.idApplicativoToken.getIdSoggettoProprietario()+DAL_REGISTRO+t.getMessage(),t);
					}	
				}
			}
		}
		if(requestFruitoreToken==null && this.idApplicativoToken!=null && soggettoToken!=null && saApplicativoToken!=null) {
			
			RequestFruitore rf = new RequestFruitore(); 
			
			rf.setIdSoggettoFruitore(this.idApplicativoToken.getIdSoggettoProprietario());
			rf.setSoggettoFruitoreRegistry(soggettoToken);	
			try {
				org.openspcoop2.core.config.Soggetto soggettoConfig = this.configurazionePdDReader.getSoggetto(this.idApplicativoToken.getIdSoggettoProprietario(), this.requestInfo); 
				rf.setSoggettoFruitoreConfig(soggettoConfig);
			}catch(Throwable t) {
				this.logCore.error(ERRORE_LETTURA_SOGGETTO+this.idApplicativoToken.getIdSoggettoProprietario()+"' dalla configurazione: "+t.getMessage(),t);
			}	
			try {
				String idPorta = this.configurazionePdDReader.getIdentificativoPorta(this.idApplicativoToken.getIdSoggettoProprietario(), this.protocolFactory, this.requestInfo);
				rf.setSoggettoFruitoreIdentificativoPorta(idPorta);
			}catch(Throwable t) {
				this.logCore.error("Errore durante la lettura dell'identificativo porta del soggetto '"+this.idApplicativoToken.getIdSoggettoProprietario()+DAL_REGISTRO+t.getMessage(),t);
			}	
			try {
				boolean soggettoVirtualeFRU = this.configurazionePdDReader.isSoggettoVirtuale(this.idApplicativoToken.getIdSoggettoProprietario(), this.requestInfo);
				rf.setSoggettoFruitoreSoggettoVirtuale(soggettoVirtualeFRU);
			}catch(Throwable t) {
				this.logCore.error("Errore durante la lettura dell'indicazione sul soggetto virtuale associato al soggetto '"+this.idApplicativoToken.getIdSoggettoProprietario()+DAL_REGISTRO+t.getMessage(),t);
			}	
			try {
				if(rf!=null) {
					if(rf.getSoggettoFruitoreRegistry().getPortaDominio()!=null &&
							StringUtils.isNotEmpty(rf.getSoggettoFruitoreRegistry().getPortaDominio())) {
						PortaDominio pdd = this.registroServiziReader.getPortaDominio(rf.getSoggettoFruitoreRegistry().getPortaDominio(), null, this.requestInfo);
						rf.setSoggettoFruitorePddReaded(true);
						rf.setSoggettoFruitorePdd(pdd);
					}
					else {
						rf.setSoggettoFruitorePddReaded(true);
					}
				}
			}catch(Throwable t) {
				this.logCore.error("Errore durante la lettura dei dati della pdd del soggetto '"+this.idApplicativoToken.getIdSoggettoProprietario()+DAL_REGISTRO+t.getMessage(),t);
			}	
			try {
				String impl = this.registroServiziReader.getImplementazionePdD(this.idApplicativoToken.getIdSoggettoProprietario(), null, this.requestInfo);
				rf.setSoggettoFruitoreImplementazionePdd(impl);
			}catch(Throwable t) {
				this.logCore.error("Errore durante la lettura dell'implementazione pdd del soggetto '"+this.idApplicativoToken.getIdSoggettoProprietario()+DAL_REGISTRO+t.getMessage(),t);
			}	
				
			rf.setIdServizioApplicativoFruitore(this.idApplicativoToken);
			rf.setServizioApplicativoFruitore(saApplicativoToken);
			/** nel token non ha senso rf.setServizioApplicativoFruitoreAnonimo(saApplicativoToken==null); */
			
			try {
				GestoreRichieste.saveRequestFruitoreToken(this.requestInfo, rf);
			} catch (Throwable e) {
				this.logCore.error("Errore durante il salvataggio nella cache e nel thread context delle informazioni sul fruitore token: "+e.getMessage(),e);
			}
			
		}
	
		return true;
	}
	
	private void updateCredenzialiSafe(String credenzialeTrasporto, String tipoAutenticazione, InformazioniToken informazioniTokenNormalizzate) {
		
		// Viene chiamato se l'autenticazione fallisce
		
		CredenzialiMittente credenzialiMittente = this.transaction.getCredenzialiMittente();
		if(credenzialiMittente==null) {
			credenzialiMittente = new CredenzialiMittente();
			try {
				this.transaction.setCredenzialiMittente(credenzialiMittente);
			}catch(Exception e) {
				this.logCore.error("SetCredenzialiMittente error: "+e.getMessage(),e);
			}
		}
		
		updateCredenzialiTrasportoSafe(credenzialiMittente, credenzialeTrasporto, tipoAutenticazione);
		
		updateCredenzialiTokenSafe(credenzialiMittente, informazioniTokenNormalizzate);
		
	}
	private void updateCredenzialiTrasportoSafe(CredenzialiMittente credenzialiMittente, String credenzialeTrasporto, String tipoAutenticazione) {
		if(OpenSPCoop2Properties.getInstance().isGestioneAutenticazioneSaveTokenAuthenticationInfoAuthenticationFailed() &&
				tipoAutenticazione!=null && credenzialeTrasporto!=null) {
			try {
				GestoreAutenticazione.updateCredenzialiTrasporto(this.identitaPdD, RicezioneBuste.ID_MODULO, this.idTransazione, tipoAutenticazione, credenzialeTrasporto, credenzialiMittente, 
						this.openspcoopstate, "RicezioneBuste.credenzialiTrasporto", this.requestInfo);
			}catch(Exception e) {
				this.logCore.error("updateCredenzialiTrasporto error: "+e.getMessage(),e);
			}
		}
	}
	private void updateCredenzialiTokenSafe(CredenzialiMittente credenzialiMittente, InformazioniToken informazioniTokenNormalizzate) {
		if(OpenSPCoop2Properties.getInstance().isGestioneTokenSaveTokenAuthenticationInfoAuthenticationFailed()) {
			InformazioniToken info = informazioniTokenNormalizzate;
			if(info==null && this.pddContext.containsKey(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE)) {
				info = (InformazioniToken) this.pddContext.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE);
			}
			if(info!=null) {
				try {
					GestoreAutenticazione.updateCredenzialiToken(this.identitaPdD, RicezioneBuste.ID_MODULO, this.idTransazione, info, this.idApplicativoToken, credenzialiMittente, 
							this.openspcoopstate, "RicezioneBuste.credenzialiToken", this.requestInfo,
							this.pddContext);
				}catch(Exception e) {
					this.logCore.error("updateCredenzialiToken error: "+e.getMessage(),e);
				}
			}
		}
	}

	private static final String IDENTIFICAZIONE_SOGGETTO_TRAMITE_PROFILO = "Identificato un soggetto (tramite profilo di interoperabilità) '";
	private static final String ERRORE_LETTURA_SOGGETTO = "Errore durante la lettura del soggetto '";
	private static final String OGGETTO_REQUEST_INFO = "' dall'oggetto request info: ";
	private static final String DAL_REGISTRO = "' dal registro: ";
	private static final String PROCESSO_AUTENTICAZIONE = "] processo di autenticazione [";
	private static final String PROCESSO_FALLITO = "] fallito";
	private static final String DESCRIZIONE_ERROR = "getDescrizione Error:";
	
	public static void registraIntermediario(IDSoggetto idSoggettoCanale, MsgDiagnostico msgDiag, String idTransazione, Transaction transaction, Context context) throws TransactionDeletedException {
		if(msgDiag!=null) {
			msgDiag.addKeyword(CostantiPdD.KEY_INTERMEDIARIO,idSoggettoCanale.getNome());
			msgDiag.logPersonalizzato("soggettoIntermediario");
		}
		if(transaction==null && idTransazione!=null) {
			try{
				transaction = TransactionContext.getTransaction(idTransazione);
			}catch(Exception e){
				// ignore per ModI
			}
		}
		if(transaction!=null) {
			transaction.addEventoGestione("intermediario="+idSoggettoCanale.getNome());
		}
		if(context!=null) {
			context.put(CostantiPdD.INTERMEDIARIO, idSoggettoCanale);
		}
	}
}

