create database poker;
create database user_service;
create database wallet_service;
create database operator_service;

grant all on poker.* to 'poker'@'localhost' identified by 'poker';
grant all on user_service.* to 'poker'@'localhost' identified by 'poker';
grant all on wallet_service.* to 'poker'@'localhost' identified by 'poker';
grant all on operator_service.* to 'poker'@'localhost' identified by 'poker';

