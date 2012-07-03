/*
 * This file is part of SpaceRTK (http://spacebukkit.xereo.net/).
 *
 * SpaceRTK is free software: you can redistribute it and/or modify it under the terms of the
 * Attribution-NonCommercial-ShareAlike Unported (CC BY-NC-SA) license as published by the Creative
 * Common organization, either version 3.0 of the license, or (at your option) any later version.
 *
 * SpaceRTK is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * Attribution-NonCommercial-ShareAlike Unported (CC BY-NC-SA) license for more details.
 *
 * You should have received a copy of the Attribution-NonCommercial-ShareAlike Unported (CC BY-NC-SA)
 * license along with this program. If not, see <http://creativecommons.org/licenses/by-nc-sa/3.0/>.
 */
package me.neatmonster.spacertk.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import me.neatmonster.spacemodule.api.Action;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

/**
 * Actions handler for any File-related actions
 */
public class FileActions {

    /**
     * Copy's a directory to another directory
     * @param oldDirectory Directory to copy
     * @param newDirectory Directory to copy to
     * @return If successful
     */
    @Action(
            aliases = {"copyDirectory", "copyDir"})
    public boolean copyDirectory(final String oldDirectory, final String newDirectory) {
        try {
            FileUtils.copyDirectory(new File(oldDirectory), new File(newDirectory));
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Copy's a file
     * @param oldFile File to copy
     * @param newFile File to copy to
     * @return
     */
    @Action(
            aliases = {"copyFile"})
    public boolean copyFile(final String oldFile, final String newFile) {
        try {
            FileUtils.copyFile(new File(oldFile), new File(newFile));
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Creates a directory
     * @param directory Directory to create
     * @return If successful
     */
    @Action(
            aliases = {"createDirectory", "createDir"})
    public boolean createDirectory(final String directory) {
        try {
            FileUtils.forceMkdir(new File(directory));
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Creates a file
     * @param file File to create
     * @return If successful
     */
    @Action(
            aliases = {"createFile"})
    public boolean createFile(final String file) {
        final File file_ = new File(file);
        if (!file_.exists())
            try {
                file_.createNewFile();
                return true;
            } catch (final IOException e) {
                e.printStackTrace();
                return false;
            }
        return true;
    }

    /**
     * Deletes a directory
     * @param directory Directory to delete
     * @return If successful
     */
    @Action(
            aliases = {"deleteDirectory", "deleteDir"})
    public boolean deleteDirectory(final String directory) {
        try {
            FileUtils.deleteDirectory(new File(directory));
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes a file
     * @param file File to delete
     * @return If successful
     */
    @Action(
            aliases = {"deleteFile"})
    public boolean deleteFile(final String file) {
        FileUtils.deleteQuietly(new File(file));
        return true;
    }

    /**
     * Reads a file and returns a String representation of the contents
     * @param file File to read
     * @return String representation of the File
     */
    @Action(
            aliases = {"getFileContent", "getContent"})
    public String getFileContent(final String file) {
        try {
            return FileUtils.readFileToString(new File(file), "UTF-8");
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Gets information about a File
     * @param file File to get information about
     * @return Information about a file
     */
    @Action(
            aliases = {"getFileInformations", "fileInformations", "informations"})
    public TreeMap<String, Object> getFileInformations(final String file) {
        final TreeMap<String, Object> fileInformations = new TreeMap<String, Object>();
        final File file_ = new File(file);
        if (file_.exists()) {
            fileInformations.put("Name", file_.getName());
            fileInformations.put("Path", file_.getPath());
            fileInformations.put("Size", file_.length());
            fileInformations.put("Execute", file_.canExecute());
            fileInformations.put("Read", file_.canRead());
            fileInformations.put("Write", file_.canWrite());
            fileInformations.put("IsDirectory", file_.isDirectory());
            fileInformations.put("IsFile", file_.isFile());
            fileInformations.put("IsHidden", file_.isHidden());
            final FileNameMap fileNameMap = URLConnection.getFileNameMap();
            fileInformations.put("Mime", fileNameMap.getContentTypeFor("file://" + file_.getPath()));
            return fileInformations;
        }
        return new TreeMap<String, Object>();
    }

    /**
     * Gets a list of directories in a directory
     * @param directory Base directory
     * @return List of directories
     */
    @Action(
            aliases = {"listDirectories", "listDirs"})
    public List<String> listDirectories(final String directory) {
        return Arrays.asList(new File(directory).list(DirectoryFileFilter.INSTANCE));
    }

    /**
     * Gets a list of files in a directory
     * @param directory Base directory
     * @return List of files
     */
    @Action(
            aliases = {"listFiles"})
    public List<String> listFiles(final String directory) {
        return Arrays.asList(new File(directory).list(FileFileFilter.FILE));
    }

    /**
     * Gets a list of files and directories in a directory
     * @param directory Base directory
     * @return List of files and directory
     */
    @Action(
            aliases = {"listFilesAndDirectories", "listFilesDirs"})
    public List<String> listFilesAndDirectories(final String directory) {
        return Arrays.asList(new File(directory).list(TrueFileFilter.INSTANCE));
    }

    /**
     * Sends a file to a URL
     * @param url URL to send to
     * @param file File to send
     * @return If successful
     */
    @Action(
            aliases = {"sendFile", "fileSend"})
    public boolean sendFile(final String url, final String file) {
        try {
            final File file_ = new File(file);
            if (file_.exists())
                if (!deleteFile(file_.getPath()))
                    return false;
            final URL url_ = new URL(url);
            final ReadableByteChannel readableByteChannel = Channels.newChannel(url_.openStream());
            final FileOutputStream fileOutputStream = new FileOutputStream(file_);
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, 1 << 24);
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Sets a files contents
     * @param file File to set
     * @param content Contents to set
     * @return If successful
     */
    @Action(
            aliases = {"setFileContent", "setContent"})
    public boolean setFileContent(final String file, final String content) {
        try {
            FileUtils.write(new File(file), content, "UTF-8");
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
