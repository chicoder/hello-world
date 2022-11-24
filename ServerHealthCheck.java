package nexcore.framework.custom.nexbank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerHealthCheck {

	public static void main(String[] args) { 
		
		String[] serverAddress = new String[] {"http://127.0.0.1:8080", "http://nexcore.skcc.com"};
		
		ServerHealthCheck server = new ServerHealthCheck();
		for(String addr : serverAddress) {
			server.doWork(addr);
		}
	}
	
	private void doWork(final String serverAddress) {
		Thread t = new Thread() {
			public void run() {
				System.out.println("[" + this.getName() + "] service test... " + serverAddress);
				while(true) {
					if(!isTargetServerAlive(serverAddress)) {
						System.out.println("[" + this.getName() + "] connection failed");
					}
					
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			private boolean isTargetServerAlive(String serverAddress) {
				HttpURLConnection conn = null;
				BufferedReader    in   = null;
				try {
					URL url = new URL(serverAddress);
					String healthCheckUrl = url.getProtocol()+"://"+url.getHost()+":"+url.getPort();
					
					//System.out.println("[" + this.getName() + "] url : " + healthCheckUrl);
					conn = (HttpURLConnection) new URL(healthCheckUrl).openConnection();
					conn.setUseCaches(false);
					
					conn.setRequestMethod("GET");
					conn.setRequestProperty("User-Agent", "NEXCORE CenterCut Runner");
					conn.setConnectTimeout(30 * 1000);
					conn.setReadTimeout(30 * 1000);
					
					int result = conn.getResponseCode();
					if (result == HttpURLConnection.HTTP_OK) {
						//throw new CCException("NJF57304", new Object[]{result, conn.getResponseMessage()});
						return true;
					} else {
						return false;
					}
					
//		            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//		            String inputLine;
//		            while ((inputLine = in.readLine()) != null) {
//		            	inputLine = inputLine.trim();
//		            	if (inputLine.length() == 0) {
//		            		continue; // 공백 라인 무시.
//		            	}else {
//		            		if ("OK".equals(inputLine)) { // Health.jsp 는 정상상황에서 "OK" 를 리턴한다.
//		            			return true;
//		            		}
//		            	}
//		            }
		            
		            // "OK" 응답을 못받은 경우.
//		            return false;
				}catch(Exception e) {
					// 접속 에러.
					return false;
				}finally {
					if(in != null) {
						try {
							in.close();
						} catch (IOException e) {
						}
					}
					if(conn != null) {
						try { 
							conn.disconnect(); 
						}catch(Exception ignore) {
						}
					}
				}
			}
		};
		
		t.start();
	}
	
}
