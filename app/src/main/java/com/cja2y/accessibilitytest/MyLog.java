package com.cja2y.accessibilitytest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.format.Time;
import android.util.Log;

public class MyLog
{
	private static String phoneID=android.os.Build.ID;
	public static class MLog  //��̬��
	{
		
		/**
		 * ��������Ψһ���⹫�����½��߳�ִ�У������������󣬵��³������
		 */
		public static void Log()
		{
			Thread th=new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					getLog();
				}
			});

			th.start();
			
		}
		
		/**
		 * ������־
		 */
		private static void getLog()
		{
			System.out.println("--------func start--------"); 
			try
			{
				ArrayList<String> cmdLine=new ArrayList<String>();   
				cmdLine.add("logcat");
				cmdLine.add("-d");
				
				ArrayList<String> clearLog=new ArrayList<String>(); 
				clearLog.add("logcat");
				clearLog.add("-c");
				
				Process process=Runtime.getRuntime().exec(cmdLine.toArray(new String[cmdLine.size()]));   
				BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(process.getInputStream()));

				String str=null;
				while((str=bufferedReader.readLine())!=null)	
				{
					Runtime.getRuntime().exec(clearLog.toArray(new String[clearLog.size()])); 
					
					//sendLogUDP(logToJobj(str));
					System.out.println("cja2y log" + str);
					
				}
				if(str==null)
				{
					System.out.println("--   is null   --");
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			System.out.println("--------func end--------");
		}
		
		/**
		 * ��Logת����JSONObject
		 * @param s
		 * @return
		 */
		private static JSONObject logToJobj(String s)
		{
			
			String s1=s.substring(0,1);
			
			String s2=s.substring(s.indexOf('/')+1, s.indexOf('('));
			
			String s3=s.substring(s.indexOf('(')+1, s.indexOf(')'));
			
			String s4=s.substring(s.indexOf(':')+1);
			
			
			JSONObject obj=new JSONObject();
			try
			{
				obj.put("PhoneID", phoneID);
				obj.put("Type", s1.trim());
				obj.put("Tag", s2.trim());
				obj.put("ThreadID", s3.trim());
				obj.put("Msg", s4.trim());
				obj.put("Time", getTime());
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			return obj;
		}
		
		/**
		 * ͨ��UDP��ʽ������ݵ�������
		 * @param obj
		 */
		private static void sendLogUDP(JSONObject obj)
		{
			String objStr=obj.toString();
			int port=8081;
			DatagramSocket ds=null;
			InetAddress ia=null;
			if(obj!=null)
			{
				try
				{
					ds=new DatagramSocket();
					ia=InetAddress.getByAddress(null);
					//Log.d("cja2y",ia.toString());
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				int msgLenght=objStr.length();
				byte[] msgByte=objStr.getBytes();
				
				DatagramPacket dp=new DatagramPacket(msgByte, msgLenght, ia, port);
				
				try
				{
					ds.send(dp);
				}
				catch (Exception e) {
					e.printStackTrace();
					// TODO: handle exception
				}
			}
		}
	
		/**
		 * ��ȡ��ǰʱ��
		 * @return
		 */
		private static String getTime()
		{
	        Time time = new Time("GMT+8");    
	        time.setToNow();
	        int year = time.year;
	        int month = time.month;
	        int day = time.monthDay;
	        int minute = time.minute;
	        int hour = time.hour;
	        int sec = time.second;	        
	        return year+"-"+month+"-"+day+" "+hour+":"+minute+":"+sec;
		}
	}
}