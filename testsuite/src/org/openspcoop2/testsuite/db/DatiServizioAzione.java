/*
 * OpenSPCoop - Customizable API Gateway
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.testsuite.db;

/**
 * DatiServizioAzione
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiServizioAzione extends DatiServizio {

	private String azione;
	
	public DatiServizioAzione(String tipoServizio,String nomeServizio,int versioneServizio,String azione){
		super(tipoServizio, nomeServizio, versioneServizio);
		this.azione = azione;
	}
	public DatiServizioAzione(DatiServizio datiServizio,String azione){
		super(datiServizio.getTipoServizio(), datiServizio.getNomeServizio(), datiServizio.getVersioneServizio());
		this.azione = azione;
	}
	
	public String getAzione() {
		return this.azione;
	}
	public void setAzione(String azione) {
		this.azione = azione;
	}

}
