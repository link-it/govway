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
package org.openspcoop2.pdd.monitor.ws.server.filter.beans;

/**
 * <p>Java class for Filtro complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="filtro">
 *     &lt;sequence>
 *         &lt;element name="correlazione-applicativa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="busta" type="{http://www.openspcoop2.org/pdd/monitor/management}busta" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="id-messaggio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="message-pattern" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="soglia" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="stato" type="{http://www.openspcoop2.org/pdd/monitor}StatoMessaggio" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.pdd.monitor.constants.StatoMessaggio;
import org.openspcoop2.pdd.monitor.ws.server.filter.beans.Busta;

/**     
 * Filtro
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "filtro", namespace="http://www.openspcoop2.org/pdd/monitor/management", propOrder = {
    "correlazioneApplicativa",
    "busta",
    "idMessaggio",
    "messagePattern",
    "soglia",
    "stato",
    "tipo"
})
@javax.xml.bind.annotation.XmlRootElement(name = "filtro")
public class Filtro extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="correlazione-applicativa",required=false,nillable=false)
	private String correlazioneApplicativa;
	
	public void setCorrelazioneApplicativa(String correlazioneApplicativa){
		this.correlazioneApplicativa = correlazioneApplicativa;
	}
	
	public String getCorrelazioneApplicativa(){
		return this.correlazioneApplicativa;
	}
	
	
	@XmlElement(name="busta",required=false,nillable=false)
	private Busta busta;
	
	public void setBusta(Busta busta){
		this.busta = busta;
	}
	
	public Busta getBusta(){
		return this.busta;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-messaggio",required=false,nillable=false)
	private String idMessaggio;
	
	public void setIdMessaggio(String idMessaggio){
		this.idMessaggio = idMessaggio;
	}
	
	public String getIdMessaggio(){
		return this.idMessaggio;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="message-pattern",required=false,nillable=false)
	private String messagePattern;
	
	public void setMessagePattern(String messagePattern){
		this.messagePattern = messagePattern;
	}
	
	public String getMessagePattern(){
		return this.messagePattern;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="long")
  @XmlElement(name="soglia",required=false,nillable=false)
	private Long soglia;
	
	public void setSoglia(Long soglia){
		this.soglia = soglia;
	}
	
	public Long getSoglia(){
		return this.soglia;
	}
	
	
	@XmlElement(name="stato",required=false,nillable=false)
	private StatoMessaggio stato;
	
	public void setStato(StatoMessaggio stato){
		this.stato = stato;
	}
	
	public StatoMessaggio getStato(){
		return this.stato;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo",required=false,nillable=false)
	private String tipo;
	
	public void setTipo(String tipo){
		this.tipo = tipo;
	}
	
	public String getTipo(){
		return this.tipo;
	}
	
	
	
	
}