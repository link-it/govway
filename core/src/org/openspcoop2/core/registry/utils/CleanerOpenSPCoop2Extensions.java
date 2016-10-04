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

package org.openspcoop2.core.registry.utils;

import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.ServizioAzione;
import org.openspcoop2.core.registry.ServizioAzioneFruitore;
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
		
	public void clean(org.openspcoop2.core.registry.Soggetto soggettoRegistro){
		
		soggettoRegistro.setSuperUser(null);
		soggettoRegistro.setPrivato(null);
		
		if(soggettoRegistro.getConnettore()!=null){
			this.clean(soggettoRegistro.getConnettore());
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
					}
				}
			}
		}
		
		if(accordo.sizeAzioneList()>0){
			for (Azione azione : accordo.getAzioneList()) {
				azione.setProfAzione(null);
				azione.setIdAccordo(null);
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
		
	}

	public void clean(AccordoServizioParteSpecifica accordo){
		
		accordo.setSuperUser(null);
		accordo.setStatoPackage(null);
		accordo.setPrivato(null);
		accordo.setIdAccordo(null);
		accordo.setIdSoggetto(null);
		accordo.setByteWsdlImplementativoErogatore(null);
		accordo.setByteWsdlImplementativoFruitore(null);
		
		if(accordo.getServizio()!=null){
			
			accordo.getServizio().setTipoSoggettoErogatore(null);
			accordo.getServizio().setNomeSoggettoErogatore(null);
			
			if(accordo.getServizio().getConnettore()!=null){
				this.clean(accordo.getServizio().getConnettore());
			}
			
			if(accordo.getServizio().sizeParametriAzioneList()>0){
				for (ServizioAzione servizioAzione : accordo.getServizio().getParametriAzioneList()) {
					if(servizioAzione.getConnettore()!=null){
						this.clean(servizioAzione.getConnettore());
					}
					if(servizioAzione.sizeParametriFruitoreList()>0){
						for (ServizioAzioneFruitore servizioAzioneFruitore : servizioAzione.getParametriFruitoreList()) {
							if(servizioAzioneFruitore.getConnettore()!=null){
								this.clean(servizioAzioneFruitore.getConnettore());
							}
						}
					}
				}
			}
			
		}
		
		if(accordo.sizeFruitoreList()>0){
			for (Fruitore fruitore : accordo.getFruitoreList()) {
				this.clean(fruitore);
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
		
		while(fruitore.sizeServizioApplicativoList()>0){
			fruitore.removeServizioApplicativo(0);
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
}
