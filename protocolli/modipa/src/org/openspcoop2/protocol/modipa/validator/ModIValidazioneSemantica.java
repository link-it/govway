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



package org.openspcoop2.protocol.modipa.validator;

import java.util.ArrayList;
import java.util.Date;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.RuoloTipologia;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.config.ConfigurazionePdDReader;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.token.parser.Claims;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.protocol.basic.validator.ValidazioneSemantica;
import org.openspcoop2.protocol.engine.SecurityTokenUtilities;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.modipa.utils.ModIPropertiesUtils;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.SecurityToken;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.RuoloBusta;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazione;
import org.openspcoop2.protocol.sdk.validator.ValidazioneSemanticaResult;
import org.openspcoop2.protocol.sdk.validator.ValidazioneUtils;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;



/**
 * Classe che implementa, in base al protocollo SdI, l'interfaccia {@link org.openspcoop2.protocol.sdk.validator.IValidazioneSemantica}
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ModIValidazioneSemantica extends ValidazioneSemantica {

	/** ValidazioneUtils */
	protected ValidazioneUtils validazioneUtils;
	
	/** Properties */
	protected ModIProperties modiProperties;
	
	/** Errori di validazione riscontrati sulla busta */
	protected java.util.List<Eccezione> erroriValidazione = new ArrayList<>();
	/** Errori di processamento riscontrati sulla busta */
	protected java.util.List<Eccezione> erroriProcessamento = new ArrayList<>();
	
	public ModIValidazioneSemantica(IProtocolFactory<?> factory, IState state) throws ProtocolException {
		super(factory, state);
		this.modiProperties = ModIProperties.getInstance();
		this.validazioneUtils = new ValidazioneUtils(factory);
	}

	private static final String DIAGNOSTIC_VALIDATE = "validazioneSemantica";
	private static final String DIAGNOSTIC_IN_CORSO = "inCorso";
	private static final String DIAGNOSTIC_COMPLETATA = "completata";
	private static final String DIAGNOSTIC_FALLITA = "fallita";
	
	private String getErroreClaimNonValido(String claim) {
		return "Token contenente un claim '"+claim+"' non valido";
	}
	private Eccezione getErroreMittenteNonAutorizzato(Busta busta, String msgErrore) throws ProtocolException {
		String idApp = busta.getServizioApplicativoFruitore() + " (Soggetto: "+busta.getMittente()+")";
		return this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA, 
				"Applicativo Mittente "+idApp+" non autorizzato"+(msgErrore!=null ? "; "+msgErrore: ""));
	}
	private void addErroreMittenteNonAutorizzato(Busta busta, String msgErrore) throws ProtocolException {
		this.erroriValidazione.add(getErroreMittenteNonAutorizzato(busta, msgErrore));
	}
	
	@Override
	public ValidazioneSemanticaResult valida(OpenSPCoop2Message msg, Busta busta, 
			ProprietaValidazione proprietaValidazione, 
			RuoloBusta tipoBusta) throws ProtocolException{
		
		this.valida(msg,busta,tipoBusta, this.protocolFactory, this.state);
		
		java.util.List<Eccezione> erroriValidazioneList = null;
		if(!this.erroriValidazione.isEmpty()){
			erroriValidazioneList = this.erroriValidazione;
			if(this.context!=null) {
				this.context.addObject(Costanti.ERRORE_VALIDAZIONE_PROTOCOLLO, Costanti.ERRORE_TRUE);
			}
		}
		java.util.List<Eccezione> erroriProcessamentoList = null;
		if(!this.erroriProcessamento.isEmpty()){
			erroriValidazioneList = this.erroriProcessamento;
		}
		return new ValidazioneSemanticaResult(erroriValidazioneList, erroriProcessamentoList, null, null, null, null);
		
	}

	private void valida(OpenSPCoop2Message msg,Busta busta, RuoloBusta tipoBusta, IProtocolFactory<?> factory, IState state) throws ProtocolException{
		
		MsgDiagnostico msgDiag = null;
		String tipoDiagnostico = null;
		try{
		
			String prefixIntegrity = "[Header '"+this.modiProperties.getRestSecurityTokenHeaderModI()+"'] ";
			
			RequestInfo requestInfo = null;
			if(this.context!=null && this.context.containsKey(org.openspcoop2.core.constants.Costanti.REQUEST_INFO)) {
				requestInfo = (RequestInfo) this.context.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
			}
			
			boolean isRichiesta = RuoloBusta.RICHIESTA.equals(tipoBusta);
			
			boolean rest = ServiceBinding.REST.equals(msg.getServiceBinding());
			
			TipoPdD tipoPdD = isRichiesta ? TipoPdD.APPLICATIVA : TipoPdD.DELEGATA;
			IDSoggetto idSoggetto = null;
			if(busta!=null) {
				idSoggetto = TipoPdD.DELEGATA.equals(tipoPdD) ? 
						new IDSoggetto(busta.getTipoMittente(),busta.getMittente()) : 
						new IDSoggetto(busta.getTipoDestinatario(),busta.getDestinatario());
			}
			if(idSoggetto==null || idSoggetto.getTipo()==null || idSoggetto.getNome()==null) {
				idSoggetto = OpenSPCoop2Properties.getInstance().getIdentitaPortaDefault(this.protocolFactory.getProtocol(), requestInfo);
			}
			else {
				IRegistryReader registryReader = this.getProtocolFactory().getCachedRegistryReader(this.state, requestInfo);
				idSoggetto.setCodicePorta(registryReader.getDominio(idSoggetto));
			}
			msgDiag = MsgDiagnostico.newInstance(TipoPdD.DELEGATA, 
					idSoggetto,
					"ModI", requestInfo!=null && requestInfo.getProtocolContext()!=null ? requestInfo.getProtocolContext().getInterfaceName() : null,
					requestInfo,
					this.state);
			if(TipoPdD.DELEGATA.equals(tipoPdD)){
				msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI);
			}
			else {
				msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE);
			}
			msgDiag.setPddContext(this.context, this.protocolFactory);
			tipoDiagnostico = isRichiesta ? ".richiesta." : ".risposta.";
			
			msgDiag.logPersonalizzato(DIAGNOSTIC_VALIDATE+tipoDiagnostico+DIAGNOSTIC_IN_CORSO);
			
			
			
			
			
			String securityMessageProfile = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO);
			
			if(securityMessageProfile!=null) {
				securityMessageProfile = ModIPropertiesUtils.convertProfiloSicurezzaToConfigurationValue(securityMessageProfile);
			}
			
			boolean sicurezzaMessaggio = securityMessageProfile!=null && !ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_UNDEFINED.equals(securityMessageProfile);
			if(sicurezzaMessaggio) {
								
				Date now = DateManager.getDate();
				
				String exp = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_EXP);
				if(exp!=null) {
					checkExp(exp, now, rest, "");
				}
				
				String expIntegrity = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_INTEGRITY_EXP);
				if(expIntegrity!=null) {
					checkExp(expIntegrity, now, rest, prefixIntegrity);
				}
				
				String nbf = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_NBF);
				if(nbf!=null) {
					checkNbf(nbf, now, "");
				}
				
				String nbfIntegrity = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_INTEGRITY_NBF);
				if(nbfIntegrity!=null) {
					checkNbf(nbfIntegrity, now, prefixIntegrity);
				}
				
				String iat = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_IAT);
				if(iat!=null) {
					checkIat(iat, msg, rest, "");
				}
				
				String iatIntegrity = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_INTEGRITY_IAT);
				if(iatIntegrity!=null) {
					checkIat(iatIntegrity, msg, rest, prefixIntegrity);
				}
				
				String audience = busta.getProperty(rest ? 
						ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE :
						ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_SOAP_WSA_TO	);
				Object audienceAttesoObject = msg.getContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_AUDIENCE_CHECK);
				if(audienceAttesoObject!=null) {
					String audienceAtteso = (String) audienceAttesoObject;
					if(!audienceAtteso.equals(audience)) {
						
						boolean buildSecurityTokenInRequest = true;
						Object buildSecurityTokenInRequestObject = msg.getContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_BUILD_SECURITY_REQUEST_TOKEN);
						if(buildSecurityTokenInRequestObject instanceof Boolean) {
							buildSecurityTokenInRequest = (Boolean) buildSecurityTokenInRequestObject;
						}
						
						if(isRichiesta || buildSecurityTokenInRequest) {
						
							this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
									isRichiesta ? CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_EROGATORE_NON_VALIDO :
										CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_FRUITORE_NON_VALIDO, 
										getErroreClaimNonValido(Claims.JSON_WEB_TOKEN_RFC_7519_AUDIENCE)));
							
						}
					}
				}
				if(rest) { 
					Object audienceIntegrityAttesoObject = msg.getContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_AUDIENCE_INTEGRITY_CHECK);
					if(audienceIntegrityAttesoObject!=null) {
						String audienceIntegrityAtteso = (String) audienceIntegrityAttesoObject;
						String audienceIntegrity = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_INTEGRITY_AUDIENCE);
						if(audienceIntegrity==null) {
							// significa che l'audience tra i due token ricevuto e' identico
							audienceIntegrity = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE);
						}
						if(!audienceIntegrityAtteso.equals(audienceIntegrity)) {
							this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
									isRichiesta ? CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_EROGATORE_NON_VALIDO :
										CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_FRUITORE_NON_VALIDO, 
									prefixIntegrity+getErroreClaimNonValido(Claims.JSON_WEB_TOKEN_RFC_7519_AUDIENCE)));
						}
					}
				}
				
				Object audienceAuditAttesoObject = msg.getContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_AUDIENCE_AUDIT_CHECK);
				if(audienceAuditAttesoObject!=null) {
					String audienceAuditAtteso = (String) audienceAuditAttesoObject;
					String audienceAudit = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_AUDIENCE);
					if(audienceAudit==null) {
						// significa che l'audience tra i due token ricevuto e' identico
						audienceAudit = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_AUDIENCE);
					}
					String prefix = "[Header '"+this.modiProperties.getSecurityTokenHeaderModIAudit()+"'] ";
					if(!audienceAuditAtteso.equals(audienceAudit)) {
						this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
								isRichiesta ? CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_EROGATORE_NON_VALIDO :
									CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_FRUITORE_NON_VALIDO, 
								prefix+getErroreClaimNonValido(Claims.JSON_WEB_TOKEN_RFC_7519_AUDIENCE)));
					}
				}
			}
			
			if(isRichiesta) {
				
				// vale sia per sicurezza messaggio che per token
				// durante l'identificazione viene identificato 1 solo applicativo (non possono essere differenti tra token e trasporto)
				// viene quindi inserito dentro busta e usato per i controlli sottostanti
				
				if(msg.getTransportRequestContext()==null || msg.getTransportRequestContext().getInterfaceName()==null) {
					throw new ProtocolException("ID Porta non presente");
				}
				IDPortaApplicativa idPA = new IDPortaApplicativa();
				idPA.setNome(msg.getTransportRequestContext().getInterfaceName());
				PortaApplicativa pa = factory.getCachedConfigIntegrationReader(state, requestInfo).getPortaApplicativa(idPA);
				
				
				/** Identificazione Mittente by LineeGuida e Token */
				boolean saIdentificatoBySecurity = busta.getServizioApplicativoFruitore()!=null && !CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(busta.getServizioApplicativoFruitore());
				
				boolean sicurezzaToken = this.context.containsKey(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE);
				boolean saIdentificatoByToken = false;
				boolean saVerificatoBySecurity = false;
				if(sicurezzaToken) {
					IDServizioApplicativo idSAbyToken = null;
					StringBuilder sbError = new StringBuilder();
					try {
						idSAbyToken = IdentificazioneApplicativoMittenteUtils.identificazioneApplicativoMittenteByToken(this.log, state, busta, this.context, requestInfo, sbError);
					}catch(Exception e) {
						if(sbError!=null && sbError.length()>0) {
							this.context.addObject(Costanti.ERRORE_AUTORIZZAZIONE, Costanti.ERRORE_TRUE);
							this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA, 
									sbError.toString()));
							return;
						}
						else {
							throw e;
						}
					}
					saIdentificatoByToken = idSAbyToken!=null;
					
					if(saIdentificatoByToken) {
						/**	&& !saIdentificatoBySecurity) { 
						L'identificazione per sicurezza, se c'è abilitata l'autenticazione per token, non dovrebbe esserci mai poichè la funzionalità è stata disabilitata.
						Con autenticazione token attiva, gli applicativi anche se presente un x509 non vengono identificati dall'autenticazione https effettuata in AbstractModIValidazioneSintatticaCommons e di conseguenza in ModIValidazioneSintatticaRest e ModIValidazioneSintatticaSoap durante il trattamento del token di sicurezza
						bisogna quindi verificare, se è presente un certificato x509 di firma, che corrisponda a quello registrato nell'applicativo di tipo token identificato*/
						
						SecurityToken securityTokenForContext = SecurityTokenUtilities.readSecurityToken(this.context);
						if(securityTokenForContext!=null) {
							try {
								if(securityTokenForContext.getAuthorization()!=null && securityTokenForContext.getAuthorization().getCertificate()!=null) {
									sbError = new StringBuilder();
									String tipoToken = //"Http Header "+
											(securityTokenForContext.getAuthorization().getHttpHeaderName()!=null ? securityTokenForContext.getAuthorization().getHttpHeaderName() : HttpConstants.AUTHORIZATION);
									IdentificazioneApplicativoMittenteUtils.checkApplicativoTokenByX509(this.log, idSAbyToken, 
											state, requestInfo, tipoToken, securityTokenForContext.getAuthorization().getCertificate(), sbError);
									saVerificatoBySecurity = true;
								}
								if(securityTokenForContext.getIntegrity()!=null && securityTokenForContext.getIntegrity().getCertificate()!=null) {
									sbError = new StringBuilder();
									String tipoToken = //"Http Header "+
											(securityTokenForContext.getIntegrity().getHttpHeaderName()!=null ? securityTokenForContext.getIntegrity().getHttpHeaderName() : this.modiProperties.getRestSecurityTokenHeaderModI());
									IdentificazioneApplicativoMittenteUtils.checkApplicativoTokenByX509(this.log, idSAbyToken, 
											state, requestInfo, tipoToken, securityTokenForContext.getIntegrity().getCertificate(), sbError);
									saVerificatoBySecurity = true;
								}
								if(securityTokenForContext.getEnvelope()!=null && securityTokenForContext.getEnvelope().getCertificate()!=null) {
									sbError = new StringBuilder();
									String tipoToken = "WSSecurity";
									IdentificazioneApplicativoMittenteUtils.checkApplicativoTokenByX509(this.log, idSAbyToken, 
											state, requestInfo, tipoToken, securityTokenForContext.getEnvelope().getCertificate(), sbError);
									saVerificatoBySecurity = true;
								}
							}catch(Exception e) {
								if(sbError!=null && sbError.length()>0) {
									this.context.addObject(Costanti.ERRORE_AUTORIZZAZIONE, Costanti.ERRORE_TRUE);
									this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA, 
											sbError.toString()));
									return;
								}
								else {
									throw e;
								}
							}
						}
						
					}
				}
				
				boolean saFruitoreAnonimo = busta.getServizioApplicativoFruitore()==null || CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(busta.getServizioApplicativoFruitore());
								
				/** Tipi di Autorizzazione definiti */
				boolean autorizzazionePerRichiedente = false;
				if(pa.getAutorizzazioneToken()!=null) {
					autorizzazionePerRichiedente = StatoFunzionalita.ABILITATO.equals(pa.getAutorizzazioneToken().getAutorizzazioneApplicativi());
				}
				if(!autorizzazionePerRichiedente && pa.getServiziApplicativiAutorizzati()!=null) {
					// backward compatibility
					autorizzazionePerRichiedente = pa.getServiziApplicativiAutorizzati().sizeServizioApplicativoList()>0;
				}
				
				boolean autorizzazionePerRuolo = false;
				boolean checkRuoloRegistro = false;
				boolean checkRuoloEsterno = false;
				if(pa.getAutorizzazioneToken()!=null) {
	    			autorizzazionePerRuolo = StatoFunzionalita.ABILITATO.equals(pa.getAutorizzazioneToken().getAutorizzazioneRuoli());
				}
				if(autorizzazionePerRuolo) {
    				if( pa.getAutorizzazioneToken().getTipologiaRuoli()==null ||
    					RuoloTipologia.QUALSIASI.equals(pa.getAutorizzazioneToken().getTipologiaRuoli())){
    					checkRuoloRegistro = true;
    					checkRuoloEsterno = true;
    				} 
    				else if( RuoloTipologia.INTERNO.equals(pa.getAutorizzazioneToken().getTipologiaRuoli())){
    					checkRuoloRegistro = true;
    				}
    				else if( RuoloTipologia.ESTERNO.equals(pa.getAutorizzazioneToken().getTipologiaRuoli())){
    					checkRuoloEsterno = true;
    				}
				}
				
				/** Verifica consistenza identificazione del mittente */
				if(
						(
								autorizzazionePerRichiedente 
								|| 
								(autorizzazionePerRuolo && checkRuoloRegistro && !checkRuoloEsterno)
						)
						&&
						saFruitoreAnonimo
				){	
					this.context.addObject(Costanti.ERRORE_AUTORIZZAZIONE, Costanti.ERRORE_TRUE);
					this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA, 
							"Applicativo Mittente non identificato"));
					return;
				}
				if(!saFruitoreAnonimo &&
					(autorizzazionePerRichiedente || checkRuoloRegistro) 
					){
					// se utilizzo l'informazione dell'applicativo, tale informazione deve essere consistente rispetto a tutti i criteri di sicurezza
					if(sicurezzaMessaggio &&
						!saIdentificatoBySecurity && !saVerificatoBySecurity) {
						this.context.addObject(Costanti.ERRORE_AUTORIZZAZIONE, Costanti.ERRORE_TRUE);
						addErroreMittenteNonAutorizzato(busta, "il certificato di firma non corrisponde all'applicativo");
						return;
					}
					if(sicurezzaToken &&
						!saIdentificatoByToken) {
						// CASO DEPRECATO: questo caso non puo' succedere poiche' nel caso di sicurezza token l'identificazione avviene SOLO per token
						//                 quindi si rientra nel caso sopra 'Applicativo Mittente non identificato'
						this.context.addObject(Costanti.ERRORE_AUTORIZZAZIONE, Costanti.ERRORE_TRUE);
						addErroreMittenteNonAutorizzato(busta, "il claim 'clientId' presente nel token non corrisponde all'applicativo");
						return;
					}
				}
				
				/** Autorizzazione per Richiedente */
				Eccezione eccezioneAutorizzazionePerRichiedente = null;
				if(autorizzazionePerRichiedente) {					
					boolean autorizzato = false;
					if(pa.getServiziApplicativiAutorizzati()!=null) {
						for (PortaApplicativaAutorizzazioneServizioApplicativo paSA : pa.getServiziApplicativiAutorizzati().getServizioApplicativoList()) {
							if(paSA.getTipoSoggettoProprietario().equals(busta.getTipoMittente()) &&
									paSA.getNomeSoggettoProprietario().equals(busta.getMittente()) &&
									paSA.getNome().equals(busta.getServizioApplicativoFruitore())) {
								autorizzato = true;
							}
						}
					}
					if(!autorizzato) {
						eccezioneAutorizzazionePerRichiedente = getErroreMittenteNonAutorizzato(busta, null);
					}
				}
				
				/** Autorizzazione per Ruolo */
				Eccezione eccezioneAutorizzazionePerRuolo = null;
				if(autorizzazionePerRuolo) {
    				StringBuilder detailsBufferRuoli = new StringBuilder();
    				ServizioApplicativo sa = null;
    				if(!saFruitoreAnonimo) {
    					IDServizioApplicativo idSA = new IDServizioApplicativo();
    					idSA.setIdSoggettoProprietario(new IDSoggetto(busta.getTipoMittente(), busta.getMittente()));
    					idSA.setNome(busta.getServizioApplicativoFruitore());
    					sa = factory.getCachedConfigIntegrationReader(state,requestInfo).getServizioApplicativo(idSA);
    				}
    				boolean authRuoli = ConfigurazionePdDReader._autorizzazioneRoles(
    								RegistroServiziManager.getInstance(state),
    								null, sa, 
    								null, false, 
    								this.context, requestInfo,
    								checkRuoloRegistro, checkRuoloEsterno,
    								detailsBufferRuoli,
    								pa.getAutorizzazioneToken().getRuoli().getMatch(), pa.getAutorizzazioneToken().getRuoli(),
    								true);
    				if(!authRuoli) {
    					String errore = "Applicativo Mittente";
    					if(!saFruitoreAnonimo) {
    						errore = errore + " "+ busta.getServizioApplicativoFruitore() + " (Soggetto: "+busta.getMittente()+")";
    					}
    					errore = errore + " non autorizzato; ";
    					eccezioneAutorizzazionePerRuolo = this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA, 
    							errore + detailsBufferRuoli.toString());
    				}
	    		}
				
				/** Gestione Eccezioni */
				if(autorizzazionePerRichiedente && autorizzazionePerRuolo) {
					if(eccezioneAutorizzazionePerRichiedente!=null && eccezioneAutorizzazionePerRuolo!=null) {
						this.context.addObject(Costanti.ERRORE_AUTORIZZAZIONE, Costanti.ERRORE_TRUE);
						this.erroriValidazione.add(eccezioneAutorizzazionePerRuolo); // uso eccezione per ruolo che e' più completa come messaggistica
					}
					// se una delle due autorizzazione e' andata a buon fine devo autorizzare
				}
				else {
					if(eccezioneAutorizzazionePerRichiedente!=null || eccezioneAutorizzazionePerRuolo!=null) {
						this.context.addObject(Costanti.ERRORE_AUTORIZZAZIONE, Costanti.ERRORE_TRUE);
						if(eccezioneAutorizzazionePerRichiedente!=null) {
							this.erroriValidazione.add(eccezioneAutorizzazionePerRichiedente); 
						}
						else {
							this.erroriValidazione.add(eccezioneAutorizzazionePerRuolo);
						}
					}
				}
				
			}
			
			if(this.erroriValidazione.isEmpty() && this.erroriProcessamento.isEmpty()) {
				msgDiag.logPersonalizzato(DIAGNOSTIC_VALIDATE+tipoDiagnostico+DIAGNOSTIC_COMPLETATA);
			}
			else {
				String errore = null;
				if(!this.erroriValidazione.isEmpty()) {
					errore = ModIValidazioneSintattica.buildErrore(this.erroriValidazione, factory);
				}
				else {
					errore = ModIValidazioneSintattica.buildErrore(this.erroriProcessamento, factory);
				}
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, errore);
				msgDiag.logPersonalizzato(DIAGNOSTIC_VALIDATE+tipoDiagnostico+DIAGNOSTIC_FALLITA);
						
			}
			
		}catch(Exception e){
			
			if(msgDiag!=null) {
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.getMessage());
				msgDiag.logPersonalizzato(DIAGNOSTIC_VALIDATE+tipoDiagnostico+DIAGNOSTIC_FALLITA);
			}
			
			this.erroriProcessamento.add(this.validazioneUtils.newEccezioneProcessamento(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO, 
					e.getMessage(),e));
		}
	}

	private void checkExp(String exp, Date now, boolean rest, String prefix) throws ProtocolException {
		
		boolean enabled = true;
		if(rest) {
			enabled = this.modiProperties.isRestSecurityTokenClaimsExpTimeCheck();
		}
		else {
			enabled = this.modiProperties.isSoapSecurityTokenTimestampExpiresTimeCheck();
		}
		if(!enabled) {
			return;
		}
		
		if(prefix==null) {
			prefix="";
		}
		Date dateExp = null;
		try {
			dateExp = DateUtils.getSimpleDateFormatMs().parse(exp);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		/*
		 *   The "exp" (expiration time) claim identifies the expiration time on
			 *   or after which the JWT MUST NOT be accepted for processing.  The
			 *   processing of the "exp" claim requires that the current date/time
			 *   MUST be before the expiration date/time listed in the "exp" claim.
		 **/
		Date checkNow = now;
		Long tolerance = null;
		if(rest) {
			tolerance = this.modiProperties.getRestSecurityTokenClaimsExpTimeCheck_toleranceMilliseconds();
		}
		else {
			tolerance = this.modiProperties.getSoapSecurityTokenTimestampExpiresTimeCheck_toleranceMilliseconds();
		}
		if(tolerance!=null && tolerance.longValue()>0) {
			checkNow = new Date(now.getTime() - tolerance.longValue());
		}
		if(!checkNow.before(dateExp)){
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.MESSAGGIO_SCADUTO, 
					prefix+"Token scaduto in data '"+exp+"'"));
		}
	}
	
	private void checkNbf(String nbf, Date now, String prefix) throws ProtocolException {
		if(prefix==null) {
			prefix="";
		}
		Date dateNbf = null;
		try {
			dateNbf = DateUtils.getSimpleDateFormatMs().parse(nbf);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		/*
		 *   The "nbf" (not before) claim identifies the time before which the JWT
		 *   MUST NOT be accepted for processing.  The processing of the "nbf"
		 *   claim requires that the current date/time MUST be after or equal to
		 *   the not-before date/time listed in the "nbf" claim. 
		 **/
		if(!dateNbf.before(now)){
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.MESSAGGIO_SCADUTO, 
					prefix+"Token non utilizzabile prima della data '"+nbf+"'"));
		}
	}
	
	private void checkIat(String iat, OpenSPCoop2Message msg, boolean rest, String prefix) throws ProtocolException {
		if(prefix==null) {
			prefix="";
		}
		Date dateIat = null;
		try {
			dateIat = DateUtils.getSimpleDateFormatMs().parse(iat);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		/*
		 *   The "iat" (issued at) claim identifies the time at which the JWT was
			 *   issued.  This claim can be used to determine the age of the JWT.
			 *   The iat Claim can be used to reject tokens that were issued too far away from the current time, 
			 *   limiting the amount of time that nonces need to be stored to prevent attacks. The acceptable range is Client specific. 
		 **/
		Long old = null;
		Object iatObject = null;
		if(msg!=null) {
			iatObject = msg.getContextProperty(ModICostanti.MODIPA_OPENSPCOOP2_MSG_CONTEXT_IAT_TTL_CHECK);
		}
		if(iatObject instanceof Long) {
			old = (Long) iatObject;
		}
		if(old==null) {
			if(rest) {
				old = this.modiProperties.getRestSecurityTokenClaimsIatTimeCheck_milliseconds();
			}
			else {
				old = this.modiProperties.getSoapSecurityTokenTimestampCreatedTimeCheck_milliseconds();
			}
		}
		if(old!=null) {
			Date oldMax = new Date((DateManager.getTimeMillis() - old.longValue()));
			if(dateIat.before(oldMax)) {
				logError(prefix+"Token creato da troppo tempo (data creazione: '"+iat+"', data più vecchia consentita: '"+DateUtils.getSimpleDateFormatMs().format(oldMax)+"', configurazione ms: '"+old.longValue()+"')");
				this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.MESSAGGIO_SCADUTO, 
						prefix+"Token creato da troppo tempo (data creazione: '"+iat+"')"));
			}
		}
		
		
		Long future = null;
		if(rest) {
			future = this.modiProperties.getRestSecurityTokenClaimsIatTimeCheck_futureToleranceMilliseconds();
		}
		else {
			future = this.modiProperties.getSoapSecurityTokenTimestampCreatedTimeCheck_futureToleranceMilliseconds();
		}
		if(future!=null) {
			Date futureMax = new Date((DateManager.getTimeMillis() + future.longValue()));
			if(dateIat.after(futureMax)) {
				logError(prefix+"Token creato nel futuro (data creazione: '"+iat+"', data massima futura consentita: '"+DateUtils.getSimpleDateFormatMs().format(futureMax)+"', configurazione ms: '"+future.longValue()+"')");
				this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.ORA_REGISTRAZIONE_NON_VALIDA, 
						prefix+"Token creato nel futuro (data creazione: '"+iat+"')"));
			}
		}
	}
	
	private void logError(String msg) {
		if(this.log!=null) {
			this.log.error(msg);
		}
	}
}
