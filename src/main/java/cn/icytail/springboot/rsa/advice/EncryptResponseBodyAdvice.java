package cn.icytail.springboot.rsa.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.alibaba.fastjson.JSON;

import cn.icytail.springboot.rsa.annotation.Encrypt;
import cn.icytail.springboot.rsa.config.SecretKeyConfig;
import cn.icytail.springboot.rsa.util.AESUtil;
import cn.icytail.springboot.rsa.util.Base64Util;
import cn.icytail.springboot.rsa.util.RSAUtil;

/**
 * Author:Bobby DateTime:2019/4/9
 **/
@ControllerAdvice
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Object> {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private boolean encrypt;

	@Autowired
	private SecretKeyConfig secretKeyConfig;

	private static ThreadLocal<Boolean> encryptLocal = new ThreadLocal<>();

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		encrypt = false;
		if (returnType.getMethod().isAnnotationPresent(Encrypt.class) && secretKeyConfig.isOpen()) {
			encrypt = true;
		}
		return encrypt;
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		// EncryptResponseBodyAdvice.setEncryptStatus(false);
		// Dynamic Settings Not Encrypted
		Boolean status = encryptLocal.get();
		if (null != status && !status) {
			encryptLocal.remove();
			return body;
		}
		if (encrypt) {
			String publicKey = secretKeyConfig.getPublicKey();
			String aesHeaderName = secretKeyConfig.getAesHeaderName();
			try {
				String content = JSON.toJSONString(body);

				if (!StringUtils.hasText(aesHeaderName)) {
					throw new NullPointerException("Please configure rsa.encrypt.aesHeaderName parameter!");
				}

				if (!StringUtils.hasText(publicKey)) {
					throw new NullPointerException("Please configure rsa.encrypt.privatekeyc parameter!");
				}

				byte[] aeskey = AESUtil.generateDesKey();

				byte[] encodedData = AESUtil.encrypt(content, aeskey);
				String result = Base64Util.encode(encodedData);
				if (secretKeyConfig.isShowLog()) {
					log.info("Pre-encrypted data：{}，After encryption：{}", content, result);
				}

				String aeskeyEncodedData = Base64Util.encode(RSAUtil.encrypt(aeskey, publicKey));
				response.getHeaders().add(aesHeaderName, aeskeyEncodedData);
				return result;
			} catch (Exception e) {
				log.error("Encrypted data exception", e);
			}
		}
		return body;
	}
}
