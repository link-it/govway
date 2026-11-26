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

package org.openspcoop2.security.message.wss4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.wss4j.common.crypto.AlgorithmSuite;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.handler.RequestData;

/**
 * Estende {@link WSS4JInInterceptor} consentendo di indicare, in ricezione, piu' algoritmi accettati
 * nelle proprieta' 'signatureAlgorithm' e 'signatureDigestAlgorithm', separati da virgola.
 *
 * WSS4J legge ciascuna proprieta' come un unico valore e lo inserisce nell'{@link AlgorithmSuite}
 * usato per validare la firma ricevuta: con un elenco, il valore non corrisponderebbe a nessun algoritmo
 * e ogni firma verrebbe rifiutata. L'{@link AlgorithmSuite} gestisce gia' un insieme di valori, quindi
 * e' sufficiente sostituire il valore con virgole con i singoli algoritmi.
 *
 * Con un solo valore, o senza le proprieta', il comportamento resta quello di WSS4J.
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WSS4JInInterceptorAlgorithmList extends WSS4JInInterceptor {

	public static final String SEPARATORE = ",";

	@Override
	protected void setAlgorithmSuites(SoapMessage message, RequestData data) throws WSSecurityException {
		super.setAlgorithmSuites(message, data);
		AlgorithmSuite algorithmSuite = data.getAlgorithmSuite();
		if(algorithmSuite!=null) {
			expand(algorithmSuite.getSignatureMethods(), algorithmSuite::addSignatureMethod);
			expand(algorithmSuite.getDigestAlgorithms(), algorithmSuite::addDigestAlgorithm);
		}
	}

	static void expand(Set<String> values, Consumer<String> add) {
		if(!containsList(values)) {
			return;
		}
		List<String> algorithms = split(values);
		values.clear();
		algorithms.forEach(add);
	}

	private static boolean containsList(Set<String> values) {
		return values!=null && values.stream().anyMatch(v -> v!=null && v.contains(SEPARATORE));
	}

	private static List<String> split(Set<String> values) {
		List<String> algorithms = new ArrayList<>();
		for (String v : values) {
			if(v!=null) {
				addAlgorithms(v, algorithms);
			}
		}
		return algorithms;
	}

	private static void addAlgorithms(String value, List<String> algorithms) {
		for (String a : value.split(SEPARATORE)) {
			String algorithm = a.trim();
			if(!algorithm.isEmpty() && !algorithms.contains(algorithm)) {
				algorithms.add(algorithm);
			}
		}
	}

}
