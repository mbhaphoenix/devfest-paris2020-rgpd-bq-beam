SELECT
  rowkey AS indicator_code,
  cf.keyset.cell.value AS keyset
FROM
  df_secure.streaming_indicators_keysets
