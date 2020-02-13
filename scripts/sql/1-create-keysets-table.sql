CREATE OR REPLACE TABLE
  df_secure.batch_indicators_keysets AS
SELECT
  indicator_code,
  KEYS.NEW_KEYSET('AEAD_AES_GCM_256') AS keyset
FROM (
  SELECT
    DISTINCT(indicator_code)
  FROM
    df.plain_international_debt)
