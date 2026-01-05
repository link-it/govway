/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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
package org.openspcoop2.core.registry.wsdl;

/**
 * WSDLValidatorConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WSDLValidatorConfig {

	private boolean gestioneXsiTypeRpcLiteral;
	private boolean rpcAcceptRootElementUnqualified;
	
	private boolean validationXsdAddNamespaceXSITypeIfNotExists;
	private boolean validationRpcAddNamespaceXSITypeIfNotExists;
	private boolean validationDocumentAddNamespaceXSITypeIfNotExists;
	
	public boolean isGestioneXsiTypeRpcLiteral() {
		return this.gestioneXsiTypeRpcLiteral;
	}
	public void setGestioneXsiTypeRpcLiteral(boolean gestioneXsiTypeRpcLiteral) {
		this.gestioneXsiTypeRpcLiteral = gestioneXsiTypeRpcLiteral;
	}
	public boolean isRpcAcceptRootElementUnqualified() {
		return this.rpcAcceptRootElementUnqualified;
	}
	public void setRpcAcceptRootElementUnqualified(boolean rpcAcceptRootElementUnqualified) {
		this.rpcAcceptRootElementUnqualified = rpcAcceptRootElementUnqualified;
	}
	public boolean isValidationXsdAddNamespaceXSITypeIfNotExists() {
		return this.validationXsdAddNamespaceXSITypeIfNotExists;
	}
	public void setValidationXsdAddNamespaceXSITypeIfNotExists(boolean validationXsdAddNamespaceXSITypeIfNotExists) {
		this.validationXsdAddNamespaceXSITypeIfNotExists = validationXsdAddNamespaceXSITypeIfNotExists;
	}
	public boolean isValidationRpcAddNamespaceXSITypeIfNotExists() {
		return this.validationRpcAddNamespaceXSITypeIfNotExists;
	}
	public void setValidationRpcAddNamespaceXSITypeIfNotExists(boolean validationRpcAddNamespaceXSITypeIfNotExists) {
		this.validationRpcAddNamespaceXSITypeIfNotExists = validationRpcAddNamespaceXSITypeIfNotExists;
	}
	public boolean isValidationDocumentAddNamespaceXSITypeIfNotExists() {
		return this.validationDocumentAddNamespaceXSITypeIfNotExists;
	}
	public void setValidationDocumentAddNamespaceXSITypeIfNotExists(
			boolean validationDocumentAddNamespaceXSITypeIfNotExists) {
		this.validationDocumentAddNamespaceXSITypeIfNotExists = validationDocumentAddNamespaceXSITypeIfNotExists;
	}
	
}
