/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import java.io.Serializable;


/** <p>Java class for ruolo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ruolo">
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="tipologia" type="{http://www.openspcoop2.org/core/registry}RuoloTipologia" use="optional" default="qualsiasi"/>
 * 		&lt;attribute name="nome-esterno" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="contesto-utilizzo" type="{http://www.openspcoop2.org/core/registry}RuoloContesto" use="optional" default="qualsiasi"/>
 * 		&lt;attribute name="ora-registrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="optional"/>
 * 		&lt;attribute name="super-user" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="old-id-ruolo-for-update" type="{http://www.openspcoop2.org/core/id}id-ruolo" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ruolo")

@XmlRootElement(name = "ruolo")

public class Ruolo extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Ruolo() {
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

  public IDRuolo getOldIDRuoloForUpdate() {
    return this.oldIDRuoloForUpdate;
  }

  public void setOldIDRuoloForUpdate(IDRuolo oldIDRuoloForUpdate) {
    this.oldIDRuoloForUpdate=oldIDRuoloForUpdate;
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

  public void set_value_tipologia(String value) {
    this.tipologia = (RuoloTipologia) RuoloTipologia.toEnumConstantFromString(value);
  }

  public String get_value_tipologia() {
    if(this.tipologia == null){
    	return null;
    }else{
    	return this.tipologia.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.RuoloTipologia getTipologia() {
    return this.tipologia;
  }

  public void setTipologia(org.openspcoop2.core.registry.constants.RuoloTipologia tipologia) {
    this.tipologia = tipologia;
  }

  public java.lang.String getNomeEsterno() {
    return this.nomeEsterno;
  }

  public void setNomeEsterno(java.lang.String nomeEsterno) {
    this.nomeEsterno = nomeEsterno;
  }

  public void set_value_contestoUtilizzo(String value) {
    this.contestoUtilizzo = (RuoloContesto) RuoloContesto.toEnumConstantFromString(value);
  }

  public String get_value_contestoUtilizzo() {
    if(this.contestoUtilizzo == null){
    	return null;
    }else{
    	return this.contestoUtilizzo.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.RuoloContesto getContestoUtilizzo() {
    return this.contestoUtilizzo;
  }

  public void setContestoUtilizzo(org.openspcoop2.core.registry.constants.RuoloContesto contestoUtilizzo) {
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

  private static org.openspcoop2.core.registry.model.RuoloModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.registry.Ruolo.modelStaticInstance==null){
  			org.openspcoop2.core.registry.Ruolo.modelStaticInstance = new org.openspcoop2.core.registry.model.RuoloModel();
	  }
  }
  public static org.openspcoop2.core.registry.model.RuoloModel model(){
	  if(org.openspcoop2.core.registry.Ruolo.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.registry.Ruolo.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlTransient
  protected IDRuolo oldIDRuoloForUpdate;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=false)
  protected java.lang.String descrizione;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_tipologia;

  @XmlAttribute(name="tipologia",required=false)
  protected RuoloTipologia tipologia = (RuoloTipologia) RuoloTipologia.toEnumConstantFromString("qualsiasi");

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome-esterno",required=false)
  protected java.lang.String nomeEsterno;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_contestoUtilizzo;

  @XmlAttribute(name="contesto-utilizzo",required=false)
  protected RuoloContesto contestoUtilizzo = (RuoloContesto) RuoloContesto.toEnumConstantFromString("qualsiasi");

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlAttribute(name="ora-registrazione",required=false)
  protected java.util.Date oraRegistrazione;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String superUser;

}
