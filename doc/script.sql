CREATE TABLE "user" (
    id BIGSERIAL PRIMARY KEY,
    wealth DOUBLE PRECISION NOT NULL,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE "transaction" (
    idTransaction SERIAL PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    description TEXT,
    transactionDate TIMESTAMP,
    amount DOUBLE PRECISION NOT NULL,
    fee DOUBLE PRECISION,
    FOREIGN KEY (sender_id) REFERENCES "user" (id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES "user" (id) ON DELETE CASCADE
);

CREATE TABLE user_connections (
    user_id BIGINT NOT NULL,
    connection_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, connection_id),
    FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE,
    FOREIGN KEY (connection_id) REFERENCES "user" (id) ON DELETE CASCADE
);

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(255) NOT NULL
);

CREATE TABLE users_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);
