package com.yunfang.eias.logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.yunfang.eias.base.EIASApplication;
import com.yunfang.eias.dto.LoginEasyDto;
import com.yunfang.eias.http.task.OnlineLoginTask;
import com.yunfang.framework.dto.UserInfoDTO;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;
import com.yunfang.framework.utils.JSONHelper;
import com.yunfang.framework.utils.BitmapHelperUtil;
import com.yunfang.framework.utils.SpUtil;

/**
 * 用户操作类
 * 
 * @author gorson
 * 
 */
public class LoginInfoOperator {

	// {{ 静态变量

	/**
	 * 当前用户信息
	 */
	private static UserInfo _currentUser = null;

	/**
	 * 记录登录相关的值
	 */
	public static final String KEY_LOGINPARAM = "Login_Infos";

	/**
	 * 用于记录最后一次登录时的用户名与密码
	 */
	public static final String KEY_LATESTLOGININFO = "Latest_LoginInfo";

	/**
	 * 记录所有在此App上成功在线登录过的用户名与密码
	 */
	public static final String KEY_LOGINUSERINFOS = "Login_UserInfoes";

	/**
	 * 记录版本信息相关的值
	 */
	public static final String KEY_VERSIONINFO = "Login_UserInfoes";

	/**
	 * 记录最后一次版本信息
	 */
	public static final String KEY_LASTVERSIONINFO = "Login_UserInfoes";

	/**
	 * 当前系统的Context
	 */
	private static Context appContext = EIASApplication.getInstance()
			.getApplicationContext();

	// }}

	/**
	 * 用户在线登录，与后台服务器交互
	 * 
	 * @param userName
	 *            :用户登录名称
	 * @param userPwd
	 *            :用户登录密码
	 * @param serverName
	 *            :服务器根地址
	 * @param isAuto
	 *            :是否自动登录
	 * @param isRememberPwd
	 *            :是否记住当前密码
	 * @return ResultInfo.Data 为UserInfo对象
	 */
	public static ResultInfo<UserInfo> login(String userName, String userPwd,
			String serverName, boolean isAuto, boolean isRememberPwd) {
		ResultInfo<UserInfo> result = new ResultInfo<UserInfo>();
		ResultInfo<UserInfoDTO> tempResult = new ResultInfo<UserInfoDTO>();

		try {
			String serverUrl = "";
			if (EIASApplication.Services.containsKey(serverName)) {
				serverUrl = EIASApplication.Services.get(serverName);
			} else {
				serverUrl = serverName;
			}
			OnlineLoginTask task = new OnlineLoginTask();
			tempResult = task.request(userName, userPwd, serverUrl);
			// 轉回原來的userinfo
			result.Success = tempResult.Success;
			result.Message = tempResult.Message;
			result.Others = tempResult.Others;
			if (tempResult.Data != null) {
				result.Data = new UserInfo(tempResult.Data);
			}
			result.Message = tempResult.Message;
			if (tempResult.Success && tempResult.Data != null
					&& tempResult.Data.Token.length() > 0
					&& !tempResult.Data.Token.equals("null")) {
				// 保存圖片
				if (tempResult.Data.ImagePath != null) {// 用户头像路径不为空
					result.Data.ImagePath=tempResult.Data.ImagePath;
				} else {
					if (tempResult.Data.ImageData != null
							&& tempResult.Data.ImageData.length() > 0
							&& !tempResult.Data.ImageData.equals("null")) {
						saveUserImage(tempResult);
					}
				}
				EIASApplication.IsNetworking = true;
				EIASApplication.IsOffline = false;
				result.Data.LatestServerName = serverName;
				result.Data.LatestServer = serverUrl;
				result.Data.IsAuto = isAuto;
				result.Data.IsRememberPwd = isRememberPwd;
				result.Data.Password = userPwd;
				setCurrentUser(result.Data);
				saveToLocalLoginInfos(result.Data);
				saveToLatestLoginInfo(result.Data);
				saveToLocalVersionInfo((LoginEasyDto) result.Others);
				DataLogOperator.userLogin(false, "");
			} else {
				setCurrentUser(null);
			}
		} catch (Exception e) {
			result.Success = false;
			result.Message = "保存数据失败";
			DataLogOperator.userLogin(false, result.Message);
		}

		return result;
	}

	/**
	 * 保存用户头像
	 * 
	 * @param tempResult
	 * @throws FileNotFoundException
	 */
	private static void saveUserImage(ResultInfo<UserInfoDTO> tempResult)
			throws FileNotFoundException {
		String IMAGE_FILE_NAME = "faceImage.jpg";
		String imagePath = EIASApplication.userRoot
				+ tempResult.Data.UserAccount + File.separator;
		File file = new File(imagePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		Bitmap photo = BitmapHelperUtil
				.stringBase64toBitmap(tempResult.Data.ImageData);
		FileOutputStream out = new FileOutputStream(imagePath + IMAGE_FILE_NAME);
		photo.compress(Bitmap.CompressFormat.JPEG, 90, out);
	}

	/**
	 * 用户离线登录，从以前登录过的记录中对比用户名和密码
	 * 
	 * @param userName
	 *            :用户登录名称
	 * @param userPwd
	 *            :用户登录密码
	 * @param serverUrl
	 *            :服务器根地址
	 * @param isAuto
	 *            :是否自动登录
	 * @param isRememberPwd
	 *            :是否记住当前密码
	 * @return ResultInfo.Data 为UserInfo对象
	 */
	public static ResultInfo<UserInfo> loginByOffline(String userName,
			String userPwd, String serverUrl, boolean isAuto,
			boolean isRememberPwd) {
		ResultInfo<UserInfo> result = new ResultInfo<UserInfo>();

		try {
			ArrayList<UserInfo> localUserInfos = getAllLocalLoginInfos();
			if (localUserInfos.size() > 0) {
				for (UserInfo item : localUserInfos) {
					if ((item.Account.equals(userName) && item.Password
							.equals(userPwd))) {
						item.IsAuto = isAuto;
						item.IsRememberPwd = isRememberPwd;
						setCurrentUser(item);
						item.Password = userPwd;
						saveToLocalLoginInfos(item);
						saveToLatestLoginInfo(item);
						result.Data = item;

						EIASApplication.IsNetworking = false;
						EIASApplication.IsOffline = true;
						DataLogOperator.userLogin(true, "");
						break;
					}
				}
			}
			if (result.Data == null) {
				result.Data = new UserInfo();
				result.Message = "用户需要重新登录";
			}
		} catch (Exception e) {
			result.Success = false;
			result.Message = "用户需要重新登录";
			DataLogOperator.userLogin(true, result.Message);
		}

		return result;
	}

	/**
	 * 用户注销
	 * 
	 * @return
	 */
	public static ResultInfo<Boolean> logout() {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		try {
			if (_currentUser != null) {
				_currentUser.IsAuto = false;
				saveToLocalLoginInfos(_currentUser);
				saveToLatestLoginInfo(_currentUser);
				/** 退出登录时清空后台进行中的任务 */
				/*
				 * MainService.cleanBgTask();
				 */
				_currentUser = null;
				result.Data = true;
			} else {
				result.Data = false;
				result.Message = "系统未登录";
			}
		} catch (Exception e) {
			result.Success = false;
			result.Data = false;
			result.Message = "系统未登录";
		}

		return result;
	}

	/**
	 * 设置当前用户信息
	 * 
	 * @param user
	 *            当用用户信息
	 */
	private static void setCurrentUser(UserInfo user) {
		_currentUser = user;
	}

	/**
	 * 获取当前登录用户信息
	 * 
	 * @return
	 */
	public static UserInfo getCurrentUser() {
		if (_currentUser == null) {
			// 如果当前用户信息为空了，获取最后一次登录的用户信息。
			_currentUser = GetLatestLoginInfo();
		}
		return _currentUser;
	}

	/**
	 * 获取最后一次登录时的用户信息
	 * 
	 * @return 最后登录的用户信息，如果未有登录信息，即返回一个空的UserInfo对象
	 */
	public static UserInfo GetLatestLoginInfo() {
		UserInfo result = new UserInfo();
		SpUtil sp = SpUtil.getInstance(KEY_LOGINPARAM);
		String latestLoginInfoJsonStr = sp.getString(KEY_LATESTLOGININFO, "");
		if (latestLoginInfoJsonStr.length() > 0) {
			result = JSONHelper.parseObject(latestLoginInfoJsonStr,
					UserInfo.class);
		}
		return result;
	}

	/**
	 * 当用户在线登录成功后，将此用户信息保存到系统中，用于离线勘察时用户信息对比
	 * 
	 * @param userInfo
	 *            :当前登录的用户信息
	 */
	private static void saveToLocalLoginInfos(UserInfo userInfo) {
		SpUtil sp = SpUtil.getInstance(KEY_LOGINPARAM);
		String allUserInfosStr = sp.getString(KEY_LOGINUSERINFOS, "");
		if (allUserInfosStr.equals("[null]")) {
			allUserInfosStr = "";
		}
		ArrayList<UserInfo> list = new ArrayList<UserInfo>();
		if (allUserInfosStr.length() == 0) {
			list.add(userInfo);
		} else {
			UserInfo[] temp = JSONHelper.parseArray(allUserInfosStr,
					UserInfo.class);
			list.add(userInfo);
			if (temp.length > 0) {
				for (UserInfo item : temp) {
					if (!item.Name.equals(userInfo.Name)) {
						list.add(item);
					}
				}
				if (list.size() > 0) {
					int i = 0;
					for (UserInfo item : list) {
						if (item.Name.equals(userInfo.Name)) {
							list.set(i, userInfo);
							break;
						}
						i++;
					}
				}
			} else {
				list.add(userInfo);
			}
		}
		sp.putString(KEY_LOGINUSERINFOS, JSONHelper.toJSON(list.toArray()));
	}

	/**
	 * 将用户保存为最后一次登录的信息，下次用户打开客户端，自动加载此登录信息
	 * 
	 * @param userInfo
	 *            :当前登录的用户信息
	 */
	private static void saveToLatestLoginInfo(UserInfo userInfo) {
		SharedPreferences sp = appContext.getSharedPreferences(KEY_LOGINPARAM,
				Activity.MODE_PRIVATE);
		sp.edit().putString(KEY_LATESTLOGININFO, JSONHelper.toJSON(userInfo))
				.commit();
	}

	/**
	 * 将服务器的版本信息保存起来
	 * 
	 * @param versionDto
	 */
	private static void saveToLocalVersionInfo(LoginEasyDto dto) {
		EIASApplication.version.ServerReleasedTime = dto.LastUpdateTime;
		EIASApplication.version.ServerVersionCode = dto.VersionCode;
		EIASApplication.version.ServerVersionDescription = dto.UpdateContent;
		EIASApplication.version.ServerVersionName = dto.VersionName;
		
		EIASApplication.loginEasy.LastUpdateTime = dto.LastUpdateTime;
		EIASApplication.loginEasy.VersionCode = dto.VersionCode;
		EIASApplication.loginEasy.VersionName = dto.VersionName;
		EIASApplication.loginEasy.UpdateContent = dto.UpdateContent;
		EIASApplication.loginEasy.NormalDoingTask = dto.NormalDoingTask;
		EIASApplication.loginEasy.NormalTodoTask = dto.NormalTodoTask;
		EIASApplication.loginEasy.UrgentlDoingTask = dto.UrgentlDoingTask;
		EIASApplication.loginEasy.UrgentTodoTask = dto.UrgentTodoTask;
		SpUtil sp = SpUtil.getInstance(KEY_VERSIONINFO);
		sp.putString(KEY_LASTVERSIONINFO,JSONHelper.toJSON(EIASApplication.loginEasy));
	}

	/**
	 * 读取客户端所有登录过的用户信息
	 * 
	 * @return
	 */
	private static ArrayList<UserInfo> getAllLocalLoginInfos() {
		ArrayList<UserInfo> result = new ArrayList<UserInfo>();
		SpUtil sp = SpUtil.getInstance(KEY_LOGINPARAM);
		String allUserInfosStr = sp.getString(KEY_LOGINUSERINFOS, "");
		if (allUserInfosStr.length() > 0) {
			UserInfo[] temp = JSONHelper.parseArray(allUserInfosStr,
					UserInfo.class);
			for (UserInfo item : temp) {
				result.add(item);
			}
		}
		return result;
	}

	/**
	 * 根据用户Account获取曾经登陆的用户信息
	 * 
	 * @param userAccount
	 * @return
	 */
	public static UserInfo getLoginInfo(String userAccount) {
		UserInfo result = new UserInfo();
		SpUtil sp = SpUtil.getInstance(KEY_LOGINPARAM);
		String allUserInfosStr = sp.getString(KEY_LOGINUSERINFOS, "");
		if (allUserInfosStr.length() > 0) {
			UserInfo[] temp = JSONHelper.parseArray(allUserInfosStr,
					UserInfo.class);
			for (UserInfo item : temp) {
				if (item.Account.equals(userAccount)) {
					result = item;
				}
			}
		}
		return result;
	}

	/**
	 * 用userinfo保存用户信息
	 * 
	 * @param useInfo
	 */
	public static void saveTOLoaclUserInfo(UserInfo useInfo) {
		EIASApplication.IsNetworking = true;
		EIASApplication.IsOffline = false;
		setCurrentUser(useInfo);
		saveToLocalLoginInfos(useInfo);
		saveToLatestLoginInfo(useInfo);
	}
}
