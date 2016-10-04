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


/** <p>Java class for accesso-registro complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="accesso-registro">
 * 		&lt;sequence>
 * 			&lt;element name="cache" type="{http://www.openspcoop2.org/core/config}cache" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="registro" type="{http://www.openspcoop2.org/core/config}accesso-registro-registro" minOccurs="1" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "accesso-registro", 
  propOrder = {
  	"cache",
  	"registro"
  }
)

@XmlRootElement(name = "accesso-registro")

public class AccessoRegistro extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public AccessoRegistro() {
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

  public Cache getCache() {
    return this.cache;
  }

  public void setCache(Cache cache) {
    this.cache = cache;
  }

  public void addRegistro(AccessoRegistroRegistro registro) {
    this.registro.add(registro);
  }

  public AccessoRegistroRegistro getRegistro(int index) {
    return this.registro.get( index );
  }

  public AccessoRegistroRegistro removeRegistro(int index) {
    return this.registro.remove( index );
  }

  public List<AccessoRegistroRegistro> getRegistroList() {
    return this.registro;
  }

  public void setRegistroList(List<AccessoRegistroRegistro> registro) {
    this.registro=registro;
  }

  public int sizeRegistroList() {
    return this.registro.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="cache",required=false,nillable=false)
  protected Cache cache;

  @XmlElement(name="registro",required=true,nillable=false)
  protected List<AccessoRegistroRegistro> registro = new ArrayList<AccessoRegistroRegistro>();

  /**
   * @deprecated Use method getRegistroList
   * @return List<AccessoRegistroRegistro>
  */
  @Deprecated
  public List<AccessoRegistroRegistro> getRegistro() {
  	return this.registro;
  }

  /**
   * @deprecated Use method setRegistroList
   * @param registro List<AccessoRegistroRegistro>
  */
  @Deprecated
  public void setRegistro(List<AccessoRegistroRegistro> registro) {
  	this.registro=registro;
  }

  /**
   * @deprecated Use method sizeRegistroList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeRegistro() {
  	return this.registro.size();
  }

}
