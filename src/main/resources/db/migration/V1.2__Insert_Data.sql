INSERT INTO eligibility_records (
    member_unique_id, first_name, last_name, date_of_birth, eligibility_start_date, eligibility_end_date, employee_status, employee_group
)
VALUES
    ('EMP001', 'John', 'Doe', '1980-01-15', '2010-06-01', '2025-12-31', 'employee', 'GROUP1'),
    ('DEP001', 'Jane', 'Doe', '2010-05-20', '2020-01-01', '2025-12-31', 'dependent', 'GROUP2'),
    ('EMP002', 'Alice', 'Smith', '1975-03-10', '2005-04-15', '2020-12-31', 'employee', 'GROUP1'),
    ('DEP002', 'Bob', 'Smith', '2000-07-15', '2010-01-01', '2024-01-01', 'dependent', 'GROUP2');