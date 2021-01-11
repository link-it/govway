/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.core.registry.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;


/** 
 * AccordoServizioParteComuneServizioCompostoSintetico
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */
public class AccordoServizioParteComuneServizioCompostoSintetico extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AccordoServizioParteComuneServizioCompostoSintetico() {
	}
	public AccordoServizioParteComuneServizioCompostoSintetico(AccordoServizioParteComuneServizioComposto param) {
		this.id = param.getId();
		if(param.sizeServizioComponenteList()>0) {
			for (AccordoServizioParteComuneServizioCompostoServizioComponente componente : param.getServizioComponenteList()) {
				this.servizioComponente.add(new AccordoServizioParteComuneServizioCompostoServizioComponenteSintetico(componente));
			}
		}
		this.idAccordoCooperazione = param.getIdAccordoCooperazione();
		this.accordoCooperazione = param.getAccordoCooperazione();
	}

	private Long id;

	private List<AccordoServizioParteComuneServizioCompostoServizioComponenteSintetico> servizioComponente = new ArrayList<AccordoServizioParteComuneServizioCompostoServizioComponenteSintetico>();

	private java.lang.Long idAccordoCooperazione;

	private java.lang.String accordoCooperazione;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<AccordoServizioParteComuneServizioCompostoServizioComponenteSintetico> getServizioComponente() {
		return this.servizioComponente;
	}

	public void setServizioComponente(
			List<AccordoServizioParteComuneServizioCompostoServizioComponenteSintetico> servizioComponente) {
		this.servizioComponente = servizioComponente;
	}

	public java.lang.Long getIdAccordoCooperazione() {
		return this.idAccordoCooperazione;
	}

	public void setIdAccordoCooperazione(java.lang.Long idAccordoCooperazione) {
		this.idAccordoCooperazione = idAccordoCooperazione;
	}

	public java.lang.String getAccordoCooperazione() {
		return this.accordoCooperazione;
	}

	public void setAccordoCooperazione(java.lang.String accordoCooperazione) {
		this.accordoCooperazione = accordoCooperazione;
	}

}
