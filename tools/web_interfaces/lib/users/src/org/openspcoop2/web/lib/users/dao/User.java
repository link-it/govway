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


package org.openspcoop2.web.lib.users.dao;

import java.io.Serializable;


/**
 * User
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */

public class User implements Serializable {
	private Long id;

	protected String login;
	protected String password;
	private InterfaceType interfaceType;
	private PermessiUtente permessi;
	
	public InterfaceType getInterfaceType() {
		return this.interfaceType;
	}

	public void setInterfaceType(InterfaceType interfaceType) {
		this.interfaceType = interfaceType;
	}

	public PermessiUtente getPermessi() {
		return this.permessi;
	}

	public void setPermessi(PermessiUtente permessi) {
		this.permessi = permessi;
	}

	public Long getId() {
		if (this.id != null)
			return this.id;
		else
			return new Long(-1);
	}

	public void setId(Long id) {
		if (id != null)
			this.id = id;
		else
			this.id = new Long(-1);
	}

	public String getLogin() {
		if (this.login != null && ("".equals(this.login) == false)) {
			return this.login.trim();
		} else {
			return null;
		}
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		if (this.password != null && ("".equals(this.password) == false)) {
			return this.password.trim();
		} else {
			return null;
		}
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private static final long serialVersionUID = 1L;

	public static final String LOGIN = "login";

	public static final String PASSWORD = "password";
}
