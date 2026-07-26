import javax.swing.*;
import java.util.function.Consumer;

public class Slider extends JPanel {
    JSlider slider;
    String post = "";

    public Slider(String pre, int min, int max, int v, Consumer<Integer> onChange) {
        super();
        JLabel label = new JLabel(pre + ": " + v + post);
        add(label);
        slider = new JSlider(min, max, v);
        slider.addChangeListener((changeEvent) -> {
            onChange.accept(slider.getValue());
            label.setText(pre + ": " + slider.getValue() + post);
        });
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(2);
        slider.setPaintTicks(true);
        slider.setPaintTrack(true);
        add(slider);
    }

    public Slider setPost(String post) {
        this.post = post;
        return this;
    }
}
