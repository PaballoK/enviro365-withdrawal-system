INSERT INTO investors (id, first_name, last_name, email, password, birth_date) VALUES
(1, 'Sipho', 'Nkosi', 'sipho.nkosi@gmail.com', '$2a$10$Z1.wfCHZVH/39jaYwz/2/OiIh3TyHltmyHLUY2mmayK2oSGhk.nKS', '1955-03-15'),
(2, 'Thandi', 'Mokoena', 'thandi.mokoena@gmail.com', '$2a$10$Z1.wfCHZVH/39jaYwz/2/OiIh3TyHltmyHLUY2mmayK2oSGhk.nKS', '1985-07-22'),
(3, 'Pieter', 'van der Merwe', 'pieter.vdm@gmail.com', '$2a$10$Z1.wfCHZVH/39jaYwz/2/OiIh3TyHltmyHLUY2mmayK2oSGhk.nKS', '1960-11-08');

INSERT INTO products (id, product_name, product_type, balance, investor_id) VALUES
(1, 'Enviro365 Retirement Annuity', 'RETIREMENT', 850000.00, 1),
(2, 'Enviro365 Tax-Free Savings Account', 'SAVINGS', 120000.00, 1),
(3, 'Enviro365 Retirement Annuity', 'RETIREMENT', 95000.00, 2),
(4, 'Enviro365 Easy Savings Account', 'SAVINGS', 45000.00, 2),
(5, 'Enviro365 Retirement Annuity', 'RETIREMENT', 620000.00, 3),
(6, 'Enviro365 Balanced Growth Fund', 'INVESTMENT', 180000.00, 3);
