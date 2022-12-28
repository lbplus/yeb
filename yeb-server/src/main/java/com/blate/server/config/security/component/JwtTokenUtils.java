/**
 * @author Blate
 * @BiuBiuBiu ****************常用快捷键如下****************
 * ctrl+J：显示所有快捷键
 * itit: 快速出while循环
 * ctrl+alt+L: 格式化代码
 * ctrl+alt+T:捕获异常
 * ctrl+B:追当前光标所指方法
 * ctrl+shift+T:创建测试方法
 * ctrl+h/r:批量查找替换
 * ctrl+shift+h/r:大批量查找替换
 * /**+回车:快速生成方法注释
 * ctrl+F12:查看类结构
 * ctrl+alt+V:自动出返回值
 * Alt+enter:自动出方法或者实现接口
 * ctrl+shift+Y:翻译
 * ctrl+p:查看填入参数提示
 * ctrl+d:快速复制手动替换
 * control+option+0:清理无用的包
 * control+h:查看类继承图
 * command+n:生成构造函数
 * control+0:快速选择要覆盖的方法
 */
package com.blate.server.config.security.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JwtToken工具类
 *
 * @author blate
 * @since 1.0.0
 */
@Component
public class JwtTokenUtils {

    //荷载claim的名称(用户的Key)
    private static final String CLAIM_KEY_USERNAME = "sub";
    //荷载的创建时间(jwt创建时间)
    private static final String CLAIM_KEY_CREATED = "created";
    // jwt令牌的秘钥
    @Value("${jwt.secret}")
    private String secret;
    //jwt的失效时间
    @Value("${jwt.expiration}")
    private Long expiration;


    /**
     * 根据用户信息生成Token
     * 用户信息是 Security中的 UserDetails 的username
     * 实际过程是:用户信息---->载荷---->Token
     *
     * @param userDetails
     * @return
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    /**
     * 根据荷载生成token
     *
     * @param claims
     * @return
     */
    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder()//创建对象
                .setClaims(claims)//设置载荷（也就是用户信息的map数据结构）
                .setExpiration(generateExpirationDate())//设置过期时间
                .signWith(SignatureAlgorithm.HS512, secret)//签名
                .compact();//序列化为紧凑的字符串
    }

    /**
     * 从token中获取登录用户名
     * 通过荷载 claims 就可以拿到用户名
     * 获取用户信息也是如此：Token---->载荷---->用户信息
     *
     * @param token
     * @return
     */
    public String getUserNameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 根据token获取荷载
     *
     * @param token
     * @return
     */
    private Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()//返回一个可以配置然后用于解析 JWT 字符串的新JwtParser实例
                    .setSigningKey(secret)//解密秘钥
                    .parseClaimsJws(token)//根据构建器的当前配置状态解析指定的紧凑序列化 JWS 字符串并返回生成的 Claims JWS 实例。
                    .getBody();//返回 JWT 主体，可以是String或Claims实例。
        } catch (Exception e) {
            e.printStackTrace();
        }
        return claims;
    }

    /**
     * token是否有效（比较token提取的用户名必须是是当前登录用户并且时间没有过期）
     *
     * @param token
     * @param userDetails
     * @return
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        String username = getUserNameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * 判断token是否失效
     * 如果token有效的时间在当前时间之前就表示失效（比如载荷里面的时间是2.23日，但是当前时间是2.24，所以过期）
     *
     * @param token
     * @return
     */
    private boolean isTokenExpired(String token) {
        Date expireDate = getExpiredDateFromToken(token);
        return expireDate.before(new Date());
    }

    /**
     * 生成token失效时间（当前毫秒时间时间加上我们设置的时间毫秒）
     *
     * @return
     */
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    /**
     * 从token中获取失效(过期)时间
     * 先从token里面获取荷载，因为token的过期时间有对应的数据,设置过的,荷载里面就有设置过的数据
     *
     * @param token
     * @return
     */
    private Date getExpiredDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * 判断token是否可以被刷新
     * 如果过期了就可以被刷新，如果没过期就不能被刷新
     *
     * @param token
     * @return
     */
    public boolean canRefresh(String token) {
        return !isTokenExpired(token);
    }

    /**
     * 刷新token
     * 将创建时间改成当前时间，就相当于刷新了
     *
     * @param token
     * @return
     */
    public String refreshToken(String token) {
        Claims claims = getClaimsFromToken(token);
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

}
