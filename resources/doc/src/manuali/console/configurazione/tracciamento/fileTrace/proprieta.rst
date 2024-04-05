.. _avanzate_fileTrace_proprieta:

Informazioni raggruppate in Proprietà 
--------------------------------------

Le informazioni associabili ai topic devono essere definite attraverso la sintassi descritta nella sezione :ref:`avanzate_fileTrace_info`.

Di seguito un esempio di definizione di 2 topic:

   ::

      format.topic.erogazioni.inputRequest="req"|"${log:transactionId}"|"govway"|"${log:inRequestDateZ(yyyy-MM-dd HH:mm:ss:SSS,UTC)}"|"${log:inRequestDate(Z)}"|"${log:forwardedIP}"|"HTTP/1.1"|"${log:httpMethod}"
      format.topic.erogazioni.inputResponse="res"|"${log:transactionId}"|"govway"|"${log:inRequestDateZ(yyyy-MM-dd HH:mm:ss:SSS,UTC)}"|"${log:inRequestDate(Z)}"|"${log:forwardedIP}"|"HTTP/1.1"|"${log:httpMethod}"|"${log:outHttpStatus}"
      
Nell'esempio appena riportato si può notare come i 2 topic utilizzano una parte comune ripetuta. È possibile migliorare la scrittura del file di definizione dei topic esplicitando una volta sola l'insieme di informazioni comuni tramite una proprietà che potrà essere riferita nella definizione di ogni topic.

Per definire una proprietà deve essere utilizzata la sintassi:

- '*format.property.<posizione>.<nomeProprietà>=<valoreProprietà>*'

Le proprietà verranno risolte in ordine lessicografico rispetto alla posizione indicata, in modo da garantire la corretta risoluzione se si hanno proprietà che sono definite tramite altre proprietà.

Di seguito il precedente esempio ridefinito tramite proprietà.

   ::

      # properties
      format.property.001.commons.govway-id=govway
      format.property.002.commons.id="${log:transactionId}"|"${log:property(commons.govway-id)}"
      format.property.003.commons.data="${log:inRequestDateZ(yyyy-MM-dd HH:mm:ss:SSS,UTC)}"|"${log:inRequestDate(Z)}"
      format.property.004.commons.remoteIP-protocol-method="${log:forwardedIP}"|"HTTP/1.1"|"${log:httpMethod}"
      format.property.005.commons=${log:property(commons.id)}|${log:property(commons.data)}|${log:property(commons.remoteIP-protocol-method)}

      # topic
      format.topic.erogazioni.inputRequest="req"|${log:property(commons)}
      format.topic.erogazioni.inputResponse="res"|${log:property(commons)}|"${log:outHttpStatus}"

È inoltre possibile definire l'escape di caratteri che possono essere presenti nelle informazioni da tracciare tramite la proprietà '*format.escape.<char>=<charEscaped>*'.

Di seguito un esempio di configurazione che effettua l'escape del carattere '\\"' sostituendolo con '\\\\"':

   ::

      format.escape."=\\"

Infine è possibile cifrare le informazioni registrate su file di log utilizzando una proprietà definita tramite la seguente sintassi:

- '*format.encryptedProperty.<posizione>.<nomeProprietà>=<valoreProprietàDaCifrare>*'

La modalità con cui verranno cifrati i valori della proprietà deve essere indicata tramite un'altra riga definita tramite la seguente sintassi: 

  '*format.encrypt.<posizione>.<nomeProprietà>=<encryptionMode>*'

La modalità '*<encryptionMode>*' indicata deve corrispondere ad una di quelle definite all'interno del file di configurazione stesso dei topic come descritto nella sezione :ref:`avanzate_fileTrace_proprietaCifrate`.

Rimane valida la considerazione che le proprietà (semplici o cifrate che siano) verranno risolte in ordine lessicografico rispetto alla posizione indicata, in modo da garantire la corretta risoluzione se si hanno proprietà che sono definite tramite altre proprietà.

Di seguito viene riportato l'esempio precedente modificato per cifrare la parte relativa agli indirizzi IP:

   ::

      # encryption modes
      encrypt.encTEST.mode=java
      encrypt.encTEST.keystore.type=symm
      encrypt.encTEST.key.path=/tmp/symmetric.secretkey
      encrypt.encTEST.key.algorithm=AES
      encrypt.encTEST.algorithm=AES/CBC/PKCS5Padding
      encrypt.encTEST.encoding=base64

      # properties
      format.property.001.commons.govway-id=govway
      format.property.002.commons.id="${log:transactionId}"|"${log:property(commons.govway-id)}"
      format.property.003.commons.data="${log:inRequestDateZ(yyyy-MM-dd HH:mm:ss:SSS,UTC)}"|"${log:inRequestDate(Z)}"
      format.encryptedProperty.004.commons.remoteIP-protocol-method="${log:forwardedIP}"|"HTTP/1.1"|"${log:httpMethod}"
      format.encrypt.004.commons.remoteIP-protocol-method=encTEST
      format.property.005.commons=${log:property(commons.id)}|${log:property(commons.data)}|${log:property(commons.remoteIP-protocol-method)}

      # topic
      format.topic.erogazioni.inputRequest="req"|${log:property(commons)}
      format.topic.erogazioni.inputResponse="res"|${log:property(commons)}|"${log:outHttpStatus}"

Di seguito viene fornito un esempio di informazioni prodotte per il topic 'inputRequest' senza cifrare i valori della proprietà 'commons.remoteIP-protocol-method':

   ::

      "req"|"b6cdd758-342c-4599-ae95-33a781730b3f"|"govway"|"2020-06-26 12:46:50:629"|"+0200"|"192.168.1.2"|"HTTP/1.1"|"POST"
      "req"|"2a9dc253-9dd5-458b-8689-edee7c9ba139"|"govway"|"2020-06-26 12:47:50:561"|"+0200"|"192.168.1.2"|"HTTP/1.1"|"POST"

Introducendo la cifratura si avrà un log simile al seguente:

   ::

      "req"|"b6cdd758-342c-4599-ae95-33a781730b3f"|"govway"|"2020-06-26 12:46:50:629"|"+0200"|Olq0UhaXq7OF2wAfwh+XuA==.DTAZdcP3keHRN97tWRoPVmlcMG91aScUFU2/r2TOwg0=
      "req"|"2a9dc253-9dd5-458b-8689-edee7c9ba139"|"govway"|"2020-06-26 12:47:50:561"|"+0200"|Olq0UhaXq7OF2wAfwh+XuA==.DTAZdcP3keHRN97tWRoPVmlcMG91aScUFU2/r2TOwg0=
