Miglioramenti alla funzionalità di Validazione dei Contenuti
------------------------------------------------------------

La funzionalità di validazione dei contenuti tramite OpenAPI è stata modificata per garantire che i payload contenenti elementi 'date-time' non conformi a RFC 3339 (#section-5.6), come caratteri minuscoli (t, z) o spazi (' ') al posto del separatore T, vengano rifiutati. 

Prima della modifica tali formati venivano accettati nei payload ma non negli header, nei parametri delle URL e nei path; adesso la validazione è uniforme su tutte le sorgenti. 

È stata inoltre introdotta una configurazione parametrica che consente, se necessario, di ripristinare l'accettazione di formati non conformi.

