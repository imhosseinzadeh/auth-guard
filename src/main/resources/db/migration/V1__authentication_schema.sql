-- Create schema for authentication if it doesn't exist
CREATE SCHEMA IF NOT EXISTS authentication;

-- Set search path to include the authentication schema
SET search_path TO public, authentication;

-- Enable the UUID extension for generating UUIDs
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create users table to store user information
CREATE TABLE authentication.users
(
    user_id         UUID PRIMARY KEY,
    email           VARCHAR(100) NOT NULL UNIQUE,
    firstname       VARCHAR(100),
    lastname        VARCHAR(100),
    phone_number    VARCHAR(15),
    hashed_password VARCHAR(255) NOT NULL, -- Hashed password of the user
    created_at      TIMESTAMP WITH TIME ZONE,
    enabled         BOOLEAN
);

CREATE TABLE authentication.verification_token
(
    verification_token_id BIGSERIAL PRIMARY KEY,
    token                 VARCHAR(255) NOT NULL,
    created_at            TIMESTAMP WITH TIME ZONE,
    expires_at            TIMESTAMP WITH TIME ZONE,
    verified_at           TIMESTAMP WITH TIME ZONE,
    user_id               UUID REFERENCES authentication.users (user_id)
);

-- Create roles table to define user roles
CREATE TABLE authentication.roles
(
    role_id     SMALLINT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(255)
);

-- Create users_roles table to establish a many-to-many relationship between users and roles
CREATE TABLE authentication.users_roles
(
    user_id UUID REFERENCES authentication.users (user_id) ON DELETE CASCADE,
    role_id SMALLINT REFERENCES authentication.roles (role_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Create permissions table to define permissions for roles
CREATE TABLE authentication.permissions
(
    permission_id SMALLINT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    description   VARCHAR(255)
);

-- Create roles_permissions table to establish a many-to-many relationship between roles and permissions
CREATE TABLE authentication.roles_permissions
(
    role_id       SMALLINT REFERENCES authentication.roles (role_id) ON DELETE CASCADE,
    permission_id SMALLINT REFERENCES authentication.permissions (permission_id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

-- Add indexes for foreign keys to improve query performance
CREATE INDEX ON authentication.users_roles (user_id);
CREATE INDEX ON authentication.users_roles (role_id);
CREATE INDEX ON authentication.roles_permissions (role_id);
CREATE INDEX ON authentication.roles_permissions (permission_id);