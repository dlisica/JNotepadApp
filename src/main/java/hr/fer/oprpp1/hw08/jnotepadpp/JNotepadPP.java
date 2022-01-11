package hr.fer.oprpp1.hw08.jnotepadpp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import zi.zad1.ExamZad01_1;
import zi.zad1.ExamZad01_2;
import zi.zad3.Dijalog;

/**
 * Grafička aplikacija za pisanje teksta. Slična popularnom programu Notepad++.
 * 
 * @author David Lisica
 *
 */
public class JNotepadPP extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final String FRAME_ICON_URI = "/notepadIcon.png";

	private DefaultMultipleDocumentModel model;
	private StatusBar statusBar;

	/**
	 * Konstruktor stvara primjerak razreda <code>JNotepadPP</code>. Postavlja
	 * pripadajuće vrijednosti i parametre.
	 */
	public JNotepadPP() {
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setLocation(50, 50);
		setSize(600, 600);
		setTitle("JNotepad++");
		setFrameIcon();

		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				checkAllDocumentsStatus();
			}

		});

		initGUI();
	}

	private void setFrameIcon() {
		InputStream is = this.getClass().getResourceAsStream(FRAME_ICON_URI);
		if (is == null) {
			System.err.println("Dogodila se greška pri učitavanju ikone!");
			System.exit(-1);
		}
		byte[] iconBytes = {};
		try {
			iconBytes = is.readAllBytes();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		setIconImage(new ImageIcon(iconBytes).getImage());
	}

	/**
	 * Metoda inicijalizira grafičko korisničko sučelje.
	 */
	public void initGUI() {
		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());

		createToolbars();
		createMenus();
		createStatusBar();
		createActions();

		model = new DefaultMultipleDocumentModel(statusBar);
		c.add(model, BorderLayout.CENTER);

	}

	/**
	 * Metoda provjerava status modificiranosti svih dokumenata u modelu prije
	 * zatvaranja prozora. Zatvara dokumente za koje mu korisnik da takvu naredbu.
	 */
	private void checkAllDocumentsStatus() {

		List<SingleDocumentModel> docsToRemove = new LinkedList<>();

		for (SingleDocumentModel doc : model) {
			if (checkOneDocumentStatus(doc)) {
				docsToRemove.add(doc);
			} else {
				break;
			}
		}

		for (SingleDocumentModel doc : docsToRemove) {
			if (model.getNumberOfDocuments() <= 1)
				closeWindow();

			model.closeDocument(doc);
		}

		if (model.getNumberOfDocuments() == 0)
			closeWindow();
	}

	/**
	 * Metoda provjerava status modificiranosti dokumenta <code>doc</code>. Ako je
	 * nespremljen, o tome upozorava korisnika i nudi mu prikladne mogućnosti
	 * odabira (spremi, odbaci, odustani).
	 * 
	 * @param doc model dokumenta kojem se provjerava status
	 * @return <code>true</code> ako je dokument potrebno zatvoriti,
	 *         <code>false</code> inače
	 */
	private boolean checkOneDocumentStatus(SingleDocumentModel doc) {

		if (doc.isModified()) {
			String fileName;
			if (doc.getFilePath() == null)
				fileName = "(unnamed)";
			else
				fileName = doc.getFilePath().getFileName().toString();

			Object[] options = { "Save", "Discard the changes", "Cancel" };
			int reply = JOptionPane.showOptionDialog(JNotepadPP.this,
					"Do you want to save changes in file: " + fileName + ".\nWARNING: Unsaved things will be deleted!",
					"Warning!", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
					options[2]);

			if (reply == 0) {
				saveDocumentAction.actionPerformed(null);
				return true;
			} else if (reply == 1) {
				return true;
			} else
				return false;

		} else {
			return true;
		}
	}

	/**
	 * Metoda stvara izborničku traku u prozoru.
	 */
	private void createMenus() {
		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		fileMenu.add(new JMenuItem(newDocumentAction));
		fileMenu.add(new JMenuItem(openDocumentAction));
		fileMenu.add(new JMenuItem(saveDocumentAction));
		fileMenu.add(new JMenuItem(closeDocumentAction));
		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem(exitAction));

		JMenu editMenu = new JMenu("Edit");
		menuBar.add(editMenu);

		editMenu.add(new JMenuItem(deleteSelectedPartAction));

		JMenu changeCaseMenu = new JMenu("Change case");
		changeCaseMenu.add(new JMenuItem(upperCaseAction));
		changeCaseMenu.add(new JMenuItem(lowerCaseAction));
		changeCaseMenu.add(new JMenuItem(toggleCaseAction));
		menuBar.add(changeCaseMenu);

		JMenu ispitMenu = new JMenu("Ispit");
		ispitMenu.add(new JMenuItem(zadatak1Action));
		ispitMenu.add(new JMenuItem(zadatak1_2Action));
		ispitMenu.add(new JMenuItem(zadatak3Action));
		menuBar.add(ispitMenu);

		this.setJMenuBar(menuBar);
	}

	private Action zadatak1Action = new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					new ExamZad01_1().setVisible(true);
				}
			});
		}
	};

	private Action zadatak1_2Action = new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					new ExamZad01_2().setVisible(true);
				}
			});
		}
	};
	
	private Action zadatak3Action = new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					Dijalog d = new Dijalog(model);
					d.setModal(false);
					d.setVisible(true);
				}
			});
		}
	};

	/**
	 * Metoda stvara alatnu traku u prozoru.
	 */
	private void createToolbars() {
		JToolBar toolBar = new JToolBar("Tools");
		toolBar.setFloatable(true);

		toolBar.add(new JButton(newDocumentAction));
		toolBar.add(new JButton(openDocumentAction));
		toolBar.add(new JButton(saveDocumentAction));
		toolBar.add(new JButton(closeDocumentAction));
		toolBar.addSeparator();
		toolBar.add(new JButton(deleteSelectedPartAction));
		toolBar.add(new JButton(toggleCaseAction));

		this.getContentPane().add(toolBar, BorderLayout.PAGE_START);
	}

	private void createStatusBar() {
		statusBar = new StatusBar();
		this.getContentPane().add(statusBar, BorderLayout.PAGE_END);
	}

	/**
	 * Metoda stvara akcije i pridjeljuje im prikladne atribute.
	 */
	private void createActions() {
		newDocumentAction.putValue(Action.NAME, "New");
		newDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control N"));
		newDocumentAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
		newDocumentAction.putValue(Action.SHORT_DESCRIPTION, "Used to open new file.");

		openDocumentAction.putValue(Action.NAME, "Open");
		openDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control O"));
		openDocumentAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
		openDocumentAction.putValue(Action.SHORT_DESCRIPTION, "Used to open existing file from disk.");

		saveDocumentAction.putValue(Action.NAME, "Save");
		saveDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control S"));
		saveDocumentAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
		saveDocumentAction.putValue(Action.SHORT_DESCRIPTION, "Used to save current file to disk.");

		closeDocumentAction.putValue(Action.NAME, "Close");
		closeDocumentAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control C"));
		closeDocumentAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
		closeDocumentAction.putValue(Action.SHORT_DESCRIPTION, "Used to close current file.");

		deleteSelectedPartAction.putValue(Action.NAME, "Delete selected text");
		deleteSelectedPartAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("F2"));
		deleteSelectedPartAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_D);
		deleteSelectedPartAction.putValue(Action.SHORT_DESCRIPTION, "Used to delete the selected part of text.");

		toggleCaseAction.putValue(Action.NAME, "Toggle case");
		toggleCaseAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control F3"));
		toggleCaseAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_T);
		toggleCaseAction.putValue(Action.SHORT_DESCRIPTION,
				"Used to toggle character case in selected part of text or in entire document.");

		upperCaseAction.putValue(Action.NAME, "Upper case");
		upperCaseAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control F4"));
		upperCaseAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_U);
		upperCaseAction.putValue(Action.SHORT_DESCRIPTION,
				"Used to upper character case in selected part of text or in entire document.");

		lowerCaseAction.putValue(Action.NAME, "Lower case");
		lowerCaseAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control F5"));
		lowerCaseAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_L);
		lowerCaseAction.putValue(Action.SHORT_DESCRIPTION,
				"Used to lower character case in selected part of text or in entire document.");

		exitAction.putValue(Action.NAME, "Exit");
		exitAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control X"));
		exitAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
		exitAction.putValue(Action.SHORT_DESCRIPTION, "Exit application.");

		zadatak1Action.putValue(Action.NAME, "Zadatak 1.1.");
		zadatak1Action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control I"));
		zadatak1Action.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_I);
		zadatak1Action.putValue(Action.SHORT_DESCRIPTION, "Open dialog set on exam");

		zadatak1_2Action.putValue(Action.NAME, "Zadatak 1.2.");
		zadatak1_2Action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control P"));
		zadatak1_2Action.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_P);
		zadatak1_2Action.putValue(Action.SHORT_DESCRIPTION, "Open dialog set on exam");
		
		zadatak3Action.putValue(Action.NAME, "Zadatak 3");
		zadatak3Action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control L"));
		zadatak3Action.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_L);
		zadatak3Action.putValue(Action.SHORT_DESCRIPTION, "Open dialog set on exam");

	}

	/**
	 * Akcija koja otvara novi prazni dokument.
	 */
	private Action newDocumentAction = new AbstractAction() {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			model.createNewDocument();
		}
	};

	/**
	 * Akcija koja otvara postojeći dokument s diska.
	 */
	private Action openDocumentAction = new AbstractAction() {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			Path filePath = openFileChooser();
			if (filePath != null)
				model.loadDocument(filePath);
		}
	};

	/**
	 * Akcija koja sprema trenutni dokument na disk.
	 */
	private Action saveDocumentAction = new AbstractAction() {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {

			SingleDocumentModel document = model.getCurrentDocument();

			if (document != null) {
				if (document.getFilePath() == null) {
					Path path = saveFileChooser();
					if (path != null)
						model.saveDocument(document, path);
				} else
					model.saveDocument(document, null);

				JOptionPane.showMessageDialog(JNotepadPP.this, "File is saved!", "Info",
						JOptionPane.INFORMATION_MESSAGE); // ako se stisne save!!!!!!!!!!!!!
			}
		}
	};

	/**
	 * Akcija koja zatvara trenutni dokument. U slučaju da nije spremljen o tome
	 * obavještava korisnika.
	 */
	private Action closeDocumentAction = new AbstractAction() {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {

			if (model.getNumberOfDocuments() < 1)
				closeWindow();

			if (!checkOneDocumentStatus(model.getCurrentDocument()))
				return;

			if (model.getNumberOfDocuments() <= 1)
				closeWindow();

			model.closeDocument(model.getCurrentDocument());
		}
	};

	/**
	 * Akcija briše označeni dio teksta.
	 */
	private Action deleteSelectedPartAction = new AbstractAction() {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			JTextArea editor = model.getCurrentDocument().getTextComponent();
			Document doc = editor.getDocument();
			int len = Math.abs(editor.getCaret().getDot() - editor.getCaret().getMark());
			if (len == 0)
				return;
			int offset = Math.min(editor.getCaret().getDot(), editor.getCaret().getMark());
			try {
				doc.remove(offset, len);
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}

		}
	};

	/**
	 * Akcija koja mijenja velika u mala slova (i obratno) u označenom dijelu
	 * teksta. Ako ništa nije označeno, akcija se primjenjuje na cijeli trenutni
	 * dokument.
	 */
	private Action toggleCaseAction = new AbstractAction() {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			caseMethod("toggle");
		}
	};

	private Action upperCaseAction = new AbstractAction() {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			caseMethod("upper");
		}
	};

	private Action lowerCaseAction = new AbstractAction() {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			caseMethod("lower");
		}
	};

	private void caseMethod(String textCase) {

		if (model.getCurrentDocument() == null)
			return;

		JTextArea editor = model.getCurrentDocument().getTextComponent();
		Document doc = editor.getDocument();
		int len = Math.abs(editor.getCaret().getDot() - editor.getCaret().getMark());
		int offset = 0;
		if (len != 0) {
			offset = Math.min(editor.getCaret().getDot(), editor.getCaret().getMark());
		} else {
			len = doc.getLength();
		}
		try {
			String text = doc.getText(offset, len);

			if (textCase.equals("toggle"))
				text = changeCase(text);
			else if (textCase.equals("upper"))
				text = text.toUpperCase();
			else
				text = text.toLowerCase();

			doc.remove(offset, len);
			doc.insertString(offset, text, null);
		} catch (BadLocationException ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Akcija koja gasi program.
	 */
	private Action exitAction = new AbstractAction() {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			checkAllDocumentsStatus();
		}
	};

	/**
	 * Metoda otvara datotečni izbornik u kojemu korisnik bira datoteku koju želi
	 * otvoriti.
	 * 
	 * @return datotečna putanja datoteke koju je korisnik izabrao
	 */
	private Path openFileChooser() {

		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Open file");
		if (fc.showOpenDialog(JNotepadPP.this) != JFileChooser.APPROVE_OPTION) {
			return null;
		}
		File fileName = fc.getSelectedFile();
		Path filePath = fileName.toPath();
		if (!Files.isReadable(filePath)) {
			JOptionPane.showMessageDialog(JNotepadPP.this, "Datoteka " + fileName.getAbsolutePath() + " ne postoji!",
					"Pogreška", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		return filePath;
	}

	/**
	 * Metoda otvara datotečni izbornik u kojemu korisnik bira mjesto na disku gdje
	 * želi spremiti datoteku.
	 * 
	 * @return datotečna putanja koju je korisnik izabrao
	 */
	private Path saveFileChooser() {

		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Save file");
		if (fc.showSaveDialog(JNotepadPP.this) != JFileChooser.APPROVE_OPTION) {
			return null;
		}

		File fileName = fc.getSelectedFile();
		Path filePath = fileName.toPath();

		return filePath;
	}

	/**
	 * Metoda mijenja velika u mala slova (i obratno) u predanom stringu
	 * <code>text</code>.
	 * 
	 * @param text string nad kojim se vrše promjene
	 * @return novi, promijenjeni string
	 */
	private String changeCase(String text) {
		char[] znakovi = text.toCharArray();

		for (int i = 0; i < znakovi.length; i++) {
			char c = znakovi[i];
			if (Character.isLowerCase(c)) {
				znakovi[i] = Character.toUpperCase(c);
			} else if (Character.isUpperCase(c)) {
				znakovi[i] = Character.toLowerCase(c);
			}
		}
		return new String(znakovi);
	}

	/**
	 * Metoda gasi program (zatvara prozor). Također poziva metodu za ubijanje
	 * dretve koja dohvaća trenutno vrijeme koje se prikazuje na statusnoj traci.
	 */
	private void closeWindow() {
		dispose();
		statusBar.getClock().stop();
	}

	/**
	 * Glavna metoda koja pokreće program.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new JNotepadPP().setVisible(true);
			}
		});
	}

}
