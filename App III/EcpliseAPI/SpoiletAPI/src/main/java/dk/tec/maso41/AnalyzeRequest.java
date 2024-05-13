package dk.tec.maso41;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalyzeRequest {

	private MatchEnum match;
	private int id;
	
	public MatchEnum getMatch() {
		return match;
	}
	
	public int getId() {
		return id;
	}
	
	/**
	 * We analyse the request sent to the API so we know whether the GET/POST/DELETE/PUT
	 * is in regards to a Toilet, Haircolor or Programming Language.
	 * Note we take the potential for IDs first to avoid false-positives, should we check generalized paths first.
	 * @param pathInfo
	 */
	public AnalyzeRequest(String pathInfo) {
		Matcher toiletMatcher = Pattern.compile("/Toilet/([0-9]+)").matcher(pathInfo);
		
		if (toiletMatcher.find()) {
			match = MatchEnum.MatchToiletId;
			id = Integer.parseInt(toiletMatcher.group(1));
		} else {
			match = MatchEnum.MatchNo;
		}
		
	}
}
