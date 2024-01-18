/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.core.commons.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for gruppo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="gruppo"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="service-binding" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "gruppo", 
  propOrder = {
  	"nome",
  	"serviceBinding"
  }
)

@XmlRootElement(name = "gruppo")

public class Gruppo extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Gruppo() {
    super();
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getServiceBinding() {
    return this.serviceBinding;
  }

  public void setServiceBinding(java.lang.String serviceBinding) {
    this.serviceBinding = serviceBinding;
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.core.commons.search.model.GruppoModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.commons.search.Gruppo.modelStaticInstance==null){
  			org.openspcoop2.core.commons.search.Gruppo.modelStaticInstance = new org.openspcoop2.core.commons.search.model.GruppoModel();
	  }
  }
  public static org.openspcoop2.core.commons.search.model.GruppoModel model(){
	  if(org.openspcoop2.core.commons.search.Gruppo.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.commons.search.Gruppo.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=true,nillable=false)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="service-binding",required=false,nillable=false)
  protected java.lang.String serviceBinding;

}
