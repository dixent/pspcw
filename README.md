# pspcw

/*
CLASSPATH=$CLASSPATH:/usr/share/java/mysql.jar
export CLASSPATH
*/


mysql:

create database ComputerShop;
use CimputerShop;
create table user(id int not null auto_increment, login char(20) not null, password char(50) not null, admin boolean, primary key (id));


create table computer(id int not null auto_increment, model char(20) not null, videocard char(50) not null, ram int not null, memory int not null, processor char(50) not null, active boolean not null default true, primary key (id));
