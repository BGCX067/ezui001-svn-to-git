package cn.vstore.appserver.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.vstore.appserver.model.FeatureApplication;
import cn.vstore.appserver.model.Space;
import cn.vstore.appserver.model.Top3OfCategory;
import cn.vstore.appserver.service.ResultCode.CommonCode;

/** 
 * @author Raymond 
 * @version 创建时间：2012-6-18 上午11:50:25 
 * 类说明 专区Service
 */
@Service("SpaceService")
public class SpaceService {
	
	@Autowired
    private SqlMapClientTemplate sqlMapClientTemplate;
	
	@Autowired
	private ConstantService constantService;
    
    /**
     * SpaceList : 取得专区 service
     */
    @Transactional(propagation = Propagation.NEVER)
    public ServiceResult<List<Space>> getSpaces(SpaceService self,int spaceType,String spaceID,BigDecimal storeID) {
        try {
            // 参数map
            Map<String, Object> map = new HashMap<String, Object>();
            if(spaceType == 2){
            	map.put("storeID", storeID);
            	map.put("spaceID", spaceID);
            	 @SuppressWarnings("unchecked")
                 List<Space> spaceDetailList;
            	 spaceDetailList = sqlMapClientTemplate.queryForList("space.getSpaceDetail", map);
            	 return new ServiceResult<List<Space>>(CommonCode.SUCCESS, spaceDetailList);
            }
            map.put("storeID", storeID);
            map.put("spaceType", spaceType);
            //声明首页应用集合
            @SuppressWarnings("unchecked")
            List<Space> spaceList;
            spaceList = sqlMapClientTemplate.queryForList("space.getSpaces", map);
            
            List<Space> spaceWithTop3List = new ArrayList<Space>();
            for (Space space : spaceList) {
                //设置对象中专区的前三个好应用
                @SuppressWarnings("unchecked")
                Map spaceTop = new HashMap();
                spaceTop.put("storeID", storeID);
                spaceTop.put("spaceID", space.getId());
                //声明前三名的集合
                List<Top3OfCategory> top3 = sqlMapClientTemplate.queryForList("Application.getTop3OfSapce",spaceTop);
                if(top3.size()<1){
                	//设置到list
                    spaceWithTop3List.add(space);
                }else{
                	
                	//设置前三名的数据
                	for(int i = 0; i < top3.size();i++){
                		if(i==0){
                			space.setTop1(top3.get(i).getAppTitle());
                		}else if(i==1){
                			space.setTop2(top3.get(i).getAppTitle());
                		}else if(i==2){
                			space.setTop3(top3.get(i).getAppTitle());
                		}
                	}
                	//设置到list
                	spaceWithTop3List.add(space);
                }
                
            }            
            return new ServiceResult<List<Space>>(CommonCode.SUCCESS, spaceWithTop3List);
        } catch (Throwable e) {
            return new ServiceResult<List<Space>>(CommonCode.SERVICE_FAIL, null, e);
        }

    }
    
    /**
     * SpaceList : 取得专区内的应用 service
     */
    @Transactional(propagation = Propagation.NEVER)
    public ServiceResult<List<FeatureApplication>> getAppsOfSpace(SpaceService self,BigDecimal storeID,String spaceID) {
        try {
            // 参数map
            Map<String, Object> app = new HashMap<String, Object>();
            app.put("storeID", storeID);
            app.put("spaceID", spaceID);
           
            //声明首页应用集合
            @SuppressWarnings("unchecked")
            List<FeatureApplication> appsOfSpace = null;
            appsOfSpace = sqlMapClientTemplate.queryForList("space.getAppOfSpace", app);
            
            String hostPath = constantService.getHostPath();
            List<FeatureApplication> appsOfSpaceEnd = new ArrayList<FeatureApplication>();
            for (FeatureApplication application : appsOfSpace) {
                // icon url前加上hostPath
            	if (StringUtils.isNotBlank(application.getIcon()) && application.getIcon().charAt(0) == '/')
            		application.setIcon(hostPath + application.getIcon());
            		appsOfSpaceEnd.add(application);
            }       
            return new ServiceResult<List<FeatureApplication>>(CommonCode.SUCCESS, appsOfSpaceEnd);
        } catch (Throwable e) {
            return new ServiceResult<List<FeatureApplication>>(CommonCode.SERVICE_FAIL, null, e);
        }

    }

}
