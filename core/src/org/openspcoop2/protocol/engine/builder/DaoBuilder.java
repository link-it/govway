/*
 * OpenSPCoop - Customizable API Gateway 
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

package org.openspcoop2.protocol.engine.builder;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Date;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.eccezione.details.Dettagli;
import org.openspcoop2.core.eccezione.details.Dettaglio;
import org.openspcoop2.core.eccezione.details.DettaglioEccezione;
import org.openspcoop2.core.eccezione.details.Dominio;
import org.openspcoop2.core.eccezione.details.Eccezione;
import org.openspcoop2.core.eccezione.details.Eccezioni;
import org.openspcoop2.core.eccezione.details.constants.Costanti;
import org.openspcoop2.core.eccezione.details.DominioSoggetto;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.utils.date.DateManager;

/**
 * DAOBuilder
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class DaoBuilder {
	/** ------------------- Metodi che generano un DettaglioEccezione ---------------- */

	public static Dominio buildDominio(IDSoggetto identitaPdD,TipoPdD tipoPdD,String modulo){
		String idFunzione = null;
		if(TipoPdD.DELEGATA.equals(tipoPdD)){
			idFunzione = org.openspcoop2.core.eccezione.details.constants.Costanti.TIPO_PDD_PORTA_DELEGATA;
		}
		else if(TipoPdD.APPLICATIVA.equals(tipoPdD)){
			idFunzione = org.openspcoop2.core.eccezione.details.constants.Costanti.TIPO_PDD_PORTA_APPLICATIVA;
		}
		else if(TipoPdD.INTEGRATION_MANAGER.equals(tipoPdD)){
			idFunzione = org.openspcoop2.core.eccezione.details.constants.Costanti.TIPO_PDD_INTEGRATION_MANAGER;
		}
		else if(TipoPdD.ROUTER.equals(tipoPdD)){
			idFunzione = org.openspcoop2.core.eccezione.details.constants.Costanti.TIPO_PDD_ROUTER;
		}
		
		Dominio dominio = new Dominio();
		DominioSoggetto dominioSoggetto = new DominioSoggetto();
		dominioSoggetto.setBase(identitaPdD.getNome());
		dominioSoggetto.setTipo(identitaPdD.getTipo());
		dominio.setSoggetto(dominioSoggetto);
		dominio.setIdentificativoPorta(identitaPdD.getCodicePorta());
		dominio.setFunzione(idFunzione);
		dominio.setModulo(modulo);
		return dominio;
	}
	
	public static DettaglioEccezione buildDettaglioEccezione(IDSoggetto identitaPdD,TipoPdD tipoPdD,String modulo, String codErrore,String msgErrore,boolean isErroreProtocollo){
		DettaglioEccezione dettaglioEccezione = new DettaglioEccezione();
			
		dettaglioEccezione.setOraRegistrazione(DateManager.getDate());
		
		dettaglioEccezione.setDominio(buildDominio(identitaPdD, tipoPdD, modulo));
		
		org.openspcoop2.core.eccezione.details.Eccezione eccezione = new org.openspcoop2.core.eccezione.details.Eccezione();
		eccezione.setCodice(codErrore);
		eccezione.setDescrizione(msgErrore);
		if(isErroreProtocollo){
			eccezione.setTipo(Costanti.TIPO_ECCEZIONE_PROTOCOLLO);
		}else{
			eccezione.setTipo(Costanti.TIPO_ECCEZIONE_INTEGRAZIONE);
		}
		if(dettaglioEccezione.getEccezioni()==null){
			dettaglioEccezione.setEccezioni(new Eccezioni());
		}
		dettaglioEccezione.getEccezioni().addEccezione(eccezione);
		return dettaglioEccezione;
	}
	
	public static DettaglioEccezione buildDettaglioEccezione(Date oraRegistrazione, IDSoggetto identitaPdD,TipoPdD tipoPdD, String modulo, Eccezione eccezione, Dettaglio dettaglio){
		DettaglioEccezione dettaglioEccezione = new DettaglioEccezione();

		// info generali
		dettaglioEccezione.setOraRegistrazione(oraRegistrazione);
		
		dettaglioEccezione.setDominio(buildDominio(identitaPdD, tipoPdD, modulo));
		
		if(eccezione != null){
			if(dettaglioEccezione.getEccezioni()==null){
				dettaglioEccezione.setEccezioni(new Eccezioni());
			}
			dettaglioEccezione.getEccezioni().addEccezione(eccezione);
		}
		
		if(dettaglio != null){
			if(dettaglioEccezione.getDettagli()==null){
				dettaglioEccezione.setDettagli(new Dettagli());
			}
			dettaglioEccezione.getDettagli().addDettaglio(dettaglio);
		}
		return dettaglioEccezione;
	}
	
	public static Eccezione buildEccezione(String codice, String descrizione, String rilevanza, String contesto, boolean isErroreProtocollo){
		Eccezione eccezione = new Eccezione();
		eccezione.setCodice(codice);
		eccezione.setDescrizione(descrizione);
		eccezione.setRilevanza(rilevanza);
		eccezione.setContestoCodifica(contesto);
		if(isErroreProtocollo){
			eccezione.setTipo(Costanti.TIPO_ECCEZIONE_PROTOCOLLO);
		}else{
			eccezione.setTipo(Costanti.TIPO_ECCEZIONE_INTEGRAZIONE);
		}
		return eccezione;
	}
	
	public static Dettaglio buildDettagio(String tipo, String base){
		Dettaglio dettaglio = new Dettaglio();
		dettaglio.setTipo(tipo);
		dettaglio.setBase(base);
		return dettaglio;
	}
	
	
	
	
	
	
	
	
	
	public static void gestioneDettaglioEccezioneIntegrazione(IProtocolFactory protocolFactory,Exception eProcessamento,DettaglioEccezione dettaglioEccezione,boolean generaInformazioniGeneriche) throws ProtocolException{
		IProtocolManager protocolManager = protocolFactory.createProtocolManager();
		DaoBuilder.gestioneDettaglioEccezioneProcessamento_engine(eProcessamento, dettaglioEccezione, 
				protocolManager.isGenerazioneDetailsSOAPFaultIntegrationeConStackTrace(),
				generaInformazioniGeneriche);
	}
	public static void gestioneDettaglioEccezioneProcessamento(IProtocolFactory protocolFactory,Exception eProcessamento,DettaglioEccezione dettaglioEccezione) throws ProtocolException{
		IProtocolManager protocolManager = protocolFactory.createProtocolManager();
		DaoBuilder.gestioneDettaglioEccezioneProcessamento_engine(eProcessamento, dettaglioEccezione, 
				protocolManager.isGenerazioneDetailsSOAPFaultProtocolloConStackTrace(),
				protocolManager.isGenerazioneDetailsSOAPFaultProtocolloConInformazioniGeneriche());
	}
	
	private static void gestioneDettaglioEccezioneProcessamento_engine(Exception eProcessamento,DettaglioEccezione dettaglioEccezione,
			boolean generaStackTrace,boolean generaInformazioniGeneriche){
		if(eProcessamento!=null){

			if(generaInformazioniGeneriche){

				Dettaglio detail = new Dettaglio();
				detail.setTipo("causa");

				String msg = null;
				if(eProcessamento.getMessage()!=null){
					msg = eProcessamento.getMessage();
				}else{
					msg = eProcessamento.toString();
				}
				//System.out.println("MESSAGE ["+msg+"]");
				// faccio viaggiare solo le informazioni che ritengo pubbliche
				if("Connection refused".equals(msg)){
					msg = "Connection refused";
					detail.setBase(msg);
					if(dettaglioEccezione.getDettagli()==null){
						dettaglioEccezione.setDettagli(new Dettagli());
					}
					dettaglioEccezione.getDettagli().addDettaglio(detail);			
				}else if("Read timed out".equals(msg)){
					msg = "Read timed out";
					detail.setBase(msg);
					if(dettaglioEccezione.getDettagli()==null){
						dettaglioEccezione.setDettagli(new Dettagli());
					}
					dettaglioEccezione.getDettagli().addDettaglio(detail);	
				}else if("connect timed out".equals(msg)){
					msg = "Connect timed out";
					detail.setBase(msg);
					if(dettaglioEccezione.getDettagli()==null){
						dettaglioEccezione.setDettagli(new Dettagli());
					}
					dettaglioEccezione.getDettagli().addDettaglio(detail);	
				}

			}else{
				String base;
				if(eProcessamento.getMessage()!=null)
					base = eProcessamento.getMessage();
				else
					base = eProcessamento.toString();
				
				Dettaglio detail = DaoBuilder.buildDettagio("causa",base);
				
				if(dettaglioEccezione.getDettagli()==null){
					dettaglioEccezione.setDettagli(new Dettagli());
				}
				dettaglioEccezione.getDettagli().addDettaglio(detail);

				if(eProcessamento.getCause()!=null){
					DaoBuilder.gestioneDettaglioEccezioneProcessamento_engine_InnerException(eProcessamento.getCause(), dettaglioEccezione);
				}

				if(generaStackTrace){
					Dettaglio detailStackTrace = new Dettaglio();
					detailStackTrace.setTipo("stackTrace");
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					PrintWriter pWriter = new PrintWriter(bout);
					eProcessamento.printStackTrace(pWriter);
					try{
						bout.flush();
						pWriter.flush();
						pWriter.close();
						bout.close();
					}catch(Exception eClose){
						System.err.println("ERRORE buildEccezioneProcessamentoFromBusta: "+eClose.getMessage());
					}
					detailStackTrace.setBase(bout.toString());
					if(dettaglioEccezione.getDettagli()==null){
						dettaglioEccezione.setDettagli(new Dettagli());
					}
					dettaglioEccezione.getDettagli().addDettaglio(detailStackTrace);
				}
			}
		}
	}
	
	private static void gestioneDettaglioEccezioneProcessamento_engine_InnerException(Throwable e,DettaglioEccezione dettaglioEccezione){
		if(e!=null){
			String base;
			if(e.getMessage()!=null)
				base = e.getMessage();
			else
				base = e.toString();
			
			Dettaglio detail = DaoBuilder.buildDettagio("causato da", base);
			
			
			if(dettaglioEccezione.getDettagli()==null){
				dettaglioEccezione.setDettagli(new Dettagli());
			}
			dettaglioEccezione.getDettagli().addDettaglio(detail);

			if(e.getCause()!=null){
				DaoBuilder.gestioneDettaglioEccezioneProcessamento_engine_InnerException(e.getCause(), dettaglioEccezione);
			}
		}
	}
}
