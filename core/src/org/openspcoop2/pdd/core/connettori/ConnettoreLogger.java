package org.openspcoop2.pdd.core.connettori;

import org.apache.log4j.Logger;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;

public class ConnettoreLogger {

	private boolean debug;
	private Logger loggerConnettore;
	private Logger loggerCore;
	private String idMessaggio;
	private PdDContext pddContext;
	private String idTransazione;
	
	public ConnettoreLogger(boolean debug,String idMessaggio,PdDContext pddContext){
		this.debug = debug;
		this.idMessaggio = idMessaggio;
		this.pddContext = pddContext;
		
		Object oIdTransazione = this.pddContext.getObject(Costanti.CLUSTER_ID);
		if(oIdTransazione!=null && (oIdTransazione instanceof String)){
			this.idTransazione = (String) oIdTransazione;
		}
		
		this.loggerConnettore = OpenSPCoop2Logger.getLoggerOpenSPCoopConnettori();
		
		this.loggerCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
	}
	
	public Logger getLogger(){
		if(this.debug){
			return this.loggerConnettore;
		}
		else{
			return this.loggerCore;
		}
	}
	
	private String buildMsg(String msg){
		StringBuffer bf = new StringBuffer();
		if(this.idTransazione!=null){
			bf.append("id:").append(this.idTransazione);
		}
		if(this.idMessaggio!=null){
			if(bf.length()>0){
				bf.append(" ");
			}
			bf.append("(id-busta:").append(this.idMessaggio);
		}
		if(bf.length()>0){
			return "<"+bf.toString()+"> "+msg;
		}
		else{
			return  msg;
		}
	}
	
	public void error(String msg){
		this.loggerCore.error(this.buildMsg(msg));
		if(this.debug){
			this.loggerConnettore.error(this.buildMsg(msg));
		}
	}
	
	public void error(String msg, Throwable t){
		this.loggerCore.error(this.buildMsg(msg),t);
		if(this.debug){
			this.loggerConnettore.error(this.buildMsg(msg),t);
		}
	}
	
	public void warn(String msg){
		this.loggerCore.warn(this.buildMsg(msg));
		if(this.debug){
			this.loggerConnettore.warn(this.buildMsg(msg));
		}
	}

	public void info(String msg, boolean logInCore){
		if(logInCore){
			this.loggerCore.info(this.buildMsg(msg));
		}
		if(this.debug){
			this.loggerConnettore.info(this.buildMsg(msg));
		}
	}
	
	public void debug(String msg){
		if(this.debug){
			this.loggerConnettore.debug(this.buildMsg(msg));
		}
	}

	
	
}
