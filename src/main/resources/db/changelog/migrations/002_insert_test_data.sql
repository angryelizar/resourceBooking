--liquibase formatted sql
--changeset angryelizar:002_insert_test_data

-- Заполнение БД тестовыми данными

-- Таблица authorities (роли)
-- В рамках системы существует две роли - администратор и пользователь
insert into authorities(authority)
VALUES ('ADMIN'),
       ('USER');

-- Таблица users (пользователи)
-- Пароль от обоих юзеров - qwerty
-- Шифрование bcrypt
insert into users(name, surname, email, password, authority_id, is_enabled)
VALUES ('Елизар', 'Коновалов', 'conovalov.elizar@gmail.com',
        '$2a$12$R8T1RRsUxhN12UpXCcd11eQ/jvM6ZXKa8Os8J7RxI5doC.hbH7aWW',
        (select id from authorities where authority = 'ADMIN'), true),
       ('Ормон', 'Сааданбеков', 'saadanbekov@gmail.com', '$2a$12$R8T1RRsUxhN12UpXCcd11eQ/jvM6ZXKa8Os8J7RxI5doC.hbH7aWW',
        (select id from authorities where authority = 'USER'), true);

-- Таблица resources (ресурсы)
insert into resources(title, description, is_active, hourly_rate, created_at, updated_at, updated_by)
VALUES ('Большой зал', 'Государственная резиденция Президента КР №2', true, 500, '2024-01-01 10:00:00',
        '2024-01-01 12:00:00', (select id from users where email = 'conovalov.elizar@gmail.com')),
       ('зал ololoPlanet', 'ololo', true, 100, '2024-01-01 10:00:00', '2024-01-01 12:00:00',
        (select id from users where email = 'conovalov.elizar@gmail.com')),
       ('Иссык-Куль', 'Гостиница, Бишкек', false, 50, '2014-01-01 10:00:00', '2014-01-01 10:15:00',
        (select id from users where email = 'conovalov.elizar@gmail.com'));

-- Таблица bookings (бронирования)
insert into bookings(start_date, end_date, is_confirmed, resource_id, author_id, created_at, updated_at, updated_by)
VALUES ('2024-10-20 10:00:00', '2024-10-20 20:00:00', true, (select id from resources where title = 'Большой зал'),
        (select id from users where email = 'saadanbekov@gmail.com'), '2024-10-14 10:00:00', '2024-10-14 10:00:00',
        (select id from users where email = 'saadanbekov@gmail.com')),
       ('2024-10-30 11:00:00', '2024-10-30 12:00:00', false, (select id from resources where title = 'зал ololoPlanet'),
        (select id from users where email = 'saadanbekov@gmail.com'), '2024-10-14 09:00:00', '2024-10-14 09:00:00',
        (select id from users where email = 'saadanbekov@gmail.com'));

-- Таблица payment_statuses
-- Платежи в системе могут иметь несколько состояний - ожидает оплату, подтвержден, отменен
insert into payment_statuses(status)
VALUES ('PENDING'),
       ('CONFIRMED'),
       ('CANCELED');

-- Таблица payment_methods
-- Перечисление методов оплаты, а также демонстрация примеров данных для оплаты по тому или иному методу оплаты
insert into payment_methods(title, description, credentials_example)
VALUES ('Банковская карта', 'VISA, Mastercard, Элкарт', '4169963312341234'),
       ('PayPal', 'Электронная почта', 'paypal@paypal.com');

-- Таблица payments (платежи)
insert into payments (booking_id, method_id, created_at, updated_at, updated_by, status_id, credentials, amount)
values ((select id from bookings where resource_id = (select id from resources where title = 'Большой зал')),
        (select id from payment_methods where title = 'Банковская карта'), '2024-10-14 10:05:00', '2024-10-14 10:10:00',
        (select id from users where email = 'saadanbekov@gmail.com'),
        (select id from payment_statuses where status = 'CONFIRMED'), '4169963312341234', 5000),
       ((select id from bookings where resource_id = (select id from resources where title = 'зал ololoPlanet')),
        (select id from payment_methods where title = 'PayPal'), '2024-10-14 09:10:00', '2024-10-14 09:15:00',
        (select id from users where email = 'saadanbekov@gmail.com'),
        (select id from payment_statuses where status = 'CANCELED'), 'paypal@paypal.com', 100);