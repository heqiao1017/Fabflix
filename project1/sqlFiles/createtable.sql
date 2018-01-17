use moviedb;

create table movies(
	id varchar(10) primary key not null,
	title varchar(100) not null,
    year integer not null,
    director varchar(100) not null
    );

create table stars(
    id varchar(10) primary key not null,
    name varchar(100) not null,
    birthyear integer
    );

create table stars_in_movies(
	starid varchar(10) not null,
	movieid varchar(10) not null,
	foreign key(starid) references stars(id),
	foreign key(movieid) references movies(id)
	);

create table genres(
	id integer primary key auto_increment not null,
	name varchar(32) not null
	);

create table genres_in_movies(
	genreid integer not null,
	movieid varchar(10) not null,
	foreign key(genreid) references genres(id),
	foreign key(movieid) references movies(id)
	);

create table creditcards(
	id varchar(20) primary key not null,
	firstname varchar(50) not null,
	lastname varchar(50) not null,
	expiration date not null
	);


create table customers(
	id integer primary key auto_increment not null, 
    firstname varchar(50) not null, 
    lastname varchar(50) not null, 
    ccid varchar(20) not null, 
    address varchar(200) not null, 
    email varchar(50) not null, 
    password varchar(20) not null, 
    foreign key(ccid) references creditcards(id)
    );

create table sales( 
	id integer primary key auto_increment not null, 
    customerid integer not null, 
    movieid varchar(10) not null, 
    saledate date not null, 
    foreign key(customerid) references customers(id), 
    foreign key(movieid) references movies(id)
    );

create table ratings(
	movieid varchar(10) not null, 
	rating float not null, 
	numvotes integer not null, 
	foreign key(movieid) references movies(id)
	);
