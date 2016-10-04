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
package it.gov.fatturapa.sdi.fatturapa.v1_1.model;

import it.gov.fatturapa.sdi.fatturapa.v1_1.DatiCassaPrevidenzialeType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DatiCassaPrevidenzialeType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiCassaPrevidenzialeTypeModel extends AbstractModel<DatiCassaPrevidenzialeType> {

	public DatiCassaPrevidenzialeTypeModel(){
	
		super();
	
		this.TIPO_CASSA = new Field("TipoCassa",java.lang.String.class,"DatiCassaPrevidenzialeType",DatiCassaPrevidenzialeType.class);
		this.AL_CASSA = new Field("AlCassa",java.lang.Double.class,"DatiCassaPrevidenzialeType",DatiCassaPrevidenzialeType.class);
		this.IMPORTO_CONTRIBUTO_CASSA = new Field("ImportoContributoCassa",java.lang.Double.class,"DatiCassaPrevidenzialeType",DatiCassaPrevidenzialeType.class);
		this.IMPONIBILE_CASSA = new Field("ImponibileCassa",java.lang.Double.class,"DatiCassaPrevidenzialeType",DatiCassaPrevidenzialeType.class);
		this.ALIQUOTA_IVA = new Field("AliquotaIVA",java.lang.Double.class,"DatiCassaPrevidenzialeType",DatiCassaPrevidenzialeType.class);
		this.RITENUTA = new Field("Ritenuta",java.lang.String.class,"DatiCassaPrevidenzialeType",DatiCassaPrevidenzialeType.class);
		this.NATURA = new Field("Natura",java.lang.String.class,"DatiCassaPrevidenzialeType",DatiCassaPrevidenzialeType.class);
		this.RIFERIMENTO_AMMINISTRAZIONE = new Field("RiferimentoAmministrazione",java.lang.String.class,"DatiCassaPrevidenzialeType",DatiCassaPrevidenzialeType.class);
	
	}
	
	public DatiCassaPrevidenzialeTypeModel(IField father){
	
		super(father);
	
		this.TIPO_CASSA = new ComplexField(father,"TipoCassa",java.lang.String.class,"DatiCassaPrevidenzialeType",DatiCassaPrevidenzialeType.class);
		this.AL_CASSA = new ComplexField(father,"AlCassa",java.lang.Double.class,"DatiCassaPrevidenzialeType",DatiCassaPrevidenzialeType.class);
		this.IMPORTO_CONTRIBUTO_CASSA = new ComplexField(father,"ImportoContributoCassa",java.lang.Double.class,"DatiCassaPrevidenzialeType",DatiCassaPrevidenzialeType.class);
		this.IMPONIBILE_CASSA = new ComplexField(father,"ImponibileCassa",java.lang.Double.class,"DatiCassaPrevidenzialeType",DatiCassaPrevidenzialeType.class);
		this.ALIQUOTA_IVA = new ComplexField(father,"AliquotaIVA",java.lang.Double.class,"DatiCassaPrevidenzialeType",DatiCassaPrevidenzialeType.class);
		this.RITENUTA = new ComplexField(father,"Ritenuta",java.lang.String.class,"DatiCassaPrevidenzialeType",DatiCassaPrevidenzialeType.class);
		this.NATURA = new ComplexField(father,"Natura",java.lang.String.class,"DatiCassaPrevidenzialeType",DatiCassaPrevidenzialeType.class);
		this.RIFERIMENTO_AMMINISTRAZIONE = new ComplexField(father,"RiferimentoAmministrazione",java.lang.String.class,"DatiCassaPrevidenzialeType",DatiCassaPrevidenzialeType.class);
	
	}
	
	

	public IField TIPO_CASSA = null;
	 
	public IField AL_CASSA = null;
	 
	public IField IMPORTO_CONTRIBUTO_CASSA = null;
	 
	public IField IMPONIBILE_CASSA = null;
	 
	public IField ALIQUOTA_IVA = null;
	 
	public IField RITENUTA = null;
	 
	public IField NATURA = null;
	 
	public IField RIFERIMENTO_AMMINISTRAZIONE = null;
	 

	@Override
	public Class<DatiCassaPrevidenzialeType> getModeledClass(){
		return DatiCassaPrevidenzialeType.class;
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