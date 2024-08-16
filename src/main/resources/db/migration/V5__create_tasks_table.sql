create sequence tasks_task_id_seq start 1;

create table tasks (
task_id int primary key,
title varchar(255),
description text,
status_id int,
priority_id int,
author_id int,
executor_id int,
comment text,
constraint fk_statuses_status_id foreign key (status_id) references statuses (status_id),
constraint fk_priorities_priority_id foreign key (priority_id) references priorities (priority_id),
constraint fk_accounts_account_id_for_author foreign key (author_id) references accounts (account_id),
constraint fk_accounts_account_id_for_executor foreign key (executor_id) references accounts (account_id)
);

alter table tasks alter column task_id set default nextval('tasks_task_id_seq');

alter sequence tasks_task_id_seq owned by tasks.task_id;