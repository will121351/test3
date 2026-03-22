package com.jspxcms.common.security;

/**
 * SHA256 证书加密
 * 
 * @author liufang
 * 
 */
public class SHA256CredentialsDigest extends HashCredentialsDigest {
	@Override
	protected byte[] digest(byte[] input, byte[] salt) {
		return Digests.sha256(input, salt, HASH_ITERATIONS);
	}
}