.. _modipa_sicurezza_avanzate_header:

Header HTTP del token JWT
--------------------------

Il pattern di sicurezza, su API di tipo REST, produrrà la generazione di un token JWT firmato inserito all'interno dell'header HTTP previsto dalle *Linee Guida AGID di Interoperabilità*. L'header utilizzato varia a seconda del pattern di sicurezza selezionato:

- :ref:`modipa_idar01`: header HTTP 'Authorization' e token presente con prefisso 'BEARER '.

- :ref:`modipa_idar02`: header HTTP 'Authorization' e token presente con prefisso 'BEARER '.

- :ref:`modipa_idar03`: header HTTP 'Agid-JWT-Signature'.

Per consentire la retrocompatibilità con il pattern IDAR03/IDAS03 previsto nelle linee guida della versione 'bozza' (https://docs.italia.it/italia/piano-triennale-ict/lg-modellointeroperabilita-docs/it/bozza/) è possibile selezionare l'header HTTP utilizzato come mostrato nella figura :numref:`modipa_sicurezza_header`.

   .. figure:: ../../../_figure_console/modipa_sicurezza_header.png
    :scale: 50%
    :align: center
    :name: modipa_sicurezza_header

    Selezione dell'Header HTTP del token JWT
