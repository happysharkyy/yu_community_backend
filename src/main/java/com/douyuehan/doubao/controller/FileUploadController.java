package com.douyuehan.doubao.controller;

import com.douyuehan.doubao.common.api.ApiResult;
import com.douyuehan.doubao.model.entity.SysFile;
import com.douyuehan.doubao.model.entity.SysUser;
import com.douyuehan.doubao.service.IUmsUserService;
import com.douyuehan.doubao.service.SysFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("upload")
public class FileUploadController {
        private final String path="E:/uploadFile/";
        @Autowired
        IUmsUserService iUmsUserService;
        @Autowired
        SysFileService sysFileService;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        @ResponseBody
        @RequestMapping(value = "/fileupload",method = RequestMethod.POST)
        public String uploadImg(HttpServletRequest request) throws IOException {
            MultipartRequest multipartRequest= (MultipartRequest) request;
            MultipartFile uploadFile = multipartRequest.getFile("file[]");
            String format = sdf.format(new Date());
            String file = path+format+"/";
            File folder = new File(file);
            if(!folder.isDirectory()) {
                folder.mkdirs();
            }
            String oldName = uploadFile.getOriginalFilename();
            //获取文件类型，以最后一个`.`为标识
            String type = oldName.substring(oldName.lastIndexOf(".") + 1);
            SysFile sysFile = new SysFile();
            sysFile.setId(0);
            sysFile.setName(oldName);
            sysFile.setLocal("/image/"+format+"/"+oldName);
            sysFile.setFileType(type);
            sysFile.setCreateTime(new Date());
            sysFileService.insert(sysFile);

            try {
                // 文件保存操作
                uploadFile.transferTo(new File(folder, oldName));
                System.out.println(folder);
                return "/image/"+format+"/"+oldName;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return "上传失败!";
        }
        @ResponseBody
        @RequestMapping(value = "/avatarUpload/{userName}",method = RequestMethod.POST)
        public ApiResult<String> avatarUpload(@RequestParam("picFile") MultipartFile picture,HttpServletRequest request, @PathVariable String userName) throws IOException {
            SysUser sysUser  = iUmsUserService.getUserByUsername(userName);
            String originalFileName = picture.getOriginalFilename();
            System.out.println("原始文件名称：" + originalFileName);

            //获取文件类型，以最后一个`.`为标识
            String type = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
            System.out.println("文件类型：" + type);
            //获取文件名称（不包含格式）
            String name = originalFileName.substring(0, originalFileName.lastIndexOf("."));

            //设置文件新名称: 当前时间+文件名称（不包含格式）
            Date d = new Date();
            String date = sdf.format(d);
            String fileName =  name + "." + type;
            System.out.println("新文件名称：" + fileName);

            //在指定路径下创建一个文件
            File targetFile = new File(path+"avatar/"+date+"/", fileName);
            File folder =  new File(path+"avatar/"+date+"/");
            if(!folder.isDirectory()) {
                folder.mkdirs();
            }
            SysFile sysFile = new SysFile();
            sysFile.setId(0);
            sysFile.setName(fileName);
            sysFile.setLocal("/image/avatar/"+date+"/"+fileName);
            sysFile.setFileType(type);
            sysFile.setCreateTime(new Date());
            sysFileService.insert(sysFile);
            sysUser.setAvatar("/image/avatar/"+date+"/"+fileName);
            iUmsUserService.updateById(sysUser);
            //将文件保存到服务器指定位置
            try {
                picture.transferTo(targetFile);
                System.out.println("上传成功");
                //将文件在服务器的存储路径返回
                return ApiResult.success("/image/avatar/"+date+"/"+fileName);
            } catch (IOException e) {
                System.out.println("上传失败");
                e.printStackTrace();
                return null;
            }
        }
    @ResponseBody
    @RequestMapping(value = "/file",method = RequestMethod.POST)
    public ApiResult<String> avatarUpload(@RequestParam("picFile") MultipartFile picture,HttpServletRequest request) throws IOException {

        String originalFileName = picture.getOriginalFilename();
        System.out.println("原始文件名称：" + originalFileName);

        //获取文件类型，以最后一个`.`为标识
        String type = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        System.out.println("文件类型：" + type);
        //获取文件名称（不包含格式）
        String name = originalFileName.substring(0, originalFileName.lastIndexOf("."));

        //设置文件新名称: 当前时间+文件名称（不包含格式）
        Date d = new Date();
        String date = sdf.format(d);
        String fileName =  name + "." + type;
        System.out.println("新文件名称：" + fileName);

        //在指定路径下创建一个文件
        File targetFile = new File(path+"series/"+date+"/", fileName);
        File folder =  new File(path+"series/"+date+"/");
        if(!folder.isDirectory()) {
            folder.mkdirs();
        }
        SysFile sysFile = new SysFile();
        sysFile.setId(0);
        sysFile.setName(fileName);
        sysFile.setLocal("/image/series/"+date+"/"+fileName);
        sysFile.setFileType(type);
        sysFile.setCreateTime(new Date());
        sysFileService.insert(sysFile);
        //将文件保存到服务器指定位置
        try {
            picture.transferTo(targetFile);
            System.out.println("上传成功");
            //将文件在服务器的存储路径返回
            return ApiResult.success("/image/series/"+date+"/"+fileName);
        } catch (IOException e) {
            System.out.println("上传失败");
            e.printStackTrace();
            return null;
        }
    }
}
