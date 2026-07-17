-- @formatter:off
-- =============================================================================
-- AngusStorage MySQL Schema
-- Generated from JPA @Entity under cloud.xcan.angus.core.storage.domain
-- =============================================================================
-- Base columns:
--   TenantAuditingEntity : tenant_id, created_by, created_date, modified_by, modified_date
--   AuditingEntity       : created_by, created_date, modified_by, modified_date
--   TenantEntity         : tenant_id
-- =============================================================================


-- ============================================================
-- Bucket
-- ============================================================
CREATE TABLE IF NOT EXISTS `storage_bucket` (
  `id`             BIGINT       NOT NULL,
  `name`           VARCHAR(40)  NOT NULL,
  `acl`            VARCHAR(40)  NULL,
  `tenant_created` TINYINT(1)   NULL,
  `created_by`     BIGINT       NOT NULL,
  `created_date`   DATETIME     NOT NULL,
  `modified_by`    BIGINT       NULL,
  `modified_date`  DATETIME     NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uidx_name` (`name`),
  KEY `idx_tenant_created_flag` (`tenant_created`),
  KEY `idx_created_date` (`created_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS `storage_bucket_biz_config` (
  `id`                   BIGINT       NOT NULL,
  `biz_key`              VARCHAR(80)  NOT NULL,
  `bucket_name`          VARCHAR(40)  NOT NULL,
  `remark`               VARCHAR(200) NULL,
  `public_access`        TINYINT(1)   NULL,
  `public_token_auth`    TINYINT(1)   NULL,
  `encrypt`              TINYINT(1)   NULL,
  `multi_tenant_ctrl`    TINYINT(1)   NULL,
  `enabled_auth`         TINYINT(1)   NULL,
  `allow_tenant_created` TINYINT(1)   NULL,
  `app_code`             VARCHAR(80)  NULL,
  `app_admin_code`       VARCHAR(80)  NULL,
  `cache_age`            INT          NOT NULL DEFAULT 0,
  `private0`             TINYINT(1)   NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uidx_biz_key` (`biz_key`),
  KEY `idx_bucket_name` (`bucket_name`),
  KEY `idx_app_code` (`app_code`),
  KEY `idx_bucket_biz` (`bucket_name`, `biz_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- Space
-- ============================================================
CREATE TABLE IF NOT EXISTS `storage_object_space` (
  `id`             BIGINT       NOT NULL,
  `project_id`     BIGINT       NULL,
  `name`           VARCHAR(100) NULL,
  `biz_key`        VARCHAR(80)  NULL,
  `bucket_name`    VARCHAR(40)  NULL,
  `quota_size`     VARCHAR(40)  NULL,
  `auth`           TINYINT(1)   NULL,
  `customized`     TINYINT(1)   NULL,
  `remark`         VARCHAR(200) NULL,
  `tenant_id`      BIGINT       NULL,
  `created_by`     BIGINT       NOT NULL,
  `created_date`   DATETIME     NOT NULL,
  `modified_by`    BIGINT       NULL,
  `modified_date`  DATETIME     NULL,
  PRIMARY KEY (`id`),
  KEY `idx_tenant_created` (`tenant_id`, `created_date`),
  KEY `idx_tenant_biz_key` (`tenant_id`, `biz_key`),
  KEY `idx_tenant_name` (`tenant_id`, `name`),
  KEY `idx_bucket_name` (`bucket_name`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_auth` (`auth`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS `storage_space_object` (
  `id`                  BIGINT        NOT NULL,
  `project_id`          BIGINT        NULL,
  `name`                VARCHAR(200)  NULL,
  `type`                VARCHAR(20)   NULL,
  `store_type`          VARCHAR(40)   NULL,
  `fid`                 BIGINT        NULL,
  `level`               INT           NOT NULL DEFAULT 0,
  `size`                BIGINT        NOT NULL DEFAULT 0,
  `space_id`            BIGINT        NULL,
  `parent_directory_id` BIGINT        NULL,
  `parent_like_id`      VARCHAR(200)  NULL,
  `tenant_id`           BIGINT        NULL,
  `created_by`          BIGINT        NOT NULL,
  `created_date`        DATETIME      NOT NULL,
  `modified_by`         BIGINT        NULL,
  `modified_date`       DATETIME      NULL,
  PRIMARY KEY (`id`),
  KEY `idx_tenant_created` (`tenant_id`, `created_date`),
  KEY `idx_tenant_type` (`tenant_id`, `type`),
  KEY `idx_space_type` (`space_id`, `type`),
  KEY `idx_space_id` (`space_id`),
  KEY `idx_parent_dir_type` (`parent_directory_id`, `type`),
  KEY `idx_parent_dir_name_type` (`parent_directory_id`, `name`(100), `type`),
  KEY `idx_parent_like_id` (`parent_like_id`),
  KEY `idx_fid` (`fid`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_store_type` (`store_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS `storage_object_file` (
  `id`                  BIGINT        NOT NULL,
  `project_id`          BIGINT        NULL,
  `name`                VARCHAR(200)  NULL,
  `unique_name`         VARCHAR(255)  NULL,
  `oid`                 BIGINT        NULL,
  `path`                VARCHAR(800)  NULL,
  `size`                BIGINT        NOT NULL DEFAULT 0,
  `content_type`        VARCHAR(200)  NULL,
  `store_address`       VARCHAR(800)  NULL,
  `store_type`          VARCHAR(40)   NULL,
  `space_id`            BIGINT        NULL,
  `parent_directory_id` BIGINT        NULL,
  `instance_id`         VARCHAR(100)  NULL,
  `biz_key`             VARCHAR(80)   NULL,
  `bucket_name`         VARCHAR(40)   NULL,
  `upload_id`           VARCHAR(200)  NULL,
  `upload_type`         VARCHAR(40)   NULL,
  `completed`           TINYINT(1)    NULL,
  `store_deleted`       TINYINT(1)    NULL,
  `deleted_retry_num`   INT           NULL,
  `public_token`        VARCHAR(40)   NULL,
  `tenant_id`           BIGINT        NULL,
  `created_by`          BIGINT        NOT NULL,
  `created_date`        DATETIME      NOT NULL,
  `modified_by`         BIGINT        NULL,
  `modified_date`       DATETIME      NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uidx_unique_name` (`unique_name`),
  KEY `idx_tenant_created` (`tenant_id`, `created_date`),
  KEY `idx_store_deleted_unique` (`store_deleted`, `unique_name`),
  KEY `idx_bucket_name` (`bucket_name`),
  KEY `idx_bucket_biz` (`bucket_name`, `biz_key`),
  KEY `idx_space_id` (`space_id`),
  KEY `idx_oid` (`oid`),
  KEY `idx_public_token` (`public_token`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_parent_directory_id` (`parent_directory_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS `storage_space_auth` (
  `id`               BIGINT       NOT NULL,
  `space_id`         BIGINT       NULL,
  `auth_object_type` VARCHAR(40)  NULL,
  `auth_object_id`   BIGINT       NULL,
  `auth_data`        JSON         NULL,
  `creator`          TINYINT(1)   NULL,
  `created_by`       BIGINT       NULL,
  `created_date`     DATETIME     NULL,
  `tenant_id`        BIGINT       NULL,
  PRIMARY KEY (`id`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_space_id` (`space_id`),
  KEY `idx_auth_object_id` (`auth_object_id`),
  KEY `idx_space_auth_object` (`space_id`, `auth_object_id`),
  KEY `idx_space_creator` (`space_id`, `creator`),
  KEY `idx_space_auth_object_type` (`space_id`, `auth_object_id`, `auth_object_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS `storage_space_share` (
  `id`               BIGINT       NOT NULL,
  `space_id`         BIGINT       NULL,
  `share_type`       VARCHAR(40)  NULL,
  `all`              TINYINT(1)   NULL,
  `object_ids`       JSON         NULL,
  `quick_object_id`  BIGINT       NULL,
  `url`              VARCHAR(400) NULL,
  `expired`          TINYINT(1)   NULL,
  `expired_duration` VARCHAR(80)  NULL,
  `expired_date`     DATETIME     NULL,
  `public0`          TINYINT(1)   NULL,
  `public_token`     VARCHAR(40)  NULL,
  `password`         VARCHAR(40)  NULL,
  `remark`           VARCHAR(200) NULL,
  `tenant_id`        BIGINT       NULL,
  `created_by`       BIGINT       NOT NULL,
  `created_date`     DATETIME     NOT NULL,
  `modified_by`      BIGINT       NULL,
  `modified_date`    DATETIME     NULL,
  PRIMARY KEY (`id`),
  KEY `idx_tenant_created` (`tenant_id`, `created_date`),
  KEY `idx_space_id` (`space_id`),
  KEY `idx_quick_object_id` (`quick_object_id`),
  KEY `idx_public_token` (`public_token`),
  KEY `idx_expired_date` (`expired_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- Setting
-- ============================================================
CREATE TABLE IF NOT EXISTS `storage_setting` (
  `id`     BIGINT       NOT NULL,
  `pkey`   VARCHAR(40)  NOT NULL,
  `pvalue` LONGTEXT     NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uidx_pkey` (`pkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
