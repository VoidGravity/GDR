CREATE TABLE rooms (
                       number INT PRIMARY KEY,
                       type VARCHAR(20) NOT NULL
);

CREATE TABLE reservations (
                              id SERIAL PRIMARY KEY,
                              client_name VARCHAR(100) NOT NULL,
                              room_number INT NOT NULL,
                              check_in_date DATE NOT NULL,
                              check_out_date DATE NOT NULL,
                              status VARCHAR(20) NOT NULL,
                              total_price DECIMAL(10, 2) NOT NULL,
                              special_notes TEXT,
                              FOREIGN KEY (room_number) REFERENCES rooms(number)
);

-- Insert initial rooms

INSERT INTO rooms (number, type) VALUES
                                     (1, 'STANDARD'), (2, 'STANDARD'), (3, 'STANDARD'), (4, 'STANDARD'),
                                     (5, 'DELUXE'), (6, 'DELUXE'), (7, 'DELUXE'),
                                     (8, 'SUITE'), (9, 'SUITE'), (10, 'SUITE');
