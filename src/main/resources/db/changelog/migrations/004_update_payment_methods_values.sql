--liquibase formatted sql
--changeset angryelizar:004_insert_more_values_to_the_payment_methods

-- В процессе разработки стало понятно, что помимо информации о методах оплаты для юзеров
-- есть необходимость определенных обозначений, следовательно нужна новая колонка в таблице
-- Ее и было решено добавить, теперь нужно обновить значения записей в таблице

UPDATE payment_methods SET code = 'CARD'
where title = 'Банковская карта';

UPDATE payment_methods SET code = 'PAYPAL'
where title = 'PayPal';
