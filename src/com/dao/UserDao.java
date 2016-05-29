package com.dao;

import java.util.List;

import com.entity.User;

/**
* @ClassName: UserDao 
* @Description: User dao接口
* @author 王宇 
* @date 2016-3-25
* @version 1.0
 */
public interface UserDao extends BaseDao<User>
{
	/**
	 *@Title: findByUserName 
	* @Description: 根据用户名查询
	* @param @param username
	* @param @return    设定文件 
	* @return List<User>    返回类型 
	* @throws
	 */
	public List<User> findByUserName(String username);
}
