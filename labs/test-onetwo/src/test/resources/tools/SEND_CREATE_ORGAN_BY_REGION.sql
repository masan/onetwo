USE [ICCARD]
GO
/****** Object:  StoredProcedure [dbo].[PROC_CREATE_ORGAN_BY_REGION]    Script Date: 01/15/2014 11:11:04 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
--交易记录上传  消费记录插入数据库  充值数据更新TAC
ALTER PROCEDURE [dbo].[PROC_CREATE_ORGAN_BY_REGION]
(	
) AS
BEGIN TRY

--创建机构
--没表，select into from
select ID, 
NAME= CASE CLEVEL
		WHEN 'CITY' THEN NAME+'社保卡服务中心'
		WHEN 'DISTRICT' THEN NAME+'社保股'
		WHEN 'TOWN' THEN NAME+'人社所'
		WHEN 'VILLAGE' THEN NAME+'居委'
		ELSE
			NAME
		END
, ID AS REGION_ID, CCODE AS REGION_CODE, CLEVEL AS ORG_LEVEL,
'GOVERMENT' AS ORGAN_TYPE, PARENT_ID, 'NORMAL' AS STATUS, 
NULL AS ADDRESS,NULL AS LINK_MAN, NULL AS LINK_TEL,NULL AS EMAIL,
NULL AS REMARK,GETDATE() AS CREATE_TIME, GETDATE() AS LAST_UPDATE_TIME
into admin_organ_bak
from ADMIN_REGION ;


---创建用户，注意主键不要重复
drop table admin_user_bak;
select 
USER_ID = IDENTITY(bigint, 10000, 1) , name as user_name, name as nick_name, 
'{SHA}fEqNCco3Yq9h5ZUglD3CZJT4lBs=' as password, 'NORMAL' as status, id as ORGAN_ID, 'cardmanage' as APP_CODE
into dbo.admin_user_bak 
from admin_organ

insert into admin_user(id, user_name, nick_name, password, status, ORGAN_ID, APP_CODE)
select * from admin_user_bak;


-----绑定用户和角色，注意主键不要重复
insert into admin_user_role (user_id, role_id)
select au.id, ar.id from admin_user au 
left join ADMIN_ORGAN aorg on aorg.ID=au.ORGAN_ID
left join admin_role ar on ar.code='ORG_'+aorg.ORG_LEVEL
where au.id>=10000 and au.id<=11348;



---删除表
drop table admin_user_bak;
drop table admin_organ_bak;

END TRY
BEGIN CATCH 
	ROLLBACK TRANSACTION;
	PRINT ERROR_MESSAGE();
	PRINT ERROR_STATE();
	PRINT ERROR_LINE();
	RETURN -1;  
END CATCH
