CREATE TABLE IF NOT EXISTS queues(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS queue_entries(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'WAITING',
    joined_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    wait_time BIGINT,
    queue_id BIGINT NOT NULL REFERENCES queues(id),
    active BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS queue_events(
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    event_timestamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    queue_id BIGINT NOT NULL,
    entry_id BIGINT NOT NULL,
    published BOOLEAN NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_queue_events_published ON queue_events(published);