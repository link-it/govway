/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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
 * @author $Author: apoli $
 * @version $Rev: 13574 $, $Date: 2018-01-26 12:24:34 +0100 (Fri, 26 Jan 2018) $
 */
public class ArchiveActivePolicy implements IArchiveObject {

	public static String buildKey(String idPolicy) throws ProtocolException{
		
		if(idPolicy==null){
			throw new ProtocolException("idPolicy non fornito");
		}
		
		StringBuffer bf = new StringBuffer();
		bf.append("ControlloTraffico_ActivePolicy_");
		bf.append(idPolicy);
		return bf.toString();
	}
	
	@Override
	public String key() throws ProtocolException {
		return ArchiveActivePolicy.buildKey(this.idPolicy);
	}
	
	
	
	private String idPolicy;
	private org.openspcoop2.core.controllo_traffico.AttivazionePolicy policy;
	
	private ArchiveIdCorrelazione idCorrelazione; // permette di correlare più oggetti tra di loro 

	public ArchiveActivePolicy(org.openspcoop2.core.controllo_traffico.AttivazionePolicy policy, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		
		if(policy==null){
			throw new ProtocolException("Policy non fornito");
		}
		if(policy.getIdActivePolicy()==null){
			throw new ProtocolException("Policy.idActivePolicy non definito");
		}
		this.idPolicy = policy.getIdActivePolicy();
		this.policy = policy;
		
		this.idCorrelazione = idCorrelazione;
		
	}
	
	
	public String getNomePolicy() {
		return this.idPolicy;
	}
	public org.openspcoop2.core.controllo_traffico.AttivazionePolicy getPolicy() {
		return this.policy;
	}
	
	public ArchiveIdCorrelazione getIdCorrelazione() {
		return this.idCorrelazione;
	}

}
