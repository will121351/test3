package com.jspxcms.common.security;

/**
 * 证书加密
 *
 * @author liufang
 */
public interface CredentialsDigest {
    String digest(String plainCredentials, byte[] salt);

    boolean matches(String credentials, String plainCredentials, byte[] salt);
}
