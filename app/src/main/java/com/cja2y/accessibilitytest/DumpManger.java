package com.cja2y.accessibilitytest;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by Administrator on 2016/4/27.
 */
public class DumpManger {
    public static DumpManger instance = null;
    public static AccessibilityNodeInfo rootWindowInfo = null;
    public   static DumpManger getInstance(AccessibilityService service){
        rootWindowInfo = service.getRootInActiveWindow();
        if(instance==null){
            instance = new DumpManger();
        }
        return instance;
    }
//    public DumpManger init(AccessibilityService service){
//        rootWindowInfo = service.getRootInActiveWindow();
//        return instance;
//    }

    public  AccessibilityNodeInfo getNodeInfo(int nodeData){

        AccessibilityNodeInfo currentNodeInfo = null;
        switch (nodeData){
            case PublicNode.ACCOUNT_BALANCE:
                currentNodeInfo = rootWindowInfo.getChild(0).//framelayout
                        getChild(0).//linerlayout
                        getChild(0).//framelayout
                        getChild(0).//relativelayout
                        getChild(1)//relativelayout
                        .getChild(0).//linearlayout
                                getChild(1).//relativelayout
                                getChild(0).//listview
                                getChild(0).//relactivelayout
                                getChild(1).//tablelayout
                                getChild(1).//tablerow2
                                getChild(1);//********************* 最后缴存

                return currentNodeInfo;
            case PublicNode.ACCOUNT_COMPANY:
                currentNodeInfo = rootWindowInfo.getChild(0).//framelayout
                        getChild(0).//linerlayout
                        getChild(0).//framelayout
                        getChild(0).//relativelayout
                         getChild(1)//relativelayout
                        .getChild(0).//linearlayout
                                getChild(0).//TableLayout
                                getChild(1);//*****************************             companyname
                return currentNodeInfo;
            case PublicNode.ACCOUNT_LAST_PAY:
                currentNodeInfo = rootWindowInfo.getChild(0).//framelayout
                        getChild(0).//linerlayout
                        getChild(0).//framelayout
                        getChild(0).//relativelayout
                        getChild(1)//relativelayout
                        .getChild(0).//linearlayout
                        getChild(1).//relativelayout
                        getChild(0).//listview
                        getChild(0).//relactivelayout
                        getChild(1).//tablelayout
                        getChild(2).//tablerow3
                        getChild(3);//********************* 最后缴存

                return currentNodeInfo;

            case PublicNode.ACCOUNT_MOUNTH_PAY:


                currentNodeInfo = rootWindowInfo.getChild(0).//framelayout
                        getChild(0).//linerlayout
                        getChild(0).//framelayout
                        getChild(0).//relativelayout
                        getChild(1)//relativelayout
                        .getChild(0).//linearlayout
                                getChild(1).//relativelayout
                                getChild(0).//listview
                                getChild(0).//relactivelayout
                                getChild(1).//tablelayout
                                getChild(4).//tablerow5
                                getChild(1);//********************* 月缴额
                return currentNodeInfo;
            case PublicNode.ACCOUNT_STATE:

                currentNodeInfo = rootWindowInfo.getChild(0).//framelayout
                        getChild(0).//linerlayout
                        getChild(0).//framelayout
                        getChild(0).//relativelayout
                        getChild(1)//relativelayout
                        .getChild(0).//linearlayout
                                getChild(1).//relativelayout
                                getChild(0).//listview
                                getChild(0).//relactivelayout
                                getChild(1).//tablelayout
                                getChild(0).//tablerow5
                                getChild(3);//********************* 用户状态
                return currentNodeInfo;

            case PublicNode.ACCOUNT_NAME:

                currentNodeInfo = rootWindowInfo.getChild(0).//framelayout
                        getChild(0).//linerlayout
                        getChild(0).//framelayout
                        getChild(0).//relativelayout
                        getChild(1)//relativelayout
                        .getChild(0).//linearlayout
                                getChild(0).//TableLayout
                                getChild(0);//*****************************             companyname
                return currentNodeInfo;
                default: return null;
        }

    }
}
