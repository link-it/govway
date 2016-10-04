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
package org.openspcoop2.core.diagnostica.ws.server.filter.beans;

/**
 * <p>Java class for DominioDiagnostico complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dominio-diagnostico">
 *     &lt;sequence>
 *         &lt;element name="identificativo-porta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="soggetto" type="{http://www.openspcoop2.org/core/diagnostica/management}dominio-soggetto" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="modulo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.diagnostica.ws.server.filter.beans.DominioSoggetto;

/**     
 * DominioDiagnostico
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "dominio-diagnostico", namespace="http://www.openspcoop2.org/core/diagnostica/management", propOrder = {
    "identificativoPorta",
    "soggetto",
    "modulo"
})
@javax.xml.bind.annotation.XmlRootElement(name = "dominio-diagnostico")
public class DominioDiagnostico extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="identificativo-porta",required=false,nillable=false)
	private String identificativoPorta;
	
	public void setIdentificativoPorta(String identificativoPorta){
		this.identificativoPorta = identificativoPorta;
	}
	
	public String getIdentificativoPorta(){
		return this.identificativoPorta;
	}
	
	
	@XmlElement(name="soggetto",required=false,nillable=false)
	private DominioSoggetto soggetto;
	
	public void setSoggetto(DominioSoggetto soggetto){
		this.soggetto = soggetto;
	}
	
	public DominioSoggetto getSoggetto(){
		return this.soggetto;
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
	
	
	
	
}