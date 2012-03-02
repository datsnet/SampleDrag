package com.blahti.example.drag3;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ImageLinearLayout extends LinearLayout implements DragSource, DropTarget {

    public boolean mEmpty = true;
    public int mCellNumber = -1;
    public GridView mGrid;

	public ImageLinearLayout(Context context) {
		super(context);
	}
	public ImageLinearLayout (Context context, AttributeSet attrs) {
		super (context, attrs);
	}

	@Override
	public void onDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
	    // Mark the cell so it is no longer empty.
	    mEmpty = false;
	    int bg = mEmpty ? R.color.cell_empty : R.color.cell_filled;
	    setBackgroundResource (bg);

	    // The view being dragged does not actually change its parent and switch over to the ImageCell.
	    // What we do is copy the drawable from the source view.
	    ImageView sourceView = (ImageView) source;
	    Drawable d = sourceView.getDrawable ();
	    if (d != null) {
//	       this.setImageDrawable (d);

	    }

	     toast ("onDrop cell " + mCellNumber);

	}

	@Override
	public void onDragEnter(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
	    int bg = mEmpty ? R.color.cell_empty_hover : R.color.cell_filled_hover;
	    setBackgroundResource (bg);

	}

	@Override
	public void onDragOver(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {

	}

	@Override
	public void onDragExit(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
	    int bg = mEmpty ? R.color.cell_empty : R.color.cell_filled;
	    setBackgroundResource (bg);

	}

	@Override
	public boolean acceptDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		return mEmpty  && (mCellNumber >= 0);
	}

	@Override
	public Rect estimateDropLocation(DragSource source, int x, int y,
			int xOffset, int yOffset, DragView dragView, Object dragInfo,
			Rect recycle) {
		return null;
	}
	/**
	 * Return true if this cell is empty.
	 * If it is, it means that it will accept dropped views.
	 * It also means that there is nothing to drag.
	 *
	 * @return boolean
	 */

	public boolean isEmpty ()
	{
	    return mEmpty;
	}

	/**
	 * Call this view's onClick listener. Return true if it was called.
	 * Clicks are ignored if the cell is empty.
	 *
	 * @return boolean
	 */

	@Override
	public void setDragController(DragController dragger) {
	}

	@Override
	public void onDropCompleted(View target, boolean success) {
	    // If the drop succeeds, the image has moved elsewhere.
	    // So clear the image cell.
	    if (success) {
	       mEmpty = true;
	       if (mCellNumber >= 0) {
	          int bg = mEmpty ? R.color.cell_empty : R.color.cell_filled;
	          setBackgroundResource (bg);
//	          setImageDrawable (null);
	       } else {
	         // For convenience, we use a free-standing ImageCell to
	         // take the image added when the Add Image button is clicked.
//	         setImageResource (0);
	       }
	    }

	}
	public boolean performClick ()
	{
	    if (!mEmpty) return super.performClick ();
	    return false;
	}

	/**
	 * Call this view's onLongClick listener. Return true if it was called.
	 * Clicks are ignored if the cell is empty.
	 *
	 * @return boolean
	 */

	public boolean performLongClick ()
	{
	    if (!mEmpty) return super.performLongClick ();
	    	return super.performLongClick ();
//	    return false;
	}
	public void toast (String msg)
	{
	    if (!DragActivity.Debugging) return;
	    Toast.makeText (getContext (), msg, Toast.LENGTH_SHORT).show ();
	}
}
