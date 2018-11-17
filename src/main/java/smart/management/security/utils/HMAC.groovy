package smart.management.security.utils

import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class HMAC {

    public static final String HMAC_MD5 = 'HmacMD5'
    public static final String HMAC_SHA1 = 'HmacSHA1'
    public static final String HMAC_SHA256 = 'HmacSHA256'
    public static final String HMAC_SHA384 = 'HmacSHA384'
    public static final String HMAC_SHA512 = 'HmacSHA512'

    static String generateKey (String algorithm) {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm)
        return Base64.getEncoder().encodeToString(keyGenerator.generateKey().getEncoded())
    }

    static String digest (String str, String key, String algorithm) {
        SecretKey secretKey = new SecretKeySpec(Base64.getDecoder().decode(key), algorithm)
        Mac mac = Mac.getInstance(algorithm)
        mac.init(secretKey)
        return Base64.getEncoder().encodeToString(mac.doFinal(str.getBytes()))
    }

    static Boolean validate (String str, String key, String algorithm, String expect) {
        boolean result = false
        try {
            result = digest(str, key, algorithm) == expect
        } catch (Exception e) {
            result = false
        } finally {
            return result
        }
    }

}
