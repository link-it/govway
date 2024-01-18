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



package org.openspcoop2.testsuite.core.asincrono;



import java.util.HashMap;


import org.apache.axis.Message;
import org.openspcoop2.testsuite.core.TestSuiteException;

/**
 * Repository degli id utilizzati nelle istanze di richiesta di un profilo asincrono simmetrico.
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class RepositoryConsegnaRisposteAsincroneSimmetriche {

	private HashMap<String, Message> hash;
	
	/** Tempo di attesa nel repository asincrono */
	private long tempoAttesaRepository;
	
	public RepositoryConsegnaRisposteAsincroneSimmetriche(long tempoAttesaRepository){
		this.hash=new HashMap<String, Message>();
		this.tempoAttesaRepository = tempoAttesaRepository;
	}


	public synchronized void  put(String id,Message msg){
		this.hash.put(id, msg);
	}

	public Message get(String id) throws InterruptedException{
		long time=System.currentTimeMillis();
		while(true){
			synchronized(this.hash){
				if(this.hash.containsKey(id))
					break;
			}
			try{
				Thread.sleep(300);
			}catch(Exception e){}
			long time2=System.currentTimeMillis();
			if(time2-time>this.tempoAttesaRepository)throw new TestSuiteException("Attesa nel repository asincrono, ha superato i "+this.tempoAttesaRepository+" milliSecondi senza trovare la risposta");
		}
		return this.hash.get(id);
	}
}
