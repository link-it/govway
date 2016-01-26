/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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



package org.openspcoop2.pdd.services;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.PdDContext;

/**
 * Informazioni per la gestione della risposta inviata come errore
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RicezioneBusteParametriInvioBustaErrore extends RicezioneBusteParametriGestioneBustaErrore {
	
	private OpenSPCoop2Message openspcoopMsg;

	private boolean newConnectionForResponse;
	private boolean utilizzoIndirizzoTelematico;
	private boolean functionAsRouter;
	private boolean onewayVersione11;
	private PdDContext pddContext;

	public boolean isNewConnectionForResponse() {
		return this.newConnectionForResponse;
	}
	public void setNewConnectionForResponse(boolean newConnectionForResponse) {
		this.newConnectionForResponse = newConnectionForResponse;
	}
	public boolean isUtilizzoIndirizzoTelematico() {
		return this.utilizzoIndirizzoTelematico;
	}
	public void setUtilizzoIndirizzoTelematico(boolean utilizzoIndirizzoTelematico) {
		this.utilizzoIndirizzoTelematico = utilizzoIndirizzoTelematico;
	}
	public boolean isFunctionAsRouter() {
		return this.functionAsRouter;
	}
	public void setFunctionAsRouter(boolean functionAsRouter) {
		this.functionAsRouter = functionAsRouter;
	}
	public boolean isOnewayVersione11() {
		return this.onewayVersione11;
	}
	public void setOnewayVersione11(boolean onewayVersione11) {
		this.onewayVersione11 = onewayVersione11;
	}
	public OpenSPCoop2Message getOpenspcoopMsg() {
		return this.openspcoopMsg;
	}
	public void setOpenspcoopMsg(OpenSPCoop2Message openspcoopMsg) {
		this.openspcoopMsg = openspcoopMsg;
	}
	public PdDContext getPddContext() {
		return this.pddContext;
	}
	public void setPddContext(PdDContext pddContext) {
		this.pddContext = pddContext;
	}
}
