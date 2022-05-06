
CREATE TABLE  users(
    id  SERIAL PRIMARY KEY,
    name varchar(250),
    contactNumber varchar(20),
    email varchar(50),
    password varchar(250),
    status varchar(20),
    role varchar(20),
    UNIQUE (email)
);

INSERT INTO users(name,contactNumber,email,password,status,role) VALUES('Peter Ngu','123333','peterngu@gmail.com','mypass','true','admin');