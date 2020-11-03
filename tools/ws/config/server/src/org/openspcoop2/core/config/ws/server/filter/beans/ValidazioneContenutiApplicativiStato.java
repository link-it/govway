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
 * <p>Java class for ValidazioneContenutiApplicativiStato complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="validazione-contenuti-applicativi-stato"&gt;
 *     &lt;sequence&gt;
 *         &lt;element name="configurazione-pattern" type="{http://www.openspcoop2.org/core/config/management}validazione-contenuti-applicativi-pattern" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalitaConWarning" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="tipo" type="{http://www.openspcoop2.org/core/config}ValidazioneContenutiApplicativiTipo" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="accept-mtom-message" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="soap-action" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="json-schema" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *     &lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.ws.server.filter.beans.ValidazioneContenutiApplicativiPattern;
import org.openspcoop2.core.config.constants.StatoFunzionalita;

/**     
 * ValidazioneContenutiApplicativiStato
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "validazione-contenuti-applicativi-stato", namespace="http://www.openspcoop2.org/core/config/management", propOrder = {
    "configurazionePattern",
    "stato",
    "tipo",
    "acceptMtomMessage",
    "soapAction",
    "jsonSchema"
})
@javax.xml.bind.annotation.XmlRootElement(name = "validazione-contenuti-applicativi-stato")
public class ValidazioneContenutiApplicativiStato extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="configurazione-pattern",required=false,nillable=false)
	private ValidazioneContenutiApplicativiPattern configurazionePattern;
	
	public void setConfigurazionePattern(ValidazioneContenutiApplicativiPattern configurazionePattern){
		this.configurazionePattern = configurazionePattern;
	}
	
	public ValidazioneContenutiApplicativiPattern getConfigurazionePattern(){
		return this.configurazionePattern;
	}
	
	
	@XmlElement(name="stato",required=false,nillable=false)
	private StatoFunzionalitaConWarning stato;
	
	public void setStato(StatoFunzionalitaConWarning stato){
		this.stato = stato;
	}
	
	public StatoFunzionalitaConWarning getStato(){
		return this.stato;
	}
	
	
	@XmlElement(name="tipo",required=false,nillable=false)
	private ValidazioneContenutiApplicativiTipo tipo;
	
	public void setTipo(ValidazioneContenutiApplicativiTipo tipo){
		this.tipo = tipo;
	}
	
	public ValidazioneContenutiApplicativiTipo getTipo(){
		return this.tipo;
	}
	
	
	@XmlElement(name="accept-mtom-message",required=false,nillable=false)
	private StatoFunzionalita acceptMtomMessage;
	
	public void setAcceptMtomMessage(StatoFunzionalita acceptMtomMessage){
		this.acceptMtomMessage = acceptMtomMessage;
	}
	
	public StatoFunzionalita getAcceptMtomMessage(){
		return this.acceptMtomMessage;
	}
	
	
	@XmlElement(name="soap-action",required=false,nillable=false)
	private StatoFunzionalita soapAction;
	
	public void setSoapAction(StatoFunzionalita soapAction){
		this.soapAction = soapAction;
	}
	
	public StatoFunzionalita getSoapAction(){
		return this.soapAction;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="json-schema",required=false,nillable=false)
	private String jsonSchema;
	
	public void setJsonSchema(String jsonSchema){
		this.jsonSchema = jsonSchema;
	}
	
	public String getJsonSchema(){
		return this.jsonSchema;
	}
	
	
	
	
}