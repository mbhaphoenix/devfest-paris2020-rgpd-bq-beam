(
  SELECT * EXCEPT(plain_indicator_name), plain_indicator_name
  FROM
    df.streaming_decrypted_international_debt EXCEPT DISTINCT
  SELECT * EXCEPT(indicator_name), indicator_name
  FROM
    df.plain_international_debt
)
UNION ALL (
  SELECT * EXCEPT(indicator_name), indicator_name
  FROM
    df.plain_international_debt EXCEPT DISTINCT
  SELECT * EXCEPT(plain_indicator_name), plain_indicator_name
  FROM
    df.streaming_decrypted_international_debt
)