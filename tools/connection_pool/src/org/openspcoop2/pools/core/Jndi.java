/*
 * OpenSPCoop - Customizable API Gateway 
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
package org.openspcoop2.pools.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class Jndi.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class Jndi extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;


  public Jndi() {
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

  public void addContextProperty(ContextProperty contextProperty) {
    this.contextProperty.add(contextProperty);
  }

  public ContextProperty getContextProperty(int index) {
    return this.contextProperty.get( index );
  }

  public ContextProperty removeContextProperty(int index) {
    return this.contextProperty.remove( index );
  }

  public List<ContextProperty> getContextPropertyList() {
    return this.contextProperty;
  }

  public void setContextPropertyList(List<ContextProperty> contextProperty) {
    this.contextProperty=contextProperty;
  }

  public int sizeContextPropertyList() {
    return this.contextProperty.size();
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

  protected List<ContextProperty> contextProperty = new ArrayList<ContextProperty>();

  /**
   * @deprecated Use method getContextPropertyList
   * @return List<ContextProperty>
  */
  @Deprecated
  public List<ContextProperty> getContextProperty() {
  	return this.contextProperty;
  }

  /**
   * @deprecated Use method setContextPropertyList
   * @param contextProperty List<ContextProperty>
  */
  @Deprecated
  public void setContextProperty(List<ContextProperty> contextProperty) {
  	this.contextProperty=contextProperty;
  }

  /**
   * @deprecated Use method sizeContextPropertyList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeContextProperty() {
  	return this.contextProperty.size();
  }

  public static final String CONTEXT_PROPERTY = "contextProperty";

}
