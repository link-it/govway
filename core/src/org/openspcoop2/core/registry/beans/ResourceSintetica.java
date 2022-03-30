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
package org.openspcoop2.core.registry.beans;

import java.io.Serializable;

import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.constants.HttpMethod;


/** 
 * ResourceSintetica
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

public class ResourceSintetica extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResourceSintetica() {
	}
	public ResourceSintetica(Resource param) {
		this.id = param.getId();
		this.profAzione = param.getProfAzione();
		this.idAccordo = param.getIdAccordo();
		this.nome = param.getNome();
		this.descrizione = param.getDescrizione();
		this.path = param.getPath();
		this.method = param.getMethod();
	}
	
	private Long id;

	private java.lang.String profAzione;

	private java.lang.Long idAccordo;

	private java.lang.String nome;

	private java.lang.String descrizione;

	private java.lang.String path;

	private HttpMethod method;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public java.lang.String getProfAzione() {
		return this.profAzione;
	}

	public void setProfAzione(java.lang.String profAzione) {
		this.profAzione = profAzione;
	}

	public java.lang.Long getIdAccordo() {
		return this.idAccordo;
	}

	public void setIdAccordo(java.lang.Long idAccordo) {
		this.idAccordo = idAccordo;
	}

	public java.lang.String getNome() {
		return this.nome;
	}

	public void setNome(java.lang.String nome) {
		this.nome = nome;
	}

	public java.lang.String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(java.lang.String descrizione) {
		this.descrizione = descrizione;
	}

	public java.lang.String getPath() {
		return this.path;
	}

	public void setPath(java.lang.String path) {
		this.path = path;
	}

	public HttpMethod getMethod() {
		return this.method;
	}

	public void setMethod(HttpMethod method) {
		this.method = method;
	}


}
