package animation2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SwitchablePanel extends Panel {

	private Label label;
	private Label label2;
	private Panel labels;
	private Container panel;

	public SwitchablePanel(final String title, final Container panel) {
		this.panel = panel;
		labels = new Panel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.label = new Label(title);
		label.setBackground(new Color(100, 140, 200));
		label.setForeground(Color.WHITE);
		label.setFont(new Font("Helvetica", Font.BOLD, 14));
		labels.add(label);

		label2 = new UnderlinedLabel("hide");
		label2.setBackground(new Color(100, 140, 200));
		label2.setForeground(Color.WHITE);
		label2.setFont(new Font("Helvetica", Font.ITALIC, 12));

		labels.add(label2);

		labels.setBackground(new Color(100, 140, 200));


		setLayout(new BorderLayout());
		add(labels, BorderLayout.NORTH);
		add(panel, BorderLayout.CENTER);

		this.label2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println(getParent());
				boolean visible = panel.isVisible();
				panel.setVisible(!visible);
				String sign = visible ? "(hide)" : "(show)";
				label2.setText(sign);
				invalidate();
				revalidate();
			}
		});
	}

	public void switchOn() {
		panel.setVisible(true);
		String sign = panel.isVisible() ? "(hide)" : "(show)";
		label2.setText(sign);
		invalidate();
		revalidate();
	}

	public void switchOff() {
		panel.setVisible(false);
		String sign = panel.isVisible() ? "(hide)" : "(show)";
		label2.setText(sign);
		invalidate();
		revalidate();
	}

	private static class UnderlinedLabel extends Label {
		public UnderlinedLabel() {
			this("");
		}

		public UnderlinedLabel(String text) {
			super(text);
		}

		@Override
		public void paint(Graphics g) {
			Rectangle r;
			super.paint(g);
			r = this.getBounds();
			g.drawLine(
					0,
					r.height - this.getFontMetrics(this.getFont()).getDescent(),
					this.getFontMetrics(this.getFont()).stringWidth(this.getText()),
					r.height - this.getFontMetrics(this.getFont()).getDescent());
		}
	}

	public static void main(String[] args) {
		Frame f = new Frame();
		f.setLayout(new GridLayout(2, 1));
		Panel panel = new Panel();
		panel.add(new Label("lkj lkj"));
		f.add(new SwitchablePanel("bla", panel));
		panel = new Panel();
		panel.add(new Label("lkj lkj"));
		f.add(new SwitchablePanel("bla", panel));
		f.pack();
		f.setVisible(true);
	}
}
