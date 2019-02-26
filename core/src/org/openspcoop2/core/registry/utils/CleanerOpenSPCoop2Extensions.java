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

package org.openspcoop2.core.registry.utils;

import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.ResourceResponse;
import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.core.registry.constants.BindingUse;

/**
 * CleanerOpenSPCoop2Extensions
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CleanerOpenSPCoop2Extensions {

	public void clean(PortaDominio portaDominio){

		portaDominio.setSuperUser(null);
		
	}
	
	public void clean(Ruolo ruolo){

		ruolo.setSuperUser(null);
		
	}
	
	public void clean(Scope scope){

		scope.setSuperUser(null);
		
	}
		
	public void clean(org.openspcoop2.core.registry.Soggetto soggettoRegistro){
		
		soggettoRegistro.setSuperUser(null);
		soggettoRegistro.setPrivato(null);
		
		if(soggettoRegistro.getConnettore()!=null){
			this.clean(soggettoRegistro.getConnettore());
		}
		
		if(soggettoRegistro.sizeProtocolPropertyList()>0) {
			for (ProtocolProperty pp : soggettoRegistro.getProtocolPropertyList()) {
				this.clean(pp);
			}
		}
	}

	public void clean(AccordoServizioParteComune accordo){

		accordo.setSuperUser(null);
		accordo.setStatoPackage(null);
		accordo.setPrivato(null);
		accordo.setByteWsdlDefinitorio(null);
		accordo.setByteWsdlConcettuale(null);
		accordo.setByteWsdlLogicoErogatore(null);
		accordo.setByteWsdlLogicoFruitore(null);
		accordo.setByteSpecificaConversazioneConcettuale(null);
		accordo.setByteSpecificaConversazioneErogatore(null);
		accordo.setByteSpecificaConversazioneFruitore(null);
		
		if(accordo.getServizioComposto()!=null){
			accordo.getServizioComposto().setIdAccordoCooperazione(null);
			
			if(accordo.getServizioComposto().sizeServizioComponenteList()>0){
				for (AccordoServizioParteComuneServizioCompostoServizioComponente servizioComponente : accordo.getServizioComposto().getServizioComponenteList()) {
					servizioComponente.setIdServizioComponente(null);
				}
			}
			
			if(accordo.getServizioComposto().sizeSpecificaCoordinamentoList()>0){
				for (Documento documento : accordo.getServizioComposto().getSpecificaCoordinamentoList()) {
					this.clean(documento);
				}
			}
		}
		
		if(accordo.sizePortTypeList()>0){
			for (PortType portType : accordo.getPortTypeList()) {
				portType.setProfiloPT(null);
				portType.setIdAccordo(null);
				
				if(portType.sizeAzioneList()>0){
					for (Operation operation : portType.getAzioneList()) {
						operation.setProfAzione(null);
						operation.setIdPortType(null);
						if(operation.getMessageInput()!=null){
							if(operation.getMessageInput().sizePartList()<=0){
								if(operation.getMessageInput().getSoapNamespace()==null && 
										(operation.getMessageInput().getUse()==null || BindingUse.LITERAL.equals(operation.getMessageInput().getUse())) // il default non viene serializzato
									){
									operation.setMessageInput(null);
								}
							}
						}
						if(operation.getMessageOutput()!=null){
							if(operation.getMessageOutput().sizePartList()<=0){
								if(operation.getMessageOutput().getSoapNamespace()==null && 
										(operation.getMessageOutput().getUse()==null || BindingUse.LITERAL.equals(operation.getMessageOutput().getUse())) // il default non viene serializzato
									){
									operation.setMessageOutput(null);
								}
							}
						}
						
						if(operation.sizeProtocolPropertyList()>0) {
							for (ProtocolProperty pp : operation.getProtocolPropertyList()) {
								this.clean(pp);
							}
						}
					}
				}
				
				if(portType.sizeProtocolPropertyList()>0) {
					for (ProtocolProperty pp : portType.getProtocolPropertyList()) {
						this.clean(pp);
					}
				}
			}
		}
		
		if(accordo.sizeAzioneList()>0){
			for (Azione azione : accordo.getAzioneList()) {
				azione.setProfAzione(null);
				azione.setIdAccordo(null);
				
				if(azione.sizeProtocolPropertyList()>0) {
					for (ProtocolProperty pp : azione.getProtocolPropertyList()) {
						this.clean(pp);
					}
				}
			}
		}
		
		if(accordo.sizeResourceList()>0){
			for (Resource resource : accordo.getResourceList()) {
				resource.setProfAzione(null);
				resource.setIdAccordo(null);
				
				if(resource.getRequest()!=null) {
					resource.getRequest().setIdResource(null);
				}
				
				if(resource.sizeResponseList()>0) {
					for (ResourceResponse rr : resource.getResponseList()) {
						rr.setIdResource(null);
					}
				}
				
				if(resource.sizeProtocolPropertyList()>0) {
					for (ProtocolProperty pp : resource.getProtocolPropertyList()) {
						this.clean(pp);
					}
				}
			}
		}
		
		if(accordo.sizeAllegatoList()>0){
			for (Documento documento : accordo.getAllegatoList()) {
				this.clean(documento);
			}
		}
		if(accordo.sizeSpecificaSemiformaleList()>0){
			for (Documento documento : accordo.getSpecificaSemiformaleList()) {
				this.clean(documento);
			}
		}
		
		if(accordo.sizeProtocolPropertyList()>0) {
			for (ProtocolProperty pp : accordo.getProtocolPropertyList()) {
				this.clean(pp);
			}
		}
	}

	public void clean(AccordoServizioParteSpecifica accordo){
		
		accordo.setSuperUser(null);
		accordo.setStatoPackage(null);
		accordo.setPrivato(null);
		accordo.setIdAccordo(null);
		accordo.setIdSoggetto(null);
		accordo.setByteWsdlImplementativoErogatore(null);
		accordo.setByteWsdlImplementativoFruitore(null);
		
		accordo.setTipoSoggettoErogatore(null);
		accordo.setNomeSoggettoErogatore(null);
		
		if(accordo.getConfigurazioneServizio()!=null){
						
			if(accordo.getConfigurazioneServizio().getConnettore()!=null){
				this.clean(accordo.getConfigurazioneServizio().getConnettore());
			}
			
			if(accordo.getConfigurazioneServizio().sizeConfigurazioneAzioneList()>0){
				for (ConfigurazioneServizioAzione servizioAzione : accordo.getConfigurazioneServizio().getConfigurazioneAzioneList()) {
					if(servizioAzione.getConnettore()!=null){
						this.clean(servizioAzione.getConnettore());
					}
				}
			}
			
		}
		
		if(accordo.sizeFruitoreList()>0){
			for (Fruitore fruitore : accordo.getFruitoreList()) {
				this.clean(fruitore);
				
				if(fruitore.sizeConfigurazioneAzioneList()>0){
					for (ConfigurazioneServizioAzione servizioAzione : fruitore.getConfigurazioneAzioneList()) {
						if(servizioAzione.getConnettore()!=null){
							this.clean(servizioAzione.getConnettore());
						}
					}
				}
			}
		}
		
		if(accordo.sizeAllegatoList()>0){
			for (Documento documento : accordo.getAllegatoList()) {
				this.clean(documento);
			}
		}
		if(accordo.sizeSpecificaSemiformaleList()>0){
			for (Documento documento : accordo.getSpecificaSemiformaleList()) {
				this.clean(documento);
			}
		}
		if(accordo.sizeSpecificaLivelloServizioList()>0){
			for (Documento documento : accordo.getSpecificaLivelloServizioList()) {
				this.clean(documento);
			}
		}
		if(accordo.sizeSpecificaSicurezzaList()>0){
			for (Documento documento : accordo.getSpecificaSicurezzaList()) {
				this.clean(documento);
			}
		}
		
		if(accordo.sizeProtocolPropertyList()>0) {
			for (ProtocolProperty pp : accordo.getProtocolPropertyList()) {
				this.clean(pp);
			}
		}
	}
	
	public void clean(AccordoCooperazione accordo){

		accordo.setSuperUser(null);
		accordo.setStatoPackage(null);
		accordo.setPrivato(null);
		
		while(accordo.sizeUriServiziCompostiList()>0){
			accordo.removeUriServiziComposti(0);
		}
		
		if(accordo.sizeAllegatoList()>0){
			for (Documento documento : accordo.getAllegatoList()) {
				this.clean(documento);
			}
		}
		if(accordo.sizeSpecificaSemiformaleList()>0){
			for (Documento documento : accordo.getSpecificaSemiformaleList()) {
				this.clean(documento);
			}
		}
		
		if(accordo.getElencoPartecipanti()!=null){
			if(accordo.getElencoPartecipanti().sizeSoggettoPartecipanteList()>0){
				for (IdSoggetto idSoggetto : accordo.getElencoPartecipanti().getSoggettoPartecipanteList()) {
					if(idSoggetto.getIdSoggetto()!=null){
						idSoggetto.setIdSoggetto(null);
					}
				}	
			}	
		}
		
		if(accordo.sizeProtocolPropertyList()>0) {
			for (ProtocolProperty pp : accordo.getProtocolPropertyList()) {
				this.clean(pp);
			}
		}
		
	}
	
	public void clean(Fruitore fruitore){
		
		fruitore.setStatoPackage(null);
		fruitore.setByteWsdlImplementativoErogatore(null);
		fruitore.setByteWsdlImplementativoFruitore(null);
		fruitore.setIdSoggetto(null);
		fruitore.setIdServizio(null);
		
		if(fruitore.getConnettore()!=null){
			this.clean(fruitore.getConnettore());
		}
		
		if(fruitore.sizeConfigurazioneAzioneList()>0) {
			for (ConfigurazioneServizioAzione configServizioAzione : fruitore.getConfigurazioneAzioneList()) {
				if(configServizioAzione.getConnettore()!=null) {
					this.clean(configServizioAzione.getConnettore());
				}
			}
		}
		
		while(fruitore.sizeServizioApplicativoList()>0){
			fruitore.removeServizioApplicativo(0);
		}
		
		if(fruitore.sizeProtocolPropertyList()>0) {
			for (ProtocolProperty pp : fruitore.getProtocolPropertyList()) {
				this.clean(pp);
			}
		}
		
	}

	
	// ---- UTILITY
	
	private void clean(org.openspcoop2.core.registry.Connettore connettoreRegistry){
		connettoreRegistry.setCustom(null);
	}
	
	private void clean(Documento documento){
		documento.setByteContenuto(null);
		documento.setRuolo(null);
		documento.setTipoProprietarioDocumento(null);
		documento.setIdProprietarioDocumento(null);
	}
	
	private void clean(ProtocolProperty pp){
		pp.setByteFile(null);
		pp.setIdProprietario(null);
		pp.setTipoProprietario(null);
	}
}
