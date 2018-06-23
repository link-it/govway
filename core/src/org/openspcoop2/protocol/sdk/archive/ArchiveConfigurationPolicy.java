/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
 * ArchiveSoggetto
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiveConfigurationPolicy implements IArchiveObject {

	public static String buildKey(String idPolicy) throws ProtocolException{
		
		if(idPolicy==null){
			throw new ProtocolException("idPolicy non fornito");
		}
		
		StringBuffer bf = new StringBuffer();
		bf.append("ControlloTraffico_ConfigurationPolicy_");
		bf.append(idPolicy);
		return bf.toString();
	}
	
	@Override
	public String key() throws ProtocolException {
		return ArchiveConfigurationPolicy.buildKey(this.idPolicy);
	}
	
	
	
	private String idPolicy;
	private org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy policy;
	
	private ArchiveIdCorrelazione idCorrelazione; // permette di correlare più oggetti tra di loro 

	public ArchiveConfigurationPolicy(org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy policy, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		
		if(policy==null){
			throw new ProtocolException("Policy non fornito");
		}
		if(policy.getIdPolicy()==null){
			throw new ProtocolException("Policy.idPolicy non definito");
		}
		this.idPolicy = policy.getIdPolicy();
		this.policy = policy;
		
		this.idCorrelazione = idCorrelazione;
		
	}
	
	
	public String getNomePolicy() {
		return this.idPolicy;
	}
	public org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy getPolicy() {
		return this.policy;
	}
	
	public ArchiveIdCorrelazione getIdCorrelazione() {
		return this.idCorrelazione;
	}

}
