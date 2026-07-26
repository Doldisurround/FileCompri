import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class GifUI {
    CompriUI compriUI;
    int delay, loop;

    public GifUI(CompriUI compriUI) {
        this.compriUI = compriUI;
        delay = 100;
        loop = 0;

        FlatDarkLaf.setup();
        JFrame frame = new JFrame("GIF Merger");
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(800, 600);
        frame.setVisible(true);

        frame.add(new Button("Convert", e -> convert()));
        frame.add(new Slider("Delay", 1, 100, delay, i -> delay = i).setPost("ms"));
        frame.add(new Slider("Loop", 0, 100, loop, i -> loop = i));
    }

    public void convert() {
        if (compriUI.converting) return;
        new Thread(() -> {
            compriUI.converting = true;
            if (compriUI.out != null) if(compriUI.out.mkdirs()) System.out.println("Created Outputfolder");
            ArrayList<String> cmd = new ArrayList<>();
            Collections.addAll(cmd, "/usr/bin/convert", "-delay", delay + "", "-loop", loop + "");
            for (int i = 0; i < compriUI.files.length; i++) cmd.add(compriUI.files[i].getAbsolutePath());
            cmd.addAll(Arrays.asList("-resize", compriUI.scale + "%", "-quality", compriUI.quality + "", compriUI.out + "/animation.gif"));
            CompriUI.exec(cmd.toArray(String[]::new));
            compriUI.progress.setValue(0);
            compriUI.converting = false;
        }).start();
    }
}
