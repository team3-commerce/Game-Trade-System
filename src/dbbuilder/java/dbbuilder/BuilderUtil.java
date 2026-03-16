package dbbuilder;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

public class BuilderUtil {
    public static DockerClient createClient() {
        DockerClientConfig config =
                DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .build();

        return DockerClientImpl.getInstance(config, httpClient);
    }

    public static void pullDockerImage(DockerClient dockerClient, String imageName) throws InterruptedException {

        dockerClient
                .pullImageCmd(imageName)
                .exec(new ResultCallback.Adapter<PullResponseItem>() {
                    @Override
                    public void onNext(PullResponseItem item) {
                        if (item != null) {
                            System.out.println(item);
                        }
                    }
                })
                .awaitCompletion();
    }

    public static String getBeanClassName(BeanDefinition bean) {
        String fullName = bean.getBeanClassName();
        return fullName.substring(fullName.lastIndexOf('.') + 1);
    }

    public static String getDataBuilderFullClassName(String dataBuilderName) throws Exception {
        var beans = getDataBuilderBeans();

        for (BeanDefinition bean : beans) {
            if (getBeanClassName(bean).equals(dataBuilderName)) {
                return bean.getBeanClassName();
            }
        }

        throw new Exception("Bean named %s that inherits DataBuilder doesn't exits".formatted(dataBuilderName));
    }

    public static Set<BeanDefinition> getDataBuilderBeans() {
        var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(DataBuilder.class));

        return scanner.findCandidateComponents("dbbuilder");
    }

    public static boolean dataBuilderBeanExists(String dataBuilderName) {
        var beans = getDataBuilderBeans();

        for (BeanDefinition bean : beans) {
            if (getBeanClassName(bean).equals(dataBuilderName)) {
                return true;
            }
        }

        return false;
    }

    public static List<Path> getBackupTarPaths() throws IOException {
        try (Stream<Path> entries = Files.list(Path.of("./db-images"))) {
            return entries.filter(p -> p.toString().endsWith(".tar.bz2"))
                    .filter(Files::isRegularFile)
                    .toList();
        }
    }
}
