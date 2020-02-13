CREATE OR REPLACE TABLE
  `df.streaming_decrypted_international_debt` AS
SELECT
  encrypted.* EXCEPT(cipher_indicator_name),
  AEAD.DECRYPT_STRING( (
    SELECT
      cf.keyset.cell.value as keyset
    FROM
      `df_secure.streaming_indicators_keysets` AS keysets
    WHERE
      keysets.rowkey = encrypted.indicator_code),
    encrypted.cipher_indicator_name,
    encrypted.indicator_code ) AS plain_indicator_name,
FROM
  `df.streaming_encrypted_international_debt` AS encrypted