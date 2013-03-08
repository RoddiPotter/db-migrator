@UP
-- a test table
CREATE table test(
	test varchar(200) not null, -- a test column 	
);
insert into test (test) values ('woohoo');
@UP

@DOWN
drop table test;
@DOWN