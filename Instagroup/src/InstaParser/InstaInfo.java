package InstaParser;

import java.util.*;

/*
 * 1개의 게시글에 
 * 문자 아이디 / 숫자아이디 / 태그(tags) / 이미지(3종류) 
 * 날짜(유닉스날짜) / 멘션(멘션+테그) / 좋아요 갯수 / 댓글 갯수
 * 이미지 - low_resolution (306x306) / thumbnail (150x150) / standard_resolution (640x640)
 */
public class InstaInfo {
	String stringID; // ex) railrac007
	String numberID; // ex) 926664164
	String postID; // 게시물 고유 번호	
	List<String> tags; // 인스타그램의 태그 저장
	String imagesURL; // 제일 큰 이미지 1개
	int createdTime; // 유닉스 시간
	String content; // 멘션
	
	int likeCount; // 좋아요 갯수
	int replyCount; // 댓글 갯수
	
	
	//imageURL의 경우 크기에 따라 3종류임
	//0 - 150x150 / 1 - 306x306 / 2 - 640x640
	public InstaInfo(){
		stringID = null;
		numberID = null;
		tags = new ArrayList<String>();
		imagesURL = null;
		createdTime = 0;
		content = null;
		likeCount = 0;
		replyCount = 0;
	}
	
	//Getter
	public String getStringID() {
		return stringID;
	}
	public String getNumberID() {
		return numberID;
	}
	public String getPostID() {
		return postID;
	}
	public List<String> getTags() {
		return tags;
	}
	public String getImagesURL() {
		return imagesURL;
	}
	
	public int getCreateTime() {
		return createdTime;
	}
	public String getContent() {
		return content;
	}
	public int getLikeCount() {
		return likeCount;
	}
	public int getReplyCount() {
		return replyCount;
	}
	
	//Setter
	public void setStringID(String stringID) {
		this.stringID = stringID;
	}
	public void setNumberID(String numberID) {
		this.numberID = numberID;
	}
	public void setPostID(String postID) {
		this.postID = postID;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public void setImagesURL(String imagesURL) {
		this.imagesURL = imagesURL;
	}
	public void setCreateTime(int createdTime) {
		this.createdTime = createdTime;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}
	public void setReplyCount(int replyCount) {
		this.replyCount = replyCount;
	}
	
}
