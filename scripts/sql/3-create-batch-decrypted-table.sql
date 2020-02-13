CREATE OR REPLACE TABLE
  `df.batch_decrypted_international_debt` AS
SELECT
  encrypted.* EXCEPT(cipher_indicator_name),
  AEAD.DECRYPT_STRING( (
    SELECT
      keyset
    FROM
      `df_secure.batch_indicators_keysets` AS keysets
    WHERE
      keysets.indicator_code = encrypted.indicator_code),
    encrypted.cipher_indicator_name,
    encrypted.indicator_code ) AS plain_indicator_name,
FROM
  `df.batch_encrypted_international_debt` AS encrypted