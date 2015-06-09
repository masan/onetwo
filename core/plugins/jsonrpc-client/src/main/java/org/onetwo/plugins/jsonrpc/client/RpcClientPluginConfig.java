package org.onetwo.plugins.jsonrpc.client;

import org.onetwo.common.spring.plugin.AbstractLoadingConfig;
import org.onetwo.common.utils.LangUtils;
import org.onetwo.common.utils.propconf.JFishProperties;

public class RpcClientPluginConfig extends AbstractLoadingConfig {
	
	private String[] rpcClientPackages;
	private String rpcServerEndpoint;
	private boolean rpcClientScanable;
	
	@Override
	protected void initConfig(JFishProperties config) {
		rpcClientPackages = config.getStringArray("rpc.client.packages", ",");
		rpcClientScanable = !LangUtils.isEmpty(rpcClientPackages);
		if(rpcClientScanable){
			rpcServerEndpoint = config.getAndThrowIfEmpty("rpc.server.endpoint");
		}
//		System.out.println("rpcServerEndpoint:"+rpcServerEndpoint);
	}

	public String[] getRpcClientPackages() {
		return rpcClientPackages;
	}

	public String getRpcServerEndpoint() {
		return rpcServerEndpoint;
	}

	public boolean isRpcClientScanable() {
		return rpcClientScanable;
	}

}
