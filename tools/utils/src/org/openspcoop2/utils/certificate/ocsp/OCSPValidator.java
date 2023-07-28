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
package org.openspcoop2.utils.certificate.ocsp;

import java.math.BigInteger;
import java.security.cert.CRLReason;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.ocsp.ResponderID;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.cert.ocsp.OCSPReqBuilder;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.bouncycastle.cert.ocsp.RevokedStatus;
import org.bouncycastle.cert.ocsp.SingleResp;
import org.bouncycastle.cert.ocsp.UnknownStatus;
import org.bouncycastle.cert.ocsp.jcajce.JcaCertificateID;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.openspcoop2.utils.LoggerBuffer;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.UtilsMultiException;
import org.openspcoop2.utils.certificate.CRLDistributionPoint;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.ExtendedKeyUsage;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.random.RandomGenerator;
import org.openspcoop2.utils.random.SecureRandomAlgorithm;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

/**
 * OCSPValidator
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OCSPValidator {

	public static CertificateStatus check(Logger log, OCSPRequestParams params) throws UtilsException {
		LoggerBuffer lb = new LoggerBuffer();
		lb.setLogDebug(log);
		lb.setLogError(log);
		return check(lb, params, null);
	}
	public static CertificateStatus check(Logger log, OCSPRequestParams params, String crlInput) throws UtilsException {
		LoggerBuffer lb = new LoggerBuffer();
		lb.setLogDebug(log);
		lb.setLogError(log);
		return check(lb, params, crlInput);
	}
	public static CertificateStatus check(LoggerBuffer log, OCSPRequestParams params) throws UtilsException {
		return check(log, params, null);
	}
	public static CertificateStatus check(LoggerBuffer log, OCSPRequestParams params, String crlInput) throws UtilsException {
	
		if(params==null) {
			throw new UtilsException("Params is null");
		}
		if(params.getCertificate()==null) {
			throw new UtilsException("Certificate not provided");
		}
		if(params.getConfig()==null) {
			throw new UtilsException("OCSP config not provided");
		}
		if(params.isSelfSigned()) {
			return CertificateStatus.SELF_SIGNED();
		}

		int indexLimit = 1000;
		int index = 0;
		OCSPRequestParams req = params;
		CertificateStatus principalStatus = null;
		while(index<indexLimit) {

			CertificateStatus status = _check(log, req, crlInput);
			if(principalStatus==null) {
				principalStatus = status;
			}
			
			if(status.isREVOKED() || status.isEXPIRED() || status.isUNKNOWN()) {
				return status;
			}
			else {
				if(params.getConfig().isCertificateChainVerify() 
						&&
						!req.isSelfSigned() // altrimenti sono arrivato alla ca radice
						) {
					OCSPRequestParams newReq = OCSPRequestParams.build(log, req.getIssuerCertificate(), params.getConfigTrustStore(), 
							params.getConfig(), params.getReader());
					req = newReq;
				}
				else {
					return principalStatus; // ritorno lo stato di verifica del certificato principale
				}
			}			

			index++;
		}

		throw new UtilsException("Certificate chain too big");
	}

	private static CertificateStatus _check(LoggerBuffer log, OCSPRequestParams params, String crlInput) throws UtilsException {

		if(params==null) {
			throw new UtilsException("Params is null");
		}

		if(params.getCertificate()==null) {
			throw new UtilsException("Certificate not provided");
		}
		if(params.getConfig()==null) {
			throw new UtilsException("OCSP config not provided");
		}

		String prefixCert = "OCSP [certificate: "+params.getCertificate().getSubjectX500Principal()+"] ";

		CertificateInfo certificateInfo = new CertificateInfo(params.getCertificate(), "certificate");
		log.debug(prefixCert+"issuer: "+params.getCertificate().getIssuerX500Principal());
		try {
			log.debug(prefixCert+"CAissuer: "+(certificateInfo.getAuthorityInformationAccess()!=null ? certificateInfo.getAuthorityInformationAccess().getCAIssuers() : null));
		}catch(Throwable t) {
			log.debug(prefixCert+"CAissuer: read error: "+t.getMessage(),t);
		}
		try {
			log.debug(prefixCert+"OCSP: "+(certificateInfo.getAuthorityInformationAccess()!=null ? certificateInfo.getAuthorityInformationAccess().getOCSPs() : null));
		}catch(Throwable t) {
			log.debug(prefixCert+"OCSP: read error: "+t.getMessage(),t);
		}
		try {
			if(certificateInfo.getCRLDistributionPoints()!=null && 
					certificateInfo.getCRLDistributionPoints().getCRLDistributionPoints()!=null && 
					!certificateInfo.getCRLDistributionPoints().getCRLDistributionPoints().isEmpty()) {
				int indexCRL = 0;
				for (CRLDistributionPoint point : certificateInfo.getCRLDistributionPoints().getCRLDistributionPoints()) {
					log.debug(prefixCert+"CRL-"+indexCRL+"-Issuer: "+point.getCRLIssuers());
					log.debug(prefixCert+"CRL-"+indexCRL+": "+point.getDistributionPointNames());
					indexCRL++;
				}
			}
			else {
				log.debug(prefixCert+"CRL: null");
			}
		}catch(Throwable t) {
			log.debug(prefixCert+"CRL: read error: "+t.getMessage(),t);
		}
		
		Date date = DateManager.getDate();

		boolean isCA = false;
		try {
			isCA = certificateInfo.isCA();
		}catch(Throwable e) {
			throw new UtilsException(e.getMessage(),e);
		}
		
		// controllo validità temporale
		try {
			if(isCA) {
				if(params.getConfig().isCheckCAValidity()) {
					log.debug(prefixCert+"Check validity ..."); // la faccio sempre per avere l'errore puntuale
					certificateInfo.checkValid(date);
				}
			}
			else {
				if(params.getConfig().isCheckValidity()) {
					log.debug(prefixCert+"Check validity ..."); // la faccio sempre per avere l'errore puntuale
					certificateInfo.checkValid(date);		
				}
			}
		}catch(CertificateExpiredException t) {
			return CertificateStatus.EXPIRED(prefixCert+t.getMessage(), certificateInfo.getNotAfter());
		}catch(CertificateNotYetValidException t) {
			return CertificateStatus.EXPIRED(prefixCert+t.getMessage(), certificateInfo.getNotBefore());
		}catch(Throwable t) {
			return CertificateStatus.EXPIRED(prefixCert+t.getMessage(), certificateInfo.getNotAfter());
		}
		
		if(certificateInfo.isSelfSigned()) {
			log.debug(prefixCert+"Self signed");			
			return CertificateStatus.SELF_SIGNED();
		}
		
		if(params.getIssuerCertificate()==null) {
			if(params.getConfig().isRejectsCertificateWithoutCA()) {
				throw new UtilsException(prefixCert+"IssuerCertificate not provided");
			}
			else {
				return CertificateStatus.ISSUER_NOT_FOUND();
			}
		}
		
		if(params.getConfig().isCrl()) {
			// viene richiesta una validazione CRL alternativa al OCSP
			return _checkCRL(log, params, crlInput, date, prefixCert);
		}

		if (params.getResponderURIs() == null || params.getResponderURIs().isEmpty()) {
			boolean reject = true;
			if(isCA) {
				reject = params.getConfig().isRejectsCAWithoutResponderUrl();
			}
			else {
				reject = params.getConfig().isRejectsCertificateWithoutResponderUrl();
			}
			if(reject) {
				throw new UtilsException(prefixCert+"At least one OCSP responder required");
			}
			else {
				if(isCA && params.getConfig().isCrlCaCheck()) {
					return _checkCRL(log, params, crlInput, date, prefixCert);
				}
				else {
					return CertificateStatus.OCSP_RESPONDER_NOT_FOUND();
				}
			}
		}

		StringBuilder sbError = new StringBuilder();
		
		for (String responderURI : params.getResponderURIs()) {

			String prefix =prefixCert+ "["+responderURI+"] ";

			OCSPResponseCode responseCode = null;
			UtilsException error = null;
			
			OCSPRequestSigned ocspRequest = null;
			try {
				log.debug(prefix+"Build request ...");
				ocspRequest = buildOCSPReq(log, prefix, params);
			}catch(Throwable t) {
				responseCode = OCSPResponseCode.OCSP_BUILD_REQUEST_FAILED;
				error = new UtilsException(prefixCert+"(url: "+responderURI+"): "+t.getMessage(),t);
				log.error(prefix+"costruzione richiesta fallita: "+t.getMessage(),t);
				// gestito sotto con gli stati throw error;
			}
				
			BasicOCSPResp ocspResp = null;
			if(ocspRequest!=null) {
				try {
					log.debug(prefix+"Invoke ocsp ...");
					OCSPResp ocspResponse = invokeOCSP(log, ocspRequest, responderURI, params);
	
					log.debug(prefix+"Analyze response ...");
					responseCode = OCSPResponseCode.toOCSPResponseCode(ocspResponse.getStatus());
	
					if(OCSPResponseCode.SUCCESSFUL.equals(responseCode)) {
						if(ocspResponse.getResponseObject()==null) {
							throw new Exception("OCSP response object not found");
						}
						else if (ocspResponse.getResponseObject() instanceof BasicOCSPResp) {
							ocspResp = (BasicOCSPResp) ocspResponse.getResponseObject();
						} else {
							throw new Exception("Invalid or unknown OCSP response");
						}
					}
					
				}catch(Throwable t) {
					responseCode = OCSPResponseCode.OCSP_INVOKE_FAILED;
					error = new UtilsException(prefixCert+"(url: "+responderURI+"): "+t.getMessage(),t);
					log.error(prefix+"invocazione servizio ocsp fallita: "+t.getMessage(),t);
					// gestito sotto con gli stati throw error;
				}
			}
				
			try {
				if(OCSPResponseCode.SUCCESSFUL.equals(responseCode) && ocspResp!=null) {
					return analyzeResponse(log, prefix, params, date, ocspRequest, ocspResp);
				}
				else {
					
					String msgFailed = responseCode.getMessage();
					if(error!=null) {
						msgFailed = msgFailed+"; "+error.getMessage();
					}
					String exceptionMessage = "OCSP response error ("+responseCode.getCode()+" - "+responseCode.name()+"): "+msgFailed;
					
					if(params.getConfig().getResponderBreakStatus()==null || 
							params.getConfig().getResponderBreakStatus().isEmpty() ||
							params.getConfig().getResponderBreakStatus().contains(responseCode)) {
						throw new Exception(exceptionMessage);
					}
					else {
						// continue verso il prossimo responder url
						// salvo la prima risposta, e se dopo aver provato tutte le url non ho trovato una risposta valido ritorna la prima
						if(sbError.length()>0) {
							sbError.append("\n");
						}
						sbError.append("[").append(responderURI).append("] ");
						sbError.append(exceptionMessage);
					}
				}

			}catch(Throwable t) {
				log.error(prefix+"analisi fallita: "+t.getMessage(),t);
				throw new UtilsException(prefixCert+"OCSP analysis failed (url: "+responderURI+"): "+t.getMessage(),t);
			}

		}
		
		throw new UtilsException("OCSP analysis failed.\n"+sbError.toString());
	}

	private static OCSPRequestSigned buildOCSPReq(LoggerBuffer log, String prefix, OCSPRequestParams params) throws UtilsException {

		try {

			OCSPRequestSigned ocspRequestSigned = new OCSPRequestSigned();

			DigestCalculatorProvider digestCalculatorProvider = new JcaDigestCalculatorProviderBuilder().build();
			DigestCalculator digestCalculator = digestCalculatorProvider.get(CertificateID.HASH_SHA1);

			JcaCertificateID certificateID = new JcaCertificateID(digestCalculator, params.getIssuerCertificate(), params.getCertificate().getSerialNumber());

			// Nounce extension (evitare reply attacks)
			SecureRandomAlgorithm secureRandomAlgorithm = params.getConfig().getSecureRandomAlgorithm();
			if(secureRandomAlgorithm==null) {
				secureRandomAlgorithm = SecureRandomAlgorithm.SHA1PRNG;
			}
			RandomGenerator randomGenerator = new RandomGenerator(true, secureRandomAlgorithm);
			BigInteger nounce = BigInteger.valueOf(Math.abs(randomGenerator.nextInt()));

			DEROctetString derNounceString = new DEROctetString(nounce.toByteArray());
			Extension nounceExtension = new Extension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce, false, derNounceString);
			Extensions extensions = new Extensions(nounceExtension);

			OCSPReqBuilder builder = new OCSPReqBuilder();
			if(params.getConfig().isNonce()) {
				log.debug(prefix+"Build nonce value '"+derNounceString+"' ...");
				builder.addRequest(certificateID, extensions); // l'estensione nonce viene aggiunta come 'Request Single Extensions' dentro 'Requestor List'
				builder.setRequestExtensions(extensions); // l'estensione nonce viene aggiunta come 'Request Extensions' nel livello principale
			}
			else {
				builder.addRequest(certificateID);
			}
			OCSPReq ocspRequest = builder.build();

			ocspRequestSigned.request = ocspRequest;
			ocspRequestSigned.certificateID = certificateID;
			ocspRequestSigned.nounce = nounce;

			return ocspRequestSigned;

		}catch(Throwable t) {
			throw new UtilsException("Build OCSP Request failed: "+t.getMessage(),t);
		}

	}

	private static OCSPResp invokeOCSP(LoggerBuffer log, OCSPRequestSigned ocspRequest, String responderURI, OCSPRequestParams params) throws UtilsException {

		try {
			
			//java.io.File f = new java.io.File("/tmp/ocsp.request");
			//org.openspcoop2.utils.resources.FileSystemUtilities.writeFile(f, ocspRequest.request.getEncoded());
			// Per verificare serializzare response su file e usare il comando: openssl ocsp -reqin /tmp/ocsp.request -noverify -text
			
			if(!responderURI.trim().startsWith("http") && !responderURI.trim().startsWith("file")) {
				throw new Exception("Unsupported protocol");
			}
			
			HttpRequest req = new HttpRequest();
			req.setMethod(HttpRequestMethod.POST);
			req.setContentType(HttpConstants.CONTENT_TYPE_OCSP_REQUEST);
			req.setContent(ocspRequest.request.getEncoded());
			
			responderURI = responderURI.trim();
			if(params.getConfig().getForwardProxy_url()!=null && StringUtils.isNotEmpty(params.getConfig().getForwardProxy_url())) {
				String forwardProxyUrl = params.getConfig().getForwardProxy_url();
				String remoteLocation = params.getConfig().isForwardProxy_base64() ? Base64Utilities.encodeAsString(responderURI.getBytes()) : responderURI;
				if(params.getConfig().getForwardProxy_header()!=null && StringUtils.isNotEmpty(params.getConfig().getForwardProxy_header())) {
					req.addHeader(params.getConfig().getForwardProxy_header(), remoteLocation);
				}
				else if(params.getConfig().getForwardProxy_queryParameter()!=null && StringUtils.isNotEmpty(params.getConfig().getForwardProxy_queryParameter())) {
					Map<String, List<String>> queryParameters = new HashMap<>();
					TransportUtils.addParameter(queryParameters,params.getConfig().getForwardProxy_queryParameter(), remoteLocation);
					forwardProxyUrl = TransportUtils.buildUrlWithParameters(queryParameters, forwardProxyUrl, false, log.getLogDebug());
				}
				else {
					throw new Exception("Forward Proxy configuration error: header and query parameter not found");
				}
				req.setUrl(forwardProxyUrl);
			}
			else {
				req.setUrl(responderURI);
			}
			
			if(req.getUrl().startsWith("https")) {
				req.setHostnameVerifier(params.getConfig().isExternalResources_hostnameVerifier());
				req.setTrustAllCerts(params.getConfig().isExternalResources_trustAllCerts());
				if(params.getHttpsTrustStore()!=null) {
					req.setTrustStore(params.getHttpsTrustStore().getKeystore());
				}
				if(params.getHttpsKeyStore()!=null) {
					req.setKeyStore(params.getHttpsKeyStore().getKeystore());
					req.setKeyAlias(params.getConfig().getExternalResources_keyAlias());
					req.setKeyPassword(params.getConfig().getExternalResources_keyPassword());
				}
			}
			req.setConnectTimeout(params.getConfig().getConnectTimeout());
			req.setReadTimeout(params.getConfig().getReadTimeout());

			HttpResponse res = HttpUtilities.httpInvoke(req);

			List<Integer> returnCodeValid = params.getConfig().getResponderReturnCodeOk();
			if(returnCodeValid==null) {
				returnCodeValid = new ArrayList<>();
			}
			if(returnCodeValid.isEmpty()) {
				returnCodeValid.add(200);
			}
			boolean isValid = false;
			for (Integer rt : returnCodeValid) {
				if(rt!=null && rt.intValue() == res.getResultHTTPOperation()) {
					isValid = true;
					break;
				}
			}

			byte[] response = res.getContent();
			
			//java.io.File f = new java.io.File("/tmp/ocsp.response");
			//org.openspcoop2.utils.resources.FileSystemUtilities.writeFile(f, response);
			// Per verificare serializzare response su file e usare il comando:  openssl ocsp -respin /tmp/ocsp.response -noverify -text

			if(isValid) {
				if(response!=null && response.length>0) {
					return new OCSPResp(res.getContent());
				}
				else {
					throw new Exception("OCSP empty response (http code: "+res.getResultHTTPOperation()+")");
				}
			}
			else {
				String error = null;
				if(response.length<=(2048)) {
					error = Utilities.convertToPrintableText(response, 2048);
					if(error!=null && error.contains("Visualizzazione non riuscita")) {
						error = null;
					}
				}
				if(error==null) {
					error="";
				}
				else {
					error = ": "+error;
				}
				throw new Exception("OCSP response error (http code: "+res.getResultHTTPOperation()+")"+error);
			}

		}catch(Throwable t) {
			throw new UtilsException("Invoke OCSP '"+responderURI+"' failed: "+t.getMessage(),t);
		}

	}

	private static CertificateStatus analyzeResponse(LoggerBuffer log, String prefix, OCSPRequestParams params, Date date, OCSPRequestSigned requestSigned, BasicOCSPResp basicOcspResponse)
			throws UtilsException {

		// Verifico la risposta ottenuta dal OCSP
		verifyResponse(log, prefix, params, date, requestSigned, basicOcspResponse);
		
		// Analizzo la risposta, la quale potrebbe contenere informazioni anche per altri certificati.
		SingleResp expectedResponseForCertificate = null;
		for (SingleResp resp : basicOcspResponse.getResponses()) {
			if (isEquals(requestSigned.certificateID, resp.getCertID())) {
				expectedResponseForCertificate = resp;
				break;
			}
		}
		if (expectedResponseForCertificate == null) {
			throw new UtilsException("OSPC Response does not contain info for certificate supplied in the OCSP request");
		}
		org.bouncycastle.cert.ocsp.CertificateStatus certStatus = expectedResponseForCertificate.getCertStatus();
		
		// NO! lo stato null è proprio il GOOD. GOOD è un alias a null
//		if(certStatus==null) {
//			throw new UtilsException("OSPC Response does not contain status info for certificate supplied in the OCSP request");
//		}
		if (certStatus == org.bouncycastle.cert.ocsp.CertificateStatus.GOOD) {
			 return CertificateStatus.GOOD();
		}
		else if (certStatus instanceof RevokedStatus) {
			RevokedStatus revoked = (RevokedStatus)certStatus;
			CRLReason reason = null;
			if (revoked.hasRevocationReason()) {
				reason = CRLReason.values()[revoked.getRevocationReason()];
			}
			return CertificateStatus.REVOKED(reason, revoked.getRevocationTime());
		}
		else if (certStatus instanceof UnknownStatus) {
			return CertificateStatus.UNKNOWN();
		}
		else {
            throw new UtilsException("OSPC Response contain unknown revocation status ("+certStatus+")");
        }
		
	}

	private static boolean isEquals(JcaCertificateID ocspRequestCertificateId, CertificateID ocspResponseCertificateId) {
		if (ocspRequestCertificateId == null || ocspResponseCertificateId == null)
			return false;
		if (ocspRequestCertificateId == ocspResponseCertificateId)
			return true;        
		boolean serialNumberEquals = ocspRequestCertificateId.getSerialNumber()!=null && ocspResponseCertificateId.getSerialNumber()!=null && ocspRequestCertificateId.getSerialNumber().equals(ocspResponseCertificateId.getSerialNumber());
		boolean nameHashEquals = ocspRequestCertificateId.getIssuerNameHash()!=null && ocspResponseCertificateId.getIssuerNameHash()!=null && Arrays.equals(ocspRequestCertificateId.getIssuerNameHash(), ocspResponseCertificateId.getIssuerNameHash());
		boolean keyHashEquals = ocspRequestCertificateId.getIssuerKeyHash()!=null && ocspResponseCertificateId.getIssuerKeyHash()!=null && Arrays.equals(ocspRequestCertificateId.getIssuerKeyHash(), ocspResponseCertificateId.getIssuerKeyHash());
		return serialNumberEquals && nameHashEquals && keyHashEquals;

	}

	private static void verifyResponse(LoggerBuffer log, String prefix, OCSPRequestParams params, Date date, OCSPRequestSigned requestSigned, BasicOCSPResp basicOcspResponse) throws UtilsException {

		List<X509CertificateHolder> certs = new ArrayList<>(Arrays.asList(basicOcspResponse.getCerts())); 
		try {
			// certificati ritornati con la risposta ocsp
			X509CertificateHolder[] ospcCerts = basicOcspResponse.getCerts();
			if(ospcCerts!=null && ospcCerts.length>0) {
				certs.addAll(Arrays.asList(ospcCerts));
			}
			
			// certificato dell'issuer
			certs.add(new JcaX509CertificateHolder(params.getIssuerCertificate())); 
			
			// eventuale certificato di firma del responder OCSP autorizzato puntualmente
			if (params.getSignerCertificate() != null) {
				certs.add(new JcaX509CertificateHolder(params.getSignerCertificate()));
			}
		} catch (Throwable e) {
			throw new UtilsException("OCSP Response signature unverifiable; read certs failed: "+e.getMessage(),e);
		}
		
		// Ottengo il certificato utilizzato per firmare la risposta
		X509Certificate signingCert = readSigningCertByResponderId(log, certs, basicOcspResponse);
		if (signingCert == null) {
			throw new UtilsException("OCSP Response signature unverifiable: signing cert not found");
		}

		// Valido il certificato
		verifySigningCert(log, prefix, signingCert, params, date);
		
		// Verifico la risposta rispetto al certificato
		try {
			log.debug(prefix+"Verify OCSP Response ...");
			JcaContentVerifierProviderBuilder builder = new JcaContentVerifierProviderBuilder();
			builder.setProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
			ContentVerifierProvider contentVerifier = builder.build(signingCert.getPublicKey());
	        boolean valid = basicOcspResponse.isSignatureValid(contentVerifier);
	        if(!valid) {
	        	throw new Exception("invalid signature");
	        }
		} catch(Throwable t) {
			throw new UtilsException("Verifying OCSP Response's signature failed: "+t.getMessage(),t);
		}

		// Verifico validità risposta rispetto a nonce o data
		verifyNonceOrDates(log, prefix, params, requestSigned, basicOcspResponse, date);
	}
	
	private static X509Certificate readSigningCertByResponderId(LoggerBuffer log, List<X509CertificateHolder> certs, BasicOCSPResp basicOcspResponse) throws UtilsException {
		
		if(basicOcspResponse.getResponderId()==null) {
			throw new UtilsException("OSPC Response does not contain responder id");
		}
		ResponderID responderId = basicOcspResponse.getResponderId().toASN1Primitive();
		if(responderId==null) {
			throw new UtilsException("OSPC Response does not contain responder id (asn1)");
		}
		
		List<Throwable> listThrowable = new ArrayList<>();
		
		if (certs!=null && !certs.isEmpty()) {

			X500Name responderName = responderId.getName();
			byte[] responderKey = responderId.getKeyHash();

			if (responderName != null) {
				for (X509CertificateHolder certHolder : certs) {
					try {
						JcaX509CertificateConverter converter = new JcaX509CertificateConverter();
						converter.setProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
						X509Certificate certCheck = converter.getCertificate(certHolder);
						X500Name nameCheck = new X500Name(certCheck.getSubjectX500Principal().getName());
						if (responderName.equals(nameCheck)) {
							return certCheck;
						}
					} catch (Throwable t) {
						log.debug("check (responderName) failed: "+t.getMessage(),t);
						listThrowable.add(t);
					}
				}
			} else if (responderKey != null) {
				SubjectKeyIdentifier responderSubjectKey = new SubjectKeyIdentifier(responderKey);
				for (X509CertificateHolder certHolder : certs) {
					try {
						JcaX509CertificateConverter converter = new JcaX509CertificateConverter();
						converter.setProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
						X509Certificate certCheck = converter.getCertificate(certHolder);
						
						// verifico per subject key identifier
						SubjectKeyIdentifier subjectKeyIdentifierCheck = null;
						if (certHolder.getExtensions() != null) {
							subjectKeyIdentifierCheck = SubjectKeyIdentifier.fromExtensions(certHolder.getExtensions());
						}
						if (subjectKeyIdentifierCheck != null && responderSubjectKey.equals(subjectKeyIdentifierCheck)) {
							return certCheck;
						}

						// verifico per chiave pubblica
						subjectKeyIdentifierCheck = new JcaX509ExtensionUtils().createSubjectKeyIdentifier(certCheck.getPublicKey());
						if (responderSubjectKey.equals(subjectKeyIdentifierCheck)) {
							return certCheck;
						}
					} catch (Throwable t) {
						log.debug("check (responderKey) failed: "+t.getMessage(),t);
						listThrowable.add(t);
					}
				}
			}
		}
		
		if(!listThrowable.isEmpty()) {
			if(listThrowable.size()==1) {
				Throwable t = listThrowable.get(0);
				throw new UtilsException("OCSP Response signature unverifiable: signing cert not found; "+t.getMessage(),t);
			}
			else {
				UtilsMultiException multi = new UtilsMultiException("OCSP Response signature unverifiable: signing cert not found; multiple exception",listThrowable.toArray(new Throwable[1]));
				throw new UtilsException(multi.getMessage(),multi);
			}
		}
		
		return null;
	}
	
	private static void verifySigningCert(LoggerBuffer log, String prefix, X509Certificate signingCert, OCSPRequestParams params, Date date) throws UtilsException {
		
		// Un responder OCSP può firmare le risposte in 3 modi (https://www.rfc-editor.org/rfc/rfc6960#section-2.6):
		// 1) La risposta viene firmata utilizzando lo stesso certificato della CA che ha emesso i certificati che si controlla.
		//    In questo caso, nella risposta non viene ritornato alcun certificato.
		//    Questo caso non richiede altri controlli al di fuori delle normali verifiche previste nella connessione TLS o gestione della sicurezza messaggio da cui il certificato proviene.
		// 2) La risposta viene firmata utilizzando un altro certificato firmato dalla stessa CA che ha emesso i certificati che si controlla.
		//    In questo caso nella risposta viene ritornato il certificato di firma utilizzato.
		//    Deve essere controllato che il certificato abbia una 'Extended Key Usage Extension' impostata a 'id-kp-OCSPSigning' così che possa essere considerato affidabile per questo scopo.
		//    Il controllo è fondamentale per prevenire attacchi "man in the middle" (siamo in http durante la comunicazione con OCSP) 
		//    dove la risposta intercettata viene alterata firmandola con un altro certificato rilasciato sempre dalla CA, ma non adibito a firmare risposte OCSP.
		// 3) La risposta viene firmata usando un altro certificato che non è in relazione con il certificato che si sta controllando.
		//    Deve quindi essere verificato ulteriormente il certificato ritornato dal responder per assicurarsi che sia "trusted"
			
		if(signingCert==null) {
			throw new UtilsException("Signing certificate not found");
		}

		if (signingCert.equals(params.getIssuerCertificate())) {

			// System.out.println("*** [Case 1] OCSP response is signed by the certificate's Issuing CA ***");
			// Non sono richiesti altri controlli
			
			log.debug(prefix+"[Case 1] OCSP response is signed by the certificate's Issuing CA");
			
		} else if (params.getSignerCertificate() != null && signingCert.equals(params.getSignerCertificate())) {
			
			// System.out.println("*** [Case 3] OCSP response is signed by an authorized responder certificate manually configured***");
			// Essendo un certificato fornito manualmente non servono altri controlli
			// NOTA: essendo fornito puntualmente, è compito di chi lo configura assicurarsi che abbia l'extension key usage corretto o ne accetta il fatto che non le abbia

			log.debug(prefix+"[Case 3] OCSP response is signed by an authorized responder certificate manually configured: "+signingCert.getSubjectX500Principal());
			
		} else {

			// System.out.println("*** [Case 2 e 3] OCSP response is signed by an responder certificate readed in ocsp response ***");

			// Estratto da https://www.rfc-editor.org/rfc/rfc6960#section-4.2.2.2
			// - OCSP signing delegation SHALL be designated by the inclusion of  id-kp-OCSPSigning in an extended key usage certificate extension included in the OCSP response signer's certificate.  
			//   This certificate MUST be issued directly by the CA that is identified in the request.
			
			boolean responderCertificateManuallyAuthorized = false;
			X509Certificate differentIssuerResponderCertificateCA = null;
			KeyStore differentIssuerResponderCertificateTrustStore = null;
			
			if (!signingCert.getIssuerX500Principal().equals(params.getIssuerCertificate().getSubjectX500Principal())) {
				// Case 3: La risposta viene firmata usando un altro certificato che non è in relazione con il certificato che si sta controllando.
				
				log.debug(prefix+"[Case 3] OCSP response is signed by an responder certificate readed in ocsp response (different CA '"+signingCert.getIssuerX500Principal()+"'): "+signingCert.getSubjectX500Principal());
				
				if(params.getSignerTrustStore()!=null) {
					X509Certificate tmp = (X509Certificate) params.getSignerTrustStore().getCertificateBySubject(signingCert.getSubjectX500Principal());
					if(tmp!=null && tmp.equals(signingCert)) {
						responderCertificateManuallyAuthorized = true; // autorizzato puntualmente il certificato
					}
					else {
						differentIssuerResponderCertificateCA = (X509Certificate) params.getSignerTrustStore().getCertificateBySubject(signingCert.getIssuerX500Principal());
					}
					
					differentIssuerResponderCertificateTrustStore = params.getSignerTrustStore();
				}
				
				if(!responderCertificateManuallyAuthorized && differentIssuerResponderCertificateCA==null) {
					throw new UtilsException("Signing certificate is not authorized to sign OCSP responses: unauthorized different issuer certificate '"+signingCert.getIssuerX500Principal()+"'");
				}
			}
			else {
				log.debug(prefix+"[Case 2] OCSP response is signed by an responder certificate readed in ocsp response (same CA): "+signingCert.getSubjectX500Principal());
			}
			
			// Controllo Extended Key Usage
			
			CertificateInfo certificateInfo = new CertificateInfo(signingCert, "signingCert");
			List<ExtendedKeyUsage> requiredExtendedKeyUsages = params.getConfig().getExtendedKeyUsageRequired(); // consente di disabilitare il controllo per ambienti di test
			if(requiredExtendedKeyUsages!=null && !requiredExtendedKeyUsages.isEmpty()) {
				for (ExtendedKeyUsage extendedKeyUsage : requiredExtendedKeyUsages) {
					boolean hasExtendedKeyUsage = false;
					try {
						log.debug(prefix+"Check ExtendedKeyUsage '"+extendedKeyUsage+"' ...");
						hasExtendedKeyUsage = certificateInfo.hasExtendedKeyUsage(extendedKeyUsage);
					}catch(Throwable t) {
						throw new UtilsException("Signing certificate not valid for signing OCSP responses: extended key usage '"+extendedKeyUsage+"' not found; "+t.getMessage(),t);
					}
					if(!hasExtendedKeyUsage) {
						throw new UtilsException("Signing certificate not valid for signing OCSP responses: extended key usage '"+extendedKeyUsage+"' not found");
					}
				}
			}
			else {
				log.debug(prefix+"Check ExtendedKeyUsage disable");
			}
			
			// Verifica del certificato di firma
			
			// https://www.rfc-editor.org/rfc/rfc6960#section-4.2.2.2.1
			
			// Revocation Checking of an Authorized Responder
			//    Since an authorized OCSP responder provides status information for one or more CAs, OCSP clients need to know how to check that an Authorized Responder's certificate has not been revoked.  
			//    CAs may  choose to deal with this problem in one of three ways:
			//
			//    1) A CA may specify that an OCSP client can trust a responder for the lifetime of the responder's certificate.  
			//       The CA does so by including the extension id-pkix-ocsp-nocheck.  
			//       This SHOULD be a non-critical extension.  The value of the extension SHALL be NULL.
			//       CAs issuing such a certificate should realize that a compromise of the responder's key is as serious as the compromise of a CA key used to sign CRLs, 
			//       at least for the validity period of this certificate.  
			//       CAs may choose to issue this type of certificate with a very short lifetime and renew it frequently.
			//       Identificativo: id-pkix-ocsp-nocheck OBJECT IDENTIFIER ::= { id-pkix-ocsp 5 }
			//
			//    2) A CA may specify how the responder's certificate is to be checked for revocation.  
			//        This can be done by using CRL Distribution Points if the check should be done using CRLs, 
			//        or by using Authority Information Access if the check should be done in some other way.
			//        Details for specifying either of these two mechanisms are available in [RFC5280].
			//    
			//    3) A CA may choose not to specify any method of revocation checking for the responder's certificate, 
			//       in which case it would be up to the OCSP client's local security policy to decide whether that certificate should be checked for revocation or not.
			
			org.openspcoop2.utils.certificate.Extensions exts = null;
			try {
				exts = certificateInfo.getExtensions();
			}catch(Throwable t) {
				log.debug("Extension read failed: "+t.getMessage(),t);
			}
			boolean ocsp_nocheck = exts!=null && exts.hasExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nocheck);
			
			log.debug(prefix+"ocsp_nocheck:"+ocsp_nocheck);
			
			// Caso 1 e 3 vengono gestiti in ugual maniera
			// Il caso 2 viene gestito con CRL, mentre non si gestisce un eventuale loop verso un altro servizio OCSP
			
			CRLParams crlParams = null;
			if(!ocsp_nocheck) {
				if(params.getConfig().isCrlSigningCertCheck()) {
					log.debug(prefix+"(SigningCert:"+signingCert.getSubjectX500Principal()+") Build CRL params...");
					KeyStore trustStore_config = differentIssuerResponderCertificateTrustStore!=null ? differentIssuerResponderCertificateTrustStore : params.getIssuerTrustStore();
					try {
						crlParams = CRLParams.build(log, signingCert, null, trustStore_config, params.getConfig(), params.getReader());
					}catch(Throwable t) {
						throw new UtilsException(t.getMessage(),t);
					}
				}
			}
			
			if(crlParams==null || crlParams.getCrlCertstore()==null) { // altrimenti la validita' viene verificata insieme alle CRL)
				// Controllo che non sia scaduto
				try {
					log.debug(prefix+" (SigningCert:"+signingCert.getSubjectX500Principal()+") Check valid...");
					certificateInfo.checkValid(date);
				}catch(CertificateNotYetValidException t) {
					throw new UtilsException("Signing certificate not yet valid: "+t.getMessage(),t);
				}catch(CertificateExpiredException t) {
					throw new UtilsException("Signing certificate expired: "+t.getMessage(),t);
				}catch(Throwable t) {
					throw new UtilsException("Signing certificate not valid: "+t.getMessage(),t);
				}
			}
			
			String CA = "n.d.";
			try {
				if(responderCertificateManuallyAuthorized) {
					// non devo verificarlo, è stato inserito nel truststore dedicato il certificato del responder puntualmente
				}
				else if(differentIssuerResponderCertificateCA!=null) {
					if(differentIssuerResponderCertificateCA.getSubjectX500Principal()!=null) {
						CA = differentIssuerResponderCertificateCA.getSubjectX500Principal().toString();
					}
					log.debug(prefix+"(SigningCert:"+signingCert.getSubjectX500Principal()+") verify against ca '"+CA+"'...");
					certificateInfo.verify(differentIssuerResponderCertificateCA);
				}
				else {
					if(params.getIssuerCertificate().getSubjectX500Principal()!=null) {
						CA = params.getIssuerCertificate().getSubjectX500Principal().toString();
					}
					log.debug(prefix+"(SigningCert:"+signingCert.getSubjectX500Principal()+") verify against ca '"+CA+"'...");
					certificateInfo.verify(params.getIssuerCertificate());
				}
			} catch (Throwable t) {
				throw new UtilsException("Signing certificate not valid (CA: "+CA+"): "+t.getMessage(),t);
			}
			
			if(crlParams!=null && crlParams.getCrlCertstore()!=null) {
				try {
					log.debug(prefix+"(SigningCert:"+signingCert.getSubjectX500Principal()+") CRL check...");
					certificateInfo.checkValid(crlParams.getCrlCertstore(), crlParams.getCrlTrustStore(), date);
				}catch(Throwable t) {
					throw new UtilsException("Signing certificate not valid (CRL): "+t.getMessage(),t);
				}
			}

		}
		
	}
	
	private static void verifyNonceOrDates(LoggerBuffer log, String prefix, OCSPRequestParams params, OCSPRequestSigned requestSigned, BasicOCSPResp basicOcspResponse, Date date) throws UtilsException {
		// https://www.rfc-editor.org/rfc/rfc6960#section-4.4.1
		
		// The nonce cryptographically binds a request and a response to prevent replay attacks.  
		// The nonce is included as one of the requestExtensions in requests, while in responses it would be included as one of the responseExtensions.  
		// In both the request and the response, the nonce will be identified by the object identifier id-pkix-ocsp-nonce, while the extnValue is the value of the nonce.
		byte[] requestNonce = requestSigned.nounce!=null ? requestSigned.nounce.toByteArray() : null;
		Extension responseNonce = basicOcspResponse.getExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce);
		if (responseNonce != null && requestNonce != null) {
			log.debug(prefix+"Verify nonce request-response...");
			if(!Arrays.equals(requestNonce, responseNonce.getExtnValue().getOctets())) {
				throw new UtilsException("OCSP Response not valid: nonces do not match");
			}
		}
		else {
			log.debug(prefix+"Verify nonce dates...");
			
			// https://www.rfc-editor.org/rfc/rfc6960#section-4.2.2.1
			
			// Responses can contain four times -- thisUpdate, nextUpdate, producedAt, and revocationTime.  
			// The semantics of these fields are defined in Section 2.4.  The format for GeneralizedTime is as specified in Section 4.1.2.5.2 of [RFC5280].

			// The thisUpdate and nextUpdate fields define a recommended validity interval.  This interval corresponds to the {thisUpdate, nextUpdate} interval in CRLs.  
			// Responses whose nextUpdate value is earlier than the local system time value SHOULD be considered unreliable.
			// Responses whose thisUpdate time is later than the local system time SHOULD be considered unreliable.
			// If nextUpdate is not set, the responder is indicating that newer  revocation information is available all the time.
			
			// The semantics of these fields are:

			// thisUpdate      The most recent time at which the status being indicated is known by the responder to have been correct.

			// nextUpdate      The time at or before which newer information will be available about the status of the certificate.

			// producedAt      The time at which the OCSP responder signed this response.

			// revocationTime  The time at which the certificate was revoked or placed on hold.
			
			long current = date.getTime();
			int toleranceMilliseconds = params.getConfig().getResponseCheckDateToleranceMilliseconds();
			Date rightInterval = new Date(current + (long) toleranceMilliseconds);
			Date leftInterval = new Date(current - (long) toleranceMilliseconds);

			SingleResp[] resp = basicOcspResponse.getResponses();
			if(resp!=null && resp.length>0) {
				// una risposta ci deve essere, se non c'è è già stata sollevata una eccezione in precedenza
				for (SingleResp singleResp : resp) {
					
					// Responses whose thisUpdate time is later than the local system time SHOULD be considered unreliable.
					if(rightInterval.before(singleResp.getThisUpdate())){
						throw new UtilsException("OCSP Response is unreliable: this update time '"+DateUtils.getSimpleDateFormatMs().format(singleResp.getThisUpdate())+"' is later than the local system time");
					}
					
					// Responses whose nextUpdate value is earlier than the local system time value SHOULD be considered unreliable.
					// If nextUpdate is not set, the responder is indicating that newer  revocation information is available all the time.
					Date dateCheck = singleResp.getNextUpdate() != null ? singleResp.getNextUpdate() : singleResp.getThisUpdate();
					if(leftInterval.after(dateCheck)) {
						throw new UtilsException("OCSP Response is unreliable: next update time '"+DateUtils.getSimpleDateFormatMs().format(dateCheck)+"' is earlier than the local system time");
					}
				}
			}

		}
		
	}
	
	private static CertificateStatus _checkCRL(LoggerBuffer log, OCSPRequestParams params, String crlInput, Date date, String prefix) throws UtilsException {
		
		log.debug(prefix+"Build CRL request ...");
		
		CRLParams crlParams = null;
		try {
			crlParams = CRLParams.build(log, params.getCertificate(), crlInput, params.getIssuerTrustStore(), params.getConfig(), params.getReader());
		}catch(Throwable t) {
			throw new UtilsException(t.getMessage(),t);
		}
				
		CertificateInfo certificateInfo = new CertificateInfo(params.getCertificate(), "certificateCrlCheck");
		
		if(!certificateInfo.isSelfSigned()) {
			log.debug(prefix+"Verify against CA ...");
			
			String CA = "n.d.";
			try {
				if(params.getIssuerCertificate().getSubjectX500Principal()!=null) {
					CA = params.getIssuerCertificate().getSubjectX500Principal().toString();
				}
				certificateInfo.verify(params.getIssuerCertificate());
			} catch (Throwable t) {
				CertificateStatus cs = CertificateStatus.REVOKED(CRLReason.UNSPECIFIED, null);
				String eMessage = t.getMessage();
				String msgError = "Certificate not valid (CA: "+CA+"): "+eMessage;
				log.error(msgError, t);
				cs.setDetails(msgError);
				return cs;
			}
		}
		else {
			log.debug(prefix+"Certificate self-signed");
		}
		
		CertificateStatus status = CertificateStatus.GOOD();
		
		try {
			if(crlParams.getCrlCertstore()!=null) {
				
				log.debug(prefix+"Verify CRL ...");
				
				certificateInfo.checkValid(crlParams.getCrlCertstore(), crlParams.getCrlTrustStore(), date);
			}
			else {
				log.debug(prefix+"CRL undefined");
				
				status = CertificateStatus.CRL_NOT_FOUND();
			}
		}catch(Throwable t) {
			CertificateStatus cs = CertificateStatus.REVOKED(CRLReason.UNSPECIFIED, null);
			String eMessage = t.getMessage();
			if(Utilities.existsInnerException(t, java.security.cert.CertificateExpiredException.class)) {
				Throwable inner = Utilities.getInnerException(t, java.security.cert.CertificateExpiredException.class);
				if(inner!=null && inner.getMessage()!=null) {
					eMessage = inner.getMessage();
				}
			}
			else if (Utilities.existsInnerException(t, java.security.cert.CertificateRevokedException.class)) {
				Throwable inner = Utilities.getInnerException(t, java.security.cert.CertificateRevokedException.class);
				if(inner!=null && inner instanceof java.security.cert.CertificateRevokedException) {
					java.security.cert.CertificateRevokedException cre = (java.security.cert.CertificateRevokedException) inner;
					if(cre.getRevocationReason()!=null) {
						cs = CertificateStatus.REVOKED(cre.getRevocationReason(), cre.getRevocationDate());
					}
				}
			}
			String msgError = "Certificate not valid (CRL): "+eMessage;
			log.error(msgError, t);
			cs.setDetails(msgError);
			return cs;
		}
		
		return status;
	}
}

class OCSPRequestSigned {
	OCSPReq request;
	BigInteger nounce;
	JcaCertificateID certificateID;
}
