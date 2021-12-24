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



package org.openspcoop2.protocol.trasparente.testsuite.units.utils;

import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.protocol.trasparente.testsuite.core.Utilities;

/**
 * Test sui profili di collaborazione implementati nella Porta di Dominio
 * 
 * @author Giovanni Bussu (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaImpl {

	protected PortaDelegata pdSOAP11Stateless;
	protected PortaDelegata pdSOAP12Stateless;
	protected PortaDelegata pdSOAP11WithAttachmentsStateless;
	protected PortaDelegata pdSOAP12WithAttachmentsStateless;
	protected PortaDelegata pdSOAP11Stateful;
	protected PortaDelegata pdSOAP12Stateful;
	protected PortaDelegata pdSOAP11WithAttachmentsStateful;
	protected PortaDelegata pdSOAP12WithAttachmentsStateful;

	protected PortaApplicativa paSOAP11Stateless;
	protected PortaApplicativa paSOAP12Stateless;
	protected PortaApplicativa paSOAP11WithAttachmentsStateless;
	protected PortaApplicativa paSOAP12WithAttachmentsStateless;
	protected PortaApplicativa paSOAP11Stateful;
	protected PortaApplicativa paSOAP12Stateful;
	protected PortaApplicativa paSOAP11WithAttachmentsStateful;
	protected PortaApplicativa paSOAP12WithAttachmentsStateful;

	protected boolean doTestStateful = true;
	private static boolean printMsg = false;
	private static synchronized void printMsgStateful(){
		if(printMsg==false){
			System.out.println("WARNING: Verifiche Stateful disabilitate per Tomcat");
			printMsg = true;
		}
	}	
	
	public PortaImpl() {

		this.pdSOAP11Stateless = new PortaDelegata(MessageType.SOAP_11, false, false);
		this.pdSOAP12Stateless = new PortaDelegata(MessageType.SOAP_12, false, false);
		this.pdSOAP11WithAttachmentsStateless = new PortaDelegata(MessageType.SOAP_11, true, false);
		this.pdSOAP12WithAttachmentsStateless = new PortaDelegata(MessageType.SOAP_12, true, false);
		this.pdSOAP11Stateful = new PortaDelegata(MessageType.SOAP_11, false, true);
		this.pdSOAP12Stateful = new PortaDelegata(MessageType.SOAP_12, false, true);
		this.pdSOAP11WithAttachmentsStateful = new PortaDelegata(MessageType.SOAP_11, true, true);
		this.pdSOAP12WithAttachmentsStateful = new PortaDelegata(MessageType.SOAP_12, true, true);

		this.paSOAP11Stateless = new PortaApplicativa(MessageType.SOAP_11, false, false);
		this.paSOAP12Stateless = new PortaApplicativa(MessageType.SOAP_12, false, false);
		this.paSOAP11WithAttachmentsStateless = new PortaApplicativa(MessageType.SOAP_11, true, false);
		this.paSOAP12WithAttachmentsStateless = new PortaApplicativa(MessageType.SOAP_12, true, false);
		this.paSOAP11Stateful = new PortaApplicativa(MessageType.SOAP_11, false, true);
		this.paSOAP12Stateful = new PortaApplicativa(MessageType.SOAP_12, false, true);
		this.paSOAP11WithAttachmentsStateful = new PortaApplicativa(MessageType.SOAP_11, true, true);
		this.paSOAP12WithAttachmentsStateful = new PortaApplicativa(MessageType.SOAP_12, true, true);
		
		try{
			String version_jbossas = Utilities.readApplicationServerVersion();
			if(version_jbossas.startsWith("tomcat")){
				printMsgStateful();
				this.doTestStateful = false;
			}
		}catch(Exception e){
			System.out.println("Errore durante l'identificazione dell'application server");
			e.printStackTrace(System.out);
		}
	}
}
