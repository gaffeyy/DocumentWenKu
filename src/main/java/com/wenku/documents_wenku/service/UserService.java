package com.wenku.documents_wenku.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wenku.documents_wenku.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wenku.documents_wenku.model.domain.Usercollect;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* @author gaffey
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-03-02 17:45:49
*/
@Service
public interface UserService extends IService<User> {

	/**
	 * 用户注册
	 *
	 * @param userAccount
	 * @param userPassword
	 * @param checkPassword
	 * @return 用户id(主键)
	 */
	public Long userRegesiter(String userAccount,String userPassword,String checkPassword);

	/**
	 * 用户登录
	 *
	 * @param request
	 * @param response
	 * @param userAccount
	 * @param userPassword
	 * @return 用户脱敏后信息
	 */
	public User userLogin(HttpServletRequest request, HttpServletResponse response,String userAccount, String userPassword);

	/**
	 * 用户注销
	 *
	 * @param request
	 * @param response
	 * @return 1 - 成功; 0 - 失败
	 */
	public int userLogout(HttpServletRequest request,HttpServletResponse response);

	/**
	 *
	 * 获取当前登录用户信息
	 *
	 * @param request
	 * @return 当前登录用户信息
	 */
	public User getCurrentUser(HttpServletRequest request);

	/**
     * 当前用户是否是管理员
     *
     * @return 1 - 是; 0 - 否
     */
	public boolean isAdmin(User user);

	/**
	 *
	 * 当前用户是否是管理员
	 *
	 * @param request
	 * @return 1 - 是; 0 - 否
	 */
	public boolean isAdmin(HttpServletRequest request);

	/**
	 * 给文档点赞（如果点过赞则取消点赞）
	 *
	 * @param documentId
	 * @param userId
	 * @return 该文档当前的总赞数
	 */
	public Long setLike(long documentId,long userId);

	/**
	 * 记录浏览文档
	 * @param documentId
	 * @param userId
	 * @return 文档ID
	 */
	public Long setBrowser(Long documentId, long userId);

	/**
	 * 用户收藏文档
	 * @param documentId
	 * @param userId
	 * @return 文档Id
	 */
	public Long collectDoc(Long documentId, Long userId);

	/**
	 * 获取用户收藏
	 *
	 * @return List
	 */
	public Page<Usercollect> getCollect(Long userId, long pageNum, long pageSize);
}
