/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.security.message.utils;

import java.security.KeyStore;

import org.openspcoop2.security.keystore.MerlinKeystore;
import org.openspcoop2.security.keystore.MerlinTruststore;
import org.openspcoop2.security.keystore.MultiKeystore;
import org.openspcoop2.security.keystore.SymmetricKeystore;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.constants.SecurityConstants;

/**
 * KeystoreUtils
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class KeystoreUtils {

	public static EncryptionBean getSenderEncryptionBean(MessageSecurityContext messageSecurityContext) throws Exception {
		
		KeyStore encryptionKS = null;
		KeyStore encryptionTrustStoreKS = null;
		boolean encryptionSymmetric = false;
		String aliasEncryptUser = null;
		String aliasEncryptPassword = null;
		// Alias Chiave/Certificato per Encrypt
		aliasEncryptUser = (String) messageSecurityContext.getOutgoingProperties().get(SecurityConstants.ENCRYPTION_USER);
		if(aliasEncryptUser==null){
			aliasEncryptUser = (String) messageSecurityContext.getOutgoingProperties().get(SecurityConstants.USER);
			if(aliasEncryptUser==null){
				throw new Exception(SecurityConstants.ENCRYPTION_USER+"/"+SecurityConstants.USER+" non fornita");
			}
		}

		// Leggo Modalita' di encrypt (Symmetric/Asymmetric)
		Object encryptionSymmetricObject = messageSecurityContext.getOutgoingProperties().get(SecurityConstants.ENCRYPTION_SYMMETRIC);
		if(encryptionSymmetricObject!=null){
			encryptionSymmetric = SecurityConstants.TRUE.equals(encryptionSymmetricObject);
		}
		// 0. TrustStore
		String encryptionTrustStore = (String) messageSecurityContext.getOutgoingProperties().get(SecurityConstants.ENCRYPTION_TRUSTSTORE_PROPERTY_FILE);
		// 1. Property Merlin
		String encryptionStore = (String) messageSecurityContext.getOutgoingProperties().get(SecurityConstants.ENCRYPTION_PROPERTY_FILE);
		// 2. Multi Property
		String multiEncryptionStore = (String) messageSecurityContext.getOutgoingProperties().get(SecurityConstants.ENCRYPTION_MULTI_PROPERTY_FILE);
		// 3. In caso di chiave simmetrica provo a vedere se e' stata fornita direttamente una chiave
		String encryptionSymmetricKeyValue = null;
		if(encryptionSymmetric){
			encryptionSymmetricKeyValue = (String) messageSecurityContext.getOutgoingProperties().get(SecurityConstants.ENCRYPTION_SYMMETRIC_KEY_VALUE);
		}
			
		// Istanzione truststore
		// 0. TrustStore
		if(encryptionTrustStore!=null){
			MerlinTruststore merlinTruststore = GestoreKeystoreCache.getMerlinTruststore(encryptionTrustStore);
			encryptionTrustStoreKS = merlinTruststore.getTrustStore();
		}

		// Istanzio keystore
		// 1. Property Merlin
		if(encryptionStore!=null){
			aliasEncryptPassword = (String) messageSecurityContext.getOutgoingProperties().get(SecurityConstants.ENCRYPTION_PASSWORD);
			if(aliasEncryptPassword==null){
				throw new Exception(SecurityConstants.ENCRYPTION_PASSWORD+" non fornita");
			}
			MerlinKeystore merlinKeystore = GestoreKeystoreCache.getMerlinKeystore(encryptionStore, aliasEncryptPassword);
			encryptionKS = merlinKeystore.getKeyStore();
		}
		// 2. Multi Property
		else if(multiEncryptionStore!=null){
			MultiKeystore multiKeystore = GestoreKeystoreCache.getMultiKeystore(multiEncryptionStore);
			if(SecurityConstants.MULTI_USER_KEYWORD_FRUITORE.equals(aliasEncryptUser) && 
					messageSecurityContext.getIdFruitore()!=null && messageSecurityContext.getIdFruitore().getNome()!=null){
				String fruitore = messageSecurityContext.getIdFruitore().getNome();
				encryptionKS = multiKeystore.getKeyStore(fruitore);
				aliasEncryptUser = multiKeystore.getKeyAlias(fruitore);
				aliasEncryptPassword = multiKeystore.getKeyPassword(fruitore);
			}
			else if(SecurityConstants.MULTI_USER_KEYWORD_EROGATORE.equals(aliasEncryptUser) &&
					messageSecurityContext.getIdServizio()!=null && 
					messageSecurityContext.getIdServizio().getSoggettoErogatore()!=null &&
					messageSecurityContext.getIdServizio().getSoggettoErogatore().getNome()!=null){
				String erogatore = messageSecurityContext.getIdServizio().getSoggettoErogatore().getNome();
				encryptionKS = multiKeystore.getKeyStore(erogatore);
				aliasEncryptUser = multiKeystore.getKeyAlias(erogatore);
				aliasEncryptPassword = multiKeystore.getKeyPassword(erogatore);
			}
			else if(SecurityConstants.MULTI_USER_KEYWORD_FRUITORE_EROGATORE.equals(aliasEncryptUser) &&
					messageSecurityContext.getIdFruitore()!=null && messageSecurityContext.getIdFruitore().getNome()!=null &&
					messageSecurityContext.getIdServizio()!=null && 
					messageSecurityContext.getIdServizio().getSoggettoErogatore()!=null &&
					messageSecurityContext.getIdServizio().getSoggettoErogatore().getNome()!=null){
				String fruitore = messageSecurityContext.getIdFruitore().getNome();
				String erogatore = messageSecurityContext.getIdServizio().getSoggettoErogatore().getNome();
				String aliasFR_ER = fruitore+"-"+erogatore;
				String aliasER_FR = erogatore+"-"+fruitore;
				if(multiKeystore.existsAlias(aliasFR_ER)){
					encryptionKS = multiKeystore.getKeyStore(aliasFR_ER);
					aliasEncryptUser = multiKeystore.getKeyAlias(aliasFR_ER);
					aliasEncryptPassword = multiKeystore.getKeyPassword(aliasFR_ER);
				}
				else if(multiKeystore.existsAlias(aliasER_FR)){
					encryptionKS = multiKeystore.getKeyStore(aliasER_FR);
					aliasEncryptUser = multiKeystore.getKeyAlias(aliasER_FR);
					aliasEncryptPassword = multiKeystore.getKeyPassword(aliasER_FR);
				}
				else{
					throw new Exception("Alias ["+aliasFR_ER+"] o ["+aliasER_FR+"] non trovato nella configurazione MultiKeystore");
				}
			}
			else if(SecurityConstants.MULTI_USER_KEYWORD_PORTA_DOMINIO_FRUITORE.equals(aliasEncryptUser) && 
					messageSecurityContext.getPddFruitore()!=null){
				String fruitore = messageSecurityContext.getPddFruitore();
				encryptionKS = multiKeystore.getKeyStore(fruitore);
				aliasEncryptUser = multiKeystore.getKeyAlias(fruitore);
				aliasEncryptPassword = multiKeystore.getKeyPassword(fruitore);
			}
			else if(SecurityConstants.MULTI_USER_KEYWORD_PORTA_DOMINIO_EROGATORE.equals(aliasEncryptUser) &&
					messageSecurityContext.getPddErogatore()!=null){
				String erogatore = messageSecurityContext.getPddErogatore();
				encryptionKS = multiKeystore.getKeyStore(erogatore);
				aliasEncryptUser = multiKeystore.getKeyAlias(erogatore);
				aliasEncryptPassword = multiKeystore.getKeyPassword(erogatore);
			}
			else if(SecurityConstants.MULTI_USER_KEYWORD_IDENTIFICATIVO_PORTA_FRUITORE.equals(aliasEncryptUser) && 
					messageSecurityContext.getIdFruitore()!=null && messageSecurityContext.getIdFruitore().getCodicePorta()!=null){
				String fruitore = messageSecurityContext.getIdFruitore().getCodicePorta();
				encryptionKS = multiKeystore.getKeyStore(fruitore);
				aliasEncryptUser = multiKeystore.getKeyAlias(fruitore);
				aliasEncryptPassword = multiKeystore.getKeyPassword(fruitore);
			}
			else if(SecurityConstants.MULTI_USER_KEYWORD_IDENTIFICATIVO_PORTA_EROGATORE.equals(aliasEncryptUser) &&
					messageSecurityContext.getIdServizio()!=null && 
					messageSecurityContext.getIdServizio().getSoggettoErogatore()!=null &&
					messageSecurityContext.getIdServizio().getSoggettoErogatore().getCodicePorta()!=null){
				String erogatore = messageSecurityContext.getIdServizio().getSoggettoErogatore().getCodicePorta();
				encryptionKS = multiKeystore.getKeyStore(erogatore);
				aliasEncryptUser = multiKeystore.getKeyAlias(erogatore);
				aliasEncryptPassword = multiKeystore.getKeyPassword(erogatore);
			}
			else{
				encryptionKS = multiKeystore.getKeyStore(aliasEncryptUser);
				aliasEncryptPassword = multiKeystore.getKeyPassword(aliasEncryptUser);
				aliasEncryptUser = multiKeystore.getKeyAlias(aliasEncryptUser); // aggiorno con identita alias del keystore
			}
		}
		// 3. In caso di chiave simmetrica provo a vedere se e' stata fornita direttamente una chiave
		else if(encryptionSymmetricKeyValue!=null){
			Object encryptionSymmetricAlgoritm = messageSecurityContext.getOutgoingProperties().get(SecurityConstants.ENCRYPTION_SYMMETRIC_ALGORITHM);
			if(encryptionSymmetricAlgoritm==null){
				throw new Exception("E' stata indicata una funzionalita' di encrypt con chiave simmetrica fornita direttamente nelle proprieta' ["+
						SecurityConstants.ENCRYPTION_SYMMETRIC_KEY_VALUE+"="+encryptionSymmetricKeyValue+"], ma non e' stato indicato l'algoritmo associato tramite la proprieta' "+
						SecurityConstants.ENCRYPTION_SYMMETRIC_ALGORITHM);
			}
			SymmetricKeystore symmetricKeystore = GestoreKeystoreCache.getSymmetricKeystore(aliasEncryptUser, encryptionSymmetricKeyValue, (String)encryptionSymmetricAlgoritm);
			encryptionKS = symmetricKeystore.getKeyStore();
			aliasEncryptPassword = symmetricKeystore.getPasswordKey();
		}
		else{
			throw new Exception("Nessuna modalita' di recupero del Keystore per la funzionalita' di Encryption indicata");
		}

		EncryptionBean bean = new EncryptionBean();
		
		bean.setKeystore(encryptionKS);
		bean.setTruststore(encryptionTrustStoreKS);
		bean.setUser(aliasEncryptUser);
		bean.setPassword(aliasEncryptPassword);
		bean.setEncryptionSimmetric(encryptionSymmetric);
		
		return bean;
	}
	public static EncryptionBean getReceiverEncryptionBean(MessageSecurityContext messageSecurityContext) throws Exception {

		KeyStore decryptionKS = null;
		KeyStore decryptionTrustStoreKS = null;
		String aliasDecryptUser = null;
		String aliasDecryptPassword = null;
		boolean decryptionSymmetric = false;
		// Alias Chiave/Certificato per Encrypt
		aliasDecryptUser = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.DECRYPTION_USER);
		if(aliasDecryptUser==null){
			aliasDecryptUser = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.USER);
			if(aliasDecryptUser==null){
				throw new Exception(SecurityConstants.DECRYPTION_USER+"/"+SecurityConstants.USER+" non fornita");
			}
		}
		
		// Leggo Modalita' di encrypt (Symmetric/Asymmetric)
		Object decryptionSymmetricObject = messageSecurityContext.getIncomingProperties().get(SecurityConstants.DECRYPTION_SYMMETRIC);
		if(decryptionSymmetricObject!=null){
			decryptionSymmetric = SecurityConstants.TRUE.equals(decryptionSymmetricObject);
		}
		// 0. TrustStore
		String decryptionTrustStore = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.DECRYPTION_TRUSTSTORE_PROPERTY_FILE);
		// 1. Property Merlin
		String decryptionStore = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.DECRYPTION_PROPERTY_FILE);
		// 2. Multi Property
		String multiDecryptionStore = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.DECRYPTION_MULTI_PROPERTY_FILE);
		// 3. In caso di chiave simmetrica provo a vedere se e' stata fornita direttamente una chiave
		String decryptionSymmetricKeyValue = null;
		if(decryptionSymmetric){
			decryptionSymmetricKeyValue = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.DECRYPTION_SYMMETRIC_KEY_VALUE);
		}
		
		// Istanzione truststore
		// 0. TrustStore
		if(decryptionTrustStore!=null){
			MerlinTruststore merlinTruststore = GestoreKeystoreCache.getMerlinTruststore(decryptionTrustStore);
			decryptionTrustStoreKS = merlinTruststore.getTrustStore();
		}
		
		// Istanzio keystore
		// 1. Property Merlin
		if(decryptionStore!=null){
			aliasDecryptPassword = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.DECRYPTION_PASSWORD);
			if(aliasDecryptPassword==null){
				throw new Exception(SecurityConstants.DECRYPTION_PASSWORD+" non fornita");
			}
			MerlinKeystore merlinKeystore = GestoreKeystoreCache.getMerlinKeystore(decryptionStore, aliasDecryptPassword);
			decryptionKS = merlinKeystore.getKeyStore();
		}
		// 2. Multi Property
		else if(multiDecryptionStore!=null){
			MultiKeystore multiKeystore = GestoreKeystoreCache.getMultiKeystore(multiDecryptionStore);
			if(SecurityConstants.MULTI_USER_KEYWORD_FRUITORE.equals(aliasDecryptUser) && 
					messageSecurityContext.getIdFruitore()!=null && messageSecurityContext.getIdFruitore().getNome()!=null){
				String fruitore = messageSecurityContext.getIdFruitore().getNome();
				decryptionKS = multiKeystore.getKeyStore(fruitore);
				aliasDecryptUser = multiKeystore.getKeyAlias(fruitore);
				aliasDecryptPassword = multiKeystore.getKeyPassword(fruitore);
			}
			else if(SecurityConstants.MULTI_USER_KEYWORD_EROGATORE.equals(aliasDecryptUser) &&
					messageSecurityContext.getIdServizio()!=null && 
					messageSecurityContext.getIdServizio().getSoggettoErogatore()!=null &&
					messageSecurityContext.getIdServizio().getSoggettoErogatore().getNome()!=null){
				String erogatore = messageSecurityContext.getIdServizio().getSoggettoErogatore().getNome();
				decryptionKS = multiKeystore.getKeyStore(erogatore);
				aliasDecryptUser = multiKeystore.getKeyAlias(erogatore);
				aliasDecryptPassword = multiKeystore.getKeyPassword(erogatore);
			}
			else if(SecurityConstants.MULTI_USER_KEYWORD_FRUITORE_EROGATORE.equals(aliasDecryptUser) &&
					messageSecurityContext.getIdFruitore()!=null && messageSecurityContext.getIdFruitore().getNome()!=null &&
					messageSecurityContext.getIdServizio()!=null && 
					messageSecurityContext.getIdServizio().getSoggettoErogatore()!=null &&
					messageSecurityContext.getIdServizio().getSoggettoErogatore().getNome()!=null){
				String fruitore = messageSecurityContext.getIdFruitore().getNome();
				String erogatore = messageSecurityContext.getIdServizio().getSoggettoErogatore().getNome();
				String aliasFR_ER = fruitore+"-"+erogatore;
				String aliasER_FR = erogatore+"-"+fruitore;
				if(multiKeystore.existsAlias(aliasFR_ER)){
					decryptionKS = multiKeystore.getKeyStore(aliasFR_ER);
					aliasDecryptUser = multiKeystore.getKeyAlias(aliasFR_ER);
					aliasDecryptPassword = multiKeystore.getKeyPassword(aliasFR_ER);
				}
				else if(multiKeystore.existsAlias(aliasER_FR)){
					decryptionKS = multiKeystore.getKeyStore(aliasER_FR);
					aliasDecryptUser = multiKeystore.getKeyAlias(aliasER_FR);
					aliasDecryptPassword = multiKeystore.getKeyPassword(aliasER_FR);
				}
				else{
					throw new Exception("Alias ["+aliasFR_ER+"] o ["+aliasER_FR+"] non trovato nella configurazione MultiKeystore");
				}
			}
			else if(SecurityConstants.MULTI_USER_KEYWORD_PORTA_DOMINIO_FRUITORE.equals(aliasDecryptUser) && 
					messageSecurityContext.getPddFruitore()!=null){
				String fruitore = messageSecurityContext.getPddFruitore();
				decryptionKS = multiKeystore.getKeyStore(fruitore);
				aliasDecryptUser = multiKeystore.getKeyAlias(fruitore);
				aliasDecryptPassword = multiKeystore.getKeyPassword(fruitore);
			}
			else if(SecurityConstants.MULTI_USER_KEYWORD_PORTA_DOMINIO_EROGATORE.equals(aliasDecryptUser) &&
					messageSecurityContext.getPddErogatore()!=null){
				String erogatore = messageSecurityContext.getPddErogatore();
				decryptionKS = multiKeystore.getKeyStore(erogatore);
				aliasDecryptUser = multiKeystore.getKeyAlias(erogatore);
				aliasDecryptPassword = multiKeystore.getKeyPassword(erogatore);
			}
			else if(SecurityConstants.MULTI_USER_KEYWORD_IDENTIFICATIVO_PORTA_FRUITORE.equals(aliasDecryptUser) && 
					messageSecurityContext.getIdFruitore()!=null && messageSecurityContext.getIdFruitore().getCodicePorta()!=null){
				String fruitore = messageSecurityContext.getIdFruitore().getCodicePorta();
				decryptionKS = multiKeystore.getKeyStore(fruitore);
				aliasDecryptUser = multiKeystore.getKeyAlias(fruitore);
				aliasDecryptPassword = multiKeystore.getKeyPassword(fruitore);
			}
			else if(SecurityConstants.MULTI_USER_KEYWORD_IDENTIFICATIVO_PORTA_EROGATORE.equals(aliasDecryptUser) &&
					messageSecurityContext.getIdServizio()!=null && 
					messageSecurityContext.getIdServizio().getSoggettoErogatore()!=null &&
					messageSecurityContext.getIdServizio().getSoggettoErogatore().getCodicePorta()!=null){
				String erogatore = messageSecurityContext.getIdServizio().getSoggettoErogatore().getCodicePorta();
				decryptionKS = multiKeystore.getKeyStore(erogatore);
				aliasDecryptUser = multiKeystore.getKeyAlias(erogatore);
				aliasDecryptPassword = multiKeystore.getKeyPassword(erogatore);
			}
			else{
				decryptionKS = multiKeystore.getKeyStore(aliasDecryptUser);
				aliasDecryptPassword = multiKeystore.getKeyPassword(aliasDecryptUser);
				aliasDecryptUser = multiKeystore.getKeyAlias(aliasDecryptUser); // aggiorno con identita alias del keystore
			}
		}
		// 3. In caso di chiave simmetrica provo a vedere se e' stata fornita direttamente una chiave
		else if(decryptionSymmetricKeyValue!=null){
			Object decryptionSymmetricAlgoritm = messageSecurityContext.getIncomingProperties().get(SecurityConstants.DECRYPTION_SYMMETRIC_ALGORITHM);
			if(decryptionSymmetricAlgoritm==null){
				throw new Exception("E' stata indicata una funzionalita' di encrypt con chiave simmetrica fornita direttamente nelle proprieta' ["+
						SecurityConstants.DECRYPTION_SYMMETRIC_KEY_VALUE+"="+decryptionSymmetricKeyValue+"], ma non e' stato indicato l'algoritmo associato tramite la proprieta' "+

						SecurityConstants.DECRYPTION_SYMMETRIC_ALGORITHM);
			}
			SymmetricKeystore symmetricKeystore = GestoreKeystoreCache.getSymmetricKeystore(aliasDecryptUser, decryptionSymmetricKeyValue, (String)decryptionSymmetricAlgoritm);
			decryptionKS = symmetricKeystore.getKeyStore();
			aliasDecryptPassword = symmetricKeystore.getPasswordKey();
		}
		else{
			throw new Exception("Nessuna modalita' di recupero del Keystore per la funzionalita' di Encryption indicata");
		}

		EncryptionBean bean = new EncryptionBean();
		
		bean.setKeystore(decryptionKS);
		bean.setTruststore(decryptionTrustStoreKS);
		bean.setUser(aliasDecryptUser);
		bean.setPassword(aliasDecryptPassword);
		bean.setEncryptionSimmetric(decryptionSymmetric);

		return bean;
	}

	public static SignatureBean getSenderSignatureBean(MessageSecurityContext messageSecurityContext) throws Exception {

		KeyStore signatureKS = null;
		KeyStore signatureTrustStoreKS = null;
		String aliasSignatureUser = null;
		String aliasSignaturePassword = null;
		// Alias Chiave/Certificato per Signature
		aliasSignatureUser = (String) messageSecurityContext.getOutgoingProperties().get(SecurityConstants.SIGNATURE_USER);
		if(aliasSignatureUser==null){
			aliasSignatureUser = (String) messageSecurityContext.getOutgoingProperties().get(SecurityConstants.USER);
			if(aliasSignatureUser==null){
				throw new Exception(SecurityConstants.SIGNATURE_USER+"/"+SecurityConstants.USER+" non fornita");
			}
		}

		// 0. TrustStore
		String signatureTrustStore = (String) messageSecurityContext.getOutgoingProperties().get(SecurityConstants.SIGNATURE_TRUSTSTORE_PROPERTY_FILE);
		// 1. Property Merlin
		String signatureStore = (String) messageSecurityContext.getOutgoingProperties().get(SecurityConstants.SIGNATURE_PROPERTY_FILE);
		// 2. Multi Property
		String multiSignatureStore = (String) messageSecurityContext.getOutgoingProperties().get(SecurityConstants.SIGNATURE_MULTI_PROPERTY_FILE);

		// Istanzione truststore
		// 0. TrustStore
		if(signatureTrustStore!=null){
			MerlinTruststore merlinTruststore = GestoreKeystoreCache.getMerlinTruststore(signatureTrustStore);
			signatureTrustStoreKS = merlinTruststore.getTrustStore();
		}

		// Istanzio keystore
		// 1. Property Merlin
		if(signatureStore!=null){
			aliasSignaturePassword = (String) messageSecurityContext.getOutgoingProperties().get(SecurityConstants.SIGNATURE_PASSWORD);
			if(aliasSignaturePassword==null){
				throw new Exception(SecurityConstants.SIGNATURE_PASSWORD+" non fornita");
			}
			MerlinKeystore merlinKeystore = GestoreKeystoreCache.getMerlinKeystore(signatureStore, aliasSignaturePassword);
			signatureKS = merlinKeystore.getKeyStore();
		}
		// 2. Multi Property
		else if(multiSignatureStore!=null){
			MultiKeystore multiKeystore = GestoreKeystoreCache.getMultiKeystore(multiSignatureStore);
			if(SecurityConstants.MULTI_USER_KEYWORD_FRUITORE.equals(aliasSignatureUser) && 
					messageSecurityContext.getIdFruitore()!=null && messageSecurityContext.getIdFruitore().getNome()!=null){
				String fruitore = messageSecurityContext.getIdFruitore().getNome();
				signatureKS = multiKeystore.getKeyStore(fruitore);
				aliasSignatureUser = multiKeystore.getKeyAlias(fruitore);
				aliasSignaturePassword = multiKeystore.getKeyPassword(fruitore);
			}
			else if(SecurityConstants.MULTI_USER_KEYWORD_EROGATORE.equals(aliasSignatureUser) &&
					messageSecurityContext.getIdServizio()!=null && 
					messageSecurityContext.getIdServizio().getSoggettoErogatore()!=null &&
					messageSecurityContext.getIdServizio().getSoggettoErogatore().getNome()!=null){
				String erogatore = messageSecurityContext.getIdServizio().getSoggettoErogatore().getNome();
				signatureKS = multiKeystore.getKeyStore(erogatore);
				aliasSignatureUser = multiKeystore.getKeyAlias(erogatore);
				aliasSignaturePassword = multiKeystore.getKeyPassword(erogatore);
			}
			else if(SecurityConstants.MULTI_USER_KEYWORD_FRUITORE_EROGATORE.equals(aliasSignatureUser) &&
					messageSecurityContext.getIdFruitore()!=null && messageSecurityContext.getIdFruitore().getNome()!=null &&
					messageSecurityContext.getIdServizio()!=null && 
					messageSecurityContext.getIdServizio().getSoggettoErogatore()!=null &&
					messageSecurityContext.getIdServizio().getSoggettoErogatore().getNome()!=null){
				String fruitore = messageSecurityContext.getIdFruitore().getNome();
				String erogatore = messageSecurityContext.getIdServizio().getSoggettoErogatore().getNome();
				String aliasFR_ER = fruitore+"-"+erogatore;
				String aliasER_FR = erogatore+"-"+fruitore;
				if(multiKeystore.existsAlias(aliasFR_ER)){
					signatureKS = multiKeystore.getKeyStore(aliasFR_ER);
					aliasSignatureUser = multiKeystore.getKeyAlias(aliasFR_ER);
					aliasSignaturePassword = multiKeystore.getKeyPassword(aliasFR_ER);
				}
				else if(multiKeystore.existsAlias(aliasER_FR)){
					signatureKS = multiKeystore.getKeyStore(aliasER_FR);
					aliasSignatureUser = multiKeystore.getKeyAlias(aliasER_FR);
					aliasSignaturePassword = multiKeystore.getKeyPassword(aliasER_FR);
				}
				else{
					throw new Exception("Alias ["+aliasFR_ER+"] o ["+aliasER_FR+"] non trovato nella configurazione MultiKeystore");
				}
			}
			else if(SecurityConstants.MULTI_USER_KEYWORD_PORTA_DOMINIO_FRUITORE.equals(aliasSignatureUser) && 
					messageSecurityContext.getPddFruitore()!=null){
				String fruitore = messageSecurityContext.getPddFruitore();
				signatureKS = multiKeystore.getKeyStore(fruitore);
				aliasSignatureUser = multiKeystore.getKeyAlias(fruitore);
				aliasSignaturePassword = multiKeystore.getKeyPassword(fruitore);
			}
			else if(SecurityConstants.MULTI_USER_KEYWORD_PORTA_DOMINIO_EROGATORE.equals(aliasSignatureUser) &&
					messageSecurityContext.getPddErogatore()!=null){
				String erogatore = messageSecurityContext.getPddErogatore();
				signatureKS = multiKeystore.getKeyStore(erogatore);
				aliasSignatureUser = multiKeystore.getKeyAlias(erogatore);
				aliasSignaturePassword = multiKeystore.getKeyPassword(erogatore);
			}
			else if(SecurityConstants.MULTI_USER_KEYWORD_IDENTIFICATIVO_PORTA_FRUITORE.equals(aliasSignatureUser) && 
					messageSecurityContext.getIdFruitore()!=null && messageSecurityContext.getIdFruitore().getCodicePorta()!=null){
				String fruitore = messageSecurityContext.getIdFruitore().getCodicePorta();
				signatureKS = multiKeystore.getKeyStore(fruitore);
				aliasSignatureUser = multiKeystore.getKeyAlias(fruitore);
				aliasSignaturePassword = multiKeystore.getKeyPassword(fruitore);
			}
			else if(SecurityConstants.MULTI_USER_KEYWORD_IDENTIFICATIVO_PORTA_EROGATORE.equals(aliasSignatureUser) &&
					messageSecurityContext.getIdServizio()!=null && 
					messageSecurityContext.getIdServizio().getSoggettoErogatore()!=null &&
					messageSecurityContext.getIdServizio().getSoggettoErogatore().getCodicePorta()!=null){
				String erogatore = messageSecurityContext.getIdServizio().getSoggettoErogatore().getCodicePorta();
				signatureKS = multiKeystore.getKeyStore(erogatore);
				aliasSignatureUser = multiKeystore.getKeyAlias(erogatore);
				aliasSignaturePassword = multiKeystore.getKeyPassword(erogatore);
			}
			else{
				signatureKS = multiKeystore.getKeyStore(aliasSignatureUser);
				aliasSignaturePassword = multiKeystore.getKeyPassword(aliasSignatureUser);
				aliasSignatureUser = multiKeystore.getKeyAlias(aliasSignatureUser); // aggiorno con identita alias del keystore
			}
		}
		else{
			throw new Exception("Nessuna modalita' di recupero del Keystore per la funzionalita' di Signature indicata");
		}

		SignatureBean bean = new SignatureBean();
		bean.setKeystore(signatureKS);
		bean.setTruststore(signatureTrustStoreKS);
		bean.setUser(aliasSignatureUser);
		bean.setPassword(aliasSignaturePassword);
		
		return bean;

	}	
	public static SignatureBean getReceiverSignatureBean(MessageSecurityContext messageSecurityContext) throws Exception {

		KeyStore signatureKS = null;
		KeyStore signatureTrustStoreKS = null;
		String aliasSignatureUser = null;
		String aliasSignaturePassword = null;
		String crlPath = null;
		// Alias Chiave/Certificato per Signature
		aliasSignatureUser = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.SIGNATURE_USER);
		if(aliasSignatureUser==null){
			aliasSignatureUser = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.USER);
			if(aliasSignatureUser==null){
				throw new Exception(SecurityConstants.SIGNATURE_USER+"/"+SecurityConstants.USER+" non fornita");
			}
		}

		// CRL
		crlPath = (String)  messageSecurityContext.getIncomingProperties().get(SecurityConstants.SIGNATURE_CRL);

		// 0. TrustStore
		String signatureTrustStore = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.SIGNATURE_TRUSTSTORE_PROPERTY_FILE);
		// 1. Property Merlin
		String signatureStore = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.SIGNATURE_PROPERTY_FILE);
		// 2. Multi Property
		String multiSignatureStore = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.SIGNATURE_MULTI_PROPERTY_FILE);

		// Istanzione truststore
		// 0. TrustStore
		if(signatureTrustStore!=null){
			MerlinTruststore merlinTruststore = GestoreKeystoreCache.getMerlinTruststore(signatureTrustStore);
			signatureTrustStoreKS = merlinTruststore.getTrustStore();
		}

		// Istanzio keystore
		// 1. Property Merlin
		if(signatureStore!=null){
			aliasSignaturePassword = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.SIGNATURE_PASSWORD);
			if(aliasSignaturePassword==null){
				throw new Exception(SecurityConstants.SIGNATURE_PASSWORD+" non fornita");
			}
			MerlinKeystore merlinKeystore = GestoreKeystoreCache.getMerlinKeystore(signatureStore, aliasSignaturePassword);
			signatureKS = merlinKeystore.getKeyStore();
		}
		// 2. Multi Property
		else if(multiSignatureStore!=null){
			MultiKeystore multiKeystore = GestoreKeystoreCache.getMultiKeystore(multiSignatureStore);
			if(SecurityConstants.MULTI_USER_KEYWORD_FRUITORE.equals(aliasSignatureUser) && 
					messageSecurityContext.getIdFruitore()!=null && messageSecurityContext.getIdFruitore().getNome()!=null){
				String fruitore = messageSecurityContext.getIdFruitore().getNome();
				signatureKS = multiKeystore.getKeyStore(fruitore);
				aliasSignatureUser = multiKeystore.getKeyAlias(fruitore);
				aliasSignaturePassword = multiKeystore.getKeyPassword(fruitore);
			}
			else if(SecurityConstants.MULTI_USER_KEYWORD_EROGATORE.equals(aliasSignatureUser) &&
					messageSecurityContext.getIdServizio()!=null && 
					messageSecurityContext.getIdServizio().getSoggettoErogatore()!=null &&
					messageSecurityContext.getIdServizio().getSoggettoErogatore().getNome()!=null){
				String erogatore = messageSecurityContext.getIdServizio().getSoggettoErogatore().getNome();
				signatureKS = multiKeystore.getKeyStore(erogatore);
				aliasSignatureUser = multiKeystore.getKeyAlias(erogatore);
				aliasSignaturePassword = multiKeystore.getKeyPassword(erogatore);
			}
			else if(SecurityConstants.MULTI_USER_KEYWORD_FRUITORE_EROGATORE.equals(aliasSignatureUser) &&
					messageSecurityContext.getIdFruitore()!=null && messageSecurityContext.getIdFruitore().getNome()!=null &&
					messageSecurityContext.getIdServizio()!=null && 
					messageSecurityContext.getIdServizio().getSoggettoErogatore()!=null &&
					messageSecurityContext.getIdServizio().getSoggettoErogatore().getNome()!=null){
				String fruitore = messageSecurityContext.getIdFruitore().getNome();
				String erogatore = messageSecurityContext.getIdServizio().getSoggettoErogatore().getNome();
				String aliasFR_ER = fruitore+"-"+erogatore;
				String aliasER_FR = erogatore+"-"+fruitore;
				if(multiKeystore.existsAlias(aliasFR_ER)){
					signatureKS = multiKeystore.getKeyStore(aliasFR_ER);
					aliasSignatureUser = multiKeystore.getKeyAlias(aliasFR_ER);
					aliasSignaturePassword = multiKeystore.getKeyPassword(aliasFR_ER);
				}
				else if(multiKeystore.existsAlias(aliasER_FR)){
					signatureKS = multiKeystore.getKeyStore(aliasER_FR);
					aliasSignatureUser = multiKeystore.getKeyAlias(aliasER_FR);
					aliasSignaturePassword = multiKeystore.getKeyPassword(aliasER_FR);
				}
				else{
					throw new Exception("Alias ["+aliasFR_ER+"] o ["+aliasER_FR+"] non trovato nella configurazione MultiKeystore");
				}
			}
			else if(SecurityConstants.MULTI_USER_KEYWORD_PORTA_DOMINIO_FRUITORE.equals(aliasSignatureUser) && 
					messageSecurityContext.getPddFruitore()!=null){
				String fruitore = messageSecurityContext.getPddFruitore();
				signatureKS = multiKeystore.getKeyStore(fruitore);
				aliasSignatureUser = multiKeystore.getKeyAlias(fruitore);
				aliasSignaturePassword = multiKeystore.getKeyPassword(fruitore);
			}
			else if(SecurityConstants.MULTI_USER_KEYWORD_PORTA_DOMINIO_EROGATORE.equals(aliasSignatureUser) &&
					messageSecurityContext.getPddErogatore()!=null){
				String erogatore = messageSecurityContext.getPddErogatore();
				signatureKS = multiKeystore.getKeyStore(erogatore);
				aliasSignatureUser = multiKeystore.getKeyAlias(erogatore);
				aliasSignaturePassword = multiKeystore.getKeyPassword(erogatore);
			}
			else if(SecurityConstants.MULTI_USER_KEYWORD_IDENTIFICATIVO_PORTA_FRUITORE.equals(aliasSignatureUser) && 
					messageSecurityContext.getIdFruitore()!=null && messageSecurityContext.getIdFruitore().getCodicePorta()!=null){
				String fruitore = messageSecurityContext.getIdFruitore().getCodicePorta();
				signatureKS = multiKeystore.getKeyStore(fruitore);
				aliasSignatureUser = multiKeystore.getKeyAlias(fruitore);
				aliasSignaturePassword = multiKeystore.getKeyPassword(fruitore);
			}
			else if(SecurityConstants.MULTI_USER_KEYWORD_IDENTIFICATIVO_PORTA_EROGATORE.equals(aliasSignatureUser) &&
					messageSecurityContext.getIdServizio()!=null && 
					messageSecurityContext.getIdServizio().getSoggettoErogatore()!=null &&
					messageSecurityContext.getIdServizio().getSoggettoErogatore().getCodicePorta()!=null){
				String erogatore = messageSecurityContext.getIdServizio().getSoggettoErogatore().getCodicePorta();
				signatureKS = multiKeystore.getKeyStore(erogatore);
				aliasSignatureUser = multiKeystore.getKeyAlias(erogatore);
				aliasSignaturePassword = multiKeystore.getKeyPassword(erogatore);
			}
			else{
				signatureKS = multiKeystore.getKeyStore(aliasSignatureUser);
				aliasSignaturePassword = multiKeystore.getKeyPassword(aliasSignatureUser);
				aliasSignatureUser = multiKeystore.getKeyAlias(aliasSignatureUser); // aggiorno con identita alias del keystore
			}
		}
		else{
			throw new Exception("Nessuna modalita' di recupero del Keystore per la funzionalita' di Signature indicata");
		}
		
		SignatureBean bean = new SignatureBean();
		bean.setKeystore(signatureKS);
		bean.setTruststore(signatureTrustStoreKS);
		bean.setUser(aliasSignatureUser);
		bean.setPassword(aliasSignaturePassword);
		bean.setCrlPath(crlPath);
		
		return bean;

	}
//	
//	private static SignatureBean _getSignatureBean(MessageSecurityContext messageSecurityContext, Hashtable<String, Object> properties) throws Exception {
//		
//		KeyStore signatureKS = null;
//		KeyStore signatureTrustStoreKS = null;
//		String aliasSignatureUser = null;
//		String aliasSignaturePassword = null;
//		String crlPath = null;
//
//		// Alias Chiave/Certificato per Signature
//		aliasSignatureUser = (String) properties.get(SecurityConstants.SIGNATURE_USER);
//		if(aliasSignatureUser==null){
//			aliasSignatureUser = (String) properties.get(SecurityConstants.USER);
//			if(aliasSignatureUser==null){
//				throw new Exception(SecurityConstants.SIGNATURE_USER+"/"+SecurityConstants.USER+" non fornita");
//			}
//		}
//		
//		// CRL
//		Object crlPathObject = properties.get(SecurityConstants.SIGNATURE_CRL);
//		if(crlPathObject != null)
//			crlPath = (String)  properties.get(SecurityConstants.SIGNATURE_CRL);
//
//		
//		// 0. TrustStore
//		String signatureTrustStore = (String) properties.get(SecurityConstants.SIGNATURE_TRUSTSTORE_PROPERTY_FILE);
//		// 1. Property Merlin
//		String signatureStore = (String) properties.get(SecurityConstants.SIGNATURE_PROPERTY_FILE);
//		// 2. Multi Property
//		String multiSignatureStore = (String) properties.get(SecurityConstants.SIGNATURE_MULTI_PROPERTY_FILE);
//		
//		// Istanzione truststore
//		// 0. TrustStore
//		if(signatureTrustStore!=null){
//			MerlinTruststore merlinTruststore = GestoreKeystoreCache.getMerlinTruststore(signatureTrustStore);
//			signatureTrustStoreKS = merlinTruststore.getTrustStore();
//		}
//		
//		// Istanzio keystore
//		// 1. Property Merlin
//		if(signatureStore!=null){
//			aliasSignaturePassword = (String) properties.get(SecurityConstants.SIGNATURE_PASSWORD);
//			if(aliasSignaturePassword==null){
//				throw new Exception(SecurityConstants.SIGNATURE_PASSWORD+" non fornita");
//			}
//			MerlinKeystore merlinKeystore = GestoreKeystoreCache.getMerlinKeystore(signatureStore, aliasSignaturePassword);
//			signatureKS = merlinKeystore.getKeyStore();
//		}
//		// 2. Multi Property
//		else if(multiSignatureStore!=null){
//			MultiKeystore multiKeystore = GestoreKeystoreCache.getMultiKeystore(multiSignatureStore);
//			if(SecurityConstants.MULTI_USER_KEYWORD_FRUITORE.equals(aliasSignatureUser) && 
//					messageSecurityContext.getIdFruitore()!=null && messageSecurityContext.getIdFruitore().getNome()!=null){
//				String fruitore = messageSecurityContext.getIdFruitore().getNome();
//				signatureKS = multiKeystore.getKeyStore(fruitore);
//				aliasSignatureUser = multiKeystore.getKeyAlias(fruitore);
//				aliasSignaturePassword = multiKeystore.getKeyPassword(fruitore);
//			}
//			else if(SecurityConstants.MULTI_USER_KEYWORD_EROGATORE.equals(aliasSignatureUser) &&
//					messageSecurityContext.getIdServizio()!=null && 
//					messageSecurityContext.getIdServizio().getSoggettoErogatore()!=null &&
//					messageSecurityContext.getIdServizio().getSoggettoErogatore().getNome()!=null){
//				String erogatore = messageSecurityContext.getIdServizio().getSoggettoErogatore().getNome();
//				signatureKS = multiKeystore.getKeyStore(erogatore);
//				aliasSignatureUser = multiKeystore.getKeyAlias(erogatore);
//				aliasSignaturePassword = multiKeystore.getKeyPassword(erogatore);
//			}
//			else if(SecurityConstants.MULTI_USER_KEYWORD_FRUITORE_EROGATORE.equals(aliasSignatureUser) &&
//					messageSecurityContext.getIdFruitore()!=null && messageSecurityContext.getIdFruitore().getNome()!=null &&
//					messageSecurityContext.getIdServizio()!=null && 
//					messageSecurityContext.getIdServizio().getSoggettoErogatore()!=null &&
//					messageSecurityContext.getIdServizio().getSoggettoErogatore().getNome()!=null){
//				String fruitore = messageSecurityContext.getIdFruitore().getNome();
//				String erogatore = messageSecurityContext.getIdServizio().getSoggettoErogatore().getNome();
//				String aliasFR_ER = fruitore+"-"+erogatore;
//				String aliasER_FR = erogatore+"-"+fruitore;
//				if(multiKeystore.existsAlias(aliasFR_ER)){
//					signatureKS = multiKeystore.getKeyStore(aliasFR_ER);
//					aliasSignatureUser = multiKeystore.getKeyAlias(aliasFR_ER);
//					aliasSignaturePassword = multiKeystore.getKeyPassword(aliasFR_ER);
//				}
//				else if(multiKeystore.existsAlias(aliasER_FR)){
//					signatureKS = multiKeystore.getKeyStore(aliasER_FR);
//					aliasSignatureUser = multiKeystore.getKeyAlias(aliasER_FR);
//					aliasSignaturePassword = multiKeystore.getKeyPassword(aliasER_FR);
//				}
//				else{
//					throw new Exception("Alias ["+aliasFR_ER+"] o ["+aliasER_FR+"] non trovato nella configurazione MultiKeystore");
//				}
//			}
//			else if(SecurityConstants.MULTI_USER_KEYWORD_PORTA_DOMINIO_FRUITORE.equals(aliasSignatureUser) && 
//					messageSecurityContext.getPddFruitore()!=null){
//				String fruitore = messageSecurityContext.getPddFruitore();
//				signatureKS = multiKeystore.getKeyStore(fruitore);
//				aliasSignatureUser = multiKeystore.getKeyAlias(fruitore);
//				aliasSignaturePassword = multiKeystore.getKeyPassword(fruitore);
//			}
//			else if(SecurityConstants.MULTI_USER_KEYWORD_PORTA_DOMINIO_EROGATORE.equals(aliasSignatureUser) &&
//					messageSecurityContext.getPddErogatore()!=null){
//				String erogatore = messageSecurityContext.getPddErogatore();
//				signatureKS = multiKeystore.getKeyStore(erogatore);
//				aliasSignatureUser = multiKeystore.getKeyAlias(erogatore);
//				aliasSignaturePassword = multiKeystore.getKeyPassword(erogatore);
//			}
//			else if(SecurityConstants.MULTI_USER_KEYWORD_IDENTIFICATIVO_PORTA_FRUITORE.equals(aliasSignatureUser) && 
//					messageSecurityContext.getIdFruitore()!=null && messageSecurityContext.getIdFruitore().getCodicePorta()!=null){
//				String fruitore = messageSecurityContext.getIdFruitore().getCodicePorta();
//				signatureKS = multiKeystore.getKeyStore(fruitore);
//				aliasSignatureUser = multiKeystore.getKeyAlias(fruitore);
//				aliasSignaturePassword = multiKeystore.getKeyPassword(fruitore);
//			}
//			else if(SecurityConstants.MULTI_USER_KEYWORD_IDENTIFICATIVO_PORTA_EROGATORE.equals(aliasSignatureUser) &&
//					messageSecurityContext.getIdServizio()!=null && 
//					messageSecurityContext.getIdServizio().getSoggettoErogatore()!=null &&
//					messageSecurityContext.getIdServizio().getSoggettoErogatore().getCodicePorta()!=null){
//				String erogatore = messageSecurityContext.getIdServizio().getSoggettoErogatore().getCodicePorta();
//				signatureKS = multiKeystore.getKeyStore(erogatore);
//				aliasSignatureUser = multiKeystore.getKeyAlias(erogatore);
//				aliasSignaturePassword = multiKeystore.getKeyPassword(erogatore);
//			}
//			else{
//				signatureKS = multiKeystore.getKeyStore(aliasSignatureUser);
//				aliasSignaturePassword = multiKeystore.getKeyPassword(aliasSignatureUser);
//				aliasSignatureUser = multiKeystore.getKeyAlias(aliasSignatureUser); // aggiorno con identita alias del keystore
//			}
//		}
//		else{
//			throw new Exception("Nessuna modalita' di recupero del Keystore per la funzionalita' di Signature indicata");
//		}
//
//		SignatureBean bean = new SignatureBean();
//		
//		bean.setKeystore(signatureKS);
//		bean.setTruststore(signatureTrustStoreKS);
//		bean.setUser(aliasSignatureUser);
//		bean.setPassword(aliasSignaturePassword);
//		bean.setCrlPath(crlPath);
//		
//		return bean;
//
//	}
}
