/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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
import org.herasaf.xacml.core.dataTypeAttribute.impl.StringDataTypeAttribute;
import org.herasaf.xacml.core.function.Function;
import org.herasaf.xacml.core.function.impl.equalityPredicates.StringEqualFunction;
import org.herasaf.xacml.core.policy.Evaluatable;
import org.herasaf.xacml.core.policy.PolicyMarshaller;
import org.herasaf.xacml.core.policy.impl.ActionAttributeDesignatorType;
import org.herasaf.xacml.core.policy.impl.ActionMatchType;
import org.herasaf.xacml.core.policy.impl.ActionsType;
import org.herasaf.xacml.core.policy.impl.EvaluatableIDImpl;
import org.herasaf.xacml.core.policy.impl.ObjectFactory;
import org.herasaf.xacml.core.policy.impl.PolicyType;
import org.herasaf.xacml.core.policy.impl.TargetType;
import org.herasaf.xacml.core.simplePDP.MapBasedSimplePolicyRepository;
import org.herasaf.xacml.core.simplePDP.SimplePDPConfiguration;
import org.herasaf.xacml.core.simplePDP.SimplePDPFactory;
import org.herasaf.xacml.core.simplePDP.initializers.InitializerExecutor;
import org.herasaf.xacml.core.targetMatcher.TargetMatcher;
import org.openspcoop2.utils.xacml.PolicyException;

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
	private TargetMatcher matcher;
	
	public PolicyDecisionPoint(boolean singlePdP) throws PolicyException {
		this(singlePdP, null);
	}

	public PolicyDecisionPoint(boolean singlePDP, TargetMatcher matcher) throws PolicyException {
		this.singlePDP=singlePDP;
		this.matcher = matcher;
		this.pdp = new HashMap<String, PDP>();

		if(singlePDP) {
			this.pdp.put(SINGLE, this.newPDP());
		}
	}
	
	private PDP newPDP() throws PolicyException {
		SimplePDPConfiguration configuration = new SimplePDPConfiguration();
		configuration.setTargetMatcher(this.matcher);
		PolicyRetrievalPoint policyRetrievalPoint = new CachedMapBasedSimplePolicyRepository();
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
		
		addActionToPolicy((PolicyType)eval, key);
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
	

	private static void addActionToPolicy(PolicyType policy1, String key) {
		
		ObjectFactory factory = new ObjectFactory();
		
		TargetType target = factory.createTargetType();
		
		if(policy1.getTarget() != null) {
			target = policy1.getTarget();
		}

		org.herasaf.xacml.core.policy.impl.ActionType action =  factory.createActionType();

		ActionMatchType actionMatch = factory.createActionMatchType();
		Function function = new StringEqualFunction();
		actionMatch.setMatchFunction(function);
		ActionAttributeDesignatorType attributeDesignator = factory.createActionAttributeDesignatorType();
		attributeDesignator.setMustBePresent(true);
		attributeDesignator.setAttributeId("urn:oasis:names:tc:xacml:1.0:action:action-id");
		attributeDesignator.setDataType(new StringDataTypeAttribute());
		actionMatch.setActionAttributeDesignator(attributeDesignator);
		org.herasaf.xacml.core.policy.impl.AttributeValueType attributeValue = new org.herasaf.xacml.core.policy.impl.AttributeValueType();
		attributeValue.getContent().add(key);
		attributeValue.setDataType(new StringDataTypeAttribute());
		actionMatch.setAttributeValue(attributeValue);
		action.getActionMatches().add(actionMatch);
		ActionsType actions = factory.createActionsType();
		actions.getActions().add(action);
		target.setActions(actions);
		policy1.setTarget(target);
		EvaluatableIDImpl policyId = new EvaluatableIDImpl(key);
		policy1.setPolicyId(policyId);
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
