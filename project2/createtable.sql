DROP DATABASE IF EXISTS moviedb;
CREATE DATABASE moviedb;
USE moviedb;

CREATE TABLE movies(
	id VARCHAR(10) PRIMARY KEY NOT NULL,
	title VARCHAR(100) NOT NULL,
    year INTEGER NOT NULL,
    director VARCHAR(100) NOT NULL
);

CREATE TABLE stars(
    id VARCHAR(10) PRIMARY KEY NOT NULL,
    name VARCHAR(100) NOT NULL,
    birthYear INTEGER
);

CREATE TABLE stars_in_movies(
	starId VARCHAR(10) NOT NULL,
	movieId VARCHAR(10) NOT NULL,
	FOREIGN KEY(starId) REFERENCES stars(id),
	FOREIGN KEY(movieId) REFERENCES movies(id)
);

CREATE TABLE genres(
	id INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL,
	name VARCHAR(32) NOT NULL
);

CREATE TABLE genres_in_movies(
	genreId INTEGER NOT NULL,
	movieId VARCHAR(10) NOT NULL,
	FOREIGN KEY(genreId) REFERENCES genres(id),
	FOREIGN KEY(movieId) REFERENCES movies(id)
);

CREATE TABLE creditcards(
	id VARCHAR(20) PRIMARY KEY NOT NULL,
	firstName VARCHAR(50) NOT NULL,
	lastName VARCHAR(50) NOT NULL,
	expiration DATE NOT NULL
);


CREATE TABLE customers(
	id INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL, 
    firstName VARCHAR(50) NOT NULL, 
    lastName VARCHAR(50) NOT NULL, 
    ccId VARCHAR(20) NOT NULL, 
    address VARCHAR(200) NOT NULL, 
    email VARCHAR(50) NOT NULL, 
    password VARCHAR(20) NOT NULL, 
    FOREIGN KEY(ccId) REFERENCES creditcards(id)
);

CREATE TABLE sales( 
	id INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL, 
    customerId INTEGER NOT NULL, 
    movieId VARCHAR(10) NOT NULL, 
    saleDate DATE NOT NULL, 
    FOREIGN KEY(customerId) REFERENCES customers(id), 
    FOREIGN KEY(movieId) REFERENCES movies(id)
);

CREATE TABLE ratings(
	movieId VARCHAR(10) NOT NULL, 
	rating FLOAT NOT NULL, 
	numVotes INTEGER NOT NULL, 
	FOREIGN KEY(movieId) REFERENCES movies(id)
);

CREATE TABLE employees(
	email VARCHAR(50) PRIMARY KEY NOT NULL, 
	password VARCHAR(20) NOT NULL, 
	fullname VARCHAR(100)
);

INSERT IGNORE INTO employees VALUES('classta@email.edu','classta','TA CS122B');



DROP PROCEDURE IF EXISTS add_movie;

DELIMITER $$
CREATE PROCEDURE add_movie (
	IN movieTitle VARCHAR(100),
	IN movieYear INT,
	IN movieDirector VARCHAR(100),
	IN starName VARCHAR(100),
	IN starBirthYear INT,
	IN genreName VARCHAR(100), 
	OUT message VARCHAR(500))
BEGIN
	DECLARE newMovieId VARCHAR(10);
	DECLARE newStarId VARCHAR(10);
	DECLARE newGenreId INT DEFAULT 0;
	DECLARE Exist INT DEFAULT 0;
	DECLARE nextMovieId INT DEFAULT 0;
	DECLARE nextStarId INT DEFAULT 0;
	DECLARE stars_in_movies_count INT DEFAULT 0;
	DECLARE genres_in_movies_count INT DEFAULT 0;
    
    IF movieTitle = "" OR movieYear = 0 OR movieDirector = "" OR starName = "" OR genreName = "" THEN
		SET message = "All input except for star year cannot be empty\n!";
	ELSE
		SET message ="";
		#get max movieId and starId
		SELECT movieMaxId INTO nextMovieId FROM maxId;
		SELECT starMaxId INTO nextStarId FROM maxId;
		#SET nextMovieId = nextMovieId + 1;
		#SET nextStarId = nextStarId + 1;

		#check if movie exists
		SELECT movies.id, count(*) INTO newMovieId, Exist FROM movies 
        where movies.title = movieTitle AND movies.year = movieYear AND movies.director = movieDirector group by movies.id;

		IF Exist = 0 THEN
			#create new movie in movies table
            SET nextMovieId = nextMovieId + 1;
			INSERT INTO movies VALUES (concat("tt",CAST(nextMovieId AS CHAR(10))), movieTitle, movieYear, movieDirector);
			UPDATE maxId SET movieMaxId = nextMovieId;
			SET newMovieId = concat("tt",CAST(nextMovieId AS CHAR(10)));
			SET message = CONCAT(message,"New movie added!\n");
		ELSE
			SET message = CONCAT(message, "Movie exists!\n");
		END IF;

		#reset
		SET Exist = 0;

		#check if star exists
		IF starBirthYear = 0 THEN
			SELECT id, count(*) INTO newStarId, Exist FROM stars 
			WHERE name = starName GROUP BY id;
		ELSE
			SELECT stars.id, count(*) INTO newStarId, Exist FROM stars 
			WHERE stars.name = starName AND stars.birthYear = starBirthYear GROUP BY stars.id;
		END IF;

		IF Exist = 0 THEN
			#create new star in stars table
            SET nextStarId = nextStarId + 1;
			IF starBirthYear = 0 THEN
				INSERT INTO stars VALUES (concat("nm",CAST(nextStarId AS CHAR(10))), starName, NULL);
			ELSE
				INSERT INTO stars VALUES (concat("nm",CAST(nextStarId AS CHAR(10))), starName, starBirthYear);
			END IF;

			UPDATE maxId SET starMaxId = nextStarId;
			SET newStarId = concat("nm",CAST(nextStarId AS CHAR(10)));
			SET message = CONCAT(message,"New star created!\n");
		ELSE
			SET message = CONCAT(message,"Star exists!\n");
		END IF;

		#reset
		SET Exist = 0;

		#check if genre exists
		SELECT id, count(*) INTO newGenreId, Exist FROM genres 
		WHERE name = genreName GROUP BY id;

		IF Exist = 0 THEN
			#create new genre in genres table
			INSERT INTO genres VALUES (DEFAULT, genreName);
			SET message = CONCAT(message,"New genre created!\n");
			SET newGenreId = (SELECT max(id) FROM genres WHERE name = genreName);
		ELSE
			SET message = CONCAT(message,"Genre exists!\n");
		END IF;

			
		#linking
		SELECT count(*) INTO stars_in_movies_count FROM stars_in_movies WHERE 
		starId = newStarId AND movieId = newMovieId;
			
		IF stars_in_movies_count = 0 THEN
			INSERT INTO stars_in_movies VALUES (newStarId, newMovieId);
			SET message = CONCAT(message,"New stars_in_movies link created!\n");
		ELSE
			SET message = CONCAT(message,"stars_in_movies link exists!\n");
		END IF;

		SELECT count(*) INTO genres_in_movies_count FROM genres_in_movies WHERE 
		genreId = newGenreId AND movieId = newMovieId;
			
		IF genres_in_movies_count = 0 THEN
			INSERT INTO genres_in_movies VALUES (newGenreId, newMovieId);
			SET message = CONCAT(message,"New genres_in_movies link created!\n");
		ELSE
			SET message = CONCAT(message,"genres_in_movies link exists!\n");
		END IF;
	END IF;
END $$
DELIMITER ;


