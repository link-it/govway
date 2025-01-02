/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for roles complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="roles"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="role" type="{http://www.domibus.eu/configuration}role" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "roles", 
  propOrder = {
  	"role"
  }
)

@XmlRootElement(name = "roles")

public class Roles extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Roles() {
    super();
  }

  public void addRole(Role role) {
    this.role.add(role);
  }

  public Role getRole(int index) {
    return this.role.get( index );
  }

  public Role removeRole(int index) {
    return this.role.remove( index );
  }

  public List<Role> getRoleList() {
    return this.role;
  }

  public void setRoleList(List<Role> role) {
    this.role=role;
  }

  public int sizeRoleList() {
    return this.role.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="role",required=true,nillable=false)
  private List<Role> role = new ArrayList<>();

  /**
   * Use method getRoleList
   * @return List&lt;Role&gt;
  */
  public List<Role> getRole() {
  	return this.getRoleList();
  }

  /**
   * Use method setRoleList
   * @param role List&lt;Role&gt;
  */
  public void setRole(List<Role> role) {
  	this.setRoleList(role);
  }

  /**
   * Use method sizeRoleList
   * @return lunghezza della lista
  */
  public int sizeRole() {
  	return this.sizeRoleList();
  }

}
