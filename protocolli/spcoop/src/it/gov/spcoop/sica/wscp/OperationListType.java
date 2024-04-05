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
package it.gov.spcoop.sica.wscp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for operationListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="operationListType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="collaborazione" type="{http://spcoop.gov.it/sica/wscp}operationType" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "operationListType", 
  propOrder = {
  	"collaborazione"
  }
)

@XmlRootElement(name = "operationListType")

public class OperationListType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public OperationListType() {
    super();
  }

  public void addCollaborazione(OperationType collaborazione) {
    this.collaborazione.add(collaborazione);
  }

  public OperationType getCollaborazione(int index) {
    return this.collaborazione.get( index );
  }

  public OperationType removeCollaborazione(int index) {
    return this.collaborazione.remove( index );
  }

  public List<OperationType> getCollaborazioneList() {
    return this.collaborazione;
  }

  public void setCollaborazioneList(List<OperationType> collaborazione) {
    this.collaborazione=collaborazione;
  }

  public int sizeCollaborazioneList() {
    return this.collaborazione.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="collaborazione",required=true,nillable=false)
  private List<OperationType> collaborazione = new ArrayList<>();

  /**
   * Use method getCollaborazioneList
   * @return List&lt;OperationType&gt;
  */
  public List<OperationType> getCollaborazione() {
  	return this.getCollaborazioneList();
  }

  /**
   * Use method setCollaborazioneList
   * @param collaborazione List&lt;OperationType&gt;
  */
  public void setCollaborazione(List<OperationType> collaborazione) {
  	this.setCollaborazioneList(collaborazione);
  }

  /**
   * Use method sizeCollaborazioneList
   * @return lunghezza della lista
  */
  public int sizeCollaborazione() {
  	return this.sizeCollaborazioneList();
  }

}
