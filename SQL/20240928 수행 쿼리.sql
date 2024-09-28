create table date_weather (
	date DATE not null primary key,
    weather varchar(50) not null,
    icon varchar(50) not null,
    temperature double not null
);

select * from date_weather;