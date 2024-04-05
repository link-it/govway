/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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



package org.openspcoop2.testsuite.core;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.Semaphore;

/**
 * Repository degli id utilizzati nelle istanze di profili non asincroni.
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Repository {
	private List<String> vet;
	private int index;
	private org.openspcoop2.utils.Semaphore semaphore = new Semaphore("Repository");

	public Repository(){
		this.vet=new ArrayList<>();
		this.index=0;
	}

	public int add(String id){
		this.semaphore.acquireThrowRuntime("add");
		try {
			this.vet.add(id);
			//System.out.println("ADD (size:"+this.vet.size()+") indexOf("+this.vet.indexOf(id)+") index("+this.index+")");
			return this.vet.indexOf(id);
		}finally {
			this.semaphore.release("add");
		}
	}


	public String getNext(){
		this.semaphore.acquireThrowRuntime("getNext");
		String str;
		try{
			//System.out.println("GET NEXT (size:"+this.vet.size()+") index("+this.index+") ...");
			str= this.vet.get(this.index);
			this.index++;
			//System.out.println("GET NEXT (size:"+this.vet.size()+") index("+this.index+")");
		}
		catch(IndexOutOfBoundsException nil){
			//System.out.println("GET NEXT (size:"+this.vet.size()+") index("+this.index+") ERROR OUT OF BOUND");
			return null;
		}
		finally {
			this.semaphore.release("add");
		}
		return str;
	}
	
	public void setIndex(int i){
		this.semaphore.acquireThrowRuntime("setIndex");
		try {
			//System.out.println("SET INDEX (size:"+this.vet.size()+") index("+i+") ...");
			this.index = i;
		}finally {
			this.semaphore.release("setIndex");
		}
	}

}
