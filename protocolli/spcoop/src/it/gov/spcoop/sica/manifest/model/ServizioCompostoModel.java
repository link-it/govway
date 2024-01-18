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
package it.gov.spcoop.sica.manifest.model;

import it.gov.spcoop.sica.manifest.ServizioComposto;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ServizioComposto 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServizioCompostoModel extends AbstractModel<ServizioComposto> {

	public ServizioCompostoModel(){
	
		super();
	
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"servizioComposto",ServizioComposto.class);
		this.SPECIFICA_SEMIFORMALE = new it.gov.spcoop.sica.manifest.model.SpecificaSemiformaleModel(new Field("specificaSemiformale",it.gov.spcoop.sica.manifest.SpecificaSemiformale.class,"servizioComposto",ServizioComposto.class));
		this.ALLEGATI = new it.gov.spcoop.sica.manifest.model.ElencoAllegatiModel(new Field("allegati",it.gov.spcoop.sica.manifest.ElencoAllegati.class,"servizioComposto",ServizioComposto.class));
		this.SPECIFICA_INTERFACCIA = new it.gov.spcoop.sica.manifest.model.SpecificaInterfacciaModel(new Field("specificaInterfaccia",it.gov.spcoop.sica.manifest.SpecificaInterfaccia.class,"servizioComposto",ServizioComposto.class));
		this.SPECIFICA_CONVERSAZIONE = new it.gov.spcoop.sica.manifest.model.SpecificaConversazioneModel(new Field("specificaConversazione",it.gov.spcoop.sica.manifest.SpecificaConversazione.class,"servizioComposto",ServizioComposto.class));
		this.RIFERIMENTO_ACCORDO_COOPERAZIONE = new Field("riferimentoAccordoCooperazione",java.net.URI.class,"servizioComposto",ServizioComposto.class);
		this.SERVIZI_COMPONENTI = new it.gov.spcoop.sica.manifest.model.ElencoServiziComponentiModel(new Field("serviziComponenti",it.gov.spcoop.sica.manifest.ElencoServiziComponenti.class,"servizioComposto",ServizioComposto.class));
		this.SPECIFICA_COORDINAMENTO = new it.gov.spcoop.sica.manifest.model.SpecificaCoordinamentoModel(new Field("specificaCoordinamento",it.gov.spcoop.sica.manifest.SpecificaCoordinamento.class,"servizioComposto",ServizioComposto.class));
		this.NOME = new Field("nome",java.lang.String.class,"servizioComposto",ServizioComposto.class);
		this.VERSIONE = new Field("versione",java.lang.String.class,"servizioComposto",ServizioComposto.class);
		this.DATA_CREAZIONE = new Field("dataCreazione",java.util.Date.class,"servizioComposto",ServizioComposto.class);
		this.DATA_PUBBLICAZIONE = new Field("dataPubblicazione",java.util.Date.class,"servizioComposto",ServizioComposto.class);
		this.FIRMATO = new Field("firmato",boolean.class,"servizioComposto",ServizioComposto.class);
		this.RISERVATO = new Field("riservato",boolean.class,"servizioComposto",ServizioComposto.class);
		this.PUBBLICATORE = new Field("pubblicatore",java.net.URI.class,"servizioComposto",ServizioComposto.class);
	
	}
	
	public ServizioCompostoModel(IField father){
	
		super(father);
	
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"servizioComposto",ServizioComposto.class);
		this.SPECIFICA_SEMIFORMALE = new it.gov.spcoop.sica.manifest.model.SpecificaSemiformaleModel(new ComplexField(father,"specificaSemiformale",it.gov.spcoop.sica.manifest.SpecificaSemiformale.class,"servizioComposto",ServizioComposto.class));
		this.ALLEGATI = new it.gov.spcoop.sica.manifest.model.ElencoAllegatiModel(new ComplexField(father,"allegati",it.gov.spcoop.sica.manifest.ElencoAllegati.class,"servizioComposto",ServizioComposto.class));
		this.SPECIFICA_INTERFACCIA = new it.gov.spcoop.sica.manifest.model.SpecificaInterfacciaModel(new ComplexField(father,"specificaInterfaccia",it.gov.spcoop.sica.manifest.SpecificaInterfaccia.class,"servizioComposto",ServizioComposto.class));
		this.SPECIFICA_CONVERSAZIONE = new it.gov.spcoop.sica.manifest.model.SpecificaConversazioneModel(new ComplexField(father,"specificaConversazione",it.gov.spcoop.sica.manifest.SpecificaConversazione.class,"servizioComposto",ServizioComposto.class));
		this.RIFERIMENTO_ACCORDO_COOPERAZIONE = new ComplexField(father,"riferimentoAccordoCooperazione",java.net.URI.class,"servizioComposto",ServizioComposto.class);
		this.SERVIZI_COMPONENTI = new it.gov.spcoop.sica.manifest.model.ElencoServiziComponentiModel(new ComplexField(father,"serviziComponenti",it.gov.spcoop.sica.manifest.ElencoServiziComponenti.class,"servizioComposto",ServizioComposto.class));
		this.SPECIFICA_COORDINAMENTO = new it.gov.spcoop.sica.manifest.model.SpecificaCoordinamentoModel(new ComplexField(father,"specificaCoordinamento",it.gov.spcoop.sica.manifest.SpecificaCoordinamento.class,"servizioComposto",ServizioComposto.class));
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"servizioComposto",ServizioComposto.class);
		this.VERSIONE = new ComplexField(father,"versione",java.lang.String.class,"servizioComposto",ServizioComposto.class);
		this.DATA_CREAZIONE = new ComplexField(father,"dataCreazione",java.util.Date.class,"servizioComposto",ServizioComposto.class);
		this.DATA_PUBBLICAZIONE = new ComplexField(father,"dataPubblicazione",java.util.Date.class,"servizioComposto",ServizioComposto.class);
		this.FIRMATO = new ComplexField(father,"firmato",boolean.class,"servizioComposto",ServizioComposto.class);
		this.RISERVATO = new ComplexField(father,"riservato",boolean.class,"servizioComposto",ServizioComposto.class);
		this.PUBBLICATORE = new ComplexField(father,"pubblicatore",java.net.URI.class,"servizioComposto",ServizioComposto.class);
	
	}
	
	

	public IField DESCRIZIONE = null;
	 
	public it.gov.spcoop.sica.manifest.model.SpecificaSemiformaleModel SPECIFICA_SEMIFORMALE = null;
	 
	public it.gov.spcoop.sica.manifest.model.ElencoAllegatiModel ALLEGATI = null;
	 
	public it.gov.spcoop.sica.manifest.model.SpecificaInterfacciaModel SPECIFICA_INTERFACCIA = null;
	 
	public it.gov.spcoop.sica.manifest.model.SpecificaConversazioneModel SPECIFICA_CONVERSAZIONE = null;
	 
	public IField RIFERIMENTO_ACCORDO_COOPERAZIONE = null;
	 
	public it.gov.spcoop.sica.manifest.model.ElencoServiziComponentiModel SERVIZI_COMPONENTI = null;
	 
	public it.gov.spcoop.sica.manifest.model.SpecificaCoordinamentoModel SPECIFICA_COORDINAMENTO = null;
	 
	public IField NOME = null;
	 
	public IField VERSIONE = null;
	 
	public IField DATA_CREAZIONE = null;
	 
	public IField DATA_PUBBLICAZIONE = null;
	 
	public IField FIRMATO = null;
	 
	public IField RISERVATO = null;
	 
	public IField PUBBLICATORE = null;
	 

	@Override
	public Class<ServizioComposto> getModeledClass(){
		return ServizioComposto.class;
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