CREATE TABLE homework (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    description TEXT
);

CREATE TABLE lesson (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    updatedAt TIMESTAMP,
    homework_id INTEGER UNIQUE REFERENCES homework(id)
);