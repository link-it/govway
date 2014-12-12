/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved. 
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



package org.openspcoop2.pdd.services.connector;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.openspcoop2.pdd.logger.Dump;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.logger.Tracciamento;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.pdd.timers.TimerMonitoraggioRisorse;
import org.openspcoop2.pdd.timers.TimerThreshold;

/**
 * Servlet che serve per verificare l'installazione di OpenSPCoop.
 * Ritorna 200 in caso l'installazione sia correttamente inizializzata e tutte le risorse disponibili.
 * Ritorna 500 in caso la PdD non sia in funzione
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class CheckStatoPdD extends HttpServlet {

	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	

	@Override public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		
		Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(log==null)
			log = Logger.getLogger(CheckStatoPdD.class);
		
		if( OpenSPCoop2Startup.initialize == false){
			String msg = "Porta di dominio OpenSPCoop non inzializzata";
			log.error("[CheckStatoPdD] "+msg);
			res.setStatus(500);
			res.getOutputStream().write(msg.getBytes());	
		}
		else if( TimerMonitoraggioRisorse.risorseDisponibili == false){
			String msg = "Risorse di sistema non disponibili: "+TimerMonitoraggioRisorse.risorsaNonDisponibile.getMessage();
			log.error("[CheckStatoPdD] "+msg,TimerMonitoraggioRisorse.risorsaNonDisponibile);
			res.setStatus(500);
			res.getOutputStream().write(msg.getBytes());	
		}
		else if( TimerThreshold.freeSpace == false){
			String msg = "Non sono disponibili abbastanza risorse per la gestione della richiesta";
			log.error("[CheckStatoPdD] "+msg);
			res.setStatus(500);
			res.getOutputStream().write(msg.getBytes());	
		}
		else if( Tracciamento.tracciamentoDisponibile == false){
			String msg = "Tracciatura non disponibile: "+Tracciamento.motivoMalfunzionamentoTracciamento.getMessage();
			log.error("[CheckStatoPdD] "+msg,Tracciamento.motivoMalfunzionamentoTracciamento);
			res.setStatus(500);
			res.getOutputStream().write(msg.getBytes());	
		}
		else if( MsgDiagnostico.gestoreDiagnosticaDisponibile == false){
			String msg = "Sistema di diagnostica non disponibile: "+MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage();
			log.error("[CheckStatoPdD] "+msg,MsgDiagnostico.motivoMalfunzionamentoDiagnostici);
			res.setStatus(500);
			res.getOutputStream().write(msg.getBytes());	
		}
		else if( Dump.sistemaDumpDisponibile == false){
			String msg = "Sistema di dump dei contenuti applicativi non disponibile: "+Dump.motivoMalfunzionamentoDump.getMessage();
			log.error("[CheckStatoPdD] "+msg,Dump.motivoMalfunzionamentoDump);
			res.setStatus(500);
			res.getOutputStream().write(msg.getBytes());	
		}
		return;

	}
	@Override public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		doGet(req,res);
	}

}
