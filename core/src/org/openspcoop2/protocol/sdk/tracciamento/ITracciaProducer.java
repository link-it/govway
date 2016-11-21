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



package org.openspcoop2.protocol.sdk.tracciamento;

import java.sql.Connection;

import org.openspcoop2.core.commons.IMonitoraggioRisorsa;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.protocol.sdk.IComponentFactory;

/**
 * Contiene la definizione una interfaccia per la registrazione personalizzata delle buste.
 *
 * @author Poli Andrea (apoli@link.it)
* @author $Author: apoli $
* @version $Rev: 12237 $, $Date: 2016-10-04 11:41:45 +0200 (Tue, 04 Oct 2016) $
 */

public interface ITracciaProducer extends IMonitoraggioRisorsa,IComponentFactory {

	
	/**
	 * Inizializza l'engine di un appender per la registrazione
	 * di un tracciamento emesso da una porta di dominio.
	 * 
	 * @param appenderProperties Proprieta' dell'appender
	 * @throws TracciamentoException
	 */
	public void initializeAppender(OpenspcoopAppender appenderProperties) throws TracciamentoException;

	
	/**
	 * Registra una traccia prodotta da una porta di dominio.
	 * 
	 * @param conOpenSPCoopPdD Connessione verso il database della Porta di Dominio
	 * @param traccia Traccia
	 * @throws TracciamentoException
	 */
	public void log(Connection conOpenSPCoopPdD,Traccia traccia) throws TracciamentoException;

	
}
