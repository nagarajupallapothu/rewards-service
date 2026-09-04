INSERT INTO customers (customer_id, customer_name, email)
VALUES ('CUST001', 'Ravi Kumar', 'ravi@example.com');

INSERT INTO customers (customer_id, customer_name, email)
VALUES ('CUST002', 'Priya Sharma', 'priya@example.com');

INSERT INTO customers (customer_id, customer_name, email)
VALUES ('CUST003', 'Arjun Reddy', 'arjun@example.com');

INSERT INTO transactions
(transaction_id, customer_id, transaction_date, amount)
VALUES
('TXN001', 'CUST001', '2026-06-10', 40.00);

INSERT INTO transactions
(transaction_id, customer_id, transaction_date, amount)
VALUES
('TXN002', 'CUST001', '2026-06-15', 60.00);

INSERT INTO transactions
(transaction_id, customer_id, transaction_date, amount)
VALUES
('TXN003', 'CUST001', '2026-06-20', 120.00);

INSERT INTO transactions
(transaction_id, customer_id, transaction_date, amount)
VALUES
('TXN004', 'CUST001', '2026-07-10', 75.00);

INSERT INTO transactions
(transaction_id, customer_id, transaction_date, amount)
VALUES
('TXN005', 'CUST001', '2026-07-20', 100.00);

INSERT INTO transactions
(transaction_id, customer_id, transaction_date, amount)
VALUES
('TXN006', 'CUST001', '2026-08-10', 150.00);


INSERT INTO transactions
(transaction_id, customer_id, transaction_date, amount)
VALUES
('TXN007', 'CUST002', '2026-06-05', 50.00);

INSERT INTO transactions
(transaction_id, customer_id, transaction_date, amount)
VALUES
('TXN008', 'CUST002', '2026-06-18', 200.00);

INSERT INTO transactions
(transaction_id, customer_id, transaction_date, amount)
VALUES
('TXN009', 'CUST002', '2026-07-15', 80.00);

INSERT INTO transactions
(transaction_id, customer_id, transaction_date, amount)
VALUES
('TXN010', 'CUST002', '2026-08-15', 110.00);


INSERT INTO transactions
(transaction_id, customer_id, transaction_date, amount)
VALUES
('TXN011', 'CUST003', '2026-06-05', 30.00);

INSERT INTO transactions
(transaction_id, customer_id, transaction_date, amount)
VALUES
('TXN012', 'CUST003', '2026-07-15', 45.00);

INSERT INTO transactions
(transaction_id, customer_id, transaction_date, amount)
VALUES
('TXN013', 'CUST003', '2026-08-20', 49.00);