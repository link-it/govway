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



package org.openspcoop2.pdd.services.core;

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
