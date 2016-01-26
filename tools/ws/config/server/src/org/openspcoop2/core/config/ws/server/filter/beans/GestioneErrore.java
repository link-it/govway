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
package org.openspcoop2.core.config.ws.server.filter.beans;

/**
 * <p>Java class for GestioneErrore complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="gestione-errore">
 *     &lt;sequence>
 *         &lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="comportamento" type="{http://www.openspcoop2.org/core/config}GestioneErroreComportamento" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="cadenza-rispedizione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
 import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.config.constants.GestioneErroreComportamento;

/**     
 * GestioneErrore
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "gestione-errore", namespace="http://www.openspcoop2.org/core/config/management", propOrder = {
    "nome",
    "comportamento",
    "cadenzaRispedizione"
})
@javax.xml.bind.annotation.XmlRootElement(name = "gestione-errore")
public class GestioneErrore extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable  {
	
	private static final long serialVersionUID = -1L;

	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=false,nillable=false)
	private String nome;
	
	public void setNome(String nome){
		this.nome = nome;
	}
	
	public String getNome(){
		return this.nome;
	}
	
	
	@XmlElement(name="comportamento",required=false,nillable=false)
	private GestioneErroreComportamento comportamento;
	
	public void setComportamento(GestioneErroreComportamento comportamento){
		this.comportamento = comportamento;
	}
	
	public GestioneErroreComportamento getComportamento(){
		return this.comportamento;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="cadenza-rispedizione",required=false,nillable=false)
	private String cadenzaRispedizione;
	
	public void setCadenzaRispedizione(String cadenzaRispedizione){
		this.cadenzaRispedizione = cadenzaRispedizione;
	}
	
	public String getCadenzaRispedizione(){
		return this.cadenzaRispedizione;
	}
	
	
	
	
}