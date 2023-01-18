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

package org.openspcoop2.core.config.rs.server.api.impl.erogazioni;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.rs.server.api.impl.ProtocolPropertiesHelper;
import org.openspcoop2.core.config.rs.server.model.Erogazione;
import org.openspcoop2.core.config.rs.server.model.ErogazioneModI;
import org.openspcoop2.core.config.rs.server.model.ErogazioneModIRest;
import org.openspcoop2.core.config.rs.server.model.ErogazioneModIRestRichiesta;
import org.openspcoop2.core.config.rs.server.model.ErogazioneModIRestRichiestaSicurezzaMessaggio;
import org.openspcoop2.core.config.rs.server.model.ErogazioneModIRestRichiestaSicurezzaMessaggioContemporaneita;
import org.openspcoop2.core.config.rs.server.model.ErogazioneModIRestRisposta;
import org.openspcoop2.core.config.rs.server.model.ErogazioneModIRestRispostaSicurezzaMessaggio;
import org.openspcoop2.core.config.rs.server.model.ErogazioneModIRestRispostaSicurezzaMessaggioContemporaneita;
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
import org.openspcoop2.core.config.rs.server.model.FruizioneModIRestRichiestaSicurezzaMessaggioContemporaneita;
import org.openspcoop2.core.config.rs.server.model.FruizioneModIRestRisposta;
import org.openspcoop2.core.config.rs.server.model.FruizioneModIRestRispostaSicurezzaMessaggio;
import org.openspcoop2.core.config.rs.server.model.FruizioneModIRestRispostaSicurezzaMessaggioContemporaneita;
import org.openspcoop2.core.config.rs.server.model.FruizioneModISoap;
import org.openspcoop2.core.config.rs.server.model.FruizioneModISoapRichiesta;
import org.openspcoop2.core.config.rs.server.model.FruizioneModISoapRichiestaInformazioneUtente;
import org.openspcoop2.core.config.rs.server.model.FruizioneModISoapRichiestaSicurezzaMessaggio;
import org.openspcoop2.core.config.rs.server.model.FruizioneModISoapRisposta;
import org.openspcoop2.core.config.rs.server.model.FruizioneModISoapRispostaSicurezzaMessaggio;
import org.openspcoop2.core.config.rs.server.model.ModIKeyStoreArchive;
import org.openspcoop2.core.config.rs.server.model.ModIKeyStoreDefault;
import org.openspcoop2.core.config.rs.server.model.ModIKeyStoreFile;
import org.openspcoop2.core.config.rs.server.model.ModIKeyStoreHSM;
import org.openspcoop2.core.config.rs.server.model.ModIKeyStoreRidefinito;
import org.openspcoop2.core.config.rs.server.model.ModIKeystoreEnum;
import org.openspcoop2.core.config.rs.server.model.ModIKeystoreTipologiaEnum;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaMessaggioRestAlgoritmoFirma;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaMessaggioRestRiferimentoX509;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaMessaggioRestRiferimentoX509Risposta;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaMessaggioRestSameDifferentEnum;
import org.openspcoop2.core.config.rs.server.model.ModISicurezzaMessaggioRestTokenChoiseEnum;
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
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.sdk.properties.AbstractProperty;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesFactory;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;

/**
 * ModiErogazioniApiHelper
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
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
	
	class ErogazioneConf {
		public boolean sicurezzaMessaggioAPIAbilitata;
		public boolean sicurezzaMessaggioHeaderDuplicatiAbilitato;
	}
	
	class FruizioneConf extends ErogazioneConf {
		public boolean informazioniUtenteAbilitato;
	}
	
	private static boolean isSicurezzaMessaggioAbilitata(AccordoServizioParteComune aspc, String portType) throws Exception {
		List<String> valueAbilitato = new ArrayList<String>();
		valueAbilitato.add(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01);
		valueAbilitato.add(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02);
		valueAbilitato.add(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301);
		valueAbilitato.add(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302);
		return _isAbilitata(aspc, portType, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO, valueAbilitato, null);
	}
	private static boolean isSicurezzaMessaggioHeaderDuplicati(AccordoServizioParteComune aspc, String portType) throws Exception {
		if(!ServiceBinding.REST.equals(aspc.getServiceBinding())) {
			return false;
		}
		List<String> valueAbilitato = new ArrayList<String>();
		valueAbilitato.add(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA_AUTH_IN_RESPONSE);
		valueAbilitato.add(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA);
		valueAbilitato.add(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_CUSTOM_AUTH_IN_RESPONSE);
		valueAbilitato.add(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_CUSTOM);
		return _isAbilitata(aspc, portType, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER, valueAbilitato, null);
	}
	private static boolean isInfoUtenteAbilitato(AccordoServizioParteComune aspc, String portType) throws Exception {
		return _isAbilitata(aspc, portType, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA, null, true);
	}
	private static boolean _isAbilitata(AccordoServizioParteComune aspc, String portType, String pName, List<String> valueAbilitatoString, Boolean valueAbilitatoBoolean) throws Exception {
		if(aspc.getProtocolPropertyList()==null) {
			return false;
		}
		String stringValue = null;
		Boolean booleanValue = null;
		for(ProtocolProperty p: aspc.getProtocolPropertyList()) {
			if(p.getName().equals(pName)) {
				stringValue = p.getValue();	
				booleanValue = p.getBooleanValue();
				break;
			}
		}
		if(valueAbilitatoString!=null && StringUtils.isNotEmpty(stringValue) && valueAbilitatoString.contains(stringValue)) {
			return true;
		}
		else if(valueAbilitatoBoolean!=null && booleanValue!=null && valueAbilitatoBoolean.booleanValue()==booleanValue.booleanValue()) {
			return true;
		}
		
		if(ServiceBinding.REST.equals(aspc.getServiceBinding()) && aspc.sizeResourceList()>0) {
			for (Resource resource : aspc.getResourceList()) {
				stringValue = null;
				booleanValue = null;
				for(ProtocolProperty p: resource.getProtocolPropertyList()) {
					if(p.getName().equals(pName)) {
						stringValue = p.getValue();	
						booleanValue = p.getBooleanValue();
						break;
					}
				}
				if(valueAbilitatoString!=null && StringUtils.isNotEmpty(stringValue) && valueAbilitatoString.contains(stringValue)) {
					return true;
				}
				else if(valueAbilitatoBoolean!=null && booleanValue!=null && valueAbilitatoBoolean.booleanValue()==booleanValue.booleanValue()) {
					return true;
				}
			}
		}
		else {
			if(portType!=null && aspc.sizePortTypeList()>0) {
				PortType pt = null;
				for (PortType ptCheck : aspc.getPortTypeList()) {
					if(portType.equals(ptCheck.getNome())) {
						pt = ptCheck;
						break;
					}
				}
				if(pt!=null && pt.sizeAzioneList()>0) {
					for (Operation op : pt.getAzioneList()) {
						stringValue = null;
						booleanValue = null;
						for(ProtocolProperty p: op.getProtocolPropertyList()) {
							if(p.getName().equals(pName)) {
								stringValue = p.getValue();	
								booleanValue = p.getBooleanValue();
								break;
							}
						}
						if(valueAbilitatoString!=null && StringUtils.isNotEmpty(stringValue) && valueAbilitatoString.contains(stringValue)) {
							return true;
						}
						else if(valueAbilitatoBoolean!=null && booleanValue!=null && valueAbilitatoBoolean.booleanValue()==booleanValue.booleanValue()) {
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	
	private static ErogazioneConf getErogazioneConf(AccordoServizioParteSpecifica asps, ErogazioniEnv env) throws Exception {
		
		ErogazioneConf c = new ModiErogazioniApiHelper(). new ErogazioneConf();
		
		AccordoServizioParteComune aspc = env.apcCore.getAccordoServizioFull(asps.getIdAccordo());
		c.sicurezzaMessaggioAPIAbilitata = isSicurezzaMessaggioAbilitata(aspc, asps.getPortType());
		c.sicurezzaMessaggioHeaderDuplicatiAbilitato = isSicurezzaMessaggioHeaderDuplicati(aspc, asps.getPortType());
		
		return c;
	}
	
	private static FruizioneConf getFruizioneConf(AccordoServizioParteSpecifica asps, ErogazioniEnv env) throws Exception {
		
		FruizioneConf c = new ModiErogazioniApiHelper(). new FruizioneConf();
		
		AccordoServizioParteComune aspc = env.apcCore.getAccordoServizioFull(asps.getIdAccordo());
		c.sicurezzaMessaggioAPIAbilitata = isSicurezzaMessaggioAbilitata(aspc, asps.getPortType());
		c.sicurezzaMessaggioHeaderDuplicatiAbilitato = isSicurezzaMessaggioHeaderDuplicati(aspc, asps.getPortType());
		c.informazioniUtenteAbilitato= isInfoUtenteAbilitato(aspc, asps.getPortType());

		return c;
	}
	
	private static TipoApiEnum getTipoApi(AccordoServizioParteSpecifica asps, ErogazioniEnv env) throws Exception {
		AccordoServizioParteComune aspc = env.apcCore.getAccordoServizioFull(asps.getIdAccordo());
		return aspc.getFormatoSpecifica().equals(FormatoSpecifica.WSDL_11) ? TipoApiEnum.SOAP: TipoApiEnum.REST;

	}
	
	private static ErogazioneModISoap getSOAPProperties(Map<String, AbstractProperty<?>> p) throws Exception {

		ErogazioneModISoap modi = new ErogazioneModISoap();
		modi.setProtocollo(TipoApiEnum.SOAP);
		
		// *** richiesta ***
		
		ErogazioneModISoapRichiesta richiesta = new ErogazioneModISoapRichiesta();
		ErogazioneModISoapRichiestaSicurezzaMessaggio sicurezzaMessaggioRichiesta = new ErogazioneModISoapRichiestaSicurezzaMessaggio();
		
		String truststoreMode = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE, true);
		if(truststoreMode.equals(ModICostanti.MODIPA_PROFILO_DEFAULT)) {
			ModITrustStoreDefault truststore = new ModITrustStoreDefault().modalita(StatoDefaultRidefinitoEnum.DEFAULT);
			sicurezzaMessaggioRichiesta.setTruststore(truststore);
		} else {
			ModITrustStoreRidefinito truststore = new ModITrustStoreRidefinito().modalita(StatoDefaultRidefinitoEnum.RIDEFINITO);
			
			truststore.setTruststoreCrl(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS, true));
			truststore.setTruststoreOcspPolicy(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_OCSP_POLICY, false));
			ModITruststoreEnum tipo = null;
			String truststoreTipoString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE, true);
			if(HSMUtils.isKeystoreHSM(truststoreTipoString)) {
				tipo = ModITruststoreEnum.PKCS11;
				truststore.setPcks11Tipo(truststoreTipoString);
			}
			else {
				if(truststoreTipoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_VALUE_JKS)) {
					tipo = ModITruststoreEnum.JKS;
				}
			}
			truststore.setTruststoreTipo(tipo);
			truststore.setTruststorePath(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH, true));
			truststore.setTruststorePassword(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD, true));
			sicurezzaMessaggioRichiesta.setTruststore(truststore);
		}
		
		String iatMode = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_IAT, false);
		if(StringUtils.isNotEmpty(iatMode) && ModICostanti.MODIPA_PROFILO_RIDEFINISCI.equals(iatMode)) {
			sicurezzaMessaggioRichiesta.setTimeToLive(ProtocolPropertiesHelper.getIntegerProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_IAT_SECONDS, false));
		}
		
		String wsaTo = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE, false);
		if(wsaTo != null) {
			sicurezzaMessaggioRichiesta.setWsaTo(wsaTo);
		}
		
		richiesta.setSicurezzaMessaggio(sicurezzaMessaggioRichiesta);
		modi.setRichiesta(richiesta);
		
		
		// *** risposta ***
				
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
		
		String headersString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_SOAP, false);
		if(headersString!=null) {
			List<String> headers = new ArrayList<>();
			String[] headersplit = headersString.split("\n");
			for(String header: headersplit) {
				headers.add(header);
			}
			sicurezzaMessaggio.setHeaderSoapFirmare(headers);
		}
		
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
		
		sicurezzaMessaggio.setCertificateChain(ProtocolPropertiesHelper.getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_USE_CERTIFICATE_CHAIN, true));
		sicurezzaMessaggio.setIncludiSignatureToken(ProtocolPropertiesHelper.getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_INCLUDE_SIGNATURE_TOKEN, true));

		sicurezzaMessaggio.setTimeToLive(ProtocolPropertiesHelper.getIntegerProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_EXPIRED, true));
		
		String keystoreTypeString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE, true);
		if(keystoreTypeString.equals(ModICostanti.MODIPA_PROFILO_DEFAULT)) {
			
			ModIKeyStoreDefault ks = new ModIKeyStoreDefault().modalita(StatoDefaultRidefinitoEnum.DEFAULT);
			sicurezzaMessaggio.setKeystore(ks);
		} else {
			
			ModIKeyStoreRidefinito ks = new ModIKeyStoreRidefinito().modalita(StatoDefaultRidefinitoEnum.RIDEFINITO);
			
			String keystoreModeString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_KEYSTORE_MODE, true);

			if(keystoreModeString.equals(ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_HSM)) {
				ModIKeyStoreHSM datiKeystore = new ModIKeyStoreHSM().tipologia(ModIKeystoreTipologiaEnum.HSM);

				String keystoreTipoString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_KEYSTORE_TYPE, true);
				datiKeystore.setPcks11Tipo(keystoreTipoString);
				datiKeystore.setKeyAlias(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_KEY_ALIAS, true));
				
				ks.setDatiKeystore(datiKeystore);
			}
			else if(keystoreModeString.equals(ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_PATH)) {
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
		
		// *** richiesta ***
		
		ErogazioneModIRestRichiesta richiesta = new ErogazioneModIRestRichiesta();
		ErogazioneModIRestRichiestaSicurezzaMessaggio sicurezzaMessaggioRichiesta = new ErogazioneModIRestRichiestaSicurezzaMessaggio();
		
		sicurezzaMessaggioRichiesta.setRiferimentoX509(getX509(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_RIFERIMENTO_X509, true)));
		
		String truststoreMode = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE, true);
		if(truststoreMode.equals(ModICostanti.MODIPA_PROFILO_DEFAULT)) {
			ModITrustStoreDefault truststore = new ModITrustStoreDefault().modalita(StatoDefaultRidefinitoEnum.DEFAULT);
			sicurezzaMessaggioRichiesta.setTruststore(truststore);
		} else {
			ModITrustStoreRidefinito truststore = new ModITrustStoreRidefinito().modalita(StatoDefaultRidefinitoEnum.RIDEFINITO);
			
			truststore.setTruststoreCrl(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS, true));
			truststore.setTruststoreOcspPolicy(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_OCSP_POLICY, false));
			ModITruststoreEnum tipo = null;
			String truststoreTipoString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE, true);
			if(HSMUtils.isKeystoreHSM(truststoreTipoString)) {
				tipo = ModITruststoreEnum.PKCS11;
				truststore.setPcks11Tipo(truststoreTipoString);
			}
			else {
				if(truststoreTipoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_VALUE_JKS)) {
					tipo = ModITruststoreEnum.JKS;
				}
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
				truststore.setTruststoreOcspPolicy(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_OCSP_POLICY, false));
				ModITruststoreEnum tipo = null;
				String truststoreTipoString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE, true);
				if(HSMUtils.isKeystoreHSM(truststoreTipoString)) {
					tipo = ModITruststoreEnum.PKCS11;
					truststore.setPcks11Tipo(truststoreTipoString);
				}
				else {
					if(truststoreTipoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_VALUE_JKS)) {
						tipo = ModITruststoreEnum.JKS;
					}
				}
				truststore.setTruststoreTipo(tipo);
				truststore.setTruststorePath(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH, true));
				truststore.setTruststorePassword(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PASSWORD, true));
				sicurezzaMessaggioRichiesta.setTruststoreSsl(truststore);
			}
		}
		
		String iatMode = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_IAT, false);
		if(StringUtils.isNotEmpty(iatMode) && ModICostanti.MODIPA_PROFILO_RIDEFINISCI.equals(iatMode)) {
			sicurezzaMessaggioRichiesta.setTimeToLive(ProtocolPropertiesHelper.getIntegerProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_IAT_SECONDS, false));
		}

		sicurezzaMessaggioRichiesta.setAudience(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE, false));
		
		// contemporaneita'
		
		String idJtiRichiesta = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_FILTRO_DUPLICATI, false);
		String audienceRichiesta = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_AUDIENCE, false);
		if(StringUtils.isNotEmpty(idJtiRichiesta) || StringUtils.isNotEmpty(audienceRichiesta) ) {
			
			ErogazioneModIRestRichiestaSicurezzaMessaggioContemporaneita richiestaContemporaneita = new ErogazioneModIRestRichiestaSicurezzaMessaggioContemporaneita();
			
			if(StringUtils.isNotEmpty(idJtiRichiesta)) {
				if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_FILTRO_DUPLICATI_VALUE_AUTHORIZATION.equals(idJtiRichiesta)) {
					richiestaContemporaneita.setIdentificativo(ModISicurezzaMessaggioRestTokenChoiseEnum.BEARER);
				}
				else if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_FILTRO_DUPLICATI_VALUE_MODI.equals(idJtiRichiesta)) {
					richiestaContemporaneita.setIdentificativo(ModISicurezzaMessaggioRestTokenChoiseEnum.AGID);
				}
			}
			if(richiestaContemporaneita.getIdentificativo()==null) {
				richiestaContemporaneita.setIdentificativo(ModISicurezzaMessaggioRestTokenChoiseEnum.AGID); // default
			}
			
			if(StringUtils.isNotEmpty(audienceRichiesta)) {
				if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_AUDIENCE_VALUE_SAME.equals(audienceRichiesta)) {
					richiestaContemporaneita.setAudience(ModISicurezzaMessaggioRestSameDifferentEnum.SAME);
				}
				else if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_AUDIENCE_VALUE_DIFFERENT.equals(audienceRichiesta)) {
					richiestaContemporaneita.setAudience(ModISicurezzaMessaggioRestSameDifferentEnum.DIFFERENT);
				}
			}
			if(richiestaContemporaneita.getAudience()==null) {
				richiestaContemporaneita.setAudience(ModISicurezzaMessaggioRestSameDifferentEnum.SAME); // default
			}
			if(ModISicurezzaMessaggioRestSameDifferentEnum.DIFFERENT.equals(richiestaContemporaneita.getAudience())) {
				String audienceAtteso = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_AUDIENCE_INTEGRITY, false);
				richiestaContemporaneita.setAudienceAtteso(audienceAtteso);
			}
			
			sicurezzaMessaggioRichiesta.setContemporaneita(richiestaContemporaneita);
		}
		
		richiesta.setSicurezzaMessaggio(sicurezzaMessaggioRichiesta);
		modi.setRichiesta(richiesta);
		
		
		
		// *** risposta ***
		
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
		
		String headersString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST, false);
		if(headersString!=null) {
			List<String> headers = new ArrayList<>();
			String[] headersplit = headersString.split(",");
			for(String header: headersplit) {
				headers.add(header);
				
			}
			sicurezzaMessaggio.setHeaderHttpFirmare(headers);
		}
		
		if(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_AS_REQUEST, true).equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_AS_REQUEST_VALUE_TRUE)){
			sicurezzaMessaggio.setRiferimentoX509(ModISicurezzaMessaggioRestRiferimentoX509Risposta.RICHIESTA);
		} else {
			sicurezzaMessaggio.setRiferimentoX509(ModISicurezzaMessaggioRestRiferimentoX509Risposta.RIDEFINITO);
			sicurezzaMessaggio.setRiferimentoX509Risposta(getX509(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509, true)));
		}
		
		sicurezzaMessaggio.setCertificateChain(ProtocolPropertiesHelper.getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_X5C_USE_CERTIFICATE_CHAIN, true));
		
		sicurezzaMessaggio.setUrl(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_X509_VALUE_X5URL, false));
		
		sicurezzaMessaggio.setTimeToLive(ProtocolPropertiesHelper.getIntegerProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_EXPIRED, true));
		
		String pString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_JWT_CLAIMS, false);
		if(pString!=null) {
			List<String> proprieta = new ArrayList<>();
			String[] psplit = pString.split("\n");
			for(String pr: psplit) {
				proprieta.add(pr);
			}
			sicurezzaMessaggio.setClaims(proprieta);
		}
		
		String keystoreTypeString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE, true);
		if(keystoreTypeString.equals(ModICostanti.MODIPA_PROFILO_DEFAULT)) {
			
			ModIKeyStoreDefault ks = new ModIKeyStoreDefault().modalita(StatoDefaultRidefinitoEnum.DEFAULT);
			sicurezzaMessaggio.setKeystore(ks);
		} else {
			
			ModIKeyStoreRidefinito ks = new ModIKeyStoreRidefinito().modalita(StatoDefaultRidefinitoEnum.RIDEFINITO);
			
			String keystoreModeString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_KEYSTORE_MODE, true);

			if(keystoreModeString.equals(ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_HSM)) {
				ModIKeyStoreHSM datiKeystore = new ModIKeyStoreHSM().tipologia(ModIKeystoreTipologiaEnum.HSM);

				String keystoreTipoString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_KEYSTORE_TYPE, true);
				datiKeystore.setPcks11Tipo(keystoreTipoString);
				datiKeystore.setKeyAlias(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_KEY_ALIAS, true));
				
				ks.setDatiKeystore(datiKeystore);
			}
			else if(keystoreModeString.equals(ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_PATH)) {
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
		
		
		// contemporaneita'
		
		String idJtiRisposta = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_JTI, false);
		String claimsAuthRisposta = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION, false);
		String claimsModiRisposta = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_JWT_CLAIMS_MODI, false);
		if(StringUtils.isNotEmpty(idJtiRisposta) || 
				StringUtils.isNotEmpty(claimsAuthRisposta)  || StringUtils.isNotEmpty(claimsModiRisposta) ) {
			ErogazioneModIRestRispostaSicurezzaMessaggioContemporaneita rispostaContemporaneita = new ErogazioneModIRestRispostaSicurezzaMessaggioContemporaneita();
			
			if(StringUtils.isNotEmpty(idJtiRisposta)) {
				if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JTI_VALUE_SAME.equals(idJtiRisposta)) {
					rispostaContemporaneita.setIdentificativo(ModISicurezzaMessaggioRestSameDifferentEnum.SAME);
				}
				else if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JTI_VALUE_DIFFERENT.equals(idJtiRisposta)) {
					rispostaContemporaneita.setIdentificativo(ModISicurezzaMessaggioRestSameDifferentEnum.DIFFERENT);
				}
			}
			if(rispostaContemporaneita.getIdentificativo()==null) {
				rispostaContemporaneita.setIdentificativo(ModISicurezzaMessaggioRestSameDifferentEnum.SAME); // default
			}
			if(ModISicurezzaMessaggioRestSameDifferentEnum.DIFFERENT.equals(rispostaContemporaneita.getIdentificativo())) {
				String idJtiAsIdMessaggio = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO, false);
				if(StringUtils.isNotEmpty(idJtiAsIdMessaggio)) {
					if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_VALUE_AUTHORIZATION.equals(idJtiAsIdMessaggio)) {
						rispostaContemporaneita.setUsaComeIdMessaggio(ModISicurezzaMessaggioRestTokenChoiseEnum.BEARER);
					}
					else if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_VALUE_MODI.equals(idJtiAsIdMessaggio)) {
						rispostaContemporaneita.setUsaComeIdMessaggio(ModISicurezzaMessaggioRestTokenChoiseEnum.AGID);
					}
				}
				if(rispostaContemporaneita.getUsaComeIdMessaggio()==null) {
					rispostaContemporaneita.setUsaComeIdMessaggio(ModISicurezzaMessaggioRestTokenChoiseEnum.AGID); // default
				}
			}
			
			if(claimsAuthRisposta!=null) {
				List<String> proprieta = new ArrayList<>();
				String[] psplit = claimsAuthRisposta.split("\n");
				for(String pr: psplit) {
					proprieta.add(pr);
				}
				rispostaContemporaneita.setClaimsBearer(proprieta);
			}
			if(claimsModiRisposta!=null) {
				List<String> proprieta = new ArrayList<>();
				String[] psplit = claimsModiRisposta.split("\n");
				for(String pr: psplit) {
					proprieta.add(pr);
				}
				rispostaContemporaneita.setClaimsAgid(proprieta);
			}
			
			sicurezzaMessaggio.setContemporaneita(rispostaContemporaneita);
		}
		
		risposta.setSicurezzaMessaggio(sicurezzaMessaggio);
		
		modi.setRisposta(risposta);
		
		
		return modi;
	}

	private static FruizioneModISoap getFruizioneModISoap(Map<String, AbstractProperty<?>> p) throws Exception {

		FruizioneModISoap modi = new FruizioneModISoap();
		modi.setProtocollo(TipoApiEnum.SOAP);
		
		// *** richiesta ***
		
		FruizioneModISoapRichiesta richiesta = new FruizioneModISoapRichiesta();
		FruizioneModISoapRichiestaSicurezzaMessaggio sicurezzaMessaggioRichiesta = new FruizioneModISoapRichiestaSicurezzaMessaggio();
		
		ModISicurezzaMessaggioSoapAlgoritmoFirma algo = null;
		String algoString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_ALG, true);
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
		String canonicString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_CANONICALIZATION_ALG, true);
		if(canonicString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_INCLUSIVE_C14N_10)) {
			canonic = ModISicurezzaMessaggioSoapFormaCanonicaXml.CANONICAL_XML_10;
		} else if(canonicString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_INCLUSIVE_C14N_11)) {
			canonic = ModISicurezzaMessaggioSoapFormaCanonicaXml.CANONICAL_XML_11;
		} else if(canonicString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_EXCLUSIVE_C14N_10)) {
			canonic = ModISicurezzaMessaggioSoapFormaCanonicaXml.EXCLUSIVE_CANONICAL_XML_10;
		}
		sicurezzaMessaggioRichiesta.setFormaCanonicaXml(canonic);
		
		String headersString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_SOAP, false);
		if(headersString!=null) {
			List<String> headers = new ArrayList<>();
			String[] headersplit = headersString.split("\n");
			for(String header: headersplit) {
				headers.add(header);
			}
			sicurezzaMessaggioRichiesta.setHeaderSoapFirmare(headers);
		}
		
		ModISicurezzaMessaggioSoapRiferimentoX509 rif509 = null;
		String rif509String = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_RIFERIMENTO_X509, true);
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
		
		sicurezzaMessaggioRichiesta.setCertificateChain(ProtocolPropertiesHelper.getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_USE_CERTIFICATE_CHAIN, true));
		sicurezzaMessaggioRichiesta.setIncludiSignatureToken(ProtocolPropertiesHelper.getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_INCLUDE_SIGNATURE_TOKEN, true));
		
		sicurezzaMessaggioRichiesta.setTimeToLive(ProtocolPropertiesHelper.getIntegerProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_EXPIRED, true));
		
		String wsaTo = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE, false);
		if(wsaTo != null) {
			sicurezzaMessaggioRichiesta.setWsaTo(wsaTo);
		}
		
		
		// info utente
		
		sicurezzaMessaggioRichiesta.setInformazioniUtenteCodiceEnte(getInformazioniUtenteCodiceEnte(p));
		sicurezzaMessaggioRichiesta.setInformazioniUtenteUserid(getInformazioniUtenteUserid(p));
		sicurezzaMessaggioRichiesta.setInformazioniUtenteIndirizzoIp(getInformazioniUtenteIndirizzoIp(p));
		
		richiesta.setSicurezzaMessaggio(sicurezzaMessaggioRichiesta);
		modi.setRichiesta(richiesta);
		
		
		
		// *** risposta ***
		
		FruizioneModISoapRispostaSicurezzaMessaggio sicurezzaMessaggioRisposta = new FruizioneModISoapRispostaSicurezzaMessaggio();
		FruizioneModISoapRisposta risposta = new FruizioneModISoapRisposta();
		
		String truststoreMode = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE, true);
		if(truststoreMode.equals(ModICostanti.MODIPA_PROFILO_DEFAULT)) {
			ModITrustStoreDefault truststore = new ModITrustStoreDefault().modalita(StatoDefaultRidefinitoEnum.DEFAULT);
			sicurezzaMessaggioRisposta.setTruststore(truststore);
		} else {
			ModITrustStoreRidefinito truststore = new ModITrustStoreRidefinito().modalita(StatoDefaultRidefinitoEnum.RIDEFINITO);
			
			truststore.setTruststoreCrl(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS, true));
			truststore.setTruststoreOcspPolicy(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_OCSP_POLICY, false));
			ModITruststoreEnum tipo = null;
			String truststoreTipoString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE, true);
			if(HSMUtils.isKeystoreHSM(truststoreTipoString)) {
				tipo = ModITruststoreEnum.PKCS11;
				truststore.setPcks11Tipo(truststoreTipoString);
			}
			else {
				if(truststoreTipoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_VALUE_JKS)) {
					tipo = ModITruststoreEnum.JKS;
				}
			}
			truststore.setTruststoreTipo(tipo);
			truststore.setTruststorePath(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH, true));
			truststore.setTruststorePassword(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD, true));
			sicurezzaMessaggioRisposta.setTruststore(truststore);
		}
		
		String iatMode = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_IAT, false);
		if(StringUtils.isNotEmpty(iatMode) && ModICostanti.MODIPA_PROFILO_RIDEFINISCI.equals(iatMode)) {
			sicurezzaMessaggioRisposta.setTimeToLive(ProtocolPropertiesHelper.getIntegerProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_IAT_SECONDS, false));
		}

		Boolean verificaWsaTo = ProtocolPropertiesHelper.getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE, false);
		sicurezzaMessaggioRisposta.setVerificaWsaTo(ProtocolPropertiesHelper.getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE, verificaWsaTo!=null ? verificaWsaTo : false));
		if(sicurezzaMessaggioRisposta.isVerificaWsaTo()!=null && sicurezzaMessaggioRisposta.isVerificaWsaTo()) {
			sicurezzaMessaggioRisposta.setAudienceAtteso(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE_VALORE, false));	
		}
		
		risposta.setSicurezzaMessaggio(sicurezzaMessaggioRisposta);
		
		modi.setRisposta(risposta);
		
		
		return modi;
	}

	private static FruizioneModIRest getFruizioneModIRest(Map<String, AbstractProperty<?>> p) throws Exception {

		FruizioneModIRest modi = new FruizioneModIRest();
		modi.setProtocollo(TipoApiEnum.REST);
		
		// *** richiesta ***
		
		FruizioneModIRestRichiesta richiesta = new FruizioneModIRestRichiesta();
		FruizioneModIRestRichiestaSicurezzaMessaggio sicurezzaMessaggioRichiesta = new FruizioneModIRestRichiestaSicurezzaMessaggio();

		ModISicurezzaMessaggioRestAlgoritmoFirma algo = null;
		String algoString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_ALG, true);
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
		
		String headersString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST, false);
		if(headersString!=null) {
			List<String> headers = new ArrayList<>();
			String[] headersplit = headersString.split(",");
			for(String header: headersplit) {
				headers.add(header);
			}
			sicurezzaMessaggioRichiesta.setHeaderHttpFirmare(headers);
		}
		
		sicurezzaMessaggioRichiesta.setRiferimentoX509(getX509(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_RIFERIMENTO_X509, true)));

		sicurezzaMessaggioRichiesta.setCertificateChain(ProtocolPropertiesHelper.getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_RIFERIMENTO_X509_X5C_USE_CERTIFICATE_CHAIN, true));
		
		sicurezzaMessaggioRichiesta.setTimeToLive(ProtocolPropertiesHelper.getIntegerProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_EXPIRED, true));
		
		sicurezzaMessaggioRichiesta.setAudience(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE, false));
		
		String pString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_JWT_CLAIMS, false);
		if(pString!=null) {
			List<String> proprieta = new ArrayList<>();
			String[] psplit = pString.split("\n");
			for(String pr: psplit) {
				proprieta.add(pr);
			}
			sicurezzaMessaggioRichiesta.setClaims(proprieta);
		}
		
		// info utente
		
		sicurezzaMessaggioRichiesta.setInformazioniUtenteCodiceEnte(getInformazioniUtenteCodiceEnte(p));
		sicurezzaMessaggioRichiesta.setInformazioniUtenteUserid(getInformazioniUtenteUserid(p));
		sicurezzaMessaggioRichiesta.setInformazioniUtenteIndirizzoIp(getInformazioniUtenteIndirizzoIp(p));
		
		// contemporaneita'
		
		String idJtiRichiesta = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_JTI, false);
		String audienceRichiesta = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_AUDIENCE, false);
		String claimsAuthRichiesta = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION, false);
		String claimsModiRichiesta = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_JWT_CLAIMS_MODI, false);
		if(StringUtils.isNotEmpty(idJtiRichiesta) || StringUtils.isNotEmpty(audienceRichiesta)  ||
				StringUtils.isNotEmpty(claimsAuthRichiesta)  || StringUtils.isNotEmpty(claimsModiRichiesta) ) {
			FruizioneModIRestRichiestaSicurezzaMessaggioContemporaneita richiestaContemporaneita = new FruizioneModIRestRichiestaSicurezzaMessaggioContemporaneita();
			
			if(StringUtils.isNotEmpty(idJtiRichiesta)) {
				if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JTI_VALUE_SAME.equals(idJtiRichiesta)) {
					richiestaContemporaneita.setIdentificativo(ModISicurezzaMessaggioRestSameDifferentEnum.SAME);
				}
				else if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JTI_VALUE_DIFFERENT.equals(idJtiRichiesta)) {
					richiestaContemporaneita.setIdentificativo(ModISicurezzaMessaggioRestSameDifferentEnum.DIFFERENT);
				}
			}
			if(richiestaContemporaneita.getIdentificativo()==null) {
				richiestaContemporaneita.setIdentificativo(ModISicurezzaMessaggioRestSameDifferentEnum.SAME); // default
			}
			if(ModISicurezzaMessaggioRestSameDifferentEnum.DIFFERENT.equals(richiestaContemporaneita.getIdentificativo())) {
				String idJtiAsIdMessaggio = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO, false);
				if(StringUtils.isNotEmpty(idJtiAsIdMessaggio)) {
					if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_VALUE_AUTHORIZATION.equals(idJtiAsIdMessaggio)) {
						richiestaContemporaneita.setUsaComeIdMessaggio(ModISicurezzaMessaggioRestTokenChoiseEnum.BEARER);
					}
					else if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_VALUE_MODI.equals(idJtiAsIdMessaggio)) {
						richiestaContemporaneita.setUsaComeIdMessaggio(ModISicurezzaMessaggioRestTokenChoiseEnum.AGID);
					}
				}
				if(richiestaContemporaneita.getUsaComeIdMessaggio()==null) {
					richiestaContemporaneita.setUsaComeIdMessaggio(ModISicurezzaMessaggioRestTokenChoiseEnum.AGID); // default
				}
			}
			
			if(StringUtils.isNotEmpty(audienceRichiesta)) {
				if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_AUDIENCE_VALUE_SAME.equals(audienceRichiesta)) {
					richiestaContemporaneita.setAudience(ModISicurezzaMessaggioRestSameDifferentEnum.SAME);
				}
				else if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_AUDIENCE_VALUE_DIFFERENT.equals(audienceRichiesta)) {
					richiestaContemporaneita.setAudience(ModISicurezzaMessaggioRestSameDifferentEnum.DIFFERENT);
				}
			}
			if(richiestaContemporaneita.getAudience()==null) {
				richiestaContemporaneita.setAudience(ModISicurezzaMessaggioRestSameDifferentEnum.SAME); // default
			}
			if(ModISicurezzaMessaggioRestSameDifferentEnum.DIFFERENT.equals(richiestaContemporaneita.getAudience())) {
				String audienceAtteso = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_AUDIENCE_INTEGRITY, false);
				richiestaContemporaneita.setAudienceAtteso(audienceAtteso);
			}
			
			if(claimsAuthRichiesta!=null) {
				List<String> proprieta = new ArrayList<>();
				String[] psplit = claimsAuthRichiesta.split("\n");
				for(String pr: psplit) {
					proprieta.add(pr);
				}
				richiestaContemporaneita.setClaimsBearer(proprieta);
			}
			if(claimsModiRichiesta!=null) {
				List<String> proprieta = new ArrayList<>();
				String[] psplit = claimsModiRichiesta.split("\n");
				for(String pr: psplit) {
					proprieta.add(pr);
				}
				richiestaContemporaneita.setClaimsAgid(proprieta);
			}
			
			sicurezzaMessaggioRichiesta.setContemporaneita(richiestaContemporaneita);
		}
		
		richiesta.setSicurezzaMessaggio(sicurezzaMessaggioRichiesta);
		modi.setRichiesta(richiesta);
		
		
		// *** risposta ***
		
		FruizioneModIRestRisposta risposta = new FruizioneModIRestRisposta();
		FruizioneModIRestRispostaSicurezzaMessaggio sicurezzaMessaggioRisposta = new FruizioneModIRestRispostaSicurezzaMessaggio();

		if(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_AS_REQUEST, true).equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_AS_REQUEST_VALUE_TRUE)){
			sicurezzaMessaggioRisposta.setRiferimentoX509(ModISicurezzaMessaggioRestRiferimentoX509Risposta.RICHIESTA);
		} else {
			sicurezzaMessaggioRisposta.setRiferimentoX509(ModISicurezzaMessaggioRestRiferimentoX509Risposta.RIDEFINITO);
			sicurezzaMessaggioRisposta.setRiferimentoX509Risposta(getX509(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509, true)));
		}
		
		String truststoreMode = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE, true);
		if(truststoreMode.equals(ModICostanti.MODIPA_PROFILO_DEFAULT)) {
			ModITrustStoreDefault truststore = new ModITrustStoreDefault().modalita(StatoDefaultRidefinitoEnum.DEFAULT);
			sicurezzaMessaggioRisposta.setTruststore(truststore);
		} else {
			ModITrustStoreRidefinito truststore = new ModITrustStoreRidefinito().modalita(StatoDefaultRidefinitoEnum.RIDEFINITO);
			
			truststore.setTruststoreCrl(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS, true));
			truststore.setTruststoreOcspPolicy(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_OCSP_POLICY, false));
			ModITruststoreEnum tipo = null;
			String truststoreTipoString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE, true);
			if(HSMUtils.isKeystoreHSM(truststoreTipoString)) {
				tipo = ModITruststoreEnum.PKCS11;
				truststore.setPcks11Tipo(truststoreTipoString);
			}
			else {
				if(truststoreTipoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_VALUE_JKS)) {
					tipo = ModITruststoreEnum.JKS;
				}
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
				truststore.setTruststoreOcspPolicy(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_OCSP_POLICY, false));
				ModITruststoreEnum tipo = null;
				String truststoreTipoString = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE, true);
				if(HSMUtils.isKeystoreHSM(truststoreTipoString)) {
					tipo = ModITruststoreEnum.PKCS11;
					truststore.setPcks11Tipo(truststoreTipoString);
				}
				else {
					if(truststoreTipoString.equals(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_VALUE_JKS)) {
						tipo = ModITruststoreEnum.JKS;
					}
				}
				truststore.setTruststoreTipo(tipo);
				truststore.setTruststorePath(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH, true));
				truststore.setTruststorePassword(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PASSWORD, true));
				sicurezzaMessaggioRisposta.setTruststoreSsl(truststore);
			}
		}
		
		String iatMode = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_IAT, false);
		if(StringUtils.isNotEmpty(iatMode) && ModICostanti.MODIPA_PROFILO_RIDEFINISCI.equals(iatMode)) {
			sicurezzaMessaggioRisposta.setTimeToLive(ProtocolPropertiesHelper.getIntegerProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_IAT_SECONDS, false));
		}

		Boolean verificaAudience = ProtocolPropertiesHelper.getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE, false);
		sicurezzaMessaggioRisposta.setVerificaAudience(ProtocolPropertiesHelper.getBooleanProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE, verificaAudience!=null ? verificaAudience : false));
		if(sicurezzaMessaggioRisposta.isVerificaAudience()!=null && sicurezzaMessaggioRisposta.isVerificaAudience()) {
			sicurezzaMessaggioRisposta.setAudienceAtteso(ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE_VALORE, false));	
		}
		
		// contemporaneita'
		
		String idJtiRisposta = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_FILTRO_DUPLICATI, false);
		String audienceRisposta = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_AUDIENCE, false);
		if(StringUtils.isNotEmpty(idJtiRisposta) || StringUtils.isNotEmpty(audienceRisposta) ) {
			
			FruizioneModIRestRispostaSicurezzaMessaggioContemporaneita rispostaContemporaneita = new FruizioneModIRestRispostaSicurezzaMessaggioContemporaneita();
			
			if(StringUtils.isNotEmpty(idJtiRisposta)) {
				if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_FILTRO_DUPLICATI_VALUE_AUTHORIZATION.equals(idJtiRisposta)) {
					rispostaContemporaneita.setIdentificativo(ModISicurezzaMessaggioRestTokenChoiseEnum.BEARER);
				}
				else if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_FILTRO_DUPLICATI_VALUE_MODI.equals(idJtiRisposta)) {
					rispostaContemporaneita.setIdentificativo(ModISicurezzaMessaggioRestTokenChoiseEnum.AGID);
				}
			}
			if(rispostaContemporaneita.getIdentificativo()==null) {
				rispostaContemporaneita.setIdentificativo(ModISicurezzaMessaggioRestTokenChoiseEnum.AGID); // default
			}
			
			if(StringUtils.isNotEmpty(audienceRisposta)) {
				if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_AUDIENCE_VALUE_SAME.equals(audienceRisposta)) {
					rispostaContemporaneita.setAudience(ModISicurezzaMessaggioRestSameDifferentEnum.SAME);
				}
				else if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_AUDIENCE_VALUE_DIFFERENT.equals(audienceRisposta)) {
					rispostaContemporaneita.setAudience(ModISicurezzaMessaggioRestSameDifferentEnum.DIFFERENT);
				}
			}
			if(rispostaContemporaneita.getAudience()==null) {
				rispostaContemporaneita.setAudience(ModISicurezzaMessaggioRestSameDifferentEnum.SAME); // default
			}
			if(ModISicurezzaMessaggioRestSameDifferentEnum.DIFFERENT.equals(rispostaContemporaneita.getAudience())) {
				String audienceAtteso = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_AUDIENCE_INTEGRITY, false);
				rispostaContemporaneita.setAudienceAtteso(audienceAtteso);
			}
			
			sicurezzaMessaggioRisposta.setContemporaneita(rispostaContemporaneita);
		}
		
		risposta.setSicurezzaMessaggio(sicurezzaMessaggioRisposta);
		
		modi.setRisposta(risposta);
		
		
		return modi;
	}

	private static FruizioneModISoapRichiestaInformazioneUtente getInformazioniUtenteCodiceEnte(
			Map<String, AbstractProperty<?>> p) throws Exception {
		return getInformazioniUtente(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_MODE, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE);
	}

	private static FruizioneModISoapRichiestaInformazioneUtente getInformazioniUtente(
			Map<String, AbstractProperty<?>> p, String mod, String value) throws Exception {
		
		String modalitaString = ProtocolPropertiesHelper.getStringProperty(p, mod, false);
		
		if(modalitaString == null) {
			return null;
		} else {
			FruizioneModISoapRichiestaInformazioneUtente u = new FruizioneModISoapRichiestaInformazioneUtente();
			StatoDefaultRidefinitoEnum modalita = modalitaString.equals(ModICostanti.MODIPA_PROFILO_DEFAULT) ? modalita = StatoDefaultRidefinitoEnum.DEFAULT : StatoDefaultRidefinitoEnum.RIDEFINITO;
		
			u.setModalita(modalita);
			if(modalita.equals(StatoDefaultRidefinitoEnum.RIDEFINITO)) {
				String valueString = ProtocolPropertiesHelper.getStringProperty(p, value, false);
				u.setValore(valueString);
			}

			return u;
		}
	}

	private static FruizioneModISoapRichiestaInformazioneUtente getInformazioniUtenteUserid(
			Map<String, AbstractProperty<?>> p) throws Exception {
		return getInformazioniUtente(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_MODE, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER);
	}

	private static FruizioneModISoapRichiestaInformazioneUtente getInformazioniUtenteIndirizzoIp(
			Map<String, AbstractProperty<?>> p) throws Exception {
		return getInformazioniUtente(p, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER);
	}

	private static List<ModISicurezzaMessaggioRestRiferimentoX509> getX509(String x509String) {
		String[] split = x509String.split(",");
		
		List<ModISicurezzaMessaggioRestRiferimentoX509> lst = new ArrayList<>();
		for(String x: split) {
			lst.add(ModISicurezzaMessaggioRestRiferimentoX509.fromValue(x));
		}
		
		return lst;
		
	}

	public static ProtocolProperties getProtocolProperties(Erogazione body, AccordoServizioParteSpecifica asps, ErogazioniEnv env, boolean required) throws Exception {
		return getModiProtocolProperties(body.getModi(), getTipoApi(asps, env), getErogazioneConf(asps, env), required);
	}

	public static ProtocolProperties getProtocolProperties(Fruizione body, AccordoServizioParteSpecifica asps, ErogazioniEnv env, boolean required) throws Exception {
		return getModiProtocolProperties(body.getModi(), getTipoApi(asps, env), getFruizioneConf(asps, env), required);
	}

	public static ProtocolProperties updateModiProtocolProperties(AccordoServizioParteSpecifica asps, ProfiloEnum profilo, OneOfErogazioneModIModi modi, ErogazioniEnv env) throws Exception {
		if(profilo == null || (!profilo.equals(ProfiloEnum.MODI) && !profilo.equals(ProfiloEnum.MODIPA))) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Operazione utilizzabile solamente con Profilo 'ModI'");
		}

		TipoApiEnum protocollo = getTipoApi(asps, env);
		ErogazioneConf erogazioneConf = getErogazioneConf(asps, env);
		return getModiProtocolProperties(modi, protocollo, erogazioneConf);

	}

	public static ProtocolProperties updateModiProtocolProperties(AccordoServizioParteSpecifica asps, ProfiloEnum profilo, OneOfFruizioneModIModi modi, ErogazioniEnv env) throws Exception {
		if(profilo == null || (!profilo.equals(ProfiloEnum.MODI) && !profilo.equals(ProfiloEnum.MODIPA))) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Operazione utilizzabile solamente con Profilo 'ModI'");
		}

		TipoApiEnum protocollo = getTipoApi(asps, env);
		FruizioneConf fruizioneConf = getFruizioneConf(asps, env);
		return getModiProtocolProperties(modi, protocollo, fruizioneConf);

	}

	private static ProtocolProperties getModiProtocolProperties(OneOfErogazioneModIModi modi, TipoApiEnum protocollo, ErogazioneConf erogazioneConf) {

		if(modi == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare la configurazione 'ModI'");
		}

		ProtocolProperties p = new ProtocolProperties();

		if(protocollo.equals(TipoApiEnum.SOAP)) {
			getSOAPProperties((ErogazioneModISoap)modi, p, erogazioneConf);
		} else if(protocollo.equals(TipoApiEnum.REST)) {
			getRESTProperties((ErogazioneModIRest)modi, p, erogazioneConf);
		}


		return p;
	}

	private static ProtocolProperties getModiProtocolProperties(OneOfFruizioneModIModi modi, TipoApiEnum protocollo, FruizioneConf fruizioneConf) {

		if(modi == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare la configurazione 'ModI'");
		}

		ProtocolProperties p = new ProtocolProperties();

		if(protocollo.equals(TipoApiEnum.SOAP)) {
			getSOAPProperties((FruizioneModISoap)modi, p, fruizioneConf);
		} else if(protocollo.equals(TipoApiEnum.REST)) {
			getRESTProperties((FruizioneModIRest)modi, p, fruizioneConf);
		}


		return p;
	}

	private static ProtocolProperties getModiProtocolProperties(OneOfFruizioneModi modi, TipoApiEnum protocollo, FruizioneConf fruizioneConf,
			boolean modiRequired) {

		if(modi == null) {
			if(modiRequired) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare la configurazione 'ModI'");
			}
			else {
				return null;
			}
		}

		ProtocolProperties p = new ProtocolProperties();

		if(protocollo.equals(TipoApiEnum.SOAP)) {
			getSOAPProperties((FruizioneModISoap)modi, p, fruizioneConf);
		} else if(protocollo.equals(TipoApiEnum.REST)) {
			getRESTProperties((FruizioneModIRest)modi, p, fruizioneConf);
		}


		return p;
	}

	private static ProtocolProperties getModiProtocolProperties(OneOfErogazioneModi modi, TipoApiEnum protocollo, ErogazioneConf erogazioneConf,
			boolean modiRequired) {

		if(modi == null) {
			if(modiRequired) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare la configurazione 'ModI'");
			}
			else {
				return null;
			}
		}

		ProtocolProperties p = new ProtocolProperties();

		if(protocollo.equals(TipoApiEnum.SOAP)) {
			getSOAPProperties((ErogazioneModISoap)modi, p, erogazioneConf);
		} else if(protocollo.equals(TipoApiEnum.REST)) {
			getRESTProperties((ErogazioneModIRest)modi, p, erogazioneConf);
		}


		return p;
	}

	private static void getRESTProperties(ErogazioneModIRest modi, ProtocolProperties p, ErogazioneConf erogazioneConf) {

		if(!erogazioneConf.sicurezzaMessaggioAPIAbilitata) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Impossibile abilitare la sicurezza messaggio, deve essere abilitata nella API implementata");
		}

		// **** richiesta ****
		
		ErogazioneModIRestRichiestaSicurezzaMessaggio sicurezzaMessaggioRichiesta = modi.getRichiesta().getSicurezzaMessaggio();
		
		p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_RIFERIMENTO_X509, getX509(sicurezzaMessaggioRichiesta.getRiferimentoX509()));
		
		if(sicurezzaMessaggioRichiesta.getTruststore().getModalita().equals(StatoDefaultRidefinitoEnum.DEFAULT)) {
			
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE, ModICostanti.MODIPA_PROFILO_DEFAULT);

			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS, "");
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_OCSP_POLICY, "");
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD, "");
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH, "");
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE, "");
			
		} else {
			
			ModITrustStoreRidefinito truststoreRidefinito = (ModITrustStoreRidefinito)sicurezzaMessaggioRichiesta.getTruststore();
			
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE, ModICostanti.MODIPA_PROFILO_RIDEFINISCI);

			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS, truststoreRidefinito.getTruststoreCrl());
			if(truststoreRidefinito.getTruststoreOcspPolicy()!=null) {
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_OCSP_POLICY, truststoreRidefinito.getTruststoreOcspPolicy());
			}
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD, truststoreRidefinito.getTruststorePassword());
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH, truststoreRidefinito.getTruststorePath());
			
			String tipo = null;
			switch(truststoreRidefinito.getTruststoreTipo()) {
			case JKS:tipo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_VALUE_JKS;
				break;
			case PKCS11:
				tipo = truststoreRidefinito.getPcks11Tipo();
				if(tipo==null) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Tipo truststore pks11 non indicato");
				}
				break;
			}
			
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE, tipo);

		}

		if(sicurezzaMessaggioRichiesta.getTruststoreSsl()!=null) {
			if(sicurezzaMessaggioRichiesta.getTruststoreSsl().getModalita().equals(StatoDefaultRidefinitoEnum.DEFAULT)) {
				
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE, ModICostanti.MODIPA_PROFILO_DEFAULT);
	
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS, "");
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_OCSP_POLICY, "");
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PASSWORD, "");
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH, "");
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE, "");
				
			} else {
				
				ModITrustStoreRidefinito truststoreRidefinito = (ModITrustStoreRidefinito)sicurezzaMessaggioRichiesta.getTruststoreSsl();
				
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE, ModICostanti.MODIPA_PROFILO_RIDEFINISCI);
	
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS, truststoreRidefinito.getTruststoreCrl());
				if(truststoreRidefinito.getTruststoreOcspPolicy()!=null) {
					p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_OCSP_POLICY, truststoreRidefinito.getTruststoreOcspPolicy());
				}
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PASSWORD, truststoreRidefinito.getTruststorePassword());
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH, truststoreRidefinito.getTruststorePath());
				
				String tipo = null;
				switch(truststoreRidefinito.getTruststoreTipo()) {
				case JKS:tipo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_VALUE_JKS;
					break;
				case PKCS11:
					tipo = truststoreRidefinito.getPcks11Tipo();
					if(tipo==null) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("Tipo truststore pks11 non indicato");
					}
					break;
				}
				
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE, tipo);
	
			}
		}

		if(sicurezzaMessaggioRichiesta.getTimeToLive()!=null && sicurezzaMessaggioRichiesta.getTimeToLive()>0) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_IAT, ModICostanti.MODIPA_PROFILO_RIDEFINISCI);
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_IAT_SECONDS,  sicurezzaMessaggioRichiesta.getTimeToLive()); 
		}
		else {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_IAT, ModICostanti.MODIPA_PROFILO_DEFAULT); 
		}

		if(sicurezzaMessaggioRichiesta.getAudience()!=null) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE, sicurezzaMessaggioRichiesta.getAudience());
		} else {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE, "");
		}

		// contemporaneita'
		
		if(sicurezzaMessaggioRichiesta.getContemporaneita()!=null) {
			
			if(!erogazioneConf.sicurezzaMessaggioHeaderDuplicatiAbilitato) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'API implementata non risulta configurata per gestire la contemporaneità dei token AGID che servirebbe alle opzioni indicate per la richiesta");
			}
			
			if(sicurezzaMessaggioRichiesta.getContemporaneita().getIdentificativo()!=null) {
				switch (sicurezzaMessaggioRichiesta.getContemporaneita().getIdentificativo()) {
				case AGID:
					p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_FILTRO_DUPLICATI, 
							ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_FILTRO_DUPLICATI_VALUE_MODI);
					break;
				case BEARER:
					p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_FILTRO_DUPLICATI, 
							ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_FILTRO_DUPLICATI_VALUE_AUTHORIZATION);
					break;
				}				
			}
			if(sicurezzaMessaggioRichiesta.getContemporaneita().getAudience()!=null) {
				switch (sicurezzaMessaggioRichiesta.getContemporaneita().getAudience()) {
				case SAME:
					p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_AUDIENCE, 
							ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_AUDIENCE_VALUE_SAME);
					break;
				case DIFFERENT:
					p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_AUDIENCE, 
							ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_AUDIENCE_VALUE_DIFFERENT);
					break;
				}
				if(ModISicurezzaMessaggioRestSameDifferentEnum.DIFFERENT.equals(sicurezzaMessaggioRichiesta.getContemporaneita().getAudience()) &&
						sicurezzaMessaggioRichiesta.getContemporaneita().getAudienceAtteso()!=null) {
					p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_AUDIENCE_INTEGRITY, 
							sicurezzaMessaggioRichiesta.getContemporaneita().getAudienceAtteso());
				}
			}
		}
		
		
		// **** risposta ****

		ErogazioneModIRestRispostaSicurezzaMessaggio sicurezzaMessaggioRisposta = modi.getRisposta().getSicurezzaMessaggio();
		
		String algo = null;
		if(sicurezzaMessaggioRisposta.getAlgoritmo()!= null) {
			switch(sicurezzaMessaggioRisposta.getAlgoritmo()) {
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
		
		if(sicurezzaMessaggioRisposta.getHeaderHttpFirmare()!=null) {
			String httpHeaders = String.join(",", sicurezzaMessaggioRisposta.getHeaderHttpFirmare());
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST, httpHeaders);
		}
		else {
			// viene applicato il default
			try {
				String httpHeaders = ModIProperties.getInstance().getRestSecurityTokenSignedHeadersAsString();
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST, httpHeaders);
			}catch(Exception e) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Recupero header http da firmare per default fallito: "+e.getMessage());
			}
		}
		
		if(sicurezzaMessaggioRisposta.getRiferimentoX509() == null || sicurezzaMessaggioRisposta.getRiferimentoX509().equals(ModISicurezzaMessaggioRestRiferimentoX509Risposta.RICHIESTA)) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_AS_REQUEST, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_AS_REQUEST_VALUE_TRUE);
		} else {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_AS_REQUEST, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_AS_REQUEST_VALUE_FALSE);
			
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509, getX509(sicurezzaMessaggioRisposta.getRiferimentoX509Risposta())); 
		}
		
		p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_X5C_USE_CERTIFICATE_CHAIN, sicurezzaMessaggioRisposta.isCertificateChain() != null ?sicurezzaMessaggioRisposta.isCertificateChain() : false));
		
		if(sicurezzaMessaggioRisposta.getUrl()!=null) {
			
			if(!modi.getRichiesta().getSicurezzaMessaggio().getRiferimentoX509().contains(ModISicurezzaMessaggioRestRiferimentoX509.X5U)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Impossibile settare URL X5U con riferimento x509 "+modi.getRichiesta().getSicurezzaMessaggio().getRiferimentoX509());
			}
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_X509_VALUE_X5URL, sicurezzaMessaggioRisposta.getUrl());
		} else {
			if(modi.getRichiesta().getSicurezzaMessaggio().getRiferimentoX509().contains(ModISicurezzaMessaggioRestRiferimentoX509.X5U)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare URL X5U con riferimento x509 "+modi.getRichiesta().getSicurezzaMessaggio().getRiferimentoX509());
			}
		}

		if(sicurezzaMessaggioRisposta.getTimeToLive()!=null) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_EXPIRED, sicurezzaMessaggioRisposta.getTimeToLive());
		}
		
		if(sicurezzaMessaggioRisposta.getClaims()!=null && !sicurezzaMessaggioRisposta.getClaims().isEmpty()) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_JWT_CLAIMS, String.join("\n", sicurezzaMessaggioRisposta.getClaims()));
		}
		
		if(sicurezzaMessaggioRisposta.getKeystore().getModalita().equals(StatoDefaultRidefinitoEnum.DEFAULT)) {
			
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE, ModICostanti.MODIPA_PROFILO_DEFAULT);
			p.addProperty(ModICostanti.MODIPA_KEYSTORE_MODE, ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE);
			p.addProperty(ModICostanti.MODIPA_KEYSTORE_TYPE, ModICostanti.MODIPA_KEYSTORE_TYPE_VALUE_JKS);
			p.addProperty(ModICostanti.MODIPA_KEY_ALIAS, "");
			p.addProperty(ModICostanti.MODIPA_KEY_PASSWORD, "");
			p.addProperty(ModICostanti.MODIPA_KEYSTORE_PASSWORD, "");
			p.addProperty(ModICostanti.MODIPA_KEYSTORE_PATH, "");
			
		} else {

			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE, ModICostanti.MODIPA_PROFILO_RIDEFINISCI);

			ModIKeyStoreRidefinito keystoreRidefinito = (ModIKeyStoreRidefinito)sicurezzaMessaggioRisposta.getKeystore();
			
			if(keystoreRidefinito.getDatiKeystore().getTipologia().equals(ModIKeystoreTipologiaEnum.HSM)) {
				ModIKeyStoreHSM hsmKeystore = (ModIKeyStoreHSM)keystoreRidefinito.getDatiKeystore();
				p.addProperty(ModICostanti.MODIPA_KEYSTORE_MODE, ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_HSM);

				p.addProperty(ModICostanti.MODIPA_KEYSTORE_TYPE, hsmKeystore.getPcks11Tipo());
				p.addProperty(ModICostanti.MODIPA_KEY_ALIAS, hsmKeystore.getKeyAlias());

			}
			else if(keystoreRidefinito.getDatiKeystore().getTipologia().equals(ModIKeystoreTipologiaEnum.FILESYSTEM)) {
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
		
		// contemporaneita'
		
		if(sicurezzaMessaggioRisposta.getContemporaneita()!=null) {
			
			if(!erogazioneConf.sicurezzaMessaggioHeaderDuplicatiAbilitato) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'API implementata non risulta configurata per gestire la contemporaneità dei token AGID che servirebbe alle opzioni indicate per la risposta");
			}
			
			if(sicurezzaMessaggioRisposta.getContemporaneita().getIdentificativo()!=null) {
				switch (sicurezzaMessaggioRisposta.getContemporaneita().getIdentificativo()) {
				case SAME:
					p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_JTI, 
							ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_AUDIENCE_VALUE_SAME);
					break;
				case DIFFERENT:
					p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_JTI, 
							ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_AUDIENCE_VALUE_DIFFERENT);
					break;
				}
				if(ModISicurezzaMessaggioRestSameDifferentEnum.DIFFERENT.equals(sicurezzaMessaggioRisposta.getContemporaneita().getIdentificativo()) &&
						sicurezzaMessaggioRisposta.getContemporaneita().getUsaComeIdMessaggio()!=null) {
					switch (sicurezzaMessaggioRisposta.getContemporaneita().getUsaComeIdMessaggio()) {
					case AGID:
						p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO, 
								ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_VALUE_MODI);
						break;
					case BEARER:
						p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO, 
								ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_VALUE_AUTHORIZATION);
						break;
					}	
				}
			}
			
			if(sicurezzaMessaggioRisposta.getContemporaneita().getClaimsBearer()!=null && !sicurezzaMessaggioRisposta.getContemporaneita().getClaimsBearer().isEmpty()) {
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION, 
						String.join("\n", sicurezzaMessaggioRisposta.getContemporaneita().getClaimsBearer()));
			}
			if(sicurezzaMessaggioRisposta.getContemporaneita().getClaimsAgid()!=null && !sicurezzaMessaggioRisposta.getContemporaneita().getClaimsAgid().isEmpty()) {
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_JWT_CLAIMS_MODI, 
						String.join("\n", sicurezzaMessaggioRisposta.getContemporaneita().getClaimsAgid()));
			}
		}
	}

	private static void getRESTProperties(FruizioneModIRest modi, ProtocolProperties p, FruizioneConf fruizioneConf) {

		if(!fruizioneConf.sicurezzaMessaggioAPIAbilitata) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Impossibile abilitare la sicurezza messaggio, deve essere abilitata nella API implementata");
		}

		// **** richiesta ****
		
		FruizioneModIRestRichiestaSicurezzaMessaggio sicurezzaMessaggioRichiesta = modi.getRichiesta().getSicurezzaMessaggio();
		
		String algo = null;
		if(sicurezzaMessaggioRichiesta.getAlgoritmo()!= null) {
			switch(sicurezzaMessaggioRichiesta.getAlgoritmo()) {
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
		p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_ALG, algo);
		
		if(sicurezzaMessaggioRichiesta.getHeaderHttpFirmare()!=null) {
			String httpHeaders = String.join(",", sicurezzaMessaggioRichiesta.getHeaderHttpFirmare());
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST, httpHeaders);
		}
		else {
			// viene applicato il default
			try {
				String httpHeaders = ModIProperties.getInstance().getRestSecurityTokenSignedHeadersAsString();
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST, httpHeaders);
			}catch(Exception e) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Recupero header http da firmare per default fallito: "+e.getMessage());
			}
		}
		
		p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_RIFERIMENTO_X509, getX509(sicurezzaMessaggioRichiesta.getRiferimentoX509())); 

		p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_RIFERIMENTO_X509_X5C_USE_CERTIFICATE_CHAIN, sicurezzaMessaggioRichiesta.isCertificateChain() != null ?sicurezzaMessaggioRichiesta.isCertificateChain() : false));
	
		if(sicurezzaMessaggioRichiesta.getTimeToLive()!=null) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_EXPIRED, sicurezzaMessaggioRichiesta.getTimeToLive());
		}
		
		if(sicurezzaMessaggioRichiesta.getAudience()!=null) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE, sicurezzaMessaggioRichiesta.getAudience());
		} else {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE, "");
		}

		if(sicurezzaMessaggioRichiesta.getClaims()!=null && !sicurezzaMessaggioRichiesta.getClaims().isEmpty()) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_JWT_CLAIMS, String.join("\n", sicurezzaMessaggioRichiesta.getClaims()));
		}
		

		// informazione utente
		
		addInformazioniUtente(sicurezzaMessaggioRichiesta.getInformazioniUtenteCodiceEnte(), p, fruizioneConf, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_MODE, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE); 
		addInformazioniUtente(sicurezzaMessaggioRichiesta.getInformazioniUtenteUserid(), p, fruizioneConf, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_MODE, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER); 
		addInformazioniUtente(sicurezzaMessaggioRichiesta.getInformazioniUtenteIndirizzoIp(), p, fruizioneConf, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER);
		
		
		// contemporaneita'
		
		if(sicurezzaMessaggioRichiesta.getContemporaneita()!=null) {
			
			if(!fruizioneConf.sicurezzaMessaggioHeaderDuplicatiAbilitato) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'API implementata non risulta configurata per gestire la contemporaneità dei token AGID che servirebbe alle opzioni indicate per la richiesta");
			}
			
			if(sicurezzaMessaggioRichiesta.getContemporaneita().getIdentificativo()!=null) {
				switch (sicurezzaMessaggioRichiesta.getContemporaneita().getIdentificativo()) {
				case SAME:
					p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_JTI, 
							ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_AUDIENCE_VALUE_SAME);
					break;
				case DIFFERENT:
					p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_JTI, 
							ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_AUDIENCE_VALUE_DIFFERENT);
					break;
				}
				if(ModISicurezzaMessaggioRestSameDifferentEnum.DIFFERENT.equals(sicurezzaMessaggioRichiesta.getContemporaneita().getIdentificativo()) &&
						sicurezzaMessaggioRichiesta.getContemporaneita().getUsaComeIdMessaggio()!=null) {
					switch (sicurezzaMessaggioRichiesta.getContemporaneita().getUsaComeIdMessaggio()) {
					case AGID:
						p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO, 
								ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_VALUE_MODI);
						break;
					case BEARER:
						p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO, 
								ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_VALUE_AUTHORIZATION);
						break;
					}	
				}
			}
			
			if(sicurezzaMessaggioRichiesta.getContemporaneita().getAudience()!=null) {
				switch (sicurezzaMessaggioRichiesta.getContemporaneita().getAudience()) {
				case SAME:
					p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_AUDIENCE, 
							ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_AUDIENCE_VALUE_SAME);
					break;
				case DIFFERENT:
					p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_AUDIENCE, 
							ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_AUDIENCE_VALUE_DIFFERENT);
					break;
				}
				if(ModISicurezzaMessaggioRestSameDifferentEnum.DIFFERENT.equals(sicurezzaMessaggioRichiesta.getContemporaneita().getAudience()) &&
						sicurezzaMessaggioRichiesta.getContemporaneita().getAudienceAtteso()!=null) {
					p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_AUDIENCE_INTEGRITY, 
							sicurezzaMessaggioRichiesta.getContemporaneita().getAudienceAtteso());
				}
			}
			
			if(sicurezzaMessaggioRichiesta.getContemporaneita().getClaimsBearer()!=null && !sicurezzaMessaggioRichiesta.getContemporaneita().getClaimsBearer().isEmpty()) {
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION, 
						String.join("\n", sicurezzaMessaggioRichiesta.getContemporaneita().getClaimsBearer()));
			}
			if(sicurezzaMessaggioRichiesta.getContemporaneita().getClaimsAgid()!=null && !sicurezzaMessaggioRichiesta.getContemporaneita().getClaimsAgid().isEmpty()) {
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_JWT_CLAIMS_MODI, 
						String.join("\n", sicurezzaMessaggioRichiesta.getContemporaneita().getClaimsAgid()));
			}
		}
				
		
		// **** risposta ****
		
		FruizioneModIRestRispostaSicurezzaMessaggio sicurezzaMessaggioRisposta = modi.getRisposta().getSicurezzaMessaggio();
		
		if(sicurezzaMessaggioRisposta.getRiferimentoX509() == null || sicurezzaMessaggioRisposta.getRiferimentoX509().equals(ModISicurezzaMessaggioRestRiferimentoX509Risposta.RICHIESTA)) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_AS_REQUEST, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_AS_REQUEST_VALUE_TRUE);
		} else {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_AS_REQUEST, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_AS_REQUEST_VALUE_FALSE);
			
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509, getX509(sicurezzaMessaggioRisposta.getRiferimentoX509Risposta())); 
		}

		if(sicurezzaMessaggioRisposta.getTruststore().getModalita().equals(StatoDefaultRidefinitoEnum.DEFAULT)) {
			
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE, ModICostanti.MODIPA_PROFILO_DEFAULT);

			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS, "");
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_OCSP_POLICY, "");
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD, "");
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH, "");
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE, "");
			
		} else {
			
			ModITrustStoreRidefinito truststoreRidefinito = (ModITrustStoreRidefinito)sicurezzaMessaggioRisposta.getTruststore();
			
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE, ModICostanti.MODIPA_PROFILO_RIDEFINISCI);

			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS, truststoreRidefinito.getTruststoreCrl());
			if(truststoreRidefinito.getTruststoreOcspPolicy()!=null) {
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_OCSP_POLICY, truststoreRidefinito.getTruststoreOcspPolicy());
			}
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD, truststoreRidefinito.getTruststorePassword());
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH, truststoreRidefinito.getTruststorePath());
			
			String tipo = null;
			switch(truststoreRidefinito.getTruststoreTipo()) {
			case JKS:tipo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_VALUE_JKS;
				break;
			case PKCS11:
				tipo = truststoreRidefinito.getPcks11Tipo();
				if(tipo==null) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Tipo truststore pks11 non indicato");
				}
				break;
			}
			
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE, tipo);

		}

		if(sicurezzaMessaggioRisposta.getTruststoreSsl()!=null) {
			if(sicurezzaMessaggioRisposta.getTruststoreSsl().getModalita().equals(StatoDefaultRidefinitoEnum.DEFAULT)) {
				
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE, ModICostanti.MODIPA_PROFILO_DEFAULT);
	
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS, "");
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_OCSP_POLICY, "");
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PASSWORD, "");
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH, "");
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE, "");
				
			} else {
				
				ModITrustStoreRidefinito truststoreRidefinito = (ModITrustStoreRidefinito)sicurezzaMessaggioRisposta.getTruststoreSsl();
				
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE, ModICostanti.MODIPA_PROFILO_RIDEFINISCI);
	
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS, truststoreRidefinito.getTruststoreCrl());
				if(truststoreRidefinito.getTruststoreOcspPolicy()!=null) {
					p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_OCSP_POLICY, truststoreRidefinito.getTruststoreOcspPolicy());
				}
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PASSWORD, truststoreRidefinito.getTruststorePassword());
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH, truststoreRidefinito.getTruststorePath());
				
				String tipo = null;
				switch(truststoreRidefinito.getTruststoreTipo()) {
				case JKS:tipo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_VALUE_JKS;
					break;
				case PKCS11:
					tipo = truststoreRidefinito.getPcks11Tipo();
					if(tipo==null) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("Tipo truststore pks11 non indicato");
					}
					break;
				}
				
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE, tipo);
	
			}
		}
		
		if(sicurezzaMessaggioRisposta.getTimeToLive()!=null && sicurezzaMessaggioRisposta.getTimeToLive()>0) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_IAT, ModICostanti.MODIPA_PROFILO_RIDEFINISCI);
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_IAT_SECONDS,  sicurezzaMessaggioRisposta.getTimeToLive()); 
		}
		else {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_IAT, ModICostanti.MODIPA_PROFILO_DEFAULT); 
		}

		p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE, sicurezzaMessaggioRisposta.isVerificaAudience()!=null ? sicurezzaMessaggioRisposta.isVerificaAudience() : false)); 
		if(sicurezzaMessaggioRisposta.isVerificaAudience()!=null && sicurezzaMessaggioRisposta.isVerificaAudience() && sicurezzaMessaggioRisposta.getAudienceAtteso()!=null) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE_VALORE, sicurezzaMessaggioRisposta.getAudienceAtteso()); 
		}
		
		// contemporaneita'
		
		if(sicurezzaMessaggioRisposta.getContemporaneita()!=null) {
			
			if(!fruizioneConf.sicurezzaMessaggioHeaderDuplicatiAbilitato) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'API implementata non risulta configurata per gestire la contemporaneità dei token AGID che servirebbe alle opzioni indicate per la risposta");
			}
			
			if(sicurezzaMessaggioRisposta.getContemporaneita().getIdentificativo()!=null) {
				switch (sicurezzaMessaggioRisposta.getContemporaneita().getIdentificativo()) {
				case AGID:
					p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_FILTRO_DUPLICATI, 
							ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_FILTRO_DUPLICATI_VALUE_MODI);
					break;
				case BEARER:
					p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_FILTRO_DUPLICATI, 
							ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_FILTRO_DUPLICATI_VALUE_AUTHORIZATION);
					break;
				}				
			}
			if(sicurezzaMessaggioRisposta.getContemporaneita().getAudience()!=null) {
				switch (sicurezzaMessaggioRisposta.getContemporaneita().getAudience()) {
				case SAME:
					p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_AUDIENCE, 
							ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_AUDIENCE_VALUE_SAME);
					break;
				case DIFFERENT:
					p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_AUDIENCE, 
							ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_AUDIENCE_VALUE_DIFFERENT);
					break;
				}
				if(ModISicurezzaMessaggioRestSameDifferentEnum.DIFFERENT.equals(sicurezzaMessaggioRisposta.getContemporaneita().getAudience()) &&
						sicurezzaMessaggioRisposta.getContemporaneita().getAudienceAtteso()!=null) {
					p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_AUDIENCE_INTEGRITY, 
							sicurezzaMessaggioRisposta.getContemporaneita().getAudienceAtteso());
				}
			}
		}
	}

	private static void addInformazioniUtente(FruizioneModISoapRichiestaInformazioneUtente informazioniUtenteCodiceEnte, ProtocolProperties p, FruizioneConf fruizioneConf, String modalitaString, String valueString) {
		if(informazioniUtenteCodiceEnte!= null) {
			if(fruizioneConf.informazioniUtenteAbilitato) {

				if(informazioniUtenteCodiceEnte.getModalita().equals(StatoDefaultRidefinitoEnum.DEFAULT)) {
					p.addProperty(modalitaString, ModICostanti.MODIPA_PROFILO_DEFAULT);
					p.addProperty(valueString, "");
				} else {
					p.addProperty(modalitaString, ModICostanti.MODIPA_PROFILO_RIDEFINISCI);
					p.addProperty(valueString, informazioniUtenteCodiceEnte.getValore());
				}
			} else {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Impossibile settare info utente");
			}
		}
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

	private static void getSOAPProperties(ErogazioneModISoap modi, ProtocolProperties p, ErogazioneConf erogazioneConf) {

		if(!erogazioneConf.sicurezzaMessaggioAPIAbilitata) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Impossibile abilitare la sicurezza messaggio, deve essere abilitata nella API implementata");
		}
		
		// **** richiesta ****
		
		ErogazioneModISoapRichiestaSicurezzaMessaggio sicurezzaMessaggioRichiesta = modi.getRichiesta().getSicurezzaMessaggio();
		
		if(sicurezzaMessaggioRichiesta.getTruststore().getModalita().equals(StatoDefaultRidefinitoEnum.DEFAULT)) {
			
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE, ModICostanti.MODIPA_PROFILO_DEFAULT);

			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS, "");
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_OCSP_POLICY, "");
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD, "");
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH, "");
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE, "");
			
		} else {
			
			ModITrustStoreRidefinito truststoreRidefinito = (ModITrustStoreRidefinito)sicurezzaMessaggioRichiesta.getTruststore();
			
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE, ModICostanti.MODIPA_PROFILO_RIDEFINISCI);

			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS, truststoreRidefinito.getTruststoreCrl());
			if(truststoreRidefinito.getTruststoreOcspPolicy()!=null) {
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_OCSP_POLICY, truststoreRidefinito.getTruststoreOcspPolicy());
			}
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD, truststoreRidefinito.getTruststorePassword());
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH, truststoreRidefinito.getTruststorePath());
			
			String tipo = null;
			switch(truststoreRidefinito.getTruststoreTipo()) {
			case JKS:tipo = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_VALUE_JKS;
				break;
			case PKCS11:
				tipo = truststoreRidefinito.getPcks11Tipo();
				if(tipo==null) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Tipo truststore pks11 non indicato");
				}
				break;
			}
			
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE, tipo);

		}

		if(sicurezzaMessaggioRichiesta.getTimeToLive()!=null && sicurezzaMessaggioRichiesta.getTimeToLive()>0) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_IAT, ModICostanti.MODIPA_PROFILO_RIDEFINISCI);
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_IAT_SECONDS,  sicurezzaMessaggioRichiesta.getTimeToLive()); 
		}
		else {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_IAT, ModICostanti.MODIPA_PROFILO_DEFAULT); 
		}
		
		if(sicurezzaMessaggioRichiesta.getWsaTo()!=null) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE, sicurezzaMessaggioRichiesta.getWsaTo());
		} else {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE, "");
		}

		
		// **** risposta ****
		
		ErogazioneModISoapRispostaSicurezzaMessaggio sicurezzaMessaggioRisposta = modi.getRisposta().getSicurezzaMessaggio();
		
		String algo = null;
		if(sicurezzaMessaggioRisposta.getAlgoritmo()!= null) {
			switch(sicurezzaMessaggioRisposta.getAlgoritmo()) {
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
		if(sicurezzaMessaggioRisposta.getFormaCanonicaXml()!=null) {
			switch(sicurezzaMessaggioRisposta.getFormaCanonicaXml()) {
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

		if(sicurezzaMessaggioRisposta.getHeaderSoapFirmare()!=null) {
			String soapHeaders = String.join("\n", sicurezzaMessaggioRisposta.getHeaderSoapFirmare());
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_SOAP, soapHeaders);
		}
		
		String rif509 = null;
		if(sicurezzaMessaggioRisposta.getRiferimentoX509()!= null) {
			switch(sicurezzaMessaggioRisposta.getRiferimentoX509()) {
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
		
		p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_INCLUDE_SIGNATURE_TOKEN, sicurezzaMessaggioRisposta.isIncludiSignatureToken() != null ? sicurezzaMessaggioRisposta.isIncludiSignatureToken() : false ));
		p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_USE_CERTIFICATE_CHAIN, sicurezzaMessaggioRisposta.isCertificateChain() != null ?sicurezzaMessaggioRisposta.isCertificateChain() : false));

		if(sicurezzaMessaggioRisposta.getTimeToLive()!=null) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_EXPIRED, sicurezzaMessaggioRisposta.getTimeToLive());
		} else {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_EXPIRED, 300);
		}
		
		if(sicurezzaMessaggioRisposta.getKeystore().getModalita().equals(StatoDefaultRidefinitoEnum.DEFAULT)) {
			
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE, ModICostanti.MODIPA_PROFILO_DEFAULT);
			p.addProperty(ModICostanti.MODIPA_KEYSTORE_MODE, ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE);
			p.addProperty(ModICostanti.MODIPA_KEYSTORE_TYPE, ModICostanti.MODIPA_KEYSTORE_TYPE_VALUE_JKS);
			p.addProperty(ModICostanti.MODIPA_KEY_ALIAS, "");
			p.addProperty(ModICostanti.MODIPA_KEY_PASSWORD, "");
			p.addProperty(ModICostanti.MODIPA_KEYSTORE_PASSWORD, "");
			p.addProperty(ModICostanti.MODIPA_KEYSTORE_PATH, "");
			
		} else {

			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE, ModICostanti.MODIPA_PROFILO_RIDEFINISCI);

			ModIKeyStoreRidefinito keystoreRidefinito = (ModIKeyStoreRidefinito)sicurezzaMessaggioRisposta.getKeystore();
			
			if(keystoreRidefinito.getDatiKeystore().getTipologia().equals(ModIKeystoreTipologiaEnum.HSM)) {
				ModIKeyStoreHSM hsmKeystore = (ModIKeyStoreHSM)keystoreRidefinito.getDatiKeystore();
				p.addProperty(ModICostanti.MODIPA_KEYSTORE_MODE, ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_HSM);

				p.addProperty(ModICostanti.MODIPA_KEYSTORE_TYPE, hsmKeystore.getPcks11Tipo());
				p.addProperty(ModICostanti.MODIPA_KEY_ALIAS, hsmKeystore.getKeyAlias());

			}
			else if(keystoreRidefinito.getDatiKeystore().getTipologia().equals(ModIKeystoreTipologiaEnum.FILESYSTEM)) {
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

	}

	private static void getSOAPProperties(FruizioneModISoap modi, ProtocolProperties p, FruizioneConf fruizioneConf) {

		if(!fruizioneConf.sicurezzaMessaggioAPIAbilitata) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Impossibile abilitare la sicurezza messaggio, deve essere abilitata nella API implementata");
		}
		
		// **** richiesta ****
		
		FruizioneModISoapRichiestaSicurezzaMessaggio sicurezzaMessaggioRichiesta = modi.getRichiesta().getSicurezzaMessaggio();
		
		String algo = null;
		if(sicurezzaMessaggioRichiesta.getAlgoritmo()!= null) {
			switch(sicurezzaMessaggioRichiesta.getAlgoritmo()) {
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
		p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_ALG, algo);

		String canonic = null;
		if(sicurezzaMessaggioRichiesta.getFormaCanonicaXml()!=null) {
			switch(sicurezzaMessaggioRichiesta.getFormaCanonicaXml()) {
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
		p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_CANONICALIZATION_ALG, canonic);
		
		if(sicurezzaMessaggioRichiesta.getHeaderSoapFirmare()!=null) {
			String soapHeaders = String.join("\n", sicurezzaMessaggioRichiesta.getHeaderSoapFirmare());
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_SOAP, soapHeaders);
		}
		
		String rif509 = null;
		if(sicurezzaMessaggioRichiesta.getRiferimentoX509()!= null) {
			switch(sicurezzaMessaggioRichiesta.getRiferimentoX509()) {
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
		p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_RIFERIMENTO_X509, rif509);

		p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_INCLUDE_SIGNATURE_TOKEN, sicurezzaMessaggioRichiesta.isIncludiSignatureToken() != null ? sicurezzaMessaggioRichiesta.isIncludiSignatureToken() : false ));
		p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_USE_CERTIFICATE_CHAIN, sicurezzaMessaggioRichiesta.isCertificateChain() != null ?sicurezzaMessaggioRichiesta.isCertificateChain() : false));
		
		if(sicurezzaMessaggioRichiesta.getTimeToLive()!=null) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_EXPIRED, sicurezzaMessaggioRichiesta.getTimeToLive());
		}
				
		if(sicurezzaMessaggioRichiesta.getWsaTo()!=null) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE, sicurezzaMessaggioRichiesta.getWsaTo());
		} else {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE, "");
		}

		// informazione utente

		addInformazioniUtente(sicurezzaMessaggioRichiesta.getInformazioniUtenteCodiceEnte(), p, fruizioneConf, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_MODE, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE); 
		addInformazioniUtente(sicurezzaMessaggioRichiesta.getInformazioniUtenteUserid(), p, fruizioneConf, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_MODE, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER); 
		addInformazioniUtente(sicurezzaMessaggioRichiesta.getInformazioniUtenteIndirizzoIp(), p, fruizioneConf, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE, ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER);
		
		
		// **** risposta ****
		
		FruizioneModISoapRispostaSicurezzaMessaggio sicurezzaMessaggioRisposta = modi.getRisposta().getSicurezzaMessaggio();
		
		if(sicurezzaMessaggioRisposta.getTruststore().getModalita().equals(StatoDefaultRidefinitoEnum.DEFAULT)) {
			
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE, ModICostanti.MODIPA_PROFILO_DEFAULT);
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE, ModICostanti.MODIPA_KEYSTORE_TYPE_VALUE_JKS);
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD, "");
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH, "");
			
		} else {

			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE, ModICostanti.MODIPA_PROFILO_RIDEFINISCI);

			ModITrustStoreRidefinito truststoreRidefinito = (ModITrustStoreRidefinito)sicurezzaMessaggioRisposta.getTruststore();
			
			String tipo = null;
			switch(truststoreRidefinito.getTruststoreTipo()) {
			case JKS:tipo = ModICostanti.MODIPA_KEYSTORE_TYPE_VALUE_JKS;
				break;
			case PKCS11:
				tipo = truststoreRidefinito.getPcks11Tipo();
				if(tipo==null) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Tipo truststore pks11 non indicato");
				}
				break;
			}
			
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE, tipo);
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD, truststoreRidefinito.getTruststorePassword());
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH, truststoreRidefinito.getTruststorePath());
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS, truststoreRidefinito.getTruststoreCrl());
			if(truststoreRidefinito.getTruststoreOcspPolicy()!=null) {
				p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_OCSP_POLICY, truststoreRidefinito.getTruststoreOcspPolicy());
			}
			
		}

		if(sicurezzaMessaggioRisposta.getTimeToLive()!=null && sicurezzaMessaggioRisposta.getTimeToLive()>0) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_IAT, ModICostanti.MODIPA_PROFILO_RIDEFINISCI);
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_IAT_SECONDS,  sicurezzaMessaggioRisposta.getTimeToLive()); 
		}
		else {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_IAT, ModICostanti.MODIPA_PROFILO_DEFAULT); 
		}

		p.addProperty(ProtocolPropertiesFactory.newProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE, sicurezzaMessaggioRisposta.isVerificaWsaTo()!=null ? sicurezzaMessaggioRisposta.isVerificaWsaTo() : false)); 
		if(sicurezzaMessaggioRisposta.isVerificaWsaTo()!=null && sicurezzaMessaggioRisposta.isVerificaWsaTo() && sicurezzaMessaggioRisposta.getAudienceAtteso()!=null) {
			p.addProperty(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE_VALORE, sicurezzaMessaggioRisposta.getAudienceAtteso()); 
		}
	}

}
