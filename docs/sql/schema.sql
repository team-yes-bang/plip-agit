# DDL for this microservice.
# Append and keep up to date whenever tables are added or changed.

CREATE TABLE agits (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    agit_uuid          BINARY(16)  NOT NULL COMMENT 'UUIDv7',
    agit_name          VARCHAR(20) NOT NULL,
    description        TEXT        NOT NULL,
    maximum_capacity   INT         NOT NULL,
    code               CHAR(6)     NOT NULL,
    status             VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE, DELETED',
    created_at         DATETIME    NOT NULL,
    updated_at         DATETIME    NOT NULL,
    CONSTRAINT uk_agits_agit_uuid UNIQUE (agit_uuid),
    CONSTRAINT uk_agits_code UNIQUE (code)
);
