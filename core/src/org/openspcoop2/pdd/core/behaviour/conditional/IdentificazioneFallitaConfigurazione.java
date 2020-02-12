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
package org.openspcoop2.pdd.core.behaviour.conditional;

/**
 * IdentificazioneFallitaConfigurazione
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IdentificazioneFallitaConfigurazione {

	private boolean abortTransaction = true;
	private boolean emitDiagnosticInfo = false;
	private boolean emitDiagnosticError = false;
	private String nomeConnettore; // se non si effettua la terminazione della transazione, deve essere fornito un nome di connettore
		
	public boolean isAbortTransaction() {
		return this.abortTransaction;
	}
	public void setAbortTransaction(boolean abortTransaction) {
		this.abortTransaction = abortTransaction;
	}
	public boolean isEmitDiagnosticInfo() {
		return this.emitDiagnosticInfo;
	}
	public void setEmitDiagnosticInfo(boolean emitDiagnosticInfo) {
		this.emitDiagnosticInfo = emitDiagnosticInfo;
	}
	public boolean isEmitDiagnosticError() {
		return this.emitDiagnosticError;
	}
	public void setEmitDiagnosticError(boolean emitDiagnosticError) {
		this.emitDiagnosticError = emitDiagnosticError;
	}
	public String getNomeConnettore() {
		return this.nomeConnettore;
	}
	public void setNomeConnettore(String nomeConnettore) {
		this.nomeConnettore = nomeConnettore;
	}
}
