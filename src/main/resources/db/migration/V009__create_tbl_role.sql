CREATE TABLE TBL_ROLES (
    role_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role_type VARCHAR(30) NOT NULL UNIQUE
);
