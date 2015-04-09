package com.zoltarianie.ftpclient.src;

import android.os.AsyncTask;
import android.util.Log;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class FtpCon {

    private static FtpCon ourInstance = new FtpCon();

    public static FtpCon getInstance() {
        return ourInstance;
    }

    private FtpCon() {
    }

    private FTPClient mFtpClient = new FTPClient();

    private String path = "";
    private Event event;
    private String newPath = "";

    public void ftpPrintFilesList(String dir_path) {

    }

    public String getPathToWindow() {
        return ("ftp:/" + path);
    }

    public String getPathToDownload() {
        return ("" + path);
    }

    // -----------------------------------------------------------------------------
    // getDir ----------------------------------------------------------------------
    public HashMap<String, String>[] aFiles = null;

    public void getFilesFromServ(Event event) {
        this.event = event;
        this.newPath = "";
        CommunicateDialog.getInstance().showProgresDialog();
        FtpPrintFilesList ftpPrintFilesList = new FtpPrintFilesList();
        ftpPrintFilesList.execute("");
    }

    public void getFilesFromServ(Event event, String newPath) {
        this.event = event;
        this.newPath = newPath;
        CommunicateDialog.getInstance().showProgresDialog();
        FtpPrintFilesList ftpPrintFilesList = new FtpPrintFilesList();
        ftpPrintFilesList.execute("");
    }

    // AsyncTask -----------------------------------------------------------------
    private class FtpPrintFilesList extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String dir = urls[0];
            String result = "";

            try {
                if (!newPath.equals("")) {
                    mFtpClient.changeWorkingDirectory(newPath);
                }
                path = mFtpClient.printWorkingDirectory();
                int skip = path.equals("/") ? 2 : 1;
                FTPFile[] ftpFiles = mFtpClient.listFiles(dir);
                int length = ftpFiles.length;
                aFiles = new HashMap[length - skip];
                for (int i = skip; i < length; i++) {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    HashMap<String, String> poz = new HashMap<String, String>();
                    poz.put("name", "" + ftpFiles[i].getName());
                    poz.put("type", "" + (ftpFiles[i].isFile() ? "file" : "dir"));
                    poz.put("mod_dat", "" + sdf.format(ftpFiles[i].getTimestamp().getTime()));
                    aFiles[i - skip] = poz;
                }
                result = "true";
            } catch (Exception e) {
                result = e.getMessage();
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            CommunicateDialog.getInstance().hideProgresDialog();
            event.dispath("ftpGetFiles", result);
        }
    }

    // -----------------------------------------------------------------------------
    // conectToFtp -----------------------------------------------------------------
    public void conectToFtp(HashMap<String, String> ftpParams, Event event) {
        this.event = event;
        CommunicateDialog.getInstance().showProgresDialog();
        cConnectToFtp newConection = new cConnectToFtp();
        newConection.execute(ftpParams.get("url"), ftpParams.get("user"), ftpParams.get("pass"));
    }

    // AsyncTask -----------------------------------------------------------------
    private class cConnectToFtp extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String url = urls[0];
            String user = urls[1];
            String pass = urls[2];
            String result = "";

            try {
                mFtpClient.setConnectTimeout(10 * 1000);
                mFtpClient.connect(InetAddress.getByName(url));
                if (!FTPReply.isPositiveCompletion(mFtpClient.getReplyCode())) {
                    result = "Server refused connection on port.";
                } else if (mFtpClient.login(user, pass)) {
                    mFtpClient.setFileType(FTP.ASCII_FILE_TYPE);
                    mFtpClient.enterLocalPassiveMode();
                    result = "true";
                } else {
                    result = "Login failed on server.";
                }
            } catch (SocketException e) {
                result = e.getMessage();
                e.printStackTrace();
            } catch (UnknownHostException e) {
                result = e.getMessage();
                e.printStackTrace();
            } catch (IOException e) {
                result = e.getMessage();
                e.printStackTrace();
            }

            ftpPrintFilesList("");

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            CommunicateDialog.getInstance().hideProgresDialog();
            event.dispath("ftpCon", result);
        }
    }

    // -----------------------------------------------------------------------------
    // downloadFromFtp -----------------------------------------------------------------
    public void downloadFromFtp(String saveDirPath, String remoteDirPath, HashMap<String, String>[] aFilesToCopy, Event event) {
        this.event = event;
        CommunicateDialog.getInstance().showProgresDialog(true);
        cDownloadFromFtp newDownloadFromFtp = new cDownloadFromFtp();
        newDownloadFromFtp.saveDirPath = saveDirPath + "/";
        newDownloadFromFtp.remoteDirPath = remoteDirPath;
        newDownloadFromFtp.aFilesToCopy = aFilesToCopy;
        newDownloadFromFtp.execute();
    }

    // AsyncTask -----------------------------------------------------------------
    private class cDownloadFromFtp extends AsyncTask<String, String, String> {

        public String saveDirPath;
        public String remoteDirPath;
        public HashMap<String, String>[] aFilesToCopy;

        @Override
        protected String doInBackground(String... urls) {

            String result = "";

            try {
                for (int i = 0; i < aFilesToCopy.length; i++) {
                    if (CommunicateDialog.getInstance().pd == null) {
                        break;
                    }
                    String type = aFilesToCopy[i].get("type");
                    String name = aFilesToCopy[i].get("name");
                    if (type.equals("file")) {
                        boolean success = downloadSingleFile(mFtpClient, remoteDirPath + name, saveDirPath + name);
                        if (success) {
                            publishProgress("DOWNLOADED the file: " + remoteDirPath + name);
                        } else {
                            publishProgress("COULD NOT download the file: " + remoteDirPath + name);
                        }
                    } else {
                        FTPFile[] subFiles = mFtpClient.listFiles(remoteDirPath + name);
                        if (subFiles.length < 3) {
                            File newDir = new File(saveDirPath + name);
                            boolean created = newDir.mkdirs();
                            if (created) {
                                publishProgress("CREATED the directory: " + saveDirPath + name);
                            } else {
                                publishProgress("COULD NOT create the directory: " + saveDirPath + name);
                            }
                        } else {
                            downloadDirectory(mFtpClient, remoteDirPath + name, "", saveDirPath);
                        }
                    }
                }
                result = "true";
            } catch (IOException e) {
                result = e.getMessage();
                e.printStackTrace();
            }

            return result;
        }

        @Override
        public void onProgressUpdate(String... progress) {
            CommunicateDialog.getInstance().ProgresDialogSetNewText(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            CommunicateDialog.getInstance().hideProgresDialog();
            event.dispath("ftpDownload", result);
        }

        private void downloadDirectory(FTPClient ftpClient, String parentDir, String currentDir, String saveDir) throws IOException {
            String dirToList = parentDir;
            if (!currentDir.equals("")) {
                dirToList += "/" + currentDir;
            }

            FTPFile[] subFiles = ftpClient.listFiles(dirToList);

            if (subFiles != null && subFiles.length > 0) {
                for (FTPFile aFile : subFiles) {
                    if (CommunicateDialog.getInstance().pd == null) {
                        break;
                    }

                    String currentFileName = aFile.getName();
                    if (currentFileName.equals(".") || currentFileName.equals("..")) {
                        // skip parent directory and the directory itself
                        continue;
                    }
                    String filePath = parentDir + "/" + currentDir + "/" + currentFileName;
                    if (currentDir.equals("")) {
                        filePath = parentDir + "/" + currentFileName;
                    }

                    String newDirPath = saveDir + parentDir + File.separator + currentDir + File.separator + currentFileName;
                    if (currentDir.equals("")) {
                        newDirPath = saveDir + parentDir + File.separator + currentFileName;
                    }

                    if (aFile.isDirectory()) {
                        // create the directory in saveDir
                        File newDir = new File(newDirPath);
                        boolean created = newDir.mkdirs();
                        if (created) {
                            publishProgress("CREATED the directory: " + newDirPath);
                        } else {
                            publishProgress("COULD NOT create the directory: " + newDirPath);
                        }

                        // download the sub directory
                        downloadDirectory(ftpClient, dirToList, currentFileName, saveDir);
                    } else {
                        // download the file
                        boolean success = downloadSingleFile(ftpClient, filePath, newDirPath);
                        if (success) {
                            publishProgress("DOWNLOADED the file: " + filePath);
                        } else {
                            publishProgress("COULD NOT download the file: " + filePath);
                        }
                    }
                }
            }
        }

        public boolean downloadSingleFile(FTPClient ftpClient, String remoteFilePath, String savePath) throws IOException {
            File downloadFile = new File(savePath);

            File parentDir = downloadFile.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdir();
            }

            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
            try {
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                return ftpClient.retrieveFile(remoteFilePath, outputStream);
            } catch (IOException ex) {
                throw ex;
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        }
    }

    // -----------------------------------------------------------------------------
    // downloadFromFtp -----------------------------------------------------------------
    public void uploadToFtp(String localDirPath, String remoteDirPath, HashMap<String, String>[] aFilesToCopy, Event event) {
        this.event = event;
        CommunicateDialog.getInstance().showProgresDialog(true);
        cUploadToFtp newUploadToFtp = new cUploadToFtp();
        newUploadToFtp.localDirPath = localDirPath + "/";
        newUploadToFtp.remoteDirPath = remoteDirPath;
        newUploadToFtp.aFilesToCopy = aFilesToCopy;
        newUploadToFtp.execute();
    }

    // AsyncTask -----------------------------------------------------------------
    private class cUploadToFtp extends AsyncTask<String, String, String> {

        public String localDirPath;
        public String remoteDirPath;
        public HashMap<String, String>[] aFilesToCopy;

        @Override
        protected String doInBackground(String... urls) {

            String result = "";

            try {
                for (int i = 0; i < aFilesToCopy.length; i++) {
                    if (CommunicateDialog.getInstance().pd == null) {
                        break;
                    }
                    String type = aFilesToCopy[i].get("type");
                    String name = aFilesToCopy[i].get("name");

                    if (type.equals("file")) {
                        Log.i("ftp", localDirPath + name + ", " + remoteDirPath +"/"+ name);
                    } else {
                        Log.i("ftp", remoteDirPath+", "+localDirPath + name);
                    }

                    if (type.equals("file")) {
                        boolean success = uploadSingleFile(mFtpClient, localDirPath + name, remoteDirPath +"/"+ name);
                        if (success) {
                            publishProgress("UPLOADED a file to: " + remoteDirPath +"/"+ name);
                        } else {
                            publishProgress("COULD NOT upload the file: " + localDirPath + name);
                        }
                    } else {
                        uploadDirectory(mFtpClient, remoteDirPath, localDirPath + name, "");
                    }
                }
                result = "true";
            } catch (IOException e) {
                result = e.getMessage();
                e.printStackTrace();
            }

            return result;
        }

        @Override
        public void onProgressUpdate(String... progress) {
            CommunicateDialog.getInstance().ProgresDialogSetNewText(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            CommunicateDialog.getInstance().hideProgresDialog();
            event.dispath("ftpUpload", result);
        }

        public void uploadDirectory(FTPClient ftpClient, String remoteDirPath, String localParentDir, String remoteParentDir) throws IOException {

            System.out.println("LISTING directory: " + localParentDir);

            File localDir = new File(localParentDir);
            File[] subFiles = localDir.listFiles();
            if (subFiles != null && subFiles.length > 0) {
                for (File item : subFiles) {
                    if (CommunicateDialog.getInstance().pd == null) {
                        break;
                    }

                    String remoteFilePath = remoteDirPath + "/" + remoteParentDir + "/" + item.getName();
                    if (remoteParentDir.equals("")) {
                        remoteFilePath = remoteDirPath + "/" + item.getName();
                    }

                    if (item.isFile()) {
                        // upload the file
                        String localFilePath = item.getAbsolutePath();
                        System.out.println("About to upload the file: " + localFilePath);
                        boolean uploaded = uploadSingleFile(ftpClient, localFilePath, remoteFilePath);
                        if (uploaded) {
                            publishProgress("UPLOADED a file to: " + remoteFilePath);
                        } else {
                            publishProgress("COULD NOT upload the file: " + localFilePath);
                        }
                    } else {
                        // create directory on the server
                        boolean created = ftpClient.makeDirectory(remoteFilePath);
                        if (created) {
                            publishProgress("CREATED the directory: " + remoteFilePath);
                        } else {
                            publishProgress("COULD NOT create the directory: " + remoteFilePath);
                        }

                        // upload the sub directory
                        String parent = remoteParentDir + "/" + item.getName();
                        if (remoteParentDir.equals("")) {
                            parent = item.getName();
                        }

                        localParentDir = item.getAbsolutePath();
                        uploadDirectory(ftpClient, remoteDirPath, localParentDir, parent);
                    }
                }
            }
        }

        public boolean uploadSingleFile(FTPClient ftpClient, String localFilePath, String remoteFilePath) throws IOException {
            File localFile = new File(localFilePath);

            InputStream inputStream = new FileInputStream(localFile);
            try {
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                return ftpClient.storeFile(remoteFilePath, inputStream);
            } finally {
                inputStream.close();
            }
        }
    }
}



