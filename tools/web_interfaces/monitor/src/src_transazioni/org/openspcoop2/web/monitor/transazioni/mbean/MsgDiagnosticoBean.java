/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.web.monitor.transazioni.mbean;

import org.apache.commons.lang.StringEscapeUtils;
import org.openspcoop2.pdd.logger.LogLevels;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;

/**
 * MsgDiagnosticoBean
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class MsgDiagnosticoBean extends MsgDiagnostico {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String severitaString;

	public String getSeveritaAsString() {
		if (this.severitaString == null) {
			this.severitaString = LogLevels.toOpenSPCoop2(this.getSeverita());
		}

		return this.severitaString;
	}
	
	public String getIdFunzioneAsString(){
		String tmp = this.getIdFunzione();
		if(tmp!=null){
			if(tmp.equals("all")){
				return "core";
			}
		}
		return tmp;
	}
	
	public String getMessaggioAsString(){
		String tmp = this.getMessaggio();
		// devo fare l'escape html prima di convertire
		tmp = StringEscapeUtils.escapeHtml(tmp);
		if(tmp!=null){
			while(tmp.contains("\n")){
				tmp = tmp.replace("\n", "<br/>");
			}
		}
		return tmp;
	}
}
