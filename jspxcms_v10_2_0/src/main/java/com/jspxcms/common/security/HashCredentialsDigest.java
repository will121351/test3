package com.jspxcms.common.security;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * Hash 证书加密
 *
 * @author liufang
 */
public abstract class HashCredentialsDigest implements CredentialsDigest {
    static final int HASH_ITERATIONS = 1024;

    public String digest(String plainCredentials, byte[] salt) {
        if (StringUtils.isBlank(plainCredentials)) return null;
        byte[] hashPassword = digest(plainCredentials.getBytes(StandardCharsets.UTF_8), salt);
        return Hex.encodeHexString(hashPassword);
    }

    public boolean matches(String credentials, String plainCredentials, byte[] salt) {
        if (StringUtils.isBlank(credentials) && StringUtils.isBlank(plainCredentials)) return true;
        return StringUtils.equals(credentials, digest(plainCredentials, salt));
    }

    protected abstract byte[] digest(byte[] input, byte[] salt);
}
