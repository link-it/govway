/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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

package org.openspcoop2.security.keystore;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509CRL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.openspcoop2.utils.transport.http.SSLConfig;

/**
 * SSLContentFingerprint
 *
 * Impronta (SHA-256) del contenuto di keystore, truststore e CRL di una configurazione SSL:
 * alias e certificati dei keystore (per le chiavi private solo la catena dei certificati) e CRL.
 *
 * Consente ai pool di connessioni di riconoscere un aggiornamento del contenuto dei file, a parità di configurazione:
 * la stessa impronta indica lo stesso contenuto anche se gli oggetti sono stati riletti (es. dopo un reset della cache).
 *
 * L'impronta di ogni keystore e certstore viene calcolata una sola volta per istanza
 * (le istanze sono mantenute nella cache dei keystore).
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SSLContentFingerprint {

	private SSLContentFingerprint() {}

	private static final String ALGORITHM = "SHA-256";

	/** Impronte calcolate per istanza: la chiave è l'oggetto stesso (KeyStore e CertStore non ridefiniscono equals/hashCode) */
	private static final Map<Object, String> cache = Collections.synchronizedMap(new WeakHashMap<>());

	public static String compute(SSLConfig sslConfig) {
		if(sslConfig==null) {
			return null;
		}
		String ks = getFingerprint(sslConfig.getKeyStore());
		String ts = getFingerprint(sslConfig.getTrustStore());
		String crls = getFingerprint(sslConfig.getTrustStoreCRLs());
		if(ks==null && ts==null && crls==null) {
			return null;
		}
		MessageDigest md = newDigest();
		update(md, "keystore", ks);
		update(md, "truststore", ts);
		update(md, "crls", crls);
		return HexFormat.of().formatHex(md.digest());
	}

	private static String getFingerprint(Object store) {
		if(store==null) {
			return null;
		}
		return cache.computeIfAbsent(store, SSLContentFingerprint::computeFingerprint);
	}

	private static String computeFingerprint(Object store) {
		try {
			MessageDigest md = newDigest();
			if(store instanceof KeyStore ks) {
				updateKeyStore(md, ks);
			}
			else if(store instanceof CertStore cs) {
				updateCertStore(md, cs);
			}
			return HexFormat.of().formatHex(md.digest());
		}catch(Exception e) {
			// contenuto non leggibile: l'impronta identifica la sola istanza, che cambia a ogni rilettura dalla cache
			return "instance-"+System.identityHashCode(store);
		}
	}
	
	private static void updateKeyStore(MessageDigest md, KeyStore ks) throws KeyStoreException, CertificateEncodingException {
		List<String> aliases = Collections.list(ks.aliases());
		Collections.sort(aliases);
		for (String alias : aliases) {
			update(md, "alias", alias);
			// per le chiavi private solo la catena dei certificati
			Certificate[] chain = ks.getCertificateChain(alias);
			if(chain==null) {
				Certificate certificate = ks.getCertificate(alias);
				chain = certificate!=null ? new Certificate[] {certificate} : new Certificate[0];
			}
			for (Certificate certificate : chain) {
				md.update(certificate.getEncoded());
			}
		}
	}
	
	private static void updateCertStore(MessageDigest md, CertStore cs) throws CertStoreException, CRLException {
		Collection<? extends CRL> crls = cs.getCRLs(null);
		List<String> hashCrls = new ArrayList<>();
		if(crls!=null) {
			for (CRL crl : crls) {
				hashCrls.add(crl instanceof X509CRL x509crl ? HexFormat.of().formatHex(newDigest().digest(x509crl.getEncoded())) : crl.toString());
			}
		}
		// l'ordine delle CRL non è significativo
		Collections.sort(hashCrls);
		for (String hashCrl : hashCrls) {
			update(md, "crl", hashCrl);
		}
	}
	
	private static MessageDigest newDigest() {
		try {
			return MessageDigest.getInstance(ALGORITHM);
		}catch(Exception e) {
			throw new IllegalStateException(e.getMessage(),e);
		}
	}

	private static void update(MessageDigest md, String label, String value) {
		md.update(label.getBytes(StandardCharsets.UTF_8));
		md.update((byte)'=');
		if(value!=null) {
			md.update(value.getBytes(StandardCharsets.UTF_8));
		}
		md.update((byte)'\n');
	}
}
