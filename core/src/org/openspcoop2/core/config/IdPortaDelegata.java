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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for id-porta-delegata complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="id-porta-delegata">
 * 		&lt;sequence>
 * 			&lt;element name="id-soggetto" type="{http://www.openspcoop2.org/core/config}id-soggetto" minOccurs="1" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "id-porta-delegata", 
  propOrder = {
  	"idSoggetto"
  }
)

@XmlRootElement(name = "id-porta-delegata")

public class IdPortaDelegata extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public IdPortaDelegata() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
  }

  public IdSoggetto getIdSoggetto() {
    return this.idSoggetto;
  }

  public void setIdSoggetto(IdSoggetto idSoggetto) {
    this.idSoggetto = idSoggetto;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  public IdPortaDelegata(org.openspcoop2.core.id.IDPortaDelegata idPortaDelegata){
  	if(idPortaDelegata!=null){
  		this.nome = idPortaDelegata.getLocationPD();
  		if(idPortaDelegata.getSoggettoFruitore()!=null){
  			this.idSoggetto = new IdSoggetto();
  			this.idSoggetto.setNome(idPortaDelegata.getSoggettoFruitore().getNome());
  			this.idSoggetto.setTipo(idPortaDelegata.getSoggettoFruitore().getTipo());
  		}
  	}
  }

  public org.openspcoop2.core.id.IDPortaDelegata toIDPortaDelegata() throws org.openspcoop2.core.commons.CoreException{
  	if(this.nome==null){
  		throw new org.openspcoop2.core.commons.CoreException("Nome undefined");
  	}
  	if(this.idSoggetto==null){
  		throw new org.openspcoop2.core.commons.CoreException("IdSoggetto undefined");
  	}
  	if(this.idSoggetto.getTipo()==null){
  		throw new org.openspcoop2.core.commons.CoreException("IdSoggetto.tipo undefined");
  	}
  	if(this.idSoggetto.getNome()==null){
  		throw new org.openspcoop2.core.commons.CoreException("IdSoggetto.nome undefined");
  	}
  	org.openspcoop2.core.id.IDPortaDelegata idPD = new org.openspcoop2.core.id.IDPortaDelegata();
  	idPD.setLocationPD(this.nome);
  	idPD.setSoggettoFruitore(this.idSoggetto.toIDSoggetto());
  	return idPD;
  }

  @XmlElement(name="id-soggetto",required=true,nillable=false)
  protected IdSoggetto idSoggetto;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

}
