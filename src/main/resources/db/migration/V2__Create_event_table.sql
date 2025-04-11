CREATE TABLE events
(
    id        SERIAL PRIMARY KEY,
    account_id VARCHAR(50) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    event_data TEXT
)