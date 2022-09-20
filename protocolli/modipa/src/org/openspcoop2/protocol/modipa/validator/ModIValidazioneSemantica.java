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



package org.openspcoop2.protocol.modipa.validator;

import java.util.ArrayList;
import java.util.Date;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.RuoloTipologia;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.config.ConfigurazionePdDReader;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.token.parser.Claims;
import org.openspcoop2.protocol.basic.validator.ValidazioneSemantica;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.modipa.utils.ModIPropertiesUtils;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.RuoloBusta;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazione;
import org.openspcoop2.protocol.sdk.validator.ValidazioneSemanticaResult;
import org.openspcoop2.protocol.sdk.validator.ValidazioneUtils;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;



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
	protected java.util.List<Eccezione> erroriValidazione = new ArrayList<Eccezione>();
	/** Errori di processamento riscontrati sulla busta */
	protected java.util.List<Eccezione> erroriProcessamento = new ArrayList<Eccezione>();
	
	public ModIValidazioneSemantica(IProtocolFactory<?> factory, IState state) throws ProtocolException {
		super(factory, state);
		this.modiProperties = ModIProperties.getInstance();
		this.validazioneUtils = new ValidazioneUtils(factory);
	}

	@Override
	public ValidazioneSemanticaResult valida(OpenSPCoop2Message msg, Busta busta, 
			ProprietaValidazione proprietaValidazione, 
			RuoloBusta tipoBusta) throws ProtocolException{
		
		this.valida(msg,busta,tipoBusta, this.protocolFactory, this.state);
		
		java.util.List<Eccezione> erroriValidazione = null;
		if(this.erroriValidazione.size()>0){
			erroriValidazione = this.erroriValidazione;
			if(this.context!=null) {
				this.context.addObject(Costanti.ERRORE_VALIDAZIONE_PROTOCOLLO, Costanti.ERRORE_TRUE);
			}
		}
		java.util.List<Eccezione> erroriProcessamento = null;
		if(this.erroriProcessamento.size()>0){
			erroriValidazione = this.erroriProcessamento;
		}
		ValidazioneSemanticaResult validazioneSemantica = new ValidazioneSemanticaResult(erroriValidazione, erroriProcessamento, null, null, null, null);
		return validazioneSemantica;
		
	}

	private void valida(OpenSPCoop2Message msg,Busta busta, RuoloBusta tipoBusta, IProtocolFactory<?> factory, IState state) throws ProtocolException{
		try{
			
			boolean isRichiesta = RuoloBusta.RICHIESTA.equals(tipoBusta);
			
			boolean rest = ServiceBinding.REST.equals(msg.getServiceBinding());
			
			String securityMessageProfile = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO);
			
			if(securityMessageProfile!=null) {
				securityMessageProfile = ModIPropertiesUtils.convertProfiloSicurezzaToConfigurationValue(securityMessageProfile);
			}
			
			boolean sicurezzaMessaggio = securityMessageProfile!=null && !ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_UNDEFINED.equals(securityMessageProfile);
			if(sicurezzaMessaggio) {
				
				Date now = DateManager.getDate();
				
				String prefixIntegrity = "[Header '"+this.modiProperties.getRestSecurityTokenHeaderModI()+"'] ";
				
				String exp = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_EXP);
				if(exp!=null) {
					checkExp(exp, now, "");
				}
				
				String expIntegrity = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_REST_INTEGRITY_EXP);
				if(expIntegrity!=null) {
					checkExp(expIntegrity, now, prefixIntegrity);
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
						if(buildSecurityTokenInRequestObject!=null && buildSecurityTokenInRequestObject instanceof Boolean) {
							buildSecurityTokenInRequest = (Boolean) buildSecurityTokenInRequestObject;
						}
						
						if(isRichiesta || buildSecurityTokenInRequest) {
						
							this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
									isRichiesta ? CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_EROGATORE_NON_VALIDO :
										CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_FRUITORE_NON_VALIDO, 
									"Token contenente un claim '"+Claims.JSON_WEB_TOKEN_RFC_7519_AUDIENCE+"' non valido"));
							
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
						String prefix = "[Header '"+this.modiProperties.getRestSecurityTokenHeaderModI()+"'] ";
						if(!audienceIntegrityAtteso.equals(audienceIntegrity)) {
							this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(
									isRichiesta ? CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_EROGATORE_NON_VALIDO :
										CodiceErroreCooperazione.SERVIZIO_APPLICATIVO_FRUITORE_NON_VALIDO, 
									prefix+"Token contenente un claim '"+Claims.JSON_WEB_TOKEN_RFC_7519_AUDIENCE+"' non valido"));
						}
					}
				}
				
			}
			
			if(isRichiesta) {
				
				// vale sia per sicurezza messaggio che per token
				// durante l'identificazione viene identificato 1 solo applicativo (non possono essere differenti tra token e trasporto)
				// viene quindi inserito dentro busta e usato per i controlli sottostanti
				
				if(msg.getTransportRequestContext()==null || msg.getTransportRequestContext().getInterfaceName()==null) {
					throw new Exception("ID Porta non presente");
				}
				IDPortaApplicativa idPA = new IDPortaApplicativa();
				idPA.setNome(msg.getTransportRequestContext().getInterfaceName());
				PortaApplicativa pa = factory.getCachedConfigIntegrationReader(state).getPortaApplicativa(idPA);
				
				
				/** Identificazione Mittente by LineeGuida e Token */
				boolean saIdentificatoBySecurity = busta.getServizioApplicativoFruitore()!=null && !CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(busta.getServizioApplicativoFruitore());
				
				boolean sicurezzaToken = this.context.containsKey(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE);
				boolean saIdentificatoByToken = false;
				if(sicurezzaToken) {
					IDServizioApplicativo idSAbyToken = null;
					StringBuilder sbError = new StringBuilder();
					try {
						idSAbyToken = IdentificazioneApplicativoMittenteUtils.identificazioneApplicativoMittenteByToken(this.log, state, busta, this.context, sbError);
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
				}
				
				boolean saFruitoreAnonimo = busta.getServizioApplicativoFruitore()==null || CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(busta.getServizioApplicativoFruitore());
								
				/** Tipi di Autorizzazione definiti */
				boolean autorizzazionePerRichiedente = false;
				if(pa.getServiziApplicativiAutorizzati()!=null) {
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
				if(autorizzazionePerRichiedente || 
						(autorizzazionePerRuolo && checkRuoloRegistro && !checkRuoloEsterno) ) {	
					if(saFruitoreAnonimo) {
						this.context.addObject(Costanti.ERRORE_AUTORIZZAZIONE, Costanti.ERRORE_TRUE);
						this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA, 
								"Applicativo Mittente non identificato"));
						return;
					}
				}
				if(!saFruitoreAnonimo) {
					if(autorizzazionePerRichiedente || checkRuoloRegistro) {
						// se utilizzo l'informazione dell'applicativo, tale informazione deve essere consistente rispetto a tutti i criteri di sicurezza
						if(sicurezzaMessaggio) {
							if(!saIdentificatoBySecurity) {
								this.context.addObject(Costanti.ERRORE_AUTORIZZAZIONE, Costanti.ERRORE_TRUE);
								String idApp = busta.getServizioApplicativoFruitore() + " (Soggetto: "+busta.getMittente()+")";
								this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA, 
										"Applicativo Mittente "+idApp+" non autorizzato; il certificato di firma non corrisponde all'applicativo"));
								return;
							}
						}
						if(sicurezzaToken) {
							if(!saIdentificatoByToken) {
								this.context.addObject(Costanti.ERRORE_AUTORIZZAZIONE, Costanti.ERRORE_TRUE);
								String idApp = busta.getServizioApplicativoFruitore() + " (Soggetto: "+busta.getMittente()+")";
								this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA, 
										"Applicativo Mittente "+idApp+" non autorizzato; il claim 'clientId' presente nel token non corrisponde all'applicativo"));
								return;
							}
						}
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
						String idApp = busta.getServizioApplicativoFruitore() + " (Soggetto: "+busta.getMittente()+")";
						eccezioneAutorizzazionePerRichiedente = this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA, 
								"Applicativo Mittente "+idApp+" non autorizzato");
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
    					sa = factory.getCachedConfigIntegrationReader(state).getServizioApplicativo(idSA);
    				}
    				boolean authRuoli = ConfigurazionePdDReader._autorizzazioneRoles(
    								RegistroServiziManager.getInstance(state),
    								null, sa, 
    								null, false, 
    								this.context,
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
			
		}catch(Exception e){
			this.erroriProcessamento.add(this.validazioneUtils.newEccezioneProcessamento(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO, 
					e.getMessage(),e));
			return;
		}
	}

	private void checkExp(String exp, Date now, String prefix) throws Exception {
		if(prefix==null) {
			prefix="";
		}
		Date dateExp = DateUtils.getSimpleDateFormatMs().parse(exp);
		/*
		 *   The "exp" (expiration time) claim identifies the expiration time on
			 *   or after which the JWT MUST NOT be accepted for processing.  The
			 *   processing of the "exp" claim requires that the current date/time
			 *   MUST be before the expiration date/time listed in the "exp" claim.
		 **/
		if(!now.before(dateExp)){
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.MESSAGGIO_SCADUTO, 
					prefix+"Token scaduto in data '"+exp+"'"));
		}
	}
	
	private void checkNbf(String nbf, Date now, String prefix) throws Exception {
		if(prefix==null) {
			prefix="";
		}
		Date dateNbf = DateUtils.getSimpleDateFormatMs().parse(nbf);
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
	
	private void checkIat(String iat, OpenSPCoop2Message msg, boolean rest, String prefix) throws Exception {
		if(prefix==null) {
			prefix="";
		}
		Date dateIat = DateUtils.getSimpleDateFormatMs().parse(iat);
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
		if(iatObject!=null && iatObject instanceof Long) {
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
				this.log.error(prefix+"Token creato da troppo tempo (data creazione: '"+iat+"', data più vecchia consentita: '"+DateUtils.getSimpleDateFormatMs().format(oldMax)+"', configurazione ms: '"+old.longValue()+"')");
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
				this.log.error(prefix+"Token creato nel futuro (data creazione: '"+iat+"', data massima futura consentita: '"+DateUtils.getSimpleDateFormatMs().format(futureMax)+"', configurazione ms: '"+future.longValue()+"')");
				this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.ORA_REGISTRAZIONE_NON_VALIDA, 
						prefix+"Token creato nel futuro (data creazione: '"+iat+"')"));
			}
		}
	}
}
