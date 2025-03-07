package com.wenku.documents_wenku.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wenku.documents_wenku.constant.RedisConstant;
import com.wenku.documents_wenku.job.UpdateLikeAndReadJob;
import com.wenku.documents_wenku.model.domain.Document;
import com.wenku.documents_wenku.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Slf4j
class DocumentServiceImplTest {

	@Resource
	private DocumentService documentService;
	@Test
	void addDocument() {
		String documentName = "计算机网路";
		String category = "计算机";
		long uploadUser = 1;
		String documentUrl = "url";
		String tags = "教学";
		String s = documentService.addDocument(documentName, category, uploadUser, documentUrl, tags);
		System.out.println(s);

	}

	@Test
	void deleteDocument() {
		long userId = 1;
		long documentId = 2;
		Long l = documentService.deleteDocument(1, 2);
		System.out.println(l);
	}

	@Test
	void searchDocumentByName() {
		long start = System.currentTimeMillis();
		Page<Document> documentPage = documentService.searchDocumentByName("计算机", 22, 22);
//		System.out.println(documents);
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}

	@Test
	void searchDocumentById() {
		long start = System.currentTimeMillis();
		Document document = documentService.searchDocumentById(1);
//		System.out.println(document);
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}

	@Test
	void searchDocumentByTags() {
		long start = System.currentTimeMillis();
		Page<Document> documentPage = documentService.searchDocumentByTags("教", 10, 10);
//		System.out.println(documents);
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}

	@Test
	void recomentDocument() {
	}

	@Test
	void redommendFromRedis() {
		List<String> documents = documentService.redommendFromRedis();
		System.out.println(documents);
	}

	@Resource
	RedissonClient redissonClient;
	@Resource
	RedisTemplate redisTemplate;
	@Test
	void testSche(){
		System.out.println("start");
		RLock rLock = redissonClient.getLock("wenku:doCacheRecommendDocument:lock");
		try{
			List<String> recomendList = new ArrayList<>();
			int size = 0;
			if(rLock.tryLock(0,-1, TimeUnit.MILLISECONDS)){
				Long originSize = redisTemplate.opsForList().size(RedisConstant.RECOMEND_TOP_DOCUMENT);
				for (int i = 0;i < originSize;i++){
					redisTemplate.opsForList().leftPop(RedisConstant.RECOMEND_TOP_DOCUMENT);
				}
				log.info(Thread.currentThread().getId()+"get lock");
				recomendList = documentService.recommednDocument();
				size = recomendList.size();
			}
			try{
				for(int i = 0;i < size;i++){
					redisTemplate.opsForList().leftPush(RedisConstant.RECOMEND_TOP_DOCUMENT,recomendList.get(i));
				}
			}catch(Exception e){
				log.error("Redis set key Error",e);
			}
		}catch(Exception e){
			log.error("doCacheRecommendDocument Error",e);
		}finally {
			if (rLock.isHeldByCurrentThread()){
				rLock.unlock();
				log.info(Thread.currentThread().getId()+"unlocck");
			}
		}
	}

	@Resource
	private UpdateLikeAndReadJob updateLikeAndReadJob;

	@Test
	void recommednDocument() {
		List<String> documents = documentService.recommednDocument();
		System.out.println(documents);
		updateLikeAndReadJob.updateLikeAndReadCount();
	}

	@Test
	void searchDocumentByCategory() {
	}

	@Test
	void documentUpload() {
	}

	@Test
	void documentUploadToCos() {
	}

	@Test
	void uploadDoc() {
	}

	@Test
	void updateLandB() {
	}

	@Test
	void getSafetyDoc() {
	}
}