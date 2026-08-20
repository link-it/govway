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

package org.openspcoop2.utils.security;

import java.security.spec.MGF1ParameterSpec;

import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

/**
 * OAEPUtils
 *
 * Utilita' per rendere esplicita la parametrizzazione OAEP di una trasformazione.
 *
 * La trasformazione 'RSA/ECB/OAEPWithSHA-256AndMGF1Padding' indica il digest utilizzato dalla funzione hash di OAEP,
 * ma non quello utilizzato dalla mask generation function MGF1, ed i provider JCE lo interpretano in modo differente:
 * SunJCE utilizza SHA-1, che e' il default di 'OAEPParameterSpec', mentre BouncyCastle utilizza il medesimo digest
 * indicato nel nome. I due provider non sono quindi interoperabili su quella trasformazione, ed il formato prodotto
 * dipende da quale dei due risolve la richiesta, quindi dall'ordine di registrazione dei provider nella jvm.
 * Indicando i parametri in modo esplicito il comportamento diventa deterministico.
 *
 * NOTA: i parametri restituiti riproducono la semantica di BouncyCastle, cioe' MGF1 sul medesimo digest indicato nella
 * trasformazione, che e' anche quella richiesta da RFC 8017 e da RFC 7518. I contenuti gia' cifrati restano quindi leggibili.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OAEPUtils {

	private OAEPUtils(){}

	private static final String PREFISSO_OAEP = "OAEPWith";
	private static final String SUFFISSO_OAEP = "AndMGF1Padding";
	private static final String MGF = "MGF1";

	/**
	 * Restituisce i parametri OAEP corrispondenti alla trasformazione indicata, oppure null se la trasformazione non
	 * utilizza un padding OAEP che nomini esplicitamente il digest.
	 *
	 * Vengono gestite le sole trasformazioni nella forma '&lt;algoritmo&gt;/&lt;modo&gt;/OAEPWith&lt;digest&gt;AndMGF1Padding'.
	 * Per ogni altra forma, compreso il padding 'OAEPPadding' che non nomina alcun digest, viene restituito null ed il
	 * provider utilizza quindi i propri default, come avveniva in precedenza.
	 */
	public static OAEPParameterSpec getOaepParameterSpec(String trasformazione) {

		if(trasformazione==null) {
			return null;
		}

		int inizioPadding = trasformazione.lastIndexOf('/');
		String padding = inizioPadding>=0 ? trasformazione.substring(inizioPadding+1) : trasformazione;

		if(!padding.startsWith(PREFISSO_OAEP) || !padding.endsWith(SUFFISSO_OAEP)) {
			return null;
		}

		String digest = padding.substring(PREFISSO_OAEP.length(), padding.length()-SUFFISSO_OAEP.length());
		if(digest.length()<=0) {
			return null;
		}

		return new OAEPParameterSpec(digest, MGF, new MGF1ParameterSpec(digest), PSource.PSpecified.DEFAULT);
	}

}
