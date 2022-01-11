package hr.fer.oprpp1.hw08.jnotepadpp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.MatteBorder;

/**
 * Swing komponenta (statusna traka) koja pokazuje položaj kareta u tekstu.
 * Također, pokazuje trenutni datum i vrijeme.
 * 
 * @author David Lisica
 *
 */
public class StatusBar extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Klasa modelira sat koji se treba prikazati na statusnoj traci.
	 * 
	 * @author David Lisica
	 *
	 */
	static class Clock extends JLabel {

		private static final long serialVersionUID = 1L;

		volatile String dateTime;
		volatile boolean stopRequested;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY/MM/DD HH:mm:ss");

		public Clock() {
			updateTime();

			Thread t = new Thread(() -> {
				while (true) {
					try {
						Thread.sleep(500);
					} catch (Exception ex) {
					}
					if (stopRequested)
						break;
					SwingUtilities.invokeLater(() -> {
						updateTime();
					});
				}
			});
			t.setDaemon(true);
			t.start();
		}

		protected void stop() {
			stopRequested = true;
		}

		private void updateTime() {
			dateTime = formatter.format(LocalDateTime.now());
			this.setText(dateTime);
			this.setHorizontalAlignment(SwingConstants.CENTER);
		}

	}

	private static final int WIDTH = 25;

	private Clock clock;

	private int length;
	private int line;
	private int column;
	private int selected;

	private JLabel lengthLabel;
	private JLabel lineLabel;
	private JLabel columnLabel;
	private JLabel selectedLabel;

	public Clock getClock() {
		return this.clock;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
		lengthLabel.setText("length : " + this.length);
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
		lineLabel.setText("Ln : " + this.line);
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
		columnLabel.setText("Col : " + this.column);
	}

	public int getSelected() {
		return selected;
	}

	public void setSelected(int selected) {
		this.selected = selected;
		selectedLabel.setText("Sel : " + this.selected);
	}

	public StatusBar() {
		this(0, 1, 1, 0);
	}

	public StatusBar(int length, int line, int column, int selected) {
		super();
		this.length = length;
		this.line = line;
		this.column = column;
		this.selected = selected;
		initStatusBar();
	}

	/**
	 * Inicijalizacija statusne trake.
	 */
	private void initStatusBar() {

		this.setBorder(new BevelBorder(BevelBorder.LOWERED));
		this.setPreferredSize(new Dimension(this.getWidth(), WIDTH));
		this.setLayout(new GridLayout());

		lengthLabel = new JLabel("length : " + this.length, SwingConstants.CENTER);

		JPanel middlePanel = new JPanel();
		middlePanel.setBorder(new MatteBorder(0, 1, 0, 1, Color.GRAY));
		middlePanel.setLayout(new GridLayout());

		lineLabel = new JLabel("Ln : " + this.line, SwingConstants.CENTER);
		columnLabel = new JLabel("Col : " + this.column, SwingConstants.CENTER);
		selectedLabel = new JLabel("Sel : " + this.selected, SwingConstants.CENTER);

		columnLabel.setBorder(new MatteBorder(0, 1, 0, 1, Color.GRAY));

		middlePanel.add(lineLabel);
		middlePanel.add(columnLabel);
		middlePanel.add(selectedLabel);

		clock = new Clock();

		this.add(lengthLabel);
		this.add(middlePanel);
		this.add(clock);
	}

	/**
	 * Metoda osvježava sadržaj statusne trake.
	 * 
	 * @param length   novi broj znakova
	 * @param line     broj nove linije
	 * @param column   broj novog stupca
	 * @param selected novi broj označenih znakova
	 */
	public void update(int length, int line, int column, int selected) {
		setLength(length);
		setLine(line);
		setColumn(column);
		setSelected(selected);
	}

}
