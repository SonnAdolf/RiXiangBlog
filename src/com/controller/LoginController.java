package com.controller;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.entity.User;
import com.message.bean.SimpleBackMessage;
import com.service.UserService;
import com.util.CaptchaUtil;
import com.util.MessageUtil;
import com.util.Principal;
import com.util.StringUtill;

/**
 * 
* @ClassName: LoginController 
* @Description:Login CONTROLLER类
* @author 无名
* @date 2016-4-25 下午2:52:03 2016-05-02 具体编码 2016-05-07验证码相关
* @version 1.0
 */
@Controller
@RequestMapping("/login")
public class LoginController
{
    @Resource(name = "userServiceImpl")
    private UserService userService;
    
    @RequestMapping(value = "/show", method = RequestMethod.GET)
    public String show()throws Exception
    {
        return "loginPage";
    }
    
    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    @ResponseBody
    public void captcha(HttpServletRequest request, HttpServletResponse response)
    		throws ServletException, IOException 
    {
    	CaptchaUtil.outputCaptcha(request, response);
    }
    
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject submit(HttpServletRequest request,User user,String captcha) throws Exception
    {
		JSONObject jo = new JSONObject();	
		HttpSession session = request.getSession();
    	SimpleBackMessage loginMessage = checkUserInfor(user,captcha,session);
    	MessageUtil.setJSONObject(jo,loginMessage);
    	if(!loginMessage.isSuccess())
    	{
            return jo;
    	}
    	session.setAttribute(User.PRINCIPAL_ATTRIBUTE_NAME,
    			              new Principal(user.getId(),user.getUsername()));
        return jo;
    }
    
    private SimpleBackMessage checkUserInfor(User user,String captcha,HttpSession session)
    {
    	SimpleBackMessage backMessage = checkInput(new SimpleBackMessage(),user,captcha);
    	if(!backMessage.isSuccess())
    	{
    		return backMessage;
    	}
    	backMessage = checkCaptcha(backMessage,captcha,session);
    	if(!backMessage.isSuccess())
    	{
    		return backMessage;
    	}
    	backMessage = CheckUserNameAndPassword(backMessage,user);
    	if(!backMessage.isSuccess())
    	{
    		return backMessage;
    	}
    	return backMessage;
    }
    
    /*
     * 检查输入信息
     */
    private SimpleBackMessage checkInput(SimpleBackMessage backMessage,
    		                     User user,String captcha)
    {
    	if(StringUtill.isStringEmpty(captcha))
    	{
    		MessageUtil.setSimpleBackMessage(backMessage, false, "Please input captcha!");
    		return backMessage;
    	}
    	if(null == user||StringUtill.isStringEmpty(user.getPassword())
    			||StringUtill.isStringEmpty(user.getUsername()))
    	{
    		MessageUtil.setSimpleBackMessage(backMessage, false, "Input Wrong!");
    		return backMessage;
    	}
    	return backMessage;
    }
    
    /*
     * 检查验证码
     */
    private SimpleBackMessage checkCaptcha(SimpleBackMessage backMessage,
    		              String captcha,HttpSession session)
    {
    	String captchaInSession = (String) session.getAttribute("randomString");
    	if(StringUtill.isStringEmpty(captchaInSession))
    	{
    		MessageUtil.setSimpleBackMessage(backMessage, false,"System captcha error");
    		return backMessage;
    	}
    	if(!captchaInSession.equals(captcha))
    	{
    		MessageUtil.setSimpleBackMessage(backMessage, false,"Captcha wrong");
    		return backMessage;
    	}
    	return backMessage;
    }
    
    /**
     * 检查用户名
     */
    private SimpleBackMessage CheckUserNameAndPassword(SimpleBackMessage backMessage,User user)
    {
    	List<User> users = userService.findByUserName(user.getUsername());
    	if(users.isEmpty())
    	{
    		MessageUtil.setSimpleBackMessage(backMessage, false, 
    				                "This username doesn't exist!");
    		return backMessage;
    	}
    	User userFromDB = users.get(0);
    	if(!userFromDB.getPassword().equals(user.getPassword()))
    	{
    		MessageUtil.setSimpleBackMessage(backMessage, false, 
	                "Password is wrong!");
    		return backMessage;
    	}
    	MessageUtil.setSimpleBackMessage(backMessage, true, 
    			             "Welcome to RiXiangBlog!");
		return backMessage;
    }
}
