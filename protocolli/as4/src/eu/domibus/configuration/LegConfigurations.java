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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for legConfigurations complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="legConfigurations"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="legConfiguration" type="{http://www.domibus.eu/configuration}legConfiguration" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "legConfigurations", 
  propOrder = {
  	"legConfiguration"
  }
)

@XmlRootElement(name = "legConfigurations")

public class LegConfigurations extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public LegConfigurations() {
    super();
  }

  public void addLegConfiguration(LegConfiguration legConfiguration) {
    this.legConfiguration.add(legConfiguration);
  }

  public LegConfiguration getLegConfiguration(int index) {
    return this.legConfiguration.get( index );
  }

  public LegConfiguration removeLegConfiguration(int index) {
    return this.legConfiguration.remove( index );
  }

  public List<LegConfiguration> getLegConfigurationList() {
    return this.legConfiguration;
  }

  public void setLegConfigurationList(List<LegConfiguration> legConfiguration) {
    this.legConfiguration=legConfiguration;
  }

  public int sizeLegConfigurationList() {
    return this.legConfiguration.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="legConfiguration",required=true,nillable=false)
  protected List<LegConfiguration> legConfiguration = new ArrayList<LegConfiguration>();

  /**
   * @deprecated Use method getLegConfigurationList
   * @return List&lt;LegConfiguration&gt;
  */
  @Deprecated
  public List<LegConfiguration> getLegConfiguration() {
  	return this.legConfiguration;
  }

  /**
   * @deprecated Use method setLegConfigurationList
   * @param legConfiguration List&lt;LegConfiguration&gt;
  */
  @Deprecated
  public void setLegConfiguration(List<LegConfiguration> legConfiguration) {
  	this.legConfiguration=legConfiguration;
  }

  /**
   * @deprecated Use method sizeLegConfigurationList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeLegConfiguration() {
  	return this.legConfiguration.size();
  }

}
