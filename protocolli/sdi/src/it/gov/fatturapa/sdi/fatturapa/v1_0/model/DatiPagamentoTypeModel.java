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
package it.gov.fatturapa.sdi.fatturapa.v1_0.model;

import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiPagamentoType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DatiPagamentoType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiPagamentoTypeModel extends AbstractModel<DatiPagamentoType> {

	public DatiPagamentoTypeModel(){
	
		super();
	
		this.CONDIZIONI_PAGAMENTO = new Field("CondizioniPagamento",java.lang.String.class,"DatiPagamentoType",DatiPagamentoType.class);
		this.DETTAGLIO_PAGAMENTO = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.DettaglioPagamentoTypeModel(new Field("DettaglioPagamento",it.gov.fatturapa.sdi.fatturapa.v1_0.DettaglioPagamentoType.class,"DatiPagamentoType",DatiPagamentoType.class));
	
	}
	
	public DatiPagamentoTypeModel(IField father){
	
		super(father);
	
		this.CONDIZIONI_PAGAMENTO = new ComplexField(father,"CondizioniPagamento",java.lang.String.class,"DatiPagamentoType",DatiPagamentoType.class);
		this.DETTAGLIO_PAGAMENTO = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.DettaglioPagamentoTypeModel(new ComplexField(father,"DettaglioPagamento",it.gov.fatturapa.sdi.fatturapa.v1_0.DettaglioPagamentoType.class,"DatiPagamentoType",DatiPagamentoType.class));
	
	}
	
	

	public IField CONDIZIONI_PAGAMENTO = null;
	 
	public it.gov.fatturapa.sdi.fatturapa.v1_0.model.DettaglioPagamentoTypeModel DETTAGLIO_PAGAMENTO = null;
	 

	@Override
	public Class<DatiPagamentoType> getModeledClass(){
		return DatiPagamentoType.class;
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