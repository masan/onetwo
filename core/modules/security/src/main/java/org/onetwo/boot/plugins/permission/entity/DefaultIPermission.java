package org.onetwo.boot.plugins.permission.entity;

import org.onetwo.boot.plugins.permission.IPermission;
import org.onetwo.boot.plugins.permission.utils.PermissionUtils;
import org.onetwo.common.utils.func.Closure1;


public interface DefaultIPermission<T extends DefaultIPermission<T>> extends IPermission<T> {
	
	default public String toTreeString(String spliter){
		final StringBuilder str = new StringBuilder();
		PermissionUtils.buildString(str, (T)this, "--", new Closure1<T>() {
			
			@Override
			public void execute(T obj) {
				if(PermissionUtils.isMenu(obj)){
					str.append(obj.getName()).append("(").append(obj.getCode()).append(")");
					str.append(":").append(obj.getUrl()==null?"":obj.getUrl() );
				}else{
					str.append(obj.getName()).append("(").append(obj.getCode()).append(")");
				}
				str.append(" -> ").append(obj.getResourcesPattern()==null?"":obj.getResourcesPattern() );
				str.append(spliter);
			}
		});
		return str.toString();
	}
}