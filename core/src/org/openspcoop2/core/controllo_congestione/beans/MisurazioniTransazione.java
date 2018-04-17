/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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

package org.openspcoop2.core.controllo_congestione.beans;

import java.io.Serializable;
import java.util.Date;

import org.openspcoop2.core.constants.TipoPdD;

/**
 * MisurazioniTransazione 
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MisurazioniTransazione implements Serializable , Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TipoPdD tipoPdD;
	
	private java.util.Date dataIngressoRichiesta;
	private java.util.Date dataUscitaRichiesta;
	private java.util.Date dataIngressoRisposta;
	private java.util.Date dataUscitaRisposta;

	private java.lang.Long richiestaIngressoBytes;
	private java.lang.Long richiestaUscitaBytes;
	private java.lang.Long rispostaIngressoBytes;
	private java.lang.Long rispostaUscitaBytes;
	
	private int esitoTransazione;

	public int getEsitoTransazione() {
		return this.esitoTransazione;
	}
	public void setEsitoTransazione(int esitoTransazione) {
		this.esitoTransazione = esitoTransazione;
	}
	public TipoPdD getTipoPdD() {
		return this.tipoPdD;
	}
	public void setTipoPdD(TipoPdD tipoPdD) {
		this.tipoPdD = tipoPdD;
	}
	
	public java.util.Date getDataIngressoRichiesta() {
		return this.dataIngressoRichiesta;
	}
	public void setDataIngressoRichiesta(java.util.Date dataIngressoRichiesta) {
		this.dataIngressoRichiesta = dataIngressoRichiesta;
	}
	public java.util.Date getDataUscitaRichiesta() {
		return this.dataUscitaRichiesta;
	}
	public void setDataUscitaRichiesta(java.util.Date dataUscitaRichiesta) {
		this.dataUscitaRichiesta = dataUscitaRichiesta;
	}
	public java.util.Date getDataIngressoRisposta() {
		return this.dataIngressoRisposta;
	}
	public void setDataIngressoRisposta(java.util.Date dataIngressoRisposta) {
		this.dataIngressoRisposta = dataIngressoRisposta;
	}
	public java.util.Date getDataUscitaRisposta() {
		return this.dataUscitaRisposta;
	}
	public void setDataUscitaRisposta(java.util.Date dataUscitaRisposta) {
		this.dataUscitaRisposta = dataUscitaRisposta;
	}
	public java.lang.Long getRichiestaIngressoBytes() {
		return this.richiestaIngressoBytes;
	}
	public void setRichiestaIngressoBytes(java.lang.Long richiestaIngressoBytes) {
		this.richiestaIngressoBytes = richiestaIngressoBytes;
	}
	public java.lang.Long getRichiestaUscitaBytes() {
		return this.richiestaUscitaBytes;
	}
	public void setRichiestaUscitaBytes(java.lang.Long richiestaUscitaBytes) {
		this.richiestaUscitaBytes = richiestaUscitaBytes;
	}
	public java.lang.Long getRispostaIngressoBytes() {
		return this.rispostaIngressoBytes;
	}
	public void setRispostaIngressoBytes(java.lang.Long rispostaIngressoBytes) {
		this.rispostaIngressoBytes = rispostaIngressoBytes;
	}
	public java.lang.Long getRispostaUscitaBytes() {
		return this.rispostaUscitaBytes;
	}
	public void setRispostaUscitaBytes(java.lang.Long rispostaUscitaBytes) {
		this.rispostaUscitaBytes = rispostaUscitaBytes;
	}
	
	
	
	// **** UTILITIES ****
	
	public static String serialize(MisurazioniTransazione misurazione){
		StringBuffer bf = new StringBuffer();
		
		
		if(misurazione.tipoPdD!=null){
			bf.append(misurazione.tipoPdD.getTipo());
		}
		else{
			bf.append("-");
		}
		bf.append("\n");
		
		if(misurazione.dataIngressoRichiesta!=null){
			bf.append(misurazione.dataIngressoRichiesta.getTime());
		}
		else{
			bf.append("-");
		}
		bf.append("\n");
		
		if(misurazione.dataUscitaRichiesta!=null){
			bf.append(misurazione.dataUscitaRichiesta.getTime());
		}
		else{
			bf.append("-");
		}
		bf.append("\n");
		
		if(misurazione.dataIngressoRisposta!=null){
			bf.append(misurazione.dataIngressoRisposta.getTime());
		}
		else{
			bf.append("-");
		}
		bf.append("\n");
		
		if(misurazione.dataUscitaRisposta!=null){
			bf.append(misurazione.dataUscitaRisposta.getTime());
		}
		else{
			bf.append("-");
		}
		bf.append("\n");
		
		if(misurazione.richiestaIngressoBytes!=null){
			bf.append(misurazione.richiestaIngressoBytes);
		}
		else{
			bf.append("-");
		}
		bf.append("\n");
		
		if(misurazione.richiestaUscitaBytes!=null){
			bf.append(misurazione.richiestaUscitaBytes);
		}
		else{
			bf.append("-");
		}
		bf.append("\n");
		
		if(misurazione.rispostaIngressoBytes!=null){
			bf.append(misurazione.rispostaIngressoBytes);
		}
		else{
			bf.append("-");
		}
		bf.append("\n");
		
		if(misurazione.rispostaUscitaBytes!=null){
			bf.append(misurazione.rispostaUscitaBytes);
		}
		else{
			bf.append("-");
		}
		bf.append("\n");
		
		bf.append(misurazione.esitoTransazione);
		
		return bf.toString();
	}
	
	public static MisurazioniTransazione deserialize(String s) throws Exception{
		MisurazioniTransazione misurazioni = new MisurazioniTransazione();
		String [] tmp = s.split("\n");
		if(tmp==null){
			throw new Exception("Wrong Format");
		}
		if(tmp.length!=10){
			throw new Exception("Wrong Format (size: "+tmp.length+")");
		}
		for (int i = 0; i < tmp.length; i++) {
			if(i==0){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					misurazioni.tipoPdD = TipoPdD.toTipoPdD(tmpValue);
				}
			}
			else if(i==1){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					misurazioni.dataIngressoRichiesta = new Date(Long.parseLong(tmpValue));
				}
			}
			else if(i==2){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					misurazioni.dataUscitaRichiesta = new Date(Long.parseLong(tmpValue));
				}
			}
			else if(i==3){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					misurazioni.dataIngressoRisposta = new Date(Long.parseLong(tmpValue));
				}
			}
			else if(i==4){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					misurazioni.dataUscitaRisposta = new Date(Long.parseLong(tmpValue));
				}
			}
			else if(i==5){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					misurazioni.richiestaIngressoBytes = Long.parseLong(tmpValue);
				}
			}
			else if(i==6){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					misurazioni.richiestaUscitaBytes = Long.parseLong(tmpValue);
				}
			}
			else if(i==7){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					misurazioni.rispostaIngressoBytes = Long.parseLong(tmpValue);
				}
			}
			else if(i==8){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					misurazioni.rispostaUscitaBytes = Long.parseLong(tmpValue);
				}
			}
			else if(i==9){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					misurazioni.esitoTransazione = Integer.parseInt(tmpValue);
				}
			}
		}
		
		return misurazioni;
	}
}
