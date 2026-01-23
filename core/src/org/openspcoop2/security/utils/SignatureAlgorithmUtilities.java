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

package org.openspcoop2.security.utils;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.certificate.KeyUtils;

/**     
 * SignatureAlgorithmUtilities
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SignatureAlgorithmUtilities {

	private SignatureAlgorithmUtilities() {}
	
	public static final String LABEL_DEFINITO_HEANDER = "Definito nel token (alg)";
	public static final String VALUE_DEFINITO_HEANDER = "alg";
	
	public static List<String> getValuesSignatureAlgorithm(boolean symmetric) {
		return getValuesSignatureAlgorithm(symmetric, false);
	}
	public static List<String> getValuesSignatureAlgorithm(boolean symmetric, boolean addDefinitoHeader) {
		List<String> l = new ArrayList<>();
		if(addDefinitoHeader) {
			l.add(VALUE_DEFINITO_HEANDER);
		}
		org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm [] tmp = org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.values();
		for (int i = 0; i < tmp.length; i++) {
			if(org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm.NONE.equals(tmp[i])) {
				continue;
			}
			if(symmetric) {
				if(tmp[i].name().toLowerCase().startsWith("hs")) {
					l.add(tmp[i].name());
				}
			}
			else {
				if(!tmp[i].name().toLowerCase().startsWith("hs")) {
					l.add(tmp[i].name());
				}
			}
		}
		return l;
	}

	public static List<String> getLabelsSignatureAlgorithm(boolean symmetric) {
		return getLabelsSignatureAlgorithm(symmetric, false);
	}
	public static List<String> getLabelsSignatureAlgorithm(boolean symmetric, boolean addDefinitoHeader) {
		List<String> l = getValuesSignatureAlgorithm(symmetric, false);
		List<String> labels = new ArrayList<>();
		if(addDefinitoHeader) {
			labels.add(LABEL_DEFINITO_HEANDER);
		}
		for (String value : l) {
			if(value.contains("_")) {
				String t = "" + value;
				while(t.contains("_")) {
					t = t.replace("_", "-");
				}
				labels.add(t);
			}
			else {
				labels.add(value);
			}
		}
		return labels;
	}
	
	public static String covertToKeyPairAlgorithm(String signatureAlgorithm) {
		if(signatureAlgorithm==null) {
			return null;
		}
		if(signatureAlgorithm.toLowerCase().startsWith("es")) {
			return KeyUtils.ALGO_EC;
		}
		else if(signatureAlgorithm.toLowerCase().startsWith("rs") || signatureAlgorithm.toLowerCase().startsWith("ps")) {
			return KeyUtils.ALGO_RSA;
		}
		return null;
	}
}
