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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.herasaf.xacml.core.WritingException;
import org.herasaf.xacml.core.context.impl.RequestType;
import org.herasaf.xacml.core.policy.Evaluatable;
import org.herasaf.xacml.core.policy.EvaluatableID;
import org.herasaf.xacml.core.policy.PolicyMarshaller;
import org.herasaf.xacml.core.policy.impl.EvaluatableIDImpl;
import org.herasaf.xacml.core.simplePDP.OrderedMapBasedSimplePolicyRepository;
import org.openspcoop2.utils.xacml.PolicyException;

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

	public CachedMapBasedSimplePolicyRepository() throws PolicyException {
		super();
		this.cacheMap = new HashMap<EvaluatableID, String>();
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
		if(super.individualEvaluatables.containsKey(evaluatable.getId())) {
			this.undeploy(evaluatable.getId());
		}
		super.deploy(evaluatable);
		this.cacheMap.put(evaluatable.getId(), this.hash(unmarshallPolicy(evaluatable)));
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
			String key = (String) request.getAction().getAttributes().get(0).getAttributeValues().get(0).getContent().get(0);
			EvaluatableIDImpl policyId = new EvaluatableIDImpl(key);
	
			Evaluatable eval = super.getEvaluatable(policyId);
			
			return Arrays.asList(eval);
		} catch(Exception e){}
		return super.getEvaluatables(request);				
	}

}
