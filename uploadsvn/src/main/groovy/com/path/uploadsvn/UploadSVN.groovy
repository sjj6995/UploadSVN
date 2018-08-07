package com.path.uploadsvn

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.impldep.org.apache.http.util.TextUtils
import org.tmatesoft.svn.core.SVNDepth
import org.tmatesoft.svn.core.SVNURL
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions
import org.tmatesoft.svn.core.wc.ISVNOptions
import org.tmatesoft.svn.core.wc.SVNClientManager
import org.tmatesoft.svn.core.wc.SVNCommitClient
import org.tmatesoft.svn.core.wc.SVNWCUtil

public class UploadSVN extends DefaultTask {
    private String localPath;
    UploadSVN() {
        super()
        dependsOn 'assembleDebug'
    }

    @TaskAction
    void upload() {

        if (!project.hasProperty("UserName")) {
            throw new RuntimeException("you should set UserName in main module's gradle.properties")
        }
        if (!project.hasProperty("PassWord")) {
            throw new RuntimeException("you should set PassWord in main module's gradle.properties")
        }
        if (localPath == null||localPath.equals("")) {
            System.out.println("localPath: "+localPath)
            throw new RuntimeException("Please add flavaor svn{} in main module's build.gradle")
        }

        if (!project.hasProperty("SvnPath")) {
            throw new RuntimeException("you should set SvnPath in main module's gradle.properties")
        }

        String userName = project.getProperties().get("UserName")
        String passWord = project.getProperties().get("PassWord")
        String svnPath = project.getProperties().get("SvnPath")

        System.out.println("apk路径："+localPath);
        deleteJsonFile(localPath)

        SVNRepositoryFactoryImpl.setup();
        ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
        SVNClientManager ourClientManager = SVNClientManager.newInstance(
                (DefaultSVNOptions) options, userName, passWord);
        File impDir = new File(localPath);
        SVNCommitClient commitClient = ourClientManager.getCommitClient();
        commitClient.setIgnoreExternals(false);
//    try {
        SVNURL repositoryOptUrl = SVNURL.parseURIEncoded(svnPath);
        commitClient.doImport(impDir,
                repositoryOptUrl, "提交新版本", null, true, true,
                SVNDepth.INFINITY);
//    } catch (SVNException e) {
//        // TODO Auto-generated catch block
//        e.printStackTrace();
//    }

        System.out.println("apk upload to svn successfully!")
    }

    private void deleteJsonFile(String path){
        File file = new File(path)
        File temp = null ;
        for (int i = 0; i < file.list().length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + file.list()[i]);
            } else {
                temp = new File(path + File.separator + file.list()[i]);
            }
            if (!file.list()[i].endsWith(".apk")){
                if (temp.isFile()) {
                    temp.delete();
                }
            }
        }
    }


    public void setLocalPath(String path){
        System.out.println("path: "+path)
        this.localPath = path
    }
    @Input
    public String getLocalPath(){
        return localPath;
    }
}