-- =============================================
-- FURNITURE STORE DATABASE INITIALIZATION
-- Normalized schema - Professional design
-- =============================================

-- AUTH SCHEMA
CREATE SCHEMA IF NOT EXISTS auth;
CREATE SCHEMA IF NOT EXISTS inventory;
CREATE SCHEMA IF NOT EXISTS hr;
CREATE SCHEMA IF NOT EXISTS logistics;

-- =============================================
-- AUTH SCHEMA: roles, users
-- =============================================
CREATE TABLE auth.roles (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE auth.users (
    id           BIGSERIAL PRIMARY KEY,
    username     VARCHAR(100) NOT NULL UNIQUE,
    email        VARCHAR(255) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    role_id      BIGINT NOT NULL REFERENCES auth.roles(id),
    is_active    BOOLEAN NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_username ON auth.users(username);
CREATE INDEX idx_users_email ON auth.users(email);
CREATE INDEX idx_users_role ON auth.users(role_id);

-- =============================================
-- INVENTORY SCHEMA: categories, products, stock
-- =============================================
CREATE TABLE inventory.categories (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    parent_id   BIGINT REFERENCES inventory.categories(id),
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE inventory.products (
    id           BIGSERIAL PRIMARY KEY,
    sku          VARCHAR(50) NOT NULL UNIQUE,
    name         VARCHAR(200) NOT NULL,
    description  TEXT,
    category_id  BIGINT NOT NULL REFERENCES inventory.categories(id),
    price        NUMERIC(10,2) NOT NULL CHECK (price >= 0),
    weight_kg    NUMERIC(8,2),
    dimensions   VARCHAR(100),
    material     VARCHAR(100),
    color        VARCHAR(50),
    brand        VARCHAR(100),
    is_active    BOOLEAN NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_products_sku ON inventory.products(sku);
CREATE INDEX idx_products_category ON inventory.products(category_id);
CREATE INDEX idx_products_name ON inventory.products(name);

CREATE TABLE inventory.stock (
    id          BIGSERIAL PRIMARY KEY,
    product_id  BIGINT NOT NULL UNIQUE REFERENCES inventory.products(id),
    quantity    INT NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    min_stock   INT NOT NULL DEFAULT 5,
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- =============================================
-- HR SCHEMA: departments, positions, employees
-- =============================================
CREATE TABLE hr.departments (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE hr.positions (
    id            BIGSERIAL PRIMARY KEY,
    title         VARCHAR(100) NOT NULL,
    department_id BIGINT NOT NULL REFERENCES hr.departments(id),
    base_salary   NUMERIC(10,2),
    is_active     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(title, department_id)
);

CREATE TABLE hr.employees (
    id            BIGSERIAL PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL UNIQUE,
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    phone         VARCHAR(20),
    position_id   BIGINT NOT NULL REFERENCES hr.positions(id),
    user_id       BIGINT REFERENCES auth.users(id),
    hire_date     DATE NOT NULL,
    salary        NUMERIC(10,2) NOT NULL,
    is_active     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_employees_code ON hr.employees(employee_code);
CREATE INDEX idx_employees_email ON hr.employees(email);
CREATE INDEX idx_employees_position ON hr.employees(position_id);
CREATE INDEX idx_employees_user ON hr.employees(user_id);

-- =============================================
-- LOGISTICS SCHEMA: customers, addresses, deliveries
-- =============================================
CREATE TABLE logistics.customers (
    id           BIGSERIAL PRIMARY KEY,
    first_name   VARCHAR(100) NOT NULL,
    last_name    VARCHAR(100) NOT NULL,
    email        VARCHAR(255) UNIQUE,
    phone        VARCHAR(20) NOT NULL,
    rfc          VARCHAR(13),
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_customers_email ON logistics.customers(email);
CREATE INDEX idx_customers_phone ON logistics.customers(phone);

CREATE TABLE logistics.addresses (
    id           BIGSERIAL PRIMARY KEY,
    customer_id  BIGINT NOT NULL REFERENCES logistics.customers(id),
    street       VARCHAR(255) NOT NULL,
    ext_number   VARCHAR(20) NOT NULL,
    int_number   VARCHAR(20),
    neighborhood VARCHAR(100) NOT NULL,
    city         VARCHAR(100) NOT NULL,
    state        VARCHAR(100) NOT NULL,
    zip_code     VARCHAR(10) NOT NULL,
    country      VARCHAR(50) NOT NULL DEFAULT 'México',
    address_reference   TEXT,
    is_default   BOOLEAN NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_addresses_customer ON logistics.addresses(customer_id);

CREATE TABLE logistics.delivery_status_catalog (
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(30) NOT NULL UNIQUE,
    description VARCHAR(100) NOT NULL
);

CREATE TABLE logistics.deliveries (
    id                BIGSERIAL PRIMARY KEY,
    delivery_number   VARCHAR(30) NOT NULL UNIQUE,
    customer_id       BIGINT NOT NULL REFERENCES logistics.customers(id),
    address_id        BIGINT NOT NULL REFERENCES logistics.addresses(id),
    employee_id       BIGINT NOT NULL REFERENCES hr.employees(id),
    status_id         BIGINT NOT NULL REFERENCES logistics.delivery_status_catalog(id),
    scheduled_date    DATE NOT NULL,
    delivered_date    DATE,
    total_amount      NUMERIC(10,2) NOT NULL,
    delivery_cost     NUMERIC(10,2) NOT NULL DEFAULT 0,
    notes             TEXT,
    created_at        TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_deliveries_number ON logistics.deliveries(delivery_number);
CREATE INDEX idx_deliveries_customer ON logistics.deliveries(customer_id);
CREATE INDEX idx_deliveries_employee ON logistics.deliveries(employee_id);
CREATE INDEX idx_deliveries_status ON logistics.deliveries(status_id);
CREATE INDEX idx_deliveries_scheduled ON logistics.deliveries(scheduled_date);

CREATE TABLE logistics.delivery_items (
    id           BIGSERIAL PRIMARY KEY,
    delivery_id  BIGINT NOT NULL REFERENCES logistics.deliveries(id),
    product_id   BIGINT NOT NULL REFERENCES inventory.products(id),
    quantity     INT NOT NULL CHECK (quantity > 0),
    unit_price   NUMERIC(10,2) NOT NULL,
    UNIQUE(delivery_id, product_id)
);

CREATE INDEX idx_delivery_items_delivery ON logistics.delivery_items(delivery_id);
CREATE INDEX idx_delivery_items_product ON logistics.delivery_items(product_id);

-- =============================================
-- SEED DATA
-- =============================================
INSERT INTO auth.roles (name, description) VALUES
    ('ROLE_ADMIN', 'Full system access'),
    ('ROLE_CASHIER', 'Sales and delivery management');

INSERT INTO inventory.categories (name, description) VALUES
    ('Sala', 'Muebles para sala'),
    ('Recámara', 'Muebles para recámara'),
    ('Comedor', 'Muebles para comedor'),
    ('Oficina', 'Muebles para oficina'),
    ('Exterior', 'Muebles para exteriores');

INSERT INTO hr.departments (name, description) VALUES
    ('Ventas', 'Departamento de ventas'),
    ('Almacén', 'Control de inventario'),
    ('Administración', 'Administración general'),
    ('Entregas', 'Logística y entregas');

INSERT INTO hr.positions (title, department_id, base_salary) VALUES
    ('Gerente General', 3, 25000.00),
    ('Vendedor', 1, 10000.00),
    ('Cajero', 1, 8500.00),
    ('Almacenista', 2, 9000.00),
    ('Repartidor', 4, 8000.00);

INSERT INTO logistics.delivery_status_catalog (code, description) VALUES
    ('PENDING', 'Pendiente de asignación'),
    ('SCHEDULED', 'Programado'),
    ('IN_TRANSIT', 'En camino'),
    ('DELIVERED', 'Entregado'),
    ('CANCELLED', 'Cancelado'),
    ('FAILED', 'Entrega fallida');
