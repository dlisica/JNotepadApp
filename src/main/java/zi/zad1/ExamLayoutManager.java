package zi.zad1;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

public class ExamLayoutManager implements LayoutManager2 {

	private int postotak;
	private Component[] komponente;

	public static final Integer AREA1 = 1;
	public static final Integer AREA2 = 2;
	public static final Integer AREA3 = 3;
	
	public static final int SLIDER_HEIGHT = 50;

	public void setPostotak(int postotak) {
		if (postotak < 10 || postotak > 90)
			throw new IllegalArgumentException("Postotak mora biti u intervalu [10,90]");

		this.postotak = postotak;
		
	}

	public ExamLayoutManager(int postotak) {
		komponente = new Component[4];
		setPostotak(postotak);
	}

	@Override
	public void addLayoutComponent(String arg0, Component arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void layoutContainer(Container parent) {
		
		int localSliderHeight = 0;
		
		if(komponente[3] != null) {
			localSliderHeight = SLIDER_HEIGHT;
		}

		int componentWidth = parent.getWidth();
		int componentHeight = parent.getHeight();

		int hSkalirani = (int) (componentHeight * ((double) postotak / 100));
		int wSkalirani = (int) (componentWidth * ((double) postotak / 100));

		if (komponente[0] != null) { // area1
			komponente[0].setBounds(0, localSliderHeight, componentWidth, hSkalirani);
		}

		if (komponente[1] != null) { // area2
			komponente[1].setBounds(0, hSkalirani, wSkalirani, componentHeight - hSkalirani);
		}

		if (komponente[2] != null) { // area3
			komponente[2].setBounds(wSkalirani, hSkalirani, componentWidth - wSkalirani, componentHeight - hSkalirani);
		}
		
		if(komponente[3] != null) {
			komponente[3].setBounds(0, 0, componentWidth, SLIDER_HEIGHT);
		}
 
	}

	@Override
	public Dimension minimumLayoutSize(Container arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dimension preferredLayoutSize(Container arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeLayoutComponent(Component arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addLayoutComponent(Component comp, Object constraints) {

		if (constraints.equals(ExamLayoutManager.AREA1)) {
			komponente[0] = comp;

		} else if (constraints.equals(ExamLayoutManager.AREA2)) {
			komponente[1] = comp;

		} else if (constraints.equals(ExamLayoutManager.AREA3)) {
			komponente[2] = comp;

		} else {
			komponente[3] = comp;
		}

	}

	@Override
	public float getLayoutAlignmentX(Container arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getLayoutAlignmentY(Container arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void invalidateLayout(Container container) {
		
	}

	@Override
	public Dimension maximumLayoutSize(Container arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
