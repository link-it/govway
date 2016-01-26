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
package org.openspcoop2.web.lib.audit.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/** <p>Java class Operation.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class Operation extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;

  protected java.lang.String interfaceMsg;

  protected String objectDetails;

  protected String tipologia;

  protected String tipoOggetto;

  protected String objectId;

  protected String objectOldId;

  protected String utente;

  protected String stato;

  protected String objectClass;

  protected String error;

  protected Date timeRequest;

  protected Date timeExecute;


  public Operation() {
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

  public String getInterfaceMsg() {
    if(this.interfaceMsg!=null && ("".equals(this.interfaceMsg)==false)){
		return this.interfaceMsg.trim();
	}else{
		return null;
	}

  }

  public void setInterfaceMsg(String interfaceMsg) {
    this.interfaceMsg=interfaceMsg;
  }

  public String getObjectDetails() {
    if(this.objectDetails!=null && ("".equals(this.objectDetails)==false)){
		return this.objectDetails.trim();
	}else{
		return null;
	}

  }

  public void setObjectDetails(String objectDetails) {
    this.objectDetails = objectDetails;
  }

  public void addBinary(Binary binary) {
    this.binary.add(binary);
  }

  public Binary getBinary(int index) {
    return this.binary.get( index );
  }

  public Binary removeBinary(int index) {
    return this.binary.remove( index );
  }

  public List<Binary> getBinaryList() {
    return this.binary;
  }

  public void setBinaryList(List<Binary> binary) {
    this.binary=binary;
  }

  public int sizeBinaryList() {
    return this.binary.size();
  }

  public String getTipologia() {
    if(this.tipologia!=null && ("".equals(this.tipologia)==false)){
		return this.tipologia.trim();
	}else{
		return null;
	}

  }

  public void setTipologia(String tipologia) {
    this.tipologia = tipologia;
  }

  public String getTipoOggetto() {
    if(this.tipoOggetto!=null && ("".equals(this.tipoOggetto)==false)){
		return this.tipoOggetto.trim();
	}else{
		return null;
	}

  }

  public void setTipoOggetto(String tipoOggetto) {
    this.tipoOggetto = tipoOggetto;
  }

  public String getObjectId() {
    if(this.objectId!=null && ("".equals(this.objectId)==false)){
		return this.objectId.trim();
	}else{
		return null;
	}

  }

  public void setObjectId(String objectId) {
    this.objectId = objectId;
  }

  public String getObjectOldId() {
    if(this.objectOldId!=null && ("".equals(this.objectOldId)==false)){
		return this.objectOldId.trim();
	}else{
		return null;
	}

  }

  public void setObjectOldId(String objectOldId) {
    this.objectOldId = objectOldId;
  }

  public String getUtente() {
    if(this.utente!=null && ("".equals(this.utente)==false)){
		return this.utente.trim();
	}else{
		return null;
	}

  }

  public void setUtente(String utente) {
    this.utente = utente;
  }

  public String getStato() {
    if(this.stato!=null && ("".equals(this.stato)==false)){
		return this.stato.trim();
	}else{
		return null;
	}

  }

  public void setStato(String stato) {
    this.stato = stato;
  }

  public String getObjectClass() {
    if(this.objectClass!=null && ("".equals(this.objectClass)==false)){
		return this.objectClass.trim();
	}else{
		return null;
	}

  }

  public void setObjectClass(String objectClass) {
    this.objectClass = objectClass;
  }

  public String getError() {
    if(this.error!=null && ("".equals(this.error)==false)){
		return this.error.trim();
	}else{
		return null;
	}

  }

  public void setError(String error) {
    this.error = error;
  }

  public Date getTimeRequest() {
    return this.timeRequest;
  }

  public void setTimeRequest(Date timeRequest) {
    this.timeRequest = timeRequest;
  }

  public Date getTimeExecute() {
    return this.timeExecute;
  }

  public void setTimeExecute(Date timeExecute) {
    this.timeExecute = timeExecute;
  }

  private static final long serialVersionUID = 1L;

  public static final String INTERFACE_MSG = "interfaceMsg";

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

  public static final String OBJECT_DETAILS = "objectDetails";

  protected List<Binary> binary = new ArrayList<Binary>();

  /**
   * @deprecated Use method getBinaryList
   * @return List<Binary>
  */
  @Deprecated
  public List<Binary> getBinary() {
  	return this.binary;
  }

  /**
   * @deprecated Use method setBinaryList
   * @param binary List<Binary>
  */
  @Deprecated
  public void setBinary(List<Binary> binary) {
  	this.binary=binary;
  }

  /**
   * @deprecated Use method sizeBinaryList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeBinary() {
  	return this.binary.size();
  }

  public static final String BINARY = "binary";

  public static final String TIPOLOGIA = "tipologia";

  public static final String TIPO_OGGETTO = "tipoOggetto";

  public static final String OBJECT_ID = "objectId";

  public static final String OBJECT_OLD_ID = "objectOldId";

  public static final String UTENTE = "utente";

  public static final String STATO = "stato";

  public static final String OBJECT_CLASS = "objectClass";

  public static final String ERROR = "error";

  public static final String TIME_REQUEST = "timeRequest";

  public static final String TIME_EXECUTE = "timeExecute";

}
