-- Insert Code Lists


-- User
insert into user(id, first_name, last_name,CREATETIME,updatetime,number,disabled) values
	('1000','Bob', 'Austin',0,0,'1000',false),('1001','Tim', 'Shaw',0,0,'1001',false);



	-- Privi
insert into privi(id, name,createactorid, createtime, number, updateactorid, updatetime,disabled) values
	('1000','删除用户功能',null,0,'1000',null,0 ,false),('1001','查看用户功能',null,0,'1001',null,0,false),('1002','修改用户功能',null,0,'1002',null,0,false);

	-- role
insert into roles(id, name,createactorid, createtime, number, updateactorid, updatetime,disabled) values
	('1000','管理员角色' ,null,0,'1000',null,0,false),('1001','guest角色',null,0,'1001',null,0,false),('1002','普通角色',null,0,'1002',null,0,false);

-- RolePrevilegeItem
insert into role_previlege_item(id, parent_id, privi_id) values
	('1000','1000', '1000'),('1001','1000', '1001'),('1002','1000', '1002'),
	--('1003','1001', '1001'),
	('1004','1002', '1001'),('1005','1002', '1002');


-- UserRoleItem
insert into User_Role_Item(id, parent_id, role_id) values
	('1000','1000', '1000'),('1001','1000', '1001'),('1002','1000', '1002');
	--('1004','1001', '1001'),('1005','1001', '1002');


--insert into department(createactorid, createtime, number, updateactorid, updatetime, id) values
-- (null, 0, 'software', null, 0, 'Vub1QmDPFbOGU_I0L9r-S2A03'), (null, 0, 'auditing', null, 0, 'Vub1xmDPFbOGU_I0L9r-S2A03');


