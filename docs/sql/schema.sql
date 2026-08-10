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
    thumbnail_path     VARCHAR(255) NULL COMMENT '아지트 섬네일',
    created_at         DATETIME    NOT NULL,
    updated_at         DATETIME    NOT NULL,
    CONSTRAINT uk_agits_agit_uuid UNIQUE (agit_uuid),
    CONSTRAINT uk_agits_code UNIQUE (code)
);

CREATE TABLE agit_member_profiles (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    agit_id              BIGINT       NOT NULL COMMENT 'agits.id 논리 FK',
    user_uuid            BINARY(16)   NOT NULL COMMENT 'UUIDv7',
    nickname             VARCHAR(100) NOT NULL,
    profile_image_path   VARCHAR(255) NULL,
    status               VARCHAR(20)  NOT NULL COMMENT 'ACTIVE, LEFT, BANNED',
    role                 VARCHAR(20)  NOT NULL COMMENT 'HOST, GUEST',
    apply_items          JSON         NULL COMMENT 'json으로 아이템 key 보유',
    created_at           DATETIME     NOT NULL,
    updated_at           DATETIME     NOT NULL,
    CONSTRAINT uq_agit_user UNIQUE (agit_id, user_uuid)
);
