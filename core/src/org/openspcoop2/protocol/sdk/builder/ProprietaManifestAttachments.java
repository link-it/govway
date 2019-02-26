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


package org.openspcoop2.protocol.sdk.builder;


/**
 * Proprieta' da utilizzare per la generazione/processamento di un manifest degli attachments
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ProprietaManifestAttachments {

	private boolean gestioneManifest;
	private boolean readQualifiedAttribute;
	
	// scartaBody Indica se il corpo del messaggio deve essere trasmesso come allegato
	private boolean scartaBody;

	
	public boolean isScartaBody() {
		return this.scartaBody;
	}

	public void setScartaBody(boolean scartaBody) {
		this.scartaBody = scartaBody;
	}

	public boolean isGestioneManifest() {
		return this.gestioneManifest;
	}

	public void setGestioneManifest(boolean gestioneManifest) {
		this.gestioneManifest = gestioneManifest;
	}
	
	public boolean isReadQualifiedAttribute() {
		return this.readQualifiedAttribute;
	}

	public void setReadQualifiedAttribute(boolean readQualifiedAttribute) {
		this.readQualifiedAttribute = readQualifiedAttribute;
	}
	
}
