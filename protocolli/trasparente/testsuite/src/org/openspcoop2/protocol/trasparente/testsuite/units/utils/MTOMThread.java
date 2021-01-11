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



package org.openspcoop2.protocol.trasparente.testsuite.units.utils;

import javax.xml.ws.Endpoint;

import org.openspcoop2.example.server.mtom.ws.MTOMServiceExampleSOAP11Impl;
import org.openspcoop2.example.server.mtom.ws.MTOMServiceExampleSOAP12Impl;

/**
 * Thread per la gestione del Threshold
 * 
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MTOMThread extends Thread{

 
    // VARIABILE PER STOP
	private boolean stop = false;
	
	public boolean isStop() {
		return this.stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}
	
	private int port;
	private boolean soap11;
	private int timeout;
	
	private boolean started = false;
	public boolean isStarted() {
		return this.started;
	}

	private boolean finished = false;
	
	public boolean isFinished() {
		return this.finished;
	}

	/** Costruttore */
	public MTOMThread(int port,boolean soap11,int timeout) {
		this.port = port;
		this.soap11 = soap11;
		this.timeout = timeout;
	}
	
	/**
	 * Metodo che fa partire il Thread. 
	 *
	 */
	@Override
	public void run(){
		
		while(this.stop == false){
			
			String soap = "soap11";
			if(!this.soap11){
				soap = "soap12";
			}
	        String address = "http://localhost:"+this.port+"/MTOMExample/"+soap;
			try{
				Object implementor = null;
				if(this.soap11){
					implementor = new MTOMServiceExampleSOAP11Impl();
				}
				else{
					implementor = new MTOMServiceExampleSOAP12Impl();
				}
		        Endpoint.publish(address, implementor);
		        
		        System.out.println("Server ["+address+"] started ...");
		        this.started = true;
		        
				// CheckInterval
				if(this.stop==false){
					int i=0;
					while(i<this.timeout){
						try{
							Thread.sleep(1000);		
						}catch(Exception e){}
						if(this.stop){
							break; // thread terminato, non lo devo far piu' dormire
						}
						i++;
					}
				}
				
				System.out.println("Server ["+address+"] finished");
				this.finished = true;
		        
			}catch(Throwable t){
				this.finished = true;
				t.printStackTrace(System.out);
			}
	        
		} 
	}
}
