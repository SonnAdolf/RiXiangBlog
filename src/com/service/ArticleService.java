package com.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.entity.Article;
import com.util.Page;
import com.util.PageInfo;
import com.util.Principal;

/**
 * @author 无名
 * @date 2016.04.21
 * @description:文章service接口
 */
public interface ArticleService extends BaseService<Article>
{
	public List<Article> findAllArticle();
	
	/*
	 * 返回article该存储的路径
	 */
	public String getArticleUrl(Article article,HttpServletRequest request,
			                                         Principal userPrincipal);
	
	/*
	 * 根据用户名查找文章
	 */
	public Page<Article> getArticlesByUsername(String username,PageInfo pageInfo);
}
