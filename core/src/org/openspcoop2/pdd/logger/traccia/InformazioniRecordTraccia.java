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

package org.openspcoop2.pdd.logger.traccia;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.CoreException;

import org.openspcoop2.pdd.logger.record.AbstractDatoRicostruzione;
import org.openspcoop2.pdd.logger.record.CharDatoRicostruzione;
import org.openspcoop2.pdd.logger.record.StringDatoRicostruzione;
import org.openspcoop2.pdd.logger.record.TimestampDatoRicostruzione;

/**     
 * InformazioniSalvataggioTraccia
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InformazioniRecordTraccia {

	protected boolean presente = false;
	
	protected boolean ricostruibile = false;
	
	protected List<AbstractDatoRicostruzione<?>> dati = new ArrayList<>();

	protected String motivoRicostruzioneNonFattibile = null;
	
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
	
	public boolean isRicostruibile() {
		return this.ricostruibile;
	}
	
	public boolean isPresente() {
		return this.presente;
	}

	public AbstractDatoRicostruzione<?> getDato(MappingRicostruzioneTraccia mapping){
		if(mapping.getValue()==null) {
			return null;
		}
		int index = mapping.getValue();
		if(index < this.dati.size()) {
			return this.dati.get(index);
		}
		return null;
	}
	
	public void setPresente(boolean presente) {
		this.presente = presente;
	}
	
	public void setRicostruibile(boolean ricostruibile) {
		this.ricostruibile = ricostruibile;
	}

	public void setMotivoRicostruzioneNonFattibile(
			String motivoRicostruzioneNonFattibile) {
		this.motivoRicostruzioneNonFattibile = motivoRicostruzioneNonFattibile;
	}

	public void setDati(List<AbstractDatoRicostruzione<?>> dati) {
		this.dati = dati;
	}
	
	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();
		bf.append("---- Traccia -----\n");
		bf.append("\tpresente("+this.presente+")\n");
		bf.append("\tricostruibile("+this.ricostruibile+")\n");
		bf.append("\tdati size:"+this.dati.size()).append("\n");
		for (int i = 0; i < this.dati.size(); i++) {
			bf.append("\t\tdato["+MappingRicostruzioneTraccia.toEnumConstant(i).name()+"("+i+")]").append("\n");
			bf.append("\t\t\tdescrizione:"+this.dati.get(i).getInfo().getDescription()).append("\n");
			try{
				bf.append("\t\t\tvalore:"+this.dati.get(i).convertToString()).append("\n");
			}catch(Exception e){
				bf.append("\t\t\tvalore: ERRORE NEL CALCOLO: "+e.getMessage()).append("\n");
			}
		}
		if(!this.ricostruibile){
			bf.append(" motivoRicostruzioneNonFattibile("+this.motivoRicostruzioneNonFattibile+")\n");	
		}
		return bf.toString();
	}
	

	public static InformazioniRecordTraccia convertoFromDBColumnValue(String value) throws CoreException{
		
		InformazioniRecordTraccia info = new InformazioniRecordTraccia();
				
		if(value == null || "".equals(value.trim())){
			throw new CoreException("Valore non fornito");
		}
		info.setRawDBValue(value);
		
		if(value.length()==1 && (CostantiMappingTracciamento.NON_PRESENTE == value.charAt(0)) ){
			info.setPresente(false);
			return info;
		}
		
		info.setPresente(true);
		
		String dbValue = value.trim();
		
		if(!dbValue.contains(CostantiMappingTracciamento.SEPARATOR)){
			info.setRicostruibile(false);
			info.setMotivoRicostruzioneNonFattibile("Non sono presenti caratteri separatori ["+CostantiMappingTracciamento.SEPARATOR+"] nel valore");
			return info;
		}
		
		String [] split = dbValue.split(CostantiMappingTracciamento.SEPARATOR);
		if( CostantiMappingTracciamento.TRACCIA_EMESSA_RICOSTRUIBILE != split[0].charAt(0) ){
			info.setRicostruibile(false);
			info.setMotivoRicostruzioneNonFattibile(dbValue);
		}
		else{
			info.setRicostruibile(true);
			List<AbstractDatoRicostruzione<?>> listaDati = new ArrayList<>();
			info.setDati(listaDati);
			
			listaDati.add(new CharDatoRicostruzione(split[MappingRicostruzioneTraccia.TRACCIA_EMESSA.getPosition()],
					CostantiMappingTracciamento.TRACCIA_EMESSA));

			listaDati.add(new TimestampDatoRicostruzione(split[MappingRicostruzioneTraccia.TRACCIA_DATA_REGISTRAZIONE.getPosition()],
					CostantiMappingTracciamento.TRACCIA_DATA_REGISTRAZIONE));
			
			listaDati.add(new TimestampDatoRicostruzione(split[MappingRicostruzioneTraccia.TRACCIA_BUSTA_ORA_REGISTRAZIONE.getPosition()],
					CostantiMappingTracciamento.TRACCIA_BUSTA_ORA_REGISTRAZIONE));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneTraccia.TRACCIA_BUSTA_TIPO_ORA_REGISTRAZIONE_BY_PROTOCOLLO.getPosition()],
					CostantiMappingTracciamento.TRACCIA_BUSTA_TIPO_ORA_REGISTRAZIONE_BY_PROTOCOLLO));
			
			listaDati.add(new CharDatoRicostruzione(split[MappingRicostruzioneTraccia.TRACCIA_BUSTA_TIPO_ORA_REGISTRAZIONE_BY_OPENSPCOOP.getPosition()],
					CostantiMappingTracciamento.TRACCIA_BUSTA_TIPO_ORA_REGISTRAZIONE_BY_OPENSPCOOP));

			listaDati.add(new CharDatoRicostruzione(split[MappingRicostruzioneTraccia.TRACCIA_BUSTA_RIFERIMENTO_MESSAGGIO.getPosition()],
					CostantiMappingTracciamento.TRACCIA_BUSTA_RIFERIMENTO_MESSAGGIO));

			listaDati.add(new TimestampDatoRicostruzione(split[MappingRicostruzioneTraccia.TRACCIA_BUSTA_SCADENZA.getPosition()],
					CostantiMappingTracciamento.TRACCIA_BUSTA_SCADENZA));

			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneTraccia.TRACCIA_FILTRO_DUPLICATI.getPosition()],
					CostantiMappingTracciamento.TRACCIA_FILTRO_DUPLICATI));

			listaDati.add(new CharDatoRicostruzione(split[MappingRicostruzioneTraccia.TRACCIA_FILTRO_DUPLICATI_CODE.getPosition()],
					CostantiMappingTracciamento.TRACCIA_FILTRO_DUPLICATI_CODE));

			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneTraccia.TRACCIA_BUSTA_SEQUENZA.getPosition()],
					CostantiMappingTracciamento.TRACCIA_BUSTA_SEQUENZA));

			listaDati.add(new TimestampDatoRicostruzione(split[MappingRicostruzioneTraccia.TRACCIA_BUSTA_RISCONTRO_ORA_REGISTRAZIONE.getPosition()],
					CostantiMappingTracciamento.TRACCIA_BUSTA_RISCONTRO_ORA_REGISTRAZIONE));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneTraccia.TRACCIA_BUSTA_RISCONTRO_TIPO_ORA_REGISTRAZIONE.getPosition()],
					CostantiMappingTracciamento.TRACCIA_BUSTA_RISCONTRO_TIPO_ORA_REGISTRAZIONE));
			
			
			listaDati.add(new CharDatoRicostruzione(split[MappingRicostruzioneTraccia.TRACCIA_BUSTA_RISCONTRO_TIPO_ORA_REGISTRAZIONE_CODE.getPosition()],
					CostantiMappingTracciamento.TRACCIA_BUSTA_RISCONTRO_TIPO_ORA_REGISTRAZIONE_CODE));

			listaDati.add(new TimestampDatoRicostruzione(split[MappingRicostruzioneTraccia.TRACCIA_BUSTA_PRIMA_TRASMISSIONE_ORA_REGISTRAZIONE.getPosition()],
					CostantiMappingTracciamento.TRACCIA_BUSTA_PRIMA_TRASMISSIONE_ORA_REGISTRAZIONE));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneTraccia.TRACCIA_BUSTA_PRIMA_TRASMISSIONE_TIPO_ORA_REGISTRAZIONE.getPosition()],
					CostantiMappingTracciamento.TRACCIA_BUSTA_PRIMA_TRASMISSIONE_TIPO_ORA_REGISTRAZIONE));
			
			listaDati.add(new CharDatoRicostruzione(split[MappingRicostruzioneTraccia.TRACCIA_BUSTA_PRIMA_TRASMISSIONE_TIPO_ORA_REGISTRAZIONE_CODE.getPosition()],
					CostantiMappingTracciamento.TRACCIA_BUSTA_PRIMA_TRASMISSIONE_TIPO_ORA_REGISTRAZIONE_CODE));
			
			listaDati.add(new TimestampDatoRicostruzione(split[MappingRicostruzioneTraccia.TRACCIA_BUSTA_SECONDA_TRASMISSIONE_ORA_REGISTRAZIONE.getPosition()],
					CostantiMappingTracciamento.TRACCIA_BUSTA_SECONDA_TRASMISSIONE_ORA_REGISTRAZIONE));
			
			listaDati.add(new StringDatoRicostruzione(split[MappingRicostruzioneTraccia.TRACCIA_BUSTA_SECONDA_TRASMISSIONE_TIPO_ORA_REGISTRAZIONE.getPosition()],
					CostantiMappingTracciamento.TRACCIA_BUSTA_SECONDA_TRASMISSIONE_TIPO_ORA_REGISTRAZIONE));
			
			listaDati.add(new CharDatoRicostruzione(split[MappingRicostruzioneTraccia.TRACCIA_BUSTA_SECONDA_TRASMISSIONE_TIPO_ORA_REGISTRAZIONE_CODE.getPosition()],
					CostantiMappingTracciamento.TRACCIA_BUSTA_SECONDA_TRASMISSIONE_TIPO_ORA_REGISTRAZIONE_CODE));
			
			listaDati.add(new CharDatoRicostruzione(split[MappingRicostruzioneTraccia.TRACCIA_BUSTA_CONFERMA_RICHIESTA.getPosition()],
					CostantiMappingTracciamento.TRACCIA_BUSTA_CONFERMA_RICHIESTA));
			
			listaDati.add(new CharDatoRicostruzione(split[MappingRicostruzioneTraccia.TRACCIA_BUSTA_SERVIZIO_CORRELATO_PRESENTE.getPosition()],
					CostantiMappingTracciamento.TRACCIA_BUSTA_SERVIZIO_CORRELATO_PRESENTE));
			
			listaDati.add(new CharDatoRicostruzione(split[MappingRicostruzioneTraccia.TRACCIA_BUSTA_ESITO_TRACCIA.getPosition()],
					CostantiMappingTracciamento.TRACCIA_BUSTA_ESITO_TRACCIA));
						
			if(split.length>(CostantiMappingTracciamento.LENGHT_DATI_SIMULATI_VERSIONE_ATTUALE_PRECEDENTE_INTRODOTTO_22)){
			
				listaDati.add(new CharDatoRicostruzione(split[MappingRicostruzioneTraccia.TRACCIA_BUSTA_SOGGETTO_APPLICATIVO_TOKEN.getPosition()],
						CostantiMappingTracciamento.TRACCIA_BUSTA_SOGGETTO_APPLICATIVO_TOKEN));
				
			}
			
			if(split.length>CostantiMappingTracciamento.LENGHT_DATI_SIMULATI_VERSIONE_ATTUALE){
				
				// NOTA: successivi dati aggiunti
				// GESTIRE QUANDO SI INTRODUCONO NUOVE INFO
				
			}
		}
		
		return info;
	}
}
