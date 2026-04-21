CREATE TABLE IF NOT EXISTS cars (
    id VARCHAR(50) PRIMARY KEY,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    plate_number VARCHAR(50) NOT NULL UNIQUE,
    rental_type VARCHAR(20) NOT NULL,
    price_per_unit NUMERIC(12, 2) NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS rentals (
    id VARCHAR(50) PRIMARY KEY,
    car_id VARCHAR(50) NOT NULL,
    customer_name VARCHAR(150) NOT NULL,
    rental_type VARCHAR(20) NOT NULL,
    duration INTEGER NOT NULL,
    rented_at VARCHAR(50) NOT NULL,
    total_price NUMERIC(12, 2) NOT NULL,
    CONSTRAINT fk_rentals_car FOREIGN KEY (car_id) REFERENCES cars(id)
);

INSERT INTO cars (id, brand, model, plate_number, rental_type, price_per_unit, available)
VALUES
    ('CAR-001', 'Toyota', 'Prius', 'UBA-1024', 'DAILY', 120000, TRUE),
    ('CAR-002', 'Hyundai', 'Sonata', 'UBA-2048', 'DAILY', 180000, TRUE),
    ('CAR-003', 'Tesla', 'Model 3', 'UBA-4096', 'HOURLY', 35000, TRUE),
    ('CAR-004', 'Kia', 'Morning', 'UBA-8192', 'HOURLY', 22000, TRUE)
ON CONFLICT (id) DO NOTHING;

