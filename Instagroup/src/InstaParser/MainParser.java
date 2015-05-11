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
 *  Input 검색어 
 *  Output 인스타그램 결과 
 *  인스타그램 결과 - 문자 아이디(user-username)/ 숫자아이디 / 태그(tags) / 이미지 / 날짜(유닉스날짜) / 멘션(멘션+테그) / 좋아요 갯수 / 댓글 갯수
 *  태그 - tags
 *  
 *
 */
public class MainParser {
	public static void main(String[] args) {
		
		//예약 검색어 목록
		ArrayList<String> searchWordList = new ArrayList<String>();
	
		searchWordList.add("셀카");
		searchWordList.add("팔로우");
		searchWordList.add("일상");
		searchWordList.add("패션");
		searchWordList.add("신발");
		searchWordList.add("커플");
		searchWordList.add("음식");
		searchWordList.add("디저트");
		searchWordList.add("술");
		searchWordList.add("카페");
		searchWordList.add("육아");
		searchWordList.add("여행");
		searchWordList.add("강아지");
		searchWordList.add("고양이");
		searchWordList.add("사랑");
		searchWordList.add("운동");
		searchWordList.add("연예");
		searchWordList.add("꽃");
		searchWordList.add("책");
		searchWordList.add("다이어트");
		searchWordList.add("뷰티");
		searchWordList.add("축구");
		searchWordList.add("운동");
		searchWordList.add("음악");
		searchWordList.add("영화");
		
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
		URL requestURL; // access_token을 가진 처음 요청 URL
		String nextURL = ""; // api 검색후 api에서 제공하는 다음 URL
		HttpURLConnection httpCon; 
		
		Connection dbConn = null;
		java.sql.Statement stmt = null;
		String postInsertSQL = null; // 게시글 insertSQL 문
		String tagInsertSQL = null; // 테그 insertSQL 문
		String startURL = null;
		System.out.println(searchWord + " 수집중...");
			for (int i = 0; i<50; i++) {
				
				
				//DB Connection
				try {
					 String url = "jdbc:mysql://202.31.202.199:3306/testinsta?useUnicode=true&characterEncoding=euckr";
					 dbConn = DriverManager.getConnection(url,"root","kle445");
					 stmt = dbConn.createStatement();
			         System.out.println("mysql 접속 성공");
			         
				 }catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("DB 오류");
				}
				//end of DB Connection
				
		/*		//너무 빠르면 안되니 0.5초의 sleep
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//end of sleep
		*/		
				Date date = new Date(); // 가져온 날짜 확인하기 위해서
			    System.out.println("----- "+searchWord  + i +" -----" + date.toString());
			    
			    //api 토큰은 내꺼 가져옴
				if (i == 0) { // 첫번째면 URL 생성
					startURL = "https://api.instagram.com/v1/tags/"+ URLEncoder.encode(searchWord, "UTF-8")
							    + "/media/recent?access_token=1583007482.1fb234f.395c91113a6d43be81db7a7d25a5b02e";
					requestURL = new URL(startURL);
					
			/* 요청했던 URL 저장(처음과 마지막만)	
					try {
						java.sql.ResultSet rs = stmt.executeQuery( "SELECT * FROM requesturl where starturl = '"+startURL+"'");               
						rs.beforeFirst();
						if(!rs.next()) // 중복 없으면
						{
							String startURLInsertSQL = " insert requesturl (searchword, starturl, starttime) values('"+searchWord+"','"+startURL+"','"+date.toString()+"')";
							stmt.executeUpdate( startURLInsertSQL );
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("startURLInsertSQL 오류");
					}
				*/	
				} 
				else // 첫번째가 아니라면 requsetURL을 api에서 나오는 NextURL로 변경 
				{ 
					requestURL = new URL(nextURL);
				}
				
				httpCon = (HttpURLConnection) requestURL.openConnection(); // URL 연결
				Object instaObject = JSONValue.parse(new InputStreamReader(httpCon.getInputStream()));
				JSONObject jInstaObject = (JSONObject) instaObject;

				// json으로 데이터를 받아옴
				JSONArray datas = (JSONArray) jInstaObject.get("data"); // data로 묶여진(게시글) 것들을 가지고 옴 (20개의 게시글 list)
				Iterator<JSONObject> datas_iterator = datas.iterator();

				// next_url이 있는 부분 pagination
				JSONObject next_url_str = (JSONObject) jInstaObject.get("pagination");
				nextURL = (String) next_url_str.get("next_url"); // 다음 url을 추출
				System.out.println("게시글 갯수 : " + datas.size());
				System.out.println("요청 URL : " + requestURL);
				System.out.println("다음 URL : " + nextURL);
			
			/* 처음과 끝 요청 URL 저장하기(마지막 부분)	
				try {
					String endURLUpdateSQL = " update requesturl set endurl = '"+nextURL+"', endtime = '"+date.toString()+"' where searchword = '"+searchWord+"' AND starturl = '"+startURL+"'";
					System.out.println(endURLUpdateSQL);
					stmt.executeUpdate( endURLUpdateSQL );
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("endURLUpdateSQL 오류");
				}
			*/
				// 하나의 게시글 (최대 20개)
				while (datas_iterator.hasNext()) {
					InstaInfo instainfo = new InstaInfo();
					List<String> tempTags = new ArrayList<String>();

					JSONObject index = datas_iterator.next();

					// 태그 가져오기
					// 태그의 갯수만큼 for문 동작
					JSONArray tags = (JSONArray) index.get("tags"); // 한개의 글 안에 속한 태그들을 다 들고옴
					for (int j = 0; j < tags.size(); j++) {
						tempTags.add((String) tags.get(j));
					}
					// 다가져온후 InstaInfo에 저장
					instainfo.setTags(tempTags);

					// 게시글 고유번호 가져오기
					String postID = (String) index.get("id");
					instainfo.setPostID(postID);
					
//					System.out.println(postID);
					// 작성날짜가져오기
					// String으로 가져와서 Int로 저장
					String stringTime = (String) index.get("created_time");
					instainfo.setCreateTime(Integer.parseInt(stringTime));

					
					//null 확인해주기 
					// 본문 내용 가져오기
					JSONObject caption = (JSONObject) index.get("caption");
					try{
						instainfo.setContent((String) caption.get("text"));
					}catch(NullPointerException e)
					{
						System.out.println("Text Null");
						instainfo.setContent("");
					}
					
					// image추출 0 - 150x150(thumbnail) / 1 -
					// 306x306(low_resolution) / 2 -
					// 640x640(standard_resolution)
					JSONObject images = (JSONObject) index.get("images");
					JSONObject standard_resolution = (JSONObject) images.get("standard_resolution");

					instainfo.setImagesURL((String) standard_resolution.get("url"));

					// 댓글 갯수 가져오기
					JSONObject reply = (JSONObject) index.get("comments");
					instainfo.setReplyCount(Integer.parseInt(reply.get("count").toString()));

					// 좋아요 갯수 가져오기
					JSONObject like = (JSONObject) index.get("likes");
					instainfo.setLikeCount(Integer.parseInt(like.get("count").toString()));

					// 사용자 정보가져오기
					JSONObject user = (JSONObject) index.get("user");
					instainfo.setStringID((String) user.get("username"));
					instainfo.setNumberID((String) user.get("id"));

					//게시글 DB에 저장하기
					
					try {
						java.sql.ResultSet rs = stmt.executeQuery( "SELECT * FROM post where postid = '"+instainfo.postID+"'");               
						rs.beforeFirst();
						if(!rs.next()) // 중복된 게시글이 없다면
						{
							postInsertSQL =  " insert into post values('"+instainfo.postID+"','"+instainfo.stringID+"','"+instainfo.numberID
			                          +"','"+instainfo.createdTime+"','"+instainfo.content+"','"+instainfo.likeCount+"','"+instainfo.replyCount+"','"+instainfo.getImagesURL()+"','"+instainfo.tags+"')";
							
							stmt.executeUpdate( postInsertSQL ); // 게시글 저장하기
							/*
							//tag 따로 저장
							List<String> temp = instainfo.getTags();
							for(int tagIndex = 0; tagIndex<temp.size(); tagIndex++)
							{
								//searchWord
								tagInsertSQL =  " insert tags values('"+instainfo.postID+"','"+temp.get(tagIndex)+"')";
								stmt.executeUpdate( tagInsertSQL );
								
								//검색어랑 태그랑 같으면 저장 안함
								if(!searchWord.equals(temp.get(tagIndex)))
								{
									java.sql.ResultSet rsTag = stmt.executeQuery( "SELECT * FROM searchwordlist where searchword = '"+searchWord+"' AND tag = '"+temp.get(tagIndex)+"'");               
									rsTag.beforeFirst();
								
									// 제주도 한라산 0 / 제주도 게스트하우스 1 이런식의 저장
									if(!rsTag.next()) // 연관 tag가 없다면
									{
										String searchWordInsertSQL = " insert searchwordlist values('"+searchWord+"','"+temp.get(tagIndex)+"','1')";
										System.out.println(searchWordInsertSQL);
										stmt.executeUpdate( searchWordInsertSQL );
									}
									else // 등록 되어 있다면
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
						System.out.println("insert 오류");
						System.out.println(postInsertSQL);
						System.out.println(tagInsertSQL);
					}       
				} // end of while(게시글 목록)
				
				
				if (datas.size() < 20 || nextURL == null) { // data 크기가 20개가 안되면 -> 마지막 검색 결과
					System.out.println("종료");
					break;
				}
				
			     
		         try {
					dbConn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		   } // end of for(인스타 몇번 반복 할지)
			// return instaInfoList;
	}

	// Map 정렬
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

		Collections.reverse(list); // 주석시 오름차순
		return list;
	}
}
