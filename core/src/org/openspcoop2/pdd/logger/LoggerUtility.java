package org.openspcoop2.pdd.logger;

import org.slf4j.Logger;

public class LoggerUtility {

	private Logger log = null;
	private boolean debug = false;
	
	public boolean isDebug() {
		return this.debug;
	}
	public Logger getLog() {
		return this.log;
	}

	public LoggerUtility(Logger log,boolean debug){
		this.log = log;
		this.debug = debug;
	}

	public void error(String msg){
		this.log.error(msg);
	}
	public void error(String msg,Exception e){
		this.log.error(msg,e);
	}
	public void error(String msg,Throwable e){
		this.log.error(msg,e);
	}
	
	public void info(String msg){
		this.log.info(msg);
	}
	public void info(String msg,Exception e){
		this.log.info(msg,e);
	}
	public void info(String msg,Throwable e){
		this.log.info(msg,e);
	}
	
	public void debug(String msg){
		if(this.debug){
			this.log.debug(msg);
		}
	}
	public void debug(String msg,Exception e){
		if(this.debug){
			this.log.debug(msg,e);
		}
	}
	public void debug(String msg,Throwable e){
		if(this.debug){
			this.log.debug(msg,e);
		}
	}
}
