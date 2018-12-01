package com.mrkj.ygl.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MainPageService {

	//注入Spring JdbcTemplate
	@Resource
	JdbcTemplate jdbc;
	//注入时间格式化
	@Resource
	SimpleDateFormat sdf;
	/**
	 * 
	 * @param content 帖子内容
	 * @param mainTitle 帖子标题
	 * @param mainCreatuser 发帖人，这里我们使用用户IP作为发帖
	 * @return
	 */
	public int saveMainContent(String content,String mainTitle,String mainCreatuser){
		//定义sql语句，这里的sql使用的是防注入模式，VALUES的值使用的是?占位符
		String sql_save_mymain = "INSERT INTO my_main "
		+ "(main_id,main_title,main_content,"
		+ "main_creatime,main_creatuser,main_commend)"
		+ " VALUES (?,?,?,?,?,?)";
		
		String sql_save_myinfo = "INSERT INTO my_info "
				+ "(main_id,info_reply,info_see,"
				+ "info_lastuser,info_lastime) "
				+ "VALUES (?,0,0,?,?)";

		//表id使用的是UUID
		String mainId = UUID.randomUUID().toString();
		//时间格式化，格式要与数据库中的datatime相对应yyyy-MM-dd hh:mm:ss
		sdf.applyPattern("yyyy-MM-dd hh:mm:ss");
		//获取当前时间作为创建时间
		String mainCreatime = sdf.format(new Date());
		//精华帖标记，0普通帖，1精华帖
		Integer mainCommend = 0;
		
		//初始化myinfo表数据，注意my_info表的id为自增长所以这里并没有设置info_id的值
		jdbc.update(sql_save_myinfo, mainId,mainCreatuser,mainCreatime);

		
		//执行update语句，第一个参数sql语句，后面可以写任意多的参数
		return jdbc.update(sql_save_mymain, 
				mainId,mainTitle,content,mainCreatime,mainCreatuser,mainCommend);
		
	}
	public List<Map<String, Object> >  getMainPage(int row,int offset){
		//分页查找my_main左连接(left join)my_info约定好每页最多显示40条帖子
		String sql_select_mymain = "SELECT main.*,info.info_id,info.info_reply,info.info_see,"
			+ "info.info_lastuser,info.info_lastime FROM mrbbs.my_main as main "
			+ "left join my_info as info on main.main_id = info.main_id "
			+ "order by main.main_commend,main.main_creatime desc limit ?,?";
		//一定要认真仔细的查看空格，代码4、5行结尾有一个空格
		return jdbc.queryForList(sql_select_mymain,row,offset);
	}
	public Long getMainCount(){
	//是用count关键字，查询总条数
	String sql_select_mymain = "select count(main_id) as count from my_main";
	//执行SQL语句，返回总条数
	return (Long)jdbc.queryForMap(sql_select_mymain).get("count");
}

	public String getPage (Long count,Integer currentPage,Integer offset){
		//数据
		Long currentLong = Long.parseLong(currentPage+"");
		Long countPage = 0L;
		//这里计算总页数
		if(count%offset!=0){
			countPage = count/offset+1; 
		}else{
			countPage = count/offset;
		}
		//使用StringBuffer拼接字符串
		StringBuffer sb = new StringBuffer();
		//前一页判断，判断当前页数大于1则存在前一页，否则不存在前一页
		if (currentPage> 1){
			sb.append("<span class=\"page\"> <a href=\"?page="+(currentPage-1));
			sb.append("\"> «</a> </span> ");
		}else{
			sb.append("<span class=\"page\"> <a href=\"?page=1");
			sb.append("\"> «</a> </span> ");
		}
		sb.append("<span class=\"page\" style=\"width: 50px !important;\"> ");
		sb.append("<a href=\"?page=1");
		sb.append("\"> start</a> ");
		sb.append("</span> ");
		
		//中间页数导航，中间最多显示5页，这里的计算有些复杂，判断了三次
		//第一次判断总页数减去当前页数加1大于等于5，证明向后存在5页
		//假设我们当前页数为2，那么我们中间导航显示为2、3、4、5、6
		if ((countPage-currentLong+1) >=5){
			for (Long i = currentLong ; i<currentPage+5;i++){
				sb.append("<span class=\"page\"> ");
				sb.append("<a href=\"?page="+i);
				sb.append("\"> "+i+"</a> ");
				sb.append("</span> ");
			}
		//第二次判断，基于上一次的判断不成立，那么证明当前页数向后不足5页
		//这时候判断总页数减4，判断中间导航是否能够支撑5页，假设总页数为10
		//当前页数为7，7向后不足5页，那么判断总页数是否够支撑5页，用总页数-4
		//如果够5页，那么得出一个结论是当前页数向后不够5页，总页数大于或等于5页
		//当前页数包含在最后5页，那么中间导航显示的就是6、7、8、9、10
		}else if (countPage-4 >  0){
			for (long i = countPage-4 ; i<= countPage;i++){
				sb.append("<span class=\"page\"> ");
				sb.append("<a href=\"?page="+i);
				sb.append("\"> "+i+"</a> ");
				sb.append("</span> ");
			}
		//经过上面两轮的判断，可以直接得出结论，总页数不足支撑5页
		//那么从1开始到总页数结束
		}else{
			for (long i = 1 ; i<= countPage;i++){
				sb.append("<span class=\"page\"> ");
				sb.append("<a href=\"?page="+i);
				sb.append("\"> "+i+"</a> ");
				sb.append("</span> ");
			}
		}
		//判断最后一页，最后一页等于总页数，这里只要判断是否存在1页，不存在最后一页设为1
		sb.append("<span class=\"page\" style=\"width: 40px !important;\"> ");
		sb.append("<a href=\"?page="+(countPage==0?1:countPage));
		sb.append("\"> end</a> ");
		sb.append("</span> ");
		//判断是否存在下一页，当前页数小于总页数，那么存在最后一页
		if (currentLong<countPage){
			sb.append("<span class=\"page\"> ");
			sb.append("<a href=\"?page="+currentLong+1);
			sb.append("\"> »</a> ");
			sb.append("</span> ");
		}else{
			sb.append("<span class=\"page\"> ");
			sb.append("<a href=\"?page="+currentLong);
			sb.append("\"> »</a> ");
			sb.append("</span> ");
		}
		//输出总页数
		sb.append("<span> ");
		sb.append("共"+countPage+"页");
		sb.append("</span> ");
		
		return sb.toString();
	}


}

