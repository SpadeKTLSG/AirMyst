package css.front;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PopupDialog extends JDialog {
    private final JLabel textLabel;
    private final JButton okButton;

    public PopupDialog(JFrame parent) {
        super(parent, "提示", true);

        textLabel = new JLabel();

        textLabel.setFont(new Font("Bold", Font.PLAIN, 20)); //字体样式 : 加粗 , 大小 30
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);        //居中显示

        okButton = new JButton("OK");

        okButton.setFont(new Font("Bold", Font.PLAIN, 30));
        okButton.setHorizontalAlignment(SwingConstants.CENTER);

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setLayout(new BorderLayout());
        add(textLabel, BorderLayout.CENTER);
        add(okButton, BorderLayout.SOUTH);
        setSize(400, 300);

        setLocationRelativeTo(null);        //居中显示
    }

    public void setText(String text) {
        textLabel.setText(text);
    }
}
