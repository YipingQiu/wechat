package cn.qiuyiping.wechat.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token")
public class TokenController {

    @RequestMapping("/validate")
    String validate(String signature, String timestamp, String nonce, String echostr) {
        return echostr;
    }

}
