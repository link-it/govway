/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package it.gov.fatturapa.sdi.messaggi.v1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for ListaErrori_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ListaErrori_Type">
 * 		&lt;sequence>
 * 			&lt;element name="Errore" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}Errore_Type" minOccurs="1" maxOccurs="200"/>
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
@XmlType(name = "ListaErrori_Type", 
  propOrder = {
  	"errore"
  }
)

@XmlRootElement(name = "ListaErrori_Type")

public class ListaErroriType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ListaErroriType() {
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

  public void addErrore(ErroreType errore) {
    this.errore.add(errore);
  }

  public ErroreType getErrore(int index) {
    return this.errore.get( index );
  }

  public ErroreType removeErrore(int index) {
    return this.errore.remove( index );
  }

  public List<ErroreType> getErroreList() {
    return this.errore;
  }

  public void setErroreList(List<ErroreType> errore) {
    this.errore=errore;
  }

  public int sizeErroreList() {
    return this.errore.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="Errore",required=true,nillable=false)
  protected List<ErroreType> errore = new ArrayList<ErroreType>();

  /**
   * @deprecated Use method getErroreList
   * @return List<ErroreType>
  */
  @Deprecated
  public List<ErroreType> getErrore() {
  	return this.errore;
  }

  /**
   * @deprecated Use method setErroreList
   * @param errore List<ErroreType>
  */
  @Deprecated
  public void setErrore(List<ErroreType> errore) {
  	this.errore=errore;
  }

  /**
   * @deprecated Use method sizeErroreList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeErrore() {
  	return this.errore.size();
  }

}
