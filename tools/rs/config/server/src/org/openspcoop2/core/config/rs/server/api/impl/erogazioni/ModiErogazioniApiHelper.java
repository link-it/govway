package org.openspcoop2.core.config.rs.server.api.impl.erogazioni;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.config.rs.server.api.impl.ProtocolPropertiesHelper;
import org.openspcoop2.core.config.rs.server.model.Erogazione;
import org.openspcoop2.core.config.rs.server.model.ErogazioneModI;
import org.openspcoop2.core.config.rs.server.model.ErogazioneModIRest;
import org.openspcoop2.core.config.rs.server.model.ErogazioneModIRestRichiesta;
import org.openspcoop2.core.config.rs.server.model.ErogazioneModIRestRichiestaSicurezzaMessaggio;
import org.openspcoop2.core.config.rs.server.model.ErogazioneModIRestRisposta;
import org.openspcoop2.core.config.rs.server.model.ErogazioneModIRestRispostaSicurezzaMessaggio;
import org.openspcoop2.core.config.rs.server.model.ErogazioneModISoap;
import org.openspcoop2.core.config.rs.server.model.ErogazioneModISoapRichiesta;
import org.openspcoop2.core.config.rs.server.model.ErogazioneModISoapRichiestaSicurezzaMessaggio;
import org.openspcoop2.core.config.rs.server.model.ErogazioneModISoapRisposta;
import org.openspcoop2.core.config.rs.server.model.ErogazioneModISoapRispostaSicurezzaMessaggio;
import org.openspcoop2.core.config.rs.server.model.Fruizione;
import org.openspcoop2.core.config.rs.server.model.FruizioneModI;
import org.openspcoop2.core.config.rs.server.model.FruizioneModIRest;
import org.openspcoop2.core.config.rs.server.model.FruizioneModIRestRichiesta;
import org.openspcoop2.core.config.rs.server.model.FruizioneModIRestRichiestaSicurezzaMessaggio;
import org.openspcoop2.core.config.rs.server.model.FruizioneModIRestRisposta;
import org.openspcoop2.core.config.rs.server.model.FruizioneModIRestRispostaSicurezzaMessaggio;
import org.openspcoop2.core.config.rs.server.model.FruizioneModISoap;
import org.openspcoop2.core.config.rs.server.model.FruizioneModISoapRichiesta;
import org.openspcoop2.core.config.rs.server.model.FruizioneModISoapRichiestaSicurezzaMessaggio;
import org.openspcoop2.core.config.rs.server.model.FruizioneModISoapRisposta;
import org.openspcoop2.core.config.rs.server.model.FruizioneModISoapRispostaSicurezzaMessaggio;
import org.openspcoop2.core.config.rs.server.model.ModIKeyStoreArchive;
import org.openspcoop2.core.config.rs.server.model.ModIKeyStoreDefault;
import org.openspcoop2.core.config.rs.server.model.ModIKeyStoreFile;
import org.openspcoop2.core.config.rs.server.model.ModIKeyStoreRidefinito;
import org.openspcoop2.core.config.rs.server.model.ModIKeystoreEnum;
import org.openspcoop2.core.config.rs.server.model.ModIKeystoreTipologiaEnum;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaMessaggioRestAlgoritmoFirma;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaMessaggioRestRiferimentoX509;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaMessaggioRestRiferimentoX509Risposta;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaMessaggioSoapAlgoritmoFirma;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaMessaggioSoapFormaCanonicaXml;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaMessaggioSoapRiferimentoX509;
import org.openspcoop2.core.config.rs.server.model.ModITrustStoreDefault;
import org.openspcoop2.core.config.rs.server.model.ModITrustStoreRidefinito;
import org.openspcoop2.core.config.rs.server.model.ModITruststoreEnum;
import org.openspcoop2.core.config.rs.server.model.OneOfErogazioneModIModi;
import org.openspcoop2.core.config.rs.server.model.OneOfErogazioneModi;
import org.openspcoop2.core.config.rs.server.model.OneOfFruizioneModIModi;
import org.openspcoop2.core.config.rs.server.model.OneOfFruizioneModi;
import org.openspcoop2.core.config.rs.server.model.StatoDefaultRidefinitoEnum;
import org.openspcoop2.core.config.rs.server.model.TipoApiEnum;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.sdk.properties.AbstractProperty;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesFactory;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;

public class ModiErogazioniApiHelper {

	
	public static FruizioneModI getFruizioneModI(AccordoServizioParteSpecifica asps, ErogazioniEnv env, ProfiloEnum profilo, Map <String, AbstractProperty<?>> p) throws Exception {
		if(profilo == null || (!profilo.equals(ProfiloEnum.MODI) && !profilo.equals(ProfiloEnum.MODIPA))) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Operazione utilizzabile solamente con Profilo 'ModI'");
		}

		TipoApiEnum protocollo = getTipoApi(asps, env);

		FruizioneModI fruizionemodi = new FruizioneModI();
		if(p!= null) {

			if(protocollo.equals(TipoApiEnum.SOAP)) {
				fruizionemodi.setModi(getFruizioneModISoap(p));
			} else {
				fruizionemodi.setModi(getFruizioneModIRest(p));
			}
		}
		
		return fruizionemodi;
	}
	
	public static ErogazioneModI getErogazioneModi(AccordoServizioParteSpecifica asps, ErogazioniEnv env, ProfiloEnum profilo, Map <String, AbstractProperty<?>> p) throws Exception {
		if(profilo == null || (!profilo.equals(ProfiloEnum.MODI) && !profilo.equals(ProfiloEnum.MODIPA))) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Operazione utilizzabile solamente con Profilo 'ModI'");
		}

		TipoApiEnum protocollo = getTipoApi(asps, env);

		ErogazioneModI erogazionemodi = new ErogazioneModI();
		if(p!= null) {

			if(protocollo.equals(TipoApiEnum.SOAP)) {
				erogazionemodi.setModi(getSOAPProperties(p));
			} else {
				erogazionemodi.setModi(getRestProperties(p));
			}
		}
		
		return erogazionemodi;
	}
	
	private static boolean isSicurezzaMessaggioAPIAbilitata(AccordoServizioParteSpecifica asps, ErogazioniEnv env) throws Exception {
		AccordoServizioParteComune aspc = env.apcCore.getAccordoServizioFull(asps.getIdAccordo());
		if(aspc.getProtocolPropertyList()==null) {
			return false;
		}
		String sicurezzaMessaggioValue = null;
		for(ProtocolProperty p: aspc.getProtocolPropertyList()) {
			if(p.getName().equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO)) {
				sicurezzaMessaggioValue = p.getValue();	
			}
		}
		
		if(sicurezzaMessaggioValue == null) {
			return false;
		}
		
		return !sicurezzaMessaggioValue.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_UNDEFINED);
	}
	
	private static TipoApiEnum getTipoApi(AccordoServizioParteSpecifica asps, ErogazioniEnv env) throws Exception {
		AccordoServizioParteComune aspc = env.apcCore.getAccordoServizioFull(asps.getIdAccordo());
		return aspc.getFormatoSpecifica().equals(FormatoSpecifica.WSDL_11) ? TipoApiEnum.SOAP: TipoApiEnum.REST;

	}
	
	private static ErogazioneModISoap getSOAPProperties(Map<String, AbstractProperty<?>> p) throws Exception {

		ErogazioneModISoap modi = new ErogazioneModISoap();
		
		modi.setProtocollo(TipoApiEnum.SOAP);
		ErogazioneModISoapRichiesta richiesta = new ErogazioneModISoapRichiesta();
		ErogazioneModISoapRichiestaSicurezzaMessaggio sicurezzaMessaggioRichiesta = new ErogazioneModISoapRichiestaSicurezzaMessaggio();
		String truststoreMode = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE, true);
		if(truststoreMode.equals(ModICostanti.MODIPA_PROFILO_DEFAULT)) {
			ModITrustStoreDefault truststore = new ModITrustStoreDefault().modalita(StatoDefaultRidefinitoEnum.DEFAULT);
			sicurezzaMessaggioRichiesta.setTruststore(truststore);
		} else {
			ModITrustStoreRidefinito truststore = new ModITrustStoreRidefinito().modalita(StatoDefaultRidefinitoEnum.RIDEFINITO);
			
			truststore.setTruststoreCrl(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS, true));
			ModITruststoreEnum tipo = null;
			String truststoreTipoString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE, true);
			if(truststoreTipoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_VALUE_JKS)) {
				tipo = ModITruststoreEnum.JKS;
			}
			truststore.setTruststoreTipo(tipo);
			truststore.setTruststorePath(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH, true));
			truststore.setTruststorePassword(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD, true));
			sicurezzaMessaggioRichiesta.setTruststore(truststore);
		}
		
		String wsaTo = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE, false);
		
		if(wsaTo != null) {
			sicurezzaMessaggioRichiesta.setWsaTo(wsaTo);
		}
		
		richiesta.setSicurezzaMessaggio(sicurezzaMessaggioRichiesta);
		modi.setRichiesta(richiesta);
		
		
		
		ErogazioneModISoapRisposta risposta = new ErogazioneModISoapRisposta();
		
		ErogazioneModISoapRispostaSicurezzaMessaggio sicurezzaMessaggio = new ErogazioneModISoapRispostaSicurezzaMessaggio();
		
		
		ModISicurezzaMessaggioSoapAlgoritmoFirma algo = null;
		
		String algoString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_ALG, true);

		if(algoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_DSA_SHA_256)) {
			algo = ModISicurezzaMessaggioSoapAlgoritmoFirma.DSA_SHA_256;
		} else if(algoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_ECDSA_SHA_256)) {
			algo = ModISicurezzaMessaggioSoapAlgoritmoFirma.ECDSA_SHA_256;
		} else if(algoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_ECDSA_SHA_384)) {
			algo = ModISicurezzaMessaggioSoapAlgoritmoFirma.ECDSA_SHA_384;
		} else if(algoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_ECDSA_SHA_512)) {
			algo = ModISicurezzaMessaggioSoapAlgoritmoFirma.ECDSA_SHA_512;
		} else if(algoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_RSA_SHA_256)) {
			algo = ModISicurezzaMessaggioSoapAlgoritmoFirma.RSA_SHA_256;
		} else if(algoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_RSA_SHA_384)) {
			algo = ModISicurezzaMessaggioSoapAlgoritmoFirma.RSA_SHA_384;
		} else if(algoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_RSA_SHA_512)) {
			algo = ModISicurezzaMessaggioSoapAlgoritmoFirma.RSA_SHA_512;
		}
		
		sicurezzaMessaggio.setAlgoritmo(algo);
		
		ModISicurezzaMessaggioSoapFormaCanonicaXml canonic = null;
		String canonicString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_CANONICALIZATION_ALG, true);

		if(canonicString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_INCLUSIVE_C14N_10)) {
			canonic = ModISicurezzaMessaggioSoapFormaCanonicaXml.CANONICAL_XML_10;
		} else if(canonicString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_INCLUSIVE_C14N_11)) {
			canonic = ModISicurezzaMessaggioSoapFormaCanonicaXml.CANONICAL_XML_11;
		} else if(canonicString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_EXCLUSIVE_C14N_10)) {
			canonic = ModISicurezzaMessaggioSoapFormaCanonicaXml.EXCLUSIVE_CANONICAL_XML_10;
		}
		sicurezzaMessaggio.setFormaCanonicaXml(canonic);
		
		
		ModISicurezzaMessaggioSoapRiferimentoX509 rif509 = null;
		String rif509String = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509, true);

		if(rif509String.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_BINARY_SECURITY_TOKEN)) {
			rif509 = ModISicurezzaMessaggioSoapRiferimentoX509.BINARY_SECURITY_TOKEN;
		} else if(rif509String.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_SECURITY_TOKEN_REFERENCE)) {
			rif509 = ModISicurezzaMessaggioSoapRiferimentoX509.ISSUER_SERIAL_SECURITY_TOKEN_REFERENCE;
		} else if(rif509String.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_KEY_IDENTIFIER_SKI)) {
			rif509 = ModISicurezzaMessaggioSoapRiferimentoX509.SKI_KEY_IDENTIFIER;
		} else if(rif509String.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_KEY_IDENTIFIER_THUMBPRINT)) {
			rif509 = ModISicurezzaMessaggioSoapRiferimentoX509.THUMBPRINT_KEY_IDENTIFIER;
		} else if(rif509String.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_KEY_IDENTIFIER_X509)) {
			rif509 = ModISicurezzaMessaggioSoapRiferimentoX509.X509_KEY_IDENTIFIER;
		}
		
		
		sicurezzaMessaggio.setRiferimentoX509(rif509);
		
		sicurezzaMessaggio.setTimeToLive(ProtocolPropertiesHelper.getIntegerProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_EXPIRED, true));
		sicurezzaMessaggio.setCertificateChain(ProtocolPropertiesHelper.getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_USE_CERTIFICATE_CHAIN, true));
		sicurezzaMessaggio.setIncludiSignatureToken(ProtocolPropertiesHelper.getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_INCLUDE_SIGNATURE_TOKEN, true));

		
		String keystoreTypeString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE, true);

		if(keystoreTypeString.equals(ModICostanti.MODIPA_PROFILO_DEFAULT)) {
			
			ModIKeyStoreDefault ks = new ModIKeyStoreDefault().modalita(StatoDefaultRidefinitoEnum.DEFAULT);
			sicurezzaMessaggio.setKeystore(ks);
		} else {
			
			ModIKeyStoreRidefinito ks = new ModIKeyStoreRidefinito().modalita(StatoDefaultRidefinitoEnum.RIDEFINITO);
			
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

				ks.setDatiKeystore(datiKeystore);
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

				ks.setDatiKeystore(datiKeystore);
			}
			
			sicurezzaMessaggio.setKeystore(ks);
		}
		
		risposta.setSicurezzaMessaggio(sicurezzaMessaggio);
		
		modi.setRisposta(risposta);
		
		
		return modi;
	}

	private static ErogazioneModIRest getRestProperties(Map<String, AbstractProperty<?>> p) throws Exception {

		ErogazioneModIRest modi = new ErogazioneModIRest();
		
		modi.setProtocollo(TipoApiEnum.REST);
		ErogazioneModIRestRichiesta richiesta = new ErogazioneModIRestRichiesta();
		ErogazioneModIRestRichiestaSicurezzaMessaggio sicurezzaMessaggioRichiesta = new ErogazioneModIRestRichiestaSicurezzaMessaggio();
		String truststoreMode = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE, true);
		if(truststoreMode.equals(ModICostanti.MODIPA_PROFILO_DEFAULT)) {
			ModITrustStoreDefault truststore = new ModITrustStoreDefault().modalita(StatoDefaultRidefinitoEnum.DEFAULT);
			sicurezzaMessaggioRichiesta.setTruststore(truststore);
		} else {
			ModITrustStoreRidefinito truststore = new ModITrustStoreRidefinito().modalita(StatoDefaultRidefinitoEnum.RIDEFINITO);
			
			truststore.setTruststoreCrl(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS, true));
			ModITruststoreEnum tipo = null;
			String truststoreTipoString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE, true);
			if(truststoreTipoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_VALUE_JKS)) {
				tipo = ModITruststoreEnum.JKS;
			}
			truststore.setTruststoreTipo(tipo);
			truststore.setTruststorePath(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH, true));
			truststore.setTruststorePassword(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD, true));
			sicurezzaMessaggioRichiesta.setTruststore(truststore);
		}
		
		String truststoreSSLMode = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE, false);
		
		if(truststoreSSLMode!=null) {
			if(truststoreSSLMode.equals(ModICostanti.MODIPA_PROFILO_DEFAULT)) {
				ModITrustStoreDefault truststore = new ModITrustStoreDefault().modalita(StatoDefaultRidefinitoEnum.DEFAULT);
				sicurezzaMessaggioRichiesta.setTruststoreSsl(truststore);
			} else {
				ModITrustStoreRidefinito truststore = new ModITrustStoreRidefinito().modalita(StatoDefaultRidefinitoEnum.RIDEFINITO);
				
				truststore.setTruststoreCrl(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS, true));
				ModITruststoreEnum tipo = null;
				String truststoreTipoString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE, true);
				if(truststoreTipoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_VALUE_JKS)) {
					tipo = ModITruststoreEnum.JKS;
				}
				truststore.setTruststoreTipo(tipo);
				truststore.setTruststorePath(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH, true));
				truststore.setTruststorePassword(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PASSWORD, true));
				sicurezzaMessaggioRichiesta.setTruststoreSsl(truststore);
			}
		}
		
		sicurezzaMessaggioRichiesta.setRiferimentoX509(getX509(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_RIFERIMENTO_X509, true)));

		sicurezzaMessaggioRichiesta.setAudience(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE, false));
		
		richiesta.setSicurezzaMessaggio(sicurezzaMessaggioRichiesta);
		modi.setRichiesta(richiesta);
		
		
		
		ErogazioneModIRestRisposta risposta = new ErogazioneModIRestRisposta();
		
		ErogazioneModIRestRispostaSicurezzaMessaggio sicurezzaMessaggio = new ErogazioneModIRestRispostaSicurezzaMessaggio();
		
		
		ModISicurezzaMessaggioRestAlgoritmoFirma algo = null;
		
		String algoString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_ALG, true);

		if(algoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_ES256)) {
			algo = ModISicurezzaMessaggioRestAlgoritmoFirma.ES256;
		} else if(algoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_ES384)) {
			algo = ModISicurezzaMessaggioRestAlgoritmoFirma.ES384;
		} else if(algoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_ES512)) {
			algo = ModISicurezzaMessaggioRestAlgoritmoFirma.ES512;
		} else if(algoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_RS256)) {
			algo = ModISicurezzaMessaggioRestAlgoritmoFirma.RS256;
		} else if(algoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_RS384)) {
			algo = ModISicurezzaMessaggioRestAlgoritmoFirma.RS384;
		} else if(algoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_RS512)) {
			algo = ModISicurezzaMessaggioRestAlgoritmoFirma.RS512;
		}
		
		sicurezzaMessaggio.setAlgoritmo(algo);
		
		if(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_AS_REQUEST, true).equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_AS_REQUEST_VALUE_TRUE)){
			sicurezzaMessaggio.setRiferimentoX509(ModISicurezzaMessaggioRestRiferimentoX509Risposta.RICHIESTA);
		} else {
			sicurezzaMessaggio.setRiferimentoX509(ModISicurezzaMessaggioRestRiferimentoX509Risposta.RIDEFINITO);
			sicurezzaMessaggio.setRiferimentoX509Risposta(getX509(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509, true)));
		}
		
		
		
		sicurezzaMessaggio.setUrl(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_X509_VALUE_X5URL, false));
		sicurezzaMessaggio.setTimeToLive(ProtocolPropertiesHelper.getIntegerProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_EXPIRED, true));
		sicurezzaMessaggio.setCertificateChain(ProtocolPropertiesHelper.getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_X5C_USE_CERTIFICATE_CHAIN, true));
		
		
		String headersString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST, false);
		
		if(headersString!=null) {
			List<String> headers = new ArrayList<>();
			String[] headersplit = headersString.split(",");
			for(String header: headersplit) {
				headers.add(header);
				
			}
			sicurezzaMessaggio.setHeaderHttpFirmare(headers);
		}
		
		String keystoreTypeString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE, true);

		if(keystoreTypeString.equals(ModICostanti.MODIPA_PROFILO_DEFAULT)) {
			
			ModIKeyStoreDefault ks = new ModIKeyStoreDefault().modalita(StatoDefaultRidefinitoEnum.DEFAULT);
			sicurezzaMessaggio.setKeystore(ks);
		} else {
			
			ModIKeyStoreRidefinito ks = new ModIKeyStoreRidefinito().modalita(StatoDefaultRidefinitoEnum.RIDEFINITO);
			
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

				ks.setDatiKeystore(datiKeystore);
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

				ks.setDatiKeystore(datiKeystore);
			}
			
			sicurezzaMessaggio.setKeystore(ks);
		}
		
		risposta.setSicurezzaMessaggio(sicurezzaMessaggio);
		
		modi.setRisposta(risposta);
		
		
		return modi;
	}

	private static FruizioneModISoap getFruizioneModISoap(Map<String, AbstractProperty<?>> p) throws Exception {

		FruizioneModISoap modi = new FruizioneModISoap();
		
		modi.setProtocollo(TipoApiEnum.SOAP);
		FruizioneModISoapRichiesta richiesta = new FruizioneModISoapRichiesta();
		FruizioneModISoapRichiestaSicurezzaMessaggio sicurezzaMessaggioRichiesta = new FruizioneModISoapRichiestaSicurezzaMessaggio();
		FruizioneModISoapRispostaSicurezzaMessaggio sicurezzaMessaggioRisposta = new FruizioneModISoapRispostaSicurezzaMessaggio();

		String truststoreMode = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE, true);
		if(truststoreMode.equals(ModICostanti.MODIPA_PROFILO_DEFAULT)) {
			ModITrustStoreDefault truststore = new ModITrustStoreDefault().modalita(StatoDefaultRidefinitoEnum.DEFAULT);
			sicurezzaMessaggioRisposta.setTruststore(truststore);
		} else {
			ModITrustStoreRidefinito truststore = new ModITrustStoreRidefinito().modalita(StatoDefaultRidefinitoEnum.RIDEFINITO);
			
			truststore.setTruststoreCrl(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS, true));
			ModITruststoreEnum tipo = null;
			String truststoreTipoString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE, true);
			if(truststoreTipoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_VALUE_JKS)) {
				tipo = ModITruststoreEnum.JKS;
			}
			truststore.setTruststoreTipo(tipo);
			truststore.setTruststorePath(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH, true));
			truststore.setTruststorePassword(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD, true));
			sicurezzaMessaggioRisposta.setTruststore(truststore);
		}
		
		String wsaTo = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE, false);
		
		if(wsaTo != null) {
			sicurezzaMessaggioRichiesta.setWsaTo(wsaTo);
		}
		
		richiesta.setSicurezzaMessaggio(sicurezzaMessaggioRichiesta);
		modi.setRichiesta(richiesta);
		
		
		
		FruizioneModISoapRisposta risposta = new FruizioneModISoapRisposta();
		
		
		ModISicurezzaMessaggioSoapAlgoritmoFirma algo = null;
		
		String algoString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_ALG, true);

		if(algoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_DSA_SHA_256)) {
			algo = ModISicurezzaMessaggioSoapAlgoritmoFirma.DSA_SHA_256;
		} else if(algoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_ECDSA_SHA_256)) {
			algo = ModISicurezzaMessaggioSoapAlgoritmoFirma.ECDSA_SHA_256;
		} else if(algoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_ECDSA_SHA_384)) {
			algo = ModISicurezzaMessaggioSoapAlgoritmoFirma.ECDSA_SHA_384;
		} else if(algoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_ECDSA_SHA_512)) {
			algo = ModISicurezzaMessaggioSoapAlgoritmoFirma.ECDSA_SHA_512;
		} else if(algoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_RSA_SHA_256)) {
			algo = ModISicurezzaMessaggioSoapAlgoritmoFirma.RSA_SHA_256;
		} else if(algoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_RSA_SHA_384)) {
			algo = ModISicurezzaMessaggioSoapAlgoritmoFirma.RSA_SHA_384;
		} else if(algoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_RSA_SHA_512)) {
			algo = ModISicurezzaMessaggioSoapAlgoritmoFirma.RSA_SHA_512;
		}
		
		sicurezzaMessaggioRichiesta.setAlgoritmo(algo);
		
		ModISicurezzaMessaggioSoapFormaCanonicaXml canonic = null;
		String canonicString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_CANONICALIZATION_ALG, true);

		if(canonicString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_INCLUSIVE_C14N_10)) {
			canonic = ModISicurezzaMessaggioSoapFormaCanonicaXml.CANONICAL_XML_10;
		} else if(canonicString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_INCLUSIVE_C14N_11)) {
			canonic = ModISicurezzaMessaggioSoapFormaCanonicaXml.CANONICAL_XML_11;
		} else if(canonicString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_EXCLUSIVE_C14N_10)) {
			canonic = ModISicurezzaMessaggioSoapFormaCanonicaXml.EXCLUSIVE_CANONICAL_XML_10;
		}
		sicurezzaMessaggioRichiesta.setFormaCanonicaXml(canonic);
		
		
		ModISicurezzaMessaggioSoapRiferimentoX509 rif509 = null;
		String rif509String = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509, true);

		if(rif509String.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_BINARY_SECURITY_TOKEN)) {
			rif509 = ModISicurezzaMessaggioSoapRiferimentoX509.BINARY_SECURITY_TOKEN;
		} else if(rif509String.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_SECURITY_TOKEN_REFERENCE)) {
			rif509 = ModISicurezzaMessaggioSoapRiferimentoX509.ISSUER_SERIAL_SECURITY_TOKEN_REFERENCE;
		} else if(rif509String.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_KEY_IDENTIFIER_SKI)) {
			rif509 = ModISicurezzaMessaggioSoapRiferimentoX509.SKI_KEY_IDENTIFIER;
		} else if(rif509String.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_KEY_IDENTIFIER_THUMBPRINT)) {
			rif509 = ModISicurezzaMessaggioSoapRiferimentoX509.THUMBPRINT_KEY_IDENTIFIER;
		} else if(rif509String.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_KEY_IDENTIFIER_X509)) {
			rif509 = ModISicurezzaMessaggioSoapRiferimentoX509.X509_KEY_IDENTIFIER;
		}
		
		
		sicurezzaMessaggioRichiesta.setRiferimentoX509(rif509);
		
		sicurezzaMessaggioRichiesta.setTimeToLive(ProtocolPropertiesHelper.getIntegerProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_EXPIRED, true));
		sicurezzaMessaggioRichiesta.setCertificateChain(ProtocolPropertiesHelper.getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_USE_CERTIFICATE_CHAIN, true));
		sicurezzaMessaggioRichiesta.setIncludiSignatureToken(ProtocolPropertiesHelper.getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_INCLUDE_SIGNATURE_TOKEN, true));

//		sicurezzaMessaggioRichiesta.setInformazioniUtenteCodiceEnte(null); //TODO ECC
		
		risposta.setSicurezzaMessaggio(sicurezzaMessaggioRisposta);
		
		modi.setRisposta(risposta);
		
		
		return modi;
	}

	private static FruizioneModIRest getFruizioneModIRest(Map<String, AbstractProperty<?>> p) throws Exception {

		FruizioneModIRest modi = new FruizioneModIRest();
		
		modi.setProtocollo(TipoApiEnum.REST);
		FruizioneModIRestRichiesta richiesta = new FruizioneModIRestRichiesta();
		FruizioneModIRestRichiestaSicurezzaMessaggio sicurezzaMessaggioRichiesta = new FruizioneModIRestRichiestaSicurezzaMessaggio();
		FruizioneModIRestRispostaSicurezzaMessaggio sicurezzaMessaggioRisposta = new FruizioneModIRestRispostaSicurezzaMessaggio();

		String truststoreMode = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE, true);
		if(truststoreMode.equals(ModICostanti.MODIPA_PROFILO_DEFAULT)) {
			ModITrustStoreDefault truststore = new ModITrustStoreDefault().modalita(StatoDefaultRidefinitoEnum.DEFAULT);
			sicurezzaMessaggioRisposta.setTruststore(truststore);
		} else {
			ModITrustStoreRidefinito truststore = new ModITrustStoreRidefinito().modalita(StatoDefaultRidefinitoEnum.RIDEFINITO);
			
			truststore.setTruststoreCrl(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS, true));
			ModITruststoreEnum tipo = null;
			String truststoreTipoString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE, true);
			if(truststoreTipoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_VALUE_JKS)) {
				tipo = ModITruststoreEnum.JKS;
			}
			truststore.setTruststoreTipo(tipo);
			truststore.setTruststorePath(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH, true));
			truststore.setTruststorePassword(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD, true));
			sicurezzaMessaggioRisposta.setTruststore(truststore);
		}
		
		String truststoreSSLMode = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE, false);
		
		if(truststoreSSLMode!=null) {
			if(truststoreSSLMode.equals(ModICostanti.MODIPA_PROFILO_DEFAULT)) {
				ModITrustStoreDefault truststore = new ModITrustStoreDefault().modalita(StatoDefaultRidefinitoEnum.DEFAULT);
				sicurezzaMessaggioRisposta.setTruststoreSsl(truststore);
			} else {
				ModITrustStoreRidefinito truststore = new ModITrustStoreRidefinito().modalita(StatoDefaultRidefinitoEnum.RIDEFINITO);
				
				truststore.setTruststoreCrl(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS, true));
				ModITruststoreEnum tipo = null;
				String truststoreTipoString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE, true);
				if(truststoreTipoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_VALUE_JKS)) {
					tipo = ModITruststoreEnum.JKS;
				}
				truststore.setTruststoreTipo(tipo);
				truststore.setTruststorePath(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH, true));
				truststore.setTruststorePassword(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PASSWORD, true));
				sicurezzaMessaggioRisposta.setTruststoreSsl(truststore);
			}
		}
		
		sicurezzaMessaggioRichiesta.setRiferimentoX509(getX509(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_RIFERIMENTO_X509, true)));

		sicurezzaMessaggioRichiesta.setAudience(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE, false));
		
		richiesta.setSicurezzaMessaggio(sicurezzaMessaggioRichiesta);
		modi.setRichiesta(richiesta);
		
		
		
		FruizioneModIRestRisposta risposta = new FruizioneModIRestRisposta();
		
		ModISicurezzaMessaggioRestAlgoritmoFirma algo = null;
		
		String algoString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_ALG, true);

		if(algoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_ES256)) {
			algo = ModISicurezzaMessaggioRestAlgoritmoFirma.ES256;
		} else if(algoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_ES384)) {
			algo = ModISicurezzaMessaggioRestAlgoritmoFirma.ES384;
		} else if(algoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_ES512)) {
			algo = ModISicurezzaMessaggioRestAlgoritmoFirma.ES512;
		} else if(algoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_RS256)) {
			algo = ModISicurezzaMessaggioRestAlgoritmoFirma.RS256;
		} else if(algoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_RS384)) {
			algo = ModISicurezzaMessaggioRestAlgoritmoFirma.RS384;
		} else if(algoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_RS512)) {
			algo = ModISicurezzaMessaggioRestAlgoritmoFirma.RS512;
		}
		
		sicurezzaMessaggioRichiesta.setAlgoritmo(algo);
		
		if(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_AS_REQUEST, true).equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_AS_REQUEST_VALUE_TRUE)){
			sicurezzaMessaggioRisposta.setRiferimentoX509(ModISicurezzaMessaggioRestRiferimentoX509Risposta.RICHIESTA);
		} else {
			sicurezzaMessaggioRisposta.setRiferimentoX509(ModISicurezzaMessaggioRestRiferimentoX509Risposta.RIDEFINITO);
			sicurezzaMessaggioRisposta.setRiferimentoX509Risposta(getX509(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509, true)));
		}
		
		
		
		sicurezzaMessaggioRichiesta.setTimeToLive(ProtocolPropertiesHelper.getIntegerProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_EXPIRED, true));
		sicurezzaMessaggioRichiesta.setCertificateChain(ProtocolPropertiesHelper.getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_X5C_USE_CERTIFICATE_CHAIN, true));
		
//		sicurezzaMessaggioRichiesta.setInformazioniUtenteCodiceEnte(null);//TODO ECC
		
		
		String headersString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST, false);
		
		if(headersString!=null) {
			List<String> headers = new ArrayList<>();
			String[] headersplit = headersString.split(",");
			for(String header: headersplit) {
				headers.add(header);
				
			}
			sicurezzaMessaggioRichiesta.setHeaderHttpFirmare(headers);
		}
		
		risposta.setSicurezzaMessaggio(sicurezzaMessaggioRisposta);
		
		modi.setRisposta(risposta);
		
		
		return modi;
	}

	private static List<ModISicurezzaMessaggioRestRiferimentoX509> getX509(String x509String) {
		String[] split = x509String.split(",");
		
		List<ModISicurezzaMessaggioRestRiferimentoX509> lst = new ArrayList<>();
		for(String x: split) {
			lst.add(ModISicurezzaMessaggioRestRiferimentoX509.fromValue(x));
		}
		
		return lst;
		
	}

	public static ProtocolProperties getProtocolProperties(Erogazione body, AccordoServizioParteSpecifica asps, ErogazioniEnv env) throws Exception {
		return getModiProtocolProperties(body.getModi(), getTipoApi(asps, env), isSicurezzaMessaggioAPIAbilitata(asps, env));
	}

	public static ProtocolProperties getProtocolProperties(Fruizione body, AccordoServizioParteSpecifica asps, ErogazioniEnv env) throws Exception {
		return getModiProtocolProperties(body.getModi(), getTipoApi(asps, env), isSicurezzaMessaggioAPIAbilitata(asps, env));
	}

	public static ProtocolProperties updateModiProtocolProperties(AccordoServizioParteSpecifica asps, ProfiloEnum profilo, OneOfErogazioneModIModi modi, ErogazioniEnv env) throws Exception {
		if(profilo == null || (!profilo.equals(ProfiloEnum.MODI) && !profilo.equals(ProfiloEnum.MODIPA))) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Operazione utilizzabile solamente con Profilo 'ModI'");
		}

		TipoApiEnum protocollo = getTipoApi(asps, env);
		boolean sicurezzaMessaggioAPIAbilitata = isSicurezzaMessaggioAPIAbilitata(asps, env);
		return getModiProtocolProperties(modi, protocollo, sicurezzaMessaggioAPIAbilitata);

	}

	public static ProtocolProperties updateModiProtocolProperties(AccordoServizioParteSpecifica asps, ProfiloEnum profilo, OneOfFruizioneModIModi modi, ErogazioniEnv env) throws Exception {
		if(profilo == null || (!profilo.equals(ProfiloEnum.MODI) && !profilo.equals(ProfiloEnum.MODIPA))) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Operazione utilizzabile solamente con Profilo 'ModI'");
		}

		TipoApiEnum protocollo = getTipoApi(asps, env);
		boolean sicurezzaMessaggioAPIAbilitata = isSicurezzaMessaggioAPIAbilitata(asps, env);
		return getModiProtocolProperties(modi, protocollo, sicurezzaMessaggioAPIAbilitata);

	}

	private static ProtocolProperties getModiProtocolProperties(OneOfErogazioneModIModi modi, TipoApiEnum protocollo, boolean sicurezzaMessaggioAPIAbilitata) {

		if(modi == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare la configurazione 'ModI'");
		}

		ProtocolProperties p = new ProtocolProperties();

		if(protocollo.equals(TipoApiEnum.SOAP)) {
			getSOAPProperties((ErogazioneModISoap)modi, p, sicurezzaMessaggioAPIAbilitata);
		} else if(protocollo.equals(TipoApiEnum.REST)) {
			getRESTProperties((ErogazioneModIRest)modi, p, sicurezzaMessaggioAPIAbilitata);
		}


		return p;
	}

	private static ProtocolProperties getModiProtocolProperties(OneOfFruizioneModIModi modi, TipoApiEnum protocollo, boolean sicurezzaMessaggioAPIAbilitata) {

		if(modi == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare la configurazione 'ModI'");
		}

		ProtocolProperties p = new ProtocolProperties();

		if(protocollo.equals(TipoApiEnum.SOAP)) {
			getSOAPProperties((FruizioneModISoap)modi, p, sicurezzaMessaggioAPIAbilitata);
		} else if(protocollo.equals(TipoApiEnum.REST)) {
			getRESTProperties((FruizioneModIRest)modi, p, sicurezzaMessaggioAPIAbilitata);
		}


		return p;
	}

	private static ProtocolProperties getModiProtocolProperties(OneOfFruizioneModi modi, TipoApiEnum protocollo, boolean sicurezzaMessaggioAPIAbilitata) {

		if(modi == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare la configurazione 'ModI'");
		}

		ProtocolProperties p = new ProtocolProperties();

		if(protocollo.equals(TipoApiEnum.SOAP)) {
			getSOAPProperties((FruizioneModISoap)modi, p, sicurezzaMessaggioAPIAbilitata);
		} else if(protocollo.equals(TipoApiEnum.REST)) {
			getRESTProperties((FruizioneModIRest)modi, p, sicurezzaMessaggioAPIAbilitata);
		}


		return p;
	}

	private static ProtocolProperties getModiProtocolProperties(OneOfErogazioneModi modi, TipoApiEnum protocollo, boolean sicurezzaMessaggioAPIAbilitata) {

		if(modi == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare la configurazione 'ModI'");
		}

		ProtocolProperties p = new ProtocolProperties();

		if(protocollo.equals(TipoApiEnum.SOAP)) {
			getSOAPProperties((ErogazioneModISoap)modi, p, sicurezzaMessaggioAPIAbilitata);
		} else if(protocollo.equals(TipoApiEnum.REST)) {
			getRESTProperties((ErogazioneModIRest)modi, p, sicurezzaMessaggioAPIAbilitata);
		}


		return p;
	}

	private static void getRESTProperties(ErogazioneModIRest modi, ProtocolProperties p, boolean sicurezzaMessaggioAPIAbilitata) {

		if(!sicurezzaMessaggioAPIAbilitata) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Impossibile abilitare la sicurezza messaggio, deve essere abilitata nella API implementata");
		}

		if(modi.getRichiesta().getSicurezzaMessaggio().getTruststore().getModalita().equals(StatoDefaultRidefinitoEnum.DEFAULT)) {
			
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE, ModICostanti.MODIPA_PROFILO_DEFAULT);

			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS, "");
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD, "");
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH, "");
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE, "");
			
		} else {
			
			ModITrustStoreRidefinito truststoreRidefinito = (ModITrustStoreRidefinito)modi.getRichiesta().getSicurezzaMessaggio().getTruststore();
			
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE, ModICostanti.MODIPA_PROFILO_RIDEFINISCI);

			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS, truststoreRidefinito.getTruststoreCrl());
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD, truststoreRidefinito.getTruststorePassword());
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH, truststoreRidefinito.getTruststorePath());
			
			String tipo = null;
			switch(truststoreRidefinito.getTruststoreTipo()) {
			case JKS:tipo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_VALUE_JKS;
				break;
			}
			
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE, tipo);

		}

		if(modi.getRichiesta().getSicurezzaMessaggio().getTruststoreSsl()!=null) {
			if(modi.getRichiesta().getSicurezzaMessaggio().getTruststoreSsl().getModalita().equals(StatoDefaultRidefinitoEnum.DEFAULT)) {
				
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE, ModICostanti.MODIPA_PROFILO_DEFAULT);
	
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS, "");
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PASSWORD, "");
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH, "");
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE, "");
				
			} else {
				
				ModITrustStoreRidefinito truststoreRidefinito = (ModITrustStoreRidefinito)modi.getRichiesta().getSicurezzaMessaggio().getTruststoreSsl();
				
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE, ModICostanti.MODIPA_PROFILO_RIDEFINISCI);
	
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS, truststoreRidefinito.getTruststoreCrl());
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PASSWORD, truststoreRidefinito.getTruststorePassword());
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH, truststoreRidefinito.getTruststorePath());
				
				String tipo = null;
				switch(truststoreRidefinito.getTruststoreTipo()) {
				case JKS:tipo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_VALUE_JKS;
					break;
				}
				
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE, tipo);
	
			}
		}

		p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_RIFERIMENTO_X509, getX509(modi.getRichiesta().getSicurezzaMessaggio().getRiferimentoX509())); 

		if(modi.getRichiesta().getSicurezzaMessaggio().getAudience()!=null) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE, modi.getRichiesta().getSicurezzaMessaggio().getAudience());
		} else {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE, "");
		}


		if(modi.getRisposta().getSicurezzaMessaggio().getKeystore().getModalita().equals(StatoDefaultRidefinitoEnum.DEFAULT)) {
			
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE, ModICostanti.MODIPA_PROFILO_DEFAULT);
			p.addProperty(ModICostanti.MODIPA_KEYSTORE_MODE, ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE);
			p.addProperty(ModICostanti.MODIPA_KEYSTORE_TYPE, ModICostanti.MODIPA_KEYSTORE_TYPE_VALUE_JKS);
			p.addProperty(ModICostanti.MODIPA_KEY_ALIAS, "");
			p.addProperty(ModICostanti.MODIPA_KEY_PASSWORD, "");
			p.addProperty(ModICostanti.MODIPA_KEYSTORE_PASSWORD, "");
			p.addProperty(ModICostanti.MODIPA_KEYSTORE_PATH, "");
			
		} else {

			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE, ModICostanti.MODIPA_PROFILO_RIDEFINISCI);

			ModIKeyStoreRidefinito keystoreRidefinito = (ModIKeyStoreRidefinito)modi.getRisposta().getSicurezzaMessaggio().getKeystore();
			
			if(keystoreRidefinito.getDatiKeystore().getTipologia().equals(ModIKeystoreTipologiaEnum.FILESYSTEM)) {
				ModIKeyStoreFile fsKeystore = (ModIKeyStoreFile)keystoreRidefinito.getDatiKeystore();
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
				ModIKeyStoreArchive archiveKeystore = (ModIKeyStoreArchive)keystoreRidefinito.getDatiKeystore();
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
			
		}

		if(modi.getRisposta().getSicurezzaMessaggio().getHeaderHttpFirmare()!=null) {
			String httpHeaders = String.join(",", modi.getRisposta().getSicurezzaMessaggio().getHeaderHttpFirmare());
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST, httpHeaders); //TODO verificare condizioni
		}
		
		if(modi.getRisposta().getSicurezzaMessaggio().getRiferimentoX509() == null || modi.getRisposta().getSicurezzaMessaggio().getRiferimentoX509().equals(ModISicurezzaMessaggioRestRiferimentoX509Risposta.RICHIESTA)) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_AS_REQUEST, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_AS_REQUEST_VALUE_TRUE);
		} else {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_AS_REQUEST, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_AS_REQUEST_VALUE_FALSE);
			
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509, getX509(modi.getRisposta().getSicurezzaMessaggio().getRiferimentoX509Risposta())); 
		}

		if(modi.getRisposta().getSicurezzaMessaggio().getUrl()!=null) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_X509_VALUE_X5URL, modi.getRisposta().getSicurezzaMessaggio().getUrl()); //TODO verificare condizioni
		}

		if(modi.getRisposta().getSicurezzaMessaggio().getTimeToLive()!=null) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_EXPIRED, modi.getRisposta().getSicurezzaMessaggio().getTimeToLive());
		}

		String algo = null;
		if(modi.getRisposta().getSicurezzaMessaggio().getAlgoritmo()!= null) {
			switch(modi.getRisposta().getSicurezzaMessaggio().getAlgoritmo()) {
			case ES256: algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_ES256;
				break;
			case ES384: algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_ES384;
				break;
			case ES512: algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_ES512;
				break;
			case RS256: algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_RS256;
				break;
			case RS384: algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_RS384;
				break;
			case RS512: algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_RS512;
				break;
			default:
				break;
			}
		} else {
			algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_RS256;
		}
		p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_ALG, algo);

		p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_X5C_USE_CERTIFICATE_CHAIN, modi.getRisposta().getSicurezzaMessaggio().isCertificateChain() != null ?modi.getRisposta().getSicurezzaMessaggio().isCertificateChain() : false));
		
	}

	private static void getRESTProperties(FruizioneModIRest modi, ProtocolProperties p, boolean sicurezzaMessaggioAPIAbilitata) {

		if(!sicurezzaMessaggioAPIAbilitata) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Impossibile abilitare la sicurezza messaggio, deve essere abilitata nella API implementata");
		}

		if(modi.getRisposta().getSicurezzaMessaggio().getTruststore().getModalita().equals(StatoDefaultRidefinitoEnum.DEFAULT)) {
			
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE, ModICostanti.MODIPA_PROFILO_DEFAULT);

			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS, "");
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD, "");
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH, "");
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE, "");
			
		} else {
			
			ModITrustStoreRidefinito truststoreRidefinito = (ModITrustStoreRidefinito)modi.getRisposta().getSicurezzaMessaggio().getTruststore();
			
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE, ModICostanti.MODIPA_PROFILO_RIDEFINISCI);

			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS, truststoreRidefinito.getTruststoreCrl());
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD, truststoreRidefinito.getTruststorePassword());
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH, truststoreRidefinito.getTruststorePath());
			
			String tipo = null;
			switch(truststoreRidefinito.getTruststoreTipo()) {
			case JKS:tipo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_VALUE_JKS;
				break;
			}
			
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE, tipo);

		}

		if(modi.getRisposta().getSicurezzaMessaggio().getTruststoreSsl()!=null) {
			if(modi.getRisposta().getSicurezzaMessaggio().getTruststoreSsl().getModalita().equals(StatoDefaultRidefinitoEnum.DEFAULT)) {
				
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE, ModICostanti.MODIPA_PROFILO_DEFAULT);
	
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS, "");
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PASSWORD, "");
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH, "");
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE, "");
				
			} else {
				
				ModITrustStoreRidefinito truststoreRidefinito = (ModITrustStoreRidefinito)modi.getRisposta().getSicurezzaMessaggio().getTruststoreSsl();
				
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE, ModICostanti.MODIPA_PROFILO_RIDEFINISCI);
	
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS, truststoreRidefinito.getTruststoreCrl());
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PASSWORD, truststoreRidefinito.getTruststorePassword());
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH, truststoreRidefinito.getTruststorePath());
				
				String tipo = null;
				switch(truststoreRidefinito.getTruststoreTipo()) {
				case JKS:tipo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_VALUE_JKS;
					break;
				}
				
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE, tipo);
	
			}
		}

		p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_RIFERIMENTO_X509, getX509(modi.getRichiesta().getSicurezzaMessaggio().getRiferimentoX509())); 

		if(modi.getRichiesta().getSicurezzaMessaggio().getAudience()!=null) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE, modi.getRichiesta().getSicurezzaMessaggio().getAudience());
		} else {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE, "");
		}


//		if(modi.getRisposta().getSicurezzaMessaggio().getKeystore().getModalita().equals(StatoDefaultRidefinitoEnum.DEFAULT)) {
//			
//			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE, ModICostanti.MODIPA_PROFILO_DEFAULT);
//			p.addProperty(ModICostanti.MODIPA_KEYSTORE_MODE, ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE);
//			p.addProperty(ModICostanti.MODIPA_KEYSTORE_TYPE, ModICostanti.MODIPA_KEYSTORE_TYPE_VALUE_JKS);
//			p.addProperty(ModICostanti.MODIPA_KEY_ALIAS, "");
//			p.addProperty(ModICostanti.MODIPA_KEY_PASSWORD, "");
//			p.addProperty(ModICostanti.MODIPA_KEYSTORE_PASSWORD, "");
//			p.addProperty(ModICostanti.MODIPA_KEYSTORE_PATH, "");
//			
//		} else {
//
//			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE, ModICostanti.MODIPA_PROFILO_RIDEFINISCI);
//
//			ModIKeyStoreRidefinito keystoreRidefinito = (ModIKeyStoreRidefinito)modi.getRisposta().getSicurezzaMessaggio().getKeystore();
//			
//			if(keystoreRidefinito.getDatiKeystore().getTipologia().equals(ModIKeystoreTipologiaEnum.FILESYSTEM)) {
//				ModIKeyStoreFile fsKeystore = (ModIKeyStoreFile)keystoreRidefinito.getDatiKeystore();
//				p.addProperty(ModICostanti.MODIPA_KEYSTORE_MODE, ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_PATH);
//
//				
//				String tipo = null;
//				switch(fsKeystore.getKeystoreTipo()) {
//				case JKS:tipo = ModICostanti.MODIPA_KEYSTORE_TYPE_VALUE_JKS;
//					break;
//				case PKCS12:tipo = ModICostanti.MODIPA_KEYSTORE_TYPE_VALUE_PKCS12;
//					break;
//				default:
//					break;
//				}
//				
//				p.addProperty(ModICostanti.MODIPA_KEYSTORE_TYPE, tipo);
//				p.addProperty(ModICostanti.MODIPA_KEY_ALIAS, fsKeystore.getKeyAlias());
//				p.addProperty(ModICostanti.MODIPA_KEY_PASSWORD, fsKeystore.getKeyPassword());
//				p.addProperty(ModICostanti.MODIPA_KEYSTORE_PASSWORD, fsKeystore.getKeystorePassword());
//				p.addProperty(ModICostanti.MODIPA_KEYSTORE_PATH, fsKeystore.getKeystorePath());
//			} else {
//				ModIKeyStoreArchive archiveKeystore = (ModIKeyStoreArchive)keystoreRidefinito.getDatiKeystore();
//				p.addProperty(ModICostanti.MODIPA_KEYSTORE_MODE, ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE);
//
//				
//				String tipo = null;
//				String filename = null;
//				
//				switch(archiveKeystore.getKeystoreTipo()) {
//				case JKS:tipo = ModICostanti.MODIPA_KEYSTORE_TYPE_VALUE_JKS; filename = "Keystore.jks";
//					break;
//				case PKCS12:tipo = ModICostanti.MODIPA_KEYSTORE_TYPE_VALUE_PKCS12; filename = "Keystore.p12";
//					break;
//				default:
//					break;
//				}
//				
//				p.addProperty(ModICostanti.MODIPA_KEYSTORE_TYPE, tipo);
//				p.addProperty(ModICostanti.MODIPA_KEY_ALIAS, archiveKeystore.getKeyAlias());
//				p.addProperty(ModICostanti.MODIPA_KEY_PASSWORD, archiveKeystore.getKeyPassword());
//				p.addProperty(ModICostanti.MODIPA_KEYSTORE_PASSWORD, archiveKeystore.getKeystorePassword());
//				p.addProperty(ModICostanti.MODIPA_KEYSTORE_ARCHIVE, archiveKeystore.getKeystoreArchivio(), filename, filename);
//			}
//			
//		}

		if(modi.getRichiesta().getSicurezzaMessaggio().getHeaderHttpFirmare()!=null) {
			String httpHeaders = String.join(",", modi.getRichiesta().getSicurezzaMessaggio().getHeaderHttpFirmare());
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST, httpHeaders); //TODO verificare condizioni
		}
		
		if(modi.getRisposta().getSicurezzaMessaggio().getRiferimentoX509() == null || modi.getRisposta().getSicurezzaMessaggio().getRiferimentoX509().equals(ModISicurezzaMessaggioRestRiferimentoX509Risposta.RICHIESTA)) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_AS_REQUEST, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_AS_REQUEST_VALUE_TRUE);
		} else {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_AS_REQUEST, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_AS_REQUEST_VALUE_FALSE);
			
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509, getX509(modi.getRisposta().getSicurezzaMessaggio().getRiferimentoX509Risposta())); 
		}

//		if(modi.getRichiesta().getSicurezzaMessaggio().getUrl()!=null) {
//			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_X509_VALUE_X5URL, modi.getRichiesta().getSicurezzaMessaggio().getUrl()); //TODO verificare condizioni
//		}

		if(modi.getRichiesta().getSicurezzaMessaggio().getTimeToLive()!=null) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_EXPIRED, modi.getRichiesta().getSicurezzaMessaggio().getTimeToLive());
		}

		String algo = null;
		if(modi.getRichiesta().getSicurezzaMessaggio().getAlgoritmo()!= null) {
			switch(modi.getRichiesta().getSicurezzaMessaggio().getAlgoritmo()) {
			case ES256: algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_ES256;
				break;
			case ES384: algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_ES384;
				break;
			case ES512: algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_ES512;
				break;
			case RS256: algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_RS256;
				break;
			case RS384: algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_RS384;
				break;
			case RS512: algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_RS512;
				break;
			default:
				break;
			}
		} else {
			algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_RS256;
		}
		p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_ALG, algo);

		p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_X5C_USE_CERTIFICATE_CHAIN, modi.getRichiesta().getSicurezzaMessaggio().isCertificateChain() != null ?modi.getRichiesta().getSicurezzaMessaggio().isCertificateChain() : false));
		
	}

	private static String getX509(List<ModISicurezzaMessaggioRestRiferimentoX509> rif) {
		List<String> sX509 = new ArrayList<>();
		
		if(rif.contains(ModISicurezzaMessaggioRestRiferimentoX509.X5C)) {
			sX509.add(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_VALUE_X5C);
		}
		if(rif.contains(ModISicurezzaMessaggioRestRiferimentoX509.X5T_256)) {
			sX509.add(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_VALUE_X5T);
		}
		if(rif.contains(ModISicurezzaMessaggioRestRiferimentoX509.X5U)) {
			sX509.add(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_VALUE_X5U);
		}
		
		String value = String.join(",", sX509);
		return value;
	}

	private static void getSOAPProperties(ErogazioneModISoap modi, ProtocolProperties p, boolean sicurezzaMessaggioAPIAbilitata) {

		if(!sicurezzaMessaggioAPIAbilitata) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Impossibile abilitare la sicurezza messaggio, deve essere abilitata nella API implementata");
		}
		if(modi.getRichiesta().getSicurezzaMessaggio().getTruststore().getModalita().equals(StatoDefaultRidefinitoEnum.DEFAULT)) {
			
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE, ModICostanti.MODIPA_PROFILO_DEFAULT);

			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS, "");
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD, "");
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH, "");
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE, "");
			
		} else {
			
			ModITrustStoreRidefinito truststoreRidefinito = (ModITrustStoreRidefinito)modi.getRichiesta().getSicurezzaMessaggio().getTruststore();
			
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE, ModICostanti.MODIPA_PROFILO_RIDEFINISCI);

			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS, truststoreRidefinito.getTruststoreCrl());
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD, truststoreRidefinito.getTruststorePassword());
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH, truststoreRidefinito.getTruststorePath());
			
			String tipo = null;
			switch(truststoreRidefinito.getTruststoreTipo()) {
			case JKS:tipo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_VALUE_JKS;
				break;
			}
			
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE, tipo);

		}

		if(modi.getRichiesta().getSicurezzaMessaggio().getWsaTo()!=null) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE, modi.getRichiesta().getSicurezzaMessaggio().getWsaTo());
		} else {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE, "");
		}


		if(modi.getRisposta().getSicurezzaMessaggio().getKeystore().getModalita().equals(StatoDefaultRidefinitoEnum.DEFAULT)) {
			
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE, ModICostanti.MODIPA_PROFILO_DEFAULT);
			p.addProperty(ModICostanti.MODIPA_KEYSTORE_MODE, ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE);
			p.addProperty(ModICostanti.MODIPA_KEYSTORE_TYPE, ModICostanti.MODIPA_KEYSTORE_TYPE_VALUE_JKS);
			p.addProperty(ModICostanti.MODIPA_KEY_ALIAS, "");
			p.addProperty(ModICostanti.MODIPA_KEY_PASSWORD, "");
			p.addProperty(ModICostanti.MODIPA_KEYSTORE_PASSWORD, "");
			p.addProperty(ModICostanti.MODIPA_KEYSTORE_PATH, "");
			
		} else {

			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE, ModICostanti.MODIPA_PROFILO_RIDEFINISCI);

			ModIKeyStoreRidefinito keystoreRidefinito = (ModIKeyStoreRidefinito)modi.getRisposta().getSicurezzaMessaggio().getKeystore();
			
			if(keystoreRidefinito.getDatiKeystore().getTipologia().equals(ModIKeystoreTipologiaEnum.FILESYSTEM)) {
				ModIKeyStoreFile fsKeystore = (ModIKeyStoreFile)keystoreRidefinito.getDatiKeystore();
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
				ModIKeyStoreArchive archiveKeystore = (ModIKeyStoreArchive)keystoreRidefinito.getDatiKeystore();
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
			
		}

		if(modi.getRisposta().getSicurezzaMessaggio().getTimeToLive()!=null) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_EXPIRED, modi.getRisposta().getSicurezzaMessaggio().getTimeToLive());
		} else {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_EXPIRED, 300);
		}

		String algo = null;
		if(modi.getRisposta().getSicurezzaMessaggio().getAlgoritmo()!= null) {
			switch(modi.getRisposta().getSicurezzaMessaggio().getAlgoritmo()) {
			case DSA_SHA_256:algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_DSA_SHA_256;
				break;
			case ECDSA_SHA_256: algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_ECDSA_SHA_256;
				break;
			case ECDSA_SHA_384:algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_ECDSA_SHA_384;
				break;
			case ECDSA_SHA_512:algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_ECDSA_SHA_512;
				break;
			case RSA_SHA_256:algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_RSA_SHA_256;
				break;
			case RSA_SHA_384:algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_RSA_SHA_384;
				break;
			case RSA_SHA_512:algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_RSA_SHA_512;
				break;
			default:
				break;}
		} else {
			algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_ECDSA_SHA_256;
		}
		p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_ALG, algo);

		String canonic = null;
		
		if(modi.getRisposta().getSicurezzaMessaggio().getFormaCanonicaXml()!=null) {
			switch(modi.getRisposta().getSicurezzaMessaggio().getFormaCanonicaXml()) {
			case CANONICAL_XML_10:canonic = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_INCLUSIVE_C14N_10;
				break;
			case CANONICAL_XML_11:canonic = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_INCLUSIVE_C14N_11;
				break;
			case EXCLUSIVE_CANONICAL_XML_10: canonic = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_EXCLUSIVE_C14N_10;
				break;
			default:
				break;}
		} else {
			canonic = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_EXCLUSIVE_C14N_10;
		}
		p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_CANONICALIZATION_ALG, canonic);
		
		String rif509 = null;
		
		if(modi.getRisposta().getSicurezzaMessaggio().getRiferimentoX509()!= null) {
			switch(modi.getRisposta().getSicurezzaMessaggio().getRiferimentoX509()) {
			case BINARY_SECURITY_TOKEN: rif509 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_BINARY_SECURITY_TOKEN;
				break;
			case ISSUER_SERIAL_SECURITY_TOKEN_REFERENCE: rif509 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_SECURITY_TOKEN_REFERENCE;
				break;
			case SKI_KEY_IDENTIFIER: rif509 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_KEY_IDENTIFIER_SKI;
				break;
			case THUMBPRINT_KEY_IDENTIFIER: rif509 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_KEY_IDENTIFIER_THUMBPRINT;
				break;
			case X509_KEY_IDENTIFIER: rif509 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_KEY_IDENTIFIER_X509;
				break;
			default:
			}
		} else {
			rif509 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_BINARY_SECURITY_TOKEN;
		}

		p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509, rif509);
		
		p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_INCLUDE_SIGNATURE_TOKEN, modi.getRisposta().getSicurezzaMessaggio().isIncludiSignatureToken() != null ? modi.getRisposta().getSicurezzaMessaggio().isIncludiSignatureToken() : false ));
		p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_USE_CERTIFICATE_CHAIN, modi.getRisposta().getSicurezzaMessaggio().isCertificateChain() != null ?modi.getRisposta().getSicurezzaMessaggio().isCertificateChain() : false));

		
	}

	private static void getSOAPProperties(FruizioneModISoap modi, ProtocolProperties p, boolean sicurezzaMessaggioAPIAbilitata) {

		if(!sicurezzaMessaggioAPIAbilitata) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Impossibile abilitare la sicurezza messaggio, deve essere abilitata nella API implementata");
		}
//		if(modi.getRichiesta().getSicurezzaMessaggio().getTruststore().getModalita().equals(StatoDefaultRidefinitoEnum.DEFAULT)) {
//			
//			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE, ModICostanti.MODIPA_PROFILO_DEFAULT);
//
//			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS, "");
//			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD, "");
//			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH, "");
//			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE, "");
//			
//		} else {
//			
//			ModITrustStoreRidefinito truststoreRidefinito = (ModITrustStoreRidefinito)modi.getRichiesta().getSicurezzaMessaggio().getTruststore();
//			
//			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE, ModICostanti.MODIPA_PROFILO_RIDEFINISCI);
//
//			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS, truststoreRidefinito.getTruststoreCrl());
//			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD, truststoreRidefinito.getTruststorePassword());
//			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH, truststoreRidefinito.getTruststorePath());
//			
//			String tipo = null;
//			switch(truststoreRidefinito.getTruststoreTipo()) {
//			case JKS:tipo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_VALUE_JKS;
//				break;
//			}
//			
//			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE, tipo);
//
//		}

		if(modi.getRichiesta().getSicurezzaMessaggio().getWsaTo()!=null) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE, modi.getRichiesta().getSicurezzaMessaggio().getWsaTo());
		} else {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE, "");
		}


		if(modi.getRisposta().getSicurezzaMessaggio().getTruststore().getModalita().equals(StatoDefaultRidefinitoEnum.DEFAULT)) {
			
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE, ModICostanti.MODIPA_PROFILO_DEFAULT);
			p.addProperty(ModICostanti.MODIPA_KEYSTORE_MODE, ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE);
			p.addProperty(ModICostanti.MODIPA_KEYSTORE_TYPE, ModICostanti.MODIPA_KEYSTORE_TYPE_VALUE_JKS);
			p.addProperty(ModICostanti.MODIPA_KEY_ALIAS, "");
			p.addProperty(ModICostanti.MODIPA_KEY_PASSWORD, "");
			p.addProperty(ModICostanti.MODIPA_KEYSTORE_PASSWORD, "");
			p.addProperty(ModICostanti.MODIPA_KEYSTORE_PATH, "");
			
		} else {

			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE, ModICostanti.MODIPA_PROFILO_RIDEFINISCI);

			ModIKeyStoreRidefinito keystoreRidefinito = (ModIKeyStoreRidefinito)modi.getRisposta().getSicurezzaMessaggio().getTruststore();
			
			if(keystoreRidefinito.getDatiKeystore().getTipologia().equals(ModIKeystoreTipologiaEnum.FILESYSTEM)) {
				ModIKeyStoreFile fsKeystore = (ModIKeyStoreFile)keystoreRidefinito.getDatiKeystore();
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
				ModIKeyStoreArchive archiveKeystore = (ModIKeyStoreArchive)keystoreRidefinito.getDatiKeystore();
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
			
		}

		if(modi.getRichiesta().getSicurezzaMessaggio().getTimeToLive()!=null) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_EXPIRED, modi.getRichiesta().getSicurezzaMessaggio().getTimeToLive());
		} else {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_EXPIRED, 300);
		}

		String algo = null;
		if(modi.getRichiesta().getSicurezzaMessaggio().getAlgoritmo()!= null) {
			switch(modi.getRichiesta().getSicurezzaMessaggio().getAlgoritmo()) {
			case DSA_SHA_256:algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_DSA_SHA_256;
				break;
			case ECDSA_SHA_256: algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_ECDSA_SHA_256;
				break;
			case ECDSA_SHA_384:algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_ECDSA_SHA_384;
				break;
			case ECDSA_SHA_512:algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_ECDSA_SHA_512;
				break;
			case RSA_SHA_256:algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_RSA_SHA_256;
				break;
			case RSA_SHA_384:algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_RSA_SHA_384;
				break;
			case RSA_SHA_512:algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_RSA_SHA_512;
				break;
			default:
				break;}
		} else {
			algo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_ECDSA_SHA_256;
		}
		p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_ALG, algo);

		String canonic = null;
		
		if(modi.getRichiesta().getSicurezzaMessaggio().getFormaCanonicaXml()!=null) {
			switch(modi.getRichiesta().getSicurezzaMessaggio().getFormaCanonicaXml()) {
			case CANONICAL_XML_10:canonic = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_INCLUSIVE_C14N_10;
				break;
			case CANONICAL_XML_11:canonic = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_INCLUSIVE_C14N_11;
				break;
			case EXCLUSIVE_CANONICAL_XML_10: canonic = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_EXCLUSIVE_C14N_10;
				break;
			default:
				break;}
		} else {
			canonic = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_EXCLUSIVE_C14N_10;
		}
		p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_CANONICALIZATION_ALG, canonic);
		
		String rif509 = null;
		
		if(modi.getRichiesta().getSicurezzaMessaggio().getRiferimentoX509()!= null) {
			switch(modi.getRichiesta().getSicurezzaMessaggio().getRiferimentoX509()) {
			case BINARY_SECURITY_TOKEN: rif509 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_BINARY_SECURITY_TOKEN;
				break;
			case ISSUER_SERIAL_SECURITY_TOKEN_REFERENCE: rif509 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_SECURITY_TOKEN_REFERENCE;
				break;
			case SKI_KEY_IDENTIFIER: rif509 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_KEY_IDENTIFIER_SKI;
				break;
			case THUMBPRINT_KEY_IDENTIFIER: rif509 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_KEY_IDENTIFIER_THUMBPRINT;
				break;
			case X509_KEY_IDENTIFIER: rif509 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_KEY_IDENTIFIER_X509;
				break;
			default:
			}
		} else {
			rif509 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_BINARY_SECURITY_TOKEN;
		}

		p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509, rif509);
		
		p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_INCLUDE_SIGNATURE_TOKEN, modi.getRichiesta().getSicurezzaMessaggio().isIncludiSignatureToken() != null ? modi.getRichiesta().getSicurezzaMessaggio().isIncludiSignatureToken() : false ));
		p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_USE_CERTIFICATE_CHAIN, modi.getRichiesta().getSicurezzaMessaggio().isCertificateChain() != null ?modi.getRichiesta().getSicurezzaMessaggio().isCertificateChain() : false));

		//TODO info utente ecc
		
	}

}
