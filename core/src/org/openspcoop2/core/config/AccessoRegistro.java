/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for accesso-registro complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="accesso-registro"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="cache" type="{http://www.openspcoop2.org/core/config}cache" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="registro" type="{http://www.openspcoop2.org/core/config}accesso-registro-registro" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "accesso-registro", 
  propOrder = {
  	"cache",
  	"registro"
  }
)

@XmlRootElement(name = "accesso-registro")

public class AccessoRegistro extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public AccessoRegistro() {
    super();
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



  @XmlElement(name="cache",required=false,nillable=false)
  protected Cache cache;

  @XmlElement(name="registro",required=true,nillable=false)
  private List<AccessoRegistroRegistro> registro = new ArrayList<>();

  /**
   * Use method getRegistroList
   * @return List&lt;AccessoRegistroRegistro&gt;
  */
  public List<AccessoRegistroRegistro> getRegistro() {
  	return this.getRegistroList();
  }

  /**
   * Use method setRegistroList
   * @param registro List&lt;AccessoRegistroRegistro&gt;
  */
  public void setRegistro(List<AccessoRegistroRegistro> registro) {
  	this.setRegistroList(registro);
  }

  /**
   * Use method sizeRegistroList
   * @return lunghezza della lista
  */
  public int sizeRegistro() {
  	return this.sizeRegistroList();
  }

}
