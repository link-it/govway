/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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


/** <p>Java class for mpcs complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mpcs">
 * 		&lt;sequence>
 * 			&lt;element name="mpc" type="{http://www.domibus.eu/configuration}mpc" minOccurs="1" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mpcs", 
  propOrder = {
  	"mpc"
  }
)

@XmlRootElement(name = "mpcs")

public class Mpcs extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Mpcs() {
  }

  public void addMpc(Mpc mpc) {
    this.mpc.add(mpc);
  }

  public Mpc getMpc(int index) {
    return this.mpc.get( index );
  }

  public Mpc removeMpc(int index) {
    return this.mpc.remove( index );
  }

  public List<Mpc> getMpcList() {
    return this.mpc;
  }

  public void setMpcList(List<Mpc> mpc) {
    this.mpc=mpc;
  }

  public int sizeMpcList() {
    return this.mpc.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="mpc",required=true,nillable=false)
  protected List<Mpc> mpc = new ArrayList<Mpc>();

  /**
   * @deprecated Use method getMpcList
   * @return List<Mpc>
  */
  @Deprecated
  public List<Mpc> getMpc() {
  	return this.mpc;
  }

  /**
   * @deprecated Use method setMpcList
   * @param mpc List<Mpc>
  */
  @Deprecated
  public void setMpc(List<Mpc> mpc) {
  	this.mpc=mpc;
  }

  /**
   * @deprecated Use method sizeMpcList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeMpc() {
  	return this.mpc.size();
  }

}
