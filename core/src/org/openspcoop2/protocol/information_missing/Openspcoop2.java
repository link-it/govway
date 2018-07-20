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
package org.openspcoop2.protocol.information_missing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for openspcoop2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="openspcoop2">
 * 		&lt;sequence>
 * 			&lt;element name="wizard" type="{http://www.openspcoop2.org/protocol/information_missing}Wizard" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="operazione" type="{http://www.openspcoop2.org/protocol/information_missing}operazione" minOccurs="1" maxOccurs="unbounded"/>
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
@XmlType(name = "openspcoop2", 
  propOrder = {
  	"wizard",
  	"operazione"
  }
)

@XmlRootElement(name = "openspcoop2")

public class Openspcoop2 extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Openspcoop2() {
  }

  public Wizard getWizard() {
    return this.wizard;
  }

  public void setWizard(Wizard wizard) {
    this.wizard = wizard;
  }

  public void addOperazione(Operazione operazione) {
    this.operazione.add(operazione);
  }

  public Operazione getOperazione(int index) {
    return this.operazione.get( index );
  }

  public Operazione removeOperazione(int index) {
    return this.operazione.remove( index );
  }

  public List<Operazione> getOperazioneList() {
    return this.operazione;
  }

  public void setOperazioneList(List<Operazione> operazione) {
    this.operazione=operazione;
  }

  public int sizeOperazioneList() {
    return this.operazione.size();
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.protocol.information_missing.model.Openspcoop2Model modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.protocol.information_missing.Openspcoop2.modelStaticInstance==null){
  			org.openspcoop2.protocol.information_missing.Openspcoop2.modelStaticInstance = new org.openspcoop2.protocol.information_missing.model.Openspcoop2Model();
	  }
  }
  public static org.openspcoop2.protocol.information_missing.model.Openspcoop2Model model(){
	  if(org.openspcoop2.protocol.information_missing.Openspcoop2.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.protocol.information_missing.Openspcoop2.modelStaticInstance;
  }


  @XmlElement(name="wizard",required=false,nillable=false)
  protected Wizard wizard;

  @XmlElement(name="operazione",required=true,nillable=false)
  protected List<Operazione> operazione = new ArrayList<Operazione>();

  /**
   * @deprecated Use method getOperazioneList
   * @return List<Operazione>
  */
  @Deprecated
  public List<Operazione> getOperazione() {
  	return this.operazione;
  }

  /**
   * @deprecated Use method setOperazioneList
   * @param operazione List<Operazione>
  */
  @Deprecated
  public void setOperazione(List<Operazione> operazione) {
  	this.operazione=operazione;
  }

  /**
   * @deprecated Use method sizeOperazioneList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeOperazione() {
  	return this.operazione.size();
  }

}
