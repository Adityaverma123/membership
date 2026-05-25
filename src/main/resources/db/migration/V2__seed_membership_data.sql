insert into membership_plan (id, name, description, duration, status, price)
values
    (1, 'Monthly Membership', 'Monthly FirstClub membership plan', 'MONTHLY', 'ACTIVE', 199.00),
    (2, 'Quarterly Membership', 'Quarterly FirstClub membership plan', 'QUARTERLY', 'ACTIVE', 499.00),
    (3, 'Yearly Membership', 'Yearly FirstClub membership plan', 'YEARLY', 'ACTIVE', 1499.00);

alter table membership_plan alter column id restart with 4;

insert into membership_tier (id, name, type, description, status)
values
    (1, 'Silver', 'SILVER', 'Default tier for active members', 'ACTIVE'),
    (2, 'Gold', 'GOLD', 'Higher tier for engaged members', 'ACTIVE'),
    (3, 'Platinum', 'PLATINUM', 'Highest tier for premium members', 'ACTIVE');

alter table membership_tier alter column id restart with 4;

insert into membership_tier_rules (membership_tier_id, cohorts, min_order_value, min_order_count, status)
values
    (1, JSON '["DEFAULT"]', 0.00, 0, 'ACTIVE'),
    (2, JSON '["DEFAULT"]', 5000.00, 5, 'ACTIVE'),
    (3, JSON '["DEFAULT", "HIGH_INTENT"]', 15000.00, 12, 'ACTIVE');
