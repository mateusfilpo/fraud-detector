CREATE CONSTRAINT fraud_alert_id_unique IF NOT EXISTS
FOR (f:FraudAlert) REQUIRE f.alertId IS UNIQUE;

CREATE INDEX fraud_alert_status_idx IF NOT EXISTS
FOR (f:FraudAlert) ON (f.status);

CREATE INDEX fraud_alert_account_id_idx IF NOT EXISTS
FOR (f:FraudAlert) ON (f.accountId);

CREATE INDEX fraud_alert_transaction_id_idx IF NOT EXISTS
FOR (f:FraudAlert) ON (f.transactionId);