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


/** <p>Java class Validation.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class Validation extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;

  protected String operation;

  protected boolean testOnGet;

  protected boolean testOnRelease;


  public Validation() {
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

  public String getOperation() {
    if(this.operation!=null && ("".equals(this.operation)==false)){
		return this.operation.trim();
	}else{
		return null;
	}

  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  public boolean isTestOnGet() {
    return this.testOnGet;
  }

  public boolean getTestOnGet() {
    return this.testOnGet;
  }

  public void setTestOnGet(boolean testOnGet) {
    this.testOnGet = testOnGet;
  }

  public boolean isTestOnRelease() {
    return this.testOnRelease;
  }

  public boolean getTestOnRelease() {
    return this.testOnRelease;
  }

  public void setTestOnRelease(boolean testOnRelease) {
    this.testOnRelease = testOnRelease;
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

  public static final String OPERATION = "operation";

  public static final String TEST_ON_GET = "testOnGet";

  public static final String TEST_ON_RELEASE = "testOnRelease";

}
