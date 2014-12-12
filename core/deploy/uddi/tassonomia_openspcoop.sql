INSERT INTO TMODEL (TMODEL_KEY,AUTHORIZED_NAME,OPERATOR,NAME,OVERVIEW_URL)
VALUES ('uuid:b6ba2f0c-b4e5-40c4-a4f4-6212aa8d389f','Administrator','jUDDI.org','OpenSPCoop:SPCoopIdentifier', 'http://www.openspcoop2.org/taxonomies/SPCoopIdentifier.xml');

INSERT INTO TMODEL_DESCR (TMODEL_KEY,TMODEL_DESCR_ID,LANG_CODE,DESCR)
VALUES ('uuid:b6ba2f0c-b4e5-40c4-a4f4-6212aa8d389f',0,'it','OpenSPCoop');

INSERT INTO TMODEL_DOC_DESCR (TMODEL_KEY,TMODEL_DOC_DESCR_ID,LANG_CODE,DESCR)
VALUES ('uuid:b6ba2f0c-b4e5-40c4-a4f4-6212aa8d389f',0,'it','tModel utilizzata per la identificazione di un Mittente/Destinatario di una busta');

INSERT INTO TMODEL_CATEGORY (TMODEL_KEY,CATEGORY_ID,TMODEL_KEY_REF,KEY_NAME,KEY_VALUE)
VALUES ('uuid:b6ba2f0c-b4e5-40c4-a4f4-6212aa8d389f',0, 'uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4', 'types', 'identifier');


