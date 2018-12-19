# pspcw

/*
CLASSPATH=$CLASSPATH:/usr/share/java/mysql.jar
export CLASSPATH
*/


mysql:

create database ComputerShop;
use CimputerShop;
create table users(id int not null auto_increment, login char(20) not null, password char(50) not null, admin boolean not null default false, primary key (id));


create table computers(id int not null auto_increment, model char(20) not null, videocard char(50) not null, ram int not null, memory int not null, processor char(50) not null, active boolean not null default true, primary key (id));

alter table computers add column user_id int not null default 0;
alter table computers add foreign key (user_id) references users(id);
