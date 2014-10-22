package com.afollestad.cabinet.zip;

import android.app.ProgressDialog;
import android.util.Log;

import com.afollestad.cabinet.R;
import com.afollestad.cabinet.file.LocalFile;
import com.afollestad.cabinet.file.base.File;
import com.afollestad.cabinet.fragments.DirectoryFragment;
import com.afollestad.cabinet.utils.Utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Unzipper {

    private static ProgressDialog mDialog;

    private static void unzip(final DirectoryFragment context, final LocalFile zipFile) {
        final String outputFolder = context.getDirectory().getPath() + java.io.File.separator + zipFile.getNameNoExtension();
        log("Output folder: " + outputFolder);
        byte[] buffer = new byte[1024];
        try {
            java.io.File folder = new java.io.File(outputFolder);
            if (!folder.exists()) folder.mkdir();
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile.toJavaFile()));
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                if (ze.isDirectory()) {
                    ze = zis.getNextEntry();
                    continue;
                }
                log("Original file: " + fileName);
                if (fileName.startsWith("/")) fileName = fileName.substring(1);
                String newPath = outputFolder + java.io.File.separator + fileName;
                log("Unzip file: " + newPath);
                java.io.File newFile = new java.io.File(newPath);
                new java.io.File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void unzip(final DirectoryFragment context, final List<File> files, final Zipper.ZipCallback callback) {
        mDialog = new ProgressDialog(context.getActivity());
        mDialog.setTitle(R.string.unzipping);
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setMax(files.size());
        mDialog.setCancelable(true);
        mDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (File fi : files) {
                        unzip(context, (LocalFile) fi);
                        if(mDialog == null || !mDialog.isShowing()) {
                            // Cancelled
                            break;
                        }
                        mDialog.setProgress(mDialog.getProgress() + 1);
                    }
                    if (context.getActivity() == null) return;
                    context.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDialog.dismiss();
                            context.reload();
                            if (mDialog.isShowing() && callback != null) callback.onComplete();
                        }
                    });
                } catch (final Exception e) {
                    e.printStackTrace();
                    if (context.getActivity() == null) return;
                    context.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDialog.dismiss();
                            Utils.showErrorDialog(context.getActivity(), R.string.failed_unzip_file, e);
                        }
                    });
                }
            }
        }).start();
    }

    private static void log(String message) {
        Log.v("Unzipper", message);
    }
}