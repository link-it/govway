/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.xacml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.herasaf.xacml.core.SyntaxException;
import org.herasaf.xacml.core.api.PDP;
import org.herasaf.xacml.core.api.PolicyRetrievalPoint;
import org.herasaf.xacml.core.context.RequestMarshaller;
import org.herasaf.xacml.core.context.impl.RequestType;
import org.herasaf.xacml.core.context.impl.ResponseType;
import org.herasaf.xacml.core.context.impl.ResultType;
import org.herasaf.xacml.core.policy.Evaluatable;
import org.herasaf.xacml.core.policy.PolicyMarshaller;
import org.herasaf.xacml.core.policy.impl.EvaluatableIDImpl;
import org.herasaf.xacml.core.policy.impl.PolicyType;
import org.herasaf.xacml.core.simplePDP.MapBasedSimplePolicyRepository;
import org.herasaf.xacml.core.simplePDP.SimplePDPConfiguration;
import org.herasaf.xacml.core.simplePDP.SimplePDPFactory;
import org.herasaf.xacml.core.simplePDP.initializers.InitializerExecutor;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/**
 * PolicyDecisionPoint
 *
 * @author Bussu Giovanni (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PolicyDecisionPoint {

	private static final String SINGLE = "SINGLE";
	private Map<String, PDP> pdp;
	private boolean singlePDP;
	private Logger log;

	public static void runInitializers() {
		InitializerExecutor.runInitializers(); //Inizializza gli unmarshaller
	}
	
	public PolicyDecisionPoint() throws PolicyException {
		this(LoggerWrapperFactory.getLogger(PolicyDecisionPoint.class));
	}

	public PolicyDecisionPoint(Logger log) throws PolicyException {
		this(log, true);
	}

	public PolicyDecisionPoint(boolean singlePDP) throws PolicyException {
		this(LoggerWrapperFactory.getLogger(PolicyDecisionPoint.class), singlePDP);
	}

	public PolicyDecisionPoint(Logger log, boolean singlePDP) throws PolicyException {
		runInitializers();
		this.singlePDP=singlePDP;
		this.pdp = new HashMap<String, PDP>();
		this.log = log;
		if(singlePDP) {
			this.pdp.put(SINGLE, this.newPDP());
		}
	}
	
	private PDP newPDP() throws PolicyException {
		SimplePDPConfiguration configuration = new SimplePDPConfiguration();
		PolicyRetrievalPoint policyRetrievalPoint = new CachedMapBasedSimplePolicyRepository(this.log);
		configuration.setPolicyRetrievalPoint(policyRetrievalPoint);
		return SimplePDPFactory.getSimplePDP(configuration);
	}

	public List<ResultType> evaluate(String requestString) throws PolicyException {
		RequestType request;
		try {
			request = _unmarshalRequest(requestString);
		} catch (SyntaxException e) {
			throw new PolicyException(e);
		}
		return this._evaluate(request);
	}

	public List<ResultType> evaluate(XacmlRequest request) throws PolicyException {
		return this.evaluate(request.getXacmlRequest());
	}
	
	public List<ResultType> evaluate(RequestType request) throws PolicyException {
		return this._evaluate(request);
	}

	public static RequestType _unmarshalRequest(String requestString) throws PolicyException, SyntaxException {
		ByteArrayInputStream bais = null;
		try{
			InitializerExecutor.runInitializers(); //Inizializza gli unmarshaller
			bais = new ByteArrayInputStream(requestString.getBytes());
			return RequestMarshaller.unmarshal(bais);
		} finally {
			if(bais != null) {
				try {
					bais.close();
				} catch (IOException e) {}
			}
		}
	}

	public static boolean isValidRequest(String requestString) throws PolicyException {
		try{
			_unmarshalRequest(requestString);
		} catch(SyntaxException e) {
			return false;
		}
		return true;
	}

	public static Evaluatable _unmarshalPolicy(String policyString) throws PolicyException, SyntaxException {
		ByteArrayInputStream bais = null;
		try{
			bais = new ByteArrayInputStream(policyString.getBytes());
			InitializerExecutor.runInitializers(); //Inizializza gli unmarshaller
			return PolicyMarshaller.unmarshal(bais);
		} finally {
			if(bais != null) {
				try {
					bais.close();
				} catch (IOException e) {}
			}
		}
	}

	public void addPolicy(Evaluatable eval, String key) throws PolicyException {
		this._addPolicy(eval, key);
	}

	public void addPolicy(String policyString, String key) throws PolicyException {
		try {
			Evaluatable eval;
			eval = _unmarshalPolicy(policyString);
			((PolicyType)eval).setPolicyId(new EvaluatableIDImpl(key));
			this._addPolicy(eval, key);
		} catch (SyntaxException e) {
			throw new PolicyException(e);
		}
	}

	private void _addPolicy(Evaluatable eval, String key) throws PolicyException {
		
		EvaluatableIDImpl policyId = new EvaluatableIDImpl(key);
		((PolicyType)eval).setPolicyId(policyId);

		if(this.singlePDP) {
			PDP pdp = this.getPDP(SINGLE);
			MapBasedSimplePolicyRepository repo = (MapBasedSimplePolicyRepository)pdp.getPolicyRepository();
			repo.deploy(eval);
		} else {
			PDP newPDP = this.newPDP();
			MapBasedSimplePolicyRepository repo = (MapBasedSimplePolicyRepository)newPDP.getPolicyRepository();
			repo.deploy(eval);
			this.pdp.put(key, newPDP);
		}
	}

	private List<ResultType> _evaluate(RequestType request) throws PolicyException {
		PDP pdp = (this.singlePDP) ? this.getPDP(SINGLE): this.getPDP(request);
		ResponseType response = pdp.evaluate(request);
		return response.getResults();
	}

	private PDP getPDP(RequestType request) throws PolicyException {
		if(request == null) {
			throw new PolicyException("request non puo essere null");
		}
		
		if(request.getAction() == null) {
			throw new PolicyException("request.action non puo essere null");
		}
		
		if(request.getAction().getAttributes() == null || request.getAction().getAttributes().isEmpty()) {
			throw new PolicyException("request.action.attributes non puo essere null o vuoto");
		}
		
		if(request.getAction().getAttributes().get(0).getAttributeValues() == null || request.getAction().getAttributes().get(0).getAttributeValues().isEmpty()) {
			throw new PolicyException("request.action.attributes[0].attributeValues non puo essere null o vuoto");
		}
		
		if(request.getAction().getAttributes().get(0).getAttributeValues().get(0).getContent() == null || request.getAction().getAttributes().get(0).getAttributeValues().get(0).getContent().isEmpty()) {
			throw new PolicyException("request.action.attributes[0].attributeValues[0].content non puo essere null o vuoto");
		}
		
		String key = (String) request.getAction().getAttributes().get(0).getAttributeValues().get(0).getContent().get(0);
		return this.pdp.get(key);
	}
	
	private PDP getPDP(String key) {
		return this.pdp.get(key);
	}

}
