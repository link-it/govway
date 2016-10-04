/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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


package org.openspcoop2.utils.id;

/**
 * Implementazione tramite java.util.UUID
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ClusterIdentifierGenerator implements IUniqueIdentifierGenerator {

	private String clusterIDSuffix = null;
	
	@Override
	public void init(Object ... o) throws UniqueIdentifierException{
		this.clusterIDSuffix = (String) o[0];
	}
	
	@Override
	public IUniqueIdentifier newID() throws UniqueIdentifierException {
		try{
			
			ClusterIdentifier clusterID = new ClusterIdentifier();
			java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
			clusterID.setHostname(localMachine.getHostName());
			clusterID.setSuffix(this.clusterIDSuffix);
			return clusterID;
			
		}catch(Exception e){
			throw new UniqueIdentifierException("Generazione clusteID fallita: "+e.getMessage(),e);
		}
	}

	@Override
	public IUniqueIdentifier convertFromString(String value)
			throws UniqueIdentifierException {
		
		if(value.endsWith(this.clusterIDSuffix)==false){
			throw new UniqueIdentifierException("ClusterID non nel corretto formato");
		}
		if(value.length()<=(this.clusterIDSuffix.length()+1)){
			throw new UniqueIdentifierException("ClusterID non nel corretto formato");
		}
		
		ClusterIdentifier clusterID = new ClusterIdentifier();
		clusterID.setHostname(value.substring(0,(value.length()-(this.clusterIDSuffix.length()+1))));
		clusterID.setSuffix(this.clusterIDSuffix);
		return clusterID;
	}
	
}
