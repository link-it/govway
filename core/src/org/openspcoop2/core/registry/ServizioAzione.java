/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for servizio-azione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="servizio-azione">
 * 		&lt;sequence>
 * 			&lt;element name="connettore" type="{http://www.openspcoop2.org/core/registry}connettore" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="parametri-fruitore" type="{http://www.openspcoop2.org/core/registry}servizio-azione-fruitore" minOccurs="0" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "servizio-azione", 
  propOrder = {
  	"connettore",
  	"parametriFruitore"
  }
)

@XmlRootElement(name = "servizio-azione")

public class ServizioAzione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ServizioAzione() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
  }

  public Connettore getConnettore() {
    return this.connettore;
  }

  public void setConnettore(Connettore connettore) {
    this.connettore = connettore;
  }

  public void addParametriFruitore(ServizioAzioneFruitore parametriFruitore) {
    this.parametriFruitore.add(parametriFruitore);
  }

  public ServizioAzioneFruitore getParametriFruitore(int index) {
    return this.parametriFruitore.get( index );
  }

  public ServizioAzioneFruitore removeParametriFruitore(int index) {
    return this.parametriFruitore.remove( index );
  }

  public List<ServizioAzioneFruitore> getParametriFruitoreList() {
    return this.parametriFruitore;
  }

  public void setParametriFruitoreList(List<ServizioAzioneFruitore> parametriFruitore) {
    this.parametriFruitore=parametriFruitore;
  }

  public int sizeParametriFruitoreList() {
    return this.parametriFruitore.size();
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="connettore",required=true,nillable=false)
  protected Connettore connettore;

  @XmlElement(name="parametri-fruitore",required=true,nillable=false)
  protected List<ServizioAzioneFruitore> parametriFruitore = new ArrayList<ServizioAzioneFruitore>();

  /**
   * @deprecated Use method getParametriFruitoreList
   * @return List<ServizioAzioneFruitore>
  */
  @Deprecated
  public List<ServizioAzioneFruitore> getParametriFruitore() {
  	return this.parametriFruitore;
  }

  /**
   * @deprecated Use method setParametriFruitoreList
   * @param parametriFruitore List<ServizioAzioneFruitore>
  */
  @Deprecated
  public void setParametriFruitore(List<ServizioAzioneFruitore> parametriFruitore) {
  	this.parametriFruitore=parametriFruitore;
  }

  /**
   * @deprecated Use method sizeParametriFruitoreList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeParametriFruitore() {
  	return this.parametriFruitore.size();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

}
