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
package org.openspcoop2.utils.certificate.ocsp;

import java.security.cert.CertStore;
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
import org.openspcoop2.utils.certificate.CRLCertstore;
import org.openspcoop2.utils.certificate.CRLDistributionPoint;
import org.openspcoop2.utils.certificate.CRLDistributionPoints;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.certificate.KeystoreType;

/**
 * OCSPRequestParams
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CRLParams {

	private KeyStore crlTrustStore;
	private CertStore crlCertstore;
	
	
	public KeyStore getCrlTrustStore() {
		return this.crlTrustStore;
	}
	public void setCrlTrustStore(KeyStore crlTrustStore) {
		this.crlTrustStore = crlTrustStore;
	}
	public CertStore getCrlCertstore() {
		return this.crlCertstore;
	}
	public void setCrlCertstore(CertStore crlCertstore) {
		this.crlCertstore = crlCertstore;
	}
	
	public static CRLParams build(LoggerBuffer log, X509Certificate certificate, String crlInputConfig, KeyStore trustStore, OCSPConfig config, IOCSPResourceReader reader) throws UtilsException, UtilsMultiException {
		CertificateInfo cer = new CertificateInfo(certificate, "ocspVerifica");
		return build(log, cer, crlInputConfig, trustStore, config, reader);
	}
	public static CRLParams build(LoggerBuffer log, CertificateInfo certificate, String crlInputConfig, KeyStore trustStore, OCSPConfig config, IOCSPResourceReader reader) throws UtilsException, UtilsMultiException {
	
		if(config==null) {
			throw new UtilsException("Param config is null");
		}
		if(certificate==null) {
			throw new UtilsException("Param certificate is null");
		}
		
		
		// comprendo path CRL
		List<String> crlPaths = new ArrayList<>();
		Map<String, byte[]> localResources = new HashMap<>();
		
		if(config.getCrlSource()!=null && !config.getCrlSource().isEmpty()) {
			
			List<Throwable> listExceptions = new ArrayList<>();
						
			for (CertificateSource s : config.getCrlSource()) {
				if(CertificateSource.CONFIG.equals(s)) {
					if(crlInputConfig!=null) {
						List<String> l = CRLCertstore.readCrlPaths(crlInputConfig);
						if(l!=null && !l.isEmpty()) {
							for (String crlPath : l) {
								String p = crlPath.trim();
								if(!crlPaths.contains(p)) {
									crlPaths.add(p);
									log.debug("OCSP-CRL: add config path '"+p+"'");
								}
							}
						}
					}
					if(!crlPaths.isEmpty()) {
						break;
					}
				}
				else if(CertificateSource.ALTERNATIVE_CONFIG.equals(s)) {
					List<String> l = null;
					if(config.getCrlAlternative()!=null) {
						l = config.getCrlAlternative();
					}
					if(l!=null && !l.isEmpty()) {
						for (String crlPath : l) {
							String p = crlPath.trim();
							if(!crlPaths.contains(p)) {
								crlPaths.add(p);
								log.debug("OCSP-CRL: add alternative config path '"+p+"'");
							}
						}
					}
					if(!crlPaths.isEmpty()) {
						break;
					}
				}
				else if(CertificateSource.AUTHORITY_INFORMATION_ACCESS.equals(s)) {
					try {
						CRLDistributionPoints crls = certificate.getCRLDistributionPoints();
						if(crls!=null) {
							List<CRLDistributionPoint> crlsList = crls.getCRLDistributionPoints();
							if(crlsList!=null && !crlsList.isEmpty()) {
								for (CRLDistributionPoint crlDP : crlsList) {
									List<String> l = crlDP.getDistributionPointNames();
									if(l!=null) {
										for (String crlPath : l) {
											if(!crlPaths.contains(crlPath)) {
												try {
													Map<String, byte[]> map = new HashMap<>();
													reader.readExternalResource(crlPath, map);
													byte [] crl = null;
													if(!map.isEmpty()) {
														crl = map.get(crlPath);
													}
													if(crl==null || crl.length<=0) {
														throw new Exception("empty resource");
													}
													localResources.put(crlPath, crl);
													crlPaths.add(crlPath);
													log.debug("OCSP-CRL: add external resource '"+crlPath+"'");
												}catch(Throwable t) {
													String msgError = "[crl: "+crlPath+"] retrieve failed: "+t.getMessage();
													log.debug("OCSP-CRL "+msgError,t);
													listExceptions.add(new Exception(msgError,t));
												}
											}
										}
									}
								}
							}
						}						
					}catch(Throwable t) {
						throw new UtilsException(t.getMessage(),t);
					}
					if(!crlPaths.isEmpty()) {
						break;
					}
				}
			}
			
			if(crlPaths.isEmpty()) {
				boolean reject = false;
				boolean isCA = false;
				try {
					isCA = certificate.isCA();
				}catch(Throwable t) {
					throw new UtilsException(t.getMessage(),t);
				}
				if(isCA) {
					reject = config.isRejectsCAWithoutCRL();
				}
				else {
					reject = config.isRejectsCertificateWithoutCRL();
				}
				if(reject) {
					if(!listExceptions.isEmpty()) {
						if(listExceptions.size()==1) {
							Throwable t = listExceptions.get(0);
							if(t!=null) {
								throw new UtilsException("Crl retrieve failed; "+t.getMessage(),t);
							}
						}
						UtilsMultiException multi = new UtilsMultiException("Crl retrieve failed", listExceptions.toArray(new Throwable[1]));
						throw new UtilsException("Crl retrieve failed;\n"+multi.getMultiMessage(),multi);
					}
					else {
						throw new UtilsException("Crl retrieve failed");
					}
				}
			}
			
		}
		
		KeyStore crlTrustStore = null;
		if(!crlPaths.isEmpty()) {
			
			// comprendo issuer del certificato da validare
			if(config.getCrlTrustStoreSource()!=null && !config.getCrlTrustStoreSource().isEmpty()) {
				for (CertificateSource s : config.getCrlTrustStoreSource()) {
					if(CertificateSource.CONFIG.equals(s)) {
						if(trustStore!=null) {
							try {
								if(crlTrustStore==null) {
									java.security.KeyStore ks = java.security.KeyStore.getInstance(KeystoreType.JKS.getNome());
									ks.load(null, null);
									crlTrustStore = new KeyStore(ks);
								}
							}catch(Throwable t) {
								String msgError = "OCSP-CRL [crlssuer: CONFIG] retrieve failed: "+t.getMessage();
								log.debug(msgError,t);
							}
							if(crlTrustStore!=null) {
								crlTrustStore.putAllCertificate(trustStore, false);
							}
							log.debug("OCSP-CRL: add certificate in truststore config");
						}
					}
					else if(CertificateSource.ALTERNATIVE_CONFIG.equals(s)) {
						KeyStore alternativeTrustStore = null;
						if(config.getAlternativeTrustStoreCRL_path()!=null) {
							alternativeTrustStore = reader.getCrlAlternativeTrustStore();
						}
						if(alternativeTrustStore!=null) {
							try {
								if(crlTrustStore==null) {
									java.security.KeyStore ks = java.security.KeyStore.getInstance(KeystoreType.JKS.getNome());
									ks.load(null, null);
									crlTrustStore = new KeyStore(ks);
								}
							}catch(Throwable t) {
								String msgError = "OCSP-CRL [crlssuer: ALTERNATIVE_CONFIG] retrieve failed: "+t.getMessage();
								log.debug(msgError,t);
							}
							if(crlTrustStore!=null) {
								crlTrustStore.putAllCertificate(alternativeTrustStore, false);
							}
							log.debug("OCSP-CRL: add certificate in alternative truststore config");
						}
					}
					else if(CertificateSource.AUTHORITY_INFORMATION_ACCESS.equals(s)) {
						try {
							CRLDistributionPoints crls = certificate.getCRLDistributionPoints();
							if(crls!=null) {
								List<CRLDistributionPoint> crlsList = crls.getCRLDistributionPoints();
								int indexCrl = 0;
								if(crlsList!=null && !crlsList.isEmpty()) {
									for (CRLDistributionPoint crlDP : crlsList) {
										List<String> l = crlDP.getCRLIssuers();
										if(l!=null) {
											for (String crlPath : l) {
												if(!crlPaths.contains(crlPath)) {
													try {
														Map<String, byte[]> map = new HashMap<>();
														reader.readExternalResource(crlPath, map);
														byte [] issuer = null;
														if(!map.isEmpty()) {
															issuer = map.get(crlPath);
														}
														if(issuer==null || issuer.length<=0) {
															throw new Exception("empty resource");
														}
														Certificate cer = ArchiveLoader.load(issuer);
														if(cer!=null && cer.getCertificate()!=null) {
															if(crlTrustStore==null) {
																java.security.KeyStore ks = java.security.KeyStore.getInstance(KeystoreType.JKS.getNome());
																ks.load(null, null);
																crlTrustStore = new KeyStore(ks);
															}
															crlTrustStore.putCertificate("crlIssuerCert-"+indexCrl, cer.getCertificate().getCertificate(), false);
															indexCrl++;
															log.debug("OCSP-CRL: add certificate retrieved from '"+crlPath+"'");
														}
													}catch(Throwable t) {
														String msgError = "OCSP-CRL [crlssuer: "+crlPath+"] retrieve failed: "+t.getMessage();
														log.debug(msgError,t);
													}
												}
											}
										}
									}
								}
							}						
						}catch(Throwable t) {
							throw new UtilsException(t.getMessage(),t);
						}
						try {
							AuthorityInformationAccess aia = certificate.getAuthorityInformationAccess();
							if(aia!=null) {
								List<String> issuer = aia.getCAIssuers();
								int indexAia = 0;
								if(issuer!=null && !issuer.isEmpty()) {
									for (String urlIssuer : issuer) {
										Map<String, byte[]> map = new HashMap<>();
										reader.readExternalResource(urlIssuer, map);
										if(!map.isEmpty()) {
											byte [] issCert = map.get(urlIssuer);
											if(issCert!=null) {
												Certificate cer = ArchiveLoader.load(issCert);
												if(cer!=null && cer.getCertificate()!=null) {
													if(crlTrustStore==null) {
														java.security.KeyStore ks = java.security.KeyStore.getInstance(KeystoreType.JKS.getNome());
														ks.load(null, null);
														crlTrustStore = new KeyStore(ks);
													}
													crlTrustStore.putCertificate("aiaIssuerCert-"+indexAia, cer.getCertificate().getCertificate(), false);
													indexAia++;
													log.debug("OCSP-CRL-AIA: add certificate retrieved from '"+urlIssuer+"'");
												}
											}
										}
									}
								}
							}
						}catch(Throwable t) {
							throw new UtilsException(t.getMessage(),t);
						}
					}
				}
			}
			
		}
		
		CRLParams params = new CRLParams();
		if(!crlPaths.isEmpty()) {
			params.crlCertstore = reader.readCRL(crlPaths, localResources).getCertStore();
			
/**			boolean newTrustStore = false;
//			if(crlTrustStore==null) {
//				try {
//					java.security.KeyStore ks = java.security.KeyStore.getInstance(KeystoreType.JKS.getNome());
//					ks.load(null, null);
//					crlTrustStore = new KeyStore(ks);
//					newTrustStore = true;
//				}catch(Throwable t) {
//					throw new UtilsException(t.getMessage(),t);
//				}
//			}
//			
//			try {
//				if(certificate.isCA()) {
//					if(newTrustStore || (!params.crlTrustStore.existsCertificateBySubject(certificate.getCertificate().getSubjectX500Principal()))) {
//						crlTrustStore.putCertificate("ca", certificate.getCertificate(), false);
//					}
//				}
//			}catch(Throwable t) {
//				throw new UtilsException(t.getMessage(),t);
//			}*/
			
			params.crlTrustStore = crlTrustStore;
		}
		
		return params;
		
	}
	
}
