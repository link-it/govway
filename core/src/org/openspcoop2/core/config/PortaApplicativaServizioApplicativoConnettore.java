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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for porta-applicativa-servizio-applicativo-connettore complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="porta-applicativa-servizio-applicativo-connettore">
 * 		&lt;sequence>
 * 			&lt;element name="filtro" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="proprieta" type="{http://www.openspcoop2.org/core/config}proprieta" minOccurs="0" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="notifica" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional"/>
 * 		&lt;attribute name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "porta-applicativa-servizio-applicativo-connettore", 
  propOrder = {
  	"filtro",
  	"proprieta"
  }
)

@XmlRootElement(name = "porta-applicativa-servizio-applicativo-connettore")

public class PortaApplicativaServizioApplicativoConnettore extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public PortaApplicativaServizioApplicativoConnettore() {
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

  public void addFiltro(java.lang.String filtro) {
    this.filtro.add(filtro);
  }

  public java.lang.String getFiltro(int index) {
    return this.filtro.get( index );
  }

  public java.lang.String removeFiltro(int index) {
    return this.filtro.remove( index );
  }

  public List<java.lang.String> getFiltroList() {
    return this.filtro;
  }

  public void setFiltroList(List<java.lang.String> filtro) {
    this.filtro=filtro;
  }

  public int sizeFiltroList() {
    return this.filtro.size();
  }

  public void addProprieta(Proprieta proprieta) {
    this.proprieta.add(proprieta);
  }

  public Proprieta getProprieta(int index) {
    return this.proprieta.get( index );
  }

  public Proprieta removeProprieta(int index) {
    return this.proprieta.remove( index );
  }

  public List<Proprieta> getProprietaList() {
    return this.proprieta;
  }

  public void setProprietaList(List<Proprieta> proprieta) {
    this.proprieta=proprieta;
  }

  public int sizeProprietaList() {
    return this.proprieta.size();
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

  public boolean isNotifica() {
    return this.notifica;
  }

  public boolean getNotifica() {
    return this.notifica;
  }

  public void setNotifica(boolean notifica) {
    this.notifica = notifica;
  }

  public void set_value_stato(String value) {
    this.stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_stato() {
    if(this.stato == null){
    	return null;
    }else{
    	return this.stato.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getStato() {
    return this.stato;
  }

  public void setStato(org.openspcoop2.core.config.constants.StatoFunzionalita stato) {
    this.stato = stato;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="filtro",required=true,nillable=false)
  protected List<java.lang.String> filtro = new ArrayList<java.lang.String>();

  /**
   * @deprecated Use method getFiltroList
   * @return List<java.lang.String>
  */
  @Deprecated
  public List<java.lang.String> getFiltro() {
  	return this.filtro;
  }

  /**
   * @deprecated Use method setFiltroList
   * @param filtro List<java.lang.String>
  */
  @Deprecated
  public void setFiltro(List<java.lang.String> filtro) {
  	this.filtro=filtro;
  }

  /**
   * @deprecated Use method sizeFiltroList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeFiltro() {
  	return this.filtro.size();
  }

  @XmlElement(name="proprieta",required=true,nillable=false)
  protected List<Proprieta> proprieta = new ArrayList<Proprieta>();

  /**
   * @deprecated Use method getProprietaList
   * @return List<Proprieta>
  */
  @Deprecated
  public List<Proprieta> getProprieta() {
  	return this.proprieta;
  }

  /**
   * @deprecated Use method setProprietaList
   * @param proprieta List<Proprieta>
  */
  @Deprecated
  public void setProprieta(List<Proprieta> proprieta) {
  	this.proprieta=proprieta;
  }

  /**
   * @deprecated Use method sizeProprietaList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeProprieta() {
  	return this.proprieta.size();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=false)
  protected java.lang.String descrizione;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="notifica",required=false)
  protected boolean notifica;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_stato;

  @XmlAttribute(name="stato",required=false)
  protected StatoFunzionalita stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

}
