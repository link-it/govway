/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.core.mapping;

import java.io.Serializable;


/**
 * ProtocolProperty
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProtocolProperty extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ProtocolProperty() {
  }
  public ProtocolProperty(org.openspcoop2.core.registry.ProtocolProperty pp) {
	  this.id = pp.getId();
	  this.name = pp.getName();
	  this.value = pp.getValue();
	  this.numberValue = pp.getNumberValue();
	  this.booleanValue = pp.getBooleanValue();
	  this.file = pp.getFile();
	  this.byteFile = pp.getByteFile();
	  this.tipoProprietario = pp.getTipoProprietario();
	  this.idProprietario = pp.getIdProprietario();
  }
  public ProtocolProperty(org.openspcoop2.core.config.ProtocolProperty pp) {
	  this.id = pp.getId();
	  this.name = pp.getName();
	  this.value = pp.getValue();
	  this.numberValue = pp.getNumberValue();
	  this.booleanValue = pp.getBooleanValue();
	  this.file = pp.getFile();
	  this.byteFile = pp.getByteFile();
	  this.tipoProprietario = pp.getTipoProprietario();
	  this.idProprietario = pp.getIdProprietario();
  }

  public org.openspcoop2.core.registry.ProtocolProperty toRegistry(){
	  org.openspcoop2.core.registry.ProtocolProperty pp = new org.openspcoop2.core.registry.ProtocolProperty();
	  pp.setId(this.id);
	  pp.setName(this.name);
	  pp.setValue(this.value);
	  pp.setNumberValue(this.numberValue);
	  pp.setBooleanValue(this.booleanValue);
	  pp.setFile(this.file);
	  pp.setByteFile(this.byteFile);
	  pp.setTipoProprietario(this.tipoProprietario);
	  pp.setIdProprietario(this.idProprietario);
	  return pp;
  }
  
  public org.openspcoop2.core.config.ProtocolProperty toConfig(){
	  org.openspcoop2.core.config.ProtocolProperty pp = new org.openspcoop2.core.config.ProtocolProperty();
	  pp.setId(this.id);
	  pp.setName(this.name);
	  pp.setValue(this.value);
	  pp.setNumberValue(this.numberValue);
	  pp.setBooleanValue(this.booleanValue);
	  pp.setFile(this.file);
	  pp.setByteFile(this.byteFile);
	  pp.setTipoProprietario(this.tipoProprietario);
	  pp.setIdProprietario(this.idProprietario);
	  return pp;
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

  public java.lang.String getName() {
    return this.name;
  }

  public void setName(java.lang.String name) {
    this.name = name;
  }

  public java.lang.String getValue() {
    return this.value;
  }

  public void setValue(java.lang.String value) {
    this.value = value;
  }

  public java.lang.Long getNumberValue() {
    return this.numberValue;
  }

  public void setNumberValue(java.lang.Long numberValue) {
    this.numberValue = numberValue;
  }

  public Boolean getBooleanValue() {
    return this.booleanValue;
  }

  public void setBooleanValue(Boolean booleanValue) {
    this.booleanValue = booleanValue;
  }

  public java.lang.String getFile() {
    return this.file;
  }

  public void setFile(java.lang.String file) {
    this.file = file;
  }

  public byte[] getByteFile() {
    return this.byteFile;
  }

  public void setByteFile(byte[] byteFile) {
    this.byteFile = byteFile;
  }

  public java.lang.String getTipoProprietario() {
    return this.tipoProprietario;
  }

  public void setTipoProprietario(java.lang.String tipoProprietario) {
    this.tipoProprietario = tipoProprietario;
  }

  public java.lang.Long getIdProprietario() {
    return this.idProprietario;
  }

  public void setIdProprietario(java.lang.Long idProprietario) {
    this.idProprietario = idProprietario;
  }

  private static final long serialVersionUID = 1L;

  private Long id;

  protected java.lang.String name;

  protected java.lang.String value;

  protected java.lang.Long numberValue;

  protected Boolean booleanValue;

  protected java.lang.String file;

  protected byte[] byteFile;

  protected java.lang.String tipoProprietario;

  protected java.lang.Long idProprietario;

}
