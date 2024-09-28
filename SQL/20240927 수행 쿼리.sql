create database project;

use project;

create table memo (
	id INT not null primary key auto_increment,
    text varchar(50) not null
);

create table diary (
	id INT not null primary key auto_increment,
    weather varchar(50) not null,
    icon varchar(50) not null,
    temperature varchar(50) not null,
    text varchar(50) not null,
    date DATE not null
);

select * from diary;