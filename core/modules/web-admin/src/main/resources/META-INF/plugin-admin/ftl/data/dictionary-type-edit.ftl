
<@extends>	

	
	<@override name="title">
		编辑字典类型
	</@override>
	
 	<@override name="main-content">
 
 	<@widget.form name="dictionary" action="${pluginConfig.baseURL}/data/dictionary-type/${id}" method="put" label="新增Dictionary">
 		<#include "${pluginConfig.templateBasePath}/data/dictionary-type-form.ftl"/>
 	</@widget.form>
	
  </@override>
  
</@extends>