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
package org.openspcoop2.generic_project.expression.impl.test.beans;

import java.io.Serializable;

import org.openspcoop2.generic_project.expression.impl.test.model.FruitoreModel;
import org.openspcoop2.utils.beans.BaseBean;


/**
 * Fruitore
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Fruitore extends BaseBean implements Serializable , Cloneable {
  private Long id;


  public Fruitore() {
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

  public IdSoggetto getIdFruitore() {
    return this.idFruitore;
  }

  public void setIdFruitore(IdSoggetto idFruitore) {
    this.idFruitore = idFruitore;
  }

  public IdAccordoServizioParteSpecifica getIdAccordoServizioParteSpecifica() {
    return this.idAccordoServizioParteSpecifica;
  }

  public void setIdAccordoServizioParteSpecifica(IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica) {
    this.idAccordoServizioParteSpecifica = idAccordoServizioParteSpecifica;
  }

  public java.util.Date getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(java.util.Date oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  private static final long serialVersionUID = 1L;

  public static FruitoreModel model(){
	  return new FruitoreModel();
  }

  protected IdSoggetto idFruitore;

  protected IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica;

  protected java.util.Date oraRegistrazione;

}
