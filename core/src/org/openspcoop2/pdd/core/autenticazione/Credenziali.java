/*
 * OpenSPCoop - Customizable API Gateway 
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



package org.openspcoop2.pdd.core.autenticazione;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.utils.transport.Credential;
import org.openspcoop2.utils.transport.http.HttpServletCredential;
import org.slf4j.Logger;

/**
 * Classe utilizzata per rappresentare le informazioni utilizzata da un generico connettore.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Credenziali extends HttpServletCredential implements java.io.Serializable  {

	public Credenziali(){
		super();
	}
	public Credenziali(Credential credentials){
		if(credentials!=null){
			this.username = credentials.getUsername();
			this.password = credentials.getPassword();
			this.certs = credentials.getCerts();
			this.subject = credentials.getSubject();
			this.principal = credentials.getPrincipalObject();
			this.principalName = credentials.getPrincipal();
		}
	}
	public Credenziali(HttpServletRequest req){
		super(req, null);
	}
	public Credenziali(HttpServletRequest req,Logger log){
		super(req, log);
	}
	
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

    @Override
	public boolean equals(Object c){
    	return this.toString().equals(((Credenziali)c).toString());
    }
    
    @Override
	public String toString(){
    	String credenzialiFornite = "";
		if (this.getUsername() != null || this.getSubject() != null || this.getPrincipal() != null) {
			credenzialiFornite = "(";
			if (this.getUsername() != null){
				if(this.getPassword()==null || "".equals(this.getPassword()) )
					credenzialiFornite = credenzialiFornite + " Basic Username: ["+ this.getUsername() + "]  Basic Password: non definita";
				else
					credenzialiFornite = credenzialiFornite + " Basic Username: ["+ this.getUsername() + "] ";
			}
			if (this.getSubject() != null)
				credenzialiFornite = credenzialiFornite + " SSL Subject: ["+ this.getSubject() + "] ";
			if (this.getPrincipal() != null)
				credenzialiFornite = credenzialiFornite + " Principal: ["+ this.getPrincipal() + "] ";
			credenzialiFornite = credenzialiFornite + ") ";
		}
		return credenzialiFornite;
    }
}
