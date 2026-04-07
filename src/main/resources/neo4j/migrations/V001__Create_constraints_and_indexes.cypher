CREATE CONSTRAINT account_id_unique IF NOT EXISTS
FOR (a:Account) REQUIRE a.accountId IS UNIQUE;

CREATE CONSTRAINT transaction_id_unique IF NOT EXISTS
FOR (t:Transaction) REQUIRE t.transactionId IS UNIQUE;

CREATE CONSTRAINT device_fingerprint_unique IF NOT EXISTS
FOR (d:Device) REQUIRE d.fingerprint IS UNIQUE;

CREATE CONSTRAINT merchant_id_unique IF NOT EXISTS
FOR (m:Merchant) REQUIRE m.merchantId IS UNIQUE;

CREATE INDEX account_status_idx IF NOT EXISTS
FOR (a:Account) ON (a.status);

CREATE INDEX account_created_at_idx IF NOT EXISTS
FOR (a:Account) ON (a.createdAt);

CREATE INDEX transaction_timestamp_idx IF NOT EXISTS
FOR (t:Transaction) ON (t.timestamp);

CREATE INDEX transaction_amount_idx IF NOT EXISTS
FOR (t:Transaction) ON (t.amount);

CREATE INDEX location_coordinates_idx IF NOT EXISTS
FOR (l:Location) ON (l.latitude, l.longitude);

CREATE INDEX merchant_risk_score_idx IF NOT EXISTS
FOR (m:Merchant) ON (m.riskScore);