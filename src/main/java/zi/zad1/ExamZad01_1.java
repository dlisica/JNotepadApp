package zi.zad1;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;

import javax.swing.JDialog;
import javax.swing.JLabel;

public class ExamZad01_1 extends JDialog {

	private static final long serialVersionUID = 1L;

	public ExamZad01_1() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);
		setSize(200, 200);
		initGUI();
	}

	private void initGUI() {
		Container cp = getContentPane();
		ExamLayoutManager exlm = new ExamLayoutManager(20);
		cp.setLayout(exlm);
		cp.add(makeLabel("Ovo je tekst za područje 1.", Color.RED), ExamLayoutManager.AREA1);
		cp.add(makeLabel("Područje 2.", Color.GREEN), ExamLayoutManager.AREA2);
		cp.add(makeLabel("Područje 3.", Color.YELLOW), ExamLayoutManager.AREA3);
	}

	private Component makeLabel(String txt, Color col) {
		JLabel lab = new JLabel(txt);
		lab.setOpaque(true);
		lab.setBackground(col);
		return lab;
	}
}
