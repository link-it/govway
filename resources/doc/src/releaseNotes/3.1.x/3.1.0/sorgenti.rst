Sorgenti e Librerie 3Parti
---------------------------

Introdotto l'utilizzo di Maven (https://maven.apache.org/) per migliorare gli aspetti di gestone delle librerie esterne, di compilazione e di packaging. Ogni funzionalità introdotta, descritta di seguito, è attivabile con il relativo comando maven eseguibile nella radice del progetto:

-  Le librerie 3parti non devono più essere reperite tramite un file statico esterno, ma vengono scaricate da rete nella fase 'initialize'. Per forzare il download è possibile utilizzare il comando 'mvn initialize'.

-  Gli archivi jar sono ottenibili tramite il comando 'mvn compile'. Tutti i jar compilati saranno disponibili al termine della compilazione nella sottodirectory 'dist'.

-  Il pacchetto di installazione può essere prodotto a partire dai sorgenti utilizzando il comando 'mvn package'.

-  La documentazione presente all'interno del pacchetto di installazione viene prelevata dalla directory 'resources/doc/pdf/'. Per generarla a partire dai sorgenti (resources/doc/src/) è possibile utilizzare il comando 'mvn package -Dpackage.doc.generate=true'

   .. note::
      La generazione della documentazione, a partire dai sorgenti, richiede sphinx e latex.


