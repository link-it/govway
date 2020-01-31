/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * ArchiveScope
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiveScope implements IArchiveObject {

	public static String buildKey(String nomeScope) throws ProtocolException{
		
		if(nomeScope==null){
			throw new ProtocolException("nomeScope non fornito");
		}
		
		StringBuilder bf = new StringBuilder();
		bf.append("Scope_");
		bf.append(nomeScope);
		return bf.toString();
	}
	
	@Override
	public String key() throws ProtocolException {
		return ArchiveScope.buildKey(this.idScope.getNome());
	}
	
	
	
	private IDScope idScope;

	private org.openspcoop2.core.registry.Scope scope;
	
	private ArchiveIdCorrelazione idCorrelazione; // permette di correlare più oggetti tra di loro 

	public ArchiveScope(org.openspcoop2.core.registry.Scope scope, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		
		if(scope==null){
			throw new ProtocolException("Scope non fornito");
		}
		if(scope.getNome()==null){
			throw new ProtocolException("Scope.nome non definito");
		}
		this.idScope = new IDScope(scope.getNome());
		this.scope = scope;
		
		this.idCorrelazione = idCorrelazione;
		
	}
	
	
	public IDScope getIdScope() {
		return this.idScope;
	}
	public org.openspcoop2.core.registry.Scope getScope() {
		return this.scope;
	}
	
	public ArchiveIdCorrelazione getIdCorrelazione() {
		return this.idCorrelazione;
	}

}
