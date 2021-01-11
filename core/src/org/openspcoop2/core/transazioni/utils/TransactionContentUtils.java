/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

package org.openspcoop2.core.transazioni.utils;

import java.util.Date;

import org.openspcoop2.core.transazioni.DumpContenuto;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.CompressorType;
import org.openspcoop2.utils.io.CompressorUtilities;

/**     
 * TransactionContentUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransactionContentUtils {

	public static final String KEY_VALUE_TOO_LONG = "_____ValueTooLong_SaveBinaryInfo____";
	public static final int SOGLIA_VALUE_TOO_LONG = 4000;
	
	public final static String KEY_COMPRESSED = "_____Compressed_____";
	
	public static DumpContenuto createDumpContenuto(String nome, String valore, Date dumpTimestamp){
		DumpContenuto dumpContenuto = new DumpContenuto();
		dumpContenuto.setNome(nome);
		setDumpContenutoValue(dumpContenuto, valore);
		dumpContenuto.setDumpTimestamp(dumpTimestamp);
		return dumpContenuto;
	}
	
	public static void setDumpContenutoValue(DumpContenuto dumpContenuto, String valore){
		if(valore!=null && valore.length()>SOGLIA_VALUE_TOO_LONG){
			dumpContenuto.setValore(KEY_VALUE_TOO_LONG);
			dumpContenuto.setValoreAsBytes(valore.getBytes());
		}
		else{
			dumpContenuto.setValore(valore);
			dumpContenuto.setValoreAsBytes(null);
		}
	}
	
	public static String getDumpContenutoValue(DumpContenuto dumpContenuto){
		if(KEY_VALUE_TOO_LONG.equals(dumpContenuto.getValore())){
			return new String(dumpContenuto.getValoreAsBytes());
		}
		else{
			return dumpContenuto.getValore();
		}
	}
	
	public static void compress(DumpContenuto dumpContenuto,CompressorType tipoCompressione) throws UtilsException{
		dumpContenuto.setValoreAsBytes(CompressorUtilities.compress(TransactionContentUtils.getDumpContenutoValue(dumpContenuto).getBytes(),tipoCompressione));
		dumpContenuto.setValore(KEY_COMPRESSED);
	}
	
	public static void decompress(DumpContenuto dumpContenuto,CompressorType tipoCompressione) throws UtilsException{
		String valoreDecompresso = new String(CompressorUtilities.decompress(dumpContenuto.getValoreAsBytes(),tipoCompressione));
//		dumpContenuto.setValore(valoreDecompresso);
//		dumpContenuto.setValoreAsBytes(null);
		setDumpContenutoValue(dumpContenuto, valoreDecompresso);
	}
}
