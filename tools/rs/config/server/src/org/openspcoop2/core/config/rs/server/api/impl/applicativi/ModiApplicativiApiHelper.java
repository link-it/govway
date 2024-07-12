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

package org.openspcoop2.core.config.rs.server.api.impl.applicativi;

import java.util.Map;

import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.rs.server.api.impl.ProtocolPropertiesHelper;
import org.openspcoop2.core.config.rs.server.model.Applicativo;
import org.openspcoop2.core.config.rs.server.model.AuthenticationHttps;
import org.openspcoop2.core.config.rs.server.model.AuthenticationToken;
import org.openspcoop2.core.config.rs.server.model.DominioEnum;
import org.openspcoop2.core.config.rs.server.model.ModIApplicativoAuthenticationToken;
import org.openspcoop2.core.config.rs.server.model.ModIApplicativoEsterno;
import org.openspcoop2.core.config.rs.server.model.ModIApplicativoInterno;
import org.openspcoop2.core.config.rs.server.model.ModIApplicativoSicurezzaMessaggio;
import org.openspcoop2.core.config.rs.server.model.ModIKeyStoreArchive;
import org.openspcoop2.core.config.rs.server.model.ModIKeyStoreFileApplicativo;
import org.openspcoop2.core.config.rs.server.model.ModIKeyStoreHSMApplicativo;
import org.openspcoop2.core.config.rs.server.model.ModIKeystoreEnum;
import org.openspcoop2.core.config.rs.server.model.ModIKeystoreTipologiaEnum;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.modipa.properties.ModIDynamicConfigurationKeystoreUtilities;
import org.openspcoop2.protocol.sdk.properties.AbstractProperty;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesFactory;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;

/**
 * ModiApplicativiApiHelper
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ModiApplicativiApiHelper {
	
	private ModiApplicativiApiHelper() {}

	public static ProtocolProperties getProtocolProperties(Applicativo body, ServizioApplicativo sa, ApplicativiEnv env) {
		/**		if(body.getModi() == null) {
		//			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare la configurazione 'ModI'");
		//		}*/

		ProtocolProperties p = new ProtocolProperties();

		if(isDominioInterno(sa, env)) {

			if(body.getModi() != null) {
				if(body.getModi() instanceof ModIApplicativoInterno) {
					ModIApplicativoInterno mai = (ModIApplicativoInterno)body.getModi();

					if(mai.getSicurezzaMessaggio()==null && mai.getToken()==null) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione 'ModI' non valida: definire almeno una configurazione tra sicurezza messaggio e token");
					}
					
					if(mai.getSicurezzaMessaggio()!=null) {
						p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_SICUREZZA_MESSAGGIO, true));
	
						if(mai.getSicurezzaMessaggio().getKeystore().getTipologia().equals(ModIKeystoreTipologiaEnum.HSM)) {
							ModIKeyStoreHSMApplicativo hsmKeystore = (ModIKeyStoreHSMApplicativo)mai.getSicurezzaMessaggio().getKeystore();
							p.addProperty(ModICostanti.MODIPA_KEYSTORE_MODE, ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_HSM);
	
	
							p.addProperty(ModICostanti.MODIPA_KEYSTORE_TYPE, hsmKeystore.getPcks11Tipo());
							p.addProperty(ModICostanti.MODIPA_KEY_ALIAS, hsmKeystore.getKeyAlias());
							if( hsmKeystore.getKeystoreCertificato()!=null && hsmKeystore.getKeystoreCertificato().length>0) {
								String filename = "applicativo.cer";
								p.addProperty(ModICostanti.MODIPA_KEYSTORE_CERTIFICATE, hsmKeystore.getKeystoreCertificato(), filename, filename);
								
								try {
									CertificateInfo cert = ModIDynamicConfigurationKeystoreUtilities.readKeystoreConfig(p, true);
									if(cert!=null && cert.getSubject()!=null) {
										p.addProperty(ModICostanti.MODIPA_KEY_CN_SUBJECT, cert.getSubject().toString());
										if(cert.getIssuer()!=null) {
											p.addProperty(ModICostanti.MODIPA_KEY_CN_ISSUER, cert.getIssuer().toString());
										}
									}
								}catch(Exception e) {
									// errore sollevato in validazione
								}
							}
						}
						else if(mai.getSicurezzaMessaggio().getKeystore().getTipologia().equals(ModIKeystoreTipologiaEnum.FILESYSTEM)) {
							ModIKeyStoreFileApplicativo fsKeystore = (ModIKeyStoreFileApplicativo)mai.getSicurezzaMessaggio().getKeystore();
							p.addProperty(ModICostanti.MODIPA_KEYSTORE_MODE, ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_PATH);
	
	
							String tipo = null;
							switch(fsKeystore.getKeystoreTipo()) {
							case JKS:tipo = ModICostanti.MODIPA_KEYSTORE_TYPE_VALUE_JKS;
							break;
							case PKCS12:tipo = ModICostanti.MODIPA_KEYSTORE_TYPE_VALUE_PKCS12;
							break;
							default:
								break;
							}
	
							p.addProperty(ModICostanti.MODIPA_KEYSTORE_TYPE, tipo);
							p.addProperty(ModICostanti.MODIPA_KEY_ALIAS, fsKeystore.getKeyAlias());
							p.addProperty(ModICostanti.MODIPA_KEY_PASSWORD, fsKeystore.getKeyPassword());
							p.addProperty(ModICostanti.MODIPA_KEYSTORE_PASSWORD, fsKeystore.getKeystorePassword());
							p.addProperty(ModICostanti.MODIPA_KEYSTORE_PATH, fsKeystore.getKeystorePath());
							p.addProperty(ModICostanti.MODIPA_KEYSTORE_BYOK_POLICY, fsKeystore.getKeystoreByokPolicy());
							
							if( fsKeystore.getKeystoreCertificato()!=null && fsKeystore.getKeystoreCertificato().length>0) {
								String filename = "applicativo.cer";
								p.addProperty(ModICostanti.MODIPA_KEYSTORE_CERTIFICATE, fsKeystore.getKeystoreCertificato(), filename, filename);
								
								try {
									CertificateInfo cert = ModIDynamicConfigurationKeystoreUtilities.readKeystoreConfig(p, true);
									if(cert!=null && cert.getSubject()!=null) {
										p.addProperty(ModICostanti.MODIPA_KEY_CN_SUBJECT, cert.getSubject().toString());
										if(cert.getIssuer()!=null) {
											p.addProperty(ModICostanti.MODIPA_KEY_CN_ISSUER, cert.getIssuer().toString());
										}
									}
								}catch(Exception e) {
									// errore sollevato in validazione
								}
							}
							
						} else {
							ModIKeyStoreArchive archiveKeystore = (ModIKeyStoreArchive)mai.getSicurezzaMessaggio().getKeystore();
							p.addProperty(ModICostanti.MODIPA_KEYSTORE_MODE, ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE);
	
	
							String tipo = null;
							String filename = null;
	
							switch(archiveKeystore.getKeystoreTipo()) {
							case JKS:tipo = ModICostanti.MODIPA_KEYSTORE_TYPE_VALUE_JKS; filename = "Keystore.jks";
							break;
							case PKCS12:tipo = ModICostanti.MODIPA_KEYSTORE_TYPE_VALUE_PKCS12; filename = "Keystore.p12";
							break;
							default:
								break;
							}
	
							p.addProperty(ModICostanti.MODIPA_KEYSTORE_TYPE, tipo);
							p.addProperty(ModICostanti.MODIPA_KEY_ALIAS, archiveKeystore.getKeyAlias());
							p.addProperty(ModICostanti.MODIPA_KEY_PASSWORD, archiveKeystore.getKeyPassword());
							p.addProperty(ModICostanti.MODIPA_KEYSTORE_PASSWORD, archiveKeystore.getKeystorePassword());
							p.addProperty(ModICostanti.MODIPA_KEYSTORE_ARCHIVE, archiveKeystore.getKeystoreArchivio(), filename, filename);
							
							try {
								CertificateInfo cert = ModIDynamicConfigurationKeystoreUtilities.readKeystoreConfig(p, false);
								if(cert!=null && cert.getSubject()!=null) {
									p.addProperty(ModICostanti.MODIPA_KEY_CN_SUBJECT, cert.getSubject().toString());
									if(cert.getIssuer()!=null) {
										p.addProperty(ModICostanti.MODIPA_KEY_CN_ISSUER, cert.getIssuer().toString());
									}
								}
							}catch(Exception e) {
								// errore sollevato in validazione
							}
						}
	
						if(mai.getSicurezzaMessaggio().getReplyAudience()!=null) {
							p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE, mai.getSicurezzaMessaggio().getReplyAudience());
						}
	
						if(mai.getSicurezzaMessaggio().getUrlX5u()!=null) {
							p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_SA_RICHIESTA_X509_VALUE_X5URL, mai.getSicurezzaMessaggio().getUrlX5u());
						}
					}
					
					if(mai.getToken()!=null) {
						p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_SICUREZZA_TOKEN, true));
						if(mai.getToken().getTokenPolicy()!=null) {
							p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_SICUREZZA_TOKEN_POLICY, mai.getToken().getTokenPolicy()));
						}
						p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_SICUREZZA_TOKEN_CLIENT_ID, mai.getToken().getIdentificativo()));
						if(mai.getToken().getKid()!=null) {
							p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_SICUREZZA_TOKEN_KID_ID, mai.getToken().getKid()));
						}
					}

				} else {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione 'ModI' non valida");
				}

			} else {
				p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_SICUREZZA_MESSAGGIO, false));
			}
		} else {

			if(!(body.getCredenziali() instanceof AuthenticationHttps) && !(body.getCredenziali() instanceof AuthenticationToken)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non sono state fornite credenziali (certificato o tokenClientId) che identificano l’applicativo");
			}
			if(body.getModi() != null) {
	
				if(body.getModi() instanceof ModIApplicativoEsterno) {
					ModIApplicativoEsterno mae = (ModIApplicativoEsterno)body.getModi();
					if(mae.getReplyAudience()!=null) {
						p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE, mae.getReplyAudience());
					}
				} else {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione 'ModI' non valida");
				}
			}
		}

		return p;

	}

	private static boolean isDominioInterno(ServizioApplicativo sa, ApplicativiEnv env) {
		IDSoggetto idSoggetto = new IDSoggetto(sa.getTipoSoggettoProprietario(),sa.getNomeSoggettoProprietario());

		return env.isDominioInterno(idSoggetto);
	}

	public static void populateProtocolInfo(ServizioApplicativo sa, ApplicativiEnv env, Applicativo ret) throws Exception {

		Map<String, AbstractProperty<?>> p = ApplicativiApiHelper.getProtocolPropertiesMap(sa, env);

		if(isDominioInterno(sa, env)) {

			ModIApplicativoInterno app = new ModIApplicativoInterno();
			app.setDominio(DominioEnum.INTERNO);

			boolean enabledSicurezzaMessaggio = false;
			AbstractProperty<?> ap = ProtocolPropertiesHelper.getProperty(p, ModICostanti.MODIPA_SICUREZZA_MESSAGGIO, false);
			if(ap!=null && ap.getValue()!=null) {
				enabledSicurezzaMessaggio = ProtocolPropertiesHelper.getBooleanProperty(p, ModICostanti.MODIPA_SICUREZZA_MESSAGGIO, true);
			}

			if(enabledSicurezzaMessaggio) {
				ModIApplicativoSicurezzaMessaggio sicurezzaMsg = new ModIApplicativoSicurezzaMessaggio();

				String keystoreModeString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_KEYSTORE_MODE, true);

				if(keystoreModeString.equals(ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_HSM)) {
					ModIKeyStoreHSMApplicativo datiKeystore = new ModIKeyStoreHSMApplicativo();
					datiKeystore.tipologia(ModIKeystoreTipologiaEnum.HSM);

					String keystoreTipoString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_KEYSTORE_TYPE, true);
					datiKeystore.setPcks11Tipo(keystoreTipoString);
					datiKeystore.setKeyAlias(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_KEY_ALIAS, true));
					
					datiKeystore.setKeystoreCertificato(ProtocolPropertiesHelper.getByteArrayProperty(p, ModICostanti.MODIPA_KEYSTORE_CERTIFICATE, false));

					sicurezzaMsg.setKeystore(datiKeystore);
				}
				else if(keystoreModeString.equals(ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_PATH)) {
					ModIKeyStoreFileApplicativo datiKeystore = new ModIKeyStoreFileApplicativo();
					datiKeystore.tipologia(ModIKeystoreTipologiaEnum.FILESYSTEM);

					ModIKeystoreEnum keystoreTipo = null;

					String keystoreTipoString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_KEYSTORE_TYPE, true);

					if(keystoreTipoString.equals(ModICostanti.MODIPA_KEYSTORE_TYPE_VALUE_JKS)) {
						keystoreTipo = ModIKeystoreEnum.JKS;
					} else if(keystoreTipoString.equals(ModICostanti.MODIPA_KEYSTORE_TYPE_VALUE_PKCS12)) {
						keystoreTipo = ModIKeystoreEnum.PKCS12;
					}

					datiKeystore.setKeystoreTipo(keystoreTipo);
					datiKeystore.setKeyAlias(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_KEY_ALIAS, true));
					datiKeystore.setKeyPassword(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_KEY_PASSWORD, true));
					datiKeystore.setKeystorePassword(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_KEYSTORE_PASSWORD, true));
					datiKeystore.setKeystorePath(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_KEYSTORE_PATH, true));
					datiKeystore.setKeystoreByokPolicy(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_KEYSTORE_BYOK_POLICY, false));
					
					datiKeystore.setKeystoreCertificato(ProtocolPropertiesHelper.getByteArrayProperty(p, ModICostanti.MODIPA_KEYSTORE_CERTIFICATE, false));

					sicurezzaMsg.setKeystore(datiKeystore);
				} else if(keystoreModeString.equals(ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE)) {
					ModIKeyStoreArchive datiKeystore = new ModIKeyStoreArchive().tipologia(ModIKeystoreTipologiaEnum.ARCHIVIO);

					ModIKeystoreEnum keystoreTipo = null;

					String keystoreTipoString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_KEYSTORE_TYPE, true);

					if(keystoreTipoString.equals(ModICostanti.MODIPA_KEYSTORE_TYPE_VALUE_JKS)) {
						keystoreTipo = ModIKeystoreEnum.JKS;
					} else if(keystoreTipoString.equals(ModICostanti.MODIPA_KEYSTORE_TYPE_VALUE_PKCS12)) {
						keystoreTipo = ModIKeystoreEnum.PKCS12;
					}

					datiKeystore.setKeystoreTipo(keystoreTipo);
					datiKeystore.setKeyAlias(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_KEY_ALIAS, true));
					datiKeystore.setKeyPassword(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_KEY_PASSWORD, true));
					datiKeystore.setKeystorePassword(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_KEYSTORE_PASSWORD, true));
					datiKeystore.setKeystoreArchivio(ProtocolPropertiesHelper.getByteArrayProperty(p, ModICostanti.MODIPA_KEYSTORE_ARCHIVE, true));

					sicurezzaMsg.setKeystore(datiKeystore);
				}

				String wsaTo = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE, false);
				if(wsaTo != null) {
					sicurezzaMsg.setReplyAudience(wsaTo);
				}

				String x509 = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_SA_RICHIESTA_X509_VALUE_X5URL, false);
				if(x509 != null) {
					sicurezzaMsg.setUrlX5u(x509);
				}
				app.setSicurezzaMessaggio(sicurezzaMsg);
			}

			boolean enabledSicurezzaToken = false;
			ap = ProtocolPropertiesHelper.getProperty(p, ModICostanti.MODIPA_SICUREZZA_TOKEN, false);
			if(ap!=null && ap.getValue()!=null) {
				enabledSicurezzaToken = ProtocolPropertiesHelper.getBooleanProperty(p, ModICostanti.MODIPA_SICUREZZA_TOKEN, true);
			}

			if(enabledSicurezzaToken) {
				
				ModIApplicativoAuthenticationToken token = new ModIApplicativoAuthenticationToken();
				
				String tokenPolicy = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_SICUREZZA_TOKEN_POLICY, false);
				if(tokenPolicy != null) {
					token.setTokenPolicy(tokenPolicy);
				}
				
				String identificativo = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_SICUREZZA_TOKEN_CLIENT_ID, false);
				if(identificativo != null) {
					token.setIdentificativo(identificativo);
				}
				
				String kid = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_SICUREZZA_TOKEN_KID_ID, false);
				if(kid != null) {
					token.setKid(kid);
				}
				
				app.setToken(token);
			}
			
			if(enabledSicurezzaMessaggio || enabledSicurezzaToken) {
				// modi richiede la presenza obbligatoria della sicurezza
				ret.setModi(app);
			}
		} else {
			ModIApplicativoEsterno app = new ModIApplicativoEsterno();
			app.setDominio(DominioEnum.ESTERNO);

			String wsaTo = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE, false);
			if(wsaTo != null) {
				app.setReplyAudience(wsaTo);
			}

			ret.setModi(app);

		}

	}

}
