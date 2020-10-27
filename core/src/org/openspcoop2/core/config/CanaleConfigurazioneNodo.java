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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for canale-configurazione-nodo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="canale-configurazione-nodo"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="canale" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "canale-configurazione-nodo", 
  propOrder = {
  	"nome",
  	"descrizione",
  	"canale"
  }
)

@XmlRootElement(name = "canale-configurazione-nodo")

public class CanaleConfigurazioneNodo extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public CanaleConfigurazioneNodo() {
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

  public void addCanale(java.lang.String canale) {
    this.canale.add(canale);
  }

  public java.lang.String getCanale(int index) {
    return this.canale.get( index );
  }

  public java.lang.String removeCanale(int index) {
    return this.canale.remove( index );
  }

  public List<java.lang.String> getCanaleList() {
    return this.canale;
  }

  public void setCanaleList(List<java.lang.String> canale) {
    this.canale=canale;
  }

  public int sizeCanaleList() {
    return this.canale.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=true,nillable=false)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="descrizione",required=false,nillable=false)
  protected java.lang.String descrizione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="canale",required=true,nillable=false)
  protected List<java.lang.String> canale = new ArrayList<java.lang.String>();

  /**
   * @deprecated Use method getCanaleList
   * @return List&lt;java.lang.String&gt;
  */
  @Deprecated
  public List<java.lang.String> getCanale() {
  	return this.canale;
  }

  /**
   * @deprecated Use method setCanaleList
   * @param canale List&lt;java.lang.String&gt;
  */
  @Deprecated
  public void setCanale(List<java.lang.String> canale) {
  	this.canale=canale;
  }

  /**
   * @deprecated Use method sizeCanaleList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeCanale() {
  	return this.canale.size();
  }

}
