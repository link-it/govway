Miglioramenti alla funzionalità di Tracciatura su File
------------------------------------------------------

È adesso possibile configurare una 'DenyList' o una 'WhiteList' che consente di personalizzare gli header HTTP ottenibili tramite la chiamate delle primitive: 'getInRequestHeaders', 'getOutRequestHeaders', 'getInResponseHeaders', 'getOutResponseHeaders'.

È inoltre stato corretto un bug presente sulle primitive indicate precedentemente quando venivano invocate con l'istruzione '${logBase64:xx}': il valore codificato in base64 restituito dalle primitive conteneva una lista di header in cui a sua volta i nomi e i valori erano nuovamente codificati in base64.

È infine adesso possibile abilitare il dump binario solamente per gli headers o per il payload dei messaggi scambiati.
