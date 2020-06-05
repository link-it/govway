.. _errori_400_ContentTypeNotSupported:

ContentTypeNotSupported
-----------------------

GovWay ha rilevato una richiesta verso una API SOAP che possiede un header http 'Content-Type' non supportato. 

Il valore supportato per SOAP 1.1 è 'text/xml' mentre per SOAP 1.2 è 'application/soap+xml'. Sono supportati anche i formati multipart 'SOAP With Attachments' (Multipart/Related; type=text/xml; boundary=...) e MTOM (Multipart/Related; type="application/xop+xml"; start-info="text/xml"; boundary=...).

