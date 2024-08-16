create sequence statuses_status_id_seq start 1;

create table statuses (
status_id int primary key,
status varchar(10)
);

alter table statuses alter column status_id set default nextval('statuses_status_id_seq');

alter sequence statuses_status_id_seq owned by statuses.status_id;