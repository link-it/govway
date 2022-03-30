/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.engine.alarm;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.openspcoop2.monitor.sdk.alarm.IAlarmLogger;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.slf4j.Logger;

/**     
 * AlarmLogger
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AlarmLogger implements IAlarmLogger {

	private CircularFifoQueue<String> logHistory; 
	private final static int NUMERO_EVENTI = 20;
	
	private String threadName;
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	private String nome;
	private String id;
	private Logger _log;
	private Logger _logSql;
	
	
	public AlarmLogger(String nome, String id, String threadName, Logger log, Logger logSql) {
		this.logHistory = new CircularFifoQueue<String>(NUMERO_EVENTI); // mantiene gli ultimi x log
		this.nome = nome;
		this.id = id;
		this.threadName = threadName;
		this._log = log;
		this._logSql = logSql;
	}
	

	@Override
	public Logger getInternalLogger() {
		return this._log;
	}
	@Override
	public Logger getInternalSqlLogger() {
		return this._logSql;
	}
	
	@Override
	public void debug(String messaggio) {
		_log(messaggio, true, false, false, false, null);
	}
	@Override
	public void debug(String messaggio,  Throwable t) {
		_log(messaggio, true, false, false, false, t);
	}
	
	@Override
	public void info(String messaggio) {
		_log(messaggio, false, true, false, false, null);
	}
	@Override
	public void info(String messaggio,  Throwable t) {
		_log(messaggio, false, true, false, false, t);
	}
	
	@Override
	public void warn(String messaggio) {
		_log(messaggio, false, false, true, false, null);
	}
	@Override
	public void warn(String messaggio,  Throwable t) {
		_log(messaggio, false, false, true, false, t);
	}
	
	@Override
	public void error(String messaggio) {
		_log(messaggio, false, false, false, true, null);
	}
	@Override
	public void error(String messaggio,  Throwable t) {
		_log(messaggio, false, false, false, true, t);
	}
	
	private void _log(String messaggio, boolean debug, boolean info, boolean warning, boolean error, Throwable e) {
		String prefix = buildPrefix(this.threadName,this.nome,this.id);
		String logMsg = prefix+messaggio;
		if(debug) {
			if(e!=null) {
				this._log.debug(logMsg,e);
			}
			else {
				this._log.debug(logMsg);
			}
		}
		else if(info) {
			if(e!=null) {
				this._log.info(logMsg,e);
			}
			else {
				this._log.info(logMsg);
			}
		}
		else if(warning) {
			if(e!=null) {
				this._log.warn(logMsg,e);
			}
			else {
				this._log.warn(logMsg);
			}
		}
		else if(error) {
			if(e!=null) {
				this._log.error(logMsg,e);
			}
			else {
				this._log.error(logMsg);
			}
		}
		String data = " <"+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+"> ";
		this.logHistory.add("\n"+data+ messaggio);
	}

	public static String buildPrefix(String threadName, String aliasAllarme, String idAllarme) {
		String prefix = "";
		if(threadName!=null) {
			prefix = "["+threadName+"] ";
		}
		prefix = prefix+"Allarme '"+aliasAllarme+"' (" + idAllarme+ ") ";
		return prefix;
	}

	public String getStatoAllarme() {
		StringBuilder sb = new StringBuilder();
		sb.append("================================================================\n");
		sb.append("Dati Identificativi\n");
		sb.append("- Nome: ").append(this.nome).append("\n");
		sb.append("- IdAllarme: ").append(this.id).append("\n");
		sb.append("- IdThread: ").append(this.threadName).append("\n");
		sb.append("\nUltimi "+NUMERO_EVENTI+" eventi:");
		if(this.logHistory.isFull()) {
			sb.append("\n...");
		}
		String s = this.logHistory.toString();
		if(s.startsWith("[") && s.length()>1) {
			s = s.substring(1);
		}
		if(s.endsWith("]") && s.length()>1) {
			s = s.substring(0, s.length()-1);
		}
		sb.append(s);
		sb.append("\n");
		sb.append("================================================================\n");
		return sb.toString();
	}
}
