Feature: Controllo del token client

Scenario:

    * match bodyPath('/Envelope/Header/Security/Signature') == "#present"
    * match bodyPath('/Envelope/Header/Security/Timestamp/Created') == "#string"
    * match bodyPath('/Envelope/Header/Security/Timestamp/Expires') == "#string"
    * match bodyPath('/Envelope/Header/To') == to
    * match bodyPath('/Envelope/Header/From/Address') == address
    * match bodyPath('/Envelope/Header/MessageID') == "#uuid"
    * match bodyPath('/Envelope/Header/ReplyTo/Address') == "http://www.w3.org/2005/08/addressing/anonymous"

    * def body = bodyPath('/')
    * call check_signature [ {element: 'To'}, {element: 'From'}, {element: 'MessageID'}, {element: 'ReplyTo'} ]