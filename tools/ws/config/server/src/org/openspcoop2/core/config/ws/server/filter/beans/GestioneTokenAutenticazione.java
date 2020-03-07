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
package org.openspcoop2.core.config.ws.server.filter.beans;

/**
 * <p>Java class for GestioneTokenAutenticazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="gestione-token-autenticazione"&gt;
 *     &lt;sequence&gt;
 *         &lt;element name="issuer" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="client-id" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="subject" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="username" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="email" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" /&gt;
 *     &lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.config.constants.StatoFunzionalita;

/**     
 * GestioneTokenAutenticazione
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "gestione-token-autenticazione", namespace="http://www.openspcoop2.org/core/config/management", propOrder = {
    "issuer",
    "clientId",
    "subject",
    "username",
    "email"
})
@javax.xml.bind.annotation.XmlRootElement(name = "gestione-token-autenticazione")
public class GestioneTokenAutenticazione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="issuer",required=false,nillable=false)
	private StatoFunzionalita issuer;
	
	public void setIssuer(StatoFunzionalita issuer){
		this.issuer = issuer;
	}
	
	public StatoFunzionalita getIssuer(){
		return this.issuer;
	}
	
	
	@XmlElement(name="client-id",required=false,nillable=false)
	private StatoFunzionalita clientId;
	
	public void setClientId(StatoFunzionalita clientId){
		this.clientId = clientId;
	}
	
	public StatoFunzionalita getClientId(){
		return this.clientId;
	}
	
	
	@XmlElement(name="subject",required=false,nillable=false)
	private StatoFunzionalita subject;
	
	public void setSubject(StatoFunzionalita subject){
		this.subject = subject;
	}
	
	public StatoFunzionalita getSubject(){
		return this.subject;
	}
	
	
	@XmlElement(name="username",required=false,nillable=false)
	private StatoFunzionalita username;
	
	public void setUsername(StatoFunzionalita username){
		this.username = username;
	}
	
	public StatoFunzionalita getUsername(){
		return this.username;
	}
	
	
	@XmlElement(name="email",required=false,nillable=false)
	private StatoFunzionalita email;
	
	public void setEmail(StatoFunzionalita email){
		this.email = email;
	}
	
	public StatoFunzionalita getEmail(){
		return this.email;
	}
	
	
	
	
}