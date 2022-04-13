package com.example.watcher.service;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

/***
 * ref: https://m.blog.naver.com/wpdus2694/221677886304
 */
@Slf4j
public class WatchService {
    public static void main(String[] args) {
        // monitoring path set.
        String monitoringPath = "./src/main/resources/checkTest.txt";
        log.info("monitoringPath: {}", monitoringPath);
        try {
            // create watchService.
            java.nio.file.WatchService watchService = FileSystems.getDefault().newWatchService();
            // check file.
            File file = new File(monitoringPath);
            // create path.
            Path path = Paths.get(file.getParentFile().getAbsolutePath());
            // registers the file located by this path with a watch service.
            path.register
                    (
                            watchService,
                            StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_DELETE,
                            StandardWatchEventKinds.ENTRY_MODIFY
                    );
            while (true) {
                try {
                    WatchKey key = watchService.take();
                    List<WatchEvent<?>> watchEventList = key.pollEvents(); // Wait for event to be received.
                    for (WatchEvent<?> event : watchEventList) {
                        WatchEvent.Kind<?> kind = event.kind();
                        Path pth = (Path) event.context();
                        if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                            // Code executed when a file is generated.
                            log.info("ENTRY_CREATE: {}", pth.getFileName());
                        } else if (kind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                            // Code executed when files are deleted.
                            log.info("ENTRY_DELETE: {}", pth.getFileName());
                        } else if (kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                            // Code executed when files are modified.
                            log.info("ENTRY_MODIFY: {}", pth.getFileName());
                        } else if (kind.equals(StandardWatchEventKinds.OVERFLOW)) {
                            // Codes executed when an event is lost or discarded in the operating system.
                            System.out.println("OVERFLOW");
                            log.info("OVERFLOW: {}", pth.getFileName());
                        }
                    }
                    if (!key.reset()) break; // reset key.
                } catch (InterruptedException e) {
                    log.error("key = watchService.take();");
                    e.printStackTrace();
                }
            }
            watchService.close(); // Close watchService.

        } catch (IOException e) {
            log.error("create watchService");
            e.printStackTrace();
        }
    }
}
