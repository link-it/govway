/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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



package org.openspcoop2.core.protocolli.trasparente.testsuite.validazione.rpc;

import jakarta.xml.ws.Endpoint;

import org.openspcoop2.utils.threads.BaseThread;

/**
 * Thread per la gestione del Threshold
 * 
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RPCServerThread extends BaseThread{

	private int port;
	private boolean schemaValidation;
	private boolean initialized;
	private boolean soapNamespaceRidefinito;
	
	public boolean isInitialized() {
		return this.initialized;
	}

	/** Costruttore */
	public RPCServerThread(int port,boolean schemaValidation, boolean soapNamespaceRidefinito) {
		this.port = port;
		this.schemaValidation = schemaValidation;
		this.soapNamespaceRidefinito = soapNamespaceRidefinito;
	}
	
	@Override
	protected boolean initialize() {
		
		System.out.println("Starting Server '"+this.port+"' (validazione:"+this.schemaValidation+" namespaceRidefinito:"+this.soapNamespaceRidefinito+")");
		Object implementor = null;
		if(this.soapNamespaceRidefinito) {
			org.openspcoop2.example.server.rpc.literal.skeleton_namespace_ridefinito.ServiceRPCLiteralImpl.debug = false;
			implementor = new org.openspcoop2.example.server.rpc.literal.skeleton_namespace_ridefinito.ServiceRPCLiteralImpl();
		}
		else {
			org.openspcoop2.example.server.rpc.literal.skeleton.ServiceRPCLiteralImpl.debug = false;
			implementor = new org.openspcoop2.example.server.rpc.literal.skeleton.ServiceRPCLiteralImpl();
		}
        String address = "http://127.0.0.1:"+this.port+"/ServiceRPCLiteral";
        
        Endpoint e = Endpoint.create(implementor);
        if(this.schemaValidation) {
        	e.getProperties().put("schema-validation-enabled", true);
        }
        e.publish(address);
		
        this.initialized = true;
        
		return true;
	}
	@Override
	protected void process() {
		
	}
	@Override
	protected void close() {
		System.out.println("Stop Server '"+this.port+"' (validazione:"+this.schemaValidation+" namespaceRidefinito:"+this.soapNamespaceRidefinito+")");
	}
	
	
}
