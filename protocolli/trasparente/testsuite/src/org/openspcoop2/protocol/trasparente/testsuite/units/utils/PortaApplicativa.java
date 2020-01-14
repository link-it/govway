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



package org.openspcoop2.protocol.trasparente.testsuite.units.utils;

import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;

/**
 * Test sui profili di collaborazione implementati nella Porta di Dominio
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaApplicativa extends Porta {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "PortaApplicativa";
	
		
	public PortaApplicativa(MessageType messageType, boolean attachments, boolean stateful) {
		super(messageType, attachments, stateful, false, CostantiTestSuite.PROXY_SOGGETTO_FRUITORE_ANONIMO, CostantiTestSuite.PROXY_SOGGETTO_FRUITORE_AUTENTICATO_PORTA_APPLICATIVA, CostantiTestSuite.PROXY_SOGGETTO_FRUITORE_ANONIMO, CostantiTestSuite.PROXY_SOGGETTO_FRUITORE_ANONIMO, CostantiTestSuite.PROXY_SOGGETTO_EROGATORE);

		if(stateful) {
			this.setPortaDelegataOneWay(CostantiTestSuite.PORTA_APPLICATIVA_PROFILO_ONEWAY_STATEFUL_NON_AUTENTICATO);
			this.setPortaDelegataSincrono(CostantiTestSuite.PORTA_APPLICATIVA_PROFILO_SINCRONO_STATEFUL_NON_AUTENTICATO);

			if(MessageType.SOAP_11.equals(messageType)) {
				this.setPortaDelegataOneWayFault200(CostantiTestSuite.PORTA_APPLICATIVA_PROFILO_ONEWAY_STATEFUL_FAULT11_200);
				this.setPortaDelegataSincronoFault200(CostantiTestSuite.PORTA_APPLICATIVA_PROFILO_SINCRONO_STATEFUL_FAULT11_200);
				this.setPortaDelegataOneWayFault500(CostantiTestSuite.PORTA_APPLICATIVA_PROFILO_ONEWAY_STATEFUL_FAULT11_500);
				this.setPortaDelegataSincronoFault500(CostantiTestSuite.PORTA_APPLICATIVA_PROFILO_SINCRONO_STATEFUL_FAULT11_500);
			} else {
				this.setPortaDelegataOneWayFault200(CostantiTestSuite.PORTA_APPLICATIVA_PROFILO_ONEWAY_STATEFUL_FAULT12_200);
				this.setPortaDelegataSincronoFault200(CostantiTestSuite.PORTA_APPLICATIVA_PROFILO_SINCRONO_STATEFUL_FAULT12_200);
				this.setPortaDelegataOneWayFault500(CostantiTestSuite.PORTA_APPLICATIVA_PROFILO_ONEWAY_STATEFUL_FAULT12_500);
				this.setPortaDelegataSincronoFault500(CostantiTestSuite.PORTA_APPLICATIVA_PROFILO_SINCRONO_STATEFUL_FAULT12_500);
			}

		
			this.setPortaDelegataOneWayAutenticata(CostantiTestSuite.PORTA_APPLICATIVA_PROFILO_ONEWAY_STATEFUL_AUTENTICATO);
			this.setUsernameOneWayAutenticata(CostantiTestSuite.USERNAME_PORTA_APPLICATIVA_ONEWAY_AUTENTICATA);
			this.setPasswordOneWayAutenticata(CostantiTestSuite.PASSWORD_PORTA_DELEGATA_ONEWAY_AUTENTICATA);
			
			this.setPortaDelegataSincronoAutenticata(CostantiTestSuite.PORTA_APPLICATIVA_PROFILO_SINCRONO_STATEFUL_AUTENTICATO);
			this.setUsernameSincronoAutenticata(CostantiTestSuite.USERNAME_PORTA_APPLICATIVA_SINCRONO_AUTENTICATA);
			this.setPasswordSincronoAutenticata(CostantiTestSuite.PASSWORD_PORTA_DELEGATA_SINCRONO_AUTENTICATA);
			
		} else {
			this.setPortaDelegataOneWay(CostantiTestSuite.PORTA_APPLICATIVA_PROFILO_ONEWAY_STATELESS_NON_AUTENTICATO);
			this.setPortaDelegataSincrono(CostantiTestSuite.PORTA_APPLICATIVA_PROFILO_SINCRONO_STATELESS_NON_AUTENTICATO);

			if(MessageType.SOAP_11.equals(messageType)) {
				this.setPortaDelegataOneWayFault200(CostantiTestSuite.PORTA_APPLICATIVA_PROFILO_ONEWAY_STATELESS_FAULT11_200);
				this.setPortaDelegataSincronoFault200(CostantiTestSuite.PORTA_APPLICATIVA_PROFILO_SINCRONO_STATELESS_FAULT11_200);
				this.setPortaDelegataOneWayFault500(CostantiTestSuite.PORTA_APPLICATIVA_PROFILO_ONEWAY_STATELESS_FAULT11_500);
				this.setPortaDelegataSincronoFault500(CostantiTestSuite.PORTA_APPLICATIVA_PROFILO_SINCRONO_STATELESS_FAULT11_500);
			} else {
				this.setPortaDelegataOneWayFault200(CostantiTestSuite.PORTA_APPLICATIVA_PROFILO_ONEWAY_STATELESS_FAULT12_200);
				this.setPortaDelegataSincronoFault200(CostantiTestSuite.PORTA_APPLICATIVA_PROFILO_SINCRONO_STATELESS_FAULT12_200);
				this.setPortaDelegataOneWayFault500(CostantiTestSuite.PORTA_APPLICATIVA_PROFILO_ONEWAY_STATELESS_FAULT12_500);
				this.setPortaDelegataSincronoFault500(CostantiTestSuite.PORTA_APPLICATIVA_PROFILO_SINCRONO_STATELESS_FAULT12_500);
			}

			this.setPortaDelegataOneWayAutenticata(CostantiTestSuite.PORTA_APPLICATIVA_PROFILO_ONEWAY_STATELESS_AUTENTICATO);
			this.setUsernameOneWayAutenticata(CostantiTestSuite.USERNAME_PORTA_APPLICATIVA_ONEWAY_AUTENTICATA);
			this.setPasswordOneWayAutenticata(CostantiTestSuite.PASSWORD_PORTA_DELEGATA_ONEWAY_AUTENTICATA);

			this.setPortaDelegataSincronoAutenticata(CostantiTestSuite.PORTA_APPLICATIVA_PROFILO_SINCRONO_STATELESS_AUTENTICATO);
			this.setUsernameSincronoAutenticata(CostantiTestSuite.USERNAME_PORTA_APPLICATIVA_SINCRONO_AUTENTICATA);
			this.setPasswordSincronoAutenticata(CostantiTestSuite.PASSWORD_PORTA_DELEGATA_SINCRONO_AUTENTICATA);
			
		}

	}

}
