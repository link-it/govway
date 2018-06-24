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
package org.openspcoop2.pdd.core.controllo_traffico.policy;

import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.handlers.InRequestProtocolContext;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.slf4j.Logger;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento;
import org.openspcoop2.core.controllo_traffico.beans.DatiTransazione;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;

/**     
 * InterceptorPolicyUtilities
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InterceptorPolicyUtilities {


	public static IDUnivocoGroupByPolicy convertToID(Logger log,DatiTransazione datiTransazione, AttivazionePolicyRaggruppamento policyGroupBy,
			InRequestProtocolContext context) throws Exception{
		
		IDUnivocoGroupByPolicy groupBy = new IDUnivocoGroupByPolicy();
		
		if(policyGroupBy.isRuoloPorta()){
			groupBy.setRuoloPorta(datiTransazione.getTipoPdD().getTipo());
		}
		
		if(policyGroupBy.isProtocollo()){
			groupBy.setProtocollo(datiTransazione.getProtocollo());
		}
		
		if(policyGroupBy.isFruitore()){
			groupBy.setFruitore(datiTransazione.getSoggettoFruitore().toString());
		}
		
		if(TipoPdD.DELEGATA.equals(datiTransazione.getTipoPdD())){
			if(policyGroupBy.isServizioApplicativoFruitore()){
				groupBy.setServizioApplicativoFruitore(datiTransazione.getServizioApplicativoFruitore());
			}
		}
		
		if(policyGroupBy.isErogatore()){
			groupBy.setErogatore(datiTransazione.getIdServizio().getSoggettoErogatore().toString());
		}
		
		if(TipoPdD.APPLICATIVA.equals(datiTransazione.getTipoPdD())){
			if(policyGroupBy.isServizioApplicativoErogatore()){
				groupBy.setServizioApplicativoErogatore(datiTransazione.getServiziApplicativiErogatoreAsString());
			}
		}
		
		if(policyGroupBy.isServizio()){
			groupBy.setServizio(datiTransazione.getIdServizio().getTipo()+"/"+datiTransazione.getIdServizio().getNome()+"/"+datiTransazione.getIdServizio().getVersione());
		}
		
		if(policyGroupBy.isAzione()){
			groupBy.setAzione(datiTransazione.getIdServizio().getAzione());
		}
		
		if(policyGroupBy.isInformazioneApplicativaEnabled()){
			String valorePresente = PolicyFiltroApplicativoUtilities.getValore(log, policyGroupBy.getInformazioneApplicativaTipo(), 
					policyGroupBy.getInformazioneApplicativaNome(), 
					context, datiTransazione, false);
			if(valorePresente==null){
				valorePresente = "n.d.";
			}
			groupBy.setTipoKey(policyGroupBy.getInformazioneApplicativaTipo());
			groupBy.setNomeKey(policyGroupBy.getInformazioneApplicativaNome());
			groupBy.setValoreKey(valorePresente);
		}
		
		return groupBy;
	}
	
	public static DatiTransazione readDatiTransazione(InRequestProtocolContext context){
		DatiTransazione datiTransazione = new DatiTransazione();
		
		datiTransazione.setTipoPdD(context.getTipoPorta());
		datiTransazione.setModulo(context.getIdModulo());
		
		String idTransazione = (String) context.getPddContext().getObject(Costanti.ID_TRANSAZIONE);
		datiTransazione.setIdTransazione(idTransazione);		
		
		if(context.getProtocolFactory()!=null)
			datiTransazione.setProtocollo(context.getProtocolFactory().getProtocol());
		
		// Controllo sui dati del Fruitore e del Servizio (Erogatore)
		if(context.getProtocollo()!=null){
			
			datiTransazione.setDominio(context.getProtocollo().getDominio());
			
			datiTransazione.setSoggettoFruitore(context.getProtocollo().getFruitore());
			
			IDSoggetto soggettoErogatore = context.getProtocollo().getErogatore();
			String soggettoErogatoreTipo = null;
			String soggettoErogatoreNome = null;
			if(soggettoErogatore!=null) {
				soggettoErogatoreTipo = soggettoErogatore.getTipo();
				soggettoErogatoreNome = soggettoErogatore.getNome();
			}
			IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValuesWithoutCheck(context.getProtocollo().getTipoServizio(), 
					context.getProtocollo().getServizio(),
					soggettoErogatoreTipo, soggettoErogatoreNome, 
					context.getProtocollo().getVersioneServizio()); 
			idServizio.setAzione(context.getProtocollo().getAzione());
			datiTransazione.setIdServizio(idServizio);
			
		}
		
		if(context.getIntegrazione()!=null && context.getIntegrazione().getIdPD()!=null){

			if(context.getIntegrazione().getServizioApplicativoFruitore()!=null){
				datiTransazione.setServizioApplicativoFruitore(context.getIntegrazione().getServizioApplicativoFruitore());
			}
			else{
				datiTransazione.setServizioApplicativoFruitore(CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO);
			}

		}
		else if(context.getIntegrazione()!=null && context.getIntegrazione().getIdPA()!=null){
			
			if(context.getIntegrazione().sizeServiziApplicativiErogatori()>0){
				for (int i = 0; i < context.getIntegrazione().sizeServiziApplicativiErogatori(); i++) {
					datiTransazione.getListServiziApplicativiErogatori().add(context.getIntegrazione().getServizioApplicativoErogatore(i));
				}
			}
			
		}
		
		return datiTransazione;
	}
	
	public static boolean checkRegisterThread(DatiTransazione datiTransazione){
		boolean registerThread = true;
		if(datiTransazione.getDominio()==null || 
				datiTransazione.getDominio().getTipo()==null ||
						datiTransazione.getDominio().getNome()==null || 
						datiTransazione.getDominio().getCodicePorta()==null ){
			registerThread = false; // i dati sul dominio ci devono essere
		}
		else if(datiTransazione.getModulo()==null){
			registerThread = false; // i dati sul modulo ci devono essere
		}
		else if(datiTransazione.getIdTransazione()==null){
			registerThread = false; // i dati sull'identificativo transazione ci devono essere
		}
		else if(datiTransazione.getSoggettoFruitore()==null || 
				datiTransazione.getSoggettoFruitore().getTipo()==null ||
						datiTransazione.getSoggettoFruitore().getNome()==null){
			registerThread = false; // i dati sul fruitore ci devono essere
		}
		else if(datiTransazione.getIdServizio()==null ||
				datiTransazione.getIdServizio().getSoggettoErogatore()==null || 
				datiTransazione.getIdServizio().getSoggettoErogatore().getTipo()==null ||
				datiTransazione.getIdServizio().getSoggettoErogatore().getNome()==null){
			registerThread = false; // i dati sull'erogatore ci devono essere
		}
		else if(datiTransazione.getIdServizio()==null ||
				datiTransazione.getIdServizio().getTipo()==null || datiTransazione.getIdServizio().getNome()==null || datiTransazione.getIdServizio().getVersione()==null){
			registerThread = false; // i dati sul servizio ci devono essere
		}
		else if(datiTransazione.getServizioApplicativoFruitore()==null && 
				(datiTransazione.getListServiziApplicativiErogatori()==null || datiTransazione.getListServiziApplicativiErogatori().size()<=0)){
			registerThread = false; // i dati sul servizio applicativo ci devono essere
		}
		return registerThread;
	}
	
	public static boolean filter(AttivazionePolicyFiltro filtro, DatiTransazione datiTransazione) throws Exception{
		
		if(filtro.isEnabled()){
						
			if(filtro.getProtocollo()!=null && !"".equals(filtro.getProtocollo())){
				if(filtro.getProtocollo().equals(datiTransazione.getProtocollo())==false){
					return false;
				}
			}
			
			if(filtro.getRuoloPorta()!=null && !"".equals(filtro.getRuoloPorta()) && 
					!RuoloPolicy.ENTRAMBI.equals(filtro.getRuoloPorta())){
				
				if(RuoloPolicy.DELEGATA.equals(filtro.getRuoloPorta())){
					if(TipoPdD.DELEGATA.equals(datiTransazione.getTipoPdD())==false){
						return false;
					}
				}
				if(RuoloPolicy.APPLICATIVA.equals(filtro.getRuoloPorta())){
					if(TipoPdD.APPLICATIVA.equals(datiTransazione.getTipoPdD())==false){
						return false;
					}
				}
				
			}
			
			if(filtro.getRuoloFruitore()!=null && !"".equals(filtro.getRuoloFruitore())){
				if(datiTransazione.getSoggettoFruitore()==null || 
						datiTransazione.getSoggettoFruitore().getTipo()==null || 
						datiTransazione.getSoggettoFruitore().getNome()==null){
					return false;
				}
				IDSoggetto idFruitore = new IDSoggetto(datiTransazione.getSoggettoFruitore().getTipo(), datiTransazione.getSoggettoFruitore().getNome());
				RegistroServiziManager registroManager = RegistroServiziManager.getInstance();
				Soggetto soggetto = registroManager.getSoggetto(idFruitore, null);
				boolean foundRuolo = false;
				if(soggetto.getRuoli()!=null) {
					for (int i = 0; i < soggetto.getRuoli().sizeRuoloList(); i++) {
						if(soggetto.getRuoli().getRuolo(i).getNome().equals(filtro.getRuoloFruitore())) {
							foundRuolo = true;
							break;
						}
					}
				}
				if(!foundRuolo) {
					return false;
				}
			}
			
			if(filtro.getTipoFruitore()!=null && !"".equals(filtro.getTipoFruitore())){
				if(datiTransazione.getSoggettoFruitore()==null){
					return false;
				}
				if(filtro.getTipoFruitore().equals(datiTransazione.getSoggettoFruitore().getTipo())==false){
					return false;
				}
			}
			if(filtro.getNomeFruitore()!=null && !"".equals(filtro.getNomeFruitore())){
				if(datiTransazione.getSoggettoFruitore()==null){
					return false;
				}
				if(filtro.getNomeFruitore().equals(datiTransazione.getSoggettoFruitore().getNome())==false){
					return false;
				}
			}
			
			if(filtro.getServizioApplicativoFruitore()!=null && !"".equals(filtro.getServizioApplicativoFruitore())){
				if(filtro.getServizioApplicativoFruitore().equals(datiTransazione.getServizioApplicativoFruitore())==false){
					return false;
				}
			}
			
			if(filtro.getRuoloErogatore()!=null && !"".equals(filtro.getRuoloErogatore())){
				if(datiTransazione.getIdServizio() == null ||
						datiTransazione.getIdServizio().getSoggettoErogatore()==null || 
						datiTransazione.getIdServizio().getSoggettoErogatore().getTipo()==null || 
						datiTransazione.getIdServizio().getSoggettoErogatore().getNome()==null){
					return false;
				}
				IDSoggetto idErogatore = new IDSoggetto(datiTransazione.getIdServizio().getSoggettoErogatore().getTipo(), datiTransazione.getIdServizio().getSoggettoErogatore().getNome());
				RegistroServiziManager registroManager = RegistroServiziManager.getInstance();
				Soggetto soggetto = registroManager.getSoggetto(idErogatore, null);
				boolean foundRuolo = false;
				if(soggetto.getRuoli()!=null) {
					for (int i = 0; i < soggetto.getRuoli().sizeRuoloList(); i++) {
						if(soggetto.getRuoli().getRuolo(i).getNome().equals(filtro.getRuoloErogatore())) {
							foundRuolo = true;
							break;
						}
					}
				}
				if(!foundRuolo) {
					return false;
				}
			}
			
			if(filtro.getTipoErogatore()!=null && !"".equals(filtro.getTipoErogatore())){
				if(datiTransazione.getIdServizio()==null){
					return false;
				}
				if(datiTransazione.getIdServizio().getSoggettoErogatore()==null){
					return false;
				}
				if(filtro.getTipoErogatore().equals(datiTransazione.getIdServizio().getSoggettoErogatore().getTipo())==false){
					return false;
				}
			}
			if(filtro.getNomeErogatore()!=null && !"".equals(filtro.getNomeErogatore())){
				if(datiTransazione.getIdServizio()==null){
					return false;
				}
				if(datiTransazione.getIdServizio().getSoggettoErogatore()==null){
					return false;
				}
				if(filtro.getNomeErogatore().equals(datiTransazione.getIdServizio().getSoggettoErogatore().getNome())==false){
					return false;
				}
			}
			
			if(filtro.getServizioApplicativoErogatore()!=null && !"".equals(filtro.getServizioApplicativoErogatore())){
				if(datiTransazione.getListServiziApplicativiErogatori()==null || datiTransazione.getListServiziApplicativiErogatori().size()<=0){
					return false;
				}				
				if(datiTransazione.getListServiziApplicativiErogatori().contains(filtro.getServizioApplicativoErogatore())==false){
					return false;
				}
			}
			
			if(filtro.getTipoServizio()!=null && !"".equals(filtro.getTipoServizio())){
				if(datiTransazione.getIdServizio()==null){
					return false;
				}
				if(filtro.getTipoServizio().equals(datiTransazione.getIdServizio().getTipo())==false){
					return false;
				}
			}
			if(filtro.getNomeServizio()!=null && !"".equals(filtro.getNomeServizio())){
				if(datiTransazione.getIdServizio()==null){
					return false;
				}
				if(filtro.getNomeServizio().equals(datiTransazione.getIdServizio().getNome())==false){
					return false;
				}
			}
			if(filtro.getVersioneServizio()!=null){
				if(datiTransazione.getIdServizio()==null || datiTransazione.getIdServizio().getVersione()==null){
					return false;
				}
				if(filtro.getVersioneServizio().intValue() != datiTransazione.getIdServizio().getVersione().intValue()){
					return false;
				}
			}
			
			if(filtro.getAzione()!=null && !"".equals(filtro.getAzione())){
				if(datiTransazione.getIdServizio()==null){
					return false;
				}
				if(filtro.getAzione().equals(datiTransazione.getIdServizio().getAzione())==false){
					return false;
				}
			}
						
		}
		
		return true;
		
	}
	
}
