package hr.fer.oprpp1.hw08.jnotepadpp;

import java.awt.GridLayout;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;

/**
 * Implementacija modela više tekstualnih dokumenata prikazanih u tabovima.
 * 
 * @author David Lisica
 *
 */
public class DefaultMultipleDocumentModel extends JTabbedPane implements MultipleDocumentModel {

	private static final long serialVersionUID = 1L;

	private static final String GREEN_ICON_URI = "/greenIcon.png";
	private static final String RED_ICON_URI = "/redIcon.png";

	private List<SingleDocumentModel> documents;
	private SingleDocumentModel previousDoc;
	private SingleDocumentModel currentDoc;
	private List<MultipleDocumentListener> listeners;

	private MultipleDocumentListener mulDocListener;
	private CaretListener caretListener;
	private StatusBar statusBar;

	/**
	 * Defaultni konstruktor koji stvara prazan model.
	 */
	public DefaultMultipleDocumentModel(StatusBar statusBar) {
		this.statusBar = statusBar;

		documents = new LinkedList<>();
		listeners = new LinkedList<>();

		caretListener = new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent event) {
				try {
					calculateStatisticsAndUpdate();
				} catch (BadLocationException e) {
					System.err.println("Greška pri računanju statistike!");
					System.exit(-1);
				}
			}

		};

		mulDocListener = new MultipleDocumentListener() {

			@Override
			public void documentRemoved(SingleDocumentModel model) {
			}

			@Override
			public void documentAdded(SingleDocumentModel model) {
			}

			@Override
			public void currentDocumentChanged(SingleDocumentModel previousModel, SingleDocumentModel currentModel) {
				if (previousModel != null)
					previousModel.getTextComponent().removeCaretListener(caretListener);
				currentModel.getTextComponent().addCaretListener(caretListener);
			}
		};

		this.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent event) {

				int index = DefaultMultipleDocumentModel.this.getSelectedIndex();
				if (index >= 0) {
					setCurrentDocument(DefaultMultipleDocumentModel.this.documents.get(index));
					mulDocListener.currentDocumentChanged(previousDoc, currentDoc);
				}
			}
		});

	}

	@Override
	public Iterator<SingleDocumentModel> iterator() {
		return documents.iterator();
	}

	@Override
	public SingleDocumentModel createNewDocument() {

		SingleDocumentModel document = new DefaultSingleDocumentModel(null, "");
		document.setModified(true);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().add(document.getTextComponent());

		JPanel panel = new JPanel(new GridLayout());
		panel.add(scrollPane);

		addingDocumentToList(document);

		this.addTab("(unnamed)", loadIcon(RED_ICON_URI), panel, "(unnamed)");

		for (MultipleDocumentListener l : listeners)
			l.documentAdded(document);

		setCurrentDocument(document);

		return document;
	}

	@Override
	public SingleDocumentModel getCurrentDocument() {
		return currentDoc;
	}

	@Override
	public SingleDocumentModel loadDocument(Path path) {

		Objects.requireNonNull(path, "Putanja ne smije biti null!");

		SingleDocumentModel alredyOpened = fileAlreayOpened(path);
		if (alredyOpened != null) {
			setCurrentDocument(alredyOpened);
			return alredyOpened;
		}

		byte[] bytes;
		try {
			bytes = Files.readAllBytes(path);
		} catch (Exception ex) {
			return null;
		}
		String text = new String(bytes, StandardCharsets.UTF_8);
		SingleDocumentModel document = new DefaultSingleDocumentModel(path, text);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().add(document.getTextComponent());

		JPanel panel = new JPanel(new GridLayout());
		panel.add(scrollPane);

		addingDocumentToList(document);
		this.addTab(path.getFileName().toString(), loadIcon(GREEN_ICON_URI), panel, path.toString());

		for (MultipleDocumentListener l : listeners)
			l.documentAdded(document);

		setCurrentDocument(document);

		return document;
	}

	@Override
	public void saveDocument(SingleDocumentModel model, Path newPath) {

		if (newPath != null)
			model.setFilePath(newPath);

		byte[] bytes = model.getTextComponent().getText().getBytes(StandardCharsets.UTF_8);
		try {
			Files.write(model.getFilePath(), bytes);
		} catch (IOException e1) {
			return;
		}

		model.setModified(false);
	}

	@Override
	public void closeDocument(SingleDocumentModel model) {

		this.remove(documents.indexOf(model));
		documents.remove(model);

		if (currentDoc.equals(model)) {
			if (iterator().hasNext())
				setCurrentDocument(iterator().next());
			else
				setCurrentDocument(getNumberOfDocuments() > 0 ? documents.get(0) : null);
		}

		for (MultipleDocumentListener l : listeners) {
			l.documentRemoved(model);
		}
	}

	@Override
	public void addMultipleDocumentListener(MultipleDocumentListener l) {
		listeners.add(l);
	}

	@Override
	public void removeMultipleDocumentListener(MultipleDocumentListener l) {
		listeners.remove(l);
	}

	@Override
	public int getNumberOfDocuments() {
		return documents.size();
	}

	@Override
	public SingleDocumentModel getDocument(int index) {
		return documents.get(index);
	}

	/**
	 * Metoda učitava sliku sa zadane putanje <code>path</code> i vraća je kao
	 * objekt tipa <code>ImageIcon</code>.
	 * 
	 * @param path datotečna putanja s koje se učitava slika (ikona)
	 * @return ikona
	 */
	private ImageIcon loadIcon(String path) {

		InputStream is = this.getClass().getResourceAsStream(path);
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

		return new ImageIcon(iconBytes);
	}

	/**
	 * Metoda provjerava je li dokument na zadanoj putanji već u modelu.
	 * 
	 * @param path datotečna putanja za koju se vrši provjera
	 * @return referenca na dokument (ako se već nalazi u modelu), <code>null</code>
	 *         inače
	 */
	private SingleDocumentModel fileAlreayOpened(Path path) {
		for (SingleDocumentModel doc : documents) {
			Path docPath = doc.getFilePath();
			if (docPath == null)
				continue;
			if (docPath.equals(path))
				return doc;
		}
		return null;
	}

	/**
	 * Metoda postavlja trenutni dokument na novu vrijednost. O tome obavještava
	 * zainteresirane slušače.
	 * 
	 * @param newDocument dokument koji se postavlja kao trenutni
	 */
	private void setCurrentDocument(SingleDocumentModel newDocument) {
		previousDoc = currentDoc;
		currentDoc = newDocument;

		this.setSelectedIndex(documents.indexOf(currentDoc));
		updateWindowTitle();
		caretListener.caretUpdate(null);

		for (MultipleDocumentListener l : listeners)
			l.currentDocumentChanged(previousDoc, currentDoc);
	}

	/**
	 * Metoda ažurira naslov prozora pri promjeni trenutnog dokumenta.
	 */
	private void updateWindowTitle() {

		if (currentDoc == null)
			return;

		JFrame window = (JFrame) SwingUtilities.getWindowAncestor(this);
		Path filePath = currentDoc.getFilePath();

		if (filePath == null)
			window.setTitle("(unnamed) - JNotepad++");
		else
			window.setTitle(filePath + " - JNotepad++");

	}

	/**
	 * Metoda dodaje dokument u model. Postavljaju se prikladni slušači.
	 * 
	 * @param document dokument koji se dodaje u model
	 */
	private void addingDocumentToList(SingleDocumentModel document) {

		documents.add(document);
		document.addSingleDocumentListener(new SingleDocumentListener() {

			@Override
			public void documentModifyStatusUpdated(SingleDocumentModel model) {
				if (document.isModified())
					setIconAt(getSelectedIndex(), loadIcon(RED_ICON_URI));
				else
					setIconAt(getSelectedIndex(), loadIcon(GREEN_ICON_URI));
			}

			@Override
			public void documentFilePathUpdated(SingleDocumentModel model) {
				updateWindowTitle();
				setToolTipTextAt(getSelectedIndex(), document.getFilePath().toString());
				setTitleAt(getSelectedIndex(), document.getFilePath().getFileName().toString());
			}
		});
	}

	/**
	 * Metoda računa statistiku prikazanu na statusnoj traci, te ju ažurira.
	 * 
	 * @throws BadLocationException ako je dohvat položaja kareta završio neuspješno
	 */
	private void calculateStatisticsAndUpdate() throws BadLocationException {
		if (currentDoc == null)
			return;

		JTextComponent text = currentDoc.getTextComponent();

		int length = text.getText().length();
		int selected = Math.abs(text.getCaret().getDot() - text.getCaret().getMark());

		int caretPos = text.getCaretPosition();
		int rowNum = (caretPos == 0) ? 1 : 0;
		for (int offset = caretPos; offset > 0;) {
			offset = Utilities.getRowStart(text, offset) - 1;
			rowNum++;
		}

		int colNum = caretPos - Utilities.getRowStart(text, caretPos) + 1;

		statusBar.update(length, rowNum, colNum, selected);

	}

}
