package com.path.uploadsvn

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.tmatesoft.svn.core.wc.*

public class UploadSVNPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        UploadSVN uploadTask = project.getTasks().create("uploadsvn", UploadSVN.class)
        def projectName = "Autozi"
        if (project.hasProperty("ProjectName")) {
            projectName = project.getProperties().get("ProjectName");
        }
        delAllFile(project.rootDir.absolutePath + "/apks")
        //输出到指定路径
        project.android.applicationVariants.all { variant ->
            variant.outputs.all { output ->
                if (variant.buildType.name.equals('debug')) {
                    if (outputFileName != null && outputFileName.endsWith('.apk')) {
                        def flavor = variant.productFlavors[0]
                        System.out.println("flavor is "+flavor.name)
                        if (flavor.name.equals("svn")) {
                            variant.getPackageApplication().outputDirectory = new File(project.rootDir.absolutePath + "/apks")
                            outputFileName = projectName + "-${variant.buildType.name}-${getSvnRevision(project)}.apk";
                            System.out.println("apk's outputFileName is " +output.outputFile.parentFile.absolutePath + outputFileName)
                            uploadTask.setLocalPath(output.outputFile.parentFile.absolutePath)
                        }
                        System.out.println("apk's location is " + output.outputFile.parentFile.absolutePath + java.io.File.separator + outputFileName)
                    }
//
//                    def outputFile = output.outputFile
//                    if (outputFile != null && outputFile.name.endsWith('.apk')) {
//                        // 输出apk名称为Debug_v1.0_2015-01-15_wandoujia.apk
//                        def fileName = projectName+"-${variant.buildType.name}-${getSvnRevision(project)}.apk"
//                        delAllFile(localPath)
//                        output.outputFile = new File(localPath, fileName)
//                        System.out.println("apk's location is" + output.outputFile)
//                    }
                }

            }
        }

    }

    // 删除指定文件夹下所有文件
    private boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                flag = true;
            }
        }
        return flag;
    }

    private long getSvnRevision(Project project) {
        ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
        SVNClientManager clientManager = SVNClientManager.newInstance(options);
        SVNStatusClient statusClient = clientManager.getStatusClient();
        SVNStatus status = statusClient.doStatus(project.getProjectDir(), false);
        SVNRevision revision = status.getRevision();
        def svnNum = revision.getNumber();
        println("Svn version: " + svnNum);
        return svnNum;
    }

    def releaseTime() {
        return new Date().format("yyyy-MM-dd-HH-mm")
    }

}