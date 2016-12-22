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




package org.openspcoop2.pdd.timers;

import javax.rmi.PortableRemoteObject;

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.utils.resources.GestoreJNDI;

/**
 * Libreria contenente metodi utili per lo smistamento delle buste 
 * all'interno dei moduli di openspcoop realizzati tramite servizi.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class TimerUtils {
    
  
   

    /**
     * Metodo che si occupa di ottenere il EJB utile alla gestione dei Riscontri Scaduti
     *
     * @return un EJB utile alla gestione dei riscontri scaduti {@link org.openspcoop2.pdd.timers.TimerGestoreBusteNonRiscontrate}.
     * 
     */
    public static TimerGestoreBusteNonRiscontrate createTimerGestoreBusteNonRiscontrate() throws Exception {
        
    	OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
        	GestoreJNDI jndi = null;
        	if(properties.getJNDIContext_TimerEJB()==null)
        		jndi = new GestoreJNDI();
        	else
        		jndi = new GestoreJNDI(properties.getJNDIContext_TimerEJB());
	    
        	String nomeJNDI = properties.getJNDITimerEJBName().get(TimerGestoreBusteNonRiscontrate.ID_MODULO);
        	Object objref = jndi.lookup(nomeJNDI);
        	TimerGestoreBusteNonRiscontrateHome timerHome = 
        		(TimerGestoreBusteNonRiscontrateHome) PortableRemoteObject.narrow(objref,TimerGestoreBusteNonRiscontrateHome.class);
        	TimerGestoreBusteNonRiscontrate timerDiServizio = timerHome.create();	
        
            return timerDiServizio;
	    
        
    }




    /**
     * Metodo che si occupa di ottenere il EJB utile alla gestione dei Messaggi
     *
     * @return un EJB utile alla gestione dei messaggi {@link org.openspcoop2.pdd.timers.TimerGestoreMessaggi}.
     * 
     */
    public static TimerGestoreMessaggi createTimerGestoreMessaggi() throws Exception {
       
    	OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
        	GestoreJNDI jndi = null;
        	if(properties.getJNDIContext_TimerEJB()==null)
        		jndi = new GestoreJNDI();
        	else
        		jndi = new GestoreJNDI(properties.getJNDIContext_TimerEJB());
        	
	    String nomeJNDI = properties.getJNDITimerEJBName().get(TimerGestoreMessaggi.ID_MODULO);
	    Object objref = jndi.lookup(nomeJNDI);
	    TimerGestoreMessaggiHome timerHome = 
		(TimerGestoreMessaggiHome) PortableRemoteObject.narrow(objref,TimerGestoreMessaggiHome.class);
	    TimerGestoreMessaggi timerDiServizio = timerHome.create();	
 
            return timerDiServizio;
	    
       
    }
    
    
    /**
     * Metodo che si occupa di ottenere il EJB utile alla gestione dei Messaggi anomali
     *
     * @return un EJB utile alla gestione dei messaggi {@link org.openspcoop2.pdd.timers.TimerGestorePuliziaMessaggiAnomali}.
     * 
     */
    public static TimerGestorePuliziaMessaggiAnomali createTimerGestorePuliziaMessaggiAnomali() throws Exception {
       
    	OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
    	GestoreJNDI jndi = null;
    	if(properties.getJNDIContext_TimerEJB()==null)
    		jndi = new GestoreJNDI();
    	else
    		jndi = new GestoreJNDI(properties.getJNDIContext_TimerEJB());
        	
	    String nomeJNDI = properties.getJNDITimerEJBName().get(TimerGestorePuliziaMessaggiAnomali.ID_MODULO);
	    Object objref = jndi.lookup(nomeJNDI);
	    TimerGestorePuliziaMessaggiAnomaliHome timerHome = 
		(TimerGestorePuliziaMessaggiAnomaliHome) PortableRemoteObject.narrow(objref,TimerGestorePuliziaMessaggiAnomaliHome.class);
	    TimerGestorePuliziaMessaggiAnomali timerDiServizio = timerHome.create();	
 
	    return timerDiServizio;
	    
       
    }

    
    /**
     * Metodo che si occupa di ottenere il EJB utile alla gestione delle buste
     *
     * @return un EJB utile alla gestione delle buste {@link org.openspcoop2.pdd.timers.TimerGestoreRepositoryBuste}.
     * 
     */
    public static TimerGestoreRepositoryBuste createTimerGestoreRepositoryBuste() throws Exception {
       
        	OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
        	GestoreJNDI jndi = null;
        	if(properties.getJNDIContext_TimerEJB()==null)
        		jndi = new GestoreJNDI();
        	else
        		jndi = new GestoreJNDI(properties.getJNDIContext_TimerEJB());
        	String nomeJNDI = properties.getJNDITimerEJBName().get(TimerGestoreRepositoryBuste.ID_MODULO);
        	Object objref = jndi.lookup(nomeJNDI);
        	TimerGestoreRepositoryBusteHome timerHome = 
        		(TimerGestoreRepositoryBusteHome) PortableRemoteObject.narrow(objref,TimerGestoreRepositoryBusteHome.class);
        	TimerGestoreRepositoryBuste timerDiServizio = timerHome.create();	
 
        	return timerDiServizio;
	    
    }
    

}
