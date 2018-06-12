package cn.qiuyiping.wechat.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileController {

    @RequestMapping("/create")
    public String createFile(HttpServletRequest request) {
        String path = request.getSession().getServletContext().getRealPath("/"); //文件存储位置

        File dir = new File(path + "excel/");
        if(!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(path + "excel/" + "tt.txt");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "over";
    }

}
