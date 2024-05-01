package com.threlease.base.utils.blockchains;

import com.threlease.base.utils.Failable;
import com.threlease.base.utils.Hash;
import lombok.Getter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

@Getter
public class Peer {
    private String currentDirectory = System.getProperty("user.dir") + "/data/peers";

    public Failable<Boolean, String> addPeer(String requestAddr) {
        File dir = new File(currentDirectory);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, new Hash().base64Encode(requestAddr));
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("flow_" + new Hash().base64Encode(requestAddr));

            return Failable.success(true);
        } catch (IOException e) {
            return Failable.error(e.getMessage());
        }
    }

    public Failable<File, String> getPeer(String id) {
        File directory = new File(currentDirectory);
        File[] fileList = directory.listFiles();

        if (fileList == null)
            return Failable.error("Not Found Peer");

        return Failable.success(
                Arrays.stream(fileList).filter(
                        (v) -> v.getName().split("_")[1].equals(id)
                )
                .toList()
                .get(0)
        );
    }

    public List<Path> peerList() throws IOException {
        List<Path> fileList = new ArrayList<>();
        Path directory = Paths.get(currentDirectory);

        Files.walkFileTree(directory, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                // 파일일 경우 파일의 경로를 출력
                fileList.add(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                // 파일에 접근할 수 없는 경우 예외 처리
                System.err.println(exc);
                return FileVisitResult.CONTINUE;
            }
        });

        return fileList;
    }
}
