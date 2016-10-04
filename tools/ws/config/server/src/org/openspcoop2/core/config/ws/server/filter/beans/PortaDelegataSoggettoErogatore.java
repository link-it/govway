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
package org.openspcoop2.core.config.ws.server.filter.beans;

/**
 * <p>Java class for PortaDelegataSoggettoErogatore complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="porta-delegata-soggetto-erogatore">
 *     &lt;sequence>
 *         &lt;element name="identificazione" type="{http://www.openspcoop2.org/core/config}PortaDelegataSoggettoErogatoreIdentificazione" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="pattern" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.config.constants.PortaDelegataSoggettoErogatoreIdentificazione;

/**     
 * PortaDelegataSoggettoErogatore
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "porta-delegata-soggetto-erogatore", namespace="http://www.openspcoop2.org/core/config/management", propOrder = {
    "identificazione",
    "pattern",
    "tipo",
    "nome"
})
@javax.xml.bind.annotation.XmlRootElement(name = "porta-delegata-soggetto-erogatore")
public class PortaDelegataSoggettoErogatore extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="identificazione",required=false,nillable=false)
	private PortaDelegataSoggettoErogatoreIdentificazione identificazione;
	
	public void setIdentificazione(PortaDelegataSoggettoErogatoreIdentificazione identificazione){
		this.identificazione = identificazione;
	}
	
	public PortaDelegataSoggettoErogatoreIdentificazione getIdentificazione(){
		return this.identificazione;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="pattern",required=false,nillable=false)
	private String pattern;
	
	public void setPattern(String pattern){
		this.pattern = pattern;
	}
	
	public String getPattern(){
		return this.pattern;
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
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=false,nillable=false)
	private String nome;
	
	public void setNome(String nome){
		this.nome = nome;
	}
	
	public String getNome(){
		return this.nome;
	}
	
	
	
	
}