SELECT KEYS.KEYSET_TO_JSON(KEYS.NEW_KEYSET('AEAD_AES_GCM_256'));

SELECT KEYS.KEYSET_TO_JSON(keyset) from df.batch_indicators_keysets limit 3