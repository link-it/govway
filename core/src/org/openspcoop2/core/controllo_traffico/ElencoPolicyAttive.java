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
package org.openspcoop2.core.controllo_traffico;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for elenco-policy-attive complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="elenco-policy-attive"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="attivazione-policy" type="{http://www.openspcoop2.org/core/controllo_traffico}attivazione-policy" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "elenco-policy-attive", 
  propOrder = {
  	"attivazionePolicy"
  }
)

@XmlRootElement(name = "elenco-policy-attive")

public class ElencoPolicyAttive extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ElencoPolicyAttive() {
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

  public void addAttivazionePolicy(AttivazionePolicy attivazionePolicy) {
    this.attivazionePolicy.add(attivazionePolicy);
  }

  public AttivazionePolicy getAttivazionePolicy(int index) {
    return this.attivazionePolicy.get( index );
  }

  public AttivazionePolicy removeAttivazionePolicy(int index) {
    return this.attivazionePolicy.remove( index );
  }

  public List<AttivazionePolicy> getAttivazionePolicyList() {
    return this.attivazionePolicy;
  }

  public void setAttivazionePolicyList(List<AttivazionePolicy> attivazionePolicy) {
    this.attivazionePolicy=attivazionePolicy;
  }

  public int sizeAttivazionePolicyList() {
    return this.attivazionePolicy.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="attivazione-policy",required=true,nillable=false)
  protected List<AttivazionePolicy> attivazionePolicy = new ArrayList<AttivazionePolicy>();

  /**
   * @deprecated Use method getAttivazionePolicyList
   * @return List&lt;AttivazionePolicy&gt;
  */
  @Deprecated
  public List<AttivazionePolicy> getAttivazionePolicy() {
  	return this.attivazionePolicy;
  }

  /**
   * @deprecated Use method setAttivazionePolicyList
   * @param attivazionePolicy List&lt;AttivazionePolicy&gt;
  */
  @Deprecated
  public void setAttivazionePolicy(List<AttivazionePolicy> attivazionePolicy) {
  	this.attivazionePolicy=attivazionePolicy;
  }

  /**
   * @deprecated Use method sizeAttivazionePolicyList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeAttivazionePolicy() {
  	return this.attivazionePolicy.size();
  }

}
