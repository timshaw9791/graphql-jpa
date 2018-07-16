-- Insert Code Lists
insert into code_list (id, type, code, description, sequence, active) values
    (0, 'org.crygier.graphql.model.starwars.Gender', 'Male', 'Male', 1, true),
    (1, 'org.crygier.graphql.model.starwars.Gender', 'Female', 'Female', 2, true);

-- Insert Droids
insert into character (id, name, primary_function, dtype) values
    ('2000', 'C-3PO', 'Protocol', 'Droid'),
    ('2001', 'R2-D2', 'Astromech', 'Droid');

-- Insert Humans
insert into character (id, name, home_planet, favorite_droid_id, dtype, gender_code_id) values
    ('1000', 'Luke Skywalker', 'Tatooine', '2000', 'Human', 0),
    ('1001', 'Darth Vader', 'Tatooine', '2001', 'Human', 0),
    ('1002', 'Han Solo', NULL, NULL, 'Human', 0),
    ('1003', 'Leia Organa', 'Alderaan', NULL, 'Human', 1),
    ('1004', 'Wilhuff Tarkin', NULL, NULL, 'Human', 0);

-- Luke's friends
insert into character_friends (source_id, friend_id) values
    ('1000', '1002'),
    ('1000', '1003'),
    ('1000', '2000'),
    ('1000', '2001');

-- Luke Appears in
insert into character_appears_in (character_id, appears_in) values
    ('1000', 3),
    ('1000', 4),
    ('1000', 5),
    ('1000', 6);

-- Vader's friends
insert into character_friends (source_id, friend_id) values
    ('1001', '1004');

-- Vader Appears in
insert into character_appears_in (character_id, appears_in) values
    ('1001', 3),
    ('1001', 4),
    ('1001', 5);

-- Solo's friends
insert into character_friends (source_id, friend_id) values
    ('1002', '1000'),
    ('1002', '1003'),
    ('1002', '2001');

-- Solo Appears in
insert into character_appears_in (character_id, appears_in) values
    ('1002', 3),
    ('1002', 4),
    ('1002', 5),
    ('1002', 6);

-- Leia's friends
insert into character_friends (source_id, friend_id) values
    ('1003', '1000'),
    ('1003', '1002'),
    ('1003', '2000'),
    ('1003', '2001');

-- Leia Appears in
insert into character_appears_in (character_id, appears_in) values
    ('1003', 3),
    ('1003', 4),
    ('1003', 5),
    ('1003', 6);

-- Wilhuff's friends
insert into character_friends (source_id, friend_id) values
    ('1004', '1001');

-- Wilhuff Appears in
insert into character_appears_in (character_id, appears_in) values
    ('1004', 3);

-- C3PO's friends
insert into character_friends (source_id, friend_id) values
    ('2000', '1000'),
    ('2000', '1002'),
    ('2000', '1003'),
    ('2000', '2001');

-- C3PO Appears in
insert into character_appears_in (character_id, appears_in) values
    ('2000', 3),
    ('2000', 4),
    ('2000', 5),
    ('2000', 6);

-- R2's friends
insert into character_friends (source_id, friend_id) values
    ('2001', '1000'),
    ('2001', '1002'),
    ('2001', '1003');

-- R2 Appears in
insert into character_appears_in (character_id, appears_in) values
    ('2001', 3),
    ('2001', 4),
    ('2001', 5),
    ('2001', 6);

-- Things
insert into thing (id, type) values
    ('2D1EBC5B7D2741979CF0E84451C5BBB1', 'Thing1');

-- User
insert into user(id, first_name, last_name,CREATETIME,updatetime,number) values
	('1000','Bob', 'Austin',0,0,'1000'),('1001','Tim', 'Shaw',0,0,'1001');

insert into spaceship(id, name, createddate,createduser) values
	('1000','X-Wing', sysdate, '1000');

	-- Privi
insert into privi(id, name) values
	('1000','删除用户功能' ),('1001','查看用户功能'),('1002','修改用户功能');

	-- role
insert into roles(id, name) values
	('1000','管理员角色' ),('1001','guest角色'),('1002','普通角色');

-- RolePrevilegeItem
insert into role_previlege_item(id, parent_id, privi_id) values
	('1000','1000', '1000'),('1001','1000', '1001'),('1002','1000', '1002'),
	--('1003','1001', '1001'),
	('1004','1002', '1001'),('1005','1002', '1002');


-- UserRoleItem
insert into User_Role_Item(id, parent_id, role_id) values
	('1000','1000', '1000'),('1001','1000', '1001'),('1002','1000', '1002');
	--('1004','1001', '1001'),('1005','1001', '1002');


insert into department(createactorid, createtime, number, updateactorid, updatetime, id) values
 (null, 0, 'software', null, 0, 'Vub1QmDPFbOGU_I0L9r-S2A03'), (null, 0, 'auditing', null, 0, 'Vub1xmDPFbOGU_I0L9r-S2A03');


