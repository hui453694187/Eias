/**
 * 
 */
package com.yunfang.eias.beta.logic;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;

import com.yunfang.eias.beta.base.EIASApplication;
import com.yunfang.eias.beta.dto.VersionDTO;
import com.yunfang.eias.beta.http.task.UploadUserInfoTask;
import com.yunfang.framework.dto.UserInfoDTO;
import com.yunfang.framework.model.ResultInfo;
import com.yunfang.framework.model.UserInfo;
import com.yunfang.framework.utils.CameraUtils;
import com.yunfang.framework.utils.DateTimeUtil;
import com.yunfang.framework.utils.FileUtil;
import com.yunfang.framework.utils.JSONHelper;
import com.yunfang.framework.utils.BitmapHelperUtil;
import com.yunfang.framework.utils.SpUtil;

/**
 * @author LHM 用户信息操作类
 * 
 */
public class UserInfoOperator {
	
	/**
	 * 修改密码操作
	 * 
	 * @param changesubmit 修改按钮文本
	 * @param oldPassword 旧密码
	 * @param newPassword 新密码
	 * @param aginPassword 重复新密码
	 * @return
	 */
	public static ResultInfo<Boolean> changePassword(String changesubmit,
			String oldPassword, String newPassword, String aginPassword) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		if (!changesubmit.equals("修改密码")) {
			UserInfoDTO userInfoDto = new UserInfoDTO();
			// 密码验证
			result= passwordAuthenticate(
					oldPassword, newPassword, aginPassword);
			if (!result.Data) {
				return result;
			}
			userInfoDto.UserPwd = newPassword;
			userInfoDto.UserPastPwd = oldPassword;
			UserInfo user = EIASApplication.getCurrentUser();
			UploadUserInfoTask task = new UploadUserInfoTask();
			result = task.request(user, userInfoDto);
			if (result.Data == null || !(Boolean) result.Data) {
				result.Data = false;
			} else {
				result.Data = true;
				UserInfo userInfo = EIASApplication.getCurrentUser();
				userInfo.Password = newPassword;
				LoginInfoOperator.saveTOLoaclUserInfo(userInfo);
				result.Message = "密码修改成功!";
			}
		}
		return result;
	}

	/**
	 * 密码验证
	 * 
	 * @param oldPassword 旧密码
	 * @param newPassword  新密码
	 * @param aginPassword 重复新密码
	 * @return
	 */
	private static ResultInfo<Boolean> passwordAuthenticate(String oldPassword,
			String newPassword, String aginPassword) {
		ResultInfo<Boolean> result = new ResultInfo<Boolean>();
		result.Data = true;
		result.Message = "";
		if (!newPassword.equals(aginPassword)) {
			result.Message = "新密码与重复密码不一致!";
			result.Data = false;
		} else if (oldPassword.length() < 6 || newPassword.length() < 6
				|| aginPassword.length() < 6) {
			result.Message = "密码长度不能小于6位数!";
			result.Data = false;
		} else if (oldPassword.equals(newPassword)) {
			result.Message = "旧密码与新密码不能一样!";
			result.Data = false;
		}
		return result;
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param data 目标
	 * @param path  图片保存路径
	 * @return 用户信息
	 */
	public static UserInfoDTO getImageToView(Intent data, String path, String tempPath) {
		Bundle extras = data.getExtras();
		UserInfoDTO userDto = new UserInfoDTO();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			try {
				// 图片压缩
				photo = BitmapHelperUtil.compressImage(photo);
				// 保存图片
				FileOutputStream out = new FileOutputStream(path);
				photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
				// 根据路径获得图片对象
				photo = CameraUtils.getBitmap(path);
				FileUtil.delFile(tempPath);
			} catch (Exception e) {
				e.printStackTrace();
			}
			userDto.ImageData = getImageStr(photo);
			userDto.UserVersion = DateTimeUtil.getCurrentTime_NoSymbol();
		}
		return userDto;
	}

	/**
	 * 图片对象转换图片字符串
	 * 
	 * @param image 图片对象
	 * @return 图像字符串
	 */
	public static String getImageStr(Bitmap image) {
		// 获得图片字符串
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		String imageData = android.util.Base64.encodeToString(byteArray,
				Base64.DEFAULT);
		return  imageData;
	}

	/**
	 * 设置版本信息 并保存信息到安卓自带的数据库中
	 * @param versionDto:服务器版本信息对象
	 */
	public static void setVersionInfo(VersionDTO versionDto){
		EIASApplication.versionInfo.ServerReleasedTime = versionDto.LastUpdateTime;
		EIASApplication.versionInfo.ServerVersionCode = versionDto.VersionCode;
		EIASApplication.versionInfo.ServerVersionDescription = versionDto.UpdateContent;
		EIASApplication.versionInfo.ServerVersionName = versionDto.VersionName;
		EIASApplication.versionInfo.GetVersionLocalTime = DateTimeUtil.getCurrentTime();
		SpUtil sp = SpUtil.getInstance(LoginInfoOperator.KEY_VERSIONINFO);			
		sp.putString(LoginInfoOperator.KEY_LASTVERSIONINFO,JSONHelper.toJSON(EIASApplication.versionInfo));
	}
}
