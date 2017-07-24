package org.onetwo.common.md;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.onetwo.common.utils.LangUtils;
import org.onetwo.common.utils.StringUtils;

import com.google.common.base.Charsets;

abstract public class Hashs {
	private static final Map<String, MessageDigestHasher> encrypts;
//	public static final String BASE64_POSTFIX = MDEncryptImpl.BASE64_POSTFIX;

	public static final MessageDigestHasher MD5 = MessageDigestHasherBuilder.newBuilder("MD5")
																			.size(16)
																			.withLabel(false)
																			.build();
	public static final MessageDigestHasher SHA = MessageDigestHasherBuilder.newBuilder("SHA")
																			.size(20)
																			.withLabel(false)
																			.build();
	
	static {
		Map<String, MessageDigestHasher> map = new HashMap<String, MessageDigestHasher>();
		MessageDigestHasher md5 = MessageDigestHasherBuilder.newBuilder("MD5")
															.size(16)
															.withLabel(true)
															.build();//16bytes, 128bits
		MessageDigestHasher sha = MessageDigestHasherBuilder.newBuilder("SHA")
															.size(20)
															.withLabel(true)
															.build();//sha-1,  20bytes, 160bits
		map.put("MD5", md5);
		map.put("SHA", sha);
		map.put("SMD5", md5);
		map.put("SSHA", sha);
		map.put("CLEARTEXT", new ClearTextMDEncryptImpl());
		map.put("PLAIN", new ClearTextMDEncryptImpl("PLAIN"));
		
		/*MDEncrypt md5Base64 = new MDEncryptImpl("MD5", 16, true);
		MDEncrypt shaBase64 = new MDEncryptImpl("SHA", 20, true);
		map.put("MD5"+BASE64_POSTFIX, md5Base64);
		map.put("SHA"+BASE64_POSTFIX, shaBase64);*/
		
		encrypts = Collections.unmodifiableMap(map);
	}
	
	public static MessageDigestHasher getMDEncrypt(){
		return getMDEncrypt(null);
	}

	
	/*public static MDEncrypt getMDEncryptWithBase64(String algorithm){
		if(StringUtils.isBlank(algorithm))
			algorithm = "SHA"+BASE64_POSTFIX;
		else
			algorithm += BASE64_POSTFIX;
		return getMDEncrypt(algorithm);
	}*/
	
	public static MessageDigestHasher getMDEncrypt(String algorithm){
		if(StringUtils.isBlank(algorithm))
			algorithm = "SHA";
		if(algorithm.startsWith("{") && algorithm.endsWith("}"))
			algorithm = algorithm.substring(1, algorithm.length()-1);
		MessageDigestHasher encrypt = encrypts.get(algorithm.toUpperCase());
		if(encrypt==null){
			LangUtils.throwBaseException("不支持这个算法：" + algorithm);
		}
		return encrypt;
	}
	
	public static boolean checkEncrypt(String source, String encrypt){
		String label = MessageDigestHashUtils.getLabel(encrypt, "{md5}");
		MessageDigestHasher md = getMDEncrypt(label);
		return md.checkHash(source, encrypt);
	}

	
	public static class MessageDigestHasherBuilder {
		
		public static MessageDigestHasherBuilder newBuilder(String algorithm){
			return new MessageDigestHasherBuilder(algorithm);
		}

		private String algorithm;
		private String charset = Charsets.UTF_8.name();
		private int size;
		private CodeType codeType = CodeType.HEX;
		private boolean withLabel;
		
		public MessageDigestHasherBuilder(String algorithm) {
			super();
			this.algorithm = algorithm;
		}
		
		public MessageDigestHasherBuilder charset(String charset) {
			this.charset = charset;
			return this;
		}

		public MessageDigestHasherBuilder codeType(CodeType codeType) {
			this.codeType = codeType;
			return this;
		}

		public MessageDigestHasherBuilder withLabel(boolean withLabel) {
			this.withLabel = withLabel;
			return this;
		}

		public MessageDigestHasherBuilder size(int size) {
			this.size = size;
			return this;
		}

		public MessageDigestHasher build(){
			MessageDigestHasherImpl hasher = new MessageDigestHasherImpl(algorithm, size);
			hasher.setWithLabel(withLabel);
			hasher.setCharset(charset);
			hasher.setCodeType(codeType);
			return hasher;
		}
	}
}