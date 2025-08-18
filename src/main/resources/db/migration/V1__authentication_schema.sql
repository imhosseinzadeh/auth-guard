-- Create schema for authentication if it doesn't exist
CREATE SCHEMA IF NOT EXISTS authentication;

-- Set search path to include the authentication schema
SET search_path TO public, authentication;

-- Enable the UUID extension for generating UUIDs
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE authentication.users
(
    user_id        UUID PRIMARY KEY,
    email          VARCHAR(100) NOT NULL UNIQUE,
    email_verified BOOLEAN      NOT NULL,
    firstname      VARCHAR(100),
    lastname       VARCHAR(100),
    phone_number   VARCHAR(15),
    password       VARCHAR(255) NOT NULL,
    created_at     TIMESTAMPTZ  NOT NULL,
    updated_at     TIMESTAMPTZ  NOT NULL,
    version        BIGINT,
    enabled        BOOLEAN      NOT NULL
);

CREATE TABLE authentication.otp
(
    otp_id     BIGSERIAL PRIMARY KEY,
    otp_type   VARCHAR(255) NOT NULL,
    code       VARCHAR(255) NOT NULL,
    issued_at  TIMESTAMPTZ  NOT NULL,
    expires_at TIMESTAMPTZ  NOT NULL,
    used_at    TIMESTAMPTZ,
    created_at TIMESTAMPTZ  NOT NULL,
    updated_at TIMESTAMPTZ  NOT NULL,
    version    BIGINT,
    user_id    UUID         NOT NULL REFERENCES authentication.users (user_id) ON DELETE CASCADE
);

-- Create roles table to define user roles
CREATE TABLE authentication.roles
(
    role_id     SMALLINT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE NOT NULL,
    description VARCHAR(255),
    created_at  TIMESTAMPTZ  NOT NULL,
    updated_at  TIMESTAMPTZ  NOT NULL,
    version     BIGINT
);

CREATE TABLE authentication.users_roles
(
    user_id UUID REFERENCES authentication.users (user_id) ON DELETE CASCADE,
    role_id SMALLINT REFERENCES authentication.roles (role_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE authentication.authorities
(
    authority_id SMALLINT PRIMARY KEY,
    name         VARCHAR(100) NOT NULL UNIQUE,
    description  VARCHAR(255),
    created_at   TIMESTAMPTZ  NOT NULL,
    updated_at   TIMESTAMPTZ  NOT NULL,
    version      BIGINT
);

CREATE TABLE authentication.roles_authorities
(
    role_id      SMALLINT REFERENCES authentication.roles (role_id) ON DELETE CASCADE,
    authority_id SMALLINT REFERENCES authentication.authorities (authority_id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, authority_id)
);

-- Indexes for users_roles
CREATE INDEX idx_users_roles_user_id ON authentication.users_roles (user_id);
CREATE INDEX idx_users_roles_role_id ON authentication.users_roles (role_id);

-- Indexes for roles
CREATE INDEX idx_roles_authorities_role_id ON authentication.roles_authorities (role_id);
CREATE INDEX idx_roles_authorities_authority_id ON authentication.roles_authorities (authority_id);

-- Indexes for one_time_passwords
CREATE INDEX idx_otp_code ON authentication.otp (code);
CREATE INDEX idx_otp_user ON authentication.otp (user_id);
CREATE INDEX idx_otp_user_code ON authentication.otp (user_id, code);