/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;

/**
 * Test sui profili di collaborazione implementati nella Porta di Dominio
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaDelegata extends Porta {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "PortaDelegata";
	
	private String portaDelegataOneWayLocalForward;
	private String portaDelegataSincronoLocalForward;

	public void setPortaDelegataOneWayLocalForward(
			String portaDelegataOneWayLocalForward) {
		this.portaDelegataOneWayLocalForward = portaDelegataOneWayLocalForward;
	}

	public void setPortaDelegataSincronoLocalForward(
			String portaDelegataSincronoLocalForward) {
		this.portaDelegataSincronoLocalForward = portaDelegataSincronoLocalForward;
	}
	
	public PortaDelegata(MessageType messageType, boolean attachments, boolean stateful) {
		super(messageType, attachments, stateful, true, CostantiTestSuite.PROXY_SOGGETTO_FRUITORE, CostantiTestSuite.PROXY_SOGGETTO_FRUITORE, 
				MessageType.SOAP_11.equals(messageType) ? CostantiTestSuite.PROXY_SOGGETTO_FRUITORE_FAULT11500: CostantiTestSuite.PROXY_SOGGETTO_FRUITORE_FAULT12500, 
						MessageType.SOAP_11.equals(messageType) ? CostantiTestSuite.PROXY_SOGGETTO_FRUITORE_FAULT11200 : CostantiTestSuite.PROXY_SOGGETTO_FRUITORE_FAULT12200, CostantiTestSuite.PROXY_SOGGETTO_EROGATORE_ESTERNO);
		
		if(stateful) {
			this.setPortaDelegataOneWay(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_STATEFUL_NON_AUTENTICATO);
			this.setPortaDelegataSincrono(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL_NON_AUTENTICATO);

			if(MessageType.SOAP_11.equals(messageType)) {
				this.setPortaDelegataOneWayFault200(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_STATEFUL_FAULT11_200);
				this.setPortaDelegataSincronoFault200(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL_FAULT11_200);
				this.setPortaDelegataOneWayFault500(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_STATEFUL_FAULT11_500);
				this.setPortaDelegataSincronoFault500(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL_FAULT11_500);
			} else {
				this.setPortaDelegataOneWayFault200(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_STATEFUL_FAULT12_200);
				this.setPortaDelegataSincronoFault200(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL_FAULT12_200);
				this.setPortaDelegataOneWayFault500(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_STATEFUL_FAULT12_500);
				this.setPortaDelegataSincronoFault500(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL_FAULT12_500);
			}

		
			this.setPortaDelegataOneWayAutenticata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_STATEFUL_AUTENTICATO);
			this.setUsernameOneWayAutenticata(CostantiTestSuite.USERNAME_PORTA_DELEGATA_ONEWAY_AUTENTICATA);
			this.setPasswordOneWayAutenticata(CostantiTestSuite.PASSWORD_PORTA_DELEGATA_ONEWAY_AUTENTICATA);
			
			this.setPortaDelegataSincronoAutenticata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL_AUTENTICATO);
			this.setUsernameSincronoAutenticata(CostantiTestSuite.USERNAME_PORTA_DELEGATA_SINCRONO_AUTENTICATA);
			this.setPasswordSincronoAutenticata(CostantiTestSuite.PASSWORD_PORTA_DELEGATA_SINCRONO_AUTENTICATA);
			
			this.setPortaDelegataOneWayLocalForward(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_STATEFUL_LOCAL_FORWARD);
			this.setPortaDelegataSincronoLocalForward(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL_LOCAL_FORWARD);

		} else {
			this.setPortaDelegataOneWay(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS_NON_AUTENTICATO);
			this.setPortaDelegataSincrono(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_STATELESS_NON_AUTENTICATO);

			if(MessageType.SOAP_11.equals(messageType)) {
				this.setPortaDelegataOneWayFault200(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS_FAULT11_200);
				this.setPortaDelegataSincronoFault200(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_STATELESS_FAULT11_200);
				this.setPortaDelegataOneWayFault500(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS_FAULT11_500);
				this.setPortaDelegataSincronoFault500(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_STATELESS_FAULT11_500);
			} else {
				this.setPortaDelegataOneWayFault200(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS_FAULT12_200);
				this.setPortaDelegataSincronoFault200(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_STATELESS_FAULT12_200);
				this.setPortaDelegataOneWayFault500(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS_FAULT12_500);
				this.setPortaDelegataSincronoFault500(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_STATELESS_FAULT12_500);
			}

			this.setPortaDelegataOneWayAutenticata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS_AUTENTICATO);
			this.setUsernameOneWayAutenticata(CostantiTestSuite.USERNAME_PORTA_DELEGATA_ONEWAY_AUTENTICATA);
			this.setPasswordOneWayAutenticata(CostantiTestSuite.PASSWORD_PORTA_DELEGATA_ONEWAY_AUTENTICATA);

			this.setPortaDelegataSincronoAutenticata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_STATELESS_AUTENTICATO);
			this.setUsernameSincronoAutenticata(CostantiTestSuite.USERNAME_PORTA_DELEGATA_SINCRONO_AUTENTICATA);
			this.setPasswordSincronoAutenticata(CostantiTestSuite.PASSWORD_PORTA_DELEGATA_SINCRONO_AUTENTICATA);
			
			this.setPortaDelegataOneWayLocalForward(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS_LOCAL_FORWARD);
			this.setPortaDelegataSincronoLocalForward(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_STATELESS_LOCAL_FORWARD);

		}

	}

	public void oneWayLocalForward(Repository repositoryOneWay) throws TestSuiteException, Exception{
		this.collaborazioneTrasparenteBase.oneWay(repositoryOneWay,this.portaDelegataOneWayLocalForward,true, null, null);
	}

	public void testOneWayLocalForward(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneTrasparenteBase.testOneWayLocalForward(data, msgDiagData,id, CostantiTestSuite.SOAP_TIPO_SERVIZIO,
					CostantiTestSuite.SOAP_NOME_SERVIZIO_ONEWAY,checkServizioApplicativo);
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				msgDiagData.close();
			}catch(Exception e){}
		}
	}


	public void sincronoLocalForward(Repository repository) throws TestSuiteException, Exception{
		this.collaborazioneTrasparenteBase.sincrono(repository,this.portaDelegataSincronoLocalForward,true);
	}

	public void testSincronoLocalForward(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneTrasparenteBase.testSincronoLocalForward(data, msgDiagData,id, CostantiTestSuite.SOAP_TIPO_SERVIZIO,
					CostantiTestSuite.SOAP_NOME_SERVIZIO_SINCRONO, checkServizioApplicativo);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}

	public static void _oneWayLocalForward(PortaDelegata port, Repository repository) throws TestSuiteException, Exception{
		port.oneWayLocalForward(repository);
	}
	
	public static void _testOneWayLocalForward(PortaDelegata port, DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		port.testOneWayLocalForward(data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	public static void _sincronoLocalForward(PortaDelegata port, Repository repository) throws TestSuiteException, Exception{
		port.sincronoLocalForward(repository);
	}
	
	public static void _testSincronoLocalForward(PortaDelegata port, DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		port.testSincronoLocalForward(data, msgDiagData, id, checkServizioApplicativo);
	}

}
