keytool -importkeystore -srcstorepass 123456 -srckeystore ExampleClient1.p12 -alias "ExampleClient1" -srcstoretype PKCS12 -destkeystore ExampleClient1ContainsP12.p12 -deststorepass 123456 -deststoretype PKCS12
keytool -importkeystore -srcstorepass 123456 -srckeystore ExampleClient1.p12 -alias "ExampleClient1" -srcstoretype PKCS12 -destkeystore ExampleClient1ContainsP12.jks -deststorepass 123456 -destkeypass 123456 -deststoretype JKS


