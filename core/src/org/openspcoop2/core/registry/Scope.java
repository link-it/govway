/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.registry.constants.ScopeContesto;
import java.io.Serializable;


/** <p>Java class for scope complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="scope"&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="tipologia" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="nome-esterno" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="contesto-utilizzo" type="{http://www.openspcoop2.org/core/registry}ScopeContesto" use="optional" default="qualsiasi"/&gt;
 * 		&lt;attribute name="ora-registrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="optional"/&gt;
 * 		&lt;attribute name="super-user" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="old-id-scope-for-update" type="{http://www.openspcoop2.org/core/registry}id-scope" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "scope")

@XmlRootElement(name = "scope")

public class Scope extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Scope() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return Long.valueOf(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=Long.valueOf(-1);
  }

  public IDScope getOldIDScopeForUpdate() {
    return this.oldIDScopeForUpdate;
  }

  public void setOldIDScopeForUpdate(IDScope oldIDScopeForUpdate) {
    this.oldIDScopeForUpdate=oldIDScopeForUpdate;
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

  public java.lang.String getTipologia() {
    return this.tipologia;
  }

  public void setTipologia(java.lang.String tipologia) {
    this.tipologia = tipologia;
  }

  public java.lang.String getNomeEsterno() {
    return this.nomeEsterno;
  }

  public void setNomeEsterno(java.lang.String nomeEsterno) {
    this.nomeEsterno = nomeEsterno;
  }

  public void set_value_contestoUtilizzo(String value) {
    this.contestoUtilizzo = (ScopeContesto) ScopeContesto.toEnumConstantFromString(value);
  }

  public String get_value_contestoUtilizzo() {
    if(this.contestoUtilizzo == null){
    	return null;
    }else{
    	return this.contestoUtilizzo.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.ScopeContesto getContestoUtilizzo() {
    return this.contestoUtilizzo;
  }

  public void setContestoUtilizzo(org.openspcoop2.core.registry.constants.ScopeContesto contestoUtilizzo) {
    this.contestoUtilizzo = contestoUtilizzo;
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

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.registry.model.ScopeModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.registry.Scope.modelStaticInstance==null){
  			org.openspcoop2.core.registry.Scope.modelStaticInstance = new org.openspcoop2.core.registry.model.ScopeModel();
	  }
  }
  public static org.openspcoop2.core.registry.model.ScopeModel model(){
	  if(org.openspcoop2.core.registry.Scope.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.registry.Scope.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlTransient
  protected IDScope oldIDScopeForUpdate;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=false)
  protected java.lang.String descrizione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipologia",required=false)
  protected java.lang.String tipologia;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome-esterno",required=false)
  protected java.lang.String nomeEsterno;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_contestoUtilizzo;

  @XmlAttribute(name="contesto-utilizzo",required=false)
  protected ScopeContesto contestoUtilizzo = (ScopeContesto) ScopeContesto.toEnumConstantFromString("qualsiasi");

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlAttribute(name="ora-registrazione",required=false)
  protected java.util.Date oraRegistrazione;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String superUser;

}
