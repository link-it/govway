package org.openspcoop2.monitor.engine.transaction;

import java.util.Date;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.CompressorType;
import org.openspcoop2.utils.io.CompressorUtilities;

import org.openspcoop2.monitor.engine.config.transazioni.constants.TipoCompressione;
import org.openspcoop2.core.transazioni.DumpContenuto;

/**
 * TransactionContentUtils
 *
 * @author Poli Andrea (apoli@link.it)
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
	
	public static void compress(DumpContenuto dumpContenuto,TipoCompressione tipoCompressione) throws UtilsException{
		switch (tipoCompressione) {
		case DEFLATER:
			dumpContenuto.setValoreAsBytes(CompressorUtilities.compress(TransactionContentUtils.getDumpContenutoValue(dumpContenuto).getBytes(),CompressorType.DEFLATER));
			break;
		case GZIP:
			dumpContenuto.setValoreAsBytes(CompressorUtilities.compress(TransactionContentUtils.getDumpContenutoValue(dumpContenuto).getBytes(),CompressorType.GZIP));
			break;
		case ZIP:
			dumpContenuto.setValoreAsBytes(CompressorUtilities.compress(TransactionContentUtils.getDumpContenutoValue(dumpContenuto).getBytes(),CompressorType.ZIP));
			break;
		}
		dumpContenuto.setValore(KEY_COMPRESSED);
	}
	
	public static void decompress(DumpContenuto dumpContenuto,TipoCompressione tipoCompressione) throws UtilsException{
		switch (tipoCompressione) {
		case DEFLATER:
			dumpContenuto.setValore(new String(CompressorUtilities.decompress(dumpContenuto.getValoreAsBytes(),CompressorType.DEFLATER)));
			break;
		case GZIP:
			dumpContenuto.setValore(new String(CompressorUtilities.decompress(dumpContenuto.getValoreAsBytes(),CompressorType.GZIP)));
			break;
		case ZIP:
			dumpContenuto.setValore(new String(CompressorUtilities.decompress(dumpContenuto.getValoreAsBytes(),CompressorType.ZIP)));
			break;
		}
		dumpContenuto.setValoreAsBytes(null);
	}
}
