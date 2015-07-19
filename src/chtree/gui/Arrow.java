package chtree.gui;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;

import chtree.Constants;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

public class Arrow extends PNode implements Selectable {
	private static final long serialVersionUID = 1L;

	// Arrow direction: child -> parent
	// Containers
	private PPath arrowPath;
	private PText label;
	private int direction; // true: L->R; false: R->L
	private boolean selected = false;

	// Outside dependencies
	private EditArea editArea;
	private Token fromToken;
	private Token toToken;
	private String relate;

	public Arrow(EditArea editArea, Token fromToken, Token toToken) {
		this.editArea = editArea;
		this.fromToken = fromToken;
		this.toToken = toToken;
		this.relate = fromToken.getWord().getRelateString();

		this.addInputEventListener(new ArrowHandler());
	}

	@Override
	public String toString() {
		return "{" + fromToken.getWord() + " , " + relate + " , "
				+ toToken.getWord() + "}";
	}

	public Token getFromToken() {
		return fromToken;
	}

	public Token getToToken() {
		return toToken;
	}

	public String getRelate() {
		return relate;
	}

	public void setFromToken(Token fromToken) {
		this.fromToken = fromToken;
	}

	public void setToToken(Token toToken) {
		this.toToken = toToken;
	}

	public PPath getArrowPath() {
		return arrowPath;
	}

	public PText getLabel() {
		return label;
	}

	private void drawArrow(Point2D fromPoint, Point2D toPoint, Point2D midPoint) {
		arrowPath = new PPath();
		this.addChild(arrowPath);
		
		arrowPath.parentToLocal(fromPoint);
		arrowPath.parentToLocal(toPoint);
		arrowPath.parentToLocal(midPoint);

		arrowPath.setStroke(new BasicStroke(Constants.NORMAL_ARROW_WIDTH));
		arrowPath.setStrokePaint(Constants.NORMAL_ARROW_COLOR);
		arrowPath.moveTo((float) fromPoint.getX(), (float) fromPoint.getY());
		arrowPath.curveTo((float) midPoint.getX(), (float) midPoint.getY(),
				(float) toPoint.getX(), (float) toPoint.getY(),
				(float) toPoint.getX(), (float) toPoint.getY());

		arrowPath.lineTo((float) toPoint.getX(), (float) toPoint.getY()
				- Constants.HEAD_LEN);
		arrowPath.moveTo((float) toPoint.getX(), (float) toPoint.getY());
		arrowPath.lineTo((float) toPoint.getX() - direction
				* Constants.HEAD_LEN, (float) toPoint.getY());
	}

	private void showLabel(Point2D fromPoint, Point2D midPoint) {
		label = new PText(relate);
		label.setFont(Constants.LABEL_FONT);
		label.setTextPaint(Constants.NORMAL_ARROW_COLOR);
		this.addChild(label);

		label.parentToLocal(fromPoint);
		label.parentToLocal(midPoint);
		
		double x_Label = midPoint.getX() - relate.length()
				* Constants.LABEL_FONT_SIZE / 2;
		double y_Label = fromPoint.getY() - (Math.sqrt(2) - 1)
				* (fromPoint.getY() - midPoint.getY()) - 2
				* Constants.LABEL_FONT_SIZE;
		label.translate(x_Label, y_Label);
	}

	public void draw() {
		editArea.getCanvas().getLayer().addChild(this);

		// The parent of fromPoint is the same with "this" Arrow, i.e.
		// editArea.getCanvas().getLayer()
		Point2D fromPoint = fromToken.localToParent(new Point2D.Double(
				fromToken.getX(), fromToken.getY()));
		fromPoint.setLocation(fromPoint.getX() + Constants.TOKEN_WIDTH / 2,
				fromPoint.getY());
		// The parent of toPoint is the same with "this" Arrow, i.e.
		// editArea.getCanvas().getLayer()
		Point2D toPoint = toToken.localToParent(new Point2D.Double(toToken
				.getX(), toToken.getY()));
		toPoint.setLocation(toPoint.getX() + Constants.TOKEN_WIDTH / 2,
				toPoint.getY());

		direction = (toPoint.getX() - fromPoint.getX() > 0) ? 1 : -1;

		double midX = (fromPoint.getX() + toPoint.getX()) / 2;
		double midY = fromPoint.getY()
				- Math.abs(toPoint.getX() - fromPoint.getX()) / 1.5;
		Point2D midPoint = new Point2D.Double(midX, midY);

		// Transform into "this" coordinate
		drawArrow(this.parentToLocal(fromPoint), this.parentToLocal(toPoint),
				this.parentToLocal(midPoint));
		showLabel(this.parentToLocal(fromPoint), this.parentToLocal(midPoint));
	}

	private void changeStyle(int styleCode) {
		switch (styleCode) {
		case Constants.STYLE_NORMAL:
			arrowPath.setStrokePaint(Constants.NORMAL_ARROW_COLOR);
			label.setTextPaint(Constants.NORMAL_ARROW_COLOR);
			break;
		case Constants.STYLE_HIGHLIGHTED:
			arrowPath.setStrokePaint(Constants.HIGHLIGHTED_ARROW_COLOR);
			label.setTextPaint(Constants.HIGHLIGHTED_ARROW_COLOR);
			break;
		case Constants.STYLE_SELECTED:
			arrowPath.setStrokePaint(Constants.SELECTED_ARROW_COLOR);
			label.setTextPaint(Constants.SELECTED_ARROW_COLOR);
			break;
		}

	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void toggleSelected(boolean underMouse) {
		selected = !selected;
		if (selected) {
			Selectable selectedItem = editArea.getSelectedItem();
			if (selectedItem != null) {
				if (selectedItem.getClass() == Arrow.class) {
					((Arrow) selectedItem).toggleSelected(false);
				} else {
					((Token) selectedItem).toggleSelected(false);
				}
			}
			editArea.setSelectedItem(this);
			changeStyle(Constants.STYLE_SELECTED);
		} else {
			editArea.setSelectedItem(null);
			if (underMouse) {
				changeStyle(Constants.STYLE_HIGHLIGHTED);
			} else {
				changeStyle(Constants.STYLE_NORMAL);
			}
		}
	}

	private class ArrowHandler extends PBasicInputEventHandler {
		@Override
		public void mouseClicked(PInputEvent event) {
			super.mouseClicked(event);
			toggleSelected(true);
		}

		@Override
		public void mouseEntered(PInputEvent event) {
			super.mouseEntered(event);
			if (!isSelected())
				changeStyle(Constants.STYLE_HIGHLIGHTED);
		}

		@Override
		public void mouseExited(PInputEvent event) {
			super.mouseExited(event);
			if (!isSelected())
				changeStyle(Constants.STYLE_NORMAL);

		}
	}

}
