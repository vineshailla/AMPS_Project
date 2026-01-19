
Feature: Trade Data Enrichment
  As a trading system
  I want to enrich trade data with product information
  So that trades have complete and accurate financial details

  Background:
    Given the following products exist in the system:
      | product_name | price | broker_id | commission | tax |
      | AAPL         | 150.0 | 101       | 10.5       | 5.0  |
      | GOOGL        | 2800.0| 102       | 25.0       | 12.5 |
      | TSLA         | 700.0 | 103       | 15.0       | 7.5  |

  Scenario: Enrich trade with matching product
    Given a trade with symbol "AAPL" and price 0
    When the enrichment process runs
    Then the trade should be enriched with product price 150.0
    And the broker_id should be "101"
    And the commission should be 10.5
    And the tax should be 5.0
    And the status should be "ENRICHED"
    And gross_amount should be calculated correctly
    And net_amount should be calculated correctly

  Scenario: Enrich trade with existing price
    Given a trade with symbol "GOOGL" and price 2850.0
    When the enrichment process runs
    Then the trade price should remain 2850.0
    And the broker_id should be "102"
    And the status should be "ENRICHED"

  Scenario: Trade with missing product
    Given a trade with symbol "MSFT" which has no product definition
    When the enrichment process runs
    Then the trade should remain unchanged
    And the status should not be "ENRICHED"

  Scenario: Enrich trade with empty broker_id
    Given a trade with symbol "TSLA" and empty broker_id
    When the enrichment process runs
    Then the broker_id should be "103"
    And the trade should be marked as "ENRICHED"

  Scenario: Enrich trade with existing broker_id
    Given a trade with symbol "AAPL" and broker_id "999"
    When the enrichment process runs
    Then the broker_id should remain "999"
    And the trade should be enriched

  Scenario: Calculate financial amounts correctly
    Given a trade with symbol "AAPL", quantity 100, and price 0
    When the enrichment process runs
    Then gross_amount should be 15000.0
    And net_amount should be 14984.5

  Scenario: Mark trade as enriched
    Given any trade
    When the isEnriched function is called
    Then the trade status should be "ENRICHED"
    And all other fields should remain unchanged