package com.sunwayworld.escm.core.utils;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletResponse;

import net.sf.json.JSONObject;

import com.sunwayworld.escm.Constant;
import com.sunwayworld.escm.core.exception.InternalException;

/**
 * Ajax工具类
 */
public class AjaxUtils {
	/**
	 * 输出Ajax请求内容到客户端
	 */
	public static final <T> void response(final ServletResponse response, final T content) {
		if (ObjectUtils.isEmpty(content)) {
			return;
		}
		
		response.setContentType(Constant.CONTENT_TYPE);
		
		PrintWriter writer = null;
		
		try {
			writer = response.getWriter();
			writer.write(ConvertUtils.convert(content, String.class));
			writer.flush();
		} catch (IOException ioe) {
			throw new InternalException(ioe);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch(Exception ex) {
					/* ignore */
				}
			}
		}
	}
	
	/**
	 * 输出Ajax请求成功信息到客户端
	 */
	public static final void responseSuccess(final ServletResponse response) {
		final JSONObject json = new JSONObject();
		json.accumulate("success", true);
		
		response(response, json.toString());
	}
	
	/**
	 * 输出Ajax请求失败信息到客户端
	 */
	public static final void responseFailure(final ServletResponse response) {
		final JSONObject json = new JSONObject();
		json.accumulate("success", false);
		
		response(response, json.toString());
	}
}
