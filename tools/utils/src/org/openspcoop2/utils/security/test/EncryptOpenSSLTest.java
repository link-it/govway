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
package org.openspcoop2.utils.security.test;

import java.io.File;
import java.io.IOException;
import java.security.Security;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.UtilsRuntimeException;
import org.openspcoop2.utils.certificate.SymmetricKeyUtils;
import org.openspcoop2.utils.id.IDUtilities;
import org.openspcoop2.utils.io.HexBinaryUtilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.security.Decrypt;
import org.openspcoop2.utils.security.DecryptOpenSSLPass;
import org.openspcoop2.utils.security.DecryptOpenSSLPassPBKDF2;
import org.openspcoop2.utils.security.Encrypt;
import org.openspcoop2.utils.security.EncryptOpenSSLPass;
import org.openspcoop2.utils.security.EncryptOpenSSLPassPBKDF2;
import org.openspcoop2.utils.security.OpenSSLEncryptionMode;

/**	
 * EncryptOpenSSLTest
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EncryptOpenSSLTest {

	public static void main(String[] args) throws UtilsException, IOException {
		
		for (int i = 0; i < 2; i++) {
			boolean base64 = (i==0);
			
			testCifraturaConKey(128, base64);
			testDecifraturaConKey(128, base64);
			
			testCifraturaConKey(192, base64);
			testDecifraturaConKey(192, base64);
			
			testCifraturaConKey(256, base64);
			testDecifraturaConKey(256, base64);
			
			// I test con 128 e 192 non funzionano
			testCifraturaConPassword(256, base64);
			testDecifraturaConPassword(256, base64);
			
			for (int j = 0; j < 2; j++) {
				
				Integer iteration = j==0 ? null : 65535;
				
				testCifraturaConPasswordPBKDF2(128, base64, iteration);
				testDecifraturaConPasswordPBKDF2(128, base64, iteration);
				
				testCifraturaConPasswordPBKDF2(192, base64, iteration);
				testDecifraturaConPasswordPBKDF2(192, base64, iteration);
				
				testCifraturaConPasswordPBKDF2(256, base64, iteration);
				testDecifraturaConPasswordPBKDF2(256, base64, iteration);
			}
		}
		
		logDebug("\n\nTestsuite terminata");
	}

	private static final String PASSWORD = "SegretoMoltoLungoE@D1ff1Cil!";
	
	private static final String TEXT_PLAIN = "Lorem ipsum dolor sit amet, consectetur adipisci elit, sed do eiusmod tempor incidunt ut labore et dolore magna aliqua. \nUt enim ad minim veniam, quis nostrum exercitationem ullamco laboriosam, nisi ut aliquid ex ea commodi consequatur. Duis aute irure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. \nExcepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
	
	private static final String AES_CBC_PKCS5PADDING = "AES/CBC/PKCS5Padding";
	
	private static final String FILE_NAME_IN = "testOpenSSL-in";
	private static final String FILE_NAME_OUT = "testOpenSSL-out";
	
	private static final String ALGO_TEST = "] algo-";
	private static final String END_TEST = " =============";
	
	private static final String ERROR_PREFIX = "ERRORE!!: ";
	
	private static final String OPENSSL_OPTION_DECRYPT = "-d";
	private static final String OPENSSL_OPTION_KEY = "-K";
	private static final String OPENSSL_OPTION_IV = "-iv";
	private static final String OPENSSL_OPTION_SALT = "-salt";
	private static final String OPENSSL_OPTION_BASE64 = "-a";
	private static final String OPENSSL_OPTION_PASS = "-pass";
	private static final String OPENSSL_OPTION_PASS_PREFIX = "pass:";
	private static final String OPENSSL_OPTION_PBKDF2 = "-pbkdf2";
	private static final String OPENSSL_OPTION_PBKDF2_ITERATION = "-iter";
	
	private static String getErrorTestoDecifrato(String decoded) {
		return "testo decifrato '"+decoded+"' diverso da quello atteso '"+TEXT_PLAIN+"'";
	}
	
	private static final String CIFRATO = "cifrato: ";
	private static final String CIFRATO_IN = "cifrato in: ";
	
	private static final String VERIFICA_CIFRATURA_OPENSSL_DECIFRATURA_JAVA_OK = "Testo cifrato con openssl e decifrato correttamente con java";
	private static final String VERIFICA_CIFRATURA_JAVA_DECIFRATURA_OPENSSL_OK = "Testo cifrato con java e decodificato correttamente con openssl";
	
	// AES-128 richiede una chiave di 128 bit (16 byte).
	// AES-192 richiede una chiave di 192 bit (24 byte).
	//AES-256 richiede una chiave di 256 bit (32 byte).
		 
	private static String chiave256 = null;
	static {
		try{
			chiave256 = HexBinaryUtilities.encodeAsString(IDUtilities.generateAlphaNumericRandomString(32).getBytes());
		}catch(Exception e) {
			throw new UtilsRuntimeException(e.getMessage(),e);
		}
	}
	private static String chiave192 = null;
	static {
		try{
			chiave192 = HexBinaryUtilities.encodeAsString(IDUtilities.generateAlphaNumericRandomString(24).getBytes());
		}catch(Exception e) {
			throw new UtilsRuntimeException(e.getMessage(),e);
		}
	}
	private static String chiave128 = null;
	static {
		try{
			chiave128 = HexBinaryUtilities.encodeAsString(IDUtilities.generateAlphaNumericRandomString(16).getBytes());
		}catch(Exception e) {
			throw new UtilsRuntimeException(e.getMessage(),e);
		}
	}
	
	public static void testCifraturaConKey(int size, boolean base64) throws UtilsException, IOException {
		 
		Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 2); // lasciare alla posizione 1 il provider 'SUN'
		File fIn = File.createTempFile(FILE_NAME_IN, ".dat");
		File fOut = File.createTempFile(FILE_NAME_OUT, ".dat");
		try{
		
			logDebug("\n\n========= **encrypt** chiave -K [base64:"+base64+ALGO_TEST+size+END_TEST);
			
			String chiave = chiave256;
			if(size==128) {
				chiave = chiave128;
			}
			else if(size==192) {
				chiave = chiave192;
			}
			
			Encrypt encrypt = new Encrypt(HexBinaryUtilities.decode(chiave), SymmetricKeyUtils.ALGO_AES);
			encrypt.initIV(AES_CBC_PKCS5PADDING);
			String iv = encrypt.getIVHexBinaryAsString();
			logDebug("IV: "+iv);
			
			if(base64) {
				String cipherText = encrypt.encryptBase64AsString(TEXT_PLAIN.getBytes(), AES_CBC_PKCS5PADDING);
				logDebug(CIFRATO+cipherText);
				FileSystemUtilities.writeFile(fIn, (cipherText+"\n").getBytes());
			}
			else {
				byte[] cipherText = encrypt.encrypt(TEXT_PLAIN.getBytes(), AES_CBC_PKCS5PADDING);
				FileSystemUtilities.writeFile(fIn, cipherText);
				logDebug(CIFRATO_IN+fIn.getAbsolutePath());
			}
			
			executeOpensslCommand(base64, ("aes-"+size+"-cbc"), OPENSSL_OPTION_KEY, chiave, OPENSSL_OPTION_IV, iv, OPENSSL_OPTION_SALT, OPENSSL_OPTION_DECRYPT, OPENSSL_OPTION_BASE64, "-in", fIn.getAbsolutePath(), "-out", fOut.getAbsolutePath());
			
			String decoded = FileSystemUtilities.readFile(fOut);
			if(!TEXT_PLAIN.equals(decoded)) {
				String msgError = getErrorTestoDecifrato(decoded); 
				logDebug(ERROR_PREFIX+msgError);
				throw new UtilsException(msgError);
			}
			else {
				logDebug(VERIFICA_CIFRATURA_JAVA_DECIFRATURA_OPENSSL_OK);
			}
			
		}finally {
			Security.removeProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
			
			FileSystemUtilities.deleteFile(fIn);
			FileSystemUtilities.deleteFile(fOut);
		}

	}
	
	
	
	public static void testDecifraturaConKey(int size, boolean base64) throws UtilsException, IOException {
		 
		Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 2); // lasciare alla posizione 1 il provider 'SUN'
		File fIn = File.createTempFile(FILE_NAME_IN, ".dat");
		File fOut = File.createTempFile(FILE_NAME_OUT, ".dat");
		try{
		
			logDebug("\n\n========= **decrypt** chiave -K [base64:"+base64+ALGO_TEST+size+END_TEST);
			
			String chiave = chiave256;
			if(size==128) {
				chiave = chiave128;
			}
			else if(size==192) {
				chiave = chiave192;
			}
			
			FileSystemUtilities.writeFile(fIn, TEXT_PLAIN.getBytes());
			
			String iv = "680260f540ba414b7f804782786701f6"; // numero casuale
			executeOpensslCommand(base64, ("aes-"+size+"-cbc"), OPENSSL_OPTION_KEY, chiave, OPENSSL_OPTION_IV, iv, OPENSSL_OPTION_SALT, OPENSSL_OPTION_BASE64, "-in", fIn.getAbsolutePath(), "-out", fOut.getAbsolutePath());
			
			Decrypt decrypt = new Decrypt(HexBinaryUtilities.decode(chiave), SymmetricKeyUtils.ALGO_AES, HexBinaryUtilities.decode(iv));
						
			String decoded = null;
			
			if(base64) {
				String cipherText = FileSystemUtilities.readFile(fOut);
				decoded = new String(decrypt.decryptBase64(cipherText, AES_CBC_PKCS5PADDING));
			}
			else {
				byte[] cipherText = FileSystemUtilities.readBytesFromFile(fOut);
				decoded = new String(decrypt.decrypt(cipherText, AES_CBC_PKCS5PADDING));
			}
			
			if(!TEXT_PLAIN.equals(decoded)) {
				String msgError = getErrorTestoDecifrato(decoded); 
				logDebug(ERROR_PREFIX+msgError);
				throw new UtilsException(msgError);
			}
			else {
				logDebug(VERIFICA_CIFRATURA_OPENSSL_DECIFRATURA_JAVA_OK);
			} 
			
		}finally {
			Security.removeProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
			
			FileSystemUtilities.deleteFile(fIn);
			FileSystemUtilities.deleteFile(fOut);
		}
		
	}
	
	
	
	
	public static void testCifraturaConPassword(int size, boolean base64) throws UtilsException, IOException {
		 
		Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 2); // lasciare alla posizione 1 il provider 'SUN'
		File fIn = File.createTempFile(FILE_NAME_IN, ".dat");
		File fOut = File.createTempFile(FILE_NAME_OUT, ".dat");
		try{
		
			logDebug("\n\n========= **encrypt** password -pass [base64:"+base64+ALGO_TEST+size+END_TEST);
			
			EncryptOpenSSLPass encrypt = new EncryptOpenSSLPass(PASSWORD);
			String iv = encrypt.getIVHexBinaryAsString();
			logDebug("IV: "+iv);
			
			if(base64) {
				String cipherText = encrypt.encryptBase64AsString(TEXT_PLAIN.getBytes());
				logDebug(CIFRATO+cipherText);
				FileSystemUtilities.writeFile(fIn, (cipherText+"\n").getBytes());
			}
			else {
				byte[] cipherText = encrypt.encrypt(TEXT_PLAIN.getBytes());
				FileSystemUtilities.writeFile(fIn, cipherText);
				logDebug(CIFRATO_IN+fIn.getAbsolutePath());
			}
			
			executeOpensslCommand(base64, ("aes-"+size+"-cbc"), OPENSSL_OPTION_PASS, (OPENSSL_OPTION_PASS_PREFIX+PASSWORD), OPENSSL_OPTION_SALT, OPENSSL_OPTION_DECRYPT, OPENSSL_OPTION_BASE64, "-in", fIn.getAbsolutePath(), "-out", fOut.getAbsolutePath());
			
			String decoded = FileSystemUtilities.readFile(fOut);
			if(!TEXT_PLAIN.equals(decoded)) {
				String msgError = getErrorTestoDecifrato(decoded); 
				logDebug(ERROR_PREFIX+msgError);
				throw new UtilsException(msgError);
			}
			else {
				logDebug(VERIFICA_CIFRATURA_JAVA_DECIFRATURA_OPENSSL_OK);
			}
			
		}finally {
			Security.removeProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
			
			FileSystemUtilities.deleteFile(fIn);
			FileSystemUtilities.deleteFile(fOut);
		}
		
	}
	
	
	
	
	
	public static void testDecifraturaConPassword(int size, boolean base64) throws UtilsException, IOException {
		 
		Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 2); // lasciare alla posizione 1 il provider 'SUN'
		File fIn = File.createTempFile(FILE_NAME_IN, ".dat");
		File fOut = File.createTempFile(FILE_NAME_OUT, ".dat");
		try{
		
			logDebug("\n\n========= **decrypt** password -pass [base64:"+base64+ALGO_TEST+size+END_TEST);
			
			FileSystemUtilities.writeFile(fIn, TEXT_PLAIN.getBytes());
			
			executeOpensslCommand(base64, ("aes-"+size+"-cbc"), OPENSSL_OPTION_PASS, (OPENSSL_OPTION_PASS_PREFIX+PASSWORD), OPENSSL_OPTION_SALT, OPENSSL_OPTION_BASE64, "-in", fIn.getAbsolutePath(), "-out", fOut.getAbsolutePath());
			
			DecryptOpenSSLPass decrypt = new DecryptOpenSSLPass(PASSWORD);
			
			String decoded = null;
			if(base64) {
				String cipherText = FileSystemUtilities.readFile(fOut);
				decoded = new String(decrypt.decryptBase64(cipherText));
			}
			else {
				byte[] cipherText = FileSystemUtilities.readBytesFromFile(fOut);
				decoded = new String(decrypt.decrypt(cipherText));
			}
			
			if(!TEXT_PLAIN.equals(decoded)) {
				String msgError = getErrorTestoDecifrato(decoded); 
				logDebug(ERROR_PREFIX+msgError);
				throw new UtilsException(msgError);
			}
			else {
				logDebug(VERIFICA_CIFRATURA_OPENSSL_DECIFRATURA_JAVA_OK);
			} 
			
		}finally {
			Security.removeProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
			
			FileSystemUtilities.deleteFile(fIn);
			FileSystemUtilities.deleteFile(fOut);
		}
		
	}
	
	
	
	
	
	
	private static EncryptOpenSSLPassPBKDF2 getEncryptOpenSSLPassPBKDF2(int size, Integer iteration) throws UtilsException {
		EncryptOpenSSLPassPBKDF2 encrypt = null;
		if(size == 128) {
			encrypt = iteration==null ? new EncryptOpenSSLPassPBKDF2(PASSWORD, OpenSSLEncryptionMode.AES_128_CBC) : new EncryptOpenSSLPassPBKDF2(PASSWORD, iteration, OpenSSLEncryptionMode.AES_128_CBC);
		}
		else if(size == 192) {
			encrypt = iteration==null ? new EncryptOpenSSLPassPBKDF2(PASSWORD, OpenSSLEncryptionMode.AES_192_CBC) : new EncryptOpenSSLPassPBKDF2(PASSWORD, iteration, OpenSSLEncryptionMode.AES_192_CBC);
		}
		else {
			encrypt = iteration==null ? new EncryptOpenSSLPassPBKDF2(PASSWORD) : new EncryptOpenSSLPassPBKDF2(PASSWORD, iteration);
		}
		return encrypt;
	}
	public static void testCifraturaConPasswordPBKDF2(int size, boolean base64, Integer iteration) throws UtilsException, IOException {
		 
		Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 2); // lasciare alla posizione 1 il provider 'SUN'
		File fIn = File.createTempFile(FILE_NAME_IN, ".dat");
		File fOut = File.createTempFile(FILE_NAME_OUT, ".dat");
		try{
		
			String iteratorS = iteration!=null && iteration.intValue()>0 ? " -iter "+iteration : "";
			logDebug("\n\n========= **encrypt** password -pass -pbkdf2"+iteratorS+" [base64:"+base64+ALGO_TEST+size+END_TEST);
			
			EncryptOpenSSLPassPBKDF2 encrypt = getEncryptOpenSSLPassPBKDF2(size, iteration);
			String iv = encrypt.getIVHexBinaryAsString();
			logDebug("IV: "+iv);
			
			if(base64) {
				String cipherText = encrypt.encryptBase64AsString(TEXT_PLAIN.getBytes());
				logDebug(CIFRATO+cipherText);
				FileSystemUtilities.writeFile(fIn, (cipherText+"\n").getBytes());
			}
			else {
				byte[] cipherText = encrypt.encrypt(TEXT_PLAIN.getBytes());
				FileSystemUtilities.writeFile(fIn, cipherText);
				logDebug(CIFRATO_IN+fIn.getAbsolutePath());
			}
			
			String pbkdf2 = OPENSSL_OPTION_PBKDF2+ (iteration!=null && iteration.intValue()>0 ? " "+OPENSSL_OPTION_PBKDF2_ITERATION+" "+ iteration.intValue() : "");
			executeOpensslCommand(base64, ("aes-"+size+"-cbc"), OPENSSL_OPTION_PASS, (OPENSSL_OPTION_PASS_PREFIX+PASSWORD), pbkdf2, OPENSSL_OPTION_SALT, OPENSSL_OPTION_DECRYPT, OPENSSL_OPTION_BASE64, "-in", fIn.getAbsolutePath(), "-out", fOut.getAbsolutePath());
			
			String decoded = FileSystemUtilities.readFile(fOut);
			if(!TEXT_PLAIN.equals(decoded)) {
				String msgError = getErrorTestoDecifrato(decoded); 
				logDebug(ERROR_PREFIX+msgError);
				throw new UtilsException(msgError);
			}
			else {
				logDebug(VERIFICA_CIFRATURA_JAVA_DECIFRATURA_OPENSSL_OK);
			}
			
		}finally {
			Security.removeProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
			
			FileSystemUtilities.deleteFile(fIn);
			FileSystemUtilities.deleteFile(fOut);
		}
		
	}
	
	
	
	
	
	private static DecryptOpenSSLPassPBKDF2 getDecryptOpenSSLPassPBKDF2(int size, Integer iteration) throws UtilsException {
		DecryptOpenSSLPassPBKDF2 decrypt = null;
		if(size == 128) {
			decrypt = iteration==null ? new DecryptOpenSSLPassPBKDF2(PASSWORD, OpenSSLEncryptionMode.AES_128_CBC) : new DecryptOpenSSLPassPBKDF2(PASSWORD, iteration, OpenSSLEncryptionMode.AES_128_CBC);
		}
		else if(size == 192) {
			decrypt = iteration==null ? new DecryptOpenSSLPassPBKDF2(PASSWORD, OpenSSLEncryptionMode.AES_192_CBC) : new DecryptOpenSSLPassPBKDF2(PASSWORD, iteration, OpenSSLEncryptionMode.AES_192_CBC);
		}
		else {
			decrypt = iteration==null ? new DecryptOpenSSLPassPBKDF2(PASSWORD) : new DecryptOpenSSLPassPBKDF2(PASSWORD, iteration);
		}
		return decrypt;
	}
	public static void testDecifraturaConPasswordPBKDF2(int size, boolean base64, Integer iteration) throws UtilsException, IOException {
		 
		Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 2); // lasciare alla posizione 1 il provider 'SUN'
		File fIn = File.createTempFile(FILE_NAME_IN, ".dat");
		File fOut = File.createTempFile(FILE_NAME_OUT, ".dat");
		try{
		
			String iteratorS = iteration!=null && iteration.intValue()>0 ? " -iter "+iteration : "";
			logDebug("\n\n========= **decrypt** password -pass -pbkdf2"+iteratorS+" [base64:"+base64+ALGO_TEST+size+END_TEST);
			
			FileSystemUtilities.writeFile(fIn, TEXT_PLAIN.getBytes());
			
			String pbkdf2 = OPENSSL_OPTION_PBKDF2+ (iteration!=null && iteration.intValue()>0 ? " "+OPENSSL_OPTION_PBKDF2_ITERATION+" "+ iteration.intValue() : "");
			executeOpensslCommand(base64, ("aes-"+size+"-cbc"), OPENSSL_OPTION_PASS, (OPENSSL_OPTION_PASS_PREFIX+PASSWORD), pbkdf2, OPENSSL_OPTION_SALT, OPENSSL_OPTION_BASE64, "-in", fIn.getAbsolutePath(), "-out", fOut.getAbsolutePath());
			
			DecryptOpenSSLPassPBKDF2 decrypt = getDecryptOpenSSLPassPBKDF2(size, iteration);
			
			String decoded = null;
			if(base64) {
				String cipherText = FileSystemUtilities.readFile(fOut);
				decoded = new String(decrypt.decryptBase64(cipherText));
			}
			else {
				byte[] cipherText = FileSystemUtilities.readBytesFromFile(fOut);
				decoded = new String(decrypt.decrypt(cipherText));
			}
			
			if(!TEXT_PLAIN.equals(decoded)) {
				String msgError = getErrorTestoDecifrato(decoded); 
				logDebug(ERROR_PREFIX+msgError);
				throw new UtilsException(msgError);
			}
			else {
				logDebug(VERIFICA_CIFRATURA_OPENSSL_DECIFRATURA_JAVA_OK);
			} 
			
		}finally {
			Security.removeProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
			
			FileSystemUtilities.deleteFile(fIn);
			FileSystemUtilities.deleteFile(fOut);
		}
		
	}
	
	
	
	private static final String OPENSSL_COMMAND = "opensslCmd";
	
	private static String buildCommand(boolean base64, String ... args) {
		
		String opensslCommand = System.getProperty(OPENSSL_COMMAND);
		if(opensslCommand==null || StringUtils.isEmpty(opensslCommand)) {
			opensslCommand = "openssl";
		}
		
		StringBuilder sb = new StringBuilder(opensslCommand);
		if(args!=null && args.length>0) {
			for (int i = 0; i < args.length; i++) {
				if(OPENSSL_OPTION_BASE64.equals(args[i]) && !base64) {
					continue;
				}
				sb.append(" ").append(args[i]);
			}
		}
		return sb.toString();
	}
	private static void executeOpensslCommand(boolean base64, String ... args) throws UtilsException, IOException {
		
		File fExecute = File.createTempFile("testOpenSSL-exec", ".sh");
		try{
			String com = buildCommand(base64, args);	
			logDebug("Eseguo comando '"+com+"'");
			
			StringBuilder sbExec = new StringBuilder();
			sbExec.append("#!/bin/bash\n\n").append(com);
			
			FileSystemUtilities.writeFile(fExecute, sbExec.toString().getBytes());
			if(!fExecute.setExecutable(true)) {
				// ignore
			}
			
			Process process = Runtime.getRuntime().exec(new String[]{fExecute.getAbsolutePath()});
						
			if(process.getInputStream()!=null) {
				byte[] s = Utilities.getAsByteArray(process.getInputStream(), false);
				if(s!=null && s.length>0) {
					logDebug("INFO: '"+new String(s)+"'");
				}
			}
			if(process.getErrorStream()!=null) {
				byte[] s = Utilities.getAsByteArray(process.getErrorStream(), false);
				if(s!=null && s.length>0) {
					logDebug("ERROR: '"+new String(s)+"'");
				}
			}
			
			process.descendants().forEach(ProcessHandle::destroy);
			
			process.destroy();
			
			int exitValue = -1;
			boolean terminated = false;
			while(!terminated){
				try{
					Utilities.sleep(500);
					exitValue = process.exitValue();
					terminated = true;
				}catch(java.lang.IllegalThreadStateException exit){
					// ignore
				}
			}
			
			logDebug("terminated: "+terminated);
			logDebug("exitValue: "+exitValue);
			
		}finally {
			FileSystemUtilities.deleteFile(fExecute);
		}
	}
	
	private static void logDebug(String msg) {
		System.out.println(msg);
	}
}