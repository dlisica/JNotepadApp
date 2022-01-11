package hr.fer.oprpp1.hw08.jnotepadpp;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JTextArea;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

/**
 * Implementacija modela tekstualnog dokumenta.
 * 
 * @author David Lisica
 *
 */
public class DefaultSingleDocumentModel implements SingleDocumentModel {

	private boolean modificationStatus;
	private JTextArea editor;
	private Path openedFilePath;

	private List<SingleDocumentListener> listeners;

	/**
	 * Konstruktor stvara model dokumenta. Pretplaćuje zainteresirane slušače na
	 * promjene u dokumentu.
	 * 
	 * @param filePath    putanja dokumenta u datotečnom sustavu
	 * @param textContent sadržaj dokumenta
	 */
	public DefaultSingleDocumentModel(Path filePath, String textContent) {
		listeners = new LinkedList<>();
		editor = new JTextArea(textContent);
		setFilePath(filePath);
		addTextChangeListener();
	}

	@Override
	public JTextArea getTextComponent() {
		return editor;
	}

	@Override
	public Path getFilePath() {
		return openedFilePath;
	}

	@Override
	public void setFilePath(Path path) {
		// Objects.requireNonNull(path, "Putanja ne smije biti null!");
		// kako handlati path od (unnamed) ???
		this.openedFilePath = path;
		for (SingleDocumentListener l : listeners)
			l.documentFilePathUpdated(this);
	}

	@Override
	public boolean isModified() {
		return modificationStatus;
	}

	@Override
	public void setModified(boolean modified) {
		this.modificationStatus = modified;
		if (!modificationStatus) {
			addTextChangeListener();
		}

		for (SingleDocumentListener l : listeners)
			l.documentModifyStatusUpdated(this);
	}

	@Override
	public void addSingleDocumentListener(SingleDocumentListener l) {
		listeners.add(l);
	}

	@Override
	public void removeSingleDocumentListener(SingleDocumentListener l) {
		listeners.remove(l);
	}

	/**
	 * Metoda dodaje slušača na promjene u tekstu i prema tome mijenja status
	 * modificiranosti dokumenta.
	 */
	private void addTextChangeListener() {
		editor.getDocument().addUndoableEditListener(new UndoableEditListener() {

			@Override
			public void undoableEditHappened(UndoableEditEvent event) {
				setModified(true);
				DefaultSingleDocumentModel.this.editor.getDocument().removeUndoableEditListener(this);
			}
		});
	}

}
