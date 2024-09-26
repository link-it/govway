.. _aaRisposta:

Risposta della Attribute Authority
----------------------------------

Ogni singola AA utilizza un proprio formato per la descrizione degli attributi nella risposta fornita. Il tipo della risposta deve essere definito nel campo '*Tipo Risposta*'. 
Di seguito vengono descritte le opzioni richieste per ogni tipo.

-  *JWS*: la risposta viene gestita come token JWT firmato (https://datatracker.ietf.org/doc/html/rfc7515) presente nel payload http. Deve essere indicato il claim che contiene gli attributi richiesti ed è possibile elencare più claim separandoli tramite virgola. 

   Nella sezione '*TrustStore*' devono essere indicati i dati che consentono di accedere al truststore da utilizzare per validare il token jws.  Nella configurazione proposta per default il certificato utilizzato per validare il token sarà quello presente all'interno del truststore, corrispondente all'identificativo indicato nel campo 'Alias Certificato'. In alternativa il certificato è ottenibile tramite le informazioni presenti nel token jwt (x5c, x5t, x5u) attraverso la modalità indicata nel campo 'Riferimento X.509'. Un certificato ottenuto tramite le informazioni presenti nel token jwt viene sempre validato rispetto al truststore e possono essere abilitati ulteriori criteri di verifica tramite CRL o Policy OCSP (vedi sezione :ref:`ocsp`).

   .. figure:: ../../_figure_console/AA-risposta-jws.png
      :scale: 100%
      :align: center
      :name: aaRispostaJwsFig

      Risposta di Attributi nel formato JWS

  Durante il processo di validazione della risposta, se il token viene firmato tramite un certificato x509, viene effettuato per default il controllo della validità (scadenza) del certificato. È possibile modificare tale controllo registrando la :ref:`configProprieta` '*attributeAuthority.validityCheck*', sull'erogazione o sulla fruizione dove viene utilizzata l'AttributeAuthority, con uno dei seguenti valori:

    - true: (default) il controllo di validità viene effettuato;
    - false: il controllo viene disabilitato; questo consente di accettare token firmati con certificati scaduti;
    - ifNotInTruststore: permette di eseguire la verifica della validità del certificato di firma solo se il certificato non è presente nel truststore utilizzato per la validazione (ad esempio, quando nel truststore è presente solo la CA). Con questa impostazione, un certificato scaduto verrà accettato se è presente nel truststore; in caso contrario, la transazione verrà rifiutata.

-  *JSON*: la risposta viene processata come messaggio JSON. Se gli attributi sono contenuti in uno o più elementi devono esserne elencati i nomi separandoli tramite virgola. Invece lasciando vuoto il campo '*Attributi*' tutti gli elementi presenti saranno interpretati come attributi.

   .. figure:: ../../_figure_console/AA-risposta-json.png
       :scale: 100%
       :align: center
       :name: aaRispostaJsonFig

       Risposta di Attributi nel formato JSON

-  *Personalizzata*: la risposta viene processata tramite la classe indicata nel campo '*ClassName*'. La classe fornita deve implementare l'interfaccia 'org.openspcoop2.pdd.core.token.attribute_authority.IRetrieveAttributeAuthorityResponseParser'.

   .. figure:: ../../_figure_console/AA-risposta-custom.png
       :scale: 100%
       :align: center
       :name: aaRispostaCustomFig

       Risposta di Attributi in un formato personalizzato
