package org.onetwo.common.excel;

import org.onetwo.common.jackson.JsonMapper;
import org.onetwo.common.web.view.jsp.datagrid.AbstractDatagridRenderListener;
import org.onetwo.common.web.view.jsp.datagrid.DataGridTag;
import org.onetwo.common.web.view.jsp.grid.FieldTagBean;
import org.onetwo.common.web.view.jsp.grid.GridTagBean;
import org.onetwo.common.web.view.jsp.grid.RowTagBean;

public class DatagridExcelModelBuilder extends AbstractDatagridRenderListener {

	@Override
	public void afterRender(DataGridTag tag, GridTagBean tagBean) {
		if(!tagBean.isExportable())
			return ;
		WorkbookModel workbook = new WorkbookModel();
		TemplateModel template = new TemplateModel();
		template.setLabel(tagBean.getLabel());
		template.setName(tagBean.getName());
		for(RowTagBean rowTag : tagBean.getRows()){
			RowModel row = buildRow(rowTag);
			template.addRow(row);
		}
		workbook.addSheet(template);
		JsonMapper jsonMapper = JsonMapper.ignoreEmpty()
											.filter(ExcelUtils.JSON_FILTER_TEMPLATE, "multiSheet", "varName")
											.filter(ExcelUtils.JSON_FILTER_ROW, "row", "title")
											.filter(ExcelUtils.JSON_FILTER_FIELD, "space", "height", "columnTotal", "rowTotal", "var", "rowField", "range");
		String json = jsonMapper.toJson(workbook);
		tag.write("<input name=exporter value='"+json+"'/>");
	}
	
	private RowModel buildRow(RowTagBean rowTag){
		RowModel row = new RowModel();
		row.setType(rowTag.getType().toString());
		row.setName(rowTag.getName());
		for(FieldTagBean fieldTag : rowTag.getFields()){
			row.addField(buildField(fieldTag));
		}
		return row;
	}
	
	private FieldModel buildField(FieldTagBean fieldTag){
		FieldModel field = new FieldModel();
		field.setName(fieldTag.getName());
		field.setValue(fieldTag.getValue());
		field.setLabel(fieldTag.getLabel());
		return field;
	}
	

}
