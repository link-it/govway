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
package org.openspcoop2.protocol.sdk.archive;

import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * AbstractArchiveGenericProperties
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractArchiveGenericProperties implements IArchiveObject {

	public static String buildKey(String object, String tipologiaPolicy, String nomePolicy) throws ProtocolException{
		
		if(tipologiaPolicy==null){
			throw new ProtocolException("tipologiaPolicy non fornito");
		}
		if(nomePolicy==null){
			throw new ProtocolException("nomePolicy non fornito");
		}
		
		StringBuilder bf = new StringBuilder();
		bf.append(object);
		bf.append("_");
		bf.append(tipologiaPolicy);
		bf.append("_");
		bf.append(nomePolicy);
		return bf.toString();
	}
	
	
	protected String tipologiaPolicy; // validazione/negoziazione/attributeAuthority
	protected String idPolicy;
	protected org.openspcoop2.core.config.GenericProperties policy;
	
	protected ArchiveIdCorrelazione idCorrelazione; // permette di correlare più oggetti tra di loro 

	public AbstractArchiveGenericProperties(org.openspcoop2.core.config.GenericProperties policy, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		
		if(policy==null){
			throw new ProtocolException("Policy non fornito");
		}
		if(policy.getTipologia()==null){
			throw new ProtocolException("Policy.tipologia non definito");
		}
		if(policy.getNome()==null){
			throw new ProtocolException("Policy.nome non definito");
		}
		this.tipologiaPolicy = policy.getTipologia();
		this.idPolicy = policy.getNome();
		this.policy = policy;
		
		this.idCorrelazione = idCorrelazione;
		
	}
	
	public String getTipologiaPolicy() {
		return this.tipologiaPolicy;
	}
	public String getNomePolicy() {
		return this.idPolicy;
	}
	public org.openspcoop2.core.config.GenericProperties getPolicy() {
		return this.policy;
	}
	
	public ArchiveIdCorrelazione getIdCorrelazione() {
		return this.idCorrelazione;
	}

}
