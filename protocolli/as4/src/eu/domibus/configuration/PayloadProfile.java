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
package eu.domibus.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for payloadProfile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="payloadProfile"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="attachment" type="{http://www.domibus.eu/configuration}attachment" minOccurs="1" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="name" type="{http://www.domibus.eu/configuration}string" use="required"/&gt;
 * 		&lt;attribute name="maxSize" type="{http://www.w3.org/2001/XMLSchema}integer" use="required"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "payloadProfile", 
  propOrder = {
  	"attachment"
  }
)

@XmlRootElement(name = "payloadProfile")

public class PayloadProfile extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public PayloadProfile() {
  }

  public void addAttachment(Attachment attachment) {
    this.attachment.add(attachment);
  }

  public Attachment getAttachment(int index) {
    return this.attachment.get( index );
  }

  public Attachment removeAttachment(int index) {
    return this.attachment.remove( index );
  }

  public List<Attachment> getAttachmentList() {
    return this.attachment;
  }

  public void setAttachmentList(List<Attachment> attachment) {
    this.attachment=attachment;
  }

  public int sizeAttachmentList() {
    return this.attachment.size();
  }

  public java.lang.String getName() {
    return this.name;
  }

  public void setName(java.lang.String name) {
    this.name = name;
  }

  public java.math.BigInteger getMaxSize() {
    return this.maxSize;
  }

  public void setMaxSize(java.math.BigInteger maxSize) {
    this.maxSize = maxSize;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="attachment",required=true,nillable=false)
  protected List<Attachment> attachment = new ArrayList<Attachment>();

  /**
   * @deprecated Use method getAttachmentList
   * @return List&lt;Attachment&gt;
  */
  @Deprecated
  public List<Attachment> getAttachment() {
  	return this.attachment;
  }

  /**
   * @deprecated Use method setAttachmentList
   * @param attachment List&lt;Attachment&gt;
  */
  @Deprecated
  public void setAttachment(List<Attachment> attachment) {
  	this.attachment=attachment;
  }

  /**
   * @deprecated Use method sizeAttachmentList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeAttachment() {
  	return this.attachment.size();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="name",required=true)
  protected java.lang.String name;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlAttribute(name="maxSize",required=true)
  protected java.math.BigInteger maxSize;

}
