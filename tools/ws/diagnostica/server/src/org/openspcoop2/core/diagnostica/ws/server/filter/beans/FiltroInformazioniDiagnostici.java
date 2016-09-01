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
package org.openspcoop2.core.diagnostica.ws.server.filter.beans;

/**
 * <p>Java class for FiltroInformazioniDiagnostici complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="filtro-informazioni-diagnostici">
 *     &lt;sequence>
 *         &lt;element name="codice" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="messaggio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="severita" type="{http://www.openspcoop2.org/core/diagnostica}LivelloDiSeveritaType" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="modulo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="identificativo-risposta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;

/**     
 * FiltroInformazioniDiagnostici
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "filtro-informazioni-diagnostici", namespace="http://www.openspcoop2.org/core/diagnostica/management", propOrder = {
    "codice",
    "messaggio",
    "severita",
    "modulo",
    "identificativoRisposta"
})
@javax.xml.bind.annotation.XmlRootElement(name = "filtro-informazioni-diagnostici")
public class FiltroInformazioniDiagnostici extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="codice",required=false,nillable=false)
	private String codice;
	
	public void setCodice(String codice){
		this.codice = codice;
	}
	
	public String getCodice(){
		return this.codice;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="messaggio",required=false,nillable=false)
	private String messaggio;
	
	public void setMessaggio(String messaggio){
		this.messaggio = messaggio;
	}
	
	public String getMessaggio(){
		return this.messaggio;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="positiveInteger")
  @XmlElement(name="severita",required=false,nillable=false)
	private Integer severita;
	
	public void setSeverita(Integer severita){
		this.severita = severita;
	}
	
	public Integer getSeverita(){
		return this.severita;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="modulo",required=false,nillable=false)
	private String modulo;
	
	public void setModulo(String modulo){
		this.modulo = modulo;
	}
	
	public String getModulo(){
		return this.modulo;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="identificativo-risposta",required=false,nillable=false)
	private String identificativoRisposta;
	
	public void setIdentificativoRisposta(String identificativoRisposta){
		this.identificativoRisposta = identificativoRisposta;
	}
	
	public String getIdentificativoRisposta(){
		return this.identificativoRisposta;
	}
	
	
	
	
}