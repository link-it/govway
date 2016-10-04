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
package it.gov.spcoop.sica.wsbl;

import java.io.Serializable;


/** <p>Java class TemporalConditionType.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class TemporalConditionType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;

  protected String predicate;

  protected String boolop;

  protected String data;

  protected String description;


  public TemporalConditionType() {
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

  public String getPredicate() {
    if(this.predicate!=null && ("".equals(this.predicate)==false)){
		return this.predicate.trim();
	}else{
		return null;
	}

  }

  public void setPredicate(String predicate) {
    this.predicate = predicate;
  }

  public String getBoolop() {
    if(this.boolop!=null && ("".equals(this.boolop)==false)){
		return this.boolop.trim();
	}else{
		return null;
	}

  }

  public void setBoolop(String boolop) {
    this.boolop = boolop;
  }

  public String getData() {
    if(this.data!=null && ("".equals(this.data)==false)){
		return this.data.trim();
	}else{
		return null;
	}

  }

  public void setData(String data) {
    this.data = data;
  }

  public String getDescription() {
    if(this.description!=null && ("".equals(this.description)==false)){
		return this.description.trim();
	}else{
		return null;
	}

  }

  public void setDescription(String description) {
    this.description = description;
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

  public static final String PREDICATE = "predicate";

  public static final String BOOLOP = "boolop";

  public static final String DATA = "data";

  public static final String DESCRIPTION = "description";

}
