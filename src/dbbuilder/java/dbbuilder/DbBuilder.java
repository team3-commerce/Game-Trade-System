package dbbuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Frame;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.example.tradedemo", "dbbuilder"})
public class DbBuilder {
    public static final String DB_IMAGE_NAME = "mysql:8.4";
    public static final String DB_PASSWORD = "1234";
    public static final String DB_NAME = "tradedb";
    public static  final int DB_PORT = 42000;

    /**
     * =============================================================
     * IntelliJ 쓰시는 분들은 그냥 왼쪽 초록색 화살표 버튼을 누르시면 됩니다.
     * =============================================================
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            try {
                args = runGui();
            }catch (Exception e) {
            }
        }

        boolean buildDb = false;
        boolean runDb = false;

        String dataBuilderName = null;

        List<String> argsToPass = new ArrayList<>();

        for (String arg : args) {
            boolean parsedArg = false;

            String[] splits = arg.split("=");

            if (splits.length == 2) {
                if (splits[0].equals("--build-db")) {
                    parsedArg = true;
                    buildDb = true;
                    dataBuilderName = splits[1];
                }

                if (splits[0].equals("--run-db")) {
                    parsedArg = true;
                    runDb = true;
                    dataBuilderName = splits[1];
                }
            }

            if (!parsedArg) {
                argsToPass.add(arg);
            }
        }

        if (buildDb && runDb) {
            System.out.println("you can't pass both --build-db and --run-db");
            printHelp();
            System.exit(1);
        }

        if (!buildDb && !runDb) {
            System.out.println("you must pass --build-db or --run-db");
            printHelp();
            System.exit(1);
        }

        if (buildDb) {
            BuildModule buildModule = new BuildModule();
            try {
                buildModule.buildDbTar(argsToPass, dataBuilderName);
            } catch (Exception e) {
                System.out.println("failed to create DB " + e);
                System.out.println(e);
            } finally {
                buildModule.cleanUp();
            }
        }

        if (runDb) {
            LoadModule loadModule = new LoadModule();
            try {
                loadModule.loadDbImage(dataBuilderName);
            } catch (Exception e) {
                System.out.println("failed to run DB " + e);
                loadModule.cleanUpOnError();
            }
        }
    }

    public static void printHelp() {
        System.out.println("usage:");
        System.out.println("  --build-db=<BuilderName>  run DataBuilder to create DB tar");
        System.out.println("  --run-db=<BuilderName>    use tar to run DB");
        System.out.println();
        System.out.println("list of DataBuilder:");

        var beans = BuilderUtil.getDataBuilderBeans();

        if (beans.isEmpty()) {
            System.out.println("  (none)");
        } else {
            for (var bean : beans) {
                System.out.println("  - " + BuilderUtil.getBeanClassName(bean));
            }
        }
    }

    private static String[] runGui() throws IOException {
        var beans = BuilderUtil.getDataBuilderBeans();
        String[] builderOptions = beans.stream()
            .map(BuilderUtil::getBeanClassName)
            .toArray(String[]::new);

        var tarPaths = BuilderUtil.getBackupTarPaths();
        String[] tarOptions = tarPaths.stream()
            .map(p -> p.getFileName().toString().replace(".tar.bz2", ""))
            .toArray(String[]::new);

        JComboBox<String> buildCombo = new JComboBox<>(builderOptions);
        JComboBox<String> tarCombo = new JComboBox<>(tarOptions);

        String[] result = new String[1];

        JDialog dialog = new JDialog((Frame) null, "DB Builder", true);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Build DB"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(buildCombo);
        JButton buildBtn = new JButton("build-db");
        panel.add(buildBtn);

        panel.add(Box.createVerticalStrut(15));

        panel.add(new JLabel("Run DB"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(tarCombo);
        JButton runBtn = new JButton("run-db");
        panel.add(runBtn);

        buildBtn.addActionListener(e -> {
            if (buildCombo.getSelectedItem() != null) {
                result[0] = "--build-db=" + buildCombo.getSelectedItem();
            }
            dialog.dispose();
        });

        runBtn.addActionListener(e -> {
            if (tarCombo.getSelectedItem() != null) {
                result[0] = "--run-db=" + tarCombo.getSelectedItem();
            }
            dialog.dispose();
        });

        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);

        if (result[0] == null) {
            System.exit(0);
        }

        return new String[]{result[0]};
    }
}
