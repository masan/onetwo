USE [ICCARD]
GO
/****** Object:  StoredProcedure [dbo].[SEND_CREATE_BATCH_BY_ACARD]    Script Date: 01/15/2014 11:11:04 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
--交易记录上传  消费记录插入数据库  充值数据更新TAC
ALTER PROCEDURE [dbo].[SEND_CREATE_BATCH_BY_ACARD]
(	
) AS

DECLARE @V_TOWN_NAME VARCHAR(100);
BEGIN TRY
	BEGIN TRANSACTION
	
	SET @V_TOWN_NAME = '青莲镇';
	
	--根据acard数据创建临时批次
	drop table send_batch_temp
	
	SELECT 
		BT.*,
		substring(TOWN_CODE, 5, 20)+CARD_BATCH_NO SEND_BATCH_NO ,
		substring(TOWN_CODE, 5, 20)+CARD_BATCH_NO+(REPLICATE('0', 5-LEN(cast(ID AS VARCHAR)))+cast(ID AS VARCHAR)) SEND_BATCH_NO2 ,
		getdate() as CREATE_TIME,
		getdate() as LAST_UPDATE_TIME
	into send_batch_temp
	FROM (
		select 
			--IDENTITY(bigint, 1, 1) as ID,
			row_number() over(order by  rand() ) as ID,
			sa.batchcode as CARD_BATCH_NO, 
			sa.countyname as DISTRICT_NAME, sa.countycode as DISTRICT_CODE, 
			sa.townname as TOWN_NAME, sa.towncode as TOWN_CODE, 
			sa.villagename as VILLAGE_NAME, sa.villagecode as VILLAGE_CODE,
			COUNT(*) as CARD_COUNT,
			'CITY_RECEIVE' AS STATUS,
			'VILLAGE' as BATCH_TYPE
		from 
			send_acard sa
		where 
			sa.townname=@V_TOWN_NAME and sa.cardtype=3
		group by
				sa.batchcode, sa.countyname, sa.countycode, sa.townname, sa.towncode, sa.villagename, sa.villagecode
	) BT;
	
	--把临时批次表的数据插入到批次表
	insert into send_batch(
		[ID]
		  ,[CARD_BATCH_NO]
		  ,[DISTRICT_NAME]
		  ,[DISTRICT_CODE]
		  ,[TOWN_NAME]
		  ,[TOWN_CODE]
		  ,[VILLAGE_NAME]
		  ,[VILLAGE_CODE]
		  ,[CARD_COUNT]
		  ,[STATUS]
		  ,[BATCH_TYPE]
		  ,[SEND_BATCH_NO]
		  ,[SEND_BATCH_NO2]
		  ,[CREATE_TIME]
		  ,[LAST_UPDATE_TIME]
	)
	select 
			[ID]
		  ,[CARD_BATCH_NO]
		  ,[DISTRICT_NAME]
		  ,[DISTRICT_CODE]
		  ,[TOWN_NAME]
		  ,[TOWN_CODE]
		  ,[VILLAGE_NAME]
		  ,[VILLAGE_CODE]
		  ,[CARD_COUNT]
		  ,[STATUS]
		  ,[BATCH_TYPE]
		  ,[SEND_BATCH_NO]
		  ,[SEND_BATCH_NO2]
		  ,[CREATE_TIME]
		  ,[LAST_UPDATE_TIME]
	from 
	   send_batch_temp;
	
	--创建明细
	 insert into 
		SEND_BATCH_USER(
			ID,
			BATCH_ID,
			STATUS,
			USER_TYPE,
			ID_NAME,
			ID_NO,
			ADDRESS,
			BIG_BOX_NO,
			BOX_NO,
			DISTRICT_NAME,
			TOWN_NAME,
			VILLAGE_NAME,
			CARD_BATCH_NO
		)
		select 
			row_number() over(order by  rand() ) as ID,
			sbt.id as BATCH_ID,
			sbt.status as STATUS,
			sbt.BATCH_TYPE as USER_TYPE,
			sa.name as ID_NAME,
			sa.idcard as ID_NO,
			sa.telnum as ADDRESS,
			sa.boxcode as BIG_BOX_NO,
			sa.packagecode as BOX_NO,
			sa.countyname as DISTRICT_NAME,
			sa.townname as TOWN_NAME, 
			sa.villagename as VILLAGE_NAME,
			sbt.card_batch_no as CARD_BATCH_NO 
		   from send_acard sa
		  left join send_batch_temp sbt on 
				( sa.batchcode = sbt.CARD_BATCH_NO
				and sa.countyname = sbt.DISTRICT_NAME
				and sa.countycode = sbt.DISTRICT_CODE
				and sa.townname = sbt.TOWN_NAME
				and sa.towncode = sbt.TOWN_CODE
				and sa.villagename = sbt.VILLAGE_NAME
				and sa.villagecode = sbt.VILLAGE_CODE
				)
			where 
			sbt.id is not null
			and sa.townname=@V_TOWN_NAME and sa.cardtype=3
			order by sbt.id
	
	---更新批次为正确地区代码
	update sb set sb.TOWN_CODE=ar.CCODE
	from SEND_BATCH_USER sbu
	left join SEND_BATCH sb on sb.ID=sbu.BATCH_ID
	left join ADMIN_REGION ar on sbu.town_name = ar.NAME
	where ar.NAME is not null
   
	--删除临时表   
	drop table send_batch_temp;
   COMMIT TRANSACTION;
   
END TRY
BEGIN CATCH 
	ROLLBACK TRANSACTION;
	PRINT ERROR_MESSAGE();
	PRINT ERROR_STATE();
	PRINT ERROR_LINE();
	RETURN -1;  
END CATCH
