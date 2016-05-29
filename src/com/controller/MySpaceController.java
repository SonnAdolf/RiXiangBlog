package com.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.entity.Article;
import com.entity.User;
import com.service.ArticleService;
import com.util.Page;
import com.util.PageInfo;
import com.util.Principal;

/**
* @ClassName: MySpaceController 
* @Description: 个人空间
* @author 无名
* @date 2016-5-21 下午6:38:00 
* @version 1.0
 */
@Controller
@RequestMapping("/space")
public class MySpaceController 
{
    @Resource(name = "articleServiceImpl")
    private ArticleService articleService;
    
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String mySpace(HttpServletRequest request,PageInfo pageInfo,Model model) throws Exception
    {
		HttpSession session = request.getSession();
		//获取登录用户
		Principal userPrincipal =
				(Principal) session. getAttribute(User.PRINCIPAL_ATTRIBUTE_NAME);
        Page<Article> page = articleService.getArticlesByUsername(userPrincipal.getUsername(), pageInfo);
       	model.addAttribute("page",page);
        return "mySpace";
    }
}
