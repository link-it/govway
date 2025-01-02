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
package org.openspcoop2.core.config.ws.server.filter.beans;

/**
 * <p>Java class for InvocazionePortaGestioneErrore complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="invocazione-porta-gestione-errore"&gt;
 *     &lt;sequence&gt;
 *         &lt;element name="fault" type="{http://www.openspcoop2.org/core/config}FaultIntegrazioneTipo" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="fault-actor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="generic-fault-code" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="prefix-fault-code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *     &lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.config.constants.FaultIntegrazioneTipo;
import org.openspcoop2.core.config.constants.StatoFunzionalita;

/**     
 * InvocazionePortaGestioneErrore
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "invocazione-porta-gestione-errore", namespace="http://www.openspcoop2.org/core/config/management", propOrder = {
    "fault",
    "faultActor",
    "genericFaultCode",
    "prefixFaultCode"
})
@javax.xml.bind.annotation.XmlRootElement(name = "invocazione-porta-gestione-errore")
public class InvocazionePortaGestioneErrore extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="fault",required=false,nillable=false)
	private FaultIntegrazioneTipo fault;
	
	public void setFault(FaultIntegrazioneTipo fault){
		this.fault = fault;
	}
	
	public FaultIntegrazioneTipo getFault(){
		return this.fault;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="fault-actor",required=false,nillable=false)
	private String faultActor;
	
	public void setFaultActor(String faultActor){
		this.faultActor = faultActor;
	}
	
	public String getFaultActor(){
		return this.faultActor;
	}
	
	
	@XmlElement(name="generic-fault-code",required=false,nillable=false)
	private StatoFunzionalita genericFaultCode;
	
	public void setGenericFaultCode(StatoFunzionalita genericFaultCode){
		this.genericFaultCode = genericFaultCode;
	}
	
	public StatoFunzionalita getGenericFaultCode(){
		return this.genericFaultCode;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="prefix-fault-code",required=false,nillable=false)
	private String prefixFaultCode;
	
	public void setPrefixFaultCode(String prefixFaultCode){
		this.prefixFaultCode = prefixFaultCode;
	}
	
	public String getPrefixFaultCode(){
		return this.prefixFaultCode;
	}
	
	
	
	
}