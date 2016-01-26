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
package org.openspcoop2.core.config.driver;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.config.PortaApplicativa;

/**
 * PorteApplicative2Hashtable
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteApplicative2Hashtable extends XmlAdapter<PorteApplicative, Hashtable<IDSoggetto, PortaApplicativa>>
{
	@Override
	public PorteApplicative marshal(Hashtable<IDSoggetto, PortaApplicativa> hash) throws Exception {
		PorteApplicative porte = new PorteApplicative();
		Iterator<Entry<IDSoggetto,PortaApplicativa>> i = hash.entrySet().iterator();
		while(i.hasNext()){
			Entry<IDSoggetto,PortaApplicativa> e = i.next();
			porte.getEntry().add(new PorteApplicative.Entry(e.getKey(),e.getValue()));
		}
		return porte;
	}
	@Override
	public Hashtable<IDSoggetto, PortaApplicativa> unmarshal(PorteApplicative porte) throws Exception {
		Hashtable<IDSoggetto, PortaApplicativa> hash = new Hashtable<IDSoggetto, PortaApplicativa>();
		Iterator<PorteApplicative.Entry> i = porte.getEntry().iterator();
		while(i.hasNext()){
			PorteApplicative.Entry e = i.next();
			hash.put(e.getIdSoggetto(), e.getPortaApplicativa());
		}
		return hash;
	}
}

