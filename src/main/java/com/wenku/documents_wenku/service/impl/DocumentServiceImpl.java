package com.wenku.documents_wenku.service.impl;
import java.util.ArrayList;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wenku.documents_wenku.common.BusinessErrors;
import com.wenku.documents_wenku.constant.RedisConstant;
import com.wenku.documents_wenku.exception.BusinessException;
import com.wenku.documents_wenku.exception.ThrowUtils;
import com.wenku.documents_wenku.manage.FileManager;
import com.wenku.documents_wenku.model.DocUploadRequest;
import com.wenku.documents_wenku.model.DocVO;
import com.wenku.documents_wenku.model.UploadDocResult;
import com.wenku.documents_wenku.model.domain.Document;
import com.wenku.documents_wenku.model.domain.User;
import com.wenku.documents_wenku.service.DocumentService;
import com.wenku.documents_wenku.mapper.DocumentMapper;
import com.wenku.documents_wenku.utils.FtpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
* @author gaffey
* @description 针对表【document】的数据库操作Service实现
* @createDate 2024-03-02 17:42:31
*/
@Service
@Slf4j
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document>
    implements DocumentService {

	@Resource
	private DocumentMapper documentMapper;

	@Resource
	private RedissonClient redissionClient;

	@Resource
	private FileManager fileManager;

	@Resource
	private RedisTemplate<String,Object> redisTemplate;

	@Override
	public String addDocument(String documentName, String category, long uploadUser, String documentUrl, String tags) {
		if(StringUtils.isAnyBlank(documentName,category,documentUrl,tags)){
			//请求参数有误
			log.error("请求参数有误"+ new Date());
			return null;
		}
		Document addDocument = new Document();
		addDocument.setDocumentName(documentName);
		addDocument.setCategory(category);
		addDocument.setUploadUserId(uploadUser);
		addDocument.setTags(tags);
		addDocument.setDocumentUrl(documentUrl);
		boolean savedState = this.save(addDocument);
		if(savedState){
			//添加成功
			return documentName + ":" +documentUrl;
		}else {
			//添加失败
			log.error("添加文档失败"+new Date());
			throw new BusinessException(BusinessErrors.SYSTEM_ERROR);
		}
	}

	@Override
	public Long deleteDocument(long userId, long documentId) {
		int i = documentMapper.deleteById(documentId);
		return documentId;
	}

	@Override
	public Page<Document> searchDocumentByName(String documentName, long pageNum, long pageSize) {
		QueryWrapper<Document> queryWrapper = new QueryWrapper<>();
		queryWrapper.select("documentName","documentUrl");
		queryWrapper.like("documentName",documentName);
		Page<Document> searchPage = this.page(new Page<>(pageNum, pageSize), queryWrapper);
//		List<Document> searchedDocuments = documentMapper.selectList(queryWrapper);
		if(searchPage == null){
			//未查询到相关文档
			return null;
		}else {
			return searchPage;
		}
	}

	@Override
	public Document searchDocumentById(long documentId) {
		QueryWrapper<Document> queryWrapper = new QueryWrapper<>();
<<<<<<< HEAD
		queryWrapper.select("documentName","documentUrl","likes","browser");
=======
		queryWrapper.select("documentName","documentUrl","uploadUser");
>>>>>>> c2dbf2786ab223221577c80d49a3ad1bab0ee4da
		queryWrapper.eq("documentId",documentId);
		Document selectedDocument = documentMapper.selectOne(queryWrapper);
		if(selectedDocument == null){
			//未查询到ID为documentId的文档
			return null;
		}else {
			//查询成功返回
			return getSafetyDoc(selectedDocument);
		}

	}

	@Override
	public Page<Document> searchDocumentByTags(String tags, long pageNum, long pageSize) {
		QueryWrapper<Document> queryWrapper = new QueryWrapper<>();
		queryWrapper.like("tags",tags);
		List<Document> selectedDocuments = documentMapper.selectList(queryWrapper);
		Page<Document> documentPage = this.page(new Page<>(pageNum, pageSize), queryWrapper);
		return documentPage;
	}

	@Override
	public Page<Document> searchDocumentByCategory(String category, long pageNum, long pageSize) {
		QueryWrapper<Document> queryWrapper = new QueryWrapper<>();
		queryWrapper.select("documentName","documentUrl");
		queryWrapper.eq("category",category);
		Page<Document> documentPage = this.page(new Page<>(pageNum,pageSize),queryWrapper);
		return documentPage;
	}

	@Override
	public String documentUpload(MultipartFile uploadDocument) {
		String fileOriginalName = uploadDocument.getOriginalFilename();
		System.out.println(fileOriginalName);
		FtpUtils ftpUtil = new FtpUtils();

		String newname=new String();

		if(uploadDocument!=null){
			//文件名称 = 时间戳 + 文件自己的名字；
			newname = System.currentTimeMillis()+uploadDocument.getOriginalFilename();

			try {
				boolean hopson = ftpUtil.uploadFileToFtp("Document", newname, uploadDocument.getInputStream());
				if(hopson) {  //上传成功
					log.info("文件上传服务器成功 ---- "+newname);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("文件上传服务器失败 ---- "+e);
				return null;
			}
		}
		return newname;
	}

	@Override
	public String documentUploadToCos(MultipartFile uploadDoc) {

		return null;
	}

	@Override
	public DocVO uploadDoc(MultipartFile multipartFile, DocUploadRequest pictureUploadRequest, User loginUser) {
		ThrowUtils.throwIf(loginUser == null, BusinessErrors.SYSTEM_ERROR);
		// 用于判断是新增还是更新
		Long docId = null;
		if (pictureUploadRequest != null) {
			docId = pictureUploadRequest.getId();
		}

		if(docId != null){
			Document document = documentMapper.selectById(docId);
			ThrowUtils.throwIf(document==null, BusinessErrors.NULL_ERROR, "图片不存在");
		}
		// 上传图片，得到信息
		// 按照用户 id 划分目录
		String uploadPathPrefix = String.format("doc/%s", loginUser.getId());
		UploadDocResult uploadDocResult = fileManager.uploadDoc(multipartFile, uploadPathPrefix);
		// 构造要入库的文档信息
		Document document = new Document();
//		document.setDocumentId(0L);
		document.setDocumentName(uploadDocResult.getDocName());
		document.setCategory("");
		document.setUploadUserId(loginUser.getId());
		document.setUploadTime(new Date());
		document.setIsDelete(0);
		document.setDocumentUrl(uploadDocResult.getUrl());
		document.setTags("");
		document.setLikes(0L);
		document.setBrowser(0L);

		int insert = documentMapper.insert(document);
		ThrowUtils.throwIf(insert == 0, BusinessErrors.NULL_ERROR, "图片上传失败");
		DocVO docVO = new DocVO();
		docVO.setId(document.getDocumentId());
		docVO.setUrl(document.getDocumentUrl());
		docVO.setName(document.getDocumentName());
		docVO.setUserId(loginUser.getId());
		docVO.setCreateTime(new Date());

		return docVO;
	}


	@Override
	public List<String> recommednDocument() {
		List<Document> documents = documentMapper.selectTopTenDocument();
		List<String> docUrls = new ArrayList<>();
		for(Document document : documents){
			docUrls.add(document.getDocumentUrl());
		}
		return docUrls;
//		return documents.stream().map(this::getSafetyDoc).collect(Collectors.toList());
	}

	@Override
	public List<String> redommendFromRedis() {
//		RList<Document> list = redissionClient.getList(RedisConstant.RECOMEND_TOP_DOCUMENT);
		List<Object> objectList = redisTemplate.opsForList().range(RedisConstant.RECOMEND_TOP_DOCUMENT, 0, 10);
		List<String> list = new ArrayList<>();
		for(Object obj : objectList){
			list.add((String) obj);
		}
		return list;
//		return 	list.stream().map(this::getSafetyDoc).collect(Collectors.toList());
	}

	@Override
	public boolean updateLandB(Long likes, Long browser, Long documentId) {
		boolean updateResult = documentMapper.updateLikesAndBrowser(likes, browser, documentId);
		if(!updateResult){
			log.error("更新点赞和浏览量失败 ---- "+new Date());
		}
		return updateResult;
	}

	public Document getSafetyDoc(Document document){
		Document safetyDoc = new Document();
		safetyDoc.setDocumentId(document.getDocumentId());
		safetyDoc.setDocumentName(document.getDocumentName());
		safetyDoc.setCategory(document.getCategory());
//		safetyDoc.setUploadUserId(0L);
//		safetyDoc.setUploadTime(new Date());
//		safetyDoc.setIsDelete(0);
		safetyDoc.setDocumentUrl(document.getDocumentUrl());
		safetyDoc.setTags(document.getTags());
		safetyDoc.setLikes(document.getLikes());
		safetyDoc.setBrowser(document.getBrowser());
		return safetyDoc;
	}
}




