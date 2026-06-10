INSERT INTO investors (id, first_name, last_name, email, password, birth_date) VALUES
(1, 'Sipho', 'Nkosi', 'sipho.nkosi@gmail.com', '$2a$10$Z1.wfCHZVH/39jaYwz/2/OiIh3TyHltmyHLUY2mmayK2oSGhk.nKS', '1955-03-15'),
(2, 'Thandi', 'Mokoena', 'thandi.mokoena@gmail.com', '$2a$10$Z1.wfCHZVH/39jaYwz/2/OiIh3TyHltmyHLUY2mmayK2oSGhk.nKS', '1985-07-22'),
(3, 'Pieter', 'van der Merwe', 'pieter.vdm@gmail.com', '$2a$10$Z1.wfCHZVH/39jaYwz/2/OiIh3TyHltmyHLUY2mmayK2oSGhk.nKS', '1960-11-08');

INSERT INTO products (id, product_name, product_type, balance, investor_id) VALUES
(1, 'Sipho Retirement Annuity - Old Mutual', 'RETIREMENT', 850000.00, 1),
(2, 'Sipho Tax-Free Savings Account - FNB', 'SAVINGS', 120000.00, 1),
(3, 'Thandi Retirement Portfolio - Allan Gray', 'RETIREMENT', 95000.00, 2),
(4, 'Thandi Easy Save Account - Standard Bank', 'SAVINGS', 45000.00, 2),
(5, 'Pieter Pension Fund - Sanlam', 'RETIREMENT', 620000.00, 3),
(6, 'Pieter Balanced Investment Portfolio - PSG Wealth', 'INVESTMENT', 180000.00, 3);
