package com.onarandombox.MultiverseCore.worldnew.helpers;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Try;
import org.jvnet.hk2.annotations.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

@Service
public class FilesManipulator {

    public Try<Void> deleteFolder(File file) {
        return deleteFolder(file.toPath());
    }

    public Try<Void> deleteFolder(Path path) {
        try (Stream<Path> files = Files.walk(path)) {
            files.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
            return Try.success(null);
        } catch (IOException e) {
            Logging.severe("Failed to delete folder: " + path.toAbsolutePath());
            e.printStackTrace();
            return Try.failure(e);
        }
    }
}
