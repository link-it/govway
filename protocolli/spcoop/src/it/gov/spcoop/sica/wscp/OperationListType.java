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
package it.gov.spcoop.sica.wscp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class OperationListType.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class OperationListType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;


  public OperationListType() {
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

	@Override
	public String serialize(org.openspcoop2.utils.beans.WriteToSerializerType type) throws org.openspcoop2.utils.UtilsException {
		if(type!=null && org.openspcoop2.utils.beans.WriteToSerializerType.JAXB.equals(type)){
			throw new org.openspcoop2.utils.UtilsException("Jaxb annotations not generated");
		}
		else{
			return super.serialize(type);
		}
	}
	@Override
	public String toXml_Jaxb() throws org.openspcoop2.utils.UtilsException {
		throw new org.openspcoop2.utils.UtilsException("Jaxb annotations not generated");
	}

  protected List<OperationType> collaborazione = new ArrayList<OperationType>();

  /**
   * @deprecated Use method getCollaborazioneList
   * @return List<OperationType>
  */
  @Deprecated
  public List<OperationType> getCollaborazione() {
  	return this.collaborazione;
  }

  /**
   * @deprecated Use method setCollaborazioneList
   * @param collaborazione List<OperationType>
  */
  @Deprecated
  public void setCollaborazione(List<OperationType> collaborazione) {
  	this.collaborazione=collaborazione;
  }

  /**
   * @deprecated Use method sizeCollaborazioneList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeCollaborazione() {
  	return this.collaborazione.size();
  }

  public static final String COLLABORAZIONE = "collaborazione";

}
