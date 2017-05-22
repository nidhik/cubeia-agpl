package com.cubeia.poker.client.web;

import com.cubeia.backoffice.operator.api.OperatorConfigParamDTO;
import com.cubeia.backoffice.operator.client.OperatorServiceClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

import static com.cubeia.backoffice.operator.api.OperatorConfigParamDTO.CLIENT_TITLE;
import static com.cubeia.backoffice.operator.api.OperatorConfigParamDTO.CSS_URL;

@Controller
public class ClientController {

    @Value("${default.skin}")
    private String defaultSkin;
    
    @Value("${firebase.host:}")
    private String firebaseHost;

    @Value("${firebase.http-port:-1}")
    private int firebaseHttpPort;

    @Value("${google.analytics.id}")
    private String googleAnalyticsId;

    @Value("${uservoice.id}")
    private String userVoiceId;

    @Value("${operator-api.service.url}")
    private String operatorApiBaseUrl;

    @Value("${player-api.service.url}")
    private String playerApiBaseUrl;

    @Value("${addthis.pubid}")
    private String addThisPubId;

    @Value("${pure.token.enabled}")
    private boolean trueTokenEnabled;

    @Value("${firebase.secure.connection}")
    private boolean secureConnection;

    // @Value("${operator.config.cache-ttl}")
    // private Long configCacheTtl;
    
    @Resource(name = "operatorService")
    private OperatorServiceClient operatorService;
    
    private final String SAFE_PATTER = "[a-zA-Z0-9\\.\\-_]*";
    
    // TODO: Cache config (see below), we don't want to hit the 
    // oeprator config too hard /LJN
    /*private final LoadingCache<Long, Map<OperatorConfigParamDTO,String>> operatorConfig = 
    		CacheBuilder.newBuilder().expireAfterAccess(30000, MILLISECONDS).build(new CacheLoader<Long, Map<OperatorConfigParamDTO,String>>() {
				
				@Override
				public Map<OperatorConfigParamDTO,String> load(Long id) throws Exception {
					return operatorService.getConfig(id);
				}
			});*/



    @RequestMapping("/")
    public String handleDefault(HttpServletRequest request, ModelMap modelMap) {
        String servletPath = request.getServletPath();
        return "redirect:"+servletPath+"/"+defaultSkin;
    }

    @RequestMapping(value = {"/{skin}"})
    public String handleStart(HttpServletRequest request, ModelMap modelMap,
                              @PathVariable("skin") String skin) {

        modelMap.addAttribute("cp",request.getContextPath());

        if(skin == null) {
            skin = defaultSkin;
        } else if(!skin.matches(SAFE_PATTER)) {
            modelMap.addAttribute("skin","");
        }
        checkSetFirebaseAttributes(modelMap);
        return "index";
    }

    @RequestMapping(value = {"/{operatorId}/{skin}"})
    public String handleStartWithOperator(HttpServletRequest request, ModelMap modelMap,
                              @PathVariable("operatorId") Long operatorId, @PathVariable("skin") String skin ) {

        modelMap.addAttribute("operatorId",operatorId);
        return handleStart(request,modelMap,skin);
    }

    @RequestMapping("/logout")
    public @ResponseBody String invalidateSession(HttpSession session) {
        session.invalidate();
        return "{ \"status\" : \"OK\"}";
    }

	private void checkSetFirebaseAttributes(ModelMap modelMap) {
		if(firebaseHost != null && firebaseHost.length() > 0) {
        	modelMap.addAttribute("firebaseHost", firebaseHost);
        }
        if(firebaseHttpPort != -1) {
        	modelMap.addAttribute("firebaseHttpPort", firebaseHttpPort);
        }
        if(googleAnalyticsId != null) {
            modelMap.addAttribute("googleAnalyticsId", googleAnalyticsId);
        }
        if(userVoiceId != null) {
            modelMap.addAttribute("userVoiceId", userVoiceId);
        }
        if(playerApiBaseUrl !=null) {
            modelMap.addAttribute("playerApiBaseUrl",playerApiBaseUrl);
        }
        if(operatorApiBaseUrl!=null) {
            modelMap.addAttribute("operatorApiBaseUrl",operatorApiBaseUrl);
        }
        if(addThisPubId!=null) {
            modelMap.addAttribute("addThisPubId",addThisPubId);
        }
        modelMap.addAttribute("secureConnection",secureConnection);
	}

    @RequestMapping("/skin/{skin}/operator/{operatorId}/token/{token}/{section:[a-z0-9]+}/{value:[a-z0-9]+}")
    public String handleStartWithTokenURLAndSection(HttpServletRequest request,
                                          HttpSession session,
                                          @PathVariable("skin") String skin,
                                          @PathVariable("operatorId") Long operatorId,
                                          @PathVariable("token") String token,
                                          @PathVariable("section") String section,
                                          @PathVariable("value") String value) {
        String hash = String.format("#/%s/%s",section,value);
        return setSessionAttributeAndRedirect(request, session, skin, operatorId, token,hash);
    }

    @RequestMapping(value = {"/skin/{skin}/operator/{operatorId}/token/{token}"})
    public String handleStartWithTokenURL(HttpServletRequest request,
                                          HttpSession session,
                                          @PathVariable("skin") String skin,
                                          @PathVariable("operatorId") Long operatorId,
                                          @PathVariable("token") String token) {


        return setSessionAttributeAndRedirect(request, session, skin, operatorId, token,"");
    }

    private String setSessionAttributeAndRedirect(HttpServletRequest request,
                                                  HttpSession session,
                                                  String skin,
                                                  Long operatorId,
                                                  String token,
                                                  String hash) {

        session.setAttribute("token",token);

        String servletPath = request.getServletPath();
        return String.format("redirect:%s/skin/%s/operator/%s%s",servletPath,skin,operatorId.toString(),hash);
    }



    @RequestMapping(value = {"/skin/{skin}/operator/{operatorId}"})
    public String handleStartWithToken(HttpServletRequest request,
                                             HttpSession session,
                                             ModelMap map,
                                             @PathVariable("skin") String skin,
                                             @PathVariable("operatorId") Long operatorId) {
        Object tokenObj = session.getAttribute("token");
        String token = null;
        if(tokenObj!=null) {
            token = (String) tokenObj;
            return doHandleStartWithToken(request, map, skin, operatorId, token, trueTokenEnabled);
        } else {
            return handleSessionTimedOut(request,map,skin,operatorId);
        }
    }

    private String handleSessionTimedOut(HttpServletRequest request, ModelMap map, String skin, Long operatorId) {
        Map<OperatorConfigParamDTO, String> opConfig = safeGetOperatorConfig(operatorId);
        if(opConfig != null && opConfig.get(CSS_URL) != null) {
            map.addAttribute("cssOverride", opConfig.get(CSS_URL));
        }
        if(opConfig != null && opConfig.get(OperatorConfigParamDTO.LOGOUT_PAGE_URL) != null) {
            String url = opConfig.get(OperatorConfigParamDTO.LOGOUT_PAGE_URL);
            map.put("logoutUrl",url);
        }
        if(skin==null || !skin.matches(SAFE_PATTER)) {
            map.addAttribute("skin","");
        }
        map.addAttribute("cp",request.getContextPath());
        return "session-timeout";
    }

    @RequestMapping(value = "/session/skin/{skin}/operator/{operatorId}")
    public String handleStartWithSessionCookie(HttpServletRequest request, ModelMap modelMap,
                                             @PathVariable("skin") String skin,
                                             @PathVariable("operatorId") Long operatorId,
                                             @ModelAttribute("token") String session) {
        return doHandleStartWithToken(request, modelMap, skin, operatorId, session, trueTokenEnabled);
    }

    @RequestMapping(value = {"/skin/{skin}/operator/{operatorId}/session/{token}"})
    public String handleStartWithPureToken(HttpServletRequest request,
                                           HttpServletResponse response,
                                           HttpSession httpSession,
                                           @PathVariable("skin") String skin,
                                           @PathVariable("operatorId") Long operatorId,
                                           @PathVariable("session") String session) {

        httpSession.setAttribute("token",session);
        String servletPath = request.getServletPath();
        return String.format("redirect:%s/session/skin/%s/operator/%s",servletPath,skin,operatorId.toString());
    }

    private String doHandleStartWithToken(HttpServletRequest request, ModelMap modelMap, String skin, Long operatorId,
        String token, boolean pure) {
        modelMap.addAttribute("cp",request.getContextPath());
        modelMap.addAttribute("operatorId",operatorId);

        Map<OperatorConfigParamDTO, String> opConfig = safeGetOperatorConfig(operatorId);

        if(token==null || !token.matches(SAFE_PATTER)) {
            modelMap.addAttribute("token","");
        } else {
            modelMap.addAttribute("token",token);
        }
        if(skin==null || !skin.matches(SAFE_PATTER)) {
            modelMap.addAttribute("skin","");
        }
        if(opConfig != null && opConfig.get(CSS_URL) != null) {
            modelMap.addAttribute("cssOverride", opConfig.get(CSS_URL));
        }

        if(opConfig!=null && opConfig.get(CLIENT_TITLE) != null) {
            modelMap.addAttribute("clientTitle", opConfig.get(CLIENT_TITLE));
        }

        modelMap.addAttribute("pureToken", pure);
        
        checkSetFirebaseAttributes(modelMap);
        
        return "index";
    }
    
	private Map<OperatorConfigParamDTO, String> safeGetOperatorConfig(Long operatorId) {
		try {
			return operatorService.getConfig(operatorId); // operatorConfig.get(operatorId);
		} catch (Exception e) {
			Logger.getLogger(getClass()).error("failed to get operator config", e);
			return null;
		}
	}

    @RequestMapping(value = {"/operator/{operatorId}/token/{token}"})
    public String handleStartWithTokenAndDefaultSkin(HttpServletRequest request,
                                                     HttpSession session,
                                                     @PathVariable("operatorId") Long operatorId,
                                                     @PathVariable("token") String token) {

        return setSessionAttributeAndRedirect(request, session, defaultSkin, operatorId, token,"");
    }


    @RequestMapping(value = {"/skin/{skin}/hand-history/{tableId}"})
    public String handleHansHistory(HttpServletRequest request, ModelMap modelMap,
                                                     @PathVariable("skin") String skin,
                                                     @PathVariable("tableId") Integer tableId) {

        if(skin==null || !skin.matches(SAFE_PATTER)) {
            modelMap.addAttribute("skin","");
        }
        modelMap.addAttribute("tableId",tableId);
        modelMap.addAttribute("cp",request.getContextPath());
        return "hand-history";
    }
    @RequestMapping(value = {"/ping"})
    public @ResponseBody String ping() {
        return "";
    }

    public void setDefaultSkin(String defaultSkin) {
        this.defaultSkin = defaultSkin;
    }
}
