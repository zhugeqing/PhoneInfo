package com.example.administrator.phoneinfo;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Administrator on 2017/3/12.
 */

public class ProgressDlgUtil {
    public static ProgressDialog progressDlg = null;

    /**
     * 启动进度条
     *
     * @param strMessage
     *            进度条显示的信息
     * @param ctx
     *            当前的activity
     */
    public static ProgressDialog showProgressDlg(String strMessage, Context ctx) {

        if (null == progressDlg) {
            progressDlg = new ProgressDialog(ctx);
            //设置进度条样式
            progressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            //设置进度条标题
            progressDlg.setTitle("网优云图");
            //提示的消息
            progressDlg.setMessage(strMessage);
            progressDlg.setIndeterminate(false);
            progressDlg.setCancelable(false);
            progressDlg.setMax(40000);
            //progressDlg.setIcon(R.drawable.ic_launcher_scale);
            progressDlg.show();
        }
        return progressDlg;
    }

    /**
     * 结束进度条
     */
    public static void stopProgressDlg() {
        if (null != progressDlg) {
            progressDlg.dismiss();
            progressDlg = null;
        }
    }
}
