  -- homework
WITH
  analytics AS (
  SELECT
    fullVisitorId,
    weekOfYear,
    productQuantity,
    v2ProductName as productName,
    productSKU,
    localProductPrice AS price
  FROM (
    SELECT
      fullVisitorId,
      EXTRACT(ISOYEAR
      FROM
        PARSE_DATE('%Y%m%d',
          date))*100 + EXTRACT(WEEK
      FROM
        PARSE_DATE('%Y%m%d',
          date)) AS weekOfYear,
      product,
      eCommerceAction
    FROM
      `data-to-insights.ecommerce.web_analytics`
    CROSS JOIN
      UNNEST(hits) )
  CROSS JOIN
    UNNEST(product)
  WHERE
    eCommerceAction.action_type = '6'
    AND productQuantity IS NOT NULL ),
  sequentialPurchasesByUser AS (
  SELECT
    *,
    ROW_NUMBER() OVER (PARTITION BY fullVisitorId, productSKU ORDER BY weekOfYear) AS rn,
    weekOfYear - ROW_NUMBER() OVER (PARTITION BY fullVisitorId, productSKU ORDER BY weekOfYear) AS grp,
  FROM
    analytics ),
    
  result AS (
  SELECT
    fullVisitorId,
    productName,
    productSKU,
    SUM(productQuantity) AS quantity,
    SUM(productQuantity * price) AS totalValue,
    COUNT(*) AS consecutiveWeeksCount,
    MAX(weekOfYear) AS lastWeek
  FROM
    sequentialPurchasesByUser
  GROUP BY
    grp,
    fullVisitorId,
    productSKU,
    productName
  HAVING
    consecutiveWeeksCount > 1 )
    
SELECT
  fullVisitorId,
  ARRAY_AGG(STRUCT(productName,
      productSKU,
      quantity,
      lastWeek,
      consecutiveWeeksCount,
      totalValue)) AS products,
FROM
  result
GROUP BY
  fullVisitorId;