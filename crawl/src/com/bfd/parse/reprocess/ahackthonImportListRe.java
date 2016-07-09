package com.bfd.parse.reprocess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bfd.crawler.utils.JsonUtils;
import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;
import com.bfd.parse.util.ahackthonUtil;

public class ahackthonImportListRe implements ReProcessor {
	private static final Log LOG = LogFactory.getLog(ahackthonImportListRe.class);
	private Pattern postTimePartten = Pattern.compile("([0-9]*/[0-9]*/[0-9]*)");
	
	@SuppressWarnings("unchecked")
	public ReProcessResult process(ParseUnit unit, ParseResult result, ParserFace arg2) {
		Map<String, Object> processdata = new HashMap<String, Object>();
		Map<String, Object> resultData = result.getParsedata().getData();
		try {
			//处理发表时间
			if(resultData.containsKey("items")){
				List<Map<String, Object>> items = (List<Map<String, Object>>) resultData.get("items");
				for(Map<String, Object> item : items){
					if(item.containsKey("posttime")){
						String pTime = (String) item.get("posttime");
						Matcher pTimeMatch = postTimePartten.matcher(pTime);
						if(pTimeMatch.find()){
							String postTime = pTimeMatch.group(1).replace("/", "-");
							item.put("posttime", postTime);
						}
					}
				}
				resultData.put("items", items);
			}
			//items里增加iid与url
			ahackthonUtil.getIid(resultData);
			LOG.info("url:" + unit.getUrl() + "after:" + JsonUtils.toJSONString(resultData));
			return new ReProcessResult(SUCCESS, processdata);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("url:" + unit.getUrl() + " reprocess error!");
			return new ReProcessResult(FAILED, processdata); 
		}
	}
}
