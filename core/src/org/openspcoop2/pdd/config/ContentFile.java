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



package org.openspcoop2.pdd.config;

/**
 * Classe utilizzata per raccogliere il contenuto di un file
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ContentFile implements java.io.Serializable  {
    
	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
    
	private byte[] content;
	private boolean exists;
	
	public byte[] getContent() {
		return this.content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	public boolean isExists() {
		return this.exists;
	}
	public void setExists(boolean exists) {
		this.exists = exists;
	}
}
