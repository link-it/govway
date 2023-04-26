/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for web complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="web"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="context" type="{http://www.openspcoop2.org/protocol/manifest}Context" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="emptyContext" type="{http://www.openspcoop2.org/protocol/manifest}WebEmptyContext" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "web", 
  propOrder = {
  	"context",
  	"emptyContext"
  }
)

@XmlRootElement(name = "web")

public class Web extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Web() {
    super();
  }

  public void addContext(Context context) {
    this.context.add(context);
  }

  public Context getContext(int index) {
    return this.context.get( index );
  }

  public Context removeContext(int index) {
    return this.context.remove( index );
  }

  public List<Context> getContextList() {
    return this.context;
  }

  public void setContextList(List<Context> context) {
    this.context=context;
  }

  public int sizeContextList() {
    return this.context.size();
  }

  public WebEmptyContext getEmptyContext() {
    return this.emptyContext;
  }

  public void setEmptyContext(WebEmptyContext emptyContext) {
    this.emptyContext = emptyContext;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="context",required=true,nillable=false)
  private List<Context> context = new ArrayList<>();

  /**
   * Use method getContextList
   * @return List&lt;Context&gt;
  */
  public List<Context> getContext() {
  	return this.getContextList();
  }

  /**
   * Use method setContextList
   * @param context List&lt;Context&gt;
  */
  public void setContext(List<Context> context) {
  	this.setContextList(context);
  }

  /**
   * Use method sizeContextList
   * @return lunghezza della lista
  */
  public int sizeContext() {
  	return this.sizeContextList();
  }

  @XmlElement(name="emptyContext",required=false,nillable=false)
  protected WebEmptyContext emptyContext;

}
