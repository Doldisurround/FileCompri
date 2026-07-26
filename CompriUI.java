import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * @author Doldisurround, BlueFill, Herrmoerlin
 * @version 0.2.1
 */

public class CompriUI {
    File[] files;
    File out;
    int scale, quality;
    boolean converting = false;

    JProgressBar progress;

    public CompriUI() {
        scale = 50;
        quality = 80;

        FlatDarkLaf.setup();
        JFrame frame = new JFrame("Compromiser");
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(800, 600);
        frame.setVisible(true);

        frame.add(new Button("Open Images", e -> openImages()));
        frame.add(new Button("Open Output Folder", e -> openOutput()));
        frame.add(new Button("Convert", e -> convert()));
        frame.add(new Button("Merge to GIF", e -> new GifUI(this)));
        frame.add(new Slider("Scale", 1, 100, scale, i -> scale = i).setPost("%"));
        frame.add(new Slider("Quality", 1, 100, quality, i -> quality = i));

        progress = new JProgressBar(0, 0);
        progress.setValue(0);
        frame.add(progress);
    }

    public void openImages() {
        JFileChooser chooser = new JFileChooser("Open");
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.showOpenDialog(null);
        files = chooser.getSelectedFiles();
        progress.setMaximum(files.length);
        //for (File f : files) System.out.println(f);
    }

    public void openOutput() {
        JFileChooser chooser = new JFileChooser("Open");
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.showOpenDialog(null);
        out = chooser.getSelectedFile();
    }

    public void convert() {
        if (converting) return;
        new Thread(() -> {
            converting = true;
            if (out != null) if (out.mkdirs()) System.out.println("Created Outputfolder");
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                progress.setValue(i);
                convert(f);
            }
            progress.setValue(0);
            converting = false;
        }).start();
    }

    public void convert(File in) {
        //System.out.println("\n\nConverting: " + in.getAbsolutePath() + "\nto: " + getOutName(in));
        exec("/usr/bin/convert", in.getAbsolutePath(), "-resize", scale + "%", "-quality", quality + "", getOutName(in));
    }

    public String getOutName(File in) {
        String out = in.getName();
        out = out.substring(0, out.lastIndexOf(".")) + ".jpg";
        out = this.out.getAbsolutePath() + "/" + out;
        return out;
    }

    public static void exec(String... command) {
        String reset = "\033[0m", err = "\033[31m", cmd = "\033[34m", good = "\033[32m";

        System.out.println("┌──── " + cmd + String.join(" ", command) + reset);
        ArrayList<String> out = new ArrayList<>();
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = null;
        try {
            process = pb.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                out.add(line);
                System.out.println("│ " + line);
            }

            process.waitFor();
        } catch (Exception e) {
            System.out.println("├──── " + e.getCause());
            System.out.println("├──── " + e.getMessage());
            //e.printStackTrace();
        }

        System.out.println("└──── " + (process == null ? (err + "null") : (process.exitValue() == 0 ? (good + process.exitValue()) : (err + process.exitValue()))) + reset);
        out.toArray(String[]::new);
    }
}
