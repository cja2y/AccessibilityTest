package com.cja2y.accessibilitytest;

import java.io.DataOutputStream;
import java.io.OutputStream;

/**
 * Created by Administrator on 2016/4/21.
 */
public class ExecCommandUtil {
    //**********************************通过天津键盘位置用adb命令模拟点击按键***************************************************//
    public static final  String password_edittext_pos = " 492 591";
    public static final  String keyboard_change_pos = " 15 1080";
    public static final  String keyboard_close_pos = "600 1080";
    public static final String y_6_pos = " 388 791";
    public static final  String t_5_pos = " 300 791";
    public static final  String r_4_pos = " 230 791";
    public static final String  e_3_pos =" 150 791";
    public static final  String w_2_pos =" 85 791";
    public static final String q_1_pos =" 15 791";
    public static final String u_7_pos =" 460 791";
    public static final String i_8_pos = " 540 791";
    public static final String o_9_pos = " 620 791";
    public static final String p_0_pos = " 700 791";


    public static void passwordAction(String password){
        String currentCommand = "";
        currentCommand = "input tap 492 591"+"&&"+"input tap 15 1080";
        for(int i=0;i<7;i++){
            currentCommand += "&&"+"input tap 650 990";
        }
        for (int i=0;i<password.length();i++){
            currentCommand +="&&"+getCommand(password.substring(i,i+1));
        }
        currentCommand += "&&"+"input tap 600 1080";//**************************600 1080 坐标：收起键盘
        try {
            execShell(currentCommand);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void execShell(String cmd){
        try{
            //权限设置
            Process p = Runtime.getRuntime().exec("su");
            //获取输出流
            OutputStream outputStream = p.getOutputStream();
            DataOutputStream dataOutputStream=new DataOutputStream(outputStream);
            //将命令写入
            dataOutputStream.writeBytes(cmd);
            //提交命令
            dataOutputStream.flush();
            //关闭流操作
            dataOutputStream.close();
            outputStream.close();
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
    }
    public static String getCommand(String s){
        return "input tap"+getPos(s);
    }
    public static String getPos(String s){
        switch (s){
            case "0":return p_0_pos;
            case "1":return q_1_pos;
            case "2":return w_2_pos;
            case "3":return e_3_pos;
            case "4":return r_4_pos;
            case "5":return t_5_pos;
            case "6":return y_6_pos;
            case "7":return u_7_pos;
            case "8":return i_8_pos;
            case "9":return o_9_pos;
            default:
                return null;
        }
    }

}
