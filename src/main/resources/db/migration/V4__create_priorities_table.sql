create sequence priorities_priority_id_seq start 1;

create table priorities (
priority_id int primary key,
priority varchar(7)
);

alter table priorities alter column priority_id set default nextval('priorities_priority_id_seq');

alter sequence priorities_priority_id_seq owned by priorities.priority_id;