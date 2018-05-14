package org.openspcoop2.web.monitor.transazioni.mbean;

import org.apache.commons.lang.StringEscapeUtils;
import org.openspcoop2.pdd.logger.LogLevels;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;


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
