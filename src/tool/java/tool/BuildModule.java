package tool;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.Volume;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.FileSystemUtils;

public class BuildModule {

    private DockerClient dockerClient;

    // ==========================================
    // 일을 마친후 청소해야 할것들
    // ==========================================
    private String alpineContainerId;

    private String dockerVolume;

    private String tempDirectoryPath;

    private ConfigurableApplicationContext springContext;

    public BuildModule() {
        this.dockerClient = BuilderUtil.createClient();
    }

    public void buildDbTar(List<String> argsToPass, String dataBuilderName) throws Exception {
        if (!BuilderUtil.dataBuilderBeanExists(dataBuilderName)) {
            throw new Exception("Bean named %s that inherits DataBuilder doesn't exits".formatted(dataBuilderName));
        }

        DockerClient dockerClient = BuilderUtil.createClient();

        this.dockerVolume = "tmp-db-volume-" + UUID.randomUUID();

        dockerClient.createVolumeCmd().withName(this.dockerVolume).exec();

        BuilderUtil.dockerComposeDown();

        BuilderUtil.dockerComposeUp(
                TradeDockerBuilder.DB_PORT,
                TradeDockerBuilder.REDIS_PORT,
                this.dockerVolume
        );

        System.out.println("========================================");
        System.out.println("WAITING DOCKER IMAGE TO START (10s)");
        System.out.println("========================================");

        Thread.sleep(10000);

        argsToPass.add(
                0, "--spring.config.additional-location=file:./misc/application-docker-builder.yml");

        this.springContext = SpringApplication.run(TradeDockerBuilder.class, argsToPass.toArray(new String[0]));

        System.out.println("========================");
        System.out.println("SAVING DATA TO DB");
        System.out.println("========================");

        Class<?> c = Class.forName(BuilderUtil.getDataBuilderFullClassName(dataBuilderName));
        DataBuilder builder = (DataBuilder) this.springContext.getBean(c);
        builder.run();

        System.out.println("========================");
        System.out.println("STOPING CONTAINER");
        System.out.println("========================");

        this.springContext.close();

        BuilderUtil.dockerComposeDown();

        System.out.println("========================================");
        System.out.println("EXPORTING DOCKER IMAGE TO TAR");
        System.out.println("========================================");

        saveVolume(dataBuilderName);

        System.out.println("========================");
        System.out.println("DONE");
        System.out.println("========================");
    }

    private void saveVolume(String backupName) throws InterruptedException, IOException {

        this.tempDirectoryPath =
                Files.createTempDirectory("docker-tmp-volume").toAbsolutePath().toString();

        String backupFileName = "%s.tar.bz2".formatted(backupName);

        // =======================================
        // alpine image를 이용해 백업 tar 생성
        // =======================================
        BuilderUtil.pullDockerImage(this.dockerClient, "alpine:latest");

        var hostConfig = HostConfig.newHostConfig()
                .withBinds(
                        new Bind(this.dockerVolume, new Volume("/volume")),
                        new Bind(this.tempDirectoryPath, new Volume("/backup")));

        var container = dockerClient
                .createContainerCmd("alpine")
                .withHostConfig(hostConfig)
                .withCmd("tar", "-cjf", "/backup/%s".formatted(backupFileName), "-C", "/volume", "./")
                .exec();

        this.alpineContainerId = container.getId();

        dockerClient.startContainerCmd(this.alpineContainerId).exec();
        dockerClient.waitContainerCmd(this.alpineContainerId).start().awaitCompletion();
        dockerClient.removeContainerCmd(this.alpineContainerId).exec();

        // =======================================
        // 임시 폴더에 있는 tar를 현재 폴더로 옮김
        // =======================================
        Files.createDirectories(Path.of("./db-images"));

        Path backupTarPath = Path.of("./db-images", backupFileName);

        moveToBackupIfExists(backupTarPath);

        Files.copy(Paths.get(this.tempDirectoryPath, backupFileName), backupTarPath);
    }

    private void moveToBackupIfExists(Path path) throws IOException{
        if (!Files.exists(path)) {
            return;
        }

        Path parent = path.getParent();

        String ogFileBase;
        String ogFileExt;

        {
            String fileName = path.getFileName().toString();
            int dotIndex = fileName.indexOf(".");

            if (dotIndex < 0) {
                ogFileBase = fileName;
                ogFileExt = "";
            }else {
                ogFileBase = fileName.substring(0, dotIndex);
                ogFileExt = fileName.substring(dotIndex, fileName.length());
            }
        }

        int backupIndex = 1;
        Path newPath = parent.resolve("%s_bak_%d%s".formatted(ogFileBase, backupIndex, ogFileExt));

        while (Files.exists(newPath)) {
            backupIndex++;
            newPath = parent.resolve("%s_bak_%d%s".formatted(ogFileBase, backupIndex, ogFileExt));
        }

        Files.move(path, newPath);
    }

    public void cleanUp() {
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

        try {
            BuilderUtil.dockerComposeDown();
        } catch (Exception e) {
            System.out.println(e);
        }

        if (dockerVolume != null) {
            try {
                dockerClient.removeVolumeCmd(dockerVolume).exec();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        if (springContext != null) {
            try {
                springContext.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        if (tempDirectoryPath != null) {
            try {
                FileSystemUtils.deleteRecursively(Path.of(tempDirectoryPath));
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
