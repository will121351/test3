package com.jspxcms.common.security;

/**
 * SHA512 证书加密
 * 
 * @author liufang
 * 
 */
public class SHA512CredentialsDigest extends HashCredentialsDigest {
	@Override
	protected byte[] digest(byte[] input, byte[] salt) {
		return Digests.sha512(input, salt, HASH_ITERATIONS);
	}
}