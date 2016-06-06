package com.cja2y.accessibilitytest;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cja2y.accessibilitytest.async.ToolKit;
import com.cja2y.accessibilitytest.logUtil.Level;
import com.cja2y.accessibilitytest.logUtil.LogBean;
import com.cja2y.accessibilitytest.logUtil.LogCat;
import com.cja2y.accessibilitytest.logUtil.LogLoader;
import com.cja2y.accessibilitytest.logUtil.LogParser;
import com.cja2y.accessibilitytest.logUtil.Options;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.trinea.android.common.util.ShellUtils;

/**
 * Created by Administrator on 2016/3/30.
 */
public class MyAccessibilityService extends AccessibilityService implements Runnable {


    private static final String TAG = "MyAccessibility";
    private Dama2Web dama2 = new Dama2Web(205, "9503ce045ad14d83ea876ab578bd3184", "jianghejun2002", "1225pass");//e9cd57222f08a8012b03f9163a7177cb//9503ce045ad14d83ea876ab578bd3184
    //��֤��id
    private int id;
    //sd����Ŀ¼
    private String sdPath = Environment.getExternalStorageDirectory().getPath();
    String[] PACKAGES = {"com.android.settings"};
    private AccessibilityEvent currentEvent;
    private AccessibilityNodeInfo editText1, editText2, editText3, registerBtn, loginBtn;
    private Panel mPanel;
    ActivityManager mActivityManager = null;
    private Activity currentActivity = null;
    private SimpleDateFormat dateFormat = null;
    private String strDate = null;
    private String currentCodeString;
    private int tasks = 100;

    private StringBuffer logContent = null;
    private boolean isObserverLog = false;

    private String account = "";
    private String password = "";
    private String jobSid = "";

    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + MyAccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }

        return false;
    }


    public void onCreate() {
        if (!isAccessibilitySettingsOn(getApplicationContext())) {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        }
    }

    public void onServiceConnected() {
        //mPanel = new Panel(getApplicationContext());
        // bindServiceInvoked();

        logContent = new StringBuffer();
        EventBus.getDefault().register(this);
        dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
        strDate = dateFormat.format(new java.util.Date());
        bindServiceInvoked();

        //   getlog1();
        //   startTimer();
        //  beginAction();
        //  startLogObserver();

        //startListenLog();


    }


    @Subscribe
    public void onEventMainThread(FirstEvent event) {

        String msg = "onEventMainThread收到了消息：" + event.getMsg();
        //  Toast.makeText(this, "收到信息" + msg, Toast.LENGTH_LONG).show();
        //Log.d("harvic", msg);\
        if (!event.getMsg().contains("#")) {
            currentCodeString = event.getMsg();
            new getDataAsyk().execute("decode");
            // tv.setText(msg);
            //     Toast.makeText(this, "收到截图服务信息" + msg, Toast.LENGTH_LONG).show();
        } else {

            String[] extra = event.getMsg().split("#");
            account = extra[0];
            password = extra[1];
            beginAction();
        }
    }

    public void onAccessibilityEvent(AccessibilityEvent event) {
        // TODO Auto-generated method stub
//        int eventType = event.getEventType();
//        //saveCaptcha();
//        currentEvent = event;
//
//        String eventText = "";
//        // Log.i(TAG, "==============Start====================");
//        // Toast("get log");
//       // getlog1();
//        switch (eventType) {
//            case AccessibilityEvent.TYPE_VIEW_CLICKED:
//                //     Log.i(TAG, "==============Start====================");
//                eventText = "TYPE_VIEW_CLICKED";
//                // AccessibilityNodeInfo noteInfo = event.getSource();
//
//                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
////                Log.i(TAG, noteInfo.toString());
//                //      Log.i(TAG, "=============END=====================");
//                //  startListenLog();
//                break;
//            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
//                eventText = "TYPE_VIEW_FOCUSED";
//                break;
//            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
//                eventText = "TYPE_VIEW_LONG_CLICKED";
//                break;
//            case AccessibilityEvent.TYPE_VIEW_SELECTED:
//                eventText = "TYPE_VIEW_SELECTED";
//                break;
//            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
//                eventText = "TYPE_VIEW_TEXT_CHANGED";
//                break;
//            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
//
//                // Toast.makeText(getApplication(), event.getClassName().toString(), Toast.LENGTH_LONG).show();
//
//                eventText = "TYPE_WINDOW_STATE_CHANGED";
//                break;
//            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
//                eventText = "TYPE_NOTIFICATION_STATE_CHANGED";
//                break;
//            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END:
//                eventText = "TYPE_TOUCH_EXPLORATION_GESTURE_END";
//                break;
//            case AccessibilityEvent.TYPE_ANNOUNCEMENT:
//                eventText = "TYPE_ANNOUNCEMENT";
//                break;
//            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START:
//                eventText = "TYPE_TOUCH_EXPLORATION_GESTURE_START";
//                break;
//            case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
//                eventText = "TYPE_VIEW_HOVER_ENTER";
//                break;
//            case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
//                eventText = "TYPE_VIEW_HOVER_EXIT";
//                break;
//            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
//                eventText = "TYPE_VIEW_SCROLLED";
//                break;
//            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
//                eventText = "TYPE_VIEW_TEXT_SELECTION_CHANGED";
//                break;
//            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
//                eventText = "TYPE_WINDOW_CONTENT_CHANGED";
//                break;
//        }


    }

    public void onInterrupt() {

    }


    private void openShotService() {

        Util.startScreenShotService(this);
        Toast("开启截图服务");
    }

    private void openTJGJJ() {

        Util.startTJAPP(this);//*********************************************打开天津公积金应用*****************************************************//
        Toast("打开天津公积金");
    }

    private void beginAction() {
        //
        //openShotService();
//        Util.killTJAPP(MyAccessibilityService.this);
//        Util.sleep(1000);
        openTJGJJ();
        Util.sleep(4500);//**************************************************休眠2s*****************************************************************//------------------------所有的页面切换之前都需要sleep
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        //  Log.d("nodenode", nodeInfo.toString());
        // Toast(nodeInfo.toString());
        if (isWaitFor(TJGJJ_PAGE.loginPage, nodeInfo)) {
            Toast("点击左上角登录键");
            Util.click("150", "150");

        } else {


            Toast("未找到主页面");
            tellTjjRobotTaskFailed();
        }
        //   AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        Util.sleep(5000);
        nodeInfo = getRootInActiveWindow();
        if (isWaitFor(TJGJJ_PAGE.mainPage, nodeInfo)) {

            editText1 = nodeInfo.getChild(2).getChild(3);//*****************************************身份证输入框

            editText2 = nodeInfo.getChild(2).getChild(6);//***************************************密码输入框
            editText3 = nodeInfo.getChild(2).getChild(9);//****************************************验证码输入框
            registerBtn = nodeInfo.getChild(2).getChild(11);
            loginBtn = nodeInfo.getChild(2).getChild(12);

            if (editText1 == null || editText2 == null || editText3 == null) {
                // Toast.makeText(getApplication(),"null"+nodeInfo.getChild(1).toString(),Toast.LENGTH_LONG).show();
                Toast("当前页面非查询页面 error2");
                tellTjjRobotTaskFailed();
                return;
            }

            editTextAction(editText1, account);//************************************************************************输入身份证号

            Util.passwordAction(password);//**************************************************************************************************输入密码
            // editTextAction(editText3, "333");

            //loginBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            //  new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            //  public void run() {
            //execute the task
            Util.sleep(3000);
            currentCodeString = dateFormat.format(new java.util.Date());
            saveCaptcha();

            //     }
            // }, 6000);
        } else {
            Toast("当前页面非查询页面 error3");
            tellTjjRobotTaskFailed();
            return;

        }


    }

    private void editTextAction(AccessibilityNodeInfo nodeInfo, String text) {
//         nodeInfo.setFocused(true);
        Bundle arguments = new Bundle();
        arguments.putCharSequence(AccessibilityNodeInfo
                .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);


    }


    private void Toast(final String s) {
//        ToolKit.runOnMainThreadAsync(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
//            }
//        });
    }

    private void toastError(String s) {
        Toast(s);
        startTimer();
    }


    public AccessibilityNodeInfo findNodeInfosByText(AccessibilityNodeInfo nodeInfo, String text) {
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(text);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    //模拟点击事件
    public void performClick(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return;
        }
        if (nodeInfo.isClickable()) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
            performClick(nodeInfo.getParent());
        }
    }

    private Timer timer = null;
    private TimerTask timerTask = null;

    private void startTimer() {
        // Util.killTJAPP(this);
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                timerTaskRun();
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

    }

    public void timerTaskRun() {
        // beginAction();
        // Util.sleep(3000);
        //  test(getRootInActiveWindow());
        // test();
        //startListenLog();
    }

    private Boolean isPage(PAGE_TYPE page, String activityName) {


        return false;
    }

    enum PAGE_TYPE {
        homepage,
        addaccount_page,
    }


    class getDataAsyk extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String msg = params[0];
            String s = null;
            if ("queryBalance".equals(msg)) {
                Dama2Web.ReadBalanceResult res = dama2.getBalance();
                if (res.ret >= 0) {
                    s = "balance=" + res.balance;
                } else {
                    s = "failed: ret = " + res.ret + "; desc=" + res.desc;
                }
            }
            if ("readInfo".equals(msg)) {
                Dama2Web.ReadInfoResult res = dama2.readInfo();
                if (res.ret == 0) {
                    s = "name=" + res.name + "; qq=" + res.qq + "; email=" + res.email + "; tel=" + res.tel;
                } else {
                    s = "failed: ret = " + res.ret + "; desc=" + res.desc;
                }
            }
            if ("decodeUrl".equals(msg)) {
                int type = 42;
                int timeout = 30;
                String url = "http://icode.renren.com/getcode.do?t=web_reg&rnd=1383107243557";
                Dama2Web.DecodeResult res = dama2.decodeUrlAndGetResult(url, type, timeout);
                if (res.ret >= 0) {
                    id = res.ret;
                    //s = "success: result=" + res.result + "; id=" + res.ret;
                    s = res.result;


                } else {
                    s = "failed: ret = " + res.ret + "; desc=" + res.desc;
                }
            }
            if ("decode".equals(msg)) {
                int type = 200;
                int timeout = 30;
                //��ȡsd����Ŀ¼��getcode.jpgͼƬ
                File file = new File(sdPath + "/Pictures/" + currentCodeString + ".png");
                //File file = new File( "/sdcard/Pictures/currentcode.png");
                if (!file.exists()) {
                    return sdPath + "/Pictures/" + currentCodeString + ".png";
                }

                FileInputStream fis;
                byte[] data = new byte[(int) file.length()];
                try {
                    fis = new FileInputStream(file);
                    fis.read(data);
                    fis.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Dama2Web.DecodeResult res = dama2.decodeAndGetResult(type, timeout, data);
                if (res.ret >= 0) {
                    //     id = res.ret;
                    //       s = "success: result=" + res.result + "; id=" + res.ret;
//                    editTextAction(editText3, res.result);
//                    loginBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);


                    id = res.ret;
                    //s = "success: result=" + res.result + "; id=" + res.ret;
                    s = res.result;
                } else {
                    s = "failed: ret = " + res.ret + "; desc=" + res.desc;
                }

            }
            if ("decodeText".equals(msg)) {
                int type = 200;
                int timeout = 30;

                Dama2Web.DecodeResult res = dama2.decodeAndGetResult(type, timeout, "һ���϶�");
                if (res.ret >= 0) {
                    id = res.ret;
                    s = "success: result=" + res.result + "; id=" + res.ret;
                } else {
                    s = "failed: ret = " + res.ret + "; desc=" + res.desc;
                }
            }
            if ("reportError".equals(msg)) {
                Dama2Web.RequestResult res = dama2.reportError(id);
                if (res.ret == 0) {
                    s = "report success(id=" + id + ")";
                } else {
                    s = "failed: ret = " + res.ret + "; desc=" + res.desc;
                }
            }
            return s;
        }

        @Override
        //*******************************************************输入完有用信息并会在此回调到验证码、、、之后模拟点击登陆按钮  登陆若成功 会弹出确定登陆成功弹出框
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //  Util.click("837","1158");
            Util.sleep(3000);
            //startLogListen();
            editTextAction(editText3, result);
            loginBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            // saveCaptcha();

            Util.sleep(1000);
            Util.click(Util.login_success_enter_pos);
            // MyLog.MLog.Log();
            // getlog1();
            //startListenLog();
            //startLogListen();
            //  getLog2();
            //Util.openDetailActivity(MyAccessibilityService.this);
            //     Util.sleep(2000);
            //    Util.click(Util.personal_account_btn_pos);


//            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
//            nodeInfo.getChild(0).getChild(0).getChild(0).getChild(0).getChild(1).getChild(0).getChild(4).performAction(AccessibilityNodeInfo.ACTION_CLICK);


            Util.sleep(3000);
            Util.click(Util.personal_account_btn_pos);
            Util.sleep(2000);
            Util.click(Util.login_detail_pos);
            Util.sleep(2000);
            Util.click(Util.login_detail_more_pos);
            Util.sleep(2000);
            Util.click(Util.login_detail_final_pos);
//            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                public void run() {
////
////                    //   stopLogObserver();
////                    // Util.sleep(2000);
////                    Util.killTJAPP(MyAccessibilityService.this);
//                    // tellTjjRobotGetTask();
//                    Toast("点击详情");
//                    Util.click(Util.personal_account_btn_pos);
//                    //AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
//                    // nodeInfo.getChild(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                    // nodeInfo.getChild(0).getChild(0).getChild(0).getChild(1).getChild(0).getChild(4).performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                }
//            }, 10000);


            //  startLogObserver();


//            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                public void run() {
////
////                    //   stopLogObserver();
////                    // Util.sleep(2000);
////                    Util.killTJAPP(MyAccessibilityService.this);
//                    // tellTjjRobotGetTask();
//                    Util.click(Util.login_detail_pos);
//                }
//            }, 11000);
//
//            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                public void run() {
////
////                    //   stopLogObserver();
////                    // Util.sleep(2000);
////                    Util.killTJAPP(MyAccessibilityService.this);
//                    // tellTjjRobotGetTask();
//                    Util.click(Util.login_detail_more_pos);
//                }
//            }, 12000);
//
//            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                public void run() {
////
////                    //   stopLogObserver();
////                    // Util.sleep(2000);
////                    Util.killTJAPP(MyAccessibilityService.this);
//                    // tellTjjRobotGetTask();
//                    Util.click(Util.login_detail_final_pos);
//                }
//            },13000);
//
//
////            AccessibilityNodeInfo rootwindow = getRootInActiveWindow();
////
////            AccessibilityNodeInfo  currentNodeInfo = rootwindow.getChild(0).//framelayout
////                    getChild(0).//linerlayout
////                    getChild(0).//framelayout
////                    getChild(0).//relativelayout
////                    getChild(1)//relativelayout
////                    .getChild(0).//linearlayout
////                            getChild(0).//TableLayout
////                            getChild(1);//*****************************             companyname
////            Toast(currentNodeInfo.getText().toString());
//            //  Util.sleep(3000);
//            //   Util.getLog(MyAccessibilityService.this);
//            //  MyLog.MLog.getLog();
////            ArrayList<String> commnandList = new ArrayList<>();
////            commnandList.add("rm -r /mnt/sdcard/bugLog/logcat.txt");//如果不删除之前的logcat.txt文件，每次执行logcat命令也不会更新该文件
////            commnandList.add("logcat -d -v time -f /mnt/sdcard/bugLog/logcat.txt");
////            ShellUtils.execCommand(commnandList, true);
//          //  Util.killTJAPP(MyAccessibilityService.this);
//
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                public void run() {
//
//                    //   stopLogObserver();
//                    // Util.sleep(2000);
//                    Util.killTJAPP(MyAccessibilityService.this);
                    tellTjjRobotGetTask();
                }
            }, 1000);

        }
    }

    private void saveToSDCard(String filename, String content) throws IOException {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory(), filename);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(content.getBytes());
            outStream.close();
        } else {
            showToast("faildsave");
        }
    }

    /**
     * ʵ���ı����ƹ���
     *
     * @param content
     */
    private void copy(String content, Context context) {
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }

    /**
     * ��ʾToast
     *
     * @param text
     */
    private void showToast(String text) {
        Toast.makeText(getApplicationContext(), text + "", Toast.LENGTH_LONG).show();
        // Log.i("dama2", text + "");
    }

    /**
     * ��ʾToast
     *
     * @param text
     */
    private void showToast(int text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
        //   Log.i("dama2", getResources().getString(text) + "");
    }


    private void saveCaptcha() {

        try {
            //takeScreenShot();
            Intent intent = new Intent("com.cja2y.accessibilitytest.MYBROADCAST");
            //  将要广播的数据添加到Intent对象中
            intent.putExtra("text", "shot");
            //  发送广播
            sendBroadcast(intent);
        } catch (Exception e) {
            System.err.println(e);
        }


    }

    private void tellTjjRobotGetTask() {
        Util.killTJAPP(MyAccessibilityService.this);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            public void run() {
                //execute the task
                try {
                    //takeScreenShot();
                    Intent intent = new Intent("com.cja2y.accessibilitytest.MYBROADCAST");
                    //  将要广播的数据添加到Intent对象中
                    intent.putExtra("text", "starttask");
                    //  发送广播
                    sendBroadcast(intent);
                } catch (Exception e) {
                    System.err.println(e);
                }

            }
        }, 2000);

    }

    private void tellTjjRobotTaskFailed(){
        Util.killTJAPP(MyAccessibilityService.this);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            public void run() {
                //execute the task
                try {
                    //takeScreenShot();
                    Intent intent = new Intent("com.cja2y.accessibilitytest.MYBROADCAST");
                    //  将要广播的数据添加到Intent对象中
                    intent.putExtra("text", "taskfailed");
                    //  发送广播
                    sendBroadcast(intent);
                } catch (Exception e) {
                    System.err.println(e);
                }

            }
        }, 2000);
    }

    public void execShell(String cmd) {
        try {
            //权限设置
            Process p = Runtime.getRuntime().exec("su");
            //获取输出流
            OutputStream outputStream = p.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            //将命令写入
            dataOutputStream.writeBytes(cmd);
            //提交命令
            dataOutputStream.flush();
            //关闭流操作
            dataOutputStream.close();
            outputStream.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    private String exec(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();
            process.waitFor();
            return output.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public void adbCommand(String command) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(command);
        Process sh;
        try {
//            if (proc.waitFor() != 0) {
//                System.err.println("exit value = " + proc.exitValue());
//            }
//            BufferedReader in = new BufferedReader(new InputStreamReader(
//                    proc.getInputStream()));
//            StringBuffer stringBuffer = new StringBuffer();
//            String line = null;
//            while ((line = in.readLine()) != null) {
//                stringBuffer.append(line + " ");
//            }
//            System.out.println(stringBuffer.toString());

            sh = Runtime.getRuntime().exec("su", null, null);
            OutputStream os = sh.getOutputStream();
            os.write(("/system/bin/" + command).getBytes("ASCII"));
            os.flush();
            os.close();
            sh.waitFor();


        } catch (InterruptedException e) {
            System.err.println(e);
        } finally {
            try {
                proc.destroy();
            } catch (Exception e2) {
            }
        }
    }

    public void execCommand(String command) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(command);

        try {
            if (proc.waitFor() != 0) {
                System.err.println("exit value = " + proc.exitValue());
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    proc.getInputStream()));
            StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while ((line = in.readLine()) != null) {
                stringBuffer.append(line + " ");
            }
            System.out.println(stringBuffer.toString());

        } catch (InterruptedException e) {
            System.err.println(e);
        } finally {
            try {
                proc.destroy();
            } catch (Exception e2) {
            }
        }
    }

    public void screenShot() throws InterruptedException {
        Process sh;
        try {
            sh = Runtime.getRuntime().exec("su", null, null);
            OutputStream os = sh.getOutputStream();
            os.write(("/system/bin/screencap -p " + "/sdcard/Image.png").getBytes("ASCII"));
            os.flush();
            os.close();
            sh.waitFor();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    private void takeScreenShot() {
        //Log.e("cja2y","takeScreenShot");
        try {
            int a = mA++;
            int b = (int) (Math.random() * 100);


            Message msgFromClient = Message.obtain(null, MSG_SUM, a, b);
            // msgFromClient.replyTo = mMessenger;
            //Log.e("cja2y", "try to sendmsg");
            if (isConn) {
                //往服务端发送消息
                //Log.e("cja2y","sendmsg");
                mService.send(msgFromClient);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static final int MSG_SUM = 0x110;
    private boolean isConn;
    private Messenger mService;
    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            isConn = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            isConn = false;

        }
    };

    private int mA;

    private void bindServiceInvoked() {
        Intent intent = new Intent();
        intent.setAction("com.cja2y.tjgjjrobot");
        intent.setPackage(getPackageName());
        bindService(intent, mConn, Context.BIND_AUTO_CREATE);
        // Log.e("cja2y mconn state",isConn+"");
        // Log.e(TAG, "bindService invoked !");
    }


//    /**
//
//     *
//
//     * @MethodName:closeInputMethod
//
//     * @Description:关闭系统软键盘
//
//     * @throws
//
//     */
//
//    public void closeInputMethod(){
//
//        try {
//
//            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
//
//                    .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
//
//                            InputMethodManager.HIDE_NOT_ALWAYS);
//
//        } catch (Exception e) { }finally{ }
//
//    }
//
//    /**
//
//     *
//
//     * @MethodName:openInputMethod
//
//     * @Description:打开系统软键盘
//
//     * @throws
//
//     */
//
//    public void openInputMethod(final EditText editText){
//
//        Timer timer = new Timer();
//
//        timer.schedule(new TimerTask() {
//
//            public void run() {
//
//                InputMethodManager inputManager = (InputMethodManager) editText
//
//                        .getContext().getSystemService(
//
//                                Context.INPUT_METHOD_SERVICE);
//
//                inputManager.showSoftInput(editText, 0);
//
//            }
//
//        }, 200);
//
//    }


    private void simulateAction(AccessibilityNodeInfo nodeInfo) {
        // nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        try {
            execShell("input tap 492 591");//**************密码输入框
//            execShell("input tap 472 591");
//           execShell("input tap 452 391");
//            execShell("input tap 432 291");
//            execShell("input tap 412 591");
            execShell("input tap 15 1080");//*************               ?123shezhi
            execShell("input tap 388 791");//*************              Y  6
            execShell("input tap 300 791");//*************              T  5
            execShell("input tap 230 791");//*************              R  4
            execShell("input tap 150 791");//*************              E  3
            execShell("input tap 85 791");//*************               W  2
            execShell("input tap 15 791");//*************               Q  1
            execShell("input tap 460 791");//***************            U  7
            execShell("input tap 540 791");//***************            I  8
            execShell("input tap 620 791");//***************            O  9
            execShell("input tap 700 791");//*************              P  0
            // execShell("input tap 600 1080");//*************               收起

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private final String password_edittext_pos = " 492 591";
    private final String keyboard_change_pos = " 15 1080";
    private final String keyboard_close_pos = "600 1080";
    private final String y_6_pos = " 388 791";
    private final String t_5_pos = " 300 791";
    private final String r_4_pos = " 230 791";
    private final String e_3_pos = " 150 791";
    private final String w_2_pos = " 85 791";
    private final String q_1_pos = " 15 791";
    private final String u_7_pos = " 460 791";
    private final String i_8_pos = " 540 791";
    private final String o_9_pos = " 620 791";
    private final String p_0_pos = " 700 791";

    private final String[] keyboards_pos = {password_edittext_pos, keyboard_change_pos, keyboard_close_pos, p_0_pos, q_1_pos, w_2_pos, e_3_pos, r_4_pos, t_5_pos, y_6_pos, u_7_pos, i_8_pos, o_9_pos};

    private void passwordAction(String password) {
        String currentCommand = "";
        currentCommand = "input tap 492 591" + "&&" + "input tap 15 1080";
        for (int i = 0; i < 7; i++) {
            currentCommand += "&&" + "input tap 650 990";
        }
        for (int i = 0; i < password.length(); i++) {
            currentCommand += "&&" + getCommand(password.substring(i, i + 1));
        }
        currentCommand += "&&" + "input tap 600 1080";//**************************600 1080 坐标：收起键盘
        try {
            execShell(currentCommand);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCommand(String s) {
        return "input tap" + getPos(s);
    }

    private String getPos(String s) {
        switch (s) {
            case "0":
                return p_0_pos;
            case "1":
                return q_1_pos;
            case "2":
                return w_2_pos;
            case "3":
                return e_3_pos;
            case "4":
                return r_4_pos;
            case "5":
                return t_5_pos;
            case "6":
                return y_6_pos;
            case "7":
                return u_7_pos;
            case "8":
                return i_8_pos;
            case "9":
                return o_9_pos;
            default:
                return null;
        }
    }

    enum TJGJJ_PAGE {
        loginPage,
        mainPage,
        infoPage
    }

    private Boolean isWaitFor(TJGJJ_PAGE currentPage, AccessibilityNodeInfo info) {
        if (currentPage.equals(TJGJJ_PAGE.loginPage)) {
            return searchforNode("动态新闻", info);
        } else if (currentPage.equals(TJGJJ_PAGE.mainPage)) {
            //return searchforNode("注  册",info);

            if (info == null) {
                // Log.w(TAG, "rootWindow为空");
                return false;
            }
            if (info.getChildCount() <= 2) {
                return false;
            }
            if (info.getChild(2) == null) {
                return false;
            }
            if (info.getChild(2).getChildCount() < 12) {
                return false;
            }

            return true;
        } else if (currentPage.equals(TJGJJ_PAGE.infoPage)) {
            return searchforNode("", info);
        }

        return false;
    }

    private Boolean searchforNode(String s, AccessibilityNodeInfo nodeInfo) {
        // AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            //Log.w(TAG, "rootWindow为空");
            return false;
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(s);
        if (list != null) {
            return true;
        }
        return false;
    }


    // private void nextStepAction(){}


    @Nullable
    private LogBean getLogBean(String line) {
        LogBean logBean = new LogBean();
        int tagStart = line.indexOf("/");
        int msgStart = line.indexOf("):");

        if (msgStart == -1 || tagStart == -1) {
            return null;
        }

        logBean.tag = line.substring(tagStart + 1, msgStart + 1);
        logBean.msg = line.substring(msgStart + 2);
        String lev = line.substring(tagStart - 1, tagStart);

        logBean.lev = LogParser.parseLev(lev);
        logBean.time = line.substring(0, tagStart - 2);
        return logBean;
    }

    @Override
    public void run() {


    }

    public void writeFileSdcard(String fileName, String message) {

        try {

            // FileOutputStream fout = openFileOutput(fileName, MODE_PRIVATE);

            FileOutputStream fout = new FileOutputStream(fileName);

            byte[] bytes = message.getBytes();

            fout.write(bytes);

            fout.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }


}
