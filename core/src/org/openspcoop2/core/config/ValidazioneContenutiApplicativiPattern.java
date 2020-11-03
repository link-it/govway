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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for validazione-contenuti-applicativi-pattern complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="validazione-contenuti-applicativi-pattern"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="pattern" type="{http://www.openspcoop2.org/core/config}validazione-contenuti-applicativi-pattern-regola" minOccurs="1" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="and" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="true"/&gt;
 * 		&lt;attribute name="not" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "validazione-contenuti-applicativi-pattern", 
  propOrder = {
  	"pattern"
  }
)

@XmlRootElement(name = "validazione-contenuti-applicativi-pattern")

public class ValidazioneContenutiApplicativiPattern extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ValidazioneContenutiApplicativiPattern() {
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

  public void addPattern(ValidazioneContenutiApplicativiPatternRegola pattern) {
    this.pattern.add(pattern);
  }

  public ValidazioneContenutiApplicativiPatternRegola getPattern(int index) {
    return this.pattern.get( index );
  }

  public ValidazioneContenutiApplicativiPatternRegola removePattern(int index) {
    return this.pattern.remove( index );
  }

  public List<ValidazioneContenutiApplicativiPatternRegola> getPatternList() {
    return this.pattern;
  }

  public void setPatternList(List<ValidazioneContenutiApplicativiPatternRegola> pattern) {
    this.pattern=pattern;
  }

  public int sizePatternList() {
    return this.pattern.size();
  }

  public boolean isAnd() {
    return this.and;
  }

  public boolean getAnd() {
    return this.and;
  }

  public void setAnd(boolean and) {
    this.and = and;
  }

  public boolean isNot() {
    return this.not;
  }

  public boolean getNot() {
    return this.not;
  }

  public void setNot(boolean not) {
    this.not = not;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="pattern",required=true,nillable=false)
  protected List<ValidazioneContenutiApplicativiPatternRegola> pattern = new ArrayList<ValidazioneContenutiApplicativiPatternRegola>();

  /**
   * @deprecated Use method getPatternList
   * @return List&lt;ValidazioneContenutiApplicativiPatternRegola&gt;
  */
  @Deprecated
  public List<ValidazioneContenutiApplicativiPatternRegola> getPattern() {
  	return this.pattern;
  }

  /**
   * @deprecated Use method setPatternList
   * @param pattern List&lt;ValidazioneContenutiApplicativiPatternRegola&gt;
  */
  @Deprecated
  public void setPattern(List<ValidazioneContenutiApplicativiPatternRegola> pattern) {
  	this.pattern=pattern;
  }

  /**
   * @deprecated Use method sizePatternList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizePattern() {
  	return this.pattern.size();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="and",required=false)
  protected boolean and = true;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="not",required=false)
  protected boolean not = false;

}
