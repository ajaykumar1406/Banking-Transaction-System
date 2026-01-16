create database banking_db;
use banking_db;


create table accounts(account_id int auto_increment primary key,
 account_number varchar(20) unique not null ,
 holder_name varchar(100) not null,
 pin varchar(10) not null ,
 balance double default 0,
 created_at timestamp default current_timestamp);


create table transactions( txn_id int auto_increment primary key,
account_number varchar(20),
txn_type varchar(20),
amount double,
 balance_after double,
 txn_date timestamp default current_timestamp,
 foreign key(account_number) references accounts(account_number));