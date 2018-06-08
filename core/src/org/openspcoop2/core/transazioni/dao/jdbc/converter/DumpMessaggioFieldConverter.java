/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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
package org.openspcoop2.core.transazioni.dao.jdbc.converter;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.sql.AbstractSQLFieldConverter;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.transazioni.DumpMessaggio;


/**     
 * DumpMessaggioFieldConverter
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DumpMessaggioFieldConverter extends AbstractSQLFieldConverter {

	private TipiDatabase databaseType;
	
	public DumpMessaggioFieldConverter(String databaseType){
		this.databaseType = TipiDatabase.toEnumConstant(databaseType);
	}
	public DumpMessaggioFieldConverter(TipiDatabase databaseType){
		this.databaseType = databaseType;
	}


	@Override
	public IModel<?> getRootModel() throws ExpressionException {
		return DumpMessaggio.model();
	}
	
	@Override
	public TipiDatabase getDatabaseType() throws ExpressionException {
		return this.databaseType;
	}
	


	@Override
	public String toColumn(IField field,boolean returnAlias,boolean appendTablePrefix) throws ExpressionException {
		
		// In the case of columns with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the column containing the alias
		
		if(field.equals(DumpMessaggio.model().ID_TRANSAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".id_transazione";
			}else{
				return "id_transazione";
			}
		}
		if(field.equals(DumpMessaggio.model().PROTOCOLLO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".protocollo";
			}else{
				return "protocollo";
			}
		}
		if(field.equals(DumpMessaggio.model().TIPO_MESSAGGIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_messaggio";
			}else{
				return "tipo_messaggio";
			}
		}
		if(field.equals(DumpMessaggio.model().FORMATO_MESSAGGIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".formato_messaggio";
			}else{
				return "formato_messaggio";
			}
		}
		if(field.equals(DumpMessaggio.model().CONTENT_TYPE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".content_type";
			}else{
				return "content_type";
			}
		}
		if(field.equals(DumpMessaggio.model().MULTIPART_CONTENT_TYPE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".multipart_content_type";
			}else{
				return "multipart_content_type";
			}
		}
		if(field.equals(DumpMessaggio.model().MULTIPART_CONTENT_ID)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".multipart_content_id";
			}else{
				return "multipart_content_id";
			}
		}
		if(field.equals(DumpMessaggio.model().MULTIPART_CONTENT_LOCATION)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".multipart_content_location";
			}else{
				return "multipart_content_location";
			}
		}
		if(field.equals(DumpMessaggio.model().MULTIPART_HEADER.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(DumpMessaggio.model().MULTIPART_HEADER.VALORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".valore";
			}else{
				return "valore";
			}
		}
		if(field.equals(DumpMessaggio.model().MULTIPART_HEADER.DUMP_TIMESTAMP)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".dump_timestamp";
			}else{
				return "dump_timestamp";
			}
		}
		if(field.equals(DumpMessaggio.model().BODY)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".body";
			}else{
				return "body";
			}
		}
		if(field.equals(DumpMessaggio.model().HEADER_TRASPORTO.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(DumpMessaggio.model().HEADER_TRASPORTO.VALORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".valore";
			}else{
				return "valore";
			}
		}
		if(field.equals(DumpMessaggio.model().HEADER_TRASPORTO.DUMP_TIMESTAMP)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".dump_timestamp";
			}else{
				return "dump_timestamp";
			}
		}
		if(field.equals(DumpMessaggio.model().ALLEGATO.CONTENT_TYPE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".content_type";
			}else{
				return "content_type";
			}
		}
		if(field.equals(DumpMessaggio.model().ALLEGATO.CONTENT_ID)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".content_id";
			}else{
				return "content_id";
			}
		}
		if(field.equals(DumpMessaggio.model().ALLEGATO.CONTENT_LOCATION)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".content_location";
			}else{
				return "content_location";
			}
		}
		if(field.equals(DumpMessaggio.model().ALLEGATO.ALLEGATO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".allegato";
			}else{
				return "allegato";
			}
		}
		if(field.equals(DumpMessaggio.model().ALLEGATO.HEADER.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(DumpMessaggio.model().ALLEGATO.HEADER.VALORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".valore";
			}else{
				return "valore";
			}
		}
		if(field.equals(DumpMessaggio.model().ALLEGATO.HEADER.DUMP_TIMESTAMP)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".dump_timestamp";
			}else{
				return "dump_timestamp";
			}
		}
		if(field.equals(DumpMessaggio.model().ALLEGATO.DUMP_TIMESTAMP)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".dump_timestamp";
			}else{
				return "dump_timestamp";
			}
		}
		if(field.equals(DumpMessaggio.model().CONTENUTO.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(DumpMessaggio.model().CONTENUTO.VALORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".valore";
			}else{
				return "valore";
			}
		}
		if(field.equals(DumpMessaggio.model().CONTENUTO.VALORE_AS_BYTES)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".valore_as_bytes";
			}else{
				return "valore_as_bytes";
			}
		}
		if(field.equals(DumpMessaggio.model().CONTENUTO.DUMP_TIMESTAMP)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".dump_timestamp";
			}else{
				return "dump_timestamp";
			}
		}
		if(field.equals(DumpMessaggio.model().DUMP_TIMESTAMP)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".dump_timestamp";
			}else{
				return "dump_timestamp";
			}
		}
		if(field.equals(DumpMessaggio.model().POST_PROCESS_HEADER)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".post_process_header";
			}else{
				return "post_process_header";
			}
		}
		if(field.equals(DumpMessaggio.model().POST_PROCESS_FILENAME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".post_process_filename";
			}else{
				return "post_process_filename";
			}
		}
		if(field.equals(DumpMessaggio.model().POST_PROCESS_CONTENT)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".post_process_content";
			}else{
				return "post_process_content";
			}
		}
		if(field.equals(DumpMessaggio.model().POST_PROCESS_CONFIG_ID)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".post_process_config_id";
			}else{
				return "post_process_config_id";
			}
		}
		if(field.equals(DumpMessaggio.model().POST_PROCESS_TIMESTAMP)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".post_process_timestamp";
			}else{
				return "post_process_timestamp";
			}
		}
		if(field.equals(DumpMessaggio.model().POST_PROCESSED)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".post_processed";
			}else{
				return "post_processed";
			}
		}


		return super.toColumn(field,returnAlias,appendTablePrefix);
		
	}
	
	@Override
	public String toTable(IField field,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(field.equals(DumpMessaggio.model().ID_TRANSAZIONE)){
			return this.toTable(DumpMessaggio.model(), returnAlias);
		}
		if(field.equals(DumpMessaggio.model().PROTOCOLLO)){
			return this.toTable(DumpMessaggio.model(), returnAlias);
		}
		if(field.equals(DumpMessaggio.model().TIPO_MESSAGGIO)){
			return this.toTable(DumpMessaggio.model(), returnAlias);
		}
		if(field.equals(DumpMessaggio.model().FORMATO_MESSAGGIO)){
			return this.toTable(DumpMessaggio.model(), returnAlias);
		}
		if(field.equals(DumpMessaggio.model().CONTENT_TYPE)){
			return this.toTable(DumpMessaggio.model(), returnAlias);
		}
		if(field.equals(DumpMessaggio.model().MULTIPART_CONTENT_TYPE)){
			return this.toTable(DumpMessaggio.model(), returnAlias);
		}
		if(field.equals(DumpMessaggio.model().MULTIPART_CONTENT_ID)){
			return this.toTable(DumpMessaggio.model(), returnAlias);
		}
		if(field.equals(DumpMessaggio.model().MULTIPART_CONTENT_LOCATION)){
			return this.toTable(DumpMessaggio.model(), returnAlias);
		}
		if(field.equals(DumpMessaggio.model().MULTIPART_HEADER.NOME)){
			return this.toTable(DumpMessaggio.model().MULTIPART_HEADER, returnAlias);
		}
		if(field.equals(DumpMessaggio.model().MULTIPART_HEADER.VALORE)){
			return this.toTable(DumpMessaggio.model().MULTIPART_HEADER, returnAlias);
		}
		if(field.equals(DumpMessaggio.model().MULTIPART_HEADER.DUMP_TIMESTAMP)){
			return this.toTable(DumpMessaggio.model().MULTIPART_HEADER, returnAlias);
		}
		if(field.equals(DumpMessaggio.model().BODY)){
			return this.toTable(DumpMessaggio.model(), returnAlias);
		}
		if(field.equals(DumpMessaggio.model().HEADER_TRASPORTO.NOME)){
			return this.toTable(DumpMessaggio.model().HEADER_TRASPORTO, returnAlias);
		}
		if(field.equals(DumpMessaggio.model().HEADER_TRASPORTO.VALORE)){
			return this.toTable(DumpMessaggio.model().HEADER_TRASPORTO, returnAlias);
		}
		if(field.equals(DumpMessaggio.model().HEADER_TRASPORTO.DUMP_TIMESTAMP)){
			return this.toTable(DumpMessaggio.model().HEADER_TRASPORTO, returnAlias);
		}
		if(field.equals(DumpMessaggio.model().ALLEGATO.CONTENT_TYPE)){
			return this.toTable(DumpMessaggio.model().ALLEGATO, returnAlias);
		}
		if(field.equals(DumpMessaggio.model().ALLEGATO.CONTENT_ID)){
			return this.toTable(DumpMessaggio.model().ALLEGATO, returnAlias);
		}
		if(field.equals(DumpMessaggio.model().ALLEGATO.CONTENT_LOCATION)){
			return this.toTable(DumpMessaggio.model().ALLEGATO, returnAlias);
		}
		if(field.equals(DumpMessaggio.model().ALLEGATO.ALLEGATO)){
			return this.toTable(DumpMessaggio.model().ALLEGATO, returnAlias);
		}
		if(field.equals(DumpMessaggio.model().ALLEGATO.HEADER.NOME)){
			return this.toTable(DumpMessaggio.model().ALLEGATO.HEADER, returnAlias);
		}
		if(field.equals(DumpMessaggio.model().ALLEGATO.HEADER.VALORE)){
			return this.toTable(DumpMessaggio.model().ALLEGATO.HEADER, returnAlias);
		}
		if(field.equals(DumpMessaggio.model().ALLEGATO.HEADER.DUMP_TIMESTAMP)){
			return this.toTable(DumpMessaggio.model().ALLEGATO.HEADER, returnAlias);
		}
		if(field.equals(DumpMessaggio.model().ALLEGATO.DUMP_TIMESTAMP)){
			return this.toTable(DumpMessaggio.model().ALLEGATO, returnAlias);
		}
		if(field.equals(DumpMessaggio.model().CONTENUTO.NOME)){
			return this.toTable(DumpMessaggio.model().CONTENUTO, returnAlias);
		}
		if(field.equals(DumpMessaggio.model().CONTENUTO.VALORE)){
			return this.toTable(DumpMessaggio.model().CONTENUTO, returnAlias);
		}
		if(field.equals(DumpMessaggio.model().CONTENUTO.VALORE_AS_BYTES)){
			return this.toTable(DumpMessaggio.model().CONTENUTO, returnAlias);
		}
		if(field.equals(DumpMessaggio.model().CONTENUTO.DUMP_TIMESTAMP)){
			return this.toTable(DumpMessaggio.model().CONTENUTO, returnAlias);
		}
		if(field.equals(DumpMessaggio.model().DUMP_TIMESTAMP)){
			return this.toTable(DumpMessaggio.model(), returnAlias);
		}
		if(field.equals(DumpMessaggio.model().POST_PROCESS_HEADER)){
			return this.toTable(DumpMessaggio.model(), returnAlias);
		}
		if(field.equals(DumpMessaggio.model().POST_PROCESS_FILENAME)){
			return this.toTable(DumpMessaggio.model(), returnAlias);
		}
		if(field.equals(DumpMessaggio.model().POST_PROCESS_CONTENT)){
			return this.toTable(DumpMessaggio.model(), returnAlias);
		}
		if(field.equals(DumpMessaggio.model().POST_PROCESS_CONFIG_ID)){
			return this.toTable(DumpMessaggio.model(), returnAlias);
		}
		if(field.equals(DumpMessaggio.model().POST_PROCESS_TIMESTAMP)){
			return this.toTable(DumpMessaggio.model(), returnAlias);
		}
		if(field.equals(DumpMessaggio.model().POST_PROCESSED)){
			return this.toTable(DumpMessaggio.model(), returnAlias);
		}


		return super.toTable(field,returnAlias);
		
	}

	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(model.equals(DumpMessaggio.model())){
			return CostantiDB.DUMP_MESSAGGI;
		}
		if(model.equals(DumpMessaggio.model().MULTIPART_HEADER)){
			return CostantiDB.DUMP_MULTIPART_HEADER;
		}
		if(model.equals(DumpMessaggio.model().HEADER_TRASPORTO)){
			return CostantiDB.DUMP_HEADER_TRASPORTO;
		}
		if(model.equals(DumpMessaggio.model().ALLEGATO)){
			return CostantiDB.DUMP_ALLEGATI;
		}
		if(model.equals(DumpMessaggio.model().ALLEGATO.HEADER)){
			return CostantiDB.DUMP_ALLEGATI_HEADER;
		}
		if(model.equals(DumpMessaggio.model().CONTENUTO)){
			return CostantiDB.DUMP_CONTENUTI;
		}


		return super.toTable(model,returnAlias);
		
	}

}
