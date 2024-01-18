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
package org.openspcoop2.utils.certificate;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.UtilsException;

/**
 * PEMArchive
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PEMArchive implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private PEMReader reader;
	private String algo;
	private String keyPassword;
	
	private transient Boolean initialized;
	private transient PrivateKey privateKey;
	private transient PublicKey publicKey;
	private transient List<CertificateInfo> certificates = null;
	
	public PEMArchive(byte[] pem) throws UtilsException {
		this(pem, KeyUtils.ALGO_RSA, null);
	}
	public PEMArchive(byte[] pem, String keyPassword) throws UtilsException {
		this(pem, KeyUtils.ALGO_RSA, keyPassword);
	}
	public PEMArchive(byte[] pem, String algo, String keyPassword) throws UtilsException {
		this.reader = new PEMReader(pem);
		this.algo = algo;
		this.keyPassword = keyPassword;
		this.init();
	}
	
	public PEMArchive(String pem) throws UtilsException {
		this(pem, KeyUtils.ALGO_RSA, null);
	}
	public PEMArchive(String pem, String keyPassword) throws UtilsException {
		this(pem, KeyUtils.ALGO_RSA, keyPassword);
	}
	public PEMArchive(String pem, String algo, String keyPassword) throws UtilsException {
		this.reader = new PEMReader(pem);
		this.algo = algo;
		this.keyPassword = keyPassword;
		this.init();
	}
	
	private synchronized void init() throws UtilsException {
		
		if(this.initialized==null || !this.initialized.booleanValue()) {
		
			KeyUtils keyUtils = KeyUtils.getInstance(this.algo);
			
			initPrivateKey(keyUtils);
			
			initPublicKey(keyUtils);
						
			initCertificates();
			
			this.initialized = true;
			
		}
	}
	private void initPrivateKey(KeyUtils keyUtils) throws UtilsException {
		if(this.reader.getPrivateKey()!=null) {
			if(this.keyPassword!=null) {
				try {
					this.privateKey = keyUtils.getPrivateKey(this.reader.getPrivateKey().getBytes(), this.keyPassword);
				}catch(Exception e) {
					throw new UtilsException("Load encrypted private key failed: "+e.getMessage(),e);
				}
			}
			else {
				try {
					this.privateKey = keyUtils.getPrivateKey(this.reader.getPrivateKey().getBytes());
				}catch(Exception e) {
					throw new UtilsException("Load private key failed: "+e.getMessage(),e);
				}
			}
		}
	}
	private void initPublicKey(KeyUtils keyUtils) throws UtilsException {
		if(this.reader.getPublicKey()!=null) {
			try {
				this.publicKey = keyUtils.getPublicKey(this.reader.getPublicKey().getBytes());
			}catch(Exception e) {
				throw new UtilsException("Load public key failed: "+e.getMessage(),e);
			}
		}
	}
	private void initCertificates() throws UtilsException {
		if(this.reader.getCertificates()!=null && !this.reader.getCertificates().isEmpty()) {
			this.certificates = new ArrayList<>();
			for (String c : this.reader.getCertificates()) {
				try {
					CertificateInfo cInfo = ArchiveLoader.load(c.getBytes()).getCertificate();
					this.certificates.add(cInfo);
				}catch(Exception e) {
					throw new UtilsException("Load x509 failed: "+e.getMessage(),e);
				}
			}
		}
	}
	
	private void checkInit() throws UtilsException {
		if(this.initialized==null || !this.initialized.booleanValue()) {
			this.init();
		}
	}
	
	public PrivateKey getPrivateKey() throws UtilsException {
		checkInit();
		return this.privateKey;
	}
	public PublicKey getPublicKey() throws UtilsException {
		checkInit();
		return this.publicKey;
	}
	public List<CertificateInfo> getCertificates() throws UtilsException {
		checkInit();
		return this.certificates;
	}
}
