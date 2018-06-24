/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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



package org.openspcoop2.pdd.core.credenziali;

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

public class Credenziali  extends HttpServletCredential implements java.io.Serializable  {


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Credenziali(){
        super();
	}
	public Credenziali(Credential credentials){
	        if(credentials!=null){
	                this.username = credentials.getUsername();
	                this.password = credentials.getPassword();
	                this.bearerToken = credentials.getBearerToken();
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




	@Override
	public boolean equals(Object c){
		return this.toString().equals(((Credenziali)c).toString());
	}

	@Override
	public String toString(){
		return this.toString(false);
	}
	public String toString(boolean showBasicPassword){
		String credenzialiFornite = "";
		if (this.getUsername() != null || this.getSubject() != null || this.getPassword() != null) {
			credenzialiFornite = "( ";
			boolean printPrincipal = true;
			if (this.getUsername() != null){
				String label = "BasicUsername";
				if (this.getPrincipal() != null && this.getPrincipal().equals(this.getUsername())){
					label = label + "/Principal";
					printPrincipal = false;
				}
				if(this.getPassword()==null)
					credenzialiFornite = credenzialiFornite + label+" '"+ this.getUsername() + "', BasicPassword undefined";
				else if("".equals(this.getPassword()) )
					credenzialiFornite = credenzialiFornite + label+" '"+ this.getUsername() + "', BasicPassword empty";
				else{
					if(showBasicPassword){
						credenzialiFornite = credenzialiFornite + label+" '"+ this.getUsername() + "', BasicPassword '"+this.getPassword()+"'";
					}
					else{
						credenzialiFornite = credenzialiFornite + label+" '"+ this.getUsername() + "'";
					}
				}
			}
			if (this.getSubject() != null){
				String label = "SSL-Subject";
				if (this.getPrincipal() != null && this.getPrincipal().equals(this.getSubject())){
					label = label + "/Principal";
					printPrincipal = false;
				}
				if (this.getUsername() != null){
					label = ", " + label;
				}
				credenzialiFornite = credenzialiFornite + label+" '"+ this.getSubject() + "'";
			}
			if (this.getPrincipal() != null && printPrincipal){
				String label = "Principal";
				if ( (this.getUsername() != null) || (this.getSubject() != null) ){
					label = ", " + label;
				}
				credenzialiFornite = credenzialiFornite + label+ " '"+ this.getPrincipal() + "'";
			}
			credenzialiFornite = credenzialiFornite + " ) ";
		}
		return credenzialiFornite;
	}
}
