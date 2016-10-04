/*
 * OpenSPCoop - Customizable API Gateway 
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



package org.openspcoop2.testsuite.core;

import java.util.Vector;

/**
 * Repository degli id utilizzati nelle istanze di profili non asincroni.
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Repository {
	private Vector<String> vet;
	private int index;

	public Repository(){
		this.vet=new Vector<String>();
		this.index=0;
	}

	public synchronized int add(String id){
		this.vet.add(id);
		return this.vet.indexOf(id);
	}


	public synchronized String getNext(){
		String str;
		try{
			str= this.vet.elementAt(this.index);
			this.index++;
		}
		catch(ArrayIndexOutOfBoundsException nil){
			return null;
		}
		return str;
	}
	
	public synchronized void setIndex(int i){
		this.index = i;
	}

}
