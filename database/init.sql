CREATE TABLE roles
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    created_at timestamp DEFAULT current_timestamp,
    updated_at timestamp DEFAULT current_timestamp,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    version BIGINT DEFAULT 0 NOT NULL
);

INSERT INTO roles (name)
VALUES ('ROLE_ADMIN'),
       ('ROLE_DOCTOR'),
       ('ROLE_PATIENT');

CREATE TABLE users
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email      varchar(100) UNIQUE NOT NULL,
    password   varchar(255)        NOT NULL,
    full_name  varchar(255)        NOT NULL,
    phone      varchar(20),
    status     varchar(20)      DEFAULT 'ACTIVE',
    role_id    INT                 NOT NULL,

    created_at timestamp        DEFAULT current_timestamp,
    updated_at timestamp        DEFAULT current_timestamp,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    version BIGINT DEFAULT 0 NOT NULL,

    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE TABLE doctors
(
    id               uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id          uuid NOT NULL UNIQUE,
    specialization   varchar(100),
    experience_years INT,
    biography        TEXT,

    created_at       timestamp        default current_timestamp,
    updated_at       timestamp        default current_timestamp,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    version BIGINT DEFAULT 0 NOT NULL,

    CONSTRAINT fk_doctor_user FOREIGN KEY (user_id) REFERENCES users (id) On DELETE CASCADE
);

CREATE TABLE patients
(
    id              uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         uuid NOT NULL UNIQUE,
    blood_type      varchar(5),
    height          DECIMAL(5, 2),
    weight          DECIMAL(5, 2),
    medical_history TEXT,

    created_at      timestamp        default current_timestamp,
    updated_at      timestamp        default current_timestamp,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    version BIGINT DEFAULT 0 NOT NULL,

    CONSTRAINT fk_patient_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE schedules (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    doctor_id uuid NOT NULL,
    available_from timestamp NOT NULL,
    available_to timestamp NOT NULL,
    is_available BOOLEAN DEFAULT True,
    created_at timestamp DEFAULT current_timestamp,
    updated_at timestamp default current_timestamp,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    version BIGINT DEFAULT 0 NOT NULL,

    CONSTRAINT fk_schedule_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

CREATE INDEX idx_schedule_doctor_date ON schedules(doctor_id, available_from);

Create table appointments (
    id uuid primary key default pg_catalog.gen_random_uuid(),
    patient_id uuid not null,
    doctor_id uuid not null,
    schedule_id uuid not null,
    reason text,
    status varchar(20) default 'PENDING',

    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    version BIGINT DEFAULT 0 NOT NULL,

    constraint fk_appointment_patient foreign key (patient_id) references patients(id),
    constraint fk_appointment_doctor foreign key (doctor_id) references doctors(id),
    constraint fk_appointment_schedule foreign key (schedule_id) references schedules(id)
);

CREATE INDEX idx_appointment_patient on appointments(patient_id);

Create table medical_records (
    id uuid primary key,
    appointment_id uuid not null unique,
    symptoms text,
    diagnosis text not null,
    treatment_plan text,
    notes text,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    version BIGINT DEFAULT 0 NOT NULL,
    constraint fk_medical_record_appointment foreign key (appointment_id) references appointments(id)
);

create table prescriptions (
    id uuid primary key,
    medical_record_id uuid not null,
    medication_name varchar(255) not null,
    dosage varchar(255) not null,
    duration varchar(255),
    instructions text,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    version BIGINT DEFAULT 0 NOT NULL,
    constraint fr_prescription_medical_record foreign key (medical_record_id) references medical_records(id)
);
