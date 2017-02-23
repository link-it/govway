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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.herasaf.xacml.core.WritingException;
import org.herasaf.xacml.core.context.impl.AttributeType;
import org.herasaf.xacml.core.context.impl.RequestType;
import org.herasaf.xacml.core.context.impl.ResourceType;
import org.herasaf.xacml.core.dataTypeAttribute.impl.StringDataTypeAttribute;
import org.herasaf.xacml.core.function.Function;
import org.herasaf.xacml.core.function.impl.equalityPredicates.StringEqualFunction;
import org.herasaf.xacml.core.policy.Evaluatable;
import org.herasaf.xacml.core.policy.EvaluatableID;
import org.herasaf.xacml.core.policy.PolicyMarshaller;
import org.herasaf.xacml.core.policy.impl.ActionAttributeDesignatorType;
import org.herasaf.xacml.core.policy.impl.ActionMatchType;
import org.herasaf.xacml.core.policy.impl.ActionsType;
import org.herasaf.xacml.core.policy.impl.EvaluatableIDImpl;
import org.herasaf.xacml.core.policy.impl.ObjectFactory;
import org.herasaf.xacml.core.policy.impl.PolicyType;
import org.herasaf.xacml.core.policy.impl.ResourceAttributeDesignatorType;
import org.herasaf.xacml.core.policy.impl.ResourceMatchType;
import org.herasaf.xacml.core.policy.impl.ResourcesType;
import org.herasaf.xacml.core.policy.impl.TargetType;
import org.herasaf.xacml.core.simplePDP.OrderedMapBasedSimplePolicyRepository;
import org.slf4j.Logger;

/**
 * CachedMapBasedSimplePolicyRepository
 *
 * @author Bussu Giovanni (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CachedMapBasedSimplePolicyRepository extends
OrderedMapBasedSimplePolicyRepository {

	private Map<EvaluatableID, String> cacheMap;
	private MessageDigest md;
	private Logger log;
	/**
	 * Indicazione se usare la risorsa (con attribute id dato da RESOURCE_ATTRIBUTE_ID_TO_MATCH)
	 * o la action (con attribute id dato da ACTION_ATTRIBUTE_ID_TO_MATCH)
	 * per capire qual e' la policy da usare
	 */
	public static final boolean USE_RESOURCE_TO_MATCH_POLICY = true;
	
	
	public static final String RESOURCE_ATTRIBUTE_ID_TO_MATCH = "___resource-id___";
	public static final String ACTION_ATTRIBUTE_ID_TO_MATCH = "urn:oasis:names:tc:xacml:1.0:action:action-id";

	public CachedMapBasedSimplePolicyRepository(Logger log) throws PolicyException {
		super();
		this.cacheMap = new HashMap<EvaluatableID, String>();
		this.log = log;
		try {
			this.md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new PolicyException(e);
		}
	}

	public boolean existsPolicy(EvaluatableID id, String policyString) {
		return this.cacheMap.containsKey(id) && this.cacheMap.get(id).equals(this.hash(policyString));
	}

	public void deploy(Evaluatable evaluatable, String policyString) {
		if(super.individualEvaluatables.containsKey(evaluatable.getId())) {
			this.undeploy(evaluatable.getId());
		}
		super.deploy(evaluatable);
		this.cacheMap.put(evaluatable.getId(), this.hash(policyString));
	}

	@Override
	public void deploy(Evaluatable evaluatable) {
		if(USE_RESOURCE_TO_MATCH_POLICY) {
			addResourceToPolicy((PolicyType)evaluatable, evaluatable.getId().toString());
		} else {
			addActionToPolicy((PolicyType)evaluatable, evaluatable.getId().toString());
		}

		if(super.individualEvaluatables.containsKey(evaluatable.getId())) {
			this.undeploy(evaluatable.getId());
		}
		super.deploy(evaluatable);
		this.cacheMap.put(evaluatable.getId(), this.hash(unmarshallPolicy(evaluatable)));
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
		attributeDesignator.setAttributeId(ACTION_ATTRIBUTE_ID_TO_MATCH);
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
	}

	private static void addResourceToPolicy(PolicyType policy1, String key) {
		
		ObjectFactory factory = new ObjectFactory();
		
		TargetType target = factory.createTargetType();
		
		if(policy1.getTarget() != null) {
			target = policy1.getTarget();
		}

		org.herasaf.xacml.core.policy.impl.ResourceType resource =  factory.createResourceType();

		ResourceMatchType resourceMatch = factory.createResourceMatchType();
		Function function = new StringEqualFunction();
		resourceMatch.setMatchFunction(function);
		ResourceAttributeDesignatorType attributeDesignator = factory.createResourceAttributeDesignatorType();
		attributeDesignator.setMustBePresent(true);
		attributeDesignator.setAttributeId(RESOURCE_ATTRIBUTE_ID_TO_MATCH);
		attributeDesignator.setDataType(new StringDataTypeAttribute());
		resourceMatch.setResourceAttributeDesignator(attributeDesignator);
		org.herasaf.xacml.core.policy.impl.AttributeValueType attributeValue = new org.herasaf.xacml.core.policy.impl.AttributeValueType();
		attributeValue.getContent().add(key);
		attributeValue.setDataType(new StringDataTypeAttribute());
		resourceMatch.setAttributeValue(attributeValue);
		resource.getResourceMatches().add(resourceMatch);
		ResourcesType resources = factory.createResourcesType();
		resources.getResources().add(resource);
		target.setResources(resources);
		policy1.setTarget(target);
	}

	public String unmarshallPolicy(Evaluatable eval) {
		ByteArrayOutputStream baos = null;
		try{
			baos = new ByteArrayOutputStream();
			PolicyMarshaller.marshal(eval, baos);
			return baos.toString();
		} catch(WritingException e) {
			return null;
		} finally {
			if(baos != null) {
				try {
					baos.flush();
				} catch (IOException e) {}
				try {
					baos.close();
				} catch (IOException e) {}
			}
		}
	}
	
	public void deploy(Collection<Evaluatable> evaluatables, String policyString) {
		for (Evaluatable eval : evaluatables) {
			this.deploy(eval, this.hash(policyString));
		}
	}

	@Override
	public void undeploy(EvaluatableID evaluatable) {
		super.undeploy(evaluatable);
		this.cacheMap.remove(evaluatable);
	}

	public void undeploy(Collection<EvaluatableID> evaluatables, String policyString) {
		super.undeploy(evaluatables);
		for (EvaluatableID eval : evaluatables) {
			this.cacheMap.remove(eval);
		}
	}


	private String hash(String policyString) {
		String digest = toHex(this.md.digest(policyString.getBytes()));
		return digest;
	}

	private static String toHex(byte[] bytes) {
		BigInteger bi = new BigInteger(1, bytes);
		return String.format("%0" + (bytes.length << 1) + "X", bi);
	}
	
	@Override
	public List<Evaluatable> getEvaluatables(RequestType request) {
		try {
			String key = getKey(request);
			this.log.info("KEY: " + key);
			if(key != null) {
				EvaluatableIDImpl policyId = new EvaluatableIDImpl(key);
				Evaluatable eval = super.getEvaluatable(policyId);
				this.log.info("eval is null? " + (eval == null));
				if(eval != null) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					PolicyMarshaller.marshal(eval, baos);
					this.log.info("eval:" +new String(baos.toByteArray()));
				}
				return Arrays.asList(eval);
			} else {
				return new ArrayList<Evaluatable>();
			}
		} catch(Exception e){}
		return super.getEvaluatables(request);				
	}

	private String getKey(RequestType request) {
		try {
			if(USE_RESOURCE_TO_MATCH_POLICY) {
				for(ResourceType resource: request.getResources()) {
					for(AttributeType attribute: resource.getAttributes()) {
						if(attribute.getAttributeId().equals(RESOURCE_ATTRIBUTE_ID_TO_MATCH)) {
							return (String) attribute.getAttributeValues().get(0).getContent().get(0); 
						}
					}
				}
				return null;
			} else {
				for(AttributeType attribute: request.getAction().getAttributes()) {
					if(attribute.getAttributeId().equals(ACTION_ATTRIBUTE_ID_TO_MATCH)) {
						return (String) attribute.getAttributeValues().get(0).getContent().get(0); 
					}
				}
				return null;
			}
			
		} catch(Exception e) {
			return null;
		}
	}

}
