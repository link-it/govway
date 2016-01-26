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

package org.openspcoop2.core.registry.driver;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Servizio;

/**
 * IDServizioFactory
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IDServizioFactory {

	
	private static IDAccordoFactory factory = null;
	private static synchronized void init(){
		if(IDServizioFactory.factory==null){
			IDServizioFactory.factory = new IDAccordoFactory();
		}
	}
	public static IDAccordoFactory getInstance(){
		if(IDServizioFactory.factory==null){
			IDServizioFactory.init();
		}
		return IDServizioFactory.factory;
	}
	
	
	public IDServizio getIDAccordoFromServizio(AccordoServizioParteSpecifica accordo) throws DriverRegistroServiziException{
		if(accordo==null){
			throw new DriverRegistroServiziException("AccordoServizioParteSpecifica non fornito");
		}
		Servizio servizio = accordo.getServizio();
		if(servizio==null){
			throw new DriverRegistroServiziException("Servizio non fornito");
		}
		IDServizio idServizio = new IDServizio(servizio.getTipoSoggettoErogatore(),servizio.getNomeSoggettoErogatore(),
				servizio.getTipo(),servizio.getNome());
		return idServizio;
	}
	
}
