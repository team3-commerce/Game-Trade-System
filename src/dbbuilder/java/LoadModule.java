package dbbuilder;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.util.FileSystemUtils;

public class LoadModule {
    private DockerClient dockerClient;

    // ==========================================
    // 일을 마친후 청소해야 할것들
    // ==========================================
    private String alpineContainerId;

    private String dockerVolume;

    private String tempDirectoryPath;

    public LoadModule() {
        this.dockerClient = BuilderUtil.createClient();
    }

    public void loadDbImage(String dataBuilderName) throws Exception {
        this.dockerVolume = "game-trade-system-volume-test";

        dockerComposeDown();

        try {
            this.dockerClient.removeVolumeCmd(this.dockerVolume).exec();
        } catch (NotFoundException e) {
        }

        loadVolume(dataBuilderName);

        dockerComposeUp();

        System.out.println("========================");
        System.out.println("DONE");
        System.out.println("========================");
    }

    private void loadVolume(String dataBuilderName) throws IOException, InterruptedException {

        String backupFileName = "%s.tar.bz2".formatted(dataBuilderName);
        Path backupFilePath = Path.of("./db-images", backupFileName).toAbsolutePath();

        final String alpineTarName = "db-backup.tar.bz2";

        // 백업 tar 파일을 임시 폴더로 옮김
        this.tempDirectoryPath =
                Files.createTempDirectory("docker-tmp-volume").toAbsolutePath().toString();
        Files.copy(backupFilePath, Path.of(this.tempDirectoryPath, alpineTarName));

        // =======================================
        // alpine image를 이용해 volume 생성
        // =======================================
        dockerClient.createVolumeCmd().withName(this.dockerVolume).exec();

        BuilderUtil.pullDockerImage(dockerClient, "alpine:latest");

        var hostConfig = HostConfig.newHostConfig()
                .withBinds(
                        new Bind(this.dockerVolume, new Volume("/volume")),
                        new Bind(this.tempDirectoryPath, new Volume("/backup")));

        var container = dockerClient
                .createContainerCmd("alpine")
                .withHostConfig(hostConfig)
                .withCmd(
                        "sh",
                        "-c",
                        "rm -rf /volume/* /volume/..?* /volume/.[!.]* ; tar -C /volume/ -xjf /backup/" + alpineTarName)
                .exec();

        this.alpineContainerId = container.getId();

        dockerClient.startContainerCmd(this.alpineContainerId).exec();

        dockerClient.waitContainerCmd(this.alpineContainerId).start().awaitCompletion();

        dockerClient.removeContainerCmd(this.alpineContainerId).exec();

        // =======================================
        // 임시 폴더 삭제
        // =======================================
        FileSystemUtils.deleteRecursively(Path.of(this.tempDirectoryPath));
    }

    private void dockerComposeUp() throws Exception {
        ProcessBuilder builder =
                new ProcessBuilder("docker", "compose", "-f", "docker-compose-test.yml", "up", "-d").inheritIO();
        builder.environment().put("GAME_TRADE_SYSTEM_DB_VOLUME_NAME", this.dockerVolume);
        int exitCode = builder.start().waitFor();

        if (exitCode != 0) {
            throw new Exception("docker compose up exited with %s".formatted(exitCode));
        }
    }

    private void dockerComposeDown() throws Exception {
        ProcessBuilder builder =
                new ProcessBuilder("docker", "compose", "-f", "docker-compose-test.yml", "down").inheritIO();
        builder.environment().put("GAME_TRADE_SYSTEM_DB_VOLUME_NAME", this.dockerVolume);
        int exitCode = builder.start().waitFor();

        if (exitCode != 0) {
            throw new Exception("docker compose down exited with %s".formatted(exitCode));
        }
    }

    public void cleanUpOnError() {
        try {
            dockerComposeDown();
        } catch (Exception e) {
            System.out.println(e);
        }

        if (alpineContainerId != null) {
            try {
                dockerClient
                        .removeContainerCmd(alpineContainerId)
                        .withForce(true)
                        .exec();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        if (tempDirectoryPath != null) {
            try {
                FileSystemUtils.deleteRecursively(Path.of(this.tempDirectoryPath));
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        if (dockerVolume != null) {
            try {
                dockerClient.removeVolumeCmd(dockerVolume).exec();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
