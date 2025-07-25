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
package eu.domibus.configuration;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for meps complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="meps"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="mep" type="{http://www.domibus.eu/configuration}mep" minOccurs="1" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="binding" type="{http://www.domibus.eu/configuration}binding" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "meps", 
  propOrder = {
  	"mep",
  	"binding"
  }
)

@XmlRootElement(name = "meps")

public class Meps extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Meps() {
    super();
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
  private List<Mep> mep = new ArrayList<>();

  /**
   * Use method getMepList
   * @return List&lt;Mep&gt;
  */
  public List<Mep> getMep() {
  	return this.getMepList();
  }

  /**
   * Use method setMepList
   * @param mep List&lt;Mep&gt;
  */
  public void setMep(List<Mep> mep) {
  	this.setMepList(mep);
  }

  /**
   * Use method sizeMepList
   * @return lunghezza della lista
  */
  public int sizeMep() {
  	return this.sizeMepList();
  }

  @XmlElement(name="binding",required=true,nillable=false)
  private List<Binding> binding = new ArrayList<>();

  /**
   * Use method getBindingList
   * @return List&lt;Binding&gt;
  */
  public List<Binding> getBinding() {
  	return this.getBindingList();
  }

  /**
   * Use method setBindingList
   * @param binding List&lt;Binding&gt;
  */
  public void setBinding(List<Binding> binding) {
  	this.setBindingList(binding);
  }

  /**
   * Use method sizeBindingList
   * @return lunghezza della lista
  */
  public int sizeBinding() {
  	return this.sizeBindingList();
  }

}
