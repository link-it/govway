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
import java.math.BigInteger;


/** <p>Java class IdleObjectEviction.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class IdleObjectEviction extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;

  protected String timeBetweenEvictionRuns;

  protected BigInteger numTestsPerEvictionRun;

  protected String idleObjectTimeout;

  protected boolean validateObject;


  public IdleObjectEviction() {
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

  public String getTimeBetweenEvictionRuns() {
    if(this.timeBetweenEvictionRuns!=null && ("".equals(this.timeBetweenEvictionRuns)==false)){
		return this.timeBetweenEvictionRuns.trim();
	}else{
		return null;
	}

  }

  public void setTimeBetweenEvictionRuns(String timeBetweenEvictionRuns) {
    this.timeBetweenEvictionRuns = timeBetweenEvictionRuns;
  }

  public BigInteger getNumTestsPerEvictionRun() {
    return this.numTestsPerEvictionRun;
  }

  public void setNumTestsPerEvictionRun(BigInteger numTestsPerEvictionRun) {
    this.numTestsPerEvictionRun = numTestsPerEvictionRun;
  }

  public String getIdleObjectTimeout() {
    if(this.idleObjectTimeout!=null && ("".equals(this.idleObjectTimeout)==false)){
		return this.idleObjectTimeout.trim();
	}else{
		return null;
	}

  }

  public void setIdleObjectTimeout(String idleObjectTimeout) {
    this.idleObjectTimeout = idleObjectTimeout;
  }

  public boolean isValidateObject() {
    return this.validateObject;
  }

  public boolean getValidateObject() {
    return this.validateObject;
  }

  public void setValidateObject(boolean validateObject) {
    this.validateObject = validateObject;
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

  public static final String TIME_BETWEEN_EVICTION_RUNS = "timeBetweenEvictionRuns";

  public static final String NUM_TESTS_PER_EVICTION_RUN = "numTestsPerEvictionRun";

  public static final String IDLE_OBJECT_TIMEOUT = "idleObjectTimeout";

  public static final String VALIDATE_OBJECT = "validateObject";

}
