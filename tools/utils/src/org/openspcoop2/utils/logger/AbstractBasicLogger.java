/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.utils.logger;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.id.UUIDUtilsGenerator;
import org.openspcoop2.utils.logger.beans.Diagnostic;
import org.openspcoop2.utils.logger.config.DiagnosticConfig;
import org.openspcoop2.utils.logger.constants.LowSeverity;
import org.openspcoop2.utils.logger.constants.Severity;

/**
 * AbstractBasicLogger
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractBasicLogger extends AbstractBaseDiagnosticManagerCore implements ILogger {

	protected String idTransaction;
		
	public AbstractBasicLogger(DiagnosticConfig diagnosticConfig) throws UtilsException{
		super(diagnosticConfig);
	}

	@Override
	public void initLogger() throws UtilsException{
		
		try{
			
			this.idTransaction = UUIDUtilsGenerator.newUUID();
			
			super.init(this.getContext(), this);
			
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	@Override
	public void initLogger(String idTransaction) throws UtilsException{
		try{
			
			if(idTransaction!=null && !"".equals(idTransaction)){
				this.idTransaction = idTransaction;
			}
			else{
				this.idTransaction = UUIDUtilsGenerator.newUUID();
			}
			
			super.init(this.getContext(), this);
			
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	@Override
	public void log(String message, LowSeverity severity) throws UtilsException{
		String functionDefault = this.diagnosticManager.getDefaultFunction();
		this.log(functionDefault, message, severity.toSeverity(), this.diagnosticManager.getDefaultCode(functionDefault, severity));
	}
	
	@Override
	public void log(String message, LowSeverity severity, String function) throws UtilsException{
		this.log(function, message, severity.toSeverity(), this.diagnosticManager.getDefaultCode(function, severity));
	}

	@Override
	public void log(String code) throws UtilsException{
		this.log(this.diagnosticManager.getFunction(code),
				this.diagnosticManager.getMessage(code),
				this.diagnosticManager.getSeverity(code),
				this.diagnosticManager.getCode(code));
	}

	@Override
	public void log(String code, String... params) throws UtilsException{
		this.log(this.diagnosticManager.getFunction(code),
				this.diagnosticManager.getMessage(code, params),
				this.diagnosticManager.getSeverity(code),
				this.diagnosticManager.getCode(code));
	}
	
	// serve per evitare che la chiamata con string ricata erroneamente nella firma Object invece che nella firma String ... params
	@Override
	public void log(String code, String param) throws UtilsException{
		this.log(code,new String [] {param});
	} 
	
	@Override
	public void log(String code, Object o) throws UtilsException{
		this.log(this.diagnosticManager.getFunction(code),
				this.diagnosticManager.getMessage(code,o),
				this.diagnosticManager.getSeverity(code),
				this.diagnosticManager.getCode(code));
	}
	
	private void log(String function,String message, Severity severity, String code) throws UtilsException{
		
		Diagnostic diagnostic = new Diagnostic();
		diagnostic.setDate(DateManager.getDate());
		diagnostic.setCode(code);
		diagnostic.setFunction(function);
		diagnostic.setIdTransaction(this.idTransaction);
		diagnostic.setMessage(message);
		diagnostic.setSeverity(severity);
		this.log(diagnostic, this.getContext());
		
	}
	
	protected abstract void log(Diagnostic diagnostic,IContext context) throws UtilsException;
	
	
	
}
