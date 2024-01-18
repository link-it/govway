/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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



package org.openspcoop2.pdd.core.behaviour;

import org.openspcoop2.pdd.core.EJBUtilsConsegnaException;
import org.openspcoop2.pdd.logger.MsgDiagnostico;

/**	
 * Contiene la definizione di una eccezione lanciata dalla classe EJBUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class BehaviourEmitDiagnosticException extends EJBUtilsConsegnaException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BehaviourEmitDiagnosticException(MsgDiagnostico msgDiag, String idFunzioneDiagnostica, String idDiagnostico,
			Throwable cause) {
		super(msgDiag, idFunzioneDiagnostica, idDiagnostico, cause);
	}

	public BehaviourEmitDiagnosticException(MsgDiagnostico msgDiag, String idFunzioneDiagnostica,
			String idDiagnostico) {
		super(msgDiag, idFunzioneDiagnostica, idDiagnostico);
	}

	public BehaviourEmitDiagnosticException(String message, int livelloErrore, String codice, Throwable cause) {
		super(message, livelloErrore, codice, cause);
	}

	public BehaviourEmitDiagnosticException(String message, int livelloErrore, String codice) {
		super(message, livelloErrore, codice);
	}
}

