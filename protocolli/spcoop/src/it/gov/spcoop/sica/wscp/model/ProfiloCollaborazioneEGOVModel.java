/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package it.gov.spcoop.sica.wscp.model;

import it.gov.spcoop.sica.wscp.ProfiloCollaborazioneEGOV;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ProfiloCollaborazioneEGOV 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProfiloCollaborazioneEGOVModel extends AbstractModel<ProfiloCollaborazioneEGOV> {

	public ProfiloCollaborazioneEGOVModel(){
	
		super();
	
		this.VERSIONE_EGOV = new Field("versioneEGOV",java.lang.String.class,"profiloCollaborazioneEGOV",ProfiloCollaborazioneEGOV.class);
		this.RIFERIMENTO_DEFINIZIONE_INTERFACCIA = new Field("riferimentoDefinizioneInterfaccia",java.net.URI.class,"profiloCollaborazioneEGOV",ProfiloCollaborazioneEGOV.class);
		this.LISTA_COLLABORAZIONI = new it.gov.spcoop.sica.wscp.model.OperationListTypeModel(new Field("listaCollaborazioni",it.gov.spcoop.sica.wscp.OperationListType.class,"profiloCollaborazioneEGOV",ProfiloCollaborazioneEGOV.class));
	
	}
	
	public ProfiloCollaborazioneEGOVModel(IField father){
	
		super(father);
	
		this.VERSIONE_EGOV = new ComplexField(father,"versioneEGOV",java.lang.String.class,"profiloCollaborazioneEGOV",ProfiloCollaborazioneEGOV.class);
		this.RIFERIMENTO_DEFINIZIONE_INTERFACCIA = new ComplexField(father,"riferimentoDefinizioneInterfaccia",java.net.URI.class,"profiloCollaborazioneEGOV",ProfiloCollaborazioneEGOV.class);
		this.LISTA_COLLABORAZIONI = new it.gov.spcoop.sica.wscp.model.OperationListTypeModel(new ComplexField(father,"listaCollaborazioni",it.gov.spcoop.sica.wscp.OperationListType.class,"profiloCollaborazioneEGOV",ProfiloCollaborazioneEGOV.class));
	
	}
	
	

	public IField VERSIONE_EGOV = null;
	 
	public IField RIFERIMENTO_DEFINIZIONE_INTERFACCIA = null;
	 
	public it.gov.spcoop.sica.wscp.model.OperationListTypeModel LISTA_COLLABORAZIONI = null;
	 

	@Override
	public Class<ProfiloCollaborazioneEGOV> getModeledClass(){
		return ProfiloCollaborazioneEGOV.class;
	}
	
	@Override
	public String toString(){
		if(this.getModeledClass()!=null){
			return this.getModeledClass().getName();
		}else{
			return "N.D.";
		}
	}

}