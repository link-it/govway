/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
import java.util.ArrayList;
import java.util.List;


/** <p>Java class StatesType.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class StatesType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;

  protected StateTypeInitial stateInitial;

  protected StateTypeFinal stateFinal;


  public StatesType() {
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

  public StateTypeInitial getStateInitial() {
    return this.stateInitial;
  }

  public void setStateInitial(StateTypeInitial stateInitial) {
    this.stateInitial = stateInitial;
  }

  public StateTypeFinal getStateFinal() {
    return this.stateFinal;
  }

  public void setStateFinal(StateTypeFinal stateFinal) {
    this.stateFinal = stateFinal;
  }

  public void addState(StateTypeNormal state) {
    this.state.add(state);
  }

  public StateTypeNormal getState(int index) {
    return this.state.get( index );
  }

  public StateTypeNormal removeState(int index) {
    return this.state.remove( index );
  }

  public List<StateTypeNormal> getStateList() {
    return this.state;
  }

  public void setStateList(List<StateTypeNormal> state) {
    this.state=state;
  }

  public int sizeStateList() {
    return this.state.size();
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

  public static final String STATE_INITIAL = "stateInitial";

  public static final String STATE_FINAL = "stateFinal";

  protected List<StateTypeNormal> state = new ArrayList<StateTypeNormal>();

  /**
   * @deprecated Use method getStateList
   * @return List<StateTypeNormal>
  */
  @Deprecated
  public List<StateTypeNormal> getState() {
  	return this.state;
  }

  /**
   * @deprecated Use method setStateList
   * @param state List<StateTypeNormal>
  */
  @Deprecated
  public void setState(List<StateTypeNormal> state) {
  	this.state=state;
  }

  /**
   * @deprecated Use method sizeStateList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeState() {
  	return this.state.size();
  }

  public static final String STATE = "state";

}
