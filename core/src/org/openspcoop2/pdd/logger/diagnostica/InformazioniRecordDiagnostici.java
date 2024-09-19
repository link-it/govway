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

package org.openspcoop2.pdd.logger.diagnostica;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.pdd.logger.record.AbstractDatoRicostruzione;
import org.openspcoop2.pdd.logger.record.CharDatoRicostruzione;
import org.openspcoop2.pdd.logger.record.StringDatoRicostruzione;
import org.openspcoop2.pdd.logger.record.TimestampDatoRicostruzione;

/**     
 * InformazioniRecordDiagnostici
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InformazioniRecordDiagnostici {

	protected boolean presenti = false;
	
	protected boolean ricostruibili = false;
	
	protected List<AbstractDatoRicostruzione<?>> dati = new ArrayList<>();

	protected List<InfoDiagnostico> diagnostici = new ArrayList<>();
		
	protected String motivoRicostruzioneNonFattibile = null;
	
	
	// Gli Ext servono per generare diagnostici dinamici che magari presentano template uguali ma che devono essere risolti con valori diversi
	
	protected List<InfoDiagnostico> diagnosticiExt = new ArrayList<>();
	
	protected List<DynamicExtendedInfoDiagnostico> datiExt = new ArrayList<>();
	
	protected String rawDBValue = null;
	
	public String getRawDBValue() {
		return this.rawDBValue;
	}

	public void setRawDBValue(String rawDBValue) {
		this.rawDBValue = rawDBValue;
	}
	

	public String getMotivoRicostruzioneNonFattibile() {
		return this.motivoRicostruzioneNonFattibile;
	}
	
	public boolean isRicostruibili() {
		return this.ricostruibili;
	}
	
	public boolean isPresenti() {
		return this.presenti;
	}

	public int sizeMetaDati(){
		return this.dati.size();
	}
	
	public AbstractDatoRicostruzione<?> getDato(MappingRicostruzioneDiagnostici mapping){
		if(mapping.getValue()==null) {
			return null;
		}
		int index = mapping.getValue();
		if(index < this.dati.size()) {
			return this.dati.get(index);
		}
		return null;
	}
	
	public List<InfoDiagnostico> getDiagnostici() {
		return this.diagnostici;
	}
	
	public void setPresenti(boolean presenti) {
		this.presenti = presenti;
	}
	
	public void setRicostruibili(boolean ricostruibili) {
		this.ricostruibili = ricostruibili;
	}

	public void setMotivoRicostruzioneNonFattibile(
			String motivoRicostruzioneNonFattibile) {
		this.motivoRicostruzioneNonFattibile = motivoRicostruzioneNonFattibile;
	}

	public void setDati(List<AbstractDatoRicostruzione<?>> dati) {
		this.dati = dati;
	}
	
	public void setDiagnostici(List<InfoDiagnostico> diagnostici) {
		this.diagnostici = diagnostici;
	}
	
	public List<InfoDiagnostico> getDiagnosticiExt() {
		return this.diagnosticiExt;
	}

	public void setDiagnosticiExt(List<InfoDiagnostico> diagnosticiExt) {
		this.diagnosticiExt = diagnosticiExt;
	}

	public void setDatiExt(List<DynamicExtendedInfoDiagnostico> datiExt) {
		this.datiExt = datiExt;
	}
	
	public List<DynamicExtendedInfoDiagnostico> getDatiExt() {
		return this.datiExt;
	}
	
	
	
	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();
		bf.append("---- Diagnostici -----\n");
		bf.append("\tpresenti("+this.presenti+")\n");
		bf.append("\tricostruibili("+this.ricostruibili+")\n");
		bf.append("\tdati size:"+this.dati.size()).append("\n");
		for (int i = 0; i < this.dati.size(); i++) {
			bf.append("\t\tdato["+MappingRicostruzioneDiagnostici.toEnumConstant(i).name()+"("+i+")]").append("\n");
			bf.append("\t\t\tdescrizione:"+this.dati.get(i).getInfo().getDescription()).append("\n");
			try{
				bf.append("\t\t\tvalore:"+this.dati.get(i).convertToString()).append("\n");
			}catch(Exception e){
				bf.append("\t\t\tvalore: ERRORE NEL CALCOLO: "+e.getMessage()).append("\n");
			}
		}
		if(!this.ricostruibili){
			bf.append(" motivoRicostruzioneNonFattibile("+this.motivoRicostruzioneNonFattibile+")\n");	
		}
		return bf.toString();
	}
		
	public static InformazioniRecordDiagnostici convertoFromDBColumnValue(String columnMetaInf, String columnList1, String columnList2,
			String columnListExt, String datiExt) throws CoreException{
		
		InformazioniRecordDiagnostici info = new InformazioniRecordDiagnostici();
		
		if(columnMetaInf == null || "".equals(columnMetaInf.trim())){
			throw new CoreException("Valore non fornito");
		}
		info.setRawDBValue(columnMetaInf);
		
		if(columnMetaInf.length()==1 && (CostantiMappingDiagnostici.NON_PRESENTE == columnMetaInf.charAt(0)) ){
			info.setPresenti(false);
			return info;
		}
		
		info.setPresenti(true);
		
		
		// meta-inf
		
		String dbValue = columnMetaInf.trim();
		
		if(!dbValue.contains(CostantiMappingDiagnostici.SEPARATOR)){
			info.setRicostruibili(false);
			info.setMotivoRicostruzioneNonFattibile("Non sono presenti caratteri separatori ["+CostantiMappingDiagnostici.SEPARATOR+"] nel valore");
			return info;
		}
		
		String [] split = dbValue.split(CostantiMappingDiagnostici.SEPARATOR);
		Date gdoFirstDiagnostic = null;
		if( CostantiMappingDiagnostici.DIAGNOSTICI_EMESSI_RICOSTRUIBILI != split[0].charAt(0) ){
			info.setRicostruibili(false);
			info.setMotivoRicostruzioneNonFattibile(dbValue);
		}
		else{
			info.setRicostruibili(true);
			List<AbstractDatoRicostruzione<?>> listaDati = new ArrayList<>();
			info.setDati(listaDati);
			
			listaDati.add(new CharDatoRicostruzione(split[MappingRicostruzioneDiagnostici.DIAGNOSTICI_EMESSI.getPosition()],
					CostantiMappingDiagnostici.DIAGNOSTICI_EMESSI));
			
			listaDati.add(new TimestampDatoRicostruzione(split[MappingRicostruzioneDiagnostici.DIAGNOSTICI_EMISSIONE_FIRST_DATE.getPosition()],
					CostantiMappingDiagnostici.DIAGNOSTICI_EMISSIONE_FIRST_DATE));
			Object oGdoFist = listaDati.get(MappingRicostruzioneDiagnostici.DIAGNOSTICI_EMISSIONE_FIRST_DATE.getPosition()).getDato();
			gdoFirstDiagnostic = (oGdoFist!=null ? (Date)oGdoFist : null);
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.TIPO_AUTORIZZAZIONE.getPosition()],
					CostantiMappingDiagnostici.TIPO_AUTORIZZAZIONE));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.CODICE_TRASPORTO_RICHIESTA.getPosition()],
					CostantiMappingDiagnostici.CODICE_TRASPORTO_RICHIESTA));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.CODICE_TRASPORTO_RISPOSTA.getPosition()],
					CostantiMappingDiagnostici.CODICE_TRASPORTO_RISPOSTA));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.TIPO_CONNETTORE.getPosition()],
					CostantiMappingDiagnostici.TIPO_CONNETTORE));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.MAX_THREADS_THRESHOLD.getPosition()],
					CostantiMappingDiagnostici.MAX_THREADS_THRESHOLD));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.CONTROLLO_TRAFFICO_THRESHOLD.getPosition()],
					CostantiMappingDiagnostici.CONTROLLO_TRAFFICO_THRESHOLD));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.ACTIVE_THREADS.getPosition()],
					CostantiMappingDiagnostici.ACTIVE_THREADS));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.NUMERO_POLICY_CONFIGURATE.getPosition()],
					CostantiMappingDiagnostici.NUMERO_POLICY_CONFIGURATE));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.NUMERO_POLICY_DISABILITATE.getPosition()],
					CostantiMappingDiagnostici.NUMERO_POLICY_DISABILITATE));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.NUMERO_POLICY_FILTRATE.getPosition()],
					CostantiMappingDiagnostici.NUMERO_POLICY_FILTRATE));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.NUMERO_POLICY_NON_APPLICATE.getPosition()],
					CostantiMappingDiagnostici.NUMERO_POLICY_NON_APPLICATE));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.NUMERO_POLICY_RISPETTATE.getPosition()],
					CostantiMappingDiagnostici.NUMERO_POLICY_RISPETTATE));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.NUMERO_POLICY_VIOLATE.getPosition()],
					CostantiMappingDiagnostici.NUMERO_POLICY_VIOLATE));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.NUMERO_POLICY_VIOLATE_WARNING_ONLY.getPosition()],
					CostantiMappingDiagnostici.NUMERO_POLICY_VIOLATE_WARNING_ONLY));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.NUMERO_POLICY_IN_ERRORE.getPosition()],
					CostantiMappingDiagnostici.NUMERO_POLICY_IN_ERRORE));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.TIPO_AUTENTICAZIONE.getPosition()],
					CostantiMappingDiagnostici.TIPO_AUTENTICAZIONE));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.TIPO_AUTORIZZAZIONE_CONTENUTI.getPosition()],
					CostantiMappingDiagnostici.TIPO_AUTORIZZAZIONE_CONTENUTI));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.TIPO_VALIDAZIONE_CONTENUTI.getPosition()],
					CostantiMappingDiagnostici.TIPO_VALIDAZIONE_CONTENUTI));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.TIPO_PROCESSAMENTO_MTOM_RICHIESTA.getPosition()],
					CostantiMappingDiagnostici.TIPO_PROCESSAMENTO_MTOM_RICHIESTA));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.TIPO_PROCESSAMENTO_MTOM_RISPOSTA.getPosition()],
					CostantiMappingDiagnostici.TIPO_PROCESSAMENTO_MTOM_RISPOSTA));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.TIPO_PROCESSAMENTO_MESSAGE_SECURITY_RICHIESTA.getPosition()],
					CostantiMappingDiagnostici.TIPO_PROCESSAMENTO_MESSAGE_SECURITY_RICHIESTA));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.TIPO_PROCESSAMENTO_MESSAGE_SECURITY_RISPOSTA.getPosition()],
					CostantiMappingDiagnostici.TIPO_PROCESSAMENTO_MESSAGE_SECURITY_RISPOSTA));
				
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.AUTENTICAZIONE_IN_CACHE.getPosition()],
					CostantiMappingDiagnostici.AUTENTICAZIONE_IN_CACHE));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.AUTORIZZAZIONE_IN_CACHE.getPosition()],
					CostantiMappingDiagnostici.AUTORIZZAZIONE_IN_CACHE));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.AUTORIZZAZIONE_CONTENUTI_IN_CACHE.getPosition()],
					CostantiMappingDiagnostici.AUTORIZZAZIONE_CONTENUTI_IN_CACHE));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.TOKEN_POLICY.getPosition()],
					CostantiMappingDiagnostici.TOKEN_POLICY));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.TOKEN_POLICY_ACTIONS.getPosition()],
					CostantiMappingDiagnostici.TOKEN_POLICY_ACTIONS));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.TOKEN_POLICY_AUTENTCAZIONE.getPosition()],
					CostantiMappingDiagnostici.TOKEN_POLICY_AUTENTCAZIONE));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.RESPONSE_FROM_CACHE.getPosition()],
					CostantiMappingDiagnostici.RESPONSE_FROM_CACHE));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.TIPO_TRASFORMAZIONE_RICHIESTA.getPosition()],
					CostantiMappingDiagnostici.TIPO_TRASFORMAZIONE_RICHIESTA));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.TIPO_TRASFORMAZIONE_RISPOSTA.getPosition()],
					CostantiMappingDiagnostici.TIPO_TRASFORMAZIONE_RISPOSTA));
			
			if(split.length>(CostantiMappingDiagnostici.LENGHT_DATI_SIMULATI_VERSIONE_ATTUALE_PRECEDENTE_INTRODOTTO_33_34)){
			
				listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.AUTENTICAZIONE_TOKEN_IN_CACHE.getPosition()],
						CostantiMappingDiagnostici.AUTENTICAZIONE_TOKEN_IN_CACHE));
				
				listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.AUTENTICAZIONE_FALLITA_MOTIVAZIONE.getPosition()],
						CostantiMappingDiagnostici.AUTENTICAZIONE_FALLITA_MOTIVAZIONE));
				
			}
			
			if(split.length>(CostantiMappingDiagnostici.LENGHT_DATI_SIMULATI_VERSIONE_ATTUALE_PRECEDENTE_INTRODOTTO_35_36_37)){
				
				listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.MODI_TOKEN_AUTHORIZATION_IN_CACHE.getPosition()],
						CostantiMappingDiagnostici.MODI_TOKEN_AUTHORIZATION_IN_CACHE));
				
				listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.MODI_TOKEN_INTEGRITY_IN_CACHE.getPosition()],
						CostantiMappingDiagnostici.MODI_TOKEN_INTEGRITY_IN_CACHE));
				
				listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneDiagnostici.MODI_TOKEN_AUDIT_IN_CACHE.getPosition()],
						CostantiMappingDiagnostici.MODI_TOKEN_AUDIT_IN_CACHE));
				
			}
			
			if(split.length>CostantiMappingDiagnostici.LENGHT_DATI_SIMULATI_VERSIONE_ATTUALE){
				
				// NOTA: successivi dati aggiunti
				// GESTIRE QUANDO SI INTRODUCONO NUOVE INFO
				
			}
		}
		
		
		// diagnostic
		
		List<InfoDiagnostico> listaDiagnostici = new ArrayList<>();
		info.setDiagnostici(listaDiagnostici);
		
		if( (columnList1==null || "".equals(columnList1)) 
				&&
			(info.isPresenti() && info.isRicostruibili())
			&&
				
			// Prima di sollevare l'eccezione controllo se i diagnostici simulati sono presenti solamente nella colonna column ext
			(columnListExt==null || "".equals(columnListExt) ||
					datiExt==null || "".equals(datiExt))
			){
			throw new CoreException("Valore ColumnList1 non fornito, nonostante dovrebbero essere presenti dei diagnostici (informazione meta-inf indica la presenza e indica che sono ricostruibili)");
		}
		if(columnList1!=null && !"".equals(columnList1)){
			String [] splitDiagnosticList1 = columnList1.split(CostantiMappingDiagnostici.SEPARATOR);
			if(splitDiagnosticList1.length>CostantiMappingDiagnostici.MAX_DIAGNOSTIC_LIST_ROW_1){
				throw new CoreException("Valore ColumnList1 fornito non corretto. Non possono essere definiti piu' di "+CostantiMappingDiagnostici.MAX_DIAGNOSTIC_LIST_ROW_1+" diagnostici (separati dal carattere separatore ["+CostantiMappingDiagnostici.SEPARATOR+"])");
			}
			for (int i = 0; i < splitDiagnosticList1.length; i++) {
				String diagnostic = splitDiagnosticList1[i].trim();
				listaDiagnostici.add(InfoDiagnostico.convertoFromDBColumnValue(gdoFirstDiagnostic, diagnostic));
			}
		}
		
		if(columnList2!=null && !"".equals(columnList2)){
			String [] splitDiagnosticList2 = columnList2.split(CostantiMappingDiagnostici.SEPARATOR);
			if(splitDiagnosticList2.length>CostantiMappingDiagnostici.MAX_DIAGNOSTIC_LIST_ROW_2){
				throw new CoreException("Valore ColumnList2 fornito non corretto. Non possono essere definiti piu' di "+CostantiMappingDiagnostici.MAX_DIAGNOSTIC_LIST_ROW_2+" diagnostici (separati dal carattere separatore ["+CostantiMappingDiagnostici.SEPARATOR+"])");
			}
			for (int i = 0; i < splitDiagnosticList2.length; i++) {
				String diagnostic = splitDiagnosticList2[i].trim();
				listaDiagnostici.add(InfoDiagnostico.convertoFromDBColumnValue(gdoFirstDiagnostic, diagnostic));
			}
		}
		
		if(columnListExt!=null && !"".equals(columnListExt)){
			
			if(datiExt==null || "".equals(datiExt)){
				throw new CoreException("Trovato valore ColumnListExt ma non esiste un analogo valore per i DatiExt");
			}
						
			String [] splitDiagnosticListExt = columnListExt.split(CostantiMappingDiagnostici.SEPARATOR);
			String [] splitDatiExt = datiExt.split(CostantiMappingDiagnostici.DIAGNOSTIC_WITH_DYNAMIC_INFO_DIAG_SEPARATOR);
			
			if(splitDiagnosticListExt==null || splitDiagnosticListExt.length<=0){
				throw new CoreException("Trovato valore ColumnListExt corrotto");
			}
			if(splitDatiExt==null || splitDatiExt.length<=0){
				throw new CoreException("Trovato valore DatiExt corrotto");
			}
			if(splitDiagnosticListExt.length!=splitDatiExt.length){
				throw new CoreException("Assocazione tra ColumnListExt("+splitDiagnosticListExt.length+") e DatiExt("+splitDatiExt.length+") non corretta");
			}
			
			List<InfoDiagnostico> listaDiagnosticiExt = new ArrayList<>();
			info.setDiagnosticiExt(listaDiagnosticiExt);
			
			List<DynamicExtendedInfoDiagnostico> listaDatiExt = new ArrayList<>();
			info.setDatiExt(listaDatiExt);
			
			for (int i = 0; i < splitDiagnosticListExt.length; i++) {
				
				String diagnostic = splitDiagnosticListExt[i].trim();
				listaDiagnosticiExt.add(InfoDiagnostico.convertoFromDBColumnValue(gdoFirstDiagnostic, diagnostic));
				
				String valueExt = splitDatiExt[i].trim();
				listaDatiExt.add(DynamicExtendedInfoDiagnostico.convertoFromDBColumnValue(valueExt));
				
			}
			
		}
		
		return info;
	}
}
