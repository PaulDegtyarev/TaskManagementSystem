create sequence accounts_account_id_seq start 1;

create table accounts (
account_id int primary key,
email varchar(256),
password text,
firstname varchar(255),
lastname varchar(255),
role_id int,
constraint unique_accounts_email unique(email),
constraint fk_roles_role_id foreign key (role_id) references roles (role_id)
);

alter table accounts alter column account_id set default nextval('accounts_account_id_seq');

alter sequence accounts_account_id_seq owned by accounts.account_id;