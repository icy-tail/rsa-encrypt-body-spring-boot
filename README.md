### 1.介绍
**rsa-encrypt-spring-boot**  
Spring Boot接口加密，可以对返回值、参数值通过注解的方式自动加解密。

### 2.使用方法

[Spring Boot接口RSA自动加解密](https://www.shuibo.cn/102.html)

- **启动类Application中添加@EnableSecurity注解**

```
@SpringBootApplication
@EnableSecurity
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```
- **在application.yml或者application.properties中添加RSA公钥及私钥**

```
rsa:
  encrypt:
    open: true # 是否开启加密 true  or  false
    showLog: true # 是否打印加解密log true  or  false
    publicKey: # RSA公钥
    privateKey: # RSA私钥
```
- **对返回值进行加密**

```
@Encrypt
@GetMapping("/encryption")
public TestBean encryption(){
    TestBean testBean = new TestBean();
    testBean.setName("shuibo.cn");
    testBean.setAge(18);
    return testBean;
}
```
- **对传过来的加密参数解密**

```
@Decrypt
@PostMapping("/decryption")
public String Decryption(@RequestBody TestBean testBean){
    return testBean.toString();
}
```
### 3.About author
 [![](https://img.shields.io/badge/Author-Bobby-ff69b4.svg)]()
- Blog：https://www.shuibo.cn/102.html
- Fork：https://github.com/ishuibo/rsa-encrypt-body-spring-boot
