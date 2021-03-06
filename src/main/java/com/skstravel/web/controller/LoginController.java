package com.skstravel.web.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.skstravel.common.api.Constants;
import com.skstravel.common.tools.CookieUtils2;
import com.skstravel.common.utils.sendMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.skstravel.common.service.ISkBearerInfoService;
import com.skstravel.common.service.ISkUserAddressService;
import com.skstravel.common.utils.CookieUtils;
import com.skstravel.common.utils.MD5Utils;
import com.skstravel.service.UserService;


/**
 * @author jefferyChang
 *         <p>
 *         2017年12月28日
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private ISkBearerInfoService skBearerInfoService;

    @Autowired
    private JdbcTemplate jdbcTemplateForSksports2;

    @Autowired
    private ISkUserAddressService skUserAddressService;

    @Autowired
    private UserService userService;


    @RequestMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        String phone = request.getParameter("mobilePhone");
        String mobileValidateCode = request.getParameter("mobileValidateCode");
        LOGGER.debug("****************************************************");
        if (StringUtils.isBlank(phone) || StringUtils.isBlank(mobileValidateCode)) {
            String msg = "<font color='red'>手机号或者手机短信验证码错误，请稍后重试！！</font>";
            request.setAttribute("errorMsg", msg);
            return "login";
        }
        Object MobileCode1 = request.getSession().getAttribute("MobileCode");
        if(MobileCode1==null||mobileValidateCode==null){
            String flag="<font color='red'>请获取手机验证码，进行登录</font>";
            request.setAttribute("mobilePhone",phone);
            model.addAttribute("errorMsg",flag);
            return "login";
        }
        //销毁session
        if (mobileValidateCode.equals(MobileCode1.toString())) {
            request.getSession().setAttribute("MobileCode", "");
            String sql = "SELECT user_name userName,user_id userId FROM sk_users WHERE mobile_phone = ? ";
            List<Map<String, Object>> list = jdbcTemplateForSksports2.queryForList(sql, phone);
            List<Map<String, Object>> list1 = jdbcTemplateForSksports2.queryForList(sql, "86"+phone);
            String userName = "";
            if (list.size() > 0) {

                userName = (String) list.get(0).get("userName");
                LOGGER.debug("userName============="+userName);
                request.setAttribute("userName", userName);
                CookieUtils.setCookie("memberId", list.get(0).get("userId").toString(), -1, response, Constants.domain);
                return "center";
            } else if(list1.size() > 0){
                userName = (String) list1.get(0).get("userName");
                request.setAttribute("userName", userName);
                CookieUtils.setCookie("memberId", list1.get(0).get("userId").toString(), -1, response, Constants.domain);

                return "center";
            }else{
                String msg = "<font color='red'>您的手机号还没有注册，请先注册！</font>";
                request.setAttribute("errorMsg", msg);
                return "login";
            }

        }else{
            LOGGER.debug("************************99999****************************");
            String msg = "<font color='red'>系统异常，请稍后重试！！</font>";
            request.setAttribute("errorMsg", msg);
            return "login";
        }
    }


    @RequestMapping("/loginOut")
    public String loginOut(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {


        return "center";
    }

    /***
     * 用户信息注册
     * @param request
     * @param response
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping("/register")
    public void register(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        //0.获取令牌,判断令牌
        String r_code = request.getParameter("r_code");
        String s_code = (String) request.getSession().getAttribute("s_code");

        //0.1 保证商品只添加一次 需要保证令牌只能使用一次
        request.getSession().removeAttribute("s_code");
        //0.2判断code是否一致
        if(s_code == null || s_code == null || !s_code.equals(r_code)){
            request.setAttribute("exception", "页面已经提交过了，请去登录!!");
            request.getRequestDispatcher("/modules/error.jsp").forward(request, response);
            return;
        }
        String mobilePhone = request.getParameter("mobilePhone");
        String password1 = request.getParameter("password");
        if(StringUtils.isNotBlank(mobilePhone)&&StringUtils.isNotBlank(password1)){
            String password = MD5Utils.md5(password1);
            String validateCode = request.getParameter("checkValidateCode");
            String mobileValidateCode = request.getParameter("mobileValidateCode");
            if (StringUtils.isNoneBlank(validateCode) && StringUtils.isNotBlank(mobileValidateCode)) {
                Integer MobileCode1 = (Integer) request.getSession().getAttribute("MobileCode");
                //销毁session  短信验证码
                request.getSession().setAttribute("MobileCode", "");
                String MobileCode = String.valueOf(MobileCode1);
                //验证码
                String msg = (String) request.getSession().getAttribute("msg");
                if (MobileCode.equalsIgnoreCase(mobileValidateCode) && msg.equalsIgnoreCase(validateCode)) {
                    //销毁验证码
                    request.getSession().setAttribute("msg", "");
                    try {
                        Integer id= userService.register(mobilePhone, password);
                        if(id!=0){
                            String userId=id.toString();
                            CookieUtils2.setCookie(request,response,"memberId",userId,3600);
                            request.setAttribute("userName", mobilePhone);
                            request.getRequestDispatcher("/modules/center.jsp").forward(request, response);
                        }else{
                            request.setAttribute("errorMsg","对不起，您已经注册过，请直接登录！");
                            request.getRequestDispatcher("/modules/login.jsp").forward(request, response);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        request.getRequestDispatcher("/modules/error.jsp").forward(request, response);
                    }
                } else {
                    request.getRequestDispatcher("/modules/login.jsp").forward(request, response);
                }
            } else {
                request.getRequestDispatcher("/modules/login.jsp").forward(request, response);
            }

        }else{
            request.getRequestDispatcher("/modules/error.jsp").forward(request,response);
        }




    }


    /**
     * 跳转到登录页面
     *
     * @param request
     * @param response
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping("/registerUI")
    public String registerUI(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {


        return "register";
    }







    @RequestMapping("/getMobileCode")
    public void getMobileCode(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        //1.获取手机号码
        String mobilePhone = request.getParameter("mobilePhone");
        int mobileCode = (int) ((Math.random() * 9 + 1) * 10000);
        System.out.println(mobileCode);
        request.getSession().setAttribute("MobileCode", mobileCode);
        //2.调用发送短信功能
        String flag = "0";
        try {
           // String result = sendMessage.getMobileCode(mobilePhone, mobileCode);
            String result = "success";
            if("success".equals(result)){
                flag="1";
            }
        } catch (Exception e) {
            flag = "0";
            e.printStackTrace();
        }
        response.getWriter().write(flag);
    }

    @RequestMapping("/validateCode")
    public void validateCode(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        int height = 30;
        int width = 60;
        String data = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();


        //2 创建一个图片
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        //3 获得画板
        Graphics g = image.getGraphics();
        //4 填充一个矩形
        // * 设置颜色
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        g.setColor(Color.WHITE);
        g.fillRect(1, 1, width - 2, height - 2);
        // * 设置字体
        g.setFont(new Font("楷体", Font.BOLD | Font.ITALIC, 25));

        //正确验证码
        String msg = "";
        //5 写随机字
        for (int i = 0; i < 4; i++) {
            // 设置颜色--随机数
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));

            // 获得随机字
            int index = random.nextInt(data.length());
            String str = data.substring(index, index + 1);
            //拼接正确的字符
            msg += str;
            // 写入
            g.drawString(str, width / 6 * (i + 1), 20);

        }
        //共享正确验证码，session
        request.getSession().setAttribute("msg", msg);

        //6 干扰线
        for (int i = 0; i < 2; i++) {
            // 设置颜色--随机数
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            // 随机绘制先
            g.drawLine(random.nextInt(width), random.nextInt(height), random.nextInt(width), random.nextInt(height));
            // 随机点
            g.drawOval(random.nextInt(width), random.nextInt(height), 2, 2);
        }


        //end 将图片响应给浏览器
        ImageIO.write(image, "jpg", response.getOutputStream());
    }


}
