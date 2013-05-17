package org.onetwo.common.utils.propconf;

import org.onetwo.common.db.ExtQueryUtils;
import org.onetwo.common.utils.LangUtils;
import org.onetwo.common.utils.StringUtils;

public class JFishPropertyInfo extends JFishNameValuePair {
	private String mappedEntity;
	private String countSql;
	private boolean ignoreNull;
	
	private Class<?> mappedEntityClass;

	public String getSql() {
		return getValue();
	}

	public void setSql(String sql) {
		this.setValue(sql);
	}

	public String getMappedEntity() {
		return mappedEntity;
	}

	public Class<?> getMappedEntityClass() {
		return mappedEntityClass;
	}

	public void setMappedEntity(String mappedEntity) {
		this.mappedEntity = mappedEntity;
	}

	public String getCountSql() {
		if(StringUtils.isBlank(countSql)){
			this.countSql = ExtQueryUtils.buildCountSql(this.getSql(), "");
		}
		return countSql;
	}

	public void setCountSql(String countSql) {
		this.countSql = countSql;
	}

	public boolean isIgnoreNull() {
		return ignoreNull;
	}

	public void setIgnoreNull(boolean ignoreNull) {
		this.ignoreNull = ignoreNull;
	}
	
	public boolean isNeedParseSql(){
		return isIgnoreNull();
	}

	public String toString() {
		return LangUtils.append("{name:", getName(), ", mappedEntity:", mappedEntity, ", sql:", getSql(), ", countSql:", countSql, "}");
	}
}