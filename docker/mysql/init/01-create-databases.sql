-- MySQL 1대 + 서비스별 스키마. agit 기본 DB는 MYSQL_DATABASE env 가 생성.
-- init 스크립트는 최초 볼륨 생성 시에만 실행됩니다.
-- 기존 볼륨이 있으면 수동 CREATE DATABASE 하거나 down -v 후 재기동.

CREATE DATABASE IF NOT EXISTS plip_user
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
