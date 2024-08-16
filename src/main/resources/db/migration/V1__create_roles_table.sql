create sequence roles_role_id_seq start 1;

create table roles (
role_id int primary key,
role varchar(13)
);

alter table roles alter column role_id set default nextval('roles_role_id_seq');

alter sequence roles_role_id_seq owned by roles.role_id;