package com.wenku.documents_wenku.job;


import com.wenku.documents_wenku.constant.RedisConstant;
import com.wenku.documents_wenku.model.domain.Document;
import com.wenku.documents_wenku.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热项目
 *
 * @author GaffEy
 */

@Component
@Slf4j
public class PreCacheJob {


	@Resource
	private RedissonClient redissonClient;

	@Resource
	private DocumentService documentService;

	@Resource
	private RedisTemplate<String ,Object> redisTemplate;

	/**
	 * 缓存预热首页每日推荐
	 *
	 */

	@Scheduled(cron = "0 0 0 * * *")  // 秒 时 分 * * *
	public void doCacheRecommendDocument(){
		RLock rLock = redissonClient.getLock("wenku:doCacheRecommendDocument:lock");
		log.info(Thread.currentThread().getId() + " ---- 获得缓存任务锁");
		log.info("开始每日推荐缓存预热任务 ---- "+new Date());
		try{
			List<String> recomendList = new ArrayList<>();
			int size = 0;
			if(rLock.tryLock(0,-1, TimeUnit.MILLISECONDS)){
				Long originSize = redisTemplate.opsForList().size(RedisConstant.RECOMEND_TOP_DOCUMENT);
				for (int i = 0;i < originSize;i++){
					redisTemplate.opsForList().leftPop(RedisConstant.RECOMEND_TOP_DOCUMENT);
				}
				log.info(Thread.currentThread().getId()+" ---- 执行缓存任务");
				recomendList = documentService.recommednDocument();
				size = recomendList.size();
			}
			try{
				for(int i = 0;i < size;i++){
					redisTemplate.opsForList().leftPush(RedisConstant.RECOMEND_TOP_DOCUMENT,recomendList.get(i));
				}
			}catch(Exception e){
			    log.error("Redis set key Error",e);
				log.info("缓存任务结束 ---- "+ new Date());
			}
		}catch(Exception e){
		    log.error("doCacheRecommendDocument Error",e);
			log.info("缓存任务执行结束 ---- "+ new Date());
		}finally {
			if (rLock.isHeldByCurrentThread()){
				rLock.unlock();
				log.info(Thread.currentThread().getId()+" ---- 释放缓存任务锁" + new Date());
			}
		}
	}
}
