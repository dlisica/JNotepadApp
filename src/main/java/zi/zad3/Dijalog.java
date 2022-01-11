package zi.zad3;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.JDialog;
import javax.swing.JLabel;

import hr.fer.oprpp1.hw08.jnotepadpp.MultipleDocumentModel;
import hr.fer.oprpp1.hw08.jnotepadpp.SingleDocumentModel;

public class Dijalog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	private MultipleDocumentModel model;

	public Dijalog(MultipleDocumentModel model) {
		this.model = model;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);
		setSize(200, 200);
		initGUI();
	}
	
	private void initGUI() {
		Container cp = getContentPane();
		cp.removeAll();
		cp.setLayout(new GridLayout(0, 1));

		for(SingleDocumentModel doc : model) {
			String path = "unnamed";
			if(doc.getFilePath() != null) {
				path = doc.getFilePath().toString();
			}
			cp.add(makeLabel(path));
		}
		
	}

	private Component makeLabel(String txt) {
		JLabel lab = new JLabel(txt);
		lab.setOpaque(true);
		lab.setBackground(Color.YELLOW);
		return lab;
	}
	
}
