/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
import java.io.Serializable;


/** <p>Java class for accordo-servizio-parte-comune-servizio-composto-servizio-componente complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="accordo-servizio-parte-comune-servizio-composto-servizio-componente">
 * 		&lt;attribute name="id-servizio-componente" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/>
 * 		&lt;attribute name="tipo-soggetto" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="nome-soggetto" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="versione" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" use="optional" default="1"/>
 * 		&lt;attribute name="azione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "accordo-servizio-parte-comune-servizio-composto-servizio-componente")

@XmlRootElement(name = "accordo-servizio-parte-comune-servizio-composto-servizio-componente")

public class AccordoServizioParteComuneServizioCompostoServizioComponente extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public AccordoServizioParteComuneServizioCompostoServizioComponente() {
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

  public java.lang.Long getIdServizioComponente() {
    return this.idServizioComponente;
  }

  public void setIdServizioComponente(java.lang.Long idServizioComponente) {
    this.idServizioComponente = idServizioComponente;
  }

  public java.lang.String getTipoSoggetto() {
    return this.tipoSoggetto;
  }

  public void setTipoSoggetto(java.lang.String tipoSoggetto) {
    this.tipoSoggetto = tipoSoggetto;
  }

  public java.lang.String getNomeSoggetto() {
    return this.nomeSoggetto;
  }

  public void setNomeSoggetto(java.lang.String nomeSoggetto) {
    this.nomeSoggetto = nomeSoggetto;
  }

  public java.lang.String getTipo() {
    return this.tipo;
  }

  public void setTipo(java.lang.String tipo) {
    this.tipo = tipo;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.Integer getVersione() {
    return this.versione;
  }

  public void setVersione(java.lang.Integer versione) {
    this.versione = versione;
  }

  public java.lang.String getAzione() {
    return this.azione;
  }

  public void setAzione(java.lang.String azione) {
    this.azione = azione;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.Long idServizioComponente;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo-soggetto",required=true)
  protected java.lang.String tipoSoggetto;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome-soggetto",required=true)
  protected java.lang.String nomeSoggetto;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo",required=true)
  protected java.lang.String tipo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.Integer versione = java.lang.Integer.valueOf("1");

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="azione",required=false)
  protected java.lang.String azione;

}
