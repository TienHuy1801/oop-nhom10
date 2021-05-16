package source;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;


public class HelpAndAbout extends JFrame {

	private static final long serialVersionUID = 1L;
	private int bound = 10;

	public HelpAndAbout(int type, String title) {
		add(createContent(type));
		setTitle(title);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
	}

	private JPanel createContent(int type) {
		JTextArea ta = new JTextArea();
		ta.setWrapStyleWord(true);
		ta.setLineWrap(true);
		ta.setBackground(null);
		ta.setEditable(false);
		ta.setColumns(30);
		ta.setRows(15);
		String text;
		if (type == 0) { 
			text = "Đào Nguyễn Tiến Huy -  20194077\n"
					+ "";
		}
		else {
			text = "BTL nhóm 10";
		}
		ta.append(text);
		JScrollPane scrollPanel = new JScrollPane(ta);
		scrollPanel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(scrollPanel, BorderLayout.CENTER);
		panel.setBorder(new EmptyBorder(bound, bound, bound, bound / 2));
		return panel;
	}
}
