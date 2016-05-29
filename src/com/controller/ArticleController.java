package com.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.entity.Article;
import com.entity.User;
import com.service.ArticleService;
import com.util.IOUtill;
import com.util.MessageUtil;
import com.util.Page;
import com.util.PageInfo;
import com.util.Principal;
import com.util.StringUtill;

/**
* @ClassName: ArticleController 
* @Description: 文章controller代码
* @author 王宇 
* @date 2016-3-25 2016-05-15 write article func 
*         2016-05-21保存文章内容到服务器目录
* @version 1.0
 */
@Controller
@RequestMapping("/article")
public class ArticleController 
{
    @Resource(name = "articleServiceImpl")
    private ArticleService articleService;
    
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(HttpServletRequest request,PageInfo pageInfo,
    		                   Model model) throws Exception
    {
    	Page<Article> page = articleService.findPage(pageInfo,Article.class);
    	List<Article> articleList = page.getContent();
    	page.setContent(getArticleListOfContentByUrl(articleList));
    	model.addAttribute("page",page);
    	
		HttpSession session = request.getSession();
		//获取登录用户
		Principal userPrincipal =
				(Principal) session. getAttribute(User.PRINCIPAL_ATTRIBUTE_NAME);
		if(null != userPrincipal)
		{
			String userName = userPrincipal.getUsername();
	    	model.addAttribute("userName",userName);
		}
        return "mainPage";
    }
    
    @RequestMapping(value = "/writeArticlePage", method = RequestMethod.GET)
    public String writeArticlePage(PageInfo pageInfo,Model model) throws Exception
    {
        return "writeArticlePage";
    }
    
    @RequestMapping(value = "/writeArticle", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject submit(HttpServletRequest request,Article article,
    		                             String articleContent) throws Exception
    {
		JSONObject jo = new JSONObject();
		if(null == article || StringUtill.isStringEmpty(articleContent))
		{
			MessageUtil.setSimpleIsSuccessJSON(jo, false);
	        return jo;
		}
		HttpSession session = request.getSession();
		Principal userPrincipal =
				(Principal) session. getAttribute(User.PRINCIPAL_ATTRIBUTE_NAME);
		article.setAuthorName(userPrincipal.getUsername());

		String articleUrl = 
				articleService.getArticleUrl(article, request, userPrincipal);

		//数据库中存储文章路径
		article.setArticleAddr(articleUrl);
		
		//向指定目录下存储文章内容
		IOUtill.writeByUrl(articleUrl,articleContent);
		
		articleService.save(article);
		
		MessageUtil.setSimpleIsSuccessJSON(jo, true);
        return jo;
    }
    
    /*
     * 数据库里保存的是文章路径，将从数据库里查询到的文章list（只有文章路径，不含文章内容）
     * 转化为包含文章内容的文章list（依据路径读取文章内容）
     */
    private List<Article> getArticleListOfContentByUrl(List<Article> articleList)
    {
    	List<Article> newArtiList = new ArrayList<Article>();
    	Article article = new Article();
    	String url;
    	for(int i = 0; i < articleList.size(); i++ )
    	{
    		article = articleList.get(i);
    		if(null == article)
    		{
    			break;
    		}
    		url = article.getArticleAddr();
    		if(StringUtill.isStringEmpty(url))
    		{
    			article.setContent("");
    		}
    		else
    		{
        		article.setContent(IOUtill.readByUrl(url));
    		}
    		newArtiList.add(article);
    	}
    	return newArtiList;
    }
    
}