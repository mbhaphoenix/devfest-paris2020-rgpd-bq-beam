CREATE OR REPLACE TABLE
  `df.batch_encrypted_international_debt` AS
SELECT
  plain.* EXCEPT(indicator_name),
  AEAD.ENCRYPT( (
    SELECT
      keyset
    FROM
      `df_secure.batch_indicators_keysets` AS keysets
    WHERE
      keysets.indicator_code = plain.indicator_code),
    plain.indicator_name,
    plain.indicator_code ) AS cipher_indicator_name,
FROM
  `df.plain_international_debt` AS plain