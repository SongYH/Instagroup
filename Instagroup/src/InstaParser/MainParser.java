package InstaParser;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.json.simple.*;

/*
 *  Input �˻��� 
 *  Output �ν�Ÿ�׷� ��� 
 *  �ν�Ÿ�׷� ��� - ���� ���̵�(user-username)/ ���ھ��̵� / �±�(tags) / �̹��� / ��¥(���н���¥) / ���(���+�ױ�) / ���ƿ� ���� / ��� ����
 *  �±� - tags
 *  
 *
 */
public class MainParser {
	public static void main(String[] args) {
		
		//���� �˻��� ���
		ArrayList<String> searchWordList = new ArrayList<String>();
	
		searchWordList.add("��ī");
		searchWordList.add("�ȷο�");
		searchWordList.add("�ϻ�");
		searchWordList.add("�м�");
		searchWordList.add("�Ź�");
		searchWordList.add("Ŀ��");
		searchWordList.add("����");
		searchWordList.add("����Ʈ");
		searchWordList.add("��");
		searchWordList.add("ī��");
		searchWordList.add("����");
		searchWordList.add("����");
		searchWordList.add("������");
		searchWordList.add("�����");
		searchWordList.add("���");
		searchWordList.add("�");
		searchWordList.add("����");
		searchWordList.add("��");
		searchWordList.add("å");
		searchWordList.add("���̾�Ʈ");
		searchWordList.add("��Ƽ");
		searchWordList.add("�౸");
		searchWordList.add("�");
		searchWordList.add("����");
		searchWordList.add("��ȭ");
		
		try {
			for(String word : searchWordList)
			{
				getInstaInformation(word);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void getInstaInformation(String searchWord) throws IOException 
	{
		URL requestURL; // access_token�� ���� ó�� ��û URL
		String nextURL = ""; // api �˻��� api���� �����ϴ� ���� URL
		HttpURLConnection httpCon; 
		
		Connection dbConn = null;
		java.sql.Statement stmt = null;
		String postInsertSQL = null; // �Խñ� insertSQL ��
		String tagInsertSQL = null; // �ױ� insertSQL ��
		String startURL = null;
		System.out.println(searchWord + " ������...");
			for (int i = 0; i<50; i++) {
				
				
				//DB Connection
				try {
					 String url = "jdbc:mysql://202.31.202.199:3306/testinsta?useUnicode=true&characterEncoding=euckr";
					 dbConn = DriverManager.getConnection(url,"root","kle445");
					 stmt = dbConn.createStatement();
			         System.out.println("mysql ���� ����");
			         
				 }catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("DB ����");
				}
				//end of DB Connection
				
		/*		//�ʹ� ������ �ȵǴ� 0.5���� sleep
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//end of sleep
		*/		
				Date date = new Date(); // ������ ��¥ Ȯ���ϱ� ���ؼ�
			    System.out.println("----- "+searchWord  + i +" -----" + date.toString());
			    
			    //api ��ū�� ���� ������
				if (i == 0) { // ù��°�� URL ����
					startURL = "https://api.instagram.com/v1/tags/"+ URLEncoder.encode(searchWord, "UTF-8")
							    + "/media/recent?access_token=1583007482.1fb234f.395c91113a6d43be81db7a7d25a5b02e";
					requestURL = new URL(startURL);
					
			/* ��û�ߴ� URL ����(ó���� ��������)	
					try {
						java.sql.ResultSet rs = stmt.executeQuery( "SELECT * FROM requesturl where starturl = '"+startURL+"'");               
						rs.beforeFirst();
						if(!rs.next()) // �ߺ� ������
						{
							String startURLInsertSQL = " insert requesturl (searchword, starturl, starttime) values('"+searchWord+"','"+startURL+"','"+date.toString()+"')";
							stmt.executeUpdate( startURLInsertSQL );
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("startURLInsertSQL ����");
					}
				*/	
				} 
				else // ù��°�� �ƴ϶�� requsetURL�� api���� ������ NextURL�� ���� 
				{ 
					requestURL = new URL(nextURL);
				}
				
				httpCon = (HttpURLConnection) requestURL.openConnection(); // URL ����
				Object instaObject = JSONValue.parse(new InputStreamReader(httpCon.getInputStream()));
				JSONObject jInstaObject = (JSONObject) instaObject;

				// json���� �����͸� �޾ƿ�
				JSONArray datas = (JSONArray) jInstaObject.get("data"); // data�� ������(�Խñ�) �͵��� ������ �� (20���� �Խñ� list)
				Iterator<JSONObject> datas_iterator = datas.iterator();

				// next_url�� �ִ� �κ� pagination
				JSONObject next_url_str = (JSONObject) jInstaObject.get("pagination");
				nextURL = (String) next_url_str.get("next_url"); // ���� url�� ����
				System.out.println("�Խñ� ���� : " + datas.size());
				System.out.println("��û URL : " + requestURL);
				System.out.println("���� URL : " + nextURL);
			
			/* ó���� �� ��û URL �����ϱ�(������ �κ�)	
				try {
					String endURLUpdateSQL = " update requesturl set endurl = '"+nextURL+"', endtime = '"+date.toString()+"' where searchword = '"+searchWord+"' AND starturl = '"+startURL+"'";
					System.out.println(endURLUpdateSQL);
					stmt.executeUpdate( endURLUpdateSQL );
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("endURLUpdateSQL ����");
				}
			*/
				// �ϳ��� �Խñ� (�ִ� 20��)
				while (datas_iterator.hasNext()) {
					InstaInfo instainfo = new InstaInfo();
					List<String> tempTags = new ArrayList<String>();

					JSONObject index = datas_iterator.next();

					// �±� ��������
					// �±��� ������ŭ for�� ����
					JSONArray tags = (JSONArray) index.get("tags"); // �Ѱ��� �� �ȿ� ���� �±׵��� �� ����
					for (int j = 0; j < tags.size(); j++) {
						tempTags.add((String) tags.get(j));
					}
					// �ٰ������� InstaInfo�� ����
					instainfo.setTags(tempTags);

					// �Խñ� ������ȣ ��������
					String postID = (String) index.get("id");
					instainfo.setPostID(postID);
					
//					System.out.println(postID);
					// �ۼ���¥��������
					// String���� �����ͼ� Int�� ����
					String stringTime = (String) index.get("created_time");
					instainfo.setCreateTime(Integer.parseInt(stringTime));

					
					//null Ȯ�����ֱ� 
					// ���� ���� ��������
					JSONObject caption = (JSONObject) index.get("caption");
					try{
						instainfo.setContent((String) caption.get("text"));
					}catch(NullPointerException e)
					{
						System.out.println("Text Null");
						instainfo.setContent("");
					}
					
					// image���� 0 - 150x150(thumbnail) / 1 -
					// 306x306(low_resolution) / 2 -
					// 640x640(standard_resolution)
					JSONObject images = (JSONObject) index.get("images");
					JSONObject standard_resolution = (JSONObject) images.get("standard_resolution");

					instainfo.setImagesURL((String) standard_resolution.get("url"));

					// ��� ���� ��������
					JSONObject reply = (JSONObject) index.get("comments");
					instainfo.setReplyCount(Integer.parseInt(reply.get("count").toString()));

					// ���ƿ� ���� ��������
					JSONObject like = (JSONObject) index.get("likes");
					instainfo.setLikeCount(Integer.parseInt(like.get("count").toString()));

					// ����� ������������
					JSONObject user = (JSONObject) index.get("user");
					instainfo.setStringID((String) user.get("username"));
					instainfo.setNumberID((String) user.get("id"));

					//�Խñ� DB�� �����ϱ�
					
					try {
						java.sql.ResultSet rs = stmt.executeQuery( "SELECT * FROM post where postid = '"+instainfo.postID+"'");               
						rs.beforeFirst();
						if(!rs.next()) // �ߺ��� �Խñ��� ���ٸ�
						{
							postInsertSQL =  " insert into post values('"+instainfo.postID+"','"+instainfo.stringID+"','"+instainfo.numberID
			                          +"','"+instainfo.createdTime+"','"+instainfo.content+"','"+instainfo.likeCount+"','"+instainfo.replyCount+"','"+instainfo.getImagesURL()+"','"+instainfo.tags+"')";
							
							stmt.executeUpdate( postInsertSQL ); // �Խñ� �����ϱ�
							/*
							//tag ���� ����
							List<String> temp = instainfo.getTags();
							for(int tagIndex = 0; tagIndex<temp.size(); tagIndex++)
							{
								//searchWord
								tagInsertSQL =  " insert tags values('"+instainfo.postID+"','"+temp.get(tagIndex)+"')";
								stmt.executeUpdate( tagInsertSQL );
								
								//�˻���� �±׶� ������ ���� ����
								if(!searchWord.equals(temp.get(tagIndex)))
								{
									java.sql.ResultSet rsTag = stmt.executeQuery( "SELECT * FROM searchwordlist where searchword = '"+searchWord+"' AND tag = '"+temp.get(tagIndex)+"'");               
									rsTag.beforeFirst();
								
									// ���ֵ� �Ѷ�� 0 / ���ֵ� �Խ�Ʈ�Ͽ콺 1 �̷����� ����
									if(!rsTag.next()) // ���� tag�� ���ٸ�
									{
										String searchWordInsertSQL = " insert searchwordlist values('"+searchWord+"','"+temp.get(tagIndex)+"','1')";
										System.out.println(searchWordInsertSQL);
										stmt.executeUpdate( searchWordInsertSQL );
									}
									else // ��� �Ǿ� �ִٸ�
									{
										int tempCnt = rsTag.getInt("count");
										tempCnt+=1;
										String searchWordUpdateSQL = " update searchwordlist set count = '"+tempCnt+"' where searchword = '"+searchWord+"' AND tag = '"+temp.get(tagIndex)+"' ";
										stmt.executeUpdate( searchWordUpdateSQL );
									}
								}
							}*/
						}
					
					
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("insert ����");
						System.out.println(postInsertSQL);
						System.out.println(tagInsertSQL);
					}       
				} // end of while(�Խñ� ���)
				
				
				if (datas.size() < 20 || nextURL == null) { // data ũ�Ⱑ 20���� �ȵǸ� -> ������ �˻� ���
					System.out.println("����");
					break;
				}
				
			     
		         try {
					dbConn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		   } // end of for(�ν�Ÿ ��� �ݺ� ����)
			// return instaInfoList;
	}

	// Map ����
	public static ArrayList<String> sortByValue(final Map map) {
		ArrayList<String> list = new ArrayList();
		list.addAll(map.keySet());

		Collections.sort(list, new Comparator() {

			public int compare(Object o1, Object o2) {
				Object v1 = map.get(o1);
				Object v2 = map.get(o2);

				return ((Comparable) v1).compareTo(v2);
			}
		});

		Collections.reverse(list); // �ּ��� ��������
		return list;
	}
}
