/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for id-porta-applicativa complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="id-porta-applicativa"&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "id-porta-applicativa")

@XmlRootElement(name = "id-porta-applicativa")

public class IdPortaApplicativa extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public IdPortaApplicativa() {
    super();
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  private static final long serialVersionUID = 1L;



  public IdPortaApplicativa(org.openspcoop2.core.id.IDPortaApplicativa idPortaApplicativa){
  	if(idPortaApplicativa!=null){
  		this.nome = idPortaApplicativa.getNome();
  	}
  }

  public org.openspcoop2.core.id.IDPortaApplicativa toIDPortaApplicativa() throws org.openspcoop2.core.commons.CoreException{
  	if(this.nome==null){
  		throw new org.openspcoop2.core.commons.CoreException("Nome undefined");
  	}
  	org.openspcoop2.core.id.IDPortaApplicativa idPA = new org.openspcoop2.core.id.IDPortaApplicativa();
  	idPA.setNome(this.nome);
  	return idPA;
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

}
