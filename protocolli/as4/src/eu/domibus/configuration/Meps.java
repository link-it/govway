/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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
package eu.domibus.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for meps complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="meps">
 * 		&lt;sequence>
 * 			&lt;element name="mep" type="{http://www.domibus.eu/configuration}mep" minOccurs="1" maxOccurs="unbounded"/>
 * 			&lt;element name="binding" type="{http://www.domibus.eu/configuration}binding" minOccurs="1" maxOccurs="unbounded"/>
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
@XmlType(name = "meps", 
  propOrder = {
  	"mep",
  	"binding"
  }
)

@XmlRootElement(name = "meps")

public class Meps extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Meps() {
  }

  public void addMep(Mep mep) {
    this.mep.add(mep);
  }

  public Mep getMep(int index) {
    return this.mep.get( index );
  }

  public Mep removeMep(int index) {
    return this.mep.remove( index );
  }

  public List<Mep> getMepList() {
    return this.mep;
  }

  public void setMepList(List<Mep> mep) {
    this.mep=mep;
  }

  public int sizeMepList() {
    return this.mep.size();
  }

  public void addBinding(Binding binding) {
    this.binding.add(binding);
  }

  public Binding getBinding(int index) {
    return this.binding.get( index );
  }

  public Binding removeBinding(int index) {
    return this.binding.remove( index );
  }

  public List<Binding> getBindingList() {
    return this.binding;
  }

  public void setBindingList(List<Binding> binding) {
    this.binding=binding;
  }

  public int sizeBindingList() {
    return this.binding.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="mep",required=true,nillable=false)
  protected List<Mep> mep = new ArrayList<Mep>();

  /**
   * @deprecated Use method getMepList
   * @return List<Mep>
  */
  @Deprecated
  public List<Mep> getMep() {
  	return this.mep;
  }

  /**
   * @deprecated Use method setMepList
   * @param mep List<Mep>
  */
  @Deprecated
  public void setMep(List<Mep> mep) {
  	this.mep=mep;
  }

  /**
   * @deprecated Use method sizeMepList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeMep() {
  	return this.mep.size();
  }

  @XmlElement(name="binding",required=true,nillable=false)
  protected List<Binding> binding = new ArrayList<Binding>();

  /**
   * @deprecated Use method getBindingList
   * @return List<Binding>
  */
  @Deprecated
  public List<Binding> getBinding() {
  	return this.binding;
  }

  /**
   * @deprecated Use method setBindingList
   * @param binding List<Binding>
  */
  @Deprecated
  public void setBinding(List<Binding> binding) {
  	this.binding=binding;
  }

  /**
   * @deprecated Use method sizeBindingList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeBinding() {
  	return this.binding.size();
  }

}
