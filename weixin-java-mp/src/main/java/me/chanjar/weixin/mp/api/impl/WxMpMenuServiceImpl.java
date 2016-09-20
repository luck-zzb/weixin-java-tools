package me.chanjar.weixin.mp.api.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.util.http.SimpleGetRequestExecutor;
import me.chanjar.weixin.common.util.http.SimplePostRequestExecutor;
import me.chanjar.weixin.mp.api.WxMpMenuService;
import me.chanjar.weixin.mp.api.WxMpService;

/**
 * Created by Binary Wang on 2016/7/21.
 */
public class WxMpMenuServiceImpl implements WxMpMenuService {
  private static final String API_URL_PREFIX = "https://api.weixin.qq.com/cgi-bin/menu";
  private static Logger log = LoggerFactory.getLogger(WxMpMenuServiceImpl.class);

  private WxMpService wxMpService;

  public WxMpMenuServiceImpl(WxMpService wxMpService) {
    this.wxMpService = wxMpService;
  }

  @Override
  public void menuCreate(WxMenu menu) throws WxErrorException {
	String menuJson  = menu.toJson();
	String url  =   API_URL_PREFIX + "/create";
    if (menu.getMatchRule() != null) {
      url = API_URL_PREFIX + "/addconditional";
    } 
    if(log.isTraceEnabled()){
      log.trace("开始创建菜单：{}", menuJson);
    }
      
    String result = this.wxMpService.execute(new SimplePostRequestExecutor(), url,menuJson);
    
    if(log.isDebugEnabled()){
      log.debug("创建菜单：{},结果：{}", menuJson, result);
    }
  }

  @Override
  public void menuDelete() throws WxErrorException {
    String url = API_URL_PREFIX + "/delete";
    String result  = this.wxMpService.execute(new SimpleGetRequestExecutor(), url, null);
    if(log.isDebugEnabled()){
      log.debug("删除菜单结果：{}", result);
    }
  }

  @Override
  public void menuDelete(String menuid) throws WxErrorException {
    String url = API_URL_PREFIX + "/delconditional";
    String result  = this.wxMpService.execute(new SimpleGetRequestExecutor(), url, "menuid=" + menuid);
    if(log.isDebugEnabled()){
      log.debug("根据MeunId({})删除菜单结果：{}", menuid,result);
    }
  }

  @Override
  public WxMenu menuGet() throws WxErrorException {
    String url = API_URL_PREFIX + "/get";
    try {
      String resultContent = this.wxMpService.execute(new SimpleGetRequestExecutor(), url, null);
      return WxMenu.fromJson(resultContent);
    } catch (WxErrorException e) {
      // 46003 不存在的菜单数据
      if (e.getError().getErrorCode() == 46003) {
        return null;
      }
      throw e;
    }
  }

  @Override
  public WxMenu menuTryMatch(String userid) throws WxErrorException {
    String url = API_URL_PREFIX + "/trymatch";
    try {
      String resultContent = this.wxMpService.execute(new SimpleGetRequestExecutor(), url, "user_id=" + userid);
      return WxMenu.fromJson(resultContent);
    } catch (WxErrorException e) {
      // 46003 不存在的菜单数据     46002 不存在的菜单版本
      if (e.getError().getErrorCode() == 46003 || e.getError().getErrorCode() == 46002) {
        return null;
      }
      throw e;
    }
  }
}
