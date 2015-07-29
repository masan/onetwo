package org.onetwo.boot.plugins.permission.service;

import java.util.Collection;

import org.onetwo.common.utils.UserDetail;

public interface MenuItemRepository<T> {

//	@PreAuthorize("hasRole('ADMIN')")
	public Collection<T> findAllMenus();
	public Collection<T> findUserMenus(UserDetail loginUser);

}
