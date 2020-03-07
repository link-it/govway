/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package it.gov.spcoop.sica.wsbl;

import it.gov.spcoop.sica.wsbl.constants.Mode;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for completionModeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="completionModeType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="mode" type="{http://spcoop.gov.it/sica/wsbl}mode" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="compensateMessage" type="{http://spcoop.gov.it/sica/wsbl}completionModeTypeCompensateMessage" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "completionModeType", 
  propOrder = {
  	"mode",
  	"compensateMessage"
  }
)

@XmlRootElement(name = "completionModeType")

public class CompletionModeType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public CompletionModeType() {
  }

  public void set_value_mode(String value) {
    this.mode = (Mode) Mode.toEnumConstantFromString(value);
  }

  public String get_value_mode() {
    if(this.mode == null){
    	return null;
    }else{
    	return this.mode.toString();
    }
  }

  public it.gov.spcoop.sica.wsbl.constants.Mode getMode() {
    return this.mode;
  }

  public void setMode(it.gov.spcoop.sica.wsbl.constants.Mode mode) {
    this.mode = mode;
  }

  public CompletionModeTypeCompensateMessage getCompensateMessage() {
    return this.compensateMessage;
  }

  public void setCompensateMessage(CompletionModeTypeCompensateMessage compensateMessage) {
    this.compensateMessage = compensateMessage;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_mode;

  @XmlElement(name="mode",required=true,nillable=false)
  protected Mode mode;

  @XmlElement(name="compensateMessage",required=false,nillable=false)
  protected CompletionModeTypeCompensateMessage compensateMessage;

}
