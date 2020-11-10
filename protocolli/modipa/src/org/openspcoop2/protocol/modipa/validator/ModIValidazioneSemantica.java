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



package org.openspcoop2.protocol.modipa.validator;

import java.util.ArrayList;
import java.util.Date;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.token.parser.Claims;
import org.openspcoop2.protocol.basic.validator.ValidazioneSemantica;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.modipa.utils.ModIPropertiesUtils;
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
			
			if(securityMessageProfile!=null && !ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_UNDEFINED.equals(securityMessageProfile)) {
				
				Date now = DateManager.getDate();
				
				String exp = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_EXP);
				if(exp!=null) {
					Date dateExp = DateUtils.getSimpleDateFormatMs().parse(exp);
					/*
					 *   The "exp" (expiration time) claim identifies the expiration time on
	   				 *   or after which the JWT MUST NOT be accepted for processing.  The
	   				 *   processing of the "exp" claim requires that the current date/time
	   				 *   MUST be before the expiration date/time listed in the "exp" claim.
					 **/
					if(!now.before(dateExp)){
						this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.MESSAGGIO_SCADUTO, 
								"Token scaduto in data '"+exp+"'"));
					}
				}
				
				String nbf = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_NBF);
				if(nbf!=null) {
					Date dateNbf = DateUtils.getSimpleDateFormatMs().parse(nbf);
					/*
					 *   The "nbf" (not before) claim identifies the time before which the JWT
					 *   MUST NOT be accepted for processing.  The processing of the "nbf"
					 *   claim requires that the current date/time MUST be after or equal to
					 *   the not-before date/time listed in the "nbf" claim. 
					 **/
					if(!dateNbf.before(now)){
						this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.MESSAGGIO_SCADUTO, 
								"Token non utilizzabile prima della data '"+nbf+"'"));
					}
				}
				
				String iat = busta.getProperty(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_IAT);
				if(iat!=null) {
					Date dateIat = DateUtils.getSimpleDateFormatMs().parse(iat);
					/*
					 *   The "iat" (issued at) claim identifies the time at which the JWT was
	   				 *   issued.  This claim can be used to determine the age of the JWT.
	   				 *   The iat Claim can be used to reject tokens that were issued too far away from the current time, 
	   				 *   limiting the amount of time that nonces need to be stored to prevent attacks. The acceptable range is Client specific. 
					 **/
					Integer old = null;
					if(rest) {
						old = this.modiProperties.getRestSecurityTokenClaimsIatTimeCheck_milliseconds();
					}
					else {
						old = this.modiProperties.getSoapSecurityTokenTimestampCreatedTimeCheck_milliseconds();
					}
					if(old!=null) {
						Date oldMax = new Date((DateManager.getTimeMillis() - old.intValue()));
						if(dateIat.before(oldMax)) {
							this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.MESSAGGIO_SCADUTO, 
									"Token creato da troppo tempo (data creazione: '"+iat+"')"));
						}
					}
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
				
				if(isRichiesta) {
					
					if(msg.getTransportRequestContext()==null || msg.getTransportRequestContext().getInterfaceName()==null) {
						throw new Exception("ID Porta non presente");
					}
					IDPortaApplicativa idPA = new IDPortaApplicativa();
					idPA.setNome(msg.getTransportRequestContext().getInterfaceName());
					PortaApplicativa pa = factory.getCachedConfigIntegrationReader(state).getPortaApplicativa(idPA);
					boolean autorizzazioneModIPAAbilitata = false;
					if(pa.getServiziApplicativiAutorizzati()!=null) {
						autorizzazioneModIPAAbilitata = pa.getServiziApplicativiAutorizzati().sizeServizioApplicativoList()>0;
					}
					
					if(autorizzazioneModIPAAbilitata) {
					
						if(busta.getServizioApplicativoFruitore()==null || CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(busta.getServizioApplicativoFruitore())) {
							this.context.addObject(Costanti.ERRORE_AUTORIZZAZIONE, Costanti.ERRORE_TRUE);
							this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA, 
									"Applicativo Mittente non identificato"));
						}
						else {
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
								this.context.addObject(Costanti.ERRORE_AUTORIZZAZIONE, Costanti.ERRORE_TRUE);
								this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA, 
										"Applicativo Mittente "+idApp+" non autorizzato"));
							}
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
	
}
