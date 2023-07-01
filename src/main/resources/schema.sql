drop table if exists users_items cascade;
drop table if exists requests cascade;
drop table if exists items cascade;
drop table if exists bookings cascade;
drop table if exists comments cascade;

CREATE TABLE IF NOT EXISTS users_items
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description  VARCHAR(1000) NOT NULL,
    requestor_id BIGINT REFERENCES users_items (id)
);

CREATE TABLE IF NOT EXISTS items
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         VARCHAR(255)     NOT NULL,
    description  VARCHAR(1000) NOT NULL,
    is_available BOOLEAN       NOT NULL,
    owner_id     BIGINT REFERENCES users_items (id),
    request_id   BIGINT REFERENCES requests (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id    BIGINT REFERENCES items (id),
    booker_id  BIGINT REFERENCES users_items (id),
    status     VARCHAR(8)                     NOT NULL
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text      VARCHAR(1000)               NOT NULL,
    item_id   BIGINT REFERENCES items (id),
    author_id BIGINT REFERENCES users_items (id),
    created   TIMESTAMP WITHOUT TIME ZONE NOT NULL
);