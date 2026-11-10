CREATE TABLE IF NOT EXISTS sports
(
    id
    INTEGER
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    name
    VARCHAR
(
    255
) NOT NULL
    );

CREATE UNIQUE INDEX IF NOT EXISTS uk_sports_name_lower
    ON sports (LOWER (name));