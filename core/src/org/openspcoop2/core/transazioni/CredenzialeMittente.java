/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.core.transazioni;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for credenziale-mittente complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="credenziale-mittente"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="credenziale" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="ora-registrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="ref-credenziale" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "credenziale-mittente", 
  propOrder = {
  	"tipo",
  	"credenziale",
  	"oraRegistrazione",
  	"refCredenziale"
  }
)

@XmlRootElement(name = "credenziale-mittente")

public class CredenzialeMittente extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public CredenzialeMittente() {
    super();
  }

  public java.lang.String getTipo() {
    return this.tipo;
  }

  public void setTipo(java.lang.String tipo) {
    this.tipo = tipo;
  }

  public java.lang.String getCredenziale() {
    return this.credenziale;
  }

  public void setCredenziale(java.lang.String credenziale) {
    this.credenziale = credenziale;
  }

  public java.util.Date getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(java.util.Date oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  public java.lang.Long getRefCredenziale() {
    return this.refCredenziale;
  }

  public void setRefCredenziale(java.lang.Long refCredenziale) {
    this.refCredenziale = refCredenziale;
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.core.transazioni.model.CredenzialeMittenteModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.transazioni.CredenzialeMittente.modelStaticInstance==null){
  			org.openspcoop2.core.transazioni.CredenzialeMittente.modelStaticInstance = new org.openspcoop2.core.transazioni.model.CredenzialeMittenteModel();
	  }
  }
  public static org.openspcoop2.core.transazioni.model.CredenzialeMittenteModel model(){
	  if(org.openspcoop2.core.transazioni.CredenzialeMittente.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.transazioni.CredenzialeMittente.modelStaticInstance;
  }


  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo",required=true,nillable=false)
  protected java.lang.String tipo;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="credenziale",required=true,nillable=false)
  protected java.lang.String credenziale;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="ora-registrazione",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date oraRegistrazione;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="unsignedLong")
  @XmlElement(name="ref-credenziale",required=false,nillable=false)
  protected java.lang.Long refCredenziale;

}
