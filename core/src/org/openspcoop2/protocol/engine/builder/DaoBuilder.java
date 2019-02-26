/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.engine.builder;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Date;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.eccezione.details.Dettagli;
import org.openspcoop2.core.eccezione.details.Dettaglio;
import org.openspcoop2.core.eccezione.details.DettaglioEccezione;
import org.openspcoop2.core.eccezione.details.Dominio;
import org.openspcoop2.core.eccezione.details.DominioSoggetto;
import org.openspcoop2.core.eccezione.details.Eccezione;
import org.openspcoop2.core.eccezione.details.Eccezioni;
import org.openspcoop2.core.eccezione.details.constants.TipoEccezione;
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
		org.openspcoop2.core.eccezione.details.constants.TipoPdD idFunzione = null;
		if(TipoPdD.DELEGATA.equals(tipoPdD)){
			idFunzione = org.openspcoop2.core.eccezione.details.constants.TipoPdD.OUTBOUND_PROXY;
		}
		else if(TipoPdD.APPLICATIVA.equals(tipoPdD)){
			idFunzione = org.openspcoop2.core.eccezione.details.constants.TipoPdD.INBOUND_PROXY;
		}
		else if(TipoPdD.INTEGRATION_MANAGER.equals(tipoPdD)){
			idFunzione = org.openspcoop2.core.eccezione.details.constants.TipoPdD.INTEGRATION_MANAGER;
		}
		else if(TipoPdD.ROUTER.equals(tipoPdD)){
			idFunzione = org.openspcoop2.core.eccezione.details.constants.TipoPdD.ROUTER;
		}
		
		Dominio dominio = new Dominio();
		DominioSoggetto dominioSoggetto = new DominioSoggetto();
		dominioSoggetto.setBase(identitaPdD.getNome());
		dominioSoggetto.setType(identitaPdD.getTipo());
		dominio.setOrganization(dominioSoggetto);
		dominio.setId(identitaPdD.getCodicePorta());
		dominio.setRole(idFunzione);
		dominio.setModule(modulo);
		return dominio;
	}
	
	public static DettaglioEccezione buildDettaglioEccezione(IDSoggetto identitaPdD,TipoPdD tipoPdD,String modulo, String codErrore,String msgErrore,boolean isErroreProtocollo){
		DettaglioEccezione dettaglioEccezione = new DettaglioEccezione();
			
		dettaglioEccezione.setTimestamp(DateManager.getDate());
		
		dettaglioEccezione.setDomain(buildDominio(identitaPdD, tipoPdD, modulo));
		
		org.openspcoop2.core.eccezione.details.Eccezione eccezione = new org.openspcoop2.core.eccezione.details.Eccezione();
		eccezione.setCode(codErrore);
		eccezione.setDescription(msgErrore);
		if(isErroreProtocollo){
			eccezione.setType(TipoEccezione.PROTOCOL);
		}else{
			eccezione.setType(TipoEccezione.INTEGRATION);
		}
		if(dettaglioEccezione.getExceptions()==null){
			dettaglioEccezione.setExceptions(new Eccezioni());
		}
		dettaglioEccezione.getExceptions().addException(eccezione);
		return dettaglioEccezione;
	}
	
	public static DettaglioEccezione buildDettaglioEccezione(Date oraRegistrazione, IDSoggetto identitaPdD,TipoPdD tipoPdD, String modulo, Eccezione eccezione, Dettaglio dettaglio){
		DettaglioEccezione dettaglioEccezione = new DettaglioEccezione();

		// info generali
		dettaglioEccezione.setTimestamp(oraRegistrazione);
		
		dettaglioEccezione.setDomain(buildDominio(identitaPdD, tipoPdD, modulo));
		
		if(eccezione != null){
			if(dettaglioEccezione.getExceptions()==null){
				dettaglioEccezione.setExceptions(new Eccezioni());
			}
			dettaglioEccezione.getExceptions().addException(eccezione);
		}
		
		if(dettaglio != null){
			if(dettaglioEccezione.getDetails()==null){
				dettaglioEccezione.setDetails(new Dettagli());
			}
			dettaglioEccezione.getDetails().addDetail(dettaglio);
		}
		return dettaglioEccezione;
	}
	
	public static Eccezione buildEccezione(String codice, String descrizione, String rilevanza, String contesto, boolean isErroreProtocollo){
		Eccezione eccezione = new Eccezione();
		eccezione.setCode(codice);
		eccezione.setDescription(descrizione);
		eccezione.setSeverity(rilevanza);
		eccezione.setContext(contesto);
		if(isErroreProtocollo){
			eccezione.setType(TipoEccezione.PROTOCOL);
		}else{
			eccezione.setType(TipoEccezione.INTEGRATION);
		}
		return eccezione;
	}
	
	public static Dettaglio buildDettagio(String tipo, String base){
		Dettaglio dettaglio = new Dettaglio();
		dettaglio.setType(tipo);
		dettaglio.setBase(base);
		return dettaglio;
	}
	
	
	
	
	
	
	
	
	
	public static void gestioneDettaglioEccezioneIntegrazione(IProtocolFactory<?> protocolFactory,Exception eProcessamento,DettaglioEccezione dettaglioEccezione,boolean generaInformazioniGeneriche) throws ProtocolException{
		IProtocolManager protocolManager = protocolFactory.createProtocolManager();
		DaoBuilder.gestioneDettaglioEccezioneProcessamento_engine(eProcessamento, dettaglioEccezione, 
				protocolManager.isGenerazioneDetailsFaultIntegrationeConStackTrace(),
				generaInformazioniGeneriche);
	}
	public static void gestioneDettaglioEccezioneProcessamento(IProtocolFactory<?> protocolFactory,Exception eProcessamento,DettaglioEccezione dettaglioEccezione) throws ProtocolException{
		IProtocolManager protocolManager = protocolFactory.createProtocolManager();
		DaoBuilder.gestioneDettaglioEccezioneProcessamento_engine(eProcessamento, dettaglioEccezione, 
				protocolManager.isGenerazioneDetailsFaultProtocolloConStackTrace(),
				protocolManager.isGenerazioneDetailsFaultProtocolloConInformazioniGeneriche());
	}
	
	private static void gestioneDettaglioEccezioneProcessamento_engine(Exception eProcessamento,DettaglioEccezione dettaglioEccezione,
			boolean generaStackTrace,boolean generaInformazioniGeneriche){
		if(eProcessamento!=null){

			if(generaInformazioniGeneriche){

				Dettaglio detail = new Dettaglio();
				detail.setType("causa");

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
					if(dettaglioEccezione.getDetails()==null){
						dettaglioEccezione.setDetails(new Dettagli());
					}
					dettaglioEccezione.getDetails().addDetail(detail);			
				}else if("Read timed out".equals(msg)){
					msg = "Read timed out";
					detail.setBase(msg);
					if(dettaglioEccezione.getDetails()==null){
						dettaglioEccezione.setDetails(new Dettagli());
					}
					dettaglioEccezione.getDetails().addDetail(detail);	
				}else if("connect timed out".equals(msg)){
					msg = "Connect timed out";
					detail.setBase(msg);
					if(dettaglioEccezione.getDetails()==null){
						dettaglioEccezione.setDetails(new Dettagli());
					}
					dettaglioEccezione.getDetails().addDetail(detail);	
				}

			}else{
				String base;
				if(eProcessamento.getMessage()!=null)
					base = eProcessamento.getMessage();
				else
					base = eProcessamento.toString();
				
				Dettaglio detail = DaoBuilder.buildDettagio("causa",base);
				
				if(dettaglioEccezione.getDetails()==null){
					dettaglioEccezione.setDetails(new Dettagli());
				}
				dettaglioEccezione.getDetails().addDetail(detail);

				if(eProcessamento.getCause()!=null){
					DaoBuilder.gestioneDettaglioEccezioneProcessamento_engine_InnerException(eProcessamento.getCause(), dettaglioEccezione);
				}

				if(generaStackTrace){
					Dettaglio detailStackTrace = new Dettaglio();
					detailStackTrace.setType("stackTrace");
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
					if(dettaglioEccezione.getDetails()==null){
						dettaglioEccezione.setDetails(new Dettagli());
					}
					dettaglioEccezione.getDetails().addDetail(detailStackTrace);
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
			
			
			if(dettaglioEccezione.getDetails()==null){
				dettaglioEccezione.setDetails(new Dettagli());
			}
			dettaglioEccezione.getDetails().addDetail(detail);

			if(e.getCause()!=null){
				DaoBuilder.gestioneDettaglioEccezioneProcessamento_engine_InnerException(e.getCause(), dettaglioEccezione);
			}
		}
	}
}
