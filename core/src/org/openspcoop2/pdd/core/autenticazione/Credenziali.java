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


/**
 * Classe utilizzata per rappresentare le informazioni utilizzata da un generico connettore.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Credenziali implements java.io.Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/* ---- User (HTTP Authentication) --- */
    private String user;
    /* ---- Password (HTTP Authentication) --- */
    private String password;
    /* ---- Subject --- */
    private String subject;
    /* ---- Certificati ---- */
    private java.security.cert.X509Certificate[] certs;

    
    /** ---------------- SETTER ---------------------- */

    /**
     * Imposta il parametro 'user' di una autenticazione BASIC.
     *
     * @param us  parametro 'user' di una autenticazione BASIC.
     * 
     */
    public void setUsername(String us){
	this.user = us;
    }

    /**
     * Imposta il parametro 'password' di una autenticazione BASIC.
     *
     * @param passw  parametro 'password' di una autenticazione BASIC.
     * 
     */
    public void setPassword(String passw){
	this.password = passw;
    }

    /**
     * Imposta i certificati utilizzati in una autenticazione SSL.
     *
     * @param c certificati di una autenticazione SSL.
     * 
     */
    public void setCertificati(java.security.cert.X509Certificate[] c){
	this.certs = c;
    }

    /**
     * Imposta il parametro 'subject' di una autenticazione SSL (primo certificato).
     *
     * @param s  parametro 'subject' di una autenticazione SSL.
     * 
     */
    public void setSubject(String s){
	this.subject = s;
    }






    

    /** -------------- GETTER ------------------ */

     /**
     * Ritorna il parametro 'user' di una autenticazione BASIC.
     *
     * @return parametro 'user' di una autenticazione BASIC.
     * 
     */
    public String getUsername(){
	return this.user;
    }

    /**
     * Ritorna il parametro 'password' di una autenticazione BASIC.
     *
     * @return parametro 'password' di una autenticazione BASIC.
     * 
     */
    public String getPassword(){
	return this.password;
    }

    /**
     * Ritorna i certificati utilizzati in una autenticazione SSL.
     *
     * @return certificati di una autenticazione SSL.
     * 
     */
    public java.security.cert.X509Certificate[] getCertificati(){
	return this.certs;
    }

    /**
     * Ritorna il parametro 'subject' di una autenticazione SSL (primo certificato).
     *
     * @return  parametro 'subject' di una autenticazione SSL.
     * 
     */
    public String getSubject(){
	return this.subject;
    }
    
    
    
    
    
    
    
    
    @Override
	public boolean equals(Object c){
    	return this.toString().equals(((Credenziali)c).toString());
    }
    
    @Override
	public String toString(){
    	String credenzialiFornite = "";
		if (this.getUsername() != null || this.getSubject() != null) {
			credenzialiFornite = "(";
			if (this.getUsername() != null){
				if(this.getPassword()==null || "".equals(this.getPassword()) )
					credenzialiFornite = credenzialiFornite + " Basic Username: ["+ this.getUsername() + "]  Basic Password: non definita";
				else
					credenzialiFornite = credenzialiFornite + " Basic Username: ["+ this.getUsername() + "] ";
			}
			if (this.getSubject() != null)
				credenzialiFornite = credenzialiFornite + " SSL Subject: ["+ this.getSubject() + "] ";
			credenzialiFornite = credenzialiFornite + ") ";
		}
		return credenzialiFornite;
    }
}
