/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.ScopeTipoMatch;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for autorizzazione-scope complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="autorizzazione-scope">
 * 		&lt;sequence>
 * 			&lt;element name="scope" type="{http://www.openspcoop2.org/core/config}scope" minOccurs="1" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/>
 * 		&lt;attribute name="match" type="{http://www.openspcoop2.org/core/config}ScopeTipoMatch" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "autorizzazione-scope", 
  propOrder = {
  	"scope"
  }
)

@XmlRootElement(name = "autorizzazione-scope")

public class AutorizzazioneScope extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public AutorizzazioneScope() {
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

  public void addScope(Scope scope) {
    this.scope.add(scope);
  }

  public Scope getScope(int index) {
    return this.scope.get( index );
  }

  public Scope removeScope(int index) {
    return this.scope.remove( index );
  }

  public List<Scope> getScopeList() {
    return this.scope;
  }

  public void setScopeList(List<Scope> scope) {
    this.scope=scope;
  }

  public int sizeScopeList() {
    return this.scope.size();
  }

  public void set_value_stato(String value) {
    this.stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_stato() {
    if(this.stato == null){
    	return null;
    }else{
    	return this.stato.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getStato() {
    return this.stato;
  }

  public void setStato(org.openspcoop2.core.config.constants.StatoFunzionalita stato) {
    this.stato = stato;
  }

  public void set_value_match(String value) {
    this.match = (ScopeTipoMatch) ScopeTipoMatch.toEnumConstantFromString(value);
  }

  public String get_value_match() {
    if(this.match == null){
    	return null;
    }else{
    	return this.match.toString();
    }
  }

  public org.openspcoop2.core.config.constants.ScopeTipoMatch getMatch() {
    return this.match;
  }

  public void setMatch(org.openspcoop2.core.config.constants.ScopeTipoMatch match) {
    this.match = match;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="scope",required=true,nillable=false)
  protected List<Scope> scope = new ArrayList<Scope>();

  /**
   * @deprecated Use method getScopeList
   * @return List<Scope>
  */
  @Deprecated
  public List<Scope> getScope() {
  	return this.scope;
  }

  /**
   * @deprecated Use method setScopeList
   * @param scope List<Scope>
  */
  @Deprecated
  public void setScope(List<Scope> scope) {
  	this.scope=scope;
  }

  /**
   * @deprecated Use method sizeScopeList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeScope() {
  	return this.scope.size();
  }

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_stato;

  @XmlAttribute(name="stato",required=false)
  protected StatoFunzionalita stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_match;

  @XmlAttribute(name="match",required=false)
  protected ScopeTipoMatch match;

}
