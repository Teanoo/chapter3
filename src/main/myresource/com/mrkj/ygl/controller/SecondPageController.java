package com.mrkj.ygl.controller;

import com.mrkj.ygl.service.SecondPageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class SecondPageController {
    //注入Service
    @Resource
    SecondPageService sps;

    @RequestMapping(value="/secondPageContent")
    public ModelAndView goSecondPage(String mainId,
                                     @RequestParam(name="page",defaultValue="1") Integer page,
                                     @RequestParam(name="row",defaultValue="15")Integer row){
        ModelAndView mav = new ModelAndView("myJSP/secondPage");
        //根据传递过来的mainId查找my_main与my_second表
        Map<String, Object> mainAndSecond = sps.getMainAndSeconds(mainId,(page-1)*row, row);
        //将返回值传递给JSP
        mav.addObject("mainAndSeconds", mainAndSecond);

        Long count = sps.getSecondCount(mainId);
        Map<String,String> parm = new HashMap<>();
        parm.put("mainId", mainId);
        String pageHtml = sps.getPage(count, page, row,parm);
        mav.addObject("pageHtml", pageHtml);

        return mav;
    }


    //接收JSP传递过来的参数main_id与富文本content，保存至数据库my_second表中
    @RequestMapping(value="/saveSecondPage")
    public ModelAndView saveSecondPage(HttpServletRequest request,
                                       String mainId,String content){
        ModelAndView mav = new ModelAndView();
        String mainCreatuser = request.getRemoteAddr();
        int result = sps.saveSecondPage(mainId, content, mainCreatuser);
        if (result == 1){
            mav.setViewName("redirect:/secondPageContent?mainId="+mainId);
        }else{
            mav.setViewName("404");
        }
        return mav;
    }


}
