package cn.icytail.springboot.rsa.advice;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.util.CollectionUtils;

import cn.icytail.springboot.rsa.util.AESUtil;
import cn.icytail.springboot.rsa.util.Base64Util;
import cn.icytail.springboot.rsa.util.RSAUtil;

/**
 * Author:Bobby DateTime:2019/4/9
 **/
public class DecryptHttpInputMessage implements HttpInputMessage {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private HttpHeaders headers;
	private InputStream body;

	public DecryptHttpInputMessage(HttpInputMessage inputMessage, String aesHeaderName, String privateKey,
			String charset, boolean showLog) throws Exception {

		if (StringUtils.isEmpty(aesHeaderName)) {
			throw new IllegalArgumentException("aesHeaderName is null");
		}
		if (StringUtils.isEmpty(privateKey)) {
			throw new IllegalArgumentException("privateKey is null");
		}

		this.headers = inputMessage.getHeaders();
		List<String> aeskeys = this.headers.get(aesHeaderName);
		if (CollectionUtils.isEmpty(aeskeys)) {
			throw new IllegalArgumentException("header aes keys is empty");
		}

		String content = new BufferedReader(new InputStreamReader(inputMessage.getBody())).lines()
				.collect(Collectors.joining(System.lineSeparator()));
		String decryptBody;
		if (content.startsWith("{")) {
			log.info("Unencrypted without decryption:{}", content);
			decryptBody = content;
		} else {
			byte[] aeskeybytes = Base64Util.decode(aeskeys.get(0));
			aeskeybytes = RSAUtil.decrypt(aeskeybytes, privateKey);

			StringBuilder json = new StringBuilder();
			content = content.replaceAll(" ", "+");
			if (!StringUtils.isEmpty(content)) {
				String value = new String(AESUtil.decrypt(Base64Util.decode(content), aeskeybytes), charset);
				json.append(value);
			}
			decryptBody = json.toString();
			if (showLog) {
				log.info("Encrypted data received：{},After decryption：{}", content, decryptBody);
			}
		}
		this.body = new ByteArrayInputStream(decryptBody.getBytes());
	}

	@Override
	public InputStream getBody() {
		return body;
	}

	@Override
	public HttpHeaders getHeaders() {
		return headers;
	}
}
