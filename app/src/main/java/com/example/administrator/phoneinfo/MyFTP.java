package com.example.administrator.phoneinfo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class MyFTP {
    /**
     * 服务器名.
     */
    private String hostName;

    /**
     * 端口号
     */
    private int serverPort;

    /**
     * 用户名.
     */
    private String userName;

    /**
     * 密码.
     */
    private String password;

    /**
     * FTP连接.
     */
    private FTPClient ftpClient;

    public MyFTP() {
        this.hostName = MyApplication.mrFtpServerIp;
        this.serverPort = MyApplication.mrFtpServerPort;
        this.userName = MyApplication.mrFtpUser;
        this.password = MyApplication.mrFtpPassword;
        this.ftpClient = new FTPClient();
    }

    // -------------------------------------------------------文件上传方法------------------------------------------------

    /**
     * 上传单个文件.
     *
     //* @param localFile
     *      本地文件
     * @param remotePath
     *      FTP目录
     * @param listener
     *      监听器
     * @throws IOException
     */
    public void uploadSingleFile(File singleFile, String remotePath,
                                 UploadProgressListener listener) throws IOException {

        // 上传之前初始化
        this.uploadBeforeOperate(remotePath, listener);

        boolean flag;
        flag = uploadingSingle(singleFile, listener);
        if (flag) {
            listener.onUploadProgress(FtpActivity.FTP_UPLOAD_SUCCESS, 0,
                    singleFile);
        } else {
            listener.onUploadProgress(FtpActivity.FTP_UPLOAD_FAIL, 0,
                    singleFile);
        }

        // 上传完成之后关闭连接
        this.uploadAfterOperate(listener);
    }

    /**
     * 上传多个文件.
     *
     //* @param localFile
     *      本地文件
     * @param remotePath
     *      FTP目录
     * @param listener
     *      监听器
     * @throws IOException
     */
    public void uploadMultiFile(LinkedList<File> fileList, String remotePath,
                                UploadProgressListener listener) throws IOException {

        // 上传之前初始化
        this.uploadBeforeOperate(remotePath, listener);

        boolean flag;

        for (File singleFile : fileList) {
            flag = uploadingSingle(singleFile, listener);
            if (flag) {
                listener.onUploadProgress(FtpActivity.FTP_UPLOAD_SUCCESS, 0,
                        singleFile);
            } else {
                listener.onUploadProgress(FtpActivity.FTP_UPLOAD_FAIL, 0,
                        singleFile);
            }
        }

        // 上传完成之后关闭连接
        this.uploadAfterOperate(listener);
    }

    /**
     * 上传单个文件.
     *
     * @param localFile
     *      本地文件
     * @return true上传成功, false上传失败
     * @throws IOException
     */
    private boolean uploadingSingle(File localFile,
                                    UploadProgressListener listener) throws IOException {
        boolean flag = true;
        // 不带进度的方式
        // // 创建输入流
        // InputStream inputStream = new FileInputStream(localFile);
        // // 上传单个文件
        // flag = ftpClient.storeFile(localFile.getName(), inputStream);
        // // 关闭文件流
        // inputStream.close();

        // 带有进度的方式
        BufferedInputStream buffIn = new BufferedInputStream(
                new FileInputStream(localFile));
        ProgressInputStream progressInput = new ProgressInputStream(buffIn, listener, localFile);
        flag = ftpClient.storeFile(localFile.getName(), progressInput);
        buffIn.close();

        return flag;
    }

    /**
     * 上传文件之前初始化相关参数
     *
     * @param remotePath
     *      FTP目录
     * @param listener
     *      监听器
     * @throws IOException
     */
    private void uploadBeforeOperate(String remotePath,
                                     UploadProgressListener listener) throws IOException {

        // 打开FTP服务
        try {
            this.openConnect();
            //listener.onUploadProgress(FtpActivity.FTP_CONNECT_SUCCESSS, 0,null);
        } catch (IOException e1) {
            e1.printStackTrace();
            listener.onUploadProgress(FtpActivity.FTP_CONNECT_FAIL, 0, null);
            return;
        }

        // 设置模式
        ftpClient.setFileTransferMode(org.apache.commons.net.ftp.FTP.STREAM_TRANSFER_MODE);
        // FTP下创建文件夹
        ftpClient.makeDirectory(remotePath);
        // 改变FTP目录
        ftpClient.changeWorkingDirectory(remotePath);
        // 上传单个文件

    }
    //  删除FTP上的文件
    public Boolean deleteFtpFile(String remote){
        Boolean result = true;
        try {
            if(!ftpClient.deleteFile(remote))
                result = false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
    //删除FTP上的文件夹
    public Boolean deleteSubDirectory(String remote){
        Boolean result=false;
        try {
            this.openConnect();
            result=deleteDirectory(remote);
            this.closeConnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    public Boolean deleteDirectory(String remote){
        Boolean result = true;
        try{
            if(ftpClient.changeWorkingDirectory(remote)){
                FTPFile[] files = ftpClient.listFiles();
                for(FTPFile f : files){
                    System.out.println(f.getName());
                    if(f.isFile()){
                        if(ftpClient.deleteFile(remote.endsWith("/") ? remote + f.getName() : remote + "/"+f.getName()))
                            System.out.println("remove "+f.getName());
                        else
                            return false;
                    }
                    else
                        deleteDirectory(remote.endsWith("/") ? remote + f.getName() : remote + "/" + f.getName());
                }
                if(!ftpClient.removeDirectory(remote))
                    return false;
            }else {
                System.out.println("enter " +remote +" fail");
            }
        }catch(IOException e){
            e.printStackTrace();

        }
        return result;
    }


    public boolean ftpMakeDirectory(String ftpPath){
        try{
            this.openConnect();
            // 设置模式
            ftpClient.setFileTransferMode(org.apache.commons.net.ftp.FTP.STREAM_TRANSFER_MODE);
            // FTP下创建文件夹
            ftpClient.makeDirectory(ftpPath);
            this.closeConnect();
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean ftpMakeMultiDirectory(List<String> ftpPathList){
        try{
            this.openConnect();
            // 设置模式
            ftpClient.setFileTransferMode(org.apache.commons.net.ftp.FTP.STREAM_TRANSFER_MODE);
            // FTP下创建文件夹
            for(int i=0;i<ftpPathList.size();i++) {
                ftpClient.makeDirectory(ftpPathList.get(i));
            }
            this.closeConnect();
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 上传完成之后关闭连接
     *
     * @param listener
     * @throws IOException
     */
    private void uploadAfterOperate(UploadProgressListener listener)
            throws IOException {
        this.closeConnect();
        listener.onUploadProgress(FtpActivity.FTP_DISCONNECT_SUCCESS, 0, null);
    }

    // -------------------------------------------------------文件下载方法------------------------------------------------

    /**
     * 下载单个文件，可实现断点下载.
     *
     * @param serverPath
     *      Ftp目录及文件路径
     * @param localPath
     *      本地目录
     * @param fileName
     *      下载之后的文件名称
     * @param listener
     *      监听器
     * @throws IOException
     */
    public void downloadSingleFile(String serverPath, String localPath, String fileName, DownLoadProgressListener listener)
            throws Exception {

        // 打开FTP服务
        try {
            this.openConnect();
            listener.onDownLoadProgress(FtpActivity.FTP_CONNECT_SUCCESSS, 0, null);
        } catch (IOException e1) {
            e1.printStackTrace();
            listener.onDownLoadProgress(FtpActivity.FTP_CONNECT_FAIL, 0, null);
            return;
        }

        // 先判断服务器文件是否存在
        FTPFile[] files = ftpClient.listFiles(serverPath);
        if (files.length == 0) {
            listener.onDownLoadProgress(FtpActivity.FTP_FILE_NOTEXISTS, 0, null);
            return;
        }

        //创建本地文件夹
        File mkFile = new File(localPath);
        if (!mkFile.exists()) {
            mkFile.mkdirs();
        }

        localPath = localPath + fileName;
        // 接着判断下载的文件是否能断点下载
        long serverSize = files[0].getSize(); // 获取远程文件的长度
        File localFile = new File(localPath);
        long localSize = 0;
        if (localFile.exists()) {
            File file = new File(localPath); //如果本地文件存在，删除本地文件
            file.delete();
            //localSize = localFile.length(); // 如果本地文件存在，获取本地文件的长度
            //if (localSize >= serverSize) {
            //    File file = new File(localPath);
            //    file.delete();
            //}
        }

        // 进度
        long step = serverSize / 100;
        long process = 0;
        long currentSize = 0;
        // 开始准备下载文件
        OutputStream out = new FileOutputStream(localFile, true);
        ftpClient.setRestartOffset(localSize);
        InputStream input = ftpClient.retrieveFileStream(serverPath+fileName);
        byte[] b = new byte[1024];
        int length = 0;
        while ((length = input.read(b)) != -1) {
            out.write(b, 0, length);
            currentSize = currentSize + length;
            if (currentSize / step != process) {
                process = currentSize / step;
                if (process % 5 == 0) { //每隔%5的进度返回一次
                    listener.onDownLoadProgress(FtpActivity.FTP_DOWN_LOADING, process, null);
                }
            }
        }
        out.flush();
        out.close();
        input.close();

        // 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉
        if (ftpClient.completePendingCommand()) {
            listener.onDownLoadProgress(FtpActivity.FTP_DOWN_SUCCESS, 0, new File(localPath));
        } else {
            listener.onDownLoadProgress(FtpActivity.FTP_DOWN_FAIL, 0, null);
        }

        // 下载完成之后关闭连接
        this.closeConnect();
        listener.onDownLoadProgress(FtpActivity.FTP_DISCONNECT_SUCCESS, 0, null);

        return;
    }

    public void downloadMultiFilesAtOneFtpPath(String serverPath, String localPath, List<String> downFileNames, DownLoadProgressListener listener)
            throws Exception {
        String mLocalPath=localPath;
        // 打开FTP服务
        try {
            this.openConnect();
            listener.onDownLoadProgress(FtpActivity.FTP_CONNECT_SUCCESSS, 0, null);
        } catch (IOException e1) {
            e1.printStackTrace();
            listener.onDownLoadProgress(FtpActivity.FTP_CONNECT_FAIL, 0, null);
            return;
        }

            // 先判断服务器文件是否存在
            FTPFile[] files = ftpClient.listFiles(serverPath);
            if (files.length == 0) {
                listener.onDownLoadProgress(FtpActivity.FTP_FILE_NOTEXISTS, 0, null);
                return;
            }

            //创建本地文件夹
            File mkFile = new File(localPath);
            if (!mkFile.exists()) {
                mkFile.mkdirs();
            }
            //便利要下载的文件
        for(int i=0;i<downFileNames.size();i++) {
            localPath = mLocalPath + downFileNames.get(i);
            int filesIndex=0;
            for(int j=0;j<files.length;j++){
                //查找下载目标文件在FTP上的文件列表序号
                if(downFileNames.get(i).equals(files[j].getName())){
                    filesIndex=j;
                    break;
                }
            }

            // 接着判断下载的文件是否能断点下载
            long serverSize = files[filesIndex].getSize(); // 获取远程文件的长度
            File localFile = new File(localPath); //在本地创建文件
            long localSize = 0;
            if (localFile.exists()) {
                File file = new File(localPath); //如果本地文件存在，删除本地文件
                file.delete();
                //localSize = localFile.length(); // 如果本地文件存在，获取本地文件的长度
                //if (localSize >= serverSize) {
                //    File file = new File(localPath);
                //    file.delete();
                //}
            }

            // 进度
            long step = serverSize / 100;
            long process = 0;
            long currentSize = 0;
            // 开始准备下载文件
            OutputStream out = new FileOutputStream(localFile, true);
            ftpClient.setRestartOffset(localSize);
            InputStream input = ftpClient.retrieveFileStream(serverPath + downFileNames.get(i));
            byte[] b = new byte[1024];
            int length = 0;
            while ((length = input.read(b)) != -1) {
                out.write(b, 0, length);
                currentSize = currentSize + length;
                if (currentSize / step != process) {
                    process = currentSize / step;
                    if (process % 5 == 0) { //每隔%5的进度返回一次
                        listener.onDownLoadProgress(FtpActivity.FTP_DOWN_LOADING, process, null);
                    }
                }
            }
            out.flush();
            out.close();
            input.close();

            // 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉
            if (ftpClient.completePendingCommand()) {
                listener.onDownLoadProgress(FtpActivity.FTP_DOWN_SUCCESS, 0, new File(localPath));
            } else {
                listener.onDownLoadProgress(FtpActivity.FTP_DOWN_FAIL, 0, null);
            }
        }
        // 下载完成之后关闭连接
        this.closeConnect();
        listener.onDownLoadProgress(FtpActivity.FTP_DISCONNECT_SUCCESS, 0, null);

        return;
    }

    public static FTPFile[] sortFilesByLastModTime(FTPFile[] files){

        TreeMap<Long,FTPFile> tm = new TreeMap<Long,FTPFile>();
        //File file = new File("C:\\FTPLOG\\logfile\\2012-04-22\\CP");
        int fileNum = files.length;
        FTPFile[] sortedFiles=new FTPFile[fileNum];
        List<Long> tempLongList=new ArrayList<>();

        for (int i = 0; i < fileNum; i++) {
            Long tempLong = -(files[i].getTimestamp().getTimeInMillis()+files[i].getSize()%1000);
            if(tempLongList.indexOf(tempLong)>-1){
                tempLong=tempLong-1;
            }
            tm.put(tempLong, files[i]);
            tempLongList.add(tempLong);
        }

        //System.out.println("按时间从前到后排序--->");
        //System.out.println("最早的一个文件的路径-->"+tm.get(tm.firstKey()).getPath()+tm.firstKey());
        //System.out.println("最近的一个文件的路径-->"+tm.get(tm.lastKey()).getPath()+tm.lastKey());
        Set<Long> set = tm.keySet();
        Iterator<Long> it = set.iterator();
        int k=0;
        while (it.hasNext()) {
            Object key = it.next();
            Object objValue = tm.get(key);
            FTPFile tempFile =  (FTPFile)objValue;
            sortedFiles[k]=tempFile;
            k++;
            //Date date=new Date((Long)key);
            //System.out.println(tempFile.getPath() + "\t"+date);
        }
        //return null;
        return  sortedFiles;
    }

    public FTPFile[] listFtpServerFiles(String serverPath)
            throws Exception {

        // 打开FTP服务
        try {
            this.openConnect();
        } catch (IOException e1) {
            e1.printStackTrace();
            return null;
        }
        // 先判断服务器文件是否存在
        FTPFile[] files = ftpClient.listFiles(serverPath);
        if (files.length == 0) {
            return files;
        }
        // 下载完成之后关闭连接
        FTPFile[] sortedFiles;
        sortedFiles= sortFilesByLastModTime(files);
        this.closeConnect();
        return sortedFiles;
    }


    /**
      * 复制文件夹.
      *
      * @param sourceDir
      * @param targetDir
      * @throws IOException
      */
    public void copyFtpDirectiory(String sourceDir, String targetDir) throws IOException {
        // 新建目标目录

        //if (!existDirectory(targetDir)) {
        //    createDirectory(targetDir);
        //}
        // 获取源文件夹当前下的文件或目录
        // File[] file = (new File(sourceDir)).listFiles();
        this.openConnect();

        FTPFile[] ftpFiles = ftpClient.listFiles(sourceDir);
        for (int i = 0; i < ftpFiles.length; i++) {
            if (ftpFiles[i].isFile()) {
                copyFile(ftpFiles[i].getName(), sourceDir, targetDir);
            } else if (ftpFiles[i].isDirectory()) {
                copyFtpDirectiory(sourceDir + "/" + ftpFiles[i].getName(), targetDir + "/" + ftpFiles[i].getName());
            }
        }

        //if (ftpClient.completePendingCommand()) {
            //listener.onDownLoadProgress(FtpActivity.FTP_DOWN_SUCCESS, 0, new File(localPath));
        //} else {
        //    //listener.onDownLoadProgress(FtpActivity.FTP_DOWN_FAIL, 0, null);
        //}
        this.closeConnect();
    }

    public void copyFtpMultiDirectiory(List<String> sourceDirList, List<String> targetDirList) throws IOException {
        // 新建目标目录

        //if (!existDirectory(targetDir)) {
        //    createDirectory(targetDir);
        //}
        // 获取源文件夹当前下的文件或目录
        // File[] file = (new File(sourceDir)).listFiles();
        this.openConnect();
        for(int dir=0;dir<sourceDirList.size();dir++) {

            FTPFile[] ftpFiles = ftpClient.listFiles(sourceDirList.get(dir));
            for (int i = 0; i < ftpFiles.length; i++) {
                if (ftpFiles[i].isFile()) {
                    copyFile(ftpFiles[i].getName(), sourceDirList.get(dir), targetDirList.get(dir));
                } else if (ftpFiles[i].isDirectory()) {
                    copyFtpDirectiory(sourceDirList.get(dir) + "/" + ftpFiles[i].getName(), targetDirList.get(dir) + "/" + ftpFiles[i].getName());
                }
                //if (ftpClient.completePendingCommand()) {
                //    //listener.onDownLoadProgress(FtpActivity.FTP_DOWN_SUCCESS, 0, new File(localPath));
                //} else {
                //    //listener.onDownLoadProgress(FtpActivity.FTP_DOWN_FAIL, 0, null);
                //}
            }
        }
        this.closeConnect();
    }

    /**
     02.     * 复制文件.
     03.     *
     04.     * @param sourceFileName
     05.     * @param targetFile
     06.     * @throws IOException
     07.     */
    public void copyFile(String sourceFileName, String sourceDir, String targetDir) throws IOException {
                InputStream is = null;
                try {
                        //if (!existDirectory(targetDir)) {
                        //        createDirectory(targetDir);
                        //    }
                        // 变更工作路径
                        ftpClient.changeWorkingDirectory(sourceDir);
                        // 设置以二进制流的方式传输
                        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                        is = ftpClient.retrieveFileStream(new String(sourceFileName.getBytes("GBK"), "iso-8859-1"));
                        // 主动调用一次getReply()把接下来的226消费掉. 这样做是可以解决这个返回null问题
                        try{
                            ftpClient.getReply();
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                        if (is != null) {
                                ftpClient.changeWorkingDirectory(targetDir);
                                ftpClient.storeFile(new String(sourceFileName.getBytes("GBK"), "iso-8859-1"), is);
                            }

                    }catch (Exception e){
                    e.printStackTrace();
                }
                    finally {
                        // 关闭流
                        if (is != null) {
                                is.close();
                            }
                    }
    }



    // -------------------------------------------------------文件删除方法------------------------------------------------

    /**
     * 删除Ftp下的文件.
     *
     * @param serverPath
     *      Ftp目录及文件路径
     * @param listener
     *      监听器
     * @throws IOException
     */
    public void deleteSingleFile(String serverPath, DeleteFileProgressListener listener)
            throws Exception {

        // 打开FTP服务
        try {
            this.openConnect();
            listener.onDeleteProgress(FtpActivity.FTP_CONNECT_SUCCESSS);
        } catch (IOException e1) {
            e1.printStackTrace();
            listener.onDeleteProgress(FtpActivity.FTP_CONNECT_FAIL);
            return;
        }

        // 先判断服务器文件是否存在
        FTPFile[] files = ftpClient.listFiles(serverPath);
        if (files.length == 0) {
            listener.onDeleteProgress(FtpActivity.FTP_FILE_NOTEXISTS);
            return;
        }

        //进行删除操作
        boolean flag = true;
        flag = ftpClient.deleteFile(serverPath);
        if (flag) {
            listener.onDeleteProgress(FtpActivity.FTP_DELETEFILE_SUCCESS);
        } else {
            listener.onDeleteProgress(FtpActivity.FTP_DELETEFILE_FAIL);
        }

        // 删除完成之后关闭连接
        this.closeConnect();
        listener.onDeleteProgress(FtpActivity.FTP_DISCONNECT_SUCCESS);

        return;
    }


    public void deleteMultiFile(List<String> serverPath, DeleteFileProgressListener listener)
            throws Exception {

        // 打开FTP服务
        try {
            this.openConnect();
            listener.onDeleteProgress(FtpActivity.FTP_CONNECT_SUCCESSS);
        } catch (IOException e1) {
            e1.printStackTrace();
            listener.onDeleteProgress(FtpActivity.FTP_CONNECT_FAIL);
            return;
        }

        // 先判断服务器文件是否存在
        for(int i=0;i<serverPath.size();i++) {
            FTPFile[] files = ftpClient.listFiles(serverPath.get(i));
            if (files.length == 0) {
                listener.onDeleteProgress(FtpActivity.FTP_FILE_NOTEXISTS);
                break;
            }

            //进行删除操作
            boolean flag = true;
            flag = ftpClient.deleteFile(serverPath.get(i));
            if (flag) {
                listener.onDeleteProgress(FtpActivity.FTP_DELETEFILE_SUCCESS);
            } else {
                listener.onDeleteProgress(FtpActivity.FTP_DELETEFILE_FAIL);
            }
        }
        // 删除完成之后关闭连接
        this.closeConnect();
        listener.onDeleteProgress(FtpActivity.FTP_DISCONNECT_SUCCESS);

        return;
    }

    // -------------------------------------------------------打开关闭连接------------------------------------------------

    /**
     * 打开FTP服务.
     *
     * @throws IOException
     */
    public void openConnect() throws IOException {
        // 中文转码
        ftpClient.setControlEncoding("UTF-8");
        int reply; // 服务器响应值
        // 连接至服务器
        ftpClient.connect(hostName, serverPort);
        // 获取响应值
        reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftpClient.disconnect();
            throw new IOException("connect fail: " + reply);
        }
        // 登录到服务器
        ftpClient.login(userName, password);
        // 获取响应值
        reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftpClient.disconnect();
            throw new IOException("connect fail: " + reply);
        } else {

            // 获取登录信息
            FTPClientConfig config = new FTPClientConfig(ftpClient
                    .getSystemType().split(" ")[0]);
            config.setServerLanguageCode("zh");
            ftpClient.configure(config);
            // 使用被动模式设为默认
            ftpClient.enterLocalPassiveMode();
            // 二进制文件支持
            ftpClient
                    .setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);

            //////////////
            String LOCAL_CHARSET = "GBK";
            if (FTPReply.isPositiveCompletion(ftpClient.sendCommand(
                    "OPTS UTF8", "ON"))) {// 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
                LOCAL_CHARSET = "UTF-8";
            }
            ftpClient.setControlEncoding(LOCAL_CHARSET);
            ////////////////

        }
    }

    /**
     * 关闭FTP服务.
     *
     * @throws IOException
     */
    public void closeConnect() throws IOException {
        if (ftpClient != null) {
            // 退出FTP
            ftpClient.logout();
            // 断开连接
            ftpClient.disconnect();
        }
    }

    // ---------------------------------------------------上传、下载、删除监听---------------------------------------------

    /*
     * 上传进度监听
     */
    public interface UploadProgressListener {
        public void onUploadProgress(String currentStep, long uploadSize, File file);
    }

    /*
     * 下载进度监听
     */
    public interface DownLoadProgressListener {
        public void onDownLoadProgress(String currentStep, long downProcess, File file);
    }

    /*
     * 文件删除监听
     */
    public interface DeleteFileProgressListener {
        public void onDeleteProgress(String currentStep);
    }

}


