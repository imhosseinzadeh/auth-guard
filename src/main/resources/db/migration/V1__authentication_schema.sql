-- Create schema for authentication if it doesn't exist
CREATE SCHEMA IF NOT EXISTS authentication;

-- Set search path to include the authentication schema
SET search_path TO public, authentication;

-- Enable the UUID extension for generating UUIDs
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create users table to store user information
CREATE TABLE authentication.users
(
    user_id      UUID PRIMARY KEY,
    email        VARCHAR(100) NOT NULL UNIQUE,
    firstname    VARCHAR(100),
    lastname     VARCHAR(100),
    phone_number VARCHAR(15),
    password     VARCHAR(255) NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL,
    updated_at   TIMESTAMPTZ  NOT NULL,
    version      BIGINT,
    enabled      BOOLEAN      NOT NULL DEFAULT true
);

-- Create verification_codes table
CREATE TABLE authentication.verification_codes
(
    verification_code_id BIGSERIAL PRIMARY KEY,
    code                 VARCHAR(255) NOT NULL,
    issued_at            TIMESTAMP WITH TIME ZONE,
    expires_at           TIMESTAMPTZ  NOT NULL,
    verified_at          TIMESTAMPTZ,
    created_at           TIMESTAMPTZ  NOT NULL,
    updated_at           TIMESTAMPTZ  NOT NULL,
    version              BIGINT,
    user_id              UUID REFERENCES authentication.users (user_id) ON DELETE CASCADE
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

-- Create users_roles table for many-to-many relationship between users and roles
CREATE TABLE authentication.users_roles
(
    user_id UUID REFERENCES authentication.users (user_id) ON DELETE CASCADE,
    role_id SMALLINT REFERENCES authentication.roles (role_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Create permissions table
CREATE TABLE authentication.permissions
(
    permission_id SMALLINT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL UNIQUE,
    description   VARCHAR(255),
    created_at    TIMESTAMPTZ  NOT NULL,
    updated_at    TIMESTAMPTZ  NOT NULL,
    version       BIGINT
);

-- Create roles_permissions table for many-to-many relationship
CREATE TABLE authentication.roles_permissions
(
    role_id       SMALLINT REFERENCES authentication.roles (role_id) ON DELETE CASCADE,
    permission_id SMALLINT REFERENCES authentication.permissions (permission_id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

-- Add indexes for foreign keys
CREATE INDEX idx_users_roles_user_id ON authentication.users_roles (user_id);
CREATE INDEX idx_users_roles_role_id ON authentication.users_roles (role_id);
CREATE INDEX idx_roles_permissions_role_id ON authentication.roles_permissions (role_id);
CREATE INDEX idx_roles_permissions_permission_id ON authentication.roles_permissions (permission_id);