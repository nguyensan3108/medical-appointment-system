CREATE TABLE  roles (
    id SERIAL PRIMARY KEY ,
    name VARCHAR(50) UNIQUE NOT NULL
);

INSERT INTO roles (name)
VALUES ('ROLE_ADMIN'),
       ('ROLE_DOCTOR'),
       ('ROLE_PATIENT');

CREATE TABLE  users(
    id UUID PRIMARY KEY  DEFAULT gen_random_uuid(),
    email varchar(100) UNIQUE NOT NULL,
    password varchar(255) NOT NULL,
    full_name varchar(255) NOT NULL,
    phone varchar(20),
    status varchar(20) DEFAULT 'ACTIVE',
    role_id INT NOT NULL,

    created_at timestamp DEFAULT current_timestamp,
    updated_at timestamp DEFAULT current_timestamp,

    CONSTRAINT  fk_user_role FOREIGN KEY (role_id) REFERENCES roles(id)
);
