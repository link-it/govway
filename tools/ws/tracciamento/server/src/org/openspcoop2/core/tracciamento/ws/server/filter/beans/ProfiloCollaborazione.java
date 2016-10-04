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
package org.openspcoop2.core.tracciamento.ws.server.filter.beans;

/**
 * <p>Java class for ProfiloCollaborazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="profilo-collaborazione">
 *     &lt;sequence>
 *         &lt;element name="base" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="tipo" type="{http://www.openspcoop2.org/core/tracciamento}TipoProfiloCollaborazione" minOccurs="0" maxOccurs="1" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.tracciamento.constants.TipoProfiloCollaborazione;

/**     
 * ProfiloCollaborazione
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "profilo-collaborazione", namespace="http://www.openspcoop2.org/core/tracciamento/management", propOrder = {
    "base",
    "tipo"
})
@javax.xml.bind.annotation.XmlRootElement(name = "profilo-collaborazione")
public class ProfiloCollaborazione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="base",required=false,nillable=false)
	private String base;
	
	public void setBase(String base){
		this.base = base;
	}
	
	public String getBase(){
		return this.base;
	}
	
	
	@XmlElement(name="tipo",required=false,nillable=false)
	private TipoProfiloCollaborazione tipo;
	
	public void setTipo(TipoProfiloCollaborazione tipo){
		this.tipo = tipo;
	}
	
	public TipoProfiloCollaborazione getTipo(){
		return this.tipo;
	}
	
	
	
	
}