package org.openspcoop2.core.config.rs.server.api.impl.applicativi;

import java.util.Map;

import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.rs.server.api.impl.ProtocolPropertiesHelper;
import org.openspcoop2.core.config.rs.server.model.Applicativo;
import org.openspcoop2.core.config.rs.server.model.AuthenticationHttps;
import org.openspcoop2.core.config.rs.server.model.DominioEnum;
import org.openspcoop2.core.config.rs.server.model.ModIApplicativoEsterno;
import org.openspcoop2.core.config.rs.server.model.ModIApplicativoInterno;
import org.openspcoop2.core.config.rs.server.model.ModIApplicativoSicurezzaMessaggio;
import org.openspcoop2.core.config.rs.server.model.ModIKeyStoreArchive;
import org.openspcoop2.core.config.rs.server.model.ModIKeyStoreFile;
import org.openspcoop2.core.config.rs.server.model.ModIKeystoreEnum;
import org.openspcoop2.core.config.rs.server.model.ModIKeystoreTipologiaEnum;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.sdk.properties.AbstractProperty;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesFactory;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;

public class ModiApplicativiApiHelper {

	public static ProtocolProperties getProtocolProperties(Applicativo body, ServizioApplicativo sa, ApplicativiEnv env) {
		//		if(body.getModi() == null) {
		//			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare la configurazione 'ModI'");
		//		}

		ProtocolProperties p = new ProtocolProperties();

		if(isDominioInterno(sa, env)) {

			if(body.getModi() != null) {
				if(body.getModi() instanceof ModIApplicativoInterno) {
					ModIApplicativoInterno mai = (ModIApplicativoInterno)body.getModi();

					p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_SICUREZZA_MESSAGGIO, true));

					if(mai.getSicurezzaMessaggio().getKeystore().getTipologia().equals(ModIKeystoreTipologiaEnum.FILESYSTEM)) {
						ModIKeyStoreFile fsKeystore = (ModIKeyStoreFile)mai.getSicurezzaMessaggio().getKeystore();
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
					}

					if(mai.getSicurezzaMessaggio().getReplyAudience()!=null) {
						p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE, mai.getSicurezzaMessaggio().getReplyAudience());
					}

					if(mai.getSicurezzaMessaggio().getUrlX5u()!=null) {
						p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_SA_RICHIESTA_X509_VALUE_X5URL, mai.getSicurezzaMessaggio().getUrlX5u());
					}

				} else {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione 'ModI' non valida");
				}

			} else {
				p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_SICUREZZA_MESSAGGIO, false));
			}
		} else {

			if(!(body.getCredenziali() instanceof AuthenticationHttps)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Certificato, che identifica l’applicativo, non fornito");
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

			boolean enabled = ProtocolPropertiesHelper.getBooleanProperty(p, ModICostanti.MODIPA_SICUREZZA_MESSAGGIO, true);

			if(enabled) {
				ModIApplicativoSicurezzaMessaggio sicurezzaMsg = new ModIApplicativoSicurezzaMessaggio();

				String keystoreModeString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_KEYSTORE_MODE, true);

				if(keystoreModeString.equals(ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_PATH)) {
					ModIKeyStoreFile datiKeystore = new ModIKeyStoreFile().tipologia(ModIKeystoreTipologiaEnum.FILESYSTEM);

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

			ret.setModi(app);
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
