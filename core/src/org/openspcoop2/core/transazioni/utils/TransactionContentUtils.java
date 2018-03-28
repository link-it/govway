package org.openspcoop2.core.transazioni.utils;

import java.util.Date;

import org.openspcoop2.core.transazioni.DumpContenuto;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.CompressorType;
import org.openspcoop2.utils.io.CompressorUtilities;

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
