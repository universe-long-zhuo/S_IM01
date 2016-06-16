package vaint.wyt.encrypt;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import sun.misc.BASE64Decoder;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 * 加密工具类
 * 
 * @author Vaint
 * @E-mail vaintwyt@163.com
 * 
 */
public class EncryptUtils {
	/** 服务器端RSA解密的私钥 ,与客户端的公钥对应*/
	private static final String privateKeyStr = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALR0tNc4I3tUwzgxtvGRUjAhGYSPOUolJt33u6haLC3fP9DW7gNA19NV14u0MgQV5hTPX+Yk+W0x2VQMHz9g7Yx8ee+KmTgML4LuZnkOA//lC2CW+XpqU9MvbKFk2knkwIlA29iTuXkDWUPBLAUqQWYGXa8d4ZjvoL9jrah95lhHAgMBAAECgYEAqpnjFc0HDmP2I7wsXniqoMHKJB5bZRN2iUbZ7LFDLyLua/umDQFSiYOQQY1b86zYVjgvS58NCASml+TV7c8vA5care9HqpQX+1YdBUB1Hw+zloIwJD5894123wumRpbHBK1vkgvX3cKz0K+lNHfnXoov49DLvDeTCtBb9GQQX2ECQQDv1avuF4DlbVavIuTNZFw5YntZM13E2M+JMx84XGODvncuDl8C/TpW+ZsaaKqU7caXtWl2p5ip4UZPYLwyRh65AkEAwJ51buJ5qZhA7kD6is4RC4P7F2LwGkhKrpXxlzHsPeW8m9if62E7AH3B8MGicIVeQjJwxSrsiI3wYEHDlIyu/wJAN7ZzEgPztVgI4vZAIFZH9iyiar478hZLX5u4jOcpVtlP5isAdzlL7Bhfp2rY9W+mymch8KZOGGh0ZMwb67HOQQJAcOw04mXpd3CoGEWF3FxEh+C/Eo3RP0dEaSfEs6Pz4LHPqfoMfvzIj1gqm8+ZQKgfg2V40U6BzuiPlI7Zbzwu1wJAMbq668GPCzMgc0LLImkGTaOPcmjPYUbAYXa4k/90M3sX0t6s9u0kl9NfotSpF9M3AdbFSdKWXoY8XScOkhnpQQ==";

	/**
	 * 生成天翼短信验证码下发接口要求的sign数据
	 * 
	 * @param par
	 * @param strkey
	 * @return HMAC_SHA1+BASE64之后的数据
	 */
	public static String getSmsSign(TreeMap par, String strkey) {
		String sign = "";
		Iterator it = par.entrySet().iterator();
		String nosha = "";
		while (it.hasNext()) {
			// entry的输出结果如key0=value0等
			Map.Entry entry = (Map.Entry) it.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			nosha += "&" + key.toString() + "=" + value.toString();
		}
		nosha = nosha.substring(1);

		sign = Base64.encode(SHA1.getHmacSHA1(nosha, strkey));

		return sign;
	}

	/**
	 * RSA解密<BR/>
	 * <BR/>
	 * 用于将客户端上传的，经过RSA加密的参数进行解密
	 * 
	 * @param src
	 * @return 解密后的数据
	 */
	public static String GetRsaDecrypt(String src) {
		String deData = null;
		try {
			Cipher cipher = Cipher.getInstance("RSA",
					new BouncyCastleProvider());
			RSAPrivateKey priKey = GetPrivateKey(privateKeyStr);
			cipher.init(Cipher.DECRYPT_MODE, priKey);
			byte[] output = cipher.doFinal(StringToByte(src));
			deData = new String(output, "utf-8");// 此处如果不指定编码格式，则会产生中文乱码
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deData;
	}

	/**
	 * 将String型私钥转换为RSAPrivateKey
	 * 
	 * @param privateKeyStr
	 * @return
	 */
	private static RSAPrivateKey GetPrivateKey(String privateKeyStr) {
		RSAPrivateKey priKey = null;
		try {
			BASE64Decoder base64Decoder = new BASE64Decoder();
			byte[] buffer = base64Decoder.decodeBuffer(privateKeyStr);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			priKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return priKey;
	}

	private static byte[] StringToByte(String str) {
		int len = str.length();
		byte[] data = new byte[len / 2];
		char[] ch = str.toCharArray();
		String tmp;
		int i = 0, j = 0;
		while (i < len) {
			tmp = String.valueOf(ch[i++]);
			int high = Integer.parseInt(tmp, 16);
			tmp = String.valueOf(ch[i++]);
			int low = Integer.parseInt(tmp, 16);

			data[j] = (byte) ((high << 4) + low);
			j++;

		}
		return data;
	}
}
