/**
 * 
 */
package com.yunfang.eias.logic;

import java.util.ArrayList;
import java.util.List;

import com.yunfang.eias.model.DataDefine;
import com.yunfang.eias.tables.DataDefineWorker;

/**
 * 勘察配置信息数据操作
 * @author kevin
 *
 */
public class DatadefinesOperator {
	
	/***
	 * 对比远程服务器勘察表，和本地勘察表，删除远程不存在的勘察表
	 * @param localDefines 本地所有勘察表
	 * @param motroDefines 远程所有勘察表
	 */
	public static void deleteUnDealDefines(ArrayList<DataDefine> localDefines,ArrayList<DataDefine> motroDefines){
		List<String> ddidList=new ArrayList<String>();
		for(DataDefine localDefine:localDefines){// 对比本地勘察表， 和远程勘察表
			boolean isExist=false;
			for(DataDefine motroDefine:motroDefines){
				if(motroDefine.DDID==localDefine.DDID){// 本地的DDID 对应远程服务器的 define 的ID 
					isExist=true;
					break;
				}
			}
			if(!isExist){// 远程服务器不存这张勘察表
				ddidList.add(String.valueOf(localDefine.DDID));
			}
		}
		if(ddidList.size()>0){// 删除远程不存在的 勘察表
			DataDefineWorker.deletDataDefneByDDID(ddidList);
		}
	}
}
