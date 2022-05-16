import java.io.ByteArrayOutputStream;

import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.mime.MimeMultipart;
import org.openspcoop2.utils.openapi.validator.MultipartUtilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;

public class MultipartProducer {

	public static void main(String[] args) throws Exception {
		
		String dir = "DIRECTORY_BENCHMARK";
		
		String dimensione = "409600B";
		
		String fileJson = FileSystemUtilities.readFile(dir+"benchmark/example/rest/messaggi/none/"+dimensione+".json");
		
		byte [] file = FileSystemUtilities.readBytesFromFile(dir+"benchmark/multipartGenerator/files/documenti.zip");
		byte [] fileEncodedBase64 = Base64Utilities.encode(file);
		
		MimeMultipart mm = MultipartUtilities.buildMimeMultipart(HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA_SUBTYPE,
				Integer.MAX_VALUE+"", HttpConstants.CONTENT_TYPE_PLAIN, "\"id\"", null,
				fileJson, HttpConstants.CONTENT_TYPE_JSON, "\"metadati\"", null,
				file, HttpConstants.CONTENT_TYPE_ZIP, "\"docPdf\"", "\"attachment.zip\"",
				fileEncodedBase64, HttpConstants.CONTENT_TYPE_PDF, "\"docPdf2\"", "\"attachment2.pdf\"",
				null, null, null, null);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		mm.writeTo(out);
		out.flush();
		out.close();
		
		FileSystemUtilities.writeFile(dir+"benchmark/example/rest/messaggi/multipart/"+dimensione+".bin", out.toByteArray());
		
		System.out.println("WRITE");		
	}

}
