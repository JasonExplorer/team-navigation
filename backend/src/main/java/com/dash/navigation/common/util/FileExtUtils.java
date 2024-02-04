package com.dash.navigation.common.util;

import com.dash.navigation.common.constants.TeamNavConstants;
import com.dash.navigation.common.util.thread.CompletableUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;

/**
 * FileExtUtils.
 *
 * @author Jason.liu
 * @date 2024-02-04
 */
@Slf4j
@UtilityClass
public class FileExtUtils {

    private static final String FILE_HEADER_HTML = "3C21444F";

    private static final String FILE_HEADER_ZIP = "504B0304";

    /**
     * isHtml.
     *
     * @param data data
     * @return boolean
     */
    public static boolean isHtml(byte[] data) {
        return fileType(FILE_HEADER_HTML, data);
    }

    /**
     * isZip.
     *
     * @param data data
     * @return boolean
     */
    public static boolean isZip(byte[] data) {
        return fileType(FILE_HEADER_ZIP, data);
    }

    private static boolean fileType(String fileHeader, byte[] data) {
        byte[] src = new byte[4];
        System.arraycopy(data, 0, src, 0, src.length);
        StringBuilder builder = new StringBuilder();
        for (byte bt : src) {
            String hv = Integer.toHexString(bt & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return fileHeader.equals(builder.toString());
    }

    /**
     * deleteFiles
     *
     * @param sync sync
     * @param paths paths
     */
    public static void deleteFiles(boolean sync, String... paths) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        for (String path : paths) {
            File file = new File(TeamNavConstants.ROOT_DIR + path);
            if (!file.exists()) {
                continue;
            }
            futures.add(CompletableUtils.runAsync(() -> {
                try {
                    FileUtils.forceDelete(file);
                } catch (IOException ex) {
                    log.error("删除原文件失败：【{}】", path, ex);
                }
            }));
        }
        if (sync && CollectionUtils.isNotEmpty(futures)) {
            CompletableUtils.waitAll(futures);
        }
    }

}
