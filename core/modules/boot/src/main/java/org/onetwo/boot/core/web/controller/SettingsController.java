package org.onetwo.boot.core.web.controller;

import org.onetwo.boot.core.web.service.impl.SettingsManager;
import org.onetwo.boot.core.web.utils.BootWebUtils;
import org.onetwo.common.spring.mvc.utils.DataResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author wayshall
 * <br/>
 */
@RequestMapping(BootWebUtils.CONTROLLER_PREFIX)
public class SettingsController extends AbstractBaseController {
	
	@Autowired
	private SettingsManager settingsManager;
	
	@PutMapping("/setting/{configName}")
	@ResponseBody
	public Object update(@PathVariable("configName") String configName, @RequestParam("value") String value){
		settingsManager.updateConfig(configName, value);
		return DataResults.success("change config success!").build();
	}
	
	@PutMapping("/settings/reset")
	@ResponseBody
	public Object reset(){
		settingsManager.reset();
		return DataResults.success("reset settings success!").build();
	}


}
