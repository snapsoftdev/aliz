WITH
  relevant_properties AS (
  SELECT
    PARSE_DATE("%Y%m%d", date) as parsedDate,
    DATE_DIFF(DATE '2010-01-04', PARSE_DATE("%Y%m%d",
        date), WEEK(MONDAY)) AS weeksDiff,
    fullVisitorId,
    prod.v2ProductName,
    prod.productSKU,
    prod.productQuantity,
    prod.productQuantity * prod.productPrice/1000000 totalPrice
  FROM
    `data-to-insights.ecommerce.web_analytics`,
    UNNEST(hits) AS h,
    UNNEST (h.product) AS prod
  WHERE
    h.eCommerceAction.action_type = '6'
    AND prod.productQuantity IS NOT NULL),
  week_range_helper AS (
  SELECT
    (DENSE_RANK() OVER(PARTITION BY fullVisitorId, productSKU ORDER BY weeksDiff)) - weeksDiff intervalNumber,
    *
  FROM
    relevant_properties ),
  results_before_schema_aggregation AS (
  SELECT
    fullVisitorId,
    productSKU,
    SUM(totalPrice) AS totalValue,
    MAX(weeksDiff) - MIN (weeksDiff) + 1 AS consecutiveWeeksCount,
    SUM(productQuantity) AS quantity,
    MIN(v2ProductName) AS productName,
    MAX(parsedDate) AS lastPurchaseDay
  FROM
    week_range_helper
  GROUP BY
    fullVisitorId,
    productSKU,
    intervalNumber
  HAVING
    consecutiveWeeksCount > 1 )
SELECT
  fullVisitorId,
  ARRAY_AGG ( STRUCT(productSKU,
      productName,
      quantity,
      totalValue,
      consecutiveWeeksCount,
      DATE_TRUNC(lastPurchaseDay, WEEK) AS lastWeek)) AS products
FROM
  results_before_schema_aggregation
GROUP BY
  fullVisitorId;
