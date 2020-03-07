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
 * <p>Java class for GestioneToken complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="gestione-token"&gt;
 *     &lt;sequence&gt;
 *         &lt;element name="autenticazione" type="{http://www.openspcoop2.org/core/config/management}gestione-token-autenticazione" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="policy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="token-opzionale" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="validazione" type="{http://www.openspcoop2.org/core/config}StatoFunzionalitaConWarning" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="introspection" type="{http://www.openspcoop2.org/core/config}StatoFunzionalitaConWarning" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="userInfo" type="{http://www.openspcoop2.org/core/config}StatoFunzionalitaConWarning" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="forward" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="options" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *     &lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.config.ws.server.filter.beans.GestioneTokenAutenticazione;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.StatoFunzionalita;

/**     
 * GestioneToken
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "gestione-token", namespace="http://www.openspcoop2.org/core/config/management", propOrder = {
    "autenticazione",
    "policy",
    "tokenOpzionale",
    "validazione",
    "introspection",
    "userInfo",
    "forward",
    "options"
})
@javax.xml.bind.annotation.XmlRootElement(name = "gestione-token")
public class GestioneToken extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="autenticazione",required=false,nillable=false)
	private GestioneTokenAutenticazione autenticazione;
	
	public void setAutenticazione(GestioneTokenAutenticazione autenticazione){
		this.autenticazione = autenticazione;
	}
	
	public GestioneTokenAutenticazione getAutenticazione(){
		return this.autenticazione;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="policy",required=false,nillable=false)
	private String policy;
	
	public void setPolicy(String policy){
		this.policy = policy;
	}
	
	public String getPolicy(){
		return this.policy;
	}
	
	
	@XmlElement(name="token-opzionale",required=false,nillable=false)
	private StatoFunzionalita tokenOpzionale;
	
	public void setTokenOpzionale(StatoFunzionalita tokenOpzionale){
		this.tokenOpzionale = tokenOpzionale;
	}
	
	public StatoFunzionalita getTokenOpzionale(){
		return this.tokenOpzionale;
	}
	
	
	@XmlElement(name="validazione",required=false,nillable=false)
	private StatoFunzionalitaConWarning validazione;
	
	public void setValidazione(StatoFunzionalitaConWarning validazione){
		this.validazione = validazione;
	}
	
	public StatoFunzionalitaConWarning getValidazione(){
		return this.validazione;
	}
	
	
	@XmlElement(name="introspection",required=false,nillable=false)
	private StatoFunzionalitaConWarning introspection;
	
	public void setIntrospection(StatoFunzionalitaConWarning introspection){
		this.introspection = introspection;
	}
	
	public StatoFunzionalitaConWarning getIntrospection(){
		return this.introspection;
	}
	
	
	@XmlElement(name="userInfo",required=false,nillable=false)
	private StatoFunzionalitaConWarning userInfo;
	
	public void setUserInfo(StatoFunzionalitaConWarning userInfo){
		this.userInfo = userInfo;
	}
	
	public StatoFunzionalitaConWarning getUserInfo(){
		return this.userInfo;
	}
	
	
	@XmlElement(name="forward",required=false,nillable=false)
	private StatoFunzionalita forward;
	
	public void setForward(StatoFunzionalita forward){
		this.forward = forward;
	}
	
	public StatoFunzionalita getForward(){
		return this.forward;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="options",required=false,nillable=false)
	private String options;
	
	public void setOptions(String options){
		this.options = options;
	}
	
	public String getOptions(){
		return this.options;
	}
	
	
	
	
}