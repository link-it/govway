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


package org.openspcoop2.pools.pdd.jms;

/**
 * JMSInfoContextProperty
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JMSInfoContextProperty  {
  
  protected String name;

  protected String value;


  public String getName() {
    if(this.name!=null && ("".equals(this.name)==false)){
		return this.name.trim();
	}else{
		return null;
	}

  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    if(this.value!=null && ("".equals(this.value)==false)){
		return this.value.trim();
	}else{
		return null;
	}

  }

  public void setValue(String value) {
    this.value = value;
  }


}
