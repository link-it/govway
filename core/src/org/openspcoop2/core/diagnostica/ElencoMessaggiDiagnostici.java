/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.core.diagnostica;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for elenco-messaggi-diagnostici complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="elenco-messaggi-diagnostici"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="messaggio-diagnostico" type="{http://www.openspcoop2.org/core/diagnostica}messaggio-diagnostico" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "elenco-messaggi-diagnostici", 
  propOrder = {
  	"messaggioDiagnostico"
  }
)

@XmlRootElement(name = "elenco-messaggi-diagnostici")

public class ElencoMessaggiDiagnostici extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ElencoMessaggiDiagnostici() {
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

  public void addMessaggioDiagnostico(MessaggioDiagnostico messaggioDiagnostico) {
    this.messaggioDiagnostico.add(messaggioDiagnostico);
  }

  public MessaggioDiagnostico getMessaggioDiagnostico(int index) {
    return this.messaggioDiagnostico.get( index );
  }

  public MessaggioDiagnostico removeMessaggioDiagnostico(int index) {
    return this.messaggioDiagnostico.remove( index );
  }

  public List<MessaggioDiagnostico> getMessaggioDiagnosticoList() {
    return this.messaggioDiagnostico;
  }

  public void setMessaggioDiagnosticoList(List<MessaggioDiagnostico> messaggioDiagnostico) {
    this.messaggioDiagnostico=messaggioDiagnostico;
  }

  public int sizeMessaggioDiagnosticoList() {
    return this.messaggioDiagnostico.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="messaggio-diagnostico",required=true,nillable=false)
  protected List<MessaggioDiagnostico> messaggioDiagnostico = new ArrayList<MessaggioDiagnostico>();

  /**
   * @deprecated Use method getMessaggioDiagnosticoList
   * @return List&lt;MessaggioDiagnostico&gt;
  */
  @Deprecated
  public List<MessaggioDiagnostico> getMessaggioDiagnostico() {
  	return this.messaggioDiagnostico;
  }

  /**
   * @deprecated Use method setMessaggioDiagnosticoList
   * @param messaggioDiagnostico List&lt;MessaggioDiagnostico&gt;
  */
  @Deprecated
  public void setMessaggioDiagnostico(List<MessaggioDiagnostico> messaggioDiagnostico) {
  	this.messaggioDiagnostico=messaggioDiagnostico;
  }

  /**
   * @deprecated Use method sizeMessaggioDiagnosticoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeMessaggioDiagnostico() {
  	return this.messaggioDiagnostico.size();
  }

}
