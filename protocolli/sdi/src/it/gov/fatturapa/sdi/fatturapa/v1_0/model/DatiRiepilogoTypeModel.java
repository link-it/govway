/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package it.gov.fatturapa.sdi.fatturapa.v1_0.model;

import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiRiepilogoType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DatiRiepilogoType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiRiepilogoTypeModel extends AbstractModel<DatiRiepilogoType> {

	public DatiRiepilogoTypeModel(){
	
		super();
	
		this.ALIQUOTA_IVA = new Field("AliquotaIVA",java.math.BigDecimal.class,"DatiRiepilogoType",DatiRiepilogoType.class);
		this.NATURA = new Field("Natura",java.lang.String.class,"DatiRiepilogoType",DatiRiepilogoType.class);
		this.SPESE_ACCESSORIE = new Field("SpeseAccessorie",java.math.BigDecimal.class,"DatiRiepilogoType",DatiRiepilogoType.class);
		this.ARROTONDAMENTO = new Field("Arrotondamento",java.math.BigDecimal.class,"DatiRiepilogoType",DatiRiepilogoType.class);
		this.IMPONIBILE_IMPORTO = new Field("ImponibileImporto",java.math.BigDecimal.class,"DatiRiepilogoType",DatiRiepilogoType.class);
		this.IMPOSTA = new Field("Imposta",java.math.BigDecimal.class,"DatiRiepilogoType",DatiRiepilogoType.class);
		this.ESIGIBILITA_IVA = new Field("EsigibilitaIVA",java.lang.String.class,"DatiRiepilogoType",DatiRiepilogoType.class);
		this.RIFERIMENTO_NORMATIVO = new Field("RiferimentoNormativo",java.lang.String.class,"DatiRiepilogoType",DatiRiepilogoType.class);
	
	}
	
	public DatiRiepilogoTypeModel(IField father){
	
		super(father);
	
		this.ALIQUOTA_IVA = new ComplexField(father,"AliquotaIVA",java.math.BigDecimal.class,"DatiRiepilogoType",DatiRiepilogoType.class);
		this.NATURA = new ComplexField(father,"Natura",java.lang.String.class,"DatiRiepilogoType",DatiRiepilogoType.class);
		this.SPESE_ACCESSORIE = new ComplexField(father,"SpeseAccessorie",java.math.BigDecimal.class,"DatiRiepilogoType",DatiRiepilogoType.class);
		this.ARROTONDAMENTO = new ComplexField(father,"Arrotondamento",java.math.BigDecimal.class,"DatiRiepilogoType",DatiRiepilogoType.class);
		this.IMPONIBILE_IMPORTO = new ComplexField(father,"ImponibileImporto",java.math.BigDecimal.class,"DatiRiepilogoType",DatiRiepilogoType.class);
		this.IMPOSTA = new ComplexField(father,"Imposta",java.math.BigDecimal.class,"DatiRiepilogoType",DatiRiepilogoType.class);
		this.ESIGIBILITA_IVA = new ComplexField(father,"EsigibilitaIVA",java.lang.String.class,"DatiRiepilogoType",DatiRiepilogoType.class);
		this.RIFERIMENTO_NORMATIVO = new ComplexField(father,"RiferimentoNormativo",java.lang.String.class,"DatiRiepilogoType",DatiRiepilogoType.class);
	
	}
	
	

	public IField ALIQUOTA_IVA = null;
	 
	public IField NATURA = null;
	 
	public IField SPESE_ACCESSORIE = null;
	 
	public IField ARROTONDAMENTO = null;
	 
	public IField IMPONIBILE_IMPORTO = null;
	 
	public IField IMPOSTA = null;
	 
	public IField ESIGIBILITA_IVA = null;
	 
	public IField RIFERIMENTO_NORMATIVO = null;
	 

	@Override
	public Class<DatiRiepilogoType> getModeledClass(){
		return DatiRiepilogoType.class;
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