/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
package org.openspcoop2.pdd.core.dynamic;

import java.io.Serializable;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.constants.TipiDocumentoLivelloServizio;
import org.openspcoop2.core.registry.constants.TipiDocumentoSemiformale;
import org.openspcoop2.core.registry.constants.TipiDocumentoSicurezza;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.state.RequestInfo;

/**
 * SystemPropertiesReader
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AttachmentsReader implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private IDServizio idServizio;
	private IDAccordo idAccordo;
	private transient RegistroServiziManager registroServiziManagerField;
	private RequestInfo requestInfo;
	
	public AttachmentsReader(IDServizio idServizio, RequestInfo requestInfo) {
		this.idServizio = idServizio;
		this.registroServiziManagerField = RegistroServiziManager.getInstance();
		this.requestInfo = requestInfo;
	}
	
	private RegistroServiziManager getRegistroServiziManager() {
		if(this.registroServiziManagerField==null) {
			this.registroServiziManagerField = RegistroServiziManager.getInstance();
		}
		return this.registroServiziManagerField;
	}
	
	private IDAccordo getIDAccordo() throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		if(this.idAccordo==null) {
			AccordoServizioParteSpecifica asps = this.getRegistroServiziManager().getAccordoServizioParteSpecifica(this.idServizio, null, false, this.requestInfo);
			this.idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
		}
		return this.idAccordo;
	}
		
	public byte[] read(String nome) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return read(nome, false);
	}
	public byte[] read(String nome, boolean throwNotFoundException) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		try {
			return this.getRegistroServiziManager().getAllegato(this.idServizio, nome, this.requestInfo).getByteContenuto();
		}catch(DriverRegistroServiziNotFound notFound) {
			byte[] bNull = null;
			if(throwNotFoundException) {
				throw notFound;
			}
			else {
				return bNull;
			}
		}
	}
	
	public byte[] readSemiformalDocumentation(String nome, String tipo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return readSemiformalDocumentation(nome, TipiDocumentoSemiformale.toEnumConstant(tipo), false);
	}
	public byte[] readSemiformalDocumentation(String nome, String tipo, boolean throwNotFoundException) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return readSemiformalDocumentation(nome, TipiDocumentoSemiformale.toEnumConstant(tipo), throwNotFoundException);
	}
	public byte[] readSemiformalDocumentation(String nome, TipiDocumentoSemiformale tipo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return readSemiformalDocumentation(nome, tipo, false);
	}
	public byte[] readSemiformalDocumentation(String nome, TipiDocumentoSemiformale tipo, boolean throwNotFoundException) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		try {
			return this.getRegistroServiziManager().getSpecificaSemiformale(this.idServizio, tipo, nome, this.requestInfo).getByteContenuto();
		}catch(DriverRegistroServiziNotFound notFound) {
			byte[] bNull = null;
			if(throwNotFoundException) {
				throw notFound;
			}
			else {
				return bNull;
			}
		}
	}

	public byte[] readSecurityDocumentation(String nome, String tipo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return readSecurityDocumentation(nome, TipiDocumentoSicurezza.toEnumConstant(tipo), false);
	}
	public byte[] readSecurityDocumentation(String nome, String tipo, boolean throwNotFoundException) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return readSecurityDocumentation(nome, TipiDocumentoSicurezza.toEnumConstant(tipo), throwNotFoundException);
	}
	public byte[] readSecurityDocumentation(String nome, TipiDocumentoSicurezza tipo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return readSecurityDocumentation(nome, tipo, false);
	}
	public byte[] readSecurityDocumentation(String nome, TipiDocumentoSicurezza tipo, boolean throwNotFoundException) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		try {
			return this.getRegistroServiziManager().getSpecificaSicurezza(this.idServizio, tipo, nome, this.requestInfo).getByteContenuto();
		}catch(DriverRegistroServiziNotFound notFound) {
			byte[] bNull = null;
			if(throwNotFoundException) {
				throw notFound;
			}
			else {
				return bNull;
			}
		}
	}
	
	public byte[] readServiceLevelDocumentation(String nome, String tipo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return readServiceLevelDocumentation(nome, TipiDocumentoLivelloServizio.toEnumConstant(tipo), false);
	}
	public byte[] readServiceLevelDocumentation(String nome, String tipo, boolean throwNotFoundException) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return readServiceLevelDocumentation(nome, TipiDocumentoLivelloServizio.toEnumConstant(tipo), throwNotFoundException);
	}
	public byte[] readServiceLevelDocumentation(String nome, TipiDocumentoLivelloServizio tipo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return readServiceLevelDocumentation(nome, tipo, false);
	}
	public byte[] readServiceLevelDocumentation(String nome, TipiDocumentoLivelloServizio tipo, boolean throwNotFoundException) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		try {
			return this.getRegistroServiziManager().getSpecificaLivelloServizio(this.idServizio, tipo, nome, this.requestInfo).getByteContenuto();
		}catch(DriverRegistroServiziNotFound notFound) {
			byte[] bNull = null;
			if(throwNotFoundException) {
				throw notFound;
			}
			else {
				return bNull;
			}
		}
	}

	
	public byte[] readFromApi(String nome) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return readFromApi(nome, false);
	}
	public byte[] readFromApi(String nome, boolean throwNotFoundException) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		try {
			return this.getRegistroServiziManager().getAllegato(getIDAccordo(), nome, this.requestInfo).getByteContenuto();
		}catch(DriverRegistroServiziNotFound notFound) {
			byte[] bNull = null;
			if(throwNotFoundException) {
				throw notFound;
			}
			else {
				return bNull;
			}
		}
	}
	
	public byte[] readSemiformalDocumentationFromApi(String nome, String tipo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return readSemiformalDocumentationFromApi(nome, TipiDocumentoSemiformale.toEnumConstant(tipo), false);
	}
	public byte[] readSemiformalDocumentationFromApi(String nome, String tipo, boolean throwNotFoundException) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return readSemiformalDocumentationFromApi(nome, TipiDocumentoSemiformale.toEnumConstant(tipo), throwNotFoundException);
	}
	public byte[] readSemiformalDocumentationFromApi(String nome, TipiDocumentoSemiformale tipo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return readSemiformalDocumentationFromApi(nome, tipo, false);
	}
	public byte[] readSemiformalDocumentationFromApi(String nome, TipiDocumentoSemiformale tipo, boolean throwNotFoundException) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		try {
			return this.getRegistroServiziManager().getSpecificaSemiformale(getIDAccordo(), tipo, nome, this.requestInfo).getByteContenuto();
		}catch(DriverRegistroServiziNotFound notFound) {
			byte[] bNull = null;
			if(throwNotFoundException) {
				throw notFound;
			}
			else {
				return bNull;
			}
		}
	}
}
