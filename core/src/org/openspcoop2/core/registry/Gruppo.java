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
package org.openspcoop2.core.registry;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import java.io.Serializable;


/** <p>Java class for gruppo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="gruppo"&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="service-binding" type="{http://www.openspcoop2.org/core/registry}ServiceBinding" use="optional"/&gt;
 * 		&lt;attribute name="ora-registrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="optional"/&gt;
 * 		&lt;attribute name="super-user" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="old-id-gruppo-for-update" type="{http://www.openspcoop2.org/core/registry}id-gruppo" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "gruppo")

@XmlRootElement(name = "gruppo")

public class Gruppo extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Gruppo() {
    super();
  }

  public IDGruppo getOldIDGruppoForUpdate() {
    return this.oldIDGruppoForUpdate;
  }

  public void setOldIDGruppoForUpdate(IDGruppo oldIDGruppoForUpdate) {
    this.oldIDGruppoForUpdate=oldIDGruppoForUpdate;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  public void setServiceBindingRawEnumValue(String value) {
    this.serviceBinding = (ServiceBinding) ServiceBinding.toEnumConstantFromString(value);
  }

  public String getServiceBindingRawEnumValue() {
    if(this.serviceBinding == null){
    	return null;
    }else{
    	return this.serviceBinding.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.ServiceBinding getServiceBinding() {
    return this.serviceBinding;
  }

  public void setServiceBinding(org.openspcoop2.core.registry.constants.ServiceBinding serviceBinding) {
    this.serviceBinding = serviceBinding;
  }

  public java.util.Date getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(java.util.Date oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  public java.lang.String getSuperUser() {
    return this.superUser;
  }

  public void setSuperUser(java.lang.String superUser) {
    this.superUser = superUser;
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.core.registry.model.GruppoModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.registry.Gruppo.modelStaticInstance==null){
  			org.openspcoop2.core.registry.Gruppo.modelStaticInstance = new org.openspcoop2.core.registry.model.GruppoModel();
	  }
  }
  public static org.openspcoop2.core.registry.model.GruppoModel model(){
	  if(org.openspcoop2.core.registry.Gruppo.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.registry.Gruppo.modelStaticInstance;
  }


  @jakarta.xml.bind.annotation.XmlTransient
  protected IDGruppo oldIDGruppoForUpdate;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=false)
  protected java.lang.String descrizione;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String serviceBindingRawEnumValue;

  @XmlAttribute(name="service-binding",required=false)
  protected ServiceBinding serviceBinding;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlAttribute(name="ora-registrazione",required=false)
  protected java.util.Date oraRegistrazione;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String superUser;

}
