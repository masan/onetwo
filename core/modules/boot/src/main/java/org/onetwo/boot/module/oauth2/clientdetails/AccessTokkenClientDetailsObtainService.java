package org.onetwo.boot.module.oauth2.clientdetails;

import java.io.Serializable;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.onetwo.boot.module.oauth2.util.OAuth2Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * 这个解释器只负责解释出clientDetail，不做任何验证检查，这些应该在网关层做
 * @author wayshall
 * <br/>
 */
public class AccessTokkenClientDetailsObtainService implements ClientDetailsObtainService  {

	@Autowired
	private TokenStore tokenStore;
	private TokenExtractor tokenExtractor = new BearerTokenExtractor();
	private ClientDetailConverter clientDetailConverter;

	
	@Override
	public Optional<String> getTokenValue(HttpServletRequest request){
		return OAuth2Utils.getAccessTokenValue(tokenExtractor, request);
	}
	
	@Override
	public Optional<? extends Serializable> resolveAndStoreClientDetails(HttpServletRequest request) {
		Optional<String> at = getTokenValue(request);
		if(!at.isPresent()){
			return Optional.empty();
		}
		return OAuth2Utils.getOrSetClientDetails(request, ()->{
			return resolveClientDetails(at.get());
		});
	}

	@Override
	public Serializable resolveClientDetails(String accessTokenValue) {
		OAuth2AccessToken accessToken = tokenStore.readAccessToken(accessTokenValue);
		OAuth2Authentication authentication = tokenStore.readAuthentication(accessTokenValue);
		if(clientDetailConverter!=null){
			return clientDetailConverter.convert(accessToken, authentication);
		}
		return authentication;
	}
	

	public void setClientDetailConverter(ClientDetailConverter clientDetailConverter) {
		this.clientDetailConverter = clientDetailConverter;
	}
	

}
