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
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import java.io.Serializable;


/** <p>Java class for porta-dominio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="porta-dominio"&gt;
 * 		&lt;attribute name="super-user" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="implementazione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional" default="standard"/&gt;
 * 		&lt;attribute name="subject" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="client-auth" type="{http://www.openspcoop2.org/core/registry}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="ora-registrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="optional"/&gt;
 * 		&lt;attribute name="old-nome-for-update" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "porta-dominio")

@XmlRootElement(name = "porta-dominio")

public class PortaDominio extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public PortaDominio() {
    super();
  }

  public String getOldNomeForUpdate() {
    if(this.oldNomeForUpdate!=null && ("".equals(this.oldNomeForUpdate)==false)){
		return this.oldNomeForUpdate.trim();
	}else{
		return null;
	}

  }

  public void setOldNomeForUpdate(String oldNomeForUpdate) {
    this.oldNomeForUpdate=oldNomeForUpdate;
  }

  public java.lang.String getSuperUser() {
    return this.superUser;
  }

  public void setSuperUser(java.lang.String superUser) {
    this.superUser = superUser;
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

  public java.lang.String getImplementazione() {
    return this.implementazione;
  }

  public void setImplementazione(java.lang.String implementazione) {
    this.implementazione = implementazione;
  }

  public java.lang.String getSubject() {
    return this.subject;
  }

  public void setSubject(java.lang.String subject) {
    this.subject = subject;
  }

  public void setClientAuthRawEnumValue(String value) {
    this.clientAuth = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getClientAuthRawEnumValue() {
    if(this.clientAuth == null){
    	return null;
    }else{
    	return this.clientAuth.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.StatoFunzionalita getClientAuth() {
    return this.clientAuth;
  }

  public void setClientAuth(org.openspcoop2.core.registry.constants.StatoFunzionalita clientAuth) {
    this.clientAuth = clientAuth;
  }

  public java.util.Date getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(java.util.Date oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.core.registry.model.PortaDominioModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.registry.PortaDominio.modelStaticInstance==null){
  			org.openspcoop2.core.registry.PortaDominio.modelStaticInstance = new org.openspcoop2.core.registry.model.PortaDominioModel();
	  }
  }
  public static org.openspcoop2.core.registry.model.PortaDominioModel model(){
	  if(org.openspcoop2.core.registry.PortaDominio.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.registry.PortaDominio.modelStaticInstance;
  }


  @jakarta.xml.bind.annotation.XmlTransient
  protected String oldNomeForUpdate;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String superUser;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=false)
  protected java.lang.String descrizione;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="implementazione",required=false)
  protected java.lang.String implementazione = "standard";

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="subject",required=false)
  protected java.lang.String subject;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String clientAuthRawEnumValue;

  @XmlAttribute(name="client-auth",required=false)
  protected StatoFunzionalita clientAuth = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlAttribute(name="ora-registrazione",required=false)
  protected java.util.Date oraRegistrazione;

}
