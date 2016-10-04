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

import it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaBodyType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model FatturaElettronicaBodyType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FatturaElettronicaBodyTypeModel extends AbstractModel<FatturaElettronicaBodyType> {

	public FatturaElettronicaBodyTypeModel(){
	
		super();
	
		this.DATI_GENERALI = new it.gov.fatturapa.sdi.fatturapa.v1_1.model.DatiGeneraliTypeModel(new Field("DatiGenerali",it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliType.class,"FatturaElettronicaBodyType",FatturaElettronicaBodyType.class));
		this.DATI_BENI_SERVIZI = new it.gov.fatturapa.sdi.fatturapa.v1_1.model.DatiBeniServiziTypeModel(new Field("DatiBeniServizi",it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBeniServiziType.class,"FatturaElettronicaBodyType",FatturaElettronicaBodyType.class));
		this.DATI_VEICOLI = new it.gov.fatturapa.sdi.fatturapa.v1_1.model.DatiVeicoliTypeModel(new Field("DatiVeicoli",it.gov.fatturapa.sdi.fatturapa.v1_1.DatiVeicoliType.class,"FatturaElettronicaBodyType",FatturaElettronicaBodyType.class));
		this.DATI_PAGAMENTO = new it.gov.fatturapa.sdi.fatturapa.v1_1.model.DatiPagamentoTypeModel(new Field("DatiPagamento",it.gov.fatturapa.sdi.fatturapa.v1_1.DatiPagamentoType.class,"FatturaElettronicaBodyType",FatturaElettronicaBodyType.class));
		this.ALLEGATI = new it.gov.fatturapa.sdi.fatturapa.v1_1.model.AllegatiTypeModel(new Field("Allegati",it.gov.fatturapa.sdi.fatturapa.v1_1.AllegatiType.class,"FatturaElettronicaBodyType",FatturaElettronicaBodyType.class));
	
	}
	
	public FatturaElettronicaBodyTypeModel(IField father){
	
		super(father);
	
		this.DATI_GENERALI = new it.gov.fatturapa.sdi.fatturapa.v1_1.model.DatiGeneraliTypeModel(new ComplexField(father,"DatiGenerali",it.gov.fatturapa.sdi.fatturapa.v1_1.DatiGeneraliType.class,"FatturaElettronicaBodyType",FatturaElettronicaBodyType.class));
		this.DATI_BENI_SERVIZI = new it.gov.fatturapa.sdi.fatturapa.v1_1.model.DatiBeniServiziTypeModel(new ComplexField(father,"DatiBeniServizi",it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBeniServiziType.class,"FatturaElettronicaBodyType",FatturaElettronicaBodyType.class));
		this.DATI_VEICOLI = new it.gov.fatturapa.sdi.fatturapa.v1_1.model.DatiVeicoliTypeModel(new ComplexField(father,"DatiVeicoli",it.gov.fatturapa.sdi.fatturapa.v1_1.DatiVeicoliType.class,"FatturaElettronicaBodyType",FatturaElettronicaBodyType.class));
		this.DATI_PAGAMENTO = new it.gov.fatturapa.sdi.fatturapa.v1_1.model.DatiPagamentoTypeModel(new ComplexField(father,"DatiPagamento",it.gov.fatturapa.sdi.fatturapa.v1_1.DatiPagamentoType.class,"FatturaElettronicaBodyType",FatturaElettronicaBodyType.class));
		this.ALLEGATI = new it.gov.fatturapa.sdi.fatturapa.v1_1.model.AllegatiTypeModel(new ComplexField(father,"Allegati",it.gov.fatturapa.sdi.fatturapa.v1_1.AllegatiType.class,"FatturaElettronicaBodyType",FatturaElettronicaBodyType.class));
	
	}
	
	

	public it.gov.fatturapa.sdi.fatturapa.v1_1.model.DatiGeneraliTypeModel DATI_GENERALI = null;
	 
	public it.gov.fatturapa.sdi.fatturapa.v1_1.model.DatiBeniServiziTypeModel DATI_BENI_SERVIZI = null;
	 
	public it.gov.fatturapa.sdi.fatturapa.v1_1.model.DatiVeicoliTypeModel DATI_VEICOLI = null;
	 
	public it.gov.fatturapa.sdi.fatturapa.v1_1.model.DatiPagamentoTypeModel DATI_PAGAMENTO = null;
	 
	public it.gov.fatturapa.sdi.fatturapa.v1_1.model.AllegatiTypeModel ALLEGATI = null;
	 

	@Override
	public Class<FatturaElettronicaBodyType> getModeledClass(){
		return FatturaElettronicaBodyType.class;
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