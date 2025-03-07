package com.wenku.documents_wenku.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wenku.documents_wenku.model.DocUploadRequest;
import com.wenku.documents_wenku.model.DocVO;
import com.wenku.documents_wenku.model.UploadDocResult;
import com.wenku.documents_wenku.model.domain.Document;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wenku.documents_wenku.model.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* @author gaffey
* @description 针对表【document】的数据库操作Service
* @createDate 2024-03-02 17:42:31
*/
@Service
public interface DocumentService extends IService<Document> {

	/**
	 *
	 * 增加文档记录到数据库
	 *
	 * @param documentName
	 * @param category
	 * @param uploadUser
	 * @param documentUrl
	 * @param tags
	 * @return 文档名称
	 */
	public String addDocument(String documentName,String category,long uploadUser,
							  String documentUrl,String tags);

	/**
	 *
	 * 删除指定ID的文档
	 *
	 * @param userId
	 * @param documentId
	 * @return 被删除文档的ID
	 */
	public Long deleteDocument(long userId,long documentId);

	/**
	 * 根据文档名搜索
	 *
	 * @param documentName
	 * @param pageNum
	 * @param pageSize
	 * @return 文档信息
	 */
	public Page<Document> searchDocumentByName(String documentName,long pageNum,long pageSize);

	/**
	 *
	 * 根据Id搜索文档
	 *
	 * @param documentId
	 * @return 文档信息
	 */
	public Document searchDocumentById(long documentId);

	/**
	 * 根据标签搜索文档
	 *
	 * @param tags
	 * @param pageNum
	 * @param pageSize
	 * @return 文档信息
	 */
	public Page<Document> searchDocumentByTags(String tags,long pageNum,long pageSize);

	/**
	 * 通过类型查询
	 * @param category
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public Page<Document> searchDocumentByCategory(String category,long pageNum,long pageSize);


	/**
	 * 上传文件(Ftp方式)
	 * @param uploadDocument
	 * @return 文件地址URL
	 */
	public String documentUpload(MultipartFile uploadDocument);

	/**
	 * 文件上传至Cos
	 * @param uploadDoc
	 * @return
	 */
	public String documentUploadToCos(MultipartFile uploadDoc);
	/**
	 * 上传图片
	 *
	 * @param multipartFile
	 * @param pictureUploadRequest
	 * @param loginUser
	 * @return
	 */
	DocVO uploadDoc(MultipartFile multipartFile,
					DocUploadRequest pictureUploadRequest,
					User loginUser);


	/**
	 * 从数据库获取点赞最高的10个文档
	 *
	 * @return List
	 */
	public List<String> recommednDocument();

	/**
	 * 从Redis缓存获得推荐
	 *
	 * @return List
	 */
	public List<String> redommendFromRedis();

	/**
	 * 更新点赞和浏览量
	 *
	 * @return boolean
	 */
	public boolean updateLandB(Long likes,Long browser,Long documentId);
}
