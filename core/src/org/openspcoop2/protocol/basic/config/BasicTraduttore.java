/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.basic.config;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.basic.Costanti;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ContestoCodificaEccezione;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.LivelloRilevanza;
import org.openspcoop2.protocol.sdk.constants.MessaggiFaultErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.SubCodiceErrore;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.utils.date.DateManager;


/**	
 * BasicTraduttore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BasicTraduttore implements org.openspcoop2.protocol.sdk.config.ITraduttore {

	protected Logger log = null;
	protected IProtocolFactory protocolFactory;
	

	public BasicTraduttore(IProtocolFactory factory){
		this.log = factory.getLogger();
		this.protocolFactory = factory;
	}

	@Override
	public IProtocolFactory getProtocolFactory() {
		return this.protocolFactory;
	}
	
	
	public String getPrefissoEccezioni() {
		return "ECCEZIONE_PROTOCOLLO_";
	}

	@Override
	public String toString(CodiceErroreCooperazione cod) {
		return toString(cod, null);
	}
	
	@Override
	public String toString(CodiceErroreCooperazione cod,SubCodiceErrore subCode) {
		if(subCode==null || subCode.getSubCodice()==null)
			return getPrefissoEccezioni() + cod.getCodice();
		else
			return getPrefissoEccezioni() + cod.getCodice() + "_" + subCode.getSubCodice();
	}
	
	@Override
	public CodiceErroreCooperazione toCodiceErroreCooperazione(String codiceCooperazione) {
		if(codiceCooperazione == null) return CodiceErroreCooperazione.UNKNOWN;
		// Elimino il prefisso
		String codiceNum = codiceCooperazione.replace(getPrefissoEccezioni(), "");
		
		try{
			if(codiceNum.contains("_")){
				// e' presente il subCode
				codiceNum = codiceNum.split("_")[0];
			}
			
			//Provo a convertire i 3 caratteri in un codice intero registrato.
			return CodiceErroreCooperazione.toCodiceErroreCooperazione(Integer.parseInt(codiceNum));
		} catch (Exception e){
			return CodiceErroreCooperazione.UNKNOWN;
		}
	}

	@Override
	public String toString(MessaggiFaultErroreCooperazione msg){
		return msg.toString();
	}
	
	@Override
	public String toString(ErroreCooperazione msg){
		return msg.getDescrizioneRawValue();
	}
	
	@Override
	public String toString(CodiceErroreIntegrazione cod,String prefix, boolean isGenericCodeFor5XX) {
    	
		String codiceMappato = cod.getCodice() + "";
    	
    	if(isGenericCodeFor5XX){
    		// devo ritornare un codice generico, elimino ultime due cifre e aggiungo 00
    		if(cod.getCodice() >= 500 && 
    				!cod.equals(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE) &&
    				!cod.equals(CodiceErroreIntegrazione.CODICE_517_RISPOSTA_RICHIESTA_NON_RITORNATA) &&
    				!cod.equals(CodiceErroreIntegrazione.CODICE_518_RISPOSTA_RICHIESTA_RITORNATA_COME_FAULT) &&
    				!cod.equals(CodiceErroreIntegrazione.CODICE_559_RICEVUTA_RISPOSTA_CON_ERRORE_TRASPORTO) ){
    			codiceMappato = "500";
    		}
    	}
    	
    	codiceMappato = (prefix == null ? Costanti.ERRORE_INTEGRAZIONE_PREFIX_CODE : prefix) + codiceMappato;
    	
    	return codiceMappato;
		
	}

	@Override
	public CodiceErroreIntegrazione toCodiceErroreIntegrazione(
			String codiceErroreIntegrazione, String prefix) {
		
		try{
			String intValue = codiceErroreIntegrazione.substring(prefix.length());
			return CodiceErroreIntegrazione.toCodiceErroreIntegrazione(Integer.parseInt(intValue));
		}catch(Exception e){
			this.log.error("Conversione in CodiceErroreIntegrazione della stringa["+codiceErroreIntegrazione+"] prefix["+prefix+"] non riuscita: "+e.getMessage(),e);
			return CodiceErroreIntegrazione.UNKNOWN;
		}
		
	}
	
	@Override
	public String toString(ErroreIntegrazione msg) {
		return msg.getDescrizioneRawValue();
	}
	
	@Override
	public String getDate_protocolFormat() {
		return getDate_protocolFormat(null);
	}
	
	@Override
	public String getDate_protocolFormat(Date date) {
		if(date == null) 
			date = DateManager.getDate();
		SimpleDateFormat dateformat = new SimpleDateFormat ("yyyy-MM-dd_HH:mm:ss.SSS"); // SimpleDateFormat non e' thread-safe
		return dateformat.format(date).replace('_','T');
	}

	/**
	 * Restituisce il valore dell'identificativo porta di default per il soggetto.
	 * @param soggetto
	 * @return IdentificativoPortaDefault
	 */
	@Override
	public String getIdentificativoPortaDefault(IDSoggetto soggetto){
		return soggetto.getNome()+"PdD";
	}

	/**
	 * Restituisce il valore del Codice IPA di Default
	 * @param soggetto
	 * @return IdentificativoCodiceIPADefault
	 * @throws ProtocolException 
	 */
	@Override
	public String getIdentificativoCodiceIPADefault(IDSoggetto soggetto,boolean createURI) throws ProtocolException{
		if(soggetto==null || "".equals(soggetto))
			throw new ProtocolException("Identificativo soggetto non fornito");
		StringBuffer bf = new StringBuffer();
		if(createURI){
			bf.append("uri:dn:");
		}
		bf.append("o=");
		bf.append(getIdentificativoCodiceIPA_Nome(soggetto));
		bf.append(",c=it");
		return bf.toString();
	}
	protected String getIdentificativoCodiceIPA_Nome(IDSoggetto idSoggetto){
		// non standard, utilizzo tipo/nome per avere l'univocita'
		return idSoggetto.toString();
	}
	
	@Override
	public String toString(Inoltro inoltro) {
		return inoltro.getEngineValue();
	}

	@Override
	public String toString(ProfiloDiCollaborazione profilo) {
		return profilo.getEngineValue();
	}

	@Override
	public String toString(TipoOraRegistrazione tipo) {
		return tipo.getEngineValue();
	}

	@Override
	public String toString(LivelloRilevanza rilevanza) {
		return rilevanza.getEngineValue();
	}
	
	@Override
	public LivelloRilevanza toLivelloRilevanza(String livelloRilevanza) {
		return LivelloRilevanza.toLivelloRilevanza(livelloRilevanza);
	}

	@Override
	public Inoltro toInoltro(String inoltro) {
		return Inoltro.toInoltro(inoltro);
	}

	@Override
	public TipoOraRegistrazione toTipoOraRegistrazione(
			String tipoOraRegistrazione) {
		return TipoOraRegistrazione.toTipoOraRegistrazione(tipoOraRegistrazione);
	}

	@Override
	public ProfiloDiCollaborazione toProfiloDiCollaborazione(
			String profiloDiCollaborazione) {
		return ProfiloDiCollaborazione.toProfiloDiCollaborazione(profiloDiCollaborazione);
	}

	@Override
	public String toString(ContestoCodificaEccezione contesto) {
		return contesto.getEngineValue();
	}

	@Override
	public ContestoCodificaEccezione toContestoCodificaEccezione(
			String contestoCodificaEccezione) {
		return ContestoCodificaEccezione.toContestoCodificaEccezione(contestoCodificaEccezione);
	}


}
