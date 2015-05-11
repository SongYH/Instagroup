package InstaParser;

import java.util.*;

/*
 * 1���� �Խñۿ� 
 * ���� ���̵� / ���ھ��̵� / �±�(tags) / �̹���(3����) 
 * ��¥(���н���¥) / ���(���+�ױ�) / ���ƿ� ���� / ��� ����
 * �̹��� - low_resolution (306x306) / thumbnail (150x150) / standard_resolution (640x640)
 */
public class InstaInfo {
	String stringID; // ex) railrac007
	String numberID; // ex) 926664164
	String postID; // �Խù� ���� ��ȣ	
	List<String> tags; // �ν�Ÿ�׷��� �±� ����
	String imagesURL; // ���� ū �̹��� 1��
	int createdTime; // ���н� �ð�
	String content; // ���
	
	int likeCount; // ���ƿ� ����
	int replyCount; // ��� ����
	
	
	//imageURL�� ��� ũ�⿡ ���� 3������
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
