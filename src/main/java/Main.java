import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();

        Path destination = Paths.get("DESTINATION");
        Path root = Paths.get("FROM");

        List<Path> paths = Files.list(root).filter(item -> item.toString().contains("FILTER")).collect(Collectors.toList());
        for (Path path : paths) {
            Files.walk(path).parallel().filter(item -> Files.isRegularFile(item) && item.toString().endsWith(".zip"))
                    .forEach(item -> unzip(item, destination));
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println(elapsedTime);
    }

    private static void unzip(Path from, Path to) {
        try (FileSystem fileSystem = FileSystems.newFileSystem(from, null)) {
            Files.walkFileTree(fileSystem.getPath(fileSystem.getSeparator()), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    System.out.println(dir);
                    Path newDirectory = Paths.get(to.toString(), dir.toString());
                    if (Files.notExists(newDirectory)) {
                        Files.createDirectory(newDirectory);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    System.out.println(file);
                    Files.copy(file, Paths.get(to.toString(), file.toString()));
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
