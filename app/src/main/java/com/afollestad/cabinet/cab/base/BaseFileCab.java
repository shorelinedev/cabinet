package com.afollestad.cabinet.cab.base;

import android.view.ActionMode;

import com.afollestad.cabinet.file.base.File;
import com.afollestad.cabinet.fragments.DirectoryFragment;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseFileCab extends BaseCab {

    public BaseFileCab() {
        super();
        mFiles = new ArrayList<File>();
    }

    private File mDirectory;
    private final List<File> mFiles;
    public boolean overrideDestroy;

    public abstract void paste();

    public abstract boolean canPaste();

    public void invalidateFab() {
        if (canPaste()) {
            boolean hide = false;
            for (File fi : getFiles()) {
                if (fi.getParent().equals(getDirectory())) {
                    hide = true;
                    break;
                }
            }
            getFragment().disableFab(hide);
        }
    }

    @Override
    public BaseFileCab setFragment(DirectoryFragment fragment) {
        mDirectory = fragment.getDirectory();
        super.setFragment(fragment);
        invalidateFab();
        fragment.setPasteMode(canPaste());
        return this;
    }

    public final BaseFileCab addFile(File file) {
        getFragment().mAdapter.setItemChecked(file, true);
        mFiles.add(file);
        invalidate();
        return this;
    }

    public final BaseFileCab addFiles(List<File> files) {
        getFragment().mAdapter.setItemsChecked(files, true);
        mFiles.addAll(files);
        invalidate();
        return this;
    }

    public final BaseFileCab removeFile(File file) {
        getFragment().mAdapter.setItemChecked(file, false);
        for (int i = 0; i < mFiles.size(); i++) {
            if (file.getPath().equals(mFiles.get(i).getPath())) {
                mFiles.remove(i);
                invalidate();
                break;
            }
        }
        return this;
    }

    public final BaseFileCab setFile(File file) {
        getFragment().mAdapter.resetChecked();
        getFragment().mAdapter.setItemChecked(file, true);
        clearFiles();
        mFiles.add(file);
        invalidate();
        return this;
    }

    public final BaseFileCab setFiles(List<File> files) {
        getFragment().mAdapter.resetChecked();
        getFragment().mAdapter.setItemsChecked(files, true);
        clearFiles();
        mFiles.addAll(files);
        invalidate();
        return this;
    }

    public final void clearFiles() {
        mFiles.clear();
    }

    public final boolean containsFile(File file) {
        for (File fi : mFiles) {
            if (fi.equals(file)) return true;
        }
        return false;
    }

    @Override
    public final void invalidate() {
        if (getFiles().size() == 0) finish();
        else super.invalidate();
    }

    public final File getDirectory() {
        return mDirectory;
    }

    public final List<File> getFiles() {
        return mFiles;
    }

    @Override
    public int getMenu() {
        return -1;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        if (!overrideDestroy) {
            clearFiles();
            getFragment().mAdapter.resetChecked();
            if (canPaste()) getFragment().setPasteMode(false);
        }
        super.onDestroyActionMode(actionMode);
    }
}
