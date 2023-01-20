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

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.LoggerBuffer;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.UtilsMultiException;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.AuthorityInformationAccess;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.KeyStore;

/**
 * OCSPRequestParams
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OCSPRequestParams {

	private X509Certificate certificate;
	private X509Certificate issuerCertificate;
	private KeyStore issuerTrustStore;
	private X509Certificate signerCertificate;
	private KeyStore signerTrustStore;
	private List<String> responderURIs;
	private KeyStore httpsTrustStore;
	private KeyStore httpsKeyStore;
	private OCSPConfig config;
	private boolean isSelfSigned;
	private boolean isCA;
	
	// serve per validare la catena 
	private KeyStore configTrustStore; 
	private IOCSPResourceReader reader;
	
	public boolean isSelfSigned() {
		return this.isSelfSigned;
	}
	public void setSelfSigned(boolean isSelfSigned) {
		this.isSelfSigned = isSelfSigned;
	}
	public boolean isCA() {
		return this.isCA;
	}
	public void setCA(boolean isCA) {
		this.isCA = isCA;
	}
	public OCSPConfig getConfig() {
		return this.config;
	}
	public void setConfig(OCSPConfig config) {
		this.config = config;
	}
	public X509Certificate getCertificate() {
		return this.certificate;
	}
	public void setCertificate(X509Certificate certificate) {
		this.certificate = certificate;
	}
	public X509Certificate getIssuerCertificate() {
		return this.issuerCertificate;
	}
	public void setIssuerCertificate(X509Certificate issuerCertificate) {
		this.issuerCertificate = issuerCertificate;
	}
	public KeyStore getIssuerTrustStore() {
		return this.issuerTrustStore;
	}
	public void setIssuerTrustStore(KeyStore issuerTrustStore) {
		this.issuerTrustStore = issuerTrustStore;
	}
	public X509Certificate getSignerCertificate() {
		return this.signerCertificate;
	}
	public void setSignerCertificate(X509Certificate signerCertificate) {
		this.signerCertificate = signerCertificate;
	}
	public KeyStore getSignerTrustStore() {
		return this.signerTrustStore;
	}
	public void setSignerTrustStore(KeyStore signerTrustStore) {
		this.signerTrustStore = signerTrustStore;
	}
	public List<String> getResponderURIs() {
		return this.responderURIs;
	}
	public void setResponderURIs(List<String> responderURIs) {
		this.responderURIs = responderURIs;
	}
	public KeyStore getHttpsTrustStore() {
		return this.httpsTrustStore;
	}
	public void setHttpsTrustStore(KeyStore httpsTrustStore) {
		this.httpsTrustStore = httpsTrustStore;
	}
	public KeyStore getHttpsKeyStore() {
		return this.httpsKeyStore;
	}
	public void setHttpsKeyStore(KeyStore httpsKeyStore) {
		this.httpsKeyStore = httpsKeyStore;
	}
	
	public KeyStore getConfigTrustStore() {
		return this.configTrustStore;
	}
	public void setConfigTrustStore(KeyStore configTrustStore) {
		this.configTrustStore = configTrustStore;
	}
	public IOCSPResourceReader getReader() {
		return this.reader;
	}
	public void setReader(IOCSPResourceReader reader) {
		this.reader = reader;
	}
	
	public static OCSPRequestParams build(LoggerBuffer log, X509Certificate certificate, KeyStore trustStore, OCSPConfig config, IOCSPResourceReader reader) throws UtilsException {
		CertificateInfo cer = new CertificateInfo(certificate, "ocspVerifica");
		return build(log, cer, trustStore, config, reader);
	}
	public static OCSPRequestParams build(LoggerBuffer log, CertificateInfo certificate, KeyStore trustStore, OCSPConfig config, IOCSPResourceReader reader) throws UtilsException {
	
		if(config==null) {
			throw new UtilsException("Param config is null");
		}
		if(certificate==null) {
			throw new UtilsException("Param certificate is null");
		}
		
		OCSPRequestParams params = new OCSPRequestParams();
		params.config = config;
		params.certificate = certificate.getCertificate();
		try {
			params.isCA = certificate.isCA();
		}catch(Throwable t) {
			throw new UtilsException(t.getMessage(),t);
		}
		params.isSelfSigned = certificate.isSelfSigned();
		
		params.configTrustStore = trustStore;
		params.reader = reader;
		
		if(!params.isSelfSigned) {
		
			reader.initConfig(config);
			
			// comprendo issuer del certificato da validare
			if(config.getCaSource()!=null && !config.getCaSource().isEmpty()) {
				for (CertificateSource s : config.getCaSource()) {
					if(CertificateSource.CONFIG.equals(s)) {
						if(trustStore!=null) {
							params.issuerCertificate = (X509Certificate) trustStore.getCertificateBySubject(params.certificate.getIssuerX500Principal());
							if(params.issuerCertificate!=null) {
								params.issuerTrustStore = trustStore;
								log.debug("OCSP IssuerCertificate: use truststore config");
								break;
							}
						}
					}
					else if(CertificateSource.ALTERNATIVE_CONFIG.equals(s)) {
						KeyStore alternativeTrustStore = null;
						if(config.getAlternativeTrustStoreCA_path()!=null) {
							alternativeTrustStore = reader.getIssuerAlternativeTrustStore();
						}
						if(alternativeTrustStore!=null) {
							params.issuerCertificate = (X509Certificate) alternativeTrustStore.getCertificateBySubject(params.certificate.getIssuerX500Principal());
							if(params.issuerCertificate!=null) {
								params.issuerTrustStore = alternativeTrustStore;
								log.debug("OCSP IssuerCertificate: use alternative truststore config");
								break;
							}
						}
					}
					else if(CertificateSource.AUTHORITY_INFORMATION_ACCESS.equals(s)) {
						try {
							AuthorityInformationAccess aia = certificate.getAuthorityInformationAccess();
							if(aia!=null) {
								List<String> issuer = aia.getCAIssuers();
								if(issuer!=null && !issuer.isEmpty()) {
									List<Exception> listExceptions = new ArrayList<>();
									StringBuilder sbError = new StringBuilder();
									boolean find = false;
									for (String urlIssuer : issuer) {
										Map<String, byte[]> map = new HashMap<>();
										try {
											reader.readExternalResource(urlIssuer, map);
											if(!map.isEmpty()) {
												byte [] issCert = map.get(urlIssuer);
												if(issCert!=null) {
													Certificate cer = ArchiveLoader.load(issCert);
													if(cer!=null && cer.getCertificate()!=null) {
														params.issuerCertificate = cer.getCertificate().getCertificate();
														if(params.issuerCertificate!=null) {
															log.debug("OCSP IssuerCertificate: retrieved from external url '"+urlIssuer+"'");
															find = true;
															break;
														}
													}
												}
											}
										}catch(Throwable t) {
											String msgError = "[AuthorityInformationAccess-CAIssuer: "+urlIssuer+"] retrieve failed: "+t.getMessage();
											if(sbError.length()>0) {
												sbError.append("\n");
											}
											sbError.append(msgError);
											log.debug("OCSP "+msgError,t);
											listExceptions.add(new Exception(msgError,t));
										}
									}
									if(find) {
										break;
									}
									if(!listExceptions.isEmpty()) {
										throw new UtilsMultiException("OCSP IssuerCertificate retrieve failed: "+sbError.toString(), listExceptions.toArray(new Throwable[1]));
									}
								}
							}
						}catch(Throwable t) {
							throw new UtilsException(t.getMessage(),t);
						}
					}
				}
			}
			
			// comprendo signer
			KeyStore signerTrustStore = reader.getSignerTrustStore();
			if(config.getAliasCertificateSigner()!=null) {
				try {
					params.signerCertificate = (X509Certificate) signerTrustStore.getCertificate(config.getAliasCertificateSigner());
					if(params.signerCertificate==null) {
						throw new Exception("Not found");
					}
					log.debug("OCSP SignerCertificate: retrieved from truststore config (alias:"+config.getAliasCertificateSigner()+")");
				}catch(Throwable t) {
					throw new UtilsException("Get signer certificate failed: "+t.getMessage(),t);
				}
			}
			else {
				params.signerTrustStore = signerTrustStore;
				if(signerTrustStore!=null) {
					log.debug("OCSP SignerCertificate: use truststore config");
				}
			}
			
			// responder url
			params.responderURIs = new ArrayList<>();
			if(config.getResponderUrlSource()!=null && !config.getResponderUrlSource().isEmpty()) {
				for (CertificateSource s : config.getResponderUrlSource()) {
					if(CertificateSource.CONFIG.equals(s)) {
						throw new UtilsException("Unsupported");
					}
					else if(CertificateSource.ALTERNATIVE_CONFIG.equals(s)) {
						List<String> alternativeUrl = params.isCA ? config.getAlternativeResponderUrlCA() : config.getAlternativeResponderUrl();
						if(alternativeUrl!=null && !alternativeUrl.isEmpty()) {
							for (String url : alternativeUrl) {
								if(!params.responderURIs.contains(url)) {
									params.responderURIs.add(url);
									log.debug("OCSP ResponderURL: add alternative url '"+url+"'");
								}
							}
						}
					}
					else if(CertificateSource.AUTHORITY_INFORMATION_ACCESS.equals(s)) {
						try {
							AuthorityInformationAccess aia = certificate.getAuthorityInformationAccess();
							if(aia!=null) {
								List<String> ocsps = aia.getOCSPs();
								if(ocsps!=null && !ocsps.isEmpty()) {
									String urlOcsp = ocsps.get(0);
									if(!params.responderURIs.contains(urlOcsp)) {
										params.responderURIs.add(urlOcsp);
										log.debug("OCSP ResponderURL: add external url '"+urlOcsp+"'");
									}
								}
							}
						}catch(Throwable t) {
							throw new UtilsException(t.getMessage(),t);
						}
					}
				}
			}
			
			// https truststore
			params.httpsTrustStore = reader.getHttpsTrustStore();
			
			// https keystore
			params.httpsKeyStore = reader.getHttpsKeyStore();
		}
		
		return params;
	}
	
}
