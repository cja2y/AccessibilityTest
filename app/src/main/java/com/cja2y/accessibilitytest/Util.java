package com.cja2y.accessibilitytest;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.cja2y.accessibilitytest.async.ToolKit;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.Process;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/21.
 */
public class Util {

    //*******************************************sleep函数 模仿robotium sleep功能
    public  static  void sleep(long time){
        try {
            Thread.currentThread().sleep(time);//阻断2秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //**********************************通过天津键盘位置用adb命令模拟点击按键***************************************************//
//    public static final  String password_edittext_pos = " 335 877";//"335 877"
//    public static final  String keyboard_change_pos = " 122 1701";//"119 1826"
//    public static final  String keyboard_delete_pos = " 969 1535";//
//    public static final  String keyboard_close_pos = " 889 1698";//938 1818
//    public static final String y_6_pos = " 588 1190";//585 1352
//    public static final  String t_5_pos = " 500 1190";//469 1352
//    public static final  String r_4_pos = " 362 1190";//376 1352
//    public static final String  e_3_pos =" 183 1190";//273 1352
//    public static final  String w_2_pos =" 158 1190";//159 1352
//    public static final String q_1_pos =" 51 1190";//59 1330
//    public static final String u_7_pos =" 702 1190";//682 1352
//    public static final String i_8_pos = " 801 1190";//800 1352
//    public static final String o_9_pos = " 908 1190";//900 1352
//    public static final String p_0_pos = " 1007 1190";//1021 1352

    public static final  String login_btn_pos = " 104 162";
    public static final  String return_btn_pos = " 104 162";
    public static final String personal_account_btn_pos =" 150 788";
   // public static final String login_success_enter_pos = " 561 1077"; //" 593 1160"
    public static final String login_detail_pos = " 599 877";
    public static final String login_next_detail = " 586 271";
    public static final String login_detail_more_pos = " 955 278";
    public static final String login_detail_final_pos = " 566 1050";

    public static final  String password_edittext_pos = " 335 877";//"335 877"
    public static final  String keyboard_change_pos = " 119 1826";//" 119 1826"
    public static final  String keyboard_delete_pos = " 969 1535";//
    public static final  String keyboard_close_pos = " 938 1818";//938 1818
    public static final String y_6_pos = " 585 1352";//585 1352
    public static final  String t_5_pos = " 469 1352";//469 1352
    public static final  String r_4_pos = " 376 1352";//376 1352
    public static final String  e_3_pos =" 273 1352";//273 1352
    public static final  String w_2_pos =" 159 1352";//159 1352
    public static final String q_1_pos =" 59 1330";//59 1330
    public static final String u_7_pos =" 682 1352";//682 1352
    public static final String i_8_pos = " 800 1352";//800 1352
    public static final String o_9_pos = " 900 1352";//900 1352
    public static final String p_0_pos = " 1021 1352";//1021 1352
    public static final String login_success_enter_pos = " 593 1160"; //" 593 1160"

    public static void passwordAction(String password){
        String currentCommand = "";
        currentCommand = "input tap "+password_edittext_pos+"&&"+"input tap "+keyboard_change_pos;
//        for(int i=0;i<7;i++){
//            currentCommand += "&&"+"input tap"+keyboard_delete_pos;
//        }
        for (int i=0;i<password.length();i++){
            currentCommand +="&&"+getCommand(password.substring(i,i+1));
        }
        currentCommand += "&&"+"input tap"+keyboard_close_pos;//**************************600 1080 坐标：收起键盘
        try {
            execShell(currentCommand);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void execShell(String cmd){//android 执行adb命令
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


    public static void startTJAPP(Context context){
       // killTJAPP(context);
        ComponentName com = new ComponentName("com.tayh.gjjclient","com.tayh.gjjclient.MainActivity");
        Intent intent = new Intent();
        intent.setComponent(com);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    public static void openDetailActivity(Context context){
        ComponentName com = new ComponentName("com.tayh.gjjclient","com.tayh.gjjclient.GetGrzhDetailThreeActivity");
        Intent intent = new Intent();
        intent.setComponent(com);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }
    public static void killTJAPP(Context context){
        //System.exit(0);
       // android.os.Process.
       // ActivityManager.

//        ActivityManager mAm = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//      //  mAm.forceStopPackage("xxx.xxx.xxx");
//        mAm.killBackgroundProcesses("com.tayh.gjjclient");
        execShell("am force-stop " + "com.tayh.gjjclient");

    }

    public static void click(String point_x,String point_y){
        execShell("input tap" + " " + point_x + " " + point_y);
    }
    public static void click(String pos){
        execShell("input tap" + " " + pos);
    }
    public static void startScreenShotService(Context context){
        if(!isServiceRunning(context,"com.cja2y.tjgjjrobot.service1")) {

            ComponentName com = new ComponentName("com.cja2y.tjgjjrobot", "com.cja2y.tjgjjrobot.MainActivity");
            Intent intent = new Intent();
            intent.setComponent(com);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public static boolean isServiceRunning(Context mContext,String className) {

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(30);

        if (!(serviceList.size()>0)) {
            return false;
        }

        for (int i=0; i<serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    public static void getLog(final Context context) {
        Process mLogcatProc = null;
        BufferedReader reader = null;
        try {
            //获取logcat日志信息
            mLogcatProc = Runtime.getRuntime().exec(new String[] { "logcat","HttpUtil:I *:S" });//***********************************************抓取天津公积金HttpUtil的log数据
            reader = new BufferedReader(new InputStreamReader(mLogcatProc.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                //if (line.indexOf("this is a test") > 0) {
                    //logcat打印信息在这里可以监听到
                    // 使用looper 把给界面一个显示
                 //   Looper.prepare();

//                ToolKit.runOnMainThreadAsync(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(context, "监听到log信息" + line.toString(), Toast.LENGTH_SHORT).show();
//                    }
//                });

                //Log.e("cja2y log",line.toString());
                 //   Looper.loop();
                //}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  static void getLog2()
    {
//        execShell("adb logcat -v time> d/*");

        ArrayList commandLine = new ArrayList();
        commandLine.add( "logcat");
        commandLine.add( "-d");//使用该参数可以让logcat获取日志完毕后终止进程
        commandLine.add( "-v");
        commandLine.add( "time");
        commandLine.add( "-f");//如果使用commandLine.add(">");是不会写入文件，必须使用-f的方式
        commandLine.add( "/sdcard/log/logcat.txt");
     //   execShell(commandLine.toArray( new String[commandLine.size()]));
    }
}
